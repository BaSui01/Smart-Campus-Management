package com.campus.shared.security;

import com.campus.domain.entity.system.AuditLog;
import com.campus.domain.repository.system.AuditLogRepository;
import com.campus.shared.util.JsonUtil;
import com.campus.shared.util.SecurityUtil;
import com.campus.shared.util.WebUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 审计日志切面
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;

    @Around("@annotation(auditLog)")
    public Object around(ProceedingJoinPoint point, com.campus.shared.security.AuditLog auditLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception exception = null;

        try {
            result = point.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            saveAuditLog(point, auditLog, result, exception, executionTime);
        }
    }

    /**
     * 异步保存审计日志
     */
    @Async
    public void saveAuditLog(ProceedingJoinPoint point, com.campus.shared.security.AuditLog auditLogAnnotation,
                            Object result, Exception exception, long executionTime) {
        try {
            AuditLog auditLog = new AuditLog();
            
            // 获取当前用户信息
            Long currentUserId = SecurityUtil.getCurrentUserId();
            String currentUsername = SecurityUtil.getCurrentUsername();
            String currentRealName = SecurityUtil.getCurrentUserRealName();
            
            auditLog.setUserId(currentUserId);
            auditLog.setUsername(currentUsername);
            auditLog.setRealName(currentRealName);
            
            // 设置操作信息
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            
            auditLog.setOperationType(getOperationType(auditLogAnnotation, method));
            auditLog.setOperationDesc(getOperationDesc(auditLogAnnotation, method));
            auditLog.setModule(getModule(auditLogAnnotation, method));
            auditLog.setMethod(method.getDeclaringClass().getName() + "." + method.getName());
            auditLog.setBusinessType(auditLogAnnotation.businessType());
            auditLog.setRiskLevel(auditLogAnnotation.riskLevel());
            
            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                auditLog.setRequestUrl(request.getRequestURL().toString());
                auditLog.setHttpMethod(request.getMethod());
                auditLog.setClientIp(WebUtil.getClientIp(request));
                auditLog.setUserAgent(request.getHeader("User-Agent"));
                
                // 记录请求参数
                if (auditLogAnnotation.includeParams()) {
                    auditLog.setRequestParams(getRequestParams(point, request));
                }
            }
            
            // 记录响应结果
            if (auditLogAnnotation.includeResult() && result != null) {
                auditLog.setResponseResult(JsonUtil.toJson(result));
            }
            
            // 设置操作状态和错误信息
            if (exception != null) {
                auditLog.setOperationStatus(AuditLog.OperationStatus.FAILURE.getCode());
                auditLog.setErrorMessage(exception.getMessage());
            } else {
                auditLog.setOperationStatus(AuditLog.OperationStatus.SUCCESS.getCode());
            }
            
            auditLog.setExecutionTime(executionTime);
            auditLog.setOperationTime(LocalDateTime.now());
            
            // 保存审计日志
            auditLogRepository.save(auditLog);
            
        } catch (Exception e) {
            log.error("保存审计日志失败", e);
        }
    }

    /**
     * 获取操作类型
     */
    private String getOperationType(com.campus.shared.security.AuditLog auditLogAnnotation, Method method) {
        if (!auditLogAnnotation.operationType().isEmpty()) {
            return auditLogAnnotation.operationType();
        }
        
        String methodName = method.getName().toLowerCase();
        if (methodName.startsWith("create") || methodName.startsWith("add") || methodName.startsWith("insert")) {
            return AuditLog.OperationType.CREATE.getCode();
        } else if (methodName.startsWith("update") || methodName.startsWith("modify") || methodName.startsWith("edit")) {
            return AuditLog.OperationType.UPDATE.getCode();
        } else if (methodName.startsWith("delete") || methodName.startsWith("remove")) {
            return AuditLog.OperationType.DELETE.getCode();
        } else if (methodName.startsWith("get") || methodName.startsWith("find") || methodName.startsWith("list") || methodName.startsWith("query")) {
            return AuditLog.OperationType.QUERY.getCode();
        } else if (methodName.startsWith("export")) {
            return AuditLog.OperationType.EXPORT.getCode();
        } else if (methodName.startsWith("import")) {
            return AuditLog.OperationType.IMPORT.getCode();
        } else if (methodName.startsWith("upload")) {
            return AuditLog.OperationType.UPLOAD.getCode();
        } else if (methodName.startsWith("download")) {
            return AuditLog.OperationType.DOWNLOAD.getCode();
        }
        
        return "UNKNOWN";
    }

    /**
     * 获取操作描述
     */
    private String getOperationDesc(com.campus.shared.security.AuditLog auditLogAnnotation, Method method) {
        if (!auditLogAnnotation.operationDesc().isEmpty()) {
            return auditLogAnnotation.operationDesc();
        }
        return method.getName();
    }

    /**
     * 获取操作模块
     */
    private String getModule(com.campus.shared.security.AuditLog auditLogAnnotation, Method method) {
        if (!auditLogAnnotation.module().isEmpty()) {
            return auditLogAnnotation.module();
        }
        
        String className = method.getDeclaringClass().getSimpleName();
        if (className.endsWith("Controller")) {
            return className.substring(0, className.length() - 10);
        }
        return className;
    }

    /**
     * 获取请求参数
     */
    private String getRequestParams(ProceedingJoinPoint point, HttpServletRequest request) {
        try {
            // 获取方法参数
            Object[] args = point.getArgs();
            if (args != null && args.length > 0) {
                // 过滤敏感参数
                String params = Arrays.stream(args)
                        .filter(arg -> arg != null)
                        .filter(arg -> !(arg instanceof HttpServletRequest))
                        .filter(arg -> !(arg instanceof jakarta.servlet.http.HttpServletResponse))
                        .map(arg -> {
                            String argStr = arg.toString();
                            // 过滤密码等敏感信息
                            if (argStr.toLowerCase().contains("password")) {
                                return "[FILTERED]";
                            }
                            return argStr;
                        })
                        .collect(Collectors.joining(", "));
                
                return params.length() > 2000 ? params.substring(0, 2000) + "..." : params;
            }
            
            // 获取请求参数
            return request.getQueryString();
        } catch (Exception e) {
            log.warn("获取请求参数失败", e);
            return "";
        }
    }
}
