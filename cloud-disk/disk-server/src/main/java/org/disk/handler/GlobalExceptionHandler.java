package org.disk.handler;

import lombok.extern.slf4j.Slf4j;
import org.disk.constant.ExceptionConstant;
import org.disk.exception.AuthorizationException;
import org.disk.exception.CloudDiskException;
import org.disk.result.Result;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailSendException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获异常
     * @param ex
     * @return
     */
    @ExceptionHandler(CloudDiskException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result exceptionHandler(CloudDiskException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }
    @ExceptionHandler(MailSendException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result MailSendExceptionHandler(MailSendException e){
        log.error("邮箱不存在错误:{}",e.getMessage());
        return Result.error(ExceptionConstant.MAIL_NOT_EXIST);
    }


    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result allExceptionHandler(Exception e){
        log.error("未知异常{}",e.getMessage());
        e.printStackTrace();
        return Result.error(ExceptionConstant.UNKNOWN_EXCEPTION);
    }

    @ExceptionHandler({DuplicateKeyException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result duplicateException(Exception e){
        log.error("文件名重复:{}",e.getMessage());
        return Result.error("文件名重复");
    }

    @ExceptionHandler({AuthorizationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result authError(Exception e){
        log.error(e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 处理JSR303 参数校验异常
     *
     * @param e e
     * @return {@link Result}<{@link ?}>
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BindException.class})
    public Result<?> argumentError(Exception e) {
        e.printStackTrace();
        BindingResult bindingResult = null;
        if (e instanceof MethodArgumentNotValidException) {
            bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
        } else if (e instanceof BindException) {
            bindingResult = ((BindException) e).getBindingResult();
        }
        StringBuilder msg = new StringBuilder();
        assert bindingResult != null;
        bindingResult.getFieldErrors().forEach((fieldError) ->
                msg.append(fieldError.getField()).append(fieldError.getDefaultMessage()).append(" ")
        );
        String res = msg.toString().trim();
        log.error(res);
        return Result.error(res);
    }



}
