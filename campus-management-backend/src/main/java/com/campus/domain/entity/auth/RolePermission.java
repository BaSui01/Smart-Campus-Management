package com.campus.domain.entity.auth;

import com.campus.domain.entity.infrastructure.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

/**
 * 角色权限关联实体类
 * 管理角色和权限的多对多关联关系
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_role_permission", indexes = {
    @Index(name = "idx_role_id", columnList = "role_id"),
    @Index(name = "idx_permission_id", columnList = "permission_id"),
    @Index(name = "idx_role_permission", columnList = "role_id,permission_id", unique = true),
    @Index(name = "idx_status_deleted", columnList = "status,deleted")
})
public class RolePermission extends BaseEntity {

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    /**
     * 权限ID
     */
    @NotNull(message = "权限ID不能为空")
    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    /**
     * 授权类型
     * grant（授权）, deny（拒绝）
     */
    @Column(name = "grant_type", length = 10, nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'grant'")
    private String grantType = "grant";

    /**
     * 授权人ID
     */
    @Column(name = "granted_by")
    private Long grantedBy;

    /**
     * 授权时间
     */
    @Column(name = "granted_at")
    private java.time.LocalDateTime grantedAt;

    /**
     * 过期时间
     */
    @Column(name = "expires_at")
    private java.time.LocalDateTime expiresAt;

    // ================================
    // 关联关系
    // ================================

    /**
     * 角色信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    @JsonIgnore
    private Role role;

    /**
     * 权限信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", insertable = false, updatable = false)
    @JsonIgnore
    private Permission permission;

    /**
     * 授权人信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by", insertable = false, updatable = false)
    @JsonIgnore
    private User grantedByUser;

    // ================================
    // 构造函数
    // ================================

    public RolePermission() {
        super();
    }

    public RolePermission(Long roleId, Long permissionId) {
        this();
        this.roleId = roleId;
        this.permissionId = permissionId;
        this.grantedAt = java.time.LocalDateTime.now();
    }

    public RolePermission(Long roleId, Long permissionId, String grantType) {
        this(roleId, permissionId);
        this.grantType = grantType;
    }

    public RolePermission(Long roleId, Long permissionId, Long grantedBy) {
        this(roleId, permissionId);
        this.grantedBy = grantedBy;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 检查是否为授权类型
     */
    public boolean isGranted() {
        return "grant".equals(grantType);
    }

    /**
     * 检查是否为拒绝类型
     */
    public boolean isDenied() {
        return "deny".equals(grantType);
    }

    /**
     * 检查权限是否已过期
     */
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(java.time.LocalDateTime.now());
    }

    /**
     * 检查权限是否有效
     */
    public boolean isValid() {
        return isEnabled() && isGranted() && !isExpired();
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
     * 获取权限名称
     */
    public String getPermissionName() {
        try {
            return permission != null ? permission.getPermissionName() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取授权人姓名
     */
    public String getGrantedByName() {
        try {
            return grantedByUser != null ? grantedByUser.getRealName() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取授权类型文本
     */
    public String getGrantTypeText() {
        if (grantType == null) return "未知";
        return switch (grantType) {
            case "grant" -> "授权";
            case "deny" -> "拒绝";
            default -> grantType;
        };
    }

    /**
     * 设置过期时间（天数）
     */
    public void setExpiresInDays(int days) {
        this.expiresAt = java.time.LocalDateTime.now().plusDays(days);
    }

    /**
     * 撤销权限
     */
    public void revoke() {
        this.grantType = "deny";
        this.disable();
    }

    /**
     * 恢复权限
     */
    public void restore() {
        this.grantType = "grant";
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

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public Long getGrantedBy() {
        return grantedBy;
    }

    public void setGrantedBy(Long grantedBy) {
        this.grantedBy = grantedBy;
    }

    public java.time.LocalDateTime getGrantedAt() {
        return grantedAt;
    }

    public void setGrantedAt(java.time.LocalDateTime grantedAt) {
        this.grantedAt = grantedAt;
    }

    public java.time.LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(java.time.LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        return "RolePermission{" +
                "id=" + id +
                ", roleId=" + roleId +
                ", permissionId=" + permissionId +
                ", grantType='" + grantType + '\'' +
                ", grantedBy=" + grantedBy +
                ", grantedAt=" + grantedAt +
                ", expiresAt=" + expiresAt +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
