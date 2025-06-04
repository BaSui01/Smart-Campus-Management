package com.campus.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.entity.Course;
import com.campus.repository.CourseRepository.CourseDetail;
import com.campus.repository.CourseRepository.CourseTypeCount;

/**
 * 课程服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
public interface CourseService extends IService<Course> {

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
     * 获取课程详情
     *
     * @param courseId 课程ID
     * @return 课程详情
     */
    Optional<CourseDetail> findCourseDetailById(Long courseId);

    /**
     * 统计课程数量按类型
     *
     * @return 统计结果
     */
    List<CourseTypeCount> countCoursesByType();

    /**
     * 分页查询课程列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param params 查询参数
     * @return 分页结果
     */
    IPage<Course> findCoursesByPage(int page, int size, Map<String, Object> params);

    /**
     * 创建课程
     *
     * @param course 课程信息
     * @return 创建结果
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
     * @param id 课程ID
     * @return 删除结果
     */
    boolean deleteCourse(Long id);

    /**
     * 批量删除课程
     *
     * @param ids 课程ID列表
     * @return 删除结果
     */
    boolean batchDeleteCourses(List<Long> ids);

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
}
