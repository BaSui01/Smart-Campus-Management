package com.campus.application.dto;

import com.campus.domain.entity.auth.User;
import com.campus.domain.entity.auth.UserRole;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 用户数据传输对象
 * 用于避免懒加载问题和减少数据传输量
 *
 * @author campus
 * @since 2025-06-05
 */
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String realName;
    private String phone;
    private String avatarUrl;
    private Integer status;
    private String statusText;
    private String roleName;
    private String roleKey;
    private Integer loginCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // 构造函数
    public UserDTO() {}

    public UserDTO(User user) {
        if (user != null) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.realName = user.getRealName();
            this.phone = user.getPhone();
            this.avatarUrl = user.getAvatarUrl();
            this.status = user.getStatus();
            this.statusText = user.getStatus() == 1 ? "正常" : "禁用";
            this.loginCount = user.getLoginCount();
            this.lastLoginTime = user.getLastLoginTime();
            this.createdAt = user.getCreatedAt();
            this.updatedAt = user.getUpdatedAt();
            
            // 安全获取角色信息，避免懒加载异常
            try {
                if (user.getUserRoles() != null && !user.getUserRoles().isEmpty()) {
                    UserRole userRole = user.getUserRoles().iterator().next();
                    if (userRole.getRole() != null) {
                        this.roleName = userRole.getRole().getRoleName();
                        this.roleKey = userRole.getRole().getRoleKey();
                    } else {
                        this.roleName = "未分配";
                        this.roleKey = "UNKNOWN";
                    }
                } else {
                    this.roleName = "未分配";
                    this.roleKey = "UNKNOWN";
                }
            } catch (Exception e) {
                this.roleName = "未分配";
                this.roleKey = "UNKNOWN";
            }
        }
    }

    // 静态工厂方法
    public static UserDTO from(User user) {
        return new UserDTO(user);
    }

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
        this.statusText = status == 1 ? "正常" : "禁用";
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
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

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", realName='" + realName + '\'' +
                ", phone='" + phone + '\'' +
                ", status=" + status +
                ", roleName='" + roleName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
