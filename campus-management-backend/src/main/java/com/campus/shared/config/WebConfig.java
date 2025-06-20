package com.campus.shared.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

/**
 * Web配置类
 * 配置拦截器、静态资源处理、CORS等
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Web拦截器已移除 - 纯API服务不需要Web拦截器

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
        // API文档重定向 - 保留用于API文档访问
        registry.addViewController("/api").setViewName("redirect:/swagger-ui/index.html");
        registry.addViewController("/docs").setViewName("redirect:/swagger-ui/index.html");
        registry.addViewController("/api-docs").setViewName("redirect:/swagger-ui/index.html");
    }

    /**
     * 前后端分离CORS配置
     * 支持前端应用跨域访问API
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 允许的前端域名 - 开发和生产环境
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",    // Vue.js开发服务器
            "http://localhost:8080",    // 备用前端端口
            "http://localhost:5173",    // Vite开发服务器
            "https://*.yourdomain.com", // 生产环境域名
            "*"                         // 开发阶段允许所有域名
        ));

        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With",
            "Accept", "Origin", "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));

        // 暴露的响应头
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"
        ));

        // 允许发送凭证（Cookie、Authorization header等）
        configuration.setAllowCredentials(true);

        // 预检请求的缓存时间（1小时）
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        source.registerCorsConfiguration("/swagger-ui/**", configuration);
        source.registerCorsConfiguration("/v3/api-docs/**", configuration);

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
