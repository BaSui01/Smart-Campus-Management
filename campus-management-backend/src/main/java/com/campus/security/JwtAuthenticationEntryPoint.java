package com.campus.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT认证入口点
 * 处理未认证的请求
 *
 * @author campus
 * @since 2025-06-04
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {

        // 设置响应状态码为401未授权
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        // 返回JSON格式的错误信息
        String jsonResponse = "{\"code\": 401, \"message\": \"未授权访问，请先登录\", \"data\": null}";
        response.getWriter().write(jsonResponse);
    }
}
