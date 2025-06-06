package com.campus.application.service.impl;

import java.time.format.DateTimeFormatter;
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
        return courseScheduleRepository.findByClassIdAndDeleted(classId, 0);
    }

    @Override
    public List<CourseSchedule> findBySemester(String semester) {
        return courseScheduleRepository.findBySemesterAndDeleted(semester, 0);
    }

    @Override
    public List<CourseSchedule> findByClassroom(String classroom) {
        return courseScheduleRepository.findByClassroomAndDeleted(classroom, 0);
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
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("dayOfWeek").and(Sort.by("startTime")));

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
        if (isClassroomOccupied(schedule.getClassroom(), schedule.getDayOfWeek(),
                startTimeStr, endTimeStr,
                schedule.getSemester(), null)) {
            throw new IllegalArgumentException("教室已被占用：" + schedule.getClassroom());
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
        if (isClassroomOccupied(schedule.getClassroom(), schedule.getDayOfWeek(),
                startTimeStr, endTimeStr,
                schedule.getSemester(), schedule.getId())) {
            throw new IllegalArgumentException("教室已被占用：" + schedule.getClassroom());
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
        return courseScheduleRepository.findByClassIdAndSemesterAndDeleted(classId, semester, 0);
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
                                schedule.setClassroom(classroom);
                                schedule.setDayOfWeek(dayOfWeek);
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
}
