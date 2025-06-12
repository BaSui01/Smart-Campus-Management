package com.campus.application.service.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.campus.domain.entity.infrastructure.Classroom;

import java.util.List;
import java.util.Optional;

/**
 * 教室管理服务接口
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
public interface ClassroomService {
    
    // ================================
    // 基础CRUD操作
    // ================================
    
    /**
     * 创建教室
     */
    Classroom createClassroom(Classroom classroom);
    
    /**
     * 根据ID查找教室
     */
    Optional<Classroom> findClassroomById(Long id);
    
    /**
     * 根据ID获取教室（兼容性方法）
     */
    Classroom getClassroomById(Long id);
    
    /**
     * 更新教室信息
     */
    Classroom updateClassroom(Classroom classroom);
    
    /**
     * 删除教室（软删除）
     */
    void deleteClassroom(Long id);
    
    // ================================
    // 查询操作
    // ================================
    
    /**
     * 分页获取所有教室
     */
    Page<Classroom> findAllClassrooms(Pageable pageable);
    
    /**
     * 获取所有可用教室
     */
    List<Classroom> findAvailableClassrooms();
    
    /**
     * 根据建筑物查找教室
     */
    List<Classroom> findClassroomsByBuilding(String building);
    
    /**
     * 根据容量范围查找教室
     */
    List<Classroom> findClassroomsByCapacityRange(Integer minCapacity, Integer maxCapacity);
    
    /**
     * 根据楼层查找教室
     */
    List<Classroom> findClassroomsByFloor(String building, Integer floor);
    
    // ================================
    // 业务操作
    // ================================
    
    /**
     * 检查教室在指定时间段是否可用
     */
    boolean isClassroomAvailable(Long classroomId, String timeSlot, String dayOfWeek);
    
    /**
     * 查找指定时间段的可用教室
     */
    List<Classroom> findAvailableClassroomsForTimeSlot(String timeSlot, String dayOfWeek);
    
    /**
     * 检查教室时间冲突
     */
    boolean checkClassroomConflict(Long classroomId, String timeSlot, String dayOfWeek);
    
    /**
     * 预订教室
     */
    boolean reserveClassroom(Long classroomId, String timeSlot, String dayOfWeek, Long userId);
    
    /**
     * 取消教室预订
     */
    boolean cancelClassroomReservation(Long classroomId, String timeSlot, String dayOfWeek);
    
    // ================================
    // 统计操作
    // ================================
    
    /**
     * 统计教室总数
     */
    long countTotalClassrooms();
    
    /**
     * 统计可用教室数量
     */
    long countAvailableClassrooms();
    
    /**
     * 统计各建筑物教室数量
     */
    List<Object[]> countClassroomsByBuilding();
    
    /**
     * 统计教室使用率
     */
    Double calculateClassroomUtilizationRate(Long classroomId);
    
    // ================================
    // 搜索操作
    // ================================
    
    /**
     * 搜索教室
     */
    Page<Classroom> searchClassrooms(String keyword, Pageable pageable);
    
    /**
     * 根据设备搜索教室
     */
    List<Classroom> findClassroomsByEquipment(String equipment);
    
    // ================================
    // 管理操作
    // ================================
    
    /**
     * 启用教室
     */
    void enableClassroom(Long id);
    
    /**
     * 禁用教室
     */
    void disableClassroom(Long id);
    
    /**
     * 批量导入教室
     */
    void importClassrooms(List<Classroom> classrooms);
    
    /**
     * 导出教室数据
     */
    List<Classroom> exportClassrooms();
}
