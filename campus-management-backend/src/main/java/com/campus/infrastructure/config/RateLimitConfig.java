package com.campus.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 限流配置类
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "campus.rate-limit")
public class RateLimitConfig {

    /**
     * 是否启用限流
     */
    private boolean enabled = true;

    /**
     * 全局默认限流配置
     */
    private GlobalConfig global = new GlobalConfig();

    /**
     * API特定限流配置
     */
    private Map<String, ApiConfig> apis = new HashMap<>();

    /**
     * IP限流配置
     */
    private IpConfig ip = new IpConfig();

    /**
     * 用户限流配置
     */
    private UserConfig user = new UserConfig();

    @Data
    public static class GlobalConfig {
        /**
         * 默认限流次数
         */
        private int count = 1000;

        /**
         * 默认限流时间窗口（秒）
         */
        private int time = 3600;

        /**
         * 默认限流消息
         */
        private String message = "系统繁忙，请稍后再试";
    }

    @Data
    public static class ApiConfig {
        /**
         * 限流次数
         */
        private int count;

        /**
         * 限流时间窗口（秒）
         */
        private int time;

        /**
         * 限流消息
         */
        private String message;
    }

    @Data
    public static class IpConfig {
        /**
         * IP限流次数
         */
        private int count = 100;

        /**
         * IP限流时间窗口（秒）
         */
        private int time = 60;

        /**
         * IP限流消息
         */
        private String message = "您的访问过于频繁，请稍后再试";

        /**
         * IP白名单
         */
        private String[] whitelist = {};
    }

    @Data
    public static class UserConfig {
        /**
         * 用户限流次数
         */
        private int count = 200;

        /**
         * 用户限流时间窗口（秒）
         */
        private int time = 60;

        /**
         * 用户限流消息
         */
        private String message = "您的操作过于频繁，请稍后再试";

        /**
         * VIP用户限流次数
         */
        private int vipCount = 500;

        /**
         * 管理员用户限流次数
         */
        private int adminCount = 1000;
    }
}
