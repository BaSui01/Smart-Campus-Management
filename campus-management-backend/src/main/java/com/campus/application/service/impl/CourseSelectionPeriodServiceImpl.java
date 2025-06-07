package com.campus.application.service.impl;

import com.campus.application.service.CourseSelectionPeriodService;
import com.campus.domain.entity.CourseSelectionPeriod;
import com.campus.domain.repository.CourseSelectionPeriodRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 选课时间段服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Service
@Transactional
public class CourseSelectionPeriodServiceImpl implements CourseSelectionPeriodService {

    private static final Logger log = LoggerFactory.getLogger(CourseSelectionPeriodServiceImpl.class);

    @Autowired
    private CourseSelectionPeriodRepository periodRepository;

    @Override
    public CourseSelectionPeriod createPeriod(CourseSelectionPeriod period) {
        log.info("创建选课时间段: {}", period.getPeriodName());
        
        // 检查时间冲突
        if (hasTimeConflict(null, period.getSelectionType(), period.getSemester(), 
                           period.getAcademicYear(), period.getStartTime(), period.getEndTime())) {
            throw new IllegalArgumentException("选课时间段存在冲突");
        }
        
        return periodRepository.save(period);
    }

    @Override
    public CourseSelectionPeriod updatePeriod(CourseSelectionPeriod period) {
        log.info("更新选课时间段: {}", period.getId());
        
        // 检查时间冲突（排除自身）
        if (hasTimeConflict(period.getId(), period.getSelectionType(), period.getSemester(), 
                           period.getAcademicYear(), period.getStartTime(), period.getEndTime())) {
            throw new IllegalArgumentException("选课时间段存在冲突");
        }
        
        return periodRepository.save(period);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseSelectionPeriod> findById(Long id) {
        return periodRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        log.info("删除选课时间段: {}", id);
        periodRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseSelectionPeriod> findWithFilters(String semester, Integer academicYear, 
                                                       String selectionType, Pageable pageable) {
        return periodRepository.findWithFilters(semester, academicYear, selectionType, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSelectionPeriod> getCurrentOpenPeriods() {
        LocalDateTime now = LocalDateTime.now();
        return periodRepository.findCurrentOpenPeriods(now);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSelectionPeriod> getAvailablePeriodsForStudent(String grade, String major) {
        return periodRepository.findAvailablePeriodsForStudent(grade, major);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSelectionPeriod> getCurrentOpenPeriodsForStudent(String grade, String major) {
        LocalDateTime now = LocalDateTime.now();
        return periodRepository.findCurrentOpenPeriodsForStudent(now, grade, major);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSelectionPeriod> findBySemesterAndAcademicYear(String semester, Integer academicYear) {
        return periodRepository.findBySemesterAndAcademicYearOrderByPriorityAsc(semester, academicYear);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSelectionPeriod> getUpcomingPeriods(int hours) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureTime = now.plusHours(hours);
        return periodRepository.findUpcomingPeriods(now, futureTime);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasTimeConflict(Long excludeId, String selectionType, String semester, 
                                  Integer academicYear, LocalDateTime startTime, LocalDateTime endTime) {
        Long excludeIdValue = excludeId != null ? excludeId : -1L;
        long conflictCount = periodRepository.countConflictingPeriods(
            excludeIdValue, selectionType, semester, academicYear, startTime, endTime);
        return conflictCount > 0;
    }

    @Override
    public void enablePeriod(Long id) {
        log.info("启用选课时间段: {}", id);
        Optional<CourseSelectionPeriod> periodOpt = periodRepository.findById(id);
        if (periodOpt.isPresent()) {
            CourseSelectionPeriod period = periodOpt.get();
            period.enable();
            periodRepository.save(period);
        }
    }

    @Override
    public void disablePeriod(Long id) {
        log.info("禁用选课时间段: {}", id);
        Optional<CourseSelectionPeriod> periodOpt = periodRepository.findById(id);
        if (periodOpt.isPresent()) {
            CourseSelectionPeriod period = periodOpt.get();
            period.disable();
            periodRepository.save(period);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canStudentSelectCourse(Long periodId, String grade, String major) {
        Optional<CourseSelectionPeriod> periodOpt = periodRepository.findById(periodId);
        if (periodOpt.isEmpty()) {
            return false;
        }
        
        CourseSelectionPeriod period = periodOpt.get();
        return period.isSelectionOpen() && 
               period.isGradeApplicable(grade) && 
               period.isMajorApplicable(major);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canStudentDropCourse(Long periodId) {
        Optional<CourseSelectionPeriod> periodOpt = periodRepository.findById(periodId);
        if (periodOpt.isEmpty()) {
            return false;
        }
        
        return periodOpt.get().canDropCourse();
    }

    @Override
    @Transactional(readOnly = true)
    public PeriodStatistics getStatistics() {
        LocalDateTime now = LocalDateTime.now();
        
        long totalPeriods = periodRepository.count();
        long activePeriods = periodRepository.findCurrentOpenPeriods(now).size();
        long expiredPeriods = periodRepository.findExpiredPeriods(now).size();
        long upcomingPeriods = periodRepository.findUpcomingPeriods(now, now.plusDays(7)).size();
        
        PeriodStatistics statistics = new PeriodStatistics(totalPeriods, activePeriods, expiredPeriods, upcomingPeriods);
        
        // 获取类型统计
        List<Object[]> typeStats = periodRepository.countBySelectionType();
        List<TypeCount> typeCounts = typeStats.stream()
            .map(stat -> new TypeCount((String) stat[0], (Long) stat[1]))
            .collect(Collectors.toList());
        statistics.setTypeStatistics(typeCounts);
        
        return statistics;
    }

    @Override
    public List<CourseSelectionPeriod> batchCreatePeriods(List<CourseSelectionPeriod> periods) {
        log.info("批量创建选课时间段: {} 个", periods.size());
        
        // 验证每个时间段
        for (CourseSelectionPeriod period : periods) {
            if (hasTimeConflict(null, period.getSelectionType(), period.getSemester(), 
                               period.getAcademicYear(), period.getStartTime(), period.getEndTime())) {
                throw new IllegalArgumentException("选课时间段 " + period.getPeriodName() + " 存在冲突");
            }
        }
        
        return periodRepository.saveAll(periods);
    }

    @Override
    public List<CourseSelectionPeriod> copyPeriodsToNewSemester(String fromSemester, Integer fromYear, 
                                                               String toSemester, Integer toYear) {
        log.info("复制选课时间段从 {}-{} 到 {}-{}", fromYear, fromSemester, toYear, toSemester);
        
        List<CourseSelectionPeriod> sourcePeriods = periodRepository.findBySemesterAndAcademicYearOrderByPriorityAsc(fromSemester, fromYear);
        List<CourseSelectionPeriod> newPeriods = new ArrayList<>();
        
        for (CourseSelectionPeriod source : sourcePeriods) {
            CourseSelectionPeriod newPeriod = new CourseSelectionPeriod();
            newPeriod.setPeriodName(source.getPeriodName());
            newPeriod.setSemester(toSemester);
            newPeriod.setAcademicYear(toYear);
            newPeriod.setSelectionType(source.getSelectionType());
            newPeriod.setPriority(source.getPriority());
            newPeriod.setApplicableGrades(source.getApplicableGrades());
            newPeriod.setApplicableMajors(source.getApplicableMajors());
            newPeriod.setMaxCredits(source.getMaxCredits());
            newPeriod.setMinCredits(source.getMinCredits());
            newPeriod.setAllowDrop(source.getAllowDrop());
            newPeriod.setDescription(source.getDescription());
            
            // 时间需要手动设置，不能直接复制
            newPeriod.setStatus(0); // 默认禁用，需要手动设置时间后启用
            
            newPeriods.add(newPeriod);
        }
        
        return periodRepository.saveAll(newPeriods);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSelectionPeriod> getExpiredPeriods() {
        LocalDateTime now = LocalDateTime.now();
        return periodRepository.findExpiredPeriods(now);
    }

    @Override
    public int autoCloseExpiredPeriods() {
        log.info("自动关闭过期的选课时间段");
        
        List<CourseSelectionPeriod> expiredPeriods = getExpiredPeriods();
        int closedCount = 0;
        
        for (CourseSelectionPeriod period : expiredPeriods) {
            period.disable();
            periodRepository.save(period);
            closedCount++;
        }
        
        log.info("已关闭 {} 个过期的选课时间段", closedCount);
        return closedCount;
    }

    @Override
    public void sendSelectionReminders() {
        log.info("发送选课提醒通知");
        
        // 查找即将开始的选课时间段（24小时内）
        List<CourseSelectionPeriod> upcomingPeriods = getUpcomingPeriods(24);
        
        // 查找即将结束的选课时间段（24小时内）
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusHours(24);
        
        List<CourseSelectionPeriod> endingSoonPeriods = periodRepository.findCurrentOpenPeriods(now)
            .stream()
            .filter(period -> period.getEndTime().isBefore(tomorrow))
            .collect(Collectors.toList());
        
        // TODO: 实现具体的通知发送逻辑
        // 这里可以集成邮件服务、短信服务等
        
        log.info("即将开始的选课时间段: {} 个", upcomingPeriods.size());
        log.info("即将结束的选课时间段: {} 个", endingSoonPeriods.size());
    }
}
