package com.campus.interfaces.web;

import com.campus.application.service.CourseResourceService;
import com.campus.application.service.CourseService;
import com.campus.application.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 课程资源管理页面控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Controller
@RequestMapping("/admin/course-resources")
public class CourseResourceController {
    
    private static final Logger logger = LoggerFactory.getLogger(CourseResourceController.class);
    
    @Autowired
    private CourseResourceService courseResourceService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private UserService userService;
    
    // ================================
    // 页面路由
    // ================================
    
    @GetMapping
    public String resourceList(Model model) {
        try {
            logger.info("访问课程资源管理页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "课程资源管理");
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("resourceTypes", courseResourceService.getResourceTypes());
            
            return "admin/course-resources/list";
            
        } catch (Exception e) {
            return handlePageError(e, "访问课程资源管理页面", model);
        }
    }
    
    @GetMapping("/upload")
    public String uploadResource(Model model) {
        try {
            logger.info("访问资源上传页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "上传资源");
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("resourceTypes", courseResourceService.getResourceTypes());
            model.addAttribute("maxFileSize", courseResourceService.getMaxFileSize());
            model.addAttribute("allowedFileTypes", courseResourceService.getAllowedFileTypes());
            
            return "admin/course-resources/upload";
            
        } catch (Exception e) {
            return handlePageError(e, "访问资源上传页面", model);
        }
    }
    
    @GetMapping("/{resourceId}")
    public String resourceDetail(@PathVariable Long resourceId, Model model) {
        try {
            logger.info("访问资源详情页面: resourceId={}", resourceId);
            
            Object resource = courseResourceService.getResourceById(resourceId);
            if (resource == null) {
                model.addAttribute("error", "资源不存在");
                return "error/404";
            }
            
            Object accessLogs = courseResourceService.getResourceAccessLogs(resourceId, 0, 20);
            Object downloadStats = courseResourceService.getResourceDownloadStats(resourceId);
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "资源详情");
            model.addAttribute("resource", resource);
            model.addAttribute("accessLogs", accessLogs);
            model.addAttribute("downloadStats", downloadStats);
            
            return "admin/course-resources/detail";
            
        } catch (Exception e) {
            return handlePageError(e, "访问资源详情页面", model);
        }
    }
    
    @GetMapping("/{resourceId}/edit")
    public String editResource(@PathVariable Long resourceId, Model model) {
        try {
            logger.info("访问编辑资源页面: resourceId={}", resourceId);
            
            // 使用findById方法替代
            java.util.Optional<com.campus.domain.entity.CourseResource> resourceOpt = courseResourceService.findById(resourceId);
            Object resource = resourceOpt.orElse(null);
            if (resource == null) {
                model.addAttribute("error", "资源不存在");
                return "error/404";
            }
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "编辑资源");
            model.addAttribute("action", "edit");
            model.addAttribute("resource", resource);
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("resourceTypes", courseResourceService.getResourceTypes());
            
            return "admin/course-resources/form";
            
        } catch (Exception e) {
            return handlePageError(e, "访问编辑资源页面", model);
        }
    }
    
    @GetMapping("/course/{courseId}")
    public String courseResources(@PathVariable Long courseId, Model model) {
        try {
            logger.info("访问课程资源页面: courseId={}", courseId);
            
            Object course = courseService.getCourseById(courseId);
            if (course == null) {
                model.addAttribute("error", "课程不存在");
                return "error/404";
            }
            
            Object resources = courseResourceService.getResourcesByCourse(courseId, null);
            Object resourceStats = courseResourceService.getResourceStatistics(courseId);
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "课程资源");
            model.addAttribute("course", course);
            model.addAttribute("resources", resources);
            model.addAttribute("resourceStats", resourceStats);
            model.addAttribute("resourceTypes", courseResourceService.getResourceTypes());
            
            return "admin/course-resources/course-resources";
            
        } catch (Exception e) {
            return handlePageError(e, "访问课程资源页面", model);
        }
    }
    
    @GetMapping("/categories")
    public String resourceCategories(Model model) {
        try {
            logger.info("访问资源分类管理页面");
            
            Object categories = courseResourceService.getResourceCategories();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "资源分类管理");
            model.addAttribute("categories", categories);
            
            return "admin/course-resources/categories";
            
        } catch (Exception e) {
            return handlePageError(e, "访问资源分类管理页面", model);
        }
    }
    
    @GetMapping("/permissions")
    public String resourcePermissions(Model model) {
        try {
            logger.info("访问资源权限管理页面");
            
            Object resources = courseResourceService.getAllResources();
            Object roles = userService.getAllRoles();
            Object userGroups = userService.getUserGroups();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "资源权限管理");
            model.addAttribute("resources", resources);
            model.addAttribute("roles", roles);
            model.addAttribute("userGroups", userGroups);
            
            return "admin/course-resources/permissions";
            
        } catch (Exception e) {
            return handlePageError(e, "访问资源权限管理页面", model);
        }
    }
    
    @GetMapping("/storage")
    public String storageManagement(Model model) {
        try {
            logger.info("访问存储管理页面");
            
            Object storageInfo = courseResourceService.getStorageInfo();
            Object storageStats = courseResourceService.getStorageStatistics();
            Object largeFiles = courseResourceService.getLargeFiles();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "存储管理");
            model.addAttribute("storageInfo", storageInfo);
            model.addAttribute("storageStats", storageStats);
            model.addAttribute("largeFiles", largeFiles);
            
            return "admin/course-resources/storage";
            
        } catch (Exception e) {
            return handlePageError(e, "访问存储管理页面", model);
        }
    }
    
    @GetMapping("/batch-upload")
    public String batchUpload(Model model) {
        try {
            logger.info("访问批量上传页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "批量上传");
            model.addAttribute("courses", courseService.findActiveCourses());
            // TODO: CourseResourceService缺少getResourceTypes方法
            model.addAttribute("resourceTypes", courseResourceService.getSupportedFileTypes());
            model.addAttribute("maxBatchSize", courseResourceService.getMaxBatchSize());
            
            return "admin/course-resources/batch-upload";
            
        } catch (Exception e) {
            return handlePageError(e, "访问批量上传页面", model);
        }
    }
    
    @GetMapping("/statistics")
    public String resourceStatistics(Model model) {
        try {
            logger.info("访问资源统计页面");
            
            Object overallStats = courseResourceService.getOverallStatistics();
            Object typeStats = courseResourceService.getResourceTypeStatistics();
            Object usageStats = courseResourceService.getResourceUsageStatistics();
            Object downloadTrends = courseResourceService.getDownloadTrends();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "资源统计");
            model.addAttribute("overallStats", overallStats);
            model.addAttribute("typeStats", typeStats);
            model.addAttribute("usageStats", usageStats);
            model.addAttribute("downloadTrends", downloadTrends);
            
            return "admin/course-resources/statistics";
            
        } catch (Exception e) {
            return handlePageError(e, "访问资源统计页面", model);
        }
    }
    
    @GetMapping("/search")
    public String resourceSearch(Model model) {
        try {
            logger.info("访问资源搜索页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "资源搜索");
            model.addAttribute("courses", courseService.findActiveCourses());
            model.addAttribute("resourceTypes", courseResourceService.getResourceTypes());
            model.addAttribute("searchFilters", courseResourceService.getSearchFilters());
            
            return "admin/course-resources/search";
            
        } catch (Exception e) {
            return handlePageError(e, "访问资源搜索页面", model);
        }
    }
    
    @GetMapping("/recycle-bin")
    public String recycleBin(Model model) {
        try {
            logger.info("访问回收站页面");
            
            Object deletedResources = courseResourceService.getDeletedResources();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "回收站");
            model.addAttribute("deletedResources", deletedResources);
            
            return "admin/course-resources/recycle-bin";
            
        } catch (Exception e) {
            return handlePageError(e, "访问回收站页面", model);
        }
    }
    
    @GetMapping("/settings")
    public String resourceSettings(Model model) {
        try {
            logger.info("访问资源设置页面");
            
            Object resourceSettings = courseResourceService.getResourceSettings();
            Object storageSettings = courseResourceService.getStorageSettings();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "资源设置");
            model.addAttribute("resourceSettings", resourceSettings);
            model.addAttribute("storageSettings", storageSettings);
            
            return "admin/course-resources/settings";
            
        } catch (Exception e) {
            return handlePageError(e, "访问资源设置页面", model);
        }
    }
    
    @GetMapping("/preview/{resourceId}")
    public String previewResource(@PathVariable Long resourceId, Model model) {
        try {
            logger.info("访问资源预览页面: resourceId={}", resourceId);
            
            Object resource = courseResourceService.getResourceById(resourceId);
            if (resource == null) {
                model.addAttribute("error", "资源不存在");
                return "error/404";
            }
            
            // TODO: 检查用户是否有预览权限
            Long currentUserId = getCurrentUserId();
            boolean hasAccess = courseResourceService.checkResourceAccess(resourceId, currentUserId);
            if (!hasAccess) {
                model.addAttribute("error", "无权限访问该资源");
                return "error/403";
            }
            
            Object previewData = courseResourceService.previewResource(resourceId, currentUserId);
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "资源预览");
            model.addAttribute("resource", resource);
            model.addAttribute("previewData", previewData);
            
            return "admin/course-resources/preview";
            
        } catch (Exception e) {
            return handlePageError(e, "访问资源预览页面", model);
        }
    }
    
    // ================================
    // 辅助方法
    // ================================
    
    /**
     * 添加通用页面属性
     */
    private void addCommonAttributes(Model model) {
        model.addAttribute("currentModule", "course-resources");
        model.addAttribute("breadcrumb", "课程资源管理");
    }
    
    /**
     * 处理页面错误
     */
    private String handlePageError(Exception e, String operation, Model model) {
        logger.error("{}失败", operation, e);
        model.addAttribute("error", operation + "失败: " + e.getMessage());
        return "error/500";
    }
    
    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        // TODO: 实现获取当前用户ID逻辑
        return 1L;
    }
}
