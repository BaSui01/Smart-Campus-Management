package com.campus.interfaces.rest.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.campus.domain.entity.auth.User;
import com.campus.domain.entity.organization.SchoolClass;
import com.campus.domain.entity.organization.Student;
import com.campus.application.service.academic.StudentService;
import com.campus.application.service.auth.UserService;
import com.campus.application.service.organization.SchoolClassService;
import com.campus.application.service.academic.CourseScheduleService;
import com.campus.application.service.academic.GradeService;
import com.campus.domain.entity.academic.CourseSchedule;
import com.campus.domain.entity.academic.Grade;

/**
 * 学生管理控制器 - 页面路由
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@Controller
public class StudentController {

    private final StudentService studentService;
    private final SchoolClassService schoolClassService;
    private final GradeService gradeService;
    private final CourseScheduleService courseScheduleService;
    private final UserService userService;

    public StudentController(StudentService studentService,
                           SchoolClassService schoolClassService,
                           GradeService gradeService,
                           CourseScheduleService courseScheduleService,
                           UserService userService) {
        this.studentService = studentService;
        this.schoolClassService = schoolClassService;
        this.gradeService = gradeService;
        this.courseScheduleService = courseScheduleService;
        this.userService = userService;
    }

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
            return "admin/student/students";
        } catch (Exception e) {
            model.addAttribute("error", "加载学生列表失败：" + e.getMessage());
            return "admin/student/students";
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
            return "admin/student/students";
        } catch (Exception e) {
            model.addAttribute("error", "加载添加学生页面失败：" + e.getMessage());
            return "admin/student/students";
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
                return "admin/student/students";
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
            return "admin/student/students";
        } catch (Exception e) {
            model.addAttribute("error", "加载编辑学生页面失败：" + e.getMessage());
            return "admin/student/students";
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
                return "admin/student/students";
            }

            model.addAttribute("student", student);
            model.addAttribute("pageTitle", "学生详情");
            model.addAttribute("currentPage", "students");
            return "admin/student/students";
        } catch (Exception e) {
            model.addAttribute("error", "加载学生详情失败：" + e.getMessage());
            return "admin/student/students";
        }
    }

    // ========== 辅助方法 ==========

    // ========== 学生端页面路由 ==========

    /**
     * 学生个人信息页面
     */
    @GetMapping("/student/profile")
    public String studentProfile(HttpServletRequest request, Model model) {
        try {
            // 获取当前登录学生的信息
            Student currentStudent = getCurrentStudent(request);
            if (currentStudent == null) {
                model.addAttribute("error", "未找到学生信息，请重新登录");
                return "student/profile";
            }

            // 获取学生所在班级信息
            SchoolClass studentClass = null;
            if (currentStudent.getClassId() != null) {
                studentClass = schoolClassService.findById(currentStudent.getClassId()).orElse(null);
            }

            model.addAttribute("student", currentStudent);
            model.addAttribute("studentClass", studentClass);
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
                               HttpServletRequest request,
                               Model model) {
        try {
            // 获取当前登录学生的信息
            Student currentStudent = getCurrentStudent(request);
            if (currentStudent == null) {
                model.addAttribute("error", "未找到学生信息，请重新登录");
                return "student/grades";
            }

            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            params.put("studentId", currentStudent.getId());
            if (!semester.isEmpty()) {
                params.put("semester", semester);
            }
            if (!courseType.isEmpty()) {
                params.put("courseType", courseType);
            }

            // 分页查询学生成绩
            Pageable pageable = PageRequest.of(page, size);
            Page<Grade> gradePage = gradeService.findGradesByPage(pageable, params);

            // 获取学生成绩统计
            Map<String, Object> gradeStats = gradeService.getStudentGradeStatistics(currentStudent.getId());

            // 生成学期列表
            List<String> semesters = generateSemesterList();

            model.addAttribute("grades", gradePage);
            model.addAttribute("gradeStats", gradeStats);
            model.addAttribute("semesters", semesters);
            model.addAttribute("semester", semester);
            model.addAttribute("courseType", courseType);
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
                                 HttpServletRequest request,
                                 Model model) {
        try {
            // 获取当前登录学生的信息
            Student currentStudent = getCurrentStudent(request);
            if (currentStudent == null) {
                model.addAttribute("error", "未找到学生信息，请重新登录");
                return "student/schedule";
            }

            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            params.put("studentId", currentStudent.getId());
            if (!semester.isEmpty()) {
                params.put("semester", semester);
            }

            // 获取学生课程表
            List<CourseSchedule> schedules = courseScheduleService.findSchedulesByStudent(currentStudent.getId(), params);

            // 生成学期列表
            List<String> semesters = generateSemesterList();

            model.addAttribute("schedules", schedules);
            model.addAttribute("semesters", semesters);
            model.addAttribute("semester", semester);
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

    /**
     * 获取当前登录学生信息
     */
    private Student getCurrentStudent(HttpServletRequest request) {
        try {
            // 从session或JWT token中获取当前用户信息
            String username = (String) request.getAttribute("currentUsername");
            if (username != null) {
                // 根据用户名查找用户信息
                Optional<User> userOpt = userService.findByUsername(username);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    // 根据用户ID查找对应的学生信息
                    // 假设用户表和学生表通过某种方式关联（如用户名、邮箱等）
                    return studentService.findByUserId(user.getId()).orElse(null);
                }
            }
            return null;
        } catch (Exception e) {
            System.err.println("获取当前学生信息失败: " + e.getMessage());
            return null;
        }
    }

    /**
     * 生成学期列表
     */
    private List<String> generateSemesterList() {
        List<String> semesters = new ArrayList<>();
        int currentYear = java.time.LocalDate.now().getYear();

        // 生成过去2年到未来1年的学期
        for (int year = currentYear - 2; year <= currentYear + 1; year++) {
            semesters.add(year + "春季学期");
            semesters.add(year + "秋季学期");
        }

        return semesters;
    }
}
