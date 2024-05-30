package org.disk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.disk.aop.annotation.PathEmpty;
import org.disk.constant.ExceptionConstant;
import org.disk.context.BaseContext;
import org.disk.dto.pyl.PageRequest;
import org.disk.dto.yd.UploadFileDto;
import org.disk.entity.FileInfo;

import org.disk.entity.UserDiskCap;
import org.disk.entity.UserFileList;
import org.disk.exception.CloudDiskException;
import org.disk.mapper.FileInfoMapper;
import org.disk.mapper.UserDiskCapMapper;
import org.disk.mapper.UserFileListMapper;
import org.disk.result.PageResult;
import org.disk.result.Result;
import org.disk.service.IFileInfoService;

import org.disk.utils.IdGeneratorSnowflake;
import org.disk.vo.pyl.CollectionFileVO;
import org.disk.vo.yd.UploadFileVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author author
 * @since 2023-12-10
 */
@Service
@Slf4j
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements IFileInfoService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private UserFileListMapper userFileListMapper;

    @Autowired
    private UserDiskCapMapper userDiskCapMapper;

    //普通文件桶
    @Value("${minio.bucket.files}")
    private String bucket_Files;

    @Value("${minio.bucket.bigfiles}")
    private String bucket_BigFiles;

    @Value("${minio.bucket.avatar}")
    private String bucket_Avatar;

    @Override
    @Transactional
    @PathEmpty
    public UploadFileVo uploadFile(UploadFileDto uploadFileDto, String absolutePath, String parentPath, int status) {
        File file = new File(absolutePath);
        if (!file.exists()) {
            throw new CloudDiskException(ExceptionConstant.FILE_NOT_EXIST);
        }

        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<UserDiskCap> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDiskCap::getUserId, userId);
        UserDiskCap userDiskCap = userDiskCapMapper.selectOne(wrapper);
        Long avaCap = userDiskCap.getAllCap() - userDiskCap.getUsedCap();
        Long upMb = byteToMb(uploadFileDto.getSize());

        if (avaCap < upMb) {
            throw new CloudDiskException(ExceptionConstant.LACK_SPACE);
        }

        String filename = uploadFileDto.getFileName();
        String extension = filename.substring(filename.lastIndexOf("."));
        String mimeType = getMimeType(extension);
        String fileMd5 = getFileMd5(file);
        String defaultFolderPath = getDefaultFolderPath();
        String objectName = defaultFolderPath + fileMd5 + extension;

        boolean result = addFilesToMinIO(absolutePath, mimeType, bucket_Files, objectName);
        if (!result) {
            throw new CloudDiskException(ExceptionConstant.UPLOAD_FILE_INTO_MINIO_FAILED);
        }

        uploadFileDto.setSize(file.length());
        uploadFileDto.setType(extension);
        UserFileList userFile = UserFileList.builder().fileName(filename).parentPath(parentPath).share(status).collection(0).status(1).build();
        FileInfo fileInfo = addFilesToDb(fileMd5, uploadFileDto, bucket_Files, objectName, userFile);
        userDiskCap.setUsedCap(upMb + userDiskCap.getUsedCap());
        userDiskCapMapper.updateById(userDiskCap);

        UploadFileVo uploadFileVo = new UploadFileVo();
        BeanUtils.copyProperties(fileInfo, uploadFileVo);
        return uploadFileVo;
    }


    //将文件信息保存到数据库
    @Transactional
    public FileInfo addFilesToDb(String fileMd5, UploadFileDto uploadFileDto, String bucket, String objectName, UserFileList userFile) {
        LambdaQueryWrapper<FileInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileInfo::getFileMd5, fileMd5);
        FileInfo fileInfo = baseMapper.selectOne(wrapper);
        long file_Id = IdGeneratorSnowflake.snowflakeId();

        if (fileInfo == null) {
            fileInfo = new FileInfo();
            BeanUtils.copyProperties(uploadFileDto, fileInfo);
            fileInfo.setUrl("/" + bucket + "/" + objectName);
            fileInfo.setFileId(file_Id);
            fileInfo.setUserId(BaseContext.getCurrentId());
            fileInfo.setUploadTime(LocalDateTime.now());
            fileInfo.setFileMd5(fileMd5);
            fileInfo.setCount(1);
            int insert = baseMapper.insert(fileInfo);
            if (insert < 0) {
                throw new CloudDiskException(ExceptionConstant.SAVE_FILE_INFORMATION_FAILED);
            }
            //保存用户与文件信息
            userFile.setUserId(BaseContext.getCurrentId());
            userFile.setChangeDate(LocalDateTime.now());
            userFile.setFileId(file_Id);
            int insert_UserFile = userFileListMapper.insert(userFile);
            if (insert_UserFile < 0) {
                throw new CloudDiskException(ExceptionConstant.SAVE_USER_FILE_INFORMATION_FAILED);
            }
        }
        return fileInfo;
    }

    //校验文件是否存在
    @Override
    @PathEmpty
    public Result<String> checkFile(String fileMd5, String fileName, String parentPath, int status) {
        LambdaQueryWrapper<FileInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileInfo::getFileMd5, fileMd5);
        wrapper.ge(FileInfo::getCount, 1);
        FileInfo fileInfo = baseMapper.selectOne(wrapper);

        if (fileInfo == null) {
            return Result.error(ExceptionConstant.FILE_NOT_EXIST);

        }
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<UserDiskCap> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(UserDiskCap::getUserId, userId);
        UserDiskCap userDiskCap = userDiskCapMapper.selectOne(userWrapper);
        Long avaCap = userDiskCap.getAllCap() - userDiskCap.getUsedCap();
        Long upMb = byteToMb(fileInfo.getSize());

        if (avaCap < upMb) {
            throw new CloudDiskException(ExceptionConstant.LACK_SPACE);
        }

        String url = fileInfo.getUrl();
        int firstSlashIndex = url.indexOf("/");
        int secondSlashIndex = url.indexOf("/", firstSlashIndex + 1);
        String bucket = url.substring(firstSlashIndex + 1, secondSlashIndex);
        String filePath = url.substring(secondSlashIndex);
        InputStream stream = null;
        try {
            stream = minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(filePath).build());
            if (stream != null) {
                UserFileList userFileList = UserFileList.builder().fileName(fileName)
                        .fileId(fileInfo.getFileId())
                        .changeDate(LocalDateTime.now())
                        .status(status)
                        .userId(userId)
                        .parentPath(parentPath)
                        .collection(0)
                        .share(status)
                        .build();
                int result = userFileListMapper.insert(userFileList);
                if (result < 0) {
                    throw new CloudDiskException(ExceptionConstant.SAVE_USER_FILE_INFORMATION_FAILED);
                }
                fileInfo.setCount(fileInfo.getCount() + 1);
                int res = baseMapper.updateById(fileInfo);
                if(res < 0){
                    return Result.error(ExceptionConstant.UPDATE_FILE_FAILED);
                }
                userDiskCap.setUsedCap(upMb + userDiskCap.getUsedCap());
                int capRes = userDiskCapMapper.updateById(userDiskCap);
                if(capRes < 0){
                    return Result.error(ExceptionConstant.UPDATE_CPA_FAILED);
                }

                return Result.success(ExceptionConstant.UPLOAD_SUCCESS);
            }
        } catch (Exception e) {
            log.error(ExceptionConstant.FILE_NOT_EXIST);
        }
        return Result.error(ExceptionConstant.FILE_NOT_EXIST);
    }

    //分块文件上传前的检查
    @Override
    public Result<Boolean> checkChunkFile(String fileMd5, int chunkIndex) {
        String chunkFileFolderPath = getChunkFilePath(fileMd5);

        GetObjectArgs getObjectArgs = GetObjectArgs
                .builder()
                .bucket(bucket_BigFiles)
                .object(chunkFileFolderPath + chunkIndex)
                .build();


        try {
            FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
            if (inputStream != null) {
                return Result.success(true);
            }
        } catch (Exception e) {
        }
        return Result.success(false);
    }

    //上传分块文件
    @Override
    public Result uploadChunk(String fileMd5, int chunk, String absolutePath) {
        String chunkFilePath = getChunkFilePath(fileMd5) + Integer.toString(chunk);

        String mimeType = getMimeType(null);

        boolean result = addFilesToMinIO(absolutePath, mimeType, bucket_BigFiles, chunkFilePath);

        if (!result) {
            return Result.error(ExceptionConstant.UPLOAD_CHUNK_FILE_FAILED);
        }
        return Result.success(true);
    }

    //合并文件
    @Override
    @Transactional
    @PathEmpty
    public Result mergeChunks(String fileMd5, int chunkTotal, UploadFileDto uploadFileDto, String parentPath, int status) {
        String chunkFilePath = getChunkFilePath(fileMd5);

        List<ComposeSource> sourceObjectList = Stream.iterate(0, i -> ++i)
                .limit(chunkTotal)
                .map(i -> ComposeSource.builder()
                        .bucket(bucket_BigFiles)
                        .object(chunkFilePath.concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());

        String fileName = uploadFileDto.getFileName();

        String extension = fileName.substring(fileName.lastIndexOf("."));

        String mergeFilePath = getMergeFilePath(fileMd5, extension);

        try {
            //合并文件
            ObjectWriteResponse response = minioClient.composeObject(
                    ComposeObjectArgs.builder()
                            .bucket(bucket_BigFiles)
                            .object(mergeFilePath)
                            .sources(sourceObjectList)
                            .build());
            log.debug("合并文件成功:{}", mergeFilePath);
        } catch (Exception e) {
            e.printStackTrace();
            log.debug("合并文件失败,fileMd5:{},异常:{}", fileMd5, e.getMessage(), e);
            throw new CloudDiskException(ExceptionConstant.MERGE_FILE_FAILED);
        }


        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<UserDiskCap> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDiskCap::getUserId, userId);
        UserDiskCap userDiskCap = userDiskCapMapper.selectOne(wrapper);

        StatObjectArgs statObjectArgs = StatObjectArgs.builder().bucket(bucket_BigFiles).object(mergeFilePath).build();
        try {
            StatObjectResponse statObjectResponse = minioClient.statObject(statObjectArgs);
            //文件大小
            uploadFileDto.setSize(statObjectResponse.size());

            Long avaCap = userDiskCap.getAllCap() - userDiskCap.getUsedCap();
            Long upMb = byteToMb(uploadFileDto.getSize());

            if (avaCap < upMb) {
                throw new CloudDiskException(ExceptionConstant.LACK_SPACE);
            }

            userDiskCap.setUsedCap(upMb + userDiskCap.getUsedCap());
            userDiskCapMapper.updateById(userDiskCap);

        } catch (Exception e) {
            e.printStackTrace();
        }

        uploadFileDto.setType(extension);
        UserFileList userFile = UserFileList.builder().fileName(fileName).parentPath(parentPath).share(status).collection(0).status(1).build();
        FileInfo fileInfo = addFilesToDb(fileMd5, uploadFileDto, bucket_BigFiles, mergeFilePath, userFile);

        //=====清除分块文件=====
        clearChunkFiles(chunkFilePath, chunkTotal);
        return Result.success(true);
    }

    //下载
    @Override
    public Result download(Integer id, String aimPath) {


        InputStream inputStream = null;
        try {
            if (id == null) {
                throw new CloudDiskException(ExceptionConstant.FILE_NOT_EXIST);
            }

            FileInfo fileInfo = baseMapper.selectById(id);

            LambdaQueryWrapper<UserFileList> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserFileList::getFileId, fileInfo.getFileId());
            UserFileList userFileList = userFileListMapper.selectOne(wrapper);
            String fileName = userFileList.getFileName();

            String url = fileInfo.getUrl();
            int firstSlashIndex = url.indexOf("/");
            int secondSlashIndex = url.indexOf("/", firstSlashIndex + 1);
            String bucket = url.substring(firstSlashIndex + 1, secondSlashIndex);
            String filePath = url.substring(secondSlashIndex);


            inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(filePath)
                            .build()
            );
            FileOutputStream outputStream = new FileOutputStream(aimPath + fileName);
            // 用于拷贝流
            IOUtils.copy(inputStream, outputStream);
            return Result.success(ExceptionConstant.DOWNLOAD_FILE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.error(ExceptionConstant.DOWNLOAD_FILE_FAILED);
    }

    //预览
    @Override
    public FileInfo priview(Integer id) {
        if (id == null) {
            throw new CloudDiskException(ExceptionConstant.FILE_NOT_EXIST);
        }
        UserFileList userFileList = userFileListMapper.selectById(id);
        if(userFileList == null){
            throw new CloudDiskException(ExceptionConstant.FILE_NOT_EXIST);
        }
        LambdaQueryWrapper<FileInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileInfo::getFileId,userFileList.getFileId());
        wrapper.ge(FileInfo::getCount,1);
        FileInfo fileInfo = baseMapper.selectOne(wrapper);
        return fileInfo;
    }


    //清除分块文件
    private void clearChunkFiles(String chunkFilePath, int chunkTotal) {
        try {
            List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i)
                    .limit(chunkTotal)
                    .map(i -> new DeleteObject(chunkFilePath.concat(Integer.toString(i))))
                    .collect(Collectors.toList());

            RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket("video").objects(deleteObjects).build();
            Iterable<io.minio.Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
            results.forEach(r -> {
                DeleteError deleteError = null;
                try {
                    deleteError = r.get();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("清楚分块文件失败,objectname:{}", deleteError.objectName(), e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            log.error("清楚分块文件失败,chunkFileFolderPath:{}", chunkFilePath, e);
        }
    }

    //得到合并后的文件的地址
    private String getMergeFilePath(String fileMd5, String extension) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + extension;
    }

    //得到分块文件的目录
    private String getChunkFilePath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }

    //将文件上传到minio
    @Override
    public boolean addFilesToMinIO(String absolutePath, String mimeType, String bucket, String objectName) {
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .filename(absolutePath)
                    .object(objectName)
                    .contentType(mimeType)
                    .build();
            minioClient.uploadObject(uploadObjectArgs);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件到minio失败");
            throw new CloudDiskException(ExceptionConstant.UPLOAD_FILE_INTO_MINIO_FAILED);
        }
    }

    //获取文件默认存储目录路径 年/月/日
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String folder = sdf.format(new Date()).replace("-", "/") + "/";
        return folder;
    }

    //获取文件的md5值
    private String getFileMd5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            String fileMd5 = DigestUtils.md5DigestAsHex(fileInputStream);
            return fileMd5;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取文件的mimeType
    private String getMimeType(String extension) {
        if (extension == null) {
            extension = "";
        }
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    //字节转mb
    private Long byteToMb(Long size) {
        return (long) Math.ceil((double) size / (1024 * 1024) * 100);
    }

    /**
     * 上传头像
     * @param uploadFileDto
     * @param absolutePath
     * @return
     */
    @Override
    public Result<String> uploadAvatar(UploadFileDto uploadFileDto, String absolutePath) {

        File file = new File(absolutePath);
        if (!file.exists()){
            throw new CloudDiskException(ExceptionConstant.FILE_NOT_EXIST);
        }

        String filename = uploadFileDto.getFileName();
        String extension = filename.substring(filename.lastIndexOf("."));
        if (!extension.equals(".jpg")&&!extension.equals(".jepg")&&!extension.equals(".png")){
            throw new CloudDiskException(ExceptionConstant.UPLOAD_AVATAR_FAILED);
        }
        String mimeType = getMimeType(extension);
        String fileMd5 = getFileMd5(file);
        String defaultFolderPath = getDefaultFolderPath();
        String objectName = defaultFolderPath + fileMd5 + extension;

        boolean result = addFilesToMinIO(absolutePath, mimeType, bucket_Avatar, objectName);
        if (!result){
            throw new CloudDiskException(ExceptionConstant.UPLOAD_FILE_INTO_MINIO_FAILED);
        }

        return Result.success("/" + bucket_Avatar + "/" + objectName);
    }

    @Override
    public boolean deleteMinioFile(String url){
        int firstSlashIndex = url.indexOf("/");
        int secondSlashIndex = url.indexOf("/", firstSlashIndex + 1);
        String bucket = url.substring(firstSlashIndex + 1, secondSlashIndex);
        String filePath = url.substring(secondSlashIndex);
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(bucket).object(filePath).build();
        try {
            minioClient.removeObject(removeObjectArgs);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
