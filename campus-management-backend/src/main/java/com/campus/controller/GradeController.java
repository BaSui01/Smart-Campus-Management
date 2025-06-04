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
import com.campus.entity.Grade;
import com.campus.repository.GradeRepository.CourseGradeDetail;
import com.campus.repository.GradeRepository.GradeDetail;
import com.campus.repository.GradeRepository.StudentGradeDetail;
import com.campus.service.GradeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 成绩管理控制器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@RestController
@RequestMapping("/grades")
@Tag(name = "成绩管理", description = "成绩信息管理相关接口")
@SecurityRequirement(name = "Bearer")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    /**
     * 获取成绩列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取成绩列表", description = "分页查询成绩信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<Map<String, Object>> getGrades(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "课程表ID") @RequestParam(required = false) Long scheduleId,
            @Parameter(description = "选课ID") @RequestParam(required = false) Long selectionId,
            @Parameter(description = "学期") @RequestParam(required = false) String semester,
            @Parameter(description = "教师ID") @RequestParam(required = false) Long teacherId,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "是否补考") @RequestParam(required = false) Integer isMakeup,
            @Parameter(description = "是否重修") @RequestParam(required = false) Integer isRetake) {

        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (studentId != null) {
            params.put("studentId", studentId);
        }
        if (courseId != null) {
            params.put("courseId", courseId);
        }
        if (scheduleId != null) {
            params.put("scheduleId", scheduleId);
        }
        if (selectionId != null) {
            params.put("selectionId", selectionId);
        }
        if (semester != null && !semester.isEmpty()) {
            params.put("semester", semester);
        }
        if (teacherId != null) {
            params.put("teacherId", teacherId);
        }
        if (status != null) {
            params.put("status", status);
        }
        if (isMakeup != null) {
            params.put("isMakeup", isMakeup);
        }
        if (isRetake != null) {
            params.put("isRetake", isRetake);
        }

        // 执行分页查询
        IPage<Grade> pageResult = gradeService.findGradesByPage(page, size, params);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        result.put("current", pageResult.getCurrent());
        result.put("size", pageResult.getSize());
        result.put("records", pageResult.getRecords());

        return ApiResponse.success("获取成绩列表成功", result);
    }

    /**
     * 获取成绩详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取成绩详情", description = "根据ID查询成绩详细信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<GradeDetail> getGradeById(@Parameter(description = "成绩ID") @PathVariable Long id) {
        Optional<GradeDetail> gradeDetail = gradeService.findGradeDetailById(id);
        if (gradeDetail.isPresent()) {
            return ApiResponse.success(gradeDetail.get());
        } else {
            return ApiResponse.error(404, "成绩不存在");
        }
    }

    /**
     * 根据学生ID查询成绩列表
     */
    @GetMapping("/student/{studentId}")
    @Operation(summary = "根据学生ID查询成绩列表", description = "获取指定学生的所有成绩")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<List<Grade>> getGradesByStudentId(@Parameter(description = "学生ID") @PathVariable Long studentId) {
        List<Grade> grades = gradeService.findByStudentId(studentId);
        return ApiResponse.success(grades);
    }

    /**
     * 根据课程ID查询成绩列表
     */
    @GetMapping("/course/{courseId}")
    @Operation(summary = "根据课程ID查询成绩列表", description = "获取指定课程的所有成绩")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<Grade>> getGradesByCourseId(@Parameter(description = "课程ID") @PathVariable Long courseId) {
        List<Grade> grades = gradeService.findByCourseId(courseId);
        return ApiResponse.success(grades);
    }

    /**
     * 根据课程表ID查询成绩列表
     */
    @GetMapping("/schedule/{scheduleId}")
    @Operation(summary = "根据课程表ID查询成绩列表", description = "获取指定课程表的所有成绩")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<Grade>> getGradesByScheduleId(@Parameter(description = "课程表ID") @PathVariable Long scheduleId) {
        List<Grade> grades = gradeService.findByScheduleId(scheduleId);
        return ApiResponse.success(grades);
    }

    /**
     * 根据选课ID查询成绩
     */
    @GetMapping("/selection/{selectionId}")
    @Operation(summary = "根据选课ID查询成绩", description = "获取指定选课的成绩")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<Grade> getGradeBySelectionId(@Parameter(description = "选课ID") @PathVariable Long selectionId) {
        Optional<Grade> grade = gradeService.findBySelectionId(selectionId);
        if (grade.isPresent()) {
            return ApiResponse.success(grade.get());
        } else {
            return ApiResponse.error(404, "成绩不存在");
        }
    }

    /**
     * 根据学期查询成绩列表
     */
    @GetMapping("/semester/{semester}")
    @Operation(summary = "根据学期查询成绩列表", description = "获取指定学期的所有成绩")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<Grade>> getGradesBySemester(@Parameter(description = "学期") @PathVariable String semester) {
        List<Grade> grades = gradeService.findBySemester(semester);
        return ApiResponse.success(grades);
    }

    /**
     * 根据学生ID和课程ID查询成绩列表
     */
    @GetMapping("/student/{studentId}/course/{courseId}")
    @Operation(summary = "根据学生ID和课程ID查询成绩列表", description = "获取指定学生在指定课程的所有成绩")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<List<Grade>> getGradesByStudentIdAndCourseId(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        List<Grade> grades = gradeService.findByStudentIdAndCourseId(studentId, courseId);
        return ApiResponse.success(grades);
    }

    /**
     * 根据学生ID和学期查询成绩列表
     */
    @GetMapping("/student/{studentId}/semester/{semester}")
    @Operation(summary = "根据学生ID和学期查询成绩列表", description = "获取指定学生在指定学期的所有成绩")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<List<Grade>> getGradesByStudentIdAndSemester(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @Parameter(description = "学期") @PathVariable String semester) {
        List<Grade> grades = gradeService.findByStudentIdAndSemester(studentId, semester);
        return ApiResponse.success(grades);
    }

    /**
     * 获取学生的成绩详情列表
     */
    @GetMapping("/student/{studentId}/details/{semester}")
    @Operation(summary = "获取学生的成绩详情列表", description = "获取指定学生在指定学期的成绩详情列表")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<List<StudentGradeDetail>> getStudentGradeDetails(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @Parameter(description = "学期") @PathVariable String semester) {
        List<StudentGradeDetail> gradeDetails = gradeService.findStudentGradeDetails(studentId, semester);
        return ApiResponse.success(gradeDetails);
    }

    /**
     * 获取课程的学生成绩列表
     */
    @GetMapping("/course/{courseId}/schedule/{scheduleId}/details")
    @Operation(summary = "获取课程的学生成绩列表", description = "获取指定课程和课程表的学生成绩列表")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<CourseGradeDetail>> getCourseGradeDetails(
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            @Parameter(description = "课程表ID") @PathVariable Long scheduleId) {
        List<CourseGradeDetail> gradeDetails = gradeService.findCourseGradeDetails(courseId, scheduleId);
        return ApiResponse.success(gradeDetails);
    }

    /**
     * 计算学生的平均绩点
     */
    @GetMapping("/student/{studentId}/gpa/{semester}")
    @Operation(summary = "计算学生的平均绩点", description = "计算指定学生在指定学期的平均绩点")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<Double> calculateStudentGPA(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @Parameter(description = "学期") @PathVariable String semester) {
        Double gpa = gradeService.calculateStudentGPA(studentId, semester);
        return ApiResponse.success(gpa);
    }

    /**
     * 计算班级的平均成绩
     */
    @GetMapping("/class/{classId}/course/{courseId}/average")
    @Operation(summary = "计算班级的平均成绩", description = "计算指定班级在指定课程的平均成绩")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<Double> calculateClassAverageScore(
            @Parameter(description = "班级ID") @PathVariable Long classId,
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        Double averageScore = gradeService.calculateClassAverageScore(classId, courseId);
        return ApiResponse.success(averageScore);
    }

    /**
     * 创建成绩
     */
    @PostMapping
    @Operation(summary = "创建成绩", description = "添加新成绩信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<Grade> createGrade(@Parameter(description = "成绩信息") @Valid @RequestBody Grade grade) {
        try {
            Grade createdGrade = gradeService.createGrade(grade);
            return ApiResponse.success("创建成绩成功", createdGrade);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "创建成绩失败：" + e.getMessage());
        }
    }

    /**
     * 更新成绩信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新成绩信息", description = "修改成绩信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<Void> updateGrade(
            @Parameter(description = "成绩ID") @PathVariable Long id,
            @Parameter(description = "成绩信息") @Valid @RequestBody Grade grade) {
        try {
            grade.setId(id);
            boolean result = gradeService.updateGrade(grade);
            if (result) {
                return ApiResponse.success("更新成绩信息成功");
            } else {
                return ApiResponse.error(404, "成绩不存在");
            }
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "更新成绩信息失败：" + e.getMessage());
        }
    }

    /**
     * 批量更新成绩
     */
    @PutMapping("/batch")
    @Operation(summary = "批量更新成绩", description = "批量修改成绩信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<Void> batchUpdateGrades(@Parameter(description = "成绩列表") @Valid @RequestBody List<Grade> grades) {
        try {
            boolean result = gradeService.batchUpdateGrades(grades);
            if (result) {
                return ApiResponse.success("批量更新成绩成功");
            } else {
                return ApiResponse.error(500, "批量更新成绩失败");
            }
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "批量更新成绩失败：" + e.getMessage());
        }
    }

    /**
     * 删除成绩
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除成绩", description = "删除指定成绩")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteGrade(@Parameter(description = "成绩ID") @PathVariable Long id) {
        try {
            boolean result = gradeService.deleteGrade(id);
            if (result) {
                return ApiResponse.success("删除成绩成功");
            } else {
                return ApiResponse.error(404, "成绩不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "删除成绩失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除成绩
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除成绩", description = "批量删除多个成绩")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> batchDeleteGrades(@Parameter(description = "成绩ID列表") @RequestBody List<Long> ids) {
        try {
            boolean result = gradeService.batchDeleteGrades(ids);
            if (result) {
                return ApiResponse.success("批量删除成绩成功");
            } else {
                return ApiResponse.error(500, "批量删除成绩失败");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "批量删除成绩失败：" + e.getMessage());
        }
    }

    /**
     * 从选课记录创建成绩记录
     */
    @PostMapping("/selection/{selectionId}")
    @Operation(summary = "从选课记录创建成绩记录", description = "根据选课记录创建成绩记录")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<Grade> createGradeFromSelection(@Parameter(description = "选课ID") @PathVariable Long selectionId) {
        try {
            Grade grade = gradeService.createGradeFromSelection(selectionId);
            return ApiResponse.success("创建成绩记录成功", grade);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "创建成绩记录失败：" + e.getMessage());
        }
    }

    /**
     * 批量从课程表创建成绩记录
     */
    @PostMapping("/schedule/{scheduleId}/batch-create")
    @Operation(summary = "批量从课程表创建成绩记录", description = "根据课程表批量创建成绩记录")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<Integer> batchCreateGradesFromSchedule(@Parameter(description = "课程表ID") @PathVariable Long scheduleId) {
        try {
            int count = gradeService.batchCreateGradesFromSchedule(scheduleId);
            return ApiResponse.success("批量创建成绩记录成功，共创建 " + count + " 条记录", count);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "批量创建成绩记录失败：" + e.getMessage());
        }
    }

    /**
     * 计算总成绩
     */
    @PutMapping("/{id}/calculate-final-score")
    @Operation(summary = "计算总成绩", description = "根据平时成绩、期中成绩和期末成绩计算总成绩")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<Grade> calculateFinalScore(
            @Parameter(description = "成绩ID") @PathVariable Long id) {
        try {
            Grade grade = gradeService.getById(id);
            if (grade == null) {
                return ApiResponse.error(404, "成绩不存在");
            }

            grade = gradeService.calculateFinalScore(grade);
            gradeService.updateById(grade);

            return ApiResponse.success("计算总成绩成功", grade);
        } catch (Exception e) {
            return ApiResponse.error(500, "计算总成绩失败：" + e.getMessage());
        }
    }

    /**
     * 计算绩点和等级
     */
    @PutMapping("/{id}/calculate-grade-point")
    @Operation(summary = "计算绩点和等级", description = "根据总成绩计算绩点和等级")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<Grade> calculateGradePointAndLevel(
            @Parameter(description = "成绩ID") @PathVariable Long id) {
        try {
            Grade grade = gradeService.getById(id);
            if (grade == null) {
                return ApiResponse.error(404, "成绩不存在");
            }

            grade = gradeService.calculateGradePointAndLevel(grade);
            gradeService.updateById(grade);

            return ApiResponse.success("计算绩点和等级成功", grade);
        } catch (Exception e) {
            return ApiResponse.error(500, "计算绩点和等级失败：" + e.getMessage());
        }
    }
}
