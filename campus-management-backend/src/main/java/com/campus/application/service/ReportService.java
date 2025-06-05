package com.campus.application.service;

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
}