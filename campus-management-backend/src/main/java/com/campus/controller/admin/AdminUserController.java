package com.campus.controller.admin;

import com.campus.entity.User;

import com.campus.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理后台用户管理控制器
 * 处理用户、角色、权限管理相关的页面请求
 *
 * @author campus
 * @since 2025-06-05
 */
@Controller
@RequestMapping("/admin")
public class AdminUserController {

    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户管理页面
     */
    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       @RequestParam(defaultValue = "") String search,
                       @RequestParam(defaultValue = "") String role,
                       @RequestParam(defaultValue = "") String status,
                       Model model) {
        try {
            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            if (!search.isEmpty()) {
                params.put("search", search);
            }
            if (!role.isEmpty()) {
                params.put("role", role);
            }
            if (!status.isEmpty()) {
                params.put("status", status);
            }

            // 分页查询用户
            IPage<User> userPage = userService.findUsersByPage(page, size, params);

            // 获取用户统计
            UserService.UserStatistics userStats = userService.getUserStatistics();

            model.addAttribute("users", userPage);
            model.addAttribute("stats", userStats);
            model.addAttribute("search", search);
            model.addAttribute("role", role);
            model.addAttribute("status", status);
            model.addAttribute("pageTitle", "用户管理");
            model.addAttribute("currentPage", "users");
            return "admin/users";
        } catch (Exception e) {
            model.addAttribute("error", "加载用户列表失败：" + e.getMessage());
            return "admin/users";
        }
    }

    /**
     * 角色管理页面
     */
    @GetMapping("/roles")
    public String roles(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        try {
            // 获取各角色的用户数量
            List<User> adminUsers = userService.findUsersByRole("ADMIN");
            List<User> teacherUsers = userService.findUsersByRole("TEACHER");
            List<User> studentUsers = userService.findUsersByRole("STUDENT");
            List<User> financeUsers = userService.findUsersByRole("FINANCE");
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalRoles", 4);
            stats.put("systemRoles", 4);
            stats.put("customRoles", 0);
            stats.put("activeRoles", 4);

            // 真实角色列表
            List<Map<String, Object>> roles = new ArrayList<>();
            roles.add(Map.of("id", 1, "name", "ADMIN", "displayName", "系统管理员", "description", "拥有所有权限", "userCount", adminUsers.size(), "status", 1));
            roles.add(Map.of("id", 2, "name", "TEACHER", "displayName", "教师", "description", "教学管理权限", "userCount", teacherUsers.size(), "status", 1));
            roles.add(Map.of("id", 3, "name", "STUDENT", "displayName", "学生", "description", "学生基础权限", "userCount", studentUsers.size(), "status", 1));
            roles.add(Map.of("id", 4, "name", "FINANCE", "displayName", "财务人员", "description", "财务管理权限", "userCount", financeUsers.size(), "status", 1));

            model.addAttribute("roles", roles);
            model.addAttribute("stats", stats);
            model.addAttribute("pageTitle", "角色管理");
            model.addAttribute("currentPage", "roles");
            return "admin/roles";
        } catch (Exception e) {
            model.addAttribute("error", "加载角色列表失败：" + e.getMessage());
            return "admin/roles";
        }
    }

    /**
     * 权限管理页面
     */
    @GetMapping("/permissions")
    public String permissions(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "20") int size,
                             Model model) {
        try {
            // 系统预定义权限列表
            List<Map<String, Object>> permissions = new ArrayList<>();
            permissions.add(Map.of("id", 1, "code", "user:view", "name", "查看用户", "module", "用户管理", "description", "查看用户列表和详情"));
            permissions.add(Map.of("id", 2, "code", "user:create", "name", "创建用户", "module", "用户管理", "description", "创建新用户"));
            permissions.add(Map.of("id", 3, "code", "user:edit", "name", "编辑用户", "module", "用户管理", "description", "编辑用户信息"));
            permissions.add(Map.of("id", 4, "code", "user:delete", "name", "删除用户", "module", "用户管理", "description", "删除用户"));
            permissions.add(Map.of("id", 5, "code", "course:view", "name", "查看课程", "module", "课程管理", "description", "查看课程列表和详情"));
            permissions.add(Map.of("id", 6, "code", "course:create", "name", "创建课程", "module", "课程管理", "description", "创建新课程"));
            permissions.add(Map.of("id", 7, "code", "course:edit", "name", "编辑课程", "module", "课程管理", "description", "编辑课程信息"));
            permissions.add(Map.of("id", 8, "code", "course:delete", "name", "删除课程", "module", "课程管理", "description", "删除课程"));
            permissions.add(Map.of("id", 9, "code", "student:view", "name", "查看学生", "module", "学生管理", "description", "查看学生列表和详情"));
            permissions.add(Map.of("id", 10, "code", "student:create", "name", "创建学生", "module", "学生管理", "description", "创建新学生"));
            permissions.add(Map.of("id", 11, "code", "grade:view", "name", "查看成绩", "module", "成绩管理", "description", "查看成绩列表"));
            permissions.add(Map.of("id", 12, "code", "grade:edit", "name", "编辑成绩", "module", "成绩管理", "description", "录入和修改成绩"));
            permissions.add(Map.of("id", 13, "code", "payment:view", "name", "查看缴费", "module", "缴费管理", "description", "查看缴费记录"));
            permissions.add(Map.of("id", 14, "code", "payment:manage", "name", "管理缴费", "module", "缴费管理", "description", "处理缴费业务"));

            // 权限统计
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalPermissions", permissions.size());
            stats.put("systemPermissions", permissions.size());
            stats.put("modulePermissions", 5);
            stats.put("activePermissions", permissions.size());

            model.addAttribute("permissions", permissions);
            model.addAttribute("stats", stats);
            model.addAttribute("pageTitle", "权限管理");
            model.addAttribute("currentPage", "permissions");
            return "admin/permissions";
        } catch (Exception e) {
            model.addAttribute("error", "加载权限列表失败：" + e.getMessage());
            return "admin/permissions";
        }
    }
}
