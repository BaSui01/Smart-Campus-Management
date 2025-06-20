package com.campus.interfaces.rest.dto.response.payment;

import com.campus.interfaces.rest.dto.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 缴费响应DTO
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-19
 */
@Schema(description = "缴费响应数据")
public class PaymentResponse extends BaseResponse {

    /**
     * 学生ID
     */
    @Schema(description = "学生ID", example = "1")
    private Long studentId;

    /**
     * 学生姓名
     */
    @Schema(description = "学生姓名", example = "张三")
    private String studentName;

    /**
     * 学号
     */
    @Schema(description = "学号", example = "2024001")
    private String studentNo;

    /**
     * 班级名称
     */
    @Schema(description = "班级名称", example = "计算机2024-1班")
    private String className;

    /**
     * 费用项目ID
     */
    @Schema(description = "费用项目ID", example = "1")
    private Long feeItemId;

    /**
     * 费用项目名称
     */
    @Schema(description = "费用项目名称", example = "学费")
    private String feeItemName;

    /**
     * 费用类型
     */
    @Schema(description = "费用类型", example = "学费")
    private String feeType;

    /**
     * 缴费金额
     */
    @Schema(description = "缴费金额", example = "5000.00")
    private BigDecimal amount;

    /**
     * 缴费方式
     */
    @Schema(description = "缴费方式", example = "在线支付")
    private String paymentMethod;

    /**
     * 缴费渠道
     */
    @Schema(description = "缴费渠道", example = "支付宝")
    private String paymentChannel;

    /**
     * 交易流水号
     */
    @Schema(description = "交易流水号", example = "202501151234567890")
    private String transactionId;

    /**
     * 第三方支付订单号
     */
    @Schema(description = "第三方支付订单号", example = "ALI202501151234567890")
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
    @Schema(description = "学年", example = "2024-2025")
    private String academicYear;

    /**
     * 学期
     */
    @Schema(description = "学期", example = "1")
    private Integer semester;

    /**
     * 学期文本
     */
    @Schema(description = "学期文本", example = "第一学期")
    private String semesterText;

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
    private Boolean overdue;

    /**
     * 逾期天数
     */
    @Schema(description = "逾期天数", example = "0")
    private Integer overdueDays;

    /**
     * 滞纳金
     */
    @Schema(description = "滞纳金", example = "0.00")
    private BigDecimal lateFee;

    /**
     * 优惠金额
     */
    @Schema(description = "优惠金额", example = "0.00")
    private BigDecimal discountAmount;

    /**
     * 实际缴费金额
     */
    @Schema(description = "实际缴费金额", example = "5000.00")
    private BigDecimal actualAmount;

    /**
     * 缴费状态
     */
    @Schema(description = "缴费状态", example = "1")
    private Integer paymentStatus;

    /**
     * 缴费状态文本
     */
    @Schema(description = "缴费状态文本", example = "已缴费")
    private String paymentStatusText;

    /**
     * 操作员ID
     */
    @Schema(description = "操作员ID", example = "1")
    private Long operatorId;

    /**
     * 操作员姓名
     */
    @Schema(description = "操作员姓名", example = "李老师")
    private String operatorName;

    /**
     * 缴费说明
     */
    @Schema(description = "缴费说明", example = "2024-2025学年第一学期学费")
    private String description;

    /**
     * 收据号
     */
    @Schema(description = "收据号", example = "R202501151234567890")
    private String receiptNo;

    /**
     * 发票号
     */
    @Schema(description = "发票号", example = "I202501151234567890")
    private String invoiceNo;

    // 构造函数
    public PaymentResponse() {}

    // Getter和Setter方法
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getFeeItemId() {
        return feeItemId;
    }

    public void setFeeItemId(Long feeItemId) {
        this.feeItemId = feeItemId;
    }

    public String getFeeItemName() {
        return feeItemName;
    }

    public void setFeeItemName(String feeItemName) {
        this.feeItemName = feeItemName;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
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
        // 自动设置学期文本
        switch (semester != null ? semester : 0) {
            case 1:
                this.semesterText = "第一学期";
                break;
            case 2:
                this.semesterText = "第二学期";
                break;
            case 3:
                this.semesterText = "第三学期";
                break;
            default:
                this.semesterText = "未知学期";
                break;
        }
    }

    public String getSemesterText() {
        return semesterText;
    }

    public void setSemesterText(String semesterText) {
        this.semesterText = semesterText;
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
        // 自动设置缴费状态文本
        switch (paymentStatus != null ? paymentStatus : 0) {
            case 1:
                this.paymentStatusText = "待缴费";
                break;
            case 2:
                this.paymentStatusText = "已缴费";
                break;
            case 3:
                this.paymentStatusText = "部分缴费";
                break;
            case 4:
                this.paymentStatusText = "已退费";
                break;
            default:
                this.paymentStatusText = "未知状态";
                break;
        }
    }

    public String getPaymentStatusText() {
        return paymentStatusText;
    }

    public void setPaymentStatusText(String paymentStatusText) {
        this.paymentStatusText = paymentStatusText;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    /**
     * 是否已完成缴费
     */
    public Boolean isPaid() {
        return paymentStatus != null && paymentStatus == 2;
    }

    /**
     * 是否需要缴费
     */
    public Boolean needPayment() {
        return paymentStatus != null && (paymentStatus == 1 || paymentStatus == 3);
    }

    /**
     * 获取节省金额（优惠金额）
     */
    public BigDecimal getSavedAmount() {
        return discountAmount != null ? discountAmount : BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "id=" + getId() +
                ", studentId=" + studentId +
                ", studentName='" + studentName + '\'' +
                ", studentNo='" + studentNo + '\'' +
                ", className='" + className + '\'' +
                ", feeItemId=" + feeItemId +
                ", feeItemName='" + feeItemName + '\'' +
                ", feeType='" + feeType + '\'' +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentChannel='" + paymentChannel + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", thirdPartyOrderId='" + thirdPartyOrderId + '\'' +
                ", paymentTime=" + paymentTime +
                ", academicYear='" + academicYear + '\'' +
                ", semester=" + semester +
                ", semesterText='" + semesterText + '\'' +
                ", dueTime=" + dueTime +
                ", overdue=" + overdue +
                ", overdueDays=" + overdueDays +
                ", lateFee=" + lateFee +
                ", discountAmount=" + discountAmount +
                ", actualAmount=" + actualAmount +
                ", paymentStatus=" + paymentStatus +
                ", paymentStatusText='" + paymentStatusText + '\'' +
                ", operatorId=" + operatorId +
                ", operatorName='" + operatorName + '\'' +
                ", description='" + description + '\'' +
                ", receiptNo='" + receiptNo + '\'' +
                ", invoiceNo='" + invoiceNo + '\'' +
                ", status=" + getStatus() +
                ", statusText='" + getStatusText() + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}
