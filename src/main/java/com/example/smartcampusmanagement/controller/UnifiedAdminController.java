package com.example.smartcampusmanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/admin")  // 修改路径前缀
public class UnifiedAdminController {

    @GetMapping("/notifications")
    public String notifications(Model model, HttpServletRequest request) {
        // 方法逻辑保持不变
    }

    // 其他方法保持不变
}