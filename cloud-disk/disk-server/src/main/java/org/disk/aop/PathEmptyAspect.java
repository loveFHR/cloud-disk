package org.disk.aop;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.checkerframework.checker.guieffect.qual.UI;
import org.disk.context.BaseContext;
import org.disk.entity.UserFileList;
import org.disk.entity.UserInfo;
import org.disk.exception.CloudDiskException;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;

@Aspect
@Component
@Slf4j
public class PathEmptyAspect {

    @Pointcut("@annotation(org.disk.aop.annotation.PathEmpty)")
    public void pointcut() {
    }

    @Around(value = "pointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();

        //获取参数值
        Object[] args = joinPoint.getArgs();

        int pathIndex = ArrayUtils.indexOf(parameterNames, "path");
        int userIndex = ArrayUtils.indexOf(parameterNames, "user");

        if(pathIndex==-1) {
            pathIndex = ArrayUtils.indexOf(parameterNames, "parentPath");
        }

        UserInfo user=userIndex==-1? BaseContext.getCurrentUser():(UserInfo) args[userIndex];

        if( pathIndex!=-1 && !Objects.isNull(user)){
            String path= (String) args[pathIndex];
            log.warn("PathEmpty处理path==>"+path);

            if(StringUtils.hasText(path)){
                if(!path.startsWith("/")||path.substring(1).length()==0){
                    throw new CloudDiskException("路径名必须为空或 以 / 开头,不可单独传/");
                }
            }

            if(!StringUtils.hasText(path)){
                path=user.getUserName();
            }else {
                path=user.getUserName()+path;
            }
            args[pathIndex]=path;

            log.info("PathEmpty处理path==>"+path);
        }else {
            log.warn("缺少参数,PathEmpty无法处理path");
        }


        return joinPoint.proceed(args);
    }
}
