package com.campus.application.service.impl;

import com.campus.application.service.AutoScheduleService;
import com.campus.application.service.CourseService;
import com.campus.domain.entity.*;
import com.campus.domain.repository.CourseScheduleRepository;
import com.campus.domain.repository.ClassroomRepository;
import com.campus.domain.repository.TimeSlotRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 自动排课服务实现类
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Service
@Transactional
public class AutoScheduleServiceImpl implements AutoScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(AutoScheduleServiceImpl.class);

    @Autowired
    private CourseScheduleRepository courseScheduleRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private CourseService courseService;

    @Override
    public ScheduleResult autoSchedule(ScheduleRequest request) {
        logger.info("开始自动排课: 学期={}, 学年={}, 课程数={}", 
                   request.getSemester(), request.getAcademicYear(), 
                   request.getCourseIds() != null ? request.getCourseIds().size() : 0);

        try {
            // 1. 验证输入参数
            if (!validateRequest(request)) {
                return new ScheduleResult(false, "排课参数验证失败");
            }

            // 2. 获取基础数据
            List<Course> courses = getCourses(request.getCourseIds());
            List<Classroom> classrooms = getClassrooms(request.getClassroomIds());
            List<TimeSlot> timeSlots = getTimeSlots(request.getTimeSlotIds());
            List<CourseSchedule> existingSchedules = getExistingSchedules(request.getSemester(), request.getAcademicYear());

            // 3. 执行排课算法
            List<CourseSchedule> newSchedules = new ArrayList<>();
            List<ConflictInfo> conflicts = new ArrayList<>();
            
            for (Course course : courses) {
                ScheduleResult courseResult = scheduleCourse(course, classrooms, timeSlots, 
                                                           existingSchedules, newSchedules, request);
                if (courseResult.getSchedules() != null) {
                    newSchedules.addAll(courseResult.getSchedules());
                    existingSchedules.addAll(courseResult.getSchedules());
                }
                if (courseResult.getConflicts() != null) {
                    conflicts.addAll(courseResult.getConflicts());
                }
            }

            // 4. 保存排课结果
            if (!newSchedules.isEmpty()) {
                courseScheduleRepository.saveAll(newSchedules);
            }

            // 5. 生成统计信息
            ScheduleStatistics statistics = generateStatistics(courses, newSchedules, conflicts);

            // 6. 返回结果
            ScheduleResult result = new ScheduleResult(true, "自动排课完成");
            result.setSchedules(newSchedules);
            result.setConflicts(conflicts);
            result.setStatistics(statistics);

            logger.info("自动排课完成: 成功排课{}门，冲突{}个", 
                       newSchedules.size(), conflicts.size());

            return result;

        } catch (Exception e) {
            logger.error("自动排课失败: {}", e.getMessage(), e);
            return new ScheduleResult(false, "自动排课失败: " + e.getMessage());
        }
    }

    @Override
    public List<ConflictInfo> checkConflicts(CourseSchedule schedule, List<CourseSchedule> existingSchedules) {
        List<ConflictInfo> conflicts = new ArrayList<>();

        for (CourseSchedule existing : existingSchedules) {
            if (schedule.conflictsWith(existing)) {
                ConflictInfo conflict = new ConflictInfo();
                
                // 判断冲突类型
                if (schedule.getTeacherId().equals(existing.getTeacherId())) {
                    conflict.setType("teacher");
                    conflict.setDescription("教师时间冲突");
                } else if (schedule.getClassroomId().equals(existing.getClassroomId())) {
                    conflict.setType("classroom");
                    conflict.setDescription("教室时间冲突");
                } else {
                    conflict.setType("student");
                    conflict.setDescription("学生时间冲突");
                }
                
                conflict.setCourseId1(schedule.getCourseId());
                conflict.setCourseId2(existing.getCourseId());
                conflict.setSuggestion("建议调整时间或教室");
                
                conflicts.add(conflict);
            }
        }

        return conflicts;
    }

    @Override
    public ScheduleResult validateSchedule(List<CourseSchedule> schedules) {
        List<ConflictInfo> allConflicts = new ArrayList<>();
        
        for (int i = 0; i < schedules.size(); i++) {
            for (int j = i + 1; j < schedules.size(); j++) {
                CourseSchedule schedule1 = schedules.get(i);
                CourseSchedule schedule2 = schedules.get(j);
                
                if (schedule1.conflictsWith(schedule2)) {
                    ConflictInfo conflict = new ConflictInfo();
                    conflict.setType("schedule");
                    conflict.setDescription("课程安排冲突");
                    conflict.setCourseId1(schedule1.getCourseId());
                    conflict.setCourseId2(schedule2.getCourseId());
                    allConflicts.add(conflict);
                }
            }
        }

        ScheduleResult result = new ScheduleResult(allConflicts.isEmpty(), 
                                                 allConflicts.isEmpty() ? "验证通过" : "发现冲突");
        result.setConflicts(allConflicts);
        return result;
    }

    @Override
    public ScheduleResult optimizeSchedule(List<CourseSchedule> schedules, ScheduleRequest request) {
        // 简化的优化实现
        logger.info("开始优化排课方案，共{}个安排", schedules.size());
        
        // 按优先级重新排序
        schedules.sort((s1, s2) -> {
            // 优先级：时间段早的优先，教室容量大的优先
            int timeCompare = Integer.compare(s1.getPeriodNumber(), s2.getPeriodNumber());
            if (timeCompare != 0) return timeCompare;
            
            return Integer.compare(s1.getDayOfWeek(), s2.getDayOfWeek());
        });

        ScheduleResult result = new ScheduleResult(true, "优化完成");
        result.setSchedules(schedules);
        return result;
    }

    @Override
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
    public List<Classroom> getRecommendedClassrooms(Course course, TimeSlot timeSlot, Integer studentCount) {
        List<Classroom> allClassrooms = classroomRepository.findAll();
        
        return allClassrooms.stream()
            .filter(classroom -> classroom.hasEnoughCapacity(studentCount))
            .filter(classroom -> classroom.isSuitableForCourseType(course.getCourseType()))
            .sorted((c1, c2) -> Integer.compare(c1.getCapacity(), c2.getCapacity()))
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> generateScheduleReport(String semester, Integer academicYear) {
        Map<String, Object> report = new HashMap<>();
        
        List<CourseSchedule> schedules = courseScheduleRepository
            .findBySemesterAndAcademicYear(semester, academicYear);
        
        report.put("totalSchedules", schedules.size());
        report.put("semester", semester);
        report.put("academicYear", academicYear);
        
        // 按星期统计
        Map<Integer, Long> schedulesByDay = schedules.stream()
            .collect(Collectors.groupingBy(CourseSchedule::getDayOfWeek, Collectors.counting()));
        report.put("schedulesByDay", schedulesByDay);
        
        // 按教室统计
        Map<Long, Long> schedulesByClassroom = schedules.stream()
            .collect(Collectors.groupingBy(CourseSchedule::getClassroomId, Collectors.counting()));
        report.put("schedulesByClassroom", schedulesByClassroom);
        
        return report;
    }

    @Override
    public ScheduleResult batchImportSchedule(List<CourseSchedule> schedules) {
        try {
            // 验证导入数据
            ScheduleResult validation = validateSchedule(schedules);
            if (!validation.isSuccess()) {
                return validation;
            }
            
            // 保存数据
            courseScheduleRepository.saveAll(schedules);
            
            ScheduleResult result = new ScheduleResult(true, "批量导入成功");
            result.setSchedules(schedules);
            return result;
            
        } catch (Exception e) {
            logger.error("批量导入排课失败: {}", e.getMessage(), e);
            return new ScheduleResult(false, "批量导入失败: " + e.getMessage());
        }
    }

    @Override
    public boolean clearSchedule(String semester, Integer academicYear) {
        try {
            courseScheduleRepository.deleteBySemesterAndAcademicYear(semester, academicYear);
            logger.info("清空排课成功: 学期={}, 学年={}", semester, academicYear);
            return true;
        } catch (Exception e) {
            logger.error("清空排课失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public ScheduleResult copySchedule(String sourceSemester, Integer sourceAcademicYear, 
                                      String targetSemester, Integer targetAcademicYear) {
        try {
            List<CourseSchedule> sourceSchedules = courseScheduleRepository
                .findBySemesterAndAcademicYear(sourceSemester, sourceAcademicYear);
            
            List<CourseSchedule> newSchedules = sourceSchedules.stream()
                .map(this::cloneSchedule)
                .peek(schedule -> {
                    schedule.setSemester(targetSemester);
                    schedule.setAcademicYear(targetAcademicYear);
                    schedule.setId(null); // 新记录
                })
                .collect(Collectors.toList());
            
            courseScheduleRepository.saveAll(newSchedules);
            
            ScheduleResult result = new ScheduleResult(true, "复制排课成功");
            result.setSchedules(newSchedules);
            return result;
            
        } catch (Exception e) {
            logger.error("复制排课失败: {}", e.getMessage(), e);
            return new ScheduleResult(false, "复制排课失败: " + e.getMessage());
        }
    }

    @Override
    public ScheduleStatistics getScheduleStatistics(String semester, Integer academicYear) {
        List<CourseSchedule> schedules = courseScheduleRepository
            .findBySemesterAndAcademicYear(semester, academicYear);
        
        ScheduleStatistics statistics = new ScheduleStatistics();
        statistics.setTotalCourses(schedules.size());
        statistics.setScheduledCourses(schedules.size());
        statistics.setUnscheduledCourses(0);
        
        // 计算冲突
        List<ConflictInfo> conflicts = new ArrayList<>();
        for (int i = 0; i < schedules.size(); i++) {
            for (int j = i + 1; j < schedules.size(); j++) {
                if (schedules.get(i).conflictsWith(schedules.get(j))) {
                    ConflictInfo conflict = new ConflictInfo();
                    conflicts.add(conflict);
                }
            }
        }
        
        statistics.setTotalConflicts(conflicts.size());
        statistics.setSuccessRate(schedules.isEmpty() ? 0.0 : 
                                 (double)(schedules.size() - conflicts.size()) / schedules.size() * 100);
        
        return statistics;
    }

    @Override
    public boolean hasTeacherConflict(Long teacherId, TimeSlot timeSlot, String semester, Integer academicYear) {
        List<CourseSchedule> teacherSchedules = courseScheduleRepository
            .findByTeacherIdAndSemesterAndAcademicYear(teacherId, semester, academicYear);
        
        return teacherSchedules.stream()
            .anyMatch(schedule -> schedule.getDayOfWeek().equals(timeSlot.getDayOfWeek()) 
                                && schedule.getPeriodNumber().equals(timeSlot.getPeriodNumber()));
    }

    @Override
    public boolean hasClassroomConflict(Long classroomId, TimeSlot timeSlot, String semester, Integer academicYear) {
        List<CourseSchedule> classroomSchedules = courseScheduleRepository
            .findByClassroomIdAndSemesterAndAcademicYear(classroomId, semester, academicYear);
        
        return classroomSchedules.stream()
            .anyMatch(schedule -> schedule.getDayOfWeek().equals(timeSlot.getDayOfWeek()) 
                                && schedule.getPeriodNumber().equals(timeSlot.getPeriodNumber()));
    }

    @Override
    public boolean hasStudentConflict(List<String> classList, TimeSlot timeSlot, String semester, Integer academicYear) {
        // 简化实现，实际应该查询选课记录
        List<CourseSchedule> allSchedules = courseScheduleRepository
            .findBySemesterAndAcademicYear(semester, academicYear);
        
        return allSchedules.stream()
            .filter(schedule -> schedule.getDayOfWeek().equals(timeSlot.getDayOfWeek()) 
                              && schedule.getPeriodNumber().equals(timeSlot.getPeriodNumber()))
            .anyMatch(schedule -> hasClassListOverlap(classList, schedule.getClassArray()));
    }

    // ================================
    // 私有辅助方法
    // ================================

    private boolean validateRequest(ScheduleRequest request) {
        return request.getSemester() != null && 
               request.getAcademicYear() != null && 
               request.getCourseIds() != null && 
               !request.getCourseIds().isEmpty();
    }

    private List<Course> getCourses(List<Long> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) {
            return new ArrayList<>();
        }
        return courseIds.stream()
            .map(courseService::findById)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    private List<Classroom> getClassrooms(List<Long> classroomIds) {
        if (classroomIds == null || classroomIds.isEmpty()) {
            return classroomRepository.findAll();
        }
        return classroomRepository.findAllById(classroomIds);
    }

    private List<TimeSlot> getTimeSlots(List<Long> timeSlotIds) {
        if (timeSlotIds == null || timeSlotIds.isEmpty()) {
            return timeSlotRepository.findAll();
        }
        return timeSlotRepository.findAllById(timeSlotIds);
    }

    private List<CourseSchedule> getExistingSchedules(String semester, Integer academicYear) {
        return courseScheduleRepository.findBySemesterAndAcademicYear(semester, academicYear);
    }

    private ScheduleResult scheduleCourse(Course course, List<Classroom> classrooms, 
                                        List<TimeSlot> timeSlots, List<CourseSchedule> existingSchedules,
                                        List<CourseSchedule> newSchedules, ScheduleRequest request) {
        
        // 简化的排课逻辑：为课程找到第一个可用的时间和教室
        for (TimeSlot timeSlot : timeSlots) {
            for (Classroom classroom : classrooms) {
                if (isSlotAvailable(course, classroom, timeSlot, existingSchedules, newSchedules)) {
                    CourseSchedule schedule = createSchedule(course, classroom, timeSlot, request);
                    
                    ScheduleResult result = new ScheduleResult(true, "排课成功");
                    result.setSchedules(Arrays.asList(schedule));
                    return result;
                }
            }
        }
        
        // 没有找到合适的时间段
        ScheduleResult result = new ScheduleResult(false, "无法为课程找到合适的时间和教室");
        ConflictInfo conflict = new ConflictInfo("schedule", "无可用时间段");
        conflict.setCourseId1(course.getId());
        result.setConflicts(Arrays.asList(conflict));
        return result;
    }

    private boolean isSlotAvailable(Course course, Classroom classroom, TimeSlot timeSlot,
                                   List<CourseSchedule> existingSchedules, List<CourseSchedule> newSchedules) {
        
        // 检查教室容量
        if (!classroom.hasEnoughCapacity(course.getMaxStudents())) {
            return false;
        }
        
        // 检查教室类型适配
        if (!classroom.isSuitableForCourseType(course.getCourseType())) {
            return false;
        }
        
        // 检查时间冲突
        List<CourseSchedule> allSchedules = new ArrayList<>(existingSchedules);
        allSchedules.addAll(newSchedules);
        
        for (CourseSchedule schedule : allSchedules) {
            if (schedule.getDayOfWeek().equals(timeSlot.getDayOfWeek()) && 
                schedule.getPeriodNumber().equals(timeSlot.getPeriodNumber())) {
                
                // 检查教师冲突
                if (schedule.getTeacherId().equals(course.getTeacherId())) {
                    return false;
                }
                
                // 检查教室冲突
                if (schedule.getClassroomId().equals(classroom.getId())) {
                    return false;
                }
            }
        }
        
        return true;
    }

    private CourseSchedule createSchedule(Course course, Classroom classroom, TimeSlot timeSlot, ScheduleRequest request) {
        CourseSchedule schedule = new CourseSchedule();
        schedule.setCourseId(course.getId());
        schedule.setClassroomId(classroom.getId());
        schedule.setTeacherId(course.getTeacherId());
        schedule.setTimeSlotId(timeSlot.getId());
        schedule.setDayOfWeek(timeSlot.getDayOfWeek());
        schedule.setPeriodNumber(timeSlot.getPeriodNumber());
        schedule.setSemester(request.getSemester());
        schedule.setAcademicYear(request.getAcademicYear());
        schedule.setStartWeek(request.getStartWeek() != null ? request.getStartWeek() : 1);
        schedule.setEndWeek(request.getEndWeek() != null ? request.getEndWeek() : 18);
        schedule.setWeekType(request.getWeekType() != null ? request.getWeekType() : "all");
        schedule.setStudentCount(course.getEnrolledStudents());
        schedule.setStatus(1);
        schedule.setDeleted(0);
        return schedule;
    }

    private boolean isTimeSlotAvailable(TimeSlot timeSlot, Long classroomId, Long teacherId, 
                                       List<CourseSchedule> existingSchedules) {
        return existingSchedules.stream()
            .noneMatch(schedule -> 
                schedule.getDayOfWeek().equals(timeSlot.getDayOfWeek()) && 
                schedule.getPeriodNumber().equals(timeSlot.getPeriodNumber()) &&
                (schedule.getClassroomId().equals(classroomId) || schedule.getTeacherId().equals(teacherId))
            );
    }

    private ScheduleStatistics generateStatistics(List<Course> courses, List<CourseSchedule> schedules, 
                                                 List<ConflictInfo> conflicts) {
        ScheduleStatistics statistics = new ScheduleStatistics();
        statistics.setTotalCourses(courses.size());
        statistics.setScheduledCourses(schedules.size());
        statistics.setUnscheduledCourses(courses.size() - schedules.size());
        statistics.setTotalConflicts(conflicts.size());
        
        // 统计冲突类型
        long teacherConflicts = conflicts.stream().filter(c -> "teacher".equals(c.getType())).count();
        long classroomConflicts = conflicts.stream().filter(c -> "classroom".equals(c.getType())).count();
        long studentConflicts = conflicts.stream().filter(c -> "student".equals(c.getType())).count();
        
        statistics.setTeacherConflicts((int) teacherConflicts);
        statistics.setClassroomConflicts((int) classroomConflicts);
        statistics.setStudentConflicts((int) studentConflicts);
        
        statistics.setSuccessRate(courses.isEmpty() ? 0.0 : 
                                 (double) schedules.size() / courses.size() * 100);
        
        return statistics;
    }

    private CourseSchedule cloneSchedule(CourseSchedule original) {
        CourseSchedule clone = new CourseSchedule();
        clone.setCourseId(original.getCourseId());
        clone.setClassroomId(original.getClassroomId());
        clone.setTeacherId(original.getTeacherId());
        clone.setTimeSlotId(original.getTimeSlotId());
        clone.setDayOfWeek(original.getDayOfWeek());
        clone.setPeriodNumber(original.getPeriodNumber());
        clone.setStartWeek(original.getStartWeek());
        clone.setEndWeek(original.getEndWeek());
        clone.setWeekType(original.getWeekType());
        if (original.getClassList() != null) {
            clone.setClassList(original.getClassList());
        }
        clone.setStudentCount(original.getStudentCount());
        clone.setScheduleType(original.getScheduleType());
        clone.setStatus(original.getStatus());
        clone.setDeleted(original.getDeleted());
        return clone;
    }

    private boolean hasClassListOverlap(List<String> classList1, String[] classList2) {
        if (classList1 == null || classList2 == null) {
            return false;
        }
        
        Set<String> set1 = new HashSet<>(classList1);
        Set<String> set2 = new HashSet<>(Arrays.asList(classList2));
        
        return set1.stream().anyMatch(set2::contains);
    }
}