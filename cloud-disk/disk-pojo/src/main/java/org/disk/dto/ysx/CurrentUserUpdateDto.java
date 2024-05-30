package org.disk.dto.ysx;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "个人信息修改时接受的数据")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentUserUpdateDto {

    @ApiModelProperty(value = "用户手机号")
    private Long phone;

    @ApiModelProperty(value = "用户邮箱")
    private String email;

    @ApiModelProperty(value = "新密码")
    private String password;

}
