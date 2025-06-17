package com.campus.interfaces.rest.common;

import com.campus.shared.common.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

/**
 * REST API基础控制器类
 * 专门为REST接口层提供通用功能和工具方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
public abstract class BaseController {

    /**
     * 日志记录器
     */
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 默认页面大小
     */
    protected static final int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * 最大页面大小
     */
    protected static final int MAX_PAGE_SIZE = 100;
    
    // ==================== 响应构建方法 ====================
    
    /**
     * 构建成功响应
     */
    protected <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }
    
    /**
     * 构建成功响应（带消息）
     */
    protected <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        return ResponseEntity.ok(ApiResponse.success(message, data));
    }

    /**
     * 构建成功响应（无数据）
     */
    protected <T> ResponseEntity<ApiResponse<T>> success(String message) {
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    /**
     * 构建分页成功响应
     */
    protected <T> ResponseEntity<ApiResponse<List<T>>> successPage(Page<T> page) {
        return ResponseEntity.ok(ApiResponse.success("查询成功", page.getContent()));
    }

    /**
     * 构建分页成功响应（带消息）
     */
    protected <T> ResponseEntity<ApiResponse<List<T>>> successPage(Page<T> page, String message) {
        return ResponseEntity.ok(ApiResponse.success(message, page.getContent()));
    }
    
    /**
     * 构建错误响应
     */
    protected <T> ResponseEntity<ApiResponse<T>> error(String message) {
        return ResponseEntity.badRequest().body(ApiResponse.error(message));
    }
    
    /**
     * 构建错误响应（带状态码）
     */
    protected <T> ResponseEntity<ApiResponse<T>> error(int code, String message) {
        return ResponseEntity.status(code).body(ApiResponse.error(message));
    }
    
    /**
     * 构建未找到响应
     */
    protected <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return ResponseEntity.status(404).body(ApiResponse.error(message));
    }
    
    /**
     * 构建未授权响应
     */
    protected <T> ResponseEntity<ApiResponse<T>> unauthorized(String message) {
        return ResponseEntity.status(401).body(ApiResponse.error(message));
    }
    
    /**
     * 构建禁止访问响应
     */
    protected <T> ResponseEntity<ApiResponse<T>> forbidden(String message) {
        return ResponseEntity.status(403).body(ApiResponse.error(message));
    }
    
    /**
     * 构建参数错误响应
     */
    protected <T> ResponseEntity<ApiResponse<T>> badRequest(String message) {
        return ResponseEntity.status(400).body(ApiResponse.error(message));
    }
    
    // ==================== 分页工具方法 ====================
    
    /**
     * 创建分页对象
     */
    protected Pageable createPageable(int page, int size, String sortBy, String sortDir) {
        // 验证页码
        page = Math.max(0, page - 1); // 转换为从0开始的页码
        
        // 验证页面大小
        size = Math.min(Math.max(1, size), MAX_PAGE_SIZE);
        
        // 创建排序
        Sort sort = Sort.unsorted();
        if (StringUtils.hasText(sortBy)) {
            Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, sortBy);
        }
        
        return PageRequest.of(page, size, sort);
    }
    
    /**
     * 创建默认分页对象
     */
    protected Pageable createPageable(int page, int size) {
        return createPageable(page, size, null, null);
    }
    
    /**
     * 创建默认排序的分页对象
     */
    protected Pageable createPageable(int page, int size, String sortBy) {
        return createPageable(page, size, sortBy, "desc");
    }
    
    // ==================== 参数验证方法 ====================

    /**
     * 验证ID参数
     */
    protected void validateId(Long id, String entityName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(entityName + "ID不能为空或小于等于0");
        }
    }

    /**
     * 验证分页参数
     */
    protected void validatePageParams(int page, int size) {
        if (page < 1) {
            throw new IllegalArgumentException("页码不能小于1");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("页面大小必须在1到" + MAX_PAGE_SIZE + "之间");
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 处理搜索关键词
     */
    protected String processSearchKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        return keyword.trim();
    }

    /**
     * 记录操作日志
     */
    protected void logOperation(String operation, Object... params) {
        if (log.isInfoEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("执行操作: ").append(operation);
            if (params != null && params.length > 0) {
                sb.append(", 参数: ");
                for (int i = 0; i < params.length; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(params[i]);
                }
            }
            log.info(sb.toString());
        }
    }
    
    /**
     * 验证字符串参数
     */
    protected void validateStringParam(String param, String paramName) {
        if (!StringUtils.hasText(param)) {
            throw new IllegalArgumentException(paramName + "不能为空");
        }
    }
    
    /**
     * 验证必需参数
     */
    protected void validateRequired(Object param, String paramName) {
        if (param == null) {
            throw new IllegalArgumentException(paramName + "不能为空");
        }
    }
    

    
    /**
     * 记录错误日志
     */
    protected void logError(String operation, Exception e) {
        log.error("API操作失败: {} - 错误: {}", operation, e.getMessage(), e);
    }
    
    // ==================== 异常处理 ====================
    
    /**
     * 全局异常处理
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        logError("控制器异常", e);
        
        // 根据异常类型返回不同的错误响应
        if (e instanceof IllegalArgumentException) {
            return badRequest("参数错误: " + e.getMessage());
        } else if (e instanceof SecurityException) {
            return unauthorized("权限不足: " + e.getMessage());
        } else if (e instanceof RuntimeException) {
            return error(500, "业务处理失败: " + e.getMessage());
        } else {
            return error(500, "服务器内部错误");
        }
    }
    
    /**
     * 参数验证异常处理
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        logError("参数验证失败", e);
        return badRequest(e.getMessage());
    }
    
    /**
     * 安全异常处理
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse<Object>> handleSecurityException(SecurityException e) {
        logError("安全验证失败", e);
        return forbidden(e.getMessage());
    }
    
    // ==================== 数据转换方法 ====================
    
    /**
     * 安全地转换Long类型
     */
    protected Long safeParseLong(String value, Long defaultValue) {
        try {
            return StringUtils.hasText(value) ? Long.valueOf(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * 安全地转换Integer类型
     */
    protected Integer safeParseInteger(String value, Integer defaultValue) {
        try {
            return StringUtils.hasText(value) ? Integer.valueOf(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * 安全地转换Boolean类型
     */
    protected Boolean safeParseBoolean(String value, Boolean defaultValue) {
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(value) || "1".equals(value) || "yes".equalsIgnoreCase(value);
    }
}
