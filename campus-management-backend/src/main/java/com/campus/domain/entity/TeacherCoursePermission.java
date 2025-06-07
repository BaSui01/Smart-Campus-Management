package com.campus.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


import java.time.LocalDateTime;

/**
 * 教师课程权限实体类
 * 管理教师对课程和班级的访问权限
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_teacher_course_permission", indexes = {
    @Index(name = "idx_teacher_id", columnList = "teacher_id"),
    @Index(name = "idx_course_id", columnList = "course_id"),
    @Index(name = "idx_class_id", columnList = "class_id"),
    @Index(name = "idx_semester", columnList = "semester"),
    @Index(name = "idx_teacher_course", columnList = "teacher_id,course_id"),
    @Index(name = "idx_status_deleted", columnList = "status,deleted")
})
public class TeacherCoursePermission extends BaseEntity {

    /**
     * 教师ID
     */
    @NotNull(message = "教师ID不能为空")
    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    /**
     * 班级ID
     */
    @NotNull(message = "班级ID不能为空")
    @Column(name = "class_id", nullable = false)
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
     * 权限类型
     * teaching（教学）, grading（成绩录入）, assignment（作业管理）, exam（考试管理）, all（全部权限）
     */
    @NotBlank(message = "权限类型不能为空")
    @Size(max = 50, message = "权限类型长度不能超过50个字符")
    @Column(name = "permission_type", nullable = false, length = 50)
    private String permissionType = "teaching";

    /**
     * 是否可以查看学生信息
     */
    @Column(name = "can_view_students")
    private Boolean canViewStudents = true;

    /**
     * 是否可以录入成绩
     */
    @Column(name = "can_input_grades")
    private Boolean canInputGrades = true;

    /**
     * 是否可以修改成绩
     */
    @Column(name = "can_modify_grades")
    private Boolean canModifyGrades = true;

    /**
     * 是否可以发布作业
     */
    @Column(name = "can_publish_assignments")
    private Boolean canPublishAssignments = true;

    /**
     * 是否可以批改作业
     */
    @Column(name = "can_grade_assignments")
    private Boolean canGradeAssignments = true;

    /**
     * 是否可以创建考试
     */
    @Column(name = "can_create_exams")
    private Boolean canCreateExams = true;

    /**
     * 是否可以管理考试
     */
    @Column(name = "can_manage_exams")
    private Boolean canManageExams = true;

    /**
     * 是否可以查看课程表
     */
    @Column(name = "can_view_schedule")
    private Boolean canViewSchedule = true;

    /**
     * 是否可以发布公告
     */
    @Column(name = "can_publish_announcements")
    private Boolean canPublishAnnouncements = true;

    /**
     * 是否可以管理课程资源
     */
    @Column(name = "can_manage_resources")
    private Boolean canManageResources = true;

    /**
     * 生效时间
     */
    @Column(name = "effective_time")
    private LocalDateTime effectiveTime;

    /**
     * 过期时间
     */
    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;

    /**
     * 授权人ID
     */
    @Column(name = "granted_by")
    private Long grantedBy;

    /**
     * 授权时间
     */
    @Column(name = "granted_time")
    private LocalDateTime grantedTime;

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
     * 关联教师
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private User teacher;

    /**
     * 关联课程
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;

    /**
     * 关联班级
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", insertable = false, updatable = false)
    private SchoolClass clazz;

    /**
     * 关联授权人
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by", insertable = false, updatable = false)
    private User grantor;

    // ================================
    // 构造函数
    // ================================

    public TeacherCoursePermission() {
        super();
    }

    public TeacherCoursePermission(Long teacherId, Long courseId, Long classId, 
                                 String semester, Integer academicYear) {
        this();
        this.teacherId = teacherId;
        this.courseId = courseId;
        this.classId = classId;
        this.semester = semester;
        this.academicYear = academicYear;
        this.effectiveTime = LocalDateTime.now();
        this.grantedTime = LocalDateTime.now();
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 授权权限
     */
    public void grant(Long grantorId) {
        this.grantedBy = grantorId;
        this.grantedTime = LocalDateTime.now();
        this.effectiveTime = LocalDateTime.now();
        this.status = 1; // 激活状态
    }

    /**
     * 撤销权限
     */
    public void revoke() {
        this.status = 0; // 停用状态
        this.expiryTime = LocalDateTime.now();
    }

    /**
     * 检查权限是否有效
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return status == 1 && 
               (effectiveTime == null || now.isAfter(effectiveTime)) &&
               (expiryTime == null || now.isBefore(expiryTime));
    }

    /**
     * 检查权限是否已过期
     */
    public boolean isExpired() {
        return expiryTime != null && LocalDateTime.now().isAfter(expiryTime);
    }

    /**
     * 设置权限过期时间
     */
    public void setExpiry(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    /**
     * 设置全部权限
     */
    public void setAllPermissions(boolean granted) {
        this.canViewStudents = granted;
        this.canInputGrades = granted;
        this.canModifyGrades = granted;
        this.canPublishAssignments = granted;
        this.canGradeAssignments = granted;
        this.canCreateExams = granted;
        this.canManageExams = granted;
        this.canViewSchedule = granted;
        this.canPublishAnnouncements = granted;
        this.canManageResources = granted;
        this.permissionType = granted ? "all" : "none";
    }

    /**
     * 设置教学权限
     */
    public void setTeachingPermissions() {
        this.canViewStudents = true;
        this.canViewSchedule = true;
        this.canPublishAnnouncements = true;
        this.canManageResources = true;
        this.permissionType = "teaching";
    }

    /**
     * 设置成绩管理权限
     */
    public void setGradingPermissions() {
        this.canViewStudents = true;
        this.canInputGrades = true;
        this.canModifyGrades = true;
        this.permissionType = "grading";
    }

    /**
     * 设置作业管理权限
     */
    public void setAssignmentPermissions() {
        this.canViewStudents = true;
        this.canPublishAssignments = true;
        this.canGradeAssignments = true;
        this.permissionType = "assignment";
    }

    /**
     * 设置考试管理权限
     */
    public void setExamPermissions() {
        this.canViewStudents = true;
        this.canCreateExams = true;
        this.canManageExams = true;
        this.permissionType = "exam";
    }

    /**
     * 获取权限类型文本
     */
    public String getPermissionTypeText() {
        if (permissionType == null) return "未知";
        return switch (permissionType) {
            case "teaching" -> "教学权限";
            case "grading" -> "成绩录入";
            case "assignment" -> "作业管理";
            case "exam" -> "考试管理";
            case "all" -> "全部权限";
            case "none" -> "无权限";
            default -> permissionType;
        };
    }

    /**
     * 检查是否有指定权限
     */
    public boolean hasPermission(String permission) {
        if (!isValid()) return false;
        
        return switch (permission.toLowerCase()) {
            case "view_students" -> canViewStudents;
            case "input_grades" -> canInputGrades;
            case "modify_grades" -> canModifyGrades;
            case "publish_assignments" -> canPublishAssignments;
            case "grade_assignments" -> canGradeAssignments;
            case "create_exams" -> canCreateExams;
            case "manage_exams" -> canManageExams;
            case "view_schedule" -> canViewSchedule;
            case "publish_announcements" -> canPublishAnnouncements;
            case "manage_resources" -> canManageResources;
            default -> false;
        };
    }

    /**
     * 获取教师姓名
     */
    public String getTeacherName() {
        return teacher != null ? teacher.getRealName() : null;
    }

    /**
     * 获取课程名称
     */
    public String getCourseName() {
        return course != null ? course.getCourseName() : null;
    }

    /**
     * 获取班级名称
     */
    public String getClassName() {
        return clazz != null ? clazz.getClassName() : null;
    }

    /**
     * 获取授权人姓名
     */
    public String getGrantorName() {
        return grantor != null ? grantor.getRealName() : null;
    }

    /**
     * 获取权限摘要
     */
    public String getPermissionSummary() {
        StringBuilder summary = new StringBuilder();
        if (canViewStudents) summary.append("查看学生, ");
        if (canInputGrades) summary.append("录入成绩, ");
        if (canModifyGrades) summary.append("修改成绩, ");
        if (canPublishAssignments) summary.append("发布作业, ");
        if (canGradeAssignments) summary.append("批改作业, ");
        if (canCreateExams) summary.append("创建考试, ");
        if (canManageExams) summary.append("管理考试, ");
        if (canViewSchedule) summary.append("查看课表, ");
        if (canPublishAnnouncements) summary.append("发布公告, ");
        if (canManageResources) summary.append("管理资源, ");
        
        String result = summary.toString();
        return result.endsWith(", ") ? result.substring(0, result.length() - 2) : result;
    }

    /**
     * 获取权限状态描述
     */
    public String getStatusDescription() {
        if (!isValid()) {
            if (isExpired()) {
                return "已过期";
            } else if (status == 0) {
                return "已停用";
            } else {
                return "未生效";
            }
        }
        return "有效";
    }

    // ================================
    // 验证方法
    // ================================

    /**
     * 验证权限数据
     */
    @PrePersist
    @PreUpdate
    public void validatePermission() {
        // 确保过期时间在生效时间之后
        if (effectiveTime != null && expiryTime != null && expiryTime.isBefore(effectiveTime)) {
            throw new IllegalArgumentException("过期时间不能早于生效时间");
        }
        
        // 确保至少有一个权限被授予
        if (!canViewStudents && !canInputGrades && !canModifyGrades && 
            !canPublishAssignments && !canGradeAssignments && !canCreateExams && 
            !canManageExams && !canViewSchedule && !canPublishAnnouncements && 
            !canManageResources) {
            throw new IllegalArgumentException("至少需要授予一个权限");
        }
    }
// ================================
    // Getter and Setter methods
    // ================================

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
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

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public Boolean getCanViewStudents() {
        return canViewStudents;
    }

    public void setCanViewStudents(Boolean canViewStudents) {
        this.canViewStudents = canViewStudents;
    }

    public Boolean getCanInputGrades() {
        return canInputGrades;
    }

    public void setCanInputGrades(Boolean canInputGrades) {
        this.canInputGrades = canInputGrades;
    }

    public Boolean getCanModifyGrades() {
        return canModifyGrades;
    }

    public void setCanModifyGrades(Boolean canModifyGrades) {
        this.canModifyGrades = canModifyGrades;
    }

    public Boolean getCanPublishAssignments() {
        return canPublishAssignments;
    }

    public void setCanPublishAssignments(Boolean canPublishAssignments) {
        this.canPublishAssignments = canPublishAssignments;
    }

    public Boolean getCanGradeAssignments() {
        return canGradeAssignments;
    }

    public void setCanGradeAssignments(Boolean canGradeAssignments) {
        this.canGradeAssignments = canGradeAssignments;
    }

    public Boolean getCanCreateExams() {
        return canCreateExams;
    }

    public void setCanCreateExams(Boolean canCreateExams) {
        this.canCreateExams = canCreateExams;
    }

    public Boolean getCanManageExams() {
        return canManageExams;
    }

    public void setCanManageExams(Boolean canManageExams) {
        this.canManageExams = canManageExams;
    }

    public Boolean getCanViewSchedule() {
        return canViewSchedule;
    }

    public void setCanViewSchedule(Boolean canViewSchedule) {
        this.canViewSchedule = canViewSchedule;
    }

    public Boolean getCanPublishAnnouncements() {
        return canPublishAnnouncements;
    }

    public void setCanPublishAnnouncements(Boolean canPublishAnnouncements) {
        this.canPublishAnnouncements = canPublishAnnouncements;
    }

    public Boolean getCanManageResources() {
        return canManageResources;
    }

    public void setCanManageResources(Boolean canManageResources) {
        this.canManageResources = canManageResources;
    }

    public LocalDateTime getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(LocalDateTime effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public Long getGrantedBy() {
        return grantedBy;
    }

    public void setGrantedBy(Long grantedBy) {
        this.grantedBy = grantedBy;
    }

    public LocalDateTime getGrantedTime() {
        return grantedTime;
    }

    public void setGrantedTime(LocalDateTime grantedTime) {
        this.grantedTime = grantedTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public SchoolClass getClazz() {
        return clazz;
    }

    public void setClazz(SchoolClass clazz) {
        this.clazz = clazz;
    }

    public User getGrantor() {
        return grantor;
    }

    public void setGrantor(User grantor) {
        this.grantor = grantor;
    }

    // ================================
    // equals, hashCode, toString methods
    // ================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        
        TeacherCoursePermission that = (TeacherCoursePermission) o;
        
        if (teacherId != null ? !teacherId.equals(that.teacherId) : that.teacherId != null) return false;
        if (courseId != null ? !courseId.equals(that.courseId) : that.courseId != null) return false;
        if (classId != null ? !classId.equals(that.classId) : that.classId != null) return false;
        if (semester != null ? !semester.equals(that.semester) : that.semester != null) return false;
        if (academicYear != null ? !academicYear.equals(that.academicYear) : that.academicYear != null) return false;
        if (permissionType != null ? !permissionType.equals(that.permissionType) : that.permissionType != null) return false;
        if (canViewStudents != null ? !canViewStudents.equals(that.canViewStudents) : that.canViewStudents != null) return false;
        if (canInputGrades != null ? !canInputGrades.equals(that.canInputGrades) : that.canInputGrades != null) return false;
        if (canModifyGrades != null ? !canModifyGrades.equals(that.canModifyGrades) : that.canModifyGrades != null) return false;
        if (canPublishAssignments != null ? !canPublishAssignments.equals(that.canPublishAssignments) : that.canPublishAssignments != null) return false;
        if (canGradeAssignments != null ? !canGradeAssignments.equals(that.canGradeAssignments) : that.canGradeAssignments != null) return false;
        if (canCreateExams != null ? !canCreateExams.equals(that.canCreateExams) : that.canCreateExams != null) return false;
        if (canManageExams != null ? !canManageExams.equals(that.canManageExams) : that.canManageExams != null) return false;
        if (canViewSchedule != null ? !canViewSchedule.equals(that.canViewSchedule) : that.canViewSchedule != null) return false;
        if (canPublishAnnouncements != null ? !canPublishAnnouncements.equals(that.canPublishAnnouncements) : that.canPublishAnnouncements != null) return false;
        if (canManageResources != null ? !canManageResources.equals(that.canManageResources) : that.canManageResources != null) return false;
        if (effectiveTime != null ? !effectiveTime.equals(that.effectiveTime) : that.effectiveTime != null) return false;
        if (expiryTime != null ? !expiryTime.equals(that.expiryTime) : that.expiryTime != null) return false;
        if (grantedBy != null ? !grantedBy.equals(that.grantedBy) : that.grantedBy != null) return false;
        if (grantedTime != null ? !grantedTime.equals(that.grantedTime) : that.grantedTime != null) return false;
        return remarks != null ? remarks.equals(that.remarks) : that.remarks == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (teacherId != null ? teacherId.hashCode() : 0);
        result = 31 * result + (courseId != null ? courseId.hashCode() : 0);
        result = 31 * result + (classId != null ? classId.hashCode() : 0);
        result = 31 * result + (semester != null ? semester.hashCode() : 0);
        result = 31 * result + (academicYear != null ? academicYear.hashCode() : 0);
        result = 31 * result + (permissionType != null ? permissionType.hashCode() : 0);
        result = 31 * result + (canViewStudents != null ? canViewStudents.hashCode() : 0);
        result = 31 * result + (canInputGrades != null ? canInputGrades.hashCode() : 0);
        result = 31 * result + (canModifyGrades != null ? canModifyGrades.hashCode() : 0);
        result = 31 * result + (canPublishAssignments != null ? canPublishAssignments.hashCode() : 0);
        result = 31 * result + (canGradeAssignments != null ? canGradeAssignments.hashCode() : 0);
        result = 31 * result + (canCreateExams != null ? canCreateExams.hashCode() : 0);
        result = 31 * result + (canManageExams != null ? canManageExams.hashCode() : 0);
        result = 31 * result + (canViewSchedule != null ? canViewSchedule.hashCode() : 0);
        result = 31 * result + (canPublishAnnouncements != null ? canPublishAnnouncements.hashCode() : 0);
        result = 31 * result + (canManageResources != null ? canManageResources.hashCode() : 0);
        result = 31 * result + (effectiveTime != null ? effectiveTime.hashCode() : 0);
        result = 31 * result + (expiryTime != null ? expiryTime.hashCode() : 0);
        result = 31 * result + (grantedBy != null ? grantedBy.hashCode() : 0);
        result = 31 * result + (grantedTime != null ? grantedTime.hashCode() : 0);
        result = 31 * result + (remarks != null ? remarks.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TeacherCoursePermission{" +
                "id=" + getId() +
                ", teacherId=" + teacherId +
                ", courseId=" + courseId +
                ", classId=" + classId +
                ", semester='" + semester + '\'' +
                ", academicYear=" + academicYear +
                ", permissionType='" + permissionType + '\'' +
                ", canViewStudents=" + canViewStudents +
                ", canInputGrades=" + canInputGrades +
                ", canModifyGrades=" + canModifyGrades +
                ", canPublishAssignments=" + canPublishAssignments +
                ", canGradeAssignments=" + canGradeAssignments +
                ", canCreateExams=" + canCreateExams +
                ", canManageExams=" + canManageExams +
                ", canViewSchedule=" + canViewSchedule +
                ", canPublishAnnouncements=" + canPublishAnnouncements +
                ", canManageResources=" + canManageResources +
                ", effectiveTime=" + effectiveTime +
                ", expiryTime=" + expiryTime +
                ", grantedBy=" + grantedBy +
                ", grantedTime=" + grantedTime +
                ", remarks='" + remarks + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                ", status=" + getStatus() +
                ", deleted=" + getDeleted() +
                '}';
    }
}