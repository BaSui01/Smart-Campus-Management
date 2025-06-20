package com.campus.shared.exception;

import java.util.List;
import java.util.Map;

/**
 * 数据验证异常类
 * 用于处理数据验证失败的异常情况
 * 
 * @author Campus Management Team
 * @since 2025-06-20
 */
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误代码
     */
    private String errorCode;

    /**
     * 验证错误详情
     */
    private transient Map<String, List<String>> validationErrors;

    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public ValidationException(String message) {
        super(message);
        this.errorCode = "VALIDATION_ERROR";
    }

    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param cause 原因
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "VALIDATION_ERROR";
    }

    /**
     * 构造函数
     * 
     * @param errorCode 错误代码
     * @param message 错误消息
     */
    public ValidationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param validationErrors 验证错误详情
     */
    public ValidationException(String message, Map<String, List<String>> validationErrors) {
        super(message);
        this.errorCode = "VALIDATION_ERROR";
        this.validationErrors = validationErrors;
    }

    /**
     * 构造函数
     * 
     * @param errorCode 错误代码
     * @param message 错误消息
     * @param validationErrors 验证错误详情
     */
    public ValidationException(String errorCode, String message, Map<String, List<String>> validationErrors) {
        super(message);
        this.errorCode = errorCode;
        this.validationErrors = validationErrors;
    }

    /**
     * 获取错误代码
     * 
     * @return 错误代码
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 设置错误代码
     * 
     * @param errorCode 错误代码
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 获取验证错误详情
     * 
     * @return 验证错误详情
     */
    public Map<String, List<String>> getValidationErrors() {
        return validationErrors;
    }

    /**
     * 设置验证错误详情
     * 
     * @param validationErrors 验证错误详情
     */
    public void setValidationErrors(Map<String, List<String>> validationErrors) {
        this.validationErrors = validationErrors;
    }

    // 常用验证异常静态方法

    /**
     * 必填字段为空异常
     */
    public static ValidationException requiredFieldEmpty(String fieldName) {
        return new ValidationException("REQUIRED_FIELD_EMPTY", 
            String.format("必填字段 %s 不能为空", fieldName));
    }

    /**
     * 字段格式错误异常
     */
    public static ValidationException invalidFormat(String fieldName, String expectedFormat) {
        return new ValidationException("INVALID_FORMAT", 
            String.format("字段 %s 格式错误，期望格式: %s", fieldName, expectedFormat));
    }

    /**
     * 字段长度超限异常
     */
    public static ValidationException lengthExceeded(String fieldName, int maxLength) {
        return new ValidationException("LENGTH_EXCEEDED", 
            String.format("字段 %s 长度超过限制，最大长度: %d", fieldName, maxLength));
    }

    /**
     * 字段长度不足异常
     */
    public static ValidationException lengthInsufficient(String fieldName, int minLength) {
        return new ValidationException("LENGTH_INSUFFICIENT", 
            String.format("字段 %s 长度不足，最小长度: %d", fieldName, minLength));
    }

    /**
     * 数值超出范围异常
     */
    public static ValidationException valueOutOfRange(String fieldName, Object min, Object max) {
        return new ValidationException("VALUE_OUT_OF_RANGE", 
            String.format("字段 %s 数值超出范围，有效范围: %s - %s", fieldName, min, max));
    }

    /**
     * 邮箱格式错误异常
     */
    public static ValidationException invalidEmail(String email) {
        return new ValidationException("INVALID_EMAIL", 
            String.format("邮箱格式错误: %s", email));
    }

    /**
     * 手机号格式错误异常
     */
    public static ValidationException invalidPhone(String phone) {
        return new ValidationException("INVALID_PHONE", 
            String.format("手机号格式错误: %s", phone));
    }

    /**
     * 密码强度不足异常
     */
    public static ValidationException weakPassword() {
        return new ValidationException("WEAK_PASSWORD", 
            "密码强度不足，密码应包含大小写字母、数字和特殊字符");
    }

    /**
     * 日期格式错误异常
     */
    public static ValidationException invalidDate(String fieldName, String dateValue) {
        return new ValidationException("INVALID_DATE", 
            String.format("字段 %s 日期格式错误: %s", fieldName, dateValue));
    }

    /**
     * 文件类型不支持异常
     */
    public static ValidationException unsupportedFileType(String fileName, String fileType) {
        return new ValidationException("UNSUPPORTED_FILE_TYPE", 
            String.format("文件 %s 类型不支持: %s", fileName, fileType));
    }

    /**
     * 文件大小超限异常
     */
    public static ValidationException fileSizeExceeded(String fileName, long fileSize, long maxSize) {
        return new ValidationException("FILE_SIZE_EXCEEDED", 
            String.format("文件 %s 大小超限，当前大小: %d bytes，最大允许: %d bytes", 
                fileName, fileSize, maxSize));
    }

    /**
     * 重复值异常
     */
    public static ValidationException duplicateValue(String fieldName, Object value) {
        return new ValidationException("DUPLICATE_VALUE", 
            String.format("字段 %s 值重复: %s", fieldName, value));
    }

    /**
     * 引用不存在异常
     */
    public static ValidationException referenceNotFound(String fieldName, Object value) {
        return new ValidationException("REFERENCE_NOT_FOUND", 
            String.format("字段 %s 引用的对象不存在: %s", fieldName, value));
    }
}
