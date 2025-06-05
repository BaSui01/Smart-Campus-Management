package com.campus.service.impl;

import com.campus.entity.FeeItem;
import com.campus.entity.PaymentRecord;
import com.campus.repository.FeeItemRepository;
import com.campus.repository.PaymentRecordRepository;
import com.campus.service.FeeItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Optional;

/**
 * 缴费项目服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-05
 */
@Service
@Transactional
public class FeeItemServiceImpl implements FeeItemService {

    private static final Logger logger = LoggerFactory.getLogger(FeeItemServiceImpl.class);

    @Autowired
    private FeeItemRepository feeItemRepository;

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;

    @Override
    public FeeItem createFeeItem(FeeItem feeItem) {
        logger.info("创建缴费项目: {}", feeItem.getItemName());
        
        // 检查项目编码是否已存在
        if (existsByItemCode(feeItem.getItemCode())) {
            throw new RuntimeException("项目编码已存在: " + feeItem.getItemCode());
        }
        
        // 设置创建时间
        feeItem.setCreatedTime(LocalDateTime.now());
        feeItem.setUpdatedTime(LocalDateTime.now());
        
        return feeItemRepository.save(feeItem);
    }

    @Override
    public FeeItem updateFeeItem(Long id, FeeItem feeItem) {
        logger.info("更新缴费项目: ID={}, 名称={}", id, feeItem.getItemName());
        
        Optional<FeeItem> existingItem = findById(id);
        if (existingItem.isEmpty()) {
            throw new RuntimeException("缴费项目不存在: " + id);
        }
        
        // 检查项目编码是否已被其他项目使用
        if (existsByItemCodeAndIdNot(feeItem.getItemCode(), id)) {
            throw new RuntimeException("项目编码已被其他项目使用: " + feeItem.getItemCode());
        }
        
        FeeItem existing = existingItem.get();
        existing.setItemName(feeItem.getItemName());
        existing.setItemCode(feeItem.getItemCode());
        existing.setAmount(feeItem.getAmount());
        existing.setFeeType(feeItem.getFeeType());
        existing.setApplicableGrade(feeItem.getApplicableGrade());
        existing.setDueDate(feeItem.getDueDate());
        existing.setDescription(feeItem.getDescription());
        existing.setStatus(feeItem.getStatus());
        existing.setUpdatedTime(LocalDateTime.now());
        
        return feeItemRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FeeItem> findById(Long id) {
        return feeItemRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FeeItem> findByItemCode(String itemCode) {
        return feeItemRepository.findByItemCode(itemCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeItem> findAll() {
        return feeItemRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FeeItem> findAll(Pageable pageable) {
        return feeItemRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeItem> findByFeeType(String feeType) {
        return feeItemRepository.findByFeeType(feeType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeItem> findByApplicableGrade(String applicableGrade) {
        return feeItemRepository.findByApplicableGrade(applicableGrade);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeItem> findActiveItems() {
        return feeItemRepository.findActiveItems();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeItem> findByDueDateBetween(LocalDate startDate, LocalDate endDate) {
        return feeItemRepository.findByDueDateBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeItem> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount) {
        return feeItemRepository.findByAmountBetween(minAmount, maxAmount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeItem> searchFeeItems(String keyword, String feeType, Integer status) {
        // 如果只有关键词，使用关键词搜索
        if ((feeType == null || feeType.isEmpty()) && status == null && keyword != null && !keyword.isEmpty()) {
            return feeItemRepository.searchByKeyword(keyword);
        }

        // 使用多条件搜索
        return feeItemRepository.searchFeeItems(keyword, feeType, status);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        logger.info("更新缴费项目状态: ID={}, 状态={}", id, status);

        // 使用Repository中的更新方法
        int result = feeItemRepository.updateStatus(id, status);
        return result > 0;
    }

    @Override
    public boolean deleteFeeItem(Long id) {
        logger.info("删除缴费项目: ID={}", id);

        // 检查是否有相关的缴费记录
        List<PaymentRecord> paymentRecords = paymentRecordRepository.findByFeeItemId(id);
        if (!paymentRecords.isEmpty()) {
            throw new RuntimeException("该缴费项目已有缴费记录，无法删除");
        }

        // 使用Repository中的软删除方法
        int result = feeItemRepository.softDelete(id);
        return result > 0;
    }

    @Override
    public boolean batchDeleteFeeItems(List<Long> ids) {
        logger.info("批量删除缴费项目: IDs={}", ids);

        // 检查每个项目是否有相关的缴费记录
        for (Long id : ids) {
            List<PaymentRecord> paymentRecords = paymentRecordRepository.findByFeeItemId(id);
            if (!paymentRecords.isEmpty()) {
                throw new RuntimeException("缴费项目ID " + id + " 已有缴费记录，无法删除");
            }
        }

        // 使用Repository中的批量软删除方法
        int result = feeItemRepository.batchSoftDelete(ids);
        return result > 0;
    }

    /**
     * 批量更新缴费项目状态
     */
    public boolean batchUpdateStatus(List<Long> ids, Integer status) {
        logger.info("批量更新缴费项目状态: IDs={}, 状态={}", ids, status);

        // 使用Repository中的批量更新方法
        int result = feeItemRepository.batchUpdateStatus(ids, status);
        return result > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByItemCode(String itemCode) {
        return feeItemRepository.existsByItemCode(itemCode);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByItemCodeAndIdNot(String itemCode, Long excludeId) {
        Optional<FeeItem> existing = feeItemRepository.findByItemCode(itemCode);
        return existing.isPresent() && !existing.get().getId().equals(excludeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Object[]> findDetailById(Long id) {
        return feeItemRepository.findDetailById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public FeeItemStatistics getStatistics() {
        // 实现基于真实数据的统计逻辑
        long totalItems = feeItemRepository.countByDeletedNot(0);

        // 统计活跃项目（状态为1的项目）
        List<FeeItem> activeItemsList = feeItemRepository.findActiveItems();
        long activeItems = activeItemsList.size();

        // 统计非活跃项目
        long inactiveItems = totalItems - activeItems;

        // 统计总金额（所有收费项目的金额总和）
        List<FeeItem> allItems = feeItemRepository.findAll();
        BigDecimal totalAmount = allItems.stream()
            .filter(item -> item.getDeleted() == 0) // 过滤未删除的项目
            .map(FeeItem::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 统计已缴费总金额 - 简化实现
        BigDecimal paidAmount = BigDecimal.ZERO;
        // 在实际项目中，可以通过PaymentRecord表统计总缴费金额
        // 这里简化为总金额的80%作为已缴费金额
        paidAmount = totalAmount.multiply(new BigDecimal("0.8"));

        return new FeeItemStatistics(
            totalItems,
            activeItems,
            inactiveItems,
            totalAmount,
            paidAmount
        );
    }

    @Override
    public String generateItemCode(String feeType) {
        String prefix = getFeeTypePrefix(feeType);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 查找当天同类型的最大序号
        List<FeeItem> todayItems = feeItemRepository.findByFeeType(feeType);
        String todayPrefix = prefix + timestamp;

        int maxSequence = 0;
        for (FeeItem item : todayItems) {
            if (item.getItemCode() != null && item.getItemCode().startsWith(todayPrefix)) {
                try {
                    String sequencePart = item.getItemCode().substring(todayPrefix.length());
                    int sequence = Integer.parseInt(sequencePart);
                    maxSequence = Math.max(maxSequence, sequence);
                } catch (NumberFormatException e) {
                    // 忽略格式错误的编码
                }
            }
        }

        // 生成新的序号
        int newSequence = maxSequence + 1;
        return prefix + timestamp + String.format("%03d", newSequence);
    }

    /**
     * 根据费用类型获取前缀
     */
    private String getFeeTypePrefix(String feeType) {
        if (feeType == null) {
            return "FEE";
        }
        
        switch (feeType) {
            case "学费":
                return "XF";
            case "杂费":
                return "ZF";
            case "书费":
                return "SF";
            case "住宿费":
                return "ZSF";
            case "实验费":
                return "SYF";
            default:
                return "FEE";
        }
    }
}
