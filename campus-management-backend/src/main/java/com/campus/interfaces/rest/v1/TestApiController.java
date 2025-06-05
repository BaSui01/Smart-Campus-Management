package com.campus.interfaces.rest.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试API控制器 - 无需认证
 */
@RestController
@RequestMapping("/api/test")
@Tag(name = "测试接口", description = "用于测试API访问的接口，无需认证")
public class TestApiController {

    @GetMapping("/hello")
    @Operation(summary = "Hello接口", description = "简单的Hello World接口，用于测试API是否可访问")
    public Map<String, Object> hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Hello from Smart Campus Management API!");
        result.put("timestamp", LocalDateTime.now());
        result.put("version", "1.0.0");
        return result;
    }

    @GetMapping("/status")
    @Operation(summary = "系统状态", description = "获取系统运行状态信息")
    public Map<String, Object> status() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("status", "running");
        result.put("uptime", System.currentTimeMillis());
        result.put("timestamp", LocalDateTime.now());
        
        // 系统信息
        Map<String, Object> system = new HashMap<>();
        system.put("java_version", System.getProperty("java.version"));
        system.put("os_name", System.getProperty("os.name"));
        system.put("available_processors", Runtime.getRuntime().availableProcessors());
        system.put("max_memory", Runtime.getRuntime().maxMemory());
        system.put("total_memory", Runtime.getRuntime().totalMemory());
        system.put("free_memory", Runtime.getRuntime().freeMemory());
        
        result.put("system", system);
        return result;
    }

    @GetMapping("/ping")
    @Operation(summary = "Ping接口", description = "简单的ping接口，用于健康检查")
    public Map<String, Object> ping() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "pong");
        result.put("timestamp", LocalDateTime.now());
        return result;
    }
}
