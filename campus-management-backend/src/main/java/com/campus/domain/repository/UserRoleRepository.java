package com.campus.domain.repository;

import com.campus.domain.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 用户角色关联仓储接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    /**
     * 根据用户ID查找用户角色关联
     */
    List<UserRole> findByUserId(Long userId);

    /**
     * 根据角色ID查找用户角色关联
     */
    List<UserRole> findByRoleId(Long roleId);

    /**
     * 根据用户ID和角色ID查找用户角色关联
     */
    Optional<UserRole> findByUserIdAndRoleId(Long userId, Long roleId);

    /**
     * 检查用户是否有指定角色
     */
    boolean existsByUserIdAndRoleId(Long userId, Long roleId);

    /**
     * 根据用户ID删除所有角色关联
     */
    @Modifying
    @Transactional
    void deleteByUserId(Long userId);

    /**
     * 根据角色ID删除所有用户关联
     */
    @Modifying
    @Transactional
    void deleteByRoleId(Long roleId);

    /**
     * 根据用户ID和角色ID删除关联
     */
    @Modifying
    @Transactional
    void deleteByUserIdAndRoleId(Long userId, Long roleId);

    /**
     * 统计角色下的用户数量
     */
    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.roleId = :roleId")
    long countUsersByRoleId(@Param("roleId") Long roleId);

    /**
     * 统计用户的角色数量
     */
    @Query("SELECT COUNT(ur) FROM UserRole ur WHERE ur.userId = :userId")
    long countRolesByUserId(@Param("userId") Long userId);

    /**
     * 获取用户的主要角色（第一个角色）
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.userId = :userId ORDER BY ur.createdTime ASC")
    List<UserRole> findPrimaryRoleByUserId(@Param("userId") Long userId);
}
