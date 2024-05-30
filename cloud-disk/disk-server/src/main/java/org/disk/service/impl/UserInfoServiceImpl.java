package org.disk.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.disk.constant.CommonConstant;
import org.disk.constant.ExceptionConstant;
import org.disk.constant.JwtClaimsConstant;
import org.disk.constant.RedisConstant;
import org.disk.context.BaseContext;
import org.disk.dto.pyl.UpdateUserInfoDto;
import org.disk.dto.ysx.*;
import org.disk.dto.yyf.ResetPasswordDto;
import org.disk.dto.yyf.UserLoginDto;
import org.disk.dto.yyf.UserRegisterDto;
import org.disk.entity.*;
import org.disk.exception.CloudDiskException;
import org.disk.mapper.GroupTableMapper;
import org.disk.mapper.UserDiskCapMapper;
import org.disk.mapper.UserFileListMapper;
import org.disk.mapper.UserInfoMapper;
import org.disk.properties.JwtProperties;
import org.disk.result.PageResult;
import org.disk.result.Result;
import org.disk.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.disk.utils.CheckUtils;
import org.disk.utils.IdGeneratorSnowflake;
import org.disk.utils.JwtUtil;
import org.disk.vo.pyl.UserInfoVO;
import org.disk.vo.ysx.CurrentUserInfoVo;
import org.disk.vo.yyf.UserLoginVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author author
 * @since 2023-12-10
 */
@Service
@Slf4j

public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {
    @Autowired
    private UserDiskCapMapper userDiskCapMapper;
    @Autowired
    private UserFileListMapper userFileListMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private GroupTableMapper groupTableMapper;
    @Autowired
    private IMailSendService mailSendService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private IGroupTableService iGroupTableService;
    @Autowired
    private IFileInfoService fileInfoService;

    /**
     * 添加用户到表中，用户已存在则抛出异常,邮箱不存在也抛出异常
     *
     * @param userRegisterDto
     */
    @Override
    @Transactional
    public void registerUser(UserRegisterDto userRegisterDto) {
        CheckUtils.checkUserRegisterDto(userRegisterDto);
        LambdaQueryWrapper<UserInfo> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(UserInfo::getUserName, userRegisterDto.getUserName())
                .or()
                .eq(UserInfo::getEmail, userRegisterDto.getEmail());

        List<UserInfo> userInfos = userInfoMapper.selectList(userQueryWrapper);
        if (userInfos.size() != 0) {
            throw new CloudDiskException(ExceptionConstant.USER_EXIST_EXCEPTION_MSG);
        }

        UserInfo userInfo = new UserInfo();

        BeanUtils.copyProperties(userRegisterDto, userInfo);

        long id = IdGeneratorSnowflake.snowflakeId();
        userInfo.setId(id);
        //设置用户默认头像
        userInfo.setAvatar(CommonConstant.USER_DEFAULT_AVATAR);
        String salt = RandomUtil.randomString(2);
        userInfo.setSalt(salt);
        // 加密用户密码
        MD5 md5 = new MD5();
        String password = md5.digestHex(userRegisterDto.getPassword() + salt);
        userInfo.setPassword(password);
        userInfo.setCreateTime(LocalDateTime.now());
        sendRegisterEmail(userRegisterDto.getEmail());
        userInfoMapper.insert(userInfo);
        userDiskCapMapper.insert(UserDiskCap.builder().userId(id).build());
        userFileListMapper.insert(UserFileList.builder().fileName(userRegisterDto.getUserName())
                .changeDate(LocalDateTime.now())
                .userId(id)
                .status(1)
                .isDir(true)
                .build()
        );
    }


    @Override
    public UserLoginVo userLogin(UserLoginDto userLoginDto) {
        CheckUtils.checkUserLoginDto(userLoginDto);
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        UserInfo userInfo = userInfoMapper.selectOne(wrapper.eq(UserInfo::getEmail, userLoginDto.getEmail()));
        if (userInfo == null) {
            throw new CloudDiskException(ExceptionConstant.NO_REGISTER_EMAIL);
        }
        MD5 md5 = new MD5();
        String password = md5.digestHex(userLoginDto.getPassword() + userInfo.getSalt());
        if (!password.equals(userInfo.getPassword())) {
            throw new CloudDiskException(ExceptionConstant.USER_PASSWORD_ERROR);
        }
        if (userInfo.getStatus() != 1) {
            throw new CloudDiskException(ExceptionConstant.USER_STATUS_ERROR);

        }
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, userInfo.getId());
        claims.put(JwtClaimsConstant.USER_NAME, userInfo.getUserName());
        String token = JwtUtil.createJWT(
                jwtProperties.getSecretKey(),
                jwtProperties.getTtl(),
                claims);
        return UserLoginVo.builder()
                .id(userInfo.getId())
                .userName(userInfo.getUserName())
                .avatar(userInfo.getAvatar())
                .isAdmin(CommonConstant.ADMIN_USER.equals(userInfo.getUserName()))
                .token(token).build();
    }

    @Override
    public void sendEmail(String email) {
        CheckUtils.checkEmail(email);
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        UserInfo userInfo = userInfoMapper.selectOne(wrapper.eq(UserInfo::getEmail, email));
        if (userInfo == null) {
            throw new CloudDiskException(ExceptionConstant.NO_REGISTER_EMAIL);
        }
        int code = RandomUtil.randomInt(100000, 999999);
        //将生成的code存入redis中
        String redisKey = RedisConstant.SEND_MAIL_PREFIX + email;
        redisTemplate.opsForValue().set(redisKey, String.valueOf(code), 60, TimeUnit.SECONDS);
        String text = "您的验证码为:" + code + ",该验证码60秒内有效";
        mailSendService.sendTextMailMessage(email, "重置您的洋盘密码", text);
        log.info("验证码发送成功，验证码为：" + code);
    }

    /**
     * 发送注册成功邮件
     *
     * @param email
     */
    private void sendRegisterEmail(String email) {
        String text = "您的洋盘账号已注册成功，感谢您的使用！";
        mailSendService.sendTextMailMessage(email, "洋盘注册成功", text);
    }

    @Override
    public void ResetPassword(ResetPasswordDto resetPasswordDto) {
        CheckUtils.checkResetPasswordDto(resetPasswordDto);
        String redisKey = RedisConstant.SEND_MAIL_PREFIX + resetPasswordDto.getEmail();
        String code = redisTemplate.opsForValue().get(redisKey);
        if (code == null) {
            throw new CloudDiskException(ExceptionConstant.CODE_LAPSE);
        }
        if (!resetPasswordDto.getCode().equals(code)) {
            throw new CloudDiskException(ExceptionConstant.CODE_ERROR);
        }
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        UserInfo userInfo = userInfoMapper.selectOne(wrapper.eq(UserInfo::getEmail, resetPasswordDto.getEmail()));
        MD5 md5 = new MD5();
        String password = md5.digestHex(resetPasswordDto.getPassword() + userInfo.getSalt());
        userInfo.setPassword(password);
        userInfoMapper.updateById(userInfo);
        log.info("密码重置成功！");
    }

    /**
     * 管理员更新用户信息
     *
     * @param updateUserInfoDto
     * @return
     */
    @Override
    public int updateUser(UpdateUserInfoDto updateUserInfoDto) {
        String userName = updateUserInfoDto.getUserName();
        UserInfo newUser = new UserInfo();
        //讲全部属性复制到新用户
        BeanUtils.copyProperties(updateUserInfoDto, newUser);
        QueryWrapper<UserInfo> qw2 = new QueryWrapper<>();
        qw2.eq("user_name", userName);
        int i = userInfoMapper.update(newUser, qw2);
        return i;
    }

    @Override
    public List<UserInfoVO> getAllUsers() {
        List<UserInfo> list = this.list();
        List<UserInfoVO> newUserInfo = getNewUserInfo(list);
        return newUserInfo;
    }

    /**
     * 分页获取所有用户
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult getAllUser(int pageNum, int pageSize) {
        QueryWrapper<UserInfo> qw = new QueryWrapper<>();
        List<UserInfo> allUser = this.list();//总的人数
        Page<UserInfo> page = this.page(new Page<>(pageNum, pageSize), qw);
        //用户信息脱敏
        List<UserInfo> records = page.getRecords();
        List<UserInfoVO> newUserInfos = getNewUserInfo(records);
        PageResult pageResult = new PageResult();
        pageResult.setRecords(newUserInfos);
        pageResult.setTotal(allUser.size());
//        PageResult pageResult = new PageResult(newUserInfos.size(),newUserInfos);
        return pageResult;
    }

    /**
     * 根据姓名查用户
     *
     * @param name
     * @return
     */
    @Override
    public List<UserInfoVO> getUserByName(String name) {
        QueryWrapper<UserInfo> qw = new QueryWrapper<>();
        qw.like("user_name", name);
        List<UserInfo> userlist = userInfoMapper.selectList(qw);
        List<UserInfoVO> newUserInfo = getNewUserInfo(userlist);
        return newUserInfo;
    }

    /**
     * 分页根据用户名和部门名查用户
     *
     * @param groupId
     * @param name
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult getUsersPageByName(Long groupId, String name, int pageNum, int pageSize) {
        QueryWrapper<UserInfo> qw = new QueryWrapper<>();
        if(name != null){
            qw.like("user_name", name);
        }
        if(groupId != null){
            qw.eq("group_id",groupId);
        }
        List<UserInfo> allUser = this.list(qw);//总的用户条数
        Page<UserInfo> page = this.page(new Page<>(pageNum, pageSize), qw);
        List<UserInfo> userlist = page.getRecords();
        List<UserInfoVO> newUserInfo = getNewUserInfo(userlist);
        PageResult pageResult = new PageResult(allUser.size(), newUserInfo);
        return pageResult;
    }

    /**
     * 根据部门信息查用户
     *
     * @param groupName
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult getUsersByGroup(String groupName, int pageNum, int pageSize) {
        QueryWrapper<GroupTable> groupqw = new QueryWrapper<>();
        groupqw.eq("group_name", groupName);
        GroupTable one = iGroupTableService.getOne(groupqw);
        Long groupId = one.getId();//获取部门id
        QueryWrapper<UserInfo> qw = new QueryWrapper<>();
        qw.eq("group_id", groupId);
        List<UserInfo> allUser = this.list(qw);//总的用户条数
        Page<UserInfo> userInfoPage = this.page(new Page<>(pageNum, pageSize), qw);
        //获取用户脱敏信息
        List<UserInfoVO> newUserInfo = getNewUserInfo(userInfoPage.getRecords());
        PageResult pageResult = new PageResult(allUser.size(), newUserInfo);
        return pageResult;
    }


    /**
     * 获取用户脱敏信息
     *
     * @param userlist
     * @return
     */
    private List<UserInfoVO> getNewUserInfo(List<UserInfo> userlist) {
        List<UserInfoVO> userInfoVOs = new ArrayList<>();
        for (UserInfo userInfo : userlist) {
            UserInfoVO userInfoVO = new UserInfoVO();
            BeanUtils.copyProperties(userInfo, userInfoVO);//将已有的信息拷贝到VO中
            Long groupId = userInfo.getGroupId();

            if (groupId == null || groupId == 0) { //部门未设置
                userInfoVO.setGroupId(null);
                userInfoVO.setGroupName("暂无");

            } else {
                userInfoVO.setGroupId(groupId);
                GroupTable group = iGroupTableService.getById(groupId);
                userInfoVO.setGroupName(group.getGroupName());//设置部门的名字到视图
            }
            Long userId = userInfo.getId();
            QueryWrapper<UserDiskCap> qw = new QueryWrapper<>();
            qw.eq("user_id", userId);
            //为视图添加用户使用云盘容量
            UserDiskCap userDiskCap = userDiskCapMapper.selectOne(qw);
            if (userDiskCap != null) {
                userInfoVO.setUsed_cap(userDiskCap.getUsedCap());
                userInfoVO.setAll_cap(userDiskCap.getAllCap());
            }
            userInfoVOs.add(userInfoVO);
        }
        return userInfoVOs;
    }

    /**
     * 根据当前用户id查询该用户信息
     *
     * @param
     * @return
     */
    @Override
    public CurrentUserInfoVo queryUserById() throws CloudDiskException {
        long id = BaseContext.getCurrentId();
        UserInfo user = userInfoMapper.selectById(id);
        String groupName;
        if (user.getGroupId() == null) {
            groupName = "暂无";
        } else {
            LambdaQueryWrapper<GroupTable> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(GroupTable::getId, user.getGroupId());
            GroupTable groupTable = groupTableMapper.selectOne(wrapper);
            groupName = groupTable.getGroupName();
        }

        CurrentUserInfoVo currentUser = new CurrentUserInfoVo(user.getId(), user.getUserName(),
                groupName, user.getAvatar(), user.getPhone(), user.getEmail());
        return currentUser;
    }

    /**
     * 确认输入原密码是否一致并且正确
     *
     * @param password
     * @return
     */
    @Override
    public boolean confirmPassword(String password) {
        UserInfo user = userInfoMapper.selectById(BaseContext.getCurrentId());
        String salt = user.getSalt();
        MD5 md5 = new MD5();
        String passwordMd = md5.digestHex(password + salt);
        if (passwordMd.equals(user.getPassword())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 修改当前用户密码
     *
     * @param changePasswordDto
     * @return
     */
    @Override
    public String updatePassword(ChangePasswordDto changePasswordDto) {
        UserInfo user = userInfoMapper.selectById(BaseContext.getCurrentId());
        String salt = user.getSalt();
        MD5 md5 = new MD5();
        String password1 = md5.digestHex(changePasswordDto.getPassword1() + salt);
        String password2 = md5.digestHex(changePasswordDto.getPassword2() + salt);

        if (password1.equals(password2)) {
            if (StringUtils.isEmpty(changePasswordDto.getPassword1()) || changePasswordDto.getPassword1().length() < 6 ||
                    changePasswordDto.getPassword1().length() > 16) {
//                throw new CloudDiskException(ExceptionConstant.PASSWORD_REGEX_ERROR);
                return ExceptionConstant.PASSWORD_REGEX_ERROR;
            } else {
                user.setPassword(password1);
                userInfoMapper.updateById(user);
                return "yes";
            }
        } else {
            return "两次输入密码不一致！";
        }

    }

    /**
     * 修改当前用户手机号
     *
     * @param changePhoneDto
     * @return
     */
    @Override
    public String updatePhone(ChangePhoneDto changePhoneDto) {
        UserInfo user = userInfoMapper.selectById(BaseContext.getCurrentId());
        if (user.getPhone().equals(changePhoneDto.getPhone1())) {
            String regex = "^1[3-9]\\d{9}$";
            if (changePhoneDto.getPhone2() == null || !String.valueOf(changePhoneDto.getPhone2()).matches(regex)) {
//                throw new CloudDiskException(ExceptionConstant.PHONE_ERROR);
                return ExceptionConstant.PHONE_ERROR;
            } else {
                user.setPhone(changePhoneDto.getPhone2());
                userInfoMapper.updateById(user);
                return "yes";
            }
        } else {
            return "原手机号输入错误！";
        }
    }

    /**
     * 修改当前用户邮箱
     *
     * @param changeEmailDto
     * @return
     */
    @Override
    public String updateEmail(ChangeEmailDto changeEmailDto) {
        UserInfo user = userInfoMapper.selectById(BaseContext.getCurrentId());
        List<UserInfo> userInfoList = userInfoMapper.selectList(null);
        boolean flag = true;
        for (UserInfo userInfo : userInfoList) {
            if (userInfo.getEmail().equals(changeEmailDto.getEmail2())) {
                flag = false;
                break;
            }
        }
        if (user.getEmail().equals(changeEmailDto.getEmail1())) {
            String regex = "^[a-z0-9A-Z]+[- | a-z0-9A-Z . _]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-z]{2,}$";
            boolean matches = changeEmailDto.getEmail2().matches(regex);
            if (!matches || changeEmailDto.getEmail2() == null || "".equals(changeEmailDto.getEmail2())) {
//                throw new CloudDiskException(ExceptionConstant.MAIL_ERROR);
                return ExceptionConstant.MAIL_ERROR;
            } else if (!flag) {
                return "该邮箱已被注册，请重新输入！";

            } else {
                String text1 = "您的洋盘邮箱成功更改，感谢您的使用！";
                mailSendService.sendTextMailMessage(changeEmailDto.getEmail1(), "洋盘用户更改", text1);
                String text2 = "您的洋盘邮箱已成功更改为此邮箱，感谢您的使用！";
                mailSendService.sendTextMailMessage(changeEmailDto.getEmail2(), "洋盘用户更改", text2);
                user.setEmail(changeEmailDto.getEmail2());
                userInfoMapper.updateById(user);
                return "yes";
            }
        } else {
            return "原邮箱输入错误！";
        }
    }

    /**
     * 更新用户头像
     *
     * @param url
     * @return
     */
    @Override
    public Result<String> updateAvatar(String url) {
        if (url == null || url.isEmpty()) {
            return Result.error("URL不能为空");
        }
        UserInfo user = userInfoMapper.selectById(BaseContext.getCurrentId());
        String avatarUrl = "http://123.249.45.197:9000" + url;

        if (user.getAvatar().equals("https://bpic.588ku.com/element_origin_min_pic/19/04/09/cb02b26df1cd2ff6dfc97456e2cab54d.jpg")) {
            user.setAvatar(avatarUrl);
            userInfoMapper.updateById(user);
            return Result.success(avatarUrl);
        } else {
            if (fileInfoService.deleteMinioFile(user.getAvatar().substring(7))) {
                user.setAvatar(avatarUrl);
                userInfoMapper.updateById(user);
                return Result.success(avatarUrl);
            }
            user.setAvatar(avatarUrl);
            userInfoMapper.updateById(user);

            return Result.error("更新头像失败");
        }

    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteUserlist(List<Long> list) {
        for (Long id : list) {
            boolean b = this.removeById(id);
            boolean b1 = deleteOther(id);
            if(b==false || b1==false){
                return false;
            }
        }
        return false;

    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteById(Long id) {
        boolean b = this.removeById(id);
        boolean b1 = deleteOther(id);
        if(b1==false || b == false){
           throw new RuntimeException("回滚操作");
        }
        return true;
    }

    private boolean deleteOther(Long id){
        //删除用户磁盘表
        QueryWrapper<UserDiskCap> qw2 = new QueryWrapper<>();
        qw2.eq("user_id",id);
        int i = userDiskCapMapper.delete(qw2);
        if(i<=0){
            return false;
        }
        Boolean b = true;
        //删除文件信息
        QueryWrapper<FileInfo> qw = new QueryWrapper<>();
        qw.eq("user_id",id);
        List<FileInfo> list = fileInfoService.list(qw);
        if(list != null){
            for (FileInfo fileInfo : list) {
                if(fileInfo.getCount()==1){
                    b = fileInfoService.removeById(fileInfo.getId());
                        fileInfoService.deleteMinioFile(fileInfo.getUrl());
                }else{
                    fileInfo.setCount(fileInfo.getCount()-1);
                    b = fileInfoService.updateById(fileInfo);
                }
            }
        }
        //删除失败，返回flase
        if(b==false){
            return false;
        }
        //删除用户文件列表
        QueryWrapper<UserFileList> qw3 = new QueryWrapper<>();
        qw3.eq("user_id",id);
        int delete = userFileListMapper.delete(qw3);
        if(delete<0){
            return false;
        }
        return true;
    }
}
