package com.campus.infrastructure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Redis连接测试
 * 验证Redis服务是否正常工作
 * 
 * @author Campus Management Team
 * @since 2025-06-09
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Redis连接测试")
class RedisConnectionTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    @DisplayName("测试Redis基本连接")
    void testRedisConnection() {
        // 测试基本的ping操作
        var connectionFactory = stringRedisTemplate.getConnectionFactory();
        assertNotNull(connectionFactory, "Redis连接工厂不应该为null");

        String result = connectionFactory.getConnection().ping();

        assertNotNull(result, "Redis连接应该返回PONG响应");
        assertEquals("PONG", result, "Redis ping应该返回PONG");
    }

    @Test
    @DisplayName("测试Redis字符串操作")
    void testRedisStringOperations() {
        String key = "test:string:key";
        String value = "test-value-" + System.currentTimeMillis();
        
        // 设置值
        stringRedisTemplate.opsForValue().set(key, value);
        
        // 获取值
        String retrievedValue = stringRedisTemplate.opsForValue().get(key);
        assertEquals(value, retrievedValue, "Redis应该能够正确存储和检索字符串值");
        
        // 清理
        stringRedisTemplate.delete(key);
    }

    @Test
    @DisplayName("测试Redis对象操作")
    void testRedisObjectOperations() {
        String key = "test:object:key";
        TestObject testObject = new TestObject("test-name", 123);
        
        // 设置对象
        redisTemplate.opsForValue().set(key, testObject);
        
        // 获取对象
        Object retrievedObject = redisTemplate.opsForValue().get(key);
        assertNotNull(retrievedObject, "Redis应该能够存储对象");
        
        // 清理
        redisTemplate.delete(key);
    }

    @Test
    @DisplayName("测试Redis过期时间")
    void testRedisExpiration() {
        String key = "test:expiration:key";
        String value = "expiring-value";
        
        // 设置带过期时间的值（1秒）
        stringRedisTemplate.opsForValue().set(key, value, Duration.ofSeconds(1));
        
        // 立即检查值存在
        String retrievedValue = stringRedisTemplate.opsForValue().get(key);
        assertEquals(value, retrievedValue, "值应该立即可用");
        
        // 检查TTL
        Long ttl = stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        assertTrue(ttl > 0, "TTL应该大于0");
        
        // 清理（如果还存在）
        stringRedisTemplate.delete(key);
    }

    @Test
    @DisplayName("测试Redis哈希操作")
    void testRedisHashOperations() {
        String key = "test:hash:key";
        String hashKey = "field1";
        String hashValue = "value1";
        
        // 设置哈希值
        stringRedisTemplate.opsForHash().put(key, hashKey, hashValue);
        
        // 获取哈希值
        Object retrievedValue = stringRedisTemplate.opsForHash().get(key, hashKey);
        assertEquals(hashValue, retrievedValue, "Redis应该能够正确存储和检索哈希值");
        
        // 检查哈希是否存在
        Boolean exists = stringRedisTemplate.opsForHash().hasKey(key, hashKey);
        assertTrue(exists, "哈希键应该存在");
        
        // 清理
        stringRedisTemplate.delete(key);
    }

    @Test
    @DisplayName("测试Redis列表操作")
    void testRedisListOperations() {
        String key = "test:list:key";
        String value1 = "item1";
        String value2 = "item2";
        
        // 添加到列表
        stringRedisTemplate.opsForList().rightPush(key, value1);
        stringRedisTemplate.opsForList().rightPush(key, value2);
        
        // 获取列表大小
        Long size = stringRedisTemplate.opsForList().size(key);
        assertEquals(2L, size, "列表应该包含2个元素");
        
        // 获取列表元素
        String firstItem = stringRedisTemplate.opsForList().index(key, 0);
        assertEquals(value1, firstItem, "第一个元素应该是item1");
        
        // 清理
        stringRedisTemplate.delete(key);
    }

    @Test
    @DisplayName("测试Redis集合操作")
    void testRedisSetOperations() {
        String key = "test:set:key";
        String member1 = "member1";
        String member2 = "member2";
        
        // 添加到集合
        stringRedisTemplate.opsForSet().add(key, member1, member2);
        
        // 检查集合大小
        Long size = stringRedisTemplate.opsForSet().size(key);
        assertEquals(2L, size, "集合应该包含2个成员");
        
        // 检查成员是否存在
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, member1);
        assertNotNull(isMember, "isMember结果不应该为null");
        assertTrue(isMember, "member1应该在集合中");
        
        // 清理
        stringRedisTemplate.delete(key);
    }

    /**
     * 测试用的简单对象
     */
    public static class TestObject {
        private String name;
        private Integer value;

        public TestObject() {}

        public TestObject(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getValue() { return value; }
        public void setValue(Integer value) { this.value = value; }
    }
}
