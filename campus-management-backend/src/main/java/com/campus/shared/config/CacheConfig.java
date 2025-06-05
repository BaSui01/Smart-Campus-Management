package com.campus.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis 缓存配置类
 * 
 * @author Campus Team
 * @since 2025-06-05
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${campus.cache.course-ttl:3600}")
    private int courseTtl;

    @Value("${campus.cache.permission-ttl:1800}")
    private int permissionTtl;

    @Value("${campus.cache.config-ttl:86400}")
    private int configTtl;

    @Value("${campus.cache.student-ttl:7200}")
    private int studentTtl;

    @Value("${campus.cache.class-ttl:14400}")
    private int classTtl;

    /**
     * Redis 模板配置
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 设置序列化器
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        // Key 序列化
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Value 序列化
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 缓存管理器配置
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        // 默认缓存配置
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))  // 默认30分钟过期
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()))
            .disableCachingNullValues();

        // 不同缓存的配置
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 课程信息缓存
        cacheConfigurations.put("courses", defaultConfig.entryTtl(Duration.ofSeconds(courseTtl)));

        // 用户权限缓存
        cacheConfigurations.put("permissions", defaultConfig.entryTtl(Duration.ofSeconds(permissionTtl)));

        // 系统配置缓存
        cacheConfigurations.put("system-config", defaultConfig.entryTtl(Duration.ofSeconds(configTtl)));

        // 学生信息缓存
        cacheConfigurations.put("students", defaultConfig.entryTtl(Duration.ofSeconds(studentTtl)));

        // 班级信息缓存
        cacheConfigurations.put("classes", defaultConfig.entryTtl(Duration.ofSeconds(classTtl)));

        // 实时数据缓存 - 5分钟
        cacheConfigurations.put("realtime", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        // 统计数据缓存 - 15分钟
        cacheConfigurations.put("statistics", defaultConfig.entryTtl(Duration.ofMinutes(15)));

        // 热点数据缓存 - 1小时
        cacheConfigurations.put("hotdata", defaultConfig.entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(factory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }
}
