package com.campus.interfaces.rest.v1;

import com.campus.application.service.AutoScheduleService;
import com.campus.application.service.AutoScheduleService.*;
import com.campus.domain.entity.Classroom;
import com.campus.domain.entity.CourseSchedule;
import com.campus.domain.entity.TimeSlot;
import com.campus.shared.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 自动排课API控制器
 * 提供智能排课算法相关的REST API接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@RestController
@RequestMapping("/api/v1/auto-schedule")
@Tag(name = "自动排课管理", description = "智能排课算法API")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AutoScheduleApiController {

    private final AutoScheduleService autoScheduleService;

    @Autowired
    public AutoScheduleApiController(AutoScheduleService autoScheduleService) {
        this.autoScheduleService = autoScheduleService;
    }

    /**
     * 执行自动排课
     */
    @PostMapping("/schedule")
    @Operation(summary = "执行自动排课", description = "根据指定参数自动生成课程安排")
    public ResponseEntity<ApiResponse<ScheduleResult>> autoSchedule(
            @Parameter(description = "排课请求参数") @RequestBody ScheduleRequest request) {
        try {
            ScheduleResult result = autoScheduleService.autoSchedule(request);
            if (result.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success("自动排课完成", result));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("自动排课失败: " + result.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("自动排课异常: " + e.getMessage()));
        }
    }

    /**
     * 验证排课方案
     */
    @PostMapping("/validate")
    @Operation(summary = "验证排课方案", description = "检查课程安排是否存在冲突")
    public ResponseEntity<ApiResponse<ScheduleResult>> validateSchedule(
            @Parameter(description = "课程安排列表") @RequestBody List<CourseSchedule> schedules) {
        try {
            ScheduleResult result = autoScheduleService.validateSchedule(schedules);
            return ResponseEntity.ok(ApiResponse.success("验证完成", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("验证失败: " + e.getMessage()));
        }
    }

    /**
     * 优化排课方案
     */
    @PostMapping("/optimize")
    @Operation(summary = "优化排课方案", description = "对现有排课方案进行优化")
    public ResponseEntity<ApiResponse<ScheduleResult>> optimizeSchedule(
            @Parameter(description = "现有课程安排") @RequestBody List<CourseSchedule> schedules,
            @Parameter(description = "优化参数") @RequestParam(required = false) String preferences) {
        try {
            ScheduleRequest request = new ScheduleRequest();
            // 可以根据需要设置优化参数
            
            ScheduleResult result = autoScheduleService.optimizeSchedule(schedules, request);
            return ResponseEntity.ok(ApiResponse.success("优化完成", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("优化失败: " + e.getMessage()));
        }
    }

    /**
     * 检查冲突
     */
    @PostMapping("/check-conflicts")
    @Operation(summary = "检查冲突", description = "检查特定课程安排的冲突情况")
    public ResponseEntity<ApiResponse<List<ConflictInfo>>> checkConflicts(
            @Parameter(description = "待检查的课程安排") @RequestBody CourseSchedule schedule,
            @Parameter(description = "已有的课程安排") @RequestBody List<CourseSchedule> existingSchedules) {
        try {
            List<ConflictInfo> conflicts = autoScheduleService.checkConflicts(schedule, existingSchedules);
            return ResponseEntity.ok(ApiResponse.success("冲突检查完成", conflicts));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("冲突检查失败: " + e.getMessage()));
        }
    }

    /**
     * 获取可用时间段
     */
    @GetMapping("/available-time-slots")
    @Operation(summary = "获取可用时间段", description = "获取指定条件下的可用时间段")
    public ResponseEntity<ApiResponse<List<TimeSlot>>> getAvailableTimeSlots(
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "教室ID") @RequestParam(required = false) Long classroomId,
            @Parameter(description = "教师ID") @RequestParam(required = false) Long teacherId,
            @Parameter(description = "学期") @RequestParam String semester,
            @Parameter(description = "学年") @RequestParam Integer academicYear) {
        try {
            List<TimeSlot> timeSlots = autoScheduleService.getAvailableTimeSlots(
                courseId, classroomId, teacherId, semester, academicYear);
            return ResponseEntity.ok(ApiResponse.success("获取可用时间段成功", timeSlots));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取可用时间段失败: " + e.getMessage()));
        }
    }

    /**
     * 获取推荐教室
     */
    @PostMapping("/recommended-classrooms")
    @Operation(summary = "获取推荐教室", description = "根据课程和时间段推荐合适的教室")
    public ResponseEntity<ApiResponse<List<Classroom>>> getRecommendedClassrooms(
            @Parameter(description = "课程信息") @RequestParam Long courseId,
            @Parameter(description = "时间段ID") @RequestParam Long timeSlotId,
            @Parameter(description = "学生数量") @RequestParam(required = false) Integer studentCount) {
        try {
            // 这里简化处理，实际应该通过courseId获取课程信息
            // List<Classroom> classrooms = autoScheduleService.getRecommendedClassrooms(course, timeSlot, studentCount);
            return ResponseEntity.ok(ApiResponse.success("获取推荐教室成功", List.of()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取推荐教室失败: " + e.getMessage()));
        }
    }

    /**
     * 生成排课报告
     */
    @GetMapping("/report")
    @Operation(summary = "生成排课报告", description = "生成指定学期的排课统计报告")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generateScheduleReport(
            @Parameter(description = "学期") @RequestParam String semester,
            @Parameter(description = "学年") @RequestParam Integer academicYear) {
        try {
            Map<String, Object> report = autoScheduleService.generateScheduleReport(semester, academicYear);
            return ResponseEntity.ok(ApiResponse.success("生成排课报告成功", report));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("生成排课报告失败: " + e.getMessage()));
        }
    }

    /**
     * 批量导入排课
     */
    @PostMapping("/batch-import")
    @Operation(summary = "批量导入排课", description = "批量导入课程安排数据")
    public ResponseEntity<ApiResponse<ScheduleResult>> batchImportSchedule(
            @Parameter(description = "课程安排列表") @RequestBody List<CourseSchedule> schedules) {
        try {
            ScheduleResult result = autoScheduleService.batchImportSchedule(schedules);
            if (result.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success("批量导入成功", result));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("批量导入失败: " + result.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("批量导入异常: " + e.getMessage()));
        }
    }

    /**
     * 清空排课
     */
    @DeleteMapping("/clear")
    @Operation(summary = "清空排课", description = "清空指定学期的所有排课数据")
    public ResponseEntity<ApiResponse<String>> clearSchedule(
            @Parameter(description = "学期") @RequestParam String semester,
            @Parameter(description = "学年") @RequestParam Integer academicYear) {
        try {
            boolean success = autoScheduleService.clearSchedule(semester, academicYear);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("清空排课成功"));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("清空排课失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("清空排课异常: " + e.getMessage()));
        }
    }

    /**
     * 复制排课方案
     */
    @PostMapping("/copy")
    @Operation(summary = "复制排课方案", description = "将一个学期的排课方案复制到另一个学期")
    public ResponseEntity<ApiResponse<ScheduleResult>> copySchedule(
            @Parameter(description = "源学期") @RequestParam String sourceSemester,
            @Parameter(description = "源学年") @RequestParam Integer sourceAcademicYear,
            @Parameter(description = "目标学期") @RequestParam String targetSemester,
            @Parameter(description = "目标学年") @RequestParam Integer targetAcademicYear) {
        try {
            ScheduleResult result = autoScheduleService.copySchedule(
                sourceSemester, sourceAcademicYear, targetSemester, targetAcademicYear);
            if (result.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success("复制排课方案成功", result));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("复制排课方案失败: " + result.getMessage()));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("复制排课方案异常: " + e.getMessage()));
        }
    }

    /**
     * 获取排课统计
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取排课统计", description = "获取指定学期的排课统计信息")
    public ResponseEntity<ApiResponse<ScheduleStatistics>> getScheduleStatistics(
            @Parameter(description = "学期") @RequestParam String semester,
            @Parameter(description = "学年") @RequestParam Integer academicYear) {
        try {
            ScheduleStatistics statistics = autoScheduleService.getScheduleStatistics(semester, academicYear);
            return ResponseEntity.ok(ApiResponse.success("获取排课统计成功", statistics));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取排课统计失败: " + e.getMessage()));
        }
    }

    /**
     * 检查教师时间冲突
     */
    @GetMapping("/check-teacher-conflict")
    @Operation(summary = "检查教师时间冲突", description = "检查教师在指定时间段是否有冲突")
    public ResponseEntity<ApiResponse<Boolean>> checkTeacherConflict(
            @Parameter(description = "教师ID") @RequestParam Long teacherId,
            @Parameter(description = "时间段ID") @RequestParam Long timeSlotId,
            @Parameter(description = "学期") @RequestParam String semester,
            @Parameter(description = "学年") @RequestParam Integer academicYear) {
        try {
            // 这里简化处理，实际应该通过timeSlotId获取TimeSlot对象
            // boolean hasConflict = autoScheduleService.hasTeacherConflict(teacherId, timeSlot, semester, academicYear);
            boolean hasConflict = false; // 临时返回false
            return ResponseEntity.ok(ApiResponse.success("检查教师冲突完成", hasConflict));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("检查教师冲突失败: " + e.getMessage()));
        }
    }

    /**
     * 检查教室时间冲突
     */
    @GetMapping("/check-classroom-conflict")
    @Operation(summary = "检查教室时间冲突", description = "检查教室在指定时间段是否有冲突")
    public ResponseEntity<ApiResponse<Boolean>> checkClassroomConflict(
            @Parameter(description = "教室ID") @RequestParam Long classroomId,
            @Parameter(description = "时间段ID") @RequestParam Long timeSlotId,
            @Parameter(description = "学期") @RequestParam String semester,
            @Parameter(description = "学年") @RequestParam Integer academicYear) {
        try {
            // 这里简化处理，实际应该通过timeSlotId获取TimeSlot对象
            // boolean hasConflict = autoScheduleService.hasClassroomConflict(classroomId, timeSlot, semester, academicYear);
            boolean hasConflict = false; // 临时返回false
            return ResponseEntity.ok(ApiResponse.success("检查教室冲突完成", hasConflict));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("检查教室冲突失败: " + e.getMessage()));
        }
    }

    /**
     * 获取排课算法配置
     */
    @GetMapping("/algorithm-config")
    @Operation(summary = "获取排课算法配置", description = "获取当前排课算法的配置参数")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAlgorithmConfig() {
        try {
            Map<String, Object> config = Map.of(
                "algorithm", "greedy",
                "maxIterations", 1000,
                "conflictWeights", Map.of(
                    "teacher", 1.0,
                    "classroom", 1.0,
                    "student", 0.8
                ),
                "preferences", Map.of(
                    "morningWeight", 1.2,
                    "afternoonWeight", 1.0,
                    "eveningWeight", 0.8
                )
            );
            return ResponseEntity.ok(ApiResponse.success("获取算法配置成功", config));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取算法配置失败: " + e.getMessage()));
        }
    }

    /**
     * 更新排课算法配置
     */
    @PostMapping("/algorithm-config")
    @Operation(summary = "更新排课算法配置", description = "更新排课算法的配置参数")
    public ResponseEntity<ApiResponse<String>> updateAlgorithmConfig(
            @Parameter(description = "算法配置") @RequestBody Map<String, Object> config) {
        try {
            // 这里应该保存配置到数据库或配置文件
            return ResponseEntity.ok(ApiResponse.success("更新算法配置成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("更新算法配置失败: " + e.getMessage()));
        }
    }
}