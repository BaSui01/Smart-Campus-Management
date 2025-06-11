package com.campus.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;

/**
 * 测试环境JPA配置
 * 简化JPA配置，便于测试执行
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-08
 */
@TestConfiguration
@EnableTransactionManagement
public class TestJpaConfig {

    /**
     * 测试环境审计提供者
     * 返回固定的测试用户
     * 注意：不启用JPA审计以避免与主配置冲突
     */
    @Bean("testAuditorProvider")
    @Primary
    public AuditorAware<String> testAuditorProvider() {
        return () -> Optional.of("test-user");
    }
}
