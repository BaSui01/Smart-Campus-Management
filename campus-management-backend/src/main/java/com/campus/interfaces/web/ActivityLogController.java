package com.campus.interfaces.web;

import com.campus.application.service.ActivityLogService;
import com.campus.domain.entity.ActivityLog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 活动日志控制器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-01
 */
@Controller
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    /**
     * 活动日志管理页面
     */
    @GetMapping("/admin/activity-log")
    public String activityLog(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "20") int size,
                             @RequestParam(defaultValue = "") String activityType,
                             @RequestParam(defaultValue = "") String module,
                             @RequestParam(defaultValue = "") String level,
                             @RequestParam(defaultValue = "") String result,
                             @RequestParam(defaultValue = "") String username,
                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
                             Model model) {
        try {
            // 创建分页对象
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));

            // 查询活动日志
            Page<ActivityLog> activityLogs;
            try {
                if (hasSearchConditions(activityType, module, level, result, username, startTime, endTime)) {
                    activityLogs = activityLogService.findByConditions(
                            (activityType != null && !activityType.isEmpty()) ? activityType : null,
                            (module != null && !module.isEmpty()) ? module : null,
                            (level != null && !level.isEmpty()) ? level : null,
                            (result != null && !result.isEmpty()) ? result : null,
                            (username != null && !username.isEmpty()) ? username : null,
                            startTime, endTime, pageable);
                } else {
                    activityLogs = activityLogService.findAll(pageable);
                }
            } catch (Exception e) {
                System.err.println("查询活动日志失败: " + e.getMessage());
                // 创建空的Page对象
                activityLogs = Page.empty(pageable);
            }

            // 获取统计信息
            Map<String, Object> statistics;
            try {
                statistics = activityLogService.getActivityStatistics();
            } catch (Exception e) {
                System.err.println("获取统计信息失败: " + e.getMessage());
                statistics = new HashMap<>();
                statistics.put("totalCount", 0L);
                statistics.put("todayCount", 0L);
                statistics.put("weekCount", 0L);
                statistics.put("monthCount", 0L);
            }
            
            // 添加模型属性
            model.addAttribute("activityLogs", activityLogs);
            model.addAttribute("statistics", statistics);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("activityType", activityType);
            model.addAttribute("module", module);
            model.addAttribute("level", level);
            model.addAttribute("result", result);
            model.addAttribute("username", username);
            model.addAttribute("startTime", startTime);
            model.addAttribute("endTime", endTime);
            model.addAttribute("pageTitle", "活动日志");
            model.addAttribute("currentPageName", "activity-log");
            
            // 添加选项数据 - 确保这些数组不为null
            String[] activityTypes = {"LOGIN", "LOGOUT", "OPERATION", "ERROR"};
            String[] modules = {"用户管理", "学生管理", "课程管理", "财务管理", "系统管理", "通知管理"};
            String[] levels = {"INFO", "WARN", "ERROR"};
            String[] results = {"SUCCESS", "FAILED", "PARTIAL"};

            model.addAttribute("activityTypes", activityTypes);
            model.addAttribute("modules", modules);
            model.addAttribute("levels", levels);
            model.addAttribute("results", results);
            
            return "admin/system/activity-log";
        } catch (Exception e) {
            model.addAttribute("error", "加载活动日志失败：" + e.getMessage());
            return "admin/system/activity-log";
        }
    }

    /**
     * 活动日志详情页面
     */
    @GetMapping("/admin/activity-log/detail")
    public String activityLogDetail(@RequestParam Long id, Model model) {
        try {
            ActivityLog activityLog = activityLogService.findById(id);
            model.addAttribute("activityLog", activityLog);
            model.addAttribute("pageTitle", "活动日志详情");
            model.addAttribute("currentPageName", "activity-log");
            
            return "admin/system/activity-log-detail";
        } catch (Exception e) {
            model.addAttribute("error", "加载活动日志详情失败：" + e.getMessage());
            return "admin/system/activity-log";
        }
    }

    /**
     * 活动统计页面
     */
    @GetMapping("/admin/activity-log/statistics")
    public String activityStatistics(Model model) {
        try {
            // 获取统计信息
            Map<String, Object> statistics = activityLogService.getActivityStatistics();
            
            // 获取每日活动统计（最近7天）
            List<Map<String, Object>> dailyStats = activityLogService.getDailyActivityStats(7);
            
            // 获取用户活动排行榜（最近30天，前10名）
            List<Map<String, Object>> userRanking = activityLogService.getUserActivityRanking(30, 10);
            
            // 获取模块操作统计（最近30天）
            List<Map<String, Object>> moduleStats = activityLogService.getModuleActionStats(30);
            
            model.addAttribute("statistics", statistics);
            model.addAttribute("dailyStats", dailyStats);
            model.addAttribute("userRanking", userRanking);
            model.addAttribute("moduleStats", moduleStats);
            model.addAttribute("pageTitle", "活动统计");
            model.addAttribute("currentPageName", "activity-log");
            
            return "admin/system/activity-statistics";
        } catch (Exception e) {
            model.addAttribute("error", "加载活动统计失败：" + e.getMessage());
            return "admin/system/activity-log";
        }
    }

    /**
     * 删除活动日志
     */
    @PostMapping("/admin/activity-log/delete")
    @ResponseBody
    public Map<String, Object> deleteActivityLog(@RequestParam Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            activityLogService.deleteByIds(List.of(id));
            result.put("success", true);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 批量删除活动日志
     */
    @PostMapping("/admin/activity-log/batch-delete")
    @ResponseBody
    public Map<String, Object> batchDeleteActivityLogs(@RequestParam List<Long> ids) {
        Map<String, Object> result = new HashMap<>();
        try {
            activityLogService.deleteByIds(ids);
            result.put("success", true);
            result.put("message", "批量删除成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量删除失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 清理过期日志
     */
    @PostMapping("/admin/activity-log/clean")
    @ResponseBody
    public Map<String, Object> cleanExpiredLogs(@RequestParam(defaultValue = "30") int daysToKeep) {
        Map<String, Object> result = new HashMap<>();
        try {
            activityLogService.cleanExpiredLogs(daysToKeep);
            result.put("success", true);
            result.put("message", "清理过期日志成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "清理过期日志失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取最近活动（AJAX）
     */
    @GetMapping("/admin/activity-log/recent")
    @ResponseBody
    public Map<String, Object> getRecentActivities(@RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ActivityLog> recentActivities = activityLogService.getRecentActivities(limit);
            result.put("success", true);
            result.put("data", recentActivities);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取最近活动失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取活动统计（AJAX）
     */
    @GetMapping("/admin/activity-log/stats")
    @ResponseBody
    public Map<String, Object> getActivityStats() {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> statistics = activityLogService.getActivityStatistics();
            result.put("success", true);
            result.put("data", statistics);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取活动统计失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 获取每日活动统计（AJAX）
     */
    @GetMapping("/admin/activity-log/daily-stats")
    @ResponseBody
    public Map<String, Object> getDailyStats(@RequestParam(defaultValue = "7") int days) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> dailyStats = activityLogService.getDailyActivityStats(days);
            result.put("success", true);
            result.put("data", dailyStats);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取每日统计失败：" + e.getMessage());
        }
        return result;
    }

    /**
     * 检查是否有搜索条件
     */
    private boolean hasSearchConditions(String activityType, String module, String level,
                                      String result, String username,
                                      LocalDateTime startTime, LocalDateTime endTime) {
        return (activityType != null && !activityType.isEmpty()) ||
               (module != null && !module.isEmpty()) ||
               (level != null && !level.isEmpty()) ||
               (result != null && !result.isEmpty()) ||
               (username != null && !username.isEmpty()) ||
               startTime != null || endTime != null;
    }
}
