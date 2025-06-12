package com.campus.domain.repository.academic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campus.domain.entity.academic.Attendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 考勤数据访问接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // ==================== 基础查询方法 ====================

    /**
     * 根据学生ID查找考勤记录
     */
    List<Attendance> findByStudentIdAndDeleted(Long studentId, Integer deleted);

    /**
     * 根据学生ID分页查找考勤记录
     */
    Page<Attendance> findByStudentIdAndDeleted(Long studentId, Integer deleted, Pageable pageable);

    /**
     * 根据课程ID查找考勤记录
     */
    List<Attendance> findByCourseIdAndDeleted(Long courseId, Integer deleted);

    /**
     * 根据课程ID分页查找考勤记录
     */
    Page<Attendance> findByCourseIdAndDeleted(Long courseId, Integer deleted, Pageable pageable);

    /**
     * 根据学生ID和课程ID查找考勤记录
     */
    List<Attendance> findByStudentIdAndCourseIdAndDeleted(Long studentId, Long courseId, Integer deleted);

    /**
     * 根据考勤日期查找考勤记录
     */
    List<Attendance> findByAttendanceDateAndDeleted(LocalDate attendanceDate, Integer deleted);

    /**
     * 根据考勤状态查找考勤记录
     */
    List<Attendance> findByAttendanceStatusAndDeleted(String attendanceStatus, Integer deleted);

    /**
     * 根据日期范围查找考勤记录
     */
    List<Attendance> findByAttendanceDateBetweenAndDeleted(LocalDate startDate, LocalDate endDate, Integer deleted);

    /**
     * 查找需要审批的请假记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.attendanceStatus = 'leave' AND a.isApproved IS NULL AND a.deleted = 0")
    List<Attendance> findPendingLeaveRequests();

    /**
     * 查找今日考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE a.attendanceDate = CURRENT_DATE AND a.deleted = 0")
    List<Attendance> findTodayAttendance();

    /**
     * 查找本周考勤记录（使用已有的日期范围查询方法）
     */
    // 使用 findByAttendanceDateBetweenAndDeleted 方法

    /**
     * 查找本月考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE YEAR(a.attendanceDate) = :year AND MONTH(a.attendanceDate) = :month AND a.deleted = 0")
    List<Attendance> findMonthlyAttendance(@Param("year") int year, @Param("month") int month);

    // ==================== 统计查询方法 ====================

    /**
     * 统计学生考勤数量
     */
    long countByStudentIdAndDeleted(Long studentId, Integer deleted);

    /**
     * 统计课程考勤数量
     */
    long countByCourseIdAndDeleted(Long courseId, Integer deleted);

    /**
     * 统计学生和课程的考勤数量
     */
    long countByStudentIdAndCourseIdAndDeleted(Long studentId, Long courseId, Integer deleted);

    /**
     * 计算学生出勤率
     */
    @Query("SELECT " +
           "COUNT(CASE WHEN a.attendanceStatus = 'present' THEN 1 END) * 100.0 / COUNT(*) " +
           "FROM Attendance a WHERE a.studentId = :studentId AND a.deleted = 0")
    Double calculateAttendanceRateByStudentId(@Param("studentId") Long studentId);

    /**
     * 计算课程出勤率
     */
    @Query("SELECT " +
           "COUNT(CASE WHEN a.attendanceStatus = 'present' THEN 1 END) * 100.0 / COUNT(*) " +
           "FROM Attendance a WHERE a.courseId = :courseId AND a.deleted = 0")
    Double calculateAttendanceRateByCourseId(@Param("courseId") Long courseId);

    /**
     * 计算指定时间范围内的出勤率
     */
    @Query("SELECT " +
           "COUNT(CASE WHEN a.attendanceStatus = 'present' THEN 1 END) * 100.0 / COUNT(*) " +
           "FROM Attendance a WHERE a.attendanceDate BETWEEN :startDate AND :endDate AND a.deleted = 0")
    Double calculateAttendanceRateByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // ==================== 复杂查询方法 ====================

    /**
     * 根据条件分页查询考勤记录
     */
    @Query("SELECT a FROM Attendance a WHERE " +
           "(:studentId IS NULL OR a.studentId = :studentId) AND " +
           "(:courseId IS NULL OR a.courseId = :courseId) AND " +
           "(:attendanceStatus IS NULL OR a.attendanceStatus = :attendanceStatus) AND " +
           "(:startDate IS NULL OR a.attendanceDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.attendanceDate <= :endDate) AND " +
           "a.deleted = 0")
    Page<Attendance> findByConditions(@Param("studentId") Long studentId,
                                    @Param("courseId") Long courseId,
                                    @Param("attendanceStatus") String attendanceStatus,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate,
                                    Pageable pageable);

    /**
     * 获取考勤统计信息
     */
    @Query("SELECT " +
           "COUNT(*) as totalRecords, " +
           "COUNT(CASE WHEN a.attendanceStatus = 'present' THEN 1 END) as presentCount, " +
           "COUNT(CASE WHEN a.attendanceStatus = 'absent' THEN 1 END) as absentCount, " +
           "COUNT(CASE WHEN a.attendanceStatus = 'late' THEN 1 END) as lateCount, " +
           "COUNT(CASE WHEN a.attendanceStatus = 'leave' THEN 1 END) as leaveCount " +
           "FROM Attendance a WHERE " +
           "(:studentId IS NULL OR a.studentId = :studentId) AND " +
           "(:courseId IS NULL OR a.courseId = :courseId) AND " +
           "a.deleted = 0")
    List<Object[]> getAttendanceStatistics(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 获取学生考勤统计
     */
    @Query("SELECT " +
           "COUNT(*) as totalRecords, " +
           "COUNT(CASE WHEN a.attendanceStatus = 'present' THEN 1 END) as presentCount, " +
           "COUNT(CASE WHEN a.attendanceStatus = 'absent' THEN 1 END) as absentCount, " +
           "COUNT(CASE WHEN a.attendanceStatus = 'late' THEN 1 END) as lateCount, " +
           "COUNT(CASE WHEN a.attendanceStatus = 'leave' THEN 1 END) as leaveCount, " +
           "AVG(CASE WHEN a.lateMinutes IS NOT NULL THEN a.lateMinutes ELSE 0 END) as avgLateMinutes " +
           "FROM Attendance a WHERE a.studentId = :studentId AND " +
           "(:startDate IS NULL OR a.attendanceDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.attendanceDate <= :endDate) AND " +
           "a.deleted = 0")
    List<Object[]> getStudentAttendanceStatistics(@Param("studentId") Long studentId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);

    /**
     * 获取课程考勤统计
     */
    @Query("SELECT " +
           "COUNT(*) as totalRecords, " +
           "COUNT(CASE WHEN a.attendanceStatus = 'present' THEN 1 END) as presentCount, " +
           "COUNT(CASE WHEN a.attendanceStatus = 'absent' THEN 1 END) as absentCount, " +
           "COUNT(CASE WHEN a.attendanceStatus = 'late' THEN 1 END) as lateCount, " +
           "COUNT(CASE WHEN a.attendanceStatus = 'leave' THEN 1 END) as leaveCount, " +
           "COUNT(DISTINCT a.studentId) as totalStudents " +
           "FROM Attendance a WHERE a.courseId = :courseId AND " +
           "(:startDate IS NULL OR a.attendanceDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.attendanceDate <= :endDate) AND " +
           "a.deleted = 0")
    List<Object[]> getCourseAttendanceStatistics(@Param("courseId") Long courseId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    /**
     * 获取班级考勤统计
     */
    @Query("SELECT " +
           "COUNT(*) as totalRecords, " +
           "COUNT(CASE WHEN a.attendanceStatus = 'present' THEN 1 END) as presentCount, " +
           "COUNT(CASE WHEN a.attendanceStatus = 'absent' THEN 1 END) as absentCount, " +
           "COUNT(CASE WHEN a.attendanceStatus = 'late' THEN 1 END) as lateCount, " +
           "COUNT(CASE WHEN a.attendanceStatus = 'leave' THEN 1 END) as leaveCount, " +
           "COUNT(DISTINCT a.studentId) as totalStudents " +
           "FROM Attendance a JOIN Student s ON a.studentId = s.id " +
           "WHERE s.classId = :classId AND " +
           "(:startDate IS NULL OR a.attendanceDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.attendanceDate <= :endDate) AND " +
           "a.deleted = 0 AND s.deleted = 0")
    List<Object[]> getClassAttendanceStatistics(@Param("classId") Long classId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    // ==================== 验证方法 ====================

    /**
     * 检查学生今日是否已签到
     */
    @Query("SELECT COUNT(a) > 0 FROM Attendance a WHERE a.studentId = :studentId AND a.courseId = :courseId AND a.attendanceDate = CURRENT_DATE AND a.checkInTime IS NOT NULL AND a.deleted = 0")
    boolean hasCheckedInToday(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

    /**
     * 检查考勤记录是否存在
     */
    boolean existsByStudentIdAndCourseIdAndAttendanceDateAndDeleted(Long studentId, Long courseId, LocalDate date, Integer deleted);

    /**
     * 根据学生ID和课程ID和日期查找考勤记录
     */
    Optional<Attendance> findByStudentIdAndCourseIdAndAttendanceDateAndDeleted(Long studentId, Long courseId, LocalDate date, Integer deleted);

    // ==================== 更新方法 ====================

    /**
     * 更新考勤状态
     */
    @Modifying
    @Query("UPDATE Attendance a SET a.attendanceStatus = :status, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id IN :ids")
    int batchUpdateAttendanceStatus(@Param("ids") List<Long> ids, @Param("status") String status);

    /**
     * 更新签退时间
     */
    @Modifying
    @Query("UPDATE Attendance a SET a.checkOutTime = :checkOutTime, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id = :id")
    int updateCheckOutTime(@Param("id") Long id, @Param("checkOutTime") LocalDateTime checkOutTime);

    /**
     * 更新迟到信息
     */
    @Modifying
    @Query("UPDATE Attendance a SET a.attendanceStatus = 'late', a.lateMinutes = :minutes, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id = :id")
    int updateLateInfo(@Param("id") Long id, @Param("minutes") int minutes);

    /**
     * 更新早退信息
     */
    @Modifying
    @Query("UPDATE Attendance a SET a.earlyLeaveMinutes = :minutes, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id = :id")
    int updateEarlyLeaveInfo(@Param("id") Long id, @Param("minutes") int minutes);

    /**
     * 更新请假审批状态
     */
    @Modifying
    @Query("UPDATE Attendance a SET a.isApproved = :isApproved, a.approverId = :approverId, a.approvalTime = CURRENT_TIMESTAMP, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id = :id")
    int updateLeaveApproval(@Param("id") Long id, @Param("isApproved") Boolean isApproved, @Param("approverId") Long approverId);

    /**
     * 更新状态
     */
    @Modifying
    @Query("UPDATE Attendance a SET a.status = :status, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 批量更新状态
     */
    @Modifying
    @Query("UPDATE Attendance a SET a.status = :status, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id IN :ids")
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);
}
