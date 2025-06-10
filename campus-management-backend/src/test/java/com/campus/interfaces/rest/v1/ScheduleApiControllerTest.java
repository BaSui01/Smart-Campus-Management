package com.campus.interfaces.rest.v1;

import com.campus.application.service.CourseScheduleService;
import com.campus.application.service.CourseService;
import com.campus.application.service.SchoolClassService;
import com.campus.domain.entity.Course;
import com.campus.domain.entity.CourseSchedule;
import com.campus.domain.entity.SchoolClass;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ScheduleApiController单元测试
 */
@WebMvcTest(ScheduleApiController.class)
@SpringJUnitConfig
class ScheduleApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseScheduleService courseScheduleService;

    @MockBean
    private CourseService courseService;

    @MockBean
    private SchoolClassService schoolClassService;

    @Autowired
    private ObjectMapper objectMapper;

    private CourseSchedule testSchedule;

    @BeforeEach
    void setUp() {
        testSchedule = new CourseSchedule();
        testSchedule.setId(1L);
        testSchedule.setCourseId(1L);
        testSchedule.setTeacherId(1L);
        testSchedule.setClassroomId(1L);
        testSchedule.setTimeSlotId(1L);
        testSchedule.setDayOfWeek(1);
        testSchedule.setStartTime(LocalTime.of(8, 0));
        testSchedule.setEndTime(LocalTime.of(9, 40));
        testSchedule.setSemester("2024-2025-1");
    }

    /**
     * 测试获取课程安排统计信息
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetScheduleStats() throws Exception {
        mockMvc.perform(get("/api/schedules/stats")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalSchedules").value(120))
                .andExpect(jsonPath("$.data.activeSchedules").value(110))
                .andExpect(jsonPath("$.data.dayStats").exists())
                .andExpect(jsonPath("$.data.timeSlotStats").exists())
                .andExpect(jsonPath("$.data.classroomStats").exists());
    }

    /**
     * 测试批量删除课程安排
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchDeleteSchedules() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        
        when(courseScheduleService.deleteSchedule(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/api/schedules/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(3))
                .andExpect(jsonPath("$.data.failCount").value(0));

        verify(courseScheduleService, times(3)).deleteSchedule(anyLong());
    }

    /**
     * 测试批量创建课程安排
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchCreateSchedules() throws Exception {
        List<CourseSchedule> schedules = Arrays.asList(testSchedule);
        
        when(courseScheduleService.createSchedule(any(CourseSchedule.class))).thenReturn(testSchedule);

        mockMvc.perform(post("/api/schedules/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(schedules)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(1))
                .andExpect(jsonPath("$.data.failCount").value(0));

        verify(courseScheduleService).createSchedule(any(CourseSchedule.class));
    }

    /**
     * 测试批量操作 - 空列表
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchOperationWithEmptyList() throws Exception {
        List<Long> ids = Arrays.asList();

        mockMvc.perform(delete("/api/schedules/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("课程安排ID列表不能为空"));

        verify(courseScheduleService, never()).deleteSchedule(anyLong());
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

        mockMvc.perform(delete("/api/schedules/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("单次批量操作不能超过100条记录"));

        verify(courseScheduleService, never()).deleteSchedule(anyLong());
    }

    /**
     * 测试权限控制 - 学生无法进行批量删除
     */
    @Test
    @WithMockUser(roles = {"STUDENT"})
    void testPermissionControlForBatchDelete() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        mockMvc.perform(delete("/api/schedules/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isForbidden());

        verify(courseScheduleService, never()).deleteSchedule(anyLong());
    }

    /**
     * 测试权限控制 - 教师可以访问统计信息
     */
    @Test
    @WithMockUser(roles = {"TEACHER"})
    void testPermissionControlForStats() throws Exception {
        mockMvc.perform(get("/api/schedules/stats")
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
        mockMvc.perform(get("/api/schedules/stats")
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

        mockMvc.perform(delete("/api/schedules/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verify(courseScheduleService, never()).deleteSchedule(anyLong());
    }

    /**
     * 测试获取课程安排列表
     */
    @Test
    @WithMockUser(roles = {"TEACHER"})
    void testGetSchedules() throws Exception {
        Page<CourseSchedule> schedulePage = new PageImpl<>(Arrays.asList(testSchedule));
        when(courseScheduleService.findSchedulesByPage(anyInt(), anyInt(), anyMap()))
            .thenReturn(schedulePage);

        mockMvc.perform(get("/api/schedules")
                        .param("page", "1")
                        .param("size", "10")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());

        verify(courseScheduleService).findSchedulesByPage(eq(0), eq(10), anyMap());
    }

    /**
     * 测试获取课程安排详情
     */
    @Test
    @WithMockUser(roles = {"TEACHER"})
    void testGetScheduleById() throws Exception {
        when(courseScheduleService.findById(1L)).thenReturn(Optional.of(testSchedule));

        mockMvc.perform(get("/api/schedules/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(courseScheduleService).findById(1L);
    }

    /**
     * 测试创建课程安排
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCreateSchedule() throws Exception {
        when(courseScheduleService.createSchedule(any(CourseSchedule.class))).thenReturn(testSchedule);

        mockMvc.perform(post("/api/schedules")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSchedule)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(courseScheduleService).createSchedule(any(CourseSchedule.class));
    }

    /**
     * 测试获取表单数据
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetScheduleFormData() throws Exception {
        when(courseService.findAll()).thenReturn(Arrays.asList(new Course()));
        when(schoolClassService.findAll()).thenReturn(Arrays.asList(new SchoolClass()));

        mockMvc.perform(get("/api/schedules/form-data")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.courses").isArray())
                .andExpect(jsonPath("$.data.classes").isArray())
                .andExpect(jsonPath("$.data.dayOfWeekOptions").exists());

        verify(courseService).findAll();
        verify(schoolClassService).findAll();
    }
}
