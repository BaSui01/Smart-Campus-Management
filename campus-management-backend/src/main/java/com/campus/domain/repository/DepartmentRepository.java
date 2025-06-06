package com.campus.domain.repository;

import com.campus.domain.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 院系数据访问接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * 根据院系代码查找院系
     */
    Optional<Department> findByDeptCodeAndDeleted(String deptCode, Integer deleted);

    /**
     * 根据院系名称查找院系
     */
    Optional<Department> findByDeptNameAndDeleted(String deptName, Integer deleted);

    /**
     * 查找所有未删除的院系
     */
    List<Department> findByDeletedOrderBySortOrderAsc(Integer deleted);

    /**
     * 根据上级院系ID查找子院系
     */
    List<Department> findByParentIdAndDeletedOrderBySortOrderAsc(Long parentId, Integer deleted);

    /**
     * 查找顶级院系（没有上级院系）
     */
    List<Department> findByParentIdIsNullAndDeletedOrderBySortOrderAsc(Integer deleted);

    /**
     * 根据院系类型查找院系
     */
    List<Department> findByDeptTypeAndDeletedOrderBySortOrderAsc(String deptType, Integer deleted);

    /**
     * 根据院系级别查找院系
     */
    List<Department> findByDeptLevelAndDeletedOrderBySortOrderAsc(Integer deptLevel, Integer deleted);

    /**
     * 分页查询院系
     */
    Page<Department> findByDeletedOrderByCreatedAtDesc(Integer deleted, Pageable pageable);

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
     * 根据负责人ID查找院系
     */
    List<Department> findByLeaderIdAndDeleted(Long leaderId, Integer deleted);

    /**
     * 查找启用的院系
     */
    List<Department> findByStatusAndDeletedOrderBySortOrderAsc(Integer status, Integer deleted);
}