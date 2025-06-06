package com.campus.interfaces.rest.v1;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.campus.application.service.UserService;
import com.campus.shared.common.ApiResponse;
import com.campus.domain.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 用户管理API控制器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理API", description = "用户信息管理REST API接口")
@SecurityRequirement(name = "Bearer")
public class UserApiController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取用户列表", description = "分页查询用户信息")
    public ApiResponse<Map<String, Object>> getUsers(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "真实姓名") @RequestParam(required = false) String realName,
            @Parameter(description = "角色") @RequestParam(required = false) String role,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (username != null && !username.isEmpty()) {
            params.put("username", username);
        }
        if (realName != null && !realName.isEmpty()) {
            params.put("realName", realName);
        }
        if (role != null && !role.isEmpty()) {
            params.put("role", role);
        }
        if (status != null) {
            params.put("status", status);
        }

        // 执行分页查询
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> pageResult = userService.findUsersByPage(pageable, params);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotalElements());
        result.put("pages", pageResult.getTotalPages());
        result.put("current", pageResult.getNumber() + 1);
        result.put("size", pageResult.getSize());
        result.put("records", pageResult.getContent());

        return ApiResponse.success("获取用户列表成功", result);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "根据ID查询用户详细信息")
    public ApiResponse<User> getUserById(@Parameter(description = "用户ID") @PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null) {
            return ApiResponse.success(user);
        } else {
            return ApiResponse.error(404, "用户不存在");
        }
    }

    /**
     * 根据用户名查询用户
     */
    @GetMapping("/username/{username}")
    @Operation(summary = "根据用户名查询用户", description = "根据用户名查询用户信息")
    public ApiResponse<User> getUserByUsername(@Parameter(description = "用户名") @PathVariable String username) {
        Optional<User> user = userService.findByUsername(username);
        if (user.isPresent()) {
            return ApiResponse.success(user.get());
        } else {
            return ApiResponse.error(404, "用户不存在");
        }
    }

    /**
     * 根据邮箱查询用户
     */
    @GetMapping("/email/{email}")
    @Operation(summary = "根据邮箱查询用户", description = "根据邮箱查询用户信息")
    public ApiResponse<User> getUserByEmail(@Parameter(description = "邮箱") @PathVariable String email) {
        Optional<User> user = userService.findByEmail(email);
        if (user.isPresent()) {
            return ApiResponse.success(user.get());
        } else {
            return ApiResponse.error(404, "用户不存在");
        }
    }

    /**
     * 搜索用户
     */
    @GetMapping("/search")
    @Operation(summary = "搜索用户", description = "根据关键词搜索用户")
    public ApiResponse<List<User>> searchUsers(@Parameter(description = "关键词") @RequestParam(required = false, defaultValue = "") String keyword) {
        List<User> users = userService.searchUsers(keyword);
        return ApiResponse.success(users);
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取用户统计信息", description = "获取用户统计数据")
    public ApiResponse<UserService.UserStatistics> getUserStatistics() {
        UserService.UserStatistics stats = userService.getUserStatistics();
        return ApiResponse.success(stats);
    }

    /**
     * 创建用户
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "添加新用户信息")
    public ApiResponse<User> createUser(@Parameter(description = "用户信息") @Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ApiResponse.success("创建用户成功", createdUser);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "创建用户失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息", description = "修改用户信息")
    public ApiResponse<Void> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "用户信息") @Valid @RequestBody User user) {
        try {
            user.setId(id);
            boolean result = userService.updateUser(user);
            if (result) {
                return ApiResponse.success("更新用户信息成功");
            } else {
                return ApiResponse.error(404, "用户不存在");
            }
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "更新用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "删除指定用户")
    public ApiResponse<Void> deleteUser(@Parameter(description = "用户ID") @PathVariable Long id) {
        boolean result = userService.deleteUser(id);
        if (result) {
            return ApiResponse.success("删除用户成功");
        } else {
            return ApiResponse.error(404, "用户不存在");
        }
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除用户", description = "批量删除多个用户")
    public ApiResponse<Void> batchDeleteUsers(@Parameter(description = "用户ID列表") @RequestBody List<Long> ids) {
        boolean result = userService.batchDeleteUsers(ids);
        if (result) {
            return ApiResponse.success("批量删除用户成功");
        } else {
            return ApiResponse.error(500, "批量删除用户失败");
        }
    }

    /**
     * 重置用户密码
     */
    @PostMapping("/{id}/reset-password")
    @Operation(summary = "重置用户密码", description = "重置指定用户的密码")
    public ApiResponse<Void> resetPassword(@Parameter(description = "用户ID") @PathVariable Long id) {
        try {
            boolean result = userService.resetPassword(id);
            if (result) {
                return ApiResponse.success("重置密码成功");
            } else {
                return ApiResponse.error(404, "用户不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "重置密码失败：" + e.getMessage());
        }
    }

    /**
     * 修改用户密码
     */
    @PostMapping("/{id}/change-password")
    @Operation(summary = "修改用户密码", description = "修改用户密码")
    public ApiResponse<Void> changePassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "旧密码") @RequestParam String oldPassword,
            @Parameter(description = "新密码") @RequestParam String newPassword) {
        try {
            boolean result = userService.changePassword(id, oldPassword, newPassword);
            if (result) {
                return ApiResponse.success("修改密码成功");
            } else {
                return ApiResponse.error(400, "旧密码错误或用户不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "修改密码失败：" + e.getMessage());
        }
    }

    /**
     * 启用/禁用用户
     */
    @PostMapping("/{id}/toggle-status")
    @Operation(summary = "启用/禁用用户", description = "切换用户状态")
    public ApiResponse<Void> toggleUserStatus(@Parameter(description = "用户ID") @PathVariable Long id) {
        try {
            boolean result = userService.toggleUserStatus(id);
            if (result) {
                return ApiResponse.success("用户状态切换成功");
            } else {
                return ApiResponse.error(404, "用户不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "用户状态切换失败：" + e.getMessage());
        }
    }

    /**
     * 导出用户数据
     */
    @GetMapping("/export")
    @Operation(summary = "导出用户数据", description = "导出用户数据")
    public ApiResponse<List<User>> exportUsers(
            @Parameter(description = "角色") @RequestParam(required = false) String role,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (role != null && !role.isEmpty()) {
            params.put("role", role);
        }
        if (status != null) {
            params.put("status", status);
        }

        List<User> users = userService.exportUsers(params);
        return ApiResponse.success("导出用户数据成功", users);
    }
}
