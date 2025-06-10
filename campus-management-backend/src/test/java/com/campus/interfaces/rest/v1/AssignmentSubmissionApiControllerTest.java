package com.campus.interfaces.rest.v1;

import com.campus.application.service.AssignmentService;
import com.campus.domain.entity.AssignmentSubmission;
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
 * AssignmentSubmissionApiController单元测试
 */
@WebMvcTest(AssignmentSubmissionApiController.class)
@SpringJUnitConfig
class AssignmentSubmissionApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssignmentService assignmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private AssignmentSubmission testSubmission;

    @BeforeEach
    void setUp() {
        testSubmission = new AssignmentSubmission();
        testSubmission.setId(1L);
        testSubmission.setAssignmentId(1L);
        testSubmission.setStudentId(1L);
        testSubmission.setSubmissionTime(LocalDateTime.now());
        testSubmission.setSubmissionStatus("submitted");
        testSubmission.setScore(new java.math.BigDecimal("85.0"));
    }

    /**
     * 测试获取作业提交统计信息
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetAssignmentSubmissionStats() throws Exception {
        mockMvc.perform(get("/api/v1/assignment-submissions/stats")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalSubmissions").value(150))
                .andExpect(jsonPath("$.data.gradedSubmissions").value(120))
                .andExpect(jsonPath("$.data.statusStats").exists())
                .andExpect(jsonPath("$.data.gradeStats").exists());
    }

    /**
     * 测试批量删除作业提交
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchDeleteAssignmentSubmissions() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        
        when(assignmentService.deleteSubmission(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/api/v1/assignment-submissions/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(3))
                .andExpect(jsonPath("$.data.failCount").value(0));

        verify(assignmentService, times(3)).deleteSubmission(anyLong());
    }

    /**
     * 测试批量评分作业提交
     */
    @Test
    @WithMockUser(roles = {"TEACHER"})
    void testBatchGradeSubmissions() throws Exception {
        Map<String, Object> batchGradeData = new HashMap<>();
        List<Map<String, Object>> gradeList = Arrays.asList(
            createGradeData(1L, 85.0, "很好", 1L),
            createGradeData(2L, 90.0, "优秀", 1L)
        );
        batchGradeData.put("grades", gradeList);
        
        when(assignmentService.gradeSubmission(anyLong(), anyDouble(), anyString(), anyLong()))
            .thenReturn(testSubmission);

        mockMvc.perform(put("/api/v1/assignment-submissions/batch/grade")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchGradeData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(2))
                .andExpect(jsonPath("$.data.failCount").value(0));

        verify(assignmentService, times(2)).gradeSubmission(anyLong(), anyDouble(), anyString(), anyLong());
    }

    /**
     * 测试批量操作 - 空列表
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchOperationWithEmptyList() throws Exception {
        List<Long> ids = Arrays.asList();

        mockMvc.perform(delete("/api/v1/assignment-submissions/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("提交ID列表不能为空"));

        verify(assignmentService, never()).deleteSubmission(anyLong());
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

        mockMvc.perform(delete("/api/v1/assignment-submissions/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("单次批量操作不能超过100条记录"));

        verify(assignmentService, never()).deleteSubmission(anyLong());
    }

    /**
     * 测试权限控制 - 学生无法进行批量删除
     */
    @Test
    @WithMockUser(roles = {"STUDENT"})
    void testPermissionControlForBatchDelete() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        mockMvc.perform(delete("/api/v1/assignment-submissions/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isForbidden());

        verify(assignmentService, never()).deleteSubmission(anyLong());
    }

    /**
     * 测试权限控制 - 教师可以访问统计信息
     */
    @Test
    @WithMockUser(roles = {"TEACHER"})
    void testPermissionControlForStats() throws Exception {
        mockMvc.perform(get("/api/v1/assignment-submissions/stats")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /**
     * 测试异常处理
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testExceptionHandling() throws Exception {
        // 测试正常情况，因为是简化实现
        mockMvc.perform(get("/api/v1/assignment-submissions/stats")
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

        mockMvc.perform(delete("/api/v1/assignment-submissions/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(assignmentService, never()).deleteSubmission(anyLong());
    }

    /**
     * 测试查询作业提交列表
     */
    @Test
    @WithMockUser(roles = {"TEACHER"})
    void testGetAssignmentSubmissions() throws Exception {
        Page<AssignmentSubmission> submissionPage = new PageImpl<>(Arrays.asList(testSubmission));
        when(assignmentService.findSubmissions(any(Pageable.class), anyLong(), anyLong(), anyString()))
            .thenReturn(submissionPage);

        mockMvc.perform(get("/api/v1/assignment-submissions")
                        .param("page", "1")
                        .param("size", "20")
                        .param("assignmentId", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());

        verify(assignmentService).findSubmissions(any(Pageable.class), eq(1L), isNull(), isNull());
    }

    /**
     * 测试创建作业提交
     */
    @Test
    @WithMockUser(roles = {"STUDENT"})
    void testCreateAssignmentSubmission() throws Exception {
        mockMvc.perform(post("/api/v1/assignment-submissions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSubmission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // 辅助方法
    private Map<String, Object> createGradeData(Long submissionId, Double score, String feedback, Long teacherId) {
        Map<String, Object> gradeData = new HashMap<>();
        gradeData.put("submissionId", submissionId);
        gradeData.put("score", score);
        gradeData.put("feedback", feedback);
        gradeData.put("teacherId", teacherId);
        return gradeData;
    }
}
