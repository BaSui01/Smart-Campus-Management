package com.campus.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campus.domain.entity.CourseSchedule;

/**
 * 课程表数据访问层
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Repository
public interface CourseScheduleRepository extends JpaRepository<CourseSchedule, Long> {

    /**
     * 根据课程ID查找课程表
     *
     * @param courseId 课程ID
     * @return 课程表列表
     */
    List<CourseSchedule> findByCourseIdAndDeleted(Long courseId, Integer deleted);

    /**
     * 根据教师ID查找课程表
     *
     * @param teacherId 教师ID
     * @return 课程表列表
     */
    List<CourseSchedule> findByTeacherIdAndDeleted(Long teacherId, Integer deleted);

    /**
     * 根据班级ID查找课程表
     *
     * @param classId 班级ID
     * @return 课程表列表
     */
    List<CourseSchedule> findByClassIdAndDeleted(Long classId, Integer deleted);

    /**
     * 根据学期查找课程表
     *
     * @param semester 学期
     * @return 课程表列表
     */
    List<CourseSchedule> findBySemesterAndDeleted(String semester, Integer deleted);

    /**
     * 根据教室查找课程表
     *
     * @param classroom 教室
     * @return 课程表列表
     */
    List<CourseSchedule> findByClassroomAndDeleted(String classroom, Integer deleted);

    /**
     * 组合查询方法
     */
    List<CourseSchedule> findByCourseIdAndSemesterAndDeleted(Long courseId, String semester, Integer deleted);
    List<CourseSchedule> findByTeacherIdAndSemesterAndDeleted(Long teacherId, String semester, Integer deleted);
    List<CourseSchedule> findByClassIdAndSemesterAndDeleted(Long classId, String semester, Integer deleted);

    /**
     * 获取课程表详情（包含课程和教师信息）
     */
    @Query("""
        SELECT cs, c.courseName, c.courseCode, u.realName
        FROM CourseSchedule cs
        LEFT JOIN Course c ON cs.courseId = c.id AND c.deleted = 0
        LEFT JOIN User u ON cs.teacherId = u.id AND u.deleted = 0
        WHERE cs.id = :scheduleId AND cs.deleted = 0
        """)
    Optional<Object[]> findScheduleDetailById(@Param("scheduleId") Long scheduleId);

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
    @Query("""
        SELECT COUNT(cs) > 0
        FROM CourseSchedule cs
        WHERE cs.classroom = :classroom
        AND cs.dayOfWeek = :dayOfWeek
        AND cs.semester = :semester
        AND cs.deleted = 0
        AND cs.id != :excludeId
        AND (
            (cs.startTime <= :startTime AND cs.endTime > :startTime)
            OR (cs.startTime < :endTime AND cs.endTime >= :endTime)
            OR (cs.startTime >= :startTime AND cs.endTime <= :endTime)
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
    @Query("""
        SELECT COUNT(cs) > 0
        FROM CourseSchedule cs
        WHERE cs.teacherId = :teacherId
        AND cs.dayOfWeek = :dayOfWeek
        AND cs.semester = :semester
        AND cs.deleted = 0
        AND cs.id != :excludeId
        AND (
            (cs.startTime <= :startTime AND cs.endTime > :startTime)
            OR (cs.startTime < :endTime AND cs.endTime >= :endTime)
            OR (cs.startTime >= :startTime AND cs.endTime <= :endTime)
        )
        """)
    boolean isTeacherOccupied(@Param("teacherId") Long teacherId,
                             @Param("dayOfWeek") Integer dayOfWeek,
                             @Param("startTime") String startTime,
                             @Param("endTime") String endTime,
                             @Param("semester") String semester,
                             @Param("excludeId") Long excludeId);
}
