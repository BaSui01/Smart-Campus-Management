package com.campus.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.entity.SchoolClass;
import com.campus.repository.SchoolClassRepository.ClassDetail;
import com.campus.repository.SchoolClassRepository.ClassGradeCount;

/**
 * 班级服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
public interface SchoolClassService extends IService<SchoolClass> {

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
     * 获取班级详情
     *
     * @param classId 班级ID
     * @return 班级详情
     */
    Optional<ClassDetail> findClassDetailById(Long classId);

    /**
     * 统计班级数量按年级
     *
     * @return 统计结果
     */
    List<ClassGradeCount> countClassesByGrade();

    /**
     * 分页查询班级列表
     *
     * @param page 页码
     * @param size 每页大小
     * @param params 查询参数
     * @return 分页结果
     */
    IPage<SchoolClass> findClassesByPage(int page, int size, Map<String, Object> params);

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
}
