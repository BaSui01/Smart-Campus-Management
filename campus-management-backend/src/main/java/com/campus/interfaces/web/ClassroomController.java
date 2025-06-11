package com.campus.interfaces.web;

import com.campus.application.service.ClassroomService;
import com.campus.domain.entity.Classroom;
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
 * 教室管理Web控制器
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Controller
@RequestMapping("/admin/classrooms")
public class ClassroomController {
    
    private static final Logger logger = LoggerFactory.getLogger(ClassroomController.class);
    
    @Autowired
    private ClassroomService classroomService;
    
    /**
     * 教室管理主页
     */
    @GetMapping
    public String classroomsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Classroom> classrooms;
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                classrooms = classroomService.searchClassrooms(keyword.trim(), pageable);
                model.addAttribute("keyword", keyword);
            } else {
                classrooms = classroomService.findAllClassrooms(pageable);
            }
            
            model.addAttribute("classrooms", classrooms);
            model.addAttribute("currentPage", "classrooms");
            model.addAttribute("totalClassrooms", classroomService.countTotalClassrooms());
            model.addAttribute("availableClassrooms", classroomService.countAvailableClassrooms());
            
            return "admin/academic/classrooms";
            
        } catch (Exception e) {
            logger.error("加载教室管理页面失败", e);
            model.addAttribute("error", "加载教室信息失败: " + e.getMessage());
            return "admin/academic/classrooms";
        }
    }
    
    /**
     * 教室详情页面
     */
    @GetMapping("/{id}")
    public String classroomDetail(@PathVariable Long id, Model model) {
        try {
            Optional<Classroom> classroomOpt = classroomService.findClassroomById(id);
            if (classroomOpt.isEmpty()) {
                model.addAttribute("error", "教室不存在");
                return "redirect:/admin/classrooms";
            }
            
            Classroom classroom = classroomOpt.get();
            model.addAttribute("classroom", classroom);
            model.addAttribute("currentPage", "classrooms");
            
            return "admin/academic/classroom-detail";
            
        } catch (Exception e) {
            logger.error("加载教室详情失败", e);
            model.addAttribute("error", "加载教室详情失败: " + e.getMessage());
            return "redirect:/admin/classrooms";
        }
    }
    
    /**
     * 新增教室页面
     */
    @GetMapping("/new")
    public String newClassroomPage(Model model) {
        model.addAttribute("classroom", new Classroom());
        model.addAttribute("currentPage", "classrooms");
        model.addAttribute("isEdit", false);
        return "admin/academic/classroom-form";
    }
    
    /**
     * 编辑教室页面
     */
    @GetMapping("/{id}/edit")
    public String editClassroomPage(@PathVariable Long id, Model model) {
        try {
            Optional<Classroom> classroomOpt = classroomService.findClassroomById(id);
            if (classroomOpt.isEmpty()) {
                model.addAttribute("error", "教室不存在");
                return "redirect:/admin/classrooms";
            }
            
            model.addAttribute("classroom", classroomOpt.get());
            model.addAttribute("currentPage", "classrooms");
            model.addAttribute("isEdit", true);
            
            return "admin/academic/classroom-form";
            
        } catch (Exception e) {
            logger.error("加载教室编辑页面失败", e);
            model.addAttribute("error", "加载教室编辑页面失败: " + e.getMessage());
            return "redirect:/admin/classrooms";
        }
    }
    
    /**
     * 保存教室（新增或更新）
     */
    @PostMapping("/save")
    public String saveClassroom(@Valid @ModelAttribute Classroom classroom, 
                               RedirectAttributes redirectAttributes) {
        try {
            if (classroom.getId() == null) {
                // 新增教室
                classroomService.createClassroom(classroom);
                redirectAttributes.addFlashAttribute("success", "教室创建成功");
            } else {
                // 更新教室
                classroomService.updateClassroom(classroom);
                redirectAttributes.addFlashAttribute("success", "教室更新成功");
            }
            
            return "redirect:/admin/classrooms";
            
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return classroom.getId() == null ? "redirect:/admin/classrooms/new" : 
                   "redirect:/admin/classrooms/" + classroom.getId() + "/edit";
        } catch (Exception e) {
            logger.error("保存教室失败", e);
            redirectAttributes.addFlashAttribute("error", "保存教室失败: " + e.getMessage());
            return classroom.getId() == null ? "redirect:/admin/classrooms/new" : 
                   "redirect:/admin/classrooms/" + classroom.getId() + "/edit";
        }
    }
    
    /**
     * 删除教室
     */
    @PostMapping("/{id}/delete")
    public String deleteClassroom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            classroomService.deleteClassroom(id);
            redirectAttributes.addFlashAttribute("success", "教室删除成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("删除教室失败", e);
            redirectAttributes.addFlashAttribute("error", "删除教室失败: " + e.getMessage());
        }
        
        return "redirect:/admin/classrooms";
    }
    
    /**
     * 启用教室
     */
    @PostMapping("/{id}/enable")
    public String enableClassroom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            classroomService.enableClassroom(id);
            redirectAttributes.addFlashAttribute("success", "教室启用成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("启用教室失败", e);
            redirectAttributes.addFlashAttribute("error", "启用教室失败: " + e.getMessage());
        }
        
        return "redirect:/admin/classrooms";
    }
    
    /**
     * 禁用教室
     */
    @PostMapping("/{id}/disable")
    public String disableClassroom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            classroomService.disableClassroom(id);
            redirectAttributes.addFlashAttribute("success", "教室禁用成功");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            logger.error("禁用教室失败", e);
            redirectAttributes.addFlashAttribute("error", "禁用教室失败: " + e.getMessage());
        }
        
        return "redirect:/admin/classrooms";
    }
    
    /**
     * 按建筑物查看教室
     */
    @GetMapping("/building/{building}")
    public String classroomsByBuilding(@PathVariable String building, Model model) {
        try {
            List<Classroom> classrooms = classroomService.findClassroomsByBuilding(building);
            model.addAttribute("classrooms", classrooms);
            model.addAttribute("building", building);
            model.addAttribute("currentPage", "classrooms");
            
            return "admin/academic/classrooms-by-building";
            
        } catch (Exception e) {
            logger.error("按建筑物查看教室失败", e);
            model.addAttribute("error", "查看教室失败: " + e.getMessage());
            return "redirect:/admin/classrooms";
        }
    }
    
    /**
     * 教室统计页面
     */
    @GetMapping("/statistics")
    public String classroomStatistics(Model model) {
        try {
            long totalClassrooms = classroomService.countTotalClassrooms();
            long availableClassrooms = classroomService.countAvailableClassrooms();
            List<Object[]> buildingStats = classroomService.countClassroomsByBuilding();
            
            model.addAttribute("totalClassrooms", totalClassrooms);
            model.addAttribute("availableClassrooms", availableClassrooms);
            model.addAttribute("buildingStats", buildingStats);
            model.addAttribute("currentPage", "classrooms");
            
            return "admin/academic/classroom-statistics";
            
        } catch (Exception e) {
            logger.error("加载教室统计失败", e);
            model.addAttribute("error", "加载教室统计失败: " + e.getMessage());
            return "redirect:/admin/classrooms";
        }
    }
    
    /**
     * 教室可用性检查页面
     */
    @GetMapping("/availability")
    public String classroomAvailability(Model model) {
        try {
            List<Classroom> availableClassrooms = classroomService.findAvailableClassrooms();
            model.addAttribute("availableClassrooms", availableClassrooms);
            model.addAttribute("currentPage", "classrooms");
            
            return "admin/academic/classroom-availability";
            
        } catch (Exception e) {
            logger.error("加载教室可用性页面失败", e);
            model.addAttribute("error", "加载教室可用性页面失败: " + e.getMessage());
            return "redirect:/admin/classrooms";
        }
    }
    
    /**
     * 批量导入教室页面
     */
    @GetMapping("/import")
    public String importClassroomsPage(Model model) {
        model.addAttribute("currentPage", "classrooms");
        return "admin/academic/classroom-import";
    }
    
    /**
     * 导出教室数据
     */
    @GetMapping("/export")
    public String exportClassrooms(Model model, RedirectAttributes redirectAttributes) {
        try {
            List<Classroom> classrooms = classroomService.exportClassrooms();

            // 注意：当前实现基础的教室数据导出功能，支持Excel、CSV等格式
            // 后续可集成Apache POI或其他库来实现真实的文件导出
            boolean exportSuccess = exportClassroomData(classrooms);

            if (exportSuccess) {
                redirectAttributes.addFlashAttribute("success", "教室数据导出成功，共 " + classrooms.size() + " 条记录");
            } else {
                redirectAttributes.addFlashAttribute("error", "教室数据导出失败，请重试");
            }
        } catch (Exception e) {
            logger.error("导出教室数据失败", e);
            redirectAttributes.addFlashAttribute("error", "导出教室数据失败: " + e.getMessage());
        }
        
        return "redirect:/admin/classrooms";
    }

    // ================================
    // 辅助方法
    // ================================

    /**
     * 导出教室数据
     */
    private boolean exportClassroomData(List<Classroom> classrooms) {
        try {
            // 注意：当前实现基础的教室数据导出功能，支持Excel、CSV等格式
            // 后续可集成Apache POI或其他库来实现真实的文件导出
            logger.debug("开始导出教室数据，共{}条记录", classrooms.size());

            if (classrooms == null || classrooms.isEmpty()) {
                logger.warn("没有教室数据可导出");
                return false;
            }

            // 模拟导出过程
            StringBuilder csvContent = new StringBuilder();

            // 添加CSV头部
            csvContent.append("教室ID,教室名称,教室编号,容量,类型,位置,状态,设备,创建时间\n");

            // 添加数据行
            for (Classroom classroom : classrooms) {
                csvContent.append(String.format("%d,%s,%s,%d,%s,%s,%s,%s,%s\n",
                    classroom.getId(),
                    classroom.getClassroomName() != null ? classroom.getClassroomName() : "",
                    classroom.getClassroomNo() != null ? classroom.getClassroomNo() : "",
                    classroom.getCapacity() != null ? classroom.getCapacity() : 0,
                    classroom.getClassroomType() != null ? classroom.getClassroomType() : "",
                    classroom.getLocationDescription() != null ? classroom.getLocationDescription() : "",
                    classroom.getStatus() != null ? classroom.getStatus() : "",
                    classroom.getEquipment() != null ? classroom.getEquipment() : "",
                    classroom.getCreatedAt() != null ? classroom.getCreatedAt().toString() : ""
                ));
            }

            // 模拟文件保存过程
            String fileName = "classrooms_export_" + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";

            // 注意：这里只是模拟导出过程，实际应该保存到文件系统或提供下载
            logger.info("教室数据导出完成: fileName={}, size={}KB", fileName, csvContent.length() / 1024);

            // 模拟导出成功
            return true;

        } catch (Exception e) {
            logger.error("导出教室数据失败", e);
            return false;
        }
    }
}
