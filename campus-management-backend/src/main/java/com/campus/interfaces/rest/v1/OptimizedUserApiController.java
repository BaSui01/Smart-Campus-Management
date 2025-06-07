package com.campus.interfaces.rest.v1;

import com.campus.application.service.UserService;
import com.campus.application.service.RoleService;
import com.campus.common.controller.BaseController;
import com.campus.shared.common.ApiResponse;
import com.campus.domain.entity.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 优化的用户管理API控制器
 * 使用真实数据库数据，优化性能，确保UTF-8编码
 * 
 * @author Campus Management System
 * @since 2025-06-07
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "用户管理", description = "用户信息的增删改查操作")
public class OptimizedUserApiController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(OptimizedUserApiController.class);

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public OptimizedUserApiController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    // 邮箱格式验证正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // 手机号格式验证正则表达式
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^1[3-9]\\d{9}$"
    );

    /**
     * 分页查询用户列表
     */
    @GetMapping
    @Operation(summary = "分页查询用户列表", description = "支持按用户名、姓名、邮箱等条件搜索")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getUsers(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "搜索关键词") @RequestParam(defaultValue = "") String search,
            @Parameter(description = "角色筛选") @RequestParam(defaultValue = "") String role,
            @Parameter(description = "状态筛选") @RequestParam(defaultValue = "") String status,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            logOperation("查询用户列表", page, size, search, role, status);

            // 验证分页参数
            validatePageParams(page, size);

            // 创建分页对象
            Pageable pageable = createPageable(page, size, sortBy, sortDir);

            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            if (StringUtils.hasText(search)) {
                params.put("search", processSearchKeyword(search));
            }
            if (StringUtils.hasText(role)) {
                params.put("role", ensureUtf8(role));
            }
            if (StringUtils.hasText(status)) {
                params.put("status", Integer.valueOf(status));
            }

            // 执行查询
            Page<User> userPage = userService.findUsersByPage(pageable, params);

            return successPage(userPage, "查询用户列表成功");

        } catch (Exception e) {
            log.error("查询用户列表失败: ", e);
            return error("查询用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询用户详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询用户详情", description = "根据用户ID查询详细信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<User>> getUserById(
            @Parameter(description = "用户ID") @PathVariable Long id) {

        try {
            logOperation("查询用户详情", id);
            validateId(id, "用户");

            Optional<User> user = userService.findByIdOptional(id);
            if (user.isPresent()) {
                // 清除敏感信息
                User safeUser = user.get();
                safeUser.setPassword(null);
                return success(safeUser, "查询用户详情成功");
            } else {
                return notFound("用户不存在");
            }

        } catch (Exception e) {
            log.error("查询用户详情失败: ", e);
            return error("查询用户详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建新用户
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "添加新的用户信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> createUser(
            @Parameter(description = "用户信息") @Valid @RequestBody User user) {

        try {
            logOperation("创建用户", user.getUsername(), user.getRealName());

            // 验证用户数据
            validateUserData(user, true);

            // 确保UTF-8编码
            user.setUsername(ensureUtf8(user.getUsername()));
            user.setRealName(ensureUtf8(user.getRealName()));
            user.setEmail(ensureUtf8(user.getEmail()));

            // 加密密码
            if (StringUtils.hasText(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                // 设置默认密码
                user.setPassword(passwordEncoder.encode("123456"));
            }

            // 设置创建时间
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            // 保存用户
            User savedUser = userService.save(user);

            // 清除敏感信息
            savedUser.setPassword(null);

            return success(savedUser, "用户创建成功");

        } catch (Exception e) {
            log.error("创建用户失败: ", e);
            return error("创建用户失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息", description = "修改用户的详细信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "用户信息") @Valid @RequestBody User user) {

        try {
            logOperation("更新用户信息", id, user.getUsername());
            validateId(id, "用户");

            // 检查用户是否存在
            Optional<User> existingUser = userService.findByIdOptional(id);
            if (!existingUser.isPresent()) {
                return notFound("用户不存在");
            }

            // 验证用户数据
            validateUserData(user, false);

            // 确保UTF-8编码
            user.setUsername(ensureUtf8(user.getUsername()));
            user.setRealName(ensureUtf8(user.getRealName()));
            user.setEmail(ensureUtf8(user.getEmail()));

            // 设置ID和更新时间
            user.setId(id);
            user.setUpdatedAt(LocalDateTime.now());

            // 如果没有提供密码，保持原密码
            if (!StringUtils.hasText(user.getPassword())) {
                user.setPassword(existingUser.get().getPassword());
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            // 保存用户
            User savedUser = userService.save(user);

            // 清除敏感信息
            savedUser.setPassword(null);

            return success(savedUser, "用户信息更新成功");

        } catch (Exception e) {
            log.error("更新用户信息失败: ", e);
            return error("更新用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "根据ID删除用户信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {

        try {
            logOperation("删除用户", id);
            validateId(id, "用户");

            // 检查用户是否存在
            Optional<User> user = userService.findByIdOptional(id);
            if (!user.isPresent()) {
                return notFound("用户不存在");
            }

            // 检查是否为管理员用户
            if ("admin".equals(user.get().getUsername())) {
                return error("不能删除管理员用户");
            }

            // 删除用户
            userService.deleteById(id);

            return success("用户删除成功");

        } catch (Exception e) {
            log.error("删除用户失败: ", e);
            return error("删除用户失败: " + e.getMessage());
        }
    }

    /**
     * 切换用户状态
     */
    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "切换用户状态", description = "启用或禁用用户")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> toggleUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long id) {

        try {
            logOperation("切换用户状态", id);
            validateId(id, "用户");

            // 检查用户是否存在
            Optional<User> userOpt = userService.findByIdOptional(id);
            if (!userOpt.isPresent()) {
                return notFound("用户不存在");
            }

            User user = userOpt.get();

            // 检查是否为管理员用户
            if ("admin".equals(user.getUsername())) {
                return error("不能禁用管理员用户");
            }

            // 切换状态
            user.setStatus(user.getStatus() == 1 ? 0 : 1);
            user.setUpdatedAt(LocalDateTime.now());
            userService.save(user);

            String statusText = user.getStatus() == 1 ? "启用" : "禁用";
            return success("用户" + statusText + "成功");

        } catch (Exception e) {
            log.error("切换用户状态失败: ", e);
            return error("切换用户状态失败: " + e.getMessage());
        }
    }

    /**
     * 重置用户密码
     */
    @PatchMapping("/{id}/reset-password")
    @Operation(summary = "重置用户密码", description = "重置用户密码为默认密码")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Parameter(description = "用户ID") @PathVariable Long id) {

        try {
            logOperation("重置用户密码", id);
            validateId(id, "用户");

            // 检查用户是否存在
            Optional<User> userOpt = userService.findByIdOptional(id);
            if (!userOpt.isPresent()) {
                return notFound("用户不存在");
            }

            User user = userOpt.get();

            // 重置密码为默认密码
            user.setPassword(passwordEncoder.encode("123456"));
            user.setUpdatedAt(LocalDateTime.now());
            userService.save(user);

            return success("密码重置成功，新密码为：123456");

        } catch (Exception e) {
            log.error("重置用户密码失败: ", e);
            return error("重置用户密码失败: " + e.getMessage());
        }
    }

    /**
     * 验证用户数据
     */
    private void validateUserData(User user, boolean isCreate) {
        if (user == null) {
            throw new IllegalArgumentException("用户信息不能为空");
        }
        if (!StringUtils.hasText(user.getUsername())) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (!StringUtils.hasText(user.getRealName())) {
            throw new IllegalArgumentException("真实姓名不能为空");
        }

        // 验证用户名格式（只能包含字母、数字、下划线）
        if (!user.getUsername().matches("^[a-zA-Z0-9_]{3,20}$")) {
            throw new IllegalArgumentException("用户名只能包含字母、数字、下划线，长度3-20位");
        }

        // 验证邮箱格式
        if (StringUtils.hasText(user.getEmail()) && !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }

        // 验证手机号格式
        if (StringUtils.hasText(user.getPhone()) && !PHONE_PATTERN.matcher(user.getPhone()).matches()) {
            throw new IllegalArgumentException("手机号格式不正确");
        }

        // 验证用户名是否重复（需要在UserService中实现此方法）
        // if (userService.existsByUsername(user.getUsername(), user.getId())) {
        //     throw new IllegalArgumentException("用户名已存在");
        // }

        // 验证邮箱是否重复（需要在UserService中实现此方法）
        // if (StringUtils.hasText(user.getEmail()) && userService.existsByEmail(user.getEmail(), user.getId())) {
        //     throw new IllegalArgumentException("邮箱已存在");
        // }
    }
}
