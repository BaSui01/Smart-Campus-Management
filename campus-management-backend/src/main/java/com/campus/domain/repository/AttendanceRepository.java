package com.campus.domain.repository;

import com.campus.domain.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 考勤Repository接口
 * 提供考勤相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface AttendanceRepository extends BaseRepository<Attendance> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据学生ID查找考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.studentId = :studentId AND a.deleted = 0 ORDER BY a.attendanceDate DESC")
    List<Attendance> findByStudentId(@Param("studentId") Long studentId);

    /**
     * 分页根据学生ID查找考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.studentId = :studentId AND a.deleted = 0")
    Page<Attendance> findByStudentId(@Param("studentId") Long studentId, Pageable pageable);

    /**
     * 根据课程ID查找考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.courseId = :courseId AND a.deleted = 0 ORDER BY a.attendanceDate DESC")
    List<Attendance> findByCourseId(@Param("courseId") Long courseId);

    /**
     * 分页根据课程ID查找考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.courseId = :courseId AND a.deleted = 0")
    Page<Attendance> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    /**
     * 根据教师ID查找考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.teacherId = :teacherId AND a.deleted = 0 ORDER BY a.attendanceDate DESC")
    List<Attendance> findByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 根据考勤状态查找考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.attendanceStatus = :status AND a.deleted = 0 ORDER BY a.attendanceDate DESC")
    List<Attendance> findByAttendanceStatus(@Param("status") String attendanceStatus);

    /**
     * 根据考勤类型查找考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.attendanceType = :type AND a.deleted = 0 ORDER BY a.attendanceDate DESC")
    List<Attendance> findByAttendanceType(@Param("type") String attendanceType);

    /**
     * 根据考勤日期查找考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.attendanceDate = :date AND a.deleted = 0 ORDER BY a.createdAt DESC")
    List<Attendance> findByAttendanceDate(@Param("date") LocalDate attendanceDate);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE " +
           "(:studentId IS NULL OR a.studentId = :studentId) AND " +
           "(:courseId IS NULL OR a.courseId = :courseId) AND " +
           "(:teacherId IS NULL OR a.teacherId = :teacherId) AND " +
           "(:attendanceStatus IS NULL OR a.attendanceStatus = :attendanceStatus) AND " +
           "(:attendanceType IS NULL OR a.attendanceType = :attendanceType) AND " +
           "a.deleted = 0")
    Page<Attendance> findByMultipleConditions(@Param("studentId") Long studentId,
                                             @Param("courseId") Long courseId,
                                             @Param("teacherId") Long teacherId,
                                             @Param("attendanceStatus") String attendanceStatus,
                                             @Param("attendanceType") String attendanceType,
                                             Pageable pageable);

    /**
     * 根据学生和课程查找考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.studentId = :studentId AND a.courseId = :courseId AND a.deleted = 0 ORDER BY a.attendanceDate DESC")
    List<Attendance> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 根据学生和日期查找考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.studentId = :studentId AND a.attendanceDate = :date AND a.deleted = 0")
    List<Attendance> findByStudentIdAndAttendanceDate(@Param("studentId") Long studentId, @Param("date") LocalDate attendanceDate);

    /**
     * 根据课程和日期查找考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.courseId = :courseId AND a.attendanceDate = :date AND a.deleted = 0 ORDER BY a.createdAt DESC")
    List<Attendance> findByCourseIdAndAttendanceDate(@Param("courseId") Long courseId, @Param("date") LocalDate attendanceDate);

    /**
     * 根据学生、课程和日期查找考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.studentId = :studentId AND a.courseId = :courseId AND a.attendanceDate = :date AND a.deleted = 0")
    Optional<Attendance> findByStudentIdAndCourseIdAndAttendanceDate(@Param("studentId") Long studentId, 
                                                                    @Param("courseId") Long courseId, 
                                                                    @Param("date") LocalDate attendanceDate);

    // ================================
    // 时间相关查询
    // ================================

    /**
     * 根据考勤日期范围查找考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.attendanceDate BETWEEN :startDate AND :endDate AND a.deleted = 0 ORDER BY a.attendanceDate DESC")
    List<Attendance> findByAttendanceDateBetween(@Param("startDate") LocalDate startDate, 
                                                 @Param("endDate") LocalDate endDate);

    /**
     * 分页根据考勤日期范围查找考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.attendanceDate BETWEEN :startDate AND :endDate AND a.deleted = 0")
    Page<Attendance> findByAttendanceDateBetween(@Param("startDate") LocalDate startDate, 
                                                 @Param("endDate") LocalDate endDate, 
                                                 Pageable pageable);

    /**
     * 查找今日考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.attendanceDate = CURRENT_DATE AND a.deleted = 0 ORDER BY a.createdAt DESC")
    List<Attendance> findTodayAttendance();

    /**
     * 查找本周考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.attendanceDate BETWEEN :startOfWeek AND :endOfWeek AND a.deleted = 0 ORDER BY a.attendanceDate DESC")
    List<Attendance> findWeeklyAttendance(@Param("startOfWeek") LocalDate startOfWeek, @Param("endOfWeek") LocalDate endOfWeek);

    /**
     * 查找本月考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE YEAR(a.attendanceDate) = :year AND MONTH(a.attendanceDate) = :month AND a.deleted = 0 ORDER BY a.attendanceDate DESC")
    List<Attendance> findMonthlyAttendance(@Param("year") int year, @Param("month") int month);

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 查找考勤记录并预加载学生信息
     */
    @Query("SELECT DISTINCT a FROM Attendance a LEFT JOIN FETCH a.student WHERE a.deleted = 0")
    List<Attendance> findAllWithStudent();

    /**
     * 查找考勤记录并预加载课程信息
     */
    @Query("SELECT DISTINCT a FROM Attendance a LEFT JOIN FETCH a.course WHERE a.deleted = 0")
    List<Attendance> findAllWithCourse();

    /**
     * 查找考勤记录并预加载教师信息
     */
    @Query("SELECT DISTINCT a FROM Attendance a LEFT JOIN FETCH a.teacher WHERE a.deleted = 0")
    List<Attendance> findAllWithTeacher();

    /**
     * 查找考勤记录并预加载所有关联信息
     */
    @Query("SELECT DISTINCT a FROM Attendance a " +
           "LEFT JOIN FETCH a.student s " +
           "LEFT JOIN FETCH a.course c " +
           "LEFT JOIN FETCH a.teacher t " +
           "WHERE a.deleted = 0")
    List<Attendance> findAllWithAssociations();

    /**
     * 根据课程ID查找考勤记录并预加载学生信息
     */
    @Query("SELECT DISTINCT a FROM Attendance a LEFT JOIN FETCH a.student WHERE a.courseId = :courseId AND a.deleted = 0 ORDER BY a.attendanceDate DESC")
    List<Attendance> findByCourseIdWithStudent(@Param("courseId") Long courseId);

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据考勤状态统计数量
     */
    @Query("SELECT a.attendanceStatus, COUNT(a) FROM Attendance a WHERE a.deleted = 0 GROUP BY a.attendanceStatus ORDER BY COUNT(a) DESC")
    List<Object[]> countByAttendanceStatus();

    /**
     * 根据考勤类型统计数量
     */
    @Query("SELECT a.attendanceType, COUNT(a) FROM Attendance a WHERE a.deleted = 0 GROUP BY a.attendanceType ORDER BY COUNT(a) DESC")
    List<Object[]> countByAttendanceType();

    /**
     * 根据课程统计考勤数量
     */
    @Query("SELECT c.courseName, COUNT(a) FROM Attendance a LEFT JOIN a.course c WHERE a.deleted = 0 GROUP BY a.courseId, c.courseName ORDER BY COUNT(a) DESC")
    List<Object[]> countByCourse();

    /**
     * 根据学生统计考勤数量
     */
    @Query("SELECT s.studentNumber, COUNT(a) FROM Attendance a LEFT JOIN a.student s WHERE a.deleted = 0 GROUP BY a.studentId, s.studentNumber ORDER BY COUNT(a) DESC")
    List<Object[]> countByStudent();

    /**
     * 统计指定学生的考勤数量
     */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.studentId = :studentId AND a.deleted = 0")
    long countByStudentId(@Param("studentId") Long studentId);

    /**
     * 统计指定课程的考勤数量
     */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.courseId = :courseId AND a.deleted = 0")
    long countByCourseId(@Param("courseId") Long courseId);

    /**
     * 统计指定学生和课程的考勤数量
     */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.studentId = :studentId AND a.courseId = :courseId AND a.deleted = 0")
    long countByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 统计指定学生的出勤率
     */
    @Query("SELECT " +
           "COUNT(CASE WHEN a.attendanceStatus = 'PRESENT' THEN 1 END) * 100.0 / COUNT(a) " +
           "FROM Attendance a WHERE a.studentId = :studentId AND a.deleted = 0")
    Double calculateAttendanceRateByStudentId(@Param("studentId") Long studentId);

    /**
     * 统计指定课程的出勤率
     */
    @Query("SELECT " +
           "COUNT(CASE WHEN a.attendanceStatus = 'PRESENT' THEN 1 END) * 100.0 / COUNT(a) " +
           "FROM Attendance a WHERE a.courseId = :courseId AND a.deleted = 0")
    Double calculateAttendanceRateByCourseId(@Param("courseId") Long courseId);

    /**
     * 统计指定时间范围内的出勤率
     */
    @Query("SELECT " +
           "COUNT(CASE WHEN a.attendanceStatus = 'PRESENT' THEN 1 END) * 100.0 / COUNT(a) " +
           "FROM Attendance a WHERE a.attendanceDate BETWEEN :startDate AND :endDate AND a.deleted = 0")
    Double calculateAttendanceRateByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查学生在指定日期是否已有考勤记录
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Attendance a WHERE a.studentId = :studentId AND a.attendanceDate = :date AND a.deleted = 0")
    boolean existsByStudentIdAndAttendanceDate(@Param("studentId") Long studentId, @Param("date") LocalDate attendanceDate);

    /**
     * 检查学生在指定课程和日期是否已有考勤记录
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Attendance a WHERE a.studentId = :studentId AND a.courseId = :courseId AND a.attendanceDate = :date AND a.deleted = 0")
    boolean existsByStudentIdAndCourseIdAndAttendanceDate(@Param("studentId") Long studentId, 
                                                         @Param("courseId") Long courseId, 
                                                         @Param("date") LocalDate attendanceDate);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新考勤状态
     */
    @Modifying
    @Query("UPDATE Attendance a SET a.attendanceStatus = :status, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id = :attendanceId")
    int updateAttendanceStatus(@Param("attendanceId") Long attendanceId, @Param("status") String attendanceStatus);

    /**
     * 批量更新考勤状态
     */
    @Modifying
    @Query("UPDATE Attendance a SET a.attendanceStatus = :status, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id IN :attendanceIds")
    int batchUpdateAttendanceStatus(@Param("attendanceIds") List<Long> attendanceIds, @Param("status") String attendanceStatus);

    /**
     * 更新考勤备注
     */
    @Modifying
    @Query("UPDATE Attendance a SET a.remarks = :remarks, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id = :attendanceId")
    int updateRemarks(@Param("attendanceId") Long attendanceId, @Param("remarks") String remarks);

    // ================================
    // 数据清理方法
    // ================================

    /**
     * 删除指定日期之前的考勤记录
     */
    @Modifying
    @Query("UPDATE Attendance a SET a.deleted = 1, a.updatedAt = CURRENT_TIMESTAMP WHERE a.attendanceDate < :beforeDate")
    int deleteAttendanceBefore(@Param("beforeDate") LocalDate beforeDate);

    /**
     * 批量删除指定学生的考勤记录
     */
    @Modifying
    @Query("UPDATE Attendance a SET a.deleted = 1, a.updatedAt = CURRENT_TIMESTAMP WHERE a.studentId = :studentId")
    int deleteAttendanceByStudentId(@Param("studentId") Long studentId);

    /**
     * 批量删除指定课程的考勤记录
     */
    @Modifying
    @Query("UPDATE Attendance a SET a.deleted = 1, a.updatedAt = CURRENT_TIMESTAMP WHERE a.courseId = :courseId")
    int deleteAttendanceByCourseId(@Param("courseId") Long courseId);

}
