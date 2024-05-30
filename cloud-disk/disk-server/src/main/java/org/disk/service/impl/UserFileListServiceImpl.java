package org.disk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import lombok.extern.slf4j.Slf4j;
import org.disk.aop.annotation.PathEmpty;
import org.disk.constant.CommonConstant;
import org.disk.constant.ExceptionConstant;
import org.disk.context.BaseContext;
import org.disk.entity.FileInfo;
import org.disk.entity.UserDiskCap;
import org.disk.entity.UserFileList;
import org.disk.entity.UserInfo;
import org.disk.exception.AuthorizationException;
import org.disk.exception.CloudDiskException;
import org.disk.mapper.FileInfoMapper;
import org.disk.mapper.UserDiskCapMapper;
import org.disk.mapper.UserFileListMapper;
import org.disk.mapper.UserInfoMapper;
import org.disk.result.PageResult;
import org.disk.result.Result;
import org.disk.service.AsyncService;
import org.disk.service.IRedisService;
import org.disk.service.IUserFileListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.disk.utils.RandonStringUtils;
import org.disk.vo.pyl.CollectionFileVO;
import org.disk.vo.yls.PageVO;
import org.disk.vo.yls.ShareFilesVO;
import org.disk.vo.yls.UserFileVO;
import org.disk.vo.yls.UserVO;
import org.disk.vo.ysx.RecycleBinFilesVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.net.Inet4Address;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2023-12-10
 */
@Service
@Slf4j
public class UserFileListServiceImpl extends ServiceImpl<UserFileListMapper, UserFileList> implements IUserFileListService {

    @Resource
    UserFileListMapper userFileListMapper;

    @Resource
    UserInfoMapper userInfoMapper;

    @Resource
    FileInfoMapper fileInfoMapper;

    @Resource
    IRedisService redisService;

    @Resource
    AsyncService asyncService;

    @Autowired
    UserDiskCapMapper userDiskCapMapper;


    @Override
    @PathEmpty
    public UserFileList createDirectory(String path, String dirName, UserInfo user,Boolean isDirectory) {
        checkPath(path,user.getId());

        dirName=path+"/"+dirName;

        UserFileList userFileList = new UserFileList();
        userFileList.setUserId(user.getId());
        userFileList.setFileName(dirName);
        userFileList.setParentPath(path);
        userFileList.setIsDir(isDirectory);

        userFileList.setStatus(1);

        userFileList.setChangeDate(LocalDateTime.now());


        userFileListMapper.insert(userFileList);

        return userFileList;
    }

    private void checkPath(String path,Long id) throws CloudDiskException{
        if(StringUtils.hasText(path)){
            Integer count = userFileListMapper.selectCount(new QueryWrapper<UserFileList>().lambda().eq(UserFileList::getFileName, path).eq(UserFileList::getUserId, id));
            if(count==0) {
                throw new CloudDiskException("path不存在");
            }
        }
    }

    @Override
    @PathEmpty
    public PageResult<UserFileVO> getUserFileList(String path, UserInfo user, Integer begin, Integer end) {

        Page<UserFileList> userFileListPage = userFileListMapper.selectPage(new Page<>(begin, end)
                , new QueryWrapper<UserFileList>().lambda().eq(UserFileList::getUserId
                , user.getId()).eq(UserFileList::getParentPath, path).eq(UserFileList::getStatus,1));
        if (0 == userFileListPage.getRecords().size()){
            return new PageResult<>(0,new LinkedList<>());
        }

        List<Long> users=userFileListPage.getRecords().stream().map(UserFileList::getUserId).collect(Collectors.toList());
        Map<Long, String> userMap = userInfoMapper.selectBatchIds(users).stream()
                .collect(Collectors.toMap(UserInfo::getId, UserInfo::getUserName));

        return new PageResult<>(userFileListPage.getTotal(),convert2VO(new ArrayList<>(userFileListPage.getRecords()),userMap));
    }

    @Override
    public Result deleteFilesByIds(List<Integer> ids, UserInfo user) {
        List<UserFileList> fileLists = userFileListMapper.selectBatchIds(ids);
        if(fileLists==null||fileLists.size()==0){
            return Result.error("文件id错误");
        }
        return deleteBatch(fileLists,user.getId());
    }

    @Override
    public List<Integer> collectFilesByIds(List<Integer> ids, UserInfo user) {
        List<UserFileList> fileLists = userFileListMapper.selectBatchIds(ids);
        LinkedList<Integer> failCollection = new LinkedList<>();
        for(UserFileList files : fileLists){
            if(!files.getUserId().equals(user.getId())){
                log.info("fail:{}",files.getId());
                failCollection.add(files.getId());
//                fileLists.remove(files);
                continue;
            }
            files.setCollection(1);
            files.setChangeDate(LocalDateTime.now());
            userFileListMapper.updateById(files);
        }
        return failCollection;
    }

    @Override
    public ShareFilesVO getShareFiles(String token) {
        ShareFilesVO vo = redisService.getCacheValue(token);
        List<Integer> sharedIds = vo.getSharedIds();
        LinkedList<UserFileList> list = new LinkedList<>(userFileListMapper.selectBatchIds(sharedIds));
        vo.setSharedFiles(convert2VO(list,new HashMap<Long,String>(){{
            put(vo.getUser().getUserId(),vo.getUser().getUsername());
        }}));

        return vo;
    }

    @Override
    public String  shareFilesByIds(List<Integer> ids, UserInfo user,Long timeout) {
        List<UserFileList> list = userFileListMapper.selectBatchIds(ids);
        LinkedList<Integer> res = new LinkedList<>();

        for(UserFileList userFileList:list){
            if(!userFileList.getUserId().equals(user.getId())||userFileList.getStatus().equals(0)){
                throw new CloudDiskException("无分享权限");
            }

            if(Objects.isNull(userFileList.getIsDir()))
                userFileList.setIsDir(false);

            if(userFileList.getIsDir()){
                throw new CloudDiskException("不可分享文件夹");
            }

            res.add(userFileList.getId());

        }

        UserVO userVO = new UserVO();
        userVO.setUsername(user.getUserName());
        userVO.setUserId(user.getId());

        ShareFilesVO shareFilesVO = new ShareFilesVO();
        shareFilesVO.setSharedIds(res);
        shareFilesVO.setUser(userVO);

        String randomString = RandonStringUtils.getRandomString(CommonConstant.RANDOM_STRING_LENGTH);

        if (timeout == null || timeout.equals(0L)) {
            redisService.setCacheObject(randomString, shareFilesVO);
        } else {
            redisService.setCacheObject(randomString, shareFilesVO, timeout, TimeUnit.SECONDS);
        }

        return randomString;
    }

    @Override
    @Transactional
    public Result<String> updateFileName(String fileName, Integer userFileId) {
        UserFileList file = userFileListMapper.selectById(userFileId);

        if(Objects.isNull(file)){
            return Result.error("userFile id 不存在");
        }
        if(!file.getUserId().equals(BaseContext.getCurrentId())){
            throw new AuthorizationException("非本人文件");
        }

        if(Boolean.TRUE.equals(file.getIsDir())){
            String newName = generateDirectoryName(file.getFileName(), fileName,true);
            asyncService.renameDirectory(newName,file);
        }else{
            file.setFileName(fileName);
            userFileListMapper.updateById(file);
        }

        return Result.success();
    }

    @Override
    public Result<String> changeSharedStatus(Boolean isShared, Integer userFileId) {
        UserFileList file = userFileListMapper.selectById(userFileId);

        if(Objects.isNull(file)){
            return Result.error("userFile id 不存在");
        }
        if(!file.getUserId().equals(BaseContext.getCurrentId())){
            throw new AuthorizationException("非本人文件");
        }

        file.setChangeDate(LocalDateTime.now());
        file.setShare(isShared ?1:0);
        userFileListMapper.updateById(file);

        return Result.success();
    }

    @Override
    public PageResult<UserFileVO> getFilesByType(String type, UserInfo user, Integer curr, Integer size) {
        Page<UserFileList> page = new Page<>(curr, size);
        userFileListMapper.selectPage(page,new LambdaQueryWrapper<UserFileList>()
                        .and(wrapper ->wrapper
                                .eq(UserFileList::getUserId,user.getId())
                                .eq(UserFileList::getStatus,1)
                                .likeLeft(UserFileList::getFileName,type))
                        .or(wrapper ->wrapper
                                .eq(UserFileList::getIsDir,0)
                                .isNull(UserFileList::getIsDir)));

        if(page.getRecords().size()==0){
            return new PageResult<>();
        }

        List<Long> users=page.getRecords().stream().map(UserFileList::getUserId).collect(Collectors.toList());
        Map<Long, String> userMap = userInfoMapper.selectBatchIds(users).stream()
                .collect(Collectors.toMap(UserInfo::getId, UserInfo::getUserName));

        return new PageResult<>(page.getTotal(),convert2VO(new ArrayList<>(page.getRecords()),userMap));
    }

    @Override
    @Transactional
    @PathEmpty
    public Result moveFiles(String path, Integer id, UserInfo user) {
        UserFileList userFileList = userFileListMapper.selectById(id);

        if(Objects.isNull(userFileList)){
            return Result.error("id not found");
        }

        if(!userFileList.getUserId().equals(user.getId())){
            throw new AuthorizationException("无文件权限");
        }

        checkPath(path,user.getId());

        if(Boolean.TRUE.equals(userFileList.getIsDir())){
            userFileList.setParentPath(path);
            String newPath = generateDirectoryName(userFileList.getFileName(), path,false);
            asyncService.renameDirectory(newPath,userFileList);
        }else {
            userFileList.setParentPath(path);
            userFileListMapper.updateById(userFileList);
        }
        return Result.success();
    }

    /**
     * 分页查询收藏文件
     * @param id
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult<CollectionFileVO> collectFile(Long id,int pageNum, int pageSize) {
        UserInfo userInfo = userInfoMapper.selectById(id);
        if (userInfo == null || userInfo.getStatus() == 0) {
            throw new CloudDiskException("无权限");
        }
        String userName = userInfo.getUserName();//用户名
        //1、在用户文件列表中搜出用户搜藏文件
        QueryWrapper<UserFileList> qw = new QueryWrapper<>();
        qw.eq("user_id", id);
        qw.eq("collection", 1);
        qw.eq("status",1);
        List<UserFileList> fileLists = this.list(qw);//总的数据条数
        Page<UserFileList> page = this.page(new Page<>(pageNum, pageSize), qw);
        List<UserFileList> records = page.getRecords();
        ArrayList<CollectionFileVO> list = new ArrayList<>();
        for (UserFileList record : records) {
            CollectionFileVO collectionFileVO = new CollectionFileVO();
            //2、如果不是文件夹，就在FileInfo表中查找这些文件信息
            if (record.getIsDir()!=null && record.getIsDir()!=false) {
                //是文件夹
                String parentPath = record.getParentPath();
                if(parentPath != null){
                    String newParentPath = parentPath.substring(userName.length());
                    collectionFileVO.setParent_path(newParentPath);
                }else{
                    collectionFileVO.setParent_path(null);
                }
                String fileName = record.getFileName();
                String newFileName = fileName.substring(userName.length());
                collectionFileVO.setFileName(newFileName);

            } else {
                QueryWrapper<FileInfo> fileInfoQueryWrapper = new QueryWrapper<>();
                fileInfoQueryWrapper.eq("file_id", record.getFileId());
                FileInfo fileInfo = fileInfoMapper.selectOne(fileInfoQueryWrapper);
                if (fileInfo == null) {
                    throw new CloudDiskException(ExceptionConstant.FILE_NOT_EXIST);
                }
                collectionFileVO.setFileId(record.getFileId());
                collectionFileVO.setUrl(fileInfo.getUrl());
                collectionFileVO.setType(fileInfo.getType());
                collectionFileVO.setSize((long) Math.ceil((double) fileInfo.getSize() / (1024 * 1024) * 100));
                collectionFileVO.setUpload_time(fileInfo.getUploadTime());
                collectionFileVO.setFileName(record.getFileName());
            }
            //公共信息
            collectionFileVO.setId(record.getId());
            collectionFileVO.setChangeDate(record.getChangeDate());
            list.add(collectionFileVO);
        }
        PageResult<CollectionFileVO> pageResult = new PageResult<>(fileLists.size(), list);
        return pageResult;
    }
    private List<UserFileVO> convert2VO(List<UserFileList> list,Map<Long, String> map){
        List<Long> fileIds = list.stream().map(UserFileList::getFileId).collect(Collectors.toList());
        Map<Long, FileInfo> files = fileInfoMapper
                .selectList(new QueryWrapper<FileInfo>().lambda()
                        .in(FileInfo::getFileId,fileIds))
                .stream().collect(Collectors.toMap(FileInfo::getFileId, Function.identity()));

        LinkedList<UserFileVO> vos = new LinkedList<>();

        for(UserFileList userFileList:list){
            String username = map.get(userFileList.getUserId());
            //处理路径的用户名前缀
            UserFileVO vo = new UserFileVO(userFileList,files.get(userFileList.getFileId()),username);

            if(!Objects.isNull(vo.getParentPath()))
                vo.setParentPath(vo.getParentPath().substring(username.length()));
            if(vo.getFileName().startsWith(username)){
                vo.setFileName(vo.getFileName().substring(username.length()));
            }

            vos.add(vo);
        }

        return vos;
    }

    /**
     * 递归删除
     * @param list
     * @param userId
     * @return
     */
    private Result deleteBatch(List<UserFileList> list,Long userId){
        for(UserFileList userFileList:list){
            if(!userFileList.getUserId().equals(userId)){
                return Result.error("无权限删除");
            }
            if(userFileList.getStatus().equals(0)){
                continue;
            }
            if(Objects.isNull(userFileList.getIsDir()))
                userFileList.setIsDir(false);

            if(userFileList.getIsDir()){
                 List<UserFileList> fileLists = userFileListMapper.selectList(new QueryWrapper<UserFileList>().lambda()
                         .eq(UserFileList::getParentPath, userFileList.getFileName()));
                 if (fileLists.size() > 0){
                     deleteBatch(fileLists,userId);
                 }
             }
             userFileList.setDeleteTime(LocalDate.from(LocalDateTime.now()));
             userFileList.setStatus(0);
             userFileListMapper.update(userFileList,new UpdateWrapper<UserFileList>().lambda()
                     .eq(UserFileList::getId, userFileList.getId())
                     .eq(UserFileList::getStatus,1));
        }

        return Result.success();
    }

    private String generateDirectoryName(String old,String filename,Boolean isRename){
        String[] split= old.split("/");
        String res;
        if(isRename){
            split[split.length-1]=filename;
            res=String.join("/", split);
        }else {
            res=filename+"/"+split[split.length-1];
        }
        return res;
    }

    /**
     * 查询一个文件夹下的所有文件,查询回收站文件，恢复文件夹，删除文件夹时使用
     * @param fileName
     * @param userId
     * @return  目录下的所有的文件和文件夹
     */
    public List<UserFileList> selectAllFiles(String fileName, Long userId){
        QueryWrapper<UserFileList> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("parent_path", fileName);
        List<UserFileList> userFileList = userFileListMapper.selectList(wrapper);
        List<UserFileList> containFiles = new ArrayList<>();
        for (UserFileList userFile:userFileList){
            containFiles.add(userFile);
            if (userFile.getIsDir()){
                List<UserFileList> subFiles = selectAllFiles(userFile.getFileName(),userId);
                containFiles.addAll(subFiles);
            }
        }
        return containFiles;
    }

    /**
     * 分页查询回收站文件
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<RecycleBinFilesVo> listRecycleBinFiles(int page, int size) {
        QueryWrapper<UserFileList> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", BaseContext.getCurrentId())
                .eq("status", 0);
        List<UserFileList> allFiles = userFileListMapper.selectList(wrapper);
        //用于检验回收站中的文件是否存在于被删除的文件夹中
        List<UserFileList> allFiles1 = userFileListMapper.selectList(wrapper);
        //遍历所有被删除文件
//        for (UserFileList file:allFiles){
        for (int i = 0; i < allFiles.size(); i++ ){
            UserFileList file = allFiles.get(i);
            //如果被删除的是文件夹，获取文件夹内所有文件
            if (file.getIsDir()){
                List<UserFileList> containFiles = selectAllFiles(file.getFileName(), BaseContext.getCurrentId());
                for (UserFileList containFile:containFiles){
                    //将文件夹内包含文件与所有被删除文件对比，若id相同则不在回收站显示
                    for (UserFileList file1:allFiles1){
                        if (file1.getId().equals(containFile.getId())){
                            allFiles.remove(file1);
                        }
                    }
                }
            }
        }

        long total = allFiles.size();
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, allFiles.size());

        List<UserFileList> result = allFiles.subList(startIndex, endIndex);
        List<RecycleBinFilesVo> records = new ArrayList<>();
        for (UserFileList file:result){
            records.add(new RecycleBinFilesVo(file.getId(),file.getFileName(),file.getIsDir(),file.getDeleteTime()));
        }

        return new PageResult<>(total, records);
    }

    /**
     * 恢复回收站文件
     * @param fileIds
     * @return
     */
    @Override
    public void restoreFile(List<Integer> fileIds) {
        UserInfo currentUser = userInfoMapper.selectById(BaseContext.getCurrentId());
        List<UserFileList> userFileList = new ArrayList<>();
        for (Integer id:fileIds){
            QueryWrapper<UserFileList> wrapper = new QueryWrapper<>();
            wrapper.eq("id", id)
                    .eq("user_id", currentUser.getId())
                    .eq("status", 0);
            userFileList.add(userFileListMapper.selectOne(wrapper));
        }

        for (UserFileList file:userFileList){

            if (file.getIsDir()){
                List<UserFileList> allFiles = selectAllFiles(file.getFileName(),currentUser.getId());
                for (UserFileList containFile:allFiles){
                    UpdateWrapper<UserFileList> wrapper = new UpdateWrapper<>();
                    wrapper.lambda().eq(UserFileList::getFileName, containFile.getFileName())
                                    .set(UserFileList::getStatus, 1)
                                    .set(UserFileList::getDeleteTime, null);
                    userFileListMapper.update(containFile, wrapper);
                }

            }
            UpdateWrapper<UserFileList> wrapper = new UpdateWrapper<>();
            wrapper.lambda().eq(UserFileList::getFileName, file.getFileName())
                    .set(UserFileList::getStatus, 1)
                    .set(UserFileList::getDeleteTime, null)
                    .set(UserFileList::getParentPath, currentUser.getUserName());
            userFileListMapper.update(file,wrapper);

        }
    }

    /**
     * 彻底删除回收站文件
     * @param fileIds
     */
    @Override
    public void completelyDeleteFile(List<Integer> fileIds) {
        List<UserFileList> userFileList = new ArrayList<>();
        for (Integer id:fileIds){
            QueryWrapper<UserFileList> wrapper = new QueryWrapper<>();
            wrapper.eq("id", id)
                    .eq("user_id", BaseContext.getCurrentId())
                    .eq("status", 0);
            userFileList.add(userFileListMapper.selectOne(wrapper));
        }

        for (UserFileList file:userFileList){
            if (file.getIsDir()){
                List<UserFileList> allFiles = selectAllFiles(file.getFileName(),BaseContext.getCurrentId());
                //删除文件夹内所有文件
                for (UserFileList containFile:allFiles){
                    if (containFile.getIsDir()){    //若为文件夹则不需设置count=count-1
                        userFileListMapper.deleteById(containFile);
                    }else {
                        userFileListMapper.deleteById(containFile);
                        QueryWrapper<FileInfo> wrapper = new QueryWrapper<>();
                        wrapper.eq("file_id", containFile.getFileId());
                        FileInfo fileInfo = fileInfoMapper.selectOne(wrapper);
                        fileInfo.setCount(fileInfo.getCount()-1);
                        fileInfoMapper.updateById(fileInfo);

                        QueryWrapper<UserDiskCap> wrapper1 = new QueryWrapper<>();
                        wrapper1.eq("user_id", BaseContext.getCurrentId());
                        UserDiskCap userDiskCap = userDiskCapMapper.selectOne(wrapper1);
                        userDiskCap.setUsedCap(userDiskCap.getUsedCap()-fileInfo.getSize()/(1024*1024));
                        userDiskCapMapper.updateById(userDiskCap);
                    }

                }
                //删除该文件夹
                userFileListMapper.deleteById(file);
            }else {
                userFileListMapper.deleteById(file);
                QueryWrapper<FileInfo> wrapper = new QueryWrapper<>();
                wrapper.eq("file_id", file.getFileId());
                FileInfo fileInfo = fileInfoMapper.selectOne(wrapper);
                fileInfo.setCount(fileInfo.getCount()-1);
                fileInfoMapper.updateById(fileInfo);

                QueryWrapper<UserDiskCap> wrapper1 = new QueryWrapper<>();
                wrapper1.eq("user_id", BaseContext.getCurrentId());
                UserDiskCap userDiskCap = userDiskCapMapper.selectOne(wrapper1);
                userDiskCap.setUsedCap(userDiskCap.getUsedCap()-fileInfo.getSize()/(1024*1024));
                userDiskCapMapper.updateById(userDiskCap);
            }
        }

    }

    /**
     * 清空回收站
     */
    @Override
    public void clearAll() {
        QueryWrapper<UserFileList> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", BaseContext.getCurrentId())
                .eq("status", 0);
        List<UserFileList> files = userFileListMapper.selectList(wrapper);
        if (files.isEmpty()){
            throw new CloudDiskException(ExceptionConstant.CLEARING_FAILED);
        }
        for (UserFileList file : files) {
            userFileListMapper.deleteById(file);
            if (!file.getIsDir()){

                QueryWrapper<FileInfo> wrapper1 = new QueryWrapper<>();
                wrapper1.eq("file_id", file.getFileId());
                FileInfo fileInfo = fileInfoMapper.selectOne(wrapper1);
                fileInfo.setCount(fileInfo.getCount()-1);
                fileInfoMapper.updateById(fileInfo);

                QueryWrapper<UserDiskCap> wrapper2 = new QueryWrapper<>();
                wrapper2.eq("user_id", BaseContext.getCurrentId());
                UserDiskCap userDiskCap = userDiskCapMapper.selectOne(wrapper2);
                userDiskCap.setUsedCap(userDiskCap.getUsedCap()-fileInfo.getSize()/(1024*1024));
                userDiskCapMapper.updateById(userDiskCap);
            }
        }
    }
}
