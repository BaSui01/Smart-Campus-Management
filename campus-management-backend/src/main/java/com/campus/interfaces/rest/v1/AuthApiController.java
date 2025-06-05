package com.campus.interfaces.rest.v1;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.campus.application.service.UserService;
import com.campus.shared.common.ApiResponse;
import com.campus.domain.entity.User;
import com.campus.shared.util.JwtUtil;

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
@RequestMapping("/api/auth")
@Tag(name = "认证API", description = "用户认证相关REST API接口")
public class AuthApiController {

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
            
            result.put("user", userInfo);

            return ApiResponse.success("登录成功", result);
        } catch (Exception e) {
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

    // ========== 请求对象 ==========

    public static class LoginRequest {
        private String username;
        private String password;

        // Getter和Setter
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
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
