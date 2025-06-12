package com.campus.interfaces.rest.academic;

import com.campus.application.service.academic.AssignmentService;
import com.campus.application.service.academic.CourseService;
import com.campus.application.service.auth.UserService;
import com.campus.domain.entity.academic.Assignment;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 作业管理页面控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Controller
@RequestMapping("/admin/assignments")
public class AssignmentController {
    
    private static final Logger logger = LoggerFactory.getLogger(AssignmentController.class);
    
    @Autowired
    private AssignmentService assignmentService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private UserService userService;
    
    // ================================
    // 页面路由
    // ================================
    
    @GetMapping
    public String assignmentList(Model model) {
        try {
            logger.info("访问作业管理页面");
            
            // 添加页面所需数据
            model.addAttribute("pageTitle", "作业管理");
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("teachers", userService.findTeachers());
            
            return "admin/assignments/list";
            
        } catch (Exception e) {
            logger.error("访问作业管理页面失败", e);
            model.addAttribute("error", "页面加载失败: " + e.getMessage());
            return "error/500";
        }
    }
    
    @GetMapping("/create")
    public String createAssignmentPage(Model model) {
        try {
            logger.info("访问创建作业页面");
            
            model.addAttribute("pageTitle", "创建作业");
            model.addAttribute("action", "create");
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("teachers", userService.findTeachers());
            
            return "admin/assignments/form";
            
        } catch (Exception e) {
            logger.error("访问创建作业页面失败", e);
            model.addAttribute("error", "页面加载失败: " + e.getMessage());
            return "error/500";
        }
    }
    
    @GetMapping("/{assignmentId}/edit")
    public String editAssignmentPage(@PathVariable Long assignmentId, Model model) {
        try {
            logger.info("访问编辑作业页面: assignmentId={}", assignmentId);
            
            // 获取作业详情
            Optional<Assignment> assignmentOpt = assignmentService.findById(assignmentId);
            if (!assignmentOpt.isPresent()) {
                model.addAttribute("error", "作业不存在");
                return "error/404";
            }
            Assignment assignment = assignmentOpt.get();
            if (assignment == null) {
                model.addAttribute("error", "作业不存在");
                return "error/404";
            }
            
            model.addAttribute("pageTitle", "编辑作业");
            model.addAttribute("action", "edit");
            model.addAttribute("assignment", assignment);
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("teachers", userService.findTeachers());
            
            return "admin/assignments/form";
            
        } catch (Exception e) {
            logger.error("访问编辑作业页面失败", e);
            model.addAttribute("error", "页面加载失败: " + e.getMessage());
            return "error/500";
        }
    }
    
    @GetMapping("/{assignmentId}/detail")
    public String assignmentDetail(@PathVariable Long assignmentId, Model model) {
        try {
            logger.info("访问作业详情页面: assignmentId={}", assignmentId);
            
            // 获取作业详情和提交情况
            Optional<Assignment> assignmentOpt = assignmentService.findById(assignmentId);
            if (!assignmentOpt.isPresent()) {
                model.addAttribute("error", "作业不存在");
                return "error/404";
            }
            Assignment assignment = assignmentOpt.get();
            
            Object submissions = assignmentService.getSubmissionsByAssignment(assignmentId);
            Object statistics = assignmentService.getSubmissionStatistics(assignmentId);
            
            model.addAttribute("pageTitle", "作业详情");
            model.addAttribute("assignment", assignment);
            model.addAttribute("submissions", submissions);
            model.addAttribute("statistics", statistics);
            
            return "admin/assignments/detail";
            
        } catch (Exception e) {
            logger.error("访问作业详情页面失败", e);
            model.addAttribute("error", "页面加载失败: " + e.getMessage());
            return "error/500";
        }
    }
    
    @GetMapping("/{assignmentId}/submissions")
    public String assignmentSubmissions(@PathVariable Long assignmentId, Model model) {
        try {
            logger.info("访问作业提交管理页面: assignmentId={}", assignmentId);
            
            // 获取作业和提交列表
            Optional<Assignment> assignmentOpt = assignmentService.findById(assignmentId);
            if (!assignmentOpt.isPresent()) {
                model.addAttribute("error", "作业不存在");
                return "error/404";
            }
            Assignment assignment = assignmentOpt.get();
            
            Object submissions = assignmentService.getSubmissionsByAssignment(assignmentId);
            
            model.addAttribute("pageTitle", "作业提交管理");
            model.addAttribute("assignment", assignment);
            model.addAttribute("submissions", submissions);
            
            return "admin/assignments/submissions";
            
        } catch (Exception e) {
            logger.error("访问作业提交管理页面失败", e);
            model.addAttribute("error", "页面加载失败: " + e.getMessage());
            return "error/500";
        }
    }
    
    @GetMapping("/{assignmentId}/grading")
    public String assignmentGrading(@PathVariable Long assignmentId, Model model) {
        try {
            logger.info("访问作业评分页面: assignmentId={}", assignmentId);
            
            // 获取作业和待评分提交
            Optional<Assignment> assignmentOpt = assignmentService.findById(assignmentId);
            if (!assignmentOpt.isPresent()) {
                model.addAttribute("error", "作业不存在");
                return "error/404";
            }
            Assignment assignment = assignmentOpt.get();

            // 获取待评分提交（暂时返回空数据）
            Object pendingSubmissions = new java.util.ArrayList<>();
            
            model.addAttribute("pageTitle", "作业评分");
            model.addAttribute("assignment", assignment);
            model.addAttribute("pendingSubmissions", pendingSubmissions);
            
            return "admin/assignments/grading";
            
        } catch (Exception e) {
            logger.error("访问作业评分页面失败", e);
            model.addAttribute("error", "页面加载失败: " + e.getMessage());
            return "error/500";
        }
    }
    
    @GetMapping("/statistics")
    public String assignmentStatistics(Model model) {
        try {
            logger.info("访问作业统计页面");
            
            // 获取作业统计数据（暂时返回空数据）
            Object overallStats = new java.util.HashMap<>();
            Object courseStats = new java.util.ArrayList<>();
            Object teacherStats = new java.util.ArrayList<>();
            
            model.addAttribute("pageTitle", "作业统计");
            model.addAttribute("overallStats", overallStats);
            model.addAttribute("courseStats", courseStats);
            model.addAttribute("teacherStats", teacherStats);
            
            return "admin/assignments/statistics";
            
        } catch (Exception e) {
            logger.error("访问作业统计页面失败", e);
            model.addAttribute("error", "页面加载失败: " + e.getMessage());
            return "error/500";
        }
    }
    
    @GetMapping("/templates")
    public String assignmentTemplates(Model model) {
        try {
            logger.info("访问作业模板页面");
            
            // 获取作业模板列表（暂时返回空数据）
            Object templates = new java.util.ArrayList<>();
            
            model.addAttribute("pageTitle", "作业模板");
            model.addAttribute("templates", templates);
            model.addAttribute("courses", courseService.findActiveCourses());
            
            return "admin/assignments/templates";
            
        } catch (Exception e) {
            logger.error("访问作业模板页面失败", e);
            model.addAttribute("error", "页面加载失败: " + e.getMessage());
            return "error/500";
        }
    }
    
    @GetMapping("/calendar")
    public String assignmentCalendar(Model model) {
        try {
            logger.info("访问作业日历页面");
            
            // 获取作业日历数据（暂时返回空数据）
            Object calendarData = new java.util.HashMap<>();
            
            model.addAttribute("pageTitle", "作业日历");
            model.addAttribute("calendarData", calendarData);
            model.addAttribute("courses", courseService.findActiveCourses());
            
            return "admin/assignments/calendar";
            
        } catch (Exception e) {
            logger.error("访问作业日历页面失败", e);
            model.addAttribute("error", "页面加载失败: " + e.getMessage());
            return "error/500";
        }
    }
    
    @GetMapping("/batch-operations")
    public String batchOperations(Model model) {
        try {
            logger.info("访问批量操作页面");
            
            model.addAttribute("pageTitle", "批量操作");
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("teachers", userService.findTeachers());
            
            return "admin/assignments/batch-operations";
            
        } catch (Exception e) {
            logger.error("访问批量操作页面失败", e);
            model.addAttribute("error", "页面加载失败: " + e.getMessage());
            return "error/500";
        }
    }
    

}
