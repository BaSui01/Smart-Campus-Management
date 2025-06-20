package com.campus.domain.entity.organization;

import com.campus.domain.entity.infrastructure.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 学生学术信息实体类
 * 存储学生的学术相关信息，与Student主表分离以优化性能
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Entity
@Table(name = "tb_student_academic", indexes = {
    @Index(name = "idx_student_id", columnList = "student_id"),
    @Index(name = "idx_academic_status", columnList = "academic_status"),
    @Index(name = "idx_gpa", columnList = "gpa"),
    @Index(name = "idx_graduation_date", columnList = "graduation_date")
})
public class StudentAcademic extends BaseEntity {

    /**
     * 关联的学生ID
     */
    @Column(name = "student_id", nullable = false, unique = true)
    private Long studentId;

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
     * 预计毕业日期
     */
    @Column(name = "expected_graduation_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedGraduationDate;

    /**
     * 学位类型
     */
    @Size(max = 20, message = "学位类型长度不能超过20个字符")
    @Column(name = "degree_type", length = 20)
    private String degreeType;

    /**
     * 论文题目
     */
    @Size(max = 200, message = "论文题目长度不能超过200个字符")
    @Column(name = "thesis_title", length = 200)
    private String thesisTitle;

    /**
     * 导师ID
     */
    @Column(name = "supervisor_id")
    private Long supervisorId;

    /**
     * 研究方向
     */
    @Size(max = 100, message = "研究方向长度不能超过100个字符")
    @Column(name = "research_direction", length = 100)
    private String researchDirection;

    /**
     * 学术成果
     */
    @Size(max = 1000, message = "学术成果长度不能超过1000个字符")
    @Column(name = "academic_achievements", length = 1000)
    private String academicAchievements;

    /**
     * 实习经历
     */
    @Size(max = 1000, message = "实习经历长度不能超过1000个字符")
    @Column(name = "internship_experience", length = 1000)
    private String internshipExperience;

    /**
     * 社会实践
     */
    @Size(max = 1000, message = "社会实践长度不能超过1000个字符")
    @Column(name = "social_practice", length = 1000)
    private String socialPractice;

    /**
     * 获奖情况
     */
    @Size(max = 1000, message = "获奖情况长度不能超过1000个字符")
    @Column(name = "awards", length = 1000)
    private String awards;

    /**
     * 学术备注
     */
    @Size(max = 500, message = "学术备注长度不能超过500个字符")
    @Column(name = "academic_remarks", length = 500)
    private String academicRemarks;

    // ================================
    // 关联关系
    // ================================

    /**
     * 关联的学生信息
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    // ================================
    // 构造函数
    // ================================

    public StudentAcademic() {
        super();
    }

    public StudentAcademic(Long studentId) {
        this();
        this.studentId = studentId;
    }

    // ================================
    // 业务方法
    // ================================

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
    }

    /**
     * 转学
     */
    public void transfer() {
        this.academicStatus = 5;
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
    // Getter/Setter 方法
    // ================================

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
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

    public LocalDate getExpectedGraduationDate() {
        return expectedGraduationDate;
    }

    public void setExpectedGraduationDate(LocalDate expectedGraduationDate) {
        this.expectedGraduationDate = expectedGraduationDate;
    }

    public String getDegreeType() {
        return degreeType;
    }

    public void setDegreeType(String degreeType) {
        this.degreeType = degreeType;
    }

    public String getThesisTitle() {
        return thesisTitle;
    }

    public void setThesisTitle(String thesisTitle) {
        this.thesisTitle = thesisTitle;
    }

    public Long getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(Long supervisorId) {
        this.supervisorId = supervisorId;
    }

    public String getResearchDirection() {
        return researchDirection;
    }

    public void setResearchDirection(String researchDirection) {
        this.researchDirection = researchDirection;
    }

    public String getAcademicAchievements() {
        return academicAchievements;
    }

    public void setAcademicAchievements(String academicAchievements) {
        this.academicAchievements = academicAchievements;
    }

    public String getInternshipExperience() {
        return internshipExperience;
    }

    public void setInternshipExperience(String internshipExperience) {
        this.internshipExperience = internshipExperience;
    }

    public String getSocialPractice() {
        return socialPractice;
    }

    public void setSocialPractice(String socialPractice) {
        this.socialPractice = socialPractice;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public String getAcademicRemarks() {
        return academicRemarks;
    }

    public void setAcademicRemarks(String academicRemarks) {
        this.academicRemarks = academicRemarks;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
