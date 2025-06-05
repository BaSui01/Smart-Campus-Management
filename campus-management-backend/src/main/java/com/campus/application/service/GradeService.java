package com.campus.application.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.campus.domain.entity.Grade;

/**
 * 成绩服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
public interface GradeService {

    /**
     * 保存成绩记录
     *
     * @param grade 成绩记录
     * @return 保存的成绩记录
     */
    Grade save(Grade grade);

    /**
     * 根据ID查找成绩记录
     *
     * @param id 成绩ID
     * @return 成绩记录
     */
    Optional<Grade> findById(Long id);

    /**
     * 查找所有成绩记录
     *
     * @return 成绩记录列表
     */
    List<Grade> findAll();

    /**
     * 分页查找所有成绩记录
     *
     * @param pageable 分页参数
     * @return 成绩记录分页结果
     */
    Page<Grade> findAll(Pageable pageable);

    /**
     * 根据ID删除成绩记录
     *
     * @param id 成绩ID
     */
    void deleteById(Long id);

    /**
     * 批量删除成绩记录
     *
     * @param ids 成绩ID列表
     */
    void deleteAllById(List<Long> ids);

    /**
     * 统计成绩记录数量
     *
     * @return 总数量
     */
    long count();

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
     * 获取成绩详情（使用Object[]返回）
     *
     * @param gradeId 成绩ID
     * @return 成绩详情
     */
    Optional<Object[]> findGradeDetailById(Long gradeId);

    /**
     * 获取学生的成绩详情列表（使用Object[]返回）
     *
     * @param studentId 学生ID
     * @param semester 学期
     * @return 成绩详情列表
     */
    List<Object[]> findStudentGradeDetails(Long studentId, String semester);

    /**
     * 获取课程的学生成绩列表（使用Object[]返回）
     *
     * @param courseId 课程ID
     * @param scheduleId 课程表ID
     * @return 成绩详情列表
     */
    List<Object[]> findCourseGradeDetails(Long courseId, Long scheduleId);

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
     * @param pageable 分页参数
     * @param params 查询参数
     * @return 分页结果
     */
    Page<Grade> findGradesByPage(Pageable pageable, Map<String, Object> params);

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

    /**
     * 生成综合统计报告
     *
     * @return 统计报告数据
     */
    Map<String, Object> generateComprehensiveStatistics();

    /**
     * 搜索成绩
     *
     * @param keyword 关键词
     * @return 成绩列表
     */
    List<Grade> searchGrades(String keyword);

    /**
     * 获取学生成绩统计
     *
     * @param studentId 学生ID
     * @return 统计数据
     */
    Map<String, Object> getStudentGradeStatistics(Long studentId);

    /**
     * 获取课程成绩统计
     *
     * @param courseId 课程ID
     * @return 统计数据
     */
    Map<String, Object> getCourseGradeStatistics(Long courseId);

    /**
     * 导入成绩数据
     *
     * @param grades 成绩列表
     * @return 导入结果
     */
    Map<String, Object> importGrades(List<Grade> grades);

    /**
     * 导出成绩数据
     *
     * @param params 查询参数
     * @return 成绩列表
     */
    List<Grade> exportGrades(Map<String, Object> params);
}
