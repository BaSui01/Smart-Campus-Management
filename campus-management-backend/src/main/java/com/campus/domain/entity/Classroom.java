package com.campus.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


import java.util.List;

/**
 * 教室实体类
 * 管理教室信息、容量、设备等
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_classroom", indexes = {
    @Index(name = "idx_classroom_no", columnList = "classroom_no"),
    @Index(name = "idx_building_id", columnList = "building_id"),
    @Index(name = "idx_classroom_type", columnList = "classroom_type"),
    @Index(name = "idx_capacity", columnList = "capacity"),
    @Index(name = "idx_status_deleted", columnList = "status,deleted")
})
public class Classroom extends BaseEntity {

    /**
     * 教室编号
     */
    @NotBlank(message = "教室编号不能为空")
    @Size(max = 20, message = "教室编号长度不能超过20个字符")
    @Column(name = "classroom_no", nullable = false, unique = true, length = 20)
    private String classroomNo;

    /**
     * 教室名称
     */
    @NotBlank(message = "教室名称不能为空")
    @Size(max = 100, message = "教室名称长度不能超过100个字符")
    @Column(name = "classroom_name", nullable = false, length = 100)
    private String classroomName;

    /**
     * 所属建筑ID
     */
    @Column(name = "building_id")
    private Long buildingId;

    /**
     * 楼层
     */
    @Min(value = -3, message = "楼层不能小于-3")
    @Max(value = 50, message = "楼层不能大于50")
    @Column(name = "floor")
    private Integer floor;

    /**
     * 教室类型
     * 普通教室、实验室、多媒体教室、大阶梯教室、机房等
     */
    @NotBlank(message = "教室类型不能为空")
    @Size(max = 20, message = "教室类型长度不能超过20个字符")
    @Column(name = "classroom_type", nullable = false, length = 20)
    private String classroomType;

    /**
     * 容量（座位数）
     */
    @NotNull(message = "教室容量不能为空")
    @Min(value = 1, message = "教室容量不能小于1")
    @Max(value = 1000, message = "教室容量不能大于1000")
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    /**
     * 面积（平方米）
     */
    @DecimalMin(value = "10.0", message = "教室面积不能小于10平方米")
    @DecimalMax(value = "1000.0", message = "教室面积不能大于1000平方米")
    @Column(name = "area", precision = 6, scale = 2)
    private java.math.BigDecimal area;

    /**
     * 是否有投影设备
     */
    @Column(name = "has_projector")
    private Boolean hasProjector = false;

    /**
     * 是否有音响设备
     */
    @Column(name = "has_audio")
    private Boolean hasAudio = false;

    /**
     * 是否有空调
     */
    @Column(name = "has_air_conditioning")
    private Boolean hasAirConditioning = false;

    /**
     * 是否有网络
     */
    @Column(name = "has_network")
    private Boolean hasNetwork = false;

    /**
     * 是否有电脑
     */
    @Column(name = "has_computer")
    private Boolean hasComputer = false;

    /**
     * 设备描述
     */
    @Size(max = 500, message = "设备描述长度不能超过500个字符")
    @Column(name = "equipment_description", length = 500)
    private String equipmentDescription;

    /**
     * 适用课程类型
     * 理论课、实验课、体育课、艺术课等
     */
    @Size(max = 200, message = "适用课程类型长度不能超过200个字符")
    @Column(name = "suitable_course_types", length = 200)
    private String suitableCourseTypes;

    /**
     * 管理员ID
     */
    @Column(name = "administrator_id")
    private Long administratorId;

    /**
     * 位置描述
     */
    @Size(max = 200, message = "位置描述长度不能超过200个字符")
    @Column(name = "location_description", length = 200)
    private String locationDescription;

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
     * 建筑名称（暂时使用字符串）
     */
    @Column(name = "building_name", length = 50)
    private String buildingName;

    /**
     * 管理员
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrator_id", insertable = false, updatable = false)
    private User administrator;

    /**
     * 课程安排
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "classroom")
    private List<CourseSchedule> schedules;

    // ================================
    // 构造函数
    // ================================

    public Classroom() {
        super();
    }

    public Classroom(String classroomNo, String classroomName, String classroomType, Integer capacity) {
        this();
        this.classroomNo = classroomNo;
        this.classroomName = classroomName;
        this.classroomType = classroomType;
        this.capacity = capacity;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 获取建筑名称
     */
    public String getBuildingName() {
        return buildingName;
    }

    /**
     * 获取管理员姓名
     */
    public String getAdministratorName() {
        return administrator != null ? administrator.getRealName() : null;
    }

    /**
     * 检查是否适合某种课程类型
     */
    public boolean isSuitableForCourseType(String courseType) {
        if (suitableCourseTypes == null || suitableCourseTypes.trim().isEmpty()) {
            return true; // 如果没有限制，则适合所有类型
        }
        return suitableCourseTypes.contains(courseType);
    }

    /**
     * 检查容量是否满足需求
     */
    public boolean hasEnoughCapacity(Integer requiredCapacity) {
        if (requiredCapacity == null || capacity == null) {
            return true;
        }
        return capacity >= requiredCapacity;
    }

    /**
     * 获取教室类型文本
     */
    public String getClassroomTypeText() {
        if (classroomType == null) return "未知";
        return switch (classroomType) {
            case "normal" -> "普通教室";
            case "laboratory" -> "实验室";
            case "multimedia" -> "多媒体教室";
            case "lecture_hall" -> "大阶梯教室";
            case "computer_room" -> "机房";
            case "language_lab" -> "语音室";
            case "art_room" -> "美术室";
            case "music_room" -> "音乐室";
            case "gym" -> "体育馆";
            case "conference_room" -> "会议室";
            default -> classroomType;
        };
    }

    /**
     * 获取设备简介
     */
    public String getEquipmentSummary() {
        StringBuilder summary = new StringBuilder();
        if (Boolean.TRUE.equals(hasProjector)) summary.append("投影 ");
        if (Boolean.TRUE.equals(hasAudio)) summary.append("音响 ");
        if (Boolean.TRUE.equals(hasAirConditioning)) summary.append("空调 ");
        if (Boolean.TRUE.equals(hasNetwork)) summary.append("网络 ");
        if (Boolean.TRUE.equals(hasComputer)) summary.append("电脑 ");
        return summary.toString().trim();
    }

    /**
     * 检查是否有必要设备
     */
    public boolean hasRequiredEquipment(List<String> requiredEquipment) {
        if (requiredEquipment == null || requiredEquipment.isEmpty()) {
            return true;
        }
        
        for (String equipment : requiredEquipment) {
            switch (equipment.toLowerCase()) {
                case "projector":
                    if (!Boolean.TRUE.equals(hasProjector)) return false;
                    break;
                case "audio":
                    if (!Boolean.TRUE.equals(hasAudio)) return false;
                    break;
                case "computer":
                    if (!Boolean.TRUE.equals(hasComputer)) return false;
                    break;
                case "network":
                    if (!Boolean.TRUE.equals(hasNetwork)) return false;
                    break;
                case "air_conditioning":
                    if (!Boolean.TRUE.equals(hasAirConditioning)) return false;
                    break;
            }
        }
        return true;
    }

    /**
     * 获取完整的教室标识（建筑+教室）
     */
    public String getFullClassroomName() {
        String buildingName = getBuildingName();
        if (buildingName != null) {
            return buildingName + "-" + classroomName;
        }
        return classroomName;
    }

    // ================================
    // Getter/Setter 方法
    // ================================

    public String getClassroomNo() {
        return classroomNo;
    }

    public void setClassroomNo(String classroomNo) {
        this.classroomNo = classroomNo;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public String getClassroomType() {
        return classroomType;
    }

    public void setClassroomType(String classroomType) {
        this.classroomType = classroomType;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public java.math.BigDecimal getArea() {
        return area;
    }

    public void setArea(java.math.BigDecimal area) {
        this.area = area;
    }

    public Boolean getHasProjector() {
        return hasProjector;
    }

    public void setHasProjector(Boolean hasProjector) {
        this.hasProjector = hasProjector;
    }

    public Boolean getHasAudio() {
        return hasAudio;
    }

    public void setHasAudio(Boolean hasAudio) {
        this.hasAudio = hasAudio;
    }

    public Boolean getHasAirConditioning() {
        return hasAirConditioning;
    }

    public void setHasAirConditioning(Boolean hasAirConditioning) {
        this.hasAirConditioning = hasAirConditioning;
    }

    public Boolean getHasNetwork() {
        return hasNetwork;
    }

    public void setHasNetwork(Boolean hasNetwork) {
        this.hasNetwork = hasNetwork;
    }

    public Boolean getHasComputer() {
        return hasComputer;
    }

    public void setHasComputer(Boolean hasComputer) {
        this.hasComputer = hasComputer;
    }

    public String getEquipmentDescription() {
        return equipmentDescription;
    }

    public void setEquipmentDescription(String equipmentDescription) {
        this.equipmentDescription = equipmentDescription;
    }

    public String getSuitableCourseTypes() {
        return suitableCourseTypes;
    }

    public void setSuitableCourseTypes(String suitableCourseTypes) {
        this.suitableCourseTypes = suitableCourseTypes;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public Long getAdministratorId() {
        return administratorId;
    }

    public void setAdministratorId(Long administratorId) {
        this.administratorId = administratorId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public User getAdministrator() {
        return administrator;
    }

    public void setAdministrator(User administrator) {
        this.administrator = administrator;
    }

    public List<CourseSchedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<CourseSchedule> schedules) {
        this.schedules = schedules;
    }
}