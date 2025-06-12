package com.campus.application.service.academic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.campus.domain.entity.academic.TimeSlot;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * 时间段管理服务接口
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
public interface TimeSlotService {
    
    // ================================
    // 基础CRUD操作
    // ================================
    
    /**
     * 创建时间段
     */
    TimeSlot createTimeSlot(TimeSlot timeSlot);
    
    /**
     * 根据ID查找时间段
     */
    Optional<TimeSlot> findTimeSlotById(Long id);
    
    /**
     * 更新时间段信息
     */
    TimeSlot updateTimeSlot(TimeSlot timeSlot);
    
    /**
     * 删除时间段（软删除）
     */
    void deleteTimeSlot(Long id);
    
    // ================================
    // 查询操作
    // ================================
    
    /**
     * 分页获取所有时间段
     */
    Page<TimeSlot> findAllTimeSlots(Pageable pageable);
    
    /**
     * 获取所有可用时间段
     */
    List<TimeSlot> findAvailableTimeSlots();
    
    /**
     * 根据时间段名称查找
     */
    Optional<TimeSlot> findTimeSlotByName(String slotName);
    
    /**
     * 根据时间范围查找时间段
     */
    List<TimeSlot> findTimeSlotsByTimeRange(LocalTime startTime, LocalTime endTime);
    
    /**
     * 根据时间段类型查找
     */
    List<TimeSlot> findTimeSlotsByType(String slotType);
    
    /**
     * 根据学期查找时间段
     */
    List<TimeSlot> findTimeSlotsBySemester(String semester);
    
    /**
     * 获取标准时间段（如第1节课、第2节课等）
     */
    List<TimeSlot> findStandardTimeSlots();
    
    // ================================
    // 业务操作
    // ================================
    
    /**
     * 检查时间段是否冲突
     */
    boolean checkTimeSlotConflict(LocalTime startTime, LocalTime endTime, Long excludeId);
    
    /**
     * 检查时间段名称是否已存在
     */
    boolean existsTimeSlotByName(String slotName);
    
    /**
     * 获取指定时间点所属的时间段
     */
    Optional<TimeSlot> findTimeSlotByTime(LocalTime time);
    
    /**
     * 获取下一个时间段
     */
    Optional<TimeSlot> getNextTimeSlot(Long currentTimeSlotId);
    
    /**
     * 获取上一个时间段
     */
    Optional<TimeSlot> getPreviousTimeSlot(Long currentTimeSlotId);
    
    /**
     * 批量创建时间段
     */
    List<TimeSlot> batchCreateTimeSlots(List<TimeSlot> timeSlots);
    
    // ================================
    // 统计操作
    // ================================
    
    /**
     * 统计时间段总数
     */
    long countTotalTimeSlots();
    
    /**
     * 统计可用时间段数量
     */
    long countAvailableTimeSlots();
    
    /**
     * 按类型统计时间段数量
     */
    List<Object[]> countTimeSlotsByType();
    
    /**
     * 获取时间段使用统计
     */
    List<Object[]> getTimeSlotUsageStatistics();
    
    // ================================
    // 搜索操作
    // ================================
    
    /**
     * 搜索时间段
     */
    Page<TimeSlot> searchTimeSlots(String keyword, Pageable pageable);
    
    /**
     * 根据时间范围搜索
     */
    List<TimeSlot> searchByTimeRange(String startTime, String endTime);
    
    // ================================
    // 管理操作
    // ================================
    
    /**
     * 启用时间段
     */
    void enableTimeSlot(Long id);
    
    /**
     * 禁用时间段
     */
    void disableTimeSlot(Long id);
    
    /**
     * 批量删除时间段
     */
    void batchDeleteTimeSlots(List<Long> timeSlotIds);
    
    /**
     * 初始化标准时间段
     */
    void initializeStandardTimeSlots();
    
    /**
     * 复制时间段配置到新学期
     */
    List<TimeSlot> copyTimeSlotsToSemester(String sourceSemester, String targetSemester);
    
    /**
     * 验证时间段配置
     */
    boolean validateTimeSlotConfiguration();
    
    /**
     * 导出时间段配置
     */
    List<TimeSlot> exportTimeSlots();
    
    /**
     * 导入时间段配置
     */
    void importTimeSlots(List<TimeSlot> timeSlots);
    
    /**
     * 清理无效时间段
     */
    void cleanupInvalidTimeSlots();
}
