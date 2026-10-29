package com.yuhao.weblog.common;

import lombok.Getter;

/**
 * 业务异常：携带业务错误码，由全局异常处理器统一转为统一响应
 */
@Getter
public class BizException extends RuntimeException {

    private final String code;

    public BizException(String message) {
        super(message);
        this.code = ResponseCodeEnum.FAIL.getCode();
    }

    public BizException(ResponseCodeEnum codeEnum) {
        super(codeEnum.getMessage());
        this.code = codeEnum.getCode();
    }

    public BizException(ResponseCodeEnum codeEnum, String message) {
        super(message);
        this.code = codeEnum.getCode();
    }

    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }
}
