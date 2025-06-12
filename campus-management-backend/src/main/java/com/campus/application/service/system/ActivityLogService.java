package com.campus.application.service.system;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.campus.domain.entity.system.ActivityLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 活动日志服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public interface ActivityLogService {

    /**
     * 保存活动日志
     */
    ActivityLog save(ActivityLog activityLog);

    /**
     * 根据ID查询活动日志
     */
    ActivityLog findById(Long id);

    /**
     * 分页查询所有活动日志
     */
    Page<ActivityLog> findAll(Pageable pageable);

    /**
     * 根据条件查询活动日志
     */
    Page<ActivityLog> findByConditions(String activityType, String module, String level, 
                                     String result, String username, 
                                     LocalDateTime startTime, LocalDateTime endTime, 
                                     Pageable pageable);

    /**
     * 根据用户ID查询活动日志
     */
    Page<ActivityLog> findByUserId(Long userId, Pageable pageable);

    /**
     * 获取最近的活动日志
     */
    List<ActivityLog> getRecentActivities(int limit);

    /**
     * 获取活动统计信息
     */
    Map<String, Object> getActivityStatistics();

    /**
     * 获取每日活动统计
     */
    List<Map<String, Object>> getDailyActivityStats(int days);

    /**
     * 获取用户活动排行榜
     */
    List<Map<String, Object>> getUserActivityRanking(int days, int limit);

    /**
     * 获取模块操作统计
     */
    List<Map<String, Object>> getModuleActionStats(int days);

    /**
     * 记录登录日志
     */
    void recordLoginLog(Long userId, String username, String realName, String ipAddress, String userAgent);

    /**
     * 记录登出日志
     */
    void recordLogoutLog(Long userId, String username, String realName, String ipAddress);

    /**
     * 记录操作日志
     */
    void recordOperationLog(Long userId, String username, String realName, String module, 
                          String action, String description, String targetType, 
                          Long targetId, String targetName, String ipAddress, 
                          String requestPath, String requestMethod);

    /**
     * 记录错误日志
     */
    void recordErrorLog(Long userId, String username, String realName, String module, 
                       String action, String description, String errorMessage, 
                       String ipAddress, String requestPath);

    /**
     * 批量删除活动日志
     */
    void deleteByIds(List<Long> ids);

    /**
     * 清理过期日志
     */
    void cleanExpiredLogs(int daysToKeep);

    /**
     * 导出活动日志
     */
    byte[] exportActivityLogs(String activityType, String module, String level, 
                            String result, String username, 
                            LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取失败操作日志
     */
    List<ActivityLog> getFailedOperations();

    /**
     * 获取错误日志
     */
    List<ActivityLog> getErrorLogs();

    /**
     * 统计总数
     */
    long count();

    /**
     * 统计今日活动数量
     */
    long countTodayActivities();

    /**
     * 统计本周活动数量
     */
    long countThisWeekActivities();

    /**
     * 统计本月活动数量
     */
    long countThisMonthActivities();
}
