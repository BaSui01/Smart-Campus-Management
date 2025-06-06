package com.campus.shared.config;

import java.util.Arrays;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security 配置类
 * 负责系统的安全配置，包括认证、授权、密码加密等
 *
 * @author campus
 * @since 2025-06-04
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

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

            // 配置CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 配置会话管理为有状态（传统session方式）
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

            // 配置请求授权
            .authorizeHttpRequests(authz -> authz
                // 公开访问的端点 - 首页和静态资源
                .requestMatchers(
                    "/",                    // 首页
                    "/index",               // 首页别名
                    "/home",                // 首页别名
                    "/about",               // 关于我们
                    "/contact",             // 联系我们
                    "/help",                // 帮助页面
                    "/privacy",             // 隐私政策
                    "/terms",               // 服务条款
                    "/admin/login",         // 登录页面
                    "/admin/captcha",       // 验证码接口
                    "/admin/check-login",   // 登录状态检查
                    "/static/**",           // 静态资源
                    "/css/**",              // CSS文件
                    "/js/**",               // JavaScript文件
                    "/images/**",           // 图片文件
                    "/fonts/**",            // 字体文件
                    "/favicon.ico",         // 网站图标
                    "/error"                // 错误页面
                ).permitAll()

                // API文档完全开放，不需要认证
                .requestMatchers(
                    "/swagger-ui/**",       // Swagger UI
                    "/swagger-ui.html",     // Swagger UI首页
                    "/v3/api-docs/**",      // OpenAPI 3.0文档
                    "/v2/api-docs/**",      // OpenAPI 2.0文档
                    "/swagger-resources/**", // Swagger资源
                    "/webjars/**",          // Web JAR资源
                    "/doc.html",            // Knife4j文档
                    "/favicon.ico"          // 图标
                ).permitAll()

                // API接口需要认证
                .requestMatchers("/api/**").authenticated()

                // 管理后台使用自定义拦截器认证，这里允许访问
                .requestMatchers("/admin/**").permitAll()

                // 其他所有请求都允许访问
                .anyRequest().permitAll()
            )

            // 配置登录页面
            .formLogin(form -> form
                .loginPage("/admin/login")
                .permitAll()
                .disable() // 禁用默认表单登录，使用自定义登录
            )

            // 配置登出
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // 配置异常处理
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/admin/access-denied")
                .authenticationEntryPoint((request, response, authException) -> {
                    String requestURI = request.getRequestURI();
                    // 如果是管理后台页面，重定向到登录页面
                    if (requestURI.startsWith("/admin/")) {
                        response.sendRedirect("/admin/login");
                    } else {
                        // 其他页面重定向到首页
                        response.sendRedirect("/");
                    }
                })
            );

        return http.build();
    }

    /**
     * CORS配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 允许的域名
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // 允许发送凭证
        configuration.setAllowCredentials(true);

        // 预检请求的缓存时间
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
