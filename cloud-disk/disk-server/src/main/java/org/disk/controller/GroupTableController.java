package org.disk.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.disk.constant.ExceptionConstant;
import org.disk.dto.pyl.*;
import org.disk.result.PageResult;
import org.disk.result.Result;
import org.disk.service.IGroupTableService;
import org.disk.service.IUserInfoService;
import org.disk.vo.pyl.GroupInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/groups")
@Api(tags = "部门接口")
@CrossOrigin
public class GroupTableController {
    @Autowired
    private IUserInfoService iUserInfoService;

    @Autowired
    private IGroupTableService iGroupTableService;

//    /**
//     * 根据部门查用户
//     *
//     * @param userQuery
//     * @return
//     */
//    @GetMapping("/page/getUsersByGroup")
//    @ApiOperation("根据部门查用户")
//    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String")
//    public Result<PageResult> getUsersByGroup(UserQuery userQuery) {
//        String groupName = userQuery.getGroupName();
//        if(groupName == null){
//            return Result.error(ExceptionConstant.NULL_ERROR);
//        }
//        int pageNum = userQuery.getPageNum();
//        int pageSize = userQuery.getPageSize();
//
//        PageResult usersByGroup = iUserInfoService.getUsersByGroup(groupName, pageNum, pageSize);
//        return Result.success(usersByGroup);
//    }

    @GetMapping("/getAllGroups")
    @ApiOperation("查询所有部门")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String")
    public Result<List<GroupInfoVO>> getAllGroups() {
        List<GroupInfoVO> list = iGroupTableService.getAllGroups();
        return Result.success(list);
    }

    /**
     * 查询所有部门 包括部门人数
     *
     * @param groupQuery
     * @return
     */
    @GetMapping("/page/getGroupsByPage")
    @ApiOperation("分页查询部门")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String")
    public Result<PageResult> getGroupsByPage(GroupQuery groupQuery) {
        String name = groupQuery.getName();
        int pageNum = groupQuery.getPageNum();
        int pageSize = groupQuery.getPageSize();
        PageResult pageResult = iGroupTableService.getGroupsByPage(name,pageNum, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 新添加部门
     * @param groupDto
     * @return
     */
    @PostMapping("/addGroup")
    @ApiOperation("新添加部门")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String")
    public Result<Boolean> addGroup(@RequestBody GroupDto groupDto) {
        String groupName = groupDto.getGroupName();
        boolean b = iGroupTableService.addGroup(groupName);
        if (b == false) {
            return Result.error("添加失败");
        }
        return Result.success(b);
    }

    /**
     * 删除某个部门
     *
     * @param deleteDto
     * @return
     */
    @PostMapping("/deleteGroupByName")
    @ApiOperation("删除某个部门")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String")
    public Result<Boolean> deleteGroupByName(@RequestBody DeleteDto deleteDto) {
        String deleteName = deleteDto.getName();
        if (deleteName == null) {
            return Result.error(ExceptionConstant.NULL_ERROR);
        }
        boolean b = iGroupTableService.deleteGroupByName(deleteName);
        if (b == false) {
            return Result.error("删除失败");
        }
        return Result.success(b);
    }

    /**
     * 批量删除部门
     * @param deleteDto
     * @return
     */
    @PostMapping("/deleteGrouplist")
    @ApiOperation("批量删除部门")
    @ApiImplicitParam(value = "鉴权token",name = "token",paramType = "header", dataType = "String")
    public Result<Boolean> deleteGrouplist(@RequestBody DeleteDto deleteDto) {
        List<Long> list = deleteDto.getList();
        boolean b = iGroupTableService.removeByIds(list);
        if (b == true) {
            return Result.success(b);
        } else {
            return Result.error(500, "删除失败");
        }
    }

}
