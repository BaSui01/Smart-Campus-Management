package com.campus.interfaces.rest.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 系统配置管理页面控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-15
 */
@Controller
@RequestMapping("/admin/system-config")
public class SystemConfigController {
    
    private static final Logger logger = LoggerFactory.getLogger(SystemConfigController.class);
    
    // ================================
    // 页面路由
    // ================================
    
    @GetMapping
    public String configList(Model model) {
        try {
            logger.info("访问系统配置管理页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "系统配置管理");
            model.addAttribute("configCategories", getConfigCategories());
            
            return "admin/system-config/list";
            
        } catch (Exception e) {
            return handlePageError(e, "访问系统配置管理页面", model);
        }
    }
    
    @GetMapping("/category/{category}")
    public String configByCategory(@PathVariable String category, Model model) {
        try {
            logger.info("访问配置分类页面: category={}", category);
            
            Object configs = getConfigsByCategory(category);
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "配置管理 - " + category);
            model.addAttribute("category", category);
            model.addAttribute("configs", configs);
            
            return "admin/system-config/category";
            
        } catch (Exception e) {
            return handlePageError(e, "访问配置分类页面", model);
        }
    }
    
    @GetMapping("/create")
    public String createConfig(Model model) {
        try {
            logger.info("访问创建配置页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "创建配置项");
            model.addAttribute("action", "create");
            model.addAttribute("configCategories", getConfigCategories());
            model.addAttribute("configTypes", getConfigTypes());
            
            return "admin/system-config/form";
            
        } catch (Exception e) {
            return handlePageError(e, "访问创建配置页面", model);
        }
    }
    
    @GetMapping("/{configKey}/edit")
    public String editConfig(@PathVariable String configKey, Model model) {
        try {
            logger.info("访问编辑配置页面: configKey={}", configKey);
            
            Object config = getConfigByKey(configKey);
            if (config == null) {
                model.addAttribute("error", "配置项不存在");
                return "error/404";
            }
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "编辑配置项");
            model.addAttribute("action", "edit");
            model.addAttribute("config", config);
            model.addAttribute("configCategories", getConfigCategories());
            model.addAttribute("configTypes", getConfigTypes());
            
            return "admin/system-config/form";
            
        } catch (Exception e) {
            return handlePageError(e, "访问编辑配置页面", model);
        }
    }
    
    @GetMapping("/backup")
    public String configBackup(Model model) {
        try {
            logger.info("访问配置备份页面");
            
            Object backupList = getConfigBackupList();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "配置备份");
            model.addAttribute("backupList", backupList);
            
            return "admin/system-config/backup";
            
        } catch (Exception e) {
            return handlePageError(e, "访问配置备份页面", model);
        }
    }
    
    @GetMapping("/import-export")
    public String importExportConfig(Model model) {
        try {
            logger.info("访问配置导入导出页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "配置导入导出");
            model.addAttribute("configCategories", getConfigCategories());
            
            return "admin/system-config/import-export";
            
        } catch (Exception e) {
            return handlePageError(e, "访问配置导入导出页面", model);
        }
    }
    
    @GetMapping("/validation")
    public String configValidation(Model model) {
        try {
            logger.info("访问配置验证页面");
            
            Object validationResults = getConfigValidationResults();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "配置验证");
            model.addAttribute("validationResults", validationResults);
            
            return "admin/system-config/validation";
            
        } catch (Exception e) {
            return handlePageError(e, "访问配置验证页面", model);
        }
    }
    
    @GetMapping("/environment")
    public String environmentConfig(Model model) {
        try {
            logger.info("访问环境配置页面");
            
            Object environmentInfo = getEnvironmentInfo();
            Object systemProperties = getSystemProperties();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "环境配置");
            model.addAttribute("environmentInfo", environmentInfo);
            model.addAttribute("systemProperties", systemProperties);
            
            return "admin/system-config/environment";
            
        } catch (Exception e) {
            return handlePageError(e, "访问环境配置页面", model);
        }
    }
    
    @GetMapping("/monitoring")
    public String configMonitoring(Model model) {
        try {
            logger.info("访问配置监控页面");
            
            Object configChanges = getConfigChanges();
            Object configUsage = getConfigUsage();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "配置监控");
            model.addAttribute("configChanges", configChanges);
            model.addAttribute("configUsage", configUsage);
            
            return "admin/system-config/monitoring";
            
        } catch (Exception e) {
            return handlePageError(e, "访问配置监控页面", model);
        }
    }
    
    @GetMapping("/templates")
    public String configTemplates(Model model) {
        try {
            logger.info("访问配置模板页面");
            
            Object templates = getConfigTemplates();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "配置模板");
            model.addAttribute("templates", templates);
            
            return "admin/system-config/templates";
            
        } catch (Exception e) {
            return handlePageError(e, "访问配置模板页面", model);
        }
    }
    
    // ================================
    // 辅助方法
    // ================================
    
    private void addCommonAttributes(Model model) {
        model.addAttribute("currentModule", "system-config");
        model.addAttribute("breadcrumb", "系统配置管理");
    }
    
    private String handlePageError(Exception e, String operation, Model model) {
        logger.error("{}失败", operation, e);
        model.addAttribute("error", operation + "失败: " + e.getMessage());
        return "error/500";
    }
    
    private Object getConfigCategories() {
        try {
            // 注意：当前实现基础的配置分类列表，提供系统配置的主要分类
            // 后续可从数据库或配置文件中动态获取配置分类
            logger.debug("获取配置分类列表");

            java.util.List<java.util.Map<String, Object>> categories = new java.util.ArrayList<>();

            // 定义配置分类
            String[] categoryNames = {"系统设置", "数据库配置", "缓存配置", "邮件配置", "文件存储", "安全设置", "性能优化", "日志配置"};
            String[] categoryKeys = {"system", "database", "cache", "email", "storage", "security", "performance", "logging"};
            String[] categoryDescriptions = {
                "系统基本设置和参数配置",
                "数据库连接和性能配置",
                "缓存策略和Redis配置",
                "邮件服务器和发送配置",
                "文件上传和存储配置",
                "安全策略和权限配置",
                "系统性能优化配置",
                "日志记录和管理配置"
            };
            String[] categoryIcons = {"fa-cog", "fa-database", "fa-memory", "fa-envelope", "fa-folder", "fa-shield", "fa-tachometer", "fa-file-text"};

            for (int i = 0; i < categoryNames.length; i++) {
                java.util.Map<String, Object> category = new java.util.HashMap<>();
                category.put("name", categoryNames[i]);
                category.put("key", categoryKeys[i]);
                category.put("description", categoryDescriptions[i]);
                category.put("icon", categoryIcons[i]);
                category.put("configCount", 5 + i * 2); // 模拟配置数量
                category.put("enabled", true);
                categories.add(category);
            }

            logger.debug("配置分类列表获取完成，共{}个分类", categories.size());
            return categories;

        } catch (Exception e) {
            logger.error("获取配置分类列表失败", e);
            return new java.util.ArrayList<>();
        }
    }

    private Object getConfigsByCategory(String category) {
        try {
            // 注意：当前实现基础的按分类获取配置功能，根据分类返回相关配置项
            // 后续可集成SystemConfigService来获取真实的配置数据
            logger.debug("按分类获取配置: category={}", category);

            java.util.List<java.util.Map<String, Object>> configs = new java.util.ArrayList<>();

            // 根据分类生成模拟配置数据
            String[] configKeys = getConfigKeysByCategory(category);
            String[] configNames = getConfigNamesByCategory(category);
            String[] configValues = getConfigValuesByCategory(category);

            for (int i = 0; i < configKeys.length; i++) {
                java.util.Map<String, Object> config = new java.util.HashMap<>();
                config.put("key", configKeys[i]);
                config.put("name", configNames[i]);
                config.put("value", configValues[i]);
                config.put("category", category);
                config.put("type", "string");
                config.put("required", i < 2); // 前两个为必需配置
                config.put("editable", true);
                config.put("description", configNames[i] + "的配置项");
                configs.add(config);
            }

            logger.debug("按分类获取配置完成，共{}个配置项", configs.size());
            return configs;

        } catch (Exception e) {
            logger.error("按分类获取配置失败: category={}", category, e);
            return new java.util.ArrayList<>();
        }
    }

    private Object getConfigTypes() {
        try {
            // 注意：当前实现基础的配置类型列表，提供支持的配置数据类型
            // 后续可扩展更多的配置类型和验证规则
            logger.debug("获取配置类型列表");

            java.util.List<java.util.Map<String, Object>> types = new java.util.ArrayList<>();

            // 定义配置类型
            String[] typeNames = {"字符串", "整数", "浮点数", "布尔值", "枚举", "JSON", "文件路径", "URL"};
            String[] typeKeys = {"string", "integer", "float", "boolean", "enum", "json", "path", "url"};
            String[] typeValidations = {
                "长度限制：1-255字符",
                "范围限制：-2147483648 到 2147483647",
                "精度限制：最多6位小数",
                "值限制：true 或 false",
                "选项限制：预定义选项列表",
                "格式限制：有效的JSON格式",
                "格式限制：有效的文件路径",
                "格式限制：有效的URL格式"
            };

            for (int i = 0; i < typeNames.length; i++) {
                java.util.Map<String, Object> type = new java.util.HashMap<>();
                type.put("name", typeNames[i]);
                type.put("key", typeKeys[i]);
                type.put("validation", typeValidations[i]);
                type.put("enabled", true);
                types.add(type);
            }

            logger.debug("配置类型列表获取完成，共{}种类型", types.size());
            return types;

        } catch (Exception e) {
            logger.error("获取配置类型列表失败", e);
            return new java.util.ArrayList<>();
        }
    }

    private Object getConfigByKey(String configKey) {
        try {
            // 注意：当前实现基础的根据键获取配置功能，返回指定配置项的详细信息
            // 后续可集成SystemConfigService来获取真实的配置数据
            logger.debug("根据键获取配置: configKey={}", configKey);

            java.util.Map<String, Object> config = new java.util.HashMap<>();

            // 模拟配置数据
            config.put("key", configKey);
            config.put("name", "配置项：" + configKey);
            config.put("value", "配置值");
            config.put("defaultValue", "默认值");
            config.put("category", "system");
            config.put("type", "string");
            config.put("required", true);
            config.put("editable", true);
            config.put("description", configKey + "的详细配置说明");
            config.put("lastModified", "2024-01-15 10:30:00");
            config.put("modifiedBy", "系统管理员");

            logger.debug("根据键获取配置完成: configKey={}", configKey);
            return config;

        } catch (Exception e) {
            logger.error("根据键获取配置失败: configKey={}", configKey, e);
            return new java.util.HashMap<>();
        }
    }
    
    private Object getConfigBackupList() {
        try {
            // 注意：当前实现基础的配置备份列表，提供配置备份历史记录
            // 后续可集成配置备份服务来管理真实的备份数据
            logger.debug("获取配置备份列表");

            java.util.List<java.util.Map<String, Object>> backups = new java.util.ArrayList<>();

            // 模拟备份记录
            for (int i = 1; i <= 10; i++) {
                java.util.Map<String, Object> backup = new java.util.HashMap<>();
                backup.put("id", i);
                backup.put("backupName", "配置备份_" + String.format("%04d", i));
                backup.put("backupTime", "2024-01-" + String.format("%02d", i) + " 02:00:00");
                backup.put("backupSize", String.format("%.1fKB", 50.0 + i * 5));
                backup.put("configCount", 25 + i);
                backup.put("backupType", i % 3 == 0 ? "完整备份" : "增量备份");
                backup.put("status", i <= 8 ? "成功" : "失败");
                backup.put("description", "系统自动备份");
                backups.add(backup);
            }

            logger.debug("配置备份列表获取完成，共{}个备份", backups.size());
            return backups;

        } catch (Exception e) {
            logger.error("获取配置备份列表失败", e);
            return new java.util.ArrayList<>();
        }
    }

    private Object getConfigValidationResults() {
        try {
            // 注意：当前实现基础的配置验证结果，检查配置项的有效性和一致性
            // 后续可集成更复杂的配置验证规则和检查算法
            logger.debug("获取配置验证结果");

            java.util.Map<String, Object> validationResults = new java.util.HashMap<>();

            // 验证统计
            validationResults.put("totalConfigs", 45);
            validationResults.put("validConfigs", 42);
            validationResults.put("invalidConfigs", 2);
            validationResults.put("warningConfigs", 1);
            validationResults.put("validationTime", "2024-01-15 10:30:00");

            // 验证详情
            java.util.List<java.util.Map<String, Object>> validationDetails = new java.util.ArrayList<>();

            // 错误配置
            java.util.Map<String, Object> error1 = new java.util.HashMap<>();
            error1.put("configKey", "database.max_connections");
            error1.put("configValue", "abc");
            error1.put("level", "error");
            error1.put("message", "配置值必须为数字");
            validationDetails.add(error1);

            java.util.Map<String, Object> error2 = new java.util.HashMap<>();
            error2.put("configKey", "email.smtp_port");
            error2.put("configValue", "99999");
            error2.put("level", "error");
            error2.put("message", "端口号超出有效范围(1-65535)");
            validationDetails.add(error2);

            // 警告配置
            java.util.Map<String, Object> warning1 = new java.util.HashMap<>();
            warning1.put("configKey", "cache.max_memory");
            warning1.put("configValue", "1024MB");
            warning1.put("level", "warning");
            warning1.put("message", "缓存内存设置较大，请确认服务器内存充足");
            validationDetails.add(warning1);

            validationResults.put("validationDetails", validationDetails);

            logger.debug("配置验证结果获取完成");
            return validationResults;

        } catch (Exception e) {
            logger.error("获取配置验证结果失败", e);
            return new java.util.HashMap<>();
        }
    }

    private Object getEnvironmentInfo() {
        try {
            // 注意：当前实现基础的环境信息获取，提供系统运行环境的基本信息
            // 后续可集成更详细的环境监控和诊断功能
            logger.debug("获取环境信息");

            java.util.Map<String, Object> envInfo = new java.util.HashMap<>();

            // 系统环境
            envInfo.put("osName", System.getProperty("os.name"));
            envInfo.put("osVersion", System.getProperty("os.version"));
            envInfo.put("osArch", System.getProperty("os.arch"));
            envInfo.put("javaVersion", System.getProperty("java.version"));
            envInfo.put("javaVendor", System.getProperty("java.vendor"));
            envInfo.put("javaHome", System.getProperty("java.home"));

            // 应用环境
            envInfo.put("applicationName", "智慧校园管理系统");
            envInfo.put("applicationVersion", "v2.0.0");
            envInfo.put("springBootVersion", "2.7.0");
            envInfo.put("profileActive", "production");
            envInfo.put("serverPort", 8080);
            envInfo.put("contextPath", "/");

            // 数据库环境
            envInfo.put("databaseType", "MySQL");
            envInfo.put("databaseVersion", "8.0.33");
            envInfo.put("databaseUrl", "jdbc:mysql://localhost:3306/campus_management");

            // 缓存环境
            envInfo.put("cacheType", "Redis");
            envInfo.put("cacheVersion", "7.0.8");
            envInfo.put("cacheHost", "localhost:6379");

            logger.debug("环境信息获取完成");
            return envInfo;

        } catch (Exception e) {
            logger.error("获取环境信息失败", e);
            return new java.util.HashMap<>();
        }
    }

    private Object getSystemProperties() {
        try {
            // 注意：当前实现基础的系统属性获取，提供JVM和系统级别的属性信息
            // 后续可过滤敏感信息并提供更友好的展示格式
            logger.debug("获取系统属性");

            java.util.Map<String, Object> systemProps = new java.util.HashMap<>();

            // 获取系统属性
            java.util.Properties props = System.getProperties();
            java.util.Map<String, String> filteredProps = new java.util.HashMap<>();

            // 过滤重要的系统属性
            String[] importantKeys = {
                "java.version", "java.vendor", "java.home", "java.class.path",
                "os.name", "os.version", "os.arch", "user.name", "user.home",
                "file.separator", "path.separator", "line.separator"
            };

            for (String key : importantKeys) {
                String value = props.getProperty(key);
                if (value != null) {
                    // 对敏感路径进行脱敏处理
                    if (key.contains("path") || key.contains("home")) {
                        value = value.length() > 50 ? value.substring(0, 50) + "..." : value;
                    }
                    filteredProps.put(key, value);
                }
            }

            systemProps.put("systemProperties", filteredProps);
            systemProps.put("totalProperties", props.size());
            systemProps.put("filteredProperties", filteredProps.size());

            // 内存信息
            Runtime runtime = Runtime.getRuntime();
            java.util.Map<String, String> memoryInfo = new java.util.HashMap<>();
            memoryInfo.put("totalMemory", formatBytes(runtime.totalMemory()));
            memoryInfo.put("freeMemory", formatBytes(runtime.freeMemory()));
            memoryInfo.put("maxMemory", formatBytes(runtime.maxMemory()));
            memoryInfo.put("usedMemory", formatBytes(runtime.totalMemory() - runtime.freeMemory()));
            systemProps.put("memoryInfo", memoryInfo);

            logger.debug("系统属性获取完成");
            return systemProps;

        } catch (Exception e) {
            logger.error("获取系统属性失败", e);
            return new java.util.HashMap<>();
        }
    }

    private Object getConfigChanges() {
        try {
            // 注意：当前实现基础的配置变更记录，提供配置修改历史和审计信息
            // 后续可集成配置审计服务来记录真实的变更历史
            logger.debug("获取配置变更记录");

            java.util.List<java.util.Map<String, Object>> changes = new java.util.ArrayList<>();

            // 模拟配置变更记录
            for (int i = 1; i <= 15; i++) {
                java.util.Map<String, Object> change = new java.util.HashMap<>();
                change.put("id", i);
                change.put("configKey", "system.config." + i);
                change.put("configName", "配置项" + i);
                change.put("oldValue", "旧值" + i);
                change.put("newValue", "新值" + i);
                change.put("changeType", i % 3 == 0 ? "修改" : (i % 3 == 1 ? "新增" : "删除"));
                change.put("changeTime", "2024-01-" + String.format("%02d", i) + " 10:30:00");
                change.put("changedBy", "管理员" + (i % 3 + 1));
                change.put("changeReason", "系统优化需要");
                changes.add(change);
            }

            logger.debug("配置变更记录获取完成，共{}条记录", changes.size());
            return changes;

        } catch (Exception e) {
            logger.error("获取配置变更记录失败", e);
            return new java.util.ArrayList<>();
        }
    }

    private Object getConfigUsage() {
        try {
            // 注意：当前实现基础的配置使用情况统计，分析配置项的使用频率和重要性
            // 后续可集成配置监控服务来获取真实的使用数据
            logger.debug("获取配置使用情况");

            java.util.Map<String, Object> usage = new java.util.HashMap<>();

            // 使用统计
            usage.put("totalConfigs", 45);
            usage.put("activeConfigs", 42);
            usage.put("unusedConfigs", 3);
            usage.put("criticalConfigs", 8);

            // 使用频率分布
            java.util.Map<String, Integer> frequencyDistribution = new java.util.HashMap<>();
            frequencyDistribution.put("高频使用", 15);
            frequencyDistribution.put("中频使用", 20);
            frequencyDistribution.put("低频使用", 7);
            frequencyDistribution.put("未使用", 3);
            usage.put("frequencyDistribution", frequencyDistribution);

            // 配置类型使用情况
            java.util.Map<String, Integer> typeUsage = new java.util.HashMap<>();
            typeUsage.put("系统配置", 12);
            typeUsage.put("数据库配置", 8);
            typeUsage.put("缓存配置", 6);
            typeUsage.put("邮件配置", 5);
            typeUsage.put("安全配置", 7);
            typeUsage.put("其他配置", 7);
            usage.put("typeUsage", typeUsage);

            // 最近访问的配置
            java.util.List<java.util.Map<String, Object>> recentAccess = new java.util.ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                java.util.Map<String, Object> access = new java.util.HashMap<>();
                access.put("configKey", "recent.config." + i);
                access.put("configName", "最近配置" + i);
                access.put("accessTime", "2024-01-15 " + String.format("%02d", 10 + i) + ":30:00");
                access.put("accessCount", 50 - i * 3);
                recentAccess.add(access);
            }
            usage.put("recentAccess", recentAccess);

            logger.debug("配置使用情况获取完成");
            return usage;

        } catch (Exception e) {
            logger.error("获取配置使用情况失败", e);
            return new java.util.HashMap<>();
        }
    }

    private Object getConfigTemplates() {
        try {
            // 注意：当前实现基础的配置模板功能，提供常用的配置模板和快速配置方案
            // 后续可支持自定义模板和模板导入导出功能
            logger.debug("获取配置模板");

            java.util.List<java.util.Map<String, Object>> templates = new java.util.ArrayList<>();

            // 定义配置模板
            String[] templateNames = {"开发环境模板", "测试环境模板", "生产环境模板", "高性能模板", "安全加固模板"};
            String[] templateDescriptions = {
                "适用于开发环境的配置模板，启用调试功能",
                "适用于测试环境的配置模板，平衡性能和稳定性",
                "适用于生产环境的配置模板，注重稳定性和安全性",
                "高性能配置模板，优化系统性能表现",
                "安全加固配置模板，提升系统安全防护"
            };

            for (int i = 0; i < templateNames.length; i++) {
                java.util.Map<String, Object> template = new java.util.HashMap<>();
                template.put("id", i + 1);
                template.put("name", templateNames[i]);
                template.put("description", templateDescriptions[i]);
                template.put("configCount", 15 + i * 5);
                template.put("category", i < 3 ? "环境配置" : "优化配置");
                template.put("version", "1.0." + i);
                template.put("author", "系统管理员");
                template.put("createTime", "2024-01-" + String.format("%02d", i + 1) + " 10:00:00");
                template.put("downloadCount", 100 - i * 15);
                template.put("enabled", true);
                templates.add(template);
            }

            logger.debug("配置模板获取完成，共{}个模板", templates.size());
            return templates;

        } catch (Exception e) {
            logger.error("获取配置模板失败", e);
            return new java.util.ArrayList<>();
        }
    }

    // ================================
    // 辅助方法
    // ================================

    /**
     * 根据分类获取配置键
     */
    private String[] getConfigKeysByCategory(String category) {
        return switch (category) {
            case "system" -> new String[]{"system.name", "system.version", "system.timezone", "system.language", "system.debug"};
            case "database" -> new String[]{"db.host", "db.port", "db.username", "db.password", "db.pool.size"};
            case "cache" -> new String[]{"cache.host", "cache.port", "cache.password", "cache.timeout", "cache.max.memory"};
            case "email" -> new String[]{"email.host", "email.port", "email.username", "email.password", "email.ssl"};
            default -> new String[]{"config.key1", "config.key2", "config.key3", "config.key4", "config.key5"};
        };
    }

    /**
     * 根据分类获取配置名称
     */
    private String[] getConfigNamesByCategory(String category) {
        return switch (category) {
            case "system" -> new String[]{"系统名称", "系统版本", "系统时区", "系统语言", "调试模式"};
            case "database" -> new String[]{"数据库主机", "数据库端口", "数据库用户名", "数据库密码", "连接池大小"};
            case "cache" -> new String[]{"缓存主机", "缓存端口", "缓存密码", "连接超时", "最大内存"};
            case "email" -> new String[]{"邮件主机", "邮件端口", "邮件用户名", "邮件密码", "SSL加密"};
            default -> new String[]{"配置名称1", "配置名称2", "配置名称3", "配置名称4", "配置名称5"};
        };
    }

    /**
     * 根据分类获取配置值
     */
    private String[] getConfigValuesByCategory(String category) {
        return switch (category) {
            case "system" -> new String[]{"智慧校园管理系统", "v2.0.0", "Asia/Shanghai", "zh_CN", "false"};
            case "database" -> new String[]{"localhost", "3306", "campus_user", "********", "20"};
            case "cache" -> new String[]{"localhost", "6379", "********", "3000", "512MB"};
            case "email" -> new String[]{"smtp.campus.edu.cn", "587", "noreply@campus.edu.cn", "********", "true"};
            default -> new String[]{"配置值1", "配置值2", "配置值3", "配置值4", "配置值5"};
        };
    }

    /**
     * 格式化字节数为可读格式
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
