package com.campus.domain.entity.system;

import com.campus.domain.entity.infrastructure.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * IP白名单实体类
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tb_ip_whitelist")
public class IpWhitelist extends BaseEntity {

    /**
     * IP地址或IP段
     */
    @NotBlank(message = "IP地址不能为空")
    @Size(max = 50, message = "IP地址长度不能超过50个字符")
    @Column(name = "ip_address", nullable = false, length = 50)
    private String ipAddress;

    /**
     * IP类型：1-单个IP，2-IP段，3-IP范围
     */
    @NotNull(message = "IP类型不能为空")
    @Min(value = 1, message = "IP类型值无效")
    @Max(value = 3, message = "IP类型值无效")
    @Column(name = "ip_type", nullable = false)
    private Integer ipType;

    /**
     * 起始IP（用于IP范围）
     */
    @Size(max = 50, message = "起始IP长度不能超过50个字符")
    @Column(name = "start_ip", length = 50)
    private String startIp;

    /**
     * 结束IP（用于IP范围）
     */
    @Size(max = 50, message = "结束IP长度不能超过50个字符")
    @Column(name = "end_ip", length = 50)
    private String endIp;

    /**
     * 子网掩码（用于IP段）
     */
    @Size(max = 20, message = "子网掩码长度不能超过20个字符")
    @Column(name = "subnet_mask", length = 20)
    private String subnetMask;

    /**
     * 白名单名称
     */
    @NotBlank(message = "白名单名称不能为空")
    @Size(max = 100, message = "白名单名称长度不能超过100个字符")
    @Column(name = "whitelist_name", nullable = false, length = 100)
    private String whitelistName;

    /**
     * 描述
     */
    @Size(max = 500, message = "描述长度不能超过500个字符")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 状态：1-启用，0-禁用
     */
    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    @Column(name = "status", columnDefinition = "TINYINT DEFAULT 1")
    private Integer status = 1;

    /**
     * 优先级（数字越小优先级越高）
     */
    @Min(value = 1, message = "优先级不能小于1")
    @Column(name = "priority", columnDefinition = "INT DEFAULT 100")
    private Integer priority = 100;

    /**
     * 访问次数
     */
    @Min(value = 0, message = "访问次数不能为负数")
    @Column(name = "access_count", columnDefinition = "BIGINT DEFAULT 0")
    private Long accessCount = 0L;

    /**
     * 最后访问时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "last_access_time")
    private LocalDateTime lastAccessTime;

    /**
     * 生效时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "effective_time")
    private LocalDateTime effectiveTime;

    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "expire_time")
    private LocalDateTime expireTime;

    /**
     * 创建者ID
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * 更新者ID
     */
    @Column(name = "updated_by")
    private Long updatedBy;

    /**
     * 备注
     */
    @Size(max = 1000, message = "备注长度不能超过1000个字符")
    @Column(name = "remark", length = 1000)
    private String remark;

    // IP类型枚举
    public enum IpType {
        SINGLE(1, "单个IP"),
        SUBNET(2, "IP段"),
        RANGE(3, "IP范围");

        private final int code;
        private final String description;

        IpType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }
        public String getDescription() { return description; }
    }

    // 状态枚举
    public enum Status {
        DISABLED(0, "禁用"),
        ENABLED(1, "启用");

        private final int code;
        private final String description;

        Status(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() { return code; }
        public String getDescription() { return description; }
    }

    /**
     * 检查IP是否在有效期内
     */
    public boolean isEffective() {
        LocalDateTime now = LocalDateTime.now();
        
        if (effectiveTime != null && now.isBefore(effectiveTime)) {
            return false;
        }
        
        if (expireTime != null && now.isAfter(expireTime)) {
            return false;
        }
        
        return status == Status.ENABLED.getCode();
    }

    /**
     * 增加访问次数
     */
    public void incrementAccessCount() {
        this.accessCount = this.accessCount == null ? 1L : this.accessCount + 1;
        this.lastAccessTime = LocalDateTime.now();
    }
}
