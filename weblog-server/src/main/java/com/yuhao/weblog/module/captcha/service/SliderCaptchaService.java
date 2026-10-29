package com.yuhao.weblog.module.captcha.service;

import com.yuhao.weblog.common.BizException;
import com.yuhao.weblog.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 滑块验证码服务（自实现，纯 Java AWT，无外部依赖）
 *
 * 生成一张带缺口的拼图背景，前端拖拽滑块到缺口位置，
 * 后端根据 token 校验滑动距离是否在容差范围内。
 */
@Slf4j
@Service
public class SliderCaptchaService {

    private static final int WIDTH = 320;
    private static final int HEIGHT = 160;
    /** 缺口大小 */
    private static final int PUZZLE = 48;
    /** 容差（像素） */
    private static final int TOLERANCE = 6;

    private final SecureRandom random = new SecureRandom();
    /** token -> 缺口 x 坐标 */
    private final Map<String, Integer> tokenCache = new ConcurrentHashMap<>();
    /** 验证通过凭证（滑块校验成功后颁发，登录时一次性消费） */
    private final Map<String, Long> passCache = new ConcurrentHashMap<>();

    /**
     * 生成滑块验证码
     * 返回：{ token, backgroundImage, puzzleImage, y } 其中 x 为缺口坐标（仅服务端校验用）
     */
    public Map<String, Object> generate() {
        // 缺口 x 范围：[PUZZLE, WIDTH - PUZZLE]，与前端滑块可移动范围一致
        int x = PUZZLE + random.nextInt(WIDTH - PUZZLE * 2 + 1);
        int y = random.nextInt(HEIGHT - PUZZLE);

        BufferedImage bg = createBackground();
        // 抠出缺口
        cutHole(bg, x, y);
        // 生成滑块小图
        BufferedImage puzzle = createPuzzle(bg, x, y);

        String token = UUID.randomUUID().toString().replace("-", "");
        tokenCache.put(token, x);

        Map<String, Object> result = new java.util.HashMap<>();
        result.put("token", token);
        result.put("backgroundImage", toBase64(bg));
        result.put("puzzleImage", toBase64(puzzle));
        result.put("y", y);
        return result;
    }

    /**
     * 校验滑块位置（失败保留 token 允许重试；成功后移除 token 并颁发通过凭证 passToken）
     */
    public Result<Map<String, Object>> verify(String token, int sliderX) {
        Integer targetX = tokenCache.get(token);
        if (targetX == null) {
            throw new BizException("验证码已失效，请刷新后重试");
        }
        if (Math.abs(sliderX - targetX) <= TOLERANCE) {
            tokenCache.remove(token);
            // 颁发一次性通过凭证，登录时消费
            String passToken = UUID.randomUUID().toString().replace("-", "");
            passCache.put(passToken, System.currentTimeMillis());
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("passToken", passToken);
            return Result.success(data);
        }
        throw new BizException("验证未通过，请重试");
    }

    /**
     * 消费验证通过凭证（登录时调用，一次性，5 分钟有效）
     */
    public void consumePassToken(String passToken) {
        if (passToken == null || passToken.trim().isEmpty()) {
            throw new BizException("请先完成滑块验证");
        }
        Long ts = passCache.remove(passToken);
        if (ts == null) {
            throw new BizException("验证码已失效，请重新验证");
        }
        if (System.currentTimeMillis() - ts > 5 * 60 * 1000) {
            throw new BizException("验证已超时，请重新验证");
        }
    }

    /**
     * 生成随机背景图（渐变 + 干扰线）
     */
    private BufferedImage createBackground() {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 随机渐变背景
        Color c1 = randomColor();
        Color c2 = randomColor();
        g.setPaint(new GradientPaint(0, 0, c1, WIDTH, HEIGHT, c2));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        // 干扰线
        g.setColor(new Color(255, 255, 255, 80));
        for (int i = 0; i < 8; i++) {
            g.drawLine(random.nextInt(WIDTH), random.nextInt(HEIGHT),
                    random.nextInt(WIDTH), random.nextInt(HEIGHT));
        }
        // 干扰点
        g.setColor(new Color(255, 255, 255, 100));
        for (int i = 0; i < 40; i++) {
            g.fillOval(random.nextInt(WIDTH), random.nextInt(HEIGHT), 2, 2);
        }
        g.dispose();
        return img;
    }

    /**
     * 在背景图上抠出缺口（绘制缺口形状）
     */
    private void cutHole(BufferedImage bg, int x, int y) {
        Graphics2D g = bg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 用稍暗颜色填充缺口，形成视觉差异
        g.setColor(new Color(0, 0, 0, 70));
        g.fillRoundRect(x, y, PUZZLE, PUZZLE, 12, 12);
        // 缺口边框
        g.setStroke(new BasicStroke(2f));
        g.setColor(new Color(255, 255, 255, 150));
        g.drawRoundRect(x, y, PUZZLE, PUZZLE, 12, 12);
        g.dispose();
    }

    /**
     * 从背景图截取滑块（缺口位置对应的图块）
     */
    private BufferedImage createPuzzle(BufferedImage bg, int x, int y) {
        BufferedImage puzzle = bg.getSubimage(x, y, PUZZLE, PUZZLE);
        BufferedImage result = new BufferedImage(PUZZLE, PUZZLE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 圆角裁剪
        g.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, PUZZLE, PUZZLE, 12, 12));
        g.drawImage(puzzle, 0, 0, null);
        g.setClip(null);
        // 边框
        g.setColor(new Color(255, 255, 255, 200));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(0, 0, PUZZLE - 1, PUZZLE - 1, 12, 12);
        g.dispose();
        return result;
    }

    private String toBase64(BufferedImage img) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", baos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new BizException("验证码生成失败");
        }
    }

    private Color randomColor() {
        return new Color(random.nextInt(180), random.nextInt(180), random.nextInt(200));
    }
}
