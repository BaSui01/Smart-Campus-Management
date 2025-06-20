package com.campus.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 业务配置类
 * 统一管理系统中的业务配置参数，避免硬编码
 * 
 * @author Campus Management Team
 * @since 2025-06-20
 */
@Configuration
@ConfigurationProperties(prefix = "campus.business")
public class BusinessConfig {

    /**
     * 分页配置
     */
    private Pagination pagination = new Pagination();

    /**
     * 文件上传配置
     */
    private FileUpload fileUpload = new FileUpload();

    /**
     * 缓存配置
     */
    private Cache cache = new Cache();

    /**
     * 业务规则配置
     */
    private BusinessRules businessRules = new BusinessRules();

    /**
     * 安全配置
     */
    private Security security = new Security();

    /**
     * 通知配置
     */
    private Notification notification = new Notification();

    // Getters and Setters
    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public FileUpload getFileUpload() {
        return fileUpload;
    }

    public void setFileUpload(FileUpload fileUpload) {
        this.fileUpload = fileUpload;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public BusinessRules getBusinessRules() {
        return businessRules;
    }

    public void setBusinessRules(BusinessRules businessRules) {
        this.businessRules = businessRules;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    /**
     * 分页配置
     */
    public static class Pagination {
        /**
         * 默认页大小
         */
        private int defaultPageSize = 10;

        /**
         * 最大页大小
         */
        private int maxPageSize = 100;

        /**
         * 默认排序字段
         */
        private String defaultSortField = "id";

        /**
         * 默认排序方向
         */
        private String defaultSortDirection = "desc";

        // Getters and Setters
        public int getDefaultPageSize() {
            return defaultPageSize;
        }

        public void setDefaultPageSize(int defaultPageSize) {
            this.defaultPageSize = defaultPageSize;
        }

        public int getMaxPageSize() {
            return maxPageSize;
        }

        public void setMaxPageSize(int maxPageSize) {
            this.maxPageSize = maxPageSize;
        }

        public String getDefaultSortField() {
            return defaultSortField;
        }

        public void setDefaultSortField(String defaultSortField) {
            this.defaultSortField = defaultSortField;
        }

        public String getDefaultSortDirection() {
            return defaultSortDirection;
        }

        public void setDefaultSortDirection(String defaultSortDirection) {
            this.defaultSortDirection = defaultSortDirection;
        }
    }

    /**
     * 文件上传配置
     */
    public static class FileUpload {
        /**
         * 最大文件大小（字节）
         */
        private long maxFileSize = 10 * 1024 * 1024; // 10MB

        /**
         * 允许的文件类型
         */
        private String[] allowedTypes = {"jpg", "jpeg", "png", "gif", "pdf", "doc", "docx", "xls", "xlsx"};

        /**
         * 上传路径
         */
        private String uploadPath = "uploads/";

        /**
         * 头像上传路径
         */
        private String avatarPath = "uploads/avatars/";

        /**
         * 文档上传路径
         */
        private String documentPath = "uploads/documents/";

        // Getters and Setters
        public long getMaxFileSize() {
            return maxFileSize;
        }

        public void setMaxFileSize(long maxFileSize) {
            this.maxFileSize = maxFileSize;
        }

        public String[] getAllowedTypes() {
            return allowedTypes;
        }

        public void setAllowedTypes(String[] allowedTypes) {
            this.allowedTypes = allowedTypes;
        }

        public String getUploadPath() {
            return uploadPath;
        }

        public void setUploadPath(String uploadPath) {
            this.uploadPath = uploadPath;
        }

        public String getAvatarPath() {
            return avatarPath;
        }

        public void setAvatarPath(String avatarPath) {
            this.avatarPath = avatarPath;
        }

        public String getDocumentPath() {
            return documentPath;
        }

        public void setDocumentPath(String documentPath) {
            this.documentPath = documentPath;
        }
    }

    /**
     * 缓存配置
     */
    public static class Cache {
        /**
         * 默认缓存过期时间（秒）
         */
        private long defaultTtl = 1800; // 30分钟

        /**
         * 用户缓存过期时间（秒）
         */
        private long userTtl = 3600; // 1小时

        /**
         * 课程缓存过期时间（秒）
         */
        private long courseTtl = 7200; // 2小时

        /**
         * 系统配置缓存过期时间（秒）
         */
        private long configTtl = 86400; // 24小时

        // Getters and Setters
        public long getDefaultTtl() {
            return defaultTtl;
        }

        public void setDefaultTtl(long defaultTtl) {
            this.defaultTtl = defaultTtl;
        }

        public long getUserTtl() {
            return userTtl;
        }

        public void setUserTtl(long userTtl) {
            this.userTtl = userTtl;
        }

        public long getCourseTtl() {
            return courseTtl;
        }

        public void setCourseTtl(long courseTtl) {
            this.courseTtl = courseTtl;
        }

        public long getConfigTtl() {
            return configTtl;
        }

        public void setConfigTtl(long configTtl) {
            this.configTtl = configTtl;
        }
    }

    /**
     * 业务规则配置
     */
    public static class BusinessRules {
        /**
         * 最大选课数量
         */
        private int maxCourseSelection = 8;

        /**
         * 最小选课学分
         */
        private int minCredits = 12;

        /**
         * 最大选课学分
         */
        private int maxCredits = 30;

        /**
         * 密码最小长度
         */
        private int passwordMinLength = 6;

        /**
         * 密码最大长度
         */
        private int passwordMaxLength = 20;

        /**
         * 登录失败最大次数
         */
        private int maxLoginAttempts = 5;

        /**
         * 账户锁定时间（分钟）
         */
        private int accountLockDuration = 30;

        // Getters and Setters
        public int getMaxCourseSelection() {
            return maxCourseSelection;
        }

        public void setMaxCourseSelection(int maxCourseSelection) {
            this.maxCourseSelection = maxCourseSelection;
        }

        public int getMinCredits() {
            return minCredits;
        }

        public void setMinCredits(int minCredits) {
            this.minCredits = minCredits;
        }

        public int getMaxCredits() {
            return maxCredits;
        }

        public void setMaxCredits(int maxCredits) {
            this.maxCredits = maxCredits;
        }

        public int getPasswordMinLength() {
            return passwordMinLength;
        }

        public void setPasswordMinLength(int passwordMinLength) {
            this.passwordMinLength = passwordMinLength;
        }

        public int getPasswordMaxLength() {
            return passwordMaxLength;
        }

        public void setPasswordMaxLength(int passwordMaxLength) {
            this.passwordMaxLength = passwordMaxLength;
        }

        public int getMaxLoginAttempts() {
            return maxLoginAttempts;
        }

        public void setMaxLoginAttempts(int maxLoginAttempts) {
            this.maxLoginAttempts = maxLoginAttempts;
        }

        public int getAccountLockDuration() {
            return accountLockDuration;
        }

        public void setAccountLockDuration(int accountLockDuration) {
            this.accountLockDuration = accountLockDuration;
        }
    }

    /**
     * 安全配置
     */
    public static class Security {
        /**
         * 是否启用验证码
         */
        private boolean captchaEnabled = true;

        /**
         * 验证码过期时间（秒）
         */
        private int captchaExpiration = 300; // 5分钟

        /**
         * 是否启用IP白名单
         */
        private boolean ipWhitelistEnabled = false;

        /**
         * IP白名单
         */
        private String[] ipWhitelist = {};

        // Getters and Setters
        public boolean isCaptchaEnabled() {
            return captchaEnabled;
        }

        public void setCaptchaEnabled(boolean captchaEnabled) {
            this.captchaEnabled = captchaEnabled;
        }

        public int getCaptchaExpiration() {
            return captchaExpiration;
        }

        public void setCaptchaExpiration(int captchaExpiration) {
            this.captchaExpiration = captchaExpiration;
        }

        public boolean isIpWhitelistEnabled() {
            return ipWhitelistEnabled;
        }

        public void setIpWhitelistEnabled(boolean ipWhitelistEnabled) {
            this.ipWhitelistEnabled = ipWhitelistEnabled;
        }

        public String[] getIpWhitelist() {
            return ipWhitelist;
        }

        public void setIpWhitelist(String[] ipWhitelist) {
            this.ipWhitelist = ipWhitelist;
        }
    }

    /**
     * 通知配置
     */
    public static class Notification {
        /**
         * 是否启用邮件通知
         */
        private boolean emailEnabled = true;

        /**
         * 是否启用短信通知
         */
        private boolean smsEnabled = false;

        /**
         * 通知重试次数
         */
        private int retryCount = 3;

        /**
         * 通知重试间隔（秒）
         */
        private int retryInterval = 60;

        // Getters and Setters
        public boolean isEmailEnabled() {
            return emailEnabled;
        }

        public void setEmailEnabled(boolean emailEnabled) {
            this.emailEnabled = emailEnabled;
        }

        public boolean isSmsEnabled() {
            return smsEnabled;
        }

        public void setSmsEnabled(boolean smsEnabled) {
            this.smsEnabled = smsEnabled;
        }

        public int getRetryCount() {
            return retryCount;
        }

        public void setRetryCount(int retryCount) {
            this.retryCount = retryCount;
        }

        public int getRetryInterval() {
            return retryInterval;
        }

        public void setRetryInterval(int retryInterval) {
            this.retryInterval = retryInterval;
        }
    }
}
