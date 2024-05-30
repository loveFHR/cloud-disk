package org.disk.vo.yyf;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "用户登录时返回的数据类型")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginVo {

    @ApiModelProperty(value = "用户id，主键")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty("用户名")
    private String userName;


    @ApiModelProperty("jwt令牌")
    private String token;

    @ApiModelProperty("用户头像url")
    private String avatar;

    @ApiModelProperty("是否是管理员,为true则跳转到管理页面")
    private boolean isAdmin;
}
