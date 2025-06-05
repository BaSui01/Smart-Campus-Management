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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 班级实体类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Entity
@Table(name = "tb_class")
public class SchoolClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "班级名称不能为空")
    @Size(max = 50, message = "班级名称长度不能超过50个字符")
    @Column(name = "class_name", nullable = false, length = 50)
    private String className;

    @NotBlank(message = "班级代码不能为空")
    @Size(max = 20, message = "班级代码长度不能超过20个字符")
    @Column(name = "class_code", nullable = false, unique = true, length = 20)
    private String classCode;

    @NotBlank(message = "年级不能为空")
    @Size(max = 20, message = "年级长度不能超过20个字符")
    @Column(nullable = false, length = 20)
    private String grade;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "head_teacher_id")
    private Long headTeacherId;

    @Size(max = 500, message = "描述长度不能超过500个字符")
    @Column(length = 500)
    private String description;

    @Column(name = "student_count")
    private Integer studentCount = 0;

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;

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
    public SchoolClass() {}

    public SchoolClass(String className, String classCode, String grade) {
        this.className = className;
        this.classCode = classCode;
        this.grade = grade;
    }

    public SchoolClass(String className, String classCode, String grade, Long departmentId, Long headTeacherId) {
        this.className = className;
        this.classCode = classCode;
        this.grade = grade;
        this.departmentId = departmentId;
        this.headTeacherId = headTeacherId;
    }

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getHeadTeacherId() {
        return headTeacherId;
    }

    public void setHeadTeacherId(Long headTeacherId) {
        this.headTeacherId = headTeacherId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
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
        return "SchoolClass{" +
                "id=" + id +
                ", className='" + className + '\'' +
                ", classCode='" + classCode + '\'' +
                ", grade='" + grade + '\'' +
                ", departmentId=" + departmentId +
                ", headTeacherId=" + headTeacherId +
                ", studentCount=" + studentCount +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
