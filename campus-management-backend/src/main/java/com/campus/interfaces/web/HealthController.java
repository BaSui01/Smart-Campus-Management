package com.campus.interfaces.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.application.service.UserService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 用于检查系统各组件的运行状态
 *
 * @author campus
 * @since 2025-06-05
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final UserService userService;
    private final DataSource dataSource;

    @Autowired
    public HealthController(UserService userService, DataSource dataSource) {
        this.userService = userService;
        this.dataSource = dataSource;
    }

    /**
     * 基础健康检查
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        try {
            result.put("status", "UP");
            result.put("timestamp", LocalDateTime.now());
            result.put("service", "Smart Campus Management");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("status", "DOWN");
            result.put("error", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 数据库连接检查
     */
    @GetMapping("/db")
    public ResponseEntity<Map<String, Object>> databaseHealth() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 尝试执行一个简单的数据库查询
            long userCount = userService.countTotalUsers();

            result.put("status", "UP");
            result.put("database", "Connected");
            result.put("userCount", userCount);
            result.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("status", "DOWN");
            result.put("database", "Disconnected");
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());
            result.put("timestamp", LocalDateTime.now());

            // 打印详细错误信息到控制台
            System.err.println("数据库连接检查失败: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 用户服务检查
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> userServiceHealth() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 尝试获取用户统计信息
            UserService.UserStatistics stats = userService.getUserStatistics();
            
            result.put("status", "UP");
            result.put("userService", "Available");
            result.put("totalUsers", stats.getTotalUsers());
            result.put("activeUsers", stats.getActiveUsers());
            result.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("status", "DOWN");
            result.put("userService", "Unavailable");
            result.put("error", e.getMessage());
            result.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 直接数据库连接测试
     */
    @GetMapping("/db-direct")
    public ResponseEntity<Map<String, Object>> directDatabaseTest() {
        Map<String, Object> result = new HashMap<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // 直接获取数据库连接
            connection = dataSource.getConnection();
            result.put("connectionObtained", true);

            // 测试简单查询
            statement = connection.prepareStatement("SELECT 1 as test_value");
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int testValue = resultSet.getInt("test_value");
                result.put("queryResult", testValue);
                result.put("status", "UP");
                result.put("database", "Connected");
            } else {
                result.put("status", "DOWN");
                result.put("database", "Query failed");
            }

            result.put("timestamp", LocalDateTime.now());
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            result.put("status", "DOWN");
            result.put("database", "Connection failed");
            result.put("error", e.getMessage());
            result.put("errorType", e.getClass().getSimpleName());
            result.put("timestamp", LocalDateTime.now());

            System.err.println("直接数据库连接测试失败: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(500).body(result);
        } finally {
            // 关闭资源
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                System.err.println("关闭数据库资源失败: " + e.getMessage());
            }
        }
    }
}
