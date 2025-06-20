package com.campus;

import com.campus.config.TestCacheConfig;
import com.campus.config.TestJpaConfig;
import com.campus.config.TestJwtUtil;
import com.campus.config.TestSecurityConfig;
import com.campus.domain.entity.auth.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;



import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * API测试基础类
 * 提供通用的测试配置和工具方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-08
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Import({TestSecurityConfig.class, TestJpaConfig.class, TestCacheConfig.class})
@Transactional
public abstract class BaseApiTest {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected TestJwtUtil testJwtUtil;

    protected MockMvc mockMvc;
    protected String adminToken;
    protected String studentToken;
    protected String teacherToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        // 创建测试用户和令牌
        setupTestUsers();
    }

    /**
     * 设置测试用户和JWT令牌
     */
    private void setupTestUsers() {
        try {
            // 创建管理员令牌
            adminToken = "Bearer " + testJwtUtil.generateToken(1L, "admin", "ADMIN");

            // 创建学生令牌
            studentToken = "Bearer " + testJwtUtil.generateToken(2L, "student", "STUDENT");

            // 创建教师令牌
            teacherToken = "Bearer " + testJwtUtil.generateToken(3L, "teacher", "TEACHER");

        } catch (Exception e) {
            // 如果JWT生成失败，使用模拟令牌
            // 注意：这些是测试专用的模拟令牌，不会影响生产环境
            adminToken = "Bearer test-admin-token-" + System.currentTimeMillis();
            studentToken = "Bearer test-student-token-" + System.currentTimeMillis();
            teacherToken = "Bearer test-teacher-token-" + System.currentTimeMillis();

            System.out.println("警告：JWT生成失败，使用模拟令牌进行测试: " + e.getMessage());
        }
    }

    /**
     * 将对象转换为JSON字符串
     */
    protected String asJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }

    /**
     * 获取JSON内容类型
     */
    protected MediaType getJsonContentType() {
        return MediaType.APPLICATION_JSON;
    }

    /**
     * 创建测试用户
     */
    protected User createTestUser(String username, String email, String role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setRealName("Test " + username);
        user.setPassword("password123");
        user.setStatus(1);
        return user;
    }

    /**
     * 获取管理员授权头
     */
    protected String getAdminAuthHeader() {
        return adminToken;
    }

    /**
     * 获取学生授权头
     */
    protected String getStudentAuthHeader() {
        return studentToken;
    }

    /**
     * 获取教师授权头
     */
    protected String getTeacherAuthHeader() {
        return teacherToken;
    }
}
