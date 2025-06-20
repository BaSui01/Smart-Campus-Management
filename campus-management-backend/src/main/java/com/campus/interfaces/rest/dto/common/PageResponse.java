package com.campus.interfaces.rest.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 分页响应DTO
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-19
 */
@Schema(description = "分页响应数据")
public class PageResponse<T> {

    /**
     * 数据列表
     */
    @Schema(description = "数据列表")
    private List<T> content;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1")
    private Integer page;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "20")
    private Integer size;

    /**
     * 总记录数
     */
    @Schema(description = "总记录数", example = "100")
    private Long total;

    /**
     * 总页数
     */
    @Schema(description = "总页数", example = "5")
    private Integer totalPages;

    /**
     * 是否为第一页
     */
    @Schema(description = "是否为第一页", example = "true")
    private Boolean first;

    /**
     * 是否为最后一页
     */
    @Schema(description = "是否为最后一页", example = "false")
    private Boolean last;

    /**
     * 是否有下一页
     */
    @Schema(description = "是否有下一页", example = "true")
    private Boolean hasNext;

    /**
     * 是否有上一页
     */
    @Schema(description = "是否有上一页", example = "false")
    private Boolean hasPrevious;

    // 构造函数
    public PageResponse() {}

    public PageResponse(List<T> content, Integer page, Integer size, Long total) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / size);
        this.first = page == 1;
        this.last = page.equals(totalPages);
        this.hasNext = page < totalPages;
        this.hasPrevious = page > 1;
    }

    /**
     * 从Spring Data的Page对象创建PageResponse
     */
    public static <T> PageResponse<T> of(org.springframework.data.domain.Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber() + 1, // Spring Data页码从0开始，转换为从1开始
            page.getSize(),
            page.getTotalElements()
        );
    }

    /**
     * 创建空的分页响应
     */
    public static <T> PageResponse<T> empty(Integer page, Integer size) {
        return new PageResponse<>(List.of(), page, size, 0L);
    }

    // Getter和Setter方法
    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

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

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Boolean getFirst() {
        return first;
    }

    public void setFirst(Boolean first) {
        this.first = first;
    }

    public Boolean getLast() {
        return last;
    }

    public void setLast(Boolean last) {
        this.last = last;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public Boolean getHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(Boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    @Override
    public String toString() {
        return "PageResponse{" +
                "content=" + content +
                ", page=" + page +
                ", size=" + size +
                ", total=" + total +
                ", totalPages=" + totalPages +
                ", first=" + first +
                ", last=" + last +
                ", hasNext=" + hasNext +
                ", hasPrevious=" + hasPrevious +
                '}';
    }
}
