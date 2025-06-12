package com.campus.application.service.system;

/**
 * 缓存预热服务接口
 * 在应用启动时预热关键缓存，提高首次访问性能
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
public interface CacheWarmupService {

    /**
     * 执行缓存预热
     */
    void warmupCache();

    /**
     * 预热仪表盘缓存
     */
    void warmupDashboardCache();

    /**
     * 预热基础统计缓存
     */
    void warmupBasicStatsCache();

    /**
     * 清除所有缓存
     */
    void clearAllCache();

    /**
     * 清除指定缓存
     */
    void clearCache(String cacheName);

    /**
     * 获取缓存统计信息
     */
    void logCacheStats();

    /**
     * 手动刷新仪表盘缓存
     */
    void refreshDashboardCache();

    /**
     * 手动刷新基础统计缓存
     */
    void refreshBasicStatsCache();
}
