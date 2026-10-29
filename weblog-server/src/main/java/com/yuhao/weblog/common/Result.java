package com.yuhao.weblog.common;

import lombok.Getter;

import java.io.Serializable;

/**
 * 统一响应工具类
 */
@Getter
public class Result<T> implements Serializable {

    private String code;
    private String message;
    private T data;
    /** 成功标识 */
    private boolean success;

    private Result() {
    }

    public static <T> Result<T> success() {
        return build(ResponseCodeEnum.SUCCESS.getCode(), ResponseCodeEnum.SUCCESS.getMessage(), null, true);
    }

    public static <T> Result<T> success(T data) {
        return build(ResponseCodeEnum.SUCCESS.getCode(), ResponseCodeEnum.SUCCESS.getMessage(), data, true);
    }

    public static <T> Result<T> fail(String message) {
        return build(ResponseCodeEnum.FAIL.getCode(), message, null, false);
    }

    public static <T> Result<T> fail(String code, String message) {
        return build(code, message, null, false);
    }

    public static <T> Result<T> fail(ResponseCodeEnum codeEnum) {
        return build(codeEnum.getCode(), codeEnum.getMessage(), null, false);
    }

    public static <T> Result<T> fail(ResponseCodeEnum codeEnum, String message) {
        return build(codeEnum.getCode(), message, null, false);
    }

    private static <T> Result<T> build(String code, String message, T data, boolean success) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        result.data = data;
        result.success = success;
        return result;
    }
}
