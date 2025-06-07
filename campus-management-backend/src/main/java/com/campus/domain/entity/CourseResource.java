package com.campus.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程资源实体类
 * 管理课程相关的资源文件，如课件、参考资料、视频等
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "course_resources", indexes = {
    @Index(name = "idx_course_id", columnList = "course_id"),
    @Index(name = "idx_teacher_id", columnList = "teacher_id"),
    @Index(name = "idx_resource_type", columnList = "resource_type"),
    @Index(name = "idx_upload_time", columnList = "upload_time")
})
public class CourseResource extends BaseEntity {

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    /**
     * 上传教师ID
     */
    @NotNull(message = "教师ID不能为空")
    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    /**
     * 资源名称
     */
    @NotBlank(message = "资源名称不能为空")
    @Size(max = 200, message = "资源名称长度不能超过200个字符")
    @Column(name = "resource_name", nullable = false, length = 200)
    private String resourceName;

    /**
     * 资源描述
     */
    @Size(max = 1000, message = "资源描述长度不能超过1000个字符")
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * 资源类型 (courseware/reference/video/audio/document/image/other)
     */
    @NotBlank(message = "资源类型不能为空")
    @Size(max = 20, message = "资源类型长度不能超过20个字符")
    @Column(name = "resource_type", nullable = false, length = 20)
    private String resourceType;

    /**
     * 文件路径
     */
    @NotBlank(message = "文件路径不能为空")
    @Size(max = 500, message = "文件路径长度不能超过500个字符")
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    /**
     * 文件名
     */
    @NotBlank(message = "文件名不能为空")
    @Size(max = 255, message = "文件名长度不能超过255个字符")
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    /**
     * 文件大小（字节）
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * 文件扩展名
     */
    @Size(max = 10, message = "文件扩展名长度不能超过10个字符")
    @Column(name = "file_extension", length = 10)
    private String fileExtension;

    /**
     * MIME类型
     */
    @Size(max = 100, message = "MIME类型长度不能超过100个字符")
    @Column(name = "mime_type", length = 100)
    private String mimeType;

    /**
     * 上传时间
     */
    @Column(name = "upload_time")
    private LocalDateTime uploadTime;

    /**
     * 是否公开（对学生可见）
     */
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    /**
     * 访问权限 (all/enrolled_students/specific_class/teacher_only)
     */
    @Size(max = 20, message = "访问权限长度不能超过20个字符")
    @Column(name = "access_permission", length = 20)
    private String accessPermission = "enrolled_students";

    /**
     * 下载次数
     */
    @Column(name = "download_count")
    private Integer downloadCount = 0;

    /**
     * 浏览次数
     */
    @Column(name = "view_count")
    private Integer viewCount = 0;

    /**
     * 是否允许下载
     */
    @Column(name = "allow_download", nullable = false)
    private Boolean allowDownload = true;

    /**
     * 是否允许在线预览
     */
    @Column(name = "allow_preview", nullable = false)
    private Boolean allowPreview = true;

    /**
     * 资源分类标签
     */
    @Size(max = 200, message = "标签长度不能超过200个字符")
    @Column(name = "tags", length = 200)
    private String tags;

    /**
     * 章节/单元
     */
    @Size(max = 100, message = "章节长度不能超过100个字符")
    @Column(name = "chapter", length = 100)
    private String chapter;

    /**
     * 排序顺序
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /**
     * 是否为必读资料
     */
    @Column(name = "is_required", nullable = false)
    private Boolean isRequired = false;

    /**
     * 有效期开始时间
     */
    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    /**
     * 有效期结束时间
     */
    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Column(name = "remarks", length = 500)
    private String remarks;

    // ================================
    // 关联关系
    // ================================

    /**
     * 所属课程
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    @JsonIgnore
    private Course course;

    /**
     * 上传教师
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    @JsonIgnore
    private User teacher;

    /**
     * 资源访问记录
     */
    @OneToMany(mappedBy = "resource", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ResourceAccessLog> accessLogs;

    // ================================
    // 构造函数
    // ================================

    public CourseResource() {
        super();
    }

    public CourseResource(Long courseId, Long teacherId, String resourceName, String resourceType, 
                         String filePath, String fileName) {
        this();
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.resourceName = resourceName;
        this.resourceType = resourceType;
        this.filePath = filePath;
        this.fileName = fileName;
        this.uploadTime = LocalDateTime.now();
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 获取资源类型文本
     */
    public String getResourceTypeText() {
        if (resourceType == null) return "未知";
        return switch (resourceType) {
            case "courseware" -> "课件";
            case "reference" -> "参考资料";
            case "video" -> "视频";
            case "audio" -> "音频";
            case "document" -> "文档";
            case "image" -> "图片";
            case "other" -> "其他";
            default -> resourceType;
        };
    }

    /**
     * 获取访问权限文本
     */
    public String getAccessPermissionText() {
        if (accessPermission == null) return "未知";
        return switch (accessPermission) {
            case "all" -> "所有人";
            case "enrolled_students" -> "选课学生";
            case "specific_class" -> "指定班级";
            case "teacher_only" -> "仅教师";
            default -> accessPermission;
        };
    }

    /**
     * 获取文件大小文本
     */
    public String getFileSizeText() {
        if (fileSize == null) return "未知";
        
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else if (fileSize < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", fileSize / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * 检查是否在有效期内
     */
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        
        if (validFrom != null && now.isBefore(validFrom)) {
            return false;
        }
        
        if (validUntil != null && now.isAfter(validUntil)) {
            return false;
        }
        
        return true;
    }

    /**
     * 检查用户是否有访问权限
     */
    public boolean hasAccessPermission(String userRole, boolean isEnrolled) {
        if (!isPublic || !isEnabled()) {
            return false;
        }
        
        return switch (accessPermission) {
            case "all" -> true;
            case "enrolled_students" -> isEnrolled || "TEACHER".equals(userRole) || "ADMIN".equals(userRole);
            case "specific_class" -> isEnrolled || "TEACHER".equals(userRole) || "ADMIN".equals(userRole);
            case "teacher_only" -> "TEACHER".equals(userRole) || "ADMIN".equals(userRole);
            default -> false;
        };
    }

    /**
     * 增加下载次数
     */
    public void incrementDownloadCount() {
        if (downloadCount == null) {
            downloadCount = 0;
        }
        downloadCount++;
    }

    /**
     * 增加浏览次数
     */
    public void incrementViewCount() {
        if (viewCount == null) {
            viewCount = 0;
        }
        viewCount++;
    }

    /**
     * 检查是否可以预览
     */
    public boolean canPreview() {
        if (!allowPreview) return false;
        
        // 根据文件类型判断是否支持预览
        if (fileExtension == null) return false;
        
        String ext = fileExtension.toLowerCase();
        return switch (ext) {
            case "pdf", "txt", "md" -> true;
            case "jpg", "jpeg", "png", "gif", "bmp" -> true;
            case "mp4", "avi", "mov", "wmv" -> true;
            case "mp3", "wav", "ogg" -> true;
            case "doc", "docx", "xls", "xlsx", "ppt", "pptx" -> true;
            default -> false;
        };
    }

    /**
     * 获取课程名称
     */
    public String getCourseName() {
        return course != null ? course.getCourseName() : null;
    }

    /**
     * 获取教师姓名
     */
    public String getTeacherName() {
        return teacher != null ? teacher.getRealName() : null;
    }

    /**
     * 获取标签数组
     */
    public String[] getTagArray() {
        if (tags == null || tags.trim().isEmpty()) {
            return new String[0];
        }
        return tags.split(",");
    }

    /**
     * 设置标签数组
     */
    public void setTagArray(String[] tagArray) {
        if (tagArray == null || tagArray.length == 0) {
            this.tags = "";
        } else {
            this.tags = String.join(",", tagArray);
        }
    }

    /**
     * 验证资源数据
     */
    @PrePersist
    @PreUpdate
    public void validateResource() {
        if (validUntil != null && validFrom != null && validUntil.isBefore(validFrom)) {
            throw new IllegalArgumentException("有效期结束时间不能早于开始时间");
        }
        
        if (fileSize != null && fileSize < 0) {
            throw new IllegalArgumentException("文件大小不能为负数");
        }
        
        if (downloadCount != null && downloadCount < 0) {
            throw new IllegalArgumentException("下载次数不能为负数");
        }
        
        if (viewCount != null && viewCount < 0) {
            throw new IllegalArgumentException("浏览次数不能为负数");
        }
    }

    // ================================
    // Getter and Setter methods
    // ================================

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getAccessPermission() {
        return accessPermission;
    }

    public void setAccessPermission(String accessPermission) {
        this.accessPermission = accessPermission;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Boolean getAllowDownload() {
        return allowDownload;
    }

    public void setAllowDownload(Boolean allowDownload) {
        this.allowDownload = allowDownload;
    }

    public Boolean getAllowPreview() {
        return allowPreview;
    }

    public void setAllowPreview(Boolean allowPreview) {
        this.allowPreview = allowPreview;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public List<ResourceAccessLog> getAccessLogs() {
        return accessLogs;
    }

    public void setAccessLogs(List<ResourceAccessLog> accessLogs) {
        this.accessLogs = accessLogs;
    }
}
