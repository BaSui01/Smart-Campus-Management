package com.campus.application.service.academic;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.campus.domain.entity.academic.Course;
import com.campus.domain.entity.academic.CourseSchedule;

/**
 * 课程表服务接口 - JPA版本
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
public interface CourseScheduleService {

    /**
     * 根据课程ID查找课程表
     *
     * @param courseId 课程ID
     * @return 课程表列表
     */
    List<CourseSchedule> findByCourseId(Long courseId);

    /**
     * 根据教师ID查找课程表
     *
     * @param teacherId 教师ID
     * @return 课程表列表
     */
    List<CourseSchedule> findByTeacherId(Long teacherId);

    /**
     * 根据班级ID查找课程表
     *
     * @param classId 班级ID
     * @return 课程表列表
     */
    List<CourseSchedule> findByClassId(Long classId);

    /**
     * 根据学期查找课程表
     *
     * @param semester 学期
     * @return 课程表列表
     */
    List<CourseSchedule> findBySemester(String semester);

    /**
     * 根据教室查找课程表
     *
     * @param classroom 教室
     * @return 课程表列表
     */
    List<CourseSchedule> findByClassroom(String classroom);

    /**
     * 获取课程表详情
     *
     * @param scheduleId 课程表ID
     * @return 课程表详情
     */
    Optional<Object[]> findScheduleDetailById(Long scheduleId);

    /**
     * 检查教室在指定时间是否被占用
     *
     * @param classroom 教室
     * @param dayOfWeek 星期几
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param semester 学期
     * @param excludeId 排除的课程表ID（用于更新时）
     * @return 是否被占用
     */
    boolean isClassroomOccupied(String classroom, Integer dayOfWeek, String startTime, String endTime, String semester, Long excludeId);

    /**
     * 检查教师在指定时间是否有其他课程
     *
     * @param teacherId 教师ID
     * @param dayOfWeek 星期几
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param semester 学期
     * @param excludeId 排除的课程表ID（用于更新时）
     * @return 是否有冲突
     */
    boolean isTeacherOccupied(Long teacherId, Integer dayOfWeek, String startTime, String endTime, String semester, Long excludeId);

    /**
     * 分页查询课程表列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param params 查询参数
     * @return 分页结果
     */
    Page<CourseSchedule> findSchedulesByPage(int page, int size, Map<String, Object> params);

    /**
     * 创建课程表
     *
     * @param schedule 课程表信息
     * @return 创建结果
     */
    CourseSchedule createSchedule(CourseSchedule schedule);

    /**
     * 更新课程表信息
     *
     * @param schedule 课程表信息
     * @return 更新结果
     */
    boolean updateSchedule(CourseSchedule schedule);

    /**
     * 删除课程表
     *
     * @param id 课程表ID
     * @return 删除结果
     */
    boolean deleteSchedule(Long id);

    /**
     * 批量删除课程表
     *
     * @param ids 课程表ID列表
     * @return 删除结果
     */
    boolean batchDeleteSchedules(List<Long> ids);

    /**
     * 根据课程ID和学期获取课程表
     *
     * @param courseId 课程ID
     * @param semester 学期
     * @return 课程表列表
     */
    List<CourseSchedule> findByCourseIdAndSemester(Long courseId, String semester);

    /**
     * 根据教师ID和学期获取课程表
     *
     * @param teacherId 教师ID
     * @param semester 学期
     * @return 课程表列表
     */
    List<CourseSchedule> findByTeacherIdAndSemester(Long teacherId, String semester);

    /**
     * 根据班级ID和学期获取课程表
     *
     * @param classId 班级ID
     * @param semester 学期
     * @return 课程表列表
     */
    List<CourseSchedule> findByClassIdAndSemester(Long classId, String semester);

    /**
     * 查找需要排课的课程
     *
     * @return 待排课程列表
     */
    List<Course> findPendingCourses();

    /**
     * 自动排课
     *
     * @param course 课程信息
     * @return 排课是否成功
     */
    boolean autoScheduleCourse(Course course);

    /**
     * 统计课程表数量
     *
     * @return 总数量
     */
    long count();

    /**
     * 根据学生ID查找课程表
     *
     * @param studentId 学生ID
     * @param params 查询参数
     * @return 课程表列表
     */
    List<CourseSchedule> findSchedulesByStudent(Long studentId, Map<String, Object> params);

    /**
     * 根据ID查找课程安排
     *
     * @param id 课程安排ID
     * @return 课程安排
     */
    Optional<CourseSchedule> findById(Long id);

    /**
     * 获取所有课程安排
     *
     * @return 课程安排列表
     */
    List<CourseSchedule> findAll();

    // ================================
    // Web控制器需要的方法
    // ================================

    /**
     * 根据ID获取课程表详情
     *
     * @param id 课程表ID
     * @return 课程表详情
     */
    CourseSchedule getScheduleById(Long id);

    /**
     * 获取课程表日历数据
     *
     * @return 日历数据
     */
    Map<String, Object> getScheduleCalendar();

    /**
     * 获取课程表数据
     *
     * @return 课程表数据
     */
    Map<String, Object> getTimetableData();

    /**
     * 获取课程表冲突信息
     *
     * @return 冲突信息列表
     */
    List<Map<String, Object>> getScheduleConflicts();

    // ================================
    // API控制器需要的方法
    // ================================

    /**
     * 创建课程安排
     *
     * @param courseSchedule 课程安排信息
     * @return 创建的课程安排
     */
    CourseSchedule createCourseSchedule(CourseSchedule courseSchedule);

    /**
     * 根据ID查找课程安排
     *
     * @param id 课程安排ID
     * @return 课程安排
     */
    Optional<CourseSchedule> findCourseScheduleById(Long id);

    /**
     * 更新课程安排
     *
     * @param courseSchedule 课程安排信息
     * @return 更新的课程安排
     */
    CourseSchedule updateCourseSchedule(CourseSchedule courseSchedule);

    /**
     * 删除课程安排
     *
     * @param id 课程安排ID
     * @return 删除是否成功
     */
    boolean deleteCourseSchedule(Long id);

    /**
     * 分页查询所有课程安排
     *
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<CourseSchedule> findAllCourseSchedules(Pageable pageable);

    /**
     * 根据课程ID查找课程安排
     *
     * @param courseId 课程ID
     * @return 课程安排列表
     */
    List<CourseSchedule> findSchedulesByCourse(Long courseId);

    /**
     * 根据教师ID查找课程安排
     *
     * @param teacherId 教师ID
     * @return 课程安排列表
     */
    List<CourseSchedule> findSchedulesByTeacher(Long teacherId);

    /**
     * 根据教室ID查找课程安排
     *
     * @param classroomId 教室ID
     * @return 课程安排列表
     */
    List<CourseSchedule> findSchedulesByClassroom(Long classroomId);

    /**
     * 根据日期范围查找课程安排
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 课程安排列表
     */
    List<CourseSchedule> findSchedulesByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * 根据星期几查找课程安排
     *
     * @param dayOfWeek 星期几
     * @return 课程安排列表
     */
    List<CourseSchedule> findSchedulesByDayOfWeek(String dayOfWeek);

    /**
     * 检查课程安排冲突
     *
     * @param courseId 课程ID
     * @param teacherId 教师ID
     * @param classroomId 教室ID
     * @param dayOfWeek 星期几
     * @param timeSlot 时间段
     * @return 冲突的课程安排列表
     */
    List<CourseSchedule> checkScheduleConflicts(Long courseId, Long teacherId, Long classroomId, String dayOfWeek, String timeSlot);

    /**
     * 批量创建课程安排
     *
     * @param courseSchedules 课程安排列表
     * @return 创建的课程安排列表
     */
    List<CourseSchedule> batchCreateSchedules(List<CourseSchedule> courseSchedules);

    /**
     * 获取教师周课表
     *
     * @param teacherId 教师ID
     * @param weekStart 周开始日期
     * @return 周课表数据
     */
    Object getTeacherWeeklySchedule(Long teacherId, LocalDate weekStart);

    /**
     * 统计总课程安排数
     *
     * @return 总数
     */
    long countTotalSchedules();

    /**
     * 统计活跃课程安排数
     *
     * @return 活跃数
     */
    long countActiveSchedules();

    /**
     * 按天统计课程安排数
     *
     * @return 按天统计结果
     */
    Map<String, Long> countSchedulesByDay();

    /**
     * 按时间段统计课程安排数
     *
     * @return 按时间段统计结果
     */
    Map<String, Long> countSchedulesByTimeSlot();

    // ================================
    // Web控制器需要的额外方法
    // ================================

    /**
     * 获取教师课程表
     *
     * @param teacherId 教师ID
     * @return 教师课程表
     */
    List<CourseSchedule> getTeacherSchedule(Long teacherId);

    /**
     * 获取教室课程表
     *
     * @param classroomId 教室ID
     * @return 教室课程表
     */
    List<CourseSchedule> getClassroomSchedule(Long classroomId);

    /**
     * 获取课程表模板
     *
     * @return 课程表模板
     */
    List<Map<String, Object>> getScheduleTemplates();

    /**
     * 获取课程表统计信息
     *
     * @return 统计信息
     */
    Map<String, Object> getScheduleStatistics();

    /**
     * 获取教室利用率统计
     *
     * @return 教室利用率统计
     */
    Map<String, Object> getClassroomUtilizationStats();

    /**
     * 获取教师工作量统计
     *
     * @return 教师工作量统计
     */
    Map<String, Object> getTeacherWorkloadStats();
}
