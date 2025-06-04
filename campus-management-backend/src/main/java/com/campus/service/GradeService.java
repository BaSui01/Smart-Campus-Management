package com.campus.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.entity.Grade;
import com.campus.repository.GradeRepository.CourseGradeDetail;
import com.campus.repository.GradeRepository.GradeDetail;
import com.campus.repository.GradeRepository.StudentGradeDetail;

/**
 * 成绩服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
public interface GradeService extends IService<Grade> {

    /**
     * 根据学生ID查找成绩记录
     *
     * @param studentId 学生ID
     * @return 成绩记录列表
     */
    List<Grade> findByStudentId(Long studentId);

    /**
     * 根据课程ID查找成绩记录
     *
     * @param courseId 课程ID
     * @return 成绩记录列表
     */
    List<Grade> findByCourseId(Long courseId);

    /**
     * 根据课程表ID查找成绩记录
     *
     * @param scheduleId 课程表ID
     * @return 成绩记录列表
     */
    List<Grade> findByScheduleId(Long scheduleId);

    /**
     * 根据选课ID查找成绩记录
     *
     * @param selectionId 选课ID
     * @return 成绩记录
     */
    Optional<Grade> findBySelectionId(Long selectionId);

    /**
     * 根据学期查找成绩记录
     *
     * @param semester 学期
     * @return 成绩记录列表
     */
    List<Grade> findBySemester(String semester);

    /**
     * 根据学生ID和课程ID查找成绩记录
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 成绩记录列表
     */
    List<Grade> findByStudentIdAndCourseId(Long studentId, Long courseId);

    /**
     * 根据学生ID和学期查找成绩记录
     *
     * @param studentId 学生ID
     * @param semester 学期
     * @return 成绩记录列表
     */
    List<Grade> findByStudentIdAndSemester(Long studentId, String semester);

    /**
     * 获取成绩详情
     *
     * @param gradeId 成绩ID
     * @return 成绩详情
     */
    Optional<GradeDetail> findGradeDetailById(Long gradeId);

    /**
     * 获取学生的成绩详情列表
     *
     * @param studentId 学生ID
     * @param semester 学期
     * @return 成绩详情列表
     */
    List<StudentGradeDetail> findStudentGradeDetails(Long studentId, String semester);

    /**
     * 获取课程的学生成绩列表
     *
     * @param courseId 课程ID
     * @param scheduleId 课程表ID
     * @return 成绩详情列表
     */
    List<CourseGradeDetail> findCourseGradeDetails(Long courseId, Long scheduleId);

    /**
     * 计算学生的平均绩点
     *
     * @param studentId 学生ID
     * @param semester 学期
     * @return 平均绩点
     */
    Double calculateStudentGPA(Long studentId, String semester);

    /**
     * 计算班级的平均成绩
     *
     * @param classId 班级ID
     * @param courseId 课程ID
     * @return 平均成绩
     */
    Double calculateClassAverageScore(Long classId, Long courseId);

    /**
     * 分页查询成绩列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param params 查询参数
     * @return 分页结果
     */
    IPage<Grade> findGradesByPage(int page, int size, Map<String, Object> params);

    /**
     * 创建成绩
     *
     * @param grade 成绩信息
     * @return 创建结果
     */
    Grade createGrade(Grade grade);

    /**
     * 更新成绩信息
     *
     * @param grade 成绩信息
     * @return 更新结果
     */
    boolean updateGrade(Grade grade);

    /**
     * 批量更新成绩
     *
     * @param grades 成绩列表
     * @return 更新结果
     */
    boolean batchUpdateGrades(List<Grade> grades);

    /**
     * 删除成绩
     *
     * @param id 成绩ID
     * @return 删除结果
     */
    boolean deleteGrade(Long id);

    /**
     * 批量删除成绩
     *
     * @param ids 成绩ID列表
     * @return 删除结果
     */
    boolean batchDeleteGrades(List<Long> ids);

    /**
     * 计算总成绩
     * 根据平时成绩、期中成绩和期末成绩计算总成绩
     *
     * @param grade 成绩信息
     * @return 更新后的成绩
     */
    Grade calculateFinalScore(Grade grade);

    /**
     * 计算绩点和等级
     * 根据总成绩计算绩点和等级
     *
     * @param grade 成绩信息
     * @return 更新后的成绩
     */
    Grade calculateGradePointAndLevel(Grade grade);

    /**
     * 从选课记录创建成绩记录
     *
     * @param selectionId 选课ID
     * @return 创建的成绩记录
     */
    Grade createGradeFromSelection(Long selectionId);

    /**
     * 批量从选课记录创建成绩记录
     *
     * @param scheduleId 课程表ID
     * @return 创建的成绩记录数量
     */
    int batchCreateGradesFromSchedule(Long scheduleId);
}
