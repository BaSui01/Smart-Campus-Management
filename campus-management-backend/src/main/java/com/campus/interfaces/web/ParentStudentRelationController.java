package com.campus.interfaces.web;

import com.campus.application.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 家长学生关系管理页面控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-15
 */
@Controller
@RequestMapping("/admin/parent-student-relations")
public class ParentStudentRelationController {
    
    private static final Logger logger = LoggerFactory.getLogger(ParentStudentRelationController.class);
    
    @Autowired
    private UserService userService;
    
    // ================================
    // 页面路由
    // ================================
    
    @GetMapping
    public String relationList(Model model) {
        try {
            logger.info("访问家长学生关系管理页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "家长学生关系管理");
            model.addAttribute("parents", userService.findParents());
            model.addAttribute("students", userService.findStudents());
            model.addAttribute("relationTypes", getRelationTypes());
            
            return "admin/parent-student-relations/list";
            
        } catch (Exception e) {
            return handlePageError(e, "访问家长学生关系管理页面", model);
        }
    }
    
    @GetMapping("/create")
    public String createRelation(Model model) {
        try {
            logger.info("访问创建关系页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "创建家长学生关系");
            model.addAttribute("action", "create");
            model.addAttribute("parents", userService.findParents());
            model.addAttribute("students", userService.findStudents());
            model.addAttribute("relationTypes", getRelationTypes());
            
            return "admin/parent-student-relations/form";
            
        } catch (Exception e) {
            return handlePageError(e, "访问创建关系页面", model);
        }
    }
    
    @GetMapping("/{relationId}/edit")
    public String editRelation(@PathVariable Long relationId, Model model) {
        try {
            logger.info("访问编辑关系页面: relationId={}", relationId);
            
            Object relation = userService.getParentStudentRelationById(relationId);
            if (relation == null) {
                model.addAttribute("error", "关系不存在");
                return "error/404";
            }
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "编辑家长学生关系");
            model.addAttribute("action", "edit");
            model.addAttribute("relation", relation);
            model.addAttribute("parents", userService.findParents());
            model.addAttribute("students", userService.findStudents());
            model.addAttribute("relationTypes", getRelationTypes());
            
            return "admin/parent-student-relations/form";
            
        } catch (Exception e) {
            return handlePageError(e, "访问编辑关系页面", model);
        }
    }
    
    @GetMapping("/parent/{parentId}")
    public String parentRelations(@PathVariable Long parentId, Model model) {
        try {
            logger.info("访问家长关系页面: parentId={}", parentId);
            
            Object parent = userService.getUserById(parentId);
            if (parent == null) {
                model.addAttribute("error", "家长不存在");
                return "error/404";
            }
            
            Object relations = userService.getRelationsByParent(parentId);
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "家长关系管理");
            model.addAttribute("parent", parent);
            model.addAttribute("relations", relations);
            
            return "admin/parent-student-relations/parent-relations";
            
        } catch (Exception e) {
            return handlePageError(e, "访问家长关系页面", model);
        }
    }
    
    @GetMapping("/student/{studentId}")
    public String studentRelations(@PathVariable Long studentId, Model model) {
        try {
            logger.info("访问学生关系页面: studentId={}", studentId);
            
            Object student = userService.getUserById(studentId);
            if (student == null) {
                model.addAttribute("error", "学生不存在");
                return "error/404";
            }
            
            Object relations = userService.getRelationsByStudent(studentId);
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "学生关系管理");
            model.addAttribute("student", student);
            model.addAttribute("relations", relations);
            
            return "admin/parent-student-relations/student-relations";
            
        } catch (Exception e) {
            return handlePageError(e, "访问学生关系页面", model);
        }
    }
    
    @GetMapping("/statistics")
    public String relationStatistics(Model model) {
        try {
            logger.info("访问关系统计页面");
            
            Object statistics = userService.getParentStudentRelationStatistics();
            Object typeCounts = userService.countParentStudentRelationsByType();
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "关系统计");
            model.addAttribute("statistics", statistics);
            model.addAttribute("typeCounts", typeCounts);
            
            return "admin/parent-student-relations/statistics";
            
        } catch (Exception e) {
            return handlePageError(e, "访问关系统计页面", model);
        }
    }
    
    @GetMapping("/import")
    public String importRelations(Model model) {
        try {
            logger.info("访问关系导入页面");
            
            addCommonAttributes(model);
            model.addAttribute("pageTitle", "关系导入");
            
            return "admin/parent-student-relations/import";
            
        } catch (Exception e) {
            return handlePageError(e, "访问关系导入页面", model);
        }
    }
    
    // ================================
    // 辅助方法
    // ================================
    
    private void addCommonAttributes(Model model) {
        model.addAttribute("currentModule", "parent-student-relations");
        model.addAttribute("breadcrumb", "家长学生关系管理");
    }
    
    private String handlePageError(Exception e, String operation, Model model) {
        logger.error("{}失败", operation, e);
        model.addAttribute("error", operation + "失败: " + e.getMessage());
        return "error/500";
    }
    
    private Object getRelationTypes() {
        // TODO: 实现获取关系类型逻辑
        return new Object();
    }
}
