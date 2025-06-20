package com.campus.domain.entity.organization;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * 教师实体
 *
 * @author Campus Management System
 * @since 2025-06-20
 */
@Data
@Entity
@Table(name = "tb_teacher")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 工号
     */
    @Column(name = "employee_no", unique = true, nullable = false, length = 20)
    private String employeeNo;

    /**
     * 姓名
     */
    @Column(name = "real_name", nullable = false, length = 50)
    private String realName;

    /**
     * 性别
     */
    @Column(name = "gender", length = 10)
    private String gender;

    /**
     * 身份证号
     */
    @Column(name = "id_card", length = 18)
    private String idCard;

    /**
     * 手机号
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 邮箱
     */
    @Column(name = "email", length = 100)
    private String email;

    /**
     * 部门
     */
    @Column(name = "department", length = 100)
    private String department;

    /**
     * 职位
     */
    @Column(name = "position", length = 50)
    private String position;

    /**
     * 职称
     */
    @Column(name = "title", length = 50)
    private String title;

    /**
     * 入职日期
     */
    @Column(name = "hire_date")
    private LocalDate hireDate;

    /**
     * 教师状态
     */
    @Column(name = "teacher_status", length = 20)
    private String teacherStatus;

    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;
}
