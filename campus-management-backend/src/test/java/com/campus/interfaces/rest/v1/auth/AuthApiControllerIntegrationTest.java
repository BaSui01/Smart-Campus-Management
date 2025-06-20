package com.campus.interfaces.rest.v1.auth;

import com.campus.domain.entity.auth.User;
import com.campus.domain.repository.auth.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证API控制器集成测试
 * 测试认证相关的REST API端点
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-18
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "campus.security.encryption.enabled=false",
    "campus.jwt.secret=test-jwt-secret-key-for-integration-testing"
})
@Transactional
class AuthApiControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        // 清理测试数据
        userRepository.deleteAll();
        
        // 创建测试用户
        createTestUser();
    }

    private void createTestUser() {
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setRealName("测试用户");
        testUser.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFVMLVZqpjxSOpE6.jVgKlW"); // password123
        testUser.setStatus(1);
        testUser.setDeleted(0);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(testUser);
    }

    @Test
    void testLogin_Success() throws Exception {
        // Given
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "testuser");
        loginRequest.put("password", "password123");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.user.username").value("testuser"))
                .andExpect(jsonPath("$.data.user.email").value("test@example.com"));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        // Given
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "testuser");
        loginRequest.put("password", "wrongpassword");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    @Test
    void testLogin_UserNotFound() throws Exception {
        // Given
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "nonexistentuser");
        loginRequest.put("password", "password123");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    @Test
    void testRegister_Success() throws Exception {
        // Given
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", "newuser");
        registerRequest.put("email", "newuser@example.com");
        registerRequest.put("phone", "13900139000");
        registerRequest.put("realName", "新用户");
        registerRequest.put("password", "password123");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.data.username").value("newuser"))
                .andExpect(jsonPath("$.data.email").value("newuser@example.com"));
    }

    @Test
    void testRegister_UsernameExists() throws Exception {
        // Given
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", "testuser"); // 已存在的用户名
        registerRequest.put("email", "newuser@example.com");
        registerRequest.put("phone", "13900139000");
        registerRequest.put("realName", "新用户");
        registerRequest.put("password", "password123");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户名已存在"));
    }

    @Test
    void testRegister_InvalidEmail() throws Exception {
        // Given
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", "newuser");
        registerRequest.put("email", "invalid-email");
        registerRequest.put("phone", "13900139000");
        registerRequest.put("realName", "新用户");
        registerRequest.put("password", "password123");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testRegister_InvalidPhone() throws Exception {
        // Given
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", "newuser");
        registerRequest.put("email", "newuser@example.com");
        registerRequest.put("phone", "invalid-phone");
        registerRequest.put("realName", "新用户");
        registerRequest.put("password", "password123");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testRegister_MissingRequiredFields() throws Exception {
        // Given
        Map<String, String> registerRequest = new HashMap<>();
        registerRequest.put("username", "newuser");
        // 缺少必填字段

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetCurrentUser_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/auth/current-user")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    void testGetCurrentUser_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/auth/current-user")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testChangePassword_Success() throws Exception {
        // Given
        Map<String, String> changePasswordRequest = new HashMap<>();
        changePasswordRequest.put("oldPassword", "password123");
        changePasswordRequest.put("newPassword", "newpassword123");

        // When & Then
        mockMvc.perform(put("/api/v1/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("密码修改成功"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testChangePassword_WrongOldPassword() throws Exception {
        // Given
        Map<String, String> changePasswordRequest = new HashMap<>();
        changePasswordRequest.put("oldPassword", "wrongpassword");
        changePasswordRequest.put("newPassword", "newpassword123");

        // When & Then
        mockMvc.perform(put("/api/v1/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("原密码不正确"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testLogout_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("退出登录成功"));
    }

    @Test
    void testRefreshToken_Success() throws Exception {
        // Given - 首先登录获取token
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "testuser");
        loginRequest.put("password", "password123");

        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 解析响应获取refreshToken
        @SuppressWarnings("unchecked")
        Map<String, Object> loginData = objectMapper.readValue(loginResponse, Map.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) loginData.get("data");
        String refreshToken = (String) data.get("refreshToken");

        // When & Then - 使用refreshToken刷新token
        Map<String, String> refreshRequest = new HashMap<>();
        refreshRequest.put("refreshToken", refreshToken);

        mockMvc.perform(post("/api/v1/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    @Test
    void testRefreshToken_InvalidToken() throws Exception {
        // Given
        Map<String, String> refreshRequest = new HashMap<>();
        refreshRequest.put("refreshToken", "invalid-refresh-token");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
}
