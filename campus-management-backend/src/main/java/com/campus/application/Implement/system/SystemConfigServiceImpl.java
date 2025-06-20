package com.campus.application.Implement.system;

import com.campus.application.service.system.SystemConfigService;
import com.campus.domain.entity.system.SystemConfig;
import com.campus.domain.repository.system.SystemConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统配置服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-19
 */
@Service
@Transactional
public class SystemConfigServiceImpl implements SystemConfigService {

    private static final Logger log = LoggerFactory.getLogger(SystemConfigServiceImpl.class);

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    // ==================== 基础CRUD方法 ====================

    @Override
    public SystemConfig save(SystemConfig config) {
        try {
            if (config.getId() == null) {
                config.setCreatedAt(LocalDateTime.now());
            }
            config.setUpdatedAt(LocalDateTime.now());
            
            SystemConfig saved = systemConfigRepository.save(config);
            refreshCache(); // 刷新缓存
            
            log.info("保存系统配置成功: configKey={}", config.getConfigKey());
            return saved;
        } catch (Exception e) {
            log.error("保存系统配置失败: configKey={}", config.getConfigKey(), e);
            throw new RuntimeException("保存系统配置失败", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SystemConfig> findById(Long id) {
        try {
            return systemConfigRepository.findById(id);
        } catch (Exception e) {
            log.error("根据ID查找系统配置失败: id={}", id, e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemConfig> findAll() {
        try {
            return systemConfigRepository.findAll();
        } catch (Exception e) {
            log.error("查找所有系统配置失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SystemConfig> findAll(Pageable pageable) {
        try {
            return systemConfigRepository.findAll(pageable);
        } catch (Exception e) {
            log.error("分页查找系统配置失败", e);
            return Page.empty(pageable);
        }
    }

    @Override
    @CacheEvict(value = "systemConfig", allEntries = true)
    public void deleteById(Long id) {
        try {
            Optional<SystemConfig> configOpt = findById(id);
            if (configOpt.isPresent()) {
                SystemConfig config = configOpt.get();
                if (config.getIsSystem()) {
                    throw new RuntimeException("系统配置不允许删除");
                }
                systemConfigRepository.deleteById(id);
                log.info("删除系统配置成功: id={}, configKey={}", id, config.getConfigKey());
            }
        } catch (Exception e) {
            log.error("删除系统配置失败: id={}", id, e);
            throw new RuntimeException("删除系统配置失败", e);
        }
    }

    @Override
    @CacheEvict(value = "systemConfig", allEntries = true)
    public void deleteByIds(List<Long> ids) {
        try {
            List<SystemConfig> configs = systemConfigRepository.findAllById(ids);
            List<Long> deletableIds = configs.stream()
                .filter(config -> !config.getIsSystem() && config.getDeleted() == 0)
                .map(SystemConfig::getId)
                .collect(Collectors.toList());
                
            if (!deletableIds.isEmpty()) {
                systemConfigRepository.deleteAllById(deletableIds);
                log.info("批量删除系统配置成功: count={}", deletableIds.size());
            }
        } catch (Exception e) {
            log.error("批量删除系统配置失败: ids={}", ids, e);
            throw new RuntimeException("批量删除系统配置失败", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        try {
            return systemConfigRepository.count();
        } catch (Exception e) {
            log.error("统计系统配置数量失败", e);
            return 0;
        }
    }

    // ==================== 业务查询方法 ====================

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "systemConfig", key = "#configKey")
    public Optional<SystemConfig> findByConfigKey(String configKey) {
        try {
            return systemConfigRepository.findByConfigKey(configKey);
        } catch (Exception e) {
            log.error("根据配置键查找系统配置失败: configKey={}", configKey, e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemConfig> findByConfigGroup(String configGroup) {
        try {
            return findAll().stream()
                .filter(config -> configGroup.equals(config.getConfigGroup()))
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据配置分组查找系统配置失败: configGroup={}", configGroup, e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SystemConfig> findByConfigGroup(String configGroup, Pageable pageable) {
        try {
            List<SystemConfig> filtered = findByConfigGroup(configGroup);
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), filtered.size());
            List<SystemConfig> pageContent = start < filtered.size() ?
                filtered.subList(start, end) : new ArrayList<>();
            return new PageImpl<>(pageContent, pageable, filtered.size());
        } catch (Exception e) {
            log.error("分页根据配置分组查找系统配置失败: configGroup={}", configGroup, e);
            return Page.empty(pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemConfig> findByConfigType(String configType) {
        try {
            return findAll().stream()
                .filter(config -> configType.equals(config.getConfigType()))
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据配置类型查找系统配置失败: configType={}", configType, e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "systemConfig", key = "'activeConfigs'")
    public List<SystemConfig> findAllActiveConfigs() {
        try {
            return findAll().stream()
                .filter(config -> config.getStatus() != null && config.getStatus() == 1)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查找所有启用的系统配置失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SystemConfig> findAllActiveConfigs(Pageable pageable) {
        try {
            List<SystemConfig> activeConfigs = findAllActiveConfigs();
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), activeConfigs.size());
            List<SystemConfig> pageContent = start < activeConfigs.size() ?
                activeConfigs.subList(start, end) : new ArrayList<>();
            return new PageImpl<>(pageContent, pageable, activeConfigs.size());
        } catch (Exception e) {
            log.error("分页查找启用的系统配置失败", e);
            return Page.empty(pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SystemConfig> findByConditions(Map<String, Object> params, Pageable pageable) {
        try {
            Specification<SystemConfig> spec = buildSpecification(params);
            return systemConfigRepository.findAll(spec, pageable);
        } catch (Exception e) {
            log.error("条件查询系统配置失败: params={}", params, e);
            return Page.empty(pageable);
        }
    }

    // ==================== 配置值操作方法 ====================

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "configValue", key = "#configKey")
    public String getConfigValue(String configKey) {
        try {
            Optional<SystemConfig> configOpt = findByConfigKey(configKey);
            return configOpt.map(SystemConfig::getConfigValue).orElse(null);
        } catch (Exception e) {
            log.error("获取配置值失败: configKey={}", configKey, e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getConfigValue(String configKey, String defaultValue) {
        String value = getConfigValue(configKey);
        return value != null ? value : defaultValue;
    }

    @Override
    @CacheEvict(value = {"systemConfig", "configValue"}, allEntries = true)
    public void setConfigValue(String configKey, String configValue) {
        try {
            Optional<SystemConfig> configOpt = findByConfigKey(configKey);
            if (configOpt.isPresent()) {
                SystemConfig config = configOpt.get();
                if (!config.getIsEditable()) {
                    throw new RuntimeException("配置不允许编辑: " + configKey);
                }
                config.setConfigValue(configValue);
                config.setUpdatedAt(LocalDateTime.now());
                systemConfigRepository.save(config);
                log.info("设置配置值成功: configKey={}", configKey);
            } else {
                throw new RuntimeException("配置不存在: " + configKey);
            }
        } catch (Exception e) {
            log.error("设置配置值失败: configKey={}", configKey, e);
            throw new RuntimeException("设置配置值失败", e);
        }
    }

    @Override
    @CacheEvict(value = {"systemConfig", "configValue"}, allEntries = true)
    public void setConfigValues(Map<String, String> configMap) {
        try {
            for (Map.Entry<String, String> entry : configMap.entrySet()) {
                setConfigValue(entry.getKey(), entry.getValue());
            }
            log.info("批量设置配置值成功: count={}", configMap.size());
        } catch (Exception e) {
            log.error("批量设置配置值失败", e);
            throw new RuntimeException("批量设置配置值失败", e);
        }
    }

    // ==================== 配置分组管理 ====================

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "configGroups")
    public List<String> getAllConfigGroups() {
        try {
            return systemConfigRepository.findAllConfigGroups();
        } catch (Exception e) {
            log.error("获取所有配置分组失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getConfigGroupStats() {
        try {
            List<Object[]> results = systemConfigRepository.countByConfigGroup();
            return results.stream()
                .collect(Collectors.toMap(
                    result -> (String) result[0],
                    result -> (Long) result[1]
                ));
        } catch (Exception e) {
            log.error("获取配置分组统计失败", e);
            return new HashMap<>();
        }
    }

    // ==================== 配置验证方法 ====================

    @Override
    @Transactional(readOnly = true)
    public boolean existsByConfigKey(String configKey) {
        try {
            return systemConfigRepository.existsByConfigKey(configKey);
        } catch (Exception e) {
            log.error("检查配置键是否存在失败: configKey={}", configKey, e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByConfigKeyExcludeId(String configKey, Long excludeId) {
        try {
            return systemConfigRepository.existsByConfigKeyAndIdNot(configKey, excludeId);
        } catch (Exception e) {
            log.error("检查配置键是否存在失败: configKey={}, excludeId={}", configKey, excludeId, e);
            return false;
        }
    }

    @Override
    public boolean validateConfigValue(String configType, String configValue) {
        try {
            if (configValue == null) {
                return false;
            }

            switch (configType.toLowerCase()) {
                case "boolean":
                    return "true".equalsIgnoreCase(configValue) || "false".equalsIgnoreCase(configValue);
                case "number":
                    try {
                        Double.parseDouble(configValue);
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                case "json":
                    try {
                        // 简单的JSON格式验证
                        return configValue.trim().startsWith("{") && configValue.trim().endsWith("}") ||
                               configValue.trim().startsWith("[") && configValue.trim().endsWith("]");
                    } catch (Exception e) {
                        return false;
                    }
                case "string":
                default:
                    return true;
            }
        } catch (Exception e) {
            log.error("验证配置值格式失败: configType={}, configValue={}", configType, configValue, e);
            return false;
        }
    }

    // ==================== 配置缓存管理 ====================

    @Override
    @CacheEvict(value = {"systemConfig", "configValue", "configGroups"}, allEntries = true)
    public void refreshCache() {
        log.info("刷新系统配置缓存");
    }

    @Override
    @CacheEvict(value = {"systemConfig", "configValue", "configGroups"}, allEntries = true)
    public void clearCache() {
        log.info("清除系统配置缓存");
    }

    // ==================== 配置导入导出 ====================

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> exportConfigs(String configGroup) {
        try {
            List<SystemConfig> configs;
            if (configGroup != null && !configGroup.isEmpty()) {
                configs = findByConfigGroup(configGroup);
            } else {
                configs = findAllActiveConfigs();
            }

            Map<String, Object> exportData = new HashMap<>();
            exportData.put("exportTime", LocalDateTime.now());
            exportData.put("configGroup", configGroup);
            exportData.put("totalCount", configs.size());
            exportData.put("configs", configs);

            log.info("导出配置成功: configGroup={}, count={}", configGroup, configs.size());
            return exportData;
        } catch (Exception e) {
            log.error("导出配置失败: configGroup={}", configGroup, e);
            throw new RuntimeException("导出配置失败", e);
        }
    }

    @Override
    @CacheEvict(value = {"systemConfig", "configValue", "configGroups"}, allEntries = true)
    public void importConfigs(List<SystemConfig> configs) {
        try {
            int successCount = 0;
            int skipCount = 0;

            for (SystemConfig config : configs) {
                try {
                    if (existsByConfigKey(config.getConfigKey())) {
                        skipCount++;
                        continue;
                    }

                    config.setId(null); // 确保是新增
                    config.setCreatedAt(LocalDateTime.now());
                    config.setUpdatedAt(LocalDateTime.now());
                    save(config);
                    successCount++;
                } catch (Exception e) {
                    log.warn("导入配置失败: configKey={}", config.getConfigKey(), e);
                }
            }

            log.info("导入配置完成: total={}, success={}, skip={}", configs.size(), successCount, skipCount);
        } catch (Exception e) {
            log.error("导入配置失败", e);
            throw new RuntimeException("导入配置失败", e);
        }
    }

    // ==================== 配置统计方法 ====================

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getConfigStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCount", count());
            stats.put("activeCount", findAllActiveConfigs().size());
            stats.put("groupStats", getConfigGroupStats());
            stats.put("typeStats", getConfigTypeStats());
            stats.put("systemConfigCount", findAll().stream().filter(SystemConfig::getIsSystem).count());
            stats.put("editableConfigCount", findAll().stream().filter(SystemConfig::getIsEditable).count());

            return stats;
        } catch (Exception e) {
            log.error("获取配置统计信息失败", e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemConfig> getRecentUpdatedConfigs(int limit) {
        try {
            return findAll().stream()
                .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                .limit(limit)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取最近更新的配置失败: limit={}", limit, e);
            return new ArrayList<>();
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 构建查询条件
     */
    private Specification<SystemConfig> buildSpecification(Map<String, Object> params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 基础条件：未删除
            predicates.add(criteriaBuilder.equal(root.get("deleted"), 0));

            // 配置键模糊查询
            if (params.containsKey("configKey") && params.get("configKey") != null) {
                String configKey = params.get("configKey").toString();
                predicates.add(criteriaBuilder.like(root.get("configKey"), "%" + configKey + "%"));
            }

            // 配置分组
            if (params.containsKey("configGroup") && params.get("configGroup") != null) {
                String configGroup = params.get("configGroup").toString();
                predicates.add(criteriaBuilder.equal(root.get("configGroup"), configGroup));
            }

            // 配置类型
            if (params.containsKey("configType") && params.get("configType") != null) {
                String configType = params.get("configType").toString();
                predicates.add(criteriaBuilder.equal(root.get("configType"), configType));
            }

            // 状态
            if (params.containsKey("status") && params.get("status") != null) {
                Integer status = Integer.valueOf(params.get("status").toString());
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // 是否系统配置
            if (params.containsKey("isSystem") && params.get("isSystem") != null) {
                Boolean isSystem = Boolean.valueOf(params.get("isSystem").toString());
                predicates.add(criteriaBuilder.equal(root.get("isSystem"), isSystem));
            }

            // 关键词搜索
            if (params.containsKey("keyword") && params.get("keyword") != null) {
                String keyword = params.get("keyword").toString();
                Predicate keywordPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(root.get("configKey"), "%" + keyword + "%"),
                    criteriaBuilder.like(root.get("configName"), "%" + keyword + "%"),
                    criteriaBuilder.like(root.get("description"), "%" + keyword + "%")
                );
                predicates.add(keywordPredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 获取配置类型统计
     */
    private Map<String, Long> getConfigTypeStats() {
        try {
            return findAll().stream()
                .collect(Collectors.groupingBy(
                    config -> config.getConfigType() != null ? config.getConfigType() : "unknown",
                    Collectors.counting()
                ));
        } catch (Exception e) {
            log.error("获取配置类型统计失败", e);
            return new HashMap<>();
        }
    }
}
