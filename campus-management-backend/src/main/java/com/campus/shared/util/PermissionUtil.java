package com.campus.shared.util;

import com.campus.application.service.UserService;
import com.campus.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

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
        return hasAnyRole("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_PRINCIPAL", "ROLE_VICE_PRINCIPAL");
    }

    /**
     * 检查是否为教务管理员
     */
    public boolean isAcademicAdmin() {
        return hasAnyRole("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_PRINCIPAL", "ROLE_VICE_PRINCIPAL",
                         "ROLE_ACADEMIC_DIRECTOR", "ROLE_DEAN", "ROLE_VICE_DEAN", "ROLE_DEPARTMENT_HEAD",
                         "ROLE_TEACHING_GROUP_HEAD", "ROLE_ACADEMIC_STAFF");
    }

    /**
     * 检查是否为财务管理员
     */
    public boolean isFinanceAdmin() {
        return hasAnyRole("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_PRINCIPAL", "ROLE_VICE_PRINCIPAL",
                         "ROLE_FINANCE_DIRECTOR", "ROLE_FINANCE_STAFF");
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
                         "ROLE_FINANCE_DIRECTOR", "ROLE_LOGISTICS_DIRECTOR", "ROLE_IT_DIRECTOR",
                         "ROLE_INTERNATIONAL_DIRECTOR", "ROLE_ADMISSION_DIRECTOR");
    }

    /**
     * 检查是否为学院领导（院长、副院长、系主任）
     */
    public boolean isCollegeLeader() {
        return hasAnyRole("ROLE_DEAN", "ROLE_VICE_DEAN", "ROLE_DEPARTMENT_HEAD", "ROLE_VICE_DEPARTMENT_HEAD",
                         "ROLE_TEACHING_GROUP_HEAD", "ROLE_LAB_DIRECTOR");
    }

    /**
     * 检查是否为教学人员
     */
    public boolean isTeachingStaff() {
        return hasAnyRole("ROLE_TEACHER", "ROLE_PROFESSOR", "ROLE_ASSOCIATE_PROFESSOR", "ROLE_LECTURER",
                         "ROLE_ASSISTANT", "ROLE_SUPERVISOR", "ROLE_VISITING_TEACHER");
    }

    /**
     * 检查是否为学生工作人员
     */
    public boolean isStudentAffairsStaff() {
        return hasAnyRole("ROLE_CLASS_TEACHER", "ROLE_COUNSELOR", "ROLE_STUDENT_AFFAIRS_STAFF");
    }

    /**
     * 检查是否为学生
     */
    public boolean isStudent() {
        return hasAnyRole("ROLE_STUDENT", "ROLE_UNDERGRADUATE", "ROLE_GRADUATE", "ROLE_MASTER_STUDENT",
                         "ROLE_PHD_STUDENT", "ROLE_INTERNATIONAL_STUDENT", "ROLE_EXCHANGE_STUDENT", "ROLE_AUDITOR");
    }

    /**
     * 检查是否为学生干部
     */
    public boolean isStudentLeader() {
        return hasAnyRole("ROLE_STUDENT_LEADER", "ROLE_CLASS_MONITOR");
    }

    /**
     * 检查是否为行政支持人员
     */
    public boolean isAdministrativeStaff() {
        return hasAnyRole("ROLE_HR_STAFF", "ROLE_ACADEMIC_STAFF", "ROLE_FINANCE_STAFF", "ROLE_LOGISTICS_STAFF");
    }

    /**
     * 检查是否为家长
     */
    public boolean isParent() {
        return hasRole("ROLE_PARENT");
    }

    /**
     * 检查是否为访客
     */
    public boolean isVisitor() {
        return hasRole("ROLE_VISITOR");
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
