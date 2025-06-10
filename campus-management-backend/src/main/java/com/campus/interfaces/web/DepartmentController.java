package com.campus.interfaces.web;

import com.campus.application.service.DepartmentService;
import com.campus.application.service.UserService;
import com.campus.domain.entity.Department;
import com.campus.domain.entity.User;
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

/**
 * 院系管理Web控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Controller
@RequestMapping("/admin/departments")
public class DepartmentController {
    
    private static final Logger logger = LoggerFactory.getLogger(DepartmentController.class);
    
    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 院系管理主页
     */
    @GetMapping
    public String departmentsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Department> departments;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                departments = departmentService.searchDepartments(keyword.trim(), pageable);
                model.addAttribute("keyword", keyword);
            } else {
                departments = departmentService.findAllDepartments(pageable);
            }
            
            model.addAttribute("departments", departments);
            model.addAttribute("currentPage", "departments");
            model.addAttribute("totalDepartments", departmentService.countTotalDepartments());
            model.addAttribute("activeDepartments", departmentService.countActiveDepartments());
            
            return "admin/academic/departments";
            
        } catch (Exception e) {
            logger.error("加载院系管理页面失败", e);
            model.addAttribute("error", "加载院系信息失败: " + e.getMessage());
            return "admin/academic/departments";
        }
    }
    
    /**
     * 院系详情页面
     */
    @GetMapping("/{id}")
    public String departmentDetail(@PathVariable Long id, Model model) {
        try {
            Department department = departmentService.findDepartmentById(id);
            if (department == null) {
                model.addAttribute("error", "院系不存在");
                return "redirect:/admin/departments";
            }
            
            // 获取院系相关统计信息
            long teacherCount = departmentService.countTeachersByDepartment(id);
            long studentCount = departmentService.countStudentsByDepartment(id);
            long courseCount = departmentService.countCoursesByDepartment(id);
            
            model.addAttribute("department", department);
            model.addAttribute("teacherCount", teacherCount);
            model.addAttribute("studentCount", studentCount);
            model.addAttribute("courseCount", courseCount);
            model.addAttribute("currentPage", "departments");
            
            return "admin/academic/department-detail";
            
        } catch (Exception e) {
            logger.error("加载院系详情失败", e);
            model.addAttribute("error", "加载院系详情失败: " + e.getMessage());
            return "redirect:/admin/departments";
        }
    }
    
    /**
     * 新增院系页面
     */
    @GetMapping("/new")
    public String newDepartmentPage(Model model) {
        model.addAttribute("department", new Department());
        model.addAttribute("currentPage", "departments");
        model.addAttribute("isEdit", false);
        
        // 获取可选的院系负责人（教师用户）
        List<User> teachers = userService.findTeachers();
        model.addAttribute("teachers", teachers);
        
        return "admin/academic/department-form";
    }
    
    /**
     * 编辑院系页面
     */
    @GetMapping("/{id}/edit")
    public String editDepartmentPage(@PathVariable Long id, Model model) {
        try {
            Department department = departmentService.findDepartmentById(id);
            if (department == null) {
                model.addAttribute("error", "院系不存在");
                return "redirect:/admin/departments";
            }
            model.addAttribute("department", department);
            model.addAttribute("currentPage", "departments");
            model.addAttribute("isEdit", true);
            
            // 获取可选的院系负责人（教师用户）
            List<User> teachers = userService.findTeachers();
            model.addAttribute("teachers", teachers);
            
            return "admin/academic/department-form";
            
        } catch (Exception e) {
            logger.error("加载院系编辑页面失败", e);
            model.addAttribute("error", "加载院系编辑页面失败: " + e.getMessage());
            return "redirect:/admin/departments";
        }
    }
    
    /**
     * 保存院系（新增或更新）
     */
    @PostMapping("/save")
    public String saveDepartment(@Valid @ModelAttribute Department department, 
                                RedirectAttributes redirectAttributes) {
        try {
            if (department.getId() == null) {
                // 新增院系
                departmentService.createDepartment(department);
                redirectAttributes.addFlashAttribute("success", "院系创建成功");
            } else {
                // 更新院系
                departmentService.updateDepartment(department);
                redirectAttributes.addFlashAttribute("success", "院系更新成功");
            }
            
            return "redirect:/admin/departments";
            
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return department.getId() == null ? "redirect:/admin/departments/new" : 
                   "redirect:/admin/departments/" + department.getId() + "/edit";
        } catch (Exception e) {
            logger.error("保存院系失败", e);
            redirectAttributes.addFlashAttribute("error", "保存院系失败: " + e.getMessage());
            return department.getId() == null ? "redirect:/admin/departments/new" : 
                   "redirect:/admin/departments/" + department.getId() + "/edit";
        }
    }
    
    /**
     * 删除院系
     */
    @PostMapping("/{id}/delete")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.deleteDepartment(id);
            redirectAttributes.addFlashAttribute("success", "院系删除成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("删除院系失败", e);
            redirectAttributes.addFlashAttribute("error", "删除院系失败: " + e.getMessage());
        }
        
        return "redirect:/admin/departments";
    }
    
    /**
     * 启用院系
     */
    @PostMapping("/{id}/enable")
    public String enableDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.enableDepartment(id);
            redirectAttributes.addFlashAttribute("success", "院系启用成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("启用院系失败", e);
            redirectAttributes.addFlashAttribute("error", "启用院系失败: " + e.getMessage());
        }
        
        return "redirect:/admin/departments";
    }
    
    /**
     * 禁用院系
     */
    @PostMapping("/{id}/disable")
    public String disableDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.disableDepartment(id);
            redirectAttributes.addFlashAttribute("success", "院系禁用成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("禁用院系失败", e);
            redirectAttributes.addFlashAttribute("error", "禁用院系失败: " + e.getMessage());
        }
        
        return "redirect:/admin/departments";
    }
    
    /**
     * 院系教师列表页面
     */
    @GetMapping("/{id}/teachers")
    public String departmentTeachers(@PathVariable Long id, Model model) {
        try {
            Department department = departmentService.findDepartmentById(id);
            if (department == null) {
                model.addAttribute("error", "院系不存在");
                return "redirect:/admin/departments";
            }

            List<Object> teachers = departmentService.findTeachersByDepartment(id);
            
            model.addAttribute("department", department);
            model.addAttribute("teachers", teachers);
            model.addAttribute("currentPage", "departments");
            
            return "admin/academic/department-teachers";
            
        } catch (Exception e) {
            logger.error("加载院系教师页面失败", e);
            model.addAttribute("error", "加载院系教师页面失败: " + e.getMessage());
            return "redirect:/admin/departments";
        }
    }
    
    /**
     * 院系学生列表页面
     */
    @GetMapping("/{id}/students")
    public String departmentStudents(@PathVariable Long id, Model model) {
        try {
            Department department = departmentService.findDepartmentById(id);
            if (department == null) {
                model.addAttribute("error", "院系不存在");
                return "redirect:/admin/departments";
            }

            // 注意：院系学生列表功能待实现
            
            model.addAttribute("department", department);
            model.addAttribute("currentPage", "departments");
            
            return "admin/academic/department-students";
            
        } catch (Exception e) {
            logger.error("加载院系学生页面失败", e);
            model.addAttribute("error", "加载院系学生页面失败: " + e.getMessage());
            return "redirect:/admin/departments";
        }
    }
    
    /**
     * 院系课程列表页面
     */
    @GetMapping("/{id}/courses")
    public String departmentCourses(@PathVariable Long id, Model model) {
        try {
            Department department = departmentService.findDepartmentById(id);
            if (department == null) {
                model.addAttribute("error", "院系不存在");
                return "redirect:/admin/departments";
            }

            // 注意：院系课程列表功能待实现
            
            model.addAttribute("department", department);
            model.addAttribute("currentPage", "departments");
            
            return "admin/academic/department-courses";
            
        } catch (Exception e) {
            logger.error("加载院系课程页面失败", e);
            model.addAttribute("error", "加载院系课程页面失败: " + e.getMessage());
            return "redirect:/admin/departments";
        }
    }
    
    /**
     * 院系统计页面
     */
    @GetMapping("/statistics")
    public String departmentStatistics(Model model) {
        try {
            long totalDepartments = departmentService.countTotalDepartments();
            long activeDepartments = departmentService.countActiveDepartments();
            Object departmentStats = departmentService.getDepartmentStatistics();
            
            model.addAttribute("totalDepartments", totalDepartments);
            model.addAttribute("activeDepartments", activeDepartments);
            model.addAttribute("departmentStats", departmentStats);
            model.addAttribute("currentPage", "departments");
            
            return "admin/academic/department-statistics";
            
        } catch (Exception e) {
            logger.error("加载院系统计失败", e);
            model.addAttribute("error", "加载院系统计失败: " + e.getMessage());
            return "redirect:/admin/departments";
        }
    }
}
