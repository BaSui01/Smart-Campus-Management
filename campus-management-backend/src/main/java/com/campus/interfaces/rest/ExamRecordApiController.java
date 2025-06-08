package com.campus.interfaces.rest;

import com.campus.application.service.ExamService;
import com.campus.domain.entity.ExamRecord;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 考试记录API控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@RestController
@RequestMapping("/api/exam-records")
@Tag(name = "考试记录管理", description = "考试记录相关API接口")
public class ExamRecordApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(ExamRecordApiController.class);
    
    @Autowired
    private ExamService examService;
    
    // ================================
    // 基础CRUD操作
    // ================================
    
    @PostMapping
    @Operation(summary = "创建考试记录", description = "学生开始考试时创建考试记录")
    public ResponseEntity<ApiResponse<ExamRecord>> createExamRecord(
            @Valid @RequestBody ExamRecord examRecord) {
        try {
            logger.info("创建考试记录: studentId={}, examId={}", 
                       examRecord.getStudentId(), examRecord.getExamId());
            
            ExamRecord result = examService.createExamRecord(examRecord);
            return ResponseEntity.ok(ApiResponse.success("考试记录创建成功", result));
            
        } catch (Exception e) {
            logger.error("创建考试记录失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("创建考试记录失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{recordId}")
    @Operation(summary = "获取考试记录详情", description = "根据ID获取考试记录详细信息")
    public ResponseEntity<ApiResponse<ExamRecord>> getExamRecordById(
            @Parameter(description = "考试记录ID") @PathVariable Long recordId) {
        try {
            ExamRecord examRecord = examService.getExamRecordById(recordId);
            if (examRecord != null) {
                return ResponseEntity.ok(ApiResponse.success(examRecord));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("获取考试记录详情失败: recordId={}", recordId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取考试记录详情失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{recordId}")
    @Operation(summary = "更新考试记录", description = "更新考试记录信息")
    public ResponseEntity<ApiResponse<ExamRecord>> updateExamRecord(
            @PathVariable Long recordId,
            @Valid @RequestBody ExamRecord examRecord) {
        try {
            logger.info("更新考试记录: recordId={}", recordId);
            
            examRecord.setId(recordId);
            ExamRecord result = examService.updateExamRecord(examRecord);
            return ResponseEntity.ok(ApiResponse.success("考试记录更新成功", result));
            
        } catch (Exception e) {
            logger.error("更新考试记录失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("更新考试记录失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{recordId}/submit")
    @Operation(summary = "提交考试", description = "学生提交考试答案")
    public ResponseEntity<ApiResponse<ExamRecord>> submitExam(
            @PathVariable Long recordId,
            @RequestBody Map<String, Object> examAnswers) {
        try {
            logger.info("提交考试: recordId={}", recordId);
            
            ExamRecord result = examService.submitExam(recordId, examAnswers);
            return ResponseEntity.ok(ApiResponse.success("考试提交成功", result));
            
        } catch (Exception e) {
            logger.error("提交考试失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("提交考试失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{recordId}")
    @Operation(summary = "删除考试记录", description = "删除指定的考试记录")
    public ResponseEntity<ApiResponse<Void>> deleteExamRecord(
            @Parameter(description = "考试记录ID") @PathVariable Long recordId) {
        try {
            logger.info("删除考试记录: recordId={}", recordId);
            
            examService.deleteExamRecord(recordId);
            return ResponseEntity.ok(ApiResponse.success("考试记录删除成功"));
            
        } catch (Exception e) {
            logger.error("删除考试记录失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("删除考试记录失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 查询操作
    // ================================
    
    @GetMapping
    @Operation(summary = "分页获取考试记录列表", description = "分页查询考试记录信息")
    public ResponseEntity<ApiResponse<Page<ExamRecord>>> getExamRecords(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "考试ID") @RequestParam(required = false) Long examId,
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "考试状态") @RequestParam(required = false) String status) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ExamRecord> examRecords = examService.findExamRecords(
                pageable, examId, studentId, status);
            
            return ResponseEntity.ok(ApiResponse.success(examRecords));
            
        } catch (Exception e) {
            logger.error("获取考试记录列表失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取考试记录列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/exam/{examId}")
    @Operation(summary = "获取考试的所有记录", description = "获取指定考试的所有学生考试记录")
    public ResponseEntity<ApiResponse<List<ExamRecord>>> getRecordsByExam(
            @Parameter(description = "考试ID") @PathVariable Long examId) {
        try {
            List<ExamRecord> examRecords = examService.getRecordsByExam(examId);
            return ResponseEntity.ok(ApiResponse.success(examRecords));
            
        } catch (Exception e) {
            logger.error("获取考试记录失败: examId={}", examId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取考试记录失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/student/{studentId}")
    @Operation(summary = "获取学生的考试记录", description = "获取指定学生的所有考试记录")
    public ResponseEntity<ApiResponse<List<ExamRecord>>> getRecordsByStudent(
            @Parameter(description = "学生ID") @PathVariable Long studentId) {
        try {
            List<ExamRecord> examRecords = examService.getRecordsByStudent(studentId);
            return ResponseEntity.ok(ApiResponse.success(examRecords));
            
        } catch (Exception e) {
            logger.error("获取学生考试记录失败: studentId={}", studentId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取学生考试记录失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/student/{studentId}/exam/{examId}")
    @Operation(summary = "获取学生特定考试记录", description = "获取学生在特定考试中的记录")
    public ResponseEntity<ApiResponse<ExamRecord>> getStudentExamRecord(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @Parameter(description = "考试ID") @PathVariable Long examId) {
        try {
            ExamRecord examRecord = examService.getStudentExamRecord(studentId, examId);
            if (examRecord != null) {
                return ResponseEntity.ok(ApiResponse.success(examRecord));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("获取学生考试记录失败: studentId={}, examId={}", studentId, examId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取学生考试记录失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 考试监控
    // ================================
    
    @GetMapping("/ongoing")
    @Operation(summary = "获取正在进行的考试", description = "获取当前正在进行的考试记录")
    public ResponseEntity<ApiResponse<List<ExamRecord>>> getOngoingExams(
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId) {
        try {
            List<ExamRecord> ongoingExams = examService.getOngoingExams(courseId);
            return ResponseEntity.ok(ApiResponse.success(ongoingExams));
            
        } catch (Exception e) {
            logger.error("获取正在进行的考试失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取正在进行的考试失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{recordId}/heartbeat")
    @Operation(summary = "考试心跳检测", description = "更新学生考试状态心跳")
    public ResponseEntity<ApiResponse<Void>> updateHeartbeat(
            @PathVariable Long recordId) {
        try {
            examService.updateExamHeartbeat(recordId);
            return ResponseEntity.ok(ApiResponse.success("心跳更新成功"));
            
        } catch (Exception e) {
            logger.error("更新考试心跳失败: recordId={}", recordId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("更新考试心跳失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/suspicious")
    @Operation(summary = "获取异常考试记录", description = "获取可能存在作弊的考试记录")
    public ResponseEntity<ApiResponse<List<ExamRecord>>> getSuspiciousRecords(
            @Parameter(description = "考试ID") @RequestParam(required = false) Long examId,
            @Parameter(description = "检测类型") @RequestParam(required = false) String detectionType) {
        try {
            List<ExamRecord> suspiciousRecords = examService.getSuspiciousRecords(examId, detectionType);
            return ResponseEntity.ok(ApiResponse.success(suspiciousRecords));
            
        } catch (Exception e) {
            logger.error("获取异常考试记录失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取异常考试记录失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 成绩管理
    // ================================
    
    @PostMapping("/{recordId}/grade")
    @Operation(summary = "考试评分", description = "对考试进行评分")
    public ResponseEntity<ApiResponse<ExamRecord>> gradeExam(
            @PathVariable Long recordId,
            @RequestBody Map<String, Object> gradeData) {
        try {
            logger.info("考试评分: recordId={}", recordId);
            
            ExamRecord result = examService.gradeExam(recordId, gradeData);
            return ResponseEntity.ok(ApiResponse.success("考试评分成功", result));
            
        } catch (Exception e) {
            logger.error("考试评分失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("考试评分失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/auto-grade/{examId}")
    @Operation(summary = "自动评分", description = "对考试进行自动评分")
    public ResponseEntity<ApiResponse<Map<String, Object>>> autoGradeExam(
            @PathVariable Long examId) {
        try {
            logger.info("自动评分: examId={}", examId);
            
            Map<String, Object> result = examService.autoGradeExam(examId);
            return ResponseEntity.ok(ApiResponse.success("自动评分完成", result));
            
        } catch (Exception e) {
            logger.error("自动评分失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("自动评分失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 统计分析
    // ================================
    
    @GetMapping("/statistics/exam/{examId}")
    @Operation(summary = "获取考试统计", description = "获取考试的统计信息")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getExamStatistics(
            @Parameter(description = "考试ID") @PathVariable Long examId) {
        try {
            Map<String, Object> statistics = examService.getExamStatistics(examId);
            return ResponseEntity.ok(ApiResponse.success(statistics));
            
        } catch (Exception e) {
            logger.error("获取考试统计失败: examId={}", examId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取考试统计失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/analysis/score-distribution/{examId}")
    @Operation(summary = "成绩分布分析", description = "分析考试成绩分布情况")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getScoreDistribution(
            @Parameter(description = "考试ID") @PathVariable Long examId) {
        try {
            Map<String, Object> distribution = examService.getScoreDistribution(examId);
            return ResponseEntity.ok(ApiResponse.success(distribution));
            
        } catch (Exception e) {
            logger.error("成绩分布分析失败: examId={}", examId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("成绩分布分析失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 导出功能
    // ================================
    
    @GetMapping("/export/exam/{examId}")
    @Operation(summary = "导出考试记录", description = "导出指定考试的所有记录")
    public ResponseEntity<ApiResponse<List<ExamRecord>>> exportExamRecords(
            @Parameter(description = "考试ID") @PathVariable Long examId,
            @Parameter(description = "包含答案详情") @RequestParam(defaultValue = "false") boolean includeAnswers) {
        try {
            List<ExamRecord> records = examService.exportExamRecords(examId, includeAnswers);
            return ResponseEntity.ok(ApiResponse.success(records));
            
        } catch (Exception e) {
            logger.error("导出考试记录失败: examId={}", examId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("导出考试记录失败: " + e.getMessage()));
        }
    }
}
