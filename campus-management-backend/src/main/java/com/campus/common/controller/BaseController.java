package com.campus.common.controller;

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

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 基础控制器类
 * 提供通用的控制器功能和工具方法
 *
 * @author Campus Management System
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
    
    /**
     * 构建成功响应
     */
    protected <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }
    
    /**
     * 构建成功响应（带消息）
     */
    protected <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
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
        return ResponseEntity.status(code).body(ApiResponse.error(code, message));
    }
    
    /**
     * 构建未找到响应
     */
    protected <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return ResponseEntity.status(404).body(ApiResponse.notFound(message));
    }
    
    /**
     * 构建未授权响应
     */
    protected <T> ResponseEntity<ApiResponse<T>> unauthorized(String message) {
        return ResponseEntity.status(401).body(ApiResponse.unauthorized(message));
    }
    
    /**
     * 构建禁止访问响应
     */
    protected <T> ResponseEntity<ApiResponse<T>> forbidden(String message) {
        return ResponseEntity.status(403).body(ApiResponse.forbidden(message));
    }
    
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
     * 确保字符串使用UTF-8编码
     */
    protected String ensureUtf8(String str) {
        if (str == null) {
            return null;
        }
        try {
            // 检查字符串是否已经是UTF-8编码
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("UTF-8编码转换失败: {}", e.getMessage());
            return str;
        }
    }
    
    /**
     * 处理搜索关键词
     */
    protected String processSearchKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return "";
        }
        // 确保UTF-8编码
        keyword = ensureUtf8(keyword.trim());
        // 转义SQL特殊字符
        keyword = keyword.replace("%", "\\%").replace("_", "\\_");
        return keyword;
    }
    
    /**
     * 全局异常处理
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        log.error("控制器异常: ", e);
        
        // 根据异常类型返回不同的错误响应
        if (e instanceof IllegalArgumentException) {
            return error(400, "参数错误: " + e.getMessage());
        } else if (e instanceof SecurityException) {
            return unauthorized("权限不足: " + e.getMessage());
        } else {
            return error(500, "服务器内部错误: " + e.getMessage());
        }
    }
    
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
    
    /**
     * 记录操作日志
     */
    protected void logOperation(String operation, Object... params) {
        log.info("操作: {} - 参数: {}", operation, params);
    }
}
