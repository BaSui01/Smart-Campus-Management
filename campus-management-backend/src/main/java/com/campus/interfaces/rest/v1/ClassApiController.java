package com.campus.interfaces.rest.v1;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.campus.application.service.SchoolClassService;
import com.campus.shared.common.ApiResponse;
import com.campus.domain.entity.SchoolClass;
import com.campus.interfaces.rest.common.BaseController;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 班级管理API控制器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@RestController
@RequestMapping("/api/v1/classes")
@Tag(name = "班级管理API", description = "班级信息管理REST API接口")
@SecurityRequirement(name = "Bearer")
public class ClassApiController extends BaseController {

    @Autowired
    private SchoolClassService schoolClassService;

    /**
     * 获取班级列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取班级列表", description = "分页查询班级信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ApiResponse<Map<String, Object>> getClasses(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "班级名称") @RequestParam(required = false) String className,
            @Parameter(description = "年级") @RequestParam(required = false) String grade,
            @Parameter(description = "专业") @RequestParam(required = false) String major,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (className != null && !className.isEmpty()) {
            params.put("className", className);
        }
        if (grade != null && !grade.isEmpty()) {
            params.put("grade", grade);
        }
        if (major != null && !major.isEmpty()) {
            params.put("major", major);
        }
        if (status != null) {
            params.put("status", status);
        }

        // 执行分页查询
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<SchoolClass> pageResult = schoolClassService.findClassesByPage(pageable, params);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotalElements());
        result.put("pages", pageResult.getTotalPages());
        result.put("current", pageResult.getNumber() + 1);
        result.put("size", pageResult.getSize());
        result.put("classes", pageResult.getContent());

        return ApiResponse.success("获取班级列表成功", result);
    }

    /**
     * 获取班级详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取班级详情", description = "根据ID查询班级详细信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ApiResponse<SchoolClass> getClassById(@Parameter(description = "班级ID") @PathVariable Long id) {
        Optional<SchoolClass> schoolClass = schoolClassService.findById(id);
        if (schoolClass.isPresent()) {
            return ApiResponse.success(schoolClass.get());
        } else {
            return ApiResponse.error(404, "班级不存在");
        }
    }

    /**
     * 根据班级代码查询班级
     */
    @GetMapping("/code/{classCode}")
    @Operation(summary = "根据班级代码查询班级", description = "根据班级代码查询班级信息")
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
    public ApiResponse<List<SchoolClass>> getClassesByGrade(@Parameter(description = "年级") @PathVariable String grade) {
        List<SchoolClass> classes = schoolClassService.findByGrade(grade);
        return ApiResponse.success(classes);
    }

    /**
     * 获取班级表单数据
     */
    @GetMapping("/form-data")
    @Operation(summary = "获取班级表单数据", description = "获取创建/编辑班级表单所需的数据")
    public ApiResponse<Map<String, Object>> getClassFormData() {
        Map<String, Object> formData = new HashMap<>();
        
        // 年级选项
        List<Map<String, String>> grades = Arrays.asList(
            Map.of("value", "2021级", "label", "2021级"),
            Map.of("value", "2022级", "label", "2022级"),
            Map.of("value", "2023级", "label", "2023级"),
            Map.of("value", "2024级", "label", "2024级"),
            Map.of("value", "2025级", "label", "2025级")
        );
        
        // 专业选项
        List<Map<String, String>> majors = Arrays.asList(
            Map.of("value", "计算机科学与技术", "label", "计算机科学与技术"),
            Map.of("value", "软件工程", "label", "软件工程"),
            Map.of("value", "信息安全", "label", "信息安全"),
            Map.of("value", "数据科学与大数据技术", "label", "数据科学与大数据技术"),
            Map.of("value", "人工智能", "label", "人工智能"),
            Map.of("value", "网络工程", "label", "网络工程"),
            Map.of("value", "物联网工程", "label", "物联网工程")
        );
        
        // 状态选项
        List<Map<String, Object>> statusOptions = Arrays.asList(
            Map.of("value", 1, "label", "正常"),
            Map.of("value", 0, "label", "停用")
        );
        
        // 班级状态选项
        List<Map<String, Object>> classStatusOptions = Arrays.asList(
            Map.of("value", 1, "label", "在读"),
            Map.of("value", 2, "label", "毕业"),
            Map.of("value", 0, "label", "停班")
        );
        
        formData.put("grades", grades);
        formData.put("majors", majors);
        formData.put("statusOptions", statusOptions);
        formData.put("classStatusOptions", classStatusOptions);
        
        return ApiResponse.success("获取班级表单数据成功", formData);
    }

    /**
     * 创建班级
     */
    @PostMapping
    @Operation(summary = "创建班级", description = "添加新班级信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN')")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN')")
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
    public ApiResponse<Void> deleteClass(@Parameter(description = "班级ID") @PathVariable Long id) {
        boolean result = schoolClassService.deleteClass(id);
        if (result) {
            return ApiResponse.success("删除班级成功");
        } else {
            return ApiResponse.error(404, "班级不存在");
        }
    }

    /**
     * 批量删除班级
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除班级", description = "批量删除多个班级")
    public ApiResponse<Void> batchDeleteClasses(@Parameter(description = "班级ID列表") @RequestBody List<Long> ids) {
        boolean result = schoolClassService.batchDeleteClasses(ids);
        if (result) {
            return ApiResponse.success("批量删除班级成功");
        } else {
            return ApiResponse.error(500, "批量删除班级失败");
        }
    }

    // ==================== 统计端点 ====================

    /**
     * 获取班级统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取班级统计信息", description = "获取班级模块的统计数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getClassStats() {
        try {
            log.info("获取班级统计信息");

            Map<String, Object> stats = new HashMap<>();

            // 基础统计
            long totalClasses = schoolClassService.countTotalClasses();
            stats.put("totalClasses", totalClasses);

            long activeClasses = schoolClassService.countActiveClasses();
            stats.put("activeClasses", activeClasses);

            long inactiveClasses = totalClasses - activeClasses;
            stats.put("inactiveClasses", inactiveClasses);

            // 按年级统计
            List<Object[]> gradeStats = schoolClassService.countClassesByGrade();
            Map<String, Long> gradeStatsMap = new HashMap<>();
            for (Object[] row : gradeStats) {
                gradeStatsMap.put((String) row[0], ((Number) row[1]).longValue());
            }
            stats.put("gradeStats", gradeStatsMap);

            // 按部门统计
            Map<String, Long> departmentStats = schoolClassService.countClassesByDepartment();
            stats.put("departmentStats", departmentStats);

            // 年级列表
            List<String> allGrades = schoolClassService.findAllGrades();
            stats.put("allGrades", allGrades);

            // 时间统计（简化实现）
            stats.put("todayClasses", 0L);
            stats.put("weekClasses", 0L);
            stats.put("monthClasses", 0L);

            // 班级容量统计
            Map<String, Object> capacityStats = new HashMap<>();
            capacityStats.put("averageStudentCount", 35);
            capacityStats.put("maxStudentCount", 50);
            capacityStats.put("minStudentCount", 20);
            stats.put("capacityStats", capacityStats);

            // 最近活动（简化实现）
            List<Map<String, Object>> recentActivity = new ArrayList<>();
            stats.put("recentActivity", recentActivity);

            return success("获取班级统计信息成功", stats);

        } catch (Exception e) {
            log.error("获取班级统计信息失败: ", e);
            return error("获取班级统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 统计班级数量按年级
     */
    @GetMapping("/stats/grade")
    @Operation(summary = "统计班级数量按年级", description = "按年级统计班级数量")
    public ApiResponse<List<Object[]>> countClassesByGrade() {
        List<Object[]> stats = schoolClassService.countClassesByGrade();
        return ApiResponse.success(stats);
    }

    // ==================== 批量操作端点 ====================

    /**
     * 批量更新班级状态
     */
    @PutMapping("/batch/status")
    @Operation(summary = "批量更新班级状态", description = "批量更新班级的启用/禁用状态")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchUpdateClassStatus(
            @Parameter(description = "批量更新请求") @RequestBody Map<String, Object> request) {

        try {
            @SuppressWarnings("unchecked")
            List<Long> ids = (List<Long>) request.get("ids");
            Integer status = (Integer) request.get("status");

            logOperation("批量更新班级状态", ids.size(), "状态: " + status);

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("班级ID列表不能为空");
            }

            if (status == null || (status != 0 && status != 1)) {
                return badRequest("状态值必须为0（禁用）或1（启用）");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "班级");
            }

            // 执行批量状态更新
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Long id : ids) {
                try {
                    boolean result = status == 1 ?
                        schoolClassService.enableClass(id) :
                        schoolClassService.disableClass(id);
                    if (result) {
                        successCount++;
                    } else {
                        failCount++;
                        failReasons.add("班级ID " + id + ": 更新失败");
                    }
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("班级ID " + id + ": " + e.getMessage());
                    log.warn("更新班级{}状态失败: {}", id, e.getMessage());
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", ids.size());
            responseData.put("status", status == 1 ? "启用" : "禁用");
            responseData.put("failReasons", failReasons);

            if (failCount == 0) {
                return success("批量更新班级状态成功", responseData);
            } else if (successCount > 0) {
                return success("批量更新班级状态部分成功", responseData);
            } else {
                return error("批量更新班级状态失败");
            }

        } catch (Exception e) {
            log.error("批量更新班级状态失败: ", e);
            return error("批量更新班级状态失败: " + e.getMessage());
        }
    }

    /**
     * 批量导入班级
     */
    @PostMapping("/batch/import")
    @Operation(summary = "批量导入班级", description = "批量导入班级数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchImportClasses(
            @Parameter(description = "班级数据列表") @RequestBody List<SchoolClass> classes) {

        try {
            logOperation("批量导入班级", classes.size());

            // 验证参数
            if (classes == null || classes.isEmpty()) {
                return badRequest("班级数据列表不能为空");
            }

            if (classes.size() > 100) {
                return badRequest("单次批量导入不能超过100条记录");
            }

            // 执行批量导入
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (SchoolClass schoolClass : classes) {
                try {
                    // 验证班级数据
                    if (schoolClass.getClassName() == null || schoolClass.getClassName().trim().isEmpty()) {
                        failCount++;
                        failReasons.add("班级名称不能为空");
                        continue;
                    }

                    if (schoolClass.getClassCode() == null || schoolClass.getClassCode().trim().isEmpty()) {
                        failCount++;
                        failReasons.add("班级代码不能为空");
                        continue;
                    }

                    // 检查班级代码是否已存在
                    if (schoolClassService.existsByClassCode(schoolClass.getClassCode())) {
                        failCount++;
                        failReasons.add("班级代码 " + schoolClass.getClassCode() + " 已存在");
                        continue;
                    }

                    // 设置默认值
                    if (schoolClass.getStatus() == null) {
                        schoolClass.setStatus(1);
                    }
                    if (schoolClass.getStudentCount() == null) {
                        schoolClass.setStudentCount(0);
                    }

                    schoolClassService.createClass(schoolClass);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("班级 " + schoolClass.getClassName() + ": " + e.getMessage());
                    log.warn("导入班级{}失败: {}", schoolClass.getClassName(), e.getMessage());
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("totalRequested", classes.size());
            result.put("failReasons", failReasons);

            return success("批量导入班级完成", result);

        } catch (Exception e) {
            log.error("批量导入班级失败: ", e);
            return error("批量导入班级失败: " + e.getMessage());
        }
    }
}
