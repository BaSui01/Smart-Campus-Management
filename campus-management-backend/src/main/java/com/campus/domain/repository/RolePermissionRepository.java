package com.campus.domain.repository;

import com.campus.domain.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 角色权限关联仓储接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    /**
     * 根据角色ID查找角色权限关联
     */
    List<RolePermission> findByRoleId(Long roleId);

    /**
     * 根据权限ID查找角色权限关联
     */
    List<RolePermission> findByPermissionId(Long permissionId);

    /**
     * 根据角色ID和权限ID查找角色权限关联
     */
    Optional<RolePermission> findByRoleIdAndPermissionId(Long roleId, Long permissionId);

    /**
     * 检查角色是否有指定权限
     */
    boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);

    /**
     * 根据角色ID删除所有权限关联
     */
    @Modifying
    @Transactional
    void deleteByRoleId(Long roleId);

    /**
     * 根据权限ID删除所有角色关联
     */
    @Modifying
    @Transactional
    void deleteByPermissionId(Long permissionId);

    /**
     * 根据角色ID和权限ID删除关联
     */
    @Modifying
    @Transactional
    void deleteByRoleIdAndPermissionId(Long roleId, Long permissionId);

    /**
     * 统计权限下的角色数量
     */
    @Query("SELECT COUNT(rp) FROM RolePermission rp WHERE rp.permissionId = :permissionId")
    long countRolesByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 统计角色的权限数量
     */
    @Query("SELECT COUNT(rp) FROM RolePermission rp WHERE rp.roleId = :roleId")
    long countPermissionsByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色权限关联
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO tb_role_permission (role_id, permission_id, created_time, updated_time) " +
                   "VALUES (:roleId, :permissionId, NOW(), NOW())", nativeQuery = true)
    void insertRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
}
