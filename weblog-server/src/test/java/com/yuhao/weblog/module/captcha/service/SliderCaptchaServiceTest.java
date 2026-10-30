package com.yuhao.weblog.module.captcha.service;

import com.yuhao.weblog.common.BizException;
import com.yuhao.weblog.common.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SliderCaptchaService 单元测试：验证码生成与校验逻辑
 * （纯逻辑部分，不依赖 AWT 图形环境）
 */
class SliderCaptchaServiceTest {

    private SliderCaptchaService service;

    @BeforeEach
    void setUp() {
        service = new SliderCaptchaService();
    }

    @Test
    @DisplayName("生成验证码返回 token、背景图、滑块图与 y 坐标")
    void generateReturnsExpectedFields() {
        Map<String, Object> result = service.generate();

        assertNotNull(result.get("token"));
        assertNotNull(result.get("backgroundImage"));
        assertNotNull(result.get("puzzleImage"));
        assertNotNull(result.get("y"));
        assertTrue(result.get("backgroundImage").toString().startsWith("data:image/png;base64,"));
    }

    @Test
    @DisplayName("校验成功时返回一次性 passToken")
    void verifySuccessIssuesPassToken() {
        Map<String, Object> generated = service.generate();
        String token = (String) generated.get("token");

        // 通过反射获取目标 x 坐标进行精确校验
        // 这里采用多次尝试：直接命中服务端缓存的实际坐标
        Result<Map<String, Object>> result = verifyWithCorrectX(token);

        assertTrue(result.isSuccess());
        Map<String, Object> data = result.getData();
        assertNotNull(data.get("passToken"));
    }

    @Test
    @DisplayName("校验失败抛出异常")
    void verifyFailThrows() {
        Map<String, Object> generated = service.generate();
        String token = (String) generated.get("token");

        // 传入一个明显错误的坐标（-9999）必然超出容差
        assertThrows(BizException.class, () -> service.verify(token, -9999));
    }

    @Test
    @DisplayName("校验无效 token 抛出「验证码已失效」异常")
    void verifyInvalidTokenThrows() {
        assertThrows(BizException.class, () -> service.verify("nonexistent-token", 100));
    }

    @Test
    @DisplayName("消费无效 passToken 抛出异常")
    void consumeInvalidPassTokenThrows() {
        assertThrows(BizException.class, () -> service.consumePassToken(null));
        assertThrows(BizException.class, () -> service.consumePassToken("invalid"));
    }

    /**
     * 通过反射读取 tokenCache 中的目标坐标，构造精确命中校验
     */
    private Result<Map<String, Object>> verifyWithCorrectX(String token) {
        try {
            java.lang.reflect.Field field = SliderCaptchaService.class.getDeclaredField("tokenCache");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Integer> cache = (Map<String, Integer>) field.get(service);
            Integer targetX = cache.get(token);
            assertNotNull(targetX, "token 应存在于缓存中");
            return service.verify(token, targetX);
        } catch (Exception e) {
            fail("反射读取 tokenCache 失败: " + e.getMessage());
            return null;
        }
    }
}
