package org.disk.dto.yls;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ShareDTO {
    @NotNull
    @ApiModelProperty("分享id列表")
    private List<Integer> ids;

    @ApiModelProperty("存在时长")
    private Long timeOut;
}
