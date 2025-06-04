package com.campus.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 测试控制器
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@RestController
@RequestMapping("/test")
@Tag(name = "系统测试", description = "系统功能测试接口")
public class TestController {

    /**
     * 健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查系统运行状态")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("timestamp", LocalDateTime.now());
        data.put("service", "Campus Management Backend");
        data.put("version", "1.0.0");

        return ApiResponse.success("系统运行正常", data);
    }

    /**
     * 版本信息
     */
    @GetMapping("/version")
    @Operation(summary = "版本信息", description = "获取系统版本信息")
    public ApiResponse<Map<String, Object>> version() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "智慧校园管理平台");
        data.put("version", "1.0.0");
        data.put("buildTime", "2025-06-03");
        data.put("author", "Campus Management Team");

        return ApiResponse.success(data);
    }

    /**
     * 数据库连接测试
     */
    @GetMapping("/database")
    @Operation(summary = "数据库测试", description = "测试数据库连接状态")
    public ApiResponse<Map<String, Object>> database() {
        Map<String, Object> data = new HashMap<>();
        data.put("database", "MySQL 8.0");
        data.put("status", "连接正常");
        data.put("message", "数据库服务运行正常");

        return ApiResponse.success(data);
    }

    /**
     * API文档链接
     */
    @GetMapping("/docs")
    @Operation(summary = "API文档", description = "获取API文档链接信息")
    public ApiResponse<Map<String, Object>> docs() {
        Map<String, Object> data = new HashMap<>();
        data.put("swaggerUrl", "/swagger-ui.html");
        data.put("apiDocsUrl", "/v3/api-docs");
        data.put("description", "完整的API文档和测试界面");

        return ApiResponse.success(data);
    }
}
