package com.campus.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 考试记录实体类
 * 管理学生的考试记录，包括考试成绩、答题情况等
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "exam_records")
public class ExamRecord extends BaseEntity {

    /**
     * 考试ID
     */
    @NotNull(message = "考试ID不能为空")
    @Column(name = "exam_id", nullable = false)
    private Long examId;

    /**
     * 学生ID
     */
    @NotNull(message = "学生ID不能为空")
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    /**
     * 开始时间
     */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /**
     * 提交时间
     */
    @Column(name = "submit_time")
    private LocalDateTime submitTime;

    /**
     * 用时（分钟）
     */
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    /**
     * 得分
     */
    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;

    /**
     * 总分
     */
    @Column(name = "total_score", precision = 5, scale = 2)
    private BigDecimal totalScore;

    /**
     * 是否及格
     */
    @Column(name = "is_passed")
    private Boolean isPassed;

    /**
     * 考试状态 (not_started/in_progress/submitted/graded/cancelled)
     */
    @Size(max = 20, message = "考试状态长度不能超过20个字符")
    @Column(name = "exam_status", length = 20)
    private String examStatus = "not_started";

    /**
     * 答题详情（JSON格式）
     */
    @Column(name = "answer_details", columnDefinition = "TEXT")
    private String answerDetails;

    /**
     * 切屏次数
     */
    @Column(name = "switch_count")
    private Integer switchCount = 0;

    /**
     * 异常行为记录
     */
    @Column(name = "abnormal_behaviors", columnDefinition = "TEXT")
    private String abnormalBehaviors;

    /**
     * IP地址
     */
    @Size(max = 50, message = "IP地址长度不能超过50个字符")
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    /**
     * 浏览器信息
     */
    @Size(max = 200, message = "浏览器信息长度不能超过200个字符")
    @Column(name = "browser_info", length = 200)
    private String browserInfo;

    /**
     * 是否作弊
     */
    @Column(name = "is_cheating")
    private Boolean isCheating = false;

    /**
     * 作弊原因
     */
    @Size(max = 500, message = "作弊原因长度不能超过500个字符")
    @Column(name = "cheating_reason", length = 500)
    private String cheatingReason;

    /**
     * 教师评语
     */
    @Size(max = 1000, message = "教师评语长度不能超过1000个字符")
    @Column(name = "teacher_comment", length = 1000)
    private String teacherComment;

    /**
     * 阅卷教师ID
     */
    @Column(name = "grader_id")
    private Long graderId;

    /**
     * 阅卷时间
     */
    @Column(name = "graded_time")
    private LocalDateTime gradedTime;

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
     * 所属考试
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", insertable = false, updatable = false)
    @JsonIgnore
    private Exam exam;

    /**
     * 考试学生
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    @JsonIgnore
    private Student student;

    /**
     * 阅卷教师
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grader_id", insertable = false, updatable = false)
    @JsonIgnore
    private User grader;

    // ================================
    // 构造函数
    // ================================

    public ExamRecord() {
        super();
    }

    public ExamRecord(Long examId, Long studentId) {
        this();
        this.examId = examId;
        this.studentId = studentId;
        this.examStatus = "not_started";
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 开始考试
     */
    public void startExam() {
        this.startTime = LocalDateTime.now();
        this.examStatus = "in_progress";
    }

    /**
     * 提交考试
     */
    public void submitExam() {
        this.submitTime = LocalDateTime.now();
        this.examStatus = "submitted";
        
        // 计算用时
        if (startTime != null) {
            this.durationMinutes = (int) java.time.Duration.between(startTime, submitTime).toMinutes();
        }
    }

    /**
     * 完成阅卷
     */
    public void completeGrading(BigDecimal score, BigDecimal totalScore, Long graderId) {
        this.score = score;
        this.totalScore = totalScore;
        this.graderId = graderId;
        this.gradedTime = LocalDateTime.now();
        this.examStatus = "graded";
        
        // 判断是否及格
        if (score != null && totalScore != null && totalScore.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal passRate = score.divide(totalScore, 4, java.math.RoundingMode.HALF_UP);
            this.isPassed = passRate.compareTo(new BigDecimal("0.6")) >= 0; // 60%及格
        }
    }

    /**
     * 取消考试
     */
    public void cancelExam() {
        this.examStatus = "cancelled";
    }

    /**
     * 获取考试状态文本
     */
    public String getExamStatusText() {
        if (examStatus == null) return "未知";
        return switch (examStatus) {
            case "not_started" -> "未开始";
            case "in_progress" -> "进行中";
            case "submitted" -> "已提交";
            case "graded" -> "已阅卷";
            case "cancelled" -> "已取消";
            default -> examStatus;
        };
    }

    /**
     * 获取得分率
     */
    public BigDecimal getScoreRate() {
        if (score == null || totalScore == null || totalScore.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return score.divide(totalScore, 4, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }

    /**
     * 检查是否超时
     */
    public boolean isTimeout() {
        if (exam == null || startTime == null) {
            return false;
        }
        
        LocalDateTime expectedEndTime = startTime.plusMinutes(exam.getDurationMinutes());
        return LocalDateTime.now().isAfter(expectedEndTime);
    }

    /**
     * 检查是否可以开始
     */
    public boolean canStart() {
        return "not_started".equals(examStatus);
    }

    /**
     * 检查是否可以提交
     */
    public boolean canSubmit() {
        return "in_progress".equals(examStatus);
    }

    /**
     * 检查是否已完成
     */
    public boolean isCompleted() {
        return "submitted".equals(examStatus) || "graded".equals(examStatus);
    }

    /**
     * 记录异常行为
     */
    public void recordAbnormalBehavior(String behavior) {
        if (abnormalBehaviors == null) {
            abnormalBehaviors = "";
        }
        String timestamp = LocalDateTime.now().toString();
        abnormalBehaviors += "[" + timestamp + "] " + behavior + "\n";
    }

    /**
     * 增加切屏次数
     */
    public void incrementSwitchCount() {
        if (switchCount == null) {
            switchCount = 0;
        }
        switchCount++;
        recordAbnormalBehavior("切屏操作，当前次数：" + switchCount);
    }

    /**
     * 标记作弊
     */
    public void markAsCheating(String reason) {
        this.isCheating = true;
        this.cheatingReason = reason;
        recordAbnormalBehavior("标记作弊：" + reason);
    }

    /**
     * 获取考试标题
     */
    public String getExamTitle() {
        return exam != null ? exam.getTitle() : null;
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
     * 获取阅卷教师姓名
     */
    public String getGraderName() {
        return grader != null ? grader.getRealName() : null;
    }

    // ================================
    // 验证方法
    // ================================

    /**
     * 验证考试记录数据
     */
    @PrePersist
    @PreUpdate
    public void validateExamRecord() {
        // 确保提交时间在开始时间之后
        if (submitTime != null && startTime != null && submitTime.isBefore(startTime)) {
            throw new IllegalArgumentException("提交时间不能早于开始时间");
        }
        
        // 确保得分不超过总分
        if (score != null && totalScore != null && score.compareTo(totalScore) > 0) {
            throw new IllegalArgumentException("得分不能超过总分");
        }
        
        // 确保分数为非负数
        if (score != null && score.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("得分不能为负数");
        }
    }

    // ================================
    // Getter and Setter methods
    // ================================

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public Boolean getIsPassed() {
        return isPassed;
    }

    public void setIsPassed(Boolean isPassed) {
        this.isPassed = isPassed;
    }

    public String getExamStatus() {
        return examStatus;
    }

    public void setExamStatus(String examStatus) {
        this.examStatus = examStatus;
    }

    public String getAnswerDetails() {
        return answerDetails;
    }

    public void setAnswerDetails(String answerDetails) {
        this.answerDetails = answerDetails;
    }

    public Integer getSwitchCount() {
        return switchCount;
    }

    public void setSwitchCount(Integer switchCount) {
        this.switchCount = switchCount;
    }

    public String getAbnormalBehaviors() {
        return abnormalBehaviors;
    }

    public void setAbnormalBehaviors(String abnormalBehaviors) {
        this.abnormalBehaviors = abnormalBehaviors;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getBrowserInfo() {
        return browserInfo;
    }

    public void setBrowserInfo(String browserInfo) {
        this.browserInfo = browserInfo;
    }

    public Boolean getIsCheating() {
        return isCheating;
    }

    public void setIsCheating(Boolean isCheating) {
        this.isCheating = isCheating;
    }

    public String getCheatingReason() {
        return cheatingReason;
    }

    public void setCheatingReason(String cheatingReason) {
        this.cheatingReason = cheatingReason;
    }

    public String getTeacherComment() {
        return teacherComment;
    }

    public void setTeacherComment(String teacherComment) {
        this.teacherComment = teacherComment;
    }

    public Long getGraderId() {
        return graderId;
    }

    public void setGraderId(Long graderId) {
        this.graderId = graderId;
    }

    public LocalDateTime getGradedTime() {
        return gradedTime;
    }

    public void setGradedTime(LocalDateTime gradedTime) {
        this.gradedTime = gradedTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public User getGrader() {
        return grader;
    }

    public void setGrader(User grader) {
        this.grader = grader;
    }
}
