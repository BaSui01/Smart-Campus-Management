package com.campus.interfaces.rest.v1;

import com.campus.application.service.NotificationService;
import com.campus.domain.entity.NotificationTemplate;
import com.campus.shared.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * NotificationTemplateApiController单元测试
 */
@WebMvcTest(NotificationTemplateApiController.class)
@SpringJUnitConfig
class NotificationTemplateApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    private NotificationTemplate testTemplate;

    @BeforeEach
    void setUp() {
        testTemplate = new NotificationTemplate();
        testTemplate.setId(1L);
        testTemplate.setTemplateName("测试模板");
        testTemplate.setTemplateType("system");
        testTemplate.setTemplateContent("这是一个测试模板内容");
        testTemplate.setNotificationChannel("email");
        testTemplate.setTemplateStatus("active");
        testTemplate.setCreateTime(LocalDateTime.now());
        testTemplate.setUpdateTime(LocalDateTime.now());
    }

    /**
     * 测试获取通知模板统计信息
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetTemplateStatistics() throws Exception {
        when(notificationService.countTotalTemplates()).thenReturn(50L);
        when(notificationService.countActiveTemplates()).thenReturn(45L);
        when(notificationService.countInactiveTemplates()).thenReturn(5L);
        when(notificationService.getTemplateTypeStatistics()).thenReturn(new HashMap<>());
        when(notificationService.getChannelStatistics()).thenReturn(new HashMap<>());
        when(notificationService.getTemplateUsageStatistics()).thenReturn(new HashMap<>());

        mockMvc.perform(get("/api/v1/notification-templates/stats")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalTemplates").value(50))
                .andExpect(jsonPath("$.data.activeTemplates").value(45))
                .andExpect(jsonPath("$.data.inactiveTemplates").value(5));

        verify(notificationService).countTotalTemplates();
        verify(notificationService).countActiveTemplates();
        verify(notificationService).countInactiveTemplates();
    }

    /**
     * 测试批量删除通知模板
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchDeleteTemplates() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        
        doNothing().when(notificationService).deleteTemplate(anyLong());

        mockMvc.perform(delete("/api/v1/notification-templates/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(3))
                .andExpect(jsonPath("$.data.failCount").value(0));

        verify(notificationService, times(3)).deleteTemplate(anyLong());
    }

    /**
     * 测试批量更新模板状态
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchUpdateTemplateStatus() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        
        when(notificationService.batchUpdateTemplateStatus(eq(ids), eq("active"))).thenReturn(3);

        mockMvc.perform(put("/api/v1/notification-templates/batch-status")
                        .param("templateIds", "1", "2", "3")
                        .param("newStatus", "active")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.updatedCount").value(3))
                .andExpect(jsonPath("$.data.newStatus").value("active"));

        verify(notificationService).batchUpdateTemplateStatus(eq(ids), eq("active"));
    }

    /**
     * 测试批量操作 - 空列表
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchOperationWithEmptyList() throws Exception {
        List<Long> ids = Arrays.asList();

        mockMvc.perform(delete("/api/v1/notification-templates/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("模板ID列表不能为空"));

        verify(notificationService, never()).deleteTemplate(anyLong());
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

        mockMvc.perform(delete("/api/v1/notification-templates/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("单次批量操作不能超过100条记录"));

        verify(notificationService, never()).deleteTemplate(anyLong());
    }

    /**
     * 测试权限控制 - 学生无法进行批量删除
     */
    @Test
    @WithMockUser(roles = {"STUDENT"})
    void testPermissionControlForBatchDelete() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        mockMvc.perform(delete("/api/v1/notification-templates/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isForbidden());

        verify(notificationService, never()).deleteTemplate(anyLong());
    }

    /**
     * 测试权限控制 - 教师可以访问统计信息
     */
    @Test
    @WithMockUser(roles = {"TEACHER"})
    void testPermissionControlForStats() throws Exception {
        when(notificationService.countTotalTemplates()).thenReturn(50L);
        when(notificationService.countActiveTemplates()).thenReturn(45L);
        when(notificationService.countInactiveTemplates()).thenReturn(5L);
        when(notificationService.getTemplateTypeStatistics()).thenReturn(new HashMap<>());
        when(notificationService.getChannelStatistics()).thenReturn(new HashMap<>());
        when(notificationService.getTemplateUsageStatistics()).thenReturn(new HashMap<>());

        mockMvc.perform(get("/api/v1/notification-templates/stats")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    /**
     * 测试异常处理
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testExceptionHandling() throws Exception {
        when(notificationService.countTotalTemplates()).thenThrow(new RuntimeException("数据库连接失败"));

        mockMvc.perform(get("/api/v1/notification-templates/stats")
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("获取统计信息失败: 数据库连接失败"));
    }

    /**
     * 测试参数验证 - 无效ID
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testParameterValidation() throws Exception {
        List<Long> ids = Arrays.asList(0L, -1L);

        mockMvc.perform(delete("/api/v1/notification-templates/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(notificationService, never()).deleteTemplate(anyLong());
    }

    /**
     * 测试创建通知模板
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateNotificationTemplate() throws Exception {
        when(notificationService.existsByTemplateName(anyString())).thenReturn(false);
        when(notificationService.saveTemplate(any(NotificationTemplate.class))).thenReturn(testTemplate);

        mockMvc.perform(post("/api/v1/notification-templates")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTemplate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(notificationService).saveTemplate(any(NotificationTemplate.class));
    }

    /**
     * 测试查询模板列表
     */
    @Test
    @WithMockUser(roles = {"TEACHER"})
    void testGetNotificationTemplates() throws Exception {
        Page<NotificationTemplate> templatePage = new PageImpl<>(Arrays.asList(testTemplate));
        when(notificationService.findTemplates(any(Pageable.class), any(Map.class)))
            .thenReturn(templatePage);

        mockMvc.perform(get("/api/v1/notification-templates")
                        .param("page", "1")
                        .param("size", "20")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());

        verify(notificationService).findTemplates(any(Pageable.class), any(Map.class));
    }

    /**
     * 测试获取模板详情
     */
    @Test
    @WithMockUser(roles = {"TEACHER"})
    void testGetNotificationTemplateById() throws Exception {
        when(notificationService.getTemplateById(1L)).thenReturn(testTemplate);

        mockMvc.perform(get("/api/v1/notification-templates/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(notificationService).getTemplateById(1L);
    }

    /**
     * 测试复制模板
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCopyNotificationTemplate() throws Exception {
        when(notificationService.existsByTemplateName("复制的模板")).thenReturn(false);
        when(notificationService.copyTemplate(1L, "复制的模板")).thenReturn(testTemplate);

        mockMvc.perform(post("/api/v1/notification-templates/1/copy")
                        .param("newTemplateName", "复制的模板")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(notificationService).copyTemplate(1L, "复制的模板");
    }

    /**
     * 测试预览模板
     */
    @Test
    @WithMockUser(roles = {"TEACHER"})
    void testPreviewNotificationTemplate() throws Exception {
        Map<String, Object> previewParams = new HashMap<>();
        previewParams.put("userName", "张三");
        
        Map<String, Object> previewResult = new HashMap<>();
        previewResult.put("content", "预览内容");
        
        when(notificationService.previewTemplate(1L, previewParams)).thenReturn(previewResult);

        mockMvc.perform(post("/api/v1/notification-templates/1/preview")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(previewParams)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value("预览内容"));

        verify(notificationService).previewTemplate(1L, previewParams);
    }
}
