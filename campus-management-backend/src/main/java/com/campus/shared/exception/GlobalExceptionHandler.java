package com.campus.shared.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.campus.shared.common.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

/**
 * 全局异常处理器 - 完全前后端分离版本
 * 统一处理系统中的各种异常，返回标准的JSON响应
 *
 * 重构说明：
 * - 增强异常处理覆盖范围
 * - 统一API响应格式
 * - 完善日志记录
 * - 支持开发/生产环境差异化处理
 *
 * @author Campus Management Team
 * @version 2.0.0
 * @since 2025-06-17 (重构版本)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        logger.warn("参数验证失败: {}", ex.getMessage());

        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        String message = "参数验证失败: " + String.join(", ", errors);
        ApiResponse<Object> response = ApiResponse.badRequest(message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Object>> handleBindException(
            BindException ex, HttpServletRequest request) {

        logger.warn("数据绑定失败: {}", ex.getMessage());

        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }

        String message = "数据绑定失败: " + String.join(", ", errors);
        ApiResponse<Object> response = ApiResponse.badRequest(message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {

        logger.warn("约束验证失败: {}", ex.getMessage());

        List<String> errors = new ArrayList<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            errors.add(violation.getPropertyPath() + ": " + violation.getMessage());
        }

        String message = "约束验证失败: " + String.join(", ", errors);
        ApiResponse<Object> response = ApiResponse.badRequest(message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        logger.warn("参数类型不匹配: {}", ex.getMessage());

        Class<?> requiredType = ex.getRequiredType();
        String typeName = (requiredType != null) ? requiredType.getSimpleName() : "未知";
        String message = String.format("参数 '%s' 类型不匹配，期望类型: %s",
                ex.getName(), typeName);
        ApiResponse<Object> response = ApiResponse.badRequest(message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理认证失败异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(
            BadCredentialsException ex, HttpServletRequest request) {

        logger.warn("认证失败: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.unauthorized("用户名或密码错误");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 处理访问拒绝异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {

        logger.warn("访问被拒绝: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.forbidden("没有权限访问此资源");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {

        logger.warn("业务异常: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理资源未找到异常
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {

        logger.warn("资源未找到: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.notFound(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 处理JWT异常
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleJwtException(
            JwtException ex, HttpServletRequest request) {

        logger.warn("JWT异常 - URL: {}, 消息: {}", request.getRequestURI(), ex.getMessage());
        
        ApiResponse<Object> response = ApiResponse.unauthorized("令牌验证失败: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * 处理数据库异常
     */
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ApiResponse<Object>> handleDatabaseException(
            DatabaseException ex, HttpServletRequest request) {

        logger.error("数据库异常 - URL: {}, 消息: {}", request.getRequestURI(), ex.getMessage(), ex);
        
        ApiResponse<Object> response = ApiResponse.error("数据库操作失败，请稍后重试");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 处理通用异常 - 兜底处理
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(
            Exception ex, HttpServletRequest request) {

        logger.error("未知异常 - URL: {}, 消息: {}", request.getRequestURI(), ex.getMessage(), ex);
        
        // 在生产环境中隐藏具体错误信息
        String message = "系统内部错误，请稍后重试";
        if (logger.isDebugEnabled()) {
            message += " (详情: " + ex.getMessage() + ")";
        }
        
        ApiResponse<Object> response = ApiResponse.error(message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {

        logger.warn("非法参数: {}", ex.getMessage());

        ApiResponse<Object> response = ApiResponse.badRequest("参数错误: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Object>> handleNullPointerException(
            NullPointerException ex, HttpServletRequest request) {

        logger.error("空指针异常: ", ex);

        ApiResponse<Object> response = ApiResponse.error("系统内部错误，请联系管理员");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {

        logger.error("运行时异常: ", ex);

        ApiResponse<Object> response = ApiResponse.error("操作失败: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }



    // 注意：BusinessException 和 ResourceNotFoundException 已在独立文件中定义
    // 不再在此处重复定义，避免类冲突
}
