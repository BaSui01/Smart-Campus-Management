package com.campus.application.service.impl;

import com.campus.application.service.TimeSlotService;
import com.campus.domain.entity.TimeSlot;
import com.campus.domain.repository.TimeSlotRepository;
import com.campus.shared.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 时间段管理服务实现类
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Service
@Transactional
public class TimeSlotServiceImpl implements TimeSlotService {
    
    private static final Logger logger = LoggerFactory.getLogger(TimeSlotServiceImpl.class);
    
    @Autowired
    private TimeSlotRepository timeSlotRepository;
    
    @Override
    public TimeSlot createTimeSlot(TimeSlot timeSlot) {
        logger.info("创建时间段: {}", timeSlot.getSlotName());
        
        // 验证时间段名称唯一性
        if (existsTimeSlotByName(timeSlot.getSlotName())) {
            throw new BusinessException("时间段名称已存在");
        }
        
        // 验证时间范围
        if (timeSlot.getStartTime().isAfter(timeSlot.getEndTime())) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }
        
        // 检查时间冲突
        if (checkTimeSlotConflict(timeSlot.getStartTime(), timeSlot.getEndTime(), null)) {
            throw new BusinessException("时间段与现有时间段冲突");
        }
        
        timeSlot.setStatus(1);
        timeSlot.setDeleted(0);
        return timeSlotRepository.save(timeSlot);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<TimeSlot> findTimeSlotById(Long id) {
        return timeSlotRepository.findByIdAndDeleted(id, 0);
    }
    
    @Override
    public TimeSlot updateTimeSlot(TimeSlot timeSlot) {
        logger.info("更新时间段: {}", timeSlot.getId());
        
        Optional<TimeSlot> existingOpt = findTimeSlotById(timeSlot.getId());
        if (existingOpt.isEmpty()) {
            throw new BusinessException("时间段不存在");
        }
        
        TimeSlot existing = existingOpt.get();
        
        // 检查名称是否与其他时间段冲突
        if (!existing.getSlotName().equals(timeSlot.getSlotName()) && existsTimeSlotByName(timeSlot.getSlotName())) {
            throw new BusinessException("时间段名称已存在");
        }
        
        // 验证时间范围
        if (timeSlot.getStartTime().isAfter(timeSlot.getEndTime())) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }
        
        // 检查时间冲突（排除当前时间段）
        if (checkTimeSlotConflict(timeSlot.getStartTime(), timeSlot.getEndTime(), timeSlot.getId())) {
            throw new BusinessException("时间段与现有时间段冲突");
        }
        
        existing.setSlotName(timeSlot.getSlotName());
        existing.setStartTime(timeSlot.getStartTime());
        existing.setEndTime(timeSlot.getEndTime());
        existing.setSlotType(timeSlot.getSlotType());
        existing.setSemester(timeSlot.getSemester());
        existing.setDescription(timeSlot.getDescription());
        
        return timeSlotRepository.save(existing);
    }
    
    @Override
    public void deleteTimeSlot(Long id) {
        logger.info("删除时间段: {}", id);
        
        Optional<TimeSlot> timeSlotOpt = findTimeSlotById(id);
        if (timeSlotOpt.isEmpty()) {
            throw new BusinessException("时间段不存在");
        }
        
        TimeSlot timeSlot = timeSlotOpt.get();
        timeSlot.setDeleted(1);
        timeSlotRepository.save(timeSlot);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TimeSlot> findAllTimeSlots(Pageable pageable) {
        return timeSlotRepository.findByDeletedOrderByStartTimeAsc(0, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TimeSlot> findAvailableTimeSlots() {
        return timeSlotRepository.findByStatusAndDeletedOrderByStartTimeAsc(1, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<TimeSlot> findTimeSlotByName(String slotName) {
        return timeSlotRepository.findBySlotNameAndDeleted(slotName, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TimeSlot> findTimeSlotsByTimeRange(LocalTime startTime, LocalTime endTime) {
        return timeSlotRepository.findByStartTimeBetweenAndDeletedOrderByStartTimeAsc(startTime, endTime, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TimeSlot> findTimeSlotsByType(String slotType) {
        return timeSlotRepository.findBySlotTypeAndDeletedOrderByStartTimeAsc(slotType, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TimeSlot> findTimeSlotsBySemester(String semester) {
        return timeSlotRepository.findBySemesterAndDeletedOrderByStartTimeAsc(semester, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TimeSlot> findStandardTimeSlots() {
        return timeSlotRepository.findBySlotTypeAndDeletedOrderByStartTimeAsc("standard", 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean checkTimeSlotConflict(LocalTime startTime, LocalTime endTime, Long excludeId) {
        List<TimeSlot> conflictingSlots;
        
        if (excludeId != null) {
            conflictingSlots = timeSlotRepository.findConflictingTimeSlotsExcluding(startTime, endTime, excludeId, 0);
        } else {
            conflictingSlots = timeSlotRepository.findConflictingTimeSlots(startTime, endTime, 0);
        }
        
        return !conflictingSlots.isEmpty();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsTimeSlotByName(String slotName) {
        return timeSlotRepository.existsBySlotNameAndDeleted(slotName, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<TimeSlot> findTimeSlotByTime(LocalTime time) {
        return timeSlotRepository.findByTimeInRange(time, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<TimeSlot> getNextTimeSlot(Long currentTimeSlotId) {
        Optional<TimeSlot> currentOpt = findTimeSlotById(currentTimeSlotId);
        if (currentOpt.isEmpty()) {
            return Optional.empty();
        }
        
        TimeSlot current = currentOpt.get();
        return timeSlotRepository.findNextTimeSlot(current.getEndTime(), 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<TimeSlot> getPreviousTimeSlot(Long currentTimeSlotId) {
        Optional<TimeSlot> currentOpt = findTimeSlotById(currentTimeSlotId);
        if (currentOpt.isEmpty()) {
            return Optional.empty();
        }
        
        TimeSlot current = currentOpt.get();
        return timeSlotRepository.findPreviousTimeSlot(current.getStartTime(), 0);
    }
    
    @Override
    public List<TimeSlot> batchCreateTimeSlots(List<TimeSlot> timeSlots) {
        logger.info("批量创建时间段: {} 个", timeSlots.size());
        
        for (TimeSlot timeSlot : timeSlots) {
            // 验证每个时间段
            if (existsTimeSlotByName(timeSlot.getSlotName())) {
                throw new BusinessException("时间段名称已存在: " + timeSlot.getSlotName());
            }
            
            if (timeSlot.getStartTime().isAfter(timeSlot.getEndTime())) {
                throw new BusinessException("时间段 '" + timeSlot.getSlotName() + "' 的开始时间不能晚于结束时间");
            }
            
            if (checkTimeSlotConflict(timeSlot.getStartTime(), timeSlot.getEndTime(), null)) {
                throw new BusinessException("时间段 '" + timeSlot.getSlotName() + "' 与现有时间段冲突");
            }
            
            timeSlot.setStatus(1);
            timeSlot.setDeleted(0);
        }
        
        return timeSlotRepository.saveAll(timeSlots);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countTotalTimeSlots() {
        return timeSlotRepository.countByDeleted(0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countAvailableTimeSlots() {
        return timeSlotRepository.countByStatusAndDeleted(1, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> countTimeSlotsByType() {
        return timeSlotRepository.countBySlotType();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTimeSlotUsageStatistics() {
        return timeSlotRepository.getUsageStatistics();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<TimeSlot> searchTimeSlots(String keyword, Pageable pageable) {
        return timeSlotRepository.findBySlotNameContainingIgnoreCaseAndDeletedOrderByStartTimeAsc(keyword, 0, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TimeSlot> searchByTimeRange(String startTime, String endTime) {
        try {
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);
            return findTimeSlotsByTimeRange(start, end);
        } catch (Exception e) {
            logger.error("时间格式解析失败: startTime={}, endTime={}", startTime, endTime, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public void enableTimeSlot(Long id) {
        logger.info("启用时间段: {}", id);
        
        Optional<TimeSlot> timeSlotOpt = findTimeSlotById(id);
        if (timeSlotOpt.isEmpty()) {
            throw new BusinessException("时间段不存在");
        }
        
        TimeSlot timeSlot = timeSlotOpt.get();
        timeSlot.setStatus(1);
        timeSlotRepository.save(timeSlot);
    }
    
    @Override
    public void disableTimeSlot(Long id) {
        logger.info("禁用时间段: {}", id);
        
        Optional<TimeSlot> timeSlotOpt = findTimeSlotById(id);
        if (timeSlotOpt.isEmpty()) {
            throw new BusinessException("时间段不存在");
        }
        
        TimeSlot timeSlot = timeSlotOpt.get();
        timeSlot.setStatus(0);
        timeSlotRepository.save(timeSlot);
    }
    
    @Override
    public void batchDeleteTimeSlots(List<Long> timeSlotIds) {
        logger.info("批量删除时间段: {} 个", timeSlotIds.size());
        
        for (Long timeSlotId : timeSlotIds) {
            try {
                deleteTimeSlot(timeSlotId);
            } catch (Exception e) {
                logger.error("删除时间段失败: timeSlotId={}", timeSlotId, e);
            }
        }
    }
    
    @Override
    public void initializeStandardTimeSlots() {
        logger.info("初始化标准时间段");
        
        List<TimeSlot> standardSlots = createStandardTimeSlots();
        
        for (TimeSlot timeSlot : standardSlots) {
            if (!existsTimeSlotByName(timeSlot.getSlotName())) {
                createTimeSlot(timeSlot);
            }
        }
        
        logger.info("标准时间段初始化完成");
    }
    
    @Override
    public List<TimeSlot> copyTimeSlotsToSemester(String sourceSemester, String targetSemester) {
        logger.info("复制时间段配置: {} -> {}", sourceSemester, targetSemester);
        
        List<TimeSlot> sourceSlots = findTimeSlotsBySemester(sourceSemester);
        List<TimeSlot> targetSlots = new ArrayList<>();
        
        for (TimeSlot sourceSlot : sourceSlots) {
            TimeSlot targetSlot = new TimeSlot();
            targetSlot.setSlotName(sourceSlot.getSlotName());
            targetSlot.setStartTime(sourceSlot.getStartTime());
            targetSlot.setEndTime(sourceSlot.getEndTime());
            targetSlot.setSlotType(sourceSlot.getSlotType());
            targetSlot.setSemester(targetSemester);
            targetSlot.setDescription(sourceSlot.getDescription());
            targetSlot.setStatus(1);
            targetSlot.setDeleted(0);
            
            targetSlots.add(targetSlot);
        }
        
        return timeSlotRepository.saveAll(targetSlots);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validateTimeSlotConfiguration() {
        List<TimeSlot> allSlots = timeSlotRepository.findByDeletedOrderByStartTimeAsc(0);
        
        for (int i = 0; i < allSlots.size() - 1; i++) {
            TimeSlot current = allSlots.get(i);
            TimeSlot next = allSlots.get(i + 1);
            
            if (current.getEndTime().isAfter(next.getStartTime())) {
                logger.warn("时间段配置冲突: {} 与 {}", current.getSlotName(), next.getSlotName());
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TimeSlot> exportTimeSlots() {
        return timeSlotRepository.findByDeletedOrderByStartTimeAsc(0);
    }
    
    @Override
    public void importTimeSlots(List<TimeSlot> timeSlots) {
        logger.info("导入时间段配置: {} 个", timeSlots.size());
        
        for (TimeSlot timeSlot : timeSlots) {
            try {
                if (!existsTimeSlotByName(timeSlot.getSlotName())) {
                    timeSlot.setStatus(1);
                    timeSlot.setDeleted(0);
                    timeSlotRepository.save(timeSlot);
                }
            } catch (Exception e) {
                logger.error("导入时间段失败: {}", timeSlot.getSlotName(), e);
            }
        }
    }
    
    @Override
    public void cleanupInvalidTimeSlots() {
        logger.info("清理无效时间段");
        
        List<TimeSlot> invalidSlots = timeSlotRepository.findInvalidTimeSlots();
        
        for (TimeSlot slot : invalidSlots) {
            slot.setDeleted(1);
            timeSlotRepository.save(slot);
        }
        
        logger.info("清理无效时间段完成，共处理 {} 个", invalidSlots.size());
    }
    
    /**
     * 创建标准时间段
     */
    private List<TimeSlot> createStandardTimeSlots() {
        List<TimeSlot> slots = new ArrayList<>();
        
        slots.add(createTimeSlot("第1节", LocalTime.of(8, 0), LocalTime.of(8, 45), "standard"));
        slots.add(createTimeSlot("第2节", LocalTime.of(8, 55), LocalTime.of(9, 40), "standard"));
        slots.add(createTimeSlot("第3节", LocalTime.of(10, 0), LocalTime.of(10, 45), "standard"));
        slots.add(createTimeSlot("第4节", LocalTime.of(10, 55), LocalTime.of(11, 40), "standard"));
        slots.add(createTimeSlot("第5节", LocalTime.of(14, 0), LocalTime.of(14, 45), "standard"));
        slots.add(createTimeSlot("第6节", LocalTime.of(14, 55), LocalTime.of(15, 40), "standard"));
        slots.add(createTimeSlot("第7节", LocalTime.of(16, 0), LocalTime.of(16, 45), "standard"));
        slots.add(createTimeSlot("第8节", LocalTime.of(16, 55), LocalTime.of(17, 40), "standard"));
        
        return slots;
    }
    
    private TimeSlot createTimeSlot(String name, LocalTime startTime, LocalTime endTime, String type) {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setSlotName(name);
        timeSlot.setStartTime(startTime);
        timeSlot.setEndTime(endTime);
        timeSlot.setSlotType(type);
        timeSlot.setStatus(1);
        timeSlot.setDeleted(0);
        return timeSlot;
    }
}
