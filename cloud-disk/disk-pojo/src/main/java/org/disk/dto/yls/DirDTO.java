package org.disk.dto.yls;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DirDTO {

    @ApiModelProperty(value = "指定路径")
    private String path;

    @NotBlank
    @ApiModelProperty(value = "目录名")
    private String name;
}
