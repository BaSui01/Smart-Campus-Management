package com.campus.domain.repository;

import com.campus.domain.entity.ResourceAccessLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 资源访问日志Repository接口
 * 提供资源访问日志相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface ResourceAccessLogRepository extends BaseRepository<ResourceAccessLog> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据资源ID查找访问日志
     */
    @Query("SELECT ral FROM ResourceAccessLog ral WHERE ral.resourceId = :resourceId AND ral.deleted = 0 ORDER BY ral.accessTime DESC")
    List<ResourceAccessLog> findByResourceId(@Param("resourceId") Long resourceId);

    /**
     * 分页根据资源ID查找访问日志
     */
    @Query("SELECT ral FROM ResourceAccessLog ral WHERE ral.resourceId = :resourceId AND ral.deleted = 0")
    Page<ResourceAccessLog> findByResourceId(@Param("resourceId") Long resourceId, Pageable pageable);

    /**
     * 根据用户ID查找访问日志
     */
    @Query("SELECT ral FROM ResourceAccessLog ral WHERE ral.userId = :userId AND ral.deleted = 0 ORDER BY ral.accessTime DESC")
    List<ResourceAccessLog> findByUserId(@Param("userId") Long userId);

    /**
     * 分页根据用户ID查找访问日志
     */
    @Query("SELECT ral FROM ResourceAccessLog ral WHERE ral.userId = :userId AND ral.deleted = 0")
    Page<ResourceAccessLog> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 根据访问类型查找访问日志
     */
    @Query("SELECT ral FROM ResourceAccessLog ral WHERE ral.accessType = :accessType AND ral.deleted = 0 ORDER BY ral.accessTime DESC")
    List<ResourceAccessLog> findByAccessType(@Param("accessType") String accessType);

    /**
     * 根据IP地址查找访问日志
     */
    @Query("SELECT ral FROM ResourceAccessLog ral WHERE ral.ipAddress = :ipAddress AND ral.deleted = 0 ORDER BY ral.accessTime DESC")
    List<ResourceAccessLog> findByIpAddress(@Param("ipAddress") String ipAddress);

    /**
     * 根据用户代理查找访问日志
     */
    @Query("SELECT ral FROM ResourceAccessLog ral WHERE ral.userAgent LIKE %:userAgent% AND ral.deleted = 0 ORDER BY ral.accessTime DESC")
    List<ResourceAccessLog> findByUserAgentContaining(@Param("userAgent") String userAgent);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找访问日志
     */
    @Query("SELECT ral FROM ResourceAccessLog ral WHERE " +
           "(:resourceId IS NULL OR ral.resourceId = :resourceId) AND " +
           "(:userId IS NULL OR ral.userId = :userId) AND " +
           "(:accessType IS NULL OR ral.accessType = :accessType) AND " +
           "(:ipAddress IS NULL OR ral.ipAddress = :ipAddress) AND " +
           "ral.deleted = 0")
    Page<ResourceAccessLog> findByMultipleConditions(@Param("resourceId") Long resourceId,
                                                    @Param("userId") Long userId,
                                                    @Param("accessType") String accessType,
                                                    @Param("ipAddress") String ipAddress,
                                                    Pageable pageable);

    /**
     * 根据用户和资源查找访问日志
     */
    @Query("SELECT ral FROM ResourceAccessLog ral WHERE ral.userId = :userId AND ral.resourceId = :resourceId AND ral.deleted = 0 ORDER BY ral.accessTime DESC")
    List<ResourceAccessLog> findByUserIdAndResourceId(@Param("userId") Long userId, @Param("resourceId") Long resourceId);

    // ================================
    // 时间相关查询
    // ================================

    /**
     * 根据访问时间范围查找访问日志
     */
    @Query("SELECT ral FROM ResourceAccessLog ral WHERE ral.accessTime BETWEEN :startTime AND :endTime AND ral.deleted = 0 ORDER BY ral.accessTime DESC")
    List<ResourceAccessLog> findByAccessTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 分页根据访问时间范围查找访问日志
     */
    @Query("SELECT ral FROM ResourceAccessLog ral WHERE ral.accessTime BETWEEN :startTime AND :endTime AND ral.deleted = 0")
    Page<ResourceAccessLog> findByAccessTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                                   @Param("endTime") LocalDateTime endTime, 
                                                   Pageable pageable);

    /**
     * 查找今日访问日志
     */
    @Query("SELECT ral FROM ResourceAccessLog ral WHERE DATE(ral.accessTime) = CURRENT_DATE AND ral.deleted = 0 ORDER BY ral.accessTime DESC")
    List<ResourceAccessLog> findTodayAccessLogs();

    /**
     * 查找最近的访问日志
     */
    @Query("SELECT ral FROM ResourceAccessLog ral WHERE ral.deleted = 0 ORDER BY ral.accessTime DESC")
    List<ResourceAccessLog> findRecentAccessLogs(Pageable pageable);

    /**
     * 查找指定用户今日的访问日志
     */
    @Query("SELECT ral FROM ResourceAccessLog ral WHERE ral.userId = :userId AND DATE(ral.accessTime) = CURRENT_DATE AND ral.deleted = 0 ORDER BY ral.accessTime DESC")
    List<ResourceAccessLog> findTodayAccessLogsByUserId(@Param("userId") Long userId);

    /**
     * 查找指定资源今日的访问日志
     */
    @Query("SELECT ral FROM ResourceAccessLog ral WHERE ral.resourceId = :resourceId AND DATE(ral.accessTime) = CURRENT_DATE AND ral.deleted = 0 ORDER BY ral.accessTime DESC")
    List<ResourceAccessLog> findTodayAccessLogsByResourceId(@Param("resourceId") Long resourceId);

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 查找访问日志并预加载用户信息
     */
    @Query("SELECT DISTINCT ral FROM ResourceAccessLog ral LEFT JOIN FETCH ral.user WHERE ral.deleted = 0 ORDER BY ral.accessTime DESC")
    List<ResourceAccessLog> findAllWithUser();

    /**
     * 查找访问日志并预加载资源信息
     */
    @Query("SELECT DISTINCT ral FROM ResourceAccessLog ral LEFT JOIN FETCH ral.resource WHERE ral.deleted = 0 ORDER BY ral.accessTime DESC")
    List<ResourceAccessLog> findAllWithResource();

    /**
     * 查找访问日志并预加载所有关联信息
     */
    @Query("SELECT DISTINCT ral FROM ResourceAccessLog ral " +
           "LEFT JOIN FETCH ral.user u " +
           "LEFT JOIN FETCH ral.resource r " +
           "WHERE ral.deleted = 0 ORDER BY ral.accessTime DESC")
    List<ResourceAccessLog> findAllWithAssociations();

    /**
     * 根据资源ID查找访问日志并预加载用户信息
     */
    @Query("SELECT DISTINCT ral FROM ResourceAccessLog ral LEFT JOIN FETCH ral.user WHERE ral.resourceId = :resourceId AND ral.deleted = 0 ORDER BY ral.accessTime DESC")
    List<ResourceAccessLog> findByResourceIdWithUser(@Param("resourceId") Long resourceId);

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据访问类型统计访问次数
     */
    @Query("SELECT ral.accessType, COUNT(ral) FROM ResourceAccessLog ral WHERE ral.deleted = 0 GROUP BY ral.accessType ORDER BY COUNT(ral) DESC")
    List<Object[]> countByAccessType();

    /**
     * 根据资源统计访问次数
     */
    @Query("SELECT r.resourceName, COUNT(ral) FROM ResourceAccessLog ral LEFT JOIN ral.resource r WHERE ral.deleted = 0 GROUP BY ral.resourceId, r.resourceName ORDER BY COUNT(ral) DESC")
    List<Object[]> countByResource();

    /**
     * 根据用户统计访问次数
     */
    @Query("SELECT u.username, COUNT(ral) FROM ResourceAccessLog ral LEFT JOIN ral.user u WHERE ral.deleted = 0 GROUP BY ral.userId, u.username ORDER BY COUNT(ral) DESC")
    List<Object[]> countByUser();

    /**
     * 根据IP地址统计访问次数
     */
    @Query("SELECT ral.ipAddress, COUNT(ral) FROM ResourceAccessLog ral WHERE ral.deleted = 0 GROUP BY ral.ipAddress ORDER BY COUNT(ral) DESC")
    List<Object[]> countByIpAddress();

    /**
     * 统计今日访问次数
     */
    @Query("SELECT COUNT(ral) FROM ResourceAccessLog ral WHERE DATE(ral.accessTime) = CURRENT_DATE AND ral.deleted = 0")
    long countTodayAccess();

    /**
     * 统计指定时间范围内的访问次数
     */
    @Query("SELECT COUNT(ral) FROM ResourceAccessLog ral WHERE ral.accessTime BETWEEN :startTime AND :endTime AND ral.deleted = 0")
    long countByDateRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计指定资源的访问次数
     */
    @Query("SELECT COUNT(ral) FROM ResourceAccessLog ral WHERE ral.resourceId = :resourceId AND ral.deleted = 0")
    long countByResourceId(@Param("resourceId") Long resourceId);

    /**
     * 统计指定用户的访问次数
     */
    @Query("SELECT COUNT(ral) FROM ResourceAccessLog ral WHERE ral.userId = :userId AND ral.deleted = 0")
    long countByUserId(@Param("userId") Long userId);

    /**
     * 统计指定资源今日的访问次数
     */
    @Query("SELECT COUNT(ral) FROM ResourceAccessLog ral WHERE ral.resourceId = :resourceId AND DATE(ral.accessTime) = CURRENT_DATE AND ral.deleted = 0")
    long countTodayAccessByResourceId(@Param("resourceId") Long resourceId);

    /**
     * 统计指定用户今日的访问次数
     */
    @Query("SELECT COUNT(ral) FROM ResourceAccessLog ral WHERE ral.userId = :userId AND DATE(ral.accessTime) = CURRENT_DATE AND ral.deleted = 0")
    long countTodayAccessByUserId(@Param("userId") Long userId);

    /**
     * 统计指定资源的独立访问用户数
     */
    @Query("SELECT COUNT(DISTINCT ral.userId) FROM ResourceAccessLog ral WHERE ral.resourceId = :resourceId AND ral.deleted = 0")
    long countUniqueUsersByResourceId(@Param("resourceId") Long resourceId);

    /**
     * 统计指定时间范围内资源的独立访问用户数
     */
    @Query("SELECT COUNT(DISTINCT ral.userId) FROM ResourceAccessLog ral WHERE ral.resourceId = :resourceId AND ral.accessTime BETWEEN :startTime AND :endTime AND ral.deleted = 0")
    long countUniqueUsersByResourceIdAndDateRange(@Param("resourceId") Long resourceId, 
                                                 @Param("startTime") LocalDateTime startTime, 
                                                 @Param("endTime") LocalDateTime endTime);

    // ================================
    // 热门资源查询
    // ================================

    /**
     * 查找热门资源（按访问次数排序）
     */
    @Query("SELECT ral.resourceId, COUNT(ral) as accessCount FROM ResourceAccessLog ral WHERE ral.deleted = 0 GROUP BY ral.resourceId ORDER BY accessCount DESC")
    List<Object[]> findPopularResources(Pageable pageable);

    /**
     * 查找指定时间范围内的热门资源
     */
    @Query("SELECT ral.resourceId, COUNT(ral) as accessCount FROM ResourceAccessLog ral WHERE ral.accessTime BETWEEN :startTime AND :endTime AND ral.deleted = 0 GROUP BY ral.resourceId ORDER BY accessCount DESC")
    List<Object[]> findPopularResourcesByDateRange(@Param("startTime") LocalDateTime startTime, 
                                                  @Param("endTime") LocalDateTime endTime, 
                                                  Pageable pageable);

    /**
     * 查找今日热门资源
     */
    @Query("SELECT ral.resourceId, COUNT(ral) as accessCount FROM ResourceAccessLog ral WHERE DATE(ral.accessTime) = CURRENT_DATE AND ral.deleted = 0 GROUP BY ral.resourceId ORDER BY accessCount DESC")
    List<Object[]> findTodayPopularResources(Pageable pageable);

    // ================================
    // 数据清理方法
    // ================================

    /**
     * 删除指定时间之前的访问日志
     */
    @Modifying
    @Query("UPDATE ResourceAccessLog ral SET ral.deleted = 1, ral.updatedAt = CURRENT_TIMESTAMP WHERE ral.accessTime < :beforeTime")
    int deleteAccessLogsBefore(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 批量删除指定资源的访问日志
     */
    @Modifying
    @Query("UPDATE ResourceAccessLog ral SET ral.deleted = 1, ral.updatedAt = CURRENT_TIMESTAMP WHERE ral.resourceId = :resourceId")
    int deleteAccessLogsByResourceId(@Param("resourceId") Long resourceId);

    /**
     * 批量删除指定用户的访问日志
     */
    @Modifying
    @Query("UPDATE ResourceAccessLog ral SET ral.deleted = 1, ral.updatedAt = CURRENT_TIMESTAMP WHERE ral.userId = :userId")
    int deleteAccessLogsByUserId(@Param("userId") Long userId);

    /**
     * 清理过期的访问日志（保留最近N天）
     */
    @Modifying
    @Query("UPDATE ResourceAccessLog ral SET ral.deleted = 1, ral.updatedAt = CURRENT_TIMESTAMP WHERE ral.accessTime < :cutoffDate")
    int cleanupExpiredAccessLogs(@Param("cutoffDate") LocalDateTime cutoffDate);

    // ================================
    // 访问行为分析
    // ================================

    /**
     * 查找用户最近访问的资源
     */
    @Query("SELECT DISTINCT ral.resourceId FROM ResourceAccessLog ral WHERE ral.userId = :userId AND ral.deleted = 0 ORDER BY ral.accessTime DESC")
    List<Long> findRecentAccessedResourcesByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 查找用户最常访问的资源
     */
    @Query("SELECT ral.resourceId, COUNT(ral) as accessCount FROM ResourceAccessLog ral WHERE ral.userId = :userId AND ral.deleted = 0 GROUP BY ral.resourceId ORDER BY accessCount DESC")
    List<Object[]> findMostAccessedResourcesByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 查找资源的活跃访问用户
     */
    @Query("SELECT ral.userId, COUNT(ral) as accessCount FROM ResourceAccessLog ral WHERE ral.resourceId = :resourceId AND ral.deleted = 0 GROUP BY ral.userId ORDER BY accessCount DESC")
    List<Object[]> findActiveUsersByResourceId(@Param("resourceId") Long resourceId, Pageable pageable);

}
