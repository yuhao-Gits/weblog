package com.yuhao.weblog.module.captcha.controller;

import com.yuhao.weblog.common.Result;
import com.yuhao.weblog.module.captcha.service.SliderCaptchaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 滑块验证码接口
 */
@RestController
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final SliderCaptchaService sliderCaptchaService;

    /** 获取滑块验证码 */
    @GetMapping("/generate")
    public Result<Map<String, Object>> generate() {
        return Result.success(sliderCaptchaService.generate());
    }

    /** 校验滑块位置（成功后返回 passToken） */
    @PostMapping("/verify")
    public Result<Map<String, Object>> verify(@RequestParam("token") String token,
                                              @RequestParam("x") Integer x) {
        return sliderCaptchaService.verify(token, x);
    }
}
