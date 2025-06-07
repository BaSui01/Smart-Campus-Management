package com.campus.application.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.campus.application.dto.UserDTO;
import com.campus.domain.entity.User;

/**
 * 用户服务接口
 * 定义用户管理的核心业务方法
 *
 * @author campus
 * @since 2025-06-04
 */
public interface UserService {

    /**
     * 分页查询所有用户
     */
    Page<User> findAllUsers(Pageable pageable);

    /**
     * 根据ID查找用户
     */
    User findUserById(Long id);

    /**
     * 根据用户名查找用户（返回Optional）
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据ID获取用户（兼容方法）
     */
    User getUserById(Long id);

    /**
     * 获取包含角色信息的用户DTO
     */
    UserDTO getUserDTOWithRole(Long userId);

    /**
     * 用户认证
     */
    User authenticate(String username, String password);

    /**
     * 更新最后登录信息
     */
    void updateLastLoginInfo(Long userId, String ipAddress);

    /**
     * 检查用户是否有指定角色
     */
    boolean hasRole(Long userId, String roleName);

    /**
     * 获取用户的所有角色
     */
    List<String> getUserRoles(Long userId);

    /**
     * 检查用户是否有权限访问指定菜单
     */
    boolean hasMenuPermission(Long userId, String menuPath);

    /**
     * 创建新用户
     */
    User createUser(User user);

    /**
     * 更新用户信息
     */
    User updateUser(Long id, User userDetails);

    /**
     * 删除用户（软删除）
     */
    boolean deleteUser(Long id);

    /**
     * 重置用户密码
     */
    void resetPassword(Long userId, String newPassword);

    /**
     * 重置用户密码为默认密码
     */
    boolean resetPassword(Long userId);

    /**
     * 修改用户密码（返回boolean）
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 切换用户状态
     */
    boolean toggleUserStatus(Long userId);

    /**
     * 获取用户统计信息
     */
    UserStatistics getUserStatistics();

    /**
     * 根据角色名查找用户
     */
    List<User> findUsersByRole(String roleName);

    /**
     * 统计指定角色的用户数量
     */
    long countUsersByRole(String roleName);

    /**
     * 记录用户登录日志
     */
    void recordLoginLog(Long userId, String ipAddress, String userAgent);

    /**
     * 记录用户登出日志
     */
    void recordLogoutLog(Long userId);

    /**
     * 搜索用户
     */
    List<User> searchUsers(String keyword);

    /**
     * 根据状态分页查询用户
     */
    Page<User> findUsersByStatus(Integer status, Pageable pageable);

    /**
     * 分页查询用户列表
     */
    Page<User> findUsersByPage(Pageable pageable, Map<String, Object> params);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 统计总用户数
     */
    long countTotalUsers();

    /**
     * 统计活跃用户数
     */
    long countActiveUsers();

    /**
     * 统计非活跃用户数
     */
    long countInactiveUsers();

    /**
     * 导出用户列表
     */
    List<User> exportUsers(Map<String, Object> params);

    /**
     * 根据ID查找用户
     */
    User findById(Long userId);

    /**
     * 根据ID查找用户（返回Optional）
     */
    Optional<User> findByIdOptional(Long userId);

    /**
     * 保存用户
     */
    User save(User user);

    /**
     * 根据ID删除用户
     */
    void deleteById(Long userId);

    /**
     * 更新用户状态
     */
    void updateUserStatus(Long userId, int status);

    /**
     * 创建用户
     */
    User createUser(Map<String, Object> userData);

    /**
     * 更新用户信息
     */
    User updateUser(Long userId, Map<String, Object> userData);

    /**
     * 更新用户信息（直接传入User对象）
     */
    boolean updateUser(User user);

    /**
     * 验证用户密码
     */
    boolean validatePassword(Long userId, String password);

    /**
     * 更新用户密码（返回boolean）
     */
    boolean updatePassword(Long userId, String newPassword);

    /**
     * 根据邮箱查找用户（返回Optional）
     */
    Optional<User> findByEmail(String email);

    /**
     * 批量删除用户
     */
    boolean batchDeleteUsers(List<Long> userIds);

    /**
     * 重置用户状态
     */
    boolean resetUserStatus(Long userId);

    /**
     * 分配角色给用户
     */
    boolean assignRoleToUser(Long userId, Long roleId);

    /**
     * 移除用户角色
     */
    boolean removeRoleFromUser(Long userId, Long roleId);



    /**
     * 用户统计信息类
     */
    class UserStatistics {
        private final long totalUsers;
        private final long activeUsers;
        private final long inactiveUsers;
        private final long todayLogins;

        public UserStatistics(long totalUsers, long activeUsers, long inactiveUsers) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.inactiveUsers = inactiveUsers;
            this.todayLogins = 0; // 默认值，后续可以实现真实的今日登录统计
        }

        public UserStatistics(long totalUsers, long activeUsers, long inactiveUsers, long todayLogins) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.inactiveUsers = inactiveUsers;
            this.todayLogins = todayLogins;
        }

        public long getTotalUsers() {
            return totalUsers;
        }

        public long getActiveUsers() {
            return activeUsers;
        }

        public long getInactiveUsers() {
            return inactiveUsers;
        }

        public long getTodayLogins() {
            return todayLogins;
        }
    }
}
