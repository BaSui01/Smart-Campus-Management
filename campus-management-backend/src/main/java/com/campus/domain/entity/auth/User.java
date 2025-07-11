package com.campus.domain.entity.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.campus.domain.entity.infrastructure.BaseEntity;
import com.campus.shared.security.EncryptionConfig.EncryptedField;
import com.campus.shared.security.EncryptionConfig.EncryptionEntityListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户实体类
 * 支持管理员、教师、学生等多种角色
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_user", indexes = {
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_phone", columnList = "phone"),
    @Index(name = "idx_status_deleted", columnList = "status,deleted")
})
@EntityListeners(EncryptionEntityListener.class)
public class User extends BaseEntity {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 128, message = "密码长度必须在6-128个字符之间")
    @JsonIgnore
    @Column(name = "password", nullable = false, length = 128)
    private String password;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @EncryptedField
    @Column(name = "email", unique = true, length = 255)  // 增加长度以容纳加密数据
    private String email;

    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    @Column(name = "real_name", nullable = false, length = 50)
    private String realName;

    /**
     * 手机号码
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    @EncryptedField
    @Column(name = "phone", length = 255)  // 增加长度以容纳加密数据
    private String phone;

    /**
     * 性别
     */
    @Pattern(regexp = "^(男|女|其他)$", message = "性别只能是：男、女、其他")
    @Column(name = "gender", length = 10)
    private String gender;

    /**
     * 生日
     */
    @Column(name = "birthday")
    private LocalDate birthday;

    /**
     * 身份证号
     */
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$",
             message = "身份证号格式不正确")
    @EncryptedField
    @Column(name = "id_card", length = 255)  // 增加长度以容纳加密数据
    private String idCard;

    /**
     * 头像URL
     */
    @Size(max = 500, message = "头像URL长度不能超过500个字符")
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    /**
     * 地址
     */
    @Size(max = 200, message = "地址长度不能超过200个字符")
    @Column(name = "address", length = 200)
    private String address;

    /**
     * 最后登录时间
     */
    @Column(name = "last_login_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    @Column(name = "last_login_ip", length = 50)
    private String lastLoginIp;

    /**
     * 登录次数
     */
    @Column(name = "login_count", columnDefinition = "INT DEFAULT 0")
    private Integer loginCount = 0;

    /**
     * 密码重置令牌
     */
    @JsonIgnore
    @Column(name = "reset_token", length = 100)
    private String resetToken;

    /**
     * 密码重置令牌过期时间
     */
    @JsonIgnore
    @Column(name = "reset_token_expire")
    private LocalDateTime resetTokenExpire;

    /**
     * 账户是否未过期
     */
    @Column(name = "account_non_expired", columnDefinition = "TINYINT DEFAULT 1")
    private Boolean accountNonExpired = true;

    /**
     * 账户是否未锁定
     */
    @Column(name = "account_non_locked", columnDefinition = "TINYINT DEFAULT 1")
    private Boolean accountNonLocked = true;

    /**
     * 密码是否未过期
     */
    @Column(name = "credentials_non_expired", columnDefinition = "TINYINT DEFAULT 1")
    private Boolean credentialsNonExpired = true;

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
     * 用户角色关联
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private List<UserRole> userRoles;

    // ================================
    // 构造函数
    // ================================

    public User() {
        super();
    }

    public User(String username, String password, String email, String realName) {
        this();
        this.username = username;
        this.password = password;
        this.email = email;
        this.realName = realName;
    }

    public User(String username, String password, String email, String realName, String phone, String gender) {
        this(username, password, email, realName);
        this.phone = phone;
        this.gender = gender;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 获取主要角色名称（安全版本，避免懒加载异常）
     */
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

    /**
     * 获取主要角色键（安全版本，避免懒加载异常）
     */
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

    /**
     * 检查是否有指定角色（安全版本，避免懒加载异常）
     */
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

    /**
     * 是否是管理员
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * 是否是教师
     */
    public boolean isTeacher() {
        return hasRole("TEACHER");
    }

    /**
     * 是否是学生
     */
    public boolean isStudent() {
        return hasRole("STUDENT");
    }

    /**
     * 记录登录信息
     */
    public void recordLogin(String loginIp) {
        this.lastLoginTime = LocalDateTime.now();
        this.lastLoginIp = loginIp;
        this.loginCount = (this.loginCount == null ? 0 : this.loginCount) + 1;
    }

    /**
     * 重置密码令牌
     */
    public void generateResetToken(String token, int expireMinutes) {
        this.resetToken = token;
        this.resetTokenExpire = LocalDateTime.now().plusMinutes(expireMinutes);
    }

    /**
     * 清除重置令牌
     */
    public void clearResetToken() {
        this.resetToken = null;
        this.resetTokenExpire = null;
    }

    /**
     * 检查重置令牌是否有效
     */
    public boolean isResetTokenValid(String token) {
        return this.resetToken != null
               && this.resetToken.equals(token)
               && this.resetTokenExpire != null
               && this.resetTokenExpire.isAfter(LocalDateTime.now());
    }

    /**
     * 锁定账户
     */
    public void lockAccount() {
        this.accountNonLocked = false;
        this.disable();
    }

    /**
     * 解锁账户
     */
    public void unlockAccount() {
        this.accountNonLocked = true;
        this.enable();
    }

    /**
     * 兼容性方法 - 为了向后兼容
     */
    public LocalDateTime getCreatedTime() {
        return this.createdAt;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdAt = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return this.updatedAt;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedAt = updatedTime;
    }

    // ================================
    // Getter/Setter 方法 (手动添加以解决Lombok问题)
    // ================================

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

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpire() {
        return resetTokenExpire;
    }

    public void setResetTokenExpire(LocalDateTime resetTokenExpire) {
        this.resetTokenExpire = resetTokenExpire;
    }

    public Boolean getAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(Boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public Boolean getAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public Boolean getCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    // 继续添加缺失的getter/setter方法

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setStatus(int status) {
        this.status = status;
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
}
