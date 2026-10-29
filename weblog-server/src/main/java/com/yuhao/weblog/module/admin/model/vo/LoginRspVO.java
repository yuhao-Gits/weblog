package com.yuhao.weblog.module.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRspVO {
    private String token;
    private String username;
    private String role;
}
