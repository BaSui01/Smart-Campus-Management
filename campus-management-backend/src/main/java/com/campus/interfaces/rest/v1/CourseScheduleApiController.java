package com.campus.interfaces.rest.v1;

import com.campus.application.service.CourseScheduleService;
import com.campus.domain.entity.CourseSchedule;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 课程安排API控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@RestController
@RequestMapping("/api/v1/course-schedules")
@Tag(name = "课程安排管理", description = "课程安排管理相关API")
public class CourseScheduleApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseScheduleApiController.class);
    
    @Autowired
    private CourseScheduleService courseScheduleService;
    
    @PostMapping
    @Operation(summary = "创建课程安排", description = "创建新的课程安排")
    public ResponseEntity<ApiResponse<CourseSchedule>> createCourseSchedule(@Valid @RequestBody CourseSchedule courseSchedule) {
        try {
            CourseSchedule created = courseScheduleService.createCourseSchedule(courseSchedule);
            return ResponseEntity.ok(ApiResponse.success("课程安排创建成功", created));
        } catch (Exception e) {
            logger.error("创建课程安排失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("创建课程安排失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取课程安排详情", description = "根据ID获取课程安排详细信息")
    public ResponseEntity<ApiResponse<CourseSchedule>> getCourseSchedule(
            @Parameter(description = "课程安排ID") @PathVariable Long id) {
        try {
            Optional<CourseSchedule> courseSchedule = courseScheduleService.findCourseScheduleById(id);
            if (courseSchedule.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(courseSchedule.get()));
            } else {
                return ResponseEntity.ok(ApiResponse.error("课程安排不存在"));
            }
        } catch (Exception e) {
            logger.error("获取课程安排详情失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取课程安排详情失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新课程安排", description = "更新课程安排信息")
    public ResponseEntity<ApiResponse<CourseSchedule>> updateCourseSchedule(
            @Parameter(description = "课程安排ID") @PathVariable Long id, 
            @Valid @RequestBody CourseSchedule courseSchedule) {
        try {
            courseSchedule.setId(id);
            CourseSchedule updated = courseScheduleService.updateCourseSchedule(courseSchedule);
            return ResponseEntity.ok(ApiResponse.success("课程安排更新成功", updated));
        } catch (Exception e) {
            logger.error("更新课程安排失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("更新课程安排失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程安排", description = "删除指定课程安排")
    public ResponseEntity<ApiResponse<Void>> deleteCourseSchedule(
            @Parameter(description = "课程安排ID") @PathVariable Long id) {
        try {
            courseScheduleService.deleteCourseSchedule(id);
            return ResponseEntity.ok(ApiResponse.success("课程安排删除成功"));
        } catch (Exception e) {
            logger.error("删除课程安排失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("删除课程安排失败: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "获取课程安排列表", description = "分页获取课程安排列表")
    public ResponseEntity<ApiResponse<Page<CourseSchedule>>> getCourseSchedules(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CourseSchedule> courseSchedules = courseScheduleService.findAllCourseSchedules(pageable);
            return ResponseEntity.ok(ApiResponse.success(courseSchedules));
        } catch (Exception e) {
            logger.error("获取课程安排列表失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取课程安排列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/course/{courseId}")
    @Operation(summary = "按课程获取安排", description = "获取指定课程的所有安排")
    public ResponseEntity<ApiResponse<List<CourseSchedule>>> getCourseSchedulesByCourse(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        try {
            List<CourseSchedule> courseSchedules = courseScheduleService.findSchedulesByCourse(courseId);
            return ResponseEntity.ok(ApiResponse.success(courseSchedules));
        } catch (Exception e) {
            logger.error("按课程获取安排失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("按课程获取安排失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "按教师获取安排", description = "获取指定教师的所有课程安排")
    public ResponseEntity<ApiResponse<List<CourseSchedule>>> getCourseSchedulesByTeacher(
            @Parameter(description = "教师ID") @PathVariable Long teacherId) {
        try {
            List<CourseSchedule> courseSchedules = courseScheduleService.findSchedulesByTeacher(teacherId);
            return ResponseEntity.ok(ApiResponse.success(courseSchedules));
        } catch (Exception e) {
            logger.error("按教师获取安排失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("按教师获取安排失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/classroom/{classroomId}")
    @Operation(summary = "按教室获取安排", description = "获取指定教室的所有课程安排")
    public ResponseEntity<ApiResponse<List<CourseSchedule>>> getCourseSchedulesByClassroom(
            @Parameter(description = "教室ID") @PathVariable Long classroomId) {
        try {
            List<CourseSchedule> courseSchedules = courseScheduleService.findSchedulesByClassroom(classroomId);
            return ResponseEntity.ok(ApiResponse.success(courseSchedules));
        } catch (Exception e) {
            logger.error("按教室获取安排失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("按教室获取安排失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/date-range")
    @Operation(summary = "按日期范围获取安排", description = "获取指定日期范围内的课程安排")
    public ResponseEntity<ApiResponse<List<CourseSchedule>>> getCourseSchedulesByDateRange(
            @Parameter(description = "开始日期") @RequestParam LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam LocalDate endDate) {
        try {
            List<CourseSchedule> courseSchedules = courseScheduleService.findSchedulesByDateRange(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success(courseSchedules));
        } catch (Exception e) {
            logger.error("按日期范围获取安排失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("按日期范围获取安排失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/day/{dayOfWeek}")
    @Operation(summary = "按星期获取安排", description = "获取指定星期的课程安排")
    public ResponseEntity<ApiResponse<List<CourseSchedule>>> getCourseSchedulesByDay(
            @Parameter(description = "星期几") @PathVariable String dayOfWeek) {
        try {
            List<CourseSchedule> courseSchedules = courseScheduleService.findSchedulesByDayOfWeek(dayOfWeek);
            return ResponseEntity.ok(ApiResponse.success(courseSchedules));
        } catch (Exception e) {
            logger.error("按星期获取安排失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("按星期获取安排失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/conflicts")
    @Operation(summary = "检查时间冲突", description = "检查课程安排的时间冲突")
    public ResponseEntity<ApiResponse<List<CourseSchedule>>> checkScheduleConflicts(
            @Parameter(description = "课程ID") @RequestParam Long courseId,
            @Parameter(description = "教师ID") @RequestParam(required = false) Long teacherId,
            @Parameter(description = "教室ID") @RequestParam(required = false) Long classroomId,
            @Parameter(description = "星期几") @RequestParam String dayOfWeek,
            @Parameter(description = "时间段") @RequestParam String timeSlot) {
        try {
            List<CourseSchedule> conflicts = courseScheduleService.checkScheduleConflicts(
                courseId, teacherId, classroomId, dayOfWeek, timeSlot);
            return ResponseEntity.ok(ApiResponse.success(conflicts));
        } catch (Exception e) {
            logger.error("检查时间冲突失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("检查时间冲突失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/batch")
    @Operation(summary = "批量创建课程安排", description = "批量创建多个课程安排")
    public ResponseEntity<ApiResponse<List<CourseSchedule>>> batchCreateCourseSchedules(
            @RequestBody List<CourseSchedule> courseSchedules) {
        try {
            List<CourseSchedule> created = courseScheduleService.batchCreateSchedules(courseSchedules);
            return ResponseEntity.ok(ApiResponse.success("批量创建课程安排成功", created));
        } catch (Exception e) {
            logger.error("批量创建课程安排失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("批量创建课程安排失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/weekly/{teacherId}")
    @Operation(summary = "获取教师周课表", description = "获取指定教师的周课表")
    public ResponseEntity<ApiResponse<Object>> getTeacherWeeklySchedule(
            @Parameter(description = "教师ID") @PathVariable Long teacherId,
            @Parameter(description = "周开始日期") @RequestParam(required = false) LocalDate weekStart) {
        try {
            Object weeklySchedule = courseScheduleService.getTeacherWeeklySchedule(teacherId, weekStart);
            return ResponseEntity.ok(ApiResponse.success(weeklySchedule));
        } catch (Exception e) {
            logger.error("获取教师周课表失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取教师周课表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "获取课程安排统计", description = "获取课程安排相关统计信息")
    public ResponseEntity<ApiResponse<Object>> getCourseScheduleStatistics() {
        try {
            long totalSchedules = courseScheduleService.countTotalSchedules();
            long activeSchedules = courseScheduleService.countActiveSchedules();
            Map<String, Long> dayStats = courseScheduleService.countSchedulesByDay();
            Map<String, Long> timeSlotStats = courseScheduleService.countSchedulesByTimeSlot();

            Map<String, Object> statistics = new java.util.HashMap<>();
            statistics.put("total", totalSchedules);
            statistics.put("active", activeSchedules);
            statistics.put("byDay", dayStats);
            statistics.put("byTimeSlot", timeSlotStats);
            
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            logger.error("获取课程安排统计失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取课程安排统计失败: " + e.getMessage()));
        }
    }
}
