package com.campus.shared.util;

import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 角色层级权限管理工具类
 * 基于50个角色的层级结构进行权限控制
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Component
public class RoleHierarchyUtil {

    /**
     * 角色层级映射 - 数字越小权限越高
     */
    private static final Map<String, Integer> ROLE_HIERARCHY = new HashMap<>();
    
    /**
     * 角色分组映射
     */
    private static final Map<String, Set<String>> ROLE_GROUPS = new HashMap<>();

    static {
        initializeRoleHierarchy();
        initializeRoleGroups();
    }

    /**
     * 初始化角色层级
     */
    private static void initializeRoleHierarchy() {
        // 核心管理角色 (1-10) - 最高权限
        ROLE_HIERARCHY.put("ROLE_SUPER_ADMIN", 1);
        ROLE_HIERARCHY.put("ROLE_ADMIN", 1);
        ROLE_HIERARCHY.put("ROLE_PRINCIPAL", 2);
        ROLE_HIERARCHY.put("ROLE_VICE_PRINCIPAL", 3);
        ROLE_HIERARCHY.put("ROLE_ACADEMIC_DIRECTOR", 4);
        ROLE_HIERARCHY.put("ROLE_STUDENT_AFFAIRS_DIRECTOR", 4);
        ROLE_HIERARCHY.put("ROLE_HR_DIRECTOR", 4);
        ROLE_HIERARCHY.put("ROLE_FINANCE_DIRECTOR", 4);
        ROLE_HIERARCHY.put("ROLE_LOGISTICS_DIRECTOR", 4);
        ROLE_HIERARCHY.put("ROLE_IT_DIRECTOR", 4);

        // 学院管理角色 (11-20)
        ROLE_HIERARCHY.put("ROLE_DEAN", 5);
        ROLE_HIERARCHY.put("ROLE_VICE_DEAN", 6);
        ROLE_HIERARCHY.put("ROLE_DEPARTMENT_HEAD", 7);
        ROLE_HIERARCHY.put("ROLE_VICE_DEPARTMENT_HEAD", 8);
        ROLE_HIERARCHY.put("ROLE_TEACHING_GROUP_HEAD", 9);
        ROLE_HIERARCHY.put("ROLE_LAB_DIRECTOR", 9);
        ROLE_HIERARCHY.put("ROLE_LIBRARY_DIRECTOR", 5);
        ROLE_HIERARCHY.put("ROLE_SPORTS_DIRECTOR", 6);
        ROLE_HIERARCHY.put("ROLE_INTERNATIONAL_DIRECTOR", 4);
        ROLE_HIERARCHY.put("ROLE_ADMISSION_DIRECTOR", 5);

        // 教学角色 (21-30)
        ROLE_HIERARCHY.put("ROLE_TEACHER", 10);
        ROLE_HIERARCHY.put("ROLE_PROFESSOR", 10);
        ROLE_HIERARCHY.put("ROLE_ASSOCIATE_PROFESSOR", 11);
        ROLE_HIERARCHY.put("ROLE_LECTURER", 12);
        ROLE_HIERARCHY.put("ROLE_ASSISTANT", 13);
        ROLE_HIERARCHY.put("ROLE_CLASS_TEACHER", 15);
        ROLE_HIERARCHY.put("ROLE_COUNSELOR", 14);
        ROLE_HIERARCHY.put("ROLE_SUPERVISOR", 10);
        ROLE_HIERARCHY.put("ROLE_LAB_TECHNICIAN", 16);
        ROLE_HIERARCHY.put("ROLE_VISITING_TEACHER", 17);

        // 学生角色 (31-40)
        ROLE_HIERARCHY.put("ROLE_STUDENT", 20);
        ROLE_HIERARCHY.put("ROLE_UNDERGRADUATE", 20);
        ROLE_HIERARCHY.put("ROLE_GRADUATE", 19);
        ROLE_HIERARCHY.put("ROLE_MASTER_STUDENT", 19);
        ROLE_HIERARCHY.put("ROLE_PHD_STUDENT", 18);
        ROLE_HIERARCHY.put("ROLE_INTERNATIONAL_STUDENT", 21);
        ROLE_HIERARCHY.put("ROLE_EXCHANGE_STUDENT", 22);
        ROLE_HIERARCHY.put("ROLE_AUDITOR", 25);
        ROLE_HIERARCHY.put("ROLE_STUDENT_LEADER", 18);
        ROLE_HIERARCHY.put("ROLE_CLASS_MONITOR", 19);

        // 行政支持角色 (41-50)
        ROLE_HIERARCHY.put("ROLE_PARENT", 30);
        ROLE_HIERARCHY.put("ROLE_FINANCE_STAFF", 16);
        ROLE_HIERARCHY.put("ROLE_HR_STAFF", 16);
        ROLE_HIERARCHY.put("ROLE_ACADEMIC_STAFF", 16);
        ROLE_HIERARCHY.put("ROLE_STUDENT_AFFAIRS_STAFF", 16);
        ROLE_HIERARCHY.put("ROLE_LOGISTICS_STAFF", 18);
        ROLE_HIERARCHY.put("ROLE_SECURITY", 20);
        ROLE_HIERARCHY.put("ROLE_CLEANER", 22);
        ROLE_HIERARCHY.put("ROLE_MAINTENANCE", 20);
        ROLE_HIERARCHY.put("ROLE_VISITOR", 50);
    }

    /**
     * 初始化角色分组
     */
    private static void initializeRoleGroups() {
        // 系统管理组
        ROLE_GROUPS.put("SYSTEM_ADMIN", Set.of(
            "ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_PRINCIPAL", "ROLE_VICE_PRINCIPAL"
        ));

        // 高级管理组
        ROLE_GROUPS.put("SENIOR_MANAGEMENT", Set.of(
            "ROLE_ACADEMIC_DIRECTOR", "ROLE_STUDENT_AFFAIRS_DIRECTOR", "ROLE_HR_DIRECTOR",
            "ROLE_FINANCE_DIRECTOR", "ROLE_LOGISTICS_DIRECTOR", "ROLE_IT_DIRECTOR",
            "ROLE_INTERNATIONAL_DIRECTOR", "ROLE_ADMISSION_DIRECTOR"
        ));

        // 学院管理组
        ROLE_GROUPS.put("COLLEGE_MANAGEMENT", Set.of(
            "ROLE_DEAN", "ROLE_VICE_DEAN", "ROLE_DEPARTMENT_HEAD", "ROLE_VICE_DEPARTMENT_HEAD",
            "ROLE_TEACHING_GROUP_HEAD", "ROLE_LAB_DIRECTOR", "ROLE_LIBRARY_DIRECTOR", "ROLE_SPORTS_DIRECTOR"
        ));

        // 教学人员组
        ROLE_GROUPS.put("TEACHING_STAFF", Set.of(
            "ROLE_TEACHER", "ROLE_PROFESSOR", "ROLE_ASSOCIATE_PROFESSOR", "ROLE_LECTURER",
            "ROLE_ASSISTANT", "ROLE_SUPERVISOR", "ROLE_VISITING_TEACHER"
        ));

        // 学生工作组
        ROLE_GROUPS.put("STUDENT_AFFAIRS", Set.of(
            "ROLE_CLASS_TEACHER", "ROLE_COUNSELOR", "ROLE_STUDENT_AFFAIRS_STAFF"
        ));

        // 学生组
        ROLE_GROUPS.put("STUDENTS", Set.of(
            "ROLE_STUDENT", "ROLE_UNDERGRADUATE", "ROLE_GRADUATE", "ROLE_MASTER_STUDENT",
            "ROLE_PHD_STUDENT", "ROLE_INTERNATIONAL_STUDENT", "ROLE_EXCHANGE_STUDENT",
            "ROLE_AUDITOR", "ROLE_STUDENT_LEADER", "ROLE_CLASS_MONITOR"
        ));

        // 行政支持组
        ROLE_GROUPS.put("ADMINISTRATIVE_STAFF", Set.of(
            "ROLE_HR_STAFF", "ROLE_ACADEMIC_STAFF", "ROLE_FINANCE_STAFF", "ROLE_STUDENT_AFFAIRS_STAFF",
            "ROLE_LOGISTICS_STAFF", "ROLE_LAB_TECHNICIAN"
        ));

        // 外部用户组
        ROLE_GROUPS.put("EXTERNAL_USERS", Set.of(
            "ROLE_PARENT", "ROLE_VISITOR"
        ));

        // 服务人员组
        ROLE_GROUPS.put("SERVICE_STAFF", Set.of(
            "ROLE_SECURITY", "ROLE_CLEANER", "ROLE_MAINTENANCE"
        ));
    }

    /**
     * 检查角色是否属于指定组
     */
    public boolean isRoleInGroup(String roleKey, String groupName) {
        Set<String> groupRoles = ROLE_GROUPS.get(groupName);
        return groupRoles != null && groupRoles.contains(roleKey);
    }

    /**
     * 检查用户是否具有指定组的任一角色
     */
    public boolean hasAnyRoleInGroup(Set<String> userRoles, String groupName) {
        Set<String> groupRoles = ROLE_GROUPS.get(groupName);
        if (groupRoles == null) return false;
        
        return userRoles.stream().anyMatch(groupRoles::contains);
    }

    /**
     * 获取角色层级
     */
    public int getRoleLevel(String roleKey) {
        return ROLE_HIERARCHY.getOrDefault(roleKey, 999);
    }

    /**
     * 检查角色A是否比角色B权限更高（层级数字更小）
     */
    public boolean isHigherRole(String roleA, String roleB) {
        return getRoleLevel(roleA) < getRoleLevel(roleB);
    }

    /**
     * 检查用户是否具有足够的权限级别
     */
    public boolean hasRequiredLevel(Set<String> userRoles, int requiredLevel) {
        return userRoles.stream()
                .mapToInt(this::getRoleLevel)
                .min()
                .orElse(999) <= requiredLevel;
    }

    /**
     * 获取用户的最高权限级别
     */
    public int getHighestLevel(Set<String> userRoles) {
        return userRoles.stream()
                .mapToInt(this::getRoleLevel)
                .min()
                .orElse(999);
    }

    /**
     * 生成权限检查表达式
     */
    public String generateRoleExpression(String... groups) {
        Set<String> allRoles = new HashSet<>();
        for (String group : groups) {
            Set<String> groupRoles = ROLE_GROUPS.get(group);
            if (groupRoles != null) {
                allRoles.addAll(groupRoles);
            }
        }
        
        if (allRoles.isEmpty()) {
            return "hasRole('ROLE_VISITOR')";
        }
        
        return "hasAnyRole('" + String.join("', '", allRoles) + "')";
    }

    /**
     * 获取所有角色组
     */
    public Set<String> getAllGroups() {
        return ROLE_GROUPS.keySet();
    }

    /**
     * 获取指定组的所有角色
     */
    public Set<String> getRolesInGroup(String groupName) {
        return ROLE_GROUPS.getOrDefault(groupName, new HashSet<>());
    }
}
