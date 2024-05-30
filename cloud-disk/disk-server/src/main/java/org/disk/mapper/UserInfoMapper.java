package org.disk.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.disk.entity.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author author
 * @since 2023-12-10
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

}
