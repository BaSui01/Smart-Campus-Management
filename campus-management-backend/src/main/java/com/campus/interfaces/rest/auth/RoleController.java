package com.campus.interfaces.rest.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.campus.application.service.auth.PermissionService;
import com.campus.application.service.auth.RoleService;
import com.campus.domain.entity.auth.Permission;
import com.campus.domain.entity.auth.Role;
import com.campus.shared.exception.BusinessException;

import jakarta.validation.Valid;

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
            Role role = roleService.findRoleById(id);
            if (role == null) {
                model.addAttribute("error", "角色不存在");
                return "redirect:/admin/roles";
            }

            List<Map<String, Object>> rolePermissions = roleService.getRolePermissions(id);
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
            Role role = roleService.findRoleById(id);
            if (role == null) {
                model.addAttribute("error", "角色不存在");
                return "redirect:/admin/roles";
            }

            List<Map<String, Object>> rolePermissions = roleService.getRolePermissions(id);
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
            Role role = roleService.findRoleById(id);
            if (role == null) {
                model.addAttribute("error", "角色不存在");
                return "redirect:/admin/roles";
            }

            List<Map<String, Object>> rolePermissions = roleService.getRolePermissions(id);
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
            Role role = roleService.findRoleById(id);
            if (role == null) {
                model.addAttribute("error", "角色不存在");
                return "redirect:/admin/roles";
            }

            // 注意：获取拥有该角色的用户列表功能待实现
            
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
    
    /**
     * REST API - 获取角色列表（JSON响应）
     * 用于前端AJAX请求
     */
    @GetMapping(produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> getRolesJson(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Role> roles;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                roles = roleService.searchRoles(keyword.trim(), pageable);
            } else {
                roles = roleService.findAllRoles(pageable);
            }
            
            // 构建响应数据
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", roles);
            response.put("totalRoles", roleService.countTotalRoles());
            response.put("systemRoles", roleService.countSystemRoles());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取角色列表JSON失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取角色信息失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * REST API - 获取角色详情（JSON响应）
     */
    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> getRoleDetailJson(@PathVariable Long id) {
        try {
            Role role = roleService.findRoleById(id);
            if (role == null) {
                return ResponseEntity.notFound().build();
            }

            List<Map<String, Object>> rolePermissions = roleService.getRolePermissions(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("role", role);
            response.put("permissions", rolePermissions);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取角色详情JSON失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取角色详情失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
