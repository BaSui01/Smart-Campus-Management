package com.campus.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.entity.Student;
import com.campus.repository.StudentRepository.StudentGradeCount;
import com.campus.repository.StudentRepository.StudentWithUser;

/**
 * 学生服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
public interface StudentService extends IService<Student> {

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
     * 获取学生及其用户信息
     *
     * @param studentId 学生ID
     * @return 学生信息包含用户详情
     */
    Optional<StudentWithUser> findStudentWithUser(Long studentId);

    /**
     * 搜索学生
     *
     * @param keyword 关键词
     * @return 学生列表
     */
    List<StudentWithUser> searchStudents(String keyword);

    /**
     * 统计学生数量按年级
     *
     * @return 统计结果
     */
    List<StudentGradeCount> countStudentsByGrade();

    /**
     * 分页查询学生列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param params 查询参数
     * @return 分页结果
     */
    IPage<Student> findStudentsByPage(int page, int size, Map<String, Object> params);

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
}
