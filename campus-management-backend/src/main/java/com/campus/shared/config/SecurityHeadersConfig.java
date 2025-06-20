package com.campus.shared.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 安全响应头配置
 * 添加各种安全相关的HTTP响应头
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-18
 */
@Configuration
public class SecurityHeadersConfig {

    /**
     * 安全响应头过滤器
     * 为所有响应添加安全相关的HTTP头
     */
    @Bean
    public OncePerRequestFilter securityHeadersFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, 
                                          HttpServletResponse response, 
                                          FilterChain filterChain) throws ServletException, IOException {
                
                // HSTS - 强制HTTPS
                response.setHeader("Strict-Transport-Security", 
                    "max-age=31536000; includeSubDomains; preload");
                
                // 防止点击劫持
                response.setHeader("X-Frame-Options", "DENY");
                
                // 防止MIME类型嗅探
                response.setHeader("X-Content-Type-Options", "nosniff");
                
                // XSS保护
                response.setHeader("X-XSS-Protection", "1; mode=block");
                
                // 引用策略
                response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
                
                // 内容安全策略
                response.setHeader("Content-Security-Policy", 
                    "default-src 'self'; " +
                    "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                    "style-src 'self' 'unsafe-inline'; " +
                    "img-src 'self' data: https:; " +
                    "font-src 'self' https:; " +
                    "connect-src 'self' https:; " +
                    "frame-ancestors 'none'; " +
                    "base-uri 'self'; " +
                    "form-action 'self'");
                
                // 权限策略
                response.setHeader("Permissions-Policy", 
                    "camera=(), microphone=(), geolocation=(), payment=()");
                
                // 服务器信息隐藏
                response.setHeader("Server", "Campus-Management-Server");
                
                // 缓存控制（敏感页面）
                if (request.getRequestURI().contains("/api/")) {
                    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                    response.setHeader("Pragma", "no-cache");
                    response.setHeader("Expires", "0");
                }
                
                filterChain.doFilter(request, response);
            }
        };
    }
}
