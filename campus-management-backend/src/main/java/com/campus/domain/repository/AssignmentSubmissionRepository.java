package com.campus.domain.repository;

import com.campus.domain.entity.AssignmentSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 作业提交Repository接口
 * 提供作业提交相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface AssignmentSubmissionRepository extends BaseRepository<AssignmentSubmission> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据作业ID查找提交记录
     */
    @Query("SELECT s FROM AssignmentSubmission s WHERE s.assignmentId = :assignmentId AND s.deleted = 0 ORDER BY s.submittedAt DESC")
    List<AssignmentSubmission> findByAssignmentId(@Param("assignmentId") Long assignmentId);

    /**
     * 分页根据作业ID查找提交记录
     */
    @Query("SELECT s FROM AssignmentSubmission s WHERE s.assignmentId = :assignmentId AND s.deleted = 0")
    Page<AssignmentSubmission> findByAssignmentId(@Param("assignmentId") Long assignmentId, Pageable pageable);

    /**
     * 根据学生ID查找提交记录
     */
    @Query("SELECT s FROM AssignmentSubmission s WHERE s.studentId = :studentId AND s.deleted = 0 ORDER BY s.submittedAt DESC")
    List<AssignmentSubmission> findByStudentId(@Param("studentId") Long studentId);

    /**
     * 分页根据学生ID查找提交记录
     */
    @Query("SELECT s FROM AssignmentSubmission s WHERE s.studentId = :studentId AND s.deleted = 0")
    Page<AssignmentSubmission> findByStudentId(@Param("studentId") Long studentId, Pageable pageable);

    /**
     * 根据作业ID和学生ID查找提交记录
     */
    @Query("SELECT s FROM AssignmentSubmission s WHERE s.assignmentId = :assignmentId AND s.studentId = :studentId AND s.deleted = 0")
    Optional<AssignmentSubmission> findByAssignmentIdAndStudentId(@Param("assignmentId") Long assignmentId, 
                                                                 @Param("studentId") Long studentId);

    /**
     * 根据提交状态查找提交记录
     */
    @Query("SELECT s FROM AssignmentSubmission s WHERE s.submissionStatus = :status AND s.deleted = 0 ORDER BY s.submittedAt DESC")
    List<AssignmentSubmission> findBySubmissionStatus(@Param("status") String submissionStatus);

    /**
     * 根据评分状态查找提交记录
     */
    @Query("SELECT s FROM AssignmentSubmission s WHERE s.gradingStatus = :status AND s.deleted = 0 ORDER BY s.submittedAt DESC")
    List<AssignmentSubmission> findByGradingStatus(@Param("status") String gradingStatus);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找提交记录
     */
    @Query("SELECT s FROM AssignmentSubmission s WHERE " +
           "(:assignmentId IS NULL OR s.assignmentId = :assignmentId) AND " +
           "(:studentId IS NULL OR s.studentId = :studentId) AND " +
           "(:submissionStatus IS NULL OR s.submissionStatus = :submissionStatus) AND " +
           "(:gradingStatus IS NULL OR s.gradingStatus = :gradingStatus) AND " +
           "s.deleted = 0")
    Page<AssignmentSubmission> findByMultipleConditions(@Param("assignmentId") Long assignmentId,
                                                        @Param("studentId") Long studentId,
                                                        @Param("submissionStatus") String submissionStatus,
                                                        @Param("gradingStatus") String gradingStatus,
                                                        Pageable pageable);

    /**
     * 搜索提交记录（根据提交内容等关键词）
     */
    @Query("SELECT s FROM AssignmentSubmission s WHERE " +
           "(s.submissionContent LIKE %:keyword% OR " +
           "s.feedback LIKE %:keyword%) AND " +
           "s.deleted = 0 ORDER BY s.submittedAt DESC")
    List<AssignmentSubmission> searchSubmissions(@Param("keyword") String keyword);

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 查找提交记录并预加载作业信息
     */
    @Query("SELECT DISTINCT s FROM AssignmentSubmission s LEFT JOIN FETCH s.assignment WHERE s.deleted = 0")
    List<AssignmentSubmission> findAllWithAssignment();

    /**
     * 查找提交记录并预加载学生信息
     */
    @Query("SELECT DISTINCT s FROM AssignmentSubmission s LEFT JOIN FETCH s.student WHERE s.deleted = 0")
    List<AssignmentSubmission> findAllWithStudent();

    /**
     * 查找提交记录并预加载所有关联信息
     */
    @Query("SELECT DISTINCT s FROM AssignmentSubmission s " +
           "LEFT JOIN FETCH s.assignment a " +
           "LEFT JOIN FETCH s.student st " +
           "WHERE s.deleted = 0")
    List<AssignmentSubmission> findAllWithAssociations();

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据作业统计提交数量
     */
    @Query("SELECT a.title, COUNT(s) FROM AssignmentSubmission s LEFT JOIN s.assignment a WHERE s.deleted = 0 GROUP BY s.assignmentId, a.title ORDER BY COUNT(s) DESC")
    List<Object[]> countByAssignment();

    /**
     * 根据学生统计提交数量
     */
    @Query("SELECT st.studentNo, COUNT(s) FROM AssignmentSubmission s LEFT JOIN s.student st WHERE s.deleted = 0 GROUP BY s.studentId, st.studentNo ORDER BY COUNT(s) DESC")
    List<Object[]> countByStudent();

    /**
     * 根据提交状态统计数量
     */
    @Query("SELECT s.submissionStatus, COUNT(s) FROM AssignmentSubmission s WHERE s.deleted = 0 GROUP BY s.submissionStatus")
    List<Object[]> countBySubmissionStatus();

    /**
     * 根据评分状态统计数量
     */
    @Query("SELECT s.gradingStatus, COUNT(s) FROM AssignmentSubmission s WHERE s.deleted = 0 GROUP BY s.gradingStatus")
    List<Object[]> countByGradingStatus();

    /**
     * 统计指定作业的提交率
     */
    @Query("SELECT " +
           "(SELECT COUNT(s) FROM AssignmentSubmission s WHERE s.assignmentId = :assignmentId AND s.deleted = 0) as submitted, " +
           "(SELECT COUNT(cs) FROM CourseSelection cs WHERE cs.courseId = (SELECT a.courseId FROM Assignment a WHERE a.id = :assignmentId) AND cs.deleted = 0) as total")
    Object[] getSubmissionRate(@Param("assignmentId") Long assignmentId);

    // ================================
    // 时间相关查询
    // ================================

    /**
     * 查找逾期提交的记录
     */
    @Query("SELECT s FROM AssignmentSubmission s LEFT JOIN s.assignment a " +
           "WHERE s.submittedAt > a.dueDate AND s.deleted = 0 ORDER BY s.submittedAt DESC")
    List<AssignmentSubmission> findLateSubmissions();

    /**
     * 查找按时提交的记录
     */
    @Query("SELECT s FROM AssignmentSubmission s LEFT JOIN s.assignment a " +
           "WHERE s.submittedAt <= a.dueDate AND s.deleted = 0 ORDER BY s.submittedAt DESC")
    List<AssignmentSubmission> findOnTimeSubmissions();

    /**
     * 查找指定时间范围内的提交记录
     */
    @Query("SELECT s FROM AssignmentSubmission s WHERE s.submittedAt BETWEEN :startTime AND :endTime AND s.deleted = 0 ORDER BY s.submittedAt DESC")
    List<AssignmentSubmission> findBySubmittedAtBetween(@Param("startTime") LocalDateTime startTime, 
                                                        @Param("endTime") LocalDateTime endTime);

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查学生是否已提交指定作业
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM AssignmentSubmission s WHERE s.assignmentId = :assignmentId AND s.studentId = :studentId AND s.deleted = 0")
    boolean existsByAssignmentIdAndStudentId(@Param("assignmentId") Long assignmentId, @Param("studentId") Long studentId);

    /**
     * 检查学生是否有未评分的提交
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM AssignmentSubmission s WHERE s.studentId = :studentId AND s.gradingStatus = 'pending' AND s.deleted = 0")
    boolean hasUngradedSubmissions(@Param("studentId") Long studentId);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新提交分数
     */
    @Modifying
    @Query("UPDATE AssignmentSubmission s SET s.score = :score, s.gradingStatus = 'graded', s.gradedAt = CURRENT_TIMESTAMP, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id = :submissionId")
    int updateScore(@Param("submissionId") Long submissionId, @Param("score") Integer score);

    /**
     * 更新提交反馈
     */
    @Modifying
    @Query("UPDATE AssignmentSubmission s SET s.feedback = :feedback, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id = :submissionId")
    int updateFeedback(@Param("submissionId") Long submissionId, @Param("feedback") String feedback);

    /**
     * 更新评分状态
     */
    @Modifying
    @Query("UPDATE AssignmentSubmission s SET s.gradingStatus = :status, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id = :submissionId")
    int updateGradingStatus(@Param("submissionId") Long submissionId, @Param("status") String gradingStatus);

    /**
     * 批量更新评分状态
     */
    @Modifying
    @Query("UPDATE AssignmentSubmission s SET s.gradingStatus = :status, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id IN :submissionIds")
    int batchUpdateGradingStatus(@Param("submissionIds") List<Long> submissionIds, @Param("status") String gradingStatus);

}
