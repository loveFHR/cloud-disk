package org.disk.service;

import org.disk.dto.pyl.UpdateUserInfoDto;
import org.disk.dto.ysx.*;
import org.disk.dto.yyf.ResetPasswordDto;
import org.disk.dto.yyf.UserLoginDto;
import org.disk.dto.yyf.UserRegisterDto;
import org.disk.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.disk.result.PageResult;
import org.disk.result.Result;
import org.disk.vo.pyl.UserInfoVO;
import org.disk.vo.ysx.CurrentUserInfoVo;
import org.disk.vo.yyf.UserLoginVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2023-12-10
 */
public interface IUserInfoService extends IService<UserInfo> {

    /**
     * 用户注册
     * @param userRegisterDto
     */
    void registerUser(UserRegisterDto userRegisterDto);

    /**
     * 用户登录
     * @param userLoginDto
     * @return
     */
    UserLoginVo userLogin(UserLoginDto userLoginDto);

    /**
     * 发送邮件
     * @param email
     */
    void sendEmail(String email);


    /**
     * 重置密码
     * @param resetPasswordDto
     */
    void ResetPassword(ResetPasswordDto resetPasswordDto);

    int updateUser(UpdateUserInfoDto userInfo);

    List<UserInfoVO> getAllUsers();
    /**
     * 分页获取所有用户
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageResult getAllUser(int pageNum, int pageSize);
    /**
     * 根据姓名查用户
     * @param name
     * @return
     */
    List<UserInfoVO> getUserByName(String name);

    /**
     * 分页查用户
     *
     * @param groupId
     * @param name
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageResult getUsersPageByName(Long groupId, String name, int pageNum, int pageSize);

    PageResult getUsersByGroup(String groupName, int pageNum, int pageSize);

    /**
     * 根据id查询当前用户信息
     * @param "currentId"
     * @return
     */
    CurrentUserInfoVo queryUserById();

    /**
     * 确认输入密码是否正确
     * @param password
     * @return
     */
    boolean confirmPassword(String password);

    /**
     * 修改当前用户密码
     * @param changePasswordDto
     * @return
     */
    String updatePassword(ChangePasswordDto changePasswordDto);

    /**
     * 修改当前用户手机号
     * @param changePhoneDto
     * @return
     */
    String updatePhone(ChangePhoneDto changePhoneDto);

    /**
     * 修改当前用户邮箱
     * @param changeEmailDto
     * @return
     */
    String updateEmail(ChangeEmailDto changeEmailDto);

    /**
     * 用户更新头像
     * @param url
     * @return
     */
    Result<String> updateAvatar(String url);

    boolean deleteUserlist(List<Long> list);

    boolean deleteById(Long id);
}
