package com.campus.interfaces.web;

import com.campus.application.service.UserService;
import com.campus.domain.entity.User;
import com.campus.domain.entity.Role;
import com.campus.domain.entity.UserRole;
import com.campus.shared.util.PermissionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 权限诊断控制器
 * 用于诊断和调试权限相关问题
 */
@Controller
@RequestMapping("/admin/diagnostic")
public class DiagnosticController {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosticController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionUtil permissionUtil;

    /**
     * 权限诊断页面
     */
    @GetMapping("/permissions")
    public String permissionDiagnostic(Model model, HttpSession session) {
        try {
            // 获取当前用户
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                currentUser = (User) session.getAttribute("user");
            }

            if (currentUser != null) {
                // 重新从数据库获取用户信息以确保数据最新
                User dbUser = userService.findUserById(currentUser.getId());
                
                model.addAttribute("currentUser", dbUser);
                model.addAttribute("userRoles", userService.getUserRoles(dbUser.getId()));
                
                // 获取用户的角色详细信息
                List<Role> roles = dbUser.getUserRoles().stream()
                    .map(UserRole::getRole)
                    .collect(Collectors.toList());
                model.addAttribute("roleDetails", roles);
                
                // 权限检查结果
                Map<String, Boolean> permissionChecks = new HashMap<>();
                permissionChecks.put("isSuperAdmin", permissionUtil.isSuperAdmin());
                permissionChecks.put("isSystemAdmin", permissionUtil.isSystemAdmin());
                permissionChecks.put("isAcademicAdmin", permissionUtil.isAcademicAdmin());
                permissionChecks.put("isFinanceAdmin", permissionUtil.isFinanceAdmin());
                
                // 菜单权限检查
                Map<String, Boolean> menuPermissions = new HashMap<>();
                menuPermissions.put("/admin/dashboard", userService.hasMenuPermission(dbUser.getId(), "/admin/dashboard"));
                menuPermissions.put("/admin/users", userService.hasMenuPermission(dbUser.getId(), "/admin/users"));
                menuPermissions.put("/admin/roles", userService.hasMenuPermission(dbUser.getId(), "/admin/roles"));
                menuPermissions.put("/admin/permissions", userService.hasMenuPermission(dbUser.getId(), "/admin/permissions"));
                menuPermissions.put("/admin/settings", userService.hasMenuPermission(dbUser.getId(), "/admin/settings"));
                menuPermissions.put("/admin/students", userService.hasMenuPermission(dbUser.getId(), "/admin/students"));
                menuPermissions.put("/admin/fee-items", userService.hasMenuPermission(dbUser.getId(), "/admin/fee-items"));
                
                model.addAttribute("permissionChecks", permissionChecks);
                model.addAttribute("menuPermissions", menuPermissions);
                
                // 角色权限检查
                Map<String, Boolean> roleChecks = new HashMap<>();
                roleChecks.put("SUPER_ADMIN", userService.hasRole(dbUser.getId(), "SUPER_ADMIN"));
                roleChecks.put("ADMIN", userService.hasRole(dbUser.getId(), "ADMIN"));
                roleChecks.put("SYSTEM_ADMIN", userService.hasRole(dbUser.getId(), "SYSTEM_ADMIN"));
                roleChecks.put("ACADEMIC_ADMIN", userService.hasRole(dbUser.getId(), "ACADEMIC_ADMIN"));
                roleChecks.put("FINANCE_ADMIN", userService.hasRole(dbUser.getId(), "FINANCE_ADMIN"));
                roleChecks.put("TEACHER", userService.hasRole(dbUser.getId(), "TEACHER"));
                
                model.addAttribute("roleChecks", roleChecks);
            }
            
            model.addAttribute("currentPage", "diagnostic");
            return "admin/diagnostic/permissions";
            
        } catch (Exception e) {
            logger.error("权限诊断页面加载失败", e);
            model.addAttribute("error", "诊断信息加载失败: " + e.getMessage());
            return "admin/diagnostic/permissions";
        }
    }

    /**
     * API方式获取权限诊断信息
     */
    @GetMapping("/api/permissions")
    @ResponseBody
    public Map<String, Object> getPermissionDiagnosticApi(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                currentUser = (User) session.getAttribute("user");
            }

            if (currentUser != null) {
                User dbUser = userService.findUserById(currentUser.getId());
                
                result.put("userId", dbUser.getId());
                result.put("username", dbUser.getUsername());
                result.put("realName", dbUser.getRealName());
                result.put("status", dbUser.getStatus());
                
                // 用户角色
                List<String> userRoles = userService.getUserRoles(dbUser.getId());
                result.put("userRoles", userRoles);
                
                // 角色详细信息
                List<Map<String, Object>> roleDetails = dbUser.getUserRoles().stream()
                    .map(userRole -> {
                        Role role = userRole.getRole();
                        Map<String, Object> roleInfo = new HashMap<>();
                        roleInfo.put("id", role.getId());
                        roleInfo.put("roleName", role.getRoleName());
                        roleInfo.put("roleKey", role.getRoleKey());
                        roleInfo.put("description", role.getDescription());
                        roleInfo.put("isSystem", role.getIsSystem());
                        roleInfo.put("roleLevel", role.getRoleLevel());
                        return roleInfo;
                    })
                    .collect(Collectors.toList());
                result.put("roleDetails", roleDetails);
                
                // 权限检查结果
                Map<String, Boolean> permissionChecks = new HashMap<>();
                permissionChecks.put("isSuperAdmin", permissionUtil.isSuperAdmin());
                permissionChecks.put("isSystemAdmin", permissionUtil.isSystemAdmin());
                permissionChecks.put("isAcademicAdmin", permissionUtil.isAcademicAdmin());
                permissionChecks.put("isFinanceAdmin", permissionUtil.isFinanceAdmin());
                result.put("permissionChecks", permissionChecks);
                
                // 角色检查
                Map<String, Boolean> roleChecks = new HashMap<>();
                roleChecks.put("SUPER_ADMIN", userService.hasRole(dbUser.getId(), "SUPER_ADMIN"));
                roleChecks.put("ADMIN", userService.hasRole(dbUser.getId(), "ADMIN"));
                roleChecks.put("SYSTEM_ADMIN", userService.hasRole(dbUser.getId(), "SYSTEM_ADMIN"));
                roleChecks.put("ACADEMIC_ADMIN", userService.hasRole(dbUser.getId(), "ACADEMIC_ADMIN"));
                roleChecks.put("FINANCE_ADMIN", userService.hasRole(dbUser.getId(), "FINANCE_ADMIN"));
                roleChecks.put("TEACHER", userService.hasRole(dbUser.getId(), "TEACHER"));
                result.put("roleChecks", roleChecks);
                
                result.put("success", true);
            } else {
                result.put("success", false);
                result.put("message", "用户未登录");
            }
            
        } catch (Exception e) {
            logger.error("获取权限诊断信息失败", e);
            result.put("success", false);
            result.put("message", "获取诊断信息失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 修复admin用户权限
     */
    @GetMapping("/fix-admin-permissions")
    @ResponseBody
    public Map<String, Object> fixAdminPermissions() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 查找admin用户
            Optional<User> adminUserOpt = userService.findByUsername("admin");
            User adminUser = adminUserOpt.orElse(null);
            if (adminUser == null) {
                result.put("success", false);
                result.put("message", "未找到admin用户");
                return result;
            }
            
            // 检查是否已有SUPER_ADMIN角色
            boolean hasSuperAdmin = userService.hasRole(adminUser.getId(), "SUPER_ADMIN");
            
            result.put("success", true);
            result.put("adminUserId", adminUser.getId());
            result.put("adminUsername", adminUser.getUsername());
            result.put("hasSuperAdminRole", hasSuperAdmin);
            result.put("currentRoles", userService.getUserRoles(adminUser.getId()));
            
            if (hasSuperAdmin) {
                result.put("message", "admin用户已具有SUPER_ADMIN角色，无需修复");
            } else {
                result.put("message", "admin用户缺少SUPER_ADMIN角色，需要手动分配");
            }
            
        } catch (Exception e) {
            logger.error("修复admin权限失败", e);
            result.put("success", false);
            result.put("message", "修复失败: " + e.getMessage());
        }
        
        return result;
    }
}
