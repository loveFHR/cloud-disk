package org.disk.service;

import org.disk.entity.UserDiskCap;
import com.baomidou.mybatisplus.extension.service.IService;
import org.disk.vo.pyl.UserCapInfoVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2023-12-10
 */
public interface IUserDiskCapService extends IService<UserDiskCap> {

    UserCapInfoVO getUserCapInfo(Long id);
}
