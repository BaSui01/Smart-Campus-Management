package com.campus.application.Implement.auth;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.application.service.auth.PermissionService;
import com.campus.domain.entity.auth.Permission;
import com.campus.domain.repository.auth.PermissionRepository;

/**
 * 权限管理服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-05
 */
@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private static final Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public Map<String, Object> getPermissionStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 权限总数
        long totalPermissions = permissionRepository.count();
        stats.put("totalPermissions", totalPermissions);
        
        // 系统权限（status = 1 的权限）
        List<Permission> activePermissions = permissionRepository.findByStatusOrderByPermissionCode(1);
        long systemPermissions = activePermissions.stream()
            .filter(p -> "SYSTEM".equals(p.getResourceType()))
            .count();
        stats.put("systemPermissions", systemPermissions);
        
        // 模块权限（非系统权限）
        long modulePermissions = activePermissions.stream()
            .filter(p -> !"SYSTEM".equals(p.getResourceType()))
            .count();
        stats.put("modulePermissions", modulePermissions);
        
        // 活跃权限（status = 1）
        stats.put("activePermissions", activePermissions.size());
        
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getAllPermissions() {
        try {
            return permissionRepository.findAll();
        } catch (Exception e) {
            logger.error("获取权限列表失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Permission getPermissionById(Long id) {
        try {
            return permissionRepository.findById(id).orElse(null);
        } catch (Exception e) {
            logger.error("根据ID获取权限失败: {}", id, e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Permission getPermissionByCode(String permissionCode) {
        try {
            return permissionRepository.findByPermissionCode(permissionCode).orElse(null);
        } catch (Exception e) {
            logger.error("根据权限代码获取权限失败: {}", permissionCode, e);
            return null;
        }
    }

    @Override
    public Permission createPermission(Permission permission) {
        try {
            // 检查权限代码是否已存在
            if (existsByPermissionCode(permission.getPermissionCode())) {
                throw new RuntimeException("权限代码已存在: " + permission.getPermissionCode());
            }
            
            // 设置创建时间
            permission.setCreatedAt(LocalDateTime.now());
            permission.setUpdatedAt(LocalDateTime.now());
            
            // 默认状态为启用
            if (permission.getStatus() == null) {
                permission.setStatus(1);
            }
            
            return permissionRepository.save(permission);
        } catch (DataIntegrityViolationException e) {
            logger.error("创建权限失败 - 数据完整性违反", e);
            throw new RuntimeException("创建权限失败: 权限代码已存在或数据不完整");
        } catch (DataAccessException e) {
            logger.error("创建权限失败 - 数据访问异常", e);
            throw new RuntimeException("创建权限失败: 数据库访问错误");
        } catch (RuntimeException e) {
            logger.error("创建权限失败 - 未知异常", e);
            throw new RuntimeException("创建权限失败: " + e.getMessage());
        }
    }

    @Override
    public Permission updatePermission(Long id, Permission permission) {
        try {
            Permission existingPermission = getPermissionById(id);
            if (existingPermission == null) {
                throw new RuntimeException("权限不存在: " + id);
            }
            
            // 检查权限代码是否被其他权限使用
            if (!existingPermission.getPermissionCode().equals(permission.getPermissionCode()) &&
                existsByPermissionCode(permission.getPermissionCode())) {
                throw new RuntimeException("权限代码已存在: " + permission.getPermissionCode());
            }
            
            // 更新字段
            existingPermission.setPermissionCode(permission.getPermissionCode());
            existingPermission.setPermissionName(permission.getPermissionName());
            existingPermission.setResourceType(permission.getResourceType());
            existingPermission.setResourceUrl(permission.getResourceUrl());
            existingPermission.setPermissionDesc(permission.getPermissionDesc());
            existingPermission.setStatus(permission.getStatus());
            existingPermission.setUpdatedAt(LocalDateTime.now());
            
            return permissionRepository.save(existingPermission);
        } catch (DataIntegrityViolationException e) {
            logger.error("更新权限失败 - 数据完整性违反: {}", id, e);
            throw new RuntimeException("更新权限失败: 权限代码已存在或数据不完整");
        } catch (DataAccessException e) {
            logger.error("更新权限失败 - 数据访问异常: {}", id, e);
            throw new RuntimeException("更新权限失败: 数据库访问错误");
        } catch (RuntimeException e) {
            logger.error("更新权限失败 - 未知异常: {}", id, e);
            throw new RuntimeException("更新权限失败: " + e.getMessage());
        }
    }

    @Override
    public boolean deletePermission(Long id) {
        try {
            Permission permission = getPermissionById(id);
            if (permission == null) {
                return false;
            }
            
            permissionRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            logger.error("删除权限失败: {}", id, e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPermissionCode(String permissionCode) {
        try {
            return permissionRepository.existsByPermissionCode(permissionCode);
        } catch (Exception e) {
            logger.error("检查权限代码是否存在失败: {}", permissionCode, e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getPermissionsByStatus(Integer status) {
        try {
            return permissionRepository.findByStatusOrderByPermissionCode(status);
        } catch (Exception e) {
            logger.error("根据状态获取权限列表失败: {}", status, e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getPermissionsByResourceType(String resourceType) {
        try {
            return permissionRepository.findByResourceTypeOrderByPermissionCode(resourceType);
        } catch (Exception e) {
            logger.error("根据资源类型获取权限列表失败: {}", resourceType, e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        try {
            return permissionRepository.findByRoleId(roleId);
        } catch (Exception e) {
            logger.error("根据角色ID获取权限列表失败: {}", roleId, e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> getPermissionsByUserId(Long userId) {
        try {
            return permissionRepository.findByUserId(userId);
        } catch (Exception e) {
            logger.error("根据用户ID获取权限列表失败: {}", userId, e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> searchPermissions(String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return getAllPermissions();
            }
            return permissionRepository.findByPermissionNameContaining(keyword.trim());
        } catch (Exception e) {
            logger.error("搜索权限失败: {}", keyword, e);
            return new ArrayList<>();
        }
    }

    @Override
    public int batchDeletePermissions(List<Long> ids) {
        try {
            int deletedCount = 0;
            for (Long id : ids) {
                if (deletePermission(id)) {
                    deletedCount++;
                }
            }
            return deletedCount;
        } catch (Exception e) {
            logger.error("批量删除权限失败", e);
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getPermissionModules() {
        try {
            List<Permission> permissions = getAllPermissions();
            return permissions.stream()
                .map(Permission::getResourceType)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("获取权限模块列表失败", e);
            return Arrays.asList("用户管理", "角色管理", "权限管理", "课程管理", "学生管理", "财务管理");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String permissionCode) {
        return existsByPermissionCode(permissionCode);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalPermissions() {
        try {
            return permissionRepository.count();
        } catch (Exception e) {
            logger.error("统计权限总数失败", e);
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> findAllPermissions() {
        return getAllPermissions();
    }

    @Override
    @Transactional(readOnly = true)
    public Object getPermissionTree() {
        try {
            List<Permission> permissions = getAllPermissions();
            // 构建权限树结构
            return buildPermissionTree(permissions);
        } catch (Exception e) {
            logger.error("获取权限树结构失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Permission> findAllPermissions(org.springframework.data.domain.Pageable pageable) {
        try {
            return permissionRepository.findAll(pageable);
        } catch (Exception e) {
            logger.error("分页获取权限列表失败", e);
            return org.springframework.data.domain.Page.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Permission> searchPermissions(String keyword, org.springframework.data.domain.Pageable pageable) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return findAllPermissions(pageable);
            }
            return permissionRepository.findByPermissionNameContaining(keyword.trim(), pageable);
        } catch (Exception e) {
            logger.error("搜索权限失败: {}", keyword, e);
            return org.springframework.data.domain.Page.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> findPermissionsByModule(String module) {
        try {
            return permissionRepository.findByResourceTypeOrderByPermissionCode(module);
        } catch (Exception e) {
            logger.error("根据模块查找权限失败: {}", module, e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> findPermissionsByType(String type) {
        try {
            return permissionRepository.findByResourceTypeOrderByPermissionCode(type);
        } catch (Exception e) {
            logger.error("根据类型查找权限失败: {}", type, e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countSystemPermissions() {
        try {
            return permissionRepository.countByResourceType("SYSTEM");
        } catch (Exception e) {
            logger.error("统计系统权限数量失败", e);
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> countPermissionsByModule() {
        try {
            return permissionRepository.countByResourceTypeGroupBy();
        } catch (Exception e) {
            logger.error("按模块统计权限数量失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> countPermissionsByType() {
        try {
            return permissionRepository.countByResourceTypeGroupBy();
        } catch (Exception e) {
            logger.error("按类型统计权限数量失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Permission> findPermissionById(Long id) {
        try {
            return permissionRepository.findById(id);
        } catch (Exception e) {
            logger.error("根据ID查找权限失败: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public Permission updatePermission(Permission permission) {
        return updatePermission(permission.getId(), permission);
    }

    @Override
    public boolean enablePermission(Long id) {
        try {
            Permission permission = getPermissionById(id);
            if (permission == null) {
                return false;
            }
            permission.setStatus(1);
            permission.setUpdatedAt(LocalDateTime.now());
            permissionRepository.save(permission);
            return true;
        } catch (Exception e) {
            logger.error("启用权限失败: {}", id, e);
            return false;
        }
    }

    @Override
    public boolean disablePermission(Long id) {
        try {
            Permission permission = getPermissionById(id);
            if (permission == null) {
                return false;
            }
            permission.setStatus(0);
            permission.setUpdatedAt(LocalDateTime.now());
            permissionRepository.save(permission);
            return true;
        } catch (Exception e) {
            logger.error("禁用权限失败: {}", id, e);
            return false;
        }
    }

    /**
     * 构建权限树结构
     */
    private Object buildPermissionTree(List<Permission> permissions) {
        // 按模块分组
        Map<String, List<Permission>> moduleMap = permissions.stream()
            .filter(p -> p.getResourceType() != null)
            .collect(Collectors.groupingBy(Permission::getResourceType));

        List<Object> tree = new ArrayList<>();
        for (Map.Entry<String, List<Permission>> entry : moduleMap.entrySet()) {
            Map<String, Object> moduleNode = new HashMap<>();
            moduleNode.put("name", entry.getKey());
            moduleNode.put("permissions", entry.getValue());
            tree.add(moduleNode);
        }

        return tree;
    }
}