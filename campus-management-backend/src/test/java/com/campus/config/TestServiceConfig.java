package com.campus.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import static org.mockito.Mockito.mock;

/**
 * 测试Service配置类
 * 提供测试环境所需的Service依赖Bean配置
 * 
 * @author Campus Management Team
 * @since 2025-06-10
 */
@TestConfiguration
public class TestServiceConfig {

    /**
     * 为测试环境提供JavaMailSender的Mock实现
     * 避免邮件服务依赖问题
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean
    public JavaMailSender javaMailSender() {
        return mock(JavaMailSender.class);
    }
}
