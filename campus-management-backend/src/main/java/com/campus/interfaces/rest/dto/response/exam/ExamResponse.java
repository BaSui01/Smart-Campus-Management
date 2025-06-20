package com.campus.interfaces.rest.dto.response.exam;

import com.campus.interfaces.rest.dto.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * 考试响应DTO
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-19
 */
@Schema(description = "考试响应数据")
public class ExamResponse extends BaseResponse {

    /**
     * 考试名称
     */
    @Schema(description = "考试名称", example = "计算机科学导论期末考试")
    private String examName;

    /**
     * 关联课程ID
     */
    @Schema(description = "关联课程ID", example = "1")
    private Long courseId;

    /**
     * 课程名称
     */
    @Schema(description = "课程名称", example = "计算机科学导论")
    private String courseName;

    /**
     * 课程代码
     */
    @Schema(description = "课程代码", example = "CS101")
    private String courseCode;

    /**
     * 考试类型
     */
    @Schema(description = "考试类型", example = "期末考试")
    private String examType;

    /**
     * 考试形式
     */
    @Schema(description = "考试形式", example = "闭卷")
    private String examFormat;

    /**
     * 考试开始时间
     */
    @Schema(description = "考试开始时间", example = "2025-01-15 09:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 考试结束时间
     */
    @Schema(description = "考试结束时间", example = "2025-01-15 11:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 考试时长（分钟）
     */
    @Schema(description = "考试时长", example = "120")
    private Integer duration;

    /**
     * 考试地点
     */
    @Schema(description = "考试地点", example = "教学楼A101")
    private String location;

    /**
     * 教室ID
     */
    @Schema(description = "教室ID", example = "1")
    private Long classroomId;

    /**
     * 教室名称
     */
    @Schema(description = "教室名称", example = "A101")
    private String classroomName;

    /**
     * 监考教师ID
     */
    @Schema(description = "监考教师ID", example = "1")
    private Long proctorId;

    /**
     * 监考教师姓名
     */
    @Schema(description = "监考教师姓名", example = "李老师")
    private String proctorName;

    /**
     * 总分
     */
    @Schema(description = "总分", example = "100")
    private Integer totalScore;

    /**
     * 及格分数
     */
    @Schema(description = "及格分数", example = "60")
    private Integer passingScore;

    /**
     * 考试说明
     */
    @Schema(description = "考试说明", example = "请携带学生证和身份证参加考试")
    private String instructions;

    /**
     * 考试要求
     */
    @Schema(description = "考试要求", example = "禁止携带手机等电子设备")
    private String requirements;

    /**
     * 是否允许迟到
     */
    @Schema(description = "是否允许迟到", example = "false")
    private Boolean allowLateEntry;

    /**
     * 迟到限制时间（分钟）
     */
    @Schema(description = "迟到限制时间", example = "15")
    private Integer lateEntryLimit;

    /**
     * 是否允许提前交卷
     */
    @Schema(description = "是否允许提前交卷", example = "true")
    private Boolean allowEarlySubmission;

    /**
     * 提前交卷限制时间（分钟）
     */
    @Schema(description = "提前交卷限制时间", example = "30")
    private Integer earlySubmissionLimit;

    /**
     * 考试状态
     */
    @Schema(description = "考试状态", example = "1")
    private Integer examStatus;

    /**
     * 考试状态文本
     */
    @Schema(description = "考试状态文本", example = "未开始")
    private String examStatusText;

    /**
     * 参考人数
     */
    @Schema(description = "参考人数", example = "45")
    private Integer participantCount;

    /**
     * 已提交人数
     */
    @Schema(description = "已提交人数", example = "42")
    private Integer submittedCount;

    /**
     * 平均分
     */
    @Schema(description = "平均分", example = "78.5")
    private Double averageScore;

    /**
     * 及格率
     */
    @Schema(description = "及格率", example = "85.7")
    private Double passingRate;

    /**
     * 最高分
     */
    @Schema(description = "最高分", example = "95")
    private Integer highestScore;

    /**
     * 最低分
     */
    @Schema(description = "最低分", example = "45")
    private Integer lowestScore;

    // 构造函数
    public ExamResponse() {}

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

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
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

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public Long getProctorId() {
        return proctorId;
    }

    public void setProctorId(Long proctorId) {
        this.proctorId = proctorId;
    }

    public String getProctorName() {
        return proctorName;
    }

    public void setProctorName(String proctorName) {
        this.proctorName = proctorName;
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
        // 自动设置考试状态文本
        switch (examStatus != null ? examStatus : 0) {
            case 1:
                this.examStatusText = "未开始";
                break;
            case 2:
                this.examStatusText = "进行中";
                break;
            case 3:
                this.examStatusText = "已结束";
                break;
            case 4:
                this.examStatusText = "已取消";
                break;
            default:
                this.examStatusText = "未知状态";
                break;
        }
    }

    public String getExamStatusText() {
        return examStatusText;
    }

    public void setExamStatusText(String examStatusText) {
        this.examStatusText = examStatusText;
    }

    public Integer getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(Integer participantCount) {
        this.participantCount = participantCount;
    }

    public Integer getSubmittedCount() {
        return submittedCount;
    }

    public void setSubmittedCount(Integer submittedCount) {
        this.submittedCount = submittedCount;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public Double getPassingRate() {
        return passingRate;
    }

    public void setPassingRate(Double passingRate) {
        this.passingRate = passingRate;
    }

    public Integer getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(Integer highestScore) {
        this.highestScore = highestScore;
    }

    public Integer getLowestScore() {
        return lowestScore;
    }

    public void setLowestScore(Integer lowestScore) {
        this.lowestScore = lowestScore;
    }

    /**
     * 获取提交进度百分比
     */
    public Double getSubmissionProgress() {
        if (participantCount == null || participantCount == 0 || submittedCount == null) {
            return 0.0;
        }
        return (double) submittedCount / participantCount * 100;
    }

    /**
     * 是否可以参加考试
     */
    public Boolean canTakeExam() {
        return examStatus != null && examStatus == 2; // 进行中
    }

    /**
     * 考试是否已结束
     */
    public Boolean isFinished() {
        return examStatus != null && examStatus == 3; // 已结束
    }

    @Override
    public String toString() {
        return "ExamResponse{" +
                "id=" + getId() +
                ", examName='" + examName + '\'' +
                ", courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", courseCode='" + courseCode + '\'' +
                ", examType='" + examType + '\'' +
                ", examFormat='" + examFormat + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", duration=" + duration +
                ", location='" + location + '\'' +
                ", classroomId=" + classroomId +
                ", classroomName='" + classroomName + '\'' +
                ", proctorId=" + proctorId +
                ", proctorName='" + proctorName + '\'' +
                ", totalScore=" + totalScore +
                ", passingScore=" + passingScore +
                ", instructions='" + instructions + '\'' +
                ", requirements='" + requirements + '\'' +
                ", allowLateEntry=" + allowLateEntry +
                ", lateEntryLimit=" + lateEntryLimit +
                ", allowEarlySubmission=" + allowEarlySubmission +
                ", earlySubmissionLimit=" + earlySubmissionLimit +
                ", examStatus=" + examStatus +
                ", examStatusText='" + examStatusText + '\'' +
                ", participantCount=" + participantCount +
                ", submittedCount=" + submittedCount +
                ", averageScore=" + averageScore +
                ", passingRate=" + passingRate +
                ", highestScore=" + highestScore +
                ", lowestScore=" + lowestScore +
                ", status=" + getStatus() +
                ", statusText='" + getStatusText() + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}
