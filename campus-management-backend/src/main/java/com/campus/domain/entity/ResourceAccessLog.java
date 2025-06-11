package com.campus.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 资源访问日志实体类
 * 记录用户对课程资源的访问情况
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_resource_access_log", indexes = {
    @Index(name = "idx_resource_id", columnList = "resource_id"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_access_time", columnList = "access_time"),
    @Index(name = "idx_access_type", columnList = "access_type")
})
public class ResourceAccessLog extends BaseEntity {

    /**
     * 资源ID
     */
    @NotNull(message = "资源ID不能为空")
    @Column(name = "resource_id", nullable = false)
    private Long resourceId;

    /**
     * 资源类型
     */
    @Size(max = 50, message = "资源类型长度不能超过50个字符")
    @Column(name = "resource_type", length = 50)
    private String resourceType;

    /**
     * 资源名称
     */
    @Size(max = 200, message = "资源名称长度不能超过200个字符")
    @Column(name = "resource_name", length = 200)
    private String resourceName;

    /**
     * 用户名
     */
    @Size(max = 50, message = "用户名长度不能超过50个字符")
    @Column(name = "username", length = 50)
    private String username;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 访问类型 (view/download/preview)
     */
    @NotNull(message = "访问类型不能为空")
    @Size(max = 20, message = "访问类型长度不能超过20个字符")
    @Column(name = "access_type", nullable = false, length = 20)
    private String accessType;

    /**
     * 访问方式
     */
    @Size(max = 50, message = "访问方式长度不能超过50个字符")
    @Column(name = "access_method", length = 50)
    private String accessMethod;

    /**
     * 访问状态
     */
    @Size(max = 20, message = "访问状态长度不能超过20个字符")
    @Column(name = "access_status", length = 20)
    private String accessStatus;

    /**
     * 访问时长（毫秒）
     */
    @Column(name = "access_duration")
    private Long accessDuration;

    /**
     * 访问时间
     */
    @NotNull(message = "访问时间不能为空")
    @Column(name = "access_time", nullable = false)
    private LocalDateTime accessTime;

    /**
     * IP地址
     */
    @Size(max = 50, message = "IP地址长度不能超过50个字符")
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    /**
     * 用户代理
     */
    @Size(max = 500, message = "用户代理长度不能超过500个字符")
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * 访问设备类型 (pc/mobile/tablet)
     */
    @Size(max = 20, message = "设备类型长度不能超过20个字符")
    @Column(name = "device_type", length = 20)
    private String deviceType;

    /**
     * 访问时长（秒）
     */
    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    /**
     * 是否访问成功
     */
    @Column(name = "is_successful", nullable = false)
    private Boolean isSuccessful = true;

    /**
     * 错误信息
     */
    @Size(max = 500, message = "错误信息长度不能超过500个字符")
    @Column(name = "error_message", length = 500)
    private String errorMessage;

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
     * 访问的资源
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", insertable = false, updatable = false)
    @JsonIgnore
    private CourseResource resource;

    /**
     * 访问用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    // ================================
    // 构造函数
    // ================================

    public ResourceAccessLog() {
        super();
    }

    public ResourceAccessLog(Long resourceId, Long userId, String accessType) {
        this();
        this.resourceId = resourceId;
        this.userId = userId;
        this.accessType = accessType;
        this.accessTime = LocalDateTime.now();
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 获取访问类型文本
     */
    public String getAccessTypeText() {
        if (accessType == null) return "未知";
        return switch (accessType) {
            case "view" -> "查看";
            case "download" -> "下载";
            case "preview" -> "预览";
            default -> accessType;
        };
    }

    /**
     * 获取设备类型文本
     */
    public String getDeviceTypeText() {
        if (deviceType == null) return "未知";
        return switch (deviceType) {
            case "pc" -> "电脑";
            case "mobile" -> "手机";
            case "tablet" -> "平板";
            default -> deviceType;
        };
    }

    /**
     * 获取访问时长文本
     */
    public String getDurationText() {
        if (durationSeconds == null) return "未知";
        
        if (durationSeconds < 60) {
            return durationSeconds + "秒";
        } else if (durationSeconds < 3600) {
            return (durationSeconds / 60) + "分" + (durationSeconds % 60) + "秒";
        } else {
            int hours = durationSeconds / 3600;
            int minutes = (durationSeconds % 3600) / 60;
            int seconds = durationSeconds % 60;
            return hours + "时" + minutes + "分" + seconds + "秒";
        }
    }

    /**
     * 记录访问失败
     */
    public void recordFailure(String errorMessage) {
        this.isSuccessful = false;
        this.errorMessage = errorMessage;
    }

    /**
     * 设置访问时长
     */
    public void setDuration(LocalDateTime endTime) {
        if (accessTime != null && endTime != null) {
            this.durationSeconds = (int) java.time.Duration.between(accessTime, endTime).getSeconds();
        }
    }

    /**
     * 获取关联资源名称
     */
    public String getRelatedResourceName() {
        return resource != null ? resource.getResourceName() : null;
    }

    /**
     * 获取用户姓名
     */
    public String getUserName() {
        return user != null ? user.getRealName() : null;
    }

    /**
     * 获取用户角色
     */
    public String getUserRole() {
        return user != null ? user.getRoleKey() : null;
    }

    /**
     * 检测设备类型
     */
    public void detectDeviceType(String userAgent) {
        if (userAgent == null) {
            this.deviceType = "unknown";
            return;
        }
        
        String ua = userAgent.toLowerCase();
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            this.deviceType = "mobile";
        } else if (ua.contains("tablet") || ua.contains("ipad")) {
            this.deviceType = "tablet";
        } else {
            this.deviceType = "pc";
        }
    }

    /**
     * 创建访问日志
     */
    public static ResourceAccessLog createLog(Long resourceId, Long userId, String accessType, 
                                            String ipAddress, String userAgent) {
        ResourceAccessLog log = new ResourceAccessLog(resourceId, userId, accessType);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.detectDeviceType(userAgent);
        return log;
    }

    // ================================
    // Getter and Setter methods
    // ================================

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public String getAccessMethod() {
        return accessMethod;
    }

    public void setAccessMethod(String accessMethod) {
        this.accessMethod = accessMethod;
    }

    public String getAccessStatus() {
        return accessStatus;
    }

    public void setAccessStatus(String accessStatus) {
        this.accessStatus = accessStatus;
    }

    public Long getAccessDuration() {
        return accessDuration;
    }

    public void setAccessDuration(Long accessDuration) {
        this.accessDuration = accessDuration;
    }

    public LocalDateTime getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(LocalDateTime accessTime) {
        this.accessTime = accessTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Boolean getIsSuccessful() {
        return isSuccessful;
    }

    public void setIsSuccessful(Boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public CourseResource getResource() {
        return resource;
    }

    public void setResource(CourseResource resource) {
        this.resource = resource;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
