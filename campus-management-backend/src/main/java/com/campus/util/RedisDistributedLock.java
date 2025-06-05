package com.campus.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Redis 分布式锁工具类
 * 
 * @author Campus Team
 * @since 2025-06-05
 */
@Component
public class RedisDistributedLock {

    private static final Logger logger = LoggerFactory.getLogger(RedisDistributedLock.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String LOCK_PREFIX = "lock:";
    private static final int DEFAULT_EXPIRE_TIME = 30; // 30秒

    /**
     * 获取分布式锁
     * 
     * @param key 锁的键
     * @param value 锁的值（通常使用UUID）
     * @param expireTime 过期时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String key, String value, int expireTime) {
        try {
            String lockKey = LOCK_PREFIX + key;
            Boolean result = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, value, expireTime, TimeUnit.SECONDS);
            
            boolean success = Boolean.TRUE.equals(result);
            if (success) {
                logger.debug("获取分布式锁成功: key={}, value={}", lockKey, value);
            } else {
                logger.debug("获取分布式锁失败: key={}, value={}", lockKey, value);
            }
            
            return success;
        } catch (Exception e) {
            logger.error("获取分布式锁异常: key={}, value={}", key, value, e);
            return false;
        }
    }

    /**
     * 获取分布式锁（使用默认过期时间）
     * 
     * @param key 锁的键
     * @param value 锁的值
     * @return 是否获取成功
     */
    public boolean tryLock(String key, String value) {
        return tryLock(key, value, DEFAULT_EXPIRE_TIME);
    }

    /**
     * 获取分布式锁（自动生成UUID作为值）
     * 
     * @param key 锁的键
     * @param expireTime 过期时间（秒）
     * @return 锁的值（UUID），如果获取失败返回null
     */
    public String tryLock(String key, int expireTime) {
        String value = UUID.randomUUID().toString();
        boolean success = tryLock(key, value, expireTime);
        return success ? value : null;
    }

    /**
     * 获取分布式锁（自动生成UUID，使用默认过期时间）
     * 
     * @param key 锁的键
     * @return 锁的值（UUID），如果获取失败返回null
     */
    public String tryLock(String key) {
        return tryLock(key, DEFAULT_EXPIRE_TIME);
    }

    /**
     * 释放分布式锁
     * 
     * @param key 锁的键
     * @param value 锁的值
     * @return 是否释放成功
     */
    public boolean releaseLock(String key, String value) {
        try {
            String lockKey = LOCK_PREFIX + key;
            
            // 使用 Lua 脚本确保原子性操作
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                           "return redis.call('del', KEYS[1]) else return 0 end";
            
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            redisScript.setResultType(Long.class);
            
            Long result = redisTemplate.execute(
                redisScript,
                Collections.singletonList(lockKey),
                value
            );
            
            boolean success = Long.valueOf(1).equals(result);
            if (success) {
                logger.debug("释放分布式锁成功: key={}, value={}", lockKey, value);
            } else {
                logger.debug("释放分布式锁失败: key={}, value={}", lockKey, value);
            }
            
            return success;
        } catch (Exception e) {
            logger.error("释放分布式锁异常: key={}, value={}", key, value, e);
            return false;
        }
    }

    /**
     * 检查锁是否存在
     * 
     * @param key 锁的键
     * @return 是否存在
     */
    public boolean isLocked(String key) {
        try {
            String lockKey = LOCK_PREFIX + key;
            return Boolean.TRUE.equals(redisTemplate.hasKey(lockKey));
        } catch (Exception e) {
            logger.error("检查锁是否存在异常: key={}", key, e);
            return false;
        }
    }

    /**
     * 获取锁的剩余过期时间
     * 
     * @param key 锁的键
     * @return 剩余过期时间（秒），-1表示永不过期，-2表示键不存在
     */
    public long getLockTtl(String key) {
        try {
            String lockKey = LOCK_PREFIX + key;
            return redisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.error("获取锁过期时间异常: key={}", key, e);
            return -2;
        }
    }

    /**
     * 续期锁
     * 
     * @param key 锁的键
     * @param value 锁的值
     * @param expireTime 新的过期时间（秒）
     * @return 是否续期成功
     */
    public boolean renewLock(String key, String value, int expireTime) {
        try {
            String lockKey = LOCK_PREFIX + key;
            
            // 使用 Lua 脚本确保原子性操作
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                           "return redis.call('expire', KEYS[1], ARGV[2]) else return 0 end";
            
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            redisScript.setResultType(Long.class);
            
            Long result = redisTemplate.execute(
                redisScript,
                Collections.singletonList(lockKey),
                value,
                String.valueOf(expireTime)
            );
            
            boolean success = Long.valueOf(1).equals(result);
            if (success) {
                logger.debug("续期分布式锁成功: key={}, value={}, expireTime={}", lockKey, value, expireTime);
            } else {
                logger.debug("续期分布式锁失败: key={}, value={}, expireTime={}", lockKey, value, expireTime);
            }
            
            return success;
        } catch (Exception e) {
            logger.error("续期分布式锁异常: key={}, value={}, expireTime={}", key, value, expireTime, e);
            return false;
        }
    }

    /**
     * 强制释放锁（不检查值是否匹配）
     * 注意：这个方法应该谨慎使用，只在确定需要强制释放时使用
     * 
     * @param key 锁的键
     * @return 是否释放成功
     */
    public boolean forceReleaseLock(String key) {
        try {
            String lockKey = LOCK_PREFIX + key;
            Boolean result = redisTemplate.delete(lockKey);
            
            boolean success = Boolean.TRUE.equals(result);
            if (success) {
                logger.warn("强制释放分布式锁: key={}", lockKey);
            }
            
            return success;
        } catch (Exception e) {
            logger.error("强制释放分布式锁异常: key={}", key, e);
            return false;
        }
    }

    /**
     * 执行带锁的操作
     * 
     * @param key 锁的键
     * @param expireTime 过期时间（秒）
     * @param task 要执行的任务
     * @return 是否执行成功
     */
    public boolean executeWithLock(String key, int expireTime, Runnable task) {
        String lockValue = tryLock(key, expireTime);
        if (lockValue != null) {
            try {
                task.run();
                return true;
            } finally {
                releaseLock(key, lockValue);
            }
        }
        return false;
    }

    /**
     * 执行带锁的操作（使用默认过期时间）
     * 
     * @param key 锁的键
     * @param task 要执行的任务
     * @return 是否执行成功
     */
    public boolean executeWithLock(String key, Runnable task) {
        return executeWithLock(key, DEFAULT_EXPIRE_TIME, task);
    }
}
