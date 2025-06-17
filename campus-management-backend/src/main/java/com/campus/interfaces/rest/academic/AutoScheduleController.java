package com.campus.interfaces.rest.academic;

import com.campus.application.service.academic.AutoScheduleService;
import com.campus.application.service.academic.CourseService;
import com.campus.domain.entity.academic.Course;
import com.campus.domain.entity.academic.CourseSchedule;
import com.campus.domain.repository.academic.TimeSlotRepository;
import com.campus.domain.repository.infrastructure.ClassroomRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自动排课Web控制器
 * 提供排课管理的Web界面
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Controller
@RequestMapping("/admin/auto-schedule")
public class AutoScheduleController {

    private final AutoScheduleService autoScheduleService;
    private final CourseService courseService;
    private final ClassroomRepository classroomRepository;
    private final TimeSlotRepository timeSlotRepository;

    public AutoScheduleController(AutoScheduleService autoScheduleService,
                                 CourseService courseService,
                                 ClassroomRepository classroomRepository,
                                 TimeSlotRepository timeSlotRepository) {
        this.autoScheduleService = autoScheduleService;
        this.courseService = courseService;
        this.classroomRepository = classroomRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    /**
     * 自动排课主页面
     */
    @GetMapping
    public String index(Model model) {
        model.addAttribute("pageTitle", "智能排课管理");
        model.addAttribute("activeMenu", "autoSchedule");
        
        // 获取基础统计数据
        long totalCourses = courseService.count();
        long totalClassrooms = classroomRepository.countByDeletedNot();
        long totalTimeSlots = timeSlotRepository.countByDeletedNot();
        
        model.addAttribute("totalCourses", totalCourses);
        model.addAttribute("totalClassrooms", totalClassrooms);
        model.addAttribute("totalTimeSlots", totalTimeSlots);
        
        return "admin/auto_schedule/index";
    }

    /**
     * 排课配置页面
     */
    @GetMapping("/config")
    public String config(Model model) {
        model.addAttribute("pageTitle", "排课配置");
        model.addAttribute("activeMenu", "autoSchedule");
        
        // 获取可用资源
        model.addAttribute("classrooms", classroomRepository.findActiveClassrooms());
        model.addAttribute("timeSlots", timeSlotRepository.findActiveTimeSlots());
        
        // 获取算法配置
        Map<String, Object> algorithmConfig = new HashMap<>();
        algorithmConfig.put("algorithm", "greedy");
        algorithmConfig.put("maxIterations", 1000);
        algorithmConfig.put("allowCombinedClass", true);
        algorithmConfig.put("maxClassSize", 150);
        
        Map<String, Double> conflictWeights = new HashMap<>();
        conflictWeights.put("teacher", 1.0);
        conflictWeights.put("classroom", 1.0);
        conflictWeights.put("student", 0.8);
        algorithmConfig.put("conflictWeights", conflictWeights);
        
        Map<String, Double> timePreferences = new HashMap<>();
        timePreferences.put("morning", 1.2);
        timePreferences.put("afternoon", 1.0);
        timePreferences.put("evening", 0.8);
        algorithmConfig.put("timePreferences", timePreferences);
        
        model.addAttribute("algorithmConfig", algorithmConfig);
        
        return "admin/auto_schedule/config";
    }

    /**
     * 课程排课页面
     */
    @GetMapping("/schedule-courses")
    public String scheduleCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String semester,
            @RequestParam(defaultValue = "") String courseType,
            Model model) {
        
        model.addAttribute("pageTitle", "课程排课");
        model.addAttribute("activeMenu", "autoSchedule");
        
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (!search.isEmpty()) {
            params.put("search", search);
        }
        if (!semester.isEmpty()) {
            params.put("semester", semester);
        }
        if (!courseType.isEmpty()) {
            params.put("courseType", courseType);
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursePage = courseService.findCoursesByPage(pageable, params);
        
        model.addAttribute("coursePage", coursePage);
        model.addAttribute("search", search);
        model.addAttribute("semester", semester);
        model.addAttribute("courseType", courseType);
        
        // 获取学期列表
        List<String> semesters = List.of("2024-2025-1", "2024-2025-2", "2025-2026-1", "2025-2026-2");
        model.addAttribute("semesters", semesters);
        
        // 获取课程类型列表
        List<String> courseTypes = List.of("必修课", "选修课", "限选课", "实践课");
        model.addAttribute("courseTypes", courseTypes);
        
        return "admin/auto_schedule/schedule_courses";
    }

    /**
     * 排课结果页面
     */
    @GetMapping("/results")
    public String results(
            @RequestParam(defaultValue = "") String semester,
            @RequestParam(defaultValue = "") String academicYear,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        
        model.addAttribute("pageTitle", "排课结果");
        model.addAttribute("activeMenu", "autoSchedule");
        
        if (!semester.isEmpty() && !academicYear.isEmpty()) {
            try {
                Integer year = Integer.valueOf(academicYear);
                
                // 获取排课统计
                AutoScheduleService.ScheduleStatistics statistics = 
                    autoScheduleService.getScheduleStatistics(semester, year);
                model.addAttribute("statistics", statistics);
                
                // 获取排课报告
                Map<String, Object> report = autoScheduleService.generateScheduleReport(semester, year);
                model.addAttribute("report", report);
                
                // 获取课程表数据（简化版）
                List<CourseSchedule> schedules = List.of(); // 这里应该从service获取实际数据
                model.addAttribute("schedules", schedules);
                
            } catch (NumberFormatException e) {
                model.addAttribute("error", "学年格式错误");
            }
        }
        
        model.addAttribute("semester", semester);
        model.addAttribute("academicYear", academicYear);
        
        // 获取学期和学年选项
        List<String> semesters = List.of("2024-2025-1", "2024-2025-2", "2025-2026-1", "2025-2026-2");
        List<String> academicYears = List.of("2024", "2025", "2026");
        model.addAttribute("semesters", semesters);
        model.addAttribute("academicYears", academicYears);
        
        return "admin/auto_schedule/results";
    }

    /**
     * 冲突检查页面
     */
    @GetMapping("/conflicts")
    public String conflicts(
            @RequestParam(defaultValue = "") String semester,
            @RequestParam(defaultValue = "") String academicYear,
            Model model) {
        
        model.addAttribute("pageTitle", "冲突检查");
        model.addAttribute("activeMenu", "autoSchedule");
        
        if (!semester.isEmpty() && !academicYear.isEmpty()) {
            try {
                // 验证学年格式
                Integer.valueOf(academicYear);

                // 获取冲突信息（这里需要实现具体的冲突检查逻辑）
                List<AutoScheduleService.ConflictInfo> conflicts = List.of();
                model.addAttribute("conflicts", conflicts);
                
                // 按冲突类型分组统计
                Map<String, Long> conflictStats = new HashMap<>();
                conflictStats.put("teacher", 0L);
                conflictStats.put("classroom", 0L);
                conflictStats.put("student", 0L);
                model.addAttribute("conflictStats", conflictStats);
                
            } catch (NumberFormatException e) {
                model.addAttribute("error", "学年格式错误");
            }
        }
        
        model.addAttribute("semester", semester);
        model.addAttribute("academicYear", academicYear);
        
        // 获取学期和学年选项
        List<String> semesters = List.of("2024-2025-1", "2024-2025-2", "2025-2026-1", "2025-2026-2");
        List<String> academicYears = List.of("2024", "2025", "2026");
        model.addAttribute("semesters", semesters);
        model.addAttribute("academicYears", academicYears);
        
        return "admin/auto_schedule/conflicts";
    }

    /**
     * 教室管理页面
     */
    @GetMapping("/classrooms")
    public String classrooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String classroomType,
            @RequestParam(defaultValue = "") String status,
            Model model) {
        
        model.addAttribute("pageTitle", "教室管理");
        model.addAttribute("activeMenu", "autoSchedule");
        
        // 构建查询参数
        Integer statusValue = status.isEmpty() ? null : Integer.valueOf(status);
        
        List<com.campus.domain.entity.infrastructure.Classroom> classrooms = classroomRepository.searchClassrooms(
            search.isEmpty() ? null : search,
            classroomType.isEmpty() ? null : classroomType,
            null, null, null, statusValue
        );
        
        model.addAttribute("classrooms", classrooms);
        model.addAttribute("search", search);
        model.addAttribute("classroomType", classroomType);
        model.addAttribute("status", status);
        
        // 获取教室类型列表
        List<String> classroomTypes = List.of("普通教室", "多媒体教室", "实验室", "大阶梯教室", "机房");
        model.addAttribute("classroomTypes", classroomTypes);
        
        // 获取教室统计
        Object[] capacityStats = classroomRepository.getCapacityStatistics();
        if (capacityStats != null && capacityStats.length >= 3) {
            model.addAttribute("minCapacity", capacityStats[0]);
            model.addAttribute("maxCapacity", capacityStats[1]);
            model.addAttribute("avgCapacity", capacityStats[2]);
        }
        
        return "admin/auto_schedule/classrooms";
    }

    /**
     * 时间段管理页面
     */
    @GetMapping("/time-slots")
    public String timeSlots(
            @RequestParam(defaultValue = "") String dayOfWeek,
            @RequestParam(defaultValue = "") String slotType,
            @RequestParam(defaultValue = "") String status,
            Model model) {
        
        model.addAttribute("pageTitle", "时间段管理");
        model.addAttribute("activeMenu", "autoSchedule");
        
        // 构建查询参数
        Integer dayValue = dayOfWeek.isEmpty() ? null : Integer.valueOf(dayOfWeek);
        Integer statusValue = status.isEmpty() ? null : Integer.valueOf(status);
        
        List<com.campus.domain.entity.academic.TimeSlot> timeSlots = timeSlotRepository.searchTimeSlots(
            null, dayValue, null, slotType.isEmpty() ? null : slotType, statusValue
        );
        
        model.addAttribute("timeSlots", timeSlots);
        model.addAttribute("dayOfWeek", dayOfWeek);
        model.addAttribute("slotType", slotType);
        model.addAttribute("status", status);
        
        // 获取选项列表
        Map<Integer, String> dayOptions = new HashMap<>();
        dayOptions.put(1, "周一");
        dayOptions.put(2, "周二");
        dayOptions.put(3, "周三");
        dayOptions.put(4, "周四");
        dayOptions.put(5, "周五");
        dayOptions.put(6, "周六");
        dayOptions.put(7, "周日");
        model.addAttribute("dayOptions", dayOptions);
        
        List<String> slotTypes = List.of("morning", "afternoon", "evening");
        model.addAttribute("slotTypes", slotTypes);
        
        // 获取时间段统计
        Object[] timeSlotStats = timeSlotRepository.getTimeSlotStatistics();
        if (timeSlotStats != null && timeSlotStats.length >= 3) {
            model.addAttribute("totalSlots", timeSlotStats[0]);
            model.addAttribute("activeSlots", timeSlotStats[1]);
            model.addAttribute("inactiveSlots", timeSlotStats[2]);
        }
        
        return "admin/auto_schedule/time_slots";
    }

    /**
     * 算法设置页面
     */
    @GetMapping("/algorithm-settings")
    public String algorithmSettings(Model model) {
        model.addAttribute("pageTitle", "算法设置");
        model.addAttribute("activeMenu", "autoSchedule");
        
        // 获取当前算法配置
        Map<String, Object> currentConfig = new HashMap<>();
        currentConfig.put("algorithm", "greedy");
        currentConfig.put("maxIterations", 1000);
        currentConfig.put("timeoutSeconds", 300);
        currentConfig.put("allowCombinedClass", true);
        currentConfig.put("maxClassSize", 150);
        currentConfig.put("prioritizeTeacherPreference", true);
        currentConfig.put("prioritizeTimeSlotBalance", true);
        
        model.addAttribute("currentConfig", currentConfig);
        
        // 可选算法列表
        Map<String, String> algorithms = new HashMap<>();
        algorithms.put("greedy", "贪心算法");
        algorithms.put("genetic", "遗传算法");
        algorithms.put("simulated_annealing", "模拟退火算法");
        algorithms.put("constraint_satisfaction", "约束满足算法");
        model.addAttribute("algorithms", algorithms);
        
        return "admin/auto_schedule/algorithm_settings";
    }

    /**
     * 排课历史页面
     */
    @GetMapping("/history")
    public String history(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        model.addAttribute("pageTitle", "排课历史");
        model.addAttribute("activeMenu", "autoSchedule");
        
        // 模拟排课历史数据
        List<Map<String, Object>> historyRecords = List.of(
            Map.of(
                "id", 1L,
                "semester", "2024-2025-1",
                "academicYear", 2024,
                "totalCourses", 120,
                "successCount", 115,
                "conflictCount", 5,
                "successRate", 95.8,
                "executeTime", "2024-08-15 10:30:00",
                "executor", "系统管理员",
                "status", "已完成"
            ),
            Map.of(
                "id", 2L,
                "semester", "2024-2025-2",
                "academicYear", 2024,
                "totalCourses", 118,
                "successCount", 118,
                "conflictCount", 0,
                "successRate", 100.0,
                "executeTime", "2025-06-07-20 14:15:00",
                "executor", "教务管理员",
                "status", "已完成"
            )
        );
        
        model.addAttribute("historyRecords", historyRecords);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalRecords", historyRecords.size());
        
        return "admin/auto_schedule/history";
    }
}