package com.chatbot.common.result;

import lombok.Data;

/**
 * 统一返回结果封装
 * @author Diamond
 */
@Data
public class Result<T> {

    /**
     * 响应码：0表示成功，1表示失败
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功码
     */
    public static final Integer SUCCESS_CODE = 0;

    /**
     * 失败码
     */
    public static final Integer FAIL_CODE = 1;

    public Result() {
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功返回（有数据）
     */
    public static <T> Result<T> ok(T data) {
        return new Result<>(SUCCESS_CODE, "success", data);
    }

    /**
     * 成功返回（无数据）
     */
    public static <T> Result<T> ok() {
        return new Result<>(SUCCESS_CODE, "success", null);
    }

    /**
     * 成功返回（自定义消息）
     */
    public static <T> Result<T> ok(String msg, T data) {
        return new Result<>(SUCCESS_CODE, msg, data);
    }

    /**
     * 失败返回
     */
    public static <T> Result<T> fail(String msg) {
        return new Result<>(FAIL_CODE, msg, null);
    }

    /**
     * 失败返回（自定义错误码）
     */
    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 失败返回（带数据）
     */
    public static <T> Result<T> fail(String msg, T data) {
        return new Result<>(FAIL_CODE, msg, data);
    }
}
