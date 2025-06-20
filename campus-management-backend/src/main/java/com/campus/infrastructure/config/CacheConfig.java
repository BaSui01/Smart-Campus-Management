package com.campus.infrastructure.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 缓存配置类
 * 配置Redis缓存管理器和不同类型数据的缓存策略
 * 
 * @author Campus Management Team
 * @since 2025-06-20
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Autowired
    private BusinessConfig businessConfig;

    /**
     * 缓存名称常量
     */
    public static final String CACHE_USER = "users";
    public static final String CACHE_STUDENT = "students";
    public static final String CACHE_TEACHER = "teachers";
    public static final String CACHE_COURSE = "courses";
    public static final String CACHE_CLASS = "classes";
    public static final String CACHE_MAJOR = "majors";
    public static final String CACHE_DEPARTMENT = "departments";
    public static final String CACHE_GRADE = "grades";
    public static final String CACHE_SCHEDULE = "schedules";
    public static final String CACHE_NOTIFICATION = "notifications";
    public static final String CACHE_SYSTEM_CONFIG = "system_configs";
    public static final String CACHE_PERMISSION = "permissions";
    public static final String CACHE_STATISTICS = "statistics";

    /**
     * Redis缓存管理器
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 默认缓存配置
        RedisCacheConfiguration defaultConfig = createCacheConfiguration(
            Duration.ofSeconds(businessConfig.getCache().getDefaultTtl())
        );

        // 不同类型数据的缓存配置
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // 用户缓存 - 1小时
        cacheConfigurations.put(CACHE_USER, createCacheConfiguration(
            Duration.ofSeconds(businessConfig.getCache().getUserTtl())
        ));
        
        // 学生缓存 - 30分钟
        cacheConfigurations.put(CACHE_STUDENT, createCacheConfiguration(
            Duration.ofMinutes(30)
        ));
        
        // 教师缓存 - 1小时
        cacheConfigurations.put(CACHE_TEACHER, createCacheConfiguration(
            Duration.ofHours(1)
        ));
        
        // 课程缓存 - 2小时
        cacheConfigurations.put(CACHE_COURSE, createCacheConfiguration(
            Duration.ofSeconds(businessConfig.getCache().getCourseTtl())
        ));
        
        // 班级缓存 - 1小时
        cacheConfigurations.put(CACHE_CLASS, createCacheConfiguration(
            Duration.ofHours(1)
        ));
        
        // 专业缓存 - 4小时
        cacheConfigurations.put(CACHE_MAJOR, createCacheConfiguration(
            Duration.ofHours(4)
        ));
        
        // 学院缓存 - 4小时
        cacheConfigurations.put(CACHE_DEPARTMENT, createCacheConfiguration(
            Duration.ofHours(4)
        ));
        
        // 成绩缓存 - 30分钟
        cacheConfigurations.put(CACHE_GRADE, createCacheConfiguration(
            Duration.ofMinutes(30)
        ));
        
        // 课程表缓存 - 1小时
        cacheConfigurations.put(CACHE_SCHEDULE, createCacheConfiguration(
            Duration.ofHours(1)
        ));
        
        // 通知缓存 - 15分钟
        cacheConfigurations.put(CACHE_NOTIFICATION, createCacheConfiguration(
            Duration.ofMinutes(15)
        ));
        
        // 系统配置缓存 - 24小时
        cacheConfigurations.put(CACHE_SYSTEM_CONFIG, createCacheConfiguration(
            Duration.ofSeconds(businessConfig.getCache().getConfigTtl())
        ));
        
        // 权限缓存 - 15分钟
        cacheConfigurations.put(CACHE_PERMISSION, createCacheConfiguration(
            Duration.ofMinutes(15)
        ));
        
        // 统计数据缓存 - 5分钟
        cacheConfigurations.put(CACHE_STATISTICS, createCacheConfiguration(
            Duration.ofMinutes(5)
        ));

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .transactionAware()
            .build();
    }

    /**
     * 创建缓存配置
     */
    private RedisCacheConfiguration createCacheConfiguration(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(ttl)
            .disableCachingNullValues()
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    /**
     * 缓存键生成器
     */
    @Bean
    public org.springframework.cache.interceptor.KeyGenerator customKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getSimpleName()).append(":");
            sb.append(method.getName()).append(":");
            
            if (params.length > 0) {
                for (Object param : params) {
                    if (param != null) {
                        sb.append(param.toString()).append(":");
                    } else {
                        sb.append("null:");
                    }
                }
                // 移除最后一个冒号
                sb.setLength(sb.length() - 1);
            }
            
            return sb.toString();
        };
    }

    /**
     * 缓存错误处理器
     */
    @Bean
    public org.springframework.cache.interceptor.CacheErrorHandler cacheErrorHandler() {
        return new org.springframework.cache.interceptor.SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(@org.springframework.lang.NonNull RuntimeException exception,
                    @org.springframework.lang.NonNull org.springframework.cache.Cache cache, @org.springframework.lang.NonNull Object key) {
                // 记录缓存获取错误，但不抛出异常
                System.err.println("Cache get error: " + exception.getMessage());
            }

            @Override
            public void handleCachePutError(@org.springframework.lang.NonNull RuntimeException exception,
                    @org.springframework.lang.NonNull org.springframework.cache.Cache cache, @org.springframework.lang.NonNull Object key, @org.springframework.lang.Nullable Object value) {
                // 记录缓存存储错误，但不抛出异常
                System.err.println("Cache put error: " + exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(@org.springframework.lang.NonNull RuntimeException exception,
                    @org.springframework.lang.NonNull org.springframework.cache.Cache cache, @org.springframework.lang.NonNull Object key) {
                // 记录缓存清除错误，但不抛出异常
                System.err.println("Cache evict error: " + exception.getMessage());
            }

            @Override
            public void handleCacheClearError(@org.springframework.lang.NonNull RuntimeException exception,
                    @org.springframework.lang.NonNull org.springframework.cache.Cache cache) {
                // 记录缓存清空错误，但不抛出异常
                System.err.println("Cache clear error: " + exception.getMessage());
            }
        };
    }

    /**
     * 缓存管理器配置
     */
    @Bean
    public org.springframework.cache.interceptor.CacheResolver cacheResolver(CacheManager cacheManager) {
        org.springframework.cache.interceptor.SimpleCacheResolver resolver = 
            new org.springframework.cache.interceptor.SimpleCacheResolver(cacheManager);
        return resolver;
    }

    /**
     * 缓存操作工具类
     */
    @Bean
    public CacheUtils cacheUtils(CacheManager cacheManager) {
        return new CacheUtils(cacheManager);
    }

    /**
     * 缓存工具类
     */
    public static class CacheUtils {
        private final CacheManager cacheManager;

        public CacheUtils(CacheManager cacheManager) {
            this.cacheManager = cacheManager;
        }

        /**
         * 清除指定缓存的所有数据
         */
        public void clearCache(String cacheName) {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        }

        /**
         * 清除指定缓存的特定键
         */
        public void evictCache(String cacheName, Object key) {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.evict(key);
            }
        }

        /**
         * 获取缓存值
         */
        public <T> T getCacheValue(String cacheName, Object key, Class<T> type) {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                org.springframework.cache.Cache.ValueWrapper wrapper = cache.get(key);
                if (wrapper != null) {
                    return type.cast(wrapper.get());
                }
            }
            return null;
        }

        /**
         * 设置缓存值
         */
        public void putCacheValue(String cacheName, Object key, Object value) {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.put(key, value);
            }
        }

        /**
         * 清除所有缓存
         */
        public void clearAllCaches() {
            cacheManager.getCacheNames().forEach(this::clearCache);
        }

        /**
         * 清除用户相关的所有缓存
         */
        public void clearUserRelatedCaches(Long userId) {
            evictCache(CACHE_USER, userId);
            evictCache(CACHE_PERMISSION, userId);
            evictCache(CACHE_NOTIFICATION, userId);
            // 可以根据需要添加更多相关缓存的清除
        }

        /**
         * 清除课程相关的所有缓存
         */
        public void clearCourseRelatedCaches(Long courseId) {
            evictCache(CACHE_COURSE, courseId);
            clearCache(CACHE_SCHEDULE); // 课程表可能受影响
            clearCache(CACHE_STATISTICS); // 统计数据可能受影响
        }

        /**
         * 清除学生相关的所有缓存
         */
        public void clearStudentRelatedCaches(Long studentId) {
            evictCache(CACHE_STUDENT, studentId);
            evictCache(CACHE_GRADE, studentId);
            clearCache(CACHE_STATISTICS); // 统计数据可能受影响
        }
    }
}
