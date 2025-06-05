package com.campus.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.campus.entity.User;
import com.campus.service.UserService;
import com.campus.util.CaptchaUtil;
import com.campus.utils.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 处理用户登录、登出、验证码等认证相关功能
 *
 * @author campus
 * @since 2025-06-04
 */
@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final CaptchaUtil captchaUtil;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil, CaptchaUtil captchaUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.captchaUtil = captchaUtil;
    }

    /**
     * 显示登录页面
     */
    @GetMapping("/admin/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           @RequestParam(value = "redirect", required = false) String redirect,
                           Model model) {
        try {
            if (error != null) {
                model.addAttribute("error", "用户名或密码错误");
            }
            if (logout != null) {
                model.addAttribute("message", "您已成功登出");
            }

            // 保存重定向URL
            model.addAttribute("redirectUrl", redirect != null ? redirect : "dashboard");

            return "admin/login";
        } catch (Exception e) {
            // 如果出现异常，返回简单的错误页面
            model.addAttribute("error", "页面加载失败：" + e.getMessage());
            return "admin/login";
        }
    }

    /**
     * 处理登录请求
     */
    @PostMapping("/admin/login")
    public String login(@RequestParam String username,
                       @RequestParam String password,
                       @RequestParam(value = "captcha", required = false) String captcha,
                       @RequestParam(value = "rememberMe", required = false) boolean rememberMe,
                       @RequestParam(value = "redirect", required = false, defaultValue = "dashboard") String redirectUrl,
                       HttpServletRequest request,
                       RedirectAttributes redirectAttributes) {
        try {
            HttpSession session = request.getSession();

            // 验证验证码（如果提供了验证码）
            if (captcha != null && !captcha.isEmpty()) {
                String sessionCaptcha = (String) session.getAttribute("captcha");
                if (sessionCaptcha == null || !sessionCaptcha.equalsIgnoreCase(captcha)) {
                    redirectAttributes.addFlashAttribute("error", "验证码错误");
                    return "redirect:/admin/login?redirect=" + redirectUrl;
                }
                // 清除验证码
                session.removeAttribute("captcha");
            }

            // 验证用户是否为系统管理员
            User user = userService.findByUsername(username);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "用户不存在");
                return "redirect:/admin/login?redirect=" + redirectUrl;
            }

            // 检查用户状态
            if (user.getStatus() != 1) {
                redirectAttributes.addFlashAttribute("error", "用户已被禁用");
                return "redirect:/admin/login?redirect=" + redirectUrl;
            }

            // 检查用户角色（只允许管理员和教师登录后台）
            boolean hasAdminRole = userService.hasRole(user.getId(), "ADMIN") ||
                                 userService.hasRole(user.getId(), "TEACHER");
            if (!hasAdminRole) {
                redirectAttributes.addFlashAttribute("error", "无权限访问管理后台");
                return "redirect:/admin/login?redirect=" + redirectUrl;
            }

            // 执行用户认证
            User authenticatedUser = userService.authenticate(username, password);
            if (authenticatedUser == null) {
                logger.warn("用户认证失败: {}", username);
                redirectAttributes.addFlashAttribute("error", "用户名或密码错误");
                redirectAttributes.addFlashAttribute("username", username);
                redirectAttributes.addFlashAttribute("rememberMe", rememberMe);
                return "redirect:/admin/login?redirect=" + redirectUrl;
            }

            logger.info("用户登录成功: {}", username);

            // 将用户信息存储到session
            session.setAttribute("currentUser", authenticatedUser);
            session.setAttribute("loginTime", System.currentTimeMillis());

            // 生成管理后台专用JWT Token
            String token = jwtUtil.generateAdminToken(authenticatedUser.getId(), username, "ADMIN");
            session.setAttribute("accessToken", token);

            // 记录登录日志
            userService.recordLoginLog(authenticatedUser.getId(), request.getRemoteAddr(),
                                     request.getHeader("User-Agent"));

            // 根据重定向参数决定跳转位置
            String redirectTarget;
            if ("home".equals(redirectUrl)) {
                redirectTarget = "redirect:/";  // 跳转到首页
            } else {
                redirectTarget = "redirect:/admin/dashboard";  // 跳转到管理后台
            }

            logger.info("用户 {} 登录成功，准备重定向到: {}", username, redirectTarget);
            return redirectTarget;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "登录失败：" + e.getMessage());
            return "redirect:/admin/login?redirect=" + redirectUrl;
        }
    }

    /**
     * 登出处理
     */
    @PostMapping("/admin/logout")
    public String logout(HttpServletRequest request,
                        @RequestParam(value = "redirect", required = false, defaultValue = "home") String redirect,
                        RedirectAttributes redirectAttributes) {
        try {
            // 获取当前用户信息
            HttpSession session = request.getSession(false);
            if (session != null) {
                User currentUser = (User) session.getAttribute("currentUser");
                if (currentUser != null) {
                    // 记录登出日志
                    userService.recordLogoutLog(currentUser.getId());
                }

                // 清除session
                session.invalidate();
            }

            redirectAttributes.addFlashAttribute("message", "您已成功登出");

            // 根据参数决定跳转位置
            if ("home".equals(redirect)) {
                return "redirect:/";  // 跳转到首页
            } else {
                return "redirect:/admin/login";  // 跳转到登录页面
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "登出过程中发生错误");
            return "redirect:/";
        }
    }

    /**
     * 生成验证码图片
     */
    @GetMapping("/admin/captcha")
    public ResponseEntity<byte[]> generateCaptcha(HttpServletRequest request) {
        try {
            CaptchaUtil.CaptchaResult captchaResult = captchaUtil.generateCaptcha();

            // 将验证码保存到session
            HttpSession session = request.getSession();
            session.setAttribute("captcha", captchaResult.getCode());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);

            return new ResponseEntity<>(captchaResult.getImageBytes(), headers, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("生成验证码失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 系统测试页面
     */
    @GetMapping("/admin/test")
    public String testPage(Model model) {
        model.addAttribute("pageTitle", "系统测试");
        return "admin/test";
    }

    /**
     * 检查登录状态
     */
    @GetMapping("/admin/check-login")
    @ResponseBody
    public boolean checkLoginStatus(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        return currentUser != null;
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/admin/current-user")
    @ResponseBody
    public User getCurrentUser(HttpServletRequest request) {
        // 优先从请求属性获取（JWT认证设置的）
        User user = (User) request.getAttribute("currentUser");
        if (user != null) {
            return user;
        }

        // 兼容session方式
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        return (User) session.getAttribute("currentUser");
    }

    /**
     * 刷新JWT Token
     */
    @PostMapping("/admin/refresh-token")
    @ResponseBody
    public Map<String, Object> refreshToken(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            HttpSession session = request.getSession(false);
            String token = null;

            // 从session或请求头获取token
            if (session != null) {
                token = (String) session.getAttribute("accessToken");
            }

            if (token == null) {
                String authHeader = request.getHeader("Authorization");
                token = jwtUtil.extractTokenFromHeader(authHeader);
            }

            if (token == null) {
                result.put("success", false);
                result.put("message", "未找到有效的Token");
                return result;
            }

            // 验证并刷新token
            if (jwtUtil.validateToken(token) && jwtUtil.isAdminToken(token)) {
                String newToken = jwtUtil.refreshToken(token);

                // 更新session中的token
                if (session != null) {
                    session.setAttribute("accessToken", newToken);
                }

                result.put("success", true);
                result.put("token", newToken);
                result.put("expiresIn", jwtUtil.getTokenRemainingTime(newToken));

                logger.info("Token刷新成功: {}", jwtUtil.getUsernameFromToken(newToken));
            } else {
                result.put("success", false);
                result.put("message", "Token无效或已过期");
            }

        } catch (Exception e) {
            logger.error("Token刷新失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "Token刷新失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 获取Token状态信息
     */
    @GetMapping("/admin/token-status")
    @ResponseBody
    public Map<String, Object> getTokenStatus(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();

        try {
            HttpSession session = request.getSession(false);
            String token = null;

            if (session != null) {
                token = (String) session.getAttribute("accessToken");
            }

            if (token == null) {
                String authHeader = request.getHeader("Authorization");
                token = jwtUtil.extractTokenFromHeader(authHeader);
            }

            if (token != null && jwtUtil.validateToken(token)) {
                result.put("valid", true);
                result.put("username", jwtUtil.getUsernameFromToken(token));
                result.put("role", jwtUtil.getRoleFromToken(token));
                result.put("expiresIn", jwtUtil.getTokenRemainingTime(token));
                result.put("nearExpiry", jwtUtil.isTokenNearExpiry(token));
                result.put("isAdminToken", jwtUtil.isAdminToken(token));
            } else {
                result.put("valid", false);
                result.put("message", "Token无效或已过期");
            }

        } catch (Exception e) {
            result.put("valid", false);
            result.put("message", "Token验证失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 修改密码页面
     */
    @GetMapping("/admin/change-password")
    public String changePasswordPage(Model model) {
        model.addAttribute("pageTitle", "修改密码");
        model.addAttribute("currentPage", "change-password");
        return "admin/change-password";
    }

    /**
     * 处理修改密码请求
     */
    @PostMapping("/admin/change-password")
    public String changePassword(@RequestParam String oldPassword,
                               @RequestParam String newPassword,
                               @RequestParam String confirmPassword,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        try {
            // 获取当前用户
            HttpSession session = request.getSession();
            User currentUser = (User) session.getAttribute("currentUser");

            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "用户未登录");
                return "redirect:/admin/login";
            }

            // 验证新密码
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "两次密码输入不一致");
                return "redirect:/admin/change-password";
            }

            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "密码长度不能少于6位");
                return "redirect:/admin/change-password";
            }

            // 修改密码
            userService.changePassword(currentUser.getId(), oldPassword, newPassword);

            redirectAttributes.addFlashAttribute("message", "密码修改成功，请重新登录");

            // 清除session，要求重新登录
            session.invalidate();

            return "redirect:/admin/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "密码修改失败：" + e.getMessage());
            return "redirect:/admin/change-password";
        }
    }

    /**
     * 根路径重定向（已移至HomeController）
     */
    @GetMapping("/admin")
    public String adminIndex() {
        return "redirect:/admin/dashboard";
    }


    /**
     * 会话超时处理
     */
    @GetMapping("/admin/session-timeout")
    public String sessionTimeout(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "会话已超时，请重新登录");
        return "redirect:/admin/login";
    }

    /**
     * 403错误页面
     */
    @GetMapping("/admin/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("pageTitle", "访问被拒绝");
        return "admin/access-denied";
    }
}
