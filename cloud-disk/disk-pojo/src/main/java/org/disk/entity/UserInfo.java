package org.disk.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author author
 * @since 2023-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_info")
@ApiModel(value="UserInfo对象", description="")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id，主键")
    @TableId(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户名，唯一")
    private String userName;

    @ApiModelProperty(value = "密码，存储md5加密后的密码")
    private String password;

    @ApiModelProperty(value = "头像url")
    private String avatar;

    @ApiModelProperty(value = "手机号")
    private Long phone;

    @ApiModelProperty(value = "盐，用于密码加密")
    private String salt;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "创建时间，存表时传入")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间，为null表示从未更新")
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "用户状态，1表示启用，0表示禁用")
    private Integer status;

    @ApiModelProperty(value = "分组id，默认无分组")
    private Long groupId;


}
