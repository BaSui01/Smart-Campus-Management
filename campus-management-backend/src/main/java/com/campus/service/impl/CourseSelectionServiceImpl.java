package com.campus.service.impl;

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

import com.campus.entity.Course;
import com.campus.entity.CourseSchedule;
import com.campus.entity.CourseSelection;
import com.campus.entity.Student;
import com.campus.repository.CourseRepository;
import com.campus.repository.CourseScheduleRepository;
import com.campus.repository.CourseSelectionRepository;
import com.campus.repository.StudentRepository;
import com.campus.service.CourseSelectionService;

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
}
