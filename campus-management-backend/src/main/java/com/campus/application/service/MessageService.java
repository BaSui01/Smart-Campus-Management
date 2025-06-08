package com.campus.application.service;

import com.campus.domain.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 消息管理服务接口
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
public interface MessageService {
    
    // ================================
    // 基础CRUD操作
    // ================================
    
    /**
     * 发送消息
     */
    Message sendMessage(Message message);
    
    /**
     * 根据ID查找消息
     */
    Optional<Message> findMessageById(Long id);
    
    /**
     * 删除消息（软删除）
     */
    void deleteMessage(Long id);
    
    /**
     * 批量删除消息
     */
    void deleteMessages(List<Long> messageIds);
    
    // ================================
    // 查询操作
    // ================================
    
    /**
     * 分页获取接收者的消息
     */
    Page<Message> findMessagesByReceiver(Long receiverId, Pageable pageable);
    
    /**
     * 分页获取发送者的消息
     */
    Page<Message> findMessagesBySender(Long senderId, Pageable pageable);
    
    /**
     * 获取未读消息
     */
    List<Message> findUnreadMessages(Long receiverId);
    
    /**
     * 获取已读消息
     */
    Page<Message> findReadMessages(Long receiverId, Pageable pageable);
    
    /**
     * 根据消息类型查找消息
     */
    Page<Message> findMessagesByType(Long userId, String messageType, Pageable pageable);
    
    /**
     * 获取用户的收件箱
     */
    Page<Message> getInbox(Long userId, Pageable pageable);
    
    /**
     * 获取用户的发件箱
     */
    Page<Message> getOutbox(Long userId, Pageable pageable);
    
    // ================================
    // 业务操作
    // ================================
    
    /**
     * 标记消息为已读
     */
    void markAsRead(Long messageId);
    
    /**
     * 标记所有消息为已读
     */
    void markAllAsRead(Long receiverId);
    
    /**
     * 标记消息为未读
     */
    void markAsUnread(Long messageId);
    
    /**
     * 检查消息是否已读
     */
    boolean isMessageRead(Long messageId);
    
    /**
     * 转发消息
     */
    Message forwardMessage(Long messageId, Long newReceiverId);
    
    /**
     * 回复消息
     */
    Message replyMessage(Long originalMessageId, String content);
    
    /**
     * 群发消息
     */
    List<Message> sendBroadcastMessage(List<Long> receiverIds, String title, String content, String messageType);
    
    // ================================
    // 统计操作
    // ================================
    
    /**
     * 统计未读消息数量
     */
    long countUnreadMessages(Long receiverId);
    
    /**
     * 统计用户总消息数量
     */
    long countTotalMessages(Long userId);
    
    /**
     * 统计发送的消息数量
     */
    long countSentMessages(Long senderId);
    
    /**
     * 统计接收的消息数量
     */
    long countReceivedMessages(Long receiverId);
    
    /**
     * 按消息类型统计数量
     */
    List<Object[]> countMessagesByType(Long userId);
    
    // ================================
    // 搜索操作
    // ================================
    
    /**
     * 搜索消息
     */
    Page<Message> searchMessages(Long userId, String keyword, Pageable pageable);
    
    /**
     * 在收件箱中搜索
     */
    Page<Message> searchInbox(Long userId, String keyword, Pageable pageable);
    
    /**
     * 在发件箱中搜索
     */
    Page<Message> searchOutbox(Long userId, String keyword, Pageable pageable);
    
    // ================================
    // 管理操作
    // ================================
    
    /**
     * 清理已删除的消息
     */
    void cleanupDeletedMessages();
    
    /**
     * 清理过期消息
     */
    void cleanupExpiredMessages(int daysToKeep);
    
    /**
     * 获取消息统计信息
     */
    Object getMessageStatistics(Long userId);
    
    /**
     * 批量标记为已读
     */
    void batchMarkAsRead(List<Long> messageIds);
    
    /**
     * 批量标记为未读
     */
    void batchMarkAsUnread(List<Long> messageIds);
}
