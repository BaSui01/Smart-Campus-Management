package com.campus.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;

/**
 * JPA配置类
 * 配置JPA审计、事务管理等功能
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableJpaRepositories(basePackages = "com.campus.domain.repository")
@EnableTransactionManagement
public class JpaConfig {

    /**
     * 审计提供者
     * 自动填充创建人和修改人字段
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new SpringSecurityAuditorAware();
    }

    /**
     * Spring Security审计感知实现
     */
    public static class SpringSecurityAuditorAware implements AuditorAware<String> {

        @Override
        public Optional<String> getCurrentAuditor() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("system");
            }

            String username = authentication.getName();
            if ("anonymousUser".equals(username)) {
                return Optional.of("system");
            }

            return Optional.of(username);
        }
    }
}