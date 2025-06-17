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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

import com.campus.shared.security.JwtAuthenticationFilter;
import com.campus.shared.security.SecurityConstants;

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

            // 配置请求授权
            .authorizeHttpRequests(authz -> authz
                // API接口完全开放，不需要Spring Security认证
                .requestMatchers(new AntPathRequestMatcher(SecurityConstants.API_PATH_PATTERN)).permitAll()

                // 公开访问的端点 - 首页和静态资源
                .requestMatchers(SecurityConstants.getPublicEndpoints()).permitAll()

                // API文档完全开放，不需要认证
                .requestMatchers(SecurityConstants.getApiDocEndpoints()).permitAll()

                // 管理后台使用自定义拦截器认证，这里允许访问
                .requestMatchers(new AntPathRequestMatcher(SecurityConstants.ADMIN_PATH_PATTERN)).permitAll()

                // 其他所有请求都允许访问
                .anyRequest().permitAll()
            )

            // 完全禁用表单登录
            .formLogin(AbstractHttpConfigurer::disable)

            // 禁用HTTP Basic认证
            .httpBasic(AbstractHttpConfigurer::disable)

            // 配置登出
            .logout(logout -> logout
                .logoutUrl(SecurityConstants.LOGOUT_URL)
                .logoutSuccessUrl(SecurityConstants.LOGOUT_SUCCESS_URL)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // 配置异常处理 - 对于API请求返回JSON错误
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedPage(SecurityConstants.ACCESS_DENIED_PAGE)
                .authenticationEntryPoint((request, response, authException) -> {
                    @SuppressWarnings("unused") // authException parameter is required by interface
                    var unused = authException;
                    String requestURI = request.getRequestURI();

                    // 对于API请求，返回JSON错误
                    if (requestURI.startsWith(SecurityConstants.API_PREFIX)) {
                        response.setStatus(401);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write("{\"code\":401,\"message\":\"未授权访问\",\"data\":null}");
                        return;
                    }

                    // 如果是管理后台页面，重定向到登录页面
                    if (requestURI.startsWith(SecurityConstants.ADMIN_PREFIX)) {
                        response.sendRedirect(SecurityConstants.LOGIN_PAGE);
                    } else {
                        // 其他页面重定向到首页
                        response.sendRedirect(SecurityConstants.HOME_PAGE);
                    }
                })
            );

        return http.build();
    }
}
