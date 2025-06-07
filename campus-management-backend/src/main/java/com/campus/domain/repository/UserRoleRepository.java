package com.campus.domain.repository;

import com.campus.domain.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 用户角色关联Repository接口
 * 提供用户角色关联相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface UserRoleRepository extends BaseRepository<UserRole> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据用户ID查找角色关联
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.userId = :userId AND ur.deleted = 0")
    List<UserRole> findByUserId(@Param("userId") Long userId);

    /**
     * 分页根据用户ID查找角色关联
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.userId = :userId AND ur.deleted = 0")
    Page<UserRole> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 根据角色ID查找用户关联
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.roleId = :roleId AND ur.deleted = 0")
    List<UserRole> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 分页根据角色ID查找用户关联
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.roleId = :roleId AND ur.deleted = 0")
    Page<UserRole> findByRoleId(@Param("roleId") Long roleId, Pageable pageable);

    /**
     * 根据用户ID和角色ID查找关联
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.userId = :userId AND ur.roleId = :roleId AND ur.deleted = 0")
    Optional<UserRole> findByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 查找用户角色关联并预加载用户信息
     */
    @Query("SELECT DISTINCT ur FROM UserRole ur LEFT JOIN FETCH ur.user WHERE ur.deleted = 0")
    List<UserRole> findAllWithUser();

    /**
     * 查找用户角色关联并预加载角色信息
     */
    @Query("SELECT DISTINCT ur FROM UserRole ur LEFT JOIN FETCH ur.role WHERE ur.deleted = 0")
    List<UserRole> findAllWithRole();

    /**
     * 查找用户角色关联并预加载所有关联信息
     */
    @Query("SELECT DISTINCT ur FROM UserRole ur " +
           "LEFT JOIN FETCH ur.user u " +
           "LEFT JOIN FETCH ur.role r " +
           "WHERE ur.deleted = 0")
    List<UserRole> findAllWithAssociations();

    /**
     * 根据用户ID查找关联并预加载角色信息
     */
    @Query("SELECT DISTINCT ur FROM UserRole ur LEFT JOIN FETCH ur.role WHERE ur.userId = :userId AND ur.deleted = 0")
    List<UserRole> findByUserIdWithRole(@Param("userId") Long userId);

    /**
     * 根据角色ID查找关联并预加载用户信息
     */
    @Query("SELECT DISTINCT ur FROM UserRole ur LEFT JOIN FETCH ur.user WHERE ur.roleId = :roleId AND ur.deleted = 0")
    List<UserRole> findByRoleIdWithUser(@Param("roleId") Long roleId);

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据角色统计用户数量
     */
    @Query("SELECT r.roleName, COUNT(ur) FROM UserRole ur LEFT JOIN ur.role r WHERE ur.deleted = 0 GROUP BY ur.roleId, r.roleName ORDER BY COUNT(ur) DESC")
    List<Object[]> countByRole();

    /**
     * 根据用户统计角色数量
     */
    @Query("SELECT u.username, COUNT(ur) FROM UserRole ur LEFT JOIN ur.user u WHERE ur.deleted = 0 GROUP BY ur.userId, u.username ORDER BY COUNT(ur) DESC")
    List<Object[]> countByUser();

    /**
     * 统计指定用户的角色数量
     */
    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.userId = :userId AND ur.deleted = 0")
    long countByUserId(@Param("userId") Long userId);

    /**
     * 统计指定角色的用户数量
     */
    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.roleId = :roleId AND ur.deleted = 0")
    long countByRoleId(@Param("roleId") Long roleId);

    // ================================
    // 角色检查方法
    // ================================

    /**
     * 检查用户是否拥有指定角色
     */
    @Query("SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END FROM UserRole ur WHERE ur.userId = :userId AND ur.roleId = :roleId AND ur.deleted = 0")
    boolean hasRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 检查用户是否拥有指定角色键的角色
     */
    @Query("SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END FROM UserRole ur " +
           "LEFT JOIN ur.role r WHERE ur.userId = :userId AND r.roleKey = :roleKey AND ur.deleted = 0")
    boolean hasRoleByKey(@Param("userId") Long userId, @Param("roleKey") String roleKey);

    /**
     * 检查用户是否拥有管理员角色
     */
    @Query("SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END FROM UserRole ur " +
           "LEFT JOIN ur.role r WHERE ur.userId = :userId AND r.roleKey = 'admin' AND ur.deleted = 0")
    boolean isAdmin(@Param("userId") Long userId);

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查用户角色关联是否存在
     */
    @Query("SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END FROM UserRole ur WHERE ur.userId = :userId AND ur.roleId = :roleId AND ur.deleted = 0")
    boolean existsByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    // ================================
    // 批量操作方法
    // ================================

    /**
     * 批量删除用户的所有角色
     */
    @Modifying
    @Query("UPDATE UserRole ur SET ur.deleted = 1, ur.updatedAt = CURRENT_TIMESTAMP WHERE ur.userId = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);

    /**
     * 批量删除角色的所有用户关联
     */
    @Modifying
    @Query("UPDATE UserRole ur SET ur.deleted = 1, ur.updatedAt = CURRENT_TIMESTAMP WHERE ur.roleId = :roleId")
    int deleteAllByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量删除指定的用户角色关联
     */
    @Modifying
    @Query("UPDATE UserRole ur SET ur.deleted = 1, ur.updatedAt = CURRENT_TIMESTAMP WHERE ur.userId = :userId AND ur.roleId IN :roleIds")
    int batchDeleteByUserIdAndRoleIds(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    /**
     * 批量恢复用户角色关联
     */
    @Modifying
    @Query("UPDATE UserRole ur SET ur.deleted = 0, ur.updatedAt = CURRENT_TIMESTAMP WHERE ur.userId = :userId AND ur.roleId IN :roleIds")
    int batchRestoreByUserIdAndRoleIds(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    // ================================
    // 角色管理方法
    // ================================

    /**
     * 获取用户的所有角色ID
     */
    @Query("SELECT ur.roleId FROM UserRole ur WHERE ur.userId = :userId AND ur.deleted = 0")
    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 获取角色的所有用户ID
     */
    @Query("SELECT ur.userId FROM UserRole ur WHERE ur.roleId = :roleId AND ur.deleted = 0")
    List<Long> findUserIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 获取用户的主要角色（第一个角色）
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.userId = :userId AND ur.deleted = 0 ORDER BY ur.createdAt ASC")
    List<UserRole> findPrimaryRoleByUserId(@Param("userId") Long userId);

    /**
     * 获取用户的所有角色键
     */
    @Query("SELECT r.roleKey FROM UserRole ur LEFT JOIN ur.role r WHERE ur.userId = :userId AND ur.deleted = 0")
    List<String> findRoleKeysByUserId(@Param("userId") Long userId);

    // ================================
    // 兼容性方法（为现有Service提供支持）
    // ================================

    /**
     * 根据用户ID删除所有角色关联（兼容性方法）
     */
    @Modifying
    @Transactional
    default void deleteByUserId(Long userId) {
        deleteAllByUserId(userId);
    }

    /**
     * 根据角色ID删除所有用户关联（兼容性方法）
     */
    @Modifying
    @Transactional
    default void deleteByRoleId(Long roleId) {
        deleteAllByRoleId(roleId);
    }

    /**
     * 根据用户ID和角色ID删除关联（兼容性方法）
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserRole ur SET ur.deleted = 1, ur.updatedAt = CURRENT_TIMESTAMP WHERE ur.userId = :userId AND ur.roleId = :roleId")
    void deleteByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 统计角色下的用户数量（兼容性方法）
     */
    default long countUsersByRoleId(Long roleId) {
        return countByRoleId(roleId);
    }

    /**
     * 统计用户的角色数量（兼容性方法）
     */
    default long countRolesByUserId(Long userId) {
        return countByUserId(userId);
    }

}
