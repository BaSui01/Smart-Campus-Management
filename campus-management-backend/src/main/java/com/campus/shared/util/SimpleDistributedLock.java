package com.campus.shared.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 简单分布式锁工具类
 * 用于非Redis环境的本地锁实现
 * 
 * @author Campus Team
 * @since 2025-06-20
 */
@Component
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "simple", matchIfMissing = true)
public class SimpleDistributedLock implements DistributedLock {

    private static final Logger logger = LoggerFactory.getLogger(SimpleDistributedLock.class);

    private final ConcurrentHashMap<String, LockInfo> locks = new ConcurrentHashMap<>();
    private static final String LOCK_PREFIX = "lock:";
    private static final int DEFAULT_EXPIRE_TIME = 30; // 30秒

    private static class LockInfo {
        final String value;
        final long expireTime;

        LockInfo(String value, long expireTime) {
            this.value = value;
            this.expireTime = expireTime;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }

    /**
     * 获取分布式锁
     */
    public boolean tryLock(String key, String value, int expireTime) {
        try {
            String lockKey = LOCK_PREFIX + key;
            long expireTimeMs = System.currentTimeMillis() + expireTime * 1000L;
            
            LockInfo newLock = new LockInfo(value, expireTimeMs);
            LockInfo existingLock = locks.putIfAbsent(lockKey, newLock);
            
            if (existingLock == null) {
                logger.debug("获取简单锁成功: key={}, value={}", lockKey, value);
                return true;
            } else if (existingLock.isExpired()) {
                // 如果现有锁已过期，尝试替换
                if (locks.replace(lockKey, existingLock, newLock)) {
                    logger.debug("获取简单锁成功（替换过期锁）: key={}, value={}", lockKey, value);
                    return true;
                }
            }
            
            logger.debug("获取简单锁失败: key={}, value={}", lockKey, value);
            return false;
        } catch (Exception e) {
            logger.error("获取简单锁异常: key={}, value={}", key, value, e);
            return false;
        }
    }

    public boolean tryLock(String key, String value) {
        return tryLock(key, value, DEFAULT_EXPIRE_TIME);
    }

    public String tryLock(String key, int expireTime) {
        String value = UUID.randomUUID().toString();
        boolean success = tryLock(key, value, expireTime);
        return success ? value : null;
    }

    public String tryLock(String key) {
        return tryLock(key, DEFAULT_EXPIRE_TIME);
    }

    /**
     * 释放分布式锁
     */
    public boolean releaseLock(String key, String value) {
        try {
            String lockKey = LOCK_PREFIX + key;
            LockInfo lockInfo = locks.get(lockKey);
            
            if (lockInfo != null && value.equals(lockInfo.value)) {
                boolean removed = locks.remove(lockKey, lockInfo);
                if (removed) {
                    logger.debug("释放简单锁成功: key={}, value={}", lockKey, value);
                    return true;
                }
            }
            
            logger.debug("释放简单锁失败: key={}, value={}", lockKey, value);
            return false;
        } catch (Exception e) {
            logger.error("释放简单锁异常: key={}, value={}", key, value, e);
            return false;
        }
    }

    public boolean isLocked(String key) {
        try {
            String lockKey = LOCK_PREFIX + key;
            LockInfo lockInfo = locks.get(lockKey);
            return lockInfo != null && !lockInfo.isExpired();
        } catch (Exception e) {
            logger.error("检查简单锁是否存在异常: key={}", key, e);
            return false;
        }
    }

    public long getLockTtl(String key) {
        try {
            String lockKey = LOCK_PREFIX + key;
            LockInfo lockInfo = locks.get(lockKey);
            if (lockInfo == null) {
                return -2; // 键不存在
            }
            long remainingMs = lockInfo.expireTime - System.currentTimeMillis();
            return remainingMs > 0 ? remainingMs / 1000 : -2;
        } catch (Exception e) {
            logger.error("获取简单锁过期时间异常: key={}", key, e);
            return -2;
        }
    }

    public boolean renewLock(String key, String value, int expireTime) {
        try {
            String lockKey = LOCK_PREFIX + key;
            LockInfo lockInfo = locks.get(lockKey);
            
            if (lockInfo != null && value.equals(lockInfo.value)) {
                long newExpireTime = System.currentTimeMillis() + expireTime * 1000L;
                LockInfo newLockInfo = new LockInfo(value, newExpireTime);
                
                if (locks.replace(lockKey, lockInfo, newLockInfo)) {
                    logger.debug("续期简单锁成功: key={}, value={}, expireTime={}", lockKey, value, expireTime);
                    return true;
                }
            }
            
            logger.debug("续期简单锁失败: key={}, value={}, expireTime={}", lockKey, value, expireTime);
            return false;
        } catch (Exception e) {
            logger.error("续期简单锁异常: key={}, value={}, expireTime={}", key, value, expireTime, e);
            return false;
        }
    }

    public boolean forceReleaseLock(String key) {
        try {
            String lockKey = LOCK_PREFIX + key;
            LockInfo removed = locks.remove(lockKey);
            
            if (removed != null) {
                logger.warn("强制释放简单锁: key={}", lockKey);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("强制释放简单锁异常: key={}", key, e);
            return false;
        }
    }

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

    public boolean executeWithLock(String key, Runnable task) {
        return executeWithLock(key, DEFAULT_EXPIRE_TIME, task);
    }
}
