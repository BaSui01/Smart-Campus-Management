package com.campus.infrastructure.monitoring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统监控服务
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemMonitorService implements HealthIndicator {

    private static final double CPU_THRESHOLD = 80.0;
    private static final double MEMORY_THRESHOLD = 85.0;
    @SuppressWarnings("unused")
    private static final double DISK_THRESHOLD = 90.0;

    /**
     * 获取系统监控指标
     * 
     * @return 监控指标
     */
    public Map<String, Object> getSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        try {
            // CPU指标
            metrics.put("cpu", getCpuMetrics());
            
            // 内存指标
            metrics.put("memory", getMemoryMetrics());
            
            // JVM指标
            metrics.put("jvm", getJvmMetrics());
            
            // 系统信息
            metrics.put("system", getSystemInfo());
            
            // 运行时间
            metrics.put("uptime", getUptimeMetrics());
            
        } catch (Exception e) {
            log.error("获取系统监控指标失败", e);
            metrics.put("error", e.getMessage());
        }
        
        return metrics;
    }

    /**
     * 获取CPU指标
     */
    private Map<String, Object> getCpuMetrics() {
        Map<String, Object> cpuMetrics = new HashMap<>();
        
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            
            // 系统CPU使用率
            double systemCpuLoad = osBean.getSystemLoadAverage();
            cpuMetrics.put("systemLoadAverage", systemCpuLoad);
            
            // 可用处理器数量
            int availableProcessors = osBean.getAvailableProcessors();
            cpuMetrics.put("availableProcessors", availableProcessors);
            
            // CPU使用率百分比
            if (systemCpuLoad > 0) {
                double cpuUsagePercent = (systemCpuLoad / availableProcessors) * 100;
                cpuMetrics.put("cpuUsagePercent", Math.round(cpuUsagePercent * 100.0) / 100.0);
                cpuMetrics.put("cpuStatus", cpuUsagePercent > CPU_THRESHOLD ? "WARNING" : "NORMAL");
            }
            
        } catch (Exception e) {
            log.warn("获取CPU指标失败", e);
            cpuMetrics.put("error", e.getMessage());
        }
        
        return cpuMetrics;
    }

    /**
     * 获取内存指标
     */
    private Map<String, Object> getMemoryMetrics() {
        Map<String, Object> memoryMetrics = new HashMap<>();
        
        try {
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            
            // 堆内存
            long heapUsed = memoryBean.getHeapMemoryUsage().getUsed();
            long heapMax = memoryBean.getHeapMemoryUsage().getMax();
            long heapCommitted = memoryBean.getHeapMemoryUsage().getCommitted();
            
            memoryMetrics.put("heapUsed", formatBytes(heapUsed));
            memoryMetrics.put("heapMax", formatBytes(heapMax));
            memoryMetrics.put("heapCommitted", formatBytes(heapCommitted));
            
            double heapUsagePercent = (double) heapUsed / heapMax * 100;
            memoryMetrics.put("heapUsagePercent", Math.round(heapUsagePercent * 100.0) / 100.0);
            memoryMetrics.put("heapStatus", heapUsagePercent > MEMORY_THRESHOLD ? "WARNING" : "NORMAL");
            
            // 非堆内存
            long nonHeapUsed = memoryBean.getNonHeapMemoryUsage().getUsed();
            long nonHeapMax = memoryBean.getNonHeapMemoryUsage().getMax();
            long nonHeapCommitted = memoryBean.getNonHeapMemoryUsage().getCommitted();
            
            memoryMetrics.put("nonHeapUsed", formatBytes(nonHeapUsed));
            memoryMetrics.put("nonHeapMax", nonHeapMax > 0 ? formatBytes(nonHeapMax) : "unlimited");
            memoryMetrics.put("nonHeapCommitted", formatBytes(nonHeapCommitted));
            
        } catch (Exception e) {
            log.warn("获取内存指标失败", e);
            memoryMetrics.put("error", e.getMessage());
        }
        
        return memoryMetrics;
    }

    /**
     * 获取JVM指标
     */
    private Map<String, Object> getJvmMetrics() {
        Map<String, Object> jvmMetrics = new HashMap<>();
        
        try {
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            
            jvmMetrics.put("jvmName", runtimeBean.getVmName());
            jvmMetrics.put("jvmVersion", runtimeBean.getVmVersion());
            jvmMetrics.put("jvmVendor", runtimeBean.getVmVendor());
            jvmMetrics.put("startTime", runtimeBean.getStartTime());
            jvmMetrics.put("uptime", formatDuration(runtimeBean.getUptime()));
            
            // GC信息
            ManagementFactory.getGarbageCollectorMXBeans().forEach(gcBean -> {
                String gcName = gcBean.getName().replaceAll("\\s+", "");
                Map<String, Object> gcInfo = new HashMap<>();
                gcInfo.put("collectionCount", gcBean.getCollectionCount());
                gcInfo.put("collectionTime", gcBean.getCollectionTime());
                jvmMetrics.put("gc" + gcName, gcInfo);
            });
            
        } catch (Exception e) {
            log.warn("获取JVM指标失败", e);
            jvmMetrics.put("error", e.getMessage());
        }
        
        return jvmMetrics;
    }

    /**
     * 获取系统信息
     */
    private Map<String, Object> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        
        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            
            systemInfo.put("osName", osBean.getName());
            systemInfo.put("osVersion", osBean.getVersion());
            systemInfo.put("osArch", osBean.getArch());
            
            // Java信息
            systemInfo.put("javaVersion", System.getProperty("java.version"));
            systemInfo.put("javaVendor", System.getProperty("java.vendor"));
            systemInfo.put("javaHome", System.getProperty("java.home"));
            
            // 用户信息
            systemInfo.put("userName", System.getProperty("user.name"));
            systemInfo.put("userDir", System.getProperty("user.dir"));
            systemInfo.put("userTimezone", System.getProperty("user.timezone"));
            
        } catch (Exception e) {
            log.warn("获取系统信息失败", e);
            systemInfo.put("error", e.getMessage());
        }
        
        return systemInfo;
    }

    /**
     * 获取运行时间指标
     */
    private Map<String, Object> getUptimeMetrics() {
        Map<String, Object> uptimeMetrics = new HashMap<>();
        
        try {
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            
            long uptime = runtimeBean.getUptime();
            long startTime = runtimeBean.getStartTime();
            
            uptimeMetrics.put("startTime", startTime);
            uptimeMetrics.put("uptime", uptime);
            uptimeMetrics.put("uptimeFormatted", formatDuration(uptime));
            
        } catch (Exception e) {
            log.warn("获取运行时间指标失败", e);
            uptimeMetrics.put("error", e.getMessage());
        }
        
        return uptimeMetrics;
    }

    /**
     * 定时监控系统状态
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void monitorSystemStatus() {
        try {
            Map<String, Object> metrics = getSystemMetrics();
            
            // 检查CPU使用率
            Object cpuMetricsObj = metrics.get("cpu");
            @SuppressWarnings("unchecked")
            Map<String, Object> cpuMetrics = cpuMetricsObj instanceof Map ? (Map<String, Object>) cpuMetricsObj : null;
            if (cpuMetrics != null && "WARNING".equals(cpuMetrics.get("cpuStatus"))) {
                log.warn("CPU使用率过高: {}%", cpuMetrics.get("cpuUsagePercent"));
                // 这里可以发送告警通知
            }

            // 检查内存使用率
            Object memoryMetricsObj = metrics.get("memory");
            @SuppressWarnings("unchecked")
            Map<String, Object> memoryMetrics = memoryMetricsObj instanceof Map ? (Map<String, Object>) memoryMetricsObj : null;
            if (memoryMetrics != null && "WARNING".equals(memoryMetrics.get("heapStatus"))) {
                log.warn("内存使用率过高: {}%", memoryMetrics.get("heapUsagePercent"));
                // 这里可以发送告警通知
            }
            
        } catch (Exception e) {
            log.error("系统状态监控失败", e);
        }
    }

    @Override
    public Health health() {
        try {
            Map<String, Object> metrics = getSystemMetrics();
            
            // 检查系统健康状态
            Object cpuMetricsObj = metrics.get("cpu");
            @SuppressWarnings("unchecked")
            Map<String, Object> cpuMetrics = cpuMetricsObj instanceof Map ? (Map<String, Object>) cpuMetricsObj : null;
            Object memoryMetricsObj = metrics.get("memory");
            @SuppressWarnings("unchecked")
            Map<String, Object> memoryMetrics = memoryMetricsObj instanceof Map ? (Map<String, Object>) memoryMetricsObj : null;
            
            boolean cpuHealthy = cpuMetrics == null || !"WARNING".equals(cpuMetrics.get("cpuStatus"));
            boolean memoryHealthy = memoryMetrics == null || !"WARNING".equals(memoryMetrics.get("heapStatus"));
            
            if (cpuHealthy && memoryHealthy) {
                return Health.up()
                        .withDetail("cpu", cpuMetrics)
                        .withDetail("memory", memoryMetrics)
                        .build();
            } else {
                return Health.down()
                        .withDetail("cpu", cpuMetrics)
                        .withDetail("memory", memoryMetrics)
                        .withDetail("reason", "系统资源使用率过高")
                        .build();
            }
            
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }

    /**
     * 格式化字节数
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * 格式化持续时间
     */
    private String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return String.format("%d天%d小时%d分钟", days, hours % 24, minutes % 60);
        } else if (hours > 0) {
            return String.format("%d小时%d分钟", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d分钟%d秒", minutes, seconds % 60);
        } else {
            return String.format("%d秒", seconds);
        }
    }
}
