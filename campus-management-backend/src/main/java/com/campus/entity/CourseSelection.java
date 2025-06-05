package com.campus.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * 选课实体类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Entity
@Table(name = "tb_course_selection")
public class CourseSelection {

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

    @Column(name = "semester", length = 50)
    private String semester;

    @Column(name = "selection_time")
    private LocalDateTime selectionTime;

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;

    @Column(name = "remarks", length = 500)
    private String remarks;

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
    public CourseSelection() {}

    public CourseSelection(Long studentId, Long courseId, Long scheduleId, String semester) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.scheduleId = scheduleId;
        this.semester = semester;
        this.selectionTime = LocalDateTime.now();
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

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public LocalDateTime getSelectionTime() {
        return selectionTime;
    }

    public void setSelectionTime(LocalDateTime selectionTime) {
        this.selectionTime = selectionTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
        return "CourseSelection{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", courseId=" + courseId +
                ", scheduleId=" + scheduleId +
                ", semester='" + semester + '\'' +
                ", selectionTime=" + selectionTime +
                ", status=" + status +
                '}';
    }
}
