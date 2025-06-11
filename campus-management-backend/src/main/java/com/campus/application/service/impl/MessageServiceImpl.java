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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        List<Message> messages = messageRepository.findByIdAndDeleted(id, 0);
        return messages.isEmpty() ? Optional.empty() : Optional.of(messages.get(0));
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
        try {
            // 注意：当前实现基础的消息统计信息功能，提供用户的消息概览和统计数据
            // 后续可扩展更详细的统计分析，如消息趋势、类型分布等
            logger.debug("获取消息统计信息: userId={}", userId);

            java.util.Map<String, Object> statistics = new java.util.HashMap<>();

            if (userId == null) {
                return statistics;
            }

            // 获取用户相关的消息
            // 注意：使用分页查询方法获取所有消息，然后转换为List
            org.springframework.data.domain.Pageable allPageable = org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE);
            java.util.List<Message> receivedMessages = messageRepository.findByReceiverIdAndDeletedOrderByCreatedAtDesc(userId, 0, allPageable).getContent();
            java.util.List<Message> sentMessages = messageRepository.findBySenderIdAndDeletedOrderByCreatedAtDesc(userId, 0, allPageable).getContent();

            // 基础统计
            statistics.put("totalReceived", receivedMessages.size());
            statistics.put("totalSent", sentMessages.size());

            // 未读消息统计
            long unreadCount = receivedMessages.stream()
                .filter(msg -> msg.getIsRead() != null && !msg.getIsRead())
                .count();
            statistics.put("unreadCount", unreadCount);

            // 消息类型分布
            java.util.Map<String, Long> typeDistribution = receivedMessages.stream()
                .filter(msg -> msg.getMessageType() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                    Message::getMessageType,
                    java.util.stream.Collectors.counting()));
            statistics.put("typeDistribution", typeDistribution);

            // 最近7天的消息统计
            java.time.LocalDateTime sevenDaysAgo = java.time.LocalDateTime.now().minusDays(7);
            long recentReceived = receivedMessages.stream()
                .filter(msg -> msg.getSendTime() != null && msg.getSendTime().isAfter(sevenDaysAgo))
                .count();
            long recentSent = sentMessages.stream()
                .filter(msg -> msg.getSendTime() != null && msg.getSendTime().isAfter(sevenDaysAgo))
                .count();

            statistics.put("recentReceived", recentReceived);
            statistics.put("recentSent", recentSent);

            // 优先级分布
            java.util.Map<String, Long> priorityDistribution = receivedMessages.stream()
                .filter(msg -> msg.getPriority() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                    Message::getPriority,
                    java.util.stream.Collectors.counting()));
            statistics.put("priorityDistribution", priorityDistribution);

            logger.debug("消息统计信息获取完成: {}", statistics);
            return statistics;

        } catch (Exception e) {
            logger.error("获取消息统计信息失败: userId={}", userId, e);
            return new java.util.HashMap<>();
        }
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

    // ================================
    // 消息管理页面需要的方法实现
    // ================================

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMessageTypes() {
        List<Map<String, Object>> types = new ArrayList<>();

        String[] typeNames = {"系统通知", "课程通知", "作业通知", "考试通知", "缴费通知", "个人消息"};
        String[] typeCodes = {"SYSTEM", "COURSE", "ASSIGNMENT", "EXAM", "PAYMENT", "PERSONAL"};

        for (int i = 0; i < typeNames.length; i++) {
            Map<String, Object> type = new HashMap<>();
            type.put("name", typeNames[i]);
            type.put("code", typeCodes[i]);
            type.put("description", typeNames[i] + "类型");
            types.add(type);
        }

        return types;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMessageTemplates() {
        List<Map<String, Object>> templates = new ArrayList<>();

        String[] templateNames = {"课程通知模板", "作业提醒模板", "考试通知模板", "缴费提醒模板"};
        String[] templateContents = {
            "亲爱的同学，您的课程{courseName}将于{time}开始，请准时参加。",
            "您有新的作业{assignmentName}，截止时间为{deadline}，请及时完成。",
            "考试通知：{examName}将于{examTime}举行，考试地点：{location}。",
            "缴费提醒：您有{amount}元的{feeType}费用待缴纳，截止日期：{dueDate}。"
        };

        for (int i = 0; i < templateNames.length; i++) {
            Map<String, Object> template = new HashMap<>();
            template.put("id", (long) (i + 1));
            template.put("name", templateNames[i]);
            template.put("content", templateContents[i]);
            template.put("category", "系统模板");
            template.put("status", 1);
            templates.add(template);
        }

        return templates;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getMessageById(Long messageId) {
        Optional<Message> messageOpt = findMessageById(messageId);
        if (messageOpt.isEmpty()) {
            return null;
        }

        Message message = messageOpt.get();
        Map<String, Object> result = new HashMap<>();
        result.put("id", message.getId());
        result.put("title", message.getTitle());
        result.put("content", message.getContent());
        result.put("messageType", message.getMessageType());
        result.put("senderId", message.getSenderId());
        result.put("receiverId", message.getReceiverId());
        result.put("isRead", message.getIsRead());
        result.put("sentAt", message.getSentAt());
        result.put("status", message.getStatus());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMessageRecipients(Long messageId) {
        List<Map<String, Object>> recipients = new ArrayList<>();

        // 模拟接收者数据
        String[] recipientNames = {"张三", "李四", "王五"};
        String[] recipientTypes = {"学生", "教师", "家长"};

        for (int i = 0; i < recipientNames.length; i++) {
            Map<String, Object> recipient = new HashMap<>();
            recipient.put("id", (long) (i + 1));
            recipient.put("name", recipientNames[i]);
            recipient.put("type", recipientTypes[i]);
            recipient.put("isRead", i % 2 == 0);
            recipient.put("readTime", i % 2 == 0 ? LocalDateTime.now().minusHours(i + 1) : null);
            recipients.add(recipient);
        }

        return recipients;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getMessageReadStatus(Long messageId) {
        Map<String, Object> status = new HashMap<>();
        status.put("totalRecipients", 10);
        status.put("readCount", 7);
        status.put("unreadCount", 3);
        status.put("readRate", 70.0);

        return status;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getInboxMessages() {
        List<Map<String, Object>> messages = new ArrayList<>();

        String[] titles = {"课程通知", "作业提醒", "考试安排", "缴费通知", "系统维护通知"};
        String[] senders = {"教务处", "任课教师", "考试中心", "财务处", "系统管理员"};

        for (int i = 0; i < titles.length; i++) {
            Map<String, Object> message = new HashMap<>();
            message.put("id", (long) (i + 1));
            message.put("title", titles[i]);
            message.put("sender", senders[i]);
            message.put("isRead", i % 3 != 0);
            message.put("sentAt", LocalDateTime.now().minusHours(i + 1));
            message.put("messageType", "SYSTEM");
            messages.add(message);
        }

        return messages;
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadMessageCount() {
        // 模拟未读消息数量
        return 5;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSentMessages() {
        List<Map<String, Object>> messages = new ArrayList<>();

        String[] titles = {"班级通知", "作业布置", "考试提醒"};
        String[] recipients = {"2024级计算机1班", "全体学生", "参考学生"};

        for (int i = 0; i < titles.length; i++) {
            Map<String, Object> message = new HashMap<>();
            message.put("id", (long) (i + 1));
            message.put("title", titles[i]);
            message.put("recipient", recipients[i]);
            message.put("sentAt", LocalDateTime.now().minusDays(i + 1));
            message.put("status", "已发送");
            messages.add(message);
        }

        return messages;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDraftMessages() {
        List<Map<String, Object>> drafts = new ArrayList<>();

        String[] titles = {"期末考试安排草稿", "假期通知草稿"};

        for (int i = 0; i < titles.length; i++) {
            Map<String, Object> draft = new HashMap<>();
            draft.put("id", (long) (i + 1));
            draft.put("title", titles[i]);
            draft.put("lastModified", LocalDateTime.now().minusHours(i + 2));
            draft.put("status", "草稿");
            drafts.add(draft);
        }

        return drafts;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getSystemNotifications() {
        List<Map<String, Object>> notifications = new ArrayList<>();

        String[] notificationTitles = {"系统维护通知", "版本更新通知", "安全提醒", "功能升级通知"};
        String[] notificationTypes = {"维护", "更新", "安全", "功能"};

        for (int i = 0; i < notificationTitles.length; i++) {
            Map<String, Object> notification = new HashMap<>();
            notification.put("id", (long) (i + 1));
            notification.put("title", notificationTitles[i]);
            notification.put("type", notificationTypes[i]);
            notification.put("publishTime", LocalDateTime.now().minusDays(i + 1));
            notification.put("status", "已发布");
            notifications.add(notification);
        }

        return notifications;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getNotificationTypes() {
        List<Map<String, Object>> types = new ArrayList<>();

        String[] typeNames = {"系统通知", "维护通知", "安全通知", "功能通知"};
        String[] typeCodes = {"SYSTEM", "MAINTENANCE", "SECURITY", "FEATURE"};

        for (int i = 0; i < typeNames.length; i++) {
            Map<String, Object> type = new HashMap<>();
            type.put("name", typeNames[i]);
            type.put("code", typeCodes[i]);
            type.put("description", typeNames[i] + "类型");
            types.add(type);
        }

        return types;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTemplateCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();

        String[] categoryNames = {"系统模板", "课程模板", "考试模板", "通知模板"};
        String[] descriptions = {"系统相关模板", "课程相关模板", "考试相关模板", "通知相关模板"};

        for (int i = 0; i < categoryNames.length; i++) {
            Map<String, Object> category = new HashMap<>();
            category.put("id", (long) (i + 1));
            category.put("name", categoryNames[i]);
            category.put("description", descriptions[i]);
            category.put("templateCount", (i + 1) * 3);
            categories.add(category);
        }

        return categories;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTemplateById(Long templateId) {
        Map<String, Object> template = new HashMap<>();
        template.put("id", templateId);
        template.put("name", "模板 " + templateId);
        template.put("content", "这是模板内容 {placeholder}");
        template.put("category", "系统模板");
        template.put("status", 1);
        template.put("createTime", LocalDateTime.now().minusDays(1));

        return template;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getOverallStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 模拟整体统计数据
        stats.put("totalMessages", 1250);
        stats.put("unreadMessages", 85);
        stats.put("sentToday", 45);
        stats.put("activeUsers", 320);
        stats.put("systemNotifications", 12);
        stats.put("readRate", 87.5);

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUserMessageStats() {
        List<Map<String, Object>> userStats = new ArrayList<>();

        String[] userNames = {"张三", "李四", "王五", "赵六"};
        String[] userTypes = {"学生", "教师", "管理员", "家长"};

        for (int i = 0; i < userNames.length; i++) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("userName", userNames[i]);
            stat.put("userType", userTypes[i]);
            stat.put("sentCount", (i + 1) * 15);
            stat.put("receivedCount", (i + 1) * 25);
            stat.put("unreadCount", (i + 1) * 3);
            userStats.add(stat);
        }

        return userStats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMessageTypeStats() {
        List<Map<String, Object>> typeStats = new ArrayList<>();

        String[] types = {"系统通知", "课程通知", "作业通知", "考试通知", "缴费通知"};
        int[] counts = {120, 85, 95, 45, 65};

        for (int i = 0; i < types.length; i++) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("type", types[i]);
            stat.put("count", counts[i]);
            stat.put("percentage", (counts[i] / 410.0) * 100);
            typeStats.add(stat);
        }

        return typeStats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMessageTrendStats() {
        List<Map<String, Object>> trendStats = new ArrayList<>();

        // 模拟最近7天的消息趋势
        for (int i = 6; i >= 0; i--) {
            Map<String, Object> trend = new HashMap<>();
            trend.put("date", LocalDateTime.now().minusDays(i).toLocalDate());
            trend.put("sentCount", 15 + (int)(Math.random() * 20));
            trend.put("receivedCount", 25 + (int)(Math.random() * 30));
            trendStats.add(trend);
        }

        return trendStats;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getMessageSettings() {
        Map<String, Object> settings = new HashMap<>();

        settings.put("enableEmailNotification", true);
        settings.put("enableSMSNotification", false);
        settings.put("autoDeleteDays", 30);
        settings.put("maxMessageLength", 1000);
        settings.put("allowAttachments", true);
        settings.put("maxAttachmentSize", "10MB");

        return settings;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getNotificationSettings() {
        Map<String, Object> settings = new HashMap<>();

        settings.put("enablePushNotification", true);
        settings.put("enableBrowserNotification", true);
        settings.put("notificationSound", true);
        settings.put("quietHoursStart", "22:00");
        settings.put("quietHoursEnd", "08:00");
        settings.put("weekendNotifications", false);

        return settings;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getScheduledMessages() {
        List<Map<String, Object>> scheduledMessages = new ArrayList<>();

        String[] titles = {"期末考试通知", "假期安排通知", "开学准备通知"};
        String[] schedules = {"2024-01-15 09:00", "2024-01-20 10:00", "2024-02-25 08:00"};

        for (int i = 0; i < titles.length; i++) {
            Map<String, Object> message = new HashMap<>();
            message.put("id", (long) (i + 1));
            message.put("title", titles[i]);
            message.put("scheduledTime", schedules[i]);
            message.put("status", i % 2 == 0 ? "待发送" : "已发送");
            message.put("recipientCount", (i + 1) * 50);
            scheduledMessages.add(message);
        }

        return scheduledMessages;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getArchivedMessages() {
        List<Map<String, Object>> archivedMessages = new ArrayList<>();

        String[] titles = {"2023年度总结通知", "暑期实习安排", "毕业典礼通知"};
        String[] archiveDates = {"2023-12-31", "2023-06-15", "2023-06-30"};

        for (int i = 0; i < titles.length; i++) {
            Map<String, Object> message = new HashMap<>();
            message.put("id", (long) (i + 1));
            message.put("title", titles[i]);
            message.put("archiveDate", archiveDates[i]);
            message.put("originalSentDate", LocalDateTime.now().minusMonths(i + 6));
            message.put("recipientCount", (i + 1) * 100);
            archivedMessages.add(message);
        }

        return archivedMessages;
    }
}
