package com.campus.service;

import com.campus.entity.FeeItem;
import com.campus.repository.FeeItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 缴费项目服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-20
 */
public interface FeeItemService {

    /**
     * 创建缴费项目
     *
     * @param feeItem 缴费项目信息
     * @return 创建的缴费项目
     */
    FeeItem createFeeItem(FeeItem feeItem);

    /**
     * 更新缴费项目
     *
     * @param id 项目ID
     * @param feeItem 更新的缴费项目信息
     * @return 更新后的缴费项目
     */
    FeeItem updateFeeItem(Long id, FeeItem feeItem);

    /**
     * 根据ID查找缴费项目
     *
     * @param id 项目ID
     * @return 缴费项目
     */
    Optional<FeeItem> findById(Long id);

    /**
     * 根据项目编码查找缴费项目
     *
     * @param itemCode 项目编码
     * @return 缴费项目
     */
    Optional<FeeItem> findByItemCode(String itemCode);

    /**
     * 获取所有缴费项目
     *
     * @return 缴费项目列表
     */
    List<FeeItem> findAll();

    /**
     * 分页查询缴费项目
     *
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<FeeItem> findAll(Pageable pageable);

    /**
     * 根据费用类型查找缴费项目
     *
     * @param feeType 费用类型
     * @return 缴费项目列表
     */
    List<FeeItem> findByFeeType(String feeType);

    /**
     * 根据适用年级查找缴费项目
     *
     * @param applicableGrade 适用年级
     * @return 缴费项目列表
     */
    List<FeeItem> findByApplicableGrade(String applicableGrade);

    /**
     * 查找启用状态的缴费项目
     *
     * @return 缴费项目列表
     */
    List<FeeItem> findActiveItems();

    /**
     * 根据截止日期范围查找缴费项目
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 缴费项目列表
     */
    List<FeeItem> findByDueDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * 根据金额范围查找缴费项目
     *
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @return 缴费项目列表
     */
    List<FeeItem> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    /**
     * 搜索缴费项目
     *
     * @param keyword 关键词
     * @param feeType 费用类型
     * @param status 状态
     * @return 缴费项目列表
     */
    List<FeeItem> searchFeeItems(String keyword, String feeType, Integer status);

    /**
     * 更新缴费项目状态
     *
     * @param id 项目ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 删除缴费项目
     *
     * @param id 项目ID
     * @return 是否成功
     */
    boolean deleteFeeItem(Long id);

    /**
     * 批量删除缴费项目
     *
     * @param ids 项目ID列表
     * @return 是否成功
     */
    boolean batchDeleteFeeItems(List<Long> ids);

    /**
     * 检查项目编码是否存在
     *
     * @param itemCode 项目编码
     * @return 是否存在
     */
    boolean existsByItemCode(String itemCode);

    /**
     * 检查项目编码是否存在（排除指定ID）
     *
     * @param itemCode 项目编码
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    boolean existsByItemCodeAndIdNot(String itemCode, Long excludeId);

    /**
     * 获取缴费项目详情（包含缴费统计）
     *
     * @param id 项目ID
     * @return 缴费项目详情
     */
    Optional<FeeItemRepository.FeeItemDetail> findDetailById(Long id);

    /**
     * 缴费项目统计信息
     */
    class FeeItemStatistics {
        private long totalItems;
        private long activeItems;
        private long inactiveItems;
        private BigDecimal totalAmount;
        private BigDecimal totalPaid;

        public FeeItemStatistics() {}

        public FeeItemStatistics(long totalItems, long activeItems, long inactiveItems, 
                               BigDecimal totalAmount, BigDecimal totalPaid) {
            this.totalItems = totalItems;
            this.activeItems = activeItems;
            this.inactiveItems = inactiveItems;
            this.totalAmount = totalAmount;
            this.totalPaid = totalPaid;
        }

        // Getter 和 Setter 方法
        public long getTotalItems() { return totalItems; }
        public void setTotalItems(long totalItems) { this.totalItems = totalItems; }

        public long getActiveItems() { return activeItems; }
        public void setActiveItems(long activeItems) { this.activeItems = activeItems; }

        public long getInactiveItems() { return inactiveItems; }
        public void setInactiveItems(long inactiveItems) { this.inactiveItems = inactiveItems; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public BigDecimal getTotalPaid() { return totalPaid; }
        public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }
    }

    /**
     * 获取缴费项目统计信息
     *
     * @return 统计信息
     */
    FeeItemStatistics getStatistics();

    /**
     * 生成项目编码
     *
     * @param feeType 费用类型
     * @return 项目编码
     */
    String generateItemCode(String feeType);
}
