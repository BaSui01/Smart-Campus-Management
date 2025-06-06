package com.campus.interfaces.rest.v1;

import com.campus.application.dto.DashboardStatsDTO;
import com.campus.application.service.DashboardService;
import com.campus.shared.common.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

/**
 * 仪表盘API控制器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "仪表盘API", description = "仪表盘数据管理REST API接口")
@SecurityRequirement(name = "Bearer")
public class DashboardApiController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/stats")
    @Operation(summary = "获取仪表盘统计数据", description = "获取仪表盘基础统计信息")
    @Cacheable(value = "dashboard:stats", unless = "#result == null")
    public ApiResponse<DashboardStatsDTO> getDashboardStats() {
        try {
            DashboardStatsDTO stats = dashboardService.getDashboardStats();
            return ApiResponse.success("获取仪表盘数据成功", stats);
        } catch (Exception e) {
            log.error("获取仪表盘统计数据失败", e);
            return ApiResponse.error(500, "获取仪表盘数据失败：" + e.getMessage());
        }
    }



    /**
     * 获取实时统计数据
     */
    @GetMapping("/realtime")
    @Operation(summary = "获取实时统计数据", description = "获取仪表盘实时更新数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'FINANCE_ADMIN', 'TEACHER')")
    public ApiResponse<DashboardStatsDTO> getRealTimeStats() {
        try {
            DashboardStatsDTO stats = dashboardService.getRealTimeStats();
            return ApiResponse.success("获取实时数据成功", stats);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取实时数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有图表数据
     */
    @GetMapping("/chart-data")
    @Operation(summary = "获取所有图表数据", description = "获取仪表盘所有图表数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'FINANCE_ADMIN', 'TEACHER')")
    @Cacheable(value = "dashboard:chart-data", unless = "#result == null")
    public ApiResponse<Map<String, Object>> getAllChartData() {
        try {
            DashboardStatsDTO stats = dashboardService.getDashboardStats();
            Map<String, Object> chartData = new HashMap<>();

            // 学生趋势数据
            chartData.put("studentTrendData", stats.getStudentTrendData());

            // 课程分布数据
            chartData.put("courseDistribution", stats.getCourseDistribution());

            // 年级分布数据
            chartData.put("gradeDistribution", stats.getGradeDistribution());

            // 收入趋势数据
            chartData.put("revenueTrendData", stats.getRevenueTrendData());

            // 专业分布数据
            chartData.put("majorDistribution", stats.getMajorDistribution());

            return ApiResponse.success("获取图表数据成功", chartData);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取图表数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取图表数据
     */
    @GetMapping("/charts/{type}")
    @Operation(summary = "获取图表数据", description = "根据类型获取特定图表数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'FINANCE_ADMIN', 'TEACHER')")
    @Cacheable(value = "dashboard:charts", key = "#type", unless = "#result == null")
    public ApiResponse<List<DashboardStatsDTO.ChartDataDTO>> getChartData(
            @Parameter(description = "图表类型") @PathVariable String type) {
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
                case "major-distribution":
                    chartData = stats.getMajorDistribution();
                    break;
                default:
                    return ApiResponse.error(400, "不支持的图表类型：" + type);
            }

            return ApiResponse.success("获取图表数据成功", chartData);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取图表数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取最近活动
     */
    @GetMapping("/activities")
    @Operation(summary = "获取最近活动", description = "获取系统最近活动记录")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'FINANCE_ADMIN', 'TEACHER')")
    @Cacheable(value = "dashboard:activities", unless = "#result == null")
    public ApiResponse<List<DashboardStatsDTO.RecentActivityDTO>> getRecentActivities() {
        try {
            DashboardStatsDTO stats = dashboardService.getDashboardStats();
            return ApiResponse.success("获取最近活动成功", stats.getRecentActivities());
        } catch (Exception e) {
            return ApiResponse.error(500, "获取最近活动失败：" + e.getMessage());
        }
    }

    /**
     * 获取系统通知
     */
    @GetMapping("/notifications")
    @Operation(summary = "获取系统通知", description = "获取系统通知信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'FINANCE_ADMIN', 'TEACHER')")
    @Cacheable(value = "dashboard:notifications", unless = "#result == null")
    public ApiResponse<List<DashboardStatsDTO.SystemNotificationDTO>> getSystemNotifications() {
        try {
            DashboardStatsDTO stats = dashboardService.getDashboardStats();
            return ApiResponse.success("获取系统通知成功", stats.getSystemNotifications());
        } catch (Exception e) {
            return ApiResponse.error(500, "获取系统通知失败：" + e.getMessage());
        }
    }

    /**
     * 获取快速统计
     */
    @GetMapping("/quick-stats")
    @Operation(summary = "获取快速统计", description = "获取今日快速统计数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'FINANCE_ADMIN', 'TEACHER')")
    @Cacheable(value = "dashboard:quick-stats", unless = "#result == null")
    public ApiResponse<DashboardStatsDTO.QuickStatsDTO> getQuickStats() {
        try {
            DashboardStatsDTO stats = dashboardService.getDashboardStats();
            return ApiResponse.success("获取快速统计成功", stats.getQuickStats());
        } catch (Exception e) {
            return ApiResponse.error(500, "获取快速统计失败：" + e.getMessage());
        }
    }

    /**
     * 清除仪表盘缓存
     */
    @PostMapping("/cache/clear")
    @Operation(summary = "清除仪表盘缓存", description = "清除所有仪表盘相关缓存")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    @CacheEvict(value = {"dashboard:stats", "dashboard:chart-data", "dashboard:charts",
                         "dashboard:activities", "dashboard:notifications", "dashboard:quick-stats"},
                allEntries = true)
    public ApiResponse<Void> clearDashboardCache() {
        try {
            return ApiResponse.success("仪表盘缓存已清除");
        } catch (Exception e) {
            return ApiResponse.error(500, "清除缓存失败：" + e.getMessage());
        }
    }

    /**
     * 刷新仪表盘数据
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新仪表盘数据", description = "清除缓存并重新获取数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'FINANCE_ADMIN', 'TEACHER')")
    @CacheEvict(value = {"dashboard:stats", "dashboard:chart-data", "dashboard:charts",
                         "dashboard:activities", "dashboard:notifications", "dashboard:quick-stats"},
                allEntries = true)
    public ApiResponse<DashboardStatsDTO> refreshDashboardData() {
        try {
            DashboardStatsDTO stats = dashboardService.getDashboardStats();
            return ApiResponse.success("仪表盘数据已刷新", stats);
        } catch (Exception e) {
            return ApiResponse.error(500, "刷新数据失败：" + e.getMessage());
        }
    }
}
