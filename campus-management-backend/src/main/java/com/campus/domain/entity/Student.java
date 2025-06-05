package com.campus.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 学生实体类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Entity
@Table(name = "tb_student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @NotBlank(message = "学号不能为空")
    @Size(max = 20, message = "学号长度不能超过20个字符")
    @Column(name = "student_no", nullable = false, unique = true, length = 20)
    private String studentNo;

    @NotBlank(message = "年级不能为空")
    @Size(max = 20, message = "年级长度不能超过20个字符")
    @Column(nullable = false, length = 20)
    private String grade;

    @Column(name = "class_id")
    private Long classId;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "graduation_date")
    private LocalDate graduationDate;

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Column(length = 500)
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

    // 关联关系
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private SchoolClass schoolClass;

    // private List<CourseSelection> courseSelections;
    // private List<Grade> grades;
    // private List<PaymentRecord> paymentRecords;
    // private List<StudentParent> studentParents;

    // 构造函数
    public Student() {}

    public Student(Long userId, String studentNo, String grade) {
        this.userId = userId;
        this.studentNo = studentNo;
        this.grade = grade;
    }

    public Student(Long userId, String studentNo, String grade, Long classId, LocalDate enrollmentDate) {
        this.userId = userId;
        this.studentNo = studentNo;
        this.grade = grade;
        this.classId = classId;
        this.enrollmentDate = enrollmentDate;
    }

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public LocalDate getGraduationDate() {
        return graduationDate;
    }

    public void setGraduationDate(LocalDate graduationDate) {
        this.graduationDate = graduationDate;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SchoolClass getSchoolClass() {
        return schoolClass;
    }

    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }

    // 便利方法 - 获取用户真实姓名（安全版本，避免懒加载异常）
    public String getName() {
        try {
            return user != null ? user.getRealName() : "未知学生";
        } catch (Exception e) {
            return "未知学生";
        }
    }

    public String getRealName() {
        try {
            return user != null ? user.getRealName() : "未知学生";
        } catch (Exception e) {
            return "未知学生";
        }
    }

    // 便利方法 - 获取用户邮箱（安全版本，避免懒加载异常）
    public String getEmail() {
        try {
            return user != null ? user.getEmail() : null;
        } catch (Exception e) {
            return null;
        }
    }

    // 便利方法 - 获取用户手机号（安全版本，避免懒加载异常）
    public String getPhone() {
        try {
            return user != null ? user.getPhone() : null;
        } catch (Exception e) {
            return null;
        }
    }

    // 便利方法 - 获取性别（从关联的User对象中获取）
    public String getGender() {
        try {
            return user != null ? user.getGender() : null;
        } catch (Exception e) {
            return null;
        }
    }

    // 便利方法 - 获取年级作为专业信息
    public String getMajor() {
        return this.grade;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", userId=" + userId +
                ", studentNo='" + studentNo + '\'' +
                ", grade='" + grade + '\'' +
                ", classId=" + classId +
                ", enrollmentDate=" + enrollmentDate +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
