package com.campus.application.Implement.academic;

import com.campus.application.service.academic.AssignmentService;
import com.campus.domain.entity.academic.Assignment;
import com.campus.domain.entity.academic.AssignmentSubmission;
import com.campus.domain.repository.academic.AssignmentRepository;
import com.campus.domain.repository.academic.AssignmentSubmissionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Collections;

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

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AssignmentSubmissionRepository assignmentSubmissionRepository;

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
    public String uploadSubmissionFile(Long submissionId, org.springframework.web.multipart.MultipartFile file) {
        // 简化实现：返回文件URL
        return "/uploads/assignments/" + submissionId + "/" + file.getOriginalFilename();
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
        // 简化实现：返回空列表
        return Collections.emptyList();
    }

    @Override
    public Map<String, Object> batchGradeSubmissions(Map<String, Object> batchGradeData) {
        // 简化实现：返回成功结果
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("processedCount", 0);
        return result;
    }
}
