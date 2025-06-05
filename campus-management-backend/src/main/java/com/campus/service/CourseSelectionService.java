package com.campus.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.campus.entity.CourseSelection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 选课服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
public interface CourseSelectionService {

    /**
     * 保存选课记录
     *
     * @param courseSelection 选课记录
     * @return 保存的选课记录
     */
    CourseSelection save(CourseSelection courseSelection);

    /**
     * 根据ID查找选课记录
     *
     * @param id 选课ID
     * @return 选课记录
     */
    Optional<CourseSelection> findById(Long id);

    /**
     * 查找所有选课记录
     *
     * @return 选课记录列表
     */
    List<CourseSelection> findAll();

    /**
     * 分页查找所有选课记录
     *
     * @param pageable 分页参数
     * @return 选课记录分页结果
     */
    Page<CourseSelection> findAll(Pageable pageable);

    /**
     * 根据ID删除选课记录
     *
     * @param id 选课ID
     */
    void deleteById(Long id);

    /**
     * 批量删除选课记录
     *
     * @param ids 选课ID列表
     */
    void deleteAllById(List<Long> ids);

    /**
     * 根据学生ID查找选课记录
     *
     * @param studentId 学生ID
     * @return 选课记录列表
     */
    List<CourseSelection> findByStudentId(Long studentId);

    /**
     * 根据课程ID查找选课记录
     *
     * @param courseId 课程ID
     * @return 选课记录列表
     */
    List<CourseSelection> findByCourseId(Long courseId);

    /**
     * 根据课程表ID查找选课记录
     *
     * @param scheduleId 课程表ID
     * @return 选课记录列表
     */
    List<CourseSelection> findByScheduleId(Long scheduleId);

    /**
     * 根据学期查找选课记录
     *
     * @param semester 学期
     * @return 选课记录列表
     */
    List<CourseSelection> findBySemester(String semester);

    /**
     * 检查学生是否已选择课程
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 是否已选择
     */
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * 检查学生是否已选择课程表
     *
     * @param studentId 学生ID
     * @param scheduleId 课程表ID
     * @return 是否已选择
     */
    boolean existsByStudentIdAndScheduleId(Long studentId, Long scheduleId);

    /**
     * 分页查询选课列表
     *
     * @param pageable 分页参数
     * @param params 查询参数
     * @return 分页结果
     */
    Page<CourseSelection> findSelectionsByPage(Pageable pageable, Map<String, Object> params);

    /**
     * 学生选课
     *
     * @param studentId 学生ID
     * @param scheduleId 课程表ID
     * @return 选课结果
     */
    CourseSelection selectCourse(Long studentId, Long scheduleId);

    /**
     * 学生退课
     *
     * @param studentId 学生ID
     * @param scheduleId 课程表ID
     * @return 退课结果
     */
    boolean dropCourse(Long studentId, Long scheduleId);

    /**
     * 批量选课
     *
     * @param studentId 学生ID
     * @param scheduleIds 课程表ID列表
     * @return 选课结果
     */
    List<CourseSelection> batchSelectCourses(Long studentId, List<Long> scheduleIds);

    /**
     * 批量退课
     *
     * @param studentId 学生ID
     * @param scheduleIds 课程表ID列表
     * @return 退课结果
     */
    boolean batchDropCourses(Long studentId, List<Long> scheduleIds);

    /**
     * 根据学生ID和学期查找选课记录
     *
     * @param studentId 学生ID
     * @param semester 学期
     * @return 选课记录列表
     */
    List<CourseSelection> findByStudentIdAndSemester(Long studentId, String semester);

    /**
     * 根据课程ID和学期查找选课记录
     *
     * @param courseId 课程ID
     * @param semester 学期
     * @return 选课记录列表
     */
    List<CourseSelection> findByCourseIdAndSemester(Long courseId, String semester);

    /**
     * 检查学生是否可以选择指定课程表
     * 检查条件：课程人数限制、时间冲突等
     *
     * @param studentId 学生ID
     * @param scheduleId 课程表ID
     * @return 是否可以选课
     */
    boolean canSelectCourse(Long studentId, Long scheduleId);

    /**
     * 统计选课记录数量
     *
     * @return 总数量
     */
    long count();

    /**
     * 根据课程ID统计选课人数
     *
     * @param courseId 课程ID
     * @return 选课人数
     */
    long countByCourseId(Long courseId);

    /**
     * 根据课程表ID统计选课人数
     *
     * @param scheduleId 课程表ID
     * @return 选课人数
     */
    long countByScheduleId(Long scheduleId);
}
