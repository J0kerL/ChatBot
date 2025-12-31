package com.chatbot.common.exception;

import com.chatbot.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 全局异常处理器
 * @author Diamond
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException e) {
        log.error("业务异常：{}", e.getMessage(), e);
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常处理 - @Valid
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        StringBuilder msg = new StringBuilder();
        for (FieldError error : fieldErrors) {
            msg.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        }
        log.error("参数校验失败：{}", msg);
        return Result.fail("参数校验失败：" + msg);
    }

    /**
     * 参数绑定异常处理
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        StringBuilder msg = new StringBuilder();
        for (FieldError error : fieldErrors) {
            msg.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        }
        log.error("参数绑定失败：{}", msg);
        return Result.fail("参数绑定失败：" + msg);
    }

    /**
     * 非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("非法参数：{}", e.getMessage(), e);
        return Result.fail("非法参数：" + e.getMessage());
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public Result<Void> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常", e);
        return Result.fail("系统错误：空指针异常");
    }

    /**
     * 其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常：{}", e.getMessage(), e);
        return Result.fail("系统异常：" + e.getMessage());
    }
}
