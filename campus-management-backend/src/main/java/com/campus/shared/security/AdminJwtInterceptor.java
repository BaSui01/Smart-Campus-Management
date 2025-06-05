package com.campus.shared.security;

import com.campus.application.service.UserService;
import com.campus.domain.entity.User;
import com.campus.shared.util.JwtUtil;

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
 * 管理后台JWT认证拦截器
 * 使用JWT Token进行认证，支持自动刷新
 */
@Component
public class AdminJwtInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminJwtInterceptor.class);
    
    @Autowired
    private JwtUtil jwtUtil;
    
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
        String token = null;
        
        // 优先从session中获取token
        if (session != null) {
            token = (String) session.getAttribute("accessToken");
        }
        
        // 如果session中没有token，尝试从请求头获取
        if (token == null) {
            String authHeader = request.getHeader("Authorization");
            token = jwtUtil.extractTokenFromHeader(authHeader);
        }
        
        // 如果没有token，重定向到登录页面
        if (token == null) {
            logger.warn("访问管理后台但未提供JWT Token: {}", requestURI);
            redirectToLogin(request, response);
            return false;
        }
        
        try {
            // 验证token有效性
            if (!jwtUtil.validateToken(token)) {
                logger.warn("JWT Token已过期或无效: {}", requestURI);
                clearSessionAndRedirect(request, response);
                return false;
            }
            
            // 检查是否为管理后台token
            if (!jwtUtil.isAdminToken(token)) {
                logger.warn("非管理后台Token尝试访问: {}", requestURI);
                clearSessionAndRedirect(request, response);
                return false;
            }
            
            // 从token中获取用户信息
            String username = jwtUtil.getUsernameFromToken(token);
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            
            if (username == null || userId == null) {
                logger.warn("Token中缺少用户信息: {}", requestURI);
                clearSessionAndRedirect(request, response);
                return false;
            }
            
            // 验证用户是否存在且有效
            User user = userService.findUserById(userId);
            if (user == null || user.getStatus() != 1) {
                logger.warn("用户不存在或已被禁用: {}", username);
                clearSessionAndRedirect(request, response);
                return false;
            }
            
            // 检查用户权限
            boolean hasAdminRole = userService.hasRole(userId, "ADMIN") ||
                                  userService.hasRole(userId, "SYSTEM_ADMIN") ||
                                  userService.hasRole(userId, "ACADEMIC_ADMIN") ||
                                  userService.hasRole(userId, "FINANCE_ADMIN") ||
                                  userService.hasRole(userId, "TEACHER");
            if (!hasAdminRole) {
                logger.warn("无管理权限用户尝试访问管理后台: {}", username);
                response.sendRedirect("/admin/access-denied");
                return false;
            }
            
            // 检查是否需要刷新token
            if (jwtUtil.shouldRefreshAdminToken(token)) {
                try {
                    String newToken = jwtUtil.refreshToken(token);
                    if (session != null) {
                        session.setAttribute("accessToken", newToken);
                    }
                    // 在响应头中返回新token
                    response.setHeader("X-New-Token", newToken);
                    logger.info("自动刷新用户Token: {}", username);
                } catch (Exception e) {
                    logger.warn("Token刷新失败: {}", e.getMessage());
                }
            }
            
            // 将用户信息设置到请求属性中
            request.setAttribute("currentUser", user);
            request.setAttribute("currentUserId", userId);
            request.setAttribute("currentUsername", username);
            request.setAttribute("currentUserRole", role);
            
            logger.debug("JWT认证成功: {} 访问 {}", username, requestURI);
            return true;
            
        } catch (Exception e) {
            logger.error("JWT认证过程中发生错误: {}", e.getMessage());
            clearSessionAndRedirect(request, response);
            return false;
        }
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
     * 清除session并重定向到登录页面
     */
    private void clearSessionAndRedirect(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        redirectToLogin(request, response);
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
