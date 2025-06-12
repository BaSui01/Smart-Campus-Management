package com.campus.application.service.organization;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.campus.domain.entity.academic.StudentEvaluation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 学生评价服务接口
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
public interface StudentEvaluationService {
    
    // ================================
    // 基础CRUD操作
    // ================================
    
    /**
     * 创建学生评价
     */
    StudentEvaluation createEvaluation(StudentEvaluation evaluation);
    
    /**
     * 根据ID查找评价
     */
    Optional<StudentEvaluation> findEvaluationById(Long id);
    
    /**
     * 更新评价信息
     */
    StudentEvaluation updateEvaluation(StudentEvaluation evaluation);
    
    /**
     * 删除评价（软删除）
     */
    void deleteEvaluation(Long id);
    
    // ================================
    // 查询操作
    // ================================
    
    /**
     * 分页获取所有评价
     */
    Page<StudentEvaluation> findAllEvaluations(Pageable pageable);
    
    /**
     * 根据学生ID查找评价
     */
    List<StudentEvaluation> findEvaluationsByStudent(Long studentId);
    
    /**
     * 根据评价者ID查找评价
     */
    List<StudentEvaluation> findEvaluationsByEvaluator(Long evaluatorId);
    
    /**
     * 根据学生和评价类型查找评价
     */
    List<StudentEvaluation> findEvaluationsByStudentAndType(Long studentId, String evaluationType);
    
    /**
     * 根据日期范围查找评价
     */
    List<StudentEvaluation> findEvaluationsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 根据评分范围查找评价
     */
    List<StudentEvaluation> findEvaluationsByScoreRange(Double minScore, Double maxScore);
    
    /**
     * 根据课程查找评价
     */
    List<StudentEvaluation> findEvaluationsByCourse(Long courseId);
    
    // ================================
    // 业务操作
    // ================================
    
    /**
     * 检查是否已存在评价
     */
    boolean existsEvaluation(Long studentId, Long evaluatorId, String evaluationType);
    
    /**
     * 计算学生平均分
     */
    Double calculateAverageScore(Long studentId);
    
    /**
     * 计算学生在指定类型的平均分
     */
    Double calculateAverageScoreByType(Long studentId, String evaluationType);
    
    /**
     * 获取学生最新评价
     */
    List<StudentEvaluation> getLatestEvaluations(Long studentId, int limit);
    
    /**
     * 批量创建评价
     */
    List<StudentEvaluation> batchCreateEvaluations(List<StudentEvaluation> evaluations);
    
    // ================================
    // 统计操作
    // ================================
    
    /**
     * 统计学生评价数量
     */
    long countEvaluationsByStudent(Long studentId);
    
    /**
     * 统计评价者的评价数量
     */
    long countEvaluationsByEvaluator(Long evaluatorId);
    
    /**
     * 按评价类型统计数量
     */
    List<Object[]> countEvaluationsByType();
    
    /**
     * 获取评价统计信息
     */
    Object getEvaluationStatistics(Long studentId);
    
    // ================================
    // 搜索操作
    // ================================
    
    /**
     * 搜索评价
     */
    Page<StudentEvaluation> searchEvaluations(String keyword, Pageable pageable);
    
    /**
     * 根据评价内容搜索
     */
    List<StudentEvaluation> searchByContent(String content);
    
    // ================================
    // 管理操作
    // ================================
    
    /**
     * 启用评价
     */
    void enableEvaluation(Long id);
    
    /**
     * 禁用评价
     */
    void disableEvaluation(Long id);
    
    /**
     * 批量删除评价
     */
    void batchDeleteEvaluations(List<Long> evaluationIds);
    
    /**
     * 导出评价数据
     */
    List<StudentEvaluation> exportEvaluations(Long studentId);
    
    /**
     * 清理过期评价
     */
    void cleanupExpiredEvaluations(int daysToKeep);
}
