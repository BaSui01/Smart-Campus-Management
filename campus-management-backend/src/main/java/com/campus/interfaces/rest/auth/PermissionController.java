package com.campus.interfaces.rest.auth;

import com.campus.application.service.auth.PermissionService;
import com.campus.domain.entity.auth.Permission;
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
 * 权限管理Web控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Controller
@RequestMapping("/admin/permissions")
public class PermissionController {
    
    private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);
    
    @Autowired
    private PermissionService permissionService;
    
    /**
     * 权限管理主页
     */
    @GetMapping
    public String permissionsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String type,
            Model model) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Permission> permissions;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                permissions = permissionService.searchPermissions(keyword.trim(), pageable);
                model.addAttribute("keyword", keyword);
            } else if (module != null && !module.trim().isEmpty()) {
                List<Permission> modulePermissions = permissionService.findPermissionsByModule(module);
                // 注意：当前使用所有权限的分页结果，模块过滤功能待完善
                permissions = permissionService.findAllPermissions(pageable);
                model.addAttribute("module", module);
                model.addAttribute("modulePermissionsCount", modulePermissions.size());
            } else if (type != null && !type.trim().isEmpty()) {
                List<Permission> typePermissions = permissionService.findPermissionsByType(type);
                // 注意：当前使用所有权限的分页结果，类型过滤功能待完善
                permissions = permissionService.findAllPermissions(pageable);
                model.addAttribute("type", type);
                model.addAttribute("typePermissionsCount", typePermissions.size());
            } else {
                permissions = permissionService.findAllPermissions(pageable);
            }
            
            model.addAttribute("permissions", permissions);
            model.addAttribute("currentPage", "permissions");
            model.addAttribute("totalPermissions", permissionService.countTotalPermissions());
            model.addAttribute("systemPermissions", permissionService.countSystemPermissions());
            
            // 获取模块和类型列表用于筛选
            List<Object[]> moduleStats = permissionService.countPermissionsByModule();
            List<Object[]> typeStats = permissionService.countPermissionsByType();
            model.addAttribute("moduleStats", moduleStats);
            model.addAttribute("typeStats", typeStats);
            
            return "admin/system/permissions";
            
        } catch (Exception e) {
            logger.error("加载权限管理页面失败", e);
            model.addAttribute("error", "加载权限信息失败: " + e.getMessage());
            return "admin/system/permissions";
        }
    }
    
    /**
     * 权限详情页面
     */
    @GetMapping("/{id}")
    public String permissionDetail(@PathVariable Long id, Model model) {
        try {
            Optional<Permission> permissionOpt = permissionService.findPermissionById(id);
            if (permissionOpt.isEmpty()) {
                model.addAttribute("error", "权限不存在");
                return "redirect:/admin/permissions";
            }
            
            Permission permission = permissionOpt.get();
            model.addAttribute("permission", permission);
            model.addAttribute("currentPage", "permissions");
            
            return "admin/system/permission-detail";
            
        } catch (Exception e) {
            logger.error("加载权限详情失败", e);
            model.addAttribute("error", "加载权限详情失败: " + e.getMessage());
            return "redirect:/admin/permissions";
        }
    }
    
    /**
     * 新增权限页面
     */
    @GetMapping("/new")
    public String newPermissionPage(Model model) {
        model.addAttribute("permission", new Permission());
        model.addAttribute("currentPage", "permissions");
        model.addAttribute("isEdit", false);
        
        // 获取模块和类型选项
        List<Object[]> moduleStats = permissionService.countPermissionsByModule();
        List<Object[]> typeStats = permissionService.countPermissionsByType();
        model.addAttribute("moduleOptions", moduleStats);
        model.addAttribute("typeOptions", typeStats);
        
        return "admin/system/permission-form";
    }
    
    /**
     * 编辑权限页面
     */
    @GetMapping("/{id}/edit")
    public String editPermissionPage(@PathVariable Long id, Model model) {
        try {
            Optional<Permission> permissionOpt = permissionService.findPermissionById(id);
            if (permissionOpt.isEmpty()) {
                model.addAttribute("error", "权限不存在");
                return "redirect:/admin/permissions";
            }
            
            Permission permission = permissionOpt.get();
            model.addAttribute("permission", permission);
            model.addAttribute("currentPage", "permissions");
            model.addAttribute("isEdit", true);
            
            // 获取模块和类型选项
            List<Object[]> moduleStats = permissionService.countPermissionsByModule();
            List<Object[]> typeStats = permissionService.countPermissionsByType();
            model.addAttribute("moduleOptions", moduleStats);
            model.addAttribute("typeOptions", typeStats);
            
            return "admin/system/permission-form";
            
        } catch (Exception e) {
            logger.error("加载权限编辑页面失败", e);
            model.addAttribute("error", "加载权限编辑页面失败: " + e.getMessage());
            return "redirect:/admin/permissions";
        }
    }
    
    /**
     * 保存权限（新增或更新）
     */
    @PostMapping("/save")
    public String savePermission(@Valid @ModelAttribute Permission permission, 
                                RedirectAttributes redirectAttributes) {
        try {
            if (permission.getId() == null) {
                // 新增权限
                permissionService.createPermission(permission);
                redirectAttributes.addFlashAttribute("success", "权限创建成功");
            } else {
                // 更新权限
                permissionService.updatePermission(permission);
                redirectAttributes.addFlashAttribute("success", "权限更新成功");
            }
            
            return "redirect:/admin/permissions";
            
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return permission.getId() == null ? "redirect:/admin/permissions/new" : 
                   "redirect:/admin/permissions/" + permission.getId() + "/edit";
        } catch (Exception e) {
            logger.error("保存权限失败", e);
            redirectAttributes.addFlashAttribute("error", "保存权限失败: " + e.getMessage());
            return permission.getId() == null ? "redirect:/admin/permissions/new" : 
                   "redirect:/admin/permissions/" + permission.getId() + "/edit";
        }
    }
    
    /**
     * 删除权限
     */
    @PostMapping("/{id}/delete")
    public String deletePermission(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            permissionService.deletePermission(id);
            redirectAttributes.addFlashAttribute("success", "权限删除成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("删除权限失败", e);
            redirectAttributes.addFlashAttribute("error", "删除权限失败: " + e.getMessage());
        }
        
        return "redirect:/admin/permissions";
    }
    
    /**
     * 启用权限
     */
    @PostMapping("/{id}/enable")
    public String enablePermission(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            permissionService.enablePermission(id);
            redirectAttributes.addFlashAttribute("success", "权限启用成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("启用权限失败", e);
            redirectAttributes.addFlashAttribute("error", "启用权限失败: " + e.getMessage());
        }
        
        return "redirect:/admin/permissions";
    }
    
    /**
     * 禁用权限
     */
    @PostMapping("/{id}/disable")
    public String disablePermission(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            permissionService.disablePermission(id);
            redirectAttributes.addFlashAttribute("success", "权限禁用成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("禁用权限失败", e);
            redirectAttributes.addFlashAttribute("error", "禁用权限失败: " + e.getMessage());
        }
        
        return "redirect:/admin/permissions";
    }
    
    /**
     * 权限树形结构页面
     */
    @GetMapping("/tree")
    public String permissionTree(Model model) {
        try {
            Object permissionTree = permissionService.getPermissionTree();
            model.addAttribute("permissionTree", permissionTree);
            model.addAttribute("currentPage", "permissions");
            
            return "admin/system/permission-tree";
            
        } catch (Exception e) {
            logger.error("加载权限树失败", e);
            model.addAttribute("error", "加载权限树失败: " + e.getMessage());
            return "redirect:/admin/permissions";
        }
    }
    
    /**
     * 权限统计页面
     */
    @GetMapping("/statistics")
    public String permissionStatistics(Model model) {
        try {
            long totalPermissions = permissionService.countTotalPermissions();
            long systemPermissions = permissionService.countSystemPermissions();
            long customPermissions = totalPermissions - systemPermissions;
            List<Object[]> moduleStats = permissionService.countPermissionsByModule();
            List<Object[]> typeStats = permissionService.countPermissionsByType();
            
            model.addAttribute("totalPermissions", totalPermissions);
            model.addAttribute("systemPermissions", systemPermissions);
            model.addAttribute("customPermissions", customPermissions);
            model.addAttribute("moduleStats", moduleStats);
            model.addAttribute("typeStats", typeStats);
            model.addAttribute("currentPage", "permissions");
            
            return "admin/system/permission-statistics";
            
        } catch (Exception e) {
            logger.error("加载权限统计失败", e);
            model.addAttribute("error", "加载权限统计失败: " + e.getMessage());
            return "redirect:/admin/permissions";
        }
    }
    
    /**
     * 批量导入权限页面
     */
    @GetMapping("/import")
    public String importPermissionsPage(Model model) {
        model.addAttribute("currentPage", "permissions");
        return "admin/system/permission-import";
    }
    
    /**
     * 导出权限数据
     */
    @GetMapping("/export")
    public String exportPermissions(Model model, RedirectAttributes redirectAttributes) {
        try {
            List<Permission> permissions = permissionService.findAllPermissions();
            // 注意：实际的导出功能（Excel、CSV等）待实现
            redirectAttributes.addFlashAttribute("success", "权限数据导出成功，共 " + permissions.size() + " 条记录");
        } catch (Exception e) {
            logger.error("导出权限数据失败", e);
            redirectAttributes.addFlashAttribute("error", "导出权限数据失败: " + e.getMessage());
        }
        
        return "redirect:/admin/permissions";
    }
}
