package com.campus.shared.constants;

/**
 * 角色权限常量类
 * 定义基于50个角色的权限表达式常量
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-20
 */
public final class RolePermissions {

    private RolePermissions() {
        // 工具类，禁止实例化
    }

    // ==================== 系统管理权限 ====================
    
    /**
     * 系统管理员权限 - 最高权限
     */
    public static final String SYSTEM_ADMIN = 
        "hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN')";

    /**
     * 校级领导权限 - 校长、副校长
     */
    public static final String SCHOOL_LEADERSHIP = 
        "hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL')";

    /**
     * 高级管理权限 - 包含处级干部
     */
    public static final String SENIOR_MANAGEMENT = 
        "hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', " +
        "'ROLE_ACADEMIC_DIRECTOR', 'ROLE_STUDENT_AFFAIRS_DIRECTOR', 'ROLE_HR_DIRECTOR', " +
        "'ROLE_FINANCE_DIRECTOR', 'ROLE_LOGISTICS_DIRECTOR', 'ROLE_IT_DIRECTOR', " +
        "'ROLE_INTERNATIONAL_DIRECTOR', 'ROLE_ADMISSION_DIRECTOR')";

    // ==================== 教务管理权限 ====================

    /**
     * 教务管理权限 - 教学相关管理
     */
    public static final String ACADEMIC_MANAGEMENT = 
        "hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', " +
        "'ROLE_ACADEMIC_DIRECTOR', 'ROLE_DEAN', 'ROLE_VICE_DEAN', 'ROLE_DEPARTMENT_HEAD', " +
        "'ROLE_VICE_DEPARTMENT_HEAD', 'ROLE_TEACHING_GROUP_HEAD', 'ROLE_ACADEMIC_STAFF')";

    /**
     * 教学人员权限 - 所有教学岗位
     */
    public static final String TEACHING_STAFF = 
        "hasAnyRole('ROLE_TEACHER', 'ROLE_PROFESSOR', 'ROLE_ASSOCIATE_PROFESSOR', 'ROLE_LECTURER', " +
        "'ROLE_ASSISTANT', 'ROLE_SUPERVISOR', 'ROLE_VISITING_TEACHER')";

    /**
     * 教学管理和教学人员权限
     */
    public static final String ACADEMIC_AND_TEACHING = 
        "hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', " +
        "'ROLE_ACADEMIC_DIRECTOR', 'ROLE_DEAN', 'ROLE_VICE_DEAN', 'ROLE_DEPARTMENT_HEAD', " +
        "'ROLE_VICE_DEPARTMENT_HEAD', 'ROLE_TEACHING_GROUP_HEAD', 'ROLE_ACADEMIC_STAFF', " +
        "'ROLE_TEACHER', 'ROLE_PROFESSOR', 'ROLE_ASSOCIATE_PROFESSOR', 'ROLE_LECTURER', " +
        "'ROLE_ASSISTANT', 'ROLE_SUPERVISOR', 'ROLE_VISITING_TEACHER')";

    /**
     * 课程管理权限
     */
    public static final String COURSE_MANAGEMENT = 
        "hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_ACADEMIC_DIRECTOR', " +
        "'ROLE_DEAN', 'ROLE_DEPARTMENT_HEAD', 'ROLE_TEACHING_GROUP_HEAD', 'ROLE_ACADEMIC_STAFF')";

    /**
     * 成绩管理权限
     */
    public static final String GRADE_MANAGEMENT = 
        "hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_ACADEMIC_DIRECTOR', " +
        "'ROLE_DEAN', 'ROLE_DEPARTMENT_HEAD', 'ROLE_TEACHING_GROUP_HEAD', 'ROLE_TEACHER', " +
        "'ROLE_PROFESSOR', 'ROLE_ASSOCIATE_PROFESSOR', 'ROLE_LECTURER', 'ROLE_ACADEMIC_STAFF')";

    // ==================== 学生事务权限 ====================

    /**
     * 学生事务管理权限
     */
    public static final String STUDENT_AFFAIRS_MANAGEMENT = 
        "hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', " +
        "'ROLE_STUDENT_AFFAIRS_DIRECTOR', 'ROLE_DEAN', 'ROLE_VICE_DEAN', 'ROLE_DEPARTMENT_HEAD', " +
        "'ROLE_STUDENT_AFFAIRS_STAFF')";

    /**
     * 学生工作权限 - 班主任、辅导员等
     */
    public static final String STUDENT_WORK = 
        "hasAnyRole('ROLE_CLASS_TEACHER', 'ROLE_COUNSELOR', 'ROLE_STUDENT_AFFAIRS_STAFF')";

    /**
     * 学生管理权限 - 包含学生工作和管理
     */
    public static final String STUDENT_MANAGEMENT = 
        "hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', " +
        "'ROLE_STUDENT_AFFAIRS_DIRECTOR', 'ROLE_DEAN', 'ROLE_VICE_DEAN', 'ROLE_DEPARTMENT_HEAD', " +
        "'ROLE_CLASS_TEACHER', 'ROLE_COUNSELOR', 'ROLE_STUDENT_AFFAIRS_STAFF')";

    /**
     * 考勤管理权限
     */
    public static final String ATTENDANCE_MANAGEMENT = 
        "hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_ACADEMIC_DIRECTOR', " +
        "'ROLE_STUDENT_AFFAIRS_DIRECTOR', 'ROLE_DEAN', 'ROLE_DEPARTMENT_HEAD', 'ROLE_CLASS_TEACHER', " +
        "'ROLE_COUNSELOR', 'ROLE_TEACHER', 'ROLE_PROFESSOR', 'ROLE_ASSOCIATE_PROFESSOR', 'ROLE_LECTURER')";

    // ==================== 财务管理权限 ====================

    /**
     * 财务管理权限
     */
    public static final String FINANCE_MANAGEMENT = 
        "hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', " +
        "'ROLE_FINANCE_DIRECTOR', 'ROLE_FINANCE_STAFF')";

    /**
     * 财务查看权限
     */
    public static final String FINANCE_VIEW = 
        "hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', " +
        "'ROLE_FINANCE_DIRECTOR', 'ROLE_FINANCE_STAFF', 'ROLE_DEAN', 'ROLE_DEPARTMENT_HEAD')";

    // ==================== 人事管理权限 ====================

    /**
     * 人事管理权限
     */
    public static final String HR_MANAGEMENT = 
        "hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', " +
        "'ROLE_HR_DIRECTOR', 'ROLE_HR_STAFF')";

    /**
     * 教师管理权限
     */
    public static final String TEACHER_MANAGEMENT = 
        "hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', " +
        "'ROLE_HR_DIRECTOR', 'ROLE_ACADEMIC_DIRECTOR', 'ROLE_DEAN', 'ROLE_DEPARTMENT_HEAD', " +
        "'ROLE_HR_STAFF')";

    // ==================== 学生权限 ====================

    /**
     * 学生权限 - 所有学生类型
     */
    public static final String STUDENT_ACCESS = 
        "hasAnyRole('ROLE_STUDENT', 'ROLE_UNDERGRADUATE', 'ROLE_GRADUATE', 'ROLE_MASTER_STUDENT', " +
        "'ROLE_PHD_STUDENT', 'ROLE_INTERNATIONAL_STUDENT', 'ROLE_EXCHANGE_STUDENT', 'ROLE_AUDITOR')";

    /**
     * 学生干部权限
     */
    public static final String STUDENT_LEADER = 
        "hasAnyRole('ROLE_STUDENT_LEADER', 'ROLE_CLASS_MONITOR')";

    /**
     * 学生签到权限 - 包含学生和学生干部
     */
    public static final String STUDENT_CHECKIN = 
        "hasAnyRole('ROLE_STUDENT', 'ROLE_UNDERGRADUATE', 'ROLE_GRADUATE', 'ROLE_MASTER_STUDENT', " +
        "'ROLE_PHD_STUDENT', 'ROLE_INTERNATIONAL_STUDENT', 'ROLE_EXCHANGE_STUDENT', 'ROLE_AUDITOR', " +
        "'ROLE_STUDENT_LEADER', 'ROLE_CLASS_MONITOR')";

    // ==================== 家长权限 ====================

    /**
     * 家长权限 - 查看学生信息
     */
    public static final String PARENT_ACCESS = "hasRole('ROLE_PARENT')";

    // ==================== 综合权限 ====================

    /**
     * 管理员和教学人员权限
     */
    public static final String ADMIN_AND_TEACHING = 
        "hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', " +
        "'ROLE_ACADEMIC_DIRECTOR', 'ROLE_STUDENT_AFFAIRS_DIRECTOR', 'ROLE_DEAN', 'ROLE_VICE_DEAN', " +
        "'ROLE_DEPARTMENT_HEAD', 'ROLE_TEACHING_GROUP_HEAD', 'ROLE_TEACHER', 'ROLE_PROFESSOR', " +
        "'ROLE_ASSOCIATE_PROFESSOR', 'ROLE_LECTURER', 'ROLE_CLASS_TEACHER', 'ROLE_COUNSELOR')";

    /**
     * 所有教职工权限
     */
    public static final String ALL_STAFF = 
        "hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', " +
        "'ROLE_ACADEMIC_DIRECTOR', 'ROLE_STUDENT_AFFAIRS_DIRECTOR', 'ROLE_HR_DIRECTOR', " +
        "'ROLE_FINANCE_DIRECTOR', 'ROLE_LOGISTICS_DIRECTOR', 'ROLE_IT_DIRECTOR', " +
        "'ROLE_DEAN', 'ROLE_VICE_DEAN', 'ROLE_DEPARTMENT_HEAD', 'ROLE_TEACHING_GROUP_HEAD', " +
        "'ROLE_TEACHER', 'ROLE_PROFESSOR', 'ROLE_ASSOCIATE_PROFESSOR', 'ROLE_LECTURER', " +
        "'ROLE_CLASS_TEACHER', 'ROLE_COUNSELOR', 'ROLE_HR_STAFF', 'ROLE_ACADEMIC_STAFF', " +
        "'ROLE_FINANCE_STAFF', 'ROLE_STUDENT_AFFAIRS_STAFF', 'ROLE_LOGISTICS_STAFF')";

    /**
     * 统计查看权限 - 管理层和相关工作人员
     */
    public static final String STATISTICS_VIEW = 
        "hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL', 'ROLE_VICE_PRINCIPAL', " +
        "'ROLE_ACADEMIC_DIRECTOR', 'ROLE_STUDENT_AFFAIRS_DIRECTOR', 'ROLE_FINANCE_DIRECTOR', " +
        "'ROLE_HR_DIRECTOR', 'ROLE_DEAN', 'ROLE_VICE_DEAN', 'ROLE_DEPARTMENT_HEAD')";

    /**
     * 批量操作权限 - 仅高级管理员
     */
    public static final String BATCH_OPERATIONS = 
        "hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PRINCIPAL')";
}
