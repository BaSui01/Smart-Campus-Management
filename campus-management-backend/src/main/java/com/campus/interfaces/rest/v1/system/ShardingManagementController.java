package com.campus.interfaces.rest.v1.system;

import com.campus.infrastructure.sharding.ShardingTableManager;
import com.campus.infrastructure.sharding.ShardingTableScheduler;
import com.campus.infrastructure.sharding.ShardingValidationService;
import com.campus.infrastructure.sharding.ShardingPerformanceTest;
import com.campus.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 分表管理控制器
 * 提供分表管理、验证和性能测试的API接口
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Tag(name = "分表管理", description = "数据库分表管理、验证和性能测试功能")
@Slf4j
@RestController
@RequestMapping("/api/v1/system/sharding")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.shardingsphere.enabled", havingValue = "true", matchIfMissing = true)
public class ShardingManagementController {

    private final ShardingTableManager shardingTableManager;
    private final ShardingTableScheduler shardingTableScheduler;
    private final ShardingValidationService shardingValidationService;
    private final ShardingPerformanceTest shardingPerformanceTest;

    @Operation(summary = "获取分表统计信息", description = "获取所有分表的统计信息")
    @GetMapping("/statistics")
    @PreAuthorize("hasAuthority('system:sharding:view')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getShardingStatistics() {
        try {
            Map<String, Object> statistics = shardingTableManager.getShardingStatistics();
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            log.error("获取分表统计信息失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取分表统计信息失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "获取所有分表信息", description = "获取所有分表的详细信息")
    @GetMapping("/tables")
    @PreAuthorize("hasAuthority('system:sharding:view')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllShardingTables() {
        try {
            List<Map<String, Object>> tables = shardingTableManager.getAllShardingTables();
            return ResponseEntity.ok(ApiResponse.success(tables));
        } catch (Exception e) {
            log.error("获取分表信息失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取分表信息失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "创建学生分表", description = "为指定年份创建学生分表")
    @PostMapping("/tables/student/{year}")
    @PreAuthorize("hasAuthority('system:sharding:manage')")
    public ResponseEntity<ApiResponse<String>> createStudentTable(
            @Parameter(description = "入学年份", example = "2024")
            @PathVariable int year) {
        try {
            shardingTableManager.createStudentTable(year);
            return ResponseEntity.ok(ApiResponse.success("学生分表创建成功: tb_student_" + year));
        } catch (Exception e) {
            log.error("创建学生分表失败: {}", year, e);
            return ResponseEntity.ok(ApiResponse.error("创建学生分表失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "创建成绩分表", description = "为指定学期创建成绩分表")
    @PostMapping("/tables/grade/{semester}")
    @PreAuthorize("hasAuthority('system:sharding:manage')")
    public ResponseEntity<ApiResponse<String>> createGradeTable(
            @Parameter(description = "学期", example = "2024-1")
            @PathVariable String semester) {
        try {
            shardingTableManager.createGradeTable(semester);
            return ResponseEntity.ok(ApiResponse.success("成绩分表创建成功: tb_grade_" + semester.replace("-", "_")));
        } catch (Exception e) {
            log.error("创建成绩分表失败: {}", semester, e);
            return ResponseEntity.ok(ApiResponse.error("创建成绩分表失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "创建考勤分表", description = "为指定日期创建考勤分表")
    @PostMapping("/tables/attendance")
    @PreAuthorize("hasAuthority('system:sharding:manage')")
    public ResponseEntity<ApiResponse<String>> createAttendanceTable(
            @Parameter(description = "日期", example = "2024-06-20")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            shardingTableManager.createAttendanceTable(date);
            String yearMonth = date.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM"));
            return ResponseEntity.ok(ApiResponse.success("考勤分表创建成功: tb_attendance_" + yearMonth));
        } catch (Exception e) {
            log.error("创建考勤分表失败: {}", date, e);
            return ResponseEntity.ok(ApiResponse.error("创建考勤分表失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "检查表是否存在", description = "检查指定表是否存在")
    @GetMapping("/tables/{tableName}/exists")
    @PreAuthorize("hasAuthority('system:sharding:view')")
    public ResponseEntity<ApiResponse<Boolean>> checkTableExists(
            @Parameter(description = "表名", example = "tb_student_2024")
            @PathVariable String tableName) {
        try {
            boolean exists = shardingTableManager.tableExists(tableName);
            return ResponseEntity.ok(ApiResponse.success(exists));
        } catch (Exception e) {
            log.error("检查表是否存在失败: {}", tableName, e);
            return ResponseEntity.ok(ApiResponse.error("检查表是否存在失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "删除分表", description = "删除指定的分表")
    @DeleteMapping("/tables/{tableName}")
    @PreAuthorize("hasAuthority('system:sharding:manage')")
    public ResponseEntity<ApiResponse<String>> dropTable(
            @Parameter(description = "表名", example = "tb_student_2020")
            @PathVariable String tableName) {
        try {
            shardingTableManager.dropTable(tableName);
            return ResponseEntity.ok(ApiResponse.success("分表删除成功: " + tableName));
        } catch (Exception e) {
            log.error("删除分表失败: {}", tableName, e);
            return ResponseEntity.ok(ApiResponse.error("删除分表失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "手动创建学生分表", description = "手动触发创建指定年份的学生分表")
    @PostMapping("/manual/student/{year}")
    @PreAuthorize("hasAuthority('system:sharding:manage')")
    public ResponseEntity<ApiResponse<String>> manualCreateStudentTable(
            @Parameter(description = "入学年份", example = "2025")
            @PathVariable int year) {
        try {
            shardingTableScheduler.manualCreateStudentTable(year);
            return ResponseEntity.ok(ApiResponse.success("手动创建学生分表成功: tb_student_" + year));
        } catch (Exception e) {
            log.error("手动创建学生分表失败: {}", year, e);
            return ResponseEntity.ok(ApiResponse.error("手动创建学生分表失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "手动创建成绩分表", description = "手动触发创建指定学期的成绩分表")
    @PostMapping("/manual/grade/{semester}")
    @PreAuthorize("hasAuthority('system:sharding:manage')")
    public ResponseEntity<ApiResponse<String>> manualCreateGradeTable(
            @Parameter(description = "学期", example = "2025-1")
            @PathVariable String semester) {
        try {
            shardingTableScheduler.manualCreateGradeTable(semester);
            return ResponseEntity.ok(ApiResponse.success("手动创建成绩分表成功: tb_grade_" + semester.replace("-", "_")));
        } catch (Exception e) {
            log.error("手动创建成绩分表失败: {}", semester, e);
            return ResponseEntity.ok(ApiResponse.error("手动创建成绩分表失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "手动创建考勤分表", description = "手动触发创建指定月份的考勤分表")
    @PostMapping("/manual/attendance")
    @PreAuthorize("hasAuthority('system:sharding:manage')")
    public ResponseEntity<ApiResponse<String>> manualCreateAttendanceTable(
            @Parameter(description = "日期", example = "2025-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            shardingTableScheduler.manualCreateAttendanceTable(date);
            String yearMonth = date.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM"));
            return ResponseEntity.ok(ApiResponse.success("手动创建考勤分表成功: tb_attendance_" + yearMonth));
        } catch (Exception e) {
            log.error("手动创建考勤分表失败: {}", date, e);
            return ResponseEntity.ok(ApiResponse.error("手动创建考勤分表失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "验证分表功能", description = "验证分表功能的完整性和正确性")
    @PostMapping("/validate")
    @PreAuthorize("hasAuthority('system:sharding:test')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateShardingFunction() {
        try {
            Map<String, Object> result = shardingValidationService.validateShardingFunction();
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("验证分表功能失败", e);
            return ResponseEntity.ok(ApiResponse.error("验证分表功能失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "执行性能测试", description = "执行分表性能测试")
    @PostMapping("/performance-test")
    @PreAuthorize("hasAuthority('system:sharding:test')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> executePerformanceTest() {
        try {
            Map<String, Object> result = shardingPerformanceTest.executePerformanceTest();
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            log.error("执行性能测试失败", e);
            return ResponseEntity.ok(ApiResponse.error("执行性能测试失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "获取性能优化建议", description = "根据性能测试结果获取优化建议")
    @PostMapping("/optimization-suggestions")
    @PreAuthorize("hasAuthority('system:sharding:test')")
    public ResponseEntity<ApiResponse<List<String>>> getOptimizationSuggestions(
            @RequestBody Map<String, Object> performanceResult) {
        try {
            List<String> suggestions = shardingPerformanceTest.generateOptimizationSuggestions(performanceResult);
            return ResponseEntity.ok(ApiResponse.success(suggestions));
        } catch (Exception e) {
            log.error("获取优化建议失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取优化建议失败: " + e.getMessage()));
        }
    }

    @Operation(summary = "获取分表健康状态", description = "获取分表系统的健康状态")
    @GetMapping("/health")
    @PreAuthorize("hasAuthority('system:sharding:view')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getShardingHealth() {
        try {
            Map<String, Object> health = new java.util.HashMap<>();
            
            // 检查分表数量
            List<Map<String, Object>> tables = shardingTableManager.getAllShardingTables();
            health.put("totalTables", tables.size());
            
            // 检查统计信息
            Map<String, Object> statistics = shardingTableManager.getShardingStatistics();
            health.put("statistics", statistics);
            
            // 健康状态
            boolean isHealthy = tables.size() > 0;
            health.put("status", isHealthy ? "healthy" : "unhealthy");
            health.put("message", isHealthy ? "分表系统运行正常" : "分表系统异常");
            
            return ResponseEntity.ok(ApiResponse.success(health));
        } catch (Exception e) {
            log.error("获取分表健康状态失败", e);
            return ResponseEntity.ok(ApiResponse.error("获取分表健康状态失败: " + e.getMessage()));
        }
    }
}
