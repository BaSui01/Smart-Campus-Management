package com.campus.interfaces.rest.v1;

import com.campus.application.service.TimeSlotService;
import com.campus.domain.entity.TimeSlot;
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
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * 时间段管理API控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@RestController
@RequestMapping("/api/time-slots")
@Tag(name = "时间段管理", description = "时间段管理相关API接口")
public class TimeSlotApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(TimeSlotApiController.class);
    
    @Autowired
    private TimeSlotService timeSlotService;
    
    // ================================
    // 基础CRUD操作
    // ================================
    
    @PostMapping
    @Operation(summary = "创建时间段", description = "创建新的时间段")
    public ResponseEntity<ApiResponse<TimeSlot>> createTimeSlot(
            @Valid @RequestBody TimeSlot timeSlot) {
        try {
            logger.info("创建时间段: slotName={}, startTime={}, endTime={}", 
                       timeSlot.getSlotName(), timeSlot.getStartTime(), timeSlot.getEndTime());
            
            TimeSlot result = timeSlotService.createTimeSlot(timeSlot);
            return ResponseEntity.ok(ApiResponse.success("时间段创建成功", result));
            
        } catch (Exception e) {
            logger.error("创建时间段失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("创建时间段失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{timeSlotId}")
    @Operation(summary = "获取时间段详情", description = "根据ID获取时间段详细信息")
    public ResponseEntity<ApiResponse<TimeSlot>> getTimeSlotById(
            @Parameter(description = "时间段ID") @PathVariable Long timeSlotId) {
        try {
            TimeSlot timeSlot = timeSlotService.findTimeSlotById(timeSlotId).orElse(null);
            if (timeSlot != null) {
                return ResponseEntity.ok(ApiResponse.success(timeSlot));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("获取时间段详情失败: timeSlotId={}", timeSlotId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取时间段详情失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{timeSlotId}")
    @Operation(summary = "更新时间段", description = "更新时间段信息")
    public ResponseEntity<ApiResponse<TimeSlot>> updateTimeSlot(
            @PathVariable Long timeSlotId,
            @Valid @RequestBody TimeSlot timeSlot) {
        try {
            logger.info("更新时间段: timeSlotId={}", timeSlotId);
            
            timeSlot.setId(timeSlotId);
            TimeSlot result = timeSlotService.updateTimeSlot(timeSlot);
            return ResponseEntity.ok(ApiResponse.success("时间段更新成功", result));
            
        } catch (Exception e) {
            logger.error("更新时间段失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("更新时间段失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{timeSlotId}")
    @Operation(summary = "删除时间段", description = "删除指定的时间段")
    public ResponseEntity<ApiResponse<Void>> deleteTimeSlot(
            @Parameter(description = "时间段ID") @PathVariable Long timeSlotId) {
        try {
            logger.info("删除时间段: timeSlotId={}", timeSlotId);
            
            timeSlotService.deleteTimeSlot(timeSlotId);
            return ResponseEntity.ok(ApiResponse.success("时间段删除成功"));
            
        } catch (Exception e) {
            logger.error("删除时间段失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("删除时间段失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 查询操作
    // ================================
    
    @GetMapping
    @Operation(summary = "分页获取时间段列表", description = "分页查询时间段信息")
    public ResponseEntity<ApiResponse<Page<TimeSlot>>> getTimeSlots(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "时间段类型") @RequestParam(required = false) String slotType,
            @Parameter(description = "学期") @RequestParam(required = false) String semester) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<TimeSlot> timeSlots = timeSlotService.findAllTimeSlots(pageable);
            
            return ResponseEntity.ok(ApiResponse.success(timeSlots));
            
        } catch (Exception e) {
            logger.error("获取时间段列表失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取时间段列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/available")
    @Operation(summary = "获取可用时间段", description = "获取所有可用的时间段")
    public ResponseEntity<ApiResponse<List<TimeSlot>>> getAvailableTimeSlots() {
        try {
            List<TimeSlot> timeSlots = timeSlotService.findAvailableTimeSlots();
            return ResponseEntity.ok(ApiResponse.success(timeSlots));
            
        } catch (Exception e) {
            logger.error("获取可用时间段失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取可用时间段失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/type/{slotType}")
    @Operation(summary = "按类型获取时间段", description = "根据时间段类型获取时间段列表")
    public ResponseEntity<ApiResponse<List<TimeSlot>>> getTimeSlotsByType(
            @Parameter(description = "时间段类型") @PathVariable String slotType) {
        try {
            List<TimeSlot> timeSlots = timeSlotService.findTimeSlotsByType(slotType);
            return ResponseEntity.ok(ApiResponse.success(timeSlots));
            
        } catch (Exception e) {
            logger.error("按类型获取时间段失败: slotType={}", slotType, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("按类型获取时间段失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/semester/{semester}")
    @Operation(summary = "按学期获取时间段", description = "根据学期获取时间段列表")
    public ResponseEntity<ApiResponse<List<TimeSlot>>> getTimeSlotsBySemester(
            @Parameter(description = "学期") @PathVariable String semester) {
        try {
            List<TimeSlot> timeSlots = timeSlotService.findTimeSlotsBySemester(semester);
            return ResponseEntity.ok(ApiResponse.success(timeSlots));
            
        } catch (Exception e) {
            logger.error("按学期获取时间段失败: semester={}", semester, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("按学期获取时间段失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/standard")
    @Operation(summary = "获取标准时间段", description = "获取标准时间段配置")
    public ResponseEntity<ApiResponse<List<TimeSlot>>> getStandardTimeSlots() {
        try {
            List<TimeSlot> timeSlots = timeSlotService.findStandardTimeSlots();
            return ResponseEntity.ok(ApiResponse.success(timeSlots));
            
        } catch (Exception e) {
            logger.error("获取标准时间段失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取标准时间段失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 时间检查
    // ================================
    
    @PostMapping("/check-conflict")
    @Operation(summary = "检查时间冲突", description = "检查时间段是否与现有时间段冲突")
    public ResponseEntity<ApiResponse<Boolean>> checkTimeSlotConflict(
            @RequestBody Map<String, Object> timeData) {
        try {
            String startTimeStr = (String) timeData.get("startTime");
            String endTimeStr = (String) timeData.get("endTime");
            Long excludeId = timeData.get("excludeId") != null ? 
                Long.valueOf(timeData.get("excludeId").toString()) : null;
            
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);
            
            boolean hasConflict = timeSlotService.checkTimeSlotConflict(startTime, endTime, excludeId);
            return ResponseEntity.ok(ApiResponse.success(hasConflict));
            
        } catch (Exception e) {
            logger.error("检查时间冲突失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("检查时间冲突失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/find-by-time")
    @Operation(summary = "根据时间查找时间段", description = "根据指定时间查找对应的时间段")
    public ResponseEntity<ApiResponse<TimeSlot>> findTimeSlotByTime(
            @Parameter(description = "时间") @RequestParam String time) {
        try {
            LocalTime localTime = LocalTime.parse(time);
            TimeSlot timeSlot = timeSlotService.findTimeSlotByTime(localTime).orElse(null);
            
            if (timeSlot != null) {
                return ResponseEntity.ok(ApiResponse.success(timeSlot));
            } else {
                return ResponseEntity.ok(ApiResponse.success("未找到对应的时间段"));
            }
            
        } catch (Exception e) {
            logger.error("根据时间查找时间段失败: time={}", time, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("根据时间查找时间段失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{timeSlotId}/next")
    @Operation(summary = "获取下一个时间段", description = "获取指定时间段的下一个时间段")
    public ResponseEntity<ApiResponse<TimeSlot>> getNextTimeSlot(
            @Parameter(description = "当前时间段ID") @PathVariable Long timeSlotId) {
        try {
            TimeSlot nextTimeSlot = timeSlotService.getNextTimeSlot(timeSlotId).orElse(null);
            return ResponseEntity.ok(ApiResponse.success(nextTimeSlot));
            
        } catch (Exception e) {
            logger.error("获取下一个时间段失败: timeSlotId={}", timeSlotId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取下一个时间段失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{timeSlotId}/previous")
    @Operation(summary = "获取上一个时间段", description = "获取指定时间段的上一个时间段")
    public ResponseEntity<ApiResponse<TimeSlot>> getPreviousTimeSlot(
            @Parameter(description = "当前时间段ID") @PathVariable Long timeSlotId) {
        try {
            TimeSlot previousTimeSlot = timeSlotService.getPreviousTimeSlot(timeSlotId).orElse(null);
            return ResponseEntity.ok(ApiResponse.success(previousTimeSlot));
            
        } catch (Exception e) {
            logger.error("获取上一个时间段失败: timeSlotId={}", timeSlotId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取上一个时间段失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 批量操作
    // ================================
    
    @PostMapping("/batch")
    @Operation(summary = "批量创建时间段", description = "批量创建时间段")
    public ResponseEntity<ApiResponse<List<TimeSlot>>> batchCreateTimeSlots(
            @RequestBody List<TimeSlot> timeSlots) {
        try {
            logger.info("批量创建时间段: {} 个", timeSlots.size());
            
            List<TimeSlot> result = timeSlotService.batchCreateTimeSlots(timeSlots);
            return ResponseEntity.ok(ApiResponse.success("批量创建时间段成功", result));
            
        } catch (Exception e) {
            logger.error("批量创建时间段失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("批量创建时间段失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除时间段", description = "批量删除时间段")
    public ResponseEntity<ApiResponse<Void>> batchDeleteTimeSlots(
            @RequestBody List<Long> timeSlotIds) {
        try {
            logger.info("批量删除时间段: {} 个", timeSlotIds.size());
            
            timeSlotService.batchDeleteTimeSlots(timeSlotIds);
            return ResponseEntity.ok(ApiResponse.success("批量删除时间段成功"));
            
        } catch (Exception e) {
            logger.error("批量删除时间段失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("批量删除时间段失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 管理操作
    // ================================
    
    @PostMapping("/{timeSlotId}/enable")
    @Operation(summary = "启用时间段", description = "启用指定的时间段")
    public ResponseEntity<ApiResponse<Void>> enableTimeSlot(
            @Parameter(description = "时间段ID") @PathVariable Long timeSlotId) {
        try {
            timeSlotService.enableTimeSlot(timeSlotId);
            return ResponseEntity.ok(ApiResponse.success("时间段启用成功"));
            
        } catch (Exception e) {
            logger.error("启用时间段失败: timeSlotId={}", timeSlotId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("启用时间段失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{timeSlotId}/disable")
    @Operation(summary = "禁用时间段", description = "禁用指定的时间段")
    public ResponseEntity<ApiResponse<Void>> disableTimeSlot(
            @Parameter(description = "时间段ID") @PathVariable Long timeSlotId) {
        try {
            timeSlotService.disableTimeSlot(timeSlotId);
            return ResponseEntity.ok(ApiResponse.success("时间段禁用成功"));
            
        } catch (Exception e) {
            logger.error("禁用时间段失败: timeSlotId={}", timeSlotId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("禁用时间段失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/initialize-standard")
    @Operation(summary = "初始化标准时间段", description = "初始化系统标准时间段配置")
    public ResponseEntity<ApiResponse<Void>> initializeStandardTimeSlots() {
        try {
            timeSlotService.initializeStandardTimeSlots();
            return ResponseEntity.ok(ApiResponse.success("标准时间段初始化成功"));
            
        } catch (Exception e) {
            logger.error("初始化标准时间段失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("初始化标准时间段失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 配置管理
    // ================================
    
    @PostMapping("/copy-semester")
    @Operation(summary = "复制学期时间段", description = "将时间段配置复制到新学期")
    public ResponseEntity<ApiResponse<List<TimeSlot>>> copyTimeSlotsToSemester(
            @RequestBody Map<String, String> semesterData) {
        try {
            String sourceSemester = semesterData.get("sourceSemester");
            String targetSemester = semesterData.get("targetSemester");
            
            logger.info("复制时间段配置: {} -> {}", sourceSemester, targetSemester);
            
            List<TimeSlot> result = timeSlotService.copyTimeSlotsToSemester(sourceSemester, targetSemester);
            return ResponseEntity.ok(ApiResponse.success("时间段配置复制成功", result));
            
        } catch (Exception e) {
            logger.error("复制时间段配置失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("复制时间段配置失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/validate-configuration")
    @Operation(summary = "验证时间段配置", description = "验证当前时间段配置的有效性")
    public ResponseEntity<ApiResponse<Boolean>> validateTimeSlotConfiguration() {
        try {
            boolean isValid = timeSlotService.validateTimeSlotConfiguration();
            return ResponseEntity.ok(ApiResponse.success(isValid));
            
        } catch (Exception e) {
            logger.error("验证时间段配置失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("验证时间段配置失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/cleanup-invalid")
    @Operation(summary = "清理无效时间段", description = "清理系统中的无效时间段")
    public ResponseEntity<ApiResponse<Void>> cleanupInvalidTimeSlots() {
        try {
            timeSlotService.cleanupInvalidTimeSlots();
            return ResponseEntity.ok(ApiResponse.success("无效时间段清理完成"));
            
        } catch (Exception e) {
            logger.error("清理无效时间段失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("清理无效时间段失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 统计分析
    // ================================
    
    @GetMapping("/statistics")
    @Operation(summary = "获取时间段统计", description = "获取时间段统计信息")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTimeSlotStatistics() {
        try {
            Map<String, Object> statistics = Map.of(
                "totalTimeSlots", timeSlotService.countTotalTimeSlots(),
                "availableTimeSlots", timeSlotService.countAvailableTimeSlots(),
                "typeStatistics", timeSlotService.countTimeSlotsByType(),
                "usageStatistics", timeSlotService.getTimeSlotUsageStatistics()
            );
            
            return ResponseEntity.ok(ApiResponse.success(statistics));
            
        } catch (Exception e) {
            logger.error("获取时间段统计失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取时间段统计失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 导入导出
    // ================================
    
    @GetMapping("/export")
    @Operation(summary = "导出时间段配置", description = "导出时间段配置数据")
    public ResponseEntity<ApiResponse<List<TimeSlot>>> exportTimeSlots() {
        try {
            List<TimeSlot> timeSlots = timeSlotService.exportTimeSlots();
            return ResponseEntity.ok(ApiResponse.success(timeSlots));
            
        } catch (Exception e) {
            logger.error("导出时间段配置失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("导出时间段配置失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/import")
    @Operation(summary = "导入时间段配置", description = "导入时间段配置数据")
    public ResponseEntity<ApiResponse<Void>> importTimeSlots(
            @RequestBody List<TimeSlot> timeSlots) {
        try {
            logger.info("导入时间段配置: {} 个", timeSlots.size());
            
            timeSlotService.importTimeSlots(timeSlots);
            return ResponseEntity.ok(ApiResponse.success("时间段配置导入成功"));
            
        } catch (Exception e) {
            logger.error("导入时间段配置失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("导入时间段配置失败: " + e.getMessage()));
        }
    }
}
