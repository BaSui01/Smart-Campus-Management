package com.campus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campus.entity.Course;

/**
 * 课程数据访问层
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * 根据课程代码查找课程
     *
     * @param courseCode 课程代码
     * @return 课程信息
     */
    Optional<Course> findByCourseCodeAndDeleted(String courseCode, Integer deleted);

    /**
     * 根据课程名称查找课程
     *
     * @param courseName 课程名称
     * @return 课程列表
     */
    List<Course> findByCourseNameContainingAndDeleted(String courseName, Integer deleted);

    /**
     * 根据部门ID查找课程列表
     *
     * @param departmentId 部门ID
     * @return 课程列表
     */
    List<Course> findByDepartmentIdAndDeleted(Long departmentId, Integer deleted);

    /**
     * 根据教师ID查找课程列表
     *
     * @param teacherId 教师ID
     * @return 课程列表
     */
    List<Course> findByTeacherIdAndDeleted(Long teacherId, Integer deleted);

    /**
     * 根据学期查找课程列表
     *
     * @param semester 学期
     * @return 课程列表
     */
    List<Course> findBySemesterAndDeleted(String semester, Integer deleted);

    /**
     * 根据课程类型查找课程列表
     *
     * @param courseType 课程类型
     * @return 课程列表
     */
    List<Course> findByCourseTypeAndDeleted(String courseType, Integer deleted);

    /**
     * 检查课程代码是否存在
     *
     * @param courseCode 课程代码
     * @return 是否存在
     */
    boolean existsByCourseCodeAndDeleted(String courseCode, Integer deleted);

    /**
     * 获取课程详情（包含教师信息）
     */
    @Query("""
        SELECT c, u.realName as teacherName
        FROM Course c
        LEFT JOIN User u ON c.teacherId = u.id AND u.deleted = 0
        WHERE c.id = :courseId AND c.deleted = 0
        """)
    Optional<Object[]> findCourseDetailById(@Param("courseId") Long courseId);

    /**
     * 统计课程数量按类型
     */
    @Query("""
        SELECT c.courseType, COUNT(c)
        FROM Course c
        WHERE c.deleted = 0
        GROUP BY c.courseType
        ORDER BY COUNT(c) DESC
        """)
    List<Object[]> countCoursesByType();

    /**
     * 统计课程数量按学期
     */
    @Query("""
        SELECT c.semester, COUNT(c)
        FROM Course c
        WHERE c.deleted = 0
        GROUP BY c.semester
        ORDER BY c.semester DESC
        """)
    List<Object[]> countCoursesBySemester();

    /**
     * 根据学分范围查找课程
     *
     * @param minCredits 最小学分
     * @param maxCredits 最大学分
     * @return 课程列表
     */
    @Query("SELECT c FROM Course c WHERE c.credits BETWEEN :minCredits AND :maxCredits AND c.deleted = 0 ORDER BY c.credits ASC")
    List<Course> findByCreditsBetween(@Param("minCredits") Integer minCredits, @Param("maxCredits") Integer maxCredits);

    /**
     * 查找有选课学生的课程
     */
    @Query("""
        SELECT DISTINCT c
        FROM Course c
        INNER JOIN CourseSelection cs ON c.id = cs.courseId AND cs.deleted = 0
        WHERE c.deleted = 0
        ORDER BY c.courseName
        """)
    List<Course> findCoursesWithSelections();

    /**
     * 查找没有选课学生的课程
     */
    @Query("""
        SELECT c
        FROM Course c
        LEFT JOIN CourseSelection cs ON c.id = cs.courseId AND cs.deleted = 0
        WHERE c.deleted = 0 AND cs.id IS NULL
        ORDER BY c.courseName
        """)
    List<Course> findCoursesWithoutSelections();

    /**
     * 根据关键词搜索课程
     *
     * @param keyword 关键词
     * @return 课程列表
     */
    @Query("""
        SELECT c FROM Course c
        WHERE c.deleted = 0
        AND (c.courseName LIKE %:keyword%
             OR c.courseCode LIKE %:keyword%
             OR c.description LIKE %:keyword%)
        ORDER BY c.courseName
        """)
    List<Course> searchCourses(@Param("keyword") String keyword);

}
