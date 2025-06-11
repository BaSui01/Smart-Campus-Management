package com.campus.interfaces.web;

import com.campus.domain.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * 新版管理后台页面控制器
 * 处理新版管理页面的路由和模板渲染
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Controller
@RequestMapping("/admin/new")
public class AdminManagementController {

    /**
     * 用户管理页面
     */
    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_SYSTEM_ADMIN', 'ROLE_HR_DIRECTOR', 'ROLE_HR_STAFF')")
    public String usersPage(Model model, HttpServletRequest request) {
        try {
            // 设置页面基本信息
            model.addAttribute("pageTitle", "用户管理");
            model.addAttribute("currentPage", "users");

            // 添加当前用户信息
            addCurrentUserToModel(model, request);

            return "admin/users/management";
        } catch (Exception e) {
            model.addAttribute("error", "加载用户管理页面失败：" + e.getMessage());
            return "admin/error";
        }
    }

    /**
     * 课程管理页面
     */
    @GetMapping("/courses")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_ACADEMIC_DIRECTOR', 'ROLE_DEAN', 'ROLE_DEPARTMENT_HEAD')")
    public String coursesPage(Model model, HttpServletRequest request) {
        try {
            // 设置页面基本信息
            model.addAttribute("pageTitle", "课程管理");
            model.addAttribute("currentPage", "courses");
            
            // 添加当前用户信息
            addCurrentUserToModel(model, request);
            
            return "admin/courses/management";
        } catch (Exception e) {
            model.addAttribute("error", "加载课程管理页面失败：" + e.getMessage());
            return "admin/error";
        }
    }

    /**
     * 成绩管理页面
     */
    @GetMapping("/grades")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_ACADEMIC_DIRECTOR', 'ROLE_DEAN', 'ROLE_TEACHER')")
    public String gradesPage(Model model, HttpServletRequest request) {
        try {
            // 设置页面基本信息
            model.addAttribute("pageTitle", "成绩管理");
            model.addAttribute("currentPage", "grades");
            
            // 添加当前用户信息
            addCurrentUserToModel(model, request);
            
            return "admin/grades/index";
        } catch (Exception e) {
            model.addAttribute("error", "加载成绩管理页面失败：" + e.getMessage());
            return "admin/error";
        }
    }

    /**
     * 考勤管理页面
     */
    @GetMapping("/attendance")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_ACADEMIC_DIRECTOR', 'ROLE_DEAN', 'ROLE_TEACHER')")
    public String attendancePage(Model model, HttpServletRequest request) {
        try {
            // 设置页面基本信息
            model.addAttribute("pageTitle", "考勤管理");
            model.addAttribute("currentPage", "attendance");
            
            // 添加当前用户信息
            addCurrentUserToModel(model, request);
            
            return "admin/attendance/index";
        } catch (Exception e) {
            model.addAttribute("error", "加载考勤管理页面失败：" + e.getMessage());
            return "admin/error";
        }
    }

    /**
     * 缴费管理页面
     */
    @GetMapping("/payments")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_FINANCE_DIRECTOR', 'ROLE_FINANCE_STAFF')")
    public String paymentsPage(Model model, HttpServletRequest request) {
        try {
            // 设置页面基本信息
            model.addAttribute("pageTitle", "缴费管理");
            model.addAttribute("currentPage", "payments");
            
            // 添加当前用户信息
            addCurrentUserToModel(model, request);
            
            return "admin/payments/index";
        } catch (Exception e) {
            model.addAttribute("error", "加载缴费管理页面失败：" + e.getMessage());
            return "admin/error";
        }
    }

    /**
     * 学生管理页面（新版本）
     */
    @GetMapping("/students-new")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_ACADEMIC_DIRECTOR', 'ROLE_DEAN', 'ROLE_DEPARTMENT_HEAD')")
    public String studentsPage(Model model, HttpServletRequest request) {
        try {
            // 设置页面基本信息
            model.addAttribute("pageTitle", "学生管理");
            model.addAttribute("currentPage", "students");

            // 添加当前用户信息
            addCurrentUserToModel(model, request);

            return "admin/students/index";
        } catch (Exception e) {
            model.addAttribute("error", "加载学生管理页面失败：" + e.getMessage());
            return "admin/error";
        }
    }





    /**
     * 管理界面演示页面
     */
    @GetMapping("/demo")
    public String managementDemo(Model model, HttpServletRequest request) {
        try {
            // 设置页面基本信息
            model.addAttribute("pageTitle", "管理界面演示");
            model.addAttribute("currentPage", "demo");

            // 添加当前用户信息
            addCurrentUserToModel(model, request);

            return "admin/test/management-demo";
        } catch (Exception e) {
            model.addAttribute("error", "加载演示页面失败：" + e.getMessage());
            return "admin/error";
        }
    }

    /**
     * 添加当前用户信息到模型
     */
    private void addCurrentUserToModel(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser != null) {
                model.addAttribute("currentUser", currentUser);
                model.addAttribute("username", currentUser.getUsername());
                model.addAttribute("realName", currentUser.getRealName());
            }
        }
    }
}
