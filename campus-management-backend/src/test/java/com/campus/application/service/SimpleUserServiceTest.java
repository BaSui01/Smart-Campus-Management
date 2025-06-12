package com.campus.application.service;

import com.campus.application.Implement.auth.UserServiceImpl;
import com.campus.domain.entity.auth.User;
import com.campus.domain.repository.auth.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;

/**
 * UserService单元测试
 * 使用Mock对象测试业务逻辑
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@ExtendWith(MockitoExtension.class)
class SimpleUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private PasswordEncoder passwordEncoder;
    private User testUser;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        
        // 使用反射设置passwordEncoder
        try {
            java.lang.reflect.Field field = UserServiceImpl.class.getDeclaredField("passwordEncoder");
            field.setAccessible(true);
            field.set(userService, passwordEncoder);
        } catch (Exception e) {
            // 忽略反射异常
        }

        // 创建测试用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setRealName("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setStatus(1);
        testUser.setDeleted(0);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testFindByUsername() {
        // 模拟Repository返回
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // 执行测试
        Optional<User> result = userService.findByUsername("testuser");

        // 验证结果
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
        assertThat(result.get().getRealName()).isEqualTo("测试用户");
    }

    @Test
    void testFindByUsernameNotFound() {
        // 模拟Repository返回空
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // 执行测试
        Optional<User> result = userService.findByUsername("nonexistent");

        // 验证结果
        assertThat(result).isEmpty();
    }

    @Test
    void testAuthenticate() {
        // 模拟Repository返回用户
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // 测试正确密码
        User result1 = userService.authenticate("testuser", "password123");
        assertThat(result1).isNotNull();
        assertThat(result1.getUsername()).isEqualTo("testuser");

        // 测试错误密码
        User result2 = userService.authenticate("testuser", "wrongpassword");
        assertThat(result2).isNull();
    }

    @Test
    void testAuthenticateUserNotFound() {
        // 模拟Repository返回空
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // 执行测试
        User result = userService.authenticate("nonexistent", "password123");

        // 验证结果
        assertThat(result).isNull();
    }

    @Test
    void testCreateUser() {
        // 创建新用户
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("newpassword");
        newUser.setRealName("新用户");
        newUser.setEmail("new@example.com");
        newUser.setStatus(1);

        // 模拟Repository检查（UserServiceImpl实际上没有调用这些方法，所以注释掉）
        // when(userRepository.existsByUsername("newuser")).thenReturn(false);
        // when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        
        // 模拟保存返回
        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setUsername("newuser");
        savedUser.setPassword(passwordEncoder.encode("newpassword"));
        savedUser.setRealName("新用户");
        savedUser.setEmail("new@example.com");
        savedUser.setStatus(1);
        savedUser.setDeleted(0);
        savedUser.setCreatedAt(LocalDateTime.now());
        savedUser.setUpdatedAt(LocalDateTime.now());
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // 执行测试
        User result = userService.createUser(newUser);

        // 验证结果
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUsername()).isEqualTo("newuser");
        assertThat(result.getRealName()).isEqualTo("新用户");
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    @Test
    void testPasswordEncryption() {
        // 测试密码加密功能
        String plainPassword = "testpassword123";
        String encryptedPassword = passwordEncoder.encode(plainPassword);
        
        assertThat(encryptedPassword).isNotEqualTo(plainPassword);
        assertThat(passwordEncoder.matches(plainPassword, encryptedPassword)).isTrue();
        assertThat(passwordEncoder.matches("wrongpassword", encryptedPassword)).isFalse();
    }

    @Test
    void testUserValidation() {
        // 测试用户对象的基本验证
        assertThat(testUser.getId()).isEqualTo(1L);
        assertThat(testUser.getUsername()).isEqualTo("testuser");
        assertThat(testUser.getRealName()).isEqualTo("测试用户");
        assertThat(testUser.getEmail()).isEqualTo("test@example.com");
        assertThat(testUser.getPhone()).isEqualTo("13800138000");
        assertThat(testUser.getStatus()).isEqualTo(1);
        assertThat(testUser.getDeleted()).isEqualTo(0);
        assertThat(testUser.getCreatedAt()).isNotNull();
        assertThat(testUser.getUpdatedAt()).isNotNull();
    }
}
