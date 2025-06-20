package com.campus.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Redis集群配置
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Data
@Configuration
@EnableCaching
@ConfigurationProperties(prefix = "campus.redis")
public class RedisClusterConfig {

    /**
     * 是否启用Redis集群
     */
    private boolean clusterEnabled = false;

    /**
     * 集群节点
     */
    private List<String> clusterNodes;

    /**
     * 最大重定向次数
     */
    private int maxRedirects = 3;

    /**
     * 连接超时时间
     */
    private Duration timeout = Duration.ofSeconds(5);

    /**
     * 密码
     */
    private String password;

    /**
     * 缓存配置
     */
    private Cache cache = new Cache();

    @Data
    public static class Cache {
        /**
         * 默认过期时间（秒）
         */
        private long defaultExpiration = 3600;

        /**
         * 缓存名称和过期时间映射
         */
        private Map<String, Long> expirations = new HashMap<>();

        /**
         * 缓存前缀
         */
        private String keyPrefix = "campus:";

        /**
         * 是否允许缓存空值
         */
        private boolean cacheNullValues = false;
    }

    /**
     * Redis连接工厂
     */
    @Bean
    @SuppressWarnings("deprecation")
    public RedisConnectionFactory redisConnectionFactory() {
        if (clusterEnabled && clusterNodes != null && !clusterNodes.isEmpty()) {
            // 集群模式
            RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration(clusterNodes);
            clusterConfig.setMaxRedirects(maxRedirects);
            if (password != null) {
                clusterConfig.setPassword(password);
            }
            
            LettuceConnectionFactory factory = new LettuceConnectionFactory(clusterConfig);
            factory.setTimeout(timeout.toMillis());
            return factory;
        } else {
            // 单机模式（开发环境）
            LettuceConnectionFactory factory = new LettuceConnectionFactory();
            factory.setTimeout(timeout.toMillis());
            if (password != null) {
                factory.setPassword(password);
            }
            return factory;
        }
    }

    /**
     * RedisTemplate配置
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 缓存管理器
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(cache.getDefaultExpiration()))
                .computePrefixWith(cacheName -> cache.getKeyPrefix() + cacheName + ":")
                .disableCachingNullValues();

        if (cache.isCacheNullValues()) {
            defaultConfig = defaultConfig.disableCachingNullValues();
        }

        // 设置不同缓存的过期时间
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cache.getExpirations().forEach((cacheName, expiration) -> {
            RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofSeconds(expiration))
                    .computePrefixWith(name -> cache.getKeyPrefix() + name + ":");
            cacheConfigurations.put(cacheName, config);
        });

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
