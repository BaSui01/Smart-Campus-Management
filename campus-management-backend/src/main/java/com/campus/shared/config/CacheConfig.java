package com.campus.shared.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 缓存配置类
 * 配置Redis缓存和缓存管理器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 配置缓存管理器
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 配置序列化器
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = createJacksonSerializer();

        // 默认配置缓存
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) // 默认缓存30分钟
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();

        // 针对不同的缓存设置不同的过期时间
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // 仪表盘统计数据 - 缓存5分钟，频繁更新
        cacheConfigurations.put("dashboard:stats", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("dashboard:service:stats", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        // 图表数据 - 缓存10分钟，相对稳定
        cacheConfigurations.put("dashboard:chart-data", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        cacheConfigurations.put("dashboard:charts", defaultConfig.entryTtl(Duration.ofMinutes(10)));

        // 活动数据 - 缓存3分钟，实时性要求高
        cacheConfigurations.put("dashboard:activities", defaultConfig.entryTtl(Duration.ofMinutes(3)));

        // 通知数据 - 缓存2分钟，实时性要求很高
        cacheConfigurations.put("dashboard:notifications", defaultConfig.entryTtl(Duration.ofMinutes(2)));

        // 快速统计 - 缓存1分钟，实时性要求最高
        cacheConfigurations.put("dashboard:quick-stats", defaultConfig.entryTtl(Duration.ofMinutes(1)));

        // 用户相关缓存 - 缓存15分钟，相对稳定
        cacheConfigurations.put("user:info", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigurations.put("user:permissions", defaultConfig.entryTtl(Duration.ofMinutes(15)));

        // 学生数据缓存 - 缓存20分钟，变化不频繁
        cacheConfigurations.put("student:list", defaultConfig.entryTtl(Duration.ofMinutes(20)));
        cacheConfigurations.put("student:count", defaultConfig.entryTtl(Duration.ofMinutes(20)));
        cacheConfigurations.put("student:grade-stats", defaultConfig.entryTtl(Duration.ofMinutes(20)));

        // 课程数据缓存 - 缓存30分钟，相对稳定
        cacheConfigurations.put("course:list", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("course:count", defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // 班级数据缓存 - 缓存30分钟，相对稳定
        cacheConfigurations.put("class:list", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put("class:count", defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // 缴费记录缓存 - 缓存10分钟，有一定变化
        cacheConfigurations.put("payment:stats", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        cacheConfigurations.put("payment:records", defaultConfig.entryTtl(Duration.ofMinutes(10)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    /**
     * 配置Redis模板
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 配置序列化器
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = createJacksonSerializer();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // key序列化
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        // value序列化
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 创建Jackson序列化器
     */
    private Jackson2JsonRedisSerializer<Object> createJacksonSerializer() {
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        om.registerModule(new JavaTimeModule());

        // 使用新的构造函数方式，避免弃用的setObjectMapper方法
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(om, Object.class);

        return jackson2JsonRedisSerializer;
    }
}
