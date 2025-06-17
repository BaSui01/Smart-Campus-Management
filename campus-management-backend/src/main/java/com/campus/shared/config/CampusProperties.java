package com.campus.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 校园管理系统配置属性
 * 用于绑定application.yml中的自定义配置
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-15
 */
@Component
@ConfigurationProperties(prefix = "campus")
public class CampusProperties {

    private App app = new App();
    private System system = new System();
    private Upload upload = new Upload();
    private Security security = new Security();
    private Business business = new Business();
    private Cache cache = new Cache();
    private Monitor monitor = new Monitor();
    private Mail mail = new Mail();

    // Getters and Setters
    public App getApp() { return app; }
    public void setApp(App app) { this.app = app; }

    public System getSystem() { return system; }
    public void setSystem(System system) { this.system = system; }

    public Upload getUpload() { return upload; }
    public void setUpload(Upload upload) { this.upload = upload; }

    public Security getSecurity() { return security; }
    public void setSecurity(Security security) { this.security = security; }

    public Business getBusiness() { return business; }
    public void setBusiness(Business business) { this.business = business; }

    public Cache getCache() { return cache; }
    public void setCache(Cache cache) { this.cache = cache; }

    public Monitor getMonitor() { return monitor; }
    public void setMonitor(Monitor monitor) { this.monitor = monitor; }

    public Mail getMail() { return mail; }
    public void setMail(Mail mail) { this.mail = mail; }

    /**
     * 应用配置
     */
    public static class App {
        private String name;
        private String version;
        private String description;
        private Jwt jwt = new Jwt();

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Jwt getJwt() { return jwt; }
        public void setJwt(Jwt jwt) { this.jwt = jwt; }

        /**
         * JWT配置
         */
        public static class Jwt {
            private String secret;
            private long expiration;
            private long refreshExpiration;

            public String getSecret() { return secret; }
            public void setSecret(String secret) { this.secret = secret; }

            public long getExpiration() { return expiration; }
            public void setExpiration(long expiration) { this.expiration = expiration; }

            public long getRefreshExpiration() { return refreshExpiration; }
            public void setRefreshExpiration(long refreshExpiration) { this.refreshExpiration = refreshExpiration; }
        }
    }

    /**
     * 系统配置
     */
    public static class System {
        private String name;
        private String version;
        private boolean debug;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public boolean isDebug() { return debug; }
        public void setDebug(boolean debug) { this.debug = debug; }
    }

    /**
     * 上传配置
     */
    public static class Upload {
        private boolean enabled = true;
        private String path = "uploads/";
        private long maxSizeBytes = 10485760L; // 10MB in bytes
        private String[] allowedTypes = {"jpg", "jpeg", "png", "gif", "pdf", "doc", "docx"};

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }

        public long getMaxSizeBytes() { return maxSizeBytes; }
        public void setMaxSizeBytes(long maxSizeBytes) { this.maxSizeBytes = maxSizeBytes; }

        public String[] getAllowedTypes() { return allowedTypes; }
        public void setAllowedTypes(String[] allowedTypes) { this.allowedTypes = allowedTypes; }
    }

    /**
     * 安全配置
     */
    public static class Security {
        private boolean enabled;
        private String[] ignoredUrls;
        private int maxLoginAttempts;
        private long lockoutDuration;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String[] getIgnoredUrls() { return ignoredUrls; }
        public void setIgnoredUrls(String[] ignoredUrls) { this.ignoredUrls = ignoredUrls; }

        public int getMaxLoginAttempts() { return maxLoginAttempts; }
        public void setMaxLoginAttempts(int maxLoginAttempts) { this.maxLoginAttempts = maxLoginAttempts; }

        public long getLockoutDuration() { return lockoutDuration; }
        public void setLockoutDuration(long lockoutDuration) { this.lockoutDuration = lockoutDuration; }
    }

    /**
     * 业务配置
     */
    public static class Business {
        private boolean enableNotification;
        private boolean enableAudit;
        private int defaultPageSize;

        public boolean isEnableNotification() { return enableNotification; }
        public void setEnableNotification(boolean enableNotification) { this.enableNotification = enableNotification; }

        public boolean isEnableAudit() { return enableAudit; }
        public void setEnableAudit(boolean enableAudit) { this.enableAudit = enableAudit; }

        public int getDefaultPageSize() { return defaultPageSize; }
        public void setDefaultPageSize(int defaultPageSize) { this.defaultPageSize = defaultPageSize; }
    }

    /**
     * 缓存配置
     */
    public static class Cache {
        private boolean enabled;
        private String type;
        private long defaultTtl;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public long getDefaultTtl() { return defaultTtl; }
        public void setDefaultTtl(long defaultTtl) { this.defaultTtl = defaultTtl; }
    }

    /**
     * 监控配置
     */
    public static class Monitor {
        private boolean enabled;
        private String[] endpoints;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String[] getEndpoints() { return endpoints; }
        public void setEndpoints(String[] endpoints) { this.endpoints = endpoints; }
    }

    /**
     * 邮件配置
     */
    public static class Mail {
        private boolean enabled;
        private String from;
        private String[] adminEmails;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }

        public String[] getAdminEmails() { return adminEmails; }
        public void setAdminEmails(String[] adminEmails) { this.adminEmails = adminEmails; }
    }
}
