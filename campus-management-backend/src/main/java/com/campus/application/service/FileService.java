package com.campus.application.service;

import com.campus.application.dto.FileUploadResult;
import com.campus.domain.entity.system.FileInfo;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * 文件服务接口
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
public interface FileService {
    
    /**
     * 上传文件
     * 
     * @param file 文件
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @return 上传结果
     */
    FileUploadResult uploadFile(MultipartFile file, String businessType, String businessId);
    
    /**
     * 批量上传文件
     * 
     * @param files 文件列表
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @return 上传结果列表
     */
    List<FileUploadResult> batchUploadFiles(List<MultipartFile> files, String businessType, String businessId);
    
    /**
     * 下载文件
     * 
     * @param fileId 文件ID
     * @param response HTTP响应
     */
    void downloadFile(Long fileId, HttpServletResponse response);
    
    /**
     * 预览文件
     * 
     * @param fileId 文件ID
     * @param response HTTP响应
     */
    void previewFile(Long fileId, HttpServletResponse response);
    
    /**
     * 删除文件
     * 
     * @param fileId 文件ID
     * @return 是否删除成功
     */
    boolean deleteFile(Long fileId);
    
    /**
     * 获取业务相关文件
     * 
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @return 文件列表
     */
    List<FileInfo> getFilesByBusiness(String businessType, String businessId);
    
    /**
     * 获取文件信息
     * 
     * @param fileId 文件ID
     * @return 文件信息
     */
    FileInfo getFileInfo(Long fileId);
    
    /**
     * 获取文件访问URL
     * 
     * @param fileId 文件ID
     * @param expireMinutes 过期时间（分钟）
     * @return 访问URL
     */
    String getFileAccessUrl(Long fileId, Integer expireMinutes);
}
