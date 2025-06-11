package com.campus.interfaces.rest.v1;

import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.campus.application.service.GradeService;
import com.campus.shared.common.ApiResponse;
import com.campus.domain.entity.Grade;
import com.campus.interfaces.rest.common.BaseController;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 成绩管理API控制器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@RestController
@RequestMapping("/api/grades")
@Tag(name = "成绩管理API", description = "学生成绩管理REST API接口")
@SecurityRequirement(name = "Bearer")
public class GradeApiController extends BaseController {

    private final GradeService gradeService;

    public GradeApiController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    /**
     * 获取成绩列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取成绩列表", description = "分页查询成绩信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<Map<String, Object>> getGrades(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "学期") @RequestParam(required = false) String semester,
            @Parameter(description = "考试类型") @RequestParam(required = false) String examType) {

        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (studentId != null) {
            params.put("studentId", studentId);
        }
        if (courseId != null) {
            params.put("courseId", courseId);
        }
        if (semester != null && !semester.isEmpty()) {
            params.put("semester", semester);
        }
        if (examType != null && !examType.isEmpty()) {
            params.put("examType", examType);
        }

        // 执行分页查询
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Grade> pageResult = gradeService.findGradesByPage(pageable, params);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotalElements());
        result.put("pages", pageResult.getTotalPages());
        result.put("current", pageResult.getNumber() + 1);
        result.put("size", pageResult.getSize());
        result.put("records", pageResult.getContent());

        return ApiResponse.success("获取成绩列表成功", result);
    }

    /**
     * 获取成绩详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取成绩详情", description = "根据ID查询成绩详细信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<Grade> getGradeById(@Parameter(description = "成绩ID") @PathVariable Long id) {
        Optional<Grade> grade = gradeService.findById(id);
        if (grade.isPresent()) {
            return ApiResponse.success(grade.get());
        } else {
            return ApiResponse.error(404, "成绩记录不存在");
        }
    }

    /**
     * 根据学生ID查询成绩
     */
    @GetMapping("/student/{studentId}")
    @Operation(summary = "根据学生ID查询成绩", description = "获取指定学生的所有成绩")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<List<Grade>> getGradesByStudentId(@Parameter(description = "学生ID") @PathVariable Long studentId) {
        List<Grade> grades = gradeService.findByStudentId(studentId);
        return ApiResponse.success(grades);
    }

    /**
     * 根据课程ID查询成绩
     */
    @GetMapping("/course/{courseId}")
    @Operation(summary = "根据课程ID查询成绩", description = "获取指定课程的所有成绩")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<Grade>> getGradesByCourseId(@Parameter(description = "课程ID") @PathVariable Long courseId) {
        List<Grade> grades = gradeService.findByCourseId(courseId);
        return ApiResponse.success(grades);
    }

    /**
     * 根据学期查询成绩
     */
    @GetMapping("/semester/{semester}")
    @Operation(summary = "根据学期查询成绩", description = "获取指定学期的所有成绩")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<List<Grade>> getGradesBySemester(@Parameter(description = "学期") @PathVariable String semester) {
        List<Grade> grades = gradeService.findBySemester(semester);
        return ApiResponse.success(grades);
    }

    /**
     * 搜索成绩
     */
    @GetMapping("/search")
    @Operation(summary = "搜索成绩", description = "根据关键词搜索成绩")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<Grade>> searchGrades(@Parameter(description = "关键词") @RequestParam String keyword) {
        List<Grade> grades = gradeService.searchGrades(keyword);
        return ApiResponse.success(grades);
    }

    /**
     * 获取成绩统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取成绩统计信息", description = "获取成绩模块的统计数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getGradeStats() {
        try {
            log.info("获取成绩统计信息");

            Map<String, Object> stats = new HashMap<>();

            // 基础统计
            long totalGrades = gradeService.count();
            stats.put("totalGrades", totalGrades);

            // 整体平均分
            Double overallAverage = gradeService.calculateOverallAverageScore();
            stats.put("overallAverageScore", overallAverage != null ? overallAverage : 0.0);

            // 成绩分布统计
            Map<String, Object> gradeDistribution = gradeService.getGradeDistribution();
            stats.put("gradeDistribution", gradeDistribution);

            // 综合统计
            Map<String, Object> comprehensiveStats = gradeService.generateComprehensiveStatistics();
            stats.put("comprehensiveStats", comprehensiveStats);

            // 时间统计（简化实现）
            stats.put("todayGrades", 0L);
            stats.put("weekGrades", 0L);
            stats.put("monthGrades", 0L);

            // 学期统计
            List<String> semesters = gradeService.findAllSemesters();
            Map<String, Long> semesterStats = new HashMap<>();
            for (String semester : semesters) {
                List<Grade> semesterGrades = gradeService.findBySemester(semester);
                semesterStats.put(semester, (long) semesterGrades.size());
            }
            stats.put("semesterStats", semesterStats);

            // 最近活动（简化实现）
            List<Map<String, Object>> recentActivity = new ArrayList<>();
            stats.put("recentActivity", recentActivity);

            return success("获取成绩统计信息成功", stats);

        } catch (Exception e) {
            log.error("获取成绩统计信息失败: ", e);
            return error("获取成绩统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生成绩统计
     */
    @GetMapping("/student/{studentId}/stats")
    @Operation(summary = "获取学生成绩统计", description = "获取指定学生的成绩统计信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<Map<String, Object>> getStudentGradeStats(@Parameter(description = "学生ID") @PathVariable Long studentId) {
        Map<String, Object> stats = gradeService.getStudentGradeStatistics(studentId);
        return ApiResponse.success(stats);
    }

    /**
     * 获取课程成绩统计
     */
    @GetMapping("/course/{courseId}/stats")
    @Operation(summary = "获取课程成绩统计", description = "获取指定课程的成绩统计信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<Map<String, Object>> getCourseGradeStats(@Parameter(description = "课程ID") @PathVariable Long courseId) {
        Map<String, Object> stats = gradeService.getCourseGradeStatistics(courseId);
        return ApiResponse.success(stats);
    }

    /**
     * 创建成绩记录
     */
    @PostMapping
    @Operation(summary = "创建成绩记录", description = "添加新的成绩记录")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<Grade> createGrade(@Parameter(description = "成绩信息") @Valid @RequestBody Grade grade) {
        try {
            Grade createdGrade = gradeService.createGrade(grade);
            return ApiResponse.success("创建成绩记录成功", createdGrade);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "创建成绩记录失败：" + e.getMessage());
        }
    }

    /**
     * 更新成绩记录
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新成绩记录", description = "修改成绩记录信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<Void> updateGrade(
            @Parameter(description = "成绩ID") @PathVariable Long id,
            @Parameter(description = "成绩信息") @Valid @RequestBody Grade grade) {
        try {
            grade.setId(id);
            boolean result = gradeService.updateGrade(grade);
            if (result) {
                return ApiResponse.success("更新成绩记录成功");
            } else {
                return ApiResponse.error(404, "成绩记录不存在");
            }
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "更新成绩记录失败：" + e.getMessage());
        }
    }

    /**
     * 删除成绩记录
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除成绩记录", description = "删除指定成绩记录")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteGrade(@Parameter(description = "成绩ID") @PathVariable Long id) {
        boolean result = gradeService.deleteGrade(id);
        if (result) {
            return ApiResponse.success("删除成绩记录成功");
        } else {
            return ApiResponse.error(404, "成绩记录不存在");
        }
    }

    /**
     * 批量删除成绩记录
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除成绩记录", description = "批量删除多个成绩记录")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchDeleteGrades(
            @Parameter(description = "成绩ID列表") @RequestBody List<Long> ids) {

        try {
            logOperation("批量删除成绩记录", ids.size());

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("成绩ID列表不能为空");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "成绩记录");
            }

            // 执行批量删除
            boolean result = gradeService.batchDeleteGrades(ids);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("deletedCount", result ? ids.size() : 0);
            responseData.put("totalRequested", ids.size());
            responseData.put("success", result);

            if (result) {
                return success("批量删除成绩记录成功", responseData);
            } else {
                return error("批量删除成绩记录失败");
            }

        } catch (Exception e) {
            log.error("批量删除成绩记录失败: ", e);
            return error("批量删除成绩记录失败: " + e.getMessage());
        }
    }

    /**
     * 批量导入成绩
     */
    @PostMapping("/batch/import")
    @Operation(summary = "批量导入成绩", description = "批量导入成绩数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchImportGrades(
            @Parameter(description = "成绩数据列表") @RequestBody List<Grade> grades) {

        try {
            logOperation("批量导入成绩", grades.size());

            // 验证参数
            if (grades == null || grades.isEmpty()) {
                return badRequest("成绩数据列表不能为空");
            }

            if (grades.size() > 100) {
                return badRequest("单次批量导入不能超过100条记录");
            }

            // 执行批量导入
            Map<String, Object> result = gradeService.importGrades(grades);

            return success("批量导入成绩完成", result);

        } catch (Exception e) {
            log.error("批量导入成绩失败: ", e);
            return error("批量导入成绩失败: " + e.getMessage());
        }
    }

    /**
     * 批量更新成绩
     */
    @PutMapping("/batch/update")
    @Operation(summary = "批量更新成绩", description = "批量更新成绩数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchUpdateGrades(
            @Parameter(description = "成绩数据列表") @RequestBody List<Grade> grades) {

        try {
            logOperation("批量更新成绩", grades.size());

            // 验证参数
            if (grades == null || grades.isEmpty()) {
                return badRequest("成绩数据列表不能为空");
            }

            if (grades.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Grade grade : grades) {
                if (grade.getId() == null) {
                    return badRequest("成绩记录ID不能为空");
                }
                validateId(grade.getId(), "成绩记录");
            }

            // 执行批量更新
            boolean result = gradeService.batchUpdateGrades(grades);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("updatedCount", result ? grades.size() : 0);
            responseData.put("totalRequested", grades.size());
            responseData.put("success", result);

            if (result) {
                return success("批量更新成绩成功", responseData);
            } else {
                return error("批量更新成绩失败");
            }

        } catch (Exception e) {
            log.error("批量更新成绩失败: ", e);
            return error("批量更新成绩失败: " + e.getMessage());
        }
    }

    /**
     * 导出成绩数据
     */
    @GetMapping("/export")
    @Operation(summary = "导出成绩数据", description = "导出成绩数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<Grade>> exportGrades(
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "学期") @RequestParam(required = false) String semester) {

        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (studentId != null) {
            params.put("studentId", studentId);
        }
        if (courseId != null) {
            params.put("courseId", courseId);
        }
        if (semester != null && !semester.isEmpty()) {
            params.put("semester", semester);
        }

        List<Grade> grades = gradeService.exportGrades(params);
        return ApiResponse.success("导出成绩数据成功", grades);
    }
}
