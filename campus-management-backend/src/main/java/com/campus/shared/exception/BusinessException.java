package com.campus.shared.exception;

import com.campus.shared.common.ResponseCode;

/**
 * 业务异常
 * 
 * @author campus
 * @since 1.0.0
 */
public class BusinessException extends RuntimeException {
    
    private final int code;
    
    public BusinessException(String message) {
        super(message);
        this.code = ResponseCode.ERROR.getCode();
    }
    
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
    
    public BusinessException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResponseCode.ERROR.getCode();
    }
    
    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    
    public BusinessException(ResponseCode responseCode, Throwable cause) {
        super(responseCode.getMessage(), cause);
        this.code = responseCode.getCode();
    }
    
    public int getCode() {
        return code;
    }
}