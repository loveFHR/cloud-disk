package org.disk;

import org.disk.constant.JwtClaimsConstant;
import org.disk.entity.UserDiskCap;
import org.disk.mapper.UserDiskCapMapper;
import org.disk.properties.JwtProperties;
import org.disk.service.AsyncService;
import org.disk.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class DiskTest {

    @Autowired
    private UserDiskCapMapper userDiskCapMapper;

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    public void TestConnect() {
        userDiskCapMapper.insert(UserDiskCap.builder().userId(500L).build());
    }

    @Test
    public void TestJWt() {
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, 1735235614330462208L);
        claims.put(JwtClaimsConstant.USER_NAME, "yls_000");
        String token = JwtUtil.createJWT(
                jwtProperties.getSecretKey(),
                jwtProperties.getTtl(),
                claims);
        System.out.println(token);
    }
}
