package com.campus.domain.entity.finance;

import com.campus.domain.entity.auth.User;
import com.campus.domain.entity.infrastructure.BaseEntity;
import com.campus.domain.entity.organization.Student;
import com.campus.shared.security.EncryptionConfig.EncryptedField;
import com.campus.shared.security.EncryptionConfig.EncryptionEntityListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 缴费记录实体类
 * 记录学生的各项缴费信息
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_payment_record", indexes = {
    @Index(name = "idx_student_id", columnList = "student_id"),
    @Index(name = "idx_fee_item_id", columnList = "fee_item_id"),
    @Index(name = "idx_payment_time", columnList = "payment_time"),
    @Index(name = "idx_transaction_no", columnList = "transaction_no"),
    @Index(name = "idx_status_deleted", columnList = "status,deleted")
})
@EntityListeners(EncryptionEntityListener.class)
public class PaymentRecord extends BaseEntity {

    /**
     * 学生ID
     */
    @NotNull(message = "学生ID不能为空")
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    /**
     * 缴费项目ID
     */
    @NotNull(message = "缴费项目ID不能为空")
    @Column(name = "fee_item_id", nullable = false)
    private Long feeItemId;

    /**
     * 缴费金额
     */
    @NotNull(message = "缴费金额不能为空")
    @DecimalMin(value = "0.01", message = "缴费金额必须大于0")
    @DecimalMax(value = "999999.99", message = "缴费金额不能超过999999.99")
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * 缴费方式
     * CASH: 现金, CARD: 银行卡, ALIPAY: 支付宝, WECHAT: 微信, ONLINE: 网银
     */
    @NotBlank(message = "缴费方式不能为空")
    @Size(max = 20, message = "缴费方式长度不能超过20个字符")
    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod;

    /**
     * 缴费时间
     */
    @Column(name = "payment_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;

    /**
     * 交易流水号
     */
    @Size(max = 100, message = "交易流水号长度不能超过100个字符")
    @EncryptedField
    @Column(name = "transaction_no", unique = true, length = 255)  // 增加长度以容纳加密数据
    private String transactionNo;

    /**
     * 操作员ID
     */
    @Column(name = "operator_id")
    private Long operatorId;

    /**
     * 缴费状态
     * 1: 已缴费, 2: 退费中, 3: 已退费, 0: 已取消
     */
    @NotNull(message = "缴费状态不能为空")
    @Min(value = 0, message = "缴费状态值不正确")
    @Max(value = 3, message = "缴费状态值不正确")
    @Column(name = "payment_status", nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer paymentStatus = 1;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Column(name = "remarks", length = 500)
    private String remarks;

    // ================================
    // 关联关系
    // ================================

    /**
     * 学生信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    /**
     * 缴费项目
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_item_id", insertable = false, updatable = false)
    private FeeItem feeItem;

    /**
     * 操作员
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", insertable = false, updatable = false)
    private User operator;

    // ================================
    // 构造函数
    // ================================

    public PaymentRecord() {
        super();
    }

    public PaymentRecord(Long studentId, Long feeItemId, BigDecimal amount, String paymentMethod) {
        this();
        this.studentId = studentId;
        this.feeItemId = feeItemId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentTime = LocalDateTime.now();
    }

    public PaymentRecord(Long studentId, Long feeItemId, BigDecimal amount, String paymentMethod, Long operatorId) {
        this(studentId, feeItemId, amount, paymentMethod);
        this.operatorId = operatorId;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 获取学生姓名
     */
    public String getStudentName() {
        try {
            return student != null ? student.getName() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取缴费项目名称
     */
    public String getFeeItemName() {
        try {
            return feeItem != null ? feeItem.getItemName() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取操作员姓名
     */
    public String getOperatorName() {
        try {
            return operator != null ? operator.getRealName() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查是否已缴费
     */
    public boolean isPaid() {
        return paymentStatus != null && paymentStatus == 1;
    }

    /**
     * 检查是否退费中
     */
    public boolean isRefunding() {
        return paymentStatus != null && paymentStatus == 2;
    }

    /**
     * 检查是否已退费
     */
    public boolean isRefunded() {
        return paymentStatus != null && paymentStatus == 3;
    }

    /**
     * 检查是否已取消
     */
    public boolean isCancelled() {
        return paymentStatus != null && paymentStatus == 0;
    }

    /**
     * 获取缴费方式文本
     */
    public String getPaymentMethodText() {
        if (paymentMethod == null) return "未知";
        return switch (paymentMethod) {
            case "CASH" -> "现金";
            case "CARD" -> "银行卡";
            case "ALIPAY" -> "支付宝";
            case "WECHAT" -> "微信";
            case "ONLINE" -> "网银";
            default -> paymentMethod;
        };
    }

    /**
     * 获取缴费状态文本
     */
    public String getPaymentStatusText() {
        if (paymentStatus == null) return "未知";
        return switch (paymentStatus) {
            case 0 -> "已取消";
            case 1 -> "已缴费";
            case 2 -> "退费中";
            case 3 -> "已退费";
            default -> "未知";
        };
    }

    /**
     * 退费
     */
    public void refund() {
        this.paymentStatus = 2; // 退费中
    }

    /**
     * 确认退费
     */
    public void confirmRefund() {
        this.paymentStatus = 3; // 已退费
    }

    /**
     * 取消缴费
     */
    public void cancel() {
        this.paymentStatus = 0; // 已取消
        this.disable();
    }

    /**
     * 生成交易流水号
     */
    public void generateTransactionNo() {
        if (this.transactionNo == null) {
            this.transactionNo = "PAY" + System.currentTimeMillis() +
                               String.format("%04d", (int)(Math.random() * 10000));
        }
    }

    /**
     * 完成缴费
     */
    public void completePay() {
        this.paymentStatus = 1;
        this.paymentTime = LocalDateTime.now();
        generateTransactionNo();
    }

    // ================================
    // Getter/Setter 方法
    // ================================

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getFeeItemId() {
        return feeItemId;
    }

    public void setFeeItemId(Long feeItemId) {
        this.feeItemId = feeItemId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    // 为了兼容现有代码，添加getStatus和setStatus方法
    public Integer getStatus() {
        return paymentStatus;
    }

    public void setStatus(Integer status) {
        this.paymentStatus = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public FeeItem getFeeItem() {
        return feeItem;
    }

    public void setFeeItem(FeeItem feeItem) {
        this.feeItem = feeItem;
    }

    public User getOperator() {
        return operator;
    }

    public void setOperator(User operator) {
        this.operator = operator;
    }

    // ================================
    // 兼容性方法（为了支持现有服务层代码）
    // ================================

    /**
     * 设置创建时间（兼容性方法）
     */
    public void setCreatedTime(LocalDateTime createdTime) {
        this.setCreatedAt(createdTime);
    }

    /**
     * 获取创建时间（兼容性方法）
     */
    public LocalDateTime getCreatedTime() {
        return this.getCreatedAt();
    }

    /**
     * 设置更新时间（兼容性方法）
     */
    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.setUpdatedAt(updatedTime);
    }

    /**
     * 获取更新时间（兼容性方法）
     */
    public LocalDateTime getUpdatedTime() {
        return this.getUpdatedAt();
    }
}
