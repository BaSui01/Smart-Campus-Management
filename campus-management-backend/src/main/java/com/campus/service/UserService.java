package com.campus.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.campus.entity.User;

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
     * 根据用户名查找用户
     */
    User findByUsername(String username);

    /**
     * 根据ID获取用户（兼容方法）
     */
    User getUserById(Long id);

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
    void deleteUser(Long id);

    /**
     * 重置用户密码
     */
    void resetPassword(Long userId, String newPassword);

    /**
     * 重置用户密码为默认密码
     */
    void resetPassword(Long userId);

    /**
     * 修改用户密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 切换用户状态
     */
    void toggleUserStatus(Long userId);

    /**
     * 获取用户统计信息
     */
    UserStatistics getUserStatistics();

    /**
     * 根据角色名查找用户
     */
    List<User> findUsersByRole(String roleName);

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
     * 用户统计信息类
     */
    class UserStatistics {
        private final long totalUsers;
        private final long activeUsers;
        private final long inactiveUsers;

        public UserStatistics(long totalUsers, long activeUsers, long inactiveUsers) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.inactiveUsers = inactiveUsers;
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
    }
}
