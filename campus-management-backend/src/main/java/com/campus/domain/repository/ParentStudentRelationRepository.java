package com.campus.domain.repository;

import com.campus.domain.entity.ParentStudentRelation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 家长学生关系Repository接口
 * 提供家长学生关系相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface ParentStudentRelationRepository extends BaseRepository<ParentStudentRelation> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据家长ID查找关系
     */
    @Query("SELECT psr FROM ParentStudentRelation psr WHERE psr.parentId = :parentId AND psr.deleted = 0 ORDER BY psr.createdAt DESC")
    List<ParentStudentRelation> findByParentId(@Param("parentId") Long parentId);

    /**
     * 分页根据家长ID查找关系
     */
    @Query("SELECT psr FROM ParentStudentRelation psr WHERE psr.parentId = :parentId AND psr.deleted = 0")
    Page<ParentStudentRelation> findByParentId(@Param("parentId") Long parentId, Pageable pageable);

    /**
     * 根据学生ID查找关系
     */
    @Query("SELECT psr FROM ParentStudentRelation psr WHERE psr.studentId = :studentId AND psr.deleted = 0 ORDER BY psr.createdAt DESC")
    List<ParentStudentRelation> findByStudentId(@Param("studentId") Long studentId);

    /**
     * 分页根据学生ID查找关系
     */
    @Query("SELECT psr FROM ParentStudentRelation psr WHERE psr.studentId = :studentId AND psr.deleted = 0")
    Page<ParentStudentRelation> findByStudentId(@Param("studentId") Long studentId, Pageable pageable);

    /**
     * 根据关系类型查找关系
     */
    @Query("SELECT psr FROM ParentStudentRelation psr WHERE psr.relationType = :relationType AND psr.deleted = 0 ORDER BY psr.createdAt DESC")
    List<ParentStudentRelation> findByRelationType(@Param("relationType") String relationType);

    /**
     * 根据家长和学生ID查找关系
     */
    @Query("SELECT psr FROM ParentStudentRelation psr WHERE psr.parentId = :parentId AND psr.studentId = :studentId AND psr.deleted = 0")
    Optional<ParentStudentRelation> findByParentIdAndStudentId(@Param("parentId") Long parentId, @Param("studentId") Long studentId);

    /**
     * 查找启用的关系
     */
    @Query("SELECT psr FROM ParentStudentRelation psr WHERE psr.status = 1 AND psr.deleted = 0 ORDER BY psr.createdAt DESC")
    List<ParentStudentRelation> findActiveRelations();

    /**
     * 分页查找启用的关系
     */
    @Query("SELECT psr FROM ParentStudentRelation psr WHERE psr.status = 1 AND psr.deleted = 0")
    Page<ParentStudentRelation> findActiveRelations(Pageable pageable);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找关系
     */
    @Query("SELECT psr FROM ParentStudentRelation psr WHERE " +
           "(:parentId IS NULL OR psr.parentId = :parentId) AND " +
           "(:studentId IS NULL OR psr.studentId = :studentId) AND " +
           "(:relationType IS NULL OR psr.relationType = :relationType) AND " +
           "(:status IS NULL OR psr.status = :status) AND " +
           "psr.deleted = 0")
    Page<ParentStudentRelation> findByMultipleConditions(@Param("parentId") Long parentId,
                                                        @Param("studentId") Long studentId,
                                                        @Param("relationType") String relationType,
                                                        @Param("status") Integer status,
                                                        Pageable pageable);

    /**
     * 根据家长和关系类型查找关系
     */
    @Query("SELECT psr FROM ParentStudentRelation psr WHERE psr.parentId = :parentId AND psr.relationType = :relationType AND psr.deleted = 0 ORDER BY psr.createdAt DESC")
    List<ParentStudentRelation> findByParentIdAndRelationType(@Param("parentId") Long parentId, @Param("relationType") String relationType);

    /**
     * 根据学生和关系类型查找关系
     */
    @Query("SELECT psr FROM ParentStudentRelation psr WHERE psr.studentId = :studentId AND psr.relationType = :relationType AND psr.deleted = 0 ORDER BY psr.createdAt DESC")
    List<ParentStudentRelation> findByStudentIdAndRelationType(@Param("studentId") Long studentId, @Param("relationType") String relationType);

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 查找关系并预加载家长信息
     */
    @Query("SELECT DISTINCT psr FROM ParentStudentRelation psr LEFT JOIN FETCH psr.parent WHERE psr.deleted = 0")
    List<ParentStudentRelation> findAllWithParent();

    /**
     * 查找关系并预加载学生信息
     */
    @Query("SELECT DISTINCT psr FROM ParentStudentRelation psr LEFT JOIN FETCH psr.student WHERE psr.deleted = 0")
    List<ParentStudentRelation> findAllWithStudent();

    /**
     * 查找关系并预加载所有关联信息
     */
    @Query("SELECT DISTINCT psr FROM ParentStudentRelation psr " +
           "LEFT JOIN FETCH psr.parent p " +
           "LEFT JOIN FETCH psr.student s " +
           "WHERE psr.deleted = 0")
    List<ParentStudentRelation> findAllWithAssociations();

    /**
     * 根据家长ID查找关系并预加载学生信息
     */
    @Query("SELECT DISTINCT psr FROM ParentStudentRelation psr LEFT JOIN FETCH psr.student WHERE psr.parentId = :parentId AND psr.deleted = 0 ORDER BY psr.createdAt DESC")
    List<ParentStudentRelation> findByParentIdWithStudent(@Param("parentId") Long parentId);

    /**
     * 根据学生ID查找关系并预加载家长信息
     */
    @Query("SELECT DISTINCT psr FROM ParentStudentRelation psr LEFT JOIN FETCH psr.parent WHERE psr.studentId = :studentId AND psr.deleted = 0 ORDER BY psr.createdAt DESC")
    List<ParentStudentRelation> findByStudentIdWithParent(@Param("studentId") Long studentId);

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据关系类型统计数量
     */
    @Query("SELECT psr.relationType, COUNT(psr) FROM ParentStudentRelation psr WHERE psr.deleted = 0 GROUP BY psr.relationType ORDER BY COUNT(psr) DESC")
    List<Object[]> countByRelationType();

    /**
     * 根据状态统计数量
     */
    @Query("SELECT psr.status, COUNT(psr) FROM ParentStudentRelation psr WHERE psr.deleted = 0 GROUP BY psr.status")
    List<Object[]> countByStatus();

    /**
     * 根据家长统计关系数量
     */
    @Query("SELECT u.username, COUNT(psr) FROM ParentStudentRelation psr LEFT JOIN psr.parent u WHERE psr.deleted = 0 GROUP BY psr.parentId, u.username ORDER BY COUNT(psr) DESC")
    List<Object[]> countByParent();

    /**
     * 根据学生统计关系数量
     */
    @Query("SELECT s.studentNo, COUNT(psr) FROM ParentStudentRelation psr LEFT JOIN psr.student s WHERE psr.deleted = 0 GROUP BY psr.studentId, s.studentNo ORDER BY COUNT(psr) DESC")
    List<Object[]> countByStudent();

    /**
     * 统计指定家长的关系数量
     */
    @Query("SELECT COUNT(psr) FROM ParentStudentRelation psr WHERE psr.parentId = :parentId AND psr.deleted = 0")
    long countByParentId(@Param("parentId") Long parentId);

    /**
     * 统计指定学生的关系数量
     */
    @Query("SELECT COUNT(psr) FROM ParentStudentRelation psr WHERE psr.studentId = :studentId AND psr.deleted = 0")
    long countByStudentId(@Param("studentId") Long studentId);

    /**
     * 统计指定关系类型的数量
     */
    @Query("SELECT COUNT(psr) FROM ParentStudentRelation psr WHERE psr.relationType = :relationType AND psr.deleted = 0")
    long countByRelationType(@Param("relationType") String relationType);

    /**
     * 统计启用的关系数量
     */
    @Query("SELECT COUNT(psr) FROM ParentStudentRelation psr WHERE psr.status = 1 AND psr.deleted = 0")
    long countActiveRelations();

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查家长学生关系是否存在
     */
    @Query("SELECT CASE WHEN COUNT(psr) > 0 THEN true ELSE false END FROM ParentStudentRelation psr WHERE psr.parentId = :parentId AND psr.studentId = :studentId AND psr.deleted = 0")
    boolean existsByParentIdAndStudentId(@Param("parentId") Long parentId, @Param("studentId") Long studentId);

    /**
     * 检查家长学生关系是否存在（指定关系类型）
     */
    @Query("SELECT CASE WHEN COUNT(psr) > 0 THEN true ELSE false END FROM ParentStudentRelation psr WHERE psr.parentId = :parentId AND psr.studentId = :studentId AND psr.relationType = :relationType AND psr.deleted = 0")
    boolean existsByParentIdAndStudentIdAndRelationType(@Param("parentId") Long parentId, 
                                                       @Param("studentId") Long studentId, 
                                                       @Param("relationType") String relationType);

    /**
     * 检查家长是否有子女
     */
    @Query("SELECT CASE WHEN COUNT(psr) > 0 THEN true ELSE false END FROM ParentStudentRelation psr WHERE psr.parentId = :parentId AND psr.deleted = 0")
    boolean hasChildren(@Param("parentId") Long parentId);

    /**
     * 检查学生是否有家长
     */
    @Query("SELECT CASE WHEN COUNT(psr) > 0 THEN true ELSE false END FROM ParentStudentRelation psr WHERE psr.studentId = :studentId AND psr.deleted = 0")
    boolean hasParents(@Param("studentId") Long studentId);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新关系状态
     */
    @Modifying
    @Query("UPDATE ParentStudentRelation psr SET psr.status = :status, psr.updatedAt = CURRENT_TIMESTAMP WHERE psr.id = :relationId")
    int updateStatus(@Param("relationId") Long relationId, @Param("status") Integer status);

    /**
     * 批量更新关系状态
     */
    @Modifying
    @Query("UPDATE ParentStudentRelation psr SET psr.status = :status, psr.updatedAt = CURRENT_TIMESTAMP WHERE psr.id IN :relationIds")
    int batchUpdateStatus(@Param("relationIds") List<Long> relationIds, @Param("status") Integer status);

    /**
     * 更新关系类型
     */
    @Modifying
    @Query("UPDATE ParentStudentRelation psr SET psr.relationType = :relationType, psr.updatedAt = CURRENT_TIMESTAMP WHERE psr.id = :relationId")
    int updateRelationType(@Param("relationId") Long relationId, @Param("relationType") String relationType);

    /**
     * 停用家长的所有关系
     */
    @Modifying
    @Query("UPDATE ParentStudentRelation psr SET psr.status = 0, psr.updatedAt = CURRENT_TIMESTAMP WHERE psr.parentId = :parentId")
    int deactivateAllByParentId(@Param("parentId") Long parentId);

    /**
     * 停用学生的所有关系
     */
    @Modifying
    @Query("UPDATE ParentStudentRelation psr SET psr.status = 0, psr.updatedAt = CURRENT_TIMESTAMP WHERE psr.studentId = :studentId")
    int deactivateAllByStudentId(@Param("studentId") Long studentId);

    // ================================
    // 关系管理方法
    // ================================

    /**
     * 获取所有关系类型
     */
    @Query("SELECT DISTINCT psr.relationType FROM ParentStudentRelation psr WHERE psr.deleted = 0 ORDER BY psr.relationType")
    List<String> findAllRelationTypes();

    /**
     * 获取家长的所有子女ID
     */
    @Query("SELECT psr.studentId FROM ParentStudentRelation psr WHERE psr.parentId = :parentId AND psr.status = 1 AND psr.deleted = 0")
    List<Long> findStudentIdsByParentId(@Param("parentId") Long parentId);

    /**
     * 获取学生的所有家长ID
     */
    @Query("SELECT psr.parentId FROM ParentStudentRelation psr WHERE psr.studentId = :studentId AND psr.status = 1 AND psr.deleted = 0")
    List<Long> findParentIdsByStudentId(@Param("studentId") Long studentId);

    /**
     * 获取学生的主要监护人
     */
    @Query("SELECT psr FROM ParentStudentRelation psr WHERE psr.studentId = :studentId AND psr.relationType = 'GUARDIAN' AND psr.status = 1 AND psr.deleted = 0")
    Optional<ParentStudentRelation> findPrimaryGuardianByStudentId(@Param("studentId") Long studentId);

    /**
     * 获取家长的主要监护学生
     */
    @Query("SELECT psr FROM ParentStudentRelation psr WHERE psr.parentId = :parentId AND psr.relationType = 'GUARDIAN' AND psr.status = 1 AND psr.deleted = 0 ORDER BY psr.createdAt ASC")
    List<ParentStudentRelation> findPrimaryStudentsByParentId(@Param("parentId") Long parentId);

}
