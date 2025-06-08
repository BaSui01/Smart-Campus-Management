package com.campus.application.service.impl;

import com.campus.application.service.CourseResourceService;
import com.campus.domain.entity.CourseResource;
import com.campus.domain.repository.CourseResourceRepository;
import com.campus.shared.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.util.ArrayList;
import org.springframework.web.multipart.MultipartFile;

/**
 * 课程资源服务实现类
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Service
@Transactional
public class CourseResourceServiceImpl implements CourseResourceService {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseResourceServiceImpl.class);
    
    @Autowired
    private CourseResourceRepository courseResourceRepository;
    
    // ================================
    // 辅助方法（非接口方法）
    // ================================

    public CourseResource uploadResourceFile(CourseResource courseResource) {
        logger.info("上传课程资源: {}", courseResource.getFileName());
        
        // 验证文件路径唯一性
        if (courseResourceRepository.existsByFilePathAndDeleted(courseResource.getFilePath(), 0)) {
            throw new BusinessException("文件路径已存在");
        }
        
        courseResource.setStatus(1);
        courseResource.setDeleted(0);
        return courseResourceRepository.save(courseResource);
    }
    
    @Transactional(readOnly = true)
    public Optional<CourseResource> findResourceById(Long id) {
        return courseResourceRepository.findByIdAndDeleted(id, 0);
    }
    
    public void deleteResource(Long id) {
        logger.info("删除课程资源: {}", id);
        
        Optional<CourseResource> resourceOpt = findResourceById(id);
        if (resourceOpt.isEmpty()) {
            throw new BusinessException("课程资源不存在");
        }
        
        CourseResource resource = resourceOpt.get();
        resource.setDeleted(1);
        courseResourceRepository.save(resource);
    }
    
    @Transactional(readOnly = true)
    public List<CourseResource> findResourcesByCourse(Long courseId) {
        return courseResourceRepository.findByCourseIdAndDeletedOrderByCreatedAtDesc(courseId, 0);
    }
    
    @Transactional(readOnly = true)
    public List<CourseResource> findResourcesByType(String resourceType) {
        return courseResourceRepository.findByResourceTypeAndDeletedOrderByCreatedAtDesc(resourceType, 0);
    }
    
    @Transactional(readOnly = true)
    public List<CourseResource> findResourcesByCourseAndType(Long courseId, String resourceType) {
        return courseResourceRepository.findByCourseAndType(courseId, resourceType);
    }
    
    @Transactional(readOnly = true)
    public List<CourseResource> findResourcesByUploader(Long uploaderId) {
        return courseResourceRepository.findByUploadedByAndDeletedOrderByCreatedAtDesc(uploaderId, 0);
    }
    
    @Transactional(readOnly = true)
    public Page<CourseResource> searchResources(String keyword, Pageable pageable) {
        return courseResourceRepository.findByFileNameContainingIgnoreCaseAndDeleted(keyword, 0, pageable);
    }
    
    @Transactional(readOnly = true)
    public long countResourcesByCourse(Long courseId) {
        return courseResourceRepository.countByCourseIdAndDeleted(courseId, 0);
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> getResourceStatisticsByType() {
        return courseResourceRepository.countByResourceType();
    }
    
    @Transactional(readOnly = true)
    public List<CourseResource> findLargeResources(Long maxSizeInBytes) {
        return courseResourceRepository.findLargeFiles(maxSizeInBytes);
    }
    
    public void enableResource(Long id) {
        logger.info("启用课程资源: {}", id);
        
        Optional<CourseResource> resourceOpt = findResourceById(id);
        if (resourceOpt.isEmpty()) {
            throw new BusinessException("课程资源不存在");
        }
        
        CourseResource resource = resourceOpt.get();
        resource.setStatus(1);
        courseResourceRepository.save(resource);
    }
    
    public void disableResource(Long id) {
        logger.info("禁用课程资源: {}", id);
        
        Optional<CourseResource> resourceOpt = findResourceById(id);
        if (resourceOpt.isEmpty()) {
            throw new BusinessException("课程资源不存在");
        }
        
        CourseResource resource = resourceOpt.get();
        resource.setStatus(0);
        courseResourceRepository.save(resource);
    }
    
    public List<CourseResource> batchUploadResourcesOld(List<CourseResource> resources) {
        logger.info("批量上传课程资源: {} 个", resources.size());
        
        for (CourseResource resource : resources) {
            // 验证文件路径唯一性
            if (courseResourceRepository.existsByFilePathAndDeleted(resource.getFilePath(), 0)) {
                throw new BusinessException("文件路径已存在: " + resource.getFilePath());
            }
            
            resource.setStatus(1);
            resource.setDeleted(0);
        }
        
        return courseResourceRepository.saveAll(resources);
    }
    
    public boolean isResourceAccessible(Long resourceId, Long userId) {
        Optional<CourseResource> resourceOpt = findResourceById(resourceId);
        if (resourceOpt.isEmpty()) {
            return false;
        }
        
        CourseResource resource = resourceOpt.get();
        
        // 检查资源状态
        if (resource.getStatus() != 1) {
            return false;
        }
        
        // TODO: 实现具体的权限检查逻辑
        // 例如：检查用户是否选修了该课程，是否是课程教师等
        return true;
    }
    
    public void recordResourceAccess(Long resourceId, Long userId) {
        logger.debug("记录资源访问: resourceId={}, userId={}", resourceId, userId);
        
        // TODO: 实现资源访问记录功能
        // 可以记录到ResourceAccessLog表中
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> getResourceAccessStatistics(Long resourceId) {
        // TODO: 实现资源访问统计
        logger.debug("获取资源访问统计: resourceId={}", resourceId);
        return List.of();
    }
    
    public void moveResource(Long resourceId, String newPath) {
        logger.info("移动课程资源: resourceId={}, newPath={}", resourceId, newPath);
        
        Optional<CourseResource> resourceOpt = findResourceById(resourceId);
        if (resourceOpt.isEmpty()) {
            throw new BusinessException("课程资源不存在");
        }
        
        // 检查新路径是否已存在
        if (courseResourceRepository.existsByFilePathAndDeleted(newPath, 0)) {
            throw new BusinessException("目标路径已存在");
        }
        
        CourseResource resource = resourceOpt.get();
        String oldPath = resource.getFilePath();
        resource.setFilePath(newPath);
        courseResourceRepository.save(resource);
        
        logger.info("课程资源移动成功: {} -> {}", oldPath, newPath);
    }
    
    public void copyResource(Long resourceId, Long targetCourseId) {
        logger.info("复制课程资源: resourceId={}, targetCourseId={}", resourceId, targetCourseId);
        
        Optional<CourseResource> sourceOpt = findResourceById(resourceId);
        if (sourceOpt.isEmpty()) {
            throw new BusinessException("源课程资源不存在");
        }
        
        CourseResource source = sourceOpt.get();
        CourseResource copy = new CourseResource();
        copy.setCourseId(targetCourseId);
        copy.setFileName(source.getFileName());
        copy.setFilePath(generateCopyPath(source.getFilePath()));
        copy.setFileSize(source.getFileSize());
        copy.setResourceType(source.getResourceType());
        copy.setDescription(source.getDescription() + " (副本)");
        copy.setUploadedBy(source.getUploadedBy());
        copy.setStatus(1);
        copy.setDeleted(0);
        
        courseResourceRepository.save(copy);
        logger.info("课程资源复制成功");
    }
    
    public void cleanupDeletedResources() {
        logger.info("清理已删除的课程资源");
        
        List<CourseResource> deletedResources = courseResourceRepository.findByStatusAndDeletedOrderByCreatedAtDesc(0, 1);
        
        for (CourseResource resource : deletedResources) {
            try {
                // TODO: 实现实际的文件删除逻辑
                logger.debug("清理资源文件: {}", resource.getFilePath());
            } catch (Exception e) {
                logger.error("清理资源文件失败: {}", resource.getFilePath(), e);
            }
        }
        
        // 从数据库中彻底删除
        courseResourceRepository.deleteAll(deletedResources);
        logger.info("清理已删除的课程资源完成，共处理 {} 个文件", deletedResources.size());
    }
    
    /**
     * 生成复制文件的路径
     */
    private String generateCopyPath(String originalPath) {
        int lastDotIndex = originalPath.lastIndexOf('.');
        if (lastDotIndex > 0) {
            String nameWithoutExt = originalPath.substring(0, lastDotIndex);
            String extension = originalPath.substring(lastDotIndex);
            return nameWithoutExt + "_copy" + extension;
        } else {
            return originalPath + "_copy";
        }
    }

    // ================================
    // 接口方法实现
    // ================================

    @Override
    @Transactional
    public CourseResource uploadResource(Long courseId, Long teacherId, MultipartFile file,
                                       String resourceName, String description, String resourceType) {
        // TODO: 实现文件上传逻辑
        CourseResource resource = new CourseResource();
        resource.setCourseId(courseId);
        resource.setTeacherId(teacherId);
        resource.setResourceName(resourceName != null ? resourceName : file.getOriginalFilename());
        resource.setDescription(description);
        resource.setResourceType(resourceType);
        resource.setFileName(file.getOriginalFilename());
        resource.setFileSize(file.getSize());
        resource.setStatus(1);
        resource.setDeleted(0);
        
        return courseResourceRepository.save(resource);
    }

    @Override
    @Transactional
    public CourseResource createResource(CourseResource resource) {
        if (resource.getCourseId() == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        if (resource.getStatus() == null) {
            resource.setStatus(1);
        }
        if (resource.getDeleted() == null) {
            resource.setDeleted(0);
        }
        return courseResourceRepository.save(resource);
    }

    @Override
    @Transactional
    public CourseResource updateResource(CourseResource courseResource) {
        logger.info("更新课程资源: {}", courseResource.getId());
        
        Optional<CourseResource> existingOpt = findById(courseResource.getId());
        if (existingOpt.isEmpty()) {
            throw new BusinessException("课程资源不存在");
        }
        
        CourseResource existing = existingOpt.get();
        
        // 检查文件路径是否与其他资源冲突
        if (!existing.getFilePath().equals(courseResource.getFilePath()) &&
            courseResourceRepository.existsByFilePathAndDeleted(courseResource.getFilePath(), 0)) {
            throw new BusinessException("文件路径已存在");
        }
        
        existing.setFileName(courseResource.getFileName());
        existing.setFilePath(courseResource.getFilePath());
        existing.setFileSize(courseResource.getFileSize());
        existing.setResourceType(courseResource.getResourceType());
        existing.setDescription(courseResource.getDescription());
        
        return courseResourceRepository.save(existing);
    }

    @Override
    @Transactional
    public void batchDeleteResources(List<Long> resourceIds) {
        logger.info("批量删除课程资源: {} 个", resourceIds.size());
        
        for (Long resourceId : resourceIds) {
            try {
                deleteById(resourceId);
            } catch (Exception e) {
                logger.error("删除课程资源失败: resourceId={}", resourceId, e);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseResource> findById(Long id) {
        return courseResourceRepository.findByIdAndDeleted(id, 0);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        deleteResource(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResource> findWithFilters(Long courseId, Long teacherId, String resourceType,
                                               Boolean isPublic, String keyword, Pageable pageable) {
        // TODO: 实现复杂过滤查询
        return courseResourceRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findByCourseId(Long courseId) {
        return findResourcesByCourse(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findPublicResourcesByCourse(Long courseId) {
        // TODO: 实现公开资源查询
        return courseResourceRepository.findByCourseIdAndDeletedOrderByCreatedAtDesc(courseId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findRequiredResourcesByCourse(Long courseId) {
        // TODO: 实现必读资料查询
        return courseResourceRepository.findByCourseIdAndDeletedOrderByCreatedAtDesc(courseId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findByChapter(Long courseId, String chapter) {
        // TODO: 实现按章节查询
        return courseResourceRepository.findByCourseIdAndDeletedOrderByCreatedAtDesc(courseId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findByTag(Long courseId, String tag) {
        // TODO: 实现按标签查询
        return courseResourceRepository.findByCourseIdAndDeletedOrderByCreatedAtDesc(courseId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findByResourceType(String resourceType) {
        return findResourcesByType(resourceType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findByTeacherId(Long teacherId) {
        return findResourcesByUploader(teacherId);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadResource(Long resourceId, Long userId) {
        // TODO: 实现资源下载
        recordResourceAccess(resourceId, userId);
        return new byte[0];
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] previewResource(Long resourceId, Long userId) {
        // TODO: 实现资源预览
        recordResourceAccess(resourceId, userId);
        return new byte[0];
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAccessPermission(Long resourceId, Long userId, String userRole, boolean isEnrolled) {
        return isResourceAccessible(resourceId, userId);
    }

    @Override
    @Transactional
    public void recordAccess(Long resourceId, Long userId, String accessType, String ipAddress, String userAgent) {
        recordResourceAccess(resourceId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceStatistics getStatistics(Long courseId) {
        // TODO: 实现统计信息获取
        return new ResourceStatistics();
    }

    @Override
    @Transactional
    public List<CourseResource> batchUploadResources(Long courseId, Long teacherId, List<MultipartFile> files) {
        List<CourseResource> resources = new ArrayList<>();
        for (MultipartFile file : files) {
            CourseResource resource = uploadResource(courseId, teacherId, file,
                file.getOriginalFilename(), null, "document");
            resources.add(resource);
        }
        return resources;
    }

    @Override
    @Transactional
    public List<CourseResource> copyResourcesToCourse(List<Long> resourceIds, Long targetCourseId, Long teacherId) {
        List<CourseResource> copiedResources = new ArrayList<>();
        for (Long resourceId : resourceIds) {
            copyResource(resourceId, targetCourseId);
            // TODO: 返回复制的资源
        }
        return copiedResources;
    }

    @Override
    @Transactional
    public List<CourseResource> moveResourcesToCourse(List<Long> resourceIds, Long targetCourseId) {
        List<CourseResource> movedResources = new ArrayList<>();
        for (Long resourceId : resourceIds) {
            Optional<CourseResource> resourceOpt = findById(resourceId);
            if (resourceOpt.isPresent()) {
                CourseResource resource = resourceOpt.get();
                resource.setCourseId(targetCourseId);
                courseResourceRepository.save(resource);
                movedResources.add(resource);
            }
        }
        return movedResources;
    }

    @Override
    @Transactional
    public void updateResourceOrder(List<Long> resourceIds, List<Integer> sortOrders) {
        // TODO: 实现资源排序更新
        for (int i = 0; i < resourceIds.size() && i < sortOrders.size(); i++) {
            Optional<CourseResource> resourceOpt = findById(resourceIds.get(i));
            if (resourceOpt.isPresent()) {
                CourseResource resource = resourceOpt.get();
                resource.setSortOrder(sortOrders.get(i));
                courseResourceRepository.save(resource);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findPopularResources(int limit) {
        // TODO: 实现热门资源查询
        return Collections.emptyList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findLatestResources(int limit) {
        // TODO: 实现最新资源查询
        return Collections.emptyList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findLargeFiles(Long minSizeInMB) {
        return findLargeResources(minSizeInMB * 1024 * 1024);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findExpiredResources() {
        // TODO: 实现过期资源查询
        return Collections.emptyList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findExpiringResources(int days) {
        // TODO: 实现即将过期资源查询
        return Collections.emptyList();
    }

    @Override
    @Transactional
    public int autoCleanExpiredResources() {
        // TODO: 实现自动清理过期资源
        cleanupDeletedResources();
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCourseResourceTotalSize(Long courseId) {
        // TODO: 实现课程资源总大小计算
        return 0L;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTeacherResourceTotalSize(Long teacherId) {
        // TODO: 实现教师资源总大小计算
        return 0L;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFileNameDuplicate(Long courseId, String fileName, Long excludeId) {
        // TODO: 实现文件名重复检查
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public String generateUniqueFileName(Long courseId, String originalFileName) {
        // TODO: 实现唯一文件名生成
        return originalFileName;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getSupportedFileTypes() {
        return List.of("pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt", "jpg", "png", "gif", "mp4", "mp3");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFileTypeSupported(String fileExtension) {
        return getSupportedFileTypes().contains(fileExtension.toLowerCase());
    }

    @Override
    @Transactional(readOnly = true)
    public String getFileStoragePath(Long courseId, String fileName) {
        // TODO: 实现文件存储路径生成
        return "/uploads/courses/" + courseId + "/" + fileName;
    }
}
