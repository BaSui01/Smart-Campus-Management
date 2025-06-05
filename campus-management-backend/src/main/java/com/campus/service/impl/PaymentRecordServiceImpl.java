package com.campus.service.impl;

import com.campus.entity.PaymentRecord;
import com.campus.entity.Student;
import com.campus.entity.FeeItem;
import com.campus.repository.PaymentRecordRepository;
import com.campus.repository.StudentRepository;
import com.campus.repository.FeeItemRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 缴费记录服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-05
 */
@Service
@Transactional
public class PaymentRecordServiceImpl implements PaymentRecordService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentRecordServiceImpl.class);

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FeeItemRepository feeItemRepository;

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

        paymentRecordRepository.save(paymentRecord);
        return paymentRecord;
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
        // 使用JPA实现分页
        return paymentRecordRepository.findAllActive(pageable);
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
        // 使用Repository中的优化方法
        return paymentRecordRepository.sumAmountByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumAmountByFeeItemId(Long feeItemId) {
        // 使用Repository中的优化方法
        return paymentRecordRepository.sumAmountByFeeItemId(feeItemId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumAmountByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        // 使用Repository中的优化方法
        return paymentRecordRepository.sumAmountByTimeRange(startTime, endTime);
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
        
        // 批量删除
        for (Long id : ids) {
            paymentRecordRepository.deleteById(id);
        }
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



    /**
     * 检查学生是否已缴费指定项目
     */
    @Transactional(readOnly = true)
    public boolean hasStudentPaidForFeeItem(Long studentId, Long feeItemId) {
        return paymentRecordRepository.existsByStudentIdAndFeeItemId(studentId, feeItemId);
    }

    /**
     * 统计学生在指定缴费项目的缴费次数
     */
    @Transactional(readOnly = true)
    public long countStudentPaymentsForFeeItem(Long studentId, Long feeItemId) {
        return paymentRecordRepository.countByStudentIdAndFeeItemId(studentId, feeItemId);
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
        // 实现基于真实数据的统计逻辑
        long totalRecords = paymentRecordRepository.countTotal();

        // 统计成功记录数（状态为1的记录）
        List<PaymentRecord> successRecordsList = paymentRecordRepository.findByStatus(1);
        long successRecords = successRecordsList.size();

        // 统计退款记录数（状态为2的记录）
        List<PaymentRecord> refundRecordsList = paymentRecordRepository.findByStatus(2);
        long refundRecords = refundRecordsList.size();

        // 统计失败记录数（状态为0的记录）
        List<PaymentRecord> failedRecordsList = paymentRecordRepository.findByStatus(0);
        long failedRecords = failedRecordsList.size();

        // 统计总金额
        List<PaymentRecord> allRecords = paymentRecordRepository.findAll();
        BigDecimal totalAmount = allRecords.stream()
            .map(PaymentRecord::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 统计成功缴费金额
        BigDecimal successAmount = successRecordsList.stream()
            .map(PaymentRecord::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 统计退款金额
        BigDecimal refundAmount = refundRecordsList.stream()
            .map(PaymentRecord::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PaymentStatistics(
            totalRecords,
            successRecords,
            refundRecords,
            failedRecords,
            totalAmount,
            successAmount,
            refundAmount
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentStatistics getStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        // 实现指定时间范围的统计逻辑
        List<PaymentRecord> timeRangeRecords = paymentRecordRepository.findByPaymentTimeBetween(startTime, endTime);

        long totalRecords = timeRangeRecords.size();

        // 按状态分类统计
        long successRecords = timeRangeRecords.stream()
            .filter(record -> record.getStatus() == 1)
            .count();

        long refundRecords = timeRangeRecords.stream()
            .filter(record -> record.getStatus() == 2)
            .count();

        long failedRecords = timeRangeRecords.stream()
            .filter(record -> record.getStatus() == 0)
            .count();

        // 统计金额
        BigDecimal totalAmount = timeRangeRecords.stream()
            .map(PaymentRecord::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal successAmount = timeRangeRecords.stream()
            .filter(record -> record.getStatus() == 1)
            .map(PaymentRecord::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal refundAmount = timeRangeRecords.stream()
            .filter(record -> record.getStatus() == 2)
            .map(PaymentRecord::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PaymentStatistics(
            totalRecords,
            successRecords,
            refundRecords,
            failedRecords,
            totalAmount,
            successAmount,
            refundAmount
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findUnpaidStudents() {
        logger.info("查找未缴费的学生");

        // 获取所有学生
        List<Student> allStudents = studentRepository.findAll();

        // 获取所有有效的缴费项目
        List<FeeItem> activeFeeItems = feeItemRepository.findByStatusAndDeletedOrderByCreatedTimeDesc(1, 0);

        // 筛选出有未缴费项目的学生
        return allStudents.stream()
            .filter(student -> hasUnpaidItems(student.getId(), activeFeeItems))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeeItem> getUnpaidFeeItems(Long studentId) {
        logger.info("获取学生未缴费项目: studentId={}", studentId);

        // 获取所有有效的缴费项目
        List<FeeItem> activeFeeItems = feeItemRepository.findByStatusAndDeletedOrderByCreatedTimeDesc(1, 0);

        // 筛选出学生未缴费的项目
        return activeFeeItems.stream()
            .filter(feeItem -> !hasStudentPaidForFeeItem(studentId, feeItem.getId()))
            .collect(Collectors.toList());
    }

    /**
     * 检查学生是否有未缴费项目
     */
    private boolean hasUnpaidItems(Long studentId, List<FeeItem> activeFeeItems) {
        return activeFeeItems.stream()
            .anyMatch(feeItem -> !hasStudentPaidForFeeItem(studentId, feeItem.getId()));
    }
}
