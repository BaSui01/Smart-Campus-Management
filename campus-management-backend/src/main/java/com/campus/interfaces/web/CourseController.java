package com.campus.interfaces.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.campus.application.service.CourseService;
import com.campus.domain.entity.Course;

/**
 * 课程管理控制器 - 页面路由
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@Controller
public class CourseController {

    @Autowired
    private CourseService courseService;

    // ========== 管理页面路由 ==========

    /**
     * 课程管理页面
     */
    @GetMapping("/admin/courses")
    public String adminCourses(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "20") int size,
                              @RequestParam(defaultValue = "") String search,
                              @RequestParam(defaultValue = "") String courseType,
                              @RequestParam(defaultValue = "") String department,
                              @RequestParam(defaultValue = "") String semester,
                              @RequestParam(defaultValue = "") String status,
                              Model model) {
        try {
            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            if (!search.isEmpty()) {
                params.put("search", search);
            }
            if (!courseType.isEmpty()) {
                params.put("courseType", courseType);
            }
            if (!department.isEmpty()) {
                params.put("department", department);
            }
            if (!semester.isEmpty()) {
                params.put("semester", semester);
            }
            if (!status.isEmpty()) {
                params.put("status", status);
            }

            // 分页查询课程
            Pageable pageable = PageRequest.of(page, size);
            Page<Course> coursePage = courseService.findCoursesByPage(pageable, params);

            // 获取课程统计
            Map<String, Long> courseStats = courseService.countCoursesByType();

            // 获取筛选选项
            List<String> courseTypes = List.of("必修", "选修", "实践", "通识");
            List<String> departments = List.of("计算机学院", "软件学院", "信息学院", "数学学院", "外语学院");
            List<String> semesters = generateSemesterList();

            model.addAttribute("courses", coursePage);
            model.addAttribute("stats", courseStats);
            model.addAttribute("courseTypes", courseTypes);
            model.addAttribute("departments", departments);
            model.addAttribute("semesters", semesters);
            model.addAttribute("search", search);
            model.addAttribute("courseType", courseType);
            model.addAttribute("department", department);
            model.addAttribute("semester", semester);
            model.addAttribute("status", status);
            model.addAttribute("pageTitle", "课程管理");
            model.addAttribute("currentPage", "courses");
            return "admin/courses";
        } catch (Exception e) {
            model.addAttribute("error", "加载课程列表失败：" + e.getMessage());
            return "admin/courses";
        }
    }

    /**
     * 添加课程页面
     */
    @GetMapping("/admin/courses/new")
    public String newCourse(Model model) {
        try {
            // 获取筛选选项
            List<String> courseTypes = List.of("必修", "选修", "实践", "通识");
            List<String> departments = List.of("计算机学院", "软件学院", "信息学院", "数学学院", "外语学院");
            List<String> semesters = generateSemesterList();

            model.addAttribute("courseTypes", courseTypes);
            model.addAttribute("departments", departments);
            model.addAttribute("semesters", semesters);
            model.addAttribute("pageTitle", "添加课程");
            model.addAttribute("currentPage", "courses");
            return "admin/course-form";
        } catch (Exception e) {
            model.addAttribute("error", "加载添加课程页面失败：" + e.getMessage());
            return "admin/courses";
        }
    }

    /**
     * 编辑课程页面
     */
    @GetMapping("/admin/courses/edit")
    public String editCourse(@RequestParam Long id, Model model) {
        try {
            // 获取课程信息
            Course course = courseService.findById(id).orElse(null);
            if (course == null) {
                model.addAttribute("error", "课程不存在");
                return "admin/courses";
            }

            // 获取筛选选项
            List<String> courseTypes = List.of("必修", "选修", "实践", "通识");
            List<String> departments = List.of("计算机学院", "软件学院", "信息学院", "数学学院", "外语学院");
            List<String> semesters = generateSemesterList();

            model.addAttribute("course", course);
            model.addAttribute("courseTypes", courseTypes);
            model.addAttribute("departments", departments);
            model.addAttribute("semesters", semesters);
            model.addAttribute("pageTitle", "编辑课程");
            model.addAttribute("currentPage", "courses");
            return "admin/course-form";
        } catch (Exception e) {
            model.addAttribute("error", "加载编辑课程页面失败：" + e.getMessage());
            return "admin/courses";
        }
    }

    /**
     * 课程详情页面
     */
    @GetMapping("/admin/courses/detail")
    public String courseDetail(@RequestParam Long id, Model model) {
        try {
            // 获取课程信息
            Course course = courseService.findById(id).orElse(null);
            if (course == null) {
                model.addAttribute("error", "课程不存在");
                return "admin/courses";
            }

            model.addAttribute("course", course);
            model.addAttribute("pageTitle", "课程详情");
            model.addAttribute("currentPage", "courses");
            return "admin/course-detail";
        } catch (Exception e) {
            model.addAttribute("error", "加载课程详情失败：" + e.getMessage());
            return "admin/courses";
        }
    }

    // ========== 学生端页面路由 ==========

    /**
     * 学生选课页面
     */
    @GetMapping("/student/courses")
    public String studentCourses(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "20") int size,
                                @RequestParam(defaultValue = "") String search,
                                @RequestParam(defaultValue = "") String courseType,
                                @RequestParam(defaultValue = "") String department,
                                Model model) {
        try {
            // 构建查询参数 - 只显示可选课程
            Map<String, Object> params = new HashMap<>();
            params.put("status", 1); // 只显示启用的课程
            if (!search.isEmpty()) {
                params.put("search", search);
            }
            if (!courseType.isEmpty()) {
                params.put("courseType", courseType);
            }
            if (!department.isEmpty()) {
                params.put("department", department);
            }

            // 分页查询课程
            Pageable pageable = PageRequest.of(page, size);
            Page<Course> coursePage = courseService.findCoursesByPage(pageable, params);

            // 获取筛选选项
            List<String> courseTypes = List.of("必修", "选修", "实践", "通识");
            List<String> departments = List.of("计算机学院", "软件学院", "信息学院", "数学学院", "外语学院");

            model.addAttribute("courses", coursePage);
            model.addAttribute("courseTypes", courseTypes);
            model.addAttribute("departments", departments);
            model.addAttribute("search", search);
            model.addAttribute("courseType", courseType);
            model.addAttribute("department", department);
            model.addAttribute("pageTitle", "选课中心");
            model.addAttribute("currentPage", "courses");
            return "student/courses";
        } catch (Exception e) {
            model.addAttribute("error", "加载课程列表失败：" + e.getMessage());
            return "student/courses";
        }
    }

    /**
     * 学生课程详情页面
     */
    @GetMapping("/student/courses/detail")
    public String studentCourseDetail(@RequestParam Long id, Model model) {
        try {
            // 获取课程信息
            Course course = courseService.findById(id).orElse(null);
            if (course == null) {
                model.addAttribute("error", "课程不存在");
                return "student/courses";
            }

            model.addAttribute("course", course);
            model.addAttribute("pageTitle", "课程详情");
            model.addAttribute("currentPage", "courses");
            return "student/course-detail";
        } catch (Exception e) {
            model.addAttribute("error", "加载课程详情失败：" + e.getMessage());
            return "student/courses";
        }
    }

    // ========== 教师端页面路由 ==========

    /**
     * 教师课程管理页面
     */
    @GetMapping("/teacher/courses")
    public String teacherCourses(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "20") int size,
                                @RequestParam(defaultValue = "") String search,
                                @RequestParam(defaultValue = "") String semester,
                                HttpServletRequest request,
                                Model model) {
        try {
            // 构建查询参数 - 只显示当前教师的课程
            Map<String, Object> params = new HashMap<>();

            // 添加当前教师ID过滤
            // 注意：在实际项目中，应该从认证信息中获取当前教师ID
            // 这里为了演示，使用模拟的教师ID
            Long currentTeacherId = getCurrentTeacherId(request);
            if (currentTeacherId != null) {
                params.put("teacherId", currentTeacherId);
            }

            if (!search.isEmpty()) {
                params.put("search", search);
            }
            if (!semester.isEmpty()) {
                params.put("semester", semester);
            }

            // 分页查询课程
            Pageable pageable = PageRequest.of(page, size);
            Page<Course> coursePage = courseService.findCoursesByPage(pageable, params);

            // 获取学期列表
            List<String> semesters = generateSemesterList();

            model.addAttribute("courses", coursePage);
            model.addAttribute("semesters", semesters);
            model.addAttribute("search", search);
            model.addAttribute("semester", semester);
            model.addAttribute("pageTitle", "我的课程");
            model.addAttribute("currentPage", "courses");
            return "teacher/courses";
        } catch (Exception e) {
            model.addAttribute("error", "加载课程列表失败：" + e.getMessage());
            return "teacher/courses";
        }
    }

    // ========== 辅助方法 ==========

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

    /**
     * 获取当前教师ID
     * 从认证信息中获取当前登录教师的ID
     */
    private Long getCurrentTeacherId(HttpServletRequest request) {
        try {
            // 从session或JWT token中获取当前用户信息
            String username = (String) request.getAttribute("currentUsername");
            if (username != null) {
                // 根据用户名查找用户信息，然后获取教师ID
                // 这里假设用户表中有教师相关信息，或者有单独的教师表
                // 为了演示，返回一个固定的教师ID
                // 在实际项目中，应该根据用户角色和关联关系来获取
                return 1L; // 示例教师ID
            }
            return null;
        } catch (Exception e) {
            System.err.println("获取当前教师ID失败: " + e.getMessage());
            return null;
        }
    }
}
