package com.campus.domain.entity.academic;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import com.campus.domain.entity.auth.User;
import com.campus.domain.entity.infrastructure.BaseEntity;
import com.campus.domain.entity.organization.Student;

/**
 * 学生成绩实体类
 * 管理学生的课程成绩信息
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_grade", indexes = {
    @Index(name = "idx_student_id", columnList = "student_id"),
    @Index(name = "idx_course_id", columnList = "course_id"),
    @Index(name = "idx_teacher_id", columnList = "teacher_id"),
    @Index(name = "idx_semester", columnList = "semester"),
    @Index(name = "idx_student_course", columnList = "student_id,course_id,semester"),
    @Index(name = "idx_status_deleted", columnList = "status,deleted")
})
public class Grade extends BaseEntity {

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
     * 教师ID
     */
    @NotNull(message = "教师ID不能为空")
    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    /**
     * 班级ID
     */
    @Column(name = "class_id")
    private Long classId;

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
     * 平时成绩
     */
    @DecimalMin(value = "0.0", message = "平时成绩不能小于0")
    @DecimalMax(value = "100.0", message = "平时成绩不能大于100")
    @Column(name = "usual_score", precision = 5, scale = 2)
    private BigDecimal usualScore;

    /**
     * 期中成绩
     */
    @DecimalMin(value = "0.0", message = "期中成绩不能小于0")
    @DecimalMax(value = "100.0", message = "期中成绩不能大于100")
    @Column(name = "midterm_score", precision = 5, scale = 2)
    private BigDecimal midtermScore;

    /**
     * 期末成绩
     */
    @DecimalMin(value = "0.0", message = "期末成绩不能小于0")
    @DecimalMax(value = "100.0", message = "期末成绩不能大于100")
    @Column(name = "final_score", precision = 5, scale = 2)
    private BigDecimal finalScore;

    /**
     * 总成绩
     */
    @DecimalMin(value = "0.0", message = "总成绩不能小于0")
    @DecimalMax(value = "100.0", message = "总成绩不能大于100")
    @Column(name = "total_score", precision = 5, scale = 2)
    private BigDecimal totalScore;

    /**
     * 等级成绩（A+、A、A-、B+等）
     */
    @Size(max = 10, message = "等级成绩长度不能超过10个字符")
    @Column(name = "grade_level", length = 10)
    private String gradeLevel;

    /**
     * 绩点
     */
    @DecimalMin(value = "0.0", message = "绩点不能小于0")
    @DecimalMax(value = "4.0", message = "绩点不能大于4.0")
    @Column(name = "gpa", precision = 3, scale = 2)
    private BigDecimal gpa;

    /**
     * 学分
     */
    @DecimalMin(value = "0.0", message = "学分不能小于0")
    @Column(name = "credit", precision = 4, scale = 1)
    private BigDecimal credit;

    /**
     * 成绩状态
     * pending（待录入）, recorded（已录入）, confirmed（已确认）, modified（已修改）
     */
    @NotBlank(message = "成绩状态不能为空")
    @Size(max = 20, message = "成绩状态长度不能超过20个字符")
    @Column(name = "grade_status", nullable = false, length = 20)
    private String gradeStatus = "pending";

    /**
     * 是否及格
     */
    @Column(name = "is_passed")
    private Boolean isPassed = false;

    /**
     * 补考次数
     */
    @Min(value = 0, message = "补考次数不能小于0")
    @Column(name = "makeup_count")
    private Integer makeupCount = 0;

    /**
     * 最高成绩（包含补考）
     */
    @DecimalMin(value = "0.0", message = "最高成绩不能小于0")
    @DecimalMax(value = "100.0", message = "最高成绩不能大于100")
    @Column(name = "highest_score", precision = 5, scale = 2)
    private BigDecimal highestScore;

    /**
     * 录入时间
     */
    @Column(name = "record_time")
    private LocalDateTime recordTime;

    /**
     * 确认时间
     */
    @Column(name = "confirm_time")
    private LocalDateTime confirmTime;

    /**
     * 课程安排ID（可选）
     */
    @Column(name = "schedule_id")
    private Long scheduleId;

    /**
     * 选课记录ID（可选）
     */
    @Column(name = "selection_id")
    private Long selectionId;

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
     * 关联教师
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private User teacher;

    // ================================
    // 构造函数
    // ================================

    public Grade() {
        super();
    }

    public Grade(Long studentId, Long courseId, Long teacherId, String semester, Integer academicYear) {
        this();
        this.studentId = studentId;
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.semester = semester;
        this.academicYear = academicYear;
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

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
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

    public BigDecimal getUsualScore() {
        return usualScore;
    }

    public void setUsualScore(BigDecimal usualScore) {
        this.usualScore = usualScore;
    }

    public BigDecimal getMidtermScore() {
        return midtermScore;
    }

    public void setMidtermScore(BigDecimal midtermScore) {
        this.midtermScore = midtermScore;
    }

    public BigDecimal getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(BigDecimal finalScore) {
        this.finalScore = finalScore;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public BigDecimal getGpa() {
        return gpa;
    }

    public void setGpa(BigDecimal gpa) {
        this.gpa = gpa;
    }

    public BigDecimal getCredit() {
        return credit;
    }

    public void setCredit(BigDecimal credit) {
        this.credit = credit;
    }

    public String getGradeStatus() {
        return gradeStatus;
    }

    public void setGradeStatus(String gradeStatus) {
        this.gradeStatus = gradeStatus;
    }

    public Boolean getIsPassed() {
        return isPassed;
    }

    public void setIsPassed(Boolean isPassed) {
        this.isPassed = isPassed;
    }

    public Integer getMakeupCount() {
        return makeupCount;
    }

    public void setMakeupCount(Integer makeupCount) {
        this.makeupCount = makeupCount;
    }

    public BigDecimal getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(BigDecimal highestScore) {
        this.highestScore = highestScore;
    }

    public LocalDateTime getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(LocalDateTime recordTime) {
        this.recordTime = recordTime;
    }

    public LocalDateTime getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(LocalDateTime confirmTime) {
        this.confirmTime = confirmTime;
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

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(Long selectionId) {
        this.selectionId = selectionId;
    }

    // ================================
    // 兼容性方法（为了支持现有服务层代码）
    // ================================

    /**
     * 获取成绩（映射到总成绩）
     */
    public BigDecimal getScore() {
        return totalScore;
    }

    /**
     * 设置成绩（映射到总成绩）
     */
    public void setScore(BigDecimal score) {
        this.totalScore = score;
    }

    /**
     * 获取平时成绩（兼容性方法）
     */
    public BigDecimal getRegularScore() {
        return usualScore;
    }

    /**
     * 设置平时成绩（兼容性方法）
     */
    public void setRegularScore(BigDecimal regularScore) {
        this.usualScore = regularScore;
    }

    /**
     * 获取绩点（兼容性方法）
     */
    public BigDecimal getGradePoint() {
        return gpa;
    }

    /**
     * 设置绩点（兼容性方法）
     */
    public void setGradePoint(BigDecimal gradePoint) {
        this.gpa = gradePoint;
    }

    /**
     * 获取等级（兼容性方法）
     */
    public String getLevel() {
        return gradeLevel;
    }

    /**
     * 设置等级（兼容性方法）
     */
    public void setLevel(String level) {
        this.gradeLevel = level;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 计算总成绩
     * 平时成绩30% + 期中成绩30% + 期末成绩40%
     */
    public void calculateTotalScore() {
        if (usualScore != null && midtermScore != null && finalScore != null) {
            BigDecimal usual = usualScore.multiply(new BigDecimal("0.3"));
            BigDecimal midterm = midtermScore.multiply(new BigDecimal("0.3"));
            BigDecimal finalS = finalScore.multiply(new BigDecimal("0.4"));
            
            this.totalScore = usual.add(midterm).add(finalS)
                .setScale(2, RoundingMode.HALF_UP);
            
            // 更新最高成绩
            if (this.highestScore == null || this.totalScore.compareTo(this.highestScore) > 0) {
                this.highestScore = this.totalScore;
            }
            
            // 判断是否及格
            this.isPassed = this.totalScore.compareTo(new BigDecimal("60")) >= 0;
            
            // 计算等级和绩点
            calculateGradeLevel();
            calculateGPA();
        }
    }

    /**
     * 计算等级成绩
     */
    private void calculateGradeLevel() {
        if (totalScore == null) return;
        
        double score = totalScore.doubleValue();
        if (score >= 95) {
            gradeLevel = "A+";
        } else if (score >= 90) {
            gradeLevel = "A";
        } else if (score >= 85) {
            gradeLevel = "A-";
        } else if (score >= 82) {
            gradeLevel = "B+";
        } else if (score >= 78) {
            gradeLevel = "B";
        } else if (score >= 75) {
            gradeLevel = "B-";
        } else if (score >= 72) {
            gradeLevel = "C+";
        } else if (score >= 68) {
            gradeLevel = "C";
        } else if (score >= 64) {
            gradeLevel = "C-";
        } else if (score >= 60) {
            gradeLevel = "D";
        } else {
            gradeLevel = "F";
        }
    }

    /**
     * 计算绩点
     */
    private void calculateGPA() {
        if (totalScore == null) return;
        
        double score = totalScore.doubleValue();
        if (score >= 95) {
            gpa = new BigDecimal("4.0");
        } else if (score >= 90) {
            gpa = new BigDecimal("3.7");
        } else if (score >= 85) {
            gpa = new BigDecimal("3.3");
        } else if (score >= 82) {
            gpa = new BigDecimal("3.0");
        } else if (score >= 78) {
            gpa = new BigDecimal("2.7");
        } else if (score >= 75) {
            gpa = new BigDecimal("2.3");
        } else if (score >= 72) {
            gpa = new BigDecimal("2.0");
        } else if (score >= 68) {
            gpa = new BigDecimal("1.7");
        } else if (score >= 64) {
            gpa = new BigDecimal("1.3");
        } else if (score >= 60) {
            gpa = new BigDecimal("1.0");
        } else {
            gpa = new BigDecimal("0.0");
        }
    }

    /**
     * 获取成绩状态文本
     */
    public String getGradeStatusText() {
        if (gradeStatus == null) return "未知";
        return switch (gradeStatus) {
            case "pending" -> "待录入";
            case "recorded" -> "已录入";
            case "confirmed" -> "已确认";
            case "modified" -> "已修改";
            default -> gradeStatus;
        };
    }

    /**
     * 记录成绩
     */
    public void recordGrade() {
        this.gradeStatus = "recorded";
        this.recordTime = LocalDateTime.now();
        calculateTotalScore();
    }

    /**
     * 确认成绩
     */
    public void confirmGrade() {
        this.gradeStatus = "confirmed";
        this.confirmTime = LocalDateTime.now();
    }

    /**
     * 修改成绩
     */
    public void modifyGrade() {
        this.gradeStatus = "modified";
        calculateTotalScore();
    }

    /**
     * 是否可以修改
     */
    public boolean canModify() {
        return !"confirmed".equals(gradeStatus);
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
     * 获取教师姓名
     */
    public String getTeacherName() {
        return teacher != null ? teacher.getRealName() : null;
    }

    // ================================
    // 验证方法
    // ================================

    /**
     * 验证成绩数据
     */
    @PrePersist
    @PreUpdate
    public void validateGrade() {
        // 确保至少有一个成绩分项
        if (usualScore == null && midtermScore == null && finalScore == null) {
            throw new IllegalArgumentException("至少需要录入一项成绩");
        }
        
        // 如果有总成绩，确保计算正确
        if (totalScore != null) {
            calculateTotalScore();
        }
    }

    // ================================
    // toString、equals、hashCode方法
    // ================================

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + getId() +
                ", studentId=" + studentId +
                ", courseId=" + courseId +
                ", teacherId=" + teacherId +
                ", semester='" + semester + '\'' +
                ", totalScore=" + totalScore +
                ", gradeLevel='" + gradeLevel + '\'' +
                ", gradeStatus='" + gradeStatus + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Grade grade = (Grade) o;

        if (studentId != null ? !studentId.equals(grade.studentId) : grade.studentId != null) return false;
        if (courseId != null ? !courseId.equals(grade.courseId) : grade.courseId != null) return false;
        return semester != null ? semester.equals(grade.semester) : grade.semester == null;
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
