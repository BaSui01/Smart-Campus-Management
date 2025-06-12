package com.campus.domain.entity.exam;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

import com.campus.domain.entity.academic.Course;
import com.campus.domain.entity.auth.User;
import com.campus.domain.entity.infrastructure.BaseEntity;

/**
 * 在线考试实体类
 * 管理在线考试信息
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_exam", indexes = {
    @Index(name = "idx_course_id", columnList = "course_id"),
    @Index(name = "idx_teacher_id", columnList = "teacher_id"),
    @Index(name = "idx_start_time", columnList = "start_time"),
    @Index(name = "idx_end_time", columnList = "end_time"),
    @Index(name = "idx_status_deleted", columnList = "status,deleted")
})
public class Exam extends BaseEntity {

    /**
     * 考试标题
     */
    @NotBlank(message = "考试标题不能为空")
    @Size(max = 200, message = "考试标题长度不能超过200个字符")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 考试描述
     */
    @Size(max = 1000, message = "考试描述长度不能超过1000个字符")
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    /**
     * 教师ID
     */
    @NotNull(message = "教师ID不能为空")
    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    /**
     * 班级ID（可以指定特定班级）
     */
    @Column(name = "class_id")
    private Long classId;

    /**
     * 学期
     */
    @NotBlank(message = "学期不能为空")
    @Size(max = 20, message = "学期长度不能超过20个字符")
    @Column(name = "semester", nullable = false, length = 20)
    private String semester;

    /**
     * 学年
     */
    @NotNull(message = "学年不能为空")
    @Column(name = "academic_year", nullable = false)
    private Integer academicYear;

    /**
     * 考试类型
     * quiz（测验）, midterm（期中考试）, final（期末考试）, makeup（补考）
     */
    @NotBlank(message = "考试类型不能为空")
    @Size(max = 20, message = "考试类型长度不能超过20个字符")
    @Column(name = "exam_type", nullable = false, length = 20)
    private String examType = "quiz";

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * 考试时长（分钟）
     */
    @NotNull(message = "考试时长不能为空")
    @Min(value = 1, message = "考试时长不能小于1分钟")
    @Max(value = 600, message = "考试时长不能超过600分钟")
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    /**
     * 总分
     */
    @NotNull(message = "总分不能为空")
    @Min(value = 1, message = "总分不能小于1")
    @Max(value = 1000, message = "总分不能大于1000")
    @Column(name = "total_score", nullable = false)
    private Integer totalScore = 100;

    /**
     * 及格分数
     */
    @Min(value = 0, message = "及格分数不能小于0")
    @Column(name = "pass_score")
    private Integer passScore = 60;

    /**
     * 考试说明
     */
    @Size(max = 2000, message = "考试说明长度不能超过2000个字符")
    @Column(name = "instructions", length = 2000)
    private String instructions;

    /**
     * 是否随机题目顺序
     */
    @Column(name = "random_questions")
    private Boolean randomQuestions = false;

    /**
     * 是否随机选项顺序
     */
    @Column(name = "random_options")
    private Boolean randomOptions = false;

    /**
     * 是否允许回看题目
     */
    @Column(name = "allow_review")
    private Boolean allowReview = true;

    /**
     * 是否显示题目编号
     */
    @Column(name = "show_question_number")
    private Boolean showQuestionNumber = true;

    /**
     * 是否一次显示一题
     */
    @Column(name = "one_question_per_page")
    private Boolean oneQuestionPerPage = false;

    /**
     * 防作弊模式
     * none（无）, basic（基础）, strict（严格）
     */
    @Size(max = 20, message = "防作弊模式长度不能超过20个字符")
    @Column(name = "anti_cheat_mode", length = 20)
    private String antiCheatMode = "basic";

    /**
     * 是否全屏模式
     */
    @Column(name = "fullscreen_mode")
    private Boolean fullscreenMode = false;

    /**
     * 最大切换次数（防作弊）
     */
    @Column(name = "max_switch_count")
    private Integer maxSwitchCount = 3;

    /**
     * 是否录制考试过程
     */
    @Column(name = "record_exam")
    private Boolean recordExam = false;

    /**
     * 考试状态
     * draft（草稿）, published（已发布）, ongoing（进行中）, finished（已结束）, cancelled（已取消）
     */
    @NotBlank(message = "考试状态不能为空")
    @Size(max = 20, message = "考试状态长度不能超过20个字符")
    @Column(name = "exam_status", nullable = false, length = 20)
    private String examStatus = "draft";

    /**
     * 参与人数
     */
    @Column(name = "participant_count")
    private Integer participantCount = 0;

    /**
     * 已完成人数
     */
    @Column(name = "completed_count")
    private Integer completedCount = 0;

    /**
     * 平均分
     */
    @Column(name = "average_score", precision = 5, scale = 2)
    private java.math.BigDecimal averageScore;

    /**
     * 及格率
     */
    @Column(name = "pass_rate", precision = 5, scale = 2)
    private java.math.BigDecimal passRate;

    /**
     * 发布时间
     */
    @Column(name = "published_time")
    private LocalDateTime publishedTime;

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
     * 关联课程
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;

    /**
     * 关联教师
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private User teacher;

    /**
     * 考试题目
     */
    @OneToMany(mappedBy = "exam", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ExamQuestion> questions;

    /**
     * 考试记录
     */
    @OneToMany(mappedBy = "exam", fetch = FetchType.LAZY)
    private List<ExamRecord> examRecords;

    // ================================
    // 构造函数
    // ================================

    public Exam() {
        super();
    }

    public Exam(String title, Long courseId, Long teacherId, LocalDateTime startTime, LocalDateTime endTime) {
        this();
        this.title = title;
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = (int) java.time.Duration.between(startTime, endTime).toMinutes();
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 发布考试
     */
    public void publish() {
        this.examStatus = "published";
        this.publishedTime = LocalDateTime.now();
        this.status = 1; // 激活状态
    }

    /**
     * 开始考试
     */
    public void start() {
        this.examStatus = "ongoing";
    }

    /**
     * 结束考试
     */
    public void finish() {
        this.examStatus = "finished";
    }

    /**
     * 取消考试
     */
    public void cancel() {
        this.examStatus = "cancelled";
        this.status = 0; // 停用状态
    }

    /**
     * 检查考试是否进行中
     */
    public boolean isOngoing() {
        LocalDateTime now = LocalDateTime.now();
        return "published".equals(examStatus) && 
               now.isAfter(startTime) && now.isBefore(endTime);
    }

    /**
     * 检查考试是否已结束
     */
    public boolean isFinished() {
        return "finished".equals(examStatus) || LocalDateTime.now().isAfter(endTime);
    }

    /**
     * 检查考试是否未开始
     */
    public boolean isNotStarted() {
        return "published".equals(examStatus) && LocalDateTime.now().isBefore(startTime);
    }

    /**
     * 获取考试类型文本
     */
    public String getExamTypeText() {
        if (examType == null) return "未知";
        return switch (examType) {
            case "quiz" -> "测验";
            case "midterm" -> "期中考试";
            case "final" -> "期末考试";
            case "makeup" -> "补考";
            default -> examType;
        };
    }

    /**
     * 获取考试状态文本
     */
    public String getExamStatusText() {
        if (examStatus == null) return "未知";
        return switch (examStatus) {
            case "draft" -> "草稿";
            case "published" -> "已发布";
            case "ongoing" -> "进行中";
            case "finished" -> "已结束";
            case "cancelled" -> "已取消";
            default -> examStatus;
        };
    }

    /**
     * 获取防作弊模式文本
     */
    public String getAntiCheatModeText() {
        if (antiCheatMode == null) return "无";
        return switch (antiCheatMode) {
            case "none" -> "无";
            case "basic" -> "基础";
            case "strict" -> "严格";
            default -> antiCheatMode;
        };
    }

    /**
     * 计算剩余时间（分钟）
     */
    public long getRemainingMinutes() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime)) {
            return java.time.Duration.between(now, startTime).toMinutes();
        } else if (now.isBefore(endTime)) {
            return java.time.Duration.between(now, endTime).toMinutes();
        }
        return 0;
    }

    /**
     * 获取剩余时间描述
     */
    public String getRemainingTimeDescription() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime)) {
            long minutes = java.time.Duration.between(now, startTime).toMinutes();
            if (minutes < 60) {
                return minutes + "分钟后开始";
            } else {
                long hours = minutes / 60;
                return hours + "小时后开始";
            }
        } else if (now.isBefore(endTime)) {
            long minutes = java.time.Duration.between(now, endTime).toMinutes();
            if (minutes < 60) {
                return minutes + "分钟后结束";
            } else {
                long hours = minutes / 60;
                return hours + "小时后结束";
            }
        }
        return "已结束";
    }

    /**
     * 计算完成率
     */
    public double getCompletionRate() {
        if (participantCount == null || participantCount == 0 || completedCount == null) {
            return 0.0;
        }
        return (double) completedCount / participantCount * 100;
    }

    /**
     * 更新统计信息
     */
    public void updateStatistics(int participants, int completed, 
                               java.math.BigDecimal avgScore, java.math.BigDecimal passR) {
        this.participantCount = participants;
        this.completedCount = completed;
        this.averageScore = avgScore;
        this.passRate = passR;
    }

    /**
     * 获取课程名称
     */
    public String getCourseName() {
        return course != null ? course.getCourseName() : null;
    }

    /**
     * 获取教师姓名
     */
    public String getTeacherName() {
        return teacher != null ? teacher.getRealName() : null;
    }

    /**
     * 获取题目数量
     */
    public int getQuestionCount() {
        return questions != null ? questions.size() : 0;
    }

    /**
     * 检查是否可以开始考试
     */
    public boolean canStart() {
        return "published".equals(examStatus) && 
               LocalDateTime.now().isAfter(startTime) && 
               LocalDateTime.now().isBefore(endTime);
    }

    /**
     * 检查是否可以编辑
     */
    public boolean canEdit() {
        return "draft".equals(examStatus) || 
               ("published".equals(examStatus) && LocalDateTime.now().isBefore(startTime));
    }

    // ================================
    // 验证方法
    // ================================

    /**
     * 验证考试数据
     */
    @PrePersist
    @PreUpdate
    public void validateExam() {
        // 确保结束时间在开始时间之后
        if (endTime != null && startTime != null && endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("结束时间不能早于开始时间");
        }
        
        // 确保考试时长合理
        if (startTime != null && endTime != null) {
            long actualDuration = java.time.Duration.between(startTime, endTime).toMinutes();
            if (durationMinutes != null && Math.abs(actualDuration - durationMinutes) > 5) {
                // 允许5分钟的误差
                this.durationMinutes = (int) actualDuration;
            }
        }
        
        // 确保及格分数不超过总分
        if (passScore != null && totalScore != null && passScore > totalScore) {
            throw new IllegalArgumentException("及格分数不能超过总分");
        }
    }

    // ================================
    // Getter and Setter methods
    // ================================

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
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

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getPassScore() {
        return passScore;
    }

    public void setPassScore(Integer passScore) {
        this.passScore = passScore;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Boolean getRandomQuestions() {
        return randomQuestions;
    }

    public void setRandomQuestions(Boolean randomQuestions) {
        this.randomQuestions = randomQuestions;
    }

    public Boolean getRandomOptions() {
        return randomOptions;
    }

    public void setRandomOptions(Boolean randomOptions) {
        this.randomOptions = randomOptions;
    }

    public Boolean getAllowReview() {
        return allowReview;
    }

    public void setAllowReview(Boolean allowReview) {
        this.allowReview = allowReview;
    }

    public Boolean getShowQuestionNumber() {
        return showQuestionNumber;
    }

    public void setShowQuestionNumber(Boolean showQuestionNumber) {
        this.showQuestionNumber = showQuestionNumber;
    }

    public Boolean getOneQuestionPerPage() {
        return oneQuestionPerPage;
    }

    public void setOneQuestionPerPage(Boolean oneQuestionPerPage) {
        this.oneQuestionPerPage = oneQuestionPerPage;
    }

    public String getAntiCheatMode() {
        return antiCheatMode;
    }

    public void setAntiCheatMode(String antiCheatMode) {
        this.antiCheatMode = antiCheatMode;
    }

    public Boolean getFullscreenMode() {
        return fullscreenMode;
    }

    public void setFullscreenMode(Boolean fullscreenMode) {
        this.fullscreenMode = fullscreenMode;
    }

    public Integer getMaxSwitchCount() {
        return maxSwitchCount;
    }

    public void setMaxSwitchCount(Integer maxSwitchCount) {
        this.maxSwitchCount = maxSwitchCount;
    }

    public Boolean getRecordExam() {
        return recordExam;
    }

    public void setRecordExam(Boolean recordExam) {
        this.recordExam = recordExam;
    }

    public String getExamStatus() {
        return examStatus;
    }

    public void setExamStatus(String examStatus) {
        this.examStatus = examStatus;
    }

    public Integer getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(Integer participantCount) {
        this.participantCount = participantCount;
    }

    public Integer getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(Integer completedCount) {
        this.completedCount = completedCount;
    }

    public java.math.BigDecimal getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(java.math.BigDecimal averageScore) {
        this.averageScore = averageScore;
    }

    public java.math.BigDecimal getPassRate() {
        return passRate;
    }

    public void setPassRate(java.math.BigDecimal passRate) {
        this.passRate = passRate;
    }

    public LocalDateTime getPublishedTime() {
        return publishedTime;
    }

    public void setPublishedTime(LocalDateTime publishedTime) {
        this.publishedTime = publishedTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public List<ExamQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<ExamQuestion> questions) {
        this.questions = questions;
    }

    public List<ExamRecord> getExamRecords() {
        return examRecords;
    }

    public void setExamRecords(List<ExamRecord> examRecords) {
        this.examRecords = examRecords;
    }

    // ================================
    // equals, hashCode, toString methods
    // ================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        
        Exam exam = (Exam) o;
        
        if (title != null ? !title.equals(exam.title) : exam.title != null) return false;
        if (description != null ? !description.equals(exam.description) : exam.description != null) return false;
        if (courseId != null ? !courseId.equals(exam.courseId) : exam.courseId != null) return false;
        if (teacherId != null ? !teacherId.equals(exam.teacherId) : exam.teacherId != null) return false;
        if (classId != null ? !classId.equals(exam.classId) : exam.classId != null) return false;
        if (semester != null ? !semester.equals(exam.semester) : exam.semester != null) return false;
        if (academicYear != null ? !academicYear.equals(exam.academicYear) : exam.academicYear != null) return false;
        if (examType != null ? !examType.equals(exam.examType) : exam.examType != null) return false;
        if (startTime != null ? !startTime.equals(exam.startTime) : exam.startTime != null) return false;
        if (endTime != null ? !endTime.equals(exam.endTime) : exam.endTime != null) return false;
        if (durationMinutes != null ? !durationMinutes.equals(exam.durationMinutes) : exam.durationMinutes != null) return false;
        if (totalScore != null ? !totalScore.equals(exam.totalScore) : exam.totalScore != null) return false;
        if (passScore != null ? !passScore.equals(exam.passScore) : exam.passScore != null) return false;
        if (instructions != null ? !instructions.equals(exam.instructions) : exam.instructions != null) return false;
        if (randomQuestions != null ? !randomQuestions.equals(exam.randomQuestions) : exam.randomQuestions != null) return false;
        if (randomOptions != null ? !randomOptions.equals(exam.randomOptions) : exam.randomOptions != null) return false;
        if (allowReview != null ? !allowReview.equals(exam.allowReview) : exam.allowReview != null) return false;
        if (showQuestionNumber != null ? !showQuestionNumber.equals(exam.showQuestionNumber) : exam.showQuestionNumber != null) return false;
        if (oneQuestionPerPage != null ? !oneQuestionPerPage.equals(exam.oneQuestionPerPage) : exam.oneQuestionPerPage != null) return false;
        if (antiCheatMode != null ? !antiCheatMode.equals(exam.antiCheatMode) : exam.antiCheatMode != null) return false;
        if (fullscreenMode != null ? !fullscreenMode.equals(exam.fullscreenMode) : exam.fullscreenMode != null) return false;
        if (maxSwitchCount != null ? !maxSwitchCount.equals(exam.maxSwitchCount) : exam.maxSwitchCount != null) return false;
        if (recordExam != null ? !recordExam.equals(exam.recordExam) : exam.recordExam != null) return false;
        if (examStatus != null ? !examStatus.equals(exam.examStatus) : exam.examStatus != null) return false;
        if (participantCount != null ? !participantCount.equals(exam.participantCount) : exam.participantCount != null) return false;
        if (completedCount != null ? !completedCount.equals(exam.completedCount) : exam.completedCount != null) return false;
        if (averageScore != null ? !averageScore.equals(exam.averageScore) : exam.averageScore != null) return false;
        if (passRate != null ? !passRate.equals(exam.passRate) : exam.passRate != null) return false;
        if (publishedTime != null ? !publishedTime.equals(exam.publishedTime) : exam.publishedTime != null) return false;
        return remarks != null ? remarks.equals(exam.remarks) : exam.remarks == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (courseId != null ? courseId.hashCode() : 0);
        result = 31 * result + (teacherId != null ? teacherId.hashCode() : 0);
        result = 31 * result + (classId != null ? classId.hashCode() : 0);
        result = 31 * result + (semester != null ? semester.hashCode() : 0);
        result = 31 * result + (academicYear != null ? academicYear.hashCode() : 0);
        result = 31 * result + (examType != null ? examType.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (durationMinutes != null ? durationMinutes.hashCode() : 0);
        result = 31 * result + (totalScore != null ? totalScore.hashCode() : 0);
        result = 31 * result + (passScore != null ? passScore.hashCode() : 0);
        result = 31 * result + (instructions != null ? instructions.hashCode() : 0);
        result = 31 * result + (randomQuestions != null ? randomQuestions.hashCode() : 0);
        result = 31 * result + (randomOptions != null ? randomOptions.hashCode() : 0);
        result = 31 * result + (allowReview != null ? allowReview.hashCode() : 0);
        result = 31 * result + (showQuestionNumber != null ? showQuestionNumber.hashCode() : 0);
        result = 31 * result + (oneQuestionPerPage != null ? oneQuestionPerPage.hashCode() : 0);
        result = 31 * result + (antiCheatMode != null ? antiCheatMode.hashCode() : 0);
        result = 31 * result + (fullscreenMode != null ? fullscreenMode.hashCode() : 0);
        result = 31 * result + (maxSwitchCount != null ? maxSwitchCount.hashCode() : 0);
        result = 31 * result + (recordExam != null ? recordExam.hashCode() : 0);
        result = 31 * result + (examStatus != null ? examStatus.hashCode() : 0);
        result = 31 * result + (participantCount != null ? participantCount.hashCode() : 0);
        result = 31 * result + (completedCount != null ? completedCount.hashCode() : 0);
        result = 31 * result + (averageScore != null ? averageScore.hashCode() : 0);
        result = 31 * result + (passRate != null ? passRate.hashCode() : 0);
        result = 31 * result + (publishedTime != null ? publishedTime.hashCode() : 0);
        result = 31 * result + (remarks != null ? remarks.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", courseId=" + courseId +
                ", teacherId=" + teacherId +
                ", classId=" + classId +
                ", semester='" + semester + '\'' +
                ", academicYear=" + academicYear +
                ", examType='" + examType + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", durationMinutes=" + durationMinutes +
                ", totalScore=" + totalScore +
                ", passScore=" + passScore +
                ", instructions='" + instructions + '\'' +
                ", randomQuestions=" + randomQuestions +
                ", randomOptions=" + randomOptions +
                ", allowReview=" + allowReview +
                ", showQuestionNumber=" + showQuestionNumber +
                ", oneQuestionPerPage=" + oneQuestionPerPage +
                ", antiCheatMode='" + antiCheatMode + '\'' +
                ", fullscreenMode=" + fullscreenMode +
                ", maxSwitchCount=" + maxSwitchCount +
                ", recordExam=" + recordExam +
                ", examStatus='" + examStatus + '\'' +
                ", participantCount=" + participantCount +
                ", completedCount=" + completedCount +
                ", averageScore=" + averageScore +
                ", passRate=" + passRate +
                ", publishedTime=" + publishedTime +
                ", remarks='" + remarks + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                ", status=" + getStatus() +
                ", deleted=" + getDeleted() +
                '}';
    }

    // ================================
    // 兼容性方法 - 为了支持 ExamApiController
    // ================================

    /**
     * 获取考试名称（兼容性方法）
     */
    public String getExamName() {
        return title;
    }

    /**
     * 设置考试名称（兼容性方法）
     */
    public void setExamName(String examName) {
        this.title = examName;
    }

    /**
     * 获取教室ID（兼容性方法）
     */
    public Long getClassroomId() {
        return classId;
    }

    /**
     * 设置教室ID（兼容性方法）
     */
    public void setClassroomId(Long classroomId) {
        this.classId = classroomId;
    }

    /**
     * 获取及格分数（兼容性方法）
     */
    public Integer getPassingScore() {
        return passScore;
    }

    /**
     * 设置及格分数（兼容性方法）
     */
    public void setPassingScore(Integer passingScore) {
        this.passScore = passingScore;
    }

    /**
     * 获取最大参与人数（兼容性方法）
     */
    public Integer getMaxParticipants() {
        // 如果没有专门的字段，可以返回一个默认值或者 null
        return null;
    }

    /**
     * 设置最大参与人数（兼容性方法）
     */
    public void setMaxParticipants(Integer maxParticipants) {
        // 如果没有专门的字段，可以暂时不做处理
    }

    /**
     * 获取是否在线考试（兼容性方法）
     */
    public Boolean getIsOnline() {
        // 根据防作弊模式或其他字段判断，这里简单返回 true
        return true;
    }

    /**
     * 设置是否在线考试（兼容性方法）
     */
    public void setIsOnline(Boolean isOnline) {
        // 可以根据需要设置相关字段
    }

    /**
     * 设置是否发布（兼容性方法）
     */
    public void setIsPublished(int isPublished) {
        if (isPublished == 1) {
            this.examStatus = "published";
            this.publishedTime = LocalDateTime.now();
        } else {
            this.examStatus = "draft";
            this.publishedTime = null;
        }
    }

    /**
     * 获取是否发布（兼容性方法）
     */
    public int getIsPublished() {
        return "published".equals(examStatus) ? 1 : 0;
    }

    /**
     * 设置题目数量（兼容性方法）
     */
    public void setQuestionCount(int questionCount) {
        // 题目数量通常由关联的题目列表决定，这里可以暂时不做处理
        // 或者可以添加一个字段来存储预期的题目数量
    }

    /**
     * 获取是否随机题目（兼容性方法）
     */
    public Boolean getShuffleQuestions() {
        return randomQuestions;
    }

    /**
     * 设置是否随机题目（兼容性方法）
     */
    public void setShuffleQuestions(Boolean shuffleQuestions) {
        this.randomQuestions = shuffleQuestions;
    }

    /**
     * 获取是否随机选项（兼容性方法）
     */
    public Boolean getShuffleOptions() {
        return randomOptions;
    }

    /**
     * 设置是否随机选项（兼容性方法）
     */
    public void setShuffleOptions(Boolean shuffleOptions) {
        this.randomOptions = shuffleOptions;
    }

    /**
     * 获取是否启用防作弊（兼容性方法）
     */
    public Boolean getAntiCheatEnabled() {
        return !"none".equals(antiCheatMode);
    }

    /**
     * 设置是否启用防作弊（兼容性方法）
     */
    public void setAntiCheatEnabled(Boolean antiCheatEnabled) {
        this.antiCheatMode = Boolean.TRUE.equals(antiCheatEnabled) ? "basic" : "none";
    }

    /**
     * 获取是否需要摄像头（兼容性方法）
     */
    public Boolean getCameraRequired() {
        return recordExam;
    }

    /**
     * 设置是否需要摄像头（兼容性方法）
     */
    public void setCameraRequired(Boolean cameraRequired) {
        this.recordExam = cameraRequired;
    }

    /**
     * 获取是否锁定屏幕（兼容性方法）
     */
    public Boolean getScreenLock() {
        return fullscreenMode;
    }

    /**
     * 设置是否锁定屏幕（兼容性方法）
     */
    public void setScreenLock(Boolean screenLock) {
        this.fullscreenMode = screenLock;
    }

    /**
     * 获取是否自动提交（兼容性方法）
     */
    public Boolean getAutoSubmit() {
        // 可以根据需要返回一个默认值，这里假设总是自动提交
        return true;
    }

    /**
     * 设置是否自动提交（兼容性方法）
     */
    public void setAutoSubmit(Boolean autoSubmit) {
        // 可以根据需要设置相关字段
    }

    /**
     * 获取是否允许迟交（兼容性方法）
     */
    public Boolean getLateSubmissionAllowed() {
        // 可以根据需要返回一个默认值，这里假设不允许迟交
        return false;
    }

    /**
     * 设置是否允许迟交（兼容性方法）
     */
    public void setLateSubmissionAllowed(Boolean lateSubmissionAllowed) {
        // 可以根据需要设置相关字段
    }
}