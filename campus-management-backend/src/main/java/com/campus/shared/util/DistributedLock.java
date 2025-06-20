package com.campus.shared.util;

/**
 * 分布式锁接口
 * 
 * @author Campus Team
 * @since 2025-06-20
 */
public interface DistributedLock {

    /**
     * 获取分布式锁
     * 
     * @param key 锁的键
     * @param value 锁的值（通常使用UUID）
     * @param expireTime 过期时间（秒）
     * @return 是否获取成功
     */
    boolean tryLock(String key, String value, int expireTime);

    /**
     * 获取分布式锁（使用默认过期时间）
     * 
     * @param key 锁的键
     * @param value 锁的值
     * @return 是否获取成功
     */
    boolean tryLock(String key, String value);

    /**
     * 获取分布式锁（自动生成UUID作为值）
     * 
     * @param key 锁的键
     * @param expireTime 过期时间（秒）
     * @return 锁的值（UUID），如果获取失败返回null
     */
    String tryLock(String key, int expireTime);

    /**
     * 获取分布式锁（自动生成UUID，使用默认过期时间）
     * 
     * @param key 锁的键
     * @return 锁的值（UUID），如果获取失败返回null
     */
    String tryLock(String key);

    /**
     * 释放分布式锁
     * 
     * @param key 锁的键
     * @param value 锁的值
     * @return 是否释放成功
     */
    boolean releaseLock(String key, String value);

    /**
     * 检查锁是否存在
     * 
     * @param key 锁的键
     * @return 是否存在
     */
    boolean isLocked(String key);

    /**
     * 获取锁的剩余过期时间
     * 
     * @param key 锁的键
     * @return 剩余过期时间（秒），-1表示永不过期，-2表示键不存在
     */
    long getLockTtl(String key);

    /**
     * 续期锁
     * 
     * @param key 锁的键
     * @param value 锁的值
     * @param expireTime 新的过期时间（秒）
     * @return 是否续期成功
     */
    boolean renewLock(String key, String value, int expireTime);

    /**
     * 强制释放锁（不检查值是否匹配）
     * 
     * @param key 锁的键
     * @return 是否释放成功
     */
    boolean forceReleaseLock(String key);

    /**
     * 执行带锁的操作
     * 
     * @param key 锁的键
     * @param expireTime 过期时间（秒）
     * @param task 要执行的任务
     * @return 是否执行成功
     */
    boolean executeWithLock(String key, int expireTime, Runnable task);

    /**
     * 执行带锁的操作（使用默认过期时间）
     * 
     * @param key 锁的键
     * @param task 要执行的任务
     * @return 是否执行成功
     */
    boolean executeWithLock(String key, Runnable task);
}
