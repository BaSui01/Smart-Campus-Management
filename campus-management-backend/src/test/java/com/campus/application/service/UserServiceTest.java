package com.campus.application.service;

import com.campus.application.service.impl.UserServiceImpl;
import com.campus.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserService集成测试
 * 测试用户服务的业务逻辑
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UserServiceImpl.class, BCryptPasswordEncoder.class})
class UserServiceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setRealName("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setStatus(1);
        testUser.setDeleted(0);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testUser = entityManager.persistAndFlush(testUser);
    }

    @Test
    void testFindByUsername() {
        // 测试根据用户名查找用户
        Optional<User> result = userService.findByUsername("testuser");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void testAuthenticate() {
        // 测试用户认证
        User result1 = userService.authenticate("testuser", "password123");
        User result2 = userService.authenticate("testuser", "wrongpassword");

        assertThat(result1).isNotNull();
        assertThat(result1.getUsername()).isEqualTo("testuser");

        assertThat(result2).isNull(); // 密码错误
    }

    @Test
    void testCreateUser() {
        // 测试创建用户
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("newpassword");
        newUser.setRealName("新用户");
        newUser.setEmail("new@example.com");
        newUser.setStatus(1);

        User result = userService.createUser(newUser);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUsername()).isEqualTo("newuser");
        assertThat(result.getRealName()).isEqualTo("新用户");
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();

        // 验证密码已加密
        assertThat(passwordEncoder.matches("newpassword", result.getPassword())).isTrue();
    }
}
