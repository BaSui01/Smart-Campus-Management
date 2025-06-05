package com.campus.shared.common;

/**
 * 响应状态码枚举
 * 
 * @author campus
 * @since 1.0.0
 */
public enum ResponseCode {
    
    // 成功状态码 (2xx)
    SUCCESS(200, "操作成功"),
    CREATED(201, "创建成功"),
    
    // 客户端错误状态码 (4xx)
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    
    // 服务器错误状态码 (5xx)
    ERROR(500, "系统内部错误"),
    
    // 业务状态码 (1xxx)
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    USERNAME_ALREADY_EXISTS(1003, "用户名已存在"),
    EMAIL_ALREADY_EXISTS(1004, "邮箱已存在"),
    INVALID_PASSWORD(1005, "密码错误"),
    TOKEN_INVALID(1101, "Token无效"),
    TOKEN_EXPIRED(1102, "Token已过期"),
    TOKEN_MISSING(1103, "Token缺失");
    
    private final int code;
    private final String message;
    
    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    @Override
    public String toString() {
        return String.format("[%d] %s", code, message);
    }
}