package com.campus.domain.entity.organization;

import com.campus.domain.entity.auth.User;
import com.campus.domain.entity.infrastructure.BaseEntity;
import com.campus.domain.entity.academic.CourseSelection;
import com.campus.domain.entity.academic.Grade;
import com.campus.domain.entity.finance.PaymentRecord;
import com.campus.shared.security.EncryptionConfig.EncryptedField;
import com.campus.shared.security.EncryptionConfig.EncryptionEntityListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 学生实体类
 * 记录学生的基本信息、学籍信息等
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_student", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_student_no", columnList = "student_no"),
    @Index(name = "idx_class_id", columnList = "class_id"),
    @Index(name = "idx_grade", columnList = "grade"),
    @Index(name = "idx_enrollment_year", columnList = "enrollment_year"),
    @Index(name = "idx_status_deleted", columnList = "status,deleted")
})
@EntityListeners(EncryptionEntityListener.class)
public class Student extends BaseEntity {

    /**
     * 关联的用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    /**
     * 学号
     */
    @NotBlank(message = "学号不能为空")
    @Size(max = 20, message = "学号长度不能超过20个字符")
    @Pattern(regexp = "^[0-9A-Za-z]+$", message = "学号只能包含数字和字母")
    @Column(name = "student_no", nullable = false, unique = true, length = 20)
    private String studentNo;

    /**
     * 年级/专业
     */
    @NotBlank(message = "年级不能为空")
    @Size(max = 50, message = "年级长度不能超过50个字符")
    @Column(name = "grade", nullable = false, length = 50)
    private String grade;

    /**
     * 专业
     */
    @Size(max = 100, message = "专业长度不能超过100个字符")
    @Column(name = "major", length = 100)
    private String major;

    /**
     * 班级ID
     */
    @Column(name = "class_id")
    private Long classId;

    /**
     * 入学年份
     */
    @Column(name = "enrollment_year")
    private Integer enrollmentYear;

    /**
     * 入学日期
     */
    @Column(name = "enrollment_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate enrollmentDate;

    /**
     * 毕业日期
     */
    @Column(name = "graduation_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate graduationDate;

    /**
     * 学籍状态
     * 1: 在读, 2: 毕业, 3: 退学, 4: 休学, 5: 转学, 0: 禁用
     */
    @Column(name = "academic_status", nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer academicStatus = 1;

    /**
     * 学生类型
     * 本科生、研究生、博士生等
     */
    @Size(max = 20, message = "学生类型长度不能超过20个字符")
    @Column(name = "student_type", length = 20)
    private String studentType;

    /**
     * 培养方式
     * 全日制、非全日制
     */
    @Size(max = 20, message = "培养方式长度不能超过20个字符")
    @Column(name = "training_mode", length = 20)
    private String trainingMode;

    /**
     * 学制（年）
     */
    @Column(name = "academic_system")
    private Integer academicSystem;

    /**
     * 当前学期
     */
    @Size(max = 20, message = "当前学期长度不能超过20个字符")
    @Column(name = "current_semester", length = 20)
    private String currentSemester;

    /**
     * 总学分
     */
    @Column(name = "total_credits", precision = 5, scale = 1)
    private BigDecimal totalCredits = BigDecimal.ZERO;

    /**
     * 已获得学分
     */
    @Column(name = "earned_credits", precision = 5, scale = 1)
    private BigDecimal earnedCredits = BigDecimal.ZERO;

    /**
     * GPA
     */
    @Column(name = "gpa", precision = 3, scale = 2)
    private BigDecimal gpa = BigDecimal.ZERO;

    /**
     * 家长/监护人姓名
     */
    @Size(max = 50, message = "家长姓名长度不能超过50个字符")
    @Column(name = "parent_name", length = 50)
    private String parentName;

    /**
     * 家长/监护人电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "家长电话格式不正确")
    @EncryptedField
    @Column(name = "parent_phone", length = 255)  // 增加长度以容纳加密数据
    private String parentPhone;

    /**
     * 紧急联系人
     */
    @Size(max = 50, message = "紧急联系人长度不能超过50个字符")
    @Column(name = "emergency_contact", length = 50)
    private String emergencyContact;

    /**
     * 紧急联系人电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "紧急联系人电话格式不正确")
    @EncryptedField
    @Column(name = "emergency_phone", length = 255)  // 增加长度以容纳加密数据
    private String emergencyPhone;

    /**
     * 宿舍号
     */
    @Size(max = 50, message = "宿舍号长度不能超过50个字符")
    @Column(name = "dormitory", length = 50)
    private String dormitory;

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
     * 关联的用户信息
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    /**
     * 所属班级
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    @JsonIgnore
    private SchoolClass schoolClass;

    /**
     * 选课记录
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @JsonIgnore
    private List<CourseSelection> courseSelections;

    /**
     * 成绩记录
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @JsonIgnore
    private List<Grade> grades;

    /**
     * 缴费记录
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @JsonIgnore
    private List<PaymentRecord> paymentRecords;

    // ================================
    // 构造函数
    // ================================

    public Student() {
        super();
    }

    public Student(Long userId, String studentNo, String grade) {
        this();
        this.userId = userId;
        this.studentNo = studentNo;
        this.grade = grade;
    }

    public Student(Long userId, String studentNo, String grade, String major, Long classId, LocalDate enrollmentDate) {
        this(userId, studentNo, grade);
        this.major = major;
        this.classId = classId;
        this.enrollmentDate = enrollmentDate;
        if (enrollmentDate != null) {
            this.enrollmentYear = enrollmentDate.getYear();
        }
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 获取用户真实姓名（安全版本，避免懒加载异常）
     */
    public String getName() {
        try {
            return user != null ? user.getRealName() : "未知学生";
        } catch (Exception e) {
            return "未知学生";
        }
    }

    /**
     * 获取用户真实姓名
     */
    public String getRealName() {
        return getName();
    }

    /**
     * 获取用户邮箱（安全版本，避免懒加载异常）
     */
    public String getEmail() {
        try {
            return user != null ? user.getEmail() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取用户手机号（安全版本，避免懒加载异常）
     */
    public String getPhone() {
        try {
            return user != null ? user.getPhone() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取性别（从关联的User对象中获取）
     */
    public String getGender() {
        try {
            return user != null ? user.getGender() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取班级名称
     */
    public String getClassName() {
        try {
            return schoolClass != null ? schoolClass.getClassName() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查是否在读
     */
    public boolean isActive() {
        return academicStatus != null && academicStatus == 1;
    }

    /**
     * 检查是否已毕业
     */
    public boolean isGraduated() {
        return academicStatus != null && academicStatus == 2;
    }

    /**
     * 检查是否退学
     */
    public boolean isDroppedOut() {
        return academicStatus != null && academicStatus == 3;
    }

    /**
     * 计算在校年数
     */
    public int getYearsInSchool() {
        if (enrollmentDate == null) {
            return 0;
        }
        LocalDate now = graduationDate != null ? graduationDate : LocalDate.now();
        return now.getYear() - enrollmentDate.getYear();
    }

    /**
     * 更新学分信息
     */
    public void updateCredits(BigDecimal earnedCredits, BigDecimal gpa) {
        this.earnedCredits = earnedCredits != null ? earnedCredits : BigDecimal.ZERO;
        this.gpa = gpa != null ? gpa : BigDecimal.ZERO;
    }

    /**
     * 毕业
     */
    public void graduate() {
        this.academicStatus = 2;
        this.graduationDate = LocalDate.now();
    }

    /**
     * 退学
     */
    public void dropout() {
        this.academicStatus = 3;
        this.disable();
    }

    /**
     * 休学
     */
    public void suspend() {
        this.academicStatus = 4;
    }

    /**
     * 复学
     */
    public void resume() {
        this.academicStatus = 1;
        this.enable();
    }

    /**
     * 转学
     */
    public void transfer() {
        this.academicStatus = 5;
        this.disable();
    }

    /**
     * 获取学籍状态文本
     */
    public String getAcademicStatusText() {
        if (academicStatus == null) return "未知";
        return switch (academicStatus) {
            case 1 -> "在读";
            case 2 -> "毕业";
            case 3 -> "退学";
            case 4 -> "休学";
            case 5 -> "转学";
            default -> "未知";
        };
    }

    // ================================
    // Getter/Setter 方法 (手动添加以解决Lombok问题)
    // ================================

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

    /**
     * 获取学号（兼容性方法）
     */
    public String getStudentNumber() {
        return studentNo;
    }

    /**
     * 设置学号（兼容性方法）
     */
    public void setStudentNumber(String studentNumber) {
        this.studentNo = studentNumber;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Integer getEnrollmentYear() {
        return enrollmentYear;
    }

    public void setEnrollmentYear(Integer enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
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

    public Integer getAcademicStatus() {
        return academicStatus;
    }

    public void setAcademicStatus(Integer academicStatus) {
        this.academicStatus = academicStatus;
    }

    public String getStudentType() {
        return studentType;
    }

    public void setStudentType(String studentType) {
        this.studentType = studentType;
    }

    public String getTrainingMode() {
        return trainingMode;
    }

    public void setTrainingMode(String trainingMode) {
        this.trainingMode = trainingMode;
    }

    public Integer getAcademicSystem() {
        return academicSystem;
    }

    public void setAcademicSystem(Integer academicSystem) {
        this.academicSystem = academicSystem;
    }

    public String getCurrentSemester() {
        return currentSemester;
    }

    public void setCurrentSemester(String currentSemester) {
        this.currentSemester = currentSemester;
    }

    public BigDecimal getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(BigDecimal totalCredits) {
        this.totalCredits = totalCredits;
    }

    public BigDecimal getEarnedCredits() {
        return earnedCredits;
    }

    public void setEarnedCredits(BigDecimal earnedCredits) {
        this.earnedCredits = earnedCredits;
    }

    public BigDecimal getGpa() {
        return gpa;
    }

    public void setGpa(BigDecimal gpa) {
        this.gpa = gpa;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getDormitory() {
        return dormitory;
    }

    public void setDormitory(String dormitory) {
        this.dormitory = dormitory;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public List<CourseSelection> getCourseSelections() {
        return courseSelections;
    }

    public void setCourseSelections(List<CourseSelection> courseSelections) {
        this.courseSelections = courseSelections;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    public List<PaymentRecord> getPaymentRecords() {
        return paymentRecords;
    }

    public void setPaymentRecords(List<PaymentRecord> paymentRecords) {
        this.paymentRecords = paymentRecords;
    }
}
