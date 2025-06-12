package com.campus.domain.repository.finance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campus.domain.entity.finance.PaymentRecord;
import com.campus.domain.repository.infrastructure.BaseRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 缴费记录Repository接口
 * 提供缴费记录相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface PaymentRecordRepository extends BaseRepository<PaymentRecord> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据学生ID查找缴费记录
     */
    @Query("SELECT p FROM PaymentRecord p WHERE p.studentId = :studentId AND p.deleted = 0 ORDER BY p.paymentTime DESC")
    List<PaymentRecord> findByStudentId(@Param("studentId") Long studentId);

    /**
     * 分页根据学生ID查找缴费记录
     */
    @Query("SELECT p FROM PaymentRecord p WHERE p.studentId = :studentId AND p.deleted = 0")
    Page<PaymentRecord> findByStudentId(@Param("studentId") Long studentId, Pageable pageable);

    /**
     * 根据缴费项目ID查找缴费记录
     */
    @Query("SELECT p FROM PaymentRecord p WHERE p.feeItemId = :feeItemId AND p.deleted = 0 ORDER BY p.paymentTime DESC")
    List<PaymentRecord> findByFeeItemId(@Param("feeItemId") Long feeItemId);

    /**
     * 分页根据缴费项目ID查找缴费记录
     */
    @Query("SELECT p FROM PaymentRecord p WHERE p.feeItemId = :feeItemId AND p.deleted = 0")
    Page<PaymentRecord> findByFeeItemId(@Param("feeItemId") Long feeItemId, Pageable pageable);

    /**
     * 根据交易流水号查找缴费记录
     */
    @Query("SELECT p FROM PaymentRecord p WHERE p.transactionNo = :transactionNo AND p.deleted = 0")
    Optional<PaymentRecord> findByTransactionNo(@Param("transactionNo") String transactionNo);

    /**
     * 根据缴费方式查找缴费记录
     */
    @Query("SELECT p FROM PaymentRecord p WHERE p.paymentMethod = :paymentMethod AND p.deleted = 0 ORDER BY p.paymentTime DESC")
    List<PaymentRecord> findByPaymentMethod(@Param("paymentMethod") String paymentMethod);

    /**
     * 分页根据缴费方式查找缴费记录
     */
    @Query("SELECT p FROM PaymentRecord p WHERE p.paymentMethod = :paymentMethod AND p.deleted = 0")
    Page<PaymentRecord> findByPaymentMethod(@Param("paymentMethod") String paymentMethod, Pageable pageable);

    /**
     * 根据缴费状态查找缴费记录
     */
    @Query("SELECT p FROM PaymentRecord p WHERE p.paymentStatus = :status AND p.deleted = 0 ORDER BY p.paymentTime DESC")
    List<PaymentRecord> findByPaymentStatus(@Param("status") String paymentStatus);

    /**
     * 根据金额范围查找缴费记录
     */
    @Query("SELECT p FROM PaymentRecord p WHERE p.amount BETWEEN :minAmount AND :maxAmount AND p.deleted = 0 ORDER BY p.paymentTime DESC")
    List<PaymentRecord> findByAmountBetween(@Param("minAmount") BigDecimal minAmount, @Param("maxAmount") BigDecimal maxAmount);

    /**
     * 根据时间范围查找缴费记录
     */
    @Query("SELECT p FROM PaymentRecord p WHERE p.paymentTime BETWEEN :startTime AND :endTime AND p.deleted = 0 ORDER BY p.paymentTime DESC")
    List<PaymentRecord> findByPaymentTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 分页根据时间范围查找缴费记录
     */
    @Query("SELECT p FROM PaymentRecord p WHERE p.paymentTime BETWEEN :startTime AND :endTime AND p.deleted = 0")
    Page<PaymentRecord> findByPaymentTimeBetween(@Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime,
                                                Pageable pageable);

    /**
     * 分页查询缴费记录
     *
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 缴费记录列表
     */
  
    // @Select("SELECT * FROM tb_payment_record WHERE deleted = 0 ORDER BY payment_time DESC LIMIT #{offset}, #{limit}")
    // List<PaymentRecord> findWithPagination(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 统计缴费记录总数
     *
     * @return 总数
     */
    @Query("SELECT COUNT(p) FROM PaymentRecord p WHERE p.deleted = 0")
    long countTotal();

    /**
     * 统计学生缴费总金额
     *
     * @param studentId 学生ID
     * @return 总金额
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentRecord p WHERE p.studentId = :studentId AND p.status = 1 AND p.deleted = 0")
    BigDecimal sumAmountByStudentId(@Param("studentId") Long studentId);

    /**
     * 统计缴费项目收费总金额
     *
     * @param feeItemId 缴费项目ID
     * @return 总金额
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentRecord p WHERE p.feeItemId = :feeItemId AND p.status = 1 AND p.deleted = 0")
    BigDecimal sumAmountByFeeItemId(@Param("feeItemId") Long feeItemId);

    /**
     * 统计时间范围内的缴费总金额
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 总金额
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentRecord p WHERE p.paymentTime BETWEEN :startTime AND :endTime AND p.status = 1 AND p.deleted = 0")
    BigDecimal sumAmountByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计学生在指定缴费项目的缴费次数
     *
     * @param studentId 学生ID
     * @param feeItemId 缴费项目ID
     * @return 缴费次数
     */
    @Query("SELECT COUNT(p) FROM PaymentRecord p WHERE p.studentId = :studentId AND p.feeItemId = :feeItemId AND p.status = 1 AND p.deleted = 0")
    long countByStudentIdAndFeeItemId(@Param("studentId") Long studentId, @Param("feeItemId") Long feeItemId);

    /**
     * 检查学生是否已缴费指定项目
     *
     * @param studentId 学生ID
     * @param feeItemId 缴费项目ID
     * @return 是否已缴费
     */
    @Query("SELECT COUNT(p) > 0 FROM PaymentRecord p WHERE p.studentId = :studentId AND p.feeItemId = :feeItemId AND p.status = 1 AND p.deleted = 0")
    boolean existsByStudentIdAndFeeItemId(@Param("studentId") Long studentId, @Param("feeItemId") Long feeItemId);

    /**
     * 查找未缴费的学生
     *
     * @return 未缴费学生列表
     */
    @Query(value = """
        SELECT DISTINCT s.* FROM tb_student s
        LEFT JOIN tb_payment_record p ON s.id = p.student_id AND p.deleted = 0 AND p.status = 1
        WHERE s.deleted = 0 AND p.id IS NULL
        """, nativeQuery = true)
    List<Object[]> findUnpaidStudents();

    /**
     * 根据学生ID查找未缴费项目
     *
     * @param studentId 学生ID
     * @return 未缴费项目列表
     */
    @Query(value = """
        SELECT f.* FROM tb_fee_item f
        WHERE f.deleted = 0 AND f.status = 1
        AND NOT EXISTS (
            SELECT 1 FROM tb_payment_record p
            WHERE p.student_id = :studentId AND p.fee_item_id = f.id
            AND p.deleted = 0 AND p.status = 1
        )
        """, nativeQuery = true)
    List<Object[]> findUnpaidFeeItemsByStudentId(@Param("studentId") Long studentId);

    // ================================
    // 兼容性方法（为现有Service提供支持）
    // ================================

    /**
     * 查找所有未删除的缴费记录（兼容性方法）
     */
    @Query("SELECT p FROM PaymentRecord p WHERE p.deleted = 0 ORDER BY p.paymentTime DESC")
    List<PaymentRecord> findAllActive();

    /**
     * 分页查询所有未删除的缴费记录（兼容性方法）
     */
    @Query("SELECT p FROM PaymentRecord p WHERE p.deleted = 0 ORDER BY p.paymentTime DESC")
    Page<PaymentRecord> findAllActive(Pageable pageable);

    /**
     * 根据状态查找缴费记录（兼容性方法）
     */
    default List<PaymentRecord> findByStatus(Integer status) {
        return findByPaymentStatus(String.valueOf(status));
    }



}
