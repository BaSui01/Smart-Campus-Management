package com.campus.application.service;

import com.campus.domain.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 日程管理服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
public interface ScheduleService {

    /**
     * 创建日程
     */
    Schedule createSchedule(Schedule schedule);

    /**
     * 根据ID查找日程
     */
    Optional<Schedule> findScheduleById(Long id);

    /**
     * 更新日程
     */
    Schedule updateSchedule(Schedule schedule);

    /**
     * 删除日程
     */
    void deleteSchedule(Long id);

    /**
     * 分页查询所有日程
     */
    Page<Schedule> findAllSchedules(Pageable pageable);

    /**
     * 查询用户的所有日程
     */
    List<Schedule> findSchedulesByUser(Long userId);

    /**
     * 根据时间范围查询日程
     */
    List<Schedule> findSchedulesByDateRange(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据日期查询日程
     */
    List<Schedule> findSchedulesByDate(Long userId, LocalDate date);

    /**
     * 根据类型查询日程
     */
    List<Schedule> findSchedulesByType(Long userId, String scheduleType);

    /**
     * 查询即将到来的日程
     */
    List<Schedule> findUpcomingSchedules(Long userId, int days);

    /**
     * 查询今日日程
     */
    List<Schedule> findTodaySchedules(Long userId);

    /**
     * 检查日程冲突
     */
    boolean checkScheduleConflict(Long userId, LocalDateTime startTime, LocalDateTime endTime, Long excludeScheduleId);

    /**
     * 查找冲突的日程
     */
    List<Schedule> findConflictingSchedules(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 搜索日程
     */
    Page<Schedule> searchSchedules(Long userId, String keyword, Pageable pageable);

    /**
     * 统计用户日程数量
     */
    long countUserSchedules(Long userId);

    /**
     * 统计今日日程数量
     */
    long countTodaySchedules(Long userId);

    /**
     * 获取日程统计信息
     */
    List<Object[]> getScheduleStatistics(Long userId);

    /**
     * 标记日程为已完成
     */
    void markScheduleAsCompleted(Long id);

    /**
     * 标记日程为已取消
     */
    void markScheduleAsCancelled(Long id);

    /**
     * 批量创建日程
     */
    List<Schedule> batchCreateSchedules(List<Schedule> schedules);

    /**
     * 获取周日程
     */
    List<Schedule> getWeeklySchedule(Long userId, LocalDate weekStart);

    /**
     * 获取月日程
     */
    List<Schedule> getMonthlySchedule(Long userId, LocalDate monthStart);

    /**
     * 查找需要提醒的日程
     */
    List<Schedule> findSchedulesNeedingReminder();

    /**
     * 发送日程提醒
     */
    void sendScheduleReminder(Long scheduleId);

    /**
     * 清理过期日程
     */
    void cleanupExpiredSchedules(int daysToKeep);

    // 以下是原有的课表相关方法，保持兼容性

    /**
     * 获取所有课表
     */
    List<Schedule> findAll();

    /**
     * 根据ID查找课表
     */
    Optional<Schedule> findById(Long id);

    /**
     * 保存课表
     */
    Schedule save(Schedule schedule);

    /**
     * 删除课表
     */
    void deleteById(Long id);

    /**
     * 获取课表统计信息
     */
    Map<String, Object> getScheduleStats(String semester);

    /**
     * 根据学期获取课表
     */
    List<Schedule> findBySemester(String semester);

    /**
     * 根据教师ID获取课表
     */
    List<Schedule> findByTeacherId(Long teacherId, String semester);

    /**
     * 根据教室ID获取课表
     */
    List<Schedule> findByClassroomId(Long classroomId, String semester);

    /**
     * 获取当前学期
     */
    String getCurrentSemester();

    /**
     * 根据课程ID搜索课表
     */
    List<Schedule> findByCourseId(Long courseId);
}
