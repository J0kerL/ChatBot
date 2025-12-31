package com.chatbot.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 * @author Diamond
 */
@Getter
public class BizException extends RuntimeException {

    /**
     * 错误码
     */
    private Integer code;

    public BizException(String message) {
        super(message);
        this.code = 1;
    }

    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
        this.code = 1;
    }
}
