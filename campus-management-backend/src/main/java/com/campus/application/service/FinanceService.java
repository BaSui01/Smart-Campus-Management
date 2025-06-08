package com.campus.application.service;

import com.campus.domain.entity.FeeItem;
import com.campus.domain.entity.PaymentRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 财务服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
public interface FinanceService {

    /**
     * 获取财务统计信息
     */
    Map<String, Object> getFinanceStats();

    /**
     * 获取最近的财务记录
     */
    List<PaymentRecord> getRecentFinanceRecords(int limit);

    /**
     * 获取收费项目列表
     */
    List<FeeItem> getFeeItemList();

    /**
     * 分页查询收费项目
     */
    Page<FeeItem> getFeeItemsPage(Pageable pageable, String search, String feeType, String status);

    /**
     * 分页查询收费项目（带过滤条件）
     */
    Page<FeeItem> findFeeItemsWithFilters(String itemName, String feeType, Integer status, int page, int size);

    /**
     * 获取缴费记录列表
     */
    List<PaymentRecord> getPaymentRecordList();

    /**
     * 分页查询缴费记录
     */
    Page<PaymentRecord> getPaymentRecordsPage(Pageable pageable, String search, String status, String paymentMethod);

    /**
     * 分页查询缴费记录（带过滤条件）
     */
    Page<PaymentRecord> findPaymentRecordsWithFilters(String studentName, String feeType, String status,
                                                      LocalDateTime startDate, LocalDateTime endDate, int page, int size);

    /**
     * 获取财务报表数据
     */
    Map<String, Object> getFinanceReportData();

    /**
     * 保存收费项目
     */
    FeeItem saveFeeItem(FeeItem feeItem);

    /**
     * 删除收费项目（软删除）
     */
    void deleteFeeItem(Long id);

    /**
     * 根据ID查找收费项目
     */
    Optional<FeeItem> findFeeItemById(Long id);

    /**
     * 保存缴费记录
     */
    PaymentRecord savePaymentRecord(PaymentRecord paymentRecord);

    /**
     * 根据ID查找缴费记录
     */
    Optional<PaymentRecord> findPaymentRecordById(Long id);

    /**
     * 确认缴费
     */
    PaymentRecord confirmPayment(Long id);

    /**
     * 获取学生未缴费记录
     */
    List<PaymentRecord> getUnpaidRecordsByStudent(Long studentId);

    /**
     * 获取逾期缴费记录
     */
    List<PaymentRecord> getOverdueRecords();

    /**
     * 批量生成缴费记录
     */
    void generatePaymentRecords(Long feeItemId, List<Long> studentIds);

    /**
     * 获取收入趋势数据
     */
    List<Object[]> getIncomeTraend(int months);

    /**
     * 导出财务数据
     */
    Map<String, Object> exportFinanceData();
}
