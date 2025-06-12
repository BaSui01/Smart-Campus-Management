package com.campus.domain.repository.exam;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campus.domain.entity.exam.ExamRecord;
import com.campus.domain.repository.infrastructure.BaseRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 考试记录Repository接口
 * 提供考试记录相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface ExamRecordRepository extends BaseRepository<ExamRecord> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据考试ID查找考试记录
     */
    @Query("SELECT r FROM ExamRecord r WHERE r.examId = :examId AND r.deleted = 0 ORDER BY r.score DESC")
    List<ExamRecord> findByExamId(@Param("examId") Long examId);

    /**
     * 分页根据考试ID查找考试记录
     */
    @Query("SELECT r FROM ExamRecord r WHERE r.examId = :examId AND r.deleted = 0")
    Page<ExamRecord> findByExamId(@Param("examId") Long examId, Pageable pageable);

    /**
     * 根据学生ID查找考试记录
     */
    @Query("SELECT r FROM ExamRecord r WHERE r.studentId = :studentId AND r.deleted = 0 ORDER BY r.startTime DESC")
    List<ExamRecord> findByStudentId(@Param("studentId") Long studentId);

    /**
     * 分页根据学生ID查找考试记录
     */
    @Query("SELECT r FROM ExamRecord r WHERE r.studentId = :studentId AND r.deleted = 0")
    Page<ExamRecord> findByStudentId(@Param("studentId") Long studentId, Pageable pageable);

    /**
     * 根据考试ID和学生ID查找考试记录
     */
    @Query("SELECT r FROM ExamRecord r WHERE r.examId = :examId AND r.studentId = :studentId AND r.deleted = 0")
    Optional<ExamRecord> findByExamIdAndStudentId(@Param("examId") Long examId, @Param("studentId") Long studentId);

    /**
     * 根据考试状态查找考试记录
     */
    @Query("SELECT r FROM ExamRecord r WHERE r.examStatus = :status AND r.deleted = 0 ORDER BY r.startTime DESC")
    List<ExamRecord> findByExamStatus(@Param("status") String examStatus);

    /**
     * 根据分数范围查找考试记录
     */
    @Query("SELECT r FROM ExamRecord r WHERE r.score BETWEEN :minScore AND :maxScore AND r.deleted = 0 ORDER BY r.score DESC")
    List<ExamRecord> findByScoreBetween(@Param("minScore") Integer minScore, @Param("maxScore") Integer maxScore);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找考试记录
     */
    @Query("SELECT r FROM ExamRecord r WHERE " +
           "(:examId IS NULL OR r.examId = :examId) AND " +
           "(:studentId IS NULL OR r.studentId = :studentId) AND " +
           "(:examStatus IS NULL OR r.examStatus = :examStatus) AND " +
           "(:minScore IS NULL OR r.score >= :minScore) AND " +
           "(:maxScore IS NULL OR r.score <= :maxScore) AND " +
           "r.deleted = 0")
    Page<ExamRecord> findByMultipleConditions(@Param("examId") Long examId,
                                             @Param("studentId") Long studentId,
                                             @Param("examStatus") String examStatus,
                                             @Param("minScore") Integer minScore,
                                             @Param("maxScore") Integer maxScore,
                                             Pageable pageable);

    /**
     * 搜索考试记录（根据答案内容等关键词）
     */
    @Query("SELECT r FROM ExamRecord r WHERE " +
           "r.answerDetails LIKE %:keyword% AND " +
           "r.deleted = 0 ORDER BY r.startTime DESC")
    List<ExamRecord> searchRecords(@Param("keyword") String keyword);

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 查找考试记录并预加载考试信息
     */
    @Query("SELECT DISTINCT r FROM ExamRecord r LEFT JOIN FETCH r.exam WHERE r.deleted = 0")
    List<ExamRecord> findAllWithExam();

    /**
     * 查找考试记录并预加载学生信息
     */
    @Query("SELECT DISTINCT r FROM ExamRecord r LEFT JOIN FETCH r.student WHERE r.deleted = 0")
    List<ExamRecord> findAllWithStudent();

    /**
     * 查找考试记录并预加载所有关联信息
     */
    @Query("SELECT DISTINCT r FROM ExamRecord r " +
           "LEFT JOIN FETCH r.exam e " +
           "LEFT JOIN FETCH r.student s " +
           "WHERE r.deleted = 0")
    List<ExamRecord> findAllWithAssociations();

    /**
     * 根据考试ID查找记录并预加载学生信息
     */
    @Query("SELECT DISTINCT r FROM ExamRecord r LEFT JOIN FETCH r.student WHERE r.examId = :examId AND r.deleted = 0 ORDER BY r.score DESC")
    List<ExamRecord> findByExamIdWithStudent(@Param("examId") Long examId);

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据考试统计记录数量
     */
    @Query("SELECT e.title, COUNT(r) FROM ExamRecord r LEFT JOIN r.exam e WHERE r.deleted = 0 GROUP BY r.examId, e.title ORDER BY COUNT(r) DESC")
    List<Object[]> countByExam();

    /**
     * 根据学生统计考试记录数量
     */
    @Query("SELECT s.studentNo, COUNT(r) FROM ExamRecord r LEFT JOIN r.student s WHERE r.deleted = 0 GROUP BY r.studentId, s.studentNo ORDER BY COUNT(r) DESC")
    List<Object[]> countByStudent();

    /**
     * 根据考试状态统计数量
     */
    @Query("SELECT r.examStatus, COUNT(r) FROM ExamRecord r WHERE r.deleted = 0 GROUP BY r.examStatus")
    List<Object[]> countByExamStatus();

    /**
     * 统计指定考试的成绩分布
     */
    @Query("SELECT " +
           "CASE " +
           "WHEN r.score >= 90 THEN '优秀(90-100)' " +
           "WHEN r.score >= 80 THEN '良好(80-89)' " +
           "WHEN r.score >= 70 THEN '中等(70-79)' " +
           "WHEN r.score >= 60 THEN '及格(60-69)' " +
           "ELSE '不及格(0-59)' " +
           "END as gradeLevel, COUNT(r) " +
           "FROM ExamRecord r WHERE r.examId = :examId AND r.deleted = 0 " +
           "GROUP BY " +
           "CASE " +
           "WHEN r.score >= 90 THEN '优秀(90-100)' " +
           "WHEN r.score >= 80 THEN '良好(80-89)' " +
           "WHEN r.score >= 70 THEN '中等(70-79)' " +
           "WHEN r.score >= 60 THEN '及格(60-69)' " +
           "ELSE '不及格(0-59)' " +
           "END " +
           "ORDER BY MIN(r.score) DESC")
    List<Object[]> getGradeDistributionByExamId(@Param("examId") Long examId);

    /**
     * 统计指定考试的基本统计信息
     */
    @Query("SELECT " +
           "COUNT(r) as totalCount, " +
           "AVG(r.score) as avgScore, " +
           "MAX(r.score) as maxScore, " +
           "MIN(r.score) as minScore, " +
           "COUNT(CASE WHEN r.score >= 60 THEN 1 END) as passCount " +
           "FROM ExamRecord r WHERE r.examId = :examId AND r.deleted = 0")
    Object[] getExamStatistics(@Param("examId") Long examId);

    /**
     * 统计指定学生的考试成绩统计
     */
    @Query("SELECT " +
           "COUNT(r) as totalExams, " +
           "AVG(r.score) as avgScore, " +
           "MAX(r.score) as maxScore, " +
           "MIN(r.score) as minScore, " +
           "COUNT(CASE WHEN r.score >= 60 THEN 1 END) as passCount " +
           "FROM ExamRecord r WHERE r.studentId = :studentId AND r.deleted = 0")
    Object[] getStudentExamStatistics(@Param("studentId") Long studentId);

    // ================================
    // 时间相关查询
    // ================================

    /**
     * 查找指定时间范围内的考试记录
     */
    @Query("SELECT r FROM ExamRecord r WHERE r.startTime BETWEEN :startTime AND :endTime AND r.deleted = 0 ORDER BY r.startTime DESC")
    List<ExamRecord> findByExamStartTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 查找超时的考试记录
     */
    @Query("SELECT r FROM ExamRecord r LEFT JOIN r.exam e " +
           "WHERE r.submitTime > FUNCTION('DATEADD', MINUTE, e.durationMinutes, r.startTime) AND r.deleted = 0 " +
           "ORDER BY r.startTime DESC")
    List<ExamRecord> findOvertimeRecords();

    /**
     * 查找正常完成的考试记录
     */
    @Query("SELECT r FROM ExamRecord r LEFT JOIN r.exam e " +
           "WHERE r.submitTime <= FUNCTION('DATEADD', MINUTE, e.durationMinutes, r.startTime) AND r.deleted = 0 " +
           "ORDER BY r.startTime DESC")
    List<ExamRecord> findNormalCompletedRecords();

    /**
     * 查找未完成的考试记录
     */
    @Query("SELECT r FROM ExamRecord r WHERE r.examStatus = 'in_progress' AND r.deleted = 0 ORDER BY r.startTime")
    List<ExamRecord> findIncompleteRecords();

    // ================================
    // 排名相关查询
    // ================================

    /**
     * 获取指定考试的成绩排名
     */
    @Query("SELECT r, ROW_NUMBER() OVER (ORDER BY r.score DESC) as ranking " +
           "FROM ExamRecord r WHERE r.examId = :examId AND r.deleted = 0 " +
           "ORDER BY r.score DESC")
    List<Object[]> getExamRanking(@Param("examId") Long examId);

    /**
     * 获取指定学生在指定考试中的排名
     */
    @Query("SELECT COUNT(r) + 1 FROM ExamRecord r " +
           "WHERE r.examId = :examId AND r.score > " +
           "(SELECT r2.score FROM ExamRecord r2 WHERE r2.examId = :examId AND r2.studentId = :studentId AND r2.deleted = 0) " +
           "AND r.deleted = 0")
    Integer getStudentRankingInExam(@Param("examId") Long examId, @Param("studentId") Long studentId);

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查学生是否已参加指定考试
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ExamRecord r WHERE r.examId = :examId AND r.studentId = :studentId AND r.deleted = 0")
    boolean existsByExamIdAndStudentId(@Param("examId") Long examId, @Param("studentId") Long studentId);

    /**
     * 检查学生是否有未完成的考试
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ExamRecord r WHERE r.studentId = :studentId AND r.examStatus = 'in_progress' AND r.deleted = 0")
    boolean hasIncompleteExam(@Param("studentId") Long studentId);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新考试分数
     */
    @Modifying
    @Query("UPDATE ExamRecord r SET r.score = :score, r.examStatus = 'completed', r.submitTime = CURRENT_TIMESTAMP, r.updatedAt = CURRENT_TIMESTAMP WHERE r.id = :recordId")
    int updateScore(@Param("recordId") Long recordId, @Param("score") Integer score);

    /**
     * 更新考试状态
     */
    @Modifying
    @Query("UPDATE ExamRecord r SET r.examStatus = :status, r.updatedAt = CURRENT_TIMESTAMP WHERE r.id = :recordId")
    int updateExamStatus(@Param("recordId") Long recordId, @Param("status") String examStatus);

    /**
     * 批量更新考试状态
     */
    @Modifying
    @Query("UPDATE ExamRecord r SET r.examStatus = :status, r.updatedAt = CURRENT_TIMESTAMP WHERE r.id IN :recordIds")
    int batchUpdateExamStatus(@Param("recordIds") List<Long> recordIds, @Param("status") String examStatus);

    /**
     * 更新考试结束时间
     */
    @Modifying
    @Query("UPDATE ExamRecord r SET r.submitTime = :endTime, r.updatedAt = CURRENT_TIMESTAMP WHERE r.id = :recordId")
    int updateExamEndTime(@Param("recordId") Long recordId, @Param("endTime") LocalDateTime examEndTime);

    /**
     * 更新答案内容
     */
    @Modifying
    @Query("UPDATE ExamRecord r SET r.answerDetails = :answerContent, r.updatedAt = CURRENT_TIMESTAMP WHERE r.id = :recordId")
    int updateAnswerContent(@Param("recordId") Long recordId, @Param("answerContent") String answerContent);

}
