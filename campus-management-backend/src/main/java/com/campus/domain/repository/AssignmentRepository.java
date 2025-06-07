package com.campus.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campus.domain.entity.Assignment;

import java.time.LocalDateTime;
import java.util.List;


/**
 * 作业Repository接口
 * 提供作业相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface AssignmentRepository extends BaseRepository<Assignment> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据课程ID查找作业列表
     */
    @Query("SELECT a FROM Assignment a WHERE a.courseId = :courseId AND a.deleted = 0 ORDER BY a.dueDate DESC")
    List<Assignment> findByCourseId(@Param("courseId") Long courseId);

    /**
     * 分页根据课程ID查找作业列表
     */
    @Query("SELECT a FROM Assignment a WHERE a.courseId = :courseId AND a.deleted = 0")
    Page<Assignment> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    /**
     * 根据教师ID查找作业列表
     */
    @Query("SELECT a FROM Assignment a WHERE a.teacherId = :teacherId AND a.deleted = 0 ORDER BY a.dueDate DESC")
    List<Assignment> findByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 分页根据教师ID查找作业列表
     */
    @Query("SELECT a FROM Assignment a WHERE a.teacherId = :teacherId AND a.deleted = 0")
    Page<Assignment> findByTeacherId(@Param("teacherId") Long teacherId, Pageable pageable);

    /**
     * 根据作业类型查找作业列表
     */
    @Query("SELECT a FROM Assignment a WHERE a.assignmentType = :type AND a.deleted = 0 ORDER BY a.dueDate DESC")
    List<Assignment> findByAssignmentType(@Param("type") String assignmentType);

    /**
     * 根据截止日期范围查找作业
     */
    @Query("SELECT a FROM Assignment a WHERE a.dueDate BETWEEN :startDate AND :endDate AND a.deleted = 0 ORDER BY a.dueDate")
    List<Assignment> findByDueDateBetween(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找作业
     */
    @Query("SELECT a FROM Assignment a WHERE " +
           "(:courseId IS NULL OR a.courseId = :courseId) AND " +
           "(:teacherId IS NULL OR a.teacherId = :teacherId) AND " +
           "(:assignmentType IS NULL OR a.assignmentType = :assignmentType) AND " +
           "a.deleted = 0")
    Page<Assignment> findByMultipleConditions(@Param("courseId") Long courseId,
                                             @Param("teacherId") Long teacherId,
                                             @Param("assignmentType") String assignmentType,
                                             Pageable pageable);

    /**
     * 搜索作业（根据标题、描述等关键词）
     */
    @Query("SELECT a FROM Assignment a WHERE " +
           "(a.title LIKE %:keyword% OR " +
           "a.description LIKE %:keyword%) AND " +
           "a.deleted = 0 ORDER BY a.dueDate DESC")
    List<Assignment> searchAssignments(@Param("keyword") String keyword);

    /**
     * 分页搜索作业
     */
    @Query("SELECT a FROM Assignment a WHERE " +
           "(a.title LIKE %:keyword% OR " +
           "a.description LIKE %:keyword%) AND " +
           "a.deleted = 0")
    Page<Assignment> searchAssignments(@Param("keyword") String keyword, Pageable pageable);

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 查找作业并预加载课程信息
     */
    @Query("SELECT DISTINCT a FROM Assignment a LEFT JOIN FETCH a.course WHERE a.deleted = 0")
    List<Assignment> findAllWithCourse();

    /**
     * 查找作业并预加载教师信息
     */
    @Query("SELECT DISTINCT a FROM Assignment a LEFT JOIN FETCH a.teacher WHERE a.deleted = 0")
    List<Assignment> findAllWithTeacher();

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据课程统计作业数量
     */
    @Query("SELECT c.courseName, COUNT(a) FROM Assignment a LEFT JOIN a.course c WHERE a.deleted = 0 GROUP BY a.courseId, c.courseName ORDER BY COUNT(a) DESC")
    List<Object[]> countByCourse();

    /**
     * 根据作业类型统计数量
     */
    @Query("SELECT a.assignmentType, COUNT(a) FROM Assignment a WHERE a.deleted = 0 GROUP BY a.assignmentType ORDER BY COUNT(a) DESC")
    List<Object[]> countByType();

    /**
     * 根据教师统计作业数量
     */
    @Query("SELECT u.realName, COUNT(a) FROM Assignment a LEFT JOIN a.teacher u WHERE a.deleted = 0 GROUP BY a.teacherId, u.realName ORDER BY COUNT(a) DESC")
    List<Object[]> countByTeacher();

    // ================================
    // 时间相关查询
    // ================================

    /**
     * 查找即将到期的作业（未来7天内）
     */
    @Query("SELECT a FROM Assignment a WHERE a.dueDate BETWEEN CURRENT_TIMESTAMP AND :sevenDaysLater AND a.deleted = 0 ORDER BY a.dueDate")
    List<Assignment> findUpcomingAssignments(@Param("sevenDaysLater") LocalDateTime sevenDaysLater);

    /**
     * 查找已过期的作业
     */
    @Query("SELECT a FROM Assignment a WHERE a.dueDate < CURRENT_TIMESTAMP AND a.deleted = 0 ORDER BY a.dueDate DESC")
    List<Assignment> findOverdueAssignments();

    /**
     * 查找今日到期的作业
     */
    @Query("SELECT a FROM Assignment a WHERE DATE(a.dueDate) = CURRENT_DATE AND a.deleted = 0 ORDER BY a.dueDate")
    List<Assignment> findTodayDueAssignments();

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查作业标题是否存在（同一课程内）
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Assignment a WHERE a.title = :title AND a.courseId = :courseId AND a.deleted = 0")
    boolean existsByTitleAndCourseId(@Param("title") String title, @Param("courseId") Long courseId);

    /**
     * 检查作业标题是否存在（同一课程内，排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Assignment a WHERE a.title = :title AND a.courseId = :courseId AND a.id != :excludeId AND a.deleted = 0")
    boolean existsByTitleAndCourseIdAndIdNot(@Param("title") String title, @Param("courseId") Long courseId, @Param("excludeId") Long excludeId);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新作业截止日期
     */
    @Modifying
    @Query("UPDATE Assignment a SET a.dueDate = :dueDate, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id = :assignmentId")
    int updateDueDate(@Param("assignmentId") Long assignmentId, @Param("dueDate") LocalDateTime dueDate);

    /**
     * 批量更新作业截止日期
     */
    @Modifying
    @Query("UPDATE Assignment a SET a.dueDate = :dueDate, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id IN :assignmentIds")
    int batchUpdateDueDate(@Param("assignmentIds") List<Long> assignmentIds, @Param("dueDate") LocalDateTime dueDate);

    /**
     * 更新作业最大分数
     */
    @Modifying
    @Query("UPDATE Assignment a SET a.maxScore = :maxScore, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id = :assignmentId")
    int updateMaxScore(@Param("assignmentId") Long assignmentId, @Param("maxScore") Integer maxScore);

}
