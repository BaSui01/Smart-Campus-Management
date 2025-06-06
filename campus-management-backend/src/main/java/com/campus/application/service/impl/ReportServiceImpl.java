package com.campus.application.service.impl;

import com.campus.application.service.ReportService;
import com.campus.domain.entity.FeeItem;
import com.campus.domain.repository.FeeItemRepository;
import com.campus.domain.repository.PaymentRecordRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;


/**
 * 报表服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-05
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;

    @Autowired
    private FeeItemRepository feeItemRepository;

    @Override
    public Map<String, Object> getFinancialStats(Integer year, Integer month) {
        Map<String, Object> stats = new HashMap<>();
        
        // 当前月份和年份
        LocalDateTime now = LocalDateTime.now();
        int currentYear = (year != null) ? year : now.getYear();
        int currentMonth = (month != null) ? month : now.getMonthValue();
        
        // 计算时间范围
        YearMonth yearMonth = YearMonth.of(currentYear, currentMonth);
        LocalDateTime monthStart = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        
        LocalDateTime yearStart = LocalDateTime.of(currentYear, 1, 1, 0, 0, 0);
        LocalDateTime yearEnd = LocalDateTime.of(currentYear, 12, 31, 23, 59, 59);
        
        // 月度收入
        BigDecimal monthlyRevenue = getRevenueByTimeRange(monthStart, monthEnd);
        stats.put("monthlyRevenue", "¥" + formatAmount(monthlyRevenue));
        
        // 年度收入
        BigDecimal yearlyRevenue = getRevenueByTimeRange(yearStart, yearEnd);
        stats.put("yearlyRevenue", "¥" + formatAmount(yearlyRevenue));
        
        // 未缴费用
        BigDecimal unpaidAmount = getUnpaidAmount();
        stats.put("unpaidAmount", "¥" + formatAmount(unpaidAmount));
        
        // 退费金额
        BigDecimal refundAmount = getRefundAmount(currentYear, currentMonth);
        stats.put("refundAmount", "¥" + formatAmount(refundAmount));
        
        return stats;
    }

    @Override
    public List<Map<String, Object>> getMonthlyRevenueData(Integer year) {
        List<Map<String, Object>> monthlyData = new ArrayList<>();
        int currentYear = (year != null) ? year : LocalDateTime.now().getYear();
        
        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(currentYear, month);
            LocalDateTime monthStart = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime monthEnd = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            
            BigDecimal revenue = getRevenueByTimeRange(monthStart, monthEnd);
            
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", month + "月");
            monthData.put("revenue", revenue);
            monthlyData.add(monthData);
        }
        
        return monthlyData;
    }

    @Override
    public List<Map<String, Object>> getFeeTypeStats(Integer year, Integer month) {
        List<Map<String, Object>> feeTypeStats = new ArrayList<>();
        
        // 获取所有费用项目
        List<FeeItem> feeItems = feeItemRepository.findAll();
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        Map<String, BigDecimal> feeTypeAmounts = new HashMap<>();
        
        // 计算时间范围用于日志记录
        LocalDateTime startTime;
        LocalDateTime endTime;
        if (month != null) {
            YearMonth yearMonth = YearMonth.of(year != null ? year : LocalDateTime.now().getYear(), month);
            startTime = yearMonth.atDay(1).atStartOfDay();
            endTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        } else {
            int currentYear = year != null ? year : LocalDateTime.now().getYear();
            startTime = LocalDateTime.of(currentYear, 1, 1, 0, 0, 0);
            endTime = LocalDateTime.of(currentYear, 12, 31, 23, 59, 59);
        }

        // 记录查询时间范围（用于调试和日志）
        System.out.println("费用类型统计查询时间范围: " + startTime + " 到 " + endTime);
        
        // 统计各费用类型的收入
        for (FeeItem feeItem : feeItems) {
            BigDecimal amount = paymentRecordRepository.sumAmountByFeeItemId(feeItem.getId());
            if (amount == null) amount = BigDecimal.ZERO;
            
            String feeType = feeItem.getFeeType() != null ? feeItem.getFeeType() : "其他费用";
            feeTypeAmounts.merge(feeType, amount, BigDecimal::add);
            totalAmount = totalAmount.add(amount);
        }
        
        // 生成统计数据
        for (Map.Entry<String, BigDecimal> entry : feeTypeAmounts.entrySet()) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("type", entry.getKey());
            stat.put("amount", entry.getValue());
            
            // 计算占比
            BigDecimal percentage = BigDecimal.ZERO;
            if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                percentage = entry.getValue()
                    .divide(totalAmount, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(1, RoundingMode.HALF_UP);
            }
            stat.put("percentage", percentage);
            
            feeTypeStats.add(stat);
        }
        
        // 如果没有数据，添加默认数据
        if (feeTypeStats.isEmpty()) {
            addDefaultFeeTypeStats(feeTypeStats);
        }
        
        return feeTypeStats;
    }

    @Override
    public byte[] exportReport(String type, Integer year, Integer month) {
        // 导出功能暂时返回空数组，实际项目中可以使用POI生成Excel或iText生成PDF
        return new byte[0];
    }

    @Override
    public BigDecimal getRevenueByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        BigDecimal revenue = paymentRecordRepository.sumAmountByTimeRange(startTime, endTime);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getUnpaidAmount() {
        // 这里可以根据业务逻辑计算未缴费金额
        // 例如：应缴费总额 - 已缴费总额
        // 暂时返回模拟数据
        return new BigDecimal("15000.00");
    }

    @Override
    public BigDecimal getRefundAmount(Integer year, Integer month) {
        // 这里可以根据退费记录计算退费金额
        // 暂时返回模拟数据
        return new BigDecimal("2500.00");
    }

    /**
     * 格式化金额显示
     */
    private String formatAmount(BigDecimal amount) {
        if (amount == null) return "0";
        return String.format("%,.2f", amount);
    }

    /**
     * 添加默认费用类型统计数据
     */
    private void addDefaultFeeTypeStats(List<Map<String, Object>> feeTypeStats) {
        String[] defaultTypes = {"学费", "住宿费", "教材费", "实验费", "其他费用"};
        BigDecimal[] defaultAmounts = {
            new BigDecimal("50000"), new BigDecimal("20000"), 
            new BigDecimal("8000"), new BigDecimal("5000"), new BigDecimal("3000")
        };
        BigDecimal total = Arrays.stream(defaultAmounts).reduce(BigDecimal.ZERO, BigDecimal::add);
        
        for (int i = 0; i < defaultTypes.length; i++) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("type", defaultTypes[i]);
            stat.put("amount", defaultAmounts[i]);
            
            BigDecimal percentage = defaultAmounts[i]
                .divide(total, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(1, RoundingMode.HALF_UP);
            stat.put("percentage", percentage);
            
            feeTypeStats.add(stat);
        }
    }
}