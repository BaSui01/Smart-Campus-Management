package com.campus.domain.repository;

import com.campus.domain.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


/**
 * 消息Repository接口
 * 提供消息相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface MessageRepository extends BaseRepository<Message> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据发送者ID查找消息
     */
    @Query("SELECT m FROM Message m WHERE m.senderId = :senderId AND m.deleted = 0 ORDER BY m.sendTime DESC")
    List<Message> findBySenderId(@Param("senderId") Long senderId);

    /**
     * 分页根据发送者ID查找消息
     */
    @Query("SELECT m FROM Message m WHERE m.senderId = :senderId AND m.deleted = 0")
    Page<Message> findBySenderId(@Param("senderId") Long senderId, Pageable pageable);

    /**
     * 根据接收者ID查找消息
     */
    @Query("SELECT m FROM Message m WHERE m.receiverId = :receiverId AND m.deleted = 0 ORDER BY m.sendTime DESC")
    List<Message> findByReceiverId(@Param("receiverId") Long receiverId);

    /**
     * 分页根据接收者ID查找消息
     */
    @Query("SELECT m FROM Message m WHERE m.receiverId = :receiverId AND m.deleted = 0")
    Page<Message> findByReceiverId(@Param("receiverId") Long receiverId, Pageable pageable);

    /**
     * 根据消息类型查找消息
     */
    @Query("SELECT m FROM Message m WHERE m.messageType = :messageType AND m.deleted = 0 ORDER BY m.sendTime DESC")
    List<Message> findByMessageType(@Param("messageType") String messageType);

    /**
     * 根据消息状态查找消息
     */
    @Query("SELECT m FROM Message m WHERE m.messageStatus = :status AND m.deleted = 0 ORDER BY m.sendTime DESC")
    List<Message> findByMessageStatus(@Param("status") String messageStatus);

    /**
     * 根据优先级查找消息
     */
    @Query("SELECT m FROM Message m WHERE m.priority = :priority AND m.deleted = 0 ORDER BY m.sendTime DESC")
    List<Message> findByPriority(@Param("priority") String priority);

    /**
     * 根据标题模糊查询
     */
    @Query("SELECT m FROM Message m WHERE m.title LIKE %:title% AND m.deleted = 0 ORDER BY m.sendTime DESC")
    List<Message> findByTitleContaining(@Param("title") String title);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找消息
     */
    @Query("SELECT m FROM Message m WHERE " +
           "(:senderId IS NULL OR m.senderId = :senderId) AND " +
           "(:receiverId IS NULL OR m.receiverId = :receiverId) AND " +
           "(:messageType IS NULL OR m.messageType = :messageType) AND " +
           "(:messageStatus IS NULL OR m.messageStatus = :messageStatus) AND " +
           "(:priority IS NULL OR m.priority = :priority) AND " +
           "m.deleted = 0")
    Page<Message> findByMultipleConditions(@Param("senderId") Long senderId,
                                          @Param("receiverId") Long receiverId,
                                          @Param("messageType") String messageType,
                                          @Param("messageStatus") String messageStatus,
                                          @Param("priority") String priority,
                                          Pageable pageable);

    /**
     * 根据发送者和接收者查找消息
     */
    @Query("SELECT m FROM Message m WHERE m.senderId = :senderId AND m.receiverId = :receiverId AND m.deleted = 0 ORDER BY m.sendTime DESC")
    List<Message> findBySenderIdAndReceiverId(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    /**
     * 查找两个用户之间的对话消息
     */
    @Query("SELECT m FROM Message m WHERE " +
           "((m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
           "(m.senderId = :userId2 AND m.receiverId = :userId1)) AND " +
           "m.deleted = 0 ORDER BY m.sendTime ASC")
    List<Message> findConversationBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    /**
     * 分页查找两个用户之间的对话消息
     */
    @Query("SELECT m FROM Message m WHERE " +
           "((m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
           "(m.senderId = :userId2 AND m.receiverId = :userId1)) AND " +
           "m.deleted = 0")
    Page<Message> findConversationBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2, Pageable pageable);

    /**
     * 搜索消息（根据标题、内容等关键词）
     */
    @Query("SELECT m FROM Message m WHERE " +
           "(m.title LIKE %:keyword% OR " +
           "m.content LIKE %:keyword%) AND " +
           "m.deleted = 0 ORDER BY m.sendTime DESC")
    List<Message> searchMessages(@Param("keyword") String keyword);

    /**
     * 分页搜索消息
     */
    @Query("SELECT m FROM Message m WHERE " +
           "(m.title LIKE %:keyword% OR " +
           "m.content LIKE %:keyword%) AND " +
           "m.deleted = 0")
    Page<Message> searchMessages(@Param("keyword") String keyword, Pageable pageable);

    // ================================
    // 状态相关查询
    // ================================

    /**
     * 查找未读消息
     */
    @Query("SELECT m FROM Message m WHERE m.receiverId = :receiverId AND m.isRead = false AND m.deleted = 0 ORDER BY m.sendTime DESC")
    List<Message> findUnreadMessages(@Param("receiverId") Long receiverId);

    /**
     * 分页查找未读消息
     */
    @Query("SELECT m FROM Message m WHERE m.receiverId = :receiverId AND m.isRead = false AND m.deleted = 0")
    Page<Message> findUnreadMessages(@Param("receiverId") Long receiverId, Pageable pageable);

    /**
     * 查找已读消息
     */
    @Query("SELECT m FROM Message m WHERE m.receiverId = :receiverId AND m.isRead = true AND m.deleted = 0 ORDER BY m.sendTime DESC")
    List<Message> findReadMessages(@Param("receiverId") Long receiverId);

    /**
     * 查找重要消息
     */
    @Query("SELECT m FROM Message m WHERE m.receiverId = :receiverId AND m.priority = 'HIGH' AND m.deleted = 0 ORDER BY m.sendTime DESC")
    List<Message> findImportantMessages(@Param("receiverId") Long receiverId);

    /**
     * 查找系统消息
     */
    @Query("SELECT m FROM Message m WHERE m.messageType = 'SYSTEM' AND m.deleted = 0 ORDER BY m.sendTime DESC")
    List<Message> findSystemMessages();

    /**
     * 查找广播消息
     */
    @Query("SELECT m FROM Message m WHERE m.messageType = 'BROADCAST' AND m.deleted = 0 ORDER BY m.sendTime DESC")
    List<Message> findBroadcastMessages();

    // ================================
    // 时间相关查询
    // ================================

    /**
     * 根据发送时间范围查找消息
     */
    @Query("SELECT m FROM Message m WHERE m.sendTime BETWEEN :startTime AND :endTime AND m.deleted = 0 ORDER BY m.sendTime DESC")
    List<Message> findBySendTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                       @Param("endTime") LocalDateTime endTime);

    /**
     * 分页根据发送时间范围查找消息
     */
    @Query("SELECT m FROM Message m WHERE m.sendTime BETWEEN :startTime AND :endTime AND m.deleted = 0")
    Page<Message> findBySendTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                       @Param("endTime") LocalDateTime endTime, 
                                       Pageable pageable);

    /**
     * 查找最近的消息
     */
    @Query("SELECT m FROM Message m WHERE m.deleted = 0 ORDER BY m.sendTime DESC")
    List<Message> findRecentMessages(Pageable pageable);

    /**
     * 查找今日消息
     */
    @Query("SELECT m FROM Message m WHERE DATE(m.sendTime) = CURRENT_DATE AND m.deleted = 0 ORDER BY m.sendTime DESC")
    List<Message> findTodayMessages();

    /**
     * 查找指定用户今日收到的消息
     */
    @Query("SELECT m FROM Message m WHERE m.receiverId = :receiverId AND DATE(m.sendTime) = CURRENT_DATE AND m.deleted = 0 ORDER BY m.sendTime DESC")
    List<Message> findTodayMessagesByReceiverId(@Param("receiverId") Long receiverId);

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 查找消息并预加载发送者信息
     */
    @Query("SELECT DISTINCT m FROM Message m LEFT JOIN FETCH m.sender WHERE m.deleted = 0")
    List<Message> findAllWithSender();

    /**
     * 查找消息并预加载接收者信息
     */
    @Query("SELECT DISTINCT m FROM Message m LEFT JOIN FETCH m.receiver WHERE m.deleted = 0")
    List<Message> findAllWithReceiver();

    /**
     * 查找消息并预加载所有关联信息
     */
    @Query("SELECT DISTINCT m FROM Message m " +
           "LEFT JOIN FETCH m.sender s " +
           "LEFT JOIN FETCH m.receiver r " +
           "WHERE m.deleted = 0")
    List<Message> findAllWithAssociations();

    /**
     * 根据接收者ID查找消息并预加载发送者信息
     */
    @Query("SELECT DISTINCT m FROM Message m LEFT JOIN FETCH m.sender WHERE m.receiverId = :receiverId AND m.deleted = 0 ORDER BY m.sendTime DESC")
    List<Message> findByReceiverIdWithSender(@Param("receiverId") Long receiverId);

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据消息类型统计数量
     */
    @Query("SELECT m.messageType, COUNT(m) FROM Message m WHERE m.deleted = 0 GROUP BY m.messageType ORDER BY COUNT(m) DESC")
    List<Object[]> countByMessageType();

    /**
     * 根据消息状态统计数量
     */
    @Query("SELECT m.messageStatus, COUNT(m) FROM Message m WHERE m.deleted = 0 GROUP BY m.messageStatus")
    List<Object[]> countByMessageStatus();

    /**
     * 根据优先级统计数量
     */
    @Query("SELECT m.priority, COUNT(m) FROM Message m WHERE m.deleted = 0 GROUP BY m.priority ORDER BY m.priority")
    List<Object[]> countByPriority();

    /**
     * 根据发送者统计消息数量
     */
    @Query("SELECT u.username, COUNT(m) FROM Message m LEFT JOIN m.sender u WHERE m.deleted = 0 GROUP BY m.senderId, u.username ORDER BY COUNT(m) DESC")
    List<Object[]> countBySender();

    /**
     * 统计指定用户的未读消息数量
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiverId = :receiverId AND m.isRead = false AND m.deleted = 0")
    long countUnreadMessagesByReceiverId(@Param("receiverId") Long receiverId);

    /**
     * 统计指定用户的消息数量
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiverId = :receiverId AND m.deleted = 0")
    long countByReceiverId(@Param("receiverId") Long receiverId);

    /**
     * 统计指定用户发送的消息数量
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.senderId = :senderId AND m.deleted = 0")
    long countBySenderId(@Param("senderId") Long senderId);

    /**
     * 统计今日消息数量
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE DATE(m.sendTime) = CURRENT_DATE AND m.deleted = 0")
    long countTodayMessages();

    /**
     * 统计指定时间范围内的消息数量
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.sendTime BETWEEN :startTime AND :endTime AND m.deleted = 0")
    long countByDateRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查用户是否有未读消息
     */
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Message m WHERE m.receiverId = :receiverId AND m.isRead = false AND m.deleted = 0")
    boolean hasUnreadMessages(@Param("receiverId") Long receiverId);

    /**
     * 检查两个用户之间是否有对话
     */
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Message m WHERE " +
           "((m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
           "(m.senderId = :userId2 AND m.receiverId = :userId1)) AND " +
           "m.deleted = 0")
    boolean hasConversationBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 标记消息为已读
     */
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true, m.readTime = CURRENT_TIMESTAMP, m.updatedAt = CURRENT_TIMESTAMP WHERE m.id = :messageId")
    int markAsRead(@Param("messageId") Long messageId);

    /**
     * 批量标记消息为已读
     */
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true, m.readTime = CURRENT_TIMESTAMP, m.updatedAt = CURRENT_TIMESTAMP WHERE m.id IN :messageIds")
    int batchMarkAsRead(@Param("messageIds") List<Long> messageIds);

    /**
     * 标记用户所有消息为已读
     */
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true, m.readTime = CURRENT_TIMESTAMP, m.updatedAt = CURRENT_TIMESTAMP WHERE m.receiverId = :receiverId AND m.isRead = false")
    int markAllAsReadByReceiverId(@Param("receiverId") Long receiverId);

    /**
     * 更新消息状态
     */
    @Modifying
    @Query("UPDATE Message m SET m.messageStatus = :status, m.updatedAt = CURRENT_TIMESTAMP WHERE m.id = :messageId")
    int updateMessageStatus(@Param("messageId") Long messageId, @Param("status") String messageStatus);

    /**
     * 批量更新消息状态
     */
    @Modifying
    @Query("UPDATE Message m SET m.messageStatus = :status, m.updatedAt = CURRENT_TIMESTAMP WHERE m.id IN :messageIds")
    int batchUpdateMessageStatus(@Param("messageIds") List<Long> messageIds, @Param("status") String messageStatus);

    // ================================
    // 数据清理方法
    // ================================

    /**
     * 删除指定时间之前的消息
     */
    @Modifying
    @Query("UPDATE Message m SET m.deleted = 1, m.updatedAt = CURRENT_TIMESTAMP WHERE m.sendTime < :beforeTime")
    int deleteMessagesBefore(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 批量删除指定用户的消息
     */
    @Modifying
    @Query("UPDATE Message m SET m.deleted = 1, m.updatedAt = CURRENT_TIMESTAMP WHERE m.receiverId = :receiverId")
    int deleteMessagesByReceiverId(@Param("receiverId") Long receiverId);

    /**
     * 批量删除指定发送者的消息
     */
    @Modifying
    @Query("UPDATE Message m SET m.deleted = 1, m.updatedAt = CURRENT_TIMESTAMP WHERE m.senderId = :senderId")
    int deleteMessagesBySenderId(@Param("senderId") Long senderId);

    /**
     * 清理过期的消息（保留最近N天）
     */
    @Modifying
    @Query("UPDATE Message m SET m.deleted = 1, m.updatedAt = CURRENT_TIMESTAMP WHERE m.sendTime < :cutoffDate")
    int cleanupExpiredMessages(@Param("cutoffDate") LocalDateTime cutoffDate);

}
