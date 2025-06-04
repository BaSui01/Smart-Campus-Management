package com.campus.service.impl;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.entity.Course;
import com.campus.entity.CourseSchedule;
import com.campus.entity.CourseSelection;
import com.campus.repository.CourseRepository;
import com.campus.repository.CourseScheduleRepository;
import com.campus.repository.CourseScheduleRepository.ScheduleDetail;
import com.campus.repository.CourseSelectionRepository;
import com.campus.service.CourseScheduleService;

/**
 * 课程表服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Service
public class CourseScheduleServiceImpl extends ServiceImpl<CourseScheduleRepository, CourseSchedule> implements CourseScheduleService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    private CourseScheduleRepository courseScheduleRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseSelectionRepository courseSelectionRepository;

    @Override
    public List<CourseSchedule> findByCourseId(Long courseId) {
        return courseScheduleRepository.findByCourseId(courseId);
    }

    @Override
    public List<CourseSchedule> findByTeacherId(Long teacherId) {
        return courseScheduleRepository.findByTeacherId(teacherId);
    }

    @Override
    public List<CourseSchedule> findByClassId(Long classId) {
        return courseScheduleRepository.findByClassId(classId);
    }

    @Override
    public List<CourseSchedule> findBySemester(String semester) {
        return courseScheduleRepository.findBySemester(semester);
    }

    @Override
    public List<CourseSchedule> findByClassroom(String classroom) {
        return courseScheduleRepository.findByClassroom(classroom);
    }

    @Override
    public Optional<ScheduleDetail> findScheduleDetailById(Long scheduleId) {
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
    public IPage<CourseSchedule> findSchedulesByPage(int page, int size, Map<String, Object> params) {
        Page<CourseSchedule> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<CourseSchedule> queryWrapper = new LambdaQueryWrapper<>();

        // 构建查询条件
        if (params != null) {
            // 根据课程ID查询
            if (params.containsKey("courseId")) {
                queryWrapper.eq(CourseSchedule::getCourseId, params.get("courseId"));
            }

            // 根据教师ID查询
            if (params.containsKey("teacherId")) {
                queryWrapper.eq(CourseSchedule::getTeacherId, params.get("teacherId"));
            }

            // 根据班级ID查询
            if (params.containsKey("classId")) {
                queryWrapper.eq(CourseSchedule::getClassId, params.get("classId"));
            }

            // 根据学期查询
            if (params.containsKey("semester")) {
                queryWrapper.eq(CourseSchedule::getSemester, params.get("semester"));
            }

            // 根据教室查询
            if (params.containsKey("classroom")) {
                queryWrapper.eq(CourseSchedule::getClassroom, params.get("classroom"));
            }

            // 根据星期几查询
            if (params.containsKey("dayOfWeek")) {
                queryWrapper.eq(CourseSchedule::getDayOfWeek, params.get("dayOfWeek"));
            }

            // 根据状态查询
            if (params.containsKey("status")) {
                queryWrapper.eq(CourseSchedule::getStatus, params.get("status"));
            }
        }

        // 默认按星期几和开始时间排序
        queryWrapper.orderByAsc(CourseSchedule::getDayOfWeek)
                   .orderByAsc(CourseSchedule::getStartTime);

        return page(pageRequest, queryWrapper);
    }

    @Override
    @Transactional
    public CourseSchedule createSchedule(CourseSchedule schedule) {
        // 检查课程是否存在
        Course course = courseRepository.selectById(schedule.getCourseId());
        if (course == null) {
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
        save(schedule);
        return schedule;
    }

    @Override
    @Transactional
    public boolean updateSchedule(CourseSchedule schedule) {
        // 检查课程表是否存在
        CourseSchedule existingSchedule = getById(schedule.getId());
        if (existingSchedule == null) {
            return false;
        }

        // 检查课程是否存在
        Course course = courseRepository.selectById(schedule.getCourseId());
        if (course == null) {
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
        return updateById(schedule);
    }

    @Override
    @Transactional
    public boolean deleteSchedule(Long id) {
        // 检查课程表是否有关联的选课记录
        LambdaQueryWrapper<CourseSelection> selectionWrapper = new LambdaQueryWrapper<>();
        selectionWrapper.eq(CourseSelection::getScheduleId, id);
        long selectionCount = courseSelectionRepository.selectCount(selectionWrapper);
        if (selectionCount > 0) {
            throw new IllegalStateException("课程表存在关联的选课记录，无法删除");
        }

        // 删除课程表
        return removeById(id);
    }

    @Override
    @Transactional
    public boolean batchDeleteSchedules(List<Long> ids) {
        // 检查课程表是否有关联的选课记录
        for (Long id : ids) {
            LambdaQueryWrapper<CourseSelection> selectionWrapper = new LambdaQueryWrapper<>();
            selectionWrapper.eq(CourseSelection::getScheduleId, id);
            long selectionCount = courseSelectionRepository.selectCount(selectionWrapper);
            if (selectionCount > 0) {
                throw new IllegalStateException("课程表ID " + id + " 存在关联的选课记录，无法删除");
            }
        }

        // 批量删除课程表
        return removeBatchByIds(ids);
    }

    @Override
    public List<CourseSchedule> findByCourseIdAndSemester(Long courseId, String semester) {
        LambdaQueryWrapper<CourseSchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseSchedule::getCourseId, courseId)
                   .eq(CourseSchedule::getSemester, semester);
        return list(queryWrapper);
    }

    @Override
    public List<CourseSchedule> findByTeacherIdAndSemester(Long teacherId, String semester) {
        LambdaQueryWrapper<CourseSchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseSchedule::getTeacherId, teacherId)
                   .eq(CourseSchedule::getSemester, semester);
        return list(queryWrapper);
    }

    @Override
    public List<CourseSchedule> findByClassIdAndSemester(Long classId, String semester) {
        LambdaQueryWrapper<CourseSchedule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseSchedule::getClassId, classId)
                   .eq(CourseSchedule::getSemester, semester);
        return list(queryWrapper);
    }
}
