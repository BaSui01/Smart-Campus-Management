package com.campus.interfaces.rest.v1;

import com.campus.application.service.CacheWarmupService;
import com.campus.shared.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存管理API控制器
 * 提供缓存管理相关的REST API接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@RestController
@RequestMapping("/api/v1/cache")
@Tag(name = "缓存管理", description = "缓存管理相关的API接口")
public class CacheManagementApiController {

    @Autowired
    private CacheWarmupService cacheWarmupService;

    @Autowired
    private CacheManager cacheManager;

    /**
     * 获取缓存信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取缓存信息", description = "获取系统缓存的基本信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ApiResponse<Map<String, Object>> getCacheInfo() {
        try {
            Map<String, Object> cacheInfo = new HashMap<>();
            
            // 获取缓存名称列表
            cacheInfo.put("cacheNames", cacheManager.getCacheNames());
            cacheInfo.put("cacheCount", cacheManager.getCacheNames().size());
            cacheInfo.put("cacheManagerType", cacheManager.getClass().getSimpleName());
            
            return ApiResponse.success("获取缓存信息成功", cacheInfo);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取缓存信息失败：" + e.getMessage());
        }
    }

    /**
     * 清除所有缓存
     */
    @PostMapping("/clear-all")
    @Operation(summary = "清除所有缓存", description = "清除系统中的所有缓存")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ApiResponse<Void> clearAllCache() {
        try {
            cacheWarmupService.clearAllCache();
            return ApiResponse.success("所有缓存已清除");
        } catch (Exception e) {
            return ApiResponse.error(500, "清除缓存失败：" + e.getMessage());
        }
    }

    /**
     * 清除指定缓存
     */
    @PostMapping("/clear/{cacheName}")
    @Operation(summary = "清除指定缓存", description = "清除指定名称的缓存")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ApiResponse<Void> clearCache(
            @Parameter(description = "缓存名称") @PathVariable String cacheName) {
        try {
            cacheWarmupService.clearCache(cacheName);
            return ApiResponse.success("缓存 " + cacheName + " 已清除");
        } catch (Exception e) {
            return ApiResponse.error(500, "清除缓存失败：" + e.getMessage());
        }
    }

    /**
     * 预热仪表盘缓存
     */
    @PostMapping("/warmup/dashboard")
    @Operation(summary = "预热仪表盘缓存", description = "预热仪表盘相关的缓存数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ApiResponse<Void> warmupDashboardCache() {
        try {
            cacheWarmupService.refreshDashboardCache();
            return ApiResponse.success("仪表盘缓存预热完成");
        } catch (Exception e) {
            return ApiResponse.error(500, "缓存预热失败：" + e.getMessage());
        }
    }

    /**
     * 预热基础统计缓存
     */
    @PostMapping("/warmup/stats")
    @Operation(summary = "预热基础统计缓存", description = "预热基础统计数据的缓存")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ApiResponse<Void> warmupStatsCache() {
        try {
            cacheWarmupService.refreshBasicStatsCache();
            return ApiResponse.success("基础统计缓存预热完成");
        } catch (Exception e) {
            return ApiResponse.error(500, "缓存预热失败：" + e.getMessage());
        }
    }

    /**
     * 获取缓存统计
     */
    @GetMapping("/stats")
    @Operation(summary = "获取缓存统计", description = "获取缓存的统计信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ApiResponse<Map<String, Object>> getCacheStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 获取缓存详细信息
            Map<String, Object> cacheDetails = new HashMap<>();
            cacheManager.getCacheNames().forEach(cacheName -> {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    Map<String, Object> cacheInfo = new HashMap<>();
                    cacheInfo.put("name", cacheName);
                    cacheInfo.put("type", cache.getClass().getSimpleName());
                    cacheInfo.put("nativeCache", cache.getNativeCache().getClass().getSimpleName());
                    cacheDetails.put(cacheName, cacheInfo);
                }
            });
            
            stats.put("totalCaches", cacheManager.getCacheNames().size());
            stats.put("cacheDetails", cacheDetails);
            stats.put("timestamp", System.currentTimeMillis());
            
            // 记录缓存统计到日志
            cacheWarmupService.logCacheStats();
            
            return ApiResponse.success("获取缓存统计成功", stats);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取缓存统计失败：" + e.getMessage());
        }
    }

    /**
     * 检查缓存健康状态
     */
    @GetMapping("/health")
    @Operation(summary = "检查缓存健康状态", description = "检查缓存系统的健康状态")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ApiResponse<Map<String, Object>> checkCacheHealth() {
        try {
            Map<String, Object> health = new HashMap<>();
            
            // 检查缓存管理器是否正常
            boolean cacheManagerHealthy = cacheManager != null;
            health.put("cacheManagerHealthy", cacheManagerHealthy);
            
            // 检查关键缓存是否存在
            String[] keyCaches = {
                "dashboard:stats", "dashboard:chart-data", 
                "student:count", "course:count", "class:count", "payment:stats"
            };
            
            Map<String, Boolean> cacheStatus = new HashMap<>();
            for (String cacheName : keyCaches) {
                var cache = cacheManager.getCache(cacheName);
                cacheStatus.put(cacheName, cache != null);
            }
            health.put("keyCacheStatus", cacheStatus);
            
            // 整体健康状态
            boolean overallHealthy = cacheManagerHealthy && 
                cacheStatus.values().stream().allMatch(status -> status);
            health.put("overallHealthy", overallHealthy);
            health.put("checkTime", System.currentTimeMillis());
            
            return ApiResponse.success("缓存健康检查完成", health);
        } catch (Exception e) {
            return ApiResponse.error(500, "缓存健康检查失败：" + e.getMessage());
        }
    }
}
