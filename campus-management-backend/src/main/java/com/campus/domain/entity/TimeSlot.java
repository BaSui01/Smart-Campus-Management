package com.campus.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;


import java.time.LocalTime;
import java.util.List;

/**
 * 时间段实体类
 * 管理课程时间安排的基础时间段
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_time_slot", indexes = {
    @Index(name = "idx_slot_name", columnList = "slot_name"),
    @Index(name = "idx_day_of_week", columnList = "day_of_week"),
    @Index(name = "idx_start_time", columnList = "start_time"),
    @Index(name = "idx_status_deleted", columnList = "status,deleted")
})
public class TimeSlot extends BaseEntity {

    /**
     * 时间段名称
     */
    @NotBlank(message = "时间段名称不能为空")
    @Size(max = 50, message = "时间段名称长度不能超过50个字符")
    @Column(name = "slot_name", nullable = false, length = 50)
    private String slotName;

    /**
     * 星期几 (1-7，1表示周一，7表示周日)
     */
    @NotNull(message = "星期几不能为空")
    @Min(value = 1, message = "星期几必须在1-7之间")
    @Max(value = 7, message = "星期几必须在1-7之间")
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    /**
     * 第几节课 (1-12，表示一天中的第几节课)
     */
    @NotNull(message = "节次不能为空")
    @Min(value = 1, message = "节次必须在1-12之间")
    @Max(value = 12, message = "节次必须在1-12之间")
    @Column(name = "period_number", nullable = false)
    private Integer periodNumber;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /**
     * 时间段类型
     * morning（上午）, afternoon（下午）, evening（晚上）
     */
    @NotBlank(message = "时间段类型不能为空")
    @Size(max = 20, message = "时间段类型长度不能超过20个字符")
    @Column(name = "slot_type", nullable = false, length = 20)
    private String slotType;

    /**
     * 持续时间（分钟）
     */
    @Min(value = 30, message = "持续时间不能少于30分钟")
    @Max(value = 180, message = "持续时间不能超过180分钟")
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    /**
     * 是否为连续时间段
     */
    @Column(name = "is_continuous")
    private Boolean isContinuous = false;

    /**
     * 排序序号
     */
    @Column(name = "sort_order")
    private Integer sortOrder;

    /**
     * 描述
     */
    @Size(max = 200, message = "描述长度不能超过200个字符")
    @Column(name = "description", length = 200)
    private String description;

    // ================================
    // 关联关系
    // ================================

    /**
     * 课程安排
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "timeSlot")
    private List<CourseSchedule> schedules;

    // ================================
    // 构造函数
    // ================================

    public TimeSlot() {
        super();
    }

    public TimeSlot(String slotName, Integer dayOfWeek, Integer periodNumber, LocalTime startTime, LocalTime endTime) {
        this();
        this.slotName = slotName;
        this.dayOfWeek = dayOfWeek;
        this.periodNumber = periodNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = calculateDuration();
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 计算持续时间
     */
    public Integer calculateDuration() {
        if (startTime != null && endTime != null) {
            return (int) java.time.Duration.between(startTime, endTime).toMinutes();
        }
        return null;
    }

    /**
     * 获取星期几的中文名称
     */
    public String getDayOfWeekText() {
        if (dayOfWeek == null) return "未知";
        return switch (dayOfWeek) {
            case 1 -> "周一";
            case 2 -> "周二";
            case 3 -> "周三";
            case 4 -> "周四";
            case 5 -> "周五";
            case 6 -> "周六";
            case 7 -> "周日";
            default -> "未知";
        };
    }

    /**
     * 获取时间段类型文本
     */
    public String getSlotTypeText() {
        if (slotType == null) return "未知";
        return switch (slotType) {
            case "morning" -> "上午";
            case "afternoon" -> "下午";
            case "evening" -> "晚上";
            default -> slotType;
        };
    }

    /**
     * 获取完整的时间描述
     */
    public String getFullTimeDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(getDayOfWeekText());
        sb.append(" ");
        sb.append(getSlotTypeText());
        sb.append(" ");
        sb.append("第").append(periodNumber).append("节");
        if (startTime != null && endTime != null) {
            sb.append(" (").append(startTime).append("-").append(endTime).append(")");
        }
        return sb.toString();
    }

    /**
     * 检查是否与另一个时间段冲突
     */
    public boolean conflictsWith(TimeSlot other) {
        if (other == null || !this.dayOfWeek.equals(other.dayOfWeek)) {
            return false;
        }

        // 检查时间重叠
        if (this.startTime != null && this.endTime != null && 
            other.startTime != null && other.endTime != null) {
            return !(this.endTime.isBefore(other.startTime) || this.startTime.isAfter(other.endTime));
        }

        // 如果时间信息不完整，按节次判断
        return this.periodNumber.equals(other.periodNumber);
    }

    /**
     * 检查是否在指定时间范围内
     */
    public boolean isWithinTimeRange(LocalTime rangeStart, LocalTime rangeEnd) {
        if (startTime == null || endTime == null || rangeStart == null || rangeEnd == null) {
            return false;
        }
        return !startTime.isBefore(rangeStart) && !endTime.isAfter(rangeEnd);
    }

    /**
     * 获取时间段的优先级（用于排序）
     */
    public Integer getPriority() {
        if (sortOrder != null) {
            return sortOrder;
        }
        // 默认按星期和节次计算优先级
        return dayOfWeek * 100 + periodNumber;
    }

    /**
     * 检查是否为上午时间段
     */
    public boolean isMorning() {
        return "morning".equals(slotType) || 
               (startTime != null && startTime.isBefore(LocalTime.of(12, 0)));
    }

    /**
     * 检查是否为下午时间段
     */
    public boolean isAfternoon() {
        return "afternoon".equals(slotType) || 
               (startTime != null && startTime.isAfter(LocalTime.of(12, 0)) && startTime.isBefore(LocalTime.of(18, 0)));
    }

    /**
     * 检查是否为晚上时间段
     */
    public boolean isEvening() {
        return "evening".equals(slotType) || 
               (startTime != null && startTime.isAfter(LocalTime.of(18, 0)));
    }

    /**
     * 生成唯一标识符
     */
    public String getUniqueKey() {
        return String.format("D%d_P%d_%s_%s", 
            dayOfWeek, periodNumber, 
            startTime != null ? startTime.toString() : "null",
            endTime != null ? endTime.toString() : "null");
    }

    // ================================
    // 验证方法
    // ================================

    /**
     * 验证时间段设置是否合理
     */
    @PrePersist
    @PreUpdate
    public void validateTimeSlot() {
        if (startTime != null && endTime != null) {
            if (!startTime.isBefore(endTime)) {
                throw new IllegalArgumentException("开始时间必须早于结束时间");
            }
            
            // 自动计算持续时间
            this.durationMinutes = calculateDuration();
            
            // 自动设置时间段类型
            if (slotType == null) {
                if (isMorning()) {
                    this.slotType = "morning";
                } else if (isAfternoon()) {
                    this.slotType = "afternoon";
                } else {
                    this.slotType = "evening";
                }
            }
        }
    }

    // ================================
    // Getter/Setter 方法
    // ================================

    public String getSlotName() {
        return slotName;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getPeriodNumber() {
        return periodNumber;
    }

    public void setPeriodNumber(Integer periodNumber) {
        this.periodNumber = periodNumber;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getSlotType() {
        return slotType;
    }

    public void setSlotType(String slotType) {
        this.slotType = slotType;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Boolean getIsContinuous() {
        return isContinuous;
    }

    public void setIsContinuous(Boolean isContinuous) {
        this.isContinuous = isContinuous;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CourseSchedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<CourseSchedule> schedules) {
        this.schedules = schedules;
    }
}