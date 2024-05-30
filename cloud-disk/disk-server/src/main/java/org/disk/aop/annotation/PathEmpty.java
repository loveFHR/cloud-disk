package org.disk.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 拼接路径名
 * 方法参数名必须有 path or parrentPath
 * 方法参数名必须有 user 或者是 经过登录拦截的方法
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.METHOD,//作用在方法上
})
public @interface PathEmpty {

}
