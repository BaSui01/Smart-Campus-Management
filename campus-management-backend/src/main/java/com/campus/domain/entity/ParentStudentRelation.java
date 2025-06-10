package com.campus.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 家长学生关联关系实体类
 * 管理家长与学生的关联关系，支持一个家长关联多个学生
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_parent_student_relation", indexes = {
    @Index(name = "idx_parent_id", columnList = "parent_id"),
    @Index(name = "idx_student_id", columnList = "student_id"),
    @Index(name = "idx_relationship", columnList = "relationship"),
    @Index(name = "idx_is_primary", columnList = "is_primary")
})
public class ParentStudentRelation extends BaseEntity {

    /**
     * 家长用户ID
     */
    @NotNull(message = "家长ID不能为空")
    @Column(name = "parent_id", nullable = false)
    private Long parentId;

    /**
     * 学生ID
     */
    @NotNull(message = "学生ID不能为空")
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    /**
     * 关系类型 (father/mother/guardian/grandparent/other)
     */
    @NotBlank(message = "关系类型不能为空")
    @Size(max = 20, message = "关系类型长度不能超过20个字符")
    @Column(name = "relationship", nullable = false, length = 20)
    private String relationType;

    /**
     * 关系状态
     */
    @Size(max = 20, message = "关系状态长度不能超过20个字符")
    @Column(name = "relation_status", length = 20)
    private String relationStatus = "active";

    /**
     * 是否为主要联系人
     */
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    /**
     * 是否有查看权限
     */
    @Column(name = "can_view", nullable = false)
    private Boolean canView = true;

    /**
     * 是否有操作权限（如确认缴费等）
     */
    @Column(name = "can_operate", nullable = false)
    private Boolean canOperate = false;

    /**
     * 是否接收通知
     */
    @Column(name = "receive_notifications", nullable = false)
    private Boolean receiveNotifications = true;

    /**
     * 通知方式 (email/sms/wechat/all)
     */
    @Size(max = 50, message = "通知方式长度不能超过50个字符")
    @Column(name = "notification_methods", length = 50)
    private String notificationMethods = "email";

    /**
     * 关系建立时间
     */
    @Column(name = "established_time")
    private LocalDateTime establishedTime;

    /**
     * 关系确认状态 (pending/confirmed/rejected)
     */
    @Size(max = 20, message = "确认状态长度不能超过20个字符")
    @Column(name = "confirmation_status", length = 20)
    private String confirmationStatus = "pending";

    /**
     * 确认时间
     */
    @Column(name = "confirmed_time")
    private LocalDateTime confirmedTime;

    /**
     * 确认人ID（通常是学校管理员）
     */
    @Column(name = "confirmed_by")
    private Long confirmedBy;

    /**
     * 关系描述
     */
    @Size(max = 200, message = "关系描述长度不能超过200个字符")
    @Column(name = "description", length = 200)
    private String description;

    /**
     * 紧急联系人优先级（数字越小优先级越高）
     */
    @Column(name = "emergency_priority")
    private Integer emergencyPriority;

    /**
     * 是否已验证身份
     */
    @Column(name = "identity_verified", nullable = false)
    private Boolean identityVerified = false;

    /**
     * 身份验证方式
     */
    @Size(max = 50, message = "身份验证方式长度不能超过50个字符")
    @Column(name = "verification_method", length = 50)
    private String verificationMethod;

    /**
     * 身份验证时间
     */
    @Column(name = "verification_time")
    private LocalDateTime verificationTime;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Column(name = "remarks", length = 500)
    private String remarks;

    // ================================
    // 关联关系
    // ================================

    /**
     * 家长用户信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    @JsonIgnore
    private User parent;

    /**
     * 学生信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    @JsonIgnore
    private Student student;

    /**
     * 确认人信息
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_by", insertable = false, updatable = false)
    @JsonIgnore
    private User confirmer;

    // ================================
    // 构造函数
    // ================================

    public ParentStudentRelation() {
        super();
    }

    public ParentStudentRelation(Long parentId, Long studentId, String relationType) {
        this();
        this.parentId = parentId;
        this.studentId = studentId;
        this.relationType = relationType;
        this.establishedTime = LocalDateTime.now();
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 获取关系类型文本
     */
    public String getRelationTypeText() {
        if (relationType == null) return "未知";
        return switch (relationType) {
            case "father" -> "父亲";
            case "mother" -> "母亲";
            case "guardian" -> "监护人";
            case "grandparent" -> "祖父母/外祖父母";
            case "other" -> "其他";
            default -> relationType;
        };
    }

    /**
     * 获取确认状态文本
     */
    public String getConfirmationStatusText() {
        if (confirmationStatus == null) return "未知";
        return switch (confirmationStatus) {
            case "pending" -> "待确认";
            case "confirmed" -> "已确认";
            case "rejected" -> "已拒绝";
            default -> confirmationStatus;
        };
    }

    /**
     * 确认关系
     */
    public void confirm(Long confirmerId) {
        this.confirmationStatus = "confirmed";
        this.confirmedTime = LocalDateTime.now();
        this.confirmedBy = confirmerId;
    }

    /**
     * 拒绝关系
     */
    public void reject(Long confirmerId) {
        this.confirmationStatus = "rejected";
        this.confirmedTime = LocalDateTime.now();
        this.confirmedBy = confirmerId;
    }

    /**
     * 设置为主要联系人
     */
    public void setPrimaryContact() {
        this.isPrimary = true;
        this.canOperate = true;
        this.receiveNotifications = true;
    }

    /**
     * 验证身份
     */
    public void verifyIdentity(String method) {
        this.identityVerified = true;
        this.verificationMethod = method;
        this.verificationTime = LocalDateTime.now();
    }

    /**
     * 检查是否可以查看学生信息
     */
    public boolean canViewStudentInfo() {
        return "confirmed".equals(confirmationStatus) && canView && isEnabled();
    }

    /**
     * 检查是否可以进行操作
     */
    public boolean canPerformOperations() {
        return "confirmed".equals(confirmationStatus) && canOperate && isEnabled();
    }

    /**
     * 检查是否应该接收通知
     */
    public boolean shouldReceiveNotifications() {
        return "confirmed".equals(confirmationStatus) && receiveNotifications && isEnabled();
    }

    /**
     * 检查是否为紧急联系人
     */
    public boolean isEmergencyContact() {
        return emergencyPriority != null && emergencyPriority <= 3; // 前3位为紧急联系人
    }

    /**
     * 获取家长姓名
     */
    public String getParentName() {
        return parent != null ? parent.getRealName() : null;
    }

    /**
     * 获取家长手机号
     */
    public String getParentPhone() {
        return parent != null ? parent.getPhone() : null;
    }

    /**
     * 获取家长邮箱
     */
    public String getParentEmail() {
        return parent != null ? parent.getEmail() : null;
    }

    /**
     * 获取学生姓名
     */
    public String getStudentName() {
        return student != null ? student.getRealName() : null;
    }

    /**
     * 获取学生学号
     */
    public String getStudentNumber() {
        return student != null ? student.getStudentNumber() : null;
    }

    /**
     * 获取确认人姓名
     */
    public String getConfirmerName() {
        return confirmer != null ? confirmer.getRealName() : null;
    }

    /**
     * 获取通知方式列表
     */
    public String[] getNotificationMethodArray() {
        if (notificationMethods == null || notificationMethods.trim().isEmpty()) {
            return new String[0];
        }
        return notificationMethods.split(",");
    }

    /**
     * 设置通知方式
     */
    public void setNotificationMethodArray(String[] methods) {
        if (methods == null || methods.length == 0) {
            this.notificationMethods = "";
        } else {
            this.notificationMethods = String.join(",", methods);
        }
    }

    /**
     * 检查是否支持指定的通知方式
     */
    public boolean supportsNotificationMethod(String method) {
        if (notificationMethods == null) return false;
        return notificationMethods.contains(method) || "all".equals(notificationMethods);
    }

    /**
     * 验证关系数据
     */
    @PrePersist
    @PreUpdate
    public void validateRelation() {
        if (parentId != null && parentId.equals(studentId)) {
            throw new IllegalArgumentException("家长和学生不能是同一个人");
        }
        
        if (emergencyPriority != null && emergencyPriority < 1) {
            throw new IllegalArgumentException("紧急联系人优先级必须大于0");
        }
    }

    // ================================
    // Getter and Setter methods
    // ================================

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getRelationStatus() {
        return relationStatus;
    }

    public void setRelationStatus(String relationStatus) {
        this.relationStatus = relationStatus;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public Boolean getCanView() {
        return canView;
    }

    public void setCanView(Boolean canView) {
        this.canView = canView;
    }

    public Boolean getCanOperate() {
        return canOperate;
    }

    public void setCanOperate(Boolean canOperate) {
        this.canOperate = canOperate;
    }

    public Boolean getReceiveNotifications() {
        return receiveNotifications;
    }

    public void setReceiveNotifications(Boolean receiveNotifications) {
        this.receiveNotifications = receiveNotifications;
    }

    public String getNotificationMethods() {
        return notificationMethods;
    }

    public void setNotificationMethods(String notificationMethods) {
        this.notificationMethods = notificationMethods;
    }

    public LocalDateTime getEstablishedTime() {
        return establishedTime;
    }

    public void setEstablishedTime(LocalDateTime establishedTime) {
        this.establishedTime = establishedTime;
    }

    public String getConfirmationStatus() {
        return confirmationStatus;
    }

    public void setConfirmationStatus(String confirmationStatus) {
        this.confirmationStatus = confirmationStatus;
    }

    public LocalDateTime getConfirmedTime() {
        return confirmedTime;
    }

    public void setConfirmedTime(LocalDateTime confirmedTime) {
        this.confirmedTime = confirmedTime;
    }

    public Long getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(Long confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEmergencyPriority() {
        return emergencyPriority;
    }

    public void setEmergencyPriority(Integer emergencyPriority) {
        this.emergencyPriority = emergencyPriority;
    }

    public Boolean getIdentityVerified() {
        return identityVerified;
    }

    public void setIdentityVerified(Boolean identityVerified) {
        this.identityVerified = identityVerified;
    }

    public String getVerificationMethod() {
        return verificationMethod;
    }

    public void setVerificationMethod(String verificationMethod) {
        this.verificationMethod = verificationMethod;
    }

    public LocalDateTime getVerificationTime() {
        return verificationTime;
    }

    public void setVerificationTime(LocalDateTime verificationTime) {
        this.verificationTime = verificationTime;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public User getParent() {
        return parent;
    }

    public void setParent(User parent) {
        this.parent = parent;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public User getConfirmer() {
        return confirmer;
    }

    public void setConfirmer(User confirmer) {
        this.confirmer = confirmer;
    }
}
