package org.disk.dto.yyf;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "用户重置密码时接收的参数")
public class ResetPasswordDto {

    @ApiModelProperty("用户邮箱")
    private String email;

    @ApiModelProperty("验证码")
    private String code;

    @ApiModelProperty("密码")
    private String password;
}
