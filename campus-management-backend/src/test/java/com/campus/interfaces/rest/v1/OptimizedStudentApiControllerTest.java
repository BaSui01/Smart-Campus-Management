package com.campus.interfaces.rest.v1;

import com.campus.application.service.StudentService;
import com.campus.application.service.SchoolClassService;
import com.campus.domain.entity.Student;
import com.campus.domain.entity.SchoolClass;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * OptimizedStudentApiController单元测试
 */
@WebMvcTest(OptimizedStudentApiController.class)
@SpringJUnitConfig
class OptimizedStudentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private SchoolClassService schoolClassService;

    @Autowired
    private ObjectMapper objectMapper;

    private Student testStudent;
    private SchoolClass testClass;

    @BeforeEach
    void setUp() {
        testClass = new SchoolClass();
        testClass.setId(1L);
        testClass.setClassName("计算机科学与技术2024-1班");

        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setName("张三");
        testStudent.setStudentNo("2024001");
        testStudent.setClassId(1L);
        testStudent.setGender("男");
        testStudent.setGrade("2024级");
        testStudent.setMajor("计算机科学与技术");
        testStudent.setStatus(1);
        testStudent.setCreatedAt(LocalDateTime.now());
        testStudent.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 测试获取学生统计信息
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetStudentStats() throws Exception {
        when(studentService.count()).thenReturn(650L);

        mockMvc.perform(get("/api/v1/students/stats")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalStudents").value(650))
                .andExpect(jsonPath("$.data.activeStudents").value(640))
                .andExpect(jsonPath("$.data.gradeStats").exists())
                .andExpect(jsonPath("$.data.genderStats").exists())
                .andExpect(jsonPath("$.data.majorStats").exists())
                .andExpect(jsonPath("$.data.classStats").exists());

        verify(studentService).count();
    }

    /**
     * 测试批量删除学生
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchDeleteStudents() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        
        when(studentService.findById(anyLong())).thenReturn(Optional.of(testStudent));
        doNothing().when(studentService).deleteById(anyLong());

        mockMvc.perform(delete("/api/v1/students/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(3))
                .andExpect(jsonPath("$.data.failCount").value(0));

        verify(studentService, times(3)).findById(anyLong());
        verify(studentService, times(3)).deleteById(anyLong());
    }

    /**
     * 测试批量更新学生状态
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchUpdateStudentStatus() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        Map<String, Object> batchData = new HashMap<>();
        batchData.put("ids", ids);
        batchData.put("status", 1);
        
        when(studentService.findById(anyLong())).thenReturn(Optional.of(testStudent));
        when(studentService.save(any(Student.class))).thenReturn(testStudent);

        mockMvc.perform(put("/api/v1/students/batch/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(3))
                .andExpect(jsonPath("$.data.status").value("启用"));

        verify(studentService, times(3)).findById(anyLong());
        verify(studentService, times(3)).save(any(Student.class));
    }

    /**
     * 测试批量操作 - 空列表
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchOperationWithEmptyList() throws Exception {
        List<Long> ids = Arrays.asList();

        mockMvc.perform(delete("/api/v1/students/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("学生ID列表不能为空"));

        verify(studentService, never()).deleteById(anyLong());
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

        mockMvc.perform(delete("/api/v1/students/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("单次批量操作不能超过100条记录"));

        verify(studentService, never()).deleteById(anyLong());
    }

    /**
     * 测试权限控制 - 学生无法进行批量删除
     */
    @Test
    @WithMockUser(roles = {"STUDENT"})
    void testPermissionControlForBatchDelete() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        mockMvc.perform(delete("/api/v1/students/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isForbidden());

        verify(studentService, never()).deleteById(anyLong());
    }

    /**
     * 测试权限控制 - 教师可以访问统计信息
     */
    @Test
    @WithMockUser(roles = {"TEACHER"})
    void testPermissionControlForStats() throws Exception {
        when(studentService.count()).thenReturn(650L);

        mockMvc.perform(get("/api/v1/students/stats")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(studentService).count();
    }

    /**
     * 测试异常处理
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testExceptionHandling() throws Exception {
        when(studentService.count()).thenThrow(new RuntimeException("数据库连接失败"));

        mockMvc.perform(get("/api/v1/students/stats")
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("获取学生统计信息失败: 数据库连接失败"));
    }

    /**
     * 测试参数验证 - 无效ID
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testParameterValidation() throws Exception {
        List<Long> ids = Arrays.asList(0L, -1L);

        mockMvc.perform(delete("/api/v1/students/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(studentService, never()).deleteById(anyLong());
    }

    /**
     * 测试创建学生
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateStudent() throws Exception {
        when(schoolClassService.findById(1L)).thenReturn(Optional.of(testClass));
        when(studentService.save(any(Student.class))).thenReturn(testStudent);

        mockMvc.perform(post("/api/v1/students")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(studentService).save(any(Student.class));
    }

    /**
     * 测试查询学生列表
     */
    @Test
    @WithMockUser(roles = {"TEACHER"})
    void testGetStudents() throws Exception {
        Page<Student> studentPage = new PageImpl<>(Arrays.asList(testStudent));
        when(studentService.findStudentsByPage(any(Pageable.class), any(Map.class)))
            .thenReturn(studentPage);

        mockMvc.perform(get("/api/v1/students")
                        .param("page", "1")
                        .param("size", "20")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());

        verify(studentService).findStudentsByPage(any(Pageable.class), any(Map.class));
    }

    /**
     * 测试获取学生详情
     */
    @Test
    @WithMockUser(roles = {"TEACHER"})
    void testGetStudentById() throws Exception {
        when(studentService.findById(1L)).thenReturn(Optional.of(testStudent));

        mockMvc.perform(get("/api/v1/students/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(studentService).findById(1L);
    }

    /**
     * 测试搜索学生
     */
    @Test
    @WithMockUser(roles = {"TEACHER"})
    void testSearchStudents() throws Exception {
        Page<Student> studentPage = new PageImpl<>(Arrays.asList(testStudent));
        when(studentService.findAll(any(Pageable.class))).thenReturn(studentPage);

        mockMvc.perform(get("/api/v1/students/search")
                        .param("keyword", "张三")
                        .param("page", "1")
                        .param("size", "20")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());

        verify(studentService).findAll(any(Pageable.class));
    }

    /**
     * 测试状态更新参数验证
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchUpdateStatusWithInvalidStatus() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        Map<String, Object> batchData = new HashMap<>();
        batchData.put("ids", ids);
        batchData.put("status", 2); // 无效状态值

        mockMvc.perform(put("/api/v1/students/batch/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("状态值必须为0（禁用）或1（启用）"));

        verify(studentService, never()).save(any(Student.class));
    }
}
