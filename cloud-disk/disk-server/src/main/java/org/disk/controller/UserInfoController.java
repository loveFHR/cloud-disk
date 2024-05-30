package org.disk.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.disk.constant.ExceptionConstant;
import org.disk.dto.pyl.DeleteDto;
import org.disk.dto.pyl.PageRequest;
import org.disk.dto.pyl.UpdateUserInfoDto;
import org.disk.dto.pyl.UserQuery;
import org.disk.dto.yd.UploadFileDto;
import org.disk.dto.ysx.*;
import org.disk.dto.yyf.ResetPasswordDto;
import org.disk.dto.yyf.UserLoginDto;
import org.disk.dto.yyf.UserRegisterDto;
import org.disk.entity.UserInfo;
import org.disk.exception.CloudDiskException;
import org.disk.result.PageResult;
import org.disk.result.Result;
import org.disk.service.IUserInfoService;
import org.disk.vo.pyl.UserInfoVO;
import org.disk.vo.ysx.CurrentUserInfoVo;
import org.disk.vo.yyf.UserLoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@Api(tags = "用户相关接口")
@Slf4j
@CrossOrigin
public class UserInfoController {
    @Autowired
    private IUserInfoService userInfoService;


    @PostMapping("/login")
    @ApiOperation("用户登录接口")
    public Result<UserLoginVo> login(@RequestBody UserLoginDto userLoginDto) {

        UserLoginVo userLoginVo = userInfoService.userLogin(userLoginDto);
        log.info("登录成功");
        return Result.success(userLoginVo);
    }

    @PostMapping("/register")
    @ApiOperation("用户注册接口")
    public Result register(@RequestBody UserRegisterDto userRegisterDto) {

        userInfoService.registerUser(userRegisterDto);
        log.info("注册完成");
        return Result.success();
    }


    @GetMapping("/reset/{email}")
    @ApiOperation("发送邮箱接口")
    public Result sendEmail(@PathVariable String email) {

        userInfoService.sendEmail(email);
        return Result.success();
    }

    @PostMapping("/reset")
    @ApiOperation("重置密码接口")
    public Result resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {

        userInfoService.ResetPassword(resetPasswordDto);
        return Result.success();
    }

    /**
     * 批量删除用户信息
     *
     * @param deleteDto
     * @return
     */
    @PostMapping("/deleteUserlist")
    @ApiOperation("批量删除用户信息")
    @ApiImplicitParam(value = "鉴权token", name = "token", paramType = "header", dataType = "String")
    public Result<Boolean> deleteUserlist(@RequestBody DeleteDto deleteDto) {
        List<Long> list = deleteDto.getList();
        boolean b = userInfoService.deleteUserlist(list);
        if (b == true) {
            return Result.success(b);
        } else {
            return Result.error(500, "删除失败");
        }
    }

    @PostMapping("/deleteById")
    @ApiOperation("通过id删除用户")
    @ApiImplicitParam(value = "鉴权token", name = "token", paramType = "header", dataType = "String")
    public Result<Boolean> deleteById(@RequestBody DeleteDto deleteDto) {
        Long id = deleteDto.getId();
        if (id < 0) {
            return Result.error(500, "删除失败");
        }
        boolean b = userInfoService.deleteById(id);
        if (b == true) {
            return Result.success(b);
        } else {
            return Result.error(500, "删除失败");
        }
    }

    /**
     * 修改用户状态
     *
     * @param updateUserInfoDto
     * @return
     */
    @PostMapping("/modifyUserStatusById")
    @ApiOperation("修改用户状态")
    @ApiImplicitParam(value = "鉴权token", name = "token", paramType = "header", dataType = "String")
    public Result<Boolean> modifyUserStatus(@RequestBody UpdateUserInfoDto updateUserInfoDto) {
        Long id = updateUserInfoDto.getId();
        if (id < 0) {
            throw new CloudDiskException("请求参数错误");
        }
        UserInfo byId = userInfoService.getById(id);
        if (byId == null) {
            throw new CloudDiskException("暂无此用户");
        }
        Integer status = byId.getStatus();
        UserInfo user = byId.setStatus(status == 1 ? 0 : 1);
        boolean b = userInfoService.updateById(user);
        return Result.success(b);
    }

    /**
     * 更新用户信息
     *
     * @param updateUserInfoDto
     * @return
     */
    @PostMapping("/updateUser")
    @ApiOperation("更新用户信息")
    @ApiImplicitParam(value = "鉴权token", name = "token", paramType = "header", dataType = "String")
    public Result<Integer> updateUser(@RequestBody UpdateUserInfoDto updateUserInfoDto) {
        if (updateUserInfoDto == null) {
            return Result.error("请求参数为空");
        }
        int i = userInfoService.updateUser(updateUserInfoDto);
        return Result.success(i);
    }

    @GetMapping("/listUsers")
    @ApiImplicitParam(value = "鉴权token", name = "token", paramType = "header", dataType = "String")
    @ApiOperation("获取全部用户")
    public Result<List<UserInfoVO>> getAllUsers() {
        List<UserInfoVO> userInfoVOList = userInfoService.getAllUsers();
        return Result.success(userInfoVOList);
    }

    /**
     * 分页获取用户
     *
     * @param pageRequest
     * @return
     */
    @GetMapping("/page/listUsers")
    @ApiImplicitParam(value = "鉴权token", name = "token", paramType = "header", dataType = "String")
    @ApiOperation("分页获取用户")
    public Result<PageResult> getAllUser(PageRequest pageRequest) {
        int pageSize = pageRequest.getPageSize();
        int pageNum = pageRequest.getPageNum();
        PageResult userInfoVOPage = userInfoService.getAllUser(pageNum, pageSize);
        return Result.success(userInfoVOPage);
    }

    /**
     * 分页查询用户
     *
     * @param userQuery
     * @return
     */
    @GetMapping("/page/getUsersByName")
    @ApiOperation("分页查询用户")
    @ApiImplicitParam(value = "鉴权token", name = "token", paramType = "header", dataType = "String")
    public Result<PageResult> getUsersByName(UserQuery userQuery) {
        Long groupId = userQuery.getGroupId();
        String name = userQuery.getName();
        int pageNum = userQuery.getPageNum();
        int pageSize = userQuery.getPageSize();
        PageResult usersByName = userInfoService.getUsersPageByName(groupId,name, pageNum, pageSize);

        return Result.success(usersByName);
    }

    /**
     * 查询当前用户个人信息
     */
    @GetMapping("/currentUser/getUserById")
    @ApiOperation("查询当前用户个人信息接口")
    @ApiImplicitParam(value = "鉴权token", name = "token", paramType = "header", dataType = "String")
    public Result<CurrentUserInfoVo> getUserById() {
        CurrentUserInfoVo user = userInfoService.queryUserById();
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error("用户不存在");
        }
    }

    /**
     * 修改用户信息时确认密码
     * @param password
     * @return
     */
    @PostMapping("/currentUser/confirmPassword")
    @ApiOperation("确认密码接口")
    @ApiImplicitParam(value = "鉴权token", name = "token", paramType = "header", dataType = "String")
    public Result confirmPassword(@RequestParam String password){
        boolean b = userInfoService.confirmPassword(password);
        if (b == true){
            return Result.success();
        }else {
            return Result.error("密码错误！");
        }
    }

    /**
     * 修改当前用户密码
     * @param changePasswordDto
     * @return
     */
    @PostMapping("/currentUser/updatePassword")
    @ApiOperation("修改当前用户密码接口")
    @ApiImplicitParam(value = "鉴权token", name = "token", paramType = "header", dataType = "String")
    public Result updateCurrentUser(@RequestBody ChangePasswordDto changePasswordDto) {
        String msg = userInfoService.updatePassword(changePasswordDto);
        if (msg.equals("yes")) {
            return Result.success();
        } else {
            return Result.error(msg);
        }
    }

    /**
     * 修改当前用户手机号
     * @param changePhoneDto
     * @return
     */
    @PostMapping("/currentUser/updatePhone")
    @ApiOperation("修改当前用户手机号接口")
    @ApiImplicitParam(value = "鉴权token", name = "token", paramType = "header", dataType = "String")
    public Result updateCurrentUser(@RequestBody ChangePhoneDto changePhoneDto) {
        String msg = userInfoService.updatePhone(changePhoneDto);
        if (msg.equals("yes")) {
            return Result.success();
        } else {
            return Result.error(msg);
        }
    }

    /**
     * 修改当前用户邮箱
     * @param changeEmailDto
     * @return
     */
    @PostMapping("/currentUser/updateEmail")
    @ApiOperation("修改当前用户邮箱接口")
    @ApiImplicitParam(value = "鉴权token", name = "token", paramType = "header", dataType = "String")
    public Result updateCurrentUser(@RequestBody ChangeEmailDto changeEmailDto) {
        String msg = userInfoService.updateEmail(changeEmailDto);
        if (msg.equals("yes")) {
            return Result.success();
        } else {
            return Result.error(msg);
        }
    }

    @PostMapping("/currentUser/updateAvatar")
    @ApiOperation("更新用户头像接口")
    @ApiImplicitParam(value = "鉴权token", name = "token", paramType = "header", dataType = "String")
    public Result<String> updateAvatar(@RequestParam String url){

        return userInfoService.updateAvatar(url);

    }

}
