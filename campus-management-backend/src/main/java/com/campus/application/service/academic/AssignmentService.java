package com.campus.application.service.academic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.campus.domain.entity.academic.Assignment;
import com.campus.domain.entity.academic.AssignmentSubmission;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 作业管理服务接口
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
public interface AssignmentService {

    // ==================== 基础CRUD方法 ====================

    /**
     * 保存作业
     */
    Assignment save(Assignment assignment);

    /**
     * 根据ID查找作业
     */
    Optional<Assignment> findById(Long id);

    /**
     * 获取所有作业
     */
    List<Assignment> findAll();

    /**
     * 分页获取作业
     */
    Page<Assignment> findAll(Pageable pageable);

    /**
     * 删除作业
     */
    void deleteById(Long id);

    /**
     * 批量删除作业
     */
    void deleteByIds(List<Long> ids);

    /**
     * 统计作业数量
     */
    long count();

    // ==================== 业务查询方法 ====================

    /**
     * 根据课程ID查找作业
     */
    List<Assignment> findByCourseId(Long courseId);

    /**
     * 根据教师ID查找作业
     */
    List<Assignment> findByTeacherId(Long teacherId);

    /**
     * 根据班级ID查找作业
     */
    List<Assignment> findByClassId(Long classId);

    /**
     * 根据作业类型查找作业
     */
    List<Assignment> findByAssignmentType(String assignmentType);

    /**
     * 查找已发布的作业
     */
    List<Assignment> findPublishedAssignments();

    /**
     * 查找未发布的作业
     */
    List<Assignment> findUnpublishedAssignments();

    /**
     * 根据截止时间范围查找作业
     */
    List<Assignment> findByDueDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 查找即将到期的作业
     */
    List<Assignment> findAssignmentsDueSoon(int hours);

    /**
     * 查找已过期的作业
     */
    List<Assignment> findOverdueAssignments();

    // ==================== 分页查询方法 ====================

    /**
     * 根据课程ID分页查找作业
     */
    Page<Assignment> findByCourseId(Long courseId, Pageable pageable);

    /**
     * 根据教师ID分页查找作业
     */
    Page<Assignment> findByTeacherId(Long teacherId, Pageable pageable);

    /**
     * 根据班级ID分页查找作业
     */
    Page<Assignment> findByClassId(Long classId, Pageable pageable);

    /**
     * 根据条件分页查询作业
     */
    Page<Assignment> findByConditions(String title, Long courseId, Long teacherId, 
                                    String assignmentType, Boolean isPublished, 
                                    Pageable pageable);

    // ==================== 业务操作方法 ====================

    /**
     * 发布作业
     */
    void publishAssignment(Long assignmentId);

    /**
     * 取消发布作业
     */
    void unpublishAssignment(Long assignmentId);

    /**
     * 批量发布作业
     */
    void publishAssignments(List<Long> assignmentIds);

    /**
     * 延长作业截止时间
     */
    void extendDueDate(Long assignmentId, LocalDateTime newDueDate);

    /**
     * 复制作业
     */
    Assignment copyAssignment(Long assignmentId, Long newCourseId);

    // ==================== 统计分析方法 ====================

    /**
     * 统计课程作业数量
     */
    long countByCourseId(Long courseId);

    /**
     * 统计教师作业数量
     */
    long countByTeacherId(Long teacherId);

    /**
     * 统计已发布作业数量
     */
    long countPublishedAssignments();

    /**
     * 统计未发布作业数量
     */
    long countUnpublishedAssignments();

    /**
     * 统计即将到期作业数量
     */
    long countAssignmentsDueSoon(int hours);

    /**
     * 统计已过期作业数量
     */
    long countOverdueAssignments();

    /**
     * 获取作业提交统计
     */
    List<Object[]> getSubmissionStatistics(Long assignmentId);

    /**
     * 获取课程作业统计
     */
    List<Object[]> getCourseAssignmentStatistics(Long courseId);

    /**
     * 获取教师作业统计
     */
    List<Object[]> getTeacherAssignmentStatistics(Long teacherId);

    // ==================== 作业提交相关方法 ====================

    /**
     * 获取作业的所有提交
     */
    List<AssignmentSubmission> getAssignmentSubmissions(Long assignmentId);

    /**
     * 获取作业的分页提交
     */
    Page<AssignmentSubmission> getAssignmentSubmissions(Long assignmentId, Pageable pageable);

    /**
     * 获取学生的作业提交
     */
    Optional<AssignmentSubmission> getStudentSubmission(Long assignmentId, Long studentId);

    /**
     * 统计作业提交数量
     */
    long countSubmissions(Long assignmentId);

    /**
     * 统计已批改提交数量
     */
    long countGradedSubmissions(Long assignmentId);

    /**
     * 统计未批改提交数量
     */
    long countUngradedSubmissions(Long assignmentId);

    /**
     * 计算作业平均分
     */
    Double calculateAverageScore(Long assignmentId);

    // ==================== 验证方法 ====================

    /**
     * 检查作业标题是否存在
     */
    boolean existsByTitle(String title);

    /**
     * 检查作业标题是否存在（排除指定ID）
     */
    boolean existsByTitleExcludeId(String title, Long excludeId);

    /**
     * 检查是否可以删除作业
     */
    boolean canDeleteAssignment(Long assignmentId);

    /**
     * 检查是否可以修改作业
     */
    boolean canModifyAssignment(Long assignmentId);

    // ==================== 状态管理方法 ====================

    /**
     * 启用作业
     */
    void enableAssignment(Long id);

    /**
     * 禁用作业
     */
    void disableAssignment(Long id);

    /**
     * 批量启用作业
     */
    void enableAssignments(List<Long> ids);

    /**
     * 批量禁用作业
     */
    void disableAssignments(List<Long> ids);

    // ================================
    // API控制器需要的方法
    // ================================

    /**
     * 提交作业
     *
     * @param submission 作业提交信息
     * @return 提交的作业
     */
    AssignmentSubmission submitAssignment(AssignmentSubmission submission);

    /**
     * 上传作业文件
     *
     * @param submissionId 提交ID
     * @param file 文件
     * @return 文件URL
     */
    String uploadSubmissionFile(Long submissionId, org.springframework.web.multipart.MultipartFile file);

    /**
     * 根据ID获取作业提交
     *
     * @param submissionId 提交ID
     * @return 作业提交信息
     */
    AssignmentSubmission getSubmissionById(Long submissionId);

    /**
     * 更新作业提交
     *
     * @param submission 作业提交信息
     * @return 更新的作业提交
     */
    AssignmentSubmission updateSubmission(AssignmentSubmission submission);

    /**
     * 删除作业提交
     *
     * @param submissionId 提交ID
     * @return 删除结果
     */
    boolean deleteSubmission(Long submissionId);

    /**
     * 分页查找作业提交
     *
     * @param pageable 分页参数
     * @param assignmentId 作业ID
     * @param studentId 学生ID
     * @param status 状态
     * @return 分页结果
     */
    Page<AssignmentSubmission> findSubmissions(Pageable pageable, Long assignmentId, Long studentId, String status);

    /**
     * 根据作业ID获取所有提交
     *
     * @param assignmentId 作业ID
     * @return 提交列表
     */
    List<AssignmentSubmission> getSubmissionsByAssignment(Long assignmentId);

    /**
     * 根据学生ID获取所有提交
     *
     * @param studentId 学生ID
     * @return 提交列表
     */
    List<AssignmentSubmission> getSubmissionsByStudent(Long studentId);

    /**
     * 作业评分
     *
     * @param submissionId 提交ID
     * @param score 分数
     * @param feedback 反馈
     * @param teacherId 教师ID
     * @return 评分后的作业提交
     */
    AssignmentSubmission gradeSubmission(Long submissionId, Double score, String feedback, Long teacherId);

    /**
     * 获取逾期未提交作业
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 逾期作业列表
     */
    List<Map<String, Object>> getOverdueSubmissions(Long courseId, Long studentId);

    /**
     * 批量评分
     *
     * @param batchGradeData 批量评分数据
     * @return 评分结果
     */
    Map<String, Object> batchGradeSubmissions(Map<String, Object> batchGradeData);
}
