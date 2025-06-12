package com.campus.domain.repository.academic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campus.domain.entity.academic.AssignmentSubmission;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 作业提交数据访问接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {

    // ==================== 基础查询方法 ====================

    /**
     * 根据作业ID查找提交记录
     */
    List<AssignmentSubmission> findByAssignmentIdAndDeleted(Long assignmentId, Integer deleted);

    /**
     * 根据作业ID分页查找提交记录
     */
    Page<AssignmentSubmission> findByAssignmentIdAndDeleted(Long assignmentId, Integer deleted, Pageable pageable);

    /**
     * 根据学生ID查找提交记录
     */
    List<AssignmentSubmission> findByStudentIdAndDeleted(Long studentId, Integer deleted);

    /**
     * 根据学生ID分页查找提交记录
     */
    Page<AssignmentSubmission> findByStudentIdAndDeleted(Long studentId, Integer deleted, Pageable pageable);

    /**
     * 根据作业ID和学生ID查找提交记录
     */
    Optional<AssignmentSubmission> findByAssignmentIdAndStudentIdAndDeleted(Long assignmentId, Long studentId, Integer deleted);

    /**
     * 根据批改状态查找提交记录
     */
    List<AssignmentSubmission> findBySubmissionStatusAndDeleted(String submissionStatus, Integer deleted);

    /**
     * 根据提交时间范围查找提交记录
     */
    List<AssignmentSubmission> findBySubmissionTimeBetweenAndDeleted(LocalDateTime startTime, LocalDateTime endTime, Integer deleted);

    // ==================== 统计查询方法 ====================

    /**
     * 统计作业提交数量
     */
    long countByAssignmentIdAndDeleted(Long assignmentId, Integer deleted);

    /**
     * 统计学生提交数量
     */
    long countByStudentIdAndDeleted(Long studentId, Integer deleted);

    /**
     * 统计已批改提交数量
     */
    long countByAssignmentIdAndSubmissionStatusAndDeleted(Long assignmentId, String submissionStatus, Integer deleted);

    /**
     * 统计未批改提交数量
     */
    @Query("SELECT COUNT(s) FROM AssignmentSubmission s WHERE s.assignmentId = :assignmentId AND s.submissionStatus != 'graded' AND s.deleted = :deleted")
    long countUngradedByAssignmentId(@Param("assignmentId") Long assignmentId, @Param("deleted") Integer deleted);

    /**
     * 统计迟交提交数量
     */
    @Query("SELECT COUNT(s) FROM AssignmentSubmission s JOIN Assignment a ON s.assignmentId = a.id " +
           "WHERE s.assignmentId = :assignmentId AND s.submissionTime > a.dueDate AND s.deleted = 0")
    long countLateSubmissions(@Param("assignmentId") Long assignmentId);

    // ==================== 复杂查询方法 ====================

    /**
     * 计算作业平均分
     */
    @Query("SELECT AVG(s.score) FROM AssignmentSubmission s WHERE s.assignmentId = :assignmentId AND s.submissionStatus = 'graded' AND s.deleted = 0")
    Double calculateAverageScore(@Param("assignmentId") Long assignmentId);

    /**
     * 获取作业最高分
     */
    @Query("SELECT MAX(s.score) FROM AssignmentSubmission s WHERE s.assignmentId = :assignmentId AND s.submissionStatus = 'graded' AND s.deleted = 0")
    Double getMaxScore(@Param("assignmentId") Long assignmentId);

    /**
     * 获取作业最低分
     */
    @Query("SELECT MIN(s.score) FROM AssignmentSubmission s WHERE s.assignmentId = :assignmentId AND s.submissionStatus = 'graded' AND s.deleted = 0")
    Double getMinScore(@Param("assignmentId") Long assignmentId);

    /**
     * 获取学生的作业提交统计
     */
    @Query("SELECT " +
           "COUNT(s) as totalSubmissions, " +
           "COUNT(CASE WHEN s.submissionStatus = 'graded' THEN 1 END) as gradedSubmissions, " +
           "AVG(CASE WHEN s.submissionStatus = 'graded' THEN s.score END) as averageScore " +
           "FROM AssignmentSubmission s WHERE s.studentId = :studentId AND s.deleted = 0")
    List<Object[]> getStudentSubmissionStatistics(@Param("studentId") Long studentId);

    /**
     * 获取作业提交详细统计
     */
    @Query("SELECT " +
           "COUNT(s) as totalSubmissions, " +
           "COUNT(CASE WHEN s.submissionStatus = 'graded' THEN 1 END) as gradedSubmissions, " +
           "COUNT(CASE WHEN s.submissionStatus != 'graded' THEN 1 END) as ungradedSubmissions, " +
           "COUNT(CASE WHEN s.submissionTime > a.dueDate THEN 1 END) as lateSubmissions, " +
           "AVG(CASE WHEN s.submissionStatus = 'graded' THEN s.score END) as averageScore, " +
           "MAX(CASE WHEN s.submissionStatus = 'graded' THEN s.score END) as maxScore, " +
           "MIN(CASE WHEN s.submissionStatus = 'graded' THEN s.score END) as minScore " +
           "FROM AssignmentSubmission s JOIN Assignment a ON s.assignmentId = a.id " +
           "WHERE s.assignmentId = :assignmentId AND s.deleted = 0")
    List<Object[]> getDetailedSubmissionStatistics(@Param("assignmentId") Long assignmentId);

    /**
     * 根据条件分页查询提交记录
     */
    @Query("SELECT s FROM AssignmentSubmission s WHERE " +
           "(:assignmentId IS NULL OR s.assignmentId = :assignmentId) AND " +
           "(:studentId IS NULL OR s.studentId = :studentId) AND " +
           "(:submissionStatus IS NULL OR s.submissionStatus = :submissionStatus) AND " +
           "(:minScore IS NULL OR s.score >= :minScore) AND " +
           "(:maxScore IS NULL OR s.score <= :maxScore) AND " +
           "s.deleted = 0")
    Page<AssignmentSubmission> findByConditions(@Param("assignmentId") Long assignmentId,
                                               @Param("studentId") Long studentId,
                                               @Param("submissionStatus") String submissionStatus,
                                               @Param("minScore") Double minScore,
                                               @Param("maxScore") Double maxScore,
                                               Pageable pageable);

    /**
     * 查找迟交的提交记录
     */
    @Query("SELECT s FROM AssignmentSubmission s JOIN Assignment a ON s.assignmentId = a.id " +
           "WHERE s.assignmentId = :assignmentId AND s.submissionTime > a.dueDate AND s.deleted = 0")
    List<AssignmentSubmission> findLateSubmissions(@Param("assignmentId") Long assignmentId);

    /**
     * 查找未提交作业的学生
     */
    @Query("SELECT st.id, st.studentNo, u.realName FROM Student st " +
           "JOIN User u ON st.userId = u.id " +
           "WHERE st.classId IN (SELECT a.classId FROM Assignment a WHERE a.id = :assignmentId) " +
           "AND st.id NOT IN (SELECT s.studentId FROM AssignmentSubmission s WHERE s.assignmentId = :assignmentId AND s.deleted = 0) " +
           "AND st.deleted = 0")
    List<Object[]> findStudentsWithoutSubmission(@Param("assignmentId") Long assignmentId);

    // ==================== 更新方法 ====================

    /**
     * 更新批改状态和分数
     */
    @Modifying
    @Query("UPDATE AssignmentSubmission s SET s.submissionStatus = :submissionStatus, s.score = :score, s.teacherComment = :feedback, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id = :id")
    int updateGrading(@Param("id") Long id, @Param("submissionStatus") String submissionStatus, @Param("score") Double score, @Param("feedback") String feedback);

    /**
     * 批量更新批改状态
     */
    @Modifying
    @Query("UPDATE AssignmentSubmission s SET s.submissionStatus = :submissionStatus, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id IN :ids")
    int batchUpdateGradingStatus(@Param("ids") List<Long> ids, @Param("submissionStatus") String submissionStatus);

    /**
     * 更新提交状态
     */
    @Modifying
    @Query("UPDATE AssignmentSubmission s SET s.status = :status, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 批量更新提交状态
     */
    @Modifying
    @Query("UPDATE AssignmentSubmission s SET s.status = :status, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id IN :ids")
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    // ==================== 验证方法 ====================

    /**
     * 检查学生是否已提交作业
     */
    boolean existsByAssignmentIdAndStudentIdAndDeleted(Long assignmentId, Long studentId, Integer deleted);

    /**
     * 检查是否可以删除提交记录
     */
    @Query("SELECT CASE WHEN s.submissionStatus = 'graded' THEN false ELSE true END FROM AssignmentSubmission s WHERE s.id = :id")
    Boolean canDeleteSubmission(@Param("id") Long id);

    // ==================== AssignmentServiceImpl需要的额外方法 ====================

    /**
     * 根据删除状态排序查找提交记录（按提交时间倒序）
     */
    Page<AssignmentSubmission> findByDeletedOrderBySubmissionTimeDesc(Integer deleted, Pageable pageable);

    /**
     * 根据作业ID和删除状态排序查找提交记录（按提交时间倒序）
     */
    List<AssignmentSubmission> findByAssignmentIdAndDeletedOrderBySubmissionTimeDesc(Long assignmentId, Integer deleted);

    /**
     * 根据学生ID和删除状态排序查找提交记录（按提交时间倒序）
     */
    List<AssignmentSubmission> findByStudentIdAndDeletedOrderBySubmissionTimeDesc(Long studentId, Integer deleted);
}
