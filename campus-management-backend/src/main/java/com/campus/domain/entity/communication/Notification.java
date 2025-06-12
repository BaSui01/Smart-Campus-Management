package com.campus.domain.entity.communication;

import com.campus.domain.entity.auth.User;
import com.campus.domain.entity.infrastructure.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * 通知实体类
 * 管理系统通知、公告等信息
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_notification", indexes = {
    @Index(name = "idx_type", columnList = "type"),
    @Index(name = "idx_target_audience", columnList = "target_audience"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_priority", columnList = "priority"),
    @Index(name = "idx_publish_time", columnList = "publish_time"),
    @Index(name = "idx_status_deleted", columnList = "status,deleted")
})
public class Notification extends BaseEntity {

    /**
     * 通知标题
     */
    @NotBlank(message = "通知标题不能为空")
    @Size(max = 200, message = "通知标题长度不能超过200个字符")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 通知内容
     */
    @NotBlank(message = "通知内容不能为空")
    @Size(max = 5000, message = "通知内容长度不能超过5000个字符")
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * 通知类型
     * SYSTEM: 系统通知, ACADEMIC: 学术通知, ACTIVITY: 活动通知, URGENT: 紧急通知
     */
    @NotBlank(message = "通知类型不能为空")
    @Size(max = 50, message = "通知类型长度不能超过50个字符")
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    /**
     * 目标受众
     * ALL: 全体, STUDENT: 学生, TEACHER: 教师, ADMIN: 管理员
     */
    @NotBlank(message = "目标受众不能为空")
    @Size(max = 50, message = "目标受众长度不能超过50个字符")
    @Column(name = "target_audience", nullable = false, length = 50)
    private String targetAudience;

    /**
     * 发送者ID
     */
    @Column(name = "sender_id")
    private Long senderId;

    /**
     * 发送者姓名
     */
    @Size(max = 100, message = "发送者姓名长度不能超过100个字符")
    @Column(name = "sender_name", length = 100)
    private String senderName;

    /**
     * 通知状态
     * DRAFT: 草稿, PUBLISHED: 已发布, EXPIRED: 已过期, CANCELLED: 已取消
     */
    @NotBlank(message = "通知状态不能为空")
    @Size(max = 20, message = "通知状态长度不能超过20个字符")
    @Column(name = "notification_status", nullable = false, length = 20)
    private String notificationStatus = "DRAFT";

    /**
     * 优先级
     * LOW: 低, NORMAL: 普通, HIGH: 高, URGENT: 紧急
     */
    @NotBlank(message = "优先级不能为空")
    @Size(max = 20, message = "优先级长度不能超过20个字符")
    @Column(name = "priority", nullable = false, length = 20)
    private String priority = "NORMAL";

    /**
     * 是否置顶
     */
    @Column(name = "is_pinned", columnDefinition = "TINYINT DEFAULT 0")
    private Boolean isPinned = false;

    /**
     * 发布时间
     */
    @Column(name = "publish_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    /**
     * 过期时间
     */
    @Column(name = "expire_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    /**
     * 阅读次数
     */
    @Min(value = 0, message = "阅读次数不能小于0")
    @Column(name = "read_count", columnDefinition = "INT DEFAULT 0")
    private Integer readCount = 0;

    /**
     * 附件URL
     */
    @Size(max = 500, message = "附件URL长度不能超过500个字符")
    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Column(name = "remarks", length = 500)
    private String remarks;

    // ================================
    // 关联关系
    // ================================

    /**
     * 发送者
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", insertable = false, updatable = false)
    private User sender;

    // ================================
    // 构造函数
    // ================================

    public Notification() {
        super();
    }

    public Notification(String title, String content, String type, String targetAudience) {
        this();
        this.title = title;
        this.content = content;
        this.type = type;
        this.targetAudience = targetAudience;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 获取发送者姓名
     */
    public String getSenderRealName() {
        try {
            return sender != null ? sender.getRealName() : senderName;
        } catch (Exception e) {
            return senderName;
        }
    }

    /**
     * 检查是否已发布
     */
    public boolean isPublished() {
        return "PUBLISHED".equals(notificationStatus);
    }

    /**
     * 检查是否已过期
     */
    public boolean isExpired() {
        return expireTime != null && LocalDateTime.now().isAfter(expireTime);
    }

    /**
     * 发布通知
     */
    public void publish() {
        this.notificationStatus = "PUBLISHED";
        if (this.publishTime == null) {
            this.publishTime = LocalDateTime.now();
        }
    }

    /**
     * 取消通知
     */
    public void cancel() {
        this.notificationStatus = "CANCELLED";
    }

    /**
     * 增加阅读次数
     */
    public void incrementReadCount() {
        this.readCount = (this.readCount == null ? 0 : this.readCount) + 1;
    }

    /**
     * 获取通知类型文本
     */
    public String getTypeText() {
        if (type == null) return "未知";
        return switch (type) {
            case "SYSTEM" -> "系统通知";
            case "ACADEMIC" -> "学术通知";
            case "ACTIVITY" -> "活动通知";
            case "URGENT" -> "紧急通知";
            default -> type;
        };
    }

    /**
     * 获取目标受众文本
     */
    public String getTargetAudienceText() {
        if (targetAudience == null) return "未知";
        return switch (targetAudience) {
            case "ALL" -> "全体";
            case "STUDENT" -> "学生";
            case "TEACHER" -> "教师";
            case "ADMIN" -> "管理员";
            default -> targetAudience;
        };
    }

    /**
     * 获取优先级文本
     */
    public String getPriorityText() {
        if (priority == null) return "普通";
        return switch (priority) {
            case "LOW" -> "低";
            case "NORMAL" -> "普通";
            case "HIGH" -> "高";
            case "URGENT" -> "紧急";
            default -> priority;
        };
    }

    /**
     * 获取状态文本
     */
    public String getNotificationStatusText() {
        if (notificationStatus == null) return "草稿";
        return switch (notificationStatus) {
            case "DRAFT" -> "草稿";
            case "PUBLISHED" -> "已发布";
            case "EXPIRED" -> "已过期";
            case "CANCELLED" -> "已取消";
            default -> notificationStatus;
        };
    }

    // ================================
    // Getter/Setter 方法
    // ================================

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Boolean getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(Boolean isPinned) {
        this.isPinned = isPinned;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getReadCount() {
        return readCount;
    }

    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    // ================================
    // 兼容性方法 - 为了支持 NotificationApiController
    // ================================

    /**
     * 获取通知类型（兼容性方法）
     */
    public String getNotificationType() {
        return type;
    }

    /**
     * 设置通知类型（兼容性方法）
     */
    public void setNotificationType(String notificationType) {
        this.type = notificationType;
    }

    /**
     * 获取目标类型（兼容性方法）
     */
    public String getTargetType() {
        return targetAudience;
    }

    /**
     * 设置目标类型（兼容性方法）
     */
    public void setTargetType(String targetType) {
        this.targetAudience = targetType;
    }

    /**
     * 获取目标ID列表（兼容性方法）
     */
    public String getTargetIds() {
        // 如果没有专门的字段，可以返回空字符串或根据targetAudience生成
        return "[]"; // 返回空的JSON数组
    }

    /**
     * 设置目标ID列表（兼容性方法）
     */
    public void setTargetIds(String targetIds) {
        // 可以根据需要处理目标ID列表
    }

    /**
     * 获取附件URL列表（兼容性方法）
     */
    public String getAttachmentUrls() {
        return attachmentUrl != null ? "[\"" + attachmentUrl + "\"]" : "[]";
    }

    /**
     * 设置附件URL列表（兼容性方法）
     */
    public void setAttachmentUrls(String attachmentUrls) {
        // 简单处理：如果是JSON数组格式，取第一个URL
        if (attachmentUrls != null && attachmentUrls.startsWith("[") && attachmentUrls.endsWith("]")) {
            String content = attachmentUrls.substring(1, attachmentUrls.length() - 1);
            if (!content.trim().isEmpty()) {
                // 简单解析第一个URL
                String[] urls = content.split(",");
                if (urls.length > 0) {
                    this.attachmentUrl = urls[0].trim().replace("\"", "");
                }
            }
        } else {
            this.attachmentUrl = attachmentUrls;
        }
    }

    /**
     * 获取外部链接（兼容性方法）
     */
    public String getExternalLink() {
        // 如果没有专门的字段，可以返回null或空字符串
        return null;
    }

    /**
     * 设置外部链接（兼容性方法）
     */
    public void setExternalLink(String externalLink) {
        // 可以根据需要处理外部链接
    }

    /**
     * 获取是否自动发送（兼容性方法）
     */
    public Boolean getAutoSend() {
        // 默认返回false
        return false;
    }

    /**
     * 设置是否自动发送（兼容性方法）
     */
    public void setAutoSend(Boolean autoSend) {
        // 可以根据需要处理自动发送设置
    }

    /**
     * 获取是否发送邮件（兼容性方法）
     */
    public Boolean getSendEmail() {
        // 默认返回false
        return false;
    }

    /**
     * 设置是否发送邮件（兼容性方法）
     */
    public void setSendEmail(Boolean sendEmail) {
        // 可以根据需要处理邮件发送设置
    }

    /**
     * 获取是否发送短信（兼容性方法）
     */
    public Boolean getSendSms() {
        // 默认返回false
        return false;
    }

    /**
     * 设置是否发送短信（兼容性方法）
     */
    public void setSendSms(Boolean sendSms) {
        // 可以根据需要处理短信发送设置
    }

    /**
     * 设置是否发布（兼容性方法）
     */
    public void setIsPublished(int isPublished) {
        if (isPublished == 1) {
            this.notificationStatus = "PUBLISHED";
            if (this.publishTime == null) {
                this.publishTime = LocalDateTime.now();
            }
        } else {
            this.notificationStatus = "DRAFT";
        }
    }

    /**
     * 获取是否发布（兼容性方法）
     */
    public int getIsPublished() {
        return "PUBLISHED".equals(notificationStatus) ? 1 : 0;
    }

    /**
     * 设置是否置顶（兼容性方法）
     */
    public void setIsTop(int isTop) {
        this.isPinned = (isTop == 1);
    }

    /**
     * 获取是否置顶（兼容性方法）
     */
    public int getIsTop() {
        return Boolean.TRUE.equals(isPinned) ? 1 : 0;
    }
}
