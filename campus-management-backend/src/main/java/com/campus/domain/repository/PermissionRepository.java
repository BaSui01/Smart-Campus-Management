package com.campus.domain.repository;

import com.campus.domain.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 权限仓储接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * 根据权限键查找权限
     */
    Optional<Permission> findByPermissionKey(String permissionKey);

    /**
     * 根据权限名称查找权限
     */
    Optional<Permission> findByPermissionName(String permissionName);

    /**
     * 根据权限类型查找权限列表
     */
    List<Permission> findByPermissionType(String permissionType);

    /**
     * 根据父权限ID查找子权限
     */
    List<Permission> findByParentId(Long parentId);

    /**
     * 根据状态查找权限列表
     */
    List<Permission> findByStatus(Integer status);

    /**
     * 根据状态查找权限列表（按排序字段排序）
     */
    List<Permission> findByStatusOrderBySortOrder(Integer status);

    /**
     * 检查权限键是否存在
     */
    boolean existsByPermissionKey(String permissionKey);

    /**
     * 检查权限名称是否存在
     */
    boolean existsByPermissionName(String permissionName);

    /**
     * 兼容性方法 - 根据权限代码查找权限
     */
    default Optional<Permission> findByPermissionCode(String permissionCode) {
        return findByPermissionKey(permissionCode);
    }

    /**
     * 兼容性方法 - 检查权限代码是否存在
     */
    default boolean existsByPermissionCode(String permissionCode) {
        return existsByPermissionKey(permissionCode);
    }

    /**
     * 兼容性方法 - 根据状态查找权限列表（按权限代码排序）
     */
    default List<Permission> findByStatusOrderByPermissionCode(Integer status) {
        return findByStatusOrderBySortOrder(status);
    }

    /**
     * 兼容性方法 - 根据资源类型查找权限列表（按权限代码排序）
     */
    default List<Permission> findByResourceTypeOrderByPermissionCode(String resourceType) {
        return findByPermissionType(resourceType);
    }

    /**
     * 兼容性方法 - 根据角色ID查找权限
     */
    default List<Permission> findByRoleId(Long roleId) {
        return findPermissionsByRoleId(roleId);
    }

    /**
     * 兼容性方法 - 根据用户ID查找权限
     */
    default List<Permission> findByUserId(Long userId) {
        return findPermissionsByUserId(userId);
    }

    /**
     * 根据权限名称模糊查询
     */
    List<Permission> findByPermissionNameContaining(String permissionName);

    /**
     * 搜索权限（根据权限名称、权限键、描述）
     */
    @Query("SELECT p FROM Permission p WHERE " +
           "p.permissionName LIKE %:keyword% OR " +
           "p.permissionKey LIKE %:keyword% OR " +
           "p.description LIKE %:keyword%")
    List<Permission> searchPermissions(@Param("keyword") String keyword);

    /**
     * 获取所有启用的权限（按排序字段排序）
     */
    @Query("SELECT p FROM Permission p WHERE p.status = 1 AND p.deleted = 0 ORDER BY p.sortOrder ASC")
    List<Permission> findAllActivePermissions();

    /**
     * 根据角色ID查找权限
     */
    @Query("SELECT p FROM Permission p JOIN p.rolePermissions rp WHERE rp.roleId = :roleId")
    List<Permission> findPermissionsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查找权限（通过角色关联）
     */
    @Query("SELECT DISTINCT p FROM Permission p " +
           "JOIN p.rolePermissions rp " +
           "JOIN rp.role r " +
           "JOIN r.userRoles ur " +
           "WHERE ur.userId = :userId AND p.status = 1 AND p.deleted = 0")
    List<Permission> findPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 获取顶级权限（父权限ID为空的权限）
     */
    @Query("SELECT p FROM Permission p WHERE p.parentId IS NULL AND p.status = 1 AND p.deleted = 0 ORDER BY p.sortOrder ASC")
    List<Permission> findTopLevelPermissions();

    /**
     * 统计权限数量按类型
     */
    @Query("SELECT p.permissionType, COUNT(p) FROM Permission p GROUP BY p.permissionType")
    List<Object[]> countPermissionsByType();

    /**
     * 统计权限数量按状态
     */
    @Query("SELECT p.status, COUNT(p) FROM Permission p GROUP BY p.status")
    List<Object[]> countPermissionsByStatus();
}
