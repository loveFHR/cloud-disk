package org.disk.dto.ysx;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "修改密码时接受的两次新密码")
@Data
public class ChangePasswordDto {
    @ApiModelProperty(value = "新密码")
    private String password1;

    @ApiModelProperty(value = "新密码，如果 password1=password2 且都正确则修改")
    private String password2;
}
