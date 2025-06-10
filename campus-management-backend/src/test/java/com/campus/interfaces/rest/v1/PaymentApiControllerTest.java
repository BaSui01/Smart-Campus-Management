package com.campus.interfaces.rest.v1;

import com.campus.application.service.PaymentRecordService;
import com.campus.domain.entity.PaymentRecord;
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
 * PaymentApiController单元测试
 */
@WebMvcTest(PaymentApiController.class)
@SpringJUnitConfig
class PaymentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentRecordService paymentRecordService;

    @Autowired
    private ObjectMapper objectMapper;

    private PaymentRecord testPaymentRecord;

    @BeforeEach
    void setUp() {
        testPaymentRecord = new PaymentRecord();
        testPaymentRecord.setId(1L);
        testPaymentRecord.setStudentId(1L);
        testPaymentRecord.setAmount(new BigDecimal("1000.00"));
        testPaymentRecord.setPaymentMethod("alipay");
        testPaymentRecord.setPaymentStatus(1); // 1: 已缴费
    }

    /**
     * 测试获取缴费统计信息
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetPaymentStats() throws Exception {
        // Mock service responses
        PaymentRecordService.PaymentStatistics mockStats = createMockPaymentStatistics();
        when(paymentRecordService.getStatistics()).thenReturn(mockStats);
        when(paymentRecordService.findByPaymentMethod("alipay")).thenReturn(Arrays.asList(testPaymentRecord));
        when(paymentRecordService.findByPaymentMethod("wechat")).thenReturn(Arrays.asList());
        when(paymentRecordService.findByPaymentMethod("bank")).thenReturn(Arrays.asList());
        when(paymentRecordService.findByPaymentMethod("cash")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/payments/stats")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalPayments").value(100))
                .andExpect(jsonPath("$.data.successPayments").value(85))
                .andExpect(jsonPath("$.data.methodStats.alipay").value(1));

        verify(paymentRecordService).getStatistics();
        verify(paymentRecordService).findByPaymentMethod("alipay");
    }

    /**
     * 测试批量删除缴费记录
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchDeletePayments() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        
        when(paymentRecordService.batchDeletePaymentRecords(anyList())).thenReturn(true);

        mockMvc.perform(delete("/api/payments/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.deletedCount").value(3))
                .andExpect(jsonPath("$.data.success").value(true));

        verify(paymentRecordService).batchDeletePaymentRecords(ids);
    }

    /**
     * 测试批量删除缴费记录 - 删除失败
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchDeletePaymentsFailure() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        
        when(paymentRecordService.batchDeletePaymentRecords(anyList())).thenReturn(false);

        mockMvc.perform(delete("/api/payments/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("批量删除缴费记录失败"));

        verify(paymentRecordService).batchDeletePaymentRecords(ids);
    }

    /**
     * 测试批量退款
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchRefundPayments() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        Map<String, Object> request = new HashMap<>();
        request.put("ids", ids);
        request.put("refundReason", "系统错误");
        request.put("operatorId", 1);

        when(paymentRecordService.refundPayment(anyLong(), anyString(), anyLong())).thenReturn(true);

        mockMvc.perform(put("/api/payments/batch/refund")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(3))
                .andExpect(jsonPath("$.data.failCount").value(0));

        verify(paymentRecordService, times(3)).refundPayment(anyLong(), eq("系统错误"), eq(1L));
    }

    /**
     * 测试批量退款 - 缺少退款原因
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchRefundPaymentsWithoutReason() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        Map<String, Object> request = new HashMap<>();
        request.put("ids", ids);
        request.put("operatorId", 1);

        mockMvc.perform(put("/api/payments/batch/refund")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("退款原因不能为空"));

        verify(paymentRecordService, never()).refundPayment(anyLong(), anyString(), anyLong());
    }

    /**
     * 测试批量操作 - 空列表
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testBatchOperationWithEmptyList() throws Exception {
        List<Long> ids = Arrays.asList();

        mockMvc.perform(delete("/api/payments/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("缴费记录ID列表不能为空"));

        verify(paymentRecordService, never()).batchDeletePaymentRecords(anyList());
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

        mockMvc.perform(delete("/api/payments/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("单次批量操作不能超过100条记录"));

        verify(paymentRecordService, never()).batchDeletePaymentRecords(anyList());
    }

    /**
     * 测试权限控制 - 学生无法访问统计信息
     */
    @Test
    @WithMockUser(roles = {"STUDENT"})
    void testPermissionControlForStats() throws Exception {
        mockMvc.perform(get("/api/payments/stats")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(paymentRecordService, never()).getStatistics();
    }

    /**
     * 测试权限控制 - 教师无法进行批量删除
     */
    @Test
    @WithMockUser(roles = {"TEACHER"})
    void testPermissionControlForBatchDelete() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L, 3L);

        mockMvc.perform(delete("/api/payments/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isForbidden());

        verify(paymentRecordService, never()).batchDeletePaymentRecords(anyList());
    }

    /**
     * 测试异常处理
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testExceptionHandling() throws Exception {
        when(paymentRecordService.getStatistics()).thenThrow(new RuntimeException("数据库连接失败"));

        mockMvc.perform(get("/api/payments/stats")
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("获取缴费统计信息失败: 数据库连接失败"));

        verify(paymentRecordService).getStatistics();
    }

    // 辅助方法
    private PaymentRecordService.PaymentStatistics createMockPaymentStatistics() {
        return new PaymentRecordService.PaymentStatistics() {
            @Override
            public long getTotalRecords() { return 100; }
            
            @Override
            public long getSuccessRecords() { return 85; }
            
            @Override
            public long getRefundRecords() { return 10; }
            
            @Override
            public long getFailedRecords() { return 5; }
            
            @Override
            public BigDecimal getTotalAmount() { return new BigDecimal("50000.00"); }
            
            @Override
            public BigDecimal getSuccessAmount() { return new BigDecimal("42500.00"); }
            
            @Override
            public BigDecimal getRefundAmount() { return new BigDecimal("5000.00"); }
        };
    }
}
