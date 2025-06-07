package com.campus.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * 选课实体类
 * 管理学生选课信息
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_course_selection", indexes = {
    @Index(name = "idx_student_id", columnList = "student_id"),
    @Index(name = "idx_course_id", columnList = "course_id"),
    @Index(name = "idx_schedule_id", columnList = "schedule_id"),
    @Index(name = "idx_semester", columnList = "semester"),
    @Index(name = "idx_student_course", columnList = "student_id,course_id,semester"),
    @Index(name = "idx_status_deleted", columnList = "status,deleted")
})
public class CourseSelection extends BaseEntity {

    /**
     * 学生ID
     */
    @NotNull(message = "学生ID不能为空")
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    /**
     * 课程安排ID
     */
    @Column(name = "schedule_id")
    private Long scheduleId;

    /**
     * 学期
     */
    @NotBlank(message = "学期不能为空")
    @Size(max = 20, message = "学期长度不能超过20个字符")
    @Column(name = "semester", nullable = false, length = 20)
    private String semester;

    /**
     * 学年
     */
    @NotNull(message = "学年不能为空")
    @Column(name = "academic_year", nullable = false)
    private Integer academicYear;

    /**
     * 选课状态
     * pending（待审核）, approved（已通过）, rejected（已拒绝）, withdrawn（已退课）, completed（已完成）
     */
    @NotBlank(message = "选课状态不能为空")
    @Size(max = 20, message = "选课状态长度不能超过20个字符")
    @Column(name = "selection_status", nullable = false, length = 20)
    private String selectionStatus = "pending";

    /**
     * 选课时间
     */
    @Column(name = "selection_time")
    private LocalDateTime selectionTime;

    /**
     * 审核时间
     */
    @Column(name = "approval_time")
    private LocalDateTime approvalTime;

    /**
     * 审核人ID
     */
    @Column(name = "approved_by")
    private Long approvedBy;

    /**
     * 退课时间
     */
    @Column(name = "withdrawal_time")
    private LocalDateTime withdrawalTime;

    /**
     * 退课原因
     */
    @Size(max = 500, message = "退课原因长度不能超过500个字符")
    @Column(name = "withdrawal_reason", length = 500)
    private String withdrawalReason;

    /**
     * 选课优先级
     */
    @Min(value = 1, message = "选课优先级不能小于1")
    @Max(value = 10, message = "选课优先级不能大于10")
    @Column(name = "priority")
    private Integer priority = 5;

    /**
     * 是否为必修课
     */
    @Column(name = "is_required")
    private Boolean isRequired = false;

    /**
     * 学分
     */
    @DecimalMin(value = "0.0", message = "学分不能小于0")
    @Column(name = "credit", precision = 4, scale = 1)
    private java.math.BigDecimal credit;

    /**
     * 选课类型
     * normal（正常选课）, makeup（补选）, retake（重修）
     */
    @Size(max = 20, message = "选课类型长度不能超过20个字符")
    @Column(name = "selection_type", length = 20)
    private String selectionType = "normal";

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
     * 关联学生
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    /**
     * 关联课程
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;

    /**
     * 关联课程安排
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", insertable = false, updatable = false)
    private CourseSchedule courseSchedule;

    /**
     * 关联审核人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by", insertable = false, updatable = false)
    private User approver;

    /**
     * 关联选课时间段
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selection_period_id")
    private CourseSelectionPeriod selectionPeriod;

    // ================================
    // 构造函数
    // ================================

    public CourseSelection() {
        super();
    }

    public CourseSelection(Long studentId, Long courseId, String semester, Integer academicYear) {
        this();
        this.studentId = studentId;
        this.courseId = courseId;
        this.semester = semester;
        this.academicYear = academicYear;
        this.selectionTime = LocalDateTime.now();
    }

    // ================================
    // Getter和Setter方法
    // ================================

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Integer getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(Integer academicYear) {
        this.academicYear = academicYear;
    }

    public String getSelectionStatus() {
        return selectionStatus;
    }

    public void setSelectionStatus(String selectionStatus) {
        this.selectionStatus = selectionStatus;
    }

    public LocalDateTime getSelectionTime() {
        return selectionTime;
    }

    public void setSelectionTime(LocalDateTime selectionTime) {
        this.selectionTime = selectionTime;
    }

    public LocalDateTime getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(LocalDateTime approvalTime) {
        this.approvalTime = approvalTime;
    }

    public Long getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getWithdrawalTime() {
        return withdrawalTime;
    }

    public void setWithdrawalTime(LocalDateTime withdrawalTime) {
        this.withdrawalTime = withdrawalTime;
    }

    public String getWithdrawalReason() {
        return withdrawalReason;
    }

    public void setWithdrawalReason(String withdrawalReason) {
        this.withdrawalReason = withdrawalReason;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public java.math.BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(java.math.BigDecimal credit) {
        this.credit = credit;
    }

    public String getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(String selectionType) {
        this.selectionType = selectionType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public CourseSchedule getCourseSchedule() {
        return courseSchedule;
    }

    public void setCourseSchedule(CourseSchedule courseSchedule) {
        this.courseSchedule = courseSchedule;
    }

    public User getApprover() {
        return approver;
    }

    public void setApprover(User approver) {
        this.approver = approver;
    }

    public CourseSelectionPeriod getSelectionPeriod() {
        return selectionPeriod;
    }

    public void setSelectionPeriod(CourseSelectionPeriod selectionPeriod) {
        this.selectionPeriod = selectionPeriod;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 提交选课申请
     */
    public void submit() {
        this.selectionStatus = "pending";
        this.selectionTime = LocalDateTime.now();
        this.setStatus(1); // 激活状态
    }

    /**
     * 审核通过
     */
    public void approve(Long approverId) {
        this.selectionStatus = "approved";
        this.approvalTime = LocalDateTime.now();
        this.approvedBy = approverId;
    }

    /**
     * 审核拒绝
     */
    public void reject(Long approverId, String reason) {
        this.selectionStatus = "rejected";
        this.approvalTime = LocalDateTime.now();
        this.approvedBy = approverId;
        this.remarks = reason;
    }

    /**
     * 退课
     */
    public void withdraw(String reason) {
        this.selectionStatus = "withdrawn";
        this.withdrawalTime = LocalDateTime.now();
        this.withdrawalReason = reason;
    }

    /**
     * 完成课程
     */
    public void complete() {
        this.selectionStatus = "completed";
    }

    /**
     * 检查是否可以退课
     */
    public boolean canWithdraw() {
        return "approved".equals(selectionStatus) && 
               (withdrawalTime == null || 
                withdrawalTime.isAfter(LocalDateTime.now().minusDays(30)));
    }

    /**
     * 检查是否已通过审核
     */
    public boolean isApproved() {
        return "approved".equals(selectionStatus);
    }

    /**
     * 检查是否已完成
     */
    public boolean isCompleted() {
        return "completed".equals(selectionStatus);
    }

    /**
     * 检查是否已退课
     */
    public boolean isWithdrawn() {
        return "withdrawn".equals(selectionStatus);
    }

    /**
     * 获取选课状态文本
     */
    public String getSelectionStatusText() {
        if (selectionStatus == null) return "未知";
        return switch (selectionStatus) {
            case "pending" -> "待审核";
            case "approved" -> "已通过";
            case "rejected" -> "已拒绝";
            case "withdrawn" -> "已退课";
            case "completed" -> "已完成";
            default -> selectionStatus;
        };
    }

    /**
     * 获取选课类型文本
     */
    public String getSelectionTypeText() {
        if (selectionType == null) return "正常选课";
        return switch (selectionType) {
            case "normal" -> "正常选课";
            case "makeup" -> "补选";
            case "retake" -> "重修";
            default -> selectionType;
        };
    }

    /**
     * 获取学生姓名
     */
    public String getStudentName() {
        return student != null ? student.getRealName() : null;
    }

    /**
     * 获取课程名称
     */
    public String getCourseName() {
        return course != null ? course.getCourseName() : null;
    }

    /**
     * 获取审核人姓名
     */
    public String getApproverName() {
        return approver != null ? approver.getRealName() : null;
    }

    /**
     * 获取课程学分
     */
    public java.math.BigDecimal getCourseCredit() {
        if (credit != null) {
            return credit;
        }
        return course != null ? course.getCredit() : java.math.BigDecimal.ZERO;
    }

    /**
     * 获取选课状态颜色（前端显示用）
     */
    public String getStatusColor() {
        if (selectionStatus == null) return "default";
        return switch (selectionStatus) {
            case "pending" -> "warning";
            case "approved" -> "success";
            case "rejected" -> "danger";
            case "withdrawn" -> "secondary";
            case "completed" -> "primary";
            default -> "default";
        };
    }

    /**
     * 获取优先级文本
     */
    public String getPriorityText() {
        if (priority == null) return "普通";
        return switch (priority) {
            case 1, 2 -> "最高";
            case 3, 4 -> "高";
            case 5, 6 -> "普通";
            case 7, 8 -> "低";
            case 9, 10 -> "最低";
            default -> "普通";
        };
    }

    // ================================
    // 验证方法
    // ================================

    /**
     * 验证选课数据
     */
    @PrePersist
    @PreUpdate
    public void validateSelection() {
        // 确保选课时间不为空
        if (selectionTime == null) {
            this.selectionTime = LocalDateTime.now();
        }
        
        // 确保学期和学年匹配
        if (semester != null && academicYear != null) {
            // 这里可以添加学期和学年的验证逻辑
        }
    }

    // ================================
    // toString、equals、hashCode方法
    // ================================

    @Override
    public String toString() {
        return "CourseSelection{" +
                "id=" + getId() +
                ", studentId=" + studentId +
                ", courseId=" + courseId +
                ", semester='" + semester + '\'' +
                ", selectionStatus='" + selectionStatus + '\'' +
                ", selectionTime=" + selectionTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CourseSelection that = (CourseSelection) o;

        if (studentId != null ? !studentId.equals(that.studentId) : that.studentId != null) return false;
        if (courseId != null ? !courseId.equals(that.courseId) : that.courseId != null) return false;
        return semester != null ? semester.equals(that.semester) : that.semester == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (studentId != null ? studentId.hashCode() : 0);
        result = 31 * result + (courseId != null ? courseId.hashCode() : 0);
        result = 31 * result + (semester != null ? semester.hashCode() : 0);
        return result;
    }
}
