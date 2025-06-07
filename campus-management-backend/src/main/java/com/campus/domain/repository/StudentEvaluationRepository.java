package com.campus.domain.repository;

import com.campus.domain.entity.StudentEvaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 学生评价Repository接口
 * 提供学生评价相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface StudentEvaluationRepository extends BaseRepository<StudentEvaluation> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据学生ID查找评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.studentId = :studentId AND se.deleted = 0 ORDER BY se.evaluationDate DESC")
    List<StudentEvaluation> findByStudentId(@Param("studentId") Long studentId);

    /**
     * 分页根据学生ID查找评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.studentId = :studentId AND se.deleted = 0")
    Page<StudentEvaluation> findByStudentId(@Param("studentId") Long studentId, Pageable pageable);

    /**
     * 根据课程ID查找评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.courseId = :courseId AND se.deleted = 0 ORDER BY se.evaluationDate DESC")
    List<StudentEvaluation> findByCourseId(@Param("courseId") Long courseId);

    /**
     * 分页根据课程ID查找评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.courseId = :courseId AND se.deleted = 0")
    Page<StudentEvaluation> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    /**
     * 根据教师ID查找评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.teacherId = :teacherId AND se.deleted = 0 ORDER BY se.evaluationDate DESC")
    List<StudentEvaluation> findByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 分页根据教师ID查找评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.teacherId = :teacherId AND se.deleted = 0")
    Page<StudentEvaluation> findByTeacherId(@Param("teacherId") Long teacherId, Pageable pageable);

    /**
     * 根据评价类型查找评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.evaluationType = :evaluationType AND se.deleted = 0 ORDER BY se.evaluationDate DESC")
    List<StudentEvaluation> findByEvaluationType(@Param("evaluationType") String evaluationType);

    /**
     * 根据评价等级查找评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.rating = :rating AND se.deleted = 0 ORDER BY se.evaluationDate DESC")
    List<StudentEvaluation> findByRating(@Param("rating") Integer rating);

    /**
     * 根据学期查找评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.semester = :semester AND se.deleted = 0 ORDER BY se.evaluationDate DESC")
    List<StudentEvaluation> findBySemester(@Param("semester") String semester);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE " +
           "(:studentId IS NULL OR se.studentId = :studentId) AND " +
           "(:courseId IS NULL OR se.courseId = :courseId) AND " +
           "(:teacherId IS NULL OR se.teacherId = :teacherId) AND " +
           "(:evaluationType IS NULL OR se.evaluationType = :evaluationType) AND " +
           "(:semester IS NULL OR se.semester = :semester) AND " +
           "se.deleted = 0")
    Page<StudentEvaluation> findByMultipleConditions(@Param("studentId") Long studentId,
                                                    @Param("courseId") Long courseId,
                                                    @Param("teacherId") Long teacherId,
                                                    @Param("evaluationType") String evaluationType,
                                                    @Param("semester") String semester,
                                                    Pageable pageable);

    /**
     * 根据学生和课程查找评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.studentId = :studentId AND se.courseId = :courseId AND se.deleted = 0 ORDER BY se.evaluationDate DESC")
    List<StudentEvaluation> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 根据学生和教师查找评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.studentId = :studentId AND se.teacherId = :teacherId AND se.deleted = 0 ORDER BY se.evaluationDate DESC")
    List<StudentEvaluation> findByStudentIdAndTeacherId(@Param("studentId") Long studentId, @Param("teacherId") Long teacherId);

    /**
     * 根据课程和教师查找评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.courseId = :courseId AND se.teacherId = :teacherId AND se.deleted = 0 ORDER BY se.evaluationDate DESC")
    List<StudentEvaluation> findByCourseIdAndTeacherId(@Param("courseId") Long courseId, @Param("teacherId") Long teacherId);

    /**
     * 搜索评价（根据评价内容等关键词）
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE " +
           "(se.evaluationContent LIKE %:keyword% OR " +
           "se.comments LIKE %:keyword%) AND " +
           "se.deleted = 0 ORDER BY se.evaluationDate DESC")
    List<StudentEvaluation> searchEvaluations(@Param("keyword") String keyword);

    /**
     * 分页搜索评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE " +
           "(se.evaluationContent LIKE %:keyword% OR " +
           "se.comments LIKE %:keyword%) AND " +
           "se.deleted = 0")
    Page<StudentEvaluation> searchEvaluations(@Param("keyword") String keyword, Pageable pageable);

    // ================================
    // 评分相关查询
    // ================================

    /**
     * 根据评分范围查找评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.score BETWEEN :minScore AND :maxScore AND se.deleted = 0 ORDER BY se.score DESC")
    List<StudentEvaluation> findByScoreBetween(@Param("minScore") BigDecimal minScore, @Param("maxScore") BigDecimal maxScore);

    /**
     * 查找高分评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.score >= :scoreThreshold AND se.deleted = 0 ORDER BY se.score DESC")
    List<StudentEvaluation> findHighScoreEvaluations(@Param("scoreThreshold") BigDecimal scoreThreshold);

    /**
     * 查找低分评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.score <= :scoreThreshold AND se.deleted = 0 ORDER BY se.score ASC")
    List<StudentEvaluation> findLowScoreEvaluations(@Param("scoreThreshold") BigDecimal scoreThreshold);

    // ================================
    // 时间相关查询
    // ================================

    /**
     * 根据评价时间范围查找评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.evaluationDate BETWEEN :startDate AND :endDate AND se.deleted = 0 ORDER BY se.evaluationDate DESC")
    List<StudentEvaluation> findByEvaluationDateBetween(@Param("startDate") LocalDateTime startDate, 
                                                       @Param("endDate") LocalDateTime endDate);

    /**
     * 分页根据评价时间范围查找评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.evaluationDate BETWEEN :startDate AND :endDate AND se.deleted = 0")
    Page<StudentEvaluation> findByEvaluationDateBetween(@Param("startDate") LocalDateTime startDate, 
                                                       @Param("endDate") LocalDateTime endDate, 
                                                       Pageable pageable);

    /**
     * 查找最近的评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.deleted = 0 ORDER BY se.evaluationDate DESC")
    List<StudentEvaluation> findRecentEvaluations(Pageable pageable);

    /**
     * 查找今日评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE DATE(se.evaluationDate) = CURRENT_DATE AND se.deleted = 0 ORDER BY se.evaluationDate DESC")
    List<StudentEvaluation> findTodayEvaluations();

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 查找评价并预加载学生信息
     */
    @Query("SELECT DISTINCT se FROM StudentEvaluation se LEFT JOIN FETCH se.student WHERE se.deleted = 0")
    List<StudentEvaluation> findAllWithStudent();

    /**
     * 查找评价并预加载课程信息
     */
    @Query("SELECT DISTINCT se FROM StudentEvaluation se LEFT JOIN FETCH se.course WHERE se.deleted = 0")
    List<StudentEvaluation> findAllWithCourse();

    /**
     * 查找评价并预加载教师信息
     */
    @Query("SELECT DISTINCT se FROM StudentEvaluation se LEFT JOIN FETCH se.teacher WHERE se.deleted = 0")
    List<StudentEvaluation> findAllWithTeacher();

    /**
     * 查找评价并预加载所有关联信息
     */
    @Query("SELECT DISTINCT se FROM StudentEvaluation se " +
           "LEFT JOIN FETCH se.student s " +
           "LEFT JOIN FETCH se.course c " +
           "LEFT JOIN FETCH se.teacher t " +
           "WHERE se.deleted = 0")
    List<StudentEvaluation> findAllWithAssociations();

    /**
     * 根据课程ID查找评价并预加载学生信息
     */
    @Query("SELECT DISTINCT se FROM StudentEvaluation se LEFT JOIN FETCH se.student WHERE se.courseId = :courseId AND se.deleted = 0 ORDER BY se.evaluationDate DESC")
    List<StudentEvaluation> findByCourseIdWithStudent(@Param("courseId") Long courseId);

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据评价类型统计数量
     */
    @Query("SELECT se.evaluationType, COUNT(se) FROM StudentEvaluation se WHERE se.deleted = 0 GROUP BY se.evaluationType ORDER BY COUNT(se) DESC")
    List<Object[]> countByEvaluationType();

    /**
     * 根据评价等级统计数量
     */
    @Query("SELECT se.rating, COUNT(se) FROM StudentEvaluation se WHERE se.deleted = 0 GROUP BY se.rating ORDER BY se.rating")
    List<Object[]> countByRating();

    /**
     * 根据课程统计评价数量
     */
    @Query("SELECT c.courseName, COUNT(se) FROM StudentEvaluation se LEFT JOIN se.course c WHERE se.deleted = 0 GROUP BY se.courseId, c.courseName ORDER BY COUNT(se) DESC")
    List<Object[]> countByCourse();

    /**
     * 根据教师统计评价数量
     */
    @Query("SELECT u.realName, COUNT(se) FROM StudentEvaluation se LEFT JOIN se.teacher u WHERE se.deleted = 0 GROUP BY se.teacherId, u.realName ORDER BY COUNT(se) DESC")
    List<Object[]> countByTeacher();

    /**
     * 根据学期统计评价数量
     */
    @Query("SELECT se.semester, COUNT(se) FROM StudentEvaluation se WHERE se.deleted = 0 GROUP BY se.semester ORDER BY se.semester")
    List<Object[]> countBySemester();

    /**
     * 统计指定课程的评价数量
     */
    @Query("SELECT COUNT(se) FROM StudentEvaluation se WHERE se.courseId = :courseId AND se.deleted = 0")
    long countByCourseId(@Param("courseId") Long courseId);

    /**
     * 统计指定教师的评价数量
     */
    @Query("SELECT COUNT(se) FROM StudentEvaluation se WHERE se.teacherId = :teacherId AND se.deleted = 0")
    long countByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 统计指定学生的评价数量
     */
    @Query("SELECT COUNT(se) FROM StudentEvaluation se WHERE se.studentId = :studentId AND se.deleted = 0")
    long countByStudentId(@Param("studentId") Long studentId);

    // ================================
    // 评分统计方法
    // ================================

    /**
     * 计算指定课程的平均评分
     */
    @Query("SELECT AVG(se.score) FROM StudentEvaluation se WHERE se.courseId = :courseId AND se.deleted = 0")
    BigDecimal calculateAverageScoreByCourseId(@Param("courseId") Long courseId);

    /**
     * 计算指定教师的平均评分
     */
    @Query("SELECT AVG(se.score) FROM StudentEvaluation se WHERE se.teacherId = :teacherId AND se.deleted = 0")
    BigDecimal calculateAverageScoreByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 计算指定课程和教师的平均评分
     */
    @Query("SELECT AVG(se.score) FROM StudentEvaluation se WHERE se.courseId = :courseId AND se.teacherId = :teacherId AND se.deleted = 0")
    BigDecimal calculateAverageScoreByCourseIdAndTeacherId(@Param("courseId") Long courseId, @Param("teacherId") Long teacherId);

    /**
     * 获取指定课程的评分分布
     */
    @Query("SELECT se.rating, COUNT(se) FROM StudentEvaluation se WHERE se.courseId = :courseId AND se.deleted = 0 GROUP BY se.rating ORDER BY se.rating")
    List<Object[]> getRatingDistributionByCourseId(@Param("courseId") Long courseId);

    /**
     * 获取指定教师的评分分布
     */
    @Query("SELECT se.rating, COUNT(se) FROM StudentEvaluation se WHERE se.teacherId = :teacherId AND se.deleted = 0 GROUP BY se.rating ORDER BY se.rating")
    List<Object[]> getRatingDistributionByTeacherId(@Param("teacherId") Long teacherId);

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查学生是否已评价指定课程
     */
    @Query("SELECT CASE WHEN COUNT(se) > 0 THEN true ELSE false END FROM StudentEvaluation se WHERE se.studentId = :studentId AND se.courseId = :courseId AND se.deleted = 0")
    boolean existsByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 检查学生是否已评价指定教师
     */
    @Query("SELECT CASE WHEN COUNT(se) > 0 THEN true ELSE false END FROM StudentEvaluation se WHERE se.studentId = :studentId AND se.teacherId = :teacherId AND se.deleted = 0")
    boolean existsByStudentIdAndTeacherId(@Param("studentId") Long studentId, @Param("teacherId") Long teacherId);

    /**
     * 检查学生是否已评价指定课程和教师
     */
    @Query("SELECT CASE WHEN COUNT(se) > 0 THEN true ELSE false END FROM StudentEvaluation se WHERE se.studentId = :studentId AND se.courseId = :courseId AND se.teacherId = :teacherId AND se.deleted = 0")
    boolean existsByStudentIdAndCourseIdAndTeacherId(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("teacherId") Long teacherId);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新评价状态
     */
    @Modifying
    @Query("UPDATE StudentEvaluation se SET se.status = :status, se.updatedAt = CURRENT_TIMESTAMP WHERE se.id = :evaluationId")
    int updateEvaluationStatus(@Param("evaluationId") Long evaluationId, @Param("status") Integer status);

    /**
     * 批量更新评价状态
     */
    @Modifying
    @Query("UPDATE StudentEvaluation se SET se.status = :status, se.updatedAt = CURRENT_TIMESTAMP WHERE se.id IN :evaluationIds")
    int batchUpdateEvaluationStatus(@Param("evaluationIds") List<Long> evaluationIds, @Param("status") Integer status);

    /**
     * 更新评价分数
     */
    @Modifying
    @Query("UPDATE StudentEvaluation se SET se.score = :score, se.updatedAt = CURRENT_TIMESTAMP WHERE se.id = :evaluationId")
    int updateScore(@Param("evaluationId") Long evaluationId, @Param("score") BigDecimal score);

    /**
     * 更新评价等级
     */
    @Modifying
    @Query("UPDATE StudentEvaluation se SET se.rating = :rating, se.updatedAt = CURRENT_TIMESTAMP WHERE se.id = :evaluationId")
    int updateRating(@Param("evaluationId") Long evaluationId, @Param("rating") Integer rating);

}
