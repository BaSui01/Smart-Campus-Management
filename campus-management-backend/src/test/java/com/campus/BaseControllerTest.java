package com.campus;

import com.campus.shared.common.ApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 控制器测试基类
 * 提供通用的测试工具方法和配置
 *
 * @author Campus Management Team
 * @since 2025-06-09
 */
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public abstract class BaseControllerTest {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected ObjectMapper objectMapper;

    protected MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    /**
     * 执行GET请求并返回ApiResponse
     */
    protected <T> ApiResponse<T> performGetAndGetApiResponse(String url, TypeReference<ApiResponse<T>> typeRef) throws Exception {
        MvcResult result = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        return objectMapper.readValue(content, typeRef);
    }

    /**
     * 执行POST请求并返回ApiResponse
     */
    protected <T> ApiResponse<T> performPostAndGetApiResponse(String url, Object requestBody, TypeReference<ApiResponse<T>> typeRef) throws Exception {
        MvcResult result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        return objectMapper.readValue(content, typeRef);
    }

    /**
     * 执行PUT请求并返回ApiResponse
     */
    protected <T> ApiResponse<T> performPutAndGetApiResponse(String url, Object requestBody, TypeReference<ApiResponse<T>> typeRef) throws Exception {
        MvcResult result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        return objectMapper.readValue(content, typeRef);
    }

    /**
     * 执行DELETE请求并返回ApiResponse
     */
    protected <T> ApiResponse<T> performDeleteAndGetApiResponse(String url, TypeReference<ApiResponse<T>> typeRef) throws Exception {
        MvcResult result = mockMvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        return objectMapper.readValue(content, typeRef);
    }

    /**
     * 创建测试用的JSON字符串
     */
    protected String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * 模拟管理员用户
     */
    @WithMockUser(roles = {"ADMIN", "SYSTEM_ADMIN"})
    protected static class AsAdmin {
    }

    /**
     * 模拟教师用户
     */
    @WithMockUser(roles = {"TEACHER", "ACADEMIC_ADMIN"})
    protected static class AsTeacher {
    }

    /**
     * 模拟学生用户
     */
    @WithMockUser(roles = {"STUDENT"})
    protected static class AsStudent {
    }
}
