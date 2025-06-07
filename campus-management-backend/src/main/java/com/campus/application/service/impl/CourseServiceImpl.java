package com.campus.application.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.application.service.CourseService;
import com.campus.domain.entity.Course;
import com.campus.domain.entity.CourseSelection;
import com.campus.domain.repository.CourseRepository;
import com.campus.domain.repository.CourseSelectionRepository;

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
    @Cacheable(value = "course:count", unless = "#result == null")
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

    // ==================== 新增的接口方法实现 ====================

    @Override
    @Transactional
    public Course createCourse(Course course) {
        try {
            // 检查课程代码是否已存在
            if (existsByCourseCode(course.getCourseCode())) {
                throw new IllegalArgumentException("课程代码已存在");
            }

            // 设置默认值
            if (course.getStatus() == null) {
                course.setStatus(1); // 默认启用
            }
            if (course.getEnrolledStudents() == null) {
                course.setEnrolledStudents(0); // 默认选课人数为0
            }

            return courseRepository.save(course);
        } catch (Exception e) {
            throw new RuntimeException("创建课程失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean updateCourse(Course course) {
        try {
            // 检查课程是否存在
            Optional<Course> existingCourse = courseRepository.findById(course.getId());
            if (existingCourse.isEmpty()) {
                return false;
            }

            // 检查课程代码是否被其他课程使用
            Optional<Course> courseWithSameCode = courseRepository.findByCourseCodeAndDeleted(course.getCourseCode(), 0);
            if (courseWithSameCode.isPresent() && !courseWithSameCode.get().getId().equals(course.getId())) {
                throw new IllegalArgumentException("课程代码已被其他课程使用");
            }

            courseRepository.save(course);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("更新课程失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean deleteCourse(Long courseId) {
        try {
            // 检查课程是否存在
            Optional<Course> course = courseRepository.findById(courseId);
            if (course.isEmpty()) {
                return false;
            }

            // 软删除：设置deleted标志
            Course courseEntity = course.get();
            courseEntity.setDeleted(1);
            courseRepository.save(courseEntity);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("删除课程失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean batchDeleteCourses(List<Long> courseIds) {
        try {
            for (Long courseId : courseIds) {
                deleteCourse(courseId);
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("批量删除课程失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> importCourses(List<Course> courses) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        StringBuilder errorMessages = new StringBuilder();

        for (Course course : courses) {
            try {
                createCourse(course);
                successCount++;
            } catch (Exception e) {
                failCount++;
                errorMessages.append("课程[").append(course.getCourseCode()).append("]导入失败：")
                           .append(e.getMessage()).append("; ");
            }
        }

        result.put("total", courses.size());
        result.put("success", successCount);
        result.put("fail", failCount);
        result.put("errors", errorMessages.toString());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> exportCourses(Map<String, Object> params) {
        // 根据参数过滤课程
        if (params != null && !params.isEmpty()) {
            // 这里可以根据具体的查询参数来过滤
            // 暂时返回所有课程
            return courseRepository.findAll();
        }
        return courseRepository.findAll();
    }
}
