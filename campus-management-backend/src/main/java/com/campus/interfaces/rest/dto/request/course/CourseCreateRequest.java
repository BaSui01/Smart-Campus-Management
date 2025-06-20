package com.campus.interfaces.rest.dto.request.course;

import com.campus.interfaces.rest.dto.common.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 课程创建请求DTO
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-19
 */
@Schema(description = "课程创建请求")
public class CourseCreateRequest extends BaseRequest {

    /**
     * 课程代码
     */
    @Schema(description = "课程代码", example = "CS101", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "课程代码不能为空")
    @Size(min = 3, max = 20, message = "课程代码长度必须在3-20个字符之间")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "课程代码只能包含大写字母和数字")
    private String courseCode;

    /**
     * 课程名称
     */
    @Schema(description = "课程名称", example = "计算机科学导论", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "课程名称不能为空")
    @Size(min = 2, max = 100, message = "课程名称长度必须在2-100个字符之间")
    private String courseName;

    /**
     * 课程英文名称
     */
    @Schema(description = "课程英文名称", example = "Introduction to Computer Science")
    @Size(max = 200, message = "课程英文名称长度不能超过200个字符")
    private String courseNameEn;

    /**
     * 课程类型
     */
    @Schema(description = "课程类型", example = "必修课", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "课程类型不能为空")
    @Size(max = 20, message = "课程类型长度不能超过20个字符")
    private String courseType;

    /**
     * 课程性质
     */
    @Schema(description = "课程性质", example = "理论课")
    @Size(max = 20, message = "课程性质长度不能超过20个字符")
    private String courseNature;

    /**
     * 学分
     */
    @Schema(description = "学分", example = "3.0", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "学分不能为空")
    @DecimalMin(value = "0.5", message = "学分不能少于0.5")
    @DecimalMax(value = "10.0", message = "学分不能超过10.0")
    private BigDecimal credits;

    /**
     * 总学时
     */
    @Schema(description = "总学时", example = "48", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "总学时不能为空")
    @Min(value = 1, message = "总学时必须大于0")
    @Max(value = 500, message = "总学时不能超过500")
    private Integer totalHours;

    /**
     * 理论学时
     */
    @Schema(description = "理论学时", example = "32")
    @Min(value = 0, message = "理论学时不能为负数")
    private Integer theoryHours = 0;

    /**
     * 实践学时
     */
    @Schema(description = "实践学时", example = "16")
    @Min(value = 0, message = "实践学时不能为负数")
    private Integer practiceHours = 0;

    /**
     * 实验学时
     */
    @Schema(description = "实验学时", example = "0")
    @Min(value = 0, message = "实验学时不能为负数")
    private Integer labHours = 0;

    /**
     * 开课学期
     */
    @Schema(description = "开课学期", example = "2024-2025-1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "开课学期不能为空")
    @Size(max = 20, message = "开课学期长度不能超过20个字符")
    private String semester;

    /**
     * 适用年级
     */
    @Schema(description = "适用年级", example = "2024级")
    @Size(max = 50, message = "适用年级长度不能超过50个字符")
    private String applicableGrade;

    /**
     * 适用专业
     */
    @Schema(description = "适用专业", example = "计算机科学与技术")
    @Size(max = 200, message = "适用专业长度不能超过200个字符")
    private String applicableMajor;

    /**
     * 开课部门ID
     */
    @Schema(description = "开课部门ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "开课部门ID不能为空")
    @Min(value = 1, message = "开课部门ID必须大于0")
    private Long departmentId;

    /**
     * 主讲教师ID
     */
    @Schema(description = "主讲教师ID", example = "1")
    @Min(value = 1, message = "主讲教师ID必须大于0")
    private Long teacherId;

    /**
     * 最大选课人数
     */
    @Schema(description = "最大选课人数", example = "50")
    @Min(value = 1, message = "最大选课人数必须大于0")
    @Max(value = 500, message = "最大选课人数不能超过500")
    private Integer maxStudents;

    /**
     * 最小开课人数
     */
    @Schema(description = "最小开课人数", example = "10")
    @Min(value = 1, message = "最小开课人数必须大于0")
    private Integer minStudents;

    /**
     * 课程描述
     */
    @Schema(description = "课程描述", example = "本课程介绍计算机科学的基本概念和原理")
    @Size(max = 1000, message = "课程描述长度不能超过1000个字符")
    private String description;

    /**
     * 课程目标
     */
    @Schema(description = "课程目标", example = "掌握计算机科学基础知识")
    @Size(max = 1000, message = "课程目标长度不能超过1000个字符")
    private String objectives;

    /**
     * 先修课程
     */
    @Schema(description = "先修课程", example = "高等数学,线性代数")
    @Size(max = 200, message = "先修课程长度不能超过200个字符")
    private String prerequisites;

    /**
     * 教材信息
     */
    @Schema(description = "教材信息", example = "《计算机科学导论》第3版")
    @Size(max = 500, message = "教材信息长度不能超过500个字符")
    private String textbook;

    /**
     * 考核方式
     */
    @Schema(description = "考核方式", example = "期末考试70%+平时成绩30%")
    @Size(max = 200, message = "考核方式长度不能超过200个字符")
    private String assessmentMethod;

    /**
     * 是否允许重修
     */
    @Schema(description = "是否允许重修", example = "true")
    private Boolean allowRetake = true;

    /**
     * 是否允许免修
     */
    @Schema(description = "是否允许免修", example = "false")
    private Boolean allowExemption = false;

    // 构造函数
    public CourseCreateRequest() {}

    // Getter和Setter方法
    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseNameEn() {
        return courseNameEn;
    }

    public void setCourseNameEn(String courseNameEn) {
        this.courseNameEn = courseNameEn;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getCourseNature() {
        return courseNature;
    }

    public void setCourseNature(String courseNature) {
        this.courseNature = courseNature;
    }

    public BigDecimal getCredits() {
        return credits;
    }

    public void setCredits(BigDecimal credits) {
        this.credits = credits;
    }

    public Integer getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Integer totalHours) {
        this.totalHours = totalHours;
    }

    public Integer getTheoryHours() {
        return theoryHours;
    }

    public void setTheoryHours(Integer theoryHours) {
        this.theoryHours = theoryHours;
    }

    public Integer getPracticeHours() {
        return practiceHours;
    }

    public void setPracticeHours(Integer practiceHours) {
        this.practiceHours = practiceHours;
    }

    public Integer getLabHours() {
        return labHours;
    }

    public void setLabHours(Integer labHours) {
        this.labHours = labHours;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getApplicableGrade() {
        return applicableGrade;
    }

    public void setApplicableGrade(String applicableGrade) {
        this.applicableGrade = applicableGrade;
    }

    public String getApplicableMajor() {
        return applicableMajor;
    }

    public void setApplicableMajor(String applicableMajor) {
        this.applicableMajor = applicableMajor;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Integer getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }

    public Integer getMinStudents() {
        return minStudents;
    }

    public void setMinStudents(Integer minStudents) {
        this.minStudents = minStudents;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    public String getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }

    public String getTextbook() {
        return textbook;
    }

    public void setTextbook(String textbook) {
        this.textbook = textbook;
    }

    public String getAssessmentMethod() {
        return assessmentMethod;
    }

    public void setAssessmentMethod(String assessmentMethod) {
        this.assessmentMethod = assessmentMethod;
    }

    public Boolean getAllowRetake() {
        return allowRetake;
    }

    public void setAllowRetake(Boolean allowRetake) {
        this.allowRetake = allowRetake;
    }

    public Boolean getAllowExemption() {
        return allowExemption;
    }

    public void setAllowExemption(Boolean allowExemption) {
        this.allowExemption = allowExemption;
    }

    /**
     * 验证学时分配是否合理
     */
    public boolean isHoursValid() {
        if (totalHours == null) return false;
        int calculatedHours = (theoryHours != null ? theoryHours : 0) + 
                             (practiceHours != null ? practiceHours : 0) + 
                             (labHours != null ? labHours : 0);
        return calculatedHours <= totalHours;
    }

    @Override
    public String toString() {
        return "CourseCreateRequest{" +
                "courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseNameEn='" + courseNameEn + '\'' +
                ", courseType='" + courseType + '\'' +
                ", courseNature='" + courseNature + '\'' +
                ", credits=" + credits +
                ", totalHours=" + totalHours +
                ", theoryHours=" + theoryHours +
                ", practiceHours=" + practiceHours +
                ", labHours=" + labHours +
                ", semester='" + semester + '\'' +
                ", applicableGrade='" + applicableGrade + '\'' +
                ", applicableMajor='" + applicableMajor + '\'' +
                ", departmentId=" + departmentId +
                ", teacherId=" + teacherId +
                ", maxStudents=" + maxStudents +
                ", minStudents=" + minStudents +
                ", description='" + description + '\'' +
                ", objectives='" + objectives + '\'' +
                ", prerequisites='" + prerequisites + '\'' +
                ", textbook='" + textbook + '\'' +
                ", assessmentMethod='" + assessmentMethod + '\'' +
                ", allowRetake=" + allowRetake +
                ", allowExemption=" + allowExemption +
                ", status=" + getStatus() +
                ", remarks='" + getRemarks() + '\'' +
                '}';
    }
}
