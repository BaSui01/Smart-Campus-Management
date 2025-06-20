package com.campus.shared.security;

import java.lang.annotation.*;

/**
 * API限流注解
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    
    /**
     * 限流key，支持SpEL表达式
     */
    String key() default "";
    
    /**
     * 限流次数
     */
    int count() default 100;
    
    /**
     * 限流时间窗口（秒）
     */
    int time() default 60;
    
    /**
     * 限流类型
     */
    LimitType limitType() default LimitType.DEFAULT;
    
    /**
     * 限流失败消息
     */
    String message() default "访问过于频繁，请稍后再试";
    
    /**
     * 限流类型枚举
     */
    enum LimitType {
        /**
         * 默认策略全局限流
         */
        DEFAULT,
        
        /**
         * 根据请求者IP进行限流
         */
        IP,
        
        /**
         * 根据用户ID进行限流
         */
        USER,
        
        /**
         * 根据自定义key进行限流
         */
        CUSTOM
    }
}
