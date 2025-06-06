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
@RequestMapping("/api/classes")
@Tag(name = "班级管理API", description = "班级信息管理REST API接口")
@SecurityRequirement(name = "Bearer")
public class ClassApiController {

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

    /**
     * 统计班级数量按年级
     */
    @GetMapping("/stats/grade")
    @Operation(summary = "统计班级数量按年级", description = "按年级统计班级数量")
    public ApiResponse<List<Object[]>> countClassesByGrade() {
        List<Object[]> stats = schoolClassService.countClassesByGrade();
        return ApiResponse.success(stats);
    }
}
