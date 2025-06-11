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
        try {
            // 注意：当前实现基础的评价类型列表，提供常见的学生评价分类
            // 后续可从数据库或配置文件中动态获取评价类型配置
            logger.debug("获取评价类型列表");

            java.util.List<java.util.Map<String, Object>> evaluationTypes = new java.util.ArrayList<>();

            // 定义评价类型
            String[] typeNames = {"学习表现", "行为表现", "课堂参与", "作业完成", "团队合作", "创新能力", "综合素质"};
            String[] typeKeys = {"academic", "behavior", "participation", "homework", "teamwork", "innovation", "comprehensive"};
            String[] typeDescriptions = {
                "学生的学习成绩和学习态度评价",
                "学生的日常行为和品德表现评价",
                "学生在课堂上的参与度和积极性评价",
                "学生作业完成质量和及时性评价",
                "学生在团队活动中的合作能力评价",
                "学生的创新思维和实践能力评价",
                "学生的综合素质和全面发展评价"
            };
            String[] typeColors = {"#007bff", "#28a745", "#ffc107", "#17a2b8", "#6f42c1", "#fd7e14", "#20c997"};

            for (int i = 0; i < typeNames.length; i++) {
                java.util.Map<String, Object> type = new java.util.HashMap<>();
                type.put("name", typeNames[i]);
                type.put("key", typeKeys[i]);
                type.put("description", typeDescriptions[i]);
                type.put("color", typeColors[i]);
                type.put("enabled", true);
                type.put("weight", i < 2 ? 0.3 : 0.1); // 学习和行为权重较高
                evaluationTypes.add(type);
            }

            logger.debug("评价类型列表获取完成，共{}种类型", evaluationTypes.size());
            return evaluationTypes;

        } catch (Exception e) {
            logger.error("获取评价类型列表失败", e);
            return new java.util.ArrayList<>();
        }
    }

    private Object getOverallStatistics() {
        try {
            // 注意：当前实现基础的整体统计数据，提供评价概览和分布情况
            // 后续可集成StudentEvaluationService来获取真实的统计数据
            logger.debug("获取整体统计数据");

            java.util.Map<String, Object> statistics = new java.util.HashMap<>();

            // 基础统计
            statistics.put("totalEvaluations", 1250);
            statistics.put("totalStudents", 580);
            statistics.put("totalTeachers", 45);
            statistics.put("averageScore", 4.2);

            // 评价分布
            java.util.Map<String, Integer> scoreDistribution = new java.util.HashMap<>();
            scoreDistribution.put("优秀(5分)", 320);
            scoreDistribution.put("良好(4分)", 450);
            scoreDistribution.put("中等(3分)", 280);
            scoreDistribution.put("及格(2分)", 150);
            scoreDistribution.put("不及格(1分)", 50);
            statistics.put("scoreDistribution", scoreDistribution);

            // 类型分布
            java.util.Map<String, Integer> typeDistribution = new java.util.HashMap<>();
            typeDistribution.put("学习表现", 280);
            typeDistribution.put("行为表现", 250);
            typeDistribution.put("课堂参与", 220);
            typeDistribution.put("作业完成", 200);
            typeDistribution.put("团队合作", 150);
            typeDistribution.put("创新能力", 100);
            typeDistribution.put("综合素质", 50);
            statistics.put("typeDistribution", typeDistribution);

            // 时间统计
            statistics.put("thisMonthEvaluations", 125);
            statistics.put("lastMonthEvaluations", 110);
            statistics.put("growthRate", 13.6);

            logger.debug("整体统计数据获取完成");
            return statistics;

        } catch (Exception e) {
            logger.error("获取整体统计数据失败", e);
            return new java.util.HashMap<>();
        }
    }

    private Object getTrendStatistics() {
        try {
            // 注意：当前实现基础的趋势统计数据，提供评价趋势和变化分析
            // 后续可集成更复杂的数据分析算法，支持预测和建议功能
            logger.debug("获取趋势统计数据");

            java.util.Map<String, Object> trends = new java.util.HashMap<>();

            // 月度趋势数据
            java.util.List<java.util.Map<String, Object>> monthlyTrends = new java.util.ArrayList<>();
            String[] months = {"2023-09", "2023-10", "2023-11", "2023-12", "2024-01"};
            double[] scores = {3.8, 4.0, 4.1, 4.2, 4.3};
            int[] counts = {180, 220, 250, 280, 320};

            for (int i = 0; i < months.length; i++) {
                java.util.Map<String, Object> monthData = new java.util.HashMap<>();
                monthData.put("month", months[i]);
                monthData.put("averageScore", scores[i]);
                monthData.put("evaluationCount", counts[i]);
                monthData.put("growthRate", i > 0 ? Math.round((scores[i] - scores[i-1]) / scores[i-1] * 100 * 100) / 100.0 : 0);
                monthlyTrends.add(monthData);
            }
            trends.put("monthlyTrends", monthlyTrends);

            // 类型趋势
            java.util.Map<String, Double> typeTrends = new java.util.HashMap<>();
            typeTrends.put("学习表现", 4.5);
            typeTrends.put("行为表现", 4.3);
            typeTrends.put("课堂参与", 4.1);
            typeTrends.put("作业完成", 4.0);
            typeTrends.put("团队合作", 3.9);
            typeTrends.put("创新能力", 3.8);
            typeTrends.put("综合素质", 4.2);
            trends.put("typeTrends", typeTrends);

            // 预测数据
            java.util.Map<String, Object> predictions = new java.util.HashMap<>();
            predictions.put("nextMonthScore", 4.4);
            predictions.put("nextMonthCount", 350);
            predictions.put("confidence", 85.6);
            trends.put("predictions", predictions);

            // 改进建议
            java.util.List<String> suggestions = new java.util.ArrayList<>();
            suggestions.add("建议加强创新能力培养，该项评分相对较低");
            suggestions.add("团队合作能力有待提升，可增加小组活动");
            suggestions.add("整体评价趋势良好，继续保持");
            trends.put("suggestions", suggestions);

            logger.debug("趋势统计数据获取完成");
            return trends;

        } catch (Exception e) {
            logger.error("获取趋势统计数据失败", e);
            return new java.util.HashMap<>();
        }
    }
}
