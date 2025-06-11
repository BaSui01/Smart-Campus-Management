package com.campus.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 权限实体类
 * 管理系统权限信息和资源访问控制
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_permission", indexes = {
    @Index(name = "idx_permission_key", columnList = "permission_key", unique = true),
    @Index(name = "idx_permission_type", columnList = "permission_type"),
    @Index(name = "idx_parent_id", columnList = "parent_id"),
    @Index(name = "idx_status_deleted", columnList = "status,deleted"),
    @Index(name = "idx_sort_order", columnList = "sort_order")
})
public class Permission extends BaseEntity {

    /**
     * 权限名称
     */
    @NotBlank(message = "权限名称不能为空")
    @Size(max = 50, message = "权限名称长度不能超过50个字符")
    @Column(name = "permission_name", nullable = false, length = 50)
    private String permissionName;

    /**
     * 权限键（唯一标识）
     */
    @NotBlank(message = "权限键不能为空")
    @Size(max = 100, message = "权限键长度不能超过100个字符")
    @Column(name = "permission_key", unique = true, nullable = false, length = 100)
    private String permissionKey;

    /**
     * 权限描述
     */
    @Size(max = 200, message = "权限描述长度不能超过200个字符")
    @Column(name = "description", length = 200)
    private String description;

    /**
     * 权限类型 (menu/button/api/v1/data)
     */
    @Size(max = 20, message = "权限类型长度不能超过20个字符")
    @Column(name = "permission_type", length = 20)
    private String permissionType = "api";

    /**
     * 资源路径
     */
    @Size(max = 200, message = "资源路径长度不能超过200个字符")
    @Column(name = "resource_path", length = 200)
    private String resourcePath;

    /**
     * HTTP方法 (GET/POST/PUT/DELETE/ALL)
     */
    @Size(max = 10, message = "HTTP方法长度不能超过10个字符")
    @Column(name = "http_method", length = 10)
    private String httpMethod = "ALL";

    /**
     * 父权限ID
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 权限级别（数字越小级别越高）
     */
    @Column(name = "permission_level", columnDefinition = "INT DEFAULT 1")
    private Integer permissionLevel = 1;

    /**
     * 排序顺序
     */
    @Column(name = "sort_order", columnDefinition = "INT DEFAULT 0")
    private Integer sortOrder = 0;

    /**
     * 是否为系统权限
     */
    @Column(name = "is_system", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    private Boolean isSystem = false;

    /**
     * 图标
     */
    @Size(max = 100, message = "图标长度不能超过100个字符")
    @Column(name = "icon", length = 100)
    private String icon;

    // ================================
    // 关联关系
    // ================================

    /**
     * 角色权限关联
     */
    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<RolePermission> rolePermissions;

    // ================================
    // 构造函数
    // ================================

    public Permission() {
        super();
    }

    public Permission(String permissionName, String permissionKey) {
        this();
        this.permissionName = permissionName;
        this.permissionKey = permissionKey;
    }

    public Permission(String permissionName, String permissionKey, String description, String permissionType) {
        this(permissionName, permissionKey);
        this.description = description;
        this.permissionType = permissionType;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 检查是否为菜单权限
     */
    public boolean isMenuPermission() {
        return "menu".equals(permissionType);
    }

    /**
     * 检查是否为按钮权限
     */
    public boolean isButtonPermission() {
        return "button".equals(permissionType);
    }

    /**
     * 检查是否为API权限
     */
    public boolean isApiPermission() {
        return "api".equals(permissionType);
    }

    /**
     * 检查是否为数据权限
     */
    public boolean isDataPermission() {
        return "data".equals(permissionType);
    }

    /**
     * 检查是否为系统权限
     */
    public boolean isSystemPermission() {
        return isSystem != null && isSystem;
    }

    /**
     * 检查是否为根权限（没有父权限）
     */
    public boolean isRootPermission() {
        return parentId == null || parentId == 0;
    }

    /**
     * 获取权限类型文本
     */
    public String getPermissionTypeText() {
        if (permissionType == null) return "未知";
        return switch (permissionType) {
            case "menu" -> "菜单权限";
            case "button" -> "按钮权限";
            case "api" -> "接口权限";
            case "data" -> "数据权限";
            default -> permissionType;
        };
    }

    /**
     * 获取HTTP方法文本
     */
    public String getHttpMethodText() {
        if (httpMethod == null) return "ALL";
        return switch (httpMethod.toUpperCase()) {
            case "GET" -> "查询";
            case "POST" -> "新增";
            case "PUT" -> "修改";
            case "DELETE" -> "删除";
            case "ALL" -> "全部";
            default -> httpMethod;
        };
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

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", permissionName='" + permissionName + '\'' +
                ", permissionKey='" + permissionKey + '\'' +
                ", description='" + description + '\'' +
                ", permissionType='" + permissionType + '\'' +
                ", resourcePath='" + resourcePath + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", parentId=" + parentId +
                ", status=" + status +
                ", sortOrder=" + sortOrder +
                ", isSystem=" + isSystem +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
