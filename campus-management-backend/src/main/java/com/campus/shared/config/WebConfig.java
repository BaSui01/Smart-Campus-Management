package com.campus.shared.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import com.campus.shared.security.UnifiedAdminInterceptor;

/**
 * Web配置类
 * 配置拦截器、静态资源处理、CORS等
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    @Lazy
    private UnifiedAdminInterceptor unifiedAdminInterceptor;

    @Override
    public void addInterceptors(@org.springframework.lang.NonNull InterceptorRegistry registry) {
        // 注册统一管理后台认证拦截器
        registry.addInterceptor(unifiedAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                    "/admin/login",
                    "/admin/logout",
                    "/admin/captcha",
                    "/admin/test",
                    "/admin/check-login",
                    "/admin/access-denied",
                    "/admin/session-timeout",
                    "/admin/refresh-token",
                    "/admin/token-status"
                );
    }

    @Override
    public void addResourceHandlers(@org.springframework.lang.NonNull ResourceHandlerRegistry registry) {
        // 静态资源处理
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");

        // Swagger UI 静态资源处理
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/")
                .resourceChain(false);

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .resourceChain(false);

        // API文档资源
        registry.addResourceHandler("/v3/api-docs/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .resourceChain(false);
    }

    @Override
    public void addViewControllers(@org.springframework.lang.NonNull ViewControllerRegistry registry) {
        // 简单的视图控制器
        registry.addViewController("/admin").setViewName("redirect:/admin/dashboard");

        // API文档重定向
        registry.addViewController("/api").setViewName("redirect:/swagger-ui/index.html");
        registry.addViewController("/docs").setViewName("redirect:/swagger-ui/index.html");
        registry.addViewController("/api-docs").setViewName("redirect:/swagger-ui/index.html");
    }

    /**
     * 统一的CORS配置源
     * 供SecurityConfig和其他需要CORS配置的地方使用
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

    /**
     * 配置favicon映射，避免404错误
     * 将 /favicon.ico 请求重定向到 /favicon.svg
     * 从WebResourceConfig合并过来的功能
     */
    @Bean
    public SimpleUrlHandlerMapping faviconHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Integer.MIN_VALUE);
        // 同时处理 .ico 和 .svg 请求
        mapping.setUrlMap(Collections.singletonMap("/favicon.ico", faviconRequestHandler()));
        return mapping;
    }

    /**
     * 创建favicon请求处理器
     * 从WebResourceConfig合并过来的功能
     */
    @Bean
    protected ResourceHttpRequestHandler faviconRequestHandler() {
        ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();
        ClassPathResource classPathResource = new ClassPathResource("static/");
        requestHandler.setLocations(Arrays.asList(classPathResource));
        return requestHandler;
    }
}
