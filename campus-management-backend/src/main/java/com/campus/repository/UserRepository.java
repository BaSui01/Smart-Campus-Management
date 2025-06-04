package com.campus.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campus.entity.User;

/**
 * 用户数据访问接口
 * 提供用户相关的数据库操作方法
 *
 * @author campus
 * @since 2025-06-04
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据用户名和状态查找用户
     */
    Optional<User> findByUsernameAndStatus(String username, Integer status);

    /**
     * 根据状态查找用户
     */
    Page<User> findByStatus(Integer status, Pageable pageable);

    /**
     * 根据状态统计用户数量
     */
    long countByStatus(Integer status);

    /**
     * 搜索用户（按用户名、真实姓名、邮箱）
     */
    List<User> findByUsernameContainingOrRealNameContainingOrEmailContaining(
            String username, String realName, String email);

    /**
     * 根据角色名查找用户
     */
    @Query("SELECT DISTINCT u FROM User u " +
           "JOIN u.userRoles ur " +
           "JOIN ur.role r " +
           "WHERE r.roleName = :roleName AND u.status = 1")
    List<User> findUsersByRoleName(@Param("roleName") String roleName);

    /**
     * 统计各状态用户数量
     */
    @Query("SELECT u.status as status, COUNT(u) as count FROM User u GROUP BY u.status")
    List<UserStatusCount> countUsersByStatus();

    /**
     * 根据创建时间范围查找用户
     */
    List<User> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据最后登录时间查找用户
     */
    List<User> findByLastLoginTimeBefore(LocalDateTime beforeTime);

    /**
     * 检查用户名是否存在（排除指定ID）
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username AND u.id != :excludeId")
    boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("excludeId") Long excludeId);

    /**
     * 检查邮箱是否存在（排除指定ID）
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.id != :excludeId")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("excludeId") Long excludeId);

    /**
     * 用户状态统计投影接口
     */
    interface UserStatusCount {
        Integer getStatus();
        Long getCount();
    }
}
