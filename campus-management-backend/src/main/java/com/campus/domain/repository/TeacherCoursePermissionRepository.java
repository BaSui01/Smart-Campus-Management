package com.campus.domain.repository;

import com.campus.domain.entity.TeacherCoursePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 教师课程权限Repository接口
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface TeacherCoursePermissionRepository extends JpaRepository<TeacherCoursePermission, Long> {
    
    /**
     * 根据教师ID查找权限列表
     */
    List<TeacherCoursePermission> findByTeacherIdAndDeletedOrderByCreatedAtDesc(Long teacherId, Integer deleted);
    
    /**
     * 根据课程ID查找权限列表
     */
    List<TeacherCoursePermission> findByCourseIdAndDeletedOrderByCreatedAtDesc(Long courseId, Integer deleted);
    
    /**
     * 根据教师和课程查找权限
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE tcp.teacher.id = :teacherId AND tcp.course.id = :courseId AND tcp.deleted = 0")
    Optional<TeacherCoursePermission> findByTeacherAndCourse(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);
    
    /**
     * 检查教师是否有课程权限
     */
    boolean existsByTeacherIdAndCourseIdAndDeleted(Long teacherId, Long courseId, Integer deleted);
    
    /**
     * 根据ID和删除状态查找权限
     */
    Optional<TeacherCoursePermission> findByIdAndDeleted(Long id, Integer deleted);
    
    /**
     * 根据权限类型查找权限列表
     */
    List<TeacherCoursePermission> findByPermissionTypeAndDeletedOrderByCreatedAtDesc(String permissionType, Integer deleted);
    
    /**
     * 根据教师和权限类型查找权限
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE tcp.teacher.id = :teacherId AND tcp.permissionType = :type AND tcp.deleted = 0")
    List<TeacherCoursePermission> findByTeacherAndPermissionType(@Param("teacherId") Long teacherId, @Param("type") String permissionType);
    
    /**
     * 根据课程和权限类型查找权限
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE tcp.course.id = :courseId AND tcp.permissionType = :type AND tcp.deleted = 0")
    List<TeacherCoursePermission> findByCourseAndPermissionType(@Param("courseId") Long courseId, @Param("type") String permissionType);
    
    /**
     * 统计教师的课程权限数量
     */
    long countByTeacherIdAndDeleted(Long teacherId, Integer deleted);
    
    /**
     * 统计课程的教师权限数量
     */
    long countByCourseIdAndDeleted(Long courseId, Integer deleted);
    
    /**
     * 根据状态查找权限
     */
    List<TeacherCoursePermission> findByStatusAndDeletedOrderByCreatedAtDesc(Integer status, Integer deleted);
    
    /**
     * 查找有效的权限（状态为启用）
     */
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE tcp.teacher.id = :teacherId AND tcp.status = 1 AND tcp.deleted = 0")
    List<TeacherCoursePermission> findActivePermissionsByTeacher(@Param("teacherId") Long teacherId);
    
    /**
     * 根据权限类型统计数量
     */
    @Query("SELECT tcp.permissionType, COUNT(tcp) FROM TeacherCoursePermission tcp WHERE tcp.deleted = 0 GROUP BY tcp.permissionType")
    List<Object[]> countByPermissionType();
    
    /**
     * 检查特定权限是否存在
     */
    @Query("SELECT COUNT(tcp) > 0 FROM TeacherCoursePermission tcp WHERE tcp.teacher.id = :teacherId AND tcp.course.id = :courseId AND tcp.permissionType = :type AND tcp.status = 1 AND tcp.deleted = 0")
    boolean hasPermission(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId, @Param("type") String permissionType);
}
