package com.campus.domain.entity.academic;

import com.campus.domain.entity.auth.User;
import com.campus.domain.entity.infrastructure.BaseEntity;
import com.campus.domain.entity.organization.Student;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考勤记录实体类
 * 管理学生的考勤信息
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_attendance", indexes = {
    @Index(name = "idx_student_id", columnList = "student_id"),
    @Index(name = "idx_course_id", columnList = "course_id"),
    @Index(name = "idx_attendance_date", columnList = "attendance_date"),
    @Index(name = "idx_attendance_status", columnList = "attendance_status")
})
public class Attendance extends BaseEntity {

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
     * 考勤日期
     */
    @NotNull(message = "考勤日期不能为空")
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    /**
     * 考勤状态 (present/absent/late/leave)
     */
    @NotNull(message = "考勤状态不能为空")
    @Size(max = 20, message = "考勤状态长度不能超过20个字符")
    @Column(name = "attendance_status", nullable = false, length = 20)
    private String attendanceStatus;

    /**
     * 签到时间
     */
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    /**
     * 签退时间
     */
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    /**
     * 迟到分钟数
     */
    @Column(name = "late_minutes")
    private Integer lateMinutes = 0;

    /**
     * 早退分钟数
     */
    @Column(name = "early_leave_minutes")
    private Integer earlyLeaveMinutes = 0;

    /**
     * 请假类型 (sick/personal/official/other)
     */
    @Size(max = 20, message = "请假类型长度不能超过20个字符")
    @Column(name = "leave_type", length = 20)
    private String leaveType;

    /**
     * 请假原因
     */
    @Size(max = 500, message = "请假原因长度不能超过500个字符")
    @Column(name = "leave_reason", length = 500)
    private String leaveReason;

    /**
     * 是否已批准（针对请假）
     */
    @Column(name = "is_approved")
    private Boolean isApproved;

    /**
     * 批准人ID
     */
    @Column(name = "approver_id")
    private Long approverId;

    /**
     * 批准时间
     */
    @Column(name = "approval_time")
    private LocalDateTime approvalTime;

    /**
     * 记录人ID（教师或管理员）
     */
    @Column(name = "recorder_id")
    private Long recorderId;

    /**
     * 记录时间
     */
    @Column(name = "record_time")
    private LocalDateTime recordTime;

    /**
     * 签到方式 (manual/qr_code/face_recognition/location)
     */
    @Size(max = 20, message = "签到方式长度不能超过20个字符")
    @Column(name = "check_in_method", length = 20)
    private String checkInMethod;

    /**
     * 签到位置
     */
    @Size(max = 200, message = "签到位置长度不能超过200个字符")
    @Column(name = "check_in_location", length = 200)
    private String checkInLocation;

    /**
     * 设备信息
     */
    @Size(max = 200, message = "设备信息长度不能超过200个字符")
    @Column(name = "device_info", length = 200)
    private String deviceInfo;

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
     * 学生信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    @JsonIgnore
    private Student student;

    /**
     * 课程信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    @JsonIgnore
    private Course course;

    /**
     * 课程安排信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", insertable = false, updatable = false)
    @JsonIgnore
    private CourseSchedule schedule;

    /**
     * 批准人信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", insertable = false, updatable = false)
    @JsonIgnore
    private User approver;

    /**
     * 记录人信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorder_id", insertable = false, updatable = false)
    @JsonIgnore
    private User recorder;

    // ================================
    // 构造函数
    // ================================

    public Attendance() {
        super();
    }

    public Attendance(Long studentId, Long courseId, LocalDate attendanceDate, String attendanceStatus) {
        this();
        this.studentId = studentId;
        this.courseId = courseId;
        this.attendanceDate = attendanceDate;
        this.attendanceStatus = attendanceStatus;
        this.recordTime = LocalDateTime.now();
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 获取考勤状态文本
     */
    public String getAttendanceStatusText() {
        if (attendanceStatus == null) return "未知";
        return switch (attendanceStatus) {
            case "present" -> "出勤";
            case "absent" -> "缺勤";
            case "late" -> "迟到";
            case "leave" -> "请假";
            default -> attendanceStatus;
        };
    }

    /**
     * 获取请假类型文本
     */
    public String getLeaveTypeText() {
        if (leaveType == null) return "";
        return switch (leaveType) {
            case "sick" -> "病假";
            case "personal" -> "事假";
            case "official" -> "公假";
            case "other" -> "其他";
            default -> leaveType;
        };
    }

    /**
     * 获取签到方式文本
     */
    public String getCheckInMethodText() {
        if (checkInMethod == null) return "";
        return switch (checkInMethod) {
            case "manual" -> "手动签到";
            case "qr_code" -> "扫码签到";
            case "face_recognition" -> "人脸识别";
            case "location" -> "位置签到";
            default -> checkInMethod;
        };
    }

    /**
     * 签到
     */
    public void checkIn(String method, String location, String device) {
        this.checkInTime = LocalDateTime.now();
        this.checkInMethod = method;
        this.checkInLocation = location;
        this.deviceInfo = device;
        
        // 如果之前是缺勤状态，更新为出勤
        if ("absent".equals(this.attendanceStatus)) {
            this.attendanceStatus = "present";
        }
    }

    /**
     * 签退
     */
    public void checkOut() {
        this.checkOutTime = LocalDateTime.now();
    }

    /**
     * 标记迟到
     */
    public void markLate(int minutes) {
        this.attendanceStatus = "late";
        this.lateMinutes = minutes;
    }

    /**
     * 标记早退
     */
    public void markEarlyLeave(int minutes) {
        this.earlyLeaveMinutes = minutes;
    }

    /**
     * 申请请假
     */
    public void applyLeave(String type, String reason) {
        this.attendanceStatus = "leave";
        this.leaveType = type;
        this.leaveReason = reason;
        this.isApproved = false;
    }

    /**
     * 批准请假
     */
    public void approveLeave(Long approverId) {
        this.isApproved = true;
        this.approverId = approverId;
        this.approvalTime = LocalDateTime.now();
    }

    /**
     * 拒绝请假
     */
    public void rejectLeave(Long approverId) {
        this.isApproved = false;
        this.approverId = approverId;
        this.approvalTime = LocalDateTime.now();
        // 拒绝后改为缺勤
        this.attendanceStatus = "absent";
    }

    /**
     * 检查是否需要审批
     */
    public boolean needsApproval() {
        return "leave".equals(attendanceStatus) && isApproved == null;
    }

    /**
     * 检查是否为正常出勤
     */
    public boolean isNormalAttendance() {
        return "present".equals(attendanceStatus) && 
               (lateMinutes == null || lateMinutes == 0) && 
               (earlyLeaveMinutes == null || earlyLeaveMinutes == 0);
    }

    /**
     * 获取学生姓名
     */
    public String getStudentName() {
        return student != null ? student.getRealName() : null;
    }

    /**
     * 获取学生学号
     */
    public String getStudentNumber() {
        return student != null ? student.getStudentNumber() : null;
    }

    /**
     * 获取课程名称
     */
    public String getCourseName() {
        return course != null ? course.getCourseName() : null;
    }

    /**
     * 获取批准人姓名
     */
    public String getApproverName() {
        return approver != null ? approver.getRealName() : null;
    }

    /**
     * 获取记录人姓名
     */
    public String getRecorderName() {
        return recorder != null ? recorder.getRealName() : null;
    }

    /**
     * 验证考勤数据
     */
    @PrePersist
    @PreUpdate
    public void validateAttendance() {
        if (checkOutTime != null && checkInTime != null && checkOutTime.isBefore(checkInTime)) {
            throw new IllegalArgumentException("签退时间不能早于签到时间");
        }
        
        if ("leave".equals(attendanceStatus) && (leaveType == null || leaveType.trim().isEmpty())) {
            throw new IllegalArgumentException("请假必须指定请假类型");
        }
        
        if (lateMinutes != null && lateMinutes < 0) {
            throw new IllegalArgumentException("迟到分钟数不能为负数");
        }
        
        if (earlyLeaveMinutes != null && earlyLeaveMinutes < 0) {
            throw new IllegalArgumentException("早退分钟数不能为负数");
        }
    }

    // ================================
    // Getter and Setter methods
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

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public Integer getLateMinutes() {
        return lateMinutes;
    }

    public void setLateMinutes(Integer lateMinutes) {
        this.lateMinutes = lateMinutes;
    }

    public Integer getEarlyLeaveMinutes() {
        return earlyLeaveMinutes;
    }

    public void setEarlyLeaveMinutes(Integer earlyLeaveMinutes) {
        this.earlyLeaveMinutes = earlyLeaveMinutes;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getLeaveReason() {
        return leaveReason;
    }

    public void setLeaveReason(String leaveReason) {
        this.leaveReason = leaveReason;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    public Long getApproverId() {
        return approverId;
    }

    public void setApproverId(Long approverId) {
        this.approverId = approverId;
    }

    public LocalDateTime getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(LocalDateTime approvalTime) {
        this.approvalTime = approvalTime;
    }

    public Long getRecorderId() {
        return recorderId;
    }

    public void setRecorderId(Long recorderId) {
        this.recorderId = recorderId;
    }

    public LocalDateTime getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(LocalDateTime recordTime) {
        this.recordTime = recordTime;
    }

    public String getCheckInMethod() {
        return checkInMethod;
    }

    public void setCheckInMethod(String checkInMethod) {
        this.checkInMethod = checkInMethod;
    }

    public String getCheckInLocation() {
        return checkInLocation;
    }

    public void setCheckInLocation(String checkInLocation) {
        this.checkInLocation = checkInLocation;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
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

    public CourseSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(CourseSchedule schedule) {
        this.schedule = schedule;
    }

    public User getApprover() {
        return approver;
    }

    public void setApprover(User approver) {
        this.approver = approver;
    }

    public User getRecorder() {
        return recorder;
    }

    public void setRecorder(User recorder) {
        this.recorder = recorder;
    }

    // ================================
    // 兼容性方法 - 为了支持 AttendanceApiController
    // ================================

    /**
     * 获取考勤时间（兼容性方法）
     */
    public java.time.LocalTime getAttendanceTime() {
        return checkInTime != null ? checkInTime.toLocalTime() : null;
    }

    /**
     * 设置考勤时间（兼容性方法）
     */
    public void setAttendanceTime(java.time.LocalTime attendanceTime) {
        if (attendanceTime != null) {
            LocalDate date = attendanceDate != null ? attendanceDate : LocalDate.now();
            this.checkInTime = LocalDateTime.of(date, attendanceTime);
        }
    }

    /**
     * 获取位置（兼容性方法）
     */
    public String getLocation() {
        return checkInLocation;
    }

    /**
     * 设置位置（兼容性方法）
     */
    public void setLocation(String location) {
        this.checkInLocation = location;
    }

    /**
     * 获取记录方式（兼容性方法）
     */
    public String getRecordMethod() {
        return checkInMethod;
    }

    /**
     * 设置记录方式（兼容性方法）
     */
    public void setRecordMethod(String recordMethod) {
        this.checkInMethod = recordMethod;
    }

    /**
     * 获取证明附件（兼容性方法）
     */
    public String getProofAttachment() {
        // 暂时返回 null，可以后续扩展为独立的附件字段
        return null;
    }

    /**
     * 设置证明附件（兼容性方法）
     */
    public void setProofAttachment(String proofAttachment) {
        // 暂时不做处理，可以后续扩展为独立的附件字段
    }
}
