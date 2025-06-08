package com.campus.application.service.impl;

import com.campus.application.service.AttendanceService;
import com.campus.domain.entity.Attendance;
import com.campus.domain.repository.AttendanceRepository;
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
        LocalDate today = LocalDate.now();
        
        // 检查是否已有今日考勤记录
        Optional<Attendance> existingOpt = attendanceRepository
            .findByStudentIdAndCourseIdAndAttendanceDateAndDeleted(studentId, courseId, today, 0);
        
        Attendance attendance;
        if (existingOpt.isPresent()) {
            attendance = existingOpt.get();
        } else {
            attendance = new Attendance(studentId, courseId, today, "present");
        }
        
        attendance.checkIn(method, location, device);
        return attendanceRepository.save(attendance);
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
}
