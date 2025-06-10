package com.campus.interfaces.rest.v1;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.campus.application.service.SchoolClassService;
import com.campus.application.service.StudentService;
import com.campus.shared.common.ApiResponse;
import com.campus.domain.entity.SchoolClass;
import com.campus.domain.entity.Student;
import com.campus.interfaces.rest.common.BaseController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * 学生管理API控制器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@RestController
@RequestMapping("/api/students")
@Tag(name = "学生管理API", description = "学生信息管理REST API接口")
@SecurityRequirement(name = "Bearer")
public class StudentApiController extends BaseController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private SchoolClassService schoolClassService;

    /**
     * 获取学生列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取学生列表", description = "分页查询学生信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudents(
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
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Student> pageResult = studentService.findStudentsByPage(pageable, params);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotalElements());
        result.put("pages", pageResult.getTotalPages());
        result.put("current", pageResult.getNumber() + 1);
        result.put("size", pageResult.getSize());
        result.put("records", pageResult.getContent());

        return success("获取学生列表成功", result);
    }

    /**
     * 获取学生详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取学生详情", description = "根据ID查询学生详细信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Object[]>> getStudentById(@Parameter(description = "学生ID") @PathVariable Long id) {
        try {
            logOperation("查询学生详情", id);
            validateId(id, "学生");

            Optional<Object[]> student = studentService.findStudentWithUser(id);
            if (student.isPresent()) {
                return success("查询学生详情成功", student.get());
            } else {
                return notFound("学生不存在");
            }
        } catch (Exception e) {
            log.error("查询学生详情失败: ", e);
            return error("查询学生详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生表单数据
     */
    @GetMapping("/form-data")
    @Operation(summary = "获取学生表单数据", description = "获取创建/编辑学生表单所需的数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentFormData() {
        try {
            Map<String, Object> formData = new HashMap<>();

            // 获取班级列表
            List<SchoolClass> allClasses = schoolClassService.findAll();

            // 获取年级列表
            List<String> grades = allClasses.stream()
                .map(SchoolClass::getGrade)
                .distinct()
                .sorted()
                .toList();

            // 获取专业列表
            List<String> majors = allClasses.stream()
                .map(SchoolClass::getClassName)
                .map(this::extractMajorFromClassName)
                .distinct()
                .filter(major -> !major.equals("其他专业"))
                .sorted()
                .toList();

            if (majors.isEmpty()) {
                majors = List.of("计算机科学与技术", "软件工程", "信息安全", "数据科学与大数据技术", "人工智能", "网络工程", "物联网工程");
            }

            formData.put("classes", allClasses);
            formData.put("grades", grades);
            formData.put("majors", majors);

            return success("获取表单数据成功", formData);
        } catch (Exception e) {
            log.error("获取学生表单数据失败: ", e);
            return error("获取学生表单数据失败：" + e.getMessage());
        }
    }

    /**
     * 创建学生
     */
    @PostMapping
    @Operation(summary = "创建学生", description = "添加新学生信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Student>> createStudent(@Parameter(description = "学生信息") @Valid @RequestBody Student student) {
        try {
            logOperation("创建学生", student.getName(), student.getStudentNo());
            Student createdStudent = studentService.createStudent(student);
            return success("创建学生成功", createdStudent);
        } catch (IllegalArgumentException e) {
            log.error("创建学生参数错误: ", e);
            return badRequest(e.getMessage());
        } catch (Exception e) {
            log.error("创建学生失败: ", e);
            return error("创建学生失败：" + e.getMessage());
        }
    }

    /**
     * 获取学生统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取学生统计信息", description = "获取学生模块的统计数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentStats() {
        try {
            log.info("获取学生统计信息");

            Map<String, Object> stats = new HashMap<>();

            // 获取学生统计信息
            StudentService.StudentStatistics studentStats = studentService.getStudentStatistics();

            // 基础统计
            stats.put("totalStudents", studentStats.getTotalStudents());
            stats.put("activeStudents", studentStats.getActiveStudents());
            stats.put("inactiveStudents", studentStats.getInactiveStudents());
            stats.put("maleStudents", studentStats.getMaleStudents());
            stats.put("femaleStudents", studentStats.getFemaleStudents());

            // 按年级统计
            stats.put("gradeDistribution", studentStats.getGradeDistribution());

            // 按班级统计
            stats.put("classDistribution", studentStats.getClassDistribution());

            // 按年级统计（使用现有方法）
            List<Object[]> gradeStats = studentService.countStudentsByGrade();
            Map<String, Long> gradeStatsMap = new HashMap<>();
            for (Object[] row : gradeStats) {
                gradeStatsMap.put((String) row[0], ((Number) row[1]).longValue());
            }
            stats.put("gradeStats", gradeStatsMap);

            // 今日、本周、本月统计（简化实现）
            stats.put("todayStudents", 0L);
            stats.put("weekStudents", 0L);
            stats.put("monthStudents", 0L);

            // 最近活动（简化实现）
            List<Map<String, Object>> recentActivity = new ArrayList<>();
            stats.put("recentActivity", recentActivity);

            return success("获取学生统计信息成功", stats);

        } catch (Exception e) {
            log.error("获取学生统计信息失败: ", e);
            return error("获取学生统计信息失败: " + e.getMessage());
        }
    }

    // ==================== 批量操作端点 ====================

    /**
     * 批量删除学生
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除学生", description = "根据ID列表批量删除学生")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchDeleteStudents(
            @Parameter(description = "学生ID列表") @RequestBody List<Long> ids) {

        try {
            logOperation("批量删除学生", ids.size());

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("学生ID列表不能为空");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "学生");
            }

            // 执行批量删除
            boolean result = studentService.batchDeleteStudents(ids);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("deletedCount", result ? ids.size() : 0);
            responseData.put("totalRequested", ids.size());
            responseData.put("success", result);

            if (result) {
                return success("批量删除学生成功", responseData);
            } else {
                return error("批量删除学生失败");
            }

        } catch (Exception e) {
            log.error("批量删除学生失败: ", e);
            return error("批量删除学生失败: " + e.getMessage());
        }
    }

    /**
     * 批量导入学生
     */
    @PostMapping("/batch/import")
    @Operation(summary = "批量导入学生", description = "批量导入学生数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchImportStudents(
            @Parameter(description = "学生数据列表") @RequestBody List<Student> students) {

        try {
            logOperation("批量导入学生", students.size());

            // 验证参数
            if (students == null || students.isEmpty()) {
                return badRequest("学生数据列表不能为空");
            }

            if (students.size() > 100) {
                return badRequest("单次批量导入不能超过100条记录");
            }

            // 执行批量导入
            Map<String, Object> result = studentService.importStudents(students);

            return success("批量导入学生完成", result);

        } catch (Exception e) {
            log.error("批量导入学生失败: ", e);
            return error("批量导入学生失败: " + e.getMessage());
        }
    }

    /**
     * 从班级名称中提取专业信息
     */
    private String extractMajorFromClassName(String className) {
        if (className == null || className.isEmpty()) {
            return "其他专业";
        }

        // 常见的专业关键词映射
        if (className.contains("计算机") || className.contains("CS")) {
            return "计算机科学与技术";
        } else if (className.contains("软件") || className.contains("SE")) {
            return "软件工程";
        } else if (className.contains("信息安全") || className.contains("网络安全")) {
            return "信息安全";
        } else if (className.contains("数据") || className.contains("大数据")) {
            return "数据科学与大数据技术";
        } else if (className.contains("人工智能") || className.contains("AI")) {
            return "人工智能";
        } else if (className.contains("网络工程")) {
            return "网络工程";
        } else if (className.contains("物联网")) {
            return "物联网工程";
        } else {
            return "其他专业";
        }
    }
}
