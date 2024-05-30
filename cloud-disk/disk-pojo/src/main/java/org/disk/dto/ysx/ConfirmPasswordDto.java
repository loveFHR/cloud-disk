package org.disk.dto.ysx;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "修改个人信息时需确认两次原密码")
@Data
public class ConfirmPasswordDto {

    @ApiModelProperty(value = "原密码")
    private String password1;

    @ApiModelProperty(value = "原密码，如果 password1=password2 则同意更改")
    private String password2;
}
