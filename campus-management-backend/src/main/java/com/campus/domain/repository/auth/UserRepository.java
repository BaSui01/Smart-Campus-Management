package com.campus.domain.repository.auth;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campus.domain.entity.auth.User;
import com.campus.domain.repository.infrastructure.BaseRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户Repository接口
 * 提供用户相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface UserRepository extends BaseRepository<User> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据用户名查找用户
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.deleted = 0")
    Optional<User> findByUsername(@Param("username") String username);

    /**
     * 根据用户名和状态查找用户
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.status = :status AND u.deleted = 0")
    Optional<User> findByUsernameAndStatus(@Param("username") String username, @Param("status") Integer status);

    /**
     * 根据邮箱查找用户
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.deleted = 0")
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * 根据手机号查找用户
     */
    @Query("SELECT u FROM User u WHERE u.phone = :phone AND u.deleted = 0")
    Optional<User> findByPhone(@Param("phone") String phone);

    /**
     * 根据用户名或邮箱查找用户（用于登录）
     */
    @Query("SELECT u FROM User u WHERE (u.username = :loginId OR u.email = :loginId) AND u.deleted = 0")
    Optional<User> findByUsernameOrEmail(@Param("loginId") String loginId);

    /**
     * 根据真实姓名模糊查找用户
     */
    @Query("SELECT u FROM User u WHERE u.realName LIKE %:realName% AND u.deleted = 0")
    List<User> findByRealNameContaining(@Param("realName") String realName);

    /**
     * 分页根据真实姓名模糊查找用户
     */
    @Query("SELECT u FROM User u WHERE u.realName LIKE %:realName% AND u.deleted = 0")
    Page<User> findByRealNameContaining(@Param("realName") String realName, Pageable pageable);

    /**
     * 根据角色查找用户
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.roleKey = :roleKey AND u.deleted = 0")
    List<User> findByRoleKey(@Param("roleKey") String roleKey);

    /**
     * 分页根据角色查找用户
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.roleKey = :roleKey AND u.deleted = 0")
    Page<User> findByRoleKey(@Param("roleKey") String roleKey, Pageable pageable);

    /**
     * 根据性别查找用户
     */
    @Query("SELECT u FROM User u WHERE u.gender = :gender AND u.deleted = 0")
    List<User> findByGender(@Param("gender") String gender);

    /**
     * 根据重置令牌查找用户
     */
    @Query("SELECT u FROM User u WHERE u.resetToken = :token AND u.resetTokenExpire > :now AND u.deleted = 0")
    Optional<User> findByResetToken(@Param("token") String token, @Param("now") LocalDateTime now);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找用户
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN u.userRoles ur LEFT JOIN ur.role r WHERE " +
           "(:username IS NULL OR u.username LIKE %:username%) AND " +
           "(:realName IS NULL OR u.realName LIKE %:realName%) AND " +
           "(:email IS NULL OR u.email LIKE %:email%) AND " +
           "(:roleKey IS NULL OR r.roleKey = :roleKey) AND " +
           "(:gender IS NULL OR u.gender = :gender) AND " +
           "u.deleted = 0")
    Page<User> findByMultipleConditions(@Param("username") String username,
                                       @Param("realName") String realName,
                                       @Param("email") String email,
                                       @Param("roleKey") String roleKey,
                                       @Param("gender") String gender,
                                       Pageable pageable);

    /**
     * 搜索用户（根据关键词匹配用户名、真实姓名、邮箱、手机号）
     */
    @Query("SELECT u FROM User u WHERE " +
           "(u.username LIKE %:keyword% OR " +
           "u.realName LIKE %:keyword% OR " +
           "u.email LIKE %:keyword% OR " +
           "u.phone LIKE %:keyword%) AND " +
           "u.deleted = 0")
    List<User> searchUsers(@Param("keyword") String keyword);

    /**
     * 分页搜索用户
     */
    @Query("SELECT u FROM User u WHERE " +
           "(u.username LIKE %:keyword% OR " +
           "u.realName LIKE %:keyword% OR " +
           "u.email LIKE %:keyword% OR " +
           "u.phone LIKE %:keyword%) AND " +
           "u.deleted = 0")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);

    // ================================
    // 登录相关查询
    // ================================

    /**
     * 查找最近登录的用户
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginTime IS NOT NULL AND u.deleted = 0 ORDER BY u.lastLoginTime DESC")
    List<User> findRecentlyLoggedInUsers(Pageable pageable);

    /**
     * 查找指定时间范围内登录的用户
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginTime BETWEEN :startTime AND :endTime AND u.deleted = 0")
    List<User> findUsersLoggedInBetween(@Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime);

    /**
     * 查找长时间未登录的用户
     */
    @Query("SELECT u FROM User u WHERE (u.lastLoginTime IS NULL OR u.lastLoginTime < :cutoffTime) AND u.deleted = 0")
    List<User> findInactiveUsers(@Param("cutoffTime") LocalDateTime cutoffTime);

    // ================================
    // 角色相关查询
    // ================================

    /**
     * 根据角色键查找用户
     */
    @Query("SELECT u FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.roleKey = :roleKey AND u.deleted = 0")
    List<User> findUsersByRoleKey(@Param("roleKey") String roleKey);

    /**
     * 根据角色名查找用户
     */
    @Query("SELECT u FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.roleName = :roleName AND u.deleted = 0")
    List<User> findUsersByRoleName(@Param("roleName") String roleName);

    /**
     * 查找所有用户并预加载角色信息
     */
    @Query("SELECT u FROM User u WHERE u.deleted = 0 ORDER BY u.id DESC")
    List<User> findAllWithRoles();

    /**
     * 根据删除状态查找用户（分页）
     */
    @Query("SELECT u FROM User u WHERE u.deleted = :deleted ORDER BY u.id DESC")
    Page<User> findByDeletedOrderByIdDesc(@Param("deleted") Integer deleted, Pageable pageable);

    /**
     * 根据删除状态查找用户（列表）
     */
    @Query("SELECT u FROM User u WHERE u.deleted = :deleted ORDER BY u.id DESC")
    List<User> findByDeleted(@Param("deleted") Integer deleted);

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据角色统计用户数量
     */
    @Query("SELECT COUNT(DISTINCT u) FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.roleKey = :roleKey AND u.deleted = 0")
    long countByRoleKey(@Param("roleKey") String roleKey);

    /**
     * 根据性别统计用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.gender = :gender AND u.deleted = 0")
    long countByGender(@Param("gender") String gender);

    /**
     * 统计今日新注册用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE DATE(u.createdAt) = CURRENT_DATE AND u.deleted = 0")
    long countTodayRegistered();

    /**
     * 统计指定时间范围内注册的用户数量
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :startTime AND :endTime AND u.deleted = 0")
    long countRegisteredBetween(@Param("startTime") LocalDateTime startTime,
                               @Param("endTime") LocalDateTime endTime);

    /**
     * 统计活跃用户数量（最近30天内登录过）
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.lastLoginTime > :thirtyDaysAgo AND u.deleted = 0")
    long countActiveUsers(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);

    /**
     * 统计指定角色的用户数量
     */
    @Query("SELECT COUNT(u) FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.roleName = :roleName AND u.deleted = 0")
    long countUsersByRoleName(@Param("roleName") String roleName);

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查用户名是否存在
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username AND u.deleted = 0")
    boolean existsByUsername(@Param("username") String username);

    /**
     * 检查邮箱是否存在
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.deleted = 0")
    boolean existsByEmail(@Param("email") String email);

    /**
     * 检查手机号是否存在
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.phone = :phone AND u.deleted = 0")
    boolean existsByPhone(@Param("phone") String phone);

    /**
     * 检查用户名是否存在（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.username = :username AND u.id != :excludeId AND u.deleted = 0")
    boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("excludeId") Long excludeId);

    /**
     * 检查邮箱是否存在（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.id != :excludeId AND u.deleted = 0")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("excludeId") Long excludeId);

    /**
     * 检查手机号是否存在（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.phone = :phone AND u.id != :excludeId AND u.deleted = 0")
    boolean existsByPhoneAndIdNot(@Param("phone") String phone, @Param("excludeId") Long excludeId);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新用户最后登录信息
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLoginTime = :loginTime, u.lastLoginIp = :loginIp, " +
           "u.loginCount = u.loginCount + 1, u.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE u.id = :userId")
    int updateLastLoginInfo(@Param("userId") Long userId,
                           @Param("loginTime") LocalDateTime loginTime,
                           @Param("loginIp") String loginIp);

    /**
     * 更新用户密码
     */
    @Modifying
    @Query("UPDATE User u SET u.password = :password, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    int updatePassword(@Param("userId") Long userId, @Param("password") String password);

    /**
     * 设置密码重置令牌
     */
    @Modifying
    @Query("UPDATE User u SET u.resetToken = :token, u.resetTokenExpire = :expiry, u.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE u.id = :userId")
    int setResetToken(@Param("userId") Long userId,
                     @Param("token") String token,
                     @Param("expiry") LocalDateTime expiry);

    /**
     * 清除密码重置令牌
     */
    @Modifying
    @Query("UPDATE User u SET u.resetToken = NULL, u.resetTokenExpire = NULL, u.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE u.id = :userId")
    int clearResetToken(@Param("userId") Long userId);

    /**
     * 更新用户头像
     */
    @Modifying
    @Query("UPDATE User u SET u.avatarUrl = :avatarUrl, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    int updateAvatar(@Param("userId") Long userId, @Param("avatarUrl") String avatarUrl);

    /**
     * 批量重置密码
     */
    @Modifying
    @Query("UPDATE User u SET u.password = :password, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id IN :userIds")
    int batchResetPassword(@Param("userIds") List<Long> userIds, @Param("password") String password);

    /**
     * 批量更新用户状态
     */
    @Modifying
    @Query("UPDATE User u SET u.status = :status, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id IN :userIds")
    int batchUpdateUserStatus(@Param("userIds") List<Long> userIds, @Param("status") Integer status);

    /**
     * 清理过期的重置令牌
     */
    @Modifying
    @Query("UPDATE User u SET u.resetToken = NULL, u.resetTokenExpire = NULL, u.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE u.resetTokenExpire < :now")
    int clearExpiredResetTokens(@Param("now") LocalDateTime now);

}
