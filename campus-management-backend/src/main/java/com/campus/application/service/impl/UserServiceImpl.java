package com.campus.application.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.application.dto.UserDTO;
import com.campus.application.service.UserService;
import com.campus.domain.entity.Role;
import com.campus.domain.entity.User;
import com.campus.domain.entity.UserRole;
import com.campus.domain.repository.RoleRepository;
import com.campus.domain.repository.UserRepository;
import com.campus.domain.repository.UserRoleRepository;

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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Autowired
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

        // ADMIN角色拥有所有权限
        if (userRoles.contains("ADMIN")) {
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

        // 系统管理页面 - 只有ADMIN和SYSTEM_ADMIN可以访问
        if (menuPath.startsWith("/admin/users") ||
            menuPath.startsWith("/admin/roles") ||
            menuPath.startsWith("/admin/permissions") ||
            menuPath.startsWith("/admin/settings")) {
            return userRoles.contains("ADMIN") || userRoles.contains("SYSTEM_ADMIN");
        }

        // 教务管理页面 - ADMIN、ACADEMIC_ADMIN、TEACHER可以访问
        if (menuPath.startsWith("/admin/academic/") ||
            menuPath.startsWith("/admin/students")) {
            return userRoles.contains("ADMIN") ||
                   userRoles.contains("ACADEMIC_ADMIN") ||
                   userRoles.contains("TEACHER");
        }

        // 财务管理页面 - ADMIN、FINANCE_ADMIN可以访问
        if (menuPath.startsWith("/admin/fee-items") ||
            menuPath.startsWith("/admin/payments") ||
            menuPath.startsWith("/admin/payment-records") ||
            menuPath.startsWith("/admin/reports")) {
            return userRoles.contains("ADMIN") || userRoles.contains("FINANCE_ADMIN");
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
}
