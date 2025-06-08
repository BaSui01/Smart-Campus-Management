package com.campus.application.service.impl;

import com.campus.application.service.MessageService;
import com.campus.domain.entity.Message;
import com.campus.domain.repository.MessageRepository;
import com.campus.shared.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 消息管理服务实现类
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Service
@Transactional
public class MessageServiceImpl implements MessageService {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Override
    public Message sendMessage(Message message) {
        logger.info("发送消息: 发送者={}, 接收者={}, 标题={}", 
                   message.getSenderId(), message.getReceiverId(), message.getTitle());
        
        message.setIsRead(false);
        message.setSentAt(LocalDateTime.now());
        message.setStatus(1);
        message.setDeleted(0);
        
        return messageRepository.save(message);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Message> findMessageById(Long id) {
        return messageRepository.findByIdAndDeleted(id, 0);
    }
    
    @Override
    public void deleteMessage(Long id) {
        logger.info("删除消息: {}", id);
        
        Optional<Message> messageOpt = findMessageById(id);
        if (messageOpt.isEmpty()) {
            throw new BusinessException("消息不存在");
        }
        
        Message message = messageOpt.get();
        message.setDeleted(1);
        messageRepository.save(message);
    }
    
    @Override
    public void deleteMessages(List<Long> messageIds) {
        logger.info("批量删除消息: {} 个", messageIds.size());
        
        for (Long messageId : messageIds) {
            try {
                deleteMessage(messageId);
            } catch (Exception e) {
                logger.error("删除消息失败: messageId={}", messageId, e);
            }
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Message> findMessagesByReceiver(Long receiverId, Pageable pageable) {
        return messageRepository.findByReceiverIdAndDeletedOrderByCreatedAtDesc(receiverId, 0, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Message> findMessagesBySender(Long senderId, Pageable pageable) {
        return messageRepository.findBySenderIdAndDeletedOrderByCreatedAtDesc(senderId, 0, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Message> findUnreadMessages(Long receiverId) {
        return messageRepository.findByReceiverIdAndIsReadAndDeletedOrderByCreatedAtDesc(receiverId, false, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Message> findReadMessages(Long receiverId, Pageable pageable) {
        return messageRepository.findByReceiverIdAndIsReadAndDeletedOrderByCreatedAtDesc(receiverId, true, 0, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Message> findMessagesByType(Long userId, String messageType, Pageable pageable) {
        return messageRepository.findByReceiverIdAndMessageTypeAndDeletedOrderByCreatedAtDesc(userId, messageType, 0, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Message> getInbox(Long userId, Pageable pageable) {
        return findMessagesByReceiver(userId, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Message> getOutbox(Long userId, Pageable pageable) {
        return findMessagesBySender(userId, pageable);
    }
    
    @Override
    public void markAsRead(Long messageId) {
        logger.debug("标记消息为已读: {}", messageId);
        
        Optional<Message> messageOpt = findMessageById(messageId);
        if (messageOpt.isEmpty()) {
            throw new BusinessException("消息不存在");
        }
        
        Message message = messageOpt.get();
        if (!message.getIsRead()) {
            message.setIsRead(true);
            message.setReadAt(LocalDateTime.now());
            messageRepository.save(message);
        }
    }
    
    @Override
    public void markAllAsRead(Long receiverId) {
        logger.info("标记所有消息为已读: receiverId={}", receiverId);
        
        List<Message> unreadMessages = findUnreadMessages(receiverId);
        for (Message message : unreadMessages) {
            message.setIsRead(true);
            message.setReadAt(LocalDateTime.now());
            messageRepository.save(message);
        }
    }
    
    @Override
    public void markAsUnread(Long messageId) {
        logger.debug("标记消息为未读: {}", messageId);
        
        Optional<Message> messageOpt = findMessageById(messageId);
        if (messageOpt.isEmpty()) {
            throw new BusinessException("消息不存在");
        }
        
        Message message = messageOpt.get();
        if (message.getIsRead()) {
            message.setIsRead(false);
            message.setReadAt(null);
            messageRepository.save(message);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isMessageRead(Long messageId) {
        Optional<Message> messageOpt = findMessageById(messageId);
        return messageOpt.map(Message::getIsRead).orElse(false);
    }
    
    @Override
    public Message forwardMessage(Long messageId, Long newReceiverId) {
        logger.info("转发消息: messageId={}, newReceiverId={}", messageId, newReceiverId);
        
        Optional<Message> originalOpt = findMessageById(messageId);
        if (originalOpt.isEmpty()) {
            throw new BusinessException("原消息不存在");
        }
        
        Message original = originalOpt.get();
        Message forwarded = new Message();
        forwarded.setSenderId(original.getReceiverId()); // 当前用户作为发送者
        forwarded.setReceiverId(newReceiverId);
        forwarded.setTitle("转发: " + original.getTitle());
        forwarded.setContent(original.getContent());
        forwarded.setMessageType(original.getMessageType());
        
        return sendMessage(forwarded);
    }
    
    @Override
    public Message replyMessage(Long originalMessageId, String content) {
        logger.info("回复消息: originalMessageId={}", originalMessageId);
        
        Optional<Message> originalOpt = findMessageById(originalMessageId);
        if (originalOpt.isEmpty()) {
            throw new BusinessException("原消息不存在");
        }
        
        Message original = originalOpt.get();
        Message reply = new Message();
        reply.setSenderId(original.getReceiverId()); // 当前用户作为发送者
        reply.setReceiverId(original.getSenderId()); // 原发送者作为接收者
        reply.setTitle("回复: " + original.getTitle());
        reply.setContent(content);
        reply.setMessageType(original.getMessageType());
        
        return sendMessage(reply);
    }
    
    @Override
    public List<Message> sendBroadcastMessage(List<Long> receiverIds, String title, String content, String messageType) {
        logger.info("群发消息: 接收者数量={}, 标题={}", receiverIds.size(), title);
        
        List<Message> sentMessages = new ArrayList<>();
        
        for (Long receiverId : receiverIds) {
            try {
                Message message = new Message();
                message.setReceiverId(receiverId);
                message.setTitle(title);
                message.setContent(content);
                message.setMessageType(messageType);
                
                Message sent = sendMessage(message);
                sentMessages.add(sent);
            } catch (Exception e) {
                logger.error("发送群发消息失败: receiverId={}", receiverId, e);
            }
        }
        
        return sentMessages;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countUnreadMessages(Long receiverId) {
        return messageRepository.countByReceiverIdAndIsReadAndDeleted(receiverId, false, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countTotalMessages(Long userId) {
        long received = messageRepository.countByReceiverIdAndDeleted(userId, 0);
        long sent = messageRepository.countBySenderIdAndDeleted(userId, 0);
        return received + sent;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countSentMessages(Long senderId) {
        return messageRepository.countBySenderIdAndDeleted(senderId, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countReceivedMessages(Long receiverId) {
        return messageRepository.countByReceiverIdAndDeleted(receiverId, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> countMessagesByType(Long userId) {
        return messageRepository.countByMessageTypeForUser(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Message> searchMessages(Long userId, String keyword, Pageable pageable) {
        return messageRepository.searchMessagesForUser(userId, keyword, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Message> searchInbox(Long userId, String keyword, Pageable pageable) {
        return messageRepository.searchInboxMessages(userId, keyword, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Message> searchOutbox(Long userId, String keyword, Pageable pageable) {
        return messageRepository.searchOutboxMessages(userId, keyword, pageable);
    }
    
    @Override
    public void cleanupDeletedMessages() {
        logger.info("清理已删除的消息");
        
        List<Message> deletedMessages = messageRepository.findByDeleted(1);
        messageRepository.deleteAll(deletedMessages);
        
        logger.info("清理完成，删除了 {} 条消息", deletedMessages.size());
    }
    
    @Override
    public void cleanupExpiredMessages(int daysToKeep) {
        logger.info("清理过期消息，保留天数: {}", daysToKeep);
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        List<Message> expiredMessages = messageRepository.findByCreatedAtBeforeAndDeleted(cutoffDate, 0);
        
        for (Message message : expiredMessages) {
            message.setDeleted(1);
            messageRepository.save(message);
        }
        
        logger.info("标记过期消息完成，共 {} 条", expiredMessages.size());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Object getMessageStatistics(Long userId) {
        // TODO: 实现消息统计信息
        return new Object();
    }
    
    @Override
    public void batchMarkAsRead(List<Long> messageIds) {
        logger.info("批量标记为已读: {} 个消息", messageIds.size());
        
        for (Long messageId : messageIds) {
            try {
                markAsRead(messageId);
            } catch (Exception e) {
                logger.error("标记消息为已读失败: messageId={}", messageId, e);
            }
        }
    }
    
    @Override
    public void batchMarkAsUnread(List<Long> messageIds) {
        logger.info("批量标记为未读: {} 个消息", messageIds.size());
        
        for (Long messageId : messageIds) {
            try {
                markAsUnread(messageId);
            } catch (Exception e) {
                logger.error("标记消息为未读失败: messageId={}", messageId, e);
            }
        }
    }
}
