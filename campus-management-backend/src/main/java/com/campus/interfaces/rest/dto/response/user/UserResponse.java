package com.campus.interfaces.rest.dto.response.user;

import com.campus.interfaces.rest.dto.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * 用户响应DTO
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-19
 */
@Schema(description = "用户响应数据")
public class UserResponse extends BaseResponse {

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    /**
     * 手机号
     */
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /**
     * 性别
     */
    @Schema(description = "性别", example = "M")
    private String gender;

    /**
     * 性别文本
     */
    @Schema(description = "性别文本", example = "男")
    private String genderText;

    /**
     * 头像URL
     */
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    /**
     * 角色ID
     */
    @Schema(description = "角色ID", example = "2")
    private Long roleId;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称", example = "教师")
    private String roleName;

    /**
     * 角色键值
     */
    @Schema(description = "角色键值", example = "TEACHER")
    private String roleKey;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID", example = "1")
    private Long departmentId;

    /**
     * 部门名称
     */
    @Schema(description = "部门名称", example = "计算机学院")
    private String departmentName;

    /**
     * 登录次数
     */
    @Schema(description = "登录次数", example = "10")
    private Integer loginCount;

    /**
     * 最后登录时间
     */
    @Schema(description = "最后登录时间", example = "2025-06-19 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    @Schema(description = "最后登录IP", example = "192.168.1.100")
    private String lastLoginIp;

    /**
     * 是否在线
     */
    @Schema(description = "是否在线", example = "true")
    private Boolean online;

    // 构造函数
    public UserResponse() {}

    // Getter和Setter方法
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
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
        // 自动设置性别文本
        if ("M".equals(gender)) {
            this.genderText = "男";
        } else if ("F".equals(gender)) {
            this.genderText = "女";
        } else {
            this.genderText = "未知";
        }
    }

    public String getGenderText() {
        return genderText;
    }

    public void setGenderText(String genderText) {
        this.genderText = genderText;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "id=" + getId() +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", realName='" + realName + '\'' +
                ", phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", genderText='" + genderText + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", roleKey='" + roleKey + '\'' +
                ", departmentId=" + departmentId +
                ", departmentName='" + departmentName + '\'' +
                ", loginCount=" + loginCount +
                ", lastLoginTime=" + lastLoginTime +
                ", lastLoginIp='" + lastLoginIp + '\'' +
                ", online=" + online +
                ", status=" + getStatus() +
                ", statusText='" + getStatusText() + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}
