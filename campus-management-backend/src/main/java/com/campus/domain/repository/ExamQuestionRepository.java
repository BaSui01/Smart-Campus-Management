package com.campus.domain.repository;

import com.campus.domain.entity.ExamQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 考试题目Repository接口
 * 提供考试题目相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface ExamQuestionRepository extends BaseRepository<ExamQuestion> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据考试ID查找题目列表
     */
    @Query("SELECT q FROM ExamQuestion q WHERE q.examId = :examId AND q.deleted = 0 ORDER BY q.questionOrder ASC")
    List<ExamQuestion> findByExamId(@Param("examId") Long examId);

    /**
     * 分页根据考试ID查找题目列表
     */
    @Query("SELECT q FROM ExamQuestion q WHERE q.examId = :examId AND q.deleted = 0")
    Page<ExamQuestion> findByExamId(@Param("examId") Long examId, Pageable pageable);

    /**
     * 根据题目类型查找题目列表
     */
    @Query("SELECT q FROM ExamQuestion q WHERE q.questionType = :type AND q.deleted = 0 ORDER BY q.questionOrder ASC")
    List<ExamQuestion> findByQuestionType(@Param("type") String questionType);

    /**
     * 根据难度级别查找题目列表
     */
    @Query("SELECT q FROM ExamQuestion q WHERE q.difficultyLevel = :level AND q.deleted = 0 ORDER BY q.questionOrder ASC")
    List<ExamQuestion> findByDifficultyLevel(@Param("level") String difficultyLevel);

    /**
     * 根据分值查找题目列表
     */
    @Query("SELECT q FROM ExamQuestion q WHERE q.score = :score AND q.deleted = 0 ORDER BY q.questionOrder ASC")
    List<ExamQuestion> findByScore(@Param("score") Integer score);

    /**
     * 根据分值范围查找题目
     */
    @Query("SELECT q FROM ExamQuestion q WHERE q.score BETWEEN :minScore AND :maxScore AND q.deleted = 0 ORDER BY q.score ASC")
    List<ExamQuestion> findByScoreBetween(@Param("minScore") Integer minScore, @Param("maxScore") Integer maxScore);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找题目
     */
    @Query("SELECT q FROM ExamQuestion q WHERE " +
           "(:examId IS NULL OR q.examId = :examId) AND " +
           "(:questionType IS NULL OR q.questionType = :questionType) AND " +
           "(:difficultyLevel IS NULL OR q.difficultyLevel = :difficultyLevel) AND " +
           "(:minScore IS NULL OR q.score >= :minScore) AND " +
           "(:maxScore IS NULL OR q.score <= :maxScore) AND " +
           "q.deleted = 0")
    Page<ExamQuestion> findByMultipleConditions(@Param("examId") Long examId,
                                               @Param("questionType") String questionType,
                                               @Param("difficultyLevel") String difficultyLevel,
                                               @Param("minScore") Integer minScore,
                                               @Param("maxScore") Integer maxScore,
                                               Pageable pageable);

    /**
     * 搜索题目（根据题目内容、选项等关键词）
     */
    @Query("SELECT q FROM ExamQuestion q WHERE " +
           "(q.questionContent LIKE %:keyword% OR " +
           "q.optionA LIKE %:keyword% OR " +
           "q.optionB LIKE %:keyword% OR " +
           "q.optionC LIKE %:keyword% OR " +
           "q.optionD LIKE %:keyword% OR " +
           "q.explanation LIKE %:keyword%) AND " +
           "q.deleted = 0 ORDER BY q.questionOrder ASC")
    List<ExamQuestion> searchQuestions(@Param("keyword") String keyword);

    /**
     * 分页搜索题目
     */
    @Query("SELECT q FROM ExamQuestion q WHERE " +
           "(q.questionContent LIKE %:keyword% OR " +
           "q.optionA LIKE %:keyword% OR " +
           "q.optionB LIKE %:keyword% OR " +
           "q.optionC LIKE %:keyword% OR " +
           "q.optionD LIKE %:keyword% OR " +
           "q.explanation LIKE %:keyword%) AND " +
           "q.deleted = 0")
    Page<ExamQuestion> searchQuestions(@Param("keyword") String keyword, Pageable pageable);

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 查找题目并预加载考试信息
     */
    @Query("SELECT DISTINCT q FROM ExamQuestion q LEFT JOIN FETCH q.exam WHERE q.deleted = 0")
    List<ExamQuestion> findAllWithExam();

    /**
     * 根据考试ID查找题目并预加载考试信息
     */
    @Query("SELECT DISTINCT q FROM ExamQuestion q LEFT JOIN FETCH q.exam WHERE q.examId = :examId AND q.deleted = 0 ORDER BY q.questionOrder ASC")
    List<ExamQuestion> findByExamIdWithExam(@Param("examId") Long examId);

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据考试统计题目数量
     */
    @Query("SELECT e.examName, COUNT(q) FROM ExamQuestion q LEFT JOIN q.exam e WHERE q.deleted = 0 GROUP BY q.examId, e.examName ORDER BY COUNT(q) DESC")
    List<Object[]> countByExam();

    /**
     * 根据题目类型统计数量
     */
    @Query("SELECT q.questionType, COUNT(q) FROM ExamQuestion q WHERE q.deleted = 0 GROUP BY q.questionType ORDER BY COUNT(q) DESC")
    List<Object[]> countByQuestionType();

    /**
     * 根据难度级别统计数量
     */
    @Query("SELECT q.difficultyLevel, COUNT(q) FROM ExamQuestion q WHERE q.deleted = 0 GROUP BY q.difficultyLevel ORDER BY q.difficultyLevel")
    List<Object[]> countByDifficultyLevel();

    /**
     * 根据分值统计数量
     */
    @Query("SELECT q.score, COUNT(q) FROM ExamQuestion q WHERE q.deleted = 0 GROUP BY q.score ORDER BY q.score")
    List<Object[]> countByScore();

    /**
     * 统计指定考试的总分
     */
    @Query("SELECT COALESCE(SUM(q.score), 0) FROM ExamQuestion q WHERE q.examId = :examId AND q.deleted = 0")
    Integer getTotalScoreByExamId(@Param("examId") Long examId);

    /**
     * 统计指定考试各类型题目数量
     */
    @Query("SELECT q.questionType, COUNT(q), SUM(q.score) FROM ExamQuestion q WHERE q.examId = :examId AND q.deleted = 0 GROUP BY q.questionType ORDER BY q.questionType")
    List<Object[]> getQuestionStatsByExamId(@Param("examId") Long examId);

    // ================================
    // 排序相关查询
    // ================================

    /**
     * 获取指定考试中的最大题目序号
     */
    @Query("SELECT COALESCE(MAX(q.questionOrder), 0) FROM ExamQuestion q WHERE q.examId = :examId AND q.deleted = 0")
    Integer getMaxQuestionOrderByExamId(@Param("examId") Long examId);

    /**
     * 根据考试ID和序号范围查找题目
     */
    @Query("SELECT q FROM ExamQuestion q WHERE q.examId = :examId AND q.questionOrder BETWEEN :startOrder AND :endOrder AND q.deleted = 0 ORDER BY q.questionOrder ASC")
    List<ExamQuestion> findByExamIdAndOrderBetween(@Param("examId") Long examId, 
                                                  @Param("startOrder") Integer startOrder, 
                                                  @Param("endOrder") Integer endOrder);

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查指定考试中是否存在指定序号的题目
     */
    @Query("SELECT CASE WHEN COUNT(q) > 0 THEN true ELSE false END FROM ExamQuestion q WHERE q.examId = :examId AND q.questionOrder = :order AND q.deleted = 0")
    boolean existsByExamIdAndQuestionOrder(@Param("examId") Long examId, @Param("order") Integer questionOrder);

    /**
     * 检查指定考试中是否存在指定序号的题目（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(q) > 0 THEN true ELSE false END FROM ExamQuestion q WHERE q.examId = :examId AND q.questionOrder = :order AND q.id != :excludeId AND q.deleted = 0")
    boolean existsByExamIdAndQuestionOrderAndIdNot(@Param("examId") Long examId, @Param("order") Integer questionOrder, @Param("excludeId") Long excludeId);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新题目序号
     */
    @Modifying
    @Query("UPDATE ExamQuestion q SET q.questionOrder = :order, q.updatedAt = CURRENT_TIMESTAMP WHERE q.id = :questionId")
    int updateQuestionOrder(@Param("questionId") Long questionId, @Param("order") Integer questionOrder);

    /**
     * 批量更新题目序号
     */
    @Modifying
    @Query("UPDATE ExamQuestion q SET q.questionOrder = q.questionOrder + :increment, q.updatedAt = CURRENT_TIMESTAMP WHERE q.examId = :examId AND q.questionOrder >= :startOrder")
    int batchUpdateQuestionOrder(@Param("examId") Long examId, @Param("startOrder") Integer startOrder, @Param("increment") Integer increment);

    /**
     * 更新题目分值
     */
    @Modifying
    @Query("UPDATE ExamQuestion q SET q.score = :score, q.updatedAt = CURRENT_TIMESTAMP WHERE q.id = :questionId")
    int updateScore(@Param("questionId") Long questionId, @Param("score") Integer score);

    /**
     * 批量更新题目分值
     */
    @Modifying
    @Query("UPDATE ExamQuestion q SET q.score = :score, q.updatedAt = CURRENT_TIMESTAMP WHERE q.id IN :questionIds")
    int batchUpdateScore(@Param("questionIds") List<Long> questionIds, @Param("score") Integer score);

    /**
     * 更新题目难度级别
     */
    @Modifying
    @Query("UPDATE ExamQuestion q SET q.difficultyLevel = :level, q.updatedAt = CURRENT_TIMESTAMP WHERE q.id = :questionId")
    int updateDifficultyLevel(@Param("questionId") Long questionId, @Param("level") String difficultyLevel);

    /**
     * 批量更新题目难度级别
     */
    @Modifying
    @Query("UPDATE ExamQuestion q SET q.difficultyLevel = :level, q.updatedAt = CURRENT_TIMESTAMP WHERE q.id IN :questionIds")
    int batchUpdateDifficultyLevel(@Param("questionIds") List<Long> questionIds, @Param("level") String difficultyLevel);

}
