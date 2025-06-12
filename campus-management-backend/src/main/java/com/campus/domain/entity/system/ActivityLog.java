package com.campus.domain.entity.system;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.campus.domain.entity.auth.User;

/**
 * 活动日志实体类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_activity_log", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_activity_type", columnList = "activity_type"),
    @Index(name = "idx_module", columnList = "module"),
    @Index(name = "idx_create_time", columnList = "create_time"),
    @Index(name = "idx_ip_address", columnList = "ip_address"),
    @Index(name = "idx_result", columnList = "result")
})
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 用户名
     */
    @Column(name = "username", length = 50)
    private String username;

    /**
     * 真实姓名
     */
    @Column(name = "real_name", length = 50)
    private String realName;

    /**
     * 活动类型
     */
    @Column(name = "activity_type", length = 50, nullable = false)
    private String activityType;

    /**
     * 模块名称
     */
    @Column(name = "module", length = 50)
    private String module;

    /**
     * 操作动作
     */
    @Column(name = "action", length = 100)
    private String action;

    /**
     * 操作描述
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 操作级别
     */
    @Column(name = "level", length = 20)
    private String level;

    /**
     * 操作结果
     */
    @Column(name = "result", length = 20)
    private String result;

    /**
     * IP地址
     */
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    /**
     * 用户代理
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * 请求URL
     */
    @Column(name = "request_url", length = 500)
    private String requestUrl;

    /**
     * 请求方法
     */
    @Column(name = "request_method", length = 10)
    private String requestMethod;

    /**
     * 请求参数
     */
    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams;

    /**
     * 响应结果
     */
    @Column(name = "response_result", columnDefinition = "TEXT")
    private String responseResult;

    /**
     * 执行时间（毫秒）
     */
    @Column(name = "execution_time")
    private Long executionTime;

    /**
     * 异常信息
     */
    @Column(name = "exception_info", columnDefinition = "TEXT")
    private String exceptionInfo;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    /**
     * 创建者
     */
    @Column(name = "created_by", length = 50)
    private String createdBy;

    /**
     * 创建时间（BaseEntity兼容）
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间（BaseEntity兼容）
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 删除标记（BaseEntity兼容）
     */
    @Column(name = "deleted", nullable = false)
    private Integer deleted = 0;

    /**
     * 状态（BaseEntity兼容）
     */
    @Column(name = "status")
    private Integer status = 1;

    /**
     * 用户关联（可选）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    // 构造函数
    public ActivityLog() {
        this.createTime = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deleted = 0;
        this.status = 1;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public String getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(String responseResult) {
        this.responseResult = responseResult;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public String getExceptionInfo() {
        return exceptionInfo;
    }

    public void setExceptionInfo(String exceptionInfo) {
        this.exceptionInfo = exceptionInfo;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // ================================
    // 静态工厂方法
    // ================================

    /**
     * 创建登录日志
     */
    public static ActivityLog createLoginLog(Long userId, String username, String realName, String ipAddress) {
        ActivityLog log = new ActivityLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setRealName(realName);
        log.setActivityType("LOGIN");
        log.setModule("AUTH");
        log.setAction("LOGIN");
        log.setDescription("用户登录");
        log.setLevel("INFO");
        log.setResult("SUCCESS");
        log.setIpAddress(ipAddress);
        return log;
    }

    /**
     * 创建登出日志
     */
    public static ActivityLog createLogoutLog(Long userId, String username, String realName, String ipAddress) {
        ActivityLog log = new ActivityLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setRealName(realName);
        log.setActivityType("LOGOUT");
        log.setModule("AUTH");
        log.setAction("LOGOUT");
        log.setDescription("用户登出");
        log.setLevel("INFO");
        log.setResult("SUCCESS");
        log.setIpAddress(ipAddress);
        return log;
    }

    /**
     * 创建操作日志
     */
    public static ActivityLog createOperationLog(Long userId, String username, String realName,
                                               String module, String action, String description,
                                               String targetType, Long targetId, String targetName) {
        ActivityLog log = new ActivityLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setRealName(realName);
        log.setActivityType("OPERATION");
        log.setModule(module);
        log.setAction(action);
        log.setDescription(description);
        log.setLevel("INFO");
        log.setResult("SUCCESS");
        return log;
    }

    // ================================
    // 兼容性方法
    // ================================

    /**
     * 设置请求路径（兼容性方法）
     */
    public void setRequestPath(String requestPath) {
        this.setRequestUrl(requestPath);
    }

    /**
     * 获取请求路径（兼容性方法）
     */
    public String getRequestPath() {
        return this.getRequestUrl();
    }

    /**
     * 设置错误信息（兼容性方法）
     */
    public void setErrorMessage(String errorMessage) {
        this.setExceptionInfo(errorMessage);
    }

    /**
     * 获取错误信息（兼容性方法）
     */
    public String getErrorMessage() {
        return this.getExceptionInfo();
    }

    @Override
    public String toString() {
        return "ActivityLog{" +
                "id=" + id +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", activityType='" + activityType + '\'' +
                ", module='" + module + '\'' +
                ", action='" + action + '\'' +
                ", description='" + description + '\'' +
                ", level='" + level + '\'' +
                ", result='" + result + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
