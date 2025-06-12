package com.campus.application.service.finance;

import com.campus.domain.entity.finance.FeeItem;
import com.campus.domain.entity.finance.PaymentRecord;
import com.campus.domain.entity.organization.Student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 缴费记录服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-05
 */
public interface PaymentRecordService {

    /**
     * 创建缴费记录
     *
     * @param paymentRecord 缴费记录信息
     * @return 创建的缴费记录
     */
    PaymentRecord createPaymentRecord(PaymentRecord paymentRecord);

    /**
     * 更新缴费记录
     *
     * @param id 记录ID
     * @param paymentRecord 更新的缴费记录信息
     * @return 更新后的缴费记录
     */
    PaymentRecord updatePaymentRecord(Long id, PaymentRecord paymentRecord);

    /**
     * 根据ID查找缴费记录
     *
     * @param id 记录ID
     * @return 缴费记录
     */
    Optional<PaymentRecord> findById(Long id);

    /**
     * 根据交易流水号查找缴费记录
     *
     * @param transactionNo 交易流水号
     * @return 缴费记录
     */
    Optional<PaymentRecord> findByTransactionNo(String transactionNo);

    /**
     * 获取所有缴费记录
     *
     * @return 缴费记录列表
     */
    List<PaymentRecord> findAll();

    /**
     * 分页查询缴费记录
     *
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<PaymentRecord> findAll(Pageable pageable);

    /**
     * 根据学生ID查找缴费记录
     *
     * @param studentId 学生ID
     * @return 缴费记录列表
     */
    List<PaymentRecord> findByStudentId(Long studentId);

    /**
     * 根据缴费项目ID查找缴费记录
     *
     * @param feeItemId 缴费项目ID
     * @return 缴费记录列表
     */
    List<PaymentRecord> findByFeeItemId(Long feeItemId);

    /**
     * 根据缴费方式查找缴费记录
     *
     * @param paymentMethod 缴费方式
     * @return 缴费记录列表
     */
    List<PaymentRecord> findByPaymentMethod(String paymentMethod);

    /**
     * 根据状态查找缴费记录
     *
     * @param status 状态
     * @return 缴费记录列表
     */
    List<PaymentRecord> findByStatus(Integer status);

    /**
     * 根据时间范围查找缴费记录
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 缴费记录列表
     */
    List<PaymentRecord> findByPaymentTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计学生缴费总金额
     *
     * @param studentId 学生ID
     * @return 总金额
     */
    BigDecimal sumAmountByStudentId(Long studentId);

    /**
     * 统计缴费项目收费总金额
     *
     * @param feeItemId 缴费项目ID
     * @return 总金额
     */
    BigDecimal sumAmountByFeeItemId(Long feeItemId);

    /**
     * 统计时间范围内的缴费总金额
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 总金额
     */
    BigDecimal sumAmountByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 删除缴费记录
     *
     * @param id 记录ID
     * @return 是否成功
     */
    boolean deletePaymentRecord(Long id);

    /**
     * 批量删除缴费记录
     *
     * @param ids 记录ID列表
     * @return 是否成功
     */
    boolean batchDeletePaymentRecords(List<Long> ids);

    /**
     * 退款处理
     *
     * @param id 记录ID
     * @param refundReason 退款原因
     * @param operatorId 操作员ID
     * @return 是否成功
     */
    boolean refundPayment(Long id, String refundReason, Long operatorId);



    /**
     * 生成交易流水号
     *
     * @return 交易流水号
     */
    String generateTransactionNo();

    /**
     * 缴费记录统计信息
     */
    class PaymentStatistics {
        private long totalRecords;
        private long successRecords;
        private long refundRecords;
        private long failedRecords;
        private BigDecimal totalAmount;
        private BigDecimal successAmount;
        private BigDecimal refundAmount;

        public PaymentStatistics() {}

        public PaymentStatistics(long totalRecords, long successRecords, long refundRecords, 
                               long failedRecords, BigDecimal totalAmount, BigDecimal successAmount, 
                               BigDecimal refundAmount) {
            this.totalRecords = totalRecords;
            this.successRecords = successRecords;
            this.refundRecords = refundRecords;
            this.failedRecords = failedRecords;
            this.totalAmount = totalAmount;
            this.successAmount = successAmount;
            this.refundAmount = refundAmount;
        }

        // Getter 和 Setter 方法
        public long getTotalRecords() { return totalRecords; }
        public void setTotalRecords(long totalRecords) { this.totalRecords = totalRecords; }

        public long getSuccessRecords() { return successRecords; }
        public void setSuccessRecords(long successRecords) { this.successRecords = successRecords; }

        public long getRefundRecords() { return refundRecords; }
        public void setRefundRecords(long refundRecords) { this.refundRecords = refundRecords; }

        public long getFailedRecords() { return failedRecords; }
        public void setFailedRecords(long failedRecords) { this.failedRecords = failedRecords; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public BigDecimal getSuccessAmount() { return successAmount; }
        public void setSuccessAmount(BigDecimal successAmount) { this.successAmount = successAmount; }

        public BigDecimal getRefundAmount() { return refundAmount; }
        public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    }

    /**
     * 获取缴费记录统计信息
     *
     * @return 统计信息
     */
    PaymentStatistics getStatistics();

    /**
     * 获取指定时间范围的缴费记录统计信息
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计信息
     */
    PaymentStatistics getStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查找未缴费的学生
     *
     * @return 未缴费学生列表
     */
    List<Student> findUnpaidStudents();

    /**
     * 获取学生的未缴费项目
     *
     * @param studentId 学生ID
     * @return 未缴费项目列表
     */
    List<FeeItem> getUnpaidFeeItems(Long studentId);
}
