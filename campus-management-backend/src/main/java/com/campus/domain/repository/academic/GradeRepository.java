package com.campus.domain.repository.academic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campus.domain.entity.academic.Grade;
import com.campus.domain.repository.infrastructure.BaseRepository;

import java.util.List;
import java.util.Optional;

/**
 * 成绩Repository接口
 * 提供成绩相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface GradeRepository extends BaseRepository<Grade> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据学生ID查找成绩记录
     */
    @Query("SELECT g FROM Grade g WHERE g.studentId = :studentId AND g.deleted = 0 ORDER BY g.semester DESC, g.courseId")
    List<Grade> findByStudentId(@Param("studentId") Long studentId);

    /**
     * 分页根据学生ID查找成绩记录
     */
    @Query("SELECT g FROM Grade g WHERE g.studentId = :studentId AND g.deleted = 0")
    Page<Grade> findByStudentId(@Param("studentId") Long studentId, Pageable pageable);

    /**
     * 根据课程ID查找成绩记录
     */
    @Query("SELECT g FROM Grade g WHERE g.courseId = :courseId AND g.deleted = 0 ORDER BY g.totalScore DESC")
    List<Grade> findByCourseId(@Param("courseId") Long courseId);

    /**
     * 分页根据课程ID查找成绩记录
     */
    @Query("SELECT g FROM Grade g WHERE g.courseId = :courseId AND g.deleted = 0")
    Page<Grade> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    /**
     * 根据教师ID查找成绩记录
     */
    @Query("SELECT g FROM Grade g WHERE g.teacherId = :teacherId AND g.deleted = 0 ORDER BY g.semester DESC, g.courseId")
    List<Grade> findByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 根据学期查找成绩记录
     */
    @Query("SELECT g FROM Grade g WHERE g.semester = :semester AND g.deleted = 0 ORDER BY g.studentId, g.courseId")
    List<Grade> findBySemester(@Param("semester") String semester);

    /**
     * 根据学生ID和课程ID查找成绩记录
     */
    @Query("SELECT g FROM Grade g WHERE g.studentId = :studentId AND g.courseId = :courseId AND g.deleted = 0")
    Optional<Grade> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 根据学生ID和学期查找成绩记录
     */
    @Query("SELECT g FROM Grade g WHERE g.studentId = :studentId AND g.semester = :semester AND g.deleted = 0 ORDER BY g.courseId")
    List<Grade> findByStudentIdAndSemester(@Param("studentId") Long studentId, @Param("semester") String semester);

    /**
     * 根据课程ID和学期查找成绩记录
     */
    @Query("SELECT g FROM Grade g WHERE g.courseId = :courseId AND g.semester = :semester AND g.deleted = 0 ORDER BY g.totalScore DESC")
    List<Grade> findByCourseIdAndSemester(@Param("courseId") Long courseId, @Param("semester") String semester);

    /**
     * 根据分数范围查找成绩记录
     */
    @Query("SELECT g FROM Grade g WHERE g.totalScore BETWEEN :minScore AND :maxScore AND g.deleted = 0 ORDER BY g.totalScore DESC")
    List<Grade> findByScoreBetween(@Param("minScore") Integer minScore, @Param("maxScore") Integer maxScore);

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
        SELECT COALESCE(SUM(g.gpa * c.credits) / SUM(c.credits), 0.0)
        FROM Grade g
        LEFT JOIN Course c ON g.courseId = c.id AND c.deleted = 0
        WHERE g.studentId = :studentId AND g.semester = :semester AND g.deleted = 0 AND g.gpa IS NOT NULL
        """)
    Double calculateStudentGPA(@Param("studentId") Long studentId, @Param("semester") String semester);

    /**
     * 计算班级的平均成绩
     */
    @Query("SELECT AVG(g.totalScore) FROM Grade g LEFT JOIN Student s ON g.studentId = s.id AND s.deleted = 0 " +
           "WHERE s.classId = :classId AND g.courseId = :courseId AND g.deleted = 0 AND g.totalScore IS NOT NULL")
    Double calculateClassAverageScore(@Param("classId") Long classId, @Param("courseId") Long courseId);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找成绩记录
     */
    @Query("SELECT g FROM Grade g WHERE " +
           "(:studentId IS NULL OR g.studentId = :studentId) AND " +
           "(:courseId IS NULL OR g.courseId = :courseId) AND " +
           "(:teacherId IS NULL OR g.teacherId = :teacherId) AND " +
           "(:semester IS NULL OR g.semester = :semester) AND " +
           "(:minScore IS NULL OR g.totalScore >= :minScore) AND " +
           "(:maxScore IS NULL OR g.totalScore <= :maxScore) AND " +
           "g.deleted = 0")
    Page<Grade> findByMultipleConditions(@Param("studentId") Long studentId,
                                        @Param("courseId") Long courseId,
                                        @Param("teacherId") Long teacherId,
                                        @Param("semester") String semester,
                                        @Param("minScore") Integer minScore,
                                        @Param("maxScore") Integer maxScore,
                                        Pageable pageable);

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 查找成绩并预加载学生信息
     */
    @Query("SELECT DISTINCT g FROM Grade g LEFT JOIN FETCH g.student WHERE g.deleted = 0")
    List<Grade> findAllWithStudent();

    /**
     * 查找成绩并预加载课程信息
     */
    @Query("SELECT DISTINCT g FROM Grade g LEFT JOIN FETCH g.course WHERE g.deleted = 0")
    List<Grade> findAllWithCourse();

    /**
     * 查找成绩并预加载所有关联信息
     */
    @Query("SELECT DISTINCT g FROM Grade g " +
           "LEFT JOIN FETCH g.student s " +
           "LEFT JOIN FETCH g.course c " +
           "LEFT JOIN FETCH g.teacher t " +
           "WHERE g.deleted = 0")
    List<Grade> findAllWithAssociations();

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据课程统计成绩分布
     */
    @Query("SELECT " +
           "CASE " +
           "WHEN g.totalScore >= 90 THEN '优秀(90-100)' " +
           "WHEN g.totalScore >= 80 THEN '良好(80-89)' " +
           "WHEN g.totalScore >= 70 THEN '中等(70-79)' " +
           "WHEN g.totalScore >= 60 THEN '及格(60-69)' " +
           "ELSE '不及格(0-59)' " +
           "END as gradeLevel, COUNT(g) " +
           "FROM Grade g WHERE g.courseId = :courseId AND g.deleted = 0 " +
           "GROUP BY " +
           "CASE " +
           "WHEN g.totalScore >= 90 THEN '优秀(90-100)' " +
           "WHEN g.totalScore >= 80 THEN '良好(80-89)' " +
           "WHEN g.totalScore >= 70 THEN '中等(70-79)' " +
           "WHEN g.totalScore >= 60 THEN '及格(60-69)' " +
           "ELSE '不及格(0-59)' " +
           "END " +
           "ORDER BY MIN(g.totalScore) DESC")
    List<Object[]> getGradeDistributionByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据学生统计各科成绩
     */
    @Query("SELECT c.courseName, g.totalScore, g.gpa FROM Grade g LEFT JOIN g.course c " +
           "WHERE g.studentId = :studentId AND g.semester = :semester AND g.deleted = 0 " +
           "ORDER BY c.courseName")
    List<Object[]> getStudentGradesBySemester(@Param("studentId") Long studentId, @Param("semester") String semester);

    /**
     * 统计课程成绩基本信息
     */
    @Query("SELECT " +
           "COUNT(g) as totalCount, " +
           "AVG(g.totalScore) as avgScore, " +
           "MAX(g.totalScore) as maxScore, " +
           "MIN(g.totalScore) as minScore, " +
           "COUNT(CASE WHEN g.totalScore >= 60 THEN 1 END) as passCount " +
           "FROM Grade g WHERE g.courseId = :courseId AND g.semester = :semester AND g.deleted = 0")
    Object[] getCourseGradeStatistics(@Param("courseId") Long courseId, @Param("semester") String semester);

    // ================================
    // 兼容性方法（为现有Service提供支持）
    // ================================

    /**
     * 根据课程表ID查找成绩记录（兼容性方法）
     */
    @Query("SELECT g FROM Grade g WHERE g.scheduleId = :scheduleId AND g.deleted = 0")
    List<Grade> findByScheduleIdAndDeleted(@Param("scheduleId") Long scheduleId);

    /**
     * 根据选课ID查找成绩记录（兼容性方法）
     */
    @Query("SELECT g FROM Grade g WHERE g.selectionId = :selectionId AND g.deleted = 0")
    Optional<Grade> findBySelectionIdAndDeleted(@Param("selectionId") Long selectionId);

    /**
     * 根据学期查找成绩记录（兼容性方法）
     */
    @Query("SELECT g FROM Grade g WHERE g.semester = :semester AND g.deleted = 0")
    List<Grade> findBySemesterAndDeleted(@Param("semester") String semester);

    /**
     * 根据学生ID和课程ID查找成绩记录（兼容性方法）
     */
    @Query("SELECT g FROM Grade g WHERE g.studentId = :studentId AND g.courseId = :courseId AND g.deleted = 0")
    List<Grade> findByStudentIdAndCourseIdAndDeleted(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 根据学生ID和学期查找成绩记录（兼容性方法）
     */
    @Query("SELECT g FROM Grade g WHERE g.studentId = :studentId AND g.semester = :semester AND g.deleted = 0")
    List<Grade> findByStudentIdAndSemesterAndDeleted(@Param("studentId") Long studentId, @Param("semester") String semester);

    /**
     * 根据学生ID查找成绩记录（兼容性方法）
     */
    @Query("SELECT g FROM Grade g WHERE g.studentId = :studentId AND g.deleted = 0")
    List<Grade> findByStudentIdAndDeleted(@Param("studentId") Long studentId);

    /**
     * 根据课程ID查找成绩记录（兼容性方法）
     */
    @Query("SELECT g FROM Grade g WHERE g.courseId = :courseId AND g.deleted = 0")
    List<Grade> findByCourseIdAndDeleted(@Param("courseId") Long courseId);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新成绩分数
     */
    @Modifying
    @Query("UPDATE Grade g SET g.totalScore = :score, g.updatedAt = CURRENT_TIMESTAMP WHERE g.id = :gradeId")
    int updateScore(@Param("gradeId") Long gradeId, @Param("score") Integer score);

    /**
     * 批量更新成绩分数
     */
    @Modifying
    @Query("UPDATE Grade g SET g.totalScore = :score, g.updatedAt = CURRENT_TIMESTAMP WHERE g.id IN :gradeIds")
    int batchUpdateScore(@Param("gradeIds") List<Long> gradeIds, @Param("score") Integer score);

    /**
     * 更新绩点
     */
    @Modifying
    @Query("UPDATE Grade g SET g.gpa = :gradePoint, g.updatedAt = CURRENT_TIMESTAMP WHERE g.id = :gradeId")
    int updateGradePoint(@Param("gradeId") Long gradeId, @Param("gradePoint") Double gradePoint);

}

