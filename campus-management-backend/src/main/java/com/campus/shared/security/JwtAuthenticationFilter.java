package com.campus.shared.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import com.campus.shared.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT认证过滤器
 * 处理JWT Token的验证和认证
 *
 * @author campus
 * @since 2025-06-04
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 服务器启动时间，用于检测重启前的Token
    private static final long SERVER_START_TIME = System.currentTimeMillis();

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {

        // 获取请求头中的Authorization字段
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 检查Authorization头是否存在且以"Bearer "开头
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // 移除"Bearer "前缀
            try {
                username = jwtUtil.getUsernameFromToken(token);
            } catch (Exception e) {
                logger.debug("JWT Token解析失败: " + e.getMessage());
            }
        }

        // 如果token中包含用户名且当前没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // 检查Token是否在服务器重启之前签发
                if (isTokenIssuedBeforeRestart(token)) {
                    logger.debug("检测到服务器重启前的Token，拒绝API认证");
                    // 继续过滤器链，但不设置认证信息
                    filterChain.doFilter(request, response);
                    return;
                }

                // 加载用户详细信息
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 验证token有效性
                if (jwtUtil.validateToken(token, username)) {
                    // 创建认证token
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );

                    // 设置请求详细信息
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 将认证信息设置到SecurityContext中
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    logger.debug("JWT认证成功: " + username);
                } else {
                    logger.debug("JWT token验证失败: " + username);
                }
            } catch (Exception e) {
                logger.debug("用户认证失败: " + e.getMessage());
            }
        }

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // 跳过不需要JWT验证的路径
        return path.startsWith("/admin/login") ||
               path.startsWith("/admin/captcha") ||
               path.startsWith("/admin/token-status") ||  // 允许获取token状态
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/") ||
               path.startsWith("/v2/api-docs/") ||
               path.startsWith("/swagger-resources/") ||
               path.startsWith("/webjars/") ||
               path.startsWith("/doc.html") ||
               path.startsWith("/static/") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/") ||
               path.startsWith("/fonts/") ||
               path.equals("/favicon.ico") ||
               path.equals("/");
   }
   
   /**
    * 检查Token是否在服务器重启之前签发
    */
   private boolean isTokenIssuedBeforeRestart(String token) {
       try {
           // 从JWT Token中获取签发时间
           java.util.Date issuedAt = jwtUtil.getClaimFromToken(token, io.jsonwebtoken.Claims::getIssuedAt);
           if (issuedAt == null) {
               logger.debug("无法获取API Token签发时间，为安全起见拒绝认证");
               return true; // 无法确定签发时间，为安全起见认为无效
           }
           
           // 如果Token签发时间早于服务器启动时间，则认为是重启前的Token
           boolean isBeforeRestart = issuedAt.getTime() < SERVER_START_TIME;
           if (isBeforeRestart) {
               logger.debug("检测到重启前API Token: 签发时间=" + new java.util.Date(issuedAt.getTime()) +
                   ", 服务器启动时间=" + new java.util.Date(SERVER_START_TIME));
           }
           
           return isBeforeRestart;
       } catch (Exception e) {
           logger.debug("检查API Token签发时间时发生错误: " + e.getMessage());
           return true; // 异常情况下为安全起见认为Token无效
       }
   }
}
