package com.campus.application.Implement.system;

import com.campus.application.service.system.ReportService;
import com.campus.domain.entity.finance.FeeItem;
import com.campus.domain.repository.finance.FeeItemRepository;
import com.campus.domain.repository.finance.PaymentRecordRepository;

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

    // ================================
    // 报表管理页面需要的方法实现
    // ================================

    @Override
    public List<Map<String, Object>> getReportCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();

        String[] categoryNames = {"学生报表", "教师报表", "课程报表", "考勤报表", "成绩报表", "财务报表", "招生报表"};
        String[] categoryIcons = {"user", "users", "book", "clock", "award", "dollar-sign", "user-plus"};
        String[] categoryUrls = {"/admin/reports/student", "/admin/reports/teacher", "/admin/reports/course",
                               "/admin/reports/attendance", "/admin/reports/grade", "/admin/reports/financial", "/admin/reports/enrollment"};

        for (int i = 0; i < categoryNames.length; i++) {
            Map<String, Object> category = new HashMap<>();
            category.put("name", categoryNames[i]);
            category.put("icon", categoryIcons[i]);
            category.put("url", categoryUrls[i]);
            category.put("count", (i + 1) * 5); // 模拟报表数量
            categories.add(category);
        }

        return categories;
    }

    @Override
    public List<Map<String, Object>> getRecentReports() {
        List<Map<String, Object>> reports = new ArrayList<>();

        String[] reportNames = {"月度财务报表", "学生考勤统计", "课程选课情况", "教师工作量统计", "成绩分析报告"};
        String[] reportTypes = {"财务", "考勤", "课程", "教师", "成绩"};

        for (int i = 0; i < reportNames.length; i++) {
            Map<String, Object> report = new HashMap<>();
            report.put("name", reportNames[i]);
            report.put("type", reportTypes[i]);
            report.put("createTime", LocalDateTime.now().minusDays(i + 1));
            report.put("creator", "管理员");
            reports.add(report);
        }

        return reports;
    }

    @Override
    public List<Map<String, Object>> getFavoriteReports() {
        List<Map<String, Object>> reports = new ArrayList<>();

        String[] reportNames = {"学生成绩统计", "财务收支报表", "课程评价分析"};

        for (String name : reportNames) {
            Map<String, Object> report = new HashMap<>();
            report.put("name", name);
            report.put("favoriteTime", LocalDateTime.now().minusDays(1));
            reports.add(report);
        }

        return reports;
    }

    @Override
    public List<String> getAvailableSemesters() {
        List<String> semesters = new ArrayList<>();
        int currentYear = LocalDateTime.now().getYear();

        for (int year = currentYear - 2; year <= currentYear + 1; year++) {
            semesters.add(year + "-" + (year + 1) + "-1");
            semesters.add(year + "-" + (year + 1) + "-2");
        }

        return semesters;
    }

    @Override
    public List<Map<String, Object>> getPaymentTypes() {
        List<Map<String, Object>> types = new ArrayList<>();

        String[] typeNames = {"学费", "住宿费", "教材费", "实验费", "考试费", "其他费用"};

        for (String typeName : typeNames) {
            Map<String, Object> type = new HashMap<>();
            type.put("name", typeName);
            type.put("code", typeName.toLowerCase());
            types.add(type);
        }

        return types;
    }

    @Override
    public List<Integer> getEnrollmentYears() {
        List<Integer> years = new ArrayList<>();
        int currentYear = LocalDateTime.now().getYear();

        for (int year = currentYear - 10; year <= currentYear; year++) {
            years.add(year);
        }

        return years;
    }

    @Override
    public List<Map<String, Object>> getCustomReports() {
        List<Map<String, Object>> reports = new ArrayList<>();

        String[] reportNames = {"自定义学生统计", "课程分析报告", "财务趋势分析"};

        for (int i = 0; i < reportNames.length; i++) {
            Map<String, Object> report = new HashMap<>();
            report.put("id", (long) (i + 1));
            report.put("name", reportNames[i]);
            report.put("description", "自定义报表描述");
            report.put("createTime", LocalDateTime.now().minusDays(i + 1));
            reports.add(report);
        }

        return reports;
    }

    @Override
    public List<Map<String, Object>> getReportTemplates() {
        List<Map<String, Object>> templates = new ArrayList<>();

        String[] templateNames = {"学生信息模板", "课程统计模板", "财务报表模板", "考勤统计模板"};

        for (int i = 0; i < templateNames.length; i++) {
            Map<String, Object> template = new HashMap<>();
            template.put("id", (long) (i + 1));
            template.put("name", templateNames[i]);
            template.put("category", "标准模板");
            template.put("description", "模板描述");
            templates.add(template);
        }

        return templates;
    }

    @Override
    public List<Map<String, Object>> getAvailableDataSources() {
        List<Map<String, Object>> dataSources = new ArrayList<>();

        String[] sourceNames = {"学生信息", "课程数据", "成绩记录", "考勤数据", "财务记录"};
        String[] sourceTables = {"users", "courses", "grades", "attendance", "payment_records"};

        for (int i = 0; i < sourceNames.length; i++) {
            Map<String, Object> source = new HashMap<>();
            source.put("name", sourceNames[i]);
            source.put("table", sourceTables[i]);
            source.put("description", sourceNames[i] + "数据源");
            dataSources.add(source);
        }

        return dataSources;
    }

    @Override
    public List<Map<String, Object>> getChartTypes() {
        List<Map<String, Object>> chartTypes = new ArrayList<>();

        String[] typeNames = {"柱状图", "折线图", "饼图", "散点图", "雷达图"};
        String[] typeCodes = {"bar", "line", "pie", "scatter", "radar"};

        for (int i = 0; i < typeNames.length; i++) {
            Map<String, Object> type = new HashMap<>();
            type.put("name", typeNames[i]);
            type.put("code", typeCodes[i]);
            type.put("description", typeNames[i] + "展示");
            chartTypes.add(type);
        }

        return chartTypes;
    }

    @Override
    public Map<String, Object> getCustomReportById(Long id) {
        Map<String, Object> report = new HashMap<>();
        report.put("id", id);
        report.put("name", "自定义报表 " + id);
        report.put("description", "自定义报表描述");
        report.put("createTime", LocalDateTime.now().minusDays(1));
        report.put("creator", "管理员");
        report.put("config", new HashMap<>());
        return report;
    }

    @Override
    public List<Map<String, Object>> getScheduledReports() {
        List<Map<String, Object>> reports = new ArrayList<>();

        String[] reportNames = {"每日考勤统计", "周度成绩报告", "月度财务报表"};
        String[] schedules = {"每日 08:00", "每周一 09:00", "每月1日 10:00"};

        for (int i = 0; i < reportNames.length; i++) {
            Map<String, Object> report = new HashMap<>();
            report.put("id", (long) (i + 1));
            report.put("name", reportNames[i]);
            report.put("schedule", schedules[i]);
            report.put("status", i % 2 == 0 ? "启用" : "禁用");
            report.put("lastRun", LocalDateTime.now().minusHours(i + 1));
            reports.add(report);
        }

        return reports;
    }

    @Override
    public List<Map<String, Object>> getExportHistory() {
        List<Map<String, Object>> history = new ArrayList<>();

        String[] reportNames = {"学生信息导出", "课程数据导出", "财务报表导出"};
        String[] formats = {"Excel", "PDF", "CSV"};

        for (int i = 0; i < reportNames.length; i++) {
            Map<String, Object> record = new HashMap<>();
            record.put("id", (long) (i + 1));
            record.put("reportName", reportNames[i]);
            record.put("format", formats[i]);
            record.put("exportTime", LocalDateTime.now().minusHours(i + 1));
            record.put("fileSize", (i + 1) * 1024 + " KB");
            record.put("status", "完成");
            history.add(record);
        }

        return history;
    }

    @Override
    public Map<String, Object> getAnalyticsData() {
        Map<String, Object> analytics = new HashMap<>();

        // 模拟分析数据
        analytics.put("totalStudents", 1500);
        analytics.put("totalCourses", 120);
        analytics.put("totalTeachers", 80);
        analytics.put("attendanceRate", 95.5);
        analytics.put("passRate", 88.2);
        analytics.put("satisfactionScore", 4.3);

        // 趋势数据
        List<Map<String, Object>> trends = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Map<String, Object> trend = new HashMap<>();
            trend.put("month", i + 1);
            trend.put("students", 1400 + i * 10);
            trend.put("courses", 100 + i * 2);
            trends.add(trend);
        }
        analytics.put("trends", trends);

        return analytics;
    }

    @Override
    public Map<String, Object> getKPIMetrics() {
        Map<String, Object> kpi = new HashMap<>();

        // 关键绩效指标
        kpi.put("enrollmentRate", 92.5);
        kpi.put("retentionRate", 96.8);
        kpi.put("graduationRate", 94.2);
        kpi.put("employmentRate", 89.5);
        kpi.put("teacherSatisfaction", 4.2);
        kpi.put("studentSatisfaction", 4.1);

        // 同比增长
        Map<String, Object> growth = new HashMap<>();
        growth.put("enrollmentGrowth", 5.2);
        growth.put("retentionGrowth", 2.1);
        growth.put("graduationGrowth", 1.8);
        kpi.put("growth", growth);

        return kpi;
    }

    @Override
    public List<Map<String, Object>> getDashboardWidgets() {
        List<Map<String, Object>> widgets = new ArrayList<>();

        String[] widgetNames = {"学生统计", "课程概览", "考勤监控", "成绩分析", "财务状况"};
        String[] widgetTypes = {"chart", "table", "gauge", "chart", "number"};

        for (int i = 0; i < widgetNames.length; i++) {
            Map<String, Object> widget = new HashMap<>();
            widget.put("id", (long) (i + 1));
            widget.put("name", widgetNames[i]);
            widget.put("type", widgetTypes[i]);
            widget.put("position", i + 1);
            widget.put("enabled", true);
            widgets.add(widget);
        }

        return widgets;
    }

    @Override
    public List<Map<String, Object>> getAvailableWidgets() {
        List<Map<String, Object>> widgets = new ArrayList<>();

        String[] widgetNames = {"用户活跃度", "系统性能", "数据质量", "安全监控", "业务指标"};
        String[] descriptions = {"用户登录和活跃情况", "系统运行性能监控", "数据完整性检查", "安全事件监控", "关键业务指标"};

        for (int i = 0; i < widgetNames.length; i++) {
            Map<String, Object> widget = new HashMap<>();
            widget.put("id", (long) (i + 6)); // 避免与已有widget ID冲突
            widget.put("name", widgetNames[i]);
            widget.put("description", descriptions[i]);
            widget.put("category", "系统监控");
            widgets.add(widget);
        }

        return widgets;
    }

    @Override
    public List<Map<String, Object>> getTemplateCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();

        String[] categoryNames = {"学生管理", "教学管理", "财务管理", "系统管理"};
        String[] descriptions = {"学生相关报表模板", "教学相关报表模板", "财务相关报表模板", "系统相关报表模板"};

        for (int i = 0; i < categoryNames.length; i++) {
            Map<String, Object> category = new HashMap<>();
            category.put("id", (long) (i + 1));
            category.put("name", categoryNames[i]);
            category.put("description", descriptions[i]);
            category.put("templateCount", (i + 1) * 3);
            categories.add(category);
        }

        return categories;
    }
}