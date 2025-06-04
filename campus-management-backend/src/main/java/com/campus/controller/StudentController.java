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
import com.campus.entity.Student;
import com.campus.repository.StudentRepository.StudentGradeCount;
import com.campus.repository.StudentRepository.StudentWithUser;
import com.campus.service.StudentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 学生管理控制器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@RestController
@RequestMapping("/students")
@Tag(name = "学生管理", description = "学生信息管理相关接口")
@SecurityRequirement(name = "Bearer")
public class StudentController {

    @Autowired
    private StudentService studentService;


    /**
     * 获取学生列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取学生列表", description = "分页查询学生信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<Map<String, Object>> getStudents(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "学号") @RequestParam(required = false) String studentNo,
            @Parameter(description = "年级") @RequestParam(required = false) String grade,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (studentNo != null && !studentNo.isEmpty()) {
            params.put("studentNo", studentNo);
        }
        if (grade != null && !grade.isEmpty()) {
            params.put("grade", grade);
        }
        if (classId != null) {
            params.put("classId", classId);
        }
        if (status != null) {
            params.put("status", status);
        }

        // 执行分页查询
        IPage<Student> pageResult = studentService.findStudentsByPage(page, size, params);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("pages", pageResult.getPages());
        result.put("current", pageResult.getCurrent());
        result.put("size", pageResult.getSize());
        result.put("records", pageResult.getRecords());

        return ApiResponse.success("获取学生列表成功", result);
    }

    /**
     * 获取学生详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取学生详情", description = "根据ID查询学生详细信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<StudentWithUser> getStudentById(@Parameter(description = "学生ID") @PathVariable Long id) {
        Optional<StudentWithUser> student = studentService.findStudentWithUser(id);
        if (student.isPresent()) {
            return ApiResponse.success(student.get());
        } else {
            return ApiResponse.error(404, "学生不存在");
        }
    }

    /**
     * 根据学号查询学生
     */
    @GetMapping("/no/{studentNo}")
    @Operation(summary = "根据学号查询学生", description = "根据学号查询学生信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<Student> getStudentByNo(@Parameter(description = "学号") @PathVariable String studentNo) {
        Optional<Student> student = studentService.findByStudentNo(studentNo);
        if (student.isPresent()) {
            return ApiResponse.success(student.get());
        } else {
            return ApiResponse.error(404, "学生不存在");
        }
    }

    /**
     * 根据用户ID查询学生
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "根据用户ID查询学生", description = "根据用户ID查询学生信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ApiResponse<Student> getStudentByUserId(@Parameter(description = "用户ID") @PathVariable Long userId) {
        Optional<Student> student = studentService.findByUserId(userId);
        if (student.isPresent()) {
            return ApiResponse.success(student.get());
        } else {
            return ApiResponse.error(404, "学生不存在");
        }
    }

    /**
     * 根据班级ID查询学生列表
     */
    @GetMapping("/class/{classId}")
    @Operation(summary = "根据班级ID查询学生列表", description = "获取指定班级的所有学生")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<Student>> getStudentsByClassId(@Parameter(description = "班级ID") @PathVariable Long classId) {
        List<Student> students = studentService.findByClassId(classId);
        return ApiResponse.success(students);
    }

    /**
     * 根据年级查询学生列表
     */
    @GetMapping("/grade/{grade}")
    @Operation(summary = "根据年级查询学生列表", description = "获取指定年级的所有学生")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<Student>> getStudentsByGrade(@Parameter(description = "年级") @PathVariable String grade) {
        List<Student> students = studentService.findByGrade(grade);
        return ApiResponse.success(students);
    }

    /**
     * 搜索学生
     */
    @GetMapping("/search")
    @Operation(summary = "搜索学生", description = "根据关键词搜索学生")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<StudentWithUser>> searchStudents(@Parameter(description = "关键词") @RequestParam String keyword) {
        List<StudentWithUser> students = studentService.searchStudents(keyword);
        return ApiResponse.success(students);
    }

    /**
     * 统计学生数量按年级
     */
    @GetMapping("/stats/grade")
    @Operation(summary = "统计学生数量按年级", description = "按年级统计学生数量")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<StudentGradeCount>> countStudentsByGrade() {
        List<StudentGradeCount> stats = studentService.countStudentsByGrade();
        return ApiResponse.success(stats);
    }

    /**
     * 创建学生
     */
    @PostMapping
    @Operation(summary = "创建学生", description = "添加新学生信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Student> createStudent(@Parameter(description = "学生信息") @Valid @RequestBody Student student) {
        try {
            Student createdStudent = studentService.createStudent(student);
            return ApiResponse.success("创建学生成功", createdStudent);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "创建学生失败：" + e.getMessage());
        }
    }

    /**
     * 更新学生信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新学生信息", description = "修改学生信息")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> updateStudent(
            @Parameter(description = "学生ID") @PathVariable Long id,
            @Parameter(description = "学生信息") @Valid @RequestBody Student student) {
        try {
            student.setId(id);
            boolean result = studentService.updateStudent(student);
            if (result) {
                return ApiResponse.success("更新学生信息成功");
            } else {
                return ApiResponse.error(404, "学生不存在");
            }
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "更新学生信息失败：" + e.getMessage());
        }
    }

    /**
     * 删除学生
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除学生", description = "删除指定学生")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteStudent(@Parameter(description = "学生ID") @PathVariable Long id) {
        boolean result = studentService.deleteStudent(id);
        if (result) {
            return ApiResponse.success("删除学生成功");
        } else {
            return ApiResponse.error(404, "学生不存在");
        }
    }

    /**
     * 批量删除学生
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除学生", description = "批量删除多个学生")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> batchDeleteStudents(@Parameter(description = "学生ID列表") @RequestBody List<Long> ids) {
        boolean result = studentService.batchDeleteStudents(ids);
        if (result) {
            return ApiResponse.success("批量删除学生成功");
        } else {
            return ApiResponse.error(500, "批量删除学生失败");
        }
    }

    /**
     * 导入学生数据
     */
    @PostMapping("/import")
    @Operation(summary = "导入学生数据", description = "批量导入学生数据")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> importStudents(@Parameter(description = "学生列表") @RequestBody List<Student> students) {
        Map<String, Object> result = studentService.importStudents(students);
        return ApiResponse.success("导入学生数据完成", result);
    }

    /**
     * 导出学生数据
     */
    @GetMapping("/export")
    @Operation(summary = "导出学生数据", description = "导出学生数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<List<Student>> exportStudents(
            @Parameter(description = "年级") @RequestParam(required = false) String grade,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (grade != null && !grade.isEmpty()) {
            params.put("grade", grade);
        }
        if (classId != null) {
            params.put("classId", classId);
        }
        if (status != null) {
            params.put("status", status);
        }

        List<Student> students = studentService.exportStudents(params);
        return ApiResponse.success("导出学生数据成功", students);
    }
}
