package com.campus.shared.security;

import com.campus.shared.util.EncryptionUtil;

import java.lang.annotation.*;

/**
 * 敏感数据注解
 * 用于标记需要加密存储或脱敏显示的字段
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SensitiveData {
    
    /**
     * 敏感数据类型
     */
    EncryptionUtil.SensitiveType type() default EncryptionUtil.SensitiveType.NAME;
    
    /**
     * 是否需要加密存储
     */
    boolean encrypt() default false;
    
    /**
     * 是否需要脱敏显示
     */
    boolean mask() default true;
    
    /**
     * 加密密钥（为空时使用默认密钥）
     */
    String encryptKey() default "";
}
