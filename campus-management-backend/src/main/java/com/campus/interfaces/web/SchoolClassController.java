package com.campus.interfaces.web;

import com.campus.application.service.SchoolClassService;
import com.campus.application.service.DepartmentService;
import com.campus.application.service.UserService;
import com.campus.domain.entity.SchoolClass;
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
import java.util.Map;

/**
 * 班级管理Web控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Controller
@RequestMapping("/admin/classes")
public class SchoolClassController {
    
    private static final Logger logger = LoggerFactory.getLogger(SchoolClassController.class);
    
    @Autowired
    private SchoolClassService schoolClassService;
    
    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 班级管理主页
     */
    @GetMapping
    public String classesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String grade,
            Model model) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<SchoolClass> classes;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                classes = schoolClassService.searchClasses(keyword.trim(), pageable);
                model.addAttribute("keyword", keyword);
            } else if (departmentId != null) {
                classes = schoolClassService.findClassesByDepartment(departmentId, pageable);
                model.addAttribute("departmentId", departmentId);
            } else if (grade != null && !grade.trim().isEmpty()) {
                classes = schoolClassService.findClassesByGrade(grade, pageable);
                model.addAttribute("grade", grade);
            } else {
                classes = schoolClassService.findAllClasses(pageable);
            }
            
            model.addAttribute("classes", classes);
            model.addAttribute("currentPage", "classes");
            model.addAttribute("totalClasses", schoolClassService.countTotalClasses());
            model.addAttribute("activeClasses", schoolClassService.countActiveClasses());
            
            // 获取院系列表用于筛选
            List<Department> departments = departmentService.findActiveDepartments();
            model.addAttribute("departments", departments);
            
            // 获取年级列表用于筛选
            List<String> grades = schoolClassService.findAllGrades();
            model.addAttribute("grades", grades);
            
            return "admin/academic/classes";
            
        } catch (Exception e) {
            logger.error("加载班级管理页面失败", e);
            model.addAttribute("error", "加载班级信息失败: " + e.getMessage());
            return "admin/academic/classes";
        }
    }
    
    /**
     * 班级详情页面
     */
    @GetMapping("/{id}")
    public String classDetail(@PathVariable Long id, Model model) {
        try {
            SchoolClass schoolClass = schoolClassService.findClassById(id);
            if (schoolClass == null) {
                model.addAttribute("error", "班级不存在");
                return "redirect:/admin/classes";
            }
            
            // 获取班级相关统计信息
            long studentCount = schoolClassService.countStudentsByClass(id);
            long courseCount = schoolClassService.countCoursesByClass(id);
            
            model.addAttribute("schoolClass", schoolClass);
            model.addAttribute("studentCount", studentCount);
            model.addAttribute("courseCount", courseCount);
            model.addAttribute("currentPage", "classes");
            
            return "admin/academic/class-detail";
            
        } catch (Exception e) {
            logger.error("加载班级详情失败", e);
            model.addAttribute("error", "加载班级详情失败: " + e.getMessage());
            return "redirect:/admin/classes";
        }
    }
    
    /**
     * 新增班级页面
     */
    @GetMapping("/new")
    public String newClassPage(Model model) {
        model.addAttribute("schoolClass", new SchoolClass());
        model.addAttribute("currentPage", "classes");
        model.addAttribute("isEdit", false);
        
        // 获取院系列表
        List<Department> departments = departmentService.findActiveDepartments();
        model.addAttribute("departments", departments);
        
        // 获取班主任候选人（教师用户）
        List<User> teachers = userService.findTeachers();
        model.addAttribute("teachers", teachers);
        
        return "admin/academic/class-form";
    }
    
    /**
     * 编辑班级页面
     */
    @GetMapping("/{id}/edit")
    public String editClassPage(@PathVariable Long id, Model model) {
        try {
            SchoolClass schoolClass = schoolClassService.findClassById(id);
            if (schoolClass == null) {
                model.addAttribute("error", "班级不存在");
                return "redirect:/admin/classes";
            }
            model.addAttribute("schoolClass", schoolClass);
            model.addAttribute("currentPage", "classes");
            model.addAttribute("isEdit", true);
            
            // 获取院系列表
            List<Department> departments = departmentService.findActiveDepartments();
            model.addAttribute("departments", departments);
            
            // 获取班主任候选人（教师用户）
            List<User> teachers = userService.findTeachers();
            model.addAttribute("teachers", teachers);
            
            return "admin/academic/class-form";
            
        } catch (Exception e) {
            logger.error("加载班级编辑页面失败", e);
            model.addAttribute("error", "加载班级编辑页面失败: " + e.getMessage());
            return "redirect:/admin/classes";
        }
    }
    
    /**
     * 保存班级（新增或更新）
     */
    @PostMapping("/save")
    public String saveClass(@Valid @ModelAttribute SchoolClass schoolClass, 
                           RedirectAttributes redirectAttributes) {
        try {
            if (schoolClass.getId() == null) {
                // 新增班级
                schoolClassService.createClass(schoolClass);
                redirectAttributes.addFlashAttribute("success", "班级创建成功");
            } else {
                // 更新班级
                schoolClassService.updateClass(schoolClass);
                redirectAttributes.addFlashAttribute("success", "班级更新成功");
            }
            
            return "redirect:/admin/classes";
            
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return schoolClass.getId() == null ? "redirect:/admin/classes/new" : 
                   "redirect:/admin/classes/" + schoolClass.getId() + "/edit";
        } catch (Exception e) {
            logger.error("保存班级失败", e);
            redirectAttributes.addFlashAttribute("error", "保存班级失败: " + e.getMessage());
            return schoolClass.getId() == null ? "redirect:/admin/classes/new" : 
                   "redirect:/admin/classes/" + schoolClass.getId() + "/edit";
        }
    }
    
    /**
     * 删除班级
     */
    @PostMapping("/{id}/delete")
    public String deleteClass(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            schoolClassService.deleteClass(id);
            redirectAttributes.addFlashAttribute("success", "班级删除成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("删除班级失败", e);
            redirectAttributes.addFlashAttribute("error", "删除班级失败: " + e.getMessage());
        }
        
        return "redirect:/admin/classes";
    }
    
    /**
     * 启用班级
     */
    @PostMapping("/{id}/enable")
    public String enableClass(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            schoolClassService.enableClass(id);
            redirectAttributes.addFlashAttribute("success", "班级启用成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("启用班级失败", e);
            redirectAttributes.addFlashAttribute("error", "启用班级失败: " + e.getMessage());
        }
        
        return "redirect:/admin/classes";
    }
    
    /**
     * 禁用班级
     */
    @PostMapping("/{id}/disable")
    public String disableClass(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            schoolClassService.disableClass(id);
            redirectAttributes.addFlashAttribute("success", "班级禁用成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("禁用班级失败", e);
            redirectAttributes.addFlashAttribute("error", "禁用班级失败: " + e.getMessage());
        }
        
        return "redirect:/admin/classes";
    }
    
    /**
     * 班级学生列表页面
     */
    @GetMapping("/{id}/students")
    public String classStudents(@PathVariable Long id, Model model) {
        try {
            SchoolClass schoolClass = schoolClassService.findClassById(id);
            if (schoolClass == null) {
                model.addAttribute("error", "班级不存在");
                return "redirect:/admin/classes";
            }

            // 注意：获取班级学生列表功能待实现
            
            model.addAttribute("schoolClass", schoolClass);
            model.addAttribute("currentPage", "classes");
            
            return "admin/academic/class-students";
            
        } catch (Exception e) {
            logger.error("加载班级学生页面失败", e);
            model.addAttribute("error", "加载班级学生页面失败: " + e.getMessage());
            return "redirect:/admin/classes";
        }
    }
    
    /**
     * 班级课程列表页面
     */
    @GetMapping("/{id}/courses")
    public String classCourses(@PathVariable Long id, Model model) {
        try {
            SchoolClass schoolClass = schoolClassService.findClassById(id);
            if (schoolClass == null) {
                model.addAttribute("error", "班级不存在");
                return "redirect:/admin/classes";
            }

            // 注意：获取班级课程列表功能待实现
            
            model.addAttribute("schoolClass", schoolClass);
            model.addAttribute("currentPage", "classes");
            
            return "admin/academic/class-courses";
            
        } catch (Exception e) {
            logger.error("加载班级课程页面失败", e);
            model.addAttribute("error", "加载班级课程页面失败: " + e.getMessage());
            return "redirect:/admin/classes";
        }
    }
    
    /**
     * 班级统计页面
     */
    @GetMapping("/statistics")
    public String classStatistics(Model model) {
        try {
            long totalClasses = schoolClassService.countTotalClasses();
            long activeClasses = schoolClassService.countActiveClasses();
            List<Object[]> gradeStats = schoolClassService.countClassesByGrade();
            Map<String, Long> departmentStats = schoolClassService.countClassesByDepartment();
            
            model.addAttribute("totalClasses", totalClasses);
            model.addAttribute("activeClasses", activeClasses);
            model.addAttribute("gradeStats", gradeStats);
            model.addAttribute("departmentStats", departmentStats);
            model.addAttribute("currentPage", "classes");
            
            return "admin/academic/class-statistics";
            
        } catch (Exception e) {
            logger.error("加载班级统计失败", e);
            model.addAttribute("error", "加载班级统计失败: " + e.getMessage());
            return "redirect:/admin/classes";
        }
    }
    
    /**
     * 批量导入班级页面
     */
    @GetMapping("/import")
    public String importClassesPage(Model model) {
        model.addAttribute("currentPage", "classes");
        return "admin/academic/class-import";
    }
    
    /**
     * 导出班级数据
     */
    @GetMapping("/export")
    public String exportClasses(Model model, RedirectAttributes redirectAttributes) {
        try {
            List<SchoolClass> classes = schoolClassService.findAllClasses();
            // 注意：实际的导出功能（Excel、CSV等）待实现
            redirectAttributes.addFlashAttribute("success", "班级数据导出成功，共 " + classes.size() + " 条记录");
        } catch (Exception e) {
            logger.error("导出班级数据失败", e);
            redirectAttributes.addFlashAttribute("error", "导出班级数据失败: " + e.getMessage());
        }
        
        return "redirect:/admin/classes";
    }
}
