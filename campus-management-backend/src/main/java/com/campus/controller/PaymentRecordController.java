package com.campus.controller;

import com.campus.common.ApiResponse;
import com.campus.entity.PaymentRecord;
import com.campus.service.PaymentRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 缴费记录管理控制器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-05
 */
@RestController
@RequestMapping("/api/payment-records")
@Tag(name = "缴费记录管理", description = "缴费记录的增删改查操作")
public class PaymentRecordController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentRecordController.class);

    @Autowired
    private PaymentRecordService paymentRecordService;

    /**
     * 创建缴费记录
     */
    @PostMapping
    @Operation(summary = "创建缴费记录", description = "创建新的缴费记录")
    public ResponseEntity<ApiResponse<PaymentRecord>> createPaymentRecord(@Valid @RequestBody PaymentRecord paymentRecord) {
        try {
            logger.info("创建缴费记录请求: 学生ID={}, 项目ID={}", paymentRecord.getStudentId(), paymentRecord.getFeeItemId());
            PaymentRecord created = paymentRecordService.createPaymentRecord(paymentRecord);
            return ResponseEntity.ok(ApiResponse.success("缴费记录创建成功", created));
        } catch (Exception e) {
            logger.error("创建缴费记录失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 更新缴费记录
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新缴费记录", description = "根据ID更新缴费记录信息")
    public ResponseEntity<ApiResponse<PaymentRecord>> updatePaymentRecord(
            @Parameter(description = "缴费记录ID") @PathVariable Long id,
            @Valid @RequestBody PaymentRecord paymentRecord) {
        try {
            logger.info("更新缴费记录请求: ID={}", id);
            PaymentRecord updated = paymentRecordService.updatePaymentRecord(id, paymentRecord);
            return ResponseEntity.ok(ApiResponse.success("缴费记录更新成功", updated));
        } catch (Exception e) {
            logger.error("更新缴费记录失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据ID查询缴费记录
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询缴费记录", description = "根据ID查询缴费记录详情")
    public ResponseEntity<ApiResponse<PaymentRecord>> getPaymentRecord(
            @Parameter(description = "缴费记录ID") @PathVariable Long id) {
        try {
            Optional<PaymentRecord> paymentRecord = paymentRecordService.findById(id);
            if (paymentRecord.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(paymentRecord.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("查询缴费记录失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 分页查询缴费记录
     */
    @GetMapping
    @Operation(summary = "分页查询缴费记录", description = "分页查询缴费记录列表")
    public ResponseEntity<ApiResponse<Page<PaymentRecord>>> getPaymentRecords(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "paymentTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<PaymentRecord> paymentRecords = paymentRecordService.findAll(pageable);
            return ResponseEntity.ok(ApiResponse.success(paymentRecords));
        } catch (Exception e) {
            logger.error("分页查询缴费记录失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据学生ID查询缴费记录
     */
    @GetMapping("/student/{studentId}")
    @Operation(summary = "按学生查询", description = "根据学生ID查询缴费记录")
    public ResponseEntity<ApiResponse<List<PaymentRecord>>> getPaymentRecordsByStudent(
            @Parameter(description = "学生ID") @PathVariable Long studentId) {
        try {
            List<PaymentRecord> paymentRecords = paymentRecordService.findByStudentId(studentId);
            return ResponseEntity.ok(ApiResponse.success(paymentRecords));
        } catch (Exception e) {
            logger.error("按学生查询缴费记录失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据缴费项目ID查询缴费记录
     */
    @GetMapping("/fee-item/{feeItemId}")
    @Operation(summary = "按缴费项目查询", description = "根据缴费项目ID查询缴费记录")
    public ResponseEntity<ApiResponse<List<PaymentRecord>>> getPaymentRecordsByFeeItem(
            @Parameter(description = "缴费项目ID") @PathVariable Long feeItemId) {
        try {
            List<PaymentRecord> paymentRecords = paymentRecordService.findByFeeItemId(feeItemId);
            return ResponseEntity.ok(ApiResponse.success(paymentRecords));
        } catch (Exception e) {
            logger.error("按缴费项目查询缴费记录失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据缴费方式查询缴费记录
     */
    @GetMapping("/payment-method/{paymentMethod}")
    @Operation(summary = "按缴费方式查询", description = "根据缴费方式查询缴费记录")
    public ResponseEntity<ApiResponse<List<PaymentRecord>>> getPaymentRecordsByMethod(
            @Parameter(description = "缴费方式") @PathVariable String paymentMethod) {
        try {
            List<PaymentRecord> paymentRecords = paymentRecordService.findByPaymentMethod(paymentMethod);
            return ResponseEntity.ok(ApiResponse.success(paymentRecords));
        } catch (Exception e) {
            logger.error("按缴费方式查询缴费记录失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据状态查询缴费记录
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "按状态查询", description = "根据状态查询缴费记录")
    public ResponseEntity<ApiResponse<List<PaymentRecord>>> getPaymentRecordsByStatus(
            @Parameter(description = "状态") @PathVariable Integer status) {
        try {
            List<PaymentRecord> paymentRecords = paymentRecordService.findByStatus(status);
            return ResponseEntity.ok(ApiResponse.success(paymentRecords));
        } catch (Exception e) {
            logger.error("按状态查询缴费记录失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据时间范围查询缴费记录
     */
    @GetMapping("/by-time")
    @Operation(summary = "按时间范围查询", description = "根据时间范围查询缴费记录")
    public ResponseEntity<ApiResponse<List<PaymentRecord>>> getPaymentRecordsByTime(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            List<PaymentRecord> paymentRecords = paymentRecordService.findByPaymentTimeBetween(startTime, endTime);
            return ResponseEntity.ok(ApiResponse.success(paymentRecords));
        } catch (Exception e) {
            logger.error("按时间范围查询缴费记录失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据交易流水号查询缴费记录
     */
    @GetMapping("/transaction/{transactionNo}")
    @Operation(summary = "按流水号查询", description = "根据交易流水号查询缴费记录")
    public ResponseEntity<ApiResponse<PaymentRecord>> getPaymentRecordByTransaction(
            @Parameter(description = "交易流水号") @PathVariable String transactionNo) {
        try {
            Optional<PaymentRecord> paymentRecord = paymentRecordService.findByTransactionNo(transactionNo);
            if (paymentRecord.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(paymentRecord.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("按流水号查询缴费记录失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 统计学生缴费总金额
     */
    @GetMapping("/student/{studentId}/total-amount")
    @Operation(summary = "学生缴费总额", description = "统计学生的缴费总金额")
    public ResponseEntity<ApiResponse<BigDecimal>> getStudentTotalAmount(
            @Parameter(description = "学生ID") @PathVariable Long studentId) {
        try {
            BigDecimal totalAmount = paymentRecordService.sumAmountByStudentId(studentId);
            return ResponseEntity.ok(ApiResponse.success(totalAmount));
        } catch (Exception e) {
            logger.error("统计学生缴费总金额失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 统计缴费项目收费总金额
     */
    @GetMapping("/fee-item/{feeItemId}/total-amount")
    @Operation(summary = "项目收费总额", description = "统计缴费项目的收费总金额")
    public ResponseEntity<ApiResponse<BigDecimal>> getFeeItemTotalAmount(
            @Parameter(description = "缴费项目ID") @PathVariable Long feeItemId) {
        try {
            BigDecimal totalAmount = paymentRecordService.sumAmountByFeeItemId(feeItemId);
            return ResponseEntity.ok(ApiResponse.success(totalAmount));
        } catch (Exception e) {
            logger.error("统计缴费项目收费总金额失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 统计时间范围内的缴费总金额
     */
    @GetMapping("/total-amount")
    @Operation(summary = "时间段收费总额", description = "统计指定时间范围内的缴费总金额")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalAmountByTimeRange(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            BigDecimal totalAmount = paymentRecordService.sumAmountByTimeRange(startTime, endTime);
            return ResponseEntity.ok(ApiResponse.success(totalAmount));
        } catch (Exception e) {
            logger.error("统计时间范围缴费总金额失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 删除缴费记录
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除缴费记录", description = "根据ID删除缴费记录")
    public ResponseEntity<ApiResponse<Void>> deletePaymentRecord(
            @Parameter(description = "缴费记录ID") @PathVariable Long id) {
        try {
            boolean success = paymentRecordService.deletePaymentRecord(id);
            if (success) {
                return ResponseEntity.ok(ApiResponse.<Void>success("缴费记录删除成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("缴费记录删除失败"));
            }
        } catch (Exception e) {
            logger.error("删除缴费记录失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 批量删除缴费记录
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除缴费记录", description = "批量删除多个缴费记录")
    public ResponseEntity<ApiResponse<Void>> batchDeletePaymentRecords(
            @Parameter(description = "缴费记录ID列表") @RequestBody List<Long> ids) {
        try {
            boolean success = paymentRecordService.batchDeletePaymentRecords(ids);
            if (success) {
                return ResponseEntity.ok(ApiResponse.<Void>success("批量删除成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("批量删除失败"));
            }
        } catch (Exception e) {
            logger.error("批量删除缴费记录失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 退款处理
     */
    @PostMapping("/{id}/refund")
    @Operation(summary = "退款处理", description = "对指定的缴费记录进行退款处理")
    public ResponseEntity<ApiResponse<Void>> refundPayment(
            @Parameter(description = "缴费记录ID") @PathVariable Long id,
            @Parameter(description = "退款原因") @RequestParam String refundReason,
            @Parameter(description = "操作员ID") @RequestParam Long operatorId) {
        try {
            boolean success = paymentRecordService.refundPayment(id, refundReason, operatorId);
            if (success) {
                return ResponseEntity.ok(ApiResponse.<Void>success("退款处理成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("退款处理失败"));
            }
        } catch (Exception e) {
            logger.error("退款处理失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取缴费记录详情（包含关联信息）
     */
    @GetMapping("/{id}/detail")
    @Operation(summary = "获取详情", description = "获取缴费记录详情，包含学生和缴费项目信息")
    public ResponseEntity<ApiResponse<PaymentRecord>> getPaymentRecordDetail(
            @Parameter(description = "缴费记录ID") @PathVariable Long id) {
        try {
            Optional<PaymentRecord> record = paymentRecordService.findById(id);
            if (record.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(record.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("获取缴费记录详情失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }



    /**
     * 获取统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取统计信息", description = "获取缴费记录的统计信息")
    public ResponseEntity<ApiResponse<PaymentRecordService.PaymentStatistics>> getStatistics() {
        try {
            PaymentRecordService.PaymentStatistics statistics = paymentRecordService.getStatistics();
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            logger.error("获取缴费记录统计信息失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取指定时间范围的统计信息
     */
    @GetMapping("/statistics/time-range")
    @Operation(summary = "时间段统计", description = "获取指定时间范围的缴费记录统计信息")
    public ResponseEntity<ApiResponse<PaymentRecordService.PaymentStatistics>> getStatisticsByTimeRange(
            @Parameter(description = "开始时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            PaymentRecordService.PaymentStatistics statistics = paymentRecordService.getStatistics(startTime, endTime);
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            logger.error("获取时间范围缴费记录统计信息失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 生成交易流水号
     */
    @GetMapping("/generate-transaction-no")
    @Operation(summary = "生成流水号", description = "生成新的交易流水号")
    public ResponseEntity<ApiResponse<String>> generateTransactionNo() {
        try {
            String transactionNo = paymentRecordService.generateTransactionNo();
            return ResponseEntity.ok(ApiResponse.success(transactionNo));
        } catch (Exception e) {
            logger.error("生成交易流水号失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
