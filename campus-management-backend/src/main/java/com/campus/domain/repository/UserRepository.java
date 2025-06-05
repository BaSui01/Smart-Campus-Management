package com.campus.domain.repository;

import com.campus.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
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
     * 根据用户名和密码查找用户
     */
    Optional<User> findByUsernameAndPassword(String username, String password);

    /**
     * 根据状态查找用户列表
     */
    List<User> findByStatus(Integer status);

    /**
     * 根据状态分页查找用户
     */
    Page<User> findByStatus(Integer status, Pageable pageable);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 根据真实姓名模糊查询
     */
    List<User> findByRealNameContaining(String realName);

    /**
     * 根据手机号查找用户
     */
    Optional<User> findByPhone(String phone);

    /**
     * 搜索用户（根据用户名、真实姓名、邮箱、手机号）
     */
    @Query("SELECT u FROM User u WHERE " +
           "u.username LIKE %:keyword% OR " +
           "u.realName LIKE %:keyword% OR " +
           "u.email LIKE %:keyword% OR " +
           "u.phone LIKE %:keyword%")
    List<User> searchUsers(@Param("keyword") String keyword);

    /**
     * 根据多个条件分页查询用户
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:username IS NULL OR u.username LIKE %:username%) AND " +
           "(:realName IS NULL OR u.realName LIKE %:realName%) AND " +
           "(:email IS NULL OR u.email LIKE %:email%) AND " +
           "(:status IS NULL OR u.status = :status)")
    Page<User> findUsersByConditions(@Param("username") String username,
                                   @Param("realName") String realName,
                                   @Param("email") String email,
                                   @Param("status") Integer status,
                                   Pageable pageable);

    /**
     * 统计用户数量按状态
     */
    @Query("SELECT u.status, COUNT(u) FROM User u GROUP BY u.status")
    List<Object[]> countUsersByStatus();

    /**
     * 获取最近注册的用户
     */
    @Query("SELECT u FROM User u ORDER BY u.createdTime DESC")
    List<User> findRecentUsers(Pageable pageable);

    /**
     * 根据角色查找用户
     */
    @Query("SELECT u FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.roleKey = :roleKey")
    List<User> findUsersByRoleKey(@Param("roleKey") String roleKey);

    /**
     * 统计总用户数
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.deleted = 0")
    long countActiveUsers();

    /**
     * 统计今日新增用户数
     */
    @Query("SELECT COUNT(u) FROM User u WHERE DATE(u.createdTime) = CURRENT_DATE")
    long countTodayNewUsers();

    /**
     * 根据状态统计用户数量
     */
    long countByStatus(Integer status);

    /**
     * 根据角色名查找用户
     */
    @Query("SELECT u FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.roleName = :roleName OR r.roleKey = :roleName")
    List<User> findUsersByRoleName(@Param("roleName") String roleName);

    /**
     * 统计指定角色的用户数量
     */
    @Query("SELECT COUNT(u) FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.roleName = :roleName OR r.roleKey = :roleName")
    long countUsersByRoleName(@Param("roleName") String roleName);

    /**
     * 复合搜索用户
     */
    @Query("SELECT u FROM User u WHERE " +
           "u.username LIKE %:keyword% OR " +
           "u.realName LIKE %:keyword% OR " +
           "u.email LIKE %:keyword%")
    List<User> findByUsernameContainingOrRealNameContainingOrEmailContaining(@Param("keyword") String keyword1, @Param("keyword") String keyword2, @Param("keyword") String keyword3);
}
