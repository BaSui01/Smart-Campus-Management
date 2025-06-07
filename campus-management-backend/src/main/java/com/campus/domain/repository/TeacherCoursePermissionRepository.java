package com.campus.domain.repository;

import com.campus.domain.entity.TeacherCoursePermission;
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
 * 教师课程权限Repository接口
 * 提供教师课程权限相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface TeacherCoursePermissionRepository extends BaseRepository<TeacherCoursePermission> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据教师ID查找权限
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE tcp.teacherId = :teacherId AND tcp.deleted = 0 ORDER BY tcp.createdAt DESC")
    List<TeacherCoursePermission> findByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 分页根据教师ID查找权限
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE tcp.teacherId = :teacherId AND tcp.deleted = 0")
    Page<TeacherCoursePermission> findByTeacherId(@Param("teacherId") Long teacherId, Pageable pageable);

    /**
     * 根据课程ID查找权限
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE tcp.courseId = :courseId AND tcp.deleted = 0 ORDER BY tcp.createdAt DESC")
    List<TeacherCoursePermission> findByCourseId(@Param("courseId") Long courseId);

    /**
     * 分页根据课程ID查找权限
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE tcp.courseId = :courseId AND tcp.deleted = 0")
    Page<TeacherCoursePermission> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    /**
     * 根据权限类型查找权限
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE tcp.permissionType = :permissionType AND tcp.deleted = 0 ORDER BY tcp.createdAt DESC")
    List<TeacherCoursePermission> findByPermissionType(@Param("permissionType") String permissionType);

    /**
     * 根据教师和课程ID查找权限
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE tcp.teacherId = :teacherId AND tcp.courseId = :courseId AND tcp.deleted = 0")
    Optional<TeacherCoursePermission> findByTeacherIdAndCourseId(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);

    /**
     * 查找启用的权限
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE tcp.status = 1 AND tcp.deleted = 0 ORDER BY tcp.createdAt DESC")
    List<TeacherCoursePermission> findActivePermissions();

    /**
     * 分页查找启用的权限
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE tcp.status = 1 AND tcp.deleted = 0")
    Page<TeacherCoursePermission> findActivePermissions(Pageable pageable);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找权限
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE " +
           "(:teacherId IS NULL OR tcp.teacherId = :teacherId) AND " +
           "(:courseId IS NULL OR tcp.courseId = :courseId) AND " +
           "(:permissionType IS NULL OR tcp.permissionType = :permissionType) AND " +
           "(:status IS NULL OR tcp.status = :status) AND " +
           "tcp.deleted = 0")
    Page<TeacherCoursePermission> findByMultipleConditions(@Param("teacherId") Long teacherId,
                                                          @Param("courseId") Long courseId,
                                                          @Param("permissionType") String permissionType,
                                                          @Param("status") Integer status,
                                                          Pageable pageable);

    /**
     * 根据教师和权限类型查找权限
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE tcp.teacherId = :teacherId AND tcp.permissionType = :permissionType AND tcp.deleted = 0 ORDER BY tcp.createdAt DESC")
    List<TeacherCoursePermission> findByTeacherIdAndPermissionType(@Param("teacherId") Long teacherId, @Param("permissionType") String permissionType);

    /**
     * 根据课程和权限类型查找权限
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE tcp.courseId = :courseId AND tcp.permissionType = :permissionType AND tcp.deleted = 0 ORDER BY tcp.createdAt DESC")
    List<TeacherCoursePermission> findByCourseIdAndPermissionType(@Param("courseId") Long courseId, @Param("permissionType") String permissionType);

    // ================================
    // 时间相关查询
    // ================================

    /**
     * 查找有效期内的权限
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE " +
           "(tcp.startTime IS NULL OR tcp.startTime <= :now) AND " +
           "(tcp.endTime IS NULL OR tcp.endTime >= :now) AND " +
           "tcp.status = 1 AND tcp.deleted = 0 ORDER BY tcp.createdAt DESC")
    List<TeacherCoursePermission> findValidPermissions(@Param("now") LocalDateTime now);

    /**
     * 查找已过期的权限
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE tcp.endTime IS NOT NULL AND tcp.endTime < :now AND tcp.deleted = 0 ORDER BY tcp.endTime DESC")
    List<TeacherCoursePermission> findExpiredPermissions(@Param("now") LocalDateTime now);

    /**
     * 查找即将过期的权限
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE tcp.endTime IS NOT NULL AND " +
           "tcp.endTime BETWEEN :now AND :futureTime AND tcp.status = 1 AND tcp.deleted = 0 ORDER BY tcp.endTime ASC")
    List<TeacherCoursePermission> findExpiringPermissions(@Param("now") LocalDateTime now, @Param("futureTime") LocalDateTime futureTime);

    /**
     * 根据时间范围查找权限
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE " +
           "((tcp.startTime IS NULL OR tcp.startTime <= :endTime) AND " +
           "(tcp.endTime IS NULL OR tcp.endTime >= :startTime)) AND " +
           "tcp.deleted = 0 ORDER BY tcp.createdAt DESC")
    List<TeacherCoursePermission> findByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 查找权限并预加载教师信息
     */
    @Query("SELECT DISTINCT tcp FROM TeacherCoursePermission tcp LEFT JOIN FETCH tcp.teacher WHERE tcp.deleted = 0")
    List<TeacherCoursePermission> findAllWithTeacher();

    /**
     * 查找权限并预加载课程信息
     */
    @Query("SELECT DISTINCT tcp FROM TeacherCoursePermission tcp LEFT JOIN FETCH tcp.course WHERE tcp.deleted = 0")
    List<TeacherCoursePermission> findAllWithCourse();

    /**
     * 查找权限并预加载所有关联信息
     */
    @Query("SELECT DISTINCT tcp FROM TeacherCoursePermission tcp " +
           "LEFT JOIN FETCH tcp.teacher t " +
           "LEFT JOIN FETCH tcp.course c " +
           "WHERE tcp.deleted = 0")
    List<TeacherCoursePermission> findAllWithAssociations();

    /**
     * 根据教师ID查找权限并预加载课程信息
     */
    @Query("SELECT DISTINCT tcp FROM TeacherCoursePermission tcp LEFT JOIN FETCH tcp.course WHERE tcp.teacherId = :teacherId AND tcp.deleted = 0 ORDER BY tcp.createdAt DESC")
    List<TeacherCoursePermission> findByTeacherIdWithCourse(@Param("teacherId") Long teacherId);

    /**
     * 根据课程ID查找权限并预加载教师信息
     */
    @Query("SELECT DISTINCT tcp FROM TeacherCoursePermission tcp LEFT JOIN FETCH tcp.teacher WHERE tcp.courseId = :courseId AND tcp.deleted = 0 ORDER BY tcp.createdAt DESC")
    List<TeacherCoursePermission> findByCourseIdWithTeacher(@Param("courseId") Long courseId);

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据权限类型统计数量
     */
    @Query("SELECT tcp.permissionType, COUNT(tcp) FROM TeacherCoursePermission tcp WHERE tcp.deleted = 0 GROUP BY tcp.permissionType ORDER BY COUNT(tcp) DESC")
    List<Object[]> countByPermissionType();

    /**
     * 根据状态统计数量
     */
    @Query("SELECT tcp.status, COUNT(tcp) FROM TeacherCoursePermission tcp WHERE tcp.deleted = 0 GROUP BY tcp.status")
    List<Object[]> countByStatus();

    /**
     * 根据教师统计权限数量
     */
    @Query("SELECT u.username, COUNT(tcp) FROM TeacherCoursePermission tcp LEFT JOIN tcp.teacher u WHERE tcp.deleted = 0 GROUP BY tcp.teacherId, u.username ORDER BY COUNT(tcp) DESC")
    List<Object[]> countByTeacher();

    /**
     * 根据课程统计权限数量
     */
    @Query("SELECT c.courseName, COUNT(tcp) FROM TeacherCoursePermission tcp LEFT JOIN tcp.course c WHERE tcp.deleted = 0 GROUP BY tcp.courseId, c.courseName ORDER BY COUNT(tcp) DESC")
    List<Object[]> countByCourse();

    /**
     * 统计指定教师的权限数量
     */
    @Query("SELECT COUNT(tcp) FROM TeacherCoursePermission tcp WHERE tcp.teacherId = :teacherId AND tcp.deleted = 0")
    long countByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 统计指定课程的权限数量
     */
    @Query("SELECT COUNT(tcp) FROM TeacherCoursePermission tcp WHERE tcp.courseId = :courseId AND tcp.deleted = 0")
    long countByCourseId(@Param("courseId") Long courseId);

    /**
     * 统计指定权限类型的数量
     */
    @Query("SELECT COUNT(tcp) FROM TeacherCoursePermission tcp WHERE tcp.permissionType = :permissionType AND tcp.deleted = 0")
    long countByPermissionType(@Param("permissionType") String permissionType);

    /**
     * 统计启用的权限数量
     */
    @Query("SELECT COUNT(tcp) FROM TeacherCoursePermission tcp WHERE tcp.status = 1 AND tcp.deleted = 0")
    long countActivePermissions();

    /**
     * 统计有效期内的权限数量
     */
    @Query("SELECT COUNT(tcp) FROM TeacherCoursePermission tcp WHERE " +
           "(tcp.startTime IS NULL OR tcp.startTime <= :now) AND " +
           "(tcp.endTime IS NULL OR tcp.endTime >= :now) AND " +
           "tcp.status = 1 AND tcp.deleted = 0")
    long countValidPermissions(@Param("now") LocalDateTime now);

    // ================================
    // 权限检查方法
    // ================================

    /**
     * 检查教师是否有课程权限
     */
    @Query("SELECT CASE WHEN COUNT(tcp) > 0 THEN true ELSE false END FROM TeacherCoursePermission tcp WHERE " +
           "tcp.teacherId = :teacherId AND tcp.courseId = :courseId AND " +
           "(tcp.startTime IS NULL OR tcp.startTime <= :now) AND " +
           "(tcp.endTime IS NULL OR tcp.endTime >= :now) AND " +
           "tcp.status = 1 AND tcp.deleted = 0")
    boolean hasPermission(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId, @Param("now") LocalDateTime now);

    /**
     * 检查教师是否有指定类型的课程权限
     */
    @Query("SELECT CASE WHEN COUNT(tcp) > 0 THEN true ELSE false END FROM TeacherCoursePermission tcp WHERE " +
           "tcp.teacherId = :teacherId AND tcp.courseId = :courseId AND tcp.permissionType = :permissionType AND " +
           "(tcp.startTime IS NULL OR tcp.startTime <= :now) AND " +
           "(tcp.endTime IS NULL OR tcp.endTime >= :now) AND " +
           "tcp.status = 1 AND tcp.deleted = 0")
    boolean hasPermissionType(@Param("teacherId") Long teacherId, 
                             @Param("courseId") Long courseId, 
                             @Param("permissionType") String permissionType, 
                             @Param("now") LocalDateTime now);

    /**
     * 检查教师课程权限是否存在
     */
    @Query("SELECT CASE WHEN COUNT(tcp) > 0 THEN true ELSE false END FROM TeacherCoursePermission tcp WHERE tcp.teacherId = :teacherId AND tcp.courseId = :courseId AND tcp.deleted = 0")
    boolean existsByTeacherIdAndCourseId(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新权限状态
     */
    @Modifying
    @Query("UPDATE TeacherCoursePermission tcp SET tcp.status = :status, tcp.updatedAt = CURRENT_TIMESTAMP WHERE tcp.id = :permissionId")
    int updateStatus(@Param("permissionId") Long permissionId, @Param("status") Integer status);

    /**
     * 批量更新权限状态
     */
    @Modifying
    @Query("UPDATE TeacherCoursePermission tcp SET tcp.status = :status, tcp.updatedAt = CURRENT_TIMESTAMP WHERE tcp.id IN :permissionIds")
    int batchUpdateStatus(@Param("permissionIds") List<Long> permissionIds, @Param("status") Integer status);

    /**
     * 更新权限类型
     */
    @Modifying
    @Query("UPDATE TeacherCoursePermission tcp SET tcp.permissionType = :permissionType, tcp.updatedAt = CURRENT_TIMESTAMP WHERE tcp.id = :permissionId")
    int updatePermissionType(@Param("permissionId") Long permissionId, @Param("permissionType") String permissionType);

    /**
     * 更新权限有效期
     */
    @Modifying
    @Query("UPDATE TeacherCoursePermission tcp SET tcp.startTime = :startTime, tcp.endTime = :endTime, tcp.updatedAt = CURRENT_TIMESTAMP WHERE tcp.id = :permissionId")
    int updateValidPeriod(@Param("permissionId") Long permissionId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 延长权限有效期
     */
    @Modifying
    @Query("UPDATE TeacherCoursePermission tcp SET tcp.endTime = :newEndTime, tcp.updatedAt = CURRENT_TIMESTAMP WHERE tcp.id = :permissionId")
    int extendValidPeriod(@Param("permissionId") Long permissionId, @Param("newEndTime") LocalDateTime newEndTime);

    /**
     * 撤销教师的所有课程权限
     */
    @Modifying
    @Query("UPDATE TeacherCoursePermission tcp SET tcp.status = 0, tcp.updatedAt = CURRENT_TIMESTAMP WHERE tcp.teacherId = :teacherId")
    int revokeAllByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 撤销课程的所有教师权限
     */
    @Modifying
    @Query("UPDATE TeacherCoursePermission tcp SET tcp.status = 0, tcp.updatedAt = CURRENT_TIMESTAMP WHERE tcp.courseId = :courseId")
    int revokeAllByCourseId(@Param("courseId") Long courseId);

    /**
     * 自动过期权限
     */
    @Modifying
    @Query("UPDATE TeacherCoursePermission tcp SET tcp.status = 0, tcp.updatedAt = CURRENT_TIMESTAMP WHERE tcp.endTime < :now AND tcp.status = 1")
    int autoExpirePermissions(@Param("now") LocalDateTime now);

    // ================================
    // 权限管理方法
    // ================================

    /**
     * 获取所有权限类型
     */
    @Query("SELECT DISTINCT tcp.permissionType FROM TeacherCoursePermission tcp WHERE tcp.deleted = 0 ORDER BY tcp.permissionType")
    List<String> findAllPermissionTypes();

    /**
     * 获取教师的所有课程ID
     */
    @Query("SELECT tcp.courseId FROM TeacherCoursePermission tcp WHERE tcp.teacherId = :teacherId AND " +
           "(tcp.startTime IS NULL OR tcp.startTime <= :now) AND " +
           "(tcp.endTime IS NULL OR tcp.endTime >= :now) AND " +
           "tcp.status = 1 AND tcp.deleted = 0")
    List<Long> findCourseIdsByTeacherId(@Param("teacherId") Long teacherId, @Param("now") LocalDateTime now);

    /**
     * 获取课程的所有教师ID
     */
    @Query("SELECT tcp.teacherId FROM TeacherCoursePermission tcp WHERE tcp.courseId = :courseId AND " +
           "(tcp.startTime IS NULL OR tcp.startTime <= :now) AND " +
           "(tcp.endTime IS NULL OR tcp.endTime >= :now) AND " +
           "tcp.status = 1 AND tcp.deleted = 0")
    List<Long> findTeacherIdsByCourseId(@Param("courseId") Long courseId, @Param("now") LocalDateTime now);

    /**
     * 获取教师的主要课程权限
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE tcp.teacherId = :teacherId AND tcp.permissionType = 'PRIMARY' AND " +
           "(tcp.startTime IS NULL OR tcp.startTime <= :now) AND " +
           "(tcp.endTime IS NULL OR tcp.endTime >= :now) AND " +
           "tcp.status = 1 AND tcp.deleted = 0 ORDER BY tcp.createdAt ASC")
    List<TeacherCoursePermission> findPrimaryCoursesByTeacherId(@Param("teacherId") Long teacherId, @Param("now") LocalDateTime now);

    /**
     * 获取课程的主要教师权限
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE tcp.courseId = :courseId AND tcp.permissionType = 'PRIMARY' AND " +
           "(tcp.startTime IS NULL OR tcp.startTime <= :now) AND " +
           "(tcp.endTime IS NULL OR tcp.endTime >= :now) AND " +
           "tcp.status = 1 AND tcp.deleted = 0")
    Optional<TeacherCoursePermission> findPrimaryTeacherByCourseId(@Param("courseId") Long courseId, @Param("now") LocalDateTime now);

}
