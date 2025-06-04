package com.campus.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.CourseSchedule;

/**
 * 课程表数据访问层
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Mapper
@Repository
public interface CourseScheduleRepository extends BaseMapper<CourseSchedule> {

    /**
     * 根据课程ID查找课程表
     *
     * @param courseId 课程ID
     * @return 课程表列表
     */
    @Select("SELECT * FROM tb_course_schedule WHERE course_id = #{courseId} AND deleted = 0")
    List<CourseSchedule> findByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据教师ID查找课程表
     *
     * @param teacherId 教师ID
     * @return 课程表列表
     */
    @Select("SELECT * FROM tb_course_schedule WHERE teacher_id = #{teacherId} AND deleted = 0")
    List<CourseSchedule> findByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 根据班级ID查找课程表
     *
     * @param classId 班级ID
     * @return 课程表列表
     */
    @Select("SELECT * FROM tb_course_schedule WHERE class_id = #{classId} AND deleted = 0")
    List<CourseSchedule> findByClassId(@Param("classId") Long classId);

    /**
     * 根据学期查找课程表
     *
     * @param semester 学期
     * @return 课程表列表
     */
    @Select("SELECT * FROM tb_course_schedule WHERE semester = #{semester} AND deleted = 0")
    List<CourseSchedule> findBySemester(@Param("semester") String semester);

    /**
     * 根据教室查找课程表
     *
     * @param classroom 教室
     * @return 课程表列表
     */
    @Select("SELECT * FROM tb_course_schedule WHERE classroom = #{classroom} AND deleted = 0")
    List<CourseSchedule> findByClassroom(@Param("classroom") String classroom);

    /**
     * 获取课程表详情（包含课程和教师信息）
     */
    @Select("""
        SELECT cs.*, c.course_name, c.course_code, t.real_name as teacher_name
        FROM tb_course_schedule cs
        LEFT JOIN tb_course c ON cs.course_id = c.id AND c.deleted = 0
        LEFT JOIN tb_user t ON cs.teacher_id = t.id AND t.deleted = 0
        WHERE cs.id = #{scheduleId} AND cs.deleted = 0
        """)
    Optional<ScheduleDetail> findScheduleDetailById(@Param("scheduleId") Long scheduleId);

    /**
     * 检查教室在指定时间是否被占用
     *
     * @param classroom 教室
     * @param dayOfWeek 星期几
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param semester 学期
     * @param excludeId 排除的课程表ID（用于更新时）
     * @return 是否被占用
     */
    @Select("""
        SELECT COUNT(*) > 0
        FROM tb_course_schedule
        WHERE classroom = #{classroom}
        AND day_of_week = #{dayOfWeek}
        AND semester = #{semester}
        AND deleted = 0
        AND id != #{excludeId}
        AND (
            (start_time <= #{startTime} AND end_time > #{startTime})
            OR (start_time < #{endTime} AND end_time >= #{endTime})
            OR (start_time >= #{startTime} AND end_time <= #{endTime})
        )
        """)
    boolean isClassroomOccupied(@Param("classroom") String classroom,
                               @Param("dayOfWeek") Integer dayOfWeek,
                               @Param("startTime") String startTime,
                               @Param("endTime") String endTime,
                               @Param("semester") String semester,
                               @Param("excludeId") Long excludeId);

    /**
     * 检查教师在指定时间是否有其他课程
     *
     * @param teacherId 教师ID
     * @param dayOfWeek 星期几
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param semester 学期
     * @param excludeId 排除的课程表ID（用于更新时）
     * @return 是否有冲突
     */
    @Select("""
        SELECT COUNT(*) > 0
        FROM tb_course_schedule
        WHERE teacher_id = #{teacherId}
        AND day_of_week = #{dayOfWeek}
        AND semester = #{semester}
        AND deleted = 0
        AND id != #{excludeId}
        AND (
            (start_time <= #{startTime} AND end_time > #{startTime})
            OR (start_time < #{endTime} AND end_time >= #{endTime})
            OR (start_time >= #{startTime} AND end_time <= #{endTime})
        )
        """)
    boolean isTeacherOccupied(@Param("teacherId") Long teacherId,
                             @Param("dayOfWeek") Integer dayOfWeek,
                             @Param("startTime") String startTime,
                             @Param("endTime") String endTime,
                             @Param("semester") String semester,
                             @Param("excludeId") Long excludeId);

    /**
     * 课程表详情内部类
     */
    class ScheduleDetail extends CourseSchedule {
        private String courseName;
        private String courseCode;
        private String teacherName;

        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        public String getCourseCode() { return courseCode; }
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
        public String getTeacherName() { return teacherName; }
        public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    }
}
