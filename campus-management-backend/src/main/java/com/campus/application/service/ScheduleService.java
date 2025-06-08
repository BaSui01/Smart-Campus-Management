package com.campus.application.service;

import com.campus.domain.entity.Schedule;
import com.campus.domain.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 课表服务类
 */
@Service
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    /**
     * 获取所有课表
     */
    @Transactional(readOnly = true)
    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    /**
     * 根据ID查找课表
     */
    @Transactional(readOnly = true)
    public Optional<Schedule> findById(Long id) {
        return scheduleRepository.findById(id);
    }

    /**
     * 保存课表
     */
    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    /**
     * 删除课表
     */
    public void deleteById(Long id) {
        scheduleRepository.deleteById(id);
    }

    /**
     * 获取课表统计信息
     */
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

    /**
     * 根据学期获取课表
     */
    @Transactional(readOnly = true)
    public List<Schedule> findBySemester(String semester) {
        return scheduleRepository.findBySemesterOrderByWeekdayAscStartTimeAsc(semester);
    }

    /**
     * 根据教师ID获取课表
     */
    @Transactional(readOnly = true)
    public List<Schedule> findByTeacherId(Long teacherId, String semester) {
        return scheduleRepository.findByTeacherIdAndSemesterOrderByWeekdayAscStartTimeAsc(teacherId, semester);
    }

    /**
     * 根据教室ID获取课表
     */
    @Transactional(readOnly = true)
    public List<Schedule> findByClassroomId(Long classroomId, String semester) {
        return scheduleRepository.findByClassroomIdAndSemesterOrderByWeekdayAscStartTimeAsc(classroomId, semester);
    }

    /**
     * 获取当前学期
     */
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

    /**
     * 根据课程ID搜索课表
     */
    @Transactional(readOnly = true)
    public List<Schedule> findByCourseId(Long courseId) {
        return scheduleRepository.findByCourseId(courseId);
    }
}
