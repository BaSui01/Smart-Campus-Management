package com.campus.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * API限流过滤器
 * 基于令牌桶算法实现API访问频率限制
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-18
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    @Autowired
    @SuppressWarnings("unused")
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // 本地缓存，用于存储令牌桶
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

    // 不同API的限流配置
    private static final Map<String, RateLimitConfig> RATE_LIMIT_CONFIGS = new HashMap<>();

    static {
        // 登录API - 严格限制
        RATE_LIMIT_CONFIGS.put("/api/v1/auth/login", new RateLimitConfig(5, Duration.ofMinutes(1)));
        
        // 注册API - 严格限制
        RATE_LIMIT_CONFIGS.put("/api/v1/auth/register", new RateLimitConfig(3, Duration.ofMinutes(5)));
        
        // 一般API - 中等限制
        RATE_LIMIT_CONFIGS.put("/api/v1/", new RateLimitConfig(100, Duration.ofMinutes(1)));
        
        // 管理员API - 宽松限制
        RATE_LIMIT_CONFIGS.put("/api/v1/admin/", new RateLimitConfig(200, Duration.ofMinutes(1)));
        
        // 文件上传API - 严格限制
        RATE_LIMIT_CONFIGS.put("/api/v1/upload", new RateLimitConfig(10, Duration.ofMinutes(1)));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String clientIp = getClientIpAddress(request);
        
        // 检查是否需要限流
        RateLimitConfig config = getRateLimitConfig(requestURI);
        if (config == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 生成限流键
        String rateLimitKey = generateRateLimitKey(clientIp, requestURI);
        
        // 获取或创建令牌桶
        Bucket bucket = getBucket(rateLimitKey, config);
        
        // 尝试消费令牌
        if (bucket.tryConsume(1)) {
            // 添加限流信息到响应头
            addRateLimitHeaders(response, bucket);
            filterChain.doFilter(request, response);
        } else {
            // 限流触发，记录日志并返回错误
            logger.warn("Rate limit exceeded for IP: {} on URI: {}", clientIp, requestURI);
            handleRateLimitExceeded(response, bucket);
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * 获取请求URI对应的限流配置
     */
    private RateLimitConfig getRateLimitConfig(String requestURI) {
        // 精确匹配
        if (RATE_LIMIT_CONFIGS.containsKey(requestURI)) {
            return RATE_LIMIT_CONFIGS.get(requestURI);
        }
        
        // 前缀匹配
        for (Map.Entry<String, RateLimitConfig> entry : RATE_LIMIT_CONFIGS.entrySet()) {
            if (requestURI.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        return null;
    }

    /**
     * 生成限流键
     */
    private String generateRateLimitKey(String clientIp, String requestURI) {
        return "rate_limit:" + clientIp + ":" + requestURI.replaceAll("/", "_");
    }

    /**
     * 获取或创建令牌桶
     */
    @SuppressWarnings("deprecation")
    private Bucket getBucket(String key, RateLimitConfig config) {
        return bucketCache.computeIfAbsent(key, k -> {
            Bandwidth limit = Bandwidth.classic(config.getCapacity(),
                Refill.intervally(config.getCapacity(), config.getRefillPeriod()));
            return Bucket4j.builder()
                .addLimit(limit)
                .build();
        });
    }

    /**
     * 添加限流信息到响应头
     */
    private void addRateLimitHeaders(HttpServletResponse response, Bucket bucket) {
        response.setHeader("X-RateLimit-Remaining", String.valueOf(bucket.getAvailableTokens()));
        response.setHeader("X-RateLimit-Retry-After", "60");
    }

    /**
     * 处理限流超出情况
     */
    private void handleRateLimitExceeded(HttpServletResponse response, Bucket bucket) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Rate limit exceeded");
        errorResponse.put("message", "请求过于频繁，请稍后再试");
        errorResponse.put("code", 429);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * 限流配置类
     */
    private static class RateLimitConfig {
        private final long capacity;
        private final Duration refillPeriod;

        public RateLimitConfig(long capacity, Duration refillPeriod) {
            this.capacity = capacity;
            this.refillPeriod = refillPeriod;
        }

        public long getCapacity() {
            return capacity;
        }

        public Duration getRefillPeriod() {
            return refillPeriod;
        }
    }
}
