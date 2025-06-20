package com.campus.domain.entity.system;

import com.campus.domain.entity.infrastructure.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文件信息实体类
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_file_info")
public class FileInfo extends BaseEntity {

    /**
     * 原始文件名
     */
    @NotBlank(message = "文件名不能为空")
    @Size(max = 255, message = "文件名长度不能超过255个字符")
    @Column(name = "file_name", nullable = false)
    private String fileName;

    /**
     * 存储文件名
     */
    @NotBlank(message = "存储文件名不能为空")
    @Size(max = 255, message = "存储文件名长度不能超过255个字符")
    @Column(name = "stored_file_name", nullable = false)
    private String storedFileName;

    /**
     * 文件路径
     */
    @NotBlank(message = "文件路径不能为空")
    @Size(max = 500, message = "文件路径长度不能超过500个字符")
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    /**
     * 文件类型
     */
    @NotBlank(message = "文件类型不能为空")
    @Size(max = 50, message = "文件类型长度不能超过50个字符")
    @Column(name = "file_type", nullable = false, length = 50)
    private String fileType;

    /**
     * 文件大小（字节）
     */
    @NotNull(message = "文件大小不能为空")
    @Min(value = 0, message = "文件大小不能为负数")
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    /**
     * 文件MD5值
     */
    @NotBlank(message = "文件MD5不能为空")
    @Size(min = 32, max = 32, message = "MD5长度必须为32位")
    @Column(name = "md5_hash", nullable = false, length = 32)
    private String md5Hash;

    /**
     * 文件SHA256值
     */
    @Size(min = 64, max = 64, message = "SHA256长度必须为64位")
    @Column(name = "sha256_hash", length = 64)
    private String sha256Hash;

    /**
     * MIME类型
     */
    @Size(max = 100, message = "MIME类型长度不能超过100个字符")
    @Column(name = "mime_type", length = 100)
    private String mimeType;

    /**
     * 业务类型
     */
    @Size(max = 50, message = "业务类型长度不能超过50个字符")
    @Column(name = "business_type", length = 50)
    private String businessType;

    /**
     * 业务ID
     */
    @Size(max = 100, message = "业务ID长度不能超过100个字符")
    @Column(name = "business_id", length = 100)
    private String businessId;

    /**
     * 上传用户ID
     */
    @NotNull(message = "上传用户ID不能为空")
    @Column(name = "upload_user_id", nullable = false)
    private Long uploadUserId;

    /**
     * 上传IP
     */
    @Size(max = 50, message = "上传IP长度不能超过50个字符")
    @Column(name = "upload_ip", length = 50)
    private String uploadIp;

    /**
     * 上传用户代理
     */
    @Column(name = "upload_user_agent", columnDefinition = "TEXT")
    private String uploadUserAgent;

    /**
     * 下载次数
     */
    @Min(value = 0, message = "下载次数不能为负数")
    @Column(name = "download_count", columnDefinition = "INT DEFAULT 0")
    private Integer downloadCount = 0;

    /**
     * 查看次数
     */
    @Min(value = 0, message = "查看次数不能为负数")
    @Column(name = "view_count", columnDefinition = "INT DEFAULT 0")
    private Integer viewCount = 0;

    /**
     * 最后访问时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "last_access_time")
    private LocalDateTime lastAccessTime;

    /**
     * 文件状态：1-正常，2-隔离，3-删除
     */
    @Min(value = 1, message = "文件状态值无效")
    @Max(value = 3, message = "文件状态值无效")
    @Column(name = "file_status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer fileStatus = 1;

    /**
     * 病毒扫描状态：0-未扫描，1-安全，2-威胁
     */
    @Min(value = 0, message = "病毒扫描状态值无效")
    @Max(value = 2, message = "病毒扫描状态值无效")
    @Column(name = "virus_scan_status", columnDefinition = "TINYINT DEFAULT 0")
    private Integer virusScanStatus = 0;

    /**
     * 病毒扫描时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "virus_scan_time")
    private LocalDateTime virusScanTime;

    /**
     * 存储类型：local,oss,minio
     */
    @Size(max = 20, message = "存储类型长度不能超过20个字符")
    @Column(name = "storage_type", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'local'")
    private String storageType = "local";

    /**
     * 存储桶名称
     */
    @Size(max = 100, message = "存储桶名称长度不能超过100个字符")
    @Column(name = "storage_bucket", length = 100)
    private String storageBucket;

    // 文件状态枚举
    public enum FileStatus {
        NORMAL(1, "正常"),
        QUARANTINE(2, "隔离"),
        DELETED(3, "删除");

        private final int code;
        private final String description;

        FileStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }
        public String getDescription() { return description; }
    }

    // 病毒扫描状态枚举
    public enum VirusScanStatus {
        NOT_SCANNED(0, "未扫描"),
        SAFE(1, "安全"),
        THREAT(2, "威胁");

        private final int code;
        private final String description;

        VirusScanStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }
        public String getDescription() { return description; }
    }

    // 存储类型枚举
    public enum StorageType {
        LOCAL("local", "本地存储"),
        OSS("oss", "阿里云OSS"),
        MINIO("minio", "MinIO");

        private final String code;
        private final String description;

        StorageType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }
    }
}
