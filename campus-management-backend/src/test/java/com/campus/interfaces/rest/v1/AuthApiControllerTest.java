package com.campus.interfaces.rest.v1;

import com.campus.BaseApiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证API控制器测试类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-27
 */
@DisplayName("认证API接口测试")
class AuthApiControllerTest extends BaseApiTest {

    private static final String BASE_URL = "/api/auth";

    @Test
    @DisplayName("用户登录 - 成功")
    void testLogin_Success() throws Exception {
        String loginData = "{\"username\":\"admin\",\"password\":\"admin123\"}";
        
        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(getJsonContentType())
                        .content(loginData))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    @Test
    @DisplayName("用户登录 - 密码错误")
    void testLogin_WrongPassword() throws Exception {
        String loginData = "{\"username\":\"admin\",\"password\":\"wrongpassword\"}";
        
        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(getJsonContentType())
                        .content(loginData))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("用户注册")
    void testRegister() throws Exception {
        String registerData = "{\"username\":\"newuser\",\"password\":\"password123\",\"email\":\"newuser@example.com\",\"realName\":\"New User\"}";
        
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(getJsonContentType())
                        .content(registerData))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("刷新令牌")
    void testRefreshToken() throws Exception {
        mockMvc.perform(post(BASE_URL + "/refresh")
                        .header("Authorization", getAdminAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("用户登出")
    void testLogout() throws Exception {
        mockMvc.perform(post(BASE_URL + "/logout")
                        .header("Authorization", getAdminAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("获取当前用户信息")
    void testGetCurrentUser() throws Exception {
        mockMvc.perform(get(BASE_URL + "/me")
                        .header("Authorization", getAdminAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    @Test
    @DisplayName("验证令牌")
    void testValidateToken() throws Exception {
        mockMvc.perform(post(BASE_URL + "/validate")
                        .header("Authorization", getAdminAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("忘记密码")
    void testForgotPassword() throws Exception {
        String emailData = "{\"email\":\"admin@example.com\"}";
        
        mockMvc.perform(post(BASE_URL + "/forgot-password")
                        .contentType(getJsonContentType())
                        .content(emailData))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("重置密码")
    void testResetPassword() throws Exception {
        String resetData = "{\"token\":\"reset-token\",\"newPassword\":\"newpassword123\"}";
        
        mockMvc.perform(post(BASE_URL + "/reset-password")
                        .contentType(getJsonContentType())
                        .content(resetData))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("修改密码")
    void testChangePassword() throws Exception {
        String changeData = "{\"oldPassword\":\"oldpass\",\"newPassword\":\"newpass\"}";

        mockMvc.perform(post(BASE_URL + "/change-password")
                        .header("Authorization", getAdminAuthHeader())
                        .contentType(getJsonContentType())
                        .content(changeData))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("获取认证统计信息")
    void testGetAuthStats() throws Exception {
        mockMvc.perform(get(BASE_URL + "/stats")
                        .header("Authorization", getAdminAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalUsers").exists())
                .andExpect(jsonPath("$.data.activeUsers").exists());
    }

    @Test
    @DisplayName("批量锁定用户")
    void testBatchLockUsers() throws Exception {
        String batchData = "[1,2,3]";

        mockMvc.perform(put(BASE_URL + "/batch/lock")
                        .header("Authorization", getAdminAuthHeader())
                        .contentType(getJsonContentType())
                        .content(batchData))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("批量解锁用户")
    void testBatchUnlockUsers() throws Exception {
        String batchData = "[1,2,3]";

        mockMvc.perform(put(BASE_URL + "/batch/unlock")
                        .header("Authorization", getAdminAuthHeader())
                        .contentType(getJsonContentType())
                        .content(batchData))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("批量操作 - 空列表验证")
    void testBatchOperationWithEmptyList() throws Exception {
        String emptyData = "[]";

        mockMvc.perform(put(BASE_URL + "/batch/lock")
                        .header("Authorization", getAdminAuthHeader())
                        .contentType(getJsonContentType())
                        .content(emptyData))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("权限控制测试")
    void testPermissionControl() throws Exception {
        // 测试无权限访问统计信息
        mockMvc.perform(get(BASE_URL + "/stats"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
