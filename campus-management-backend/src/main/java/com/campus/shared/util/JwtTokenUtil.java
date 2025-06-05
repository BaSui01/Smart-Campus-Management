package com.campus.shared.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * JWT 工具类 - 支持 Redis 会话管理
 * 
 * @author Campus Team
 * @since 2025-06-05
 */
@Component
public class JwtTokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    @Value("${jwt.redis.prefix:jwt:token:}")
    private String jwtPrefix;

    @Value("${jwt.redis.refresh-prefix:jwt:refresh:}")
    private String refreshPrefix;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 生成 Token 并存储到 Redis
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
        claims.put("authorities", userDetails.getAuthorities());
        
        String token = createToken(claims, userDetails.getUsername());
        
        // 存储到 Redis
        String key = jwtPrefix + userDetails.getUsername();
        redisTemplate.opsForValue().set(key, token, expiration, TimeUnit.MILLISECONDS);
        
        // 生成刷新令牌
        String refreshToken = generateRefreshToken(userDetails.getUsername());
        String refreshKey = refreshPrefix + userDetails.getUsername();
        redisTemplate.opsForValue().set(refreshKey, refreshToken, refreshExpiration, TimeUnit.MILLISECONDS);
        
        logger.info("JWT Token 生成成功: {}", userDetails.getUsername());
        return token;
    }

    /**
     * 创建 Token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 生成刷新令牌
     */
    private String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            logger.error("从 Token 中获取用户名失败", e);
            return null;
        }
    }

    /**
     * 验证 Token 有效性
     */
    public Boolean validateToken(String token, String username) {
        try {
            // 检查 Redis 中是否存在
            String key = jwtPrefix + username;
            String cachedToken = (String) redisTemplate.opsForValue().get(key);
            
            if (cachedToken == null || !token.equals(cachedToken)) {
                logger.warn("Token 在 Redis 中不存在或不匹配: {}", username);
                return false;
            }

            // 验证 Token 本身
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("Token 已过期: {}", username);
            return false;
        } catch (Exception e) {
            logger.error("Token 验证失败: {}", username, e);
            return false;
        }
    }

    /**
     * 检查 Token 是否即将过期
     */
    public Boolean isTokenNearExpiry(String token, int minutesThreshold) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            Date expiration = claims.getExpiration();
            Date now = new Date();
            long timeLeft = expiration.getTime() - now.getTime();
            long thresholdMillis = minutesThreshold * 60 * 1000L;
            
            return timeLeft < thresholdMillis;
        } catch (Exception e) {
            logger.error("检查 Token 过期时间失败", e);
            return true;
        }
    }

    /**
     * 刷新 Token
     */
    public String refreshToken(String refreshToken, String username) {
        try {
            String refreshKey = refreshPrefix + username;
            String cachedRefreshToken = (String) redisTemplate.opsForValue().get(refreshKey);
            
            if (refreshToken.equals(cachedRefreshToken)) {
                // 验证刷新令牌
                Jwts.parserBuilder()
                        .setSigningKey(getSigningKey())
                        .build()
                        .parseClaimsJws(refreshToken);
                
                // 生成新的访问令牌
                Map<String, Object> claims = new HashMap<>();
                claims.put("username", username);
                
                String newToken = createToken(claims, username);
                
                // 更新 Redis 中的 Token
                String tokenKey = jwtPrefix + username;
                redisTemplate.opsForValue().set(tokenKey, newToken, expiration, TimeUnit.MILLISECONDS);
                
                logger.info("Token 刷新成功: {}", username);
                return newToken;
            }
            
            throw new RuntimeException("刷新令牌无效");
        } catch (Exception e) {
            logger.error("Token 刷新失败: {}", username, e);
            throw new RuntimeException("刷新令牌无效", e);
        }
    }

    /**
     * 登出时清除 Token
     */
    public void logout(String username) {
        String tokenKey = jwtPrefix + username;
        String refreshKey = refreshPrefix + username;
        
        redisTemplate.delete(tokenKey);
        redisTemplate.delete(refreshKey);
        
        logger.info("用户登出，清除 Token: {}", username);
    }

    /**
     * 获取 Token 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration();
        } catch (Exception e) {
            logger.error("获取 Token 过期时间失败", e);
            return null;
        }
    }

    /**
     * 检查 Token 是否过期
     */
    public Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration != null && expiration.before(new Date());
    }
}
