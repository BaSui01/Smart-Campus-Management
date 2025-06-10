package com.campus.application.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.campus.domain.entity.SchoolClass;

/**
 * 班级服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
public interface SchoolClassService {

    /**
     * 保存班级
     *
     * @param schoolClass 班级
     * @return 保存的班级
     */
    SchoolClass save(SchoolClass schoolClass);

    /**
     * 根据ID查找班级
     *
     * @param id 班级ID
     * @return 班级
     */
    Optional<SchoolClass> findById(Long id);

    /**
     * 查找所有班级
     *
     * @return 班级列表
     */
    List<SchoolClass> findAll();

    /**
     * 分页查找所有班级
     *
     * @param pageable 分页参数
     * @return 班级分页结果
     */
    Page<SchoolClass> findAll(Pageable pageable);

    /**
     * 根据ID删除班级
     *
     * @param id 班级ID
     */
    void deleteById(Long id);

    /**
     * 批量删除班级
     *
     * @param ids 班级ID列表
     */
    void deleteAllById(List<Long> ids);

    /**
     * 统计班级数量
     *
     * @return 总数量
     */
    long count();

    /**
     * 根据班级代码查找班级
     *
     * @param classCode 班级代码
     * @return 班级信息
     */
    Optional<SchoolClass> findByClassCode(String classCode);

    /**
     * 根据年级查找班级列表
     *
     * @param grade 年级
     * @return 班级列表
     */
    List<SchoolClass> findByGrade(String grade);

    /**
     * 根据部门ID查找班级列表
     *
     * @param departmentId 部门ID
     * @return 班级列表
     */
    List<SchoolClass> findByDepartmentId(Long departmentId);

    /**
     * 根据班主任ID查找班级列表
     *
     * @param headTeacherId 班主任ID
     * @return 班级列表
     */
    List<SchoolClass> findByHeadTeacherId(Long headTeacherId);

    /**
     * 检查班级代码是否存在
     *
     * @param classCode 班级代码
     * @return 是否存在
     */
    boolean existsByClassCode(String classCode);

    /**
     * 获取班级详情（使用Object[]返回）
     *
     * @param classId 班级ID
     * @return 班级详情
     */
    Optional<Object[]> findClassDetailById(Long classId);

    /**
     * 统计班级数量按年级（使用Object[]返回）
     *
     * @return 统计结果
     */
    List<Object[]> countClassesByGrade();

    /**
     * 分页查询班级列表
     *
     * @param pageable 分页参数
     * @param params 查询参数
     * @return 分页结果
     */
    Page<SchoolClass> findClassesByPage(Pageable pageable, Map<String, Object> params);

    /**
     * 创建班级
     *
     * @param schoolClass 班级信息
     * @return 创建结果
     */
    SchoolClass createClass(SchoolClass schoolClass);

    /**
     * 更新班级信息
     *
     * @param schoolClass 班级信息
     * @return 更新结果
     */
    boolean updateClass(SchoolClass schoolClass);

    /**
     * 删除班级
     *
     * @param id 班级ID
     * @return 删除结果
     */
    boolean deleteClass(Long id);

    /**
     * 批量删除班级
     *
     * @param ids 班级ID列表
     * @return 删除结果
     */
    boolean batchDeleteClasses(List<Long> ids);

    /**
     * 更新班级学生数量
     *
     * @param classId 班级ID
     * @return 更新结果
     */
    boolean updateStudentCount(Long classId);

    // ================================
    // 班级管理页面需要的方法
    // ================================

    /**
     * 搜索班级（分页）
     */
    Page<SchoolClass> searchClasses(String keyword, Pageable pageable);

    /**
     * 根据部门查找班级（分页）
     */
    Page<SchoolClass> findClassesByDepartment(Long departmentId, Pageable pageable);

    /**
     * 根据年级查找班级（分页）
     */
    Page<SchoolClass> findClassesByGrade(String grade, Pageable pageable);

    /**
     * 查找所有班级（分页）
     */
    Page<SchoolClass> findAllClasses(Pageable pageable);

    /**
     * 统计班级总数
     */
    long countTotalClasses();

    /**
     * 统计活跃班级数
     */
    long countActiveClasses();

    /**
     * 获取所有年级列表
     */
    List<String> findAllGrades();

    /**
     * 根据ID查找班级
     */
    SchoolClass findClassById(Long id);

    /**
     * 统计班级学生数量
     */
    long countStudentsByClass(Long classId);

    /**
     * 统计班级课程数量
     */
    long countCoursesByClass(Long classId);

    /**
     * 启用班级
     */
    boolean enableClass(Long classId);

    /**
     * 禁用班级
     */
    boolean disableClass(Long classId);

    /**
     * 按部门统计班级数量
     */
    Map<String, Long> countClassesByDepartment();

    /**
     * 获取所有班级（无分页）
     */
    List<SchoolClass> findAllClasses();
}
