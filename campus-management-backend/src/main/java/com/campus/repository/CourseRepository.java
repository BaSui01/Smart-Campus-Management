package com.campus.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.Course;

/**
 * 课程数据访问层
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Mapper
@Repository
public interface CourseRepository extends BaseMapper<Course> {

    /**
     * 根据课程代码查找课程
     *
     * @param courseCode 课程代码
     * @return 课程信息
     */
    @Select("SELECT * FROM tb_course WHERE course_code = #{courseCode} AND deleted = 0")
    Optional<Course> findByCourseCode(@Param("courseCode") String courseCode);

    /**
     * 根据课程名称查找课程
     *
     * @param courseName 课程名称
     * @return 课程列表
     */
    @Select("SELECT * FROM tb_course WHERE course_name LIKE CONCAT('%', #{courseName}, '%') AND deleted = 0")
    List<Course> findByCourseName(@Param("courseName") String courseName);

    /**
     * 根据部门ID查找课程列表
     *
     * @param departmentId 部门ID
     * @return 课程列表
     */
    @Select("SELECT * FROM tb_course WHERE department_id = #{departmentId} AND deleted = 0")
    List<Course> findByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * 根据教师ID查找课程列表
     *
     * @param teacherId 教师ID
     * @return 课程列表
     */
    @Select("SELECT * FROM tb_course WHERE teacher_id = #{teacherId} AND deleted = 0")
    List<Course> findByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 根据学期查找课程列表
     *
     * @param semester 学期
     * @return 课程列表
     */
    @Select("SELECT * FROM tb_course WHERE semester = #{semester} AND deleted = 0")
    List<Course> findBySemester(@Param("semester") String semester);

    /**
     * 根据课程类型查找课程列表
     *
     * @param courseType 课程类型
     * @return 课程列表
     */
    @Select("SELECT * FROM tb_course WHERE course_type = #{courseType} AND deleted = 0")
    List<Course> findByCourseType(@Param("courseType") String courseType);

    /**
     * 检查课程代码是否存在
     *
     * @param courseCode 课程代码
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM tb_course WHERE course_code = #{courseCode} AND deleted = 0")
    boolean existsByCourseCode(@Param("courseCode") String courseCode);

    /**
     * 获取课程详情（包含教师信息）
     */
    @Select("""
        SELECT c.*, t.real_name as teacher_name
        FROM tb_course c
        LEFT JOIN tb_user t ON c.teacher_id = t.id AND t.deleted = 0
        WHERE c.id = #{courseId} AND c.deleted = 0
        """)
    Optional<CourseDetail> findCourseDetailById(@Param("courseId") Long courseId);

    /**
     * 统计课程数量按类型
     */
    @Select("""
        SELECT course_type, COUNT(*) as count
        FROM tb_course
        WHERE deleted = 0
        GROUP BY course_type
        ORDER BY count DESC
        """)
    List<CourseTypeCount> countCoursesByType();

    /**
     * 课程类型统计内部类
     */
    class CourseTypeCount {
        private String courseType;
        private Long count;

        public String getCourseType() { return courseType; }
        public void setCourseType(String courseType) { this.courseType = courseType; }
        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
    }

    /**
     * 课程详情内部类
     */
    class CourseDetail extends Course {
        private String teacherName;

        public String getTeacherName() { return teacherName; }
        public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    }
}
