package com.campus.shared.exception;

/**
 * 限流异常
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
public class RateLimitException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public RateLimitException() {
        super();
    }
    
    public RateLimitException(String message) {
        super(message);
    }
    
    public RateLimitException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public RateLimitException(Throwable cause) {
        super(cause);
    }
}
