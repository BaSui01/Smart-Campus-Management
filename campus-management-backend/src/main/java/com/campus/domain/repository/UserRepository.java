package com.campus.domain.repository;

import com.campus.domain.entity.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据手机号查找用户
     */
    Optional<User> findByPhone(String phone);

    /**
     * 根据用户名或邮箱查找用户
     */
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);

    // ================================
    // 状态查询方法
    // ================================

    /**
     * 查找所有启用的用户
     */
    List<User> findByStatusAndDeleted(Integer status, Integer deleted);

    /**
     * 分页查找启用的用户
     */
    Page<User> findByStatusAndDeleted(Integer status, Integer deleted, Pageable pageable);

    /**
     * 根据角色查找用户
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :roleName AND u.status = 1 AND u.deleted = 0")
    List<User> findByRoleName(@Param("roleName") String roleName);

    /**
     * 根据角色分页查找用户
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :roleName AND u.status = 1 AND u.deleted = 0")
    Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);

    // ================================
    // 时间范围查询方法
    // ================================

    /**
     * 查找指定时间范围内创建的用户
     */
    List<User> findByCreatedAtBetweenAndDeleted(LocalDateTime startTime, LocalDateTime endTime, Integer deleted);

    /**
     * 查找指定时间范围内最后登录的用户
     */
    List<User> findByLastLoginTimeBetweenAndDeleted(LocalDateTime startTime, LocalDateTime endTime, Integer deleted);

    /**
     * 查找今日新注册用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE DATE(u.createdAt) = CURRENT_DATE AND u.deleted = 0")
    Long countTodayNewUsers();

    /**
     * 查找在线用户数量（最近30分钟内有活动）
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.lastLoginTime >= :since AND u.deleted = 0")
    Long countOnlineUsers(@Param("since") LocalDateTime since);

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 统计用户总数（不包括已删除）
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.deleted = 0")
    Long countActiveUsers();

    /**
     * 统计启用用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 1 AND u.deleted = 0")
    Long countEnabledUsers();

    /**
     * 统计各状态用户数量
     */
    @Query("SELECT u.status, COUNT(u) FROM User u WHERE u.deleted = 0 GROUP BY u.status")
    List<Object[]> countUsersByStatus();

    /**
     * 统计各角色用户数量
     */
    @Query("SELECT r.roleName, COUNT(u) FROM User u JOIN u.roles r WHERE u.deleted = 0 GROUP BY r.roleName")
    List<Object[]> countUsersByRole();

    // ================================
    // 搜索查询方法
    // ================================

    /**
     * 根据关键字搜索用户（用户名、真实姓名、邮箱、手机号）
     */
    @Query("SELECT u FROM User u WHERE " +
           "(u.username LIKE %:keyword% OR " +
           "u.realName LIKE %:keyword% OR " +
           "u.email LIKE %:keyword% OR " +
           "u.phone LIKE %:keyword%) AND " +
           "u.deleted = 0")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据多个条件搜索用户
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:username IS NULL OR u.username LIKE %:username%) AND " +
           "(:realName IS NULL OR u.realName LIKE %:realName%) AND " +
           "(:email IS NULL OR u.email LIKE %:email%) AND " +
           "(:phone IS NULL OR u.phone LIKE %:phone%) AND " +
           "(:status IS NULL OR u.status = :status) AND " +
           "u.deleted = 0")
    Page<User> findByConditions(@Param("username") String username,
                               @Param("realName") String realName,
                               @Param("email") String email,
                               @Param("phone") String phone,
                               @Param("status") Integer status,
                               Pageable pageable);

    // ================================
    // 批量操作方法
    // ================================

    /**
     * 批量更新用户状态
     */
    @Query("UPDATE User u SET u.status = :status, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id IN :ids")
    int updateStatusByIds(@Param("status") Integer status, @Param("ids") List<Long> ids);

    /**
     * 批量软删除用户
     */
    @Query("UPDATE User u SET u.deleted = 1, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id IN :ids")
    int softDeleteByIds(@Param("ids") List<Long> ids);

    /**
     * 批量恢复用户
     */
    @Query("UPDATE User u SET u.deleted = 0, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id IN :ids")
    int restoreByIds(@Param("ids") List<Long> ids);

    // ================================
    // 安全相关方法
    // ================================

    /**
     * 更新用户最后登录时间和IP
     */
    @Query("UPDATE User u SET u.lastLoginTime = :loginTime, u.lastLoginIp = :loginIp, " +
           "u.loginCount = u.loginCount + 1, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    int updateLoginInfo(@Param("userId") Long userId,
                       @Param("loginTime") LocalDateTime loginTime,
                       @Param("loginIp") String loginIp);

    /**
     * 重置用户密码
     */
    @Query("UPDATE User u SET u.password = :password, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    int updatePassword(@Param("userId") Long userId, @Param("password") String password);

    /**
     * 查找密码过期的用户
     */
    @Query("SELECT u FROM User u WHERE u.passwordExpireTime < CURRENT_TIMESTAMP AND u.deleted = 0")
    List<User> findUsersWithExpiredPassword();

    /**
     * 查找长时间未登录的用户
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginTime < :threshold AND u.deleted = 0")
    List<User> findInactiveUsers(@Param("threshold") LocalDateTime threshold);

    // ================================
    // 数据导出方法
    // ================================

    /**
     * 查找所有用户用于导出（不包括敏感信息）
     */
    @Query("SELECT new User(u.id, u.username, u.realName, u.email, u.phone, u.gender, u.status, u.createdAt) " +
           "FROM User u WHERE u.deleted = 0 ORDER BY u.createdAt DESC")
    List<User> findAllForExport();

    /**
     * 根据条件查找用户用于导出
     */
    @Query("SELECT new User(u.id, u.username, u.realName, u.email, u.phone, u.gender, u.status, u.createdAt) " +
           "FROM User u WHERE " +
           "(:status IS NULL OR u.status = :status) AND " +
           "(:startTime IS NULL OR u.createdAt >= :startTime) AND " +
           "(:endTime IS NULL OR u.createdAt <= :endTime) AND " +
           "u.deleted = 0 ORDER BY u.createdAt DESC")
    List<User> findByConditionsForExport(@Param("status") Integer status,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);
}
