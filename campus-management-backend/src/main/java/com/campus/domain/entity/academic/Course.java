package com.campus.domain.entity.academic;

import com.campus.domain.entity.auth.User;
import com.campus.domain.entity.infrastructure.BaseEntity;
import com.campus.domain.entity.organization.Department;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;


import java.math.BigDecimal;
import java.util.List;

/**
 * 课程实体类
 * 管理课程基本信息、学分、教师等
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_course", indexes = {
    @Index(name = "idx_course_code", columnList = "course_code"),
    @Index(name = "idx_department_id", columnList = "department_id"),
    @Index(name = "idx_teacher_id", columnList = "teacher_id"),
    @Index(name = "idx_course_type", columnList = "course_type"),
    @Index(name = "idx_semester", columnList = "semester"),
    @Index(name = "idx_status_deleted", columnList = "status,deleted")
})
public class Course extends BaseEntity {

    /**
     * 课程名称
     */
    @NotBlank(message = "课程名称不能为空")
    @Size(max = 100, message = "课程名称长度不能超过100个字符")
    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;

    /**
     * 课程代码
     */
    @NotBlank(message = "课程代码不能为空")
    @Size(max = 20, message = "课程代码长度不能超过20个字符")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "课程代码只能包含大写字母和数字")
    @Column(name = "course_code", nullable = false, unique = true, length = 20)
    private String courseCode;

    /**
     * 课程英文名称
     */
    @Size(max = 100, message = "课程英文名称长度不能超过100个字符")
    @Column(name = "course_name_en", length = 100)
    private String courseNameEn;

    /**
     * 学分
     */
    @NotNull(message = "学分不能为空")
    @DecimalMin(value = "0.5", message = "学分不能小于0.5")
    @DecimalMax(value = "10.0", message = "学分不能大于10.0")
    @Column(name = "credits", nullable = false, precision = 3, scale = 1)
    private BigDecimal credits;

    /**
     * 学时
     */
    @Min(value = 1, message = "学时不能小于1")
    @Max(value = 200, message = "学时不能大于200")
    @Column(name = "hours")
    private Integer hours;

    /**
     * 理论学时
     */
    @Min(value = 0, message = "理论学时不能小于0")
    @Column(name = "theory_hours")
    private Integer theoryHours;

    /**
     * 实验学时
     */
    @Min(value = 0, message = "实验学时不能小于0")
    @Column(name = "lab_hours")
    private Integer labHours;

    /**
     * 实习学时
     */
    @Min(value = 0, message = "实习学时不能小于0")
    @Column(name = "practice_hours")
    private Integer practiceHours;

    /**
     * 所属院系ID
     */
    @Column(name = "department_id")
    private Long departmentId;

    /**
     * 课程描述
     */
    @Size(max = 1000, message = "课程描述长度不能超过1000个字符")
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * 课程目标
     */
    @Size(max = 1000, message = "课程目标长度不能超过1000个字符")
    @Column(name = "objectives", length = 1000)
    private String objectives;

    /**
     * 主讲教师ID
     */
    @Column(name = "teacher_id")
    private Long teacherId;

    /**
     * 课程类型
     * 必修课、选修课、限选课、实践课等
     */
    @NotBlank(message = "课程类型不能为空")
    @Size(max = 20, message = "课程类型长度不能超过20个字符")
    @Column(name = "course_type", nullable = false, length = 20)
    private String courseType;

    /**
     * 课程性质
     * 公共基础课、专业基础课、专业核心课、专业选修课等
     */
    @Size(max = 20, message = "课程性质长度不能超过20个字符")
    @Column(name = "course_nature", length = 20)
    private String courseNature;

    /**
     * 适用专业
     */
    @Size(max = 200, message = "适用专业长度不能超过200个字符")
    @Column(name = "applicable_majors", length = 200)
    private String applicableMajors;

    /**
     * 先修课程
     */
    @Size(max = 200, message = "先修课程长度不能超过200个字符")
    @Column(name = "prerequisites", length = 200)
    private String prerequisites;

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
     * 上课时间
     */
    @Size(max = 100, message = "上课时间长度不能超过100个字符")
    @Column(name = "class_time", length = 100)
    private String classTime;

    /**
     * 上课地点
     */
    @Size(max = 100, message = "上课地点长度不能超过100个字符")
    @Column(name = "classroom", length = 100)
    private String classroom;

    /**
     * 最大学生数
     */
    @Min(value = 1, message = "最大学生数不能小于1")
    @Max(value = 500, message = "最大学生数不能大于500")
    @Column(name = "max_students")
    private Integer maxStudents;

    /**
     * 已选学生数
     */
    @Min(value = 0, message = "已选学生数不能小于0")
    @Column(name = "enrolled_students")
    private Integer enrolledStudents = 0;

    /**
     * 选课开始时间
     */
    @Column(name = "selection_start_time")
    private java.time.LocalDateTime selectionStartTime;

    /**
     * 选课结束时间
     */
    @Column(name = "selection_end_time")
    private java.time.LocalDateTime selectionEndTime;

    /**
     * 考核方式
     */
    @Size(max = 50, message = "考核方式长度不能超过50个字符")
    @Column(name = "assessment_method", length = 50)
    private String assessmentMethod;

    /**
     * 平时成绩占比
     */
    @DecimalMin(value = "0.0", message = "平时成绩占比不能小于0")
    @DecimalMax(value = "1.0", message = "平时成绩占比不能大于1")
    @Column(name = "regular_score_ratio", precision = 3, scale = 2)
    private BigDecimal regularScoreRatio;

    /**
     * 期中成绩占比
     */
    @DecimalMin(value = "0.0", message = "期中成绩占比不能小于0")
    @DecimalMax(value = "1.0", message = "期中成绩占比不能大于1")
    @Column(name = "midterm_score_ratio", precision = 3, scale = 2)
    private BigDecimal midtermScoreRatio;

    /**
     * 期末成绩占比
     */
    @DecimalMin(value = "0.0", message = "期末成绩占比不能小于0")
    @DecimalMax(value = "1.0", message = "期末成绩占比不能大于1")
    @Column(name = "final_score_ratio", precision = 3, scale = 2)
    private BigDecimal finalScoreRatio;

    /**
     * 教材信息
     */
    @Size(max = 500, message = "教材信息长度不能超过500个字符")
    @Column(name = "textbook", length = 500)
    private String textbook;

    /**
     * 参考书目
     */
    @Size(max = 1000, message = "参考书目长度不能超过1000个字符")
    @Column(name = "reference_books", length = 1000)
    private String references;

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
     * 所属院系
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", insertable = false, updatable = false)
    @JsonIgnore
    private Department department;

    /**
     * 主讲教师
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    @JsonIgnore
    private User teacher;

    /**
     * 课程表安排
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private List<CourseSchedule> schedules;

    /**
     * 选课记录
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private List<CourseSelection> selections;

    /**
     * 成绩记录
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private List<Grade> grades;

    // ================================
    // 构造函数
    // ================================

    public Course() {
        super();
    }

    public Course(String courseName, String courseCode, BigDecimal credits) {
        this();
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.credits = credits;
    }

    public Course(String courseName, String courseCode, BigDecimal credits, String courseType, String semester) {
        this(courseName, courseCode, credits);
        this.courseType = courseType;
        this.semester = semester;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 获取教师姓名
     */
    public String getTeacherName() {
        try {
            return teacher != null ? teacher.getRealName() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取院系名称
     */
    public String getDepartmentName() {
        try {
            return department != null ? department.getDeptName() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查是否可以选课
     */
    public boolean canEnroll() {
        if (!isEnabled() || isDeleted()) {
            return false;
        }
        if (maxStudents != null && enrolledStudents != null) {
            return enrolledStudents < maxStudents;
        }
        return true;
    }

    /**
     * 检查选课时间是否有效
     */
    public boolean isSelectionTimeValid() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        if (selectionStartTime != null && now.isBefore(selectionStartTime)) {
            return false;
        }
        if (selectionEndTime != null && now.isAfter(selectionEndTime)) {
            return false;
        }
        return true;
    }

    /**
     * 增加选课人数
     */
    public boolean addEnrolledStudent() {
        if (!canEnroll()) {
            return false;
        }
        enrolledStudents = (enrolledStudents == null ? 0 : enrolledStudents) + 1;
        return true;
    }

    /**
     * 减少选课人数
     */
    public boolean removeEnrolledStudent() {
        if (enrolledStudents == null || enrolledStudents <= 0) {
            return false;
        }
        enrolledStudents--;
        return true;
    }

    /**
     * 获取剩余名额
     */
    public Integer getRemainingSlots() {
        if (maxStudents == null) {
            return null;
        }
        int enrolled = enrolledStudents != null ? enrolledStudents : 0;
        return Math.max(0, maxStudents - enrolled);
    }

    /**
     * 检查是否满员
     */
    public boolean isFull() {
        if (maxStudents == null) {
            return false;
        }
        int enrolled = enrolledStudents != null ? enrolledStudents : 0;
        return enrolled >= maxStudents;
    }

    /**
     * 获取课程类型文本
     */
    public String getCourseTypeText() {
        if (courseType == null) return "未知";
        return switch (courseType) {
            case "required" -> "必修课";
            case "elective" -> "选修课";
            case "limited_elective" -> "限选课";
            case "practice" -> "实践课";
            case "graduation_design" -> "毕业设计";
            default -> courseType;
        };
    }

    /**
     * 获取课程性质文本
     */
    public String getCourseNatureText() {
        if (courseNature == null) return "未知";
        return switch (courseNature) {
            case "public_basic" -> "公共基础课";
            case "major_basic" -> "专业基础课";
            case "major_core" -> "专业核心课";
            case "major_elective" -> "专业选修课";
            case "public_elective" -> "公共选修课";
            default -> courseNature;
        };
    }

    /**
     * 计算总学时（如果没有设置，根据学分计算）
     */
    public Integer getTotalHours() {
        if (hours != null) {
            return hours;
        }
        if (credits != null) {
            return credits.multiply(new BigDecimal(16)).intValue(); // 1学分 = 16学时
        }
        return null;
    }

    /**
     * 验证成绩占比总和是否为1
     */
    public boolean isScoreRatioValid() {
        BigDecimal total = BigDecimal.ZERO;
        if (regularScoreRatio != null) {
            total = total.add(regularScoreRatio);
        }
        if (midtermScoreRatio != null) {
            total = total.add(midtermScoreRatio);
        }
        if (finalScoreRatio != null) {
            total = total.add(finalScoreRatio);
        }
        return total.compareTo(BigDecimal.ONE) == 0;
    }

    // ================================
    // Getter/Setter 方法 (手动添加以解决Lombok问题)
    // ================================

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

    public BigDecimal getCredits() {
        return credits;
    }

    public void setCredits(BigDecimal credits) {
        this.credits = credits;
    }

    /**
     * 获取学分（兼容性方法）
     */
    public BigDecimal getCredit() {
        return credits;
    }

    /**
     * 设置学分（兼容性方法）
     */
    public void setCredit(BigDecimal credit) {
        this.credits = credit;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
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

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public Integer getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }

    public Integer getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(Integer enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
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

    public String getTextbook() {
        return textbook;
    }

    public void setTextbook(String textbook) {
        this.textbook = textbook;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public String getAssessmentMethod() {
        return assessmentMethod;
    }

    public void setAssessmentMethod(String assessmentMethod) {
        this.assessmentMethod = assessmentMethod;
    }

    public BigDecimal getRegularScoreRatio() {
        return regularScoreRatio;
    }

    public void setRegularScoreRatio(BigDecimal regularScoreRatio) {
        this.regularScoreRatio = regularScoreRatio;
    }

    public BigDecimal getMidtermScoreRatio() {
        return midtermScoreRatio;
    }

    public void setMidtermScoreRatio(BigDecimal midtermScoreRatio) {
        this.midtermScoreRatio = midtermScoreRatio;
    }

    public BigDecimal getFinalScoreRatio() {
        return finalScoreRatio;
    }

    public void setFinalScoreRatio(BigDecimal finalScoreRatio) {
        this.finalScoreRatio = finalScoreRatio;
    }

    public String getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }

    public java.time.LocalDateTime getSelectionStartTime() {
        return selectionStartTime;
    }

    public void setSelectionStartTime(java.time.LocalDateTime selectionStartTime) {
        this.selectionStartTime = selectionStartTime;
    }

    public java.time.LocalDateTime getSelectionEndTime() {
        return selectionEndTime;
    }

    public void setSelectionEndTime(java.time.LocalDateTime selectionEndTime) {
        this.selectionEndTime = selectionEndTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public List<CourseSchedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<CourseSchedule> schedules) {
        this.schedules = schedules;
    }

    public List<CourseSelection> getSelections() {
        return selections;
    }

    public void setSelections(List<CourseSelection> selections) {
        this.selections = selections;
    }

    public List<Grade> getGrades() {
        return grades;
    }

    public void setGrades(List<Grade> grades) {
        this.grades = grades;
    }

    // 继续添加缺失的getter/setter方法

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public java.time.LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
