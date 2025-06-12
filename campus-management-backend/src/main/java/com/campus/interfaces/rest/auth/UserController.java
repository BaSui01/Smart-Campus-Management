package com.campus.interfaces.rest.auth;

import com.campus.application.service.auth.RoleService;
import com.campus.application.service.auth.UserService;
import com.campus.domain.entity.auth.Role;
import com.campus.domain.entity.auth.User;
import com.campus.interfaces.web.common.BaseWebController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理Web控制器
 * 简化版本，只包含基本功能
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Controller
@RequestMapping("/admin/user-management")
@PreAuthorize("hasRole('ADMIN')")
public class UserController extends BaseWebController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    /**
     * 用户管理主页
     */
    @GetMapping
    public String index(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        try {
            logger.info("访问用户管理页面 - 页码: {}, 大小: {}", page, size);

            // 创建分页对象
            Pageable pageable = createPageable(page, size);

            // 查询用户列表
            Page<User> userPage = userService.findAllUsers(pageable);

            // 查询角色列表
            List<Role> roles = roleService.findAllActiveRoles();

            // 添加到模型
            model.addAttribute("userPage", userPage);
            model.addAttribute("roles", roles);
            addPaginationToModel(model, page, size, userPage.getTotalElements());

            return "admin/users/index";

        } catch (Exception e) {
            logger.error("访问用户管理页面失败: ", e);
            addErrorMessage(model, "加载用户列表失败: " + e.getMessage());
            return "admin/users/index";
        }
    }

    /**
     * 用户详情页面
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {

        try {
            logger.info("查看用户详情 - ID: {}", id);

            // 简化版本：直接显示基本信息
            model.addAttribute("userId", id);
            addInfoMessage(model, "用户详情功能正在开发中");

            return "admin/users/detail";

        } catch (Exception e) {
            logger.error("查看用户详情失败 - ID: {}", id, e);
            addErrorMessage(model, "查看用户详情失败: " + e.getMessage());
            return "admin/users/index";
        }
    }

    /**
     * 新增用户页面
     */
    @GetMapping("/create")
    public String create(Model model) {

        try {
            logger.info("访问新增用户页面");

            // 查询角色列表
            List<Role> roles = roleService.findAllActiveRoles();

            model.addAttribute("user", new User());
            model.addAttribute("roles", roles);

            return "admin/users/create";

        } catch (Exception e) {
            logger.error("访问新增用户页面失败: ", e);
            addErrorMessage(model, "加载页面失败: " + e.getMessage());
            return "admin/users/index";
        }
    }

    /**
     * 保存新用户
     */
    @PostMapping("/create")
    public String save(@ModelAttribute User user, Model model) {

        try {
            logger.info("保存新用户 - 用户名: {}", user.getUsername());

            // 简化版本：基本保存功能
            addInfoMessage(model, "用户创建功能正在开发中");
            return "redirect:/admin/users";

        } catch (Exception e) {
            logger.error("保存用户失败: ", e);
            addErrorMessage(model, "保存用户失败: " + e.getMessage());
            return "admin/users/create";
        }
    }

    // ==================== 简化功能提示 ====================

    /**
     * 其他功能页面（编辑、删除等）
     */
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        addInfoMessage(model, "用户编辑功能正在开发中");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        return "redirect:/admin/users?info=delete-pending";
    }

    @PostMapping("/{id}/enable")
    public String enable(@PathVariable Long id) {
        return "redirect:/admin/users?info=enable-pending";
    }

    @PostMapping("/{id}/disable")
    public String disable(@PathVariable Long id) {
        return "redirect:/admin/users?info=disable-pending";
    }
}
