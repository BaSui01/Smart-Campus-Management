package com.campus.infrastructure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 缓存集成测试
 * 验证Spring Cache与Redis的集成是否正常工作
 * 
 * @author Campus Management Team
 * @since 2025-06-09
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("缓存集成测试")
class CacheIntegrationTest {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private TestCacheService testCacheService;

    @Test
    @DisplayName("测试缓存管理器配置")
    void testCacheManagerConfiguration() {
        assertNotNull(cacheManager, "缓存管理器应该被正确配置");
        
        // 检查预配置的缓存是否存在
        Cache dashboardStatsCache = cacheManager.getCache("dashboard:stats");
        assertNotNull(dashboardStatsCache, "dashboard:stats缓存应该存在");
        
        Cache userInfoCache = cacheManager.getCache("user:info");
        assertNotNull(userInfoCache, "user:info缓存应该存在");
        
        Cache studentGradeStatsCache = cacheManager.getCache("student:grade-stats");
        assertNotNull(studentGradeStatsCache, "student:grade-stats缓存应该存在");
    }

    @Test
    @DisplayName("测试缓存基本操作")
    void testBasicCacheOperations() {
        Cache testCache = cacheManager.getCache("dashboard:stats");
        assertNotNull(testCache, "测试缓存应该存在");
        
        String key = "test-key";
        String value = "test-value";
        
        // 存储值到缓存
        testCache.put(key, value);
        
        // 从缓存获取值
        Cache.ValueWrapper wrapper = testCache.get(key);
        assertNotNull(wrapper, "缓存应该包含存储的值");
        assertEquals(value, wrapper.get(), "缓存的值应该与存储的值相同");
        
        // 清除缓存
        testCache.evict(key);
        Cache.ValueWrapper afterEvict = testCache.get(key);
        assertNull(afterEvict, "清除后缓存应该不包含该值");
    }

    @Test
    @DisplayName("测试@Cacheable注解功能")
    void testCacheableAnnotation() {
        String input = "test-input";
        
        // 重置计数器
        testCacheService.resetCounter();
        
        // 第一次调用 - 应该执行方法
        String result1 = testCacheService.getCachedValue(input);
        assertEquals("cached-" + input, result1, "方法应该返回正确的值");
        assertEquals(1, testCacheService.getCallCount(), "方法应该被调用一次");
        
        // 第二次调用相同参数 - 应该从缓存返回
        String result2 = testCacheService.getCachedValue(input);
        assertEquals("cached-" + input, result2, "缓存应该返回相同的值");
        assertEquals(1, testCacheService.getCallCount(), "方法不应该被再次调用");
        
        // 调用不同参数 - 应该执行方法
        String result3 = testCacheService.getCachedValue("different-input");
        assertEquals("cached-different-input", result3, "方法应该返回新的值");
        assertEquals(2, testCacheService.getCallCount(), "方法应该被调用第二次");
    }

    @Test
    @DisplayName("测试缓存清除功能")
    void testCacheEviction() {
        String input = "eviction-test";
        
        // 重置计数器
        testCacheService.resetCounter();
        
        // 第一次调用
        testCacheService.getCachedValue(input);
        assertEquals(1, testCacheService.getCallCount(), "方法应该被调用一次");
        
        // 第二次调用 - 从缓存返回
        testCacheService.getCachedValue(input);
        assertEquals(1, testCacheService.getCallCount(), "方法不应该被再次调用");
        
        // 清除缓存
        testCacheService.evictCache(input);
        
        // 第三次调用 - 应该重新执行方法
        testCacheService.getCachedValue(input);
        assertEquals(2, testCacheService.getCallCount(), "清除缓存后方法应该被再次调用");
    }

    @Test
    @DisplayName("测试缓存清除所有功能")
    void testCacheEvictAll() {
        // 重置计数器
        testCacheService.resetCounter();
        
        // 调用多个不同参数
        testCacheService.getCachedValue("input1");
        testCacheService.getCachedValue("input2");
        testCacheService.getCachedValue("input3");
        assertEquals(3, testCacheService.getCallCount(), "方法应该被调用3次");
        
        // 再次调用相同参数 - 应该从缓存返回
        testCacheService.getCachedValue("input1");
        testCacheService.getCachedValue("input2");
        testCacheService.getCachedValue("input3");
        assertEquals(3, testCacheService.getCallCount(), "方法不应该被再次调用");
        
        // 清除所有缓存
        testCacheService.evictAllCache();
        
        // 再次调用 - 应该重新执行方法
        testCacheService.getCachedValue("input1");
        testCacheService.getCachedValue("input2");
        assertEquals(5, testCacheService.getCallCount(), "清除所有缓存后方法应该被再次调用");
    }

    @Test
    @DisplayName("测试缓存条件功能")
    void testCacheCondition() {
        // 重置计数器
        testCacheService.resetCounter();
        
        // 测试满足缓存条件的调用
        String result1 = testCacheService.getConditionalCachedValue("valid");
        assertEquals("conditional-valid", result1, "方法应该返回正确的值");
        assertEquals(1, testCacheService.getCallCount(), "方法应该被调用一次");
        
        // 再次调用相同参数 - 应该从缓存返回
        String result2 = testCacheService.getConditionalCachedValue("valid");
        assertEquals("conditional-valid", result2, "缓存应该返回相同的值");
        assertEquals(1, testCacheService.getCallCount(), "方法不应该被再次调用");
        
        // 测试不满足缓存条件的调用
        String result3 = testCacheService.getConditionalCachedValue("invalid");
        assertEquals("conditional-invalid", result3, "方法应该返回正确的值");
        assertEquals(2, testCacheService.getCallCount(), "方法应该被调用");
        
        // 再次调用不满足条件的参数 - 应该再次执行方法
        String result4 = testCacheService.getConditionalCachedValue("invalid");
        assertEquals("conditional-invalid", result4, "方法应该返回正确的值");
        assertEquals(3, testCacheService.getCallCount(), "不满足条件时方法应该被再次调用");
    }

    /**
     * 测试用的缓存服务
     */
    @Service
    public static class TestCacheService {
        
        private final AtomicInteger callCounter = new AtomicInteger(0);
        
        @Cacheable(value = "test:cache", key = "#input")
        public String getCachedValue(String input) {
            callCounter.incrementAndGet();
            return "cached-" + input;
        }
        
        @Cacheable(value = "test:conditional", key = "#input", condition = "#input != 'invalid'")
        public String getConditionalCachedValue(String input) {
            callCounter.incrementAndGet();
            return "conditional-" + input;
        }
        
        @org.springframework.cache.annotation.CacheEvict(value = "test:cache", key = "#input")
        public void evictCache(String input) {
            // 清除指定键的缓存
        }
        
        @org.springframework.cache.annotation.CacheEvict(value = "test:cache", allEntries = true)
        public void evictAllCache() {
            // 清除所有缓存
        }
        
        public int getCallCount() {
            return callCounter.get();
        }
        
        public void resetCounter() {
            callCounter.set(0);
        }
    }
}
