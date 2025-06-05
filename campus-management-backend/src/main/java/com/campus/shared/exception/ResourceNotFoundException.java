package com.campus.shared.exception;

/**
 * 资源未找到异常
 * 
 * @author campus
 * @since 1.0.0
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static ResourceNotFoundException of(String resourceType, Object id) {
        return new ResourceNotFoundException(String.format("%s [%s] 不存在", resourceType, id));
    }
}