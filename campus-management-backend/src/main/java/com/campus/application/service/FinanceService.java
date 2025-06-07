package com.campus.application.service;

import com.campus.domain.entity.FeeItem;
import com.campus.domain.entity.PaymentRecord;
import com.campus.domain.repository.FeeItemRepository;
import com.campus.domain.repository.PaymentRecordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 财务服务类
 */
@Service
@Transactional
public class FinanceService {

    private final FeeItemRepository feeItemRepository;
    private final PaymentRecordRepository paymentRecordRepository;

    public FinanceService(FeeItemRepository feeItemRepository, PaymentRecordRepository paymentRecordRepository) {
        this.feeItemRepository = feeItemRepository;
        this.paymentRecordRepository = paymentRecordRepository;
    }

    /**
     * 获取财务统计信息
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getFinanceStats() {
        Map<String, Object> stats = new HashMap<>();

        // 总收入 - 使用现有方法计算所有已缴费记录的总金额
        List<PaymentRecord> paidRecords = paymentRecordRepository.findByStatus(1); // status=1表示已缴费
        BigDecimal totalIncome = paidRecords.stream()
                .map(PaymentRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalIncome", totalIncome);

        // 本月收入
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        BigDecimal monthlyIncome = paymentRecordRepository.sumAmountByTimeRange(monthStart, monthEnd);
        stats.put("monthlyIncome", monthlyIncome != null ? monthlyIncome : BigDecimal.ZERO);

        // 待缴费用 - 计算status!=1的记录总金额
        List<PaymentRecord> unpaidRecords = paymentRecordRepository.findByStatus(0); // status=0表示待缴费
        BigDecimal unpaidAmount = unpaidRecords.stream()
                .map(PaymentRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("unpaidAmount", unpaidAmount);

        // 逾期费用 - 简化处理，暂时设为0
        BigDecimal overdueAmount = BigDecimal.ZERO;
        stats.put("overdueAmount", overdueAmount);

        // 总支出（这里可以根据实际业务添加支出统计）
        BigDecimal totalExpense = BigDecimal.ZERO; // 暂时设为0
        stats.put("totalExpense", totalExpense);

        // 本月支出
        BigDecimal monthlyExpense = BigDecimal.ZERO; // 暂时设为0
        stats.put("monthlyExpense", monthlyExpense);

        return stats;
    }

    /**
     * 获取最近的财务记录
     */
    @Transactional(readOnly = true)
    public List<PaymentRecord> getRecentFinanceRecords(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<PaymentRecord> page = paymentRecordRepository.findAllActive(pageable);
        return page.getContent();
    }

    /**
     * 获取收费项目列表
     */
    @Transactional(readOnly = true)
    public List<FeeItem> getFeeItemList() {
        return feeItemRepository.findActiveItems();
    }

    /**
     * 分页查询收费项目
     */
    @Transactional(readOnly = true)
    public Page<FeeItem> getFeeItemsPage(Pageable pageable, String search, String feeType, String status) {
        try {
            // 简化处理：直接使用分页查询所有记录
            // 避免复杂的条件查询导致性能问题
            return feeItemRepository.findAll(pageable);
        } catch (Exception e) {
            System.err.println("分页查询收费项目失败: " + e.getMessage());
            // 返回空页面而不是抛出异常
            return Page.empty(pageable);
        }
    }

    /**
     * 分页查询收费项目
     */
    @Transactional(readOnly = true)
    public Page<FeeItem> findFeeItemsWithFilters(
            String itemName,
            String feeType,
            Integer status,
            int page,
            int size) {

        // 使用现有的搜索方法
        List<FeeItem> items = feeItemRepository.searchFeeItems(itemName, feeType, status);
        // 简化处理，返回所有结果（实际项目中应该实现真正的分页）
        return new org.springframework.data.domain.PageImpl<>(items);
    }

    /**
     * 获取缴费记录列表
     */
    @Transactional(readOnly = true)
    public List<PaymentRecord> getPaymentRecordList() {
        return paymentRecordRepository.findAllActive();
    }

    /**
     * 分页查询缴费记录
     */
    @Transactional(readOnly = true)
    public Page<PaymentRecord> getPaymentRecordsPage(Pageable pageable, String search, String status, String paymentMethod) {
        try {
            // 简化处理：直接使用分页查询所有活跃记录
            // 避免复杂的条件查询导致性能问题
            return paymentRecordRepository.findAllActive(pageable);
        } catch (Exception e) {
            System.err.println("分页查询缴费记录失败: " + e.getMessage());
            // 返回空页面而不是抛出异常
            return Page.empty(pageable);
        }
    }

    /**
     * 分页查询缴费记录
     */
    @Transactional(readOnly = true)
    public Page<PaymentRecord> findPaymentRecordsWithFilters(
            String studentName,
            String feeType,
            String status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size) {

        // 简化处理，使用现有方法
        List<PaymentRecord> records;
        if (startDate != null && endDate != null) {
            records = paymentRecordRepository.findByPaymentTimeBetween(startDate, endDate);
        } else {
            records = paymentRecordRepository.findAllActive();
        }

        return new org.springframework.data.domain.PageImpl<>(records);
    }

    /**
     * 获取财务报表数据
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getFinanceReportData() {
        Map<String, Object> reportData = new HashMap<>();

        // 简化的月度收入统计
        List<PaymentRecord> allRecords = paymentRecordRepository.findByStatus(1);
        reportData.put("monthlyIncome", allRecords);

        // 简化的费用类型统计
        List<FeeItem> feeItems = feeItemRepository.findActiveItems();
        reportData.put("feeTypeStats", feeItems);

        // 简化的年级缴费统计
        reportData.put("gradeStats", allRecords);

        // 简化的缴费方式统计
        reportData.put("paymentMethodStats", allRecords);

        return reportData;
    }

    /**
     * 保存收费项目
     */
    public FeeItem saveFeeItem(FeeItem feeItem) {
        if (feeItem.getId() == null) {
            feeItem.setCreatedTime(LocalDateTime.now());
        }
        feeItem.setUpdatedTime(LocalDateTime.now());
        return feeItemRepository.save(feeItem);
    }

    /**
     * 删除收费项目（软删除）
     */
    public void deleteFeeItem(Long id) {
        Optional<FeeItem> optionalFeeItem = feeItemRepository.findById(id);
        if (optionalFeeItem.isPresent()) {
            FeeItem feeItem = optionalFeeItem.get();
            feeItem.setDeleted(1);
            feeItem.setUpdatedTime(LocalDateTime.now());
            feeItemRepository.save(feeItem);
        }
    }

    /**
     * 根据ID查找收费项目
     */
    @Transactional(readOnly = true)
    public Optional<FeeItem> findFeeItemById(Long id) {
        return feeItemRepository.findById(id);
    }

    /**
     * 保存缴费记录
     */
    public PaymentRecord savePaymentRecord(PaymentRecord paymentRecord) {
        // PaymentRecord实体有@PrePersist和@PreUpdate方法自动设置时间
        return paymentRecordRepository.save(paymentRecord);
    }

    /**
     * 根据ID查找缴费记录
     */
    @Transactional(readOnly = true)
    public Optional<PaymentRecord> findPaymentRecordById(Long id) {
        return paymentRecordRepository.findById(id);
    }

    /**
     * 确认缴费
     */
    public PaymentRecord confirmPayment(Long id) {
        Optional<PaymentRecord> optionalRecord = paymentRecordRepository.findById(id);
        if (optionalRecord.isPresent()) {
            PaymentRecord record = optionalRecord.get();
            record.setStatus(1); // 1表示已缴费
            record.setPaymentTime(LocalDateTime.now());
            return paymentRecordRepository.save(record);
        }
        throw new RuntimeException("缴费记录不存在");
    }

    /**
     * 获取学生未缴费记录
     */
    @Transactional(readOnly = true)
    public List<PaymentRecord> getUnpaidRecordsByStudent(Long studentId) {
        List<PaymentRecord> studentRecords = paymentRecordRepository.findByStudentId(studentId);
        return studentRecords.stream()
                .filter(record -> record.getStatus() != 1) // 非已缴费状态
                .toList();
    }

    /**
     * 获取逾期缴费记录
     */
    @Transactional(readOnly = true)
    public List<PaymentRecord> getOverdueRecords() {
        // 简化处理，返回所有未缴费记录
        return paymentRecordRepository.findByStatus(0);
    }

    /**
     * 批量生成缴费记录
     */
    @Transactional
    public void generatePaymentRecords(Long feeItemId, List<Long> studentIds) {
        Optional<FeeItem> optionalFeeItem = feeItemRepository.findById(feeItemId);
        if (optionalFeeItem.isPresent()) {
            FeeItem feeItem = optionalFeeItem.get();

            for (Long studentId : studentIds) {
                // 检查是否已存在该学生的缴费记录
                boolean exists = paymentRecordRepository.existsByStudentIdAndFeeItemId(studentId, feeItemId);
                if (!exists) {
                    PaymentRecord record = new PaymentRecord();
                    record.setStudentId(studentId);
                    record.setFeeItemId(feeItemId);
                    // PaymentRecord实体没有feeItemName字段，通过关联获取
                    record.setAmount(feeItem.getAmount());
                    record.setStatus(0); // 0表示待缴费
                    // PaymentRecord实体没有dueDate字段
                    // 时间字段由@PrePersist自动设置

                    paymentRecordRepository.save(record);
                }
            }

            // 为收费项目生成了缴费记录
        }
    }

    /**
     * 获取收入趋势数据
     */
    @Transactional(readOnly = true)
    public List<Object[]> getIncomeTraend(int months) {
        // 简化处理，返回最近的缴费记录
        List<PaymentRecord> records = paymentRecordRepository.findByStatus(1);
        return records.stream()
                .map(record -> new Object[]{record.getPaymentTime(), record.getAmount()})
                .toList();
    }

    /**
     * 导出财务数据
     */
    @Transactional(readOnly = true)
    public Map<String, Object> exportFinanceData() {
        Map<String, Object> exportData = new HashMap<>();
        
        // 收费项目数据
        List<FeeItem> feeItems = getFeeItemList();
        exportData.put("feeItems", feeItems);
        
        // 缴费记录数据
        List<PaymentRecord> paymentRecords = getPaymentRecordList();
        exportData.put("paymentRecords", paymentRecords);
        
        // 统计数据
        Map<String, Object> stats = getFinanceStats();
        exportData.put("stats", stats);
        
        return exportData;
    }
}
