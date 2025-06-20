package com.campus.interfaces.rest.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 分页请求DTO
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-19
 */
@Schema(description = "分页请求参数")
public class PageRequest {

    /**
     * 页码，从1开始
     */
    @Schema(description = "页码，从1开始", example = "1", minimum = "1")
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "20", minimum = "1", maximum = "100")
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer size = 20;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段", example = "createdAt")
    private String sortBy = "createdAt";

    /**
     * 排序方向
     */
    @Schema(description = "排序方向", example = "desc", allowableValues = {"asc", "desc"})
    private String sortDirection = "desc";

    /**
     * 搜索关键词
     */
    @Schema(description = "搜索关键词", example = "张三")
    private String keyword;

    // 构造函数
    public PageRequest() {}

    public PageRequest(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    public PageRequest(Integer page, Integer size, String sortBy, String sortDirection) {
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }

    // Getter和Setter方法
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * 获取偏移量（用于数据库查询）
     */
    public int getOffset() {
        return (page - 1) * size;
    }

    /**
     * 转换为Spring Data的Pageable对象
     */
    public org.springframework.data.domain.Pageable toPageable() {
        org.springframework.data.domain.Sort.Direction direction = 
            "asc".equalsIgnoreCase(sortDirection) ? 
            org.springframework.data.domain.Sort.Direction.ASC : 
            org.springframework.data.domain.Sort.Direction.DESC;
        
        return org.springframework.data.domain.PageRequest.of(
            page - 1, 
            size, 
            org.springframework.data.domain.Sort.by(direction, sortBy)
        );
    }

    @Override
    public String toString() {
        return "PageRequest{" +
                "page=" + page +
                ", size=" + size +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                ", keyword='" + keyword + '\'' +
                '}';
    }
}
