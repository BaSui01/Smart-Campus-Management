package com.campus.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户实体类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Entity
@Table(name = "tb_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少6个字符")
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Email(message = "邮箱格式不正确")
    @Column(unique = true, length = 100)
    private String email;

    @NotBlank(message = "真实姓名不能为空")
    @Column(name = "real_name", nullable = false, length = 50)
    private String realName;

    @Column(length = 20)
    private String phone;

    @Column(length = 10)
    private String gender; // 性别：男、女、其他

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;

    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    @Column(name = "last_login_ip", length = 50)
    private String lastLoginIp;

    @Column(name = "login_count", columnDefinition = "INT DEFAULT 0")
    private Integer loginCount = 0;

    @Column(name = "created_time", updatable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "deleted", columnDefinition = "TINYINT DEFAULT 0")
    private Integer deleted = 0;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }

    // 关联关系 - 用户角色
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<UserRole> userRoles;

    // 构造函数
    public User() {}

    public User(String username, String password, String email, String realName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.realName = realName;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    // 兼容性方法 - 为了向后兼容
    public LocalDateTime getCreatedAt() {
        return createdTime;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdTime = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedTime;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedTime = updatedAt;
    }

    // 便利方法 - 获取主要角色名称（安全版本，避免懒加载异常）
    public String getRoleName() {
        try {
            if (userRoles != null && !userRoles.isEmpty()) {
                return userRoles.get(0).getRole().getRoleName();
            }
        } catch (Exception e) {
            // 懒加载异常时返回默认值
            return "未分配";
        }
        return "未分配";
    }

    // 便利方法 - 获取主要角色键（安全版本，避免懒加载异常）
    public String getRoleKey() {
        try {
            if (userRoles != null && !userRoles.isEmpty()) {
                return userRoles.get(0).getRole().getRoleKey();
            }
        } catch (Exception e) {
            // 懒加载异常时返回默认值
            return "UNKNOWN";
        }
        return "UNKNOWN";
    }

    // 便利方法 - 检查是否有指定角色（安全版本，避免懒加载异常）
    public boolean hasRole(String roleKey) {
        try {
            if (userRoles == null) {
                return false;
            }
            return userRoles.stream()
                    .anyMatch(ur -> roleKey.equals(ur.getRole().getRoleKey()));
        } catch (Exception e) {
            // 懒加载异常时返回false
            return false;
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", realName='" + realName + '\'' +
                ", phone='" + phone + '\'' +
                ", status=" + status +
                ", createdTime=" + createdTime +
                '}';
    }
}
