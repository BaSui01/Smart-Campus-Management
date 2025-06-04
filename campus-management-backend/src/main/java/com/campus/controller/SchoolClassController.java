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
import com.campus.entity.SchoolClass;
import com.campus.entity.Student;
import com.campus.repository.SchoolClassRepository.ClassDetail;
import com.campus.repository.SchoolClassRepository.ClassGradeCount;
import com.campus.service.SchoolClassService;
import com.campus.service.StudentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 班级管理控制器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@RestController
@RequestMapping("/classes")
@Tag(name = "班级管理", description = "班级信息管理相关接口")
@SecurityRequirement(name = "Bearer")
public class SchoolClassController {

    @Autowired
    private SchoolClassService schoolClassService;

    @Autowired
    private StudentService studentService;

    /**
     * 获取班级列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取班级列表", description = "分页查询班级信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<Map<String, Object>> getClasses(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "班级名称") @RequestParam(required = false) String className,
            @Parameter(description = "班级代码") @RequestParam(required = false) String classCode,
            @Parameter(description = "年级") @RequestParam(required = false) String grade,
            @Parameter(description = "部门ID") @RequestParam(required = false) Long departmentId,
            @Parameter(description = "班主任ID") @RequestParam(required = false) Long headTeacherId,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (className != null && !className.isEmpty()) {
            params.put("className", className);
        }
        if (classCode != null && !classCode.isEmpty()) {
            params.put("classCode", classCode);
        }
        if (grade != null && !grade.isEmpty()) {
            params.put("grade", grade);
        }
        if (departmentId != null) {
            params.put("departmentId", departmentId);
        }
        if (headTeacherId != null) {
            params.put("headTeacherId", headTeacherId);
        }
        if (status != null) {
            params.put("status", status);
        }

        // 执行分页查询
        IPage<SchoolClass> pageResult = schoolClassService.findClassesByPage(page, size, params);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        result.put("current", pageResult.getCurrent());
        result.put("size", pageResult.getSize());
        result.put("records", pageResult.getRecords());

        return ApiResponse.success("获取班级列表成功", result);
    }

    /**
     * 获取班级详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取班级详情", description = "根据ID查询班级详细信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<ClassDetail> getClassById(@Parameter(description = "班级ID") @PathVariable Long id) {
        Optional<ClassDetail> classDetail = schoolClassService.findClassDetailById(id);
        if (classDetail.isPresent()) {
            return ApiResponse.success(classDetail.get());
        } else {
            return ApiResponse.error(404, "班级不存在");
        }
    }

    /**
     * 根据班级代码查询班级
     */
    @GetMapping("/code/{classCode}")
    @Operation(summary = "根据班级代码查询班级", description = "根据班级代码查询班级信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<SchoolClass> getClassByCode(@Parameter(description = "班级代码") @PathVariable String classCode) {
        Optional<SchoolClass> schoolClass = schoolClassService.findByClassCode(classCode);
        if (schoolClass.isPresent()) {
            return ApiResponse.success(schoolClass.get());
        } else {
            return ApiResponse.error(404, "班级不存在");
        }
    }

    /**
     * 根据年级查询班级列表
     */
    @GetMapping("/grade/{grade}")
    @Operation(summary = "根据年级查询班级列表", description = "获取指定年级的所有班级")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<SchoolClass>> getClassesByGrade(@Parameter(description = "年级") @PathVariable String grade) {
        List<SchoolClass> classes = schoolClassService.findByGrade(grade);
        return ApiResponse.success(classes);
    }

    /**
     * 根据部门ID查询班级列表
     */
    @GetMapping("/department/{departmentId}")
    @Operation(summary = "根据部门ID查询班级列表", description = "获取指定部门的所有班级")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<SchoolClass>> getClassesByDepartmentId(@Parameter(description = "部门ID") @PathVariable Long departmentId) {
        List<SchoolClass> classes = schoolClassService.findByDepartmentId(departmentId);
        return ApiResponse.success(classes);
    }

    /**
     * 根据班主任ID查询班级列表
     */
    @GetMapping("/teacher/{headTeacherId}")
    @Operation(summary = "根据班主任ID查询班级列表", description = "获取指定班主任的所有班级")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<SchoolClass>> getClassesByHeadTeacherId(@Parameter(description = "班主任ID") @PathVariable Long headTeacherId) {
        List<SchoolClass> classes = schoolClassService.findByHeadTeacherId(headTeacherId);
        return ApiResponse.success(classes);
    }

    /**
     * 统计班级数量按年级
     */
    @GetMapping("/stats/grade")
    @Operation(summary = "统计班级数量按年级", description = "按年级统计班级数量")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<ClassGradeCount>> countClassesByGrade() {
        List<ClassGradeCount> stats = schoolClassService.countClassesByGrade();
        return ApiResponse.success(stats);
    }

    /**
     * 获取班级学生列表
     */
    @GetMapping("/{classId}/students")
    @Operation(summary = "获取班级学生列表", description = "获取指定班级的所有学生")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<Student>> getClassStudents(@Parameter(description = "班级ID") @PathVariable Long classId) {
        List<Student> students = studentService.findByClassId(classId);
        return ApiResponse.success(students);
    }

    /**
     * 创建班级
     */
    @PostMapping
    @Operation(summary = "创建班级", description = "添加新班级信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<SchoolClass> createClass(@Parameter(description = "班级信息") @Valid @RequestBody SchoolClass schoolClass) {
        try {
            SchoolClass createdClass = schoolClassService.createClass(schoolClass);
            return ApiResponse.success("创建班级成功", createdClass);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "创建班级失败：" + e.getMessage());
        }
    }

    /**
     * 更新班级信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新班级信息", description = "修改班级信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> updateClass(
            @Parameter(description = "班级ID") @PathVariable Long id,
            @Parameter(description = "班级信息") @Valid @RequestBody SchoolClass schoolClass) {
        try {
            schoolClass.setId(id);
            boolean result = schoolClassService.updateClass(schoolClass);
            if (result) {
                return ApiResponse.success("更新班级信息成功");
            } else {
                return ApiResponse.error(404, "班级不存在");
            }
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "更新班级信息失败：" + e.getMessage());
        }
    }

    /**
     * 删除班级
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除班级", description = "删除指定班级")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteClass(@Parameter(description = "班级ID") @PathVariable Long id) {
        try {
            boolean result = schoolClassService.deleteClass(id);
            if (result) {
                return ApiResponse.success("删除班级成功");
            } else {
                return ApiResponse.error(404, "班级不存在");
            }
        } catch (IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "删除班级失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除班级
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除班级", description = "批量删除多个班级")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> batchDeleteClasses(@Parameter(description = "班级ID列表") @RequestBody List<Long> ids) {
        try {
            boolean result = schoolClassService.batchDeleteClasses(ids);
            if (result) {
                return ApiResponse.success("批量删除班级成功");
            } else {
                return ApiResponse.error(500, "批量删除班级失败");
            }
        } catch (IllegalStateException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "批量删除班级失败：" + e.getMessage());
        }
    }

    /**
     * 更新班级学生数量
     */
    @PutMapping("/{id}/update-student-count")
    @Operation(summary = "更新班级学生数量", description = "更新指定班级的学生数量")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> updateStudentCount(@Parameter(description = "班级ID") @PathVariable Long id) {
        boolean result = schoolClassService.updateStudentCount(id);
        if (result) {
            return ApiResponse.success("更新班级学生数量成功");
        } else {
            return ApiResponse.error(404, "班级不存在");
        }
    }
}
