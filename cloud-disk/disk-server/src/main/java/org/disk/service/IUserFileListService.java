package org.disk.service;

import org.disk.entity.UserFileList;
import com.baomidou.mybatisplus.extension.service.IService;
import org.disk.entity.UserInfo;
import org.disk.result.PageResult;
import org.disk.result.Result;
import org.disk.vo.pyl.CollectionFileVO;
import org.disk.vo.yls.PageVO;
import org.disk.vo.yls.ShareFilesVO;
import org.disk.vo.yls.UserFileVO;
import org.disk.vo.ysx.RecycleBinFilesVo;
import org.springframework.data.domain.Page;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2023-12-10
 */
public interface IUserFileListService extends IService<UserFileList> {

    /**
     *  新建文件夹
     * @param path 父路径
     * @param dirName 文件夹名
     * @param user 用户
     * @Param isDirectory 是否是文件
     * @return 数据库返回
     */
    UserFileList createDirectory(String path, String dirName, UserInfo user,Boolean isDirectory);

    /**
     * 查询文件列表
     * @param path 指定路径
     * @param user 用户
     * @return path下文件列表
     */
    PageResult<UserFileVO> getUserFileList(String path, UserInfo user, Integer curr, Integer size);

    /**
     * 删除文件列表
     * @param ids 文件idList
     * @param user 当前用户
     * @return result
     */
    Result deleteFilesByIds(List<Integer> ids,UserInfo user);

    /**
     * 收藏文件列表
     * @param ids 文件idList
     * @param user 当前用户
     * @return result
     */
    List<Integer> collectFilesByIds(List<Integer> ids,UserInfo user);

    /**
     * 分享文件列表
     * @param ids 文件idList
     * @param user 当前用户
     * @return result
     */
    String  shareFilesByIds(List<Integer> ids,UserInfo user,Long timeout);

    /**
     * 获取分享文件
     * @param token
     * @return
     */
    ShareFilesVO getShareFiles(String token);

    PageResult<CollectionFileVO> collectFile(Long id,int pageNum, int pageSize);

    /**
     * 重命名文件或文件夹
     * @param fileName 新名字
     * @param userFileId 文件id
     * @return
     */
    Result<String> updateFileName(String fileName,Integer userFileId);

    /**
     * 部门分享或取消分享
     * @param isShared 是否分享
     * @param userFileId 用户文件id
     * @return
     */
    Result<String> changeSharedStatus(Boolean isShared,Integer userFileId);

    /**
     * 根据类型查看文件
     * @param user
     * @param curr
     * @param size
     * @return
     */
    PageResult<UserFileVO> getFilesByType(String type,UserInfo user, Integer curr, Integer size);

    /**
     * 移动文件到指定目录
     * @param path
     * @param id userFileList id
     * @param user
     * @return
     */
    Result moveFiles(String path,Integer id,UserInfo user);

    /**
     * 回收站文件列表
     * @param page
     * @param size
     * @return
     */
    PageResult<RecycleBinFilesVo> listRecycleBinFiles(int page, int size);

    /**
     * 根据选中文件的id恢复回收站文件
     * @param fileIds
     * @return
     */
    void restoreFile(List<Integer> fileIds);


    /**
     * 根据选中的文件id彻底删除回收站文件
     * @param fileIds
     */
    void completelyDeleteFile(List<Integer> fileIds);

    /**
     * 清空回收站
     */
    void clearAll();
}
