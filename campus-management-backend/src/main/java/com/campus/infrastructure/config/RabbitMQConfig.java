package com.campus.infrastructure.config;

import lombok.Data;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "campus.rabbitmq")
public class RabbitMQConfig {

    // 交换机名称
    public static final String CAMPUS_EXCHANGE = "campus.exchange";
    public static final String CAMPUS_DLX_EXCHANGE = "campus.dlx.exchange";

    // 队列名称
    public static final String NOTIFICATION_QUEUE = "campus.notification.queue";
    public static final String EMAIL_QUEUE = "campus.email.queue";
    public static final String SMS_QUEUE = "campus.sms.queue";
    public static final String AUDIT_LOG_QUEUE = "campus.audit.log.queue";
    public static final String FILE_PROCESS_QUEUE = "campus.file.process.queue";
    public static final String DATA_SYNC_QUEUE = "campus.data.sync.queue";

    // 死信队列
    public static final String NOTIFICATION_DLQ = "campus.notification.dlq";
    public static final String EMAIL_DLQ = "campus.email.dlq";
    public static final String SMS_DLQ = "campus.sms.dlq";

    // 路由键
    public static final String NOTIFICATION_ROUTING_KEY = "campus.notification";
    public static final String EMAIL_ROUTING_KEY = "campus.email";
    public static final String SMS_ROUTING_KEY = "campus.sms";
    public static final String AUDIT_LOG_ROUTING_KEY = "campus.audit.log";
    public static final String FILE_PROCESS_ROUTING_KEY = "campus.file.process";
    public static final String DATA_SYNC_ROUTING_KEY = "campus.data.sync";

    /**
     * 是否启用RabbitMQ
     */
    private boolean enabled = true;

    /**
     * 消息确认模式
     */
    private boolean publisherConfirms = true;

    /**
     * 消息返回模式
     */
    private boolean publisherReturns = true;

    /**
     * 消息转换器
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate配置
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        
        // 开启发送确认
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("消息发送成功: " + correlationData);
            } else {
                System.err.println("消息发送失败: " + cause);
            }
        });
        
        // 开启发送失败返回
        template.setReturnsCallback(returned -> {
            System.err.println("消息发送失败返回: " + returned.getMessage());
        });
        
        return template;
    }

    /**
     * 监听器容器工厂
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }

    // ================================
    // 交换机配置
    // ================================

    /**
     * 主交换机
     */
    @Bean
    public TopicExchange campusExchange() {
        return new TopicExchange(CAMPUS_EXCHANGE, true, false);
    }

    /**
     * 死信交换机
     */
    @Bean
    public TopicExchange campusDlxExchange() {
        return new TopicExchange(CAMPUS_DLX_EXCHANGE, true, false);
    }

    // ================================
    // 队列配置
    // ================================

    /**
     * 通知队列
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", CAMPUS_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", NOTIFICATION_DLQ)
                .withArgument("x-message-ttl", 300000) // 5分钟TTL
                .build();
    }

    /**
     * 邮件队列
     */
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE)
                .withArgument("x-dead-letter-exchange", CAMPUS_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", EMAIL_DLQ)
                .withArgument("x-message-ttl", 600000) // 10分钟TTL
                .build();
    }

    /**
     * 短信队列
     */
    @Bean
    public Queue smsQueue() {
        return QueueBuilder.durable(SMS_QUEUE)
                .withArgument("x-dead-letter-exchange", CAMPUS_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", SMS_DLQ)
                .withArgument("x-message-ttl", 300000) // 5分钟TTL
                .build();
    }

    /**
     * 审计日志队列
     */
    @Bean
    public Queue auditLogQueue() {
        return QueueBuilder.durable(AUDIT_LOG_QUEUE)
                .withArgument("x-message-ttl", 3600000) // 1小时TTL
                .build();
    }

    /**
     * 文件处理队列
     */
    @Bean
    public Queue fileProcessQueue() {
        return QueueBuilder.durable(FILE_PROCESS_QUEUE)
                .withArgument("x-message-ttl", 1800000) // 30分钟TTL
                .build();
    }

    /**
     * 数据同步队列
     */
    @Bean
    public Queue dataSyncQueue() {
        return QueueBuilder.durable(DATA_SYNC_QUEUE)
                .withArgument("x-message-ttl", 3600000) // 1小时TTL
                .build();
    }

    // ================================
    // 死信队列配置
    // ================================

    /**
     * 通知死信队列
     */
    @Bean
    public Queue notificationDlq() {
        return QueueBuilder.durable(NOTIFICATION_DLQ).build();
    }

    /**
     * 邮件死信队列
     */
    @Bean
    public Queue emailDlq() {
        return QueueBuilder.durable(EMAIL_DLQ).build();
    }

    /**
     * 短信死信队列
     */
    @Bean
    public Queue smsDlq() {
        return QueueBuilder.durable(SMS_DLQ).build();
    }

    // ================================
    // 绑定配置
    // ================================

    /**
     * 通知队列绑定
     */
    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(campusExchange())
                .with(NOTIFICATION_ROUTING_KEY);
    }

    /**
     * 邮件队列绑定
     */
    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(campusExchange())
                .with(EMAIL_ROUTING_KEY);
    }

    /**
     * 短信队列绑定
     */
    @Bean
    public Binding smsBinding() {
        return BindingBuilder.bind(smsQueue())
                .to(campusExchange())
                .with(SMS_ROUTING_KEY);
    }

    /**
     * 审计日志队列绑定
     */
    @Bean
    public Binding auditLogBinding() {
        return BindingBuilder.bind(auditLogQueue())
                .to(campusExchange())
                .with(AUDIT_LOG_ROUTING_KEY);
    }

    /**
     * 文件处理队列绑定
     */
    @Bean
    public Binding fileProcessBinding() {
        return BindingBuilder.bind(fileProcessQueue())
                .to(campusExchange())
                .with(FILE_PROCESS_ROUTING_KEY);
    }

    /**
     * 数据同步队列绑定
     */
    @Bean
    public Binding dataSyncBinding() {
        return BindingBuilder.bind(dataSyncQueue())
                .to(campusExchange())
                .with(DATA_SYNC_ROUTING_KEY);
    }

    /**
     * 死信队列绑定
     */
    @Bean
    public Binding notificationDlqBinding() {
        return BindingBuilder.bind(notificationDlq())
                .to(campusDlxExchange())
                .with(NOTIFICATION_DLQ);
    }

    @Bean
    public Binding emailDlqBinding() {
        return BindingBuilder.bind(emailDlq())
                .to(campusDlxExchange())
                .with(EMAIL_DLQ);
    }

    @Bean
    public Binding smsDlqBinding() {
        return BindingBuilder.bind(smsDlq())
                .to(campusDlxExchange())
                .with(SMS_DLQ);
    }
}
