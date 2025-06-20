package com.campus.application.service.system;

import com.campus.domain.entity.system.SystemConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 系统配置服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-19
 */
public interface SystemConfigService {

    // ==================== 基础CRUD方法 ====================

    /**
     * 保存系统配置
     */
    SystemConfig save(SystemConfig config);

    /**
     * 根据ID查找配置
     */
    Optional<SystemConfig> findById(Long id);

    /**
     * 查找所有配置
     */
    List<SystemConfig> findAll();

    /**
     * 分页查找配置
     */
    Page<SystemConfig> findAll(Pageable pageable);

    /**
     * 删除配置
     */
    void deleteById(Long id);

    /**
     * 批量删除配置
     */
    void deleteByIds(List<Long> ids);

    /**
     * 统计配置数量
     */
    long count();

    // ==================== 业务查询方法 ====================

    /**
     * 根据配置键查找配置
     */
    Optional<SystemConfig> findByConfigKey(String configKey);

    /**
     * 根据配置分组查找配置
     */
    List<SystemConfig> findByConfigGroup(String configGroup);

    /**
     * 分页根据配置分组查找配置
     */
    Page<SystemConfig> findByConfigGroup(String configGroup, Pageable pageable);

    /**
     * 根据配置类型查找配置
     */
    List<SystemConfig> findByConfigType(String configType);

    /**
     * 查找所有启用的配置
     */
    List<SystemConfig> findAllActiveConfigs();

    /**
     * 分页查找启用的配置
     */
    Page<SystemConfig> findAllActiveConfigs(Pageable pageable);

    /**
     * 条件查询配置
     */
    Page<SystemConfig> findByConditions(Map<String, Object> params, Pageable pageable);

    // ==================== 配置值操作方法 ====================

    /**
     * 获取配置值
     */
    String getConfigValue(String configKey);

    /**
     * 获取配置值（带默认值）
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 设置配置值
     */
    void setConfigValue(String configKey, String configValue);

    /**
     * 批量设置配置值
     */
    void setConfigValues(Map<String, String> configMap);

    // ==================== 配置分组管理 ====================

    /**
     * 获取所有配置分组
     */
    List<String> getAllConfigGroups();

    /**
     * 获取配置分组统计
     */
    Map<String, Long> getConfigGroupStats();

    // ==================== 配置验证方法 ====================

    /**
     * 检查配置键是否存在
     */
    boolean existsByConfigKey(String configKey);

    /**
     * 检查配置键是否存在（排除指定ID）
     */
    boolean existsByConfigKeyExcludeId(String configKey, Long excludeId);

    /**
     * 验证配置值格式
     */
    boolean validateConfigValue(String configType, String configValue);

    // ==================== 配置缓存管理 ====================

    /**
     * 刷新配置缓存
     */
    void refreshCache();

    /**
     * 清除配置缓存
     */
    void clearCache();

    // ==================== 配置导入导出 ====================

    /**
     * 导出配置
     */
    Map<String, Object> exportConfigs(String configGroup);

    /**
     * 导入配置
     */
    void importConfigs(List<SystemConfig> configs);

    // ==================== 配置统计方法 ====================

    /**
     * 获取配置统计信息
     */
    Map<String, Object> getConfigStatistics();

    /**
     * 获取最近更新的配置
     */
    List<SystemConfig> getRecentUpdatedConfigs(int limit);
}
