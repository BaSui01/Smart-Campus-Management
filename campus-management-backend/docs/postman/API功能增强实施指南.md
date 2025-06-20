# API功能增强实施指南

## 📋 概述

本文档详细说明智慧校园管理系统API功能增强方案，包括批量操作API、数据导入导出接口和GraphQL支持。

## 🎯 增强目标

- 🔄 实现批量操作API，提升数据处理效率
- 📤 添加数据导入导出功能，支持Excel格式
- 🚀 引入GraphQL支持，提供灵活的数据查询
- 📊 增强API监控和限流机制
- 🔍 完善API文档和测试覆盖

## 🏗️ 技术架构

```
API增强架构
├── 批量操作层
│   ├── 批量创建
│   ├── 批量更新
│   ├── 批量删除
│   └── 操作结果统计
├── 导入导出层
│   ├── Excel导入
│   ├── Excel导出
│   ├── CSV导入导出
│   └── 模板下载
├── GraphQL层
│   ├── Schema定义
│   ├── Resolver实现
│   ├── 数据加载器
│   └── 查询优化
└── 监控限流层
    ├── API限流
    ├── 性能监控
    ├── 错误统计
    └── 访问日志
```

## 📊 批量操作API实现

### 1. 批量操作基础类

```java
// 位置：src/main/java/com/campus/application/dto/BatchOperationResult.java
package com.campus.application.dto;

import lombok.Data;
import lombok.Builder;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class BatchOperationResult {
    private int totalCount;           // 总数量
    private int successCount;         // 成功数量
    private int failureCount;         // 失败数量
    private List<String> successIds;  // 成功的ID列表
    private List<BatchError> errors;  // 错误详情
    private long executionTime;       // 执行时间(毫秒)
    private Map<String, Object> metadata; // 元数据

    @Data
    @Builder
    public static class BatchError {
        private String id;            // 记录ID
        private String errorCode;     // 错误码
        private String errorMessage;  // 错误信息
        private Object originalData;  // 原始数据
    }

    public double getSuccessRate() {
        return totalCount > 0 ? (double) successCount / totalCount * 100 : 0;
    }
}
```

### 2. 批量操作注解

```java
// 位置：src/main/java/com/campus/shared/annotation/BatchOperation.java
package com.campus.shared.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BatchOperation {

    /**
     * 批量操作类型
     */
    BatchType type() default BatchType.CREATE;

    /**
     * 最大批量大小
     */
    int maxBatchSize() default 1000;

    /**
     * 是否启用事务
     */
    boolean transactional() default true;

    /**
     * 是否异步执行
     */
    boolean async() default false;

    /**
     * 操作描述
     */
    String description() default "";

    enum BatchType {
        CREATE, UPDATE, DELETE, IMPORT, EXPORT
    }
}
```

### 3. 批量用户操作实现

```java
// 位置：src/main/java/com/campus/application/service/impl/UserBatchServiceImpl.java
package com.campus.application.service.impl;

import com.campus.application.dto.BatchOperationResult;
import com.campus.application.dto.CreateUserRequest;
import com.campus.application.dto.UpdateUserRequest;
import com.campus.application.service.UserBatchService;
import com.campus.domain.entity.auth.User;
import com.campus.domain.repository.UserRepository;
import com.campus.shared.annotation.BatchOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBatchServiceImpl implements UserBatchService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final Executor taskExecutor;

    @Override
    @BatchOperation(type = BatchOperation.BatchType.CREATE, maxBatchSize = 500)
    @Transactional
    public BatchOperationResult batchCreateUsers(List<CreateUserRequest> requests) {
        long startTime = System.currentTimeMillis();

        BatchOperationResult.BatchOperationResultBuilder resultBuilder =
            BatchOperationResult.builder()
                .totalCount(requests.size())
                .successIds(new ArrayList<>())
                .errors(new ArrayList<>());

        int successCount = 0;
        int failureCount = 0;

        for (int i = 0; i < requests.size(); i++) {
            CreateUserRequest request = requests.get(i);
            try {
                // 验证请求数据
                validateCreateUserRequest(request);

                // 检查用户名是否已存在
                if (userRepository.existsByUsername(request.getUsername())) {
                    throw new IllegalArgumentException("用户名已存在: " + request.getUsername());
                }

                // 创建用户
                User user = userService.createUser(request);
                resultBuilder.successIds(List.of(user.getId().toString()));
                successCount++;

            } catch (Exception e) {
                log.warn("批量创建用户失败，索引: {}, 错误: {}", i, e.getMessage());

                BatchOperationResult.BatchError error = BatchOperationResult.BatchError.builder()
                    .id(String.valueOf(i))
                    .errorCode("CREATE_FAILED")
                    .errorMessage(e.getMessage())
                    .originalData(request)
                    .build();

                resultBuilder.errors(List.of(error));
                failureCount++;
            }
        }

        long executionTime = System.currentTimeMillis() - startTime;

        return resultBuilder
            .successCount(successCount)
            .failureCount(failureCount)
            .executionTime(executionTime)
            .build();
    }

    @Override
    @BatchOperation(type = BatchOperation.BatchType.UPDATE, maxBatchSize = 500)
    @Transactional
    public BatchOperationResult batchUpdateUsers(List<UpdateUserRequest> requests) {
        long startTime = System.currentTimeMillis();

        BatchOperationResult.BatchOperationResultBuilder resultBuilder =
            BatchOperationResult.builder()
                .totalCount(requests.size())
                .successIds(new ArrayList<>())
                .errors(new ArrayList<>());

        int successCount = 0;
        int failureCount = 0;

        for (UpdateUserRequest request : requests) {
            try {
                // 验证用户是否存在
                User existingUser = userRepository.findById(request.getId())
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + request.getId()));

                // 更新用户信息
                User updatedUser = userService.updateUser(request.getId(), request);
                resultBuilder.successIds(List.of(updatedUser.getId().toString()));
                successCount++;

            } catch (Exception e) {
                log.warn("批量更新用户失败，ID: {}, 错误: {}", request.getId(), e.getMessage());

                BatchOperationResult.BatchError error = BatchOperationResult.BatchError.builder()
                    .id(request.getId().toString())
                    .errorCode("UPDATE_FAILED")
                    .errorMessage(e.getMessage())
                    .originalData(request)
                    .build();

                resultBuilder.errors(List.of(error));
                failureCount++;
            }
        }

        long executionTime = System.currentTimeMillis() - startTime;

        return resultBuilder
            .successCount(successCount)
            .failureCount(failureCount)
            .executionTime(executionTime)
            .build();
    }

    @Override
    @BatchOperation(type = BatchOperation.BatchType.DELETE, maxBatchSize = 200)
    @Transactional
    public BatchOperationResult batchDeleteUsers(List<Long> userIds) {
        long startTime = System.currentTimeMillis();

        BatchOperationResult.BatchOperationResultBuilder resultBuilder =
            BatchOperationResult.builder()
                .totalCount(userIds.size())
                .successIds(new ArrayList<>())
                .errors(new ArrayList<>());

        int successCount = 0;
        int failureCount = 0;

        for (Long userId : userIds) {
            try {
                // 检查用户是否存在
                if (!userRepository.existsById(userId)) {
                    throw new IllegalArgumentException("用户不存在: " + userId);
                }

                // 检查是否可以删除（业务规则）
                if (!canDeleteUser(userId)) {
                    throw new IllegalArgumentException("用户不能删除，存在关联数据: " + userId);
                }

                // 软删除用户
                userService.deleteUser(userId);
                resultBuilder.successIds(List.of(userId.toString()));
                successCount++;

            } catch (Exception e) {
                log.warn("批量删除用户失败，ID: {}, 错误: {}", userId, e.getMessage());

                BatchOperationResult.BatchError error = BatchOperationResult.BatchError.builder()
                    .id(userId.toString())
                    .errorCode("DELETE_FAILED")
                    .errorMessage(e.getMessage())
                    .originalData(userId)
                    .build();

                resultBuilder.errors(List.of(error));
                failureCount++;
            }
        }

        long executionTime = System.currentTimeMillis() - startTime;

        return resultBuilder
            .successCount(successCount)
            .failureCount(failureCount)
            .executionTime(executionTime)
            .build();
    }

    @Override
    @BatchOperation(type = BatchOperation.BatchType.CREATE, async = true)
    public CompletableFuture<BatchOperationResult> batchCreateUsersAsync(List<CreateUserRequest> requests) {
        return CompletableFuture.supplyAsync(() -> batchCreateUsers(requests), taskExecutor);
    }

    private void validateCreateUserRequest(CreateUserRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("密码长度不能少于6位");
        }
        // 其他验证逻辑...
    }

    private boolean canDeleteUser(Long userId) {
        // 检查用户是否有关联的学生、教师等数据
        // 实现具体的业务规则检查
        return true;
    }
}
```

### 4. 批量学生操作实现

```java
// 位置：src/main/java/com/campus/application/service/impl/StudentBatchServiceImpl.java
package com.campus.application.service.impl;

import com.campus.application.dto.BatchOperationResult;
import com.campus.application.dto.CreateStudentRequest;
import com.campus.application.dto.BatchUpdateClassRequest;
import com.campus.application.service.StudentBatchService;
import com.campus.domain.entity.organization.Student;
import com.campus.domain.repository.StudentRepository;
import com.campus.shared.annotation.BatchOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentBatchServiceImpl implements StudentBatchService {

    private final StudentRepository studentRepository;
    private final StudentService studentService;

    @Override
    @BatchOperation(type = BatchOperation.BatchType.CREATE, maxBatchSize = 1000)
    @Transactional
    public BatchOperationResult batchCreateStudents(List<CreateStudentRequest> requests) {
        long startTime = System.currentTimeMillis();

        BatchOperationResult.BatchOperationResultBuilder resultBuilder =
            BatchOperationResult.builder()
                .totalCount(requests.size())
                .successIds(new ArrayList<>())
                .errors(new ArrayList<>());

        int successCount = 0;
        int failureCount = 0;

        for (int i = 0; i < requests.size(); i++) {
            CreateStudentRequest request = requests.get(i);
            try {
                // 验证学号是否已存在
                if (studentRepository.existsByStudentNo(request.getStudentNo())) {
                    throw new IllegalArgumentException("学号已存在: " + request.getStudentNo());
                }

                // 创建学生
                Student student = studentService.createStudent(request);
                resultBuilder.successIds(List.of(student.getId().toString()));
                successCount++;

            } catch (Exception e) {
                log.warn("批量创建学生失败，索引: {}, 错误: {}", i, e.getMessage());

                BatchOperationResult.BatchError error = BatchOperationResult.BatchError.builder()
                    .id(String.valueOf(i))
                    .errorCode("CREATE_STUDENT_FAILED")
                    .errorMessage(e.getMessage())
                    .originalData(request)
                    .build();

                resultBuilder.errors(List.of(error));
                failureCount++;
            }
        }

        long executionTime = System.currentTimeMillis() - startTime;

        return resultBuilder
            .successCount(successCount)
            .failureCount(failureCount)
            .executionTime(executionTime)
            .build();
    }

    @Override
    @BatchOperation(type = BatchOperation.BatchType.UPDATE, maxBatchSize = 500)
    @Transactional
    public BatchOperationResult batchUpdateStudentClass(BatchUpdateClassRequest request) {
        long startTime = System.currentTimeMillis();

        List<Long> studentIds = request.getStudentIds();
        Long newClassId = request.getNewClassId();

        BatchOperationResult.BatchOperationResultBuilder resultBuilder =
            BatchOperationResult.builder()
                .totalCount(studentIds.size())
                .successIds(new ArrayList<>())
                .errors(new ArrayList<>());

        int successCount = 0;
        int failureCount = 0;

        for (Long studentId : studentIds) {
            try {
                // 验证学生是否存在
                Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("学生不存在: " + studentId));

                // 更新班级
                student.setClassId(newClassId);
                studentRepository.save(student);

                resultBuilder.successIds(List.of(studentId.toString()));
                successCount++;

            } catch (Exception e) {
                log.warn("批量更新学生班级失败，学生ID: {}, 错误: {}", studentId, e.getMessage());

                BatchOperationResult.BatchError error = BatchOperationResult.BatchError.builder()
                    .id(studentId.toString())
                    .errorCode("UPDATE_CLASS_FAILED")
                    .errorMessage(e.getMessage())
                    .originalData(studentId)
                    .build();

                resultBuilder.errors(List.of(error));
                failureCount++;
            }
        }

        long executionTime = System.currentTimeMillis() - startTime;

        return resultBuilder
            .successCount(successCount)
            .failureCount(failureCount)
            .executionTime(executionTime)
            .build();
    }
}

## 📤 数据导入导出功能

### 1. 导入导出依赖配置

```xml
<!-- pom.xml 添加Excel处理依赖 -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.2.4</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.4</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-scratchpad</artifactId>
    <version>5.2.4</version>
</dependency>
```

### 2. Excel工具类

```java
// 位置：src/main/java/com/campus/shared/util/ExcelUtil.java
package com.campus.shared.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {

    /**
     * 读取Excel文件数据
     */
    public static <T> List<T> readExcel(MultipartFile file, Class<T> clazz, int startRow) throws IOException {
        List<T> result = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            // 获取表头行
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Excel文件格式错误：缺少表头");
            }

            // 解析数据行
            for (int rowIndex = startRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null || isEmptyRow(row)) {
                    continue;
                }

                T obj = parseRowToObject(row, clazz, headerRow);
                if (obj != null) {
                    result.add(obj);
                }
            }
        }

        return result;
    }

    /**
     * 生成Excel文件
     */
    public static <T> byte[] writeExcel(List<T> data, Class<T> clazz, String[] headers) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("数据");

            // 创建表头样式
            CellStyle headerStyle = createHeaderStyle(workbook);

            // 创建表头行
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // 创建数据行
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i + 1);
                T obj = data.get(i);

                for (int j = 0; j < fields.length && j < headers.length; j++) {
                    Cell cell = row.createCell(j);
                    fields[j].setAccessible(true);

                    try {
                        Object value = fields[j].get(obj);
                        setCellValue(cell, value);
                    } catch (IllegalAccessException e) {
                        cell.setCellValue("");
                    }
                }
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        return style;
    }

    private static <T> T parseRowToObject(Row row, Class<T> clazz, Row headerRow) {
        try {
            T obj = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();

            for (int i = 0; i < fields.length && i < row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i);
                if (cell == null) continue;

                fields[i].setAccessible(true);
                Object value = getCellValue(cell, fields[i].getType());
                fields[i].set(obj, value);
            }

            return obj;
        } catch (Exception e) {
            throw new RuntimeException("解析Excel行数据失败", e);
        }
    }

    private static Object getCellValue(Cell cell, Class<?> fieldType) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                String stringValue = cell.getStringCellValue();
                if (fieldType == String.class) {
                    return stringValue;
                } else if (fieldType == Long.class || fieldType == long.class) {
                    return Long.parseLong(stringValue);
                } else if (fieldType == Integer.class || fieldType == int.class) {
                    return Integer.parseInt(stringValue);
                }
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    if (fieldType == LocalDate.class) {
                        return cell.getLocalDateTimeCellValue().toLocalDate();
                    } else if (fieldType == LocalDateTime.class) {
                        return cell.getLocalDateTimeCellValue();
                    }
                } else {
                    double numericValue = cell.getNumericCellValue();
                    if (fieldType == Long.class || fieldType == long.class) {
                        return (long) numericValue;
                    } else if (fieldType == Integer.class || fieldType == int.class) {
                        return (int) numericValue;
                    } else if (fieldType == Double.class || fieldType == double.class) {
                        return numericValue;
                    }
                }
                break;
            case BOOLEAN:
                if (fieldType == Boolean.class || fieldType == boolean.class) {
                    return cell.getBooleanCellValue();
                }
                break;
        }

        return null;
    }

    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
            return;
        }

        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof LocalDate) {
            cell.setCellValue((LocalDate) value);
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue((LocalDateTime) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private static boolean isEmptyRow(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }
}
```

### 3. 导入导出服务接口

```java
// 位置：src/main/java/com/campus/application/service/DataImportExportService.java
package com.campus.application.service;

import com.campus.application.dto.*;
import org.springframework.web.multipart.MultipartFile;

public interface DataImportExportService {

    /**
     * 导入学生数据
     */
    ImportResult importStudents(MultipartFile file);

    /**
     * 导入课程数据
     */
    ImportResult importCourses(MultipartFile file);

    /**
     * 导入成绩数据
     */
    ImportResult importGrades(MultipartFile file);

    /**
     * 导入用户数据
     */
    ImportResult importUsers(MultipartFile file);

    /**
     * 导出学生数据
     */
    byte[] exportStudents(StudentExportQuery query);

    /**
     * 导出课程数据
     */
    byte[] exportCourses(CourseExportQuery query);

    /**
     * 导出成绩数据
     */
    byte[] exportGrades(GradeExportQuery query);

    /**
     * 导出用户数据
     */
    byte[] exportUsers(UserExportQuery query);

    /**
     * 下载导入模板
     */
    byte[] downloadTemplate(String templateType);

    /**
     * 验证导入文件格式
     */
    ValidationResult validateImportFile(MultipartFile file, String dataType);
}
```

### 4. 导入结果类

```java
// 位置：src/main/java/com/campus/application/dto/ImportResult.java
package com.campus.application.dto;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class ImportResult {
    private boolean success;
    private String message;
    private int totalRows;
    private int successRows;
    private int failureRows;
    private List<ImportError> errors;
    private long executionTime;
    private String importId;  // 导入任务ID

    @Data
    @Builder
    public static class ImportError {
        private int rowNumber;
        private String columnName;
        private String errorMessage;
        private String originalValue;
        private String suggestedValue;
    }

    public double getSuccessRate() {
        return totalRows > 0 ? (double) successRows / totalRows * 100 : 0;
    }
}

### 5. 导入导出控制器

```java
// 位置：src/main/java/com/campus/interfaces/rest/v1/system/DataImportExportController.java
package com.campus.interfaces.rest.v1.system;

import com.campus.application.service.DataImportExportService;
import com.campus.application.dto.*;
import com.campus.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "数据导入导出", description = "Excel数据导入导出功能")
@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
public class DataImportExportController {

    private final DataImportExportService dataImportExportService;

    @Operation(summary = "导入学生数据", description = "从Excel文件导入学生信息")
    @PostMapping(value = "/import/students", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('data:import:student')")
    public ResponseEntity<ApiResponse<ImportResult>> importStudents(
            @RequestParam("file") MultipartFile file) {
        ImportResult result = dataImportExportService.importStudents(file);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(summary = "导入课程数据", description = "从Excel文件导入课程信息")
    @PostMapping(value = "/import/courses", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('data:import:course')")
    public ResponseEntity<ApiResponse<ImportResult>> importCourses(
            @RequestParam("file") MultipartFile file) {
        ImportResult result = dataImportExportService.importCourses(file);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(summary = "导入成绩数据", description = "从Excel文件导入成绩信息")
    @PostMapping(value = "/import/grades", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('data:import:grade')")
    public ResponseEntity<ApiResponse<ImportResult>> importGrades(
            @RequestParam("file") MultipartFile file) {
        ImportResult result = dataImportExportService.importGrades(file);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(summary = "导出学生数据", description = "导出学生信息到Excel文件")
    @GetMapping("/export/students")
    @PreAuthorize("hasAuthority('data:export:student')")
    public ResponseEntity<byte[]> exportStudents(@ModelAttribute StudentExportQuery query) {
        byte[] data = dataImportExportService.exportStudents(query);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "students.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    @Operation(summary = "导出课程数据", description = "导出课程信息到Excel文件")
    @GetMapping("/export/courses")
    @PreAuthorize("hasAuthority('data:export:course')")
    public ResponseEntity<byte[]> exportCourses(@ModelAttribute CourseExportQuery query) {
        byte[] data = dataImportExportService.exportCourses(query);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "courses.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    @Operation(summary = "导出成绩数据", description = "导出成绩信息到Excel文件")
    @GetMapping("/export/grades")
    @PreAuthorize("hasAuthority('data:export:grade')")
    public ResponseEntity<byte[]> exportGrades(@ModelAttribute GradeExportQuery query) {
        byte[] data = dataImportExportService.exportGrades(query);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "grades.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    @Operation(summary = "下载导入模板", description = "下载数据导入Excel模板")
    @GetMapping("/template/{templateType}")
    @PreAuthorize("hasAuthority('data:template')")
    public ResponseEntity<byte[]> downloadTemplate(@PathVariable String templateType) {
        byte[] data = dataImportExportService.downloadTemplate(templateType);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", templateType + "_template.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    @Operation(summary = "验证导入文件", description = "验证Excel文件格式和数据")
    @PostMapping(value = "/validate/{dataType}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('data:validate')")
    public ResponseEntity<ApiResponse<ValidationResult>> validateImportFile(
            @PathVariable String dataType,
            @RequestParam("file") MultipartFile file) {
        ValidationResult result = dataImportExportService.validateImportFile(file, dataType);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
```

## 🚀 GraphQL支持实现

### 1. GraphQL依赖配置

```xml
<!-- pom.xml 添加GraphQL依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-graphql</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

### 2. GraphQL Schema定义

```graphql
# 位置：src/main/resources/graphql/schema.graphqls

# 用户类型
type User {
    id: ID!
    username: String!
    realName: String
    email: String
    phone: String
    userType: String
    status: Int
    createdAt: String
    updatedAt: String
}

# 学生类型
type Student {
    id: ID!
    userId: Long!
    studentNo: String!
    grade: String!
    major: String
    classId: Long
    enrollmentYear: Int
    user: User
    schoolClass: SchoolClass
}

# 班级类型
type SchoolClass {
    id: ID!
    className: String!
    grade: String!
    majorId: Long
    teacherId: Long
    studentCount: Int
    students: [Student]
}

# 课程类型
type Course {
    id: ID!
    courseName: String!
    courseCode: String!
    credits: Int!
    courseType: String
    description: String
    teacherId: Long
    semester: String
    maxStudents: Int
    currentStudents: Int
}

# 成绩类型
type Grade {
    id: ID!
    studentId: Long!
    courseId: Long!
    examType: String!
    score: Float!
    semester: String!
    student: Student
    course: Course
}

# 查询输入类型
input UserFilter {
    username: String
    realName: String
    userType: String
    status: Int
}

input StudentFilter {
    studentNo: String
    grade: String
    major: String
    classId: Long
    enrollmentYear: Int
}

input CourseFilter {
    courseName: String
    courseCode: String
    teacherId: Long
    semester: String
}

input GradeFilter {
    studentId: Long
    courseId: Long
    semester: String
    examType: String
    minScore: Float
    maxScore: Float
}

# 分页输入类型
input PageInput {
    page: Int = 0
    size: Int = 10
    sort: String = "id"
    direction: String = "DESC"
}

# 分页结果类型
type PageInfo {
    page: Int!
    size: Int!
    totalElements: Long!
    totalPages: Int!
    hasNext: Boolean!
    hasPrevious: Boolean!
}

type UserPage {
    content: [User]!
    pageInfo: PageInfo!
}

type StudentPage {
    content: [Student]!
    pageInfo: PageInfo!
}

type CoursePage {
    content: [Course]!
    pageInfo: PageInfo!
}

type GradePage {
    content: [Grade]!
    pageInfo: PageInfo!
}

# 根查询类型
type Query {
    # 用户查询
    users(filter: UserFilter, page: PageInput): UserPage
    user(id: ID!): User
    userByUsername(username: String!): User

    # 学生查询
    students(filter: StudentFilter, page: PageInput): StudentPage
    student(id: ID!): Student
    studentByNo(studentNo: String!): Student

    # 课程查询
    courses(filter: CourseFilter, page: PageInput): CoursePage
    course(id: ID!): Course
    coursesByTeacher(teacherId: Long!): [Course]

    # 成绩查询
    grades(filter: GradeFilter, page: PageInput): GradePage
    grade(id: ID!): Grade
    gradesByStudent(studentId: Long!, semester: String): [Grade]
    gradesByCourse(courseId: Long!, semester: String): [Grade]

    # 统计查询
    userStats: UserStats
    studentStats: StudentStats
    courseStats: CourseStats
    gradeStats: GradeStats
}

# 统计类型
type UserStats {
    totalUsers: Long!
    activeUsers: Long!
    usersByType: [UserTypeCount]!
}

type UserTypeCount {
    userType: String!
    count: Long!
}

type StudentStats {
    totalStudents: Long!
    studentsByGrade: [GradeCount]!
    studentsByMajor: [MajorCount]!
}

type GradeCount {
    grade: String!
    count: Long!
}

type MajorCount {
    major: String!
    count: Long!
}

type CourseStats {
    totalCourses: Long!
    coursesByType: [CourseTypeCount]!
    averageCredits: Float!
}

type CourseTypeCount {
    courseType: String!
    count: Long!
}

type GradeStats {
    totalGrades: Long!
    averageScore: Float!
    gradeDistribution: [ScoreRange]!
}

type ScoreRange {
    range: String!
    count: Long!
    percentage: Float!
}
```

### 3. GraphQL Resolver实现

```java
// 位置：src/main/java/com/campus/interfaces/graphql/UserQueryResolver.java
package com.campus.interfaces.graphql;

import com.campus.application.service.UserService;
import com.campus.domain.entity.auth.User;
import com.campus.application.dto.UserFilter;
import com.campus.application.dto.PageInput;
import com.campus.application.dto.UserPage;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UserQueryResolver {

    private final UserService userService;

    @QueryMapping
    public UserPage users(@Argument UserFilter filter, @Argument PageInput page) {
        return userService.findUsersWithGraphQL(filter, page);
    }

    @QueryMapping
    public User user(@Argument String id) {
        return userService.findById(Long.parseLong(id));
    }

    @QueryMapping
    public User userByUsername(@Argument String username) {
        return userService.findByUsername(username);
    }
}
```
```
```