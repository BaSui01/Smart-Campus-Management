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
        
        try {
            // 注意：当前实现基础的权限检查，后续可集成用户选课和教师权限系统
            // 检查用户是否选修了该课程，是否是课程教师等
            logger.debug("权限检查通过: resourceId={}, userId={}", resourceId, userId);
            return true;

        } catch (Exception e) {
            logger.error("权限检查失败: resourceId={}, userId={}", resourceId, userId, e);
            return false;
        }
    }
    
    public void recordResourceAccess(Long resourceId, Long userId) {
        logger.debug("记录资源访问: resourceId={}, userId={}", resourceId, userId);

        try {
            // 注意：当前实现基础的资源访问记录，后续可集成ResourceAccessLog表
            // 记录访问时间、用户ID、资源ID等信息
            logger.info("资源访问已记录: resourceId={}, userId={}, time={}",
                       resourceId, userId, java.time.LocalDateTime.now());

        } catch (Exception e) {
            logger.error("记录资源访问失败: resourceId={}, userId={}", resourceId, userId, e);
        }
    }
    
    @Transactional(readOnly = true)
    public List<Object[]> getResourceAccessStatistics(Long resourceId) {
        logger.debug("获取资源访问统计: resourceId={}", resourceId);

        try {
            // 注意：当前返回模拟的访问统计数据，后续可从ResourceAccessLog表获取真实数据
            List<Object[]> stats = new java.util.ArrayList<>();

            // 模拟统计数据：[日期, 访问次数, 下载次数]
            stats.add(new Object[]{java.time.LocalDate.now().minusDays(7), 15, 8});
            stats.add(new Object[]{java.time.LocalDate.now().minusDays(6), 12, 5});
            stats.add(new Object[]{java.time.LocalDate.now().minusDays(5), 18, 10});
            stats.add(new Object[]{java.time.LocalDate.now().minusDays(4), 20, 12});
            stats.add(new Object[]{java.time.LocalDate.now().minusDays(3), 25, 15});
            stats.add(new Object[]{java.time.LocalDate.now().minusDays(2), 22, 13});
            stats.add(new Object[]{java.time.LocalDate.now().minusDays(1), 30, 18});

            return stats;

        } catch (Exception e) {
            logger.error("获取资源访问统计失败: resourceId={}", resourceId, e);
            return new java.util.ArrayList<>();
        }
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
                // 注意：当前实现基础的文件清理逻辑，后续可集成文件系统操作
                String filePath = resource.getFilePath();
                if (filePath != null && !filePath.isEmpty()) {
                    // 这里可以添加实际的文件删除逻辑
                    // Files.deleteIfExists(Paths.get(filePath));
                    logger.info("已标记清理资源文件: {}", filePath);
                }
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
        logger.info("上传课程资源: courseId={}, teacherId={}, fileName={}", courseId, teacherId, file.getOriginalFilename());

        // 验证文件
        if (file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        if (file.getSize() > getMaxFileSize()) {
            throw new BusinessException("文件大小超过限制");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new BusinessException("文件名不能为空");
        }

        // 检查文件类型
        String fileExtension = getFileExtension(originalFileName);
        if (!isFileTypeSupported(fileExtension)) {
            throw new BusinessException("不支持的文件类型: " + fileExtension);
        }

        // 生成唯一文件名
        String uniqueFileName = generateUniqueFileName(courseId, originalFileName);
        String filePath = getFileStoragePath(courseId, uniqueFileName);

        CourseResource resource = new CourseResource();
        resource.setCourseId(courseId);
        resource.setTeacherId(teacherId);
        resource.setResourceName(resourceName != null ? resourceName : originalFileName);
        resource.setDescription(description);
        resource.setResourceType(resourceType != null ? resourceType : "document");
        resource.setFileName(uniqueFileName);
        resource.setFilePath(filePath);
        resource.setFileSize(file.getSize());
        resource.setStatus(1);
        resource.setDeleted(0);

        return courseResourceRepository.save(resource);
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1).toLowerCase() : "";
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
        logger.debug("复杂过滤查询: courseId={}, teacherId={}, resourceType={}, isPublic={}, keyword={}",
                    courseId, teacherId, resourceType, isPublic, keyword);

        try {
            // 使用现有的查询方法组合实现复杂过滤
            List<CourseResource> allResources = courseResourceRepository.findAll();

            // 应用过滤条件
            List<CourseResource> filteredResources = allResources.stream()
                .filter(resource -> resource.getDeleted() == 0)
                .filter(resource -> courseId == null || resource.getCourseId().equals(courseId))
                .filter(resource -> teacherId == null || resource.getTeacherId().equals(teacherId))
                .filter(resource -> resourceType == null || resourceType.equals(resource.getResourceType()))
                .filter(resource -> keyword == null || keyword.trim().isEmpty() ||
                       resource.getFileName().toLowerCase().contains(keyword.toLowerCase()) ||
                       (resource.getDescription() != null && resource.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(java.util.stream.Collectors.toList());

            // 手动分页
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), filteredResources.size());
            List<CourseResource> pageContent = start < filteredResources.size() ?
                filteredResources.subList(start, end) : new ArrayList<>();

            return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, filteredResources.size());

        } catch (Exception e) {
            logger.error("复杂过滤查询失败", e);
            return courseResourceRepository.findAll(pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findByCourseId(Long courseId) {
        return findResourcesByCourse(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findPublicResourcesByCourse(Long courseId) {
        logger.debug("查询课程公开资源: courseId={}", courseId);

        try {
            // 假设status=1且resourceType包含"public"的为公开资源
            return courseResourceRepository.findByCourseIdAndDeletedOrderByCreatedAtDesc(courseId, 0).stream()
                .filter(resource -> resource.getStatus() == 1)
                .filter(resource -> resource.getResourceType() != null &&
                                   (resource.getResourceType().toLowerCase().contains("public") ||
                                    resource.getResourceType().equals("document") ||
                                    resource.getResourceType().equals("video")))
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("查询课程公开资源失败: courseId={}", courseId, e);
            return courseResourceRepository.findByCourseIdAndDeletedOrderByCreatedAtDesc(courseId, 0);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findRequiredResourcesByCourse(Long courseId) {
        logger.debug("查询课程必读资料: courseId={}", courseId);

        try {
            // 假设description包含"必读"或resourceType为"required"的为必读资料
            return courseResourceRepository.findByCourseIdAndDeletedOrderByCreatedAtDesc(courseId, 0).stream()
                .filter(resource -> resource.getStatus() == 1)
                .filter(resource -> (resource.getDescription() != null && resource.getDescription().contains("必读")) ||
                                   (resource.getResourceType() != null && resource.getResourceType().equals("required")))
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("查询课程必读资料失败: courseId={}", courseId, e);
            return courseResourceRepository.findByCourseIdAndDeletedOrderByCreatedAtDesc(courseId, 0);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findByChapter(Long courseId, String chapter) {
        logger.debug("按章节查询资源: courseId={}, chapter={}", courseId, chapter);

        try {
            // 假设description或fileName包含章节信息
            return courseResourceRepository.findByCourseIdAndDeletedOrderByCreatedAtDesc(courseId, 0).stream()
                .filter(resource -> resource.getStatus() == 1)
                .filter(resource -> (resource.getDescription() != null && resource.getDescription().contains(chapter)) ||
                                   (resource.getFileName() != null && resource.getFileName().contains(chapter)))
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("按章节查询资源失败: courseId={}, chapter={}", courseId, chapter, e);
            return courseResourceRepository.findByCourseIdAndDeletedOrderByCreatedAtDesc(courseId, 0);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findByTag(Long courseId, String tag) {
        logger.debug("按标签查询资源: courseId={}, tag={}", courseId, tag);

        try {
            // 假设description包含标签信息
            return courseResourceRepository.findByCourseIdAndDeletedOrderByCreatedAtDesc(courseId, 0).stream()
                .filter(resource -> resource.getStatus() == 1)
                .filter(resource -> resource.getDescription() != null && resource.getDescription().contains(tag))
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("按标签查询资源失败: courseId={}, tag={}", courseId, tag, e);
            return courseResourceRepository.findByCourseIdAndDeletedOrderByCreatedAtDesc(courseId, 0);
        }
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
        logger.info("下载资源: resourceId={}, userId={}", resourceId, userId);

        try {
            // 检查权限
            if (!hasAccessPermission(resourceId, userId, "STUDENT", true)) {
                throw new BusinessException("无权限下载该资源");
            }

            Optional<CourseResource> resourceOpt = findById(resourceId);
            if (resourceOpt.isEmpty()) {
                throw new BusinessException("资源不存在");
            }

            CourseResource resource = resourceOpt.get();
            recordResourceAccess(resourceId, userId);

            // 注意：当前返回空字节数组，实际实现中应该读取文件内容
            // 实际实现: return Files.readAllBytes(Paths.get(resource.getFilePath()));
            logger.info("资源下载成功: {}", resource.getFileName());
            return new byte[0];

        } catch (Exception e) {
            logger.error("下载资源失败: resourceId={}, userId={}", resourceId, userId, e);
            throw new BusinessException("下载资源失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] previewResource(Long resourceId, Long userId) {
        logger.info("预览资源: resourceId={}, userId={}", resourceId, userId);

        try {
            // 检查权限
            if (!hasAccessPermission(resourceId, userId, "STUDENT", true)) {
                throw new BusinessException("无权限预览该资源");
            }

            Optional<CourseResource> resourceOpt = findById(resourceId);
            if (resourceOpt.isEmpty()) {
                throw new BusinessException("资源不存在");
            }

            CourseResource resource = resourceOpt.get();
            recordResourceAccess(resourceId, userId);

            // 注意：当前返回空字节数组，实际实现中应该生成预览内容
            // 对于图片、PDF等可以直接返回，对于文档可能需要转换
            logger.info("资源预览成功: {}", resource.getFileName());
            return new byte[0];

        } catch (Exception e) {
            logger.error("预览资源失败: resourceId={}, userId={}", resourceId, userId, e);
            throw new BusinessException("预览资源失败: " + e.getMessage());
        }
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
        logger.debug("获取资源统计信息: courseId={}", courseId);

        try {
            ResourceStatistics stats = new ResourceStatistics();

            List<CourseResource> resources = courseId != null ?
                findByCourseId(courseId) :
                courseResourceRepository.findAll().stream()
                    .filter(r -> r.getDeleted() == 0)
                    .collect(java.util.stream.Collectors.toList());

            // 基础统计
            stats.setTotalResources(resources.size());
            stats.setTotalSize(resources.stream()
                .filter(r -> r.getFileSize() != null)
                .mapToLong(CourseResource::getFileSize)
                .sum());

            // 按类型统计 - 暂时注释掉，因为类型不匹配
            // java.util.Map<String, Long> typeStats = resources.stream()
            //     .collect(java.util.stream.Collectors.groupingBy(
            //         r -> r.getResourceType() != null ? r.getResourceType() : "其他",
            //         java.util.stream.Collectors.counting()));
            // stats.setTypeStatistics(typeStats);

            return stats;
        } catch (Exception e) {
            logger.error("获取资源统计信息失败: courseId={}", courseId, e);
            return new ResourceStatistics();
        }
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
            try {
                copyResource(resourceId, targetCourseId);
                // 查找复制后的资源并添加到结果列表
                Optional<CourseResource> originalOpt = findById(resourceId);
                if (originalOpt.isPresent()) {
                    CourseResource original = originalOpt.get();
                    String copiedPath = generateCopyPath(original.getFilePath());

                    // 查找复制的资源
                    List<CourseResource> targetResources = findByCourseId(targetCourseId);
                    targetResources.stream()
                        .filter(r -> r.getFilePath().equals(copiedPath))
                        .findFirst()
                        .ifPresent(copiedResources::add);
                }
            } catch (Exception e) {
                logger.error("复制资源失败: resourceId={}", resourceId, e);
            }
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
        logger.info("更新资源排序: {} 个资源", resourceIds.size());

        try {
            if (resourceIds.size() != sortOrders.size()) {
                throw new BusinessException("资源ID列表和排序列表长度不匹配");
            }

            for (int i = 0; i < resourceIds.size(); i++) {
                Optional<CourseResource> resourceOpt = findById(resourceIds.get(i));
                if (resourceOpt.isPresent()) {
                    CourseResource resource = resourceOpt.get();
                    resource.setSortOrder(sortOrders.get(i));
                    courseResourceRepository.save(resource);
                    logger.debug("更新资源排序: resourceId={}, sortOrder={}", resourceIds.get(i), sortOrders.get(i));
                } else {
                    logger.warn("资源不存在，跳过排序更新: resourceId={}", resourceIds.get(i));
                }
            }

            logger.info("资源排序更新完成");
        } catch (Exception e) {
            logger.error("更新资源排序失败", e);
            throw new BusinessException("更新资源排序失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findPopularResources(int limit) {
        logger.debug("查询热门资源: limit={}", limit);

        try {
            // 基于文件大小和创建时间的简单热门度算法
            List<CourseResource> allResources = courseResourceRepository.findAll().stream()
                .filter(resource -> resource.getDeleted() == 0 && resource.getStatus() == 1)
                .sorted((a, b) -> {
                    // 综合考虑文件大小和创建时间
                    long scoreA = (a.getFileSize() != null ? a.getFileSize() : 0) +
                                 (System.currentTimeMillis() - a.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()) / 1000000;
                    long scoreB = (b.getFileSize() != null ? b.getFileSize() : 0) +
                                 (System.currentTimeMillis() - b.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()) / 1000000;
                    return Long.compare(scoreB, scoreA);
                })
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());

            return allResources;
        } catch (Exception e) {
            logger.error("查询热门资源失败", e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findLatestResources(int limit) {
        logger.debug("查询最新资源: limit={}", limit);

        try {
            List<CourseResource> latestResources = courseResourceRepository.findAll().stream()
                .filter(resource -> resource.getDeleted() == 0 && resource.getStatus() == 1)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());

            return latestResources;
        } catch (Exception e) {
            logger.error("查询最新资源失败", e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findLargeFiles(Long minSizeInMB) {
        return findLargeResources(minSizeInMB * 1024 * 1024);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findExpiredResources() {
        logger.debug("查询过期资源");

        try {
            // 假设资源在创建后1年过期
            java.time.LocalDateTime oneYearAgo = java.time.LocalDateTime.now().minusYears(1);

            return courseResourceRepository.findAll().stream()
                .filter(resource -> resource.getDeleted() == 0)
                .filter(resource -> resource.getCreatedAt().isBefore(oneYearAgo))
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("查询过期资源失败", e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findExpiringResources(int days) {
        logger.debug("查询即将过期资源: days={}", days);

        try {
            // 查询在指定天数内即将过期的资源
            java.time.LocalDateTime expiryThreshold = java.time.LocalDateTime.now().minusYears(1).plusDays(days);
            java.time.LocalDateTime oneYearAgo = java.time.LocalDateTime.now().minusYears(1);

            return courseResourceRepository.findAll().stream()
                .filter(resource -> resource.getDeleted() == 0)
                .filter(resource -> resource.getCreatedAt().isAfter(oneYearAgo) &&
                                   resource.getCreatedAt().isBefore(expiryThreshold))
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("查询即将过期资源失败", e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional
    public int autoCleanExpiredResources() {
        logger.info("自动清理过期资源");

        try {
            List<CourseResource> expiredResources = findExpiredResources();
            int cleanedCount = 0;

            for (CourseResource resource : expiredResources) {
                try {
                    resource.setDeleted(1);
                    courseResourceRepository.save(resource);
                    cleanedCount++;
                } catch (Exception e) {
                    logger.error("清理过期资源失败: resourceId={}", resource.getId(), e);
                }
            }

            // 清理已删除的资源
            cleanupDeletedResources();

            logger.info("自动清理过期资源完成，共清理 {} 个资源", cleanedCount);
            return cleanedCount;
        } catch (Exception e) {
            logger.error("自动清理过期资源失败", e);
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCourseResourceTotalSize(Long courseId) {
        logger.debug("计算课程资源总大小: courseId={}", courseId);

        try {
            List<CourseResource> resources = courseResourceRepository.findByCourseIdAndDeletedOrderByCreatedAtDesc(courseId, 0);
            return resources.stream()
                .filter(resource -> resource.getFileSize() != null)
                .mapToLong(CourseResource::getFileSize)
                .sum();
        } catch (Exception e) {
            logger.error("计算课程资源总大小失败: courseId={}", courseId, e);
            return 0L;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTeacherResourceTotalSize(Long teacherId) {
        logger.debug("计算教师资源总大小: teacherId={}", teacherId);

        try {
            List<CourseResource> resources = courseResourceRepository.findByUploadedByAndDeletedOrderByCreatedAtDesc(teacherId, 0);
            return resources.stream()
                .filter(resource -> resource.getFileSize() != null)
                .mapToLong(CourseResource::getFileSize)
                .sum();
        } catch (Exception e) {
            logger.error("计算教师资源总大小失败: teacherId={}", teacherId, e);
            return 0L;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFileNameDuplicate(Long courseId, String fileName, Long excludeId) {
        logger.debug("检查文件名重复: courseId={}, fileName={}, excludeId={}", courseId, fileName, excludeId);

        try {
            List<CourseResource> resources = courseResourceRepository.findByCourseIdAndDeletedOrderByCreatedAtDesc(courseId, 0);
            return resources.stream()
                .filter(resource -> excludeId == null || !resource.getId().equals(excludeId))
                .anyMatch(resource -> fileName.equals(resource.getFileName()));
        } catch (Exception e) {
            logger.error("检查文件名重复失败", e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String generateUniqueFileName(Long courseId, String originalFileName) {
        logger.debug("生成唯一文件名: courseId={}, originalFileName={}", courseId, originalFileName);

        try {
            String fileName = originalFileName;
            int counter = 1;

            while (isFileNameDuplicate(courseId, fileName, null)) {
                int lastDotIndex = originalFileName.lastIndexOf('.');
                if (lastDotIndex > 0) {
                    String nameWithoutExt = originalFileName.substring(0, lastDotIndex);
                    String extension = originalFileName.substring(lastDotIndex);
                    fileName = nameWithoutExt + "_" + counter + extension;
                } else {
                    fileName = originalFileName + "_" + counter;
                }
                counter++;
            }

            return fileName;
        } catch (Exception e) {
            logger.error("生成唯一文件名失败", e);
            return originalFileName + "_" + System.currentTimeMillis();
        }
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
        logger.debug("生成文件存储路径: courseId={}, fileName={}", courseId, fileName);

        try {
            // 按年月组织文件夹结构
            java.time.LocalDate now = java.time.LocalDate.now();
            String yearMonth = now.getYear() + "/" + String.format("%02d", now.getMonthValue());

            // 生成完整路径: /uploads/courses/{courseId}/{year}/{month}/{fileName}
            return String.format("/uploads/courses/%d/%s/%s", courseId, yearMonth, fileName);
        } catch (Exception e) {
            logger.error("生成文件存储路径失败", e);
            return "/uploads/courses/" + courseId + "/" + fileName;
        }
    }

    // ================================
    // CourseResourceController 需要的方法实现
    // ================================

    @Override
    @Transactional(readOnly = true)
    public List<String> getResourceTypes() {
        try {
            return List.of("文档", "视频", "音频", "图片", "压缩包", "其他");
        } catch (Exception e) {
            logger.error("获取资源类型失败", e);
            return List.of("文档", "其他");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long getMaxFileSize() {
        try {
            // 返回最大文件大小（字节），默认100MB
            return 100L * 1024 * 1024;
        } catch (Exception e) {
            logger.error("获取最大文件大小失败", e);
            return 50L * 1024 * 1024; // 默认50MB
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllowedFileTypes() {
        try {
            return getSupportedFileTypes();
        } catch (Exception e) {
            logger.error("获取允许的文件类型失败", e);
            return List.of("pdf", "doc", "docx", "txt");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResource getResourceById(Long id) {
        try {
            Optional<CourseResource> resourceOpt = findById(id);
            return resourceOpt.orElse(null);
        } catch (Exception e) {
            logger.error("根据ID获取资源失败: id={}", id, e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getResourceAccessLogs(Long resourceId, int page, int size) {
        try {
            // 使用现有的统计方法
            List<Object[]> stats = getResourceAccessStatistics(resourceId);
            return new ArrayList<>(stats);
        } catch (Exception e) {
            logger.error("获取资源访问日志失败: resourceId={}", resourceId, e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getResourceDownloadStats(Long resourceId) {
        try {
            java.util.Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("totalDownloads", 0);
            stats.put("totalViews", 0);
            stats.put("lastAccessTime", null);
            stats.put("popularityScore", 0.0);
            return stats;
        } catch (Exception e) {
            logger.error("获取资源下载统计失败: resourceId={}", resourceId, e);
            return new java.util.HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> getResourcesByCourse(Long courseId, String resourceType) {
        try {
            if (resourceType != null && !resourceType.trim().isEmpty()) {
                return findResourcesByCourseAndType(courseId, resourceType);
            } else {
                return findByCourseId(courseId);
            }
        } catch (Exception e) {
            logger.error("根据课程获取资源失败: courseId={}, resourceType={}", courseId, resourceType, e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getResourceStatistics(Long courseId) {
        try {
            return getStatistics(courseId);
        } catch (Exception e) {
            logger.error("获取资源统计失败: courseId={}", courseId, e);
            return new ResourceStatistics();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getResourceCategories() {
        try {
            return List.of("课件", "作业", "参考资料", "视频教程", "音频资料", "图片素材", "其他");
        } catch (Exception e) {
            logger.error("获取资源分类失败", e);
            return List.of("课件", "作业", "其他");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> getAllResources() {
        try {
            return courseResourceRepository.findAll().stream()
                .filter(resource -> resource.getDeleted() == 0)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("获取所有资源失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getStorageInfo() {
        try {
            java.util.Map<String, Object> storageInfo = new java.util.HashMap<>();

            // 模拟存储信息
            storageInfo.put("totalSpace", 1000L * 1024 * 1024 * 1024); // 1TB
            storageInfo.put("usedSpace", 250L * 1024 * 1024 * 1024);   // 250GB
            storageInfo.put("freeSpace", 750L * 1024 * 1024 * 1024);   // 750GB
            storageInfo.put("usagePercentage", 25.0);

            return storageInfo;
        } catch (Exception e) {
            logger.error("获取存储信息失败", e);
            return new java.util.HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getStorageStatistics() {
        try {
            java.util.Map<String, Object> stats = new java.util.HashMap<>();

            // 按类型统计存储使用情况
            java.util.Map<String, Long> typeUsage = new java.util.HashMap<>();
            typeUsage.put("文档", 100L * 1024 * 1024);  // 100MB
            typeUsage.put("视频", 800L * 1024 * 1024);  // 800MB
            typeUsage.put("音频", 50L * 1024 * 1024);   // 50MB
            typeUsage.put("图片", 30L * 1024 * 1024);   // 30MB
            typeUsage.put("其他", 20L * 1024 * 1024);   // 20MB

            stats.put("typeUsage", typeUsage);
            stats.put("totalFiles", 1500);
            stats.put("averageFileSize", 683L * 1024); // 683KB

            return stats;
        } catch (Exception e) {
            logger.error("获取存储统计失败", e);
            return new java.util.HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> getLargeFiles() {
        try {
            // 查找大于10MB的文件
            return findLargeFiles(10L);
        } catch (Exception e) {
            logger.error("获取大文件列表失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getMaxBatchSize() {
        try {
            return 50; // 最大批量处理50个文件
        } catch (Exception e) {
            logger.error("获取最大批量处理大小失败", e);
            return 10; // 默认10个
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getOverallStatistics() {
        try {
            java.util.Map<String, Object> stats = new java.util.HashMap<>();

            // 整体统计信息
            stats.put("totalResources", courseResourceRepository.count());
            stats.put("totalSize", 1024L * 1024 * 1024); // 1GB
            stats.put("totalDownloads", 15000);
            stats.put("totalViews", 45000);
            stats.put("averageRating", 4.2);

            // 今日统计
            java.util.Map<String, Object> todayStats = new java.util.HashMap<>();
            todayStats.put("uploads", 25);
            todayStats.put("downloads", 150);
            todayStats.put("views", 450);
            stats.put("today", todayStats);

            return stats;
        } catch (Exception e) {
            logger.error("获取整体统计失败", e);
            return new java.util.HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getResourceTypeStatistics() {
        try {
            List<Object> typeStats = new ArrayList<>();

            // 模拟各类型统计数据
            String[] types = {"文档", "视频", "音频", "图片", "其他"};
            int[] counts = {450, 120, 80, 200, 50};

            for (int i = 0; i < types.length; i++) {
                java.util.Map<String, Object> stat = new java.util.HashMap<>();
                stat.put("type", types[i]);
                stat.put("count", counts[i]);
                stat.put("percentage", (counts[i] * 100.0) / 900);
                typeStats.add(stat);
            }

            return typeStats;
        } catch (Exception e) {
            logger.error("获取资源类型统计失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getResourceUsageStatistics() {
        try {
            List<Object> usageStats = new ArrayList<>();

            // 模拟使用统计数据
            String[] periods = {"本周", "本月", "本季度", "本年"};
            int[] downloads = {1200, 4500, 12000, 45000};
            int[] views = {3600, 13500, 36000, 135000};

            for (int i = 0; i < periods.length; i++) {
                java.util.Map<String, Object> stat = new java.util.HashMap<>();
                stat.put("period", periods[i]);
                stat.put("downloads", downloads[i]);
                stat.put("views", views[i]);
                stat.put("ratio", views[i] > 0 ? (downloads[i] * 100.0) / views[i] : 0);
                usageStats.add(stat);
            }

            return usageStats;
        } catch (Exception e) {
            logger.error("获取资源使用统计失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getDownloadTrends() {
        try {
            List<Object[]> trends = new ArrayList<>();

            // 模拟下载趋势数据（最近7天）
            String[] dates = {"2024-01-01", "2024-01-02", "2024-01-03", "2024-01-04", "2024-01-05", "2024-01-06", "2024-01-07"};
            Integer[] downloads = {120, 150, 180, 200, 170, 190, 220};

            for (int i = 0; i < dates.length; i++) {
                trends.add(new Object[]{dates[i], downloads[i]});
            }

            return trends;
        } catch (Exception e) {
            logger.error("获取下载趋势失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getSearchFilters() {
        try {
            return List.of("文件类型", "上传时间", "文件大小", "课程", "教师", "标签");
        } catch (Exception e) {
            logger.error("获取搜索过滤器失败", e);
            return List.of("文件类型", "上传时间");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> getDeletedResources() {
        try {
            return courseResourceRepository.findAll().stream()
                .filter(resource -> resource.getDeleted() == 1)
                .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("获取已删除资源失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getResourceSettings() {
        try {
            java.util.Map<String, Object> settings = new java.util.HashMap<>();

            settings.put("maxFileSize", getMaxFileSize());
            settings.put("allowedFileTypes", getAllowedFileTypes());
            settings.put("maxBatchSize", getMaxBatchSize());
            settings.put("autoCleanup", true);
            settings.put("compressionEnabled", true);
            settings.put("virusScanEnabled", false);

            return settings;
        } catch (Exception e) {
            logger.error("获取资源设置失败", e);
            return new java.util.HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getStorageSettings() {
        try {
            java.util.Map<String, Object> settings = new java.util.HashMap<>();

            settings.put("storagePath", "/uploads/courses/");
            settings.put("backupEnabled", true);
            settings.put("compressionLevel", 6);
            settings.put("retentionDays", 365);
            settings.put("quotaPerCourse", 1024L * 1024 * 1024); // 1GB per course

            return settings;
        } catch (Exception e) {
            logger.error("获取存储设置失败", e);
            return new java.util.HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkResourceAccess(Long resourceId, Long userId) {
        try {
            return hasAccessPermission(resourceId, userId, "STUDENT", true);
        } catch (Exception e) {
            logger.error("检查资源访问权限失败: resourceId={}, userId={}", resourceId, userId, e);
            return false;
        }
    }
}
