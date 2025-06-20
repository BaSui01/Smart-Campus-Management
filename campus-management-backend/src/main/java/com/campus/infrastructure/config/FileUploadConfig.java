package com.campus.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 文件上传配置类
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "campus.file")
public class FileUploadConfig {

    /**
     * 文件上传路径
     */
    private String uploadPath = "/data/campus/uploads/";

    /**
     * 临时文件路径
     */
    private String tempPath = "/data/campus/temp/";

    /**
     * 最大文件大小
     */
    private String maxFileSize = "100MB";

    /**
     * 最大请求大小
     */
    private String maxRequestSize = "500MB";

    /**
     * 允许的文件类型
     */
    private List<String> allowedTypes = List.of(
        "jpg", "jpeg", "png", "gif", "bmp",
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
        "txt", "zip", "rar", "7z", "mp4", "avi", "mov"
    );

    /**
     * 禁止的文件类型
     */
    private List<String> forbiddenTypes = List.of(
        "exe", "bat", "cmd", "com", "scr", "vbs", "js", "jar"
    );

    /**
     * 存储配置
     */
    private Storage storage = new Storage();

    /**
     * 安全配置
     */
    private Security security = new Security();

    /**
     * 缓存配置
     */
    private Cache cache = new Cache();

    /**
     * 清理配置
     */
    private Cleanup cleanup = new Cleanup();

    @Data
    public static class Storage {
        private String type = "local";
        private Local local = new Local();
        private Oss oss = new Oss();
        private Minio minio = new Minio();

        @Data
        public static class Local {
            private String basePath = "/data/campus/uploads/";
            private String urlPrefix = "/files/";
        }

        @Data
        public static class Oss {
            private String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
            private String bucket = "campus-files";
            private String accessKeyId;
            private String accessKeySecret;
        }

        @Data
        public static class Minio {
            private String endpoint = "http://localhost:9000";
            private String bucket = "campus-files";
            private String accessKey;
            private String secretKey;
        }
    }

    @Data
    public static class Security {
        private boolean enableVirusScan = true;
        private int virusScanTimeout = 30000;
        private boolean enableContentCheck = true;
        private int maxFilenameLength = 255;
        private boolean enableWatermark = false;
    }

    @Data
    public static class Cache {
        private boolean enableFileCache = true;
        private int cacheExpireTime = 3600;
        private String maxCacheSize = "1GB";
    }

    @Data
    public static class Cleanup {
        private boolean enableAutoCleanup = true;
        private int tempFileExpire = 86400;
        private int deletedFileRetain = 2592000;
        private String cleanupSchedule = "0 2 * * *";
    }
}
