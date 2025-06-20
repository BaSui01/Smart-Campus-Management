package com.campus.shared.util;

import org.slf4j.Logger;
import org.slf4j.MDC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 日志工具类
 * 提供统一的日志记录方法，包含敏感信息脱敏、结构化日志等功能
 * 
 * @author Campus Management Team
 * @since 2025-06-20
 */
public class LogUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    // 日志类型常量
    public static final String LOG_TYPE_BUSINESS = "BUSINESS";
    public static final String LOG_TYPE_SECURITY = "SECURITY";
    public static final String LOG_TYPE_PERFORMANCE = "PERFORMANCE";
    public static final String LOG_TYPE_ERROR = "ERROR";
    public static final String LOG_TYPE_AUDIT = "AUDIT";

    // MDC键常量
    public static final String MDC_TRACE_ID = "traceId";
    public static final String MDC_USER_ID = "userId";
    public static final String MDC_USERNAME = "username";
    public static final String MDC_IP = "clientIp";
    public static final String MDC_LOG_TYPE = "logType";

    /**
     * 设置追踪ID
     */
    public static void setTraceId(String traceId) {
        if (traceId == null || traceId.trim().isEmpty()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        MDC.put(MDC_TRACE_ID, traceId);
    }

    /**
     * 设置用户信息
     */
    public static void setUserInfo(Long userId, String username) {
        if (userId != null) {
            MDC.put(MDC_USER_ID, userId.toString());
        }
        if (username != null) {
            MDC.put(MDC_USERNAME, username);
        }
    }

    /**
     * 设置客户端IP
     */
    public static void setClientIp(String clientIp) {
        if (clientIp != null) {
            MDC.put(MDC_IP, clientIp);
        }
    }

    /**
     * 设置日志类型
     */
    public static void setLogType(String logType) {
        MDC.put(MDC_LOG_TYPE, logType);
    }

    /**
     * 清除MDC
     */
    public static void clearMDC() {
        MDC.clear();
    }

    /**
     * 记录业务日志
     */
    public static void logBusiness(Logger logger, String action, String message, Object... params) {
        setLogType(LOG_TYPE_BUSINESS);
        logger.info("业务操作 - 动作: {}, 消息: {}, 参数: {}", action, message, sanitizeParams(params));
    }

    /**
     * 记录安全日志
     */
    public static void logSecurity(Logger logger, String event, String message, Object... params) {
        setLogType(LOG_TYPE_SECURITY);
        logger.warn("安全事件 - 事件: {}, 消息: {}, 参数: {}", event, message, sanitizeParams(params));
    }

    /**
     * 记录性能日志
     */
    public static void logPerformance(Logger logger, String operation, long duration, Object... params) {
        setLogType(LOG_TYPE_PERFORMANCE);
        logger.info("性能监控 - 操作: {}, 耗时: {}ms, 参数: {}", operation, duration, sanitizeParams(params));
    }

    /**
     * 记录错误日志
     */
    public static void logError(Logger logger, String operation, String message, Throwable throwable, Object... params) {
        setLogType(LOG_TYPE_ERROR);
        logger.error("错误记录 - 操作: {}, 消息: {}, 参数: {}", operation, message, sanitizeParams(params), throwable);
    }

    /**
     * 记录审计日志
     */
    public static void logAudit(Logger logger, String action, String resource, String result, Object... params) {
        setLogType(LOG_TYPE_AUDIT);
        logger.info("审计日志 - 动作: {}, 资源: {}, 结果: {}, 参数: {}", action, resource, result, sanitizeParams(params));
    }

    /**
     * 记录用户登录日志
     */
    public static void logUserLogin(Logger logger, String username, String clientIp, boolean success, String reason) {
        setLogType(LOG_TYPE_SECURITY);
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("username", maskSensitiveInfo(username, 2, 2));
        loginInfo.put("clientIp", clientIp);
        loginInfo.put("success", success);
        loginInfo.put("reason", reason);
        loginInfo.put("timestamp", System.currentTimeMillis());
        
        if (success) {
            logger.info("用户登录成功: {}", toJsonString(loginInfo));
        } else {
            logger.warn("用户登录失败: {}", toJsonString(loginInfo));
        }
    }

    /**
     * 记录用户操作日志
     */
    public static void logUserOperation(Logger logger, String username, String operation, String resource, 
                                       boolean success, String details) {
        setLogType(LOG_TYPE_AUDIT);
        Map<String, Object> operationInfo = new HashMap<>();
        operationInfo.put("username", maskSensitiveInfo(username, 2, 2));
        operationInfo.put("operation", operation);
        operationInfo.put("resource", resource);
        operationInfo.put("success", success);
        operationInfo.put("details", details);
        operationInfo.put("timestamp", System.currentTimeMillis());
        
        logger.info("用户操作: {}", toJsonString(operationInfo));
    }

    /**
     * 记录数据变更日志
     */
    public static void logDataChange(Logger logger, String tableName, String operation, Object oldData, Object newData) {
        setLogType(LOG_TYPE_AUDIT);
        Map<String, Object> changeInfo = new HashMap<>();
        changeInfo.put("table", tableName);
        changeInfo.put("operation", operation);
        changeInfo.put("oldData", sanitizeObject(oldData));
        changeInfo.put("newData", sanitizeObject(newData));
        changeInfo.put("timestamp", System.currentTimeMillis());
        
        logger.info("数据变更: {}", toJsonString(changeInfo));
    }

    /**
     * 记录API调用日志
     */
    public static void logApiCall(Logger logger, String method, String uri, int statusCode, long duration, 
                                 String userAgent) {
        setLogType(LOG_TYPE_PERFORMANCE);
        Map<String, Object> apiInfo = new HashMap<>();
        apiInfo.put("method", method);
        apiInfo.put("uri", uri);
        apiInfo.put("statusCode", statusCode);
        apiInfo.put("duration", duration);
        apiInfo.put("userAgent", userAgent);
        apiInfo.put("timestamp", System.currentTimeMillis());
        
        if (statusCode >= 400) {
            logger.warn("API调用异常: {}", toJsonString(apiInfo));
        } else if (duration > 1000) {
            logger.warn("API调用缓慢: {}", toJsonString(apiInfo));
        } else {
            logger.debug("API调用: {}", toJsonString(apiInfo));
        }
    }

    /**
     * 敏感信息脱敏
     */
    public static String maskSensitiveInfo(String info, int prefixLength, int suffixLength) {
        if (info == null || info.length() <= prefixLength + suffixLength) {
            return "***";
        }
        
        String prefix = info.substring(0, prefixLength);
        String suffix = info.substring(info.length() - suffixLength);
        return prefix + "***" + suffix;
    }

    /**
     * 脱敏参数
     */
    private static Object[] sanitizeParams(Object... params) {
        if (params == null || params.length == 0) {
            return new Object[0];
        }
        
        Object[] sanitized = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            sanitized[i] = sanitizeObject(params[i]);
        }
        return sanitized;
    }

    /**
     * 脱敏对象
     */
    private static Object sanitizeObject(Object obj) {
        if (obj == null) {
            return null;
        }
        
        // 如果是字符串，检查是否包含敏感信息
        if (obj instanceof String) {
            String str = (String) obj;
            // 简单的敏感信息检测和脱敏
            if (str.toLowerCase().contains("password") || 
                str.toLowerCase().contains("pwd") ||
                str.toLowerCase().contains("secret") ||
                str.toLowerCase().contains("token")) {
                return "***";
            }
            // 邮箱脱敏
            if (str.contains("@") && str.contains(".")) {
                int atIndex = str.indexOf("@");
                if (atIndex > 2) {
                    return maskSensitiveInfo(str, 2, 0);
                }
            }
            // 手机号脱敏
            if (str.matches("^1[3-9]\\d{9}$")) {
                return maskSensitiveInfo(str, 3, 4);
            }
        }
        
        return obj;
    }

    /**
     * 对象转JSON字符串
     */
    private static String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return obj.toString();
        }
    }

    /**
     * 创建结构化日志对象
     */
    public static Map<String, Object> createLogEntry(String action, String message) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("action", action);
        logEntry.put("message", message);
        logEntry.put("timestamp", System.currentTimeMillis());
        logEntry.put("traceId", MDC.get(MDC_TRACE_ID));
        logEntry.put("userId", MDC.get(MDC_USER_ID));
        logEntry.put("username", MDC.get(MDC_USERNAME));
        logEntry.put("clientIp", MDC.get(MDC_IP));
        return logEntry;
    }

    /**
     * 获取调用者信息
     */
    public static String getCallerInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 3) {
            StackTraceElement caller = stackTrace[3];
            return caller.getClassName() + "." + caller.getMethodName() + ":" + caller.getLineNumber();
        }
        return "Unknown";
    }

    /**
     * 记录方法执行时间
     */
    public static void logMethodExecution(Logger logger, String methodName, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        if (duration > 100) { // 超过100ms记录警告
            logger.warn("方法执行缓慢: {} 耗时 {}ms", methodName, duration);
        } else {
            logger.debug("方法执行: {} 耗时 {}ms", methodName, duration);
        }
    }

    /**
     * 记录SQL执行日志
     */
    public static void logSqlExecution(Logger logger, String sql, Object[] params, long duration) {
        setLogType(LOG_TYPE_PERFORMANCE);
        Map<String, Object> sqlInfo = new HashMap<>();
        sqlInfo.put("sql", sql);
        sqlInfo.put("params", sanitizeParams(params));
        sqlInfo.put("duration", duration);
        sqlInfo.put("timestamp", System.currentTimeMillis());
        
        if (duration > 1000) {
            logger.warn("慢SQL查询: {}", toJsonString(sqlInfo));
        } else {
            logger.debug("SQL执行: {}", toJsonString(sqlInfo));
        }
    }
}
