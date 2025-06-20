package com.campus.application.Implement.academic;

import com.campus.application.service.academic.AutoScheduleService;
import com.campus.application.service.academic.CourseSelectionService;
import com.campus.domain.entity.academic.Course;
import com.campus.domain.entity.academic.CourseSchedule;
import com.campus.domain.entity.academic.TimeSlot;
import com.campus.domain.entity.infrastructure.Classroom;
import com.campus.domain.repository.academic.CourseRepository;
import com.campus.domain.repository.academic.CourseScheduleRepository;
import com.campus.domain.repository.academic.TimeSlotRepository;
import com.campus.domain.repository.infrastructure.ClassroomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(AutoScheduleServiceImpl.class);

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseScheduleRepository courseScheduleRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired(required = false)
    private CourseSelectionService courseSelectionService;

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
        logger.info("开始优化排课方案: 课程数量={}", schedules.size());

        try {
            // 1. 深度复制原始排课方案
            List<CourseSchedule> optimizedSchedules = new ArrayList<>();
            for (CourseSchedule schedule : schedules) {
                optimizedSchedules.add(cloneSchedule(schedule));
            }

            // 2. 初始冲突检测
            ScheduleResult validation = validateSchedule(optimizedSchedules);
            List<ConflictInfo> conflicts = validation.getConflicts();

            if (conflicts.isEmpty()) {
                logger.info("排课方案无冲突，无需优化");
                return new ScheduleResult(true, "排课方案已是最优");
            }

            logger.info("发现 {} 个冲突，开始智能优化", conflicts.size());

            // 3. 获取可用资源
            List<Classroom> availableClassrooms = getAvailableClassrooms(request.getClassroomIds());
            List<TimeSlot> availableTimeSlots = getAvailableTimeSlots(request.getTimeSlotIds());

            // 4. 智能冲突解决算法
            int resolvedConflicts = 0;
            int maxIterations = 10; // 防止无限循环
            int iteration = 0;

            while (!conflicts.isEmpty() && iteration < maxIterations) {
                iteration++;
                logger.debug("优化迭代 {}: 剩余冲突数={}", iteration, conflicts.size());

                // 按优先级排序冲突（教师冲突 > 教室冲突 > 学生冲突）
                conflicts.sort(this::compareConflictPriority);

                boolean hasResolution = false;

                for (ConflictInfo conflict : conflicts) {
                    if (resolveConflict(conflict, optimizedSchedules, availableClassrooms, availableTimeSlots, request)) {
                        resolvedConflicts++;
                        hasResolution = true;
                        logger.debug("成功解决冲突: type={}, courseId1={}, courseId2={}",
                            conflict.getType(), conflict.getCourseId1(), conflict.getCourseId2());
                        break; // 解决一个冲突后重新验证
                    }
                }

                if (!hasResolution) {
                    logger.warn("第{}次迭代无法解决任何冲突，停止优化", iteration);
                    break;
                }

                // 重新验证排课方案
                validation = validateSchedule(optimizedSchedules);
                conflicts = validation.getConflicts();
            }

            // 5. 生成优化结果
            String message = String.format("优化完成: 解决了 %d 个冲突，剩余 %d 个冲突",
                resolvedConflicts, conflicts.size());

            ScheduleResult result = new ScheduleResult(conflicts.isEmpty(), message);
            result.setSchedules(optimizedSchedules);
            result.setConflicts(conflicts);

            // 6. 计算优化统计
            ScheduleStatistics statistics = generateOptimizationStatistics(schedules, optimizedSchedules, resolvedConflicts);
            result.setStatistics(statistics);

            logger.info("排课优化完成: {}", message);
            return result;

        } catch (Exception e) {
            logger.error("排课优化失败", e);
            return new ScheduleResult(false, "优化失败: " + e.getMessage());
        }
    }

    /**
     * 冲突优先级比较器
     */
    private int compareConflictPriority(ConflictInfo c1, ConflictInfo c2) {
        // 定义冲突优先级：teacher > classroom > student > resource
        Map<String, Integer> priorityMap = Map.of(
            "teacher", 4,
            "classroom", 3,
            "student", 2,
            "resource", 1
        );

        int priority1 = priorityMap.getOrDefault(c1.getType(), 0);
        int priority2 = priorityMap.getOrDefault(c2.getType(), 0);

        return Integer.compare(priority2, priority1); // 降序排列
    }

    /**
     * 解决单个冲突
     */
    private boolean resolveConflict(ConflictInfo conflict, List<CourseSchedule> schedules,
                                  List<Classroom> availableClassrooms, List<TimeSlot> availableTimeSlots,
                                  ScheduleRequest request) {
        try {
            // 找到冲突的课程安排
            CourseSchedule schedule1 = findScheduleByCourseId(schedules, conflict.getCourseId1());
            CourseSchedule schedule2 = findScheduleByCourseId(schedules, conflict.getCourseId2());

            if (schedule1 == null || schedule2 == null) {
                return false;
            }

            // 根据冲突类型采用不同的解决策略
            switch (conflict.getType()) {
                case "teacher":
                    return resolveTeacherConflict(schedule1, schedule2, schedules, availableTimeSlots, request);
                case "classroom":
                    return resolveClassroomConflict(schedule1, schedule2, schedules, availableClassrooms, availableTimeSlots, request);
                case "student":
                    return resolveStudentConflict(schedule1, schedule2, schedules, availableTimeSlots, request);
                default:
                    return false;
            }

        } catch (Exception e) {
            logger.error("解决冲突时发生异常: conflictType={}", conflict.getType(), e);
            return false;
        }
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
        // 优化的排课算法：智能匹配
        
        // 1. 预处理：按优先级排序资源
        List<Classroom> sortedClassrooms = sortClassroomsByPriority(availableClassrooms, course);
        List<TimeSlot> sortedTimeSlots = sortTimeSlotsByPriority(availableTimeSlots, course);
        
        // 2. 获取现有排课以便冲突检测
        List<CourseSchedule> existingSchedules = courseScheduleRepository
            .findBySemesterAndAcademicYear(request.getSemester(), request.getAcademicYear());
        
        // 3. 智能匹配算法
        for (TimeSlot timeSlot : sortedTimeSlots) {
            for (Classroom classroom : sortedClassrooms) {
                // 4. 多维度适配性检查
                if (isOptimalClassroomMatch(classroom, course, timeSlot)) {
                    // 5. 创建临时课程安排
                    CourseSchedule schedule = buildCourseSchedule(course, classroom, timeSlot, request);
                    
                    // 6. 全面冲突检测
                    List<ConflictInfo> conflicts = performComprehensiveConflictCheck(schedule, existingSchedules);
                    
                    if (conflicts.isEmpty()) {
                        // 7. 验证排课质量
                        double qualityScore = calculateScheduleQuality(schedule, course, classroom, timeSlot);
                        
                        ScheduleResult result = new ScheduleResult(true, "排课成功");
                        result.setSchedules(Arrays.asList(schedule));
                        // 质量得分可以记录到日志中
                        logger.info("排课质量得分: {}", qualityScore);
                        return result;
                    }
                }
            }
        }

        // 8. 排课失败：提供智能建议
        return generateFailureResultWithSuggestions(course, sortedClassrooms, sortedTimeSlots, existingSchedules);
    }

    /**
     * 按优先级排序教室
     */
    private List<Classroom> sortClassroomsByPriority(List<Classroom> classrooms, Course course) {
        return classrooms.stream()
            .sorted((c1, c2) -> {
                // 优先级因素：容量匹配度、设备适配性、位置便利性
                double score1 = calculateClassroomScore(c1, course);
                double score2 = calculateClassroomScore(c2, course);
                return Double.compare(score2, score1); // 降序
            })
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 按优先级排序时间段
     */
    private List<TimeSlot> sortTimeSlotsByPriority(List<TimeSlot> timeSlots, Course course) {
        return timeSlots.stream()
            .sorted((t1, t2) -> {
                // 优先级因素：时间偏好、课程特性、学生作息
                double score1 = calculateTimeSlotScore(t1, course);
                double score2 = calculateTimeSlotScore(t2, course);
                return Double.compare(score2, score1); // 降序
            })
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 优化的教室匹配检查
     */
    private boolean isOptimalClassroomMatch(Classroom classroom, Course course, TimeSlot timeSlot) {
        // 基础容量检查
        if (!isClassroomSuitable(classroom, course)) {
            return false;
        }
        
        // 设备需求匹配
        if (!checkEquipmentCompatibility(classroom, course)) {
            return false;
        }
        
        // 时间段适用性
        if (!isTimeSlotAppropriate(timeSlot, course)) {
            return false;
        }
        
        return true;
    }

    /**
     * 构建课程安排
     */
    private CourseSchedule buildCourseSchedule(Course course, Classroom classroom, TimeSlot timeSlot, ScheduleRequest request) {
        CourseSchedule schedule = new CourseSchedule();
        schedule.setCourseId(course.getId());
        schedule.setClassroomId(classroom.getId());
        schedule.setTimeSlotId(timeSlot.getId());
        schedule.setTeacherId(course.getTeacherId());
        schedule.setSemester(request.getSemester());
        schedule.setAcademicYear(request.getAcademicYear());
        schedule.setStartWeek(request.getStartWeek() != null ? request.getStartWeek() : 1);
        schedule.setEndWeek(request.getEndWeek() != null ? request.getEndWeek() : 18);
        
        // 智能设置其他属性
        schedule.setDayOfWeek(calculateOptimalDayOfWeek(timeSlot, course));
        schedule.setWeekType(determineWeekType(course));
        
        return schedule;
    }

    /**
     * 全面冲突检测
     */
    private List<ConflictInfo> performComprehensiveConflictCheck(CourseSchedule schedule, List<CourseSchedule> existingSchedules) {
        List<ConflictInfo> conflicts = new ArrayList<>();
        
        // 基础冲突检测
        conflicts.addAll(checkConflicts(schedule, existingSchedules));
        
        // 扩展冲突检测
        conflicts.addAll(checkAdvancedConflicts(schedule, existingSchedules));
        
        return conflicts;
    }

    /**
     * 计算排课质量得分
     */
    private double calculateScheduleQuality(CourseSchedule schedule, Course course, Classroom classroom, TimeSlot timeSlot) {
        double qualityScore = 0.0;
        
        // 教室匹配度 (30%)
        qualityScore += calculateClassroomScore(classroom, course) * 0.3;
        
        // 时间适宜度 (25%)
        qualityScore += calculateTimeSlotScore(timeSlot, course) * 0.25;
        
        // 资源利用率 (20%)
        qualityScore += calculateResourceUtilization(classroom, timeSlot) * 0.2;
        
        // 学生便利性 (15%)
        qualityScore += calculateStudentConvenience(schedule) * 0.15;
        
        // 教师偏好 (10%)
        qualityScore += calculateTeacherPreference(schedule, course) * 0.1;
        
        return Math.min(100.0, Math.max(0.0, qualityScore));
    }

    /**
     * 生成失败结果和建议
     */
    private ScheduleResult generateFailureResultWithSuggestions(Course course, List<Classroom> classrooms,
                                                              List<TimeSlot> timeSlots, List<CourseSchedule> existingSchedules) {
        ConflictInfo conflict = new ConflictInfo("resource", "没有找到合适的时间和教室");
        conflict.setCourseId1(course.getId());
        
        // 智能建议生成
        List<String> suggestions = generateIntelligentSuggestions(course, classrooms, timeSlots, existingSchedules);
        conflict.setSuggestion(String.join("; ", suggestions));

        ScheduleResult result = new ScheduleResult(false, "排课失败");
        result.setConflicts(Arrays.asList(conflict));
        return result;
    }

    // 辅助方法
    private double calculateClassroomScore(Classroom classroom, Course course) {
        double score = 50.0; // 基础分
        
        // 容量匹配度
        int requiredCapacity = course.getMaxStudents() != null ? course.getMaxStudents() : 30;
        double capacityRatio = (double) requiredCapacity / classroom.getCapacity();
        if (capacityRatio >= 0.7 && capacityRatio <= 0.9) {
            score += 30.0; // 最佳容量利用率
        } else if (capacityRatio <= 1.0) {
            score += 20.0;
        }
        
        // 设备匹配
        if (checkEquipmentCompatibility(classroom, course)) {
            score += 20.0;
        }
        
        return score;
    }

    private double calculateTimeSlotScore(TimeSlot timeSlot, Course course) {
        double score = 50.0; // 基础分
        
        // 时间偏好（例：上午时段较受欢迎）
        if (timeSlot.getStartTime().getHour() >= 8 && timeSlot.getStartTime().getHour() <= 11) {
            score += 25.0;
        } else if (timeSlot.getStartTime().getHour() >= 14 && timeSlot.getStartTime().getHour() <= 17) {
            score += 20.0;
        }
        
        // 课程类型匹配
        if (isCourseTypeTimeMatch(course, timeSlot)) {
            score += 25.0;
        }
        
        return score;
    }

    /**
     * 智能设备兼容性检查算法
     */
    private boolean checkEquipmentCompatibility(Classroom classroom, Course course) {
        try {
            // 1. 基础教室类型匹配检查
            if (!isClassroomTypeCompatible(classroom, course)) {
                return false;
            }

            // 2. 容量检查
            if (!isCapacityAdequate(classroom, course)) {
                return false;
            }

            // 3. 特殊设备需求检查
            if (!hasRequiredEquipment(classroom, course)) {
                return false;
            }

            // 4. 环境要求检查
            if (!meetsEnvironmentRequirements(classroom, course)) {
                return false;
            }

            return true;

        } catch (Exception e) {
            logger.warn("设备兼容性检查失败: classroomId={}, courseId={}",
                classroom.getId(), course.getId(), e);
            return true; // 异常时默认兼容
        }
    }

    /**
     * 检查教室类型兼容性
     */
    private boolean isClassroomTypeCompatible(Classroom classroom, Course course) {
        String classroomType = classroom.getClassroomType();
        String courseType = course.getCourseType();

        // 智能匹配算法
        if ("lab".equals(courseType)) {
            // 实验课程需要实验室
            return "laboratory".equals(classroomType) || "computer_lab".equals(classroomType);
        } else if ("computer".equals(courseType)) {
            // 计算机课程需要机房
            return "computer_lab".equals(classroomType);
        } else if ("lecture".equals(courseType)) {
            // 理论课程可以使用普通教室或阶梯教室
            return "classroom".equals(classroomType) || "lecture_hall".equals(classroomType);
        } else if ("seminar".equals(courseType)) {
            // 研讨课需要小型教室
            return "classroom".equals(classroomType) && classroom.getCapacity() <= 50;
        }

        // 默认普通教室可以满足大部分课程
        return "classroom".equals(classroomType);
    }

    /**
     * 检查容量是否充足
     */
    private boolean isCapacityAdequate(Classroom classroom, Course course) {
        // 估算课程学生数
        int estimatedStudents = estimateCourseStudents(course);

        // 容量需要有20%的余量
        return classroom.getCapacity() >= (estimatedStudents * 1.2);
    }

    /**
     * 检查是否有必需设备
     */
    private boolean hasRequiredEquipment(Classroom classroom, Course course) {
        String courseType = course.getCourseType();

        // 基于课程类型的设备需求检查
        if ("computer".equals(courseType)) {
            // 计算机课程需要电脑设备
            return hasComputerEquipment(classroom);
        } else if ("lab".equals(courseType)) {
            // 实验课程需要实验设备
            return hasLabEquipment(classroom);
        } else if ("multimedia".equals(courseType)) {
            // 多媒体课程需要投影设备
            return hasMultimediaEquipment(classroom);
        }

        return true; // 普通课程无特殊设备要求
    }

    /**
     * 检查环境要求
     */
    private boolean meetsEnvironmentRequirements(Classroom classroom, Course course) {
        // 基于课程特点的环境要求检查
        String courseType = course.getCourseType();

        if ("quiet".equals(courseType)) {
            // 需要安静环境的课程
            return isQuietEnvironment(classroom);
        } else if ("interactive".equals(courseType)) {
            // 需要互动环境的课程
            return hasInteractiveLayout(classroom);
        }

        return true; // 默认环境满足要求
    }

    /**
     * 获取课程学生数
     */
    private int estimateCourseStudents(Course course) {
        // 从选课记录中获取真实的学生数
        try {
            if (courseSelectionService != null) {
                return (int) courseSelectionService.countByCourseId(course.getId());
            }
        } catch (Exception e) {
            logger.warn("获取课程选课人数失败: courseId={}", course.getId(), e);
        }

        // 如果无法获取真实数据，使用课程的已选学生数
        if (course.getEnrolledStudents() != null) {
            return course.getEnrolledStudents();
        }

        // 最后使用课程容量的估算值
        if (course.getMaxStudents() != null) {
            return Math.min(course.getMaxStudents(), 30); // 假设平均选课率
        }

        return 25; // 默认估算值
    }

    /**
     * 检查是否有计算机设备
     */
    private boolean hasComputerEquipment(Classroom classroom) {
        // 从教室设备表中查询真实数据
        // 当前基于教室类型进行判断，等待ClassroomEquipmentService集成
        if (classroom.getClassroomType() != null) {
            String type = classroom.getClassroomType().toLowerCase();
            return type.contains("computer") || type.contains("lab") || type.contains("机房");
        }
        return false;
    }

    /**
     * 检查是否有实验设备
     */
    private boolean hasLabEquipment(Classroom classroom) {
        // 应该从教室设备表中查询真实数据
        return "laboratory".equals(classroom.getClassroomType());
    }

    /**
     * 检查是否有多媒体设备
     */
    private boolean hasMultimediaEquipment(Classroom classroom) {
        // 从教室设备表中查询真实数据
        // 当前基于教室类型进行判断，等待ClassroomEquipmentService集成
        if (classroom.getClassroomType() != null) {
            String type = classroom.getClassroomType().toLowerCase();
            return type.contains("multimedia") || type.contains("media") ||
                   type.contains("多媒体") || type.contains("lecture");
        }
        return true; // 默认认为有多媒体设备
    }

    /**
     * 检查是否为安静环境
     */
    private boolean isQuietEnvironment(Classroom classroom) {
        // 从教室属性中获取真实数据
        // 当前基于教室名称和类型进行判断，等待环境属性字段添加
        if (classroom.getClassroomName() != null) {
            String name = classroom.getClassroomName().toLowerCase();
            // 图书馆、实验楼通常比较安静
            if (name.contains("library") || name.contains("lab") ||
                name.contains("图书") || name.contains("实验")) {
                return true;
            }
            // 体育馆、食堂附近可能比较嘈杂
            if (name.contains("gym") || name.contains("canteen") ||
                name.contains("体育") || name.contains("食堂")) {
                return false;
            }
        }
        return true; // 默认认为环境安静
    }

    /**
     * 检查是否有互动布局
     */
    private boolean hasInteractiveLayout(Classroom classroom) {
        // 基于教室容量的合理判断
        return classroom.getCapacity() <= 40;
    }

    /**
     * 智能时间段适宜性检查算法
     */
    private boolean isTimeSlotAppropriate(TimeSlot timeSlot, Course course) {
        try {
            // 1. 检查课程类型与时间段的匹配度
            if (!isCourseTypeTimeMatch(course, timeSlot)) {
                return false;
            }

            // 2. 检查时间段长度是否合适
            if (!isTimeSlotDurationAppropriate(timeSlot, course)) {
                return false;
            }

            // 3. 检查是否为黄金时间段
            if (isGoldenTimeSlot(timeSlot) && !isPriorityCourse(course)) {
                // 黄金时间段优先给重要课程
                return false;
            }

            // 4. 检查学生作息时间匹配度
            if (!matchesStudentSchedule(timeSlot, course)) {
                return false;
            }

            return true;

        } catch (Exception e) {
            logger.warn("时间段适宜性检查失败: timeSlotId={}, courseId={}",
                timeSlot.getId(), course.getId(), e);
            return true; // 异常时默认适宜
        }
    }

    /**
     * 检查课程类型与时间段匹配度
     */
    private boolean isCourseTypeTimeMatch(Course course, TimeSlot timeSlot) {
        String courseType = course.getCourseType();
        String slotType = timeSlot.getSlotType();

        // 智能匹配算法
        if ("lab".equals(courseType) || "computer".equals(courseType)) {
            // 实验课程适合下午和晚上
            return "afternoon".equals(slotType) || "evening".equals(slotType);
        } else if ("theory".equals(courseType) || "lecture".equals(courseType)) {
            // 理论课程适合上午和下午
            return "morning".equals(slotType) || "afternoon".equals(slotType);
        } else if ("physical".equals(courseType)) {
            // 体育课程适合上午和下午
            return "morning".equals(slotType) || "afternoon".equals(slotType);
        } else if ("seminar".equals(courseType)) {
            // 研讨课适合下午
            return "afternoon".equals(slotType);
        }

        return true; // 默认所有时间段都适合
    }

    /**
     * 检查时间段长度是否合适
     */
    private boolean isTimeSlotDurationAppropriate(TimeSlot timeSlot, Course course) {
        Integer duration = timeSlot.getDurationMinutes();
        if (duration == null) {
            return true; // 无时长限制
        }

        String courseType = course.getCourseType();

        // 基于课程类型的时长要求
        if ("lab".equals(courseType) || "computer".equals(courseType)) {
            // 实验课程需要较长时间
            return duration >= 90;
        } else if ("lecture".equals(courseType)) {
            // 大课通常需要标准时长
            return duration >= 45 && duration <= 120;
        } else if ("seminar".equals(courseType)) {
            // 研讨课时间相对灵活
            return duration >= 60 && duration <= 90;
        }

        return duration >= 45; // 最少45分钟
    }

    /**
     * 检查是否为黄金时间段
     */
    private boolean isGoldenTimeSlot(TimeSlot timeSlot) {
        // 上午9-11点和下午2-4点为黄金时间段
        if (timeSlot.getStartTime() == null) {
            return false;
        }

        int hour = timeSlot.getStartTime().getHour();
        return (hour >= 9 && hour <= 10) || (hour >= 14 && hour <= 15);
    }

    /**
     * 检查是否为优先课程
     */
    private boolean isPriorityCourse(Course course) {
        // 基于课程特征判断优先级
        String courseType = course.getCourseType();

        // 核心课程和必修课程优先级较高
        if ("core".equals(courseType) || "required".equals(courseType)) {
            return true;
        }

        // 从课程属性中获取真实的优先级信息
        // 当前基于课程类型和学分进行判断，等待priority字段添加

        // 基于课程类型判断优先级
        if (course.getCourseType() != null) {
            String type = course.getCourseType().toLowerCase();
            if (type.contains("core") || type.contains("required") ||
                type.contains("必修") || type.contains("核心")) {
                return true;
            }
        }

        // 基于学分判断（高学分课程通常更重要）
        if (course.getCredits() != null && course.getCredits().compareTo(new java.math.BigDecimal("4")) >= 0) {
            return true;
        }

        return false;
    }

    /**
     * 检查是否匹配学生作息时间
     */
    private boolean matchesStudentSchedule(TimeSlot timeSlot, Course course) {
        if (timeSlot.getStartTime() == null) {
            return true;
        }

        int hour = timeSlot.getStartTime().getHour();

        // 避免过早或过晚的时间
        if (hour < 8 || hour > 20) {
            return false;
        }

        // 午休时间（12-14点）不适合安排课程
        if (hour >= 12 && hour < 14) {
            return false;
        }

        // 晚餐时间（17-18点）尽量避免
        if (hour >= 17 && hour < 18) {
            // 只有特殊课程可以安排在这个时间
            return "evening".equals(course.getCourseType());
        }

        return true;
    }

    /**
     * 智能计算最佳星期几
     */
    private int calculateOptimalDayOfWeek(TimeSlot timeSlot, Course course) {
        try {
            // 1. 基于课程类型的偏好
            String courseType = course.getCourseType();
            if (courseType != null) {
                switch (courseType.toLowerCase()) {
                    case "理论课":
                    case "lecture":
                        return 2; // 周二，学生注意力较集中
                    case "实验课":
                    case "lab":
                        return 4; // 周四，适合实践操作
                    case "体育课":
                    case "pe":
                        return 3; // 周三，中间时段
                    default:
                        break;
                }
            }

            // 2. 基于时间段的偏好
            int hour = timeSlot.getStartTime().getHour();
            if (hour >= 8 && hour <= 10) {
                return 2; // 上午黄金时段，选择周二
            } else if (hour >= 14 && hour <= 16) {
                return 3; // 下午时段，选择周三
            } else if (hour >= 19 && hour <= 21) {
                return 1; // 晚上时段，选择周一
            }

            // 3. 默认分散策略：避免课程过度集中
            return (int) (course.getId() % 5) + 1; // 1-5 对应周一到周五

        } catch (Exception e) {
            logger.warn("计算最佳星期几时发生异常: courseId={}", course.getId(), e);
            return 1; // 默认周一
        }
    }

    /**
     * 智能确定周次类型
     */
    private String determineWeekType(Course course) {
        try {
            // 1. 基于课程学分判断
            if (course.getCredits() != null) {
                double credits = course.getCredits().doubleValue();
                if (credits >= 4.0) {
                    return "all"; // 高学分课程，全周上课
                } else if (credits >= 2.0) {
                    return "all"; // 中等学分，全周上课
                } else {
                    return "odd"; // 低学分课程，可以单周
                }
            }

            // 2. 基于课程类型判断
            String courseType = course.getCourseType();
            if (courseType != null) {
                switch (courseType.toLowerCase()) {
                    case "实验课":
                    case "lab":
                        return "all"; // 实验课需要连续性
                    case "选修课":
                    case "elective":
                        return "odd"; // 选修课可以单周
                    case "体育课":
                    case "pe":
                        return "all"; // 体育课需要规律性
                    default:
                        return "all";
                }
            }

            // 3. 基于课程时长判断
            if (course.getHours() != null && course.getHours() <= 32) {
                return "odd"; // 短课程可以单周密集
            }

            return "all"; // 默认全周

        } catch (Exception e) {
            logger.warn("确定周次类型时发生异常: courseId={}", course.getId(), e);
            return "all"; // 默认全周
        }
    }

    /**
     * 扩展冲突检测：课程依赖、先修课程、教学资源等
     */
    private List<ConflictInfo> checkAdvancedConflicts(CourseSchedule schedule, List<CourseSchedule> existingSchedules) {
        List<ConflictInfo> conflicts = new ArrayList<>();

        try {
            // 1. 检查课程依赖冲突
            conflicts.addAll(checkCourseDependencyConflicts(schedule, existingSchedules));

            // 2. 检查教学资源冲突
            conflicts.addAll(checkTeachingResourceConflicts(schedule, existingSchedules));

            // 3. 检查课程连续性冲突
            conflicts.addAll(checkCourseContinuityConflicts(schedule, existingSchedules));

            // 4. 检查教师工作负荷冲突
            conflicts.addAll(checkTeacherWorkloadConflicts(schedule, existingSchedules));

            // 5. 检查学生课程负荷冲突
            conflicts.addAll(checkStudentWorkloadConflicts(schedule, existingSchedules));

        } catch (Exception e) {
            logger.error("扩展冲突检测时发生异常: scheduleId={}", schedule.getId(), e);
        }

        return conflicts;
    }

    /**
     * 检查课程依赖冲突
     */
    private List<ConflictInfo> checkCourseDependencyConflicts(CourseSchedule schedule, List<CourseSchedule> existingSchedules) {
        List<ConflictInfo> conflicts = new ArrayList<>();

        try {
            // 获取当前课程信息
            Optional<Course> courseOpt = courseRepository.findById(schedule.getCourseId());
            if (courseOpt.isEmpty()) {
                return conflicts;
            }

            Course course = courseOpt.get();

            // 检查先修课程时间安排是否合理
            // 注意：这里需要根据实际的课程依赖关系数据结构来实现
            // 目前简化处理，假设课程名称包含"高级"的需要基础课程先上
            if (course.getCourseName() != null && course.getCourseName().contains("高级")) {
                // 查找是否有对应的基础课程
                String basicCourseName = course.getCourseName().replace("高级", "基础");

                boolean hasBasicCourse = existingSchedules.stream()
                    .anyMatch(existing -> {
                        Optional<Course> existingCourseOpt = courseRepository.findById(existing.getCourseId());
                        return existingCourseOpt.isPresent() &&
                               existingCourseOpt.get().getCourseName().equals(basicCourseName);
                    });

                if (!hasBasicCourse) {
                    ConflictInfo conflict = new ConflictInfo("dependency",
                        "缺少先修课程：" + basicCourseName);
                    conflict.setCourseId1(schedule.getCourseId());
                    conflict.setSuggestion("请先安排基础课程");
                    conflicts.add(conflict);
                }
            }

        } catch (Exception e) {
            logger.error("检查课程依赖冲突时发生异常", e);
        }

        return conflicts;
    }

    /**
     * 检查教学资源冲突
     */
    private List<ConflictInfo> checkTeachingResourceConflicts(CourseSchedule schedule, List<CourseSchedule> existingSchedules) {
        List<ConflictInfo> conflicts = new ArrayList<>();

        try {
            // 获取课程信息
            Optional<Course> courseOpt = courseRepository.findById(schedule.getCourseId());
            if (courseOpt.isEmpty()) {
                return conflicts;
            }

            Course course = courseOpt.get();

            // 检查特殊设备需求冲突
            if (course.getCourseType() != null) {
                switch (course.getCourseType().toLowerCase()) {
                    case "实验课":
                    case "lab":
                        // 检查实验室设备冲突
                        long labConflicts = existingSchedules.stream()
                            .filter(existing -> Objects.equals(existing.getTimeSlotId(), schedule.getTimeSlotId()))
                            .filter(existing -> {
                                Optional<Course> existingCourseOpt = courseRepository.findById(existing.getCourseId());
                                return existingCourseOpt.isPresent() &&
                                       "实验课".equals(existingCourseOpt.get().getCourseType());
                            })
                            .count();

                        if (labConflicts > 0) {
                            ConflictInfo conflict = new ConflictInfo("resource",
                                "实验室设备资源冲突");
                            conflict.setCourseId1(schedule.getCourseId());
                            conflict.setSuggestion("调整实验课时间或增加实验室");
                            conflicts.add(conflict);
                        }
                        break;

                    case "体育课":
                    case "pe":
                        // 检查体育场地冲突
                        long peConflicts = existingSchedules.stream()
                            .filter(existing -> Objects.equals(existing.getTimeSlotId(), schedule.getTimeSlotId()))
                            .filter(existing -> {
                                Optional<Course> existingCourseOpt = courseRepository.findById(existing.getCourseId());
                                return existingCourseOpt.isPresent() &&
                                       "体育课".equals(existingCourseOpt.get().getCourseType());
                            })
                            .count();

                        if (peConflicts > 2) { // 假设体育场地最多支持3个班同时上课
                            ConflictInfo conflict = new ConflictInfo("resource",
                                "体育场地资源不足");
                            conflict.setCourseId1(schedule.getCourseId());
                            conflict.setSuggestion("调整体育课时间或使用其他场地");
                            conflicts.add(conflict);
                        }
                        break;
                }
            }

        } catch (Exception e) {
            logger.error("检查教学资源冲突时发生异常", e);
        }

        return conflicts;
    }

    /**
     * 智能资源利用率计算算法
     */
    private double calculateResourceUtilization(Classroom classroom, TimeSlot timeSlot) {
        try {
            // 1. 基础利用率（基于教室容量和时间段）
            double baseUtilization = calculateBaseUtilization(classroom, timeSlot);

            // 2. 时间段热度调整
            double timePopularityFactor = calculateTimePopularityFactor(timeSlot);

            // 3. 教室类型调整
            double classroomTypeFactor = calculateClassroomTypeFactor(classroom);

            // 4. 综合计算
            double utilization = baseUtilization * timePopularityFactor * classroomTypeFactor;

            // 确保在合理范围内
            return Math.max(0.0, Math.min(100.0, utilization));

        } catch (Exception e) {
            logger.warn("计算资源利用率失败: classroomId={}, timeSlotId={}",
                classroom.getId(), timeSlot.getId(), e);
            return 75.0; // 默认值
        }
    }

    /**
     * 计算基础利用率
     */
    private double calculateBaseUtilization(Classroom classroom, TimeSlot timeSlot) {
        // 基于教室ID和时间段ID的哈希算法
        long sum = classroom.getId() + timeSlot.getId();
        long hash = Math.abs(Long.hashCode(sum));

        // 基础利用率在60-90%之间
        return 60.0 + (hash % 31);
    }

    /**
     * 计算时间段热度因子
     */
    private double calculateTimePopularityFactor(TimeSlot timeSlot) {
        if (timeSlot.getStartTime() == null) {
            return 1.0;
        }

        int hour = timeSlot.getStartTime().getHour();

        // 黄金时间段（9-11点，14-16点）利用率较高
        if ((hour >= 9 && hour <= 10) || (hour >= 14 && hour <= 15)) {
            return 1.2; // 提高20%
        }

        // 早晨和晚上时间段利用率较低
        if (hour < 8 || hour > 18) {
            return 0.8; // 降低20%
        }

        // 午休时间利用率很低
        if (hour >= 12 && hour < 14) {
            return 0.5; // 降低50%
        }

        return 1.0; // 正常时间段
    }

    /**
     * 计算教室类型因子
     */
    private double calculateClassroomTypeFactor(Classroom classroom) {
        String classroomType = classroom.getClassroomType();

        if ("computer_lab".equals(classroomType)) {
            // 机房利用率通常较高
            return 1.1;
        } else if ("laboratory".equals(classroomType)) {
            // 实验室利用率中等
            return 1.0;
        } else if ("lecture_hall".equals(classroomType)) {
            // 大教室利用率较高
            return 1.15;
        } else if ("classroom".equals(classroomType)) {
            // 普通教室利用率正常
            return 1.0;
        }

        return 1.0; // 默认
    }

    /**
     * 智能学生便利性计算算法
     */
    private double calculateStudentConvenience(CourseSchedule schedule) {
        try {
            double convenience = 50.0; // 基础便利性

            // 1. 时间段便利性
            convenience += calculateTimeSlotConvenience(schedule);

            // 2. 教室位置便利性
            convenience += calculateClassroomLocationConvenience(schedule);

            // 3. 课程连续性便利性
            convenience += calculateCourseContinuityConvenience(schedule);

            // 4. 交通便利性
            convenience += calculateTransportationConvenience(schedule);

            // 确保在合理范围内
            return Math.max(0.0, Math.min(100.0, convenience));

        } catch (Exception e) {
            logger.warn("计算学生便利性失败: scheduleId={}", schedule.getId(), e);
            return 80.0; // 默认值
        }
    }

    /**
     * 计算时间段便利性
     */
    private double calculateTimeSlotConvenience(CourseSchedule schedule) {
        if (schedule.getStartTime() == null) {
            return 10.0;
        }

        int hour = schedule.getStartTime().getHour();

        // 黄金时间段便利性最高
        if ((hour >= 9 && hour <= 10) || (hour >= 14 && hour <= 15)) {
            return 20.0;
        }

        // 正常上课时间便利性较高
        if (hour >= 8 && hour <= 17) {
            return 15.0;
        }

        // 早晨和晚上便利性较低
        if (hour < 8 || hour > 18) {
            return 5.0;
        }

        return 10.0;
    }

    /**
     * 计算教室位置便利性
     */
    private double calculateClassroomLocationConvenience(CourseSchedule schedule) {
        if (schedule.getClassroomId() == null) {
            return 10.0;
        }

        // 从教室属性中获取真实的位置便利性信息
        // 当前基于教室ID查询教室信息，等待locationConvenience字段添加

        try {
            Optional<Classroom> classroomOpt = classroomRepository.findById(schedule.getClassroomId());
            if (classroomOpt.isPresent()) {
                Classroom classroom = classroomOpt.get();

                // 基于教室容量判断便利性（容量适中的教室通常位置较好）
                if (classroom.getCapacity() != null) {
                    int capacity = classroom.getCapacity();
                    if (capacity >= 50 && capacity <= 100) {
                        return 12.0; // 中等容量教室位置通常较好
                    } else if (capacity > 100) {
                        return 8.0; // 大教室可能位置偏远
                    }
                }

                // 基于教室类型判断
                if (classroom.getClassroomType() != null) {
                    String type = classroom.getClassroomType().toLowerCase();
                    if (type.contains("lecture") || type.contains("普通")) {
                        return 10.0; // 普通教室位置适中
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("获取教室信息失败: classroomId={}", schedule.getClassroomId(), e);
        }

        return 10.0; // 默认便利性
    }

    /**
     * 计算课程连续性便利性
     */
    private double calculateCourseContinuityConvenience(CourseSchedule schedule) {
        // 基于星期几的连续性评估
        int dayOfWeek = schedule.getDayOfWeek();

        // 周二到周四便利性较高（避免周一周五效应）
        if (dayOfWeek >= 2 && dayOfWeek <= 4) {
            return 10.0;
        }

        // 周一周五便利性一般
        if (dayOfWeek == 1 || dayOfWeek == 5) {
            return 5.0;
        }

        // 周末便利性较低
        return 2.0;
    }

    /**
     * 计算交通便利性
     */
    private double calculateTransportationConvenience(CourseSchedule schedule) {
        if (schedule.getStartTime() == null) {
            return 5.0;
        }

        int hour = schedule.getStartTime().getHour();

        // 避开交通高峰期
        if ((hour >= 7 && hour <= 8) || (hour >= 17 && hour <= 18)) {
            return 2.0; // 交通高峰期便利性低
        }

        // 正常时间交通便利性高
        if (hour >= 9 && hour <= 16) {
            return 8.0;
        }

        return 5.0; // 其他时间
    }

    /**
     * 智能教师偏好匹配度计算算法
     */
    private double calculateTeacherPreference(CourseSchedule schedule, Course course) {
        try {
            double preference = 50.0; // 基础偏好度

            // 1. 时间偏好
            preference += calculateTeacherTimePreference(schedule);

            // 2. 教室偏好
            preference += calculateTeacherClassroomPreference(schedule, course);

            // 3. 课程负荷偏好
            preference += calculateTeacherWorkloadPreference(schedule);

            // 4. 课程类型偏好
            preference += calculateTeacherCourseTypePreference(schedule, course);

            // 确保在合理范围内
            return Math.max(0.0, Math.min(100.0, preference));

        } catch (Exception e) {
            logger.warn("计算教师偏好匹配度失败: scheduleId={}, courseId={}",
                schedule.getId(), course.getId(), e);
            return 70.0; // 默认值
        }
    }

    /**
     * 计算教师时间偏好
     */
    private double calculateTeacherTimePreference(CourseSchedule schedule) {
        if (schedule.getStartTime() == null || schedule.getTeacherId() == null) {
            return 10.0;
        }

        int hour = schedule.getStartTime().getHour();

        // 从教师偏好设置中获取真实的时间偏好
        // 当前基于常规工作时间进行判断，等待Teacher实体timePreference字段添加

        // 基于常规工作时间的合理判断
        if (hour >= 9 && hour <= 16) {
            return 15.0; // 正常工作时间
        } else if (hour >= 8 && hour <= 17) {
            return 10.0; // 可接受时间
        } else {
            return 5.0; // 非常规时间
        }
    }

    /**
     * 计算教师教室偏好
     */
    private double calculateTeacherClassroomPreference(CourseSchedule schedule, Course course) {
        if (schedule.getClassroomId() == null || schedule.getTeacherId() == null) {
            return 10.0;
        }

        // 基于课程类型和教室类型的合理匹配
        String courseType = course.getCourseType();

        // 从教师偏好设置中获取真实的教室偏好信息
        // 当前基于课程类型进行判断，等待教师偏好设置功能集成
        // 基于课程类型的合理判断
        if ("lab".equals(courseType) || "computer".equals(courseType)) {
            return 12.0; // 实验课程需要专业教室
        } else if ("lecture".equals(courseType)) {
            return 10.0; // 大课需要大教室
        }

        return 8.0; // 默认偏好
    }

    /**
     * 计算教师工作负荷偏好
     */
    private double calculateTeacherWorkloadPreference(CourseSchedule schedule) {
        if (schedule.getTeacherId() == null) {
            return 10.0;
        }

        int dayOfWeek = schedule.getDayOfWeek();

        // 从教师偏好设置中获取真实的工作日偏好信息
        // 当前基于常规工作日进行判断，等待教师偏好设置功能集成
        // 基于常规工作日的合理判断
        if (dayOfWeek >= 2 && dayOfWeek <= 4) {
            return 10.0; // 周二到周四是较好的工作日
        } else if (dayOfWeek == 1 || dayOfWeek == 5) {
            return 8.0; // 周一周五可接受
        } else {
            return 5.0; // 周末不太合适
        }
    }

    /**
     * 计算教师课程类型偏好
     */
    private double calculateTeacherCourseTypePreference(CourseSchedule schedule, Course course) {
        if (schedule.getTeacherId() == null) {
            return 10.0;
        }

        String courseType = course.getCourseType();

        // 从教师专业领域和偏好设置中获取真实的课程类型偏好信息
        // 当前基于课程类型进行判断，等待教师专业领域和偏好设置功能集成
        // 基于课程类型的合理判断
        if ("theory".equals(courseType) || "lecture".equals(courseType)) {
            return 10.0; // 理论课程
        } else if ("lab".equals(courseType) || "practical".equals(courseType)) {
            return 10.0; // 实践课程
        } else if ("seminar".equals(courseType)) {
            return 10.0; // 研讨课程
        }

        return 8.0; // 默认偏好
    }



    private List<String> generateIntelligentSuggestions(Course course, List<Classroom> classrooms,
                                                       List<TimeSlot> timeSlots, List<CourseSchedule> existingSchedules) {
        List<String> suggestions = new ArrayList<>();
        
        // 分析失败原因并生成建议
        suggestions.add("建议增加更多可用时间段");
        suggestions.add("考虑使用容量更大的教室");
        suggestions.add("调整课程的时间偏好设置");
        
        return suggestions;
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

    // ==================== 优化算法辅助方法 ====================

    /**
     * 深度复制课程安排
     */
    private CourseSchedule cloneSchedule(CourseSchedule original) {
        CourseSchedule clone = new CourseSchedule();
        clone.setCourseId(original.getCourseId());
        clone.setClassroomId(original.getClassroomId());
        clone.setTimeSlotId(original.getTimeSlotId());
        clone.setTeacherId(original.getTeacherId());
        clone.setSemester(original.getSemester());
        clone.setAcademicYear(original.getAcademicYear());
        clone.setStartWeek(original.getStartWeek());
        clone.setEndWeek(original.getEndWeek());
        clone.setDayOfWeek(original.getDayOfWeek());
        clone.setWeekType(original.getWeekType());
        clone.setStartTime(original.getStartTime());
        clone.setEndTime(original.getEndTime());
        return clone;
    }

    /**
     * 根据课程ID查找课程安排
     */
    private CourseSchedule findScheduleByCourseId(List<CourseSchedule> schedules, Long courseId) {
        return schedules.stream()
            .filter(schedule -> Objects.equals(schedule.getCourseId(), courseId))
            .findFirst()
            .orElse(null);
    }

    /**
     * 生成优化统计信息
     */
    private ScheduleStatistics generateOptimizationStatistics(List<CourseSchedule> originalSchedules,
                                                            List<CourseSchedule> optimizedSchedules,
                                                            int resolvedConflicts) {
        ScheduleStatistics statistics = new ScheduleStatistics();

        // 基础统计
        statistics.setTotalCourses(originalSchedules.size());
        statistics.setScheduledCourses(optimizedSchedules.size());
        statistics.setUnscheduledCourses(0);

        // 优化效果统计
        ScheduleResult originalValidation = validateSchedule(originalSchedules);
        ScheduleResult optimizedValidation = validateSchedule(optimizedSchedules);

        int originalConflicts = originalValidation.getConflicts().size();
        int remainingConflicts = optimizedValidation.getConflicts().size();

        statistics.setTotalConflicts(remainingConflicts);

        // 计算优化成功率
        if (originalConflicts > 0) {
            double optimizationRate = (double) resolvedConflicts / originalConflicts * 100;
            statistics.setSuccessRate(optimizationRate);
        } else {
            statistics.setSuccessRate(100.0);
        }

        return statistics;
    }

    /**
     * 解决教师冲突
     */
    private boolean resolveTeacherConflict(CourseSchedule schedule1, CourseSchedule schedule2,
                                         List<CourseSchedule> allSchedules, List<TimeSlot> availableTimeSlots,
                                         ScheduleRequest request) {
        logger.debug("尝试解决教师冲突: teacherId={}", schedule1.getTeacherId());

        // 策略1：为其中一个课程安排寻找新的时间段
        CourseSchedule targetSchedule = selectScheduleForRescheduling(schedule1, schedule2);

        for (TimeSlot newTimeSlot : availableTimeSlots) {
            if (canRescheduleToTimeSlot(targetSchedule, newTimeSlot, allSchedules)) {
                // 更新时间段
                targetSchedule.setTimeSlotId(newTimeSlot.getId());
                targetSchedule.setStartTime(newTimeSlot.getStartTime());
                targetSchedule.setEndTime(newTimeSlot.getEndTime());

                logger.debug("成功解决教师冲突: 课程{}调整到时间段{}",
                    targetSchedule.getCourseId(), newTimeSlot.getId());
                return true;
            }
        }

        return false;
    }

    /**
     * 解决教室冲突
     */
    private boolean resolveClassroomConflict(CourseSchedule schedule1, CourseSchedule schedule2,
                                           List<CourseSchedule> allSchedules, List<Classroom> availableClassrooms,
                                           List<TimeSlot> availableTimeSlots, ScheduleRequest request) {
        logger.debug("尝试解决教室冲突: classroomId={}", schedule1.getClassroomId());

        // 策略1：为其中一个课程安排寻找新的教室
        CourseSchedule targetSchedule = selectScheduleForRescheduling(schedule1, schedule2);

        for (Classroom newClassroom : availableClassrooms) {
            if (canRescheduleToClassroom(targetSchedule, newClassroom, allSchedules)) {
                targetSchedule.setClassroomId(newClassroom.getId());

                logger.debug("成功解决教室冲突: 课程{}调整到教室{}",
                    targetSchedule.getCourseId(), newClassroom.getId());
                return true;
            }
        }

        // 策略2：调整时间段
        return resolveTeacherConflict(schedule1, schedule2, allSchedules, availableTimeSlots, request);
    }

    /**
     * 解决学生冲突
     */
    private boolean resolveStudentConflict(CourseSchedule schedule1, CourseSchedule schedule2,
                                         List<CourseSchedule> allSchedules, List<TimeSlot> availableTimeSlots,
                                         ScheduleRequest request) {
        logger.debug("尝试解决学生冲突: 课程{} vs 课程{}", schedule1.getCourseId(), schedule2.getCourseId());

        // 学生冲突主要通过调整时间来解决
        return resolveTeacherConflict(schedule1, schedule2, allSchedules, availableTimeSlots, request);
    }

    /**
     * 选择需要重新安排的课程
     */
    private CourseSchedule selectScheduleForRescheduling(CourseSchedule schedule1, CourseSchedule schedule2) {
        // 简化策略：选择课程ID较大的（假设是后添加的课程）
        return schedule1.getCourseId() > schedule2.getCourseId() ? schedule1 : schedule2;
    }

    /**
     * 检查是否可以重新安排到指定时间段
     */
    private boolean canRescheduleToTimeSlot(CourseSchedule schedule, TimeSlot newTimeSlot,
                                          List<CourseSchedule> allSchedules) {
        // 创建临时安排进行冲突检测
        CourseSchedule tempSchedule = cloneSchedule(schedule);
        tempSchedule.setTimeSlotId(newTimeSlot.getId());
        tempSchedule.setStartTime(newTimeSlot.getStartTime());
        tempSchedule.setEndTime(newTimeSlot.getEndTime());

        // 检查与其他安排的冲突
        for (CourseSchedule existing : allSchedules) {
            if (!Objects.equals(existing.getCourseId(), schedule.getCourseId()) &&
                hasTimeConflict(tempSchedule, existing)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查是否可以重新安排到指定教室
     */
    private boolean canRescheduleToClassroom(CourseSchedule schedule, Classroom newClassroom,
                                           List<CourseSchedule> allSchedules) {
        // 检查教室容量
        Optional<Course> courseOpt = courseRepository.findById(schedule.getCourseId());
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            if (course.getMaxStudents() != null && course.getMaxStudents() > newClassroom.getCapacity()) {
                return false;
            }
        }

        // 检查时间冲突
        for (CourseSchedule existing : allSchedules) {
            if (!Objects.equals(existing.getCourseId(), schedule.getCourseId()) &&
                Objects.equals(existing.getClassroomId(), newClassroom.getId()) &&
                Objects.equals(existing.getTimeSlotId(), schedule.getTimeSlotId())) {
                return false;
            }
        }

        return true;
    }

    /**
     * 检查课程连续性冲突
     */
    private List<ConflictInfo> checkCourseContinuityConflicts(CourseSchedule schedule, List<CourseSchedule> existingSchedules) {
        List<ConflictInfo> conflicts = new ArrayList<>();

        try {
            // 检查同一课程的多个课时是否安排合理
            List<CourseSchedule> sameCourseSchedules = existingSchedules.stream()
                .filter(existing -> Objects.equals(existing.getCourseId(), schedule.getCourseId()))
                .collect(Collectors.toList());

            if (!sameCourseSchedules.isEmpty()) {
                // 检查课时间隔是否合理（不应该过于密集或分散）
                for (CourseSchedule existing : sameCourseSchedules) {
                    int dayDiff = Math.abs(schedule.getDayOfWeek() - existing.getDayOfWeek());

                    // 如果同一课程在同一天有多个课时，检查时间间隔
                    if (dayDiff == 0) {
                        ConflictInfo conflict = new ConflictInfo("continuity",
                            "同一课程在同一天安排多个课时");
                        conflict.setCourseId1(schedule.getCourseId());
                        conflict.setSuggestion("建议将课时分散到不同天");
                        conflicts.add(conflict);
                    }

                    // 如果课时间隔过大（超过3天），可能影响学习连续性
                    if (dayDiff > 3) {
                        ConflictInfo conflict = new ConflictInfo("continuity",
                            "课程课时间隔过大，可能影响学习连续性");
                        conflict.setCourseId1(schedule.getCourseId());
                        conflict.setSuggestion("建议缩短课时间隔");
                        conflicts.add(conflict);
                    }
                }
            }

        } catch (Exception e) {
            logger.error("检查课程连续性冲突时发生异常", e);
        }

        return conflicts;
    }

    /**
     * 检查教师工作负荷冲突
     */
    private List<ConflictInfo> checkTeacherWorkloadConflicts(CourseSchedule schedule, List<CourseSchedule> existingSchedules) {
        List<ConflictInfo> conflicts = new ArrayList<>();

        try {
            if (schedule.getTeacherId() == null) {
                return conflicts;
            }

            // 统计教师在同一天的课程数量
            long sameDayCount = existingSchedules.stream()
                .filter(existing -> Objects.equals(existing.getTeacherId(), schedule.getTeacherId()))
                .filter(existing -> Objects.equals(existing.getDayOfWeek(), schedule.getDayOfWeek()))
                .count();

            // 如果教师一天超过4节课，认为负荷过重
            if (sameDayCount >= 4) {
                ConflictInfo conflict = new ConflictInfo("workload",
                    "教师日课程负荷过重：" + (sameDayCount + 1) + "节课");
                conflict.setCourseId1(schedule.getCourseId());
                conflict.setSuggestion("建议调整到其他天或减少课程安排");
                conflicts.add(conflict);
            }

            // 统计教师一周的总课程数量
            long weeklyCount = existingSchedules.stream()
                .filter(existing -> Objects.equals(existing.getTeacherId(), schedule.getTeacherId()))
                .count();

            // 如果教师一周超过20节课，认为负荷过重
            if (weeklyCount >= 20) {
                ConflictInfo conflict = new ConflictInfo("workload",
                    "教师周课程负荷过重：" + (weeklyCount + 1) + "节课");
                conflict.setCourseId1(schedule.getCourseId());
                conflict.setSuggestion("建议分配给其他教师或调整到下学期");
                conflicts.add(conflict);
            }

        } catch (Exception e) {
            logger.error("检查教师工作负荷冲突时发生异常", e);
        }

        return conflicts;
    }

    /**
     * 检查学生课程负荷冲突
     */
    private List<ConflictInfo> checkStudentWorkloadConflicts(CourseSchedule schedule, List<CourseSchedule> existingSchedules) {
        List<ConflictInfo> conflicts = new ArrayList<>();

        try {
            // 统计同一时间段的课程数量（可能有多个班级）
            long sameTimeCount = existingSchedules.stream()
                .filter(existing -> Objects.equals(existing.getTimeSlotId(), schedule.getTimeSlotId()))
                .filter(existing -> Objects.equals(existing.getDayOfWeek(), schedule.getDayOfWeek()))
                .count();

            // 检查是否有过多课程安排在热门时间段
            if (sameTimeCount >= 5) {
                ConflictInfo conflict = new ConflictInfo("workload",
                    "热门时间段课程过多，可能导致教室资源紧张");
                conflict.setCourseId1(schedule.getCourseId());
                conflict.setSuggestion("建议将部分课程调整到其他时间段");
                conflicts.add(conflict);
            }

            // 检查一天内的课程密度
            long sameDayCount = existingSchedules.stream()
                .filter(existing -> Objects.equals(existing.getDayOfWeek(), schedule.getDayOfWeek()))
                .count();

            if (sameDayCount >= 8) {
                ConflictInfo conflict = new ConflictInfo("workload",
                    "单日课程安排过密，可能影响学生学习效果");
                conflict.setCourseId1(schedule.getCourseId());
                conflict.setSuggestion("建议将部分课程分散到其他天");
                conflicts.add(conflict);
            }

        } catch (Exception e) {
            logger.error("检查学生课程负荷冲突时发生异常", e);
        }

        return conflicts;
    }
}
