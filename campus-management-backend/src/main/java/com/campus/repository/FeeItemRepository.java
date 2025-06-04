package com.campus.repository;

import com.campus.entity.FeeItem;
import org.apache.ibatis.annotations.*;
import org.springframework.data.jpa.repository.JpaRepository;
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
 * @since 2025-01-20
 */
@Repository
@Mapper
public interface FeeItemRepository extends JpaRepository<FeeItem, Long> {

    /**
     * 根据项目编码查找缴费项目
     *
     * @param itemCode 项目编码
     * @return 缴费项目
     */
    @Select("SELECT * FROM tb_fee_item WHERE item_code = #{itemCode} AND deleted = 0")
    Optional<FeeItem> findByItemCode(@Param("itemCode") String itemCode);

    /**
     * 检查项目编码是否存在
     *
     * @param itemCode 项目编码
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM tb_fee_item WHERE item_code = #{itemCode} AND deleted = 0")
    boolean existsByItemCode(@Param("itemCode") String itemCode);

    /**
     * 根据费用类型查找缴费项目列表
     *
     * @param feeType 费用类型
     * @return 缴费项目列表
     */
    @Select("SELECT * FROM tb_fee_item WHERE fee_type = #{feeType} AND deleted = 0 ORDER BY created_time DESC")
    List<FeeItem> findByFeeType(@Param("feeType") String feeType);

    /**
     * 根据适用年级查找缴费项目列表
     *
     * @param applicableGrade 适用年级
     * @return 缴费项目列表
     */
    @Select("SELECT * FROM tb_fee_item WHERE applicable_grade = #{applicableGrade} AND deleted = 0 ORDER BY created_time DESC")
    List<FeeItem> findByApplicableGrade(@Param("applicableGrade") String applicableGrade);

    /**
     * 查找启用状态的缴费项目列表
     *
     * @return 缴费项目列表
     */
    @Select("SELECT * FROM tb_fee_item WHERE status = 1 AND deleted = 0 ORDER BY created_time DESC")
    List<FeeItem> findByStatusAndDeleted(Integer status, Integer deleted);

    /**
     * 根据截止日期范围查找缴费项目
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 缴费项目列表
     */
    @Select("SELECT * FROM tb_fee_item WHERE due_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0 ORDER BY due_date ASC")
    List<FeeItem> findByDueDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 根据金额范围查找缴费项目
     *
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @return 缴费项目列表
     */
    @Select("SELECT * FROM tb_fee_item WHERE amount BETWEEN #{minAmount} AND #{maxAmount} AND deleted = 0 ORDER BY amount ASC")
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
    @Select("SELECT COUNT(*) FROM tb_fee_item WHERE deleted = 0")
    long countByDeletedNot(Integer deleted);

    /**
     * 根据条件搜索缴费项目
     *
     * @param keyword 关键词
     * @param feeType 费用类型
     * @param status 状态
     * @return 缴费项目列表
     */
    
    // @Select("""
    //     <script>
    //     SELECT * FROM tb_fee_item
    //     WHERE deleted = 0
    //     <if test="keyword != null and keyword != ''">
    //         AND (item_name LIKE CONCAT('%', #{keyword}, '%')
    //              OR item_code LIKE CONCAT('%', #{keyword}, '%')
    //              OR description LIKE CONCAT('%', #{keyword}, '%'))
    //     </if>
    //     <if test="feeType != null and feeType != ''">
    //         AND fee_type = #{feeType}
    //     </if>
    //     <if test="status != null">
    //         AND status = #{status}
    //     </if>
    //     ORDER BY created_time DESC
    //     </script>
    //     """)
    // List<FeeItem> searchFeeItems(@Param("keyword") String keyword,
    //                             @Param("feeType") String feeType,
    //                             @Param("status") Integer status);

    /**
     * 更新缴费项目状态
     *
     * @param id 项目ID
     * @param status 状态
     * @return 影响行数
     */
  
    // @Update("UPDATE tb_fee_item SET status = #{status}, updated_time = NOW() WHERE id = #{id} AND deleted = 0")
    // int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 软删除缴费项目
     *
     * @param id 项目ID
     * @return 影响行数
     */
   
    // @Update("UPDATE tb_fee_item SET deleted = 1, updated_time = NOW() WHERE id = #{id}")
    // int softDelete(@Param("id") Long id);

    /**
     * 批量软删除缴费项目
     *
     * @param ids 项目ID列表
     * @return 影响行数
     */
  
    // @Update("""
    //     <script>
    //     UPDATE tb_fee_item SET deleted = 1, updated_time = NOW()
    //     WHERE id IN
    //     <foreach collection="ids" item="id" open="(" separator="," close=")">
    //         #{id}
    //     </foreach>
    //     </script>
    //     """)
    // int batchSoftDelete(@Param("ids") List<Long> ids);

    /**
     * 缴费项目详情DTO
     */
    class FeeItemDetail {
        private Long id;
        private String itemName;
        private String itemCode;
        private BigDecimal amount;
        private String feeType;
        private String applicableGrade;
        private LocalDate dueDate;
        private String description;
        private Integer status;
        private Long paymentCount;
        private BigDecimal totalPaid;

        // Getter 和 Setter 方法
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        
        public String getItemCode() { return itemCode; }
        public void setItemCode(String itemCode) { this.itemCode = itemCode; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        public String getFeeType() { return feeType; }
        public void setFeeType(String feeType) { this.feeType = feeType; }
        
        public String getApplicableGrade() { return applicableGrade; }
        public void setApplicableGrade(String applicableGrade) { this.applicableGrade = applicableGrade; }
        
        public LocalDate getDueDate() { return dueDate; }
        public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        
        public Long getPaymentCount() { return paymentCount; }
        public void setPaymentCount(Long paymentCount) { this.paymentCount = paymentCount; }
        
        public BigDecimal getTotalPaid() { return totalPaid; }
        public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }
    }

    /**
     * 获取缴费项目详情（包含缴费统计）
     *
     * @param id 项目ID
     * @return 缴费项目详情
     */
    @Select("""
        SELECT f.*, 
               COALESCE(COUNT(p.id), 0) as payment_count,
               COALESCE(SUM(p.amount), 0) as total_paid
        FROM tb_fee_item f
        LEFT JOIN tb_payment_record p ON f.id = p.fee_item_id AND p.deleted = 0 AND p.status = 1
        WHERE f.id = #{id} AND f.deleted = 0
        GROUP BY f.id
        """)
    Optional<FeeItemDetail> findDetailById(@Param("id") Long id);
}
