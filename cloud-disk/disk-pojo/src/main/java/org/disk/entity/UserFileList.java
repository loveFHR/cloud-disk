package org.disk.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
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
@TableName("user_file_list")
@ApiModel(value="UserFileList对象", description="")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFileList implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "序号，主键，自增")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "文件名，分享文件时需要")
    private String fileName;

    @ApiModelProperty(value = "文件id，映射file_info.file_id，逻辑外键，分享文件时需要此字段。为空表示其为文件夹")
    private Long fileId;

    @ApiModelProperty(value = "更改时间，用户更改文件名时更改此字段")
    private LocalDateTime changeDate;

    @ApiModelProperty(value = "文件状态，1为正常状态，0为已被该用户删除，文件在回收站。该字段不会影响file_info.count，只有回收站过期后，file_info.count才会-1。一定时间后，用户还未恢复文件，则将该文件在本表中删除")
    private Integer status;

    @ApiModelProperty(value = "用户id，映射user_info.id，逻辑外键，该文件的所属人")
    private Long userId;

    @ApiModelProperty(value = "文件路径，该文件/文件夹的父目录，为空表示为该文件夹是根目录。不能存在父文件目录相同且文件名相同的文件")
    private String parentPath;

    @ApiModelProperty(value = "删除时间，为空时代表未删除。文件还原时，需要将该字段置空，更改status字段时，需要将填入改字段")
    private LocalDate deleteTime;

    @ApiModelProperty(value = "是否收藏，0未收藏，1已收藏")
    private Integer collection;

    @ApiModelProperty(value = "是否分享，0不分享，1分享到部门")
    private Integer share;

    @ApiModelProperty(value = "是否为文件夹，0/null是普通文件，1是文件夹")
    private Boolean isDir;


}
