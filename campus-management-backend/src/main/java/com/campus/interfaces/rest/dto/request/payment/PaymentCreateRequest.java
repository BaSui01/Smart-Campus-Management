package com.campus.interfaces.rest.dto.request.payment;

import com.campus.interfaces.rest.dto.common.BaseRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 缴费创建请求DTO
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-19
 */
@Schema(description = "缴费创建请求")
public class PaymentCreateRequest extends BaseRequest {

    /**
     * 学生ID
     */
    @Schema(description = "学生ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "学生ID不能为空")
    @Min(value = 1, message = "学生ID必须大于0")
    private Long studentId;

    /**
     * 费用项目ID
     */
    @Schema(description = "费用项目ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "费用项目ID不能为空")
    @Min(value = 1, message = "费用项目ID必须大于0")
    private Long feeItemId;

    /**
     * 缴费金额
     */
    @Schema(description = "缴费金额", example = "5000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "缴费金额不能为空")
    @DecimalMin(value = "0.01", message = "缴费金额必须大于0")
    @DecimalMax(value = "999999.99", message = "缴费金额不能超过999999.99")
    @Digits(integer = 6, fraction = 2, message = "缴费金额格式不正确")
    private BigDecimal amount;

    /**
     * 缴费方式
     */
    @Schema(description = "缴费方式", example = "在线支付", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "缴费方式不能为空")
    @Size(max = 20, message = "缴费方式长度不能超过20个字符")
    private String paymentMethod;

    /**
     * 缴费渠道
     */
    @Schema(description = "缴费渠道", example = "支付宝")
    @Size(max = 20, message = "缴费渠道长度不能超过20个字符")
    private String paymentChannel;

    /**
     * 交易流水号
     */
    @Schema(description = "交易流水号", example = "202501151234567890")
    @Size(max = 50, message = "交易流水号长度不能超过50个字符")
    private String transactionId;

    /**
     * 第三方支付订单号
     */
    @Schema(description = "第三方支付订单号", example = "ALI202501151234567890")
    @Size(max = 100, message = "第三方支付订单号长度不能超过100个字符")
    private String thirdPartyOrderId;

    /**
     * 缴费时间
     */
    @Schema(description = "缴费时间", example = "2025-01-15 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;

    /**
     * 学年
     */
    @Schema(description = "学年", example = "2024-2025", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "学年不能为空")
    @Size(max = 20, message = "学年长度不能超过20个字符")
    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "学年格式不正确，应为YYYY-YYYY格式")
    private String academicYear;

    /**
     * 学期
     */
    @Schema(description = "学期", example = "1", allowableValues = {"1", "2", "3"})
    @Min(value = 1, message = "学期必须大于0")
    @Max(value = 3, message = "学期不能超过3")
    private Integer semester;

    /**
     * 缴费截止时间
     */
    @Schema(description = "缴费截止时间", example = "2025-02-28 23:59:59")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueTime;

    /**
     * 是否逾期
     */
    @Schema(description = "是否逾期", example = "false")
    private Boolean overdue = false;

    /**
     * 逾期天数
     */
    @Schema(description = "逾期天数", example = "0")
    @Min(value = 0, message = "逾期天数不能为负数")
    private Integer overdueDays = 0;

    /**
     * 滞纳金
     */
    @Schema(description = "滞纳金", example = "0.00")
    @DecimalMin(value = "0.00", message = "滞纳金不能为负数")
    @DecimalMax(value = "99999.99", message = "滞纳金不能超过99999.99")
    private BigDecimal lateFee = BigDecimal.ZERO;

    /**
     * 优惠金额
     */
    @Schema(description = "优惠金额", example = "0.00")
    @DecimalMin(value = "0.00", message = "优惠金额不能为负数")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /**
     * 实际缴费金额
     */
    @Schema(description = "实际缴费金额", example = "5000.00")
    @DecimalMin(value = "0.01", message = "实际缴费金额必须大于0")
    private BigDecimal actualAmount;

    /**
     * 缴费状态
     */
    @Schema(description = "缴费状态", example = "1", allowableValues = {"1", "2", "3", "4"})
    @Min(value = 1, message = "缴费状态必须大于0")
    @Max(value = 4, message = "缴费状态不能超过4")
    private Integer paymentStatus = 1;

    /**
     * 操作员ID
     */
    @Schema(description = "操作员ID", example = "1")
    @Min(value = 1, message = "操作员ID必须大于0")
    private Long operatorId;

    /**
     * 缴费说明
     */
    @Schema(description = "缴费说明", example = "2024-2025学年第一学期学费")
    @Size(max = 200, message = "缴费说明长度不能超过200个字符")
    private String description;

    // 构造函数
    public PaymentCreateRequest() {}

    // Getter和Setter方法
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

    public String getPaymentChannel() {
        return paymentChannel;
    }

    public void setPaymentChannel(String paymentChannel) {
        this.paymentChannel = paymentChannel;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getThirdPartyOrderId() {
        return thirdPartyOrderId;
    }

    public void setThirdPartyOrderId(String thirdPartyOrderId) {
        this.thirdPartyOrderId = thirdPartyOrderId;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public LocalDateTime getDueTime() {
        return dueTime;
    }

    public void setDueTime(LocalDateTime dueTime) {
        this.dueTime = dueTime;
    }

    public Boolean getOverdue() {
        return overdue;
    }

    public void setOverdue(Boolean overdue) {
        this.overdue = overdue;
    }

    public Integer getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(Integer overdueDays) {
        this.overdueDays = overdueDays;
    }

    public BigDecimal getLateFee() {
        return lateFee;
    }

    public void setLateFee(BigDecimal lateFee) {
        this.lateFee = lateFee;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public Integer getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 计算实际缴费金额
     */
    public void calculateActualAmount() {
        if (amount != null) {
            BigDecimal total = amount;
            if (lateFee != null) {
                total = total.add(lateFee);
            }
            if (discountAmount != null) {
                total = total.subtract(discountAmount);
            }
            this.actualAmount = total.max(BigDecimal.ZERO);
        }
    }

    /**
     * 验证金额是否合理
     */
    public boolean isAmountValid() {
        if (amount == null || actualAmount == null) return false;
        return actualAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public String toString() {
        return "PaymentCreateRequest{" +
                "studentId=" + studentId +
                ", feeItemId=" + feeItemId +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentChannel='" + paymentChannel + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", thirdPartyOrderId='" + thirdPartyOrderId + '\'' +
                ", paymentTime=" + paymentTime +
                ", academicYear='" + academicYear + '\'' +
                ", semester=" + semester +
                ", dueTime=" + dueTime +
                ", overdue=" + overdue +
                ", overdueDays=" + overdueDays +
                ", lateFee=" + lateFee +
                ", discountAmount=" + discountAmount +
                ", actualAmount=" + actualAmount +
                ", paymentStatus=" + paymentStatus +
                ", operatorId=" + operatorId +
                ", description='" + description + '\'' +
                ", status=" + getStatus() +
                ", remarks='" + getRemarks() + '\'' +
                '}';
    }
}
