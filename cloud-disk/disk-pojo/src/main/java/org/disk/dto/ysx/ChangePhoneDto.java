package org.disk.dto.ysx;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "修改手机号时接受的原手机号和新手机号")
@Data
public class ChangePhoneDto {
    @ApiModelProperty(value = "原手机号")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long phone1;

    @ApiModelProperty(value = "新手机号，原手机号正确且新手机号格式正确则修改")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long phone2;
}
