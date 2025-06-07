package com.campus.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 角色实体类
 * 管理系统角色信息和权限分配
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_role", indexes = {
    @Index(name = "idx_role_key", columnList = "role_key", unique = true),
    @Index(name = "idx_role_name", columnList = "role_name"),
    @Index(name = "idx_status_deleted", columnList = "status,deleted"),
    @Index(name = "idx_sort_order", columnList = "sort_order")
})
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50个字符")
    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    /**
     * 角色键（唯一标识）
     */
    @NotBlank(message = "角色键不能为空")
    @Size(max = 50, message = "角色键长度不能超过50个字符")
    @Column(name = "role_key", unique = true, nullable = false, length = 50)
    private String roleKey;

    /**
     * 角色描述
     */
    @Size(max = 200, message = "角色描述长度不能超过200个字符")
    @Column(name = "description", length = 200)
    private String description;

    /**
     * 排序顺序
     */
    @Column(name = "sort_order", columnDefinition = "INT DEFAULT 0")
    private Integer sortOrder = 0;

    /**
     * 是否为系统内置角色
     */
    @Column(name = "is_system", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    private Boolean isSystem = false;

    /**
     * 角色级别（数字越小级别越高）
     */
    @Column(name = "role_level", columnDefinition = "INT DEFAULT 99")
    private Integer roleLevel = 99;

    // 非持久化字段，用于显示用户数量
    @Transient
    private Integer userCount = 0;

    // ================================
    // 关联关系
    // ================================

    /**
     * 用户角色关联
     */
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserRole> userRoles;

    /**
     * 角色权限关联
     */
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<RolePermission> rolePermissions;

    // ================================
    // 构造函数
    // ================================

    public Role() {
        super();
    }

    public Role(String roleName, String roleKey) {
        this();
        this.roleName = roleName;
        this.roleKey = roleKey;
    }

    public Role(String roleName, String roleKey, String description) {
        this(roleName, roleKey);
        this.description = description;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 检查是否为管理员角色
     */
    public boolean isAdmin() {
        return "ADMIN".equals(roleKey) || "SUPER_ADMIN".equals(roleKey);
    }

    /**
     * 检查是否为教师角色
     */
    public boolean isTeacher() {
        return "TEACHER".equals(roleKey);
    }

    /**
     * 检查是否为学生角色
     */
    public boolean isStudent() {
        return "STUDENT".equals(roleKey);
    }

    /**
     * 检查是否为家长角色
     */
    public boolean isParent() {
        return "PARENT".equals(roleKey);
    }

    /**
     * 检查是否为系统角色
     */
    public boolean isSystemRole() {
        return isSystem != null && isSystem;
    }

    /**
     * 获取角色级别文本
     */
    public String getRoleLevelText() {
        if (roleLevel == null) return "普通";
        return switch (roleLevel) {
            case 1 -> "超级管理员";
            case 2 -> "系统管理员";
            case 3 -> "部门管理员";
            case 4 -> "教师";
            case 5 -> "学生";
            case 6 -> "家长";
            default -> "普通用户";
        };
    }

    /**
     * 检查角色级别是否高于指定角色
     */
    public boolean isHigherThan(Role other) {
        if (other == null || this.roleLevel == null || other.roleLevel == null) {
            return false;
        }
        return this.roleLevel < other.roleLevel; // 数字越小级别越高
    }

    // ================================
    // Getter and Setter methods
    // ================================

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }



    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public List<RolePermission> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(List<RolePermission> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                ", roleKey='" + roleKey + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", sortOrder=" + sortOrder +
                ", isSystem=" + isSystem +
                ", roleLevel=" + roleLevel +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
