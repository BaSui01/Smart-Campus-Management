package com.campus.application.service.auth;

import com.campus.domain.entity.auth.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务简化测试类
 * 测试用户管理相关的核心业务逻辑
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-20
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    /**
     * 测试用户服务基本功能
     */
    @Test
    void testUserServiceBasicFunctionality() {
        // 测试服务是否正确注入
        assertNotNull(userService, "UserService应该被正确注入");

        // 测试基本方法是否存在（不会抛出异常）
        assertDoesNotThrow(() -> {
            userService.countTotalUsers();
        }, "countTotalUsers方法应该正常执行");
    }

    /**
     * 测试用户实体创建
     */
    @Test
    void testUserEntityCreation() {
        // 测试User实体的基本功能
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRealName("测试用户");

        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("测试用户", user.getRealName());
    }
}
