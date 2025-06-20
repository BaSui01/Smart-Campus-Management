package com.campus.infrastructure.security;

import com.campus.infrastructure.notification.AlertNotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.*;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.security.core.Authentication;

import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 安全事件监听器
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityEventListener {

    private final AlertNotificationService alertNotificationService;
    
    // 登录失败计数器
    private final Map<String, AtomicInteger> loginFailureCount = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastFailureTime = new ConcurrentHashMap<>();
    
    // 安全配置
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 30;

    /**
     * 登录成功事件
     */
    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        String username = authentication.getName();
        String clientIp = getClientIp(authentication);
        
        log.info("用户登录成功: username={}, ip={}", username, clientIp);
        
        // 清除失败计数
        loginFailureCount.remove(clientIp);
        lastFailureTime.remove(clientIp);
        
        // 记录审计日志
        recordSecurityEvent("LOGIN_SUCCESS", username, clientIp, "用户登录成功");
    }

    /**
     * 登录失败事件
     */
    @EventListener
    public void handleAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        String clientIp = getClientIp(event.getAuthentication());
        String reason = event.getException().getMessage();
        
        log.warn("用户登录失败: username={}, ip={}, reason={}", username, clientIp, reason);
        
        // 增加失败计数
        AtomicInteger count = loginFailureCount.computeIfAbsent(clientIp, k -> new AtomicInteger(0));
        int failureCount = count.incrementAndGet();
        lastFailureTime.put(clientIp, LocalDateTime.now());
        
        // 检查是否需要锁定
        if (failureCount >= MAX_LOGIN_ATTEMPTS) {
            handleBruteForceAttack(username, clientIp, failureCount);
        }
        
        // 记录审计日志
        recordSecurityEvent("LOGIN_FAILURE", username, clientIp, 
                String.format("用户登录失败: %s (尝试次数: %d)", reason, failureCount));
    }

    /**
     * 访问被拒绝事件
     */
    @EventListener
    public void handleAuthorizationDenied(AuthorizationDeniedEvent<?> event) {
        Authentication authentication = event.getAuthentication().get();
        String username = authentication.getName();
        String clientIp = getClientIp(authentication);
        String resource = event.getAuthorizationDecision().toString();
        
        log.warn("访问被拒绝: username={}, ip={}, resource={}", username, clientIp, resource);
        
        // 记录审计日志
        recordSecurityEvent("ACCESS_DENIED", username, clientIp, 
                String.format("访问被拒绝: %s", resource));
        
        // 检查是否为可疑活动
        checkSuspiciousActivity(username, clientIp);
    }

    /**
     * 账户锁定事件
     */
    @EventListener
    public void handleAccountLocked(AuthenticationSuccessEvent event) {
        // 这里可以处理账户锁定相关逻辑
    }

    /**
     * 处理暴力破解攻击
     */
    private void handleBruteForceAttack(String username, String clientIp, int attemptCount) {
        log.error("检测到暴力破解攻击: username={}, ip={}, attempts={}", username, clientIp, attemptCount);
        
        // 发送安全告警
        alertNotificationService.sendSecurityAlert(
                String.format("暴力破解攻击 - 用户: %s, 尝试次数: %d", username, attemptCount),
                clientIp
        );
        
        // 记录高风险安全事件
        recordSecurityEvent("BRUTE_FORCE_ATTACK", username, clientIp, 
                String.format("检测到暴力破解攻击，尝试次数: %d", attemptCount));
        
        // 这里可以添加自动IP封禁逻辑
        // ipBlockingService.blockIp(clientIp, LOCKOUT_DURATION_MINUTES);
    }

    /**
     * 检查可疑活动
     */
    private void checkSuspiciousActivity(String username, String clientIp) {
        // 检查短时间内多次访问被拒绝
        // 这里可以实现更复杂的可疑活动检测逻辑
        
        // 示例：如果同一IP在短时间内多次访问被拒绝，发送告警
        String key = clientIp + "_access_denied";
        AtomicInteger count = loginFailureCount.computeIfAbsent(key, k -> new AtomicInteger(0));
        int deniedCount = count.incrementAndGet();
        
        if (deniedCount > 10) {
            log.warn("检测到可疑活动: ip={}, username={}, denied_count={}", clientIp, username, deniedCount);
            
            alertNotificationService.sendSecurityAlert(
                    String.format("可疑活动 - IP: %s, 用户: %s, 拒绝次数: %d", clientIp, username, deniedCount),
                    clientIp
            );
        }
    }

    /**
     * 记录安全事件
     */
    private void recordSecurityEvent(String eventType, String username, String clientIp, String description) {
        // 这里可以记录到数据库或发送到日志系统
        @SuppressWarnings("unused")
        Map<String, Object> eventData = Map.of(
                "eventType", eventType,
                "username", username,
                "clientIp", clientIp,
                "description", description,
                "timestamp", LocalDateTime.now()
        );
        
        // 发送到审计日志队列
        // messageProducer.sendAuditLog(null, eventType, "SECURITY", eventData);
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(Authentication authentication) {
        if (authentication.getDetails() instanceof WebAuthenticationDetails) {
            WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
            return details.getRemoteAddress();
        }
        return "unknown";
    }

    /**
     * 检查IP是否被锁定
     */
    public boolean isIpLocked(String clientIp) {
        AtomicInteger count = loginFailureCount.get(clientIp);
        LocalDateTime lastFailure = lastFailureTime.get(clientIp);
        
        if (count != null && count.get() >= MAX_LOGIN_ATTEMPTS && lastFailure != null) {
            LocalDateTime unlockTime = lastFailure.plusMinutes(LOCKOUT_DURATION_MINUTES);
            if (LocalDateTime.now().isBefore(unlockTime)) {
                return true;
            } else {
                // 锁定时间已过，清除计数
                loginFailureCount.remove(clientIp);
                lastFailureTime.remove(clientIp);
            }
        }
        
        return false;
    }

    /**
     * 获取剩余锁定时间（分钟）
     */
    public long getRemainingLockoutTime(String clientIp) {
        LocalDateTime lastFailure = lastFailureTime.get(clientIp);
        if (lastFailure != null) {
            LocalDateTime unlockTime = lastFailure.plusMinutes(LOCKOUT_DURATION_MINUTES);
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(unlockTime)) {
                return java.time.Duration.between(now, unlockTime).toMinutes();
            }
        }
        return 0;
    }

    /**
     * 手动解锁IP
     */
    public void unlockIp(String clientIp) {
        loginFailureCount.remove(clientIp);
        lastFailureTime.remove(clientIp);
        log.info("手动解锁IP: {}", clientIp);
    }

    /**
     * 获取登录失败统计
     */
    public Map<String, Object> getLoginFailureStats() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        
        loginFailureCount.forEach((ip, count) -> {
            LocalDateTime lastFailure = lastFailureTime.get(ip);
            stats.put(ip, Map.of(
                    "failureCount", count.get(),
                    "lastFailureTime", lastFailure,
                    "isLocked", isIpLocked(ip),
                    "remainingLockoutTime", getRemainingLockoutTime(ip)
            ));
        });
        
        return stats;
    }
}
