package com.campus.domain.entity.organization;

import com.campus.domain.entity.auth.User;
import com.campus.domain.entity.infrastructure.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;


import java.time.LocalDate;
import java.util.List;

/**
 * 班级实体类
 * 管理学校的班级信息，包括班级基本信息、班主任、学生等
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_class", indexes = {
    @Index(name = "idx_class_code", columnList = "class_code"),
    @Index(name = "idx_department_id", columnList = "department_id"),
    @Index(name = "idx_head_teacher_id", columnList = "head_teacher_id"),
    @Index(name = "idx_grade", columnList = "grade"),
    @Index(name = "idx_enrollment_year", columnList = "enrollment_year"),
    @Index(name = "idx_status_deleted", columnList = "status,deleted")
})
public class SchoolClass extends BaseEntity {

    /**
     * 班级名称
     */
    @NotBlank(message = "班级名称不能为空")
    @Size(max = 50, message = "班级名称长度不能超过50个字符")
    @Column(name = "class_name", nullable = false, length = 50)
    private String className;

    /**
     * 班级代码
     */
    @NotBlank(message = "班级代码不能为空")
    @Size(max = 20, message = "班级代码长度不能超过20个字符")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "班级代码只能包含大写字母和数字")
    @Column(name = "class_code", nullable = false, unique = true, length = 20)
    private String classCode;

    /**
     * 年级
     */
    @NotBlank(message = "年级不能为空")
    @Size(max = 20, message = "年级长度不能超过20个字符")
    @Column(name = "grade", nullable = false, length = 20)
    private String grade;

    /**
     * 专业
     */
    @Size(max = 100, message = "专业长度不能超过100个字符")
    @Column(name = "major", length = 100)
    private String major;

    /**
     * 所属院系ID
     */
    @Column(name = "department_id")
    private Long departmentId;

    /**
     * 班主任ID
     */
    @Column(name = "head_teacher_id")
    private Long headTeacherId;

    /**
     * 副班主任ID
     */
    @Column(name = "assistant_teacher_id")
    private Long assistantTeacherId;

    /**
     * 入学年份
     */
    @Column(name = "enrollment_year")
    private Integer enrollmentYear;

    /**
     * 入学日期
     */
    @Column(name = "enrollment_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate enrollmentDate;

    /**
     * 预计毕业日期
     */
    @Column(name = "expected_graduation_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedGraduationDate;

    /**
     * 班级类型
     * 普通班、实验班、国际班等
     */
    @Size(max = 20, message = "班级类型长度不能超过20个字符")
    @Column(name = "class_type", length = 20)
    private String classType;

    /**
     * 学制（年）
     */
    @Min(value = 1, message = "学制不能小于1年")
    @Max(value = 8, message = "学制不能大于8年")
    @Column(name = "academic_system")
    private Integer academicSystem;

    /**
     * 班级描述
     */
    @Size(max = 500, message = "描述长度不能超过500个字符")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 班级口号
     */
    @Size(max = 200, message = "班级口号长度不能超过200个字符")
    @Column(name = "motto", length = 200)
    private String motto;

    /**
     * 班级目标
     */
    @Size(max = 500, message = "班级目标长度不能超过500个字符")
    @Column(name = "goals", length = 500)
    private String goals;

    /**
     * 最大学生数
     */
    @Min(value = 1, message = "最大学生数不能小于1")
    @Max(value = 100, message = "最大学生数不能大于100")
    @Column(name = "max_students")
    private Integer maxStudents;

    /**
     * 当前学生数
     */
    @Min(value = 0, message = "当前学生数不能小于0")
    @Column(name = "student_count", columnDefinition = "INT DEFAULT 0")
    private Integer studentCount = 0;

    /**
     * 男生人数
     */
    @Min(value = 0, message = "男生人数不能小于0")
    @Column(name = "male_count", columnDefinition = "INT DEFAULT 0")
    private Integer maleCount = 0;

    /**
     * 女生人数
     */
    @Min(value = 0, message = "女生人数不能小于0")
    @Column(name = "female_count", columnDefinition = "INT DEFAULT 0")
    private Integer femaleCount = 0;

    /**
     * 教室
     */
    @Size(max = 50, message = "教室长度不能超过50个字符")
    @Column(name = "classroom", length = 50)
    private String classroom;

    /**
     * 班级状态
     * 1: 正常, 2: 毕业, 3: 解散, 0: 禁用
     */
    @Column(name = "class_status", nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    private Integer classStatus = 1;

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
     * 班主任
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_teacher_id", insertable = false, updatable = false)
    @JsonIgnore
    private User headTeacher;

    /**
     * 副班主任
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assistant_teacher_id", insertable = false, updatable = false)
    @JsonIgnore
    private User assistantTeacher;

    /**
     * 班级学生列表
     */
    @OneToMany(mappedBy = "schoolClass", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Student> students;

    // 注意：课程安排通过classList字段关联，不使用直接的实体关联
    // 因为支持合班教学，一个课程安排可能对应多个班级

    // ================================
    // 构造函数
    // ================================

    public SchoolClass() {
        super();
    }

    public SchoolClass(String className, String classCode, String grade) {
        this();
        this.className = className;
        this.classCode = classCode;
        this.grade = grade;
    }

    public SchoolClass(String className, String classCode, String grade, String major, Long departmentId) {
        this(className, classCode, grade);
        this.major = major;
        this.departmentId = departmentId;
    }

    // ================================
    // 业务方法
    // ================================

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
     * 获取班主任姓名
     */
    public String getHeadTeacherName() {
        try {
            return headTeacher != null ? headTeacher.getRealName() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取副班主任姓名
     */
    public String getAssistantTeacherName() {
        try {
            return assistantTeacher != null ? assistantTeacher.getRealName() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查班级是否正常
     */
    public boolean isActive() {
        return classStatus != null && classStatus == 1;
    }

    /**
     * 检查班级是否已毕业
     */
    public boolean isGraduated() {
        return classStatus != null && classStatus == 2;
    }

    /**
     * 检查班级是否已解散
     */
    public boolean isDisbanded() {
        return classStatus != null && classStatus == 3;
    }

    /**
     * 检查是否可以添加学生
     */
    public boolean canAddStudent() {
        if (!isActive() || !isEnabled()) {
            return false;
        }
        if (maxStudents != null && studentCount != null) {
            return studentCount < maxStudents;
        }
        return true;
    }

    /**
     * 增加学生数量
     */
    public boolean addStudent(String gender) {
        if (!canAddStudent()) {
            return false;
        }
        
        studentCount = (studentCount == null ? 0 : studentCount) + 1;
        
        if ("男".equals(gender)) {
            maleCount = (maleCount == null ? 0 : maleCount) + 1;
        } else if ("女".equals(gender)) {
            femaleCount = (femaleCount == null ? 0 : femaleCount) + 1;
        }
        
        return true;
    }

    /**
     * 减少学生数量
     */
    public boolean removeStudent(String gender) {
        if (studentCount == null || studentCount <= 0) {
            return false;
        }
        
        studentCount--;
        
        if ("男".equals(gender) && maleCount != null && maleCount > 0) {
            maleCount--;
        } else if ("女".equals(gender) && femaleCount != null && femaleCount > 0) {
            femaleCount--;
        }
        
        return true;
    }

    /**
     * 获取剩余名额
     */
    public Integer getRemainingSlots() {
        if (maxStudents == null) {
            return null;
        }
        int current = studentCount != null ? studentCount : 0;
        return Math.max(0, maxStudents - current);
    }

    /**
     * 检查是否满员
     */
    public boolean isFull() {
        if (maxStudents == null) {
            return false;
        }
        int current = studentCount != null ? studentCount : 0;
        return current >= maxStudents;
    }

    /**
     * 计算在校年数
     */
    public int getYearsInSchool() {
        if (enrollmentDate == null) {
            return 0;
        }
        LocalDate now = expectedGraduationDate != null ? expectedGraduationDate : LocalDate.now();
        return now.getYear() - enrollmentDate.getYear();
    }

    /**
     * 毕业班级
     */
    public void graduate() {
        this.classStatus = 2;
        this.disable();
    }

    /**
     * 解散班级
     */
    public void disband() {
        this.classStatus = 3;
        this.disable();
    }

    /**
     * 恢复班级
     */
    public void restore() {
        this.classStatus = 1;
        this.enable();
    }

    /**
     * 获取班级状态文本
     */
    public String getClassStatusText() {
        if (classStatus == null) return "未知";
        return switch (classStatus) {
            case 1 -> "正常";
            case 2 -> "毕业";
            case 3 -> "解散";
            default -> "未知";
        };
    }

    /**
     * 获取班级类型文本
     */
    public String getClassTypeText() {
        if (classType == null) return "普通班";
        return switch (classType) {
            case "normal" -> "普通班";
            case "experimental" -> "实验班";
            case "international" -> "国际班";
            case "honors" -> "荣誉班";
            case "bilingual" -> "双语班";
            default -> classType;
        };
    }

    /**
     * 获取班级全名
     */
    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (grade != null) {
            sb.append(grade);
        }
        if (major != null) {
            sb.append(" ").append(major);
        }
        if (className != null) {
            sb.append(" ").append(className);
        }
        return sb.toString().trim();
    }

    // ================================
    // 兼容性方法
    // ================================

    /**
     * 获取班主任姓名（兼容旧版本）
     */
    public String getTeacherName() {
        return getHeadTeacherName();
    }

    /**
     * 从班级名称提取专业（兼容旧版本）
     */
    public String getMajor() {
        if (major != null) {
            return major;
        }

        if (className == null || className.isEmpty()) {
            return "未分配";
        }

        // 基于常见的班级命名规则提取专业
        if (className.contains("计算机科学") || className.contains("计科")) {
            return "计算机科学与技术";
        } else if (className.contains("软件工程") || className.contains("软工")) {
            return "软件工程";
        } else if (className.contains("信息安全") || className.contains("信安")) {
            return "信息安全";
        } else if (className.contains("数据科学") || className.contains("数科")) {
            return "数据科学与大数据技术";
        } else if (className.contains("人工智能") || className.contains("AI")) {
            return "人工智能";
        } else if (className.contains("网络工程") || className.contains("网工")) {
            return "网络工程";
        } else {
            return "其他专业";
        }
    }

    // ================================
    // Getter/Setter 方法 (手动添加以解决Lombok问题)
    // ================================

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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getHeadTeacherId() {
        return headTeacherId;
    }

    public void setHeadTeacherId(Long headTeacherId) {
        this.headTeacherId = headTeacherId;
    }

    public Long getAssistantTeacherId() {
        return assistantTeacherId;
    }

    public void setAssistantTeacherId(Long assistantTeacherId) {
        this.assistantTeacherId = assistantTeacherId;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public Integer getClassStatus() {
        return classStatus;
    }

    public void setClassStatus(Integer classStatus) {
        this.classStatus = classStatus;
    }

    public Integer getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public Integer getMaleCount() {
        return maleCount;
    }

    public void setMaleCount(Integer maleCount) {
        this.maleCount = maleCount;
    }

    public Integer getFemaleCount() {
        return femaleCount;
    }

    public void setFemaleCount(Integer femaleCount) {
        this.femaleCount = femaleCount;
    }

    public java.time.LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(java.time.LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public java.time.LocalDate getExpectedGraduationDate() {
        return expectedGraduationDate;
    }

    public void setExpectedGraduationDate(java.time.LocalDate expectedGraduationDate) {
        this.expectedGraduationDate = expectedGraduationDate;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
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

    public User getHeadTeacher() {
        return headTeacher;
    }

    public void setHeadTeacher(User headTeacher) {
        this.headTeacher = headTeacher;
    }

    public User getAssistantTeacher() {
        return assistantTeacher;
    }

    public void setAssistantTeacher(User assistantTeacher) {
        this.assistantTeacher = assistantTeacher;
    }

    public java.util.List<Student> getStudents() {
        return students;
    }

    public void setStudents(java.util.List<Student> students) {
        this.students = students;
    }

    // 课程安排相关方法已移除，因为使用classList字段进行关联

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public Integer getAcademicSystem() {
        return academicSystem;
    }

    public void setAcademicSystem(Integer academicSystem) {
        this.academicSystem = academicSystem;
    }

    public Integer getEnrollmentYear() {
        return enrollmentYear;
    }

    public void setEnrollmentYear(Integer enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
    }
}
