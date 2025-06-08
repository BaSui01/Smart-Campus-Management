package com.campus.domain.repository;

import com.campus.domain.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 角色Repository接口
 * 提供角色相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface RoleRepository extends BaseRepository<Role> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据角色键查找角色
     */
    @Query("SELECT r FROM Role r WHERE r.roleKey = :roleKey AND r.deleted = 0")
    Optional<Role> findByRoleKey(@Param("roleKey") String roleKey);

    /**
     * 根据角色名称查找角色
     */
    @Query("SELECT r FROM Role r WHERE r.roleName = :roleName AND r.deleted = 0")
    Optional<Role> findByRoleName(@Param("roleName") String roleName);

    /**
     * 根据角色级别查找角色列表
     */
    @Query("SELECT r FROM Role r WHERE r.roleLevel = :roleLevel AND r.deleted = 0 ORDER BY r.sortOrder ASC")
    List<Role> findByRoleLevel(@Param("roleLevel") Integer roleLevel);

    /**
     * 分页根据角色级别查找角色列表
     */
    @Query("SELECT r FROM Role r WHERE r.roleLevel = :roleLevel AND r.deleted = 0")
    Page<Role> findByRoleLevel(@Param("roleLevel") Integer roleLevel, Pageable pageable);

    /**
     * 根据角色名称模糊查询
     */
    @Query("SELECT r FROM Role r WHERE r.roleName LIKE %:roleName% AND r.deleted = 0 ORDER BY r.sortOrder ASC")
    List<Role> findByRoleNameContaining(@Param("roleName") String roleName);

    /**
     * 分页根据角色名称模糊查询
     */
    @Query("SELECT r FROM Role r WHERE r.roleName LIKE %:roleName% AND r.deleted = 0")
    Page<Role> findByRoleNameContaining(@Param("roleName") String roleName, Pageable pageable);

    /**
     * 获取所有启用的角色（按排序字段排序）
     */
    @Query("SELECT r FROM Role r WHERE r.status = 1 AND r.deleted = 0 ORDER BY r.sortOrder ASC")
    List<Role> findAllActiveRoles();

    /**
     * 分页获取所有启用的角色
     */
    @Query("SELECT r FROM Role r WHERE r.status = 1 AND r.deleted = 0")
    Page<Role> findAllActiveRoles(Pageable pageable);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找角色
     */
    @Query("SELECT r FROM Role r WHERE " +
           "(:roleName IS NULL OR r.roleName LIKE %:roleName%) AND " +
           "(:roleKey IS NULL OR r.roleKey LIKE %:roleKey%) AND " +
           "(:roleLevel IS NULL OR r.roleLevel = :roleLevel) AND " +
           "r.deleted = 0")
    Page<Role> findByMultipleConditions(@Param("roleName") String roleName,
                                       @Param("roleKey") String roleKey,
                                       @Param("roleLevel") Integer roleLevel,
                                       Pageable pageable);

    /**
     * 搜索角色（根据角色名称、角色键、描述）
     */
    @Query("SELECT r FROM Role r WHERE " +
           "(r.roleName LIKE %:keyword% OR " +
           "r.roleKey LIKE %:keyword% OR " +
           "r.description LIKE %:keyword%) AND " +
           "r.deleted = 0 ORDER BY r.sortOrder ASC")
    List<Role> searchRoles(@Param("keyword") String keyword);

    /**
     * 分页搜索角色
     */
    @Query("SELECT r FROM Role r WHERE " +
           "(r.roleName LIKE %:keyword% OR " +
           "r.roleKey LIKE %:keyword% OR " +
           "r.description LIKE %:keyword%) AND " +
           "r.deleted = 0")
    Page<Role> searchRoles(@Param("keyword") String keyword, Pageable pageable);

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 根据用户ID查找角色
     */
    @Query("SELECT r FROM Role r JOIN r.userRoles ur WHERE ur.userId = :userId AND r.deleted = 0 ORDER BY r.sortOrder ASC")
    List<Role> findRolesByUserId(@Param("userId") Long userId);

    /**
     * 查找角色并预加载权限信息
     */
    @Query("SELECT DISTINCT r FROM Role r LEFT JOIN FETCH r.rolePermissions rp LEFT JOIN FETCH rp.permission WHERE r.deleted = 0 ORDER BY r.sortOrder ASC")
    List<Role> findAllWithPermissions();

    /**
     * 根据权限ID查找角色
     */
    @Query("SELECT r FROM Role r JOIN r.rolePermissions rp WHERE rp.permissionId = :permissionId AND r.deleted = 0 ORDER BY r.sortOrder ASC")
    List<Role> findRolesByPermissionId(@Param("permissionId") Long permissionId);

    // ================================
    // 统计查询方法
    // ================================



    /**
     * 统计角色数量按状态
     */
    @Query("SELECT r.status, COUNT(r) FROM Role r WHERE r.deleted = 0 GROUP BY r.status")
    List<Object[]> countRolesByStatus();

    /**
     * 统计拥有指定权限的角色数量
     */
    @Query("SELECT COUNT(DISTINCT r) FROM Role r JOIN r.rolePermissions rp WHERE rp.permissionId = :permissionId AND r.deleted = 0")
    long countRolesByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 统计指定角色的用户数量
     */
    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.roleId = :roleId AND ur.deleted = 0")
    long countUsersByRoleId(@Param("roleId") Long roleId);

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查角色键是否存在
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Role r WHERE r.roleKey = :roleKey AND r.deleted = 0")
    boolean existsByRoleKey(@Param("roleKey") String roleKey);

    /**
     * 检查角色键是否存在（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Role r WHERE r.roleKey = :roleKey AND r.id != :excludeId AND r.deleted = 0")
    boolean existsByRoleKeyAndIdNot(@Param("roleKey") String roleKey, @Param("excludeId") Long excludeId);

    /**
     * 检查角色名称是否存在
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Role r WHERE r.roleName = :roleName AND r.deleted = 0")
    boolean existsByRoleName(@Param("roleName") String roleName);

    /**
     * 检查角色名称是否存在（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Role r WHERE r.roleName = :roleName AND r.id != :excludeId AND r.deleted = 0")
    boolean existsByRoleNameAndIdNot(@Param("roleName") String roleName, @Param("excludeId") Long excludeId);

    /**
     * 检查角色代码是否存在
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Role r WHERE r.roleCode = :roleCode AND r.deleted = 0")
    boolean existsByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 根据角色代码查找角色
     */
    @Query("SELECT r FROM Role r WHERE r.roleCode = :roleCode AND r.deleted = 0")
    Role findByRoleCode(@Param("roleCode") String roleCode);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新角色排序
     */
    @Modifying
    @Query("UPDATE Role r SET r.sortOrder = :sortOrder, r.updatedAt = CURRENT_TIMESTAMP WHERE r.id = :roleId")
    int updateSortOrder(@Param("roleId") Long roleId, @Param("sortOrder") Integer sortOrder);

    /**
     * 批量更新角色排序
     */
    @Modifying
    @Query("UPDATE Role r SET r.sortOrder = :sortOrder, r.updatedAt = CURRENT_TIMESTAMP WHERE r.id IN :roleIds")
    int batchUpdateSortOrder(@Param("roleIds") List<Long> roleIds, @Param("sortOrder") Integer sortOrder);



}
