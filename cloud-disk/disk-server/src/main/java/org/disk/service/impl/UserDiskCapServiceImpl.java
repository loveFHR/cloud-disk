package org.disk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.disk.constant.ExceptionConstant;
import org.disk.constant.StatusConstant;
import org.disk.entity.UserDiskCap;
import org.disk.entity.UserInfo;
import org.disk.exception.CloudDiskException;
import org.disk.mapper.UserDiskCapMapper;
import org.disk.mapper.UserInfoMapper;
import org.disk.service.IUserDiskCapService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.disk.vo.pyl.UserCapInfoVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2023-12-10
 */
@Service
public class UserDiskCapServiceImpl extends ServiceImpl<UserDiskCapMapper, UserDiskCap> implements IUserDiskCapService {
    @Resource
    private UserInfoMapper userInfoMapper;

    /**
     * 饼图云盘容量显示
     * @param id
     * @return
     */
    @Override
    public UserCapInfoVO getUserCapInfo(Long id) {
        UserInfo user = userInfoMapper.selectById(id);
        if(user == null || user.getStatus() == StatusConstant.DISABLE){
            throw new CloudDiskException(ExceptionConstant.NO_AUTH);
        }
        QueryWrapper<UserDiskCap> qw = new QueryWrapper<>();
        qw.eq("user_id",id);
        UserDiskCap userDiskCap = this.getOne(qw);
        if(userDiskCap == null){
            throw new CloudDiskException(ExceptionConstant.UNKNOWN_EXCEPTION);
        }
        UserCapInfoVO userCapInfoVO = new UserCapInfoVO();
        Long allCap = userDiskCap.getAllCap();
        Long usedCap = userDiskCap.getUsedCap();
        userCapInfoVO.setAll_cap(allCap);
        userCapInfoVO.setUsed_cap(usedCap);
        return userCapInfoVO;
    }
}
