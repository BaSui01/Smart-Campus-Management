package com.campus.interfaces.web;

import com.campus.application.service.StudentEvaluationService;
import com.campus.application.service.UserService;
import com.campus.application.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 学生评价管理页面控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Controller
@RequestMapping("/admin/student-evaluations")
public class StudentEvaluationController {
    
    private static final Logger logger = LoggerFactory.getLogger(StudentEvaluationController.class);
    
    @Autowired
    private StudentEvaluationService studentEvaluationService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CourseService courseService;
    
    // ================================
    // 页面路由
    // ================================
    
    @GetMapping
    public String evaluationList(Model model) {
        try {
            logger.info("访问学生评价管理页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "学生评价管理");
            model.addAttribute("students", userService.findStudents());
            model.addAttribute("evaluators", userService.findTeachers());
            model.addAttribute("evaluationTypes", getEvaluationTypes());
            
            return "admin/student-evaluations/list";
            
        } catch (Exception e) {
            return handlePageError(e, "访问学生评价管理页面", model);
        }
    }
    
    @GetMapping("/create")
    public String createEvaluation(Model model) {
        try {
            logger.info("访问创建评价页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "创建学生评价");
            model.addAttribute("action", "create");
            model.addAttribute("students", userService.findStudents());
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("evaluationTypes", getEvaluationTypes());
            
            return "admin/student-evaluations/form";
            
        } catch (Exception e) {
            return handlePageError(e, "访问创建评价页面", model);
        }
    }
    
    @GetMapping("/{evaluationId}/edit")
    public String editEvaluation(@PathVariable Long evaluationId, Model model) {
        try {
            logger.info("访问编辑评价页面: evaluationId={}", evaluationId);
            
            Object evaluation = studentEvaluationService.findEvaluationById(evaluationId).orElse(null);
            if (evaluation == null) {
                model.addAttribute("error", "评价不存在");
                return "error/404";
            }
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "编辑学生评价");
            model.addAttribute("action", "edit");
            model.addAttribute("evaluation", evaluation);
            model.addAttribute("students", userService.findStudents());
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("evaluationTypes", getEvaluationTypes());
            
            return "admin/student-evaluations/form";
            
        } catch (Exception e) {
            return handlePageError(e, "访问编辑评价页面", model);
        }
    }
    
    @GetMapping("/student/{studentId}")
    public String studentEvaluations(@PathVariable Long studentId, Model model) {
        try {
            logger.info("访问学生评价记录页面: studentId={}", studentId);
            
            Object student = userService.getUserById(studentId);
            if (student == null) {
                model.addAttribute("error", "学生不存在");
                return "error/404";
            }
            
            Object evaluations = studentEvaluationService.findEvaluationsByStudent(studentId);
            Object statistics = studentEvaluationService.getEvaluationStatistics(studentId);
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "学生评价记录");
            model.addAttribute("student", student);
            model.addAttribute("evaluations", evaluations);
            model.addAttribute("statistics", statistics);
            
            return "admin/student-evaluations/student-evaluations";
            
        } catch (Exception e) {
            return handlePageError(e, "访问学生评价记录页面", model);
        }
    }
    
    @GetMapping("/statistics")
    public String evaluationStatistics(Model model) {
        try {
            logger.info("访问评价统计页面");
            
            Object overallStats = getOverallStatistics();
            Object typeStats = studentEvaluationService.countEvaluationsByType();
            Object trendStats = getTrendStatistics();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "评价统计");
            model.addAttribute("overallStats", overallStats);
            model.addAttribute("typeStats", typeStats);
            model.addAttribute("trendStats", trendStats);
            
            return "admin/student-evaluations/statistics";
            
        } catch (Exception e) {
            return handlePageError(e, "访问评价统计页面", model);
        }
    }
    
    @GetMapping("/reports")
    public String evaluationReports(Model model) {
        try {
            logger.info("访问评价报表页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "评价报表");
            model.addAttribute("students", userService.findStudents());
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("evaluationTypes", getEvaluationTypes());
            
            return "admin/student-evaluations/reports";
            
        } catch (Exception e) {
            return handlePageError(e, "访问评价报表页面", model);
        }
    }
    
    // ================================
    // 辅助方法
    // ================================
    
    private void addCommonAttributes(Model model) {
        model.addAttribute("currentModule", "student-evaluations");
        model.addAttribute("breadcrumb", "学生评价管理");
    }
    
    private String handlePageError(Exception e, String operation, Model model) {
        logger.error("{}失败", operation, e);
        model.addAttribute("error", operation + "失败: " + e.getMessage());
        return "error/500";
    }
    
    private Object getEvaluationTypes() {
        // TODO: 实现获取评价类型逻辑
        return new Object();
    }
    
    private Object getOverallStatistics() {
        // TODO: 实现获取整体统计逻辑
        return new Object();
    }
    
    private Object getTrendStatistics() {
        // TODO: 实现获取趋势统计逻辑
        return new Object();
    }
}
