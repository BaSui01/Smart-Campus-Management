package com.campus.interfaces.rest.academic;

import com.campus.application.service.academic.CourseScheduleService;
import com.campus.application.service.academic.CourseService;
import com.campus.application.service.academic.TimeSlotService;
import com.campus.application.service.auth.UserService;
import com.campus.application.service.infrastructure.ClassroomService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 课程安排页面控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Controller
@RequestMapping("/admin/course-schedule")
public class CourseScheduleController {

    private static final Logger logger = LoggerFactory.getLogger(CourseScheduleController.class);

    private final CourseScheduleService courseScheduleService;
    private final CourseService courseService;
    private final UserService userService;
    private final ClassroomService classroomService;
    private final TimeSlotService timeSlotService;

    public CourseScheduleController(CourseScheduleService courseScheduleService,
                                   CourseService courseService,
                                   UserService userService,
                                   ClassroomService classroomService,
                                   TimeSlotService timeSlotService) {
        this.courseScheduleService = courseScheduleService;
        this.courseService = courseService;
        this.userService = userService;
        this.classroomService = classroomService;
        this.timeSlotService = timeSlotService;
    }
    
    // ================================
    // 页面路由
    // ================================
    
    @GetMapping
    public String scheduleList(Model model) {
        try {
            logger.info("访问课程安排管理页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "课程安排");
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("teachers", userService.findTeachers());
            model.addAttribute("classrooms", classroomService.findAvailableClassrooms());
            model.addAttribute("timeSlots", timeSlotService.findAvailableTimeSlots());
            
            return "admin/course-schedule/list";
            
        } catch (Exception e) {
            return handlePageError(e, "访问课程安排管理页面", model);
        }
    }
    
    @GetMapping("/create")
    public String createSchedulePage(Model model) {
        try {
            logger.info("访问创建课程安排页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "创建课程安排");
            model.addAttribute("action", "create");
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("teachers", userService.findTeachers());
            model.addAttribute("classrooms", classroomService.findAvailableClassrooms());
            model.addAttribute("timeSlots", timeSlotService.findAvailableTimeSlots());
            
            return "admin/course-schedule/form";
            
        } catch (Exception e) {
            return handlePageError(e, "访问创建课程安排页面", model);
        }
    }
    
    @GetMapping("/{scheduleId}/edit")
    public String editSchedulePage(@PathVariable Long scheduleId, Model model) {
        try {
            logger.info("访问编辑课程安排页面: scheduleId={}", scheduleId);
            
            Object schedule = courseScheduleService.getScheduleById(scheduleId);
            if (schedule == null) {
                model.addAttribute("error", "课程安排不存在");
                return "error/404";
            }
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "编辑课程安排");
            model.addAttribute("action", "edit");
            model.addAttribute("schedule", schedule);
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("teachers", userService.findTeachers());
            model.addAttribute("classrooms", classroomService.findAvailableClassrooms());
            model.addAttribute("timeSlots", timeSlotService.findAvailableTimeSlots());
            
            return "admin/course-schedule/form";
            
        } catch (Exception e) {
            return handlePageError(e, "访问编辑课程安排页面", model);
        }
    }
    
    @GetMapping("/calendar")
    public String scheduleCalendar(Model model) {
        try {
            logger.info("访问课程安排日历页面");
            
            Object calendarData = courseScheduleService.getScheduleCalendar();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "课程安排日历");
            model.addAttribute("calendarData", calendarData);
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("teachers", userService.findTeachers());
            
            return "admin/course-schedule/calendar";
            
        } catch (Exception e) {
            return handlePageError(e, "访问课程安排日历页面", model);
        }
    }
    
    @GetMapping("/timetable")
    public String courseTimetable(Model model) {
        try {
            logger.info("访问课程表页面");
            
            Object timetableData = courseScheduleService.getTimetableData();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "课程表");
            model.addAttribute("timetableData", timetableData);
            model.addAttribute("teachers", userService.findTeachers());
            model.addAttribute("classrooms", classroomService.findAvailableClassrooms());
            
            return "admin/course-schedule/timetable";
            
        } catch (Exception e) {
            return handlePageError(e, "访问课程表页面", model);
        }
    }
    
    @GetMapping("/conflicts")
    public String scheduleConflicts(Model model) {
        try {
            logger.info("访问课程冲突检查页面");
            
            Object conflicts = courseScheduleService.getScheduleConflicts();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "课程冲突检查");
            model.addAttribute("conflicts", conflicts);
            
            return "admin/course-schedule/conflicts";
            
        } catch (Exception e) {
            return handlePageError(e, "访问课程冲突检查页面", model);
        }
    }
    
    @GetMapping("/auto-schedule")
    public String autoSchedule(Model model) {
        try {
            logger.info("访问自动排课页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "自动排课");
            model.addAttribute("courses", courseService.findUnscheduledCourses());
            model.addAttribute("teachers", userService.findTeachers());
            model.addAttribute("classrooms", classroomService.findAvailableClassrooms());
            
            return "admin/course-schedule/auto-schedule";
            
        } catch (Exception e) {
            return handlePageError(e, "访问自动排课页面", model);
        }
    }
    
    @GetMapping("/teacher/{teacherId}")
    public String teacherSchedule(@PathVariable Long teacherId, Model model) {
        try {
            logger.info("访问教师课程表页面: teacherId={}", teacherId);
            
            Object teacher = userService.getUserById(teacherId);
            if (teacher == null) {
                model.addAttribute("error", "教师不存在");
                return "error/404";
            }
            
            Object teacherSchedule = courseScheduleService.getTeacherSchedule(teacherId);
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "教师课程表");
            model.addAttribute("teacher", teacher);
            model.addAttribute("schedule", teacherSchedule);
            
            return "admin/course-schedule/teacher-schedule";
            
        } catch (Exception e) {
            return handlePageError(e, "访问教师课程表页面", model);
        }
    }
    
    @GetMapping("/classroom/{classroomId}")
    public String classroomSchedule(@PathVariable Long classroomId, Model model) {
        try {
            logger.info("访问教室使用安排页面: classroomId={}", classroomId);
            
            Object classroom = classroomService.getClassroomById(classroomId);
            if (classroom == null) {
                model.addAttribute("error", "教室不存在");
                return "error/404";
            }
            
            Object classroomSchedule = courseScheduleService.getClassroomSchedule(classroomId);
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "教室使用安排");
            model.addAttribute("classroom", classroom);
            model.addAttribute("schedule", classroomSchedule);
            
            return "admin/course-schedule/classroom-schedule";
            
        } catch (Exception e) {
            return handlePageError(e, "访问教室使用安排页面", model);
        }
    }
    
    @GetMapping("/batch-operations")
    public String batchOperations(Model model) {
        try {
            logger.info("访问批量操作页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "批量操作");
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("teachers", userService.findTeachers());
            model.addAttribute("classrooms", classroomService.findAvailableClassrooms());
            
            return "admin/course-schedule/batch-operations";
            
        } catch (Exception e) {
            return handlePageError(e, "访问批量操作页面", model);
        }
    }
    
    @GetMapping("/templates")
    public String scheduleTemplates(Model model) {
        try {
            logger.info("访问排课模板页面");
            
            Object templates = courseScheduleService.getScheduleTemplates();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "排课模板");
            model.addAttribute("templates", templates);
            
            return "admin/course-schedule/templates";
            
        } catch (Exception e) {
            return handlePageError(e, "访问排课模板页面", model);
        }
    }
    
    @GetMapping("/statistics")
    public String scheduleStatistics(Model model) {
        try {
            logger.info("访问排课统计页面");
            
            Object statistics = courseScheduleService.getScheduleStatistics();
            Object utilizationStats = courseScheduleService.getClassroomUtilizationStats();
            Object teacherWorkloadStats = courseScheduleService.getTeacherWorkloadStats();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "排课统计");
            model.addAttribute("statistics", statistics);
            model.addAttribute("utilizationStats", utilizationStats);
            model.addAttribute("teacherWorkloadStats", teacherWorkloadStats);
            
            return "admin/course-schedule/statistics";
            
        } catch (Exception e) {
            return handlePageError(e, "访问排课统计页面", model);
        }
    }
    
    // ================================
    // 辅助方法
    // ================================
    
    /**
     * 添加通用页面属性
     */
    private void addCommonAttributes(Model model) {
        model.addAttribute("currentModule", "course-schedule");
        model.addAttribute("breadcrumb", "课程安排");
    }
    
    /**
     * 处理页面错误
     */
    private String handlePageError(Exception e, String operation, Model model) {
        logger.error("{}失败", operation, e);
        model.addAttribute("error", operation + "失败: " + e.getMessage());
        return "error/500";
    }
}
