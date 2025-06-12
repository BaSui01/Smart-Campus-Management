package com.campus.domain.entity.finance;

import com.campus.domain.entity.auth.User;
import com.campus.domain.entity.infrastructure.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 缴费项目实体类
 * 管理学校各种收费项目的定义和配置
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_fee_item", indexes = {
    @Index(name = "idx_item_code", columnList = "item_code", unique = true),
    @Index(name = "idx_fee_type", columnList = "fee_type"),
    @Index(name = "idx_applicable_grade", columnList = "applicable_grade"),
    @Index(name = "idx_due_date", columnList = "due_date"),
    @Index(name = "idx_academic_year", columnList = "academic_year"),
    @Index(name = "idx_status_deleted", columnList = "status,deleted")
})
public class FeeItem extends BaseEntity {

    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空")
    @Size(max = 100, message = "项目名称长度不能超过100个字符")
    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    /**
     * 项目编码
     */
    @NotBlank(message = "项目编码不能为空")
    @Size(max = 50, message = "项目编码长度不能超过50个字符")
    @Column(name = "item_code", nullable = false, unique = true, length = 50)
    private String itemCode;

    /**
     * 金额
     */
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * 费用类型
     * tuition（学费）, accommodation（住宿费）, textbook（教材费）,
     * activity（活动费）, insurance（保险费）, exam（考试费）, other（其他）
     */
    @NotBlank(message = "费用类型不能为空")
    @Size(max = 20, message = "费用类型长度不能超过20个字符")
    @Column(name = "fee_type", nullable = false, length = 20)
    private String feeType;

    /**
     * 适用年级
     */
    @Size(max = 100, message = "适用年级长度不能超过100个字符")
    @Column(name = "applicable_grade", length = 100)
    private String applicableGrade;

    /**
     * 适用专业
     */
    @Size(max = 200, message = "适用专业长度不能超过200个字符")
    @Column(name = "applicable_major", length = 200)
    private String applicableMajor;

    /**
     * 学年
     */
    @NotNull(message = "学年不能为空")
    @Column(name = "academic_year", nullable = false)
    private Integer academicYear;

    /**
     * 学期
     */
    @Size(max = 20, message = "学期长度不能超过20个字符")
    @Column(name = "semester", length = 20)
    private String semester;

    /**
     * 缴费截止日期
     */
    @Column(name = "due_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    /**
     * 缴费开始日期
     */
    @Column(name = "start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * 是否必缴
     */
    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory = true;

    /**
     * 是否允许分期
     */
    @Column(name = "allow_installment", nullable = false)
    private Boolean allowInstallment = false;

    /**
     * 分期数量
     */
    @Min(value = 1, message = "分期数量不能小于1")
    @Max(value = 12, message = "分期数量不能大于12")
    @Column(name = "installment_count")
    private Integer installmentCount;

    /**
     * 逾期滞纳金比例（百分比）
     */
    @DecimalMin(value = "0.0", message = "滞纳金比例不能小于0")
    @DecimalMax(value = "100.0", message = "滞纳金比例不能大于100")
    @Column(name = "late_fee_rate", precision = 5, scale = 2)
    private BigDecimal lateFeeRate;

    /**
     * 优惠金额
     */
    @DecimalMin(value = "0.0", message = "优惠金额不能小于0")
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    /**
     * 优惠截止日期
     */
    @Column(name = "discount_deadline")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate discountDeadline;

    /**
     * 项目描述
     */
    @Size(max = 1000, message = "项目描述长度不能超过1000个字符")
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * 缴费说明
     */
    @Size(max = 1000, message = "缴费说明长度不能超过1000个字符")
    @Column(name = "payment_instructions", length = 1000)
    private String paymentInstructions;

    /**
     * 创建人ID
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * 审核人ID
     */
    @Column(name = "approved_by")
    private Long approvedBy;

    /**
     * 审核时间
     */
    @Column(name = "approved_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvedTime;

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
     * 缴费记录
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "feeItem")
    @JsonIgnore
    private List<PaymentRecord> paymentRecords;

    /**
     * 创建人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    @JsonIgnore
    private User creator;

    /**
     * 审核人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by", insertable = false, updatable = false)
    @JsonIgnore
    private User approver;

    // ================================
    // 构造函数
    // ================================

    public FeeItem() {
        super();
    }

    public FeeItem(String itemName, String itemCode, BigDecimal amount, String feeType) {
        this();
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.amount = amount;
        this.feeType = feeType;
    }

    public FeeItem(String itemName, String itemCode, BigDecimal amount, String feeType,
                   Integer academicYear, String semester) {
        this();
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.amount = amount;
        this.feeType = feeType;
        this.academicYear = academicYear;
        this.semester = semester;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 获取费用类型文本
     */
    public String getFeeTypeText() {
        if (feeType == null) return "未知";
        return switch (feeType) {
            case "tuition" -> "学费";
            case "accommodation" -> "住宿费";
            case "textbook" -> "教材费";
            case "activity" -> "活动费";
            case "insurance" -> "保险费";
            case "exam" -> "考试费";
            case "other" -> "其他";
            default -> feeType;
        };
    }

    /**
     * 检查是否已过期
     */
    public boolean isOverdue() {
        return dueDate != null && LocalDate.now().isAfter(dueDate);
    }

    /**
     * 检查是否在缴费期内
     */
    public boolean isInPaymentPeriod() {
        LocalDate now = LocalDate.now();
        boolean afterStart = startDate == null || !now.isBefore(startDate);
        boolean beforeDue = dueDate == null || !now.isAfter(dueDate);
        return afterStart && beforeDue;
    }

    /**
     * 检查是否享受优惠
     */
    public boolean hasDiscount() {
        return discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0 &&
               (discountDeadline == null || !LocalDate.now().isAfter(discountDeadline));
    }

    /**
     * 获取实际应缴金额
     */
    public BigDecimal getActualAmount() {
        if (hasDiscount()) {
            return amount.subtract(discountAmount);
        }
        return amount;
    }

    /**
     * 计算逾期滞纳金
     */
    public BigDecimal calculateLateFee() {
        if (!isOverdue() || lateFeeRate == null) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(lateFeeRate).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * 获取总应缴金额（含滞纳金）
     */
    public BigDecimal getTotalAmount() {
        return getActualAmount().add(calculateLateFee());
    }

    /**
     * 检查是否适用于指定年级
     */
    public boolean isApplicableToGrade(String grade) {
        if (applicableGrade == null || applicableGrade.trim().isEmpty()) {
            return true; // 如果没有限制，则适用于所有年级
        }
        return applicableGrade.contains(grade);
    }

    /**
     * 检查是否适用于指定专业
     */
    public boolean isApplicableToMajor(String major) {
        if (applicableMajor == null || applicableMajor.trim().isEmpty()) {
            return true; // 如果没有限制，则适用于所有专业
        }
        return applicableMajor.contains(major);
    }

    /**
     * 审核通过
     */
    public void approve(Long approverId) {
        this.approvedBy = approverId;
        this.approvedTime = LocalDateTime.now();
        this.status = 1; // 激活状态
    }

    /**
     * 停用项目
     */
    public void deactivate() {
        this.status = 0;
    }

    /**
     * 激活项目
     */
    public void activate() {
        this.status = 1;
    }

    /**
     * 检查是否已审核
     */
    public boolean isApproved() {
        return approvedBy != null && approvedTime != null;
    }

    /**
     * 获取创建人姓名
     */
    public String getCreatorName() {
        return creator != null ? creator.getRealName() : null;
    }

    /**
     * 获取审核人姓名
     */
    public String getApproverName() {
        return approver != null ? approver.getRealName() : null;
    }

    /**
     * 获取状态描述
     */
    public String getStatusDescription() {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "停用";
            case 1 -> "启用";
            default -> "未知";
        };
    }

    /**
     * 获取缴费状态描述
     */
    public String getPaymentStatusDescription() {
        if (!isInPaymentPeriod()) {
            if (LocalDate.now().isBefore(startDate)) {
                return "未开始";
            } else {
                return "已截止";
            }
        }
        return "缴费中";
    }

    /**
     * 获取适用范围描述
     */
    public String getApplicableScopeDescription() {
        StringBuilder sb = new StringBuilder();
        if (applicableGrade != null && !applicableGrade.trim().isEmpty()) {
            sb.append("年级: ").append(applicableGrade);
        }
        if (applicableMajor != null && !applicableMajor.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("专业: ").append(applicableMajor);
        }
        return sb.length() > 0 ? sb.toString() : "全部";
    }

    // ================================
    // 验证方法
    // ================================

    /**
     * 验证缴费项目数据
     */
    @PrePersist
    @PreUpdate
    public void validateFeeItem() {
        // 验证日期逻辑
        if (startDate != null && dueDate != null && startDate.isAfter(dueDate)) {
            throw new IllegalArgumentException("开始日期不能晚于截止日期");
        }

        if (discountDeadline != null && dueDate != null && discountDeadline.isAfter(dueDate)) {
            throw new IllegalArgumentException("优惠截止日期不能晚于缴费截止日期");
        }

        // 验证分期设置
        if (allowInstallment && (installmentCount == null || installmentCount < 2)) {
            throw new IllegalArgumentException("允许分期时必须设置分期数量且不能少于2期");
        }

        // 验证优惠金额
        if (discountAmount != null && discountAmount.compareTo(amount) >= 0) {
            throw new IllegalArgumentException("优惠金额不能大于或等于原金额");
        }
    }

    // ================================
    // Getter and Setter methods
    // ================================

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getApplicableGrade() {
        return applicableGrade;
    }

    public void setApplicableGrade(String applicableGrade) {
        this.applicableGrade = applicableGrade;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<PaymentRecord> getPaymentRecords() {
        return paymentRecords;
    }

    public void setPaymentRecords(List<PaymentRecord> paymentRecords) {
        this.paymentRecords = paymentRecords;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public User getApprover() {
        return approver;
    }

    public void setApprover(User approver) {
        this.approver = approver;
    }

    // ================================
    // 兼容性方法（为现有Service提供支持）
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

    @Override
    public String toString() {
        return "FeeItem{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", itemCode='" + itemCode + '\'' +
                ", amount=" + amount +
                ", feeType='" + feeType + '\'' +
                ", applicableGrade='" + applicableGrade + '\'' +
                ", applicableMajor='" + applicableMajor + '\'' +
                ", academicYear=" + academicYear +
                ", semester='" + semester + '\'' +
                ", dueDate=" + dueDate +
                ", startDate=" + startDate +
                ", isMandatory=" + isMandatory +
                ", allowInstallment=" + allowInstallment +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
