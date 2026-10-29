package com.yuhao.weblog.common.enums;

import lombok.Getter;

/**
 * 角色类型：管理员 / 游客（演示账号，仅查询）
 */
@Getter
public enum RoleType {

    ADMIN("ROLE_ADMIN", "管理员"),
    GUEST("ROLE_GUEST", "游客");

    private final String code;
    private final String desc;

    RoleType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RoleType fromCode(String code) {
        for (RoleType role : values()) {
            if (role.code.equalsIgnoreCase(code)) {
                return role;
            }
        }
        return GUEST;
    }
}
