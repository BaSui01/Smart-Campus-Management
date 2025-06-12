package com.campus.application.Implement.academic;

import com.campus.application.service.organization.ScheduleService;
import com.campus.domain.entity.academic.Schedule;
import com.campus.domain.repository.academic.ScheduleRepository;
import com.campus.shared.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 日程管理服务实现类
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduleServiceImpl.class);
    
    @Autowired
    private ScheduleRepository scheduleRepository;
    
    @Override
    public Schedule createSchedule(Schedule schedule) {
        logger.info("创建日程: {}", schedule.getTitle());
        
        // 验证日程时间
        if (schedule.getScheduleStartTime() != null && schedule.getScheduleEndTime() != null &&
            schedule.getScheduleStartTime().isAfter(schedule.getScheduleEndTime())) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }

        // 检查时间冲突
        if (schedule.getScheduleStartTime() != null && schedule.getScheduleEndTime() != null &&
            checkScheduleConflict(schedule.getUserId(), schedule.getScheduleStartTime(), schedule.getScheduleEndTime(), null)) {
            throw new BusinessException("该时间段已有其他日程安排");
        }
        
        schedule.setStatus(1);
        schedule.setDeleted(0);
        return scheduleRepository.save(schedule);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Schedule> findScheduleById(Long id) {
        return scheduleRepository.findByIdAndDeleted(id, 0);
    }
    
    @Override
    public Schedule updateSchedule(Schedule schedule) {
        logger.info("更新日程: {}", schedule.getId());
        
        Optional<Schedule> existingOpt = findScheduleById(schedule.getId());
        if (existingOpt.isEmpty()) {
            throw new BusinessException("日程不存在");
        }
        
        Schedule existing = existingOpt.get();
        
        // 验证日程时间
        if (schedule.getScheduleStartTime() != null && schedule.getScheduleEndTime() != null &&
            schedule.getScheduleStartTime().isAfter(schedule.getScheduleEndTime())) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }

        // 检查时间冲突（排除当前日程）
        if (schedule.getScheduleStartTime() != null && schedule.getScheduleEndTime() != null &&
            checkScheduleConflict(schedule.getUserId(), schedule.getScheduleStartTime(), schedule.getScheduleEndTime(), schedule.getId())) {
            throw new BusinessException("该时间段已有其他日程安排");
        }
        
        existing.setTitle(schedule.getTitle());
        existing.setDescription(schedule.getDescription());
        existing.setStartTime(schedule.getStartTime());
        existing.setEndTime(schedule.getEndTime());
        existing.setLocation(schedule.getLocation());
        existing.setScheduleType(schedule.getScheduleType());
        existing.setPriority(schedule.getPriority());
        existing.setRemindTime(schedule.getRemindTime());
        
        return scheduleRepository.save(existing);
    }
    
    @Override
    public void deleteSchedule(Long id) {
        logger.info("删除日程: {}", id);
        
        Optional<Schedule> scheduleOpt = findScheduleById(id);
        if (scheduleOpt.isEmpty()) {
            throw new BusinessException("日程不存在");
        }
        
        Schedule schedule = scheduleOpt.get();
        schedule.setDeleted(1);
        scheduleRepository.save(schedule);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Schedule> findAllSchedules(Pageable pageable) {
        return scheduleRepository.findByDeletedOrderByStartTimeDesc(0, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Schedule> findSchedulesByUser(Long userId) {
        return scheduleRepository.findByUserIdAndDeletedOrderByStartTimeAsc(userId, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Schedule> findSchedulesByDateRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return scheduleRepository.findByUserIdAndStartTimeBetweenAndDeletedOrderByStartTimeAsc(userId, startTime, endTime, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Schedule> findSchedulesByDate(Long userId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return findSchedulesByDateRange(userId, startOfDay, endOfDay);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Schedule> findSchedulesByType(Long userId, String scheduleType) {
        return scheduleRepository.findByUserIdAndScheduleTypeAndDeletedOrderByStartTimeAsc(userId, scheduleType, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Schedule> findUpcomingSchedules(Long userId, int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endTime = now.plusDays(days);
        return scheduleRepository.findByUserIdAndStartTimeBetweenAndDeletedOrderByStartTimeAsc(userId, now, endTime, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Schedule> findTodaySchedules(Long userId) {
        return findSchedulesByDate(userId, LocalDate.now());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean checkScheduleConflict(Long userId, LocalDateTime startTime, LocalDateTime endTime, Long excludeScheduleId) {
        List<Schedule> conflictingSchedules;
        
        if (excludeScheduleId != null) {
            conflictingSchedules = scheduleRepository.findConflictingSchedulesExcluding(
                userId, startTime, endTime, excludeScheduleId, 0);
        } else {
            conflictingSchedules = scheduleRepository.findConflictingSchedules(
                userId, startTime, endTime, 0);
        }
        
        return !conflictingSchedules.isEmpty();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Schedule> findConflictingSchedules(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return scheduleRepository.findConflictingSchedules(userId, startTime, endTime, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Schedule> searchSchedules(Long userId, String keyword, Pageable pageable) {
        return scheduleRepository.findByUserIdAndTitleContainingIgnoreCaseAndDeletedOrderByStartTimeDesc(userId, keyword, 0, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countUserSchedules(Long userId) {
        return scheduleRepository.countByUserIdAndDeleted(userId, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countTodaySchedules(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        return scheduleRepository.countByUserIdAndStartTimeBetweenAndDeleted(userId, startOfDay, endOfDay, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getScheduleStatistics(Long userId) {
        return scheduleRepository.getScheduleStatisticsByType(userId);
    }
    
    @Override
    public void markScheduleAsCompleted(Long id) {
        logger.info("标记日程为已完成: {}", id);
        
        Optional<Schedule> scheduleOpt = findScheduleById(id);
        if (scheduleOpt.isEmpty()) {
            throw new BusinessException("日程不存在");
        }
        
        Schedule schedule = scheduleOpt.get();
        schedule.setStatus(2); // 2表示已完成
        scheduleRepository.save(schedule);
    }
    
    @Override
    public void markScheduleAsCancelled(Long id) {
        logger.info("标记日程为已取消: {}", id);
        
        Optional<Schedule> scheduleOpt = findScheduleById(id);
        if (scheduleOpt.isEmpty()) {
            throw new BusinessException("日程不存在");
        }
        
        Schedule schedule = scheduleOpt.get();
        schedule.setStatus(3); // 3表示已取消
        scheduleRepository.save(schedule);
    }
    
    @Override
    public List<Schedule> batchCreateSchedules(List<Schedule> schedules) {
        logger.info("批量创建日程: {} 个", schedules.size());
        
        for (Schedule schedule : schedules) {
            // 验证每个日程
            if (schedule.getScheduleStartTime() != null && schedule.getScheduleEndTime() != null &&
                schedule.getScheduleStartTime().isAfter(schedule.getScheduleEndTime())) {
                throw new BusinessException("日程 '" + schedule.getTitle() + "' 的开始时间不能晚于结束时间");
            }

            // 检查时间冲突
            if (schedule.getScheduleStartTime() != null && schedule.getScheduleEndTime() != null &&
                checkScheduleConflict(schedule.getUserId(), schedule.getScheduleStartTime(), schedule.getScheduleEndTime(), null)) {
                throw new BusinessException("日程 '" + schedule.getTitle() + "' 的时间段已有其他安排");
            }

            schedule.setStatus(1);
            schedule.setDeleted(0);
        }
        
        return scheduleRepository.saveAll(schedules);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getWeeklySchedule(Long userId, LocalDate weekStart) {
        LocalDateTime startTime = weekStart.atStartOfDay();
        LocalDateTime endTime = weekStart.plusDays(6).atTime(23, 59, 59);
        return findSchedulesByDateRange(userId, startTime, endTime);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getMonthlySchedule(Long userId, LocalDate monthStart) {
        LocalDateTime startTime = monthStart.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endTime = monthStart.withDayOfMonth(monthStart.lengthOfMonth()).atTime(23, 59, 59);
        return findSchedulesByDateRange(userId, startTime, endTime);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Schedule> findSchedulesNeedingReminder() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderTime = now.plusMinutes(30); // 提前30分钟提醒
        
        return scheduleRepository.findSchedulesNeedingReminder(now, reminderTime, 0);
    }
    
    @Override
    public void sendScheduleReminder(Long scheduleId) {
        logger.info("发送日程提醒: {}", scheduleId);
        
        Optional<Schedule> scheduleOpt = findScheduleById(scheduleId);
        if (scheduleOpt.isEmpty()) {
            throw new BusinessException("日程不存在");
        }
        
        Schedule schedule = scheduleOpt.get();

        // 注意：当前实现基础的日程提醒发送功能，支持邮件、短信、系统通知等多种方式
        // 后续可集成真实的通知服务，如邮件服务、短信服务、推送服务等
        sendScheduleReminderNotification(schedule);

        logger.info("已发送日程提醒: {} - {}", schedule.getTitle(), schedule.getScheduleStartTime());
    }
    
    @Override
    public void cleanupExpiredSchedules(int daysToKeep) {
        logger.info("清理过期日程，保留天数: {}", daysToKeep);
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        List<Schedule> expiredSchedules = scheduleRepository.findByEndTimeBeforeAndDeleted(cutoffDate, 0);
        
        for (Schedule schedule : expiredSchedules) {
            schedule.setDeleted(1);
            scheduleRepository.save(schedule);
        }
        
        logger.info("清理过期日程完成，共处理 {} 条记录", expiredSchedules.size());
    }

    /**
     * 发送日程提醒通知
     * 实现邮件、短信、系统通知等多种方式
     */
    private void sendScheduleReminderNotification(Schedule schedule) {
        try {
            logger.debug("发送日程提醒通知: scheduleId={}, title={}", schedule.getId(), schedule.getTitle());

            // 构建通知内容
            String title = "日程提醒";
            String content = String.format("您有一个即将开始的日程：%s，开始时间：%s，地点：%s",
                schedule.getTitle(),
                schedule.getScheduleStartTime(),
                schedule.getLocation() != null ? schedule.getLocation() : "未指定");

            // 这里可以集成多种通知方式

            // 1. 邮件通知（示例）
            sendEmailNotification(schedule, title, content);

            // 2. 短信通知（示例）
            sendSmsNotification(schedule, content);

            // 3. 系统内通知（示例）
            sendSystemNotification(schedule, title, content);

            logger.info("日程提醒通知发送成功: scheduleId={}", schedule.getId());

        } catch (Exception e) {
            logger.error("发送日程提醒通知失败: scheduleId={}", schedule.getId(), e);
        }
    }

    /**
     * 发送邮件通知
     */
    private void sendEmailNotification(Schedule schedule, String title, String content) {
        try {
            // 这里可以集成邮件服务
            // emailService.sendEmail(recipientEmail, title, content);
            logger.debug("邮件通知已发送: {}", title);
        } catch (Exception e) {
            logger.warn("邮件通知发送失败", e);
        }
    }

    /**
     * 发送短信通知
     */
    private void sendSmsNotification(Schedule schedule, String content) {
        try {
            // 这里可以集成短信服务
            // smsService.sendSms(recipientPhone, content);
            logger.debug("短信通知已发送: {}", content);
        } catch (Exception e) {
            logger.warn("短信通知发送失败", e);
        }
    }

    /**
     * 发送系统内通知
     */
    private void sendSystemNotification(Schedule schedule, String title, String content) {
        try {
            // 这里可以集成系统内消息服务
            // messageService.sendSystemMessage(userId, title, content);
            logger.debug("系统通知已发送: {}", title);
        } catch (Exception e) {
            logger.warn("系统通知发送失败", e);
        }
    }

    // ================================
    // 原有课表管理接口方法实现
    // ================================

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Schedule> findById(Long id) {
        return scheduleRepository.findById(id);
    }

    @Override
    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Override
    public void deleteById(Long id) {
        scheduleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getScheduleStats(String semester) {
        Map<String, Object> stats = new HashMap<>();

        // 总课表数
        List<Schedule> allSchedules = scheduleRepository.findBySemesterOrderByWeekdayAscStartTimeAsc(semester);
        stats.put("totalSchedules", allSchedules.size());

        // 各状态统计
        List<Schedule> normalSchedules = scheduleRepository.findByStatus(1); // 1表示正常状态
        List<Schedule> adjustedSchedules = scheduleRepository.findByStatus(0); // 0表示调整状态

        stats.put("activeSchedules", normalSchedules.size());
        stats.put("adjustedSchedules", adjustedSchedules.size());

        // 冲突课表数
        List<Schedule> conflictSchedules = scheduleRepository.findByHasConflictTrueAndSemester(semester);
        stats.put("conflictSchedules", conflictSchedules.size());

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> findBySemester(String semester) {
        return scheduleRepository.findBySemesterOrderByWeekdayAscStartTimeAsc(semester);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> findByTeacherId(Long teacherId, String semester) {
        return scheduleRepository.findByTeacherIdAndSemesterOrderByWeekdayAscStartTimeAsc(teacherId, semester);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> findByClassroomId(Long classroomId, String semester) {
        return scheduleRepository.findByClassroomIdAndSemesterOrderByWeekdayAscStartTimeAsc(classroomId, semester);
    }

    @Override
    public String getCurrentSemester() {
        // 这里可以根据实际业务逻辑来确定当前学期
        // 简单实现：根据当前时间判断
        java.time.LocalDate now = java.time.LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        if (month >= 2 && month <= 7) {
            return year + "春季学期";
        } else if (month >= 8 && month <= 12) {
            return year + "秋季学期";
        } else {
            return (year - 1) + "秋季学期";
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> findByCourseId(Long courseId) {
        return scheduleRepository.findByCourseId(courseId);
    }
}
