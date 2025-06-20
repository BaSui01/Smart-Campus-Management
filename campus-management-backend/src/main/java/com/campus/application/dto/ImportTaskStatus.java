package com.campus.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 导入任务状态DTO
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImportTaskStatus {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务状态
     * PENDING - 等待中
     * RUNNING - 执行中
     * SUCCESS - 成功
     * FAILED - 失败
     * CANCELLED - 已取消
     */
    private String status;

    /**
     * 任务消息
     */
    private String message;

    /**
     * 总记录数
     */
    private Integer totalRecords;

    /**
     * 已处理记录数
     */
    private Integer processedRecords;

    /**
     * 成功记录数
     */
    private Integer successRecords;

    /**
     * 失败记录数
     */
    private Integer failedRecords;

    /**
     * 进度百分比
     */
    private Double progress;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 错误详情
     */
    private String errorDetails;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 导入类型
     */
    private String importType;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // ================================
    // 业务方法
    // ================================

    /**
     * 检查任务是否完成
     */
    public boolean isCompleted() {
        return "SUCCESS".equals(status) || "FAILED".equals(status) || "CANCELLED".equals(status);
    }

    /**
     * 检查任务是否成功
     */
    public boolean isSuccess() {
        return "SUCCESS".equals(status);
    }

    /**
     * 检查任务是否失败
     */
    public boolean isFailed() {
        return "FAILED".equals(status);
    }

    /**
     * 检查任务是否正在运行
     */
    public boolean isRunning() {
        return "RUNNING".equals(status);
    }

    /**
     * 检查任务是否等待中
     */
    public boolean isPending() {
        return "PENDING".equals(status);
    }

    /**
     * 检查任务是否已取消
     */
    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }

    /**
     * 计算进度百分比
     */
    public Double calculateProgress() {
        if (totalRecords == null || totalRecords == 0) {
            return 0.0;
        }
        if (processedRecords == null) {
            return 0.0;
        }
        return (double) processedRecords / totalRecords * 100;
    }

    /**
     * 获取状态描述
     */
    public String getStatusDescription() {
        return switch (status) {
            case "PENDING" -> "等待中";
            case "RUNNING" -> "执行中";
            case "SUCCESS" -> "成功";
            case "FAILED" -> "失败";
            case "CANCELLED" -> "已取消";
            default -> "未知状态";
        };
    }

    /**
     * 获取执行时长（毫秒）
     */
    public Long getDuration() {
        if (startTime == null) {
            return null;
        }
        LocalDateTime end = endTime != null ? endTime : LocalDateTime.now();
        return java.time.Duration.between(startTime, end).toMillis();
    }

    /**
     * 获取执行时长描述
     */
    public String getDurationDescription() {
        Long duration = getDuration();
        if (duration == null) {
            return "未知";
        }
        
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format("%d小时%d分钟%d秒", hours, minutes % 60, seconds % 60);
        } else if (minutes > 0) {
            return String.format("%d分钟%d秒", minutes, seconds % 60);
        } else {
            return String.format("%d秒", seconds);
        }
    }

    /**
     * 获取成功率
     */
    public Double getSuccessRate() {
        if (processedRecords == null || processedRecords == 0) {
            return 0.0;
        }
        if (successRecords == null) {
            return 0.0;
        }
        return (double) successRecords / processedRecords * 100;
    }

    /**
     * 获取失败率
     */
    public Double getFailureRate() {
        if (processedRecords == null || processedRecords == 0) {
            return 0.0;
        }
        if (failedRecords == null) {
            return 0.0;
        }
        return (double) failedRecords / processedRecords * 100;
    }

    /**
     * 获取任务摘要
     */
    public String getSummary() {
        if (isCompleted()) {
            return String.format("任务%s，共处理%d条记录，成功%d条，失败%d条，耗时%s",
                    getStatusDescription(),
                    processedRecords != null ? processedRecords : 0,
                    successRecords != null ? successRecords : 0,
                    failedRecords != null ? failedRecords : 0,
                    getDurationDescription());
        } else {
            return String.format("任务%s，已处理%d/%d条记录，进度%.1f%%",
                    getStatusDescription(),
                    processedRecords != null ? processedRecords : 0,
                    totalRecords != null ? totalRecords : 0,
                    calculateProgress());
        }
    }
}
