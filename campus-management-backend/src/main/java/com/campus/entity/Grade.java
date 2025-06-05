package com.campus.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 成绩实体类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Entity
@Table(name = "tb_grade")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "学生ID不能为空")
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @NotNull(message = "课程ID不能为空")
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @NotNull(message = "课程表ID不能为空")
    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "selection_id")
    private Long selectionId;

    @Column(name = "semester", length = 50)
    private String semester;

    @DecimalMin(value = "0.0", message = "成绩不能小于0分")
    @DecimalMax(value = "100.0", message = "成绩不能大于100分")
    @Column(precision = 5, scale = 2)
    private BigDecimal score;

    @DecimalMin(value = "0.0", message = "平时成绩不能小于0分")
    @DecimalMax(value = "100.0", message = "平时成绩不能大于100分")
    @Column(name = "regular_score", precision = 5, scale = 2)
    private BigDecimal regularScore;

    @DecimalMin(value = "0.0", message = "期中成绩不能小于0分")
    @DecimalMax(value = "100.0", message = "期中成绩不能大于100分")
    @Column(name = "midterm_score", precision = 5, scale = 2)
    private BigDecimal midtermScore;

    @DecimalMin(value = "0.0", message = "期末成绩不能小于0分")
    @DecimalMax(value = "100.0", message = "期末成绩不能大于100分")
    @Column(name = "final_score", precision = 5, scale = 2)
    private BigDecimal finalScore;

    @Column(name = "grade_point", precision = 3, scale = 1)
    private BigDecimal gradePoint;

    @Size(max = 10, message = "等级长度不能超过10个字符")
    @Column(length = 10)
    private String level;

    @Column(name = "is_makeup", columnDefinition = "TINYINT DEFAULT 0")
    private Integer isMakeup = 0;

    @Column(name = "is_retake", columnDefinition = "TINYINT DEFAULT 0")
    private Integer isRetake = 0;

    @Column(name = "teacher_id")
    private Long teacherId;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Column(length = 500)
    private String remarks;

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    private Integer status = 0;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted", columnDefinition = "TINYINT DEFAULT 0")
    private Integer deleted = 0;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 构造函数
    public Grade() {}

    public Grade(Long studentId, Long courseId, Long scheduleId, String semester) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.scheduleId = scheduleId;
        this.semester = semester;
    }

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(Long selectionId) {
        this.selectionId = selectionId;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getRegularScore() {
        return regularScore;
    }

    public void setRegularScore(BigDecimal regularScore) {
        this.regularScore = regularScore;
    }

    public BigDecimal getMidtermScore() {
        return midtermScore;
    }

    public void setMidtermScore(BigDecimal midtermScore) {
        this.midtermScore = midtermScore;
    }

    public BigDecimal getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(BigDecimal finalScore) {
        this.finalScore = finalScore;
    }

    public BigDecimal getGradePoint() {
        return gradePoint;
    }

    public void setGradePoint(BigDecimal gradePoint) {
        this.gradePoint = gradePoint;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getIsMakeup() {
        return isMakeup;
    }

    public void setIsMakeup(Integer isMakeup) {
        this.isMakeup = isMakeup;
    }

    public Integer getIsRetake() {
        return isRetake;
    }

    public void setIsRetake(Integer isRetake) {
        this.isRetake = isRetake;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", courseId=" + courseId +
                ", scheduleId=" + scheduleId +
                ", semester='" + semester + '\'' +
                ", score=" + score +
                ", gradePoint=" + gradePoint +
                ", level='" + level + '\'' +
                ", status=" + status +
                '}';
    }
}
