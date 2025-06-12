package com.campus.domain.entity.academic;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


import java.time.LocalDate;
import java.util.List;

import com.campus.domain.entity.auth.User;
import com.campus.domain.entity.infrastructure.BaseEntity;
import com.campus.domain.entity.infrastructure.Classroom;

/**
 * 课程安排实体类
 * 管理具体的课程时间、教室、教师安排
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_course_schedule", indexes = {
    @Index(name = "idx_course_id", columnList = "course_id"),
    @Index(name = "idx_classroom_id", columnList = "classroom_id"),
    @Index(name = "idx_teacher_id", columnList = "teacher_id"),
    @Index(name = "idx_time_slot_id", columnList = "time_slot_id"),
    @Index(name = "idx_schedule_date", columnList = "schedule_date"),
    @Index(name = "idx_day_period", columnList = "day_of_week,period_number"),
    @Index(name = "idx_status_deleted", columnList = "status,deleted")
})
public class CourseSchedule extends BaseEntity {

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    /**
     * 教室ID
     */
    @NotNull(message = "教室ID不能为空")
    @Column(name = "classroom_id", nullable = false)
    private Long classroomId;

    /**
     * 教师ID
     */
    @NotNull(message = "教师ID不能为空")
    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    /**
     * 时间段ID
     */
    @NotNull(message = "时间段ID不能为空")
    @Column(name = "time_slot_id", nullable = false)
    private Long timeSlotId;

    /**
     * 星期几 (1-7，1表示周一，7表示周日)
     */
    @NotNull(message = "星期几不能为空")
    @Min(value = 1, message = "星期几必须在1-7之间")
    @Max(value = 7, message = "星期几必须在1-7之间")
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    /**
     * 第几节课 (1-12，表示一天中的第几节课)
     */
    @NotNull(message = "节次不能为空")
    @Min(value = 1, message = "节次必须在1-12之间")
    @Max(value = 12, message = "节次必须在1-12之间")
    @Column(name = "period_number", nullable = false)
    private Integer periodNumber;

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
     * 具体日期（可选，用于特殊安排）
     */
    @Column(name = "schedule_date")
    private LocalDate scheduleDate;

    /**
     * 开始时间
     */
    @Column(name = "start_time")
    private java.time.LocalTime startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    private java.time.LocalTime endTime;

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
     * 班级列表（JSON格式存储或逗号分隔）
     */
    @Size(max = 500, message = "班级列表长度不能超过500个字符")
    @Column(name = "class_list", length = 500)
    private String classList;

    /**
     * 学生数量
     */
    @Min(value = 0, message = "学生数量不能小于0")
    @Column(name = "student_count")
    private Integer studentCount;

    /**
     * 安排类型
     * normal（正常排课）, makeup（补课）, exam（考试）, activity（活动）
     */
    @NotBlank(message = "安排类型不能为空")
    @Size(max = 20, message = "安排类型长度不能超过20个字符")
    @Column(name = "schedule_type", nullable = false, length = 20)
    private String scheduleType = "normal";

    /**
     * 是否为合班教学
     */
    @Column(name = "is_combined_class")
    private Boolean isCombinedClass = false;

    /**
     * 合班教学的其他课程ID（逗号分隔）
     */
    @Size(max = 200, message = "合班课程ID列表长度不能超过200个字符")
    @Column(name = "combined_course_ids", length = 200)
    private String combinedCourseIds;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Column(name = "remarks", length = 500)
    private String remarks;

    /**
     * 冲突检查状态
     * none（无冲突）, warning（警告）, error（错误）
     */
    @Size(max = 20, message = "冲突状态长度不能超过20个字符")
    @Column(name = "conflict_status", length = 20)
    private String conflictStatus = "none";

    /**
     * 冲突描述
     */
    @Size(max = 500, message = "冲突描述长度不能超过500个字符")
    @Column(name = "conflict_description", length = 500)
    private String conflictDescription;

    // ================================
    // 关联关系
    // ================================

    /**
     * 关联课程
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;

    /**
     * 关联教室
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", insertable = false, updatable = false)
    private Classroom classroom;

    /**
     * 关联教师
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private User teacher;

    /**
     * 关联时间段
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_slot_id", insertable = false, updatable = false)
    private TimeSlot timeSlot;

    // ================================
    // 构造函数
    // ================================

    public CourseSchedule() {
        super();
    }

    public CourseSchedule(Long courseId, Long classroomId, Long teacherId, Long timeSlotId, 
                         Integer dayOfWeek, Integer periodNumber, String semester, Integer academicYear) {
        this();
        this.courseId = courseId;
        this.classroomId = classroomId;
        this.teacherId = teacherId;
        this.timeSlotId = timeSlotId;
        this.dayOfWeek = dayOfWeek;
        this.periodNumber = periodNumber;
        this.semester = semester;
        this.academicYear = academicYear;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 获取课程名称
     */
    public String getCourseName() {
        return course != null ? course.getCourseName() : null;
    }

    /**
     * 获取教室名称
     */
    public String getClassroomName() {
        return classroom != null ? classroom.getClassroomName() : null;
    }

    /**
     * 获取教师姓名
     */
    public String getTeacherName() {
        return teacher != null ? teacher.getRealName() : null;
    }

    /**
     * 获取时间段描述
     */
    public String getTimeSlotDescription() {
        return timeSlot != null ? timeSlot.getFullTimeDescription() : 
               String.format("周%d第%d节", dayOfWeek, periodNumber);
    }

    /**
     * 获取星期几的中文名称
     */
    public String getDayOfWeekText() {
        if (dayOfWeek == null) return "未知";
        return switch (dayOfWeek) {
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
     * 获取安排类型文本
     */
    public String getScheduleTypeText() {
        if (scheduleType == null) return "未知";
        return switch (scheduleType) {
            case "normal" -> "正常排课";
            case "makeup" -> "补课";
            case "exam" -> "考试";
            case "activity" -> "活动";
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
     * 获取完整的时间描述
     */
    public String getFullTimeDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(getDayOfWeekText());
        sb.append(" 第").append(periodNumber).append("节");
        
        if (startWeek != null && endWeek != null) {
            sb.append(" (第").append(startWeek).append("-").append(endWeek).append("周");
            if (!"all".equals(weekType)) {
                sb.append(",").append(getWeekTypeText());
            }
            sb.append(")");
        }
        
        return sb.toString();
    }

    /**
     * 检查是否与另一个课程安排冲突
     */
    public boolean conflictsWith(CourseSchedule other) {
        if (other == null) {
            return false;
        }

        // 检查学期和学年
        if (!this.semester.equals(other.semester) || !this.academicYear.equals(other.academicYear)) {
            return false;
        }

        // 检查时间冲突（星期和节次）
        if (!this.dayOfWeek.equals(other.dayOfWeek) || !this.periodNumber.equals(other.periodNumber)) {
            return false;
        }

        // 检查周次范围是否重叠
        if (!isWeekRangeOverlapping(other)) {
            return false;
        }

        // 检查教室冲突
        if (this.classroomId.equals(other.classroomId)) {
            return true;
        }

        // 检查教师冲突
        if (this.teacherId.equals(other.teacherId)) {
            return true;
        }

        // 检查班级冲突（如果有班级信息）
        if (this.classList != null && other.classList != null) {
            return hasClassConflict(other);
        }

        return false;
    }

    /**
     * 检查周次范围是否重叠
     */
    private boolean isWeekRangeOverlapping(CourseSchedule other) {
        if (this.startWeek == null || this.endWeek == null || 
            other.startWeek == null || other.endWeek == null) {
            return true; // 如果周次信息不完整，假设重叠
        }

        // 检查周次范围重叠
        boolean rangeOverlap = !(this.endWeek < other.startWeek || this.startWeek > other.endWeek);
        if (!rangeOverlap) {
            return false;
        }

        // 检查单双周冲突
        if ("odd".equals(this.weekType) && "even".equals(other.weekType)) {
            return false;
        }
        if ("even".equals(this.weekType) && "odd".equals(other.weekType)) {
            return false;
        }

        return true;
    }

    /**
     * 检查班级冲突
     */
    private boolean hasClassConflict(CourseSchedule other) {
        if (this.classList == null || other.classList == null) {
            return false;
        }

        String[] thisClasses = this.classList.split(",");
        String[] otherClasses = other.classList.split(",");

        for (String thisClass : thisClasses) {
            for (String otherClass : otherClasses) {
                if (thisClass.trim().equals(otherClass.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 生成唯一标识符
     */
    public String getUniqueKey() {
        return String.format("%s_%d_D%d_P%d_%d_%d", 
            semester, academicYear, dayOfWeek, periodNumber, classroomId, teacherId);
    }

    /**
     * 检查是否有冲突
     */
    public boolean hasConflict() {
        return "warning".equals(conflictStatus) || "error".equals(conflictStatus);
    }

    /**
     * 设置冲突状态
     */
    public void setConflictInfo(String status, String description) {
        this.conflictStatus = status;
        this.conflictDescription = description;
    }

    /**
     * 清除冲突状态
     */
    public void clearConflict() {
        this.conflictStatus = "none";
        this.conflictDescription = null;
    }

    /**
     * 获取班级数组
     */
    public String[] getClassArray() {
        if (classList == null || classList.trim().isEmpty()) {
            return new String[0];
        }
        return classList.split(",");
    }

    /**
     * 设置班级列表
     */
    public void setClassList(List<String> classes) {
        if (classes == null || classes.isEmpty()) {
            this.classList = null;
        } else {
            this.classList = String.join(",", classes);
        }
    }

    /**
     * 添加班级
     */
    public void addClass(String className) {
        if (className == null || className.trim().isEmpty()) {
            return;
        }
        
        if (classList == null || classList.trim().isEmpty()) {
            classList = className.trim();
        } else {
            classList += "," + className.trim();
        }
    }

    // ================================
    // 验证方法
    // ================================

    /**
     * 验证课程安排是否合理
     */
    @PrePersist
    @PreUpdate
    public void validateSchedule() {
        if (startWeek != null && endWeek != null) {
            if (startWeek > endWeek) {
                throw new IllegalArgumentException("开始周次不能大于结束周次");
            }
        }
        
        if (studentCount != null && studentCount < 0) {
            throw new IllegalArgumentException("学生数量不能为负数");
        }
    }

    // ================================
    // Getter/Setter 方法
    // ================================

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Long classroomId) {
        this.classroomId = classroomId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(Long timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getPeriodNumber() {
        return periodNumber;
    }

    public void setPeriodNumber(Integer periodNumber) {
        this.periodNumber = periodNumber;
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

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public java.time.LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(java.time.LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public java.time.LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(java.time.LocalTime startTime) {
        this.startTime = startTime;
    }

    public java.time.LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(java.time.LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getClassList() {
        return classList;
    }

    public void setClassList(String classList) {
        this.classList = classList;
    }

    public Boolean getIsCombinedClass() {
        return isCombinedClass;
    }

    public void setIsCombinedClass(Boolean isCombinedClass) {
        this.isCombinedClass = isCombinedClass;
    }

    public String getCombinedCourseIds() {
        return combinedCourseIds;
    }

    public void setCombinedCourseIds(String combinedCourseIds) {
        this.combinedCourseIds = combinedCourseIds;
    }

    public String getConflictStatus() {
        return conflictStatus;
    }

    public void setConflictStatus(String conflictStatus) {
        this.conflictStatus = conflictStatus;
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

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }
}
