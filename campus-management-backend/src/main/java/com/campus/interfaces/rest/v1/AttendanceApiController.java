package com.campus.interfaces.rest.v1;

import com.campus.application.service.AttendanceService;
import com.campus.interfaces.rest.common.BaseController;
import com.campus.domain.entity.Attendance;
import com.campus.shared.common.ApiResponse;
import com.campus.shared.constants.RolePermissions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 考勤管理API控制器
 * 提供考勤记录的增删改查、统计分析等功能
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@RestController
@RequestMapping("/api/v1/attendance")
@Tag(name = "考勤管理", description = "考勤记录的增删改查、统计分析等操作")
public class AttendanceApiController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(AttendanceApiController.class);

    @Autowired
    private AttendanceService attendanceService;

    // ==================== 基础CRUD操作 ====================

    /**
     * 分页查询考勤记录列表
     */
    @GetMapping
    @Operation(summary = "分页查询考勤记录列表", description = "支持按条件搜索和分页查询考勤记录")
    @PreAuthorize(RolePermissions.ATTENDANCE_MANAGEMENT)
    public ResponseEntity<ApiResponse<List<Attendance>>> getAttendanceRecords(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "考勤日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate attendanceDate,
            @Parameter(description = "考勤状态") @RequestParam(required = false) String attendanceStatus,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "attendanceDate") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String sortDir,
            HttpServletRequest request) {

        try {
            log.info("查询考勤记录列表 - 页码: {}, 大小: {}, 学生ID: {}, 课程ID: {}", 
                page, size, studentId, courseId);

            // 验证分页参数
            validatePageParams(page, size);

            // 创建分页对象
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page - 1, size, sort);

            // 查询数据
            Page<Attendance> result = attendanceService.findByConditions(
                studentId, courseId, attendanceStatus, startDate, endDate, pageable);

            return successPage(result, "查询考勤记录列表成功");

        } catch (Exception e) {
            log.error("查询考勤记录列表失败: ", e);
            return error("查询考勤记录列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询考勤记录详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询考勤记录详情", description = "根据ID查询考勤记录的详细信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Attendance>> getAttendanceRecord(
            @Parameter(description = "考勤记录ID", required = true) @PathVariable Long id) {

        try {
            log.info("查询考勤记录详情 - ID: {}", id);

            validateId(id, "考勤记录");

            Optional<Attendance> attendance = attendanceService.findById(id);
            if (attendance.isPresent()) {
                return success("查询考勤记录详情成功", attendance.get());
            } else {
                return error("考勤记录不存在");
            }

        } catch (Exception e) {
            log.error("查询考勤记录详情失败 - ID: {}", id, e);
            return error("查询考勤记录详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建考勤记录
     */
    @PostMapping
    @Operation(summary = "创建考勤记录", description = "创建新的考勤记录")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Attendance>> createAttendanceRecord(
            @RequestBody Attendance attendance,
            HttpServletRequest request) {

        try {
            log.info("创建考勤记录 - 学生ID: {}, 课程ID: {}, 日期: {}", 
                attendance.getStudentId(), attendance.getCourseId(), attendance.getAttendanceDate());

            // 验证考勤记录数据
            validateAttendance(attendance);

            // 检查是否已存在相同的考勤记录
            if (attendanceService.existsByStudentIdAndCourseIdAndDate(
                attendance.getStudentId(), attendance.getCourseId(), attendance.getAttendanceDate())) {
                return error("该学生在此日期的考勤记录已存在");
            }

            // 设置默认值
            if (attendance.getAttendanceTime() == null) {
                attendance.setAttendanceTime(LocalDateTime.now().toLocalTime());
            }
            if (attendance.getAttendanceStatus() == null) {
                attendance.setAttendanceStatus("present");
            }

            Attendance savedAttendance = attendanceService.save(attendance);
            return success("考勤记录创建成功", savedAttendance);

        } catch (Exception e) {
            log.error("创建考勤记录失败: ", e);
            return error("创建考勤记录失败: " + e.getMessage());
        }
    }

    /**
     * 更新考勤记录
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新考勤记录", description = "更新考勤记录信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Attendance>> updateAttendanceRecord(
            @Parameter(description = "考勤记录ID", required = true) @PathVariable Long id,
            @RequestBody Attendance attendance,
            HttpServletRequest request) {

        try {
            log.info("更新考勤记录 - ID: {}", id);

            validateId(id, "考勤记录");

            // 检查考勤记录是否存在
            Optional<Attendance> existingOpt = attendanceService.findById(id);
            if (!existingOpt.isPresent()) {
                return error("考勤记录不存在");
            }

            Attendance existing = existingOpt.get();

            // 验证考勤记录数据
            validateAttendance(attendance);

            // 更新字段
            existing.setAttendanceStatus(attendance.getAttendanceStatus());
            existing.setAttendanceTime(attendance.getAttendanceTime());
            existing.setCheckInTime(attendance.getCheckInTime());
            existing.setCheckOutTime(attendance.getCheckOutTime());
            existing.setLateMinutes(attendance.getLateMinutes());
            existing.setEarlyLeaveMinutes(attendance.getEarlyLeaveMinutes());
            existing.setLocation(attendance.getLocation());
            existing.setRecordMethod(attendance.getRecordMethod());
            existing.setLeaveReason(attendance.getLeaveReason());
            existing.setProofAttachment(attendance.getProofAttachment());
            existing.setRemarks(attendance.getRemarks());

            Attendance updatedAttendance = attendanceService.save(existing);
            return success("考勤记录更新成功", updatedAttendance);

        } catch (Exception e) {
            log.error("更新考勤记录失败 - ID: {}", id, e);
            return error("更新考勤记录失败: " + e.getMessage());
        }
    }

    /**
     * 删除考勤记录
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除考勤记录", description = "删除指定的考勤记录")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAttendanceRecord(
            @Parameter(description = "考勤记录ID", required = true) @PathVariable Long id) {

        try {
            log.info("删除考勤记录 - ID: {}", id);

            validateId(id, "考勤记录");

            // 检查考勤记录是否存在
            Optional<Attendance> attendance = attendanceService.findById(id);
            if (!attendance.isPresent()) {
                return error("考勤记录不存在");
            }

            attendanceService.deleteById(id);
            return success("考勤记录删除成功");

        } catch (Exception e) {
            log.error("删除考勤记录失败 - ID: {}", id, e);
            return error("删除考勤记录失败: " + e.getMessage());
        }
    }

    // ==================== 业务操作 ====================

    /**
     * 学生签到
     */
    @PostMapping("/checkin")
    @Operation(summary = "学生签到", description = "学生进行签到操作")
    @PreAuthorize(RolePermissions.STUDENT_CHECKIN + " || " + RolePermissions.ATTENDANCE_MANAGEMENT)
    public ResponseEntity<ApiResponse<Attendance>> checkIn(
            @Parameter(description = "学生ID", required = true) @RequestParam Long studentId,
            @Parameter(description = "课程ID", required = true) @RequestParam Long courseId,
            @Parameter(description = "签到位置") @RequestParam(required = false) String location,
            HttpServletRequest request) {

        try {
            log.info("学生签到 - 学生ID: {}, 课程ID: {}", studentId, courseId);

            Attendance attendance = attendanceService.checkIn(studentId, courseId, "manual", location, request.getRemoteAddr());
            return success("签到成功", attendance);

        } catch (Exception e) {
            log.error("学生签到失败 - 学生ID: {}, 课程ID: {}", studentId, courseId, e);
            return error("签到失败: " + e.getMessage());
        }
    }

    /**
     * 学生签退
     */
    @PostMapping("/checkout")
    @Operation(summary = "学生签退", description = "学生进行签退操作")
    @PreAuthorize(RolePermissions.STUDENT_CHECKIN + " || " + RolePermissions.ATTENDANCE_MANAGEMENT)
    public ResponseEntity<ApiResponse<Attendance>> checkOut(
            @Parameter(description = "学生ID", required = true) @RequestParam Long studentId,
            @Parameter(description = "课程ID", required = true) @RequestParam Long courseId,
            HttpServletRequest request) {

        try {
            log.info("学生签退 - 学生ID: {}, 课程ID: {}", studentId, courseId);

            // 查找今日的考勤记录
            List<Attendance> todayAttendance = attendanceService.findByStudentIdAndCourseId(studentId, courseId)
                .stream()
                .filter(a -> a.getAttendanceDate().equals(LocalDate.now()))
                .toList();

            if (todayAttendance.isEmpty()) {
                return error("未找到今日的签到记录");
            }

            Attendance attendance = todayAttendance.get(0);
            attendanceService.checkOut(attendance.getId());

            // 重新查询更新后的记录
            Optional<Attendance> updatedAttendance = attendanceService.findById(attendance.getId());
            return success("签退成功", updatedAttendance.orElse(attendance));

        } catch (Exception e) {
            log.error("学生签退失败 - 学生ID: {}, 课程ID: {}", studentId, courseId, e);
            return error("签退失败: " + e.getMessage());
        }
    }

    /**
     * 批量导入考勤记录
     */
    @PostMapping("/batch-import")
    @Operation(summary = "批量导入考勤记录", description = "批量导入考勤记录")
    @PreAuthorize(RolePermissions.ACADEMIC_MANAGEMENT)
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchImportAttendance(
            @RequestBody List<Attendance> attendanceList) {

        try {
            log.info("批量导入考勤记录 - 数量: {}", attendanceList.size());

            Map<String, Object> result = attendanceService.batchImport(attendanceList);
            return success("批量导入考勤记录完成", result);

        } catch (Exception e) {
            log.error("批量导入考勤记录失败: ", e);
            return error("批量导入考勤记录失败: " + e.getMessage());
        }
    }

    // ==================== 统计分析 ====================

    /**
     * 获取学生考勤统计
     */
    @GetMapping("/statistics/student/{studentId}")
    @Operation(summary = "获取学生考勤统计", description = "获取指定学生的考勤统计信息")
    @PreAuthorize(RolePermissions.ATTENDANCE_MANAGEMENT)
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentAttendanceStatistics(
            @Parameter(description = "学生ID", required = true) @PathVariable Long studentId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        try {
            log.info("获取学生考勤统计 - 学生ID: {}", studentId);

            Map<String, Object> statistics = attendanceService.getStudentAttendanceStatistics(studentId, startDate, endDate);
            return success("获取学生考勤统计成功", statistics);

        } catch (Exception e) {
            log.error("获取学生考勤统计失败 - 学生ID: {}", studentId, e);
            return error("获取学生考勤统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取课程考勤统计
     */
    @GetMapping("/statistics/course/{courseId}")
    @Operation(summary = "获取课程考勤统计", description = "获取指定课程的考勤统计信息")
    @PreAuthorize(RolePermissions.ATTENDANCE_MANAGEMENT)
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCourseAttendanceStatistics(
            @Parameter(description = "课程ID", required = true) @PathVariable Long courseId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        try {
            log.info("获取课程考勤统计 - 课程ID: {}", courseId);

            Map<String, Object> statistics = attendanceService.getCourseAttendanceStatistics(courseId, startDate, endDate);
            return success("获取课程考勤统计成功", statistics);

        } catch (Exception e) {
            log.error("获取课程考勤统计失败 - 课程ID: {}", courseId, e);
            return error("获取课程考勤统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取班级考勤统计
     */
    @GetMapping("/statistics/class/{classId}")
    @Operation(summary = "获取班级考勤统计", description = "获取指定班级的考勤统计信息")
    @PreAuthorize(RolePermissions.ATTENDANCE_MANAGEMENT)
    public ResponseEntity<ApiResponse<Map<String, Object>>> getClassAttendanceStatistics(
            @Parameter(description = "班级ID", required = true) @PathVariable Long classId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        try {
            log.info("获取班级考勤统计 - 班级ID: {}", classId);

            Map<String, Object> statistics = attendanceService.getClassAttendanceStatistics(classId, startDate, endDate);
            return success("获取班级考勤统计成功", statistics);

        } catch (Exception e) {
            log.error("获取班级考勤统计失败 - 班级ID: {}", classId, e);
            return error("获取班级考勤统计失败: " + e.getMessage());
        }
    }

    // ==================== 统计端点 ====================

    /**
     * 获取考勤统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取考勤统计信息", description = "获取考勤模块的统计数据")
    @PreAuthorize(RolePermissions.STATISTICS_VIEW)
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAttendanceStats() {
        try {
            log.info("获取考勤统计信息");

            Map<String, Object> stats = new HashMap<>();

            // 基础统计
            long totalAttendance = attendanceService.count();
            stats.put("totalAttendance", totalAttendance);

            // 按状态统计
            List<Attendance> presentRecords = attendanceService.findByAttendanceStatus("present");
            stats.put("presentCount", presentRecords.size());

            List<Attendance> absentRecords = attendanceService.findByAttendanceStatus("absent");
            stats.put("absentCount", absentRecords.size());

            List<Attendance> lateRecords = attendanceService.findByAttendanceStatus("late");
            stats.put("lateCount", lateRecords.size());

            List<Attendance> leaveRecords = attendanceService.findByAttendanceStatus("leave");
            stats.put("leaveCount", leaveRecords.size());

            // 时间统计
            List<Attendance> todayAttendance = attendanceService.findTodayAttendance();
            stats.put("todayAttendance", todayAttendance.size());

            LocalDate startOfWeek = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
            LocalDate endOfWeek = startOfWeek.plusDays(6);
            List<Attendance> weekAttendance = attendanceService.findWeeklyAttendance(startOfWeek, endOfWeek);
            stats.put("weekAttendance", weekAttendance.size());

            LocalDate now = LocalDate.now();
            List<Attendance> monthAttendance = attendanceService.findMonthlyAttendance(now.getYear(), now.getMonthValue());
            stats.put("monthAttendance", monthAttendance.size());

            // 出勤率统计
            Map<String, Object> attendanceRateStats = new HashMap<>();
            if (totalAttendance > 0) {
                double presentRate = (presentRecords.size() * 100.0) / totalAttendance;
                attendanceRateStats.put("presentRate", Math.round(presentRate * 100.0) / 100.0);

                double absentRate = (absentRecords.size() * 100.0) / totalAttendance;
                attendanceRateStats.put("absentRate", Math.round(absentRate * 100.0) / 100.0);

                double lateRate = (lateRecords.size() * 100.0) / totalAttendance;
                attendanceRateStats.put("lateRate", Math.round(lateRate * 100.0) / 100.0);
            } else {
                attendanceRateStats.put("presentRate", 0.0);
                attendanceRateStats.put("absentRate", 0.0);
                attendanceRateStats.put("lateRate", 0.0);
            }
            stats.put("attendanceRateStats", attendanceRateStats);

            // 待审批请假记录
            List<Attendance> pendingLeave = attendanceService.findPendingLeaveRequests();
            stats.put("pendingLeaveCount", pendingLeave.size());

            // 最近活动（简化实现）
            List<Map<String, Object>> recentActivity = new ArrayList<>();
            stats.put("recentActivity", recentActivity);

            return success("获取考勤统计信息成功", stats);

        } catch (Exception e) {
            log.error("获取考勤统计信息失败: ", e);
            return error("获取考勤统计信息失败: " + e.getMessage());
        }
    }

    // ==================== 批量操作端点 ====================

    /**
     * 批量删除考勤记录
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除考勤记录", description = "根据ID列表批量删除考勤记录")
    @PreAuthorize(RolePermissions.BATCH_OPERATIONS)
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchDeleteAttendance(
            @Parameter(description = "考勤记录ID列表") @RequestBody List<Long> ids) {

        try {
            logOperation("批量删除考勤记录", ids.size());

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("考勤记录ID列表不能为空");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "考勤记录");
            }

            // 执行批量删除
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Long id : ids) {
                try {
                    // 检查是否可以删除
                    if (!attendanceService.canDeleteAttendance(id)) {
                        failCount++;
                        failReasons.add("考勤记录ID " + id + ": 无法删除");
                        continue;
                    }

                    attendanceService.deleteById(id);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("考勤记录ID " + id + ": " + e.getMessage());
                    log.warn("删除考勤记录{}失败: {}", id, e.getMessage());
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", ids.size());
            responseData.put("failReasons", failReasons);

            if (failCount == 0) {
                return success("批量删除考勤记录成功", responseData);
            } else if (successCount > 0) {
                return success("批量删除考勤记录部分成功", responseData);
            } else {
                return error("批量删除考勤记录失败");
            }

        } catch (Exception e) {
            log.error("批量删除考勤记录失败: ", e);
            return error("批量删除考勤记录失败: " + e.getMessage());
        }
    }

    /**
     * 批量更新考勤状态
     */
    @PutMapping("/batch/status")
    @Operation(summary = "批量更新考勤状态", description = "批量更新考勤记录的状态")
    @PreAuthorize(RolePermissions.ATTENDANCE_MANAGEMENT)
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchUpdateAttendanceStatus(
            @Parameter(description = "批量更新请求") @RequestBody Map<String, Object> request) {

        try {
            @SuppressWarnings("unchecked")
            List<Long> ids = (List<Long>) request.get("ids");
            String status = (String) request.get("status");

            logOperation("批量更新考勤状态", ids.size(), "状态: " + status);

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("考勤记录ID列表不能为空");
            }

            if (status == null || status.trim().isEmpty()) {
                return badRequest("考勤状态不能为空");
            }

            // 验证状态值
            List<String> validStatuses = List.of("present", "absent", "late", "leave", "excused");
            if (!validStatuses.contains(status)) {
                return badRequest("无效的考勤状态: " + status);
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "考勤记录");
            }

            // 执行批量状态更新
            attendanceService.batchUpdateAttendanceStatus(ids, status);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", ids.size());
            responseData.put("totalRequested", ids.size());
            responseData.put("status", status);

            return success("批量更新考勤状态成功", responseData);

        } catch (Exception e) {
            log.error("批量更新考勤状态失败: ", e);
            return error("批量更新考勤状态失败: " + e.getMessage());
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 验证考勤记录数据
     */
    private void validateAttendance(Attendance attendance) {
        if (attendance.getStudentId() == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }
        if (attendance.getCourseId() == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        if (attendance.getAttendanceDate() == null) {
            throw new IllegalArgumentException("考勤日期不能为空");
        }
        if (attendance.getAttendanceDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("考勤日期不能是未来日期");
        }
    }
}
