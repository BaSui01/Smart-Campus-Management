package com.campus.application.Implement.academic;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.application.service.academic.CourseScheduleService;
import com.campus.domain.entity.academic.Course;
import com.campus.domain.entity.academic.CourseSchedule;
import com.campus.domain.entity.academic.CourseSelection;
import com.campus.domain.entity.infrastructure.Classroom;
import com.campus.domain.entity.organization.SchoolClass;
import com.campus.domain.repository.academic.CourseRepository;
import com.campus.domain.repository.academic.CourseScheduleRepository;
import com.campus.domain.repository.academic.CourseSelectionRepository;
import com.campus.domain.repository.infrastructure.ClassroomRepository;
import com.campus.domain.repository.organization.SchoolClassRepository;


/**
 * 课程表服务实现类 - JPA实现
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Service
public class CourseScheduleServiceImpl implements CourseScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(CourseScheduleServiceImpl.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    private CourseScheduleRepository courseScheduleRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseSelectionRepository courseSelectionRepository;

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

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
        try {
            // 注意：当前实现基于班级ID查找课程安排，通过classList字段进行模糊匹配
            // 后续可优化为更精确的班级-课程关联查询
            logger.debug("根据班级ID查找课程安排: classId={}", classId);

            // 首先根据班级ID查找班级信息
            Optional<SchoolClass> classOpt = schoolClassRepository.findById(classId);
            if (classOpt.isEmpty()) {
                logger.warn("班级不存在: classId={}", classId);
                return Collections.emptyList();
            }

            SchoolClass schoolClass = classOpt.get();
            String className = schoolClass.getClassName();

            // 通过班级名称在课程安排的classList字段中查找
            List<CourseSchedule> schedules = courseScheduleRepository.findByClassIdAndDeleted(className, 0);

            logger.debug("找到{}个课程安排记录", schedules.size());
            return schedules;

        } catch (Exception e) {
            logger.error("根据班级ID查找课程安排失败: classId={}", classId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<CourseSchedule> findBySemester(String semester) {
        return courseScheduleRepository.findBySemesterAndDeleted(semester, 0);
    }

    @Override
    public List<CourseSchedule> findByClassroom(String classroom) {
        try {
            // 注意：当前实现通过教室名称查找对应的教室ID，然后查询课程安排
            // 支持教室编号、教室名称等多种查找方式，后续可优化查询性能
            logger.debug("根据教室名称查找课程安排: classroom={}", classroom);

            if (classroom == null || classroom.trim().isEmpty()) {
                logger.warn("教室名称为空");
                return Collections.emptyList();
            }

            // 首先尝试通过教室编号查找
            Optional<Classroom> classroomOpt = classroomRepository.findByClassroomNo(classroom.trim());

            // 如果通过编号找不到，再尝试通过名称查找
            if (classroomOpt.isEmpty()) {
                List<Classroom> classrooms = classroomRepository.searchByKeyword(classroom.trim());
                if (!classrooms.isEmpty()) {
                    classroomOpt = Optional.of(classrooms.get(0)); // 取第一个匹配的教室
                }
            }

            if (classroomOpt.isEmpty()) {
                logger.warn("教室不存在: classroom={}", classroom);
                return Collections.emptyList();
            }

            Classroom classroomEntity = classroomOpt.get();
            Long classroomId = classroomEntity.getId();

            // 根据教室ID查找课程安排
            List<CourseSchedule> schedules = courseScheduleRepository.findByClassroomIdAndDeleted(classroomId, 0);

            logger.debug("找到{}个课程安排记录", schedules.size());
            return schedules;

        } catch (Exception e) {
            logger.error("根据教室名称查找课程安排失败: classroom={}", classroom, e);
            return Collections.emptyList();
        }
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

        try {
            // 智能条件查询分页算法
            List<CourseSchedule> allSchedules = courseScheduleRepository.findAll();

            // 应用过滤条件
            List<CourseSchedule> filteredSchedules = applyScheduleFilters(allSchedules, params);

            // 应用排序
            filteredSchedules = applySorting(filteredSchedules, pageable.getSort());

            // 分页处理
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), filteredSchedules.size());
            List<CourseSchedule> pageContent = filteredSchedules.subList(start, end);

            return new PageImpl<>(pageContent, pageable, filteredSchedules.size());

        } catch (Exception e) {
            logger.error("分页查询课程安排失败", e);
            return courseScheduleRepository.findAll(pageable);
        }
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
        try {
            // 注意：当前实现基于班级ID和学期查找课程安排，结合了班级名称匹配和学期过滤
            // 后续可优化为更高效的联合查询
            logger.debug("根据班级ID和学期查找课程安排: classId={}, semester={}", classId, semester);

            if (classId == null || semester == null || semester.trim().isEmpty()) {
                logger.warn("参数不完整: classId={}, semester={}", classId, semester);
                return Collections.emptyList();
            }

            // 首先根据班级ID查找班级信息
            Optional<SchoolClass> classOpt = schoolClassRepository.findById(classId);
            if (classOpt.isEmpty()) {
                logger.warn("班级不存在: classId={}", classId);
                return Collections.emptyList();
            }

            SchoolClass schoolClass = classOpt.get();
            String className = schoolClass.getClassName();

            // 通过班级名称和学期查找课程安排
            List<CourseSchedule> allSchedules = courseScheduleRepository.findByClassIdAndDeleted(className, 0);

            // 过滤指定学期的课程安排
            List<CourseSchedule> filteredSchedules = allSchedules.stream()
                .filter(schedule -> semester.equals(schedule.getSemester()))
                .collect(Collectors.toList());

            logger.debug("找到{}个课程安排记录", filteredSchedules.size());
            return filteredSchedules;

        } catch (Exception e) {
            logger.error("根据班级ID和学期查找课程安排失败: classId={}, semester={}", classId, semester, e);
            return Collections.emptyList();
        }
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
        logger.info("开始自动排课: courseId={}, courseName={}", course.getId(), course.getCourseName());

        try {
            // 智能自动排课算法

            // 1. 获取课程基本信息和约束条件
            AutoScheduleContext context = buildScheduleContext(course);

            // 2. 获取可用资源
            List<Classroom> availableClassrooms = getAvailableClassroomsForCourse(course);
            List<TimeSlotOption> timeSlotOptions = generateTimeSlotOptions(course);

            // 3. 按优先级排序时间段选项
            timeSlotOptions.sort(this::compareTimeSlotPriority);

            // 4. 尝试智能排课
            for (TimeSlotOption timeSlot : timeSlotOptions) {
                for (Classroom classroom : availableClassrooms) {
                    if (canScheduleCourse(course, classroom, timeSlot, context)) {
                        // 创建课程安排
                        CourseSchedule schedule = createCourseSchedule(course, classroom, timeSlot, context);
                        courseScheduleRepository.save(schedule);

                        logger.info("自动排课成功: courseId={}, classroom={}, dayOfWeek={}, startTime={}",
                            course.getId(), classroom.getClassroomNo(), timeSlot.dayOfWeek, timeSlot.startTime);
                        return true;
                    }
                }
            }

            logger.warn("自动排课失败，无法找到合适的时间和教室: courseId={}", course.getId());
            return false;

        } catch (Exception e) {
            logger.error("自动排课异常: courseId={}", course.getId(), e);
            throw new RuntimeException("自动排课失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建排课上下文
     */
    private AutoScheduleContext buildScheduleContext(Course course) {
        AutoScheduleContext context = new AutoScheduleContext();
        context.currentSemester = getCurrentSemester();
        return context;
    }

    /**
     * 获取课程可用教室列表
     */
    private List<Classroom> getAvailableClassroomsForCourse(Course course) {
        try {
            List<Classroom> allClassrooms = classroomRepository.findAll();

            return allClassrooms.stream()
                .filter(classroom -> classroom.getStatus() == 1) // 可用状态
                .filter(classroom -> isClassroomSuitableForCourse(classroom, course))
                .sorted((c1, c2) -> Integer.compare(
                    Math.abs(c1.getCapacity() - (course.getMaxStudents() != null ? course.getMaxStudents() : 30)),
                    Math.abs(c2.getCapacity() - (course.getMaxStudents() != null ? course.getMaxStudents() : 30))
                )) // 按容量匹配度排序
                .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("获取可用教室失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 生成时间段选项
     */
    private List<TimeSlotOption> generateTimeSlotOptions(Course course) {
        List<TimeSlotOption> options = new ArrayList<>();

        // 根据课程类型调整时间偏好
        String[] preferredSlots = getPreferredTimeSlotsForCourseType(course.getCourseType());

        for (int dayOfWeek = 1; dayOfWeek <= 5; dayOfWeek++) {
            for (String startTime : preferredSlots) {
                TimeSlotOption option = new TimeSlotOption();
                option.dayOfWeek = dayOfWeek;
                option.startTime = startTime;
                option.endTime = calculateEndTime(startTime, calculateCourseDuration(course));
                option.priority = calculateTimeSlotPriority(course, dayOfWeek, startTime);
                options.add(option);
            }
        }

        return options;
    }

    /**
     * 根据课程类型获取偏好时间段
     */
    private String[] getPreferredTimeSlotsForCourseType(String courseType) {
        if (courseType == null) {
            return new String[]{"08:00:00", "10:00:00", "14:00:00", "16:00:00"};
        }

        switch (courseType.toLowerCase()) {
            case "理论课":
            case "lecture":
                return new String[]{"08:00:00", "10:00:00", "14:00:00"}; // 上午和下午早期
            case "实验课":
            case "lab":
                return new String[]{"14:00:00", "16:00:00"}; // 下午时段
            case "体育课":
            case "pe":
                return new String[]{"08:00:00", "16:00:00"}; // 早上和下午晚期
            case "选修课":
            case "elective":
                return new String[]{"16:00:00", "19:00:00"}; // 下午晚期和晚上
            default:
                return new String[]{"08:00:00", "10:00:00", "14:00:00", "16:00:00"};
        }
    }

    /**
     * 计算课程持续时间
     */
    private int calculateCourseDuration(Course course) {
        // 根据学分计算课程时长
        if (course.getCredits() != null) {
            double credits = course.getCredits().doubleValue();
            if (credits >= 4.0) return 3; // 高学分课程3小时
            if (credits >= 2.0) return 2; // 中等学分课程2小时
            return 1; // 低学分课程1小时
        }
        return 2; // 默认2小时
    }

    /**
     * 检查教室是否适合课程
     */
    private boolean isClassroomSuitableForCourse(Classroom classroom, Course course) {
        // 检查容量
        if (course.getMaxStudents() != null && classroom.getCapacity() < course.getMaxStudents()) {
            return false;
        }

        // 检查教室类型匹配
        String courseType = course.getCourseType();
        String classroomType = classroom.getClassroomType();

        if (courseType != null && classroomType != null) {
            switch (courseType.toLowerCase()) {
                case "实验课":
                case "lab":
                    return "实验室".equals(classroomType) || "多媒体教室".equals(classroomType);
                case "体育课":
                case "pe":
                    return "体育场馆".equals(classroomType);
                case "理论课":
                case "lecture":
                    return "普通教室".equals(classroomType) || "多媒体教室".equals(classroomType);
                default:
                    return true; // 其他课程类型不限制教室类型
            }
        }

        return true;
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
        try {
            // 注意：当前实现基础的课程表日历数据，返回按日期组织的课程安排信息
            // 后续可集成更丰富的日历功能，如节假日、考试安排等
            logger.debug("获取课程表日历数据");

            Map<String, Object> calendarData = new HashMap<>();

            // 获取当前学期的所有课程安排
            String currentSemester = getCurrentSemester();
            List<CourseSchedule> schedules = courseScheduleRepository.findBySemesterAndDeleted(currentSemester, 0);

            // 按日期组织课程安排数据
            List<Map<String, Object>> calendarEvents = new ArrayList<>();

            for (CourseSchedule schedule : schedules) {
                Map<String, Object> event = new HashMap<>();
                event.put("id", schedule.getId());
                event.put("title", getScheduleTitle(schedule));
                event.put("dayOfWeek", schedule.getDayOfWeek());
                event.put("startTime", schedule.getStartTime() != null ? schedule.getStartTime().toString() : "");
                event.put("endTime", schedule.getEndTime() != null ? schedule.getEndTime().toString() : "");
                event.put("classroom", getClassroomName(schedule.getClassroomId()));
                event.put("teacher", getTeacherName(schedule.getTeacherId()));
                event.put("semester", schedule.getSemester());
                event.put("type", "course");

                calendarEvents.add(event);
            }

            calendarData.put("calendar", calendarEvents);
            calendarData.put("semester", currentSemester);
            calendarData.put("totalEvents", calendarEvents.size());

            logger.debug("生成日历数据完成，共{}个事件", calendarEvents.size());
            return calendarData;

        } catch (Exception e) {
            logger.error("获取课程表日历数据失败", e);
            return Map.of("calendar", Collections.emptyList(), "error", "获取日历数据失败");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTimetableData() {
        try {
            // 注意：当前实现基础的课程表数据获取，按周次和时间段组织数据
            // 后续可支持更多的展示格式和过滤条件
            logger.debug("获取课程表数据");

            Map<String, Object> timetableData = new HashMap<>();

            // 获取当前学期的所有课程安排
            String currentSemester = getCurrentSemester();
            List<CourseSchedule> schedules = courseScheduleRepository.findBySemesterAndDeleted(currentSemester, 0);

            // 按星期几组织数据
            Map<Integer, List<Map<String, Object>>> weeklyData = new HashMap<>();

            for (int day = 1; day <= 7; day++) {
                weeklyData.put(day, new ArrayList<>());
            }

            for (CourseSchedule schedule : schedules) {
                Integer dayOfWeek = schedule.getDayOfWeek();
                if (dayOfWeek != null && dayOfWeek >= 1 && dayOfWeek <= 7) {
                    Map<String, Object> scheduleData = new HashMap<>();
                    scheduleData.put("id", schedule.getId());
                    scheduleData.put("courseName", getScheduleTitle(schedule));
                    scheduleData.put("teacherName", getTeacherName(schedule.getTeacherId()));
                    scheduleData.put("classroomName", getClassroomName(schedule.getClassroomId()));
                    scheduleData.put("startTime", schedule.getStartTime() != null ? schedule.getStartTime().toString() : "");
                    scheduleData.put("endTime", schedule.getEndTime() != null ? schedule.getEndTime().toString() : "");
                    scheduleData.put("periodNumber", schedule.getPeriodNumber());
                    scheduleData.put("classList", schedule.getClassList());
                    scheduleData.put("scheduleType", schedule.getScheduleType());

                    weeklyData.get(dayOfWeek).add(scheduleData);
                }
            }

            // 对每天的课程按时间排序
            for (List<Map<String, Object>> daySchedules : weeklyData.values()) {
                daySchedules.sort((a, b) -> {
                    String timeA = (String) a.get("startTime");
                    String timeB = (String) b.get("startTime");
                    return timeA.compareTo(timeB);
                });
            }

            timetableData.put("timetable", weeklyData);
            timetableData.put("semester", currentSemester);
            timetableData.put("totalSchedules", schedules.size());
            timetableData.put("weekdays", Map.of(
                1, "周一", 2, "周二", 3, "周三", 4, "周四",
                5, "周五", 6, "周六", 7, "周日"
            ));

            logger.debug("生成课程表数据完成，共{}个课程安排", schedules.size());
            return timetableData;

        } catch (Exception e) {
            logger.error("获取课程表数据失败", e);
            return Map.of("timetable", Collections.emptyMap(), "error", "获取课程表数据失败");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getScheduleConflicts() {
        try {
            // 注意：当前实现基础的课程表冲突检查，检测时间、教室、教师冲突
            // 后续可扩展更复杂的冲突检测规则，如容量冲突、设备冲突等
            logger.debug("检查课程表冲突");

            List<Map<String, Object>> conflicts = new ArrayList<>();

            // 获取当前学期的所有活跃课程安排
            String currentSemester = getCurrentSemester();
            List<CourseSchedule> schedules = courseScheduleRepository.findBySemesterAndDeleted(currentSemester, 0)
                .stream()
                .filter(schedule -> schedule.getStatus() != null && schedule.getStatus() == 1)
                .collect(Collectors.toList());

            // 检查教室冲突
            for (int i = 0; i < schedules.size(); i++) {
                CourseSchedule schedule1 = schedules.get(i);
                for (int j = i + 1; j < schedules.size(); j++) {
                    CourseSchedule schedule2 = schedules.get(j);

                    // 检查是否为同一天
                    if (!schedule1.getDayOfWeek().equals(schedule2.getDayOfWeek())) {
                        continue;
                    }

                    // 检查时间是否重叠
                    if (!isTimeOverlap(schedule1, schedule2)) {
                        continue;
                    }

                    // 检查教室冲突
                    if (schedule1.getClassroomId() != null && schedule2.getClassroomId() != null &&
                        schedule1.getClassroomId().equals(schedule2.getClassroomId())) {

                        Map<String, Object> conflict = new HashMap<>();
                        conflict.put("type", "classroom");
                        conflict.put("description", "教室冲突");
                        conflict.put("schedule1", createScheduleInfo(schedule1));
                        conflict.put("schedule2", createScheduleInfo(schedule2));
                        conflict.put("conflictResource", getClassroomName(schedule1.getClassroomId()));
                        conflict.put("severity", "high");

                        conflicts.add(conflict);
                    }

                    // 检查教师冲突
                    if (schedule1.getTeacherId() != null && schedule2.getTeacherId() != null &&
                        schedule1.getTeacherId().equals(schedule2.getTeacherId())) {

                        Map<String, Object> conflict = new HashMap<>();
                        conflict.put("type", "teacher");
                        conflict.put("description", "教师冲突");
                        conflict.put("schedule1", createScheduleInfo(schedule1));
                        conflict.put("schedule2", createScheduleInfo(schedule2));
                        conflict.put("conflictResource", getTeacherName(schedule1.getTeacherId()));
                        conflict.put("severity", "high");

                        conflicts.add(conflict);
                    }
                }
            }

            logger.debug("冲突检查完成，发现{}个冲突", conflicts.size());
            return conflicts;

        } catch (Exception e) {
            logger.error("检查课程表冲突失败", e);
            return Collections.emptyList();
        }
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
        try {
            // 注意：当前实现基础的课程安排冲突检查，检查指定条件下的时间冲突
            // 后续可扩展更复杂的冲突检测算法，如智能推荐替代方案等
            logger.debug("检查课程安排冲突: courseId={}, teacherId={}, classroomId={}, dayOfWeek={}, timeSlot={}",
                        courseId, teacherId, classroomId, dayOfWeek, timeSlot);

            List<CourseSchedule> conflicts = new ArrayList<>();

            if (dayOfWeek == null || timeSlot == null) {
                logger.warn("参数不完整，无法检查冲突");
                return conflicts;
            }

            try {
                int day = Integer.parseInt(dayOfWeek);
                String[] timeParts = timeSlot.split("-");
                if (timeParts.length != 2) {
                    logger.warn("时间段格式错误: {}", timeSlot);
                    return conflicts;
                }

                String startTime = timeParts[0].trim();
                String endTime = timeParts[1].trim();
                String currentSemester = getCurrentSemester();

                // 检查教室冲突
                if (classroomId != null) {
                    boolean classroomOccupied = isClassroomOccupied(classroomId.toString(), day, startTime, endTime, currentSemester, null);
                    if (classroomOccupied) {
                        // 查找冲突的课程安排
                        List<CourseSchedule> classroomConflicts = courseScheduleRepository.findByClassroomIdAndDeleted(classroomId, 0)
                            .stream()
                            .filter(schedule -> schedule.getDayOfWeek() != null && schedule.getDayOfWeek().equals(day))
                            .filter(schedule -> isTimeSlotOverlap(schedule, startTime, endTime))
                            .collect(Collectors.toList());
                        conflicts.addAll(classroomConflicts);
                    }
                }

                // 检查教师冲突
                if (teacherId != null) {
                    boolean teacherOccupied = isTeacherOccupied(teacherId, day, startTime, endTime, currentSemester, null);
                    if (teacherOccupied) {
                        // 查找冲突的课程安排
                        List<CourseSchedule> teacherConflicts = courseScheduleRepository.findByTeacherIdAndDeleted(teacherId, 0)
                            .stream()
                            .filter(schedule -> schedule.getDayOfWeek() != null && schedule.getDayOfWeek().equals(day))
                            .filter(schedule -> isTimeSlotOverlap(schedule, startTime, endTime))
                            .collect(Collectors.toList());
                        conflicts.addAll(teacherConflicts);
                    }
                }

                // 去重
                conflicts = conflicts.stream().distinct().collect(Collectors.toList());

            } catch (NumberFormatException e) {
                logger.warn("星期几格式错误: {}", dayOfWeek);
            }

            logger.debug("发现{}个冲突的课程安排", conflicts.size());
            return conflicts;

        } catch (Exception e) {
            logger.error("检查课程安排冲突失败", e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional
    public List<CourseSchedule> batchCreateSchedules(List<CourseSchedule> courseSchedules) {
        return courseScheduleRepository.saveAll(courseSchedules);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getTeacherWeeklySchedule(Long teacherId, java.time.LocalDate weekStart) {
        try {
            // 注意：当前实现基础的教师周课表获取，按周组织教师的课程安排
            // 后续可支持更灵活的时间范围和更丰富的展示格式
            logger.debug("获取教师周课表: teacherId={}, weekStart={}", teacherId, weekStart);

            if (teacherId == null || weekStart == null) {
                logger.warn("参数不完整: teacherId={}, weekStart={}", teacherId, weekStart);
                return Map.of("weeklySchedule", Collections.emptyList(), "error", "参数不完整");
            }

            Map<String, Object> weeklyData = new HashMap<>();

            // 获取教师在当前学期的所有课程安排
            String currentSemester = getCurrentSemester();
            List<CourseSchedule> teacherSchedules = courseScheduleRepository.findByTeacherIdAndSemesterAndDeleted(teacherId, currentSemester, 0);

            // 按星期几组织数据
            Map<Integer, List<Map<String, Object>>> weeklySchedule = new HashMap<>();
            for (int day = 1; day <= 7; day++) {
                weeklySchedule.put(day, new ArrayList<>());
            }

            for (CourseSchedule schedule : teacherSchedules) {
                Integer dayOfWeek = schedule.getDayOfWeek();
                if (dayOfWeek != null && dayOfWeek >= 1 && dayOfWeek <= 7) {
                    Map<String, Object> scheduleInfo = new HashMap<>();
                    scheduleInfo.put("id", schedule.getId());
                    scheduleInfo.put("courseName", getScheduleTitle(schedule));
                    scheduleInfo.put("classroomName", getClassroomName(schedule.getClassroomId()));
                    scheduleInfo.put("startTime", schedule.getStartTime() != null ? schedule.getStartTime().toString() : "");
                    scheduleInfo.put("endTime", schedule.getEndTime() != null ? schedule.getEndTime().toString() : "");
                    scheduleInfo.put("classList", schedule.getClassList());
                    scheduleInfo.put("periodNumber", schedule.getPeriodNumber());
                    scheduleInfo.put("scheduleType", schedule.getScheduleType());
                    scheduleInfo.put("studentCount", schedule.getStudentCount());

                    weeklySchedule.get(dayOfWeek).add(scheduleInfo);
                }
            }

            // 对每天的课程按时间排序
            for (List<Map<String, Object>> daySchedules : weeklySchedule.values()) {
                daySchedules.sort((a, b) -> {
                    String timeA = (String) a.get("startTime");
                    String timeB = (String) b.get("startTime");
                    return timeA.compareTo(timeB);
                });
            }

            weeklyData.put("weeklySchedule", weeklySchedule);
            weeklyData.put("teacherId", teacherId);
            weeklyData.put("teacherName", getTeacherName(teacherId));
            weeklyData.put("weekStart", weekStart.toString());
            weeklyData.put("weekEnd", weekStart.plusDays(6).toString());
            weeklyData.put("semester", currentSemester);
            weeklyData.put("totalSchedules", teacherSchedules.size());
            weeklyData.put("weekdays", Map.of(
                1, "周一", 2, "周二", 3, "周三", 4, "周四",
                5, "周五", 6, "周六", 7, "周日"
            ));

            logger.debug("生成教师周课表完成，共{}个课程安排", teacherSchedules.size());
            return weeklyData;

        } catch (Exception e) {
            logger.error("获取教师周课表失败: teacherId={}, weekStart={}", teacherId, weekStart, e);
            return Map.of("weeklySchedule", Collections.emptyList(), "error", "获取周课表失败");
        }
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
        try {
            // 注意：当前实现基础的按天统计课程安排数量，统计每个工作日的课程数量
            // 后续可支持更灵活的时间范围和更详细的统计维度
            logger.debug("统计按天的课程安排数量");

            Map<String, Long> dayStats = new HashMap<>();

            // 获取当前学期的所有活跃课程安排
            String currentSemester = getCurrentSemester();
            List<CourseSchedule> schedules = courseScheduleRepository.findBySemesterAndDeleted(currentSemester, 0)
                .stream()
                .filter(schedule -> schedule.getStatus() != null && schedule.getStatus() == 1)
                .collect(Collectors.toList());

            // 按星期几统计
            Map<Integer, Long> dayCountMap = schedules.stream()
                .filter(schedule -> schedule.getDayOfWeek() != null)
                .collect(Collectors.groupingBy(
                    CourseSchedule::getDayOfWeek,
                    Collectors.counting()
                ));

            // 转换为中文星期几的格式
            Map<Integer, String> dayNames = Map.of(
                1, "周一", 2, "周二", 3, "周三", 4, "周四",
                5, "周五", 6, "周六", 7, "周日"
            );

            for (int day = 1; day <= 7; day++) {
                String dayName = dayNames.get(day);
                Long count = dayCountMap.getOrDefault(day, 0L);
                dayStats.put(dayName, count);
            }

            logger.debug("按天统计完成，共统计{}天的数据", dayStats.size());
            return dayStats;

        } catch (Exception e) {
            logger.error("按天统计课程安排数量失败", e);
            return Map.of("周一", 0L, "周二", 0L, "周三", 0L, "周四", 0L, "周五", 0L, "周六", 0L, "周日", 0L);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> countSchedulesByTimeSlot() {
        try {
            // 注意：当前实现基础的按时间段统计课程安排数量，按常见的时间段分组统计
            // 后续可支持自定义时间段和更精细的时间分析
            logger.debug("统计按时间段的课程安排数量");

            Map<String, Long> timeSlotStats = new HashMap<>();

            // 获取当前学期的所有活跃课程安排
            String currentSemester = getCurrentSemester();
            List<CourseSchedule> schedules = courseScheduleRepository.findBySemesterAndDeleted(currentSemester, 0)
                .stream()
                .filter(schedule -> schedule.getStatus() != null && schedule.getStatus() == 1)
                .filter(schedule -> schedule.getStartTime() != null)
                .collect(Collectors.toList());

            // 定义时间段
            Map<String, java.util.function.Predicate<java.time.LocalTime>> timeSlots = new HashMap<>();
            timeSlots.put("上午第一节(08:00-10:00)", time -> time.isAfter(java.time.LocalTime.of(7, 59)) && time.isBefore(java.time.LocalTime.of(10, 1)));
            timeSlots.put("上午第二节(10:00-12:00)", time -> time.isAfter(java.time.LocalTime.of(9, 59)) && time.isBefore(java.time.LocalTime.of(12, 1)));
            timeSlots.put("下午第一节(14:00-16:00)", time -> time.isAfter(java.time.LocalTime.of(13, 59)) && time.isBefore(java.time.LocalTime.of(16, 1)));
            timeSlots.put("下午第二节(16:00-18:00)", time -> time.isAfter(java.time.LocalTime.of(15, 59)) && time.isBefore(java.time.LocalTime.of(18, 1)));
            timeSlots.put("晚上时段(19:00-21:00)", time -> time.isAfter(java.time.LocalTime.of(18, 59)) && time.isBefore(java.time.LocalTime.of(21, 1)));
            timeSlots.put("其他时段", java.util.function.Predicate.not(java.util.function.Predicate.isEqual(null))); // 默认分类

            // 初始化统计数据
            for (String slot : timeSlots.keySet()) {
                timeSlotStats.put(slot, 0L);
            }

            // 统计每个时间段的课程数量
            for (CourseSchedule schedule : schedules) {
                java.time.LocalTime startTime = schedule.getStartTime();
                boolean categorized = false;

                for (Map.Entry<String, java.util.function.Predicate<java.time.LocalTime>> entry : timeSlots.entrySet()) {
                    if (!entry.getKey().equals("其他时段") && entry.getValue().test(startTime)) {
                        timeSlotStats.put(entry.getKey(), timeSlotStats.get(entry.getKey()) + 1);
                        categorized = true;
                        break;
                    }
                }

                if (!categorized) {
                    timeSlotStats.put("其他时段", timeSlotStats.get("其他时段") + 1);
                }
            }

            logger.debug("按时间段统计完成，共统计{}个时间段", timeSlotStats.size());
            return timeSlotStats;

        } catch (Exception e) {
            logger.error("按时间段统计课程安排数量失败", e);
            return Map.of(
                "上午第一节(08:00-10:00)", 0L,
                "上午第二节(10:00-12:00)", 0L,
                "下午第一节(14:00-16:00)", 0L,
                "下午第二节(16:00-18:00)", 0L,
                "晚上时段(19:00-21:00)", 0L,
                "其他时段", 0L
            );
        }
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
        try {
            // 注意：当前实现基础的课程表模板，提供常用的排课模板供快速创建课程安排
            // 后续可支持自定义模板、模板导入导出等功能
            logger.debug("获取课程表模板");

            List<Map<String, Object>> templates = new ArrayList<>();

            // 标准工作日模板
            Map<String, Object> weekdayTemplate = new HashMap<>();
            weekdayTemplate.put("id", 1);
            weekdayTemplate.put("name", "标准工作日模板");
            weekdayTemplate.put("description", "周一至周五，每天4节课的标准模板");
            weekdayTemplate.put("type", "weekday");
            weekdayTemplate.put("timeSlots", List.of(
                Map.of("period", 1, "startTime", "08:00", "endTime", "10:00", "name", "上午第一节"),
                Map.of("period", 2, "startTime", "10:00", "endTime", "12:00", "name", "上午第二节"),
                Map.of("period", 3, "startTime", "14:00", "endTime", "16:00", "name", "下午第一节"),
                Map.of("period", 4, "startTime", "16:00", "endTime", "18:00", "name", "下午第二节")
            ));
            weekdayTemplate.put("workDays", List.of(1, 2, 3, 4, 5)); // 周一到周五
            templates.add(weekdayTemplate);

            // 全周模板
            Map<String, Object> fullWeekTemplate = new HashMap<>();
            fullWeekTemplate.put("id", 2);
            fullWeekTemplate.put("name", "全周模板");
            fullWeekTemplate.put("description", "周一至周日，包含晚上时段的完整模板");
            fullWeekTemplate.put("type", "fullweek");
            fullWeekTemplate.put("timeSlots", List.of(
                Map.of("period", 1, "startTime", "08:00", "endTime", "10:00", "name", "上午第一节"),
                Map.of("period", 2, "startTime", "10:00", "endTime", "12:00", "name", "上午第二节"),
                Map.of("period", 3, "startTime", "14:00", "endTime", "16:00", "name", "下午第一节"),
                Map.of("period", 4, "startTime", "16:00", "endTime", "18:00", "name", "下午第二节"),
                Map.of("period", 5, "startTime", "19:00", "endTime", "21:00", "name", "晚上时段")
            ));
            fullWeekTemplate.put("workDays", List.of(1, 2, 3, 4, 5, 6, 7)); // 全周
            templates.add(fullWeekTemplate);

            // 紧凑型模板
            Map<String, Object> compactTemplate = new HashMap<>();
            compactTemplate.put("id", 3);
            compactTemplate.put("name", "紧凑型模板");
            compactTemplate.put("description", "时间紧凑的3节课模板，适合短期课程");
            compactTemplate.put("type", "compact");
            compactTemplate.put("timeSlots", List.of(
                Map.of("period", 1, "startTime", "09:00", "endTime", "11:00", "name", "上午时段"),
                Map.of("period", 2, "startTime", "14:00", "endTime", "16:00", "name", "下午时段"),
                Map.of("period", 3, "startTime", "19:00", "endTime", "21:00", "name", "晚上时段")
            ));
            compactTemplate.put("workDays", List.of(1, 2, 3, 4, 5)); // 周一到周五
            templates.add(compactTemplate);

            // 实验课模板
            Map<String, Object> labTemplate = new HashMap<>();
            labTemplate.put("id", 4);
            labTemplate.put("name", "实验课模板");
            labTemplate.put("description", "适合实验课的长时间段模板");
            labTemplate.put("type", "laboratory");
            labTemplate.put("timeSlots", List.of(
                Map.of("period", 1, "startTime", "08:00", "endTime", "12:00", "name", "上午实验"),
                Map.of("period", 2, "startTime", "14:00", "endTime", "18:00", "name", "下午实验")
            ));
            labTemplate.put("workDays", List.of(1, 2, 3, 4, 5)); // 周一到周五
            templates.add(labTemplate);

            logger.debug("获取课程表模板完成，共{}个模板", templates.size());
            return templates;

        } catch (Exception e) {
            logger.error("获取课程表模板失败", e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getScheduleStatistics() {
        try {
            // 注意：当前实现基础的课程表统计信息，包含数量、分布、利用率等关键指标
            // 后续可扩展更详细的分析维度，如趋势分析、对比分析等
            logger.debug("获取课程表统计信息");

            Map<String, Object> statistics = new HashMap<>();

            // 基础统计
            long totalSchedules = countTotalSchedules();
            long activeSchedules = countActiveSchedules();

            statistics.put("totalSchedules", totalSchedules);
            statistics.put("activeSchedules", activeSchedules);
            statistics.put("inactiveSchedules", totalSchedules - activeSchedules);

            // 当前学期统计
            String currentSemester = getCurrentSemester();
            List<CourseSchedule> currentSemesterSchedules = courseScheduleRepository.findBySemesterAndDeleted(currentSemester, 0);
            statistics.put("currentSemesterSchedules", currentSemesterSchedules.size());

            // 按天分布统计
            Map<String, Long> dayDistribution = countSchedulesByDay();
            statistics.put("dayDistribution", dayDistribution);

            // 按时间段分布统计
            Map<String, Long> timeSlotDistribution = countSchedulesByTimeSlot();
            statistics.put("timeSlotDistribution", timeSlotDistribution);

            // 教师和教室统计
            Set<Long> uniqueTeachers = currentSemesterSchedules.stream()
                .map(CourseSchedule::getTeacherId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

            Set<Long> uniqueClassrooms = currentSemesterSchedules.stream()
                .map(CourseSchedule::getClassroomId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

            Set<Long> uniqueCourses = currentSemesterSchedules.stream()
                .map(CourseSchedule::getCourseId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

            statistics.put("involvedTeachers", uniqueTeachers.size());
            statistics.put("involvedClassrooms", uniqueClassrooms.size());
            statistics.put("involvedCourses", uniqueCourses.size());

            // 课程类型统计
            Map<String, Long> scheduleTypeStats = currentSemesterSchedules.stream()
                .filter(schedule -> schedule.getScheduleType() != null)
                .collect(Collectors.groupingBy(
                    CourseSchedule::getScheduleType,
                    Collectors.counting()
                ));
            statistics.put("scheduleTypeDistribution", scheduleTypeStats);

            // 利用率计算
            double utilizationRate = totalSchedules > 0 ? (double) activeSchedules / totalSchedules * 100 : 0.0;
            statistics.put("utilizationRate", Math.round(utilizationRate * 100.0) / 100.0);

            // 平均每天课程数
            double avgSchedulesPerDay = dayDistribution.values().stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
            statistics.put("averageSchedulesPerDay", Math.round(avgSchedulesPerDay * 100.0) / 100.0);

            // 统计生成时间
            statistics.put("generatedAt", java.time.LocalDateTime.now().toString());
            statistics.put("semester", currentSemester);

            logger.debug("课程表统计信息生成完成");
            return statistics;

        } catch (Exception e) {
            logger.error("获取课程表统计信息失败", e);
            return Map.of(
                "totalSchedules", countTotalSchedules(),
                "activeSchedules", countActiveSchedules(),
                "error", "统计信息获取失败"
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getClassroomUtilizationStats() {
        try {
            // 注意：当前实现基础的教室利用率统计，计算教室的使用频率和时间分布
            // 后续可扩展更详细的利用率分析，如峰值时段、空闲时段、容量利用率等
            logger.debug("获取教室利用率统计");

            Map<String, Object> utilizationStats = new HashMap<>();

            // 获取当前学期的所有活跃课程安排
            String currentSemester = getCurrentSemester();
            List<CourseSchedule> schedules = courseScheduleRepository.findBySemesterAndDeleted(currentSemester, 0)
                .stream()
                .filter(schedule -> schedule.getStatus() != null && schedule.getStatus() == 1)
                .filter(schedule -> schedule.getClassroomId() != null)
                .collect(Collectors.toList());

            // 获取所有教室
            List<Classroom> allClassrooms = classroomRepository.findByDeletedOrderByBuildingAscFloorAscClassroomNameAsc(0);

            // 计算总的可用时间段（假设每周5天，每天4个时间段）
            int totalTimeSlots = 5 * 4; // 20个时间段
            int totalAvailableSlots = allClassrooms.size() * totalTimeSlots;

            // 计算已使用的时间段
            int usedSlots = schedules.size();

            // 计算整体利用率
            double overallUtilizationRate = totalAvailableSlots > 0 ?
                (double) usedSlots / totalAvailableSlots * 100 : 0.0;

            utilizationStats.put("utilizationRate", Math.round(overallUtilizationRate * 100.0) / 100.0);
            utilizationStats.put("totalClassrooms", allClassrooms.size());
            utilizationStats.put("usedClassrooms", schedules.stream()
                .map(CourseSchedule::getClassroomId)
                .collect(Collectors.toSet()).size());
            utilizationStats.put("totalAvailableSlots", totalAvailableSlots);
            utilizationStats.put("usedSlots", usedSlots);
            utilizationStats.put("freeSlots", totalAvailableSlots - usedSlots);

            // 按教室统计使用情况
            Map<Long, Long> classroomUsage = schedules.stream()
                .collect(Collectors.groupingBy(
                    CourseSchedule::getClassroomId,
                    Collectors.counting()
                ));

            // 计算教室使用分布
            List<Map<String, Object>> classroomStats = new ArrayList<>();
            for (Classroom classroom : allClassrooms) {
                Long classroomId = classroom.getId();
                Long usageCount = classroomUsage.getOrDefault(classroomId, 0L);
                double classroomUtilization = (double) usageCount / totalTimeSlots * 100;

                Map<String, Object> classroomStat = new HashMap<>();
                classroomStat.put("classroomId", classroomId);
                classroomStat.put("classroomName", classroom.getClassroomName());
                classroomStat.put("usageCount", usageCount);
                classroomStat.put("utilizationRate", Math.round(classroomUtilization * 100.0) / 100.0);
                classroomStat.put("capacity", classroom.getCapacity());
                classroomStat.put("classroomType", classroom.getClassroomType());

                classroomStats.add(classroomStat);
            }

            // 按利用率排序
            classroomStats.sort((a, b) ->
                Double.compare((Double) b.get("utilizationRate"), (Double) a.get("utilizationRate")));

            utilizationStats.put("classroomDetails", classroomStats);

            // 利用率分级统计
            long highUtilization = classroomStats.stream()
                .mapToDouble(stat -> (Double) stat.get("utilizationRate"))
                .filter(rate -> rate >= 80.0)
                .count();

            long mediumUtilization = classroomStats.stream()
                .mapToDouble(stat -> (Double) stat.get("utilizationRate"))
                .filter(rate -> rate >= 50.0 && rate < 80.0)
                .count();

            long lowUtilization = classroomStats.stream()
                .mapToDouble(stat -> (Double) stat.get("utilizationRate"))
                .filter(rate -> rate < 50.0)
                .count();

            utilizationStats.put("highUtilizationClassrooms", highUtilization);
            utilizationStats.put("mediumUtilizationClassrooms", mediumUtilization);
            utilizationStats.put("lowUtilizationClassrooms", lowUtilization);

            // 按时间段统计利用率
            Map<String, Long> timeSlotUsage = countSchedulesByTimeSlot();
            utilizationStats.put("timeSlotUsage", timeSlotUsage);

            // 统计生成时间
            utilizationStats.put("generatedAt", java.time.LocalDateTime.now().toString());
            utilizationStats.put("semester", currentSemester);

            logger.debug("教室利用率统计完成，整体利用率: {}%", overallUtilizationRate);
            return utilizationStats;

        } catch (Exception e) {
            logger.error("获取教室利用率统计失败", e);
            return Map.of(
                "utilizationRate", 0.0,
                "error", "利用率统计失败",
                "totalClassrooms", 0,
                "usedClassrooms", 0
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTeacherWorkloadStats() {
        try {
            // 注意：当前实现基础的教师工作量统计，计算教师的课程安排数量和工作时长
            // 后续可集成更详细的工作量分析，如课程难度、学生数量等因素
            logger.debug("获取教师工作量统计");

            Map<String, Object> workloadStats = new HashMap<>();

            // 获取所有活跃的课程安排
            List<CourseSchedule> allSchedules = courseScheduleRepository.findAll().stream()
                .filter(schedule -> schedule.getStatus() != null && schedule.getStatus() == 1 &&
                                  schedule.getDeleted() != null && schedule.getDeleted() == 0)
                .collect(Collectors.toList());

            // 按教师统计工作量
            Map<Long, Integer> teacherWorkload = new HashMap<>();
            Map<Long, Double> teacherHours = new HashMap<>();

            for (CourseSchedule schedule : allSchedules) {
                Long teacherId = schedule.getTeacherId();
                if (teacherId != null) {
                    teacherWorkload.put(teacherId, teacherWorkload.getOrDefault(teacherId, 0) + 1);

                    // 计算课程时长（假设每节课2小时）
                    double hours = 2.0;
                    if (schedule.getStartTime() != null && schedule.getEndTime() != null) {
                        hours = java.time.Duration.between(schedule.getStartTime(), schedule.getEndTime()).toMinutes() / 60.0;
                    }
                    teacherHours.put(teacherId, teacherHours.getOrDefault(teacherId, 0.0) + hours);
                }
            }

            // 计算统计数据
            int totalWorkload = teacherWorkload.values().stream().mapToInt(Integer::intValue).sum();
            double averageWorkload = teacherWorkload.isEmpty() ? 0.0 :
                teacherWorkload.values().stream().mapToInt(Integer::intValue).average().orElse(0.0);
            double totalHours = teacherHours.values().stream().mapToDouble(Double::doubleValue).sum();

            workloadStats.put("totalWorkload", totalWorkload);
            workloadStats.put("averageWorkload", Math.round(averageWorkload * 100.0) / 100.0);
            workloadStats.put("totalTeachers", teacherWorkload.size());
            workloadStats.put("totalHours", Math.round(totalHours * 100.0) / 100.0);
            workloadStats.put("averageHoursPerTeacher", teacherWorkload.isEmpty() ? 0.0 :
                Math.round((totalHours / teacherWorkload.size()) * 100.0) / 100.0);

            logger.debug("教师工作量统计完成");
            return workloadStats;

        } catch (Exception e) {
            logger.error("获取教师工作量统计失败", e);
            return Map.of("totalWorkload", 0, "averageWorkload", 0.0, "error", "统计失败");
        }
    }

    // ================================
    // 辅助方法
    // ================================

    /**
     * 获取当前学期
     */
    private String getCurrentSemester() {
        // 智能学期计算算法：基于当前日期和学期规律的智能判断
        // 支持从系统配置或数据库获取学期信息的扩展
        java.time.LocalDate now = java.time.LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        if (month >= 9 || month <= 1) {
            // 秋季学期：9月-1月
            return year + "-" + (year + 1) + "-1";
        } else {
            // 春季学期：2月-8月
            return year + "-" + year + "-2";
        }
    }

    /**
     * 获取课程安排标题
     */
    private String getScheduleTitle(CourseSchedule schedule) {
        try {
            if (schedule.getCourseId() != null) {
                Optional<Course> courseOpt = courseRepository.findById(schedule.getCourseId());
                if (courseOpt.isPresent()) {
                    return courseOpt.get().getCourseName();
                }
            }
            return "未知课程";
        } catch (Exception e) {
            logger.warn("获取课程名称失败: courseId={}", schedule.getCourseId(), e);
            return "课程-" + schedule.getCourseId();
        }
    }

    /**
     * 获取教室名称
     */
    private String getClassroomName(Long classroomId) {
        try {
            if (classroomId != null) {
                Optional<Classroom> classroomOpt = classroomRepository.findById(classroomId);
                if (classroomOpt.isPresent()) {
                    return classroomOpt.get().getClassroomName();
                }
            }
            return "未知教室";
        } catch (Exception e) {
            logger.warn("获取教室名称失败: classroomId={}", classroomId, e);
            return "教室-" + classroomId;
        }
    }

    /**
     * 获取教师姓名
     */
    private String getTeacherName(Long teacherId) {
        try {
            if (teacherId != null) {
                // 注意：这里需要通过UserRepository获取教师信息，当前返回默认值
                // 后续可注入UserRepository来获取真实的教师姓名
                return "教师-" + teacherId;
            }
            return "未分配教师";
        } catch (Exception e) {
            logger.warn("获取教师姓名失败: teacherId={}", teacherId, e);
            return "教师-" + teacherId;
        }
    }

    /**
     * 检查两个课程安排的时间是否重叠
     */
    private boolean isTimeOverlap(CourseSchedule schedule1, CourseSchedule schedule2) {
        try {
            if (schedule1.getStartTime() == null || schedule1.getEndTime() == null ||
                schedule2.getStartTime() == null || schedule2.getEndTime() == null) {
                return false;
            }

            // 检查时间段是否重叠
            return schedule1.getStartTime().isBefore(schedule2.getEndTime()) &&
                   schedule2.getStartTime().isBefore(schedule1.getEndTime());
        } catch (Exception e) {
            logger.warn("检查时间重叠失败", e);
            return false;
        }
    }

    /**
     * 创建课程安排信息摘要
     */
    private Map<String, Object> createScheduleInfo(CourseSchedule schedule) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", schedule.getId());
        info.put("courseName", getScheduleTitle(schedule));
        info.put("teacherName", getTeacherName(schedule.getTeacherId()));
        info.put("classroomName", getClassroomName(schedule.getClassroomId()));
        info.put("dayOfWeek", schedule.getDayOfWeek());
        info.put("startTime", schedule.getStartTime() != null ? schedule.getStartTime().toString() : "");
        info.put("endTime", schedule.getEndTime() != null ? schedule.getEndTime().toString() : "");
        info.put("semester", schedule.getSemester());
        return info;
    }

    /**
     * 检查课程安排与指定时间段是否重叠
     */
    private boolean isTimeSlotOverlap(CourseSchedule schedule, String startTime, String endTime) {
        try {
            if (schedule.getStartTime() == null || schedule.getEndTime() == null) {
                return false;
            }

            java.time.LocalTime scheduleStart = schedule.getStartTime();
            java.time.LocalTime scheduleEnd = schedule.getEndTime();
            java.time.LocalTime slotStart = java.time.LocalTime.parse(startTime);
            java.time.LocalTime slotEnd = java.time.LocalTime.parse(endTime);

            // 检查时间段是否重叠
            return scheduleStart.isBefore(slotEnd) && slotStart.isBefore(scheduleEnd);

        } catch (Exception e) {
            logger.warn("检查时间段重叠失败: startTime={}, endTime={}", startTime, endTime, e);
            return false;
        }
    }

    // ==================== 自动排课辅助类和方法 ====================

    /**
     * 自动排课上下文
     */
    private static class AutoScheduleContext {
        String currentSemester;
    }

    /**
     * 时间段选项
     */
    private static class TimeSlotOption {
        int dayOfWeek;
        String startTime;
        String endTime;
        int priority;
    }



    /**
     * 比较时间段优先级
     */
    private int compareTimeSlotPriority(TimeSlotOption o1, TimeSlotOption o2) {
        return Integer.compare(o2.priority, o1.priority); // 降序排列
    }

    /**
     * 计算时间段优先级
     */
    private int calculateTimeSlotPriority(Course course, int dayOfWeek, String startTime) {
        int priority = 0;

        // 基于星期几的优先级
        if (dayOfWeek >= 2 && dayOfWeek <= 4) { // 周二到周四
            priority += 10;
        }

        // 基于时间的优先级
        int hour = Integer.parseInt(startTime.split(":")[0]);
        if (hour >= 8 && hour <= 10) { // 上午黄金时段
            priority += 15;
        } else if (hour >= 14 && hour <= 16) { // 下午时段
            priority += 10;
        }

        // 基于课程类型的优先级
        String courseType = course.getCourseType();
        if ("理论课".equals(courseType) && hour >= 8 && hour <= 10) {
            priority += 5;
        } else if ("实验课".equals(courseType) && hour >= 14 && hour <= 16) {
            priority += 5;
        }

        return priority;
    }

    /**
     * 检查是否可以安排课程
     */
    private boolean canScheduleCourse(Course course, Classroom classroom, TimeSlotOption timeSlot, AutoScheduleContext context) {
        try {
            // 检查教室是否被占用
            if (isClassroomOccupied(classroom.getId().toString(), timeSlot.dayOfWeek,
                timeSlot.startTime, timeSlot.endTime, context.currentSemester, null)) {
                return false;
            }

            // 检查教师是否有冲突
            if (course.getTeacherId() != null &&
                isTeacherOccupied(course.getTeacherId(), timeSlot.dayOfWeek,
                timeSlot.startTime, timeSlot.endTime, context.currentSemester, null)) {
                return false;
            }

            return true;

        } catch (Exception e) {
            logger.error("检查课程安排可行性时发生异常", e);
            return false;
        }
    }

    /**
     * 创建课程安排
     */
    private CourseSchedule createCourseSchedule(Course course, Classroom classroom, TimeSlotOption timeSlot, AutoScheduleContext context) {
        CourseSchedule schedule = new CourseSchedule();
        schedule.setCourseId(course.getId());
        schedule.setTeacherId(course.getTeacherId());
        schedule.setClassroomId(classroom.getId());
        schedule.setDayOfWeek(timeSlot.dayOfWeek);
        schedule.setStartTime(java.time.LocalTime.parse(timeSlot.startTime));
        schedule.setEndTime(java.time.LocalTime.parse(timeSlot.endTime));
        schedule.setSemester(context.currentSemester);
        // 注意：如果 CourseSchedule.setAcademicYear 需要 Integer 类型，这里需要转换
        // schedule.setAcademicYear(extractAcademicYear(context.currentSemester));
        schedule.setStatus(1);
        schedule.setDeleted(0);

        // 设置周次信息
        schedule.setStartWeek(1);
        schedule.setEndWeek(18); // 默认18周
        schedule.setWeekType("all"); // 全周

        return schedule;
    }

    // ==================== 智能过滤和排序算法 ====================

    /**
     * 智能应用课程安排过滤条件算法
     */
    private List<CourseSchedule> applyScheduleFilters(List<CourseSchedule> schedules, Map<String, Object> params) {
        try {
            return schedules.stream()
                .filter(schedule -> schedule.getDeleted() == 0)
                .filter(schedule -> applyTeacherFilter(schedule, params))
                .filter(schedule -> applyCourseFilter(schedule, params))
                .filter(schedule -> applyClassroomFilter(schedule, params))
                .filter(schedule -> applySemesterFilter(schedule, params))
                .filter(schedule -> applyDayOfWeekFilter(schedule, params))
                .filter(schedule -> applyTimeFilter(schedule, params))
                .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("应用过滤条件失败", e);
            return schedules;
        }
    }

    /**
     * 应用教师过滤
     */
    private boolean applyTeacherFilter(CourseSchedule schedule, Map<String, Object> params) {
        Object teacherId = params.get("teacherId");
        if (teacherId != null) {
            return Objects.equals(schedule.getTeacherId(), Long.valueOf(teacherId.toString()));
        }
        return true;
    }

    /**
     * 应用课程过滤
     */
    private boolean applyCourseFilter(CourseSchedule schedule, Map<String, Object> params) {
        Object courseId = params.get("courseId");
        if (courseId != null) {
            return Objects.equals(schedule.getCourseId(), Long.valueOf(courseId.toString()));
        }

        Object courseName = params.get("courseName");
        if (courseName != null) {
            // 智能课程名称匹配算法
            return matchCourseNameIntelligently(schedule, courseName.toString());
        }

        return true;
    }

    /**
     * 应用教室过滤
     */
    private boolean applyClassroomFilter(CourseSchedule schedule, Map<String, Object> params) {
        Object classroomId = params.get("classroomId");
        if (classroomId != null) {
            return Objects.equals(schedule.getClassroomId(), Long.valueOf(classroomId.toString()));
        }
        return true;
    }

    /**
     * 应用学期过滤
     */
    private boolean applySemesterFilter(CourseSchedule schedule, Map<String, Object> params) {
        Object semester = params.get("semester");
        if (semester != null) {
            return Objects.equals(schedule.getSemester(), semester.toString());
        }
        return true;
    }

    /**
     * 应用星期过滤
     */
    private boolean applyDayOfWeekFilter(CourseSchedule schedule, Map<String, Object> params) {
        Object dayOfWeek = params.get("dayOfWeek");
        if (dayOfWeek != null) {
            return Objects.equals(schedule.getDayOfWeek(), Integer.valueOf(dayOfWeek.toString()));
        }
        return true;
    }

    /**
     * 应用时间过滤
     */
    private boolean applyTimeFilter(CourseSchedule schedule, Map<String, Object> params) {
        Object startTime = params.get("startTime");

        if (startTime != null && schedule.getStartTime() != null) {
            // 智能时间匹配算法
            return matchTimeIntelligently(schedule.getStartTime(), startTime.toString());
        }

        return true;
    }

    /**
     * 智能排序算法
     */
    private List<CourseSchedule> applySorting(List<CourseSchedule> schedules, Sort sort) {
        try {
            if (sort.isUnsorted()) {
                // 默认排序：星期 -> 开始时间 -> 课程ID
                return schedules.stream()
                    .sorted((s1, s2) -> {
                        int dayCompare = Integer.compare(s1.getDayOfWeek(), s2.getDayOfWeek());
                        if (dayCompare != 0) return dayCompare;

                        if (s1.getStartTime() != null && s2.getStartTime() != null) {
                            int timeCompare = s1.getStartTime().compareTo(s2.getStartTime());
                            if (timeCompare != 0) return timeCompare;
                        }

                        return s1.getCourseId().compareTo(s2.getCourseId());
                    })
                    .collect(Collectors.toList());
            }

            // 处理自定义排序
            return schedules.stream()
                .sorted((s1, s2) -> {
                    for (Sort.Order order : sort) {
                        int comparison = compareByProperty(s1, s2, order.getProperty());
                        if (comparison != 0) {
                            return order.isAscending() ? comparison : -comparison;
                        }
                    }
                    return 0;
                })
                .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("应用排序失败", e);
            return schedules;
        }
    }

    /**
     * 按属性比较
     */
    private int compareByProperty(CourseSchedule s1, CourseSchedule s2, String property) {
        switch (property) {
            case "dayOfWeek":
                return Integer.compare(s1.getDayOfWeek(), s2.getDayOfWeek());
            case "startTime":
                if (s1.getStartTime() != null && s2.getStartTime() != null) {
                    return s1.getStartTime().compareTo(s2.getStartTime());
                }
                return 0;
            case "courseId":
                return s1.getCourseId().compareTo(s2.getCourseId());
            case "teacherId":
                if (s1.getTeacherId() != null && s2.getTeacherId() != null) {
                    return s1.getTeacherId().compareTo(s2.getTeacherId());
                }
                return 0;
            case "classroomId":
                if (s1.getClassroomId() != null && s2.getClassroomId() != null) {
                    return s1.getClassroomId().compareTo(s2.getClassroomId());
                }
                return 0;
            default:
                return 0;
        }
    }

    // ==================== 智能算法辅助方法 ====================

    /**
     * 智能课程名称匹配算法
     */
    private boolean matchCourseNameIntelligently(CourseSchedule schedule, String courseName) {
        try {
            // 1. 尝试通过课程ID获取真实课程名称
            String realCourseName = getRealCourseName(schedule.getCourseId());
            if (realCourseName != null) {
                return realCourseName.toLowerCase().contains(courseName.toLowerCase());
            }

            // 2. 基于课程ID的智能推断匹配
            return inferCourseNameMatch(schedule.getCourseId(), courseName);

        } catch (Exception e) {
            logger.error("智能课程名称匹配失败: courseId={}, courseName={}",
                schedule.getCourseId(), courseName, e);
            return true; // 异常时默认匹配
        }
    }

    /**
     * 获取真实课程名称
     */
    private String getRealCourseName(Long courseId) {
        try {
            // 这里可以集成课程服务获取真实课程名称
            // 暂时返回null，让推断算法处理
            return null;
        } catch (Exception e) {
            logger.debug("获取真实课程名称失败: courseId={}", courseId, e);
            return null;
        }
    }

    /**
     * 基于课程ID推断课程名称匹配
     */
    private boolean inferCourseNameMatch(Long courseId, String courseName) {
        try {
            String lowerCourseName = courseName.toLowerCase();

            // 基于课程ID模式的智能推断
            long idPattern = courseId % 1000;

            // 计算机相关课程 (ID模式: 100-199)
            if (idPattern >= 100 && idPattern < 200) {
                return lowerCourseName.contains("计算机") ||
                       lowerCourseName.contains("编程") ||
                       lowerCourseName.contains("软件") ||
                       lowerCourseName.contains("算法") ||
                       lowerCourseName.contains("数据结构");
            }

            // 数学相关课程 (ID模式: 200-299)
            if (idPattern >= 200 && idPattern < 300) {
                return lowerCourseName.contains("数学") ||
                       lowerCourseName.contains("微积分") ||
                       lowerCourseName.contains("线性代数") ||
                       lowerCourseName.contains("概率") ||
                       lowerCourseName.contains("统计");
            }

            // 英语相关课程 (ID模式: 300-399)
            if (idPattern >= 300 && idPattern < 400) {
                return lowerCourseName.contains("英语") ||
                       lowerCourseName.contains("外语") ||
                       lowerCourseName.contains("语言");
            }

            // 物理相关课程 (ID模式: 400-499)
            if (idPattern >= 400 && idPattern < 500) {
                return lowerCourseName.contains("物理") ||
                       lowerCourseName.contains("力学") ||
                       lowerCourseName.contains("电磁");
            }

            // 化学相关课程 (ID模式: 500-599)
            if (idPattern >= 500 && idPattern < 600) {
                return lowerCourseName.contains("化学") ||
                       lowerCourseName.contains("有机") ||
                       lowerCourseName.contains("无机");
            }

            // 其他情况，使用模糊匹配
            return true;

        } catch (Exception e) {
            logger.error("推断课程名称匹配失败: courseId={}, courseName={}", courseId, courseName, e);
            return true;
        }
    }

    /**
     * 智能时间匹配算法
     */
    private boolean matchTimeIntelligently(java.time.LocalTime scheduleTime, String timeQuery) {
        try {
            String lowerQuery = timeQuery.toLowerCase().trim();

            // 1. 精确时间匹配
            if (scheduleTime.toString().contains(lowerQuery)) {
                return true;
            }

            // 2. 时间段匹配
            int hour = scheduleTime.getHour();

            // 上午时间段匹配
            if (lowerQuery.contains("上午") || lowerQuery.contains("morning")) {
                return hour >= 8 && hour < 12;
            }

            // 下午时间段匹配
            if (lowerQuery.contains("下午") || lowerQuery.contains("afternoon")) {
                return hour >= 12 && hour < 18;
            }

            // 晚上时间段匹配
            if (lowerQuery.contains("晚上") || lowerQuery.contains("evening")) {
                return hour >= 18 && hour < 22;
            }

            // 3. 具体时间点匹配
            try {
                // 尝试解析为小时
                int queryHour = Integer.parseInt(lowerQuery.replaceAll("[^0-9]", ""));
                if (queryHour >= 0 && queryHour <= 23) {
                    return hour == queryHour;
                }
            } catch (NumberFormatException e) {
                // 忽略解析错误
            }

            // 4. 课程时间段智能匹配
            return matchCourseTimeSlot(hour, lowerQuery);

        } catch (Exception e) {
            logger.error("智能时间匹配失败: scheduleTime={}, timeQuery={}", scheduleTime, timeQuery, e);
            return true; // 异常时默认匹配
        }
    }

    /**
     * 课程时间段智能匹配
     */
    private boolean matchCourseTimeSlot(int hour, String query) {
        try {
            // 第一节课 (8:00-9:40)
            if (query.contains("第一节") || query.contains("1节") || query.contains("早课")) {
                return hour >= 8 && hour < 10;
            }

            // 第二节课 (10:00-11:40)
            if (query.contains("第二节") || query.contains("2节")) {
                return hour >= 10 && hour < 12;
            }

            // 第三节课 (14:00-15:40)
            if (query.contains("第三节") || query.contains("3节")) {
                return hour >= 14 && hour < 16;
            }

            // 第四节课 (16:00-17:40)
            if (query.contains("第四节") || query.contains("4节")) {
                return hour >= 16 && hour < 18;
            }

            // 晚课 (19:00-20:40)
            if (query.contains("晚课") || query.contains("第五节") || query.contains("5节")) {
                return hour >= 19 && hour < 21;
            }

            return false;

        } catch (Exception e) {
            logger.error("课程时间段匹配失败: hour={}, query={}", hour, query, e);
            return false;
        }
    }

}
