package org.disk;

import lombok.extern.slf4j.Slf4j;
import org.disk.result.PageResult;
import org.disk.service.IUserInfoService;
import org.disk.vo.pyl.UserInfoVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@Slf4j
public class AdminTest {
    @Resource
    private IUserInfoService userInfoService;

//    @Test
    void test(){
        PageResult allUser = userInfoService.getAllUser(1, 10);
        System.out.println(allUser.getRecords());
    }

//    @Test
    void test1(){
        List<UserInfoVO> s = userInfoService.getUserByName("s");
        System.out.println(s);
    }


}
