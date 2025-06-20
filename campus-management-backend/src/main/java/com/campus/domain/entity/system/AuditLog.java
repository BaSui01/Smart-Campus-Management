package com.campus.domain.entity.system;

import com.campus.domain.entity.infrastructure.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 审计日志实体类
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_audit_log")
public class AuditLog extends BaseEntity {

    /**
     * 操作用户ID
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 用户名
     */
    @Size(max = 100, message = "用户名长度不能超过100个字符")
    @Column(name = "username", length = 100)
    private String username;

    /**
     * 用户真实姓名
     */
    @Size(max = 100, message = "真实姓名长度不能超过100个字符")
    @Column(name = "real_name", length = 100)
    private String realName;

    /**
     * 操作类型
     */
    @NotBlank(message = "操作类型不能为空")
    @Size(max = 50, message = "操作类型长度不能超过50个字符")
    @Column(name = "operation_type", nullable = false, length = 50)
    private String operationType;

    /**
     * 操作描述
     */
    @Size(max = 500, message = "操作描述长度不能超过500个字符")
    @Column(name = "operation_desc", length = 500)
    private String operationDesc;

    /**
     * 操作模块
     */
    @Size(max = 100, message = "操作模块长度不能超过100个字符")
    @Column(name = "module", length = 100)
    private String module;

    /**
     * 操作方法
     */
    @Size(max = 200, message = "操作方法长度不能超过200个字符")
    @Column(name = "method", length = 200)
    private String method;

    /**
     * 请求URL
     */
    @Size(max = 500, message = "请求URL长度不能超过500个字符")
    @Column(name = "request_url", length = 500)
    private String requestUrl;

    /**
     * HTTP方法
     */
    @Size(max = 10, message = "HTTP方法长度不能超过10个字符")
    @Column(name = "http_method", length = 10)
    private String httpMethod;

    /**
     * 客户端IP
     */
    @Size(max = 50, message = "客户端IP长度不能超过50个字符")
    @Column(name = "client_ip", length = 50)
    private String clientIp;

    /**
     * 用户代理
     */
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    /**
     * 请求参数
     */
    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams;

    /**
     * 响应结果
     */
    @Column(name = "response_result", columnDefinition = "TEXT")
    private String responseResult;

    /**
     * 操作状态：1-成功，0-失败
     */
    @Min(value = 0, message = "操作状态值无效")
    @Max(value = 1, message = "操作状态值无效")
    @Column(name = "operation_status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer operationStatus = 1;

    /**
     * 错误消息
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * 执行时长（毫秒）
     */
    @Min(value = 0, message = "执行时长不能为负数")
    @Column(name = "execution_time")
    private Long executionTime;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "operation_time", nullable = false)
    private LocalDateTime operationTime;

    /**
     * 风险等级：1-低，2-中，3-高
     */
    @Min(value = 1, message = "风险等级值无效")
    @Max(value = 3, message = "风险等级值无效")
    @Column(name = "risk_level", columnDefinition = "TINYINT DEFAULT 1")
    private Integer riskLevel = 1;

    /**
     * 业务类型
     */
    @Size(max = 50, message = "业务类型长度不能超过50个字符")
    @Column(name = "business_type", length = 50)
    private String businessType;

    /**
     * 业务ID
     */
    @Size(max = 100, message = "业务ID长度不能超过100个字符")
    @Column(name = "business_id", length = 100)
    private String businessId;

    /**
     * 操作前数据
     */
    @Column(name = "before_data", columnDefinition = "TEXT")
    private String beforeData;

    /**
     * 操作后数据
     */
    @Column(name = "after_data", columnDefinition = "TEXT")
    private String afterData;

    // 操作类型枚举
    public enum OperationType {
        LOGIN("LOGIN", "登录"),
        LOGOUT("LOGOUT", "登出"),
        CREATE("CREATE", "创建"),
        UPDATE("UPDATE", "更新"),
        DELETE("DELETE", "删除"),
        QUERY("QUERY", "查询"),
        EXPORT("EXPORT", "导出"),
        IMPORT("IMPORT", "导入"),
        UPLOAD("UPLOAD", "上传"),
        DOWNLOAD("DOWNLOAD", "下载"),
        APPROVE("APPROVE", "审批"),
        REJECT("REJECT", "拒绝"),
        RESET("RESET", "重置"),
        ENABLE("ENABLE", "启用"),
        DISABLE("DISABLE", "禁用");

        private final String code;
        private final String description;

        OperationType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() { return code; }
        public String getDescription() { return description; }
    }

    // 风险等级枚举
    public enum RiskLevel {
        LOW(1, "低风险"),
        MEDIUM(2, "中风险"),
        HIGH(3, "高风险");

        private final int code;
        private final String description;

        RiskLevel(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }
        public String getDescription() { return description; }
    }

    // 操作状态枚举
    public enum OperationStatus {
        SUCCESS(1, "成功"),
        FAILURE(0, "失败");

        private final int code;
        private final String description;

        OperationStatus(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }
        public String getDescription() { return description; }
    }
}
