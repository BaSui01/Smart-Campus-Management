package com.campus.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * JWT工具类
 * 用于生成、解析和验证JWT令牌
 *
 * @author campus
 * @since 2025-06-04
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:campus-management-jwt-secret-key-2024}")
    private String jwtSecret;

    @Value("${jwt.expiration:7200000}")
    private Long jwtExpiration;

    @Value("${jwt.refresh-expiration:604800000}")
    private Long jwtRefreshExpiration;

    @Value("${jwt.header:Authorization}")
    private String jwtHeader;

    @Value("${jwt.prefix:Bearer}")
    private String jwtPrefix;

    // 管理后台专用配置
    @Value("${jwt.admin.expiration:14400000}")
    private Long adminExpiration;

    @Value("${jwt.admin.auto-refresh:true}")
    private Boolean adminAutoRefresh;

    @Value("${jwt.admin.refresh-threshold:1800000}")
    private Long adminRefreshThreshold;

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 从token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }



    /**
     * 从token中获取到期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从token中获取指定claim
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从token中获取所有claims
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 检查token是否过期
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 生成token
     */
    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        return createToken(claims, username);
    }

    /**
     * 生成管理后台专用token
     */
    public String generateAdminToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("type", "admin");
        return createAdminToken(claims, username);
    }

    /**
     * 生成refresh token
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createRefreshToken(claims, username);
    }

    /**
     * 创建token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 创建管理后台token
     */
    private String createAdminToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + adminExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 创建refresh token
     */
    private String createRefreshToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 验证token
     */
    public Boolean validateToken(String token, String username) {
        try {
            final String tokenUsername = getUsernameFromToken(token);
            return (username.equals(tokenUsername) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证token（不验证用户名）
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证token格式
     */
    public Boolean isValidTokenFormat(String token) {
        try {
            getAllClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从请求头中提取token
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith(jwtPrefix + " ")) {
            return authHeader.substring(7); // 移除 "Bearer " 前缀
        }
        return null;
    }

    /**
     * 刷新token
     */
    public String refreshToken(String token) {
        try {
            final Claims claims = getAllClaimsFromToken(token);
            String username = claims.getSubject();
            Long userId = Long.valueOf(claims.get("userId").toString());
            String role = claims.get("role", String.class);
            String type = claims.get("type", String.class);

            // 根据token类型选择生成方法
            if ("admin".equals(type)) {
                return generateAdminToken(userId, username, role);
            } else {
                return generateToken(userId, username, role);
            }
        } catch (Exception e) {
            throw new RuntimeException("无法刷新token", e);
        }
    }

    /**
     * 检查管理后台token是否需要刷新
     */
    public boolean shouldRefreshAdminToken(String token) {
        if (!adminAutoRefresh) {
            return false;
        }

        try {
            Date expiration = getExpirationDateFromToken(token);
            long timeUntilExpiration = expiration.getTime() - System.currentTimeMillis();
            return timeUntilExpiration <= adminRefreshThreshold;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查是否为管理后台token
     */
    public boolean isAdminToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return "admin".equals(claims.get("type", String.class));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return Long.valueOf(claims.get("userId").toString());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从token中获取用户角色
     */
    public String getRoleFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.get("role", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取token剩余有效时间（秒）
     */
    public Long getTokenRemainingTime(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            Date now = new Date();
            return (expiration.getTime() - now.getTime()) / 1000;
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 检查token是否即将过期（30分钟内）
     */
    public Boolean isTokenNearExpiry(String token) {
        Long remainingTime = getTokenRemainingTime(token);
        return remainingTime != null && remainingTime < 1800; // 30分钟 = 1800秒
    }

    /**
     * 获取JWT配置信息
     */
    public Map<String, Object> getJwtConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("header", jwtHeader);
        config.put("prefix", jwtPrefix);
        config.put("expiration", jwtExpiration);
        config.put("refreshExpiration", jwtRefreshExpiration);
        return config;
    }
}
