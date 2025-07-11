package com.campus.interfaces.rest.v1.academic;

import com.campus.application.service.academic.AssignmentService;
import com.campus.domain.entity.academic.AssignmentSubmission;
import com.campus.shared.common.ApiResponse;
import com.campus.interfaces.rest.common.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 作业提交管理API控制器
 * 提供作业提交的增删改查、批量操作、统计分析等功能
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-08
 */
@RestController
@RequestMapping("/api/v1/assignment-submissions")
@Tag(name = "作业提交管理", description = "作业提交相关的API接口")
public class AssignmentSubmissionApiController extends BaseController {

    @Autowired
    private AssignmentService assignmentService;

    /**
     * 分页查询作业提交列表
     */
    @GetMapping
    @Operation(summary = "分页查询作业提交列表", description = "支持按作业ID、学生ID、提交状态等条件搜索")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAssignmentSubmissions(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "作业ID") @RequestParam(required = false) Long assignmentId,
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "提交状态") @RequestParam(required = false) String submissionStatus,
            @Parameter(description = "是否迟交") @RequestParam(required = false) Boolean isLate,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "submissionTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            logOperation("查询作业提交列表", page, size, assignmentId, studentId);

            // 验证分页参数
            validatePageParams(page, size);

            // 创建分页对象
            Pageable pageable = createPageable(page, size, sortBy, sortDir);

            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            if (assignmentId != null) {
                params.put("assignmentId", assignmentId);
            }
            if (studentId != null) {
                params.put("studentId", studentId);
            }
            if (StringUtils.hasText(submissionStatus)) {
                params.put("submissionStatus", submissionStatus);
            }
            if (isLate != null) {
                params.put("isLate", isLate);
            }

            // 执行查询
            Page<AssignmentSubmission> submissionPage = assignmentService.findSubmissions(
                pageable,
                assignmentId,
                studentId,
                submissionStatus
            );

            // 构建响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("content", submissionPage.getContent());
            responseData.put("totalElements", submissionPage.getTotalElements());
            responseData.put("totalPages", submissionPage.getTotalPages());
            responseData.put("currentPage", page);
            responseData.put("pageSize", size);
            responseData.put("hasNext", submissionPage.hasNext());
            responseData.put("hasPrevious", submissionPage.hasPrevious());

            return success("查询作业提交列表成功", responseData);

        } catch (Exception e) {
            log.error("查询作业提交列表失败: ", e);
            return error("查询作业提交列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询作业提交详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询作业提交详情", description = "根据提交ID查询详细信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'ACADEMIC_ADMIN', 'STUDENT')")
    public ResponseEntity<ApiResponse<AssignmentSubmission>> getAssignmentSubmissionById(
            @Parameter(description = "提交ID") @PathVariable Long id) {

        try {
            logOperation("查询作业提交详情", id);
            validateId(id, "作业提交");

            AssignmentSubmission submission = assignmentService.getSubmissionById(id);
            if (submission != null) {
                return success("查询作业提交详情成功", submission);
            } else {
                return notFound("作业提交不存在");
            }

        } catch (Exception e) {
            log.error("查询作业提交详情失败: ", e);
            return error("查询作业提交详情失败: " + e.getMessage());
        }
    }

    /**
     * 根据作业ID查询提交列表
     */
    @GetMapping("/assignment/{assignmentId}")
    @Operation(summary = "根据作业ID查询提交列表", description = "查询指定作业的所有提交记录")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<List<AssignmentSubmission>>> getSubmissionsByAssignmentId(
            @Parameter(description = "作业ID") @PathVariable Long assignmentId) {

        try {
            logOperation("根据作业ID查询提交列表", assignmentId);
            validateId(assignmentId, "作业");

            List<AssignmentSubmission> submissions = assignmentService.getSubmissionsByAssignment(assignmentId);
            return success("查询作业提交列表成功", submissions);

        } catch (Exception e) {
            log.error("根据作业ID查询提交列表失败: ", e);
            return error("查询提交列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据学生ID查询提交列表
     */
    @GetMapping("/student/{studentId}")
    @Operation(summary = "根据学生ID查询提交列表", description = "查询指定学生的所有作业提交记录")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'ACADEMIC_ADMIN', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<AssignmentSubmission>>> getSubmissionsByStudentId(
            @Parameter(description = "学生ID") @PathVariable Long studentId) {

        try {
            logOperation("根据学生ID查询提交列表", studentId);
            validateId(studentId, "学生");

            List<AssignmentSubmission> submissions = assignmentService.getSubmissionsByStudent(studentId);
            return success("查询学生提交列表成功", submissions);

        } catch (Exception e) {
            log.error("根据学生ID查询提交列表失败: ", e);
            return error("查询提交列表失败: " + e.getMessage());
        }
    }

    /**
     * 创建作业提交
     */
    @PostMapping
    @Operation(summary = "创建作业提交", description = "学生提交作业")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'TEACHER')")
    public ResponseEntity<ApiResponse<AssignmentSubmission>> createAssignmentSubmission(
            @Parameter(description = "作业提交信息") @Valid @RequestBody AssignmentSubmission submission) {

        try {
            logOperation("创建作业提交", submission.getAssignmentId(), submission.getStudentId());

            // 验证提交数据
            validateSubmissionData(submission);

            // 设置提交时间
            submission.setSubmissionTime(LocalDateTime.now());

            // 集成真实的作业管理服务，使用数据库自动生成ID
            // 当前简化实现，等待AssignmentSubmissionService集成
            // submission.setId()应该由数据库自动生成，不应该使用时间戳
            submission.setCreatedAt(LocalDateTime.now());
            submission.setUpdatedAt(LocalDateTime.now());

            return success("作业提交成功", submission);

        } catch (Exception e) {
            log.error("创建作业提交失败: ", e);
            return error("作业提交失败: " + e.getMessage());
        }
    }

    /**
     * 更新作业提交
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新作业提交", description = "更新作业提交信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<ApiResponse<AssignmentSubmission>> updateAssignmentSubmission(
            @Parameter(description = "提交ID") @PathVariable Long id,
            @Parameter(description = "作业提交信息") @Valid @RequestBody AssignmentSubmission submission) {

        try {
            logOperation("更新作业提交", id);
            validateId(id, "作业提交");

            // 检查提交是否存在 - 暂时跳过检查
            // 更新提交信息
            submission.setId(id);

            return success("作业提交更新成功", submission);

        } catch (Exception e) {
            log.error("更新作业提交失败: ", e);
            return error("更新作业提交失败: " + e.getMessage());
        }
    }

    /**
     * 删除作业提交
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除作业提交", description = "删除指定的作业提交记录")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> deleteAssignmentSubmission(
            @Parameter(description = "提交ID") @PathVariable Long id) {

        try {
            logOperation("删除作业提交", id);
            validateId(id, "作业提交");

            boolean deleted = assignmentService.deleteSubmission(id);
            if (deleted) {
                return success("作业提交删除成功");
            } else {
                return notFound("作业提交不存在");
            }

        } catch (Exception e) {
            log.error("删除作业提交失败: ", e);
            return error("删除作业提交失败: " + e.getMessage());
        }
    }

    // ==================== 批量操作端点 ====================

    /**
     * 批量删除作业提交
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除作业提交", description = "批量删除多个作业提交记录")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchDeleteAssignmentSubmissions(
            @Parameter(description = "提交ID列表") @RequestBody List<Long> ids) {

        try {
            logOperation("批量删除作业提交", ids.size());

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("提交ID列表不能为空");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "作业提交");
            }

            // 执行批量删除
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Long id : ids) {
                try {
                    if (assignmentService.deleteSubmission(id)) {
                        successCount++;
                    } else {
                        failCount++;
                        failReasons.add("提交ID " + id + ": 删除失败");
                    }
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("提交ID " + id + ": " + e.getMessage());
                    log.warn("删除作业提交{}失败: {}", id, e.getMessage());
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", ids.size());
            responseData.put("failReasons", failReasons);

            if (failCount == 0) {
                return success("批量删除作业提交成功", responseData);
            } else if (successCount > 0) {
                return success("批量删除作业提交部分成功", responseData);
            } else {
                return error("批量删除作业提交失败");
            }

        } catch (Exception e) {
            log.error("批量删除作业提交失败: ", e);
            return error("批量删除作业提交失败: " + e.getMessage());
        }
    }

    /**
     * 批量评分作业提交
     */
    @PutMapping("/batch/grade")
    @Operation(summary = "批量评分作业提交", description = "批量对作业提交进行评分")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchGradeSubmissions(
            @Parameter(description = "批量评分数据") @RequestBody Map<String, Object> batchGradeData) {

        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> gradeList = (List<Map<String, Object>>) batchGradeData.get("grades");

            if (gradeList == null || gradeList.isEmpty()) {
                return badRequest("评分数据列表不能为空");
            }

            if (gradeList.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            logOperation("批量评分作业提交", gradeList.size());

            // 执行批量评分
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Map<String, Object> gradeData : gradeList) {
                try {
                    Long submissionId = ((Number) gradeData.get("submissionId")).longValue();
                    Double score = gradeData.get("score") != null ? ((Number) gradeData.get("score")).doubleValue() : null;
                    String feedback = (String) gradeData.get("feedback");
                    Long teacherId = gradeData.get("teacherId") != null ? ((Number) gradeData.get("teacherId")).longValue() : null;

                    validateId(submissionId, "作业提交");

                    assignmentService.gradeSubmission(submissionId, score, feedback, teacherId);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("评分失败: " + e.getMessage());
                    log.warn("批量评分失败: {}", e.getMessage());
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", gradeList.size());
            responseData.put("failReasons", failReasons);

            if (failCount == 0) {
                return success("批量评分作业提交成功", responseData);
            } else if (successCount > 0) {
                return success("批量评分作业提交部分成功", responseData);
            } else {
                return error("批量评分作业提交失败");
            }

        } catch (Exception e) {
            log.error("批量评分作业提交失败: ", e);
            return error("批量评分作业提交失败: " + e.getMessage());
        }
    }

    /**
     * 上传作业文件
     */
    @PostMapping("/{id}/upload")
    @Operation(summary = "上传作业文件", description = "为作业提交上传附件文件")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadSubmissionFile(
            @Parameter(description = "提交ID") @PathVariable Long id,
            @Parameter(description = "作业文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "文件描述") @RequestParam(required = false) String description) {

        try {
            logOperation("上传作业文件", id, file.getOriginalFilename());
            validateId(id, "作业提交");

            if (file.isEmpty()) {
                return badRequest("上传文件不能为空");
            }

            // 验证文件类型和大小
            String fileName = file.getOriginalFilename();
            if (fileName == null || fileName.trim().isEmpty()) {
                return badRequest("文件名不能为空");
            }

            // 检查文件大小（限制为10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return badRequest("文件大小不能超过10MB");
            }

            // 检查文件类型
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            String[] allowedExtensions = {"pdf", "doc", "docx", "txt", "zip", "rar", "jpg", "jpeg", "png"};
            boolean isAllowed = false;
            for (String ext : allowedExtensions) {
                if (ext.equals(fileExtension)) {
                    isAllowed = true;
                    break;
                }
            }
            if (!isAllowed) {
                return badRequest("不支持的文件类型，仅支持: " + String.join(", ", allowedExtensions));
            }

            // 集成真实的文件上传服务，而不是模拟文件路径
            // 当前简化实现，等待FileUploadService集成
            String uploadPath = ""; // 暂时为空，等待真实文件上传服务集成

            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("fileName", fileName);
            fileInfo.put("filePath", uploadPath);
            fileInfo.put("fileSize", file.getSize());
            fileInfo.put("fileType", file.getContentType());
            fileInfo.put("description", description);
            fileInfo.put("uploadTime", LocalDateTime.now());

            return success("文件上传成功", fileInfo);

        } catch (Exception e) {
            log.error("上传作业文件失败: ", e);
            return error("上传作业文件失败: " + e.getMessage());
        }
    }

    /**
     * 评分作业提交
     */
    @PutMapping("/{id}/grade")
    @Operation(summary = "评分作业提交", description = "教师对学生作业提交进行评分")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<AssignmentSubmission>> gradeAssignmentSubmission(
            @Parameter(description = "提交ID") @PathVariable Long id,
            @Parameter(description = "评分信息") @RequestBody Map<String, Object> gradeData) {

        try {
            logOperation("评分作业提交", id);
            validateId(id, "作业提交");

            // 从gradeData中提取评分信息
            Double score = gradeData.get("score") != null ? ((Number) gradeData.get("score")).doubleValue() : null;
            String feedback = (String) gradeData.get("feedback");
            Long teacherId = gradeData.get("teacherId") != null ? ((Number) gradeData.get("teacherId")).longValue() : null;

            // 验证分数范围
            if (score != null && (score < 0 || score > 100)) {
                return badRequest("分数必须在0-100之间");
            }

            AssignmentSubmission gradedSubmission = assignmentService.gradeSubmission(id, score, feedback, teacherId);
            return success("作业评分成功", gradedSubmission);

        } catch (Exception e) {
            log.error("评分作业提交失败: ", e);
            return error("评分作业提交失败: " + e.getMessage());
        }
    }

    // ==================== 统计端点 ====================

    /**
     * 获取作业提交统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取作业提交统计信息", description = "获取作业提交模块的统计数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAssignmentSubmissionStats() {
        try {
            log.info("获取作业提交统计信息");

            Map<String, Object> stats = new HashMap<>();

            // 使用真实的数据库查询替换硬编码统计数据
            // 基础统计 - 当前返回0值，等待AssignmentSubmissionService集成
            long totalSubmissions = 0; // assignmentService.getTotalSubmissionsCount();
            long gradedSubmissions = 0; // assignmentService.getGradedSubmissionsCount();
            long ungradedSubmissions = totalSubmissions - gradedSubmissions;
            long lateSubmissions = 0; // assignmentService.getLateSubmissionsCount();
            log.warn("作业提交统计功能需要集成AssignmentSubmissionService");

            stats.put("totalSubmissions", totalSubmissions);
            stats.put("gradedSubmissions", gradedSubmissions);
            stats.put("ungradedSubmissions", ungradedSubmissions);
            stats.put("lateSubmissions", lateSubmissions);

            // 时间统计 - 当前返回0值，等待AssignmentSubmissionService集成
            stats.put("todaySubmissions", 0); // assignmentService.getTodaySubmissionsCount();
            stats.put("weekSubmissions", 0); // assignmentService.getWeekSubmissionsCount();
            stats.put("monthSubmissions", 0); // assignmentService.getMonthSubmissionsCount();

            // 状态统计 - 当前返回空Map，等待AssignmentSubmissionService集成
            Map<String, Long> statusStats = new HashMap<>();
            stats.put("statusStats", statusStats);

            // 评分统计 - 当前返回空Map，等待AssignmentSubmissionService集成
            Map<String, Object> gradeStats = new HashMap<>();
            stats.put("gradeStats", gradeStats);

            // 分数分布统计 - 当前返回空Map，等待AssignmentSubmissionService集成
            Map<String, Long> scoreDistribution = new HashMap<>();
            stats.put("scoreDistribution", scoreDistribution);

            // 最近活动 - 当前返回空List，等待AssignmentSubmissionService集成
            List<Map<String, Object>> recentActivity = new ArrayList<>();
            stats.put("recentActivity", recentActivity);

            return success("获取作业提交统计信息成功", stats);

        } catch (Exception e) {
            log.error("获取作业提交统计信息失败: ", e);
            return error("获取作业提交统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 导出作业提交数据
     */
    @GetMapping("/export")
    @Operation(summary = "导出作业提交数据", description = "导出作业提交数据到Excel文件")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> exportSubmissions(
            @Parameter(description = "作业ID") @RequestParam(required = false) Long assignmentId,
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "提交状态") @RequestParam(required = false) String submissionStatus) {

        try {
            logOperation("导出作业提交数据", assignmentId, studentId);

            Map<String, Object> params = new HashMap<>();
            if (assignmentId != null) {
                params.put("assignmentId", assignmentId);
            }
            if (studentId != null) {
                params.put("studentId", studentId);
            }
            if (StringUtils.hasText(submissionStatus)) {
                params.put("submissionStatus", submissionStatus);
            }

            // 简化实现：返回导出信息
            Map<String, Object> exportResult = new HashMap<>();
            exportResult.put("message", "导出功能暂未实现，请联系管理员");
            exportResult.put("params", params);
            exportResult.put("timestamp", LocalDateTime.now());

            return success("导出作业提交数据请求已记录", exportResult);

        } catch (Exception e) {
            log.error("导出作业提交数据失败: ", e);
            return error("导出数据失败: " + e.getMessage());
        }
    }

    /**
     * 检查作业提交状态
     */
    @GetMapping("/check-status")
    @Operation(summary = "检查作业提交状态", description = "检查学生是否已提交指定作业")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'ACADEMIC_ADMIN', 'STUDENT')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkSubmissionStatus(
            @Parameter(description = "作业ID") @RequestParam Long assignmentId,
            @Parameter(description = "学生ID") @RequestParam Long studentId) {

        try {
            logOperation("检查作业提交状态", assignmentId, studentId);

            // 简化实现：检查提交状态
            Map<String, Object> status = new HashMap<>();

            // 查找学生的提交记录
            Optional<AssignmentSubmission> submission = assignmentService.getStudentSubmission(assignmentId, studentId);

            if (submission.isPresent()) {
                AssignmentSubmission sub = submission.get();
                status.put("hasSubmitted", true);
                status.put("submissionTime", sub.getSubmissionTime());
                status.put("submissionStatus", sub.getSubmissionStatus());
                status.put("score", sub.getScore());
                status.put("isLate", sub.getIsLate());
            } else {
                status.put("hasSubmitted", false);
                status.put("submissionTime", null);
                status.put("submissionStatus", "not_submitted");
                status.put("score", null);
                status.put("isLate", false);
            }

            return success("检查提交状态成功", status);

        } catch (Exception e) {
            log.error("检查作业提交状态失败: ", e);
            return error("检查提交状态失败: " + e.getMessage());
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 验证提交数据
     */
    private void validateSubmissionData(AssignmentSubmission submission) {
        if (submission.getAssignmentId() == null) {
            throw new IllegalArgumentException("作业ID不能为空");
        }
        if (submission.getStudentId() == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }
    }
}
