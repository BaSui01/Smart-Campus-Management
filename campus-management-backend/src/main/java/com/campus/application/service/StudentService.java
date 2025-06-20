package com.campus.application.service;

import com.campus.domain.entity.organization.Student;
import com.campus.application.dto.StudentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 学生服务接口
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
public interface StudentService {

    // ================================
    // 基础CRUD操作
    // ================================

    /**
     * 创建学生
     */
    Student createStudent(StudentDTO studentDTO);

    /**
     * 根据ID查找学生
     */
    Optional<Student> findById(Long id);

    /**
     * 根据学号查找学生
     */
    Optional<Student> findByStudentNo(String studentNo);

    /**
     * 更新学生信息
     */
    Student updateStudent(Long id, StudentDTO studentDTO);

    /**
     * 删除学生（软删除）
     */
    void deleteStudent(Long id);

    /**
     * 恢复学生
     */
    void restoreStudent(Long id);

    // ================================
    // 查询操作
    // ================================

    /**
     * 分页查询所有学生
     */
    Page<Student> findAll(Pageable pageable);

    /**
     * 根据条件分页查询学生
     */
    Page<Student> findByConditions(String keyword, String grade, String major, 
                                  Long classId, Integer status, Pageable pageable);

    /**
     * 根据班级查找学生
     */
    List<Student> findByClassId(Long classId);

    /**
     * 根据年级查找学生
     */
    List<Student> findByGrade(String grade);

    /**
     * 根据专业查找学生
     */
    List<Student> findByMajor(String major);

    /**
     * 根据入学年份查找学生
     */
    List<Student> findByEnrollmentYear(Integer enrollmentYear);

    // ================================
    // 统计操作
    // ================================

    /**
     * 统计学生总数
     */
    long count();

    /**
     * 统计启用学生数量
     */
    long countActiveStudents();

    /**
     * 统计各年级学生数量
     */
    List<Object[]> countStudentsByGrade();

    /**
     * 统计各专业学生数量
     */
    List<Object[]> countStudentsByMajor();

    /**
     * 统计各班级学生数量
     */
    List<Object[]> countStudentsByClass();

    /**
     * 统计今日新增学生数量
     */
    long countTodayNewStudents();

    /**
     * 统计指定时间范围内的学生数量
     */
    long countStudentsByDateRange(LocalDateTime startTime, LocalDateTime endTime);

    // ================================
    // 业务操作
    // ================================

    /**
     * 批量导入学生
     */
    void batchImportStudents(List<StudentDTO> studentDTOs);

    /**
     * 批量更新学生状态
     */
    void batchUpdateStatus(List<Long> studentIds, Integer status);

    /**
     * 批量删除学生
     */
    void batchDeleteStudents(List<Long> studentIds);

    /**
     * 学生转班
     */
    void transferClass(Long studentId, Long newClassId);

    /**
     * 学生转专业
     */
    void transferMajor(Long studentId, String newMajor);

    /**
     * 学生升级
     */
    void promoteGrade(List<Long> studentIds, String newGrade);

    // ================================
    // 学籍管理
    // ================================

    /**
     * 学生注册
     */
    void registerStudent(Long studentId);

    /**
     * 学生休学
     */
    void suspendStudent(Long studentId, String reason);

    /**
     * 学生复学
     */
    void resumeStudent(Long studentId);

    /**
     * 学生退学
     */
    void withdrawStudent(Long studentId, String reason);

    /**
     * 学生毕业
     */
    void graduateStudent(Long studentId);

    // ================================
    // 关联数据操作
    // ================================

    /**
     * 获取学生的课程选择
     */
    List<Object> getStudentCourseSelections(Long studentId);

    /**
     * 获取学生的成绩记录
     */
    List<Object> getStudentGrades(Long studentId);

    /**
     * 获取学生的缴费记录
     */
    List<Object> getStudentPaymentRecords(Long studentId);

    /**
     * 获取学生的考勤记录
     */
    List<Object> getStudentAttendanceRecords(Long studentId);

    // ================================
    // 数据导出
    // ================================

    /**
     * 导出所有学生数据
     */
    List<Student> exportAllStudents();

    /**
     * 根据条件导出学生数据
     */
    List<Student> exportStudentsByConditions(String grade, String major, Long classId, Integer status);

    /**
     * 导出学生基本信息
     */
    List<StudentDTO> exportStudentBasicInfo();

    /**
     * 导出学生详细信息
     */
    List<StudentDTO> exportStudentDetailInfo();

    // ================================
    // 验证操作
    // ================================

    /**
     * 验证学号是否存在
     */
    boolean existsByStudentNo(String studentNo);

    /**
     * 验证学生是否存在
     */
    boolean existsById(Long id);

    /**
     * 验证学生状态
     */
    boolean isStudentActive(Long studentId);

    /**
     * 验证学生是否可以选课
     */
    boolean canSelectCourse(Long studentId);

    /**
     * 验证学生是否可以参加考试
     */
    boolean canTakeExam(Long studentId);

    // ================================
    // 搜索操作
    // ================================

    /**
     * 根据关键字搜索学生
     */
    Page<Student> searchStudents(String keyword, Pageable pageable);

    /**
     * 高级搜索学生
     */
    Page<Student> advancedSearchStudents(String studentNo, String realName, String grade, 
                                       String major, Long classId, Integer status, 
                                       LocalDateTime startTime, LocalDateTime endTime, 
                                       Pageable pageable);

    // ================================
    // 缓存操作
    // ================================

    /**
     * 刷新学生缓存
     */
    void refreshStudentCache(Long studentId);

    /**
     * 清除所有学生缓存
     */
    void clearAllStudentCache();

    // ================================
    // 同步操作
    // ================================

    /**
     * 同步学生用户信息
     */
    void syncStudentUserInfo(Long studentId);

    /**
     * 批量同步学生用户信息
     */
    void batchSyncStudentUserInfo(List<Long> studentIds);
}
