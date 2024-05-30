package org.disk.service;

import org.disk.dto.yd.UploadFileDto;
import org.disk.entity.FileInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.disk.entity.UserFileList;
import org.disk.result.Result;
import org.disk.vo.yd.UploadFileVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2023-12-10
 */
public interface IFileInfoService extends IService<FileInfo> {

    UploadFileVo uploadFile(UploadFileDto uploadFileDto, String absolutePath,String parentPath,int status);

    FileInfo addFilesToDb(String fileMd5, UploadFileDto uploadFileDto, String bucket, String objectName, UserFileList userFile);

    Result<String> checkFile(String fileMd5,String fileName,String parentPath, int status);

    Result<Boolean> checkChunkFile(String fileMd5, int chunkIndex);

    Result uploadChunk(String fileMd5, int chunk, String absolutePath);

    Result mergeChunks(String fileMd5, int chunkTotal, UploadFileDto uploadFileDto,String parentPath, int status );

    Result download(Integer id, String aimPath);

    FileInfo priview(Integer id);

    public boolean addFilesToMinIO(String absolutePath, String mimeType, String bucket, String objectName);

    Result<String> uploadAvatar(UploadFileDto uploadFileDto, String absolutePath);

    boolean deleteMinioFile(String url);
}
