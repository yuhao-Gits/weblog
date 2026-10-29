package com.yuhao.weblog.module.admin.controller;

import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.admin.model.req.LoginReq;
import com.yuhao.weblog.module.admin.model.req.UpdatePasswordReq;
import com.yuhao.weblog.module.admin.model.vo.LoginRspVO;
import com.yuhao.weblog.module.admin.model.vo.UserInfoVO;
import com.yuhao.weblog.module.admin.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginRspVO> login(@RequestBody @Valid LoginReq req) {
        return authService.login(req);
    }

    @GetMapping("/user/info")
    public Result<UserInfoVO> userInfo() {
        return authService.userInfo();
    }

    @PutMapping("/user/password/update")
    public Result<Boolean> updatePassword(@RequestBody @Valid UpdatePasswordReq req) {
        return authService.updatePassword(req);
    }
}
