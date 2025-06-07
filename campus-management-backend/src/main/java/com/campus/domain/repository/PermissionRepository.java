package com.campus.domain.repository;

import com.campus.domain.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 权限Repository接口
 * 提供权限相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface PermissionRepository extends BaseRepository<Permission> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据权限键查找权限
     */
    @Query("SELECT p FROM Permission p WHERE p.permissionKey = :permissionKey AND p.deleted = 0")
    Optional<Permission> findByPermissionKey(@Param("permissionKey") String permissionKey);

    /**
     * 根据权限名称查找权限
     */
    @Query("SELECT p FROM Permission p WHERE p.permissionName = :permissionName AND p.deleted = 0")
    Optional<Permission> findByPermissionName(@Param("permissionName") String permissionName);

    /**
     * 根据权限类型查找权限列表
     */
    @Query("SELECT p FROM Permission p WHERE p.permissionType = :permissionType AND p.deleted = 0 ORDER BY p.sortOrder ASC")
    List<Permission> findByPermissionType(@Param("permissionType") String permissionType);

    /**
     * 分页根据权限类型查找权限列表
     */
    @Query("SELECT p FROM Permission p WHERE p.permissionType = :permissionType AND p.deleted = 0")
    Page<Permission> findByPermissionType(@Param("permissionType") String permissionType, Pageable pageable);

    /**
     * 根据父权限ID查找子权限
     */
    @Query("SELECT p FROM Permission p WHERE p.parentId = :parentId AND p.deleted = 0 ORDER BY p.sortOrder ASC")
    List<Permission> findByParentId(@Param("parentId") Long parentId);

    /**
     * 根据权限名称模糊查询
     */
    @Query("SELECT p FROM Permission p WHERE p.permissionName LIKE %:permissionName% AND p.deleted = 0 ORDER BY p.sortOrder ASC")
    List<Permission> findByPermissionNameContaining(@Param("permissionName") String permissionName);

    /**
     * 分页根据权限名称模糊查询
     */
    @Query("SELECT p FROM Permission p WHERE p.permissionName LIKE %:permissionName% AND p.deleted = 0")
    Page<Permission> findByPermissionNameContaining(@Param("permissionName") String permissionName, Pageable pageable);

    /**
     * 获取所有启用的权限（按排序字段排序）
     */
    @Query("SELECT p FROM Permission p WHERE p.status = 1 AND p.deleted = 0 ORDER BY p.sortOrder ASC")
    List<Permission> findAllActivePermissions();

    /**
     * 分页获取所有启用的权限
     */
    @Query("SELECT p FROM Permission p WHERE p.status = 1 AND p.deleted = 0")
    Page<Permission> findAllActivePermissions(Pageable pageable);

    /**
     * 获取顶级权限（父权限ID为空的权限）
     */
    @Query("SELECT p FROM Permission p WHERE p.parentId IS NULL AND p.status = 1 AND p.deleted = 0 ORDER BY p.sortOrder ASC")
    List<Permission> findTopLevelPermissions();

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找权限
     */
    @Query("SELECT p FROM Permission p WHERE " +
           "(:permissionName IS NULL OR p.permissionName LIKE %:permissionName%) AND " +
           "(:permissionKey IS NULL OR p.permissionKey LIKE %:permissionKey%) AND " +
           "(:permissionType IS NULL OR p.permissionType = :permissionType) AND " +
           "(:parentId IS NULL OR p.parentId = :parentId) AND " +
           "p.deleted = 0")
    Page<Permission> findByMultipleConditions(@Param("permissionName") String permissionName,
                                             @Param("permissionKey") String permissionKey,
                                             @Param("permissionType") String permissionType,
                                             @Param("parentId") Long parentId,
                                             Pageable pageable);

    /**
     * 搜索权限（根据权限名称、权限键、描述）
     */
    @Query("SELECT p FROM Permission p WHERE " +
           "(p.permissionName LIKE %:keyword% OR " +
           "p.permissionKey LIKE %:keyword% OR " +
           "p.description LIKE %:keyword%) AND " +
           "p.deleted = 0 ORDER BY p.sortOrder ASC")
    List<Permission> searchPermissions(@Param("keyword") String keyword);

    /**
     * 分页搜索权限
     */
    @Query("SELECT p FROM Permission p WHERE " +
           "(p.permissionName LIKE %:keyword% OR " +
           "p.permissionKey LIKE %:keyword% OR " +
           "p.description LIKE %:keyword%) AND " +
           "p.deleted = 0")
    Page<Permission> searchPermissions(@Param("keyword") String keyword, Pageable pageable);

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 根据角色ID查找权限
     */
    @Query("SELECT p FROM Permission p JOIN p.rolePermissions rp WHERE rp.roleId = :roleId AND p.deleted = 0 ORDER BY p.sortOrder ASC")
    List<Permission> findPermissionsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查找权限（通过角色关联）
     */
    @Query("SELECT DISTINCT p FROM Permission p " +
           "JOIN p.rolePermissions rp " +
           "JOIN rp.role r " +
           "JOIN r.userRoles ur " +
           "WHERE ur.userId = :userId AND p.status = 1 AND p.deleted = 0 ORDER BY p.sortOrder ASC")
    List<Permission> findPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 查找权限并预加载角色信息
     */
    @Query("SELECT DISTINCT p FROM Permission p LEFT JOIN FETCH p.rolePermissions rp LEFT JOIN FETCH rp.role WHERE p.deleted = 0 ORDER BY p.sortOrder ASC")
    List<Permission> findAllWithRoles();

    /**
     * 根据权限类型查找权限树
     */
    @Query("SELECT p FROM Permission p WHERE p.permissionType = :permissionType AND p.deleted = 0 ORDER BY p.parentId ASC, p.sortOrder ASC")
    List<Permission> findPermissionTreeByType(@Param("permissionType") String permissionType);

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据权限类型统计权限数量
     */
    @Query("SELECT p.permissionType, COUNT(p) FROM Permission p WHERE p.deleted = 0 GROUP BY p.permissionType ORDER BY p.permissionType")
    List<Object[]> countByPermissionType();

    /**
     * 统计权限数量按状态
     */
    @Query("SELECT p.status, COUNT(p) FROM Permission p WHERE p.deleted = 0 GROUP BY p.status")
    List<Object[]> countPermissionsByStatus();

    /**
     * 统计指定权限的角色数量
     */
    @Query("SELECT COUNT(rp) FROM RolePermission rp WHERE rp.permissionId = :permissionId AND rp.deleted = 0")
    long countRolesByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 统计子权限数量
     */
    @Query("SELECT COUNT(p) FROM Permission p WHERE p.parentId = :parentId AND p.deleted = 0")
    long countChildPermissions(@Param("parentId") Long parentId);

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查权限键是否存在
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Permission p WHERE p.permissionKey = :permissionKey AND p.deleted = 0")
    boolean existsByPermissionKey(@Param("permissionKey") String permissionKey);

    /**
     * 检查权限键是否存在（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Permission p WHERE p.permissionKey = :permissionKey AND p.id != :excludeId AND p.deleted = 0")
    boolean existsByPermissionKeyAndIdNot(@Param("permissionKey") String permissionKey, @Param("excludeId") Long excludeId);

    /**
     * 检查权限名称是否存在
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Permission p WHERE p.permissionName = :permissionName AND p.deleted = 0")
    boolean existsByPermissionName(@Param("permissionName") String permissionName);

    /**
     * 检查权限名称是否存在（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Permission p WHERE p.permissionName = :permissionName AND p.id != :excludeId AND p.deleted = 0")
    boolean existsByPermissionNameAndIdNot(@Param("permissionName") String permissionName, @Param("excludeId") Long excludeId);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新权限排序
     */
    @Modifying
    @Query("UPDATE Permission p SET p.sortOrder = :sortOrder, p.updatedAt = CURRENT_TIMESTAMP WHERE p.id = :permissionId")
    int updateSortOrder(@Param("permissionId") Long permissionId, @Param("sortOrder") Integer sortOrder);

    /**
     * 批量更新权限排序
     */
    @Modifying
    @Query("UPDATE Permission p SET p.sortOrder = :sortOrder, p.updatedAt = CURRENT_TIMESTAMP WHERE p.id IN :permissionIds")
    int batchUpdateSortOrder(@Param("permissionIds") List<Long> permissionIds, @Param("sortOrder") Integer sortOrder);

    /**
     * 更新权限类型
     */
    @Modifying
    @Query("UPDATE Permission p SET p.permissionType = :permissionType, p.updatedAt = CURRENT_TIMESTAMP WHERE p.id = :permissionId")
    int updatePermissionType(@Param("permissionId") Long permissionId, @Param("permissionType") String permissionType);

    // ================================
    // 兼容性方法（为现有Service提供支持）
    // ================================

    /**
     * 根据权限代码查找权限（兼容性方法）
     */
    default Optional<Permission> findByPermissionCode(String permissionCode) {
        return findByPermissionKey(permissionCode);
    }

    /**
     * 检查权限代码是否存在（兼容性方法）
     */
    default boolean existsByPermissionCode(String permissionCode) {
        return existsByPermissionKey(permissionCode);
    }

    /**
     * 根据资源类型查找权限列表（兼容性方法）
     */
    default List<Permission> findByResourceTypeOrderByPermissionCode(String resourceType) {
        return findByPermissionType(resourceType);
    }

    /**
     * 根据角色ID查找权限（兼容性方法）
     */
    default List<Permission> findByRoleId(Long roleId) {
        return findPermissionsByRoleId(roleId);
    }

    /**
     * 根据用户ID查找权限（兼容性方法）
     */
    default List<Permission> findByUserId(Long userId) {
        return findPermissionsByUserId(userId);
    }

    /**
     * 根据资源类型和删除状态查找权限（兼容性方法）
     */
    default List<Permission> findByResourceTypeAndDeleted(String resourceType, Integer deleted) {
        if (deleted == 0) {
            return findByPermissionType(resourceType);
        }
        return List.of();
    }

    /**
     * 根据删除状态查找权限并按创建时间排序（兼容性方法）
     */
    default List<Permission> findByDeletedOrderByCreatedTimeDesc(Integer deleted) {
        if (deleted == 0) {
            return findAllActivePermissions();
        }
        return List.of();
    }

    /**
     * 分页根据删除状态查找权限并按创建时间排序（兼容性方法）
     */
    default Page<Permission> findByDeletedOrderByCreatedTimeDesc(Integer deleted, Pageable pageable) {
        if (deleted == 0) {
            return findAll(pageable);
        }
        return Page.empty(pageable);
    }

    /**
     * 检查权限键是否存在（兼容性方法）
     */
    default boolean existsByPermissionKeyAndDeleted(String permissionKey, Integer deleted) {
        if (deleted == 0) {
            return existsByPermissionKey(permissionKey);
        }
        return false;
    }

    /**
     * 检查权限名称是否存在（兼容性方法）
     */
    default boolean existsByPermissionNameAndDeleted(String permissionName, Integer deleted) {
        if (deleted == 0) {
            return existsByPermissionName(permissionName);
        }
        return false;
    }

    /**
     * 根据状态查找权限并按权限代码排序（兼容性方法）
     */
    default List<Permission> findByStatusOrderByPermissionCode(Integer status) {
        if (status == 1) {
            return findAllActivePermissions();
        }
        return List.of();
    }

}
