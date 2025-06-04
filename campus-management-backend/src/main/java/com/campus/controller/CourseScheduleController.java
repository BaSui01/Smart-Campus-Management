package com.campus.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campus.common.ApiResponse;
import com.campus.entity.CourseSchedule;
import com.campus.repository.CourseScheduleRepository.ScheduleDetail;
import com.campus.service.CourseScheduleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 课程表管理控制器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@RestController
@RequestMapping("/course-schedules")
@Tag(name = "课程表管理", description = "课程表信息管理相关接口")
@SecurityRequirement(name = "Bearer")
public class CourseScheduleController {

    @Autowired
    private CourseScheduleService courseScheduleService;

    /**
     * 获取课程表列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取课程表列表", description = "分页查询课程表信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<Map<String, Object>> getSchedules(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "教师ID") @RequestParam(required = false) Long teacherId,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "学期") @RequestParam(required = false) String semester,
            @Parameter(description = "教室") @RequestParam(required = false) String classroom,
            @Parameter(description = "星期几") @RequestParam(required = false) Integer dayOfWeek,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (courseId != null) {
            params.put("courseId", courseId);
        }
        if (teacherId != null) {
            params.put("teacherId", teacherId);
        }
        if (classId != null) {
            params.put("classId", classId);
        }
        if (semester != null && !semester.isEmpty()) {
            params.put("semester", semester);
        }
        if (classroom != null && !classroom.isEmpty()) {
            params.put("classroom", classroom);
        }
        if (dayOfWeek != null) {
            params.put("dayOfWeek", dayOfWeek);
        }
        if (status != null) {
            params.put("status", status);
        }

        // 执行分页查询
        IPage<CourseSchedule> pageResult = courseScheduleService.findSchedulesByPage(page, size, params);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        result.put("current", pageResult.getCurrent());
        result.put("size", pageResult.getSize());
        result.put("records", pageResult.getRecords());

        return ApiResponse.success("获取课程表列表成功", result);
    }

    /**
     * 获取课程表详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取课程表详情", description = "根据ID查询课程表详细信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<ScheduleDetail> getScheduleById(@Parameter(description = "课程表ID") @PathVariable Long id) {
        Optional<ScheduleDetail> scheduleDetail = courseScheduleService.findScheduleDetailById(id);
        if (scheduleDetail.isPresent()) {
            return ApiResponse.success(scheduleDetail.get());
        } else {
            return ApiResponse.error(404, "课程表不存在");
        }
    }

    /**
     * 根据课程ID查询课程表列表
     */
    @GetMapping("/course/{courseId}")
    @Operation(summary = "根据课程ID查询课程表列表", description = "获取指定课程的所有课程表")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<List<CourseSchedule>> getSchedulesByCourseId(@Parameter(description = "课程ID") @PathVariable Long courseId) {
        List<CourseSchedule> schedules = courseScheduleService.findByCourseId(courseId);
        return ApiResponse.success(schedules);
    }

    /**
     * 根据教师ID查询课程表列表
     */
    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "根据教师ID查询课程表列表", description = "获取指定教师的所有课程表")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<CourseSchedule>> getSchedulesByTeacherId(@Parameter(description = "教师ID") @PathVariable Long teacherId) {
        List<CourseSchedule> schedules = courseScheduleService.findByTeacherId(teacherId);
        return ApiResponse.success(schedules);
    }

    /**
     * 根据班级ID查询课程表列表
     */
    @GetMapping("/class/{classId}")
    @Operation(summary = "根据班级ID查询课程表列表", description = "获取指定班级的所有课程表")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<List<CourseSchedule>> getSchedulesByClassId(@Parameter(description = "班级ID") @PathVariable Long classId) {
        List<CourseSchedule> schedules = courseScheduleService.findByClassId(classId);
        return ApiResponse.success(schedules);
    }

    /**
     * 根据学期查询课程表列表
     */
    @GetMapping("/semester/{semester}")
    @Operation(summary = "根据学期查询课程表列表", description = "获取指定学期的所有课程表")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<List<CourseSchedule>> getSchedulesBySemester(@Parameter(description = "学期") @PathVariable String semester) {
        List<CourseSchedule> schedules = courseScheduleService.findBySemester(semester);
        return ApiResponse.success(schedules);
    }

    /**
     * 根据教室查询课程表列表
     */
    @GetMapping("/classroom/{classroom}")
    @Operation(summary = "根据教室查询课程表列表", description = "获取指定教室的所有课程表")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<List<CourseSchedule>> getSchedulesByClassroom(@Parameter(description = "教室") @PathVariable String classroom) {
        List<CourseSchedule> schedules = courseScheduleService.findByClassroom(classroom);
        return ApiResponse.success(schedules);
    }

    /**
     * 根据课程ID和学期查询课程表列表
     */
    @GetMapping("/course/{courseId}/semester/{semester}")
    @Operation(summary = "根据课程ID和学期查询课程表列表", description = "获取指定课程在指定学期的所有课程表")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<List<CourseSchedule>> getSchedulesByCourseIdAndSemester(
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            @Parameter(description = "学期") @PathVariable String semester) {
        List<CourseSchedule> schedules = courseScheduleService.findByCourseIdAndSemester(courseId, semester);
        return ApiResponse.success(schedules);
    }

    /**
     * 根据教师ID和学期查询课程表列表
     */
    @GetMapping("/teacher/{teacherId}/semester/{semester}")
    @Operation(summary = "根据教师ID和学期查询课程表列表", description = "获取指定教师在指定学期的所有课程表")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<CourseSchedule>> getSchedulesByTeacherIdAndSemester(
            @Parameter(description = "教师ID") @PathVariable Long teacherId,
            @Parameter(description = "学期") @PathVariable String semester) {
        List<CourseSchedule> schedules = courseScheduleService.findByTeacherIdAndSemester(teacherId, semester);
        return ApiResponse.success(schedules);
    }

    /**
     * 根据班级ID和学期查询课程表列表
     */
    @GetMapping("/class/{classId}/semester/{semester}")
    @Operation(summary = "根据班级ID和学期查询课程表列表", description = "获取指定班级在指定学期的所有课程表")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<List<CourseSchedule>> getSchedulesByClassIdAndSemester(
            @Parameter(description = "班级ID") @PathVariable Long classId,
            @Parameter(description = "学期") @PathVariable String semester) {
        List<CourseSchedule> schedules = courseScheduleService.findByClassIdAndSemester(classId, semester);
        return ApiResponse.success(schedules);
    }

    /**
     * 检查教室在指定时间是否被占用
     */
    @GetMapping("/check-classroom")
    @Operation(summary = "检查教室在指定时间是否被占用", description = "检查指定教室在指定时间是否被占用")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<Boolean> checkClassroomOccupied(
            @Parameter(description = "教室") @RequestParam String classroom,
            @Parameter(description = "星期几") @RequestParam Integer dayOfWeek,
            @Parameter(description = "开始时间") @RequestParam String startTime,
            @Parameter(description = "结束时间") @RequestParam String endTime,
            @Parameter(description = "学期") @RequestParam String semester,
            @Parameter(description = "排除的课程表ID") @RequestParam(required = false) Long excludeId) {
        boolean isOccupied = courseScheduleService.isClassroomOccupied(classroom, dayOfWeek, startTime, endTime, semester, excludeId);
        return ApiResponse.success(isOccupied);
    }

    /**
     * 检查教师在指定时间是否有其他课程
     */
    @GetMapping("/check-teacher")
    @Operation(summary = "检查教师在指定时间是否有其他课程", description = "检查指定教师在指定时间是否有其他课程")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<Boolean> checkTeacherOccupied(
            @Parameter(description = "教师ID") @RequestParam Long teacherId,
            @Parameter(description = "星期几") @RequestParam Integer dayOfWeek,
            @Parameter(description = "开始时间") @RequestParam String startTime,
            @Parameter(description = "结束时间") @RequestParam String endTime,
            @Parameter(description = "学期") @RequestParam String semester,
            @Parameter(description = "排除的课程表ID") @RequestParam(required = false) Long excludeId) {
        boolean isOccupied = courseScheduleService.isTeacherOccupied(teacherId, dayOfWeek, startTime, endTime, semester, excludeId);
        return ApiResponse.success(isOccupied);
    }

    /**
     * 创建课程表
     */
    @PostMapping
    @Operation(summary = "创建课程表", description = "添加新课程表信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CourseSchedule> createSchedule(@Parameter(description = "课程表信息") @Valid @RequestBody CourseSchedule schedule) {
        try {
            CourseSchedule createdSchedule = courseScheduleService.createSchedule(schedule);
            return ApiResponse.success("创建课程表成功", createdSchedule);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "创建课程表失败：" + e.getMessage());
        }
    }

    /**
     * 更新课程表信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新课程表信息", description = "修改课程表信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> updateSchedule(
            @Parameter(description = "课程表ID") @PathVariable Long id,
            @Parameter(description = "课程表信息") @Valid @RequestBody CourseSchedule schedule) {
        try {
            schedule.setId(id);
            boolean result = courseScheduleService.updateSchedule(schedule);
            if (result) {
                return ApiResponse.success("更新课程表信息成功");
            } else {
                return ApiResponse.error(404, "课程表不存在");
            }
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "更新课程表信息失败：" + e.getMessage());
        }
    }

    /**
     * 删除课程表
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程表", description = "删除指定课程表")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteSchedule(@Parameter(description = "课程表ID") @PathVariable Long id) {
        try {
            boolean result = courseScheduleService.deleteSchedule(id);
            if (result) {
                return ApiResponse.success("删除课程表成功");
            } else {
                return ApiResponse.error(404, "课程表不存在");
            }
        } catch (IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "删除课程表失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除课程表
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除课程表", description = "批量删除多个课程表")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> batchDeleteSchedules(@Parameter(description = "课程表ID列表") @RequestBody List<Long> ids) {
        try {
            boolean result = courseScheduleService.batchDeleteSchedules(ids);
            if (result) {
                return ApiResponse.success("批量删除课程表成功");
            } else {
                return ApiResponse.error(500, "批量删除课程表失败");
            }
        } catch (IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "批量删除课程表失败：" + e.getMessage());
        }
    }
}
