package com.campus.application.service.impl;

import com.campus.application.service.StudentEvaluationService;
import com.campus.domain.entity.StudentEvaluation;
import com.campus.domain.repository.StudentEvaluationRepository;
import com.campus.shared.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 学生评价服务实现类
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Service
@Transactional
public class StudentEvaluationServiceImpl implements StudentEvaluationService {
    
    private static final Logger logger = LoggerFactory.getLogger(StudentEvaluationServiceImpl.class);
    
    @Autowired
    private StudentEvaluationRepository studentEvaluationRepository;
    
    @Override
    public StudentEvaluation createEvaluation(StudentEvaluation evaluation) {
        logger.info("创建学生评价: studentId={}, evaluatorId={}", 
                   evaluation.getStudentId(), evaluation.getEvaluatorId());
        
        // 检查是否已存在相同的评价
        if (existsEvaluation(evaluation.getStudentId(), evaluation.getEvaluatorId(), evaluation.getEvaluationType())) {
            throw new BusinessException("该评价者已对此学生进行过此类型的评价");
        }
        
        // 验证评分范围
        if (evaluation.getScore() < 0 || evaluation.getScore() > 100) {
            throw new BusinessException("评分必须在0-100之间");
        }
        
        evaluation.setStatus(1);
        evaluation.setDeleted(0);
        return studentEvaluationRepository.save(evaluation);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<StudentEvaluation> findEvaluationById(Long id) {
        return studentEvaluationRepository.findByIdAndDeleted(id, 0);
    }
    
    @Override
    public StudentEvaluation updateEvaluation(StudentEvaluation evaluation) {
        logger.info("更新学生评价: {}", evaluation.getId());
        
        Optional<StudentEvaluation> existingOpt = findEvaluationById(evaluation.getId());
        if (existingOpt.isEmpty()) {
            throw new BusinessException("评价记录不存在");
        }
        
        StudentEvaluation existing = existingOpt.get();
        
        // 验证评分范围
        if (evaluation.getScore() < 0 || evaluation.getScore() > 100) {
            throw new BusinessException("评分必须在0-100之间");
        }
        
        existing.setScore(evaluation.getScore());
        existing.setComment(evaluation.getComment());
        existing.setEvaluationType(evaluation.getEvaluationType());
        
        return studentEvaluationRepository.save(existing);
    }
    
    @Override
    public void deleteEvaluation(Long id) {
        logger.info("删除学生评价: {}", id);
        
        Optional<StudentEvaluation> evaluationOpt = findEvaluationById(id);
        if (evaluationOpt.isEmpty()) {
            throw new BusinessException("评价记录不存在");
        }
        
        StudentEvaluation evaluation = evaluationOpt.get();
        evaluation.setDeleted(1);
        studentEvaluationRepository.save(evaluation);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<StudentEvaluation> findAllEvaluations(Pageable pageable) {
        return studentEvaluationRepository.findByDeletedOrderByCreatedAtDesc(0, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentEvaluation> findEvaluationsByStudent(Long studentId) {
        return studentEvaluationRepository.findByStudentIdAndDeletedOrderByCreatedAtDesc(studentId, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentEvaluation> findEvaluationsByEvaluator(Long evaluatorId) {
        return studentEvaluationRepository.findByEvaluatorIdAndDeletedOrderByCreatedAtDesc(evaluatorId, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentEvaluation> findEvaluationsByStudentAndType(Long studentId, String evaluationType) {
        return studentEvaluationRepository.findByStudentAndType(studentId, evaluationType);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentEvaluation> findEvaluationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return studentEvaluationRepository.findByCreatedAtBetweenAndDeletedOrderByCreatedAtDesc(startDate, endDate, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentEvaluation> findEvaluationsByScoreRange(Double minScore, Double maxScore) {
        return studentEvaluationRepository.findByScoreBetweenAndDeletedOrderByScoreDesc(minScore, maxScore, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentEvaluation> findEvaluationsByCourse(Long courseId) {
        return studentEvaluationRepository.findByCourseIdAndDeletedOrderByCreatedAtDesc(courseId, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsEvaluation(Long studentId, Long evaluatorId, String evaluationType) {
        return studentEvaluationRepository.existsByStudentIdAndEvaluatorIdAndEvaluationTypeAndDeleted(
            studentId, evaluatorId, evaluationType, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double calculateAverageScore(Long studentId) {
        return studentEvaluationRepository.calculateAverageScore(studentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double calculateAverageScoreByType(Long studentId, String evaluationType) {
        return studentEvaluationRepository.calculateAverageScoreByType(studentId, evaluationType);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentEvaluation> getLatestEvaluations(Long studentId, int limit) {
        return studentEvaluationRepository.findTopByStudentIdAndDeletedOrderByCreatedAtDesc(studentId, 0, limit);
    }
    
    @Override
    public List<StudentEvaluation> batchCreateEvaluations(List<StudentEvaluation> evaluations) {
        logger.info("批量创建学生评价: {} 个", evaluations.size());
        
        for (StudentEvaluation evaluation : evaluations) {
            // 验证每个评价
            if (existsEvaluation(evaluation.getStudentId(), evaluation.getEvaluatorId(), evaluation.getEvaluationType())) {
                throw new BusinessException("存在重复的评价记录");
            }
            
            if (evaluation.getScore() < 0 || evaluation.getScore() > 100) {
                throw new BusinessException("评分必须在0-100之间");
            }
            
            evaluation.setStatus(1);
            evaluation.setDeleted(0);
        }
        
        return studentEvaluationRepository.saveAll(evaluations);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countEvaluationsByStudent(Long studentId) {
        return studentEvaluationRepository.countByStudentIdAndDeleted(studentId, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countEvaluationsByEvaluator(Long evaluatorId) {
        return studentEvaluationRepository.countByEvaluatorIdAndDeleted(evaluatorId, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> countEvaluationsByType() {
        return studentEvaluationRepository.countByEvaluationType();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Object getEvaluationStatistics(Long studentId) {
        Double averageScore = calculateAverageScore(studentId);
        long totalEvaluations = countEvaluationsByStudent(studentId);
        List<Object[]> typeStats = studentEvaluationRepository.countByEvaluationTypeForStudent(studentId);
        
        return new Object() {
            public final Double average = averageScore;
            public final long total = totalEvaluations;
            public final List<Object[]> byType = typeStats;
        };
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<StudentEvaluation> searchEvaluations(String keyword, Pageable pageable) {
        return studentEvaluationRepository.findByCommentContainingIgnoreCaseAndDeletedOrderByCreatedAtDesc(keyword, 0, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentEvaluation> searchByContent(String content) {
        return studentEvaluationRepository.findByCommentContainingIgnoreCaseAndDeleted(content, 0);
    }
    
    @Override
    public void enableEvaluation(Long id) {
        logger.info("启用学生评价: {}", id);
        
        Optional<StudentEvaluation> evaluationOpt = findEvaluationById(id);
        if (evaluationOpt.isEmpty()) {
            throw new BusinessException("评价记录不存在");
        }
        
        StudentEvaluation evaluation = evaluationOpt.get();
        evaluation.setStatus(1);
        studentEvaluationRepository.save(evaluation);
    }
    
    @Override
    public void disableEvaluation(Long id) {
        logger.info("禁用学生评价: {}", id);
        
        Optional<StudentEvaluation> evaluationOpt = findEvaluationById(id);
        if (evaluationOpt.isEmpty()) {
            throw new BusinessException("评价记录不存在");
        }
        
        StudentEvaluation evaluation = evaluationOpt.get();
        evaluation.setStatus(0);
        studentEvaluationRepository.save(evaluation);
    }
    
    @Override
    public void batchDeleteEvaluations(List<Long> evaluationIds) {
        logger.info("批量删除学生评价: {} 个", evaluationIds.size());
        
        for (Long evaluationId : evaluationIds) {
            try {
                deleteEvaluation(evaluationId);
            } catch (Exception e) {
                logger.error("删除学生评价失败: evaluationId={}", evaluationId, e);
            }
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<StudentEvaluation> exportEvaluations(Long studentId) {
        if (studentId != null) {
            return findEvaluationsByStudent(studentId);
        } else {
            return studentEvaluationRepository.findByDeletedOrderByCreatedAtDesc(0);
        }
    }
    
    @Override
    public void cleanupExpiredEvaluations(int daysToKeep) {
        logger.info("清理过期学生评价，保留天数: {}", daysToKeep);
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        List<StudentEvaluation> expiredEvaluations = studentEvaluationRepository.findByCreatedAtBeforeAndDeleted(cutoffDate, 0);
        
        for (StudentEvaluation evaluation : expiredEvaluations) {
            evaluation.setDeleted(1);
            studentEvaluationRepository.save(evaluation);
        }
        
        logger.info("清理过期学生评价完成，共处理 {} 条记录", expiredEvaluations.size());
    }
}
