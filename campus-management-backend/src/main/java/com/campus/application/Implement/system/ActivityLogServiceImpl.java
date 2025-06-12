package com.campus.application.Implement.system;

import com.campus.application.service.system.ActivityLogService;
import com.campus.domain.entity.system.ActivityLog;
import com.campus.domain.repository.system.ActivityLogRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 活动日志服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-01
 */
@Service
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @Override
    public ActivityLog save(ActivityLog activityLog) {
        try {
            if (activityLog.getCreateTime() == null) {
                activityLog.setCreateTime(LocalDateTime.now());
            }
            return activityLogRepository.save(activityLog);
        } catch (Exception e) {
            System.err.println("保存活动日志失败: " + e.getMessage());
            throw new RuntimeException("保存活动日志失败", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityLog findById(Long id) {
        return activityLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("活动日志不存在"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLog> findAll(Pageable pageable) {
        try {
            return activityLogRepository.findAll(pageable);
        } catch (Exception e) {
            System.err.println("查询活动日志失败: " + e.getMessage());
            throw new RuntimeException("查询活动日志失败", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLog> findByConditions(String activityType, String module, String level,
                                            String result, String username,
                                            LocalDateTime startTime, LocalDateTime endTime,
                                            Pageable pageable) {
        try {
            return activityLogRepository.findByConditions(
                    activityType, module, level, result, username,
                    startTime, endTime, pageable);
        } catch (Exception e) {
            System.err.println("条件查询活动日志失败: " + e.getMessage());
            throw new RuntimeException("条件查询活动日志失败", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLog> findByUserId(Long userId, Pageable pageable) {
        try {
            return activityLogRepository.findByUserId(userId, pageable);
        } catch (Exception e) {
            System.err.println("查询用户活动日志失败: " + e.getMessage());
            throw new RuntimeException("查询用户活动日志失败", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityLog> getRecentActivities(int limit) {
        try {
            Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createTime"));
            return activityLogRepository.findAll(pageable).getContent();
        } catch (Exception e) {
            System.err.println("获取最近活动失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getActivityStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 总数统计
            stats.put("totalCount", activityLogRepository.count());
            stats.put("todayCount", countTodayActivities());
            stats.put("weekCount", countThisWeekActivities());
            stats.put("monthCount", countThisMonthActivities());
            
            // 按类型统计
            List<Object[]> typeStats = activityLogRepository.countByActivityType();
            Map<String, Long> typeMap = typeStats.stream()
                    .collect(Collectors.toMap(
                            arr -> (String) arr[0],
                            arr -> (Long) arr[1]
                    ));
            stats.put("typeStats", typeMap);
            
            // 按级别统计
            List<Object[]> levelStats = activityLogRepository.countByLevel();
            Map<String, Long> levelMap = levelStats.stream()
                    .collect(Collectors.toMap(
                            arr -> (String) arr[0],
                            arr -> (Long) arr[1]
                    ));
            stats.put("levelStats", levelMap);
            
            // 按结果统计
            List<Object[]> resultStats = activityLogRepository.countByResult();
            Map<String, Long> resultMap = resultStats.stream()
                    .collect(Collectors.toMap(
                            arr -> (String) arr[0],
                            arr -> (Long) arr[1]
                    ));
            stats.put("resultStats", resultMap);
            
            return stats;
        } catch (Exception e) {
            System.err.println("获取活动统计失败: " + e.getMessage());
            return new HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDailyActivityStats(int days) {
        try {
            LocalDateTime startDate = LocalDateTime.now().minusDays(days);
            List<Object[]> stats = activityLogRepository.getDailyActivityStats(startDate);
            
            return stats.stream()
                    .map(arr -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("date", arr[0]);
                        map.put("count", arr[1]);
                        return map;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("获取每日活动统计失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUserActivityRanking(int days, int limit) {
        try {
            LocalDateTime startDate = LocalDateTime.now().minusDays(days);
            List<Object[]> ranking = activityLogRepository.getUserActivityRanking(startDate);
            
            return ranking.stream()
                    .limit(limit)
                    .map(arr -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("username", arr[0]);
                        map.put("realName", arr[1]);
                        map.put("count", arr[2]);
                        return map;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("获取用户活动排行榜失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getModuleActionStats(int days) {
        try {
            LocalDateTime startDate = LocalDateTime.now().minusDays(days);
            List<Object[]> stats = activityLogRepository.getModuleActionStats(startDate);
            
            return stats.stream()
                    .map(arr -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("module", arr[0]);
                        map.put("action", arr[1]);
                        map.put("count", arr[2]);
                        return map;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("获取模块操作统计失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void recordLoginLog(Long userId, String username, String realName, String ipAddress, String userAgent) {
        try {
            ActivityLog log = ActivityLog.createLoginLog(userId, username, realName, ipAddress);
            log.setUserAgent(userAgent);
            save(log);
        } catch (Exception e) {
            System.err.println("记录登录日志失败: " + e.getMessage());
            // 不抛出异常，避免影响登录流程
        }
    }

    @Override
    public void recordLogoutLog(Long userId, String username, String realName, String ipAddress) {
        try {
            ActivityLog log = ActivityLog.createLogoutLog(userId, username, realName, ipAddress);
            save(log);
        } catch (Exception e) {
            System.err.println("记录登出日志失败: " + e.getMessage());
            // 不抛出异常，避免影响登出流程
        }
    }

    @Override
    public void recordOperationLog(Long userId, String username, String realName, String module,
                                 String action, String description, String targetType,
                                 Long targetId, String targetName, String ipAddress,
                                 String requestPath, String requestMethod) {
        try {
            ActivityLog log = ActivityLog.createOperationLog(userId, username, realName, module, action, description, targetType, targetId, targetName);
            log.setIpAddress(ipAddress);
            log.setRequestPath(requestPath);
            log.setRequestMethod(requestMethod);
            save(log);
        } catch (Exception e) {
            System.err.println("记录操作日志失败: " + e.getMessage());
            // 不抛出异常，避免影响业务流程
        }
    }

    @Override
    public void recordErrorLog(Long userId, String username, String realName, String module,
                             String action, String description, String errorMessage,
                             String ipAddress, String requestPath) {
        try {
            ActivityLog log = new ActivityLog();
            log.setUserId(userId);
            log.setUsername(username);
            log.setRealName(realName);
            log.setActivityType("ERROR");
            log.setModule(module);
            log.setAction(action);
            log.setDescription(description);
            log.setErrorMessage(errorMessage);
            log.setResult("FAILED");
            log.setLevel("ERROR");
            log.setIpAddress(ipAddress);
            log.setRequestPath(requestPath);
            log.setCreateTime(LocalDateTime.now());
            save(log);
        } catch (Exception e) {
            System.err.println("记录错误日志失败: " + e.getMessage());
            // 不抛出异常，避免影响业务流程
        }
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        try {
            activityLogRepository.deleteAllById(ids);
        } catch (Exception e) {
            System.err.println("批量删除活动日志失败: " + e.getMessage());
            throw new RuntimeException("批量删除活动日志失败", e);
        }
    }

    @Override
    public void cleanExpiredLogs(int daysToKeep) {
        try {
            LocalDateTime expireTime = LocalDateTime.now().minusDays(daysToKeep);
            activityLogRepository.deleteByCreateTimeBefore(expireTime);
        } catch (Exception e) {
            System.err.println("清理过期日志失败: " + e.getMessage());
            throw new RuntimeException("清理过期日志失败", e);
        }
    }

    @Override
    public byte[] exportActivityLogs(String activityType, String module, String level,
                                   String result, String username,
                                   LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // 注意：当前实现基础的CSV导出功能，后续可扩展为Excel等格式
            // 使用大分页获取所有符合条件的日志记录
            Pageable pageable = PageRequest.of(0, 10000, Sort.by(Sort.Direction.DESC, "createTime"));
            Page<ActivityLog> logPage = findByConditions(activityType, module, level, result, username, startTime, endTime, pageable);
            List<ActivityLog> logs = logPage.getContent();

            StringBuilder csvContent = new StringBuilder();
            // CSV头部
            csvContent.append("ID,活动类型,模块,级别,结果,用户名,IP地址,描述,创建时间\n");

            // 数据行
            for (ActivityLog log : logs) {
                csvContent.append(String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    log.getId(),
                    escapeCSV(log.getActivityType()),
                    escapeCSV(log.getModule()),
                    escapeCSV(log.getLevel()),
                    escapeCSV(log.getResult()),
                    escapeCSV(log.getUsername()),
                    escapeCSV(log.getIpAddress()),
                    escapeCSV(log.getDescription()),
                    log.getCreatedAt() != null ? log.getCreatedAt().toString() : ""
                ));
            }

            return csvContent.toString().getBytes("UTF-8");
        } catch (Exception e) {
            System.err.println("导出活动日志失败: " + e.getMessage());
            throw new RuntimeException("导出活动日志失败", e);
        }
    }

    /**
     * CSV字段转义处理
     */
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        // 如果包含逗号、引号或换行符，需要用引号包围并转义内部引号
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityLog> getFailedOperations() {
        try {
            return activityLogRepository.findFailedOperations();
        } catch (Exception e) {
            System.err.println("获取失败操作日志失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityLog> getErrorLogs() {
        try {
            return activityLogRepository.findErrorLogs();
        } catch (Exception e) {
            System.err.println("获取错误日志失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        try {
            return activityLogRepository.count();
        } catch (Exception e) {
            System.err.println("统计活动日志总数失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countTodayActivities() {
        try {
            return activityLogRepository.countTodayActivities();
        } catch (Exception e) {
            System.err.println("统计今日活动数量失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countThisWeekActivities() {
        try {
            return activityLogRepository.countThisWeekActivities();
        } catch (Exception e) {
            System.err.println("统计本周活动数量失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countThisMonthActivities() {
        try {
            return activityLogRepository.countThisMonthActivities();
        } catch (Exception e) {
            System.err.println("统计本月活动数量失败: " + e.getMessage());
            return 0;
        }
    }
}
