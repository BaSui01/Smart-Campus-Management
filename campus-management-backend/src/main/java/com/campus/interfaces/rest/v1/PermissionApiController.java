package com.campus.interfaces.rest.v1;

import com.campus.application.service.PermissionService;
import com.campus.domain.entity.Permission;
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

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 权限管理API控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@RestController
@RequestMapping("/api/v1/permissions")
@Tag(name = "权限管理", description = "权限管理相关API")
public class PermissionApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(PermissionApiController.class);
    
    @Autowired
    private PermissionService permissionService;
    
    @PostMapping
    @Operation(summary = "创建权限", description = "创建新的权限")
    public ResponseEntity<ApiResponse<Permission>> createPermission(@Valid @RequestBody Permission permission) {
        try {
            Permission created = permissionService.createPermission(permission);
            return ResponseEntity.ok(ApiResponse.success("权限创建成功", created));
        } catch (Exception e) {
            logger.error("创建权限失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("创建权限失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取权限详情", description = "根据ID获取权限详细信息")
    public ResponseEntity<ApiResponse<Permission>> getPermission(
            @Parameter(description = "权限ID") @PathVariable Long id) {
        try {
            Optional<Permission> permission = permissionService.findPermissionById(id);
            if (permission.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(permission.get()));
            } else {
                return ResponseEntity.ok(ApiResponse.error("权限不存在"));
            }
        } catch (Exception e) {
            logger.error("获取权限详情失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取权限详情失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新权限", description = "更新权限信息")
    public ResponseEntity<ApiResponse<Permission>> updatePermission(
            @Parameter(description = "权限ID") @PathVariable Long id, 
            @Valid @RequestBody Permission permission) {
        try {
            permission.setId(id);
            Permission updated = permissionService.updatePermission(permission);
            return ResponseEntity.ok(ApiResponse.success("权限更新成功", updated));
        } catch (Exception e) {
            logger.error("更新权限失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("更新权限失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限", description = "删除指定权限")
    public ResponseEntity<ApiResponse<Void>> deletePermission(
            @Parameter(description = "权限ID") @PathVariable Long id) {
        try {
            permissionService.deletePermission(id);
            return ResponseEntity.ok(ApiResponse.success("权限删除成功"));
        } catch (Exception e) {
            logger.error("删除权限失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("删除权限失败: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "获取权限列表", description = "分页获取权限列表")
    public ResponseEntity<ApiResponse<Page<Permission>>> getPermissions(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Permission> permissions = permissionService.findAllPermissions(pageable);
            return ResponseEntity.ok(ApiResponse.success(permissions));
        } catch (Exception e) {
            logger.error("获取权限列表失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取权限列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/all")
    @Operation(summary = "获取所有权限", description = "获取所有权限（不分页）")
    public ResponseEntity<ApiResponse<List<Permission>>> getAllPermissions() {
        try {
            List<Permission> permissions = permissionService.findAllPermissions();
            return ResponseEntity.ok(ApiResponse.success(permissions));
        } catch (Exception e) {
            logger.error("获取所有权限失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取所有权限失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/module/{module}")
    @Operation(summary = "按模块获取权限", description = "获取指定模块的所有权限")
    public ResponseEntity<ApiResponse<List<Permission>>> getPermissionsByModule(
            @Parameter(description = "模块名称") @PathVariable String module) {
        try {
            List<Permission> permissions = permissionService.findPermissionsByModule(module);
            return ResponseEntity.ok(ApiResponse.success(permissions));
        } catch (Exception e) {
            logger.error("按模块获取权限失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("按模块获取权限失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/type/{type}")
    @Operation(summary = "按类型获取权限", description = "获取指定类型的所有权限")
    public ResponseEntity<ApiResponse<List<Permission>>> getPermissionsByType(
            @Parameter(description = "权限类型") @PathVariable String type) {
        try {
            List<Permission> permissions = permissionService.findPermissionsByType(type);
            return ResponseEntity.ok(ApiResponse.success(permissions));
        } catch (Exception e) {
            logger.error("按类型获取权限失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("按类型获取权限失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "搜索权限", description = "根据关键词搜索权限")
    public ResponseEntity<ApiResponse<Page<Permission>>> searchPermissions(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Permission> permissions = permissionService.searchPermissions(keyword, pageable);
            return ResponseEntity.ok(ApiResponse.success(permissions));
        } catch (Exception e) {
            logger.error("搜索权限失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("搜索权限失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/enable")
    @Operation(summary = "启用权限", description = "启用指定权限")
    public ResponseEntity<ApiResponse<Void>> enablePermission(
            @Parameter(description = "权限ID") @PathVariable Long id) {
        try {
            permissionService.enablePermission(id);
            return ResponseEntity.ok(ApiResponse.success("权限启用成功"));
        } catch (Exception e) {
            logger.error("启用权限失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("启用权限失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/disable")
    @Operation(summary = "禁用权限", description = "禁用指定权限")
    public ResponseEntity<ApiResponse<Void>> disablePermission(
            @Parameter(description = "权限ID") @PathVariable Long id) {
        try {
            permissionService.disablePermission(id);
            return ResponseEntity.ok(ApiResponse.success("权限禁用成功"));
        } catch (Exception e) {
            logger.error("禁用权限失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("禁用权限失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/tree")
    @Operation(summary = "获取权限树", description = "获取权限的树形结构")
    public ResponseEntity<ApiResponse<List<Object>>> getPermissionTree() {
        try {
            // getPermissionTree返回Object，需要进行类型转换
            Object treeData = permissionService.getPermissionTree();
            @SuppressWarnings("unchecked")
            List<Object> permissionTree = (List<Object>) treeData;
            return ResponseEntity.ok(ApiResponse.success(permissionTree));
        } catch (Exception e) {
            logger.error("获取权限树失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取权限树失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "获取权限统计", description = "获取权限相关统计信息")
    public ResponseEntity<ApiResponse<Object>> getPermissionStatistics() {
        try {
            long totalPermissions = permissionService.countTotalPermissions();
            long systemPermissions = permissionService.countSystemPermissions();
            long customPermissions = totalPermissions - systemPermissions;
            List<Object[]> moduleStats = permissionService.countPermissionsByModule();
            List<Object[]> typeStats = permissionService.countPermissionsByType();
            
            PermissionStatistics statistics = new PermissionStatistics(
                totalPermissions, systemPermissions, customPermissions, moduleStats, typeStats
            );
            
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            logger.error("获取权限统计失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取权限统计失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/batch")
    @Operation(summary = "批量创建权限", description = "批量创建多个权限")
    public ResponseEntity<ApiResponse<List<Permission>>> batchCreatePermissions(@RequestBody List<Permission> permissions) {
        try {
            // TODO: PermissionService中缺少batchCreatePermissions方法
            List<Permission> created = new java.util.ArrayList<>();
            for (Permission permission : permissions) {
                created.add(permissionService.createPermission(permission));
            }
            return ResponseEntity.ok(ApiResponse.success("批量创建权限成功", created));
        } catch (Exception e) {
            logger.error("批量创建权限失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("批量创建权限失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除权限", description = "批量删除多个权限")
    public ResponseEntity<ApiResponse<Void>> batchDeletePermissions(@RequestBody List<Long> permissionIds) {
        try {
            permissionService.batchDeletePermissions(permissionIds);
            return ResponseEntity.ok(ApiResponse.success("批量删除权限成功"));
        } catch (Exception e) {
            logger.error("批量删除权限失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("批量删除权限失败: " + e.getMessage()));
        }
    }

    /**
     * 权限统计数据对象
     */
    public static class PermissionStatistics {
        private final long total;
        private final long system;
        private final long custom;
        private final List<Object[]> byModule;
        private final List<Object[]> byType;

        public PermissionStatistics(long total, long system, long custom,
                                  List<Object[]> byModule, List<Object[]> byType) {
            this.total = total;
            this.system = system;
            this.custom = custom;
            this.byModule = byModule;
            this.byType = byType;
        }

        public long getTotal() { return total; }
        public long getSystem() { return system; }
        public long getCustom() { return custom; }
        public List<Object[]> getByModule() { return byModule; }
        public List<Object[]> getByType() { return byType; }
    }
}
