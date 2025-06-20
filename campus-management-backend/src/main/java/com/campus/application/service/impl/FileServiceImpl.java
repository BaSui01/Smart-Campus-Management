package com.campus.application.service.impl;

import com.campus.application.dto.FileUploadResult;
import com.campus.application.service.FileService;
import com.campus.domain.entity.system.FileInfo;
import com.campus.domain.repository.FileInfoRepository;
import com.campus.infrastructure.config.FileUploadConfig;
import com.campus.shared.util.FileUtil;
import com.campus.shared.util.SecurityUtil;
import com.campus.shared.util.WebUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 文件服务实现类
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileInfoRepository fileInfoRepository;
    private final FileUploadConfig fileUploadConfig;

    @Override
    @Transactional
    public FileUploadResult uploadFile(MultipartFile file, String businessType, String businessId) {
        try {
            // 1. 文件验证
            validateFile(file);
            
            // 2. 生成存储文件名
            String originalFileName = file.getOriginalFilename();
            String storedFileName = FileUtil.generateUniqueFileName(originalFileName);
            
            // 3. 创建存储路径
            String relativePath = createStoragePath(businessType, businessId);
            Path fullPath = Paths.get(fileUploadConfig.getUploadPath(), relativePath);
            Files.createDirectories(fullPath);
            
            // 4. 保存文件
            Path filePath = fullPath.resolve(storedFileName);
            file.transferTo(filePath.toFile());
            
            // 5. 计算文件哈希
            File savedFile = filePath.toFile();
            String md5Hash = FileUtil.calculateMD5(savedFile);
            String sha256Hash = FileUtil.calculateSHA256(savedFile);
            
            // 6. 检查文件是否已存在
            Optional<FileInfo> existingFile = fileInfoRepository.findByMd5HashAndDeleted(md5Hash, 0);
            if (existingFile.isPresent()) {
                // 删除刚上传的重复文件
                Files.deleteIfExists(filePath);
                FileInfo existing = existingFile.get();
                return FileUploadResult.success(
                    existing.getId(),
                    existing.getFileName(),
                    generateFileUrl(existing),
                    existing.getFileSize(),
                    existing.getFileType(),
                    existing.getMd5Hash()
                );
            }
            
            // 7. 保存文件信息
            FileInfo fileInfo = createFileInfo(file, storedFileName, relativePath + "/" + storedFileName, 
                                             md5Hash, sha256Hash, businessType, businessId);
            fileInfo = fileInfoRepository.save(fileInfo);
            
            // 8. 返回结果
            return FileUploadResult.success(
                fileInfo.getId(),
                fileInfo.getFileName(),
                generateFileUrl(fileInfo),
                fileInfo.getFileSize(),
                fileInfo.getFileType(),
                fileInfo.getMd5Hash()
            );
                    
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return FileUploadResult.failure("文件上传失败：" + e.getMessage(), "UPLOAD_FAILED");
        }
    }

    @Override
    @Transactional
    public List<FileUploadResult> batchUploadFiles(List<MultipartFile> files, String businessType, String businessId) {
        List<FileUploadResult> results = new ArrayList<>();
        
        for (MultipartFile file : files) {
            FileUploadResult result = uploadFile(file, businessType, businessId);
            results.add(result);
        }
        
        return results;
    }

    @Override
    public void downloadFile(Long fileId, HttpServletResponse response) {
        try {
            FileInfo fileInfo = getFileInfo(fileId);
            if (fileInfo == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // 构建文件路径
            Path filePath = Paths.get(fileUploadConfig.getUploadPath(), fileInfo.getFilePath());
            File file = filePath.toFile();
            
            if (!file.exists()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // 设置响应头
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", 
                "attachment; filename=\"" + fileInfo.getFileName() + "\"");
            response.setContentLengthLong(file.length());
            
            // 写入文件内容
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }
            
            // 更新下载次数
            updateDownloadCount(fileId);
            
        } catch (Exception e) {
            log.error("文件下载失败: {}", fileId, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void previewFile(Long fileId, HttpServletResponse response) {
        try {
            FileInfo fileInfo = getFileInfo(fileId);
            if (fileInfo == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // 构建文件路径
            Path filePath = Paths.get(fileUploadConfig.getUploadPath(), fileInfo.getFilePath());
            File file = filePath.toFile();
            
            if (!file.exists()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // 设置响应头
            response.setContentType(fileInfo.getMimeType());
            response.setHeader("Content-Disposition", 
                "inline; filename=\"" + fileInfo.getFileName() + "\"");
            response.setContentLengthLong(file.length());
            
            // 写入文件内容
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }
            
            // 更新查看次数
            updateViewCount(fileId);
            
        } catch (Exception e) {
            log.error("文件预览失败: {}", fileId, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public boolean deleteFile(Long fileId) {
        try {
            Optional<FileInfo> fileInfoOpt = fileInfoRepository.findById(fileId);
            if (fileInfoOpt.isEmpty()) {
                return false;
            }
            
            FileInfo fileInfo = fileInfoOpt.get();
            
            // 软删除文件记录
            fileInfo.setDeleted(1);
            fileInfo.setUpdatedAt(LocalDateTime.now());
            fileInfoRepository.save(fileInfo);
            
            // 可选：物理删除文件
            // Path filePath = Paths.get(fileUploadConfig.getUploadPath(), fileInfo.getFilePath());
            // Files.deleteIfExists(filePath);
            
            return true;
        } catch (Exception e) {
            log.error("删除文件失败: {}", fileId, e);
            return false;
        }
    }

    @Override
    public List<FileInfo> getFilesByBusiness(String businessType, String businessId) {
        return fileInfoRepository.findByBusinessTypeAndBusinessIdAndDeletedOrderByCreatedAtDesc(
            businessType, businessId, 0);
    }

    @Override
    public FileInfo getFileInfo(Long fileId) {
        return fileInfoRepository.findById(fileId)
            .filter(file -> file.getDeleted() == 0)
            .orElse(null);
    }

    @Override
    public String getFileAccessUrl(Long fileId, Integer expireMinutes) {
        FileInfo fileInfo = getFileInfo(fileId);
        if (fileInfo == null) {
            return null;
        }
        
        // 这里可以实现临时访问URL的生成逻辑
        // 例如使用JWT token或者临时访问码
        return generateFileUrl(fileInfo);
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }
        
        String fileName = file.getOriginalFilename();
        if (!StringUtils.hasText(fileName)) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        
        if (!FileUtil.isFileNameSafe(fileName)) {
            throw new IllegalArgumentException("文件名包含非法字符");
        }
        
        String fileExtension = FileUtil.getFileExtension(fileName);
        if (!FileUtil.isAllowedFileType(fileExtension)) {
            throw new IllegalArgumentException("不支持的文件类型：" + fileExtension);
        }
        
        if (FileUtil.isForbiddenFileType(fileExtension)) {
            throw new IllegalArgumentException("禁止上传的文件类型：" + fileExtension);
        }
        
        // 检查文件大小
        long maxSize = parseFileSize(fileUploadConfig.getMaxFileSize());
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("文件大小超过限制：" + FileUtil.formatFileSize(maxSize));
        }
    }

    /**
     * 创建存储路径
     */
    private String createStoragePath(String businessType, String businessId) {
        LocalDate now = LocalDate.now();
        return String.format("%s/%s/%d/%02d/%02d", 
                businessType != null ? businessType : "general",
                businessId != null ? businessId : "common",
                now.getYear(), now.getMonthValue(), now.getDayOfMonth());
    }

    /**
     * 创建文件信息对象
     */
    private FileInfo createFileInfo(MultipartFile file, String storedFileName, String filePath,
                                   String md5Hash, String sha256Hash, String businessType, String businessId) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(file.getOriginalFilename());
        fileInfo.setStoredFileName(storedFileName);
        fileInfo.setFilePath(filePath);
        fileInfo.setFileType(FileUtil.getFileExtension(file.getOriginalFilename()));
        fileInfo.setFileSize(file.getSize());
        fileInfo.setMd5Hash(md5Hash);
        fileInfo.setSha256Hash(sha256Hash);
        fileInfo.setMimeType(file.getContentType());
        fileInfo.setBusinessType(businessType);
        fileInfo.setBusinessId(businessId);
        fileInfo.setUploadUserId(SecurityUtil.getCurrentUserId());
        fileInfo.setUploadIp(WebUtil.getClientIp());
        fileInfo.setUploadUserAgent(WebUtil.getUserAgent());
        fileInfo.setFileStatus(FileInfo.FileStatus.NORMAL.getCode());
        fileInfo.setVirusScanStatus(FileInfo.VirusScanStatus.NOT_SCANNED.getCode());
        fileInfo.setStorageType(FileInfo.StorageType.LOCAL.getCode());
        
        return fileInfo;
    }

    /**
     * 生成文件访问URL
     */
    private String generateFileUrl(FileInfo fileInfo) {
        return fileUploadConfig.getStorage().getLocal().getUrlPrefix() + fileInfo.getId();
    }

    /**
     * 更新下载次数
     */
    private void updateDownloadCount(Long fileId) {
        try {
            Optional<FileInfo> fileInfoOpt = fileInfoRepository.findById(fileId);
            if (fileInfoOpt.isPresent()) {
                FileInfo fileInfo = fileInfoOpt.get();
                fileInfo.setDownloadCount(fileInfo.getDownloadCount() + 1);
                fileInfo.setLastAccessTime(LocalDateTime.now());
                fileInfoRepository.save(fileInfo);
            }
        } catch (Exception e) {
            log.warn("更新下载次数失败: {}", fileId, e);
        }
    }

    /**
     * 更新查看次数
     */
    private void updateViewCount(Long fileId) {
        try {
            Optional<FileInfo> fileInfoOpt = fileInfoRepository.findById(fileId);
            if (fileInfoOpt.isPresent()) {
                FileInfo fileInfo = fileInfoOpt.get();
                fileInfo.setViewCount(fileInfo.getViewCount() + 1);
                fileInfo.setLastAccessTime(LocalDateTime.now());
                fileInfoRepository.save(fileInfo);
            }
        } catch (Exception e) {
            log.warn("更新查看次数失败: {}", fileId, e);
        }
    }

    /**
     * 解析文件大小
     */
    private long parseFileSize(String sizeStr) {
        if (!StringUtils.hasText(sizeStr)) {
            return 100 * 1024 * 1024; // 默认100MB
        }
        
        sizeStr = sizeStr.toUpperCase().trim();
        long multiplier = 1;
        
        if (sizeStr.endsWith("KB")) {
            multiplier = 1024;
            sizeStr = sizeStr.substring(0, sizeStr.length() - 2);
        } else if (sizeStr.endsWith("MB")) {
            multiplier = 1024 * 1024;
            sizeStr = sizeStr.substring(0, sizeStr.length() - 2);
        } else if (sizeStr.endsWith("GB")) {
            multiplier = 1024 * 1024 * 1024;
            sizeStr = sizeStr.substring(0, sizeStr.length() - 2);
        }
        
        try {
            return Long.parseLong(sizeStr.trim()) * multiplier;
        } catch (NumberFormatException e) {
            return 100 * 1024 * 1024; // 默认100MB
        }
    }
}
