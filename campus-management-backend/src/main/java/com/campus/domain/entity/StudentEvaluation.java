package com.campus.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生综合评价实体类
 * 管理学生的综合素质评价，包括品德、学习、体育、艺术等方面
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "student_evaluations", indexes = {
    @Index(name = "idx_student_id", columnList = "student_id"),
    @Index(name = "idx_evaluator_id", columnList = "evaluator_id"),
    @Index(name = "idx_evaluation_type", columnList = "evaluation_type"),
    @Index(name = "idx_evaluation_date", columnList = "evaluation_date")
})
public class StudentEvaluation extends BaseEntity {

    /**
     * 学生ID
     */
    @NotNull(message = "学生ID不能为空")
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    /**
     * 评价人ID
     */
    @NotNull(message = "评价人ID不能为空")
    @Column(name = "evaluator_id", nullable = false)
    private Long evaluatorId;

    /**
     * 评价类型 (comprehensive/moral/academic/physical/artistic/social)
     */
    @NotNull(message = "评价类型不能为空")
    @Size(max = 20, message = "评价类型长度不能超过20个字符")
    @Column(name = "evaluation_type", nullable = false, length = 20)
    private String evaluationType;

    /**
     * 评价周期 (daily/weekly/monthly/semester/annual)
     */
    @Size(max = 20, message = "评价周期长度不能超过20个字符")
    @Column(name = "evaluation_period", length = 20)
    private String evaluationPeriod;

    /**
     * 评价日期
     */
    @NotNull(message = "评价日期不能为空")
    @Column(name = "evaluation_date", nullable = false)
    private LocalDate evaluationDate;

    /**
     * 学期
     */
    @Size(max = 20, message = "学期长度不能超过20个字符")
    @Column(name = "semester", length = 20)
    private String semester;

    /**
     * 学年
     */
    @Column(name = "academic_year")
    private Integer academicYear;

    /**
     * 品德表现评分
     */
    @Column(name = "moral_score", precision = 3, scale = 1)
    private BigDecimal moralScore;

    /**
     * 学习表现评分
     */
    @Column(name = "academic_score", precision = 3, scale = 1)
    private BigDecimal academicScore;

    /**
     * 体育表现评分
     */
    @Column(name = "physical_score", precision = 3, scale = 1)
    private BigDecimal physicalScore;

    /**
     * 艺术表现评分
     */
    @Column(name = "artistic_score", precision = 3, scale = 1)
    private BigDecimal artisticScore;

    /**
     * 社会实践评分
     */
    @Column(name = "social_score", precision = 3, scale = 1)
    private BigDecimal socialScore;

    /**
     * 综合评分
     */
    @Column(name = "overall_score", precision = 3, scale = 1)
    private BigDecimal overallScore;

    /**
     * 评价等级 (excellent/good/average/poor)
     */
    @Size(max = 20, message = "评价等级长度不能超过20个字符")
    @Column(name = "evaluation_grade", length = 20)
    private String evaluationGrade;

    /**
     * 优点描述
     */
    @Size(max = 1000, message = "优点描述长度不能超过1000个字符")
    @Column(name = "strengths", length = 1000)
    private String strengths;

    /**
     * 需要改进的方面
     */
    @Size(max = 1000, message = "改进建议长度不能超过1000个字符")
    @Column(name = "improvements", length = 1000)
    private String improvements;

    /**
     * 具体表现记录
     */
    @Size(max = 2000, message = "表现记录长度不能超过2000个字符")
    @Column(name = "performance_records", length = 2000)
    private String performanceRecords;

    /**
     * 奖惩记录
     */
    @Size(max = 1000, message = "奖惩记录长度不能超过1000个字符")
    @Column(name = "rewards_punishments", length = 1000)
    private String rewardsPunishments;

    /**
     * 家长反馈
     */
    @Size(max = 1000, message = "家长反馈长度不能超过1000个字符")
    @Column(name = "parent_feedback", length = 1000)
    private String parentFeedback;

    /**
     * 学生自评
     */
    @Size(max = 1000, message = "学生自评长度不能超过1000个字符")
    @Column(name = "self_evaluation", length = 1000)
    private String selfEvaluation;

    /**
     * 发展建议
     */
    @Size(max = 1000, message = "发展建议长度不能超过1000个字符")
    @Column(name = "development_suggestions", length = 1000)
    private String developmentSuggestions;

    /**
     * 是否已确认
     */
    @Column(name = "is_confirmed", nullable = false)
    private Boolean isConfirmed = false;

    /**
     * 确认时间
     */
    @Column(name = "confirmed_time")
    private LocalDateTime confirmedTime;

    /**
     * 是否已通知家长
     */
    @Column(name = "parent_notified", nullable = false)
    private Boolean parentNotified = false;

    /**
     * 通知时间
     */
    @Column(name = "notification_time")
    private LocalDateTime notificationTime;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Column(name = "remarks", length = 500)
    private String remarks;

    // ================================
    // 关联关系
    // ================================

    /**
     * 被评价学生
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    @JsonIgnore
    private Student student;

    /**
     * 评价人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluator_id", insertable = false, updatable = false)
    @JsonIgnore
    private User evaluator;

    // ================================
    // 构造函数
    // ================================

    public StudentEvaluation() {
        super();
    }

    public StudentEvaluation(Long studentId, Long evaluatorId, String evaluationType, LocalDate evaluationDate) {
        this();
        this.studentId = studentId;
        this.evaluatorId = evaluatorId;
        this.evaluationType = evaluationType;
        this.evaluationDate = evaluationDate;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 获取评价类型文本
     */
    public String getEvaluationTypeText() {
        if (evaluationType == null) return "未知";
        return switch (evaluationType) {
            case "comprehensive" -> "综合评价";
            case "moral" -> "品德评价";
            case "academic" -> "学习评价";
            case "physical" -> "体育评价";
            case "artistic" -> "艺术评价";
            case "social" -> "社会实践评价";
            default -> evaluationType;
        };
    }

    /**
     * 获取评价周期文本
     */
    public String getEvaluationPeriodText() {
        if (evaluationPeriod == null) return "未知";
        return switch (evaluationPeriod) {
            case "daily" -> "日常评价";
            case "weekly" -> "周评价";
            case "monthly" -> "月评价";
            case "semester" -> "学期评价";
            case "annual" -> "年度评价";
            default -> evaluationPeriod;
        };
    }

    /**
     * 获取评价等级文本
     */
    public String getEvaluationGradeText() {
        if (evaluationGrade == null) return "未评级";
        return switch (evaluationGrade) {
            case "excellent" -> "优秀";
            case "good" -> "良好";
            case "average" -> "一般";
            case "poor" -> "待改进";
            default -> evaluationGrade;
        };
    }

    /**
     * 计算综合评分
     */
    public void calculateOverallScore() {
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        
        if (moralScore != null) {
            total = total.add(moralScore);
            count++;
        }
        if (academicScore != null) {
            total = total.add(academicScore);
            count++;
        }
        if (physicalScore != null) {
            total = total.add(physicalScore);
            count++;
        }
        if (artisticScore != null) {
            total = total.add(artisticScore);
            count++;
        }
        if (socialScore != null) {
            total = total.add(socialScore);
            count++;
        }
        
        if (count > 0) {
            this.overallScore = total.divide(new BigDecimal(count), 1, java.math.RoundingMode.HALF_UP);
            this.evaluationGrade = determineGrade(this.overallScore);
        }
    }

    /**
     * 根据分数确定等级
     */
    private String determineGrade(BigDecimal score) {
        if (score == null) return "average";
        
        if (score.compareTo(new BigDecimal("90")) >= 0) {
            return "excellent";
        } else if (score.compareTo(new BigDecimal("80")) >= 0) {
            return "good";
        } else if (score.compareTo(new BigDecimal("60")) >= 0) {
            return "average";
        } else {
            return "poor";
        }
    }

    /**
     * 确认评价
     */
    public void confirm() {
        this.isConfirmed = true;
        this.confirmedTime = LocalDateTime.now();
        calculateOverallScore();
    }

    /**
     * 通知家长
     */
    public void notifyParent() {
        this.parentNotified = true;
        this.notificationTime = LocalDateTime.now();
    }

    /**
     * 检查是否为优秀评价
     */
    public boolean isExcellent() {
        return "excellent".equals(evaluationGrade);
    }

    /**
     * 检查是否需要关注
     */
    public boolean needsAttention() {
        return "poor".equals(evaluationGrade) || 
               (overallScore != null && overallScore.compareTo(new BigDecimal("60")) < 0);
    }

    /**
     * 获取学生姓名
     */
    public String getStudentName() {
        return student != null ? student.getRealName() : null;
    }

    /**
     * 获取学生学号
     */
    public String getStudentNumber() {
        return student != null ? student.getStudentNumber() : null;
    }

    /**
     * 获取评价人姓名
     */
    public String getEvaluatorName() {
        return evaluator != null ? evaluator.getRealName() : null;
    }

    /**
     * 获取学生班级
     */
    public String getStudentClass() {
        return student != null ? student.getClassName() : null;
    }

    /**
     * 验证评价数据
     */
    @PrePersist
    @PreUpdate
    public void validateEvaluation() {
        // 验证分数范围
        validateScore(moralScore, "品德评分");
        validateScore(academicScore, "学习评分");
        validateScore(physicalScore, "体育评分");
        validateScore(artisticScore, "艺术评分");
        validateScore(socialScore, "社会实践评分");
        validateScore(overallScore, "综合评分");
    }

    private void validateScore(BigDecimal score, String scoreName) {
        if (score != null) {
            if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(new BigDecimal("100")) > 0) {
                throw new IllegalArgumentException(scoreName + "必须在0-100之间");
            }
        }
    }

    // ================================
    // Getter and Setter methods
    // ================================

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getEvaluatorId() {
        return evaluatorId;
    }

    public void setEvaluatorId(Long evaluatorId) {
        this.evaluatorId = evaluatorId;
    }

    public String getEvaluationType() {
        return evaluationType;
    }

    public void setEvaluationType(String evaluationType) {
        this.evaluationType = evaluationType;
    }

    public String getEvaluationPeriod() {
        return evaluationPeriod;
    }

    public void setEvaluationPeriod(String evaluationPeriod) {
        this.evaluationPeriod = evaluationPeriod;
    }

    public LocalDate getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(LocalDate evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Integer getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(Integer academicYear) {
        this.academicYear = academicYear;
    }

    public BigDecimal getMoralScore() {
        return moralScore;
    }

    public void setMoralScore(BigDecimal moralScore) {
        this.moralScore = moralScore;
    }

    public BigDecimal getAcademicScore() {
        return academicScore;
    }

    public void setAcademicScore(BigDecimal academicScore) {
        this.academicScore = academicScore;
    }

    public BigDecimal getPhysicalScore() {
        return physicalScore;
    }

    public void setPhysicalScore(BigDecimal physicalScore) {
        this.physicalScore = physicalScore;
    }

    public BigDecimal getArtisticScore() {
        return artisticScore;
    }

    public void setArtisticScore(BigDecimal artisticScore) {
        this.artisticScore = artisticScore;
    }

    public BigDecimal getSocialScore() {
        return socialScore;
    }

    public void setSocialScore(BigDecimal socialScore) {
        this.socialScore = socialScore;
    }

    public BigDecimal getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(BigDecimal overallScore) {
        this.overallScore = overallScore;
    }

    public String getEvaluationGrade() {
        return evaluationGrade;
    }

    public void setEvaluationGrade(String evaluationGrade) {
        this.evaluationGrade = evaluationGrade;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getImprovements() {
        return improvements;
    }

    public void setImprovements(String improvements) {
        this.improvements = improvements;
    }

    public String getPerformanceRecords() {
        return performanceRecords;
    }

    public void setPerformanceRecords(String performanceRecords) {
        this.performanceRecords = performanceRecords;
    }

    public String getRewardsPunishments() {
        return rewardsPunishments;
    }

    public void setRewardsPunishments(String rewardsPunishments) {
        this.rewardsPunishments = rewardsPunishments;
    }

    public String getParentFeedback() {
        return parentFeedback;
    }

    public void setParentFeedback(String parentFeedback) {
        this.parentFeedback = parentFeedback;
    }

    public String getSelfEvaluation() {
        return selfEvaluation;
    }

    public void setSelfEvaluation(String selfEvaluation) {
        this.selfEvaluation = selfEvaluation;
    }

    public String getDevelopmentSuggestions() {
        return developmentSuggestions;
    }

    public void setDevelopmentSuggestions(String developmentSuggestions) {
        this.developmentSuggestions = developmentSuggestions;
    }

    public Boolean getIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(Boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public LocalDateTime getConfirmedTime() {
        return confirmedTime;
    }

    public void setConfirmedTime(LocalDateTime confirmedTime) {
        this.confirmedTime = confirmedTime;
    }

    public Boolean getParentNotified() {
        return parentNotified;
    }

    public void setParentNotified(Boolean parentNotified) {
        this.parentNotified = parentNotified;
    }

    public LocalDateTime getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(LocalDateTime notificationTime) {
        this.notificationTime = notificationTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public User getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(User evaluator) {
        this.evaluator = evaluator;
    }
}
