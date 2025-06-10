package com.campus.interfaces.rest.v1;

import com.campus.application.service.NotificationService;
import com.campus.domain.entity.NotificationTemplate;
import com.campus.shared.common.ApiResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知模板管理API控制器
 * 提供通知模板的增删改查、模板预览、模板复制等功能
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-08
 */
@RestController
@RequestMapping("/api/v1/notification-templates")
@Tag(name = "通知模板管理", description = "通知模板相关的API接口")
public class NotificationTemplateApiController extends BaseController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 分页查询通知模板列表
     */
    @GetMapping
    @Operation(summary = "分页查询通知模板列表", description = "支持按模板类型、模板状态、关键词等条件搜索")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNotificationTemplates(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "模板类型") @RequestParam(required = false) String templateType,
            @Parameter(description = "模板状态") @RequestParam(required = false) String templateStatus,
            @Parameter(description = "通知渠道") @RequestParam(required = false) String notificationChannel,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            logOperation("查询通知模板列表", page, size, templateType);

            // 验证分页参数
            validatePageParams(page, size);

            // 创建分页对象
            Pageable pageable = createPageable(page, size, sortBy, sortDir);

            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            if (StringUtils.hasText(templateType)) {
                params.put("templateType", templateType);
            }
            if (StringUtils.hasText(templateStatus)) {
                params.put("templateStatus", templateStatus);
            }
            if (StringUtils.hasText(notificationChannel)) {
                params.put("notificationChannel", notificationChannel);
            }
            if (StringUtils.hasText(keyword)) {
                params.put("keyword", keyword);
            }

            // 执行查询
            Page<NotificationTemplate> templatePage = notificationService.findTemplates(pageable, params);

            // 构建响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", templatePage.getContent());
            responseData.put("totalElements", templatePage.getTotalElements());
            responseData.put("totalPages", templatePage.getTotalPages());
            responseData.put("currentPage", page);
            responseData.put("pageSize", size);
            responseData.put("hasNext", templatePage.hasNext());
            responseData.put("hasPrevious", templatePage.hasPrevious());

            return success("查询通知模板列表成功", responseData);

        } catch (Exception e) {
            log.error("查询通知模板列表失败: ", e);
            return error("查询通知模板列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询通知模板详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询通知模板详情", description = "根据模板ID查询详细信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<NotificationTemplate>> getNotificationTemplateById(
            @Parameter(description = "模板ID") @PathVariable Long id) {

        try {
            logOperation("查询通知模板详情", id);
            validateId(id, "通知模板");

            NotificationTemplate template = notificationService.getTemplateById(id);
            if (template != null) {
                return success("查询通知模板详情成功", template);
            } else {
                return notFound("通知模板不存在");
            }

        } catch (Exception e) {
            log.error("查询通知模板详情失败: ", e);
            return error("查询通知模板详情失败: " + e.getMessage());
        }
    }

    /**
     * 根据模板类型查询模板列表
     */
    @GetMapping("/type/{templateType}")
    @Operation(summary = "根据模板类型查询模板列表", description = "查询指定类型的所有模板")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<NotificationTemplate>>> getTemplatesByType(
            @Parameter(description = "模板类型") @PathVariable String templateType) {

        try {
            logOperation("根据模板类型查询模板列表", templateType);

            List<NotificationTemplate> templates = notificationService.getTemplatesByType(templateType);
            return success("查询模板类型列表成功", templates);

        } catch (Exception e) {
            log.error("根据模板类型查询模板列表失败: ", e);
            return error("查询模板列表失败: " + e.getMessage());
        }
    }

    /**
     * 创建通知模板
     */
    @PostMapping
    @Operation(summary = "创建通知模板", description = "添加新的通知模板")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<NotificationTemplate>> createNotificationTemplate(
            @Parameter(description = "通知模板信息") @Valid @RequestBody NotificationTemplate template) {

        try {
            logOperation("创建通知模板", template.getTemplateName());

            // 验证模板数据
            validateTemplateData(template);

            // 检查模板名称是否已存在
            if (notificationService.existsByTemplateName(template.getTemplateName())) {
                return badRequest("模板名称已存在: " + template.getTemplateName());
            }

            // 设置创建时间
            template.setCreateTime(LocalDateTime.now());
            template.setUpdateTime(LocalDateTime.now());
            template.setTemplateStatus("active");

            // 保存模板
            NotificationTemplate savedTemplate = notificationService.saveTemplate(template);

            return success("通知模板创建成功", savedTemplate);

        } catch (Exception e) {
            log.error("创建通知模板失败: ", e);
            return error("创建通知模板失败: " + e.getMessage());
        }
    }

    /**
     * 更新通知模板
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新通知模板", description = "更新通知模板信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<NotificationTemplate>> updateNotificationTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id,
            @Parameter(description = "通知模板信息") @Valid @RequestBody NotificationTemplate template) {

        try {
            logOperation("更新通知模板", id);
            validateId(id, "通知模板");

            // 检查模板是否存在
            NotificationTemplate existingTemplate = notificationService.getTemplateById(id);
            if (existingTemplate == null) {
                return notFound("通知模板不存在");
            }

            // 更新模板信息
            template.setId(id);
            template.setUpdateTime(LocalDateTime.now());
            NotificationTemplate updatedTemplate = notificationService.updateTemplate(template);

            return success("通知模板更新成功", updatedTemplate);

        } catch (Exception e) {
            log.error("更新通知模板失败: ", e);
            return error("更新通知模板失败: " + e.getMessage());
        }
    }

    /**
     * 删除通知模板
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除通知模板", description = "删除指定的通知模板")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteNotificationTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id) {

        try {
            logOperation("删除通知模板", id);
            validateId(id, "通知模板");

            // 简化实现 - 直接删除
            notificationService.deleteTemplate(id);
            return success("通知模板删除成功");

        } catch (Exception e) {
            log.error("删除通知模板失败: ", e);
            return error("删除通知模板失败: " + e.getMessage());
        }
    }

    /**
     * 复制通知模板
     */
    @PostMapping("/{id}/copy")
    @Operation(summary = "复制通知模板", description = "复制现有模板创建新模板")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<NotificationTemplate>> copyNotificationTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id,
            @Parameter(description = "新模板名称") @RequestParam String newTemplateName) {

        try {
            logOperation("复制通知模板", id, newTemplateName);
            validateId(id, "通知模板");

            // 检查新模板名称是否已存在
            if (notificationService.existsByTemplateName(newTemplateName)) {
                return badRequest("模板名称已存在: " + newTemplateName);
            }

            // 复制模板
            NotificationTemplate copiedTemplate = notificationService.copyTemplate(id, newTemplateName);
            if (copiedTemplate != null) {
                return success("通知模板复制成功", copiedTemplate);
            } else {
                return notFound("原模板不存在");
            }

        } catch (Exception e) {
            log.error("复制通知模板失败: ", e);
            return error("复制通知模板失败: " + e.getMessage());
        }
    }

    /**
     * 预览通知模板
     */
    @PostMapping("/{id}/preview")
    @Operation(summary = "预览通知模板", description = "使用测试数据预览模板效果")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> previewNotificationTemplate(
            @Parameter(description = "模板ID") @PathVariable Long id,
            @Parameter(description = "预览参数") @RequestBody Map<String, Object> previewParams) {

        try {
            logOperation("预览通知模板", id);
            validateId(id, "通知模板");

            Map<String, Object> previewResult = notificationService.previewTemplate(id, previewParams);
            if (previewResult != null) {
                return success("模板预览成功", previewResult);
            } else {
                return notFound("通知模板不存在");
            }

        } catch (Exception e) {
            log.error("预览通知模板失败: ", e);
            return error("预览通知模板失败: " + e.getMessage());
        }
    }

    /**
     * 获取模板变量列表
     */
    @GetMapping("/{id}/variables")
    @Operation(summary = "获取模板变量列表", description = "获取模板中使用的所有变量")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<String>>> getTemplateVariables(
            @Parameter(description = "模板ID") @PathVariable Long id) {

        try {
            logOperation("获取模板变量列表", id);
            validateId(id, "通知模板");

            List<String> variables = notificationService.getTemplateVariables(id);
            if (variables != null) {
                return success("获取模板变量成功", variables);
            } else {
                return notFound("通知模板不存在");
            }

        } catch (Exception e) {
            log.error("获取模板变量列表失败: ", e);
            return error("获取模板变量失败: " + e.getMessage());
        }
    }

    // ==================== 批量操作端点 ====================

    /**
     * 批量删除通知模板
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除通知模板", description = "批量删除指定的通知模板")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchDeleteTemplates(
            @Parameter(description = "模板ID列表") @RequestBody List<Long> templateIds) {

        try {
            logOperation("批量删除通知模板", templateIds.size());

            // 验证参数
            if (templateIds == null || templateIds.isEmpty()) {
                return badRequest("模板ID列表不能为空");
            }

            if (templateIds.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : templateIds) {
                validateId(id, "通知模板");
            }

            // 执行批量删除
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Long id : templateIds) {
                try {
                    notificationService.deleteTemplate(id);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("模板ID " + id + ": " + e.getMessage());
                    log.warn("删除通知模板{}失败: {}", id, e.getMessage());
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", templateIds.size());
            responseData.put("failReasons", failReasons);

            if (failCount == 0) {
                return success("批量删除通知模板成功", responseData);
            } else if (successCount > 0) {
                return success("批量删除通知模板部分成功", responseData);
            } else {
                return error("批量删除通知模板失败");
            }

        } catch (Exception e) {
            log.error("批量删除通知模板失败: ", e);
            return error("批量删除通知模板失败: " + e.getMessage());
        }
    }

    /**
     * 批量更新模板状态
     */
    @PutMapping("/batch-status")
    @Operation(summary = "批量更新模板状态", description = "批量启用或禁用模板")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchUpdateTemplateStatus(
            @Parameter(description = "模板ID列表") @RequestParam List<Long> templateIds,
            @Parameter(description = "新状态") @RequestParam String newStatus) {

        try {
            logOperation("批量更新模板状态", templateIds.size(), newStatus);

            if (templateIds == null || templateIds.isEmpty()) {
                return badRequest("模板ID列表不能为空");
            }

            if (templateIds.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            if (!StringUtils.hasText(newStatus) || (!newStatus.equals("active") && !newStatus.equals("inactive"))) {
                return badRequest("状态值必须是active或inactive");
            }

            // 验证所有ID
            for (Long id : templateIds) {
                validateId(id, "通知模板");
            }

            // 批量更新状态
            int updatedCount = notificationService.batchUpdateTemplateStatus(templateIds, newStatus);

            Map<String, Object> result = new HashMap<>();
            result.put("updatedCount", updatedCount);
            result.put("totalCount", templateIds.size());
            result.put("newStatus", newStatus);

            return success("批量更新模板状态成功", result);

        } catch (Exception e) {
            log.error("批量更新模板状态失败: ", e);
            return error("批量更新模板状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取模板类型列表
     */
    @GetMapping("/template-types")
    @Operation(summary = "获取模板类型列表", description = "获取所有可用的模板类型")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<String>>> getTemplateTypes() {

        try {
            logOperation("获取模板类型列表");

            List<String> templateTypes = notificationService.getAllTemplateTypes();
            return success("获取模板类型列表成功", templateTypes);

        } catch (Exception e) {
            log.error("获取模板类型列表失败: ", e);
            return error("获取模板类型列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取通知渠道列表
     */
    @GetMapping("/notification-channels")
    @Operation(summary = "获取通知渠道列表", description = "获取所有可用的通知渠道")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<String>>> getNotificationChannels() {

        try {
            logOperation("获取通知渠道列表");

            List<String> channels = notificationService.getAllNotificationChannels();
            return success("获取通知渠道列表成功", channels);

        } catch (Exception e) {
            log.error("获取通知渠道列表失败: ", e);
            return error("获取通知渠道列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取模板统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取模板统计信息", description = "获取通知模板的统计数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTemplateStatistics() {

        try {
            logOperation("获取模板统计信息");

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalTemplates", notificationService.countTotalTemplates());
            stats.put("activeTemplates", notificationService.countActiveTemplates());
            stats.put("inactiveTemplates", notificationService.countInactiveTemplates());
            stats.put("templateTypeStats", notificationService.getTemplateTypeStatistics());
            stats.put("channelStats", notificationService.getChannelStatistics());
            stats.put("usageStats", notificationService.getTemplateUsageStatistics());

            return success("获取模板统计信息成功", stats);

        } catch (Exception e) {
            log.error("获取模板统计信息失败: ", e);
            return error("获取统计信息失败: " + e.getMessage());
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 验证模板数据
     */
    private void validateTemplateData(NotificationTemplate template) {
        if (!StringUtils.hasText(template.getTemplateName())) {
            throw new IllegalArgumentException("模板名称不能为空");
        }
        if (!StringUtils.hasText(template.getTemplateType())) {
            throw new IllegalArgumentException("模板类型不能为空");
        }
        if (!StringUtils.hasText(template.getTemplateContent())) {
            throw new IllegalArgumentException("模板内容不能为空");
        }
        if (!StringUtils.hasText(template.getNotificationChannel())) {
            throw new IllegalArgumentException("通知渠道不能为空");
        }
    }
}
