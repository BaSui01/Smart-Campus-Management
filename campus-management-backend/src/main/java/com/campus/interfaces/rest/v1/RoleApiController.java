package com.campus.interfaces.rest.v1;

import com.campus.application.service.RoleService;
import com.campus.domain.entity.Role;
import com.campus.shared.common.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 角色管理API控制器
 * 提供角色相关的REST API接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@RestController
@RequestMapping("/api/v1/roles")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoleApiController {

    private final RoleService roleService;

    public RoleApiController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 获取角色列表（分页）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Role>>> getRoles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String status) {
        try {
            Map<String, Object> params = new HashMap<>();
            if (!search.isEmpty()) {
                params.put("search", search);
            }
            if (!status.isEmpty()) {
                params.put("status", status);
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Role> roles = roleService.findRolesByPage(pageable, params);

            return ResponseEntity.ok(ApiResponse.success(roles));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取角色列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取所有启用的角色
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Role>>> getActiveRoles() {
        try {
            List<Role> roles = roleService.findAllActiveRoles();
            return ResponseEntity.ok(ApiResponse.success(roles));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取启用角色列表失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取角色详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> getRoleById(@PathVariable Long id) {
        try {
            Optional<Role> roleOpt = roleService.findById(id);
            if (roleOpt.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(roleOpt.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取角色详情失败: " + e.getMessage()));
        }
    }

    /**
     * 创建角色
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Role>> createRole(@RequestBody Role role) {
        try {
            Role createdRole = roleService.createRole(role);
            return ResponseEntity.ok(ApiResponse.success("角色创建成功", createdRole));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("创建角色失败: " + e.getMessage()));
        }
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> updateRole(
            @PathVariable Long id, 
            @RequestBody Role role) {
        try {
            Role updatedRole = roleService.updateRole(id, role);
            return ResponseEntity.ok(ApiResponse.success("角色更新成功", updatedRole));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("更新角色失败: " + e.getMessage()));
        }
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteRole(@PathVariable Long id) {
        try {
            boolean success = roleService.deleteRole(id);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("角色删除成功"));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("角色删除失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("删除角色失败: " + e.getMessage()));
        }
    }

    /**
     * 切换角色状态
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<String>> toggleRoleStatus(@PathVariable Long id) {
        try {
            boolean success = roleService.toggleRoleStatus(id);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("角色状态切换成功"));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("角色状态切换失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("切换角色状态失败: " + e.getMessage()));
        }
    }

    /**
     * 搜索角色
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Role>>> searchRoles(
            @RequestParam String keyword) {
        try {
            List<Role> roles = roleService.searchRoles(keyword);
            return ResponseEntity.ok(ApiResponse.success(roles));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("搜索角色失败: " + e.getMessage()));
        }
    }

    /**
     * 获取角色统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<RoleService.RoleStatistics>> getRoleStatistics() {
        try {
            RoleService.RoleStatistics statistics = roleService.getRoleStatistics();
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取角色统计信息失败: " + e.getMessage()));
        }
    }

    /**
     * 根据用户ID获取角色列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Role>>> getRolesByUserId(@PathVariable Long userId) {
        try {
            List<Role> roles = roleService.findRolesByUserId(userId);
            return ResponseEntity.ok(ApiResponse.success(roles));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取用户角色失败: " + e.getMessage()));
        }
    }

    /**
     * 检查角色键是否存在
     */
    @GetMapping("/check-role-key")
    public ResponseEntity<ApiResponse<Boolean>> checkRoleKey(@RequestParam String roleKey) {
        try {
            boolean exists = roleService.existsByRoleKey(roleKey);
            return ResponseEntity.ok(ApiResponse.success(exists));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("检查角色键失败: " + e.getMessage()));
        }
    }

    /**
     * 检查角色名称是否存在
     */
    @GetMapping("/check-role-name")
    public ResponseEntity<ApiResponse<Boolean>> checkRoleName(@RequestParam String roleName) {
        try {
            boolean exists = roleService.existsByRoleName(roleName);
            return ResponseEntity.ok(ApiResponse.success(exists));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("检查角色名称失败: " + e.getMessage()));
        }
    }
}