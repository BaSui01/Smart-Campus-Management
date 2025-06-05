package com.campus.interfaces.rest.v1;

import com.campus.application.service.FeeItemService;
import com.campus.shared.common.ApiResponse;
import com.campus.domain.entity.FeeItem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 缴费项目管理API控制器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@RestController
@RequestMapping("/api/fee-items")
@Tag(name = "缴费项目管理API", description = "缴费项目的增删改查操作REST API")
@SecurityRequirement(name = "Bearer")
public class FeeItemApiController {

    private static final Logger logger = LoggerFactory.getLogger(FeeItemApiController.class);

    @Autowired
    private FeeItemService feeItemService;

    /**
     * 创建缴费项目
     */
    @PostMapping
    @Operation(summary = "创建缴费项目", description = "创建新的缴费项目")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public ResponseEntity<ApiResponse<FeeItem>> createFeeItem(@Valid @RequestBody FeeItem feeItem) {
        try {
            logger.info("创建缴费项目请求: {}", feeItem.getItemName());
            FeeItem created = feeItemService.createFeeItem(feeItem);
            return ResponseEntity.ok(ApiResponse.success("缴费项目创建成功", created));
        } catch (Exception e) {
            logger.error("创建缴费项目失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 更新缴费项目
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新缴费项目", description = "根据ID更新缴费项目信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public ResponseEntity<ApiResponse<FeeItem>> updateFeeItem(
            @Parameter(description = "缴费项目ID") @PathVariable Long id,
            @Valid @RequestBody FeeItem feeItem) {
        try {
            logger.info("更新缴费项目请求: ID={}", id);
            FeeItem updated = feeItemService.updateFeeItem(id, feeItem);
            return ResponseEntity.ok(ApiResponse.success("缴费项目更新成功", updated));
        } catch (Exception e) {
            logger.error("更新缴费项目失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据ID查询缴费项目
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询缴费项目", description = "根据ID查询缴费项目详情")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'STUDENT')")
    public ResponseEntity<ApiResponse<FeeItem>> getFeeItem(
            @Parameter(description = "缴费项目ID") @PathVariable Long id) {
        try {
            Optional<FeeItem> feeItem = feeItemService.findById(id);
            if (feeItem.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(feeItem.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("查询缴费项目失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 分页查询缴费项目
     */
    @GetMapping
    @Operation(summary = "分页查询缴费项目", description = "分页查询缴费项目列表")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'STUDENT')")
    public ResponseEntity<ApiResponse<Page<FeeItem>>> getFeeItems(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createdTime") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<FeeItem> feeItems = feeItemService.findAll(pageable);
            return ResponseEntity.ok(ApiResponse.success(feeItems));
        } catch (Exception e) {
            logger.error("分页查询缴费项目失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据费用类型查询缴费项目
     */
    @GetMapping("/by-type/{feeType}")
    @Operation(summary = "按费用类型查询", description = "根据费用类型查询缴费项目")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<FeeItem>>> getFeeItemsByType(
            @Parameter(description = "费用类型") @PathVariable String feeType) {
        try {
            List<FeeItem> feeItems = feeItemService.findByFeeType(feeType);
            return ResponseEntity.ok(ApiResponse.success(feeItems));
        } catch (Exception e) {
            logger.error("按费用类型查询缴费项目失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据适用年级查询缴费项目
     */
    @GetMapping("/by-grade/{grade}")
    @Operation(summary = "按年级查询", description = "根据适用年级查询缴费项目")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<FeeItem>>> getFeeItemsByGrade(
            @Parameter(description = "适用年级") @PathVariable String grade) {
        try {
            List<FeeItem> feeItems = feeItemService.findByApplicableGrade(grade);
            return ResponseEntity.ok(ApiResponse.success(feeItems));
        } catch (Exception e) {
            logger.error("按年级查询缴费项目失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 查询启用状态的缴费项目
     */
    @GetMapping("/active")
    @Operation(summary = "查询启用项目", description = "查询所有启用状态的缴费项目")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<FeeItem>>> getActiveFeeItems() {
        try {
            List<FeeItem> feeItems = feeItemService.findActiveItems();
            return ResponseEntity.ok(ApiResponse.success(feeItems));
        } catch (Exception e) {
            logger.error("查询启用缴费项目失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据截止日期范围查询缴费项目
     */
    @GetMapping("/by-due-date")
    @Operation(summary = "按截止日期查询", description = "根据截止日期范围查询缴费项目")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public ResponseEntity<ApiResponse<List<FeeItem>>> getFeeItemsByDueDate(
            @Parameter(description = "开始日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "结束日期") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<FeeItem> feeItems = feeItemService.findByDueDateBetween(startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success(feeItems));
        } catch (Exception e) {
            logger.error("按截止日期查询缴费项目失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 根据金额范围查询缴费项目
     */
    @GetMapping("/by-amount")
    @Operation(summary = "按金额范围查询", description = "根据金额范围查询缴费项目")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public ResponseEntity<ApiResponse<List<FeeItem>>> getFeeItemsByAmount(
            @Parameter(description = "最小金额") @RequestParam BigDecimal minAmount,
            @Parameter(description = "最大金额") @RequestParam BigDecimal maxAmount) {
        try {
            List<FeeItem> feeItems = feeItemService.findByAmountBetween(minAmount, maxAmount);
            return ResponseEntity.ok(ApiResponse.success(feeItems));
        } catch (Exception e) {
            logger.error("按金额范围查询缴费项目失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 搜索缴费项目
     */
    @GetMapping("/search")
    @Operation(summary = "搜索缴费项目", description = "根据关键词、费用类型、状态搜索缴费项目")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<FeeItem>>> searchFeeItems(
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "费用类型") @RequestParam(required = false) String feeType,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        try {
            List<FeeItem> feeItems = feeItemService.searchFeeItems(keyword, feeType, status);
            return ResponseEntity.ok(ApiResponse.success(feeItems));
        } catch (Exception e) {
            logger.error("搜索缴费项目失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 更新缴费项目状态
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "更新状态", description = "更新缴费项目的启用/停用状态")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @Parameter(description = "缴费项目ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        try {
            boolean success = feeItemService.updateStatus(id, status);
            if (success) {
                return ResponseEntity.ok(ApiResponse.<Void>success("状态更新成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("状态更新失败"));
            }
        } catch (Exception e) {
            logger.error("更新缴费项目状态失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 删除缴费项目
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除缴费项目", description = "根据ID删除缴费项目")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteFeeItem(
            @Parameter(description = "缴费项目ID") @PathVariable Long id) {
        try {
            boolean success = feeItemService.deleteFeeItem(id);
            if (success) {
                return ResponseEntity.ok(ApiResponse.<Void>success("缴费项目删除成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("缴费项目删除失败"));
            }
        } catch (Exception e) {
            logger.error("删除缴费项目失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 批量删除缴费项目
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除缴费项目", description = "批量删除多个缴费项目")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> batchDeleteFeeItems(
            @Parameter(description = "缴费项目ID列表") @RequestBody List<Long> ids) {
        try {
            boolean success = feeItemService.batchDeleteFeeItems(ids);
            if (success) {
                return ResponseEntity.ok(ApiResponse.<Void>success("批量删除成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("批量删除失败"));
            }
        } catch (Exception e) {
            logger.error("批量删除缴费项目失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取缴费项目详情（包含统计信息）
     */
    @GetMapping("/{id}/detail")
    @Operation(summary = "获取详情", description = "获取缴费项目详情，包含缴费统计信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public ResponseEntity<ApiResponse<Object[]>> getFeeItemDetail(
            @Parameter(description = "缴费项目ID") @PathVariable Long id) {
        try {
            Optional<Object[]> detail = feeItemService.findDetailById(id);
            if (detail.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(detail.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("获取缴费项目详情失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取统计信息", description = "获取缴费项目的统计信息")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public ResponseEntity<ApiResponse<FeeItemService.FeeItemStatistics>> getStatistics() {
        try {
            FeeItemService.FeeItemStatistics statistics = feeItemService.getStatistics();
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            logger.error("获取缴费项目统计信息失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 生成项目编码
     */
    @GetMapping("/generate-code")
    @Operation(summary = "生成项目编码", description = "根据费用类型生成项目编码")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public ResponseEntity<ApiResponse<String>> generateItemCode(
            @Parameter(description = "费用类型") @RequestParam String feeType) {
        try {
            String itemCode = feeItemService.generateItemCode(feeType);
            return ResponseEntity.ok(ApiResponse.success(itemCode));
        } catch (Exception e) {
            logger.error("生成项目编码失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 检查项目编码是否存在
     */
    @GetMapping("/check-code")
    @Operation(summary = "检查编码", description = "检查项目编码是否已存在")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public ResponseEntity<ApiResponse<Boolean>> checkItemCode(
            @Parameter(description = "项目编码") @RequestParam String itemCode,
            @Parameter(description = "排除的ID") @RequestParam(required = false) Long excludeId) {
        try {
            boolean exists;
            if (excludeId != null) {
                exists = feeItemService.existsByItemCodeAndIdNot(itemCode, excludeId);
            } else {
                exists = feeItemService.existsByItemCode(itemCode);
            }
            return ResponseEntity.ok(ApiResponse.success(exists));
        } catch (Exception e) {
            logger.error("检查项目编码失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
