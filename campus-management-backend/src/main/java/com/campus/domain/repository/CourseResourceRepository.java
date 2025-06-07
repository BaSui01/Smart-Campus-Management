package com.campus.domain.repository;

import com.campus.domain.entity.CourseResource;
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
 * 课程资源Repository接口
 * 提供课程资源相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface CourseResourceRepository extends BaseRepository<CourseResource> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据课程ID查找资源
     */
    @Query("SELECT cr FROM CourseResource cr WHERE cr.courseId = :courseId AND cr.deleted = 0 ORDER BY cr.sortOrder ASC, cr.createdAt DESC")
    List<CourseResource> findByCourseId(@Param("courseId") Long courseId);

    /**
     * 分页根据课程ID查找资源
     */
    @Query("SELECT cr FROM CourseResource cr WHERE cr.courseId = :courseId AND cr.deleted = 0")
    Page<CourseResource> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    /**
     * 根据资源类型查找资源
     */
    @Query("SELECT cr FROM CourseResource cr WHERE cr.resourceType = :resourceType AND cr.deleted = 0 ORDER BY cr.createdAt DESC")
    List<CourseResource> findByResourceType(@Param("resourceType") String resourceType);

    /**
     * 根据上传者ID查找资源
     */
    @Query("SELECT cr FROM CourseResource cr WHERE cr.uploaderId = :uploaderId AND cr.deleted = 0 ORDER BY cr.createdAt DESC")
    List<CourseResource> findByUploaderId(@Param("uploaderId") Long uploaderId);

    /**
     * 分页根据上传者ID查找资源
     */
    @Query("SELECT cr FROM CourseResource cr WHERE cr.uploaderId = :uploaderId AND cr.deleted = 0")
    Page<CourseResource> findByUploaderId(@Param("uploaderId") Long uploaderId, Pageable pageable);

    /**
     * 根据资源名称模糊查询
     */
    @Query("SELECT cr FROM CourseResource cr WHERE cr.resourceName LIKE %:resourceName% AND cr.deleted = 0 ORDER BY cr.createdAt DESC")
    List<CourseResource> findByResourceNameContaining(@Param("resourceName") String resourceName);

    /**
     * 根据文件类型查找资源
     */
    @Query("SELECT cr FROM CourseResource cr WHERE cr.fileType = :fileType AND cr.deleted = 0 ORDER BY cr.createdAt DESC")
    List<CourseResource> findByFileType(@Param("fileType") String fileType);

    /**
     * 查找公开的资源
     */
    @Query("SELECT cr FROM CourseResource cr WHERE cr.isPublic = true AND cr.deleted = 0 ORDER BY cr.createdAt DESC")
    List<CourseResource> findPublicResources();

    /**
     * 分页查找公开的资源
     */
    @Query("SELECT cr FROM CourseResource cr WHERE cr.isPublic = true AND cr.deleted = 0")
    Page<CourseResource> findPublicResources(Pageable pageable);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找资源
     */
    @Query("SELECT cr FROM CourseResource cr WHERE " +
           "(:courseId IS NULL OR cr.courseId = :courseId) AND " +
           "(:resourceType IS NULL OR cr.resourceType = :resourceType) AND " +
           "(:fileType IS NULL OR cr.fileType = :fileType) AND " +
           "(:uploaderId IS NULL OR cr.uploaderId = :uploaderId) AND " +
           "(:isPublic IS NULL OR cr.isPublic = :isPublic) AND " +
           "cr.deleted = 0")
    Page<CourseResource> findByMultipleConditions(@Param("courseId") Long courseId,
                                                 @Param("resourceType") String resourceType,
                                                 @Param("fileType") String fileType,
                                                 @Param("uploaderId") Long uploaderId,
                                                 @Param("isPublic") Boolean isPublic,
                                                 Pageable pageable);

    /**
     * 搜索资源（根据资源名称、描述等关键词）
     */
    @Query("SELECT cr FROM CourseResource cr WHERE " +
           "(cr.resourceName LIKE %:keyword% OR " +
           "cr.description LIKE %:keyword%) AND " +
           "cr.deleted = 0 ORDER BY cr.createdAt DESC")
    List<CourseResource> searchResources(@Param("keyword") String keyword);

    /**
     * 分页搜索资源
     */
    @Query("SELECT cr FROM CourseResource cr WHERE " +
           "(cr.resourceName LIKE %:keyword% OR " +
           "cr.description LIKE %:keyword%) AND " +
           "cr.deleted = 0")
    Page<CourseResource> searchResources(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据课程和资源类型查找资源
     */
    @Query("SELECT cr FROM CourseResource cr WHERE cr.courseId = :courseId AND cr.resourceType = :resourceType AND cr.deleted = 0 ORDER BY cr.sortOrder ASC, cr.createdAt DESC")
    List<CourseResource> findByCourseIdAndResourceType(@Param("courseId") Long courseId, @Param("resourceType") String resourceType);

    // ================================
    // 文件相关查询
    // ================================

    /**
     * 根据文件路径查找资源
     */
    @Query("SELECT cr FROM CourseResource cr WHERE cr.filePath = :filePath AND cr.deleted = 0")
    Optional<CourseResource> findByFilePath(@Param("filePath") String filePath);

    /**
     * 根据文件大小范围查找资源
     */
    @Query("SELECT cr FROM CourseResource cr WHERE cr.fileSize BETWEEN :minSize AND :maxSize AND cr.deleted = 0 ORDER BY cr.fileSize ASC")
    List<CourseResource> findByFileSizeBetween(@Param("minSize") Long minSize, @Param("maxSize") Long maxSize);

    /**
     * 查找大文件资源（超过指定大小）
     */
    @Query("SELECT cr FROM CourseResource cr WHERE cr.fileSize > :sizeThreshold AND cr.deleted = 0 ORDER BY cr.fileSize DESC")
    List<CourseResource> findLargeFiles(@Param("sizeThreshold") Long sizeThreshold);

    // ================================
    // 时间相关查询
    // ================================

    /**
     * 根据上传时间范围查找资源
     */
    @Query("SELECT cr FROM CourseResource cr WHERE cr.createdAt BETWEEN :startTime AND :endTime AND cr.deleted = 0 ORDER BY cr.createdAt DESC")
    List<CourseResource> findByUploadTimeBetween(@Param("startTime") LocalDateTime startTime, 
                                                @Param("endTime") LocalDateTime endTime);

    /**
     * 查找最近上传的资源
     */
    @Query("SELECT cr FROM CourseResource cr WHERE cr.deleted = 0 ORDER BY cr.createdAt DESC")
    List<CourseResource> findRecentResources(Pageable pageable);

    /**
     * 查找今日上传的资源
     */
    @Query("SELECT cr FROM CourseResource cr WHERE DATE(cr.createdAt) = CURRENT_DATE AND cr.deleted = 0 ORDER BY cr.createdAt DESC")
    List<CourseResource> findTodayResources();

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 查找资源并预加载课程信息
     */
    @Query("SELECT DISTINCT cr FROM CourseResource cr LEFT JOIN FETCH cr.course WHERE cr.deleted = 0")
    List<CourseResource> findAllWithCourse();

    /**
     * 查找资源并预加载上传者信息
     */
    @Query("SELECT DISTINCT cr FROM CourseResource cr LEFT JOIN FETCH cr.uploader WHERE cr.deleted = 0")
    List<CourseResource> findAllWithUploader();

    /**
     * 根据课程ID查找资源并预加载上传者信息
     */
    @Query("SELECT DISTINCT cr FROM CourseResource cr LEFT JOIN FETCH cr.uploader WHERE cr.courseId = :courseId AND cr.deleted = 0 ORDER BY cr.sortOrder ASC, cr.createdAt DESC")
    List<CourseResource> findByCourseIdWithUploader(@Param("courseId") Long courseId);

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据资源类型统计数量
     */
    @Query("SELECT cr.resourceType, COUNT(cr) FROM CourseResource cr WHERE cr.deleted = 0 GROUP BY cr.resourceType ORDER BY COUNT(cr) DESC")
    List<Object[]> countByResourceType();

    /**
     * 根据文件类型统计数量
     */
    @Query("SELECT cr.fileType, COUNT(cr) FROM CourseResource cr WHERE cr.deleted = 0 GROUP BY cr.fileType ORDER BY COUNT(cr) DESC")
    List<Object[]> countByFileType();

    /**
     * 根据课程统计资源数量
     */
    @Query("SELECT c.courseName, COUNT(cr) FROM CourseResource cr LEFT JOIN cr.course c WHERE cr.deleted = 0 GROUP BY cr.courseId, c.courseName ORDER BY COUNT(cr) DESC")
    List<Object[]> countByCourse();

    /**
     * 根据上传者统计资源数量
     */
    @Query("SELECT u.username, COUNT(cr) FROM CourseResource cr LEFT JOIN cr.uploader u WHERE cr.deleted = 0 GROUP BY cr.uploaderId, u.username ORDER BY COUNT(cr) DESC")
    List<Object[]> countByUploader();

    /**
     * 统计指定课程的资源数量
     */
    @Query("SELECT COUNT(cr) FROM CourseResource cr WHERE cr.courseId = :courseId AND cr.deleted = 0")
    long countByCourseId(@Param("courseId") Long courseId);

    /**
     * 统计指定课程和类型的资源数量
     */
    @Query("SELECT COUNT(cr) FROM CourseResource cr WHERE cr.courseId = :courseId AND cr.resourceType = :resourceType AND cr.deleted = 0")
    long countByCourseIdAndResourceType(@Param("courseId") Long courseId, @Param("resourceType") String resourceType);

    /**
     * 统计总文件大小
     */
    @Query("SELECT COALESCE(SUM(cr.fileSize), 0) FROM CourseResource cr WHERE cr.deleted = 0")
    long sumTotalFileSize();

    /**
     * 统计指定课程的总文件大小
     */
    @Query("SELECT COALESCE(SUM(cr.fileSize), 0) FROM CourseResource cr WHERE cr.courseId = :courseId AND cr.deleted = 0")
    long sumFileSizeByCourseId(@Param("courseId") Long courseId);

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查文件路径是否存在
     */
    @Query("SELECT CASE WHEN COUNT(cr) > 0 THEN true ELSE false END FROM CourseResource cr WHERE cr.filePath = :filePath AND cr.deleted = 0")
    boolean existsByFilePath(@Param("filePath") String filePath);

    /**
     * 检查资源名称是否存在（同课程内）
     */
    @Query("SELECT CASE WHEN COUNT(cr) > 0 THEN true ELSE false END FROM CourseResource cr WHERE cr.courseId = :courseId AND cr.resourceName = :resourceName AND cr.deleted = 0")
    boolean existsByCourseIdAndResourceName(@Param("courseId") Long courseId, @Param("resourceName") String resourceName);

    /**
     * 检查资源名称是否存在（同课程内，排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(cr) > 0 THEN true ELSE false END FROM CourseResource cr WHERE cr.courseId = :courseId AND cr.resourceName = :resourceName AND cr.id != :excludeId AND cr.deleted = 0")
    boolean existsByCourseIdAndResourceNameAndIdNot(@Param("courseId") Long courseId, @Param("resourceName") String resourceName, @Param("excludeId") Long excludeId);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新下载次数
     */
    @Modifying
    @Query("UPDATE CourseResource cr SET cr.downloadCount = cr.downloadCount + 1, cr.updatedAt = CURRENT_TIMESTAMP WHERE cr.id = :resourceId")
    int incrementDownloadCount(@Param("resourceId") Long resourceId);

    /**
     * 更新访问次数
     */
    @Modifying
    @Query("UPDATE CourseResource cr SET cr.viewCount = cr.viewCount + 1, cr.updatedAt = CURRENT_TIMESTAMP WHERE cr.id = :resourceId")
    int incrementViewCount(@Param("resourceId") Long resourceId);

    /**
     * 更新资源排序
     */
    @Modifying
    @Query("UPDATE CourseResource cr SET cr.sortOrder = :sortOrder, cr.updatedAt = CURRENT_TIMESTAMP WHERE cr.id = :resourceId")
    int updateSortOrder(@Param("resourceId") Long resourceId, @Param("sortOrder") Integer sortOrder);

    /**
     * 批量更新资源排序
     */
    @Modifying
    @Query("UPDATE CourseResource cr SET cr.sortOrder = :sortOrder, cr.updatedAt = CURRENT_TIMESTAMP WHERE cr.id IN :resourceIds")
    int batchUpdateSortOrder(@Param("resourceIds") List<Long> resourceIds, @Param("sortOrder") Integer sortOrder);

    /**
     * 更新公开状态
     */
    @Modifying
    @Query("UPDATE CourseResource cr SET cr.isPublic = :isPublic, cr.updatedAt = CURRENT_TIMESTAMP WHERE cr.id = :resourceId")
    int updatePublicStatus(@Param("resourceId") Long resourceId, @Param("isPublic") Boolean isPublic);

    // ================================
    // 数据清理方法
    // ================================

    /**
     * 删除指定时间之前的资源
     */
    @Modifying
    @Query("UPDATE CourseResource cr SET cr.deleted = 1, cr.updatedAt = CURRENT_TIMESTAMP WHERE cr.createdAt < :beforeTime")
    int deleteResourcesBefore(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 批量删除指定课程的资源
     */
    @Modifying
    @Query("UPDATE CourseResource cr SET cr.deleted = 1, cr.updatedAt = CURRENT_TIMESTAMP WHERE cr.courseId = :courseId")
    int deleteResourcesByCourseId(@Param("courseId") Long courseId);

    /**
     * 批量删除指定上传者的资源
     */
    @Modifying
    @Query("UPDATE CourseResource cr SET cr.deleted = 1, cr.updatedAt = CURRENT_TIMESTAMP WHERE cr.uploaderId = :uploaderId")
    int deleteResourcesByUploaderId(@Param("uploaderId") Long uploaderId);

}
