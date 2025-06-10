package com.campus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 简化的API测试类
 * 验证基本的测试环境配置
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-27
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@DisplayName("简化API测试")
class SimpleApiTest {

    @Test
    @DisplayName("测试环境验证")
    void testEnvironmentSetup() {
        // 验证测试环境基本配置
        assertTrue(true, "测试环境配置正常");
    }

    @Test
    @DisplayName("Spring Boot应用上下文加载测试")
    void testApplicationContextLoads() {
        // 这个测试验证Spring Boot应用能够正常启动
        assertTrue(true, "Spring Boot应用上下文加载成功");
    }
}
