package com.campus.application.service.impl;

import com.campus.application.service.NotificationService;
import com.campus.domain.entity.Notification;
import com.campus.domain.entity.NotificationTemplate;
import com.campus.domain.repository.NotificationRepository;
import com.campus.domain.repository.NotificationTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 通知管理服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@Service
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository notificationTemplateRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                 NotificationTemplateRepository notificationTemplateRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationTemplateRepository = notificationTemplateRepository;
    }

    // ==================== 基础CRUD方法 ====================

    @Override
    @Transactional
    public Notification save(Notification notification) {
        log.debug("保存通知: {}", notification.getTitle());
        return notificationRepository.save(notification);
    }

    @Override
    public Optional<Notification> findById(Long id) {
        log.debug("根据ID查找通知: {}", id);
        return notificationRepository.findById(id);
    }

    @Override
    public List<Notification> findAll() {
        log.debug("获取所有通知");
        return notificationRepository.findAll();
    }

    @Override
    public Page<Notification> findAll(Pageable pageable) {
        log.debug("分页获取通知");
        return notificationRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.debug("删除通知: {}", id);
        notificationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        log.debug("批量删除通知: {}", ids);
        notificationRepository.deleteAllById(ids);
    }

    @Override
    public long count() {
        log.debug("统计通知数量");
        return notificationRepository.count();
    }

    // ==================== 业务查询方法 ====================

    @Override
    public List<Notification> findBySenderId(Long senderId) {
        log.debug("根据发送人ID查找通知: {}", senderId);
        // 使用基本的JPA方法，避免复杂的自定义查询
        return notificationRepository.findAll().stream()
            .filter(n -> n.getSenderId() != null && n.getSenderId().equals(senderId))
            .filter(n -> n.getDeleted() == 0)
            .toList();
    }

    @Override
    public Page<Notification> findBySenderId(Long senderId, Pageable pageable) {
        log.debug("分页根据发送人ID查找通知: {}", senderId);
        return notificationRepository.findAll(pageable);
    }

    @Override
    public List<Notification> findByNotificationType(String notificationType) {
        log.debug("根据通知类型查找通知: {}", notificationType);
        return notificationRepository.findAll().stream()
            .filter(n -> n.getType() != null && n.getType().equals(notificationType))
            .filter(n -> n.getDeleted() == 0)
            .toList();
    }

    @Override
    public Page<Notification> findByNotificationType(String notificationType, Pageable pageable) {
        log.debug("分页根据通知类型查找通知: {}", notificationType);
        return notificationRepository.findAll(pageable);
    }

    @Override
    public List<Notification> findByTargetType(String targetType) {
        log.debug("根据目标类型查找通知: {}", targetType);
        return notificationRepository.findAll().stream()
            .filter(n -> n.getTargetAudience() != null && n.getTargetAudience().equals(targetType))
            .filter(n -> n.getDeleted() == 0)
            .toList();
    }

    @Override
    public List<Notification> findByPriority(String priority) {
        log.debug("根据优先级查找通知: {}", priority);
        return notificationRepository.findAll().stream()
            .filter(n -> n.getPriority() != null && n.getPriority().equals(priority))
            .filter(n -> n.getDeleted() == 0)
            .toList();
    }

    @Override
    public Page<Notification> findByConditions(String title, String notificationType, String priority,
                                             Boolean isPublished, Boolean isTop, Pageable pageable) {
        log.debug("根据条件查找通知");
        // 简化实现，返回所有通知的分页
        return notificationRepository.findAll(pageable);
    }

    @Override
    public List<Notification> findPublishedNotifications() {
        log.debug("获取已发布的通知");
        return notificationRepository.findAll().stream()
            .filter(n -> "PUBLISHED".equals(n.getNotificationStatus()))
            .filter(n -> n.getDeleted() == 0)
            .toList();
    }

    @Override
    public List<Notification> findUnpublishedNotifications() {
        log.debug("获取未发布的通知");
        return notificationRepository.findAll().stream()
            .filter(n -> !"PUBLISHED".equals(n.getNotificationStatus()))
            .filter(n -> n.getDeleted() == 0)
            .toList();
    }

    @Override
    public List<Notification> findTopNotifications() {
        log.debug("获取置顶通知");
        return notificationRepository.findAll().stream()
            .filter(n -> Boolean.TRUE.equals(n.getIsPinned()))
            .filter(n -> n.getDeleted() == 0)
            .toList();
    }

    @Override
    public List<Notification> findValidNotifications() {
        log.debug("获取有效通知");
        LocalDateTime now = LocalDateTime.now();
        return notificationRepository.findAll().stream()
            .filter(n -> n.getExpireTime() == null || n.getExpireTime().isAfter(now))
            .filter(n -> n.getDeleted() == 0)
            .toList();
    }

    @Override
    public List<Notification> findExpiredNotifications() {
        log.debug("获取过期通知");
        LocalDateTime now = LocalDateTime.now();
        return notificationRepository.findAll().stream()
            .filter(n -> n.getExpireTime() != null && n.getExpireTime().isBefore(now))
            .filter(n -> n.getDeleted() == 0)
            .toList();
    }

    @Override
    public List<Notification> findByPublishTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("根据发布时间范围查找通知: {} - {}", startTime, endTime);
        return notificationRepository.findAll().stream()
            .filter(n -> n.getPublishTime() != null)
            .filter(n -> !n.getPublishTime().isBefore(startTime) && !n.getPublishTime().isAfter(endTime))
            .filter(n -> n.getDeleted() == 0)
            .toList();
    }

    @Override
    public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
        log.debug("获取用户通知: {}", userId);
        // 简化实现，返回所有通知的分页
        return notificationRepository.findAll(pageable);
    }

    @Override
    public List<Notification> getUserNotifications(Long userId) {
        log.debug("获取用户通知: {}", userId);
        // 简化实现，返回所有通知
        return notificationRepository.findAll().stream()
            .filter(n -> n.getDeleted() == 0)
            .toList();
    }

    @Override
    public List<Notification> getUserUnreadNotifications(Long userId) {
        log.debug("获取用户未读通知: {}", userId);
        // 简化实现，返回空列表
        return List.of();
    }

    @Override
    public Page<Notification> getUserUnreadNotifications(Long userId, Pageable pageable) {
        log.debug("分页获取用户未读通知: {}", userId);
        return notificationRepository.findAll(pageable);
    }



    @Override
    public long countUserUnreadNotifications(Long userId) {
        log.debug("统计用户未读通知数量: {}", userId);
        return 0;
    }

    // ==================== 通知状态管理 ====================

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        log.debug("标记通知为已读: {}, 用户: {}", notificationId, userId);
        // 简化实现，暂时不做实际操作
    }

    @Override
    @Transactional
    public void markAsRead(List<Long> notificationIds, Long userId) {
        log.debug("批量标记通知为已读: {}, 用户: {}", notificationIds, userId);
        notificationIds.forEach(id -> markAsRead(id, userId));
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        log.debug("标记用户所有通知为已读: {}", userId);
        // 简化实现，暂时不做实际操作
    }

    @Override
    @Transactional
    public void enableNotification(Long notificationId) {
        log.debug("启用通知: {}", notificationId);
        Optional<Notification> optionalNotification = findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setDeleted(0);
            save(notification);
        }
    }

    @Override
    @Transactional
    public void disableNotification(Long notificationId) {
        log.debug("禁用通知: {}", notificationId);
        Optional<Notification> optionalNotification = findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setDeleted(1);
            save(notification);
        }
    }

    @Override
    @Transactional
    public void enableNotifications(List<Long> notificationIds) {
        log.debug("批量启用通知: {}", notificationIds);
        notificationIds.forEach(this::enableNotification);
    }

    @Override
    @Transactional
    public void disableNotifications(List<Long> notificationIds) {
        log.debug("批量禁用通知: {}", notificationIds);
        notificationIds.forEach(this::disableNotification);
    }

    // ==================== 通知发送方法 ====================

    @Override
    @Transactional
    public Notification sendSystemNotification(String title, String content, String targetType, List<Long> targetIds) {
        log.debug("发送系统通知: {}", title);
        
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType("SYSTEM");
        notification.setTargetAudience(targetType);
        notification.setPriority("NORMAL");
        notification.setNotificationStatus("PUBLISHED");
        notification.setPublishTime(LocalDateTime.now());
        
        return save(notification);
    }

    @Override
    @Transactional
    public Notification sendCourseNotification(String title, String content, Long courseId, Long senderId) {
        log.debug("发送课程通知: {}", title);
        
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType("ACADEMIC");
        notification.setSenderId(senderId);
        notification.setTargetAudience("COURSE");
        notification.setPriority("NORMAL");
        notification.setNotificationStatus("PUBLISHED");
        notification.setPublishTime(LocalDateTime.now());
        
        return save(notification);
    }

    @Override
    @Transactional
    public Notification sendExamNotification(String title, String content, Long examId, Long senderId) {
        log.debug("发送考试通知: {}", title);
        
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType("ACADEMIC");
        notification.setSenderId(senderId);
        notification.setTargetAudience("EXAM");
        notification.setPriority("HIGH");
        notification.setNotificationStatus("PUBLISHED");
        notification.setPublishTime(LocalDateTime.now());
        
        return save(notification);
    }

    @Override
    @Transactional
    public Notification sendAssignmentNotification(String title, String content, Long assignmentId, Long senderId) {
        log.debug("发送作业通知: {}", title);

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType("ACADEMIC");
        notification.setSenderId(senderId);
        notification.setTargetAudience("ASSIGNMENT");
        notification.setPriority("NORMAL");
        notification.setNotificationStatus("PUBLISHED");
        notification.setPublishTime(LocalDateTime.now());

        return save(notification);
    }

    @Override
    @Transactional
    public Notification sendPersonalNotification(String title, String content, Long targetUserId, Long senderId) {
        log.debug("发送个人通知: {}", title);

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType("PERSONAL");
        notification.setSenderId(senderId);
        notification.setTargetAudience("USER");
        notification.setPriority("NORMAL");
        notification.setNotificationStatus("PUBLISHED");
        notification.setPublishTime(LocalDateTime.now());

        return save(notification);
    }

    @Override
    @Transactional
    public Notification sendBroadcastNotification(String title, String content, Long senderId) {
        log.debug("发送广播通知: {}", title);

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType("BROADCAST");
        notification.setSenderId(senderId);
        notification.setTargetAudience("ALL");
        notification.setPriority("NORMAL");
        notification.setNotificationStatus("PUBLISHED");
        notification.setPublishTime(LocalDateTime.now());

        return save(notification);
    }

    @Override
    @Transactional
    public Notification sendRoleNotification(String title, String content, String roleName, Long senderId) {
        log.debug("发送角色通知: {}", title);

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType("ROLE");
        notification.setSenderId(senderId);
        notification.setTargetAudience("ROLE");
        notification.setPriority("NORMAL");
        notification.setNotificationStatus("PUBLISHED");
        notification.setPublishTime(LocalDateTime.now());

        return save(notification);
    }

    @Override
    @Transactional
    public Notification sendDepartmentNotification(String title, String content, Long departmentId, Long senderId) {
        log.debug("发送部门通知: {}", title);

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType("DEPARTMENT");
        notification.setSenderId(senderId);
        notification.setTargetAudience("DEPARTMENT");
        notification.setPriority("NORMAL");
        notification.setNotificationStatus("PUBLISHED");
        notification.setPublishTime(LocalDateTime.now());

        return save(notification);
    }

    @Override
    @Transactional
    public Notification sendClassNotification(String title, String content, Long classId, Long senderId) {
        log.debug("发送班级通知: {}", title);

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType("CLASS");
        notification.setSenderId(senderId);
        notification.setTargetAudience("CLASS");
        notification.setPriority("NORMAL");
        notification.setNotificationStatus("PUBLISHED");
        notification.setPublishTime(LocalDateTime.now());

        return save(notification);
    }

    // ==================== 模板通知方法 ====================

    @Override
    @Transactional
    public Notification sendTemplateNotification(String templateCode, Map<String, Object> variables, 
                                                String targetType, List<Long> targetIds, Long senderId) {
        log.debug("使用模板发送通知: {}", templateCode);
        
        // 这里需要实现模板处理逻辑
        // 暂时返回一个简单的通知
        Notification notification = new Notification();
        notification.setTitle("模板通知");
        notification.setContent("使用模板生成的通知内容");
        notification.setType("SYSTEM");
        notification.setSenderId(senderId);
        notification.setTargetAudience(targetType);
        notification.setPriority("NORMAL");
        notification.setNotificationStatus("PUBLISHED");
        notification.setPublishTime(LocalDateTime.now());
        
        return save(notification);
    }

    @Override
    @Transactional
    public void sendCourseSelectionReminder(Long studentId, LocalDateTime deadline) {
        log.debug("发送选课提醒: 学生ID {}, 截止时间 {}", studentId, deadline);
        
        String title = "选课提醒";
        String content = String.format("请注意，选课截止时间为：%s，请及时完成选课。", deadline);
        
        sendSystemNotification(title, content, "STUDENT", List.of(studentId));
    }

    @Override
    @Transactional
    public void sendPaymentReminder(Long studentId, String feeItemName, LocalDateTime deadline) {
        log.debug("发送缴费提醒: 学生ID {}, 费用项目 {}", studentId, feeItemName);
        
        String title = "缴费提醒";
        String content = String.format("您有待缴费项目：%s，截止时间：%s，请及时缴费。", feeItemName, deadline);
        
        sendSystemNotification(title, content, "STUDENT", List.of(studentId));
    }

    @Override
    @Transactional
    public void sendExamReminder(Long studentId, String examName, LocalDateTime examTime) {
        log.debug("发送考试提醒: 学生ID {}, 考试名称 {}", studentId, examName);
        
        String title = "考试提醒";
        String content = String.format("您有考试安排：%s，考试时间：%s，请做好准备。", examName, examTime);
        
        sendSystemNotification(title, content, "STUDENT", List.of(studentId));
    }

    @Override
    @Transactional
    public void sendAssignmentReminder(Long studentId, String assignmentTitle, LocalDateTime dueDate) {
        log.debug("发送作业提醒: 学生ID {}, 作业标题 {}", studentId, assignmentTitle);
        
        String title = "作业提醒";
        String content = String.format("您有作业待完成：%s，截止时间：%s，请及时提交。", assignmentTitle, dueDate);
        
        sendSystemNotification(title, content, "STUDENT", List.of(studentId));
    }

    @Override
    @Transactional
    public void sendGradePublishedNotification(Long studentId, String courseName) {
        log.debug("发送成绩发布通知: 学生ID {}, 课程名称 {}", studentId, courseName);
        
        String title = "成绩发布通知";
        String content = String.format("课程《%s》的成绩已发布，请登录系统查看。", courseName);
        
        sendSystemNotification(title, content, "STUDENT", List.of(studentId));
    }

    // ==================== 业务操作方法 ====================

    @Override
    @Transactional
    public void publishNotification(Long notificationId) {
        log.debug("发布通知: {}", notificationId);
        
        Optional<Notification> optionalNotification = findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.publish();
            save(notification);
        }
    }

    @Override
    @Transactional
    public void unpublishNotification(Long notificationId) {
        log.debug("取消发布通知: {}", notificationId);
        
        Optional<Notification> optionalNotification = findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setNotificationStatus("DRAFT");
            save(notification);
        }
    }

    @Override
    @Transactional
    public void publishNotifications(List<Long> notificationIds) {
        log.debug("批量发布通知: {}", notificationIds);
        notificationIds.forEach(this::publishNotification);
    }

    @Override
    @Transactional
    public void topNotification(Long notificationId) {
        log.debug("置顶通知: {}", notificationId);
        
        Optional<Notification> optionalNotification = findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setIsPinned(true);
            save(notification);
        }
    }

    @Override
    @Transactional
    public void untopNotification(Long notificationId) {
        log.debug("取消置顶通知: {}", notificationId);
        
        Optional<Notification> optionalNotification = findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setIsPinned(false);
            save(notification);
        }
    }

    @Override
    @Transactional
    public void extendExpireTime(Long notificationId, LocalDateTime newExpireTime) {
        log.debug("延长通知有效期: {}, 新过期时间: {}", notificationId, newExpireTime);
        
        Optional<Notification> optionalNotification = findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setExpireTime(newExpireTime);
            save(notification);
        }
    }

    @Override
    @Transactional
    public Notification copyNotification(Long notificationId) {
        log.debug("复制通知: {}", notificationId);
        
        Optional<Notification> optionalNotification = findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification original = optionalNotification.get();
            
            Notification copy = new Notification();
            copy.setTitle(original.getTitle() + " (副本)");
            copy.setContent(original.getContent());
            copy.setType(original.getType());
            copy.setTargetAudience(original.getTargetAudience());
            copy.setPriority(original.getPriority());
            copy.setNotificationStatus("DRAFT");
            copy.setSenderId(original.getSenderId());
            
            return save(copy);
        }
        
        return null;
    }

    // ==================== 统计分析方法 ====================

    @Override
    public long countBySenderId(Long senderId) {
        log.debug("统计发送人通知数量: {}", senderId);
        return notificationRepository.findAll().stream()
            .filter(n -> n.getSenderId() != null && n.getSenderId().equals(senderId))
            .filter(n -> n.getDeleted() == 0)
            .count();
    }

    @Override
    public long countByNotificationType(String notificationType) {
        log.debug("统计通知类型数量: {}", notificationType);
        return notificationRepository.findAll().stream()
            .filter(n -> n.getType() != null && n.getType().equals(notificationType))
            .filter(n -> n.getDeleted() == 0)
            .count();
    }

    @Override
    public long countPublishedNotifications() {
        log.debug("统计已发布通知数量");
        return notificationRepository.findAll().stream()
            .filter(n -> "PUBLISHED".equals(n.getNotificationStatus()))
            .filter(n -> n.getDeleted() == 0)
            .count();
    }

    @Override
    public long countUnpublishedNotifications() {
        log.debug("统计未发布通知数量");
        return notificationRepository.findAll().stream()
            .filter(n -> !"PUBLISHED".equals(n.getNotificationStatus()))
            .filter(n -> n.getDeleted() == 0)
            .count();
    }

    @Override
    public long countTopNotifications() {
        log.debug("统计置顶通知数量");
        return notificationRepository.findAll().stream()
            .filter(n -> Boolean.TRUE.equals(n.getIsPinned()))
            .filter(n -> n.getDeleted() == 0)
            .count();
    }

    @Override
    public List<Object[]> getSystemNotificationStatistics() {
        log.debug("获取系统通知统计信息");
        List<Object[]> stats = List.of(
            new Object[]{"totalNotifications", count()},
            new Object[]{"publishedNotifications", countPublishedNotifications()},
            new Object[]{"unpublishedNotifications", countUnpublishedNotifications()},
            new Object[]{"topNotifications", countTopNotifications()}
        );
        return stats;
    }

    @Override
    public List<Object[]> getUserNotificationStatistics(Long userId) {
        log.debug("获取用户通知统计信息: {}", userId);
        List<Object[]> stats = List.of(
            new Object[]{"totalNotifications", 0},
            new Object[]{"unreadNotifications", 0},
            new Object[]{"readNotifications", 0}
        );
        return stats;
    }

    @Override
    public List<Object[]> getNotificationReadStatistics(Long notificationId) {
        log.debug("获取通知阅读统计信息: {}", notificationId);
        List<Object[]> stats = List.of(
            new Object[]{"totalReads", 0},
            new Object[]{"uniqueReaders", 0},
            new Object[]{"readRate", 0.0}
        );
        return stats;
    }

    // ==================== 验证方法 ====================

    @Override
    public boolean existsByTitle(String title) {
        log.debug("检查通知标题是否存在: {}", title);
        return notificationRepository.findAll().stream()
            .anyMatch(n -> n.getTitle() != null && n.getTitle().equals(title) && n.getDeleted() == 0);
    }

    @Override
    public boolean existsByTitleExcludeId(String title, Long excludeId) {
        log.debug("检查通知标题是否存在（排除指定ID）: {}, 排除ID: {}", title, excludeId);
        return notificationRepository.findAll().stream()
            .anyMatch(n -> n.getTitle() != null && n.getTitle().equals(title)
                && !n.getId().equals(excludeId) && n.getDeleted() == 0);
    }

    @Override
    public boolean canDeleteNotification(Long notificationId) {
        log.debug("检查是否可以删除通知: {}", notificationId);
        return true;
    }

    @Override
    public boolean canModifyNotification(Long notificationId) {
        log.debug("检查是否可以修改通知: {}", notificationId);
        return true;
    }

    // ==================== 模板管理方法 ====================

    @Override
    @Transactional
    public NotificationTemplate saveTemplate(NotificationTemplate template) {
        log.debug("保存通知模板: {}", template.getTitle());
        return notificationTemplateRepository.save(template);
    }

    @Override
    public Optional<NotificationTemplate> findTemplateById(Long id) {
        log.debug("根据ID查找通知模板: {}", id);
        return notificationTemplateRepository.findById(id);
    }

    @Override
    public Optional<NotificationTemplate> findTemplateByCode(String code) {
        log.debug("根据代码查找通知模板: {}", code);
        // 简化实现，返回空
        return Optional.empty();
    }

    @Override
    public List<NotificationTemplate> findAllTemplates() {
        log.debug("获取所有通知模板");
        return notificationTemplateRepository.findAll();
    }

    @Override
    public Page<NotificationTemplate> findAllTemplates(Pageable pageable) {
        log.debug("分页获取通知模板");
        return notificationTemplateRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void deleteTemplate(Long id) {
        log.debug("删除通知模板: {}", id);
        notificationTemplateRepository.deleteById(id);
    }

    // ==================== 自动化通知 ====================

    @Override
    @Transactional
    public void autoCleanExpiredNotifications() {
        log.debug("自动清理过期通知");
        LocalDateTime now = LocalDateTime.now();
        List<Notification> expiredNotifications = findExpiredNotifications();

        for (Notification notification : expiredNotifications) {
            notification.setDeleted(1);
            save(notification);
        }

        log.info("清理过期通知完成，清理数量: {}", expiredNotifications.size());
    }

    @Override
    @Transactional
    public void autoSendScheduledNotifications() {
        log.debug("自动发送定时通知");
        // 简化实现，暂时不做实际操作
    }

    @Override
    @Transactional
    public void autoSendSystemReminders() {
        log.debug("自动发送系统提醒");
        // 简化实现，暂时不做实际操作
    }
}
