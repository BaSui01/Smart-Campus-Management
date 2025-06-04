package com.campus.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.campus.common.ApiResponse;
import com.campus.entity.Course;
import com.campus.entity.SchoolClass;
import com.campus.entity.Student;
import com.campus.entity.User;
import com.campus.service.UserService;

/**
 * 后台管理控制器
 * 处理后台管理页面的请求和数据
 *
 * @author campus
 * @since 2025-06-04
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 仪表盘页面
     */
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        try {
            // 获取真实统计数据
            Map<String, Object> stats = new HashMap<>();

            // 用户统计
            UserService.UserStatistics userStats = userService.getUserStatistics();
            stats.put("totalUsers", userStats.getTotalUsers());
            stats.put("activeUsers", userStats.getActiveUsers());

            // 学生统计（使用模拟数据，待StudentService实现后替换）
            long totalStudents = 450; // 模拟数据
            long activeStudents = 420; // 模拟数据
            stats.put("totalStudents", totalStudents);
            stats.put("activeStudents", activeStudents);

            // 课程统计（使用模拟数据，待CourseService实现后替换）
            long totalCourses = 85; // 模拟数据
            stats.put("totalCourses", totalCourses);

            // 班级统计（使用模拟数据，待SchoolClassService实现后替换）
            long totalClasses = 15; // 模拟数据
            stats.put("totalClasses", totalClasses);

            // 收费统计（使用模拟数据，待PaymentService实现后替换）
            String monthlyRevenue = "¥235,680"; // 模拟数据
            stats.put("monthlyRevenue", monthlyRevenue);

            // 计算百分比增长（这里使用模拟数据，实际应该从历史数据计算）
            stats.put("userGrowthPercent", 15.6);
            stats.put("studentGrowthPercent", 8.3);
            stats.put("courseGrowthPercent", 12.1);
            stats.put("classGrowthPercent", 5.2);

            model.addAttribute("stats", stats);
            model.addAttribute("pageTitle", "仪表盘");
            model.addAttribute("currentPage", "dashboard");

            return "admin/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "加载仪表盘数据失败：" + e.getMessage());
            return "admin/dashboard";
        }
    }

    /**
     * 用户管理页面
     */
    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       Model model) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> users = userService.findAllUsers(pageable);

            model.addAttribute("users", users);
            model.addAttribute("pageTitle", "用户管理");
            model.addAttribute("currentPage", "users");

            return "admin/users";
        } catch (Exception e) {
            model.addAttribute("error", "加载用户列表失败：" + e.getMessage());
            return "admin/users";
        }
    }

    /**
     * 课程管理页面
     */
    @GetMapping("/courses")
    public String courses(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "20") int size,
                         Model model) {
        try {
            // 使用模拟数据，待CourseService完善后替换
            // Page<Course> courses = courseService.findAllCourses(PageRequest.of(page, size));

            // 获取课程统计（使用模拟数据）
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCourses", 85);
            stats.put("requiredCourses", 45);
            stats.put("electiveCourses", 30);
            stats.put("practicalCourses", 10);

            // model.addAttribute("courses", courses);
            model.addAttribute("stats", stats);
            model.addAttribute("pageTitle", "课程管理");
            model.addAttribute("currentPage", "courses");

            return "admin/courses";
        } catch (Exception e) {
            model.addAttribute("error", "加载课程列表失败：" + e.getMessage());
            return "admin/courses";
        }
    }

    /**
     * 学生管理页面
     */
    @GetMapping("/students")
    public String students(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "20") int size,
                          Model model) {
        try {
            // 使用模拟数据，待StudentService完善后替换
            // Pageable pageable = PageRequest.of(page, size);
            // Page<Student> students = studentService.findAllStudents(pageable);

            // 获取班级列表（使用模拟数据）
            List<SchoolClass> classes = new ArrayList<>(); // 模拟数据

            // 获取年级列表
            List<String> grades = List.of("2024级", "2023级", "2022级", "2021级");

            // 获取学生统计（使用模拟数据）
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalStudents", 450);
            stats.put("activeStudents", 420);
            stats.put("graduatedStudents", 20);
            stats.put("suspendedStudents", 10);

            // model.addAttribute("students", students);
            model.addAttribute("classes", classes);
            model.addAttribute("grades", grades);
            model.addAttribute("stats", stats);
            model.addAttribute("pageTitle", "学生管理");
            model.addAttribute("currentPage", "students");

            return "admin/students";
        } catch (Exception e) {
            model.addAttribute("error", "加载学生列表失败：" + e.getMessage());
            return "admin/students";
        }
    }

    /**
     * 学费管理页面
     */
    @GetMapping("/fees")
    public String fees(@RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "20") int size,
                      Model model) {
        try {
            // 获取财务统计（暂时使用模拟数据，后续实现FeeService时替换）
            Map<String, Object> stats = new HashMap<>();
            long totalStudents = 450; // 模拟数据

            // 模拟数据计算
            stats.put("monthlyRevenue", "¥235,680");
            stats.put("totalStudents", totalStudents);
            stats.put("paidStudents", Math.round(totalStudents * 0.85)); // 85%已缴费
            stats.put("unpaidStudents", Math.round(totalStudents * 0.12)); // 12%未缴费
            stats.put("overdueStudents", Math.round(totalStudents * 0.03)); // 3%逾期

            model.addAttribute("stats", stats);
            model.addAttribute("pageTitle", "学费管理");
            model.addAttribute("currentPage", "fees");

            return "admin/fees";
        } catch (Exception e) {
            model.addAttribute("error", "加载学费列表失败：" + e.getMessage());
            return "admin/fees";
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
            // 获取角色统计（使用模拟数据）
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalRoles", 5);
            stats.put("systemRoles", 3);
            stats.put("customRoles", 2);
            stats.put("activeRoles", 5);

            // 模拟角色列表
            List<Map<String, Object>> roles = new ArrayList<>();
            roles.add(Map.of("id", 1, "name", "ADMIN", "displayName", "系统管理员", "description", "拥有所有权限", "userCount", 2, "status", 1));
            roles.add(Map.of("id", 2, "name", "TEACHER", "displayName", "教师", "description", "教学管理权限", "userCount", 15, "status", 1));
            roles.add(Map.of("id", 3, "name", "STUDENT", "displayName", "学生", "description", "学生基础权限", "userCount", 450, "status", 1));
            roles.add(Map.of("id", 4, "name", "FINANCE", "displayName", "财务人员", "description", "财务管理权限", "userCount", 3, "status", 1));
            roles.add(Map.of("id", 5, "name", "ACADEMIC", "displayName", "教务人员", "description", "教务管理权限", "userCount", 5, "status", 1));

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
            // 获取权限统计（使用模拟数据）
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalPermissions", 25);
            stats.put("systemPermissions", 20);
            stats.put("modulePermissions", 5);
            stats.put("activePermissions", 25);

            // 模拟权限列表
            List<Map<String, Object>> permissions = new ArrayList<>();
            permissions.add(Map.of("id", 1, "code", "user:view", "name", "查看用户", "module", "用户管理", "description", "查看用户列表和详情"));
            permissions.add(Map.of("id", 2, "code", "user:create", "name", "创建用户", "module", "用户管理", "description", "创建新用户"));
            permissions.add(Map.of("id", 3, "code", "user:edit", "name", "编辑用户", "module", "用户管理", "description", "编辑用户信息"));
            permissions.add(Map.of("id", 4, "code", "user:delete", "name", "删除用户", "module", "用户管理", "description", "删除用户"));
            permissions.add(Map.of("id", 5, "code", "course:view", "name", "查看课程", "module", "课程管理", "description", "查看课程列表和详情"));

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

    /**
     * 班级管理页面
     */
    @GetMapping("/classes")
    public String classes(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "20") int size,
                         Model model) {
        try {
            // 获取班级统计（使用模拟数据）
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalClasses", 15);
            stats.put("activeClasses", 12);
            stats.put("graduatedClasses", 3);
            stats.put("totalStudents", 450);

            // 模拟班级列表
            List<Map<String, Object>> classes = new ArrayList<>();
            classes.add(Map.of("id", 1, "name", "计算机科学2024-1班", "grade", "2024级", "major", "计算机科学与技术", "studentCount", 35, "teacher", "张老师", "status", "在读"));
            classes.add(Map.of("id", 2, "name", "计算机科学2024-2班", "grade", "2024级", "major", "计算机科学与技术", "studentCount", 33, "teacher", "李老师", "status", "在读"));
            classes.add(Map.of("id", 3, "name", "软件工程2024-1班", "grade", "2024级", "major", "软件工程", "studentCount", 32, "teacher", "王老师", "status", "在读"));
            classes.add(Map.of("id", 4, "name", "信息安全2023-1班", "grade", "2023级", "major", "信息安全", "studentCount", 28, "teacher", "赵老师", "status", "在读"));

            model.addAttribute("classes", classes);
            model.addAttribute("stats", stats);
            model.addAttribute("pageTitle", "班级管理");
            model.addAttribute("currentPage", "classes");
            return "admin/classes";
        } catch (Exception e) {
            model.addAttribute("error", "加载班级列表失败：" + e.getMessage());
            return "admin/classes";
        }
    }

    /**
     * 课程安排页面
     */
    @GetMapping("/schedules")
    public String schedules(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size,
                           Model model) {
        try {
            // 获取课程安排统计（使用模拟数据）
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalSchedules", 120);
            stats.put("todaySchedules", 15);
            stats.put("weekSchedules", 85);
            stats.put("conflictSchedules", 2);

            // 模拟课程安排列表
            List<Map<String, Object>> schedules = new ArrayList<>();
            schedules.add(Map.of("id", 1, "courseName", "数据结构", "className", "计算机科学2024-1班", "teacher", "张老师",
                                "time", "周一 08:00-09:40", "classroom", "A101", "weeks", "1-16周", "status", "正常"));
            schedules.add(Map.of("id", 2, "courseName", "高等数学", "className", "计算机科学2024-1班", "teacher", "李老师",
                                "time", "周一 10:00-11:40", "classroom", "A102", "weeks", "1-18周", "status", "正常"));
            schedules.add(Map.of("id", 3, "courseName", "程序设计基础", "className", "计算机科学2024-2班", "teacher", "王老师",
                                "time", "周二 14:00-15:40", "classroom", "B201", "weeks", "1-16周", "status", "正常"));

            model.addAttribute("schedules", schedules);
            model.addAttribute("stats", stats);
            model.addAttribute("pageTitle", "课程安排");
            model.addAttribute("currentPage", "schedules");
            return "admin/schedules";
        } catch (Exception e) {
            model.addAttribute("error", "加载课程安排失败：" + e.getMessage());
            return "admin/schedules";
        }
    }

    /**
     * 缴费记录页面
     */
    @GetMapping("/payments")
    public String payments(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "20") int size,
                          Model model) {
        try {
            // 获取缴费记录统计（使用模拟数据）
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalPayments", 1250);
            stats.put("todayPayments", 15);
            stats.put("monthPayments", 380);
            stats.put("totalAmount", "¥1,235,600");

            // 模拟缴费记录列表
            List<Map<String, Object>> payments = new ArrayList<>();
            payments.add(Map.of("id", 1, "studentName", "张三", "studentId", "2024001", "className", "计算机科学2024-1班",
                               "feeType", "学费", "amount", 8000, "paymentDate", "2024-09-01", "paymentMethod", "银行转账", "status", "已缴费"));
            payments.add(Map.of("id", 2, "studentName", "李四", "studentId", "2024002", "className", "计算机科学2024-1班",
                               "feeType", "住宿费", "amount", 1200, "paymentDate", "2024-09-02", "paymentMethod", "支付宝", "status", "已缴费"));
            payments.add(Map.of("id", 3, "studentName", "王五", "studentId", "2024003", "className", "软件工程2024-1班",
                               "feeType", "学费", "amount", 8000, "paymentDate", "2024-09-03", "paymentMethod", "微信支付", "status", "已缴费"));

            model.addAttribute("payments", payments);
            model.addAttribute("stats", stats);
            model.addAttribute("pageTitle", "缴费记录");
            model.addAttribute("currentPage", "payments");
            return "admin/payments";
        } catch (Exception e) {
            model.addAttribute("error", "加载缴费记录失败：" + e.getMessage());
            return "admin/payments";
        }
    }

    /**
     * 财务报表页面
     */
    @GetMapping("/reports")
    public String reports(@RequestParam(defaultValue = "month") String type,
                         @RequestParam(defaultValue = "2024") String year,
                         @RequestParam(defaultValue = "12") String month,
                         Model model) {
        try {
            // 获取财务报表统计（使用模拟数据）
            Map<String, Object> stats = new HashMap<>();
            stats.put("monthlyRevenue", "¥235,680");
            stats.put("yearlyRevenue", "¥2,856,400");
            stats.put("unpaidAmount", "¥45,200");
            stats.put("refundAmount", "¥8,500");

            // 模拟月度收入数据
            List<Map<String, Object>> monthlyData = new ArrayList<>();
            for (int i = 1; i <= 12; i++) {
                monthlyData.add(Map.of("month", i + "月", "revenue", 200000 + (int)(Math.random() * 100000)));
            }

            // 模拟费用类型统计
            List<Map<String, Object>> feeTypeStats = new ArrayList<>();
            feeTypeStats.add(Map.of("type", "学费", "amount", 1800000, "percentage", 75));
            feeTypeStats.add(Map.of("type", "住宿费", "amount", 540000, "percentage", 22.5));
            feeTypeStats.add(Map.of("type", "其他费用", "amount", 60000, "percentage", 2.5));

            model.addAttribute("stats", stats);
            model.addAttribute("monthlyData", monthlyData);
            model.addAttribute("feeTypeStats", feeTypeStats);
            model.addAttribute("reportType", type);
            model.addAttribute("selectedYear", year);
            model.addAttribute("selectedMonth", month);
            model.addAttribute("pageTitle", "财务报表");
            model.addAttribute("currentPage", "reports");
            return "admin/reports";
        } catch (Exception e) {
            model.addAttribute("error", "加载财务报表失败：" + e.getMessage());
            return "admin/reports";
        }
    }

    /**
     * 个人资料页面
     */
    @GetMapping("/profile")
    public String profile(Model model) {
        try {
            // 模拟当前用户信息
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("id", 1);
            userProfile.put("username", "admin");
            userProfile.put("realName", "系统管理员");
            userProfile.put("email", "admin@campus.edu.cn");
            userProfile.put("phone", "13800138000");
            userProfile.put("department", "信息技术部");
            userProfile.put("position", "系统管理员");
            userProfile.put("lastLoginTime", "2024-12-04 12:30:00");
            userProfile.put("loginCount", 156);

            model.addAttribute("userProfile", userProfile);
            model.addAttribute("pageTitle", "个人资料");
            model.addAttribute("currentPage", "profile");
            return "admin/profile";
        } catch (Exception e) {
            model.addAttribute("error", "加载个人资料失败：" + e.getMessage());
            return "admin/profile";
        }
    }

    /**
     * 系统设置页面
     */
    @GetMapping("/settings")
    public String settings(Model model) {
        try {
            // 模拟系统设置信息
            Map<String, Object> systemSettings = new HashMap<>();
            systemSettings.put("systemName", "智慧校园管理系统");
            systemSettings.put("systemVersion", "1.0.0");
            systemSettings.put("systemLogo", "/images/logo.png");
            systemSettings.put("contactEmail", "support@campus.edu.cn");
            systemSettings.put("contactPhone", "400-123-4567");
            systemSettings.put("maxLoginAttempts", 5);
            systemSettings.put("sessionTimeout", 30);
            systemSettings.put("passwordMinLength", 6);
            systemSettings.put("enableCaptcha", true);
            systemSettings.put("enableEmailNotification", true);
            systemSettings.put("enableSmsNotification", false);

            model.addAttribute("systemSettings", systemSettings);
            model.addAttribute("pageTitle", "系统设置");
            model.addAttribute("currentPage", "settings");
            return "admin/settings";
        } catch (Exception e) {
            model.addAttribute("error", "加载系统设置失败：" + e.getMessage());
            return "admin/settings";
        }
    }

    // AJAX API 接口

    /**
     * 获取用户详情
     */
    @GetMapping("/api/users/{id}")
    @ResponseBody
    public ApiResponse<User> getUserDetail(@PathVariable Long id) {
        try {
            User user = userService.findUserById(id);
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error("获取用户详情失败：" + e.getMessage());
        }
    }

    /**
     * 重置用户密码
     */
    @PostMapping("/api/users/{id}/reset-password")
    @ResponseBody
    public ApiResponse<String> resetUserPassword(@PathVariable Long id) {
        try {
            userService.resetPassword(id);
            return ApiResponse.success("密码重置成功，新密码为：123456");
        } catch (Exception e) {
            return ApiResponse.error("密码重置失败：" + e.getMessage());
        }
    }

    /**
     * 切换用户状态
     */
    @PostMapping("/api/users/{id}/toggle-status")
    @ResponseBody
    public ApiResponse<String> toggleUserStatus(@PathVariable Long id) {
        try {
            userService.toggleUserStatus(id);
            return ApiResponse.success("状态切换成功");
        } catch (Exception e) {
            return ApiResponse.error("状态切换失败：" + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/api/users/{id}")
    @ResponseBody
    public ApiResponse<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ApiResponse.success("用户删除成功");
        } catch (Exception e) {
            return ApiResponse.error("用户删除失败：" + e.getMessage());
        }
    }

    /**
     * 获取课程详情（暂时返回错误，待CourseService完善）
     */
    @GetMapping("/api/courses/{id}")
    @ResponseBody
    public ApiResponse<Course> getCourseDetail(@PathVariable Long id) {
        return ApiResponse.error("课程管理功能开发中");
    }

    /**
     * 切换课程状态（暂时返回错误，待CourseService完善）
     */
    @PostMapping("/api/courses/{id}/toggle-status")
    @ResponseBody
    public ApiResponse<String> toggleCourseStatus(@PathVariable Long id) {
        return ApiResponse.error("课程管理功能开发中");
    }

    /**
     * 删除课程（暂时返回错误，待CourseService完善）
     */
    @DeleteMapping("/api/courses/{id}")
    @ResponseBody
    public ApiResponse<String> deleteCourse(@PathVariable Long id) {
        return ApiResponse.error("课程管理功能开发中");
    }

    /**
     * 获取学生详情（暂时返回错误，待StudentService完善）
     */
    @GetMapping("/api/students/{id}")
    @ResponseBody
    public ApiResponse<Student> getStudentDetail(@PathVariable Long id) {
        return ApiResponse.error("学生管理功能开发中");
    }

    /**
     * 更改学生状态（暂时返回错误，待StudentService完善）
     */
    @PostMapping("/api/students/{id}/status")
    @ResponseBody
    public ApiResponse<String> changeStudentStatus(@PathVariable Long id,
                                                   @RequestBody Map<String, String> request) {
        return ApiResponse.error("学生管理功能开发中");
    }

    /**
     * 删除学生（暂时返回错误，待StudentService完善）
     */
    @DeleteMapping("/api/students/{id}")
    @ResponseBody
    public ApiResponse<String> deleteStudent(@PathVariable Long id) {
        return ApiResponse.error("学生管理功能开发中");
    }

    /**
     * 导出用户Excel
     */
    @GetMapping("/api/users/export")
    public void exportUsers() {
   
    }

    /**
     * 导出课程Excel
     */
    @GetMapping("/api/courses/export")
    public void exportCourses() {
    
    }

    /**
     * 导出学生Excel
     */
    @GetMapping("/api/students/export")
    public void exportStudents() {
      
    }

    /**
     * 导出学费记录Excel
     */
    @GetMapping("/api/fees/export")
    public void exportFees() {
   
    }


}
