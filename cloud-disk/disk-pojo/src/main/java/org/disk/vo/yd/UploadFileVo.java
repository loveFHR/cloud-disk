package org.disk.vo.yd;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.disk.entity.FileInfo;


@ApiModel("上传文件返回的对象")
@Data
public class UploadFileVo extends FileInfo {
}
