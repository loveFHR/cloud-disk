package org.disk.dto.yyf;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "用户注册时传递的数据类型")
public class UserRegisterDto {


    @ApiModelProperty(value = "用户名，唯一")
    private String userName;

    @ApiModelProperty(value = "密码，存储md5加密后的密码")
    private String password;

    @ApiModelProperty(value = "手机号")
    private Long phone;

    @ApiModelProperty(value = "邮箱")
    private String email;
}
