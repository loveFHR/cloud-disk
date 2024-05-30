package org.disk.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.disk.constant.ExceptionConstant;
import org.disk.context.BaseContext;
import org.disk.entity.UserInfo;
import org.disk.exception.CloudDiskException;
import org.disk.mapper.UserDiskCapMapper;
import org.disk.result.Result;
import org.disk.service.IUserDiskCapService;
import org.disk.vo.pyl.UserCapInfoVO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/api/cap")
@Api(tags = "硬盘容量接口")
@CrossOrigin
public class UserDiskCapController {

    @Resource
    private IUserDiskCapService userDiskCapService;

    /**
     * 饼图云盘容量显示
     * @return
     */
    public Result<UserCapInfoVO> getUserCapInfo(){
        UserInfo currentUser = BaseContext.getCurrentUser();
        if(currentUser == null){
            return Result.error("未登录");
        }
        Long id = currentUser.getId();
        UserCapInfoVO userCapInfoVO = userDiskCapService.getUserCapInfo(id);
        return Result.success(userCapInfoVO);
    }
}
