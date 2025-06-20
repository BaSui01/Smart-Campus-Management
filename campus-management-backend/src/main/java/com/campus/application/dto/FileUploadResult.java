package com.campus.application.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 文件上传结果DTO
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResult {
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 文件ID
     */
    private Long fileId;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件URL
     */
    private String fileUrl;
    
    /**
     * 文件大小
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * MD5值
     */
    private String md5Hash;
    
    /**
     * 错误码
     */
    private String errorCode;
    
    /**
     * 创建成功结果
     * 
     * @param fileId 文件ID
     * @param fileName 文件名
     * @param fileUrl 文件URL
     * @param fileSize 文件大小
     * @param fileType 文件类型
     * @param md5Hash MD5值
     * @return 成功结果
     */
    public static FileUploadResult success(Long fileId, String fileName, String fileUrl, 
                                         Long fileSize, String fileType, String md5Hash) {
        return FileUploadResult.builder()
                .success(true)
                .message("文件上传成功")
                .fileId(fileId)
                .fileName(fileName)
                .fileUrl(fileUrl)
                .fileSize(fileSize)
                .fileType(fileType)
                .md5Hash(md5Hash)
                .build();
    }
    
    /**
     * 创建失败结果
     * 
     * @param message 错误消息
     * @param errorCode 错误码
     * @return 失败结果
     */
    public static FileUploadResult failure(String message, String errorCode) {
        return FileUploadResult.builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }
}
