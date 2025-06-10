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
     * 分配角色给用户（简化方法）
     */
    void assignRole(Long userId, Long roleId);

    /**
     * 移除用户角色
     */
    boolean removeRoleFromUser(Long userId, Long roleId);

    /**
     * 获取用户权限
     */
    Object getUserPermissions(Long userId);

    /**
     * 获取所有活跃用户
     */
    List<User> findActiveUsers();

    /**
     * 获取所有用户（不分页）
     */
    List<User> findAllUsers();

    /**
     * 获取教师用户列表
     */
    List<User> findTeachers();

    /**
     * 获取学生用户列表
     */
    List<User> findStudents();

    /**
     * 获取所有部门列表
     */
    List<Map<String, Object>> getDepartments();

    // ================================
    // 家长相关方法
    // ================================

    /**
     * 获取家长用户列表
     */
    List<User> findParents();

    /**
     * 根据ID获取家长学生关系
     */
    Map<String, Object> getParentStudentRelationById(Long id);

    /**
     * 根据家长ID获取关系列表
     */
    List<Map<String, Object>> getRelationsByParent(Long parentId);

    /**
     * 根据学生ID获取关系列表
     */
    List<Map<String, Object>> getRelationsByStudent(Long studentId);

    /**
     * 获取家长学生关系统计
     */
    Map<String, Object> getParentStudentRelationStatistics();

    /**
     * 按类型统计家长学生关系
     */
    Map<String, Object> countParentStudentRelationsByType();

    // ================================
    // 消息管理页面需要的方法
    // ================================

    /**
     * 获取用户组列表
     */
    List<Map<String, Object>> getUserGroups();

    /**
     * 获取所有角色列表
     */
    List<Map<String, Object>> getAllRoles();

    // ================================
    // ParentStudentRelationApiController 需要的方法
    // ================================

    /**
     * 创建家长学生关系
     */
    com.campus.domain.entity.ParentStudentRelation createParentStudentRelation(com.campus.domain.entity.ParentStudentRelation relation);

    /**
     * 更新家长学生关系
     */
    com.campus.domain.entity.ParentStudentRelation updateParentStudentRelation(com.campus.domain.entity.ParentStudentRelation relation);

    /**
     * 删除家长学生关系
     */
    void deleteParentStudentRelation(Long relationId);

    /**
     * 分页查找家长学生关系
     */
    Page<com.campus.domain.entity.ParentStudentRelation> findParentStudentRelations(Pageable pageable, Long parentId, Long studentId, String relationType);

    /**
     * 根据关系类型获取关系列表
     */
    List<com.campus.domain.entity.ParentStudentRelation> getRelationsByType(String relationType);

    /**
     * 验证家长学生关系
     */
    boolean verifyParentStudentRelation(Long parentId, Long studentId);

    /**
     * 验证家长学生关系数据
     */
    Map<String, Object> validateParentStudentRelation(com.campus.domain.entity.ParentStudentRelation relation);

    /**
     * 批量创建家长学生关系
     */
    Map<String, Object> batchCreateParentStudentRelations(List<com.campus.domain.entity.ParentStudentRelation> relations);

    /**
     * 批量删除家长学生关系
     */
    void batchDeleteParentStudentRelations(List<Long> relationIds);

    /**
     * 导入家长学生关系
     */
    Map<String, Object> importParentStudentRelations(List<com.campus.domain.entity.ParentStudentRelation> relations);

    /**
     * 导出家长学生关系
     */
    List<com.campus.domain.entity.ParentStudentRelation> exportParentStudentRelations(Long parentId, Long studentId, String relationType);

    /**
     * 激活家长学生关系
     */
    void activateParentStudentRelation(Long relationId);

    /**
     * 停用家长学生关系
     */
    void deactivateParentStudentRelation(Long relationId);

    /**
     * 获取孤儿学生列表
     */
    List<Map<String, Object>> getOrphanedStudents();

    /**
     * 获取无子女家长列表
     */
    List<Map<String, Object>> getChildlessParents();

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
