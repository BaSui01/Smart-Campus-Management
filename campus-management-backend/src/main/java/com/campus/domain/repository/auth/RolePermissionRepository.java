package com.campus.domain.repository.auth;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.campus.domain.entity.auth.RolePermission;
import com.campus.domain.repository.infrastructure.BaseRepository;

import java.util.List;
import java.util.Optional;

/**
 * 角色权限关联Repository接口
 * 提供角色权限关联相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface RolePermissionRepository extends BaseRepository<RolePermission> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据角色ID查找权限关联
     */
    @Query("SELECT rp FROM RolePermission rp WHERE rp.roleId = :roleId AND rp.deleted = 0")
    List<RolePermission> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 分页根据角色ID查找权限关联
     */
    @Query("SELECT rp FROM RolePermission rp WHERE rp.roleId = :roleId AND rp.deleted = 0")
    Page<RolePermission> findByRoleId(@Param("roleId") Long roleId, Pageable pageable);

    /**
     * 根据权限ID查找角色关联
     */
    @Query("SELECT rp FROM RolePermission rp WHERE rp.permissionId = :permissionId AND rp.deleted = 0")
    List<RolePermission> findByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 分页根据权限ID查找角色关联
     */
    @Query("SELECT rp FROM RolePermission rp WHERE rp.permissionId = :permissionId AND rp.deleted = 0")
    Page<RolePermission> findByPermissionId(@Param("permissionId") Long permissionId, Pageable pageable);

    /**
     * 根据角色ID和权限ID查找关联
     */
    @Query("SELECT rp FROM RolePermission rp WHERE rp.roleId = :roleId AND rp.permissionId = :permissionId AND rp.deleted = 0")
    Optional<RolePermission> findByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 查找角色权限关联并预加载角色信息
     */
    @Query("SELECT DISTINCT rp FROM RolePermission rp LEFT JOIN FETCH rp.role WHERE rp.deleted = 0")
    List<RolePermission> findAllWithRole();

    /**
     * 查找角色权限关联并预加载权限信息
     */
    @Query("SELECT DISTINCT rp FROM RolePermission rp LEFT JOIN FETCH rp.permission WHERE rp.deleted = 0")
    List<RolePermission> findAllWithPermission();

    /**
     * 根据角色ID查找关联并预加载权限信息
     */
    @Query("SELECT DISTINCT rp FROM RolePermission rp LEFT JOIN FETCH rp.permission WHERE rp.roleId = :roleId AND rp.deleted = 0")
    List<RolePermission> findByRoleIdWithPermission(@Param("roleId") Long roleId);

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 统计指定角色的权限数量
     */
    @Query("SELECT COUNT(rp) FROM RolePermission rp WHERE rp.roleId = :roleId AND rp.deleted = 0")
    long countByRoleId(@Param("roleId") Long roleId);

    /**
     * 统计指定权限的角色数量
     */
    @Query("SELECT COUNT(rp) FROM RolePermission rp WHERE rp.permissionId = :permissionId AND rp.deleted = 0")
    long countByPermissionId(@Param("permissionId") Long permissionId);

    // ================================
    // 权限检查方法
    // ================================

    /**
     * 检查角色是否拥有指定权限
     */
    @Query("SELECT CASE WHEN COUNT(rp) > 0 THEN true ELSE false END FROM RolePermission rp WHERE rp.roleId = :roleId AND rp.permissionId = :permissionId AND rp.deleted = 0")
    boolean hasPermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    /**
     * 检查用户是否拥有指定权限（通过角色）
     */
    @Query("SELECT CASE WHEN COUNT(rp) > 0 THEN true ELSE false END FROM RolePermission rp " +
           "LEFT JOIN rp.role r " +
           "LEFT JOIN r.userRoles ur " +
           "WHERE ur.userId = :userId AND rp.permissionId = :permissionId AND rp.deleted = 0")
    boolean userHasPermission(@Param("userId") Long userId, @Param("permissionId") Long permissionId);

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查角色权限关联是否存在
     */
    @Query("SELECT CASE WHEN COUNT(rp) > 0 THEN true ELSE false END FROM RolePermission rp WHERE rp.roleId = :roleId AND rp.permissionId = :permissionId AND rp.deleted = 0")
    boolean existsByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    // ================================
    // 批量操作方法
    // ================================

    /**
     * 批量删除角色的所有权限
     */
    @Modifying
    @Query("UPDATE RolePermission rp SET rp.deleted = 1, rp.updatedAt = CURRENT_TIMESTAMP WHERE rp.roleId = :roleId")
    int deleteAllByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量删除权限的所有角色关联
     */
    @Modifying
    @Query("UPDATE RolePermission rp SET rp.deleted = 1, rp.updatedAt = CURRENT_TIMESTAMP WHERE rp.permissionId = :permissionId")
    int deleteAllByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 批量删除指定的角色权限关联
     */
    @Modifying
    @Query("UPDATE RolePermission rp SET rp.deleted = 1, rp.updatedAt = CURRENT_TIMESTAMP WHERE rp.roleId = :roleId AND rp.permissionId IN :permissionIds")
    int batchDeleteByRoleIdAndPermissionIds(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);

    // ================================
    // 权限管理方法
    // ================================

    /**
     * 获取角色的所有权限ID
     */
    @Query("SELECT rp.permissionId FROM RolePermission rp WHERE rp.roleId = :roleId AND rp.deleted = 0")
    List<Long> findPermissionIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 获取权限的所有角色ID
     */
    @Query("SELECT rp.roleId FROM RolePermission rp WHERE rp.permissionId = :permissionId AND rp.deleted = 0")
    List<Long> findRoleIdsByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 获取用户的所有权限ID（通过角色）
     */
    @Query("SELECT DISTINCT rp.permissionId FROM RolePermission rp " +
           "LEFT JOIN rp.role r " +
           "LEFT JOIN r.userRoles ur " +
           "WHERE ur.userId = :userId AND rp.deleted = 0")
    List<Long> findPermissionIdsByUserId(@Param("userId") Long userId);

    // ================================
    // 兼容性方法（为现有Service提供支持）
    // ================================

    /**
     * 根据角色ID删除所有权限关联（兼容性方法）
     */
    @Modifying
    @Transactional
    default void deleteByRoleId(Long roleId) {
        deleteAllByRoleId(roleId);
    }

    /**
     * 根据权限ID删除所有角色关联（兼容性方法）
     */
    @Modifying
    @Transactional
    default void deleteByPermissionId(Long permissionId) {
        deleteAllByPermissionId(permissionId);
    }

    /**
     * 根据角色ID和权限ID删除关联（兼容性方法）
     */
    @Modifying
    @Transactional
    @Query("UPDATE RolePermission rp SET rp.deleted = 1, rp.updatedAt = CURRENT_TIMESTAMP WHERE rp.roleId = :roleId AND rp.permissionId = :permissionId")
    void deleteByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    /**
     * 统计权限下的角色数量（兼容性方法）
     */
    default long countRolesByPermissionId(Long permissionId) {
        return countByPermissionId(permissionId);
    }

    /**
     * 统计角色的权限数量（兼容性方法）
     */
    default long countPermissionsByRoleId(Long roleId) {
        return countByRoleId(roleId);
    }

    /**
     * 批量插入角色权限关联（兼容性方法）
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO tb_role_permission (role_id, permission_id, created_at, updated_at, deleted, status) " +
                   "VALUES (:roleId, :permissionId, NOW(), NOW(), 0, 1)", nativeQuery = true)
    void insertRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

}
