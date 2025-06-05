package com.campus.shared.security;

import com.campus.shared.exception.JwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一JWT提供者
 * 
 * @author campus
 * @since 1.0.0
 */
@Component
public class JwtProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);
    
    @Value("${app.jwt.secret:campusManagementSecretKey2024}")
    private String jwtSecret;
    
    @Value("${app.jwt.expiration:86400}")
    private long jwtExpiration; // 24小时，单位：秒
    
    @Value("${app.jwt.refresh-expiration:604800}")
    private long refreshExpiration; // 7天，单位：秒
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    /**
     * 生成访问令牌
     */
    public String generateToken(Long userId, String username, String userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("userType", userType);
        return createToken(claims, username, jwtExpiration * 1000);
    }
    
    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("type", "refresh");
        return createToken(claims, username, refreshExpiration * 1000);
    }
    
    /**
     * 创建Token
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
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
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            logger.error("获取用户名失败", e);
            throw new JwtException("Token无效");
        }
    }
    
    /**
     * 从Token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Object userId = claims.get("userId");
            if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            }
            return (Long) userId;
        } catch (Exception e) {
            logger.error("获取用户ID失败", e);
            throw new JwtException("Token无效");
        }
    }
    
    /**
     * 从Token中获取用户类型
     */
    public String getUserTypeFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return (String) claims.get("userType");
        } catch (Exception e) {
            logger.error("获取用户类型失败", e);
            return null;
        }
    }
    
    /**
     * 获取Token的过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration();
        } catch (Exception e) {
            logger.error("获取过期时间失败", e);
            throw new JwtException("Token无效");
        }
    }
    
    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (JwtException e) {
            logger.error("Token验证失败: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Token验证异常", e);
            return false;
        }
    }
    
    /**
     * 检查Token是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
    
    /**
     * 刷新Token
     */
    public String refreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            String username = claims.getSubject();
            Long userId = getUserIdFromToken(token);
            String userType = getUserTypeFromToken(token);
            
            return generateToken(userId, username, userType);
        } catch (Exception e) {
            logger.error("刷新Token失败", e);
            throw new JwtException("Token刷新失败");
        }
    }
    
    /**
     * 从Token中解析Claims
     */
    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.warn("Token已过期: {}", e.getMessage());
            throw new JwtException("Token已过期");
        } catch (UnsupportedJwtException e) {
            logger.error("不支持的Token: {}", e.getMessage());
            throw new JwtException("不支持的Token");
        } catch (MalformedJwtException e) {
            logger.error("Token格式错误: {}", e.getMessage());
            throw new JwtException("Token格式错误");
        } catch (SecurityException e) {
            logger.error("Token签名无效: {}", e.getMessage());
            throw new JwtException("Token签名无效");
        } catch (IllegalArgumentException e) {
            logger.error("Token参数无效: {}", e.getMessage());
            throw new JwtException("Token参数无效");
        }
    }
    
    /**
     * 获取Token剩余有效时间（秒）
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }
    
    /**
     * 获取刷新Token剩余有效时间（秒）
     */
    public long getRefreshExpirationTime() {
        return refreshExpiration;
    }
    
    /**
     * 从Token中获取所有Claims
     */
    public Map<String, Object> getAllClaimsFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return new HashMap<>(claims);
        } catch (Exception e) {
            logger.error("获取Claims失败", e);
            throw new JwtException("Token无效");
        }
    }
}