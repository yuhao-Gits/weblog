package com.yuhao.weblog.module.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.yuhao.weblog.common.BizException;
import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.common.ResponseCodeEnum;
import com.yuhao.weblog.common.utils.SecurityUtils;
import com.yuhao.weblog.framework.security.JwtUtil;
import com.yuhao.weblog.module.admin.mapper.UserMapper;
import com.yuhao.weblog.module.admin.model.entity.User;
import com.yuhao.weblog.module.admin.model.req.LoginReq;
import com.yuhao.weblog.module.admin.model.req.UpdatePasswordReq;
import com.yuhao.weblog.module.admin.model.vo.LoginRspVO;
import com.yuhao.weblog.module.admin.model.vo.UserInfoVO;
import com.yuhao.weblog.module.admin.service.AuthService;
import com.yuhao.weblog.module.captcha.service.SliderCaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final SliderCaptchaService sliderCaptchaService;

    @Override
    public Result<LoginRspVO> login(LoginReq req) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
            // 密码正确后再消费滑块验证凭证（一次性）
            sliderCaptchaService.consumePassToken(req.getCaptchaToken());
            String username = authentication.getName();
            User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
            String token = jwtUtil.generateToken(username, user.getRole());
            LoginRspVO vo = new LoginRspVO(token, username, user.getRole());
            return Result.success(vo);
        } catch (AuthenticationException e) {
            throw new BizException(ResponseCodeEnum.LOGIN_FAIL, "用户名或密码错误");
        }
    }

    @Override
    public Result<UserInfoVO> userInfo() {
        String username = SecurityUtils.getUsername();
        if (username == null) {
            return Result.fail(ResponseCodeEnum.UNAUTHORIZED.getCode(), "未登录");
        }
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
        UserInfoVO vo = new UserInfoVO(user.getUsername(), user.getRole());
        return Result.success(vo);
    }

    @Override
    public Result<Boolean> updatePassword(UpdatePasswordReq req) {
        String username = SecurityUtils.getUsername();
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
        if (user == null) {
            return Result.fail(ResponseCodeEnum.UNAUTHORIZED.getCode(), "未登录");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BizException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userMapper.updateById(user);
        return Result.success(true);
    }
}
