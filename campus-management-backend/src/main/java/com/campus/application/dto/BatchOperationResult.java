package com.campus.application.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;

/**
 * 批量操作结果DTO
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchOperationResult {
    
    /**
     * 总数量
     */
    private int totalCount;
    
    /**
     * 成功数量
     */
    private int successCount;
    
    /**
     * 失败数量
     */
    private int failureCount;
    
    /**
     * 跳过数量
     */
    private int skipCount;
    
    /**
     * 是否全部成功
     */
    private boolean allSuccess;
    
    /**
     * 操作消息
     */
    private String message;
    
    /**
     * 成功的项目列表
     */
    @Builder.Default
    private List<BatchOperationItem> successItems = new ArrayList<>();
    
    /**
     * 失败的项目列表
     */
    @Builder.Default
    private List<BatchOperationItem> failureItems = new ArrayList<>();
    
    /**
     * 跳过的项目列表
     */
    @Builder.Default
    private List<BatchOperationItem> skipItems = new ArrayList<>();
    
    /**
     * 批量操作项目
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchOperationItem {
        /**
         * 项目ID
         */
        private Object id;
        
        /**
         * 项目数据
         */
        private Object data;
        
        /**
         * 操作结果
         */
        private String result;
        
        /**
         * 错误消息
         */
        private String errorMessage;
        
        /**
         * 错误码
         */
        private String errorCode;
    }
    
    /**
     * 创建成功结果
     */
    public static BatchOperationResult success(int totalCount, int successCount) {
        return BatchOperationResult.builder()
                .totalCount(totalCount)
                .successCount(successCount)
                .failureCount(0)
                .skipCount(0)
                .allSuccess(true)
                .message("批量操作全部成功")
                .build();
    }
    
    /**
     * 创建部分成功结果
     */
    public static BatchOperationResult partialSuccess(int totalCount, int successCount, 
                                                     int failureCount, int skipCount) {
        return BatchOperationResult.builder()
                .totalCount(totalCount)
                .successCount(successCount)
                .failureCount(failureCount)
                .skipCount(skipCount)
                .allSuccess(false)
                .message(String.format("批量操作完成：成功%d，失败%d，跳过%d", 
                        successCount, failureCount, skipCount))
                .build();
    }
    
    /**
     * 创建失败结果
     */
    public static BatchOperationResult failure(int totalCount, String message) {
        return BatchOperationResult.builder()
                .totalCount(totalCount)
                .successCount(0)
                .failureCount(totalCount)
                .skipCount(0)
                .allSuccess(false)
                .message("批量操作失败：" + message)
                .build();
    }
    
    /**
     * 添加成功项目
     */
    public void addSuccessItem(Object id, Object data, String result) {
        this.successItems.add(BatchOperationItem.builder()
                .id(id)
                .data(data)
                .result(result)
                .build());
    }
    
    /**
     * 添加失败项目
     */
    public void addFailureItem(Object id, Object data, String errorMessage, String errorCode) {
        this.failureItems.add(BatchOperationItem.builder()
                .id(id)
                .data(data)
                .errorMessage(errorMessage)
                .errorCode(errorCode)
                .build());
    }
    
    /**
     * 添加跳过项目
     */
    public void addSkipItem(Object id, Object data, String reason) {
        this.skipItems.add(BatchOperationItem.builder()
                .id(id)
                .data(data)
                .result(reason)
                .build());
    }
    
    /**
     * 计算统计信息
     */
    public void calculateStats() {
        this.successCount = this.successItems.size();
        this.failureCount = this.failureItems.size();
        this.skipCount = this.skipItems.size();
        this.totalCount = this.successCount + this.failureCount + this.skipCount;
        this.allSuccess = this.failureCount == 0 && this.skipCount == 0;
        
        if (this.allSuccess) {
            this.message = "批量操作全部成功";
        } else {
            this.message = String.format("批量操作完成：成功%d，失败%d，跳过%d", 
                    this.successCount, this.failureCount, this.skipCount);
        }
    }
}
