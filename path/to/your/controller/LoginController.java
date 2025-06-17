package com.example.smartcampusmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, 
                        @RequestParam String password,
                        RedirectAttributes redirectAttributes) {
        // 模拟登录验证逻辑
        if ("admin".equals(username) && "password".equals(password)) {
            // 登录成功，重定向到仪表盘
            return "redirect:/dashboard";
        } else {
            // 登录失败，返回错误信息
            redirectAttributes.addFlashAttribute("error", "用户名或密码错误");
            return "redirect:/login";
        }
    }
}