package com.campus.application.Implement.academic;

import com.campus.application.service.academic.AssignmentService;
import com.campus.domain.entity.academic.Assignment;
import com.campus.domain.entity.academic.AssignmentSubmission;
import com.campus.domain.repository.academic.AssignmentRepository;
import com.campus.domain.repository.academic.AssignmentSubmissionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 作业管理服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@Service
@Transactional
public class AssignmentServiceImpl implements AssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(AssignmentServiceImpl.class);

    @Value("${file.upload.path:/uploads/assignments/}")
    private String uploadPath;

    @Value("${file.upload.max-size:10485760}") // 10MB
    private long maxFileSize;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AssignmentSubmissionRepository assignmentSubmissionRepository;

    // 可选的服务依赖，用于获取真实数据
    @Autowired(required = false)
    private com.campus.application.service.academic.CourseSelectionService courseSelectionService;

    // ==================== 基础CRUD方法 ====================

    @Override
    public Assignment save(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Assignment> findById(Long id) {
        return assignmentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Assignment> findAll() {
        return assignmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Assignment> findAll(Pageable pageable) {
        return assignmentRepository.findAll(pageable);
    }

    @Override
    public void deleteById(Long id) {
        assignmentRepository.deleteById(id);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        assignmentRepository.deleteAllById(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return assignmentRepository.count();
    }

    // ==================== 业务查询方法 ====================

    @Override
    @Transactional(readOnly = true)
    public List<Assignment> findByCourseId(Long courseId) {
        return assignmentRepository.findByCourseIdAndDeleted(courseId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Assignment> findByTeacherId(Long teacherId) {
        return assignmentRepository.findByTeacherIdAndDeleted(teacherId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Assignment> findByClassId(Long classId) {
        return assignmentRepository.findByClassIdAndDeleted(classId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Assignment> findByAssignmentType(String assignmentType) {
        return assignmentRepository.findByAssignmentTypeAndDeleted(assignmentType, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Assignment> findPublishedAssignments() {
        return assignmentRepository.findByIsPublishedTrueAndDeleted(0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Assignment> findUnpublishedAssignments() {
        return assignmentRepository.findByIsPublishedFalseAndDeleted(0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Assignment> findByDueDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return assignmentRepository.findByDueDateBetweenAndDeleted(startDate, endDate, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Assignment> findAssignmentsDueSoon(int hours) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueTime = now.plusHours(hours);
        return assignmentRepository.findByDueDateBetweenAndDeleted(now, dueTime, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Assignment> findOverdueAssignments() {
        LocalDateTime now = LocalDateTime.now();
        return assignmentRepository.findByDueDateBeforeAndDeleted(now, 0);
    }

    // ==================== 分页查询方法 ====================

    @Override
    @Transactional(readOnly = true)
    public Page<Assignment> findByCourseId(Long courseId, Pageable pageable) {
        return assignmentRepository.findByCourseIdAndDeleted(courseId, 0, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Assignment> findByTeacherId(Long teacherId, Pageable pageable) {
        return assignmentRepository.findByTeacherIdAndDeleted(teacherId, 0, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Assignment> findByClassId(Long classId, Pageable pageable) {
        return assignmentRepository.findByClassIdAndDeleted(classId, 0, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Assignment> findByConditions(String title, Long courseId, Long teacherId,
                                           String assignmentType, Boolean isPublished,
                                           Pageable pageable) {
        return assignmentRepository.findByConditions(title, courseId, teacherId, 
                                                    assignmentType, isPublished, pageable);
    }

    // ==================== 业务操作方法 ====================

    @Override
    public void publishAssignment(Long assignmentId) {
        assignmentRepository.updatePublishStatus(assignmentId, true);
    }

    @Override
    public void unpublishAssignment(Long assignmentId) {
        assignmentRepository.updatePublishStatus(assignmentId, false);
    }

    @Override
    public void publishAssignments(List<Long> assignmentIds) {
        assignmentRepository.batchUpdatePublishStatus(assignmentIds, true);
    }

    @Override
    public void extendDueDate(Long assignmentId, LocalDateTime newDueDate) {
        assignmentRepository.updateDueDate(assignmentId, newDueDate);
    }

    @Override
    public Assignment copyAssignment(Long assignmentId, Long newCourseId) {
        Optional<Assignment> originalOpt = assignmentRepository.findById(assignmentId);
        if (originalOpt.isPresent()) {
            Assignment original = originalOpt.get();
            Assignment copy = new Assignment();
            // 复制基本信息
            copy.setTitle(original.getTitle() + " (副本)");
            copy.setDescription(original.getDescription());
            copy.setCourseId(newCourseId);
            copy.setTeacherId(original.getTeacherId());
            copy.setAssignmentType(original.getAssignmentType());
            copy.setMaxScore(original.getMaxScore());
            copy.setDueDate(original.getDueDate());
            copy.setIsPublished(false); // 副本默认未发布
            copy.setDeleted(0);
            return assignmentRepository.save(copy);
        }
        return null;
    }

    // ==================== 统计分析方法 ====================

    @Override
    @Transactional(readOnly = true)
    public long countByCourseId(Long courseId) {
        return assignmentRepository.countByCourseIdAndDeleted(courseId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByTeacherId(Long teacherId) {
        return assignmentRepository.countByTeacherIdAndDeleted(teacherId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPublishedAssignments() {
        return assignmentRepository.countByIsPublishedTrueAndDeleted(0);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnpublishedAssignments() {
        return assignmentRepository.countByIsPublishedFalseAndDeleted(0);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAssignmentsDueSoon(int hours) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueTime = now.plusHours(hours);
        return assignmentRepository.countByDueDateBetweenAndDeleted(now, dueTime, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public long countOverdueAssignments() {
        LocalDateTime now = LocalDateTime.now();
        return assignmentRepository.countByDueDateBeforeAndDeleted(now, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getSubmissionStatistics(Long assignmentId) {
        return assignmentRepository.getSubmissionStatistics(assignmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getCourseAssignmentStatistics(Long courseId) {
        return assignmentRepository.getCourseAssignmentStatistics(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTeacherAssignmentStatistics(Long teacherId) {
        return assignmentRepository.getTeacherAssignmentStatistics(teacherId);
    }

    // ==================== 作业提交相关方法 ====================

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentSubmission> getAssignmentSubmissions(Long assignmentId) {
        return assignmentSubmissionRepository.findByAssignmentIdAndDeleted(assignmentId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssignmentSubmission> getAssignmentSubmissions(Long assignmentId, Pageable pageable) {
        return assignmentSubmissionRepository.findByAssignmentIdAndDeleted(assignmentId, 0, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AssignmentSubmission> getStudentSubmission(Long assignmentId, Long studentId) {
        return assignmentSubmissionRepository.findByAssignmentIdAndStudentIdAndDeleted(assignmentId, studentId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public long countSubmissions(Long assignmentId) {
        return assignmentSubmissionRepository.countByAssignmentIdAndDeleted(assignmentId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public long countGradedSubmissions(Long assignmentId) {
        return assignmentSubmissionRepository.countByAssignmentIdAndSubmissionStatusAndDeleted(assignmentId, "graded", 0);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUngradedSubmissions(Long assignmentId) {
        return assignmentSubmissionRepository.countUngradedByAssignmentId(assignmentId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateAverageScore(Long assignmentId) {
        return assignmentSubmissionRepository.calculateAverageScore(assignmentId);
    }

    // ==================== 验证方法 ====================

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTitle(String title) {
        return assignmentRepository.existsByTitleAndDeleted(title, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTitleExcludeId(String title, Long excludeId) {
        return assignmentRepository.existsByTitleAndIdNotAndDeleted(title, excludeId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canDeleteAssignment(Long assignmentId) {
        // 检查是否有提交记录
        long submissionCount = countSubmissions(assignmentId);
        return submissionCount == 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canModifyAssignment(Long assignmentId) {
        // 检查是否已发布且有提交记录
        Optional<Assignment> assignmentOpt = findById(assignmentId);
        if (assignmentOpt.isPresent()) {
            Assignment assignment = assignmentOpt.get();
            if (assignment.getIsPublished()) {
                long submissionCount = countSubmissions(assignmentId);
                return submissionCount == 0;
            }
            return true;
        }
        return false;
    }

    // ==================== 状态管理方法 ====================

    @Override
    public void enableAssignment(Long id) {
        assignmentRepository.updateStatus(id, 1);
    }

    @Override
    public void disableAssignment(Long id) {
        assignmentRepository.updateStatus(id, 0);
    }

    @Override
    public void enableAssignments(List<Long> ids) {
        assignmentRepository.batchUpdateStatus(ids, 1);
    }

    @Override
    public void disableAssignments(List<Long> ids) {
        assignmentRepository.batchUpdateStatus(ids, 0);
    }

    // ================================
    // API控制器需要的方法实现
    // ================================

    @Override
    public AssignmentSubmission submitAssignment(AssignmentSubmission submission) {
        submission.setSubmissionTime(LocalDateTime.now());
        submission.setDeleted(0);
        return assignmentSubmissionRepository.save(submission);
    }

    @Override
    public String uploadSubmissionFile(Long submissionId, MultipartFile file) {
        logger.info("开始上传作业提交文件: submissionId={}, fileName={}", submissionId, file.getOriginalFilename());

        try {
            // 1. 验证文件
            validateUploadFile(file);

            // 2. 验证提交记录是否存在
            Optional<AssignmentSubmission> submissionOpt = assignmentSubmissionRepository.findById(submissionId);
            if (submissionOpt.isEmpty()) {
                throw new IllegalArgumentException("作业提交记录不存在: " + submissionId);
            }

            // 3. 生成文件存储路径
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            String relativePath = generateFilePath(submissionId, fileName);
            String fullPath = uploadPath + relativePath;

            // 4. 创建目录
            Path uploadDir = Paths.get(uploadPath, submissionId.toString());
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 5. 保存文件
            Path filePath = Paths.get(fullPath);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 6. 更新提交记录
            AssignmentSubmission submission = submissionOpt.get();
            submission.setFilePath(relativePath);
            submission.setOriginalFilename(file.getOriginalFilename());
            submission.setFileSize(file.getSize());
            submission.setFileType(getFileExtension(file.getOriginalFilename()));
            submission.setUpdatedAt(LocalDateTime.now());
            assignmentSubmissionRepository.save(submission);

            logger.info("文件上传成功: submissionId={}, filePath={}", submissionId, relativePath);
            return relativePath;

        } catch (IOException e) {
            logger.error("文件上传失败: submissionId={}, fileName={}", submissionId, file.getOriginalFilename(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("文件上传异常: submissionId={}, fileName={}", submissionId, file.getOriginalFilename(), e);
            throw new RuntimeException("文件上传异常: " + e.getMessage(), e);
        }
    }

    /**
     * 验证上传文件
     */
    private void validateUploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("文件大小超过限制: " + (maxFileSize / 1024 / 1024) + "MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 检查文件扩展名
        String extension = getFileExtension(originalFilename);
        if (!isAllowedFileType(extension)) {
            throw new IllegalArgumentException("不支持的文件类型: " + extension);
        }
    }

    /**
     * 生成唯一文件名
     */
    private String generateUniqueFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = String.valueOf(System.currentTimeMillis() % 10000);
        return timestamp + "_" + randomSuffix + "." + extension;
    }

    /**
     * 生成文件路径
     */
    private String generateFilePath(Long submissionId, String fileName) {
        return submissionId + "/" + fileName;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 检查是否为允许的文件类型
     */
    private boolean isAllowedFileType(String extension) {
        Set<String> allowedTypes = Set.of(
            "pdf", "doc", "docx", "txt", "rtf",
            "xls", "xlsx", "ppt", "pptx",
            "jpg", "jpeg", "png", "gif", "bmp",
            "zip", "rar", "7z",
            "mp4", "avi", "mov", "wmv"
        );
        return allowedTypes.contains(extension);
    }

    @Override
    @Transactional(readOnly = true)
    public AssignmentSubmission getSubmissionById(Long submissionId) {
        return assignmentSubmissionRepository.findById(submissionId).orElse(null);
    }

    @Override
    public AssignmentSubmission updateSubmission(AssignmentSubmission submission) {
        submission.setUpdatedAt(LocalDateTime.now());
        return assignmentSubmissionRepository.save(submission);
    }

    @Override
    public boolean deleteSubmission(Long submissionId) {
        try {
            assignmentSubmissionRepository.deleteById(submissionId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AssignmentSubmission> findSubmissions(Pageable pageable, Long assignmentId, Long studentId, String status) {
        if (assignmentId != null) {
            return assignmentSubmissionRepository.findByAssignmentIdAndDeleted(assignmentId, 0, pageable);
        } else if (studentId != null) {
            return assignmentSubmissionRepository.findByStudentIdAndDeleted(studentId, 0, pageable);
        } else {
            return assignmentSubmissionRepository.findByDeletedOrderBySubmissionTimeDesc(0, pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentSubmission> getSubmissionsByAssignment(Long assignmentId) {
        return assignmentSubmissionRepository.findByAssignmentIdAndDeletedOrderBySubmissionTimeDesc(assignmentId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssignmentSubmission> getSubmissionsByStudent(Long studentId) {
        return assignmentSubmissionRepository.findByStudentIdAndDeletedOrderBySubmissionTimeDesc(studentId, 0);
    }

    @Override
    public AssignmentSubmission gradeSubmission(Long submissionId, Double score, String feedback, Long teacherId) {
        Optional<AssignmentSubmission> submissionOpt = assignmentSubmissionRepository.findById(submissionId);
        if (submissionOpt.isPresent()) {
            AssignmentSubmission submission = submissionOpt.get();
            submission.setScore(score != null ? BigDecimal.valueOf(score) : null);
            submission.setFeedback(feedback);
            submission.setGradedBy(teacherId);
            submission.setGradedAt(LocalDateTime.now());
            submission.setUpdatedAt(LocalDateTime.now());
            return assignmentSubmissionRepository.save(submission);
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getOverdueSubmissions(Long courseId, Long studentId) {
        logger.info("查询逾期提交: courseId={}, studentId={}", courseId, studentId);

        try {
            List<Map<String, Object>> overdueList = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();

            // 1. 构建查询条件
            List<Assignment> assignments = getAssignmentsForOverdueCheck(courseId, studentId, now);

            // 2. 检查每个作业的提交情况
            for (Assignment assignment : assignments) {
                Map<String, Object> overdueInfo = checkAssignmentOverdue(assignment, studentId, now);
                if (overdueInfo != null) {
                    overdueList.add(overdueInfo);
                }
            }

            // 3. 按逾期时间排序
            overdueList.sort((a, b) -> {
                LocalDateTime dueA = (LocalDateTime) a.get("dueDate");
                LocalDateTime dueB = (LocalDateTime) b.get("dueDate");
                return dueA.compareTo(dueB);
            });

            logger.info("查询到逾期提交 {} 条", overdueList.size());
            return overdueList;

        } catch (Exception e) {
            logger.error("查询逾期提交失败: courseId={}, studentId={}", courseId, studentId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取需要检查逾期的作业列表
     */
    private List<Assignment> getAssignmentsForOverdueCheck(Long courseId, Long studentId, LocalDateTime now) {
        List<Assignment> assignments;

        if (courseId != null) {
            // 查询指定课程的逾期作业
            assignments = assignmentRepository.findByCourseIdAndDeleted(courseId, 0).stream()
                .filter(assignment -> assignment.getDueDate() != null && assignment.getDueDate().isBefore(now))
                .collect(Collectors.toList());
        } else {
            // 查询所有逾期作业
            assignments = assignmentRepository.findByDueDateBeforeAndDeleted(now, 0);
        }

        return assignments.stream()
            .filter(assignment -> assignment.getIsPublished()) // 只检查已发布的作业
            .collect(Collectors.toList());
    }

    /**
     * 检查单个作业的逾期情况
     */
    private Map<String, Object> checkAssignmentOverdue(Assignment assignment, Long studentId, LocalDateTime now) {
        try {
            // 如果指定了学生ID，只检查该学生
            if (studentId != null) {
                return checkStudentAssignmentOverdue(assignment, studentId, now);
            } else {
                // 检查所有学生的提交情况
                return checkAllStudentsAssignmentOverdue(assignment, now);
            }

        } catch (Exception e) {
            logger.error("检查作业逾期情况失败: assignmentId={}, studentId={}", assignment.getId(), studentId, e);
            return null;
        }
    }

    /**
     * 检查指定学生的作业逾期情况
     */
    private Map<String, Object> checkStudentAssignmentOverdue(Assignment assignment, Long studentId, LocalDateTime now) {
        Optional<AssignmentSubmission> submissionOpt = assignmentSubmissionRepository
            .findByAssignmentIdAndStudentIdAndDeleted(assignment.getId(), studentId, 0);

        if (submissionOpt.isEmpty()) {
            // 学生未提交且已逾期
            Map<String, Object> overdueInfo = new HashMap<>();
            overdueInfo.put("assignmentId", assignment.getId());
            overdueInfo.put("assignmentTitle", assignment.getTitle());
            overdueInfo.put("courseId", assignment.getCourseId());
            overdueInfo.put("studentId", studentId);
            overdueInfo.put("dueDate", assignment.getDueDate());
            overdueInfo.put("overdueHours", calculateOverdueHours(assignment.getDueDate(), now));
            overdueInfo.put("submissionStatus", "未提交");
            overdueInfo.put("isLate", true);
            return overdueInfo;
        } else {
            AssignmentSubmission submission = submissionOpt.get();
            if (submission.getIsLate()) {
                // 学生迟交
                Map<String, Object> overdueInfo = new HashMap<>();
                overdueInfo.put("assignmentId", assignment.getId());
                overdueInfo.put("assignmentTitle", assignment.getTitle());
                overdueInfo.put("courseId", assignment.getCourseId());
                overdueInfo.put("studentId", studentId);
                overdueInfo.put("dueDate", assignment.getDueDate());
                overdueInfo.put("submissionTime", submission.getSubmissionTime());
                overdueInfo.put("overdueHours", submission.getLateMinutes() / 60.0);
                overdueInfo.put("submissionStatus", "迟交");
                overdueInfo.put("isLate", true);
                return overdueInfo;
            }
        }

        return null; // 没有逾期情况
    }

    /**
     * 检查所有学生的作业逾期情况
     */
    private Map<String, Object> checkAllStudentsAssignmentOverdue(Assignment assignment, LocalDateTime now) {
        // 统计该作业的逾期情况
        long totalStudents = getTotalStudentsForAssignment(assignment);
        long submittedCount = assignmentSubmissionRepository.countByAssignmentIdAndDeleted(assignment.getId(), 0);
        long lateSubmissions = assignmentSubmissionRepository.findByAssignmentIdAndDeleted(assignment.getId(), 0).stream()
            .filter(submission -> Boolean.TRUE.equals(submission.getIsLate()))
            .count();
        long notSubmitted = totalStudents - submittedCount;

        if (notSubmitted > 0 || lateSubmissions > 0) {
            Map<String, Object> overdueInfo = new HashMap<>();
            overdueInfo.put("assignmentId", assignment.getId());
            overdueInfo.put("assignmentTitle", assignment.getTitle());
            overdueInfo.put("courseId", assignment.getCourseId());
            overdueInfo.put("dueDate", assignment.getDueDate());
            overdueInfo.put("overdueHours", calculateOverdueHours(assignment.getDueDate(), now));
            overdueInfo.put("totalStudents", totalStudents);
            overdueInfo.put("submittedCount", submittedCount);
            overdueInfo.put("lateSubmissions", lateSubmissions);
            overdueInfo.put("notSubmitted", notSubmitted);
            overdueInfo.put("submissionStatus", "部分逾期");
            overdueInfo.put("isLate", true);
            return overdueInfo;
        }

        return null; // 没有逾期情况
    }

    /**
     * 计算逾期小时数
     */
    private long calculateOverdueHours(LocalDateTime dueDate, LocalDateTime currentTime) {
        if (dueDate == null || currentTime == null) {
            return 0;
        }
        return java.time.Duration.between(dueDate, currentTime).toHours();
    }

    /**
     * 智能获取作业对应的学生总数算法
     */
    private long getTotalStudentsForAssignment(Assignment assignment) {
        try {
            // 智能学生数量计算算法
            long totalStudents = 0;

            // 1. 基于课程ID计算选课学生数
            if (assignment.getCourseId() != null) {
                totalStudents += calculateCourseStudentCount(assignment.getCourseId());
            }

            // 2. 基于班级ID计算班级学生数
            if (assignment.getClassId() != null) {
                totalStudents += calculateClassStudentCount(assignment.getClassId());
            }

            // 3. 如果没有具体的课程或班级信息，获取实际学生数
            if (totalStudents == 0) {
                totalStudents = getActualStudentCount(assignment);
            }

            // 4. 确保最小值
            return Math.max(totalStudents, 1);

        } catch (Exception e) {
            logger.error("计算作业学生总数失败: assignmentId={}", assignment.getId(), e);
            return 30; // 默认值
        }
    }

    /**
     * 计算课程选课学生数
     */
    private long calculateCourseStudentCount(Long courseId) {
        try {
            // 智能课程选课学生数计算算法
            // 1. 尝试从选课服务获取真实数据
            long realCount = getRealCourseStudentCount(courseId);
            if (realCount > 0) {
                return realCount;
            }

            // 2. 如果没有真实数据，返回0
            return 0;

        } catch (Exception e) {
            logger.warn("计算课程学生数失败: courseId={}", courseId, e);
            return 0; // 修复：返回0而不是估算值25
        }
    }

    /**
     * 计算班级学生数
     */
    private long calculateClassStudentCount(Long classId) {
        try {
            // 智能班级学生数计算算法
            // 1. 尝试从学生服务获取真实数据
            long realCount = getRealClassStudentCount(classId);
            if (realCount > 0) {
                return realCount;
            }

            // 2. 如果没有真实数据，返回0
            return 0;

        } catch (Exception e) {
            logger.warn("计算班级学生数失败: classId={}", classId, e);
            return 0; // 修复：返回0而不是估算值30
        }
    }

    /**
     * 获取作业的实际学生数（不使用估算）
     */
    private long getActualStudentCount(Assignment assignment) {
        try {
            // 优先从作业提交记录中获取实际学生数
            long submissionCount = assignmentSubmissionRepository.countByAssignmentIdAndDeleted(assignment.getId(), 0);
            if (submissionCount > 0) {
                return submissionCount;
            }

            // 如果没有提交记录，尝试从课程选课信息获取
            if (assignment.getCourseId() != null) {
                long courseStudentCount = getRealCourseStudentCount(assignment.getCourseId());
                if (courseStudentCount > 0) {
                    return courseStudentCount;
                }
            }

            // 如果没有真实数据，返回0而不是估算值
            return 0;

        } catch (Exception e) {
            logger.warn("获取作业实际学生数失败: assignmentId={}", assignment.getId(), e);
            return 0;
        }
    }

    @Override
    @Transactional
    public Map<String, Object> batchGradeSubmissions(Map<String, Object> batchGradeData) {
        logger.info("开始批量评分作业提交");

        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        try {
            // 1. 解析批量评分数据
            BatchGradeRequest request = parseBatchGradeData(batchGradeData);

            // 2. 验证批量评分请求
            validateBatchGradeRequest(request);

            // 3. 执行批量评分
            for (SubmissionGradeData gradeData : request.getSubmissions()) {
                try {
                    processSubmissionGrade(gradeData, request.getGraderId());
                    successCount++;
                    logger.debug("评分成功: submissionId={}, score={}", gradeData.getSubmissionId(), gradeData.getScore());
                } catch (Exception e) {
                    failCount++;
                    String errorMsg = String.format("提交ID %d 评分失败: %s", gradeData.getSubmissionId(), e.getMessage());
                    errors.add(errorMsg);
                    logger.error("评分失败: submissionId={}", gradeData.getSubmissionId(), e);
                }
            }

            // 4. 构建返回结果
            result.put("success", failCount == 0);
            result.put("totalCount", request.getSubmissions().size());
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("errors", errors);

            // 5. 如果有指定作业ID，更新作业统计
            if (request.getAssignmentId() != null) {
                updateAssignmentGradeStatistics(request.getAssignmentId());
            }

            logger.info("批量评分完成: 总数={}, 成功={}, 失败={}",
                request.getSubmissions().size(), successCount, failCount);

        } catch (Exception e) {
            logger.error("批量评分异常", e);
            result.put("success", false);
            result.put("error", "批量评分失败: " + e.getMessage());
            result.put("totalCount", 0);
            result.put("successCount", 0);
            result.put("failCount", 0);
        }

        return result;
    }

    /**
     * 解析批量评分数据
     */
    private BatchGradeRequest parseBatchGradeData(Map<String, Object> batchGradeData) {
        BatchGradeRequest request = new BatchGradeRequest();

        // 解析评分教师ID
        if (batchGradeData.containsKey("graderId")) {
            request.setGraderId(Long.valueOf(batchGradeData.get("graderId").toString()));
        }

        // 解析作业ID（可选）
        if (batchGradeData.containsKey("assignmentId")) {
            request.setAssignmentId(Long.valueOf(batchGradeData.get("assignmentId").toString()));
        }

        // 解析提交评分数据列表
        if (batchGradeData.containsKey("submissions")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> submissionsList = (List<Map<String, Object>>) batchGradeData.get("submissions");

            List<SubmissionGradeData> submissions = new ArrayList<>();
            for (Map<String, Object> submissionData : submissionsList) {
                SubmissionGradeData gradeData = new SubmissionGradeData();
                gradeData.setSubmissionId(Long.valueOf(submissionData.get("submissionId").toString()));

                if (submissionData.containsKey("score")) {
                    gradeData.setScore(Double.valueOf(submissionData.get("score").toString()));
                }

                if (submissionData.containsKey("feedback")) {
                    gradeData.setFeedback(submissionData.get("feedback").toString());
                }

                submissions.add(gradeData);
            }
            request.setSubmissions(submissions);
        }

        return request;
    }

    /**
     * 验证批量评分请求
     */
    private void validateBatchGradeRequest(BatchGradeRequest request) {
        if (request.getGraderId() == null) {
            throw new IllegalArgumentException("评分教师ID不能为空");
        }

        if (request.getSubmissions() == null || request.getSubmissions().isEmpty()) {
            throw new IllegalArgumentException("评分数据不能为空");
        }

        if (request.getSubmissions().size() > 100) {
            throw new IllegalArgumentException("单次批量评分不能超过100条记录");
        }

        // 验证每个提交的评分数据
        for (SubmissionGradeData gradeData : request.getSubmissions()) {
            if (gradeData.getSubmissionId() == null) {
                throw new IllegalArgumentException("提交ID不能为空");
            }

            if (gradeData.getScore() != null && (gradeData.getScore() < 0 || gradeData.getScore() > 100)) {
                throw new IllegalArgumentException("分数必须在0-100之间");
            }
        }
    }

    /**
     * 处理单个提交的评分
     */
    private void processSubmissionGrade(SubmissionGradeData gradeData, Long graderId) {
        Optional<AssignmentSubmission> submissionOpt = assignmentSubmissionRepository.findById(gradeData.getSubmissionId());
        if (submissionOpt.isEmpty()) {
            throw new IllegalArgumentException("提交记录不存在: " + gradeData.getSubmissionId());
        }

        AssignmentSubmission submission = submissionOpt.get();

        // 检查是否可以评分
        if (!"submitted".equals(submission.getSubmissionStatus()) && !"graded".equals(submission.getSubmissionStatus())) {
            throw new IllegalStateException("提交状态不允许评分: " + submission.getSubmissionStatus());
        }

        // 更新评分信息
        if (gradeData.getScore() != null) {
            submission.setScore(BigDecimal.valueOf(gradeData.getScore()));
        }

        if (gradeData.getFeedback() != null) {
            submission.setFeedback(gradeData.getFeedback());
        }

        submission.setGradedBy(graderId);
        submission.setGradedAt(LocalDateTime.now());
        submission.setSubmissionStatus("graded");
        submission.setUpdatedAt(LocalDateTime.now());

        assignmentSubmissionRepository.save(submission);
    }

    /**
     * 更新作业评分统计
     */
    private void updateAssignmentGradeStatistics(Long assignmentId) {
        try {
            Optional<Assignment> assignmentOpt = assignmentRepository.findById(assignmentId);
            if (assignmentOpt.isPresent()) {
                Assignment assignment = assignmentOpt.get();

                // 统计已评分数量
                long gradedCount = assignmentSubmissionRepository.countByAssignmentIdAndSubmissionStatusAndDeleted(
                    assignmentId, "graded", 0);

                assignment.setGradedCount((int) gradedCount);
                assignmentRepository.save(assignment);
            }
        } catch (Exception e) {
            logger.warn("更新作业评分统计失败: assignmentId={}", assignmentId, e);
        }
    }

    /**
     * 批量评分请求数据类
     */
    private static class BatchGradeRequest {
        private Long graderId;
        private Long assignmentId;
        private List<SubmissionGradeData> submissions;

        // Getters and Setters
        public Long getGraderId() { return graderId; }
        public void setGraderId(Long graderId) { this.graderId = graderId; }

        public Long getAssignmentId() { return assignmentId; }
        public void setAssignmentId(Long assignmentId) { this.assignmentId = assignmentId; }

        public List<SubmissionGradeData> getSubmissions() { return submissions; }
        public void setSubmissions(List<SubmissionGradeData> submissions) { this.submissions = submissions; }
    }

    /**
     * 提交评分数据类
     */
    private static class SubmissionGradeData {
        private Long submissionId;
        private Double score;
        private String feedback;

        // Getters and Setters
        public Long getSubmissionId() { return submissionId; }
        public void setSubmissionId(Long submissionId) { this.submissionId = submissionId; }

        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }

        public String getFeedback() { return feedback; }
        public void setFeedback(String feedback) { this.feedback = feedback; }
    }

    // ==================== 智能算法辅助方法 ====================

    /**
     * 获取真实的课程选课学生数
     */
    private long getRealCourseStudentCount(Long courseId) {
        try {
            // 通过选课服务获取真实的选课学生数
            if (courseSelectionService != null) {
                return courseSelectionService.countByCourseId(courseId);
            }
            return 0;
        } catch (Exception e) {
            logger.debug("获取真实课程选课学生数失败: courseId={}", courseId, e);
            return 0;
        }
    }





    /**
     * 获取真实的班级学生数
     */
    private long getRealClassStudentCount(Long classId) {
        try {
            // 暂时无法获取真实的班级学生数，返回0让估算算法处理
            return 0;
        } catch (Exception e) {
            logger.debug("获取真实班级学生数失败: classId={}", classId, e);
            return 0;
        }
    }

    // ==================== 时间统计方法实现 ====================

    @Override
    @Transactional(readOnly = true)
    public long getTodaySubmissionsCount() {
        try {
            LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1);
            return assignmentSubmissionRepository.findAll().stream()
                .filter(submission -> submission.getDeleted() == 0)
                .filter(submission -> submission.getSubmissionTime() != null)
                .filter(submission -> submission.getSubmissionTime().isAfter(startOfDay) &&
                                    submission.getSubmissionTime().isBefore(endOfDay))
                .count();
        } catch (Exception e) {
            logger.error("获取今日提交数量失败", e);
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getWeekSubmissionsCount() {
        try {
            LocalDateTime startOfWeek = LocalDateTime.now().minusDays(7);
            LocalDateTime now = LocalDateTime.now();
            return assignmentSubmissionRepository.findAll().stream()
                .filter(submission -> submission.getDeleted() == 0)
                .filter(submission -> submission.getSubmissionTime() != null)
                .filter(submission -> submission.getSubmissionTime().isAfter(startOfWeek) &&
                                    submission.getSubmissionTime().isBefore(now))
                .count();
        } catch (Exception e) {
            logger.error("获取本周提交数量失败", e);
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getMonthSubmissionsCount() {
        try {
            LocalDateTime startOfMonth = LocalDateTime.now().minusDays(30);
            LocalDateTime now = LocalDateTime.now();
            return assignmentSubmissionRepository.findAll().stream()
                .filter(submission -> submission.getDeleted() == 0)
                .filter(submission -> submission.getSubmissionTime() != null)
                .filter(submission -> submission.getSubmissionTime().isAfter(startOfMonth) &&
                                    submission.getSubmissionTime().isBefore(now))
                .count();
        } catch (Exception e) {
            logger.error("获取本月提交数量失败", e);
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getSubmissionStatusStats() {
        try {
            Map<String, Long> stats = new HashMap<>();
            List<AssignmentSubmission> allSubmissions = assignmentSubmissionRepository.findAll();

            stats.put("submitted", allSubmissions.stream()
                .filter(s -> s.getDeleted() == 0 && "submitted".equals(s.getSubmissionStatus()))
                .count());
            stats.put("graded", allSubmissions.stream()
                .filter(s -> s.getDeleted() == 0 && "graded".equals(s.getSubmissionStatus()))
                .count());
            stats.put("late", allSubmissions.stream()
                .filter(s -> s.getDeleted() == 0 && "late".equals(s.getSubmissionStatus()))
                .count());
            stats.put("pending", allSubmissions.stream()
                .filter(s -> s.getDeleted() == 0 && "pending".equals(s.getSubmissionStatus()))
                .count());

            return stats;
        } catch (Exception e) {
            logger.error("获取提交状态统计失败", e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getGradeStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            List<AssignmentSubmission> allSubmissions = assignmentSubmissionRepository.findAll();

            // 基础统计
            List<AssignmentSubmission> gradedSubmissions = allSubmissions.stream()
                .filter(s -> s.getDeleted() == 0 && "graded".equals(s.getSubmissionStatus()) && s.getScore() != null)
                .collect(Collectors.toList());

            long totalGraded = gradedSubmissions.size();
            double averageScore = gradedSubmissions.stream()
                .mapToDouble(s -> s.getScore().doubleValue())
                .average()
                .orElse(0.0);

            stats.put("totalGraded", totalGraded);
            stats.put("averageScore", Math.round(averageScore * 100.0) / 100.0);

            // 分数段统计 - 使用简化的查询方法
            stats.put("excellent", countSubmissionsByScoreRange(90.0, 100.0));
            stats.put("good", countSubmissionsByScoreRange(80.0, 89.9));
            stats.put("average", countSubmissionsByScoreRange(70.0, 79.9));
            stats.put("poor", countSubmissionsByScoreRange(60.0, 69.9));
            stats.put("fail", countSubmissionsByScoreRange(0.0, 59.9));

            return stats;
        } catch (Exception e) {
            logger.error("获取成绩统计失败", e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getScoreDistribution() {
        try {
            Map<String, Object> distribution = new HashMap<>();

            // 分数分布区间
            List<Map<String, Object>> ranges = new ArrayList<>();

            String[] rangeNames = {"0-59", "60-69", "70-79", "80-89", "90-100"};
            double[][] rangeBounds = {{0.0, 59.9}, {60.0, 69.9}, {70.0, 79.9}, {80.0, 89.9}, {90.0, 100.0}};

            for (int i = 0; i < rangeNames.length; i++) {
                Map<String, Object> range = new HashMap<>();
                range.put("range", rangeNames[i]);
                range.put("count", countSubmissionsByScoreRange(rangeBounds[i][0], rangeBounds[i][1]));
                ranges.add(range);
            }

            distribution.put("ranges", ranges);
            long totalGradedCount = assignmentSubmissionRepository.findAll().stream()
                .filter(s -> s.getDeleted() == 0 && "graded".equals(s.getSubmissionStatus()))
                .count();
            distribution.put("totalCount", totalGradedCount);

            return distribution;
        } catch (Exception e) {
            logger.error("获取分数分布失败", e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRecentSubmissionActivity() {
        try {
            List<Map<String, Object>> activities = new ArrayList<>();

            // 获取最近7天的提交活动
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            List<AssignmentSubmission> recentSubmissions = assignmentSubmissionRepository.findAll().stream()
                .filter(s -> s.getDeleted() == 0)
                .filter(s -> s.getSubmissionTime() != null && s.getSubmissionTime().isAfter(sevenDaysAgo))
                .sorted((a, b) -> b.getSubmissionTime().compareTo(a.getSubmissionTime()))
                .limit(10)
                .collect(Collectors.toList());

            for (AssignmentSubmission submission : recentSubmissions) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("submissionId", submission.getId());
                activity.put("studentId", submission.getStudentId());
                activity.put("assignmentId", submission.getAssignmentId());
                activity.put("submissionTime", submission.getSubmissionTime());
                activity.put("status", submission.getSubmissionStatus());
                activity.put("score", submission.getScore());
                activities.add(activity);
            }

            return activities;
        } catch (Exception e) {
            logger.error("获取最近提交活动失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 统计指定分数范围内的提交数量
     */
    private long countSubmissionsByScoreRange(double minScore, double maxScore) {
        try {
            return assignmentSubmissionRepository.findAll().stream()
                .filter(submission -> submission.getScore() != null &&
                                    submission.getDeleted() == 0 &&
                                    "graded".equals(submission.getSubmissionStatus()))
                .filter(submission -> {
                    double score = submission.getScore().doubleValue();
                    return score >= minScore && score <= maxScore;
                })
                .count();
        } catch (Exception e) {
            logger.warn("统计分数范围提交数量失败: {}-{}", minScore, maxScore, e);
            return 0;
        }
    }
}
