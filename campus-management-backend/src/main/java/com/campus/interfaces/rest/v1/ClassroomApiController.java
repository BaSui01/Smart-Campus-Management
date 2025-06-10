package com.campus.interfaces.rest.v1;

import com.campus.application.service.ClassroomService;
import com.campus.domain.entity.Classroom;
import com.campus.shared.common.ApiResponse;
import com.campus.interfaces.rest.common.BaseController;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class ClassroomApiController extends BaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(ClassroomApiController.class);
    
    @Autowired
    private ClassroomService classroomService;
    
    @PostMapping
    @Operation(summary = "创建教室", description = "创建新的教室")
    public ResponseEntity<ApiResponse<Classroom>> createClassroom(@Valid @RequestBody Classroom classroom) {
        try {
            Classroom created = classroomService.createClassroom(classroom);
            return ResponseEntity.ok(ApiResponse.success("教室创建成功", created));
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
            return ResponseEntity.ok(ApiResponse.success("教室更新成功", updated));
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
            return ResponseEntity.ok(ApiResponse.success("教室删除成功"));
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
            return ResponseEntity.ok(ApiResponse.success("教室启用成功"));
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
            return ResponseEntity.ok(ApiResponse.success("教室禁用成功"));
        } catch (Exception e) {
            logger.error("禁用教室失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("禁用教室失败: " + e.getMessage()));
        }
    }
    
    // ==================== 统计端点 ====================

    /**
     * 获取教室统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取教室统计信息", description = "获取教室模块的统计数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getClassroomStats() {
        try {
            log.info("获取教室统计信息");

            Map<String, Object> stats = new HashMap<>();

            // 基础统计
            long totalClassrooms = classroomService.countTotalClassrooms();
            stats.put("totalClassrooms", totalClassrooms);

            long availableClassrooms = classroomService.countAvailableClassrooms();
            stats.put("availableClassrooms", availableClassrooms);

            long unavailableClassrooms = totalClassrooms - availableClassrooms;
            stats.put("unavailableClassrooms", unavailableClassrooms);

            // 按建筑物统计
            List<Object[]> buildingStats = classroomService.countClassroomsByBuilding();
            Map<String, Long> buildingStatsMap = new HashMap<>();
            for (Object[] row : buildingStats) {
                buildingStatsMap.put((String) row[0], ((Number) row[1]).longValue());
            }
            stats.put("buildingStats", buildingStatsMap);

            // 时间统计（简化实现）
            stats.put("todayClassrooms", 0L);
            stats.put("weekClassrooms", 0L);
            stats.put("monthClassrooms", 0L);

            // 容量统计（简化实现）
            Map<String, Object> capacityStats = new HashMap<>();
            capacityStats.put("averageCapacity", 50);
            capacityStats.put("maxCapacity", 200);
            capacityStats.put("minCapacity", 20);
            stats.put("capacityStats", capacityStats);

            // 最近活动（简化实现）
            List<Map<String, Object>> recentActivity = new ArrayList<>();
            stats.put("recentActivity", recentActivity);

            return success("获取教室统计信息成功", stats);

        } catch (Exception e) {
            log.error("获取教室统计信息失败: ", e);
            return error("获取教室统计信息失败: " + e.getMessage());
        }
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取教室统计信息（旧版）", description = "获取教室相关统计数据")
    public ResponseEntity<ApiResponse<Object>> getClassroomStatistics() {
        try {
            long totalClassrooms = classroomService.countTotalClassrooms();
            long availableClassrooms = classroomService.countAvailableClassrooms();
            List<Object[]> buildingStats = classroomService.countClassroomsByBuilding();

            Map<String, Object> statistics = new java.util.HashMap<>();
            statistics.put("total", totalClassrooms);
            statistics.put("available", availableClassrooms);
            statistics.put("byBuilding", buildingStats);

            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            logger.error("获取教室统计信息失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取教室统计信息失败: " + e.getMessage()));
        }
    }

    // ==================== 批量操作端点 ====================

    /**
     * 批量删除教室
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除教室", description = "根据ID列表批量删除教室")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchDeleteClassrooms(
            @Parameter(description = "教室ID列表") @RequestBody List<Long> ids) {

        try {
            logOperation("批量删除教室", ids.size());

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("教室ID列表不能为空");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "教室");
            }

            // 执行批量删除
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Long id : ids) {
                try {
                    classroomService.deleteClassroom(id);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("教室ID " + id + ": " + e.getMessage());
                    log.warn("删除教室{}失败: {}", id, e.getMessage());
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", ids.size());
            responseData.put("failReasons", failReasons);

            if (failCount == 0) {
                return success("批量删除教室成功", responseData);
            } else if (successCount > 0) {
                return success("批量删除教室部分成功", responseData);
            } else {
                return error("批量删除教室失败");
            }

        } catch (Exception e) {
            log.error("批量删除教室失败: ", e);
            return error("批量删除教室失败: " + e.getMessage());
        }
    }

    /**
     * 批量更新教室状态
     */
    @PutMapping("/batch/status")
    @Operation(summary = "批量更新教室状态", description = "批量更新教室的启用/禁用状态")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchUpdateClassroomStatus(
            @Parameter(description = "批量更新请求") @RequestBody Map<String, Object> request) {

        try {
            @SuppressWarnings("unchecked")
            List<Long> ids = (List<Long>) request.get("ids");
            Integer status = (Integer) request.get("status");

            logOperation("批量更新教室状态", ids.size(), "状态: " + status);

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("教室ID列表不能为空");
            }

            if (status == null || (status != 0 && status != 1)) {
                return badRequest("状态值必须为0（禁用）或1（启用）");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "教室");
            }

            // 执行批量状态更新
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Long id : ids) {
                try {
                    if (status == 1) {
                        classroomService.enableClassroom(id);
                    } else {
                        classroomService.disableClassroom(id);
                    }
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("教室ID " + id + ": " + e.getMessage());
                    log.warn("更新教室{}状态失败: {}", id, e.getMessage());
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", ids.size());
            responseData.put("status", status == 1 ? "启用" : "禁用");
            responseData.put("failReasons", failReasons);

            if (failCount == 0) {
                return success("批量更新教室状态成功", responseData);
            } else if (successCount > 0) {
                return success("批量更新教室状态部分成功", responseData);
            } else {
                return error("批量更新教室状态失败");
            }

        } catch (Exception e) {
            log.error("批量更新教室状态失败: ", e);
            return error("批量更新教室状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量导入教室
     */
    @PostMapping("/batch/import")
    @Operation(summary = "批量导入教室", description = "批量导入教室数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchImportClassrooms(
            @Parameter(description = "教室数据列表") @RequestBody List<Classroom> classrooms) {

        try {
            logOperation("批量导入教室", classrooms.size());

            // 验证参数
            if (classrooms == null || classrooms.isEmpty()) {
                return badRequest("教室数据列表不能为空");
            }

            if (classrooms.size() > 100) {
                return badRequest("单次批量导入不能超过100条记录");
            }

            // 执行批量导入
            classroomService.importClassrooms(classrooms);

            Map<String, Object> result = new HashMap<>();
            result.put("importedCount", classrooms.size());
            result.put("totalRequested", classrooms.size());

            return success("批量导入教室完成", result);

        } catch (Exception e) {
            log.error("批量导入教室失败: ", e);
            return error("批量导入教室失败: " + e.getMessage());
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
