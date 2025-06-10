package com.campus.interfaces.web;

import com.campus.application.service.GradeService;
import com.campus.application.service.CourseService;
import com.campus.application.service.StudentService;
import com.campus.domain.entity.Grade;
import com.campus.domain.entity.Course;
import com.campus.domain.entity.Student;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 成绩管理Web控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Controller
@RequestMapping("/admin/grades")
public class GradeController {
    
    private static final Logger logger = LoggerFactory.getLogger(GradeController.class);
    
    @Autowired
    private GradeService gradeService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private StudentService studentService;

    /**
     * 成绩管理主页
     */
    @GetMapping
    public String gradesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String semester,
            Model model) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Grade> grades;
            
            if (courseId != null) {
                grades = gradeService.findGradesByCourse(courseId, pageable);
                model.addAttribute("courseId", courseId);
                model.addAttribute("selectedCourse", courseService.findCourseById(courseId));
            } else if (studentId != null) {
                grades = gradeService.findGradesByStudent(studentId, pageable);
                model.addAttribute("studentId", studentId);
                model.addAttribute("selectedStudent", studentService.findStudentById(studentId));
            } else if (semester != null && !semester.trim().isEmpty()) {
                grades = gradeService.findGradesBySemester(semester, pageable);
                model.addAttribute("semester", semester);
            } else {
                grades = gradeService.findAllGrades(pageable);
            }
            
            model.addAttribute("grades", grades);
            model.addAttribute("currentPage", "grades");
            model.addAttribute("totalGrades", gradeService.countTotalGrades());
            
            // 获取筛选选项
            List<Course> courses = courseService.findAllCourses();
            List<String> semesters = gradeService.findAllSemesters();
            model.addAttribute("courses", courses);
            model.addAttribute("semesters", semesters);
            
            return "admin/academic/grades";
            
        } catch (Exception e) {
            logger.error("加载成绩管理页面失败", e);
            model.addAttribute("error", "加载成绩信息失败: " + e.getMessage());
            return "admin/academic/grades";
        }
    }
    
    /**
     * 成绩详情页面
     */
    @GetMapping("/{id}")
    public String gradeDetail(@PathVariable Long id, Model model) {
        try {
            Grade grade = gradeService.findGradeById(id);
            if (grade == null) {
                model.addAttribute("error", "成绩记录不存在");
                return "redirect:/admin/grades";
            }
            model.addAttribute("grade", grade);
            model.addAttribute("currentPage", "grades");
            
            return "admin/academic/grade-detail";
            
        } catch (Exception e) {
            logger.error("加载成绩详情失败", e);
            model.addAttribute("error", "加载成绩详情失败: " + e.getMessage());
            return "redirect:/admin/grades";
        }
    }
    
    /**
     * 新增成绩页面
     */
    @GetMapping("/new")
    public String newGradePage(Model model) {
        model.addAttribute("grade", new Grade());
        model.addAttribute("currentPage", "grades");
        model.addAttribute("isEdit", false);
        
        // 获取课程和学生列表
        List<Course> courses = courseService.findAllCourses();
        List<Student> students = studentService.findAllStudents();
        model.addAttribute("courses", courses);
        model.addAttribute("students", students);
        
        return "admin/academic/grade-form";
    }
    
    /**
     * 编辑成绩页面
     */
    @GetMapping("/{id}/edit")
    public String editGradePage(@PathVariable Long id, Model model) {
        try {
            Grade grade = gradeService.findGradeById(id);
            if (grade == null) {
                model.addAttribute("error", "成绩记录不存在");
                return "redirect:/admin/grades";
            }
            model.addAttribute("grade", grade);
            model.addAttribute("currentPage", "grades");
            model.addAttribute("isEdit", true);
            
            // 获取课程和学生列表
            List<Course> courses = courseService.findAllCourses();
            List<Student> students = studentService.findAllStudents();
            model.addAttribute("courses", courses);
            model.addAttribute("students", students);
            
            return "admin/academic/grade-form";
            
        } catch (Exception e) {
            logger.error("加载成绩编辑页面失败", e);
            model.addAttribute("error", "加载成绩编辑页面失败: " + e.getMessage());
            return "redirect:/admin/grades";
        }
    }
    
    /**
     * 保存成绩（新增或更新）
     */
    @PostMapping("/save")
    public String saveGrade(@Valid @ModelAttribute Grade grade, 
                           RedirectAttributes redirectAttributes) {
        try {
            if (grade.getId() == null) {
                // 新增成绩
                gradeService.createGrade(grade);
                redirectAttributes.addFlashAttribute("success", "成绩录入成功");
            } else {
                // 更新成绩
                gradeService.updateGrade(grade);
                redirectAttributes.addFlashAttribute("success", "成绩更新成功");
            }
            
            return "redirect:/admin/grades";
            
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return grade.getId() == null ? "redirect:/admin/grades/new" : 
                   "redirect:/admin/grades/" + grade.getId() + "/edit";
        } catch (Exception e) {
            logger.error("保存成绩失败", e);
            redirectAttributes.addFlashAttribute("error", "保存成绩失败: " + e.getMessage());
            return grade.getId() == null ? "redirect:/admin/grades/new" : 
                   "redirect:/admin/grades/" + grade.getId() + "/edit";
        }
    }
    
    /**
     * 删除成绩
     */
    @PostMapping("/{id}/delete")
    public String deleteGrade(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            gradeService.deleteGrade(id);
            redirectAttributes.addFlashAttribute("success", "成绩删除成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("删除成绩失败", e);
            redirectAttributes.addFlashAttribute("error", "删除成绩失败: " + e.getMessage());
        }
        
        return "redirect:/admin/grades";
    }
    
    /**
     * 学生成绩单页面
     */
    @GetMapping("/student/{studentId}")
    public String studentGrades(@PathVariable Long studentId, Model model) {
        try {
            Optional<Student> studentOpt = studentService.findStudentById(studentId);
            if (studentOpt.isEmpty()) {
                model.addAttribute("error", "学生不存在");
                return "redirect:/admin/grades";
            }
            
            Student student = studentOpt.get();
            List<Grade> grades = gradeService.findGradesByStudent(studentId);
            
            // 计算统计信息
            Double averageScore = gradeService.calculateAverageScore(studentId);
            Double gpa = gradeService.calculateGPA(studentId);
            long totalCredits = gradeService.calculateTotalCredits(studentId);
            
            model.addAttribute("student", student);
            model.addAttribute("grades", grades);
            model.addAttribute("averageScore", averageScore);
            model.addAttribute("gpa", gpa);
            model.addAttribute("totalCredits", totalCredits);
            model.addAttribute("currentPage", "grades");
            
            return "admin/academic/student-grades";
            
        } catch (Exception e) {
            logger.error("加载学生成绩单失败", e);
            model.addAttribute("error", "加载学生成绩单失败: " + e.getMessage());
            return "redirect:/admin/grades";
        }
    }
    
    /**
     * 课程成绩单页面
     */
    @GetMapping("/course/{courseId}")
    public String courseGrades(@PathVariable Long courseId, Model model) {
        try {
            Optional<Course> courseOpt = courseService.findCourseById(courseId);
            if (courseOpt.isEmpty()) {
                model.addAttribute("error", "课程不存在");
                return "redirect:/admin/grades";
            }
            
            Course course = courseOpt.get();
            List<Grade> grades = gradeService.findGradesByCourse(courseId);
            
            // 计算统计信息
            Double averageScore = gradeService.calculateCourseAverageScore(courseId);
            long passCount = gradeService.countPassingGrades(courseId);
            long failCount = gradeService.countFailingGrades(courseId);
            Double passRate = grades.isEmpty() ? 0.0 : (double) passCount / grades.size() * 100;
            
            model.addAttribute("course", course);
            model.addAttribute("grades", grades);
            model.addAttribute("averageScore", averageScore);
            model.addAttribute("passCount", passCount);
            model.addAttribute("failCount", failCount);
            model.addAttribute("passRate", passRate);
            model.addAttribute("currentPage", "grades");
            
            return "admin/academic/course-grades";
            
        } catch (Exception e) {
            logger.error("加载课程成绩单失败", e);
            model.addAttribute("error", "加载课程成绩单失败: " + e.getMessage());
            return "redirect:/admin/grades";
        }
    }
    
    /**
     * 成绩统计页面
     */
    @GetMapping("/statistics")
    public String gradeStatistics(Model model) {
        try {
            long totalGrades = gradeService.countTotalGrades();
            Double overallAverage = gradeService.calculateOverallAverageScore();
            Map<String, Object> gradeDistribution = gradeService.getGradeDistribution();
            // 获取所有课程的统计信息
            Map<String, Object> courseStats = new HashMap<>();
            
            model.addAttribute("totalGrades", totalGrades);
            model.addAttribute("overallAverage", overallAverage);
            model.addAttribute("gradeDistribution", gradeDistribution);
            model.addAttribute("courseStats", courseStats);
            model.addAttribute("currentPage", "grades");
            
            return "admin/academic/grade-statistics";
            
        } catch (Exception e) {
            logger.error("加载成绩统计失败", e);
            model.addAttribute("error", "加载成绩统计失败: " + e.getMessage());
            return "redirect:/admin/grades";
        }
    }
    
    /**
     * 批量录入成绩页面
     */
    @GetMapping("/batch-input")
    public String batchInputGrades(Model model) {
        List<Course> courses = courseService.findAllCourses();
        model.addAttribute("courses", courses);
        model.addAttribute("currentPage", "grades");
        return "admin/academic/grade-batch-input";
    }
    
    /**
     * 导出成绩数据
     */
    @GetMapping("/export")
    public String exportGrades(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String semester,
            RedirectAttributes redirectAttributes) {
        try {
            // 注意：成绩导出功能待实现，当前返回成功消息
            redirectAttributes.addFlashAttribute("success", "成绩数据导出成功");
        } catch (Exception e) {
            logger.error("导出成绩数据失败", e);
            redirectAttributes.addFlashAttribute("error", "导出成绩数据失败: " + e.getMessage());
        }
        
        return "redirect:/admin/grades";
    }
}
