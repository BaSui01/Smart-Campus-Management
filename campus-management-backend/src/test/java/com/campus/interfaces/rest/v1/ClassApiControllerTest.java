package com.campus.interfaces.rest.v1;

import com.campus.application.service.SchoolClassService;
import com.campus.domain.entity.SchoolClass;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ClassApiController单元测试
 */
@WebMvcTest(ClassApiController.class)
@SpringJUnitConfig
class ClassApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SchoolClassService schoolClassService;

    @Autowired
    private ObjectMapper objectMapper;

    private SchoolClass testClass;

    @BeforeEach
    void setUp() {
        testClass = new SchoolClass();
        testClass.setId(1L);
        testClass.setClassName("计算机科学与技术1班");
        testClass.setClassCode("CS2024001");
        testClass.setGrade("2024");
        testClass.setDepartmentId(1L); // 设置院系ID而不是院系名称
        testClass.setStatus(1);
        testClass.setStudentCount(35);
    }

    /**
     * 测试获取班级统计信息
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetClassStats() throws Exception {
        // Mock service responses
        when(schoolClassService.countTotalClasses()).thenReturn(50L);
        when(schoolClassService.countActiveClasses()).thenReturn(45L);
        when(schoolClassService.countClassesByGrade()).thenReturn(createMockGradeStats());
        when(schoolClassService.countClassesByDepartment()).thenReturn(createMockDepartmentStats());
        when(schoolClassService.findAllGrades()).thenReturn(Arrays.asList("2024", "2023", "2022", "2021"));

        mockMvc.perform(get("/api/classes/stats")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalClasses").value(50))
                .andExpect(jsonPath("$.data.activeClasses").value(45))
                .andExpect(jsonPath("$.data.inactiveClasses").value(5));

        verify(schoolClassService).countTotalClasses();
        verify(schoolClassService).countActiveClasses();
        verify(schoolClassService).countClassesByGrade();
    }

    /**
     * 测试批量更新班级状态
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchUpdateClassStatus() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        Map<String, Object> request = new HashMap<>();
        request.put("ids", ids);
        request.put("status", 1);

        when(schoolClassService.enableClass(anyLong())).thenReturn(true);

        mockMvc.perform(put("/api/classes/batch/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(3))
                .andExpect(jsonPath("$.data.failCount").value(0))
                .andExpect(jsonPath("$.data.status").value("启用"));

        verify(schoolClassService, times(3)).enableClass(anyLong());
    }

    /**
     * 测试批量更新班级状态 - 禁用
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchUpdateClassStatusDisable() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        Map<String, Object> request = new HashMap<>();
        request.put("ids", ids);
        request.put("status", 0);

        when(schoolClassService.disableClass(anyLong())).thenReturn(true);

        mockMvc.perform(put("/api/classes/batch/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(3))
                .andExpect(jsonPath("$.data.status").value("禁用"));

        verify(schoolClassService, times(3)).disableClass(anyLong());
    }

    /**
     * 测试批量导入班级
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchImportClasses() throws Exception {
        List<SchoolClass> classes = Arrays.asList(testClass);
        
        when(schoolClassService.existsByClassCode(anyString())).thenReturn(false);
        when(schoolClassService.createClass(any(SchoolClass.class))).thenReturn(testClass);

        mockMvc.perform(post("/api/classes/batch/import")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(classes)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(1))
                .andExpect(jsonPath("$.data.failCount").value(0));

        verify(schoolClassService).existsByClassCode("CS2024001");
        verify(schoolClassService).createClass(any(SchoolClass.class));
    }

    /**
     * 测试批量导入班级 - 班级代码重复
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchImportClassesWithDuplicateCode() throws Exception {
        List<SchoolClass> classes = Arrays.asList(testClass);
        
        when(schoolClassService.existsByClassCode(anyString())).thenReturn(true);

        mockMvc.perform(post("/api/classes/batch/import")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(classes)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(0))
                .andExpect(jsonPath("$.data.failCount").value(1));

        verify(schoolClassService).existsByClassCode("CS2024001");
        verify(schoolClassService, never()).createClass(any(SchoolClass.class));
    }

    /**
     * 测试批量操作 - 无效状态值
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchUpdateClassStatusWithInvalidStatus() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        Map<String, Object> request = new HashMap<>();
        request.put("ids", ids);
        request.put("status", 2); // 无效状态值

        mockMvc.perform(put("/api/classes/batch/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("状态值必须为0（禁用）或1（启用）"));

        verify(schoolClassService, never()).enableClass(anyLong());
        verify(schoolClassService, never()).disableClass(anyLong());
    }

    /**
     * 测试批量操作 - 空列表
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchOperationWithEmptyList() throws Exception {
        List<Long> ids = Arrays.asList();
        Map<String, Object> request = new HashMap<>();
        request.put("ids", ids);
        request.put("status", 1);

        mockMvc.perform(put("/api/classes/batch/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("班级ID列表不能为空"));

        verify(schoolClassService, never()).enableClass(anyLong());
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
        Map<String, Object> request = new HashMap<>();
        request.put("ids", ids);
        request.put("status", 1);

        mockMvc.perform(put("/api/classes/batch/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("单次批量操作不能超过100条记录"));

        verify(schoolClassService, never()).enableClass(anyLong());
    }

    /**
     * 测试权限控制 - 学生无法访问统计信息
     */
    @Test
    @WithMockUser(roles = {"STUDENT"})
    void testPermissionControlForStats() throws Exception {
        mockMvc.perform(get("/api/classes/stats")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(schoolClassService, never()).countTotalClasses();
    }

    /**
     * 测试权限控制 - 教师无法进行批量操作
     */
    @Test
    @WithMockUser(roles = {"TEACHER"})
    void testPermissionControlForBatchOperation() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        Map<String, Object> request = new HashMap<>();
        request.put("ids", ids);
        request.put("status", 1);

        mockMvc.perform(put("/api/classes/batch/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(schoolClassService, never()).enableClass(anyLong());
    }

    /**
     * 测试异常处理
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testExceptionHandling() throws Exception {
        when(schoolClassService.countTotalClasses()).thenThrow(new RuntimeException("数据库连接失败"));

        mockMvc.perform(get("/api/classes/stats")
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("获取班级统计信息失败: 数据库连接失败"));

        verify(schoolClassService).countTotalClasses();
    }

    // 辅助方法
    private List<Object[]> createMockGradeStats() {
        return Arrays.asList(
            new Object[]{"2024", 15L},
            new Object[]{"2023", 12L},
            new Object[]{"2022", 13L},
            new Object[]{"2021", 10L}
        );
    }

    private Map<String, Long> createMockDepartmentStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("计算机学院", 20L);
        stats.put("电子工程学院", 15L);
        stats.put("机械工程学院", 15L);
        return stats;
    }
}
