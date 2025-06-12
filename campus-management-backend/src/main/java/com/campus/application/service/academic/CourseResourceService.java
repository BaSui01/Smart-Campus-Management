package com.campus.application.service.academic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.campus.domain.entity.resource.CourseResource;

import java.util.List;
import java.util.Optional;

/**
 * 课程资源服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
public interface CourseResourceService {

    /**
     * 上传课程资源
     */
    CourseResource uploadResource(Long courseId, Long teacherId, MultipartFile file, 
                                 String resourceName, String description, String resourceType);

    /**
     * 创建课程资源
     */
    CourseResource createResource(CourseResource resource);

    /**
     * 更新课程资源
     */
    CourseResource updateResource(CourseResource resource);

    /**
     * 根据ID查找资源
     */
    Optional<CourseResource> findById(Long id);

    /**
     * 删除课程资源
     */
    void deleteById(Long id);

    /**
     * 分页查询课程资源
     */
    Page<CourseResource> findWithFilters(Long courseId, Long teacherId, String resourceType, 
                                        Boolean isPublic, String keyword, Pageable pageable);

    /**
     * 根据课程ID查找资源
     */
    List<CourseResource> findByCourseId(Long courseId);

    /**
     * 查找公开的课程资源
     */
    List<CourseResource> findPublicResourcesByCourse(Long courseId);

    /**
     * 查找必读资料
     */
    List<CourseResource> findRequiredResourcesByCourse(Long courseId);

    /**
     * 根据章节查找资源
     */
    List<CourseResource> findByChapter(Long courseId, String chapter);

    /**
     * 根据标签查找资源
     */
    List<CourseResource> findByTag(Long courseId, String tag);

    /**
     * 根据资源类型查找资源
     */
    List<CourseResource> findByResourceType(String resourceType);

    /**
     * 根据教师ID查找资源
     */
    List<CourseResource> findByTeacherId(Long teacherId);

    /**
     * 下载资源
     */
    byte[] downloadResource(Long resourceId, Long userId);

    /**
     * 预览资源
     */
    byte[] previewResource(Long resourceId, Long userId);

    /**
     * 检查用户是否有访问权限
     */
    boolean hasAccessPermission(Long resourceId, Long userId, String userRole, boolean isEnrolled);

    /**
     * 记录资源访问
     */
    void recordAccess(Long resourceId, Long userId, String accessType, String ipAddress, String userAgent);

    /**
     * 获取资源统计信息
     */
    ResourceStatistics getStatistics(Long courseId);

    /**
     * 批量上传资源
     */
    List<CourseResource> batchUploadResources(Long courseId, Long teacherId, List<MultipartFile> files);

    /**
     * 批量删除资源
     */
    void batchDeleteResources(List<Long> resourceIds);

    /**
     * 复制资源到其他课程
     */
    List<CourseResource> copyResourcesToCourse(List<Long> resourceIds, Long targetCourseId, Long teacherId);

    /**
     * 移动资源到其他课程
     */
    List<CourseResource> moveResourcesToCourse(List<Long> resourceIds, Long targetCourseId);

    /**
     * 设置资源排序
     */
    void updateResourceOrder(List<Long> resourceIds, List<Integer> sortOrders);

    /**
     * 查找热门资源
     */
    List<CourseResource> findPopularResources(int limit);

    /**
     * 查找最新资源
     */
    List<CourseResource> findLatestResources(int limit);

    /**
     * 查找大文件资源
     */
    List<CourseResource> findLargeFiles(Long minSizeInMB);

    /**
     * 查找过期资源
     */
    List<CourseResource> findExpiredResources();

    /**
     * 查找即将过期的资源
     */
    List<CourseResource> findExpiringResources(int days);

    /**
     * 自动清理过期资源
     */
    int autoCleanExpiredResources();

    /**
     * 获取课程资源总大小
     */
    Long getCourseResourceTotalSize(Long courseId);

    /**
     * 获取教师上传资源总大小
     */
    Long getTeacherResourceTotalSize(Long teacherId);

    /**
     * 检查文件名是否重复
     */
    boolean isFileNameDuplicate(Long courseId, String fileName, Long excludeId);

    /**
     * 生成唯一文件名
     */
    String generateUniqueFileName(Long courseId, String originalFileName);

    /**
     * 获取支持的文件类型
     */
    List<String> getSupportedFileTypes();

    /**
     * 验证文件类型
     */
    boolean isFileTypeSupported(String fileExtension);

    /**
     * 获取文件存储路径
     */
    String getFileStoragePath(Long courseId, String fileName);

    /**
     * 资源统计信息内部类
     */
    class ResourceStatistics {
        private long totalResources;
        private long totalSize;
        private long totalDownloads;
        private long totalViews;
        private List<TypeCount> typeStatistics;
        private List<CourseResource> popularResources;
        private List<CourseResource> recentResources;

        public ResourceStatistics() {}

        public ResourceStatistics(long totalResources, long totalSize, long totalDownloads, long totalViews) {
            this.totalResources = totalResources;
            this.totalSize = totalSize;
            this.totalDownloads = totalDownloads;
            this.totalViews = totalViews;
        }

        // Getters and Setters
        public long getTotalResources() {
            return totalResources;
        }

        public void setTotalResources(long totalResources) {
            this.totalResources = totalResources;
        }

        public long getTotalSize() {
            return totalSize;
        }

        public void setTotalSize(long totalSize) {
            this.totalSize = totalSize;
        }

        public long getTotalDownloads() {
            return totalDownloads;
        }

        public void setTotalDownloads(long totalDownloads) {
            this.totalDownloads = totalDownloads;
        }

        public long getTotalViews() {
            return totalViews;
        }

        public void setTotalViews(long totalViews) {
            this.totalViews = totalViews;
        }

        public List<TypeCount> getTypeStatistics() {
            return typeStatistics;
        }

        public void setTypeStatistics(List<TypeCount> typeStatistics) {
            this.typeStatistics = typeStatistics;
        }

        public List<CourseResource> getPopularResources() {
            return popularResources;
        }

        public void setPopularResources(List<CourseResource> popularResources) {
            this.popularResources = popularResources;
        }

        public List<CourseResource> getRecentResources() {
            return recentResources;
        }

        public void setRecentResources(List<CourseResource> recentResources) {
            this.recentResources = recentResources;
        }
    }

    /**
     * 类型统计内部类
     */
    class TypeCount {
        private String type;
        private long count;

        public TypeCount() {}

        public TypeCount(String type, long count) {
            this.type = type;
            this.count = count;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }

    // ================================
    // CourseResourceController 需要的方法
    // ================================

    /**
     * 获取资源类型列表
     */
    List<String> getResourceTypes();

    /**
     * 获取最大文件大小限制
     */
    Long getMaxFileSize();

    /**
     * 获取允许的文件类型
     */
    List<String> getAllowedFileTypes();

    /**
     * 根据ID获取资源
     */
    CourseResource getResourceById(Long id);

    /**
     * 获取资源访问日志
     */
    List<Object> getResourceAccessLogs(Long resourceId, int page, int size);

    /**
     * 获取资源下载统计
     */
    Object getResourceDownloadStats(Long resourceId);

    /**
     * 根据课程获取资源列表
     */
    List<CourseResource> getResourcesByCourse(Long courseId, String resourceType);

    /**
     * 获取资源统计信息
     */
    Object getResourceStatistics(Long courseId);

    /**
     * 获取资源分类列表
     */
    List<String> getResourceCategories();

    /**
     * 获取所有资源
     */
    List<CourseResource> getAllResources();

    /**
     * 获取存储信息
     */
    Object getStorageInfo();

    /**
     * 获取存储统计
     */
    Object getStorageStatistics();

    /**
     * 获取大文件列表
     */
    List<CourseResource> getLargeFiles();

    /**
     * 获取最大批量处理大小
     */
    Integer getMaxBatchSize();

    /**
     * 获取整体统计信息
     */
    Object getOverallStatistics();

    /**
     * 获取资源类型统计
     */
    List<Object> getResourceTypeStatistics();

    /**
     * 获取资源使用统计
     */
    List<Object> getResourceUsageStatistics();

    /**
     * 获取下载趋势
     */
    List<Object[]> getDownloadTrends();

    /**
     * 获取搜索过滤器
     */
    List<String> getSearchFilters();

    /**
     * 获取已删除的资源
     */
    List<CourseResource> getDeletedResources();

    /**
     * 获取资源设置
     */
    Object getResourceSettings();

    /**
     * 获取存储设置
     */
    Object getStorageSettings();

    /**
     * 检查资源访问权限
     */
    boolean checkResourceAccess(Long resourceId, Long userId);
}
