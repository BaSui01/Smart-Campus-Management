package com.campus.shared.security;

import com.campus.shared.exception.RateLimitException;
import com.campus.shared.util.SecurityUtil;
import com.campus.shared.util.WebUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * 限流切面
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Lua脚本：实现滑动窗口限流
     */
    private static final String RATE_LIMIT_SCRIPT = """
            local key = KEYS[1]
            local window = tonumber(ARGV[1])
            local limit = tonumber(ARGV[2])
            local current = tonumber(ARGV[3])
            
            -- 清理过期的记录
            redis.call('zremrangebyscore', key, '-inf', current - window * 1000)
            
            -- 获取当前窗口内的请求数量
            local count = redis.call('zcard', key)
            
            if count < limit then
                -- 添加当前请求
                redis.call('zadd', key, current, current)
                redis.call('expire', key, window)
                return {1, limit - count - 1}
            else
                return {0, 0}
            end
            """;

    @SuppressWarnings("unchecked")
    private final DefaultRedisScript<List<Long>> rateLimitScript = new DefaultRedisScript<>(RATE_LIMIT_SCRIPT, (Class<List<Long>>) (Class<?>) List.class);

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint point, RateLimit rateLimit) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        
        // 如果方法上没有注解，检查类上的注解
        if (rateLimit == null) {
            rateLimit = AnnotationUtils.findAnnotation(method.getDeclaringClass(), RateLimit.class);
        }
        
        if (rateLimit == null) {
            return point.proceed();
        }

        String key = generateKey(point, rateLimit);
        
        if (isExceedLimit(key, rateLimit)) {
            log.warn("API限流触发: key={}, limit={}/{}", key, rateLimit.count(), rateLimit.time());
            throw new RateLimitException(rateLimit.message());
        }

        return point.proceed();
    }

    /**
     * 生成限流key
     */
    private String generateKey(ProceedingJoinPoint point, RateLimit rateLimit) {
        StringBuilder keyBuilder = new StringBuilder("rate_limit:");
        
        switch (rateLimit.limitType()) {
            case IP:
                keyBuilder.append("ip:").append(WebUtil.getClientIp());
                break;
            case USER:
                Long userId = SecurityUtil.getCurrentUserId();
                keyBuilder.append("user:").append(userId != null ? userId : "anonymous");
                break;
            case CUSTOM:
                if (StringUtils.hasText(rateLimit.key())) {
                    keyBuilder.append("custom:").append(rateLimit.key());
                } else {
                    keyBuilder.append("method:").append(point.getSignature().toShortString());
                }
                break;
            default:
                keyBuilder.append("global:").append(point.getSignature().toShortString());
                break;
        }
        
        return keyBuilder.toString();
    }

    /**
     * 检查是否超过限流
     */
    private boolean isExceedLimit(String key, RateLimit rateLimit) {
        try {
            List<Long> result = redisTemplate.execute(
                rateLimitScript,
                Collections.singletonList(key),
                rateLimit.time(),
                rateLimit.count(),
                System.currentTimeMillis()
            );
            
            if (result != null && !result.isEmpty()) {
                Long allowed = result.get(0);
                Long remaining = result.size() > 1 ? result.get(1) : 0L;
                
                log.debug("限流检查: key={}, allowed={}, remaining={}", key, allowed, remaining);
                
                return allowed == 0;
            }
            
            return false;
        } catch (Exception e) {
            log.error("限流检查异常: key={}", key, e);
            // 异常情况下不限流，保证服务可用性
            return false;
        }
    }
}
