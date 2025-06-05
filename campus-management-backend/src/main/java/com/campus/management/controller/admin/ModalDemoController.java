package com.campus.management.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 弹窗演示控制器
 * 用于展示各种弹窗功能
 */
@Controller
@RequestMapping("/admin/modal-demo")
public class ModalDemoController {

    /**
     * 显示弹窗演示页面
     */
    @GetMapping
    public String showModalDemo() {
        return "admin/modal-demo";
    }
}
