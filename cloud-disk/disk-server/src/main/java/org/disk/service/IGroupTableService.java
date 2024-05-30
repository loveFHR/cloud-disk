package org.disk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.disk.entity.GroupTable;
import org.disk.result.PageResult;
import org.disk.vo.pyl.GroupInfoVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author author
 * @since 2023-12-10
 */
public interface IGroupTableService extends IService<GroupTable> {
    /**
     * 分页获取所有的部门
     *
     * @param name
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageResult getGroupsByPage(String name, int pageNum, int pageSize);


    boolean addGroup(String groupName);

    boolean deleteGroupByName(String deleteName);

    List<GroupInfoVO> getAllGroups();
}
