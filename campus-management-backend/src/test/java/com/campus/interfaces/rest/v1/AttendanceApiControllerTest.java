package com.campus.interfaces.rest.v1;

import com.campus.application.service.AttendanceService;
import com.campus.domain.entity.Attendance;
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

import java.time.LocalDate;
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
 * AttendanceApiController单元测试
 */
@WebMvcTest(AttendanceApiController.class)
@SpringJUnitConfig
class AttendanceApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AttendanceService attendanceService;

    @Autowired
    private ObjectMapper objectMapper;

    private Attendance testAttendance;

    @BeforeEach
    void setUp() {
        testAttendance = new Attendance();
        testAttendance.setId(1L);
        testAttendance.setStudentId(1L);
        testAttendance.setCourseId(1L);
        testAttendance.setAttendanceDate(LocalDate.now());
        testAttendance.setAttendanceStatus("present");
    }

    /**
     * 测试获取考勤统计信息
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetAttendanceStats() throws Exception {
        // Mock service responses
        when(attendanceService.count()).thenReturn(100L);
        when(attendanceService.findByAttendanceStatus("present")).thenReturn(Arrays.asList(testAttendance));
        when(attendanceService.findByAttendanceStatus("absent")).thenReturn(Arrays.asList());
        when(attendanceService.findByAttendanceStatus("late")).thenReturn(Arrays.asList());
        when(attendanceService.findByAttendanceStatus("leave")).thenReturn(Arrays.asList());
        when(attendanceService.findTodayAttendance()).thenReturn(Arrays.asList(testAttendance));
        when(attendanceService.findWeeklyAttendance(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(testAttendance));
        when(attendanceService.findMonthlyAttendance(anyInt(), anyInt()))
                .thenReturn(Arrays.asList(testAttendance));
        when(attendanceService.findPendingLeaveRequests()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/attendance/stats")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalAttendance").value(100))
                .andExpect(jsonPath("$.data.presentCount").value(1))
                .andExpect(jsonPath("$.data.absentCount").value(0));

        verify(attendanceService).count();
        verify(attendanceService).findByAttendanceStatus("present");
        verify(attendanceService).findByAttendanceStatus("absent");
    }

    /**
     * 测试批量删除考勤记录
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchDeleteAttendance() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        
        when(attendanceService.canDeleteAttendance(anyLong())).thenReturn(true);
        doNothing().when(attendanceService).deleteById(anyLong());

        mockMvc.perform(delete("/api/attendance/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(3))
                .andExpect(jsonPath("$.data.failCount").value(0));

        verify(attendanceService, times(3)).canDeleteAttendance(anyLong());
        verify(attendanceService, times(3)).deleteById(anyLong());
    }

    /**
     * 测试批量删除考勤记录 - 空列表
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchDeleteAttendanceWithEmptyList() throws Exception {
        List<Long> ids = Arrays.asList();

        mockMvc.perform(delete("/api/attendance/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("考勤记录ID列表不能为空"));

        verify(attendanceService, never()).deleteById(anyLong());
    }

    /**
     * 测试批量删除考勤记录 - 超过100条记录
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchDeleteAttendanceWithTooManyRecords() throws Exception {
        List<Long> ids = Arrays.asList();
        for (long i = 1; i <= 101; i++) {
            ids.add(i);
        }

        mockMvc.perform(delete("/api/attendance/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("单次批量操作不能超过100条记录"));

        verify(attendanceService, never()).deleteById(anyLong());
    }

    /**
     * 测试批量更新考勤状态
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchUpdateAttendanceStatus() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        Map<String, Object> request = new HashMap<>();
        request.put("ids", ids);
        request.put("status", "present");

        doNothing().when(attendanceService).batchUpdateAttendanceStatus(anyList(), anyString());

        mockMvc.perform(put("/api/attendance/batch/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(3))
                .andExpect(jsonPath("$.data.status").value("present"));

        verify(attendanceService).batchUpdateAttendanceStatus(ids, "present");
    }

    /**
     * 测试权限控制 - 无权限用户
     */
    @Test
    @WithMockUser(roles = {"STUDENT"})
    void testPermissionControlForStats() throws Exception {
        mockMvc.perform(get("/api/attendance/stats")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(attendanceService, never()).count();
    }

    /**
     * 测试权限控制 - 批量删除需要管理员权限
     */
    @Test
    @WithMockUser(roles = {"TEACHER"})
    void testPermissionControlForBatchDelete() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        mockMvc.perform(delete("/api/attendance/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isForbidden());

        verify(attendanceService, never()).deleteById(anyLong());
    }

    /**
     * 测试异常处理
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testExceptionHandling() throws Exception {
        when(attendanceService.count()).thenThrow(new RuntimeException("数据库连接失败"));

        mockMvc.perform(get("/api/attendance/stats")
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("获取考勤统计信息失败: 数据库连接失败"));

        verify(attendanceService).count();
    }
}
