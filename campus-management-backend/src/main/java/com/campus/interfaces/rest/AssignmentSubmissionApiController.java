package com.campus.interfaces.rest;

import com.campus.application.service.AssignmentService;
import com.campus.domain.entity.AssignmentSubmission;
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
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 作业提交API控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@RestController
@RequestMapping("/api/assignment-submissions")
@Tag(name = "作业提交管理", description = "作业提交相关API接口")
public class AssignmentSubmissionApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(AssignmentSubmissionApiController.class);
    
    @Autowired
    private AssignmentService assignmentService;
    
    // ================================
    // 基础CRUD操作
    // ================================
    
    @PostMapping
    @Operation(summary = "提交作业", description = "学生提交作业")
    public ResponseEntity<ApiResponse<AssignmentSubmission>> submitAssignment(
            @Valid @RequestBody AssignmentSubmission submission) {
        try {
            logger.info("学生提交作业: studentId={}, assignmentId={}", 
                       submission.getStudentId(), submission.getAssignmentId());
            
            AssignmentSubmission result = assignmentService.submitAssignment(submission);
            return ResponseEntity.ok(ApiResponse.success("作业提交成功", result));
            
        } catch (Exception e) {
            logger.error("作业提交失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("作业提交失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{submissionId}/files")
    @Operation(summary = "上传作业文件", description = "为作业提交上传附件文件")
    public ResponseEntity<ApiResponse<String>> uploadSubmissionFile(
            @PathVariable Long submissionId,
            @RequestParam("file") MultipartFile file) {
        try {
            logger.info("上传作业文件: submissionId={}, fileName={}", submissionId, file.getOriginalFilename());
            
            String fileUrl = assignmentService.uploadSubmissionFile(submissionId, file);
            return ResponseEntity.ok(ApiResponse.success(fileUrl, "文件上传成功"));
            
        } catch (Exception e) {
            logger.error("文件上传失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("文件上传失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{submissionId}")
    @Operation(summary = "获取作业提交详情", description = "根据ID获取作业提交详细信息")
    public ResponseEntity<ApiResponse<AssignmentSubmission>> getSubmissionById(
            @Parameter(description = "作业提交ID") @PathVariable Long submissionId) {
        try {
            AssignmentSubmission submission = assignmentService.getSubmissionById(submissionId);
            if (submission != null) {
                return ResponseEntity.ok(ApiResponse.success(submission));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("获取作业提交详情失败: submissionId={}", submissionId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取作业提交详情失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{submissionId}")
    @Operation(summary = "更新作业提交", description = "更新作业提交内容")
    public ResponseEntity<ApiResponse<AssignmentSubmission>> updateSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody AssignmentSubmission submission) {
        try {
            logger.info("更新作业提交: submissionId={}", submissionId);
            
            submission.setId(submissionId);
            AssignmentSubmission result = assignmentService.updateSubmission(submission);
            return ResponseEntity.ok(ApiResponse.success("作业提交更新成功", result));
            
        } catch (Exception e) {
            logger.error("更新作业提交失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("更新作业提交失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{submissionId}")
    @Operation(summary = "删除作业提交", description = "删除指定的作业提交")
    public ResponseEntity<ApiResponse<Void>> deleteSubmission(
            @Parameter(description = "作业提交ID") @PathVariable Long submissionId) {
        try {
            logger.info("删除作业提交: submissionId={}", submissionId);
            
            assignmentService.deleteSubmission(submissionId);
            return ResponseEntity.ok(ApiResponse.success("作业提交删除成功"));
            
        } catch (Exception e) {
            logger.error("删除作业提交失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("删除作业提交失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 查询操作
    // ================================
    
    @GetMapping
    @Operation(summary = "分页获取作业提交列表", description = "分页查询作业提交信息")
    public ResponseEntity<ApiResponse<Page<AssignmentSubmission>>> getSubmissions(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "作业ID") @RequestParam(required = false) Long assignmentId,
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "提交状态") @RequestParam(required = false) String status) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<AssignmentSubmission> submissions = assignmentService.findSubmissions(
                pageable, assignmentId, studentId, status);
            
            return ResponseEntity.ok(ApiResponse.success(submissions));
            
        } catch (Exception e) {
            logger.error("获取作业提交列表失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取作业提交列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/assignment/{assignmentId}")
    @Operation(summary = "获取指定作业的所有提交", description = "获取某个作业的所有学生提交情况")
    public ResponseEntity<ApiResponse<List<AssignmentSubmission>>> getSubmissionsByAssignment(
            @Parameter(description = "作业ID") @PathVariable Long assignmentId) {
        try {
            List<AssignmentSubmission> submissions = assignmentService.getSubmissionsByAssignment(assignmentId);
            return ResponseEntity.ok(ApiResponse.success(submissions));
            
        } catch (Exception e) {
            logger.error("获取作业提交列表失败: assignmentId={}", assignmentId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取作业提交列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/student/{studentId}")
    @Operation(summary = "获取学生的作业提交记录", description = "获取指定学生的所有作业提交记录")
    public ResponseEntity<ApiResponse<List<AssignmentSubmission>>> getSubmissionsByStudent(
            @Parameter(description = "学生ID") @PathVariable Long studentId) {
        try {
            List<AssignmentSubmission> submissions = assignmentService.getSubmissionsByStudent(studentId);
            return ResponseEntity.ok(ApiResponse.success(submissions));
            
        } catch (Exception e) {
            logger.error("获取学生作业提交记录失败: studentId={}", studentId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取学生作业提交记录失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 评分操作
    // ================================
    
    @PostMapping("/{submissionId}/grade")
    @Operation(summary = "作业评分", description = "教师对学生作业进行评分")
    public ResponseEntity<ApiResponse<AssignmentSubmission>> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestBody Map<String, Object> gradeData) {
        try {
            logger.info("作业评分: submissionId={}", submissionId);
            
            Double score = Double.valueOf(gradeData.get("score").toString());
            String feedback = (String) gradeData.get("feedback");
            Long teacherId = Long.valueOf(gradeData.get("teacherId").toString());
            
            AssignmentSubmission result = assignmentService.gradeSubmission(
                submissionId, score, feedback, teacherId);
            
            return ResponseEntity.ok(ApiResponse.success("作业评分成功", result));
            
        } catch (Exception e) {
            logger.error("作业评分失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("作业评分失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 统计操作
    // ================================
    
    @GetMapping("/statistics/assignment/{assignmentId}")
    @Operation(summary = "获取作业提交统计", description = "获取指定作业的提交统计信息")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSubmissionStatistics(
            @Parameter(description = "作业ID") @PathVariable Long assignmentId) {
        try {
            Map<String, Object> statistics = assignmentService.getSubmissionStatistics(assignmentId);
            return ResponseEntity.ok(ApiResponse.success(statistics));
            
        } catch (Exception e) {
            logger.error("获取作业提交统计失败: assignmentId={}", assignmentId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取作业提交统计失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/overdue")
    @Operation(summary = "获取逾期未提交作业", description = "获取逾期未提交的作业列表")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getOverdueSubmissions(
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentId) {
        try {
            List<Map<String, Object>> overdueSubmissions = assignmentService.getOverdueSubmissions(courseId, studentId);
            return ResponseEntity.ok(ApiResponse.success(overdueSubmissions));
            
        } catch (Exception e) {
            logger.error("获取逾期作业失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取逾期作业失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 批量操作
    // ================================
    
    @PostMapping("/batch-grade")
    @Operation(summary = "批量评分", description = "批量对作业进行评分")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchGradeSubmissions(
            @RequestBody Map<String, Object> batchGradeData) {
        try {
            logger.info("批量作业评分");
            
            Map<String, Object> result = assignmentService.batchGradeSubmissions(batchGradeData);
            return ResponseEntity.ok(ApiResponse.success("批量评分完成", result));
            
        } catch (Exception e) {
            logger.error("批量评分失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("批量评分失败: " + e.getMessage()));
        }
    }
}
