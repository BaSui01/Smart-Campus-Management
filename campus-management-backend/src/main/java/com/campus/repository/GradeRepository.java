package com.campus.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.Grade;

/**
 * 成绩数据访问层
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Mapper
@Repository
public interface GradeRepository extends BaseMapper<Grade> {

    /**
     * 根据学生ID查找成绩记录
     *
     * @param studentId 学生ID
     * @return 成绩记录列表
     */
    @Select("SELECT * FROM tb_grade WHERE student_id = #{studentId} AND deleted = 0")
    List<Grade> findByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据课程ID查找成绩记录
     *
     * @param courseId 课程ID
     * @return 成绩记录列表
     */
    @Select("SELECT * FROM tb_grade WHERE course_id = #{courseId} AND deleted = 0")
    List<Grade> findByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据课程表ID查找成绩记录
     *
     * @param scheduleId 课程表ID
     * @return 成绩记录列表
     */
    @Select("SELECT * FROM tb_grade WHERE schedule_id = #{scheduleId} AND deleted = 0")
    List<Grade> findByScheduleId(@Param("scheduleId") Long scheduleId);

    /**
     * 根据选课ID查找成绩记录
     *
     * @param selectionId 选课ID
     * @return 成绩记录
     */
    @Select("SELECT * FROM tb_grade WHERE selection_id = #{selectionId} AND deleted = 0")
    Optional<Grade> findBySelectionId(@Param("selectionId") Long selectionId);

    /**
     * 根据学期查找成绩记录
     *
     * @param semester 学期
     * @return 成绩记录列表
     */
    @Select("SELECT * FROM tb_grade WHERE semester = #{semester} AND deleted = 0")
    List<Grade> findBySemester(@Param("semester") String semester);

    /**
     * 根据学生ID和课程ID查找成绩记录
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 成绩记录列表
     */
    @Select("SELECT * FROM tb_grade WHERE student_id = #{studentId} AND course_id = #{courseId} AND deleted = 0")
    List<Grade> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 根据学生ID和学期查找成绩记录
     *
     * @param studentId 学生ID
     * @param semester 学期
     * @return 成绩记录列表
     */
    @Select("SELECT * FROM tb_grade WHERE student_id = #{studentId} AND semester = #{semester} AND deleted = 0")
    List<Grade> findByStudentIdAndSemester(@Param("studentId") Long studentId, @Param("semester") String semester);

    /**
     * 获取成绩详情（包含学生、课程和教师信息）
     */
    @Select("""
        SELECT g.*, s.student_no, u.real_name as student_name,
               c.course_name, c.course_code, c.credits,
               t.real_name as teacher_name
        FROM tb_grade g
        LEFT JOIN tb_student s ON g.student_id = s.id AND s.deleted = 0
        LEFT JOIN tb_user u ON s.user_id = u.id AND u.deleted = 0
        LEFT JOIN tb_course c ON g.course_id = c.id AND c.deleted = 0
        LEFT JOIN tb_user t ON g.teacher_id = t.id AND t.deleted = 0
        WHERE g.id = #{gradeId} AND g.deleted = 0
        """)
    Optional<GradeDetail> findGradeDetailById(@Param("gradeId") Long gradeId);

    /**
     * 获取学生的成绩详情列表
     */
    @Select("""
        SELECT g.*, c.course_name, c.course_code, c.credits,
               t.real_name as teacher_name
        FROM tb_grade g
        LEFT JOIN tb_course c ON g.course_id = c.id AND c.deleted = 0
        LEFT JOIN tb_user t ON g.teacher_id = t.id AND t.deleted = 0
        WHERE g.student_id = #{studentId} AND g.semester = #{semester} AND g.deleted = 0
        ORDER BY g.course_id
        """)
    List<StudentGradeDetail> findStudentGradeDetails(@Param("studentId") Long studentId, @Param("semester") String semester);

    /**
     * 获取课程的学生成绩列表
     */
    @Select("""
        SELECT g.*, s.student_no, u.real_name as student_name
        FROM tb_grade g
        LEFT JOIN tb_student s ON g.student_id = s.id AND s.deleted = 0
        LEFT JOIN tb_user u ON s.user_id = u.id AND u.deleted = 0
        WHERE g.course_id = #{courseId} AND g.schedule_id = #{scheduleId} AND g.deleted = 0
        ORDER BY s.student_no
        """)
    List<CourseGradeDetail> findCourseGradeDetails(@Param("courseId") Long courseId, @Param("scheduleId") Long scheduleId);

    /**
     * 计算学生的平均绩点
     */
    @Select("""
        SELECT COALESCE(SUM(g.grade_point * c.credits) / SUM(c.credits), 0) as gpa
        FROM tb_grade g
        LEFT JOIN tb_course c ON g.course_id = c.id AND c.deleted = 0
        WHERE g.student_id = #{studentId} AND g.semester = #{semester} AND g.deleted = 0 AND g.grade_point IS NOT NULL
        """)
    Double calculateStudentGPA(@Param("studentId") Long studentId, @Param("semester") String semester);

    /**
     * 计算班级的平均成绩
     */
    @Select("""
        SELECT AVG(g.score) as average_score
        FROM tb_grade g
        LEFT JOIN tb_student s ON g.student_id = s.id AND s.deleted = 0
        WHERE s.class_id = #{classId} AND g.course_id = #{courseId} AND g.deleted = 0 AND g.score IS NOT NULL
        """)
    Double calculateClassAverageScore(@Param("classId") Long classId, @Param("courseId") Long courseId);

    /**
     * 成绩详情内部类
     */
    class GradeDetail extends Grade {
        private String studentNo;
        private String studentName;
        private String courseName;
        private String courseCode;
        private Integer credits;
        private String teacherName;

        public String getStudentNo() { return studentNo; }
        public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        public String getCourseCode() { return courseCode; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
        public Integer getCredits() { return credits; }
        public void setCredits(Integer credits) { this.credits = credits; }
        public String getTeacherName() { return teacherName; }
        public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    }

    /**
     * 学生成绩详情内部类
     */
    class StudentGradeDetail extends Grade {
        private String courseName;
        private String courseCode;
        private Integer credits;
        private String teacherName;

        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        public String getCourseCode() { return courseCode; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
        public Integer getCredits() { return credits; }
        public void setCredits(Integer credits) { this.credits = credits; }
        public String getTeacherName() { return teacherName; }
        public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    }

    /**
     * 课程成绩详情内部类
     */
    class CourseGradeDetail extends Grade {
        private String studentNo;
        private String studentName;

        public String getStudentNo() { return studentNo; }
        public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
    }
}
