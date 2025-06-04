package com.campus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campus.common.ApiResponse;
import com.campus.entity.User;
import com.campus.service.UserService;
import com.campus.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 用户控制器
 * 处理用户相关的API请求
 *
 * @author campus
 * @since 2025-06-04
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@Valid @RequestBody LoginRequest loginRequest,
                                                    HttpServletRequest request) {
        try {
            // 用户认证
            User user = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

            if (user != null) {
                // 检查用户状态
                if (user.getStatus() != 1) {
                    return ResponseEntity.ok(ApiResponse.error("用户账户已被禁用"));
                }

                // 生成JWT Token
                String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "USER");

                // 更新最后登录信息
                userService.updateLastLoginInfo(user.getId(), request.getRemoteAddr());

                // 构建响应数据
                LoginResponse response = new LoginResponse();
                response.setToken(token);
                response.setUser(user);

                return ResponseEntity.ok(ApiResponse.success(response));
            } else {
                return ResponseEntity.ok(ApiResponse.error("用户名或密码错误"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("登录失败：" + e.getMessage()));
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // 检查用户名是否已存在
            if (userService.existsByUsername(registerRequest.getUsername())) {
                return ResponseEntity.ok(ApiResponse.error("用户名已存在"));
            }

            // 检查邮箱是否已存在
            if (registerRequest.getEmail() != null && userService.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.ok(ApiResponse.error("邮箱已存在"));
            }

            // 创建新用户
            User newUser = new User();
            newUser.setUsername(registerRequest.getUsername());
            newUser.setPassword(registerRequest.getPassword());
            newUser.setRealName(registerRequest.getRealName());
            newUser.setEmail(registerRequest.getEmail());
            newUser.setPhone(registerRequest.getPhone());
            newUser.setStatus(1); // 默认激活状态

            User createdUser = userService.createUser(newUser);

            return ResponseEntity.ok(ApiResponse.success(createdUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("注册失败：" + e.getMessage()));
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(@RequestParam Long userId) {
        try {
            User user = userService.getUserById(userId);

            if (user != null) {
                return ResponseEntity.ok(ApiResponse.success(user));
            } else {
                return ResponseEntity.ok(ApiResponse.error("用户不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("获取用户信息失败：" + e.getMessage()));
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Long id,
                                                       @Valid @RequestBody UpdateUserRequest updateRequest) {
        try {
            User userDetails = new User();
            userDetails.setRealName(updateRequest.getRealName());
            userDetails.setEmail(updateRequest.getEmail());
            userDetails.setPhone(updateRequest.getPhone());

            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(ApiResponse.success(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("更新用户信息失败：" + e.getMessage()));
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@PathVariable Long id,
                                                             @Valid @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(id, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok(ApiResponse.success("密码修改成功"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("密码修改失败：" + e.getMessage()));
        }
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<Object>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            // 验证当前Token
            if (jwtUtil.validateToken(request.getToken())) {
                Long userId = jwtUtil.getUserIdFromToken(request.getToken());
                String username = jwtUtil.getUsernameFromToken(request.getToken());

                // 生成新Token
                String newToken = jwtUtil.generateToken(userId, username, "USER");

                RefreshTokenResponse response = new RefreshTokenResponse();
                response.setToken(newToken);

                return ResponseEntity.ok(ApiResponse.success(response));
            } else {
                return ResponseEntity.ok(ApiResponse.error("Token无效或已过期"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("刷新Token失败：" + e.getMessage()));
        }
    }

    // 内部类定义请求和响应对象
    public static class LoginRequest {
        private String username;
        private String password;

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private String token;
        private User user;

        // Getters and Setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
    }

    public static class RegisterRequest {
        private String username;
        private String password;
        private String realName;
        private String email;
        private String phone;

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    public static class UpdateUserRequest {
        private String realName;
        private String email;
        private String phone;

        // Getters and Setters
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;

        // Getters and Setters
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class RefreshTokenRequest {
        private String token;

        // Getters and Setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }

    public static class RefreshTokenResponse {
        private String token;

        // Getters and Setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }
}
