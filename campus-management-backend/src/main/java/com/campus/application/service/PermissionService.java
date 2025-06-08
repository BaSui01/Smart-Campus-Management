package com.campus.application.service;

import com.campus.domain.entity.Permission;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
     * 分页获取所有权限
     */
    org.springframework.data.domain.Page<Permission> findAllPermissions(org.springframework.data.domain.Pageable pageable);

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

    /**
     * 根据权限代码检查是否存在
     */
    boolean existsByCode(String permissionCode);

    /**
     * 统计权限总数
     */
    long countTotalPermissions();

    /**
     * 获取所有权限
     */
    List<Permission> findAllPermissions();

    // ================================
    // Web控制器需要的方法
    // ================================

    /**
     * 搜索权限（带分页）
     *
     * @param keyword 关键字
     * @param pageable 分页参数
     * @return 权限分页结果
     */
    org.springframework.data.domain.Page<Permission> searchPermissions(String keyword, org.springframework.data.domain.Pageable pageable);

    /**
     * 根据模块查找权限
     *
     * @param module 模块名称
     * @return 权限列表
     */
    List<Permission> findPermissionsByModule(String module);

    /**
     * 根据类型查找权限
     *
     * @param type 权限类型
     * @return 权限列表
     */
    List<Permission> findPermissionsByType(String type);

    /**
     * 统计系统权限数量
     *
     * @return 系统权限数量
     */
    long countSystemPermissions();

    /**
     * 按模块统计权限数量
     *
     * @return 模块统计结果
     */
    List<Object[]> countPermissionsByModule();

    /**
     * 按类型统计权限数量
     *
     * @return 类型统计结果
     */
    List<Object[]> countPermissionsByType();

    /**
     * 根据ID查找权限
     *
     * @param id 权限ID
     * @return 权限信息
     */
    Optional<Permission> findPermissionById(Long id);

    /**
     * 更新权限（不带ID参数）
     *
     * @param permission 权限信息
     * @return 更新的权限
     */
    Permission updatePermission(Permission permission);

    /**
     * 启用权限
     *
     * @param id 权限ID
     * @return 是否成功
     */
    boolean enablePermission(Long id);

    /**
     * 禁用权限
     *
     * @param id 权限ID
     * @return 是否成功
     */
    boolean disablePermission(Long id);

    /**
     * 获取权限树结构
     *
     * @return 权限树结构
     */
    Object getPermissionTree();
}