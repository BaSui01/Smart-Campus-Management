package com.campus.domain.repository;

import com.campus.domain.entity.CourseSelectionPeriod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 选课时间段Repository接口
 * 提供选课时间段相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface CourseSelectionPeriodRepository extends BaseRepository<CourseSelectionPeriod> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据时间段名称查找选课时间段
     */
    @Query("SELECT csp FROM CourseSelectionPeriod csp WHERE csp.periodName = :periodName AND csp.deleted = 0")
    Optional<CourseSelectionPeriod> findByPeriodName(@Param("periodName") String periodName);

    /**
     * 根据学期查找选课时间段列表
     */
    @Query("SELECT csp FROM CourseSelectionPeriod csp WHERE csp.semester = :semester AND csp.deleted = 0 ORDER BY csp.startTime ASC")
    List<CourseSelectionPeriod> findBySemester(@Param("semester") String semester);

    /**
     * 分页根据学期查找选课时间段列表
     */
    @Query("SELECT csp FROM CourseSelectionPeriod csp WHERE csp.semester = :semester AND csp.deleted = 0")
    Page<CourseSelectionPeriod> findBySemester(@Param("semester") String semester, Pageable pageable);

    /**
     * 根据选课类型查找选课时间段列表
     */
    @Query("SELECT csp FROM CourseSelectionPeriod csp WHERE csp.selectionType = :selectionType AND csp.deleted = 0 ORDER BY csp.startTime ASC")
    List<CourseSelectionPeriod> findBySelectionType(@Param("selectionType") String selectionType);

    /**
     * 根据适用年级查找选课时间段列表
     */
    @Query("SELECT csp FROM CourseSelectionPeriod csp WHERE csp.applicableGrades LIKE CONCAT('%', :applicableGrade, '%') AND csp.deleted = 0 ORDER BY csp.startTime ASC")
    List<CourseSelectionPeriod> findByApplicableGrade(@Param("applicableGrade") String applicableGrade);

    /**
     * 根据时间段名称模糊查询
     */
    @Query("SELECT csp FROM CourseSelectionPeriod csp WHERE csp.periodName LIKE %:periodName% AND csp.deleted = 0 ORDER BY csp.startTime ASC")
    List<CourseSelectionPeriod> findByPeriodNameContaining(@Param("periodName") String periodName);

    /**
     * 查找启用的选课时间段
     */
    @Query("SELECT csp FROM CourseSelectionPeriod csp WHERE csp.status = 1 AND csp.deleted = 0 ORDER BY csp.startTime ASC")
    List<CourseSelectionPeriod> findActiveSelectionPeriods();

    /**
     * 分页查找启用的选课时间段
     */
    @Query("SELECT csp FROM CourseSelectionPeriod csp WHERE csp.status = 1 AND csp.deleted = 0")
    Page<CourseSelectionPeriod> findActiveSelectionPeriods(Pageable pageable);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找选课时间段
     */
    @Query("SELECT csp FROM CourseSelectionPeriod csp WHERE " +
           "(:periodName IS NULL OR csp.periodName LIKE %:periodName%) AND " +
           "(:semester IS NULL OR csp.semester = :semester) AND " +
           "(:selectionType IS NULL OR csp.selectionType = :selectionType) AND " +
           "(:applicableGrade IS NULL OR csp.applicableGrades LIKE CONCAT('%', :applicableGrade, '%')) AND " +
           "(:status IS NULL OR csp.status = :status) AND " +
           "csp.deleted = 0")
    Page<CourseSelectionPeriod> findByMultipleConditions(@Param("periodName") String periodName,
                                                        @Param("semester") String semester,
                                                        @Param("selectionType") String selectionType,
                                                        @Param("applicableGrade") String applicableGrade,
                                                        @Param("status") Integer status,
                                                        Pageable pageable);

    /**
     * 搜索选课时间段（根据时间段名称、描述等关键词）
     */
    @Query("SELECT csp FROM CourseSelectionPeriod csp WHERE " +
           "(csp.periodName LIKE %:keyword% OR " +
           "csp.description LIKE %:keyword%) AND " +
           "csp.deleted = 0 ORDER BY csp.startTime ASC")
    List<CourseSelectionPeriod> searchSelectionPeriods(@Param("keyword") String keyword);

    /**
     * 分页搜索选课时间段
     */
    @Query("SELECT csp FROM CourseSelectionPeriod csp WHERE " +
           "(csp.periodName LIKE %:keyword% OR " +
           "csp.description LIKE %:keyword%) AND " +
           "csp.deleted = 0")
    Page<CourseSelectionPeriod> searchSelectionPeriods(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据学期和选课类型查找选课时间段
     */
    @Query("SELECT csp FROM CourseSelectionPeriod csp WHERE csp.semester = :semester AND csp.selectionType = :selectionType AND csp.deleted = 0 ORDER BY csp.startTime ASC")
    List<CourseSelectionPeriod> findBySemesterAndSelectionType(@Param("semester") String semester, 
                                                              @Param("selectionType") String selectionType);

    // ================================
    // 时间相关查询
    // ================================

    /**
     * 查找当前正在进行的选课时间段
     */
    @Query("SELECT csp FROM CourseSelectionPeriod csp WHERE csp.startTime <= :now AND csp.endTime >= :now AND csp.status = 1 AND csp.deleted = 0 ORDER BY csp.startTime ASC")
    List<CourseSelectionPeriod> findCurrentSelectionPeriods(@Param("now") LocalDateTime now);

    /**
     * 查找即将开始的选课时间段
     */
    @Query("SELECT csp FROM CourseSelectionPeriod csp WHERE csp.startTime > :now AND csp.status = 1 AND csp.deleted = 0 ORDER BY csp.startTime ASC")
    List<CourseSelectionPeriod> findUpcomingSelectionPeriods(@Param("now") LocalDateTime now);

    /**
     * 查找已结束的选课时间段
     */
    @Query("SELECT csp FROM CourseSelectionPeriod csp WHERE csp.endTime < :now AND csp.deleted = 0 ORDER BY csp.endTime DESC")
    List<CourseSelectionPeriod> findPastSelectionPeriods(@Param("now") LocalDateTime now);

    /**
     * 根据时间范围查找选课时间段
     */
    @Query("SELECT csp FROM CourseSelectionPeriod csp WHERE " +
           "((csp.startTime <= :startTime AND csp.endTime > :startTime) OR " +
           "(csp.startTime < :endTime AND csp.endTime >= :endTime) OR " +
           "(csp.startTime >= :startTime AND csp.endTime <= :endTime)) AND " +
           "csp.deleted = 0 ORDER BY csp.startTime ASC")
    List<CourseSelectionPeriod> findByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 查找指定时间点包含的选课时间段
     */
    @Query("SELECT csp FROM CourseSelectionPeriod csp WHERE csp.startTime <= :time AND csp.endTime >= :time AND csp.deleted = 0 ORDER BY csp.startTime ASC")
    List<CourseSelectionPeriod> findSelectionPeriodsContainingTime(@Param("time") LocalDateTime time);

    /**
     * 查找与指定时间段重叠的选课时间段
     */
    @Query("SELECT csp FROM CourseSelectionPeriod csp WHERE " +
           "((csp.startTime <= :startTime AND csp.endTime > :startTime) OR " +
           "(csp.startTime < :endTime AND csp.endTime >= :endTime) OR " +
           "(csp.startTime >= :startTime AND csp.endTime <= :endTime)) AND " +
           "csp.deleted = 0 ORDER BY csp.startTime ASC")
    List<CourseSelectionPeriod> findOverlappingSelectionPeriods(@Param("startTime") LocalDateTime startTime, 
                                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 查找与指定时间段重叠的选课时间段（排除指定ID）
     */
    @Query("SELECT csp FROM CourseSelectionPeriod csp WHERE " +
           "((csp.startTime <= :startTime AND csp.endTime > :startTime) OR " +
           "(csp.startTime < :endTime AND csp.endTime >= :endTime) OR " +
           "(csp.startTime >= :startTime AND csp.endTime <= :endTime)) AND " +
           "csp.id != :excludeId AND csp.deleted = 0 ORDER BY csp.startTime ASC")
    List<CourseSelectionPeriod> findOverlappingSelectionPeriodsExcluding(@Param("startTime") LocalDateTime startTime, 
                                                                        @Param("endTime") LocalDateTime endTime, 
                                                                        @Param("excludeId") Long excludeId);

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据学期统计选课时间段数量
     */
    @Query("SELECT csp.semester, COUNT(csp) FROM CourseSelectionPeriod csp WHERE csp.deleted = 0 GROUP BY csp.semester ORDER BY csp.semester")
    List<Object[]> countBySemester();

    /**
     * 根据选课类型统计数量
     */
    @Query("SELECT csp.selectionType, COUNT(csp) FROM CourseSelectionPeriod csp WHERE csp.deleted = 0 GROUP BY csp.selectionType ORDER BY csp.selectionType")
    List<Object[]> countBySelectionType();

    /**
     * 根据适用年级统计数量
     */
    @Query("SELECT csp.applicableGrades, COUNT(csp) FROM CourseSelectionPeriod csp WHERE csp.deleted = 0 GROUP BY csp.applicableGrades ORDER BY csp.applicableGrades")
    List<Object[]> countByApplicableGrade();

    /**
     * 根据状态统计数量
     */
    @Query("SELECT csp.status, COUNT(csp) FROM CourseSelectionPeriod csp WHERE csp.deleted = 0 GROUP BY csp.status")
    List<Object[]> countByStatus();

    /**
     * 统计指定学期的选课时间段数量
     */
    @Query("SELECT COUNT(csp) FROM CourseSelectionPeriod csp WHERE csp.semester = :semester AND csp.deleted = 0")
    long countBySemester(@Param("semester") String semester);

    /**
     * 统计当前正在进行的选课时间段数量
     */
    @Query("SELECT COUNT(csp) FROM CourseSelectionPeriod csp WHERE csp.startTime <= :now AND csp.endTime >= :now AND csp.status = 1 AND csp.deleted = 0")
    long countCurrentSelectionPeriods(@Param("now") LocalDateTime now);

    /**
     * 统计即将开始的选课时间段数量
     */
    @Query("SELECT COUNT(csp) FROM CourseSelectionPeriod csp WHERE csp.startTime > :now AND csp.status = 1 AND csp.deleted = 0")
    long countUpcomingSelectionPeriods(@Param("now") LocalDateTime now);

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查时间段名称是否存在
     */
    @Query("SELECT CASE WHEN COUNT(csp) > 0 THEN true ELSE false END FROM CourseSelectionPeriod csp WHERE csp.periodName = :periodName AND csp.deleted = 0")
    boolean existsByPeriodName(@Param("periodName") String periodName);

    /**
     * 检查时间段名称是否存在（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(csp) > 0 THEN true ELSE false END FROM CourseSelectionPeriod csp WHERE csp.periodName = :periodName AND csp.id != :excludeId AND csp.deleted = 0")
    boolean existsByPeriodNameAndIdNot(@Param("periodName") String periodName, @Param("excludeId") Long excludeId);

    /**
     * 检查时间段是否与现有时间段冲突
     */
    @Query("SELECT CASE WHEN COUNT(csp) > 0 THEN true ELSE false END FROM CourseSelectionPeriod csp WHERE " +
           "((csp.startTime <= :startTime AND csp.endTime > :startTime) OR " +
           "(csp.startTime < :endTime AND csp.endTime >= :endTime) OR " +
           "(csp.startTime >= :startTime AND csp.endTime <= :endTime)) AND " +
           "csp.deleted = 0")
    boolean hasTimeConflict(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 检查时间段是否与现有时间段冲突（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(csp) > 0 THEN true ELSE false END FROM CourseSelectionPeriod csp WHERE " +
           "((csp.startTime <= :startTime AND csp.endTime > :startTime) OR " +
           "(csp.startTime < :endTime AND csp.endTime >= :endTime) OR " +
           "(csp.startTime >= :startTime AND csp.endTime <= :endTime)) AND " +
           "csp.id != :excludeId AND csp.deleted = 0")
    boolean hasTimeConflictExcluding(@Param("startTime") LocalDateTime startTime, 
                                    @Param("endTime") LocalDateTime endTime, 
                                    @Param("excludeId") Long excludeId);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新选课时间段状态
     */
    @Modifying
    @Query("UPDATE CourseSelectionPeriod csp SET csp.status = :status, csp.updatedAt = CURRENT_TIMESTAMP WHERE csp.id = :periodId")
    int updateStatus(@Param("periodId") Long periodId, @Param("status") Integer status);

    /**
     * 批量更新选课时间段状态
     */
    @Modifying
    @Query("UPDATE CourseSelectionPeriod csp SET csp.status = :status, csp.updatedAt = CURRENT_TIMESTAMP WHERE csp.id IN :periodIds")
    int batchUpdateStatus(@Param("periodIds") List<Long> periodIds, @Param("status") Integer status);

    /**
     * 更新选课时间段时间
     */
    @Modifying
    @Query("UPDATE CourseSelectionPeriod csp SET csp.startTime = :startTime, csp.endTime = :endTime, csp.updatedAt = CURRENT_TIMESTAMP WHERE csp.id = :periodId")
    int updateTime(@Param("periodId") Long periodId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 延长选课时间段
     */
    @Modifying
    @Query("UPDATE CourseSelectionPeriod csp SET csp.endTime = :newEndTime, csp.updatedAt = CURRENT_TIMESTAMP WHERE csp.id = :periodId")
    int extendEndTime(@Param("periodId") Long periodId, @Param("newEndTime") LocalDateTime newEndTime);

    // ================================
    // 选课时间段管理方法
    // ================================

    /**
     * 获取所有学期
     */
    @Query("SELECT DISTINCT csp.semester FROM CourseSelectionPeriod csp WHERE csp.deleted = 0 ORDER BY csp.semester")
    List<String> findAllSemesters();

    /**
     * 获取所有选课类型
     */
    @Query("SELECT DISTINCT csp.selectionType FROM CourseSelectionPeriod csp WHERE csp.deleted = 0 ORDER BY csp.selectionType")
    List<String> findAllSelectionTypes();

    /**
     * 获取所有适用年级
     */
    @Query("SELECT DISTINCT csp.applicableGrades FROM CourseSelectionPeriod csp WHERE csp.deleted = 0 ORDER BY csp.applicableGrades")
    List<String> findAllApplicableGrades();

    /**
     * 自动关闭过期的选课时间段
     */
    @Modifying
    @Query("UPDATE CourseSelectionPeriod csp SET csp.status = 0, csp.updatedAt = CURRENT_TIMESTAMP WHERE csp.endTime < :now AND csp.status = 1")
    int autoCloseExpiredPeriods(@Param("now") LocalDateTime now);

    /**
     * 自动开启到时的选课时间段
     */
    @Modifying
    @Query("UPDATE CourseSelectionPeriod csp SET csp.status = 1, csp.updatedAt = CURRENT_TIMESTAMP WHERE csp.startTime <= :now AND csp.endTime > :now AND csp.status = 0")
    int autoOpenDuePeriods(@Param("now") LocalDateTime now);

    // ================================
    // 兼容性方法（为现有Service提供支持）
    // ================================

    /**
     * 根据多个条件查找选课时间段（兼容性方法）
     */
    default Page<CourseSelectionPeriod> findWithFilters(String semester, Integer academicYear, String selectionType, Pageable pageable) {
        return findByMultipleConditions(null, semester, selectionType, null, null, pageable);
    }

    /**
     * 查找当前开放的选课时间段（兼容性方法）
     */
    default List<CourseSelectionPeriod> findCurrentOpenPeriods(LocalDateTime now) {
        return findCurrentSelectionPeriods(now);
    }

    /**
     * 查找适用于学生的选课时间段（兼容性方法）
     */
    default List<CourseSelectionPeriod> findAvailablePeriodsForStudent(String grade, String major) {
        return findByApplicableGrade(grade);
    }

    /**
     * 查找当前开放且适用于学生的选课时间段（兼容性方法）
     */
    default List<CourseSelectionPeriod> findCurrentOpenPeriodsForStudent(LocalDateTime now, String grade, String major) {
        return findCurrentSelectionPeriods(now);
    }

    /**
     * 根据学期和学年查找选课时间段并按优先级排序（兼容性方法）
     */
    default List<CourseSelectionPeriod> findBySemesterAndAcademicYearOrderByPriorityAsc(String semester, Integer academicYear) {
        return findBySemester(semester);
    }

    /**
     * 查找即将开始的选课时间段（兼容性方法）
     */
    default List<CourseSelectionPeriod> findUpcomingPeriods(LocalDateTime now, LocalDateTime futureTime) {
        return findUpcomingSelectionPeriods(now);
    }

    /**
     * 统计冲突的选课时间段数量（兼容性方法）
     */
    @Query("SELECT COUNT(csp) FROM CourseSelectionPeriod csp WHERE " +
           "((csp.startTime <= :startTime AND csp.endTime > :startTime) OR " +
           "(csp.startTime < :endTime AND csp.endTime >= :endTime) OR " +
           "(csp.startTime >= :startTime AND csp.endTime <= :endTime)) AND " +
           "csp.id != :excludeId AND csp.selectionType = :selectionType AND " +
           "csp.semester = :semester AND csp.academicYear = :academicYear AND csp.deleted = 0")
    long countConflictingPeriods(@Param("excludeId") Long excludeId,
                                @Param("selectionType") String selectionType,
                                @Param("semester") String semester,
                                @Param("academicYear") Integer academicYear,
                                @Param("startTime") LocalDateTime startTime,
                                @Param("endTime") LocalDateTime endTime);

    /**
     * 查找过期的选课时间段（兼容性方法）
     */
    default List<CourseSelectionPeriod> findExpiredPeriods(LocalDateTime now) {
        return findPastSelectionPeriods(now);
    }

}
