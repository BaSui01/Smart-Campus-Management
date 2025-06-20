package com.campus.domain.entity.organization;

import com.campus.domain.entity.infrastructure.BaseEntity;
import com.campus.shared.security.EncryptionConfig.EncryptedField;
import com.campus.shared.security.EncryptionConfig.EncryptionEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 学生详细档案实体类
 * 存储学生的详细信息，与Student主表分离以优化性能
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Entity
@Table(name = "tb_student_profile", indexes = {
    @Index(name = "idx_student_id", columnList = "student_id"),
    @Index(name = "idx_dormitory", columnList = "dormitory")
})
@EntityListeners(EncryptionEntityListener.class)
public class StudentProfile extends BaseEntity {

    /**
     * 关联的学生ID
     */
    @Column(name = "student_id", nullable = false, unique = true)
    private Long studentId;

    /**
     * 家长/监护人姓名
     */
    @Size(max = 50, message = "家长姓名长度不能超过50个字符")
    @Column(name = "parent_name", length = 50)
    private String parentName;

    /**
     * 家长/监护人电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "家长电话格式不正确")
    @EncryptedField
    @Column(name = "parent_phone", length = 255)
    private String parentPhone;

    /**
     * 紧急联系人
     */
    @Size(max = 50, message = "紧急联系人长度不能超过50个字符")
    @Column(name = "emergency_contact", length = 50)
    private String emergencyContact;

    /**
     * 紧急联系人电话
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "紧急联系人电话格式不正确")
    @EncryptedField
    @Column(name = "emergency_phone", length = 255)
    private String emergencyPhone;

    /**
     * 宿舍号
     */
    @Size(max = 50, message = "宿舍号长度不能超过50个字符")
    @Column(name = "dormitory", length = 50)
    private String dormitory;

    /**
     * 床位号
     */
    @Size(max = 20, message = "床位号长度不能超过20个字符")
    @Column(name = "bed_number", length = 20)
    private String bedNumber;

    /**
     * 家庭住址
     */
    @Size(max = 200, message = "家庭住址长度不能超过200个字符")
    @Column(name = "home_address", length = 200)
    private String homeAddress;

    /**
     * 邮政编码
     */
    @Pattern(regexp = "^\\d{6}$", message = "邮政编码格式不正确")
    @Column(name = "postal_code", length = 6)
    private String postalCode;

    /**
     * 民族
     */
    @Size(max = 20, message = "民族长度不能超过20个字符")
    @Column(name = "ethnicity", length = 20)
    private String ethnicity;

    /**
     * 政治面貌
     */
    @Size(max = 20, message = "政治面貌长度不能超过20个字符")
    @Column(name = "political_status", length = 20)
    private String politicalStatus;

    /**
     * 籍贯
     */
    @Size(max = 100, message = "籍贯长度不能超过100个字符")
    @Column(name = "native_place", length = 100)
    private String nativePlace;

    /**
     * 特长爱好
     */
    @Size(max = 500, message = "特长爱好长度不能超过500个字符")
    @Column(name = "hobbies", length = 500)
    private String hobbies;

    /**
     * 健康状况
     */
    @Size(max = 200, message = "健康状况长度不能超过200个字符")
    @Column(name = "health_status", length = 200)
    private String healthStatus;

    /**
     * 过敏史
     */
    @Size(max = 300, message = "过敏史长度不能超过300个字符")
    @Column(name = "allergy_history", length = 300)
    private String allergyHistory;

    /**
     * 奖惩记录
     */
    @Size(max = 1000, message = "奖惩记录长度不能超过1000个字符")
    @Column(name = "reward_punishment", length = 1000)
    private String rewardPunishment;

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
     * 关联的学生信息
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;

    // ================================
    // 构造函数
    // ================================

    public StudentProfile() {
        super();
    }

    public StudentProfile(Long studentId) {
        this();
        this.studentId = studentId;
    }

    // ================================
    // Getter/Setter 方法
    // ================================

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getDormitory() {
        return dormitory;
    }

    public void setDormitory(String dormitory) {
        this.dormitory = dormitory;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getPoliticalStatus() {
        return politicalStatus;
    }

    public void setPoliticalStatus(String politicalStatus) {
        this.politicalStatus = politicalStatus;
    }

    public String getNativePlace() {
        return nativePlace;
    }

    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getAllergyHistory() {
        return allergyHistory;
    }

    public void setAllergyHistory(String allergyHistory) {
        this.allergyHistory = allergyHistory;
    }

    public String getRewardPunishment() {
        return rewardPunishment;
    }

    public void setRewardPunishment(String rewardPunishment) {
        this.rewardPunishment = rewardPunishment;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
