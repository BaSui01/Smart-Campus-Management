package com.campus.interfaces.rest.v1;

import com.campus.application.service.CourseSelectionService;
import com.campus.domain.entity.CourseSelection;
import com.campus.shared.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 选课管理API控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@RestController
@RequestMapping("/api/v1/course-selections")
@Tag(name = "选课管理", description = "选课管理相关API")
public class CourseSelectionApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseSelectionApiController.class);
    
    @Autowired
    private CourseSelectionService courseSelectionService;
    
    @PostMapping
    @Operation(summary = "学生选课", description = "学生选择课程")
    public ResponseEntity<ApiResponse<CourseSelection>> selectCourse(@Valid @RequestBody CourseSelection courseSelection) {
        try {
            CourseSelection created = courseSelectionService.selectCourse(courseSelection);
            return ResponseEntity.ok(ApiResponse.success(created, "选课成功"));
        } catch (Exception e) {
            logger.error("选课失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("选课失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取选课记录详情", description = "根据ID获取选课记录详细信息")
    public ResponseEntity<ApiResponse<CourseSelection>> getCourseSelection(
            @Parameter(description = "选课记录ID") @PathVariable Long id) {
        try {
            Optional<CourseSelection> courseSelection = courseSelectionService.findCourseSelectionById(id);
            if (courseSelection.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(courseSelection.get()));
            } else {
                return ResponseEntity.ok(ApiResponse.error("选课记录不存在"));
            }
        } catch (Exception e) {
            logger.error("获取选课记录详情失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取选课记录详情失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "退课", description = "学生退选课程")
    public ResponseEntity<ApiResponse<Void>> dropCourse(
            @Parameter(description = "选课记录ID") @PathVariable Long id) {
        try {
            courseSelectionService.dropCourse(id);
            return ResponseEntity.ok(ApiResponse.success(null, "退课成功"));
        } catch (Exception e) {
            logger.error("退课失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("退课失败: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "获取选课记录列表", description = "分页获取选课记录列表")
    public ResponseEntity<ApiResponse<Page<CourseSelection>>> getCourseSelections(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CourseSelection> courseSelections = courseSelectionService.findAllCourseSelections(pageable);
            return ResponseEntity.ok(ApiResponse.success(courseSelections));
        } catch (Exception e) {
            logger.error("获取选课记录列表失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取选课记录列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/student/{studentId}")
    @Operation(summary = "获取学生选课记录", description = "获取指定学生的所有选课记录")
    public ResponseEntity<ApiResponse<List<CourseSelection>>> getStudentCourseSelections(
            @Parameter(description = "学生ID") @PathVariable Long studentId) {
        try {
            List<CourseSelection> courseSelections = courseSelectionService.findSelectionsByStudent(studentId);
            return ResponseEntity.ok(ApiResponse.success(courseSelections));
        } catch (Exception e) {
            logger.error("获取学生选课记录失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取学生选课记录失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/course/{courseId}")
    @Operation(summary = "获取课程选课记录", description = "获取指定课程的所有选课记录")
    public ResponseEntity<ApiResponse<List<CourseSelection>>> getCourseCourseSelections(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        try {
            List<CourseSelection> courseSelections = courseSelectionService.findSelectionsByCourse(courseId);
            return ResponseEntity.ok(ApiResponse.success(courseSelections));
        } catch (Exception e) {
            logger.error("获取课程选课记录失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取课程选课记录失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/semester/{semester}")
    @Operation(summary = "按学期获取选课记录", description = "获取指定学期的选课记录")
    public ResponseEntity<ApiResponse<Page<CourseSelection>>> getCourseSelectionsBySemester(
            @Parameter(description = "学期") @PathVariable String semester,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CourseSelection> courseSelections = courseSelectionService.findSelectionsBySemester(semester, pageable);
            return ResponseEntity.ok(ApiResponse.success(courseSelections));
        } catch (Exception e) {
            logger.error("按学期获取选课记录失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("按学期获取选课记录失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "按状态获取选课记录", description = "获取指定状态的选课记录")
    public ResponseEntity<ApiResponse<Page<CourseSelection>>> getCourseSelectionsByStatus(
            @Parameter(description = "选课状态") @PathVariable String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CourseSelection> courseSelections = courseSelectionService.findSelectionsByStatus(status, pageable);
            return ResponseEntity.ok(ApiResponse.success(courseSelections));
        } catch (Exception e) {
            logger.error("按状态获取选课记录失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("按状态获取选课记录失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/check-eligibility")
    @Operation(summary = "检查选课资格", description = "检查学生是否有资格选择指定课程")
    public ResponseEntity<ApiResponse<Boolean>> checkCourseSelectionEligibility(
            @Parameter(description = "学生ID") @RequestParam Long studentId,
            @Parameter(description = "课程ID") @RequestParam Long courseId) {
        try {
            boolean eligible = courseSelectionService.checkSelectionEligibility(studentId, courseId);
            return ResponseEntity.ok(ApiResponse.success(eligible));
        } catch (Exception e) {
            logger.error("检查选课资格失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("检查选课资格失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/check-conflicts")
    @Operation(summary = "检查选课冲突", description = "检查学生选课是否存在时间冲突")
    public ResponseEntity<ApiResponse<List<CourseSelection>>> checkCourseSelectionConflicts(
            @Parameter(description = "学生ID") @RequestParam Long studentId,
            @Parameter(description = "课程ID") @RequestParam Long courseId) {
        try {
            List<CourseSelection> conflicts = courseSelectionService.checkSelectionConflicts(studentId, courseId);
            return ResponseEntity.ok(ApiResponse.success(conflicts));
        } catch (Exception e) {
            logger.error("检查选课冲突失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("检查选课冲突失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/available-courses/{studentId}")
    @Operation(summary = "获取可选课程", description = "获取学生可以选择的课程列表")
    public ResponseEntity<ApiResponse<List<Object>>> getAvailableCoursesForStudent(
            @Parameter(description = "学生ID") @PathVariable Long studentId) {
        try {
            List<Object> availableCourses = courseSelectionService.getAvailableCoursesForStudent(studentId);
            return ResponseEntity.ok(ApiResponse.success(availableCourses));
        } catch (Exception e) {
            logger.error("获取可选课程失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取可选课程失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/confirm")
    @Operation(summary = "确认选课", description = "确认学生的选课申请")
    public ResponseEntity<ApiResponse<Void>> confirmCourseSelection(
            @Parameter(description = "选课记录ID") @PathVariable Long id) {
        try {
            courseSelectionService.confirmSelection(id);
            return ResponseEntity.ok(ApiResponse.success(null, "选课确认成功"));
        } catch (Exception e) {
            logger.error("确认选课失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("确认选课失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/reject")
    @Operation(summary = "拒绝选课", description = "拒绝学生的选课申请")
    public ResponseEntity<ApiResponse<Void>> rejectCourseSelection(
            @Parameter(description = "选课记录ID") @PathVariable Long id,
            @Parameter(description = "拒绝原因") @RequestParam(required = false) String reason) {
        try {
            courseSelectionService.rejectSelection(id, reason);
            return ResponseEntity.ok(ApiResponse.success(null, "选课拒绝成功"));
        } catch (Exception e) {
            logger.error("拒绝选课失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("拒绝选课失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/batch-select")
    @Operation(summary = "批量选课", description = "学生批量选择多门课程")
    public ResponseEntity<ApiResponse<List<CourseSelection>>> batchSelectCourses(
            @RequestBody BatchCourseSelectionRequest request) {
        try {
            List<CourseSelection> selections = courseSelectionService.batchSelectCourses(
                request.getStudentId(), request.getCourseIds());
            return ResponseEntity.ok(ApiResponse.success(selections, "批量选课成功"));
        } catch (Exception e) {
            logger.error("批量选课失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("批量选课失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "获取选课统计", description = "获取选课相关统计信息")
    public ResponseEntity<ApiResponse<Object>> getCourseSelectionStatistics() {
        try {
            long totalSelections = courseSelectionService.countTotalSelections();
            long confirmedSelections = courseSelectionService.countConfirmedSelections();
            long pendingSelections = courseSelectionService.countPendingSelections();
            List<Object[]> courseStats = courseSelectionService.getPopularCourses();
            
            Object statistics = new Object() {
                public final long total = totalSelections;
                public final long confirmed = confirmedSelections;
                public final long pending = pendingSelections;
                public final List<Object[]> popularCourses = courseStats;
            };
            
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            logger.error("获取选课统计失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取选课统计失败: " + e.getMessage()));
        }
    }
    
    /**
     * 批量选课请求对象
     */
    public static class BatchCourseSelectionRequest {
        private Long studentId;
        private List<Long> courseIds;
        
        // Getters and Setters
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public List<Long> getCourseIds() { return courseIds; }
        public void setCourseIds(List<Long> courseIds) { this.courseIds = courseIds; }
    }
}
