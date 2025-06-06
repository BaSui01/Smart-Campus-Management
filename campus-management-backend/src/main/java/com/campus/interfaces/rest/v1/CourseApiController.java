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
public class CourseApiController {

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
     * 统计课程数量按类型
     */
    @GetMapping("/stats/type")
    @Operation(summary = "统计课程数量按类型", description = "按课程类型统计课程数量")
    public ApiResponse<Map<String, Long>> countCoursesByType() {
        Map<String, Long> stats = courseService.countCoursesByType();
        return ApiResponse.success(stats);
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
    public ApiResponse<Void> batchDeleteCourses(@Parameter(description = "课程ID列表") @RequestBody List<Long> ids) {
        boolean result = courseService.batchDeleteCourses(ids);
        if (result) {
            return ApiResponse.success("批量删除课程成功");
        } else {
            return ApiResponse.error(500, "批量删除课程失败");
        }
    }

    /**
     * 导入课程数据
     */
    @PostMapping("/import")
    @Operation(summary = "导入课程数据", description = "批量导入课程数据")
    public ApiResponse<Map<String, Object>> importCourses(@Parameter(description = "课程列表") @RequestBody List<Course> courses) {
        Map<String, Object> result = courseService.importCourses(courses);
        return ApiResponse.success("导入课程数据完成", result);
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
