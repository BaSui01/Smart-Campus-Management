package com.campus.application.service.impl;

import com.campus.application.service.ClassroomService;
import com.campus.domain.entity.Classroom;
import com.campus.domain.repository.ClassroomRepository;
import com.campus.shared.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 教室管理服务实现类
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Service
@Transactional
public class ClassroomServiceImpl implements ClassroomService {
    
    private static final Logger logger = LoggerFactory.getLogger(ClassroomServiceImpl.class);
    
    @Autowired
    private ClassroomRepository classroomRepository;
    
    @Override
    public Classroom createClassroom(Classroom classroom) {
        logger.info("创建教室: {}", classroom.getClassroomName());
        
        // 验证教室名称唯一性
        if (classroomRepository.existsByClassroomNameAndDeleted(classroom.getClassroomName(), 0)) {
            throw new BusinessException("教室名称已存在");
        }
        
        classroom.setStatus(1);
        classroom.setDeleted(0);
        return classroomRepository.save(classroom);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Classroom> findClassroomById(Long id) {
        return classroomRepository.findByIdAndDeleted(id, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public Classroom getClassroomById(Long id) {
        Optional<Classroom> classroom = classroomRepository.findByIdAndDeleted(id, 0);
        return classroom.orElse(null);
    }
    
    @Override
    public Classroom updateClassroom(Classroom classroom) {
        logger.info("更新教室信息: {}", classroom.getId());
        
        Optional<Classroom> existingOpt = findClassroomById(classroom.getId());
        if (existingOpt.isEmpty()) {
            throw new BusinessException("教室不存在");
        }
        
        Classroom existing = existingOpt.get();
        
        // 检查名称是否与其他教室冲突
        if (!existing.getClassroomName().equals(classroom.getClassroomName()) &&
            classroomRepository.existsByClassroomNameAndDeleted(classroom.getClassroomName(), 0)) {
            throw new BusinessException("教室名称已存在");
        }
        
        existing.setClassroomName(classroom.getClassroomName());
        existing.setBuilding(classroom.getBuilding());
        existing.setFloor(classroom.getFloor());
        existing.setCapacity(classroom.getCapacity());
        existing.setEquipment(classroom.getEquipment());
        existing.setDescription(classroom.getDescription());
        
        return classroomRepository.save(existing);
    }
    
    @Override
    public void deleteClassroom(Long id) {
        logger.info("删除教室: {}", id);
        
        Optional<Classroom> classroomOpt = findClassroomById(id);
        if (classroomOpt.isEmpty()) {
            throw new BusinessException("教室不存在");
        }
        
        Classroom classroom = classroomOpt.get();
        classroom.setDeleted(1);
        classroomRepository.save(classroom);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Classroom> findAllClassrooms(Pageable pageable) {
        return classroomRepository.findByDeletedOrderByCreatedAtDesc(0, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Classroom> findAvailableClassrooms() {
        return classroomRepository.findByStatusAndDeletedOrderByClassroomName(1, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Classroom> findClassroomsByBuilding(String building) {
        return classroomRepository.findByBuildingAndDeletedOrderByFloorAscClassroomNameAsc(building, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Classroom> findClassroomsByCapacityRange(Integer minCapacity, Integer maxCapacity) {
        return classroomRepository.findByCapacityBetweenAndDeletedOrderByCapacityAsc(minCapacity, maxCapacity, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Classroom> findClassroomsByFloor(String building, Integer floor) {
        return classroomRepository.findByBuildingAndFloorAndDeletedOrderByClassroomName(building, floor, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isClassroomAvailable(Long classroomId, String timeSlot, String dayOfWeek) {
        // TODO: 实现教室时间段可用性检查
        // 需要查询课程安排表，检查指定时间段是否有课程安排
        logger.debug("检查教室可用性: classroomId={}, timeSlot={}, dayOfWeek={}", classroomId, timeSlot, dayOfWeek);
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Classroom> findAvailableClassroomsForTimeSlot(String timeSlot, String dayOfWeek) {
        // TODO: 实现指定时间段的可用教室查询
        logger.debug("查找时间段可用教室: timeSlot={}, dayOfWeek={}", timeSlot, dayOfWeek);
        return findAvailableClassrooms();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean checkClassroomConflict(Long classroomId, String timeSlot, String dayOfWeek) {
        return !isClassroomAvailable(classroomId, timeSlot, dayOfWeek);
    }
    
    @Override
    public boolean reserveClassroom(Long classroomId, String timeSlot, String dayOfWeek, Long userId) {
        logger.info("预订教室: classroomId={}, timeSlot={}, dayOfWeek={}, userId={}", 
                   classroomId, timeSlot, dayOfWeek, userId);
        
        if (!isClassroomAvailable(classroomId, timeSlot, dayOfWeek)) {
            throw new BusinessException("教室在该时间段不可用");
        }
        
        // TODO: 实现教室预订逻辑
        return true;
    }
    
    @Override
    public boolean cancelClassroomReservation(Long classroomId, String timeSlot, String dayOfWeek) {
        logger.info("取消教室预订: classroomId={}, timeSlot={}, dayOfWeek={}", 
                   classroomId, timeSlot, dayOfWeek);
        
        // TODO: 实现取消预订逻辑
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countTotalClassrooms() {
        return classroomRepository.countByDeleted(0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countAvailableClassrooms() {
        return classroomRepository.countByStatusAndDeleted(1, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> countClassroomsByBuilding() {
        return classroomRepository.countByBuilding();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double calculateClassroomUtilizationRate(Long classroomId) {
        // TODO: 实现教室使用率计算
        logger.debug("计算教室使用率: classroomId={}", classroomId);
        return 0.0;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Classroom> searchClassrooms(String keyword, Pageable pageable) {
        return classroomRepository.findByClassroomNameContainingIgnoreCaseAndDeletedOrderByClassroomName(keyword, 0, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Classroom> findClassroomsByEquipment(String equipment) {
        return classroomRepository.findByEquipmentContainingIgnoreCaseAndDeleted(equipment, 0);
    }
    
    @Override
    public void enableClassroom(Long id) {
        logger.info("启用教室: {}", id);
        
        Optional<Classroom> classroomOpt = findClassroomById(id);
        if (classroomOpt.isEmpty()) {
            throw new BusinessException("教室不存在");
        }
        
        Classroom classroom = classroomOpt.get();
        classroom.setStatus(1);
        classroomRepository.save(classroom);
    }
    
    @Override
    public void disableClassroom(Long id) {
        logger.info("禁用教室: {}", id);
        
        Optional<Classroom> classroomOpt = findClassroomById(id);
        if (classroomOpt.isEmpty()) {
            throw new BusinessException("教室不存在");
        }
        
        Classroom classroom = classroomOpt.get();
        classroom.setStatus(0);
        classroomRepository.save(classroom);
    }
    
    @Override
    public void importClassrooms(List<Classroom> classrooms) {
        logger.info("批量导入教室: {} 个", classrooms.size());
        
        for (Classroom classroom : classrooms) {
            try {
                if (!classroomRepository.existsByClassroomNameAndDeleted(classroom.getClassroomName(), 0)) {
                    classroom.setStatus(1);
                    classroom.setDeleted(0);
                    classroomRepository.save(classroom);
                }
            } catch (Exception e) {
                logger.error("导入教室失败: {}", classroom.getClassroomName(), e);
            }
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Classroom> exportClassrooms() {
        return classroomRepository.findByDeletedOrderByBuildingAscFloorAscClassroomNameAsc(0);
    }
}
