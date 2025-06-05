package com.campus.interfaces.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.campus.domain.entity.Student;
import com.campus.application.service.SchoolClassService;
import com.campus.application.service.StudentService;
import com.campus.domain.entity.SchoolClass;

/**
 * 学生管理控制器 - 页面路由
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@Controller
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private SchoolClassService schoolClassService;

    // ========== 管理页面路由 ==========

    /**
     * 学生管理页面
     */
    @GetMapping("/admin/students")
    public String adminStudents(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size,
                               @RequestParam(defaultValue = "") String search,
                               @RequestParam(defaultValue = "") String grade,
                               @RequestParam(defaultValue = "") String classId,
                               @RequestParam(defaultValue = "") String status,
                               Model model) {
        try {
            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            if (!search.isEmpty()) {
                params.put("search", search);
            }
            if (!grade.isEmpty()) {
                params.put("grade", grade);
            }
            if (!classId.isEmpty()) {
                params.put("classId", classId);
            }
            if (!status.isEmpty()) {
                params.put("status", status);
            }

            // 分页查询学生
            Pageable pageable = PageRequest.of(page, size);
            Page<Student> studentPage = studentService.findStudentsByPage(pageable, params);

            // 获取学生统计
            StudentService.StudentStatistics studentStats = studentService.getStudentStatistics();

            // 获取班级列表供筛选
            List<SchoolClass> classes = schoolClassService.findAll();

            // 获取年级列表
            List<String> grades = generateGradeList();

            model.addAttribute("students", studentPage);
            model.addAttribute("stats", studentStats);
            model.addAttribute("classes", classes);
            model.addAttribute("grades", grades);
            model.addAttribute("search", search);
            model.addAttribute("grade", grade);
            model.addAttribute("classId", classId);
            model.addAttribute("status", status);
            model.addAttribute("pageTitle", "学生管理");
            model.addAttribute("currentPage", "students");
            return "admin/students";
        } catch (Exception e) {
            model.addAttribute("error", "加载学生列表失败：" + e.getMessage());
            return "admin/students";
        }
    }

    /**
     * 添加学生页面
     */
    @GetMapping("/admin/students/new")
    public String newStudent(Model model) {
        try {
            // 获取真实班级列表供选择
            List<SchoolClass> allClasses = schoolClassService.findAll();

            // 获取年级列表（从班级数据中提取）
            List<String> grades = allClasses.stream()
                .map(SchoolClass::getGrade)
                .distinct()
                .sorted()
                .toList();

            // 从班级名称中提取专业信息（基于命名规则）
            List<String> majors = allClasses.stream()
                .map(SchoolClass::getClassName)
                .map(this::extractMajorFromClassName)
                .distinct()
                .filter(major -> !major.equals("其他专业"))
                .sorted()
                .toList();

            // 如果没有提取到专业，使用预定义列表
            if (majors.isEmpty()) {
                majors = List.of("计算机科学与技术", "软件工程", "信息安全", "数据科学与大数据技术", "人工智能", "网络工程", "物联网工程");
            }

            model.addAttribute("classes", allClasses);
            model.addAttribute("grades", grades);
            model.addAttribute("majors", majors);
            model.addAttribute("pageTitle", "添加学生");
            model.addAttribute("currentPage", "students");
            return "admin/student-form";
        } catch (Exception e) {
            model.addAttribute("error", "加载添加学生页面失败：" + e.getMessage());
            return "admin/students";
        }
    }

    /**
     * 编辑学生页面
     */
    @GetMapping("/admin/students/edit")
    public String editStudent(@RequestParam Long id, Model model) {
        try {
            // 获取学生信息
            Student student = studentService.findById(id).orElse(null);
            if (student == null) {
                model.addAttribute("error", "学生不存在");
                return "admin/students";
            }

            // 获取班级列表
            List<SchoolClass> allClasses = schoolClassService.findAll();

            // 获取年级列表
            List<String> grades = allClasses.stream()
                .map(SchoolClass::getGrade)
                .distinct()
                .sorted()
                .toList();

            // 获取专业列表
            List<String> majors = allClasses.stream()
                .map(SchoolClass::getClassName)
                .map(this::extractMajorFromClassName)
                .distinct()
                .filter(major -> !major.equals("其他专业"))
                .sorted()
                .toList();

            if (majors.isEmpty()) {
                majors = List.of("计算机科学与技术", "软件工程", "信息安全", "数据科学与大数据技术", "人工智能", "网络工程", "物联网工程");
            }

            model.addAttribute("student", student);
            model.addAttribute("classes", allClasses);
            model.addAttribute("grades", grades);
            model.addAttribute("majors", majors);
            model.addAttribute("pageTitle", "编辑学生");
            model.addAttribute("currentPage", "students");
            return "admin/student-form";
        } catch (Exception e) {
            model.addAttribute("error", "加载编辑学生页面失败：" + e.getMessage());
            return "admin/students";
        }
    }

    /**
     * 学生详情页面
     */
    @GetMapping("/admin/students/detail")
    public String studentDetail(@RequestParam Long id, Model model) {
        try {
            // 获取学生信息
            Student student = studentService.findById(id).orElse(null);
            if (student == null) {
                model.addAttribute("error", "学生不存在");
                return "admin/students";
            }

            model.addAttribute("student", student);
            model.addAttribute("pageTitle", "学生详情");
            model.addAttribute("currentPage", "students");
            return "admin/student-detail";
        } catch (Exception e) {
            model.addAttribute("error", "加载学生详情失败：" + e.getMessage());
            return "admin/students";
        }
    }

    // ========== 学生端页面路由 ==========

    /**
     * 学生个人信息页面
     */
    @GetMapping("/student/profile")
    public String studentProfile(Model model) {
        try {
            // TODO: 获取当前登录学生的信息
            // Student currentStudent = getCurrentStudent();
            
            model.addAttribute("pageTitle", "个人信息");
            model.addAttribute("currentPage", "profile");
            return "student/profile";
        } catch (Exception e) {
            model.addAttribute("error", "加载个人信息失败：" + e.getMessage());
            return "student/profile";
        }
    }

    /**
     * 学生成绩查询页面
     */
    @GetMapping("/student/grades")
    public String studentGrades(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size,
                               @RequestParam(defaultValue = "") String semester,
                               @RequestParam(defaultValue = "") String courseType,
                               Model model) {
        try {
            // TODO: 获取当前登录学生的成绩
            
            model.addAttribute("pageTitle", "成绩查询");
            model.addAttribute("currentPage", "grades");
            return "student/grades";
        } catch (Exception e) {
            model.addAttribute("error", "加载成绩信息失败：" + e.getMessage());
            return "student/grades";
        }
    }

    /**
     * 学生课程表页面
     */
    @GetMapping("/student/schedule")
    public String studentSchedule(@RequestParam(defaultValue = "") String semester,
                                 Model model) {
        try {
            // TODO: 获取当前登录学生的课程表
            
            model.addAttribute("pageTitle", "课程表");
            model.addAttribute("currentPage", "schedule");
            return "student/schedule";
        } catch (Exception e) {
            model.addAttribute("error", "加载课程表失败：" + e.getMessage());
            return "student/schedule";
        }
    }

    // ========== 辅助方法 ==========

    /**
     * 动态生成年级列表
     */
    private List<String> generateGradeList() {
        List<String> grades = new ArrayList<>();
        int currentYear = java.time.LocalDate.now().getYear();

        // 生成过去4年到未来1年的年级
        for (int year = currentYear - 4; year <= currentYear + 1; year++) {
            grades.add(year + "级");
        }

        return grades;
    }

    /**
     * 从班级名称中提取专业信息
     */
    private String extractMajorFromClassName(String className) {
        if (className == null || className.isEmpty()) {
            return "其他专业";
        }

        // 基于常见的班级命名规则提取专业
        if (className.contains("计算机科学") || className.contains("计科")) {
            return "计算机科学与技术";
        } else if (className.contains("软件工程") || className.contains("软工")) {
            return "软件工程";
        } else if (className.contains("信息安全") || className.contains("信安")) {
            return "信息安全";
        } else if (className.contains("数据科学") || className.contains("大数据")) {
            return "数据科学与大数据技术";
        } else if (className.contains("人工智能") || className.contains("AI")) {
            return "人工智能";
        } else if (className.contains("网络工程") || className.contains("网工")) {
            return "网络工程";
        } else if (className.contains("物联网")) {
            return "物联网工程";
        } else {
            return "其他专业";
        }
    }
}
