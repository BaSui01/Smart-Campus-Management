package com.campus.application.service.impl;

import com.campus.application.service.CacheWarmupService;
import com.campus.application.service.UserService;
import com.campus.application.service.CourseService;
import com.campus.application.service.DepartmentService;
import com.campus.application.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热服务实现类
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Service
public class CacheWarmupServiceImpl implements CacheWarmupService {
    
    private static final Logger logger = LoggerFactory.getLogger(CacheWarmupServiceImpl.class);
    
    @Autowired
    private CacheManager cacheManager;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private PermissionService permissionService;
    
    @Override
    public void warmupCache() {
        logger.info("开始执行全量缓存预热");
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 并行执行各种缓存预热
            CompletableFuture<Void> userCacheFuture = CompletableFuture.runAsync(this::warmupUserCache);
            CompletableFuture<Void> courseCacheFuture = CompletableFuture.runAsync(this::warmupCourseCache);
            CompletableFuture<Void> departmentCacheFuture = CompletableFuture.runAsync(this::warmupDepartmentCache);
            CompletableFuture<Void> permissionCacheFuture = CompletableFuture.runAsync(this::warmupPermissionCache);
            CompletableFuture<Void> configCacheFuture = CompletableFuture.runAsync(this::warmupConfigCache);
            
            // 等待所有缓存预热完成
            CompletableFuture.allOf(
                userCacheFuture,
                courseCacheFuture,
                departmentCacheFuture,
                permissionCacheFuture,
                configCacheFuture
            ).join();
            
            long endTime = System.currentTimeMillis();
            logger.info("全量缓存预热完成，耗时: {}ms", endTime - startTime);
            
        } catch (Exception e) {
            logger.error("缓存预热失败", e);
            throw new RuntimeException("缓存预热失败", e);
        }
    }
    
    @Override
    public void warmupDashboardCache() {
        logger.info("开始预热仪表盘缓存");
        try {
            warmupUserCache();
            warmupCourseCache();
            warmupBasicStatsCache();
            logger.info("仪表盘缓存预热完成");
        } catch (Exception e) {
            logger.error("仪表盘缓存预热失败", e);
        }
    }

    @Override
    public void warmupBasicStatsCache() {
        logger.info("开始预热基础统计缓存");
        try {
            warmupUserCache();
            warmupCourseCache();
            warmupDepartmentCache();
            logger.info("基础统计缓存预热完成");
        } catch (Exception e) {
            logger.error("基础统计缓存预热失败", e);
        }
    }

    @Override
    public void clearAllCache() {
        clearAllCaches();
    }

    @Override
    public void clearCache(String cacheName) {
        clearCacheByPattern(cacheName);
    }

    @Override
    public void logCacheStats() {
        logger.info("缓存统计信息: {}", getCacheStatistics());
    }

    @Override
    public void refreshDashboardCache() {
        logger.info("刷新仪表盘缓存");
        clearCacheByPattern("dashboard:*");
        warmupDashboardCache();
    }

    @Override
    public void refreshBasicStatsCache() {
        logger.info("刷新基础统计缓存");
        clearCacheByPattern("stats:*");
        warmupBasicStatsCache();
    }

    // ================================
    // 辅助方法
    // ================================

    public void warmupUserCache() {
        logger.info("开始预热用户缓存");
        
        try {
            // 预热活跃用户数据
            userService.findActiveUsers().forEach(user -> {
                String cacheKey = "user:" + user.getId();
                redisTemplate.opsForValue().set(cacheKey, user, 30, TimeUnit.MINUTES);
            });
            
            // 预热用户权限数据
            userService.findAllUsers().forEach(user -> {
                try {
                    String permissionKey = "user:permissions:" + user.getId();
                    Object permissions = userService.getUserPermissions(user.getId());
                    redisTemplate.opsForValue().set(permissionKey, permissions, 60, TimeUnit.MINUTES);
                } catch (Exception e) {
                    logger.warn("预热用户权限缓存失败: userId={}", user.getId(), e);
                }
            });
            
            logger.info("用户缓存预热完成");
            
        } catch (Exception e) {
            logger.error("用户缓存预热失败", e);
        }
    }
    
    public void warmupCourseCache() {
        logger.info("开始预热课程缓存");
        
        try {
            // 预热活跃课程数据
            courseService.findActiveCourses().forEach(course -> {
                String cacheKey = "course:" + course.getId();
                redisTemplate.opsForValue().set(cacheKey, course, 60, TimeUnit.MINUTES);
            });
            
            // 预热课程统计数据
            String statsKey = "course:statistics";
            Object courseStats = courseService.getCourseStatistics();
            redisTemplate.opsForValue().set(statsKey, courseStats, 120, TimeUnit.MINUTES);
            
            logger.info("课程缓存预热完成");
            
        } catch (Exception e) {
            logger.error("课程缓存预热失败", e);
        }
    }
    
    public void warmupDepartmentCache() {
        logger.info("开始预热院系缓存");
        
        try {
            // 预热院系数据
            departmentService.findActiveDepartments().forEach(department -> {
                String cacheKey = "department:" + department.getId();
                redisTemplate.opsForValue().set(cacheKey, department, 120, TimeUnit.MINUTES);
            });
            
            // 预热院系层级结构
            String hierarchyKey = "department:hierarchy";
            Object hierarchy = departmentService.getDepartmentHierarchy();
            redisTemplate.opsForValue().set(hierarchyKey, hierarchy, 240, TimeUnit.MINUTES);
            
            logger.info("院系缓存预热完成");
            
        } catch (Exception e) {
            logger.error("院系缓存预热失败", e);
        }
    }
    
    public void warmupPermissionCache() {
        logger.info("开始预热权限缓存");
        
        try {
            // 预热权限数据
            permissionService.findAllPermissions().forEach(permission -> {
                String cacheKey = "permission:" + permission.getId();
                redisTemplate.opsForValue().set(cacheKey, permission, 240, TimeUnit.MINUTES);
            });
            
            // 预热权限树结构
            String treeKey = "permission:tree";
            Object permissionTree = permissionService.getPermissionTree();
            redisTemplate.opsForValue().set(treeKey, permissionTree, 480, TimeUnit.MINUTES);
            
            logger.info("权限缓存预热完成");
            
        } catch (Exception e) {
            logger.error("权限缓存预热失败", e);
        }
    }
    
    public void warmupConfigCache() {
        logger.info("开始预热配置缓存");
        
        try {
            // 预热系统配置
            String configKey = "system:config";
            Object systemConfig = getSystemConfiguration();
            redisTemplate.opsForValue().set(configKey, systemConfig, 600, TimeUnit.MINUTES);
            
            // 预热常用字典数据
            warmupDictionaryCache();
            
            logger.info("配置缓存预热完成");
            
        } catch (Exception e) {
            logger.error("配置缓存预热失败", e);
        }
    }
    
    public void clearAllCaches() {
        logger.info("开始清理所有缓存");
        
        try {
            // 清理Spring Cache
            cacheManager.getCacheNames().forEach(cacheName -> {
                cacheManager.getCache(cacheName).clear();
            });
            
            // 清理Redis缓存
            clearRedisCache();
            
            logger.info("所有缓存清理完成");
            
        } catch (Exception e) {
            logger.error("清理缓存失败", e);
            throw new RuntimeException("清理缓存失败", e);
        }
    }
    
    public void clearCacheByPattern(String pattern) {
        logger.info("按模式清理缓存: {}", pattern);
        
        try {
            redisTemplate.delete(redisTemplate.keys(pattern));
            logger.info("模式缓存清理完成: {}", pattern);
            
        } catch (Exception e) {
            logger.error("模式缓存清理失败: {}", pattern, e);
        }
    }
    
    public boolean isCacheWarmedUp() {
        try {
            // 检查关键缓存是否存在
            boolean userCacheExists = redisTemplate.hasKey("user:*");
            boolean courseCacheExists = redisTemplate.hasKey("course:*");
            boolean configCacheExists = redisTemplate.hasKey("system:config");
            
            return userCacheExists && courseCacheExists && configCacheExists;
            
        } catch (Exception e) {
            logger.error("检查缓存预热状态失败", e);
            return false;
        }
    }
    
    public Object getCacheStatistics() {
        try {
            Long totalKeys = redisTemplate.execute((org.springframework.data.redis.core.RedisCallback<Long>) connection -> connection.dbSize());
            
            return new Object() {
                public final long totalCacheKeys = totalKeys;
                public final boolean isWarmedUp = isCacheWarmedUp();
                public final String status = isWarmedUp ? "已预热" : "未预热";
            };
            
        } catch (Exception e) {
            logger.error("获取缓存统计失败", e);
            return new Object() {
                public final String error = "获取统计失败";
            };
        }
    }
    
    /**
     * 预热字典缓存
     */
    private void warmupDictionaryCache() {
        try {
            // 预热常用字典数据
            String[] dictTypes = {"user_status", "course_type", "grade_level", "semester"};
            
            for (String dictType : dictTypes) {
                String dictKey = "dict:" + dictType;
                Object dictData = getDictionaryData(dictType);
                redisTemplate.opsForValue().set(dictKey, dictData, 720, TimeUnit.MINUTES);
            }
            
        } catch (Exception e) {
            logger.error("字典缓存预热失败", e);
        }
    }
    
    /**
     * 清理Redis缓存
     */
    private void clearRedisCache() {
        try {
            String[] patterns = {"user:*", "course:*", "department:*", "permission:*", "system:*", "dict:*"};
            
            for (String pattern : patterns) {
                redisTemplate.delete(redisTemplate.keys(pattern));
            }
            
        } catch (Exception e) {
            logger.error("清理Redis缓存失败", e);
        }
    }
    
    /**
     * 获取系统配置
     */
    private Object getSystemConfiguration() {
        // TODO: 实现系统配置获取逻辑
        return new Object() {
            public final String version = "1.0.0";
            public final String environment = "production";
            public final boolean maintenanceMode = false;
        };
    }
    
    /**
     * 获取字典数据
     */
    private Object getDictionaryData(String dictType) {
        // TODO: 实现字典数据获取逻辑
        return new Object() {
            public final String type = dictType;
            public final String status = "loaded";
        };
    }
}
