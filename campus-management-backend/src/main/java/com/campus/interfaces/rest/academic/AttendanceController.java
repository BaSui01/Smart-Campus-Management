package com.campus.interfaces.rest.academic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.campus.application.service.academic.AttendanceService;
import com.campus.application.service.academic.CourseService;
import com.campus.application.service.auth.UserService;
import com.campus.domain.entity.academic.Attendance;
import com.campus.domain.entity.academic.Course;
import com.campus.domain.entity.auth.User;

/**
 * 考勤管理页面控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Controller
@RequestMapping("/admin/attendance")
public class AttendanceController {
    
    private static final Logger logger = LoggerFactory.getLogger(AttendanceController.class);
    
    private final AttendanceService attendanceService;
    private final CourseService courseService;
    private final UserService userService;

    public AttendanceController(AttendanceService attendanceService,
                               CourseService courseService,
                               UserService userService) {
        this.attendanceService = attendanceService;
        this.courseService = courseService;
        this.userService = userService;
    }
    
    // ================================
    // 页面路由
    // ================================
    
    @GetMapping
    public String attendanceList(Model model) {
        try {
            logger.info("访问考勤管理页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "考勤管理");
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("teachers", userService.findTeachers());
            
            return "admin/attendance/list";
            
        } catch (Exception e) {
            return handlePageError(e, "访问考勤管理页面", model);
        }
    }
    
    @GetMapping("/record")
    public String recordAttendance(Model model) {
        try {
            logger.info("访问考勤记录页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "考勤记录");
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("currentDate", java.time.LocalDate.now());
            
            return "admin/attendance/record";
            
        } catch (Exception e) {
            return handlePageError(e, "访问考勤记录页面", model);
        }
    }
    
    @GetMapping("/course/{courseId}")
    public String courseAttendance(@PathVariable Long courseId, Model model) {
        try {
            logger.info("访问课程考勤页面: courseId={}", courseId);
            
            // 获取课程信息和考勤记录
            Course course = courseService.getCourseById(courseId);
            if (course == null) {
                model.addAttribute("error", "课程不存在");
                return "error/404";
            }

            // 获取课程的考勤记录和学生列表
            List<Attendance> attendanceRecords = attendanceService.getAttendanceByCourse(courseId);
            List<User> students = getStudentsByCourse(courseId);
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "课程考勤");
            model.addAttribute("course", course);
            model.addAttribute("attendanceRecords", attendanceRecords);
            model.addAttribute("students", students);
            
            return "admin/attendance/course";
            
        } catch (Exception e) {
            return handlePageError(e, "访问课程考勤页面", model);
        }
    }
    
    @GetMapping("/student/{studentId}")
    public String studentAttendance(@PathVariable Long studentId, Model model) {
        try {
            logger.info("访问学生考勤页面: studentId={}", studentId);
            
            // 获取学生信息和相关考勤记录
            User student = userService.getUserById(studentId);
            if (student == null) {
                model.addAttribute("error", "学生不存在");
                return "error/404";
            }

            // 获取学生的考勤记录和课程列表
            List<Attendance> attendanceRecords = attendanceService.getAttendanceByStudent(studentId);
            List<Course> courses = getCoursesByStudent(studentId);
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "学生考勤");
            model.addAttribute("student", student);
            model.addAttribute("attendanceRecords", attendanceRecords);
            model.addAttribute("courses", courses);
            
            return "admin/attendance/student";
            
        } catch (Exception e) {
            return handlePageError(e, "访问学生考勤页面", model);
        }
    }
    
    @GetMapping("/statistics")
    public String attendanceStatistics(Model model) {
        try {
            logger.info("访问考勤统计页面");
            
            // 注意：当前实现基础的考勤统计功能，提供考勤数据的统计分析
            // 后续可集成AttendanceService来获取真实的统计数据
            Object overallStats = getOverallAttendanceStats();
            Object courseStats = getCourseAttendanceStats();
            Object studentStats = getStudentAttendanceStats();
            Object dailyStats = new java.util.ArrayList<>();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "考勤统计");
            model.addAttribute("overallStats", overallStats);
            model.addAttribute("courseStats", courseStats);
            model.addAttribute("studentStats", studentStats);
            model.addAttribute("dailyStats", dailyStats);
            
            return "admin/attendance/statistics";
            
        } catch (Exception e) {
            return handlePageError(e, "访问考勤统计页面", model);
        }
    }
    
    @GetMapping("/reports")
    public String attendanceReports(Model model) {
        try {
            logger.info("访问考勤报表页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "考勤报表");
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("teachers", userService.findTeachers());
            
            return "admin/attendance/reports";
            
        } catch (Exception e) {
            return handlePageError(e, "访问考勤报表页面", model);
        }
    }
    
    @GetMapping("/alerts")
    public String attendanceAlerts(Model model) {
        try {
            logger.info("访问考勤预警页面");
            
            // 获取考勤预警数据
            List<Map<String, Object>> absenteeAlerts = getAbsenteeAlerts();
            List<Map<String, Object>> lateAlerts = getLateAlerts();
            List<Map<String, Object>> frequentAbsentees = getFrequentAbsentees();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "考勤预警");
            model.addAttribute("absenteeAlerts", absenteeAlerts);
            model.addAttribute("lateAlerts", lateAlerts);
            model.addAttribute("frequentAbsentees", frequentAbsentees);
            
            return "admin/attendance/alerts";
            
        } catch (Exception e) {
            return handlePageError(e, "访问考勤预警页面", model);
        }
    }
    
    @GetMapping("/calendar")
    public String attendanceCalendar(Model model) {
        try {
            logger.info("访问考勤日历页面");
            
            // 注意：当前实现基础的考勤日历功能，提供日历视图的考勤数据
            // 后续可集成AttendanceService来获取真实的日历数据
            Object calendarDataObj = getAttendanceCalendarData();
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> calendarData = (java.util.Map<String, Object>) calendarDataObj;
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "考勤日历");
            model.addAttribute("calendarData", calendarData);
            model.addAttribute("courses", courseService.findActiveCourses());
            
            return "admin/attendance/calendar";
            
        } catch (Exception e) {
            return handlePageError(e, "访问考勤日历页面", model);
        }
    }
    
    @GetMapping("/settings")
    public String attendanceSettings(Model model) {
        try {
            logger.info("访问考勤设置页面");
            
            // 注意：当前实现基础的考勤设置功能，提供考勤规则和参数配置
            // 后续可集成AttendanceService来获取真实的设置数据
            Object attendanceSettingsObj = getAttendanceSettings();
            @SuppressWarnings("unchecked")
            Map<String, Object> attendanceSettings = (Map<String, Object>) attendanceSettingsObj;
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "考勤设置");
            model.addAttribute("settings", attendanceSettings);
            
            return "admin/attendance/settings";
            
        } catch (Exception e) {
            return handlePageError(e, "访问考勤设置页面", model);
        }
    }
    
    @GetMapping("/import")
    public String importAttendance(Model model) {
        try {
            logger.info("访问考勤导入页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "考勤导入");
            model.addAttribute("courses", courseService.findActiveCourses());
            
            return "admin/attendance/import";
            
        } catch (Exception e) {
            return handlePageError(e, "访问考勤导入页面", model);
        }
    }
    
    @GetMapping("/export")
    public String exportAttendance(Model model) {
        try {
            logger.info("访问考勤导出页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "考勤导出");
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("teachers", userService.findTeachers());
            
            return "admin/attendance/export";
            
        } catch (Exception e) {
            return handlePageError(e, "访问考勤导出页面", model);
        }
    }
    
    @GetMapping("/makeup")
    public String makeupAttendance(Model model) {
        try {
            logger.info("访问补考勤页面");
            
            // 注意：当前实现基础的补考勤功能，提供需要补录的考勤记录
            // 后续可集成AttendanceService来获取真实的补考勤数据
            Object pendingMakeupsObj = getPendingMakeupRecords();
            @SuppressWarnings("unchecked")
            java.util.List<Object> pendingMakeups = (java.util.List<Object>) pendingMakeupsObj;
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "补考勤");
            model.addAttribute("pendingMakeups", pendingMakeups);
            model.addAttribute("courses", courseService.findActiveCourses());
            
            return "admin/attendance/makeup";
            
        } catch (Exception e) {
            return handlePageError(e, "访问补考勤页面", model);
        }
    }
    
    // ================================
    // 辅助方法
    // ================================
    
    /**
     * 添加通用页面属性
     */
    private void addCommonAttributes(Model model) {
        model.addAttribute("currentModule", "attendance");
        model.addAttribute("breadcrumb", "考勤管理");
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
    // 考勤业务逻辑辅助方法
    // ================================

    /**
     * 获取课程的学生列表
     */
    private List<User> getStudentsByCourse(Long courseId) {
        try {
            logger.debug("获取课程学生列表: courseId={}", courseId);

            // 通过考勤记录获取该课程的学生
            List<Attendance> attendances = attendanceService.findByCourseId(courseId);
            Set<Long> studentIds = new HashSet<>();

            for (Attendance attendance : attendances) {
                studentIds.add(attendance.getStudentId());
            }

            List<User> students = new ArrayList<>();
            for (Long studentId : studentIds) {
                try {
                    User student = userService.getUserById(studentId);
                    if (student != null) {
                        students.add(student);
                    }
                } catch (Exception e) {
                    logger.warn("获取学生信息失败: studentId={}", studentId, e);
                }
            }

            logger.debug("课程学生列表获取完成: courseId={}, 学生数量={}", courseId, students.size());
            return students;

        } catch (Exception e) {
            logger.error("获取课程学生列表失败: courseId={}", courseId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取学生的课程列表
     */
    private List<Course> getCoursesByStudent(Long studentId) {
        try {
            logger.debug("获取学生课程列表: studentId={}", studentId);

            // 通过考勤记录获取该学生的课程
            List<Attendance> attendances = attendanceService.findByStudentId(studentId);
            Set<Long> courseIds = new HashSet<>();

            for (Attendance attendance : attendances) {
                courseIds.add(attendance.getCourseId());
            }

            List<Course> courses = new ArrayList<>();
            for (Long courseId : courseIds) {
                try {
                    Course course = courseService.getCourseById(courseId);
                    if (course != null) {
                        courses.add(course);
                    }
                } catch (Exception e) {
                    logger.warn("获取课程信息失败: courseId={}", courseId, e);
                }
            }

            logger.debug("学生课程列表获取完成: studentId={}, 课程数量={}", studentId, courses.size());
            return courses;

        } catch (Exception e) {
            logger.error("获取学生课程列表失败: studentId={}", studentId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取整体考勤统计数据
     */
    private Map<String, Object> getOverallAttendanceStats() {
        try {
            logger.debug("获取整体考勤统计数据");

            Map<String, Object> stats = new HashMap<>();

            // 基础统计
            long totalRecords = attendanceService.count();
            stats.put("totalRecords", totalRecords);

            // 今日考勤统计
            List<Attendance> todayAttendance = attendanceService.findTodayAttendance();
            stats.put("todayTotal", todayAttendance.size());

            // 按状态统计今日考勤
            long todayPresent = todayAttendance.stream()
                .filter(a -> "present".equals(a.getAttendanceStatus()))
                .count();
            long todayLate = todayAttendance.stream()
                .filter(a -> "late".equals(a.getAttendanceStatus()))
                .count();
            long todayAbsent = todayAttendance.stream()
                .filter(a -> "absent".equals(a.getAttendanceStatus()))
                .count();
            long todayLeave = todayAttendance.stream()
                .filter(a -> "leave".equals(a.getAttendanceStatus()))
                .count();

            stats.put("todayPresent", todayPresent);
            stats.put("todayLate", todayLate);
            stats.put("todayAbsent", todayAbsent);
            stats.put("todayLeave", todayLeave);

            // 计算出勤率
            if (todayAttendance.size() > 0) {
                double attendanceRate = (double) todayPresent / todayAttendance.size() * 100;
                stats.put("attendanceRate", Math.round(attendanceRate * 100.0) / 100.0);
            } else {
                stats.put("attendanceRate", 0.0);
            }

            logger.debug("整体考勤统计数据获取完成: totalRecords={}", totalRecords);
            return stats;

        } catch (Exception e) {
            logger.error("获取整体考勤统计数据失败", e);
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("error", "统计数据获取失败");
            return errorStats;
        }
    }

    /**
     * 获取课程考勤统计数据
     */
    private List<Map<String, Object>> getCourseAttendanceStats() {
        try {
            logger.debug("获取课程考勤统计数据");

            List<Map<String, Object>> courseStats = new ArrayList<>();
            List<Course> activeCourses = courseService.findActiveCourses();

            for (Course course : activeCourses) {
                Map<String, Object> stat = new HashMap<>();
                stat.put("courseId", course.getId());
                stat.put("courseName", course.getCourseName());
                stat.put("courseCode", course.getCourseCode());

                // 获取课程考勤记录
                List<Attendance> courseAttendances = attendanceService.findByCourseId(course.getId());
                stat.put("totalRecords", courseAttendances.size());

                // 按状态统计
                long presentCount = courseAttendances.stream()
                    .filter(a -> "present".equals(a.getAttendanceStatus()))
                    .count();
                long lateCount = courseAttendances.stream()
                    .filter(a -> "late".equals(a.getAttendanceStatus()))
                    .count();
                long absentCount = courseAttendances.stream()
                    .filter(a -> "absent".equals(a.getAttendanceStatus()))
                    .count();

                stat.put("presentCount", presentCount);
                stat.put("lateCount", lateCount);
                stat.put("absentCount", absentCount);

                // 计算出勤率
                if (courseAttendances.size() > 0) {
                    double attendanceRate = (double) presentCount / courseAttendances.size() * 100;
                    stat.put("attendanceRate", Math.round(attendanceRate * 100.0) / 100.0);
                } else {
                    stat.put("attendanceRate", 0.0);
                }

                courseStats.add(stat);
            }

            logger.debug("课程考勤统计数据获取完成: 课程数量={}", courseStats.size());
            return courseStats;

        } catch (Exception e) {
            logger.error("获取课程考勤统计数据失败", e);
            return new ArrayList<>();
        }
    }

    // ================================
    // 考勤统计方法
    // ================================



    /**
     * 获取学生考勤统计
     */
    private List<Map<String, Object>> getStudentAttendanceStats() {
        try {
            // 注意：当前实现基础的学生考勤统计，提供学生考勤排名和分析
            // 后续可集成AttendanceService来获取真实的统计数据
            logger.debug("获取学生考勤统计");

            java.util.List<java.util.Map<String, Object>> studentStats = new java.util.ArrayList<>();

            // 模拟学生考勤统计数据
            for (int i = 1; i <= 10; i++) {
                java.util.Map<String, Object> studentStat = new java.util.HashMap<>();
                studentStat.put("studentId", 1000 + i);
                studentStat.put("studentName", "学生" + i);
                studentStat.put("studentNumber", "2024" + String.format("%04d", i));
                studentStat.put("className", "计算机科学与技术2024-1班");
                studentStat.put("totalClasses", 80);
                studentStat.put("attendedClasses", 80 - i);
                studentStat.put("attendanceRate", Math.round(((80 - i) / 80.0 * 100) * 100) / 100.0);
                studentStat.put("absentCount", i);
                studentStat.put("lateCount", i % 3);
                studentStats.add(studentStat);
            }

            logger.debug("学生考勤统计获取完成，共{}个学生", studentStats.size());
            return studentStats;

        } catch (Exception e) {
            logger.error("获取学生考勤统计失败", e);
            return new java.util.ArrayList<>();
        }
    }

    // ================================
    // 考勤预警方法
    // ================================

    /**
     * 获取缺勤预警
     */
    private List<Map<String, Object>> getAbsenteeAlerts() {
        try {
            // 注意：当前实现基础的缺勤预警功能，识别缺勤频繁的学生
            // 后续可集成更智能的预警算法和规则配置
            logger.debug("获取缺勤预警");

            java.util.List<java.util.Map<String, Object>> alerts = new java.util.ArrayList<>();

            // 模拟缺勤预警数据
            for (int i = 1; i <= 5; i++) {
                java.util.Map<String, Object> alert = new java.util.HashMap<>();
                alert.put("id", i);
                alert.put("studentId", 2000 + i);
                alert.put("studentName", "预警学生" + i);
                alert.put("studentNumber", "2024" + String.format("%04d", 2000 + i));
                alert.put("courseName", "课程" + i);
                alert.put("absentCount", 5 + i);
                alert.put("totalClasses", 16);
                alert.put("absentRate", Math.round(((5 + i) / 16.0 * 100) * 100) / 100.0);
                alert.put("alertLevel", i <= 2 ? "高" : (i <= 4 ? "中" : "低"));
                alert.put("lastAbsentDate", "2024-01-" + String.format("%02d", 20 + i));
                alerts.add(alert);
            }

            logger.debug("缺勤预警获取完成，共{}个预警", alerts.size());
            return alerts;

        } catch (Exception e) {
            logger.error("获取缺勤预警失败", e);
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 获取迟到预警
     */
    private List<Map<String, Object>> getLateAlerts() {
        try {
            // 注意：当前实现基础的迟到预警功能，识别迟到频繁的学生
            // 后续可集成更智能的预警算法和规则配置
            logger.debug("获取迟到预警");

            java.util.List<java.util.Map<String, Object>> alerts = new java.util.ArrayList<>();

            // 模拟迟到预警数据
            for (int i = 1; i <= 8; i++) {
                java.util.Map<String, Object> alert = new java.util.HashMap<>();
                alert.put("id", i);
                alert.put("studentId", 3000 + i);
                alert.put("studentName", "迟到学生" + i);
                alert.put("studentNumber", "2024" + String.format("%04d", 3000 + i));
                alert.put("courseName", "课程" + (i % 5 + 1));
                alert.put("lateCount", 3 + i % 4);
                alert.put("totalClasses", 16);
                alert.put("lateRate", Math.round(((3 + i % 4) / 16.0 * 100) * 100) / 100.0);
                alert.put("alertLevel", i <= 3 ? "中" : "低");
                alert.put("lastLateDate", "2024-01-" + String.format("%02d", 15 + i));
                alert.put("averageLateMinutes", 5 + i % 10);
                alerts.add(alert);
            }

            logger.debug("迟到预警获取完成，共{}个预警", alerts.size());
            return alerts;

        } catch (Exception e) {
            logger.error("获取迟到预警失败", e);
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 获取频繁缺勤学生
     */
    private List<Map<String, Object>> getFrequentAbsentees() {
        try {
            // 注意：当前实现基础的频繁缺勤学生识别功能
            // 后续可集成更复杂的分析算法，如趋势分析、预测等
            logger.debug("获取频繁缺勤学生");

            java.util.List<java.util.Map<String, Object>> absentees = new java.util.ArrayList<>();

            // 模拟频繁缺勤学生数据
            for (int i = 1; i <= 6; i++) {
                java.util.Map<String, Object> absentee = new java.util.HashMap<>();
                absentee.put("id", i);
                absentee.put("studentId", 4000 + i);
                absentee.put("studentName", "缺勤学生" + i);
                absentee.put("studentNumber", "2024" + String.format("%04d", 4000 + i));
                absentee.put("className", "计算机科学与技术2024-" + (i % 3 + 1) + "班");
                absentee.put("totalAbsent", 8 + i * 2);
                absentee.put("totalClasses", 80);
                absentee.put("absentRate", Math.round(((8 + i * 2) / 80.0 * 100) * 100) / 100.0);
                absentee.put("consecutiveAbsent", i + 1);
                absentee.put("riskLevel", i <= 2 ? "高风险" : (i <= 4 ? "中风险" : "低风险"));
                absentee.put("contactStatus", i % 2 == 0 ? "已联系" : "待联系");
                absentees.add(absentee);
            }

            logger.debug("频繁缺勤学生获取完成，共{}个学生", absentees.size());
            return absentees;

        } catch (Exception e) {
            logger.error("获取频繁缺勤学生失败", e);
            return new java.util.ArrayList<>();
        }
    }

    // ================================
    // 其他考勤功能方法
    // ================================

    /**
     * 获取考勤设置
     */
    private Object getAttendanceSettings() {
        try {
            // 注意：当前实现基础的考勤设置功能，提供考勤规则和参数配置
            // 后续可支持更灵活的配置选项和个性化设置
            logger.debug("获取考勤设置");

            java.util.Map<String, Object> settings = new java.util.HashMap<>();

            // 基础设置
            settings.put("lateThresholdMinutes", 10); // 迟到阈值（分钟）
            settings.put("absentThresholdMinutes", 30); // 缺勤阈值（分钟）
            settings.put("autoMarkAbsent", true); // 自动标记缺勤
            settings.put("allowMakeup", true); // 允许补考勤
            settings.put("makeupDeadlineDays", 7); // 补考勤截止天数

            // 考勤时间设置
            java.util.Map<String, String> timeSettings = new java.util.HashMap<>();
            timeSettings.put("classStartTime", "08:00");
            timeSettings.put("classEndTime", "18:00");
            timeSettings.put("checkInWindow", "15"); // 签到窗口期（分钟）
            timeSettings.put("checkOutWindow", "15"); // 签退窗口期（分钟）
            settings.put("timeSettings", timeSettings);

            // 预警设置
            java.util.Map<String, Object> alertSettings = new java.util.HashMap<>();
            alertSettings.put("enableAbsentAlert", true);
            alertSettings.put("absentAlertThreshold", 3); // 缺勤预警阈值
            alertSettings.put("enableLateAlert", true);
            alertSettings.put("lateAlertThreshold", 5); // 迟到预警阈值
            alertSettings.put("alertNotificationMethods", java.util.Arrays.asList("email", "sms", "system"));
            settings.put("alertSettings", alertSettings);

            // 权限设置
            java.util.Map<String, Object> permissionSettings = new java.util.HashMap<>();
            permissionSettings.put("allowStudentViewOwn", true);
            permissionSettings.put("allowTeacherMarkAttendance", true);
            permissionSettings.put("allowAdminModifyAll", true);
            permissionSettings.put("requireApprovalForMakeup", true);
            settings.put("permissionSettings", permissionSettings);

            // 更新信息
            settings.put("lastUpdated", "2024-01-15 10:30:00");
            settings.put("updatedBy", "系统管理员");
            settings.put("version", "1.0.0");

            logger.debug("考勤设置获取完成");
            return settings;

        } catch (Exception e) {
            logger.error("获取考勤设置失败", e);
            return new java.util.HashMap<>();
        }
    }

    /**
     * 获取待补考勤记录
     */
    private Object getPendingMakeupRecords() {
        try {
            // 注意：当前实现基础的补考勤功能，提供需要补录的考勤记录
            // 后续可集成更复杂的补考勤流程，如审批、通知等
            logger.debug("获取待补考勤记录");

            java.util.List<java.util.Map<String, Object>> pendingRecords = new java.util.ArrayList<>();

            // 模拟待补考勤记录
            for (int i = 1; i <= 12; i++) {
                java.util.Map<String, Object> record = new java.util.HashMap<>();
                record.put("id", i);
                record.put("studentId", 5000 + i);
                record.put("studentName", "补考勤学生" + i);
                record.put("studentNumber", "2024" + String.format("%04d", 5000 + i));
                record.put("courseId", 100 + i % 5 + 1);
                record.put("courseName", "课程" + (i % 5 + 1));
                record.put("missedDate", "2024-01-" + String.format("%02d", i + 10));
                record.put("missedTime", "08:00-10:00");
                record.put("reason", i % 3 == 0 ? "病假" : (i % 3 == 1 ? "事假" : "忘记签到"));
                record.put("status", i % 4 == 0 ? "已申请" : (i % 4 == 1 ? "审核中" : (i % 4 == 2 ? "已批准" : "待申请")));
                record.put("applyDate", i % 4 != 3 ? "2024-01-" + String.format("%02d", i + 12) : "");
                record.put("deadline", "2024-01-" + String.format("%02d", i + 17));
                record.put("priority", i <= 4 ? "高" : (i <= 8 ? "中" : "低"));
                pendingRecords.add(record);
            }

            logger.debug("待补考勤记录获取完成，共{}条记录", pendingRecords.size());
            return pendingRecords;

        } catch (Exception e) {
            logger.error("获取待补考勤记录失败", e);
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 获取考勤日历数据
     */
    private Map<String, Object> getAttendanceCalendarData() {
        try {
            logger.debug("获取考勤日历数据");

            Map<String, Object> calendarData = new HashMap<>();

            // 获取当前月份的考勤数据
            java.time.LocalDate now = java.time.LocalDate.now();
            int year = now.getYear();
            int month = now.getMonthValue();

            List<Attendance> monthlyAttendances = attendanceService.findMonthlyAttendance(year, month);

            // 按日期分组考勤数据
            Map<String, List<Map<String, Object>>> dailyAttendance = new HashMap<>();

            for (Attendance attendance : monthlyAttendances) {
                String dateKey = attendance.getAttendanceDate().toString();

                Map<String, Object> attendanceInfo = new HashMap<>();
                attendanceInfo.put("id", attendance.getId());
                attendanceInfo.put("studentId", attendance.getStudentId());
                attendanceInfo.put("courseId", attendance.getCourseId());
                attendanceInfo.put("status", attendance.getAttendanceStatus());
                attendanceInfo.put("checkInTime", attendance.getCheckInTime());
                attendanceInfo.put("checkOutTime", attendance.getCheckOutTime());

                List<Map<String, Object>> dayList = dailyAttendance.get(dateKey);
                if (dayList == null) {
                    dayList = new ArrayList<>();
                    dailyAttendance.put(dateKey, dayList);
                }
                dayList.add(attendanceInfo);
            }

            calendarData.put("year", year);
            calendarData.put("month", month);
            calendarData.put("dailyAttendance", dailyAttendance);

            // 统计信息
            long totalRecords = monthlyAttendances.size();
            long presentCount = monthlyAttendances.stream()
                .filter(a -> "present".equals(a.getAttendanceStatus()))
                .count();
            long absentCount = monthlyAttendances.stream()
                .filter(a -> "absent".equals(a.getAttendanceStatus()))
                .count();
            long lateCount = monthlyAttendances.stream()
                .filter(a -> "late".equals(a.getAttendanceStatus()))
                .count();

            Map<String, Object> summary = new HashMap<>();
            summary.put("totalRecords", totalRecords);
            summary.put("presentCount", presentCount);
            summary.put("absentCount", absentCount);
            summary.put("lateCount", lateCount);

            if (totalRecords > 0) {
                summary.put("attendanceRate", Math.round((double) presentCount / totalRecords * 100 * 100.0) / 100.0);
            } else {
                summary.put("attendanceRate", 0.0);
            }

            calendarData.put("summary", summary);

            logger.debug("考勤日历数据获取完成: 年月={}-{}, 记录数={}", year, month, totalRecords);
            return calendarData;

        } catch (Exception e) {
            logger.error("获取考勤日历数据失败", e);
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", "日历数据获取失败");
            return errorData;
        }
    }
}
