package com.campus.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 通知模板实体类
 * 管理系统的各种通知模板，支持邮件、短信、站内消息等
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "notification_templates", indexes = {
    @Index(name = "idx_template_code", columnList = "template_code", unique = true),
    @Index(name = "idx_template_type", columnList = "template_type"),
    @Index(name = "idx_channel", columnList = "channel")
})
public class NotificationTemplate extends BaseEntity {

    /**
     * 模板编码
     */
    @NotBlank(message = "模板编码不能为空")
    @Size(max = 50, message = "模板编码长度不能超过50个字符")
    @Column(name = "template_code", nullable = false, unique = true, length = 50)
    private String templateCode;

    /**
     * 模板名称
     */
    @NotBlank(message = "模板名称不能为空")
    @Size(max = 200, message = "模板名称长度不能超过200个字符")
    @Column(name = "template_name", nullable = false, length = 200)
    private String templateName;

    /**
     * 模板类型 (system/course/exam/payment/attendance/evaluation)
     */
    @NotBlank(message = "模板类型不能为空")
    @Size(max = 20, message = "模板类型长度不能超过20个字符")
    @Column(name = "template_type", nullable = false, length = 20)
    private String templateType;

    /**
     * 通知渠道 (email/sms/system/wechat/all)
     */
    @NotBlank(message = "通知渠道不能为空")
    @Size(max = 20, message = "通知渠道长度不能超过20个字符")
    @Column(name = "channel", nullable = false, length = 20)
    private String channel;

    /**
     * 模板标题
     */
    @Size(max = 200, message = "模板标题长度不能超过200个字符")
    @Column(name = "title", length = 200)
    private String title;

    /**
     * 模板内容
     */
    @NotBlank(message = "模板内容不能为空")
    @Size(max = 5000, message = "模板内容长度不能超过5000个字符")
    @Column(name = "content", nullable = false, length = 5000)
    private String content;

    /**
     * 模板描述
     */
    @Size(max = 500, message = "模板描述长度不能超过500个字符")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 模板变量说明（JSON格式）
     */
    @Size(max = 2000, message = "模板变量说明长度不能超过2000个字符")
    @Column(name = "variables", length = 2000)
    private String variables;

    /**
     * 是否为系统模板
     */
    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false;

    /**
     * 是否启用
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * 优先级 (1-10，数字越大优先级越高)
     */
    @Column(name = "priority")
    private Integer priority = 5;

    /**
     * 发送条件（JSON格式）
     */
    @Size(max = 1000, message = "发送条件长度不能超过1000个字符")
    @Column(name = "send_conditions", length = 1000)
    private String sendConditions;

    /**
     * 发送时机 (immediate/scheduled/triggered)
     */
    @Size(max = 20, message = "发送时机长度不能超过20个字符")
    @Column(name = "send_timing", length = 20)
    private String sendTiming = "immediate";

    /**
     * 延迟发送时间（分钟）
     */
    @Column(name = "delay_minutes")
    private Integer delayMinutes = 0;

    /**
     * 是否允许重复发送
     */
    @Column(name = "allow_duplicate", nullable = false)
    private Boolean allowDuplicate = true;

    /**
     * 重复发送间隔（小时）
     */
    @Column(name = "duplicate_interval_hours")
    private Integer duplicateIntervalHours = 24;

    /**
     * 最大发送次数
     */
    @Column(name = "max_send_count")
    private Integer maxSendCount;

    /**
     * 使用次数
     */
    @Column(name = "usage_count")
    private Integer usageCount = 0;

    /**
     * 最后使用时间
     */
    @Column(name = "last_used_time")
    private LocalDateTime lastUsedTime;

    /**
     * 创建人ID
     */
    @Column(name = "creator_id")
    private Long creatorId;

    /**
     * 最后修改人ID
     */
    @Column(name = "last_modified_by")
    private Long lastModifiedBy;

    /**
     * 最后修改时间
     */
    @Column(name = "last_modified_time")
    private LocalDateTime lastModifiedTime;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Column(name = "remarks", length = 500)
    private String remarks;

    // ================================
    // 构造函数
    // ================================

    public NotificationTemplate() {
        super();
    }

    public NotificationTemplate(String templateCode, String templateName, String templateType, 
                               String channel, String content) {
        this();
        this.templateCode = templateCode;
        this.templateName = templateName;
        this.templateType = templateType;
        this.channel = channel;
        this.content = content;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 获取模板类型文本
     */
    public String getTemplateTypeText() {
        if (templateType == null) return "未知";
        return switch (templateType) {
            case "system" -> "系统通知";
            case "course" -> "课程通知";
            case "exam" -> "考试通知";
            case "payment" -> "缴费通知";
            case "attendance" -> "考勤通知";
            case "evaluation" -> "评价通知";
            default -> templateType;
        };
    }

    /**
     * 获取通知渠道文本
     */
    public String getChannelText() {
        if (channel == null) return "未知";
        return switch (channel) {
            case "email" -> "邮件";
            case "sms" -> "短信";
            case "system" -> "站内消息";
            case "wechat" -> "微信";
            case "all" -> "全渠道";
            default -> channel;
        };
    }

    /**
     * 获取发送时机文本
     */
    public String getSendTimingText() {
        if (sendTiming == null) return "立即发送";
        return switch (sendTiming) {
            case "immediate" -> "立即发送";
            case "scheduled" -> "定时发送";
            case "triggered" -> "触发发送";
            default -> sendTiming;
        };
    }

    /**
     * 替换模板变量
     */
    public String renderContent(java.util.Map<String, Object> variables) {
        if (content == null || variables == null || variables.isEmpty()) {
            return content;
        }
        
        String renderedContent = content;
        for (java.util.Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            renderedContent = renderedContent.replace(placeholder, value);
        }
        
        return renderedContent;
    }

    /**
     * 替换模板标题
     */
    public String renderTitle(java.util.Map<String, Object> variables) {
        if (title == null || variables == null || variables.isEmpty()) {
            return title;
        }
        
        String renderedTitle = title;
        for (java.util.Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            renderedTitle = renderedTitle.replace(placeholder, value);
        }
        
        return renderedTitle;
    }

    /**
     * 检查是否可以发送
     */
    public boolean canSend() {
        if (!isActive || !isEnabled()) {
            return false;
        }
        
        if (maxSendCount != null && usageCount != null && usageCount >= maxSendCount) {
            return false;
        }
        
        return true;
    }

    /**
     * 检查是否允许重复发送
     */
    public boolean canSendDuplicate() {
        if (!allowDuplicate) {
            return false;
        }
        
        if (lastUsedTime == null) {
            return true;
        }
        
        if (duplicateIntervalHours == null || duplicateIntervalHours <= 0) {
            return true;
        }
        
        LocalDateTime nextAllowedTime = lastUsedTime.plusHours(duplicateIntervalHours);
        return LocalDateTime.now().isAfter(nextAllowedTime);
    }

    /**
     * 记录使用
     */
    public void recordUsage() {
        if (usageCount == null) {
            usageCount = 0;
        }
        usageCount++;
        lastUsedTime = LocalDateTime.now();
    }

    /**
     * 更新模板
     */
    public void updateTemplate(String title, String content, Long modifierId) {
        this.title = title;
        this.content = content;
        this.lastModifiedBy = modifierId;
        this.lastModifiedTime = LocalDateTime.now();
    }

    /**
     * 激活模板
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * 停用模板
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 获取变量列表
     */
    public String[] getVariableList() {
        if (variables == null || variables.trim().isEmpty()) {
            return new String[0];
        }
        
        // 简单解析变量（假设是逗号分隔的格式）
        return variables.split(",");
    }

    /**
     * 验证模板内容中的变量
     */
    public boolean validateVariables() {
        if (content == null) {
            return true;
        }
        
        // 检查是否有未闭合的变量占位符
        int openCount = 0;
        for (int i = 0; i < content.length() - 1; i++) {
            if (content.charAt(i) == '$' && content.charAt(i + 1) == '{') {
                openCount++;
            } else if (content.charAt(i) == '}') {
                openCount--;
            }
        }
        
        return openCount == 0;
    }

    /**
     * 创建系统模板
     */
    public static NotificationTemplate createSystemTemplate(String code, String name, String type, 
                                                           String channel, String content) {
        NotificationTemplate template = new NotificationTemplate(code, name, type, channel, content);
        template.setIsSystem(true);
        return template;
    }

    /**
     * 验证模板数据
     */
    @PrePersist
    @PreUpdate
    public void validateTemplate() {
        if (priority != null && (priority < 1 || priority > 10)) {
            throw new IllegalArgumentException("优先级必须在1-10之间");
        }
        
        if (delayMinutes != null && delayMinutes < 0) {
            throw new IllegalArgumentException("延迟时间不能为负数");
        }
        
        if (duplicateIntervalHours != null && duplicateIntervalHours < 0) {
            throw new IllegalArgumentException("重复发送间隔不能为负数");
        }
        
        if (maxSendCount != null && maxSendCount <= 0) {
            throw new IllegalArgumentException("最大发送次数必须大于0");
        }
        
        if (!validateVariables()) {
            throw new IllegalArgumentException("模板内容中的变量格式不正确");
        }
    }

    // ================================
    // Getter and Setter methods
    // ================================

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVariables() {
        return variables;
    }

    public void setVariables(String variables) {
        this.variables = variables;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getSendConditions() {
        return sendConditions;
    }

    public void setSendConditions(String sendConditions) {
        this.sendConditions = sendConditions;
    }

    public String getSendTiming() {
        return sendTiming;
    }

    public void setSendTiming(String sendTiming) {
        this.sendTiming = sendTiming;
    }

    public Integer getDelayMinutes() {
        return delayMinutes;
    }

    public void setDelayMinutes(Integer delayMinutes) {
        this.delayMinutes = delayMinutes;
    }

    public Boolean getAllowDuplicate() {
        return allowDuplicate;
    }

    public void setAllowDuplicate(Boolean allowDuplicate) {
        this.allowDuplicate = allowDuplicate;
    }

    public Integer getDuplicateIntervalHours() {
        return duplicateIntervalHours;
    }

    public void setDuplicateIntervalHours(Integer duplicateIntervalHours) {
        this.duplicateIntervalHours = duplicateIntervalHours;
    }

    public Integer getMaxSendCount() {
        return maxSendCount;
    }

    public void setMaxSendCount(Integer maxSendCount) {
        this.maxSendCount = maxSendCount;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public LocalDateTime getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(LocalDateTime lastUsedTime) {
        this.lastUsedTime = lastUsedTime;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(Long lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(LocalDateTime lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    // ================================
    // 兼容性方法 - 为控制器提供别名
    // ================================

    /**
     * 设置创建时间（别名方法）
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.setCreatedAt(createTime);
    }

    /**
     * 获取创建时间（别名方法）
     */
    public LocalDateTime getCreateTime() {
        return this.getCreatedAt();
    }

    /**
     * 设置更新时间（别名方法）
     */
    public void setUpdateTime(LocalDateTime updateTime) {
        this.setUpdatedAt(updateTime);
    }

    /**
     * 获取更新时间（别名方法）
     */
    public LocalDateTime getUpdateTime() {
        return this.getUpdatedAt();
    }

    /**
     * 设置模板状态（别名方法）
     */
    public void setTemplateStatus(String templateStatus) {
        if ("active".equals(templateStatus) || "1".equals(templateStatus)) {
            this.setIsActive(true);
            this.setStatus(1);
        } else {
            this.setIsActive(false);
            this.setStatus(0);
        }
    }

    /**
     * 获取模板状态（别名方法）
     */
    public String getTemplateStatus() {
        if (Boolean.TRUE.equals(this.getIsActive()) && this.getStatus() == 1) {
            return "active";
        } else {
            return "inactive";
        }
    }

    /**
     * 获取模板内容（别名方法）
     */
    public String getTemplateContent() {
        return this.getContent();
    }

    /**
     * 获取通知渠道（别名方法）
     */
    public String getNotificationChannel() {
        return this.getChannel();
    }
}
