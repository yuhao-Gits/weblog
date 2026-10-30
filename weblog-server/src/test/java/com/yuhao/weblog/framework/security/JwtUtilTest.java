package com.yuhao.weblog.framework.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil 单元测试：令牌生成、解析、过期、校验
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // 通过反射注入私有字段（避免依赖 Spring 上下文）
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "unit-test-secret-key-at-least-32-bytes-long!!");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    @DisplayName("生成的 token 能解析出正确的用户名与角色")
    void generateAndParse() {
        String token = jwtUtil.generateToken("admin", "ROLE_ADMIN");

        assertNotNull(token);
        assertEquals("admin", jwtUtil.getUsernameFromToken(token));
        assertEquals("ROLE_ADMIN", jwtUtil.getRoleFromToken(token));
    }

    @Test
    @DisplayName("token 未过期时校验通过")
    void validateWhenNotExpired() {
        String token = jwtUtil.generateToken("admin", "ROLE_ADMIN");
        UserDetails userDetails = new User("admin", "pwd", new ArrayList<>());

        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    @DisplayName("token 用户名不匹配时校验失败")
    void validateWhenUsernameMismatch() {
        String token = jwtUtil.generateToken("admin", "ROLE_ADMIN");
        UserDetails userDetails = new User("other", "pwd", new ArrayList<>());

        assertFalse(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    @DisplayName("已过期 token 校验失败")
    void validateWhenExpired() throws InterruptedException {
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1000L); // 立即过期
        String token = jwtUtil.generateToken("admin", "ROLE_ADMIN");
        UserDetails userDetails = new User("admin", "pwd", new ArrayList<>());

        assertFalse(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    @DisplayName("篡改的 token 解析抛出异常")
    void parseTamperedTokenThrows() {
        String token = jwtUtil.generateToken("admin", "ROLE_ADMIN");
        String tampered = token.substring(0, token.length() - 2) + "xx";

        assertThrows(Exception.class, () -> jwtUtil.parseToken(tampered));
    }

    @Test
    @DisplayName("解析结果包含签发时间与过期时间")
    void parseContainsClaims() {
        String token = jwtUtil.generateToken("admin", "ROLE_ADMIN");
        Claims claims = jwtUtil.parseToken(token);

        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }
}
