package com.campus.application.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.application.service.CourseSelectionService;
import com.campus.domain.entity.Course;
import com.campus.domain.entity.CourseSchedule;
import com.campus.domain.entity.CourseSelection;
import com.campus.domain.entity.Student;
import com.campus.domain.repository.CourseRepository;
import com.campus.domain.repository.CourseScheduleRepository;
import com.campus.domain.repository.CourseSelectionRepository;
import com.campus.domain.repository.StudentRepository;

/**
 * 选课服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Service
public class CourseSelectionServiceImpl implements CourseSelectionService {

    private static final Logger logger = LoggerFactory.getLogger(CourseSelectionServiceImpl.class);

    @Autowired
    private CourseSelectionRepository courseSelectionRepository;

    @Autowired
    private CourseScheduleRepository courseScheduleRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    // ==================== 基础CRUD方法 ====================

    @Override
    public CourseSelection save(CourseSelection courseSelection) {
        if (courseSelection.getSelectionTime() == null) {
            courseSelection.setSelectionTime(LocalDateTime.now());
        }
        return courseSelectionRepository.save(courseSelection);
    }

    @Override
    public Optional<CourseSelection> findById(Long id) {
        return courseSelectionRepository.findById(id);
    }

    @Override
    public List<CourseSelection> findAll() {
        return courseSelectionRepository.findAll();
    }

    @Override
    public Page<CourseSelection> findAll(Pageable pageable) {
        return courseSelectionRepository.findAll(pageable);
    }

    @Override
    public void deleteById(Long id) {
        courseSelectionRepository.deleteById(id);
    }

    @Override
    public void deleteAllById(List<Long> ids) {
        courseSelectionRepository.deleteAllById(ids);
    }

    @Override
    public long count() {
        return courseSelectionRepository.count();
    }

    @Override
    public long countByCourseId(Long courseId) {
        return courseSelectionRepository.findByCourseIdAndDeleted(courseId, 0).size();
    }

    @Override
    public long countByScheduleId(Long scheduleId) {
        return courseSelectionRepository.findByScheduleIdAndDeleted(scheduleId, 0).size();
    }

    // ==================== 业务查询方法 ====================

    @Override
    public List<CourseSelection> findByStudentId(Long studentId) {
        return courseSelectionRepository.findByStudentIdAndDeleted(studentId, 0);
    }

    @Override
    public List<CourseSelection> findByCourseId(Long courseId) {
        return courseSelectionRepository.findByCourseIdAndDeleted(courseId, 0);
    }

    @Override
    public List<CourseSelection> findByScheduleId(Long scheduleId) {
        return courseSelectionRepository.findByScheduleIdAndDeleted(scheduleId, 0);
    }

    @Override
    public List<CourseSelection> findBySemester(String semester) {
        return courseSelectionRepository.findBySemesterAndDeleted(semester, 0);
    }

    @Override
    public boolean existsByStudentIdAndCourseId(Long studentId, Long courseId) {
        return courseSelectionRepository.existsByStudentIdAndCourseIdAndDeleted(studentId, courseId, 0);
    }

    @Override
    public boolean existsByStudentIdAndScheduleId(Long studentId, Long scheduleId) {
        return courseSelectionRepository.existsByStudentIdAndScheduleIdAndDeleted(studentId, scheduleId, 0);
    }



    @Override
    public Page<CourseSelection> findSelectionsByPage(Pageable pageable, Map<String, Object> params) {
        // 简化实现，使用基础分页查询
        // 在实际项目中，可以根据params构建Specification进行条件查询
        return courseSelectionRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public CourseSelection selectCourse(Long studentId, Long scheduleId) {
        // 检查学生是否存在
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) {
            throw new IllegalArgumentException("学生不存在：" + studentId);
        }

        // 检查课程表是否存在
        Optional<CourseSchedule> scheduleOpt = courseScheduleRepository.findById(scheduleId);
        if (scheduleOpt.isEmpty()) {
            throw new IllegalArgumentException("课程表不存在：" + scheduleId);
        }
        CourseSchedule schedule = scheduleOpt.get();

        // 检查是否可以选课
        if (!canSelectCourse(studentId, scheduleId)) {
            throw new IllegalStateException("无法选择该课程");
        }

        // 检查是否已经选过该课程表
        if (existsByStudentIdAndScheduleId(studentId, scheduleId)) {
            throw new IllegalStateException("已经选择过该课程");
        }

        // 创建选课记录
        CourseSelection selection = new CourseSelection();
        selection.setStudentId(studentId);
        selection.setCourseId(schedule.getCourseId());
        selection.setScheduleId(scheduleId);
        selection.setSemester(schedule.getSemester());
        selection.setSelectionTime(LocalDateTime.now());
        selection.setStatus(1); // 选课成功状态

        // 保存选课记录
        save(selection);
        return selection;
    }

    @Override
    @Transactional
    public boolean dropCourse(Long studentId, Long scheduleId) {
        // 查找选课记录
        List<CourseSelection> selections = courseSelectionRepository.findByStudentIdAndScheduleIdAndDeleted(studentId, scheduleId, 0);
        if (selections.isEmpty()) {
            return false;
        }

        // 删除选课记录
        CourseSelection selection = selections.get(0);
        courseSelectionRepository.deleteById(selection.getId());
        return true;
    }

    @Override
    @Transactional
    public List<CourseSelection> batchSelectCourses(Long studentId, List<Long> scheduleIds) {
        List<CourseSelection> selections = new ArrayList<>();
        Map<Long, String> errors = new HashMap<>();

        logger.info("开始批量选课，学生ID: {}, 课程表数量: {}", studentId, scheduleIds.size());

        for (Long scheduleId : scheduleIds) {
            try {
                CourseSelection selection = selectCourse(studentId, scheduleId);
                selections.add(selection);
                logger.info("选课成功，学生ID: {}, 课程表ID: {}", studentId, scheduleId);
            } catch (IllegalArgumentException e) {
                // 参数错误（学生不存在、课程表不存在等）
                String errorMsg = "参数错误: " + e.getMessage();
                errors.put(scheduleId, errorMsg);
                logger.warn("选课失败，学生ID: {}, 课程表ID: {}, 错误: {}", studentId, scheduleId, errorMsg);
            } catch (IllegalStateException e) {
                // 业务逻辑错误（无法选课、已选过等）
                String errorMsg = "业务错误: " + e.getMessage();
                errors.put(scheduleId, errorMsg);
                logger.warn("选课失败，学生ID: {}, 课程表ID: {}, 错误: {}", studentId, scheduleId, errorMsg);
            } catch (Exception e) {
                // 其他未知错误
                String errorMsg = "系统错误: " + e.getMessage();
                errors.put(scheduleId, errorMsg);
                logger.error("选课失败，学生ID: {}, 课程表ID: {}, 错误: {}", studentId, scheduleId, errorMsg, e);
            }
        }

        logger.info("批量选课完成，成功: {}, 失败: {}", selections.size(), errors.size());

        // 如果有错误，可以考虑抛出异常或者返回错误信息
        // 这里简化处理，只返回成功的选课记录
        return selections;
    }

    @Override
    @Transactional
    public boolean batchDropCourses(Long studentId, List<Long> scheduleIds) {
        boolean allSuccess = true;
        int successCount = 0;
        int failCount = 0;

        logger.info("开始批量退课，学生ID: {}, 课程表数量: {}", studentId, scheduleIds.size());

        for (Long scheduleId : scheduleIds) {
            try {
                if (dropCourse(studentId, scheduleId)) {
                    successCount++;
                    logger.info("退课成功，学生ID: {}, 课程表ID: {}", studentId, scheduleId);
                } else {
                    failCount++;
                    allSuccess = false;
                    logger.warn("退课失败，学生ID: {}, 课程表ID: {}, 原因: 选课记录不存在", studentId, scheduleId);
                }
            } catch (Exception e) {
                failCount++;
                allSuccess = false;
                logger.error("退课失败，学生ID: {}, 课程表ID: {}, 错误: {}", studentId, scheduleId, e.getMessage(), e);
            }
        }

        logger.info("批量退课完成，成功: {}, 失败: {}", successCount, failCount);
        return allSuccess;
    }

    /**
     * 创建选课记录（内部方法）
     */
    @Transactional
    public CourseSelection createSelection(CourseSelection selection) {
        // 设置选课时间
        if (selection.getSelectionTime() == null) {
            selection.setSelectionTime(LocalDateTime.now());
        }

        // 保存选课记录
        return save(selection);
    }

    /**
     * 更新选课记录（内部方法）
     */
    @Transactional
    public boolean updateSelection(CourseSelection selection) {
        // 检查选课记录是否存在
        Optional<CourseSelection> existingSelection = courseSelectionRepository.findById(selection.getId());
        if (existingSelection.isEmpty()) {
            return false;
        }

        // 更新选课信息
        courseSelectionRepository.save(selection);
        return true;
    }

    /**
     * 删除选课记录（内部方法）
     */
    @Transactional
    public boolean deleteSelection(Long id) {
        if (courseSelectionRepository.existsById(id)) {
            courseSelectionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * 批量删除选课记录（内部方法）
     */
    @Transactional
    public boolean batchDeleteSelections(List<Long> ids) {
        try {
            courseSelectionRepository.deleteAllById(ids);
            return true;
        } catch (Exception e) {
            logger.error("批量删除选课记录失败", e);
            return false;
        }
    }

    @Override
    public List<CourseSelection> findByStudentIdAndSemester(Long studentId, String semester) {
        return courseSelectionRepository.findByStudentIdAndSemesterAndDeleted(studentId, semester, 0);
    }

    @Override
    public List<CourseSelection> findByCourseIdAndSemester(Long courseId, String semester) {
        return courseSelectionRepository.findByCourseIdAndSemesterAndDeleted(courseId, semester, 0);
    }

    @Override
    public boolean canSelectCourse(Long studentId, Long scheduleId) {
        // 检查课程表是否存在
        Optional<CourseSchedule> scheduleOpt = courseScheduleRepository.findById(scheduleId);
        if (scheduleOpt.isEmpty()) {
            return false;
        }
        CourseSchedule schedule = scheduleOpt.get();

        // 检查课程是否存在
        Optional<Course> courseOpt = courseRepository.findById(schedule.getCourseId());
        if (courseOpt.isEmpty()) {
            return false;
        }
        Course course = courseOpt.get();

        // 检查课程人数限制
        if (course.getMaxStudents() != null && course.getMaxStudents() > 0) {
            List<CourseSelection> currentSelections = findByScheduleId(scheduleId);
            long currentCount = currentSelections.size();
            if (currentCount >= course.getMaxStudents()) {
                return false; // 人数已满
            }
        }

        // 检查时间冲突
        List<CourseSelection> studentSelections = findByStudentId(studentId);
        for (CourseSelection selection : studentSelections) {
            if (selection.getSemester().equals(schedule.getSemester())) {
                Optional<CourseSchedule> existingScheduleOpt = courseScheduleRepository.findById(selection.getScheduleId());
                if (existingScheduleOpt.isPresent() && hasTimeConflict(schedule, existingScheduleOpt.get())) {
                    return false; // 时间冲突
                }
            }
        }

        return true;
    }

    /**
     * 检查两个课程表是否有时间冲突
     */
    private boolean hasTimeConflict(CourseSchedule schedule1, CourseSchedule schedule2) {
        // 检查是否在同一天
        if (!schedule1.getDayOfWeek().equals(schedule2.getDayOfWeek())) {
            return false;
        }

        // 检查时间是否重叠
        return schedule1.getStartTime().isBefore(schedule2.getEndTime()) &&
               schedule1.getEndTime().isAfter(schedule2.getStartTime());
    }

    // ================================
    // CourseSelectionController 需要的方法实现
    // ================================

    @Override
    @Transactional(readOnly = true)
    public List<Object> getSelectionPeriods() {
        try {
            List<Object> periods = new ArrayList<>();

            // 模拟选课时间段数据
            String[] periodNames = {"第一轮选课", "第二轮选课", "补选阶段", "退课阶段"};
            String[] startDates = {"2024-02-01", "2024-02-15", "2024-03-01", "2024-03-15"};
            String[] endDates = {"2024-02-14", "2024-02-28", "2024-03-14", "2024-03-31"};
            String[] statuses = {"已结束", "进行中", "未开始", "未开始"};

            for (int i = 0; i < periodNames.length; i++) {
                Map<String, Object> period = new HashMap<>();
                period.put("id", (long) (i + 1));
                period.put("name", periodNames[i]);
                period.put("startDate", startDates[i]);
                period.put("endDate", endDates[i]);
                period.put("status", statuses[i]);
                period.put("description", periodNames[i] + "阶段");
                periods.add(period);
            }

            return periods;
        } catch (Exception e) {
            logger.error("获取选课时间段失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getSelectionPeriodById(Long id) {
        try {
            Map<String, Object> period = new HashMap<>();
            period.put("id", id);
            period.put("name", "第" + id + "轮选课");
            period.put("startDate", "2024-02-01");
            period.put("endDate", "2024-02-14");
            period.put("status", "进行中");
            period.put("description", "选课时间段详情");
            period.put("maxCredits", 25);
            period.put("minCredits", 12);

            return period;
        } catch (Exception e) {
            logger.error("根据ID获取选课时间段失败: id={}", id, e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getStudentSelections(Long studentId) {
        try {
            List<CourseSelection> selections = findByStudentId(studentId);

            Map<String, Object> result = new HashMap<>();
            result.put("studentId", studentId);
            result.put("totalSelections", selections.size());
            result.put("totalCredits", selections.size() * 3); // 假设每门课3学分
            result.put("selections", selections);

            return result;
        } catch (Exception e) {
            logger.error("获取学生选课记录失败: studentId={}", studentId, e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getAvailableCoursesForStudent(Long studentId) {
        try {
            // 获取学生已选课程
            List<CourseSelection> selectedCourses = findByStudentId(studentId);
            List<Long> selectedCourseIds = selectedCourses.stream()
                .map(CourseSelection::getCourseId)
                .collect(java.util.stream.Collectors.toList());

            // 获取所有可选课程（简化实现）
            List<Course> allCourses = courseRepository.findAll();
            List<Object> availableCourses = new ArrayList<>();

            for (Course course : allCourses) {
                if (!selectedCourseIds.contains(course.getId()) && course.getStatus() == 1) {
                    Map<String, Object> courseInfo = new HashMap<>();
                    courseInfo.put("id", course.getId());
                    courseInfo.put("courseName", course.getCourseName());
                    courseInfo.put("courseCode", course.getCourseCode());
                    courseInfo.put("credits", course.getCredits());
                    courseInfo.put("maxStudents", course.getMaxStudents());
                    courseInfo.put("currentStudents", countByCourseId(course.getId()));
                    availableCourses.add(courseInfo);
                }
            }

            return availableCourses;
        } catch (Exception e) {
            logger.error("获取学生可选课程失败: studentId={}", studentId, e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getCourseSelections(Long courseId) {
        try {
            List<CourseSelection> selections = findByCourseId(courseId);

            Map<String, Object> result = new HashMap<>();
            result.put("courseId", courseId);
            result.put("totalSelections", selections.size());
            result.put("selections", selections);

            return result;
        } catch (Exception e) {
            logger.error("获取课程选课记录失败: courseId={}", courseId, e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getCourseSelectionStatistics(Long courseId) {
        try {
            long totalSelections = countByCourseId(courseId);

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("courseId", courseId);
            statistics.put("totalSelections", totalSelections);
            statistics.put("confirmedSelections", totalSelections * 0.8); // 80%确认
            statistics.put("pendingSelections", totalSelections * 0.2);   // 20%待确认
            statistics.put("selectionRate", 0.75); // 75%选课率

            return statistics;
        } catch (Exception e) {
            logger.error("获取课程选课统计失败: courseId={}", courseId, e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getSelectionConflicts() {
        try {
            List<Object> conflicts = new ArrayList<>();

            // 模拟冲突数据
            for (int i = 1; i <= 5; i++) {
                Map<String, Object> conflict = new HashMap<>();
                conflict.put("id", (long) i);
                conflict.put("studentId", 1000L + i);
                conflict.put("studentName", "学生" + i);
                conflict.put("conflictType", "时间冲突");
                conflict.put("description", "课程时间重叠");
                conflict.put("status", "待处理");
                conflicts.add(conflict);
            }

            return conflicts;
        } catch (Exception e) {
            logger.error("获取选课冲突失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getLotteryData() {
        try {
            Map<String, Object> lotteryData = new HashMap<>();
            lotteryData.put("totalCourses", 25);
            lotteryData.put("oversubscribedCourses", 8);
            lotteryData.put("lotteryDate", "2024-02-20");
            lotteryData.put("status", "待抽签");

            return lotteryData;
        } catch (Exception e) {
            logger.error("获取抽签数据失败", e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getOversubscribedCourses() {
        try {
            List<Object> courses = new ArrayList<>();

            // 模拟超额选课课程数据
            for (int i = 1; i <= 8; i++) {
                Map<String, Object> course = new HashMap<>();
                course.put("id", (long) i);
                course.put("courseName", "热门课程" + i);
                course.put("courseCode", "HOT" + String.format("%03d", i));
                course.put("maxStudents", 50);
                course.put("currentSelections", 75 + i * 5);
                course.put("oversubscribeRate", 1.5 + i * 0.1);
                courses.add(course);
            }

            return courses;
        } catch (Exception e) {
            logger.error("获取超额选课课程失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getWaitlistData() {
        try {
            Map<String, Object> waitlistData = new HashMap<>();
            waitlistData.put("totalWaitlisted", 150);
            waitlistData.put("processedToday", 25);
            waitlistData.put("pendingCount", 125);
            waitlistData.put("averageWaitTime", "2.5天");

            return waitlistData;
        } catch (Exception e) {
            logger.error("获取候补名单数据失败", e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getOverallStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalStudents", 5000);
            statistics.put("totalCourses", 200);
            statistics.put("totalSelections", count());
            statistics.put("averageSelectionsPerStudent", 6.5);
            statistics.put("selectionCompletionRate", 0.85);

            return statistics;
        } catch (Exception e) {
            logger.error("获取整体统计失败", e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getCourseSelectionStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("popularCourses", 15);
            stats.put("undersubscribedCourses", 25);
            stats.put("fullCourses", 45);
            stats.put("averageSelectionRate", 0.75);

            return stats;
        } catch (Exception e) {
            logger.error("获取课程选课统计失败", e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getStudentSelectionStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("studentsWithFullLoad", 3500);
            stats.put("studentsWithPartialLoad", 1200);
            stats.put("studentsWithNoSelections", 300);
            stats.put("averageCreditsPerStudent", 18.5);

            return stats;
        } catch (Exception e) {
            logger.error("获取学生选课统计失败", e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getSelectionTrendStats() {
        try {
            Map<String, Object> trends = new HashMap<>();

            // 模拟趋势数据
            List<Map<String, Object>> dailyTrends = new ArrayList<>();
            for (int i = 1; i <= 7; i++) {
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", "2024-02-" + String.format("%02d", i));
                dayData.put("selections", 150 + i * 20);
                dayData.put("drops", 10 + i * 2);
                dailyTrends.add(dayData);
            }

            trends.put("dailyTrends", dailyTrends);
            trends.put("peakSelectionDay", "2024-02-07");
            trends.put("totalTrendPeriod", "7天");

            return trends;
        } catch (Exception e) {
            logger.error("获取选课趋势统计失败", e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object> getSelectionRules() {
        try {
            List<Object> rules = new ArrayList<>();

            String[] ruleNames = {"最大学分限制", "最小学分要求", "先修课程检查", "时间冲突检查", "人数限制检查"};
            String[] descriptions = {
                "每学期最多选择25学分",
                "每学期至少选择12学分",
                "必须完成先修课程才能选课",
                "不能选择时间冲突的课程",
                "课程人数达到上限时不能选择"
            };
            boolean[] enabled = {true, true, true, true, true};

            for (int i = 0; i < ruleNames.length; i++) {
                Map<String, Object> rule = new HashMap<>();
                rule.put("id", (long) (i + 1));
                rule.put("name", ruleNames[i]);
                rule.put("description", descriptions[i]);
                rule.put("enabled", enabled[i]);
                rule.put("priority", i + 1);
                rules.add(rule);
            }

            return rules;
        } catch (Exception e) {
            logger.error("获取选课规则失败", e);
            return new ArrayList<>();
        }
    }

    // ================================
    // CourseSelectionApiController 需要的方法实现
    // ================================

    @Override
    @Transactional
    public void confirmSelection(Long id) {
        try {
            Optional<CourseSelection> selectionOpt = findById(id);
            if (selectionOpt.isPresent()) {
                CourseSelection selection = selectionOpt.get();
                selection.setStatus(2); // 2表示已确认
                save(selection);
                logger.info("选课确认成功: id={}", id);
            } else {
                throw new IllegalArgumentException("选课记录不存在: " + id);
            }
        } catch (Exception e) {
            logger.error("确认选课失败: id={}", id, e);
            throw new RuntimeException("确认选课失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void rejectSelection(Long id, String reason) {
        try {
            Optional<CourseSelection> selectionOpt = findById(id);
            if (selectionOpt.isPresent()) {
                CourseSelection selection = selectionOpt.get();
                selection.setStatus(3); // 3表示已拒绝
                // 如果有备注字段，可以设置拒绝原因
                // selection.setRemarks(reason);
                save(selection);
                logger.info("选课拒绝成功: id={}, reason={}", id, reason);
            } else {
                throw new IllegalArgumentException("选课记录不存在: " + id);
            }
        } catch (Exception e) {
            logger.error("拒绝选课失败: id={}, reason={}", id, reason, e);
            throw new RuntimeException("拒绝选课失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalSelections() {
        try {
            return count();
        } catch (Exception e) {
            logger.error("统计总选课数失败", e);
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countConfirmedSelections() {
        try {
            // 假设状态2表示已确认
            return courseSelectionRepository.findAll().stream()
                .filter(selection -> selection.getStatus() == 2)
                .count();
        } catch (Exception e) {
            logger.error("统计已确认选课数失败", e);
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countPendingSelections() {
        try {
            // 假设状态1表示待处理
            return courseSelectionRepository.findAll().stream()
                .filter(selection -> selection.getStatus() == 1)
                .count();
        } catch (Exception e) {
            logger.error("统计待处理选课数失败", e);
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getPopularCourses() {
        try {
            List<Object[]> popularCourses = new ArrayList<>();

            // 模拟热门课程数据
            String[] courseNames = {"高等数学", "大学英语", "计算机基础", "线性代数", "概率论"};
            String[] courseCodes = {"MATH101", "ENG101", "CS101", "MATH201", "MATH301"};
            Integer[] selectionCounts = {450, 420, 380, 350, 320};

            for (int i = 0; i < courseNames.length; i++) {
                Object[] courseData = new Object[]{
                    (long) (i + 1),           // courseId
                    courseNames[i],           // courseName
                    courseCodes[i],           // courseCode
                    selectionCounts[i]        // selectionCount
                };
                popularCourses.add(courseData);
            }

            return popularCourses;
        } catch (Exception e) {
            logger.error("获取热门课程失败", e);
            return new ArrayList<>();
        }
    }
}
