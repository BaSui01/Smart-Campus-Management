package com.campus.interfaces.rest.v1;

import com.campus.BaseApiTest;
import com.campus.shared.common.ApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.campus.application.service.UserService;
import com.campus.application.service.StudentService;
import com.campus.application.service.DepartmentService;
import com.campus.application.service.CourseService;
import com.campus.application.service.SchoolClassService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * 核心API控制器集成测试
 * 使用MockMvc进行API集成测试，验证HTTP状态码、响应数据结构等
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-10
 */
@DisplayName("核心API控制器集成测试")
@WebMvcTest(controllers = {
    UserApiController.class,
    StudentApiController.class,
    DepartmentApiController.class,
    CourseApiController.class,
    ClassApiController.class
})
public class CoreApiIntegrationTest extends BaseApiTest {

    @MockBean
    private UserService userService;
    
    @MockBean
    private StudentService studentService;
    
    @MockBean
    private DepartmentService departmentService;
    
    @MockBean
    private CourseService courseService;
    
    @MockBean
    private SchoolClassService schoolClassService;

    /**
     * 测试用户API的HTTP状态码和响应格式
     */
    @Test
    @DisplayName("测试用户API - HTTP状态码和响应格式")
    public void testUserApiHttpStatusAndResponse() throws Exception {
        // 测试GET /api/v1/users - 获取用户列表
        mockMvc.perform(get("/api/v1/users")
                .header("Authorization", getAdminAuthHeader())
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").exists());

        // 测试GET /api/v1/users/{id} - 获取用户详情
        mockMvc.perform(get("/api/v1/users/1")
                .header("Authorization", getAdminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        // 测试GET /api/v1/users/stats - 获取用户统计
        mockMvc.perform(get("/api/v1/users/stats")
                .header("Authorization", getAdminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * 测试学生API的HTTP状态码和响应格式
     */
    @Test
    @DisplayName("测试学生API - HTTP状态码和响应格式")
    public void testStudentApiHttpStatusAndResponse() throws Exception {
        // 测试GET /api/v1/students - 获取学生列表
        mockMvc.perform(get("/api/v1/students")
                .header("Authorization", getAdminAuthHeader())
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").exists());

        // 测试GET /api/v1/students/{id} - 获取学生详情
        mockMvc.perform(get("/api/v1/students/1")
                .header("Authorization", getAdminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        // 测试GET /api/v1/students/stats - 获取学生统计
        mockMvc.perform(get("/api/v1/students/stats")
                .header("Authorization", getAdminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 测试院系API的HTTP状态码和响应格式
     */
    @Test
    @DisplayName("测试院系API - HTTP状态码和响应格式")
    public void testDepartmentApiHttpStatusAndResponse() throws Exception {
        // 测试GET /api/v1/departments - 获取院系列表
        mockMvc.perform(get("/api/v1/departments")
                .header("Authorization", getAdminAuthHeader())
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.data").exists());

        // 测试GET /api/v1/departments/tree - 获取院系树结构
        mockMvc.perform(get("/api/v1/departments/tree")
                .header("Authorization", getAdminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * 测试分页数据格式的正确性
     */
    @Test
    @DisplayName("测试分页数据格式")
    public void testPaginationDataFormat() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/users")
                .header("Authorization", getAdminAuthHeader())
                .param("page", "1")
                .param("size", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        ApiResponse<Map<String, Object>> response = objectMapper.readValue(
            responseContent, 
            new TypeReference<ApiResponse<Map<String, Object>>>() {}
        );

        // 验证分页数据结构
        Map<String, Object> data = response.getData();
        if (data != null) {
            // 验证分页字段存在
            assertTrue(data.containsKey("total"), "应包含total字段");
            assertTrue(data.containsKey("pages"), "应包含pages字段");
            assertTrue(data.containsKey("current"), "应包含current字段");
            assertTrue(data.containsKey("size"), "应包含size字段");
            assertTrue(data.containsKey("records"), "应包含records字段");

            // 验证分页字段类型
            Object total = data.get("total");
            Object pages = data.get("pages");
            Object current = data.get("current");
            Object size = data.get("size");
            Object records = data.get("records");

            assertTrue(total instanceof Number, "total应为数字类型");
            assertTrue(pages instanceof Number, "pages应为数字类型");
            assertTrue(current instanceof Number, "current应为数字类型");
            assertTrue(size instanceof Number, "size应为数字类型");
            assertTrue(records instanceof List, "records应为列表类型");

            // 验证分页逻辑
            int totalNum = ((Number) total).intValue();
            int pagesNum = ((Number) pages).intValue();
            int currentNum = ((Number) current).intValue();
            int sizeNum = ((Number) size).intValue();

            assertTrue(totalNum >= 0, "总数应大于等于0");
            assertTrue(pagesNum >= 0, "总页数应大于等于0");
            assertTrue(currentNum >= 1, "当前页应大于等于1");
            assertTrue(sizeNum > 0, "页面大小应大于0");
            assertEquals(5, sizeNum, "页面大小应与请求参数一致");
        }
    }

    /**
     * 测试请求参数验证
     */
    @Test
    @DisplayName("测试请求参数验证")
    public void testRequestParameterValidation() throws Exception {
        // 测试无效的页码参数
        mockMvc.perform(get("/api/v1/users")
                .header("Authorization", getAdminAuthHeader())
                .param("page", "-1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());

        // 测试无效的页面大小参数
        mockMvc.perform(get("/api/v1/users")
                .header("Authorization", getAdminAuthHeader())
                .param("page", "1")
                .param("size", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());

        // 测试过大的页面大小参数
        mockMvc.perform(get("/api/v1/users")
                .header("Authorization", getAdminAuthHeader())
                .param("page", "1")
                .param("size", "1000")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * 测试Content-Type验证
     */
    @Test
    @DisplayName("测试Content-Type验证")
    public void testContentTypeValidation() throws Exception {
        // 测试正确的Content-Type
        mockMvc.perform(get("/api/v1/users")
                .header("Authorization", getAdminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // 测试POST请求的Content-Type
        String testUserJson = """
            {
                "username": "testuser",
                "realName": "测试用户",
                "email": "test@example.com"
            }
            """;

        mockMvc.perform(post("/api/v1/users")
                .header("Authorization", getAdminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(testUserJson))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists());
    }

    /**
     * 测试各种HTTP方法的响应
     */
    @Test
    @DisplayName("测试HTTP方法响应")
    public void testHttpMethodsResponse() throws Exception {
        // 测试GET方法
        mockMvc.perform(get("/api/v1/users/stats")
                .header("Authorization", getAdminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        // 测试POST方法
        String createJson = """
            {
                "username": "newuser",
                "realName": "新用户",
                "email": "newuser@example.com"
            }
            """;

        mockMvc.perform(post("/api/v1/users")
                .header("Authorization", getAdminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        // 测试PUT方法
        String updateJson = """
            {
                "realName": "更新的用户名"
            }
            """;

        mockMvc.perform(put("/api/v1/users/1")
                .header("Authorization", getAdminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        // 测试DELETE方法
        mockMvc.perform(delete("/api/v1/users/999")
                .header("Authorization", getAdminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * 测试错误响应格式的一致性
     */
    @Test
    @DisplayName("测试错误响应格式一致性")
    public void testErrorResponseConsistency() throws Exception {
        // 测试404错误
        mockMvc.perform(get("/api/v1/users/999999")
                .header("Authorization", getAdminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        // 测试400错误（参数错误）
        String invalidJson = """
            {
                "username": "",
                "email": "invalid-email"
            }
            """;

        mockMvc.perform(post("/api/v1/users")
                .header("Authorization", getAdminAuthHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
