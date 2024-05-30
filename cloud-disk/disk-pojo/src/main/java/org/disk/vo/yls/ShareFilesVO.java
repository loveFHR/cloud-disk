package org.disk.vo.yls;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.disk.entity.UserFileList;
import org.disk.entity.UserInfo;

import java.io.Serializable;
import java.util.List;

@Data
public class ShareFilesVO implements Serializable {
    @ApiModelProperty("分享文件id列表")
    private List<Integer> sharedIds;

    @ApiModelProperty("分享文件列表")
    private List<UserFileVO> sharedFiles;

    @ApiModelProperty("分享用户")
    private UserVO user;
}
