package com.campus.application.service;

import com.campus.domain.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 角色服务接口
 * 提供角色管理相关的业务方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
public interface RoleService {

    /**
     * 获取所有角色（分页）
     */
    Page<Role> findAllRoles(Pageable pageable);

    /**
     * 根据条件查询角色（分页）
     */
    Page<Role> findRolesByPage(Pageable pageable, Map<String, Object> params);

    /**
     * 根据ID查找角色
     */
    Optional<Role> findById(Long id);

    /**
     * 根据角色键查找角色
     */
    Optional<Role> findByRoleKey(String roleKey);

    /**
     * 根据角色名称查找角色
     */
    Optional<Role> findByRoleName(String roleName);

    /**
     * 创建角色
     */
    Role createRole(Role role);

    /**
     * 更新角色
     */
    Role updateRole(Long id, Role roleDetails);

    /**
     * 删除角色
     */
    boolean deleteRole(Long id);

    /**
     * 切换角色状态
     */
    boolean toggleRoleStatus(Long id);

    /**
     * 获取所有启用的角色
     */
    List<Role> findAllActiveRoles();

    /**
     * 搜索角色
     */
    List<Role> searchRoles(String keyword);

    /**
     * 统计角色数量
     */
    long countRoles();

    /**
     * 获取角色统计信息
     */
    RoleStatistics getRoleStatistics();

    /**
     * 根据用户ID获取角色列表
     */
    List<Role> findRolesByUserId(Long userId);

    /**
     * 检查角色键是否存在
     */
    boolean existsByRoleKey(String roleKey);

    /**
     * 检查角色名称是否存在
     */
    boolean existsByRoleName(String roleName);

    /**
     * 角色统计信息
     */
    class RoleStatistics {
        private long totalRoles;
        private long activeRoles;
        private long inactiveRoles;
        private long systemRoles;

        public RoleStatistics() {}

        public RoleStatistics(long totalRoles, long activeRoles, long inactiveRoles, long systemRoles) {
            this.totalRoles = totalRoles;
            this.activeRoles = activeRoles;
            this.inactiveRoles = inactiveRoles;
            this.systemRoles = systemRoles;
        }

        // Getters and Setters
        public long getTotalRoles() { return totalRoles; }
        public void setTotalRoles(long totalRoles) { this.totalRoles = totalRoles; }

        public long getActiveRoles() { return activeRoles; }
        public void setActiveRoles(long activeRoles) { this.activeRoles = activeRoles; }

        public long getInactiveRoles() { return inactiveRoles; }
        public void setInactiveRoles(long inactiveRoles) { this.inactiveRoles = inactiveRoles; }

        public long getSystemRoles() { return systemRoles; }
        public void setSystemRoles(long systemRoles) { this.systemRoles = systemRoles; }
    }
}