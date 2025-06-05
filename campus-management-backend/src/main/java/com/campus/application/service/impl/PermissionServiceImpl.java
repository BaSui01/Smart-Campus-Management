package com.campus.application.service.impl;

import com.campus.application.service.PermissionService;
import com.campus.domain.entity.Permission;
import com.campus.domain.repository.PermissionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        } catch (Exception e) {
            logger.error("创建权限失败", e);
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
        } catch (Exception e) {
            logger.error("更新权限失败: {}", id, e);
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
}