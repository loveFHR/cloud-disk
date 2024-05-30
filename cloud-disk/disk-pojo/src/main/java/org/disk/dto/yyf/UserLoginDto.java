package org.disk.dto.yyf;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@ApiModel(description = "用户登录时传递的数据类型")
public class UserLoginDto {

    @ApiModelProperty("用户邮箱")
    private String email;
    @ApiModelProperty("密码")
    private String password;

}
