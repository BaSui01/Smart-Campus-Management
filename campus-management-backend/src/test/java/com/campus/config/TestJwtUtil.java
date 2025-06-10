package com.campus.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Primary;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试环境JWT工具类
 * 简化JWT生成，便于测试
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-27
 */
@TestComponent
@Primary
public class TestJwtUtil {

    private static final String TEST_SECRET = "test-secret-key-for-jwt-token-generation-in-test-environment-must-be-long-enough";
    private static final long TEST_EXPIRATION = 3600000; // 1小时
    private final SecretKey secretKey;

    public TestJwtUtil() {
        this.secretKey = Keys.hmacShaKeyFor(TEST_SECRET.getBytes());
    }

    /**
     * 生成测试JWT令牌
     */
    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        
        return createToken(claims, username);
    }

    /**
     * 创建JWT令牌
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + TEST_EXPIRATION);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 验证令牌
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从令牌中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
