package com.campus.interfaces.rest.v1;

import com.campus.application.service.AttendanceService;
import com.campus.common.controller.BaseController;
import com.campus.domain.entity.Attendance;
import com.campus.shared.common.ApiResponse;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 考勤管理API控制器
 * 提供考勤记录的增删改查、统计分析等功能
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2024-12-07
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
    @PreAuthorize("hasAuthority('academic:attendance:list')")
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
    @PreAuthorize("hasAuthority('academic:attendance:list')")
    public ResponseEntity<ApiResponse<Attendance>> getAttendanceRecord(
            @Parameter(description = "考勤记录ID", required = true) @PathVariable Long id) {

        try {
            log.info("查询考勤记录详情 - ID: {}", id);

            validateId(id, "考勤记录");

            Optional<Attendance> attendance = attendanceService.findById(id);
            if (attendance.isPresent()) {
                return success(attendance.get(), "查询考勤记录详情成功");
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
    @PreAuthorize("hasAuthority('academic:attendance:add')")
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
            return success(savedAttendance, "考勤记录创建成功");

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
    @PreAuthorize("hasAuthority('academic:attendance:edit')")
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
            return success(updatedAttendance, "考勤记录更新成功");

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
    @PreAuthorize("hasAuthority('academic:attendance:delete')")
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
            return success(null, "考勤记录删除成功");

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
    @PreAuthorize("hasAuthority('academic:attendance:checkin')")
    public ResponseEntity<ApiResponse<Attendance>> checkIn(
            @Parameter(description = "学生ID", required = true) @RequestParam Long studentId,
            @Parameter(description = "课程ID", required = true) @RequestParam Long courseId,
            @Parameter(description = "签到位置") @RequestParam(required = false) String location,
            HttpServletRequest request) {

        try {
            log.info("学生签到 - 学生ID: {}, 课程ID: {}", studentId, courseId);

            Attendance attendance = attendanceService.checkIn(studentId, courseId, "manual", location, request.getRemoteAddr());
            return success(attendance, "签到成功");

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
    @PreAuthorize("hasAuthority('academic:attendance:checkout')")
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
            return success(updatedAttendance.orElse(attendance), "签退成功");

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
    @PreAuthorize("hasAuthority('academic:attendance:import')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchImportAttendance(
            @RequestBody List<Attendance> attendanceList) {

        try {
            log.info("批量导入考勤记录 - 数量: {}", attendanceList.size());

            Map<String, Object> result = attendanceService.batchImport(attendanceList);
            return success(result, "批量导入考勤记录完成");

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
    @PreAuthorize("hasAuthority('academic:attendance:statistics')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentAttendanceStatistics(
            @Parameter(description = "学生ID", required = true) @PathVariable Long studentId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        try {
            log.info("获取学生考勤统计 - 学生ID: {}", studentId);

            Map<String, Object> statistics = attendanceService.getStudentAttendanceStatistics(studentId, startDate, endDate);
            return success(statistics, "获取学生考勤统计成功");

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
    @PreAuthorize("hasAuthority('academic:attendance:statistics')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCourseAttendanceStatistics(
            @Parameter(description = "课程ID", required = true) @PathVariable Long courseId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        try {
            log.info("获取课程考勤统计 - 课程ID: {}", courseId);

            Map<String, Object> statistics = attendanceService.getCourseAttendanceStatistics(courseId, startDate, endDate);
            return success(statistics, "获取课程考勤统计成功");

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
    @PreAuthorize("hasAuthority('academic:attendance:statistics')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getClassAttendanceStatistics(
            @Parameter(description = "班级ID", required = true) @PathVariable Long classId,
            @Parameter(description = "开始日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        try {
            log.info("获取班级考勤统计 - 班级ID: {}", classId);

            Map<String, Object> statistics = attendanceService.getClassAttendanceStatistics(classId, startDate, endDate);
            return success(statistics, "获取班级考勤统计成功");

        } catch (Exception e) {
            log.error("获取班级考勤统计失败 - 班级ID: {}", classId, e);
            return error("获取班级考勤统计失败: " + e.getMessage());
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
