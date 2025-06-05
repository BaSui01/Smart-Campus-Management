package com.campus.service;

import com.campus.dto.DashboardStatsDTO;
import com.campus.dto.DashboardStatsDTO.ChartDataDTO;
import com.campus.dto.DashboardStatsDTO.RecentActivityDTO;
import com.campus.dto.DashboardStatsDTO.SystemNotificationDTO;
import com.campus.dto.DashboardStatsDTO.QuickStatsDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 仪表盘服务类
 */
@Service
public class DashboardService {
    
    private final Random random = new Random();
    
    /**
     * 获取仪表盘统计数据
     */
    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        // 基础统计数据
        stats.setTotalStudents(generateRandomNumber(1000, 2000));
        stats.setTotalCourses(generateRandomNumber(150, 300));
        stats.setTotalClasses(generateRandomNumber(40, 80));
        stats.setMonthlyRevenue(formatCurrency(generateRandomBigDecimal(200000, 500000)));
        stats.setTotalTeachers(generateRandomNumber(80, 150));
        stats.setTotalUsers(generateRandomNumber(200, 400));
        stats.setPendingPayments(generateRandomNumber(10, 50));
        stats.setActiveSchedules(generateRandomNumber(100, 200));
        
        // 趋势数据
        stats.setStudentTrendData(generateStudentTrendData());
        stats.setCourseTrendData(generateCourseTrendData());
        stats.setRevenueTrendData(generateRevenueTrendData());
        
        // 分布数据
        stats.setCourseDistribution(generateCourseDistribution());
        stats.setGradeDistribution(generateGradeDistribution());
        stats.setMajorDistribution(generateMajorDistribution());
        
        // 最近活动
        stats.setRecentActivities(generateRecentActivities());
        
        // 系统通知
        stats.setSystemNotifications(generateSystemNotifications());
        
        // 快速统计
        stats.setQuickStats(generateQuickStats());
        
        return stats;
    }
    
    /**
     * 生成学生趋势数据
     */
    private List<ChartDataDTO> generateStudentTrendData() {
        List<ChartDataDTO> data = new ArrayList<>();
        String[] months = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
        
        for (String month : months) {
            data.add(new ChartDataDTO(month, generateRandomNumber(10, 50)));
        }
        
        return data;
    }
    
    /**
     * 生成课程趋势数据
     */
    private List<ChartDataDTO> generateCourseTrendData() {
        List<ChartDataDTO> data = new ArrayList<>();
        String[] quarters = {"Q1", "Q2", "Q3", "Q4"};
        
        for (String quarter : quarters) {
            data.add(new ChartDataDTO(quarter, generateRandomNumber(20, 80)));
        }
        
        return data;
    }
    
    /**
     * 生成收入趋势数据
     */
    private List<ChartDataDTO> generateRevenueTrendData() {
        List<ChartDataDTO> data = new ArrayList<>();
        String[] months = {"1月", "2月", "3月", "4月", "5月", "6月"};
        
        for (String month : months) {
            data.add(new ChartDataDTO(month, generateRandomBigDecimal(50000, 100000)));
        }
        
        return data;
    }
    
    /**
     * 生成课程分布数据
     */
    private List<ChartDataDTO> generateCourseDistribution() {
        List<ChartDataDTO> data = new ArrayList<>();
        data.add(new ChartDataDTO("必修课", 60, "#4e73df"));
        data.add(new ChartDataDTO("选修课", 30, "#1cc88a"));
        data.add(new ChartDataDTO("实践课", 10, "#36b9cc"));
        return data;
    }
    
    /**
     * 生成年级分布数据
     */
    private List<ChartDataDTO> generateGradeDistribution() {
        List<ChartDataDTO> data = new ArrayList<>();
        data.add(new ChartDataDTO("2024级", generateRandomNumber(300, 500), "#4e73df"));
        data.add(new ChartDataDTO("2023级", generateRandomNumber(280, 480), "#1cc88a"));
        data.add(new ChartDataDTO("2022级", generateRandomNumber(250, 450), "#36b9cc"));
        data.add(new ChartDataDTO("2021级", generateRandomNumber(200, 400), "#f6c23e"));
        return data;
    }
    
    /**
     * 生成专业分布数据
     */
    private List<ChartDataDTO> generateMajorDistribution() {
        List<ChartDataDTO> data = new ArrayList<>();
        data.add(new ChartDataDTO("计算机科学与技术", generateRandomNumber(400, 600), "#4e73df"));
        data.add(new ChartDataDTO("软件工程", generateRandomNumber(300, 500), "#1cc88a"));
        data.add(new ChartDataDTO("数据科学与大数据", generateRandomNumber(200, 400), "#36b9cc"));
        data.add(new ChartDataDTO("人工智能", generateRandomNumber(150, 350), "#f6c23e"));
        data.add(new ChartDataDTO("网络工程", generateRandomNumber(100, 300), "#e74a3b"));
        return data;
    }
    
    /**
     * 生成最近活动数据
     */
    private List<RecentActivityDTO> generateRecentActivities() {
        List<RecentActivityDTO> activities = new ArrayList<>();
        
        activities.add(new RecentActivityDTO(
            "学生注册", "新学生张三完成注册", "系统管理员", 
            LocalDateTime.now().minusMinutes(30)
        ));
        
        activities.add(new RecentActivityDTO(
            "课程添加", "新增《数据结构》课程", "教务老师", 
            LocalDateTime.now().minusHours(1)
        ));
        
        activities.add(new RecentActivityDTO(
            "缴费记录", "学生李四完成学费缴纳", "财务老师", 
            LocalDateTime.now().minusHours(2)
        ));
        
        activities.add(new RecentActivityDTO(
            "班级创建", "创建计科2024-3班", "教务老师", 
            LocalDateTime.now().minusHours(3)
        ));
        
        activities.add(new RecentActivityDTO(
            "系统备份", "完成数据库备份", "系统管理员", 
            LocalDateTime.now().minusHours(6)
        ));
        
        return activities;
    }
    
    /**
     * 生成系统通知数据
     */
    private List<SystemNotificationDTO> generateSystemNotifications() {
        List<SystemNotificationDTO> notifications = new ArrayList<>();
        
        notifications.add(new SystemNotificationDTO(
            "数据备份提醒", "系统将在今晚2:00进行自动数据备份。", 
            "系统管理员", LocalDateTime.now().minusHours(3)
        ));
        
        notifications.add(new SystemNotificationDTO(
            "新学期开始", "2024春季学期即将开始，请检查课程安排。", 
            "教务处", LocalDateTime.now().minusDays(1)
        ));
        
        notifications.add(new SystemNotificationDTO(
            "系统更新完成", "智慧校园管理系统已更新至v2.1版本。", 
            "技术部", LocalDateTime.now().minusDays(3)
        ));
        
        notifications.add(new SystemNotificationDTO(
            "缴费提醒", "本月学费缴纳截止日期为月底，请及时提醒学生。", 
            "财务处", LocalDateTime.now().minusDays(5)
        ));
        
        notifications.add(new SystemNotificationDTO(
            "安全更新", "系统安全补丁已安装，请重启相关服务。", 
            "技术部", LocalDateTime.now().minusWeeks(1)
        ));
        
        return notifications;
    }
    
    /**
     * 生成快速统计数据
     */
    private QuickStatsDTO generateQuickStats() {
        QuickStatsDTO quickStats = new QuickStatsDTO();
        quickStats.setTodayNewStudents(generateRandomNumber(5, 20));
        quickStats.setTodayPayments(generateRandomNumber(10, 50));
        quickStats.setTodayRevenue(generateRandomBigDecimal(10000, 50000));
        quickStats.setOnlineUsers(generateRandomNumber(20, 100));
        quickStats.setSystemAlerts(generateRandomNumber(0, 5));
        return quickStats;
    }
    
    /**
     * 获取实时数据（用于AJAX刷新）
     */
    public DashboardStatsDTO getRealTimeStats() {
        // 这里可以实现实时数据获取逻辑
        // 目前返回模拟数据
        return getDashboardStats();
    }
    
    // 工具方法
    private Integer generateRandomNumber(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
    
    private BigDecimal generateRandomBigDecimal(double min, double max) {
        double randomValue = min + (max - min) * random.nextDouble();
        return BigDecimal.valueOf(randomValue).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    private String formatCurrency(BigDecimal amount) {
        return "¥" + String.format("%,.2f", amount);
    }
}
