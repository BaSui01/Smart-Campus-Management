package com.campus.application.service;

import com.campus.application.dto.ImportResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 数据导入导出服务接口
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
public interface DataImportExportService {
    
    /**
     * 导入学生数据
     * 
     * @param file Excel文件
     * @return 导入结果
     */
    ImportResult importStudents(MultipartFile file);
    
    /**
     * 导入教师数据
     * 
     * @param file Excel文件
     * @return 导入结果
     */
    ImportResult importTeachers(MultipartFile file);
    
    /**
     * 导入课程数据
     * 
     * @param file Excel文件
     * @return 导入结果
     */
    ImportResult importCourses(MultipartFile file);
    
    /**
     * 导入成绩数据
     * 
     * @param file Excel文件
     * @return 导入结果
     */
    ImportResult importGrades(MultipartFile file);
    
    /**
     * 导入班级数据
     * 
     * @param file Excel文件
     * @return 导入结果
     */
    ImportResult importClasses(MultipartFile file);
    
    /**
     * 导出学生数据
     * 
     * @param query 查询条件
     * @return Excel文件字节数组
     */
    byte[] exportStudents(Map<String, Object> query);
    
    /**
     * 导出教师数据
     * 
     * @param query 查询条件
     * @return Excel文件字节数组
     */
    byte[] exportTeachers(Map<String, Object> query);
    
    /**
     * 导出课程数据
     * 
     * @param query 查询条件
     * @return Excel文件字节数组
     */
    byte[] exportCourses(Map<String, Object> query);
    
    /**
     * 导出成绩数据
     * 
     * @param query 查询条件
     * @return Excel文件字节数组
     */
    byte[] exportGrades(Map<String, Object> query);
    
    /**
     * 导出班级数据
     * 
     * @param query 查询条件
     * @return Excel文件字节数组
     */
    byte[] exportClasses(Map<String, Object> query);
    
    /**
     * 导出考勤数据
     * 
     * @param query 查询条件
     * @return Excel文件字节数组
     */
    byte[] exportAttendance(Map<String, Object> query);
    
    /**
     * 获取导入模板
     * 
     * @param templateType 模板类型
     * @return Excel模板文件字节数组
     */
    byte[] getImportTemplate(String templateType);
    
    /**
     * 验证导入文件格式
     * 
     * @param file 文件
     * @param templateType 模板类型
     * @return 验证结果
     */
    ImportResult validateImportFile(MultipartFile file, String templateType);
    
    /**
     * 获取导入任务状态
     * 
     * @param taskId 任务ID
     * @return 任务状态
     */
    ImportResult getImportTaskStatus(String taskId);
}
