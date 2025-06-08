package com.campus.domain.repository;

import com.campus.domain.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 课表Repository接口
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    /**
     * 根据学期查找课表
     */
    List<Schedule> findBySemesterOrderByWeekdayAscStartTimeAsc(String semester);

    /**
     * 根据教师ID和学期查找课表
     */
    List<Schedule> findByTeacherIdAndSemesterOrderByWeekdayAscStartTimeAsc(Long teacherId, String semester);

    /**
     * 根据教室ID和学期查找课表
     */
    List<Schedule> findByClassroomIdAndSemesterOrderByWeekdayAscStartTimeAsc(Long classroomId, String semester);

    /**
     * 根据状态查找课表
     */
    List<Schedule> findByStatus(Integer status);

    /**
     * 查找有冲突的课表
     */
    List<Schedule> findByHasConflictTrueAndSemester(String semester);

    /**
     * 根据课程ID查找课表
     */
    List<Schedule> findByCourseId(Long courseId);

    // ================================
    // 日程管理相关查询方法 (用于ScheduleServiceImpl)
    // ================================

    /**
     * 根据ID和删除状态查找日程
     */
    Optional<Schedule> findByIdAndDeleted(Long id, Integer deleted);

    /**
     * 分页查询未删除的日程，按开始时间倒序
     */
    Page<Schedule> findByDeletedOrderByStartTimeDesc(Integer deleted, Pageable pageable);

    /**
     * 根据用户ID查找未删除的日程，按开始时间升序
     */
    List<Schedule> findByUserIdAndDeletedOrderByStartTimeAsc(Long userId, Integer deleted);

    /**
     * 根据用户ID和时间范围查找未删除的日程，按开始时间升序
     */
    @Query("SELECT s FROM Schedule s WHERE s.userId = :userId AND s.scheduleStartTime BETWEEN :startTime AND :endTime AND s.deleted = :deleted ORDER BY s.scheduleStartTime ASC")
    List<Schedule> findByUserIdAndStartTimeBetweenAndDeletedOrderByStartTimeAsc(
        @Param("userId") Long userId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("deleted") Integer deleted);

    /**
     * 根据用户ID、日程类型和删除状态查找日程，按开始时间升序
     */
    List<Schedule> findByUserIdAndScheduleTypeAndDeletedOrderByStartTimeAsc(Long userId, String scheduleType, Integer deleted);

    /**
     * 查找冲突的日程（排除指定日程）
     */
    @Query("SELECT s FROM Schedule s WHERE s.userId = :userId AND s.id != :excludeId AND " +
           "((s.scheduleStartTime <= :startTime AND s.scheduleEndTime > :startTime) OR " +
           "(s.scheduleStartTime < :endTime AND s.scheduleEndTime >= :endTime) OR " +
           "(s.scheduleStartTime >= :startTime AND s.scheduleEndTime <= :endTime)) AND s.deleted = :deleted")
    List<Schedule> findConflictingSchedulesExcluding(
        @Param("userId") Long userId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("excludeId") Long excludeId,
        @Param("deleted") Integer deleted);

    /**
     * 查找冲突的日程
     */
    @Query("SELECT s FROM Schedule s WHERE s.userId = :userId AND " +
           "((s.scheduleStartTime <= :startTime AND s.scheduleEndTime > :startTime) OR " +
           "(s.scheduleStartTime < :endTime AND s.scheduleEndTime >= :endTime) OR " +
           "(s.scheduleStartTime >= :startTime AND s.scheduleEndTime <= :endTime)) AND s.deleted = :deleted")
    List<Schedule> findConflictingSchedules(
        @Param("userId") Long userId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("deleted") Integer deleted);

    /**
     * 根据用户ID、标题关键字和删除状态搜索日程，按开始时间倒序
     */
    Page<Schedule> findByUserIdAndTitleContainingIgnoreCaseAndDeletedOrderByStartTimeDesc(
        Long userId, String keyword, Integer deleted, Pageable pageable);

    /**
     * 统计用户的日程数量
     */
    long countByUserIdAndDeleted(Long userId, Integer deleted);

    /**
     * 统计用户在指定时间范围内的日程数量
     */
    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.userId = :userId AND s.scheduleStartTime BETWEEN :startTime AND :endTime AND s.deleted = :deleted")
    long countByUserIdAndStartTimeBetweenAndDeleted(
        @Param("userId") Long userId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        @Param("deleted") Integer deleted);

    /**
     * 获取用户的日程统计信息（按类型分组）
     */
    @Query("SELECT s.scheduleType, COUNT(s) FROM Schedule s WHERE s.userId = :userId AND s.deleted = 0 GROUP BY s.scheduleType")
    List<Object[]> getScheduleStatisticsByType(@Param("userId") Long userId);

    /**
     * 查找需要提醒的日程
     */
    @Query("SELECT s FROM Schedule s WHERE s.scheduleStartTime BETWEEN :now AND :reminderTime AND s.deleted = :deleted")
    List<Schedule> findSchedulesNeedingReminder(
        @Param("now") LocalDateTime now,
        @Param("reminderTime") LocalDateTime reminderTime,
        @Param("deleted") Integer deleted);

    /**
     * 查找指定时间之前结束的日程
     */
    @Query("SELECT s FROM Schedule s WHERE s.scheduleEndTime < :cutoffDate AND s.deleted = :deleted")
    List<Schedule> findByEndTimeBeforeAndDeleted(
        @Param("cutoffDate") LocalDateTime cutoffDate,
        @Param("deleted") Integer deleted);
}
