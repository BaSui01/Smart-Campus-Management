package com.campus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色实体类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Entity
@Table(name = "tb_role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "角色名不能为空")
    @Size(max = 50, message = "角色名长度不能超过50个字符")
    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName;

    @NotBlank(message = "角色标识不能为空")
    @Size(max = 50, message = "角色标识长度不能超过50个字符")
    @Column(name = "role_key", nullable = false, unique = true, length = 50)
    private String roleKey;

    @Column(name = "role_sort", columnDefinition = "INT DEFAULT 0")
    private Integer roleSort = 0;

    @Column(name = "data_scope", columnDefinition = "TINYINT DEFAULT 1")
    private Integer dataScope = 1;

    @Column(name = "menu_check_strictly", columnDefinition = "TINYINT DEFAULT 1")
    private Integer menuCheckStrictly = 1;

    @Column(name = "dept_check_strictly", columnDefinition = "TINYINT DEFAULT 1")
    private Integer deptCheckStrictly = 1;

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Column(length = 500)
    private String remark;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted", columnDefinition = "TINYINT DEFAULT 0")
    private Integer deleted = 0;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 关联关系
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<UserRole> userRoles;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<RolePermission> rolePermissions;

    // 构造函数
    public Role() {}

    public Role(String roleName, String roleKey) {
        this.roleName = roleName;
        this.roleKey = roleKey;
    }

    public Role(String roleName, String roleKey, Integer roleSort, String remark) {
        this.roleName = roleName;
        this.roleKey = roleKey;
        this.roleSort = roleSort;
        this.remark = remark;
    }

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getRoleSort() {
        return roleSort;
    }

    public void setRoleSort(Integer roleSort) {
        this.roleSort = roleSort;
    }

    public Integer getDataScope() {
        return dataScope;
    }

    public void setDataScope(Integer dataScope) {
        this.dataScope = dataScope;
    }

    public Integer getMenuCheckStrictly() {
        return menuCheckStrictly;
    }

    public void setMenuCheckStrictly(Integer menuCheckStrictly) {
        this.menuCheckStrictly = menuCheckStrictly;
    }

    public Integer getDeptCheckStrictly() {
        return deptCheckStrictly;
    }

    public void setDeptCheckStrictly(Integer deptCheckStrictly) {
        this.deptCheckStrictly = deptCheckStrictly;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                ", roleKey='" + roleKey + '\'' +
                ", roleSort=" + roleSort +
                ", status=" + status +
                ", remark='" + remark + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
