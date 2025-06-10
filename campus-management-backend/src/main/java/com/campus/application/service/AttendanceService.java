package com.campus.application.service;

import com.campus.domain.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 考勤管理服务接口
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
public interface AttendanceService {

    // ==================== 基础CRUD方法 ====================

    /**
     * 保存考勤记录
     */
    Attendance save(Attendance attendance);

    /**
     * 根据ID查找考勤记录
     */
    Optional<Attendance> findById(Long id);

    /**
     * 获取所有考勤记录
     */
    List<Attendance> findAll();

    /**
     * 分页获取考勤记录
     */
    Page<Attendance> findAll(Pageable pageable);

    /**
     * 删除考勤记录
     */
    void deleteById(Long id);

    /**
     * 批量删除考勤记录
     */
    void deleteByIds(List<Long> ids);

    /**
     * 统计考勤记录数量
     */
    long count();

    // ==================== 业务查询方法 ====================

    /**
     * 根据学生ID查找考勤记录
     */
    List<Attendance> findByStudentId(Long studentId);

    /**
     * 根据课程ID查找考勤记录
     */
    List<Attendance> findByCourseId(Long courseId);

    /**
     * 根据学生ID和课程ID查找考勤记录
     */
    List<Attendance> findByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * 根据考勤日期查找考勤记录
     */
    List<Attendance> findByAttendanceDate(LocalDate attendanceDate);

    /**
     * 根据考勤状态查找考勤记录
     */
    List<Attendance> findByAttendanceStatus(String attendanceStatus);

    /**
     * 根据日期范围查找考勤记录
     */
    List<Attendance> findByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 查找需要审批的请假记录
     */
    List<Attendance> findPendingLeaveRequests();

    /**
     * 查找今日考勤记录
     */
    List<Attendance> findTodayAttendance();

    /**
     * 查找本周考勤记录
     */
    List<Attendance> findWeeklyAttendance(LocalDate startOfWeek, LocalDate endOfWeek);

    /**
     * 查找本月考勤记录
     */
    List<Attendance> findMonthlyAttendance(int year, int month);

    // ==================== 分页查询方法 ====================

    /**
     * 根据学生ID分页查找考勤记录
     */
    Page<Attendance> findByStudentId(Long studentId, Pageable pageable);

    /**
     * 根据课程ID分页查找考勤记录
     */
    Page<Attendance> findByCourseId(Long courseId, Pageable pageable);

    /**
     * 根据条件分页查询考勤记录
     */
    Page<Attendance> findByConditions(Long studentId, Long courseId, String attendanceStatus, 
                                    LocalDate startDate, LocalDate endDate, Pageable pageable);

    // ==================== 业务操作方法 ====================

    /**
     * 学生签到
     */
    Attendance checkIn(Long studentId, Long courseId, String method, String location, String device);

    /**
     * 学生签退
     */
    void checkOut(Long attendanceId);

    /**
     * 标记迟到
     */
    void markLate(Long attendanceId, int minutes);

    /**
     * 标记早退
     */
    void markEarlyLeave(Long attendanceId, int minutes);

    /**
     * 申请请假
     */
    Attendance applyLeave(Long studentId, Long courseId, LocalDate date, String leaveType, String reason);

    /**
     * 批准请假
     */
    void approveLeave(Long attendanceId, Long approverId);

    /**
     * 拒绝请假
     */
    void rejectLeave(Long attendanceId, Long approverId);

    /**
     * 批量标记考勤状态
     */
    void batchUpdateAttendanceStatus(List<Long> attendanceIds, String status);

    // ==================== 统计分析方法 ====================

    /**
     * 统计学生考勤数量
     */
    long countByStudentId(Long studentId);

    /**
     * 统计课程考勤数量
     */
    long countByCourseId(Long courseId);

    /**
     * 统计学生和课程的考勤数量
     */
    long countByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * 计算学生出勤率
     */
    Double calculateAttendanceRateByStudentId(Long studentId);

    /**
     * 计算课程出勤率
     */
    Double calculateAttendanceRateByCourseId(Long courseId);

    /**
     * 计算指定时间范围内的出勤率
     */
    Double calculateAttendanceRateByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 获取考勤统计信息
     */
    Map<String, Object> getAttendanceStatistics(Long studentId, Long courseId);

    /**
     * 获取学生考勤统计
     */
    Map<String, Object> getStudentAttendanceStatistics(Long studentId);

    /**
     * 获取课程考勤统计
     */
    Map<String, Object> getCourseAttendanceStatistics(Long courseId);

    // ==================== 验证方法 ====================

    /**
     * 检查学生今日是否已签到
     */
    boolean hasCheckedInToday(Long studentId, Long courseId);

    /**
     * 检查考勤记录是否存在
     */
    boolean existsByStudentIdAndCourseIdAndDate(Long studentId, Long courseId, LocalDate date);

    /**
     * 检查是否可以修改考勤记录
     */
    boolean canModifyAttendance(Long attendanceId);

    /**
     * 检查是否可以删除考勤记录
     */
    boolean canDeleteAttendance(Long attendanceId);

    // ==================== 状态管理方法 ====================

    /**
     * 启用考勤记录
     */
    void enableAttendance(Long id);

    /**
     * 禁用考勤记录
     */
    void disableAttendance(Long id);

    /**
     * 批量启用考勤记录
     */
    void enableAttendances(List<Long> ids);

    /**
     * 批量禁用考勤记录
     */
    void disableAttendances(List<Long> ids);

    // ==================== AttendanceApiController 需要的额外方法 ====================

    /**
     * 批量导入考勤记录
     */
    Map<String, Object> batchImport(List<Attendance> attendanceList);

    /**
     * 获取学生考勤统计（带日期范围）
     */
    Map<String, Object> getStudentAttendanceStatistics(Long studentId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取课程考勤统计（带日期范围）
     */
    Map<String, Object> getCourseAttendanceStatistics(Long courseId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取班级考勤统计
     */
    Map<String, Object> getClassAttendanceStatistics(Long classId, LocalDate startDate, LocalDate endDate);

    // ================================
    // AttendanceController 需要的方法
    // ================================

    /**
     * 获取考勤日历数据
     */
    Object getAttendanceCalendar();

    /**
     * 获取考勤设置
     */
    Object getAttendanceSettings();

    /**
     * 获取待补考勤记录
     */
    List<Object> getPendingMakeups();

    /**
     * 根据课程获取考勤记录
     */
    List<Attendance> getAttendanceByCourse(Long courseId);

    /**
     * 根据学生获取考勤记录
     */
    List<Attendance> getAttendanceByStudent(Long studentId);

    /**
     * 获取考勤预警数据
     */
    List<Object> getAttendanceAlerts();

    /**
     * 获取考勤统计数据
     */
    Object getAttendanceStatisticsData();
}
