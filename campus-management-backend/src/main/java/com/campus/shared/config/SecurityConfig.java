package com.campus.shared.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import com.campus.shared.security.JwtAuthenticationFilter;


/**
 * Spring Security 配置类 - 纯API模式
 * 负责系统的安全配置，完全前后端分离架构
 *
 * 重构说明：
 * - 移除所有页面相关配置
 * - 纯JWT认证机制
 * - 统一API响应格式
 * - 无状态会话管理
 *
 * @author Campus Management Team
 * @version 2.0.0
 * @since 2025-06-17 (重构版本)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    /**
     * 密码编码器
     * 使用BCrypt加密算法
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     * 用于处理用户认证
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 安全过滤器链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（因为使用JWT）
            .csrf(AbstractHttpConfigurer::disable)

            // 配置CORS - 使用WebConfig中的配置源
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            // 配置会话管理为无状态（适合API）
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 添加JWT认证过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            // 配置安全头
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.deny())
                .contentTypeOptions(contentTypeOptions -> {})
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                )
                .addHeaderWriter((request, response) -> {
                    response.setHeader("X-Content-Type-Options", "nosniff");
                    response.setHeader("X-Frame-Options", "DENY");
                    response.setHeader("X-XSS-Protection", "1; mode=block");
                    response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
                    response.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()");
                })
            )

            // 配置请求授权 - 纯API模式
            .authorizeHttpRequests(authz -> authz
                // 公开API端点 - 认证相关
                .requestMatchers("/api/v1/auth/login", "/api/v1/auth/register").permitAll()

                // 健康检查端点
                .requestMatchers("/health/**", "/actuator/health/**").permitAll()

                // API文档端点（生产环境应禁用）
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()

                // 错误处理端点
                .requestMatchers("/error").permitAll()

                // 所有其他API请求需要认证
                .requestMatchers("/api/v1/**").authenticated()

                // 管理员API需要特定权限
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                // 拒绝所有非API请求（完全前后端分离）
                .anyRequest().denyAll()
            )

            // 完全禁用表单登录（纯API模式）
            .formLogin(AbstractHttpConfigurer::disable)

            // 禁用HTTP Basic认证
            .httpBasic(AbstractHttpConfigurer::disable)

            // 禁用默认登出（API模式下由前端处理）
            .logout(AbstractHttpConfigurer::disable)

            // 配置异常处理 - 统一返回JSON格式
            .exceptionHandling(exceptions -> exceptions
                // 认证失败处理
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":401,\"message\":\"未授权访问，请先登录\",\"data\":null,\"timestamp\":" + System.currentTimeMillis() + "}");
                })
                // 权限不足处理
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":403,\"message\":\"权限不足，拒绝访问\",\"data\":null,\"timestamp\":" + System.currentTimeMillis() + "}");
                })
            );

        return http.build();
    }
}
