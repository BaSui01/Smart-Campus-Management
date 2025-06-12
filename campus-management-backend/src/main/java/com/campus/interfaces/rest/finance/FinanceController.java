package com.campus.interfaces.rest.finance;

import com.campus.application.service.finance.FinanceService;
import com.campus.domain.entity.finance.FeeItem;
import com.campus.domain.entity.finance.PaymentRecord;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 财务管理控制器
 */
@Controller
public class FinanceController {

    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    /**
     * 财务管理主页
     */
    @GetMapping("/admin/finance")
    public String finance(Model model) {
        try {
            // 获取财务统计数据
            Map<String, Object> stats = financeService.getFinanceStats();

            // 获取最近收支记录
            List<PaymentRecord> recentRecords = financeService.getRecentFinanceRecords(10);

            model.addAttribute("stats", stats);
            model.addAttribute("recentRecords", recentRecords);
            model.addAttribute("pageTitle", "财务管理");
            model.addAttribute("currentPage", "finance");

            return "admin/finance/finance";
        } catch (Exception e) {
            model.addAttribute("error", "加载财务管理页面失败：" + e.getMessage());
            return "admin/finance/finance";
        }
    }

    /**
     * 收费项目管理页面
     */
    @GetMapping("/admin/finance/fee-items")
    public String feeItems(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "20") int size,
                          @RequestParam(defaultValue = "") String search,
                          @RequestParam(defaultValue = "") String feeType,
                          @RequestParam(defaultValue = "") String status,
                          Model model) {
        try {
            // 使用分页查询避免一次性加载大量数据
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdTime"));
            Page<FeeItem> feeItemPage = financeService.getFeeItemsPage(pageable, search, feeType, status);

            // 获取统计信息
            Map<String, Object> stats = financeService.getFinanceStats();

            model.addAttribute("feeItems", feeItemPage);
            model.addAttribute("stats", stats);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("search", search);
            model.addAttribute("feeType", feeType);
            model.addAttribute("status", status);
            model.addAttribute("pageTitle", "收费项目管理");
            model.addAttribute("currentPageName", "finance");

            return "admin/finance/fee-items";
        } catch (Exception e) {
            model.addAttribute("error", "加载收费项目页面失败：" + e.getMessage());
            return "admin/finance/fee-items";
        }
    }

    /**
     * 缴费记录页面
     */
    @GetMapping("/admin/finance/payment-records")
    public String paymentRecords(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "20") int size,
                                @RequestParam(defaultValue = "") String search,
                                @RequestParam(defaultValue = "") String status,
                                @RequestParam(defaultValue = "") String paymentMethod,
                                Model model) {
        try {
            // 使用分页查询避免一次性加载大量数据
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paymentTime"));
            Page<PaymentRecord> paymentPage = financeService.getPaymentRecordsPage(pageable, search, status, paymentMethod);

            // 获取统计信息
            Map<String, Object> stats = financeService.getFinanceStats();

            model.addAttribute("paymentRecords", paymentPage);
            model.addAttribute("stats", stats);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("search", search);
            model.addAttribute("status", status);
            model.addAttribute("paymentMethod", paymentMethod);
            model.addAttribute("pageTitle", "缴费记录");
            model.addAttribute("currentPageName", "finance");

            return "admin/finance/payment-records";
        } catch (Exception e) {
            model.addAttribute("error", "加载缴费记录页面失败：" + e.getMessage());
            return "admin/finance/payment-records";
        }
    }

    /**
     * 财务报表页面
     */
    @GetMapping("/admin/finance/reports")
    public String financeReports(Model model) {
        try {
            // 获取报表数据
            Map<String, Object> reportData = financeService.getFinanceReportData();

            model.addAttribute("reportData", reportData);
            model.addAttribute("pageTitle", "财务报表");
            model.addAttribute("currentPage", "finance");

            return "admin/finance/reports";
        } catch (Exception e) {
            model.addAttribute("error", "加载财务报表页面失败：" + e.getMessage());
            return "admin/finance/reports";
        }
    }

}
