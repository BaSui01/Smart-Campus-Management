package com.campus.interfaces.rest.dto.response.course;

import com.campus.interfaces.rest.dto.common.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 课程响应DTO
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-19
 */
@Schema(description = "课程响应数据")
public class CourseResponse extends BaseResponse {

    /**
     * 课程代码
     */
    @Schema(description = "课程代码", example = "CS101")
    private String courseCode;

    /**
     * 课程名称
     */
    @Schema(description = "课程名称", example = "计算机科学导论")
    private String courseName;

    /**
     * 课程英文名称
     */
    @Schema(description = "课程英文名称", example = "Introduction to Computer Science")
    private String courseNameEn;

    /**
     * 课程类型
     */
    @Schema(description = "课程类型", example = "必修课")
    private String courseType;

    /**
     * 课程性质
     */
    @Schema(description = "课程性质", example = "理论课")
    private String courseNature;

    /**
     * 学分
     */
    @Schema(description = "学分", example = "3.0")
    private BigDecimal credits;

    /**
     * 总学时
     */
    @Schema(description = "总学时", example = "48")
    private Integer totalHours;

    /**
     * 理论学时
     */
    @Schema(description = "理论学时", example = "32")
    private Integer theoryHours;

    /**
     * 实践学时
     */
    @Schema(description = "实践学时", example = "16")
    private Integer practiceHours;

    /**
     * 实验学时
     */
    @Schema(description = "实验学时", example = "0")
    private Integer labHours;

    /**
     * 开课学期
     */
    @Schema(description = "开课学期", example = "2024-2025-1")
    private String semester;

    /**
     * 适用年级
     */
    @Schema(description = "适用年级", example = "2024级")
    private String applicableGrade;

    /**
     * 适用专业
     */
    @Schema(description = "适用专业", example = "计算机科学与技术")
    private String applicableMajor;

    /**
     * 开课部门ID
     */
    @Schema(description = "开课部门ID", example = "1")
    private Long departmentId;

    /**
     * 开课部门名称
     */
    @Schema(description = "开课部门名称", example = "计算机学院")
    private String departmentName;

    /**
     * 主讲教师ID
     */
    @Schema(description = "主讲教师ID", example = "1")
    private Long teacherId;

    /**
     * 主讲教师姓名
     */
    @Schema(description = "主讲教师姓名", example = "张教授")
    private String teacherName;

    /**
     * 最大选课人数
     */
    @Schema(description = "最大选课人数", example = "50")
    private Integer maxStudents;

    /**
     * 最小开课人数
     */
    @Schema(description = "最小开课人数", example = "10")
    private Integer minStudents;

    /**
     * 当前选课人数
     */
    @Schema(description = "当前选课人数", example = "35")
    private Integer currentStudents;

    /**
     * 选课状态
     */
    @Schema(description = "选课状态", example = "1")
    private Integer selectionStatus;

    /**
     * 选课状态文本
     */
    @Schema(description = "选课状态文本", example = "开放选课")
    private String selectionStatusText;

    /**
     * 课程描述
     */
    @Schema(description = "课程描述", example = "本课程介绍计算机科学的基本概念和原理")
    private String description;

    /**
     * 课程目标
     */
    @Schema(description = "课程目标", example = "掌握计算机科学基础知识")
    private String objectives;

    /**
     * 先修课程
     */
    @Schema(description = "先修课程", example = "高等数学,线性代数")
    private String prerequisites;

    /**
     * 教材信息
     */
    @Schema(description = "教材信息", example = "《计算机科学导论》第3版")
    private String textbook;

    /**
     * 考核方式
     */
    @Schema(description = "考核方式", example = "期末考试70%+平时成绩30%")
    private String assessmentMethod;

    /**
     * 是否允许重修
     */
    @Schema(description = "是否允许重修", example = "true")
    private Boolean allowRetake;

    /**
     * 是否允许免修
     */
    @Schema(description = "是否允许免修", example = "false")
    private Boolean allowExemption;

    /**
     * 课程评分
     */
    @Schema(description = "课程评分", example = "4.5")
    private BigDecimal rating;

    /**
     * 评价人数
     */
    @Schema(description = "评价人数", example = "120")
    private Integer ratingCount;

    // 构造函数
    public CourseResponse() {}

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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
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

    public Integer getCurrentStudents() {
        return currentStudents;
    }

    public void setCurrentStudents(Integer currentStudents) {
        this.currentStudents = currentStudents;
    }

    public Integer getSelectionStatus() {
        return selectionStatus;
    }

    public void setSelectionStatus(Integer selectionStatus) {
        this.selectionStatus = selectionStatus;
        // 自动设置选课状态文本
        switch (selectionStatus != null ? selectionStatus : 0) {
            case 1:
                this.selectionStatusText = "开放选课";
                break;
            case 2:
                this.selectionStatusText = "选课已满";
                break;
            case 3:
                this.selectionStatusText = "选课关闭";
                break;
            case 4:
                this.selectionStatusText = "课程停开";
                break;
            default:
                this.selectionStatusText = "未知状态";
                break;
        }
    }

    public String getSelectionStatusText() {
        return selectionStatusText;
    }

    public void setSelectionStatusText(String selectionStatusText) {
        this.selectionStatusText = selectionStatusText;
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

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    /**
     * 获取选课进度百分比
     */
    public BigDecimal getSelectionProgress() {
        if (maxStudents == null || maxStudents == 0 || currentStudents == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(currentStudents)
                .divide(new BigDecimal(maxStudents), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * 是否可以选课
     */
    public Boolean canSelect() {
        return selectionStatus != null && selectionStatus == 1 && 
               currentStudents != null && maxStudents != null && 
               currentStudents < maxStudents;
    }

    @Override
    public String toString() {
        return "CourseResponse{" +
                "id=" + getId() +
                ", courseCode='" + courseCode + '\'' +
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
                ", departmentName='" + departmentName + '\'' +
                ", teacherId=" + teacherId +
                ", teacherName='" + teacherName + '\'' +
                ", maxStudents=" + maxStudents +
                ", minStudents=" + minStudents +
                ", currentStudents=" + currentStudents +
                ", selectionStatus=" + selectionStatus +
                ", selectionStatusText='" + selectionStatusText + '\'' +
                ", description='" + description + '\'' +
                ", objectives='" + objectives + '\'' +
                ", prerequisites='" + prerequisites + '\'' +
                ", textbook='" + textbook + '\'' +
                ", assessmentMethod='" + assessmentMethod + '\'' +
                ", allowRetake=" + allowRetake +
                ", allowExemption=" + allowExemption +
                ", rating=" + rating +
                ", ratingCount=" + ratingCount +
                ", status=" + getStatus() +
                ", statusText='" + getStatusText() + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}
