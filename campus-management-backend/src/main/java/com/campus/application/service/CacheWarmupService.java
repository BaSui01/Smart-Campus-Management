package com.campus.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缓存预热服务
 * 在应用启动时预热关键缓存，提高首次访问性能
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@Service
public class CacheWarmupService implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(CacheWarmupService.class);

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private SchoolClassService schoolClassService;

    @Autowired
    private PaymentRecordService paymentRecordService;

    @Autowired
    private CacheManager cacheManager;

    /**
     * 应用启动后执行缓存预热
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("开始缓存预热...");
        
        try {
            // 预热仪表盘数据
            warmupDashboardCache();
            
            // 预热基础统计数据
            warmupBasicStatsCache();
            
            logger.info("缓存预热完成");
        } catch (Exception e) {
            logger.error("缓存预热失败", e);
        }
    }

    /**
     * 预热仪表盘缓存
     */
    private void warmupDashboardCache() {
        try {
            logger.info("预热仪表盘缓存...");
            
            // 预热仪表盘统计数据
            dashboardService.getDashboardStats();
            
            logger.info("仪表盘缓存预热完成");
        } catch (Exception e) {
            logger.error("仪表盘缓存预热失败", e);
        }
    }

    /**
     * 预热基础统计缓存
     */
    private void warmupBasicStatsCache() {
        try {
            logger.info("预热基础统计缓存...");
            
            // 预热学生统计
            studentService.count();
            studentService.countStudentsByGrade();
            
            // 预热课程统计
            courseService.count();
            
            // 预热班级统计
            schoolClassService.count();
            
            // 预热缴费统计
            paymentRecordService.getStatistics();
            
            logger.info("基础统计缓存预热完成");
        } catch (Exception e) {
            logger.error("基础统计缓存预热失败", e);
        }
    }

    /**
     * 清除所有缓存
     */
    public void clearAllCache() {
        try {
            logger.info("清除所有缓存...");
            
            cacheManager.getCacheNames().forEach(cacheName -> {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    cache.clear();
                    logger.debug("已清除缓存: {}", cacheName);
                }
            });
            
            logger.info("所有缓存已清除");
        } catch (Exception e) {
            logger.error("清除缓存失败", e);
        }
    }

    /**
     * 清除指定缓存
     */
    public void clearCache(String cacheName) {
        try {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                logger.info("已清除缓存: {}", cacheName);
            } else {
                logger.warn("缓存不存在: {}", cacheName);
            }
        } catch (Exception e) {
            logger.error("清除缓存失败: {}", cacheName, e);
        }
    }

    /**
     * 获取缓存统计信息
     */
    public void logCacheStats() {
        try {
            logger.info("=== 缓存统计信息 ===");
            
            cacheManager.getCacheNames().forEach(cacheName -> {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    logger.info("缓存名称: {}, 类型: {}", cacheName, cache.getClass().getSimpleName());
                }
            });
            
            logger.info("=== 缓存统计结束 ===");
        } catch (Exception e) {
            logger.error("获取缓存统计失败", e);
        }
    }

    /**
     * 手动刷新仪表盘缓存
     */
    public void refreshDashboardCache() {
        try {
            logger.info("刷新仪表盘缓存...");
            
            // 清除相关缓存
            clearCache("dashboard:stats");
            clearCache("dashboard:service:stats");
            clearCache("dashboard:chart-data");
            clearCache("dashboard:charts");
            clearCache("dashboard:activities");
            clearCache("dashboard:notifications");
            clearCache("dashboard:quick-stats");
            
            // 重新预热
            warmupDashboardCache();
            
            logger.info("仪表盘缓存刷新完成");
        } catch (Exception e) {
            logger.error("仪表盘缓存刷新失败", e);
        }
    }

    /**
     * 手动刷新基础统计缓存
     */
    public void refreshBasicStatsCache() {
        try {
            logger.info("刷新基础统计缓存...");
            
            // 清除相关缓存
            clearCache("student:count");
            clearCache("student:grade-stats");
            clearCache("course:count");
            clearCache("class:count");
            clearCache("payment:stats");
            
            // 重新预热
            warmupBasicStatsCache();
            
            logger.info("基础统计缓存刷新完成");
        } catch (Exception e) {
            logger.error("基础统计缓存刷新失败", e);
        }
    }
}
