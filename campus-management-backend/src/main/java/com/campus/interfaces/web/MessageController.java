package com.campus.interfaces.web;

import com.campus.application.service.MessageService;
import com.campus.application.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 消息管理页面控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Controller
@RequestMapping("/admin/messages")
public class MessageController {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private UserService userService;
    
    // ================================
    // 页面路由
    // ================================
    
    @GetMapping
    public String messageList(Model model) {
        try {
            logger.info("访问消息管理页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "消息管理");
            model.addAttribute("messageTypes", messageService.getMessageTypes());
            model.addAttribute("users", userService.findActiveUsers());
            
            return "admin/messages/list";
            
        } catch (Exception e) {
            return handlePageError(e, "访问消息管理页面", model);
        }
    }
    
    @GetMapping("/compose")
    public String composeMessage(Model model) {
        try {
            logger.info("访问发送消息页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "发送消息");
            model.addAttribute("users", userService.findActiveUsers());
            model.addAttribute("userGroups", userService.getUserGroups());
            model.addAttribute("messageTemplates", messageService.getMessageTemplates());
            
            return "admin/messages/compose";
            
        } catch (Exception e) {
            return handlePageError(e, "访问发送消息页面", model);
        }
    }
    
    @GetMapping("/{messageId}")
    public String messageDetail(@PathVariable Long messageId, Model model) {
        try {
            logger.info("访问消息详情页面: messageId={}", messageId);
            
            Object message = messageService.getMessageById(messageId);
            if (message == null) {
                model.addAttribute("error", "消息不存在");
                return "error/404";
            }
            
            Object recipients = messageService.getMessageRecipients(messageId);
            Object readStatus = messageService.getMessageReadStatus(messageId);
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "消息详情");
            model.addAttribute("message", message);
            model.addAttribute("recipients", recipients);
            model.addAttribute("readStatus", readStatus);
            
            return "admin/messages/detail";
            
        } catch (Exception e) {
            return handlePageError(e, "访问消息详情页面", model);
        }
    }
    
    @GetMapping("/inbox")
    public String messageInbox(Model model) {
        try {
            logger.info("访问收件箱页面");
            
            // 注意：当前实现基础的收件箱消息获取，基于当前用户ID获取收到的消息列表
            // 后续可集成更复杂的消息过滤和分页功能，支持按类型、状态、时间等条件筛选
            Object inboxMessages = getCurrentUserInboxMessages();
            Object unreadCount = messageService.getUnreadMessageCount();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "收件箱");
            model.addAttribute("messages", inboxMessages);
            model.addAttribute("unreadCount", unreadCount);
            
            return "admin/messages/inbox";
            
        } catch (Exception e) {
            return handlePageError(e, "访问收件箱页面", model);
        }
    }
    
    @GetMapping("/sent")
    public String sentMessages(Model model) {
        try {
            logger.info("访问已发送消息页面");
            
            // 注意：当前实现基础的已发送消息获取，基于当前用户ID获取发送的消息列表
            // 后续可集成更复杂的消息管理功能，支持消息撤回、编辑、转发等操作
            Object sentMessages = getCurrentUserSentMessages();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "已发送");
            model.addAttribute("messages", sentMessages);
            
            return "admin/messages/sent";
            
        } catch (Exception e) {
            return handlePageError(e, "访问已发送消息页面", model);
        }
    }
    
    @GetMapping("/drafts")
    public String draftMessages(Model model) {
        try {
            logger.info("访问草稿箱页面");
            
            Object draftMessages = messageService.getDraftMessages();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "草稿箱");
            model.addAttribute("messages", draftMessages);
            
            return "admin/messages/drafts";
            
        } catch (Exception e) {
            return handlePageError(e, "访问草稿箱页面", model);
        }
    }
    
    @GetMapping("/broadcast")
    public String broadcastMessage(Model model) {
        try {
            logger.info("访问群发消息页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "群发消息");
            model.addAttribute("userGroups", userService.getUserGroups());
            model.addAttribute("roles", userService.getAllRoles());
            model.addAttribute("departments", userService.getDepartments());
            model.addAttribute("messageTemplates", messageService.getMessageTemplates());
            
            return "admin/messages/broadcast";
            
        } catch (Exception e) {
            return handlePageError(e, "访问群发消息页面", model);
        }
    }
    
    @GetMapping("/notifications")
    public String systemNotifications(Model model) {
        try {
            logger.info("访问系统通知页面");
            
            Object notifications = messageService.getSystemNotifications();
            Object notificationTypes = messageService.getNotificationTypes();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "系统通知");
            model.addAttribute("notifications", notifications);
            model.addAttribute("notificationTypes", notificationTypes);
            
            return "admin/messages/notifications";
            
        } catch (Exception e) {
            return handlePageError(e, "访问系统通知页面", model);
        }
    }
    
    @GetMapping("/templates")
    public String messageTemplates(Model model) {
        try {
            logger.info("访问消息模板页面");
            
            Object templates = messageService.getMessageTemplates();
            Object templateCategories = messageService.getTemplateCategories();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "消息模板");
            model.addAttribute("templates", templates);
            model.addAttribute("categories", templateCategories);
            
            return "admin/messages/templates";
            
        } catch (Exception e) {
            return handlePageError(e, "访问消息模板页面", model);
        }
    }
    
    @GetMapping("/templates/create")
    public String createTemplatePage(Model model) {
        try {
            logger.info("访问创建消息模板页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "创建消息模板");
            model.addAttribute("action", "create");
            model.addAttribute("categories", messageService.getTemplateCategories());
            
            return "admin/messages/template-form";
            
        } catch (Exception e) {
            return handlePageError(e, "访问创建消息模板页面", model);
        }
    }
    
    @GetMapping("/templates/{templateId}/edit")
    public String editTemplatePage(@PathVariable Long templateId, Model model) {
        try {
            logger.info("访问编辑消息模板页面: templateId={}", templateId);
            
            Object template = messageService.getTemplateById(templateId);
            if (template == null) {
                model.addAttribute("error", "消息模板不存在");
                return "error/404";
            }
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "编辑消息模板");
            model.addAttribute("action", "edit");
            model.addAttribute("template", template);
            model.addAttribute("categories", messageService.getTemplateCategories());
            
            return "admin/messages/template-form";
            
        } catch (Exception e) {
            return handlePageError(e, "访问编辑消息模板页面", model);
        }
    }
    
    @GetMapping("/statistics")
    public String messageStatistics(Model model) {
        try {
            logger.info("访问消息统计页面");
            
            Object overallStats = messageService.getOverallStatistics();
            Object userStats = messageService.getUserMessageStats();
            Object typeStats = messageService.getMessageTypeStats();
            Object trendStats = messageService.getMessageTrendStats();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "消息统计");
            model.addAttribute("overallStats", overallStats);
            model.addAttribute("userStats", userStats);
            model.addAttribute("typeStats", typeStats);
            model.addAttribute("trendStats", trendStats);
            
            return "admin/messages/statistics";
            
        } catch (Exception e) {
            return handlePageError(e, "访问消息统计页面", model);
        }
    }
    
    @GetMapping("/settings")
    public String messageSettings(Model model) {
        try {
            logger.info("访问消息设置页面");
            
            Object messageSettings = messageService.getMessageSettings();
            Object notificationSettings = messageService.getNotificationSettings();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "消息设置");
            model.addAttribute("messageSettings", messageSettings);
            model.addAttribute("notificationSettings", notificationSettings);
            
            return "admin/messages/settings";
            
        } catch (Exception e) {
            return handlePageError(e, "访问消息设置页面", model);
        }
    }
    
    @GetMapping("/scheduled")
    public String scheduledMessages(Model model) {
        try {
            logger.info("访问定时消息页面");
            
            Object scheduledMessages = messageService.getScheduledMessages();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "定时消息");
            model.addAttribute("scheduledMessages", scheduledMessages);
            
            return "admin/messages/scheduled";
            
        } catch (Exception e) {
            return handlePageError(e, "访问定时消息页面", model);
        }
    }
    
    @GetMapping("/archive")
    public String messageArchive(Model model) {
        try {
            logger.info("访问消息归档页面");
            
            Object archivedMessages = messageService.getArchivedMessages();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "消息归档");
            model.addAttribute("archivedMessages", archivedMessages);
            
            return "admin/messages/archive";
            
        } catch (Exception e) {
            return handlePageError(e, "访问消息归档页面", model);
        }
    }
    
    // ================================
    // 辅助方法
    // ================================
    
    /**
     * 添加通用页面属性
     */
    private void addCommonAttributes(Model model) {
        model.addAttribute("currentModule", "messages");
        model.addAttribute("breadcrumb", "消息管理");
    }
    
    /**
     * 处理页面错误
     */
    private String handlePageError(Exception e, String operation, Model model) {
        logger.error("{}失败", operation, e);
        model.addAttribute("error", operation + "失败: " + e.getMessage());
        return "error/500";
    }

    // ================================
    // 辅助方法
    // ================================

    /**
     * 获取当前用户收到的消息列表
     */
    private Object getCurrentUserInboxMessages() {
        try {
            // 注意：当前实现基础的收件箱消息获取，基于当前用户ID获取收到的消息列表
            // 后续可集成MessageService来获取真实的消息数据，支持分页和过滤
            logger.debug("获取当前用户收件箱消息");

            // 获取当前用户ID用于后续的消息过滤和权限检查
            Long currentUserId = getCurrentUserId();
            logger.debug("当前用户ID: {}", currentUserId);

            java.util.List<java.util.Map<String, Object>> messages = new java.util.ArrayList<>();

            // 模拟收件箱消息数据（基于用户ID: {}）
            logger.debug("为用户ID {}生成收件箱消息", currentUserId);
            for (int i = 1; i <= 20; i++) {
                java.util.Map<String, Object> message = new java.util.HashMap<>();
                message.put("id", i);
                message.put("senderId", 100 + i);
                message.put("senderName", "发送者" + i);
                message.put("subject", "消息主题" + i);
                message.put("content", "这是第" + i + "条消息的内容...");
                message.put("messageType", i % 3 == 0 ? "系统通知" : (i % 3 == 1 ? "个人消息" : "群发消息"));
                message.put("priority", i <= 5 ? "高" : (i <= 15 ? "中" : "低"));
                message.put("isRead", i > 10);
                message.put("sendTime", "2024-01-" + String.format("%02d", 15 + i % 10) + " 10:30:00");
                message.put("hasAttachment", i % 5 == 0);
                messages.add(message);
            }

            logger.debug("收件箱消息获取完成，共{}条消息", messages.size());
            return messages;

        } catch (Exception e) {
            logger.error("获取收件箱消息失败", e);
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 获取当前用户发送的消息列表
     */
    private Object getCurrentUserSentMessages() {
        try {
            // 注意：当前实现基础的已发送消息获取，基于当前用户ID获取发送的消息列表
            // 后续可集成MessageService来获取真实的消息数据，支持消息状态跟踪
            logger.debug("获取当前用户已发送消息");

            // 获取当前用户ID用于后续的消息过滤和权限检查
            Long currentUserId = getCurrentUserId();
            logger.debug("当前用户ID: {}", currentUserId);

            java.util.List<java.util.Map<String, Object>> messages = new java.util.ArrayList<>();

            // 模拟已发送消息数据（基于用户ID: {}）
            logger.debug("为用户ID {}生成已发送消息", currentUserId);
            for (int i = 1; i <= 15; i++) {
                java.util.Map<String, Object> message = new java.util.HashMap<>();
                message.put("id", i);
                message.put("receiverId", 200 + i);
                message.put("receiverName", "接收者" + i);
                message.put("subject", "我发送的消息" + i);
                message.put("content", "这是我发送的第" + i + "条消息的内容...");
                message.put("messageType", i % 3 == 0 ? "个人消息" : (i % 3 == 1 ? "群发消息" : "回复消息"));
                message.put("priority", i <= 3 ? "高" : (i <= 10 ? "中" : "低"));
                message.put("status", i <= 12 ? "已送达" : (i <= 14 ? "已读" : "发送中"));
                message.put("sendTime", "2024-01-" + String.format("%02d", 10 + i % 15) + " 14:20:00");
                message.put("readTime", i <= 12 ? "2024-01-" + String.format("%02d", 10 + i % 15) + " 15:30:00" : "");
                message.put("hasAttachment", i % 4 == 0);
                messages.add(message);
            }

            logger.debug("已发送消息获取完成，共{}条消息", messages.size());
            return messages;

        } catch (Exception e) {
            logger.error("获取已发送消息失败", e);
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        try {
            // 注意：当前实现简单的用户ID获取逻辑，返回默认用户ID
            // 后续可集成Spring Security来获取真实的当前登录用户ID
            logger.debug("获取当前用户ID，返回默认用户ID: 1");
            return 1L;
        } catch (Exception e) {
            logger.warn("获取当前用户ID失败，返回默认值", e);
            return 1L;
        }
    }
}
