package com.campus.controller;

import java.math.BigDecimal;
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
import com.campus.entity.Course;
import com.campus.repository.CourseRepository.CourseDetail;
import com.campus.repository.CourseRepository.CourseTypeCount;
import com.campus.service.CourseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 课程管理控制器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@RestController
@RequestMapping("/courses")
@Tag(name = "课程管理", description = "课程信息管理相关接口")
@SecurityRequirement(name = "Bearer")
public class CourseController {

    @Autowired
    private CourseService courseService;


    /**
     * 获取课程列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取课程列表", description = "分页查询课程信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<Map<String, Object>> getCourses(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "课程名称") @RequestParam(required = false) String courseName,
            @Parameter(description = "课程代码") @RequestParam(required = false) String courseCode,
            @Parameter(description = "学分") @RequestParam(required = false) Integer credits,
            @Parameter(description = "部门ID") @RequestParam(required = false) Long departmentId,
            @Parameter(description = "教师ID") @RequestParam(required = false) Long teacherId,
            @Parameter(description = "课程类型") @RequestParam(required = false) String courseType,
            @Parameter(description = "学期") @RequestParam(required = false) String semester,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (courseName != null && !courseName.isEmpty()) {
            params.put("courseName", courseName);
            }
        if (courseCode != null && !courseCode.isEmpty()) {
            params.put("courseCode", courseCode);
        }
        if (credits != null) {
            params.put("credits", credits);
        }
        if (departmentId != null) {
            params.put("departmentId", departmentId);
            }
        if (teacherId != null) {
            params.put("teacherId", teacherId);
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
        IPage<Course> pageResult = courseService.findCoursesByPage(page, size, params);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        result.put("current", pageResult.getCurrent());
        result.put("size", pageResult.getSize());
        result.put("records", pageResult.getRecords());

        return ApiResponse.success("获取课程列表成功", result);
    }

    /**
     * 获取课程详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取课程详情", description = "根据ID查询课程详细信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<CourseDetail> getCourseById(@Parameter(description = "课程ID") @PathVariable Long id) {
        Optional<CourseDetail> courseDetail = courseService.findCourseDetailById(id);
        if (courseDetail.isPresent()) {
            return ApiResponse.success(courseDetail.get());
        } else {
            return ApiResponse.error(404, "课程不存在");
        }
    }

    /**
     * 根据课程代码查询课程
     */
    @GetMapping("/code/{courseCode}")
    @Operation(summary = "根据课程代码查询课程", description = "根据课程代码查询课程信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<Course> getCourseByCode(@Parameter(description = "课程代码") @PathVariable String courseCode) {
        Optional<Course> course = courseService.findByCourseCode(courseCode);
        if (course.isPresent()) {
            return ApiResponse.success(course.get());
        } else {
            return ApiResponse.error(404, "课程不存在");
        }
    }

    /**
     * 根据课程名称查询课程
     */
    @GetMapping("/name")
    @Operation(summary = "根据课程名称查询课程", description = "根据课程名称查询课程信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<List<Course>> getCoursesByName(@Parameter(description = "课程名称") @RequestParam String courseName) {
        List<Course> courses = courseService.findByCourseName(courseName);
            return ApiResponse.success(courses);
    }

    /**
     * 根据部门ID查询课程列表
     */
    @GetMapping("/department/{departmentId}")
    @Operation(summary = "根据部门ID查询课程列表", description = "获取指定部门的所有课程")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<Course>> getCoursesByDepartmentId(@Parameter(description = "部门ID") @PathVariable Long departmentId) {
        List<Course> courses = courseService.findByDepartmentId(departmentId);
        return ApiResponse.success(courses);
    }

    /**
     * 根据教师ID查询课程列表
     */
    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "根据教师ID查询课程列表", description = "获取指定教师的所有课程")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<Course>> getCoursesByTeacherId(@Parameter(description = "教师ID") @PathVariable Long teacherId) {
        List<Course> courses = courseService.findByTeacherId(teacherId);
            return ApiResponse.success(courses);
    }

    /**
     * 根据学期查询课程列表
     */
    @GetMapping("/semester/{semester}")
    @Operation(summary = "根据学期查询课程列表", description = "获取指定学期的所有课程")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<List<Course>> getCoursesBySemester(@Parameter(description = "学期") @PathVariable String semester) {
        List<Course> courses = courseService.findBySemester(semester);
            return ApiResponse.success(courses);
    }

    /**
     * 根据课程类型查询课程列表
     */
    @GetMapping("/type/{courseType}")
    @Operation(summary = "根据课程类型查询课程列表", description = "获取指定类型的所有课程")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<List<Course>> getCoursesByType(@Parameter(description = "课程类型") @PathVariable String courseType) {
        List<Course> courses = courseService.findByCourseType(courseType);
            return ApiResponse.success(courses);
    }

    /**
     * 统计课程数量按类型
     */
    @GetMapping("/stats/type")
    @Operation(summary = "统计课程数量按类型", description = "按类型统计课程数量")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<CourseTypeCount>> countCoursesByType() {
        List<CourseTypeCount> stats = courseService.countCoursesByType();
        return ApiResponse.success(stats);
    }

    /**
     * 搜索课程
     */
    @GetMapping("/search")
    @Operation(summary = "搜索课程", description = "根据关键词搜索课程")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<List<Course>> searchCourses(@Parameter(description = "关键词") @RequestParam String keyword) {
        List<Course> courses = courseService.searchCourses(keyword);
            return ApiResponse.success(courses);
    }

    /**
     * 创建课程
     */
    @PostMapping
    @Operation(summary = "创建课程", description = "添加新课程信息")
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteCourse(@Parameter(description = "课程ID") @PathVariable Long id) {
        try {
            boolean result = courseService.deleteCourse(id);
            if (result) {
                return ApiResponse.success("删除课程成功");
            } else {
                return ApiResponse.error(404, "课程不存在");
            }
        } catch (IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "删除课程失败：" + e.getMessage());
            }
    }

    /**
     * 批量删除课程
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除课程", description = "批量删除多个课程")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> batchDeleteCourses(@Parameter(description = "课程ID列表") @RequestBody List<Long> ids) {
        try {
            boolean result = courseService.batchDeleteCourses(ids);
            if (result) {
                return ApiResponse.success("批量删除课程成功");
            } else {
                return ApiResponse.error(500, "批量删除课程失败");
            }
        } catch (IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "批量删除课程失败：" + e.getMessage());
        }
    }

    /**
     * 更新课程选课人数
     */
    @PutMapping("/{id}/update-enrolled-students")
    @Operation(summary = "更新课程选课人数", description = "更新指定课程的选课人数")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> updateEnrolledStudents(@Parameter(description = "课程ID") @PathVariable Long id) {
        boolean result = courseService.updateEnrolledStudents(id);
        if (result) {
            return ApiResponse.success("更新课程选课人数成功");
        } else {
            return ApiResponse.error(404, "课程不存在");
        }
    }

    // 请求对象内部类
    public static class CreateCourseRequest {
        private String courseCode;
        private String courseName;
        private BigDecimal credits;
        private String courseType;
        private String department;
        private String description;

        // Getter和Setter
        public String getCourseCode() { return courseCode; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        public BigDecimal getCredits() { return credits; }
        public void setCredits(BigDecimal credits) { this.credits = credits; }
        public String getCourseType() { return courseType; }
        public void setCourseType(String courseType) { this.courseType = courseType; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class UpdateCourseRequest {
        private String courseCode;
        private String courseName;
        private BigDecimal credits;
        private String courseType;
        private String department;
        private String description;

        // Getter和Setter
        public String getCourseCode() { return courseCode; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        public BigDecimal getCredits() { return credits; }
        public void setCredits(BigDecimal credits) { this.credits = credits; }
        public String getCourseType() { return courseType; }
        public void setCourseType(String courseType) { this.courseType = courseType; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
