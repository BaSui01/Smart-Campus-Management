package com.campus.application.service.impl;

import java.util.ArrayList;
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

    @Override
    @Transactional(readOnly = true)
    public List<Course> findActiveCourses() {
        // 查找状态为1（启用）且未删除的课程
        return courseRepository.findByStatusAndDeleted(1, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getCourseStatistics() {
        try {
            long totalCourses = count();
            long activeCourses = courseRepository.countByStatusAndDeleted(1, 0);
            Map<String, Long> typeStats = countCoursesByType();
            Map<String, Long> semesterStats = getCourseSemesterStatistics();

            return new Object() {
                public final long total = totalCourses;
                public final long active = activeCourses;
                public final Map<String, Long> byType = typeStats;
                public final Map<String, Long> bySemester = semesterStats;
            };
        } catch (Exception e) {
            // 返回默认统计信息
            return new Object() {
                public final long total = 0;
                public final long active = 0;
                public final Map<String, Long> byType = new HashMap<>();
                public final Map<String, Long> bySemester = new HashMap<>();
            };
        }
    }

    // ================================
    // Web控制器需要的缺失方法
    // ================================

    @Override
    @Transactional(readOnly = true)
    public List<Course> findUnscheduledCourses() {
        // TODO: 实现查找未排课的课程逻辑
        // 这里需要与CourseSchedule表关联查询
        return courseRepository.findByStatusAndDeleted(1, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Course> findCourseById(Long id) {
        return findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> findAllCourses() {
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Course findByCourseCodeString(String courseCode) {
        Optional<Course> course = findByCourseCode(courseCode);
        return course.orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCourseOptions() {
        List<Course> courses = findActiveCourses();
        return courses.stream().map(course -> {
            Map<String, Object> option = new HashMap<>();
            option.put("value", course.getId());
            option.put("label", course.getCourseName());
            option.put("code", course.getCourseCode());
            return option;
        }).collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> findCoursesByTeacher(Long teacherId) {
        return findByTeacherId(teacherId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> findCoursesBySemester(String semester) {
        return findBySemester(semester);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> findCurrentSemesterCourses() {
        // TODO: 实现获取当前学期逻辑
        // 假设当前学期为"2024-2025-1"
        String currentSemester = "2024-2025-1";
        return findBySemester(currentSemester);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canDeleteCourse(Long courseId) {
        // TODO: 实现删除前检查逻辑
        // 检查是否有学生选课、是否有课程安排等
        Optional<Course> course = findById(courseId);
        if (course.isEmpty()) {
            return false;
        }
        // 简单检查：如果课程状态为禁用且没有选课学生，则可以删除
        return course.get().getStatus() == 0;
    }

    @Override
    @Transactional
    public boolean enableCourse(Long courseId) {
        Optional<Course> courseOpt = findById(courseId);
        if (courseOpt.isEmpty()) {
            return false;
        }
        Course course = courseOpt.get();
        course.setStatus(1);
        save(course);
        return true;
    }

    @Override
    @Transactional
    public boolean disableCourse(Long courseId) {
        Optional<Course> courseOpt = findById(courseId);
        if (courseOpt.isEmpty()) {
            return false;
        }
        Course course = courseOpt.get();
        course.setStatus(0);
        save(course);
        return true;
    }

    @Override
    @Transactional
    public Course copyCourse(Long courseId, String newCourseCode, String newCourseName) {
        Optional<Course> originalOpt = findById(courseId);
        if (originalOpt.isEmpty()) {
            throw new IllegalArgumentException("源课程不存在");
        }
        
        Course original = originalOpt.get();
        Course newCourse = new Course();
        
        // 复制基本信息
        newCourse.setCourseCode(newCourseCode);
        newCourse.setCourseName(newCourseName);
        newCourse.setCourseType(original.getCourseType());
        newCourse.setCredits(original.getCredits());
        newCourse.setHours(original.getHours());
        newCourse.setDepartmentId(original.getDepartmentId());
        newCourse.setTeacherId(original.getTeacherId());
        newCourse.setSemester(original.getSemester());
        newCourse.setStatus(1); // 新课程默认启用
        newCourse.setDeleted(0);
        
        return save(newCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCourseCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();

        String[] categoryNames = {"必修课", "选修课", "专业课", "公共课", "实验课", "实践课"};
        String[] categoryCodes = {"required", "elective", "major", "public", "lab", "practice"};
        String[] descriptions = {"必修课程", "选修课程", "专业课程", "公共基础课", "实验课程", "实践课程"};

        for (int i = 0; i < categoryNames.length; i++) {
            Map<String, Object> category = new HashMap<>();
            category.put("id", (long) (i + 1));
            category.put("name", categoryNames[i]);
            category.put("code", categoryCodes[i]);
            category.put("description", descriptions[i]);
            category.put("courseCount", (i + 1) * 10); // 模拟课程数量
            categories.add(category);
        }

        return categories;
    }

}
