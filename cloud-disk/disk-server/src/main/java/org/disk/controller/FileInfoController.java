package org.disk.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.disk.constant.ExceptionConstant;
import org.disk.dto.yd.UploadFileDto;
import org.disk.entity.FileInfo;
import org.disk.result.Result;
import org.disk.service.IFileInfoService;
import org.disk.vo.yd.UploadFileVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@Slf4j
@Api(tags = "文件传输接口")
@CrossOrigin
public class FileInfoController {

    @Autowired
    private IFileInfoService fileInfoService;

    @ApiOperation("上传普通文件")
    @PostMapping(value = "/upload/commonFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public Result<UploadFileVo> upload(@RequestPart("filedata") MultipartFile filedata,
                                       @ApiParam(value = "父目录",required = false) String parentPath,
                                       @ApiParam(value = "个人还是部门",required = true) int status) throws IOException {
        log.info("上传普通文件");

        UploadFileDto uploadFileDto = new UploadFileDto();

        uploadFileDto.setSize(filedata.getSize());

        uploadFileDto.setFileName(filedata.getOriginalFilename());

        File tempFile = File.createTempFile("minio", ".temp");

        filedata.transferTo(tempFile);

        String absolutePath = tempFile.getAbsolutePath();

        UploadFileVo uploadFileVo = fileInfoService.uploadFile(uploadFileDto, absolutePath, parentPath, status);
        return Result.success(uploadFileVo);
    }

    @ApiOperation("文件上传前检查文件,如果存在，直接上传成功")
    @PostMapping("/upload/checkfile")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public Result<String> checkfile(
            @RequestPart("filedata") MultipartFile filedata,
            @ApiParam(value = "父目录",required = false)String parentPath,
            @ApiParam(value = "个人还是部门",required = true)int status) throws Exception {
        File tempFile = File.createTempFile("minio", ".temp");

        filedata.transferTo(tempFile);

        String fileMd5 = null;
        try (FileInputStream fileInputStream = new FileInputStream(tempFile)) {
            fileMd5 = DigestUtils.md5DigestAsHex(fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(ExceptionConstant.UNKNOWN_EXCEPTION);
        }
        return fileInfoService.checkFile(fileMd5,filedata.getOriginalFilename(),parentPath,status);
    }

    @ApiOperation("分块文件上传前的检查")
    @PostMapping("/upload/checkchunk")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public Result<Boolean> checkchunk(@RequestParam("fileMd5") String fileMd5,
                                      @RequestParam("chunk") int chunk) throws Exception {
        log.info("分块文件上传前的检查");
        return fileInfoService.checkChunkFile(fileMd5, chunk);
    }

    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public Result uploadchunk(@RequestParam("file") MultipartFile file,
                              @RequestParam("fileMd5") String fileMd5,
                              @RequestParam("chunk") int chunk) throws Exception {
        log.info("上传分块文件");
        File tempFile = File.createTempFile("minio", ".temp");

        file.transferTo(tempFile);

        String absolutePath = tempFile.getAbsolutePath();

        return fileInfoService.uploadChunk(fileMd5, chunk, absolutePath);
    }

    @ApiOperation(value = "合并文件")
    @PostMapping("/upload/mergechunks")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public Result mergechunks(@RequestParam("fileMd5") String fileMd5,
                              @RequestParam("fileName") String fileName,
                              @RequestParam("chunkTotal") int chunkTotal,
                              @ApiParam(value = "父目录",required = false)String parentPath,
                              @ApiParam(value = "个人还是部门",required = true)int status) throws Exception {
        log.info("合并文件");
        UploadFileDto uploadFileDto = new UploadFileDto();
        uploadFileDto.setFileName(fileName);
        return fileInfoService.mergeChunks(fileMd5, chunkTotal, uploadFileDto, parentPath, status);
    }

    @ApiOperation(value = "下载文件")
    @GetMapping("/download/{id}")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public Result download(@PathVariable("id") Integer id, @ApiParam(name = "下载的目的路径",required = true) String aimPath) {
        return fileInfoService.download(id,aimPath);
    }

    @ApiOperation("预览文件")
    @GetMapping("/preview/{id}")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public Result<String> preview(@PathVariable("id") Integer id){

        FileInfo fileInfo = fileInfoService.priview(id);
        return Result.success(fileInfo.getUrl());
    }

    @PostMapping("/currentUser/uploadAvatar")
    @ApiOperation("上传头像接口")
    @ApiImplicitParam(value = "鉴权token", name = "token", paramType = "header", dataType = "String")
    public Result<String> uploadAvatar(@RequestPart MultipartFile avatar) throws IOException {
        log.info("上传用户头像");

        UploadFileDto uploadFileDto = new UploadFileDto();

        uploadFileDto.setSize(avatar.getSize());

        uploadFileDto.setFileName(avatar.getOriginalFilename());

        File tempFile = File.createTempFile("minio", ".temp");

        avatar.transferTo(tempFile);

        String absolutePath = tempFile.getAbsolutePath();
        return fileInfoService.uploadAvatar(uploadFileDto,absolutePath);
    }
}
