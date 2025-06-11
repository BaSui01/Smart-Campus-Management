package com.campus.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 系统配置实体类
 * 管理系统的各种配置参数
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_system_config", indexes = {
    @Index(name = "idx_config_key", columnList = "config_key", unique = true),
    @Index(name = "idx_config_group", columnList = "config_group"),
    @Index(name = "idx_is_system", columnList = "is_system")
})
public class SystemConfig extends BaseEntity {

    /**
     * 配置键
     */
    @NotBlank(message = "配置键不能为空")
    @Size(max = 100, message = "配置键长度不能超过100个字符")
    @Column(name = "config_key", nullable = false, unique = true, length = 100)
    private String configKey;

    /**
     * 配置值
     */
    @Size(max = 2000, message = "配置值长度不能超过2000个字符")
    @Column(name = "config_value", length = 2000)
    private String configValue;

    /**
     * 配置名称
     */
    @NotBlank(message = "配置名称不能为空")
    @Size(max = 200, message = "配置名称长度不能超过200个字符")
    @Column(name = "config_name", nullable = false, length = 200)
    private String configName;

    /**
     * 配置描述
     */
    @Size(max = 500, message = "配置描述长度不能超过500个字符")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 配置分组
     */
    @Size(max = 50, message = "配置分组长度不能超过50个字符")
    @Column(name = "config_group", length = 50)
    private String configGroup;

    /**
     * 配置类型 (string/number/boolean/json/array)
     */
    @Size(max = 20, message = "配置类型长度不能超过20个字符")
    @Column(name = "config_type", length = 20)
    private String configType = "string";

    /**
     * 默认值
     */
    @Size(max = 2000, message = "默认值长度不能超过2000个字符")
    @Column(name = "default_value", length = 2000)
    private String defaultValue;

    /**
     * 是否为系统配置（系统配置不允许删除）
     */
    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false;

    /**
     * 是否可编辑
     */
    @Column(name = "is_editable", nullable = false)
    private Boolean isEditable = true;

    /**
     * 是否敏感信息（如密码等）
     */
    @Column(name = "is_sensitive", nullable = false)
    private Boolean isSensitive = false;

    /**
     * 验证规则（正则表达式）
     */
    @Size(max = 500, message = "验证规则长度不能超过500个字符")
    @Column(name = "validation_rule", length = 500)
    private String validationRule;

    /**
     * 可选值（JSON格式）
     */
    @Size(max = 1000, message = "可选值长度不能超过1000个字符")
    @Column(name = "options", length = 1000)
    private String options;

    /**
     * 排序顺序
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /**
     * 最后修改人ID
     */
    @Column(name = "last_modified_by")
    private Long lastModifiedBy;

    /**
     * 最后修改时间
     */
    @Column(name = "last_modified_time")
    private LocalDateTime lastModifiedTime;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Column(name = "remarks", length = 500)
    private String remarks;

    // ================================
    // 构造函数
    // ================================

    public SystemConfig() {
        super();
    }

    public SystemConfig(String configKey, String configValue, String configName, String configGroup) {
        this();
        this.configKey = configKey;
        this.configValue = configValue;
        this.configName = configName;
        this.configGroup = configGroup;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 获取配置类型文本
     */
    public String getConfigTypeText() {
        if (configType == null) return "字符串";
        return switch (configType) {
            case "string" -> "字符串";
            case "number" -> "数字";
            case "boolean" -> "布尔值";
            case "json" -> "JSON对象";
            case "array" -> "数组";
            default -> configType;
        };
    }

    /**
     * 获取布尔值
     */
    public Boolean getBooleanValue() {
        if (configValue == null) return null;
        return Boolean.parseBoolean(configValue);
    }

    /**
     * 获取整数值
     */
    public Integer getIntegerValue() {
        if (configValue == null) return null;
        try {
            return Integer.parseInt(configValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取长整数值
     */
    public Long getLongValue() {
        if (configValue == null) return null;
        try {
            return Long.parseLong(configValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取双精度值
     */
    public Double getDoubleValue() {
        if (configValue == null) return null;
        try {
            return Double.parseDouble(configValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取数组值
     */
    public String[] getArrayValue() {
        if (configValue == null || configValue.trim().isEmpty()) {
            return new String[0];
        }
        return configValue.split(",");
    }

    /**
     * 设置布尔值
     */
    public void setBooleanValue(Boolean value) {
        this.configValue = value != null ? value.toString() : null;
        this.configType = "boolean";
    }

    /**
     * 设置整数值
     */
    public void setIntegerValue(Integer value) {
        this.configValue = value != null ? value.toString() : null;
        this.configType = "number";
    }

    /**
     * 设置长整数值
     */
    public void setLongValue(Long value) {
        this.configValue = value != null ? value.toString() : null;
        this.configType = "number";
    }

    /**
     * 设置双精度值
     */
    public void setDoubleValue(Double value) {
        this.configValue = value != null ? value.toString() : null;
        this.configType = "number";
    }

    /**
     * 设置数组值
     */
    public void setArrayValue(String[] values) {
        if (values == null || values.length == 0) {
            this.configValue = "";
        } else {
            this.configValue = String.join(",", values);
        }
        this.configType = "array";
    }

    /**
     * 验证配置值
     */
    public boolean validateValue(String value) {
        if (validationRule == null || validationRule.trim().isEmpty()) {
            return true;
        }
        
        if (value == null) {
            return false;
        }
        
        try {
            return value.matches(validationRule);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 重置为默认值
     */
    public void resetToDefault() {
        this.configValue = this.defaultValue;
        this.lastModifiedTime = LocalDateTime.now();
    }

    /**
     * 更新配置值
     */
    public void updateValue(String newValue, Long modifierId) {
        if (!isEditable) {
            throw new IllegalStateException("该配置不允许编辑");
        }
        
        if (!validateValue(newValue)) {
            throw new IllegalArgumentException("配置值不符合验证规则");
        }
        
        this.configValue = newValue;
        this.lastModifiedBy = modifierId;
        this.lastModifiedTime = LocalDateTime.now();
    }

    /**
     * 获取显示值（敏感信息脱敏）
     */
    public String getDisplayValue() {
        if (isSensitive && configValue != null && !configValue.isEmpty()) {
            return "******";
        }
        return configValue;
    }

    /**
     * 检查是否有默认值
     */
    public boolean hasDefaultValue() {
        return defaultValue != null && !defaultValue.trim().isEmpty();
    }

    /**
     * 检查是否为默认值
     */
    public boolean isDefaultValue() {
        if (defaultValue == null) {
            return configValue == null;
        }
        return defaultValue.equals(configValue);
    }

    /**
     * 创建系统配置
     */
    public static SystemConfig createSystemConfig(String key, String value, String name, String group) {
        SystemConfig config = new SystemConfig(key, value, name, group);
        config.setIsSystem(true);
        config.setIsEditable(true);
        return config;
    }

    /**
     * 创建只读系统配置
     */
    public static SystemConfig createReadOnlySystemConfig(String key, String value, String name, String group) {
        SystemConfig config = createSystemConfig(key, value, name, group);
        config.setIsEditable(false);
        return config;
    }

    /**
     * 验证配置数据
     */
    @PrePersist
    @PreUpdate
    public void validateConfig() {
        if (configKey != null) {
            // 配置键只能包含字母、数字、下划线和点号
            if (!configKey.matches("^[a-zA-Z0-9_.]+$")) {
                throw new IllegalArgumentException("配置键只能包含字母、数字、下划线和点号");
            }
        }
        
        if (sortOrder != null && sortOrder < 0) {
            throw new IllegalArgumentException("排序顺序不能为负数");
        }
    }

    // ================================
    // Getter and Setter methods
    // ================================

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConfigGroup() {
        return configGroup;
    }

    public void setConfigGroup(String configGroup) {
        this.configGroup = configGroup;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }

    public Boolean getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(Boolean isEditable) {
        this.isEditable = isEditable;
    }

    public Boolean getIsSensitive() {
        return isSensitive;
    }

    public void setIsSensitive(Boolean isSensitive) {
        this.isSensitive = isSensitive;
    }

    public String getValidationRule() {
        return validationRule;
    }

    public void setValidationRule(String validationRule) {
        this.validationRule = validationRule;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Long getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(Long lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(LocalDateTime lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
