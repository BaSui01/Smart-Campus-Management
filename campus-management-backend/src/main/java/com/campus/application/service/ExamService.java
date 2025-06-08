package com.campus.application.service;

import com.campus.domain.entity.Exam;
import com.campus.domain.entity.ExamQuestion;
import com.campus.domain.entity.ExamRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 考试管理服务接口
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2024-12-07
 */
public interface ExamService {

    // ==================== 基础CRUD方法 ====================

    /**
     * 保存考试
     */
    Exam save(Exam exam);

    /**
     * 根据ID查找考试
     */
    Optional<Exam> findById(Long id);

    /**
     * 获取所有考试
     */
    List<Exam> findAll();

    /**
     * 分页获取考试
     */
    Page<Exam> findAll(Pageable pageable);

    /**
     * 删除考试
     */
    void deleteById(Long id);

    /**
     * 批量删除考试
     */
    void deleteByIds(List<Long> ids);

    /**
     * 统计考试数量
     */
    long count();

    // ==================== 业务查询方法 ====================

    /**
     * 根据课程ID查找考试
     */
    List<Exam> findByCourseId(Long courseId);

    /**
     * 根据教师ID查找考试
     */
    List<Exam> findByTeacherId(Long teacherId);

    /**
     * 根据考试类型查找考试
     */
    List<Exam> findByExamType(String examType);

    /**
     * 查找已发布的考试
     */
    List<Exam> findPublishedExams();

    /**
     * 查找未发布的考试
     */
    List<Exam> findUnpublishedExams();

    /**
     * 根据时间范围查找考试
     */
    List<Exam> findByTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查找即将开始的考试
     */
    List<Exam> findExamsStartingSoon(int hours);

    /**
     * 查找正在进行的考试
     */
    List<Exam> findOngoingExams();

    /**
     * 查找已结束的考试
     */
    List<Exam> findFinishedExams();

    /**
     * 根据教室ID查找考试
     */
    List<Exam> findByClassroomId(Long classroomId);

    /**
     * 查找在线考试
     */
    List<Exam> findOnlineExams();

    /**
     * 查找线下考试
     */
    List<Exam> findOfflineExams();

    // ==================== 分页查询方法 ====================

    /**
     * 根据课程ID分页查找考试
     */
    Page<Exam> findByCourseId(Long courseId, Pageable pageable);

    /**
     * 根据教师ID分页查找考试
     */
    Page<Exam> findByTeacherId(Long teacherId, Pageable pageable);

    /**
     * 根据条件分页查询考试
     */
    Page<Exam> findByConditions(String examName, Long courseId, Long teacherId, 
                               String examType, Boolean isPublished, Boolean isOnline,
                               Pageable pageable);

    // ==================== 业务操作方法 ====================

    /**
     * 发布考试
     */
    void publishExam(Long examId);

    /**
     * 取消发布考试
     */
    void unpublishExam(Long examId);

    /**
     * 批量发布考试
     */
    void publishExams(List<Long> examIds);

    /**
     * 开始考试
     */
    void startExam(Long examId);

    /**
     * 结束考试
     */
    void endExam(Long examId);

    /**
     * 延长考试时间
     */
    void extendExamTime(Long examId, int additionalMinutes);

    /**
     * 复制考试
     */
    Exam copyExam(Long examId, Long newCourseId);

    // ==================== 考试题目管理 ====================

    /**
     * 添加考试题目
     */
    ExamQuestion addQuestion(Long examId, ExamQuestion question);

    /**
     * 更新考试题目
     */
    ExamQuestion updateQuestion(Long questionId, ExamQuestion question);

    /**
     * 删除考试题目
     */
    void deleteQuestion(Long questionId);

    /**
     * 获取考试的所有题目
     */
    List<ExamQuestion> getExamQuestions(Long examId);

    /**
     * 获取考试的分页题目
     */
    Page<ExamQuestion> getExamQuestions(Long examId, Pageable pageable);

    /**
     * 批量导入题目
     */
    void importQuestions(Long examId, List<ExamQuestion> questions);

    /**
     * 随机生成题目
     */
    void generateRandomQuestions(Long examId, int questionCount);

    /**
     * 调整题目顺序
     */
    void reorderQuestions(Long examId, List<Long> questionIds);

    // ==================== 考试记录管理 ====================

    /**
     * 获取考试的所有记录
     */
    List<ExamRecord> getExamRecords(Long examId);

    /**
     * 获取考试的分页记录
     */
    Page<ExamRecord> getExamRecords(Long examId, Pageable pageable);

    /**
     * 获取学生的考试记录
     */
    Optional<ExamRecord> getStudentExamRecord(Long examId, Long studentId);

    /**
     * 开始学生考试
     */
    ExamRecord startStudentExam(Long examId, Long studentId);

    /**
     * 提交学生考试
     */
    void submitStudentExam(Long recordId, String answers);

    /**
     * 自动提交超时考试
     */
    void autoSubmitTimeoutExams();

    // ==================== 统计分析方法 ====================

    /**
     * 统计课程考试数量
     */
    long countByCourseId(Long courseId);

    /**
     * 统计教师考试数量
     */
    long countByTeacherId(Long teacherId);

    /**
     * 统计已发布考试数量
     */
    long countPublishedExams();

    /**
     * 统计未发布考试数量
     */
    long countUnpublishedExams();

    /**
     * 统计正在进行考试数量
     */
    long countOngoingExams();

    /**
     * 统计考试参与人数
     */
    long countExamParticipants(Long examId);

    /**
     * 统计考试完成人数
     */
    long countExamCompletions(Long examId);

    /**
     * 计算考试平均分
     */
    Double calculateExamAverageScore(Long examId);

    /**
     * 获取考试成绩分布
     */
    List<Object[]> getExamScoreDistribution(Long examId);

    /**
     * 获取考试统计信息
     */
    List<Object[]> getExamStatistics(Long examId);

    /**
     * 获取课程考试统计
     */
    List<Object[]> getCourseExamStatistics(Long courseId);

    /**
     * 获取教师考试统计
     */
    List<Object[]> getTeacherExamStatistics(Long teacherId);

    // ==================== 验证方法 ====================

    /**
     * 检查考试名称是否存在
     */
    boolean existsByExamName(String examName);

    /**
     * 检查考试名称是否存在（排除指定ID）
     */
    boolean existsByExamNameExcludeId(String examName, Long excludeId);

    /**
     * 检查是否可以删除考试
     */
    boolean canDeleteExam(Long examId);

    /**
     * 检查是否可以修改考试
     */
    boolean canModifyExam(Long examId);

    /**
     * 检查考试时间冲突
     */
    boolean hasTimeConflict(Long classroomId, LocalDateTime startTime, LocalDateTime endTime, Long excludeExamId);

    /**
     * 检查学生是否可以参加考试
     */
    boolean canStudentTakeExam(Long examId, Long studentId);

    // ==================== 状态管理方法 ====================

    /**
     * 启用考试
     */
    void enableExam(Long id);

    /**
     * 禁用考试
     */
    void disableExam(Long id);

    /**
     * 批量启用考试
     */
    void enableExams(List<Long> ids);

    /**
     * 批量禁用考试
     */
    void disableExams(List<Long> ids);

    // ==================== 防作弊管理 ====================

    /**
     * 记录作弊警告
     */
    void recordCheatWarning(Long recordId, String warningType, String description);

    /**
     * 获取作弊记录
     */
    List<Object[]> getCheatRecords(Long examId);

    /**
     * 标记可疑行为
     */
    void markSuspiciousBehavior(Long recordId, String behaviorType, String details);
}
