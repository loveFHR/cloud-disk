package org.disk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.disk.constant.ExceptionConstant;
import org.disk.entity.GroupTable;
import org.disk.entity.UserInfo;
import org.disk.exception.CloudDiskException;
import org.disk.mapper.GroupTableMapper;
import org.disk.mapper.UserInfoMapper;
import org.disk.result.PageResult;
import org.disk.service.IGroupTableService;
import org.disk.vo.pyl.GroupInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author author
 * @since 2023-12-10
 */
@Service
public class GroupTableServiceImpl extends ServiceImpl<GroupTableMapper, GroupTable> implements IGroupTableService {

    //    @Autowired
//    private IUserInfoService iUserInfoService;
    @Autowired
    private UserInfoMapper userInfoMapper;

    /**
     * 分页获取部门信息
     *
     * @param name
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult getGroupsByPage(String name, int pageNum, int pageSize) {
        QueryWrapper<GroupTable> qw = new QueryWrapper<>();
        if(name != null){
            qw.like("group_name",name);
        }
        List<GroupTable> list = this.list(qw);//查询全部条数
        //查询全部部门并分页展示
        Page<GroupTable> groupTablePage = this.page(new Page<>(pageNum, pageSize), qw);
        List<GroupTable> records = groupTablePage.getRecords();
        List<GroupInfoVO> groupInfoVOS = getNewGroupInfo(records);
        PageResult pageResult = new PageResult(list.size(), groupInfoVOS);
        return pageResult;
    }


    /**
     * 添加部门
     *
     * @param groupName
     * @return
     */
    @Override
    public boolean addGroup(String groupName) {
        if (groupName == null) {
            throw new CloudDiskException(ExceptionConstant.NULL_ERROR);
        }
        GroupTable groupTable = new GroupTable();
        groupTable.setGroupName(groupName);
        groupTable.setCreateTime(LocalDateTime.now());
        boolean save = this.save(groupTable);
        return save;
    }

    /**
     * 删除部门
     *
     * @param deleteName
     * @return
     */
    @Override
    public boolean deleteGroupByName(String deleteName) {
        QueryWrapper<GroupTable> qw = new QueryWrapper<>();
        qw.eq("group_name", deleteName);
        GroupTable group = this.getOne(qw);
        QueryWrapper<UserInfo> userqw = new QueryWrapper<>();
        userqw.eq("group_id", group.getId());
        Integer count = userInfoMapper.selectCount(userqw);
        //如果部门包含人数不为0，不能删除
        if (count != 0) {
            throw new CloudDiskException("不允许删除");
        }
        boolean b = this.remove(qw);
        return b;
    }

    @Override
    public List<GroupInfoVO> getAllGroups() {
        List<GroupTable> list = this.list();
        List<GroupInfoVO> newGroupInfo = getNewGroupInfo(list);
        return newGroupInfo;
    }

    private List<GroupInfoVO> getNewGroupInfo(List<GroupTable> grouplist) {
        List<GroupInfoVO> groupInfoVOS = new ArrayList<>();
        for (GroupTable record : grouplist) {
            GroupInfoVO groupInfoVO = new GroupInfoVO();
            groupInfoVO.setId(record.getId());
            groupInfoVO.setGroupName(record.getGroupName());
            QueryWrapper<UserInfo> userqw = new QueryWrapper<>();
//            获取部门人数
            userqw.eq("group_id", record.getId());
            Integer count = userInfoMapper.selectCount(userqw);
            groupInfoVO.setNum(count);
            groupInfoVOS.add(groupInfoVO);
        }
        return groupInfoVOS;
    }
}
