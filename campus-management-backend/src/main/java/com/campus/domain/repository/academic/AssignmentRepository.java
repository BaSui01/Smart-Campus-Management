package com.campus.domain.repository.academic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campus.domain.entity.academic.Assignment;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业数据访问接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    // ==================== 基础查询方法 ====================

    /**
     * 根据课程ID查找作业
     */
    List<Assignment> findByCourseIdAndDeleted(Long courseId, Integer deleted);

    /**
     * 根据课程ID分页查找作业
     */
    Page<Assignment> findByCourseIdAndDeleted(Long courseId, Integer deleted, Pageable pageable);

    /**
     * 根据教师ID查找作业
     */
    List<Assignment> findByTeacherIdAndDeleted(Long teacherId, Integer deleted);

    /**
     * 根据教师ID分页查找作业
     */
    Page<Assignment> findByTeacherIdAndDeleted(Long teacherId, Integer deleted, Pageable pageable);

    /**
     * 根据班级ID查找作业
     */
    List<Assignment> findByClassIdAndDeleted(Long classId, Integer deleted);

    /**
     * 根据班级ID分页查找作业
     */
    Page<Assignment> findByClassIdAndDeleted(Long classId, Integer deleted, Pageable pageable);

    /**
     * 根据作业类型查找作业
     */
    List<Assignment> findByAssignmentTypeAndDeleted(String assignmentType, Integer deleted);

    /**
     * 查找已发布的作业
     */
    List<Assignment> findByIsPublishedTrueAndDeleted(Integer deleted);

    /**
     * 查找未发布的作业
     */
    List<Assignment> findByIsPublishedFalseAndDeleted(Integer deleted);

    /**
     * 根据截止时间范围查找作业
     */
    List<Assignment> findByDueDateBetweenAndDeleted(LocalDateTime startDate, LocalDateTime endDate, Integer deleted);

    /**
     * 查找已过期的作业
     */
    List<Assignment> findByDueDateBeforeAndDeleted(LocalDateTime dueDate, Integer deleted);

    // ==================== 统计查询方法 ====================

    /**
     * 统计课程作业数量
     */
    long countByCourseIdAndDeleted(Long courseId, Integer deleted);

    /**
     * 统计教师作业数量
     */
    long countByTeacherIdAndDeleted(Long teacherId, Integer deleted);

    /**
     * 统计已发布作业数量
     */
    long countByIsPublishedTrueAndDeleted(Integer deleted);

    /**
     * 统计未发布作业数量
     */
    long countByIsPublishedFalseAndDeleted(Integer deleted);

    /**
     * 统计截止时间范围内的作业数量
     */
    long countByDueDateBetweenAndDeleted(LocalDateTime startDate, LocalDateTime endDate, Integer deleted);

    /**
     * 统计已过期作业数量
     */
    long countByDueDateBeforeAndDeleted(LocalDateTime dueDate, Integer deleted);

    // ==================== 验证方法 ====================

    /**
     * 检查作业标题是否存在
     */
    boolean existsByTitleAndDeleted(String title, Integer deleted);

    /**
     * 检查作业标题是否存在（排除指定ID）
     */
    boolean existsByTitleAndIdNotAndDeleted(String title, Long excludeId, Integer deleted);

    // ==================== 复杂查询方法 ====================

    /**
     * 根据条件分页查询作业
     */
    @Query("SELECT a FROM Assignment a WHERE " +
           "(:title IS NULL OR a.title LIKE %:title%) AND " +
           "(:courseId IS NULL OR a.courseId = :courseId) AND " +
           "(:teacherId IS NULL OR a.teacherId = :teacherId) AND " +
           "(:assignmentType IS NULL OR a.assignmentType = :assignmentType) AND " +
           "(:isPublished IS NULL OR a.isPublished = :isPublished) AND " +
           "a.deleted = 0")
    Page<Assignment> findByConditions(@Param("title") String title,
                                    @Param("courseId") Long courseId,
                                    @Param("teacherId") Long teacherId,
                                    @Param("assignmentType") String assignmentType,
                                    @Param("isPublished") Boolean isPublished,
                                    Pageable pageable);

    /**
     * 获取作业提交统计
     */
    @Query("SELECT " +
           "COUNT(s) as totalSubmissions, " +
           "COUNT(CASE WHEN s.submissionStatus = 'graded' THEN 1 END) as gradedSubmissions, " +
           "COUNT(CASE WHEN s.submissionStatus != 'graded' THEN 1 END) as ungradedSubmissions, " +
           "AVG(s.score) as averageScore " +
           "FROM AssignmentSubmission s WHERE s.assignmentId = :assignmentId AND s.deleted = 0")
    List<Object[]> getSubmissionStatistics(@Param("assignmentId") Long assignmentId);

    /**
     * 获取课程作业统计
     */
    @Query("SELECT " +
           "COUNT(a) as totalAssignments, " +
           "COUNT(CASE WHEN a.isPublished = true THEN 1 END) as publishedAssignments, " +
           "COUNT(CASE WHEN a.dueDate < CURRENT_TIMESTAMP THEN 1 END) as overdueAssignments " +
           "FROM Assignment a WHERE a.courseId = :courseId AND a.deleted = 0")
    List<Object[]> getCourseAssignmentStatistics(@Param("courseId") Long courseId);

    /**
     * 获取教师作业统计
     */
    @Query("SELECT " +
           "COUNT(a) as totalAssignments, " +
           "COUNT(CASE WHEN a.isPublished = true THEN 1 END) as publishedAssignments, " +
           "COUNT(CASE WHEN a.dueDate < CURRENT_TIMESTAMP THEN 1 END) as overdueAssignments " +
           "FROM Assignment a WHERE a.teacherId = :teacherId AND a.deleted = 0")
    List<Object[]> getTeacherAssignmentStatistics(@Param("teacherId") Long teacherId);

    // ==================== 更新方法 ====================

    /**
     * 更新发布状态
     */
    @Modifying
    @Query("UPDATE Assignment a SET a.isPublished = :isPublished, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id = :id")
    int updatePublishStatus(@Param("id") Long id, @Param("isPublished") Boolean isPublished);

    /**
     * 批量更新发布状态
     */
    @Modifying
    @Query("UPDATE Assignment a SET a.isPublished = :isPublished, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id IN :ids")
    int batchUpdatePublishStatus(@Param("ids") List<Long> ids, @Param("isPublished") Boolean isPublished);

    /**
     * 更新截止时间
     */
    @Modifying
    @Query("UPDATE Assignment a SET a.dueDate = :dueDate, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id = :id")
    int updateDueDate(@Param("id") Long id, @Param("dueDate") LocalDateTime dueDate);

    /**
     * 更新状态
     */
    @Modifying
    @Query("UPDATE Assignment a SET a.status = :status, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 批量更新状态
     */
    @Modifying
    @Query("UPDATE Assignment a SET a.status = :status, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id IN :ids")
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);
}
