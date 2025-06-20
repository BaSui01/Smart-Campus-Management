package com.campus.interfaces.rest.dto.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * 基础响应DTO
 * 包含通用的响应字段
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-19
 */
@Schema(description = "基础响应数据")
public abstract class BaseResponse {

    /**
     * 记录ID
     */
    @Schema(description = "记录ID", example = "1")
    private Long id;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2025-06-19 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2025-06-19 15:45:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 创建者ID
     */
    @Schema(description = "创建者ID", example = "1")
    private Long createdBy;

    /**
     * 更新者ID
     */
    @Schema(description = "更新者ID", example = "1")
    private Long updatedBy;

    /**
     * 创建者姓名
     */
    @Schema(description = "创建者姓名", example = "张三")
    private String createdByName;

    /**
     * 更新者姓名
     */
    @Schema(description = "更新者姓名", example = "李四")
    private String updatedByName;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /**
     * 状态文本
     */
    @Schema(description = "状态文本", example = "正常")
    private String statusText;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remarks;

    // 构造函数
    public BaseResponse() {}

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getUpdatedByName() {
        return updatedByName;
    }

    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", createdByName='" + createdByName + '\'' +
                ", updatedByName='" + updatedByName + '\'' +
                ", status=" + status +
                ", statusText='" + statusText + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
