package com.campus.interfaces.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.campus.application.service.UserService;
import com.campus.domain.entity.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页控制器
 * 处理网站首页和根路径的访问
 *
 * @author campus
 * @since 2025-06-04
 */
@Controller
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 根路径访问处理
     * 检查用户是否为系统管理员，决定跳转到后台主页还是登录页面
     */
    @GetMapping("/")
    public String index(HttpServletRequest request, Model model) {
        // 检查用户是否已登录
        HttpSession session = request.getSession(false);

        if (session != null) {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser != null) {
                // 检查用户是否为系统管理员
                try {
                    boolean isAdmin = userService.hasRole(currentUser.getId(), "ADMIN");
                    if (isAdmin) {
                        // 是管理员，跳转到后台主页
                        return "redirect:/admin/dashboard";
                    }
                } catch (Exception e) {
                    // 如果检查角色失败，记录错误但继续处理
                    model.addAttribute("error", "角色检查失败：" + e.getMessage());
                }
            }
        }

        // 不是管理员或未登录，重定向到登录页面
        return "redirect:/admin/login";
    }


    /**
     * API测试页面
     */
    @GetMapping("/admin/test-api")
    public String testApiPage(Model model) {
        try {
            // 构建API统计数据
            List<Map<String, Object>> apiStats = new ArrayList<>();
            apiStats.add(Map.of(
                "title", "总接口数",
                "value", "82",
                "icon", "fas fa-code",
                "color", "primary"
            ));
            apiStats.add(Map.of(
                "title", "业务接口",
                "value", "67",
                "icon", "fas fa-cogs",
                "color", "success"
            ));
            apiStats.add(Map.of(
                "title", "认证接口",
                "value", "11",
                "icon", "fas fa-shield-alt",
                "color", "warning"
            ));
            apiStats.add(Map.of(
                "title", "健康检查",
                "value", "4",
                "icon", "fas fa-heartbeat",
                "color", "info"
            ));

            // 构建完整的API列表数据
            List<Map<String, Object>> testApiList = new ArrayList<>();

            // 测试接口
            testApiList.add(Map.of("id", 1, "method", "GET", "url", "/api/v1/test/hello", "description", "Hello接口", "category", "测试接口", "status", "正常"));
            testApiList.add(Map.of("id", 2, "method", "GET", "url", "/api/v1/test/ping", "description", "Ping接口", "category", "测试接口", "status", "正常"));
            testApiList.add(Map.of("id", 3, "method", "GET", "url", "/api/v1/test/status", "description", "系统状态接口", "category", "测试接口", "status", "正常"));
            testApiList.add(Map.of("id", 4, "method", "GET", "url", "/api/v1/test/time", "description", "服务器时间接口", "category", "测试接口", "status", "正常"));

            // 仪表盘API
            testApiList.add(Map.of("id", 5, "method", "GET", "url", "/api/v1/dashboard/stats", "description", "获取仪表盘统计数据", "category", "仪表盘API", "status", "正常"));
            testApiList.add(Map.of("id", 6, "method", "GET", "url", "/api/v1/dashboard/activities", "description", "获取最近活动", "category", "仪表盘API", "status", "正常"));
            testApiList.add(Map.of("id", 7, "method", "GET", "url", "/api/v1/dashboard/realtime", "description", "获取实时统计数据", "category", "仪表盘API", "status", "正常"));
            testApiList.add(Map.of("id", 8, "method", "GET", "url", "/api/v1/dashboard/quick-stats", "description", "获取快速统计", "category", "仪表盘API", "status", "正常"));
            testApiList.add(Map.of("id", 9, "method", "GET", "url", "/api/v1/dashboard/notifications", "description", "获取系统通知", "category", "仪表盘API", "status", "正常"));
            testApiList.add(Map.of("id", 10, "method", "GET", "url", "/api/v1/dashboard/charts/student", "description", "获取学生图表数据", "category", "仪表盘API", "status", "正常"));

            // 学生管理API
            testApiList.add(Map.of("id", 11, "method", "GET", "url", "/api/v1/students", "description", "获取学生列表", "category", "学生管理API", "status", "正常"));
            testApiList.add(Map.of("id", 12, "method", "GET", "url", "/api/v1/students/1", "description", "获取学生详情", "category", "学生管理API", "status", "正常"));
            testApiList.add(Map.of("id", 13, "method", "GET", "url", "/api/v1/students/form-data", "description", "获取学生表单数据", "category", "学生管理API", "status", "正常"));
            testApiList.add(Map.of("id", 14, "method", "POST", "url", "/api/v1/students", "description", "创建学生", "category", "学生管理API", "status", "正常"));

            // 班级管理API
            testApiList.add(Map.of("id", 15, "method", "GET", "url", "/api/v1/classes", "description", "获取班级列表", "category", "班级管理API", "status", "正常"));
            testApiList.add(Map.of("id", 16, "method", "GET", "url", "/api/v1/classes/1", "description", "获取班级详情", "category", "班级管理API", "status", "正常"));
            testApiList.add(Map.of("id", 17, "method", "GET", "url", "/api/v1/classes/form-data", "description", "获取班级表单数据", "category", "班级管理API", "status", "正常"));
            testApiList.add(Map.of("id", 18, "method", "GET", "url", "/api/v1/classes/stats/grade", "description", "统计班级数量按年级", "category", "班级管理API", "status", "正常"));
            testApiList.add(Map.of("id", 19, "method", "GET", "url", "/api/v1/classes/grade/2024", "description", "根据年级查询班级列表", "category", "班级管理API", "status", "正常"));
            testApiList.add(Map.of("id", 20, "method", "POST", "url", "/api/v1/classes", "description", "创建班级", "category", "班级管理API", "status", "正常"));
            testApiList.add(Map.of("id", 21, "method", "PUT", "url", "/api/v1/classes/1", "description", "更新班级信息", "category", "班级管理API", "status", "正常"));
            testApiList.add(Map.of("id", 22, "method", "DELETE", "url", "/api/v1/classes/1", "description", "删除班级", "category", "班级管理API", "status", "正常"));

            // 课程管理API
            testApiList.add(Map.of("id", 23, "method", "GET", "url", "/api/v1/courses", "description", "获取课程列表", "category", "课程管理API", "status", "正常"));
            testApiList.add(Map.of("id", 24, "method", "GET", "url", "/api/v1/courses/1", "description", "获取课程详情", "category", "课程管理API", "status", "正常"));
            testApiList.add(Map.of("id", 25, "method", "GET", "url", "/api/v1/courses/form-data", "description", "获取课程表单数据", "category", "课程管理API", "status", "正常"));
            testApiList.add(Map.of("id", 26, "method", "GET", "url", "/api/v1/courses/semester/2024-1", "description", "根据学期查询课程列表", "category", "课程管理API", "status", "正常"));
            testApiList.add(Map.of("id", 27, "method", "GET", "url", "/api/v1/courses/stats/type", "description", "统计课程数量按类型", "category", "课程管理API", "status", "正常"));
            testApiList.add(Map.of("id", 28, "method", "POST", "url", "/api/v1/courses", "description", "创建课程", "category", "课程管理API", "status", "正常"));
            testApiList.add(Map.of("id", 29, "method", "PUT", "url", "/api/v1/courses/1", "description", "更新课程信息", "category", "课程管理API", "status", "正常"));
            testApiList.add(Map.of("id", 30, "method", "DELETE", "url", "/api/v1/courses/1", "description", "删除课程", "category", "课程管理API", "status", "正常"));

            // 成绩管理API
            testApiList.add(Map.of("id", 31, "method", "GET", "url", "/api/v1/grades", "description", "获取成绩列表", "category", "成绩管理API", "status", "正常"));
            testApiList.add(Map.of("id", 32, "method", "GET", "url", "/api/v1/grades/1", "description", "获取成绩详情", "category", "成绩管理API", "status", "正常"));
            testApiList.add(Map.of("id", 33, "method", "GET", "url", "/api/v1/grades/student/1", "description", "根据学生ID查询成绩", "category", "成绩管理API", "status", "正常"));
            testApiList.add(Map.of("id", 34, "method", "GET", "url", "/api/v1/grades/student/1/stats", "description", "获取学生成绩统计", "category", "成绩管理API", "status", "正常"));
            testApiList.add(Map.of("id", 35, "method", "GET", "url", "/api/v1/grades/semester/2024-1", "description", "根据学期查询成绩", "category", "成绩管理API", "status", "正常"));
            testApiList.add(Map.of("id", 36, "method", "POST", "url", "/api/v1/grades", "description", "创建成绩记录", "category", "成绩管理API", "status", "正常"));
            testApiList.add(Map.of("id", 37, "method", "PUT", "url", "/api/v1/grades/1", "description", "更新成绩记录", "category", "成绩管理API", "status", "正常"));
            testApiList.add(Map.of("id", 38, "method", "DELETE", "url", "/api/v1/grades/1", "description", "删除成绩记录", "category", "成绩管理API", "status", "正常"));

            // 缴费管理API
            testApiList.add(Map.of("id", 39, "method", "GET", "url", "/api/v1/payments/records", "description", "获取缴费记录列表", "category", "缴费管理API", "status", "正常"));
            testApiList.add(Map.of("id", 40, "method", "GET", "url", "/api/v1/payments/records/1", "description", "获取缴费记录详情", "category", "缴费管理API", "status", "正常"));
            testApiList.add(Map.of("id", 41, "method", "GET", "url", "/api/v1/payments/stats", "description", "获取缴费统计信息", "category", "缴费管理API", "status", "正常"));
            testApiList.add(Map.of("id", 42, "method", "GET", "url", "/api/v1/payments/records/student/1", "description", "根据学生ID查询缴费记录", "category", "缴费管理API", "status", "正常"));
            testApiList.add(Map.of("id", 43, "method", "POST", "url", "/api/v1/payments/records", "description", "创建缴费记录", "category", "缴费管理API", "status", "正常"));
            testApiList.add(Map.of("id", 44, "method", "PUT", "url", "/api/v1/payments/records/1", "description", "更新缴费记录", "category", "缴费管理API", "status", "正常"));
            testApiList.add(Map.of("id", 45, "method", "DELETE", "url", "/api/v1/payments/records/1", "description", "删除缴费记录", "category", "缴费管理API", "status", "正常"));

            // 缴费项目管理API
            testApiList.add(Map.of("id", 46, "method", "GET", "url", "/api/v1/fee-items", "description", "分页查询缴费项目", "category", "缴费项目API", "status", "正常"));
            testApiList.add(Map.of("id", 47, "method", "GET", "url", "/api/v1/fee-items/1", "description", "查询缴费项目", "category", "缴费项目API", "status", "正常"));
            testApiList.add(Map.of("id", 48, "method", "GET", "url", "/api/v1/fee-items/statistics", "description", "获取统计信息", "category", "缴费项目API", "status", "正常"));
            testApiList.add(Map.of("id", 49, "method", "GET", "url", "/api/v1/fee-items/active", "description", "查询启用项目", "category", "缴费项目API", "status", "正常"));
            testApiList.add(Map.of("id", 50, "method", "POST", "url", "/api/v1/fee-items", "description", "创建缴费项目", "category", "缴费项目API", "status", "正常"));
            testApiList.add(Map.of("id", 51, "method", "PUT", "url", "/api/v1/fee-items/1", "description", "更新缴费项目", "category", "缴费项目API", "status", "正常"));
            testApiList.add(Map.of("id", 52, "method", "DELETE", "url", "/api/v1/fee-items/1", "description", "删除缴费项目", "category", "缴费项目API", "status", "正常"));

            // 用户管理API
            testApiList.add(Map.of("id", 53, "method", "GET", "url", "/api/v1/users", "description", "获取用户列表", "category", "用户管理API", "status", "正常"));
            testApiList.add(Map.of("id", 54, "method", "GET", "url", "/api/v1/users/1", "description", "获取用户详情", "category", "用户管理API", "status", "正常"));
            testApiList.add(Map.of("id", 55, "method", "GET", "url", "/api/v1/users/stats", "description", "获取用户统计信息", "category", "用户管理API", "status", "正常"));
            testApiList.add(Map.of("id", 56, "method", "GET", "url", "/api/v1/users/search", "description", "搜索用户", "category", "用户管理API", "status", "正常"));
            testApiList.add(Map.of("id", 57, "method", "POST", "url", "/api/v1/users", "description", "创建用户", "category", "用户管理API", "status", "正常"));
            testApiList.add(Map.of("id", 58, "method", "PUT", "url", "/api/v1/users/1", "description", "更新用户信息", "category", "用户管理API", "status", "正常"));
            testApiList.add(Map.of("id", 59, "method", "DELETE", "url", "/api/v1/users/1", "description", "删除用户", "category", "用户管理API", "status", "正常"));
            testApiList.add(Map.of("id", 60, "method", "POST", "url", "/api/v1/users/1/toggle-status", "description", "启用/禁用用户", "category", "用户管理API", "status", "正常"));

            // 认证API
            testApiList.add(Map.of("id", 61, "method", "POST", "url", "/api/v1/auth/login", "description", "用户登录", "category", "认证API", "status", "正常"));
            testApiList.add(Map.of("id", 62, "method", "POST", "url", "/api/v1/auth/logout", "description", "用户登出", "category", "认证API", "status", "正常"));
            testApiList.add(Map.of("id", 63, "method", "POST", "url", "/api/v1/auth/register", "description", "用户注册", "category", "认证API", "status", "正常"));
            testApiList.add(Map.of("id", 64, "method", "POST", "url", "/api/v1/auth/refresh", "description", "刷新令牌", "category", "认证API", "status", "正常"));
            testApiList.add(Map.of("id", 65, "method", "GET", "url", "/api/v1/auth/me", "description", "获取当前用户信息", "category", "认证API", "status", "正常"));
            testApiList.add(Map.of("id", 66, "method", "POST", "url", "/api/v1/auth/change-password", "description", "修改密码", "category", "认证API", "status", "正常"));

            // 系统管理API
            testApiList.add(Map.of("id", 67, "method", "GET", "url", "/api/v1/system/status", "description", "获取系统状态", "category", "系统管理API", "status", "正常"));
            testApiList.add(Map.of("id", 68, "method", "POST", "url", "/api/v1/system/profile/update", "description", "更新个人资料", "category", "系统管理API", "status", "正常"));
            testApiList.add(Map.of("id", 69, "method", "POST", "url", "/api/v1/system/profile/change-password", "description", "修改密码", "category", "系统管理API", "status", "正常"));
            testApiList.add(Map.of("id", 70, "method", "POST", "url", "/api/v1/system/clear-cache", "description", "清理系统缓存", "category", "系统管理API", "status", "正常"));
            testApiList.add(Map.of("id", 71, "method", "POST", "url", "/api/v1/system/backup", "description", "创建系统备份", "category", "系统管理API", "status", "正常"));

            // Auth Controller
            testApiList.add(Map.of("id", 72, "method", "GET", "url", "/admin/token-status", "description", "检查令牌状态", "category", "Auth Controller", "status", "正常"));
            testApiList.add(Map.of("id", 73, "method", "GET", "url", "/admin/current-user", "description", "获取当前用户", "category", "Auth Controller", "status", "正常"));
            testApiList.add(Map.of("id", 74, "method", "GET", "url", "/admin/check-login", "description", "检查登录状态", "category", "Auth Controller", "status", "正常"));
            testApiList.add(Map.of("id", 75, "method", "GET", "url", "/admin/api/v1/version", "description", "获取API版本", "category", "Auth Controller", "status", "正常"));
            testApiList.add(Map.of("id", 76, "method", "GET", "url", "/admin/api/v1/health", "description", "健康检查", "category", "Auth Controller", "status", "正常"));
            testApiList.add(Map.of("id", 77, "method", "GET", "url", "/admin/api/v1/database", "description", "数据库状态", "category", "Auth Controller", "status", "正常"));
            testApiList.add(Map.of("id", 78, "method", "POST", "url", "/admin/refresh-token", "description", "刷新令牌", "category", "Auth Controller", "status", "正常"));

            // Health Controller
            testApiList.add(Map.of("id", 79, "method", "GET", "url", "/health", "description", "系统健康检查", "category", "Health Controller", "status", "正常"));
            testApiList.add(Map.of("id", 80, "method", "GET", "url", "/health/users", "description", "用户服务健康检查", "category", "Health Controller", "status", "正常"));
            testApiList.add(Map.of("id", 81, "method", "GET", "url", "/health/db", "description", "数据库健康检查", "category", "Health Controller", "status", "正常"));
            testApiList.add(Map.of("id", 82, "method", "GET", "url", "/health/db-direct", "description", "数据库直连检查", "category", "Health Controller", "status", "正常"));

            // 创建模拟的Page对象
            Map<String, Object> testApis = new HashMap<>();
            testApis.put("content", testApiList);
            testApis.put("totalElements", testApiList.size());
            testApis.put("empty", testApiList.isEmpty());
            testApis.put("totalPages", 1);
            testApis.put("number", 0);
            testApis.put("size", testApiList.size());
            testApis.put("numberOfElements", testApiList.size());
            testApis.put("first", true);
            testApis.put("last", true);

            // 构建表格配置
            Map<String, Object> apiTableConfig = new HashMap<>();
            apiTableConfig.put("title", "API接口列表");
            apiTableConfig.put("icon", "fas fa-code");

            // 表格列配置
            List<Map<String, Object>> columns = new ArrayList<>();
            columns.add(Map.of("field", "method", "title", "方法", "class", "text-center"));
            columns.add(Map.of("field", "url", "title", "接口地址", "class", ""));
            columns.add(Map.of("field", "description", "title", "描述", "class", ""));
            columns.add(Map.of("field", "category", "title", "分类", "class", ""));
            columns.add(Map.of("field", "status", "title", "状态", "class", "text-center"));
            apiTableConfig.put("columns", columns);

            // 操作按钮配置
            List<Map<String, Object>> actions = new ArrayList<>();
            actions.add(Map.of("function", "testSingleApi", "title", "测试", "icon", "fas fa-play", "type", "primary"));
            actions.add(Map.of("function", "copyApiUrl", "title", "复制", "icon", "fas fa-copy", "type", "info"));
            apiTableConfig.put("actions", actions);

            model.addAttribute("apiStats", apiStats);
            model.addAttribute("testApis", testApis);
            model.addAttribute("apiTableConfig", apiTableConfig);
            model.addAttribute("pageTitle", "API测试页面");
            model.addAttribute("currentPage", "test-api");
            return "test-api";
        } catch (Exception e) {
            model.addAttribute("error", "加载API测试页面失败：" + e.getMessage());
            return "test-api";
        }
    }

    /**
     * 关于我们页面
     */
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "关于我们 - 智慧校园管理系统");
        return "about";
    }

    /**
     * 联系我们页面
     */
    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("pageTitle", "联系我们 - 智慧校园管理系统");
        return "contact";
    }

    /**
     * 帮助页面
     */
    @GetMapping("/help")
    public String help(Model model) {
        model.addAttribute("pageTitle", "帮助中心 - 智慧校园管理系统");
        return "help";
    }

    /**
     * 隐私政策页面
     */
    @GetMapping("/privacy")
    public String privacy(Model model) {
        model.addAttribute("pageTitle", "隐私政策 - 智慧校园管理系统");
        return "privacy";
    }

    /**
     * 服务条款页面
     */
    @GetMapping("/terms")
    public String terms(Model model) {
        model.addAttribute("pageTitle", "服务条款 - 智慧校园管理系统");
        return "terms";
    }
}
