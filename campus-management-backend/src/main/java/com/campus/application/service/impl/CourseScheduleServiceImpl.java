package com.campus.application.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.application.service.CourseScheduleService;
import com.campus.domain.entity.Course;
import com.campus.domain.entity.CourseSchedule;
import com.campus.domain.entity.CourseSelection;
import com.campus.domain.repository.CourseRepository;
import com.campus.domain.repository.CourseScheduleRepository;
import com.campus.domain.repository.CourseSelectionRepository;


/**
 * 课程表服务实现类 - JPA实现
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Service
public class CourseScheduleServiceImpl implements CourseScheduleService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    private CourseScheduleRepository courseScheduleRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseSelectionRepository courseSelectionRepository;

    @Override
    public List<CourseSchedule> findByCourseId(Long courseId) {
        return courseScheduleRepository.findByCourseIdAndDeleted(courseId, 0);
    }

    @Override
    public List<CourseSchedule> findByTeacherId(Long teacherId) {
        return courseScheduleRepository.findByTeacherIdAndDeleted(teacherId, 0);
    }

    @Override
    public List<CourseSchedule> findByClassId(Long classId) {
        // TODO: 需要重新实现班级ID查找逻辑
        return Collections.emptyList();
    }

    @Override
    public List<CourseSchedule> findBySemester(String semester) {
        return courseScheduleRepository.findBySemesterAndDeleted(semester, 0);
    }

    @Override
    public List<CourseSchedule> findByClassroom(String classroom) {
        // TODO: 需要重新实现教室名称查找逻辑，将教室名称转换为教室ID
        return Collections.emptyList();
    }

    @Override
    public Optional<Object[]> findScheduleDetailById(Long scheduleId) {
        return courseScheduleRepository.findScheduleDetailById(scheduleId);
    }

    @Override
    public boolean isClassroomOccupied(String classroom, Integer dayOfWeek, String startTime, String endTime, String semester, Long excludeId) {
        return courseScheduleRepository.isClassroomOccupied(classroom, dayOfWeek, startTime, endTime, semester, excludeId != null ? excludeId : 0L);
    }

    @Override
    public boolean isTeacherOccupied(Long teacherId, Integer dayOfWeek, String startTime, String endTime, String semester, Long excludeId) {
        return courseScheduleRepository.isTeacherOccupied(teacherId, dayOfWeek, startTime, endTime, semester, excludeId != null ? excludeId : 0L);
    }

    @Override
    public Page<CourseSchedule> findSchedulesByPage(int page, int size, Map<String, Object> params) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dayOfWeek").and(Sort.by("startTime")));

        // 简化实现，直接使用基础分页
        // 在实际项目中，可以根据params构建Specification进行条件查询
        return courseScheduleRepository.findAll(pageable);
    }

    @Override
    public long count() {
        return courseScheduleRepository.count();
    }

    @Override
    @Transactional
    public CourseSchedule createSchedule(CourseSchedule schedule) {
        // 检查课程是否存在
        Optional<Course> courseOpt = courseRepository.findById(schedule.getCourseId());
        if (courseOpt.isEmpty()) {
            throw new IllegalArgumentException("课程不存在：" + schedule.getCourseId());
        }

        // 将LocalTime转换为String格式
        String startTimeStr = schedule.getStartTime().format(TIME_FORMATTER);
        String endTimeStr = schedule.getEndTime().format(TIME_FORMATTER);

        // 检查教室是否被占用
        if (isClassroomOccupied(schedule.getClassroomId().toString(), schedule.getDayOfWeek(),
                startTimeStr, endTimeStr,
                schedule.getSemester(), null)) {
            throw new IllegalArgumentException("教室已被占用：" + schedule.getClassroomId());
        }

        // 检查教师是否有冲突的课程
        if (isTeacherOccupied(schedule.getTeacherId(), schedule.getDayOfWeek(),
                startTimeStr, endTimeStr,
                schedule.getSemester(), null)) {
            throw new IllegalArgumentException("教师在该时间段已有其他课程");
        }

        // 保存课程表信息
        return courseScheduleRepository.save(schedule);
    }

    @Override
    @Transactional
    public boolean updateSchedule(CourseSchedule schedule) {
        // 检查课程表是否存在
        Optional<CourseSchedule> existingScheduleOpt = courseScheduleRepository.findById(schedule.getId());
        if (existingScheduleOpt.isEmpty()) {
            return false;
        }
        // 检查课程是否存在
        Optional<Course> courseOpt = courseRepository.findById(schedule.getCourseId());
        if (courseOpt.isEmpty()) {
            throw new IllegalArgumentException("课程不存在：" + schedule.getCourseId());
        }

        // 将LocalTime转换为String格式
        String startTimeStr = schedule.getStartTime().format(TIME_FORMATTER);
        String endTimeStr = schedule.getEndTime().format(TIME_FORMATTER);

        // 检查教室是否被占用
        if (isClassroomOccupied(schedule.getClassroomId().toString(), schedule.getDayOfWeek(),
                startTimeStr, endTimeStr,
                schedule.getSemester(), schedule.getId())) {
            throw new IllegalArgumentException("教室已被占用：" + schedule.getClassroomId());
        }

        // 检查教师是否有冲突的课程
        if (isTeacherOccupied(schedule.getTeacherId(), schedule.getDayOfWeek(),
                startTimeStr, endTimeStr,
                schedule.getSemester(), schedule.getId())) {
            throw new IllegalArgumentException("教师在该时间段已有其他课程");
        }

        // 更新课程表信息
        CourseSchedule savedSchedule = courseScheduleRepository.save(schedule);
        return savedSchedule != null;
    }

    @Override
    @Transactional
    public boolean deleteSchedule(Long id) {
        // 检查课程表是否有关联的选课记录
        List<CourseSelection> selections = courseSelectionRepository.findByScheduleIdAndDeleted(id, 0);
        if (!selections.isEmpty()) {
            throw new IllegalStateException("课程表存在关联的选课记录，无法删除");
        }

        // 软删除课程表
        Optional<CourseSchedule> scheduleOpt = courseScheduleRepository.findById(id);
        if (scheduleOpt.isPresent()) {
            CourseSchedule schedule = scheduleOpt.get();
            schedule.setDeleted(1);
            courseScheduleRepository.save(schedule);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean batchDeleteSchedules(List<Long> ids) {
        // 检查课程表是否有关联的选课记录
        for (Long id : ids) {
            List<CourseSelection> selections = courseSelectionRepository.findByScheduleIdAndDeleted(id, 0);
            if (!selections.isEmpty()) {
                throw new IllegalStateException("课程表ID " + id + " 存在关联的选课记录，无法删除");
            }
        }

        // 批量软删除课程表
        List<CourseSchedule> schedules = courseScheduleRepository.findAllById(ids);
        for (CourseSchedule schedule : schedules) {
            schedule.setDeleted(1);
        }
        courseScheduleRepository.saveAll(schedules);
        return true;
    }

    @Override
    public List<CourseSchedule> findByCourseIdAndSemester(Long courseId, String semester) {
        return courseScheduleRepository.findByCourseIdAndSemesterAndDeleted(courseId, semester, 0);
    }

    @Override
    public List<CourseSchedule> findByTeacherIdAndSemester(Long teacherId, String semester) {
        return courseScheduleRepository.findByTeacherIdAndSemesterAndDeleted(teacherId, semester, 0);
    }

    @Override
    public List<CourseSchedule> findByClassIdAndSemester(Long classId, String semester) {
        // TODO: 需要重新实现班级ID查找逻辑
        return Collections.emptyList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> findPendingCourses() {
        // 查找没有排课的课程 - 使用findAll然后过滤
        List<Course> allCourses = courseRepository.findAll().stream()
            .filter(course -> course.getStatus() == 1 && course.getDeleted() == 0)
            .toList();

        // 筛选出没有排课的课程
        return allCourses.stream()
            .filter(course -> {
                List<CourseSchedule> schedules = courseScheduleRepository.findByCourseIdAndDeleted(course.getId(), 0);
                return schedules.isEmpty(); // 没有排课记录的课程
            })
            .toList();
    }

    @Override
    @Transactional
    public boolean autoScheduleCourse(Course course) {
        try {
            // 简单的自动排课逻辑
            // 这里可以实现更复杂的排课算法

            // 默认排课参数
            String[] timeSlots = {"08:00:00", "10:00:00", "14:00:00", "16:00:00"};
            String[] classrooms = {"A101", "A102", "A103", "B101", "B102", "B103"};
            String currentSemester = "2024-2025-1"; // 当前学期

            // 尝试为课程安排时间和教室
            for (int dayOfWeek = 1; dayOfWeek <= 5; dayOfWeek++) { // 周一到周五
                for (String startTime : timeSlots) {
                    for (String classroom : classrooms) {
                        String endTime = calculateEndTime(startTime, 2); // 假设每节课2小时

                        // 检查教室是否可用
                        if (!isClassroomOccupied(classroom, dayOfWeek, startTime, endTime, currentSemester, null)) {
                            // 检查教师是否可用（如果有教师ID）
                            if (course.getTeacherId() == null ||
                                !isTeacherOccupied(course.getTeacherId(), dayOfWeek, startTime, endTime, currentSemester, null)) {

                                // 创建课程表
                                CourseSchedule schedule = new CourseSchedule();
                                schedule.setCourseId(course.getId());
                                schedule.setTeacherId(course.getTeacherId());
                                // 这里需要设置教室ID，暂时使用1L作为示例
                                schedule.setClassroomId(1L); // 实际应该根据classroom名称查找对应的ID
                                schedule.setScheduleDate(java.time.LocalDate.now());
                                schedule.setStartTime(java.time.LocalTime.parse(startTime));
                                schedule.setEndTime(java.time.LocalTime.parse(endTime));
                                schedule.setSemester(currentSemester);
                                schedule.setStatus(1);

                                courseScheduleRepository.save(schedule);
                                return true;
                            }
                        }
                    }
                }
            }

            return false; // 无法安排课程

        } catch (Exception e) {
            throw new RuntimeException("自动排课失败: " + e.getMessage(), e);
        }
    }

    /**
     * 计算结束时间
     */
    private String calculateEndTime(String startTime, int durationHours) {
        java.time.LocalTime start = java.time.LocalTime.parse(startTime);
        java.time.LocalTime end = start.plusHours(durationHours);
        return end.format(TIME_FORMATTER);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSchedule> findSchedulesByStudent(Long studentId, Map<String, Object> params) {
        try {
            // 通过学生的选课记录查找课程表
            List<CourseSelection> selections = courseSelectionRepository.findByStudentIdAndDeleted(studentId, 0);

            // 获取学生选课的课程表ID列表
            List<Long> scheduleIds = selections.stream()
                .map(CourseSelection::getScheduleId)
                .filter(scheduleId -> scheduleId != null)
                .toList();

            if (scheduleIds.isEmpty()) {
                return List.of(); // 返回空列表
            }

            // 根据课程表ID查找课程表
            List<CourseSchedule> schedules = courseScheduleRepository.findAllById(scheduleIds)
                .stream()
                .filter(schedule -> schedule.getDeleted() == 0)
                .toList();

            // 根据参数过滤
            if (params != null && params.containsKey("semester")) {
                String semester = (String) params.get("semester");
                if (semester != null && !semester.isEmpty()) {
                    schedules = schedules.stream()
                        .filter(schedule -> semester.equals(schedule.getSemester()))
                        .toList();
                }
            }

            return schedules;
        } catch (Exception e) {
            System.err.println("查找学生课程表失败: " + e.getMessage());
            return List.of(); // 返回空列表
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseSchedule> findById(Long id) {
        return courseScheduleRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSchedule> findAll() {
        return courseScheduleRepository.findAll();
    }

    // ================================
    // Web控制器需要的方法实现
    // ================================

    @Override
    @Transactional(readOnly = true)
    public CourseSchedule getScheduleById(Long id) {
        return courseScheduleRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getScheduleCalendar() {
        // TODO: 实现课程表日历数据获取逻辑
        return Map.of("calendar", Collections.emptyList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTimetableData() {
        // TODO: 实现课程表数据获取逻辑
        return Map.of("timetable", Collections.emptyList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getScheduleConflicts() {
        // TODO: 实现课程表冲突检查逻辑
        return Collections.emptyList();
    }

    // ================================
    // API控制器需要的方法实现
    // ================================

    @Override
    @Transactional
    public CourseSchedule createCourseSchedule(CourseSchedule courseSchedule) {
        return createSchedule(courseSchedule);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CourseSchedule> findCourseScheduleById(Long id) {
        return findById(id);
    }

    @Override
    @Transactional
    public CourseSchedule updateCourseSchedule(CourseSchedule courseSchedule) {
        updateSchedule(courseSchedule);
        return courseSchedule;
    }

    @Override
    @Transactional
    public boolean deleteCourseSchedule(Long id) {
        return deleteSchedule(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseSchedule> findAllCourseSchedules(Pageable pageable) {
        return courseScheduleRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSchedule> findSchedulesByCourse(Long courseId) {
        return findByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSchedule> findSchedulesByTeacher(Long teacherId) {
        return findByTeacherId(teacherId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSchedule> findSchedulesByClassroom(Long classroomId) {
        return courseScheduleRepository.findByClassroomIdAndDeleted(classroomId, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSchedule> findSchedulesByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        return courseScheduleRepository.findByScheduleDateBetweenAndDeleted(startDate, endDate, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSchedule> findSchedulesByDayOfWeek(String dayOfWeek) {
        try {
            int day = Integer.parseInt(dayOfWeek);
            return courseScheduleRepository.findByDayOfWeekAndDeleted(day, 0);
        } catch (NumberFormatException e) {
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSchedule> checkScheduleConflicts(Long courseId, Long teacherId, Long classroomId, String dayOfWeek, String timeSlot) {
        // TODO: 实现课程安排冲突检查逻辑
        return Collections.emptyList();
    }

    @Override
    @Transactional
    public List<CourseSchedule> batchCreateSchedules(List<CourseSchedule> courseSchedules) {
        return courseScheduleRepository.saveAll(courseSchedules);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getTeacherWeeklySchedule(Long teacherId, java.time.LocalDate weekStart) {
        // TODO: 实现教师周课表获取逻辑
        return Map.of("weeklySchedule", Collections.emptyList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalSchedules() {
        return courseScheduleRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveSchedules() {
        return courseScheduleRepository.countByStatusAndDeleted(1, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> countSchedulesByDay() {
        // TODO: 实现按天统计课程安排数逻辑
        return Map.of();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> countSchedulesByTimeSlot() {
        // TODO: 实现按时间段统计课程安排数逻辑
        return Map.of();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSchedule> getTeacherSchedule(Long teacherId) {
        return findByTeacherId(teacherId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseSchedule> getClassroomSchedule(Long classroomId) {
        return findSchedulesByClassroom(classroomId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getScheduleTemplates() {
        // TODO: 实现课程表模板获取逻辑
        return Collections.emptyList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getScheduleStatistics() {
        // TODO: 实现课程表统计信息获取逻辑
        return Map.of("totalSchedules", countTotalSchedules(), "activeSchedules", countActiveSchedules());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getClassroomUtilizationStats() {
        // TODO: 实现教室利用率统计逻辑
        return Map.of("utilizationRate", 0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTeacherWorkloadStats() {
        // TODO: 实现教师工作量统计逻辑
        return Map.of("totalWorkload", 0, "averageWorkload", 0.0);
    }
}
