package com.campus.interfaces.rest.v1;

import com.campus.application.service.ExamService;
import com.campus.common.controller.BaseController;
import com.campus.domain.entity.Exam;
import com.campus.domain.entity.ExamQuestion;
import com.campus.domain.entity.ExamRecord;
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
import java.util.List;
import java.util.Optional;

/**
 * 考试管理API控制器
 * 提供考试的增删改查、发布、题目管理等功能
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@RestController
@RequestMapping("/api/v1/exams")
@Tag(name = "考试管理", description = "考试信息的增删改查、发布、题目管理等操作")
public class ExamApiController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ExamApiController.class);

    @Autowired
    private ExamService examService;

    // ==================== 基础CRUD操作 ====================

    /**
     * 分页查询考试列表
     */
    @GetMapping
    @Operation(summary = "分页查询考试列表", description = "支持按条件搜索和分页查询考试")
    @PreAuthorize("hasAuthority('exam:manage:list')")
    public ResponseEntity<ApiResponse<List<Exam>>> getExams(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "考试名称") @RequestParam(required = false) String examName,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "教师ID") @RequestParam(required = false) Long teacherId,
            @Parameter(description = "考试类型") @RequestParam(required = false) String examType,
            @Parameter(description = "是否已发布") @RequestParam(required = false) Boolean isPublished,
            @Parameter(description = "是否在线考试") @RequestParam(required = false) Boolean isOnline,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "startTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String sortDir,
            HttpServletRequest request) {

        try {
            log.info("查询考试列表 - 页码: {}, 大小: {}, 考试名称: {}, 课程ID: {}, 教师ID: {}", 
                page, size, examName, courseId, teacherId);

            // 验证分页参数
            validatePageParams(page, size);

            // 创建分页对象
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page - 1, size, sort);

            // 查询数据
            Page<Exam> result = examService.findByConditions(
                examName, courseId, teacherId, examType, isPublished, isOnline, pageable);

            return successPage(result, "查询考试列表成功");

        } catch (Exception e) {
            log.error("查询考试列表失败: ", e);
            return error("查询考试列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询考试详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询考试详情", description = "根据ID查询考试的详细信息")
    @PreAuthorize("hasAuthority('exam:manage:list')")
    public ResponseEntity<ApiResponse<Exam>> getExam(
            @Parameter(description = "考试ID", required = true) @PathVariable Long id) {

        try {
            log.info("查询考试详情 - ID: {}", id);

            validateId(id, "考试");

            Optional<Exam> exam = examService.findById(id);
            if (exam.isPresent()) {
                return success(exam.get(), "查询考试详情成功");
            } else {
                return error("考试不存在");
            }

        } catch (Exception e) {
            log.error("查询考试详情失败 - ID: {}", id, e);
            return error("查询考试详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建考试
     */
    @PostMapping
    @Operation(summary = "创建考试", description = "创建新的考试")
    @PreAuthorize("hasAuthority('exam:manage:add')")
    public ResponseEntity<ApiResponse<Exam>> createExam(
            @RequestBody Exam exam,
            HttpServletRequest request) {

        try {
            log.info("创建考试 - 名称: {}, 课程ID: {}", exam.getExamName(), exam.getCourseId());

            // 验证考试数据
            validateExam(exam);

            // 检查名称是否重复
            if (examService.existsByExamName(exam.getExamName())) {
                return error("考试名称已存在");
            }

            // 检查时间冲突
            if (exam.getClassroomId() != null && exam.getStartTime() != null && exam.getEndTime() != null) {
                if (examService.hasTimeConflict(exam.getClassroomId(), exam.getStartTime(), exam.getEndTime(), null)) {
                    return error("考试时间与其他考试冲突");
                }
            }

            // 设置默认值
            exam.setIsPublished(0);
            exam.setQuestionCount(0);

            Exam savedExam = examService.save(exam);
            return success(savedExam, "考试创建成功");

        } catch (Exception e) {
            log.error("创建考试失败: ", e);
            return error("创建考试失败: " + e.getMessage());
        }
    }

    /**
     * 更新考试
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新考试", description = "更新考试信息")
    @PreAuthorize("hasAuthority('exam:manage:edit')")
    public ResponseEntity<ApiResponse<Exam>> updateExam(
            @Parameter(description = "考试ID", required = true) @PathVariable Long id,
            @RequestBody Exam exam,
            HttpServletRequest request) {

        try {
            log.info("更新考试 - ID: {}, 名称: {}", id, exam.getExamName());

            validateId(id, "考试");

            // 检查考试是否存在
            Optional<Exam> existingOpt = examService.findById(id);
            if (!existingOpt.isPresent()) {
                return error("考试不存在");
            }

            Exam existing = existingOpt.get();

            // 检查是否可以修改
            if (!examService.canModifyExam(id)) {
                return error("考试已开始或已有考试记录，无法修改");
            }

            // 验证考试数据
            validateExam(exam);

            // 检查名称是否重复（排除当前记录）
            if (examService.existsByExamNameExcludeId(exam.getExamName(), id)) {
                return error("考试名称已存在");
            }

            // 检查时间冲突
            if (exam.getClassroomId() != null && exam.getStartTime() != null && exam.getEndTime() != null) {
                if (examService.hasTimeConflict(exam.getClassroomId(), exam.getStartTime(), exam.getEndTime(), id)) {
                    return error("考试时间与其他考试冲突");
                }
            }

            // 更新字段
            existing.setExamName(exam.getExamName());
            existing.setDescription(exam.getDescription());
            existing.setInstructions(exam.getInstructions());
            existing.setDurationMinutes(exam.getDurationMinutes());
            existing.setTotalScore(exam.getTotalScore());
            existing.setPassingScore(exam.getPassingScore());
            existing.setStartTime(exam.getStartTime());
            existing.setEndTime(exam.getEndTime());
            existing.setClassroomId(exam.getClassroomId());
            existing.setMaxParticipants(exam.getMaxParticipants());
            existing.setIsOnline(exam.getIsOnline());
            existing.setAllowReview(exam.getAllowReview());
            existing.setShuffleQuestions(exam.getShuffleQuestions());
            existing.setShuffleOptions(exam.getShuffleOptions());
            existing.setAntiCheatEnabled(exam.getAntiCheatEnabled());
            existing.setCameraRequired(exam.getCameraRequired());
            existing.setScreenLock(exam.getScreenLock());
            existing.setAutoSubmit(exam.getAutoSubmit());
            existing.setLateSubmissionAllowed(exam.getLateSubmissionAllowed());

            Exam updatedExam = examService.save(existing);
            return success(updatedExam, "考试更新成功");

        } catch (Exception e) {
            log.error("更新考试失败 - ID: {}", id, e);
            return error("更新考试失败: " + e.getMessage());
        }
    }

    /**
     * 删除考试
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除考试", description = "删除指定的考试")
    @PreAuthorize("hasAuthority('exam:manage:delete')")
    public ResponseEntity<ApiResponse<Void>> deleteExam(
            @Parameter(description = "考试ID", required = true) @PathVariable Long id) {

        try {
            log.info("删除考试 - ID: {}", id);

            validateId(id, "考试");

            // 检查考试是否存在
            Optional<Exam> exam = examService.findById(id);
            if (!exam.isPresent()) {
                return error("考试不存在");
            }

            // 检查是否可以删除
            if (!examService.canDeleteExam(id)) {
                return error("考试已有考试记录，无法删除");
            }

            examService.deleteById(id);
            return success(null, "考试删除成功");

        } catch (Exception e) {
            log.error("删除考试失败 - ID: {}", id, e);
            return error("删除考试失败: " + e.getMessage());
        }
    }

    // ==================== 业务操作 ====================

    /**
     * 发布考试
     */
    @PostMapping("/{id}/publish")
    @Operation(summary = "发布考试", description = "发布指定的考试")
    @PreAuthorize("hasAuthority('exam:manage:publish')")
    public ResponseEntity<ApiResponse<Void>> publishExam(
            @Parameter(description = "考试ID", required = true) @PathVariable Long id) {

        try {
            log.info("发布考试 - ID: {}", id);

            validateId(id, "考试");

            // 检查考试是否存在
            Optional<Exam> exam = examService.findById(id);
            if (!exam.isPresent()) {
                return error("考试不存在");
            }

            examService.publishExam(id);
            return success(null, "考试发布成功");

        } catch (Exception e) {
            log.error("发布考试失败 - ID: {}", id, e);
            return error("发布考试失败: " + e.getMessage());
        }
    }

    /**
     * 开始考试
     */
    @PostMapping("/{id}/start")
    @Operation(summary = "开始考试", description = "开始指定的考试")
    @PreAuthorize("hasAuthority('exam:manage:control')")
    public ResponseEntity<ApiResponse<Void>> startExam(
            @Parameter(description = "考试ID", required = true) @PathVariable Long id) {

        try {
            log.info("开始考试 - ID: {}", id);

            validateId(id, "考试");

            examService.startExam(id);
            return success(null, "考试开始成功");

        } catch (Exception e) {
            log.error("开始考试失败 - ID: {}", id, e);
            return error("开始考试失败: " + e.getMessage());
        }
    }

    /**
     * 结束考试
     */
    @PostMapping("/{id}/end")
    @Operation(summary = "结束考试", description = "结束指定的考试")
    @PreAuthorize("hasAuthority('exam:manage:control')")
    public ResponseEntity<ApiResponse<Void>> endExam(
            @Parameter(description = "考试ID", required = true) @PathVariable Long id) {

        try {
            log.info("结束考试 - ID: {}", id);

            validateId(id, "考试");

            examService.endExam(id);
            return success(null, "考试结束成功");

        } catch (Exception e) {
            log.error("结束考试失败 - ID: {}", id, e);
            return error("结束考试失败: " + e.getMessage());
        }
    }

    // ==================== 题目管理 ====================

    /**
     * 获取考试题目列表
     */
    @GetMapping("/{id}/questions")
    @Operation(summary = "获取考试题目列表", description = "获取指定考试的所有题目")
    @PreAuthorize("hasAuthority('exam:question:list')")
    public ResponseEntity<ApiResponse<List<ExamQuestion>>> getExamQuestions(
            @Parameter(description = "考试ID", required = true) @PathVariable Long id,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", example = "20") @RequestParam(defaultValue = "20") int size) {

        try {
            log.info("获取考试题目列表 - 考试ID: {}", id);

            validateId(id, "考试");

            // 创建分页对象
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("questionOrder"));

            Page<ExamQuestion> result = examService.getExamQuestions(id, pageable);
            return successPage(result, "获取考试题目列表成功");

        } catch (Exception e) {
            log.error("获取考试题目列表失败 - 考试ID: {}", id, e);
            return error("获取考试题目列表失败: " + e.getMessage());
        }
    }

    /**
     * 添加考试题目
     */
    @PostMapping("/{id}/questions")
    @Operation(summary = "添加考试题目", description = "为指定考试添加题目")
    @PreAuthorize("hasAuthority('exam:question:add')")
    public ResponseEntity<ApiResponse<ExamQuestion>> addExamQuestion(
            @Parameter(description = "考试ID", required = true) @PathVariable Long id,
            @RequestBody ExamQuestion question) {

        try {
            log.info("添加考试题目 - 考试ID: {}", id);

            validateId(id, "考试");

            ExamQuestion savedQuestion = examService.addQuestion(id, question);
            return success(savedQuestion, "添加考试题目成功");

        } catch (Exception e) {
            log.error("添加考试题目失败 - 考试ID: {}", id, e);
            return error("添加考试题目失败: " + e.getMessage());
        }
    }

    // ==================== 考试记录管理 ====================

    /**
     * 获取考试记录列表
     */
    @GetMapping("/{id}/records")
    @Operation(summary = "获取考试记录列表", description = "获取指定考试的所有考试记录")
    @PreAuthorize("hasAuthority('exam:record:list')")
    public ResponseEntity<ApiResponse<List<ExamRecord>>> getExamRecords(
            @Parameter(description = "考试ID", required = true) @PathVariable Long id,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", example = "20") @RequestParam(defaultValue = "20") int size) {

        try {
            log.info("获取考试记录列表 - 考试ID: {}", id);

            validateId(id, "考试");

            // 创建分页对象
            Pageable pageable = PageRequest.of(page - 1, size, Sort.by("startTime").descending());

            Page<ExamRecord> result = examService.getExamRecords(id, pageable);
            return successPage(result, "获取考试记录列表成功");

        } catch (Exception e) {
            log.error("获取考试记录列表失败 - 考试ID: {}", id, e);
            return error("获取考试记录列表失败: " + e.getMessage());
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 验证考试数据
     */
    private void validateExam(Exam exam) {
        if (exam.getExamName() == null || exam.getExamName().trim().isEmpty()) {
            throw new IllegalArgumentException("考试名称不能为空");
        }
        if (exam.getCourseId() == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        if (exam.getTeacherId() == null) {
            throw new IllegalArgumentException("教师ID不能为空");
        }
        if (exam.getDurationMinutes() == null || exam.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("考试时长必须大于0");
        }
        if (exam.getTotalScore() == null || exam.getTotalScore() <= 0) {
            throw new IllegalArgumentException("总分必须大于0");
        }
        if (exam.getStartTime() != null && exam.getEndTime() != null) {
            if (exam.getStartTime().isAfter(exam.getEndTime())) {
                throw new IllegalArgumentException("开始时间不能晚于结束时间");
            }
            if (exam.getStartTime().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("开始时间不能早于当前时间");
            }
        }
    }
}
