package com.campus.interfaces.rest.v1;

import com.campus.application.service.StudentEvaluationService;
import com.campus.domain.entity.StudentEvaluation;
import com.campus.shared.common.ApiResponse;
import com.campus.interfaces.rest.common.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生评价API控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@RestController
@RequestMapping("/api/v1/student-evaluations")
@Tag(name = "学生评价管理", description = "学生评价相关API接口")
public class StudentEvaluationApiController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(StudentEvaluationApiController.class);

    @Autowired
    private StudentEvaluationService studentEvaluationService;
    
    // ================================
    // 基础CRUD操作
    // ================================
    
    @PostMapping
    @Operation(summary = "创建学生评价", description = "创建新的学生评价")
    public ResponseEntity<ApiResponse<StudentEvaluation>> createEvaluation(
            @Valid @RequestBody StudentEvaluation evaluation) {
        try {
            log.info("创建学生评价: studentId={}, evaluatorId={}, type={}",
                       evaluation.getStudentId(), evaluation.getEvaluatorId(), evaluation.getEvaluationType());

            StudentEvaluation result = studentEvaluationService.createEvaluation(evaluation);
            return ResponseEntity.ok(ApiResponse.success("学生评价创建成功", result));

        } catch (Exception e) {
            log.error("创建学生评价失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("创建学生评价失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{evaluationId}")
    @Operation(summary = "获取评价详情", description = "根据ID获取学生评价详细信息")
    public ResponseEntity<ApiResponse<StudentEvaluation>> getEvaluationById(
            @Parameter(description = "评价ID") @PathVariable Long evaluationId) {
        try {
            StudentEvaluation evaluation = studentEvaluationService.findEvaluationById(evaluationId)
                .orElse(null);
            if (evaluation != null) {
                return ResponseEntity.ok(ApiResponse.success(evaluation));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("获取学生评价详情失败: evaluationId={}", evaluationId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取学生评价详情失败: " + e.getMessage()));
        }
    }

    @PutMapping("/{evaluationId}")
    @Operation(summary = "更新学生评价", description = "更新学生评价信息")
    public ResponseEntity<ApiResponse<StudentEvaluation>> updateEvaluation(
            @PathVariable Long evaluationId,
            @Valid @RequestBody StudentEvaluation evaluation) {
        try {
            log.info("更新学生评价: evaluationId={}", evaluationId);

            evaluation.setId(evaluationId);
            StudentEvaluation result = studentEvaluationService.updateEvaluation(evaluation);
            return ResponseEntity.ok(ApiResponse.success("学生评价更新成功", result));

        } catch (Exception e) {
            log.error("更新学生评价失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("更新学生评价失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{evaluationId}")
    @Operation(summary = "删除学生评价", description = "删除指定的学生评价")
    public ResponseEntity<ApiResponse<Void>> deleteEvaluation(
            @Parameter(description = "评价ID") @PathVariable Long evaluationId) {
        try {
            logger.info("删除学生评价: evaluationId={}", evaluationId);
            
            studentEvaluationService.deleteEvaluation(evaluationId);
            return ResponseEntity.ok(ApiResponse.success("学生评价删除成功"));
            
        } catch (Exception e) {
            logger.error("删除学生评价失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("删除学生评价失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 查询操作
    // ================================
    
    @GetMapping
    @Operation(summary = "分页获取评价列表", description = "分页查询学生评价信息")
    public ResponseEntity<ApiResponse<Page<StudentEvaluation>>> getEvaluations(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "评价者ID") @RequestParam(required = false) Long evaluatorId,
            @Parameter(description = "评价类型") @RequestParam(required = false) String evaluationType) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<StudentEvaluation> evaluations = studentEvaluationService.findAllEvaluations(pageable);
            
            return ResponseEntity.ok(ApiResponse.success(evaluations));
            
        } catch (Exception e) {
            logger.error("获取学生评价列表失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取学生评价列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/student/{studentId}")
    @Operation(summary = "获取学生的所有评价", description = "获取指定学生的所有评价记录")
    public ResponseEntity<ApiResponse<List<StudentEvaluation>>> getEvaluationsByStudent(
            @Parameter(description = "学生ID") @PathVariable Long studentId) {
        try {
            List<StudentEvaluation> evaluations = studentEvaluationService.findEvaluationsByStudent(studentId);
            return ResponseEntity.ok(ApiResponse.success(evaluations));
            
        } catch (Exception e) {
            logger.error("获取学生评价失败: studentId={}", studentId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取学生评价失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/evaluator/{evaluatorId}")
    @Operation(summary = "获取评价者的评价记录", description = "获取指定评价者的所有评价记录")
    public ResponseEntity<ApiResponse<List<StudentEvaluation>>> getEvaluationsByEvaluator(
            @Parameter(description = "评价者ID") @PathVariable Long evaluatorId) {
        try {
            List<StudentEvaluation> evaluations = studentEvaluationService.findEvaluationsByEvaluator(evaluatorId);
            return ResponseEntity.ok(ApiResponse.success(evaluations));
            
        } catch (Exception e) {
            logger.error("获取评价者评价记录失败: evaluatorId={}", evaluatorId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取评价者评价记录失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/student/{studentId}/type/{evaluationType}")
    @Operation(summary = "获取学生特定类型评价", description = "获取学生在特定类型下的评价")
    public ResponseEntity<ApiResponse<List<StudentEvaluation>>> getEvaluationsByStudentAndType(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @Parameter(description = "评价类型") @PathVariable String evaluationType) {
        try {
            List<StudentEvaluation> evaluations = studentEvaluationService.findEvaluationsByStudentAndType(
                studentId, evaluationType);
            return ResponseEntity.ok(ApiResponse.success(evaluations));
            
        } catch (Exception e) {
            logger.error("获取学生特定类型评价失败: studentId={}, type={}", studentId, evaluationType, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取学生特定类型评价失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/course/{courseId}")
    @Operation(summary = "获取课程相关评价", description = "获取指定课程的所有评价")
    public ResponseEntity<ApiResponse<List<StudentEvaluation>>> getEvaluationsByCourse(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        try {
            List<StudentEvaluation> evaluations = studentEvaluationService.findEvaluationsByCourse(courseId);
            return ResponseEntity.ok(ApiResponse.success(evaluations));
            
        } catch (Exception e) {
            logger.error("获取课程评价失败: courseId={}", courseId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取课程评价失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 搜索操作
    // ================================
    
    @GetMapping("/search")
    @Operation(summary = "搜索学生评价", description = "根据关键词搜索学生评价")
    public ResponseEntity<ApiResponse<Page<StudentEvaluation>>> searchEvaluations(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<StudentEvaluation> evaluations = studentEvaluationService.searchEvaluations(keyword, pageable);
            return ResponseEntity.ok(ApiResponse.success(evaluations));
            
        } catch (Exception e) {
            logger.error("搜索学生评价失败: keyword={}", keyword, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("搜索学生评价失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/date-range")
    @Operation(summary = "按日期范围查询评价", description = "根据日期范围查询学生评价")
    public ResponseEntity<ApiResponse<List<StudentEvaluation>>> getEvaluationsByDateRange(
            @Parameter(description = "开始日期") @RequestParam String startDate,
            @Parameter(description = "结束日期") @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            
            List<StudentEvaluation> evaluations = studentEvaluationService.findEvaluationsByDateRange(start, end);
            return ResponseEntity.ok(ApiResponse.success(evaluations));
            
        } catch (Exception e) {
            logger.error("按日期范围查询评价失败: startDate={}, endDate={}", startDate, endDate, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("按日期范围查询评价失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/score-range")
    @Operation(summary = "按评分范围查询评价", description = "根据评分范围查询学生评价")
    public ResponseEntity<ApiResponse<List<StudentEvaluation>>> getEvaluationsByScoreRange(
            @Parameter(description = "最低分") @RequestParam Double minScore,
            @Parameter(description = "最高分") @RequestParam Double maxScore) {
        try {
            List<StudentEvaluation> evaluations = studentEvaluationService.findEvaluationsByScoreRange(minScore, maxScore);
            return ResponseEntity.ok(ApiResponse.success(evaluations));
            
        } catch (Exception e) {
            logger.error("按评分范围查询评价失败: minScore={}, maxScore={}", minScore, maxScore, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("按评分范围查询评价失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 统计分析
    // ================================
    
    @GetMapping("/statistics/student/{studentId}")
    @Operation(summary = "获取学生评价统计", description = "获取学生的评价统计信息")
    public ResponseEntity<ApiResponse<Object>> getStudentEvaluationStatistics(
            @Parameter(description = "学生ID") @PathVariable Long studentId) {
        try {
            Object statistics = studentEvaluationService.getEvaluationStatistics(studentId);
            return ResponseEntity.ok(ApiResponse.success(statistics));
            
        } catch (Exception e) {
            logger.error("获取学生评价统计失败: studentId={}", studentId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取学生评价统计失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/average-score/student/{studentId}")
    @Operation(summary = "获取学生平均分", description = "计算学生的平均评价分数")
    public ResponseEntity<ApiResponse<Double>> getStudentAverageScore(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @Parameter(description = "评价类型") @RequestParam(required = false) String evaluationType) {
        try {
            Double averageScore;
            if (evaluationType != null) {
                averageScore = studentEvaluationService.calculateAverageScoreByType(studentId, evaluationType);
            } else {
                averageScore = studentEvaluationService.calculateAverageScore(studentId);
            }
            
            return ResponseEntity.ok(ApiResponse.success(averageScore));
            
        } catch (Exception e) {
            logger.error("获取学生平均分失败: studentId={}", studentId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取学生平均分失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/latest/student/{studentId}")
    @Operation(summary = "获取学生最新评价", description = "获取学生最新的评价记录")
    public ResponseEntity<ApiResponse<List<StudentEvaluation>>> getLatestEvaluations(
            @Parameter(description = "学生ID") @PathVariable Long studentId,
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "5") int limit) {
        try {
            List<StudentEvaluation> evaluations = studentEvaluationService.getLatestEvaluations(studentId, limit);
            return ResponseEntity.ok(ApiResponse.success(evaluations));
            
        } catch (Exception e) {
            logger.error("获取学生最新评价失败: studentId={}", studentId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取学生最新评价失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/count/type")
    @Operation(summary = "按类型统计评价数量", description = "统计各类型评价的数量")
    public ResponseEntity<ApiResponse<List<Object[]>>> getEvaluationCountByType() {
        try {
            List<Object[]> counts = studentEvaluationService.countEvaluationsByType();
            return ResponseEntity.ok(ApiResponse.success(counts));
            
        } catch (Exception e) {
            logger.error("按类型统计评价数量失败", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("按类型统计评价数量失败: " + e.getMessage()));
        }
    }
    
    // ================================
    // 批量操作
    // ================================
    

    
    // ==================== 批量操作端点 ====================

    /**
     * 批量删除学生评价
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除学生评价", description = "批量删除指定的学生评价")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchDeleteEvaluations(
            @Parameter(description = "评价ID列表") @RequestBody List<Long> evaluationIds) {

        try {
            logOperation("批量删除学生评价", evaluationIds.size());

            // 验证参数
            if (evaluationIds == null || evaluationIds.isEmpty()) {
                return badRequest("评价ID列表不能为空");
            }

            if (evaluationIds.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : evaluationIds) {
                validateId(id, "学生评价");
            }

            // 执行批量删除
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Long id : evaluationIds) {
                try {
                    studentEvaluationService.deleteEvaluation(id);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("评价ID " + id + ": " + e.getMessage());
                    log.warn("删除学生评价{}失败: {}", id, e.getMessage());
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", evaluationIds.size());
            responseData.put("failReasons", failReasons);

            if (failCount == 0) {
                return success("批量删除学生评价成功", responseData);
            } else if (successCount > 0) {
                return success("批量删除学生评价部分成功", responseData);
            } else {
                return error("批量删除学生评价失败");
            }

        } catch (Exception e) {
            log.error("批量删除学生评价失败: ", e);
            return error("批量删除学生评价失败: " + e.getMessage());
        }
    }

    /**
     * 批量创建学生评价
     */
    @PostMapping("/batch/create")
    @Operation(summary = "批量创建学生评价", description = "批量创建学生评价")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchCreateEvaluations(
            @Parameter(description = "评价列表") @RequestBody List<StudentEvaluation> evaluations) {

        try {
            logOperation("批量创建学生评价", evaluations.size());

            // 验证参数
            if (evaluations == null || evaluations.isEmpty()) {
                return badRequest("评价列表不能为空");
            }

            if (evaluations.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 执行批量创建
            List<StudentEvaluation> result = studentEvaluationService.batchCreateEvaluations(evaluations);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", result.size());
            responseData.put("failCount", evaluations.size() - result.size());
            responseData.put("totalRequested", evaluations.size());
            responseData.put("createdEvaluations", result);

            return success("批量创建学生评价成功", responseData);

        } catch (Exception e) {
            log.error("批量创建学生评价失败: ", e);
            return error("批量创建学生评价失败: " + e.getMessage());
        }
    }
    
    // ================================
    // 管理操作
    // ================================
    
    @PostMapping("/{evaluationId}/enable")
    @Operation(summary = "启用评价", description = "启用指定的学生评价")
    public ResponseEntity<ApiResponse<Void>> enableEvaluation(
            @Parameter(description = "评价ID") @PathVariable Long evaluationId) {
        try {
            studentEvaluationService.enableEvaluation(evaluationId);
            return ResponseEntity.ok(ApiResponse.success("评价启用成功"));
            
        } catch (Exception e) {
            logger.error("启用评价失败: evaluationId={}", evaluationId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("启用评价失败: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{evaluationId}/disable")
    @Operation(summary = "禁用评价", description = "禁用指定的学生评价")
    public ResponseEntity<ApiResponse<Void>> disableEvaluation(
            @Parameter(description = "评价ID") @PathVariable Long evaluationId) {
        try {
            studentEvaluationService.disableEvaluation(evaluationId);
            return ResponseEntity.ok(ApiResponse.success("评价禁用成功"));
            
        } catch (Exception e) {
            logger.error("禁用评价失败: evaluationId={}", evaluationId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("禁用评价失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/export/student/{studentId}")
    @Operation(summary = "导出学生评价", description = "导出指定学生的评价数据")
    public ResponseEntity<ApiResponse<List<StudentEvaluation>>> exportStudentEvaluations(
            @Parameter(description = "学生ID") @PathVariable Long studentId) {
        try {
            List<StudentEvaluation> evaluations = studentEvaluationService.exportEvaluations(studentId);
            return ResponseEntity.ok(ApiResponse.success(evaluations));
            
        } catch (Exception e) {
            logger.error("导出学生评价失败: studentId={}", studentId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("导出学生评价失败: " + e.getMessage()));
        }
    }
}
