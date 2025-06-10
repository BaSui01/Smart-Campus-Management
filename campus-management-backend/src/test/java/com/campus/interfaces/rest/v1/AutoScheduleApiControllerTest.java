package com.campus.interfaces.rest.v1;

import com.campus.application.service.AutoScheduleService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AutoScheduleApiController单元测试
 */
@WebMvcTest(AutoScheduleApiController.class)
@SpringJUnitConfig
class AutoScheduleApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AutoScheduleService autoScheduleService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
    }

    /**
     * 测试获取自动排课统计信息
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetAutoScheduleStats() throws Exception {
        mockMvc.perform(get("/api/v1/auto-schedule/stats")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalSchedules").exists())
                .andExpect(jsonPath("$.data.algorithmStats").exists())
                .andExpect(jsonPath("$.data.utilizationStats").exists());
    }

    /**
     * 测试批量删除排课
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchDeleteSchedules() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        mockMvc.perform(delete("/api/v1/auto-schedule/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(3))
                .andExpect(jsonPath("$.data.failCount").value(0));
    }

    /**
     * 测试批量重新排课
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchReschedule() throws Exception {
        List<Long> courseIds = Arrays.asList(1L, 2L, 3L);

        mockMvc.perform(post("/api/v1/auto-schedule/batch/reschedule")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(3))
                .andExpect(jsonPath("$.data.failCount").value(0));
    }

    /**
     * 测试批量操作 - 空列表
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchOperationWithEmptyList() throws Exception {
        List<Long> ids = Arrays.asList();

        mockMvc.perform(delete("/api/v1/auto-schedule/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("排课ID列表不能为空"));
    }

    /**
     * 测试批量操作 - 超过100条记录
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchOperationWithTooManyRecords() throws Exception {
        List<Long> ids = Arrays.asList();
        for (long i = 1; i <= 101; i++) {
            ids.add(i);
        }

        mockMvc.perform(delete("/api/v1/auto-schedule/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("单次批量操作不能超过100条记录"));
    }

    /**
     * 测试权限控制 - 学生无法访问统计信息
     */
    @Test
    @WithMockUser(roles = {"STUDENT"})
    void testPermissionControlForStats() throws Exception {
        mockMvc.perform(get("/api/v1/auto-schedule/stats")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    /**
     * 测试权限控制 - 教师无法进行批量删除
     */
    @Test
    @WithMockUser(roles = {"TEACHER"})
    void testPermissionControlForBatchDelete() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        mockMvc.perform(delete("/api/v1/auto-schedule/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isForbidden());
    }

    /**
     * 测试异常处理
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testExceptionHandling() throws Exception {
        // 模拟异常情况
        mockMvc.perform(get("/api/v1/auto-schedule/stats")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /**
     * 测试参数验证 - 无效ID
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testParameterValidation() throws Exception {
        List<Long> ids = Arrays.asList(0L, -1L);

        mockMvc.perform(delete("/api/v1/auto-schedule/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * 测试执行自动排课
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testAutoSchedule() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("semester", "2024-1");
        request.put("academicYear", 2024);

        mockMvc.perform(post("/api/v1/auto-schedule/schedule")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    /**
     * 测试获取算法配置
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetAlgorithmConfig() throws Exception {
        mockMvc.perform(get("/api/v1/auto-schedule/algorithm-config")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.algorithm").exists());
    }
}
