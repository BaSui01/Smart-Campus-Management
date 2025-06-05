package com.campus.shared.exception;

/**
 * 数据库异常
 * 
 * @author campus
 * @since 1.0.0
 */
public class DatabaseException extends RuntimeException {
    
    private final int code;
    
    public DatabaseException(String message) {
        super(message);
        this.code = 1701; // 数据库操作失败
    }
    
    public DatabaseException(int code, String message) {
        super(message);
        this.code = code;
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
        this.code = 1701;
    }
    
    public DatabaseException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
}