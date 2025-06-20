package com.campus.application.service.common;

import com.campus.shared.exception.BusinessException;
import com.campus.shared.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传服务
 * 提供安全的文件上传功能，包括文件类型验证、大小限制、路径安全等
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-18
 */
@Service
public class FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    @Value("${campus.upload.path:/opt/campus/uploads/}")
    private String uploadBasePath;

    @Value("${campus.upload.max-file-size:10485760}") // 10MB
    private long maxFileSize;

    // 允许的文件类型
    private static final String[] ALLOWED_IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "bmp"};
    private static final String[] ALLOWED_DOCUMENT_EXTENSIONS = {"pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"};
    private static final String[] ALLOWED_ARCHIVE_EXTENSIONS = {"zip", "rar", "7z"};
    private static final String[] ALLOWED_VIDEO_EXTENSIONS = {"mp4", "avi", "mov", "wmv"};

    /**
     * 上传头像文件
     */
    public Map<String, Object> uploadAvatar(MultipartFile file, Long userId) {
        try {
            logger.info("开始上传用户头像，用户ID: {}", userId);
            
            // 验证文件
            validateFile(file, ALLOWED_IMAGE_EXTENSIONS, 2 * 1024 * 1024); // 2MB限制
            
            // 生成文件路径
            String relativePath = generateFilePath("avatars", userId.toString(), file.getOriginalFilename());
            
            // 保存文件
            String savedPath = saveFile(file, relativePath);
            
            Map<String, Object> result = new HashMap<>();
            result.put("fileName", file.getOriginalFilename());
            result.put("filePath", savedPath);
            result.put("fileSize", file.getSize());
            result.put("fileType", file.getContentType());
            result.put("uploadTime", LocalDateTime.now());
            result.put("category", "avatar");
            
            logger.info("用户头像上传成功，路径: {}", savedPath);
            return result;
            
        } catch (Exception e) {
            logger.error("用户头像上传失败", e);
            throw new BusinessException("头像上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传作业文件
     */
    public Map<String, Object> uploadAssignmentFile(MultipartFile file, Long assignmentId, Long studentId) {
        try {
            logger.info("开始上传作业文件，作业ID: {}, 学生ID: {}", assignmentId, studentId);
            
            // 验证文件
            String[] allowedExtensions = combineArrays(ALLOWED_DOCUMENT_EXTENSIONS, ALLOWED_ARCHIVE_EXTENSIONS, ALLOWED_IMAGE_EXTENSIONS);
            validateFile(file, allowedExtensions, maxFileSize);
            
            // 生成文件路径
            String relativePath = generateFilePath("assignments", assignmentId + "/" + studentId, file.getOriginalFilename());
            
            // 保存文件
            String savedPath = saveFile(file, relativePath);
            
            Map<String, Object> result = new HashMap<>();
            result.put("fileName", file.getOriginalFilename());
            result.put("filePath", savedPath);
            result.put("fileSize", file.getSize());
            result.put("fileType", file.getContentType());
            result.put("uploadTime", LocalDateTime.now());
            result.put("category", "assignment");
            result.put("assignmentId", assignmentId);
            result.put("studentId", studentId);
            
            logger.info("作业文件上传成功，路径: {}", savedPath);
            return result;
            
        } catch (Exception e) {
            logger.error("作业文件上传失败", e);
            throw new BusinessException("作业文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传课程资料
     */
    public Map<String, Object> uploadCourseMaterial(MultipartFile file, Long courseId, String materialType) {
        try {
            logger.info("开始上传课程资料，课程ID: {}, 类型: {}", courseId, materialType);
            
            // 验证文件
            String[] allowedExtensions;
            long sizeLimit;
            
            switch (materialType.toLowerCase()) {
                case "video":
                    allowedExtensions = ALLOWED_VIDEO_EXTENSIONS;
                    sizeLimit = 100 * 1024 * 1024; // 100MB
                    break;
                case "document":
                    allowedExtensions = ALLOWED_DOCUMENT_EXTENSIONS;
                    sizeLimit = 50 * 1024 * 1024; // 50MB
                    break;
                case "image":
                    allowedExtensions = ALLOWED_IMAGE_EXTENSIONS;
                    sizeLimit = 10 * 1024 * 1024; // 10MB
                    break;
                default:
                    allowedExtensions = combineArrays(ALLOWED_DOCUMENT_EXTENSIONS, ALLOWED_IMAGE_EXTENSIONS);
                    sizeLimit = maxFileSize;
            }
            
            validateFile(file, allowedExtensions, sizeLimit);
            
            // 生成文件路径
            String relativePath = generateFilePath("courses", courseId + "/" + materialType, file.getOriginalFilename());
            
            // 保存文件
            String savedPath = saveFile(file, relativePath);
            
            Map<String, Object> result = new HashMap<>();
            result.put("fileName", file.getOriginalFilename());
            result.put("filePath", savedPath);
            result.put("fileSize", file.getSize());
            result.put("fileType", file.getContentType());
            result.put("uploadTime", LocalDateTime.now());
            result.put("category", "course_material");
            result.put("courseId", courseId);
            result.put("materialType", materialType);
            
            logger.info("课程资料上传成功，路径: {}", savedPath);
            return result;
            
        } catch (Exception e) {
            logger.error("课程资料上传失败", e);
            throw new BusinessException("课程资料上传失败: " + e.getMessage());
        }
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file, String[] allowedExtensions, long maxSize) {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        
        // 检查文件名
        String fileName = file.getOriginalFilename();
        ValidationUtils.validateNotEmpty(fileName, "文件名");
        
        // 检查文件大小
        ValidationUtils.validateFileSize(file.getSize(), maxSize);
        
        // 检查文件类型
        ValidationUtils.validateFileType(fileName, allowedExtensions);
        
        // 检查文件内容类型
        String contentType = file.getContentType();
        if (contentType == null || contentType.trim().isEmpty()) {
            throw new BusinessException("无法确定文件类型");
        }
        
        // 安全检查：防止路径遍历攻击
        if (fileName != null && (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\"))) {
            throw new BusinessException("文件名包含非法字符");
        }
    }

    /**
     * 生成文件路径
     */
    private String generateFilePath(String category, String subPath, String originalFileName) {
        // 生成时间戳目录
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        
        // 生成唯一文件名
        String extension = getFileExtension(originalFileName);
        String uniqueFileName = UUID.randomUUID().toString() + "." + extension;
        
        // 组合路径
        return category + "/" + subPath + "/" + dateDir + "/" + uniqueFileName;
    }

    /**
     * 保存文件
     */
    private String saveFile(MultipartFile file, String relativePath) throws IOException {
        // 创建完整路径
        Path fullPath = Paths.get(uploadBasePath, relativePath);
        
        // 创建目录
        Files.createDirectories(fullPath.getParent());
        
        // 保存文件
        Files.copy(file.getInputStream(), fullPath, StandardCopyOption.REPLACE_EXISTING);
        
        logger.info("文件保存成功: {}", fullPath.toString());
        return relativePath;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 合并数组
     */
    private String[] combineArrays(String[]... arrays) {
        int totalLength = 0;
        for (String[] array : arrays) {
            totalLength += array.length;
        }
        
        String[] result = new String[totalLength];
        int currentIndex = 0;
        
        for (String[] array : arrays) {
            System.arraycopy(array, 0, result, currentIndex, array.length);
            currentIndex += array.length;
        }
        
        return result;
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(String relativePath) {
        try {
            Path fullPath = Paths.get(uploadBasePath, relativePath);
            boolean deleted = Files.deleteIfExists(fullPath);
            
            if (deleted) {
                logger.info("文件删除成功: {}", fullPath.toString());
            } else {
                logger.warn("文件不存在或删除失败: {}", fullPath.toString());
            }
            
            return deleted;
            
        } catch (IOException e) {
            logger.error("删除文件失败: " + relativePath, e);
            return false;
        }
    }

    /**
     * 检查文件是否存在
     */
    public boolean fileExists(String relativePath) {
        Path fullPath = Paths.get(uploadBasePath, relativePath);
        return Files.exists(fullPath);
    }

    /**
     * 获取文件信息
     */
    public Map<String, Object> getFileInfo(String relativePath) {
        try {
            Path fullPath = Paths.get(uploadBasePath, relativePath);
            
            if (!Files.exists(fullPath)) {
                throw new BusinessException("文件不存在");
            }
            
            Map<String, Object> info = new HashMap<>();
            info.put("fileName", fullPath.getFileName().toString());
            info.put("filePath", relativePath);
            info.put("fileSize", Files.size(fullPath));
            info.put("lastModified", Files.getLastModifiedTime(fullPath).toInstant());
            info.put("isDirectory", Files.isDirectory(fullPath));
            
            return info;
            
        } catch (IOException e) {
            logger.error("获取文件信息失败: " + relativePath, e);
            throw new BusinessException("获取文件信息失败: " + e.getMessage());
        }
    }
}
