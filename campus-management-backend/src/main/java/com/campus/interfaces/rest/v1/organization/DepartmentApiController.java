package com.campus.interfaces.rest.v1.organization;

import com.campus.application.service.organization.DepartmentService;
import com.campus.domain.entity.organization.Department;
import com.campus.interfaces.rest.common.BaseController;
import com.campus.shared.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 院系管理API控制器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@RestController
@RequestMapping("/api/v1/departments")
@Tag(name = "院系管理", description = "院系信息的增删改查操作")
public class DepartmentApiController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(DepartmentApiController.class);

    private final DepartmentService departmentService;

    public DepartmentApiController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    /**
     * 分页查询院系列表
     */
    @GetMapping
    @Operation(summary = "分页查询院系列表", description = "支持按院系名称、代码、类型等条件搜索")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<List<Department>>> getDepartments(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "院系名称") @RequestParam(required = false) String deptName,
            @Parameter(description = "院系代码") @RequestParam(required = false) String deptCode,
            @Parameter(description = "院系类型") @RequestParam(required = false) String deptType,
            @Parameter(description = "院系级别") @RequestParam(required = false) Integer deptLevel,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "sortOrder") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "asc") String sortDir) {

        try {
            log.info("查询院系列表 - 页码: {}, 大小: {}, 院系名称: {}, 院系代码: {}", page, size, deptName, deptCode);

            // 验证分页参数
            validatePageParams(page, size);

            // 创建分页对象
            Pageable pageable = createPageable(page, size, sortBy, sortDir);

            // 执行查询
            Page<Department> departmentPage = departmentService.getDepartmentsByConditions(
                deptName, deptCode, deptType, deptLevel, pageable);

            return successPage(departmentPage, "查询院系列表成功");

        } catch (Exception e) {
            log.error("查询院系列表失败: ", e);
            return error("查询院系列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取院系详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取院系详情", description = "根据院系ID获取详细信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Department>> getDepartmentById(
            @Parameter(description = "院系ID") @PathVariable Long id) {

        try {
            log.info("获取院系详情 - ID: {}", id);

            Optional<Department> departmentOpt = departmentService.getDepartmentById(id);
            if (departmentOpt.isPresent()) {
                return success("获取院系详情成功", departmentOpt.get());
            } else {
                return notFound("院系不存在");
            }

        } catch (Exception e) {
            log.error("获取院系详情失败 - ID: {}", id, e);
            return error("获取院系详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建新院系
     */
    @PostMapping
    @Operation(summary = "创建院系", description = "添加新的院系信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Department>> createDepartment(
            @Parameter(description = "院系信息") @Valid @RequestBody Department department) {

        try {
            log.info("创建院系 - 院系名称: {}, 院系代码: {}", department.getDeptName(), department.getDeptCode());

            // 验证院系数据
            validateDepartmentData(department);

            // 检查院系代码是否已存在
            if (departmentService.existsByDeptCode(department.getDeptCode())) {
                return badRequest("院系代码已存在");
            }

            // 检查院系名称是否已存在
            if (departmentService.existsByDeptName(department.getDeptName())) {
                return badRequest("院系名称已存在");
            }

            // 保存院系
            Department savedDepartment = departmentService.createDepartment(department);

            return success("院系创建成功", savedDepartment);

        } catch (Exception e) {
            log.error("创建院系失败: ", e);
            return error("创建院系失败: " + e.getMessage());
        }
    }

    /**
     * 更新院系信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新院系", description = "修改院系信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Department>> updateDepartment(
            @Parameter(description = "院系ID") @PathVariable Long id,
            @Parameter(description = "院系信息") @Valid @RequestBody Department department) {

        try {
            log.info("更新院系 - ID: {}, 院系名称: {}", id, department.getDeptName());

            // 验证院系是否存在
            if (!departmentService.getDepartmentById(id).isPresent()) {
                return notFound("院系不存在");
            }

            // 验证院系数据
            validateDepartmentData(department);

            // 检查院系代码是否已存在（排除当前院系）
            if (departmentService.existsByDeptCodeExcludeId(department.getDeptCode(), id)) {
                return badRequest("院系代码已存在");
            }

            // 检查院系名称是否已存在（排除当前院系）
            if (departmentService.existsByDeptNameExcludeId(department.getDeptName(), id)) {
                return badRequest("院系名称已存在");
            }

            // 更新院系
            Department updatedDepartment = departmentService.updateDepartment(id, department);

            return success("院系更新成功", updatedDepartment);

        } catch (Exception e) {
            log.error("更新院系失败 - ID: {}", id, e);
            return error("更新院系失败: " + e.getMessage());
        }
    }

    /**
     * 删除院系
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除院系", description = "软删除院系信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(
            @Parameter(description = "院系ID") @PathVariable Long id) {

        try {
            log.info("删除院系 - ID: {}", id);

            // 验证院系是否存在
            if (!departmentService.getDepartmentById(id).isPresent()) {
                return notFound("院系不存在");
            }

            // 删除院系
            departmentService.deleteDepartment(id);

            return success("院系删除成功");

        } catch (Exception e) {
            log.error("删除院系失败 - ID: {}", id, e);
            return error("删除院系失败: " + e.getMessage());
        }
    }

    // ==================== 统计端点 ====================

    /**
     * 获取院系统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取院系统计信息", description = "获取院系模块的统计数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDepartmentStats() {
        try {
            log.info("获取院系统计信息");

            Map<String, Object> stats = new HashMap<>();

            // 基础统计
            long totalDepartments = departmentService.countTotalDepartments();
            stats.put("totalDepartments", totalDepartments);

            long activeDepartments = departmentService.countActiveDepartments();
            stats.put("activeDepartments", activeDepartments);

            long inactiveDepartments = totalDepartments - activeDepartments;
            stats.put("inactiveDepartments", inactiveDepartments);

            // 按类型统计（简化实现）
            Map<String, Long> typeStats = new HashMap<>();
            typeStats.put("COLLEGE", 0L);
            typeStats.put("DEPARTMENT", 0L);
            typeStats.put("INSTITUTE", 0L);
            typeStats.put("CENTER", 0L);
            stats.put("typeStats", typeStats);

            // 顶级院系数量
            List<Department> topLevelDepartments = departmentService.getTopLevelDepartments();
            stats.put("topLevelCount", (long) topLevelDepartments.size());

            // 按级别统计（简化实现）
            Map<String, Long> levelStats = new HashMap<>();
            levelStats.put("1", (long) topLevelDepartments.size());
            levelStats.put("2", 0L);
            levelStats.put("3", 0L);
            stats.put("levelStats", levelStats);

            // 时间统计（简化实现）
            stats.put("todayDepartments", 0L);
            stats.put("weekDepartments", 0L);
            stats.put("monthDepartments", 0L);

            // 最近活动（简化实现）
            List<Map<String, Object>> recentActivity = new ArrayList<>();
            stats.put("recentActivity", recentActivity);

            return success("获取院系统计信息成功", stats);

        } catch (Exception e) {
            log.error("获取院系统计信息失败: ", e);
            return error("获取院系统计信息失败: " + e.getMessage());
        }
    }

    // ==================== 批量操作端点 ====================

    /**
     * 批量删除院系
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除院系", description = "根据ID列表批量删除院系")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchDeleteDepartments(
            @Parameter(description = "院系ID列表") @RequestBody List<Long> ids) {

        try {
            logOperation("批量删除院系", ids.size());

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("院系ID列表不能为空");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "院系");
            }

            // 执行批量删除
            departmentService.deleteDepartments(ids);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("deletedCount", ids.size());
            responseData.put("totalRequested", ids.size());

            return success("批量删除院系成功", responseData);

        } catch (Exception e) {
            log.error("批量删除院系失败: ", e);
            return error("批量删除院系失败: " + e.getMessage());
        }
    }

    /**
     * 批量更新院系状态
     */
    @PutMapping("/batch/status")
    @Operation(summary = "批量更新院系状态", description = "批量更新院系的启用/禁用状态")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchUpdateDepartmentStatus(
            @Parameter(description = "批量更新请求") @RequestBody Map<String, Object> request) {

        try {
            @SuppressWarnings("unchecked")
            List<Long> ids = (List<Long>) request.get("ids");
            Integer status = (Integer) request.get("status");

            logOperation("批量更新院系状态", ids.size(), "状态: " + status);

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("院系ID列表不能为空");
            }

            if (status == null || (status != 0 && status != 1)) {
                return badRequest("状态值必须为0（禁用）或1（启用）");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "院系");
            }

            // 执行批量状态更新
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Long id : ids) {
                try {
                    if (status == 1) {
                        departmentService.enableDepartment(id);
                    } else {
                        departmentService.disableDepartment(id);
                    }
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("院系ID " + id + ": " + e.getMessage());
                    log.warn("更新院系{}状态失败: {}", id, e.getMessage());
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", ids.size());
            responseData.put("status", status == 1 ? "启用" : "禁用");
            responseData.put("failReasons", failReasons);

            if (failCount == 0) {
                return success("批量更新院系状态成功", responseData);
            } else if (successCount > 0) {
                return success("批量更新院系状态部分成功", responseData);
            } else {
                return error("批量更新院系状态失败");
            }

        } catch (Exception e) {
            log.error("批量更新院系状态失败: ", e);
            return error("批量更新院系状态失败: " + e.getMessage());
        }
    }

    /**
     * 批量导入院系
     */
    @PostMapping("/batch/import")
    @Operation(summary = "批量导入院系", description = "批量导入院系数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchImportDepartments(
            @Parameter(description = "院系数据列表") @RequestBody List<Department> departments) {

        try {
            logOperation("批量导入院系", departments.size());

            // 验证参数
            if (departments == null || departments.isEmpty()) {
                return badRequest("院系数据列表不能为空");
            }

            if (departments.size() > 100) {
                return badRequest("单次批量导入不能超过100条记录");
            }

            // 执行批量导入
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Department department : departments) {
                try {
                    // 验证院系数据
                    validateDepartmentData(department);

                    // 检查院系代码是否已存在
                    if (departmentService.existsByDeptCode(department.getDeptCode())) {
                        failCount++;
                        failReasons.add("院系代码 " + department.getDeptCode() + " 已存在");
                        continue;
                    }

                    // 检查院系名称是否已存在
                    if (departmentService.existsByDeptName(department.getDeptName())) {
                        failCount++;
                        failReasons.add("院系名称 " + department.getDeptName() + " 已存在");
                        continue;
                    }

                    departmentService.createDepartment(department);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("院系 " + department.getDeptName() + ": " + e.getMessage());
                    log.warn("导入院系{}失败: {}", department.getDeptName(), e.getMessage());
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("totalRequested", departments.size());
            result.put("failReasons", failReasons);

            return success("批量导入院系完成", result);

        } catch (Exception e) {
            log.error("批量导入院系失败: ", e);
            return error("批量导入院系失败: " + e.getMessage());
        }
    }

    /**
     * 启用院系
     */
    @PutMapping("/{id}/enable")
    @Operation(summary = "启用院系", description = "启用指定院系")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> enableDepartment(
            @Parameter(description = "院系ID") @PathVariable Long id) {

        try {
            log.info("启用院系 - ID: {}", id);

            // 验证院系是否存在
            if (!departmentService.getDepartmentById(id).isPresent()) {
                return notFound("院系不存在");
            }

            // 启用院系
            departmentService.enableDepartment(id);

            return success("院系启用成功");

        } catch (Exception e) {
            log.error("启用院系失败 - ID: {}", id, e);
            return error("启用院系失败: " + e.getMessage());
        }
    }

    /**
     * 禁用院系
     */
    @PutMapping("/{id}/disable")
    @Operation(summary = "禁用院系", description = "禁用指定院系")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> disableDepartment(
            @Parameter(description = "院系ID") @PathVariable Long id) {

        try {
            log.info("禁用院系 - ID: {}", id);

            // 验证院系是否存在
            if (!departmentService.getDepartmentById(id).isPresent()) {
                return notFound("院系不存在");
            }

            // 禁用院系
            departmentService.disableDepartment(id);

            return success("院系禁用成功");

        } catch (Exception e) {
            log.error("禁用院系失败 - ID: {}", id, e);
            return error("禁用院系失败: " + e.getMessage());
        }
    }

    /**
     * 获取院系树结构
     */
    @GetMapping("/tree")
    @Operation(summary = "获取院系树结构", description = "获取院系的层级树结构")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<List<Department>>> getDepartmentTree() {

        try {
            log.info("获取院系树结构");

            List<Department> departmentTree = departmentService.getDepartmentTree();

            return success("获取院系树结构成功", departmentTree);

        } catch (Exception e) {
            log.error("获取院系树结构失败: ", e);
            return error("获取院系树结构失败: " + e.getMessage());
        }
    }

    /**
     * 获取顶级院系
     */
    @GetMapping("/top-level")
    @Operation(summary = "获取顶级院系", description = "获取所有顶级院系")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<List<Department>>> getTopLevelDepartments() {

        try {
            log.info("获取顶级院系");

            List<Department> topLevelDepartments = departmentService.getTopLevelDepartments();

            return success("获取顶级院系成功", topLevelDepartments);

        } catch (Exception e) {
            log.error("获取顶级院系失败: ", e);
            return error("获取顶级院系失败: " + e.getMessage());
        }
    }

    /**
     * 获取子院系
     */
    @GetMapping("/{parentId}/children")
    @Operation(summary = "获取子院系", description = "获取指定院系的子院系")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<List<Department>>> getChildDepartments(
            @Parameter(description = "父院系ID") @PathVariable Long parentId) {

        try {
            log.info("获取子院系 - 父院系ID: {}", parentId);

            List<Department> childDepartments = departmentService.getChildDepartments(parentId);

            return success("获取子院系成功", childDepartments);

        } catch (Exception e) {
            log.error("获取子院系失败 - 父院系ID: {}", parentId, e);
            return error("获取子院系失败: " + e.getMessage());
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 验证院系数据
     */
    private void validateDepartmentData(Department department) {
        if (department.getDeptName() == null || department.getDeptName().trim().isEmpty()) {
            throw new IllegalArgumentException("院系名称不能为空");
        }
        if (department.getDeptCode() == null || department.getDeptCode().trim().isEmpty()) {
            throw new IllegalArgumentException("院系代码不能为空");
        }
        if (department.getDeptType() == null || department.getDeptType().trim().isEmpty()) {
            throw new IllegalArgumentException("院系类型不能为空");
        }
    }
}
