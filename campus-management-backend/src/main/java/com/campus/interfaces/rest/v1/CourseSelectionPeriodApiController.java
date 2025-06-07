package com.campus.interfaces.rest.v1;

import com.campus.application.service.CourseSelectionPeriodService;
import com.campus.domain.entity.CourseSelectionPeriod;
import com.campus.interfaces.rest.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 选课时间段管理API控制器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@RestController
@RequestMapping("/api/v1/course-selection-periods")
@Tag(name = "选课时间段管理", description = "选课时间段的增删改查和业务操作")
public class CourseSelectionPeriodApiController {

    private static final Logger log = LoggerFactory.getLogger(CourseSelectionPeriodApiController.class);

    @Autowired
    private CourseSelectionPeriodService periodService;

    @Operation(summary = "分页查询选课时间段", description = "支持按学期、学年、选课类型筛选")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Page<CourseSelectionPeriod>>> getPeriodsWithFilters(
            @Parameter(description = "学期") @RequestParam(required = false) String semester,
            @Parameter(description = "学年") @RequestParam(required = false) Integer academicYear,
            @Parameter(description = "选课类型") @RequestParam(required = false) String selectionType,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CourseSelectionPeriod> periods = periodService.findWithFilters(semester, academicYear, selectionType, pageable);
            return ResponseEntity.ok(ApiResponse.success(periods));
        } catch (Exception e) {
            log.error("查询选课时间段失败", e);
            return ResponseEntity.ok(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "根据ID查询选课时间段")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<CourseSelectionPeriod>> getPeriodById(
            @Parameter(description = "选课时间段ID") @PathVariable Long id) {
        
        try {
            Optional<CourseSelectionPeriod> period = periodService.findById(id);
            if (period.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(period.get()));
            } else {
                return ResponseEntity.ok(ApiResponse.error("选课时间段不存在"));
            }
        } catch (Exception e) {
            log.error("查询选课时间段失败: {}", id, e);
            return ResponseEntity.ok(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "创建选课时间段")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseSelectionPeriod>> createPeriod(
            @Parameter(description = "选课时间段信息") @Valid @RequestBody CourseSelectionPeriod period) {
        
        try {
            CourseSelectionPeriod createdPeriod = periodService.createPeriod(period);
            return ResponseEntity.ok(ApiResponse.success(createdPeriod, "选课时间段创建成功"));
        } catch (Exception e) {
            log.error("创建选课时间段失败", e);
            return ResponseEntity.ok(ApiResponse.error("创建失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "更新选课时间段")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseSelectionPeriod>> updatePeriod(
            @Parameter(description = "选课时间段ID") @PathVariable Long id,
            @Parameter(description = "选课时间段信息") @Valid @RequestBody CourseSelectionPeriod period) {
        
        try {
            period.setId(id);
            CourseSelectionPeriod updatedPeriod = periodService.updatePeriod(period);
            return ResponseEntity.ok(ApiResponse.success(updatedPeriod, "选课时间段更新成功"));
        } catch (Exception e) {
            log.error("更新选课时间段失败: {}", id, e);
            return ResponseEntity.ok(ApiResponse.error("更新失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "删除选课时间段")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePeriod(
            @Parameter(description = "选课时间段ID") @PathVariable Long id) {
        
        try {
            periodService.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success(null, "选课时间段删除成功"));
        } catch (Exception e) {
            log.error("删除选课时间段失败: {}", id, e);
            return ResponseEntity.ok(ApiResponse.error("删除失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "获取当前开放的选课时间段")
    @GetMapping("/current-open")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<CourseSelectionPeriod>>> getCurrentOpenPeriods() {
        
        try {
            List<CourseSelectionPeriod> periods = periodService.getCurrentOpenPeriods();
            return ResponseEntity.ok(ApiResponse.success(periods));
        } catch (Exception e) {
            log.error("获取当前开放选课时间段失败", e);
            return ResponseEntity.ok(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "获取学生可用的选课时间段")
    @GetMapping("/available-for-student")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<CourseSelectionPeriod>>> getAvailablePeriodsForStudent(
            @Parameter(description = "年级") @RequestParam String grade,
            @Parameter(description = "专业") @RequestParam String major) {
        
        try {
            List<CourseSelectionPeriod> periods = periodService.getAvailablePeriodsForStudent(grade, major);
            return ResponseEntity.ok(ApiResponse.success(periods));
        } catch (Exception e) {
            log.error("获取学生可用选课时间段失败", e);
            return ResponseEntity.ok(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "获取学生当前可以选课的时间段")
    @GetMapping("/current-open-for-student")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<CourseSelectionPeriod>>> getCurrentOpenPeriodsForStudent(
            @Parameter(description = "年级") @RequestParam String grade,
            @Parameter(description = "专业") @RequestParam String major) {
        
        try {
            List<CourseSelectionPeriod> periods = periodService.getCurrentOpenPeriodsForStudent(grade, major);
            return ResponseEntity.ok(ApiResponse.success(periods));
        } catch (Exception e) {
            log.error("获取学生当前可选课时间段失败", e);
            return ResponseEntity.ok(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "启用选课时间段")
    @PostMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> enablePeriod(
            @Parameter(description = "选课时间段ID") @PathVariable Long id) {
        
        try {
            periodService.enablePeriod(id);
            return ResponseEntity.ok(ApiResponse.success(null, "选课时间段已启用"));
        } catch (Exception e) {
            log.error("启用选课时间段失败: {}", id, e);
            return ResponseEntity.ok(ApiResponse.error("启用失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "禁用选课时间段")
    @PostMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> disablePeriod(
            @Parameter(description = "选课时间段ID") @PathVariable Long id) {
        
        try {
            periodService.disablePeriod(id);
            return ResponseEntity.ok(ApiResponse.success(null, "选课时间段已禁用"));
        } catch (Exception e) {
            log.error("禁用选课时间段失败: {}", id, e);
            return ResponseEntity.ok(ApiResponse.error("禁用失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "检查学生是否可以选课")
    @GetMapping("/{id}/can-select")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<Boolean>> canStudentSelectCourse(
            @Parameter(description = "选课时间段ID") @PathVariable Long id,
            @Parameter(description = "年级") @RequestParam String grade,
            @Parameter(description = "专业") @RequestParam String major) {
        
        try {
            boolean canSelect = periodService.canStudentSelectCourse(id, grade, major);
            return ResponseEntity.ok(ApiResponse.success(canSelect));
        } catch (Exception e) {
            log.error("检查学生选课权限失败: {}", id, e);
            return ResponseEntity.ok(ApiResponse.error("检查失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "检查学生是否可以退课")
    @GetMapping("/{id}/can-drop")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<Boolean>> canStudentDropCourse(
            @Parameter(description = "选课时间段ID") @PathVariable Long id) {
        
        try {
            boolean canDrop = periodService.canStudentDropCourse(id);
            return ResponseEntity.ok(ApiResponse.success(canDrop));
        } catch (Exception e) {
            log.error("检查学生退课权限失败: {}", id, e);
            return ResponseEntity.ok(ApiResponse.error("检查失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "获取选课时间段统计信息")
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<CourseSelectionPeriodService.PeriodStatistics>> getStatistics() {
        
        try {
            CourseSelectionPeriodService.PeriodStatistics statistics = periodService.getStatistics();
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            log.error("获取选课时间段统计信息失败", e);
            return ResponseEntity.ok(ApiResponse.error("查询失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "批量创建选课时间段")
    @PostMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CourseSelectionPeriod>>> batchCreatePeriods(
            @Parameter(description = "选课时间段列表") @Valid @RequestBody List<CourseSelectionPeriod> periods) {
        
        try {
            List<CourseSelectionPeriod> createdPeriods = periodService.batchCreatePeriods(periods);
            return ResponseEntity.ok(ApiResponse.success(createdPeriods, "批量创建成功"));
        } catch (Exception e) {
            log.error("批量创建选课时间段失败", e);
            return ResponseEntity.ok(ApiResponse.error("批量创建失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "复制选课时间段到新学期")
    @PostMapping("/copy")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CourseSelectionPeriod>>> copyPeriodsToNewSemester(
            @Parameter(description = "源学期") @RequestParam String fromSemester,
            @Parameter(description = "源学年") @RequestParam Integer fromYear,
            @Parameter(description = "目标学期") @RequestParam String toSemester,
            @Parameter(description = "目标学年") @RequestParam Integer toYear) {
        
        try {
            List<CourseSelectionPeriod> copiedPeriods = periodService.copyPeriodsToNewSemester(
                fromSemester, fromYear, toSemester, toYear);
            return ResponseEntity.ok(ApiResponse.success(copiedPeriods, "复制成功"));
        } catch (Exception e) {
            log.error("复制选课时间段失败", e);
            return ResponseEntity.ok(ApiResponse.error("复制失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "自动关闭过期的选课时间段")
    @PostMapping("/auto-close-expired")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Integer>> autoCloseExpiredPeriods() {
        
        try {
            int closedCount = periodService.autoCloseExpiredPeriods();
            return ResponseEntity.ok(ApiResponse.success(closedCount, "已关闭 " + closedCount + " 个过期时间段"));
        } catch (Exception e) {
            log.error("自动关闭过期选课时间段失败", e);
            return ResponseEntity.ok(ApiResponse.error("操作失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "发送选课提醒通知")
    @PostMapping("/send-reminders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> sendSelectionReminders() {
        
        try {
            periodService.sendSelectionReminders();
            return ResponseEntity.ok(ApiResponse.success(null, "选课提醒发送成功"));
        } catch (Exception e) {
            log.error("发送选课提醒失败", e);
            return ResponseEntity.ok(ApiResponse.error("发送失败: " + e.getMessage()));
        }
    }
}
