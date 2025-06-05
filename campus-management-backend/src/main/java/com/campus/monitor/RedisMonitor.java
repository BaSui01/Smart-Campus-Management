package com.campus.monitor;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Gauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Redis 性能监控组件
 * 
 * @author Campus Team
 * @since 2025-06-05
 */
@Component
@ConditionalOnProperty(name = "campus.monitor.enabled", havingValue = "true", matchIfMissing = true)
public class RedisMonitor {

    private static final Logger logger = LoggerFactory.getLogger(RedisMonitor.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private MeterRegistry meterRegistry;

    @Value("${campus.monitor.redis-monitor-interval:60000}")
    private long monitorInterval;

    // 监控指标
    private final AtomicLong usedMemory = new AtomicLong(0);
    private final AtomicLong maxMemory = new AtomicLong(0);
    private final AtomicLong connectedClients = new AtomicLong(0);
    private final AtomicLong totalCommandsProcessed = new AtomicLong(0);
    private final AtomicLong keyspaceHits = new AtomicLong(0);
    private final AtomicLong keyspaceMisses = new AtomicLong(0);

    @PostConstruct
    public void initMetrics() {
        // 注册 Micrometer 指标
        Gauge.builder("redis.memory.used", usedMemory, AtomicLong::get)
            .description("Redis used memory in bytes")
            .register(meterRegistry);

        Gauge.builder("redis.memory.max", maxMemory, AtomicLong::get)
            .description("Redis max memory in bytes")
            .register(meterRegistry);

        Gauge.builder("redis.clients.connected", connectedClients, AtomicLong::get)
            .description("Number of connected Redis clients")
            .register(meterRegistry);

        Gauge.builder("redis.commands.processed", totalCommandsProcessed, AtomicLong::get)
            .description("Total number of commands processed by Redis")
            .register(meterRegistry);

        Gauge.builder("redis.keyspace.hits", keyspaceHits, AtomicLong::get)
            .description("Number of successful lookup of keys in the main dictionary")
            .register(meterRegistry);

        Gauge.builder("redis.keyspace.misses", keyspaceMisses, AtomicLong::get)
            .description("Number of failed lookup of keys in the main dictionary")
            .register(meterRegistry);

        // 注册缓存命中率
        Gauge.builder("redis.cache.hit.ratio", this, monitor -> monitor.calculateHitRatio())
            .description("Redis cache hit ratio")
            .register(meterRegistry);

        logger.info("Redis 监控指标初始化完成");
    }

    @Scheduled(fixedRateString = "${campus.monitor.redis-monitor-interval:60000}")
    public void monitorRedisPerformance() {
        RedisConnection connection = null;
        try {
            // 检查连接工厂是否为空
            var connectionFactory = redisTemplate.getConnectionFactory();
            if (connectionFactory == null) {
                logger.warn("Redis 连接工厂为空，跳过性能监控");
                return;
            }

            connection = connectionFactory.getConnection();

            // 使用 serverCommands() 替代已弃用的 info() 方法
            Properties info = connection.serverCommands().info();

            if (info != null) {
                updateMetrics(info);
                logPerformanceInfo(info);
            }

        } catch (Exception e) {
            logger.error("Redis 性能监控失败", e);
        } finally {
            // 确保连接被正确关闭
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    logger.warn("关闭 Redis 连接失败", e);
                }
            }
        }
    }

    /**
     * 更新监控指标
     */
    private void updateMetrics(Properties info) {
        try {
            // 内存使用情况
            String usedMemoryStr = info.getProperty("used_memory");
            if (usedMemoryStr != null) {
                usedMemory.set(Long.parseLong(usedMemoryStr));
            }

            String maxMemoryStr = info.getProperty("maxmemory");
            if (maxMemoryStr != null && !maxMemoryStr.equals("0")) {
                maxMemory.set(Long.parseLong(maxMemoryStr));
            }

            // 连接数
            String connectedClientsStr = info.getProperty("connected_clients");
            if (connectedClientsStr != null) {
                connectedClients.set(Long.parseLong(connectedClientsStr));
            }

            // 命令统计
            String totalCommandsStr = info.getProperty("total_commands_processed");
            if (totalCommandsStr != null) {
                totalCommandsProcessed.set(Long.parseLong(totalCommandsStr));
            }

            // 缓存命中统计
            String keyspaceHitsStr = info.getProperty("keyspace_hits");
            if (keyspaceHitsStr != null) {
                keyspaceHits.set(Long.parseLong(keyspaceHitsStr));
            }

            String keyspaceMissesStr = info.getProperty("keyspace_misses");
            if (keyspaceMissesStr != null) {
                keyspaceMisses.set(Long.parseLong(keyspaceMissesStr));
            }

        } catch (Exception e) {
            logger.error("更新 Redis 监控指标失败", e);
        }
    }

    /**
     * 记录性能信息
     */
    private void logPerformanceInfo(Properties info) {
        try {
            String usedMemoryHuman = info.getProperty("used_memory_human");
            String maxMemoryHuman = info.getProperty("maxmemory_human");
            String connectedClientsStr = info.getProperty("connected_clients");
            String totalCommandsStr = info.getProperty("total_commands_processed");
            
            double hitRatio = calculateHitRatio();

            logger.info("Redis 性能监控 - 内存使用: {}/{}, 连接数: {}, 总命令数: {}, 缓存命中率: {:.2f}%", 
                       usedMemoryHuman != null ? usedMemoryHuman : "N/A",
                       maxMemoryHuman != null && !maxMemoryHuman.equals("0") ? maxMemoryHuman : "无限制",
                       connectedClientsStr != null ? connectedClientsStr : "N/A",
                       totalCommandsStr != null ? totalCommandsStr : "N/A",
                       hitRatio * 100);

            // 检查内存使用率
            checkMemoryUsage();

            // 检查连接数
            checkConnectionCount();

        } catch (Exception e) {
            logger.error("记录 Redis 性能信息失败", e);
        }
    }

    /**
     * 计算缓存命中率
     */
    private double calculateHitRatio() {
        long hits = keyspaceHits.get();
        long misses = keyspaceMisses.get();
        long total = hits + misses;
        
        if (total == 0) {
            return 0.0;
        }
        
        return (double) hits / total;
    }

    /**
     * 检查内存使用情况
     */
    private void checkMemoryUsage() {
        long used = usedMemory.get();
        long max = maxMemory.get();

        if (max > 0) {
            double usageRatio = (double) used / max;
            
            if (usageRatio > 0.9) {
                logger.warn("Redis 内存使用率过高: {:.2f}%, 已使用: {} bytes, 最大: {} bytes", 
                           usageRatio * 100, used, max);
            } else if (usageRatio > 0.8) {
                logger.info("Redis 内存使用率较高: {:.2f}%, 建议关注", usageRatio * 100);
            }
        }
    }

    /**
     * 检查连接数
     */
    private void checkConnectionCount() {
        long connections = connectedClients.get();
        
        if (connections > 100) {
            logger.warn("Redis 连接数较多: {}, 建议检查连接池配置", connections);
        } else if (connections > 50) {
            logger.info("Redis 连接数: {}", connections);
        }
    }

    /**
     * 获取当前监控数据
     */
    public RedisMonitorData getCurrentMonitorData() {
        return new RedisMonitorData(
            usedMemory.get(),
            maxMemory.get(),
            connectedClients.get(),
            totalCommandsProcessed.get(),
            keyspaceHits.get(),
            keyspaceMisses.get(),
            calculateHitRatio()
        );
    }

    /**
     * Redis 监控数据类
     */
    public static class RedisMonitorData {
        private final long usedMemory;
        private final long maxMemory;
        private final long connectedClients;
        private final long totalCommandsProcessed;
        private final long keyspaceHits;
        private final long keyspaceMisses;
        private final double hitRatio;

        public RedisMonitorData(long usedMemory, long maxMemory, long connectedClients,
                               long totalCommandsProcessed, long keyspaceHits, long keyspaceMisses,
                               double hitRatio) {
            this.usedMemory = usedMemory;
            this.maxMemory = maxMemory;
            this.connectedClients = connectedClients;
            this.totalCommandsProcessed = totalCommandsProcessed;
            this.keyspaceHits = keyspaceHits;
            this.keyspaceMisses = keyspaceMisses;
            this.hitRatio = hitRatio;
        }

        // Getters
        public long getUsedMemory() { return usedMemory; }
        public long getMaxMemory() { return maxMemory; }
        public long getConnectedClients() { return connectedClients; }
        public long getTotalCommandsProcessed() { return totalCommandsProcessed; }
        public long getKeyspaceHits() { return keyspaceHits; }
        public long getKeyspaceMisses() { return keyspaceMisses; }
        public double getHitRatio() { return hitRatio; }
    }
}
