package com.campus.interfaces.rest.v1;

import com.campus.BaseApiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 学生API控制器测试类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-27
 */
@DisplayName("学生API接口测试")
class StudentApiControllerTest extends BaseApiTest {

    private static final String BASE_URL = "/api/students";

    @Test
    @DisplayName("获取学生列表 - 成功")
    void testGetStudents_Success() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", getAdminAuthHeader())
                        .param("page", "1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(getJsonContentType()))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("获取学生列表 - 带搜索条件")
    void testGetStudents_WithFilters() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", getAdminAuthHeader())
                        .param("page", "1")
                        .param("size", "10")
                        .param("search", "张三")
                        .param("classId", "1")
                        .param("departmentId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("获取学生详情 - 成功")
    void testGetStudentById_Success() throws Exception {
        mockMvc.perform(get(BASE_URL + "/1")
                        .header("Authorization", getAdminAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    @Test
    @DisplayName("根据学号查询学生")
    void testGetStudentByStudentNumber() throws Exception {
        mockMvc.perform(get(BASE_URL + "/number/2021001")
                        .header("Authorization", getAdminAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists());
    }

    @Test
    @DisplayName("创建学生 - 成功")
    void testCreateStudent_Success() throws Exception {
        String studentData = """
            {
                "studentNumber": "2025001",
                "name": "测试学生",
                "gender": "男",
                "birthDate": "2000-01-01",
                "phone": "13800138000",
                "email": "test@student.com",
                "classId": 1,
                "departmentId": 1,
                "enrollmentYear": 2025
            }
            """;
        
        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", getAdminAuthHeader())
                        .contentType(getJsonContentType())
                        .content(studentData))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("更新学生信息")
    void testUpdateStudent() throws Exception {
        String updateData = """
            {
                "name": "更新后的姓名",
                "phone": "13900139000",
                "email": "updated@student.com"
            }
            """;
        
        mockMvc.perform(put(BASE_URL + "/1")
                        .header("Authorization", getAdminAuthHeader())
                        .contentType(getJsonContentType())
                        .content(updateData))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("删除学生")
    void testDeleteStudent() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/999")
                        .header("Authorization", getAdminAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("批量删除学生")
    void testBatchDeleteStudents() throws Exception {
        String ids = "[998, 999]";
        
        mockMvc.perform(delete(BASE_URL + "/batch")
                        .header("Authorization", getAdminAuthHeader())
                        .contentType(getJsonContentType())
                        .content(ids))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("获取学生统计信息")
    void testGetStudentStatistics() throws Exception {
        mockMvc.perform(get(BASE_URL + "/statistics")
                        .header("Authorization", getAdminAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("按班级获取学生")
    void testGetStudentsByClass() throws Exception {
        mockMvc.perform(get(BASE_URL + "/class/1")
                        .header("Authorization", getAdminAuthHeader())
                        .param("page", "1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("按院系获取学生")
    void testGetStudentsByDepartment() throws Exception {
        mockMvc.perform(get(BASE_URL + "/department/1")
                        .header("Authorization", getAdminAuthHeader())
                        .param("page", "1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("搜索学生")
    void testSearchStudents() throws Exception {
        mockMvc.perform(get(BASE_URL + "/search")
                        .header("Authorization", getAdminAuthHeader())
                        .param("keyword", "张三")
                        .param("page", "1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("导出学生数据")
    void testExportStudents() throws Exception {
        mockMvc.perform(get(BASE_URL + "/export")
                        .header("Authorization", getAdminAuthHeader())
                        .param("classId", "1")
                        .param("departmentId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("批量导入学生")
    void testImportStudents() throws Exception {
        String importData = """
            [
                {
                    "studentNumber": "2025002",
                    "name": "导入学生1",
                    "gender": "女",
                    "phone": "13800138001",
                    "email": "import1@student.com",
                    "classId": 1,
                    "departmentId": 1
                },
                {
                    "studentNumber": "2025003",
                    "name": "导入学生2",
                    "gender": "男",
                    "phone": "13800138002",
                    "email": "import2@student.com",
                    "classId": 1,
                    "departmentId": 1
                }
            ]
            """;
        
        mockMvc.perform(post(BASE_URL + "/import")
                        .header("Authorization", getAdminAuthHeader())
                        .contentType(getJsonContentType())
                        .content(importData))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("学生权限测试 - 只能查看自己的信息")
    void testStudentPermission_ViewOwnInfo() throws Exception {
        mockMvc.perform(get(BASE_URL + "/1")
                        .header("Authorization", getStudentAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("学生权限测试 - 无法创建学生")
    void testStudentPermission_CannotCreate() throws Exception {
        String studentData = """
            {
                "studentNumber": "2025004",
                "name": "测试学生",
                "gender": "男"
            }
            """;
        
        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", getStudentAuthHeader())
                        .contentType(getJsonContentType())
                        .content(studentData))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
