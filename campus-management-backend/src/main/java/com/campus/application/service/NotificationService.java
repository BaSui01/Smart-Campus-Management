package com.campus.application.service;

import com.campus.domain.entity.Notification;
import com.campus.domain.entity.NotificationTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 通知管理服务接口
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
public interface NotificationService {

    // ==================== 基础CRUD方法 ====================

    /**
     * 保存通知
     */
    Notification save(Notification notification);

    /**
     * 根据ID查找通知
     */
    Optional<Notification> findById(Long id);

    /**
     * 获取所有通知
     */
    List<Notification> findAll();

    /**
     * 分页获取通知
     */
    Page<Notification> findAll(Pageable pageable);

    /**
     * 删除通知
     */
    void deleteById(Long id);

    /**
     * 批量删除通知
     */
    void deleteByIds(List<Long> ids);

    /**
     * 统计通知数量
     */
    long count();

    // ==================== 业务查询方法 ====================

    /**
     * 根据发送人ID查找通知
     */
    List<Notification> findBySenderId(Long senderId);

    /**
     * 根据通知类型查找通知
     */
    List<Notification> findByNotificationType(String notificationType);

    /**
     * 根据优先级查找通知
     */
    List<Notification> findByPriority(String priority);

    /**
     * 查找已发布的通知
     */
    List<Notification> findPublishedNotifications();

    /**
     * 查找未发布的通知
     */
    List<Notification> findUnpublishedNotifications();

    /**
     * 查找置顶通知
     */
    List<Notification> findTopNotifications();

    /**
     * 根据目标类型查找通知
     */
    List<Notification> findByTargetType(String targetType);

    /**
     * 根据发布时间范围查找通知
     */
    List<Notification> findByPublishTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查找有效期内的通知
     */
    List<Notification> findValidNotifications();

    /**
     * 查找已过期的通知
     */
    List<Notification> findExpiredNotifications();

    // ==================== 分页查询方法 ====================

    /**
     * 根据发送人ID分页查找通知
     */
    Page<Notification> findBySenderId(Long senderId, Pageable pageable);

    /**
     * 根据通知类型分页查找通知
     */
    Page<Notification> findByNotificationType(String notificationType, Pageable pageable);

    /**
     * 根据条件分页查询通知
     */
    Page<Notification> findByConditions(String title, String notificationType, 
                                       String priority, Boolean isPublished, 
                                       Boolean isTop, Pageable pageable);

    // ==================== 用户通知查询 ====================

    /**
     * 获取用户的通知
     */
    List<Notification> getUserNotifications(Long userId);

    /**
     * 分页获取用户的通知
     */
    Page<Notification> getUserNotifications(Long userId, Pageable pageable);

    /**
     * 获取用户未读通知
     */
    List<Notification> getUserUnreadNotifications(Long userId);

    /**
     * 分页获取用户未读通知
     */
    Page<Notification> getUserUnreadNotifications(Long userId, Pageable pageable);

    /**
     * 统计用户未读通知数量
     */
    long countUserUnreadNotifications(Long userId);

    /**
     * 标记通知为已读
     */
    void markAsRead(Long notificationId, Long userId);

    /**
     * 批量标记通知为已读
     */
    void markAsRead(List<Long> notificationIds, Long userId);

    /**
     * 标记用户所有通知为已读
     */
    void markAllAsRead(Long userId);

    // ==================== 通知发送方法 ====================

    /**
     * 发送系统通知
     */
    Notification sendSystemNotification(String title, String content, String targetType, List<Long> targetIds);

    /**
     * 发送课程通知
     */
    Notification sendCourseNotification(String title, String content, Long courseId, Long senderId);

    /**
     * 发送考试通知
     */
    Notification sendExamNotification(String title, String content, Long examId, Long senderId);

    /**
     * 发送作业通知
     */
    Notification sendAssignmentNotification(String title, String content, Long assignmentId, Long senderId);

    /**
     * 发送个人通知
     */
    Notification sendPersonalNotification(String title, String content, Long targetUserId, Long senderId);

    /**
     * 发送角色通知
     */
    Notification sendRoleNotification(String title, String content, String roleName, Long senderId);

    /**
     * 发送班级通知
     */
    Notification sendClassNotification(String title, String content, Long classId, Long senderId);

    /**
     * 发送院系通知
     */
    Notification sendDepartmentNotification(String title, String content, Long departmentId, Long senderId);

    /**
     * 发送广播通知
     */
    Notification sendBroadcastNotification(String title, String content, Long senderId);

    // ==================== 模板通知方法 ====================

    /**
     * 使用模板发送通知
     */
    Notification sendTemplateNotification(String templateCode, Map<String, Object> variables, 
                                        String targetType, List<Long> targetIds, Long senderId);

    /**
     * 发送选课提醒
     */
    void sendCourseSelectionReminder(Long studentId, LocalDateTime deadline);

    /**
     * 发送缴费提醒
     */
    void sendPaymentReminder(Long studentId, String feeItemName, LocalDateTime deadline);

    /**
     * 发送考试提醒
     */
    void sendExamReminder(Long studentId, String examName, LocalDateTime examTime);

    /**
     * 发送作业提醒
     */
    void sendAssignmentReminder(Long studentId, String assignmentTitle, LocalDateTime dueDate);

    /**
     * 发送成绩发布通知
     */
    void sendGradePublishedNotification(Long studentId, String courseName);

    // ==================== 业务操作方法 ====================

    /**
     * 发布通知
     */
    void publishNotification(Long notificationId);

    /**
     * 取消发布通知
     */
    void unpublishNotification(Long notificationId);

    /**
     * 批量发布通知
     */
    void publishNotifications(List<Long> notificationIds);

    /**
     * 置顶通知
     */
    void topNotification(Long notificationId);

    /**
     * 取消置顶通知
     */
    void untopNotification(Long notificationId);

    /**
     * 延长通知有效期
     */
    void extendExpireTime(Long notificationId, LocalDateTime newExpireTime);

    /**
     * 复制通知
     */
    Notification copyNotification(Long notificationId);

    // ==================== 统计分析方法 ====================

    /**
     * 统计发送人通知数量
     */
    long countBySenderId(Long senderId);

    /**
     * 统计通知类型数量
     */
    long countByNotificationType(String notificationType);

    /**
     * 统计已发布通知数量
     */
    long countPublishedNotifications();

    /**
     * 统计未发布通知数量
     */
    long countUnpublishedNotifications();

    /**
     * 统计置顶通知数量
     */
    long countTopNotifications();

    /**
     * 获取通知阅读统计
     */
    List<Object[]> getNotificationReadStatistics(Long notificationId);

    /**
     * 获取用户通知统计
     */
    List<Object[]> getUserNotificationStatistics(Long userId);

    /**
     * 获取系统通知统计
     */
    List<Object[]> getSystemNotificationStatistics();

    // ==================== 模板管理方法 ====================

    /**
     * 保存通知模板
     */
    NotificationTemplate saveTemplate(NotificationTemplate template);

    /**
     * 根据ID查找模板
     */
    Optional<NotificationTemplate> findTemplateById(Long id);

    /**
     * 根据模板代码查找模板
     */
    Optional<NotificationTemplate> findTemplateByCode(String templateCode);

    /**
     * 获取所有模板
     */
    List<NotificationTemplate> findAllTemplates();

    /**
     * 分页获取模板
     */
    Page<NotificationTemplate> findAllTemplates(Pageable pageable);

    /**
     * 删除模板
     */
    void deleteTemplate(Long id);

    // ==================== 模板查询方法 ====================

    /**
     * 分页查询模板
     */
    Page<NotificationTemplate> findTemplates(Pageable pageable, Map<String, Object> params);

    /**
     * 根据ID获取模板
     */
    NotificationTemplate getTemplateById(Long id);

    /**
     * 根据类型获取模板
     */
    List<NotificationTemplate> getTemplatesByType(String templateType);

    /**
     * 检查模板名称是否存在
     */
    boolean existsByTemplateName(String templateName);

    /**
     * 更新模板
     */
    NotificationTemplate updateTemplate(NotificationTemplate template);

    /**
     * 复制模板
     */
    NotificationTemplate copyTemplate(Long templateId, String newTemplateName);

    /**
     * 预览模板
     */
    Map<String, Object> previewTemplate(Long templateId, Map<String, Object> variables);

    /**
     * 获取模板变量
     */
    List<String> getTemplateVariables(Long templateId);

    /**
     * 批量更新模板状态
     */
    int batchUpdateTemplateStatus(List<Long> templateIds, String status);

    /**
     * 获取所有模板类型
     */
    List<String> getAllTemplateTypes();

    /**
     * 获取所有通知渠道
     */
    List<String> getAllNotificationChannels();

    /**
     * 统计模板总数
     */
    long countTotalTemplates();

    /**
     * 统计活跃模板数
     */
    long countActiveTemplates();

    /**
     * 统计非活跃模板数
     */
    long countInactiveTemplates();

    /**
     * 获取模板类型统计
     */
    Map<String, Object> getTemplateTypeStatistics();

    /**
     * 获取渠道统计
     */
    Map<String, Object> getChannelStatistics();

    /**
     * 获取模板使用统计
     */
    Map<String, Object> getTemplateUsageStatistics();

    // ==================== 自动化通知 ====================

    /**
     * 自动清理过期通知
     */
    void autoCleanExpiredNotifications();

    /**
     * 自动发送定时通知
     */
    void autoSendScheduledNotifications();

    /**
     * 自动发送系统提醒
     */
    void autoSendSystemReminders();

    // ==================== 验证方法 ====================

    /**
     * 检查通知标题是否存在
     */
    boolean existsByTitle(String title);

    /**
     * 检查通知标题是否存在（排除指定ID）
     */
    boolean existsByTitleExcludeId(String title, Long excludeId);

    /**
     * 检查是否可以删除通知
     */
    boolean canDeleteNotification(Long notificationId);

    /**
     * 检查是否可以修改通知
     */
    boolean canModifyNotification(Long notificationId);

    // ==================== 状态管理方法 ====================

    /**
     * 启用通知
     */
    void enableNotification(Long id);

    /**
     * 禁用通知
     */
    void disableNotification(Long id);

    /**
     * 批量启用通知
     */
    void enableNotifications(List<Long> ids);

    /**
     * 批量禁用通知
     */
    void disableNotifications(List<Long> ids);
}
