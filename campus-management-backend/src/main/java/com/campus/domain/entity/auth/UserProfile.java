package com.campus.domain.entity.auth;

import com.campus.domain.entity.infrastructure.BaseEntity;
import com.campus.shared.security.EncryptionConfig.EncryptedField;
import com.campus.shared.security.EncryptionConfig.EncryptionEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * 用户详细档案实体类
 * 存储用户的详细信息，与User主表分离以优化性能
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Entity
@Table(name = "tb_user_profile", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_id_card", columnList = "id_card")
})
@EntityListeners(EncryptionEntityListener.class)
public class UserProfile extends BaseEntity {

    /**
     * 关联的用户ID
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    /**
     * 生日
     */
    @Column(name = "birthday")
    private LocalDate birthday;

    /**
     * 身份证号
     */
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$",
             message = "身份证号格式不正确")
    @EncryptedField
    @Column(name = "id_card", length = 255)
    private String idCard;

    /**
     * 头像URL
     */
    @Size(max = 500, message = "头像URL长度不能超过500个字符")
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    /**
     * 地址
     */
    @Size(max = 200, message = "地址长度不能超过200个字符")
    @Column(name = "address", length = 200)
    private String address;

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
     * 婚姻状况
     */
    @Size(max = 10, message = "婚姻状况长度不能超过10个字符")
    @Column(name = "marital_status", length = 10)
    private String maritalStatus;

    /**
     * 学历
     */
    @Size(max = 20, message = "学历长度不能超过20个字符")
    @Column(name = "education", length = 20)
    private String education;

    /**
     * 毕业院校
     */
    @Size(max = 100, message = "毕业院校长度不能超过100个字符")
    @Column(name = "graduate_school", length = 100)
    private String graduateSchool;

    /**
     * 专业
     */
    @Size(max = 50, message = "专业长度不能超过50个字符")
    @Column(name = "major", length = 50)
    private String major;

    /**
     * 工作单位
     */
    @Size(max = 100, message = "工作单位长度不能超过100个字符")
    @Column(name = "work_unit", length = 100)
    private String workUnit;

    /**
     * 职务
     */
    @Size(max = 50, message = "职务长度不能超过50个字符")
    @Column(name = "position", length = 50)
    private String position;

    /**
     * 职称
     */
    @Size(max = 50, message = "职称长度不能超过50个字符")
    @Column(name = "title", length = 50)
    private String title;

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
     * 个人简介
     */
    @Size(max = 1000, message = "个人简介长度不能超过1000个字符")
    @Column(name = "bio", length = 1000)
    private String bio;

    /**
     * 特长技能
     */
    @Size(max = 500, message = "特长技能长度不能超过500个字符")
    @Column(name = "skills", length = 500)
    private String skills;

    /**
     * 兴趣爱好
     */
    @Size(max = 500, message = "兴趣爱好长度不能超过500个字符")
    @Column(name = "hobbies", length = 500)
    private String hobbies;

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
     * 关联的用户信息
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    // ================================
    // 构造函数
    // ================================

    public UserProfile() {
        super();
    }

    public UserProfile(Long userId) {
        this();
        this.userId = userId;
    }

    // ================================
    // Getter/Setter 方法
    // ================================

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getGraduateSchool() {
        return graduateSchool;
    }

    public void setGraduateSchool(String graduateSchool) {
        this.graduateSchool = graduateSchool;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getWorkUnit() {
        return workUnit;
    }

    public void setWorkUnit(String workUnit) {
        this.workUnit = workUnit;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
