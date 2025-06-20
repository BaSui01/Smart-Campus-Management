package com.campus.interfaces.rest.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * 基础请求DTO
 * 包含通用的请求字段
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-19
 */
@Schema(description = "基础请求数据")
public abstract class BaseRequest {

    /**
     * 状态
     */
    @Schema(description = "状态", example = "1", allowableValues = {"0", "1"})
    private Integer status = 1;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remarks;

    // 构造函数
    public BaseRequest() {}

    // Getter和Setter方法
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "BaseRequest{" +
                "status=" + status +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
