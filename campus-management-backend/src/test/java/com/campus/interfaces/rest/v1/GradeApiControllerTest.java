package com.campus.interfaces.rest.v1;

import com.campus.application.service.GradeService;
import com.campus.domain.entity.Grade;
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

import java.math.BigDecimal;
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
 * GradeApiController单元测试
 */
@WebMvcTest(GradeApiController.class)
@SpringJUnitConfig
class GradeApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GradeService gradeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Grade testGrade;

    @BeforeEach
    void setUp() {
        testGrade = new Grade();
        testGrade.setId(1L);
        testGrade.setStudentId(1L);
        testGrade.setCourseId(1L);
        testGrade.setScore(new BigDecimal("85.5"));
        testGrade.setSemester("2024-1");
    }

    /**
     * 测试获取成绩统计信息
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetGradeStats() throws Exception {
        // Mock service responses
        when(gradeService.count()).thenReturn(500L);
        when(gradeService.calculateOverallAverageScore()).thenReturn(82.5);
        when(gradeService.getGradeDistribution()).thenReturn(createMockGradeDistribution());
        when(gradeService.generateComprehensiveStatistics()).thenReturn(createMockComprehensiveStats());
        when(gradeService.findAllSemesters()).thenReturn(Arrays.asList("2024-1", "2023-2"));
        when(gradeService.findBySemester("2024-1")).thenReturn(Arrays.asList(testGrade));
        when(gradeService.findBySemester("2023-2")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/grades/stats")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalGrades").value(500))
                .andExpect(jsonPath("$.data.overallAverageScore").value(82.5));

        verify(gradeService).count();
        verify(gradeService).calculateOverallAverageScore();
        verify(gradeService).getGradeDistribution();
    }

    /**
     * 测试批量删除成绩记录
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchDeleteGrades() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        
        when(gradeService.batchDeleteGrades(anyList())).thenReturn(true);

        mockMvc.perform(delete("/api/grades/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.deletedCount").value(3))
                .andExpect(jsonPath("$.data.success").value(true));

        verify(gradeService).batchDeleteGrades(ids);
    }

    /**
     * 测试批量删除成绩记录 - 删除失败
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchDeleteGradesFailure() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        
        when(gradeService.batchDeleteGrades(anyList())).thenReturn(false);

        mockMvc.perform(delete("/api/grades/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("批量删除成绩记录失败"));

        verify(gradeService).batchDeleteGrades(ids);
    }

    /**
     * 测试批量导入成绩
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchImportGrades() throws Exception {
        List<Grade> grades = Arrays.asList(testGrade);
        Map<String, Object> importResult = new HashMap<>();
        importResult.put("successCount", 1);
        importResult.put("failCount", 0);
        
        when(gradeService.importGrades(anyList())).thenReturn(importResult);

        mockMvc.perform(post("/api/grades/batch/import")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(grades)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(1));

        verify(gradeService).importGrades(grades);
    }

    /**
     * 测试批量更新成绩
     */
    @Test
    @WithMockUser(roles = {"TEACHER"})
    void testBatchUpdateGrades() throws Exception {
        List<Grade> grades = Arrays.asList(testGrade);
        
        when(gradeService.batchUpdateGrades(anyList())).thenReturn(true);

        mockMvc.perform(put("/api/grades/batch/update")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(grades)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.updatedCount").value(1))
                .andExpect(jsonPath("$.data.success").value(true));

        verify(gradeService).batchUpdateGrades(grades);
    }

    /**
     * 测试批量操作 - 空列表
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchOperationWithEmptyList() throws Exception {
        List<Long> ids = Arrays.asList();

        mockMvc.perform(delete("/api/grades/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("成绩ID列表不能为空"));

        verify(gradeService, never()).batchDeleteGrades(anyList());
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

        mockMvc.perform(delete("/api/grades/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("单次批量操作不能超过100条记录"));

        verify(gradeService, never()).batchDeleteGrades(anyList());
    }

    /**
     * 测试权限控制 - 学生无法访问统计信息
     */
    @Test
    @WithMockUser(roles = {"STUDENT"})
    void testPermissionControlForStats() throws Exception {
        mockMvc.perform(get("/api/grades/stats")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(gradeService, never()).count();
    }

    /**
     * 测试权限控制 - 学生无法进行批量删除
     */
    @Test
    @WithMockUser(roles = {"STUDENT"})
    void testPermissionControlForBatchDelete() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        mockMvc.perform(delete("/api/grades/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isForbidden());

        verify(gradeService, never()).batchDeleteGrades(anyList());
    }

    /**
     * 测试异常处理
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testExceptionHandling() throws Exception {
        when(gradeService.count()).thenThrow(new RuntimeException("数据库连接失败"));

        mockMvc.perform(get("/api/grades/stats")
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("获取成绩统计信息失败: 数据库连接失败"));

        verify(gradeService).count();
    }

    // 辅助方法
    private Map<String, Object> createMockGradeDistribution() {
        Map<String, Object> distribution = new HashMap<>();
        distribution.put("excellent", 50);
        distribution.put("good", 100);
        distribution.put("average", 200);
        distribution.put("poor", 50);
        return distribution;
    }

    private Map<String, Object> createMockComprehensiveStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("passRate", 85.5);
        stats.put("excellentRate", 15.2);
        return stats;
    }
}
