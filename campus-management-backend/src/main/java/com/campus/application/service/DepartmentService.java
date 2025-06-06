package com.campus.application.service;

import com.campus.domain.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 院系服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
public interface DepartmentService {

    /**
     * 创建院系
     */
    Department createDepartment(Department department);

    /**
     * 更新院系
     */
    Department updateDepartment(Long id, Department department);

    /**
     * 根据ID获取院系
     */
    Optional<Department> getDepartmentById(Long id);

    /**
     * 根据院系代码获取院系
     */
    Optional<Department> getDepartmentByCode(String deptCode);

    /**
     * 获取所有院系
     */
    List<Department> getAllDepartments();

    /**
     * 分页获取院系
     */
    Page<Department> getDepartments(Pageable pageable);

    /**
     * 根据条件分页查询院系
     */
    Page<Department> getDepartmentsByConditions(String deptName, String deptCode, 
                                              String deptType, Integer deptLevel, 
                                              Pageable pageable);

    /**
     * 获取院系树结构
     */
    List<Department> getDepartmentTree();

    /**
     * 获取顶级院系
     */
    List<Department> getTopLevelDepartments();

    /**
     * 获取子院系
     */
    List<Department> getChildDepartments(Long parentId);

    /**
     * 根据类型获取院系
     */
    List<Department> getDepartmentsByType(String deptType);

    /**
     * 根据级别获取院系
     */
    List<Department> getDepartmentsByLevel(Integer deptLevel);

    /**
     * 删除院系（软删除）
     */
    void deleteDepartment(Long id);

    /**
     * 批量删除院系
     */
    void deleteDepartments(List<Long> ids);

    /**
     * 启用院系
     */
    void enableDepartment(Long id);

    /**
     * 禁用院系
     */
    void disableDepartment(Long id);

    /**
     * 检查院系代码是否存在
     */
    boolean existsByDeptCode(String deptCode);

    /**
     * 检查院系名称是否存在
     */
    boolean existsByDeptName(String deptName);

    /**
     * 检查院系代码是否存在（排除指定ID）
     */
    boolean existsByDeptCodeExcludeId(String deptCode, Long excludeId);

    /**
     * 检查院系名称是否存在（排除指定ID）
     */
    boolean existsByDeptNameExcludeId(String deptName, Long excludeId);

    /**
     * 统计院系数量
     */
    long countDepartments();

    /**
     * 统计子院系数量
     */
    long countChildDepartments(Long parentId);

    /**
     * 更新排序
     */
    void updateSortOrder(Long id, Integer sortOrder);

    /**
     * 设置院系负责人
     */
    void setDepartmentLeader(Long id, Long leaderId);

    /**
     * 移动院系到新的上级院系
     */
    void moveDepartment(Long id, Long newParentId);
}