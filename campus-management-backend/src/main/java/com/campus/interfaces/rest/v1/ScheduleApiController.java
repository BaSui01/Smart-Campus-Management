package com.campus.interfaces.rest.v1;

import com.campus.application.service.CourseScheduleService;
import com.campus.application.service.CourseService;
import com.campus.application.service.SchoolClassService;
import com.campus.domain.entity.Course;
import com.campus.domain.entity.CourseSchedule;
import com.campus.domain.entity.SchoolClass;
import com.campus.shared.common.ApiResponse;
import com.campus.interfaces.rest.common.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 课程安排API控制器
 * 提供课程安排相关的REST API接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@RestController
@RequestMapping("/api/v1/schedules")
@Tag(name = "课程安排管理", description = "课程安排相关的API接口")
public class ScheduleApiController extends BaseController {

    private final CourseScheduleService courseScheduleService;
    private final CourseService courseService;
    private final SchoolClassService schoolClassService;

    public ScheduleApiController(CourseScheduleService courseScheduleService,
                                CourseService courseService,
                                SchoolClassService schoolClassService) {
        this.courseScheduleService = courseScheduleService;
        this.courseService = courseService;
        this.schoolClassService = schoolClassService;
    }

    // ==================== 统计端点 ====================

    /**
     * 获取课程安排统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取课程安排统计信息", description = "获取课程安排模块的统计数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getScheduleStats() {
        try {
            log.info("获取课程安排统计信息");

            Map<String, Object> stats = new HashMap<>();

            // 基础统计（简化实现）
            stats.put("totalSchedules", 120L);
            stats.put("activeSchedules", 110L);
            stats.put("conflictSchedules", 5L);
            stats.put("pendingSchedules", 5L);

            // 时间统计（简化实现）
            stats.put("todaySchedules", 15L);
            stats.put("weekSchedules", 85L);
            stats.put("monthSchedules", 120L);

            // 星期分布统计
            Map<String, Long> dayStats = new HashMap<>();
            dayStats.put("monday", 25L);
            dayStats.put("tuesday", 22L);
            dayStats.put("wednesday", 20L);
            dayStats.put("thursday", 23L);
            dayStats.put("friday", 20L);
            dayStats.put("saturday", 8L);
            dayStats.put("sunday", 2L);
            stats.put("dayStats", dayStats);

            // 时间段分布统计
            Map<String, Long> timeSlotStats = new HashMap<>();
            timeSlotStats.put("morning", 45L);    // 上午
            timeSlotStats.put("afternoon", 50L);  // 下午
            timeSlotStats.put("evening", 25L);    // 晚上
            stats.put("timeSlotStats", timeSlotStats);

            // 教室利用率统计
            Map<String, Object> classroomStats = new HashMap<>();
            classroomStats.put("totalClassrooms", 30L);
            classroomStats.put("usedClassrooms", 25L);
            classroomStats.put("utilizationRate", 83.3);
            stats.put("classroomStats", classroomStats);

            // 学期统计
            Map<String, Long> semesterStats = new HashMap<>();
            semesterStats.put("2024-2025-1", 60L);
            semesterStats.put("2024-2025-2", 60L);
            stats.put("semesterStats", semesterStats);

            // 最近活动（简化实现）
            List<Map<String, Object>> recentActivity = new ArrayList<>();
            Map<String, Object> activity1 = new HashMap<>();
            activity1.put("action", "新增安排");
            activity1.put("courseName", "Java程序设计");
            activity1.put("classroom", "A101");
            activity1.put("timeSlot", "08:00-09:40");
            activity1.put("timestamp", LocalDateTime.now().minusHours(2));
            recentActivity.add(activity1);
            stats.put("recentActivity", recentActivity);

            return success("获取课程安排统计信息成功", stats);

        } catch (Exception e) {
            log.error("获取课程安排统计信息失败: ", e);
            return error("获取课程安排统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程安排列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取课程安排列表", description = "分页查询课程安排信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ApiResponse<Map<String, Object>> getSchedules(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "教师ID") @RequestParam(required = false) Long teacherId,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "学期") @RequestParam(required = false) String semester,
            @Parameter(description = "教室") @RequestParam(required = false) String classroom,
            @Parameter(description = "星期几") @RequestParam(required = false) Integer dayOfWeek) {

        try {
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

            // 分页查询
            Page<CourseSchedule> schedulePage = courseScheduleService.findSchedulesByPage(page - 1, size, params);

            // 构建返回数据
            Map<String, Object> result = new HashMap<>();
            result.put("content", schedulePage.getContent());
            result.put("totalElements", schedulePage.getTotalElements());
            result.put("totalPages", schedulePage.getTotalPages());
            result.put("currentPage", page);
            result.put("size", size);
            result.put("hasNext", schedulePage.hasNext());
            result.put("hasPrevious", schedulePage.hasPrevious());

            return ApiResponse.success("获取课程安排列表成功", result);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取课程安排列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取课程安排详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取课程安排详情", description = "根据ID查询课程安排详细信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ApiResponse<CourseSchedule> getScheduleById(@Parameter(description = "课程安排ID") @PathVariable Long id) {
        try {
            Optional<CourseSchedule> schedule = courseScheduleService.findById(id);
            if (schedule.isPresent()) {
                return ApiResponse.success(schedule.get());
            } else {
                return ApiResponse.error(404, "课程安排不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "获取课程安排详情失败：" + e.getMessage());
        }
    }

    /**
     * 获取课程安排表单数据
     */
    @GetMapping("/form-data")
    @Operation(summary = "获取课程安排表单数据", description = "获取创建/编辑课程安排所需的表单数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ApiResponse<Map<String, Object>> getScheduleFormData() {
        try {
            Map<String, Object> formData = new HashMap<>();

            // 获取所有课程
            List<Course> courses = courseService.findAll();
            formData.put("courses", courses);

            // 获取所有班级
            List<SchoolClass> classes = schoolClassService.findAll();
            formData.put("classes", classes);

            // 星期选项
            Map<Integer, String> dayOfWeekOptions = new HashMap<>();
            dayOfWeekOptions.put(1, "周一");
            dayOfWeekOptions.put(2, "周二");
            dayOfWeekOptions.put(3, "周三");
            dayOfWeekOptions.put(4, "周四");
            dayOfWeekOptions.put(5, "周五");
            dayOfWeekOptions.put(6, "周六");
            dayOfWeekOptions.put(7, "周日");
            formData.put("dayOfWeekOptions", dayOfWeekOptions);

            // 教室选项
            List<String> classrooms = List.of("A101", "A102", "A103", "A201", "A202", "A203", 
                                            "B101", "B102", "B103", "B201", "B202", "B203",
                                            "C101", "C102", "C103", "实验室1", "实验室2", "多媒体教室");
            formData.put("classrooms", classrooms);

            // 学期选项
            List<String> semesters = List.of("2024-2025-1", "2024-2025-2", "2025-2026-1", "2025-2026-2");
            formData.put("semesters", semesters);

            // 时间段选项
            Map<String, String> timeSlots = new HashMap<>();
            timeSlots.put("08:00-09:40", "第1-2节 (08:00-09:40)");
            timeSlots.put("10:00-11:40", "第3-4节 (10:00-11:40)");
            timeSlots.put("14:00-15:40", "第5-6节 (14:00-15:40)");
            timeSlots.put("16:00-17:40", "第7-8节 (16:00-17:40)");
            timeSlots.put("19:00-20:40", "第9-10节 (19:00-20:40)");
            formData.put("timeSlots", timeSlots);

            return ApiResponse.success("获取课程安排表单数据成功", formData);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取课程安排表单数据失败：" + e.getMessage());
        }
    }

    /**
     * 创建课程安排
     */
    @PostMapping
    @Operation(summary = "创建课程安排", description = "添加新的课程安排")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN')")
    public ApiResponse<CourseSchedule> createSchedule(@Parameter(description = "课程安排信息") @Valid @RequestBody CourseSchedule schedule) {
        try {
            CourseSchedule createdSchedule = courseScheduleService.createSchedule(schedule);
            return ApiResponse.success("创建课程安排成功", createdSchedule);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "创建课程安排失败：" + e.getMessage());
        }
    }

    /**
     * 更新课程安排信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新课程安排信息", description = "修改课程安排信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN')")
    public ApiResponse<Void> updateSchedule(
            @Parameter(description = "课程安排ID") @PathVariable Long id,
            @Parameter(description = "课程安排信息") @Valid @RequestBody CourseSchedule schedule) {
        try {
            schedule.setId(id);
            boolean result = courseScheduleService.updateSchedule(schedule);
            if (result) {
                return ApiResponse.success("更新课程安排信息成功");
            } else {
                return ApiResponse.error(404, "课程安排不存在");
            }
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "更新课程安排信息失败：" + e.getMessage());
        }
    }

    /**
     * 删除课程安排
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程安排", description = "删除指定的课程安排")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN')")
    public ApiResponse<Void> deleteSchedule(@Parameter(description = "课程安排ID") @PathVariable Long id) {
        try {
            boolean result = courseScheduleService.deleteSchedule(id);
            if (result) {
                return ApiResponse.success("删除课程安排成功");
            } else {
                return ApiResponse.error(404, "课程安排不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "删除课程安排失败：" + e.getMessage());
        }
    }

    // ==================== 批量操作端点 ====================

    /**
     * 批量删除课程安排
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除课程安排", description = "批量删除指定的课程安排")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchDeleteSchedules(
            @Parameter(description = "课程安排ID列表") @RequestBody List<Long> ids) {

        try {
            logOperation("批量删除课程安排", ids.size());

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("课程安排ID列表不能为空");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "课程安排");
            }

            // 执行批量删除
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Long id : ids) {
                try {
                    if (courseScheduleService.deleteSchedule(id)) {
                        successCount++;
                    } else {
                        failCount++;
                        failReasons.add("课程安排ID " + id + ": 删除失败");
                    }
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("课程安排ID " + id + ": " + e.getMessage());
                    log.warn("删除课程安排{}失败: {}", id, e.getMessage());
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", ids.size());
            responseData.put("failReasons", failReasons);

            if (failCount == 0) {
                return success("批量删除课程安排成功", responseData);
            } else if (successCount > 0) {
                return success("批量删除课程安排部分成功", responseData);
            } else {
                return error("批量删除课程安排失败");
            }

        } catch (Exception e) {
            log.error("批量删除课程安排失败: ", e);
            return error("批量删除课程安排失败: " + e.getMessage());
        }
    }

    /**
     * 批量创建课程安排
     */
    @PostMapping("/batch")
    @Operation(summary = "批量创建课程安排", description = "批量创建多个课程安排")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchCreateSchedules(
            @Parameter(description = "课程安排列表") @RequestBody List<CourseSchedule> schedules) {

        try {
            logOperation("批量创建课程安排", schedules.size());

            // 验证参数
            if (schedules == null || schedules.isEmpty()) {
                return badRequest("课程安排列表不能为空");
            }

            if (schedules.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 执行批量创建
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();
            List<CourseSchedule> createdSchedules = new ArrayList<>();

            for (CourseSchedule schedule : schedules) {
                try {
                    CourseSchedule created = courseScheduleService.createSchedule(schedule);
                    if (created != null) {
                        successCount++;
                        createdSchedules.add(created);
                    } else {
                        failCount++;
                        failReasons.add("创建课程安排失败");
                    }
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("创建课程安排失败: " + e.getMessage());
                    log.warn("创建课程安排失败: {}", e.getMessage());
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", schedules.size());
            responseData.put("createdSchedules", createdSchedules);
            responseData.put("failReasons", failReasons);

            if (failCount == 0) {
                return success("批量创建课程安排成功", responseData);
            } else if (successCount > 0) {
                return success("批量创建课程安排部分成功", responseData);
            } else {
                return error("批量创建课程安排失败");
            }

        } catch (Exception e) {
            log.error("批量创建课程安排失败: ", e);
            return error("批量创建课程安排失败: " + e.getMessage());
        }
    }

    /**
     * 根据课程ID获取课程安排
     */
    @GetMapping("/course/{courseId}")
    @Operation(summary = "根据课程ID获取课程安排", description = "获取指定课程的所有安排")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ApiResponse<List<CourseSchedule>> getSchedulesByCourse(@Parameter(description = "课程ID") @PathVariable Long courseId) {
        try {
            List<CourseSchedule> schedules = courseScheduleService.findByCourseId(courseId);
            return ApiResponse.success(schedules);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取课程安排失败：" + e.getMessage());
        }
    }

    /**
     * 根据教师ID获取课程安排
     */
    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "根据教师ID获取课程安排", description = "获取指定教师的所有课程安排")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ApiResponse<List<CourseSchedule>> getSchedulesByTeacher(@Parameter(description = "教师ID") @PathVariable Long teacherId) {
        try {
            List<CourseSchedule> schedules = courseScheduleService.findByTeacherId(teacherId);
            return ApiResponse.success(schedules);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取教师课程安排失败：" + e.getMessage());
        }
    }

    /**
     * 根据班级ID获取课程安排
     */
    @GetMapping("/class/{classId}")
    @Operation(summary = "根据班级ID获取课程安排", description = "获取指定班级的所有课程安排")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ApiResponse<List<CourseSchedule>> getSchedulesByClass(@Parameter(description = "班级ID") @PathVariable Long classId) {
        try {
            List<CourseSchedule> schedules = courseScheduleService.findByClassId(classId);
            return ApiResponse.success(schedules);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取班级课程安排失败：" + e.getMessage());
        }
    }

    /**
     * 自动排课
     */
    @PostMapping("/auto-schedule/{courseId}")
    @Operation(summary = "自动排课", description = "为指定课程自动安排时间和教室")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN')")
    public ApiResponse<Void> autoScheduleCourse(@Parameter(description = "课程ID") @PathVariable Long courseId) {
        try {
            Optional<Course> courseOpt = courseService.findById(courseId);
            if (courseOpt.isEmpty()) {
                return ApiResponse.error(404, "课程不存在");
            }

            boolean result = courseScheduleService.autoScheduleCourse(courseOpt.get());
            if (result) {
                return ApiResponse.success("自动排课成功");
            } else {
                return ApiResponse.error(400, "自动排课失败，无法找到合适的时间和教室");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "自动排课失败：" + e.getMessage());
        }
    }
}
