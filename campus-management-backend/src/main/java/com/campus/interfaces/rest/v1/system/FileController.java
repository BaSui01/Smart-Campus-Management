package com.campus.interfaces.rest.v1.system;

import com.campus.application.dto.FileUploadResult;
import com.campus.application.service.FileService;
import com.campus.domain.entity.system.FileInfo;
import com.campus.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 文件管理控制器
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Tag(name = "文件管理", description = "文件上传下载、版本管理等功能")
@Slf4j
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "上传文件", description = "支持单个文件上传")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('file:upload')")
    public ResponseEntity<ApiResponse<FileUploadResult>> uploadFile(
            @Parameter(description = "文件", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "业务类型", example = "student")
            @RequestParam(value = "businessType", required = false) String businessType,
            @Parameter(description = "业务ID", example = "123")
            @RequestParam(value = "businessId", required = false) String businessId) {
        
        try {
            FileUploadResult result = fileService.uploadFile(file, businessType, businessId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return ResponseEntity.ok(ApiResponse.error("文件上传失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "批量上传文件", description = "支持多个文件同时上传")
    @PostMapping(value = "/batch-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('file:upload')")
    public ResponseEntity<ApiResponse<List<FileUploadResult>>> batchUploadFiles(
            @Parameter(description = "文件列表", required = true)
            @RequestParam("files") List<MultipartFile> files,
            @Parameter(description = "业务类型", example = "course")
            @RequestParam(value = "businessType", required = false) String businessType,
            @Parameter(description = "业务ID", example = "456")
            @RequestParam(value = "businessId", required = false) String businessId) {
        
        try {
            List<FileUploadResult> results = fileService.batchUploadFiles(files, businessType, businessId);
            return ResponseEntity.ok(ApiResponse.success(results));
        } catch (Exception e) {
            log.error("批量文件上传失败", e);
            return ResponseEntity.ok(ApiResponse.error("批量文件上传失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "下载文件", description = "根据文件ID下载文件")
    @GetMapping("/download/{fileId}")
    @PreAuthorize("hasAuthority('file:download')")
    public void downloadFile(
            @Parameter(description = "文件ID", required = true, example = "1")
            @PathVariable Long fileId, 
            HttpServletResponse response) {
        
        try {
            fileService.downloadFile(fileId, response);
        } catch (Exception e) {
            log.error("文件下载失败: {}", fileId, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "预览文件", description = "在线预览文件内容")
    @GetMapping("/preview/{fileId}")
    @PreAuthorize("hasAuthority('file:view')")
    public void previewFile(
            @Parameter(description = "文件ID", required = true, example = "1")
            @PathVariable Long fileId, 
            HttpServletResponse response) {
        
        try {
            fileService.previewFile(fileId, response);
        } catch (Exception e) {
            log.error("文件预览失败: {}", fileId, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "删除文件", description = "根据文件ID删除文件")
    @DeleteMapping("/{fileId}")
    @PreAuthorize("hasAuthority('file:delete')")
    public ResponseEntity<ApiResponse<Boolean>> deleteFile(
            @Parameter(description = "文件ID", required = true, example = "1")
            @PathVariable Long fileId) {
        
        try {
            boolean result = fileService.deleteFile(fileId);
            if (result) {
                return ResponseEntity.ok(ApiResponse.success("文件删除成功", true));
            } else {
                return ResponseEntity.ok(ApiResponse.error("文件删除失败，文件不存在"));
            }
        } catch (Exception e) {
            log.error("文件删除失败: {}", fileId, e);
            return ResponseEntity.ok(ApiResponse.error("文件删除失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "获取文件信息", description = "根据文件ID获取详细信息")
    @GetMapping("/{fileId}")
    @PreAuthorize("hasAuthority('file:view')")
    public ResponseEntity<ApiResponse<FileInfo>> getFileInfo(
            @Parameter(description = "文件ID", required = true, example = "1")
            @PathVariable Long fileId) {
        
        try {
            FileInfo fileInfo = fileService.getFileInfo(fileId);
            if (fileInfo != null) {
                return ResponseEntity.ok(ApiResponse.success(fileInfo));
            } else {
                return ResponseEntity.ok(ApiResponse.error("文件不存在"));
            }
        } catch (Exception e) {
            log.error("获取文件信息失败: {}", fileId, e);
            return ResponseEntity.ok(ApiResponse.error("获取文件信息失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "获取业务文件列表", description = "根据业务类型和ID获取相关文件")
    @GetMapping("/business")
    @PreAuthorize("hasAuthority('file:view')")
    public ResponseEntity<ApiResponse<List<FileInfo>>> getFilesByBusiness(
            @Parameter(description = "业务类型", required = true, example = "student")
            @RequestParam String businessType,
            @Parameter(description = "业务ID", required = true, example = "123")
            @RequestParam String businessId) {
        
        try {
            List<FileInfo> files = fileService.getFilesByBusiness(businessType, businessId);
            return ResponseEntity.ok(ApiResponse.success(files));
        } catch (Exception e) {
            log.error("获取业务文件列表失败: businessType={}, businessId={}", businessType, businessId, e);
            return ResponseEntity.ok(ApiResponse.error("获取业务文件列表失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "获取文件访问链接", description = "生成临时访问链接")
    @GetMapping("/{fileId}/access-url")
    @PreAuthorize("hasAuthority('file:view')")
    public ResponseEntity<ApiResponse<String>> getFileAccessUrl(
            @Parameter(description = "文件ID", required = true, example = "1")
            @PathVariable Long fileId,
            @Parameter(description = "过期时间（分钟）", example = "60")
            @RequestParam(defaultValue = "60") Integer expireMinutes) {
        
        try {
            String url = fileService.getFileAccessUrl(fileId, expireMinutes);
            if (url != null) {
                return ResponseEntity.ok(ApiResponse.success(url));
            } else {
                return ResponseEntity.ok(ApiResponse.error("文件不存在"));
            }
        } catch (Exception e) {
            log.error("获取文件访问链接失败: {}", fileId, e);
            return ResponseEntity.ok(ApiResponse.error("获取文件访问链接失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "文件健康检查", description = "检查文件服务状态")
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("文件服务运行正常"));
    }
}
