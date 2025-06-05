package com.campus.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 缴费项目实体类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-05
 */
@Entity
@Table(name = "tb_fee_item")
public class FeeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "项目名称不能为空")
    @Size(max = 100, message = "项目名称长度不能超过100个字符")
    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @NotBlank(message = "项目编码不能为空")
    @Size(max = 50, message = "项目编码长度不能超过50个字符")
    @Column(name = "item_code", nullable = false, unique = true, length = 50)
    private String itemCode;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Size(max = 20, message = "费用类型长度不能超过20个字符")
    @Column(name = "fee_type", length = 20)
    private String feeType;

    @Size(max = 50, message = "适用年级长度不能超过50个字符")
    @Column(name = "applicable_grade", length = 50)
    private String applicableGrade;

    @Column(name = "due_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Size(max = 500, message = "项目描述长度不能超过500个字符")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;

    @Column(name = "created_time", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    @Column(name = "deleted", columnDefinition = "TINYINT DEFAULT 0")
    private Integer deleted = 0;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }

    // 构造函数
    public FeeItem() {}

    public FeeItem(String itemName, String itemCode, BigDecimal amount, String feeType) {
        this.itemName = itemName;
        this.itemCode = itemCode;
        this.amount = amount;
        this.feeType = feeType;
    }

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
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
                ", dueDate=" + dueDate +
                ", status=" + status +
                ", createdTime=" + createdTime +
                '}';
    }
}
