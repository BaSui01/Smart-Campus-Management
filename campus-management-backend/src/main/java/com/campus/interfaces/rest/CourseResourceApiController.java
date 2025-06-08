package com.campus.interfaces.rest;

import com.campus.application.service.CourseResourceService;
import com.campus.domain.entity.CourseResource;
import com.campus.shared.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 课程资源API控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@RestController
@RequestMapping("/api/course-resources")
@Tag(name = "课程资源管理", description = "课程资源相关API接口")
public class CourseResourceApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseResourceApiController.class);
    
    @Autowired
    private CourseResourceService courseResourceService;
    
    // ================================
    // 基础CRUD操作
    // ================================
    
    @PostMapping
    @Operation(summary = "创建课程资源", description = "创建新的课程资源")
    public ResponseEntity<ApiResponse<CourseResource>> createResource(
            @Valid @RequestBody CourseResource resource) {
        try {
            logger.info("创建课程资源: courseId={}, resourceName={}", 
                       resource.getCourseId(), resource.getResourceName());
            
            CourseResource result = courseResourceService.createResource(resource);
            return ResponseEntity.ok(ApiResponse.success("课程资源创建成功", result));
            
        } catch (Exception e) {
            logger.error("创建课程资源失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("创建课程资源失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/upload")
    @Operation(summary = "上传课程资源文件", description = "上传课程资源文件")
    public ResponseEntity<ApiResponse<CourseResource>> uploadResource(
            @RequestParam("file") MultipartFile file,
            @RequestParam("courseId") Long courseId,
            @RequestParam("resourceName") String resourceName,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "resourceType", required = false) String resourceType) {
        try {
            logger.info("上传课程资源: courseId={}, fileName={}", courseId, file.getOriginalFilename());
            
            CourseResource result = courseResourceService.uploadResource(
                file, courseId, resourceName, description, resourceType);
            return ResponseEntity.ok(ApiResponse.success("资源上传成功", result));
            
        } catch (Exception e) {
            logger.error("上传课程资源失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("上传课程资源失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{resourceId}")
    @Operation(summary = "获取资源详情", description = "根据ID获取课程资源详细信息")
    public ResponseEntity<ApiResponse<CourseResource>> getResourceById(
            @Parameter(description = "资源ID") @PathVariable Long resourceId) {
        try {
            CourseResource resource = courseResourceService.getResourceById(resourceId);
            if (resource != null) {
                return ResponseEntity.ok(ApiResponse.success(resource));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("获取课程资源详情失败: resourceId={}", resourceId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取课程资源详情失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{resourceId}")
    @Operation(summary = "更新课程资源", description = "更新课程资源信息")
    public ResponseEntity<ApiResponse<CourseResource>> updateResource(
            @PathVariable Long resourceId,
            @Valid @RequestBody CourseResource resource) {
        try {
            logger.info("更新课程资源: resourceId={}", resourceId);
            
            resource.setId(resourceId);
            CourseResource result = courseResourceService.updateResource(resource);
            return ResponseEntity.ok(ApiResponse.success("课程资源更新成功", result));
            
        } catch (Exception e) {
            logger.error("更新课程资源失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("更新课程资源失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{resourceId}")
    @Operation(summary = "删除课程资源", description = "删除指定的课程资源")
    public ResponseEntity<ApiResponse<Void>> deleteResource(
            @Parameter(description = "资源ID") @PathVariable Long resourceId) {
        try {
            logger.info("删除课程资源: resourceId={}", resourceId);
            
            courseResourceService.deleteResource(resourceId);
            return ResponseEntity.ok(ApiResponse.success("课程资源删除成功"));
            
        } catch (Exception e) {
            logger.error("删除课程资源失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("删除课程资源失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 查询操作
    // ================================
    
    @GetMapping
    @Operation(summary = "分页获取资源列表", description = "分页查询课程资源信息")
    public ResponseEntity<ApiResponse<Page<CourseResource>>> getResources(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "资源类型") @RequestParam(required = false) String resourceType,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CourseResource> resources = courseResourceService.findResources(
                pageable, courseId, resourceType, keyword);
            
            return ResponseEntity.ok(ApiResponse.success(resources));
            
        } catch (Exception e) {
            logger.error("获取课程资源列表失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取课程资源列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/course/{courseId}")
    @Operation(summary = "获取课程的所有资源", description = "获取指定课程的所有资源")
    public ResponseEntity<ApiResponse<List<CourseResource>>> getResourcesByCourse(
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            @Parameter(description = "资源类型") @RequestParam(required = false) String resourceType) {
        try {
            List<CourseResource> resources = courseResourceService.getResourcesByCourse(courseId, resourceType);
            return ResponseEntity.ok(ApiResponse.success(resources));
            
        } catch (Exception e) {
            logger.error("获取课程资源失败: courseId={}", courseId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取课程资源失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/type/{resourceType}")
    @Operation(summary = "按类型获取资源", description = "根据资源类型获取资源列表")
    public ResponseEntity<ApiResponse<List<CourseResource>>> getResourcesByType(
            @Parameter(description = "资源类型") @PathVariable String resourceType,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId) {
        try {
            List<CourseResource> resources = courseResourceService.getResourcesByType(resourceType, courseId);
            return ResponseEntity.ok(ApiResponse.success(resources));
            
        } catch (Exception e) {
            logger.error("按类型获取资源失败: resourceType={}", resourceType, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("按类型获取资源失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "搜索课程资源", description = "根据关键词搜索课程资源")
    public ResponseEntity<ApiResponse<List<CourseResource>>> searchResources(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "资源类型") @RequestParam(required = false) String resourceType) {
        try {
            List<CourseResource> resources = courseResourceService.searchResources(keyword, courseId, resourceType);
            return ResponseEntity.ok(ApiResponse.success(resources));
            
        } catch (Exception e) {
            logger.error("搜索课程资源失败: keyword={}", keyword, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("搜索课程资源失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 文件操作
    // ================================
    
    @GetMapping("/{resourceId}/download")
    @Operation(summary = "下载资源文件", description = "下载课程资源文件")
    public ResponseEntity<ApiResponse<String>> downloadResource(
            @Parameter(description = "资源ID") @PathVariable Long resourceId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            logger.info("下载课程资源: resourceId={}, userId={}", resourceId, userId);
            
            String downloadUrl = courseResourceService.downloadResource(resourceId, userId);
            return ResponseEntity.ok(ApiResponse.success(downloadUrl, "获取下载链接成功"));
            
        } catch (Exception e) {
            logger.error("下载课程资源失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("下载课程资源失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{resourceId}/preview")
    @Operation(summary = "预览资源", description = "获取资源预览信息")
    public ResponseEntity<ApiResponse<Map<String, Object>>> previewResource(
            @Parameter(description = "资源ID") @PathVariable Long resourceId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            Map<String, Object> previewData = courseResourceService.previewResource(resourceId, userId);
            return ResponseEntity.ok(ApiResponse.success(previewData));
            
        } catch (Exception e) {
            logger.error("预览课程资源失败: resourceId={}", resourceId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("预览课程资源失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 权限管理
    // ================================
    
    @PostMapping("/{resourceId}/permissions")
    @Operation(summary = "设置资源权限", description = "设置课程资源的访问权限")
    public ResponseEntity<ApiResponse<Void>> setResourcePermissions(
            @PathVariable Long resourceId,
            @RequestBody Map<String, Object> permissionData) {
        try {
            logger.info("设置资源权限: resourceId={}", resourceId);
            
            courseResourceService.setResourcePermissions(resourceId, permissionData);
            return ResponseEntity.ok(ApiResponse.success("权限设置成功"));
            
        } catch (Exception e) {
            logger.error("设置资源权限失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("设置资源权限失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{resourceId}/access-check")
    @Operation(summary = "检查访问权限", description = "检查用户是否有访问资源的权限")
    public ResponseEntity<ApiResponse<Boolean>> checkResourceAccess(
            @Parameter(description = "资源ID") @PathVariable Long resourceId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        try {
            boolean hasAccess = courseResourceService.checkResourceAccess(resourceId, userId);
            return ResponseEntity.ok(ApiResponse.success(hasAccess));
            
        } catch (Exception e) {
            logger.error("检查资源访问权限失败: resourceId={}, userId={}", resourceId, userId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("检查资源访问权限失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 统计分析
    // ================================
    
    @GetMapping("/statistics/course/{courseId}")
    @Operation(summary = "获取课程资源统计", description = "获取课程资源统计信息")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getResourceStatistics(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        try {
            Map<String, Object> statistics = courseResourceService.getResourceStatistics(courseId);
            return ResponseEntity.ok(ApiResponse.success(statistics));
            
        } catch (Exception e) {
            logger.error("获取资源统计失败: courseId={}", courseId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取资源统计失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/access-logs/{resourceId}")
    @Operation(summary = "获取资源访问日志", description = "获取资源的访问日志")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getResourceAccessLogs(
            @Parameter(description = "资源ID") @PathVariable Long resourceId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size) {
        try {
            List<Map<String, Object>> accessLogs = courseResourceService.getResourceAccessLogs(resourceId, page, size);
            return ResponseEntity.ok(ApiResponse.success(accessLogs));
            
        } catch (Exception e) {
            logger.error("获取资源访问日志失败: resourceId={}", resourceId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取资源访问日志失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 批量操作
    // ================================
    
    @PostMapping("/batch-upload")
    @Operation(summary = "批量上传资源", description = "批量上传课程资源")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchUploadResources(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("courseId") Long courseId,
            @RequestParam(value = "resourceType", required = false) String resourceType) {
        try {
            logger.info("批量上传课程资源: courseId={}, fileCount={}", courseId, files.length);
            
            Map<String, Object> result = courseResourceService.batchUploadResources(files, courseId, resourceType);
            return ResponseEntity.ok(ApiResponse.success("批量上传完成", result));
            
        } catch (Exception e) {
            logger.error("批量上传资源失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("批量上传资源失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除资源", description = "批量删除课程资源")
    public ResponseEntity<ApiResponse<Void>> batchDeleteResources(
            @RequestBody List<Long> resourceIds) {
        try {
            logger.info("批量删除课程资源: {} 个", resourceIds.size());
            
            courseResourceService.batchDeleteResources(resourceIds);
            return ResponseEntity.ok(ApiResponse.success("批量删除成功"));
            
        } catch (Exception e) {
            logger.error("批量删除资源失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("批量删除资源失败: " + e.getMessage()));
        }
    }
}
