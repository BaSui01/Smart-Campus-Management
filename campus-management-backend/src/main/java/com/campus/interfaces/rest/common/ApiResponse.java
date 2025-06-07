package com.campus.interfaces.rest.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * API统一响应格式
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 是否成功
     */
    private Boolean success;

    // ================================
    // 构造函数
    // ================================

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(Integer code, String message, T data, Boolean success) {
        this();
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }

    // ================================
    // 静态工厂方法
    // ================================

    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "操作成功", null, true);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "操作成功", data, true);
    }

    /**
     * 成功响应（带数据和消息）
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(200, message, data, true);
    }

    /**
     * 成功响应（仅消息）
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(200, message, null, true);
    }

    /**
     * 失败响应（默认错误码）
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null, false);
    }

    /**
     * 失败响应（自定义错误码）
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null, false);
    }

    /**
     * 失败响应（带数据）
     */
    public static <T> ApiResponse<T> error(Integer code, String message, T data) {
        return new ApiResponse<>(code, message, data, false);
    }

    /**
     * 参数错误响应
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(400, message, null, false);
    }

    /**
     * 未授权响应
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(401, message != null ? message : "未授权访问", null, false);
    }

    /**
     * 禁止访问响应
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(403, message != null ? message : "禁止访问", null, false);
    }

    /**
     * 资源不存在响应
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(404, message != null ? message : "资源不存在", null, false);
    }

    /**
     * 服务器内部错误响应
     */
    public static <T> ApiResponse<T> internalError(String message) {
        return new ApiResponse<>(500, message != null ? message : "服务器内部错误", null, false);
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 检查是否成功
     */
    public boolean isSuccess() {
        return success != null && success && code != null && code == 200;
    }

    /**
     * 检查是否失败
     */
    public boolean isError() {
        return !isSuccess();
    }

    /**
     * 获取错误码
     */
    public Integer getErrorCode() {
        return isError() ? code : null;
    }

    /**
     * 获取错误消息
     */
    public String getErrorMessage() {
        return isError() ? message : null;
    }

    /**
     * 设置额外信息
     */
    public ApiResponse<T> withMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * 设置数据
     */
    public ApiResponse<T> withData(T data) {
        this.data = data;
        return this;
    }

    /**
     * 设置状态码
     */
    public ApiResponse<T> withCode(Integer code) {
        this.code = code;
        return this;
    }

    // ================================
    // Getter and Setter methods
    // ================================

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    // ================================
    // toString方法
    // ================================

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp=" + timestamp +
                ", success=" + success +
                '}';
    }
}
