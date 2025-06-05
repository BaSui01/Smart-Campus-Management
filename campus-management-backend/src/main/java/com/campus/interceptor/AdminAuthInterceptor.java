package com.campus.interceptor;

import com.campus.entity.User;
import com.campus.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理后台权限拦截器
 * 确保只有管理员可以访问后台管理功能
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminAuthInterceptor.class);
    
    @Autowired
    private UserService userService;
    
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        
        // 排除不需要认证的路径
        if (isExcludedPath(requestURI)) {
            return true;
        }
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            logger.warn("访问管理后台但未登录: {}", requestURI);
            redirectToLogin(request, response);
            return false;
        }
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            logger.warn("Session中无用户信息: {}", requestURI);
            redirectToLogin(request, response);
            return false;
        }

        logger.info("拦截器检查用户: {} 访问: {}", currentUser.getUsername(), requestURI);
        
        // 检查用户状态
        if (currentUser.getStatus() != 1) {
            logger.warn("用户已被禁用尝试访问管理后台: {}", currentUser.getUsername());
            session.invalidate();
            redirectToLogin(request, response);
            return false;
        }
        
        // 检查用户是否有管理员权限
        boolean hasAdminRole = userService.hasRole(currentUser.getId(), "ADMIN") ||
                              userService.hasRole(currentUser.getId(), "TEACHER");
        
        if (!hasAdminRole) {
            logger.warn("无管理权限用户尝试访问管理后台: {}", currentUser.getUsername());
            response.sendRedirect("/admin/access-denied");
            return false;
        }
        
        // 更新最后访问时间
        session.setAttribute("lastAccessTime", System.currentTimeMillis());
        
        // 检查会话超时（30分钟）
        Long loginTime = (Long) session.getAttribute("loginTime");
        if (loginTime != null) {
            long sessionTimeout = 30 * 60 * 1000; // 30分钟
            if (System.currentTimeMillis() - loginTime > sessionTimeout) {
                logger.info("用户会话超时: {}", currentUser.getUsername());
                session.invalidate();
                redirectToLogin(request, response);
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 判断是否为排除路径
     */
    private boolean isExcludedPath(String requestURI) {
        String[] excludedPaths = {
            "/admin/login",
            "/admin/logout", 
            "/admin/captcha",
            "/admin/test",
            "/admin/check-login",
            "/admin/access-denied",
            "/admin/session-timeout",
            "/css/",
            "/js/",
            "/images/",
            "/favicon.ico"
        };
        
        for (String path : excludedPaths) {
            if (requestURI.startsWith(path)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 重定向到登录页面
     */
    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        
        // 构建重定向URL，保存原始请求路径
        StringBuilder redirectUrl = new StringBuilder("/admin/login");
        
        if (!"/admin/dashboard".equals(requestURI)) {
            redirectUrl.append("?redirect=").append(requestURI);
            if (queryString != null) {
                redirectUrl.append("?").append(queryString);
            }
        }
        
        response.sendRedirect(redirectUrl.toString());
    }
}
