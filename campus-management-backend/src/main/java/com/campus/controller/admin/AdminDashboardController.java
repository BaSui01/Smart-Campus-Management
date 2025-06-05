package com.campus.controller.admin;

import com.campus.common.ApiResponse;
import com.campus.dto.DashboardStatsDTO;
import com.campus.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
     * 获取仪表盘实时数据 API
     */
    @GetMapping("/api/dashboard/stats")
    @ResponseBody
    public ApiResponse<DashboardStatsDTO> getDashboardStats() {
        try {
            DashboardStatsDTO stats = dashboardService.getRealTimeStats();
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.error("获取仪表盘数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取图表数据 API
     */
    @GetMapping("/api/dashboard/charts/{type}")
    @ResponseBody
    public ApiResponse<List<DashboardStatsDTO.ChartDataDTO>> getChartData(@PathVariable String type) {
        try {
            DashboardStatsDTO stats = dashboardService.getDashboardStats();
            List<DashboardStatsDTO.ChartDataDTO> chartData = new ArrayList<>();

            switch (type) {
                case "student-trend":
                    chartData = stats.getStudentTrendData();
                    break;
                case "course-distribution":
                    chartData = stats.getCourseDistribution();
                    break;
                case "grade-distribution":
                    chartData = stats.getGradeDistribution();
                    break;
                case "revenue-trend":
                    chartData = stats.getRevenueTrendData();
                    break;
                default:
                    return ApiResponse.error("不支持的图表类型：" + type);
            }

            return ApiResponse.success(chartData);
        } catch (Exception e) {
            return ApiResponse.error("获取图表数据失败：" + e.getMessage());
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
