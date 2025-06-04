package com.campus.service.impl;

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
import com.campus.repository.CourseRepository.CourseDetail;
import com.campus.repository.CourseRepository.CourseTypeCount;
import com.campus.repository.CourseScheduleRepository;
import com.campus.repository.CourseSelectionRepository;
import com.campus.service.CourseService;

/**
 * 课程服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseRepository, Course> implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseScheduleRepository courseScheduleRepository;

    @Autowired
    private CourseSelectionRepository courseSelectionRepository;

    @Override
    public Optional<Course> findByCourseCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode);
    }

    @Override
    public List<Course> findByCourseName(String courseName) {
        return courseRepository.findByCourseName(courseName);
    }

    @Override
    public List<Course> findByDepartmentId(Long departmentId) {
        return courseRepository.findByDepartmentId(departmentId);
    }

    @Override
    public List<Course> findByTeacherId(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }

    @Override
    public List<Course> findBySemester(String semester) {
        return courseRepository.findBySemester(semester);
    }

    @Override
    public List<Course> findByCourseType(String courseType) {
        return courseRepository.findByCourseType(courseType);
    }

    @Override
    public boolean existsByCourseCode(String courseCode) {
        return courseRepository.existsByCourseCode(courseCode);
    }

    @Override
    public Optional<CourseDetail> findCourseDetailById(Long courseId) {
        return courseRepository.findCourseDetailById(courseId);
    }

    @Override
    public List<CourseTypeCount> countCoursesByType() {
        return courseRepository.countCoursesByType();
    }

    @Override
    public IPage<Course> findCoursesByPage(int page, int size, Map<String, Object> params) {
        Page<Course> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();

        // 构建查询条件
        if (params != null) {
            // 根据课程名称查询
            if (params.containsKey("courseName")) {
                queryWrapper.like(Course::getCourseName, params.get("courseName"));
            }

            // 根据课程代码查询
            if (params.containsKey("courseCode")) {
                queryWrapper.like(Course::getCourseCode, params.get("courseCode"));
            }

            // 根据学分查询
            if (params.containsKey("credits")) {
                queryWrapper.eq(Course::getCredits, params.get("credits"));
            }

            // 根据部门ID查询
            if (params.containsKey("departmentId")) {
                queryWrapper.eq(Course::getDepartmentId, params.get("departmentId"));
            }

            // 根据教师ID查询
            if (params.containsKey("teacherId")) {
                queryWrapper.eq(Course::getTeacherId, params.get("teacherId"));
            }

            // 根据课程类型查询
            if (params.containsKey("courseType")) {
                queryWrapper.eq(Course::getCourseType, params.get("courseType"));
            }

            // 根据学期查询
            if (params.containsKey("semester")) {
                queryWrapper.eq(Course::getSemester, params.get("semester"));
            }

            // 根据状态查询
            if (params.containsKey("status")) {
                queryWrapper.eq(Course::getStatus, params.get("status"));
            }
        }

        // 默认按课程代码排序
        queryWrapper.orderByAsc(Course::getCourseCode);

        return page(pageRequest, queryWrapper);
    }

    @Override
    @Transactional
    public Course createCourse(Course course) {
        // 检查课程代码是否已存在
        if (existsByCourseCode(course.getCourseCode())) {
            throw new IllegalArgumentException("课程代码已存在：" + course.getCourseCode());
        }

        // 保存课程信息
        save(course);
        return course;
    }

    @Override
    @Transactional
    public boolean updateCourse(Course course) {
        // 检查课程是否存在
        Course existingCourse = getById(course.getId());
        if (existingCourse == null) {
            return false;
        }

        // 如果课程代码变更，检查新代码是否已存在
        if (!existingCourse.getCourseCode().equals(course.getCourseCode())
                && existsByCourseCode(course.getCourseCode())) {
            throw new IllegalArgumentException("课程代码已存在：" + course.getCourseCode());
        }

        // 更新课程信息
        return updateById(course);
    }

    @Override
    @Transactional
    public boolean deleteCourse(Long id) {
        // 检查课程是否有关联的课程表
        LambdaQueryWrapper<CourseSchedule> scheduleWrapper = new LambdaQueryWrapper<>();
        scheduleWrapper.eq(CourseSchedule::getCourseId, id);
        long scheduleCount = courseScheduleRepository.selectCount(scheduleWrapper);
        if (scheduleCount > 0) {
            throw new IllegalStateException("课程存在关联的课程表，无法删除");
        }

        // 检查课程是否有关联的选课记录
        LambdaQueryWrapper<CourseSelection> selectionWrapper = new LambdaQueryWrapper<>();
        selectionWrapper.eq(CourseSelection::getCourseId, id);
        long selectionCount = courseSelectionRepository.selectCount(selectionWrapper);
        if (selectionCount > 0) {
            throw new IllegalStateException("课程存在关联的选课记录，无法删除");
        }

        // 删除课程
        return removeById(id);
    }

    @Override
    @Transactional
    public boolean batchDeleteCourses(List<Long> ids) {
        // 检查课程是否有关联的课程表或选课记录
        for (Long id : ids) {
            // 检查课程表
            LambdaQueryWrapper<CourseSchedule> scheduleWrapper = new LambdaQueryWrapper<>();
            scheduleWrapper.eq(CourseSchedule::getCourseId, id);
            long scheduleCount = courseScheduleRepository.selectCount(scheduleWrapper);
            if (scheduleCount > 0) {
                throw new IllegalStateException("课程ID " + id + " 存在关联的课程表，无法删除");
            }

            // 检查选课记录
            LambdaQueryWrapper<CourseSelection> selectionWrapper = new LambdaQueryWrapper<>();
            selectionWrapper.eq(CourseSelection::getCourseId, id);
            long selectionCount = courseSelectionRepository.selectCount(selectionWrapper);
            if (selectionCount > 0) {
                throw new IllegalStateException("课程ID " + id + " 存在关联的选课记录，无法删除");
            }
        }

        // 批量删除课程
        return removeBatchByIds(ids);
    }

    @Override
    @Transactional
    public boolean updateEnrolledStudents(Long courseId) {
        // 检查课程是否存在
        Course course = getById(courseId);
        if (course == null) {
            return false;
        }

        // 统计课程选课人数
        LambdaQueryWrapper<CourseSelection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseSelection::getCourseId, courseId);
        long count = courseSelectionRepository.selectCount(wrapper);

        // 更新课程选课人数
        course.setEnrolledStudents((int) count);
        return updateById(course);
    }

    @Override
    public List<Course> searchCourses(String keyword) {
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Course::getCourseName, keyword)
                   .or()
                   .like(Course::getCourseCode, keyword)
                   .or()
                   .like(Course::getDescription, keyword);
        return list(queryWrapper);
    }
}
