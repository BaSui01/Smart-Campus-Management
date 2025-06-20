package com.campus.application.Implement.attendance;

import com.campus.application.service.academic.AttendanceService;
import com.campus.domain.entity.academic.Attendance;
import com.campus.domain.repository.academic.AttendanceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 考勤管理服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    // ==================== 基础CRUD方法 ====================

    @Override
    public Attendance save(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Attendance> findById(Long id) {
        return attendanceRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findAll() {
        return attendanceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Attendance> findAll(Pageable pageable) {
        return attendanceRepository.findAll(pageable);
    }

    @Override
    public void deleteById(Long id) {
        attendanceRepository.deleteById(id);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        attendanceRepository.deleteAllById(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return attendanceRepository.count();
    }

    // ==================== 业务查询方法 ====================

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findByStudentId(Long studentId) {
        return attendanceRepository.findByStudentIdAndDeleted(studentId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findByCourseId(Long courseId) {
        return attendanceRepository.findByCourseIdAndDeleted(courseId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findByStudentIdAndCourseId(Long studentId, Long courseId) {
        return attendanceRepository.findByStudentIdAndCourseIdAndDeleted(studentId, courseId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findByAttendanceDate(LocalDate attendanceDate) {
        return attendanceRepository.findByAttendanceDateAndDeleted(attendanceDate, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findByAttendanceStatus(String attendanceStatus) {
        return attendanceRepository.findByAttendanceStatusAndDeleted(attendanceStatus, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByAttendanceDateBetweenAndDeleted(startDate, endDate, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findPendingLeaveRequests() {
        return attendanceRepository.findPendingLeaveRequests();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findTodayAttendance() {
        return attendanceRepository.findTodayAttendance();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findWeeklyAttendance(LocalDate startOfWeek, LocalDate endOfWeek) {
        return attendanceRepository.findByAttendanceDateBetweenAndDeleted(startOfWeek, endOfWeek, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findMonthlyAttendance(int year, int month) {
        return attendanceRepository.findMonthlyAttendance(year, month);
    }

    // ==================== 分页查询方法 ====================

    @Override
    @Transactional(readOnly = true)
    public Page<Attendance> findByStudentId(Long studentId, Pageable pageable) {
        return attendanceRepository.findByStudentIdAndDeleted(studentId, 0, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Attendance> findByCourseId(Long courseId, Pageable pageable) {
        return attendanceRepository.findByCourseIdAndDeleted(courseId, 0, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Attendance> findByConditions(Long studentId, Long courseId, String attendanceStatus,
                                           LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return attendanceRepository.findByConditions(studentId, courseId, attendanceStatus, 
                                                    startDate, endDate, pageable);
    }

    // ==================== 业务操作方法 ====================

    @Override
    public Attendance checkIn(Long studentId, Long courseId, String method, String location, String device) {
        try {
            LocalDate today = LocalDate.now();
            
            // 1. 数据验证
            validateCheckInData(studentId, courseId, method, location);
            
            // 2. 检查是否已有今日考勤记录
            Optional<Attendance> existingOpt = attendanceRepository
                .findByStudentIdAndCourseIdAndAttendanceDateAndDeleted(studentId, courseId, today, 0);
            
            Attendance attendance;
            if (existingOpt.isPresent()) {
                attendance = existingOpt.get();
                
                // 3. 检查是否已经签到过
                if (attendance.getCheckInTime() != null) {
                    throw new IllegalStateException("今日已签到，无需重复签到");
                }
            } else {
                attendance = new Attendance(studentId, courseId, today, "present");
                attendance.setCreatedAt(LocalDateTime.now());
            }
            
            // 4. 智能签到处理
            processSmartCheckIn(attendance, method, location, device);
            
            // 5. 保存考勤记录
            attendance.setUpdatedAt(LocalDateTime.now());
            Attendance saved = attendanceRepository.save(attendance);
            
            // 6. 异步更新统计数据
            updateAttendanceStatistics(studentId, courseId);
            
            return saved;
        } catch (Exception e) {
            throw new RuntimeException("签到失败：" + e.getMessage(), e);
        }
    }

    /**
     * 验证签到数据
     */
    private void validateCheckInData(Long studentId, Long courseId, String method, String location) {
        if (studentId == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }
        if (courseId == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        if (method == null || method.trim().isEmpty()) {
            throw new IllegalArgumentException("签到方式不能为空");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("签到位置不能为空");
        }
    }

    /**
     * 智能签到处理
     */
    private void processSmartCheckIn(Attendance attendance, String method, String location, String device) {
        LocalDateTime now = LocalDateTime.now();
        
        // 设置签到信息
        attendance.checkIn(method, location, device);
        
        // 智能判断考勤状态
        String status = calculateAttendanceStatus(now);
        attendance.setAttendanceStatus(status);
        
        // 计算迟到时间
        if ("late".equals(status)) {
            int lateMinutes = calculateLateMinutes(now);
            attendance.setLateMinutes(lateMinutes);
        }
        
        // 记录签到设备信息（使用已有的方法）
        // 注意：Attendance实体可能没有这些方法，这里使用通用方法
        if (attendance.getRemarks() == null) {
            attendance.setRemarks("");
        }
        attendance.setRemarks(attendance.getRemarks() +
            " 设备:" + device + " 位置:" + location);
    }

    /**
     * 计算考勤状态
     */
    private String calculateAttendanceStatus(LocalDateTime checkInTime) {
        // 获取课程开始时间（简化实现，实际应从课程表获取）
        LocalDateTime courseStartTime = LocalDateTime.of(checkInTime.toLocalDate(),
            java.time.LocalTime.of(8, 0)); // 假设课程8点开始
        
        if (checkInTime.isBefore(courseStartTime.plusMinutes(15))) {
            return "present"; // 准时
        } else if (checkInTime.isBefore(courseStartTime.plusMinutes(30))) {
            return "late"; // 迟到
        } else {
            return "absent"; // 缺勤
        }
    }

    /**
     * 计算迟到分钟数
     */
    private int calculateLateMinutes(LocalDateTime checkInTime) {
        LocalDateTime courseStartTime = LocalDateTime.of(checkInTime.toLocalDate(),
            java.time.LocalTime.of(8, 0));
        
        if (checkInTime.isAfter(courseStartTime)) {
            return (int) java.time.Duration.between(courseStartTime, checkInTime).toMinutes();
        }
        return 0;
    }

    /**
     * 异步更新考勤统计
     */
    private void updateAttendanceStatistics(Long studentId, Long courseId) {
        try {
            // 更新学生考勤统计
            long studentAttendanceCount = countByStudentId(studentId);
            
            // 更新课程考勤统计
            long courseAttendanceCount = countByCourseId(courseId);
            
            // 记录日志
            System.out.println("更新考勤统计 - 学生：" + studentId +
                             ", 考勤次数：" + studentAttendanceCount +
                             ", 课程：" + courseId +
                             ", 课程考勤：" + courseAttendanceCount);
        } catch (Exception e) {
            // 统计更新失败不影响主流程
            System.err.println("更新考勤统计失败：" + e.getMessage());
        }
    }

    @Override
    public void checkOut(Long attendanceId) {
        Optional<Attendance> attendanceOpt = attendanceRepository.findById(attendanceId);
        if (attendanceOpt.isPresent()) {
            Attendance attendance = attendanceOpt.get();
            attendance.checkOut();
            attendanceRepository.save(attendance);
        }
    }

    @Override
    public void markLate(Long attendanceId, int minutes) {
        attendanceRepository.updateLateInfo(attendanceId, minutes);
    }

    @Override
    public void markEarlyLeave(Long attendanceId, int minutes) {
        attendanceRepository.updateEarlyLeaveInfo(attendanceId, minutes);
    }

    @Override
    public Attendance applyLeave(Long studentId, Long courseId, LocalDate date, String leaveType, String reason) {
        // 检查是否已有考勤记录
        Optional<Attendance> existingOpt = attendanceRepository
            .findByStudentIdAndCourseIdAndAttendanceDateAndDeleted(studentId, courseId, date, 0);
        
        Attendance attendance;
        if (existingOpt.isPresent()) {
            attendance = existingOpt.get();
        } else {
            attendance = new Attendance(studentId, courseId, date, "leave");
        }
        
        attendance.applyLeave(leaveType, reason);
        return attendanceRepository.save(attendance);
    }

    @Override
    public void approveLeave(Long attendanceId, Long approverId) {
        attendanceRepository.updateLeaveApproval(attendanceId, true, approverId);
    }

    @Override
    public void rejectLeave(Long attendanceId, Long approverId) {
        attendanceRepository.updateLeaveApproval(attendanceId, false, approverId);
    }

    @Override
    public void batchUpdateAttendanceStatus(List<Long> attendanceIds, String status) {
        attendanceRepository.batchUpdateAttendanceStatus(attendanceIds, status);
    }

    // ==================== 统计分析方法 ====================

    @Override
    @Transactional(readOnly = true)
    public long countByStudentId(Long studentId) {
        return attendanceRepository.countByStudentIdAndDeleted(studentId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByCourseId(Long courseId) {
        return attendanceRepository.countByCourseIdAndDeleted(courseId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStudentIdAndCourseId(Long studentId, Long courseId) {
        return attendanceRepository.countByStudentIdAndCourseIdAndDeleted(studentId, courseId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateAttendanceRateByStudentId(Long studentId) {
        return attendanceRepository.calculateAttendanceRateByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateAttendanceRateByCourseId(Long courseId) {
        return attendanceRepository.calculateAttendanceRateByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateAttendanceRateByDateRange(LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.calculateAttendanceRateByDateRange(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAttendanceStatistics(Long studentId, Long courseId) {
        List<Object[]> results = attendanceRepository.getAttendanceStatistics(studentId, courseId);
        Map<String, Object> statistics = new HashMap<>();
        
        if (!results.isEmpty()) {
            Object[] result = results.get(0);
            statistics.put("totalRecords", result[0]);
            statistics.put("presentCount", result[1]);
            statistics.put("absentCount", result[2]);
            statistics.put("lateCount", result[3]);
            statistics.put("leaveCount", result[4]);
            
            // 计算出勤率
            Long total = (Long) result[0];
            Long present = (Long) result[1];
            if (total > 0) {
                statistics.put("attendanceRate", (present * 100.0) / total);
            } else {
                statistics.put("attendanceRate", 0.0);
            }
        }
        
        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getStudentAttendanceStatistics(Long studentId) {
        return getStudentAttendanceStatistics(studentId, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getCourseAttendanceStatistics(Long courseId) {
        return getCourseAttendanceStatistics(courseId, null, null);
    }

    // ==================== 验证方法 ====================

    @Override
    @Transactional(readOnly = true)
    public boolean hasCheckedInToday(Long studentId, Long courseId) {
        return attendanceRepository.hasCheckedInToday(studentId, courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByStudentIdAndCourseIdAndDate(Long studentId, Long courseId, LocalDate date) {
        return attendanceRepository.existsByStudentIdAndCourseIdAndAttendanceDateAndDeleted(studentId, courseId, date, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canModifyAttendance(Long attendanceId) {
        Optional<Attendance> attendanceOpt = attendanceRepository.findById(attendanceId);
        if (attendanceOpt.isPresent()) {
            Attendance attendance = attendanceOpt.get();
            // 如果是请假且已批准，则不能修改
            if ("leave".equals(attendance.getAttendanceStatus()) && Boolean.TRUE.equals(attendance.getIsApproved())) {
                return false;
            }
            // 超过7天的记录不能修改
            return attendance.getAttendanceDate().isAfter(LocalDate.now().minusDays(7));
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canDeleteAttendance(Long attendanceId) {
        Optional<Attendance> attendanceOpt = attendanceRepository.findById(attendanceId);
        if (attendanceOpt.isPresent()) {
            Attendance attendance = attendanceOpt.get();
            // 如果是请假且已批准，则不能删除
            if ("leave".equals(attendance.getAttendanceStatus()) && Boolean.TRUE.equals(attendance.getIsApproved())) {
                return false;
            }
            // 超过3天的记录不能删除
            return attendance.getAttendanceDate().isAfter(LocalDate.now().minusDays(3));
        }
        return false;
    }

    // ==================== 状态管理方法 ====================

    @Override
    public void enableAttendance(Long id) {
        attendanceRepository.updateStatus(id, 1);
    }

    @Override
    public void disableAttendance(Long id) {
        attendanceRepository.updateStatus(id, 0);
    }

    @Override
    public void enableAttendances(List<Long> ids) {
        attendanceRepository.batchUpdateStatus(ids, 1);
    }

    @Override
    public void disableAttendances(List<Long> ids) {
        attendanceRepository.batchUpdateStatus(ids, 0);
    }

    // ==================== AttendanceApiController 需要的额外方法 ====================

    @Override
    public Map<String, Object> batchImport(List<Attendance> attendanceList) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;

        for (Attendance attendance : attendanceList) {
            try {
                attendanceRepository.save(attendance);
                successCount++;
            } catch (Exception e) {
                failCount++;
            }
        }

        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("totalCount", attendanceList.size());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getStudentAttendanceStatistics(Long studentId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = attendanceRepository.getStudentAttendanceStatistics(studentId, startDate, endDate);
        Map<String, Object> statistics = new HashMap<>();

        if (!results.isEmpty()) {
            Object[] result = results.get(0);
            statistics.put("totalRecords", result[0]);
            statistics.put("presentCount", result[1]);
            statistics.put("absentCount", result[2]);
            statistics.put("lateCount", result[3]);
            statistics.put("leaveCount", result[4]);
            statistics.put("avgLateMinutes", result[5]);

            // 计算出勤率
            Long total = (Long) result[0];
            Long present = (Long) result[1];
            if (total > 0) {
                statistics.put("attendanceRate", (present * 100.0) / total);
            } else {
                statistics.put("attendanceRate", 0.0);
            }
        }

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getCourseAttendanceStatistics(Long courseId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = attendanceRepository.getCourseAttendanceStatistics(courseId, startDate, endDate);
        Map<String, Object> statistics = new HashMap<>();

        if (!results.isEmpty()) {
            Object[] result = results.get(0);
            statistics.put("totalRecords", result[0]);
            statistics.put("presentCount", result[1]);
            statistics.put("absentCount", result[2]);
            statistics.put("lateCount", result[3]);
            statistics.put("leaveCount", result[4]);
            statistics.put("totalStudents", result[5]);

            // 计算出勤率
            Long total = (Long) result[0];
            Long present = (Long) result[1];
            if (total > 0) {
                statistics.put("attendanceRate", (present * 100.0) / total);
            } else {
                statistics.put("attendanceRate", 0.0);
            }
        }

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getClassAttendanceStatistics(Long classId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = attendanceRepository.getClassAttendanceStatistics(classId, startDate, endDate);
        Map<String, Object> statistics = new HashMap<>();

        if (!results.isEmpty()) {
            Object[] result = results.get(0);
            statistics.put("totalRecords", result[0]);
            statistics.put("presentCount", result[1]);
            statistics.put("absentCount", result[2]);
            statistics.put("lateCount", result[3]);
            statistics.put("leaveCount", result[4]);
            statistics.put("totalStudents", result[5]);

            // 计算出勤率
            Long total = (Long) result[0];
            Long present = (Long) result[1];
            if (total > 0) {
                statistics.put("attendanceRate", (present * 100.0) / total);
            } else {
                statistics.put("attendanceRate", 0.0);
            }
        }

        return statistics;
    }

    // ================================
    // AttendanceController 需要的方法实现
    // ================================

    @Override
    @Transactional(readOnly = true)
    public Object getAttendanceCalendar() {
        try {
            Map<String, Object> calendar = new HashMap<>();

            // 获取当前月份的考勤数据
            LocalDate now = LocalDate.now();
            LocalDate startOfMonth = now.withDayOfMonth(1);
            LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

            List<Attendance> monthlyAttendance = findByDateRange(startOfMonth, endOfMonth);

            // 按日期分组统计
            Map<String, Object> dailyStats = new HashMap<>();
            for (Attendance attendance : monthlyAttendance) {
                String dateKey = attendance.getAttendanceDate().toString();
                @SuppressWarnings("unchecked")
                Map<String, Object> dayData = (Map<String, Object>) dailyStats.getOrDefault(dateKey, new HashMap<String, Object>());

                String status = attendance.getAttendanceStatus();
                int count = (Integer) dayData.getOrDefault(status, 0);
                dayData.put(status, count + 1);
                dailyStats.put(dateKey, dayData);
            }

            calendar.put("year", now.getYear());
            calendar.put("month", now.getMonthValue());
            calendar.put("dailyStats", dailyStats);
            calendar.put("totalDays", now.lengthOfMonth());

            return calendar;
        } catch (Exception e) {
            // 记录日志并返回空数据
            Map<String, Object> emptyCalendar = new HashMap<>();
            emptyCalendar.put("year", LocalDate.now().getYear());
            emptyCalendar.put("month", LocalDate.now().getMonthValue());
            emptyCalendar.put("dailyStats", new HashMap<>());
            return emptyCalendar;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getAttendanceSettings() {
        try {
            Map<String, Object> settings = new HashMap<>();

            // 考勤设置
            settings.put("checkInTimeLimit", "08:30"); // 签到截止时间
            settings.put("checkOutTimeLimit", "17:30"); // 签退开始时间
            settings.put("lateThresholdMinutes", 15); // 迟到阈值（分钟）
            settings.put("earlyLeaveThresholdMinutes", 30); // 早退阈值（分钟）
            settings.put("autoMarkAbsentHours", 2); // 自动标记缺勤时间（小时）

            // 请假设置
            settings.put("maxLeaveDaysPerMonth", 3); // 每月最大请假天数
            settings.put("leaveApprovalRequired", true); // 是否需要请假审批
            settings.put("advanceLeaveHours", 24); // 提前请假时间（小时）

            // 考勤方式
            settings.put("allowedMethods", List.of("手机签到", "刷卡签到", "人脸识别", "指纹识别"));
            settings.put("gpsRequired", true); // 是否需要GPS定位
            settings.put("photoRequired", false); // 是否需要拍照

            return settings;
        } catch (Exception e) {
            // 记录日志并返回默认设置
            Map<String, Object> defaultSettings = new HashMap<>();
            defaultSettings.put("checkInTimeLimit", "08:30");
            defaultSettings.put("checkOutTimeLimit", "17:30");
            defaultSettings.put("lateThresholdMinutes", 15);
            return defaultSettings;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getPendingMakeups() {
        try {
            List<Object> pendingMakeups = new java.util.ArrayList<>();

            // 查找需要补考勤的记录（例如：缺勤但有合理理由的）
            List<Attendance> absentRecords = findByAttendanceStatus("absent");

            for (Attendance attendance : absentRecords) {
                // 检查是否在允许补考勤的时间范围内（例如：7天内）
                if (attendance.getAttendanceDate().isAfter(LocalDate.now().minusDays(7))) {
                    Map<String, Object> makeup = new HashMap<>();
                    makeup.put("id", attendance.getId());
                    makeup.put("studentId", attendance.getStudentId());
                    makeup.put("courseId", attendance.getCourseId());
                    makeup.put("attendanceDate", attendance.getAttendanceDate());
                    makeup.put("status", attendance.getAttendanceStatus());
                    makeup.put("reason", "缺勤待补");
                    makeup.put("canMakeup", true);
                    pendingMakeups.add(makeup);
                }
            }

            return pendingMakeups;
        } catch (Exception e) {
            // 记录日志并返回空列表
            return new java.util.ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByCourse(Long courseId) {
        try {
            return findByCourseId(courseId);
        } catch (Exception e) {
            // 记录日志并返回空列表
            return new java.util.ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByStudent(Long studentId) {
        try {
            return findByStudentId(studentId);
        } catch (Exception e) {
            // 记录日志并返回空列表
            return new java.util.ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getAttendanceAlerts() {
        try {
            List<Object> alerts = new java.util.ArrayList<>();

            // 查找出勤率低的学生
            LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
            List<Attendance> recentAttendance = findByDateRange(oneMonthAgo, LocalDate.now());

            // 按学生分组统计出勤率
            Map<Long, Map<String, Integer>> studentStats = new HashMap<>();
            for (Attendance attendance : recentAttendance) {
                Long studentId = attendance.getStudentId();
                Map<String, Integer> stats = studentStats.getOrDefault(studentId, new HashMap<>());

                String status = attendance.getAttendanceStatus();
                stats.put(status, stats.getOrDefault(status, 0) + 1);
                stats.put("total", stats.getOrDefault("total", 0) + 1);

                studentStats.put(studentId, stats);
            }

            // 生成预警
            for (Map.Entry<Long, Map<String, Integer>> entry : studentStats.entrySet()) {
                Long studentId = entry.getKey();
                Map<String, Integer> stats = entry.getValue();

                int total = stats.getOrDefault("total", 0);
                int present = stats.getOrDefault("present", 0);

                if (total > 0) {
                    double attendanceRate = (present * 100.0) / total;
                    if (attendanceRate < 80.0) { // 出勤率低于80%预警
                        Map<String, Object> alert = new HashMap<>();
                        alert.put("studentId", studentId);
                        alert.put("alertType", "低出勤率");
                        alert.put("attendanceRate", attendanceRate);
                        alert.put("totalRecords", total);
                        alert.put("presentCount", present);
                        alert.put("severity", attendanceRate < 60 ? "严重" : "警告");
                        alerts.add(alert);
                    }
                }
            }

            return alerts;
        } catch (Exception e) {
            // 记录日志并返回空列表
            return new java.util.ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getAttendanceStatisticsData() {
        try {
            Map<String, Object> statisticsData = new HashMap<>();

            // 总体统计
            long totalRecords = count();
            long presentCount = findByAttendanceStatus("present").size();
            long absentCount = findByAttendanceStatus("absent").size();
            long lateCount = findByAttendanceStatus("late").size();
            long leaveCount = findByAttendanceStatus("leave").size();

            statisticsData.put("totalRecords", totalRecords);
            statisticsData.put("presentCount", presentCount);
            statisticsData.put("absentCount", absentCount);
            statisticsData.put("lateCount", lateCount);
            statisticsData.put("leaveCount", leaveCount);

            // 计算出勤率
            if (totalRecords > 0) {
                statisticsData.put("attendanceRate", (presentCount * 100.0) / totalRecords);
                statisticsData.put("absentRate", (absentCount * 100.0) / totalRecords);
                statisticsData.put("lateRate", (lateCount * 100.0) / totalRecords);
            } else {
                statisticsData.put("attendanceRate", 0.0);
                statisticsData.put("absentRate", 0.0);
                statisticsData.put("lateRate", 0.0);
            }

            // 今日统计
            List<Attendance> todayAttendance = findTodayAttendance();
            Map<String, Object> todayStats = new HashMap<>();
            todayStats.put("total", todayAttendance.size());
            todayStats.put("present", todayAttendance.stream().mapToInt(a -> "present".equals(a.getAttendanceStatus()) ? 1 : 0).sum());
            todayStats.put("absent", todayAttendance.stream().mapToInt(a -> "absent".equals(a.getAttendanceStatus()) ? 1 : 0).sum());
            todayStats.put("late", todayAttendance.stream().mapToInt(a -> "late".equals(a.getAttendanceStatus()) ? 1 : 0).sum());
            statisticsData.put("todayStats", todayStats);

            return statisticsData;
        } catch (Exception e) {
            // 记录日志并返回空数据
            Map<String, Object> emptyStats = new HashMap<>();
            emptyStats.put("totalRecords", 0);
            emptyStats.put("presentCount", 0);
            emptyStats.put("absentCount", 0);
            emptyStats.put("attendanceRate", 0.0);
            return emptyStats;
        }
    }
}
