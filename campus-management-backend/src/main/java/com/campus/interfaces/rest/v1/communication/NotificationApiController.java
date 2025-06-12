package com.campus.interfaces.rest.v1.communication;

import com.campus.application.service.communication.NotificationService;
import com.campus.domain.entity.communication.Notification;
import com.campus.interfaces.rest.common.BaseController;
import com.campus.shared.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 通知管理API控制器
 * 提供通知的增删改查、发布、阅读状态管理等功能
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "通知管理", description = "通知信息的增删改查、发布、阅读状态管理等操作")
public class NotificationApiController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(NotificationApiController.class);

    @Autowired
    private NotificationService notificationService;

    // ==================== 基础CRUD操作 ====================

    /**
     * 分页查询通知列表
     */
    @GetMapping
    @Operation(summary = "分页查询通知列表", description = "支持按条件搜索和分页查询通知")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<Notification>>> getNotifications(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "通知标题") @RequestParam(required = false) String title,
            @Parameter(description = "通知类型") @RequestParam(required = false) String notificationType,
            @Parameter(description = "优先级") @RequestParam(required = false) String priority,
            @Parameter(description = "是否已发布") @RequestParam(required = false) Boolean isPublished,
            @Parameter(description = "是否置顶") @RequestParam(required = false) Boolean isTop,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "publishTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String sortDir,
            HttpServletRequest request) {

        try {
            log.info("查询通知列表 - 页码: {}, 大小: {}, 标题: {}, 类型: {}", 
                page, size, title, notificationType);

            // 验证分页参数
            validatePageParams(page, size);

            // 创建分页对象
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page - 1, size, sort);

            // 查询数据
            Page<Notification> result = notificationService.findByConditions(
                title, notificationType, priority, isPublished, isTop, pageable);

            return successPage(result, "查询通知列表成功");

        } catch (Exception e) {
            log.error("查询通知列表失败: ", e);
            return error("查询通知列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询通知详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询通知详情", description = "根据ID查询通知的详细信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Notification>> getNotification(
            @Parameter(description = "通知ID", required = true) @PathVariable Long id) {

        try {
            log.info("查询通知详情 - ID: {}", id);

            validateId(id, "通知");

            Optional<Notification> notification = notificationService.findById(id);
            if (notification.isPresent()) {
                return success("查询通知详情成功", notification.get());
            } else {
                return error("通知不存在");
            }

        } catch (Exception e) {
            log.error("查询通知详情失败 - ID: {}", id, e);
            return error("查询通知详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建通知
     */
    @PostMapping
    @Operation(summary = "创建通知", description = "创建新的通知")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Notification>> createNotification(
            @RequestBody Notification notification,
            HttpServletRequest request) {

        try {
            log.info("创建通知 - 标题: {}, 类型: {}", notification.getTitle(), notification.getNotificationType());

            // 验证通知数据
            validateNotification(notification);

            // 检查标题是否重复
            if (notificationService.existsByTitle(notification.getTitle())) {
                return error("通知标题已存在");
            }

            // 设置发送人
            Long userId = getCurrentUserId();
            if (userId != null) {
                notification.setSenderId(userId);
            }

            // 设置默认值
            notification.setIsPublished(0);
            notification.setIsTop(0);
            notification.setReadCount(0);

            Notification savedNotification = notificationService.save(notification);
            return success("通知创建成功", savedNotification);

        } catch (Exception e) {
            log.error("创建通知失败: ", e);
            return error("创建通知失败: " + e.getMessage());
        }
    }

    /**
     * 更新通知
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新通知", description = "更新通知信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Notification>> updateNotification(
            @Parameter(description = "通知ID", required = true) @PathVariable Long id,
            @RequestBody Notification notification,
            HttpServletRequest request) {

        try {
            log.info("更新通知 - ID: {}, 标题: {}", id, notification.getTitle());

            validateId(id, "通知");

            // 检查通知是否存在
            Optional<Notification> existingOpt = notificationService.findById(id);
            if (!existingOpt.isPresent()) {
                return error("通知不存在");
            }

            Notification existing = existingOpt.get();

            // 检查是否可以修改
            if (!notificationService.canModifyNotification(id)) {
                return error("通知已发布，无法修改");
            }

            // 验证通知数据
            validateNotification(notification);

            // 检查标题是否重复（排除当前记录）
            if (notificationService.existsByTitleExcludeId(notification.getTitle(), id)) {
                return error("通知标题已存在");
            }

            // 更新字段
            existing.setTitle(notification.getTitle());
            existing.setContent(notification.getContent());
            existing.setNotificationType(notification.getNotificationType());
            existing.setPriority(notification.getPriority());
            existing.setTargetType(notification.getTargetType());
            existing.setTargetIds(notification.getTargetIds());
            existing.setPublishTime(notification.getPublishTime());
            existing.setExpireTime(notification.getExpireTime());
            existing.setAttachmentUrls(notification.getAttachmentUrls());
            existing.setExternalLink(notification.getExternalLink());
            existing.setAutoSend(notification.getAutoSend());
            existing.setSendEmail(notification.getSendEmail());
            existing.setSendSms(notification.getSendSms());

            Notification updatedNotification = notificationService.save(existing);
            return success("通知更新成功", updatedNotification);

        } catch (Exception e) {
            log.error("更新通知失败 - ID: {}", id, e);
            return error("更新通知失败: " + e.getMessage());
        }
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除通知", description = "删除指定的通知")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @Parameter(description = "通知ID", required = true) @PathVariable Long id) {

        try {
            log.info("删除通知 - ID: {}", id);

            validateId(id, "通知");

            // 检查通知是否存在
            Optional<Notification> notification = notificationService.findById(id);
            if (!notification.isPresent()) {
                return error("通知不存在");
            }

            // 检查是否可以删除
            if (!notificationService.canDeleteNotification(id)) {
                return error("通知已发布，无法删除");
            }

            notificationService.deleteById(id);
            return success("通知删除成功");

        } catch (Exception e) {
            log.error("删除通知失败 - ID: {}", id, e);
            return error("删除通知失败: " + e.getMessage());
        }
    }

    // ==================== 业务操作 ====================

    /**
     * 发布通知
     */
    @PostMapping("/{id}/publish")
    @Operation(summary = "发布通知", description = "发布指定的通知")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> publishNotification(
            @Parameter(description = "通知ID", required = true) @PathVariable Long id) {

        try {
            log.info("发布通知 - ID: {}", id);

            validateId(id, "通知");

            // 检查通知是否存在
            Optional<Notification> notification = notificationService.findById(id);
            if (!notification.isPresent()) {
                return error("通知不存在");
            }

            notificationService.publishNotification(id);
            return success("通知发布成功");

        } catch (Exception e) {
            log.error("发布通知失败 - ID: {}", id, e);
            return error("发布通知失败: " + e.getMessage());
        }
    }

    /**
     * 取消发布通知
     */
    @PostMapping("/{id}/unpublish")
    @Operation(summary = "取消发布通知", description = "取消发布指定的通知")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> unpublishNotification(
            @Parameter(description = "通知ID", required = true) @PathVariable Long id) {

        try {
            log.info("取消发布通知 - ID: {}", id);

            validateId(id, "通知");

            notificationService.unpublishNotification(id);
            return success("取消发布通知成功");

        } catch (Exception e) {
            log.error("取消发布通知失败 - ID: {}", id, e);
            return error("取消发布通知失败: " + e.getMessage());
        }
    }

    /**
     * 置顶通知
     */
    @PostMapping("/{id}/top")
    @Operation(summary = "置顶通知", description = "置顶指定的通知")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> topNotification(
            @Parameter(description = "通知ID", required = true) @PathVariable Long id) {

        try {
            log.info("置顶通知 - ID: {}", id);

            validateId(id, "通知");

            notificationService.topNotification(id);
            return success("置顶通知成功");

        } catch (Exception e) {
            log.error("置顶通知失败 - ID: {}", id, e);
            return error("置顶通知失败: " + e.getMessage());
        }
    }

    /**
     * 取消置顶通知
     */
    @PostMapping("/{id}/untop")
    @Operation(summary = "取消置顶通知", description = "取消置顶指定的通知")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> untopNotification(
            @Parameter(description = "通知ID", required = true) @PathVariable Long id) {

        try {
            log.info("取消置顶通知 - ID: {}", id);

            validateId(id, "通知");

            notificationService.untopNotification(id);
            return success("取消置顶通知成功");

        } catch (Exception e) {
            log.error("取消置顶通知失败 - ID: {}", id, e);
            return error("取消置顶通知失败: " + e.getMessage());
        }
    }

    // ==================== 用户通知操作 ====================

    /**
     * 获取当前用户的通知
     */
    @GetMapping("/my")
    @Operation(summary = "获取我的通知", description = "获取当前用户的通知列表")
    public ResponseEntity<ApiResponse<List<Notification>>> getMyNotifications(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", example = "20") @RequestParam(defaultValue = "20") int size) {

        try {
            // 获取当前用户ID
            Long userId = getCurrentUserId();
            if (userId == null) {
                return error("用户未登录");
            }

            log.info("获取用户通知 - 用户ID: {}", userId);

            // 创建分页对象
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("publishTime").descending());

            Page<Notification> result = notificationService.getUserNotifications(userId, pageable);
            return successPage(result, "获取用户通知成功");

        } catch (Exception e) {
            log.error("获取用户通知失败: ", e);
            return error("获取用户通知失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户的未读通知
     */
    @GetMapping("/my/unread")
    @Operation(summary = "获取我的未读通知", description = "获取当前用户的未读通知列表")
    public ResponseEntity<ApiResponse<List<Notification>>> getMyUnreadNotifications(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", example = "20") @RequestParam(defaultValue = "20") int size) {

        try {
            // 获取当前用户ID
            Long userId = getCurrentUserId();
            if (userId == null) {
                return error("用户未登录");
            }

            log.info("获取用户未读通知 - 用户ID: {}", userId);

            // 创建分页对象
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("publishTime").descending());

            Page<Notification> result = notificationService.getUserUnreadNotifications(userId, pageable);
            return successPage(result, "获取用户未读通知成功");

        } catch (Exception e) {
            log.error("获取用户未读通知失败: ", e);
            return error("获取用户未读通知失败: " + e.getMessage());
        }
    }

    /**
     * 标记通知为已读
     */
    @PostMapping("/{id}/read")
    @Operation(summary = "标记通知为已读", description = "标记指定通知为已读状态")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @Parameter(description = "通知ID", required = true) @PathVariable Long id) {

        try {
            // 获取当前用户ID
            Long userId = getCurrentUserId();
            if (userId == null) {
                return error("用户未登录");
            }

            log.info("标记通知为已读 - 通知ID: {}, 用户ID: {}", id, userId);

            validateId(id, "通知");

            notificationService.markAsRead(id, userId);
            return success("标记为已读成功");

        } catch (Exception e) {
            log.error("标记通知为已读失败 - ID: {}", id, e);
            return error("标记通知为已读失败: " + e.getMessage());
        }
    }

    /**
     * 获取通知统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取通知统计信息", description = "获取通知模块的统计数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNotificationStats() {
        try {
            log.info("获取通知统计信息");

            Map<String, Object> stats = new HashMap<>();

            // 总通知数
            long totalNotifications = notificationService.count();
            stats.put("totalNotifications", totalNotifications);

            // 已发布通知数
            long publishedNotifications = notificationService.findAll().stream()
                .filter(n -> n.getIsPublished() == 1)
                .count();
            stats.put("publishedNotifications", publishedNotifications);

            // 草稿通知数
            long draftNotifications = notificationService.findAll().stream()
                .filter(n -> n.getIsPublished() == 0)
                .count();
            stats.put("draftNotifications", draftNotifications);

            // 置顶通知数
            long topNotifications = notificationService.findAll().stream()
                .filter(n -> n.getIsTop() == 1)
                .count();
            stats.put("topNotifications", topNotifications);

            // 今日新增通知数
            long todayNotifications = 0; // 暂时设为0，需要服务层实现
            stats.put("todayNotifications", todayNotifications);

            // 按类型统计 - 使用简单的统计
            Map<String, Long> typeStats = new HashMap<>();
            List<Notification> allNotifications = notificationService.findAll();
            typeStats.put("SYSTEM", allNotifications.stream().filter(n -> "system".equals(n.getNotificationType())).count());
            typeStats.put("ACADEMIC", allNotifications.stream().filter(n -> "academic".equals(n.getNotificationType())).count());
            typeStats.put("ACTIVITY", allNotifications.stream().filter(n -> "activity".equals(n.getNotificationType())).count());
            stats.put("typeStats", typeStats);

            // 最近活动 - 暂时返回空列表
            List<Map<String, Object>> recentActivity = new ArrayList<>();
            stats.put("recentActivity", recentActivity);

            return success("获取通知统计信息成功", stats);

        } catch (Exception e) {
            log.error("获取通知统计信息失败: ", e);
            return error("获取通知统计信息失败: " + e.getMessage());
        }
    }

    // ==================== 批量操作端点 ====================

    /**
     * 批量删除通知
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除通知", description = "根据ID列表批量删除通知")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchDeleteNotifications(
            @Parameter(description = "通知ID列表") @RequestBody List<Long> ids) {

        try {
            logOperation("批量删除通知", ids.size());

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("通知ID列表不能为空");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "通知");
            }

            // 执行批量删除
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Long id : ids) {
                try {
                    // 检查是否可以删除
                    if (!notificationService.canDeleteNotification(id)) {
                        failCount++;
                        failReasons.add("通知ID " + id + ": 已发布，无法删除");
                        continue;
                    }

                    notificationService.deleteById(id);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("通知ID " + id + ": " + e.getMessage());
                    log.warn("删除通知{}失败: {}", id, e.getMessage());
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", ids.size());
            responseData.put("failReasons", failReasons);

            if (failCount == 0) {
                return success("批量删除通知成功", responseData);
            } else if (successCount > 0) {
                return success("批量删除通知部分成功", responseData);
            } else {
                return error("批量删除通知失败");
            }

        } catch (Exception e) {
            log.error("批量删除通知失败: ", e);
            return error("批量删除通知失败: " + e.getMessage());
        }
    }

    /**
     * 批量发布通知
     */
    @PutMapping("/batch/publish")
    @Operation(summary = "批量发布通知", description = "批量发布通知")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchPublishNotifications(
            @Parameter(description = "通知ID列表") @RequestBody List<Long> ids) {

        try {
            logOperation("批量发布通知", ids.size());

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("通知ID列表不能为空");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "通知");
            }

            // 执行批量发布
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Long id : ids) {
                try {
                    notificationService.publishNotification(id);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("通知ID " + id + ": " + e.getMessage());
                    log.warn("发布通知{}失败: {}", id, e.getMessage());
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", ids.size());
            responseData.put("failReasons", failReasons);

            if (failCount == 0) {
                return success("批量发布通知成功", responseData);
            } else if (successCount > 0) {
                return success("批量发布通知部分成功", responseData);
            } else {
                return error("批量发布通知失败");
            }

        } catch (Exception e) {
            log.error("批量发布通知失败: ", e);
            return error("批量发布通知失败: " + e.getMessage());
        }
    }

    /**
     * 批量导入通知
     */
    @PostMapping("/batch/import")
    @Operation(summary = "批量导入通知", description = "批量导入通知数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchImportNotifications(
            @Parameter(description = "通知数据列表") @RequestBody List<Notification> notifications) {

        try {
            logOperation("批量导入通知", notifications.size());

            // 验证参数
            if (notifications == null || notifications.isEmpty()) {
                return badRequest("通知数据列表不能为空");
            }

            if (notifications.size() > 100) {
                return badRequest("单次批量导入不能超过100条记录");
            }

            // 执行批量导入
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Notification notification : notifications) {
                try {
                    // 验证通知数据
                    validateNotification(notification);

                    // 检查标题是否重复
                    if (notificationService.existsByTitle(notification.getTitle())) {
                        failCount++;
                        failReasons.add("通知标题 " + notification.getTitle() + " 已存在");
                        continue;
                    }

                    // 设置默认值
                    notification.setIsPublished(0);
                    notification.setIsTop(0);
                    notification.setReadCount(0);

                    // 设置发送人
                    Long userId = getCurrentUserId();
                    if (userId != null) {
                        notification.setSenderId(userId);
                    }

                    notificationService.save(notification);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("通知 " + notification.getTitle() + ": " + e.getMessage());
                    log.warn("导入通知{}失败: {}", notification.getTitle(), e.getMessage());
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("totalRequested", notifications.size());
            result.put("failReasons", failReasons);

            return success("批量导入通知完成", result);

        } catch (Exception e) {
            log.error("批量导入通知失败: ", e);
            return error("批量导入通知失败: " + e.getMessage());
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 验证通知数据
     */
    private void validateNotification(Notification notification) {
        if (notification.getTitle() == null || notification.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("通知标题不能为空");
        }
        if (notification.getContent() == null || notification.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("通知内容不能为空");
        }
        if (notification.getNotificationType() == null || notification.getNotificationType().trim().isEmpty()) {
            throw new IllegalArgumentException("通知类型不能为空");
        }
        if (notification.getTargetType() == null || notification.getTargetType().trim().isEmpty()) {
            throw new IllegalArgumentException("目标类型不能为空");
        }
        if (notification.getPublishTime() != null && notification.getExpireTime() != null) {
            if (notification.getPublishTime().isAfter(notification.getExpireTime())) {
                throw new IllegalArgumentException("发布时间不能晚于过期时间");
            }
        }
    }

    /**
     * 安全地获取当前用户ID
     */
    @SuppressWarnings("unchecked")
    private Long getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Map) {
                Map<String, Object> userInfo = (Map<String, Object>) auth.getPrincipal();
                Object idObj = userInfo.get("id");
                if (idObj != null) {
                    return Long.valueOf(idObj.toString());
                }
            }
        } catch (Exception e) {
            log.warn("获取当前用户ID失败: {}", e.getMessage());
        }
        return null;
    }
}
