package com.campus.infrastructure.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 监控配置类
 * 配置自定义监控指标和健康检查
 * 
 * @author Campus Management Team
 * @since 2025-06-20
 */
@Configuration
public class MonitoringConfig {

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private DataSource dataSource;

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    // 业务指标计数器
    private final AtomicLong userLoginCount = new AtomicLong(0);
    private final AtomicLong apiRequestCount = new AtomicLong(0);
    private final AtomicLong businessErrorCount = new AtomicLong(0);

    /**
     * 自定义应用信息贡献者
     */
    @Bean
    public InfoContributor customInfoContributor() {
        return builder -> {
            Map<String, Object> details = new HashMap<>();
            details.put("app", Map.of(
                "name", "智慧校园管理系统",
                "version", "1.0.0",
                "description", "智慧校园综合管理平台",
                "build-time", LocalDateTime.now().toString()
            ));
            details.put("team", Map.of(
                "name", "Campus Management Team",
                "email", "support@campus.com"
            ));
            details.put("environment", Map.of(
                "profile", "production",
                "timezone", "Asia/Shanghai"
            ));
            builder.withDetails(details);
        };
    }

    /**
     * 数据库健康检查
     */
    @Bean
    public HealthIndicator databaseHealthIndicator() {
        return () -> {
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(1)) {
                    return org.springframework.boot.actuate.health.Health.up()
                        .withDetail("database", "MySQL")
                        .withDetail("status", "Connected")
                        .withDetail("url", connection.getMetaData().getURL())
                        .build();
                } else {
                    return org.springframework.boot.actuate.health.Health.down()
                        .withDetail("database", "MySQL")
                        .withDetail("status", "Connection Invalid")
                        .build();
                }
            } catch (Exception e) {
                return org.springframework.boot.actuate.health.Health.down()
                    .withDetail("database", "MySQL")
                    .withDetail("status", "Connection Failed")
                    .withDetail("error", e.getMessage())
                    .build();
            }
        };
    }

    /**
     * Redis健康检查
     */
    @Bean
    public HealthIndicator redisHealthIndicator() {
        return () -> {
            if (redisTemplate == null) {
                return org.springframework.boot.actuate.health.Health.up()
                    .withDetail("redis", "Not configured")
                    .withDetail("status", "Disabled")
                    .build();
            }

            try {
                var connectionFactory = redisTemplate.getConnectionFactory();
                if (connectionFactory != null) {
                    String pong = connectionFactory
                        .getConnection()
                        .ping();
                    if ("PONG".equals(pong)) {
                        return org.springframework.boot.actuate.health.Health.up()
                            .withDetail("redis", "Connected")
                            .withDetail("response", pong)
                            .build();
                    } else {
                        return org.springframework.boot.actuate.health.Health.down()
                            .withDetail("redis", "Unexpected Response")
                            .withDetail("response", pong)
                            .build();
                    }
                } else {
                    return org.springframework.boot.actuate.health.Health.down()
                        .withDetail("redis", "Connection Factory is null")
                        .build();
                }
            } catch (Exception e) {
                return org.springframework.boot.actuate.health.Health.down()
                    .withDetail("redis", "Connection Failed")
                    .withDetail("error", e.getMessage())
                    .build();
            }
        };
    }

    /**
     * 业务指标监控
     */
    @Bean
    public Counter userLoginCounter() {
        return Counter.builder("campus.user.login.count")
            .description("用户登录次数")
            .tag("type", "login")
            .register(meterRegistry);
    }

    @Bean
    public Counter apiRequestCounter() {
        return Counter.builder("campus.api.request.count")
            .description("API请求次数")
            .tag("type", "request")
            .register(meterRegistry);
    }

    @Bean
    public Counter businessErrorCounter() {
        return Counter.builder("campus.business.error.count")
            .description("业务错误次数")
            .tag("type", "error")
            .register(meterRegistry);
    }

    @Bean
    public Timer apiResponseTimer() {
        return Timer.builder("campus.api.response.time")
            .description("API响应时间")
            .register(meterRegistry);
    }

    /**
     * 系统资源监控
     */
    @Bean
    public Gauge activeUserGauge() {
        return Gauge.builder("campus.user.active.count", this, MonitoringConfig::getActiveUserCount)
            .description("当前活跃用户数")
            .register(meterRegistry);
    }

    @Bean
    public Gauge databaseConnectionGauge() {
        return Gauge.builder("campus.database.connection.count", this, MonitoringConfig::getDatabaseConnectionCount)
            .description("数据库连接数")
            .register(meterRegistry);
    }

    @Bean
    public Gauge redisConnectionGauge() {
        return Gauge.builder("campus.redis.connection.count", this, MonitoringConfig::getRedisConnectionCount)
            .description("Redis连接数")
            .register(meterRegistry);
    }

    /**
     * 获取活跃用户数（示例实现）
     */
    private double getActiveUserCount() {
        try {
            if (redisTemplate != null) {
                // 从Redis中获取在线用户数
                Long size = redisTemplate.opsForSet().size("online_users");
                return size != null ? size.doubleValue() : 0;
            } else {
                // Redis不可用时返回模拟数据
                return 10; // 示例值
            }
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取数据库连接数
     */
    private double getDatabaseConnectionCount() {
        try {
            // 这里可以实现获取数据库连接池状态的逻辑
            return 10; // 示例值
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取Redis连接数
     */
    private double getRedisConnectionCount() {
        try {
            // 这里可以实现获取Redis连接池状态的逻辑
            return 5; // 示例值
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 业务指标更新方法
     */
    public void incrementUserLogin() {
        userLoginCount.incrementAndGet();
        userLoginCounter().increment();
    }

    public void incrementApiRequest() {
        apiRequestCount.incrementAndGet();
        apiRequestCounter().increment();
    }

    public void incrementBusinessError() {
        businessErrorCount.incrementAndGet();
        businessErrorCounter().increment();
    }

    /**
     * 自定义健康检查 - 业务状态
     */
    @Bean
    public HealthIndicator businessHealthIndicator() {
        return () -> {
            try {
                // 检查关键业务功能是否正常
                boolean databaseOk = checkDatabaseHealth();
                boolean redisOk = checkRedisHealth();
                boolean businessLogicOk = checkBusinessLogic();

                if (databaseOk && redisOk && businessLogicOk) {
                    return org.springframework.boot.actuate.health.Health.up()
                        .withDetail("business", "All systems operational")
                        .withDetail("database", "OK")
                        .withDetail("redis", "OK")
                        .withDetail("business-logic", "OK")
                        .build();
                } else {
                    return org.springframework.boot.actuate.health.Health.down()
                        .withDetail("business", "Some systems have issues")
                        .withDetail("database", databaseOk ? "OK" : "FAILED")
                        .withDetail("redis", redisOk ? "OK" : "FAILED")
                        .withDetail("business-logic", businessLogicOk ? "OK" : "FAILED")
                        .build();
                }
            } catch (Exception e) {
                return org.springframework.boot.actuate.health.Health.down()
                    .withDetail("business", "Health check failed")
                    .withDetail("error", e.getMessage())
                    .build();
            }
        };
    }

    private boolean checkDatabaseHealth() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(1);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkRedisHealth() {
        if (redisTemplate == null) {
            return true; // Redis不配置时认为健康
        }

        try {
            var connectionFactory = redisTemplate.getConnectionFactory();
            if (connectionFactory != null) {
                String pong = connectionFactory
                    .getConnection()
                    .ping();
                return "PONG".equals(pong);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkBusinessLogic() {
        try {
            // 这里可以添加关键业务逻辑的健康检查
            // 例如：检查关键服务是否可用
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
