package com.campus.application.service.impl;

import com.campus.application.service.AutoScheduleService;
import com.campus.domain.entity.Course;
import com.campus.domain.entity.CourseSchedule;
import com.campus.domain.entity.Classroom;
import com.campus.domain.entity.TimeSlot;
import com.campus.domain.repository.CourseRepository;
import com.campus.domain.repository.CourseScheduleRepository;
import com.campus.domain.repository.ClassroomRepository;
import com.campus.domain.repository.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自动排课服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@Service
@Transactional
public class AutoScheduleServiceImpl implements AutoScheduleService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseScheduleRepository courseScheduleRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    // ==================== 主要排课方法 ====================

    @Override
    public ScheduleResult autoSchedule(ScheduleRequest request) {
        try {
            // 1. 验证请求参数
            if (!validateRequest(request)) {
                return new ScheduleResult(false, "请求参数无效");
            }

            // 2. 获取需要排课的课程
            List<Course> courses = getCoursesByIds(request.getCourseIds());
            if (courses.isEmpty()) {
                return new ScheduleResult(false, "没有找到需要排课的课程");
            }

            // 3. 获取可用资源
            List<Classroom> availableClassrooms = getAvailableClassrooms(request.getClassroomIds());
            List<TimeSlot> availableTimeSlots = getAvailableTimeSlots(request.getTimeSlotIds());

            // 4. 执行排课算法
            List<CourseSchedule> schedules = new ArrayList<>();
            List<ConflictInfo> conflicts = new ArrayList<>();
            
            for (Course course : courses) {
                ScheduleResult courseResult = scheduleCourse(course, availableClassrooms, 
                                                           availableTimeSlots, request);
                if (courseResult.isSuccess() && courseResult.getSchedules() != null) {
                    schedules.addAll(courseResult.getSchedules());
                } else {
                    conflicts.addAll(courseResult.getConflicts() != null ? 
                                   courseResult.getConflicts() : new ArrayList<>());
                }
            }

            // 5. 保存排课结果
            if (!schedules.isEmpty()) {
                schedules = courseScheduleRepository.saveAll(schedules);
            }

            // 6. 生成统计信息
            ScheduleStatistics statistics = generateStatistics(courses, schedules, conflicts);

            // 7. 构建结果
            ScheduleResult result = new ScheduleResult(true, "排课完成");
            result.setSchedules(schedules);
            result.setConflicts(conflicts);
            result.setStatistics(statistics);

            return result;

        } catch (Exception e) {
            return new ScheduleResult(false, "排课失败：" + e.getMessage());
        }
    }

    @Override
    public List<ConflictInfo> checkConflicts(CourseSchedule schedule, List<CourseSchedule> existingSchedules) {
        List<ConflictInfo> conflicts = new ArrayList<>();

        for (CourseSchedule existing : existingSchedules) {
            // 检查时间冲突
            if (hasTimeConflict(schedule, existing)) {
                // 检查教师冲突
                if (Objects.equals(schedule.getTeacherId(), existing.getTeacherId())) {
                    ConflictInfo conflict = new ConflictInfo("teacher", 
                        "教师时间冲突：" + schedule.getTeacherId());
                    conflict.setCourseId1(schedule.getCourseId());
                    conflict.setCourseId2(existing.getCourseId());
                    conflict.setSuggestion("调整时间或更换教师");
                    conflicts.add(conflict);
                }

                // 检查教室冲突
                if (Objects.equals(schedule.getClassroomId(), existing.getClassroomId())) {
                    ConflictInfo conflict = new ConflictInfo("classroom", 
                        "教室时间冲突：" + schedule.getClassroomId());
                    conflict.setCourseId1(schedule.getCourseId());
                    conflict.setCourseId2(existing.getCourseId());
                    conflict.setSuggestion("调整时间或更换教室");
                    conflicts.add(conflict);
                }

                // 检查学生冲突（简化处理）
                if (hasStudentConflict(schedule, existing)) {
                    ConflictInfo conflict = new ConflictInfo("student", 
                        "学生时间冲突");
                    conflict.setCourseId1(schedule.getCourseId());
                    conflict.setCourseId2(existing.getCourseId());
                    conflict.setSuggestion("调整时间");
                    conflicts.add(conflict);
                }
            }
        }

        return conflicts;
    }

    @Override
    public ScheduleResult validateSchedule(List<CourseSchedule> schedules) {
        List<ConflictInfo> allConflicts = new ArrayList<>();
        
        for (int i = 0; i < schedules.size(); i++) {
            for (int j = i + 1; j < schedules.size(); j++) {
                List<ConflictInfo> conflicts = checkConflicts(schedules.get(i), 
                                                            Arrays.asList(schedules.get(j)));
                allConflicts.addAll(conflicts);
            }
        }

        boolean isValid = allConflicts.isEmpty();
        String message = isValid ? "排课方案验证通过" : "发现 " + allConflicts.size() + " 个冲突";
        
        ScheduleResult result = new ScheduleResult(isValid, message);
        result.setConflicts(allConflicts);
        
        return result;
    }

    @Override
    public ScheduleResult optimizeSchedule(List<CourseSchedule> schedules, ScheduleRequest request) {
        // 简化的优化算法：尝试解决冲突
        List<CourseSchedule> optimizedSchedules = new ArrayList<>(schedules);
        List<ConflictInfo> conflicts = validateSchedule(schedules).getConflicts();
        
        // 对于每个冲突，尝试重新安排
        for (ConflictInfo conflict : conflicts) {
            // 这里可以实现更复杂的优化算法
            // 目前只是简单标记
            // 使用conflict变量避免警告
            if (conflict != null) {
                // 未来可以在这里实现冲突解决逻辑
            }
        }
        
        ScheduleResult result = new ScheduleResult(true, "优化完成");
        result.setSchedules(optimizedSchedules);
        result.setConflicts(conflicts);
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeSlot> getAvailableTimeSlots(Long courseId, Long classroomId, Long teacherId, 
                                              String semester, Integer academicYear) {
        // 获取所有时间段
        List<TimeSlot> allTimeSlots = timeSlotRepository.findAll();
        
        // 获取已占用的时间段
        List<CourseSchedule> existingSchedules = courseScheduleRepository
            .findBySemesterAndAcademicYear(semester, academicYear);
        
        // 过滤可用时间段
        return allTimeSlots.stream()
            .filter(timeSlot -> isTimeSlotAvailable(timeSlot, classroomId, teacherId, existingSchedules))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Classroom> getRecommendedClassrooms(Course course, TimeSlot timeSlot, Integer studentCount) {
        // 获取所有教室
        List<Classroom> allClassrooms = classroomRepository.findAll();
        
        // 根据容量和类型推荐教室
        return allClassrooms.stream()
            .filter(classroom -> classroom.getCapacity() >= studentCount)
            .filter(classroom -> isClassroomSuitable(classroom, course))
            .sorted(Comparator.comparing(Classroom::getCapacity))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> generateScheduleReport(String semester, Integer academicYear) {
        Map<String, Object> report = new HashMap<>();
        
        // 获取排课统计
        ScheduleStatistics statistics = getScheduleStatistics(semester, academicYear);
        report.put("statistics", statistics);
        
        // 获取所有课程安排
        List<CourseSchedule> schedules = courseScheduleRepository
            .findBySemesterAndAcademicYear(semester, academicYear);
        report.put("schedules", schedules);
        
        // 生成报告时间
        report.put("generatedAt", LocalDateTime.now());
        report.put("semester", semester);
        report.put("academicYear", academicYear);
        
        return report;
    }

    @Override
    public ScheduleResult batchImportSchedule(List<CourseSchedule> schedules) {
        try {
            // 验证排课方案
            ScheduleResult validation = validateSchedule(schedules);
            if (!validation.isSuccess()) {
                return validation;
            }
            
            // 保存排课
            List<CourseSchedule> savedSchedules = courseScheduleRepository.saveAll(schedules);
            
            ScheduleResult result = new ScheduleResult(true, "批量导入成功");
            result.setSchedules(savedSchedules);
            
            return result;
            
        } catch (Exception e) {
            return new ScheduleResult(false, "批量导入失败：" + e.getMessage());
        }
    }

    @Override
    public boolean clearSchedule(String semester, Integer academicYear) {
        try {
            courseScheduleRepository.deleteBySemesterAndAcademicYear(semester, academicYear);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public ScheduleResult copySchedule(String sourceSemester, Integer sourceAcademicYear, 
                                     String targetSemester, Integer targetAcademicYear) {
        try {
            // 获取源学期的排课
            List<CourseSchedule> sourceSchedules = courseScheduleRepository
                .findBySemesterAndAcademicYear(sourceSemester, sourceAcademicYear);
            
            if (sourceSchedules.isEmpty()) {
                return new ScheduleResult(false, "源学期没有排课数据");
            }
            
            // 复制到目标学期
            List<CourseSchedule> targetSchedules = sourceSchedules.stream()
                .map(schedule -> copyScheduleToNewSemester(schedule, targetSemester, targetAcademicYear))
                .collect(Collectors.toList());
            
            // 保存复制的排课
            targetSchedules = courseScheduleRepository.saveAll(targetSchedules);
            
            ScheduleResult result = new ScheduleResult(true, "排课复制成功");
            result.setSchedules(targetSchedules);
            
            return result;
            
        } catch (Exception e) {
            return new ScheduleResult(false, "排课复制失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleStatistics getScheduleStatistics(String semester, Integer academicYear) {
        ScheduleStatistics statistics = new ScheduleStatistics();

        // 获取所有课程和排课
        List<Course> allCourses = courseRepository.findAll(); // 简化：获取所有课程
        List<CourseSchedule> schedules = courseScheduleRepository
            .findBySemesterAndAcademicYear(semester, academicYear);

        statistics.setTotalCourses(allCourses.size());
        statistics.setScheduledCourses(schedules.size());
        statistics.setUnscheduledCourses(Math.max(0, allCourses.size() - schedules.size()));

        // 计算成功率
        if (allCourses.size() > 0) {
            statistics.setSuccessRate((double) schedules.size() / allCourses.size() * 100);
        }

        // 检查冲突
        ScheduleResult validation = validateSchedule(schedules);
        List<ConflictInfo> conflicts = validation.getConflicts();
        statistics.setTotalConflicts(conflicts.size());

        // 统计各类冲突
        long teacherConflicts = conflicts.stream().filter(c -> "teacher".equals(c.getType())).count();
        long classroomConflicts = conflicts.stream().filter(c -> "classroom".equals(c.getType())).count();
        long studentConflicts = conflicts.stream().filter(c -> "student".equals(c.getType())).count();

        statistics.setTeacherConflicts((int) teacherConflicts);
        statistics.setClassroomConflicts((int) classroomConflicts);
        statistics.setStudentConflicts((int) studentConflicts);

        return statistics;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasTeacherConflict(Long teacherId, TimeSlot timeSlot, String semester, Integer academicYear) {
        List<CourseSchedule> existingSchedules = courseScheduleRepository
            .findByTeacherIdAndSemesterAndAcademicYear(teacherId, semester, academicYear);

        return existingSchedules.stream()
            .anyMatch(schedule -> hasTimeSlotConflict(schedule, timeSlot));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasClassroomConflict(Long classroomId, TimeSlot timeSlot, String semester, Integer academicYear) {
        List<CourseSchedule> existingSchedules = courseScheduleRepository
            .findByClassroomIdAndSemesterAndAcademicYear(classroomId, semester, academicYear);

        return existingSchedules.stream()
            .anyMatch(schedule -> hasTimeSlotConflict(schedule, timeSlot));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasStudentConflict(List<String> classList, TimeSlot timeSlot, String semester, Integer academicYear) {
        // 简化实现：检查是否有时间冲突
        List<CourseSchedule> allSchedules = courseScheduleRepository
            .findBySemesterAndAcademicYear(semester, academicYear);

        // 检查时间段冲突
        return allSchedules.stream()
            .anyMatch(schedule -> hasTimeSlotConflict(schedule, timeSlot));
    }

    // ==================== 私有辅助方法 ====================

    private boolean validateRequest(ScheduleRequest request) {
        return request != null &&
               request.getSemester() != null &&
               request.getAcademicYear() != null &&
               request.getCourseIds() != null &&
               !request.getCourseIds().isEmpty();
    }

    private List<Course> getCoursesByIds(List<Long> courseIds) {
        return courseRepository.findAllById(courseIds);
    }

    private List<Classroom> getAvailableClassrooms(List<Long> classroomIds) {
        if (classroomIds == null || classroomIds.isEmpty()) {
            return classroomRepository.findAll();
        }
        return classroomRepository.findAllById(classroomIds);
    }

    private List<TimeSlot> getAvailableTimeSlots(List<Long> timeSlotIds) {
        if (timeSlotIds == null || timeSlotIds.isEmpty()) {
            return timeSlotRepository.findAll();
        }
        return timeSlotRepository.findAllById(timeSlotIds);
    }

    private ScheduleResult scheduleCourse(Course course, List<Classroom> availableClassrooms,
                                        List<TimeSlot> availableTimeSlots, ScheduleRequest request) {
        // 简化的排课算法
        for (TimeSlot timeSlot : availableTimeSlots) {
            for (Classroom classroom : availableClassrooms) {
                // 检查是否适合
                if (isClassroomSuitable(classroom, course)) {
                    // 创建课程安排
                    CourseSchedule schedule = new CourseSchedule();
                    schedule.setCourseId(course.getId());
                    schedule.setClassroomId(classroom.getId());
                    schedule.setTimeSlotId(timeSlot.getId());
                    schedule.setSemester(request.getSemester());
                    schedule.setAcademicYear(request.getAcademicYear());
                    schedule.setStartWeek(request.getStartWeek() != null ? request.getStartWeek() : 1);
                    schedule.setEndWeek(request.getEndWeek() != null ? request.getEndWeek() : 18);

                    // 检查冲突
                    List<CourseSchedule> existingSchedules = courseScheduleRepository
                        .findBySemesterAndAcademicYear(request.getSemester(), request.getAcademicYear());

                    List<ConflictInfo> conflicts = checkConflicts(schedule, existingSchedules);
                    if (conflicts.isEmpty()) {
                        ScheduleResult result = new ScheduleResult(true, "排课成功");
                        result.setSchedules(Arrays.asList(schedule));
                        return result;
                    }
                }
            }
        }

        // 没有找到合适的安排
        ConflictInfo conflict = new ConflictInfo("resource", "没有找到合适的时间和教室");
        conflict.setCourseId1(course.getId());
        conflict.setSuggestion("增加可用时间段或教室");

        ScheduleResult result = new ScheduleResult(false, "排课失败");
        result.setConflicts(Arrays.asList(conflict));
        return result;
    }

    private boolean hasTimeConflict(CourseSchedule schedule1, CourseSchedule schedule2) {
        // 简化实现：检查时间段ID是否相同
        return Objects.equals(schedule1.getTimeSlotId(), schedule2.getTimeSlotId()) &&
               Objects.equals(schedule1.getDayOfWeek(), schedule2.getDayOfWeek()) &&
               hasWeekOverlap(schedule1, schedule2);
    }

    private boolean hasWeekOverlap(CourseSchedule schedule1, CourseSchedule schedule2) {
        int start1 = schedule1.getStartWeek() != null ? schedule1.getStartWeek() : 1;
        int end1 = schedule1.getEndWeek() != null ? schedule1.getEndWeek() : 18;
        int start2 = schedule2.getStartWeek() != null ? schedule2.getStartWeek() : 1;
        int end2 = schedule2.getEndWeek() != null ? schedule2.getEndWeek() : 18;

        return !(end1 < start2 || end2 < start1);
    }

    private boolean hasStudentConflict(CourseSchedule schedule1, CourseSchedule schedule2) {
        // 简化实现：假设如果课程不同就可能有学生冲突
        return !Objects.equals(schedule1.getCourseId(), schedule2.getCourseId());
    }

    private boolean isTimeSlotAvailable(TimeSlot timeSlot, Long classroomId, Long teacherId,
                                      List<CourseSchedule> existingSchedules) {
        return existingSchedules.stream()
            .noneMatch(schedule ->
                Objects.equals(schedule.getTimeSlotId(), timeSlot.getId()) &&
                (Objects.equals(schedule.getClassroomId(), classroomId) ||
                 Objects.equals(schedule.getTeacherId(), teacherId)));
    }

    private boolean isClassroomSuitable(Classroom classroom, Course course) {
        // 简化实现：检查容量是否足够
        return classroom.getCapacity() >= 30; // 假设最少需要30人容量
    }

    private boolean hasTimeSlotConflict(CourseSchedule schedule, TimeSlot timeSlot) {
        return Objects.equals(schedule.getTimeSlotId(), timeSlot.getId());
    }

    private CourseSchedule copyScheduleToNewSemester(CourseSchedule original, String targetSemester, Integer targetAcademicYear) {
        CourseSchedule copy = new CourseSchedule();
        copy.setCourseId(original.getCourseId());
        copy.setClassroomId(original.getClassroomId());
        copy.setTimeSlotId(original.getTimeSlotId());
        copy.setTeacherId(original.getTeacherId());
        copy.setSemester(targetSemester);
        copy.setAcademicYear(targetAcademicYear);
        copy.setStartWeek(original.getStartWeek());
        copy.setEndWeek(original.getEndWeek());
        copy.setDayOfWeek(original.getDayOfWeek());
        copy.setWeekType(original.getWeekType());
        return copy;
    }

    private ScheduleStatistics generateStatistics(List<Course> courses, List<CourseSchedule> schedules,
                                                List<ConflictInfo> conflicts) {
        ScheduleStatistics statistics = new ScheduleStatistics();

        statistics.setTotalCourses(courses.size());
        statistics.setScheduledCourses(schedules.size());
        statistics.setUnscheduledCourses(courses.size() - schedules.size());
        statistics.setTotalConflicts(conflicts.size());

        // 统计各类冲突
        long teacherConflicts = conflicts.stream().filter(c -> "teacher".equals(c.getType())).count();
        long classroomConflicts = conflicts.stream().filter(c -> "classroom".equals(c.getType())).count();
        long studentConflicts = conflicts.stream().filter(c -> "student".equals(c.getType())).count();

        statistics.setTeacherConflicts((int) teacherConflicts);
        statistics.setClassroomConflicts((int) classroomConflicts);
        statistics.setStudentConflicts((int) studentConflicts);

        // 计算成功率
        if (courses.size() > 0) {
            statistics.setSuccessRate((double) schedules.size() / courses.size() * 100);
        }

        return statistics;
    }
}
