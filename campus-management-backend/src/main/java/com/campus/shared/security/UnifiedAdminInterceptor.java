package com.campus.shared.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.campus.application.service.auth.UserService;
import com.campus.domain.entity.auth.User;
import com.campus.shared.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 统一管理后台认证拦截器
 * 整合了JWT认证和Session管理，支持自动刷新和权限控制
 * 
 * @author campus
 * @since 2.0.0
 */
@Component
public class UnifiedAdminInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(UnifiedAdminInterceptor.class);
    
    // 服务器启动时间，用于检测重启前的Token
    private static final long SERVER_START_TIME = System.currentTimeMillis();
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    @Lazy
    private UserService userService;
    
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        
        // 排除不需要认证的路径
        if (isExcludedPath(requestURI)) {
            return true;
        }
        
        // 尝试获取并验证Token
        String token = extractToken(request);
        if (token == null) {
            logger.warn("访问管理后台但未提供有效Token: {}", requestURI);
            redirectToLogin(request, response);
            return false;
        }
        
        try {
            // 验证Token有效性
            if (!jwtUtil.validateToken(token)) {
                logger.warn("Token已过期或无效: {}", requestURI);
                clearSessionAndRedirect(request, response);
                return false;
            }
            
            // 检查Token类型和重启状态
            if (!isValidAdminToken(token, requestURI)) {
                clearSessionAndRedirect(request, response);
                return false;
            }
            
            // 获取用户信息并验证
            UserInfo userInfo = extractUserInfo(token);
            if (userInfo == null) {
                logger.warn("Token中缺少用户信息: {}", requestURI);
                clearSessionAndRedirect(request, response);
                return false;
            }
            
            // 验证用户状态和权限
            if (!validateUserAccess(userInfo, requestURI, response)) {
                return false;
            }
            
            // 处理Token自动刷新
            handleTokenRefresh(token, request, response, userInfo.username);
            
            // 设置用户上下文
            setUserContext(request, userInfo);
            
            logger.debug("认证成功: {} (角色: {}) 访问 {}", userInfo.username, userInfo.role, requestURI);
            return true;
            
        } catch (Exception e) {
            logger.error("认证过程中发生错误: {}", e.getMessage());
            clearSessionAndRedirect(request, response);
            return false;
        }
    }
    
    /**
     * 提取Token（优先级：Session > Header）
     */
    private String extractToken(HttpServletRequest request) {
        // 优先从session中获取
        HttpSession session = request.getSession(false);
        if (session != null) {
            String sessionToken = (String) session.getAttribute("accessToken");
            if (sessionToken != null) {
                return sessionToken;
            }
        }
        
        // 从请求头获取
        String authHeader = request.getHeader("Authorization");
        return jwtUtil.extractTokenFromHeader(authHeader);
    }
    
    /**
     * 验证是否为有效的管理后台Token
     */
    private boolean isValidAdminToken(String token, String requestURI) {
        // 检查是否为管理后台token
        if (!jwtUtil.isAdminToken(token)) {
            logger.warn("非管理后台Token尝试访问: {}", requestURI);
            return false;
        }
        
        // 检查Token是否在服务器重启之前签发
        if (isTokenIssuedBeforeRestart(token)) {
            logger.warn("检测到服务器重启前的Token，强制重新登录: {}", requestURI);
            return false;
        }
        
        return true;
    }
    
    /**
     * 提取用户信息
     */
    private UserInfo extractUserInfo(String token) {
        try {
            String username = jwtUtil.getUsernameFromToken(token);
            Long userId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            
            if (username == null || userId == null) {
                return null;
            }
            
            return new UserInfo(userId, username, role);
        } catch (Exception e) {
            logger.error("提取用户信息失败", e);
            return null;
        }
    }
    
    /**
     * 验证用户访问权限
     */
    private boolean validateUserAccess(UserInfo userInfo, String requestURI, HttpServletResponse response) throws Exception {
        // 验证用户是否存在且有效
        User user = userService.findUserById(userInfo.userId);
        if (user == null || user.getStatus() != 1) {
            logger.warn("用户不存在或已被禁用: {}", userInfo.username);
            return false;
        }
        
        // 检查管理权限
        if (!hasAdminRole(userInfo.userId)) {
            logger.warn("无管理权限用户尝试访问管理后台: {}", userInfo.username);
            response.sendRedirect("/admin/access-denied");
            return false;
        }
        
        // 检查具体页面权限
        if (!userService.hasMenuPermission(userInfo.userId, requestURI)) {
            logger.warn("用户 {} 无权限访问页面: {}", userInfo.username, requestURI);
            response.sendRedirect("/admin/access-denied");
            return false;
        }
        
        // 更新userInfo中的用户对象
        userInfo.user = user;
        return true;
    }
    
    /**
     * 检查是否具有管理权限
     */
    private boolean hasAdminRole(Long userId) {
        return userService.hasRole(userId, "ROLE_SUPER_ADMIN") ||
               userService.hasRole(userId, "ROLE_ADMIN") ||
               userService.hasRole(userId, "ROLE_SYSTEM_ADMIN") ||
               userService.hasRole(userId, "ROLE_ACADEMIC_ADMIN") ||
               userService.hasRole(userId, "ROLE_FINANCE_ADMIN") ||
               userService.hasRole(userId, "ROLE_TEACHER");
    }
    
    /**
     * 处理Token自动刷新
     */
    private void handleTokenRefresh(String token, HttpServletRequest request, HttpServletResponse response, String username) {
        if (jwtUtil.shouldRefreshAdminToken(token)) {
            try {
                String newToken = jwtUtil.refreshToken(token);
                
                // 更新Session中的token
                HttpSession session = request.getSession(false);
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
    }
    
    /**
     * 设置用户上下文
     */
    private void setUserContext(HttpServletRequest request, UserInfo userInfo) {
        // 设置请求属性
        request.setAttribute("currentUser", userInfo.user);
        request.setAttribute("currentUserId", userInfo.userId);
        request.setAttribute("currentUsername", userInfo.username);
        request.setAttribute("currentUserRole", userInfo.role);
        
        // 设置Session属性（供模板使用）
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute("user", userInfo.user);
            session.setAttribute("userRole", userInfo.role);
            session.setAttribute("username", userInfo.username);
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
    
    /**
     * 检查Token是否在服务器重启之前签发
     */
    private boolean isTokenIssuedBeforeRestart(String token) {
        try {
            java.util.Date issuedAt = jwtUtil.getClaimFromToken(token, io.jsonwebtoken.Claims::getIssuedAt);
            if (issuedAt == null) {
                logger.warn("无法获取Token签发时间，为安全起见拒绝访问");
                return true;
            }
            
            boolean isBeforeRestart = issuedAt.getTime() < SERVER_START_TIME;
            if (isBeforeRestart) {
                logger.info("检测到重启前Token: 签发时间={}, 服务器启动时间={}",
                    new java.util.Date(issuedAt.getTime()),
                    new java.util.Date(SERVER_START_TIME));
            }
            
            return isBeforeRestart;
        } catch (Exception e) {
            logger.error("检查Token签发时间时发生错误: {}", e.getMessage());
            return true;
        }
    }
    
    /**
     * 用户信息内部类
     */
    private static class UserInfo {
        Long userId;
        String username;
        String role;
        User user;
        
        UserInfo(Long userId, String username, String role) {
            this.userId = userId;
            this.username = username;
            this.role = role;
        }
    }
}