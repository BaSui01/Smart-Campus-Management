package com.campus.infrastructure.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 分布式配置中心
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Slf4j
@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "campus.config")
public class DistributedConfigCenter {

    /**
     * 应用配置
     */
    private ApplicationConfig application = new ApplicationConfig();

    /**
     * 数据库配置
     */
    private DatabaseConfig database = new DatabaseConfig();

    /**
     * 缓存配置
     */
    private CacheConfig cache = new CacheConfig();

    /**
     * 消息队列配置
     */
    private MessageQueueConfig messageQueue = new MessageQueueConfig();

    /**
     * 安全配置
     */
    private SecurityConfig security = new SecurityConfig();

    /**
     * 监控配置
     */
    private MonitoringConfig monitoring = new MonitoringConfig();

    /**
     * 业务配置
     */
    private BusinessConfig business = new BusinessConfig();

    @Data
    public static class ApplicationConfig {
        private String name = "campus-management-system";
        private String version = "1.0.0";
        private String environment = "dev";
        private boolean debugMode = false;
        private int maxThreads = 200;
        private int connectionTimeout = 30000;
        private int readTimeout = 60000;
    }

    @Data
    public static class DatabaseConfig {
        private int maxPoolSize = 20;
        private int minPoolSize = 5;
        private int connectionTimeout = 30000;
        private int idleTimeout = 600000;
        private int maxLifetime = 1800000;
        private boolean autoCommit = true;
        private String isolationLevel = "READ_COMMITTED";
        private boolean enableSlowQueryLog = true;
        private int slowQueryThreshold = 2000;
    }

    @Data
    public static class CacheConfig {
        private boolean enabled = true;
        private int defaultTtl = 3600;
        private int maxSize = 10000;
        private String evictionPolicy = "LRU";
        private boolean enableStatistics = true;
        private Map<String, Integer> cacheTtlConfig = new HashMap<>();
    }

    @Data
    public static class MessageQueueConfig {
        private boolean enabled = true;
        private int maxRetries = 3;
        private int retryDelay = 5000;
        private int batchSize = 100;
        private int consumerThreads = 5;
        private boolean enableDlq = true;
        private int dlqTtl = 86400000; // 24小时
    }

    @Data
    public static class SecurityConfig {
        private boolean enableRateLimit = true;
        private int rateLimitPerMinute = 100;
        private boolean enableIpWhitelist = false;
        private boolean enableAuditLog = true;
        private int passwordMinLength = 8;
        private int passwordMaxAge = 90;
        private int sessionTimeout = 1800;
        private boolean enableTwoFactor = false;
    }

    @Data
    public static class MonitoringConfig {
        private boolean enabled = true;
        private int metricsInterval = 60;
        private boolean enableHealthCheck = true;
        private int healthCheckInterval = 30;
        private boolean enableAlerts = true;
        private double cpuThreshold = 80.0;
        private double memoryThreshold = 85.0;
        private double diskThreshold = 90.0;
    }

    @Data
    public static class BusinessConfig {
        private int maxStudentsPerClass = 50;
        private int maxCoursesPerStudent = 10;
        private int gradeRetentionYears = 5;
        private int attendanceRetentionYears = 3;
        private boolean enableAutoGrading = false;
        private boolean enableAttendanceReminder = true;
        private int fileMaxSize = 10485760; // 10MB
        private String[] allowedFileTypes = {"pdf", "doc", "docx", "xls", "xlsx", "jpg", "png"};
    }

    /**
     * 配置变更监听
     */
    @Scheduled(fixedRate = 30000) // 每30秒检查一次配置变更
    public void checkConfigChanges() {
        try {
            // 这里可以实现配置变更检测逻辑
            // 比如从Nacos、Apollo等配置中心拉取最新配置
            log.debug("检查配置变更: {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("检查配置变更失败", e);
        }
    }

    /**
     * 获取配置摘要
     */
    public Map<String, Object> getConfigSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("application", application);
        summary.put("database", database);
        summary.put("cache", cache);
        summary.put("messageQueue", messageQueue);
        summary.put("security", security);
        summary.put("monitoring", monitoring);
        summary.put("business", business);
        summary.put("lastUpdated", LocalDateTime.now());
        return summary;
    }

    /**
     * 验证配置有效性
     */
    public boolean validateConfig() {
        try {
            // 验证数据库配置
            if (database.getMaxPoolSize() <= 0 || database.getMinPoolSize() <= 0) {
                log.error("数据库连接池配置无效");
                return false;
            }

            // 验证缓存配置
            if (cache.getDefaultTtl() <= 0 || cache.getMaxSize() <= 0) {
                log.error("缓存配置无效");
                return false;
            }

            // 验证安全配置
            if (security.getPasswordMinLength() < 6 || security.getSessionTimeout() <= 0) {
                log.error("安全配置无效");
                return false;
            }

            // 验证业务配置
            if (business.getMaxStudentsPerClass() <= 0 || business.getFileMaxSize() <= 0) {
                log.error("业务配置无效");
                return false;
            }

            log.info("配置验证通过");
            return true;
        } catch (Exception e) {
            log.error("配置验证失败", e);
            return false;
        }
    }

    /**
     * 重置为默认配置
     */
    public void resetToDefault() {
        this.application = new ApplicationConfig();
        this.database = new DatabaseConfig();
        this.cache = new CacheConfig();
        this.messageQueue = new MessageQueueConfig();
        this.security = new SecurityConfig();
        this.monitoring = new MonitoringConfig();
        this.business = new BusinessConfig();
        
        log.info("配置已重置为默认值");
    }

    /**
     * 动态更新配置
     */
    public void updateConfig(String configType, Map<String, Object> updates) {
        try {
            switch (configType.toLowerCase()) {
                case "application":
                    updateApplicationConfig(updates);
                    break;
                case "database":
                    updateDatabaseConfig(updates);
                    break;
                case "cache":
                    updateCacheConfig(updates);
                    break;
                case "security":
                    updateSecurityConfig(updates);
                    break;
                case "business":
                    updateBusinessConfig(updates);
                    break;
                default:
                    log.warn("未知的配置类型: {}", configType);
            }
            
            log.info("配置更新成功: type={}, updates={}", configType, updates);
        } catch (Exception e) {
            log.error("配置更新失败: type={}", configType, e);
        }
    }

    private void updateApplicationConfig(Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "debugMode":
                    application.setDebugMode((Boolean) value);
                    break;
                case "maxThreads":
                    application.setMaxThreads((Integer) value);
                    break;
                case "connectionTimeout":
                    application.setConnectionTimeout((Integer) value);
                    break;
                case "readTimeout":
                    application.setReadTimeout((Integer) value);
                    break;
            }
        });
    }

    private void updateDatabaseConfig(Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "maxPoolSize":
                    database.setMaxPoolSize((Integer) value);
                    break;
                case "minPoolSize":
                    database.setMinPoolSize((Integer) value);
                    break;
                case "connectionTimeout":
                    database.setConnectionTimeout((Integer) value);
                    break;
                case "enableSlowQueryLog":
                    database.setEnableSlowQueryLog((Boolean) value);
                    break;
                case "slowQueryThreshold":
                    database.setSlowQueryThreshold((Integer) value);
                    break;
            }
        });
    }

    private void updateCacheConfig(Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "enabled":
                    cache.setEnabled((Boolean) value);
                    break;
                case "defaultTtl":
                    cache.setDefaultTtl((Integer) value);
                    break;
                case "maxSize":
                    cache.setMaxSize((Integer) value);
                    break;
                case "enableStatistics":
                    cache.setEnableStatistics((Boolean) value);
                    break;
            }
        });
    }

    private void updateSecurityConfig(Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "enableRateLimit":
                    security.setEnableRateLimit((Boolean) value);
                    break;
                case "rateLimitPerMinute":
                    security.setRateLimitPerMinute((Integer) value);
                    break;
                case "enableIpWhitelist":
                    security.setEnableIpWhitelist((Boolean) value);
                    break;
                case "enableAuditLog":
                    security.setEnableAuditLog((Boolean) value);
                    break;
                case "sessionTimeout":
                    security.setSessionTimeout((Integer) value);
                    break;
            }
        });
    }

    private void updateBusinessConfig(Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "maxStudentsPerClass":
                    business.setMaxStudentsPerClass((Integer) value);
                    break;
                case "maxCoursesPerStudent":
                    business.setMaxCoursesPerStudent((Integer) value);
                    break;
                case "enableAutoGrading":
                    business.setEnableAutoGrading((Boolean) value);
                    break;
                case "enableAttendanceReminder":
                    business.setEnableAttendanceReminder((Boolean) value);
                    break;
                case "fileMaxSize":
                    business.setFileMaxSize((Integer) value);
                    break;
            }
        });
    }
}
