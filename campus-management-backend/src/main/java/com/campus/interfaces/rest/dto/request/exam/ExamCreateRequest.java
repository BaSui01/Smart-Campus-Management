package com.campus.interfaces.rest.dto.request.exam;

import com.campus.interfaces.rest.dto.common.BaseRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * 考试创建请求DTO
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-19
 */
@Schema(description = "考试创建请求")
public class ExamCreateRequest extends BaseRequest {

    /**
     * 考试名称
     */
    @Schema(description = "考试名称", example = "计算机科学导论期末考试", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "考试名称不能为空")
    @Size(min = 2, max = 100, message = "考试名称长度必须在2-100个字符之间")
    private String examName;

    /**
     * 关联课程ID
     */
    @Schema(description = "关联课程ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "关联课程ID不能为空")
    @Min(value = 1, message = "关联课程ID必须大于0")
    private Long courseId;

    /**
     * 考试类型
     */
    @Schema(description = "考试类型", example = "期末考试", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "考试类型不能为空")
    @Size(max = 20, message = "考试类型长度不能超过20个字符")
    private String examType;

    /**
     * 考试形式
     */
    @Schema(description = "考试形式", example = "闭卷", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "考试形式不能为空")
    @Size(max = 20, message = "考试形式长度不能超过20个字符")
    private String examFormat;

    /**
     * 考试开始时间
     */
    @Schema(description = "考试开始时间", example = "2025-01-15 09:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "考试开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future(message = "考试开始时间必须是未来时间")
    private LocalDateTime startTime;

    /**
     * 考试结束时间
     */
    @Schema(description = "考试结束时间", example = "2025-01-15 11:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "考试结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future(message = "考试结束时间必须是未来时间")
    private LocalDateTime endTime;

    /**
     * 考试时长（分钟）
     */
    @Schema(description = "考试时长", example = "120", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "考试时长不能为空")
    @Min(value = 30, message = "考试时长不能少于30分钟")
    @Max(value = 480, message = "考试时长不能超过480分钟")
    private Integer duration;

    /**
     * 考试地点
     */
    @Schema(description = "考试地点", example = "教学楼A101", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "考试地点不能为空")
    @Size(max = 100, message = "考试地点长度不能超过100个字符")
    private String location;

    /**
     * 教室ID
     */
    @Schema(description = "教室ID", example = "1")
    @Min(value = 1, message = "教室ID必须大于0")
    private Long classroomId;

    /**
     * 监考教师ID
     */
    @Schema(description = "监考教师ID", example = "1")
    @Min(value = 1, message = "监考教师ID必须大于0")
    private Long proctorId;

    /**
     * 总分
     */
    @Schema(description = "总分", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "总分不能为空")
    @Min(value = 1, message = "总分必须大于0")
    @Max(value = 1000, message = "总分不能超过1000")
    private Integer totalScore;

    /**
     * 及格分数
     */
    @Schema(description = "及格分数", example = "60")
    @Min(value = 0, message = "及格分数不能为负数")
    private Integer passingScore = 60;

    /**
     * 考试说明
     */
    @Schema(description = "考试说明", example = "请携带学生证和身份证参加考试")
    @Size(max = 500, message = "考试说明长度不能超过500个字符")
    private String instructions;

    /**
     * 考试要求
     */
    @Schema(description = "考试要求", example = "禁止携带手机等电子设备")
    @Size(max = 500, message = "考试要求长度不能超过500个字符")
    private String requirements;

    /**
     * 是否允许迟到
     */
    @Schema(description = "是否允许迟到", example = "false")
    private Boolean allowLateEntry = false;

    /**
     * 迟到限制时间（分钟）
     */
    @Schema(description = "迟到限制时间", example = "15")
    @Min(value = 0, message = "迟到限制时间不能为负数")
    @Max(value = 60, message = "迟到限制时间不能超过60分钟")
    private Integer lateEntryLimit = 0;

    /**
     * 是否允许提前交卷
     */
    @Schema(description = "是否允许提前交卷", example = "true")
    private Boolean allowEarlySubmission = true;

    /**
     * 提前交卷限制时间（分钟）
     */
    @Schema(description = "提前交卷限制时间", example = "30")
    @Min(value = 0, message = "提前交卷限制时间不能为负数")
    private Integer earlySubmissionLimit = 30;

    /**
     * 考试状态
     */
    @Schema(description = "考试状态", example = "1", allowableValues = {"1", "2", "3", "4"})
    @Min(value = 1, message = "考试状态必须大于0")
    @Max(value = 4, message = "考试状态不能超过4")
    private Integer examStatus = 1;

    // 构造函数
    public ExamCreateRequest() {}

    // Getter和Setter方法
    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public String getExamFormat() {
        return examFormat;
    }

    public void setExamFormat(String examFormat) {
        this.examFormat = examFormat;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Long classroomId) {
        this.classroomId = classroomId;
    }

    public Long getProctorId() {
        return proctorId;
    }

    public void setProctorId(Long proctorId) {
        this.proctorId = proctorId;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(Integer passingScore) {
        this.passingScore = passingScore;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public Boolean getAllowLateEntry() {
        return allowLateEntry;
    }

    public void setAllowLateEntry(Boolean allowLateEntry) {
        this.allowLateEntry = allowLateEntry;
    }

    public Integer getLateEntryLimit() {
        return lateEntryLimit;
    }

    public void setLateEntryLimit(Integer lateEntryLimit) {
        this.lateEntryLimit = lateEntryLimit;
    }

    public Boolean getAllowEarlySubmission() {
        return allowEarlySubmission;
    }

    public void setAllowEarlySubmission(Boolean allowEarlySubmission) {
        this.allowEarlySubmission = allowEarlySubmission;
    }

    public Integer getEarlySubmissionLimit() {
        return earlySubmissionLimit;
    }

    public void setEarlySubmissionLimit(Integer earlySubmissionLimit) {
        this.earlySubmissionLimit = earlySubmissionLimit;
    }

    public Integer getExamStatus() {
        return examStatus;
    }

    public void setExamStatus(Integer examStatus) {
        this.examStatus = examStatus;
    }

    /**
     * 验证考试时间是否合理
     */
    public boolean isTimeValid() {
        if (startTime == null || endTime == null) return false;
        return endTime.isAfter(startTime);
    }

    /**
     * 验证及格分数是否合理
     */
    public boolean isPassingScoreValid() {
        if (totalScore == null || passingScore == null) return true;
        return passingScore <= totalScore;
    }

    @Override
    public String toString() {
        return "ExamCreateRequest{" +
                "examName='" + examName + '\'' +
                ", courseId=" + courseId +
                ", examType='" + examType + '\'' +
                ", examFormat='" + examFormat + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", duration=" + duration +
                ", location='" + location + '\'' +
                ", classroomId=" + classroomId +
                ", proctorId=" + proctorId +
                ", totalScore=" + totalScore +
                ", passingScore=" + passingScore +
                ", instructions='" + instructions + '\'' +
                ", requirements='" + requirements + '\'' +
                ", allowLateEntry=" + allowLateEntry +
                ", lateEntryLimit=" + lateEntryLimit +
                ", allowEarlySubmission=" + allowEarlySubmission +
                ", earlySubmissionLimit=" + earlySubmissionLimit +
                ", examStatus=" + examStatus +
                ", status=" + getStatus() +
                ", remarks='" + getRemarks() + '\'' +
                '}';
    }
}
