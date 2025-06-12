package com.campus.application.service;

import com.campus.application.Implement.system.DashboardServiceImpl;
import com.campus.application.service.academic.CourseService;
import com.campus.application.service.academic.CourseScheduleService;
import com.campus.application.dto.DashboardStatsDTO;
import com.campus.application.service.auth.UserService;
import com.campus.application.service.finance.PaymentRecordService;
import com.campus.application.service.organization.SchoolClassService;
import com.campus.application.service.academic.StudentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Dashboard服务单元测试
 * 测试仪表盘数据获取和处理功能
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private StudentService studentService;

    @Mock
    private CourseService courseService;

    @Mock
    private SchoolClassService schoolClassService;

    @Mock
    private UserService userService;

    @Mock
    private PaymentRecordService paymentRecordService;

    @Mock
    private CourseScheduleService courseScheduleService;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private UserService.UserStatistics mockUserStats;
    private PaymentRecordService.PaymentStatistics mockPaymentStats;

    @BeforeEach
    void setUp() {
        // 创建Mock统计数据
        mockUserStats = new UserService.UserStatistics(100L, 80L, 20L, 5L);
        
        mockPaymentStats = new PaymentRecordService.PaymentStatistics(
            200L, 180L, 20L, 0L,
            BigDecimal.valueOf(50000), BigDecimal.valueOf(45000), BigDecimal.valueOf(5000)
        );
    }

    @Test
    void testGetDashboardStats() {
        // 模拟各个服务的返回值
        when(studentService.count()).thenReturn(500L);
        when(courseService.count()).thenReturn(50L);
        when(schoolClassService.count()).thenReturn(20L);
        when(userService.getUserStatistics()).thenReturn(mockUserStats);
        when(courseScheduleService.count()).thenReturn(100L);
        when(paymentRecordService.getStatistics()).thenReturn(mockPaymentStats);

        // 执行测试
        DashboardStatsDTO result = dashboardService.getDashboardStats();

        // 验证基础统计数据
        assertThat(result).isNotNull();
        assertThat(result.getTotalStudents()).isEqualTo(500);
        assertThat(result.getTotalCourses()).isEqualTo(50);
        assertThat(result.getTotalClasses()).isEqualTo(20);
        assertThat(result.getTotalUsers()).isEqualTo(100);
        assertThat(result.getActiveSchedules()).isEqualTo(100);
        assertThat(result.getMonthlyRevenue()).isEqualTo("¥45,000.00");
        assertThat(result.getPendingPayments()).isEqualTo(20);

        // 验证图表数据不为空
        assertThat(result.getStudentTrendData()).isNotNull().isNotEmpty();
        assertThat(result.getCourseTrendData()).isNotNull().isNotEmpty();
        assertThat(result.getRevenueTrendData()).isNotNull().isNotEmpty();
        assertThat(result.getCourseDistribution()).isNotNull().isNotEmpty();
        assertThat(result.getGradeDistribution()).isNotNull().isNotEmpty();
        assertThat(result.getMajorDistribution()).isNotNull().isNotEmpty();

        // 验证活动和通知数据
        assertThat(result.getRecentActivities()).isNotNull().isNotEmpty();
        assertThat(result.getSystemNotifications()).isNotNull().isNotEmpty();
        assertThat(result.getQuickStats()).isNotNull();
    }

    @Test
    void testGetDashboardStatsWithNullServices() {
        // 测试当某些服务为null时的处理
        when(studentService.count()).thenReturn(0L);
        when(courseService.count()).thenReturn(0L);
        when(schoolClassService.count()).thenReturn(0L);
        when(userService.getUserStatistics()).thenReturn(new UserService.UserStatistics(0L, 0L, 0L, 0L));
        when(courseScheduleService.count()).thenReturn(0L);
        when(paymentRecordService.getStatistics()).thenReturn(
            new PaymentRecordService.PaymentStatistics(0L, 0L, 0L, 0L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)
        );

        // 执行测试
        DashboardStatsDTO result = dashboardService.getDashboardStats();

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result.getTotalStudents()).isEqualTo(0);
        assertThat(result.getTotalCourses()).isEqualTo(0);
        assertThat(result.getTotalClasses()).isEqualTo(0);
        assertThat(result.getMonthlyRevenue()).isEqualTo("¥0.00");
    }

    @Test
    void testGetDashboardStatsWithServiceException() {
        // 测试当服务抛出异常时的处理
        when(studentService.count()).thenThrow(new RuntimeException("Database error"));
        when(courseService.count()).thenThrow(new RuntimeException("Database error"));

        // 执行测试
        DashboardStatsDTO result = dashboardService.getDashboardStats();

        // 验证异常处理 - 应该返回默认值而不是抛出异常
        assertThat(result).isNotNull();
        assertThat(result.getTotalStudents()).isEqualTo(0);
        assertThat(result.getTotalCourses()).isEqualTo(0);
        assertThat(result.getMonthlyRevenue()).isEqualTo("¥0.00");
    }

    @Test
    void testGetRealTimeStats() {
        // 模拟服务返回值
        when(studentService.count()).thenReturn(500L);
        when(courseService.count()).thenReturn(50L);
        when(schoolClassService.count()).thenReturn(20L);
        when(userService.getUserStatistics()).thenReturn(mockUserStats);
        when(courseScheduleService.count()).thenReturn(100L);
        when(paymentRecordService.getStatistics()).thenReturn(mockPaymentStats);

        // 执行测试
        DashboardStatsDTO result = dashboardService.getRealTimeStats();

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result.getTotalStudents()).isEqualTo(500);
        assertThat(result.getTotalCourses()).isEqualTo(50);
    }

    @Test
    void testStudentTrendDataGeneration() {
        // 模拟学生服务
        when(studentService.count()).thenReturn(120L);

        // 执行测试
        DashboardStatsDTO result = dashboardService.getDashboardStats();

        // 验证学生趋势数据
        assertThat(result.getStudentTrendData()).hasSize(12); // 12个月
        assertThat(result.getStudentTrendData().get(0).getLabel()).isEqualTo("1月");
        assertThat(result.getStudentTrendData().get(0).getValue()).isInstanceOf(Integer.class);
    }

    @Test
    void testCourseTrendDataGeneration() {
        // 模拟课程服务
        when(courseService.count()).thenReturn(40L);

        // 执行测试
        DashboardStatsDTO result = dashboardService.getDashboardStats();

        // 验证课程趋势数据
        assertThat(result.getCourseTrendData()).hasSize(4); // 4个季度
        assertThat(result.getCourseTrendData().get(0).getLabel()).isEqualTo("Q1");
        assertThat(result.getCourseTrendData().get(0).getValue()).isInstanceOf(Integer.class);
    }

    @Test
    void testRevenueTrendDataGeneration() {
        // 模拟缴费服务
        when(paymentRecordService.getStatistics()).thenReturn(mockPaymentStats);

        // 执行测试
        DashboardStatsDTO result = dashboardService.getDashboardStats();

        // 验证收入趋势数据
        assertThat(result.getRevenueTrendData()).hasSize(6); // 6个月
        assertThat(result.getRevenueTrendData().get(0).getLabel()).isEqualTo("1月");
        assertThat(result.getRevenueTrendData().get(0).getValue()).isInstanceOf(BigDecimal.class);
    }

    @Test
    void testDistributionDataGeneration() {
        // 模拟服务
        when(courseService.count()).thenReturn(30L);
        when(studentService.countStudentsByGrade()).thenReturn(java.util.Arrays.asList(
            new Object[]{"2024级", 50L},
            new Object[]{"2023级", 45L},
            new Object[]{"2022级", 40L}
        ));

        // 执行测试
        DashboardStatsDTO result = dashboardService.getDashboardStats();

        // 验证分布数据
        assertThat(result.getCourseDistribution()).hasSize(3); // 必修、选修、实践
        assertThat(result.getGradeDistribution()).hasSize(3); // 3个年级
        assertThat(result.getMajorDistribution()).isNotEmpty(); // 专业分布
    }

    @Test
    void testQuickStatsGeneration() {
        // 模拟服务
        when(studentService.count()).thenReturn(1000L);
        when(paymentRecordService.getStatistics()).thenReturn(mockPaymentStats);
        when(userService.getUserStatistics()).thenReturn(mockUserStats);

        // 执行测试
        DashboardStatsDTO result = dashboardService.getDashboardStats();

        // 验证快速统计
        assertThat(result.getQuickStats()).isNotNull();
        assertThat(result.getQuickStats().getTodayNewStudents()).isGreaterThanOrEqualTo(0);
        assertThat(result.getQuickStats().getTodayPayments()).isGreaterThanOrEqualTo(0);
        assertThat(result.getQuickStats().getTodayRevenue()).isNotNull();
        assertThat(result.getQuickStats().getOnlineUsers()).isGreaterThanOrEqualTo(0);
        assertThat(result.getQuickStats().getSystemAlerts()).isEqualTo(0);
    }

    @Test
    void testSystemNotificationsGeneration() {
        // 执行测试
        DashboardStatsDTO result = dashboardService.getDashboardStats();

        // 验证系统通知
        assertThat(result.getSystemNotifications()).isNotNull().isNotEmpty();
        assertThat(result.getSystemNotifications()).hasSize(5); // 预定义5个通知
        assertThat(result.getSystemNotifications().get(0).getTitle()).isNotBlank();
        assertThat(result.getSystemNotifications().get(0).getContent()).isNotBlank();
        assertThat(result.getSystemNotifications().get(0).getSender()).isNotBlank();
        assertThat(result.getSystemNotifications().get(0).getCreateTime()).isNotNull();
    }
}
