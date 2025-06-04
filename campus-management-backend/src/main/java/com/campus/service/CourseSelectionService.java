package com.campus.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.entity.CourseSelection;
import com.campus.repository.CourseSelectionRepository.SelectionDetail;
import com.campus.repository.CourseSelectionRepository.StudentSelectionDetail;
import com.campus.repository.CourseSelectionRepository.ScheduleStudentDetail;

/**
 * 选课服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
public interface CourseSelectionService extends IService<CourseSelection> {

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
     * 获取选课详情
     *
     * @param selectionId 选课ID
     * @return 选课详情
     */
    Optional<SelectionDetail> findSelectionDetailById(Long selectionId);

    /**
     * 获取学生的选课详情列表
     *
     * @param studentId 学生ID
     * @param semester 学期
     * @return 选课详情列表
     */
    List<StudentSelectionDetail> findStudentSelectionDetails(Long studentId, String semester);

    /**
     * 获取课程表的选课学生列表
     *
     * @param scheduleId 课程表ID
     * @return 选课学生列表
     */
    List<ScheduleStudentDetail> findScheduleStudentDetails(Long scheduleId);

    /**
     * 分页查询选课列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param params 查询参数
     * @return 分页结果
     */
    IPage<CourseSelection> findSelectionsByPage(int page, int size, Map<String, Object> params);

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
     * 创建选课记录
     *
     * @param selection 选课信息
     * @return 创建结果
     */
    CourseSelection createSelection(CourseSelection selection);

    /**
     * 更新选课信息
     *
     * @param selection 选课信息
     * @return 更新结果
     */
    boolean updateSelection(CourseSelection selection);

    /**
     * 删除选课记录
     *
     * @param id 选课ID
     * @return 删除结果
     */
    boolean deleteSelection(Long id);

    /**
     * 批量删除选课记录
     *
     * @param ids 选课ID列表
     * @return 删除结果
     */
    boolean batchDeleteSelections(List<Long> ids);

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
}
