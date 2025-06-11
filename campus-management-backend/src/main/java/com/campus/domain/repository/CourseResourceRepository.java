package com.campus.domain.repository;

import com.campus.domain.entity.CourseResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 课程资源Repository接口
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface CourseResourceRepository extends JpaRepository<CourseResource, Long>, JpaSpecificationExecutor<CourseResource> {
    
    /**
     * 根据课程ID查找资源列表
     */
    List<CourseResource> findByCourseIdAndDeletedOrderByCreatedAtDesc(Long courseId, Integer deleted);
    
    /**
     * 根据资源类型查找资源列表
     */
    List<CourseResource> findByResourceTypeAndDeletedOrderByCreatedAtDesc(String resourceType, Integer deleted);
    
    /**
     * 根据课程和资源类型查找资源
     */
    @Query("SELECT cr FROM CourseResource cr WHERE cr.course.id = :courseId AND cr.resourceType = :type AND cr.deleted = 0")
    List<CourseResource> findByCourseAndType(@Param("courseId") Long courseId, @Param("type") String resourceType);
    
    /**
     * 根据ID和删除状态查找资源
     */
    Optional<CourseResource> findByIdAndDeleted(Long id, Integer deleted);
    
    /**
     * 检查文件路径是否已存在
     */
    boolean existsByFilePathAndDeleted(String filePath, Integer deleted);
    
    /**
     * 根据文件名查找资源（分页）
     */
    Page<CourseResource> findByFileNameContainingIgnoreCaseAndDeleted(String fileName, Integer deleted, Pageable pageable);

    /**
     * 根据文件名查找资源（列表）
     */
    List<CourseResource> findByFileNameContainingIgnoreCaseAndDeleted(String fileName, Integer deleted);
    
    /**
     * 根据上传者查找资源
     */
    List<CourseResource> findByUploadedByAndDeletedOrderByCreatedAtDesc(Long uploadedBy, Integer deleted);
    
    /**
     * 统计课程资源数量
     */
    long countByCourseIdAndDeleted(Long courseId, Integer deleted);
    
    /**
     * 统计资源类型数量
     */
    @Query("SELECT cr.resourceType, COUNT(cr) FROM CourseResource cr WHERE cr.deleted = 0 GROUP BY cr.resourceType")
    List<Object[]> countByResourceType();
    
    /**
     * 查找大文件资源（超过指定大小）
     */
    @Query("SELECT cr FROM CourseResource cr WHERE cr.fileSize > :maxSize AND cr.deleted = 0")
    List<CourseResource> findLargeFiles(@Param("maxSize") Long maxSize);
    
    /**
     * 根据状态查找资源
     */
    List<CourseResource> findByStatusAndDeletedOrderByCreatedAtDesc(Integer status, Integer deleted);
}
