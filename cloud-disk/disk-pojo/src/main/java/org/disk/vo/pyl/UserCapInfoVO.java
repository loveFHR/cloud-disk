package org.disk.vo.pyl;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserCapInfoVO {
    @ApiModelProperty("已使用容量")
    private Long used_cap;

    @ApiModelProperty("总的容量")
    private Long all_cap;
}
