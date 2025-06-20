package com.campus.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 学生数据传输对象
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentDTO {

    /**
     * 学生ID
     */
    private Long id;

    /**
     * 学号
     */
    private String studentNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别
     */
    private String gender;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 年级
     */
    private String grade;

    /**
     * 专业
     */
    private String major;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 入学年份
     */
    private Integer enrollmentYear;

    /**
     * 学生状态 (1-正常, 0-禁用, 2-休学, 3-退学, 4-毕业)
     */
    private Integer status;

    /**
     * 学生状态文本
     */
    @SuppressWarnings("unused")
    private String statusText;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 更新者
     */
    private String updatedBy;

    // ================================
    // 扩展字段（用于特定场景）
    // ================================

    /**
     * 用户名
     */
    private String username;

    /**
     * 家庭住址
     */
    private String address;

    /**
     * 紧急联系人
     */
    private String emergencyContact;

    /**
     * 紧急联系人电话
     */
    private String emergencyPhone;

    /**
     * 家长姓名
     */
    private String parentName;

    /**
     * 家长电话
     */
    private String parentPhone;

    /**
     * 政治面貌
     */
    private String politicalStatus;

    /**
     * 民族
     */
    private String ethnicity;

    /**
     * 出生日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime birthDate;

    /**
     * 籍贯
     */
    private String nativePlace;

    /**
     * 宿舍号
     */
    private String dormitoryNo;

    /**
     * 床位号
     */
    private String bedNo;

    /**
     * 银行卡号
     */
    private String bankCardNo;

    /**
     * 开户银行
     */
    private String bankName;

    // ================================
    // 统计字段（用于展示）
    // ================================

    /**
     * 选课数量
     */
    private Integer courseCount;

    /**
     * 平均成绩
     */
    private Double averageGrade;

    /**
     * 学分总数
     */
    private Double totalCredits;

    /**
     * 已获得学分
     */
    private Double earnedCredits;

    /**
     * 缴费状态
     */
    private String paymentStatus;

    /**
     * 欠费金额
     */
    private Double unpaidAmount;

    // ================================
    // 业务方法
    // ================================

    /**
     * 获取学生状态文本
     */
    public String getStatusText() {
        if (status == null) return "未知";
        return switch (status) {
            case 1 -> "正常";
            case 0 -> "禁用";
            case 2 -> "休学";
            case 3 -> "退学";
            case 4 -> "毕业";
            default -> "未知";
        };
    }

    /**
     * 检查是否为正常状态
     */
    public boolean isActive() {
        return status != null && status == 1;
    }

    /**
     * 检查是否可以选课
     */
    public boolean canSelectCourse() {
        return isActive();
    }

    /**
     * 检查是否可以参加考试
     */
    public boolean canTakeExam() {
        return isActive();
    }

    /**
     * 获取完整姓名（用于显示）
     */
    public String getDisplayName() {
        if (realName != null && !realName.trim().isEmpty()) {
            return realName;
        }
        return username != null ? username : studentNo;
    }

    /**
     * 获取学生标识（学号 + 姓名）
     */
    public String getStudentIdentity() {
        String name = getDisplayName();
        if (studentNo != null && name != null) {
            return studentNo + " - " + name;
        }
        return studentNo != null ? studentNo : name;
    }

    /**
     * 获取班级信息（年级 + 专业 + 班级）
     */
    public String getClassInfo() {
        StringBuilder info = new StringBuilder();
        if (grade != null) {
            info.append(grade);
        }
        if (major != null) {
            if (info.length() > 0) info.append(" ");
            info.append(major);
        }
        if (className != null) {
            if (info.length() > 0) info.append(" ");
            info.append(className);
        }
        return info.toString();
    }

    /**
     * 获取联系方式（手机号或邮箱）
     */
    public String getContactInfo() {
        if (phone != null && !phone.trim().isEmpty()) {
            return phone;
        }
        return email;
    }

    /**
     * 检查信息是否完整
     */
    public boolean isProfileComplete() {
        return studentNo != null && realName != null && 
               phone != null && email != null && 
               grade != null && major != null;
    }

    /**
     * 获取缺失的必填字段
     */
    public String getMissingRequiredFields() {
        StringBuilder missing = new StringBuilder();
        if (studentNo == null) missing.append("学号 ");
        if (realName == null) missing.append("姓名 ");
        if (phone == null) missing.append("手机号 ");
        if (email == null) missing.append("邮箱 ");
        if (grade == null) missing.append("年级 ");
        if (major == null) missing.append("专业 ");
        return missing.toString().trim();
    }
}
