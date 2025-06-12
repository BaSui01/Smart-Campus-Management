package com.campus.domain.repository.academic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campus.domain.entity.academic.TimeSlot;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * 时间段数据访问接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    /**
     * 根据时间段名称查找
     */
    Optional<TimeSlot> findBySlotName(String slotName);

    /**
     * 根据星期几查找时间段
     */
    List<TimeSlot> findByDayOfWeek(Integer dayOfWeek);

    /**
     * 根据节次查找时间段
     */
    List<TimeSlot> findByPeriodNumber(Integer periodNumber);

    /**
     * 根据星期几和节次查找时间段
     */
    Optional<TimeSlot> findByDayOfWeekAndPeriodNumber(Integer dayOfWeek, Integer periodNumber);

    /**
     * 根据时间段类型查找
     */
    List<TimeSlot> findBySlotType(String slotType);

    /**
     * 根据开始时间范围查找
     */
    List<TimeSlot> findByStartTimeBetween(LocalTime startTime, LocalTime endTime);

    /**
     * 根据时间段类型和星期几查找
     */
    List<TimeSlot> findBySlotTypeAndDayOfWeek(String slotType, Integer dayOfWeek);

    /**
     * 查找连续时间段
     */
    List<TimeSlot> findByIsContinuous(Boolean isContinuous);

    /**
     * 按排序序号查找
     */
    List<TimeSlot> findAllByOrderBySortOrder();

    /**
     * 按星期几和节次排序查找
     */
    List<TimeSlot> findAllByOrderByDayOfWeekAscPeriodNumberAsc();

    /**
     * 查找可用时间段（状态为启用且未删除）
     */
    @Query("SELECT t FROM TimeSlot t WHERE t.status = 1 AND t.deleted = 0 ORDER BY t.dayOfWeek, t.periodNumber")
    List<TimeSlot> findActiveTimeSlots();

    /**
     * 根据时间范围查找可用时间段
     */
    @Query("SELECT t FROM TimeSlot t WHERE " +
           "t.startTime >= :startTime AND t.endTime <= :endTime AND " +
           "t.status = 1 AND t.deleted = 0 " +
           "ORDER BY t.dayOfWeek, t.periodNumber")
    List<TimeSlot> findAvailableTimeSlotsInRange(@Param("startTime") LocalTime startTime,
                                                @Param("endTime") LocalTime endTime);

    /**
     * 查找指定星期几的可用时间段
     */
    @Query("SELECT t FROM TimeSlot t WHERE " +
           "t.dayOfWeek = :dayOfWeek AND t.status = 1 AND t.deleted = 0 " +
           "ORDER BY t.periodNumber")
    List<TimeSlot> findAvailableTimeSlotsForDay(@Param("dayOfWeek") Integer dayOfWeek);

    /**
     * 查找指定时间类型的可用时间段
     */
    @Query("SELECT t FROM TimeSlot t WHERE " +
           "t.slotType = :slotType AND t.status = 1 AND t.deleted = 0 " +
           "ORDER BY t.dayOfWeek, t.periodNumber")
    List<TimeSlot> findAvailableTimeSlotsForType(@Param("slotType") String slotType);

    /**
     * 根据关键词搜索时间段
     */
    @Query("SELECT t FROM TimeSlot t WHERE " +
           "(t.slotName LIKE %:keyword% OR t.description LIKE %:keyword%) AND " +
           "t.deleted = 0")
    List<TimeSlot> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 多条件搜索时间段
     */
    @Query("SELECT t FROM TimeSlot t WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           " t.slotName LIKE %:keyword% OR t.description LIKE %:keyword%) AND " +
           "(:dayOfWeek IS NULL OR t.dayOfWeek = :dayOfWeek) AND " +
           "(:periodNumber IS NULL OR t.periodNumber = :periodNumber) AND " +
           "(:slotType IS NULL OR :slotType = '' OR t.slotType = :slotType) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "t.deleted = 0")
    List<TimeSlot> searchTimeSlots(@Param("keyword") String keyword,
                                  @Param("dayOfWeek") Integer dayOfWeek,
                                  @Param("periodNumber") Integer periodNumber,
                                  @Param("slotType") String slotType,
                                  @Param("status") Integer status);

    /**
     * 检查时间段是否存在冲突
     */
    @Query("SELECT COUNT(t) > 0 FROM TimeSlot t WHERE " +
           "t.dayOfWeek = :dayOfWeek AND t.periodNumber = :periodNumber AND " +
           "t.id != :excludeId AND t.deleted = 0")
    boolean existsConflictingTimeSlot(@Param("dayOfWeek") Integer dayOfWeek,
                                     @Param("periodNumber") Integer periodNumber,
                                     @Param("excludeId") Long excludeId);

    /**
     * 检查时间段名称是否存在
     */
    boolean existsBySlotName(String slotName);

    /**
     * 检查时间段名称是否存在（排除指定ID）
     */
    @Query("SELECT COUNT(t) > 0 FROM TimeSlot t WHERE t.slotName = :slotName AND t.id != :excludeId")
    boolean existsBySlotNameAndIdNot(@Param("slotName") String slotName, @Param("excludeId") Long excludeId);

    /**
     * 统计未删除的时间段数量
     */
    @Query("SELECT COUNT(t) FROM TimeSlot t WHERE t.deleted = 0")
    long countByDeletedNot();

    /**
     * 按时间段类型统计数量
     */
    @Query("SELECT t.slotType, COUNT(t) FROM TimeSlot t WHERE t.deleted = 0 GROUP BY t.slotType")
    List<Object[]> countBySlotType();

    /**
     * 按星期几统计时间段数量
     */
    @Query("SELECT t.dayOfWeek, COUNT(t) FROM TimeSlot t WHERE t.deleted = 0 GROUP BY t.dayOfWeek ORDER BY t.dayOfWeek")
    List<Object[]> countByDayOfWeek();

    /**
     * 获取时间段统计信息
     */
    @Query("SELECT " +
           "COUNT(t) as totalSlots, " +
           "COUNT(CASE WHEN t.status = 1 THEN 1 END) as activeSlots, " +
           "COUNT(CASE WHEN t.status = 0 THEN 1 END) as inactiveSlots " +
           "FROM TimeSlot t WHERE t.deleted = 0")
    Object[] getTimeSlotStatistics();

    /**
     * 软删除时间段
     */
    @Query("UPDATE TimeSlot t SET t.deleted = 1, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
    int softDelete(@Param("id") Long id);

    /**
     * 批量软删除时间段
     */
    @Query("UPDATE TimeSlot t SET t.deleted = 1, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id IN :ids")
    int batchSoftDelete(@Param("ids") List<Long> ids);

    /**
     * 更新时间段状态
     */
    @Query("UPDATE TimeSlot t SET t.status = :status, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 批量更新时间段状态
     */
    @Query("UPDATE TimeSlot t SET t.status = :status, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id IN :ids")
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 查找指定时间段内未被占用的时间段
     */
    @Query("SELECT t FROM TimeSlot t WHERE t.id NOT IN (" +
           "SELECT DISTINCT cs.timeSlotId FROM CourseSchedule cs WHERE " +
           "cs.semester = :semester AND cs.academicYear = :academicYear AND " +
           "cs.deleted = 0) AND t.status = 1 AND t.deleted = 0 " +
           "ORDER BY t.dayOfWeek, t.periodNumber")
    List<TimeSlot> findUnusedTimeSlots(@Param("semester") String semester,
                                      @Param("academicYear") Integer academicYear);

    /**
     * 查找指定教师在指定时间段的可用时间
     */
    @Query("SELECT t FROM TimeSlot t WHERE t.id NOT IN (" +
           "SELECT DISTINCT cs.timeSlotId FROM CourseSchedule cs WHERE " +
           "cs.teacherId = :teacherId AND cs.semester = :semester AND " +
           "cs.academicYear = :academicYear AND cs.deleted = 0) AND " +
           "t.status = 1 AND t.deleted = 0 " +
           "ORDER BY t.dayOfWeek, t.periodNumber")
    List<TimeSlot> findAvailableTimeSlotsForTeacher(@Param("teacherId") Long teacherId,
                                                   @Param("semester") String semester,
                                                   @Param("academicYear") Integer academicYear);

    /**
     * 查找指定教室在指定时间段的可用时间
     */
    @Query("SELECT t FROM TimeSlot t WHERE t.id NOT IN (" +
           "SELECT DISTINCT cs.timeSlotId FROM CourseSchedule cs WHERE " +
           "cs.classroomId = :classroomId AND cs.semester = :semester AND " +
           "cs.academicYear = :academicYear AND cs.deleted = 0) AND " +
           "t.status = 1 AND t.deleted = 0 " +
           "ORDER BY t.dayOfWeek, t.periodNumber")
    List<TimeSlot> findAvailableTimeSlotsForClassroom(@Param("classroomId") Long classroomId,
                                                     @Param("semester") String semester,
                                                     @Param("academicYear") Integer academicYear);

    /**
     * 获取时间段详细信息（包含使用统计）
     */
    @Query("SELECT t, " +
           "(SELECT COUNT(cs) FROM CourseSchedule cs WHERE cs.timeSlotId = t.id AND cs.deleted = 0) as usageCount " +
           "FROM TimeSlot t WHERE t.id = :id")
    Optional<Object[]> findDetailById(@Param("id") Long id);

    /**
     * 查找最大节次号
     */
    @Query("SELECT MAX(t.periodNumber) FROM TimeSlot t WHERE t.dayOfWeek = :dayOfWeek AND t.deleted = 0")
    Integer findMaxPeriodNumberForDay(@Param("dayOfWeek") Integer dayOfWeek);

    /**
     * 查找最大排序序号
     */
    @Query("SELECT MAX(t.sortOrder) FROM TimeSlot t WHERE t.deleted = 0")
    Integer findMaxSortOrder();

    // ================================
    // TimeSlotServiceImpl需要的方法
    // ================================

    /**
     * 根据ID和删除状态查找时间段
     */
    @Query("SELECT t FROM TimeSlot t WHERE t.id = :id AND t.deleted = :deleted")
    Optional<TimeSlot> findByIdAndDeleted(@Param("id") Long id, @Param("deleted") Integer deleted);

    /**
     * 根据删除状态查找所有时间段，按开始时间排序
     */
    @Query("SELECT t FROM TimeSlot t WHERE t.deleted = :deleted ORDER BY t.startTime ASC")
    List<TimeSlot> findByDeletedOrderByStartTimeAsc(@Param("deleted") Integer deleted);

    /**
     * 分页根据删除状态查找时间段，按开始时间排序
     */
    @Query("SELECT t FROM TimeSlot t WHERE t.deleted = :deleted ORDER BY t.startTime ASC")
    org.springframework.data.domain.Page<TimeSlot> findByDeletedOrderByStartTimeAsc(@Param("deleted") Integer deleted, org.springframework.data.domain.Pageable pageable);

    /**
     * 根据状态和删除状态查找时间段，按开始时间排序
     */
    @Query("SELECT t FROM TimeSlot t WHERE t.status = :status AND t.deleted = :deleted ORDER BY t.startTime ASC")
    List<TimeSlot> findByStatusAndDeletedOrderByStartTimeAsc(@Param("status") Integer status, @Param("deleted") Integer deleted);

    /**
     * 根据时间段名称和删除状态查找
     */
    @Query("SELECT t FROM TimeSlot t WHERE t.slotName = :slotName AND t.deleted = :deleted")
    Optional<TimeSlot> findBySlotNameAndDeleted(@Param("slotName") String slotName, @Param("deleted") Integer deleted);

    /**
     * 根据开始时间范围和删除状态查找时间段，按开始时间排序
     */
    @Query("SELECT t FROM TimeSlot t WHERE t.startTime BETWEEN :startTime AND :endTime AND t.deleted = :deleted ORDER BY t.startTime ASC")
    List<TimeSlot> findByStartTimeBetweenAndDeletedOrderByStartTimeAsc(@Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime, @Param("deleted") Integer deleted);

    /**
     * 根据时间段类型和删除状态查找，按开始时间排序
     */
    @Query("SELECT t FROM TimeSlot t WHERE t.slotType = :slotType AND t.deleted = :deleted ORDER BY t.startTime ASC")
    List<TimeSlot> findBySlotTypeAndDeletedOrderByStartTimeAsc(@Param("slotType") String slotType, @Param("deleted") Integer deleted);

    /**
     * 查找时间冲突的时间段
     */
    @Query("SELECT t FROM TimeSlot t WHERE " +
           "((t.startTime <= :startTime AND t.endTime > :startTime) OR " +
           "(t.startTime < :endTime AND t.endTime >= :endTime) OR " +
           "(t.startTime >= :startTime AND t.endTime <= :endTime)) AND " +
           "t.deleted = :deleted")
    List<TimeSlot> findConflictingTimeSlots(@Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime, @Param("deleted") Integer deleted);

    /**
     * 查找时间冲突的时间段（排除指定ID）
     */
    @Query("SELECT t FROM TimeSlot t WHERE " +
           "((t.startTime <= :startTime AND t.endTime > :startTime) OR " +
           "(t.startTime < :endTime AND t.endTime >= :endTime) OR " +
           "(t.startTime >= :startTime AND t.endTime <= :endTime)) AND " +
           "t.id != :excludeId AND t.deleted = :deleted")
    List<TimeSlot> findConflictingTimeSlotsExcluding(@Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime, @Param("excludeId") Long excludeId, @Param("deleted") Integer deleted);

    /**
     * 检查时间段名称和删除状态是否存在
     */
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM TimeSlot t WHERE t.slotName = :slotName AND t.deleted = :deleted")
    boolean existsBySlotNameAndDeleted(@Param("slotName") String slotName, @Param("deleted") Integer deleted);

    /**
     * 根据时间查找时间段
     */
    @Query("SELECT t FROM TimeSlot t WHERE :time BETWEEN t.startTime AND t.endTime AND t.deleted = :deleted")
    Optional<TimeSlot> findByTimeInRange(@Param("time") LocalTime time, @Param("deleted") Integer deleted);

    /**
     * 查找下一个时间段
     */
    @Query("SELECT t FROM TimeSlot t WHERE t.startTime >= :endTime AND t.deleted = :deleted ORDER BY t.startTime ASC")
    Optional<TimeSlot> findNextTimeSlot(@Param("endTime") LocalTime endTime, @Param("deleted") Integer deleted);

    /**
     * 查找上一个时间段
     */
    @Query("SELECT t FROM TimeSlot t WHERE t.endTime <= :startTime AND t.deleted = :deleted ORDER BY t.endTime DESC")
    Optional<TimeSlot> findPreviousTimeSlot(@Param("startTime") LocalTime startTime, @Param("deleted") Integer deleted);

    /**
     * 根据删除状态统计数量
     */
    @Query("SELECT COUNT(t) FROM TimeSlot t WHERE t.deleted = :deleted")
    long countByDeleted(@Param("deleted") Integer deleted);

    /**
     * 根据状态和删除状态统计数量
     */
    @Query("SELECT COUNT(t) FROM TimeSlot t WHERE t.status = :status AND t.deleted = :deleted")
    long countByStatusAndDeleted(@Param("status") Integer status, @Param("deleted") Integer deleted);

    /**
     * 获取使用统计
     */
    @Query("SELECT t.slotType, COUNT(t) as count FROM TimeSlot t WHERE t.deleted = 0 GROUP BY t.slotType")
    List<Object[]> getUsageStatistics();

    /**
     * 根据时间段名称模糊查询，按开始时间排序
     */
    @Query("SELECT t FROM TimeSlot t WHERE t.slotName LIKE %:slotName% AND t.deleted = :deleted ORDER BY t.startTime ASC")
    org.springframework.data.domain.Page<TimeSlot> findBySlotNameContainingIgnoreCaseAndDeletedOrderByStartTimeAsc(@Param("slotName") String slotName, @Param("deleted") Integer deleted, org.springframework.data.domain.Pageable pageable);

    /**
     * 查找无效时间段
     */
    @Query("SELECT t FROM TimeSlot t WHERE t.startTime >= t.endTime OR t.deleted = 1")
    List<TimeSlot> findInvalidTimeSlots();
}