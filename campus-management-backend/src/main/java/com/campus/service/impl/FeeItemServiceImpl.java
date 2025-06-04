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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 缴费项目服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-20
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
        return feeItemRepository.findByStatusAndDeleted(1, 0);
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
     
        return new ArrayList<>();
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        logger.info("更新缴费项目状态: ID={}, 状态={}", id, status);
       
        Optional<FeeItem> optionalFeeItem = feeItemRepository.findById(id);
        if (optionalFeeItem.isPresent()) {
            FeeItem feeItem = optionalFeeItem.get();
            feeItem.setStatus(status);
            feeItem.setUpdatedTime(LocalDateTime.now());
            feeItemRepository.save(feeItem);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteFeeItem(Long id) {
        logger.info("删除缴费项目: ID={}", id);
        
        // 检查是否有相关的缴费记录
        List<PaymentRecord> paymentRecords = paymentRecordRepository.findByFeeItemId(id);
        if (!paymentRecords.isEmpty()) {
            throw new RuntimeException("该缴费项目已有缴费记录，无法删除");
        }
        
        
        Optional<FeeItem> optionalFeeItem = feeItemRepository.findById(id);
        if (optionalFeeItem.isPresent()) {
            FeeItem feeItem = optionalFeeItem.get();
            feeItem.setDeleted(1);
            feeItem.setUpdatedTime(LocalDateTime.now());
            feeItemRepository.save(feeItem);
            return true;
        }
        return false;
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
       
        int result = 0;
        for (Long id : ids) {
            Optional<FeeItem> optionalFeeItem = feeItemRepository.findById(id);
            if (optionalFeeItem.isPresent()) {
                FeeItem feeItem = optionalFeeItem.get();
                feeItem.setDeleted(1);
                feeItem.setUpdatedTime(LocalDateTime.now());
                feeItemRepository.save(feeItem);
                result++;
            }
        }
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
    public Optional<FeeItemRepository.FeeItemDetail> findDetailById(Long id) {
        return feeItemRepository.findDetailById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public FeeItemStatistics getStatistics() {
        // 这里需要实现统计逻辑，暂时返回模拟数据
        return new FeeItemStatistics(
            feeItemRepository.countByDeletedNot(0),
            0L, // 需要实现活跃项目统计
            0L, // 需要实现非活跃项目统计
            BigDecimal.ZERO, // 需要实现总金额统计
            BigDecimal.ZERO  // 需要实现已缴费总金额统计
        );
    }

    @Override
    public String generateItemCode(String feeType) {
        String prefix = getFeeTypePrefix(feeType);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 查找当天同类型的最大序号
        // 这里需要实现查找逻辑，暂时返回简单的编码
        return prefix + timestamp + "001";
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
