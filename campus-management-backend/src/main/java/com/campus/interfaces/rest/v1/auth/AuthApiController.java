package com.campus.interfaces.rest.v1.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.campus.application.service.auth.UserService;
import com.campus.domain.entity.auth.User;
import com.campus.shared.common.ApiResponse;
import com.campus.shared.util.JwtUtil;
import com.campus.interfaces.rest.common.BaseController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 认证API控制器
 * 处理前端用户登录、注册、JWT认证等API
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "认证API", description = "用户认证相关REST API接口")
public class AuthApiController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录获取JWT令牌")
    public ApiResponse<Map<String, Object>> login(@Parameter(description = "登录信息") @Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("用户登录请求: username={}, userType={}", loginRequest.getUsername(), loginRequest.getUserType());

            // 验证用户凭据
            User user = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            if (user == null) {
                return ApiResponse.error(401, "用户名或密码错误");
            }

            // 检查用户状态
            if (user.getStatus() != 1) {
                return ApiResponse.error(403, "账户已被禁用");
            }

            // 生成JWT令牌
            String token = jwtUtil.generateToken(user.getUsername());

            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("tokenType", "Bearer");
            result.put("expiresIn", jwtUtil.getExpirationTime());

            // 用户信息（不包含敏感信息）
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("realName", user.getRealName());
            userInfo.put("email", user.getEmail());
            userInfo.put("phone", user.getPhone());
            userInfo.put("gender", user.getGender());
            userInfo.put("avatarUrl", user.getAvatarUrl());
            userInfo.put("userType", loginRequest.getUserType()); // 返回前端传递的用户类型

            result.put("user", userInfo);

            log.info("用户登录成功: username={}, userType={}", loginRequest.getUsername(), loginRequest.getUserType());
            return ApiResponse.success("登录成功", result);
        } catch (Exception e) {
            log.error("用户登录失败: username={}, error={}", loginRequest.getUsername(), e.getMessage(), e);
            return ApiResponse.error(500, "登录失败：" + e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册")
    public ApiResponse<Map<String, Object>> register(@Parameter(description = "注册信息") @Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // 检查用户名是否已存在
            if (userService.findByUsername(registerRequest.getUsername()).isPresent()) {
                return ApiResponse.error(400, "用户名已存在");
            }

            // 检查邮箱是否已存在
            if (userService.findByEmail(registerRequest.getEmail()).isPresent()) {
                return ApiResponse.error(400, "邮箱已被注册");
            }

            // 创建新用户
            User newUser = new User();
            newUser.setUsername(registerRequest.getUsername());
            newUser.setPassword(registerRequest.getPassword()); // 密码会在service层加密
            newUser.setEmail(registerRequest.getEmail());
            newUser.setRealName(registerRequest.getRealName());
            newUser.setPhone(registerRequest.getPhone());
            newUser.setGender(registerRequest.getGender());
            newUser.setStatus(1); // 默认启用

            User createdUser = userService.createUser(newUser);

            // 生成JWT令牌
            String token = jwtUtil.generateToken(createdUser.getUsername());

            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("tokenType", "Bearer");
            result.put("expiresIn", jwtUtil.getExpirationTime());
            
            // 用户信息（不包含敏感信息）
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", createdUser.getId());
            userInfo.put("username", createdUser.getUsername());
            userInfo.put("realName", createdUser.getRealName());
            userInfo.put("email", createdUser.getEmail());
            userInfo.put("phone", createdUser.getPhone());
            userInfo.put("gender", createdUser.getGender());
            userInfo.put("avatarUrl", createdUser.getAvatarUrl());
            
            result.put("user", userInfo);

            return ApiResponse.success("注册成功", result);
        } catch (Exception e) {
            return ApiResponse.error(500, "注册失败：" + e.getMessage());
        }
    }

    /**
     * 刷新令牌
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "刷新JWT令牌")
    public ApiResponse<Map<String, Object>> refreshToken(@Parameter(description = "刷新令牌") @RequestParam String refreshToken) {
        try {
            // 验证刷新令牌
            if (!jwtUtil.validateToken(refreshToken)) {
                return ApiResponse.error(401, "刷新令牌无效");
            }

            // 从令牌中获取用户名
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            
            // 生成新的访问令牌
            String newToken = jwtUtil.generateToken(username);

            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("token", newToken);
            result.put("tokenType", "Bearer");
            result.put("expiresIn", jwtUtil.getExpirationTime());

            return ApiResponse.success("令牌刷新成功", result);
        } catch (Exception e) {
            return ApiResponse.error(500, "令牌刷新失败：" + e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出")
    public ApiResponse<Void> logout(@Parameter(description = "访问令牌") @RequestHeader("Authorization") String token) {
        try {
            // 这里可以将令牌加入黑名单或执行其他登出逻辑
            // 目前JWT是无状态的，客户端删除令牌即可
            return ApiResponse.success("登出成功");
        } catch (Exception e) {
            return ApiResponse.error(500, "登出失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的信息")
    public ApiResponse<Map<String, Object>> getCurrentUser(@Parameter(description = "访问令牌") @RequestHeader("Authorization") String token) {
        try {
            // 从令牌中获取用户名
            String actualToken = token.replace("Bearer ", "");
            String username = jwtUtil.getUsernameFromToken(actualToken);
            
            // 获取用户信息
            User user = userService.findByUsername(username).orElse(null);
            if (user == null) {
                return ApiResponse.error(404, "用户不存在");
            }

            // 构建用户信息（不包含敏感信息）
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("realName", user.getRealName());
            userInfo.put("email", user.getEmail());
            userInfo.put("phone", user.getPhone());
            userInfo.put("gender", user.getGender());
            userInfo.put("avatarUrl", user.getAvatarUrl());
            userInfo.put("status", user.getStatus());
            userInfo.put("createdTime", user.getCreatedTime());
            userInfo.put("lastLoginTime", user.getLastLoginTime());

            return ApiResponse.success("获取用户信息成功", userInfo);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    @Operation(summary = "修改密码", description = "修改当前用户密码")
    public ApiResponse<Void> changePassword(
            @Parameter(description = "访问令牌") @RequestHeader("Authorization") String token,
            @Parameter(description = "密码修改信息") @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            // 从令牌中获取用户名
            String actualToken = token.replace("Bearer ", "");
            String username = jwtUtil.getUsernameFromToken(actualToken);
            
            // 获取用户信息
            User user = userService.findByUsername(username).orElse(null);
            if (user == null) {
                return ApiResponse.error(404, "用户不存在");
            }

            // 修改密码
            boolean result = userService.changePassword(user.getId(), changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());
            if (result) {
                return ApiResponse.success("密码修改成功");
            } else {
                return ApiResponse.error(400, "旧密码错误");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "密码修改失败：" + e.getMessage());
        }
    }

    // ==================== 统计端点 ====================

    /**
     * 获取认证统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取认证统计信息", description = "获取认证模块的统计数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAuthStats() {
        try {
            log.info("获取认证统计信息");

            Map<String, Object> stats = new HashMap<>();

            // 基础统计
            long totalUsers = userService.countTotalUsers();
            stats.put("totalUsers", totalUsers);

            long activeUsers = userService.countActiveUsers();
            stats.put("activeUsers", activeUsers);

            long inactiveUsers = totalUsers - activeUsers;
            stats.put("inactiveUsers", inactiveUsers);

            // 时间统计（简化实现）
            stats.put("todayLogins", 0L);
            stats.put("weekLogins", 0L);
            stats.put("monthLogins", 0L);

            // 登录方式统计（简化实现）
            Map<String, Long> loginTypeStats = new HashMap<>();
            loginTypeStats.put("username", totalUsers);
            loginTypeStats.put("email", 0L);
            loginTypeStats.put("phone", 0L);
            stats.put("loginTypeStats", loginTypeStats);

            // 用户状态统计
            Map<String, Long> statusStats = new HashMap<>();
            statusStats.put("active", activeUsers);
            statusStats.put("inactive", inactiveUsers);
            statusStats.put("locked", 0L);
            stats.put("statusStats", statusStats);

            // 最近活动（简化实现）
            List<Map<String, Object>> recentActivity = new ArrayList<>();
            stats.put("recentActivity", recentActivity);

            return success("获取认证统计信息成功", stats);

        } catch (Exception e) {
            log.error("获取认证统计信息失败: ", e);
            return error("获取认证统计信息失败: " + e.getMessage());
        }
    }

    // ==================== 批量操作端点 ====================

    /**
     * 批量锁定用户
     */
    @PutMapping("/batch/lock")
    @Operation(summary = "批量锁定用户", description = "批量锁定用户账户")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchLockUsers(
            @Parameter(description = "用户ID列表") @RequestBody List<Long> ids) {

        try {
            logOperation("批量锁定用户", ids.size());

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("用户ID列表不能为空");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "用户");
            }

            // 执行批量锁定
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Long id : ids) {
                try {
                    userService.updateUserStatus(id, 0); // 0表示禁用
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("用户ID " + id + ": " + e.getMessage());
                    log.warn("锁定用户{}失败: {}", id, e.getMessage());
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", ids.size());
            responseData.put("failReasons", failReasons);

            if (failCount == 0) {
                return success("批量锁定用户成功", responseData);
            } else if (successCount > 0) {
                return success("批量锁定用户部分成功", responseData);
            } else {
                return error("批量锁定用户失败");
            }

        } catch (Exception e) {
            log.error("批量锁定用户失败: ", e);
            return error("批量锁定用户失败: " + e.getMessage());
        }
    }

    /**
     * 批量解锁用户
     */
    @PutMapping("/batch/unlock")
    @Operation(summary = "批量解锁用户", description = "批量解锁用户账户")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchUnlockUsers(
            @Parameter(description = "用户ID列表") @RequestBody List<Long> ids) {

        try {
            logOperation("批量解锁用户", ids.size());

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("用户ID列表不能为空");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "用户");
            }

            // 执行批量解锁
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Long id : ids) {
                try {
                    userService.updateUserStatus(id, 1); // 1表示启用
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("用户ID " + id + ": " + e.getMessage());
                    log.warn("解锁用户{}失败: {}", id, e.getMessage());
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", ids.size());
            responseData.put("failReasons", failReasons);

            if (failCount == 0) {
                return success("批量解锁用户成功", responseData);
            } else if (successCount > 0) {
                return success("批量解锁用户部分成功", responseData);
            } else {
                return error("批量解锁用户失败");
            }

        } catch (Exception e) {
            log.error("批量解锁用户失败: ", e);
            return error("批量解锁用户失败: " + e.getMessage());
        }
    }

    // ========== 请求对象 ==========

    public static class LoginRequest {
        private String username;
        private String password;
        private String userType;

        // Getter和Setter
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
    }

    public static class RegisterRequest {
        private String username;
        private String password;
        private String email;
        private String realName;
        private String phone;
        private String gender;

        // Getter和Setter
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
    }

    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;

        // Getter和Setter
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
