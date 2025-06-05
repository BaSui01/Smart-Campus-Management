package com.campus.repository;

import com.campus.entity.FeeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 缴费项目数据访问层
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-05
 */
@Repository
public interface FeeItemRepository extends JpaRepository<FeeItem, Long> {

    /**
     * 根据项目编码查找缴费项目
     *
     * @param itemCode 项目编码
     * @return 缴费项目
     */
    Optional<FeeItem> findByItemCodeAndDeleted(String itemCode, Integer deleted);

    /**
     * 根据项目编码查找缴费项目（不考虑删除状态）
     *
     * @param itemCode 项目编码
     * @return 缴费项目
     */
    Optional<FeeItem> findByItemCode(String itemCode);

    /**
     * 检查项目编码是否存在
     *
     * @param itemCode 项目编码
     * @return 是否存在
     */
    boolean existsByItemCode(String itemCode);

    /**
     * 检查项目编码是否存在（包含删除状态）
     *
     * @param itemCode 项目编码
     * @param deleted 删除状态
     * @return 是否存在
     */
    boolean existsByItemCodeAndDeleted(String itemCode, Integer deleted);

    /**
     * 根据费用类型查找缴费项目列表
     *
     * @param feeType 费用类型
     * @return 缴费项目列表
     */
    List<FeeItem> findByFeeTypeAndDeletedOrderByCreatedTimeDesc(String feeType, Integer deleted);

    /**
     * 根据费用类型查找缴费项目列表（简化版本）
     *
     * @param feeType 费用类型
     * @return 缴费项目列表
     */
    List<FeeItem> findByFeeType(String feeType);

    /**
     * 根据适用年级查找缴费项目列表
     *
     * @param applicableGrade 适用年级
     * @return 缴费项目列表
     */
    List<FeeItem> findByApplicableGradeAndDeletedOrderByCreatedTimeDesc(String applicableGrade, Integer deleted);

    /**
     * 根据适用年级查找缴费项目列表（简化版本）
     *
     * @param applicableGrade 适用年级
     * @return 缴费项目列表
     */
    List<FeeItem> findByApplicableGrade(String applicableGrade);

    /**
     * 查找启用状态的缴费项目列表
     *
     * @return 缴费项目列表
     */
    List<FeeItem> findByStatusAndDeletedOrderByCreatedTimeDesc(Integer status, Integer deleted);

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
     * 统计缴费项目总数
     *
     * @return 总数
     */
    long countByDeletedNot(Integer deleted);

    /**
     * 查找活跃的缴费项目
     *
     * @return 缴费项目列表
     */
    @Query("SELECT f FROM FeeItem f WHERE f.status = 1 AND f.deleted = 0 ORDER BY f.createdTime DESC")
    List<FeeItem> findActiveItems();

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
     * 根据多条件搜索缴费项目
     *
     * @param keyword 关键词
     * @param feeType 费用类型
     * @param status 状态
     * @return 缴费项目列表
     */
    @Query("""
        SELECT f FROM FeeItem f
        WHERE f.deleted = 0
        AND (:keyword IS NULL OR f.itemName LIKE %:keyword% OR f.itemCode LIKE %:keyword% OR f.description LIKE %:keyword%)
        AND (:feeType IS NULL OR f.feeType = :feeType)
        AND (:status IS NULL OR f.status = :status)
        ORDER BY f.createdTime DESC
        """)
    List<FeeItem> searchFeeItems(@Param("keyword") String keyword,
                                @Param("feeType") String feeType,
                                @Param("status") Integer status);

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
