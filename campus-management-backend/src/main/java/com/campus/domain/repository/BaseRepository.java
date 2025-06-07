package com.campus.domain.repository;

import com.campus.domain.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 基础Repository接口
 * 提供通用的CRUD操作和查询方法
 *
 * @param <T> 实体类型，必须继承BaseEntity
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据ID查找未删除的实体
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.deleted = 0")
    Optional<T> findByIdAndNotDeleted(@Param("id") Long id);

    /**
     * 查找所有未删除的实体
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = 0 ORDER BY e.createdAt DESC")
    List<T> findAllNotDeleted();

    /**
     * 分页查找所有未删除的实体
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = 0")
    Page<T> findAllNotDeleted(Pageable pageable);

    /**
     * 根据状态查找实体
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.status = :status AND e.deleted = 0 ORDER BY e.createdAt DESC")
    List<T> findByStatus(@Param("status") Integer status);

    /**
     * 分页根据状态查找实体
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.status = :status AND e.deleted = 0")
    Page<T> findByStatus(@Param("status") Integer status, Pageable pageable);

    /**
     * 查找启用状态的实体
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.status = 1 AND e.deleted = 0 ORDER BY e.createdAt DESC")
    List<T> findAllEnabled();

    /**
     * 分页查找启用状态的实体
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.status = 1 AND e.deleted = 0")
    Page<T> findAllEnabled(Pageable pageable);

    /**
     * 查找禁用状态的实体
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.status = 0 AND e.deleted = 0 ORDER BY e.createdAt DESC")
    List<T> findAllDisabled();

    /**
     * 分页查找禁用状态的实体
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.status = 0 AND e.deleted = 0")
    Page<T> findAllDisabled(Pageable pageable);

    /**
     * 根据创建时间范围查找实体
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.createdAt BETWEEN :startTime AND :endTime AND e.deleted = 0 ORDER BY e.createdAt DESC")
    List<T> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, 
                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 分页根据创建时间范围查找实体
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.createdAt BETWEEN :startTime AND :endTime AND e.deleted = 0")
    Page<T> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, 
                                   @Param("endTime") LocalDateTime endTime, 
                                   Pageable pageable);

    /**
     * 根据更新时间范围查找实体
     */
    @Query("SELECT e FROM #{#entityName} e WHERE e.updatedAt BETWEEN :startTime AND :endTime AND e.deleted = 0 ORDER BY e.updatedAt DESC")
    List<T> findByUpdatedAtBetween(@Param("startTime") LocalDateTime startTime, 
                                   @Param("endTime") LocalDateTime endTime);

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 统计未删除的实体数量
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.deleted = 0")
    long countNotDeleted();

    /**
     * 根据状态统计实体数量
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.status = :status AND e.deleted = 0")
    long countByStatus(@Param("status") Integer status);

    /**
     * 统计启用状态的实体数量
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.status = 1 AND e.deleted = 0")
    long countEnabled();

    /**
     * 统计禁用状态的实体数量
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.status = 0 AND e.deleted = 0")
    long countDisabled();

    /**
     * 统计已删除的实体数量
     */
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.deleted = 1")
    long countDeleted();

    // ================================
    // 批量操作方法
    // ================================

    /**
     * 批量软删除
     */
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted = 1, e.status = 0, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id IN :ids")
    int batchSoftDelete(@Param("ids") List<Long> ids);

    /**
     * 批量启用
     */
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.status = 1, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id IN :ids AND e.deleted = 0")
    int batchEnable(@Param("ids") List<Long> ids);

    /**
     * 批量禁用
     */
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.status = 0, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id IN :ids AND e.deleted = 0")
    int batchDisable(@Param("ids") List<Long> ids);

    /**
     * 批量恢复（取消删除）
     */
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted = 0, e.status = 1, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id IN :ids")
    int batchRestore(@Param("ids") List<Long> ids);

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查ID是否存在且未删除
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM #{#entityName} e WHERE e.id = :id AND e.deleted = 0")
    boolean existsByIdAndNotDeleted(@Param("id") Long id);

    /**
     * 检查ID是否存在且启用
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM #{#entityName} e WHERE e.id = :id AND e.status = 1 AND e.deleted = 0")
    boolean existsByIdAndEnabled(@Param("id") Long id);

    // ================================
    // 清理方法
    // ================================

    /**
     * 物理删除已软删除超过指定天数的记录
     */
    @Modifying
    @Query("DELETE FROM #{#entityName} e WHERE e.deleted = 1 AND e.updatedAt < :cutoffTime")
    int physicalDeleteOldSoftDeleted(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * 清理指定时间之前的已删除记录
     */
    @Modifying
    @Query("DELETE FROM #{#entityName} e WHERE e.deleted = 1 AND e.updatedAt < :beforeTime")
    int cleanupDeletedBefore(@Param("beforeTime") LocalDateTime beforeTime);
}
