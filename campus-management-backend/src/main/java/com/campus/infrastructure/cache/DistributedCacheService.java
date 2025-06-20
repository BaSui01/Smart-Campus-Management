package com.campus.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 分布式缓存服务
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DistributedCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 分布式锁Lua脚本
     */
    private static final String LOCK_SCRIPT = """
            if redis.call('get', KEYS[1]) == ARGV[1] then
                return redis.call('del', KEYS[1])
            else
                return 0
            end
            """;

    private final DefaultRedisScript<Long> lockScript = new DefaultRedisScript<>(LOCK_SCRIPT, Long.class);

    /**
     * 设置缓存
     * 
     * @param key 键
     * @param value 值
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("设置缓存失败: key={}", key, e);
        }
    }

    /**
     * 设置缓存并指定过期时间
     * 
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            log.error("设置缓存失败: key={}, timeout={}", key, timeout, e);
        }
    }

    /**
     * 获取缓存
     * 
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("获取缓存失败: key={}", key, e);
            return null;
        }
    }

    /**
     * 获取缓存并指定类型
     * 
     * @param key 键
     * @param clazz 类型
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return value != null ? (T) value : null;
        } catch (Exception e) {
            log.error("获取缓存失败: key={}", key, e);
            return null;
        }
    }

    /**
     * 删除缓存
     * 
     * @param key 键
     * @return 是否删除成功
     */
    public boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            log.error("删除缓存失败: key={}", key, e);
            return false;
        }
    }

    /**
     * 批量删除缓存
     * 
     * @param keys 键集合
     * @return 删除数量
     */
    public long delete(Collection<String> keys) {
        try {
            Long count = redisTemplate.delete(keys);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("批量删除缓存失败: keys={}", keys, e);
            return 0;
        }
    }

    /**
     * 检查键是否存在
     * 
     * @param key 键
     * @return 是否存在
     */
    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("检查键是否存在失败: key={}", key, e);
            return false;
        }
    }

    /**
     * 设置过期时间
     * 
     * @param key 键
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否设置成功
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
        } catch (Exception e) {
            log.error("设置过期时间失败: key={}, timeout={}", key, timeout, e);
            return false;
        }
    }

    /**
     * 获取过期时间
     * 
     * @param key 键
     * @return 过期时间（秒）
     */
    public long getExpire(String key) {
        try {
            Long expire = redisTemplate.getExpire(key);
            return expire != null ? expire : -1;
        } catch (Exception e) {
            log.error("获取过期时间失败: key={}", key, e);
            return -1;
        }
    }

    /**
     * 递增
     * 
     * @param key 键
     * @param delta 递增值
     * @return 递增后的值
     */
    public long increment(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().increment(key, delta);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("递增失败: key={}, delta={}", key, delta, e);
            return 0;
        }
    }

    /**
     * 递减
     * 
     * @param key 键
     * @param delta 递减值
     * @return 递减后的值
     */
    public long decrement(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().decrement(key, delta);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("递减失败: key={}, delta={}", key, delta, e);
            return 0;
        }
    }

    /**
     * 获取分布式锁
     * 
     * @param lockKey 锁键
     * @param requestId 请求ID
     * @param expireTime 过期时间（毫秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String requestId, long expireTime) {
        try {
            String result = redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, 
                    Duration.ofMillis(expireTime)) ? "OK" : null;
            return "OK".equals(result);
        } catch (Exception e) {
            log.error("获取分布式锁失败: lockKey={}, requestId={}", lockKey, requestId, e);
            return false;
        }
    }

    /**
     * 释放分布式锁
     * 
     * @param lockKey 锁键
     * @param requestId 请求ID
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String requestId) {
        try {
            Long result = redisTemplate.execute(lockScript, Collections.singletonList(lockKey), requestId);
            return result != null && result == 1;
        } catch (Exception e) {
            log.error("释放分布式锁失败: lockKey={}, requestId={}", lockKey, requestId, e);
            return false;
        }
    }

    /**
     * Hash操作 - 设置
     * 
     * @param key 键
     * @param hashKey Hash键
     * @param value 值
     */
    public void hSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
        } catch (Exception e) {
            log.error("Hash设置失败: key={}, hashKey={}", key, hashKey, e);
        }
    }

    /**
     * Hash操作 - 获取
     * 
     * @param key 键
     * @param hashKey Hash键
     * @return 值
     */
    public Object hGet(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().get(key, hashKey);
        } catch (Exception e) {
            log.error("Hash获取失败: key={}, hashKey={}", key, hashKey, e);
            return null;
        }
    }

    /**
     * Hash操作 - 删除
     * 
     * @param key 键
     * @param hashKeys Hash键数组
     * @return 删除数量
     */
    public long hDelete(String key, Object... hashKeys) {
        try {
            Long count = redisTemplate.opsForHash().delete(key, hashKeys);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("Hash删除失败: key={}", key, e);
            return 0;
        }
    }

    /**
     * List操作 - 左推入
     * 
     * @param key 键
     * @param value 值
     * @return 列表长度
     */
    public long lPush(String key, Object value) {
        try {
            Long count = redisTemplate.opsForList().leftPush(key, value);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("List左推入失败: key={}", key, e);
            return 0;
        }
    }

    /**
     * List操作 - 右弹出
     * 
     * @param key 键
     * @return 值
     */
    public Object rPop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            log.error("List右弹出失败: key={}", key, e);
            return null;
        }
    }

    /**
     * Set操作 - 添加
     * 
     * @param key 键
     * @param values 值数组
     * @return 添加数量
     */
    public long sAdd(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("Set添加失败: key={}", key, e);
            return 0;
        }
    }

    /**
     * Set操作 - 获取所有成员
     * 
     * @param key 键
     * @return 成员集合
     */
    public Set<Object> sMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("Set获取成员失败: key={}", key, e);
            return new HashSet<>();
        }
    }

    /**
     * 模糊查询键
     * 
     * @param pattern 模式
     * @return 键集合
     */
    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            log.error("模糊查询键失败: pattern={}", pattern, e);
            return new HashSet<>();
        }
    }
}
