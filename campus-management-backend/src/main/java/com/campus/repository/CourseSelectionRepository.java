package com.campus.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.CourseSelection;

/**
 * 选课数据访问层
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Mapper
@Repository
public interface CourseSelectionRepository extends BaseMapper<CourseSelection> {

    /**
     * 根据学生ID查找选课记录
     *
     * @param studentId 学生ID
     * @return 选课记录列表
     */
    @Select("SELECT * FROM tb_course_selection WHERE student_id = #{studentId} AND deleted = 0")
    List<CourseSelection> findByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据课程ID查找选课记录
     *
     * @param courseId 课程ID
     * @return 选课记录列表
     */
    @Select("SELECT * FROM tb_course_selection WHERE course_id = #{courseId} AND deleted = 0")
    List<CourseSelection> findByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据课程表ID查找选课记录
     *
     * @param scheduleId 课程表ID
     * @return 选课记录列表
     */
    @Select("SELECT * FROM tb_course_selection WHERE schedule_id = #{scheduleId} AND deleted = 0")
    List<CourseSelection> findByScheduleId(@Param("scheduleId") Long scheduleId);

    /**
     * 根据学期查找选课记录
     *
     * @param semester 学期
     * @return 选课记录列表
     */
    @Select("SELECT * FROM tb_course_selection WHERE semester = #{semester} AND deleted = 0")
    List<CourseSelection> findBySemester(@Param("semester") String semester);

    /**
     * 检查学生是否已选择课程
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 是否已选择
     */
    @Select("SELECT COUNT(*) > 0 FROM tb_course_selection WHERE student_id = #{studentId} AND course_id = #{courseId} AND deleted = 0")
    boolean existsByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 检查学生是否已选择课程表
     *
     * @param studentId 学生ID
     * @param scheduleId 课程表ID
     * @return 是否已选择
     */
    @Select("SELECT COUNT(*) > 0 FROM tb_course_selection WHERE student_id = #{studentId} AND schedule_id = #{scheduleId} AND deleted = 0")
    boolean existsByStudentIdAndScheduleId(@Param("studentId") Long studentId, @Param("scheduleId") Long scheduleId);

    /**
     * 获取选课详情（包含学生、课程和课程表信息）
     */
    @Select("""
        SELECT cs.*, s.student_no, u.real_name as student_name, c.course_name, c.course_code
        FROM tb_course_selection cs
        LEFT JOIN tb_student s ON cs.student_id = s.id AND s.deleted = 0
        LEFT JOIN tb_user u ON s.user_id = u.id AND u.deleted = 0
        LEFT JOIN tb_course c ON cs.course_id = c.id AND c.deleted = 0
        WHERE cs.id = #{selectionId} AND cs.deleted = 0
        """)
    Optional<SelectionDetail> findSelectionDetailById(@Param("selectionId") Long selectionId);

    /**
     * 获取学生的选课详情列表
     */
    @Select("""
        SELECT cs.*, c.course_name, c.course_code, c.credits,
               csch.day_of_week, csch.start_time, csch.end_time, csch.classroom,
               t.real_name as teacher_name
        FROM tb_course_selection cs
        LEFT JOIN tb_course c ON cs.course_id = c.id AND c.deleted = 0
        LEFT JOIN tb_course_schedule csch ON cs.schedule_id = csch.id AND csch.deleted = 0
        LEFT JOIN tb_user t ON csch.teacher_id = t.id AND t.deleted = 0
        WHERE cs.student_id = #{studentId} AND cs.semester = #{semester} AND cs.deleted = 0
        ORDER BY csch.day_of_week, csch.start_time
        """)
    List<StudentSelectionDetail> findStudentSelectionDetails(@Param("studentId") Long studentId, @Param("semester") String semester);

    /**
     * 获取课程表的选课学生列表
     */
    @Select("""
        SELECT cs.*, s.student_no, u.real_name as student_name, u.email, u.phone
        FROM tb_course_selection cs
        LEFT JOIN tb_student s ON cs.student_id = s.id AND s.deleted = 0
        LEFT JOIN tb_user u ON s.user_id = u.id AND u.deleted = 0
        WHERE cs.schedule_id = #{scheduleId} AND cs.deleted = 0
        ORDER BY s.student_no
        """)
    List<ScheduleStudentDetail> findScheduleStudentDetails(@Param("scheduleId") Long scheduleId);

    /**
     * 选课详情内部类
     */
    class SelectionDetail extends CourseSelection {
        private String studentNo;
        private String studentName;
        private String courseName;
        private String courseCode;

        public String getStudentNo() { return studentNo; }
        public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        public String getCourseCode() { return courseCode; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    }

    /**
     * 学生选课详情内部类
     */
    class StudentSelectionDetail extends CourseSelection {
        private String courseName;
        private String courseCode;
        private Integer credits;
        private Integer dayOfWeek;
        private String startTime;
        private String endTime;
        private String classroom;
        private String teacherName;

        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        public String getCourseCode() { return courseCode; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
        public Integer getCredits() { return credits; }
        public void setCredits(Integer credits) { this.credits = credits; }
        public Integer getDayOfWeek() { return dayOfWeek; }
        public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
        public String getClassroom() { return classroom; }
        public void setClassroom(String classroom) { this.classroom = classroom; }
        public String getTeacherName() { return teacherName; }
        public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    }

    /**
     * 课程表学生详情内部类
     */
    class ScheduleStudentDetail extends CourseSelection {
        private String studentNo;
        private String studentName;
        private String email;
        private String phone;

        public String getStudentNo() { return studentNo; }
        public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
}
