package com.campus.domain.entity.auth;

import com.campus.domain.entity.infrastructure.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

/**
 * 用户角色关联实体类
 * 管理用户和角色的多对多关联关系
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_user_role", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_role_id", columnList = "role_id"),
    @Index(name = "idx_user_role", columnList = "user_id,role_id", unique = true),
    @Index(name = "idx_status_deleted", columnList = "status,deleted")
})
public class UserRole extends BaseEntity {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    /**
     * 分配类型
     * assign（分配）, inherit（继承）, temporary（临时）
     */
    @Column(name = "assign_type", length = 10, nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'assign'")
    private String assignType = "assign";

    /**
     * 分配人ID
     */
    @Column(name = "assigned_by")
    private Long assignedBy;

    /**
     * 分配时间
     */
    @Column(name = "assigned_at")
    private java.time.LocalDateTime assignedAt;

    /**
     * 过期时间
     */
    @Column(name = "expires_at")
    private java.time.LocalDateTime expiresAt;

    /**
     * 是否为主要角色
     */
    @Column(name = "is_primary", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    private Boolean isPrimary = false;

    // ================================
    // 关联关系
    // ================================

    /**
     * 用户信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    /**
     * 角色信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private Role role;

    /**
     * 分配人信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by", insertable = false, updatable = false)
    @JsonIgnore
    private User assignedByUser;

    // ================================
    // 构造函数
    // ================================

    public UserRole() {
        super();
    }

    public UserRole(Long userId, Long roleId) {
        this();
        this.userId = userId;
        this.roleId = roleId;
        this.assignedAt = java.time.LocalDateTime.now();
    }

    public UserRole(Long userId, Long roleId, String assignType) {
        this(userId, roleId);
        this.assignType = assignType;
    }

    public UserRole(Long userId, Long roleId, Long assignedBy) {
        this(userId, roleId);
        this.assignedBy = assignedBy;
    }

    public UserRole(Long userId, Long roleId, boolean isPrimary) {
        this(userId, roleId);
        this.isPrimary = isPrimary;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 检查是否为分配类型
     */
    public boolean isAssigned() {
        return "assign".equals(assignType);
    }

    /**
     * 检查是否为继承类型
     */
    public boolean isInherited() {
        return "inherit".equals(assignType);
    }

    /**
     * 检查是否为临时类型
     */
    public boolean isTemporary() {
        return "temporary".equals(assignType);
    }

    /**
     * 检查角色是否已过期
     */
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(java.time.LocalDateTime.now());
    }

    /**
     * 检查角色是否有效
     */
    public boolean isValid() {
        return isEnabled() && !isExpired();
    }

    /**
     * 检查是否为主要角色
     */
    public boolean isPrimaryRole() {
        return isPrimary != null && isPrimary;
    }

    /**
     * 获取用户名称
     */
    public String getUserName() {
        try {
            return user != null ? user.getRealName() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取角色名称
     */
    public String getRoleName() {
        try {
            return role != null ? role.getRoleName() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取分配人姓名
     */
    public String getAssignedByName() {
        try {
            return assignedByUser != null ? assignedByUser.getRealName() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取分配类型文本
     */
    public String getAssignTypeText() {
        if (assignType == null) return "未知";
        return switch (assignType) {
            case "assign" -> "分配";
            case "inherit" -> "继承";
            case "temporary" -> "临时";
            default -> assignType;
        };
    }

    /**
     * 设置过期时间（天数）
     */
    public void setExpiresInDays(int days) {
        this.expiresAt = java.time.LocalDateTime.now().plusDays(days);
    }

    /**
     * 设置为主要角色
     */
    public void setPrimary() {
        this.isPrimary = true;
    }

    /**
     * 取消主要角色
     */
    public void unsetPrimary() {
        this.isPrimary = false;
    }

    /**
     * 撤销角色
     */
    public void revoke() {
        this.disable();
    }

    /**
     * 恢复角色
     */
    public void restore() {
        this.enable();
    }

    // ================================
    // Getter and Setter methods
    // ================================

    // Getter 和 Setter 方法
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

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getAssignType() {
        return assignType;
    }

    public void setAssignType(String assignType) {
        this.assignType = assignType;
    }

    public Long getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Long assignedBy) {
        this.assignedBy = assignedBy;
    }

    public java.time.LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(java.time.LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public java.time.LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(java.time.LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "id=" + id +
                ", userId=" + userId +
                ", roleId=" + roleId +
                ", assignType='" + assignType + '\'' +
                ", assignedBy=" + assignedBy +
                ", assignedAt=" + assignedAt +
                ", expiresAt=" + expiresAt +
                ", isPrimary=" + isPrimary +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
