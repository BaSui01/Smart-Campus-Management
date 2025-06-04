package com.campus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.campus.entity.User;
import com.campus.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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

    @Autowired
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
