package com.campus.service.impl;

import com.campus.entity.PaymentRecord;
import com.campus.repository.PaymentRecordRepository;
import com.campus.service.PaymentRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 缴费记录服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Service
@Transactional
public class PaymentRecordServiceImpl implements PaymentRecordService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentRecordServiceImpl.class);

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;

    @Override
    public PaymentRecord createPaymentRecord(PaymentRecord paymentRecord) {
        logger.info("创建缴费记录: 学生ID={}, 项目ID={}, 金额={}", 
                   paymentRecord.getStudentId(), paymentRecord.getFeeItemId(), paymentRecord.getAmount());
        
        // 生成交易流水号
        if (paymentRecord.getTransactionNo() == null || paymentRecord.getTransactionNo().isEmpty()) {
            paymentRecord.setTransactionNo(generateTransactionNo());
        }
        
        // 设置缴费时间
        if (paymentRecord.getPaymentTime() == null) {
            paymentRecord.setPaymentTime(LocalDateTime.now());
        }
        
        // 设置创建时间
        paymentRecord.setCreatedTime(LocalDateTime.now());
        paymentRecord.setUpdatedTime(LocalDateTime.now());
        
        return paymentRecordRepository.save(paymentRecord);
    }

    @Override
    public PaymentRecord updatePaymentRecord(Long id, PaymentRecord paymentRecord) {
        logger.info("更新缴费记录: ID={}", id);
        
        Optional<PaymentRecord> existingRecord = findById(id);
        if (existingRecord.isEmpty()) {
            throw new RuntimeException("缴费记录不存在: " + id);
        }
        
        PaymentRecord existing = existingRecord.get();
        existing.setAmount(paymentRecord.getAmount());
        existing.setPaymentMethod(paymentRecord.getPaymentMethod());
        existing.setRemarks(paymentRecord.getRemarks());
        existing.setStatus(paymentRecord.getStatus());
        existing.setUpdatedTime(LocalDateTime.now());
        
        return paymentRecordRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentRecord> findById(Long id) {
        return paymentRecordRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentRecord> findByTransactionNo(String transactionNo) {
        return paymentRecordRepository.findByTransactionNo(transactionNo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentRecord> findAll() {
        return paymentRecordRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentRecord> findAll(Pageable pageable) {
        return paymentRecordRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentRecord> findByStudentId(Long studentId) {
        return paymentRecordRepository.findByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentRecord> findByFeeItemId(Long feeItemId) {
        return paymentRecordRepository.findByFeeItemId(feeItemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentRecord> findByPaymentMethod(String paymentMethod) {
        return paymentRecordRepository.findByPaymentMethod(paymentMethod);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentRecord> findByStatus(Integer status) {
        return paymentRecordRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentRecord> findByPaymentTimeBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return paymentRecordRepository.findByPaymentTimeBetween(startTime, endTime);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumAmountByStudentId(Long studentId) {
  
        return BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumAmountByFeeItemId(Long feeItemId) {
        // TODO: 暂时返回0，后续实现统计功能
        return BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumAmountByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return BigDecimal.ZERO;
    }

    @Override
    public boolean deletePaymentRecord(Long id) {
        logger.info("删除缴费记录: ID={}", id);
        
        Optional<PaymentRecord> record = findById(id);
        if (record.isEmpty()) {
            return false;
        }
        
        // 只有失败状态的记录才能删除
        if (record.get().getStatus() == 1) {
            throw new RuntimeException("成功的缴费记录不能删除，请使用退款功能");
        }
        
        paymentRecordRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean batchDeletePaymentRecords(List<Long> ids) {
        logger.info("批量删除缴费记录: IDs={}", ids);
        
        // 检查每个记录的状态
        for (Long id : ids) {
            Optional<PaymentRecord> record = findById(id);
            if (record.isPresent() && record.get().getStatus() == 1) {
                throw new RuntimeException("记录ID " + id + " 是成功的缴费记录，不能删除");
            }
        }
        
        paymentRecordRepository.deleteAllById(ids);
        return true;
    }

    @Override
    public boolean refundPayment(Long id, String refundReason, Long operatorId) {
        logger.info("退款处理: ID={}, 操作员={}", id, operatorId);
        
        Optional<PaymentRecord> record = findById(id);
        if (record.isEmpty()) {
            throw new RuntimeException("缴费记录不存在: " + id);
        }
        
        PaymentRecord paymentRecord = record.get();
        if (paymentRecord.getStatus() != 1) {
            throw new RuntimeException("只有成功的缴费记录才能退款");
        }
        
        // 更新状态为退款
        paymentRecord.setStatus(2);
        paymentRecord.setRemarks(paymentRecord.getRemarks() + " [退款原因: " + refundReason + "]");
        paymentRecord.setUpdatedTime(LocalDateTime.now());
        
        paymentRecordRepository.save(paymentRecord);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentRecordRepository.PaymentRecordDetail> findDetailById(Long id) {
        return paymentRecordRepository.findDetailById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentRecordRepository.PaymentRecordDetail> findDetailsWithPagination(int offset, int limit) {
     
        return new ArrayList<>();
    }

    @Override
    public String generateTransactionNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "PAY" + timestamp + uuid;
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentStatistics getStatistics() {
        // 这里需要实现统计逻辑，暂时返回模拟数据
        long totalRecords = paymentRecordRepository.countTotal();
        return new PaymentStatistics(
            totalRecords,
            0L, // 需要实现成功记录统计
            0L, // 需要实现退款记录统计
            0L, // 需要实现失败记录统计
            BigDecimal.ZERO, // 需要实现总金额统计
            BigDecimal.ZERO, // 需要实现成功金额统计
            BigDecimal.ZERO  // 需要实现退款金额统计
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentStatistics getStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        // 这里需要实现指定时间范围的统计逻辑
        BigDecimal totalAmount = sumAmountByTimeRange(startTime, endTime);
        return new PaymentStatistics(
            0L, // 需要实现记录数统计
            0L, // 需要实现成功记录统计
            0L, // 需要实现退款记录统计
            0L, // 需要实现失败记录统计
            totalAmount,
            BigDecimal.ZERO, // 需要实现成功金额统计
            BigDecimal.ZERO  // 需要实现退款金额统计
        );
    }
}
