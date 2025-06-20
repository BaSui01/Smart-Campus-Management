package com.campus.shared.exception;

/**
 * JWT相关异常
 * 
 * @author campus
 * @since 1.0.0
 */
public class JwtException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JwtException(String message) {
        super(message);
    }
    
    public JwtException(String message, Throwable cause) {
        super(message, cause);
    }
}