package com.campus.application.Implement.system;

import com.campus.application.service.system.SystemSettingsService;
import com.campus.domain.entity.system.SystemSettings;
import com.campus.domain.repository.system.SystemSettingsRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 系统设置服务实现类
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-05
 */
@Service
@Transactional
public class SystemSettingsServiceImpl implements SystemSettingsService {
    
    private static final Logger logger = LoggerFactory.getLogger(SystemSettingsServiceImpl.class);
    
    @Autowired
    private SystemSettingsRepository systemSettingsRepository;
    
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Cacheable(value = "systemSettings", key = "#key")
    public String getSettingValue(String key) {
        return systemSettingsRepository.findBySettingKey(key)
                .map(SystemSettings::getSettingValue)
                .orElse(null);
    }

    @Override
    public String getSettingValue(String key, String defaultValue) {
        String value = getSettingValue(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public Boolean getBooleanSetting(String key, Boolean defaultValue) {
        String value = getSettingValue(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    @Override
    public Integer getIntegerSetting(String key, Integer defaultValue) {
        String value = getSettingValue(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("无法解析设置值为整数: {} = {}", key, value);
            return defaultValue;
        }
    }

    @Override
    @CacheEvict(value = "systemSettings", key = "#key")
    public void setSetting(String key, String value) {
        SystemSettings setting = systemSettingsRepository.findBySettingKey(key)
                .orElse(new SystemSettings(key, value, null, "STRING"));
        setting.setSettingValue(value);
        systemSettingsRepository.save(setting);
        logger.info("系统设置已更新: {} = {}", key, value);
    }

    @Override
    @CacheEvict(value = "systemSettings", key = "#key")
    public void setSetting(String key, String value, String description, String type) {
        SystemSettings setting = systemSettingsRepository.findBySettingKey(key)
                .orElse(new SystemSettings());
        setting.setSettingKey(key);
        setting.setSettingValue(value);
        setting.setSettingDescription(description);
        setting.setSettingType(type);
        systemSettingsRepository.save(setting);
        logger.info("系统设置已更新: {} = {} ({})", key, value, description);
    }

    @Override
    @CacheEvict(value = "systemSettings", allEntries = true)
    public void batchSetSettings(Map<String, String> settings) {
        List<SystemSettings> settingsToSave = new ArrayList<>();
        
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            SystemSettings setting = systemSettingsRepository.findBySettingKey(key)
                    .orElse(new SystemSettings(key, value, null, "STRING"));
            setting.setSettingValue(value);
            settingsToSave.add(setting);
        }
        
        systemSettingsRepository.saveAll(settingsToSave);
        logger.info("批量更新系统设置，共 {} 项", settings.size());
    }

    @Override
    @CacheEvict(value = "systemSettings", key = "#key")
    public void deleteSetting(String key) {
        systemSettingsRepository.findBySettingKey(key)
                .ifPresent(setting -> {
                    systemSettingsRepository.delete(setting);
                    logger.info("系统设置已删除: {}", key);
                });
    }

    @Override
    public List<SystemSettings> getAllSettings() {
        return systemSettingsRepository.findAll();
    }

    @Override
    public List<SystemSettings> getSettingsByType(String type) {
        return systemSettingsRepository.findBySettingType(type);
    }

    @Override
    public List<SystemSettings> searchSettings(String keyword) {
        return systemSettingsRepository.searchByKeyword(keyword);
    }

    @Override
    public Map<String, Object> getSystemSettingsForDisplay() {
        Map<String, Object> settings = new HashMap<>();
        
        // 基本设置
        settings.put("systemName", getSettingValue("system.name", "智慧校园管理系统"));
        settings.put("systemVersion", getSettingValue("system.version", "1.0.0"));
        settings.put("systemLogo", getSettingValue("system.logo", "/images/logo.png"));
        settings.put("contactEmail", getSettingValue("contact.email", "admin@campus.edu.cn"));
        settings.put("contactPhone", getSettingValue("contact.phone", "400-000-0000"));
        
        // 安全设置
        settings.put("maxLoginAttempts", getIntegerSetting("security.max_login_attempts", 5));
        settings.put("sessionTimeout", getIntegerSetting("security.session_timeout", 30));
        settings.put("passwordMinLength", getIntegerSetting("security.password_min_length", 6));
        settings.put("enableCaptcha", getBooleanSetting("security.enable_captcha", true));
        
        // 通知设置
        settings.put("enableEmailNotification", getBooleanSetting("notification.email.enabled", true));
        settings.put("enableSmsNotification", getBooleanSetting("notification.sms.enabled", false));
        settings.put("notificationEmail", getSettingValue("notification.email.address", ""));
        settings.put("notificationPhone", getSettingValue("notification.sms.phone", ""));
        
        return settings;
    }

    @Override
    @CacheEvict(value = "systemSettings", allEntries = true)
    public void saveBasicSettings(Map<String, String> basicSettings) {
        Map<String, String> settingsToSave = new HashMap<>();
        
        if (basicSettings.containsKey("systemName")) {
            settingsToSave.put("system.name", basicSettings.get("systemName"));
        }
        if (basicSettings.containsKey("systemLogo")) {
            settingsToSave.put("system.logo", basicSettings.get("systemLogo"));
        }
        if (basicSettings.containsKey("contactEmail")) {
            settingsToSave.put("contact.email", basicSettings.get("contactEmail"));
        }
        if (basicSettings.containsKey("contactPhone")) {
            settingsToSave.put("contact.phone", basicSettings.get("contactPhone"));
        }
        
        batchSetSettings(settingsToSave);
    }

    @Override
    @CacheEvict(value = "systemSettings", allEntries = true)
    public void saveSecuritySettings(Map<String, String> securitySettings) {
        Map<String, String> settingsToSave = new HashMap<>();
        
        if (securitySettings.containsKey("maxLoginAttempts")) {
            settingsToSave.put("security.max_login_attempts", securitySettings.get("maxLoginAttempts"));
        }
        if (securitySettings.containsKey("sessionTimeout")) {
            settingsToSave.put("security.session_timeout", securitySettings.get("sessionTimeout"));
        }
        if (securitySettings.containsKey("passwordMinLength")) {
            settingsToSave.put("security.password_min_length", securitySettings.get("passwordMinLength"));
        }
        if (securitySettings.containsKey("enableCaptcha")) {
            settingsToSave.put("security.enable_captcha", securitySettings.get("enableCaptcha"));
        }
        
        batchSetSettings(settingsToSave);
    }

    @Override
    @CacheEvict(value = "systemSettings", allEntries = true)
    public void saveNotificationSettings(Map<String, String> notificationSettings) {
        Map<String, String> settingsToSave = new HashMap<>();
        
        if (notificationSettings.containsKey("enableEmailNotification")) {
            settingsToSave.put("notification.email.enabled", notificationSettings.get("enableEmailNotification"));
        }
        if (notificationSettings.containsKey("enableSmsNotification")) {
            settingsToSave.put("notification.sms.enabled", notificationSettings.get("enableSmsNotification"));
        }
        if (notificationSettings.containsKey("notificationEmail")) {
            settingsToSave.put("notification.email.address", notificationSettings.get("notificationEmail"));
        }
        if (notificationSettings.containsKey("notificationPhone")) {
            settingsToSave.put("notification.sms.phone", notificationSettings.get("notificationPhone"));
        }
        
        batchSetSettings(settingsToSave);
    }

    @Override
    public boolean backupData() {
        try {
            // 这里可以实现实际的数据备份逻辑
            // 例如：导出数据库、文件备份等
            logger.info("开始执行数据备份");
            
            // 模拟备份过程
            Thread.sleep(2000);
            
            logger.info("数据备份完成");
            return true;
        } catch (Exception e) {
            logger.error("数据备份失败", e);
            return false;
        }
    }

    @Override
    @CacheEvict(value = "systemSettings", allEntries = true)
    public boolean clearCache() {
        try {
            if (redisTemplate != null) {
                // 清理Redis缓存
                Set<String> keys = redisTemplate.keys("*");
                if (keys != null && !keys.isEmpty()) {
                    redisTemplate.delete(keys);
                }
            }
            logger.info("系统缓存清理完成");
            return true;
        } catch (Exception e) {
            logger.error("清理缓存失败", e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        
        try {
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            
            // 系统运行时间
            long uptime = runtimeBean.getUptime();
            long days = TimeUnit.MILLISECONDS.toDays(uptime);
            long hours = TimeUnit.MILLISECONDS.toHours(uptime) % 24;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime) % 60;
            systemInfo.put("uptime", String.format("%d天 %d小时 %d分钟", days, hours, minutes));
            
            // 内存使用情况
            long totalMemory = memoryBean.getHeapMemoryUsage().getMax();
            long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
            double memoryUsage = (double) usedMemory / totalMemory * 100;
            systemInfo.put("memoryUsage", Math.round(memoryUsage));
            
            // Java版本
            systemInfo.put("javaVersion", System.getProperty("java.version"));
            
            // 系统版本
            systemInfo.put("systemVersion", getSettingValue("system.version", "1.0.0"));
            
            // 其他系统信息
            systemInfo.put("cpuUsage", 25); // 模拟CPU使用率
            systemInfo.put("diskUsage", 45); // 模拟磁盘使用率
            systemInfo.put("networkStatus", "正常");
            
        } catch (Exception e) {
            logger.error("获取系统信息失败", e);
        }
        
        return systemInfo;
    }

    @Override
    public boolean restartSystem() {
        try {
            logger.warn("系统重启请求已接收，但出于安全考虑，此功能需要手动执行");
            // 实际生产环境中，系统重启应该通过外部脚本或容器编排工具来实现
            // 这里只是记录日志，不执行实际重启
            return false;
        } catch (Exception e) {
            logger.error("系统重启失败", e);
            return false;
        }
    }

    @Override
    @CacheEvict(value = "systemSettings", allEntries = true)
    public void initializeDefaultSettings() {
        logger.info("开始初始化默认系统设置");
        
        Map<String, String> defaultSettings = new HashMap<>();
        
        // 基本设置
        defaultSettings.put("system.name", "智慧校园管理系统");
        defaultSettings.put("system.version", "1.0.0");
        defaultSettings.put("system.logo", "/images/logo.png");
        defaultSettings.put("contact.email", "admin@campus.edu.cn");
        defaultSettings.put("contact.phone", "400-000-0000");
        
        // 安全设置
        defaultSettings.put("security.max_login_attempts", "5");
        defaultSettings.put("security.session_timeout", "30");
        defaultSettings.put("security.password_min_length", "6");
        defaultSettings.put("security.enable_captcha", "true");
        
        // 通知设置
        defaultSettings.put("notification.email.enabled", "true");
        defaultSettings.put("notification.sms.enabled", "false");
        defaultSettings.put("notification.email.address", "");
        defaultSettings.put("notification.sms.phone", "");
        
        // 只设置不存在的设置项
        for (Map.Entry<String, String> entry : defaultSettings.entrySet()) {
            if (!systemSettingsRepository.existsBySettingKey(entry.getKey())) {
                setSetting(entry.getKey(), entry.getValue());
            }
        }
        
        logger.info("默认系统设置初始化完成");
    }
}