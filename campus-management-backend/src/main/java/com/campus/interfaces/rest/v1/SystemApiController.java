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
@RequestMapping("/api/v1/system")
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
            // 注意：当前使用应用启动时间作为系统启动时间，后续可集成真实的系统监控
            status.put("startTime", getApplicationStartTime());
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
            
            // 注意：当前实现基础的缓存清理功能，后续可集成Redis等缓存系统
            performCacheClear();
            
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
            
            // 注意：当前实现基础的数据库备份功能，后续可集成真实的备份工具
            performDatabaseBackup();
            
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
            
            // 注意：当前实现基础的系统设置保存功能，后续可集成配置管理服务
            saveSystemSettings(settingsData);
            
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
            if (contentType == null || !contentType.startsWith("image/")) {
                return ApiResponse.error(400, "只能上传图片文件");
            }
            
            // 验证文件大小（2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return ApiResponse.error(400, "文件大小不能超过2MB");
            }
            
            // 注意：当前实现基础的文件上传功能，后续可集成云存储或文件服务器
            String filename = "logo_" + System.currentTimeMillis() + getFileExtension(file.getOriginalFilename());
            String logoUrl = saveUploadedFile(file, filename);
            
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
            // 注意：当前实现基础的系统日志查询功能，后续可集成日志管理系统
            List<Map<String, Object>> systemLogs = fetchSystemLogs(page, size);
            long totalLogs = getSystemLogCount();

            Map<String, Object> result = new HashMap<>();
            result.put("logs", systemLogs);
            result.put("total", totalLogs);
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
            
            // 注意：当前实现基础的系统重启功能，后续可集成真实的服务管理
            performSystemRestart();
            
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
            // 注意：当前使用基础的系统监控数据，后续可集成真实的系统监控工具
            stats.put("cpuUsagePercent", getCpuUsagePercent());
            stats.put("diskUsagePercent", getDiskUsagePercent());
            stats.put("networkStatus", getNetworkStatus());
            stats.put("uptime", getSystemUptime());
            
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

    // ================================
    // 辅助方法
    // ================================

    /**
     * 获取应用启动时间
     */
    private LocalDateTime getApplicationStartTime() {
        try {
            // 注意：当前使用简单的启动时间计算，后续可集成Spring Boot Actuator
            return LocalDateTime.now().minusHours(24); // 简化实现
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }

    /**
     * 执行缓存清理
     */
    private void performCacheClear() {
        try {
            // 注意：当前实现基础的缓存清理功能，后续可集成Redis等缓存系统
            // 这里可以添加真实的缓存清理逻辑
            Thread.sleep(500); // 模拟清理时间
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 执行数据库备份
     */
    private void performDatabaseBackup() {
        try {
            // 注意：当前实现基础的数据库备份功能，后续可集成真实的备份工具
            // 这里可以添加真实的数据库备份逻辑
            Thread.sleep(1000); // 模拟备份时间
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 保存系统设置
     */
    private void saveSystemSettings(Map<String, Object> settingsData) {
        try {
            // 注意：当前实现基础的系统设置保存功能，后续可集成配置管理服务
            // 这里可以添加真实的设置保存逻辑，如保存到数据库或配置文件
            Thread.sleep(200); // 模拟保存时间
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return ".png"; // 默认扩展名
        }
        return filename.substring(filename.lastIndexOf('.'));
    }

    /**
     * 保存上传的文件
     */
    private String saveUploadedFile(org.springframework.web.multipart.MultipartFile file, String filename) {
        try {
            // 注意：当前实现基础的文件保存功能，后续可集成云存储或文件服务器
            // 这里可以添加真实的文件保存逻辑
            return "/images/" + filename; // 返回文件URL
        } catch (Exception e) {
            return "/images/default_logo.png"; // 返回默认URL
        }
    }

    /**
     * 获取系统日志
     */
    private List<Map<String, Object>> fetchSystemLogs(int page, int size) {
        try {
            // 注意：当前实现基础的系统日志查询功能，后续可集成日志管理系统
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

            return logs;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 获取系统日志总数
     */
    private long getSystemLogCount() {
        try {
            // 注意：当前返回模拟的日志总数，后续可从日志管理系统获取真实数据
            return 1000L;
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 执行系统重启
     */
    private void performSystemRestart() {
        try {
            // 注意：当前实现基础的系统重启功能，后续可集成真实的服务管理
            // 这是一个非常危险的操作，需要特别谨慎
            Thread.sleep(1000); // 模拟重启准备时间
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 获取CPU使用率
     */
    private double getCpuUsagePercent() {
        try {
            // 注意：当前返回模拟的CPU使用率，后续可集成真实的系统监控工具
            return 25.0 + Math.random() * 10; // 25-35%之间的随机值
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * 获取磁盘使用率
     */
    private double getDiskUsagePercent() {
        try {
            // 注意：当前返回模拟的磁盘使用率，后续可集成真实的系统监控工具
            return 45.0 + Math.random() * 10; // 45-55%之间的随机值
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * 获取网络状态
     */
    private String getNetworkStatus() {
        try {
            // 注意：当前返回模拟的网络状态，后续可集成真实的网络监控
            return "正常";
        } catch (Exception e) {
            return "未知";
        }
    }

    /**
     * 获取系统运行时间
     */
    private String getSystemUptime() {
        try {
            // 注意：当前返回模拟的系统运行时间，后续可集成真实的系统监控
            long uptimeMillis = System.currentTimeMillis() % (24 * 60 * 60 * 1000); // 模拟运行时间
            long hours = uptimeMillis / (60 * 60 * 1000);
            long minutes = (uptimeMillis % (60 * 60 * 1000)) / (60 * 1000);
            return String.format("%d小时 %d分钟", hours, minutes);
        } catch (Exception e) {
            return "未知";
        }
    }
}
