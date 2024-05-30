package org.disk.vo.pyl;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CollectionFileVO {

    @ApiModelProperty(value = "文件列表id")
    private Integer id;

    @ApiModelProperty(value = "文件id")
    private Long fileId;

    @ApiModelProperty(value = "文件名，分享文件时需要")
    private String fileName;

    @ApiModelProperty(value = "更改时间，用户更改文件名时更改此字段")
    private LocalDateTime changeDate;

    @ApiModelProperty(value = "文件路径")
    private String url;

    @ApiModelProperty(value = "文件上传时间")
    private LocalDateTime upload_time;

    @ApiModelProperty(value = "文件类型")
    private String type;

    @ApiModelProperty(value = "父路径")
    private String parent_path;

    @ApiModelProperty(value = "文件大小(MB)")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long size;
}
