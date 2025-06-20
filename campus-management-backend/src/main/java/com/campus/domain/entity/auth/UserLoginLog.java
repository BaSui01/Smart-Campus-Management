package com.campus.domain.entity.auth;

import com.campus.domain.entity.infrastructure.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 用户登录日志实体类
 * 记录用户的登录历史，与User主表分离以优化性能
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Entity
@Table(name = "tb_user_login_log", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_login_time", columnList = "login_time"),
    @Index(name = "idx_login_ip", columnList = "login_ip"),
    @Index(name = "idx_login_status", columnList = "login_status")
})
public class UserLoginLog extends BaseEntity {

    /**
     * 关联的用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 登录时间
     */
    @Column(name = "login_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;

    /**
     * 登录IP
     */
    @Size(max = 50, message = "登录IP长度不能超过50个字符")
    @Column(name = "login_ip", length = 50)
    private String loginIp;

    /**
     * 登录状态
     * 1: 成功, 0: 失败
     */
    @Column(name = "login_status", nullable = false)
    private Integer loginStatus;

    /**
     * 登录方式
     * 1: 用户名密码, 2: 手机验证码, 3: 邮箱验证码, 4: 第三方登录
     */
    @Column(name = "login_type", columnDefinition = "TINYINT DEFAULT 1")
    private Integer loginType = 1;

    /**
     * 用户代理（浏览器信息）
     */
    @Size(max = 500, message = "用户代理长度不能超过500个字符")
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * 操作系统
     */
    @Size(max = 50, message = "操作系统长度不能超过50个字符")
    @Column(name = "operating_system", length = 50)
    private String operatingSystem;

    /**
     * 浏览器
     */
    @Size(max = 50, message = "浏览器长度不能超过50个字符")
    @Column(name = "browser", length = 50)
    private String browser;

    /**
     * 设备类型
     * PC, Mobile, Tablet
     */
    @Size(max = 20, message = "设备类型长度不能超过20个字符")
    @Column(name = "device_type", length = 20)
    private String deviceType;

    /**
     * 登录地点（根据IP解析）
     */
    @Size(max = 100, message = "登录地点长度不能超过100个字符")
    @Column(name = "login_location", length = 100)
    private String loginLocation;

    /**
     * 失败原因（登录失败时记录）
     */
    @Size(max = 200, message = "失败原因长度不能超过200个字符")
    @Column(name = "failure_reason", length = 200)
    private String failureReason;

    /**
     * 登出时间
     */
    @Column(name = "logout_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime logoutTime;

    /**
     * 会话时长（分钟）
     */
    @Column(name = "session_duration")
    private Integer sessionDuration;

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
     * 关联的用户信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    // ================================
    // 构造函数
    // ================================

    public UserLoginLog() {
        super();
    }

    public UserLoginLog(Long userId, String loginIp, Integer loginStatus) {
        this();
        this.userId = userId;
        this.loginIp = loginIp;
        this.loginStatus = loginStatus;
        this.loginTime = LocalDateTime.now();
    }

    public UserLoginLog(Long userId, String loginIp, Integer loginStatus, String userAgent) {
        this(userId, loginIp, loginStatus);
        this.userAgent = userAgent;
        parseUserAgent(userAgent);
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 解析用户代理信息
     */
    private void parseUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return;
        }

        // 简单的用户代理解析逻辑
        String lowerUserAgent = userAgent.toLowerCase();

        // 解析操作系统
        if (lowerUserAgent.contains("windows")) {
            this.operatingSystem = "Windows";
        } else if (lowerUserAgent.contains("mac")) {
            this.operatingSystem = "macOS";
        } else if (lowerUserAgent.contains("linux")) {
            this.operatingSystem = "Linux";
        } else if (lowerUserAgent.contains("android")) {
            this.operatingSystem = "Android";
        } else if (lowerUserAgent.contains("ios") || lowerUserAgent.contains("iphone") || lowerUserAgent.contains("ipad")) {
            this.operatingSystem = "iOS";
        }

        // 解析浏览器
        if (lowerUserAgent.contains("chrome")) {
            this.browser = "Chrome";
        } else if (lowerUserAgent.contains("firefox")) {
            this.browser = "Firefox";
        } else if (lowerUserAgent.contains("safari")) {
            this.browser = "Safari";
        } else if (lowerUserAgent.contains("edge")) {
            this.browser = "Edge";
        } else if (lowerUserAgent.contains("opera")) {
            this.browser = "Opera";
        }

        // 解析设备类型
        if (lowerUserAgent.contains("mobile")) {
            this.deviceType = "Mobile";
        } else if (lowerUserAgent.contains("tablet") || lowerUserAgent.contains("ipad")) {
            this.deviceType = "Tablet";
        } else {
            this.deviceType = "PC";
        }
    }

    /**
     * 记录登出
     */
    public void recordLogout() {
        this.logoutTime = LocalDateTime.now();
        if (this.loginTime != null) {
            long minutes = java.time.Duration.between(this.loginTime, this.logoutTime).toMinutes();
            this.sessionDuration = (int) minutes;
        }
    }

    /**
     * 检查是否登录成功
     */
    public boolean isLoginSuccess() {
        return loginStatus != null && loginStatus == 1;
    }

    /**
     * 获取登录类型文本
     */
    public String getLoginTypeText() {
        if (loginType == null) return "未知";
        return switch (loginType) {
            case 1 -> "用户名密码";
            case 2 -> "手机验证码";
            case 3 -> "邮箱验证码";
            case 4 -> "第三方登录";
            default -> "未知";
        };
    }

    // ================================
    // Getter/Setter 方法
    // ================================

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Integer getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(Integer loginStatus) {
        this.loginStatus = loginStatus;
    }

    public Integer getLoginType() {
        return loginType;
    }

    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        parseUserAgent(userAgent);
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getLoginLocation() {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation) {
        this.loginLocation = loginLocation;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(LocalDateTime logoutTime) {
        this.logoutTime = logoutTime;
    }

    public Integer getSessionDuration() {
        return sessionDuration;
    }

    public void setSessionDuration(Integer sessionDuration) {
        this.sessionDuration = sessionDuration;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
