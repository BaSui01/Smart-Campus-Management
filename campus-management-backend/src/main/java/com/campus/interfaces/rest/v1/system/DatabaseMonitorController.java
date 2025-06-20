package com.campus.interfaces.rest.v1.system;

import com.campus.infrastructure.monitoring.DatabasePerformanceMonitor;
import com.campus.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 数据库监控控制器
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Tag(name = "数据库监控", description = "数据库性能监控和优化建议")
@Slf4j
@RestController
@RequestMapping("/api/v1/system/database")
@RequiredArgsConstructor
public class DatabaseMonitorController {

    private final DatabasePerformanceMonitor databasePerformanceMonitor;

    @Operation(summary = "获取数据库性能指标", description = "获取数据库连接、查询、表大小等性能指标")
    @GetMapping("/performance")
    @PreAuthorize("hasAuthority('system:database:monitor')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDatabasePerformance() {
        try {
            Map<String, Object> metrics = databasePerformanceMonitor.getDatabasePerformanceMetrics();
            return ResponseEntity.ok(ApiResponse.success(metrics));
        } catch (Exception e) {
            log.error("获取数据库性能指标失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取数据库性能指标失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "获取优化建议", description = "根据当前数据库状态生成优化建议")
    @GetMapping("/optimization-suggestions")
    @PreAuthorize("hasAuthority('system:database:monitor')")
    public ResponseEntity<ApiResponse<List<String>>> getOptimizationSuggestions() {
        try {
            List<String> suggestions = databasePerformanceMonitor.generateOptimizationSuggestions();
            return ResponseEntity.ok(ApiResponse.success(suggestions));
        } catch (Exception e) {
            log.error("获取优化建议失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取优化建议失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "数据库健康检查", description = "检查数据库连接和基本状态")
    @GetMapping("/health")
    @PreAuthorize("hasAuthority('system:database:monitor')")
    @SuppressWarnings("unchecked")
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        try {
            Map<String, Object> health = new java.util.HashMap<>();
            
            // 获取基本性能指标
            Map<String, Object> metrics = databasePerformanceMonitor.getDatabasePerformanceMetrics();
            
            // 判断健康状态
            boolean isHealthy = true;
            StringBuilder message = new StringBuilder("数据库运行正常");
            
            // 检查连接使用率
            Map<String, Object> connections = (Map<String, Object>) metrics.get("connections");
            if (connections != null) {
                Double usage = (Double) connections.get("connectionUsage");
                if (usage != null && usage > 90) {
                    isHealthy = false;
                    message.append("，连接使用率过高(").append(usage).append("%)");
                }
            }
            
            // 检查慢查询
            Map<String, Object> slowQueries = (Map<String, Object>) metrics.get("slowQueries");
            if (slowQueries != null) {
                Long slowCount = (Long) slowQueries.get("slowQueriesCount");
                if (slowCount != null && slowCount > 200) {
                    isHealthy = false;
                    message.append("，慢查询过多(").append(slowCount).append(")");
                }
            }
            
            health.put("status", isHealthy ? "healthy" : "warning");
            health.put("message", message.toString());
            health.put("timestamp", System.currentTimeMillis());
            health.put("metrics", metrics);
            
            return ResponseEntity.ok(ApiResponse.success(health));
        } catch (Exception e) {
            log.error("数据库健康检查失败", e);
            return ResponseEntity.ok(ApiResponse.error("数据库健康检查失败: " + e.getMessage()));
        }
    }
}
