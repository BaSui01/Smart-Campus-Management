package com.campus.interfaces.web;

import com.campus.application.dto.DashboardStatsDTO;
import com.campus.application.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 管理后台仪表盘控制器
 * 处理仪表盘相关的页面和API请求
 *
 * @author campus
 * @since 2025-06-05
 */
@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public AdminDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * 仪表盘页面
     */
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        try {
            // 获取仪表盘统计数据
            DashboardStatsDTO dashboardStats = dashboardService.getDashboardStats();

            model.addAttribute("stats", dashboardStats);
            model.addAttribute("pageTitle", "仪表盘");
            model.addAttribute("currentPage", "dashboard");
            return "admin/dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "加载仪表盘数据失败：" + e.getMessage());
            return "admin/dashboard";
        }
    }



    /**
     * 侧边栏测试页面
     */
    @GetMapping("/sidebar-test")
    public String sidebarTest(Model model) {
        model.addAttribute("pageTitle", "侧边栏测试");
        model.addAttribute("currentPage", "sidebar-test");
        return "admin/sidebar-test";
    }
}
