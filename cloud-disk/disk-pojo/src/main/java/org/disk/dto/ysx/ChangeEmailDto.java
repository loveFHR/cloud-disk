package org.disk.dto.ysx;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "修改邮箱时接受的原邮箱和新邮箱")
@Data
public class ChangeEmailDto {
    @ApiModelProperty(value = "原邮箱")
    private String email1;

    @ApiModelProperty(value = "新邮箱，原邮箱正确且新邮箱格式正确且未注册则修改")
    private String email2;
}
