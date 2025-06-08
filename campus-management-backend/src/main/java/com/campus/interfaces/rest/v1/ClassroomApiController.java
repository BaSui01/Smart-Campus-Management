package com.campus.interfaces.rest.v1;

import com.campus.application.service.ClassroomService;
import com.campus.domain.entity.Classroom;
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
 * 教室管理API控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@RestController
@RequestMapping("/api/v1/classrooms")
@Tag(name = "教室管理", description = "教室管理相关API")
public class ClassroomApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(ClassroomApiController.class);
    
    @Autowired
    private ClassroomService classroomService;
    
    @PostMapping
    @Operation(summary = "创建教室", description = "创建新的教室")
    public ResponseEntity<ApiResponse<Classroom>> createClassroom(@Valid @RequestBody Classroom classroom) {
        try {
            Classroom created = classroomService.createClassroom(classroom);
            return ResponseEntity.ok(ApiResponse.success(created, "教室创建成功"));
        } catch (Exception e) {
            logger.error("创建教室失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("创建教室失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取教室详情", description = "根据ID获取教室详细信息")
    public ResponseEntity<ApiResponse<Classroom>> getClassroom(
            @Parameter(description = "教室ID") @PathVariable Long id) {
        try {
            Optional<Classroom> classroom = classroomService.findClassroomById(id);
            if (classroom.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(classroom.get()));
            } else {
                return ResponseEntity.ok(ApiResponse.error("教室不存在"));
            }
        } catch (Exception e) {
            logger.error("获取教室详情失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取教室详情失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新教室", description = "更新教室信息")
    public ResponseEntity<ApiResponse<Classroom>> updateClassroom(
            @Parameter(description = "教室ID") @PathVariable Long id, 
            @Valid @RequestBody Classroom classroom) {
        try {
            classroom.setId(id);
            Classroom updated = classroomService.updateClassroom(classroom);
            return ResponseEntity.ok(ApiResponse.success(updated, "教室更新成功"));
        } catch (Exception e) {
            logger.error("更新教室失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("更新教室失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除教室", description = "删除指定教室")
    public ResponseEntity<ApiResponse<Void>> deleteClassroom(
            @Parameter(description = "教室ID") @PathVariable Long id) {
        try {
            classroomService.deleteClassroom(id);
            return ResponseEntity.ok(ApiResponse.success(null, "教室删除成功"));
        } catch (Exception e) {
            logger.error("删除教室失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("删除教室失败: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "获取教室列表", description = "分页获取教室列表")
    public ResponseEntity<ApiResponse<Page<Classroom>>> getClassrooms(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Classroom> classrooms = classroomService.findAllClassrooms(pageable);
            return ResponseEntity.ok(ApiResponse.success(classrooms));
        } catch (Exception e) {
            logger.error("获取教室列表失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取教室列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/available")
    @Operation(summary = "获取可用教室", description = "获取所有可用的教室")
    public ResponseEntity<ApiResponse<List<Classroom>>> getAvailableClassrooms() {
        try {
            List<Classroom> classrooms = classroomService.findAvailableClassrooms();
            return ResponseEntity.ok(ApiResponse.success(classrooms));
        } catch (Exception e) {
            logger.error("获取可用教室失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取可用教室失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/building/{building}")
    @Operation(summary = "按建筑物获取教室", description = "获取指定建筑物的所有教室")
    public ResponseEntity<ApiResponse<List<Classroom>>> getClassroomsByBuilding(
            @Parameter(description = "建筑物名称") @PathVariable String building) {
        try {
            List<Classroom> classrooms = classroomService.findClassroomsByBuilding(building);
            return ResponseEntity.ok(ApiResponse.success(classrooms));
        } catch (Exception e) {
            logger.error("按建筑物获取教室失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("按建筑物获取教室失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/capacity")
    @Operation(summary = "按容量范围获取教室", description = "获取指定容量范围的教室")
    public ResponseEntity<ApiResponse<List<Classroom>>> getClassroomsByCapacity(
            @Parameter(description = "最小容量") @RequestParam Integer minCapacity,
            @Parameter(description = "最大容量") @RequestParam Integer maxCapacity) {
        try {
            List<Classroom> classrooms = classroomService.findClassroomsByCapacityRange(minCapacity, maxCapacity);
            return ResponseEntity.ok(ApiResponse.success(classrooms));
        } catch (Exception e) {
            logger.error("按容量范围获取教室失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("按容量范围获取教室失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "搜索教室", description = "根据关键词搜索教室")
    public ResponseEntity<ApiResponse<Page<Classroom>>> searchClassrooms(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Classroom> classrooms = classroomService.searchClassrooms(keyword, pageable);
            return ResponseEntity.ok(ApiResponse.success(classrooms));
        } catch (Exception e) {
            logger.error("搜索教室失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("搜索教室失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/availability")
    @Operation(summary = "检查教室可用性", description = "检查教室在指定时间段是否可用")
    public ResponseEntity<ApiResponse<Boolean>> checkAvailability(
            @Parameter(description = "教室ID") @RequestParam Long classroomId,
            @Parameter(description = "时间段") @RequestParam String timeSlot,
            @Parameter(description = "星期几") @RequestParam String dayOfWeek) {
        try {
            boolean available = classroomService.isClassroomAvailable(classroomId, timeSlot, dayOfWeek);
            return ResponseEntity.ok(ApiResponse.success(available));
        } catch (Exception e) {
            logger.error("检查教室可用性失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("检查教室可用性失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/enable")
    @Operation(summary = "启用教室", description = "启用指定教室")
    public ResponseEntity<ApiResponse<Void>> enableClassroom(
            @Parameter(description = "教室ID") @PathVariable Long id) {
        try {
            classroomService.enableClassroom(id);
            return ResponseEntity.ok(ApiResponse.success(null, "教室启用成功"));
        } catch (Exception e) {
            logger.error("启用教室失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("启用教室失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/disable")
    @Operation(summary = "禁用教室", description = "禁用指定教室")
    public ResponseEntity<ApiResponse<Void>> disableClassroom(
            @Parameter(description = "教室ID") @PathVariable Long id) {
        try {
            classroomService.disableClassroom(id);
            return ResponseEntity.ok(ApiResponse.success(null, "教室禁用成功"));
        } catch (Exception e) {
            logger.error("禁用教室失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("禁用教室失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "获取教室统计信息", description = "获取教室相关统计数据")
    public ResponseEntity<ApiResponse<Object>> getClassroomStatistics() {
        try {
            long totalClassrooms = classroomService.countTotalClassrooms();
            long availableClassrooms = classroomService.countAvailableClassrooms();
            List<Object[]> buildingStats = classroomService.countClassroomsByBuilding();
            
            Object statistics = new Object() {
                public final long total = totalClassrooms;
                public final long available = availableClassrooms;
                public final List<Object[]> byBuilding = buildingStats;
            };
            
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            logger.error("获取教室统计信息失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取教室统计信息失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/import")
    @Operation(summary = "批量导入教室", description = "批量导入教室数据")
    public ResponseEntity<ApiResponse<Void>> importClassrooms(@RequestBody List<Classroom> classrooms) {
        try {
            classroomService.importClassrooms(classrooms);
            return ResponseEntity.ok(ApiResponse.success(null, "教室导入成功"));
        } catch (Exception e) {
            logger.error("批量导入教室失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("批量导入教室失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/export")
    @Operation(summary = "导出教室数据", description = "导出所有教室数据")
    public ResponseEntity<ApiResponse<List<Classroom>>> exportClassrooms() {
        try {
            List<Classroom> classrooms = classroomService.exportClassrooms();
            return ResponseEntity.ok(ApiResponse.success(classrooms));
        } catch (Exception e) {
            logger.error("导出教室数据失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("导出教室数据失败: " + e.getMessage()));
        }
    }
}
