package com.campus.infrastructure.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 告警通知服务
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertNotificationService {

    private final JavaMailSender mailSender;
    
    // 告警频率控制，防止重复告警
    private final Map<String, LocalDateTime> alertHistory = new ConcurrentHashMap<>();
    private static final int ALERT_INTERVAL_MINUTES = 30; // 30分钟内不重复发送相同告警

    /**
     * 发送系统告警
     * 
     * @param alertType 告警类型
     * @param message 告警消息
     * @param details 详细信息
     */
    @Async
    public void sendSystemAlert(AlertType alertType, String message, Map<String, Object> details) {
        try {
            String alertKey = alertType.name() + ":" + message;
            
            // 检查是否需要发送告警（防止频繁告警）
            if (shouldSendAlert(alertKey)) {
                // 发送邮件告警
                sendEmailAlert(alertType, message, details);
                
                // 记录告警历史
                alertHistory.put(alertKey, LocalDateTime.now());
                
                log.info("发送系统告警: type={}, message={}", alertType, message);
            } else {
                log.debug("跳过重复告警: type={}, message={}", alertType, message);
            }
            
        } catch (Exception e) {
            log.error("发送系统告警失败", e);
        }
    }

    /**
     * 发送邮件告警
     */
    private void sendEmailAlert(AlertType alertType, String message, Map<String, Object> details) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(getAlertRecipients(alertType));
            mailMessage.setSubject(buildAlertSubject(alertType, message));
            mailMessage.setText(buildAlertContent(alertType, message, details));
            
            mailSender.send(mailMessage);
            
        } catch (Exception e) {
            log.error("发送邮件告警失败", e);
        }
    }

    /**
     * 检查是否应该发送告警
     */
    private boolean shouldSendAlert(String alertKey) {
        LocalDateTime lastAlertTime = alertHistory.get(alertKey);
        if (lastAlertTime == null) {
            return true;
        }
        
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(lastAlertTime.plusMinutes(ALERT_INTERVAL_MINUTES));
    }

    /**
     * 获取告警接收人
     */
    private String[] getAlertRecipients(AlertType alertType) {
        // 这里可以根据告警类型返回不同的接收人列表
        // 实际使用时应该从配置文件或数据库中获取
        switch (alertType) {
            case SYSTEM_ERROR:
            case HIGH_CPU_USAGE:
            case HIGH_MEMORY_USAGE:
            case DATABASE_ERROR:
                return new String[]{"admin@campus.edu", "ops@campus.edu"};
            case SECURITY_ALERT:
                return new String[]{"security@campus.edu", "admin@campus.edu"};
            case BUSINESS_ERROR:
                return new String[]{"business@campus.edu"};
            default:
                return new String[]{"admin@campus.edu"};
        }
    }

    /**
     * 构建告警主题
     */
    private String buildAlertSubject(AlertType alertType, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return String.format("[%s] 智慧校园系统告警 - %s (%s)", 
                alertType.getDescription(), message, timestamp);
    }

    /**
     * 构建告警内容
     */
    private String buildAlertContent(AlertType alertType, String message, Map<String, Object> details) {
        StringBuilder content = new StringBuilder();
        
        content.append("智慧校园管理系统告警通知\n\n");
        content.append("告警类型: ").append(alertType.getDescription()).append("\n");
        content.append("告警消息: ").append(message).append("\n");
        content.append("告警时间: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
        
        if (details != null && !details.isEmpty()) {
            content.append("详细信息:\n");
            details.forEach((key, value) -> {
                content.append("  ").append(key).append(": ").append(value).append("\n");
            });
            content.append("\n");
        }
        
        content.append("请及时处理相关问题。\n\n");
        content.append("此邮件由智慧校园管理系统自动发送，请勿回复。");
        
        return content.toString();
    }

    /**
     * 发送CPU使用率告警
     */
    public void sendCpuUsageAlert(double cpuUsage) {
        Map<String, Object> details = Map.of(
                "cpuUsage", cpuUsage + "%",
                "threshold", "80%",
                "suggestion", "请检查系统负载，考虑扩容或优化"
        );
        
        sendSystemAlert(AlertType.HIGH_CPU_USAGE, 
                "CPU使用率过高: " + cpuUsage + "%", details);
    }

    /**
     * 发送内存使用率告警
     */
    public void sendMemoryUsageAlert(double memoryUsage) {
        Map<String, Object> details = Map.of(
                "memoryUsage", memoryUsage + "%",
                "threshold", "85%",
                "suggestion", "请检查内存泄漏，考虑增加内存或优化代码"
        );
        
        sendSystemAlert(AlertType.HIGH_MEMORY_USAGE, 
                "内存使用率过高: " + memoryUsage + "%", details);
    }

    /**
     * 发送数据库连接告警
     */
    public void sendDatabaseConnectionAlert(String errorMessage) {
        Map<String, Object> details = Map.of(
                "errorMessage", errorMessage,
                "suggestion", "请检查数据库连接配置和网络状态"
        );
        
        sendSystemAlert(AlertType.DATABASE_ERROR, 
                "数据库连接异常", details);
    }

    /**
     * 发送安全告警
     */
    public void sendSecurityAlert(String securityEvent, String clientIp) {
        Map<String, Object> details = Map.of(
                "securityEvent", securityEvent,
                "clientIp", clientIp,
                "suggestion", "请检查是否存在安全威胁"
        );
        
        sendSystemAlert(AlertType.SECURITY_ALERT, 
                "安全事件: " + securityEvent, details);
    }

    /**
     * 发送业务异常告警
     */
    public void sendBusinessErrorAlert(String businessModule, String errorMessage) {
        Map<String, Object> details = Map.of(
                "businessModule", businessModule,
                "errorMessage", errorMessage,
                "suggestion", "请检查业务逻辑和数据完整性"
        );
        
        sendSystemAlert(AlertType.BUSINESS_ERROR, 
                "业务异常: " + businessModule, details);
    }

    /**
     * 清理过期的告警历史
     */
    public void cleanExpiredAlertHistory() {
        LocalDateTime expireTime = LocalDateTime.now().minusHours(24);
        alertHistory.entrySet().removeIf(entry -> entry.getValue().isBefore(expireTime));
    }

    /**
     * 告警类型枚举
     */
    public enum AlertType {
        SYSTEM_ERROR("系统错误"),
        HIGH_CPU_USAGE("CPU使用率过高"),
        HIGH_MEMORY_USAGE("内存使用率过高"),
        HIGH_DISK_USAGE("磁盘使用率过高"),
        DATABASE_ERROR("数据库异常"),
        SECURITY_ALERT("安全告警"),
        BUSINESS_ERROR("业务异常"),
        NETWORK_ERROR("网络异常"),
        SERVICE_UNAVAILABLE("服务不可用");

        private final String description;

        AlertType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
