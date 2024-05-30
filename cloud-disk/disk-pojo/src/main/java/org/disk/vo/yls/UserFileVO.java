package org.disk.vo.yls;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.disk.entity.FileInfo;
import org.disk.entity.UserFileList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
public class UserFileVO {
    public UserFileVO(UserFileList userFileList, FileInfo info, String userName){
        this.id=userFileList.getId();
        this.username=userName;
        this.fileName=userFileList.getFileName();
        this.collection=userFileList.getCollection();
        this.changeDate=userFileList.getChangeDate();
        this.parentPath=userFileList.getParentPath();
        this.isDir=Boolean.TRUE.equals(userFileList.getIsDir());
        if(!Objects.isNull(info)){
            this.size=info.getSize();
            this.type=info.getType();
            this.uploadTime=info.getUploadTime();
        }
    }

    @ApiModelProperty(value = "用户文件id")
    private Integer id;

    @ApiModelProperty(value = "文件名，分享文件时需要")
    private String fileName;


    @ApiModelProperty(value = "用户name,该文件的所属人")
    private String  username;

    @ApiModelProperty(value = "文件类型，zip、jpg、mp4、dir")
    private String type;


    @ApiModelProperty(value = "文件大小，单位字节，该字段一般情况下不会更改")
    private Long size;

    @ApiModelProperty(value = "文件路径，该文件/文件夹的父目录，为空表示为该文件夹是根目录。不能存在父文件目录相同且文件名相同的文件")
    private String parentPath;

    @ApiModelProperty(value = "是否收藏，0未收藏，1已收藏")
    private Integer collection;

    @ApiModelProperty(value = "是否为文件夹，false/null是普通文件，true是文件夹")
    private Boolean isDir;

    @ApiModelProperty(value = "上传时间，存表时传入")
    private LocalDateTime uploadTime;

    @ApiModelProperty(value = "更改时间，用户更改文件名时更改此字段")
    private LocalDateTime changeDate;
}
