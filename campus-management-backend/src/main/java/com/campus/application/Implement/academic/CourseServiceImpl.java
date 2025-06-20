package com.campus.application.Implement.academic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.application.service.academic.CourseService;
import com.campus.domain.entity.academic.Course;
import com.campus.domain.entity.academic.CourseSelection;
import com.campus.domain.repository.academic.CourseRepository;
import com.campus.domain.repository.academic.CourseSelectionRepository;

/**
 * 课程服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Service
public class CourseServiceImpl implements CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

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
        try {
            // 条件查询分页
            List<Course> allCourses = courseRepository.findAll();

            // 应用过滤条件
            List<Course> filteredCourses = applyIntelligentCourseFilters(allCourses, params);

            // 应用排序
            filteredCourses = applyIntelligentCourseSorting(filteredCourses, pageable.getSort());

            // 分页处理
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), filteredCourses.size());
            List<Course> pageContent = filteredCourses.subList(start, end);

            return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, filteredCourses.size());

        } catch (Exception e) {
            log.error("分页查询课程失败", e);
            return courseRepository.findAll(pageable);
        }
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

            // 数据验证增强
            validateCourseData(course);

            // 设置默认值
            if (course.getStatus() == null) {
                course.setStatus(1); // 默认启用
            }
            if (course.getEnrolledStudents() == null) {
                course.setEnrolledStudents(0); // 默认选课人数为0
            }
            if (course.getDeleted() == null) {
                course.setDeleted(0); // 默认未删除
            }

            log.info("创建课程: {}", course.getCourseName());
            return courseRepository.save(course);
        } catch (Exception e) {
            log.error("创建课程失败: {}", course.getCourseCode(), e);
            throw new RuntimeException("创建课程失败：" + e.getMessage(), e);
        }
    }

    /**
     * 课程数据验证
     */
    private void validateCourseData(Course course) {
        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
            throw new IllegalArgumentException("课程名称不能为空");
        }
        if (course.getCourseCode() == null || course.getCourseCode().trim().isEmpty()) {
            throw new IllegalArgumentException("课程代码不能为空");
        }
        if (course.getCredits() != null && course.getCredits().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("学分必须大于0");
        }
        if (course.getHours() != null && course.getHours() <= 0) {
            throw new IllegalArgumentException("学时必须大于0");
        }
        if (course.getMaxStudents() != null && course.getMaxStudents() <= 0) {
            throw new IllegalArgumentException("最大学生数必须大于0");
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

            return new CourseStatistics(totalCourses, activeCourses, typeStats, semesterStats);
        } catch (Exception e) {
            // 返回默认统计信息
            return new CourseStatistics(0L, 0L, new HashMap<>(), new HashMap<>());
        }
    }

    // ================================
    // Web控制器需要的缺失方法
    // ================================

    @Override
    @Transactional(readOnly = true)
    public List<Course> findUnscheduledCourses() {
        try {
            // 查找所有启用且未删除的课程
            List<Course> allCourses = courseRepository.findByStatusAndDeleted(1, 0);

            // 过滤未排课的课程
            return allCourses.stream()
                .filter(course -> course.getStatus() == 1)
                .filter(this::isIntelligentUnscheduledCourse)
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            log.error("查找未排课课程失败", e);
            return new ArrayList<>();
        }
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
        try {
            // 获取当前学期：基于当前日期计算学期
            java.time.LocalDate now = java.time.LocalDate.now();
            int year = now.getYear();
            int month = now.getMonthValue();

            // 简单的学期计算逻辑：9月-1月为第一学期，2月-8月为第二学期
            String currentSemester;
            if (month >= 9 || month <= 1) {
                currentSemester = year + "-" + (year + 1) + "-1";
            } else {
                currentSemester = (year - 1) + "-" + year + "-2";
            }

            log.debug("当前学期: {}", currentSemester);
            return findBySemester(currentSemester);
        } catch (Exception e) {
            log.error("获取当前学期课程失败", e);
            // 降级处理：返回默认学期的课程
            return findBySemester("2024-2025-1");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canDeleteCourse(Long courseId) {
        try {
            // 检查课程是否存在
            Optional<Course> course = findById(courseId);
            if (course.isEmpty()) {
                return false;
            }

            // 检查是否有学生选课
            List<CourseSelection> selections = courseSelectionRepository.findByCourseIdAndDeleted(courseId, 0);
            if (!selections.isEmpty()) {
                log.warn("课程ID {} 有 {} 个学生选课，不能删除", courseId, selections.size());
                return false;
            }

            // 检查课程状态：只有禁用的课程才能删除
            Course courseEntity = course.get();
            if (courseEntity.getStatus() == 1) {
                log.warn("课程ID {} 状态为启用，不能删除", courseId);
                return false;
            }

            log.debug("课程ID {} 可以删除", courseId);
            return true;
        } catch (Exception e) {
            log.error("检查课程删除权限失败: courseId={}", courseId, e);
            return false;
        }
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
        try {
            // 从数据库查询实际的课程类型分布
            Map<String, Long> typeStats = countCoursesByType();

            // 预定义的课程分类
            List<String> predefinedCategories = java.util.Arrays.asList(
                "必修课", "选修课", "专业课", "通识课", "实践课", "公共课", "基础课"
            );

            // 合并数据库中的类型和预定义类型
            java.util.Set<String> allCategories = new java.util.HashSet<>(predefinedCategories);
            allCategories.addAll(typeStats.keySet());

            return allCategories.stream()
                .sorted()
                .map(category -> {
                    Map<String, Object> categoryMap = new HashMap<>();
                    categoryMap.put("value", category);
                    categoryMap.put("label", category);
                    categoryMap.put("count", typeStats.getOrDefault(category, 0L));
                    return categoryMap;
                }).collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            log.error("获取课程分类失败", e);
            // 降级处理：返回基础分类
            List<String> basicCategories = java.util.Arrays.asList("必修课", "选修课", "专业课", "通识课", "实践课");
            return basicCategories.stream().map(category -> {
                Map<String, Object> categoryMap = new HashMap<>();
                categoryMap.put("value", category);
                categoryMap.put("label", category);
                categoryMap.put("count", 0L);
                return categoryMap;
            }).collect(java.util.stream.Collectors.toList());
        }
    }

    // ================================
    // CourseResourceController 需要的方法实现
    // ================================

    @Override
    @Transactional(readOnly = true)
    public Course getCourseById(Long id) {
        try {
            Optional<Course> courseOpt = findById(id);
            return courseOpt.orElse(null);
        } catch (Exception e) {
            // 可以记录日志
            return null;
        }
    }

    // ================================
    // CourseSelectionController 需要的方法实现
    // ================================

    @Override
    @Transactional(readOnly = true)
    public List<Course> findSelectableCourses() {
        try {
            // 查找状态为启用且未删除的课程
            return courseRepository.findByStatusAndDeleted(1, 0);
        } catch (Exception e) {
            // 记录日志并返回空列表
            return new ArrayList<>();
        }
    }

    /**
     * 课程统计信息类
     */
    public static class CourseStatistics {
        public final long total;
        public final long active;
        public final Map<String, Long> byType;
        public final Map<String, Long> bySemester;

        public CourseStatistics(long total, long active, Map<String, Long> byType, Map<String, Long> bySemester) {
            this.total = total;
            this.active = active;
            this.byType = byType;
            this.bySemester = bySemester;
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 过滤课程
     */
    private List<Course> applyIntelligentCourseFilters(List<Course> courses, Map<String, Object> params) {
        try {
            return courses.stream()
                .filter(course -> course.getDeleted() == 0)
                .filter(course -> applyCourseNameFilter(course, params))
                .filter(course -> applyCourseCodeFilter(course, params))
                .filter(course -> applyDepartmentFilter(course, params))
                .filter(course -> applyTeacherFilter(course, params))
                .filter(course -> applySemesterFilter(course, params))
                .filter(course -> applyCourseTypeFilter(course, params))
                .filter(course -> applyStatusFilter(course, params))
                .filter(course -> applyCreditsFilter(course, params))
                .collect(java.util.stream.Collectors.toList());

        } catch (Exception e) {
            log.error("应用过滤条件失败", e);
            return courses;
        }
    }

    /**
     * 应用课程名称过滤
     */
    private boolean applyCourseNameFilter(Course course, Map<String, Object> params) {
        Object courseName = params.get("courseName");
        if (courseName != null && !courseName.toString().trim().isEmpty()) {
            return course.getCourseName() != null &&
                   course.getCourseName().toLowerCase().contains(courseName.toString().toLowerCase());
        }
        return true;
    }

    /**
     * 应用课程代码过滤
     */
    private boolean applyCourseCodeFilter(Course course, Map<String, Object> params) {
        Object courseCode = params.get("courseCode");
        if (courseCode != null && !courseCode.toString().trim().isEmpty()) {
            return course.getCourseCode() != null &&
                   course.getCourseCode().toLowerCase().contains(courseCode.toString().toLowerCase());
        }
        return true;
    }

    /**
     * 应用院系过滤
     */
    private boolean applyDepartmentFilter(Course course, Map<String, Object> params) {
        Object departmentId = params.get("departmentId");
        if (departmentId != null) {
            try {
                Long deptId = Long.valueOf(departmentId.toString());
                return java.util.Objects.equals(course.getDepartmentId(), deptId);
            } catch (NumberFormatException e) {
                return true;
            }
        }
        return true;
    }

    /**
     * 应用教师过滤
     */
    private boolean applyTeacherFilter(Course course, Map<String, Object> params) {
        Object teacherId = params.get("teacherId");
        if (teacherId != null) {
            try {
                Long tId = Long.valueOf(teacherId.toString());
                return java.util.Objects.equals(course.getTeacherId(), tId);
            } catch (NumberFormatException e) {
                return true;
            }
        }
        return true;
    }

    /**
     * 应用学期过滤
     */
    private boolean applySemesterFilter(Course course, Map<String, Object> params) {
        Object semester = params.get("semester");
        if (semester != null && !semester.toString().trim().isEmpty()) {
            return java.util.Objects.equals(course.getSemester(), semester.toString());
        }
        return true;
    }

    /**
     * 应用课程类型过滤
     */
    private boolean applyCourseTypeFilter(Course course, Map<String, Object> params) {
        Object courseType = params.get("courseType");
        if (courseType != null && !courseType.toString().trim().isEmpty()) {
            return java.util.Objects.equals(course.getCourseType(), courseType.toString());
        }
        return true;
    }

    /**
     * 应用状态过滤
     */
    private boolean applyStatusFilter(Course course, Map<String, Object> params) {
        Object status = params.get("status");
        if (status != null) {
            try {
                Integer statusValue = Integer.valueOf(status.toString());
                return java.util.Objects.equals(course.getStatus(), statusValue);
            } catch (NumberFormatException e) {
                return true;
            }
        }
        return true;
    }

    /**
     * 应用学分过滤
     */
    private boolean applyCreditsFilter(Course course, Map<String, Object> params) {
        Object minCredits = params.get("minCredits");
        Object maxCredits = params.get("maxCredits");

        if (course.getCredits() == null) {
            return true;
        }

        if (minCredits != null) {
            try {
                java.math.BigDecimal min = new java.math.BigDecimal(minCredits.toString());
                if (course.getCredits().compareTo(min) < 0) {
                    return false;
                }
            } catch (NumberFormatException e) {
                // 忽略无效的数字格式
            }
        }

        if (maxCredits != null) {
            try {
                java.math.BigDecimal max = new java.math.BigDecimal(maxCredits.toString());
                if (course.getCredits().compareTo(max) > 0) {
                    return false;
                }
            } catch (NumberFormatException e) {
                // 忽略无效的数字格式
            }
        }

        return true;
    }

    /**
     * 排序算法
     */
    private List<Course> applyIntelligentCourseSorting(List<Course> courses, org.springframework.data.domain.Sort sort) {
        try {
            if (sort.isUnsorted()) {
                // 默认排序：课程代码 -> 课程名称 -> 创建时间
                return courses.stream()
                    .sorted((c1, c2) -> {
                        if (c1.getCourseCode() != null && c2.getCourseCode() != null) {
                            int codeCompare = c1.getCourseCode().compareTo(c2.getCourseCode());
                            if (codeCompare != 0) return codeCompare;
                        }

                        if (c1.getCourseName() != null && c2.getCourseName() != null) {
                            int nameCompare = c1.getCourseName().compareTo(c2.getCourseName());
                            if (nameCompare != 0) return nameCompare;
                        }

                        if (c1.getCreatedAt() != null && c2.getCreatedAt() != null) {
                            return c2.getCreatedAt().compareTo(c1.getCreatedAt());
                        }

                        return 0;
                    })
                    .collect(java.util.stream.Collectors.toList());
            }

            // 处理自定义排序
            return courses.stream()
                .sorted((c1, c2) -> {
                    for (org.springframework.data.domain.Sort.Order order : sort) {
                        int comparison = compareCoursesByProperty(c1, c2, order.getProperty());
                        if (comparison != 0) {
                            return order.isAscending() ? comparison : -comparison;
                        }
                    }
                    return 0;
                })
                .collect(java.util.stream.Collectors.toList());

        } catch (Exception e) {
            log.error("应用排序失败", e);
            return courses;
        }
    }

    /**
     * 按属性比较课程
     */
    private int compareCoursesByProperty(Course c1, Course c2, String property) {
        switch (property) {
            case "courseName":
                return compareStrings(c1.getCourseName(), c2.getCourseName());
            case "courseCode":
                return compareStrings(c1.getCourseCode(), c2.getCourseCode());
            case "credits":
                return compareBigDecimals(c1.getCredits(), c2.getCredits());
            case "hours":
                return compareIntegers(c1.getHours(), c2.getHours());
            case "semester":
                return compareStrings(c1.getSemester(), c2.getSemester());
            case "courseType":
                return compareStrings(c1.getCourseType(), c2.getCourseType());
            case "status":
                return compareIntegers(c1.getStatus(), c2.getStatus());
            case "enrolledStudents":
                return compareIntegers(c1.getEnrolledStudents(), c2.getEnrolledStudents());
            case "maxStudents":
                return compareIntegers(c1.getMaxStudents(), c2.getMaxStudents());
            case "createdAt":
                if (c1.getCreatedAt() != null && c2.getCreatedAt() != null) {
                    return c1.getCreatedAt().compareTo(c2.getCreatedAt());
                }
                return 0;
            default:
                return 0;
        }
    }

    /**
     * 比较字符串
     */
    private int compareStrings(String s1, String s2) {
        if (s1 == null && s2 == null) return 0;
        if (s1 == null) return -1;
        if (s2 == null) return 1;
        return s1.compareTo(s2);
    }

    /**
     * 比较BigDecimal
     */
    private int compareBigDecimals(java.math.BigDecimal b1, java.math.BigDecimal b2) {
        if (b1 == null && b2 == null) return 0;
        if (b1 == null) return -1;
        if (b2 == null) return 1;
        return b1.compareTo(b2);
    }

    /**
     * 比较Integer
     */
    private int compareIntegers(Integer i1, Integer i2) {
        if (i1 == null && i2 == null) return 0;
        if (i1 == null) return -1;
        if (i2 == null) return 1;
        return i1.compareTo(i2);
    }

    /**
     * 判断课程是否未排课
     */
    private boolean isIntelligentUnscheduledCourse(Course course) {
        try {
            // 基于课程状态和时间的智能判断
            if (course.getStatus() != 1) {
                return false; // 非启用状态的课程不需要排课
            }

            // 检查课程是否在当前学期
            String currentSemester = getCurrentSemester();
            if (!currentSemester.equals(course.getSemester())) {
                return false; // 非当前学期的课程不需要排课
            }

            // 检查课程是否有选课时间设置
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            if (course.getSelectionStartTime() != null && now.isBefore(course.getSelectionStartTime())) {
                return false; // 选课尚未开始的课程暂不需要排课
            }

            // 检查课程是否有足够的选课学生
            if (course.getEnrolledStudents() != null && course.getEnrolledStudents() == 0) {
                return false; // 没有学生选课的课程暂不需要排课
            }

            return true;

        } catch (Exception e) {
            log.error("判断课程排课状态失败: courseId={}", course.getId(), e);
            return true; // 异常时默认需要排课
        }
    }

    /**
     * 获取当前学期
     */
    private String getCurrentSemester() {
        try {
            java.time.LocalDate now = java.time.LocalDate.now();
            int year = now.getYear();
            int month = now.getMonthValue();

            // 学期计算逻辑：9月-1月为第一学期，2月-8月为第二学期
            if (month >= 9 || month <= 1) {
                return year + "-" + (year + 1) + "-1";
            } else {
                return (year - 1) + "-" + year + "-2";
            }
        } catch (Exception e) {
            log.error("获取当前学期失败", e);
            return "2024-2025-1"; // 默认学期
        }
    }

}
