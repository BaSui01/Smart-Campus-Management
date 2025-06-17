package com.example.smartcampusmanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 修改静态资源路径映射，确保CSS、JS等文件被正确加载
        registry.addResourceHandler("/css/**", "/js/**", "/images/**", "/favicon.ico")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600); // 设置缓存时间（可选）

        // 添加对根路径的静态资源支持（如favicon.ico）
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}
