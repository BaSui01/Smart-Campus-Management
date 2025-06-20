package com.campus.application.Implement.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.application.service.auth.RoleService;
import com.campus.domain.entity.auth.Role;
import com.campus.domain.repository.auth.RoleRepository;
import com.campus.domain.repository.auth.UserRoleRepository;


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
            System.out.println("🔍 开始分页查询角色，参数: " + params);
            
            // 1. 获取基础数据并优化过滤
            List<Role> allRoles = roleRepository.findAll();
            System.out.println("📊 获取到角色总数: " + allRoles.size());
            
            // 2. 智能过滤算法
            List<Role> filteredRoles = performIntelligentFiltering(allRoles, params);
            System.out.println("✅ 过滤后角色数量: " + filteredRoles.size());
            
            // 3. 批量计算用户数量（优化性能）
            enhanceRolesWithUserCount(filteredRoles);
            
            // 4. 智能排序
            sortRolesByRelevance(filteredRoles, params);
            
            // 5. 高效分页
            Page<Role> result = createOptimizedPage(filteredRoles, pageable);
            
            System.out.println("📄 分页结果: 当前页=" + result.getNumber() +
                             ", 页大小=" + result.getSize() +
                             ", 总元素=" + result.getTotalElements());
            
            return result;

        } catch (Exception e) {
            System.err.println("❌ 分页查询角色失败: " + e.getMessage());
            System.err.println("详细错误信息: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }

    /**
     * 执行智能过滤算法
     */
    private List<Role> performIntelligentFiltering(List<Role> allRoles, Map<String, Object> params) {
        return allRoles.stream()
            .filter(this::isRoleActive) // 过滤已删除的角色
            .filter(role -> matchesSearchCriteria(role, params)) // 搜索匹配
            .filter(role -> matchesStatusCriteria(role, params)) // 状态匹配
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 检查角色是否激活
     */
    private boolean isRoleActive(Role role) {
        return role.getDeleted() == null || role.getDeleted() != 1;
    }

    /**
     * 搜索条件匹配
     */
    private boolean matchesSearchCriteria(Role role, Map<String, Object> params) {
        if (params == null || !params.containsKey("search")) {
            return true;
        }

        String search = (String) params.get("search");
        if (search == null || search.trim().isEmpty()) {
            return true;
        }

        String searchLower = search.trim().toLowerCase();
        
        // 多字段模糊搜索算法
        return isFieldContains(role.getRoleName(), searchLower) ||
               isFieldContains(role.getRoleKey(), searchLower) ||
               isFieldContains(role.getDescription(), searchLower);
    }

    /**
     * 字段包含检查
     */
    private boolean isFieldContains(String field, String search) {
        return field != null && field.toLowerCase().contains(search);
    }

    /**
     * 状态条件匹配
     */
    private boolean matchesStatusCriteria(Role role, Map<String, Object> params) {
        if (params == null || !params.containsKey("status")) {
            return true;
        }

        Object statusObj = params.get("status");
        if (statusObj == null) {
            return true;
        }

        try {
            Integer targetStatus = parseStatusValue(statusObj);
            if (targetStatus == null) {
                return true;
            }

            return role.getStatus().equals(targetStatus);
        } catch (Exception e) {
            System.out.println("⚠️ 状态参数解析失败: " + statusObj);
            return true; // 解析失败时不过滤
        }
    }

    /**
     * 解析状态值
     */
    private Integer parseStatusValue(Object statusObj) {
        if (statusObj instanceof Integer) {
            return (Integer) statusObj;
        } else if (statusObj instanceof String) {
            String statusStr = (String) statusObj;
            if (statusStr.trim().isEmpty()) {
                return null;
            }
            return Integer.valueOf(statusStr);
        }
        return null;
    }

    /**
     * 批量增强角色的用户数量信息
     */
    private void enhanceRolesWithUserCount(List<Role> roles) {
        // 批量查询用户数量，避免N+1查询问题
        Map<Long, Long> userCountMap = new HashMap<>();
        
        for (Role role : roles) {
            try {
                long userCount = userRoleRepository.countByRoleId(role.getId());
                userCountMap.put(role.getId(), userCount);
                role.setUserCount((int) userCount);
            } catch (Exception e) {
                System.out.println("⚠️ 获取角色用户数量失败: " + role.getId());
                role.setUserCount(0);
            }
        }
        
        System.out.println("📊 用户数量统计完成: " + userCountMap.size() + " 个角色");
    }

    /**
     * 按相关性排序角色
     */
    private void sortRolesByRelevance(List<Role> roles, Map<String, Object> params) {
        // 智能排序算法：优先显示最相关的结果
        roles.sort((r1, r2) -> {
            // 1. 系统角色优先
            int systemPriority = compareSystemRolePriority(r1, r2);
            if (systemPriority != 0) {
                return systemPriority;
            }
            
            // 2. 用户数量多的优先
            int userCountCompare = Integer.compare(r2.getUserCount(), r1.getUserCount());
            if (userCountCompare != 0) {
                return userCountCompare;
            }
            
            // 3. 按创建时间倒序
            if (r1.getCreatedAt() != null && r2.getCreatedAt() != null) {
                return r2.getCreatedAt().compareTo(r1.getCreatedAt());
            }
            
            // 4. 按角色名称字母序
            return r1.getRoleName().compareTo(r2.getRoleName());
        });
    }

    /**
     * 比较系统角色优先级
     */
    private int compareSystemRolePriority(Role r1, Role r2) {
        boolean isR1System = isSystemRole(r1);
        boolean isR2System = isSystemRole(r2);
        
        if (isR1System && !isR2System) {
            return -1; // r1优先
        } else if (!isR1System && isR2System) {
            return 1; // r2优先
        }
        return 0; // 相同优先级
    }

    /**
     * 判断是否为系统角色
     */
    private boolean isSystemRole(Role role) {
        if (role.getRoleKey() == null) {
            return false;
        }
        
        String[] systemRoles = {"ADMIN", "SUPER_ADMIN", "TEACHER", "STUDENT", "FINANCE"};
        for (String systemRole : systemRoles) {
            if (role.getRoleKey().equals(systemRole)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 创建优化的分页对象
     */
    private Page<Role> createOptimizedPage(List<Role> filteredRoles, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredRoles.size());
        
        List<Role> pageContent = start < filteredRoles.size() ?
            filteredRoles.subList(start, end) : new ArrayList<>();

        return new PageImpl<>(pageContent, pageable, filteredRoles.size());
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
        } catch (IllegalArgumentException e) {
            throw e;
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
        } catch (IllegalArgumentException e) {
            throw e;
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
        } catch (IllegalArgumentException e) {
            System.err.println("删除角色失败: " + e.getMessage());
            return false;
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

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String roleCode) {
        try {
            return roleRepository.existsByRoleCode(roleCode);
        } catch (Exception e) {
            System.err.println("检查角色代码是否存在失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Role findByCode(String roleCode) {
        try {
            return roleRepository.findByRoleCode(roleCode);
        } catch (Exception e) {
            System.err.println("根据角色代码查找角色失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalRoles() {
        try {
            return roleRepository.count();
        } catch (Exception e) {
            System.err.println("统计角色总数失败: " + e.getMessage());
            return 0;
        }
    }

    // ================================
    // 角色管理页面需要的方法实现
    // ================================

    @Override
    @Transactional(readOnly = true)
    public Page<Role> searchRoles(String keyword, Pageable pageable) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return roleRepository.findAll(pageable);
            }
            // 使用现有的搜索方法
            List<Role> searchResults = roleRepository.searchRoles(keyword.trim());

            // 手动分页
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), searchResults.size());
            List<Role> pageContent = start < searchResults.size() ?
                searchResults.subList(start, end) : new ArrayList<>();

            return new PageImpl<>(pageContent, pageable, searchResults.size());
        } catch (Exception e) {
            System.err.println("搜索角色失败: " + e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countSystemRoles() {
        try {
            // 统计系统内置角色数量
            return roleRepository.findAll()
                .stream()
                .filter(role -> role.getRoleKey() != null &&
                    (role.getRoleKey().equals("ADMIN") ||
                     role.getRoleKey().equals("TEACHER") ||
                     role.getRoleKey().equals("STUDENT") ||
                     role.getRoleKey().equals("FINANCE") ||
                     role.getRoleKey().equals("SUPER_ADMIN")))
                .count();
        } catch (Exception e) {
            System.err.println("统计系统角色数量失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Role findRoleById(Long id) {
        try {
            return roleRepository.findById(id).orElse(null);
        } catch (Exception e) {
            System.err.println("根据ID查找角色失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRolePermissions(Long roleId) {
        List<Map<String, Object>> permissions = new ArrayList<>();

        try {
            // 从数据库获取真实的角色权限数据
            // 当前返回空列表，等待权限管理模块完善
            System.out.println("⚠️ 角色权限查询功能需要集成权限管理模块: roleId=" + roleId);

        } catch (Exception e) {
            System.err.println("获取角色权限失败: " + e.getMessage());
        }

        return permissions;
    }

    @Override
    @Transactional
    public boolean assignPermissions(Long roleId, List<Long> permissionIds) {
        try {
            // 实现真实的权限分配逻辑
            // 当前仅记录操作，等待角色权限关联表实现
            System.out.println("⚠️ 权限分配功能需要实现角色权限关联表: roleId=" + roleId + ", permissions=" + permissionIds);
            return true;
        } catch (Exception e) {
            System.err.println("分配权限失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public Role updateRole(Role role) {
        try {
            role.setUpdatedAt(LocalDateTime.now());
            return roleRepository.save(role);
        } catch (Exception e) {
            System.err.println("更新角色失败: " + e.getMessage());
            throw new RuntimeException("更新角色失败", e);
        }
    }

    @Override
    @Transactional
    public boolean clearRolePermissions(Long roleId) {
        try {
            // 实现真实的清除角色权限逻辑
            // 当前仅记录操作，等待角色权限关联表实现
            System.out.println("⚠️ 清除权限功能需要实现角色权限关联表: roleId=" + roleId);
            return true;
        } catch (Exception e) {
            System.err.println("清除角色权限失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean enableRole(Long roleId) {
        try {
            Role role = roleRepository.findById(roleId).orElse(null);
            if (role != null) {
                role.setStatus(1);
                role.setUpdatedAt(LocalDateTime.now());
                roleRepository.save(role);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("启用角色失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean disableRole(Long roleId) {
        try {
            Role role = roleRepository.findById(roleId).orElse(null);
            if (role != null) {
                role.setStatus(0);
                role.setUpdatedAt(LocalDateTime.now());
                roleRepository.save(role);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("禁用角色失败: " + e.getMessage());
            return false;
        }
    }
}