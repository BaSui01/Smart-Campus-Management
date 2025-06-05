package com.campus.interfaces.rest.v1;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.campus.application.service.PaymentRecordService;
import com.campus.shared.common.ApiResponse;
import com.campus.domain.entity.PaymentRecord;

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
@RequestMapping("/api/payments")
@Tag(name = "缴费管理API", description = "缴费记录和缴费项目管理REST API接口")
@SecurityRequirement(name = "Bearer")
public class PaymentApiController {

    @Autowired
    private PaymentRecordService paymentRecordService;

    // ========== 缴费记录API ==========

    /**
     * 获取缴费记录列表（分页）
     */
    @GetMapping("/records")
    @Operation(summary = "获取缴费记录列表", description = "分页查询缴费记录")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'STUDENT')")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'STUDENT')")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'STUDENT')")
    public ApiResponse<List<PaymentRecord>> getPaymentRecordsByStudentId(@Parameter(description = "学生ID") @PathVariable Long studentId) {
        List<PaymentRecord> paymentRecords = paymentRecordService.findByStudentId(studentId);
        return ApiResponse.success(paymentRecords);
    }

    /**
     * 创建缴费记录
     */
    @PostMapping("/records")
    @Operation(summary = "创建缴费记录", description = "添加新的缴费记录")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deletePaymentRecord(@Parameter(description = "缴费记录ID") @PathVariable Long id) {
        boolean result = paymentRecordService.deletePaymentRecord(id);
        if (result) {
            return ApiResponse.success("删除缴费记录成功");
        } else {
            return ApiResponse.error(404, "缴费记录不存在");
        }
    }



    // ========== 统计API ==========

    /**
     * 获取缴费统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取缴费统计信息", description = "获取缴费相关统计数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public ApiResponse<PaymentRecordService.PaymentStatistics> getPaymentStatistics() {
        try {
            PaymentRecordService.PaymentStatistics stats = paymentRecordService.getStatistics();
            return ApiResponse.success("获取缴费统计信息成功", stats);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取缴费统计信息失败：" + e.getMessage());
        }
    }
}
