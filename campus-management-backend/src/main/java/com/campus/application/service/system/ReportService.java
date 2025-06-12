package com.campus.application.service.system;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 报表服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-05
 */
public interface ReportService {

    /**
     * 获取财务统计数据
     *
     * @param year 年份
     * @param month 月份 (可选)
     * @return 统计数据
     */
    Map<String, Object> getFinancialStats(Integer year, Integer month);

    /**
     * 获取月度收入趋势数据
     *
     * @param year 年份
     * @return 月度收入数据列表
     */
    List<Map<String, Object>> getMonthlyRevenueData(Integer year);

    /**
     * 获取费用类型统计数据
     *
     * @param year 年份
     * @param month 月份 (可选)
     * @return 费用类型统计列表
     */
    List<Map<String, Object>> getFeeTypeStats(Integer year, Integer month);

    /**
     * 导出报表数据
     *
     * @param type 报表类型 (month/quarter/year)
     * @param year 年份
     * @param month 月份 (可选)
     * @return 导出数据
     */
    byte[] exportReport(String type, Integer year, Integer month);

    /**
     * 获取指定时间范围内的收入
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 收入金额
     */
    BigDecimal getRevenueByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取未缴费总金额
     *
     * @return 未缴费金额
     */
    BigDecimal getUnpaidAmount();

    /**
     * 获取退费总金额
     *
     * @param year 年份
     * @param month 月份 (可选)
     * @return 退费金额
     */
    BigDecimal getRefundAmount(Integer year, Integer month);

    // ================================
    // 报表管理页面需要的方法
    // ================================

    /**
     * 获取报表分类
     *
     * @return 报表分类列表
     */
    List<Map<String, Object>> getReportCategories();

    /**
     * 获取最近的报表
     *
     * @return 最近报表列表
     */
    List<Map<String, Object>> getRecentReports();

    /**
     * 获取收藏的报表
     *
     * @return 收藏报表列表
     */
    List<Map<String, Object>> getFavoriteReports();

    /**
     * 获取可用的学期列表
     *
     * @return 学期列表
     */
    List<String> getAvailableSemesters();

    /**
     * 获取支付类型列表
     *
     * @return 支付类型列表
     */
    List<Map<String, Object>> getPaymentTypes();

    /**
     * 获取招生年份列表
     *
     * @return 招生年份列表
     */
    List<Integer> getEnrollmentYears();

    /**
     * 获取自定义报表列表
     *
     * @return 自定义报表列表
     */
    List<Map<String, Object>> getCustomReports();

    /**
     * 获取报表模板列表
     *
     * @return 报表模板列表
     */
    List<Map<String, Object>> getReportTemplates();

    /**
     * 获取可用的数据源
     *
     * @return 数据源列表
     */
    List<Map<String, Object>> getAvailableDataSources();

    /**
     * 获取图表类型列表
     *
     * @return 图表类型列表
     */
    List<Map<String, Object>> getChartTypes();

    /**
     * 根据ID获取自定义报表
     *
     * @param id 报表ID
     * @return 自定义报表
     */
    Map<String, Object> getCustomReportById(Long id);

    /**
     * 获取计划报表列表
     *
     * @return 计划报表列表
     */
    List<Map<String, Object>> getScheduledReports();

    /**
     * 获取导出历史
     *
     * @return 导出历史列表
     */
    List<Map<String, Object>> getExportHistory();

    /**
     * 获取分析数据
     *
     * @return 分析数据
     */
    Map<String, Object> getAnalyticsData();

    /**
     * 获取KPI指标
     *
     * @return KPI指标数据
     */
    Map<String, Object> getKPIMetrics();

    /**
     * 获取仪表板小部件
     *
     * @return 仪表板小部件列表
     */
    List<Map<String, Object>> getDashboardWidgets();

    /**
     * 获取可用的小部件
     *
     * @return 可用小部件列表
     */
    List<Map<String, Object>> getAvailableWidgets();

    /**
     * 获取模板分类
     *
     * @return 模板分类列表
     */
    List<Map<String, Object>> getTemplateCategories();
}