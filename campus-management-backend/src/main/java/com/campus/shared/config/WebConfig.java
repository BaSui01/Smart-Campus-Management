package com.campus.shared.config;

import com.campus.shared.security.AdminJwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 配置拦截器、静态资源处理等
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminJwtInterceptor adminJwtInterceptor;

    @Override
    public void addInterceptors(@org.springframework.lang.NonNull InterceptorRegistry registry) {
        // 注册管理后台JWT认证拦截器
        registry.addInterceptor(adminJwtInterceptor)
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
}
