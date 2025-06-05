package com.campus.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campus.domain.entity.Grade;

/**
 * 成绩数据访问层
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    /**
     * 根据学生ID查找成绩记录
     *
     * @param studentId 学生ID
     * @return 成绩记录列表
     */
    List<Grade> findByStudentIdAndDeleted(Long studentId, Integer deleted);

    /**
     * 根据课程ID查找成绩记录
     *
     * @param courseId 课程ID
     * @return 成绩记录列表
     */
    List<Grade> findByCourseIdAndDeleted(Long courseId, Integer deleted);

    /**
     * 根据课程表ID查找成绩记录
     *
     * @param scheduleId 课程表ID
     * @return 成绩记录列表
     */
    List<Grade> findByScheduleIdAndDeleted(Long scheduleId, Integer deleted);

    /**
     * 根据选课ID查找成绩记录
     *
     * @param selectionId 选课ID
     * @return 成绩记录
     */
    Optional<Grade> findBySelectionIdAndDeleted(Long selectionId, Integer deleted);

    /**
     * 根据学期查找成绩记录
     *
     * @param semester 学期
     * @return 成绩记录列表
     */
    List<Grade> findBySemesterAndDeleted(String semester, Integer deleted);

    /**
     * 根据学生ID和课程ID查找成绩记录
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 成绩记录列表
     */
    List<Grade> findByStudentIdAndCourseIdAndDeleted(Long studentId, Long courseId, Integer deleted);

    /**
     * 根据学生ID和学期查找成绩记录
     *
     * @param studentId 学生ID
     * @param semester 学期
     * @return 成绩记录列表
     */
    List<Grade> findByStudentIdAndSemesterAndDeleted(Long studentId, String semester, Integer deleted);

    /**
     * 获取成绩详情（包含学生、课程和教师信息）
     */
    @Query("""
        SELECT g, s.studentNo, u.realName,
               c.courseName, c.courseCode, c.credits,
               t.realName
        FROM Grade g
        LEFT JOIN Student s ON g.studentId = s.id AND s.deleted = 0
        LEFT JOIN User u ON s.userId = u.id AND u.deleted = 0
        LEFT JOIN Course c ON g.courseId = c.id AND c.deleted = 0
        LEFT JOIN User t ON g.teacherId = t.id AND t.deleted = 0
        WHERE g.id = :gradeId AND g.deleted = 0
        """)
    Optional<Object[]> findGradeDetailById(@Param("gradeId") Long gradeId);

    /**
     * 获取学生的成绩详情列表
     */
    @Query("""
        SELECT g, c.courseName, c.courseCode, c.credits,
               u.realName
        FROM Grade g
        LEFT JOIN Course c ON g.courseId = c.id AND c.deleted = 0
        LEFT JOIN User u ON g.teacherId = u.id AND u.deleted = 0
        WHERE g.studentId = :studentId AND g.semester = :semester AND g.deleted = 0
        ORDER BY g.courseId
        """)
    List<Object[]> findStudentGradeDetails(@Param("studentId") Long studentId, @Param("semester") String semester);

    /**
     * 获取课程的学生成绩列表
     */
    @Query("""
        SELECT g, s.studentNo, u.realName
        FROM Grade g
        LEFT JOIN Student s ON g.studentId = s.id AND s.deleted = 0
        LEFT JOIN User u ON s.userId = u.id AND u.deleted = 0
        WHERE g.courseId = :courseId AND g.scheduleId = :scheduleId AND g.deleted = 0
        ORDER BY s.studentNo
        """)
    List<Object[]> findCourseGradeDetails(@Param("courseId") Long courseId, @Param("scheduleId") Long scheduleId);

    /**
     * 计算学生的平均绩点
     */
    @Query("""
        SELECT COALESCE(SUM(g.gradePoint * c.credits) / SUM(c.credits), 0.0)
        FROM Grade g
        LEFT JOIN Course c ON g.courseId = c.id AND c.deleted = 0
        WHERE g.studentId = :studentId AND g.semester = :semester AND g.deleted = 0 AND g.gradePoint IS NOT NULL
        """)
    Double calculateStudentGPA(@Param("studentId") Long studentId, @Param("semester") String semester);

    /**
     * 计算班级的平均成绩
     */
    @Query("""
        SELECT AVG(g.score)
        FROM Grade g
        LEFT JOIN Student s ON g.studentId = s.id AND s.deleted = 0
        WHERE s.classId = :classId AND g.courseId = :courseId AND g.deleted = 0 AND g.score IS NOT NULL
        """)
    Double calculateClassAverageScore(@Param("classId") Long classId, @Param("courseId") Long courseId);
}

