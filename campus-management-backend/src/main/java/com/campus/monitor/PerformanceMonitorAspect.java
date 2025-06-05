package com.campus.monitor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 应用性能监控切面
 * 
 * @author Campus Team
 * @since 2025-06-05
 */
@Aspect
@Component
@ConditionalOnProperty(name = "campus.monitor.enabled", havingValue = "true", matchIfMissing = true)
public class PerformanceMonitorAspect {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitorAspect.class);

    @Autowired
    private MeterRegistry meterRegistry;

    @Value("${campus.monitor.performance-log:false}")
    private boolean performanceLogEnabled;

    // 缓存 Timer 和 Counter 实例
    private final ConcurrentMap<String, Timer> timers = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Counter> counters = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Counter> errorCounters = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        logger.info("性能监控切面初始化完成，性能日志: {}", performanceLogEnabled ? "启用" : "禁用");
    }

    /**
     * 监控缓存方法执行
     */
    @Around("@annotation(org.springframework.cache.annotation.Cacheable)")
    public Object monitorCacheableMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorMethod(joinPoint, "cacheable");
    }

    /**
     * 监控缓存清除方法执行
     */
    @Around("@annotation(org.springframework.cache.annotation.CacheEvict)")
    public Object monitorCacheEvictMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorMethod(joinPoint, "cache_evict");
    }

    /**
     * 监控缓存更新方法执行
     */
    @Around("@annotation(org.springframework.cache.annotation.CachePut)")
    public Object monitorCachePutMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorMethod(joinPoint, "cache_put");
    }

    /**
     * 监控 Service 层方法执行
     */
    @Around("execution(* com.campus.service.impl.*.*(..))")
    public Object monitorServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorMethod(joinPoint, "service");
    }

    /**
     * 监控 Controller 层方法执行
     */
    @Around("execution(* com.campus.controller.*.*(..))")
    public Object monitorControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorMethod(joinPoint, "controller");
    }

    /**
     * 监控 Repository 层方法执行
     */
    @Around("execution(* com.campus.repository.*.*(..))")
    public Object monitorRepositoryMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorMethod(joinPoint, "repository");
    }

    /**
     * 通用方法监控
     */
    private Object monitorMethod(ProceedingJoinPoint joinPoint, String methodType) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String fullMethodName = className + "." + methodName;
        
        // 获取或创建 Timer
        Timer timer = timers.computeIfAbsent(
            methodType + "." + fullMethodName,
            key -> Timer.builder("method.execution.time")
                .description("Method execution time")
                .tag("type", methodType)
                .tag("class", className)
                .tag("method", methodName)
                .register(meterRegistry)
        );

        // 获取或创建 Counter
        Counter counter = counters.computeIfAbsent(
            methodType + "." + fullMethodName,
            key -> Counter.builder("method.execution.count")
                .description("Method execution count")
                .tag("type", methodType)
                .tag("class", className)
                .tag("method", methodName)
                .register(meterRegistry)
        );

        // 获取或创建错误 Counter
        Counter errorCounter = errorCounters.computeIfAbsent(
            methodType + "." + fullMethodName,
            key -> Counter.builder("method.execution.error")
                .description("Method execution error count")
                .tag("type", methodType)
                .tag("class", className)
                .tag("method", methodName)
                .register(meterRegistry)
        );

        Timer.Sample sample = Timer.start(meterRegistry);
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            
            // 记录成功执行
            counter.increment();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            if (performanceLogEnabled) {
                if (duration > getSlowThreshold(methodType)) {
                    logger.warn("慢方法执行 - 类型: {}, 方法: {}, 耗时: {}ms", 
                               methodType, fullMethodName, duration);
                } else {
                    logger.debug("方法执行 - 类型: {}, 方法: {}, 耗时: {}ms", 
                                methodType, fullMethodName, duration);
                }
            }
            
            return result;
            
        } catch (Exception e) {
            // 记录错误执行
            errorCounter.increment();
            
            if (performanceLogEnabled) {
                logger.error("方法执行异常 - 类型: {}, 方法: {}, 异常: {}", 
                           methodType, fullMethodName, e.getMessage());
            }
            
            throw e;
        } finally {
            sample.stop(timer);
        }
    }

    /**
     * 获取慢方法阈值
     */
    private long getSlowThreshold(String methodType) {
        switch (methodType) {
            case "controller":
                return 1000; // 1秒
            case "service":
                return 500;  // 500毫秒
            case "repository":
                return 200;  // 200毫秒
            case "cacheable":
            case "cache_evict":
            case "cache_put":
                return 100;  // 100毫秒
            default:
                return 1000; // 默认1秒
        }
    }

    /**
     * 获取性能统计信息
     */
    public PerformanceStats getPerformanceStats() {
        return new PerformanceStats(
            timers.size(),
            counters.values().stream().mapToDouble(Counter::count).sum(),
            errorCounters.values().stream().mapToDouble(Counter::count).sum()
        );
    }

    /**
     * 性能统计数据类
     */
    public static class PerformanceStats {
        private final int monitoredMethods;
        private final double totalExecutions;
        private final double totalErrors;

        public PerformanceStats(int monitoredMethods, double totalExecutions, double totalErrors) {
            this.monitoredMethods = monitoredMethods;
            this.totalExecutions = totalExecutions;
            this.totalErrors = totalErrors;
        }

        public int getMonitoredMethods() { return monitoredMethods; }
        public double getTotalExecutions() { return totalExecutions; }
        public double getTotalErrors() { return totalErrors; }
        public double getErrorRate() { 
            return totalExecutions > 0 ? totalErrors / totalExecutions : 0.0; 
        }
    }
}
