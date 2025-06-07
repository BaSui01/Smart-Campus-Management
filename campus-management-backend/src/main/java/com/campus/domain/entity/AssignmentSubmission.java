package com.campus.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * 作业提交实体类
 * 管理学生提交的作业信息
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_assignment_submission", indexes = {
    @Index(name = "idx_assignment_id", columnList = "assignment_id"),
    @Index(name = "idx_student_id", columnList = "student_id"),
    @Index(name = "idx_submission_time", columnList = "submission_time"),
    @Index(name = "idx_status_deleted", columnList = "status,deleted"),
    @Index(name = "idx_student_assignment", columnList = "student_id,assignment_id")
})
public class AssignmentSubmission extends BaseEntity {

    /**
     * 作业ID
     */
    @NotNull(message = "作业ID不能为空")
    @Column(name = "assignment_id", nullable = false)
    private Long assignmentId;

    /**
     * 学生ID
     */
    @NotNull(message = "学生ID不能为空")
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    /**
     * 提交内容
     */
    @Size(max = 5000, message = "提交内容长度不能超过5000个字符")
    @Column(name = "content", length = 5000)
    private String content;

    /**
     * 附件文件路径
     */
    @Size(max = 500, message = "文件路径长度不能超过500个字符")
    @Column(name = "file_path", length = 500)
    private String filePath;

    /**
     * 原始文件名
     */
    @Size(max = 200, message = "文件名长度不能超过200个字符")
    @Column(name = "original_filename", length = 200)
    private String originalFilename;

    /**
     * 文件大小（字节）
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * 文件类型
     */
    @Size(max = 50, message = "文件类型长度不能超过50个字符")
    @Column(name = "file_type", length = 50)
    private String fileType;

    /**
     * 提交时间
     */
    @Column(name = "submission_time")
    private LocalDateTime submissionTime;

    /**
     * 是否迟交
     */
    @Column(name = "is_late")
    private Boolean isLate = false;

    /**
     * 迟交时长（分钟）
     */
    @Column(name = "late_minutes")
    private Integer lateMinutes = 0;

    /**
     * 提交状态
     * submitted（已提交）, graded（已批改）, returned（已退回）
     */
    @NotBlank(message = "提交状态不能为空")
    @Size(max = 20, message = "提交状态长度不能超过20个字符")
    @Column(name = "submission_status", nullable = false, length = 20)
    private String submissionStatus = "submitted";

    /**
     * 成绩
     */
    @DecimalMin(value = "0.0", message = "成绩不能小于0")
    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;

    /**
     * 教师评语
     */
    @Size(max = 1000, message = "教师评语长度不能超过1000个字符")
    @Column(name = "teacher_comment", length = 1000)
    private String teacherComment;

    /**
     * 批改时间
     */
    @Column(name = "graded_time")
    private LocalDateTime gradedTime;

    /**
     * 批改教师ID
     */
    @Column(name = "graded_by")
    private Long gradedBy;

    /**
     * 提交次数（允许重新提交）
     */
    @Column(name = "submission_count")
    private Integer submissionCount = 1;

    /**
     * 是否为最终提交
     */
    @Column(name = "is_final")
    private Boolean isFinal = true;

    /**
     * 学生自评
     */
    @Size(max = 500, message = "学生自评长度不能超过500个字符")
    @Column(name = "self_evaluation", length = 500)
    private String selfEvaluation;

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
     * 关联作业
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", insertable = false, updatable = false)
    private Assignment assignment;

    /**
     * 关联学生
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    /**
     * 关联批改教师
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by", insertable = false, updatable = false)
    private User grader;

    // ================================
    // 构造函数
    // ================================

    public AssignmentSubmission() {
        super();
    }

    public AssignmentSubmission(Long assignmentId, Long studentId, String content) {
        this();
        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.content = content;
        this.submissionTime = LocalDateTime.now();
    }

    // ================================
    // Getter和Setter方法
    // ================================

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public LocalDateTime getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(LocalDateTime submissionTime) {
        this.submissionTime = submissionTime;
    }

    public Boolean getIsLate() {
        return isLate;
    }

    public void setIsLate(Boolean isLate) {
        this.isLate = isLate;
    }

    public Integer getLateMinutes() {
        return lateMinutes;
    }

    public void setLateMinutes(Integer lateMinutes) {
        this.lateMinutes = lateMinutes;
    }

    public String getSubmissionStatus() {
        return submissionStatus;
    }

    public void setSubmissionStatus(String submissionStatus) {
        this.submissionStatus = submissionStatus;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getTeacherComment() {
        return teacherComment;
    }

    public void setTeacherComment(String teacherComment) {
        this.teacherComment = teacherComment;
    }

    public LocalDateTime getGradedTime() {
        return gradedTime;
    }

    public void setGradedTime(LocalDateTime gradedTime) {
        this.gradedTime = gradedTime;
    }

    public Long getGradedBy() {
        return gradedBy;
    }

    public void setGradedBy(Long gradedBy) {
        this.gradedBy = gradedBy;
    }

    public Integer getSubmissionCount() {
        return submissionCount;
    }

    public void setSubmissionCount(Integer submissionCount) {
        this.submissionCount = submissionCount;
    }

    public Boolean getIsFinal() {
        return isFinal;
    }

    public void setIsFinal(Boolean isFinal) {
        this.isFinal = isFinal;
    }

    public String getSelfEvaluation() {
        return selfEvaluation;
    }

    public void setSelfEvaluation(String selfEvaluation) {
        this.selfEvaluation = selfEvaluation;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
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

    // ================================
    // 业务方法
    // ================================

    /**
     * 提交作业
     */
    public void submit() {
        this.submissionTime = LocalDateTime.now();
        this.submissionStatus = "submitted";
        this.setStatus(1); // 激活状态
        
        // 检查是否迟交
        checkIfLate();
    }

    /**
     * 检查是否迟交
     */
    private void checkIfLate() {
        if (assignment != null && assignment.getDueDate() != null && submissionTime != null) {
            if (submissionTime.isAfter(assignment.getDueDate())) {
                this.isLate = true;
                this.lateMinutes = (int) java.time.Duration.between(assignment.getDueDate(), submissionTime).toMinutes();
            }
        }
    }

    /**
     * 批改作业
     */
    public void grade(BigDecimal score, String comment, Long graderId) {
        this.score = score;
        this.teacherComment = comment;
        this.gradedBy = graderId;
        this.gradedTime = LocalDateTime.now();
        this.submissionStatus = "graded";
        
        // 如果迟交，扣除相应分数
        if (isLate && assignment != null && assignment.getLatePenaltyPercent() != null) {
            BigDecimal penalty = score.multiply(assignment.getLatePenaltyPercent()).divide(new BigDecimal("100"));
            this.score = score.subtract(penalty).max(BigDecimal.ZERO);
        }
    }

    /**
     * 退回作业
     */
    public void returnSubmission(String reason) {
        this.submissionStatus = "returned";
        this.teacherComment = reason;
        this.isFinal = false;
    }

    /**
     * 重新提交
     */
    public void resubmit(String newContent) {
        this.content = newContent;
        this.submissionTime = LocalDateTime.now();
        this.submissionStatus = "submitted";
        this.submissionCount++;
        this.isFinal = true;
        
        // 重新检查是否迟交
        checkIfLate();
    }

    /**
     * 获取提交状态文本
     */
    public String getSubmissionStatusText() {
        if (submissionStatus == null) return "未知";
        return switch (submissionStatus) {
            case "submitted" -> "已提交";
            case "graded" -> "已批改";
            case "returned" -> "已退回";
            default -> submissionStatus;
        };
    }

    /**
     * 获取成绩百分比
     */
    public double getScorePercentage() {
        if (score == null || assignment == null || assignment.getMaxScore() == null) {
            return 0.0;
        }
        return score.divide(new BigDecimal(assignment.getMaxScore()), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")).doubleValue();
    }

    /**
     * 是否已批改
     */
    public boolean isGraded() {
        return "graded".equals(submissionStatus) && score != null;
    }

    /**
     * 是否可以重新提交
     */
    public boolean canResubmit() {
        return "returned".equals(submissionStatus) || 
               (!isFinal && assignment != null && !assignment.isOverdue());
    }

    /**
     * 获取文件大小描述
     */
    public String getFileSizeDescription() {
        if (fileSize == null) return "未知";
        
        double sizeInMB = fileSize / (1024.0 * 1024.0);
        if (sizeInMB < 1.0) {
            double sizeInKB = fileSize / 1024.0;
            return String.format("%.1f KB", sizeInKB);
        }
        return String.format("%.1f MB", sizeInMB);
    }

    /**
     * 获取迟交时长描述
     */
    public String getLateDescription() {
        if (!isLate || lateMinutes == null || lateMinutes <= 0) {
            return "按时提交";
        }
        
        if (lateMinutes < 60) {
            return "迟交 " + lateMinutes + " 分钟";
        } else if (lateMinutes < 1440) { // 小于24小时
            int hours = lateMinutes / 60;
            int minutes = lateMinutes % 60;
            return String.format("迟交 %d 小时 %d 分钟", hours, minutes);
        } else {
            int days = lateMinutes / 1440;
            int hours = (lateMinutes % 1440) / 60;
            return String.format("迟交 %d 天 %d 小时", days, hours);
        }
    }

    /**
     * 获取作业标题
     */
    public String getAssignmentTitle() {
        return assignment != null ? assignment.getTitle() : null;
    }

    /**
     * 获取学生姓名
     */
    public String getStudentName() {
        return student != null ? student.getRealName() : null;
    }

    /**
     * 获取批改教师姓名
     */
    public String getGraderName() {
        return grader != null ? grader.getRealName() : null;
    }

    /**
     * 计算最终得分（考虑迟交扣分）
     */
    public BigDecimal getFinalScore() {
        if (score == null) return null;
        
        if (isLate && assignment != null && assignment.getLatePenaltyPercent() != null) {
            BigDecimal penalty = score.multiply(assignment.getLatePenaltyPercent()).divide(new BigDecimal("100"));
            return score.subtract(penalty).max(BigDecimal.ZERO);
        }
        
        return score;
    }

    // ================================
    // 验证方法
    // ================================

    /**
     * 验证提交数据
     */
    @PrePersist
    @PreUpdate
    public void validateSubmission() {
        // 确保至少有内容或文件
        if ((content == null || content.trim().isEmpty()) && 
            (filePath == null || filePath.trim().isEmpty())) {
            throw new IllegalArgumentException("必须提供提交内容或上传文件");
        }
        
        // 确保成绩在有效范围内
        if (score != null && assignment != null && assignment.getMaxScore() != null) {
            if (score.compareTo(BigDecimal.ZERO) < 0 || 
                score.compareTo(new BigDecimal(assignment.getMaxScore())) > 0) {
                throw new IllegalArgumentException("成绩必须在0到" + assignment.getMaxScore() + "之间");
            }
        }
    }

    // ================================
    // toString、equals、hashCode方法
    // ================================

    @Override
    public String toString() {
        return "AssignmentSubmission{" +
                "id=" + getId() +
                ", assignmentId=" + assignmentId +
                ", studentId=" + studentId +
                ", submissionTime=" + submissionTime +
                ", submissionStatus='" + submissionStatus + '\'' +
                ", score=" + score +
                ", isLate=" + isLate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AssignmentSubmission that = (AssignmentSubmission) o;

        if (assignmentId != null ? !assignmentId.equals(that.assignmentId) : that.assignmentId != null) return false;
        return studentId != null ? studentId.equals(that.studentId) : that.studentId == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (assignmentId != null ? assignmentId.hashCode() : 0);
        result = 31 * result + (studentId != null ? studentId.hashCode() : 0);
        return result;
    }
}