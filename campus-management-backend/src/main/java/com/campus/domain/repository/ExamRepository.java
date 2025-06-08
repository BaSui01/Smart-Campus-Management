package com.campus.domain.repository;

import com.campus.domain.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


/**
 * 考试Repository接口
 * 提供考试相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface ExamRepository extends BaseRepository<Exam> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据课程ID查找考试列表
     */
    @Query("SELECT e FROM Exam e WHERE e.courseId = :courseId AND e.deleted = 0 ORDER BY e.startTime DESC")
    List<Exam> findByCourseId(@Param("courseId") Long courseId);

    /**
     * 分页根据课程ID查找考试列表
     */
    @Query("SELECT e FROM Exam e WHERE e.courseId = :courseId AND e.deleted = 0")
    Page<Exam> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    /**
     * 根据教师ID查找考试列表
     */
    @Query("SELECT e FROM Exam e WHERE e.teacherId = :teacherId AND e.deleted = 0 ORDER BY e.startTime DESC")
    List<Exam> findByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 分页根据教师ID查找考试列表
     */
    @Query("SELECT e FROM Exam e WHERE e.teacherId = :teacherId AND e.deleted = 0")
    Page<Exam> findByTeacherId(@Param("teacherId") Long teacherId, Pageable pageable);

    /**
     * 根据考试类型查找考试列表
     */
    @Query("SELECT e FROM Exam e WHERE e.examType = :type AND e.deleted = 0 ORDER BY e.startTime DESC")
    List<Exam> findByExamType(@Param("type") String examType);

    /**
     * 根据考试状态查找考试列表
     */
    @Query("SELECT e FROM Exam e WHERE e.examStatus = :status AND e.deleted = 0 ORDER BY e.startTime DESC")
    List<Exam> findByExamStatus(@Param("status") String examStatus);

    /**
     * 根据考试日期范围查找考试
     */
    @Query("SELECT e FROM Exam e WHERE e.startTime BETWEEN :startDate AND :endDate AND e.deleted = 0 ORDER BY e.startTime")
    List<Exam> findByExamDateBetween(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找考试
     */
    @Query("SELECT e FROM Exam e WHERE " +
           "(:courseId IS NULL OR e.courseId = :courseId) AND " +
           "(:teacherId IS NULL OR e.teacherId = :teacherId) AND " +
           "(:examType IS NULL OR e.examType = :examType) AND " +
           "(:examStatus IS NULL OR e.examStatus = :examStatus) AND " +
           "e.deleted = 0")
    Page<Exam> findByMultipleConditions(@Param("courseId") Long courseId,
                                       @Param("teacherId") Long teacherId,
                                       @Param("examType") String examType,
                                       @Param("examStatus") String examStatus,
                                       Pageable pageable);

    /**
     * 搜索考试（根据考试名称、描述等关键词）
     */
    @Query("SELECT e FROM Exam e WHERE " +
           "(e.title LIKE %:keyword% OR " +
           "e.description LIKE %:keyword%) AND " +
           "e.deleted = 0 ORDER BY e.startTime DESC")
    List<Exam> searchExams(@Param("keyword") String keyword);

    /**
     * 分页搜索考试
     */
    @Query("SELECT e FROM Exam e WHERE " +
           "(e.title LIKE %:keyword% OR " +
           "e.description LIKE %:keyword%) AND " +
           "e.deleted = 0")
    Page<Exam> searchExams(@Param("keyword") String keyword, Pageable pageable);

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 查找考试并预加载课程信息
     */
    @Query("SELECT DISTINCT e FROM Exam e LEFT JOIN FETCH e.course WHERE e.deleted = 0")
    List<Exam> findAllWithCourse();

    /**
     * 查找考试并预加载教师信息
     */
    @Query("SELECT DISTINCT e FROM Exam e LEFT JOIN FETCH e.teacher WHERE e.deleted = 0")
    List<Exam> findAllWithTeacher();

    /**
     * 查找考试并预加载所有关联信息
     */
    @Query("SELECT DISTINCT e FROM Exam e " +
           "LEFT JOIN FETCH e.course c " +
           "LEFT JOIN FETCH e.teacher t " +
           "WHERE e.deleted = 0")
    List<Exam> findAllWithAssociations();

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据课程统计考试数量
     */
    @Query("SELECT c.courseName, COUNT(e) FROM Exam e LEFT JOIN e.course c WHERE e.deleted = 0 GROUP BY e.courseId, c.courseName ORDER BY COUNT(e) DESC")
    List<Object[]> countByCourse();

    /**
     * 根据考试类型统计数量
     */
    @Query("SELECT e.examType, COUNT(e) FROM Exam e WHERE e.deleted = 0 GROUP BY e.examType ORDER BY COUNT(e) DESC")
    List<Object[]> countByType();

    /**
     * 根据考试状态统计数量
     */
    @Query("SELECT e.examStatus, COUNT(e) FROM Exam e WHERE e.deleted = 0 GROUP BY e.examStatus")
    List<Object[]> countByStatus();

    /**
     * 根据教师统计考试数量
     */
    @Query("SELECT u.realName, COUNT(e) FROM Exam e LEFT JOIN e.teacher u WHERE e.deleted = 0 GROUP BY e.teacherId, u.realName ORDER BY COUNT(e) DESC")
    List<Object[]> countByTeacher();

    // ================================
    // 时间相关查询
    // ================================

    /**
     * 查找即将开始的考试（未来7天内）
     */
    @Query("SELECT e FROM Exam e WHERE e.startTime BETWEEN CURRENT_TIMESTAMP AND :sevenDaysLater AND e.examStatus = 'scheduled' AND e.deleted = 0 ORDER BY e.startTime")
    List<Exam> findUpcomingExams(@Param("sevenDaysLater") LocalDateTime sevenDaysLater);

    /**
     * 查找正在进行的考试
     */
    @Query("SELECT e FROM Exam e WHERE e.startTime <= CURRENT_TIMESTAMP AND e.endTime >= CURRENT_TIMESTAMP AND e.examStatus = 'in_progress' AND e.deleted = 0 ORDER BY e.startTime")
    List<Exam> findOngoingExams();

    /**
     * 查找已结束的考试
     */
    @Query("SELECT e FROM Exam e WHERE e.endTime < CURRENT_TIMESTAMP AND e.deleted = 0 ORDER BY e.startTime DESC")
    List<Exam> findFinishedExams();

    /**
     * 查找今日考试
     */
    @Query("SELECT e FROM Exam e WHERE DATE(e.startTime) = CURRENT_DATE AND e.deleted = 0 ORDER BY e.startTime")
    List<Exam> findTodayExams();

    // ================================
    // 学生相关查询
    // ================================

    /**
     * 根据学生ID查找可参加的考试
     */
    @Query("SELECT e FROM Exam e " +
           "INNER JOIN CourseSelection cs ON e.courseId = cs.courseId " +
           "WHERE cs.studentId = :studentId AND cs.deleted = 0 AND e.deleted = 0 " +
           "ORDER BY e.startTime")
    List<Exam> findExamsByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据学生ID查找已参加的考试
     */
    @Query("SELECT e FROM Exam e " +
           "INNER JOIN ExamRecord er ON e.id = er.examId " +
           "WHERE er.studentId = :studentId AND er.deleted = 0 AND e.deleted = 0 " +
           "ORDER BY e.startTime DESC")
    List<Exam> findTakenExamsByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据学生ID查找未参加的考试
     */
    @Query("SELECT e FROM Exam e " +
           "INNER JOIN CourseSelection cs ON e.courseId = cs.courseId " +
           "LEFT JOIN ExamRecord er ON e.id = er.examId AND er.studentId = :studentId " +
           "WHERE cs.studentId = :studentId AND cs.deleted = 0 AND e.deleted = 0 AND er.id IS NULL " +
           "ORDER BY e.startTime")
    List<Exam> findUntakenExamsByStudentId(@Param("studentId") Long studentId);

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查考试名称是否存在（同一课程内）
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Exam e WHERE e.title = :examName AND e.courseId = :courseId AND e.deleted = 0")
    boolean existsByExamNameAndCourseId(@Param("examName") String examName, @Param("courseId") Long courseId);

    /**
     * 检查考试名称是否存在（同一课程内，排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Exam e WHERE e.title = :examName AND e.courseId = :courseId AND e.id != :excludeId AND e.deleted = 0")
    boolean existsByExamNameAndCourseIdAndIdNot(@Param("examName") String examName, @Param("courseId") Long courseId, @Param("excludeId") Long excludeId);

    /**
     * 检查指定时间段是否有考试冲突
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Exam e WHERE " +
           "((e.startTime BETWEEN :startTime AND :endTime) OR (e.endTime BETWEEN :startTime AND :endTime) OR " +
           "(e.startTime <= :startTime AND e.endTime >= :endTime)) AND e.deleted = 0")
    boolean hasTimeConflict(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新考试状态
     */
    @Modifying
    @Query("UPDATE Exam e SET e.examStatus = :status, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id = :examId")
    int updateExamStatus(@Param("examId") Long examId, @Param("status") String examStatus);

    /**
     * 批量更新考试状态
     */
    @Modifying
    @Query("UPDATE Exam e SET e.examStatus = :status, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id IN :examIds")
    int batchUpdateExamStatus(@Param("examIds") List<Long> examIds, @Param("status") String examStatus);

    /**
     * 更新考试时间
     */
    @Modifying
    @Query("UPDATE Exam e SET e.startTime = :startTime, e.endTime = :endTime, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id = :examId")
    int updateExamTime(@Param("examId") Long examId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 更新考试总分
     */
    @Modifying
    @Query("UPDATE Exam e SET e.totalScore = :totalScore, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id = :examId")
    int updateTotalScore(@Param("examId") Long examId, @Param("totalScore") Integer totalScore);

}
