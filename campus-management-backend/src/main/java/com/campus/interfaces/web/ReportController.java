package com.campus.interfaces.web;

import com.campus.application.service.ReportService;
import com.campus.application.service.CourseService;
import com.campus.application.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 报表管理页面控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Controller
@RequestMapping("/admin/reports")
public class ReportController {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    
    @Autowired
    private ReportService reportService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private UserService userService;
    
    // ================================
    // 页面路由
    // ================================
    
    @GetMapping
    public String reportDashboard(Model model) {
        try {
            logger.info("访问报表管理主页");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "报表管理");
            model.addAttribute("reportCategories", reportService.getReportCategories());
            model.addAttribute("recentReports", reportService.getRecentReports());
            model.addAttribute("favoriteReports", reportService.getFavoriteReports());
            
            return "admin/reports/dashboard";
            
        } catch (Exception e) {
            return handlePageError(e, "访问报表管理主页", model);
        }
    }
    
    @GetMapping("/student")
    public String studentReports(Model model) {
        try {
            logger.info("访问学生报表页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "学生报表");
            model.addAttribute("students", userService.findStudents());
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("departments", userService.getDepartments());
            
            return "admin/reports/student";
            
        } catch (Exception e) {
            return handlePageError(e, "访问学生报表页面", model);
        }
    }
    
    @GetMapping("/teacher")
    public String teacherReports(Model model) {
        try {
            logger.info("访问教师报表页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "教师报表");
            model.addAttribute("teachers", userService.findTeachers());
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("departments", userService.getDepartments());
            
            return "admin/reports/teacher";
            
        } catch (Exception e) {
            return handlePageError(e, "访问教师报表页面", model);
        }
    }
    
    @GetMapping("/course")
    public String courseReports(Model model) {
        try {
            logger.info("访问课程报表页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "课程报表");
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("teachers", userService.findTeachers());
            model.addAttribute("courseCategories", courseService.getCourseCategories());
            
            return "admin/reports/course";
            
        } catch (Exception e) {
            return handlePageError(e, "访问课程报表页面", model);
        }
    }
    
    @GetMapping("/attendance")
    public String attendanceReports(Model model) {
        try {
            logger.info("访问考勤报表页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "考勤报表");
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("students", userService.findStudents());
            model.addAttribute("teachers", userService.findTeachers());
            
            return "admin/reports/attendance";
            
        } catch (Exception e) {
            return handlePageError(e, "访问考勤报表页面", model);
        }
    }
    
    @GetMapping("/grade")
    public String gradeReports(Model model) {
        try {
            logger.info("访问成绩报表页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "成绩报表");
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("students", userService.findStudents());
            model.addAttribute("semesters", reportService.getAvailableSemesters());
            
            return "admin/reports/grade";
            
        } catch (Exception e) {
            return handlePageError(e, "访问成绩报表页面", model);
        }
    }
    
    @GetMapping("/financial")
    public String financialReports(Model model) {
        try {
            logger.info("访问财务报表页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "财务报表");
            model.addAttribute("paymentTypes", reportService.getPaymentTypes());
            model.addAttribute("students", userService.findStudents());
            
            return "admin/reports/financial";
            
        } catch (Exception e) {
            return handlePageError(e, "访问财务报表页面", model);
        }
    }
    
    @GetMapping("/enrollment")
    public String enrollmentReports(Model model) {
        try {
            logger.info("访问招生报表页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "招生报表");
            model.addAttribute("departments", userService.getDepartments());
            model.addAttribute("enrollmentYears", reportService.getEnrollmentYears());
            
            return "admin/reports/enrollment";
            
        } catch (Exception e) {
            return handlePageError(e, "访问招生报表页面", model);
        }
    }
    
    @GetMapping("/custom")
    public String customReports(Model model) {
        try {
            logger.info("访问自定义报表页面");
            
            Object customReports = reportService.getCustomReports();
            Object reportTemplates = reportService.getReportTemplates();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "自定义报表");
            model.addAttribute("customReports", customReports);
            model.addAttribute("templates", reportTemplates);
            
            return "admin/reports/custom";
            
        } catch (Exception e) {
            return handlePageError(e, "访问自定义报表页面", model);
        }
    }
    
    @GetMapping("/custom/create")
    public String createCustomReport(Model model) {
        try {
            logger.info("访问创建自定义报表页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "创建自定义报表");
            model.addAttribute("dataSources", reportService.getAvailableDataSources());
            model.addAttribute("chartTypes", reportService.getChartTypes());
            
            return "admin/reports/custom-form";
            
        } catch (Exception e) {
            return handlePageError(e, "访问创建自定义报表页面", model);
        }
    }
    
    @GetMapping("/custom/{reportId}/edit")
    public String editCustomReport(@PathVariable Long reportId, Model model) {
        try {
            logger.info("访问编辑自定义报表页面: reportId={}", reportId);
            
            Object report = reportService.getCustomReportById(reportId);
            if (report == null) {
                model.addAttribute("error", "报表不存在");
                return "error/404";
            }
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "编辑自定义报表");
            model.addAttribute("report", report);
            model.addAttribute("dataSources", reportService.getAvailableDataSources());
            model.addAttribute("chartTypes", reportService.getChartTypes());
            
            return "admin/reports/custom-form";
            
        } catch (Exception e) {
            return handlePageError(e, "访问编辑自定义报表页面", model);
        }
    }
    
    @GetMapping("/scheduled")
    public String scheduledReports(Model model) {
        try {
            logger.info("访问定时报表页面");
            
            Object scheduledReports = reportService.getScheduledReports();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "定时报表");
            model.addAttribute("scheduledReports", scheduledReports);
            
            return "admin/reports/scheduled";
            
        } catch (Exception e) {
            return handlePageError(e, "访问定时报表页面", model);
        }
    }
    
    @GetMapping("/export-history")
    public String exportHistory(Model model) {
        try {
            logger.info("访问导出历史页面");
            
            Object exportHistory = reportService.getExportHistory();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "导出历史");
            model.addAttribute("exportHistory", exportHistory);
            
            return "admin/reports/export-history";
            
        } catch (Exception e) {
            return handlePageError(e, "访问导出历史页面", model);
        }
    }
    
    @GetMapping("/analytics")
    public String analyticsReports(Model model) {
        try {
            logger.info("访问数据分析报表页面");
            
            Object analyticsData = reportService.getAnalyticsData();
            Object kpiMetrics = reportService.getKPIMetrics();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "数据分析");
            model.addAttribute("analyticsData", analyticsData);
            model.addAttribute("kpiMetrics", kpiMetrics);
            
            return "admin/reports/analytics";
            
        } catch (Exception e) {
            return handlePageError(e, "访问数据分析报表页面", model);
        }
    }
    
    @GetMapping("/dashboard-config")
    public String dashboardConfig(Model model) {
        try {
            logger.info("访问仪表板配置页面");
            
            Object dashboardWidgets = reportService.getDashboardWidgets();
            Object availableWidgets = reportService.getAvailableWidgets();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "仪表板配置");
            model.addAttribute("dashboardWidgets", dashboardWidgets);
            model.addAttribute("availableWidgets", availableWidgets);
            
            return "admin/reports/dashboard-config";
            
        } catch (Exception e) {
            return handlePageError(e, "访问仪表板配置页面", model);
        }
    }
    
    @GetMapping("/templates")
    public String reportTemplates(Model model) {
        try {
            logger.info("访问报表模板页面");
            
            Object templates = reportService.getReportTemplates();
            Object templateCategories = reportService.getTemplateCategories();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "报表模板");
            model.addAttribute("templates", templates);
            model.addAttribute("categories", templateCategories);
            
            return "admin/reports/templates";
            
        } catch (Exception e) {
            return handlePageError(e, "访问报表模板页面", model);
        }
    }
    
    // ================================
    // 辅助方法
    // ================================
    
    /**
     * 添加通用页面属性
     */
    private void addCommonAttributes(Model model) {
        model.addAttribute("currentModule", "reports");
        model.addAttribute("breadcrumb", "报表管理");
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
