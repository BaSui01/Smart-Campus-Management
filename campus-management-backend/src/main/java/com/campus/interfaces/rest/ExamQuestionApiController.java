package com.campus.interfaces.rest;

import com.campus.application.service.ExamService;
import com.campus.domain.entity.ExamQuestion;
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
import java.util.Map;

/**
 * 考试题目API控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@RestController
@RequestMapping("/api/exam-questions")
@Tag(name = "考试题目管理", description = "考试题目相关API接口")
public class ExamQuestionApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(ExamQuestionApiController.class);
    
    @Autowired
    private ExamService examService;
    
    // ================================
    // 基础CRUD操作
    // ================================
    
    @PostMapping
    @Operation(summary = "创建考试题目", description = "创建新的考试题目")
    public ResponseEntity<ApiResponse<ExamQuestion>> createQuestion(
            @Valid @RequestBody ExamQuestion question) {
        try {
            logger.info("创建考试题目: examId={}, questionType={}", 
                       question.getExamId(), question.getQuestionType());
            
            ExamQuestion result = examService.createExamQuestion(question);
            return ResponseEntity.ok(ApiResponse.success("考试题目创建成功", result));
            
        } catch (Exception e) {
            logger.error("创建考试题目失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("创建考试题目失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{questionId}")
    @Operation(summary = "获取题目详情", description = "根据ID获取考试题目详细信息")
    public ResponseEntity<ApiResponse<ExamQuestion>> getQuestionById(
            @Parameter(description = "题目ID") @PathVariable Long questionId) {
        try {
            ExamQuestion question = examService.getExamQuestionById(questionId);
            if (question != null) {
                return ResponseEntity.ok(ApiResponse.success(question));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("获取考试题目详情失败: questionId={}", questionId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取考试题目详情失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{questionId}")
    @Operation(summary = "更新考试题目", description = "更新考试题目信息")
    public ResponseEntity<ApiResponse<ExamQuestion>> updateQuestion(
            @PathVariable Long questionId,
            @Valid @RequestBody ExamQuestion question) {
        try {
            logger.info("更新考试题目: questionId={}", questionId);
            
            question.setId(questionId);
            ExamQuestion result = examService.updateExamQuestion(question);
            return ResponseEntity.ok(ApiResponse.success("考试题目更新成功", result));
            
        } catch (Exception e) {
            logger.error("更新考试题目失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("更新考试题目失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{questionId}")
    @Operation(summary = "删除考试题目", description = "删除指定的考试题目")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @Parameter(description = "题目ID") @PathVariable Long questionId) {
        try {
            logger.info("删除考试题目: questionId={}", questionId);
            
            examService.deleteExamQuestion(questionId);
            return ResponseEntity.ok(ApiResponse.success("考试题目删除成功"));
            
        } catch (Exception e) {
            logger.error("删除考试题目失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("删除考试题目失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 查询操作
    // ================================
    
    @GetMapping
    @Operation(summary = "分页获取题目列表", description = "分页查询考试题目信息")
    public ResponseEntity<ApiResponse<Page<ExamQuestion>>> getQuestions(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "考试ID") @RequestParam(required = false) Long examId,
            @Parameter(description = "题目类型") @RequestParam(required = false) String questionType,
            @Parameter(description = "难度级别") @RequestParam(required = false) String difficulty) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ExamQuestion> questions = examService.findExamQuestions(
                pageable, examId, questionType, difficulty);
            
            return ResponseEntity.ok(ApiResponse.success(questions));
            
        } catch (Exception e) {
            logger.error("获取考试题目列表失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取考试题目列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/exam/{examId}")
    @Operation(summary = "获取考试的所有题目", description = "获取指定考试的所有题目")
    public ResponseEntity<ApiResponse<List<ExamQuestion>>> getQuestionsByExam(
            @Parameter(description = "考试ID") @PathVariable Long examId,
            @Parameter(description = "是否随机排序") @RequestParam(defaultValue = "false") boolean randomOrder) {
        try {
            List<ExamQuestion> questions = examService.getQuestionsByExam(examId, randomOrder);
            return ResponseEntity.ok(ApiResponse.success(questions));
            
        } catch (Exception e) {
            logger.error("获取考试题目失败: examId={}", examId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取考试题目失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/type/{questionType}")
    @Operation(summary = "按类型获取题目", description = "根据题目类型获取题目列表")
    public ResponseEntity<ApiResponse<List<ExamQuestion>>> getQuestionsByType(
            @Parameter(description = "题目类型") @PathVariable String questionType,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId) {
        try {
            List<ExamQuestion> questions = examService.getQuestionsByType(questionType, courseId);
            return ResponseEntity.ok(ApiResponse.success(questions));
            
        } catch (Exception e) {
            logger.error("按类型获取题目失败: questionType={}", questionType, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("按类型获取题目失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 题库管理
    // ================================
    
    @PostMapping("/import")
    @Operation(summary = "批量导入题目", description = "批量导入考试题目")
    public ResponseEntity<ApiResponse<Map<String, Object>>> importQuestions(
            @RequestBody List<ExamQuestion> questions) {
        try {
            logger.info("批量导入考试题目: {} 个", questions.size());
            
            Map<String, Object> result = examService.importExamQuestions(questions);
            return ResponseEntity.ok(ApiResponse.success("题目导入完成", result));
            
        } catch (Exception e) {
            logger.error("批量导入题目失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("批量导入题目失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/export")
    @Operation(summary = "导出题目", description = "导出考试题目数据")
    public ResponseEntity<ApiResponse<List<ExamQuestion>>> exportQuestions(
            @Parameter(description = "考试ID") @RequestParam(required = false) Long examId,
            @Parameter(description = "题目类型") @RequestParam(required = false) String questionType) {
        try {
            List<ExamQuestion> questions = examService.exportExamQuestions(examId, questionType);
            return ResponseEntity.ok(ApiResponse.success(questions));
            
        } catch (Exception e) {
            logger.error("导出题目失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("导出题目失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/duplicate")
    @Operation(summary = "复制题目", description = "复制题目到其他考试")
    public ResponseEntity<ApiResponse<List<ExamQuestion>>> duplicateQuestions(
            @RequestBody Map<String, Object> duplicateData) {
        try {
            logger.info("复制考试题目");
            
            List<Long> questionIds = (List<Long>) duplicateData.get("questionIds");
            Long targetExamId = Long.valueOf(duplicateData.get("targetExamId").toString());
            
            List<ExamQuestion> result = examService.duplicateQuestions(questionIds, targetExamId);
            return ResponseEntity.ok(ApiResponse.success("题目复制成功", result));
            
        } catch (Exception e) {
            logger.error("复制题目失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("复制题目失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 题目分析
    // ================================
    
    @GetMapping("/statistics/exam/{examId}")
    @Operation(summary = "获取题目统计", description = "获取考试题目统计信息")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getQuestionStatistics(
            @Parameter(description = "考试ID") @PathVariable Long examId) {
        try {
            Map<String, Object> statistics = examService.getQuestionStatistics(examId);
            return ResponseEntity.ok(ApiResponse.success(statistics));
            
        } catch (Exception e) {
            logger.error("获取题目统计失败: examId={}", examId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取题目统计失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/difficulty-analysis/{examId}")
    @Operation(summary = "题目难度分析", description = "分析考试题目难度分布")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDifficultyAnalysis(
            @Parameter(description = "考试ID") @PathVariable Long examId) {
        try {
            Map<String, Object> analysis = examService.getQuestionDifficultyAnalysis(examId);
            return ResponseEntity.ok(ApiResponse.success(analysis));
            
        } catch (Exception e) {
            logger.error("题目难度分析失败: examId={}", examId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("题目难度分析失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 批量操作
    // ================================
    
    @PostMapping("/batch-update")
    @Operation(summary = "批量更新题目", description = "批量更新题目信息")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchUpdateQuestions(
            @RequestBody Map<String, Object> updateData) {
        try {
            logger.info("批量更新考试题目");
            
            Map<String, Object> result = examService.batchUpdateQuestions(updateData);
            return ResponseEntity.ok(ApiResponse.success("批量更新完成", result));
            
        } catch (Exception e) {
            logger.error("批量更新题目失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("批量更新题目失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除题目", description = "批量删除考试题目")
    public ResponseEntity<ApiResponse<Void>> batchDeleteQuestions(
            @RequestBody List<Long> questionIds) {
        try {
            logger.info("批量删除考试题目: {} 个", questionIds.size());
            
            examService.batchDeleteQuestions(questionIds);
            return ResponseEntity.ok(ApiResponse.success("批量删除成功"));
            
        } catch (Exception e) {
            logger.error("批量删除题目失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("批量删除题目失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 题目验证
    // ================================
    
    @PostMapping("/validate")
    @Operation(summary = "验证题目", description = "验证题目格式和内容")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateQuestions(
            @RequestBody List<ExamQuestion> questions) {
        try {
            Map<String, Object> validationResult = examService.validateQuestions(questions);
            return ResponseEntity.ok(ApiResponse.success(validationResult));
            
        } catch (Exception e) {
            logger.error("题目验证失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("题目验证失败: " + e.getMessage()));
        }
    }
}
