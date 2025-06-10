package com.campus.interfaces.rest.v1;

import com.campus.application.service.MessageService;
import com.campus.domain.entity.Message;
import com.campus.shared.common.ApiResponse;
import com.campus.interfaces.rest.common.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 消息管理API控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@RestController
@RequestMapping("/api/v1/messages")
@Tag(name = "消息管理", description = "消息管理相关API")
public class MessageApiController extends BaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageApiController.class);
    
    @Autowired
    private MessageService messageService;
    
    @PostMapping
    @Operation(summary = "发送消息", description = "发送新消息")
    public ResponseEntity<ApiResponse<Message>> sendMessage(@Valid @RequestBody Message message) {
        try {
            Message sent = messageService.sendMessage(message);
            return ResponseEntity.ok(ApiResponse.success("消息发送成功", sent));
        } catch (Exception e) {
            logger.error("发送消息失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("发送消息失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取消息详情", description = "根据ID获取消息详细信息")
    public ResponseEntity<ApiResponse<Message>> getMessage(
            @Parameter(description = "消息ID") @PathVariable Long id) {
        try {
            Optional<Message> message = messageService.findMessageById(id);
            if (message.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(message.get()));
            } else {
                return ResponseEntity.ok(ApiResponse.error("消息不存在"));
            }
        } catch (Exception e) {
            logger.error("获取消息详情失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取消息详情失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除消息", description = "删除指定消息")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(
            @Parameter(description = "消息ID") @PathVariable Long id) {
        try {
            messageService.deleteMessage(id);
            return ResponseEntity.ok(ApiResponse.success("消息删除成功"));
        } catch (Exception e) {
            logger.error("删除消息失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("删除消息失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/inbox/{userId}")
    @Operation(summary = "获取收件箱", description = "获取用户的收件箱消息")
    public ResponseEntity<ApiResponse<Page<Message>>> getInbox(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Message> messages = messageService.getInbox(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success(messages));
        } catch (Exception e) {
            logger.error("获取收件箱失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取收件箱失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/outbox/{userId}")
    @Operation(summary = "获取发件箱", description = "获取用户的发件箱消息")
    public ResponseEntity<ApiResponse<Page<Message>>> getOutbox(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Message> messages = messageService.getOutbox(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success(messages));
        } catch (Exception e) {
            logger.error("获取发件箱失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取发件箱失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/unread/{userId}")
    @Operation(summary = "获取未读消息", description = "获取用户的未读消息")
    public ResponseEntity<ApiResponse<List<Message>>> getUnreadMessages(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            List<Message> messages = messageService.findUnreadMessages(userId);
            return ResponseEntity.ok(ApiResponse.success(messages));
        } catch (Exception e) {
            logger.error("获取未读消息失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取未读消息失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/read")
    @Operation(summary = "标记为已读", description = "标记消息为已读")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @Parameter(description = "消息ID") @PathVariable Long id) {
        try {
            messageService.markAsRead(id);
            return ResponseEntity.ok(ApiResponse.success("消息已标记为已读"));
        } catch (Exception e) {
            logger.error("标记消息为已读失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("标记消息为已读失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/unread")
    @Operation(summary = "标记为未读", description = "标记消息为未读")
    public ResponseEntity<ApiResponse<Void>> markAsUnread(
            @Parameter(description = "消息ID") @PathVariable Long id) {
        try {
            messageService.markAsUnread(id);
            return ResponseEntity.ok(ApiResponse.success("消息已标记为未读"));
        } catch (Exception e) {
            logger.error("标记消息为未读失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("标记消息为未读失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/mark-all-read/{userId}")
    @Operation(summary = "全部标记为已读", description = "标记用户所有消息为已读")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            messageService.markAllAsRead(userId);
            return ResponseEntity.ok(ApiResponse.success("所有消息已标记为已读"));
        } catch (Exception e) {
            logger.error("标记所有消息为已读失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("标记所有消息为已读失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/forward")
    @Operation(summary = "转发消息", description = "转发消息给其他用户")
    public ResponseEntity<ApiResponse<Message>> forwardMessage(
            @Parameter(description = "消息ID") @PathVariable Long id,
            @Parameter(description = "新接收者ID") @RequestParam Long newReceiverId) {
        try {
            Message forwarded = messageService.forwardMessage(id, newReceiverId);
            return ResponseEntity.ok(ApiResponse.success("消息转发成功", forwarded));
        } catch (Exception e) {
            logger.error("转发消息失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("转发消息失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/reply")
    @Operation(summary = "回复消息", description = "回复消息")
    public ResponseEntity<ApiResponse<Message>> replyMessage(
            @Parameter(description = "原消息ID") @PathVariable Long id,
            @Parameter(description = "回复内容") @RequestParam String content) {
        try {
            Message reply = messageService.replyMessage(id, content);
            return ResponseEntity.ok(ApiResponse.success("消息回复成功", reply));
        } catch (Exception e) {
            logger.error("回复消息失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("回复消息失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/broadcast")
    @Operation(summary = "群发消息", description = "向多个用户群发消息")
    public ResponseEntity<ApiResponse<List<Message>>> sendBroadcastMessage(
            @RequestBody BroadcastMessageRequest request) {
        try {
            List<Message> messages = messageService.sendBroadcastMessage(
                request.getReceiverIds(), 
                request.getTitle(), 
                request.getContent(), 
                request.getMessageType()
            );
            return ResponseEntity.ok(ApiResponse.success("群发消息成功", messages));
        } catch (Exception e) {
            logger.error("群发消息失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("群发消息失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/search/{userId}")
    @Operation(summary = "搜索消息", description = "根据关键词搜索消息")
    public ResponseEntity<ApiResponse<Page<Message>>> searchMessages(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Message> messages = messageService.searchMessages(userId, keyword, pageable);
            return ResponseEntity.ok(ApiResponse.success(messages));
        } catch (Exception e) {
            logger.error("搜索消息失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("搜索消息失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取消息统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取消息统计信息", description = "获取消息模块的统计数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMessageStats() {
        try {
            log.info("获取消息统计信息");

            Map<String, Object> stats = new HashMap<>();

            // 基础统计（简化实现）
            stats.put("totalMessages", 0L);
            stats.put("unreadMessages", 0L);
            stats.put("sentMessages", 0L);
            stats.put("receivedMessages", 0L);

            // 时间统计（简化实现）
            stats.put("todayMessages", 0L);
            stats.put("weekMessages", 0L);
            stats.put("monthMessages", 0L);

            // 按类型统计
            Map<String, Long> typeStats = new HashMap<>();
            typeStats.put("SYSTEM", 0L);
            typeStats.put("PERSONAL", 0L);
            typeStats.put("GROUP", 0L);
            typeStats.put("BROADCAST", 0L);
            stats.put("typeStats", typeStats);

            // 最近活动（简化实现）
            List<Map<String, Object>> recentActivity = new ArrayList<>();
            stats.put("recentActivity", recentActivity);

            return success("获取消息统计信息成功", stats);

        } catch (Exception e) {
            log.error("获取消息统计信息失败: ", e);
            return error("获取消息统计信息失败: " + e.getMessage());
        }
    }

    @GetMapping("/statistics/{userId}")
    @Operation(summary = "获取用户消息统计", description = "获取指定用户的消息统计信息")
    public ResponseEntity<ApiResponse<Object>> getMessageStatistics(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        try {
            long unreadCount = messageService.countUnreadMessages(userId);
            long totalCount = messageService.countTotalMessages(userId);
            long sentCount = messageService.countSentMessages(userId);
            long receivedCount = messageService.countReceivedMessages(userId);

            MessageStatistics statistics = new MessageStatistics(unreadCount, totalCount, sentCount, receivedCount);

            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            logger.error("获取消息统计失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取消息统计失败: " + e.getMessage()));
        }
    }
    
    // ==================== 批量操作端点 ====================

    /**
     * 批量删除消息
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除消息", description = "根据ID列表批量删除消息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchDeleteMessages(
            @Parameter(description = "消息ID列表") @RequestBody List<Long> ids) {

        try {
            logOperation("批量删除消息", ids.size());

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("消息ID列表不能为空");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "消息");
            }

            // 执行批量删除
            messageService.deleteMessages(ids);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("deletedCount", ids.size());
            responseData.put("totalRequested", ids.size());

            return success("批量删除消息成功", responseData);

        } catch (Exception e) {
            log.error("批量删除消息失败: ", e);
            return error("批量删除消息失败: " + e.getMessage());
        }
    }

    /**
     * 批量标记为已读
     */
    @PutMapping("/batch/read")
    @Operation(summary = "批量标记为已读", description = "批量标记多个消息为已读")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchMarkAsRead(
            @Parameter(description = "消息ID列表") @RequestBody List<Long> ids) {

        try {
            logOperation("批量标记为已读", ids.size());

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("消息ID列表不能为空");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "消息");
            }

            // 执行批量标记为已读
            messageService.batchMarkAsRead(ids);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("markedCount", ids.size());
            responseData.put("totalRequested", ids.size());

            return success("批量标记为已读成功", responseData);

        } catch (Exception e) {
            log.error("批量标记为已读失败: ", e);
            return error("批量标记为已读失败: " + e.getMessage());
        }
    }

    /**
     * 批量导入消息
     */
    @PostMapping("/batch/import")
    @Operation(summary = "批量导入消息", description = "批量导入消息数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchImportMessages(
            @Parameter(description = "消息数据列表") @RequestBody List<Message> messages) {

        try {
            logOperation("批量导入消息", messages.size());

            // 验证参数
            if (messages == null || messages.isEmpty()) {
                return badRequest("消息数据列表不能为空");
            }

            if (messages.size() > 100) {
                return badRequest("单次批量导入不能超过100条记录");
            }

            // 执行批量导入
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Message message : messages) {
                try {
                    // 验证消息数据
                    if (message.getTitle() == null || message.getTitle().trim().isEmpty()) {
                        failCount++;
                        failReasons.add("消息标题不能为空");
                        continue;
                    }

                    if (message.getContent() == null || message.getContent().trim().isEmpty()) {
                        failCount++;
                        failReasons.add("消息内容不能为空");
                        continue;
                    }

                    messageService.sendMessage(message);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("消息 " + message.getTitle() + ": " + e.getMessage());
                    log.warn("导入消息{}失败: {}", message.getTitle(), e.getMessage());
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("totalRequested", messages.size());
            result.put("failReasons", failReasons);

            return success("批量导入消息完成", result);

        } catch (Exception e) {
            log.error("批量导入消息失败: ", e);
            return error("批量导入消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 消息统计数据对象
     */
    public static class MessageStatistics {
        private final long unread;
        private final long total;
        private final long sent;
        private final long received;

        public MessageStatistics(long unread, long total, long sent, long received) {
            this.unread = unread;
            this.total = total;
            this.sent = sent;
            this.received = received;
        }

        public long getUnread() { return unread; }
        public long getTotal() { return total; }
        public long getSent() { return sent; }
        public long getReceived() { return received; }
    }

    /**
     * 群发消息请求对象
     */
    public static class BroadcastMessageRequest {
        private List<Long> receiverIds;
        private String title;
        private String content;
        private String messageType;

        // Getters and Setters
        public List<Long> getReceiverIds() { return receiverIds; }
        public void setReceiverIds(List<Long> receiverIds) { this.receiverIds = receiverIds; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
    }
}
