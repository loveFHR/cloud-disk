package org.disk.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 后端统一返回结果
 * @param <T>
 */
@Data
public class Result<T> implements Serializable {

    private Integer code; //编码：101200成功，0和其它数字为失败
    private String msg; //错误信息
    private T data; //数据

    /**
     * 成功码
     */
    public static final Integer  SUCCESS=200;

    /**
     * 失败码
     */
    public static final Integer  FAIL=0;

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = SUCCESS;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = SUCCESS;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = FAIL;
        return result;
    }

    public static <T> Result<T> error(T obj,String msg) {
        Result result = new Result();
        result.data = obj;
        result.msg=msg;
        result.code = FAIL;
        return result;
    }

    public static <T> Result<T> error(int code,String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = code;
        return result;
    }
}
