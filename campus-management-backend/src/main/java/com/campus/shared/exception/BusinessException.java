package com.campus.shared.exception;

/**
 * 业务异常类
 * 用于处理业务逻辑相关的异常
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String errorCode;
    private transient Object data; // 标记为 transient，避免序列化问题
    private int statusCode;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = String.valueOf(statusCode);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public BusinessException(String errorCode, String message, Object data) {
        super(message);
        this.errorCode = errorCode;
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}