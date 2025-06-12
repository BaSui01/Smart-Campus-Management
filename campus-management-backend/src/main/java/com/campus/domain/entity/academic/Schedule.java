package com.campus.domain.entity.academic;

import com.campus.domain.entity.auth.User;
import com.campus.domain.entity.infrastructure.BaseEntity;
import com.campus.domain.entity.infrastructure.Classroom;
import com.campus.domain.entity.organization.SchoolClass;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalTime;
import java.time.LocalDateTime;

/**
 * 课表实体类
 * 管理课程表信息，用于展示和查询课程安排
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_schedule", indexes = {
    @Index(name = "idx_course_id", columnList = "course_id"),
    @Index(name = "idx_teacher_id", columnList = "teacher_id"),
    @Index(name = "idx_classroom_id", columnList = "classroom_id"),
    @Index(name = "idx_weekday_time", columnList = "weekday,start_time"),
    @Index(name = "idx_semester_year", columnList = "semester,academic_year"),
    @Index(name = "idx_status_deleted", columnList = "status,deleted")
})
public class Schedule extends BaseEntity {

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    /**
     * 教师ID
     */
    @NotNull(message = "教师ID不能为空")
    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    /**
     * 教室ID
     */
    @NotNull(message = "教室ID不能为空")
    @Column(name = "classroom_id", nullable = false)
    private Long classroomId;

    /**
     * 班级ID
     */
    @Column(name = "class_id")
    private Long classId;

    /**
     * 星期几（1-7，1表示周一）
     */
    @NotNull(message = "星期几不能为空")
    @Min(value = 1, message = "星期几必须在1-7之间")
    @Max(value = 7, message = "星期几必须在1-7之间")
    @Column(name = "weekday", nullable = false)
    private Integer weekday;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

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
     * 课表类型
     * normal（正常课表）, exam（考试安排）, activity（活动安排）
     */
    @Size(max = 20, message = "课表类型长度不能超过20个字符")
    @Column(name = "schedule_type", length = 20)
    private String scheduleType = "normal";

    /**
     * 周次范围开始
     */
    @Min(value = 1, message = "开始周次不能小于1")
    @Max(value = 25, message = "开始周次不能大于25")
    @Column(name = "start_week")
    private Integer startWeek;

    /**
     * 周次范围结束
     */
    @Min(value = 1, message = "结束周次不能小于1")
    @Max(value = 25, message = "结束周次不能大于25")
    @Column(name = "end_week")
    private Integer endWeek;

    /**
     * 单双周限制
     * all（全部）, odd（单周）, even（双周）
     */
    @Size(max = 10, message = "单双周限制长度不能超过10个字符")
    @Column(name = "week_type", length = 10)
    private String weekType = "all";

    /**
     * 是否有冲突
     */
    @Column(name = "has_conflict")
    private Boolean hasConflict = false;

    /**
     * 冲突描述
     */
    @Size(max = 500, message = "冲突描述长度不能超过500个字符")
    @Column(name = "conflict_description", length = 500)
    private String conflictDescription;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Column(name = "remarks", length = 500)
    private String remarks;

    // ================================
    // 日程管理相关属性 (用于ScheduleServiceImpl)
    // ================================

    /**
     * 日程标题
     */
    @Size(max = 200, message = "日程标题长度不能超过200个字符")
    @Column(name = "title", length = 200)
    private String title;

    /**
     * 日程描述
     */
    @Size(max = 1000, message = "日程描述长度不能超过1000个字符")
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * 用户ID (日程所属用户)
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 地点
     */
    @Size(max = 200, message = "地点长度不能超过200个字符")
    @Column(name = "location", length = 200)
    private String location;

    /**
     * 优先级 (1-低, 2-中, 3-高)
     */
    @Min(value = 1, message = "优先级不能小于1")
    @Max(value = 3, message = "优先级不能大于3")
    @Column(name = "priority")
    private Integer priority = 2;

    /**
     * 提醒时间
     */
    @Column(name = "remind_time")
    private LocalDateTime remindTime;

    /**
     * 日程开始时间 (完整日期时间，用于日程管理)
     */
    @Column(name = "schedule_start_time")
    private LocalDateTime scheduleStartTime;

    /**
     * 日程结束时间 (完整日期时间，用于日程管理)
     */
    @Column(name = "schedule_end_time")
    private LocalDateTime scheduleEndTime;

    // ================================
    // 关联关系
    // ================================

    /**
     * 关联课程
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    @JsonIgnore
    private Course course;

    /**
     * 关联教师
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    @JsonIgnore
    private User teacher;

    /**
     * 关联教室
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", insertable = false, updatable = false)
    @JsonIgnore
    private Classroom classroom;

    /**
     * 关联班级
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    @JsonIgnore
    private SchoolClass schoolClass;

    // ================================
    // 构造函数
    // ================================

    public Schedule() {
        super();
    }

    public Schedule(Long courseId, Long teacherId, Long classroomId, Integer weekday, 
                   LocalTime startTime, LocalTime endTime, String semester, Integer academicYear) {
        this();
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.classroomId = classroomId;
        this.weekday = weekday;
        this.startTime = startTime;
        this.endTime = endTime;
        this.semester = semester;
        this.academicYear = academicYear;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 获取星期几的中文描述
     */
    public String getWeekdayName() {
        if (weekday == null) return "未知";
        return switch (weekday) {
            case 1 -> "周一";
            case 2 -> "周二";
            case 3 -> "周三";
            case 4 -> "周四";
            case 5 -> "周五";
            case 6 -> "周六";
            case 7 -> "周日";
            default -> "未知";
        };
    }

    /**
     * 获取时间段描述
     */
    public String getTimeSlot() {
        if (startTime == null || endTime == null) {
            return "时间未设置";
        }
        return startTime.toString() + "-" + endTime.toString();
    }

    /**
     * 获取课表类型文本
     */
    public String getScheduleTypeText() {
        if (scheduleType == null) return "正常课表";
        return switch (scheduleType) {
            case "normal" -> "正常课表";
            case "exam" -> "考试安排";
            case "activity" -> "活动安排";
            default -> scheduleType;
        };
    }

    /**
     * 获取周次类型文本
     */
    public String getWeekTypeText() {
        if (weekType == null) return "全部";
        return switch (weekType) {
            case "all" -> "全部";
            case "odd" -> "单周";
            case "even" -> "双周";
            default -> weekType;
        };
    }

    /**
     * 获取课程名称
     */
    public String getCourseName() {
        return course != null ? course.getCourseName() : null;
    }

    /**
     * 获取教师姓名
     */
    public String getTeacherName() {
        return teacher != null ? teacher.getRealName() : null;
    }

    /**
     * 获取教室名称
     */
    public String getClassroomName() {
        return classroom != null ? classroom.getClassroomName() : null;
    }

    /**
     * 获取班级名称
     */
    public String getClassName() {
        return schoolClass != null ? schoolClass.getClassName() : null;
    }

    /**
     * 设置冲突状态
     */
    public void setConflictInfo(String description) {
        this.hasConflict = true;
        this.conflictDescription = description;
    }

    /**
     * 清除冲突状态
     */
    public void clearConflict() {
        this.hasConflict = false;
        this.conflictDescription = null;
    }

    /**
     * 获取完整的时间描述
     */
    public String getFullTimeDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(getWeekdayName());
        sb.append(" ").append(getTimeSlot());

        if (startWeek != null && endWeek != null) {
            sb.append(" (第").append(startWeek).append("-").append(endWeek).append("周");
            if (!"all".equals(weekType)) {
                sb.append(",").append(getWeekTypeText());
            }
            sb.append(")");
        }

        return sb.toString();
    }

    // ================================
    // Getter and Setter methods
    // ================================

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public Integer getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(Integer startWeek) {
        this.startWeek = startWeek;
    }

    public Integer getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(Integer endWeek) {
        this.endWeek = endWeek;
    }

    public String getWeekType() {
        return weekType;
    }

    public void setWeekType(String weekType) {
        this.weekType = weekType;
    }

    public Boolean getHasConflict() {
        return hasConflict;
    }

    public void setHasConflict(Boolean hasConflict) {
        this.hasConflict = hasConflict;
    }

    public String getConflictDescription() {
        return conflictDescription;
    }

    public void setConflictDescription(String conflictDescription) {
        this.conflictDescription = conflictDescription;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public SchoolClass getSchoolClass() {
        return schoolClass;
    }

    public void setSchoolClass(SchoolClass schoolClass) {
        this.schoolClass = schoolClass;
    }

    // ================================
    // Getter and Setter methods
    // ================================

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Long classroomId) {
        this.classroomId = classroomId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Integer getWeekday() {
        return weekday;
    }

    public void setWeekday(Integer weekday) {
        this.weekday = weekday;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
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

    // ================================
    // 日程管理相关属性的Getter/Setter方法
    // ================================

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LocalDateTime getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(LocalDateTime remindTime) {
        this.remindTime = remindTime;
    }

    public LocalDateTime getScheduleStartTime() {
        return scheduleStartTime;
    }

    public void setScheduleStartTime(LocalDateTime scheduleStartTime) {
        this.scheduleStartTime = scheduleStartTime;
    }

    public LocalDateTime getScheduleEndTime() {
        return scheduleEndTime;
    }

    public void setScheduleEndTime(LocalDateTime scheduleEndTime) {
        this.scheduleEndTime = scheduleEndTime;
    }

    // 兼容性方法：为ScheduleServiceImpl提供LocalDateTime接口
    public LocalDateTime getStartDateTime() {
        return scheduleStartTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.scheduleStartTime = startDateTime;
        if (startDateTime != null) {
            this.startTime = startDateTime.toLocalTime();
        }
    }

    public LocalDateTime getEndDateTime() {
        return scheduleEndTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.scheduleEndTime = endDateTime;
        if (endDateTime != null) {
            this.endTime = endDateTime.toLocalTime();
        }
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", teacherId=" + teacherId +
                ", classroomId=" + classroomId +
                ", classId=" + classId +
                ", weekday=" + weekday +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", semester='" + semester + '\'' +
                ", academicYear=" + academicYear +
                ", scheduleType='" + scheduleType + '\'' +
                ", startWeek=" + startWeek +
                ", endWeek=" + endWeek +
                ", weekType='" + weekType + '\'' +
                ", hasConflict=" + hasConflict +
                ", conflictDescription='" + conflictDescription + '\'' +
                ", remarks='" + remarks + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
