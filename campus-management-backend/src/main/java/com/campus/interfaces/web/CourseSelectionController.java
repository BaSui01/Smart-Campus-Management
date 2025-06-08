package com.campus.interfaces.web;

import com.campus.application.service.CourseSelectionService;
import com.campus.application.service.CourseService;
import com.campus.application.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 选课管理页面控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Controller
@RequestMapping("/admin/course-selection")
public class CourseSelectionController {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseSelectionController.class);
    
    @Autowired
    private CourseSelectionService courseSelectionService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private UserService userService;
    
    // ================================
    // 页面路由
    // ================================
    
    @GetMapping
    public String selectionList(Model model) {
        try {
            logger.info("访问选课管理页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "选课管理");
            model.addAttribute("courses", courseService.findSelectableCourses());
            model.addAttribute("students", userService.findStudents());
            model.addAttribute("selectionPeriods", courseSelectionService.getSelectionPeriods());
            
            return "admin/course-selection/list";
            
        } catch (Exception e) {
            return handlePageError(e, "访问选课管理页面", model);
        }
    }
    
    @GetMapping("/periods")
    public String selectionPeriods(Model model) {
        try {
            logger.info("访问选课时间管理页面");
            
            Object periods = courseSelectionService.getSelectionPeriods();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "选课时间管理");
            model.addAttribute("periods", periods);
            
            return "admin/course-selection/periods";
            
        } catch (Exception e) {
            return handlePageError(e, "访问选课时间管理页面", model);
        }
    }
    
    @GetMapping("/periods/create")
    public String createPeriodPage(Model model) {
        try {
            logger.info("访问创建选课时间页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "创建选课时间");
            model.addAttribute("action", "create");
            
            return "admin/course-selection/period-form";
            
        } catch (Exception e) {
            return handlePageError(e, "访问创建选课时间页面", model);
        }
    }
    
    @GetMapping("/periods/{periodId}/edit")
    public String editPeriodPage(@PathVariable Long periodId, Model model) {
        try {
            logger.info("访问编辑选课时间页面: periodId={}", periodId);
            
            Object period = courseSelectionService.getSelectionPeriodById(periodId);
            if (period == null) {
                model.addAttribute("error", "选课时间不存在");
                return "error/404";
            }
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "编辑选课时间");
            model.addAttribute("action", "edit");
            model.addAttribute("period", period);
            
            return "admin/course-selection/period-form";
            
        } catch (Exception e) {
            return handlePageError(e, "访问编辑选课时间页面", model);
        }
    }
    
    @GetMapping("/courses")
    public String selectableCourses(Model model) {
        try {
            logger.info("访问可选课程管理页面");
            
            Object courses = courseService.findSelectableCourses();
            Object categories = courseService.getCourseCategories();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "可选课程管理");
            model.addAttribute("courses", courses);
            model.addAttribute("categories", categories);
            
            return "admin/course-selection/courses";
            
        } catch (Exception e) {
            return handlePageError(e, "访问可选课程管理页面", model);
        }
    }
    
    @GetMapping("/student/{studentId}")
    public String studentSelections(@PathVariable Long studentId, Model model) {
        try {
            logger.info("访问学生选课记录页面: studentId={}", studentId);
            
            Object student = userService.getUserById(studentId);
            if (student == null) {
                model.addAttribute("error", "学生不存在");
                return "error/404";
            }
            
            Object selections = courseSelectionService.getStudentSelections(studentId);
            Object availableCourses = courseSelectionService.getAvailableCoursesForStudent(studentId);
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "学生选课记录");
            model.addAttribute("student", student);
            model.addAttribute("selections", selections);
            model.addAttribute("availableCourses", availableCourses);
            
            return "admin/course-selection/student-selections";
            
        } catch (Exception e) {
            return handlePageError(e, "访问学生选课记录页面", model);
        }
    }
    
    @GetMapping("/course/{courseId}")
    public String courseSelections(@PathVariable Long courseId, Model model) {
        try {
            logger.info("访问课程选课情况页面: courseId={}", courseId);
            
            Object course = courseService.getCourseById(courseId);
            if (course == null) {
                model.addAttribute("error", "课程不存在");
                return "error/404";
            }
            
            Object selections = courseSelectionService.getCourseSelections(courseId);
            Object statistics = courseSelectionService.getCourseSelectionStatistics(courseId);
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "课程选课情况");
            model.addAttribute("course", course);
            model.addAttribute("selections", selections);
            model.addAttribute("statistics", statistics);
            
            return "admin/course-selection/course-selections";
            
        } catch (Exception e) {
            return handlePageError(e, "访问课程选课情况页面", model);
        }
    }
    
    @GetMapping("/conflicts")
    public String selectionConflicts(Model model) {
        try {
            logger.info("访问选课冲突检查页面");
            
            Object conflicts = courseSelectionService.getSelectionConflicts();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "选课冲突检查");
            model.addAttribute("conflicts", conflicts);
            
            return "admin/course-selection/conflicts";
            
        } catch (Exception e) {
            return handlePageError(e, "访问选课冲突检查页面", model);
        }
    }
    
    @GetMapping("/lottery")
    public String selectionLottery(Model model) {
        try {
            logger.info("访问选课抽签页面");
            
            Object lotteryData = courseSelectionService.getLotteryData();
            Object oversubscribedCourses = courseSelectionService.getOversubscribedCourses();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "选课抽签");
            model.addAttribute("lotteryData", lotteryData);
            model.addAttribute("oversubscribedCourses", oversubscribedCourses);
            
            return "admin/course-selection/lottery";
            
        } catch (Exception e) {
            return handlePageError(e, "访问选课抽签页面", model);
        }
    }
    
    @GetMapping("/waitlist")
    public String selectionWaitlist(Model model) {
        try {
            logger.info("访问选课候补名单页面");
            
            Object waitlistData = courseSelectionService.getWaitlistData();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "选课候补名单");
            model.addAttribute("waitlistData", waitlistData);
            
            return "admin/course-selection/waitlist";
            
        } catch (Exception e) {
            return handlePageError(e, "访问选课候补名单页面", model);
        }
    }
    
    @GetMapping("/statistics")
    public String selectionStatistics(Model model) {
        try {
            logger.info("访问选课统计页面");
            
            Object overallStats = courseSelectionService.getOverallStatistics();
            Object courseStats = courseSelectionService.getCourseSelectionStats();
            Object studentStats = courseSelectionService.getStudentSelectionStats();
            Object trendStats = courseSelectionService.getSelectionTrendStats();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "选课统计");
            model.addAttribute("overallStats", overallStats);
            model.addAttribute("courseStats", courseStats);
            model.addAttribute("studentStats", studentStats);
            model.addAttribute("trendStats", trendStats);
            
            return "admin/course-selection/statistics";
            
        } catch (Exception e) {
            return handlePageError(e, "访问选课统计页面", model);
        }
    }
    
    @GetMapping("/rules")
    public String selectionRules(Model model) {
        try {
            logger.info("访问选课规则管理页面");
            
            Object rules = courseSelectionService.getSelectionRules();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "选课规则管理");
            model.addAttribute("rules", rules);
            
            return "admin/course-selection/rules";
            
        } catch (Exception e) {
            return handlePageError(e, "访问选课规则管理页面", model);
        }
    }
    
    @GetMapping("/batch-operations")
    public String batchOperations(Model model) {
        try {
            logger.info("访问批量操作页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "批量操作");
            model.addAttribute("courses", courseService.findSelectableCourses());
            model.addAttribute("students", userService.findStudents());
            
            return "admin/course-selection/batch-operations";
            
        } catch (Exception e) {
            return handlePageError(e, "访问批量操作页面", model);
        }
    }
    
    @GetMapping("/reports")
    public String selectionReports(Model model) {
        try {
            logger.info("访问选课报表页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "选课报表");
            model.addAttribute("courses", courseService.findSelectableCourses());
            
            return "admin/course-selection/reports";
            
        } catch (Exception e) {
            return handlePageError(e, "访问选课报表页面", model);
        }
    }
    
    // ================================
    // 辅助方法
    // ================================
    
    /**
     * 添加通用页面属性
     */
    private void addCommonAttributes(Model model) {
        model.addAttribute("currentModule", "course-selection");
        model.addAttribute("breadcrumb", "选课管理");
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
