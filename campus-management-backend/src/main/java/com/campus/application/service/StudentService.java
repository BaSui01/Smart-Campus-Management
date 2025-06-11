package com.campus.application.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.campus.domain.entity.Student;

/**
 * 学生服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
public interface StudentService {

    /**
     * 保存学生
     *
     * @param student 学生
     * @return 保存的学生
     */
    Student save(Student student);

    /**
     * 根据ID查找学生
     *
     * @param id 学生ID
     * @return 学生
     */
    Optional<Student> findById(Long id);

    /**
     * 查找所有学生
     *
     * @return 学生列表
     */
    List<Student> findAll();

    /**
     * 分页查找所有学生
     *
     * @param pageable 分页参数
     * @return 学生分页结果
     */
    Page<Student> findAll(Pageable pageable);

    /**
     * 根据ID删除学生
     *
     * @param id 学生ID
     */
    void deleteById(Long id);

    /**
     * 批量删除学生
     *
     * @param ids 学生ID列表
     */
    void deleteAllById(List<Long> ids);

    /**
     * 统计学生数量
     *
     * @return 总数量
     */
    long count();

    /**
     * 根据学号查找学生
     *
     * @param studentNo 学号
     * @return 学生信息
     */
    Optional<Student> findByStudentNo(String studentNo);

    /**
     * 根据用户ID查找学生
     *
     * @param userId 用户ID
     * @return 学生信息
     */
    Optional<Student> findByUserId(Long userId);

    /**
     * 根据班级ID查找学生列表
     *
     * @param classId 班级ID
     * @return 学生列表
     */
    List<Student> findByClassId(Long classId);

    /**
     * 根据年级查找学生列表
     *
     * @param grade 年级
     * @return 学生列表
     */
    List<Student> findByGrade(String grade);

    /**
     * 检查学号是否存在
     *
     * @param studentNo 学号
     * @return 是否存在
     */
    boolean existsByStudentNo(String studentNo);

    /**
     * 获取学生及其用户信息（使用Object[]返回）
     *
     * @param studentId 学生ID
     * @return 学生信息包含用户详情
     */
    Optional<Object[]> findStudentWithUser(Long studentId);

    /**
     * 搜索学生（使用Object[]返回）
     *
     * @param keyword 关键词
     * @return 学生列表
     */
    List<Object[]> searchStudents(String keyword);

    /**
     * 统计学生数量按年级（使用Object[]返回）
     *
     * @return 统计结果
     */
    List<Object[]> countStudentsByGrade();

    /**
     * 分页查询学生列表
     *
     * @param pageable 分页参数
     * @param params 查询参数
     * @return 分页结果
     */
    Page<Student> findStudentsByPage(Pageable pageable, Map<String, Object> params);

    /**
     * 创建学生
     *
     * @param student 学生信息
     * @return 创建结果
     */
    Student createStudent(Student student);

    /**
     * 更新学生信息
     *
     * @param student 学生信息
     * @return 更新结果
     */
    boolean updateStudent(Student student);

    /**
     * 删除学生
     *
     * @param id 学生ID
     * @return 删除结果
     */
    boolean deleteStudent(Long id);

    /**
     * 批量删除学生
     *
     * @param ids 学生ID列表
     * @return 删除结果
     */
    boolean batchDeleteStudents(List<Long> ids);

    /**
     * 导入学生数据
     *
     * @param studentList 学生列表
     * @return 导入结果
     */
    Map<String, Object> importStudents(List<Student> studentList);

    /**
     * 导出学生数据
     *
     * @param params 导出参数
     * @return 导出结果
     */
    List<Student> exportStudents(Map<String, Object> params);

    /**
     * 获取学生统计信息
     *
     * @return 学生统计数据
     */
    StudentStatistics getStudentStatistics();

    /**
     * 搜索学生（分页）
     *
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 学生分页结果
     */
    Page<Student> searchStudents(String keyword, Pageable pageable);

    /**
     * 根据多个条件查找学生
     *
     * @param grade 年级
     * @param major 专业
     * @param classId 班级ID
     * @param enrollmentYear 入学年份
     * @param pageable 分页参数
     * @return 学生分页结果
     */
    Page<Student> findByMultipleConditions(String grade, String major, Long classId, Integer enrollmentYear, Pageable pageable);

    /**
     * 根据班级ID查找学生（分页）
     *
     * @param classId 班级ID
     * @param pageable 分页参数
     * @return 学生分页结果
     */
    Page<Student> findByClassId(Long classId, Pageable pageable);

    /**
     * 根据年级查找学生（分页）
     *
     * @param grade 年级
     * @param pageable 分页参数
     * @return 学生分页结果
     */
    Page<Student> findByGrade(String grade, Pageable pageable);

    /**
     * 根据专业查找学生列表
     *
     * @param major 专业
     * @return 学生列表
     */
    List<Student> findByMajor(String major);

    /**
     * 根据入学年份查找学生列表
     *
     * @param year 入学年份
     * @return 学生列表
     */
    List<Student> findByEnrollmentYear(Integer year);

    /**
     * 统计学生数量按专业
     *
     * @return 统计结果
     */
    List<Object[]> countStudentsByMajor();

    /**
     * 统计学生数量按班级
     *
     * @return 统计结果
     */
    List<Object[]> countStudentsByClass();

    /**
     * 统计学生数量按入学年份
     *
     * @return 统计结果
     */
    List<Object[]> countStudentsByEnrollmentYear();

    /**
     * 更新学生班级
     *
     * @param studentId 学生ID
     * @param classId 班级ID
     * @return 更新结果
     */
    boolean updateStudentClass(Long studentId, Long classId);

    /**
     * 批量更新学生班级
     *
     * @param studentIds 学生ID列表
     * @param classId 班级ID
     * @return 更新结果
     */
    boolean batchUpdateStudentClass(List<Long> studentIds, Long classId);

    /**
     * 更新学生年级
     *
     * @param studentId 学生ID
     * @param grade 年级
     * @return 更新结果
     */
    boolean updateStudentGrade(Long studentId, String grade);

    /**
     * 批量更新学生年级
     *
     * @param studentIds 学生ID列表
     * @param grade 年级
     * @return 更新结果
     */
    boolean batchUpdateStudentGrade(List<Long> studentIds, String grade);

    /**
     * 检查学号是否存在（排除指定ID）
     *
     * @param studentNo 学号
     * @param excludeId 排除的学生ID
     * @return 是否存在
     */
    boolean existsByStudentNoAndIdNot(String studentNo, Long excludeId);

    /**
     * 检查用户ID是否已关联学生
     *
     * @param userId 用户ID
     * @return 是否存在
     */
    boolean existsByUserId(Long userId);

    /**
     * 检查用户ID是否已关联学生（排除指定ID）
     *
     * @param userId 用户ID
     * @param excludeId 排除的学生ID
     * @return 是否存在
     */
    boolean existsByUserIdAndIdNot(Long userId, Long excludeId);

    /**
     * 学生统计信息内部类
     */
    class StudentStatistics {
        private long totalStudents;
        private long activeStudents;
        private long inactiveStudents;
        private long maleStudents;
        private long femaleStudents;
        private Map<String, Long> gradeDistribution;
        private Map<String, Long> classDistribution;

        public StudentStatistics() {}

        public StudentStatistics(long totalStudents, long activeStudents, long inactiveStudents,
                               long maleStudents, long femaleStudents,
                               Map<String, Long> gradeDistribution, Map<String, Long> classDistribution) {
            this.totalStudents = totalStudents;
            this.activeStudents = activeStudents;
            this.inactiveStudents = inactiveStudents;
            this.maleStudents = maleStudents;
            this.femaleStudents = femaleStudents;
            this.gradeDistribution = gradeDistribution;
            this.classDistribution = classDistribution;
        }

        // Getters and Setters
        public long getTotalStudents() { return totalStudents; }
        public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }

        public long getActiveStudents() { return activeStudents; }
        public void setActiveStudents(long activeStudents) { this.activeStudents = activeStudents; }

        public long getInactiveStudents() { return inactiveStudents; }
        public void setInactiveStudents(long inactiveStudents) { this.inactiveStudents = inactiveStudents; }

        public long getMaleStudents() { return maleStudents; }
        public void setMaleStudents(long maleStudents) { this.maleStudents = maleStudents; }

        public long getFemaleStudents() { return femaleStudents; }
        public void setFemaleStudents(long femaleStudents) { this.femaleStudents = femaleStudents; }

        public Map<String, Long> getGradeDistribution() { return gradeDistribution; }
        public void setGradeDistribution(Map<String, Long> gradeDistribution) { this.gradeDistribution = gradeDistribution; }

        public Map<String, Long> getClassDistribution() { return classDistribution; }
        public void setClassDistribution(Map<String, Long> classDistribution) { this.classDistribution = classDistribution; }
    }

    // ================================
    // GradeController 需要的别名方法
    // ================================

    /**
     * 根据ID查找学生（别名方法）
     */
    default Optional<Student> findStudentById(Long id) {
        return findById(id);
    }

    /**
     * 查找所有学生（别名方法）
     */
    default List<Student> findAllStudents() {
        return findAll();
    }
}
