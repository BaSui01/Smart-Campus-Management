package com.campus.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.entity.Course;
import com.campus.entity.CourseSelection;
import com.campus.repository.CourseRepository;
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
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseSelectionRepository courseSelectionRepository;
    // ==================== 基础CRUD方法 ====================

    @Override
    public Course save(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Override
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Override
    public Page<Course> findAll(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    @Override
    public void deleteById(Long id) {
        courseRepository.deleteById(id);
    }

    @Override
    public void deleteAllById(List<Long> ids) {
        courseRepository.deleteAllById(ids);
    }

    @Override
    public long count() {
        return courseRepository.count();
    }

    // ==================== 业务查询方法 ====================

    @Override
    public Optional<Course> findByCourseCode(String courseCode) {
        return courseRepository.findByCourseCodeAndDeleted(courseCode, 0);
    }

    @Override
    public List<Course> findByCourseName(String courseName) {
        return courseRepository.findByCourseNameContainingAndDeleted(courseName, 0);
    }

    @Override
    public List<Course> findByDepartmentId(Long departmentId) {
        return courseRepository.findByDepartmentIdAndDeleted(departmentId, 0);
    }

    @Override
    public List<Course> findByTeacherId(Long teacherId) {
        return courseRepository.findByTeacherIdAndDeleted(teacherId, 0);
    }

    @Override
    public List<Course> findBySemester(String semester) {
        return courseRepository.findBySemesterAndDeleted(semester, 0);
    }

    @Override
    public List<Course> findByCourseType(String courseType) {
        return courseRepository.findByCourseTypeAndDeleted(courseType, 0);
    }

    @Override
    public List<Course> findByStatus(Integer status) {
        // 如果需要根据状态查询，需要在Repository中添加相应方法
        // 这里暂时返回所有课程
        return courseRepository.findAll();
    }

    @Override
    public boolean existsByCourseCode(String courseCode) {
        return courseRepository.existsByCourseCodeAndDeleted(courseCode, 0);
    }

    @Override
    public Page<Course> findCoursesByPage(Pageable pageable, Map<String, Object> params) {
        // 简化实现：使用基础的分页查询
        // 实际项目中可以使用 Specification 来构建复杂查询条件
        return courseRepository.findAll(pageable);
    }

    @Override
    public List<Course> searchCourses(String keyword) {
        return courseRepository.searchCourses(keyword);
    }

    @Override
    public Map<String, Long> countCoursesByType() {
        List<Object[]> results = courseRepository.countCoursesByType();
        Map<String, Long> countMap = new HashMap<>();
        for (Object[] result : results) {
            String courseType = (String) result[0];
            Long count = (Long) result[1];
            countMap.put(courseType, count);
        }
        return countMap;
    }

    // ==================== 业务操作方法 ====================

    @Override
    @Transactional
    public boolean updateEnrolledStudents(Long courseId) {
        // 检查课程是否存在
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return false;
        }
        Course course = courseOpt.get();

        // 统计课程选课人数
        List<CourseSelection> selections = courseSelectionRepository.findByCourseIdAndDeleted(courseId, 0);
        long count = selections.size();

        // 更新课程选课人数
        course.setEnrolledStudents((int) count);
        courseRepository.save(course);
        return true;
    }

    // ==================== 附加方法 ====================

    /**
     * 根据学分范围查找课程
     */
    public List<Course> findCoursesByCreditsRange(Integer minCredits, Integer maxCredits) {
        return courseRepository.findByCreditsBetween(minCredits, maxCredits);
    }

    /**
     * 查找有选课学生的课程
     */
    public List<Course> findCoursesWithSelections() {
        return courseRepository.findCoursesWithSelections();
    }

    /**
     * 查找没有选课学生的课程
     */
    public List<Course> findCoursesWithoutSelections() {
        return courseRepository.findCoursesWithoutSelections();
    }

    /**
     * 统计课程数量按学期
     */
    public Map<String, Long> getCourseSemesterStatistics() {
        List<Object[]> results = courseRepository.countCoursesBySemester();
        Map<String, Long> countMap = new HashMap<>();
        for (Object[] result : results) {
            String semester = (String) result[0];
            Long count = (Long) result[1];
            countMap.put(semester, count);
        }
        return countMap;
    }

    /**
     * 获取课程详情（包含教师信息）
     */
    public Optional<Object[]> findCourseDetailById(Long courseId) {
        return courseRepository.findCourseDetailById(courseId);
    }
}
