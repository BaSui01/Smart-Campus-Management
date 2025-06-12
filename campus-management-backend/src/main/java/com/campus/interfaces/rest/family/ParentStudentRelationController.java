package com.campus.interfaces.rest.family;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.campus.application.service.auth.UserService;

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
        try {
            // 注意：当前实现基础的关系类型列表，提供常见的家长与学生关系类型
            // 后续可从数据库或配置文件中动态获取关系类型配置
            logger.debug("获取关系类型列表");

            java.util.List<java.util.Map<String, Object>> relationTypes = new java.util.ArrayList<>();

            // 定义关系类型
            String[] typeNames = {"父亲", "母亲", "监护人", "祖父", "祖母", "外祖父", "外祖母", "其他亲属"};
            String[] typeKeys = {"father", "mother", "guardian", "grandfather", "grandmother", "maternal_grandfather", "maternal_grandmother", "other"};
            String[] typeDescriptions = {
                "学生的父亲",
                "学生的母亲",
                "学生的法定监护人",
                "学生的祖父",
                "学生的祖母",
                "学生的外祖父",
                "学生的外祖母",
                "学生的其他亲属"
            };

            for (int i = 0; i < typeNames.length; i++) {
                java.util.Map<String, Object> type = new java.util.HashMap<>();
                type.put("name", typeNames[i]);
                type.put("key", typeKeys[i]);
                type.put("description", typeDescriptions[i]);
                type.put("enabled", true);
                type.put("priority", i < 3 ? "high" : "normal"); // 父母监护人为高优先级
                relationTypes.add(type);
            }

            logger.debug("关系类型列表获取完成，共{}种类型", relationTypes.size());
            return relationTypes;

        } catch (Exception e) {
            logger.error("获取关系类型列表失败", e);
            return new java.util.ArrayList<>();
        }
    }
}
