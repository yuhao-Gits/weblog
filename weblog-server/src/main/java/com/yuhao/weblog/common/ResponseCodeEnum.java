package com.yuhao.weblog.common;

import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
public enum ResponseCodeEnum {

    SUCCESS("10000", "成功"),
    FAIL("20000", "失败"),
    PARAM_NOT_VALID("20001", "参数校验失败"),
    LOGIN_FAIL("20002", "用户名或密码错误"),
    UNAUTHORIZED("20003", "未登录或登录已过期"),
    FORBIDDEN("20004", "无权限操作"),
    SYSTEM_ERROR("50000", "系统异常");

    private final String code;
    private final String message;

    ResponseCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
