package com.campus.repository;

import com.campus.entity.PaymentRecord;
import org.apache.ibatis.annotations.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 缴费记录数据访问层
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Repository
@Mapper
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {

    /**
     * 根据学生ID查找缴费记录
     *
     * @param studentId 学生ID
     * @return 缴费记录列表
     */
    @Select("SELECT * FROM tb_payment_record WHERE student_id = #{studentId} AND deleted = 0 ORDER BY payment_time DESC")
    List<PaymentRecord> findByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据缴费项目ID查找缴费记录
     *
     * @param feeItemId 缴费项目ID
     * @return 缴费记录列表
     */
    @Select("SELECT * FROM tb_payment_record WHERE fee_item_id = #{feeItemId} AND deleted = 0 ORDER BY payment_time DESC")
    List<PaymentRecord> findByFeeItemId(@Param("feeItemId") Long feeItemId);

    /**
     * 根据交易流水号查找缴费记录
     *
     * @param transactionNo 交易流水号
     * @return 缴费记录
     */
    @Select("SELECT * FROM tb_payment_record WHERE transaction_no = #{transactionNo} AND deleted = 0")
    Optional<PaymentRecord> findByTransactionNo(@Param("transactionNo") String transactionNo);

    /**
     * 根据缴费方式查找缴费记录
     *
     * @param paymentMethod 缴费方式
     * @return 缴费记录列表
     */
    @Select("SELECT * FROM tb_payment_record WHERE payment_method = #{paymentMethod} AND deleted = 0 ORDER BY payment_time DESC")
    List<PaymentRecord> findByPaymentMethod(@Param("paymentMethod") String paymentMethod);

    /**
     * 根据状态查找缴费记录
     *
     * @param status 状态
     * @return 缴费记录列表
     */
    @Select("SELECT * FROM tb_payment_record WHERE status = #{status} AND deleted = 0 ORDER BY payment_time DESC")
    List<PaymentRecord> findByStatus(@Param("status") Integer status);

    /**
     * 根据时间范围查找缴费记录
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 缴费记录列表
     */
    @Select("SELECT * FROM tb_payment_record WHERE payment_time BETWEEN #{startTime} AND #{endTime} AND deleted = 0 ORDER BY payment_time DESC")
    List<PaymentRecord> findByPaymentTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

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
    @Query(value = "SELECT COUNT(*) FROM tb_payment_record WHERE deleted = 0", nativeQuery = true)
    long countTotal();

    /**
     * 统计学生缴费总金额
     *
     * @param studentId 学生ID
     * @return 总金额
     */
   
    // @Select("SELECT COALESCE(SUM(amount), 0) FROM tb_payment_record WHERE student_id = #{studentId} AND status = 1 AND deleted = 0")
    // BigDecimal sumAmountByStudentId(@Param("studentId") Long studentId);

    /**
     * 统计缴费项目收费总金额
     *
     * @param feeItemId 缴费项目ID
     * @return 总金额
     */
    // TODO: 暂时注释掉，解决Spring Data JPA冲突问题
    // @Select("SELECT COALESCE(SUM(amount), 0) FROM tb_payment_record WHERE fee_item_id = #{feeItemId} AND status = 1 AND deleted = 0")
    // BigDecimal sumAmountByFeeItemId(@Param("feeItemId") Long feeItemId);

    /**
     * 统计时间范围内的缴费总金额
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 总金额
     */
  
    // @Select("SELECT COALESCE(SUM(amount), 0) FROM tb_payment_record WHERE payment_time BETWEEN #{startTime} AND #{endTime} AND status = 1 AND deleted = 0")
    // BigDecimal sumAmountByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 缴费记录详情DTO
     */
    class PaymentRecordDetail {
        private Long id;
        private Long studentId;
        private String studentName;
        private String studentNo;
        private Long feeItemId;
        private String itemName;
        private String itemCode;
        private BigDecimal amount;
        private String paymentMethod;
        private LocalDateTime paymentTime;
        private String transactionNo;
        private Long operatorId;
        private String operatorName;
        private String remarks;
        private Integer status;

        // Getter 和 Setter 方法
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        
        public String getStudentNo() { return studentNo; }
        public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
        
        public Long getFeeItemId() { return feeItemId; }
        public void setFeeItemId(Long feeItemId) { this.feeItemId = feeItemId; }
        
        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        
        public String getItemCode() { return itemCode; }
        public void setItemCode(String itemCode) { this.itemCode = itemCode; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        
        public LocalDateTime getPaymentTime() { return paymentTime; }
        public void setPaymentTime(LocalDateTime paymentTime) { this.paymentTime = paymentTime; }
        
        public String getTransactionNo() { return transactionNo; }
        public void setTransactionNo(String transactionNo) { this.transactionNo = transactionNo; }
        
        public Long getOperatorId() { return operatorId; }
        public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
        
        public String getOperatorName() { return operatorName; }
        public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
        
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
        
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
    }

    /**
     * 获取缴费记录详情（包含学生和缴费项目信息）
     *
     * @param id 记录ID
     * @return 缴费记录详情
     */
    @Select("""
        SELECT p.*, 
               u.real_name as student_name, s.student_no,
               f.item_name, f.item_code,
               o.real_name as operator_name
        FROM tb_payment_record p
        LEFT JOIN tb_student s ON p.student_id = s.id AND s.deleted = 0
        LEFT JOIN tb_user u ON s.user_id = u.id AND u.deleted = 0
        LEFT JOIN tb_fee_item f ON p.fee_item_id = f.id AND f.deleted = 0
        LEFT JOIN tb_user o ON p.operator_id = o.id AND o.deleted = 0
        WHERE p.id = #{id} AND p.deleted = 0
        """)
    Optional<PaymentRecordDetail> findDetailById(@Param("id") Long id);

    /**
     * 分页查询缴费记录详情
     *
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 缴费记录详情列表
     */

    // @Select("""
    //     SELECT p.*,
    //            u.real_name as student_name, s.student_no,
    //            f.item_name, f.item_code,
    //            o.real_name as operator_name
    //     FROM tb_payment_record p
    //     LEFT JOIN tb_student s ON p.student_id = s.id AND s.deleted = 0
    //     LEFT JOIN tb_user u ON s.user_id = u.id AND u.deleted = 0
    //     LEFT JOIN tb_fee_item f ON p.fee_item_id = f.id AND f.deleted = 0
    //     LEFT JOIN tb_user o ON p.operator_id = o.id AND o.deleted = 0
    //     WHERE p.deleted = 0
    //     ORDER BY p.payment_time DESC
    //     LIMIT #{offset}, #{limit}
    //     """)
    // List<PaymentRecordDetail> findDetailsWithPagination(@Param("offset") int offset, @Param("limit") int limit);
}
