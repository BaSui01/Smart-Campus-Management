package com.campus.shared.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.campus.application.service.auth.UserService;
import com.campus.domain.entity.auth.User;

import jakarta.servlet.http.HttpSession;

/**
 * 权限工具类
 * 用于在模板中进行权限判断
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-04
 */
@Component("permissionUtil")
public class PermissionUtil {

    @Autowired
    private UserService userService;

    /**
     * 检查当前用户是否有指定角色
     */
    public boolean hasRole(String roleName) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        // admin用户拥有所有角色权限
        if ("admin".equals(currentUser.getUsername())) {
            return true;
        }
        return userService.hasRole(currentUser.getId(), roleName);
    }

    /**
     * 检查当前用户是否有任意一个指定角色
     */
    public boolean hasAnyRole(String... roleNames) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        for (String roleName : roleNames) {
            if (userService.hasRole(currentUser.getId(), roleName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前用户是否有菜单权限
     */
    public boolean hasMenuPermission(String menuPath) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        // admin用户拥有所有菜单权限
        if ("admin".equals(currentUser.getUsername())) {
            return true;
        }
        return userService.hasMenuPermission(currentUser.getId(), menuPath);
    }

    /**
     * 获取当前用户的所有角色
     */
    public List<String> getCurrentUserRoles() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }
        return userService.getUserRoles(currentUser.getId());
    }

    /**
     * 检查是否为系统管理员（最高权限层级）
     */
    public boolean isSystemAdmin() {
        User currentUser = getCurrentUser();
        if (currentUser != null && "admin".equals(currentUser.getUsername())) {
            return true;
        }
        return hasAnyRole("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_PRINCIPAL", "ROLE_VICE_PRINCIPAL");
    }

    /**
     * 检查是否为教务管理员
     */
    public boolean isAcademicAdmin() {
        return hasAnyRole("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_PRINCIPAL", "ROLE_VICE_PRINCIPAL",
                         "ROLE_ACADEMIC_DIRECTOR");
    }

    /**
     * 检查是否为财务管理员
     */
    public boolean isFinanceAdmin() {
        return hasAnyRole("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_PRINCIPAL", "ROLE_VICE_PRINCIPAL",
                         "ROLE_FINANCE_DIRECTOR");
    }

    /**
     * 检查是否为超级管理员
     */
    public boolean isSuperAdmin() {
        return hasRole("ROLE_SUPER_ADMIN");
    }

    /**
     * 检查是否为校级领导（校长、副校长）
     */
    public boolean isSchoolLeader() {
        return hasAnyRole("ROLE_PRINCIPAL", "ROLE_VICE_PRINCIPAL");
    }

    /**
     * 检查是否为处级干部
     */
    public boolean isDepartmentDirector() {
        return hasAnyRole("ROLE_ACADEMIC_DIRECTOR", "ROLE_STUDENT_AFFAIRS_DIRECTOR", "ROLE_HR_DIRECTOR",
                         "ROLE_FINANCE_DIRECTOR");
    }

    /**
     * 检查是否为教学人员
     */
    public boolean isTeachingStaff() {
        return hasAnyRole("ROLE_TEACHER");
    }

    /**
     * 检查是否为学生工作人员
     */
    public boolean isStudentAffairsStaff() {
        return hasAnyRole("ROLE_STUDENT_AFFAIRS_DIRECTOR");
    }

    /**
     * 检查是否为学生
     */
    public boolean isStudent() {
        return hasAnyRole("ROLE_STUDENT");
    }

    /**
     * 获取当前登录用户
     */
    private User getCurrentUser() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpSession session = attributes.getRequest().getSession(false);
                if (session != null) {
                    // 优先从session中获取user（AdminJwtInterceptor设置的）
                    User user = (User) session.getAttribute("user");
                    if (user == null) {
                        // 兼容旧的属性名
                        user = (User) session.getAttribute("currentUser");
                    }
                    return user;
                }
            }
        } catch (Exception e) {
            // 忽略异常，返回null
        }
        return null;
    }
}
