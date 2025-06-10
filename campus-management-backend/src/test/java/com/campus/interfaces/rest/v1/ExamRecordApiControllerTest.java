package com.campus.interfaces.rest.v1;

import com.campus.application.service.ExamService;
import com.campus.domain.entity.ExamRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ExamRecordApiController 单元测试
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-08
 */
@ExtendWith(MockitoExtension.class)
@WebMvcTest(ExamRecordApiController.class)
@SpringJUnitConfig
class ExamRecordApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExamService examService;

    @Autowired
    private ObjectMapper objectMapper;

    private ExamRecord testRecord;

    @BeforeEach
    void setUp() {
        testRecord = new ExamRecord();
        testRecord.setId(1L);
        testRecord.setExamId(1L);
        testRecord.setStudentId(1L);
        testRecord.setExamStatus("completed");
        testRecord.setStartTime(LocalDateTime.now().minusHours(2));
        testRecord.setSubmitTime(LocalDateTime.now());
        testRecord.setScore(new BigDecimal("85.0"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetExamRecords() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/v1/exam-records")
                        .param("page", "1")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询考试记录列表成功"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetExamRecordById() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/v1/exam-records/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询考试记录详情成功"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetRecordsByExamId() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/v1/exam-records/exam/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询考试记录列表成功"));
    }

    @Test
    @WithMockUser(roles = {"STUDENT"})
    void testGetRecordsByStudentId() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/v1/exam-records/student/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("查询学生考试记录列表成功"));
    }

    @Test
    @WithMockUser(roles = {"STUDENT"})
    void testCreateExamRecord() throws Exception {
        ExamRecord newRecord = new ExamRecord();
        newRecord.setExamId(1L);
        newRecord.setStudentId(1L);

        // 执行测试
        mockMvc.perform(post("/api/v1/exam-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRecord)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("考试记录创建成功"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdateExamRecord() throws Exception {
        ExamRecord updateRecord = new ExamRecord();
        updateRecord.setScore(new BigDecimal("90.0"));

        // 执行测试
        mockMvc.perform(put("/api/v1/exam-records/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRecord)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("考试记录更新成功"));
    }

    @Test
    @WithMockUser(roles = {"STUDENT"})
    void testSubmitExam() throws Exception {
        String examData = "{\"answers\":{\"1\":\"A\",\"2\":\"B\"}}";

        // 执行测试
        mockMvc.perform(put("/api/v1/exam-records/1/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(examData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("考试提交成功"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteExamRecord() throws Exception {
        // 执行测试
        mockMvc.perform(delete("/api/v1/exam-records/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("考试记录删除成功"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetExamStatistics() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/v1/exam-records/stats")
                        .param("examId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("获取统计信息成功"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testExportExamRecords() throws Exception {
        // 执行测试
        mockMvc.perform(get("/api/v1/exam-records/export")
                        .param("examId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("导出考试记录请求已记录"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void testUnauthorizedAccess() throws Exception {
        // 测试未授权访问
        mockMvc.perform(get("/api/v1/exam-records")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
