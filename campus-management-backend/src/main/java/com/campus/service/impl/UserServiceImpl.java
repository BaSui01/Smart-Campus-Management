package com.campus.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.entity.Role;
import com.campus.entity.User;
import com.campus.entity.UserRole;
import com.campus.repository.UserRepository;
import com.campus.service.UserService;
import com.campus.service.UserService.UserStatistics;

/**
 * 用户服务实现类
 * 实现用户管理的核心业务逻辑
 *
 * @author campus
 * @since 2025-06-04
 */
@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameAndStatus(username, 1)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getStatus() == 1,
                true,
                true,
                true,
                getAuthorities(user)
        );
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
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public User authenticate(String username, String password) {
        User user = findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
    public void updateLastLoginInfo(Long userId, String ipAddress) {
        User user = findUserById(userId);
        user.setLastLoginTime(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(Long userId, String roleName) {
        User user = findUserById(userId);
        return user.getUserRoles().stream()
                .map(UserRole::getRole)
                .anyMatch(role -> role.getRoleName().equals(roleName));
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
    public void deleteUser(Long id) {
        User user = findUserById(id);
        // 软删除：设置状态为已删除
        user.setStatus(0);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        User user = findUserById(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void resetPassword(Long userId) {
        // 重置为默认密码：123456
        resetPassword(userId, "123456");
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = findUserById(userId);

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void toggleUserStatus(Long userId) {
        User user = findUserById(userId);
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public UserStatistics getUserStatistics() {
        List<UserRepository.UserStatusCount> statusCounts = userRepository.countUsersByStatus();

        long totalUsers = 0;
        long activeUsers = 0;
        long inactiveUsers = 0;

        for (UserRepository.UserStatusCount count : statusCounts) {
            totalUsers += count.getCount();
            if (count.getStatus() == 1) {
                activeUsers = count.getCount();
            } else {
                inactiveUsers += count.getCount();
            }
        }

        return new UserStatistics(totalUsers, activeUsers, inactiveUsers);
    }

    @Override
    public List<User> findUsersByRole(String roleName) {
        return userRepository.findUsersByRoleName(roleName);
    }

    @Override
    public void recordLoginLog(Long userId, String ipAddress, String userAgent) {
        // 这里可以实现登录日志记录功能
        // 暂时空实现，后续可以添加日志表来记录
        User user = findUserById(userId);
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void recordLogoutLog(Long userId) {
        // 这里可以实现登出日志记录功能
        // 暂时空实现，后续可以添加日志表来记录
        User user = findUserById(userId);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> searchUsers(String keyword) {
        return userRepository.findByUsernameContainingOrRealNameContainingOrEmailContaining(
                keyword, keyword, keyword);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findUsersByStatus(Integer status, Pageable pageable) {
        return userRepository.findByStatus(status, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findUsersByPage(Pageable pageable, Map<String, Object> params) {
        Page<User> jpaPage;

        // 根据查询条件执行不同的查询
        if (params != null && params.containsKey("status")) {
            Object statusObj = params.get("status");
            Integer status = null;
            if (statusObj instanceof String) {
                status = Integer.parseInt((String) statusObj);
            } else if (statusObj instanceof Integer) {
                status = (Integer) statusObj;
            }
            if (status != null) {
                jpaPage = userRepository.findByStatus(status, pageable);
            } else {
                jpaPage = userRepository.findAll(pageable);
            }
        } else if (params != null && (params.containsKey("search") || params.containsKey("keyword"))) {
            String keyword = (String) params.getOrDefault("search", params.get("keyword"));
            if (keyword != null && !keyword.trim().isEmpty()) {
                // 使用简化的查询，先获取所有匹配的用户，然后手动分页
                List<User> allMatchingUsers = userRepository.findByUsernameContainingOrRealNameContainingOrEmailContaining(
                    keyword, keyword, keyword);

                // 手动分页
                int start = (int) (pageable.getOffset());
                int end = Math.min(start + pageable.getPageSize(), allMatchingUsers.size());
                List<User> pageContent = start < allMatchingUsers.size() ?
                    allMatchingUsers.subList(start, end) : new ArrayList<>();

                // 创建分页对象
                jpaPage = new PageImpl<>(pageContent, pageable, allMatchingUsers.size());
            } else {
                jpaPage = userRepository.findAll(pageable);
            }
        } else {
            jpaPage = userRepository.findAll(pageable);
        }

        return jpaPage;
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

    /**
     * 获取用户权限列表
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // 添加角色权限
        for (UserRole userRole : user.getUserRoles()) {
            Role role = userRole.getRole();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName()));

            // 添加角色对应的权限
            role.getRolePermissions().forEach(rolePermission ->
                authorities.add(new SimpleGrantedAuthority(rolePermission.getPermission().getPermissionCode()))
            );
        }

        return authorities;
    }
}
