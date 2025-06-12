package com.campus.application.service.exam;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.campus.domain.entity.exam.Exam;
import com.campus.domain.entity.exam.ExamQuestion;
import com.campus.domain.entity.exam.ExamRecord;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 考试管理服务接口
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
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

    // ================================
    // ExamQuestionApiController需要的方法
    // ================================

    /**
     * 创建考试题目
     *
     * @param question 题目信息
     * @return 创建的题目
     */
    ExamQuestion createExamQuestion(ExamQuestion question);

    /**
     * 根据ID获取考试题目
     *
     * @param questionId 题目ID
     * @return 题目信息
     */
    ExamQuestion getExamQuestionById(Long questionId);

    /**
     * 更新考试题目
     *
     * @param question 题目信息
     * @return 更新的题目
     */
    ExamQuestion updateExamQuestion(ExamQuestion question);

    /**
     * 删除考试题目
     *
     * @param questionId 题目ID
     * @return 是否删除成功
     */
    boolean deleteExamQuestion(Long questionId);

    /**
     * 分页查询考试题目
     *
     * @param pageable 分页参数
     * @param examId 考试ID
     * @param questionType 题目类型
     * @param difficulty 难度级别
     * @return 分页结果
     */
    Page<ExamQuestion> findExamQuestions(Pageable pageable, Long examId, String questionType, String difficulty);

    /**
     * 根据考试ID获取题目列表
     *
     * @param examId 考试ID
     * @param randomOrder 是否随机排序
     * @return 题目列表
     */
    List<ExamQuestion> getQuestionsByExam(Long examId, boolean randomOrder);

    /**
     * 根据类型获取题目列表
     *
     * @param questionType 题目类型
     * @param examId 考试ID
     * @return 题目列表
     */
    List<ExamQuestion> getQuestionsByType(String questionType, Long examId);

    /**
     * 批量导入考试题目
     *
     * @param questions 题目列表
     * @return 导入结果
     */
    Map<String, Object> importExamQuestions(List<ExamQuestion> questions);

    /**
     * 导出考试题目
     *
     * @param examId 考试ID
     * @param questionType 题目类型
     * @return 题目列表
     */
    List<ExamQuestion> exportExamQuestions(Long examId, String questionType);

    /**
     * 复制题目到其他考试
     *
     * @param questionIds 题目ID列表
     * @param targetExamId 目标考试ID
     * @return 复制的题目列表
     */
    List<ExamQuestion> duplicateQuestions(List<Long> questionIds, Long targetExamId);

    /**
     * 获取题目统计信息
     *
     * @param examId 考试ID
     * @return 统计信息
     */
    Map<String, Object> getQuestionStatistics(Long examId);

    /**
     * 获取题目难度分析
     *
     * @param examId 考试ID
     * @return 难度分析结果
     */
    Map<String, Object> getQuestionDifficultyAnalysis(Long examId);

    /**
     * 批量更新题目
     *
     * @param updateData 更新数据
     * @return 更新结果
     */
    Map<String, Object> batchUpdateQuestions(Map<String, Object> updateData);

    /**
     * 批量删除题目
     *
     * @param questionIds 题目ID列表
     * @return 删除是否成功
     */
    boolean batchDeleteQuestions(List<Long> questionIds);

    /**
     * 验证题目格式和内容
     *
     * @param questions 题目列表
     * @return 验证结果
     */
    Map<String, Object> validateQuestions(List<ExamQuestion> questions);

    // ================================
    // Web控制器需要的方法
    // ================================

    /**
     * 搜索考试
     *
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 考试分页结果
     */
    Page<Exam> searchExams(String keyword, Pageable pageable);

    /**
     * 根据课程查找考试
     *
     * @param courseId 课程ID
     * @param pageable 分页参数
     * @return 考试分页结果
     */
    Page<Exam> findExamsByCourse(Long courseId, Pageable pageable);

    /**
     * 根据考试类型查找考试
     *
     * @param examType 考试类型
     * @param pageable 分页参数
     * @return 考试分页结果
     */
    Page<Exam> findExamsByType(String examType, Pageable pageable);

    /**
     * 根据考试状态查找考试
     *
     * @param status 考试状态
     * @param pageable 分页参数
     * @return 考试分页结果
     */
    Page<Exam> findExamsByStatus(String status, Pageable pageable);

    /**
     * 分页查找所有考试
     *
     * @param pageable 分页参数
     * @return 考试分页结果
     */
    Page<Exam> findAllExams(Pageable pageable);

    /**
     * 统计考试总数
     *
     * @return 考试总数
     */
    long countTotalExams();

    /**
     * 统计即将到来的考试数量
     *
     * @return 即将到来的考试数量
     */
    long countUpcomingExams();

    /**
     * 获取所有考试类型
     *
     * @return 考试类型列表
     */
    List<String> findAllExamTypes();

    /**
     * 获取所有考试状态
     *
     * @return 考试状态列表
     */
    List<String> findAllExamStatuses();

    /**
     * 根据ID查找考试
     *
     * @param id 考试ID
     * @return 考试信息
     */
    Optional<Exam> findExamById(Long id);

    /**
     * 统计考试提交数量
     *
     * @param examId 考试ID
     * @return 提交数量
     */
    long countExamSubmissions(Long examId);

    /**
     * 创建考试
     *
     * @param exam 考试信息
     * @return 创建的考试
     */
    Exam createExam(Exam exam);

    /**
     * 更新考试
     *
     * @param exam 考试信息
     * @return 更新的考试
     */
    Exam updateExam(Exam exam);

    /**
     * 删除考试
     *
     * @param examId 考试ID
     * @return 删除结果
     */
    boolean deleteExam(Long examId);

    /**
     * 取消考试
     *
     * @param examId 考试ID
     * @return 取消结果
     */
    boolean cancelExam(Long examId);

    /**
     * 统计已完成的考试数量
     *
     * @return 已完成的考试数量
     */
    long countCompletedExams();

    /**
     * 按类型统计考试数量
     *
     * @return 按类型统计结果
     */
    List<Object[]> countExamsByType();

    /**
     * 获取月度考试统计
     *
     * @return 月度统计数据
     */
    List<Object[]> getMonthlyExamStatistics();

    /**
     * 根据月份查找考试
     *
     * @param month 月份
     * @return 考试列表
     */
    List<Exam> findExamsByMonth(LocalDate month);
}
