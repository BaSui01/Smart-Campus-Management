package com.campus.config;

import com.campus.interceptor.AdminAuthInterceptor;
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
    private AdminAuthInterceptor adminAuthInterceptor;
    
    @Override
    public void addInterceptors(@org.springframework.lang.NonNull InterceptorRegistry registry) {
        // 注册管理后台权限拦截器
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                    "/admin/login",
                    "/admin/logout",
                    "/admin/captcha",
                    "/admin/test",
                    "/admin/check-login",
                    "/admin/access-denied",
                    "/admin/session-timeout"
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
    }
    
    @Override
    public void addViewControllers(@org.springframework.lang.NonNull ViewControllerRegistry registry) {
        // 简单的视图控制器
        registry.addViewController("/admin").setViewName("redirect:/admin/dashboard");
    }
}
