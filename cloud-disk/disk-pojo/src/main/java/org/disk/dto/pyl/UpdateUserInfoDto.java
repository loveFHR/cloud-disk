package org.disk.dto.pyl;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateUserInfoDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户id
     */
    private Long id;
    /**
     * 用户姓名
     */
    private String userName;

    @ApiModelProperty(value = "头像url")
    private String avatar;

    @ApiModelProperty(value = "手机号")
    private Long phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "用户状态，1表示启用，0表示禁用")
    private Integer status;

    @ApiModelProperty(value = "分组id，默认无分组")
    private Long groupId;
    /**
     * 部门名字
     */
    private String groupName;
}
