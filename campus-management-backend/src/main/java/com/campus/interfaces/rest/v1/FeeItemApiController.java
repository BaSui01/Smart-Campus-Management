package com.campus.interfaces.rest.v1;

import com.campus.application.service.FeeItemService;
import com.campus.domain.entity.FeeItem;
import com.campus.shared.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@CrossOrigin(origins = "*", maxAge = 3600)
public class FeeItemApiController {

    private final FeeItemService feeItemService;

    @Autowired
    public FeeItemApiController(FeeItemService feeItemService) {
        this.feeItemService = feeItemService;
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

    /**
     * 批量删除缴费项目
     */
    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<String>> batchDeleteFeeItems(@RequestBody List<Long> ids) {
        try {
            boolean success = feeItemService.batchDeleteFeeItems(ids);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("批量删除缴费项目成功"));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("批量删除缴费项目失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("批量删除缴费项目失败: " + e.getMessage()));
        }
    }
}
