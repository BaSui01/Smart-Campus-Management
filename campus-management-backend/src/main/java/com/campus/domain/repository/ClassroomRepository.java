package com.campus.domain.repository;

import com.campus.domain.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 教室数据访问接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

    /**
     * 根据教室编号查找教室
     */
    Optional<Classroom> findByClassroomNo(String classroomNo);

    /**
     * 根据教室类型查找教室
     */
    List<Classroom> findByClassroomType(String classroomType);

    /**
     * 根据容量范围查找教室
     */
    List<Classroom> findByCapacityBetween(Integer minCapacity, Integer maxCapacity);

    /**
     * 根据最小容量查找教室
     */
    List<Classroom> findByCapacityGreaterThanEqual(Integer minCapacity);

    /**
     * 根据建筑ID查找教室
     */
    List<Classroom> findByBuildingId(Long buildingId);

    /**
     * 根据楼层查找教室
     */
    List<Classroom> findByFloor(Integer floor);

    /**
     * 查找有特定设备的教室
     */
    @Query("SELECT c FROM Classroom c WHERE " +
           "(:hasProjector IS NULL OR c.hasProjector = :hasProjector) AND " +
           "(:hasAudio IS NULL OR c.hasAudio = :hasAudio) AND " +
           "(:hasComputer IS NULL OR c.hasComputer = :hasComputer) AND " +
           "(:hasNetwork IS NULL OR c.hasNetwork = :hasNetwork) AND " +
           "(:hasAirConditioning IS NULL OR c.hasAirConditioning = :hasAirConditioning) AND " +
           "c.deleted = 0")
    List<Classroom> findByEquipment(@Param("hasProjector") Boolean hasProjector,
                                   @Param("hasAudio") Boolean hasAudio,
                                   @Param("hasComputer") Boolean hasComputer,
                                   @Param("hasNetwork") Boolean hasNetwork,
                                   @Param("hasAirConditioning") Boolean hasAirConditioning);

    /**
     * 查找适合特定课程类型的教室
     */
    @Query("SELECT c FROM Classroom c WHERE " +
           "(c.suitableCourseTypes IS NULL OR c.suitableCourseTypes LIKE %:courseType%) AND " +
           "c.deleted = 0")
    List<Classroom> findSuitableForCourseType(@Param("courseType") String courseType);

    /**
     * 根据关键词搜索教室
     */
    @Query("SELECT c FROM Classroom c WHERE " +
           "(c.classroomName LIKE %:keyword% OR " +
           "c.classroomNo LIKE %:keyword% OR " +
           "c.locationDescription LIKE %:keyword%) AND " +
           "c.deleted = 0")
    List<Classroom> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 多条件搜索教室
     */
    @Query("SELECT c FROM Classroom c WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           " c.classroomName LIKE %:keyword% OR " +
           " c.classroomNo LIKE %:keyword% OR " +
           " c.locationDescription LIKE %:keyword%) AND " +
           "(:classroomType IS NULL OR :classroomType = '' OR c.classroomType = :classroomType) AND " +
           "(:minCapacity IS NULL OR c.capacity >= :minCapacity) AND " +
           "(:maxCapacity IS NULL OR c.capacity <= :maxCapacity) AND " +
           "(:buildingId IS NULL OR c.buildingId = :buildingId) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "c.deleted = 0")
    List<Classroom> searchClassrooms(@Param("keyword") String keyword,
                                    @Param("classroomType") String classroomType,
                                    @Param("minCapacity") Integer minCapacity,
                                    @Param("maxCapacity") Integer maxCapacity,
                                    @Param("buildingId") Long buildingId,
                                    @Param("status") Integer status);

    /**
     * 查找可用教室（状态为启用且未删除）
     */
    @Query("SELECT c FROM Classroom c WHERE c.status = 1 AND c.deleted = 0")
    List<Classroom> findActiveClassrooms();

    /**
     * 根据状态查找教室
     */
    List<Classroom> findByStatus(Integer status);

    /**
     * 统计未删除的教室数量
     */
    @Query("SELECT COUNT(c) FROM Classroom c WHERE c.deleted = 0")
    long countByDeletedNot();

    /**
     * 根据教室编号检查是否存在
     */
    boolean existsByClassroomNo(String classroomNo);

    /**
     * 根据教室编号检查是否存在（排除指定ID）
     */
    @Query("SELECT COUNT(c) > 0 FROM Classroom c WHERE c.classroomNo = :classroomNo AND c.id != :excludeId")
    boolean existsByClassroomNoAndIdNot(@Param("classroomNo") String classroomNo, @Param("excludeId") Long excludeId);

    /**
     * 获取教室容量统计
     */
    @Query("SELECT " +
           "MIN(c.capacity) as minCapacity, " +
           "MAX(c.capacity) as maxCapacity, " +
           "AVG(c.capacity) as avgCapacity " +
           "FROM Classroom c WHERE c.deleted = 0")
    Object[] getCapacityStatistics();

    /**
     * 按教室类型统计数量
     */
    @Query("SELECT c.classroomType, COUNT(c) FROM Classroom c WHERE c.deleted = 0 GROUP BY c.classroomType")
    List<Object[]> countByClassroomType();

    /**
     * 软删除教室
     */
    @Query("UPDATE Classroom c SET c.deleted = 1, c.updatedAt = CURRENT_TIMESTAMP WHERE c.id = :id")
    int softDelete(@Param("id") Long id);

    /**
     * 批量软删除教室
     */
    @Query("UPDATE Classroom c SET c.deleted = 1, c.updatedAt = CURRENT_TIMESTAMP WHERE c.id IN :ids")
    int batchSoftDelete(@Param("ids") List<Long> ids);

    /**
     * 更新教室状态
     */
    @Query("UPDATE Classroom c SET c.status = :status, c.updatedAt = CURRENT_TIMESTAMP WHERE c.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 批量更新教室状态
     */
    @Query("UPDATE Classroom c SET c.status = :status, c.updatedAt = CURRENT_TIMESTAMP WHERE c.id IN :ids")
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 根据管理员ID查找教室
     */
    List<Classroom> findByAdministratorId(Long administratorId);

    /**
     * 查找指定时间段可用的教室
     */
    @Query("SELECT c FROM Classroom c WHERE c.id NOT IN (" +
           "SELECT DISTINCT cs.classroomId FROM CourseSchedule cs WHERE " +
           "cs.dayOfWeek = :dayOfWeek AND cs.periodNumber = :periodNumber AND " +
           "cs.semester = :semester AND cs.academicYear = :academicYear AND " +
           "cs.deleted = 0) AND c.status = 1 AND c.deleted = 0")
    List<Classroom> findAvailableClassrooms(@Param("dayOfWeek") Integer dayOfWeek,
                                           @Param("periodNumber") Integer periodNumber,
                                           @Param("semester") String semester,
                                           @Param("academicYear") Integer academicYear);

    /**
     * 获取教室详细信息（包含统计数据）
     */
    @Query("SELECT c, " +
           "(SELECT COUNT(cs) FROM CourseSchedule cs WHERE cs.classroomId = c.id AND cs.deleted = 0) as scheduleCount " +
           "FROM Classroom c WHERE c.id = :id")
    Optional<Object[]> findDetailById(@Param("id") Long id);
}