package com.campus.shared.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

/**
 * 文件工具类
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Slf4j
public class FileUtil {

    /**
     * 允许的文件类型
     */
    private static final List<String> ALLOWED_TYPES = List.of(
        "jpg", "jpeg", "png", "gif", "bmp",
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
        "txt", "zip", "rar", "7z", "mp4", "avi", "mov"
    );

    /**
     * 禁止的文件类型
     */
    private static final List<String> FORBIDDEN_TYPES = List.of(
        "exe", "bat", "cmd", "com", "scr", "vbs", "js", "jar"
    );

    /**
     * 获取文件扩展名
     * 
     * @param fileName 文件名
     * @return 扩展名
     */
    public static String getFileExtension(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 检查是否为允许的文件类型
     * 
     * @param fileExtension 文件扩展名
     * @return 是否允许
     */
    public static boolean isAllowedFileType(String fileExtension) {
        if (!StringUtils.hasText(fileExtension)) {
            return false;
        }
        return ALLOWED_TYPES.contains(fileExtension.toLowerCase());
    }

    /**
     * 检查是否为禁止的文件类型
     * 
     * @param fileExtension 文件扩展名
     * @return 是否禁止
     */
    public static boolean isForbiddenFileType(String fileExtension) {
        if (!StringUtils.hasText(fileExtension)) {
            return false;
        }
        return FORBIDDEN_TYPES.contains(fileExtension.toLowerCase());
    }

    /**
     * 生成唯一文件名
     * 
     * @param originalFileName 原始文件名
     * @return 唯一文件名
     */
    public static String generateUniqueFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return StringUtils.hasText(extension) ? uuid + "." + extension : uuid;
    }

    /**
     * 计算文件MD5值
     * 
     * @param file 文件
     * @return MD5值
     */
    public static String calculateMD5(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int bytesRead;
            
            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("计算文件MD5失败", e);
            throw new RuntimeException("计算文件MD5失败", e);
        }
    }

    /**
     * 计算文件SHA256值
     * 
     * @param file 文件
     * @return SHA256值
     */
    public static String calculateSHA256(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int bytesRead;
            
            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("计算文件SHA256失败", e);
            throw new RuntimeException("计算文件SHA256失败", e);
        }
    }

    /**
     * 获取文件MIME类型
     * 
     * @param file 文件
     * @return MIME类型
     */
    public static String getMimeType(File file) {
        try {
            Path path = file.toPath();
            return Files.probeContentType(path);
        } catch (IOException e) {
            log.warn("获取文件MIME类型失败: {}", file.getName(), e);
            return "application/octet-stream";
        }
    }

    /**
     * 格式化文件大小
     * 
     * @param size 文件大小（字节）
     * @return 格式化后的大小
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * 验证文件名安全性
     * 
     * @param fileName 文件名
     * @return 是否安全
     */
    public static boolean isFileNameSafe(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return false;
        }
        
        // 检查文件名长度
        if (fileName.length() > 255) {
            return false;
        }
        
        // 检查是否包含危险字符
        String[] dangerousChars = {"..", "/", "\\", ":", "*", "?", "\"", "<", ">", "|"};
        for (String dangerousChar : dangerousChars) {
            if (fileName.contains(dangerousChar)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * 创建目录
     * 
     * @param dirPath 目录路径
     * @return 是否创建成功
     */
    public static boolean createDirectories(String dirPath) {
        try {
            Path path = Path.of(dirPath);
            Files.createDirectories(path);
            return true;
        } catch (IOException e) {
            log.error("创建目录失败: {}", dirPath, e);
            return false;
        }
    }

    /**
     * 删除文件
     * 
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public static boolean deleteFile(String filePath) {
        try {
            Path path = Path.of(filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("删除文件失败: {}", filePath, e);
            return false;
        }
    }

    /**
     * 检查文件是否存在
     * 
     * @param filePath 文件路径
     * @return 是否存在
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Path.of(filePath));
    }
}
