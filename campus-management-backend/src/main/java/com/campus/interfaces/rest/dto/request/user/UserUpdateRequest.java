package com.campus.interfaces.rest.dto.request.user;

import com.campus.interfaces.rest.dto.common.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * 用户更新请求DTO
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-19
 */
@Schema(description = "用户更新请求")
public class UserUpdateRequest extends BaseRequest {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "用户ID不能为空")
    @Min(value = 1, message = "用户ID必须大于0")
    private Long id;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名", example = "张三")
    @Size(min = 2, max = 50, message = "真实姓名长度必须在2-50个字符之间")
    private String realName;

    /**
     * 手机号
     */
    @Schema(description = "手机号", example = "13800138000")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 性别
     */
    @Schema(description = "性别", example = "M", allowableValues = {"M", "F", "U"})
    @Pattern(regexp = "^[MFU]$", message = "性别只能是M(男)、F(女)或U(未知)")
    private String gender;

    /**
     * 头像URL
     */
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    private String avatarUrl;

    /**
     * 角色ID
     */
    @Schema(description = "角色ID", example = "2")
    @Min(value = 1, message = "角色ID必须大于0")
    private Long roleId;

    /**
     * 部门ID
     */
    @Schema(description = "部门ID", example = "1")
    private Long departmentId;

    /**
     * 新密码（可选）
     */
    @Schema(description = "新密码", example = "newpassword123")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String newPassword;

    /**
     * 确认新密码
     */
    @Schema(description = "确认新密码", example = "newpassword123")
    private String confirmNewPassword;

    // 构造函数
    public UserUpdateRequest() {}

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }

    /**
     * 验证新密码和确认新密码是否一致
     */
    public boolean isNewPasswordMatch() {
        if (newPassword == null && confirmNewPassword == null) {
            return true; // 都为空表示不修改密码
        }
        return newPassword != null && newPassword.equals(confirmNewPassword);
    }

    /**
     * 是否需要更新密码
     */
    public boolean needUpdatePassword() {
        return newPassword != null && !newPassword.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "UserUpdateRequest{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", realName='" + realName + '\'' +
                ", phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", roleId=" + roleId +
                ", departmentId=" + departmentId +
                ", needUpdatePassword=" + needUpdatePassword() +
                ", status=" + getStatus() +
                ", remarks='" + getRemarks() + '\'' +
                '}';
    }
}
