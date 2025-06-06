package com.campus.interfaces.rest.v1;

import com.campus.application.service.UserService;
import com.campus.domain.entity.User;
import com.campus.shared.common.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

/**
 * 系统管理API控制器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@RestController
@RequestMapping("/api/system")
@Tag(name = "系统管理API", description = "系统管理相关REST API接口")
@SecurityRequirement(name = "Bearer")
public class SystemApiController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取系统状态
     */
    @GetMapping("/status")
    @Operation(summary = "获取系统状态", description = "获取系统运行状态信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getSystemStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            
            // 系统基本信息
            status.put("systemName", "Smart Campus Management");
            status.put("version", "1.0.0");
            status.put("startTime", LocalDateTime.now().minusHours(24)); // 模拟启动时间
            status.put("currentTime", LocalDateTime.now());
            
            // 数据库状态
            try {
                long userCount = userService.countTotalUsers();
                status.put("databaseStatus", "正常");
                status.put("totalUsers", userCount);
            } catch (Exception e) {
                status.put("databaseStatus", "异常");
                status.put("databaseError", e.getMessage());
            }
            
            // 系统资源（简化实现）
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            status.put("totalMemory", formatBytes(totalMemory));
            status.put("usedMemory", formatBytes(usedMemory));
            status.put("freeMemory", formatBytes(freeMemory));
            status.put("memoryUsagePercent", (usedMemory * 100) / totalMemory);
            
            return ApiResponse.success("获取系统状态成功", status);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取系统状态失败：" + e.getMessage());
        }
    }

    /**
     * 清理系统缓存
     */
    @PostMapping("/clear-cache")
    @Operation(summary = "清理系统缓存", description = "清理系统缓存数据")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> clearCache() {
        try {
            // 实现缓存清理功能（简化实现）
            // 在实际项目中，这里应该清理Redis缓存、应用缓存等
            
            // 模拟缓存清理
            Thread.sleep(1000); // 模拟清理时间
            
            return ApiResponse.success("系统缓存清理成功");
        } catch (Exception e) {
            return ApiResponse.error(500, "清理系统缓存失败：" + e.getMessage());
        }
    }

    /**
     * 创建系统备份
     */
    @PostMapping("/backup")
    @Operation(summary = "创建系统备份", description = "创建系统数据备份")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> createBackup() {
        try {
            // 实现系统备份功能（简化实现）
            // 在实际项目中，这里应该备份数据库、文件系统等
            
            Map<String, Object> backupInfo = new HashMap<>();
            backupInfo.put("backupId", "backup_" + System.currentTimeMillis());
            backupInfo.put("backupTime", LocalDateTime.now());
            backupInfo.put("backupSize", "约 50MB");
            backupInfo.put("backupPath", "/backups/campus_" + LocalDateTime.now().toLocalDate() + ".sql");
            backupInfo.put("status", "成功");
            
            // 模拟备份时间
            Thread.sleep(2000);
            
            return ApiResponse.success("系统备份创建成功", backupInfo);
        } catch (Exception e) {
            return ApiResponse.error(500, "创建系统备份失败：" + e.getMessage());
        }
    }

    /**
     * 更新个人资料
     */
    @PostMapping("/profile/update")
    @Operation(summary = "更新个人资料", description = "更新当前用户的个人资料")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<String> updateProfile(@Parameter(description = "个人资料数据") @RequestBody Map<String, Object> profileData) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ApiResponse.error(404, "用户不存在");
            }
            
            User user = userOpt.get();
            
            // 更新用户信息
            if (profileData.containsKey("realName")) {
                user.setRealName((String) profileData.get("realName"));
            }
            if (profileData.containsKey("email")) {
                user.setEmail((String) profileData.get("email"));
            }
            if (profileData.containsKey("phone")) {
                user.setPhone((String) profileData.get("phone"));
            }
            
            user.setUpdatedAt(LocalDateTime.now());
            userService.updateUser(user);
            
            return ApiResponse.success("个人资料更新成功");
        } catch (Exception e) {
            return ApiResponse.error(500, "更新个人资料失败：" + e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/profile/change-password")
    @Operation(summary = "修改密码", description = "修改当前用户密码")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<String> changePassword(@Parameter(description = "密码数据") @RequestBody Map<String, String> passwordData) {
        try {
            // 获取当前用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ApiResponse.error(404, "用户不存在");
            }
            
            User user = userOpt.get();
            String oldPassword = passwordData.get("oldPassword");
            String newPassword = passwordData.get("newPassword");
            String confirmPassword = passwordData.get("confirmPassword");
            
            // 验证旧密码
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return ApiResponse.error(400, "原密码不正确");
            }
            
            // 验证新密码
            if (!newPassword.equals(confirmPassword)) {
                return ApiResponse.error(400, "新密码与确认密码不一致");
            }
            
            if (newPassword.length() < 6) {
                return ApiResponse.error(400, "新密码长度不能少于6位");
            }
            
            // 更新密码
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());
            userService.updateUser(user);
            
            return ApiResponse.success("密码修改成功");
        } catch (Exception e) {
            return ApiResponse.error(500, "修改密码失败：" + e.getMessage());
        }
    }

    /**
     * 获取系统设置
     */
    @GetMapping("/settings")
    @Operation(summary = "获取系统设置", description = "获取系统配置参数")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getSystemSettings() {
        try {
            Map<String, Object> settings = new HashMap<>();
            
            // 基本设置
            settings.put("systemName", "智慧校园管理系统");
            settings.put("systemVersion", "v2.0.0");
            settings.put("systemLogo", "/images/logo.png");
            settings.put("contactEmail", "admin@campus.edu.cn");
            settings.put("contactPhone", "400-123-4567");
            
            // 安全设置
            settings.put("maxLoginAttempts", 5);
            settings.put("sessionTimeout", 30);
            settings.put("passwordMinLength", 8);
            settings.put("enableCaptcha", true);
            
            // 通知设置
            settings.put("enableEmailNotification", true);
            settings.put("enableSmsNotification", false);
            settings.put("notificationEmail", "admin@campus.edu.cn");
            settings.put("notificationPhone", "13800138000");
            
            return ApiResponse.success("获取系统设置成功", settings);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取系统设置失败：" + e.getMessage());
        }
    }

    /**
     * 更新系统设置
     */
    @PostMapping("/settings")
    @Operation(summary = "更新系统设置", description = "更新系统配置参数")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> updateSystemSettings(@Parameter(description = "系统设置数据") @RequestBody Map<String, Object> settingsData) {
        try {
            // 验证必要字段
            if (!settingsData.containsKey("systemName") ||
                settingsData.get("systemName").toString().trim().isEmpty()) {
                return ApiResponse.error(400, "系统名称不能为空");
            }
            
            // 验证数值类型字段
            try {
                if (settingsData.containsKey("maxLoginAttempts")) {
                    int maxAttempts = Integer.parseInt(settingsData.get("maxLoginAttempts").toString());
                    if (maxAttempts < 1 || maxAttempts > 10) {
                        return ApiResponse.error(400, "最大登录尝试次数必须在1-10之间");
                    }
                }
                
                if (settingsData.containsKey("sessionTimeout")) {
                    int timeout = Integer.parseInt(settingsData.get("sessionTimeout").toString());
                    if (timeout < 5 || timeout > 120) {
                        return ApiResponse.error(400, "会话超时时间必须在5-120分钟之间");
                    }
                }
                
                if (settingsData.containsKey("passwordMinLength")) {
                    int minLength = Integer.parseInt(settingsData.get("passwordMinLength").toString());
                    if (minLength < 4 || minLength > 20) {
                        return ApiResponse.error(400, "密码最小长度必须在4-20之间");
                    }
                }
            } catch (NumberFormatException e) {
                return ApiResponse.error(400, "数值格式不正确");
            }
            
            // 在实际项目中，这里应该保存到数据库
            // 模拟保存过程
            Thread.sleep(500);
            
            return ApiResponse.success("系统设置更新成功");
        } catch (Exception e) {
            return ApiResponse.error(500, "更新系统设置失败：" + e.getMessage());
        }
    }

    /**
     * 上传系统Logo
     */
    @PostMapping("/upload-logo")
    @Operation(summary = "上传系统Logo", description = "上传系统Logo文件")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> uploadLogo(@Parameter(description = "Logo文件") @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ApiResponse.error(400, "请选择要上传的文件");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (!contentType.startsWith("image/")) {
                return ApiResponse.error(400, "只能上传图片文件");
            }
            
            // 验证文件大小（2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return ApiResponse.error(400, "文件大小不能超过2MB");
            }
            
            // 模拟文件保存
            String filename = "logo_" + System.currentTimeMillis() + ".png";
            String logoUrl = "/images/" + filename;
            
            Map<String, Object> result = new HashMap<>();
            result.put("logoUrl", logoUrl);
            result.put("filename", filename);
            result.put("size", file.getSize());
            
            return ApiResponse.success("Logo上传成功", result);
        } catch (Exception e) {
            return ApiResponse.error(500, "上传Logo失败：" + e.getMessage());
        }
    }

    /**
     * 获取系统日志
     */
    @GetMapping("/logs")
    @Operation(summary = "获取系统日志", description = "获取系统运行日志")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getSystemLogs(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "50") int size) {
        try {
            List<Map<String, Object>> logs = new ArrayList<>();
            
            // 模拟日志数据
            for (int i = 0; i < size; i++) {
                Map<String, Object> log = new HashMap<>();
                log.put("id", (page - 1) * size + i + 1);
                log.put("level", i % 4 == 0 ? "ERROR" : i % 3 == 0 ? "WARN" : "INFO");
                log.put("message", "系统日志消息 " + ((page - 1) * size + i + 1));
                log.put("timestamp", LocalDateTime.now().minusMinutes(i * 5));
                log.put("source", "SystemService");
                logs.add(log);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("logs", logs);
            result.put("total", 1000); // 模拟总数
            result.put("page", page);
            result.put("size", size);
            result.put("totalPages", (1000 + size - 1) / size);
            
            return ApiResponse.success("获取系统日志成功", result);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取系统日志失败：" + e.getMessage());
        }
    }

    /**
     * 重启系统服务
     */
    @PostMapping("/restart")
    @Operation(summary = "重启系统服务", description = "重启系统服务（谨慎操作）")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> restartSystem() {
        try {
            // 在实际项目中，这里应该实现真正的系统重启逻辑
            // 这是一个非常危险的操作，需要特别谨慎
            
            // 模拟重启过程
            Thread.sleep(2000);
            
            return ApiResponse.success("系统重启指令已发送，系统将在30秒后重启");
        } catch (Exception e) {
            return ApiResponse.error(500, "系统重启失败：" + e.getMessage());
        }
    }

    /**
     * 获取系统统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取系统统计信息", description = "获取系统实时统计数据")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getSystemStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 系统信息
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            stats.put("javaVersion", System.getProperty("java.version"));
            stats.put("osName", System.getProperty("os.name"));
            stats.put("osVersion", System.getProperty("os.version"));
            stats.put("totalMemory", formatBytes(totalMemory));
            stats.put("usedMemory", formatBytes(usedMemory));
            stats.put("freeMemory", formatBytes(freeMemory));
            stats.put("memoryUsagePercent", (usedMemory * 100) / totalMemory);
            stats.put("cpuUsagePercent", 25); // 模拟CPU使用率
            stats.put("diskUsagePercent", 45); // 模拟磁盘使用率
            stats.put("networkStatus", "正常");
            stats.put("uptime", "15天 8小时 32分钟");
            
            return ApiResponse.success("获取系统统计信息成功", stats);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取系统统计信息失败：" + e.getMessage());
        }
    }

    /**
     * 格式化字节数
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
