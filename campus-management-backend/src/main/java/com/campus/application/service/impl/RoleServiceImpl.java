package com.campus.application.service.impl;

import com.campus.application.service.RoleService;
import com.campus.domain.entity.Role;
import com.campus.domain.repository.RoleRepository;
import com.campus.domain.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * 角色服务实现类
 * 提供角色管理的核心业务逻辑
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, UserRoleRepository userRoleRepository) {
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "roles", key = "'page_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Role> findAllRoles(Pageable pageable) {
        try {
            return roleRepository.findAll(pageable);
        } catch (Exception e) {
            System.err.println("获取角色列表失败: " + e.getMessage());
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Role> findRolesByPage(Pageable pageable, Map<String, Object> params) {
        try {
            List<Role> allRoles = roleRepository.findAll();
            List<Role> filteredRoles = new ArrayList<>();

            for (Role role : allRoles) {
                boolean matches = true;

                // 过滤已删除的角色
                if (role.getDeleted() != null && role.getDeleted() == 1) {
                    continue;
                }

                // 搜索条件
                if (params != null && params.containsKey("search")) {
                    String search = (String) params.get("search");
                    if (search != null && !search.trim().isEmpty()) {
                        search = search.trim().toLowerCase();
                        boolean searchMatch = false;

                        if (role.getRoleName() != null && role.getRoleName().toLowerCase().contains(search)) {
                            searchMatch = true;
                        }
                        if (role.getRoleKey() != null && role.getRoleKey().toLowerCase().contains(search)) {
                            searchMatch = true;
                        }
                        if (role.getDescription() != null && role.getDescription().toLowerCase().contains(search)) {
                            searchMatch = true;
                        }

                        if (!searchMatch) {
                            matches = false;
                        }
                    }
                }

                // 状态条件
                if (matches && params != null && params.containsKey("status")) {
                    Object statusObj = params.get("status");
                    if (statusObj != null) {
                        try {
                            Integer status;
                            if (statusObj instanceof Integer) {
                                status = (Integer) statusObj;
                            } else if (statusObj instanceof String) {
                                String statusStr = (String) statusObj;
                                if (statusStr.trim().isEmpty()) {
                                    continue;
                                }
                                status = Integer.parseInt(statusStr);
                            } else {
                                continue;
                            }

                            if (!role.getStatus().equals(status)) {
                                matches = false;
                            }
                        } catch (NumberFormatException e) {
                            // 状态参数格式错误，忽略该条件
                        }
                    }
                }

                if (matches) {
                    // 计算用户数量
                    long userCount = userRoleRepository.countByRoleId(role.getId());
                    role.setUserCount((int) userCount);
                    filteredRoles.add(role);
                }
            }

            // 手动分页
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), filteredRoles.size());
            List<Role> pageContent = start < filteredRoles.size() ?
                filteredRoles.subList(start, end) : new ArrayList<>();

            return new PageImpl<>(pageContent, pageable, filteredRoles.size());

        } catch (Exception e) {
            System.err.println("分页查询角色失败: " + e.getMessage());
            e.printStackTrace();
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "roles", key = "'role_' + #id")
    public Optional<Role> findById(Long id) {
        try {
            return roleRepository.findById(id);
        } catch (Exception e) {
            System.err.println("根据ID查找角色失败: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "roles", key = "'roleKey_' + #roleKey")
    public Optional<Role> findByRoleKey(String roleKey) {
        try {
            return roleRepository.findByRoleKey(roleKey);
        } catch (Exception e) {
            System.err.println("根据角色键查找角色失败: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "roles", key = "'roleName_' + #roleName")
    public Optional<Role> findByRoleName(String roleName) {
        try {
            return roleRepository.findByRoleName(roleName);
        } catch (Exception e) {
            System.err.println("根据角色名称查找角色失败: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @CacheEvict(value = "roles", allEntries = true)
    public Role createRole(Role role) {
        try {
            // 检查角色键是否已存在
            if (existsByRoleKey(role.getRoleKey())) {
                throw new IllegalArgumentException("角色键已存在: " + role.getRoleKey());
            }

            // 检查角色名称是否已存在
            if (existsByRoleName(role.getRoleName())) {
                throw new IllegalArgumentException("角色名称已存在: " + role.getRoleName());
            }

            // 设置默认值
            if (role.getStatus() == null) {
                role.setStatus(1); // 默认启用
            }
            if (role.getDeleted() == null) {
                role.setDeleted(0); // 默认未删除
            }
            if (role.getSortOrder() == null) {
                role.setSortOrder(0); // 默认排序
            }

            role.setCreatedAt(LocalDateTime.now());
            role.setUpdatedAt(LocalDateTime.now());

            return roleRepository.save(role);
        } catch (Exception e) {
            System.err.println("创建角色失败: " + e.getMessage());
            throw new RuntimeException("创建角色失败: " + e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = "roles", allEntries = true)
    public Role updateRole(Long id, Role roleDetails) {
        try {
            Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("角色不存在: " + id));

            // 更新角色信息
            if (roleDetails.getRoleName() != null && !roleDetails.getRoleName().equals(role.getRoleName())) {
                // 检查新角色名称是否已被其他角色使用
                Optional<Role> existingRole = roleRepository.findByRoleName(roleDetails.getRoleName());
                if (existingRole.isPresent() && !existingRole.get().getId().equals(id)) {
                    throw new IllegalArgumentException("角色名称已被其他角色使用: " + roleDetails.getRoleName());
                }
                role.setRoleName(roleDetails.getRoleName());
            }

            if (roleDetails.getRoleKey() != null && !roleDetails.getRoleKey().equals(role.getRoleKey())) {
                // 检查新角色键是否已被其他角色使用
                Optional<Role> existingRole = roleRepository.findByRoleKey(roleDetails.getRoleKey());
                if (existingRole.isPresent() && !existingRole.get().getId().equals(id)) {
                    throw new IllegalArgumentException("角色键已被其他角色使用: " + roleDetails.getRoleKey());
                }
                role.setRoleKey(roleDetails.getRoleKey());
            }

            if (roleDetails.getDescription() != null) {
                role.setDescription(roleDetails.getDescription());
            }
            if (roleDetails.getStatus() != null) {
                role.setStatus(roleDetails.getStatus());
            }
            if (roleDetails.getSortOrder() != null) {
                role.setSortOrder(roleDetails.getSortOrder());
            }

            role.setUpdatedAt(LocalDateTime.now());

            return roleRepository.save(role);
        } catch (Exception e) {
            System.err.println("更新角色失败: " + e.getMessage());
            throw new RuntimeException("更新角色失败: " + e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = "roles", allEntries = true)
    public boolean deleteRole(Long id) {
        try {
            Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("角色不存在: " + id));

            // 检查是否有用户使用该角色
            long userCount = userRoleRepository.countByRoleId(id);
            if (userCount > 0) {
                throw new IllegalArgumentException("该角色正在被 " + userCount + " 个用户使用，无法删除");
            }

            // 软删除：设置deleted标志
            role.setDeleted(1);
            role.setUpdatedAt(LocalDateTime.now());
            roleRepository.save(role);

            return true;
        } catch (Exception e) {
            System.err.println("删除角色失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    @CacheEvict(value = "roles", allEntries = true)
    public boolean toggleRoleStatus(Long id) {
        try {
            Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("角色不存在: " + id));

            // 切换状态
            role.setStatus(role.getStatus() == 1 ? 0 : 1);
            role.setUpdatedAt(LocalDateTime.now());
            roleRepository.save(role);

            return true;
        } catch (Exception e) {
            System.err.println("切换角色状态失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "roles", key = "'activeRoles'")
    public List<Role> findAllActiveRoles() {
        try {
            return roleRepository.findAllActiveRoles();
        } catch (Exception e) {
            System.err.println("获取启用角色列表失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> searchRoles(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return roleRepository.findAll();
            }
            return roleRepository.searchRoles(keyword.trim());
        } catch (Exception e) {
            System.err.println("搜索角色失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countRoles() {
        try {
            return roleRepository.count();
        } catch (Exception e) {
            System.err.println("统计角色数量失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "roles", key = "'roleStatistics'")
    public RoleStatistics getRoleStatistics() {
        try {
            long totalRoles = roleRepository.count();
            
            // 统计各状态角色数量
            List<Object[]> statusCounts = roleRepository.countRolesByStatus();
            long activeRoles = 0;
            long inactiveRoles = 0;
            
            for (Object[] statusCount : statusCounts) {
                Integer status = (Integer) statusCount[0];
                Long count = (Long) statusCount[1];
                
                if (status == 1) {
                    activeRoles = count;
                } else if (status == 0) {
                    inactiveRoles = count;
                }
            }

            // 系统默认角色数量
            long systemRoles = roleRepository.findAll()
                .stream()
                .filter(role -> role.getRoleKey() != null && 
                    (role.getRoleKey().equals("ADMIN") || 
                     role.getRoleKey().equals("TEACHER") || 
                     role.getRoleKey().equals("STUDENT") || 
                     role.getRoleKey().equals("FINANCE")))
                .count();

            return new RoleStatistics(totalRoles, activeRoles, inactiveRoles, systemRoles);
        } catch (Exception e) {
            System.err.println("获取角色统计信息失败: " + e.getMessage());
            return new RoleStatistics(0, 0, 0, 0);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "roles", key = "'userRoles_' + #userId")
    public List<Role> findRolesByUserId(Long userId) {
        try {
            return roleRepository.findRolesByUserId(userId);
        } catch (Exception e) {
            System.err.println("根据用户ID查找角色失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRoleKey(String roleKey) {
        try {
            return roleRepository.existsByRoleKey(roleKey);
        } catch (Exception e) {
            System.err.println("检查角色键是否存在失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRoleName(String roleName) {
        try {
            return roleRepository.existsByRoleName(roleName);
        } catch (Exception e) {
            System.err.println("检查角色名称是否存在失败: " + e.getMessage());
            return false;
        }
    }
}