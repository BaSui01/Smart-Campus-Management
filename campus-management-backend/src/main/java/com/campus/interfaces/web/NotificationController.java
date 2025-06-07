package com.campus.interfaces.web;

import com.campus.domain.entity.Notification;
import com.campus.domain.repository.NotificationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知管理控制器
 */
@Controller
public class NotificationController {

    private final NotificationRepository notificationRepository;

    public NotificationController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * 通知管理页面
     */
    @GetMapping("/admin/notifications")
    public String notifications(Model model) {
        try {
            // 获取通知列表
            List<Notification> notifications = notificationRepository.findAll();
            
            // 获取统计信息
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCount", notifications.size());
            stats.put("publishedCount", notifications.stream().filter(n -> "PUBLISHED".equals(n.getNotificationStatus())).count());
            stats.put("draftCount", notifications.stream().filter(n -> "DRAFT".equals(n.getNotificationStatus())).count());
            stats.put("todayPublished", notifications.stream()
                    .filter(n -> n.getPublishTime() != null && 
                               n.getPublishTime().toLocalDate().equals(LocalDateTime.now().toLocalDate()))
                    .count());
            
            model.addAttribute("notifications", notifications);
            model.addAttribute("stats", stats);
            model.addAttribute("pageTitle", "通知管理");
            model.addAttribute("currentPage", "notifications");
            
            return "admin/system/notifications";
        } catch (Exception e) {
            model.addAttribute("error", "加载通知管理页面失败：" + e.getMessage());
            return "admin/system/notifications";
        }
    }

    /**
     * 添加通知页面
     */
    @GetMapping("/admin/notifications/add")
    public String addNotification(Model model) {
        try {
            model.addAttribute("notification", new Notification());
            model.addAttribute("pageTitle", "添加通知");
            model.addAttribute("currentPage", "notifications");
            
            // 添加选项数据
            model.addAttribute("notificationTypes", new String[]{"SYSTEM", "ACADEMIC", "FINANCE", "ACTIVITY"});
            model.addAttribute("targetAudiences", new String[]{"ALL", "STUDENTS", "TEACHERS", "ADMINS"});
            model.addAttribute("priorities", new String[]{"LOW", "NORMAL", "HIGH", "URGENT"});
            
            return "admin/system/notification-form";
        } catch (Exception e) {
            model.addAttribute("error", "加载添加通知页面失败：" + e.getMessage());
            return "admin/system/notifications";
        }
    }

    /**
     * 编辑通知页面
     */
    @GetMapping("/admin/notifications/edit")
    public String editNotification(@RequestParam Long id, Model model) {
        try {
            Notification notification = notificationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("通知不存在"));
            
            model.addAttribute("notification", notification);
            model.addAttribute("pageTitle", "编辑通知");
            model.addAttribute("currentPage", "notifications");
            
            // 添加选项数据
            model.addAttribute("notificationTypes", new String[]{"SYSTEM", "ACADEMIC", "FINANCE", "ACTIVITY"});
            model.addAttribute("targetAudiences", new String[]{"ALL", "STUDENTS", "TEACHERS", "ADMINS"});
            model.addAttribute("priorities", new String[]{"LOW", "NORMAL", "HIGH", "URGENT"});
            
            return "admin/system/notification-form";
        } catch (Exception e) {
            model.addAttribute("error", "加载编辑通知页面失败：" + e.getMessage());
            return "admin/system/notifications";
        }
    }

    /**
     * 通知详情页面
     */
    @GetMapping("/admin/notifications/detail")
    public String notificationDetail(@RequestParam Long id, Model model) {
        try {
            Notification notification = notificationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("通知不存在"));
            
            // 增加阅读次数
            notification.setReadCount(notification.getReadCount() + 1);
            notificationRepository.save(notification);
            
            model.addAttribute("notification", notification);
            model.addAttribute("pageTitle", "通知详情");
            model.addAttribute("currentPage", "notifications");
            
            return "admin/system/notification-detail";
        } catch (Exception e) {
            model.addAttribute("error", "加载通知详情页面失败：" + e.getMessage());
            return "admin/system/notifications";
        }
    }

    /**
     * 保存通知
     */
    @PostMapping("/admin/notifications/save")
    public String saveNotification(@ModelAttribute Notification notification, Model model) {
        try {
            if (notification.getId() == null) {
                // 新增通知
                if (notification.getSenderName() == null) {
                    notification.setSenderName("系统管理员");
                }
            } else {
                // 更新通知
                Notification existingNotification = notificationRepository.findById(notification.getId())
                        .orElseThrow(() -> new RuntimeException("通知不存在"));

                // 保留原有的阅读次数
                notification.setReadCount(existingNotification.getReadCount());
            }
            
            notificationRepository.save(notification);
            return "redirect:/admin/notifications";
        } catch (Exception e) {
            model.addAttribute("error", "保存通知失败：" + e.getMessage());
            model.addAttribute("notification", notification);
            return "admin/system/notification-form";
        }
    }

    /**
     * 删除通知
     */
    @PostMapping("/admin/notifications/delete")
    @ResponseBody
    public Map<String, Object> deleteNotification(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            notificationRepository.deleteById(id);
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 发布通知
     */
    @PostMapping("/admin/notifications/publish")
    @ResponseBody
    public Map<String, Object> publishNotification(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Notification notification = notificationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("通知不存在"));
            
            notification.setNotificationStatus("PUBLISHED");
            notification.setPublishTime(LocalDateTime.now());
            notificationRepository.save(notification);
            
            result.put("success", true);
            result.put("message", "发布成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "发布失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 撤回通知
     */
    @PostMapping("/admin/notifications/withdraw")
    @ResponseBody
    public Map<String, Object> withdrawNotification(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Notification notification = notificationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("通知不存在"));
            
            notification.setNotificationStatus("CANCELLED");
            notificationRepository.save(notification);
            
            result.put("success", true);
            result.put("message", "撤回成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "撤回失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 置顶/取消置顶通知
     */
    @PostMapping("/admin/notifications/pin")
    @ResponseBody
    public Map<String, Object> pinNotification(@RequestParam Long id, @RequestParam boolean pinned) {
        Map<String, Object> result = new HashMap<>();
        try {
            Notification notification = notificationRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("通知不存在"));
            
            notification.setIsPinned(pinned);
            notificationRepository.save(notification);
            
            result.put("success", true);
            result.put("message", pinned ? "置顶成功" : "取消置顶成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", (pinned ? "置顶" : "取消置顶") + "失败：" + e.getMessage());
        }
        return result;
    }
}
