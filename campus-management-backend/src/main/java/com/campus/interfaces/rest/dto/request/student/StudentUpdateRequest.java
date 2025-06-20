package com.campus.interfaces.rest.dto.request.student;

import com.campus.interfaces.rest.dto.common.BaseRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 学生更新请求DTO
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-19
 */
@Schema(description = "学生更新请求")
public class StudentUpdateRequest extends BaseRequest {

    /**
     * 学生ID
     */
    @Schema(description = "学生ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "学生ID不能为空")
    @Min(value = 1, message = "学生ID必须大于0")
    private Long id;

    /**
     * 年级
     */
    @Schema(description = "年级", example = "2024级")
    @Size(max = 20, message = "年级长度不能超过20个字符")
    private String grade;

    /**
     * 专业
     */
    @Schema(description = "专业", example = "计算机科学与技术")
    @Size(max = 100, message = "专业长度不能超过100个字符")
    private String major;

    /**
     * 班级ID
     */
    @Schema(description = "班级ID", example = "1")
    @Min(value = 1, message = "班级ID必须大于0")
    private Long classId;

    /**
     * 毕业日期
     */
    @Schema(description = "毕业日期", example = "2028-06-30")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate graduationDate;

    /**
     * 学籍状态
     */
    @Schema(description = "学籍状态", example = "1", allowableValues = {"1", "2", "3", "4", "5"})
    @Min(value = 1, message = "学籍状态必须大于0")
    @Max(value = 5, message = "学籍状态不能超过5")
    private Integer academicStatus;

    /**
     * 学生类型
     */
    @Schema(description = "学生类型", example = "本科生")
    @Size(max = 20, message = "学生类型长度不能超过20个字符")
    private String studentType;

    /**
     * 培养方式
     */
    @Schema(description = "培养方式", example = "全日制")
    @Size(max = 20, message = "培养方式长度不能超过20个字符")
    private String trainingMode;

    /**
     * 当前学期
     */
    @Schema(description = "当前学期", example = "2024-2025-1")
    @Size(max = 20, message = "当前学期长度不能超过20个字符")
    private String currentSemester;

    /**
     * 总学分
     */
    @Schema(description = "总学分", example = "160.0")
    @DecimalMin(value = "0.0", message = "总学分不能为负数")
    @DecimalMax(value = "999.9", message = "总学分不能超过999.9")
    private BigDecimal totalCredits;

    /**
     * 已获得学分
     */
    @Schema(description = "已获得学分", example = "120.0")
    @DecimalMin(value = "0.0", message = "已获得学分不能为负数")
    private BigDecimal earnedCredits;

    /**
     * GPA
     */
    @Schema(description = "GPA", example = "3.75")
    @DecimalMin(value = "0.00", message = "GPA不能为负数")
    @DecimalMax(value = "4.00", message = "GPA不能超过4.00")
    private BigDecimal gpa;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号", example = "110101199001011234")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", 
             message = "身份证号格式不正确")
    private String idCard;

    /**
     * 家庭住址
     */
    @Schema(description = "家庭住址", example = "北京市朝阳区")
    @Size(max = 200, message = "家庭住址长度不能超过200个字符")
    private String homeAddress;

    /**
     * 宿舍号
     */
    @Schema(description = "宿舍号", example = "A101")
    @Size(max = 50, message = "宿舍号长度不能超过50个字符")
    private String dormitory;

    // 构造函数
    public StudentUpdateRequest() {}

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "StudentUpdateRequest{" +
                "id=" + id +
                ", grade='" + grade + '\'' +
                ", major='" + major + '\'' +
                ", classId=" + classId +
                ", graduationDate=" + graduationDate +
                ", academicStatus=" + academicStatus +
                ", studentType='" + studentType + '\'' +
                ", trainingMode='" + trainingMode + '\'' +
                ", currentSemester='" + currentSemester + '\'' +
                ", totalCredits=" + totalCredits +
                ", earnedCredits=" + earnedCredits +
                ", gpa=" + gpa +
                ", idCard='" + idCard + '\'' +
                ", homeAddress='" + homeAddress + '\'' +
                ", dormitory='" + dormitory + '\'' +
                ", status=" + getStatus() +
                ", remarks='" + getRemarks() + '\'' +
                '}';
    }
}
