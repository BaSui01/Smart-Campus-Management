package com.campus.interfaces.web;

import com.campus.application.service.RoleService;
import com.campus.application.service.PermissionService;
import com.campus.domain.entity.Role;
import com.campus.domain.entity.Permission;
import com.campus.shared.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 角色管理Web控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Controller
@RequestMapping("/admin/roles")
public class RoleController {
    
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private PermissionService permissionService;
    
    /**
     * 角色管理主页
     */
    @GetMapping
    public String rolesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Role> roles;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                roles = roleService.searchRoles(keyword.trim(), pageable);
                model.addAttribute("keyword", keyword);
            } else {
                roles = roleService.findAllRoles(pageable);
            }
            
            model.addAttribute("roles", roles);
            model.addAttribute("currentPage", "roles");
            model.addAttribute("totalRoles", roleService.countTotalRoles());
            model.addAttribute("systemRoles", roleService.countSystemRoles());
            
            return "admin/system/roles";
            
        } catch (Exception e) {
            logger.error("加载角色管理页面失败", e);
            model.addAttribute("error", "加载角色信息失败: " + e.getMessage());
            return "admin/system/roles";
        }
    }
    
    /**
     * 角色详情页面
     */
    @GetMapping("/{id}")
    public String roleDetail(@PathVariable Long id, Model model) {
        try {
            Optional<Role> roleOpt = roleService.findRoleById(id);
            if (roleOpt.isEmpty()) {
                model.addAttribute("error", "角色不存在");
                return "redirect:/admin/roles";
            }
            
            Role role = roleOpt.get();
            List<Permission> rolePermissions = roleService.getRolePermissions(id);
            List<Permission> allPermissions = permissionService.findAllPermissions();
            
            model.addAttribute("role", role);
            model.addAttribute("rolePermissions", rolePermissions);
            model.addAttribute("allPermissions", allPermissions);
            model.addAttribute("currentPage", "roles");
            
            return "admin/system/role-detail";
            
        } catch (Exception e) {
            logger.error("加载角色详情失败", e);
            model.addAttribute("error", "加载角色详情失败: " + e.getMessage());
            return "redirect:/admin/roles";
        }
    }
    
    /**
     * 新增角色页面
     */
    @GetMapping("/new")
    public String newRolePage(Model model) {
        model.addAttribute("role", new Role());
        model.addAttribute("allPermissions", permissionService.findAllPermissions());
        model.addAttribute("currentPage", "roles");
        model.addAttribute("isEdit", false);
        return "admin/system/role-form";
    }
    
    /**
     * 编辑角色页面
     */
    @GetMapping("/{id}/edit")
    public String editRolePage(@PathVariable Long id, Model model) {
        try {
            Optional<Role> roleOpt = roleService.findRoleById(id);
            if (roleOpt.isEmpty()) {
                model.addAttribute("error", "角色不存在");
                return "redirect:/admin/roles";
            }
            
            Role role = roleOpt.get();
            List<Permission> rolePermissions = roleService.getRolePermissions(id);
            List<Permission> allPermissions = permissionService.findAllPermissions();
            
            model.addAttribute("role", role);
            model.addAttribute("rolePermissions", rolePermissions);
            model.addAttribute("allPermissions", allPermissions);
            model.addAttribute("currentPage", "roles");
            model.addAttribute("isEdit", true);
            
            return "admin/system/role-form";
            
        } catch (Exception e) {
            logger.error("加载角色编辑页面失败", e);
            model.addAttribute("error", "加载角色编辑页面失败: " + e.getMessage());
            return "redirect:/admin/roles";
        }
    }
    
    /**
     * 保存角色（新增或更新）
     */
    @PostMapping("/save")
    public String saveRole(@Valid @ModelAttribute Role role,
                          @RequestParam(required = false) List<Long> permissionIds,
                          RedirectAttributes redirectAttributes) {
        try {
            if (role.getId() == null) {
                // 新增角色
                Role savedRole = roleService.createRole(role);
                if (permissionIds != null && !permissionIds.isEmpty()) {
                    roleService.assignPermissions(savedRole.getId(), permissionIds);
                }
                redirectAttributes.addFlashAttribute("success", "角色创建成功");
            } else {
                // 更新角色
                roleService.updateRole(role);
                if (permissionIds != null) {
                    roleService.assignPermissions(role.getId(), permissionIds);
                } else {
                    roleService.clearRolePermissions(role.getId());
                }
                redirectAttributes.addFlashAttribute("success", "角色更新成功");
            }
            
            return "redirect:/admin/roles";
            
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return role.getId() == null ? "redirect:/admin/roles/new" : 
                   "redirect:/admin/roles/" + role.getId() + "/edit";
        } catch (Exception e) {
            logger.error("保存角色失败", e);
            redirectAttributes.addFlashAttribute("error", "保存角色失败: " + e.getMessage());
            return role.getId() == null ? "redirect:/admin/roles/new" : 
                   "redirect:/admin/roles/" + role.getId() + "/edit";
        }
    }
    
    /**
     * 删除角色
     */
    @PostMapping("/{id}/delete")
    public String deleteRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roleService.deleteRole(id);
            redirectAttributes.addFlashAttribute("success", "角色删除成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("删除角色失败", e);
            redirectAttributes.addFlashAttribute("error", "删除角色失败: " + e.getMessage());
        }
        
        return "redirect:/admin/roles";
    }
    
    /**
     * 启用角色
     */
    @PostMapping("/{id}/enable")
    public String enableRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roleService.enableRole(id);
            redirectAttributes.addFlashAttribute("success", "角色启用成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("启用角色失败", e);
            redirectAttributes.addFlashAttribute("error", "启用角色失败: " + e.getMessage());
        }
        
        return "redirect:/admin/roles";
    }
    
    /**
     * 禁用角色
     */
    @PostMapping("/{id}/disable")
    public String disableRole(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roleService.disableRole(id);
            redirectAttributes.addFlashAttribute("success", "角色禁用成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("禁用角色失败", e);
            redirectAttributes.addFlashAttribute("error", "禁用角色失败: " + e.getMessage());
        }
        
        return "redirect:/admin/roles";
    }
    
    /**
     * 角色权限管理页面
     */
    @GetMapping("/{id}/permissions")
    public String rolePermissions(@PathVariable Long id, Model model) {
        try {
            Optional<Role> roleOpt = roleService.findRoleById(id);
            if (roleOpt.isEmpty()) {
                model.addAttribute("error", "角色不存在");
                return "redirect:/admin/roles";
            }
            
            Role role = roleOpt.get();
            List<Permission> rolePermissions = roleService.getRolePermissions(id);
            List<Permission> allPermissions = permissionService.findAllPermissions();
            
            model.addAttribute("role", role);
            model.addAttribute("rolePermissions", rolePermissions);
            model.addAttribute("allPermissions", allPermissions);
            model.addAttribute("currentPage", "roles");
            
            return "admin/system/role-permissions";
            
        } catch (Exception e) {
            logger.error("加载角色权限页面失败", e);
            model.addAttribute("error", "加载角色权限页面失败: " + e.getMessage());
            return "redirect:/admin/roles";
        }
    }
    
    /**
     * 更新角色权限
     */
    @PostMapping("/{id}/permissions")
    public String updateRolePermissions(@PathVariable Long id,
                                       @RequestParam(required = false) List<Long> permissionIds,
                                       RedirectAttributes redirectAttributes) {
        try {
            if (permissionIds != null && !permissionIds.isEmpty()) {
                roleService.assignPermissions(id, permissionIds);
            } else {
                roleService.clearRolePermissions(id);
            }
            redirectAttributes.addFlashAttribute("success", "角色权限更新成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("更新角色权限失败", e);
            redirectAttributes.addFlashAttribute("error", "更新角色权限失败: " + e.getMessage());
        }
        
        return "redirect:/admin/roles/" + id + "/permissions";
    }
    
    /**
     * 角色用户列表页面
     */
    @GetMapping("/{id}/users")
    public String roleUsers(@PathVariable Long id, Model model) {
        try {
            Optional<Role> roleOpt = roleService.findRoleById(id);
            if (roleOpt.isEmpty()) {
                model.addAttribute("error", "角色不存在");
                return "redirect:/admin/roles";
            }
            
            Role role = roleOpt.get();
            // TODO: 获取拥有该角色的用户列表
            
            model.addAttribute("role", role);
            model.addAttribute("currentPage", "roles");
            
            return "admin/system/role-users";
            
        } catch (Exception e) {
            logger.error("加载角色用户页面失败", e);
            model.addAttribute("error", "加载角色用户页面失败: " + e.getMessage());
            return "redirect:/admin/roles";
        }
    }
    
    /**
     * 角色统计页面
     */
    @GetMapping("/statistics")
    public String roleStatistics(Model model) {
        try {
            long totalRoles = roleService.countTotalRoles();
            long systemRoles = roleService.countSystemRoles();
            long customRoles = totalRoles - systemRoles;
            
            model.addAttribute("totalRoles", totalRoles);
            model.addAttribute("systemRoles", systemRoles);
            model.addAttribute("customRoles", customRoles);
            model.addAttribute("currentPage", "roles");
            
            return "admin/system/role-statistics";
            
        } catch (Exception e) {
            logger.error("加载角色统计失败", e);
            model.addAttribute("error", "加载角色统计失败: " + e.getMessage());
            return "redirect:/admin/roles";
        }
    }
}
