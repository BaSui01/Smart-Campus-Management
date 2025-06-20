package com.campus.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求签名验证过滤器
 * 验证API请求的数字签名，防止请求篡改
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-18
 */
@Component
public class RequestSignatureValidator extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestSignatureValidator.class);

    @Value("${campus.security.signature.enabled:false}")
    private boolean signatureEnabled;

    @Value("${campus.security.signature.secret:campus-signature-secret-2024}")
    private String signatureSecret;

    @Value("${campus.security.signature.timestamp-tolerance:300000}")
    private long timestampTolerance; // 5分钟

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 需要签名验证的API路径
    private static final String[] SIGNATURE_REQUIRED_PATHS = {
        "/api/v1/admin/",
        "/api/v1/finance/",
        "/api/v1/user/profile",
        "/api/v1/payment/"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // 如果签名验证未启用，直接通过
        if (!signatureEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestURI = request.getRequestURI();
        
        // 检查是否需要签名验证
        if (!requiresSignatureValidation(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 包装请求以便读取请求体
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        
        try {
            // 验证请求签名
            if (validateRequestSignature(wrappedRequest)) {
                filterChain.doFilter(wrappedRequest, response);
            } else {
                handleSignatureValidationFailure(response);
            }
        } catch (Exception e) {
            logger.error("签名验证过程中发生错误", e);
            handleSignatureValidationFailure(response);
        }
    }

    /**
     * 检查请求是否需要签名验证
     */
    private boolean requiresSignatureValidation(String requestURI) {
        for (String path : SIGNATURE_REQUIRED_PATHS) {
            if (requestURI.startsWith(path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证请求签名
     */
    private boolean validateRequestSignature(ContentCachingRequestWrapper request) throws IOException {
        // 获取签名相关的请求头
        String signature = request.getHeader("X-Signature");
        String timestamp = request.getHeader("X-Timestamp");
        String nonce = request.getHeader("X-Nonce");

        if (signature == null || timestamp == null || nonce == null) {
            logger.warn("缺少必要的签名头信息");
            return false;
        }

        // 验证时间戳
        if (!validateTimestamp(timestamp)) {
            logger.warn("请求时间戳验证失败: {}", timestamp);
            return false;
        }

        // 构建签名字符串
        String signatureString = buildSignatureString(request, timestamp, nonce);
        
        // 计算期望的签名
        String expectedSignature = calculateSignature(signatureString);
        
        // 比较签名
        boolean isValid = signature.equals(expectedSignature);
        
        if (!isValid) {
            logger.warn("请求签名验证失败. URI: {}, Expected: {}, Actual: {}", 
                request.getRequestURI(), expectedSignature, signature);
        }
        
        return isValid;
    }

    /**
     * 验证时间戳
     */
    private boolean validateTimestamp(String timestampStr) {
        try {
            long timestamp = Long.parseLong(timestampStr);
            long currentTime = System.currentTimeMillis();
            long timeDiff = Math.abs(currentTime - timestamp);
            
            return timeDiff <= timestampTolerance;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 构建签名字符串
     */
    private String buildSignatureString(ContentCachingRequestWrapper request, String timestamp, String nonce) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        // HTTP方法
        sb.append(request.getMethod()).append("\n");
        
        // 请求URI
        sb.append(request.getRequestURI()).append("\n");
        
        // 查询参数
        String queryString = request.getQueryString();
        sb.append(queryString != null ? queryString : "").append("\n");
        
        // 请求体
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            sb.append(new String(content, StandardCharsets.UTF_8));
        }
        sb.append("\n");
        
        // 时间戳和随机数
        sb.append(timestamp).append("\n");
        sb.append(nonce);
        
        return sb.toString();
    }

    /**
     * 计算HMAC-SHA256签名
     */
    private String calculateSignature(String data) {
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, signatureSecret)
            .hmacHex(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 处理签名验证失败
     */
    private void handleSignatureValidationFailure(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Signature validation failed");
        errorResponse.put("message", "请求签名验证失败");
        errorResponse.put("code", 401);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
