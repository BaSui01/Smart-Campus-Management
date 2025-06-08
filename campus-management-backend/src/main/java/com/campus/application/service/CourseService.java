package com.campus.application.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.campus.domain.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 课程服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
public interface CourseService {

    /**
     * 保存课程
     *
     * @param course 课程
     * @return 保存的课程
     */
    Course save(Course course);

    /**
     * 根据ID查找课程
     *
     * @param id 课程ID
     * @return 课程
     */
    Optional<Course> findById(Long id);

    /**
     * 查找所有课程
     *
     * @return 课程列表
     */
    List<Course> findAll();

    /**
     * 分页查找所有课程
     *
     * @param pageable 分页参数
     * @return 课程分页结果
     */
    Page<Course> findAll(Pageable pageable);

    /**
     * 根据ID删除课程
     *
     * @param id 课程ID
     */
    void deleteById(Long id);

    /**
     * 批量删除课程
     *
     * @param ids 课程ID列表
     */
    void deleteAllById(List<Long> ids);

    /**
     * 根据课程代码查找课程
     *
     * @param courseCode 课程代码
     * @return 课程信息
     */
    Optional<Course> findByCourseCode(String courseCode);

    /**
     * 根据课程名称查找课程
     *
     * @param courseName 课程名称
     * @return 课程列表
     */
    List<Course> findByCourseName(String courseName);

    /**
     * 根据部门ID查找课程列表
     *
     * @param departmentId 部门ID
     * @return 课程列表
     */
    List<Course> findByDepartmentId(Long departmentId);

    /**
     * 根据教师ID查找课程列表
     *
     * @param teacherId 教师ID
     * @return 课程列表
     */
    List<Course> findByTeacherId(Long teacherId);

    /**
     * 根据学期查找课程列表
     *
     * @param semester 学期
     * @return 课程列表
     */
    List<Course> findBySemester(String semester);

    /**
     * 根据课程类型查找课程列表
     *
     * @param courseType 课程类型
     * @return 课程列表
     */
    List<Course> findByCourseType(String courseType);

    /**
     * 检查课程代码是否存在
     *
     * @param courseCode 课程代码
     * @return 是否存在
     */
    boolean existsByCourseCode(String courseCode);

    /**
     * 分页查询课程列表
     *
     * @param pageable 分页参数
     * @param params 查询参数
     * @return 分页结果
     */
    Page<Course> findCoursesByPage(Pageable pageable, Map<String, Object> params);

    /**
     * 更新课程选课人数
     *
     * @param courseId 课程ID
     * @return 更新结果
     */
    boolean updateEnrolledStudents(Long courseId);

    /**
     * 搜索课程
     *
     * @param keyword 关键词
     * @return 课程列表
     */
    List<Course> searchCourses(String keyword);

    /**
     * 统计课程总数
     *
     * @return 总数
     */
    long count();

    /**
     * 根据状态查找课程
     *
     * @param status 状态
     * @return 课程列表
     */
    List<Course> findByStatus(Integer status);

    /**
     * 统计课程类型分布
     *
     * @return 类型统计Map
     */
    Map<String, Long> countCoursesByType();

    /**
     * 创建课程
     *
     * @param course 课程信息
     * @return 创建的课程
     */
    Course createCourse(Course course);

    /**
     * 更新课程信息
     *
     * @param course 课程信息
     * @return 更新结果
     */
    boolean updateCourse(Course course);

    /**
     * 删除课程
     *
     * @param courseId 课程ID
     * @return 删除结果
     */
    boolean deleteCourse(Long courseId);

    /**
     * 批量删除课程
     *
     * @param courseIds 课程ID列表
     * @return 删除结果
     */
    boolean batchDeleteCourses(List<Long> courseIds);

    /**
     * 导入课程数据
     *
     * @param courses 课程列表
     * @return 导入结果
     */
    Map<String, Object> importCourses(List<Course> courses);

    /**
     * 导出课程数据
     *
     * @param params 查询参数
     * @return 课程列表
     */
    List<Course> exportCourses(Map<String, Object> params);

    /**
     * 获取活跃课程列表
     *
     * @return 活跃课程列表
     */
    List<Course> findActiveCourses();

    /**
     * 获取课程统计信息
     *
     * @return 课程统计信息
     */
    Object getCourseStatistics();

    // ================================
    // Web控制器需要的方法
    // ================================

    /**
     * 查找未排课的课程
     *
     * @return 未排课的课程列表
     */
    List<Course> findUnscheduledCourses();

    /**
     * 根据ID查找课程
     *
     * @param id 课程ID
     * @return 课程信息
     */
    Optional<Course> findCourseById(Long id);

    /**
     * 查找所有课程（不分页）
     *
     * @return 所有课程列表
     */
    List<Course> findAllCourses();

    /**
     * 根据课程代码查找课程
     *
     * @param courseCode 课程代码
     * @return 课程信息
     */
    Course findByCourseCodeString(String courseCode);

    /**
     * 获取课程选择列表（用于下拉框）
     *
     * @return 课程选择列表
     */
    List<Map<String, Object>> getCourseOptions();

    /**
     * 根据教师ID查找授课课程
     *
     * @param teacherId 教师ID
     * @return 课程列表
     */
    List<Course> findCoursesByTeacher(Long teacherId);

    /**
     * 根据学期查找课程
     *
     * @param semester 学期
     * @return 课程列表
     */
    List<Course> findCoursesBySemester(String semester);

    /**
     * 获取当前学期的课程
     *
     * @return 当前学期课程列表
     */
    List<Course> findCurrentSemesterCourses();

    /**
     * 获取课程分类列表
     *
     * @return 课程分类列表
     */
    List<Map<String, Object>> getCourseCategories();

    /**
     * 检查课程是否可以删除
     *
     * @param courseId 课程ID
     * @return 是否可以删除
     */
    boolean canDeleteCourse(Long courseId);

    /**
     * 启用课程
     *
     * @param courseId 课程ID
     * @return 操作结果
     */
    boolean enableCourse(Long courseId);

    /**
     * 禁用课程
     *
     * @param courseId 课程ID
     * @return 操作结果
     */
    boolean disableCourse(Long courseId);

    /**
     * 复制课程
     *
     * @param courseId 源课程ID
     * @param newCourseCode 新课程代码
     * @param newCourseName 新课程名称
     * @return 复制的课程
     */
    Course copyCourse(Long courseId, String newCourseCode, String newCourseName);
}
