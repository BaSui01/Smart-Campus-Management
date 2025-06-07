package com.campus.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


import java.util.List;

/**
 * 院系实体类
 * 管理学校的各个院系、学部信息
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Entity
@Table(name = "tb_department", indexes = {
    @Index(name = "idx_dept_code", columnList = "dept_code"),
    @Index(name = "idx_parent_id", columnList = "parent_id"),
    @Index(name = "idx_status_deleted", columnList = "status,deleted")
})
public class Department extends BaseEntity {

    /**
     * 院系名称
     */
    @NotBlank(message = "院系名称不能为空")
    @Size(max = 100, message = "院系名称长度不能超过100个字符")
    @Column(name = "dept_name", nullable = false, length = 100)
    private String deptName;

    /**
     * 院系代码
     */
    @NotBlank(message = "院系代码不能为空")
    @Size(max = 20, message = "院系代码长度不能超过20个字符")
    @Column(name = "dept_code", nullable = false, unique = true, length = 20)
    private String deptCode;

    /**
     * 上级院系ID（支持多级院系结构）
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 院系类型
     * 学院、系、部门等
     */
    @Size(max = 20, message = "院系类型长度不能超过20个字符")
    @Column(name = "dept_type", length = 20)
    private String deptType;

    /**
     * 院系级别
     * 1-学院级, 2-系级, 3-专业级
     */
    @Column(name = "dept_level")
    private Integer deptLevel;

    /**
     * 排序序号
     */
    @Column(name = "sort_order")
    private Integer sortOrder;

    /**
     * 院系负责人ID
     */
    @Column(name = "leader_id")
    private Long leaderId;

    /**
     * 联系电话
     */
    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 邮箱
     */
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    @Column(name = "email", length = 100)
    private String email;

    /**
     * 办公地址
     */
    @Size(max = 200, message = "办公地址长度不能超过200个字符")
    @Column(name = "address", length = 200)
    private String address;

    /**
     * 院系描述
     */
    @Size(max = 1000, message = "院系描述长度不能超过1000个字符")
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
     * 上级院系
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private Department parent;

    /**
     * 下级院系
     */
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Department> children;

    /**
     * 院系负责人
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", insertable = false, updatable = false)
    private User leader;

    /**
     * 院系下的课程
     */
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<Course> courses;

    /**
     * 院系下的班级
     */
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<SchoolClass> schoolClasses;

    // ================================
    // 构造函数
    // ================================

    public Department() {
        super();
    }

    public Department(String deptName, String deptCode) {
        this();
        this.deptName = deptName;
        this.deptCode = deptCode;
    }

    public Department(String deptName, String deptCode, String deptType, Integer deptLevel) {
        this(deptName, deptCode);
        this.deptType = deptType;
        this.deptLevel = deptLevel;
    }

    // ================================
    // 业务方法
    // ================================

    /**
     * 是否为顶级院系
     */
    public boolean isTopLevel() {
        return parentId == null;
    }

    /**
     * 是否有子院系
     */
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    /**
     * 获取院系全名（包含上级院系）
     */
    public String getFullName() {
        if (parent == null) {
            return deptName;
        }
        try {
            return parent.getFullName() + " - " + deptName;
        } catch (Exception e) {
            return deptName;
        }
    }

    /**
     * 获取负责人姓名
     */
    public String getLeaderName() {
        try {
            return leader != null ? leader.getRealName() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取院系类型文本
     */
    public String getDeptTypeText() {
        if (deptType == null) return "未知";
        return switch (deptType) {
            case "college" -> "学院";
            case "department" -> "系";
            case "faculty" -> "学部";
            case "center" -> "中心";
            case "institute" -> "研究院";
            default -> deptType;
        };
    }

    /**
     * 获取院系级别文本
     */
    public String getDeptLevelText() {
        if (deptLevel == null) return "未知";
        return switch (deptLevel) {
            case 1 -> "学院级";
            case 2 -> "系级";
            case 3 -> "专业级";
            default -> "其他";
        };
    }

    // ================================
    // Getter/Setter 方法 (手动添加以解决Lombok问题)
    // ================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }



    public String getDeptType() {
        return deptType;
    }

    public void setDeptType(String deptType) {
        this.deptType = deptType;
    }

    public Integer getDeptLevel() {
        return deptLevel;
    }

    public void setDeptLevel(Integer deptLevel) {
        this.deptLevel = deptLevel;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Long leaderId) {
        this.leaderId = leaderId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Department getParent() {
        return parent;
    }

    public void setParent(Department parent) {
        this.parent = parent;
    }

    public User getLeader() {
        return leader;
    }

    public void setLeader(User leader) {
        this.leader = leader;
    }

    public java.util.List<Department> getChildren() {
        return children;
    }

    public void setChildren(java.util.List<Department> children) {
        this.children = children;
    }

    public java.util.List<SchoolClass> getSchoolClasses() {
        return schoolClasses;
    }

    public void setSchoolClasses(java.util.List<SchoolClass> schoolClasses) {
        this.schoolClasses = schoolClasses;
    }

    public java.util.List<Course> getCourses() {
        return courses;
    }

    public void setCourses(java.util.List<Course> courses) {
        this.courses = courses;
    }
}