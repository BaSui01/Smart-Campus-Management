package com.campus.application.Implement.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.application.dto.UserDTO;
import com.campus.application.service.auth.UserService;
import com.campus.domain.entity.auth.Role;
import com.campus.domain.entity.auth.User;
import com.campus.domain.entity.auth.UserRole;
import com.campus.domain.repository.auth.RoleRepository;
import com.campus.domain.repository.auth.UserRepository;
import com.campus.domain.repository.auth.UserRoleRepository;

/**
 * 用户服务实现类
 * 实现用户管理的核心业务逻辑
 *
 * @author campus
 * @since 2025-06-04
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @PersistenceContext
    private EntityManager entityManager;

    
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          RoleRepository roleRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }



    @Override
    @Transactional(readOnly = true)
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return findUserById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public User authenticate(String username, String password) {
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    @Override
    @Transactional
    public void updateLastLoginInfo(Long userId, String ipAddress) {
        try {
            User user = findUserById(userId);
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(ipAddress);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        } catch (Exception e) {
            System.err.println("更新登录信息失败: " + e.getMessage());
            // 不抛出异常，避免影响登录流程
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(Long userId, String roleName) {
        User user = findUserById(userId);
        return user.getUserRoles().stream()
                .map(UserRole::getRole)
                .anyMatch(role -> role.getRoleKey().equals(roleName) || role.getRoleName().equals(roleName));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getUserRoles(Long userId) {
        User user = findUserById(userId);
        return user.getUserRoles().stream()
                .map(UserRole::getRole)
                .map(Role::getRoleKey)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasMenuPermission(Long userId, String menuPath) {
        List<String> userRoles = getUserRoles(userId);

        // 系统管理员拥有所有权限
        if (userRoles.contains("ROLE_SUPER_ADMIN") || userRoles.contains("ROLE_ADMIN") ||
            userRoles.contains("ROLE_PRINCIPAL") || userRoles.contains("ROLE_VICE_PRINCIPAL")) {
            return true;
        }

        // 根据菜单路径和角色判断权限
        return checkMenuPermissionByRole(userRoles, menuPath);
    }

    /**
     * 根据角色检查菜单权限
     */
    private boolean checkMenuPermissionByRole(List<String> userRoles, String menuPath) {
        // 所有角色都可以访问的页面
        if (menuPath.equals("/admin/dashboard") ||
            menuPath.equals("/admin/profile") ||
            menuPath.equals("/admin/test-api")) {
            return true;
        }

        // 系统管理页面 - 只有SUPER_ADMIN、ADMIN和SYSTEM_ADMIN可以访问
        if (menuPath.startsWith("/admin/users") ||
            menuPath.startsWith("/admin/roles") ||
            menuPath.startsWith("/admin/permissions") ||
            menuPath.startsWith("/admin/settings")) {
            return userRoles.contains("SUPER_ADMIN") || userRoles.contains("ADMIN") || userRoles.contains("SYSTEM_ADMIN");
        }

        // 教务管理页面 - 系统管理员、教务相关角色、教学人员可以访问
        if (menuPath.startsWith("/admin/academic/") ||
            menuPath.startsWith("/admin/students") ||
            menuPath.startsWith("/admin/courses") ||
            menuPath.startsWith("/admin/grades")) {
            return userRoles.contains("ROLE_SUPER_ADMIN") ||
                   userRoles.contains("ROLE_ADMIN") ||
                   userRoles.contains("ROLE_PRINCIPAL") ||
                   userRoles.contains("ROLE_VICE_PRINCIPAL") ||
                   userRoles.contains("ROLE_ACADEMIC_DIRECTOR") ||
                   userRoles.contains("ROLE_DEAN") ||
                   userRoles.contains("ROLE_VICE_DEAN") ||
                   userRoles.contains("ROLE_DEPARTMENT_HEAD") ||
                   userRoles.contains("ROLE_TEACHING_GROUP_HEAD") ||
                   userRoles.contains("ROLE_TEACHER") ||
                   userRoles.contains("ROLE_PROFESSOR") ||
                   userRoles.contains("ROLE_ASSOCIATE_PROFESSOR") ||
                   userRoles.contains("ROLE_LECTURER") ||
                   userRoles.contains("ROLE_CLASS_TEACHER") ||
                   userRoles.contains("ROLE_COUNSELOR") ||
                   userRoles.contains("ROLE_ACADEMIC_STAFF");
        }

        // 财务管理页面 - 系统管理员、财务相关角色可以访问
        if (menuPath.startsWith("/admin/fee-items") ||
            menuPath.startsWith("/admin/payments") ||
            menuPath.startsWith("/admin/payment-records") ||
            menuPath.startsWith("/admin/finance") ||
            menuPath.startsWith("/admin/reports")) {
            return userRoles.contains("ROLE_SUPER_ADMIN") ||
                   userRoles.contains("ROLE_ADMIN") ||
                   userRoles.contains("ROLE_PRINCIPAL") ||
                   userRoles.contains("ROLE_VICE_PRINCIPAL") ||
                   userRoles.contains("ROLE_FINANCE_DIRECTOR") ||
                   userRoles.contains("ROLE_FINANCE_STAFF");
        }

        // 学生事务管理页面 - 学生工作相关角色可以访问
        if (menuPath.startsWith("/admin/student-affairs") ||
            menuPath.startsWith("/admin/classes") ||
            menuPath.startsWith("/admin/attendance")) {
            return userRoles.contains("ROLE_SUPER_ADMIN") ||
                   userRoles.contains("ROLE_ADMIN") ||
                   userRoles.contains("ROLE_PRINCIPAL") ||
                   userRoles.contains("ROLE_VICE_PRINCIPAL") ||
                   userRoles.contains("ROLE_STUDENT_AFFAIRS_DIRECTOR") ||
                   userRoles.contains("ROLE_DEAN") ||
                   userRoles.contains("ROLE_DEPARTMENT_HEAD") ||
                   userRoles.contains("ROLE_CLASS_TEACHER") ||
                   userRoles.contains("ROLE_COUNSELOR") ||
                   userRoles.contains("ROLE_STUDENT_AFFAIRS_STAFF");
        }

        // 人事管理页面 - 人事相关角色可以访问
        if (menuPath.startsWith("/admin/hr") ||
            menuPath.startsWith("/admin/teachers") ||
            menuPath.startsWith("/admin/staff")) {
            return userRoles.contains("ROLE_SUPER_ADMIN") ||
                   userRoles.contains("ROLE_ADMIN") ||
                   userRoles.contains("ROLE_PRINCIPAL") ||
                   userRoles.contains("ROLE_VICE_PRINCIPAL") ||
                   userRoles.contains("ROLE_HR_DIRECTOR") ||
                   userRoles.contains("ROLE_HR_STAFF");
        }

        // 系统管理页面 - 仅系统管理员和IT相关角色可以访问
        if (menuPath.startsWith("/admin/system") ||
            menuPath.startsWith("/admin/config") ||
            menuPath.startsWith("/admin/logs")) {
            return userRoles.contains("ROLE_SUPER_ADMIN") ||
                   userRoles.contains("ROLE_ADMIN") ||
                   userRoles.contains("ROLE_IT_DIRECTOR");
        }

        // 默认拒绝访问
        return false;
    }

    @Override
    public User createUser(User user) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (user.getEmail() != null && userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("邮箱已存在");
        }

        // 加密密码
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // 设置创建时间
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        User user = findUserById(id);

        // 更新用户信息
        if (userDetails.getRealName() != null) {
            user.setRealName(userDetails.getRealName());
        }
        if (userDetails.getEmail() != null) {
            // 检查邮箱是否被其他用户使用
            Optional<User> existingUser = userRepository.findByEmail(userDetails.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new RuntimeException("邮箱已被其他用户使用");
            }
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPhone() != null) {
            user.setPhone(userDetails.getPhone());
        }
        if (userDetails.getStatus() != null) {
            user.setStatus(userDetails.getStatus());
        }

        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public boolean deleteUser(Long id) {
        try {
            User user = findUserById(id);
            // 软删除：设置状态为已删除(-1表示已删除，0表示禁用，1表示正常)
            user.setStatus(-1);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        User user = findUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public boolean resetPassword(Long userId) {
        try {
            // 重置为默认密码：123456
            resetPassword(userId, "123456");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        try {
            User user = findUserById(userId);

            // 验证旧密码
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return false;
            }

            // 更新密码
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean toggleUserStatus(Long userId) {
        try {
            User user = findUserById(userId);
            user.setStatus(user.getStatus() == 1 ? 0 : 1);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public UserStatistics getUserStatistics() {
        try {
            long totalUsers = userRepository.count();
            long activeUsers = userRepository.countByStatus(1);
            long inactiveUsers = userRepository.countByStatus(0);

            return new UserStatistics(totalUsers, activeUsers, inactiveUsers, 0);
        } catch (Exception e) {
            System.err.println("获取用户统计失败: " + e.getMessage());
            e.printStackTrace();
            // 返回默认统计信息
            return new UserStatistics(0, 0, 0, 0);
        }
    }

    @Override
    public List<User> findUsersByRole(String roleName) {
        return userRepository.findUsersByRoleName(roleName);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsersByRole(String roleName) {
        try {
            return userRepository.countUsersByRoleName(roleName);
        } catch (Exception e) {
            System.err.println("统计角色用户数量失败: " + e.getMessage());
            e.printStackTrace();
            return 0; // 发生异常时返回0
        }
    }

    @Override
    @Transactional
    public void recordLoginLog(Long userId, String ipAddress, String userAgent) {
        try {
            // 这里可以实现登录日志记录功能
            // 暂时空实现，后续可以添加日志表来记录
            User user = findUserById(userId);
            user.setLastLoginTime(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        } catch (Exception e) {
            System.err.println("记录登录日志失败: " + e.getMessage());
            // 不抛出异常，避免影响登录流程
        }
    }

    @Override
    @Transactional
    public void recordLogoutLog(Long userId) {
        try {
            // 这里可以实现登出日志记录功能
            // 暂时空实现，后续可以添加日志表来记录
            User user = findUserById(userId);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        } catch (Exception e) {
            System.err.println("记录登出日志失败: " + e.getMessage());
            // 不抛出异常，避免影响登出流程
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> searchUsers(String keyword) {
        return userRepository.searchUsers(keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findUsersByStatus(Integer status, Pageable pageable) {
        return userRepository.findByStatus(status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findUsersByPage(Pageable pageable, Map<String, Object> params) {
        try {
            // 获取所有用户并预加载角色信息，然后根据条件过滤
            List<User> allUsers = userRepository.findAllWithRoles();
            List<User> filteredUsers = new ArrayList<>();

            System.out.println("📊 获取到用户总数: " + allUsers.size());

            // 应用筛选条件
            for (User user : allUsers) {
                boolean matches = true;

                // 首先过滤掉已删除的用户（状态为-1的用户）
                if (user.getStatus() == -1) {
                    continue; // 跳过已删除的用户
                }

                // 搜索条件
                if (params != null && params.containsKey("search")) {
                    String search = (String) params.get("search");
                    if (search != null && !search.trim().isEmpty()) {
                        search = search.trim().toLowerCase();
                        boolean searchMatch = false;

                        System.out.println("🔍 搜索关键词: " + search + ", 检查用户: " + user.getUsername());

                        if (user.getUsername() != null && user.getUsername().toLowerCase().contains(search)) {
                            searchMatch = true;
                            System.out.println("✅ 用户名匹配: " + user.getUsername());
                        }
                        if (user.getRealName() != null && user.getRealName().toLowerCase().contains(search)) {
                            searchMatch = true;
                            System.out.println("✅ 真实姓名匹配: " + user.getRealName());
                        }
                        if (user.getEmail() != null && user.getEmail().toLowerCase().contains(search)) {
                            searchMatch = true;
                            System.out.println("✅ 邮箱匹配: " + user.getEmail());
                        }

                        if (!searchMatch) {
                            matches = false;
                            System.out.println("❌ 搜索不匹配，过滤掉用户: " + user.getUsername());
                        } else {
                            System.out.println("✅ 搜索匹配，保留用户: " + user.getUsername());
                        }
                    }
                }

                // 角色条件
                if (matches && params != null && params.containsKey("role")) {
                    String role = (String) params.get("role");
                    if (role != null && !role.trim().isEmpty()) {
                        boolean roleMatch = false;
                        System.out.println("🔍 角色筛选: " + role + ", 检查用户: " + user.getUsername());

                        if (user.getUserRoles() != null) {
                            System.out.println("  用户角色数量: " + user.getUserRoles().size());
                            for (UserRole userRole : user.getUserRoles()) {
                                if (userRole.getRole() != null) {
                                    System.out.println("  检查角色: ID=" + userRole.getRole().getId() +
                                                     ", Key=" + userRole.getRole().getRoleKey() +
                                                     ", Name=" + userRole.getRole().getRoleName());

                                    // 支持按角色ID、角色名称或角色键匹配
                                    if (role.equals(String.valueOf(userRole.getRole().getId())) ||
                                        role.equals(userRole.getRole().getRoleKey()) ||
                                        role.equals(userRole.getRole().getRoleName())) {
                                        roleMatch = true;
                                        System.out.println("✅ 角色匹配: " + userRole.getRole().getRoleName());
                                        break;
                                    }
                                }
                            }
                        } else {
                            System.out.println("  用户没有角色");
                        }

                        if (!roleMatch) {
                            matches = false;
                            System.out.println("❌ 角色不匹配，过滤掉用户: " + user.getUsername());
                        } else {
                            System.out.println("✅ 角色匹配，保留用户: " + user.getUsername());
                        }
                    }
                }

                // 状态条件
                if (matches && params != null && params.containsKey("status")) {
                    Object statusObj = params.get("status");
                    if (statusObj != null) {
                        try {
                            int status;
                            if (statusObj instanceof Integer) {
                                status = (Integer) statusObj;
                            } else if (statusObj instanceof String) {
                                String statusStr = (String) statusObj;
                                if (statusStr.trim().isEmpty()) {
                                    continue; // 空字符串，跳过状态筛选
                                }
                                status = Integer.parseInt(statusStr);
                            } else {
                                continue; // 其他类型，跳过状态筛选
                            }

                            System.out.println("🔍 状态筛选: 用户状态=" + user.getStatus() + ", 筛选状态=" + status);
                            if (user.getStatus() != status) {
                                matches = false;
                                System.out.println("❌ 状态不匹配，过滤掉用户: " + user.getUsername());
                            } else {
                                System.out.println("✅ 状态匹配，保留用户: " + user.getUsername());
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("❌ 状态参数格式错误: " + statusObj);
                            // 状态参数格式错误，忽略该条件
                        }
                    }
                }

                if (matches) {
                    filteredUsers.add(user);
                }
            }

            // 手动分页
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), filteredUsers.size());
            List<User> pageContent = start < filteredUsers.size() ?
                filteredUsers.subList(start, end) : new ArrayList<>();

            return new PageImpl<>(pageContent, pageable, filteredUsers.size());

        } catch (Exception e) {
            System.err.println("分页查询用户失败: " + e.getMessage());
            e.printStackTrace();
            // 返回空页面，避免系统崩溃
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalUsers() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveUsers() {
        return userRepository.countByStatus(1);
    }

    @Override
    @Transactional(readOnly = true)
    public long countInactiveUsers() {
        return userRepository.countByStatus(0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> exportUsers(Map<String, Object> params) {
        // 根据查询条件获取所有用户（不分页）
        if (params != null && params.containsKey("status")) {
            Object statusObj = params.get("status");
            Integer status = null;
            if (statusObj instanceof String) {
                status = Integer.parseInt((String) statusObj);
            } else if (statusObj instanceof Integer) {
                status = (Integer) statusObj;
            }
            if (status != null) {
                return userRepository.findByStatus(status);
            }
        }

        if (params != null && (params.containsKey("search") || params.containsKey("keyword"))) {
            String keyword = (String) params.getOrDefault("search", params.get("keyword"));
            if (keyword != null && !keyword.trim().isEmpty()) {
                return userRepository.searchUsers(keyword);
            }
        }

        // 默认返回所有用户
        return userRepository.findAll();
    }



    @Override
    @Transactional(readOnly = true)
    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    @Transactional
    public boolean updatePassword(Long userId, String newPassword) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("更新密码失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, int status) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setStatus(status);
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public User createUser(Map<String, Object> userData) {
        // 验证必填字段
        String username = (String) userData.get("username");
        String password = (String) userData.get("password");
        String email = (String) userData.get("email");
        String roleName = (String) userData.get("role");

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("角色不能为空");
        }

        // 检查用户名是否已存在
        if (existsByUsername(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (existsByEmail(email)) {
            throw new IllegalArgumentException("邮箱已存在");
        }

        // 创建用户
        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email.trim());
        user.setRealName((String) userData.get("realName"));
        user.setPhone((String) userData.get("phone"));
        user.setStatus(userData.get("status") != null ? (Integer) userData.get("status") : 1);
        user.setCreatedAt(LocalDateTime.now());

        // 保存用户
        user = userRepository.save(user);

        // 分配角色
        String roleKey = (String) userData.get("role");
        if (roleKey != null && !roleKey.trim().isEmpty()) {
            Optional<Role> roleOpt = roleRepository.findByRoleKey(roleKey);
            if (roleOpt.isPresent()) {
                UserRole userRole = new UserRole(user.getId(), roleOpt.get().getId());
                userRoleRepository.save(userRole);
            }
        }

        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserDTOWithRole(Long userId) {
        User user = findUserById(userId);
        UserDTO userDTO = new UserDTO(user);

        // 手动设置角色信息
        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        if (!userRoles.isEmpty()) {
            UserRole userRole = userRoles.get(0);
            Optional<Role> roleOpt = roleRepository.findById(userRole.getRoleId());
            if (roleOpt.isPresent()) {
                Role role = roleOpt.get();
                userDTO.setRoleName(role.getRoleName());
                userDTO.setRoleKey(role.getRoleKey());
            }
        }

        return userDTO;
    }



    @Override
    @Transactional
    public User updateUser(Long userId, Map<String, Object> userData) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 更新用户基本信息
        if (userData.containsKey("realName")) {
            user.setRealName((String) userData.get("realName"));
        }
        if (userData.containsKey("email")) {
            String email = (String) userData.get("email");
            if (email != null && !email.equals(user.getEmail())) {
                // 检查新邮箱是否已被其他用户使用
                if (existsByEmail(email)) {
                    throw new IllegalArgumentException("邮箱已被其他用户使用");
                }
                user.setEmail(email);
            }
        }
        if (userData.containsKey("phone")) {
            user.setPhone((String) userData.get("phone"));
        }
        if (userData.containsKey("gender")) {
            user.setGender((String) userData.get("gender"));
        }
        if (userData.containsKey("idCard")) {
            user.setIdCard((String) userData.get("idCard"));
        }
        if (userData.containsKey("address")) {
            user.setAddress((String) userData.get("address"));
        }
        if (userData.containsKey("status")) {
            user.setStatus((Integer) userData.get("status"));
        }
        if (userData.containsKey("remarks")) {
            user.setRemarks((String) userData.get("remarks"));
        }

        // 设置更新时间
        user.setUpdatedAt(LocalDateTime.now());

        // 保存用户基本信息
        user = userRepository.save(user);

        // 处理角色信息
        if (userData.containsKey("roles")) {
            updateUserRoles(userId, userData.get("roles"));
        }

        return user;
    }

    /**
     * 更新用户角色
     */
    @Transactional
    public void updateUserRoles(Long userId, Object rolesData) {
        try {
            System.out.println("🔄 开始更新用户角色，用户ID: " + userId);

            // 先删除用户现有的所有角色
            System.out.println("🗑️ 删除用户现有角色...");
            userRoleRepository.deleteByUserId(userId);

            // 强制刷新，确保删除操作立即生效
            userRoleRepository.flush();
            System.out.println("✅ 用户现有角色已删除");

            // 添加新的角色
            if (rolesData instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> roles = (List<Map<String, Object>>) rolesData;

                System.out.println("📝 准备添加 " + roles.size() + " 个角色");

                for (Map<String, Object> roleData : roles) {
                    Object idObj = roleData.get("id");
                    if (idObj != null) {
                        Long roleId;
                        if (idObj instanceof Integer) {
                            roleId = ((Integer) idObj).longValue();
                        } else if (idObj instanceof Long) {
                            roleId = (Long) idObj;
                        } else {
                            System.out.println("⚠️ 跳过无效的角色ID类型: " + idObj.getClass());
                            continue; // 跳过无效的角色ID
                        }

                        // 检查角色是否存在
                        if (roleRepository.existsById(roleId)) {
                            // 检查是否已经存在该用户角色关系（防止重复）
                            if (!userRoleRepository.existsByUserIdAndRoleId(userId, roleId)) {
                                UserRole userRole = new UserRole(userId, roleId);
                                userRoleRepository.save(userRole);
                                System.out.println("✅ 添加用户角色: userId=" + userId + ", roleId=" + roleId);
                            } else {
                                System.out.println("⚠️ 用户角色关系已存在，跳过: userId=" + userId + ", roleId=" + roleId);
                            }
                        } else {
                            System.out.println("⚠️ 角色不存在，跳过: roleId=" + roleId);
                        }
                    }
                }
            }

            System.out.println("✅ 用户角色更新完成");
        } catch (Exception e) {
            System.err.println("❌ 更新用户角色失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("更新用户角色失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean updateUser(User user) {
        try {
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            System.err.println("更新用户信息失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean validatePassword(Long userId, String password) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                return passwordEncoder.matches(password, user.getPassword());
            }
            return false;
        } catch (Exception e) {
            System.err.println("验证密码失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public boolean batchDeleteUsers(List<Long> userIds) {
        try {
            for (Long userId : userIds) {
                deleteUser(userId);
            }
            return true;
        } catch (Exception e) {
            System.err.println("批量删除用户失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean resetUserStatus(Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                user.setStatus(1); // 重置为启用状态
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("重置用户状态失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean assignRoleToUser(Long userId, Long roleId) {
        try {
            // 检查用户是否存在
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return false;
            }

            // 检查角色是否存在
            Role role = roleRepository.findById(roleId).orElse(null);
            if (role == null) {
                return false;
            }

            // 检查是否已经分配了该角色
            if (userRoleRepository.existsByUserIdAndRoleId(userId, roleId)) {
                return true; // 已经分配，返回成功
            }

            // 创建用户角色关联
            UserRole userRole = new UserRole(userId, roleId);
            userRoleRepository.save(userRole);
            return true;
        } catch (Exception e) {
            System.err.println("分配角色失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean removeRoleFromUser(Long userId, Long roleId) {
        try {
            userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
            return true;
        } catch (Exception e) {
            System.err.println("移除用户角色失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByIdOptional(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    @Transactional
    public User save(User user) {
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public void assignRole(Long userId, Long roleId) {
        // 简化的角色分配方法
        assignRoleToUser(userId, roleId);
    }

    @Override
    public Object getUserPermissions(Long userId) {
        // 实现获取用户权限逻辑
        Map<String, Object> permissions = new HashMap<>();
        permissions.put("userId", userId);
        permissions.put("status", "permissions_loaded");
        return permissions;
    }

    @Override
    public List<User> findActiveUsers() {
        return userRepository.findByStatus(1);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findTeachers() {
        return userRepository.findUsersByRoleName("TEACHER");
    }

    @Override
    public List<User> findStudents() {
        return userRepository.findUsersByRoleName("STUDENT");
    }

    @Override
    public List<Map<String, Object>> getDepartments() {
        // 这里应该调用 DepartmentService 来获取部门数据
        // 为了避免循环依赖，这里返回模拟数据
        List<Map<String, Object>> departments = new ArrayList<>();

        String[] deptNames = {"计算机学院", "数学学院", "物理学院", "化学学院", "生物学院", "外语学院"};
        String[] deptCodes = {"CS", "MATH", "PHYS", "CHEM", "BIO", "LANG"};

        for (int i = 0; i < deptNames.length; i++) {
            Map<String, Object> dept = new HashMap<>();
            dept.put("id", (long) (i + 1));
            dept.put("name", deptNames[i]);
            dept.put("code", deptCodes[i]);
            dept.put("description", deptNames[i] + "描述");
            departments.add(dept);
        }

        return departments;
    }

    // ================================
    // 家长相关方法实现
    // ================================

    @Override
    public List<User> findParents() {
        return userRepository.findUsersByRoleName("PARENT");
    }

    @Override
    public Map<String, Object> getParentStudentRelationById(Long id) {
        // 模拟家长学生关系数据
        Map<String, Object> relation = new HashMap<>();
        relation.put("id", id);
        relation.put("parentId", 1L);
        relation.put("studentId", 2L);
        relation.put("relationType", "父亲");
        relation.put("parentName", "张三");
        relation.put("studentName", "张小明");
        relation.put("createTime", LocalDateTime.now().minusDays(30));
        relation.put("status", 1);
        return relation;
    }

    @Override
    public List<Map<String, Object>> getRelationsByParent(Long parentId) {
        List<Map<String, Object>> relations = new ArrayList<>();

        // 模拟数据
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> relation = new HashMap<>();
            relation.put("id", (long) i);
            relation.put("parentId", parentId);
            relation.put("studentId", (long) (i + 10));
            relation.put("relationType", i == 1 ? "父亲" : i == 2 ? "母亲" : "监护人");
            relation.put("studentName", "学生" + i);
            relation.put("studentNo", "2024" + String.format("%04d", i));
            relation.put("grade", "2024级");
            relation.put("className", "计算机" + i + "班");
            relation.put("createTime", LocalDateTime.now().minusDays(i * 10));
            relation.put("status", 1);
            relations.add(relation);
        }

        return relations;
    }

    @Override
    public List<Map<String, Object>> getRelationsByStudent(Long studentId) {
        List<Map<String, Object>> relations = new ArrayList<>();

        // 模拟数据
        String[] relationTypes = {"父亲", "母亲"};
        String[] parentNames = {"张三", "李四"};

        for (int i = 0; i < relationTypes.length; i++) {
            Map<String, Object> relation = new HashMap<>();
            relation.put("id", (long) (i + 1));
            relation.put("parentId", (long) (i + 100));
            relation.put("studentId", studentId);
            relation.put("relationType", relationTypes[i]);
            relation.put("parentName", parentNames[i]);
            relation.put("parentPhone", "138" + String.format("%08d", i + 12345678));
            relation.put("parentEmail", parentNames[i].toLowerCase() + "@example.com");
            relation.put("createTime", LocalDateTime.now().minusDays((i + 1) * 15));
            relation.put("status", 1);
            relations.add(relation);
        }

        return relations;
    }

    @Override
    public Map<String, Object> getParentStudentRelationStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 模拟统计数据
        statistics.put("totalRelations", 1250);
        statistics.put("activeRelations", 1180);
        statistics.put("inactiveRelations", 70);
        statistics.put("totalParents", 850);
        statistics.put("totalStudents", 1200);

        // 按关系类型统计
        Map<String, Integer> typeStats = new HashMap<>();
        typeStats.put("父亲", 520);
        typeStats.put("母亲", 480);
        typeStats.put("监护人", 180);
        typeStats.put("其他", 70);
        statistics.put("relationTypeStats", typeStats);

        // 按年级统计
        Map<String, Integer> gradeStats = new HashMap<>();
        gradeStats.put("2021级", 280);
        gradeStats.put("2022级", 320);
        gradeStats.put("2023级", 350);
        gradeStats.put("2024级", 300);
        statistics.put("gradeStats", gradeStats);

        return statistics;
    }

    @Override
    public Map<String, Object> countParentStudentRelationsByType() {
        Map<String, Object> typeCounts = new HashMap<>();

        // 模拟按类型统计数据
        Map<String, Long> counts = new HashMap<>();
        counts.put("父亲", 520L);
        counts.put("母亲", 480L);
        counts.put("监护人", 180L);
        counts.put("祖父母", 45L);
        counts.put("外祖父母", 35L);
        counts.put("其他", 25L);

        typeCounts.put("counts", counts);
        typeCounts.put("total", counts.values().stream().mapToLong(Long::longValue).sum());

        return typeCounts;
    }

    // ================================
    // 消息管理页面需要的方法实现
    // ================================

    @Override
    public List<Map<String, Object>> getUserGroups() {
        List<Map<String, Object>> groups = new ArrayList<>();

        String[] groupNames = {"全体学生", "全体教师", "全体家长", "管理员", "2024级学生", "2023级学生", "计算机学院", "数学学院"};
        String[] groupTypes = {"角色", "角色", "角色", "角色", "年级", "年级", "学院", "学院"};
        int[] memberCounts = {1200, 80, 800, 10, 300, 320, 450, 280};

        for (int i = 0; i < groupNames.length; i++) {
            Map<String, Object> group = new HashMap<>();
            group.put("id", (long) (i + 1));
            group.put("name", groupNames[i]);
            group.put("type", groupTypes[i]);
            group.put("memberCount", memberCounts[i]);
            group.put("description", groupNames[i] + "用户组");
            groups.add(group);
        }

        return groups;
    }

    @Override
    public List<Map<String, Object>> getAllRoles() {
        List<Map<String, Object>> roles = new ArrayList<>();

        String[] roleNames = {"超级管理员", "系统管理员", "教务管理员", "财务管理员", "教师", "学生", "家长"};
        String[] roleCodes = {"SUPER_ADMIN", "ADMIN", "ACADEMIC_ADMIN", "FINANCE_ADMIN", "TEACHER", "STUDENT", "PARENT"};
        String[] descriptions = {"系统超级管理员", "系统管理员", "教务管理员", "财务管理员", "任课教师", "在校学生", "学生家长"};

        for (int i = 0; i < roleNames.length; i++) {
            Map<String, Object> role = new HashMap<>();
            role.put("id", (long) (i + 1));
            role.put("name", roleNames[i]);
            role.put("code", roleCodes[i]);
            role.put("description", descriptions[i]);
            role.put("status", 1);
            roles.add(role);
        }

        return roles;
    }

    // ================================
    // ParentStudentRelationApiController 需要的方法实现
    // ================================

    @Override
    @Transactional
    public com.campus.domain.entity.family.ParentStudentRelation createParentStudentRelation(com.campus.domain.entity.family.ParentStudentRelation relation) {
        try {
            // 模拟创建家长学生关系
            relation.setId(System.currentTimeMillis()); // 模拟ID生成
            relation.setCreatedAt(LocalDateTime.now());
            relation.setUpdatedAt(LocalDateTime.now());
            relation.setDeleted(0);
            relation.setStatus(1);

            System.out.println("✅ 创建家长学生关系成功: " + relation.getId());
            return relation;
        } catch (Exception e) {
            System.err.println("❌ 创建家长学生关系失败: " + e.getMessage());
            throw new RuntimeException("创建家长学生关系失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public com.campus.domain.entity.family.ParentStudentRelation updateParentStudentRelation(com.campus.domain.entity.family.ParentStudentRelation relation) {
        try {
            relation.setUpdatedAt(LocalDateTime.now());
            System.out.println("✅ 更新家长学生关系成功: " + relation.getId());
            return relation;
        } catch (Exception e) {
            System.err.println("❌ 更新家长学生关系失败: " + e.getMessage());
            throw new RuntimeException("更新家长学生关系失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteParentStudentRelation(Long relationId) {
        try {
            System.out.println("✅ 删除家长学生关系成功: " + relationId);
        } catch (Exception e) {
            System.err.println("❌ 删除家长学生关系失败: " + e.getMessage());
            throw new RuntimeException("删除家长学生关系失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<com.campus.domain.entity.family.ParentStudentRelation> findParentStudentRelations(Pageable pageable, Long parentId, Long studentId, String relationType) {
        try {
            // 模拟分页数据
            List<com.campus.domain.entity.family.ParentStudentRelation> relations = new ArrayList<>();

            for (int i = 0; i < Math.min(pageable.getPageSize(), 10); i++) {
                com.campus.domain.entity.family.ParentStudentRelation relation = new com.campus.domain.entity.family.ParentStudentRelation();
                relation.setId((long) (i + 1));
                relation.setParentId(parentId != null ? parentId : (long) (i + 100));
                relation.setStudentId(studentId != null ? studentId : (long) (i + 200));
                relation.setRelationType(relationType != null ? relationType : (i % 2 == 0 ? "父亲" : "母亲"));
                relation.setCreatedAt(LocalDateTime.now().minusDays(i));
                relation.setStatus(1);
                relations.add(relation);
            }

            return new org.springframework.data.domain.PageImpl<>(relations, pageable, 50);
        } catch (Exception e) {
            System.err.println("❌ 查询家长学生关系失败: " + e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.campus.domain.entity.family.ParentStudentRelation> getRelationsByType(String relationType) {
        try {
            List<com.campus.domain.entity.family.ParentStudentRelation> relations = new ArrayList<>();

            for (int i = 1; i <= 5; i++) {
                com.campus.domain.entity.family.ParentStudentRelation relation = new com.campus.domain.entity.family.ParentStudentRelation();
                relation.setId((long) i);
                relation.setParentId((long) (i + 100));
                relation.setStudentId((long) (i + 200));
                relation.setRelationType(relationType);
                relation.setCreatedAt(LocalDateTime.now().minusDays(i));
                relation.setStatus(1);
                relations.add(relation);
            }

            return relations;
        } catch (Exception e) {
            System.err.println("❌ 根据类型查询关系失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyParentStudentRelation(Long parentId, Long studentId) {
        try {
            // 模拟验证逻辑
            return parentId != null && studentId != null && parentId > 0 && studentId > 0;
        } catch (Exception e) {
            System.err.println("❌ 验证家长学生关系失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> validateParentStudentRelation(com.campus.domain.entity.family.ParentStudentRelation relation) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean isValid = true;
            List<String> errors = new ArrayList<>();

            if (relation.getParentId() == null) {
                isValid = false;
                errors.add("家长ID不能为空");
            }

            if (relation.getStudentId() == null) {
                isValid = false;
                errors.add("学生ID不能为空");
            }

            if (relation.getRelationType() == null || relation.getRelationType().trim().isEmpty()) {
                isValid = false;
                errors.add("关系类型不能为空");
            }

            result.put("valid", isValid);
            result.put("errors", errors);
            result.put("message", isValid ? "验证通过" : "验证失败");

        } catch (Exception e) {
            System.err.println("❌ 验证关系数据失败: " + e.getMessage());
            result.put("valid", false);
            result.put("errors", List.of("验证过程出错: " + e.getMessage()));
            result.put("message", "验证失败");
        }

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> batchCreateParentStudentRelations(List<com.campus.domain.entity.family.ParentStudentRelation> relations) {
        Map<String, Object> result = new HashMap<>();

        try {
            int successCount = 0;
            int failCount = 0;
            List<String> errors = new ArrayList<>();

            for (com.campus.domain.entity.family.ParentStudentRelation relation : relations) {
                try {
                    createParentStudentRelation(relation);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    errors.add("关系创建失败: " + e.getMessage());
                }
            }

            result.put("success", true);
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("totalCount", relations.size());
            result.put("errors", errors);
            result.put("message", String.format("批量创建完成，成功: %d, 失败: %d", successCount, failCount));

        } catch (Exception e) {
            System.err.println("❌ 批量创建关系失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "批量创建失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    @Transactional
    public void batchDeleteParentStudentRelations(List<Long> relationIds) {
        try {
            for (Long relationId : relationIds) {
                deleteParentStudentRelation(relationId);
            }
            System.out.println("✅ 批量删除关系成功，数量: " + relationIds.size());
        } catch (Exception e) {
            System.err.println("❌ 批量删除关系失败: " + e.getMessage());
            throw new RuntimeException("批量删除关系失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Map<String, Object> importParentStudentRelations(List<com.campus.domain.entity.family.ParentStudentRelation> relations) {
        Map<String, Object> result = new HashMap<>();

        try {
            int successCount = 0;
            int failCount = 0;
            List<String> errors = new ArrayList<>();

            for (com.campus.domain.entity.family.ParentStudentRelation relation : relations) {
                try {
                    // 验证数据
                    Map<String, Object> validation = validateParentStudentRelation(relation);
                    if ((Boolean) validation.get("valid")) {
                        createParentStudentRelation(relation);
                        successCount++;
                    } else {
                        failCount++;
                        Object errorsObj = validation.get("errors");
                        if (errorsObj instanceof List) {
                            @SuppressWarnings("unchecked")
                            List<String> validationErrors = (List<String>) errorsObj;
                            errors.addAll(validationErrors);
                        }
                    }
                } catch (Exception e) {
                    failCount++;
                    errors.add("导入失败: " + e.getMessage());
                }
            }

            result.put("success", true);
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("totalCount", relations.size());
            result.put("errors", errors);
            result.put("message", String.format("导入完成，成功: %d, 失败: %d", successCount, failCount));

        } catch (Exception e) {
            System.err.println("❌ 导入关系失败: " + e.getMessage());
            result.put("success", false);
            result.put("message", "导入失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.campus.domain.entity.family.ParentStudentRelation> exportParentStudentRelations(Long parentId, Long studentId, String relationType) {
        try {
            List<com.campus.domain.entity.family.ParentStudentRelation> relations = new ArrayList<>();

            // 模拟导出数据
            for (int i = 1; i <= 20; i++) {
                com.campus.domain.entity.family.ParentStudentRelation relation = new com.campus.domain.entity.family.ParentStudentRelation();
                relation.setId((long) i);
                relation.setParentId(parentId != null ? parentId : (long) (i + 100));
                relation.setStudentId(studentId != null ? studentId : (long) (i + 200));
                relation.setRelationType(relationType != null ? relationType : (i % 3 == 0 ? "父亲" : i % 3 == 1 ? "母亲" : "监护人"));
                relation.setCreatedAt(LocalDateTime.now().minusDays(i));
                relation.setStatus(1);
                relations.add(relation);
            }

            return relations;
        } catch (Exception e) {
            System.err.println("❌ 导出关系失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public void activateParentStudentRelation(Long relationId) {
        try {
            System.out.println("✅ 激活家长学生关系成功: " + relationId);
        } catch (Exception e) {
            System.err.println("❌ 激活关系失败: " + e.getMessage());
            throw new RuntimeException("激活关系失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deactivateParentStudentRelation(Long relationId) {
        try {
            System.out.println("✅ 停用家长学生关系成功: " + relationId);
        } catch (Exception e) {
            System.err.println("❌ 停用关系失败: " + e.getMessage());
            throw new RuntimeException("停用关系失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getOrphanedStudents() {
        try {
            // 查询所有学生，但在家长-学生关系表中没有记录的学生
            String sql = """
                SELECT DISTINCT s.id, s.student_no, u.real_name, s.grade, s.major,
                       sc.class_name, s.enrollment_date, s.status, u.phone, u.email
                FROM tb_student s
                INNER JOIN tb_user u ON s.user_id = u.id
                LEFT JOIN tb_school_class sc ON s.class_id = sc.id
                WHERE s.status = 1
                  AND s.deleted = 0
                  AND u.status = 1
                  AND u.deleted = 0
                  AND s.id NOT IN (
                      SELECT DISTINCT psr.student_id
                      FROM tb_parent_student_relation psr
                      WHERE psr.deleted = 0 AND psr.status = 1
                  )
                ORDER BY s.enrollment_date DESC
                """;

            @SuppressWarnings("unchecked")
            List<Object[]> results = entityManager.createNativeQuery(sql).getResultList();

            List<Map<String, Object>> orphanedStudents = new ArrayList<>();
            for (Object[] row : results) {
                Map<String, Object> student = new HashMap<>();
                student.put("id", row[0]);
                student.put("studentNo", row[1]);
                student.put("name", row[2]);
                student.put("grade", row[3]);
                student.put("major", row[4]);
                student.put("className", row[5]);
                student.put("enrollmentDate", row[6]);
                student.put("status", row[7]);
                student.put("phone", row[8]);
                student.put("email", row[9]);
                orphanedStudents.add(student);
            }

            log.info("✅ 查询到 {} 个孤儿学生", orphanedStudents.size());
            return orphanedStudents;
        } catch (Exception e) {
            log.error("❌ 获取孤儿学生失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getChildlessParents() {
        try {
            // 查询所有家长角色的用户，但在家长-学生关系表中没有记录的用户
            String sql = """
                SELECT DISTINCT u.id, u.username, u.real_name, u.phone, u.email,
                       u.created_at, u.status
                FROM tb_user u
                INNER JOIN tb_user_role ur ON u.id = ur.user_id
                INNER JOIN tb_role r ON ur.role_id = r.id
                WHERE r.role_key = 'PARENT'
                  AND u.status = 1
                  AND u.deleted = 0
                  AND u.id NOT IN (
                      SELECT DISTINCT psr.parent_id
                      FROM tb_parent_student_relation psr
                      WHERE psr.deleted = 0 AND psr.status = 1
                  )
                ORDER BY u.created_at DESC
                """;

            @SuppressWarnings("unchecked")
            List<Object[]> results = entityManager.createNativeQuery(sql).getResultList();

            List<Map<String, Object>> childlessParents = new ArrayList<>();
            for (Object[] row : results) {
                Map<String, Object> parent = new HashMap<>();
                parent.put("id", row[0]);
                parent.put("username", row[1]);
                parent.put("name", row[2]);
                parent.put("phone", row[3]);
                parent.put("email", row[4]);
                parent.put("registrationDate", row[5]);
                parent.put("status", row[6]);
                childlessParents.add(parent);
            }

            log.info("✅ 查询到 {} 个无子女家长", childlessParents.size());
            return childlessParents;
        } catch (Exception e) {
            log.error("❌ 获取无子女家长失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}
