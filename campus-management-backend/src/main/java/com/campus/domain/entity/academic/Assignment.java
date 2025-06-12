package com.campus.domain.entity.academic;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

import com.campus.domain.entity.auth.User;
import com.campus.domain.entity.infrastructure.BaseEntity;

/**
 * 作业实体类
 * 管理教师发布的作业信息
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_assignment", indexes = {
    @Index(name = "idx_course_id", columnList = "course_id"),
    @Index(name = "idx_teacher_id", columnList = "teacher_id"),
    @Index(name = "idx_class_id", columnList = "class_id"),
    @Index(name = "idx_due_date", columnList = "due_date"),
    @Index(name = "idx_status_deleted", columnList = "status,deleted")
})
public class Assignment extends BaseEntity {

    /**
     * 作业标题
     */
    @NotBlank(message = "作业标题不能为空")
    @Size(max = 200, message = "作业标题长度不能超过200个字符")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 作业描述
     */
    @NotBlank(message = "作业描述不能为空")
    @Size(max = 2000, message = "作业描述长度不能超过2000个字符")
    @Column(name = "description", nullable = false, length = 2000)
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
     * 作业类型
     * homework（作业）, project（项目）, report（报告）, experiment（实验）
     */
    @NotBlank(message = "作业类型不能为空")
    @Size(max = 20, message = "作业类型长度不能超过20个字符")
    @Column(name = "assignment_type", nullable = false, length = 20)
    private String assignmentType = "homework";

    /**
     * 截止时间
     */
    @NotNull(message = "截止时间不能为空")
    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    /**
     * 发布时间
     */
    @Column(name = "publish_date")
    private LocalDateTime publishDate;

    /**
     * 最大分数
     */
    @NotNull(message = "最大分数不能为空")
    @Min(value = 1, message = "最大分数不能小于1")
    @Max(value = 1000, message = "最大分数不能大于1000")
    @Column(name = "max_score", nullable = false)
    private Integer maxScore = 100;

    /**
     * 是否允许迟交
     */
    @Column(name = "allow_late_submission")
    private Boolean allowLateSubmission = false;

    /**
     * 迟交扣分百分比
     */
    @DecimalMin(value = "0.0", message = "迟交扣分百分比不能小于0")
    @DecimalMax(value = "100.0", message = "迟交扣分百分比不能大于100")
    @Column(name = "late_penalty_percent")
    private java.math.BigDecimal latePenaltyPercent = java.math.BigDecimal.ZERO;

    /**
     * 允许的文件类型（逗号分隔）
     */
    @Size(max = 200, message = "文件类型长度不能超过200个字符")
    @Column(name = "allowed_file_types", length = 200)
    private String allowedFileTypes = "pdf,doc,docx,txt";

    /**
     * 最大文件大小（MB）
     */
    @Min(value = 1, message = "最大文件大小不能小于1MB")
    @Max(value = 100, message = "最大文件大小不能大于100MB")
    @Column(name = "max_file_size")
    private Integer maxFileSize = 10;

    /**
     * 作业要求
     */
    @Size(max = 1000, message = "作业要求长度不能超过1000个字符")
    @Column(name = "requirements", length = 1000)
    private String requirements;

    /**
     * 参考资料
     */
    @Size(max = 1000, message = "参考资料长度不能超过1000个字符")
    @Column(name = "reference_materials", length = 1000)
    private String referenceMaterials;

    /**
     * 提交方式
     * online（在线提交）, offline（线下提交）, both（两种方式）
     */
    @NotBlank(message = "提交方式不能为空")
    @Size(max = 20, message = "提交方式长度不能超过20个字符")
    @Column(name = "submission_method", nullable = false, length = 20)
    private String submissionMethod = "online";

    /**
     * 是否已发布
     */
    @Column(name = "is_published")
    private Boolean isPublished = false;

    /**
     * 难度等级
     */
    @Size(max = 20, message = "难度等级长度不能超过20个字符")
    @Column(name = "difficulty_level", length = 20)
    private String difficultyLevel = "medium";

    /**
     * 预计完成时间（小时）
     */
    @Min(value = 1, message = "预计完成时间不能小于1小时")
    @Max(value = 100, message = "预计完成时间不能大于100小时")
    @Column(name = "estimated_hours")
    private Integer estimatedHours = 2;

    /**
     * 权重（在总成绩中的占比）
     */
    @DecimalMin(value = "0.0", message = "权重不能小于0")
    @DecimalMax(value = "100.0", message = "权重不能大于100")
    @Column(name = "weight", precision = 5, scale = 2)
    private java.math.BigDecimal weight = java.math.BigDecimal.valueOf(10.0);

    /**
     * 提交格式要求
     */
    @Size(max = 200, message = "提交格式要求长度不能超过200个字符")
    @Column(name = "submission_format", length = 200)
    private String submissionFormat = "文档";

    /**
     * 是否为小组作业
     */
    @Column(name = "is_group_assignment")
    private Boolean isGroupAssignment = false;

    /**
     * 小组最大人数
     */
    @Min(value = 2, message = "小组最大人数不能小于2")
    @Max(value = 10, message = "小组最大人数不能大于10")
    @Column(name = "max_group_size")
    private Integer maxGroupSize = 4;

    /**
     * 是否自动评分
     */
    @Column(name = "auto_grade")
    private Boolean autoGrade = false;

    /**
     * 总提交人数
     */
    @Column(name = "total_submissions")
    private Integer totalSubmissions = 0;

    /**
     * 已批改数量
     */
    @Column(name = "graded_count")
    private Integer gradedCount = 0;

    /**
     * 平均分
     */
    @Column(name = "average_score", precision = 5, scale = 2)
    private java.math.BigDecimal averageScore;

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
     * 作业提交记录
     */
    @OneToMany(mappedBy = "assignment", fetch = FetchType.LAZY)
    private List<AssignmentSubmission> submissions;

    // ================================
    // 构造函数
    // ================================

    public Assignment() {
        super();
    }

    public Assignment(String title, Long courseId, Long teacherId, LocalDateTime dueDate) {
        this();
        this.title = title;
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.dueDate = dueDate;
    }

    // ================================
    // Getter和Setter方法
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

    public String getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(String assignmentType) {
        this.assignmentType = assignmentType;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }

    public Boolean getAllowLateSubmission() {
        return allowLateSubmission;
    }

    public void setAllowLateSubmission(Boolean allowLateSubmission) {
        this.allowLateSubmission = allowLateSubmission;
    }

    public java.math.BigDecimal getLatePenaltyPercent() {
        return latePenaltyPercent;
    }

    public void setLatePenaltyPercent(java.math.BigDecimal latePenaltyPercent) {
        this.latePenaltyPercent = latePenaltyPercent;
    }

    public String getAllowedFileTypes() {
        return allowedFileTypes;
    }

    public void setAllowedFileTypes(String allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
    }

    public Integer getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Integer maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getReferenceMaterials() {
        return referenceMaterials;
    }

    public void setReferenceMaterials(String referenceMaterials) {
        this.referenceMaterials = referenceMaterials;
    }

    public String getSubmissionMethod() {
        return submissionMethod;
    }

    public void setSubmissionMethod(String submissionMethod) {
        this.submissionMethod = submissionMethod;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }

    public Integer getTotalSubmissions() {
        return totalSubmissions;
    }

    public void setTotalSubmissions(Integer totalSubmissions) {
        this.totalSubmissions = totalSubmissions;
    }

    public Integer getGradedCount() {
        return gradedCount;
    }

    public void setGradedCount(Integer gradedCount) {
        this.gradedCount = gradedCount;
    }

    public java.math.BigDecimal getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(java.math.BigDecimal averageScore) {
        this.averageScore = averageScore;
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

    public List<AssignmentSubmission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<AssignmentSubmission> submissions) {
        this.submissions = submissions;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public Integer getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Integer estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public java.math.BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(java.math.BigDecimal weight) {
        this.weight = weight;
    }

    public String getSubmissionFormat() {
        return submissionFormat;
    }

    public void setSubmissionFormat(String submissionFormat) {
        this.submissionFormat = submissionFormat;
    }

    public Boolean getIsGroupAssignment() {
        return isGroupAssignment;
    }

    public void setIsGroupAssignment(Boolean isGroupAssignment) {
        this.isGroupAssignment = isGroupAssignment;
    }

    public Integer getMaxGroupSize() {
        return maxGroupSize;
    }

    public void setMaxGroupSize(Integer maxGroupSize) {
        this.maxGroupSize = maxGroupSize;
    }

    public Boolean getAutoGrade() {
        return autoGrade;
    }

    public void setAutoGrade(Boolean autoGrade) {
        this.autoGrade = autoGrade;
    }

    // 兼容性方法 - 为了支持 AssignmentApiController
    public Boolean getLateSubmissionAllowed() {
        return allowLateSubmission;
    }

    public void setLateSubmissionAllowed(Boolean lateSubmissionAllowed) {
        this.allowLateSubmission = lateSubmissionAllowed;
    }

    public java.math.BigDecimal getLatePenaltyRate() {
        return latePenaltyPercent;
    }

    public void setLatePenaltyRate(java.math.BigDecimal latePenaltyRate) {
        this.latePenaltyPercent = latePenaltyRate;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 发布作业
     */
    public void publish() {
        this.isPublished = true;
        this.publishDate = LocalDateTime.now();
        this.setStatus(1); // 激活状态
    }

    /**
     * 取消发布
     */
    public void unpublish() {
        this.isPublished = false;
        this.setStatus(0); // 停用状态
    }

    /**
     * 检查是否已截止
     */
    public boolean isOverdue() {
        return LocalDateTime.now().isAfter(dueDate);
    }

    /**
     * 检查是否接近截止时间（24小时内）
     */
    public boolean isNearDue() {
        return LocalDateTime.now().plusHours(24).isAfter(dueDate) && !isOverdue();
    }

    /**
     * 获取作业类型文本
     */
    public String getAssignmentTypeText() {
        if (assignmentType == null) return "未知";
        return switch (assignmentType) {
            case "homework" -> "作业";
            case "project" -> "项目";
            case "report" -> "报告";
            case "experiment" -> "实验";
            default -> assignmentType;
        };
    }

    /**
     * 获取提交方式文本
     */
    public String getSubmissionMethodText() {
        if (submissionMethod == null) return "未知";
        return switch (submissionMethod) {
            case "online" -> "在线提交";
            case "offline" -> "线下提交";
            case "both" -> "两种方式";
            default -> submissionMethod;
        };
    }

    /**
     * 计算提交率
     */
    public double getSubmissionRate() {
        if (totalSubmissions == null || totalSubmissions == 0) {
            return 0.0;
        }
        // 这里需要获取班级总人数，暂时返回基于提交数的比率
        return totalSubmissions > 0 ? (double) totalSubmissions / 100 * 100 : 0.0;
    }

    /**
     * 计算批改进度
     */
    public double getGradingProgress() {
        if (totalSubmissions == null || totalSubmissions == 0 || gradedCount == null) {
            return 0.0;
        }
        return (double) gradedCount / totalSubmissions * 100;
    }

    /**
     * 更新统计信息
     */
    public void updateStatistics(int submissions, int graded, java.math.BigDecimal avgScore) {
        this.totalSubmissions = submissions;
        this.gradedCount = graded;
        this.averageScore = avgScore;
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
     * 获取剩余时间描述
     */
    public String getTimeRemaining() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(dueDate)) {
            return "已截止";
        }
        
        long hours = java.time.Duration.between(now, dueDate).toHours();
        if (hours < 24) {
            return hours + "小时后截止";
        } else {
            long days = hours / 24;
            return days + "天后截止";
        }
    }

    /**
     * 获取允许的文件类型数组
     */
    public String[] getAllowedFileTypesArray() {
        if (allowedFileTypes == null || allowedFileTypes.trim().isEmpty()) {
            return new String[0];
        }
        return allowedFileTypes.split(",");
    }

    /**
     * 检查文件类型是否允许
     */
    public boolean isFileTypeAllowed(String fileExtension) {
        if (fileExtension == null) return false;
        String[] allowedTypes = getAllowedFileTypesArray();
        for (String type : allowedTypes) {
            if (type.trim().equalsIgnoreCase(fileExtension.trim())) {
                return true;
            }
        }
        return false;
    }

    // ================================
    // 验证方法
    // ================================

    /**
     * 验证作业数据
     */
    @PrePersist
    @PreUpdate
    public void validateAssignment() {
        // 确保截止时间在未来
        if (dueDate != null && dueDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("截止时间不能早于当前时间");
        }
        
        // 确保最大分数合理
        if (maxScore != null && maxScore <= 0) {
            throw new IllegalArgumentException("最大分数必须大于0");
        }
    }

    // ================================
    // toString、equals、hashCode方法
    // ================================

    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", courseId=" + courseId +
                ", teacherId=" + teacherId +
                ", assignmentType='" + assignmentType + '\'' +
                ", dueDate=" + dueDate +
                ", isPublished=" + isPublished +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Assignment that = (Assignment) o;

        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (courseId != null ? !courseId.equals(that.courseId) : that.courseId != null) return false;
        return teacherId != null ? teacherId.equals(that.teacherId) : that.teacherId == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (courseId != null ? courseId.hashCode() : 0);
        result = 31 * result + (teacherId != null ? teacherId.hashCode() : 0);
        return result;
    }
}