package com.campus.shared.exception;

import com.campus.shared.common.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * API错误处理控制器
 * 处理所有API请求的错误响应，返回统一的JSON格式
 *
 * @author Campus Management Team
 * @version 2.0.0
 * @since 2025-06-17
 */
@RestController
public class ApiExceptionHandler implements ErrorController {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);
    private static final String ERROR_PATH = "/error";

    @RequestMapping(ERROR_PATH)
    public ResponseEntity<ApiResponse<Object>> handleError(HttpServletRequest request) {
        // 获取错误状态码
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        int statusCode = 500; // 默认状态码
        if (status != null) {
            try {
                statusCode = Integer.parseInt(status.toString());
            } catch (NumberFormatException e) {
                log.warn("无法解析状态码: {}", status);
            }
        }

        // 获取请求的URL
        String requestUrl = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        if (requestUrl == null) {
            requestUrl = request.getRequestURI();
        }

        // 获取异常信息
        Throwable exception = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        String errorMessage = getErrorMessage(statusCode);
        
        // 记录错误日志
        if (exception != null) {
            log.error("API请求错误 - URL: {}, 状态码: {}, 异常: {}", 
                     requestUrl, statusCode, exception.getMessage(), exception);
        } else {
            log.warn("API请求错误 - URL: {}, 状态码: {}", requestUrl, statusCode);
        }

        // 构建错误响应
        ApiResponse<Object> errorResponse = ApiResponse.error(statusCode, errorMessage);
        
        // 在开发环境下，可以包含更多调试信息
        if (log.isDebugEnabled() && exception != null) {
            // 可以添加调试信息到响应中
            log.debug("详细异常信息: ", exception);
        }

        return ResponseEntity.status(statusCode).body(errorResponse);
    }

    /**
     * 根据状态码获取错误消息
     */
    private String getErrorMessage(int statusCode) {
        return switch (statusCode) {
            case 400 -> "请求参数错误";
            case 401 -> "未授权访问";
            case 403 -> "禁止访问";
            case 404 -> "资源未找到";
            case 405 -> "请求方法不允许";
            case 415 -> "不支持的媒体类型";
            case 429 -> "请求过于频繁";
            case 500 -> "服务器内部错误";
            case 502 -> "网关错误";
            case 503 -> "服务不可用";
            case 504 -> "网关超时";
            default -> "发生了错误 (状态码: " + statusCode + ")";
        };
    }

    /**
     * 获取错误路径
     */
    public String getErrorPath() {
        return ERROR_PATH;
    }
}