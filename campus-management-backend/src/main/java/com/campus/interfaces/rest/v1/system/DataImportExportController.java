package com.campus.interfaces.rest.v1.system;

import com.campus.application.dto.ImportResult;
import com.campus.application.service.DataImportExportService;
import com.campus.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 数据导入导出控制器
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Tag(name = "数据导入导出", description = "Excel数据导入导出功能")
@Slf4j
@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
public class DataImportExportController {

    private final DataImportExportService dataImportExportService;

    @Operation(summary = "导入学生数据", description = "通过Excel文件批量导入学生信息")
    @PostMapping(value = "/import/students", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('data:import:student')")
    public ResponseEntity<ApiResponse<ImportResult>> importStudents(
            @Parameter(description = "Excel文件", required = true)
            @RequestParam("file") MultipartFile file) {
        
        try {
            ImportResult result = dataImportExportService.importStudents(file);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("导入学生数据失败", e);
            return ResponseEntity.ok(ApiResponse.error("导入学生数据失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "导入教师数据", description = "通过Excel文件批量导入教师信息")
    @PostMapping(value = "/import/teachers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('data:import:teacher')")
    public ResponseEntity<ApiResponse<ImportResult>> importTeachers(
            @Parameter(description = "Excel文件", required = true)
            @RequestParam("file") MultipartFile file) {
        
        try {
            ImportResult result = dataImportExportService.importTeachers(file);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("导入教师数据失败", e);
            return ResponseEntity.ok(ApiResponse.error("导入教师数据失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "导入课程数据", description = "通过Excel文件批量导入课程信息")
    @PostMapping(value = "/import/courses", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('data:import:course')")
    public ResponseEntity<ApiResponse<ImportResult>> importCourses(
            @Parameter(description = "Excel文件", required = true)
            @RequestParam("file") MultipartFile file) {
        
        try {
            ImportResult result = dataImportExportService.importCourses(file);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("导入课程数据失败", e);
            return ResponseEntity.ok(ApiResponse.error("导入课程数据失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "导入成绩数据", description = "通过Excel文件批量导入成绩信息")
    @PostMapping(value = "/import/grades", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('data:import:grade')")
    public ResponseEntity<ApiResponse<ImportResult>> importGrades(
            @Parameter(description = "Excel文件", required = true)
            @RequestParam("file") MultipartFile file) {
        
        try {
            ImportResult result = dataImportExportService.importGrades(file);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("导入成绩数据失败", e);
            return ResponseEntity.ok(ApiResponse.error("导入成绩数据失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "导出学生数据", description = "导出学生信息为Excel文件")
    @GetMapping("/export/students")
    @PreAuthorize("hasAuthority('data:export:student')")
    public ResponseEntity<byte[]> exportStudents(
            @Parameter(description = "查询条件") @RequestParam Map<String, Object> query) {
        
        try {
            byte[] data = dataImportExportService.exportStudents(query);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "students.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(data);
        } catch (Exception e) {
            log.error("导出学生数据失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "导出教师数据", description = "导出教师信息为Excel文件")
    @GetMapping("/export/teachers")
    @PreAuthorize("hasAuthority('data:export:teacher')")
    public ResponseEntity<byte[]> exportTeachers(
            @Parameter(description = "查询条件") @RequestParam Map<String, Object> query) {
        
        try {
            byte[] data = dataImportExportService.exportTeachers(query);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "teachers.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(data);
        } catch (Exception e) {
            log.error("导出教师数据失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "导出课程数据", description = "导出课程信息为Excel文件")
    @GetMapping("/export/courses")
    @PreAuthorize("hasAuthority('data:export:course')")
    public ResponseEntity<byte[]> exportCourses(
            @Parameter(description = "查询条件") @RequestParam Map<String, Object> query) {
        
        try {
            byte[] data = dataImportExportService.exportCourses(query);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "courses.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(data);
        } catch (Exception e) {
            log.error("导出课程数据失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "导出成绩数据", description = "导出成绩信息为Excel文件")
    @GetMapping("/export/grades")
    @PreAuthorize("hasAuthority('data:export:grade')")
    public ResponseEntity<byte[]> exportGrades(
            @Parameter(description = "查询条件") @RequestParam Map<String, Object> query) {
        
        try {
            byte[] data = dataImportExportService.exportGrades(query);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "grades.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(data);
        } catch (Exception e) {
            log.error("导出成绩数据失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "获取导入模板", description = "下载数据导入模板文件")
    @GetMapping("/template/{templateType}")
    @PreAuthorize("hasAuthority('data:template')")
    public ResponseEntity<byte[]> getImportTemplate(
            @Parameter(description = "模板类型", example = "student")
            @PathVariable String templateType) {
        
        try {
            byte[] data = dataImportExportService.getImportTemplate(templateType);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", templateType + "_template.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(data);
        } catch (Exception e) {
            log.error("获取导入模板失败: {}", templateType, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "验证导入文件", description = "验证Excel文件格式是否正确")
    @PostMapping(value = "/validate/{templateType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('data:validate')")
    public ResponseEntity<ApiResponse<ImportResult>> validateImportFile(
            @Parameter(description = "模板类型", example = "student")
            @PathVariable String templateType,
            @Parameter(description = "Excel文件", required = true)
            @RequestParam("file") MultipartFile file) {
        
        try {
            ImportResult result = dataImportExportService.validateImportFile(file, templateType);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("验证导入文件失败: {}", templateType, e);
            return ResponseEntity.ok(ApiResponse.error("验证导入文件失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "获取导入任务状态", description = "查询导入任务的执行状态")
    @GetMapping("/import/status/{taskId}")
    @PreAuthorize("hasAuthority('data:import:status')")
    public ResponseEntity<ApiResponse<ImportResult>> getImportTaskStatus(
            @Parameter(description = "任务ID", required = true)
            @PathVariable String taskId) {
        
        try {
            ImportResult result = dataImportExportService.getImportTaskStatus(taskId);
            if (result != null) {
                return ResponseEntity.ok(ApiResponse.success(result));
            } else {
                return ResponseEntity.ok(ApiResponse.error("任务不存在或已过期"));
            }
        } catch (Exception e) {
            log.error("获取导入任务状态失败: {}", taskId, e);
            return ResponseEntity.ok(ApiResponse.error("获取导入任务状态失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "数据导入导出健康检查", description = "检查数据导入导出服务状态")
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("数据导入导出服务运行正常"));
    }
}
