package com.campus.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 选课时间段实体类
 * 管理选课开放期、分批次选课等时间控制
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_course_selection_period", indexes = {
    @Index(name = "idx_semester_year", columnList = "semester,academic_year"),
    @Index(name = "idx_selection_type", columnList = "selection_type"),
    @Index(name = "idx_start_end_time", columnList = "start_time,end_time")
})
public class CourseSelectionPeriod extends BaseEntity {

    /**
     * 选课阶段名称
     */
    @NotBlank(message = "选课阶段名称不能为空")
    @Size(max = 100, message = "选课阶段名称长度不能超过100个字符")
    @Column(name = "period_name", nullable = false, length = 100)
    private String periodName;

    /**
     * 学期
     */
    @NotBlank(message = "学期不能为空")
    @Size(max = 20, message = "学期长度不能超过20个字符")
    @Column(name = "semester", nullable = false, length = 20)
    private String semester;

    /**
     * 学年
     */
    @NotNull(message = "学年不能为空")
    @Column(name = "academic_year", nullable = false)
    private Integer academicYear;

    /**
     * 选课类型 (pre_selection/main_selection/supplementary_selection)
     */
    @NotBlank(message = "选课类型不能为空")
    @Size(max = 30, message = "选课类型长度不能超过30个字符")
    @Column(name = "selection_type", nullable = false, length = 30)
    private String selectionType;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * 优先级 (数字越小优先级越高)
     */
    @Column(name = "priority", nullable = false)
    private Integer priority = 0;

    /**
     * 适用年级 (JSON格式存储年级列表)
     */
    @Size(max = 200, message = "适用年级长度不能超过200个字符")
    @Column(name = "applicable_grades", length = 200)
    private String applicableGrades;

    /**
     * 适用专业 (JSON格式存储专业列表)
     */
    @Size(max = 500, message = "适用专业长度不能超过500个字符")
    @Column(name = "applicable_majors", length = 500)
    private String applicableMajors;

    /**
     * 最大选课学分
     */
    @Column(name = "max_credits")
    private Integer maxCredits;

    /**
     * 最小选课学分
     */
    @Column(name = "min_credits")
    private Integer minCredits;

    /**
     * 是否允许退课
     */
    @Column(name = "allow_drop", nullable = false)
    private Boolean allowDrop = true;

    /**
     * 退课截止时间
     */
    @Column(name = "drop_deadline")
    private LocalDateTime dropDeadline;

    /**
     * 选课说明
     */
    @Size(max = 1000, message = "选课说明长度不能超过1000个字符")
    @Column(name = "description", length = 1000)
    private String description;

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
     * 选课记录
     */
    @OneToMany(mappedBy = "selectionPeriod", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CourseSelection> courseSelections;

    // ================================
    // 构造函数
    // ================================

    public CourseSelectionPeriod() {
        super();
    }

    public CourseSelectionPeriod(String periodName, String semester, Integer academicYear, 
                                String selectionType, LocalDateTime startTime, LocalDateTime endTime) {
        this();
        this.periodName = periodName;
        this.semester = semester;
        this.academicYear = academicYear;
        this.selectionType = selectionType;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 获取选课类型文本
     */
    public String getSelectionTypeText() {
        if (selectionType == null) return "未知";
        return switch (selectionType) {
            case "pre_selection" -> "预选";
            case "main_selection" -> "正选";
            case "supplementary_selection" -> "补选";
            default -> selectionType;
        };
    }

    /**
     * 检查当前是否在选课时间内
     */
    public boolean isSelectionOpen() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startTime) && now.isBefore(endTime) && isEnabled();
    }

    /**
     * 检查是否可以退课
     */
    public boolean canDropCourse() {
        if (!allowDrop) return false;
        if (dropDeadline == null) return true;
        return LocalDateTime.now().isBefore(dropDeadline);
    }

    /**
     * 检查年级是否适用
     */
    public boolean isGradeApplicable(String grade) {
        if (applicableGrades == null || applicableGrades.trim().isEmpty()) {
            return true; // 如果没有限制，则所有年级都适用
        }
        return applicableGrades.contains(grade);
    }

    /**
     * 检查专业是否适用
     */
    public boolean isMajorApplicable(String major) {
        if (applicableMajors == null || applicableMajors.trim().isEmpty()) {
            return true; // 如果没有限制，则所有专业都适用
        }
        return applicableMajors.contains(major);
    }

    /**
     * 获取剩余时间（分钟）
     */
    public long getRemainingMinutes() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime)) {
            return java.time.Duration.between(now, startTime).toMinutes();
        } else if (now.isBefore(endTime)) {
            return java.time.Duration.between(now, endTime).toMinutes();
        }
        return 0;
    }

    /**
     * 获取时间状态描述
     */
    public String getTimeStatusDescription() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime)) {
            long hours = java.time.Duration.between(now, startTime).toHours();
            return hours > 0 ? hours + "小时后开始" : "即将开始";
        } else if (now.isBefore(endTime)) {
            long hours = java.time.Duration.between(now, endTime).toHours();
            return hours > 0 ? hours + "小时后结束" : "即将结束";
        }
        return "已结束";
    }

    /**
     * 验证选课时间段数据
     */
    @PrePersist
    @PreUpdate
    public void validatePeriod() {
        if (endTime != null && startTime != null && endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("结束时间不能早于开始时间");
        }
        
        if (dropDeadline != null && startTime != null && dropDeadline.isBefore(startTime)) {
            throw new IllegalArgumentException("退课截止时间不能早于选课开始时间");
        }
        
        if (maxCredits != null && minCredits != null && maxCredits < minCredits) {
            throw new IllegalArgumentException("最大学分不能小于最小学分");
        }
    }

    // ================================
    // Getter and Setter methods
    // ================================

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Integer getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(Integer academicYear) {
        this.academicYear = academicYear;
    }

    public String getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(String selectionType) {
        this.selectionType = selectionType;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getApplicableGrades() {
        return applicableGrades;
    }

    public void setApplicableGrades(String applicableGrades) {
        this.applicableGrades = applicableGrades;
    }

    public String getApplicableMajors() {
        return applicableMajors;
    }

    public void setApplicableMajors(String applicableMajors) {
        this.applicableMajors = applicableMajors;
    }

    public Integer getMaxCredits() {
        return maxCredits;
    }

    public void setMaxCredits(Integer maxCredits) {
        this.maxCredits = maxCredits;
    }

    public Integer getMinCredits() {
        return minCredits;
    }

    public void setMinCredits(Integer minCredits) {
        this.minCredits = minCredits;
    }

    public Boolean getAllowDrop() {
        return allowDrop;
    }

    public void setAllowDrop(Boolean allowDrop) {
        this.allowDrop = allowDrop;
    }

    public LocalDateTime getDropDeadline() {
        return dropDeadline;
    }

    public void setDropDeadline(LocalDateTime dropDeadline) {
        this.dropDeadline = dropDeadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public List<CourseSelection> getCourseSelections() {
        return courseSelections;
    }

    public void setCourseSelections(List<CourseSelection> courseSelections) {
        this.courseSelections = courseSelections;
    }
}
