package com.campus.interfaces.rest.system;

import com.campus.application.service.auth.UserService;
import com.campus.application.service.academic.CourseService;
import com.campus.application.service.academic.StudentService;
import com.campus.application.service.finance.FinanceService;
import com.campus.application.service.academic.AttendanceService;
import com.campus.application.service.academic.GradeService;
import com.campus.domain.entity.auth.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一管理后台页面控制器
 * 处理所有admin页面的路由，消除重复控制器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Controller
@RequestMapping("/system/admin")  // 修改路径前缀
public class UnifiedAdminController {

    private static final Logger logger = LoggerFactory.getLogger(UnifiedAdminController.class);

    private final UserService userService;
    private final CourseService courseService;
    private final StudentService studentService;
    private final FinanceService financeService;
    private final AttendanceService attendanceService;
    private final GradeService gradeService;

    public UnifiedAdminController(UserService userService, 
                                 CourseService courseService,
                                 StudentService studentService,
                                 FinanceService financeService,
                                 AttendanceService attendanceService,
                                 GradeService gradeService) {
        this.userService = userService;
        this.courseService = courseService;
        this.studentService = studentService;
        this.financeService = financeService;
        this.attendanceService = attendanceService;
        this.gradeService = gradeService;
    }

    // ================================
    // 系统管理页面
    // ================================

    /**
     * 用户管理页面
     */
    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    public String users(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       @RequestParam(defaultValue = "") String search,
                       @RequestParam(defaultValue = "") String role,
                       @RequestParam(defaultValue = "") String status,
                       Model model, HttpServletRequest request) {
        try {
            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            if (!search.isEmpty()) params.put("search", search);
            if (!role.isEmpty()) params.put("role", role);
            if (!status.isEmpty()) params.put("status", status);

            // 分页查询用户
            Pageable pageable = PageRequest.of(page, size);
            var userPage = userService.findUsersByPage(pageable, params);

            // 获取统计信息
            var userStats = userService.getUserStatistics();

            model.addAttribute("users", userPage);
            model.addAttribute("stats", userStats);
            model.addAttribute("pageTitle", "用户管理");
            model.addAttribute("currentPage", "users");
            addCurrentUserToModel(model, request);

            return "admin/system/users";
        } catch (Exception e) {
            logger.error("加载用户管理页面失败", e);
            model.addAttribute("error", "加载用户管理页面失败：" + e.getMessage());
            return "admin/system/users";
        }
    }

    /**
     * 角色管理页面
     */
    @GetMapping("/roles")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    public String roles(Model model, HttpServletRequest request) {
        try {
            model.addAttribute("pageTitle", "角色管理");
            model.addAttribute("currentPage", "roles");
            addCurrentUserToModel(model, request);
            return "admin/system/roles";
        } catch (Exception e) {
            logger.error("加载角色管理页面失败", e);
            model.addAttribute("error", "加载角色管理页面失败：" + e.getMessage());
            return "admin/system/roles";
        }
    }



    /**
     * 系统设置页面
     */
    @GetMapping("/settings")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    public String settings(Model model, HttpServletRequest request) {
        try {
            model.addAttribute("pageTitle", "系统设置");
            model.addAttribute("currentPage", "settings");
            addCurrentUserToModel(model, request);
            return "admin/system/settings";
        } catch (Exception e) {
            logger.error("加载系统设置页面失败", e);
            model.addAttribute("error", "加载系统设置页面失败：" + e.getMessage());
            return "admin/system/settings";
        }
    }

    /**
     * 通知管理页面
     */
    @GetMapping("/notifications")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    public String notifications(Model model, HttpServletRequest request) {
        try {
            model.addAttribute("pageTitle", "通知管理");
            model.addAttribute("currentPage", "notifications");
            addCurrentUserToModel(model, request);
            return "admin/system/notifications";
        } catch (Exception e) {
            logger.error("加载通知管理页面失败", e);
            model.addAttribute("error", "加载通知管理页面失败：" + e.getMessage());
            return "admin/system/notifications";
        }
    }

    /**
     * 活动日志页面
     */
    @GetMapping("/activity-log")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')")
    public String activityLog(Model model, HttpServletRequest request) {
        try {
            model.addAttribute("pageTitle", "活动日志");
            model.addAttribute("currentPage", "activity-log");
            addCurrentUserToModel(model, request);
            return "admin/system/activity-log";
        } catch (Exception e) {
            logger.error("加载活动日志页面失败", e);
            model.addAttribute("error", "加载活动日志页面失败：" + e.getMessage());
            return "admin/system/activity-log";
        }
    }

    // ================================
    // 教务管理页面
    // ================================

    /**
     * 课程管理页面
     */
    @GetMapping("/courses")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_ACADEMIC_DIRECTOR')")
    public String courses(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "20") int size,
                         @RequestParam(defaultValue = "") String search,
                         Model model, HttpServletRequest request) {
        try {
            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            if (!search.isEmpty()) params.put("search", search);

            // 分页查询课程
            Pageable pageable = PageRequest.of(page, size);
            var coursePage = courseService.findCoursesByPage(pageable, params);

            model.addAttribute("courses", coursePage);
            model.addAttribute("pageTitle", "课程管理");
            model.addAttribute("currentPage", "courses");
            addCurrentUserToModel(model, request);

            return "admin/academic/courses";
        } catch (Exception e) {
            logger.error("加载课程管理页面失败", e);
            model.addAttribute("error", "加载课程管理页面失败：" + e.getMessage());
            return "admin/academic/courses";
        }
    }

    /**
     * 学生管理页面
     */
    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_ACADEMIC_DIRECTOR', 'ROLE_STUDENT_AFFAIRS_DIRECTOR')")
    public String students(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "20") int size,
                          @RequestParam(defaultValue = "") String search,
                          Model model, HttpServletRequest request) {
        try {
            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            if (!search.isEmpty()) params.put("search", search);

            // 分页查询学生
            Pageable pageable = PageRequest.of(page, size);
            var studentPage = studentService.findStudentsByPage(pageable, params);

            model.addAttribute("students", studentPage);
            model.addAttribute("pageTitle", "学生管理");
            model.addAttribute("currentPage", "students");
            addCurrentUserToModel(model, request);

            return "admin/students/index";
        } catch (Exception e) {
            logger.error("加载学生管理页面失败", e);
            model.addAttribute("error", "加载学生管理页面失败：" + e.getMessage());
            return "admin/students/index";
        }
    }

    /**
     * 考勤管理页面
     */
    @GetMapping("/attendance")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_ACADEMIC_DIRECTOR')")
    public String attendance(Model model, HttpServletRequest request) {
        try {
            // 获取考勤统计信息
            long totalAttendanceRecords = attendanceService.count();
            model.addAttribute("totalAttendanceRecords", totalAttendanceRecords);

            model.addAttribute("pageTitle", "考勤管理");
            model.addAttribute("currentPage", "attendance");
            addCurrentUserToModel(model, request);
            return "admin/attendance/index";
        } catch (Exception e) {
            logger.error("加载考勤管理页面失败", e);
            model.addAttribute("error", "加载考勤管理页面失败：" + e.getMessage());
            return "admin/attendance/index";
        }
    }

    /**
     * 成绩管理页面
     */
    @GetMapping("/grades")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', 'ROLE_ACADEMIC_DIRECTOR')")
    public String grades(Model model, HttpServletRequest request) {
        try {
            // 获取成绩统计信息
            long totalGradeRecords = gradeService.count();
            model.addAttribute("totalGradeRecords", totalGradeRecords);

            model.addAttribute("pageTitle", "成绩管理");
            model.addAttribute("currentPage", "grades");
            addCurrentUserToModel(model, request);
            return "admin/grades/index";
        } catch (Exception e) {
            logger.error("加载成绩管理页面失败", e);
            model.addAttribute("error", "加载成绩管理页面失败：" + e.getMessage());
            return "admin/grades/index";
        }
    }

    // ================================
    // 财务管理页面
    // ================================

    /**
     * 费用项目管理页面
     */
    @GetMapping("/fee-items")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_FINANCE_DIRECTOR')")
    public String feeItems(Model model, HttpServletRequest request) {
        try {
            // 获取财务统计信息
            System.out.println("加载财务统计信息: " + financeService.getClass().getSimpleName());
            model.addAttribute("financeServiceAvailable", true);

            model.addAttribute("pageTitle", "费用项目");
            model.addAttribute("currentPage", "fee-items");
            addCurrentUserToModel(model, request);
            return "admin/finance/fee-items";
        } catch (Exception e) {
            logger.error("加载费用项目页面失败", e);
            model.addAttribute("error", "加载费用项目页面失败：" + e.getMessage());
            return "admin/finance/fee-items";
        }
    }

    /**
     * 缴费管理页面
     */
    @GetMapping("/payments")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_FINANCE_DIRECTOR')")
    public String payments(Model model, HttpServletRequest request) {
        try {
            model.addAttribute("pageTitle", "缴费管理");
            model.addAttribute("currentPage", "payments");
            addCurrentUserToModel(model, request);
            return "admin/finance/payments";
        } catch (Exception e) {
            logger.error("加载缴费管理页面失败", e);
            model.addAttribute("error", "加载缴费管理页面失败：" + e.getMessage());
            return "admin/finance/payments";
        }
    }

    /**
     * 缴费记录页面
     */
    @GetMapping("/payment-records")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_FINANCE_DIRECTOR')")
    public String paymentRecords(Model model, HttpServletRequest request) {
        try {
            model.addAttribute("pageTitle", "缴费记录");
            model.addAttribute("currentPage", "payment-records");
            addCurrentUserToModel(model, request);
            return "admin/finance/payment-records";
        } catch (Exception e) {
            logger.error("加载缴费记录页面失败", e);
            model.addAttribute("error", "加载缴费记录页面失败：" + e.getMessage());
            return "admin/finance/payment-records";
        }
    }

    // ================================
    // 人事管理页面
    // ================================

    /**
     * 教师管理页面
     */
    @GetMapping("/teachers")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_HR_DIRECTOR')")
    public String teachers(Model model, HttpServletRequest request) {
        try {
            model.addAttribute("pageTitle", "教师管理");
            model.addAttribute("currentPage", "teachers");
            addCurrentUserToModel(model, request);
            return "admin/teachers/index";
        } catch (Exception e) {
            logger.error("加载教师管理页面失败", e);
            model.addAttribute("error", "加载教师管理页面失败：" + e.getMessage());
            return "admin/teachers/index";
        }
    }

    /**
     * 员工管理页面
     */
    @GetMapping("/staff")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_HR_DIRECTOR')")
    public String staff(Model model, HttpServletRequest request) {
        try {
            model.addAttribute("pageTitle", "员工管理");
            model.addAttribute("currentPage", "staff");
            addCurrentUserToModel(model, request);
            return "admin/staff/index";
        } catch (Exception e) {
            logger.error("加载员工管理页面失败", e);
            model.addAttribute("error", "加载员工管理页面失败：" + e.getMessage());
            return "admin/staff/index";
        }
    }

    /**
     * 部门管理页面
     */
    @GetMapping("/departments")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_HR_DIRECTOR')")
    public String departments(Model model, HttpServletRequest request) {
        try {
            model.addAttribute("pageTitle", "部门管理");
            model.addAttribute("currentPage", "departments");
            addCurrentUserToModel(model, request);
            return "admin/departments/index";
        } catch (Exception e) {
            logger.error("加载部门管理页面失败", e);
            model.addAttribute("error", "加载部门管理页面失败：" + e.getMessage());
            return "admin/departments/index";
        }
    }

    /**
     * 个人资料页面
     */
    @GetMapping("/profile")
    public String profile(Model model, HttpServletRequest request) {
        try {
            model.addAttribute("pageTitle", "个人资料");
            model.addAttribute("currentPage", "profile");
            addCurrentUserToModel(model, request);
            return "admin/profile";
        } catch (Exception e) {
            logger.error("加载个人资料页面失败", e);
            model.addAttribute("error", "加载个人资料页面失败：" + e.getMessage());
            return "admin/profile";
        }
    }

    /**
     * API测试页面
     */
    @GetMapping("/test-api")
    public String testApi(Model model, HttpServletRequest request) {
        try {
            model.addAttribute("pageTitle", "API测试");
            model.addAttribute("currentPage", "test-api");
            addCurrentUserToModel(model, request);
            return "test-api";
        } catch (Exception e) {
            logger.error("加载API测试页面失败", e);
            model.addAttribute("error", "加载API测试页面失败：" + e.getMessage());
            return "test-api";
        }
    }

    // ================================
    // 辅助方法
    // ================================

    /**
     * 添加当前用户信息到模型
     */
    private void addCurrentUserToModel(Model model, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                User currentUser = (User) session.getAttribute("user");
                if (currentUser == null) {
                    currentUser = (User) session.getAttribute("currentUser");
                }
                if (currentUser != null) {
                    model.addAttribute("currentUser", currentUser);
                    model.addAttribute("userRoles", userService.getUserRoles(currentUser.getId()));
                }
            }
        } catch (Exception e) {
            logger.warn("添加当前用户信息失败", e);
        }
    }
}
