package com.campus.shared.exception;

/**
 * 加密解密异常
 * 用于处理数据加密和解密过程中的异常
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-18
 */
public class EncryptionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EncryptionException(String message) {
        super(message);
    }

    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public EncryptionException(Throwable cause) {
        super(cause);
    }
}
