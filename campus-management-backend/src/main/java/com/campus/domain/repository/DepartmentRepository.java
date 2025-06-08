package com.campus.domain.repository;

import com.campus.domain.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 部门Repository接口
 * 提供部门相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface DepartmentRepository extends BaseRepository<Department> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据部门代码查找部门
     */
    @Query("SELECT d FROM Department d WHERE d.deptCode = :deptCode AND d.deleted = 0")
    Optional<Department> findByDeptCode(@Param("deptCode") String deptCode);

    /**
     * 根据部门名称查找部门
     */
    @Query("SELECT d FROM Department d WHERE d.deptName = :deptName AND d.deleted = 0")
    Optional<Department> findByDeptName(@Param("deptName") String deptName);

    /**
     * 根据上级部门ID查找子部门
     */
    @Query("SELECT d FROM Department d WHERE d.parentId = :parentId AND d.deleted = 0 ORDER BY d.sortOrder ASC")
    List<Department> findByParentId(@Param("parentId") Long parentId);

    /**
     * 查找顶级部门（没有上级部门）
     */
    @Query("SELECT d FROM Department d WHERE d.parentId IS NULL AND d.deleted = 0 ORDER BY d.sortOrder ASC")
    List<Department> findTopLevelDepartments();

    /**
     * 根据部门类型查找部门
     */
    @Query("SELECT d FROM Department d WHERE d.deptType = :deptType AND d.deleted = 0 ORDER BY d.sortOrder ASC")
    List<Department> findByDeptType(@Param("deptType") String deptType);

    /**
     * 根据部门级别查找部门
     */
    @Query("SELECT d FROM Department d WHERE d.deptLevel = :deptLevel AND d.deleted = 0 ORDER BY d.sortOrder ASC")
    List<Department> findByDeptLevel(@Param("deptLevel") Integer deptLevel);

    /**
     * 根据负责人ID查找部门
     */
    @Query("SELECT d FROM Department d WHERE d.leaderId = :leaderId AND d.deleted = 0 ORDER BY d.sortOrder ASC")
    List<Department> findByLeaderId(@Param("leaderId") Long leaderId);

    /**
     * 根据部门名称模糊查询
     */
    @Query("SELECT d FROM Department d WHERE d.deptName LIKE %:deptName% AND d.deleted = 0 ORDER BY d.sortOrder ASC")
    List<Department> findByDeptNameContaining(@Param("deptName") String deptName);

    /**
     * 查找启用的部门
     */
    @Query("SELECT d FROM Department d WHERE d.status = 1 AND d.deleted = 0 ORDER BY d.sortOrder ASC")
    List<Department> findActiveDepartments();

    /**
     * 分页查找启用的部门
     */
    @Query("SELECT d FROM Department d WHERE d.status = 1 AND d.deleted = 0")
    Page<Department> findActiveDepartments(Pageable pageable);

    /**
     * 根据条件分页查询院系
     */
    @Query("SELECT d FROM Department d WHERE d.deleted = :deleted " +
           "AND (:deptName IS NULL OR d.deptName LIKE %:deptName%) " +
           "AND (:deptCode IS NULL OR d.deptCode LIKE %:deptCode%) " +
           "AND (:deptType IS NULL OR d.deptType = :deptType) " +
           "AND (:deptLevel IS NULL OR d.deptLevel = :deptLevel) " +
           "ORDER BY d.sortOrder ASC, d.createdAt DESC")
    Page<Department> findByConditions(@Param("deleted") Integer deleted,
                                    @Param("deptName") String deptName,
                                    @Param("deptCode") String deptCode,
                                    @Param("deptType") String deptType,
                                    @Param("deptLevel") Integer deptLevel,
                                    Pageable pageable);

    /**
     * 统计院系数量
     */
    long countByDeleted(Integer deleted);

    /**
     * 统计某上级院系下的子院系数量
     */
    long countByParentIdAndDeleted(Long parentId, Integer deleted);

    /**
     * 检查院系代码是否存在
     */
    boolean existsByDeptCodeAndDeleted(String deptCode, Integer deleted);

    /**
     * 检查院系名称是否存在
     */
    boolean existsByDeptNameAndDeleted(String deptName, Integer deleted);

    /**
     * 检查院系代码是否存在（排除指定ID）
     */
    boolean existsByDeptCodeAndDeletedAndIdNot(String deptCode, Integer deleted, Long id);

    /**
     * 检查院系名称是否存在（排除指定ID）
     */
    boolean existsByDeptNameAndDeletedAndIdNot(String deptName, Integer deleted, Long id);

    /**
     * 获取院系树结构
     */
    @Query("SELECT d FROM Department d WHERE d.deleted = 0 ORDER BY d.sortOrder ASC")
    List<Department> findDepartmentTree();



    /**
     * 查找启用的院系（兼容性方法）
     */
    default List<Department> findByStatusAndDeletedOrderBySortOrderAsc(Integer status, Integer deleted) {
        return findActiveDepartments();
    }

    // ================================
    // 兼容性方法（为现有Service提供支持）
    // ================================

    /**
     * 根据部门代码查找部门（兼容性方法）
     */
    default Optional<Department> findByDeptCodeAndDeleted(String deptCode, Integer deleted) {
        return findByDeptCode(deptCode);
    }

    /**
     * 根据部门名称查找部门（兼容性方法）
     */
    default Optional<Department> findByDeptNameAndDeleted(String deptName, Integer deleted) {
        return findByDeptName(deptName);
    }

    /**
     * 查找所有未删除的部门（兼容性方法）
     */
    @Query("SELECT d FROM Department d WHERE d.deleted = 0 ORDER BY d.sortOrder ASC")
    List<Department> findByDeletedOrderBySortOrderAsc(Integer deleted);

    /**
     * 根据上级部门ID查找子部门（兼容性方法）
     */
    default List<Department> findByParentIdAndDeletedOrderBySortOrderAsc(Long parentId, Integer deleted) {
        return findByParentId(parentId);
    }

    /**
     * 查找顶级部门（兼容性方法）
     */
    default List<Department> findByParentIdIsNullAndDeletedOrderBySortOrderAsc(Integer deleted) {
        return findTopLevelDepartments();
    }

    /**
     * 根据部门类型查找部门（兼容性方法）
     */
    default List<Department> findByDeptTypeAndDeletedOrderBySortOrderAsc(String deptType, Integer deleted) {
        return findByDeptType(deptType);
    }

    /**
     * 根据部门级别查找部门（兼容性方法）
     */
    default List<Department> findByDeptLevelAndDeletedOrderBySortOrderAsc(Integer deptLevel, Integer deleted) {
        return findByDeptLevel(deptLevel);
    }

    /**
     * 分页查询部门（兼容性方法）
     */
    @Query("SELECT d FROM Department d WHERE d.deleted = 0 ORDER BY d.createdAt DESC")
    Page<Department> findByDeletedOrderByCreatedAtDesc(Integer deleted, Pageable pageable);

    /**
     * 根据负责人ID查找部门（兼容性方法）
     */
    default List<Department> findByLeaderIdAndDeleted(Long leaderId, Integer deleted) {
        return findByLeaderId(leaderId);
    }

}