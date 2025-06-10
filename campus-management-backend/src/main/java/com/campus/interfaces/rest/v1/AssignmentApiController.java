package com.campus.interfaces.rest.v1;

import com.campus.application.service.AssignmentService;
import com.campus.interfaces.rest.common.BaseController;
import com.campus.domain.entity.Assignment;
import com.campus.shared.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 作业管理API控制器
 * 提供作业的增删改查、发布、提交等功能
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@RestController
@RequestMapping("/api/v1/assignments")
@Tag(name = "作业管理", description = "作业信息的增删改查、发布、提交等操作")
public class AssignmentApiController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(AssignmentApiController.class);

    @Autowired
    private AssignmentService assignmentService;

    // ==================== 基础CRUD操作 ====================

    /**
     * 分页查询作业列表
     */
    @GetMapping
    @Operation(summary = "分页查询作业列表", description = "支持按条件搜索和分页查询作业")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<Assignment>>> getAssignments(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "作业标题") @RequestParam(required = false) String title,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "教师ID") @RequestParam(required = false) Long teacherId,
            @Parameter(description = "作业类型") @RequestParam(required = false) String assignmentType,
            @Parameter(description = "是否已发布") @RequestParam(required = false) Boolean isPublished,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String sortDir,
            HttpServletRequest request) {

        try {
            log.info("查询作业列表 - 页码: {}, 大小: {}, 标题: {}, 课程ID: {}, 教师ID: {}", 
                page, size, title, courseId, teacherId);

            // 验证分页参数
            validatePageParams(page, size);

            // 创建分页对象
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page - 1, size, sort);

            // 查询数据
            Page<Assignment> result = assignmentService.findByConditions(
                title, courseId, teacherId, assignmentType, isPublished, pageable);

            return successPage(result, "查询作业列表成功");

        } catch (Exception e) {
            log.error("查询作业列表失败: ", e);
            return error("查询作业列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询作业详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询作业详情", description = "根据ID查询作业的详细信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Assignment>> getAssignment(
            @Parameter(description = "作业ID", required = true) @PathVariable Long id) {

        try {
            log.info("查询作业详情 - ID: {}", id);

            validateId(id, "作业");

            Optional<Assignment> assignment = assignmentService.findById(id);
            if (assignment.isPresent()) {
                return success("查询作业详情成功", assignment.get());
            } else {
                return error("作业不存在");
            }

        } catch (Exception e) {
            log.error("查询作业详情失败 - ID: {}", id, e);
            return error("查询作业详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建作业
     */
    @PostMapping
    @Operation(summary = "创建作业", description = "创建新的作业")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Assignment>> createAssignment(
            @RequestBody Assignment assignment,
            HttpServletRequest request) {

        try {
            log.info("创建作业 - 标题: {}, 课程ID: {}", assignment.getTitle(), assignment.getCourseId());

            // 验证作业数据
            validateAssignment(assignment);

            // 检查标题是否重复
            if (assignmentService.existsByTitle(assignment.getTitle())) {
                return error("作业标题已存在");
            }

            // 设置默认值
            assignment.setIsPublished(false);
            assignment.setTotalSubmissions(0);
            assignment.setGradedCount(0);

            Assignment savedAssignment = assignmentService.save(assignment);
            return success("作业创建成功", savedAssignment);

        } catch (Exception e) {
            log.error("创建作业失败: ", e);
            return error("创建作业失败: " + e.getMessage());
        }
    }

    /**
     * 更新作业
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新作业", description = "更新作业信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Assignment>> updateAssignment(
            @Parameter(description = "作业ID", required = true) @PathVariable Long id,
            @RequestBody Assignment assignment,
            HttpServletRequest request) {

        try {
            log.info("更新作业 - ID: {}, 标题: {}", id, assignment.getTitle());

            validateId(id, "作业");

            // 检查作业是否存在
            Optional<Assignment> existingOpt = assignmentService.findById(id);
            if (!existingOpt.isPresent()) {
                return error("作业不存在");
            }

            Assignment existing = existingOpt.get();

            // 检查是否可以修改
            if (!assignmentService.canModifyAssignment(id)) {
                return error("作业已发布且有提交记录，无法修改");
            }

            // 验证作业数据
            validateAssignment(assignment);

            // 检查标题是否重复（排除当前记录）
            if (assignmentService.existsByTitleExcludeId(assignment.getTitle(), id)) {
                return error("作业标题已存在");
            }

            // 更新字段
            existing.setTitle(assignment.getTitle());
            existing.setDescription(assignment.getDescription());
            existing.setRequirements(assignment.getRequirements());
            existing.setAssignmentType(assignment.getAssignmentType());
            existing.setDifficultyLevel(assignment.getDifficultyLevel());
            existing.setEstimatedHours(assignment.getEstimatedHours());
            existing.setMaxScore(assignment.getMaxScore());
            existing.setWeight(assignment.getWeight());
            existing.setDueDate(assignment.getDueDate());
            existing.setLateSubmissionAllowed(assignment.getLateSubmissionAllowed());
            existing.setLatePenaltyRate(assignment.getLatePenaltyRate());
            existing.setSubmissionFormat(assignment.getSubmissionFormat());
            existing.setIsGroupAssignment(assignment.getIsGroupAssignment());
            existing.setMaxGroupSize(assignment.getMaxGroupSize());
            existing.setAutoGrade(assignment.getAutoGrade());

            Assignment updatedAssignment = assignmentService.save(existing);
            return success("作业更新成功", updatedAssignment);

        } catch (Exception e) {
            log.error("更新作业失败 - ID: {}", id, e);
            return error("更新作业失败: " + e.getMessage());
        }
    }

    /**
     * 删除作业
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除作业", description = "删除指定的作业")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAssignment(
            @Parameter(description = "作业ID", required = true) @PathVariable Long id) {

        try {
            log.info("删除作业 - ID: {}", id);

            validateId(id, "作业");

            // 检查作业是否存在
            Optional<Assignment> assignment = assignmentService.findById(id);
            if (!assignment.isPresent()) {
                return error("作业不存在");
            }

            // 检查是否可以删除
            if (!assignmentService.canDeleteAssignment(id)) {
                return error("作业已有提交记录，无法删除");
            }

            assignmentService.deleteById(id);
            return success("作业删除成功");

        } catch (Exception e) {
            log.error("删除作业失败 - ID: {}", id, e);
            return error("删除作业失败: " + e.getMessage());
        }
    }

    /**
     * 批量删除作业
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除作业", description = "批量删除多个作业")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAssignments(
            @RequestBody List<Long> ids) {

        try {
            log.info("批量删除作业 - IDs: {}", ids);

            if (ids == null || ids.isEmpty()) {
                return error("请选择要删除的作业");
            }

            // 检查是否都可以删除
            for (Long id : ids) {
                if (!assignmentService.canDeleteAssignment(id)) {
                    return error("作业ID " + id + " 已有提交记录，无法删除");
                }
            }

            assignmentService.deleteByIds(ids);
            return success("批量删除作业成功");

        } catch (Exception e) {
            log.error("批量删除作业失败: ", e);
            return error("批量删除作业失败: " + e.getMessage());
        }
    }

    // ==================== 业务操作 ====================

    /**
     * 发布作业
     */
    @PostMapping("/{id}/publish")
    @Operation(summary = "发布作业", description = "发布指定的作业")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> publishAssignment(
            @Parameter(description = "作业ID", required = true) @PathVariable Long id) {

        try {
            log.info("发布作业 - ID: {}", id);

            validateId(id, "作业");

            // 检查作业是否存在
            Optional<Assignment> assignment = assignmentService.findById(id);
            if (!assignment.isPresent()) {
                return error("作业不存在");
            }

            assignmentService.publishAssignment(id);
            return success("作业发布成功");

        } catch (Exception e) {
            log.error("发布作业失败 - ID: {}", id, e);
            return error("发布作业失败: " + e.getMessage());
        }
    }

    /**
     * 取消发布作业
     */
    @PostMapping("/{id}/unpublish")
    @Operation(summary = "取消发布作业", description = "取消发布指定的作业")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> unpublishAssignment(
            @Parameter(description = "作业ID", required = true) @PathVariable Long id) {

        try {
            log.info("取消发布作业 - ID: {}", id);

            validateId(id, "作业");

            // 检查作业是否存在
            Optional<Assignment> assignment = assignmentService.findById(id);
            if (!assignment.isPresent()) {
                return error("作业不存在");
            }

            assignmentService.unpublishAssignment(id);
            return success("取消发布作业成功");

        } catch (Exception e) {
            log.error("取消发布作业失败 - ID: {}", id, e);
            return error("取消发布作业失败: " + e.getMessage());
        }
    }

    /**
     * 延长作业截止时间
     */
    @PostMapping("/{id}/extend")
    @Operation(summary = "延长作业截止时间", description = "延长指定作业的截止时间")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> extendDueDate(
            @Parameter(description = "作业ID", required = true) @PathVariable Long id,
            @Parameter(description = "新的截止时间", required = true) @RequestParam String newDueDate) {

        try {
            log.info("延长作业截止时间 - ID: {}, 新截止时间: {}", id, newDueDate);

            validateId(id, "作业");

            // 检查作业是否存在
            Optional<Assignment> assignment = assignmentService.findById(id);
            if (!assignment.isPresent()) {
                return error("作业不存在");
            }

            LocalDateTime dueDate = LocalDateTime.parse(newDueDate);
            assignmentService.extendDueDate(id, dueDate);
            return success("延长截止时间成功");

        } catch (Exception e) {
            log.error("延长作业截止时间失败 - ID: {}", id, e);
            return error("延长作业截止时间失败: " + e.getMessage());
        }
    }

    // ==================== 统计端点 ====================

    /**
     * 获取作业统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取作业统计信息", description = "获取作业模块的统计数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAssignmentStats() {
        try {
            log.info("获取作业统计信息");

            Map<String, Object> stats = new HashMap<>();

            // 基础统计
            long totalAssignments = assignmentService.count();
            stats.put("totalAssignments", totalAssignments);

            // 简化统计实现
            stats.put("publishedAssignments", 0L);
            stats.put("draftAssignments", 0L);

            // 按作业类型统计（简化）
            Map<String, Long> typeStats = new HashMap<>();
            typeStats.put("HOMEWORK", 0L);
            typeStats.put("PROJECT", 0L);
            typeStats.put("REPORT", 0L);
            typeStats.put("PRESENTATION", 0L);
            stats.put("typeStats", typeStats);

            // 按难度级别统计（简化）
            Map<String, Long> difficultyStats = new HashMap<>();
            difficultyStats.put("EASY", 0L);
            difficultyStats.put("MEDIUM", 0L);
            difficultyStats.put("HARD", 0L);
            stats.put("difficultyStats", difficultyStats);

            // 今日、本周、本月统计（简化）
            stats.put("todayAssignments", 0L);
            stats.put("weekAssignments", 0L);
            stats.put("monthAssignments", 0L);

            // 提交统计（简化）
            Map<String, Object> submissionStats = new HashMap<>();
            submissionStats.put("totalSubmissions", 0);
            submissionStats.put("pendingSubmissions", 0);
            submissionStats.put("submissionRate", 0.0);
            stats.put("submissionStats", submissionStats);

            // 评分统计（简化）
            Map<String, Object> gradingStats = new HashMap<>();
            gradingStats.put("totalGraded", 0);
            gradingStats.put("pendingGrading", 0);
            gradingStats.put("averageScore", 0.0);
            stats.put("gradingStats", gradingStats);

            // 最近活动（简化）
            List<Map<String, Object>> recentActivity = new ArrayList<>();
            stats.put("recentActivity", recentActivity);

            return success("获取作业统计信息成功", stats);

        } catch (Exception e) {
            log.error("获取作业统计信息失败: ", e);
            return error("获取作业统计信息失败: " + e.getMessage());
        }
    }

    // ==================== 批量操作端点 ====================

    /**
     * 批量发布作业
     */
    @PutMapping("/batch/publish")
    @Operation(summary = "批量发布作业", description = "批量发布作业")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchPublishAssignments(
            @Parameter(description = "作业ID列表") @RequestBody List<Long> ids) {

        try {
            logOperation("批量发布作业", ids.size());

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("作业ID列表不能为空");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "作业");
            }

            // 执行批量发布
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Long id : ids) {
                try {
                    assignmentService.publishAssignment(id);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("作业ID " + id + ": " + e.getMessage());
                    log.warn("发布作业{}失败: {}", id, e.getMessage());
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", ids.size());
            responseData.put("failReasons", failReasons);

            if (failCount == 0) {
                return success("批量发布作业成功", responseData);
            } else if (successCount > 0) {
                return success("批量发布作业部分成功", responseData);
            } else {
                return error("批量发布作业失败");
            }

        } catch (Exception e) {
            log.error("批量发布作业失败: ", e);
            return error("批量发布作业失败: " + e.getMessage());
        }
    }

    /**
     * 批量导入作业
     */
    @PostMapping("/batch/import")
    @Operation(summary = "批量导入作业", description = "批量导入作业数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchImportAssignments(
            @Parameter(description = "作业数据列表") @RequestBody List<Assignment> assignments) {

        try {
            logOperation("批量导入作业", assignments.size());

            // 验证参数
            if (assignments == null || assignments.isEmpty()) {
                return badRequest("作业数据列表不能为空");
            }

            if (assignments.size() > 100) {
                return badRequest("单次批量导入不能超过100条记录");
            }

            // 执行批量导入（简化实现）
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Assignment assignment : assignments) {
                try {
                    // 验证作业数据
                    validateAssignment(assignment);

                    // 设置默认值
                    assignment.setIsPublished(false);
                    assignment.setTotalSubmissions(0);
                    assignment.setGradedCount(0);

                    assignmentService.save(assignment);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("作业 " + assignment.getTitle() + ": " + e.getMessage());
                    log.warn("导入作业{}失败: {}", assignment.getTitle(), e.getMessage());
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("totalRequested", assignments.size());
            result.put("failReasons", failReasons);

            return success("批量导入作业完成", result);

        } catch (Exception e) {
            log.error("批量导入作业失败: ", e);
            return error("批量导入作业失败: " + e.getMessage());
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 验证作业数据
     */
    private void validateAssignment(Assignment assignment) {
        if (assignment.getTitle() == null || assignment.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("作业标题不能为空");
        }
        if (assignment.getCourseId() == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        if (assignment.getTeacherId() == null) {
            throw new IllegalArgumentException("教师ID不能为空");
        }
        if (assignment.getMaxScore() == null || assignment.getMaxScore() <= 0) {
            throw new IllegalArgumentException("满分必须大于0");
        }
        if (assignment.getDueDate() == null) {
            throw new IllegalArgumentException("截止时间不能为空");
        }
        if (assignment.getDueDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("截止时间不能早于当前时间");
        }
    }
}
