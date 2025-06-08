package com.campus.domain.repository;

import com.campus.domain.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知Repository接口
 * 提供通知相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface NotificationRepository extends BaseRepository<Notification> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据通知类型查找通知
     */
    @Query("SELECT n FROM Notification n WHERE n.type = :type AND n.deleted = 0 ORDER BY n.createdAt DESC")
    List<Notification> findByNotificationType(@Param("type") String notificationType);

    /**
     * 分页根据通知类型查找通知
     */
    @Query("SELECT n FROM Notification n WHERE n.type = :type AND n.deleted = 0")
    Page<Notification> findByNotificationType(@Param("type") String notificationType, Pageable pageable);

    /**
     * 根据通知状态查找通知
     */
    @Query("SELECT n FROM Notification n WHERE n.notificationStatus = :status AND n.deleted = 0 ORDER BY n.createdAt DESC")
    List<Notification> findByNotificationStatus(@Param("status") String notificationStatus);

    /**
     * 分页根据通知状态查找通知
     */
    @Query("SELECT n FROM Notification n WHERE n.notificationStatus = :status AND n.deleted = 0")
    Page<Notification> findByNotificationStatus(@Param("status") String notificationStatus, Pageable pageable);

    /**
     * 根据目标受众查找通知
     */
    @Query("SELECT n FROM Notification n WHERE n.targetAudience = :audience AND n.deleted = 0 ORDER BY n.createdAt DESC")
    List<Notification> findByTargetAudience(@Param("audience") String targetAudience);

    /**
     * 根据发送者ID查找通知
     */
    @Query("SELECT n FROM Notification n WHERE n.senderId = :senderId AND n.deleted = 0 ORDER BY n.createdAt DESC")
    List<Notification> findBySenderId(@Param("senderId") Long senderId);

    /**
     * 分页根据发送者ID查找通知
     */
    @Query("SELECT n FROM Notification n WHERE n.senderId = :senderId AND n.deleted = 0")
    Page<Notification> findBySenderId(@Param("senderId") Long senderId, Pageable pageable);

    /**
     * 根据优先级查找通知
     */
    @Query("SELECT n FROM Notification n WHERE n.priority = :priority AND n.deleted = 0 ORDER BY n.createdAt DESC")
    List<Notification> findByPriority(@Param("priority") String priority);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找通知
     */
    @Query("SELECT n FROM Notification n WHERE " +
           "(:notificationType IS NULL OR n.type = :notificationType) AND " +
           "(:notificationStatus IS NULL OR n.notificationStatus = :notificationStatus) AND " +
           "(:targetAudience IS NULL OR n.targetAudience = :targetAudience) AND " +
           "(:senderId IS NULL OR n.senderId = :senderId) AND " +
           "(:priority IS NULL OR n.priority = :priority) AND " +
           "n.deleted = 0")
    Page<Notification> findByMultipleConditions(@Param("notificationType") String notificationType,
                                               @Param("notificationStatus") String notificationStatus,
                                               @Param("targetAudience") String targetAudience,
                                               @Param("senderId") Long senderId,
                                               @Param("priority") String priority,
                                               Pageable pageable);

    /**
     * 搜索通知（根据标题、内容等关键词）
     */
    @Query("SELECT n FROM Notification n WHERE " +
           "(n.title LIKE %:keyword% OR " +
           "n.content LIKE %:keyword%) AND " +
           "n.deleted = 0 ORDER BY n.createdAt DESC")
    List<Notification> searchNotifications(@Param("keyword") String keyword);

    /**
     * 分页搜索通知
     */
    @Query("SELECT n FROM Notification n WHERE " +
           "(n.title LIKE %:keyword% OR " +
           "n.content LIKE %:keyword%) AND " +
           "n.deleted = 0")
    Page<Notification> searchNotifications(@Param("keyword") String keyword, Pageable pageable);

    // ================================
    // 时间相关查询
    // ================================

    /**
     * 查找已发布的通知
     */
    @Query("SELECT n FROM Notification n WHERE n.notificationStatus = 'published' AND " +
           "(n.expireTime IS NULL OR n.expireTime > :now) AND n.deleted = 0 ORDER BY n.createdAt DESC")
    List<Notification> findPublishedNotifications(@Param("now") LocalDateTime now);

    /**
     * 分页查找已发布的通知
     */
    @Query("SELECT n FROM Notification n WHERE n.notificationStatus = 'published' AND " +
           "(n.expireTime IS NULL OR n.expireTime > :now) AND n.deleted = 0")
    Page<Notification> findPublishedNotifications(@Param("now") LocalDateTime now, Pageable pageable);

    /**
     * 查找置顶通知
     */
    @Query("SELECT n FROM Notification n WHERE n.isPinned = true AND n.notificationStatus = 'published' AND " +
           "(n.expireTime IS NULL OR n.expireTime > :now) AND n.deleted = 0 ORDER BY n.createdAt DESC")
    List<Notification> findPinnedNotifications(@Param("now") LocalDateTime now);

    /**
     * 查找即将过期的通知
     */
    @Query("SELECT n FROM Notification n WHERE n.expireTime IS NOT NULL AND " +
           "n.expireTime BETWEEN :now AND :futureTime AND n.deleted = 0 ORDER BY n.expireTime ASC")
    List<Notification> findExpiringNotifications(@Param("now") LocalDateTime now, @Param("futureTime") LocalDateTime futureTime);

    /**
     * 查找已过期的通知
     */
    @Query("SELECT n FROM Notification n WHERE n.expireTime IS NOT NULL AND " +
           "n.expireTime < :now AND n.deleted = 0 ORDER BY n.expireTime DESC")
    List<Notification> findExpiredNotifications(@Param("now") LocalDateTime now);

    /**
     * 查找指定时间范围内的通知
     */
    @Query("SELECT n FROM Notification n WHERE n.createdAt BETWEEN :startTime AND :endTime AND n.deleted = 0 ORDER BY n.createdAt DESC")
    List<Notification> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime);

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 查找通知并预加载发送者信息
     */
    @Query("SELECT DISTINCT n FROM Notification n LEFT JOIN FETCH n.sender WHERE n.deleted = 0")
    List<Notification> findAllWithSender();



    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据通知类型统计数量
     */
    @Query("SELECT n.type, COUNT(n) FROM Notification n WHERE n.deleted = 0 GROUP BY n.type ORDER BY COUNT(n) DESC")
    List<Object[]> countByNotificationType();

    /**
     * 根据通知状态统计数量
     */
    @Query("SELECT n.notificationStatus, COUNT(n) FROM Notification n WHERE n.deleted = 0 GROUP BY n.notificationStatus")
    List<Object[]> countByNotificationStatus();

    /**
     * 根据目标受众统计数量
     */
    @Query("SELECT n.targetAudience, COUNT(n) FROM Notification n WHERE n.deleted = 0 GROUP BY n.targetAudience ORDER BY COUNT(n) DESC")
    List<Object[]> countByTargetAudience();

    /**
     * 根据优先级统计数量
     */
    @Query("SELECT n.priority, COUNT(n) FROM Notification n WHERE n.deleted = 0 GROUP BY n.priority ORDER BY n.priority")
    List<Object[]> countByPriority();

    /**
     * 统计今日发布的通知数量
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE DATE(n.createdAt) = CURRENT_DATE AND n.deleted = 0")
    long countTodayNotifications();

    /**
     * 统计指定时间范围内的通知数量
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.createdAt BETWEEN :startTime AND :endTime AND n.deleted = 0")
    long countByDateRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查通知标题是否存在
     */
    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM Notification n WHERE n.title = :title AND n.deleted = 0")
    boolean existsByTitle(@Param("title") String title);

    /**
     * 检查通知标题是否存在（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM Notification n WHERE n.title = :title AND n.id != :excludeId AND n.deleted = 0")
    boolean existsByTitleAndIdNot(@Param("title") String title, @Param("excludeId") Long excludeId);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新通知状态
     */
    @Modifying
    @Query("UPDATE Notification n SET n.notificationStatus = :status, n.updatedAt = CURRENT_TIMESTAMP WHERE n.id = :notificationId")
    int updateNotificationStatus(@Param("notificationId") Long notificationId, @Param("status") String notificationStatus);

    /**
     * 批量更新通知状态
     */
    @Modifying
    @Query("UPDATE Notification n SET n.notificationStatus = :status, n.updatedAt = CURRENT_TIMESTAMP WHERE n.id IN :notificationIds")
    int batchUpdateNotificationStatus(@Param("notificationIds") List<Long> notificationIds, @Param("status") String notificationStatus);

    /**
     * 更新通知置顶状态
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isPinned = :isPinned, n.updatedAt = CURRENT_TIMESTAMP WHERE n.id = :notificationId")
    int updatePinnedStatus(@Param("notificationId") Long notificationId, @Param("isPinned") Boolean isPinned);

    /**
     * 更新通知过期时间
     */
    @Modifying
    @Query("UPDATE Notification n SET n.expireTime = :expireTime, n.updatedAt = CURRENT_TIMESTAMP WHERE n.id = :notificationId")
    int updateExpireTime(@Param("notificationId") Long notificationId, @Param("expireTime") LocalDateTime expireTime);

    /**
     * 批量设置通知过期
     */
    @Modifying
    @Query("UPDATE Notification n SET n.notificationStatus = 'expired', n.updatedAt = CURRENT_TIMESTAMP WHERE n.expireTime < :now AND n.notificationStatus = 'published'")
    int batchExpireNotifications(@Param("now") LocalDateTime now);

    // ================================
    // 兼容性方法（为现有Service提供支持）
    // ================================

    /**
     * 根据状态查找通知（兼容性方法）
     */
    default List<Notification> findByStatus(String status) {
        return findByNotificationStatus(status);
    }

    /**
     * 根据类型查找通知（兼容性方法）
     */
    default List<Notification> findByType(String type) {
        return findByNotificationType(type);
    }

    /**
     * 查找置顶通知（兼容性方法）
     */
    default List<Notification> findByIsPinnedTrueOrderByCreateTimeDesc() {
        return findPinnedNotifications(LocalDateTime.now());
    }

    /**
     * 查找最近的通知（兼容性方法）
     */
    @Query("SELECT n FROM Notification n WHERE n.deleted = 0 ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications();

}
