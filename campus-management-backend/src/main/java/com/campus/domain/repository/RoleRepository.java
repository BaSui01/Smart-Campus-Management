package com.campus.domain.repository;

import com.campus.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 角色仓储接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * 根据角色键查找角色
     */
    Optional<Role> findByRoleKey(String roleKey);

    /**
     * 根据角色名称查找角色
     */
    Optional<Role> findByRoleName(String roleName);

    /**
     * 根据状态查找角色列表
     */
    List<Role> findByStatus(Integer status);

    /**
     * 根据状态查找角色列表（按排序字段排序）
     */
    List<Role> findByStatusOrderBySortOrder(Integer status);

    /**
     * 检查角色键是否存在
     */
    boolean existsByRoleKey(String roleKey);

    /**
     * 检查角色名称是否存在
     */
    boolean existsByRoleName(String roleName);

    /**
     * 根据角色名称模糊查询
     */
    List<Role> findByRoleNameContaining(String roleName);

    /**
     * 搜索角色（根据角色名称、角色键、描述）
     */
    @Query("SELECT r FROM Role r WHERE " +
           "r.roleName LIKE %:keyword% OR " +
           "r.roleKey LIKE %:keyword% OR " +
           "r.description LIKE %:keyword%")
    List<Role> searchRoles(@Param("keyword") String keyword);

    /**
     * 获取所有启用的角色（按排序字段排序）
     */
    @Query("SELECT r FROM Role r WHERE r.status = 1 AND r.deleted = 0 ORDER BY r.sortOrder ASC")
    List<Role> findAllActiveRoles();

    /**
     * 根据用户ID查找角色
     */
    @Query("SELECT r FROM Role r JOIN r.userRoles ur WHERE ur.userId = :userId")
    List<Role> findRolesByUserId(@Param("userId") Long userId);

    /**
     * 统计角色数量按状态
     */
    @Query("SELECT r.status, COUNT(r) FROM Role r GROUP BY r.status")
    List<Object[]> countRolesByStatus();
}
