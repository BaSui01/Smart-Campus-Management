package com.campus.interfaces.rest.v1;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.campus.application.service.CourseService;
import com.campus.shared.common.ApiResponse;
import com.campus.domain.entity.Course;
import com.campus.interfaces.rest.common.BaseController;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 课程管理API控制器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@RestController
@RequestMapping("/api/courses")
@Tag(name = "课程管理API", description = "课程信息管理REST API接口")
@SecurityRequirement(name = "Bearer")
public class CourseApiController extends BaseController {

    @Autowired
    private CourseService courseService;

    /**
     * 获取课程列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取课程列表", description = "分页查询课程信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ApiResponse<Map<String, Object>> getCourses(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "课程名称") @RequestParam(required = false) String courseName,
            @Parameter(description = "课程类型") @RequestParam(required = false) String courseType,
            @Parameter(description = "开课学期") @RequestParam(required = false) String semester,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (courseName != null && !courseName.isEmpty()) {
            params.put("courseName", courseName);
        }
        if (courseType != null && !courseType.isEmpty()) {
            params.put("courseType", courseType);
        }
        if (semester != null && !semester.isEmpty()) {
            params.put("semester", semester);
        }
        if (status != null) {
            params.put("status", status);
        }

        // 执行分页查询
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Course> pageResult = courseService.findCoursesByPage(pageable, params);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotalElements());
        result.put("pages", pageResult.getTotalPages());
        result.put("current", pageResult.getNumber() + 1);
        result.put("size", pageResult.getSize());
        result.put("records", pageResult.getContent());

        return ApiResponse.success("获取课程列表成功", result);
    }

    /**
     * 获取课程详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取课程详情", description = "根据ID查询课程详细信息")
    public ApiResponse<Course> getCourseById(@Parameter(description = "课程ID") @PathVariable Long id) {
        Optional<Course> course = courseService.findById(id);
        if (course.isPresent()) {
            return ApiResponse.success(course.get());
        } else {
            return ApiResponse.error(404, "课程不存在");
        }
    }

    /**
     * 根据课程代码查询课程
     */
    @GetMapping("/code/{courseCode}")
    @Operation(summary = "根据课程代码查询课程", description = "根据课程代码查询课程信息")
    public ApiResponse<Course> getCourseByCode(@Parameter(description = "课程代码") @PathVariable String courseCode) {
        Optional<Course> course = courseService.findByCourseCode(courseCode);
        if (course.isPresent()) {
            return ApiResponse.success(course.get());
        } else {
            return ApiResponse.error(404, "课程不存在");
        }
    }

    /**
     * 根据学期查询课程列表
     */
    @GetMapping("/semester/{semester}")
    @Operation(summary = "根据学期查询课程列表", description = "获取指定学期的所有课程")
    public ApiResponse<List<Course>> getCoursesBySemester(@Parameter(description = "学期") @PathVariable String semester) {
        List<Course> courses = courseService.findBySemester(semester);
        return ApiResponse.success(courses);
    }

    /**
     * 根据课程类型查询课程列表
     */
    @GetMapping("/type/{courseType}")
    @Operation(summary = "根据课程类型查询课程列表", description = "获取指定类型的所有课程")
    public ApiResponse<List<Course>> getCoursesByType(@Parameter(description = "课程类型") @PathVariable String courseType) {
        List<Course> courses = courseService.findByCourseType(courseType);
        return ApiResponse.success(courses);
    }

    /**
     * 搜索课程
     */
    @GetMapping("/search")
    @Operation(summary = "搜索课程", description = "根据关键词搜索课程")
    public ApiResponse<List<Course>> searchCourses(@Parameter(description = "关键词") @RequestParam String keyword) {
        List<Course> courses = courseService.searchCourses(keyword);
        return ApiResponse.success(courses);
    }

    /**
     * 获取课程统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取课程统计信息", description = "获取课程模块的统计数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCourseStats() {
        try {
            log.info("获取课程统计信息");

            Map<String, Object> stats = new HashMap<>();

            // 基础统计
            long totalCourses = courseService.count();
            stats.put("totalCourses", totalCourses);

            // 按类型统计
            Map<String, Long> typeStats = courseService.countCoursesByType();
            stats.put("typeStats", typeStats);

            // 按状态统计
            List<Course> activeCourses = courseService.findByStatus(1);
            stats.put("activeCourses", activeCourses.size());

            List<Course> inactiveCourses = courseService.findByStatus(0);
            stats.put("inactiveCourses", inactiveCourses.size());

            // 按学期统计（简化实现）
            Map<String, Long> semesterStats = new HashMap<>();
            semesterStats.put("2024-1", 0L);
            semesterStats.put("2024-2", 0L);
            semesterStats.put("2025-1", 0L);
            stats.put("semesterStats", semesterStats);

            // 今日、本周、本月统计（简化实现）
            stats.put("todayCourses", 0L);
            stats.put("weekCourses", 0L);
            stats.put("monthCourses", 0L);

            // 最近活动（简化实现）
            List<Map<String, Object>> recentActivity = new ArrayList<>();
            stats.put("recentActivity", recentActivity);

            return success("获取课程统计信息成功", stats);

        } catch (Exception e) {
            log.error("获取课程统计信息失败: ", e);
            return error("获取课程统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程表单数据
     */
    @GetMapping("/form-data")
    @Operation(summary = "获取课程表单数据", description = "获取创建/编辑课程表单所需的数据")
    public ApiResponse<Map<String, Object>> getCourseFormData() {
        Map<String, Object> formData = new HashMap<>();

        // 课程类型选项
        List<Map<String, String>> courseTypes = Arrays.asList(
            Map.of("value", "REQUIRED", "label", "必修课"),
            Map.of("value", "ELECTIVE", "label", "选修课"),
            Map.of("value", "PUBLIC", "label", "公共课"),
            Map.of("value", "PROFESSIONAL", "label", "专业课")
        );

        // 学期选项
        List<Map<String, String>> semesters = Arrays.asList(
            Map.of("value", "2024-1", "label", "2024年春季学期"),
            Map.of("value", "2024-2", "label", "2024年秋季学期"),
            Map.of("value", "2025-1", "label", "2025年春季学期"),
            Map.of("value", "2025-2", "label", "2025年秋季学期")
        );

        // 学年选项
        List<Map<String, Object>> academicYears = Arrays.asList(
            Map.of("value", 2024, "label", "2024学年"),
            Map.of("value", 2025, "label", "2025学年"),
            Map.of("value", 2026, "label", "2026学年")
        );

        // 状态选项
        List<Map<String, Object>> statusOptions = Arrays.asList(
            Map.of("value", 1, "label", "启用"),
            Map.of("value", 0, "label", "禁用")
        );

        formData.put("courseTypes", courseTypes);
        formData.put("semesters", semesters);
        formData.put("academicYears", academicYears);
        formData.put("statusOptions", statusOptions);

        return ApiResponse.success("获取课程表单数据成功", formData);
    }

    /**
     * 创建课程
     */
    @PostMapping
    @Operation(summary = "创建课程", description = "添加新课程信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN')")
    public ApiResponse<Course> createCourse(@Parameter(description = "课程信息") @Valid @RequestBody Course course) {
        try {
            Course createdCourse = courseService.createCourse(course);
            return ApiResponse.success("创建课程成功", createdCourse);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "创建课程失败：" + e.getMessage());
        }
    }

    /**
     * 更新课程信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新课程信息", description = "修改课程信息")
    public ApiResponse<Void> updateCourse(
            @Parameter(description = "课程ID") @PathVariable Long id,
            @Parameter(description = "课程信息") @Valid @RequestBody Course course) {
        try {
            course.setId(id);
            boolean result = courseService.updateCourse(course);
            if (result) {
                return ApiResponse.success("更新课程信息成功");
            } else {
                return ApiResponse.error(404, "课程不存在");
            }
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "更新课程信息失败：" + e.getMessage());
        }
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程", description = "删除指定课程")
    public ApiResponse<Void> deleteCourse(@Parameter(description = "课程ID") @PathVariable Long id) {
        boolean result = courseService.deleteCourse(id);
        if (result) {
            return ApiResponse.success("删除课程成功");
        } else {
            return ApiResponse.error(404, "课程不存在");
        }
    }

    /**
     * 批量删除课程
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除课程", description = "批量删除多个课程")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchDeleteCourses(
            @Parameter(description = "课程ID列表") @RequestBody List<Long> ids) {

        try {
            logOperation("批量删除课程", ids.size());

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("课程ID列表不能为空");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "课程");
            }

            // 执行批量删除
            boolean result = courseService.batchDeleteCourses(ids);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("deletedCount", result ? ids.size() : 0);
            responseData.put("totalRequested", ids.size());
            responseData.put("success", result);

            if (result) {
                return success("批量删除课程成功", responseData);
            } else {
                return error("批量删除课程失败");
            }

        } catch (Exception e) {
            log.error("批量删除课程失败: ", e);
            return error("批量删除课程失败: " + e.getMessage());
        }
    }

    /**
     * 批量导入课程
     */
    @PostMapping("/batch/import")
    @Operation(summary = "批量导入课程", description = "批量导入课程数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchImportCourses(
            @Parameter(description = "课程数据列表") @RequestBody List<Course> courses) {

        try {
            logOperation("批量导入课程", courses.size());

            // 验证参数
            if (courses == null || courses.isEmpty()) {
                return badRequest("课程数据列表不能为空");
            }

            if (courses.size() > 100) {
                return badRequest("单次批量导入不能超过100条记录");
            }

            // 执行批量导入
            Map<String, Object> result = courseService.importCourses(courses);

            return success("批量导入课程完成", result);

        } catch (Exception e) {
            log.error("批量导入课程失败: ", e);
            return error("批量导入课程失败: " + e.getMessage());
        }
    }

    /**
     * 批量更新课程状态
     */
    @PutMapping("/batch/status")
    @Operation(summary = "批量更新课程状态", description = "批量更新课程的启用/禁用状态")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchUpdateCourseStatus(
            @Parameter(description = "课程ID列表") @RequestBody Map<String, Object> request) {

        try {
            @SuppressWarnings("unchecked")
            List<Long> ids = (List<Long>) request.get("ids");
            Integer status = (Integer) request.get("status");

            logOperation("批量更新课程状态", ids.size(), "状态: " + status);

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("课程ID列表不能为空");
            }

            if (status == null || (status != 0 && status != 1)) {
                return badRequest("状态值必须为0（禁用）或1（启用）");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "课程");
            }

            // 执行批量状态更新
            int successCount = 0;
            int failCount = 0;

            for (Long id : ids) {
                try {
                    boolean result = status == 1 ?
                        courseService.enableCourse(id) :
                        courseService.disableCourse(id);
                    if (result) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    failCount++;
                    log.warn("更新课程{}状态失败: {}", id, e.getMessage());
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", ids.size());
            responseData.put("status", status == 1 ? "启用" : "禁用");

            if (failCount == 0) {
                return success("批量更新课程状态成功", responseData);
            } else if (successCount > 0) {
                return success("批量更新课程状态部分成功", responseData);
            } else {
                return error("批量更新课程状态失败");
            }

        } catch (Exception e) {
            log.error("批量更新课程状态失败: ", e);
            return error("批量更新课程状态失败: " + e.getMessage());
        }
    }

    /**
     * 导出课程数据
     */
    @GetMapping("/export")
    @Operation(summary = "导出课程数据", description = "导出课程数据")
    public ApiResponse<List<Course>> exportCourses(
            @Parameter(description = "课程类型") @RequestParam(required = false) String courseType,
            @Parameter(description = "学期") @RequestParam(required = false) String semester,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (courseType != null && !courseType.isEmpty()) {
            params.put("courseType", courseType);
        }
        if (semester != null && !semester.isEmpty()) {
            params.put("semester", semester);
        }
        if (status != null) {
            params.put("status", status);
        }

        List<Course> courses = courseService.exportCourses(params);
        return ApiResponse.success("导出课程数据成功", courses);
    }
}
