package com.campus.application.service;

import com.campus.domain.entity.SystemSettings;

import java.util.List;
import java.util.Map;

/**
 * 系统设置服务接口
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-05
 */
public interface SystemSettingsService {
    
    /**
     * 获取设置值
     */
    String getSettingValue(String key);
    
    /**
     * 获取设置值（带默认值）
     */
    String getSettingValue(String key, String defaultValue);
    
    /**
     * 获取布尔类型设置值
     */
    Boolean getBooleanSetting(String key, Boolean defaultValue);
    
    /**
     * 获取整数类型设置值
     */
    Integer getIntegerSetting(String key, Integer defaultValue);
    
    /**
     * 设置值
     */
    void setSetting(String key, String value);
    
    /**
     * 设置值（带描述）
     */
    void setSetting(String key, String value, String description, String type);
    
    /**
     * 批量设置
     */
    void batchSetSettings(Map<String, String> settings);
    
    /**
     * 删除设置
     */
    void deleteSetting(String key);
    
    /**
     * 获取所有设置
     */
    List<SystemSettings> getAllSettings();
    
    /**
     * 根据类型获取设置
     */
    List<SystemSettings> getSettingsByType(String type);
    
    /**
     * 搜索设置
     */
    List<SystemSettings> searchSettings(String keyword);
    
    /**
     * 获取系统设置集合用于页面显示
     */
    Map<String, Object> getSystemSettingsForDisplay();
    
    /**
     * 保存系统基本设置
     */
    void saveBasicSettings(Map<String, String> basicSettings);
    
    /**
     * 保存安全设置
     */
    void saveSecuritySettings(Map<String, String> securitySettings);
    
    /**
     * 保存通知设置
     */
    void saveNotificationSettings(Map<String, String> notificationSettings);
    
    /**
     * 数据备份
     */
    boolean backupData();
    
    /**
     * 清理缓存
     */
    boolean clearCache();
    
    /**
     * 获取系统信息
     */
    Map<String, Object> getSystemInfo();
    
    /**
     * 重启系统
     */
    boolean restartSystem();
    
    /**
     * 初始化默认设置
     */
    void initializeDefaultSettings();
}