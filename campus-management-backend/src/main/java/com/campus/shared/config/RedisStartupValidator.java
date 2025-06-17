package com.campus.shared.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis启动验证器
 * 在应用启动完成后验证Redis连接和缓存功能
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-13
 */
@Component
public class RedisStartupValidator {

    private static final Logger logger = LoggerFactory.getLogger(RedisStartupValidator.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void validateRedisOnStartup() {
        logger.info("🔍 开始验证Redis连接和缓存功能...");
        
        try {
            // 1. 测试基本连接
            validateConnection();
            
            // 2. 测试基本操作
            validateBasicOperations();
            
            // 3. 测试缓存功能
            validateCacheOperations();
            
            logger.info("✅ Redis验证完成 - 所有功能正常");
            
        } catch (Exception e) {
            logger.error("❌ Redis验证失败", e);
            // 不抛出异常，允许应用继续启动，但记录错误
        }
    }

    private void validateConnection() {
        try {
            var connectionFactory = redisTemplate.getConnectionFactory();
            if (connectionFactory == null) {
                throw new RuntimeException("Redis连接工厂为空");
            }
            
            var connection = connectionFactory.getConnection();
            try {
                String pong = connection.ping();
                if (!"PONG".equals(pong)) {
                    throw new RuntimeException("Redis ping失败: " + pong);
                }
                logger.info("✅ Redis连接测试通过");
            } finally {
                connection.close();
            }
        } catch (Exception e) {
            logger.error("❌ Redis连接测试失败", e);
            throw e;
        }
    }

    private void validateBasicOperations() {
        try {
            String testKey = "startup:test:" + System.currentTimeMillis();
            String testValue = "Redis启动测试";
            
            // 测试写入
            redisTemplate.opsForValue().set(testKey, testValue);
            
            // 测试读取
            String retrievedValue = (String) redisTemplate.opsForValue().get(testKey);
            if (!testValue.equals(retrievedValue)) {
                throw new RuntimeException("Redis读写测试失败");
            }
            
            // 测试删除
            Boolean deleted = redisTemplate.delete(testKey);
            if (!Boolean.TRUE.equals(deleted)) {
                logger.warn("⚠️ Redis删除操作可能失败");
            }
            
            logger.info("✅ Redis基本操作测试通过");
            
        } catch (Exception e) {
            logger.error("❌ Redis基本操作测试失败", e);
            throw e;
        }
    }

    private void validateCacheOperations() {
        try {
            // 测试Hash操作
            String hashKey = "startup:hash:" + System.currentTimeMillis();
            redisTemplate.opsForHash().put(hashKey, "field1", "value1");
            redisTemplate.opsForHash().put(hashKey, "field2", "value2");
            
            Object value1 = redisTemplate.opsForHash().get(hashKey, "field1");
            if (!"value1".equals(value1)) {
                throw new RuntimeException("Redis Hash操作测试失败");
            }
            
            // 测试List操作
            String listKey = "startup:list:" + System.currentTimeMillis();
            redisTemplate.opsForList().rightPush(listKey, "item1");
            redisTemplate.opsForList().rightPush(listKey, "item2");
            
            String item = (String) redisTemplate.opsForList().leftPop(listKey);
            if (!"item1".equals(item)) {
                throw new RuntimeException("Redis List操作测试失败");
            }
            
            // 清理测试数据
            redisTemplate.delete(hashKey);
            redisTemplate.delete(listKey);
            
            logger.info("✅ Redis缓存操作测试通过");
            
        } catch (Exception e) {
            logger.error("❌ Redis缓存操作测试失败", e);
            throw e;
        }
    }
}
