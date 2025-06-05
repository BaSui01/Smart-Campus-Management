package com.campus.application.service;

import com.campus.domain.entity.Permission;

import java.util.List;
import java.util.Map;

/**
 * 权限管理服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-05
 */
public interface PermissionService {

    /**
     * 获取权限统计数据
     *
     * @return 统计数据
     */
    Map<String, Object> getPermissionStats();

    /**
     * 获取所有权限列表
     *
     * @return 权限列表
     */
    List<Permission> getAllPermissions();

    /**
     * 根据ID获取权限
     *
     * @param id 权限ID
     * @return 权限信息
     */
    Permission getPermissionById(Long id);

    /**
     * 根据权限代码获取权限
     *
     * @param permissionCode 权限代码
     * @return 权限信息
     */
    Permission getPermissionByCode(String permissionCode);

    /**
     * 创建权限
     *
     * @param permission 权限信息
     * @return 创建的权限
     */
    Permission createPermission(Permission permission);

    /**
     * 更新权限
     *
     * @param id 权限ID
     * @param permission 权限信息
     * @return 更新的权限
     */
    Permission updatePermission(Long id, Permission permission);

    /**
     * 删除权限
     *
     * @param id 权限ID
     * @return 是否删除成功
     */
    boolean deletePermission(Long id);

    /**
     * 检查权限代码是否存在
     *
     * @param permissionCode 权限代码
     * @return 是否存在
     */
    boolean existsByPermissionCode(String permissionCode);

    /**
     * 根据状态获取权限列表
     *
     * @param status 状态
     * @return 权限列表
     */
    List<Permission> getPermissionsByStatus(Integer status);

    /**
     * 根据资源类型获取权限列表
     *
     * @param resourceType 资源类型
     * @return 权限列表
     */
    List<Permission> getPermissionsByResourceType(String resourceType);

    /**
     * 根据角色ID获取权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> getPermissionsByRoleId(Long roleId);

    /**
     * 根据用户ID获取权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getPermissionsByUserId(Long userId);

    /**
     * 搜索权限
     *
     * @param keyword 关键字
     * @return 权限列表
     */
    List<Permission> searchPermissions(String keyword);

    /**
     * 批量删除权限
     *
     * @param ids 权限ID列表
     * @return 删除成功的数量
     */
    int batchDeletePermissions(List<Long> ids);

    /**
     * 获取权限模块列表
     *
     * @return 模块列表
     */
    List<String> getPermissionModules();
}