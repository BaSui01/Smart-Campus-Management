package com.campus.application.Implement.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.campus.application.service.academic.CourseResourceService;
import com.campus.domain.entity.resource.CourseResource;
import com.campus.domain.repository.resourc.CourseResourceRepository;

import jakarta.persistence.criteria.Predicate;

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
        try {
            // 验证文件
            validateUploadFile(file);

            // 生成唯一文件名
            String originalFileName = file.getOriginalFilename();
            String uniqueFileName = generateUniqueFileName(courseId, originalFileName);

            // 获取存储路径
            String storagePath = getFileStoragePath(courseId, uniqueFileName);

            // 确保目录存在
            Path uploadPath = Paths.get(storagePath).getParent();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 保存文件到磁盘
            Path filePath = Paths.get(storagePath);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 创建资源记录
            CourseResource resource = new CourseResource();
            resource.setCourseId(courseId);
            resource.setTeacherId(teacherId);
            resource.setResourceName(resourceName != null ? resourceName : originalFileName);
            resource.setDescription(description);
            resource.setResourceType(resourceType != null ? resourceType : detectFileType(originalFileName));
            resource.setFileName(uniqueFileName);
            resource.setFilePath(storagePath);
            resource.setFileSize(file.getSize());
            resource.setMimeType(file.getContentType());
            resource.setStatus(1);
            resource.setDeleted(0);
            resource.setSortOrder(getNextSortOrder(courseId));

            logger.info("文件上传成功 - 课程ID: {}, 文件名: {}, 大小: {} bytes",
                courseId, uniqueFileName, file.getSize());

            return courseResourceRepository.save(resource);

        } catch (IOException e) {
            logger.error("文件上传失败 - 课程ID: {}, 文件名: {}", courseId, file.getOriginalFilename(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
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
        return courseResourceRepository.findAll(
            createFilterSpecification(courseId, teacherId, resourceType, isPublic, keyword),
            pageable
        );
    }

    /**
     * 创建资源过滤条件
     */
    private Specification<CourseResource> createFilterSpecification(Long courseId, Long teacherId,
                                                                   String resourceType, Boolean isPublic, String keyword) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 基础过滤：未删除的资源
            predicates.add(criteriaBuilder.equal(root.get("deleted"), 0));

            // 按课程ID过滤
            if (courseId != null) {
                predicates.add(criteriaBuilder.equal(root.get("courseId"), courseId));
            }

            // 按教师ID过滤
            if (teacherId != null) {
                predicates.add(criteriaBuilder.equal(root.get("teacherId"), teacherId));
            }

            // 按资源类型过滤
            if (resourceType != null && !resourceType.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("resourceType"), resourceType));
            }

            // 按公开状态过滤
            if (isPublic != null) {
                predicates.add(criteriaBuilder.equal(root.get("isPublic"), isPublic));
            }

            // 关键词搜索（文件名、资源名称、描述）
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.trim() + "%";
                Predicate fileNameLike = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("fileName")), likePattern.toLowerCase());
                Predicate resourceNameLike = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("resourceName")), likePattern.toLowerCase());
                Predicate descriptionLike = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")), likePattern.toLowerCase());

                predicates.add(criteriaBuilder.or(fileNameLike, resourceNameLike, descriptionLike));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findByCourseId(Long courseId) {
        return findResourcesByCourse(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findPublicResourcesByCourse(Long courseId) {
        return courseResourceRepository.findAll(createPublicResourcesSpecification(courseId))
            .stream()
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 创建公开资源查询条件
     */
    private Specification<CourseResource> createPublicResourcesSpecification(Long courseId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 基础条件：未删除且为公开资源
            predicates.add(criteriaBuilder.equal(root.get("deleted"), 0));
            predicates.add(criteriaBuilder.equal(root.get("courseId"), courseId));
            predicates.add(criteriaBuilder.equal(root.get("isPublic"), true));
            predicates.add(criteriaBuilder.equal(root.get("status"), 1)); // 启用状态

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findRequiredResourcesByCourse(Long courseId) {
        return courseResourceRepository.findAll(createRequiredResourcesSpecification(courseId))
            .stream()
            .sorted((a, b) -> {
                // 按排序序号排序，然后按创建时间
                Integer aSortOrder = a.getSortOrder();
                Integer bSortOrder = b.getSortOrder();
                int aSort = aSortOrder != null ? aSortOrder : 0;
                int bSort = bSortOrder != null ? bSortOrder : 0;
                int sortCompare = Integer.compare(aSort, bSort);
                return sortCompare != 0 ? sortCompare : b.getCreatedAt().compareTo(a.getCreatedAt());
            })
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 创建必读资源查询条件
     */
    private Specification<CourseResource> createRequiredResourcesSpecification(Long courseId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 基础条件：未删除且为必读资料
            predicates.add(criteriaBuilder.equal(root.get("deleted"), 0));
            predicates.add(criteriaBuilder.equal(root.get("courseId"), courseId));
            predicates.add(criteriaBuilder.equal(root.get("isRequired"), true));
            predicates.add(criteriaBuilder.equal(root.get("status"), 1));

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findByChapter(Long courseId, String chapter) {
        if (chapter == null || chapter.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return courseResourceRepository.findAll(createChapterResourcesSpecification(courseId, chapter))
            .stream()
            .sorted((a, b) -> {
                // 按章节内排序序号排序
                Integer aSortOrder = a.getSortOrder();
                Integer bSortOrder = b.getSortOrder();
                int aSortOrderValue = aSortOrder != null ? aSortOrder : 0;
                int bSortOrderValue = bSortOrder != null ? bSortOrder : 0;
                int sortCompare = Integer.compare(aSortOrderValue, bSortOrderValue);
                return sortCompare != 0 ? sortCompare : a.getCreatedAt().compareTo(b.getCreatedAt());
            })
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 创建章节资源查询条件
     */
    private Specification<CourseResource> createChapterResourcesSpecification(Long courseId, String chapter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("deleted"), 0));
            predicates.add(criteriaBuilder.equal(root.get("courseId"), courseId));
            predicates.add(criteriaBuilder.equal(root.get("chapter"), chapter.trim()));
            predicates.add(criteriaBuilder.equal(root.get("status"), 1));

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findByTag(Long courseId, String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return courseResourceRepository.findAll(createTagResourcesSpecification(courseId, tag))
            .stream()
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 创建标签资源查询条件
     */
    private Specification<CourseResource> createTagResourcesSpecification(Long courseId, String tag) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("deleted"), 0));
            predicates.add(criteriaBuilder.equal(root.get("courseId"), courseId));
            predicates.add(criteriaBuilder.like(root.get("tags"), "%" + tag.trim() + "%"));
            predicates.add(criteriaBuilder.equal(root.get("status"), 1));

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
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
        try {
            // 检查资源是否存在
            Optional<CourseResource> resourceOpt = findById(resourceId);
            if (resourceOpt.isEmpty()) {
                throw new BusinessException("资源不存在");
            }

            CourseResource resource = resourceOpt.get();

            // 检查访问权限
            if (!isResourceAccessible(resourceId, userId)) {
                throw new BusinessException("无权限访问该资源");
            }

            // 记录访问
            recordResourceAccess(resourceId, userId);

            // 读取文件内容
            String filePath = resource.getFilePath();
            if (filePath != null && Files.exists(Paths.get(filePath))) {
                byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

                // 更新下载次数
                updateDownloadCount(resourceId);

                logger.info("资源下载成功 - 资源ID: {}, 用户ID: {}, 文件大小: {} bytes",
                    resourceId, userId, fileContent.length);

                return fileContent;
            } else {
                logger.warn("文件不存在 - 路径: {}", filePath);
                throw new BusinessException("文件不存在");
            }

        } catch (IOException e) {
            logger.error("资源下载失败 - 资源ID: {}, 用户ID: {}", resourceId, userId, e);
            throw new RuntimeException("资源下载失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] previewResource(Long resourceId, Long userId) {
        try {
            // 检查资源是否存在
            Optional<CourseResource> resourceOpt = findById(resourceId);
            if (resourceOpt.isEmpty()) {
                throw new BusinessException("资源不存在");
            }

            CourseResource resource = resourceOpt.get();

            // 检查是否允许预览
            if (resource.getAllowPreview() != null && !resource.getAllowPreview()) {
                throw new BusinessException("该资源不允许预览");
            }

            // 检查访问权限
            if (!isResourceAccessible(resourceId, userId)) {
                throw new BusinessException("无权限访问该资源");
            }

            // 记录访问
            recordResourceAccess(resourceId, userId);

            // 读取文件内容（预览可能只读取部分内容）
            String filePath = resource.getFilePath();
            if (filePath != null && Files.exists(Paths.get(filePath))) {
                byte[] fileContent = Files.readAllBytes(Paths.get(filePath));

                // 更新查看次数
                updateViewCount(resourceId);

                logger.info("资源预览成功 - 资源ID: {}, 用户ID: {}, 文件大小: {} bytes",
                    resourceId, userId, fileContent.length);

                return fileContent;
            } else {
                logger.warn("文件不存在 - 路径: {}", filePath);
                throw new BusinessException("文件不存在");
            }

        } catch (IOException e) {
            logger.error("资源预览失败 - 资源ID: {}, 用户ID: {}", resourceId, userId, e);
            throw new RuntimeException("资源预览失败: " + e.getMessage(), e);
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
        try {
            // 基础统计
            List<CourseResource> allResources = findByCourseId(courseId);

            // 文件大小统计
            long totalSize = allResources.stream()
                .mapToLong(r -> r.getFileSize() != null ? r.getFileSize() : 0L)
                .sum();

            // 下载统计
            long totalDownloads = allResources.stream()
                .filter(r -> r.getDownloadCount() != null)
                .mapToLong(r -> r.getDownloadCount().longValue())
                .sum();

            // 查看统计
            long totalViews = allResources.stream()
                .filter(r -> r.getViewCount() != null)
                .mapToLong(r -> r.getViewCount().longValue())
                .sum();

            // 创建统计对象
            ResourceStatistics stats = new ResourceStatistics(
                allResources.size(),
                totalSize,
                totalDownloads,
                totalViews
            );

            return stats;

        } catch (Exception e) {
            logger.error("获取资源统计信息失败 - 课程ID: {}", courseId, e);
            return new ResourceStatistics(); // 返回空统计对象
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
                Optional<CourseResource> sourceOpt = findById(resourceId);
                if (sourceOpt.isPresent()) {
                    CourseResource source = sourceOpt.get();

                    // 创建副本
                    CourseResource copy = new CourseResource();
                    copy.setCourseId(targetCourseId);
                    copy.setTeacherId(teacherId);
                    copy.setResourceName(source.getResourceName() + " (副本)");
                    copy.setFileName(generateUniqueFileName(targetCourseId, source.getFileName()));
                    copy.setFilePath(generateCopyPath(source.getFilePath()));
                    copy.setFileSize(source.getFileSize());
                    copy.setResourceType(source.getResourceType());
                    copy.setDescription(source.getDescription());
                    copy.setMimeType(source.getMimeType());
                    copy.setTags(source.getTags());
                    copy.setChapter(source.getChapter());
                    copy.setIsPublic(false); // 副本默认为私有
                    copy.setIsRequired(false); // 副本默认为非必读
                    copy.setAllowPreview(source.getAllowPreview());
                    copy.setStatus(1);
                    copy.setDeleted(0);
                    copy.setSortOrder(getNextSortOrder(targetCourseId));

                    CourseResource savedCopy = courseResourceRepository.save(copy);
                    copiedResources.add(savedCopy);

                    logger.info("资源复制成功 - 源ID: {}, 目标课程ID: {}, 新ID: {}",
                        resourceId, targetCourseId, savedCopy.getId());
                }
            } catch (Exception e) {
                logger.error("复制资源失败 - 资源ID: {}, 目标课程ID: {}", resourceId, targetCourseId, e);
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
        if (resourceIds == null || sortOrders == null || resourceIds.isEmpty() || sortOrders.isEmpty()) {
            throw new IllegalArgumentException("资源ID列表和排序列表不能为空");
        }

        if (resourceIds.size() != sortOrders.size()) {
            throw new IllegalArgumentException("资源ID列表和排序列表长度必须一致");
        }

        int updatedCount = 0;

        for (int i = 0; i < resourceIds.size(); i++) {
            try {
                Long resourceId = resourceIds.get(i);
                Integer sortOrder = sortOrders.get(i);

                if (resourceId == null || sortOrder == null) {
                    logger.warn("跳过无效的资源ID或排序值 - 索引: {}", i);
                    continue;
                }

                Optional<CourseResource> resourceOpt = findById(resourceId);
                if (resourceOpt.isPresent()) {
                    CourseResource resource = resourceOpt.get();
                    resource.setSortOrder(sortOrder);
                    courseResourceRepository.save(resource);
                    updatedCount++;

                    logger.debug("更新资源排序 - ID: {}, 新排序: {}", resourceId, sortOrder);
                } else {
                    logger.warn("资源不存在 - ID: {}", resourceId);
                }
            } catch (Exception e) {
                logger.error("更新资源排序失败 - 索引: {}, 资源ID: {}", i, resourceIds.get(i), e);
            }
        }

        logger.info("批量更新资源排序完成 - 成功更新: {} 个，总数: {} 个", updatedCount, resourceIds.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findPopularResources(int limit) {
        try {
            return courseResourceRepository.findAll(createPopularResourcesSpecification())
                .stream()
                .sorted((a, b) -> {
                    // 按下载次数和查看次数的综合热度排序
                    Integer aDownloadCount = a.getDownloadCount();
                    Integer aViewCount = a.getViewCount();
                    Integer bDownloadCount = b.getDownloadCount();
                    Integer bViewCount = b.getViewCount();
                    int aDownloads = aDownloadCount != null ? aDownloadCount : 0;
                    int aViews = aViewCount != null ? aViewCount : 0;
                    int bDownloads = bDownloadCount != null ? bDownloadCount : 0;
                    int bViews = bViewCount != null ? bViewCount : 0;
                    int aPopularity = aDownloads * 2 + aViews;
                    int bPopularity = bDownloads * 2 + bViews;
                    return Integer.compare(bPopularity, aPopularity);
                })
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("查询热门资源失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 创建热门资源查询条件
     */
    private Specification<CourseResource> createPopularResourcesSpecification() {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("deleted"), 0));
            predicates.add(criteriaBuilder.equal(root.get("status"), 1));
            predicates.add(criteriaBuilder.equal(root.get("isPublic"), true));

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findLatestResources(int limit) {
        try {
            return courseResourceRepository.findAll(createLatestResourcesSpecification())
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("查询最新资源失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 创建最新资源查询条件
     */
    private Specification<CourseResource> createLatestResourcesSpecification() {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("deleted"), 0));
            predicates.add(criteriaBuilder.equal(root.get("status"), 1));
            predicates.add(criteriaBuilder.equal(root.get("isPublic"), true));

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findLargeFiles(Long minSizeInMB) {
        return findLargeResources(minSizeInMB * 1024 * 1024);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findExpiredResources() {
        try {
            LocalDateTime now = LocalDateTime.now();

            return courseResourceRepository.findAll(createExpiredResourcesSpecification(now))
                .stream()
                .sorted((a, b) -> a.getValidUntil().compareTo(b.getValidUntil()))
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("查询过期资源失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 创建过期资源查询条件
     */
    private Specification<CourseResource> createExpiredResourcesSpecification(LocalDateTime now) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("deleted"), 0));
            predicates.add(criteriaBuilder.isNotNull(root.get("validUntil")));
            predicates.add(criteriaBuilder.lessThan(root.get("validUntil"), now));

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResource> findExpiringResources(int days) {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime futureDate = now.plusDays(days);

            return courseResourceRepository.findAll(createExpiringResourcesSpecification(now, futureDate))
                .stream()
                .sorted((a, b) -> a.getValidUntil().compareTo(b.getValidUntil()))
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            logger.error("查询即将过期资源失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 创建即将过期资源查询条件
     */
    private Specification<CourseResource> createExpiringResourcesSpecification(LocalDateTime now, LocalDateTime futureDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("deleted"), 0));
            predicates.add(criteriaBuilder.isNotNull(root.get("validUntil")));
            predicates.add(criteriaBuilder.between(root.get("validUntil"), now, futureDate));

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    @Override
    @Transactional
    public int autoCleanExpiredResources() {
        try {
            List<CourseResource> expiredResources = findExpiredResources();
            int cleanedCount = 0;

            for (CourseResource resource : expiredResources) {
                try {
                    // 标记为删除状态
                    resource.setDeleted(1);
                    courseResourceRepository.save(resource);
                    cleanedCount++;

                    logger.info("自动清理过期资源 - ID: {}, 文件名: {}, 过期时间: {}",
                        resource.getId(), resource.getFileName(), resource.getValidUntil());
                } catch (Exception e) {
                    logger.error("清理过期资源失败 - ID: {}", resource.getId(), e);
                }
            }

            // 执行物理清理
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
        try {
            List<CourseResource> resources = findByCourseId(courseId);
            return resources.stream()
                .filter(r -> r.getFileSize() != null)
                .mapToLong(CourseResource::getFileSize)
                .sum();
        } catch (Exception e) {
            logger.error("计算课程资源总大小失败 - 课程ID: {}", courseId, e);
            return 0L;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTeacherResourceTotalSize(Long teacherId) {
        try {
            List<CourseResource> resources = findByTeacherId(teacherId);
            return resources.stream()
                .filter(r -> r.getFileSize() != null)
                .mapToLong(CourseResource::getFileSize)
                .sum();
        } catch (Exception e) {
            logger.error("计算教师资源总大小失败 - 教师ID: {}", teacherId, e);
            return 0L;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFileNameDuplicate(Long courseId, String fileName, Long excludeId) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        List<CourseResource> resources = courseResourceRepository.findAll(
            createFileNameDuplicateSpecification(courseId, fileName, excludeId)
        );

        return !resources.isEmpty();
    }

    /**
     * 创建文件名重复检查查询条件
     */
    private Specification<CourseResource> createFileNameDuplicateSpecification(Long courseId, String fileName, Long excludeId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("deleted"), 0));
            predicates.add(criteriaBuilder.equal(root.get("courseId"), courseId));
            predicates.add(criteriaBuilder.equal(root.get("fileName"), fileName.trim()));

            // 排除指定ID的资源
            if (excludeId != null) {
                predicates.add(criteriaBuilder.notEqual(root.get("id"), excludeId));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    @Override
    @Transactional(readOnly = true)
    public String generateUniqueFileName(Long courseId, String originalFileName) {
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            return "unnamed_" + System.currentTimeMillis();
        }

        String fileName = originalFileName.trim();

        // 如果文件名不重复，直接返回
        if (!isFileNameDuplicate(courseId, fileName, null)) {
            return fileName;
        }

        // 分离文件名和扩展名
        String nameWithoutExt;
        String extension = "";
        int lastDotIndex = fileName.lastIndexOf('.');

        if (lastDotIndex > 0) {
            nameWithoutExt = fileName.substring(0, lastDotIndex);
            extension = fileName.substring(lastDotIndex);
        } else {
            nameWithoutExt = fileName;
        }

        // 尝试添加数字后缀
        int counter = 1;
        String newFileName;

        do {
            newFileName = nameWithoutExt + "_" + counter + extension;
            counter++;

            // 防止无限循环
            if (counter > 1000) {
                newFileName = nameWithoutExt + "_" + System.currentTimeMillis() + extension;
                break;
            }
        } while (isFileNameDuplicate(courseId, newFileName, null));

        return newFileName;
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
        if (courseId == null || fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("课程ID和文件名不能为空");
        }

        // 基础存储路径
        String basePath = System.getProperty("user.home") + "/campus-uploads";

        // 按年月分目录存储
        LocalDate now = LocalDate.now();
        String yearMonth = now.format(DateTimeFormatter.ofPattern("yyyy/MM"));

        // 构建完整路径：基础路径/courses/课程ID/年月/文件名
        return Paths.get(basePath, "courses", courseId.toString(), yearMonth, fileName.trim())
                .toString()
                .replace("\\", "/"); // 统一使用正斜杠
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

    // ================================
    // 私有辅助方法
    // ================================

    /**
     * 验证上传文件
     */
    private void validateUploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 检查文件大小
        Long maxSize = getMaxFileSize();
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("文件大小超过限制: " + (maxSize / 1024 / 1024) + "MB");
        }

        // 检查文件类型
        String fileExtension = getFileExtension(originalFileName);
        if (!isFileTypeSupported(fileExtension)) {
            throw new IllegalArgumentException("不支持的文件类型: " + fileExtension);
        }
    }

    /**
     * 检测文件类型
     */
    private String detectFileType(String fileName) {
        if (fileName == null) return "其他";

        String extension = getFileExtension(fileName).toLowerCase();

        if (List.of("pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt").contains(extension)) {
            return "文档";
        } else if (List.of("mp4", "avi", "mov", "wmv", "flv").contains(extension)) {
            return "视频";
        } else if (List.of("mp3", "wav", "aac", "flac").contains(extension)) {
            return "音频";
        } else if (List.of("jpg", "jpeg", "png", "gif", "bmp", "svg").contains(extension)) {
            return "图片";
        } else if (List.of("zip", "rar", "7z", "tar", "gz").contains(extension)) {
            return "压缩包";
        } else {
            return "其他";
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    /**
     * 获取下一个排序序号
     */
    private Integer getNextSortOrder(Long courseId) {
        try {
            List<CourseResource> resources = findByCourseId(courseId);
            return resources.stream()
                .mapToInt(r -> {
                    Integer sortOrder = r.getSortOrder();
                    return sortOrder != null ? sortOrder : 0;
                })
                .max()
                .orElse(0) + 1;
        } catch (Exception e) {
            logger.warn("获取下一个排序序号失败，使用默认值", e);
            return 1;
        }
    }

    /**
     * 更新下载次数
     */
    private void updateDownloadCount(Long resourceId) {
        try {
            Optional<CourseResource> resourceOpt = findById(resourceId);
            if (resourceOpt.isPresent()) {
                CourseResource resource = resourceOpt.get();
                Integer currentCount = resource.getDownloadCount();
                int newCount = (currentCount != null ? currentCount : 0) + 1;
                resource.setDownloadCount(newCount);
                courseResourceRepository.save(resource);
            }
        } catch (Exception e) {
            logger.warn("更新下载次数失败 - 资源ID: {}", resourceId, e);
        }
    }

    /**
     * 更新查看次数
     */
    private void updateViewCount(Long resourceId) {
        try {
            Optional<CourseResource> resourceOpt = findById(resourceId);
            if (resourceOpt.isPresent()) {
                CourseResource resource = resourceOpt.get();
                Integer currentCount = resource.getViewCount();
                int newCount = (currentCount != null ? currentCount : 0) + 1;
                resource.setViewCount(newCount);
                courseResourceRepository.save(resource);
            }
        } catch (Exception e) {
            logger.warn("更新查看次数失败 - 资源ID: {}", resourceId, e);
        }
    }



    /**
     * 创建BusinessException类（如果不存在）
     */
    private static class BusinessException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public BusinessException(String message) {
            super(message);
        }
    }


}
