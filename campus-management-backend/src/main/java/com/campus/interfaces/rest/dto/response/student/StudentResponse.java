package com.campus.interfaces.rest.dto.response.student;

import com.campus.interfaces.rest.dto.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * 学生响应DTO
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-19
 */
@Schema(description = "学生响应数据")
public class StudentResponse extends BaseResponse {

    /**
     * 关联用户ID
     */
    @Schema(description = "关联用户ID", example = "1")
    private Long userId;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    /**
     * 学号
     */
    @Schema(description = "学号", example = "2024001")
    private String studentNo;

    /**
     * 年级
     */
    @Schema(description = "年级", example = "2024级")
    private String grade;

    /**
     * 专业
     */
    @Schema(description = "专业", example = "计算机科学与技术")
    private String major;

    /**
     * 班级ID
     */
    @Schema(description = "班级ID", example = "1")
    private Long classId;

    /**
     * 班级名称
     */
    @Schema(description = "班级名称", example = "计算机2024-1班")
    private String className;

    /**
     * 入学年份
     */
    @Schema(description = "入学年份", example = "2024")
    private Integer enrollmentYear;

    /**
     * 入学日期
     */
    @Schema(description = "入学日期", example = "2024-09-01")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate enrollmentDate;

    /**
     * 毕业日期
     */
    @Schema(description = "毕业日期", example = "2028-06-30")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate graduationDate;

    /**
     * 学籍状态
     */
    @Schema(description = "学籍状态", example = "1")
    private Integer academicStatus;

    /**
     * 学籍状态文本
     */
    @Schema(description = "学籍状态文本", example = "在读")
    private String academicStatusText;

    /**
     * 学生类型
     */
    @Schema(description = "学生类型", example = "本科生")
    private String studentType;

    /**
     * 培养方式
     */
    @Schema(description = "培养方式", example = "全日制")
    private String trainingMode;

    /**
     * 学制（年）
     */
    @Schema(description = "学制", example = "4")
    private Integer academicSystem;

    /**
     * 当前学期
     */
    @Schema(description = "当前学期", example = "2024-2025-1")
    private String currentSemester;

    /**
     * 总学分
     */
    @Schema(description = "总学分", example = "160.0")
    private BigDecimal totalCredits;

    /**
     * 已获得学分
     */
    @Schema(description = "已获得学分", example = "120.0")
    private BigDecimal earnedCredits;

    /**
     * GPA
     */
    @Schema(description = "GPA", example = "3.75")
    private BigDecimal gpa;

    /**
     * 学分完成率
     */
    @Schema(description = "学分完成率", example = "75.0")
    private BigDecimal creditCompletionRate;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号", example = "110101199001011234")
    private String idCard;

    /**
     * 家庭住址
     */
    @Schema(description = "家庭住址", example = "北京市朝阳区")
    private String homeAddress;

    /**
     * 宿舍号
     */
    @Schema(description = "宿舍号", example = "A101")
    private String dormitory;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    /**
     * 手机号
     */
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /**
     * 性别
     */
    @Schema(description = "性别", example = "M")
    private String gender;

    /**
     * 性别文本
     */
    @Schema(description = "性别文本", example = "男")
    private String genderText;

    /**
     * 头像URL
     */
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    // 构造函数
    public StudentResponse() {}

    // Getter和Setter方法
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
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
        // 自动设置学籍状态文本
        switch (academicStatus != null ? academicStatus : 0) {
            case 1:
                this.academicStatusText = "在读";
                break;
            case 2:
                this.academicStatusText = "休学";
                break;
            case 3:
                this.academicStatusText = "退学";
                break;
            case 4:
                this.academicStatusText = "毕业";
                break;
            case 5:
                this.academicStatusText = "结业";
                break;
            default:
                this.academicStatusText = "未知";
                break;
        }
    }

    public String getAcademicStatusText() {
        return academicStatusText;
    }

    public void setAcademicStatusText(String academicStatusText) {
        this.academicStatusText = academicStatusText;
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
        // 自动计算学分完成率
        if (totalCredits != null && totalCredits.compareTo(BigDecimal.ZERO) > 0) {
            this.creditCompletionRate = earnedCredits.divide(totalCredits, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
    }

    public BigDecimal getGpa() {
        return gpa;
    }

    public void setGpa(BigDecimal gpa) {
        this.gpa = gpa;
    }

    public BigDecimal getCreditCompletionRate() {
        return creditCompletionRate;
    }

    public void setCreditCompletionRate(BigDecimal creditCompletionRate) {
        this.creditCompletionRate = creditCompletionRate;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getDormitory() {
        return dormitory;
    }

    public void setDormitory(String dormitory) {
        this.dormitory = dormitory;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        // 自动设置性别文本
        if ("M".equals(gender)) {
            this.genderText = "男";
        } else if ("F".equals(gender)) {
            this.genderText = "女";
        } else {
            this.genderText = "未知";
        }
    }

    public String getGenderText() {
        return genderText;
    }

    public void setGenderText(String genderText) {
        this.genderText = genderText;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String toString() {
        return "StudentResponse{" +
                "id=" + getId() +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", studentNo='" + studentNo + '\'' +
                ", grade='" + grade + '\'' +
                ", major='" + major + '\'' +
                ", classId=" + classId +
                ", className='" + className + '\'' +
                ", enrollmentYear=" + enrollmentYear +
                ", enrollmentDate=" + enrollmentDate +
                ", graduationDate=" + graduationDate +
                ", academicStatus=" + academicStatus +
                ", academicStatusText='" + academicStatusText + '\'' +
                ", studentType='" + studentType + '\'' +
                ", trainingMode='" + trainingMode + '\'' +
                ", academicSystem=" + academicSystem +
                ", currentSemester='" + currentSemester + '\'' +
                ", totalCredits=" + totalCredits +
                ", earnedCredits=" + earnedCredits +
                ", gpa=" + gpa +
                ", creditCompletionRate=" + creditCompletionRate +
                ", idCard='" + idCard + '\'' +
                ", homeAddress='" + homeAddress + '\'' +
                ", dormitory='" + dormitory + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", genderText='" + genderText + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", status=" + getStatus() +
                ", statusText='" + getStatusText() + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}
