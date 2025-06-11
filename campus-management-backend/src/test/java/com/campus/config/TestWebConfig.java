package com.campus.config;

import com.campus.shared.security.AdminJwtInterceptor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 测试环境Web配置
 * 禁用JWT拦截器，便于API测试
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-08
 */
@TestConfiguration
@Profile("test")
public class TestWebConfig implements WebMvcConfigurer {

    /**
     * 测试环境下的空拦截器，替代AdminJwtInterceptor
     */
    @Bean
    @Primary
    public AdminJwtInterceptor testAdminJwtInterceptor() {
        return new AdminJwtInterceptor() {
            @Override
            public boolean preHandle(@NonNull HttpServletRequest request,
                                   @NonNull HttpServletResponse response,
                                   @NonNull Object handler) throws Exception {
                // 测试环境下直接放行所有请求
                return true;
            }
        };
    }

    /**
     * 测试环境Web配置器
     * 使用空的拦截器注册
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // 测试环境不添加任何拦截器
        // 或者添加一个空的拦截器来覆盖主配置
    }
}
