package com.campus.interfaces.rest.v1;

import com.campus.BaseApiTest;
import com.campus.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户API控制器测试类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-27
 */
@DisplayName("用户API接口测试")
class UserApiControllerTest extends BaseApiTest {

    private static final String BASE_URL = "/api/users";

    @Test
    @DisplayName("获取用户列表 - 成功")
    void testGetUsers_Success() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", getAdminAuthHeader())
                        .param("page", "1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(getJsonContentType()))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.total").exists())
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    @DisplayName("获取用户列表 - 带搜索参数")
    void testGetUsers_WithSearch() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", getAdminAuthHeader())
                        .param("page", "1")
                        .param("size", "10")
                        .param("search", "admin")
                        .param("role", "ADMIN"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    @DisplayName("获取用户详情 - 成功")
    void testGetUserById_Success() throws Exception {
        // 假设ID为1的用户存在
        mockMvc.perform(get(BASE_URL + "/1")
                        .header("Authorization", getAdminAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("获取用户详情 - 用户不存在")
    void testGetUserById_NotFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/99999")
                        .header("Authorization", getAdminAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("根据用户名查询用户 - 成功")
    void testGetUserByUsername_Success() throws Exception {
        mockMvc.perform(get(BASE_URL + "/username/admin")
                        .header("Authorization", getAdminAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("搜索用户")
    void testSearchUsers() throws Exception {
        mockMvc.perform(get(BASE_URL + "/search")
                        .header("Authorization", getAdminAuthHeader())
                        .param("keyword", "admin"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("获取用户统计信息")
    void testGetUserStatistics() throws Exception {
        mockMvc.perform(get(BASE_URL + "/stats")
                        .header("Authorization", getAdminAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("创建用户 - 成功")
    void testCreateUser_Success() throws Exception {
        User newUser = createTestUser("testuser", "test@example.com", "STUDENT");
        
        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", getAdminAuthHeader())
                        .contentType(getJsonContentType())
                        .content(asJsonString(newUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("创建用户 - 无权限")
    void testCreateUser_Unauthorized() throws Exception {
        User newUser = createTestUser("testuser", "test@example.com", "STUDENT");
        
        mockMvc.perform(post(BASE_URL)
                        .contentType(getJsonContentType())
                        .content(asJsonString(newUser)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("更新用户信息")
    void testUpdateUser() throws Exception {
        String updateData = "{\"realName\":\"Updated Name\",\"email\":\"updated@example.com\"}";
        
        mockMvc.perform(put(BASE_URL + "/1")
                        .header("Authorization", getAdminAuthHeader())
                        .contentType(getJsonContentType())
                        .content(updateData))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("删除用户")
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/999")
                        .header("Authorization", getAdminAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("批量删除用户")
    void testBatchDeleteUsers() throws Exception {
        String ids = "[999, 998]";
        
        mockMvc.perform(delete(BASE_URL + "/batch")
                        .header("Authorization", getAdminAuthHeader())
                        .contentType(getJsonContentType())
                        .content(ids))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("重置用户密码")
    void testResetPassword() throws Exception {
        mockMvc.perform(post(BASE_URL + "/1/reset-password")
                        .header("Authorization", getAdminAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("修改用户密码")
    void testChangePassword() throws Exception {
        mockMvc.perform(post(BASE_URL + "/1/change-password")
                        .header("Authorization", getAdminAuthHeader())
                        .param("oldPassword", "oldpass")
                        .param("newPassword", "newpass"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("切换用户状态")
    void testToggleUserStatus() throws Exception {
        mockMvc.perform(post(BASE_URL + "/1/toggle-status")
                        .header("Authorization", getAdminAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("获取禁用用户列表")
    void testGetDisabledUsers() throws Exception {
        mockMvc.perform(get(BASE_URL + "/disabled")
                        .header("Authorization", getAdminAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("导出用户数据")
    void testExportUsers() throws Exception {
        mockMvc.perform(get(BASE_URL + "/export")
                        .header("Authorization", getAdminAuthHeader())
                        .param("role", "STUDENT")
                        .param("status", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }
}
