package org.disk.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author author
 * @since 2023-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("file_info")
@ApiModel(value="FileInfo对象", description="")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "序号，主键，自增")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "文件id，用于在该表中查找文件")
    private Long fileId;

    @ApiModelProperty(value = "文件md5值，用于校验文件是否完整")
    private String fileMd5;

    @ApiModelProperty(value = "文件路径，格式如：/2023/12/9/随机生成的数/video/file_md5.mp4")
    private String url;

    @ApiModelProperty(value = "上传人id")
    private Long userId;

    @ApiModelProperty(value = "上传时间，存表时传入")
    private LocalDateTime uploadTime;

    @ApiModelProperty(value = "文件类型，zip、jpg、mp4、dir")
    private String type;

    @ApiModelProperty(value = "文件计数器，拥有该文件的用户数量，定时删除文件计数器为0的文件")
    private Integer count;

    @ApiModelProperty(value = "文件大小，单位字节，该字段一般情况下不会更改")
    private Long size;


}
