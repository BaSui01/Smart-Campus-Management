package com.campus.interfaces.rest;

import com.campus.application.service.UserService;
import com.campus.domain.entity.ParentStudentRelation;
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
import java.util.Map;

/**
 * 家长学生关系API控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@RestController
@RequestMapping("/api/parent-student-relations")
@Tag(name = "家长学生关系管理", description = "家长学生关系相关API接口")
public class ParentStudentRelationApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(ParentStudentRelationApiController.class);
    
    @Autowired
    private UserService userService;
    
    // ================================
    // 基础CRUD操作
    // ================================
    
    @PostMapping
    @Operation(summary = "创建家长学生关系", description = "建立家长与学生的关系")
    public ResponseEntity<ApiResponse<ParentStudentRelation>> createRelation(
            @Valid @RequestBody ParentStudentRelation relation) {
        try {
            logger.info("创建家长学生关系: parentId={}, studentId={}, relationType={}", 
                       relation.getParentId(), relation.getStudentId(), relation.getRelationType());
            
            ParentStudentRelation result = userService.createParentStudentRelation(relation);
            return ResponseEntity.ok(ApiResponse.success("家长学生关系创建成功", result));
            
        } catch (Exception e) {
            logger.error("创建家长学生关系失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("创建家长学生关系失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{relationId}")
    @Operation(summary = "获取关系详情", description = "根据ID获取家长学生关系详细信息")
    public ResponseEntity<ApiResponse<ParentStudentRelation>> getRelationById(
            @Parameter(description = "关系ID") @PathVariable Long relationId) {
        try {
            ParentStudentRelation relation = userService.getParentStudentRelationById(relationId);
            if (relation != null) {
                return ResponseEntity.ok(ApiResponse.success(relation));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("获取家长学生关系详情失败: relationId={}", relationId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取家长学生关系详情失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{relationId}")
    @Operation(summary = "更新家长学生关系", description = "更新家长学生关系信息")
    public ResponseEntity<ApiResponse<ParentStudentRelation>> updateRelation(
            @PathVariable Long relationId,
            @Valid @RequestBody ParentStudentRelation relation) {
        try {
            logger.info("更新家长学生关系: relationId={}", relationId);
            
            relation.setId(relationId);
            ParentStudentRelation result = userService.updateParentStudentRelation(relation);
            return ResponseEntity.ok(ApiResponse.success("家长学生关系更新成功", result));
            
        } catch (Exception e) {
            logger.error("更新家长学生关系失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("更新家长学生关系失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{relationId}")
    @Operation(summary = "删除家长学生关系", description = "删除指定的家长学生关系")
    public ResponseEntity<ApiResponse<Void>> deleteRelation(
            @Parameter(description = "关系ID") @PathVariable Long relationId) {
        try {
            logger.info("删除家长学生关系: relationId={}", relationId);
            
            userService.deleteParentStudentRelation(relationId);
            return ResponseEntity.ok(ApiResponse.success("家长学生关系删除成功"));
            
        } catch (Exception e) {
            logger.error("删除家长学生关系失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("删除家长学生关系失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 查询操作
    // ================================
    
    @GetMapping
    @Operation(summary = "分页获取关系列表", description = "分页查询家长学生关系信息")
    public ResponseEntity<ApiResponse<Page<ParentStudentRelation>>> getRelations(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "家长ID") @RequestParam(required = false) Long parentId,
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "关系类型") @RequestParam(required = false) String relationType) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ParentStudentRelation> relations = userService.findParentStudentRelations(
                pageable, parentId, studentId, relationType);
            
            return ResponseEntity.ok(ApiResponse.success(relations));
            
        } catch (Exception e) {
            logger.error("获取家长学生关系列表失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取家长学生关系列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/parent/{parentId}")
    @Operation(summary = "获取家长的所有学生", description = "获取指定家长的所有学生关系")
    public ResponseEntity<ApiResponse<List<ParentStudentRelation>>> getRelationsByParent(
            @Parameter(description = "家长ID") @PathVariable Long parentId) {
        try {
            List<ParentStudentRelation> relations = userService.getRelationsByParent(parentId);
            return ResponseEntity.ok(ApiResponse.success(relations));
            
        } catch (Exception e) {
            logger.error("获取家长学生关系失败: parentId={}", parentId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取家长学生关系失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/student/{studentId}")
    @Operation(summary = "获取学生的所有家长", description = "获取指定学生的所有家长关系")
    public ResponseEntity<ApiResponse<List<ParentStudentRelation>>> getRelationsByStudent(
            @Parameter(description = "学生ID") @PathVariable Long studentId) {
        try {
            List<ParentStudentRelation> relations = userService.getRelationsByStudent(studentId);
            return ResponseEntity.ok(ApiResponse.success(relations));
            
        } catch (Exception e) {
            logger.error("获取学生家长关系失败: studentId={}", studentId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取学生家长关系失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/type/{relationType}")
    @Operation(summary = "按关系类型查询", description = "根据关系类型查询家长学生关系")
    public ResponseEntity<ApiResponse<List<ParentStudentRelation>>> getRelationsByType(
            @Parameter(description = "关系类型") @PathVariable String relationType) {
        try {
            List<ParentStudentRelation> relations = userService.getRelationsByType(relationType);
            return ResponseEntity.ok(ApiResponse.success(relations));
            
        } catch (Exception e) {
            logger.error("按关系类型查询失败: relationType={}", relationType, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("按关系类型查询失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 验证操作
    // ================================
    
    @GetMapping("/verify")
    @Operation(summary = "验证家长学生关系", description = "验证家长与学生之间是否存在关系")
    public ResponseEntity<ApiResponse<Boolean>> verifyRelation(
            @Parameter(description = "家长ID") @RequestParam Long parentId,
            @Parameter(description = "学生ID") @RequestParam Long studentId) {
        try {
            boolean exists = userService.verifyParentStudentRelation(parentId, studentId);
            return ResponseEntity.ok(ApiResponse.success(exists));
            
        } catch (Exception e) {
            logger.error("验证家长学生关系失败: parentId={}, studentId={}", parentId, studentId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("验证家长学生关系失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/validate")
    @Operation(summary = "验证关系有效性", description = "验证家长学生关系的有效性")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateRelation(
            @RequestBody ParentStudentRelation relation) {
        try {
            Map<String, Object> validationResult = userService.validateParentStudentRelation(relation);
            return ResponseEntity.ok(ApiResponse.success(validationResult));
            
        } catch (Exception e) {
            logger.error("验证关系有效性失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("验证关系有效性失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 批量操作
    // ================================
    
    @PostMapping("/batch")
    @Operation(summary = "批量创建关系", description = "批量创建家长学生关系")
    public ResponseEntity<ApiResponse<List<ParentStudentRelation>>> batchCreateRelations(
            @RequestBody List<ParentStudentRelation> relations) {
        try {
            logger.info("批量创建家长学生关系: {} 个", relations.size());
            
            List<ParentStudentRelation> result = userService.batchCreateParentStudentRelations(relations);
            return ResponseEntity.ok(ApiResponse.success("批量创建关系成功", result));
            
        } catch (Exception e) {
            logger.error("批量创建关系失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("批量创建关系失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除关系", description = "批量删除家长学生关系")
    public ResponseEntity<ApiResponse<Void>> batchDeleteRelations(
            @RequestBody List<Long> relationIds) {
        try {
            logger.info("批量删除家长学生关系: {} 个", relationIds.size());
            
            userService.batchDeleteParentStudentRelations(relationIds);
            return ResponseEntity.ok(ApiResponse.success("批量删除关系成功"));
            
        } catch (Exception e) {
            logger.error("批量删除关系失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("批量删除关系失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 统计分析
    // ================================
    
    @GetMapping("/statistics")
    @Operation(summary = "获取关系统计", description = "获取家长学生关系统计信息")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRelationStatistics() {
        try {
            Map<String, Object> statistics = userService.getParentStudentRelationStatistics();
            return ResponseEntity.ok(ApiResponse.success(statistics));
            
        } catch (Exception e) {
            logger.error("获取关系统计失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取关系统计失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/count/type")
    @Operation(summary = "按类型统计关系数量", description = "统计各类型关系的数量")
    public ResponseEntity<ApiResponse<List<Object[]>>> getRelationCountByType() {
        try {
            List<Object[]> counts = userService.countParentStudentRelationsByType();
            return ResponseEntity.ok(ApiResponse.success(counts));
            
        } catch (Exception e) {
            logger.error("按类型统计关系数量失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("按类型统计关系数量失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 导入导出
    // ================================
    
    @PostMapping("/import")
    @Operation(summary = "导入关系数据", description = "批量导入家长学生关系数据")
    public ResponseEntity<ApiResponse<Map<String, Object>>> importRelations(
            @RequestBody List<ParentStudentRelation> relations) {
        try {
            logger.info("导入家长学生关系数据: {} 个", relations.size());
            
            Map<String, Object> result = userService.importParentStudentRelations(relations);
            return ResponseEntity.ok(ApiResponse.success("关系数据导入完成", result));
            
        } catch (Exception e) {
            logger.error("导入关系数据失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("导入关系数据失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/export")
    @Operation(summary = "导出关系数据", description = "导出家长学生关系数据")
    public ResponseEntity<ApiResponse<List<ParentStudentRelation>>> exportRelations(
            @Parameter(description = "家长ID") @RequestParam(required = false) Long parentId,
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "关系类型") @RequestParam(required = false) String relationType) {
        try {
            List<ParentStudentRelation> relations = userService.exportParentStudentRelations(
                parentId, studentId, relationType);
            return ResponseEntity.ok(ApiResponse.success(relations));
            
        } catch (Exception e) {
            logger.error("导出关系数据失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("导出关系数据失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 管理操作
    // ================================
    
    @PostMapping("/{relationId}/activate")
    @Operation(summary = "激活关系", description = "激活指定的家长学生关系")
    public ResponseEntity<ApiResponse<Void>> activateRelation(
            @Parameter(description = "关系ID") @PathVariable Long relationId) {
        try {
            userService.activateParentStudentRelation(relationId);
            return ResponseEntity.ok(ApiResponse.success("关系激活成功"));
            
        } catch (Exception e) {
            logger.error("激活关系失败: relationId={}", relationId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("激活关系失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{relationId}/deactivate")
    @Operation(summary = "停用关系", description = "停用指定的家长学生关系")
    public ResponseEntity<ApiResponse<Void>> deactivateRelation(
            @Parameter(description = "关系ID") @PathVariable Long relationId) {
        try {
            userService.deactivateParentStudentRelation(relationId);
            return ResponseEntity.ok(ApiResponse.success("关系停用成功"));
            
        } catch (Exception e) {
            logger.error("停用关系失败: relationId={}", relationId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("停用关系失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/orphaned-students")
    @Operation(summary = "获取无家长学生", description = "获取没有家长关系的学生列表")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getOrphanedStudents() {
        try {
            List<Map<String, Object>> orphanedStudents = userService.getOrphanedStudents();
            return ResponseEntity.ok(ApiResponse.success(orphanedStudents));
            
        } catch (Exception e) {
            logger.error("获取无家长学生失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取无家长学生失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/childless-parents")
    @Operation(summary = "获取无学生家长", description = "获取没有学生关系的家长列表")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getChildlessParents() {
        try {
            List<Map<String, Object>> childlessParents = userService.getChildlessParents();
            return ResponseEntity.ok(ApiResponse.success(childlessParents));
            
        } catch (Exception e) {
            logger.error("获取无学生家长失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取无学生家长失败: " + e.getMessage()));
        }
    }
}
