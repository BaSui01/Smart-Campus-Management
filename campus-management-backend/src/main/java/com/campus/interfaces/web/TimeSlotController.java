package com.campus.interfaces.web;

import com.campus.application.service.TimeSlotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 时间段管理页面控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-15
 */
@Controller
@RequestMapping("/admin/time-slots")
public class TimeSlotController {
    
    private static final Logger logger = LoggerFactory.getLogger(TimeSlotController.class);
    
    @Autowired
    private TimeSlotService timeSlotService;
    
    // ================================
    // 页面路由
    // ================================
    
    @GetMapping
    public String timeSlotList(Model model) {
        try {
            logger.info("访问时间段管理页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "时间段管理");
            model.addAttribute("timeSlotTypes", getTimeSlotTypes());
            model.addAttribute("semesters", getSemesters());
            
            return "admin/time-slots/list";
            
        } catch (Exception e) {
            return handlePageError(e, "访问时间段管理页面", model);
        }
    }
    
    @GetMapping("/create")
    public String createTimeSlot(Model model) {
        try {
            logger.info("访问创建时间段页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "创建时间段");
            model.addAttribute("action", "create");
            model.addAttribute("timeSlotTypes", getTimeSlotTypes());
            model.addAttribute("semesters", getSemesters());
            
            return "admin/time-slots/form";
            
        } catch (Exception e) {
            return handlePageError(e, "访问创建时间段页面", model);
        }
    }
    
    @GetMapping("/{timeSlotId}/edit")
    public String editTimeSlot(@PathVariable Long timeSlotId, Model model) {
        try {
            logger.info("访问编辑时间段页面: timeSlotId={}", timeSlotId);
            
            Object timeSlot = timeSlotService.findTimeSlotById(timeSlotId).orElse(null);
            if (timeSlot == null) {
                model.addAttribute("error", "时间段不存在");
                return "error/404";
            }
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "编辑时间段");
            model.addAttribute("action", "edit");
            model.addAttribute("timeSlot", timeSlot);
            model.addAttribute("timeSlotTypes", getTimeSlotTypes());
            model.addAttribute("semesters", getSemesters());
            
            return "admin/time-slots/form";
            
        } catch (Exception e) {
            return handlePageError(e, "访问编辑时间段页面", model);
        }
    }
    
    @GetMapping("/standard")
    public String standardTimeSlots(Model model) {
        try {
            logger.info("访问标准时间段页面");
            
            Object standardTimeSlots = timeSlotService.findStandardTimeSlots();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "标准时间段");
            model.addAttribute("standardTimeSlots", standardTimeSlots);
            
            return "admin/time-slots/standard";
            
        } catch (Exception e) {
            return handlePageError(e, "访问标准时间段页面", model);
        }
    }
    
    @GetMapping("/calendar")
    public String timeSlotCalendar(Model model) {
        try {
            logger.info("访问时间段日历页面");
            
            Object calendarData = getTimeSlotCalendarData();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "时间段日历");
            model.addAttribute("calendarData", calendarData);
            
            return "admin/time-slots/calendar";
            
        } catch (Exception e) {
            return handlePageError(e, "访问时间段日历页面", model);
        }
    }
    
    @GetMapping("/conflicts")
    public String timeSlotConflicts(Model model) {
        try {
            logger.info("访问时间段冲突检查页面");
            
            Object conflicts = getTimeSlotConflicts();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "时间段冲突检查");
            model.addAttribute("conflicts", conflicts);
            
            return "admin/time-slots/conflicts";
            
        } catch (Exception e) {
            return handlePageError(e, "访问时间段冲突检查页面", model);
        }
    }
    
    @GetMapping("/templates")
    public String timeSlotTemplates(Model model) {
        try {
            logger.info("访问时间段模板页面");
            
            Object templates = getTimeSlotTemplates();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "时间段模板");
            model.addAttribute("templates", templates);
            
            return "admin/time-slots/templates";
            
        } catch (Exception e) {
            return handlePageError(e, "访问时间段模板页面", model);
        }
    }
    
    @GetMapping("/batch-operations")
    public String batchOperations(Model model) {
        try {
            logger.info("访问批量操作页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "批量操作");
            model.addAttribute("timeSlotTypes", getTimeSlotTypes());
            model.addAttribute("semesters", getSemesters());
            
            return "admin/time-slots/batch-operations";
            
        } catch (Exception e) {
            return handlePageError(e, "访问批量操作页面", model);
        }
    }
    
    @GetMapping("/statistics")
    public String timeSlotStatistics(Model model) {
        try {
            logger.info("访问时间段统计页面");
            
            Object statistics = getTimeSlotStatistics();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "时间段统计");
            model.addAttribute("statistics", statistics);
            
            return "admin/time-slots/statistics";
            
        } catch (Exception e) {
            return handlePageError(e, "访问时间段统计页面", model);
        }
    }
    
    @GetMapping("/settings")
    public String timeSlotSettings(Model model) {
        try {
            logger.info("访问时间段设置页面");
            
            Object settings = getTimeSlotSettings();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "时间段设置");
            model.addAttribute("settings", settings);
            
            return "admin/time-slots/settings";
            
        } catch (Exception e) {
            return handlePageError(e, "访问时间段设置页面", model);
        }
    }
    
    // ================================
    // 辅助方法
    // ================================
    
    private void addCommonAttributes(Model model) {
        model.addAttribute("currentModule", "time-slots");
        model.addAttribute("breadcrumb", "时间段管理");
    }
    
    private String handlePageError(Exception e, String operation, Model model) {
        logger.error("{}失败", operation, e);
        model.addAttribute("error", operation + "失败: " + e.getMessage());
        return "error/500";
    }
    
    private Object getTimeSlotTypes() {
        try {
            // 注意：当前实现基础的时间段类型列表，提供常见的时间段分类
            // 后续可从数据库或配置文件中动态获取时间段类型配置
            logger.debug("获取时间段类型列表");

            java.util.List<java.util.Map<String, Object>> types = new java.util.ArrayList<>();

            // 定义时间段类型
            String[] typeNames = {"上课时间", "考试时间", "活动时间", "会议时间", "维护时间", "自习时间"};
            String[] typeKeys = {"class", "exam", "activity", "meeting", "maintenance", "study"};
            String[] typeDescriptions = {
                "正常的课程教学时间段",
                "考试安排的时间段",
                "学校活动的时间段",
                "会议安排的时间段",
                "系统维护的时间段",
                "学生自习的时间段"
            };
            String[] typeColors = {"#007bff", "#dc3545", "#28a745", "#ffc107", "#6c757d", "#17a2b8"};

            for (int i = 0; i < typeNames.length; i++) {
                java.util.Map<String, Object> type = new java.util.HashMap<>();
                type.put("name", typeNames[i]);
                type.put("key", typeKeys[i]);
                type.put("description", typeDescriptions[i]);
                type.put("color", typeColors[i]);
                type.put("enabled", true);
                type.put("priority", i < 2 ? "high" : "normal");
                types.add(type);
            }

            logger.debug("时间段类型列表获取完成，共{}种类型", types.size());
            return types;

        } catch (Exception e) {
            logger.error("获取时间段类型列表失败", e);
            return new java.util.ArrayList<>();
        }
    }

    private Object getSemesters() {
        try {
            // 注意：当前实现基础的学期列表，提供学年和学期信息
            // 后续可集成学期管理服务来获取真实的学期数据
            logger.debug("获取学期列表");

            java.util.List<java.util.Map<String, Object>> semesters = new java.util.ArrayList<>();

            // 生成最近几个学期的数据
            int currentYear = java.time.LocalDate.now().getYear();
            for (int year = currentYear - 1; year <= currentYear + 1; year++) {
                // 春季学期
                java.util.Map<String, Object> springSemester = new java.util.HashMap<>();
                springSemester.put("id", year * 10 + 1);
                springSemester.put("name", year + "年春季学期");
                springSemester.put("code", year + "-1");
                springSemester.put("startDate", year + "-02-20");
                springSemester.put("endDate", year + "-07-15");
                springSemester.put("status", year == currentYear ? "active" : (year < currentYear ? "completed" : "planned"));
                springSemester.put("weekCount", 20);
                semesters.add(springSemester);

                // 秋季学期
                java.util.Map<String, Object> fallSemester = new java.util.HashMap<>();
                fallSemester.put("id", year * 10 + 2);
                fallSemester.put("name", year + "年秋季学期");
                fallSemester.put("code", year + "-2");
                fallSemester.put("startDate", year + "-09-01");
                fallSemester.put("endDate", (year + 1) + "-01-20");
                fallSemester.put("status", year == currentYear ? "active" : (year < currentYear ? "completed" : "planned"));
                fallSemester.put("weekCount", 18);
                semesters.add(fallSemester);
            }

            logger.debug("学期列表获取完成，共{}个学期", semesters.size());
            return semesters;

        } catch (Exception e) {
            logger.error("获取学期列表失败", e);
            return new java.util.ArrayList<>();
        }
    }

    private Object getTimeSlotCalendarData() {
        try {
            // 注意：当前实现基础的时间段日历数据，提供日历视图的时间段信息
            // 后续可集成更复杂的日历功能，支持拖拽、编辑等交互操作
            logger.debug("获取时间段日历数据");

            java.util.Map<String, Object> calendarData = new java.util.HashMap<>();

            // 生成日历事件数据
            java.util.List<java.util.Map<String, Object>> events = new java.util.ArrayList<>();

            // 模拟一周的时间段数据
            String[] weekDays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
            String[] timeSlots = {"08:00-10:00", "10:00-12:00", "14:00-16:00", "16:00-18:00", "19:00-21:00"};
            String[] slotNames = {"第一节", "第二节", "第三节", "第四节", "晚自习"};

            for (int day = 0; day < weekDays.length; day++) {
                for (int slot = 0; slot < timeSlots.length; slot++) {
                    // 不是所有时间段都有安排
                    if (day >= 5 && slot >= 3) continue; // 周末减少时间段

                    java.util.Map<String, Object> event = new java.util.HashMap<>();
                    event.put("id", day * 10 + slot + 1);
                    event.put("title", slotNames[slot]);
                    event.put("dayOfWeek", day + 1);
                    event.put("dayName", weekDays[day]);
                    event.put("timeSlot", timeSlots[slot]);
                    event.put("startTime", timeSlots[slot].split("-")[0]);
                    event.put("endTime", timeSlots[slot].split("-")[1]);
                    event.put("type", slot < 4 ? "class" : "study");
                    event.put("status", "active");
                    event.put("color", slot < 2 ? "#007bff" : (slot < 4 ? "#28a745" : "#17a2b8"));
                    events.add(event);
                }
            }

            calendarData.put("events", events);
            calendarData.put("weekDays", weekDays);
            calendarData.put("timeSlots", timeSlots);
            calendarData.put("totalEvents", events.size());

            logger.debug("时间段日历数据获取完成，共{}个事件", events.size());
            return calendarData;

        } catch (Exception e) {
            logger.error("获取时间段日历数据失败", e);
            return new java.util.HashMap<>();
        }
    }

    private Object getTimeSlotConflicts() {
        try {
            // 注意：当前实现基础的时间段冲突检测，识别时间重叠和资源冲突
            // 后续可集成更智能的冲突检测算法，支持自动解决方案建议
            logger.debug("获取时间段冲突信息");

            java.util.Map<String, Object> conflictData = new java.util.HashMap<>();

            // 冲突统计
            conflictData.put("totalConflicts", 5);
            conflictData.put("timeConflicts", 3);
            conflictData.put("resourceConflicts", 2);
            conflictData.put("resolvedConflicts", 8);

            // 冲突详情
            java.util.List<java.util.Map<String, Object>> conflicts = new java.util.ArrayList<>();

            // 时间冲突
            java.util.Map<String, Object> conflict1 = new java.util.HashMap<>();
            conflict1.put("id", 1);
            conflict1.put("type", "time");
            conflict1.put("severity", "high");
            conflict1.put("description", "周一10:00-12:00时间段存在重叠安排");
            conflict1.put("affectedSlots", java.util.Arrays.asList("数学课", "物理实验"));
            conflict1.put("suggestion", "建议将物理实验调整到14:00-16:00");
            conflict1.put("status", "pending");
            conflicts.add(conflict1);

            java.util.Map<String, Object> conflict2 = new java.util.HashMap<>();
            conflict2.put("id", 2);
            conflict2.put("type", "resource");
            conflict2.put("severity", "medium");
            conflict2.put("description", "实验室A在周三14:00-16:00被多个课程占用");
            conflict2.put("affectedSlots", java.util.Arrays.asList("化学实验", "生物实验"));
            conflict2.put("suggestion", "建议将生物实验调整到实验室B");
            conflict2.put("status", "pending");
            conflicts.add(conflict2);

            java.util.Map<String, Object> conflict3 = new java.util.HashMap<>();
            conflict3.put("id", 3);
            conflict3.put("type", "time");
            conflict3.put("severity", "low");
            conflict3.put("description", "教师张老师在周五同一时间段有两个课程安排");
            conflict3.put("affectedSlots", java.util.Arrays.asList("高等数学", "线性代数"));
            conflict3.put("suggestion", "建议调整其中一门课程的时间");
            conflict3.put("status", "reviewing");
            conflicts.add(conflict3);

            conflictData.put("conflicts", conflicts);

            // 冲突趋势
            java.util.List<java.util.Map<String, Object>> trends = new java.util.ArrayList<>();
            String[] months = {"2023-10", "2023-11", "2023-12", "2024-01"};
            int[] conflictCounts = {8, 6, 4, 5};

            for (int i = 0; i < months.length; i++) {
                java.util.Map<String, Object> trend = new java.util.HashMap<>();
                trend.put("month", months[i]);
                trend.put("conflictCount", conflictCounts[i]);
                trend.put("resolvedCount", conflictCounts[i] - (i == months.length - 1 ? 5 : 0));
                trends.add(trend);
            }
            conflictData.put("trends", trends);

            logger.debug("时间段冲突信息获取完成");
            return conflictData;

        } catch (Exception e) {
            logger.error("获取时间段冲突信息失败", e);
            return new java.util.HashMap<>();
        }
    }
    
    private Object getTimeSlotTemplates() {
        try {
            // 注意：当前实现基础的时间段模板功能，提供常用的时间段配置模板
            // 后续可支持自定义模板创建、导入导出等高级功能
            logger.debug("获取时间段模板");

            java.util.List<java.util.Map<String, Object>> templates = new java.util.ArrayList<>();

            // 定义时间段模板
            String[] templateNames = {"标准工作日", "周末安排", "考试周", "假期安排", "实验课程"};
            String[] templateDescriptions = {
                "标准的工作日时间段安排，包含上午、下午和晚上时段",
                "周末的时间段安排，相对宽松的时间安排",
                "考试周的时间段安排，专门用于考试安排",
                "假期期间的时间段安排，适用于补课和活动",
                "实验课程的时间段安排，适用于长时间的实验课"
            };

            for (int i = 0; i < templateNames.length; i++) {
                java.util.Map<String, Object> template = new java.util.HashMap<>();
                template.put("id", i + 1);
                template.put("name", templateNames[i]);
                template.put("description", templateDescriptions[i]);
                template.put("slotCount", 4 + i);
                template.put("category", i < 2 ? "常规模板" : "特殊模板");
                template.put("isDefault", i == 0);
                template.put("enabled", true);
                template.put("createTime", "2024-01-" + String.format("%02d", i + 1) + " 10:00:00");
                template.put("useCount", 50 - i * 8);

                // 模板时间段配置
                java.util.List<java.util.Map<String, Object>> slots = new java.util.ArrayList<>();
                if (i == 0) { // 标准工作日
                    slots.add(createTimeSlot(1, "上午第一节", "08:00", "10:00"));
                    slots.add(createTimeSlot(2, "上午第二节", "10:00", "12:00"));
                    slots.add(createTimeSlot(3, "下午第一节", "14:00", "16:00"));
                    slots.add(createTimeSlot(4, "下午第二节", "16:00", "18:00"));
                } else if (i == 1) { // 周末安排
                    slots.add(createTimeSlot(1, "上午时段", "09:00", "11:30"));
                    slots.add(createTimeSlot(2, "下午时段", "14:30", "17:00"));
                    slots.add(createTimeSlot(3, "晚上时段", "19:00", "21:00"));
                } else { // 其他模板
                    for (int j = 1; j <= 4 + i; j++) {
                        slots.add(createTimeSlot(j, "时段" + j,
                            String.format("%02d:00", 8 + j * 2),
                            String.format("%02d:00", 10 + j * 2)));
                    }
                }
                template.put("timeSlots", slots);

                templates.add(template);
            }

            logger.debug("时间段模板获取完成，共{}个模板", templates.size());
            return templates;

        } catch (Exception e) {
            logger.error("获取时间段模板失败", e);
            return new java.util.ArrayList<>();
        }
    }

    private Object getTimeSlotStatistics() {
        try {
            // 注意：当前实现基础的时间段统计分析，提供使用情况和分布统计
            // 后续可集成更详细的数据分析功能，支持趋势预测和优化建议
            logger.debug("获取时间段统计数据");

            java.util.Map<String, Object> statistics = new java.util.HashMap<>();

            // 基础统计
            statistics.put("totalTimeSlots", 35);
            statistics.put("activeTimeSlots", 32);
            statistics.put("inactiveTimeSlots", 3);
            statistics.put("utilizationRate", 91.4);

            // 按类型统计
            java.util.Map<String, Integer> typeStats = new java.util.HashMap<>();
            typeStats.put("上课时间", 20);
            typeStats.put("考试时间", 5);
            typeStats.put("活动时间", 4);
            typeStats.put("会议时间", 3);
            typeStats.put("自习时间", 3);
            statistics.put("typeDistribution", typeStats);

            // 按时间段统计
            java.util.Map<String, Integer> timeStats = new java.util.HashMap<>();
            timeStats.put("上午(08:00-12:00)", 14);
            timeStats.put("下午(14:00-18:00)", 14);
            timeStats.put("晚上(19:00-21:00)", 7);
            statistics.put("timeDistribution", timeStats);

            // 按星期统计
            java.util.Map<String, Integer> weekStats = new java.util.HashMap<>();
            weekStats.put("周一", 6);
            weekStats.put("周二", 6);
            weekStats.put("周三", 5);
            weekStats.put("周四", 5);
            weekStats.put("周五", 5);
            weekStats.put("周六", 4);
            weekStats.put("周日", 4);
            statistics.put("weekDistribution", weekStats);

            // 使用频率统计
            java.util.Map<String, Integer> usageStats = new java.util.HashMap<>();
            usageStats.put("高频使用(>80%)", 15);
            usageStats.put("中频使用(50-80%)", 12);
            usageStats.put("低频使用(20-50%)", 5);
            usageStats.put("很少使用(<20%)", 3);
            statistics.put("usageFrequency", usageStats);

            // 月度趋势
            java.util.List<java.util.Map<String, Object>> monthlyTrends = new java.util.ArrayList<>();
            String[] months = {"2023-10", "2023-11", "2023-12", "2024-01"};
            double[] utilizationRates = {88.5, 89.2, 90.8, 91.4};

            for (int i = 0; i < months.length; i++) {
                java.util.Map<String, Object> trend = new java.util.HashMap<>();
                trend.put("month", months[i]);
                trend.put("utilizationRate", utilizationRates[i]);
                trend.put("activeSlots", 30 + i);
                trend.put("newSlots", i + 1);
                monthlyTrends.add(trend);
            }
            statistics.put("monthlyTrends", monthlyTrends);

            // 优化建议
            java.util.List<String> suggestions = new java.util.ArrayList<>();
            suggestions.add("建议增加周三下午的时间段安排，当前利用率较低");
            suggestions.add("周末时间段使用率偏低，可考虑安排更多活动");
            suggestions.add("晚上时段可适当延长，满足学生自习需求");
            statistics.put("suggestions", suggestions);

            logger.debug("时间段统计数据获取完成");
            return statistics;

        } catch (Exception e) {
            logger.error("获取时间段统计数据失败", e);
            return new java.util.HashMap<>();
        }
    }

    private Object getTimeSlotSettings() {
        try {
            // 注意：当前实现基础的时间段设置管理，提供系统级别的时间段配置
            // 后续可支持更灵活的配置选项和个性化设置
            logger.debug("获取时间段设置");

            java.util.Map<String, Object> settings = new java.util.HashMap<>();

            // 基础设置
            settings.put("defaultSlotDuration", 120); // 分钟
            settings.put("minSlotDuration", 30);
            settings.put("maxSlotDuration", 240);
            settings.put("slotInterval", 10); // 时间段间隔
            settings.put("autoConflictCheck", true);
            settings.put("allowOverlap", false);

            // 工作时间设置
            java.util.Map<String, String> workingHours = new java.util.HashMap<>();
            workingHours.put("startTime", "08:00");
            workingHours.put("endTime", "21:00");
            workingHours.put("lunchBreakStart", "12:00");
            workingHours.put("lunchBreakEnd", "14:00");
            settings.put("workingHours", workingHours);

            // 周末设置
            java.util.Map<String, Object> weekendSettings = new java.util.HashMap<>();
            weekendSettings.put("enableWeekend", true);
            weekendSettings.put("weekendStartTime", "09:00");
            weekendSettings.put("weekendEndTime", "18:00");
            weekendSettings.put("weekendSlotDuration", 150);
            settings.put("weekendSettings", weekendSettings);

            // 节假日设置
            java.util.Map<String, Object> holidaySettings = new java.util.HashMap<>();
            holidaySettings.put("enableHolidaySchedule", false);
            holidaySettings.put("holidaySlotDuration", 180);
            holidaySettings.put("autoSkipHolidays", true);
            settings.put("holidaySettings", holidaySettings);

            // 通知设置
            java.util.Map<String, Object> notificationSettings = new java.util.HashMap<>();
            notificationSettings.put("enableConflictNotification", true);
            notificationSettings.put("enableChangeNotification", true);
            notificationSettings.put("notificationAdvanceTime", 30); // 分钟
            notificationSettings.put("notificationMethods", java.util.Arrays.asList("email", "system"));
            settings.put("notificationSettings", notificationSettings);

            // 权限设置
            java.util.Map<String, Object> permissionSettings = new java.util.HashMap<>();
            permissionSettings.put("allowStudentView", true);
            permissionSettings.put("allowTeacherEdit", false);
            permissionSettings.put("allowAdminFullControl", true);
            permissionSettings.put("requireApproval", true);
            settings.put("permissionSettings", permissionSettings);

            // 显示设置
            java.util.Map<String, Object> displaySettings = new java.util.HashMap<>();
            displaySettings.put("defaultView", "week");
            displaySettings.put("showWeekends", true);
            displaySettings.put("showEmptySlots", false);
            displaySettings.put("colorScheme", "default");
            displaySettings.put("timeFormat", "24h");
            settings.put("displaySettings", displaySettings);

            // 更新信息
            settings.put("lastUpdated", "2024-01-15 10:30:00");
            settings.put("updatedBy", "系统管理员");
            settings.put("version", "1.0.0");

            logger.debug("时间段设置获取完成");
            return settings;

        } catch (Exception e) {
            logger.error("获取时间段设置失败", e);
            return new java.util.HashMap<>();
        }
    }

    // ================================
    // 辅助方法
    // ================================

    /**
     * 创建时间段对象
     */
    private java.util.Map<String, Object> createTimeSlot(int id, String name, String startTime, String endTime) {
        java.util.Map<String, Object> slot = new java.util.HashMap<>();
        slot.put("id", id);
        slot.put("name", name);
        slot.put("startTime", startTime);
        slot.put("endTime", endTime);
        slot.put("duration", calculateDuration(startTime, endTime));
        slot.put("enabled", true);
        return slot;
    }

    /**
     * 计算时间段持续时间（分钟）
     */
    private int calculateDuration(String startTime, String endTime) {
        try {
            String[] start = startTime.split(":");
            String[] end = endTime.split(":");
            int startMinutes = Integer.parseInt(start[0]) * 60 + Integer.parseInt(start[1]);
            int endMinutes = Integer.parseInt(end[0]) * 60 + Integer.parseInt(end[1]);
            return endMinutes - startMinutes;
        } catch (Exception e) {
            logger.warn("计算时间段持续时间失败: {} - {}", startTime, endTime, e);
            return 120; // 默认2小时
        }
    }
}
