package com.campus.application.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * 数据导入结果DTO
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResult {
    
    /**
     * 导入是否成功
     */
    private boolean success;
    
    /**
     * 导入任务ID
     */
    private String taskId;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件大小
     */
    private Long fileSize;
    
    /**
     * 总行数
     */
    private int totalRows;
    
    /**
     * 成功导入行数
     */
    private int successRows;
    
    /**
     * 失败行数
     */
    private int failureRows;
    
    /**
     * 跳过行数
     */
    private int skipRows;
    
    /**
     * 导入开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 导入结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 导入耗时（毫秒）
     */
    private Long duration;
    
    /**
     * 导入消息
     */
    private String message;
    
    /**
     * 错误详情列表
     */
    @Builder.Default
    private List<ImportError> errors = new ArrayList<>();
    
    /**
     * 警告列表
     */
    @Builder.Default
    private List<String> warnings = new ArrayList<>();
    
    /**
     * 导入统计信息
     */
    private ImportStatistics statistics;
    
    /**
     * 导入错误详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportError {
        /**
         * 行号
         */
        private int rowNumber;
        
        /**
         * 列名
         */
        private String columnName;
        
        /**
         * 错误值
         */
        private String errorValue;
        
        /**
         * 错误消息
         */
        private String errorMessage;
        
        /**
         * 错误类型
         */
        private String errorType;
        
        /**
         * 原始数据
         */
        private Object originalData;
    }
    
    /**
     * 导入统计信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportStatistics {
        /**
         * 处理速度（行/秒）
         */
        private double processingSpeed;
        
        /**
         * 成功率
         */
        private double successRate;
        
        /**
         * 数据质量评分
         */
        private double dataQualityScore;
        
        /**
         * 重复数据数量
         */
        private int duplicateCount;
        
        /**
         * 空值数量
         */
        private int nullValueCount;
        
        /**
         * 格式错误数量
         */
        private int formatErrorCount;
        
        /**
         * 业务规则违反数量
         */
        private int businessRuleViolationCount;
    }
    
    /**
     * 创建成功结果
     */
    public static ImportResult success(String taskId, String fileName, int totalRows, int successRows) {
        return ImportResult.builder()
                .success(true)
                .taskId(taskId)
                .fileName(fileName)
                .totalRows(totalRows)
                .successRows(successRows)
                .failureRows(0)
                .skipRows(0)
                .message("数据导入成功")
                .build();
    }
    
    /**
     * 创建部分成功结果
     */
    public static ImportResult partialSuccess(String taskId, String fileName, 
                                            int totalRows, int successRows, 
                                            int failureRows, int skipRows) {
        return ImportResult.builder()
                .success(failureRows == 0)
                .taskId(taskId)
                .fileName(fileName)
                .totalRows(totalRows)
                .successRows(successRows)
                .failureRows(failureRows)
                .skipRows(skipRows)
                .message(String.format("数据导入完成：成功%d行，失败%d行，跳过%d行", 
                        successRows, failureRows, skipRows))
                .build();
    }
    
    /**
     * 创建失败结果
     */
    public static ImportResult failure(String taskId, String fileName, String message) {
        return ImportResult.builder()
                .success(false)
                .taskId(taskId)
                .fileName(fileName)
                .totalRows(0)
                .successRows(0)
                .failureRows(0)
                .skipRows(0)
                .message("数据导入失败：" + message)
                .build();
    }
    
    /**
     * 添加错误
     */
    public void addError(int rowNumber, String columnName, String errorValue, 
                        String errorMessage, String errorType, Object originalData) {
        this.errors.add(ImportError.builder()
                .rowNumber(rowNumber)
                .columnName(columnName)
                .errorValue(errorValue)
                .errorMessage(errorMessage)
                .errorType(errorType)
                .originalData(originalData)
                .build());
    }
    
    /**
     * 添加警告
     */
    public void addWarning(String warning) {
        this.warnings.add(warning);
    }
    
    /**
     * 计算统计信息
     */
    public void calculateStatistics() {
        if (this.startTime != null && this.endTime != null) {
            this.duration = java.time.Duration.between(this.startTime, this.endTime).toMillis();
            
            if (this.duration > 0) {
                this.statistics = ImportStatistics.builder()
                        .processingSpeed((double) this.totalRows / (this.duration / 1000.0))
                        .successRate(this.totalRows > 0 ? (double) this.successRows / this.totalRows * 100 : 0)
                        .dataQualityScore(calculateDataQualityScore())
                        .duplicateCount(countErrorsByType("DUPLICATE"))
                        .nullValueCount(countErrorsByType("NULL_VALUE"))
                        .formatErrorCount(countErrorsByType("FORMAT_ERROR"))
                        .businessRuleViolationCount(countErrorsByType("BUSINESS_RULE"))
                        .build();
            }
        }
    }
    
    /**
     * 计算数据质量评分
     */
    private double calculateDataQualityScore() {
        if (this.totalRows == 0) {
            return 100.0;
        }
        
        double errorRate = (double) this.failureRows / this.totalRows;
        double warningRate = (double) this.warnings.size() / this.totalRows;
        
        return Math.max(0, 100 - (errorRate * 50 + warningRate * 20));
    }
    
    /**
     * 按类型统计错误数量
     */
    private int countErrorsByType(String errorType) {
        return (int) this.errors.stream()
                .filter(error -> errorType.equals(error.getErrorType()))
                .count();
    }
}
