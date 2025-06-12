package com.campus.shared.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.campus.shared.util.JwtUtil;

/**
 * JWT提供者 - 重构后的统一接口
 * 
 * 注意：此类已被重构为 JwtUtil 的代理类，以保持向后兼容性
 * 新代码应直接使用 JwtUtil 类
 * 
 * @author campus
 * @since 1.0.0
 * @deprecated 请使用 {@link JwtUtil} 替代此类
 */
@Component
@Deprecated
public class JwtProvider {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 生成访问令牌
     * @deprecated 请使用 {@link JwtUtil#generateToken(Long, String, String)}
     */
    @Deprecated
    public String generateToken(Long userId, String username, String userType) {
        return jwtUtil.generateToken(userId, username, userType);
    }
    
    /**
     * 生成刷新令牌
     * @deprecated 请使用 {@link JwtUtil#generateRefreshToken(String)}
     */
    @Deprecated
    public String generateRefreshToken(Long userId, String username) {
        return jwtUtil.generateRefreshToken(username);
    }
    
    /**
     * 从Token中获取用户名
     * @deprecated 请使用 {@link JwtUtil#getUsernameFromToken(String)}
     */
    @Deprecated
    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }
    
    /**
     * 从Token中获取用户ID
     * @deprecated 请使用 {@link JwtUtil#getUserIdFromToken(String)}
     */
    @Deprecated
    public Long getUserIdFromToken(String token) {
        return jwtUtil.getUserIdFromToken(token);
    }
    
    /**
     * 从Token中获取用户类型
     * @deprecated 请使用 {@link JwtUtil#getRoleFromToken(String)}
     */
    @Deprecated
    public String getUserTypeFromToken(String token) {
        return jwtUtil.getRoleFromToken(token);
    }
    
    /**
     * 验证Token是否有效
     * @deprecated 请使用 {@link JwtUtil#validateToken(String)}
     */
    @Deprecated
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
    
    /**
     * 检查Token是否过期
     * @deprecated 请使用 {@link JwtUtil#getTokenRemainingTime(String)} 判断
     */
    @Deprecated
    public boolean isTokenExpired(String token) {
        Long remainingTime = jwtUtil.getTokenRemainingTime(token);
        return remainingTime == null || remainingTime <= 0;
    }
    
    /**
     * 刷新Token
     * @deprecated 请使用 {@link JwtUtil#refreshToken(String)}
     */
    @Deprecated
    public String refreshToken(String token) {
        return jwtUtil.refreshToken(token);
    }
    
    /**
     * 获取Token剩余有效时间（秒）
     * @deprecated 请使用 {@link JwtUtil#getExpirationTime()}
     */
    @Deprecated
    public long getExpirationTime() {
        return jwtUtil.getExpirationTime() / 1000; // 转换为秒
    }
    
    /**
     * 获取刷新Token剩余有效时间（秒）
     * @deprecated 请使用 JwtUtil 的相关方法
     */
    @Deprecated
    public long getRefreshExpirationTime() {
        // 返回刷新token的过期时间（7天转换为秒）
        return 604800L;
    }
}