package com.yuhao.weblog.module.admin.model.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdatePasswordReq {

    @NotBlank(message = "原密码不能为空")
    private String password;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
