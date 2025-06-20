package com.campus.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

/**
 * 服务发现配置
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Data
@Configuration
@EnableDiscoveryClient
@ConfigurationProperties(prefix = "campus.discovery")
public class ServiceDiscoveryConfig {

    /**
     * 是否启用服务发现
     */
    private boolean enabled = true;

    /**
     * 服务名称
     */
    private String serviceName = "campus-management-service";

    /**
     * 服务版本
     */
    private String version = "1.0.0";

    /**
     * 服务描述
     */
    private String description = "智慧校园管理系统";

    /**
     * 健康检查配置
     */
    private HealthCheck healthCheck = new HealthCheck();

    /**
     * 负载均衡配置
     */
    private LoadBalancer loadBalancer = new LoadBalancer();

    @Data
    public static class HealthCheck {
        /**
         * 健康检查路径
         */
        private String path = "/actuator/health";

        /**
         * 健康检查间隔（秒）
         */
        private int interval = 30;

        /**
         * 健康检查超时（秒）
         */
        private int timeout = 10;

        /**
         * 失败阈值
         */
        private int failureThreshold = 3;
    }

    @Data
    public static class LoadBalancer {
        /**
         * 负载均衡策略
         */
        private String strategy = "round_robin";

        /**
         * 权重
         */
        private int weight = 100;

        /**
         * 是否启用粘性会话
         */
        private boolean stickySession = false;
    }
}
