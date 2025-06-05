package com.campus.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import com.campus.entity.CourseSchedule;
import com.campus.entity.Course;

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
}
