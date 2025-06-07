package com.campus.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息实体类
 * 管理系统内的消息通知和用户间的沟通
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "messages", indexes = {
    @Index(name = "idx_sender_id", columnList = "sender_id"),
    @Index(name = "idx_receiver_id", columnList = "receiver_id"),
    @Index(name = "idx_message_type", columnList = "message_type"),
    @Index(name = "idx_send_time", columnList = "send_time"),
    @Index(name = "idx_is_read", columnList = "is_read")
})
public class Message extends BaseEntity {

    /**
     * 发送者ID
     */
    @Column(name = "sender_id")
    private Long senderId;

    /**
     * 接收者ID
     */
    @Column(name = "receiver_id")
    private Long receiverId;

    /**
     * 消息类型 (system/personal/announcement/notification)
     */
    @NotBlank(message = "消息类型不能为空")
    @Size(max = 20, message = "消息类型长度不能超过20个字符")
    @Column(name = "message_type", nullable = false, length = 20)
    private String messageType;

    /**
     * 消息标题
     */
    @NotBlank(message = "消息标题不能为空")
    @Size(max = 200, message = "消息标题长度不能超过200个字符")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 2000, message = "消息内容长度不能超过2000个字符")
    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    /**
     * 发送时间
     */
    @NotNull(message = "发送时间不能为空")
    @Column(name = "send_time", nullable = false)
    private LocalDateTime sendTime;

    /**
     * 是否已读
     */
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    /**
     * 阅读时间
     */
    @Column(name = "read_time")
    private LocalDateTime readTime;

    /**
     * 优先级 (low/normal/high/urgent)
     */
    @Size(max = 10, message = "优先级长度不能超过10个字符")
    @Column(name = "priority", length = 10)
    private String priority = "normal";

    /**
     * 消息分类 (academic/administrative/social/emergency)
     */
    @Size(max = 20, message = "消息分类长度不能超过20个字符")
    @Column(name = "category", length = 20)
    private String category;

    /**
     * 关联业务ID（如课程ID、作业ID等）
     */
    @Column(name = "related_id")
    private Long relatedId;

    /**
     * 关联业务类型 (course/assignment/exam/payment)
     */
    @Size(max = 20, message = "关联业务类型长度不能超过20个字符")
    @Column(name = "related_type", length = 20)
    private String relatedType;

    /**
     * 附件路径
     */
    @Size(max = 500, message = "附件路径长度不能超过500个字符")
    @Column(name = "attachment_path", length = 500)
    private String attachmentPath;

    /**
     * 是否需要回复
     */
    @Column(name = "require_reply", nullable = false)
    private Boolean requireReply = false;

    /**
     * 回复截止时间
     */
    @Column(name = "reply_deadline")
    private LocalDateTime replyDeadline;

    /**
     * 父消息ID（用于回复消息）
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 是否为群发消息
     */
    @Column(name = "is_broadcast", nullable = false)
    private Boolean isBroadcast = false;

    /**
     * 目标用户组 (all/students/teachers/parents/admins)
     */
    @Size(max = 50, message = "目标用户组长度不能超过50个字符")
    @Column(name = "target_group", length = 50)
    private String targetGroup;

    /**
     * 发送渠道 (system/email/sms/wechat)
     */
    @Size(max = 50, message = "发送渠道长度不能超过50个字符")
    @Column(name = "send_channels", length = 50)
    private String sendChannels = "system";

    /**
     * 是否已发送邮件
     */
    @Column(name = "email_sent", nullable = false)
    private Boolean emailSent = false;

    /**
     * 是否已发送短信
     */
    @Column(name = "sms_sent", nullable = false)
    private Boolean smsSent = false;

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
    @JsonIgnore
    private User sender;

    /**
     * 接收者
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", insertable = false, updatable = false)
    @JsonIgnore
    private User receiver;

    /**
     * 父消息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    @JsonIgnore
    private Message parentMessage;

    /**
     * 回复消息列表
     */
    @OneToMany(mappedBy = "parentMessage", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Message> replies;

    // ================================
    // 构造函数
    // ================================

    public Message() {
        super();
    }

    public Message(Long senderId, Long receiverId, String messageType, String title, String content) {
        this();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageType = messageType;
        this.title = title;
        this.content = content;
        this.sendTime = LocalDateTime.now();
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 获取消息类型文本
     */
    public String getMessageTypeText() {
        if (messageType == null) return "未知";
        return switch (messageType) {
            case "system" -> "系统消息";
            case "personal" -> "个人消息";
            case "announcement" -> "公告";
            case "notification" -> "通知";
            default -> messageType;
        };
    }

    /**
     * 获取优先级文本
     */
    public String getPriorityText() {
        if (priority == null) return "普通";
        return switch (priority) {
            case "low" -> "低";
            case "normal" -> "普通";
            case "high" -> "高";
            case "urgent" -> "紧急";
            default -> priority;
        };
    }

    /**
     * 获取消息分类文本
     */
    public String getCategoryText() {
        if (category == null) return "";
        return switch (category) {
            case "academic" -> "学术";
            case "administrative" -> "行政";
            case "social" -> "社交";
            case "emergency" -> "紧急";
            default -> category;
        };
    }

    /**
     * 标记为已读
     */
    public void markAsRead() {
        this.isRead = true;
        this.readTime = LocalDateTime.now();
    }

    /**
     * 标记为未读
     */
    public void markAsUnread() {
        this.isRead = false;
        this.readTime = null;
    }

    /**
     * 检查是否过期（针对需要回复的消息）
     */
    public boolean isExpired() {
        return requireReply && replyDeadline != null && LocalDateTime.now().isAfter(replyDeadline);
    }

    /**
     * 检查是否为回复消息
     */
    public boolean isReply() {
        return parentId != null;
    }

    /**
     * 检查是否为紧急消息
     */
    public boolean isUrgent() {
        return "urgent".equals(priority) || "high".equals(priority);
    }

    /**
     * 创建系统消息
     */
    public static Message createSystemMessage(Long receiverId, String title, String content) {
        Message message = new Message();
        message.setSenderId(null); // 系统消息没有发送者
        message.setReceiverId(receiverId);
        message.setMessageType("system");
        message.setTitle(title);
        message.setContent(content);
        message.setSendTime(LocalDateTime.now());
        return message;
    }

    /**
     * 创建公告消息
     */
    public static Message createAnnouncement(Long senderId, String title, String content, String targetGroup) {
        Message message = new Message();
        message.setSenderId(senderId);
        message.setMessageType("announcement");
        message.setTitle(title);
        message.setContent(content);
        message.setIsBroadcast(true);
        message.setTargetGroup(targetGroup);
        message.setSendTime(LocalDateTime.now());
        return message;
    }

    /**
     * 创建回复消息
     */
    public Message createReply(Long senderId, String content) {
        Message reply = new Message();
        reply.setSenderId(senderId);
        reply.setReceiverId(this.senderId); // 回复给原发送者
        reply.setMessageType("personal");
        reply.setTitle("回复: " + this.title);
        reply.setContent(content);
        reply.setParentId(this.getId());
        reply.setSendTime(LocalDateTime.now());
        return reply;
    }

    /**
     * 获取发送者姓名
     */
    public String getSenderName() {
        if (sender == null) return "系统";
        return sender.getRealName();
    }

    /**
     * 获取接收者姓名
     */
    public String getReceiverName() {
        return receiver != null ? receiver.getRealName() : null;
    }

    /**
     * 获取时间描述
     */
    public String getTimeDescription() {
        if (sendTime == null) return "";
        
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(sendTime, now).toMinutes();
        
        if (minutes < 1) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (minutes < 1440) { // 24小时
            return (minutes / 60) + "小时前";
        } else {
            return (minutes / 1440) + "天前";
        }
    }

    /**
     * 验证消息数据
     */
    @PrePersist
    @PreUpdate
    public void validateMessage() {
        if (requireReply && replyDeadline != null && replyDeadline.isBefore(sendTime)) {
            throw new IllegalArgumentException("回复截止时间不能早于发送时间");
        }
        
        if (isBroadcast && receiverId != null) {
            throw new IllegalArgumentException("群发消息不能指定具体接收者");
        }
        
        if (!isBroadcast && receiverId == null) {
            throw new IllegalArgumentException("非群发消息必须指定接收者");
        }
    }

    // ================================
    // Getter and Setter methods
    // ================================

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

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

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getReadTime() {
        return readTime;
    }

    public void setReadTime(LocalDateTime readTime) {
        this.readTime = readTime;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(Long relatedId) {
        this.relatedId = relatedId;
    }

    public String getRelatedType() {
        return relatedType;
    }

    public void setRelatedType(String relatedType) {
        this.relatedType = relatedType;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public Boolean getRequireReply() {
        return requireReply;
    }

    public void setRequireReply(Boolean requireReply) {
        this.requireReply = requireReply;
    }

    public LocalDateTime getReplyDeadline() {
        return replyDeadline;
    }

    public void setReplyDeadline(LocalDateTime replyDeadline) {
        this.replyDeadline = replyDeadline;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Boolean getIsBroadcast() {
        return isBroadcast;
    }

    public void setIsBroadcast(Boolean isBroadcast) {
        this.isBroadcast = isBroadcast;
    }

    public String getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }

    public String getSendChannels() {
        return sendChannels;
    }

    public void setSendChannels(String sendChannels) {
        this.sendChannels = sendChannels;
    }

    public Boolean getEmailSent() {
        return emailSent;
    }

    public void setEmailSent(Boolean emailSent) {
        this.emailSent = emailSent;
    }

    public Boolean getSmsSent() {
        return smsSent;
    }

    public void setSmsSent(Boolean smsSent) {
        this.smsSent = smsSent;
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

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Message getParentMessage() {
        return parentMessage;
    }

    public void setParentMessage(Message parentMessage) {
        this.parentMessage = parentMessage;
    }

    public List<Message> getReplies() {
        return replies;
    }

    public void setReplies(List<Message> replies) {
        this.replies = replies;
    }
}
