package com.campus.domain.repository;

import com.campus.domain.entity.StudentEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 学生评价Repository接口
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface StudentEvaluationRepository extends JpaRepository<StudentEvaluation, Long> {
    
    /**
     * 根据学生ID查找评价列表
     */
    List<StudentEvaluation> findByStudentIdAndDeletedOrderByCreatedAtDesc(Long studentId, Integer deleted);
    
    /**
     * 根据评价者ID查找评价列表
     */
    List<StudentEvaluation> findByEvaluatorIdAndDeletedOrderByCreatedAtDesc(Long evaluatorId, Integer deleted);
    
    /**
     * 根据学生和评价类型查找评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.student.id = :studentId AND se.evaluationType = :type AND se.deleted = 0")
    List<StudentEvaluation> findByStudentAndType(@Param("studentId") Long studentId, @Param("type") String evaluationType);
    
    /**
     * 根据日期范围查找评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.createdAt BETWEEN :startDate AND :endDate AND se.deleted = 0")
    List<StudentEvaluation> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * 根据ID和删除状态查找评价
     */
    Optional<StudentEvaluation> findByIdAndDeleted(Long id, Integer deleted);
    
    /**
     * 根据评分范围查找评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.overallScore BETWEEN :minScore AND :maxScore AND se.deleted = 0")
    List<StudentEvaluation> findByScoreRange(@Param("minScore") Double minScore, @Param("maxScore") Double maxScore);
    
    /**
     * 根据学期查找评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.semester = :semester AND se.deleted = 0")
    List<StudentEvaluation> findBySemester(@Param("semester") String semester);
    
    /**
     * 统计学生评价数量
     */
    long countByStudentIdAndDeleted(Long studentId, Integer deleted);
    
    /**
     * 统计评价者的评价数量
     */
    long countByEvaluatorIdAndDeleted(Long evaluatorId, Integer deleted);
    
    /**
     * 计算学生平均分
     */
    @Query("SELECT AVG(se.overallScore) FROM StudentEvaluation se WHERE se.student.id = :studentId AND se.deleted = 0")
    Double calculateAverageScore(@Param("studentId") Long studentId);
    
    /**
     * 根据评价类型统计数量
     */
    @Query("SELECT se.evaluationType, COUNT(se) FROM StudentEvaluation se WHERE se.deleted = 0 GROUP BY se.evaluationType")
    List<Object[]> countByEvaluationType();
    
    /**
     * 查找最新的评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.student.id = :studentId AND se.deleted = 0 ORDER BY se.createdAt DESC")
    List<StudentEvaluation> findLatestEvaluations(@Param("studentId") Long studentId);
    
    /**
     * 检查是否已存在评价
     */
    boolean existsByStudentIdAndEvaluatorIdAndEvaluationTypeAndDeleted(Long studentId, Long evaluatorId, String evaluationType, Integer deleted);

    // ================================
    // StudentEvaluationServiceImpl需要的方法
    // ================================

    /**
     * 分页根据删除状态查找评价，按创建时间倒序
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.deleted = :deleted ORDER BY se.createdAt DESC")
    org.springframework.data.domain.Page<StudentEvaluation> findByDeletedOrderByCreatedAtDesc(@Param("deleted") Integer deleted, org.springframework.data.domain.Pageable pageable);

    /**
     * 根据日期范围和删除状态查找评价，按创建时间倒序
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.createdAt BETWEEN :startDate AND :endDate AND se.deleted = :deleted ORDER BY se.createdAt DESC")
    List<StudentEvaluation> findByCreatedAtBetweenAndDeletedOrderByCreatedAtDesc(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("deleted") Integer deleted);

    /**
     * 根据评分范围和删除状态查找评价，按评分倒序
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.overallScore BETWEEN :minScore AND :maxScore AND se.deleted = :deleted ORDER BY se.overallScore DESC")
    List<StudentEvaluation> findByScoreBetweenAndDeletedOrderByScoreDesc(@Param("minScore") Double minScore, @Param("maxScore") Double maxScore, @Param("deleted") Integer deleted);

    /**
     * 根据学期和删除状态查找评价，按创建时间倒序
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.semester = :semester AND se.deleted = :deleted ORDER BY se.createdAt DESC")
    List<StudentEvaluation> findBySemesterAndDeletedOrderByCreatedAtDesc(@Param("semester") String semester, @Param("deleted") Integer deleted);

    /**
     * 计算学生指定类型平均分
     */
    @Query("SELECT AVG(se.overallScore) FROM StudentEvaluation se WHERE se.studentId = :studentId AND se.evaluationType = :evaluationType AND se.deleted = 0")
    Double calculateAverageScoreByType(@Param("studentId") Long studentId, @Param("evaluationType") String evaluationType);

    /**
     * 获取学生最新评价（限制数量）
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.studentId = :studentId AND se.deleted = :deleted ORDER BY se.createdAt DESC")
    List<StudentEvaluation> findTopByStudentIdAndDeletedOrderByCreatedAtDesc(@Param("studentId") Long studentId, @Param("deleted") Integer deleted, org.springframework.data.domain.Pageable pageable);

    /**
     * 根据学生统计评价类型数量
     */
    @Query("SELECT se.evaluationType, COUNT(se) FROM StudentEvaluation se WHERE se.studentId = :studentId AND se.deleted = 0 GROUP BY se.evaluationType")
    List<Object[]> countByEvaluationTypeForStudent(@Param("studentId") Long studentId);

    /**
     * 根据评价内容模糊查询，按创建时间倒序
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE (se.strengths LIKE %:keyword% OR se.improvements LIKE %:keyword% OR se.developmentSuggestions LIKE %:keyword% OR se.remarks LIKE %:keyword%) AND se.deleted = :deleted ORDER BY se.createdAt DESC")
    org.springframework.data.domain.Page<StudentEvaluation> findByCommentContainingIgnoreCaseAndDeletedOrderByCreatedAtDesc(@Param("keyword") String keyword, @Param("deleted") Integer deleted, org.springframework.data.domain.Pageable pageable);

    /**
     * 根据评价内容模糊查询
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE (se.strengths LIKE %:content% OR se.improvements LIKE %:content% OR se.developmentSuggestions LIKE %:content% OR se.remarks LIKE %:content%) AND se.deleted = :deleted")
    List<StudentEvaluation> findByCommentContainingIgnoreCaseAndDeleted(@Param("content") String content, @Param("deleted") Integer deleted);

    /**
     * 根据删除状态查找所有评价，按创建时间倒序
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.deleted = :deleted ORDER BY se.createdAt DESC")
    List<StudentEvaluation> findByDeletedOrderByCreatedAtDesc(@Param("deleted") Integer deleted);

    /**
     * 根据创建时间查找过期评价
     */
    @Query("SELECT se FROM StudentEvaluation se WHERE se.createdAt < :cutoffDate AND se.deleted = :deleted")
    List<StudentEvaluation> findByCreatedAtBeforeAndDeleted(@Param("cutoffDate") LocalDateTime cutoffDate, @Param("deleted") Integer deleted);
}
