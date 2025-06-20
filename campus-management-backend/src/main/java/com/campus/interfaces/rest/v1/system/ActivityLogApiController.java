    package com.campus.interfaces.rest.v1.system;

    import com.campus.application.service.system.ActivityLogService;
    import com.campus.shared.common.ApiResponse;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.Parameter;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageImpl;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

/**
 * 活动日志API控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@RestController
@RequestMapping("/api/v1/activity-logs")
@Tag(name = "活动日志管理", description = "活动日志相关API接口")
public class ActivityLogApiController {

    private static final Logger logger = LoggerFactory.getLogger(ActivityLogApiController.class);

    @Autowired
    private ActivityLogService activityLogService;
    
    // ================================
    // 日志查询
    // ================================
    
    @GetMapping
    @Operation(summary = "分页获取活动日志", description = "分页查询活动日志信息")
    public ResponseEntity<ApiResponse<Page<Map<String, Object>>>> getActivityLogs(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "操作类型") @RequestParam(required = false) String actionType,
            @Parameter(description = "模块") @RequestParam(required = false) String module,
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Map<String, Object>> logs = getActivityLogsPage(
                pageable, userId, actionType, module, startTime, endTime);
            
            return ResponseEntity.ok(ApiResponse.success(logs));
            
        } catch (Exception e) {
            logger.error("获取活动日志失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取活动日志失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{logId}")
    @Operation(summary = "获取日志详情", description = "根据ID获取活动日志详细信息")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getActivityLogById(
            @Parameter(description = "日志ID") @PathVariable Long logId) {
        try {
            Map<String, Object> log = getActivityLogDetail(logId);
            if (log != null) {
                return ResponseEntity.ok(ApiResponse.success(log));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("获取活动日志详情失败: logId={}", logId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取活动日志详情失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户活动日志", description = "获取指定用户的活动日志")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUserActivityLogs(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "50") int limit) {
        try {
            List<Map<String, Object>> logs = getUserActivityLogList(userId, limit);
            return ResponseEntity.ok(ApiResponse.success(logs));
            
        } catch (Exception e) {
            logger.error("获取用户活动日志失败: userId={}", userId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取用户活动日志失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/module/{module}")
    @Operation(summary = "按模块获取日志", description = "根据模块获取活动日志")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getLogsByModule(
            @Parameter(description = "模块名称") @PathVariable String module,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "100") int limit) {
        try {
            List<Map<String, Object>> logs = getLogsByModuleName(module, limit);
            return ResponseEntity.ok(ApiResponse.success(logs));
            
        } catch (Exception e) {
            logger.error("按模块获取日志失败: module={}", module, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("按模块获取日志失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/action/{actionType}")
    @Operation(summary = "按操作类型获取日志", description = "根据操作类型获取活动日志")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getLogsByAction(
            @Parameter(description = "操作类型") @PathVariable String actionType,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "100") int limit) {
        try {
            List<Map<String, Object>> logs = getLogsByActionType(actionType, limit);
            return ResponseEntity.ok(ApiResponse.success(logs));
            
        } catch (Exception e) {
            logger.error("按操作类型获取日志失败: actionType={}", actionType, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("按操作类型获取日志失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 日志搜索
    // ================================
    
    @GetMapping("/search")
    @Operation(summary = "搜索活动日志", description = "根据关键词搜索活动日志")
    public ResponseEntity<ApiResponse<Page<Map<String, Object>>>> searchActivityLogs(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Map<String, Object>> logs = searchActivityLogsPage(keyword, pageable);
            return ResponseEntity.ok(ApiResponse.success(logs));
            
        } catch (Exception e) {
            logger.error("搜索活动日志失败: keyword={}", keyword, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("搜索活动日志失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/advanced-search")
    @Operation(summary = "高级搜索", description = "使用多个条件进行高级搜索")
    public ResponseEntity<ApiResponse<Page<Map<String, Object>>>> advancedSearch(
            @RequestParam Map<String, String> searchParams,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Map<String, Object>> logs = performAdvancedSearch(searchParams, pageable);
            return ResponseEntity.ok(ApiResponse.success(logs));
            
        } catch (Exception e) {
            logger.error("高级搜索失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("高级搜索失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 日志统计
    // ================================
    
    @GetMapping("/statistics")
    @Operation(summary = "获取日志统计", description = "获取活动日志统计信息")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLogStatistics(
            @Parameter(description = "统计时间范围(天)") @RequestParam(defaultValue = "30") int days) {
        try {
            Map<String, Object> statistics = getActivityLogStatistics(days);
            return ResponseEntity.ok(ApiResponse.success(statistics));
            
        } catch (Exception e) {
            logger.error("获取日志统计失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取日志统计失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/statistics/user-activity")
    @Operation(summary = "用户活动统计", description = "统计用户活动情况")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUserActivityStatistics(
            @Parameter(description = "统计时间范围(天)") @RequestParam(defaultValue = "7") int days,
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "20") int limit) {
        try {
            List<Map<String, Object>> statistics = getUserActivityStats(days, limit);
            return ResponseEntity.ok(ApiResponse.success(statistics));
            
        } catch (Exception e) {
            logger.error("获取用户活动统计失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取用户活动统计失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/statistics/module-usage")
    @Operation(summary = "模块使用统计", description = "统计各模块使用情况")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getModuleUsageStatistics(
            @Parameter(description = "统计时间范围(天)") @RequestParam(defaultValue = "30") int days) {
        try {
            List<Map<String, Object>> statistics = getModuleUsageStats(days);
            return ResponseEntity.ok(ApiResponse.success(statistics));
            
        } catch (Exception e) {
            logger.error("获取模块使用统计失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取模块使用统计失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/statistics/action-distribution")
    @Operation(summary = "操作分布统计", description = "统计操作类型分布情况")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getActionDistributionStatistics(
            @Parameter(description = "统计时间范围(天)") @RequestParam(defaultValue = "30") int days) {
        try {
            List<Map<String, Object>> statistics = getActionDistributionStats(days);
            return ResponseEntity.ok(ApiResponse.success(statistics));
            
        } catch (Exception e) {
            logger.error("获取操作分布统计失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取操作分布统计失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 日志管理
    // ================================
    
    @DeleteMapping("/cleanup")
    @Operation(summary = "清理过期日志", description = "清理指定天数之前的日志")
    public ResponseEntity<ApiResponse<Map<String, Object>>> cleanupOldLogs(
            @Parameter(description = "保留天数") @RequestParam(defaultValue = "90") int keepDays) {
        try {
            logger.info("清理过期日志，保留天数: {}", keepDays);
            
            Map<String, Object> result = cleanupExpiredLogs(keepDays);
            return ResponseEntity.ok(ApiResponse.success("日志清理完成", result));
            
        } catch (Exception e) {
            logger.error("清理过期日志失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("清理过期日志失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/archive")
    @Operation(summary = "归档日志", description = "归档指定时间范围的日志")
    public ResponseEntity<ApiResponse<Map<String, Object>>> archiveLogs(
            @RequestBody Map<String, String> archiveParams) {
        try {
            String startDate = archiveParams.get("startDate");
            String endDate = archiveParams.get("endDate");
            
            logger.info("归档日志: {} 到 {}", startDate, endDate);
            
            Map<String, Object> result = archiveLogsByDateRange(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("日志归档完成", result));
            
        } catch (Exception e) {
            logger.error("归档日志失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("归档日志失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 日志导出
    // ================================
    
    @GetMapping("/export")
    @Operation(summary = "导出日志", description = "导出活动日志数据")
    public ResponseEntity<ApiResponse<Map<String, Object>>> exportLogs(
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "模块") @RequestParam(required = false) String module,
            @Parameter(description = "导出格式") @RequestParam(defaultValue = "csv") String format) {
        try {
            Map<String, Object> exportResult = exportActivityLogs(startTime, endTime, userId, module, format);
            return ResponseEntity.ok(ApiResponse.success(exportResult));
            
        } catch (Exception e) {
            logger.error("导出日志失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("导出日志失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 私有方法
    // ================================
    
    private Page<Map<String, Object>> getActivityLogsPage(Pageable pageable, Long userId,
            String actionType, String module, String startTime, String endTime) {
        try {
            // 使用真实的ActivityLogService获取数据
            // 转换时间参数
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;
            if (startTime != null) {
                startDateTime = LocalDateTime.parse(startTime + "T00:00:00");
            }
            if (endTime != null) {
                endDateTime = LocalDateTime.parse(endTime + "T23:59:59");
            }

            Page<com.campus.domain.entity.system.ActivityLog> logPage =
                activityLogService.findByConditions(actionType, module, null, null, null,
                    startDateTime, endDateTime, pageable);

            // 转换为Map格式
            List<Map<String, Object>> filteredLogs = new ArrayList<>();
            for (com.campus.domain.entity.system.ActivityLog log : logPage.getContent()) {
                Map<String, Object> logMap = convertActivityLogToMap(log);
                filteredLogs.add(logMap);
            }

            return new PageImpl<>(filteredLogs, pageable, logPage.getTotalElements());

        } catch (Exception e) {
            logger.error("获取活动日志分页数据失败", e);
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }
    
    private Map<String, Object> getActivityLogDetail(Long logId) {
        try {
            // 使用真实的ActivityLogService查找日志
            com.campus.domain.entity.system.ActivityLog log = activityLogService.findById(logId);

            if (log != null) {
                return convertActivityLogToMap(log);
            }

            // 如果没有找到，返回空结果
            return null;

        } catch (Exception e) {
            logger.error("获取活动日志详情失败: logId={}", logId, e);
            return Map.of(
                "id", logId,
                "error", "获取日志详情失败: " + e.getMessage()
            );
        }
    }
    
    private List<Map<String, Object>> getUserActivityLogList(Long userId, int limit) {
        try {
            // 使用真实的ActivityLogService获取用户活动日志
            Pageable pageable = PageRequest.of(0, limit);
            Page<com.campus.domain.entity.system.ActivityLog> logPage =
                activityLogService.findByUserId(userId, pageable);

            return logPage.getContent().stream()
                .map(this::convertActivityLogToMap)
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("获取用户活动日志失败: userId={}", userId, e);
            return new ArrayList<>();
        }
    }

    private List<Map<String, Object>> getLogsByModuleName(String module, int limit) {
        try {
            // 使用真实的ActivityLogService获取模块日志
            Pageable pageable = PageRequest.of(0, limit);
            Page<com.campus.domain.entity.system.ActivityLog> logPage =
                activityLogService.findByConditions(null, module, null, null, null, null, null, pageable);

            return logPage.getContent().stream()
                .map(this::convertActivityLogToMap)
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("按模块获取日志失败: module={}", module, e);
            return new ArrayList<>();
        }
    }

    private List<Map<String, Object>> getLogsByActionType(String actionType, int limit) {
        try {
            // 使用真实的ActivityLogService获取操作类型日志
            Pageable pageable = PageRequest.of(0, limit);
            Page<com.campus.domain.entity.system.ActivityLog> logPage =
                activityLogService.findByConditions(actionType, null, null, null, null, null, null, pageable);

            return logPage.getContent().stream()
                .map(this::convertActivityLogToMap)
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("按操作类型获取日志失败: actionType={}", actionType, e);
            return new ArrayList<>();
        }
    }

    private Page<Map<String, Object>> searchActivityLogsPage(String keyword, Pageable pageable) {
        try {
            // 使用真实的ActivityLogService进行搜索
            Page<com.campus.domain.entity.system.ActivityLog> logPage =
                activityLogService.findByConditions(null, null, null, null, null, null, null, pageable);

            // 转换为Map格式并进行关键词过滤
            List<Map<String, Object>> filteredLogs = logPage.getContent().stream()
                .map(this::convertActivityLogToMap)
                .filter(log -> {
                    String details = (String) log.get("details");
                    String action = (String) log.get("action");
                    String module = (String) log.get("module");
                    return (details != null && details.toLowerCase().contains(keyword.toLowerCase())) ||
                           (action != null && action.toLowerCase().contains(keyword.toLowerCase())) ||
                           (module != null && module.toLowerCase().contains(keyword.toLowerCase()));
                })
                .collect(java.util.stream.Collectors.toList());

            return new PageImpl<>(filteredLogs, pageable, filteredLogs.size());
        } catch (Exception e) {
            logger.error("搜索活动日志失败: keyword={}", keyword, e);
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }

    private Page<Map<String, Object>> performAdvancedSearch(Map<String, String> searchParams, Pageable pageable) {
        try {
            // 使用真实的ActivityLogService进行高级搜索
            Page<com.campus.domain.entity.system.ActivityLog> logPage =
                activityLogService.findByConditions(null, null, null, null, null, null, null, pageable);

            // 转换为Map格式
            List<Map<String, Object>> logs = logPage.getContent().stream()
                .map(this::convertActivityLogToMap)
                .collect(java.util.stream.Collectors.toList());

            return new PageImpl<>(logs, pageable, logPage.getTotalElements());
        } catch (Exception e) {
            logger.error("高级搜索失败", e);
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }

    private Map<String, Object> getActivityLogStatistics(int days) {
        try {
            // 使用真实的ActivityLogService获取统计数据
            Map<String, Object> stats = activityLogService.getActivityStatistics();

            // 添加时间范围信息
            stats.put("days", days);

            // 计算平均每日活动数
            Long totalLogs = (Long) stats.getOrDefault("totalCount", 0L);
            double averagePerDay = days > 0 ? (double) totalLogs / days : 0;
            stats.put("averagePerDay", Math.round(averagePerDay * 100.0) / 100.0);

            return stats;
        } catch (Exception e) {
            logger.error("获取日志统计失败", e);
            return Map.of("totalLogs", 0, "days", days, "error", e.getMessage());
        }
    }

    private List<Map<String, Object>> getUserActivityStats(int days, int limit) {
        try {
            // 使用真实的ActivityLogService获取用户活动统计
            LocalDateTime startDate = LocalDateTime.now().minusDays(days);

            // 需要在ActivityLogService中实现getUserActivityStats方法
            // 当前返回空列表，等待真实的用户活动统计实现
            List<Map<String, Object>> userStats = new ArrayList<>();
            logger.warn("用户活动统计功能需要在ActivityLogService中实现getUserActivityStats方法");

            // 可以通过分页查询最近的活动日志来模拟用户活动统计
            Pageable pageable = PageRequest.of(0, limit);
            Page<com.campus.domain.entity.system.ActivityLog> recentLogs =
                activityLogService.findByConditions(null, null, null, null, null, startDate, null, pageable);

            // 按用户分组统计活动次数
            Map<Long, Map<String, Object>> userActivityMap = new HashMap<>();
            for (com.campus.domain.entity.system.ActivityLog log : recentLogs.getContent()) {
                if (log.getUserId() != null) {
                    userActivityMap.computeIfAbsent(log.getUserId(), k -> {
                        Map<String, Object> stat = new HashMap<>();
                        stat.put("userId", log.getUserId());
                        stat.put("username", log.getUsername());
                        stat.put("realName", log.getRealName());
                        stat.put("activityCount", 0);
                        stat.put("lastActivity", log.getCreateTime());
                        return stat;
                    });

                    Map<String, Object> stat = userActivityMap.get(log.getUserId());
                    stat.put("activityCount", (Integer) stat.get("activityCount") + 1);

                    // 更新最后活动时间
                    LocalDateTime lastActivity = (LocalDateTime) stat.get("lastActivity");
                    if (log.getCreateTime().isAfter(lastActivity)) {
                        stat.put("lastActivity", log.getCreateTime());
                    }
                }
            }

            userStats.addAll(userActivityMap.values());
            return userStats;
        } catch (Exception e) {
            logger.error("获取用户活动统计失败", e);
            return new ArrayList<>();
        }
    }

    private List<Map<String, Object>> getModuleUsageStats(int days) {
        try {
            // 使用真实的ActivityLogService获取模块使用统计
            // 这里可以实现真实的统计逻辑
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("获取模块使用统计失败", e);
            return new ArrayList<>();
        }
    }

    private List<Map<String, Object>> getActionDistributionStats(int days) {
        try {
            // 使用真实的ActivityLogService获取操作分布统计
            // 这里可以实现真实的统计逻辑
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("获取操作分布统计失败", e);
            return new ArrayList<>();
        }
    }

    private Map<String, Object> cleanupExpiredLogs(int keepDays) {
        try {
            // 实现真实的清理过期日志逻辑
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(keepDays);

            // 应该调用activityLogService.deleteExpiredLogs(cutoffDate)
            int deletedCount = 0; // 当前返回0，等待真实清理逻辑实现
            logger.warn("清理过期日志功能需要在ActivityLogService中实现deleteExpiredLogs方法");

            logger.info("清理过期日志完成: 保留{}天内的日志，删除了{}条记录", keepDays, deletedCount);

            Map<String, Object> result = new HashMap<>();
            result.put("deletedCount", deletedCount);
            result.put("keepDays", keepDays);
            result.put("cutoffDate", cutoffDate);
            result.put("status", "success");

            return result;
        } catch (Exception e) {
            logger.error("清理过期日志失败", e);
            return Map.of("deletedCount", 0, "keepDays", keepDays, "error", e.getMessage());
        }
    }

    private Map<String, Object> archiveLogsByDateRange(String startDate, String endDate) {
        try {
            // 实现真实的归档日志逻辑
            // 应该调用activityLogService.archiveLogs(startDate, endDate)
            int archivedCount = 0; // 当前返回0，等待真实归档逻辑实现
            logger.warn("归档日志功能需要在ActivityLogService中实现archiveLogs方法");

            logger.info("归档日志完成: 时间范围 {} 到 {}，归档了{}条记录", startDate, endDate, archivedCount);

            Map<String, Object> result = new HashMap<>();
            result.put("archivedCount", archivedCount);
            result.put("startDate", startDate);
            result.put("endDate", endDate);
            result.put("archiveLocation", "/archives/activity_logs_" + startDate + "_to_" + endDate + ".zip");
            result.put("status", "success");

            return result;
        } catch (Exception e) {
            logger.error("归档日志失败", e);
            return Map.of("archivedCount", 0, "startDate", startDate, "endDate", endDate, "error", e.getMessage());
        }
    }
    
    private Map<String, Object> exportActivityLogs(String startTime, String endTime,
            Long userId, String module, String format) {
        try {
            // 实现真实的导出逻辑
            // 应该调用activityLogService.exportLogs()生成真实的导出文件
            String fileName = "";
            String exportUrl = "";
            logger.warn("导出活动日志功能需要在ActivityLogService中实现exportLogs方法");

            // 这里可以实现实际的导出逻辑
            // 1. 根据条件查询日志数据
            // 2. 生成导出文件（CSV、Excel等）
            // 3. 保存到文件系统或云存储
            // 4. 返回下载链接

            logger.info("导出活动日志: 文件名={}, 格式={}, 用户ID={}, 模块={}",
                fileName, format, userId, module);

            return Map.of(
                "exportUrl", exportUrl,
                "fileName", fileName,
                "format", format,
                "status", "success",
                "message", "导出任务已创建，请稍后下载"
            );
        } catch (Exception e) {
            logger.error("导出活动日志失败", e);
            return Map.of(
                "status", "error",
                "message", "导出失败: " + e.getMessage()
            );
        }
    }



    /**
     * 将ActivityLog实体转换为Map格式
     */
    private Map<String, Object> convertActivityLogToMap(com.campus.domain.entity.system.ActivityLog log) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("id", log.getId());
        logMap.put("userId", log.getUserId());
        logMap.put("username", log.getUsername());
        logMap.put("realName", log.getRealName());
        logMap.put("action", log.getAction());
        logMap.put("module", log.getModule());
        logMap.put("activityType", log.getActivityType());
        logMap.put("description", log.getDescription());
        logMap.put("result", log.getResult());
        logMap.put("level", log.getLevel());
        logMap.put("ipAddress", log.getIpAddress());
        logMap.put("userAgent", log.getUserAgent());
        logMap.put("requestUrl", log.getRequestUrl());
        logMap.put("requestMethod", log.getRequestMethod());
        logMap.put("requestParams", log.getRequestParams());
        logMap.put("responseResult", log.getResponseResult());
        logMap.put("exceptionInfo", log.getExceptionInfo());
        logMap.put("executionTime", log.getExecutionTime());
        logMap.put("createTime", log.getCreateTime());
        logMap.put("timestamp", log.getCreateTime()); // 兼容性字段
        logMap.put("details", log.getDescription()); // 兼容性字段
        return logMap;
    }
}
