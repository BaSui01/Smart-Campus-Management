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
    @Operation(summary = "系统状态接口", description = "获取系统运行状态信息")
    public Map<String, Object> status() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("status", "running");
        result.put("uptime", System.currentTimeMillis());
        result.put("memory", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        result.put("timestamp", LocalDateTime.now());
        return result;
    }

    @GetMapping("/time")
    @Operation(summary = "服务器时间接口", description = "获取服务器当前时间")
    public Map<String, Object> time() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("serverTime", LocalDateTime.now());
        result.put("timezone", java.time.ZoneId.systemDefault().toString());
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    @GetMapping("/ping")
    @Operation(summary = "Ping接口", description = "简单的ping健康检查接口")
    public Map<String, Object> ping() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "pong");
        result.put("timestamp", LocalDateTime.now());
        result.put("latency", "< 1ms");
        return result;
    }


}
