package com.campus.interfaces.rest.v1.finance;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.campus.application.service.finance.PaymentRecordService;
import com.campus.domain.entity.finance.PaymentRecord;
import com.campus.shared.common.ApiResponse;
import com.campus.interfaces.rest.common.BaseController;
import com.campus.shared.constants.RolePermissions;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 缴费管理API控制器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "缴费管理API", description = "缴费记录和缴费项目管理REST API接口")
@SecurityRequirement(name = "Bearer")
public class PaymentApiController extends BaseController {

    @Autowired
    private PaymentRecordService paymentRecordService;

    // ========== 缴费记录API ==========

    /**
     * 获取缴费记录列表（分页）
     */
    @GetMapping("/records")
    @Operation(summary = "获取缴费记录列表", description = "分页查询缴费记录")
    @PreAuthorize(RolePermissions.FINANCE_VIEW + " || " + RolePermissions.STUDENT_ACCESS + " || " + RolePermissions.PARENT_ACCESS)
    public ApiResponse<Map<String, Object>> getPaymentRecords(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "学生ID") @RequestParam(required = false) Long studentId,
            @Parameter(description = "缴费项目ID") @RequestParam(required = false) Long feeItemId,
            @Parameter(description = "支付状态") @RequestParam(required = false) String paymentStatus,
            @Parameter(description = "支付方式") @RequestParam(required = false) String paymentMethod) {

        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        if (studentId != null) {
            params.put("studentId", studentId);
        }
        if (feeItemId != null) {
            params.put("feeItemId", feeItemId);
        }
        if (paymentStatus != null && !paymentStatus.isEmpty()) {
            params.put("paymentStatus", paymentStatus);
        }
        if (paymentMethod != null && !paymentMethod.isEmpty()) {
            params.put("paymentMethod", paymentMethod);
        }

        // 执行分页查询 - 使用现有的findAll方法
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PaymentRecord> pageResult = paymentRecordService.findAll(pageable);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotalElements());
        result.put("pages", pageResult.getTotalPages());
        result.put("current", pageResult.getNumber() + 1);
        result.put("size", pageResult.getSize());
        result.put("records", pageResult.getContent());

        return ApiResponse.success("获取缴费记录列表成功", result);
    }

    /**
     * 获取缴费记录详情
     */
    @GetMapping("/records/{id}")
    @Operation(summary = "获取缴费记录详情", description = "根据ID查询缴费记录详细信息")
    @PreAuthorize(RolePermissions.FINANCE_VIEW + " || " + RolePermissions.STUDENT_ACCESS + " || " + RolePermissions.PARENT_ACCESS)
    public ApiResponse<PaymentRecord> getPaymentRecordById(@Parameter(description = "缴费记录ID") @PathVariable Long id) {
        Optional<PaymentRecord> paymentRecord = paymentRecordService.findById(id);
        if (paymentRecord.isPresent()) {
            return ApiResponse.success(paymentRecord.get());
        } else {
            return ApiResponse.error(404, "缴费记录不存在");
        }
    }

    /**
     * 根据学生ID查询缴费记录
     */
    @GetMapping("/records/student/{studentId}")
    @Operation(summary = "根据学生ID查询缴费记录", description = "获取指定学生的所有缴费记录")
    @PreAuthorize(RolePermissions.STUDENT_ACCESS + " || " + RolePermissions.PARENT_ACCESS)
    public ApiResponse<List<PaymentRecord>> getPaymentRecordsByStudentId(@Parameter(description = "学生ID") @PathVariable Long studentId) {
        List<PaymentRecord> paymentRecords = paymentRecordService.findByStudentId(studentId);
        return ApiResponse.success(paymentRecords);
    }

    /**
     * 创建缴费记录
     */
    @PostMapping("/records")
    @Operation(summary = "创建缴费记录", description = "添加新的缴费记录")
    @PreAuthorize(RolePermissions.FINANCE_MANAGEMENT)
    public ApiResponse<PaymentRecord> createPaymentRecord(@Parameter(description = "缴费记录信息") @Valid @RequestBody PaymentRecord paymentRecord) {
        try {
            PaymentRecord createdRecord = paymentRecordService.createPaymentRecord(paymentRecord);
            return ApiResponse.success("创建缴费记录成功", createdRecord);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "创建缴费记录失败：" + e.getMessage());
        }
    }

    /**
     * 更新缴费记录
     */
    @PutMapping("/records/{id}")
    @Operation(summary = "更新缴费记录", description = "修改缴费记录信息")
    @PreAuthorize(RolePermissions.FINANCE_MANAGEMENT)
    public ApiResponse<Void> updatePaymentRecord(
            @Parameter(description = "缴费记录ID") @PathVariable Long id,
            @Parameter(description = "缴费记录信息") @Valid @RequestBody PaymentRecord paymentRecord) {
        try {
            PaymentRecord updated = paymentRecordService.updatePaymentRecord(id, paymentRecord);
            if (updated != null) {
                return ApiResponse.success("更新缴费记录成功");
            } else {
                return ApiResponse.error(404, "缴费记录不存在");
            }
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "更新缴费记录失败：" + e.getMessage());
        }
    }

    /**
     * 删除缴费记录
     */
    @DeleteMapping("/records/{id}")
    @Operation(summary = "删除缴费记录", description = "删除指定缴费记录")
    @PreAuthorize(RolePermissions.BATCH_OPERATIONS)
    public ApiResponse<Void> deletePaymentRecord(@Parameter(description = "缴费记录ID") @PathVariable Long id) {
        boolean result = paymentRecordService.deletePaymentRecord(id);
        if (result) {
            return ApiResponse.success("删除缴费记录成功");
        } else {
            return ApiResponse.error(404, "缴费记录不存在");
        }
    }



    // ==================== 统计端点 ====================

    /**
     * 获取缴费统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取缴费统计信息", description = "获取缴费模块的统计数据")
    @PreAuthorize(RolePermissions.STATISTICS_VIEW)
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPaymentStats() {
        try {
            log.info("获取缴费统计信息");

            Map<String, Object> stats = new HashMap<>();

            // 获取基础统计
            PaymentRecordService.PaymentStatistics paymentStats = paymentRecordService.getStatistics();

            // 基础统计
            stats.put("totalPayments", paymentStats.getTotalRecords());
            stats.put("successPayments", paymentStats.getSuccessRecords());
            stats.put("refundPayments", paymentStats.getRefundRecords());
            stats.put("failedPayments", paymentStats.getFailedRecords());

            // 金额统计
            stats.put("totalAmount", paymentStats.getTotalAmount());
            stats.put("successAmount", paymentStats.getSuccessAmount());
            stats.put("refundAmount", paymentStats.getRefundAmount());

            // 按支付方式统计
            Map<String, Long> methodStats = new HashMap<>();
            List<PaymentRecord> alipayRecords = paymentRecordService.findByPaymentMethod("alipay");
            methodStats.put("alipay", (long) alipayRecords.size());

            List<PaymentRecord> wechatRecords = paymentRecordService.findByPaymentMethod("wechat");
            methodStats.put("wechat", (long) wechatRecords.size());

            List<PaymentRecord> bankRecords = paymentRecordService.findByPaymentMethod("bank");
            methodStats.put("bank", (long) bankRecords.size());

            List<PaymentRecord> cashRecords = paymentRecordService.findByPaymentMethod("cash");
            methodStats.put("cash", (long) cashRecords.size());

            stats.put("methodStats", methodStats);

            // 时间统计（简化实现）
            stats.put("todayPayments", 0L);
            stats.put("weekPayments", 0L);
            stats.put("monthPayments", 0L);

            // 最近活动（简化实现）
            List<Map<String, Object>> recentActivity = new ArrayList<>();
            stats.put("recentActivity", recentActivity);

            return success("获取缴费统计信息成功", stats);

        } catch (Exception e) {
            log.error("获取缴费统计信息失败: ", e);
            return error("获取缴费统计信息失败: " + e.getMessage());
        }
    }

    // ==================== 批量操作端点 ====================

    /**
     * 批量删除缴费记录
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除缴费记录", description = "根据ID列表批量删除缴费记录")
    @PreAuthorize(RolePermissions.BATCH_OPERATIONS)
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchDeletePayments(
            @Parameter(description = "缴费记录ID列表") @RequestBody List<Long> ids) {

        try {
            logOperation("批量删除缴费记录", ids.size());

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("缴费记录ID列表不能为空");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "缴费记录");
            }

            // 执行批量删除
            boolean result = paymentRecordService.batchDeletePaymentRecords(ids);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("deletedCount", result ? ids.size() : 0);
            responseData.put("totalRequested", ids.size());
            responseData.put("success", result);

            if (result) {
                return success("批量删除缴费记录成功", responseData);
            } else {
                return error("批量删除缴费记录失败");
            }

        } catch (Exception e) {
            log.error("批量删除缴费记录失败: ", e);
            return error("批量删除缴费记录失败: " + e.getMessage());
        }
    }

    /**
     * 批量退款
     */
    @PutMapping("/batch/refund")
    @Operation(summary = "批量退款", description = "批量处理缴费记录退款")
    @PreAuthorize(RolePermissions.BATCH_OPERATIONS)
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchRefundPayments(
            @Parameter(description = "批量退款请求") @RequestBody Map<String, Object> request) {

        try {
            @SuppressWarnings("unchecked")
            List<Long> ids = (List<Long>) request.get("ids");
            String refundReason = (String) request.get("refundReason");
            Object operatorIdObj = request.get("operatorId");

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("缴费记录ID列表不能为空");
            }

            if (refundReason == null || refundReason.trim().isEmpty()) {
                return badRequest("退款原因不能为空");
            }

            if (operatorIdObj == null) {
                return badRequest("操作员ID不能为空");
            }

            Long operatorId = ((Number) operatorIdObj).longValue();
            logOperation("批量退款", ids.size(), "原因: " + refundReason);

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "缴费记录");
            }

            // 执行批量退款
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Long id : ids) {
                try {
                    boolean result = paymentRecordService.refundPayment(id, refundReason, operatorId);
                    if (result) {
                        successCount++;
                    } else {
                        failCount++;
                        failReasons.add("缴费记录ID " + id + ": 退款失败");
                    }
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("缴费记录ID " + id + ": " + e.getMessage());
                    log.warn("退款失败 - ID: {}", id, e);
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", ids.size());
            responseData.put("failReasons", failReasons);

            if (failCount == 0) {
                return success("批量退款成功", responseData);
            } else if (successCount > 0) {
                return success("批量退款部分成功", responseData);
            } else {
                return error("批量退款失败");
            }

        } catch (Exception e) {
            log.error("批量退款失败: ", e);
            return error("批量退款失败: " + e.getMessage());
        }
    }
}
