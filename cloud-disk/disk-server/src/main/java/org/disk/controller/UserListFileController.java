package org.disk.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.disk.constant.ExceptionConstant;
import org.disk.context.BaseContext;
import org.disk.dto.pyl.PageRequest;
import org.disk.dto.yls.DirDTO;
import org.disk.dto.yls.ShareDTO;
import org.disk.entity.UserFileList;
import org.disk.entity.UserInfo;
import org.disk.result.PageResult;
import org.disk.result.Result;
import org.disk.service.IUserFileListService;
import org.disk.vo.pyl.CollectionFileVO;
import org.disk.vo.yls.ShareFilesVO;
import org.disk.vo.yls.UserFileVO;
import org.disk.vo.ysx.RecycleBinFilesVo;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/userFiles")
@Api(tags = "用户文件接口")
@CrossOrigin
public class UserListFileController {

    @Resource
    private IUserFileListService userFileListService;


    @PostMapping("/createDir")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    @ApiOperation("创建文件夹")
    public Result createDir(@RequestBody DirDTO dto){
        UserInfo user = BaseContext.getCurrentUser();
        UserFileList directory = userFileListService.createDirectory(dto.getPath(), dto.getName(), user,true);
        Integer id = directory.getId();
        return id==null?Result.error("文件夹创建失败"):Result.success();
    }

    @GetMapping("/files")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    @ApiOperation("查看指定路径下的文件列表")
    public Result<PageResult<UserFileVO>> getFiles(@ApiParam(name = "path",value = "父路径")String path
            , @ApiParam(name = "curr",value = "当前页",required = true)Integer curr
            , @ApiParam(name = "size",value = "页容量",required = true)Integer size){
        if(curr == null||size == null){
            return Result.error(new PageResult<>(),"curr和size不得为空");
        }

        UserInfo user = BaseContext.getCurrentUser();
        PageResult<UserFileVO> userFileList = userFileListService.getUserFileList(path, user, curr, size);

        return Result.success(userFileList);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "用户文件id列表删除")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public Result deleteByIds( @RequestBody List<Integer> ids){
        if(ids==null||ids.size()==0){
            return Result.error("id列表不得为空");
        }
        UserInfo currentUser = BaseContext.getCurrentUser();
        return userFileListService.deleteFilesByIds(ids,currentUser);
    }
    //todo:move files
    @PostMapping("/move")
    @ApiOperation(value = "移动文件或文件夹")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public Result moveFiles(@ApiParam(name = "path",value = "目标路径") String  path
                                    , @ApiParam(name = "id",value = "userFileId",required = true) Integer id){
        if(Objects.isNull(id)){
            return Result.error("id不得为空");
        }
        UserInfo currentUser = BaseContext.getCurrentUser();

        return userFileListService.moveFiles(path,id,currentUser);
    }

    @PostMapping("/share")
    @ApiOperation(value = "用户文件id列表分享",notes = "通过msg返回token，可用token查询分享列表")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public Result<String> shareByIdList(@RequestBody ShareDTO share){
        if(share.getIds().size()==0){
            return Result.error("id列表不得为空");
        }
        UserInfo currentUser = BaseContext.getCurrentUser();
        String token = userFileListService.shareFilesByIds(share.getIds(), currentUser, share.getTimeOut());
        return Result.success(token);
    }

    //todo:get shared files list by token
    @GetMapping("/share")
    @ApiOperation(value = "获取分享",notes = "可用token查询分享列表")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public Result<ShareFilesVO> getShareFiles(@ApiParam(name = "shareToken",value = "shareToken",required = true) String  shareToken){
        if(!StringUtils.hasText(shareToken)){
            return Result.error("shareToken");
        }
        ShareFilesVO shareFiles = userFileListService.getShareFiles(shareToken);
        return Result.success(shareFiles);
    }

    @PostMapping("/rename")
    @ApiOperation(value = "重命名文件或文件夹")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public Result<String > renameFiles(@ApiParam(name = "filename",value = "重命名后的名字",required = true) String  filename
            ,@ApiParam(name = "id",value = "userFile Id",required = true) Integer id){
        if(!StringUtils.hasText(filename)|| Objects.isNull(id)){
            return Result.error("参数错误");
        }
        return userFileListService.updateFileName(filename,id);
    }

    @PostMapping("/share_status")
    @ApiOperation(value = "分享或取消分享到部门")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public Result<String > shareFiles(@ApiParam(name = "isSharing",value = "是否分享到部门",required = true) Boolean  isSharing
            ,@ApiParam(name = "id",value = "userFile Id",required = true) Integer id){
        if(Objects.isNull(isSharing)|| Objects.isNull(id)){
            return Result.error("参数错误");
        }
        return userFileListService.changeSharedStatus(isSharing,id);
    }


    @PostMapping("/collect")
    @ApiOperation(value = "用户文件id列表收藏")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public Result collectByIdList( @RequestBody List<Integer> ids){
        if(ids==null||ids.size()==0){
            return Result.error("id列表不得为空");
        }
        UserInfo currentUser = BaseContext.getCurrentUser();
        List<Integer> fail = userFileListService.collectFilesByIds(ids, currentUser);
        return fail.size()!=0 ? Result.error(fail,"部分收藏未成功"):Result.success();
    }

    @GetMapping("/types")
    @ApiOperation(value = "根据type查询文件")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public PageResult<UserFileVO> getFilesByType(@ApiParam(name = "type",value = "类型后缀，以 . 开始")String type
            , @ApiParam(name = "curr",value = "当前页",required = true)Integer curr
            , @ApiParam(name = "size",value = "页容量",required = true)Integer size){
        if(!StringUtils.hasText(type)||Objects.isNull(curr)||Objects.isNull(size)){
            return new PageResult<>();
        }
        if(!type.startsWith(".")||type.substring(1).length()==0){
            return new PageResult<>();
        }

        UserInfo currentUser = BaseContext.getCurrentUser();

        return userFileListService.getFilesByType(type,currentUser ,curr, size);
    }


    /**
     * 分页查询收藏文件
     * @param pageRequest
     * @return
     */
    @GetMapping("/page/collectFile")
    @ApiOperation(value = "分页查询收藏文件")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public Result<PageResult<CollectionFileVO>> collectFile(PageRequest pageRequest){
        UserInfo currentUser = BaseContext.getCurrentUser();
        if(currentUser == null){
            return Result.error("请登录");
        }
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        PageResult<CollectionFileVO> pageResult= userFileListService.collectFile(currentUser.getId(),pageNum,pageSize);
        return Result.success(pageResult);
    }

    /**
     * 取消收藏
     * @param id
     * @return
     */
    @GetMapping("/cancelCollection")
    @ApiOperation(value = "取消收藏")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public Result<Boolean> cancelCollection(@RequestParam(value = "id") Long id){
        if(id<0){
            return Result.error(ExceptionConstant.PARAM_ERROR);
        }
        QueryWrapper<UserFileList> qw = new QueryWrapper<>();
        qw.eq("id",id);
        UserFileList file = userFileListService.getOne(qw);
        if (file == null){
            Result.error(ExceptionConstant.FILE_NOT_EXIST);
        }
        file.setCollection(0);
        boolean b = userFileListService.updateById(file);
        if(b == false){
            return Result.error(500,"取消失败");
        }
        return Result.success(true);
    }

    @GetMapping("/recycleBin/page")
    @ApiOperation(value = "分页展示所有回收站文件")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public Result<PageResult<RecycleBinFilesVo>> queryAllDeletedFiles(@RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "10") int size) {

        PageResult<RecycleBinFilesVo> pageResult = userFileListService.listRecycleBinFiles(page, size);
        return Result.success(pageResult);
    }

    /**
     * 对用户选择的回收站文件进行恢复
     * @param fileIds
     * @return
     */
    @GetMapping("/recycleBin/restore")
    @ApiOperation(value = "恢复回收站文件")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public Result restoreFile(@RequestBody List<Integer> fileIds) {

        userFileListService.restoreFile(fileIds);
        return Result.success();

    }

    /**
     * 用户选择的回收站文件进行删除
     * @param fileIds
     * @return
     */
    @DeleteMapping("/recycleBin/completelyDelete")
    @ApiOperation(value = "彻底删除回收站文件")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public Result deleteFile(@RequestBody List<Integer> fileIds) {

        userFileListService.completelyDeleteFile(fileIds);
        return Result.success();

    }

    /**
     * 清空回收站
     * @return
     */
    @DeleteMapping("/recycleBin/clearAll")
    @ApiOperation(value = "清空回收站文件")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String",required = true)
    public Result clearAll() {

        userFileListService.clearAll();
        return Result.success();

    }

}
