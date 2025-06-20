package com.campus.application.Implement.system;

import com.campus.application.service.system.CacheWarmupService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 简单缓存预热服务实现类
 * 用于非Redis环境的缓存预热
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-20
 */
@Service
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "simple", matchIfMissing = true)
public class SimpleCacheWarmupServiceImpl implements CacheWarmupService {
    
    private static final Logger logger = LoggerFactory.getLogger(SimpleCacheWarmupServiceImpl.class);
    
    @Override
    public void warmupCache() {
        logger.info("简单缓存模式：跳过缓存预热");
    }
    
    @Override
    public void warmupDashboardCache() {
        logger.info("简单缓存模式：跳过仪表板缓存预热");
    }

    @Override
    public void warmupBasicStatsCache() {
        logger.info("简单缓存模式：跳过基础统计缓存预热");
    }

    @Override
    public void clearAllCache() {
        logger.info("简单缓存模式：跳过清理所有缓存");
    }

    @Override
    public void clearCache(String cacheName) {
        logger.info("简单缓存模式：跳过清理缓存: {}", cacheName);
    }

    @Override
    public void logCacheStats() {
        logger.info("简单缓存模式：缓存统计信息不可用");
    }

    @Override
    public void refreshDashboardCache() {
        logger.info("简单缓存模式：跳过刷新仪表板缓存");
    }

    @Override
    public void refreshBasicStatsCache() {
        logger.info("简单缓存模式：跳过刷新基础统计缓存");
    }
}
