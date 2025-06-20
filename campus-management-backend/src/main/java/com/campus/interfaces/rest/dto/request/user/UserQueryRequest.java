package com.campus.interfaces.rest.dto.request.user;

import com.campus.interfaces.rest.dto.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

/**
 * 用户查询请求DTO
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-19
 */
@Schema(description = "用户查询请求")
public class UserQueryRequest extends PageRequest {

    /**
     * 用户名（模糊查询）
     */
    @Schema(description = "用户名", example = "zhang")
    private String username;

    /**
     * 真实姓名（模糊查询）
     */
    @Schema(description = "真实姓名", example = "张")
    private String realName;

    /**
     * 邮箱（模糊查询）
     */
    @Schema(description = "邮箱", example = "zhang@")
    private String email;

    /**
     * 手机号（模糊查询）
     */
    @Schema(description = "手机号", example = "138")
    private String phone;

    /**
     * 性别
     */
    @Schema(description = "性别", example = "M", allowableValues = {"M", "F", "U"})
    @Pattern(regexp = "^[MFU]$", message = "性别只能是M(男)、F(女)或U(未知)")
    private String gender;

    /**
     * 角色ID
     */
    @Schema(description = "角色ID", example = "2")
    private Long roleId;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID", example = "1")
    private Long departmentId;

    /**
     * 状态
     */
    @Schema(description = "状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;

    /**
     * 创建时间开始
     */
    @Schema(description = "创建时间开始", example = "2025-01-01")
    private String createdAtStart;

    /**
     * 创建时间结束
     */
    @Schema(description = "创建时间结束", example = "2025-12-31")
    private String createdAtEnd;

    // 构造函数
    public UserQueryRequest() {}

    // Getter和Setter方法
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreatedAtStart() {
        return createdAtStart;
    }

    public void setCreatedAtStart(String createdAtStart) {
        this.createdAtStart = createdAtStart;
    }

    public String getCreatedAtEnd() {
        return createdAtEnd;
    }

    public void setCreatedAtEnd(String createdAtEnd) {
        this.createdAtEnd = createdAtEnd;
    }

    @Override
    public String toString() {
        return "UserQueryRequest{" +
                "username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", roleId=" + roleId +
                ", departmentId=" + departmentId +
                ", status=" + status +
                ", createdAtStart='" + createdAtStart + '\'' +
                ", createdAtEnd='" + createdAtEnd + '\'' +
                ", page=" + getPage() +
                ", size=" + getSize() +
                ", sortBy='" + getSortBy() + '\'' +
                ", sortDirection='" + getSortDirection() + '\'' +
                ", keyword='" + getKeyword() + '\'' +
                '}';
    }
}
