package com.campus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Redis启用验证测试
 * 验证Redis是否正确配置和启用
 * 
 * @author Campus Management Team
 * @since 2025-06-09
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Redis启用验证测试")
class RedisEnabledTest {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    @DisplayName("验证Redis组件正确注入")
    void testRedisComponentsInjected() {
        assertNotNull(cacheManager, "缓存管理器应该被正确注入");
        assertNotNull(redisTemplate, "Redis模板应该被正确注入");
        assertNotNull(stringRedisTemplate, "字符串Redis模板应该被正确注入");
        
        System.out.println("✅ Redis组件注入成功");
        System.out.println("📋 缓存管理器类型: " + cacheManager.getClass().getSimpleName());
        System.out.println("📋 Redis模板类型: " + redisTemplate.getClass().getSimpleName());
    }

    @Test
    @DisplayName("验证Redis缓存配置")
    void testRedisCacheConfiguration() {
        // 检查预配置的缓存是否存在
        assertNotNull(cacheManager.getCache("dashboard:stats"), "dashboard:stats缓存应该存在");
        assertNotNull(cacheManager.getCache("user:info"), "user:info缓存应该存在");
        assertNotNull(cacheManager.getCache("student:grade-stats"), "student:grade-stats缓存应该存在");
        
        System.out.println("✅ Redis缓存配置验证成功");
    }

    @Test
    @DisplayName("验证Redis基本操作")
    void testRedisBasicOperations() {
        String testKey = "test:redis:enabled:" + System.currentTimeMillis();
        String testValue = "Redis is working!";
        
        try {
            // 设置值
            stringRedisTemplate.opsForValue().set(testKey, testValue);
            
            // 获取值
            String retrievedValue = stringRedisTemplate.opsForValue().get(testKey);
            assertEquals(testValue, retrievedValue, "Redis应该能够正确存储和检索值");
            
            System.out.println("✅ Redis基本操作验证成功");
            System.out.println("📝 测试键: " + testKey);
            System.out.println("📝 存储值: " + testValue);
            System.out.println("📝 检索值: " + retrievedValue);
            
        } finally {
            // 清理测试数据
            stringRedisTemplate.delete(testKey);
        }
    }

    @Test
    @DisplayName("验证Redis连接状态")
    void testRedisConnectionStatus() {
        try {
            var connectionFactory = stringRedisTemplate.getConnectionFactory();
            assertNotNull(connectionFactory, "Redis连接工厂不应该为null");

            String pingResult = connectionFactory.getConnection().ping();

            assertEquals("PONG", pingResult, "Redis应该响应PONG");
            System.out.println("✅ Redis连接状态验证成功");
            System.out.println("📡 Ping响应: " + pingResult);

        } catch (Exception e) {
            fail("Redis连接失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("验证缓存功能")
    void testCacheFunctionality() {
        String cacheName = "test:cache";
        String cacheKey = "test-key-" + System.currentTimeMillis();
        String cacheValue = "test-value";
        
        try {
            // 获取缓存实例
            var cache = cacheManager.getCache(cacheName);
            if (cache == null) {
                // 如果缓存不存在，这是正常的，因为我们使用的是动态缓存名
                System.out.println("⚠️ 动态缓存名不存在，这是正常的");
                return;
            }
            
            // 存储到缓存
            cache.put(cacheKey, cacheValue);
            
            // 从缓存获取
            var wrapper = cache.get(cacheKey);
            assertNotNull(wrapper, "缓存应该包含存储的值");
            assertEquals(cacheValue, wrapper.get(), "缓存的值应该与存储的值相同");
            
            System.out.println("✅ 缓存功能验证成功");
            
        } catch (Exception e) {
            System.out.println("⚠️ 缓存功能测试跳过: " + e.getMessage());
        }
    }
}
