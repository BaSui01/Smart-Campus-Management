package com.campus.domain.repository;

import com.campus.domain.entity.FeeItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 缴费项目Repository接口
 * 提供缴费项目相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface FeeItemRepository extends BaseRepository<FeeItem> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据项目编码查找缴费项目
     */
    @Query("SELECT f FROM FeeItem f WHERE f.itemCode = :itemCode AND f.deleted = 0")
    Optional<FeeItem> findByItemCode(@Param("itemCode") String itemCode);

    /**
     * 根据项目名称查找缴费项目
     */
    @Query("SELECT f FROM FeeItem f WHERE f.itemName = :itemName AND f.deleted = 0")
    Optional<FeeItem> findByItemName(@Param("itemName") String itemName);

    /**
     * 根据费用类型查找缴费项目列表
     */
    @Query("SELECT f FROM FeeItem f WHERE f.feeType = :feeType AND f.deleted = 0 ORDER BY f.createdAt DESC")
    List<FeeItem> findByFeeType(@Param("feeType") String feeType);

    /**
     * 分页根据费用类型查找缴费项目列表
     */
    @Query("SELECT f FROM FeeItem f WHERE f.feeType = :feeType AND f.deleted = 0")
    Page<FeeItem> findByFeeType(@Param("feeType") String feeType, Pageable pageable);

    /**
     * 根据适用年级查找缴费项目列表
     */
    @Query("SELECT f FROM FeeItem f WHERE f.applicableGrade = :applicableGrade AND f.deleted = 0 ORDER BY f.createdAt DESC")
    List<FeeItem> findByApplicableGrade(@Param("applicableGrade") String applicableGrade);

    /**
     * 分页根据适用年级查找缴费项目列表
     */
    @Query("SELECT f FROM FeeItem f WHERE f.applicableGrade = :applicableGrade AND f.deleted = 0")
    Page<FeeItem> findByApplicableGrade(@Param("applicableGrade") String applicableGrade, Pageable pageable);

    /**
     * 查找启用状态的缴费项目列表
     */
    @Query("SELECT f FROM FeeItem f WHERE f.status = 1 AND f.deleted = 0 ORDER BY f.createdAt DESC")
    List<FeeItem> findActiveItems();

    /**
     * 分页查找启用状态的缴费项目列表
     */
    @Query("SELECT f FROM FeeItem f WHERE f.status = 1 AND f.deleted = 0")
    Page<FeeItem> findActiveItems(Pageable pageable);

    /**
     * 根据项目名称模糊查询
     */
    @Query("SELECT f FROM FeeItem f WHERE f.itemName LIKE %:itemName% AND f.deleted = 0 ORDER BY f.createdAt DESC")
    List<FeeItem> findByItemNameContaining(@Param("itemName") String itemName);



    /**
     * 根据截止日期范围查找缴费项目
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 缴费项目列表
     */
    @Query("SELECT f FROM FeeItem f WHERE f.dueDate BETWEEN :startDate AND :endDate AND f.deleted = 0 ORDER BY f.dueDate ASC")
    List<FeeItem> findByDueDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 根据金额范围查找缴费项目
     *
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @return 缴费项目列表
     */
    @Query("SELECT f FROM FeeItem f WHERE f.amount BETWEEN :minAmount AND :maxAmount AND f.deleted = 0 ORDER BY f.amount ASC")
    List<FeeItem> findByAmountBetween(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);

    /**
     * 分页查询缴费项目
     *
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 缴费项目列表
     */

    // @Select("SELECT * FROM tb_fee_item WHERE deleted = 0 ORDER BY created_time DESC LIMIT #{offset}, #{limit}")
    // List<FeeItem> findWithPagination(@Param("offset") int offset, @Param("limit") int limit);





    /**
     * 更新缴费项目状态
     *
     * @param id 项目ID
     * @param status 状态
     * @return 更新行数
     */
    @Query("UPDATE FeeItem f SET f.status = :status, f.updatedTime = CURRENT_TIMESTAMP WHERE f.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 软删除缴费项目
     *
     * @param id 项目ID
     * @return 更新行数
     */
    @Query("UPDATE FeeItem f SET f.deleted = 1, f.updatedTime = CURRENT_TIMESTAMP WHERE f.id = :id")
    int softDelete(@Param("id") Long id);

    /**
     * 批量软删除缴费项目
     *
     * @param ids 项目ID列表
     * @return 更新行数
     */
    @Query("UPDATE FeeItem f SET f.deleted = 1, f.updatedTime = CURRENT_TIMESTAMP WHERE f.id IN :ids")
    int batchSoftDelete(@Param("ids") List<Long> ids);

    /**
     * 批量更新缴费项目状态
     *
     * @param ids 项目ID列表
     * @param status 状态
     * @return 更新行数
     */
    @Query("UPDATE FeeItem f SET f.status = :status, f.updatedTime = CURRENT_TIMESTAMP WHERE f.id IN :ids")
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 根据关键词搜索缴费项目
     *
     * @param keyword 关键词
     * @return 缴费项目列表
     */
    @Query("""
        SELECT f FROM FeeItem f
        WHERE f.deleted = 0
        AND (f.itemName LIKE %:keyword%
             OR f.itemCode LIKE %:keyword%
             OR f.description LIKE %:keyword%)
        ORDER BY f.createdTime DESC
        """)
    List<FeeItem> searchByKeyword(@Param("keyword") String keyword);



    /**
     * 获取缴费项目详情（包含缴费统计）
     *
     * @param id 项目ID
     * @return 缴费项目详情
     */
    @Query("""
        SELECT f,
               COALESCE(COUNT(p.id), 0),
               COALESCE(SUM(p.amount), 0)
        FROM FeeItem f
        LEFT JOIN PaymentRecord p ON f.id = p.feeItemId AND p.deleted = 0 AND p.status = 1
        WHERE f.id = :id AND f.deleted = 0
        GROUP BY f.id
        """)
    Optional<Object[]> findDetailById(@Param("id") Long id);

    // ================================
    // 兼容性方法（为现有Service提供支持）
    // ================================

    /**
     * 根据项目编码查找缴费项目（兼容性方法）
     */
    default Optional<FeeItem> findByItemCodeAndDeleted(String itemCode, Integer deleted) {
        return findByItemCode(itemCode);
    }

    /**
     * 检查项目编码是否存在（兼容性方法）
     */
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM FeeItem f WHERE f.itemCode = :itemCode AND f.deleted = 0")
    boolean existsByItemCode(@Param("itemCode") String itemCode);

    /**
     * 检查项目编码是否存在（兼容性方法）
     */
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM FeeItem f WHERE f.itemCode = :itemCode AND f.deleted = :deleted")
    boolean existsByItemCodeAndDeleted(@Param("itemCode") String itemCode, @Param("deleted") Integer deleted);

    /**
     * 根据费用类型查找缴费项目列表（兼容性方法）
     */
    default List<FeeItem> findByFeeTypeAndDeletedOrderByCreatedTimeDesc(String feeType, Integer deleted) {
        return findByFeeType(feeType);
    }

    /**
     * 根据适用年级查找缴费项目列表（兼容性方法）
     */
    default List<FeeItem> findByApplicableGradeAndDeletedOrderByCreatedTimeDesc(String applicableGrade, Integer deleted) {
        return findByApplicableGrade(applicableGrade);
    }

    /**
     * 查找启用状态的缴费项目列表（兼容性方法）
     */
    default List<FeeItem> findByStatusAndDeletedOrderByCreatedTimeDesc(Integer status, Integer deleted) {
        return findActiveItems();
    }

    /**
     * 统计缴费项目总数（兼容性方法）
     */
    @Query("SELECT COUNT(f) FROM FeeItem f WHERE f.deleted != :deleted")
    long countByDeletedNot(@Param("deleted") Integer deleted);

    /**
     * 根据多条件搜索缴费项目（兼容性方法）
     */
    default List<FeeItem> searchFeeItems(String keyword, String feeType, Integer status) {
        return searchByKeyword(keyword);
    }

    /**
     * 缴费项目详情类
     */
    class FeeItemDetail {
        private FeeItem feeItem;
        private Long paymentCount;
        private BigDecimal totalPaid;

        public FeeItemDetail(FeeItem feeItem, Long paymentCount, BigDecimal totalPaid) {
            this.feeItem = feeItem;
            this.paymentCount = paymentCount;
            this.totalPaid = totalPaid;
        }

        // Getters
        public FeeItem getFeeItem() { return feeItem; }
        public Long getPaymentCount() { return paymentCount; }
        public BigDecimal getTotalPaid() { return totalPaid; }
    }

}
