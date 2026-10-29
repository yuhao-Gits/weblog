package com.yuhao.weblog.module.admin.model.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginReq {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    /** 验证通过凭证（滑块校验成功后由后端颁发） */
    private String captchaToken;
}
