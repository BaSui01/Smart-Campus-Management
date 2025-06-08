package com.campus.interfaces.rest.v1;

import com.campus.application.service.NotificationService;
import com.campus.common.controller.BaseController;
import com.campus.domain.entity.Notification;
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
    @PreAuthorize("hasAuthority('system:notification:list')")
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
    @PreAuthorize("hasAuthority('system:notification:list')")
    public ResponseEntity<ApiResponse<Notification>> getNotification(
            @Parameter(description = "通知ID", required = true) @PathVariable Long id) {

        try {
            log.info("查询通知详情 - ID: {}", id);

            validateId(id, "通知");

            Optional<Notification> notification = notificationService.findById(id);
            if (notification.isPresent()) {
                return success(notification.get(), "查询通知详情成功");
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
    @PreAuthorize("hasAuthority('system:notification:add')")
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
            return success(savedNotification, "通知创建成功");

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
    @PreAuthorize("hasAuthority('system:notification:edit')")
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
            return success(updatedNotification, "通知更新成功");

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
    @PreAuthorize("hasAuthority('system:notification:delete')")
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
            return success(null, "通知删除成功");

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
    @PreAuthorize("hasAuthority('system:notification:publish')")
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
            return success(null, "通知发布成功");

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
    @PreAuthorize("hasAuthority('system:notification:publish')")
    public ResponseEntity<ApiResponse<Void>> unpublishNotification(
            @Parameter(description = "通知ID", required = true) @PathVariable Long id) {

        try {
            log.info("取消发布通知 - ID: {}", id);

            validateId(id, "通知");

            notificationService.unpublishNotification(id);
            return success(null, "取消发布通知成功");

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
    @PreAuthorize("hasAuthority('system:notification:top')")
    public ResponseEntity<ApiResponse<Void>> topNotification(
            @Parameter(description = "通知ID", required = true) @PathVariable Long id) {

        try {
            log.info("置顶通知 - ID: {}", id);

            validateId(id, "通知");

            notificationService.topNotification(id);
            return success(null, "置顶通知成功");

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
    @PreAuthorize("hasAuthority('system:notification:top')")
    public ResponseEntity<ApiResponse<Void>> untopNotification(
            @Parameter(description = "通知ID", required = true) @PathVariable Long id) {

        try {
            log.info("取消置顶通知 - ID: {}", id);

            validateId(id, "通知");

            notificationService.untopNotification(id);
            return success(null, "取消置顶通知成功");

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
            return success(null, "标记为已读成功");

        } catch (Exception e) {
            log.error("标记通知为已读失败 - ID: {}", id, e);
            return error("标记通知为已读失败: " + e.getMessage());
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
