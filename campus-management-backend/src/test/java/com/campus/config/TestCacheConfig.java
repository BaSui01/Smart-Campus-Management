package com.campus.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.mockito.Mockito;

/**
 * 测试环境缓存配置
 * 
 * @author Campus Management Team
 * @since 2025-06-10
 */
@TestConfiguration
@EnableCaching
@Profile("test")
public class TestCacheConfig {

    /**
     * 测试环境使用简单的内存缓存管理器
     */
    @Bean("testCacheManager")
    @Primary
    public CacheManager testCacheManager() {
        return new ConcurrentMapCacheManager(
            "students",
            "courses",
            "users",
            "classes",
            "permissions",
            "roles",
            "system-config"
        );
    }

    /**
     * 测试环境Redis连接工厂Mock
     * 避免Redis依赖问题
     */
    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        return Mockito.mock(RedisConnectionFactory.class);
    }

    /**
     * 测试环境邮件发送器Mock
     * 避免邮件服务依赖问题
     */
    @Bean
    @Primary
    public JavaMailSender javaMailSender() {
        return Mockito.mock(JavaMailSender.class);
    }

    /**
     * 测试环境JWT工具类
     */
    @Bean
    @Primary
    public TestJwtUtil testJwtUtil() {
        return new TestJwtUtil();
    }
}
