    package com.campus.interfaces.rest.v1.system;

    import com.campus.shared.common.ApiResponse;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.Parameter;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
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
            List<Map<String, Object>> allLogs = generateMockActivityLogs();
            List<Map<String, Object>> filteredLogs = new ArrayList<>();

            // 应用过滤条件
            for (Map<String, Object> log : allLogs) {
                boolean matches = true;

                // 用户ID过滤
                if (userId != null && !userId.equals(log.get("userId"))) {
                    matches = false;
                }

                // 操作类型过滤
                if (actionType != null && !actionType.trim().isEmpty() &&
                    !actionType.equals(log.get("action"))) {
                    matches = false;
                }

                // 模块过滤
                if (module != null && !module.trim().isEmpty() &&
                    !module.equals(log.get("module"))) {
                    matches = false;
                }

                // 时间范围过滤
                if (startTime != null || endTime != null) {
                    LocalDateTime logTime = (LocalDateTime) log.get("timestamp");
                    if (startTime != null) {
                        LocalDateTime start = LocalDateTime.parse(startTime + "T00:00:00");
                        if (logTime.isBefore(start)) {
                            matches = false;
                        }
                    }
                    if (endTime != null) {
                        LocalDateTime end = LocalDateTime.parse(endTime + "T23:59:59");
                        if (logTime.isAfter(end)) {
                            matches = false;
                        }
                    }
                }

                if (matches) {
                    filteredLogs.add(log);
                }
            }

            // 手动分页
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), filteredLogs.size());
            List<Map<String, Object>> pageContent = start < filteredLogs.size() ?
                filteredLogs.subList(start, end) : new ArrayList<>();

            return new PageImpl<>(pageContent, pageable, filteredLogs.size());

        } catch (Exception e) {
            logger.error("获取活动日志分页数据失败", e);
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }
    
    private Map<String, Object> getActivityLogDetail(Long logId) {
        try {
            // 从模拟数据中查找指定ID的日志
            List<Map<String, Object>> allLogs = generateMockActivityLogs();

            for (Map<String, Object> log : allLogs) {
                if (logId.equals(log.get("id"))) {
                    // 返回详细的日志信息
                    Map<String, Object> detailLog = new HashMap<>();
                    detailLog.put("id", log.get("id"));
                    detailLog.put("userId", log.get("userId"));
                    detailLog.put("action", log.get("action"));
                    detailLog.put("module", log.get("module"));
                    detailLog.put("timestamp", log.get("timestamp"));
                    detailLog.put("details", log.get("details"));
                    detailLog.put("ipAddress", log.get("ipAddress"));
                    detailLog.put("userAgent", log.get("userAgent"));
                    detailLog.put("status", "success");
                    detailLog.put("duration", "120ms");
                    detailLog.put("requestUrl", "/api/v1/example");
                    detailLog.put("responseCode", 200);
                    return detailLog;
                }
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
            List<Map<String, Object>> allLogs = generateMockActivityLogs();
            return allLogs.stream()
                .filter(log -> userId.equals(log.get("userId")))
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("获取用户活动日志失败: userId={}", userId, e);
            return new ArrayList<>();
        }
    }

    private List<Map<String, Object>> getLogsByModuleName(String module, int limit) {
        try {
            List<Map<String, Object>> allLogs = generateMockActivityLogs();
            return allLogs.stream()
                .filter(log -> module.equals(log.get("module")))
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("按模块获取日志失败: module={}", module, e);
            return new ArrayList<>();
        }
    }

    private List<Map<String, Object>> getLogsByActionType(String actionType, int limit) {
        try {
            List<Map<String, Object>> allLogs = generateMockActivityLogs();
            return allLogs.stream()
                .filter(log -> actionType.equals(log.get("action")))
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("按操作类型获取日志失败: actionType={}", actionType, e);
            return new ArrayList<>();
        }
    }

    private Page<Map<String, Object>> searchActivityLogsPage(String keyword, Pageable pageable) {
        try {
            List<Map<String, Object>> allLogs = generateMockActivityLogs();
            List<Map<String, Object>> filteredLogs = allLogs.stream()
                .filter(log -> {
                    String details = (String) log.get("details");
                    String action = (String) log.get("action");
                    String module = (String) log.get("module");
                    return (details != null && details.toLowerCase().contains(keyword.toLowerCase())) ||
                           (action != null && action.toLowerCase().contains(keyword.toLowerCase())) ||
                           (module != null && module.toLowerCase().contains(keyword.toLowerCase()));
                })
                .collect(java.util.stream.Collectors.toList());

            // 手动分页
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), filteredLogs.size());
            List<Map<String, Object>> pageContent = start < filteredLogs.size() ?
                filteredLogs.subList(start, end) : new ArrayList<>();

            return new PageImpl<>(pageContent, pageable, filteredLogs.size());
        } catch (Exception e) {
            logger.error("搜索活动日志失败: keyword={}", keyword, e);
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }

    private Page<Map<String, Object>> performAdvancedSearch(Map<String, String> searchParams, Pageable pageable) {
        try {
            List<Map<String, Object>> allLogs = generateMockActivityLogs();
            List<Map<String, Object>> filteredLogs = new ArrayList<>();

            for (Map<String, Object> log : allLogs) {
                boolean matches = true;

                // 检查每个搜索参数
                for (Map.Entry<String, String> param : searchParams.entrySet()) {
                    String key = param.getKey();
                    String value = param.getValue();

                    if (value != null && !value.trim().isEmpty()) {
                        Object logValue = log.get(key);
                        if (logValue == null || !logValue.toString().toLowerCase().contains(value.toLowerCase())) {
                            matches = false;
                            break;
                        }
                    }
                }

                if (matches) {
                    filteredLogs.add(log);
                }
            }

            // 手动分页
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), filteredLogs.size());
            List<Map<String, Object>> pageContent = start < filteredLogs.size() ?
                filteredLogs.subList(start, end) : new ArrayList<>();

            return new PageImpl<>(pageContent, pageable, filteredLogs.size());
        } catch (Exception e) {
            logger.error("高级搜索失败", e);
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }

    private Map<String, Object> getActivityLogStatistics(int days) {
        try {
            List<Map<String, Object>> allLogs = generateMockActivityLogs();
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);

            long totalLogs = allLogs.stream()
                .filter(log -> {
                    LocalDateTime timestamp = (LocalDateTime) log.get("timestamp");
                    return timestamp.isAfter(cutoffDate);
                })
                .count();

            Map<String, Long> actionStats = allLogs.stream()
                .filter(log -> {
                    LocalDateTime timestamp = (LocalDateTime) log.get("timestamp");
                    return timestamp.isAfter(cutoffDate);
                })
                .collect(java.util.stream.Collectors.groupingBy(
                    log -> (String) log.get("action"),
                    java.util.stream.Collectors.counting()
                ));

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalLogs", totalLogs);
            stats.put("days", days);
            stats.put("actionStats", actionStats);
            stats.put("averagePerDay", totalLogs / Math.max(days, 1));

            return stats;
        } catch (Exception e) {
            logger.error("获取日志统计失败", e);
            return Map.of("totalLogs", 0, "days", days, "error", e.getMessage());
        }
    }

    private List<Map<String, Object>> getUserActivityStats(int days, int limit) {
        try {
            List<Map<String, Object>> allLogs = generateMockActivityLogs();
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);

            Map<Long, Long> userActivityCount = allLogs.stream()
                .filter(log -> {
                    LocalDateTime timestamp = (LocalDateTime) log.get("timestamp");
                    return timestamp.isAfter(cutoffDate);
                })
                .collect(java.util.stream.Collectors.groupingBy(
                    log -> (Long) log.get("userId"),
                    java.util.stream.Collectors.counting()
                ));

            return userActivityCount.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> userStat = new HashMap<>();
                    userStat.put("userId", entry.getKey());
                    userStat.put("activityCount", entry.getValue());
                    userStat.put("averagePerDay", entry.getValue() / Math.max(days, 1));
                    return userStat;
                })
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("获取用户活动统计失败", e);
            return new ArrayList<>();
        }
    }

    private List<Map<String, Object>> getModuleUsageStats(int days) {
        try {
            List<Map<String, Object>> allLogs = generateMockActivityLogs();
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);

            Map<String, Long> moduleUsage = allLogs.stream()
                .filter(log -> {
                    LocalDateTime timestamp = (LocalDateTime) log.get("timestamp");
                    return timestamp.isAfter(cutoffDate);
                })
                .collect(java.util.stream.Collectors.groupingBy(
                    log -> (String) log.get("module"),
                    java.util.stream.Collectors.counting()
                ));

            return moduleUsage.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(entry -> {
                    Map<String, Object> moduleStat = new HashMap<>();
                    moduleStat.put("module", entry.getKey());
                    moduleStat.put("usageCount", entry.getValue());
                    moduleStat.put("percentage", (entry.getValue() * 100.0) / allLogs.size());
                    return moduleStat;
                })
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("获取模块使用统计失败", e);
            return new ArrayList<>();
        }
    }

    private List<Map<String, Object>> getActionDistributionStats(int days) {
        try {
            List<Map<String, Object>> allLogs = generateMockActivityLogs();
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);

            Map<String, Long> actionDistribution = allLogs.stream()
                .filter(log -> {
                    LocalDateTime timestamp = (LocalDateTime) log.get("timestamp");
                    return timestamp.isAfter(cutoffDate);
                })
                .collect(java.util.stream.Collectors.groupingBy(
                    log -> (String) log.get("action"),
                    java.util.stream.Collectors.counting()
                ));

            return actionDistribution.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(entry -> {
                    Map<String, Object> actionStat = new HashMap<>();
                    actionStat.put("action", entry.getKey());
                    actionStat.put("count", entry.getValue());
                    actionStat.put("percentage", (entry.getValue() * 100.0) / allLogs.size());
                    return actionStat;
                })
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("获取操作分布统计失败", e);
            return new ArrayList<>();
        }
    }

    private Map<String, Object> cleanupExpiredLogs(int keepDays) {
        try {
            // 模拟清理过期日志的逻辑
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(keepDays);

            // 在实际实现中，这里会删除数据库中的过期日志
            // 现在只是模拟返回清理结果
            int deletedCount = (int) (Math.random() * 100) + 50; // 模拟删除50-150条记录

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
            // 模拟归档日志的逻辑
            // 在实际实现中，这里会将指定时间范围的日志移动到归档表或文件
            int archivedCount = (int) (Math.random() * 500) + 200; // 模拟归档200-700条记录

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
            // 模拟导出逻辑
            String fileName = "activity_logs_" + System.currentTimeMillis() + "." + format;
            String exportUrl = "/downloads/" + fileName;

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
     * 生成模拟活动日志数据
     */
    private List<Map<String, Object>> generateMockActivityLogs() {
        List<Map<String, Object>> logs = new ArrayList<>();

        // 模拟不同类型的活动日志
        String[] actions = {"LOGIN", "LOGOUT", "CREATE", "UPDATE", "DELETE", "VIEW", "DOWNLOAD"};
        String[] modules = {"USER", "COURSE", "STUDENT", "TEACHER", "SYSTEM", "REPORT"};
        Long[] userIds = {1L, 2L, 3L, 4L, 5L};

        for (int i = 1; i <= 100; i++) {
            Map<String, Object> log = Map.of(
                "id", (long) i,
                "userId", userIds[i % userIds.length],
                "action", actions[i % actions.length],
                "module", modules[i % modules.length],
                "timestamp", LocalDateTime.now().minusHours(i),
                "details", "模拟活动日志详情 " + i,
                "ipAddress", "192.168.1." + (i % 255 + 1),
                "userAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
            );
            logs.add(log);
        }

        return logs;
    }
}
