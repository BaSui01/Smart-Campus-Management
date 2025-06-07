package com.campus.domain.repository;

import com.campus.domain.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动日志Repository接口
 * 提供活动日志相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    /**
     * 根据用户ID查询活动日志
     */
    Page<ActivityLog> findByUserId(Long userId, Pageable pageable);

    /**
     * 根据活动类型查询活动日志
     */
    Page<ActivityLog> findByActivityType(String activityType, Pageable pageable);

    /**
     * 根据模块查询活动日志
     */
    Page<ActivityLog> findByModule(String module, Pageable pageable);

    /**
     * 根据操作级别查询活动日志
     */
    Page<ActivityLog> findByLevel(String level, Pageable pageable);

    /**
     * 根据操作结果查询活动日志
     */
    Page<ActivityLog> findByResult(String result, Pageable pageable);

    /**
     * 根据时间范围查询活动日志
     */
    Page<ActivityLog> findByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * 根据用户名模糊查询活动日志
     */
    @Query("SELECT a FROM ActivityLog a WHERE a.username LIKE %:username% OR a.realName LIKE %:username%")
    Page<ActivityLog> findByUsernameLike(@Param("username") String username, Pageable pageable);

    /**
     * 根据描述模糊查询活动日志
     */
    Page<ActivityLog> findByDescriptionContaining(String description, Pageable pageable);

    /**
     * 复合条件查询活动日志
     */
    @Query("SELECT a FROM ActivityLog a WHERE " +
           "(:activityType IS NULL OR a.activityType = :activityType) AND " +
           "(:module IS NULL OR a.module = :module) AND " +
           "(:level IS NULL OR a.level = :level) AND " +
           "(:result IS NULL OR a.result = :result) AND " +
           "(:username IS NULL OR a.username LIKE %:username% OR a.realName LIKE %:username%) AND " +
           "(:startTime IS NULL OR a.createTime >= :startTime) AND " +
           "(:endTime IS NULL OR a.createTime <= :endTime)")
    Page<ActivityLog> findByConditions(@Param("activityType") String activityType,
                                     @Param("module") String module,
                                     @Param("level") String level,
                                     @Param("result") String result,
                                     @Param("username") String username,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime,
                                     Pageable pageable);

    /**
     * 统计各活动类型的数量
     */
    @Query("SELECT a.activityType, COUNT(a) FROM ActivityLog a GROUP BY a.activityType")
    List<Object[]> countByActivityType();

    /**
     * 统计各模块的操作数量
     */
    @Query("SELECT a.module, COUNT(a) FROM ActivityLog a GROUP BY a.module")
    List<Object[]> countByModule();

    /**
     * 统计各级别的日志数量
     */
    @Query("SELECT a.level, COUNT(a) FROM ActivityLog a GROUP BY a.level")
    List<Object[]> countByLevel();

    /**
     * 统计各结果状态的数量
     */
    @Query("SELECT a.result, COUNT(a) FROM ActivityLog a GROUP BY a.result")
    List<Object[]> countByResult();

    /**
     * 获取最近的活动日志
     */
    List<ActivityLog> findTop10ByOrderByCreateTimeDesc();

    /**
     * 获取指定用户的最近活动
     */
    List<ActivityLog> findTop5ByUserIdOrderByCreateTimeDesc(Long userId);

    /**
     * 统计今日活动数量
     */
    @Query("SELECT COUNT(a) FROM ActivityLog a WHERE DATE(a.createTime) = CURRENT_DATE")
    long countTodayActivities();

    /**
     * 统计本周活动数量
     */
    @Query("SELECT COUNT(a) FROM ActivityLog a WHERE YEARWEEK(a.createTime) = YEARWEEK(NOW())")
    long countThisWeekActivities();

    /**
     * 统计本月活动数量
     */
    @Query("SELECT COUNT(a) FROM ActivityLog a WHERE YEAR(a.createTime) = YEAR(NOW()) AND MONTH(a.createTime) = MONTH(NOW())")
    long countThisMonthActivities();

    /**
     * 获取每日活动统计（最近7天）
     */
    @Query("SELECT DATE(a.createTime) as date, COUNT(a) as count " +
           "FROM ActivityLog a " +
           "WHERE a.createTime >= :startDate " +
           "GROUP BY DATE(a.createTime) " +
           "ORDER BY DATE(a.createTime)")
    List<Object[]> getDailyActivityStats(@Param("startDate") LocalDateTime startDate);

    /**
     * 获取用户活动排行榜（最近30天）
     */
    @Query("SELECT a.username, a.realName, COUNT(a) as count " +
           "FROM ActivityLog a " +
           "WHERE a.createTime >= :startDate AND a.userId IS NOT NULL " +
           "GROUP BY a.userId, a.username, a.realName " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> getUserActivityRanking(@Param("startDate") LocalDateTime startDate);

    /**
     * 获取模块操作统计（最近30天）
     */
    @Query("SELECT a.module, a.action, COUNT(a) as count " +
           "FROM ActivityLog a " +
           "WHERE a.createTime >= :startDate " +
           "GROUP BY a.module, a.action " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> getModuleActionStats(@Param("startDate") LocalDateTime startDate);

    /**
     * 删除指定时间之前的日志
     */
    void deleteByCreateTimeBefore(LocalDateTime beforeTime);

    /**
     * 根据IP地址查询活动日志
     */
    Page<ActivityLog> findByIpAddress(String ipAddress, Pageable pageable);

    /**
     * 查询失败的操作日志
     */
    @Query("SELECT a FROM ActivityLog a WHERE a.result = 'FAILED' ORDER BY a.createTime DESC")
    List<ActivityLog> findFailedOperations();

    /**
     * 查询异常操作日志（错误级别）
     */
    @Query("SELECT a FROM ActivityLog a WHERE a.level = 'ERROR' ORDER BY a.createTime DESC")
    List<ActivityLog> findErrorLogs();

    /**
     * 统计指定时间范围内的活动数量
     */
    long countByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
}
