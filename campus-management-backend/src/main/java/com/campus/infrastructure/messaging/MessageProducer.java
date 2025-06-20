package com.campus.infrastructure.messaging;

import com.campus.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息生产者服务
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送通知消息
     * 
     * @param userId 用户ID
     * @param title 标题
     * @param content 内容
     * @param type 通知类型
     */
    public void sendNotification(Long userId, String title, String content, String type) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("userId", userId);
            message.put("title", title);
            message.put("content", content);
            message.put("type", type);
            message.put("timestamp", LocalDateTime.now());
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.CAMPUS_EXCHANGE,
                RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                message
            );
            
            log.info("发送通知消息成功: userId={}, title={}", userId, title);
        } catch (Exception e) {
            log.error("发送通知消息失败: userId={}, title={}", userId, title, e);
        }
    }

    /**
     * 发送邮件消息
     * 
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     * @param template 模板
     */
    public void sendEmail(String to, String subject, String content, String template) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("to", to);
            message.put("subject", subject);
            message.put("content", content);
            message.put("template", template);
            message.put("timestamp", LocalDateTime.now());
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.CAMPUS_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                message
            );
            
            log.info("发送邮件消息成功: to={}, subject={}", to, subject);
        } catch (Exception e) {
            log.error("发送邮件消息失败: to={}, subject={}", to, subject, e);
        }
    }

    /**
     * 发送短信消息
     * 
     * @param phone 手机号
     * @param content 内容
     * @param template 模板
     */
    public void sendSms(String phone, String content, String template) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("phone", phone);
            message.put("content", content);
            message.put("template", template);
            message.put("timestamp", LocalDateTime.now());
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.CAMPUS_EXCHANGE,
                RabbitMQConfig.SMS_ROUTING_KEY,
                message
            );
            
            log.info("发送短信消息成功: phone={}", phone);
        } catch (Exception e) {
            log.error("发送短信消息失败: phone={}", phone, e);
        }
    }

    /**
     * 发送审计日志消息
     * 
     * @param userId 用户ID
     * @param operation 操作
     * @param module 模块
     * @param details 详情
     */
    public void sendAuditLog(Long userId, String operation, String module, Map<String, Object> details) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("userId", userId);
            message.put("operation", operation);
            message.put("module", module);
            message.put("details", details);
            message.put("timestamp", LocalDateTime.now());
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.CAMPUS_EXCHANGE,
                RabbitMQConfig.AUDIT_LOG_ROUTING_KEY,
                message
            );
            
            log.debug("发送审计日志消息成功: userId={}, operation={}", userId, operation);
        } catch (Exception e) {
            log.error("发送审计日志消息失败: userId={}, operation={}", userId, operation, e);
        }
    }

    /**
     * 发送文件处理消息
     * 
     * @param fileId 文件ID
     * @param operation 操作类型
     * @param params 参数
     */
    public void sendFileProcess(Long fileId, String operation, Map<String, Object> params) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("fileId", fileId);
            message.put("operation", operation);
            message.put("params", params);
            message.put("timestamp", LocalDateTime.now());
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.CAMPUS_EXCHANGE,
                RabbitMQConfig.FILE_PROCESS_ROUTING_KEY,
                message
            );
            
            log.info("发送文件处理消息成功: fileId={}, operation={}", fileId, operation);
        } catch (Exception e) {
            log.error("发送文件处理消息失败: fileId={}, operation={}", fileId, operation, e);
        }
    }

    /**
     * 发送数据同步消息
     * 
     * @param dataType 数据类型
     * @param dataId 数据ID
     * @param operation 操作类型
     * @param data 数据内容
     */
    public void sendDataSync(String dataType, Long dataId, String operation, Object data) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("dataType", dataType);
            message.put("dataId", dataId);
            message.put("operation", operation);
            message.put("data", data);
            message.put("timestamp", LocalDateTime.now());
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.CAMPUS_EXCHANGE,
                RabbitMQConfig.DATA_SYNC_ROUTING_KEY,
                message
            );
            
            log.info("发送数据同步消息成功: dataType={}, dataId={}, operation={}", dataType, dataId, operation);
        } catch (Exception e) {
            log.error("发送数据同步消息失败: dataType={}, dataId={}, operation={}", dataType, dataId, operation, e);
        }
    }

    /**
     * 发送延迟消息
     *
     * @param exchange 交换机
     * @param routingKey 路由键
     * @param message 消息
     * @param delayMillis 延迟时间（毫秒）
     */
    public void sendDelayedMessage(String exchange, String routingKey, Object message, long delayMillis) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, message, msg -> {
                // 使用新的延迟消息方式，通过消息头设置延迟
                msg.getMessageProperties().setHeader("x-delay", (int) delayMillis);
                return msg;
            });
            
            log.info("发送延迟消息成功: exchange={}, routingKey={}, delay={}ms", exchange, routingKey, delayMillis);
        } catch (Exception e) {
            log.error("发送延迟消息失败: exchange={}, routingKey={}", exchange, routingKey, e);
        }
    }

    /**
     * 发送批量通知
     * 
     * @param userIds 用户ID列表
     * @param title 标题
     * @param content 内容
     * @param type 通知类型
     */
    public void sendBatchNotification(java.util.List<Long> userIds, String title, String content, String type) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("userIds", userIds);
            message.put("title", title);
            message.put("content", content);
            message.put("type", type);
            message.put("timestamp", LocalDateTime.now());
            message.put("batch", true);
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.CAMPUS_EXCHANGE,
                RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                message
            );
            
            log.info("发送批量通知消息成功: userCount={}, title={}", userIds.size(), title);
        } catch (Exception e) {
            log.error("发送批量通知消息失败: userCount={}, title={}", userIds.size(), title, e);
        }
    }
}
