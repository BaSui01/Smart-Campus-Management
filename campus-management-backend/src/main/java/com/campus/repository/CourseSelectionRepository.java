package com.campus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campus.entity.CourseSelection;

/**
 * 选课数据访问层
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Repository
public interface CourseSelectionRepository extends JpaRepository<CourseSelection, Long> {

    /**
     * 根据学生ID查找选课记录
     *
     * @param studentId 学生ID
     * @return 选课记录列表
     */
    List<CourseSelection> findByStudentIdAndDeleted(Long studentId, Integer deleted);

    /**
     * 根据课程ID查找选课记录
     *
     * @param courseId 课程ID
     * @return 选课记录列表
     */
    List<CourseSelection> findByCourseIdAndDeleted(Long courseId, Integer deleted);

    /**
     * 根据课程表ID查找选课记录
     *
     * @param scheduleId 课程表ID
     * @return 选课记录列表
     */
    List<CourseSelection> findByScheduleIdAndDeleted(Long scheduleId, Integer deleted);

    /**
     * 根据学期查找选课记录
     *
     * @param semester 学期
     * @return 选课记录列表
     */
    List<CourseSelection> findBySemesterAndDeleted(String semester, Integer deleted);

    /**
     * 检查学生是否已选择课程
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 是否已选择
     */
    boolean existsByStudentIdAndCourseIdAndDeleted(Long studentId, Long courseId, Integer deleted);

    /**
     * 检查学生是否已选择课程表
     *
     * @param studentId 学生ID
     * @param scheduleId 课程表ID
     * @return 是否已选择
     */
    boolean existsByStudentIdAndScheduleIdAndDeleted(Long studentId, Long scheduleId, Integer deleted);

    /**
     * 根据学生ID和课程表ID查找选课记录
     *
     * @param studentId 学生ID
     * @param scheduleId 课程表ID
     * @param deleted 删除标志
     * @return 选课记录列表
     */
    List<CourseSelection> findByStudentIdAndScheduleIdAndDeleted(Long studentId, Long scheduleId, Integer deleted);

    /**
     * 根据学生ID和学期查找选课记录
     *
     * @param studentId 学生ID
     * @param semester 学期
     * @param deleted 删除标志
     * @return 选课记录列表
     */
    List<CourseSelection> findByStudentIdAndSemesterAndDeleted(Long studentId, String semester, Integer deleted);

    /**
     * 根据课程ID和学期查找选课记录
     *
     * @param courseId 课程ID
     * @param semester 学期
     * @param deleted 删除标志
     * @return 选课记录列表
     */
    List<CourseSelection> findByCourseIdAndSemesterAndDeleted(Long courseId, String semester, Integer deleted);

    /**
     * 获取选课详情（包含学生、课程和课程表信息）
     */
    @Query("""
        SELECT cs, s.studentNo, u.realName, c.courseName, c.courseCode
        FROM CourseSelection cs
        LEFT JOIN Student s ON cs.studentId = s.id AND s.deleted = 0
        LEFT JOIN User u ON s.userId = u.id AND u.deleted = 0
        LEFT JOIN Course c ON cs.courseId = c.id AND c.deleted = 0
        WHERE cs.id = :selectionId AND cs.deleted = 0
        """)
    Optional<Object[]> findSelectionDetailById(@Param("selectionId") Long selectionId);

    /**
     * 获取学生的选课详情列表
     */
    @Query("""
        SELECT cs, c.courseName, c.courseCode, c.credits,
               csch.dayOfWeek, csch.startTime, csch.endTime, csch.classroom,
               u.realName
        FROM CourseSelection cs
        LEFT JOIN Course c ON cs.courseId = c.id AND c.deleted = 0
        LEFT JOIN CourseSchedule csch ON cs.scheduleId = csch.id AND csch.deleted = 0
        LEFT JOIN User u ON csch.teacherId = u.id AND u.deleted = 0
        WHERE cs.studentId = :studentId AND cs.semester = :semester AND cs.deleted = 0
        ORDER BY csch.dayOfWeek, csch.startTime
        """)
    List<Object[]> findStudentSelectionDetails(@Param("studentId") Long studentId, @Param("semester") String semester);

    /**
     * 获取课程表的选课学生列表
     */
    @Query("""
        SELECT cs, s.studentNo, u.realName, u.email, u.phone
        FROM CourseSelection cs
        LEFT JOIN Student s ON cs.studentId = s.id AND s.deleted = 0
        LEFT JOIN User u ON s.userId = u.id AND u.deleted = 0
        WHERE cs.scheduleId = :scheduleId AND cs.deleted = 0
        ORDER BY s.studentNo
        """)
    List<Object[]> findScheduleStudentDetails(@Param("scheduleId") Long scheduleId);
}

