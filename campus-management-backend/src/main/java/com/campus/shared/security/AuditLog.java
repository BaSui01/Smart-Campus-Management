package com.campus.shared.security;

import java.lang.annotation.*;

/**
 * 审计日志注解
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {
    
    /**
     * 操作类型
     */
    String operationType() default "";
    
    /**
     * 操作描述
     */
    String operationDesc() default "";
    
    /**
     * 操作模块
     */
    String module() default "";
    
    /**
     * 业务类型
     */
    String businessType() default "";
    
    /**
     * 风险等级：1-低，2-中，3-高
     */
    int riskLevel() default 1;
    
    /**
     * 是否记录请求参数
     */
    boolean includeParams() default true;
    
    /**
     * 是否记录响应结果
     */
    boolean includeResult() default false;
    
    /**
     * 是否记录操作前后数据变化
     */
    boolean includeDataChange() default false;
}
