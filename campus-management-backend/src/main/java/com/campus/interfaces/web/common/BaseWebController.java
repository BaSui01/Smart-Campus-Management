package com.campus.interfaces.web.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web控制器基类
 * 专门为Web页面控制器提供通用功能和工具方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
public abstract class BaseWebController {

    /**
     * 日志记录器
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 默认页面大小
     */
    protected static final int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * 最大页面大小
     */
    protected static final int MAX_PAGE_SIZE = 100;
    
    // ==================== 分页工具方法 ====================
    
    /**
     * 创建分页对象
     */
    protected Pageable createPageable(int page, int size, String sortBy, String sortDir) {
        // 验证页码（Web页面从1开始，JPA从0开始）
        page = Math.max(0, page - 1);
        
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
        return createPageable(page, size, "createdAt", "desc");
    }
    
    // ==================== 模型数据处理 ====================
    
    /**
     * 添加分页信息到模型
     */
    protected void addPaginationToModel(Model model, int currentPage, int pageSize, long totalElements) {
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("totalPages", (int) Math.ceil((double) totalElements / pageSize));
        model.addAttribute("hasPrevious", currentPage > 1);
        model.addAttribute("hasNext", currentPage < Math.ceil((double) totalElements / pageSize));
    }
    
    /**
     * 添加搜索参数到模型
     */
    protected void addSearchParamsToModel(Model model, Object... params) {
        for (int i = 0; i < params.length; i += 2) {
            if (i + 1 < params.length) {
                String key = String.valueOf(params[i]);
                Object value = params[i + 1];
                model.addAttribute(key, value);
            }
        }
    }
    
    /**
     * 添加成功消息
     */
    protected void addSuccessMessage(Model model, String message) {
        model.addAttribute("successMessage", message);
    }
    
    /**
     * 添加错误消息
     */
    protected void addErrorMessage(Model model, String message) {
        model.addAttribute("errorMessage", message);
    }
    
    /**
     * 添加警告消息
     */
    protected void addWarningMessage(Model model, String message) {
        model.addAttribute("warningMessage", message);
    }
    
    /**
     * 添加信息消息
     */
    protected void addInfoMessage(Model model, String message) {
        model.addAttribute("infoMessage", message);
    }
    
    // ==================== 重定向工具方法 ====================
    
    /**
     * 重定向并添加成功消息
     */
    protected String redirectWithSuccess(String url, String message, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:" + url;
    }
    
    /**
     * 重定向并添加错误消息
     */
    protected String redirectWithError(String url, String message, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", message);
        return "redirect:" + url;
    }
    
    /**
     * 重定向并添加警告消息
     */
    protected String redirectWithWarning(String url, String message, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("warningMessage", message);
        return "redirect:" + url;
    }
    
    /**
     * 重定向并添加信息消息
     */
    protected String redirectWithInfo(String url, String message, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("infoMessage", message);
        return "redirect:" + url;
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
    
    // ==================== 工具方法 ====================
    
    /**
     * 处理搜索关键词
     */
    protected String processSearchKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return "";
        }
        return keyword.trim();
    }
    
    /**
     * 记录操作日志
     */
    protected void logOperation(String operation, Object... params) {
        logger.info("Web操作: {} - 参数: {}", operation, params);
    }
    
    /**
     * 记录错误日志
     */
    protected void logError(String operation, Exception e) {
        logger.error("Web操作失败: {} - 错误: {}", operation, e.getMessage(), e);
    }
    
    /**
     * 构建查询参数字符串
     */
    protected String buildQueryString(Object... params) {
        StringBuilder queryString = new StringBuilder();
        for (int i = 0; i < params.length; i += 2) {
            if (i + 1 < params.length) {
                String key = String.valueOf(params[i]);
                Object value = params[i + 1];
                if (value != null && StringUtils.hasText(value.toString())) {
                    if (queryString.length() > 0) {
                        queryString.append("&");
                    }
                    queryString.append(key).append("=").append(value);
                }
            }
        }
        return queryString.toString();
    }
    
    // ==================== 异常处理 ====================
    
    /**
     * 全局异常处理
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        logError("控制器异常", e);
        
        // 根据异常类型返回不同的错误页面
        if (e instanceof IllegalArgumentException) {
            addErrorMessage(model, "参数错误: " + e.getMessage());
        } else if (e instanceof SecurityException) {
            addErrorMessage(model, "权限不足: " + e.getMessage());
        } else if (e instanceof RuntimeException) {
            addErrorMessage(model, "业务处理失败: " + e.getMessage());
        } else {
            addErrorMessage(model, "服务器内部错误");
        }
        
        return "error/500"; // 返回错误页面
    }
    
    /**
     * 参数验证异常处理
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException e, Model model) {
        logError("参数验证失败", e);
        addErrorMessage(model, e.getMessage());
        return "error/400";
    }
    
    /**
     * 安全异常处理
     */
    @ExceptionHandler(SecurityException.class)
    public String handleSecurityException(SecurityException e, Model model) {
        logError("安全验证失败", e);
        addErrorMessage(model, e.getMessage());
        return "error/403";
    }
    
    // ==================== 数据转换方法 ====================
    
    /**
     * 安全地转换Long类型
     */
    protected Long safeParseLong(String value, Long defaultValue) {
        try {
            return StringUtils.hasText(value) ? Long.parseLong(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * 安全地转换Integer类型
     */
    protected Integer safeParseInteger(String value, Integer defaultValue) {
        try {
            return StringUtils.hasText(value) ? Integer.parseInt(value) : defaultValue;
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
    
    /**
     * 获取当前用户ID（需要子类实现）
     */
    protected Long getCurrentUserId() {
        // 子类可以重写此方法来获取当前登录用户ID
        return null;
    }
    
    /**
     * 获取当前用户名（需要子类实现）
     */
    protected String getCurrentUsername() {
        // 子类可以重写此方法来获取当前登录用户名
        return null;
    }
}
