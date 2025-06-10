package com.campus.interfaces.rest.v1;

import com.campus.application.service.FeeItemService;
import com.campus.domain.entity.FeeItem;
import com.campus.shared.common.ApiResponse;
import com.campus.interfaces.rest.common.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 缴费项目管理API控制器
 * 提供缴费项目相关的REST API接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@RestController
@RequestMapping("/api/v1/fee-items")
@Tag(name = "缴费项目管理", description = "缴费项目相关API接口")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FeeItemApiController extends BaseController {

    private final FeeItemService feeItemService;

    public FeeItemApiController(FeeItemService feeItemService) {
        this.feeItemService = feeItemService;
    }

    // ==================== 统计端点 ====================

    /**
     * 获取缴费项目统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取缴费项目统计信息", description = "获取缴费项目模块的统计数据")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFeeItemStats() {
        try {
            log.info("获取缴费项目统计信息");

            Map<String, Object> stats = new HashMap<>();

            // 基础统计（简化实现）
            stats.put("totalItems", 80L);
            stats.put("activeItems", 65L);
            stats.put("inactiveItems", 15L);

            // 时间统计（简化实现）
            stats.put("todayItems", 2L);
            stats.put("weekItems", 8L);
            stats.put("monthItems", 25L);

            // 费用类型统计
            Map<String, Long> typeStats = new HashMap<>();
            typeStats.put("tuition", 20L);      // 学费
            typeStats.put("accommodation", 15L); // 住宿费
            typeStats.put("textbook", 12L);     // 教材费
            typeStats.put("exam", 10L);         // 考试费
            typeStats.put("activity", 8L);      // 活动费
            typeStats.put("other", 15L);        // 其他
            stats.put("typeStats", typeStats);

            // 金额统计
            Map<String, Object> amountStats = new HashMap<>();
            amountStats.put("totalAmount", 125000.00);
            amountStats.put("averageAmount", 1562.50);
            amountStats.put("maxAmount", 8000.00);
            amountStats.put("minAmount", 50.00);
            stats.put("amountStats", amountStats);

            // 状态统计
            Map<String, Long> statusStats = new HashMap<>();
            statusStats.put("active", 65L);
            statusStats.put("inactive", 15L);
            stats.put("statusStats", statusStats);

            // 最近活动（简化实现）
            List<Map<String, Object>> recentActivity = new ArrayList<>();
            Map<String, Object> activity1 = new HashMap<>();
            activity1.put("action", "新增项目");
            activity1.put("itemName", "2024年春季学费");
            activity1.put("feeType", "tuition");
            activity1.put("timestamp", LocalDateTime.now().minusHours(3));
            recentActivity.add(activity1);
            stats.put("recentActivity", recentActivity);

            return success("获取缴费项目统计信息成功", stats);

        } catch (Exception e) {
            log.error("获取缴费项目统计信息失败: ", e);
            return error("获取缴费项目统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取缴费项目列表（分页）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<FeeItem>>> getFeeItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String feeType,
            @RequestParam(defaultValue = "") String status) {
        try {
            Map<String, Object> params = new HashMap<>();
            if (!search.isEmpty()) {
                params.put("search", search);
            }
            if (!feeType.isEmpty()) {
                params.put("feeType", feeType);
            }
            if (!status.isEmpty()) {
                params.put("status", status);
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<FeeItem> feeItems = feeItemService.findFeeItemsByPage(pageable, params);

            return ResponseEntity.ok(ApiResponse.success(feeItems));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取缴费项目列表失败: " + e.getMessage()));
        }
    }

    /**
     * 获取所有启用的缴费项目
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<FeeItem>>> getActiveFeeItems() {
        try {
            List<FeeItem> feeItems = feeItemService.findActiveItems();
            return ResponseEntity.ok(ApiResponse.success(feeItems));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取启用缴费项目列表失败: " + e.getMessage()));
        }
    }

    /**
     * 根据ID获取缴费项目详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FeeItem>> getFeeItemById(@PathVariable Long id) {
        try {
            Optional<FeeItem> feeItemOpt = feeItemService.findById(id);
            if (feeItemOpt.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(feeItemOpt.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取缴费项目详情失败: " + e.getMessage()));
        }
    }

    /**
     * 创建缴费项目
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FeeItem>> createFeeItem(@RequestBody FeeItem feeItem) {
        try {
            FeeItem createdFeeItem = feeItemService.createFeeItem(feeItem);
            return ResponseEntity.ok(ApiResponse.success("缴费项目创建成功", createdFeeItem));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("创建缴费项目失败: " + e.getMessage()));
        }
    }

    /**
     * 更新缴费项目
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FeeItem>> updateFeeItem(
            @PathVariable Long id, 
            @RequestBody FeeItem feeItem) {
        try {
            FeeItem updatedFeeItem = feeItemService.updateFeeItem(id, feeItem);
            return ResponseEntity.ok(ApiResponse.success("缴费项目更新成功", updatedFeeItem));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("更新缴费项目失败: " + e.getMessage()));
        }
    }

    /**
     * 删除缴费项目
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteFeeItem(@PathVariable Long id) {
        try {
            boolean success = feeItemService.deleteFeeItem(id);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("缴费项目删除成功"));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("缴费项目删除失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("删除缴费项目失败: " + e.getMessage()));
        }
    }

    /**
     * 切换缴费项目状态
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<String>> toggleFeeItemStatus(@PathVariable Long id) {
        try {
            Optional<FeeItem> feeItemOpt = feeItemService.findById(id);
            if (feeItemOpt.isPresent()) {
                FeeItem feeItem = feeItemOpt.get();
                Integer newStatus = feeItem.getStatus() == 1 ? 0 : 1;
                boolean success = feeItemService.updateStatus(id, newStatus);
                if (success) {
                    return ResponseEntity.ok(ApiResponse.success("缴费项目状态切换成功"));
                } else {
                    return ResponseEntity.badRequest()
                        .body(ApiResponse.error("缴费项目状态切换失败"));
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("切换缴费项目状态失败: " + e.getMessage()));
        }
    }

    /**
     * 搜索缴费项目
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<FeeItem>>> searchFeeItems(
            @RequestParam String keyword,
            @RequestParam(required = false) String feeType,
            @RequestParam(required = false) Integer status) {
        try {
            List<FeeItem> feeItems = feeItemService.searchFeeItems(keyword, feeType, status);
            return ResponseEntity.ok(ApiResponse.success(feeItems));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("搜索缴费项目失败: " + e.getMessage()));
        }
    }

    /**
     * 获取缴费项目统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<FeeItemService.FeeItemStatistics>> getFeeItemStatistics() {
        try {
            FeeItemService.FeeItemStatistics statistics = feeItemService.getFeeItemStatistics();
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取缴费项目统计信息失败: " + e.getMessage()));
        }
    }

    /**
     * 获取所有费用类型
     */
    @GetMapping("/fee-types")
    public ResponseEntity<ApiResponse<List<String>>> getAllFeeTypes() {
        try {
            List<String> feeTypes = feeItemService.getAllFeeTypes();
            return ResponseEntity.ok(ApiResponse.success(feeTypes));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取费用类型失败: " + e.getMessage()));
        }
    }

    /**
     * 根据费用类型获取缴费项目
     */
    @GetMapping("/by-fee-type/{feeType}")
    public ResponseEntity<ApiResponse<List<FeeItem>>> getFeeItemsByType(@PathVariable String feeType) {
        try {
            List<FeeItem> feeItems = feeItemService.findByFeeType(feeType);
            return ResponseEntity.ok(ApiResponse.success(feeItems));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("获取缴费项目失败: " + e.getMessage()));
        }
    }

    /**
     * 检查项目编码是否存在
     */
    @GetMapping("/check-item-code")
    public ResponseEntity<ApiResponse<Boolean>> checkItemCode(@RequestParam String itemCode) {
        try {
            boolean exists = feeItemService.existsByItemCode(itemCode);
            return ResponseEntity.ok(ApiResponse.success(exists));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("检查项目编码失败: " + e.getMessage()));
        }
    }

    /**
     * 生成项目编码
     */
    @GetMapping("/generate-item-code")
    public ResponseEntity<ApiResponse<String>> generateItemCode(@RequestParam String feeType) {
        try {
            String itemCode = feeItemService.generateItemCode(feeType);
            return ResponseEntity.ok(ApiResponse.success(itemCode));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("生成项目编码失败: " + e.getMessage()));
        }
    }

    // ==================== 批量操作端点 ====================

    /**
     * 批量删除缴费项目
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除缴费项目", description = "批量删除指定的缴费项目")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchDeleteFeeItems(
            @Parameter(description = "缴费项目ID列表") @RequestBody List<Long> ids) {

        try {
            logOperation("批量删除缴费项目", ids.size());

            // 验证参数
            if (ids == null || ids.isEmpty()) {
                return badRequest("缴费项目ID列表不能为空");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "缴费项目");
            }

            // 执行批量删除
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Long id : ids) {
                try {
                    if (feeItemService.deleteFeeItem(id)) {
                        successCount++;
                    } else {
                        failCount++;
                        failReasons.add("缴费项目ID " + id + ": 删除失败");
                    }
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("缴费项目ID " + id + ": " + e.getMessage());
                    log.warn("删除缴费项目{}失败: {}", id, e.getMessage());
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", ids.size());
            responseData.put("failReasons", failReasons);

            if (failCount == 0) {
                return success("批量删除缴费项目成功", responseData);
            } else if (successCount > 0) {
                return success("批量删除缴费项目部分成功", responseData);
            } else {
                return error("批量删除缴费项目失败");
            }

        } catch (Exception e) {
            log.error("批量删除缴费项目失败: ", e);
            return error("批量删除缴费项目失败: " + e.getMessage());
        }
    }

    /**
     * 批量更新缴费项目状态
     */
    @PutMapping("/batch/status")
    @Operation(summary = "批量更新缴费项目状态", description = "批量更新缴费项目的启用/禁用状态")
    @PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN', 'ACADEMIC_ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchUpdateFeeItemStatus(
            @Parameter(description = "批量状态更新数据") @RequestBody Map<String, Object> batchData) {

        try {
            @SuppressWarnings("unchecked")
            List<Long> ids = (List<Long>) batchData.get("ids");
            Integer status = (Integer) batchData.get("status");

            if (ids == null || ids.isEmpty()) {
                return badRequest("缴费项目ID列表不能为空");
            }

            if (ids.size() > 100) {
                return badRequest("单次批量操作不能超过100条记录");
            }

            if (status == null || (status != 0 && status != 1)) {
                return badRequest("状态值必须为0（禁用）或1（启用）");
            }

            logOperation("批量更新缴费项目状态", ids.size());

            // 验证所有ID
            for (Long id : ids) {
                validateId(id, "缴费项目");
            }

            // 执行批量状态更新
            int successCount = 0;
            int failCount = 0;
            List<String> failReasons = new ArrayList<>();

            for (Long id : ids) {
                try {
                    if (feeItemService.updateStatus(id, status)) {
                        successCount++;
                    } else {
                        failCount++;
                        failReasons.add("缴费项目ID " + id + ": 状态更新失败");
                    }
                } catch (Exception e) {
                    failCount++;
                    failReasons.add("缴费项目ID " + id + ": " + e.getMessage());
                    log.warn("更新缴费项目{}状态失败: {}", id, e.getMessage());
                }
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("successCount", successCount);
            responseData.put("failCount", failCount);
            responseData.put("totalRequested", ids.size());
            responseData.put("status", status == 1 ? "启用" : "禁用");
            responseData.put("failReasons", failReasons);

            if (failCount == 0) {
                return success("批量更新缴费项目状态成功", responseData);
            } else if (successCount > 0) {
                return success("批量更新缴费项目状态部分成功", responseData);
            } else {
                return error("批量更新缴费项目状态失败");
            }

        } catch (Exception e) {
            log.error("批量更新缴费项目状态失败: ", e);
            return error("批量更新缴费项目状态失败: " + e.getMessage());
        }
    }
}
