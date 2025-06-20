package com.campus.infrastructure.interceptor;

import com.campus.infrastructure.config.MonitoringConfig;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 监控指标拦截器
 * 自动收集API请求的监控指标
 * 
 * @author Campus Management Team
 * @since 2025-06-20
 */
@Component
public class MetricsInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(MetricsInterceptor.class);

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private MonitoringConfig monitoringConfig;

    private static final String START_TIME_ATTRIBUTE = "startTime";

    @Override
    public boolean preHandle(@org.springframework.lang.NonNull HttpServletRequest request, @org.springframework.lang.NonNull HttpServletResponse response, @org.springframework.lang.NonNull Object handler) throws Exception {
        // 记录请求开始时间
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        
        // 增加API请求计数
        monitoringConfig.incrementApiRequest();
        
        // 记录请求信息
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String userAgent = request.getHeader("User-Agent");
        String clientIp = getClientIpAddress(request);
        
        logger.debug("API请求开始: {} {} from {} ({})", method, uri, clientIp, userAgent);
        
        // 按HTTP方法统计请求
        Counter.builder("campus.api.request.method")
            .description("按HTTP方法统计的API请求")
            .tag("method", method)
            .register(meterRegistry)
            .increment();
        
        // 按端点统计请求
        Counter.builder("campus.api.request.endpoint")
            .description("按端点统计的API请求")
            .tag("endpoint", uri)
            .tag("method", method)
            .register(meterRegistry)
            .increment();
        
        return true;
    }

    @Override
    public void afterCompletion(@org.springframework.lang.NonNull HttpServletRequest request, @org.springframework.lang.NonNull HttpServletResponse response, @org.springframework.lang.NonNull Object handler, @org.springframework.lang.Nullable Exception ex) throws Exception {
        // 计算请求处理时间
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            
            String method = request.getMethod();
            String uri = request.getRequestURI();
            int status = response.getStatus();
            
            // 记录响应时间
            Timer.builder("campus.api.response.time")
                .description("API响应时间")
                .tag("method", method)
                .tag("endpoint", uri)
                .tag("status", String.valueOf(status))
                .register(meterRegistry)
                .record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
            
            // 按状态码统计响应
            Counter.builder("campus.api.response.status")
                .description("按状态码统计的API响应")
                .tag("status", String.valueOf(status))
                .tag("method", method)
                .register(meterRegistry)
                .increment();
            
            // 记录错误请求
            if (status >= 400) {
                monitoringConfig.incrementBusinessError();
                
                Counter.builder("campus.api.error")
                    .description("API错误请求")
                    .tag("status", String.valueOf(status))
                    .tag("endpoint", uri)
                    .tag("method", method)
                    .register(meterRegistry)
                    .increment();
                
                logger.warn("API请求错误: {} {} 返回状态码 {} (耗时: {}ms)", method, uri, status, duration);
            } else {
                logger.debug("API请求完成: {} {} 返回状态码 {} (耗时: {}ms)", method, uri, status, duration);
            }
            
            // 记录慢请求
            if (duration > 1000) { // 超过1秒的请求
                Counter.builder("campus.api.slow.request")
                    .description("慢API请求")
                    .tag("endpoint", uri)
                    .tag("method", method)
                    .register(meterRegistry)
                    .increment();
                
                logger.warn("慢API请求: {} {} 耗时 {}ms", method, uri, duration);
            }
            
            // 异常处理
            if (ex != null) {
                Counter.builder("campus.api.exception")
                    .description("API异常")
                    .tag("exception", ex.getClass().getSimpleName())
                    .tag("endpoint", uri)
                    .tag("method", method)
                    .register(meterRegistry)
                    .increment();
                
                logger.error("API请求异常: {} {} - {}", method, uri, ex.getMessage(), ex);
            }
        }
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        String xForwardedProto = request.getHeader("X-Forwarded-Proto");
        if (xForwardedProto != null && !xForwardedProto.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedProto)) {
            return xForwardedProto;
        }
        
        String proxyClientIp = request.getHeader("Proxy-Client-IP");
        if (proxyClientIp != null && !proxyClientIp.isEmpty() && !"unknown".equalsIgnoreCase(proxyClientIp)) {
            return proxyClientIp;
        }
        
        String wlProxyClientIp = request.getHeader("WL-Proxy-Client-IP");
        if (wlProxyClientIp != null && !wlProxyClientIp.isEmpty() && !"unknown".equalsIgnoreCase(wlProxyClientIp)) {
            return wlProxyClientIp;
        }
        
        String httpClientIp = request.getHeader("HTTP_CLIENT_IP");
        if (httpClientIp != null && !httpClientIp.isEmpty() && !"unknown".equalsIgnoreCase(httpClientIp)) {
            return httpClientIp;
        }
        
        String httpXForwardedFor = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (httpXForwardedFor != null && !httpXForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(httpXForwardedFor)) {
            return httpXForwardedFor;
        }
        
        return request.getRemoteAddr();
    }
}
