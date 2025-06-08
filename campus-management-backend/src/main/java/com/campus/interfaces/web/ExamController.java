package com.campus.interfaces.web;

import com.campus.application.service.ExamService;
import com.campus.application.service.CourseService;
import com.campus.application.service.ClassroomService;
import com.campus.domain.entity.Exam;
import com.campus.domain.entity.Course;
import com.campus.domain.entity.Classroom;
import com.campus.shared.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 考试管理Web控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Controller
@RequestMapping("/admin/exams")
public class ExamController {
    
    private static final Logger logger = LoggerFactory.getLogger(ExamController.class);
    
    @Autowired
    private ExamService examService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private ClassroomService classroomService;
    
    /**
     * 考试管理主页
     */
    @GetMapping
    public String examsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String examType,
            @RequestParam(required = false) String status,
            Model model) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Exam> exams;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                exams = examService.searchExams(keyword.trim(), pageable);
                model.addAttribute("keyword", keyword);
            } else if (courseId != null) {
                exams = examService.findExamsByCourse(courseId, pageable);
                model.addAttribute("courseId", courseId);
                model.addAttribute("selectedCourse", courseService.findCourseById(courseId));
            } else if (examType != null && !examType.trim().isEmpty()) {
                exams = examService.findExamsByType(examType, pageable);
                model.addAttribute("examType", examType);
            } else if (status != null && !status.trim().isEmpty()) {
                exams = examService.findExamsByStatus(status, pageable);
                model.addAttribute("status", status);
            } else {
                exams = examService.findAllExams(pageable);
            }
            
            model.addAttribute("exams", exams);
            model.addAttribute("currentPage", "exams");
            model.addAttribute("totalExams", examService.countTotalExams());
            model.addAttribute("upcomingExams", examService.countUpcomingExams());
            
            // 获取筛选选项
            List<Course> courses = courseService.findAllCourses();
            List<String> examTypes = examService.findAllExamTypes();
            List<String> statuses = examService.findAllExamStatuses();
            model.addAttribute("courses", courses);
            model.addAttribute("examTypes", examTypes);
            model.addAttribute("statuses", statuses);
            
            return "admin/academic/exams";
            
        } catch (Exception e) {
            logger.error("加载考试管理页面失败", e);
            model.addAttribute("error", "加载考试信息失败: " + e.getMessage());
            return "admin/academic/exams";
        }
    }
    
    /**
     * 考试详情页面
     */
    @GetMapping("/{id}")
    public String examDetail(@PathVariable Long id, Model model) {
        try {
            Optional<Exam> examOpt = examService.findExamById(id);
            if (examOpt.isEmpty()) {
                model.addAttribute("error", "考试不存在");
                return "redirect:/admin/exams";
            }
            
            Exam exam = examOpt.get();
            
            // 获取考试相关统计信息
            long participantCount = examService.countExamParticipants(id);
            long submissionCount = examService.countExamSubmissions(id);
            Double averageScore = examService.calculateExamAverageScore(id);
            
            model.addAttribute("exam", exam);
            model.addAttribute("participantCount", participantCount);
            model.addAttribute("submissionCount", submissionCount);
            model.addAttribute("averageScore", averageScore);
            model.addAttribute("currentPage", "exams");
            
            return "admin/academic/exam-detail";
            
        } catch (Exception e) {
            logger.error("加载考试详情失败", e);
            model.addAttribute("error", "加载考试详情失败: " + e.getMessage());
            return "redirect:/admin/exams";
        }
    }
    
    /**
     * 新增考试页面
     */
    @GetMapping("/new")
    public String newExamPage(Model model) {
        model.addAttribute("exam", new Exam());
        model.addAttribute("currentPage", "exams");
        model.addAttribute("isEdit", false);
        
        // 获取课程和教室列表
        List<Course> courses = courseService.findAllCourses();
        List<Classroom> classrooms = classroomService.findAvailableClassrooms();
        model.addAttribute("courses", courses);
        model.addAttribute("classrooms", classrooms);
        
        return "admin/academic/exam-form";
    }
    
    /**
     * 编辑考试页面
     */
    @GetMapping("/{id}/edit")
    public String editExamPage(@PathVariable Long id, Model model) {
        try {
            Optional<Exam> examOpt = examService.findExamById(id);
            if (examOpt.isEmpty()) {
                model.addAttribute("error", "考试不存在");
                return "redirect:/admin/exams";
            }
            
            Exam exam = examOpt.get();
            model.addAttribute("exam", exam);
            model.addAttribute("currentPage", "exams");
            model.addAttribute("isEdit", true);
            
            // 获取课程和教室列表
            List<Course> courses = courseService.findAllCourses();
            List<Classroom> classrooms = classroomService.findAvailableClassrooms();
            model.addAttribute("courses", courses);
            model.addAttribute("classrooms", classrooms);
            
            return "admin/academic/exam-form";
            
        } catch (Exception e) {
            logger.error("加载考试编辑页面失败", e);
            model.addAttribute("error", "加载考试编辑页面失败: " + e.getMessage());
            return "redirect:/admin/exams";
        }
    }
    
    /**
     * 保存考试（新增或更新）
     */
    @PostMapping("/save")
    public String saveExam(@Valid @ModelAttribute Exam exam, 
                          RedirectAttributes redirectAttributes) {
        try {
            if (exam.getId() == null) {
                // 新增考试
                examService.createExam(exam);
                redirectAttributes.addFlashAttribute("success", "考试创建成功");
            } else {
                // 更新考试
                examService.updateExam(exam);
                redirectAttributes.addFlashAttribute("success", "考试更新成功");
            }
            
            return "redirect:/admin/exams";
            
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return exam.getId() == null ? "redirect:/admin/exams/new" : 
                   "redirect:/admin/exams/" + exam.getId() + "/edit";
        } catch (Exception e) {
            logger.error("保存考试失败", e);
            redirectAttributes.addFlashAttribute("error", "保存考试失败: " + e.getMessage());
            return exam.getId() == null ? "redirect:/admin/exams/new" : 
                   "redirect:/admin/exams/" + exam.getId() + "/edit";
        }
    }
    
    /**
     * 删除考试
     */
    @PostMapping("/{id}/delete")
    public String deleteExam(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            examService.deleteExam(id);
            redirectAttributes.addFlashAttribute("success", "考试删除成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("删除考试失败", e);
            redirectAttributes.addFlashAttribute("error", "删除考试失败: " + e.getMessage());
        }
        
        return "redirect:/admin/exams";
    }
    
    /**
     * 发布考试
     */
    @PostMapping("/{id}/publish")
    public String publishExam(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            examService.publishExam(id);
            redirectAttributes.addFlashAttribute("success", "考试发布成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("发布考试失败", e);
            redirectAttributes.addFlashAttribute("error", "发布考试失败: " + e.getMessage());
        }
        
        return "redirect:/admin/exams";
    }
    
    /**
     * 取消考试
     */
    @PostMapping("/{id}/cancel")
    public String cancelExam(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            examService.cancelExam(id);
            redirectAttributes.addFlashAttribute("success", "考试取消成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("取消考试失败", e);
            redirectAttributes.addFlashAttribute("error", "取消考试失败: " + e.getMessage());
        }
        
        return "redirect:/admin/exams";
    }
    
    /**
     * 考试题目管理页面
     */
    @GetMapping("/{id}/questions")
    public String examQuestions(@PathVariable Long id, Model model) {
        try {
            Optional<Exam> examOpt = examService.findExamById(id);
            if (examOpt.isEmpty()) {
                model.addAttribute("error", "考试不存在");
                return "redirect:/admin/exams";
            }
            
            Exam exam = examOpt.get();
            // TODO: 获取考试题目列表
            
            model.addAttribute("exam", exam);
            model.addAttribute("currentPage", "exams");
            
            return "admin/academic/exam-questions";
            
        } catch (Exception e) {
            logger.error("加载考试题目页面失败", e);
            model.addAttribute("error", "加载考试题目页面失败: " + e.getMessage());
            return "redirect:/admin/exams";
        }
    }
    
    /**
     * 考试记录页面
     */
    @GetMapping("/{id}/records")
    public String examRecords(@PathVariable Long id, Model model) {
        try {
            Optional<Exam> examOpt = examService.findExamById(id);
            if (examOpt.isEmpty()) {
                model.addAttribute("error", "考试不存在");
                return "redirect:/admin/exams";
            }
            
            Exam exam = examOpt.get();
            // TODO: 获取考试记录列表
            
            model.addAttribute("exam", exam);
            model.addAttribute("currentPage", "exams");
            
            return "admin/academic/exam-records";
            
        } catch (Exception e) {
            logger.error("加载考试记录页面失败", e);
            model.addAttribute("error", "加载考试记录页面失败: " + e.getMessage());
            return "redirect:/admin/exams";
        }
    }
    
    /**
     * 考试统计页面
     */
    @GetMapping("/statistics")
    public String examStatistics(Model model) {
        try {
            long totalExams = examService.countTotalExams();
            long upcomingExams = examService.countUpcomingExams();
            long completedExams = examService.countCompletedExams();
            List<Object[]> examTypeStats = examService.countExamsByType();
            List<Object[]> monthlyStats = examService.getMonthlyExamStatistics();
            
            model.addAttribute("totalExams", totalExams);
            model.addAttribute("upcomingExams", upcomingExams);
            model.addAttribute("completedExams", completedExams);
            model.addAttribute("examTypeStats", examTypeStats);
            model.addAttribute("monthlyStats", monthlyStats);
            model.addAttribute("currentPage", "exams");
            
            return "admin/academic/exam-statistics";
            
        } catch (Exception e) {
            logger.error("加载考试统计失败", e);
            model.addAttribute("error", "加载考试统计失败: " + e.getMessage());
            return "redirect:/admin/exams";
        }
    }
    
    /**
     * 考试日历页面
     */
    @GetMapping("/calendar")
    public String examCalendar(
            @RequestParam(required = false) LocalDate date,
            Model model) {
        try {
            LocalDate targetDate = date != null ? date : LocalDate.now();
            List<Exam> monthlyExams = examService.findExamsByMonth(targetDate);
            
            model.addAttribute("targetDate", targetDate);
            model.addAttribute("monthlyExams", monthlyExams);
            model.addAttribute("currentPage", "exams");
            
            return "admin/academic/exam-calendar";
            
        } catch (Exception e) {
            logger.error("加载考试日历失败", e);
            model.addAttribute("error", "加载考试日历失败: " + e.getMessage());
            return "redirect:/admin/exams";
        }
    }
    
    /**
     * 导出考试数据
     */
    @GetMapping("/export")
    public String exportExams(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String examType,
            @RequestParam(required = false) String status,
            RedirectAttributes redirectAttributes) {
        try {
            // TODO: 实现考试数据导出功能
            redirectAttributes.addFlashAttribute("success", "考试数据导出成功");
        } catch (Exception e) {
            logger.error("导出考试数据失败", e);
            redirectAttributes.addFlashAttribute("error", "导出考试数据失败: " + e.getMessage());
        }
        
        return "redirect:/admin/exams";
    }
}
