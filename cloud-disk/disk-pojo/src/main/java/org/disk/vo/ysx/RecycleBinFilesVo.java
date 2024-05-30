package org.disk.vo.ysx;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@ApiModel(description = "回收站文件信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecycleBinFilesVo {

    @ApiModelProperty(value = "文件id")
    private Integer id;

    @ApiModelProperty(value = "文件名")
    private String fileName;

    @ApiModelProperty(value = "是否为文件夹")
    private boolean isDir;

    @ApiModelProperty(value = "删除时间")
    private LocalDate deleteTime;

}
