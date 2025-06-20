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
            logger.info("创建权限: code={}, name={}", permission.getPermissionCode(), permission.getPermissionName());
            
            // 1. 数据验证
            validatePermissionData(permission);
            
            // 2. 检查权限代码是否已存在
            if (existsByPermissionCode(permission.getPermissionCode())) {
                throw new RuntimeException("权限代码已存在: " + permission.getPermissionCode());
            }
            
            // 3. 智能权限代码生成
            if (permission.getPermissionCode() == null || permission.getPermissionCode().trim().isEmpty()) {
                permission.setPermissionCode(generatePermissionCode(permission));
            }
            
            // 4. 设置默认值和元数据
            setPermissionDefaults(permission);
            
            // 5. 保存权限
            Permission savedPermission = permissionRepository.save(permission);
            
            // 6. 异步更新权限缓存
            updatePermissionCache(savedPermission);
            
            logger.info("权限创建成功: id={}, code={}", savedPermission.getId(), savedPermission.getPermissionCode());
            return savedPermission;
            
        } catch (DataIntegrityViolationException e) {
            logger.error("创建权限失败 - 数据完整性违反: {}", permission.getPermissionCode(), e);
            throw new RuntimeException("创建权限失败: 权限代码已存在或数据不完整");
        } catch (DataAccessException e) {
            logger.error("创建权限失败 - 数据访问异常: {}", permission.getPermissionCode(), e);
            throw new RuntimeException("创建权限失败: 数据库访问错误");
        } catch (RuntimeException e) {
            logger.error("创建权限失败: {}", permission.getPermissionCode(), e);
            throw new RuntimeException("创建权限失败: " + e.getMessage());
        }
    }

    /**
     * 验证权限数据
     */
    private void validatePermissionData(Permission permission) {
        if (permission == null) {
            throw new IllegalArgumentException("权限对象不能为空");
        }
        
        if (permission.getPermissionName() == null || permission.getPermissionName().trim().isEmpty()) {
            throw new IllegalArgumentException("权限名称不能为空");
        }
        
        if (permission.getResourceType() == null || permission.getResourceType().trim().isEmpty()) {
            throw new IllegalArgumentException("资源类型不能为空");
        }
        
        // 验证权限名称长度
        if (permission.getPermissionName().length() > 100) {
            throw new IllegalArgumentException("权限名称长度不能超过100个字符");
        }
        
        // 验证资源URL格式
        if (permission.getResourceUrl() != null && !permission.getResourceUrl().trim().isEmpty()) {
            if (!isValidResourceUrl(permission.getResourceUrl())) {
                throw new IllegalArgumentException("资源URL格式不正确");
            }
        }
    }

    /**
     * 生成权限代码
     */
    private String generatePermissionCode(Permission permission) {
        String resourceType = permission.getResourceType().toUpperCase();
        String permissionName = permission.getPermissionName().replaceAll("[^a-zA-Z0-9]", "_").toUpperCase();
        
        String baseCode = resourceType + ":" + permissionName;
        
        // 确保代码唯一性
        String finalCode = baseCode;
        int counter = 1;
        while (existsByPermissionCode(finalCode)) {
            finalCode = baseCode + "_" + counter;
            counter++;
        }
        
        return finalCode;
    }

    /**
     * 设置权限默认值
     */
    private void setPermissionDefaults(Permission permission) {
        LocalDateTime now = LocalDateTime.now();
        
        permission.setCreatedAt(now);
        permission.setUpdatedAt(now);
        
        // 默认状态为启用
        if (permission.getStatus() == null) {
            permission.setStatus(1);
        }
        
        // 设置默认描述
        if (permission.getPermissionDesc() == null || permission.getPermissionDesc().trim().isEmpty()) {
            permission.setPermissionDesc(generateDefaultDescription(permission));
        }
    }

    /**
     * 生成默认描述
     */
    private String generateDefaultDescription(Permission permission) {
        return String.format("对%s资源的%s权限",
            permission.getResourceType(), permission.getPermissionName());
    }

    /**
     * 验证资源URL格式
     */
    private boolean isValidResourceUrl(String url) {
        // 简单的URL格式验证
        return url.matches("^/.*") || url.matches("^https?://.*");
    }

    /**
     * 更新权限缓存
     */
    private void updatePermissionCache(Permission permission) {
        try {
            // 这里可以集成Redis缓存更新
            logger.debug("更新权限缓存: {}", permission.getPermissionCode());
        } catch (Exception e) {
            logger.warn("更新权限缓存失败: {}", permission.getPermissionCode(), e);
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