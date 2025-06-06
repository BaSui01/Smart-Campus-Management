package com.campus.shared.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson JSON序列化配置
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@Configuration
public class JacksonConfig {

    /**
     * 配置ObjectMapper
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // 注册Hibernate6模块，处理懒加载代理对象
        Hibernate6Module hibernate6Module = new Hibernate6Module();
        // 禁用强制懒加载，让未初始化的代理对象序列化为null
        hibernate6Module.disable(Hibernate6Module.Feature.USE_TRANSIENT_ANNOTATION);
        hibernate6Module.disable(Hibernate6Module.Feature.FORCE_LAZY_LOADING);
        mapper.registerModule(hibernate6Module);
        
        // 注册Java时间模块
        mapper.registerModule(new JavaTimeModule());
        
        // 禁用将日期序列化为时间戳
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // 禁用空Bean序列化失败
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        
        return mapper;
    }
}
