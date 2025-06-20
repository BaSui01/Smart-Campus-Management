package com.campus.interfaces.rest.v1.system;

import com.campus.application.service.system.SystemConfigService;
import com.campus.shared.common.ApiResponse;
import com.campus.domain.entity.system.SystemConfig;
import com.campus.interfaces.rest.common.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 系统配置管理API控制器
 * 提供系统配置的增删改查、配置分组、配置导入导出等功能
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-08
 */
@RestController
@RequestMapping("/api/v1/system-configs")
@Tag(name = "系统配置管理", description = "系统配置相关的API接口")
public class SystemConfigApiController extends BaseController {

    @Autowired
    private SystemConfigService systemConfigService;

    // ==================== 统计端点 ====================

    /**
     * 获取系统配置统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取系统配置统计信息", description = "获取系统配置模块的统计数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemConfigStats() {
        try {
            log.info("获取系统配置统计信息");

            // 使用真实的SystemConfigService获取统计数据
            Map<String, Object> stats = systemConfigService.getConfigStatistics();

            // 如果Service返回的数据为空，提供基础数据
            if (stats == null || stats.isEmpty()) {
                stats = new HashMap<>();
                stats.put("totalConfigs", 0L);
                stats.put("activeConfigs", 0L);
                stats.put("inactiveConfigs", 0L);
            }

            // 获取最近更新的配置作为活动记录
            List<SystemConfig> recentConfigs = systemConfigService.getRecentUpdatedConfigs(10);
            stats.put("recentActivity", recentConfigs);

            return success("获取系统配置统计信息成功", stats);

        } catch (Exception e) {
            log.error("获取系统配置统计信息失败: ", e);
            return error("获取系统配置统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询系统配置列表
     */
    @GetMapping
    @Operation(summary = "分页查询系统配置列表", description = "支持按配置分组、配置键、状态等条件搜索")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemConfigs(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "配置分组") @RequestParam(required = false) String configGroup,
            @Parameter(description = "配置键") @RequestParam(required = false) String configKey,
            @Parameter(description = "配置状态") @RequestParam(required = false) String status,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "configGroup") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "asc") String sortDir) {

        try {
            logOperation("查询系统配置列表", page, size, configGroup);

            // 验证分页参数
            validatePageParams(page, size);

            // 创建分页对象
            Pageable pageable = createPageable(page, size, sortBy, sortDir);

            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            if (StringUtils.hasText(configGroup)) {
                params.put("configGroup", configGroup);
            }
            if (StringUtils.hasText(configKey)) {
                params.put("configKey", configKey);
            }
            if (StringUtils.hasText(status)) {
                params.put("status", status);
            }
            if (StringUtils.hasText(keyword)) {
                params.put("keyword", keyword);
            }

            // 执行查询 - 使用真实的SystemConfigService
            Page<SystemConfig> configPage = systemConfigService.findByConditions(params, pageable);

            // 构建响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", configPage.getContent());
            responseData.put("totalElements", configPage.getTotalElements());
            responseData.put("totalPages", configPage.getTotalPages());
            responseData.put("currentPage", page);
            responseData.put("pageSize", size);
            responseData.put("hasNext", configPage.hasNext());
            responseData.put("hasPrevious", configPage.hasPrevious());

            return success("查询系统配置列表成功", responseData);

        } catch (Exception e) {
            log.error("查询系统配置列表失败: ", e);
            return error("查询系统配置列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询系统配置详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询系统配置详情", description = "根据配置ID查询详细信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<SystemConfig>> getSystemConfigById(
            @Parameter(description = "配置ID") @PathVariable Long id) {

        try {
            logOperation("查询系统配置详情", id);
            validateId(id, "系统配置");

            // 使用真实的SystemConfigService查询配置
            Optional<SystemConfig> configOpt = systemConfigService.findById(id);
            if (configOpt.isPresent()) {
                return success("查询系统配置详情成功", configOpt.get());
            } else {
                return error("系统配置不存在");
            }

        } catch (Exception e) {
            log.error("查询系统配置详情失败: ", e);
            return error("查询系统配置详情失败: " + e.getMessage());
        }
    }

    /**
     * 根据配置键查询配置值
     */
    @GetMapping("/key/{configKey}")
    @Operation(summary = "根据配置键查询配置值", description = "根据配置键查询配置值")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getConfigByKey(
            @Parameter(description = "配置键") @PathVariable String configKey) {

        try {
            logOperation("根据配置键查询配置值", configKey);

            // 使用真实的SystemConfigService查询配置值
            String configValue = systemConfigService.getConfigValue(configKey);
            Map<String, Object> result = new HashMap<>();
            result.put("configKey", configKey);
            result.put("configValue", configValue);
            result.put("exists", configValue != null);

            return success("查询配置值成功", result);

        } catch (Exception e) {
            log.error("根据配置键查询配置值失败: ", e);
            return error("查询配置值失败: " + e.getMessage());
        }
    }

    /**
     * 根据配置分组查询配置列表
     */
    @GetMapping("/group/{configGroup}")
    @Operation(summary = "根据配置分组查询配置列表", description = "查询指定分组的所有配置")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<List<SystemConfig>>> getConfigsByGroup(
            @Parameter(description = "配置分组") @PathVariable String configGroup) {

        try {
            logOperation("根据配置分组查询配置列表", configGroup);

            // 使用真实的SystemConfigService查询配置分组
            List<SystemConfig> configs = systemConfigService.findByConfigGroup(configGroup);
            return success("查询配置分组列表成功", configs);

        } catch (Exception e) {
            log.error("根据配置分组查询配置列表失败: ", e);
            return error("查询配置列表失败: " + e.getMessage());
        }
    }

    /**
     * 创建系统配置
     */
    @PostMapping
    @Operation(summary = "创建系统配置", description = "添加新的系统配置")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<SystemConfig>> createSystemConfig(
            @Parameter(description = "系统配置信息") @Valid @RequestBody SystemConfig config) {

        try {
            logOperation("创建系统配置", config.getConfigKey());

            // 验证配置数据
            validateConfigData(config);

            // 使用真实的SystemConfigService创建配置
            SystemConfig savedConfig = systemConfigService.save(config);
            return success("系统配置创建成功", savedConfig);

        } catch (Exception e) {
            log.error("创建系统配置失败: ", e);
            return error("创建系统配置失败: " + e.getMessage());
        }
    }

    /**
     * 更新系统配置
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新系统配置", description = "更新系统配置信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<SystemConfig>> updateSystemConfig(
            @Parameter(description = "配置ID") @PathVariable Long id,
            @Parameter(description = "系统配置信息") @Valid @RequestBody SystemConfig config) {

        try {
            logOperation("更新系统配置", id);
            validateId(id, "系统配置");

            // 使用真实的SystemConfigService更新配置
            config.setId(id);
            SystemConfig updatedConfig = systemConfigService.save(config);
            return success("系统配置更新成功", updatedConfig);

        } catch (Exception e) {
            log.error("更新系统配置失败: ", e);
            return error("更新系统配置失败: " + e.getMessage());
        }
    }

    /**
     * 删除系统配置
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除系统配置", description = "删除指定的系统配置")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSystemConfig(
            @Parameter(description = "配置ID") @PathVariable Long id) {

        try {
            logOperation("删除系统配置", id);
            validateId(id, "系统配置");

            // 使用真实的SystemConfigService删除配置
            systemConfigService.deleteById(id);
            return success("系统配置删除成功");

        } catch (Exception e) {
            log.error("删除系统配置失败: ", e);
            return error("删除系统配置失败: " + e.getMessage());
        }
    }

    // ==================== 批量操作端点 ====================

    /**
     * 批量删除系统配置
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除系统配置", description = "批量删除指定的系统配置")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchDeleteConfigs(
            @Parameter(description = "配置ID列表") @RequestBody List<Long> ids) {

        try {
            logOperation("批量删除系统配置", ids.size());

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("配置ID列表不能为空");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "系统配置");
            }

            // 使用真实的SystemConfigService执行批量删除
            systemConfigService.deleteByIds(ids);
            int successCount = ids.size();
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", ids.size());
            responseData.put("failReasons", failReasons);

            return success("批量删除系统配置成功", responseData);

        } catch (Exception e) {
            log.error("批量删除系统配置失败: ", e);
            return error("批量删除系统配置失败: " + e.getMessage());
        }
    }

    /**
     * 批量更新配置
     */
    @PutMapping("/batch")
    @Operation(summary = "批量更新配置", description = "批量更新多个系统配置")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchUpdateConfigs(
            @Parameter(description = "配置列表") @RequestBody List<SystemConfig> configs) {

        try {
            logOperation("批量更新配置", configs.size());

            if (configs == null || configs.isEmpty()) {
                return badRequest("配置列表不能为空");
            }

            if (configs.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 使用真实的SystemConfigService批量更新配置
            int updatedCount = 0;
            for (SystemConfig config : configs) {
                try {
                    systemConfigService.save(config);
                    updatedCount++;
                } catch (Exception e) {
                    log.warn("更新配置失败: configKey={}", config.getConfigKey(), e);
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("updatedCount", updatedCount);
            result.put("totalCount", configs.size());
            result.put("configs", configs);

            return success("批量更新配置成功", result);

        } catch (Exception e) {
            log.error("批量更新配置失败: ", e);
            return error("批量更新配置失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有配置分组
     */
    @GetMapping("/groups")
    @Operation(summary = "获取所有配置分组", description = "获取系统中所有的配置分组")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<List<String>>> getConfigGroups() {

        try {
            logOperation("获取所有配置分组");

            // 使用真实的SystemConfigService获取配置分组
            List<String> groups = systemConfigService.getAllConfigGroups();
            return success("获取配置分组成功", groups);

        } catch (Exception e) {
            log.error("获取配置分组失败: ", e);
            return error("获取配置分组失败: " + e.getMessage());
        }
    }

    /**
     * 刷新配置缓存
     */
    @PostMapping("/refresh-cache")
    @Operation(summary = "刷新配置缓存", description = "刷新系统配置缓存")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> refreshConfigCache() {

        try {
            logOperation("刷新配置缓存");

            // 使用真实的SystemConfigService刷新缓存
            systemConfigService.refreshCache();
            Map<String, Object> result = new HashMap<>();
            result.put("refreshTime", LocalDateTime.now());
            result.put("message", "配置缓存刷新成功");
            return success("配置缓存刷新成功", result);

        } catch (Exception e) {
            log.error("刷新配置缓存失败: ", e);
            return error("刷新配置缓存失败: " + e.getMessage());
        }
    }

    /**
     * 导出系统配置
     */
    @GetMapping("/export")
    @Operation(summary = "导出系统配置", description = "导出系统配置数据到文件")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> exportConfigs(
            @Parameter(description = "配置分组") @RequestParam(required = false) String configGroup) {

        try {
            logOperation("导出系统配置", configGroup);

            Map<String, Object> params = new HashMap<>();
            if (StringUtils.hasText(configGroup)) {
                params.put("configGroup", configGroup);
            }

            // 使用真实的SystemConfigService导出配置
            Map<String, Object> exportResult = systemConfigService.exportConfigs(configGroup);
            return success("导出系统配置成功", exportResult);

        } catch (Exception e) {
            log.error("导出系统配置失败: ", e);
            return error("导出配置失败: " + e.getMessage());
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 验证配置数据
     */
    private void validateConfigData(SystemConfig config) {
        if (!StringUtils.hasText(config.getConfigKey())) {
            throw new IllegalArgumentException("配置键不能为空");
        }
        if (!StringUtils.hasText(config.getConfigValue())) {
            throw new IllegalArgumentException("配置值不能为空");
        }
        if (!StringUtils.hasText(config.getConfigGroup())) {
            throw new IllegalArgumentException("配置分组不能为空");
        }
    }

    // ================================
    // 辅助方法
    // ================================













}
