package com.campus.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 权限实体类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Entity
@Table(name = "tb_permission")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "权限名称不能为空")
    @Size(max = 50, message = "权限名称长度不能超过50个字符")
    @Column(name = "permission_name", nullable = false, length = 50)
    private String permissionName;

    @NotBlank(message = "权限键不能为空")
    @Size(max = 100, message = "权限键长度不能超过100个字符")
    @Column(name = "permission_key", unique = true, nullable = false, length = 100)
    private String permissionKey;

    @Size(max = 200, message = "权限描述长度不能超过200个字符")
    @Column(name = "description", length = 200)
    private String description;

    @Size(max = 50, message = "权限类型长度不能超过50个字符")
    @Column(name = "permission_type", length = 50)
    private String permissionType; // 权限类型：menu, button, api等

    @Size(max = 200, message = "资源路径长度不能超过200个字符")
    @Column(name = "resource_path", length = 200)
    private String resourcePath; // 资源路径

    @Column(name = "parent_id")
    private Long parentId; // 父权限ID

    @Column(nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;

    @Column(name = "sort_order", columnDefinition = "INT DEFAULT 0")
    private Integer sortOrder = 0;

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

    // 关联关系 - 角色权限
    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY)
    private List<RolePermission> rolePermissions;

    // 构造函数
    public Permission() {}

    public Permission(String permissionName, String permissionKey) {
        this.permissionName = permissionName;
        this.permissionKey = permissionKey;
    }

    public Permission(String permissionName, String permissionKey, String description, String permissionType) {
        this.permissionName = permissionName;
        this.permissionKey = permissionKey;
        this.description = description;
        this.permissionType = permissionType;
    }

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionKey() {
        return permissionKey;
    }

    public void setPermissionKey(String permissionKey) {
        this.permissionKey = permissionKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    public List<RolePermission> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(List<RolePermission> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    // 兼容性方法 - 为了向后兼容旧代码
    public String getPermissionCode() {
        return permissionKey;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionKey = permissionCode;
    }

    public String getResourceType() {
        return permissionType;
    }

    public void setResourceType(String resourceType) {
        this.permissionType = resourceType;
    }

    public String getResourceUrl() {
        return resourcePath;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourcePath = resourceUrl;
    }

    public String getPermissionDesc() {
        return description;
    }

    public void setPermissionDesc(String permissionDesc) {
        this.description = permissionDesc;
    }

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

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", permissionName='" + permissionName + '\'' +
                ", permissionKey='" + permissionKey + '\'' +
                ", description='" + description + '\'' +
                ", permissionType='" + permissionType + '\'' +
                ", status=" + status +
                ", createdTime=" + createdTime +
                '}';
    }
}
