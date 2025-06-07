package com.campus.domain.repository;

import com.campus.domain.entity.CourseSelection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 选课记录Repository接口
 * 提供选课记录相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface CourseSelectionRepository extends BaseRepository<CourseSelection> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据学生ID查找选课记录
     */
    @Query("SELECT cs FROM CourseSelection cs WHERE cs.studentId = :studentId AND cs.deleted = 0 ORDER BY cs.semester DESC, cs.courseId")
    List<CourseSelection> findByStudentId(@Param("studentId") Long studentId);

    /**
     * 分页根据学生ID查找选课记录
     */
    @Query("SELECT cs FROM CourseSelection cs WHERE cs.studentId = :studentId AND cs.deleted = 0")
    Page<CourseSelection> findByStudentId(@Param("studentId") Long studentId, Pageable pageable);

    /**
     * 根据课程ID查找选课记录
     */
    @Query("SELECT cs FROM CourseSelection cs WHERE cs.courseId = :courseId AND cs.deleted = 0 ORDER BY cs.selectionTime DESC")
    List<CourseSelection> findByCourseId(@Param("courseId") Long courseId);

    /**
     * 分页根据课程ID查找选课记录
     */
    @Query("SELECT cs FROM CourseSelection cs WHERE cs.courseId = :courseId AND cs.deleted = 0")
    Page<CourseSelection> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    /**
     * 根据课程表ID查找选课记录
     */
    @Query("SELECT cs FROM CourseSelection cs WHERE cs.scheduleId = :scheduleId AND cs.deleted = 0 ORDER BY cs.selectionTime DESC")
    List<CourseSelection> findByScheduleId(@Param("scheduleId") Long scheduleId);

    /**
     * 根据学期查找选课记录
     */
    @Query("SELECT cs FROM CourseSelection cs WHERE cs.semester = :semester AND cs.deleted = 0 ORDER BY cs.studentId, cs.courseId")
    List<CourseSelection> findBySemester(@Param("semester") String semester);

    /**
     * 根据选课状态查找选课记录
     */
    @Query("SELECT cs FROM CourseSelection cs WHERE cs.selectionStatus = :status AND cs.deleted = 0 ORDER BY cs.selectionTime DESC")
    List<CourseSelection> findBySelectionStatus(@Param("status") String selectionStatus);

    /**
     * 根据学生ID和课程ID查找选课记录
     */
    @Query("SELECT cs FROM CourseSelection cs WHERE cs.studentId = :studentId AND cs.courseId = :courseId AND cs.deleted = 0")
    Optional<CourseSelection> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 根据学生ID和学期查找选课记录
     */
    @Query("SELECT cs FROM CourseSelection cs WHERE cs.studentId = :studentId AND cs.semester = :semester AND cs.deleted = 0 ORDER BY cs.courseId")
    List<CourseSelection> findByStudentIdAndSemester(@Param("studentId") Long studentId, @Param("semester") String semester);

    /**
     * 根据课程ID和学期查找选课记录
     */
    @Query("SELECT cs FROM CourseSelection cs WHERE cs.courseId = :courseId AND cs.semester = :semester AND cs.deleted = 0 ORDER BY cs.selectionTime DESC")
    List<CourseSelection> findByCourseIdAndSemester(@Param("courseId") Long courseId, @Param("semester") String semester);



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
    @Query("SELECT cs, s.studentNo, u.realName, u.email, u.phone " +
           "FROM CourseSelection cs " +
           "LEFT JOIN Student s ON cs.studentId = s.id AND s.deleted = 0 " +
           "LEFT JOIN User u ON s.userId = u.id AND u.deleted = 0 " +
           "WHERE cs.scheduleId = :scheduleId AND cs.deleted = 0 " +
           "ORDER BY s.studentNo")
    List<Object[]> findScheduleStudentDetails(@Param("scheduleId") Long scheduleId);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找选课记录
     */
    @Query("SELECT cs FROM CourseSelection cs WHERE " +
           "(:studentId IS NULL OR cs.studentId = :studentId) AND " +
           "(:courseId IS NULL OR cs.courseId = :courseId) AND " +
           "(:semester IS NULL OR cs.semester = :semester) AND " +
           "(:selectionStatus IS NULL OR cs.selectionStatus = :selectionStatus) AND " +
           "cs.deleted = 0")
    Page<CourseSelection> findByMultipleConditions(@Param("studentId") Long studentId,
                                                  @Param("courseId") Long courseId,
                                                  @Param("semester") String semester,
                                                  @Param("selectionStatus") String selectionStatus,
                                                  Pageable pageable);

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 查找选课记录并预加载学生信息
     */
    @Query("SELECT DISTINCT cs FROM CourseSelection cs LEFT JOIN FETCH cs.student WHERE cs.deleted = 0")
    List<CourseSelection> findAllWithStudent();

    /**
     * 查找选课记录并预加载课程信息
     */
    @Query("SELECT DISTINCT cs FROM CourseSelection cs LEFT JOIN FETCH cs.course WHERE cs.deleted = 0")
    List<CourseSelection> findAllWithCourse();

    /**
     * 查找选课记录并预加载所有关联信息
     */
    @Query("SELECT DISTINCT cs FROM CourseSelection cs " +
           "LEFT JOIN FETCH cs.student s " +
           "LEFT JOIN FETCH cs.course c " +
           "LEFT JOIN FETCH cs.courseSchedule sch " +
           "WHERE cs.deleted = 0")
    List<CourseSelection> findAllWithAssociations();

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据课程统计选课人数
     */
    @Query("SELECT c.courseName, COUNT(cs) FROM CourseSelection cs LEFT JOIN cs.course c " +
           "WHERE cs.deleted = 0 GROUP BY cs.courseId, c.courseName ORDER BY COUNT(cs) DESC")
    List<Object[]> countByCourse();

    /**
     * 根据学期统计选课人数
     */
    @Query("SELECT cs.semester, COUNT(cs) FROM CourseSelection cs WHERE cs.deleted = 0 GROUP BY cs.semester ORDER BY cs.semester DESC")
    List<Object[]> countBySemester();

    /**
     * 根据选课状态统计数量
     */
    @Query("SELECT cs.selectionStatus, COUNT(cs) FROM CourseSelection cs WHERE cs.deleted = 0 GROUP BY cs.selectionStatus")
    List<Object[]> countBySelectionStatus();

    /**
     * 统计指定课程的选课人数
     */
    @Query("SELECT COUNT(cs) FROM CourseSelection cs WHERE cs.courseId = :courseId AND cs.deleted = 0")
    long countByCourseId(@Param("courseId") Long courseId);

    /**
     * 统计指定学生的选课数量
     */
    @Query("SELECT COUNT(cs) FROM CourseSelection cs WHERE cs.studentId = :studentId AND cs.semester = :semester AND cs.deleted = 0")
    long countByStudentIdAndSemester(@Param("studentId") Long studentId, @Param("semester") String semester);

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查学生是否已选择课程
     */
    @Query("SELECT CASE WHEN COUNT(cs) > 0 THEN true ELSE false END FROM CourseSelection cs WHERE cs.studentId = :studentId AND cs.courseId = :courseId AND cs.deleted = 0")
    boolean existsByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 检查学生是否已选择课程表
     */
    @Query("SELECT CASE WHEN COUNT(cs) > 0 THEN true ELSE false END FROM CourseSelection cs WHERE cs.studentId = :studentId AND cs.scheduleId = :scheduleId AND cs.deleted = 0")
    boolean existsByStudentIdAndScheduleId(@Param("studentId") Long studentId, @Param("scheduleId") Long scheduleId);

    /**
     * 检查学生在指定学期是否有选课记录
     */
    @Query("SELECT CASE WHEN COUNT(cs) > 0 THEN true ELSE false END FROM CourseSelection cs WHERE cs.studentId = :studentId AND cs.semester = :semester AND cs.deleted = 0")
    boolean existsByStudentIdAndSemester(@Param("studentId") Long studentId, @Param("semester") String semester);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新选课状态
     */
    @Modifying
    @Query("UPDATE CourseSelection cs SET cs.selectionStatus = :status, cs.updatedAt = CURRENT_TIMESTAMP WHERE cs.id = :selectionId")
    int updateSelectionStatus(@Param("selectionId") Long selectionId, @Param("status") String selectionStatus);

    /**
     * 批量更新选课状态
     */
    @Modifying
    @Query("UPDATE CourseSelection cs SET cs.selectionStatus = :status, cs.updatedAt = CURRENT_TIMESTAMP WHERE cs.id IN :selectionIds")
    int batchUpdateSelectionStatus(@Param("selectionIds") List<Long> selectionIds, @Param("status") String selectionStatus);

    // ================================
    // 兼容性方法（为现有Service提供支持）
    // ================================

    /**
     * 根据学生ID查找选课记录（兼容性方法）
     */
    @Query("SELECT cs FROM CourseSelection cs WHERE cs.studentId = :studentId AND cs.deleted = :deleted")
    List<CourseSelection> findByStudentIdAndDeleted(@Param("studentId") Long studentId, @Param("deleted") Integer deleted);

    /**
     * 根据课程ID查找选课记录（兼容性方法）
     */
    @Query("SELECT cs FROM CourseSelection cs WHERE cs.courseId = :courseId AND cs.deleted = :deleted")
    List<CourseSelection> findByCourseIdAndDeleted(@Param("courseId") Long courseId, @Param("deleted") Integer deleted);

    /**
     * 根据课程表ID查找选课记录（兼容性方法）
     */
    @Query("SELECT cs FROM CourseSelection cs WHERE cs.scheduleId = :scheduleId AND cs.deleted = :deleted")
    List<CourseSelection> findByScheduleIdAndDeleted(@Param("scheduleId") Long scheduleId, @Param("deleted") Integer deleted);

    /**
     * 根据学期查找选课记录（兼容性方法）
     */
    @Query("SELECT cs FROM CourseSelection cs WHERE cs.semester = :semester AND cs.deleted = :deleted")
    List<CourseSelection> findBySemesterAndDeleted(@Param("semester") String semester, @Param("deleted") Integer deleted);

    /**
     * 检查学生是否已选择课程（兼容性方法）
     */
    @Query("SELECT CASE WHEN COUNT(cs) > 0 THEN true ELSE false END FROM CourseSelection cs WHERE cs.studentId = :studentId AND cs.courseId = :courseId AND cs.deleted = :deleted")
    boolean existsByStudentIdAndCourseIdAndDeleted(@Param("studentId") Long studentId, @Param("courseId") Long courseId, @Param("deleted") Integer deleted);

    /**
     * 检查学生是否已选择课程表（兼容性方法）
     */
    @Query("SELECT CASE WHEN COUNT(cs) > 0 THEN true ELSE false END FROM CourseSelection cs WHERE cs.studentId = :studentId AND cs.scheduleId = :scheduleId AND cs.deleted = :deleted")
    boolean existsByStudentIdAndScheduleIdAndDeleted(@Param("studentId") Long studentId, @Param("scheduleId") Long scheduleId, @Param("deleted") Integer deleted);

    /**
     * 根据学生ID和课程表ID查找选课记录（兼容性方法）
     */
    @Query("SELECT cs FROM CourseSelection cs WHERE cs.studentId = :studentId AND cs.scheduleId = :scheduleId AND cs.deleted = :deleted")
    List<CourseSelection> findByStudentIdAndScheduleIdAndDeleted(@Param("studentId") Long studentId, @Param("scheduleId") Long scheduleId, @Param("deleted") Integer deleted);

    /**
     * 根据学生ID和学期查找选课记录（兼容性方法）
     */
    @Query("SELECT cs FROM CourseSelection cs WHERE cs.studentId = :studentId AND cs.semester = :semester AND cs.deleted = :deleted")
    List<CourseSelection> findByStudentIdAndSemesterAndDeleted(@Param("studentId") Long studentId, @Param("semester") String semester, @Param("deleted") Integer deleted);

    /**
     * 根据课程ID和学期查找选课记录（兼容性方法）
     */
    @Query("SELECT cs FROM CourseSelection cs WHERE cs.courseId = :courseId AND cs.semester = :semester AND cs.deleted = :deleted")
    List<CourseSelection> findByCourseIdAndSemesterAndDeleted(@Param("courseId") Long courseId, @Param("semester") String semester, @Param("deleted") Integer deleted);

}

