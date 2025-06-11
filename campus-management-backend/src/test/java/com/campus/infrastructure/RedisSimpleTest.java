package com.campus.infrastructure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Redis简单连接测试
 * 使用@DataRedisTest避免加载完整的Spring上下文
 * 
 * @author Campus Management Team
 * @since 2025-06-09
 */
@DataRedisTest
@ActiveProfiles("test")
@DisplayName("Redis简单连接测试")
class RedisSimpleTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    @DisplayName("验证Redis组件正确注入")
    void testRedisComponentsInjected() {
        assertNotNull(stringRedisTemplate, "字符串Redis模板应该被正确注入");

        System.out.println("✅ Redis组件注入成功");
        System.out.println("📋 字符串Redis模板类型: " + stringRedisTemplate.getClass().getSimpleName());
    }

    @Test
    @DisplayName("验证Redis连接状态")
    void testRedisConnectionStatus() {
        try {
            var connectionFactory = stringRedisTemplate.getConnectionFactory();
            assertNotNull(connectionFactory, "Redis连接工厂不应为空");

            String pingResult = connectionFactory.getConnection().ping();

            assertEquals("PONG", pingResult, "Redis应该响应PONG");
            System.out.println("✅ Redis连接状态验证成功");
            System.out.println("📡 Ping响应: " + pingResult);

        } catch (Exception e) {
            fail("Redis连接失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("验证Redis基本操作")
    void testRedisBasicOperations() {
        String testKey = "test:redis:simple:" + System.currentTimeMillis();
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
    @DisplayName("验证Redis字符串操作")
    void testRedisStringOperations() {
        String key = "test:string:key:" + System.currentTimeMillis();
        String value = "test-value-" + System.currentTimeMillis();
        
        try {
            // 设置值
            stringRedisTemplate.opsForValue().set(key, value);
            
            // 获取值
            String retrievedValue = stringRedisTemplate.opsForValue().get(key);
            assertEquals(value, retrievedValue, "Redis应该能够正确存储和检索字符串值");
            
            // 检查键是否存在
            Boolean exists = stringRedisTemplate.hasKey(key);
            assertTrue(exists, "键应该存在");
            
            System.out.println("✅ Redis字符串操作验证成功");
            
        } finally {
            // 清理
            stringRedisTemplate.delete(key);
        }
    }

    @Test
    @DisplayName("验证Redis哈希操作")
    void testRedisHashOperations() {
        String key = "test:hash:key:" + System.currentTimeMillis();
        String hashKey = "field1";
        String hashValue = "value1";
        
        try {
            // 设置哈希值
            stringRedisTemplate.opsForHash().put(key, hashKey, hashValue);
            
            // 获取哈希值
            Object retrievedValue = stringRedisTemplate.opsForHash().get(key, hashKey);
            assertEquals(hashValue, retrievedValue, "Redis应该能够正确存储和检索哈希值");
            
            // 检查哈希是否存在
            Boolean exists = stringRedisTemplate.opsForHash().hasKey(key, hashKey);
            assertTrue(exists, "哈希键应该存在");
            
            System.out.println("✅ Redis哈希操作验证成功");
            
        } finally {
            // 清理
            stringRedisTemplate.delete(key);
        }
    }

    @Test
    @DisplayName("验证Redis列表操作")
    void testRedisListOperations() {
        String key = "test:list:key:" + System.currentTimeMillis();
        String value1 = "item1";
        String value2 = "item2";
        
        try {
            // 添加到列表
            stringRedisTemplate.opsForList().rightPush(key, value1);
            stringRedisTemplate.opsForList().rightPush(key, value2);
            
            // 获取列表大小
            Long size = stringRedisTemplate.opsForList().size(key);
            assertEquals(2L, size, "列表应该包含2个元素");
            
            // 获取列表元素
            String firstItem = stringRedisTemplate.opsForList().index(key, 0);
            assertEquals(value1, firstItem, "第一个元素应该是item1");
            
            System.out.println("✅ Redis列表操作验证成功");
            
        } finally {
            // 清理
            stringRedisTemplate.delete(key);
        }
    }

    @Test
    @DisplayName("验证Redis集合操作")
    void testRedisSetOperations() {
        String key = "test:set:key:" + System.currentTimeMillis();
        String member1 = "member1";
        String member2 = "member2";
        
        try {
            // 添加到集合
            stringRedisTemplate.opsForSet().add(key, member1, member2);
            
            // 检查集合大小
            Long size = stringRedisTemplate.opsForSet().size(key);
            assertEquals(2L, size, "集合应该包含2个成员");
            
            // 检查成员是否存在
            Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, member1);
            assertTrue(Boolean.TRUE.equals(isMember), "member1应该在集合中");
            
            System.out.println("✅ Redis集合操作验证成功");
            
        } finally {
            // 清理
            stringRedisTemplate.delete(key);
        }
    }
}
