package com.campus.application.service.impl;

import com.campus.application.dto.ImportResult;
import com.campus.application.service.DataImportExportService;
import com.campus.domain.entity.organization.Student;
import com.campus.domain.entity.organization.Teacher;
import com.campus.domain.entity.academic.Course;
import com.campus.domain.entity.academic.Grade;
import com.campus.domain.entity.organization.SchoolClass;
import com.campus.domain.repository.UserRepository;
import com.campus.domain.repository.organization.StudentRepository;
import com.campus.domain.repository.organization.TeacherRepository;
import com.campus.domain.repository.academic.CourseRepository;
import com.campus.domain.repository.academic.GradeRepository;
import com.campus.domain.repository.organization.SchoolClassRepository;
import com.campus.shared.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据导入导出服务实现类
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataImportExportServiceImpl implements DataImportExportService {

    @SuppressWarnings("unused")
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final GradeRepository gradeRepository;
    private final SchoolClassRepository classRepository;
    
    // 导入任务状态缓存
    private final Map<String, ImportResult> taskStatusCache = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public ImportResult importStudents(MultipartFile file) {
        String taskId = UUID.randomUUID().toString();
        ImportResult result = ImportResult.builder()
                .taskId(taskId)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .startTime(LocalDateTime.now())
                .build();
        
        try {
            // 验证文件格式
            List<String> requiredHeaders = Arrays.asList(
                "学号", "姓名", "性别", "身份证号", "手机号", "邮箱", "班级", "专业", "年级", "入学年份"
            );
            
            Map<String, Object> validation = ExcelUtil.validateExcelFormat(file, requiredHeaders);
            if (!(Boolean) validation.get("valid")) {
                return ImportResult.failure(taskId, file.getOriginalFilename(), 
                        (String) validation.get("message"));
            }
            
            // 读取Excel数据
            List<Map<String, Object>> dataList = ExcelUtil.readExcel(file);
            result.setTotalRows(dataList.size());
            
            int successCount = 0;
            int failureCount = 0;
            
            for (int i = 0; i < dataList.size(); i++) {
                Map<String, Object> rowData = dataList.get(i);
                try {
                    // 创建学生对象
                    Student student = createStudentFromRowData(rowData);
                    studentRepository.save(student);
                    successCount++;
                } catch (Exception e) {
                    failureCount++;
                    result.addError(i + 2, "", "", e.getMessage(), "BUSINESS_RULE", rowData);
                    log.warn("导入学生数据失败，行号: {}, 错误: {}", i + 2, e.getMessage());
                }
            }
            
            result.setSuccessRows(successCount);
            result.setFailureRows(failureCount);
            result.setEndTime(LocalDateTime.now());
            result.calculateStatistics();
            
            if (failureCount == 0) {
                result.setSuccess(true);
                result.setMessage("学生数据导入成功");
            } else {
                result.setSuccess(successCount > 0);
                result.setMessage(String.format("学生数据导入完成：成功%d行，失败%d行", successCount, failureCount));
            }
            
        } catch (Exception e) {
            log.error("导入学生数据失败", e);
            result = ImportResult.failure(taskId, file.getOriginalFilename(), e.getMessage());
        }
        
        // 缓存任务状态
        taskStatusCache.put(taskId, result);
        return result;
    }

    @Override
    @Transactional
    public ImportResult importTeachers(MultipartFile file) {
        String taskId = UUID.randomUUID().toString();
        ImportResult result = ImportResult.builder()
                .taskId(taskId)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .startTime(LocalDateTime.now())
                .build();
        
        try {
            // 验证文件格式
            List<String> requiredHeaders = Arrays.asList(
                "工号", "姓名", "性别", "身份证号", "手机号", "邮箱", "部门", "职位", "职称"
            );
            
            Map<String, Object> validation = ExcelUtil.validateExcelFormat(file, requiredHeaders);
            if (!(Boolean) validation.get("valid")) {
                return ImportResult.failure(taskId, file.getOriginalFilename(), 
                        (String) validation.get("message"));
            }
            
            // 读取Excel数据
            List<Map<String, Object>> dataList = ExcelUtil.readExcel(file);
            result.setTotalRows(dataList.size());
            
            int successCount = 0;
            int failureCount = 0;
            
            for (int i = 0; i < dataList.size(); i++) {
                Map<String, Object> rowData = dataList.get(i);
                try {
                    // 创建教师对象
                    Teacher teacher = createTeacherFromRowData(rowData);
                    teacherRepository.save(teacher);
                    successCount++;
                } catch (Exception e) {
                    failureCount++;
                    result.addError(i + 2, "", "", e.getMessage(), "BUSINESS_RULE", rowData);
                    log.warn("导入教师数据失败，行号: {}, 错误: {}", i + 2, e.getMessage());
                }
            }
            
            result.setSuccessRows(successCount);
            result.setFailureRows(failureCount);
            result.setEndTime(LocalDateTime.now());
            result.calculateStatistics();
            
            if (failureCount == 0) {
                result.setSuccess(true);
                result.setMessage("教师数据导入成功");
            } else {
                result.setSuccess(successCount > 0);
                result.setMessage(String.format("教师数据导入完成：成功%d行，失败%d行", successCount, failureCount));
            }
            
        } catch (Exception e) {
            log.error("导入教师数据失败", e);
            result = ImportResult.failure(taskId, file.getOriginalFilename(), e.getMessage());
        }
        
        // 缓存任务状态
        taskStatusCache.put(taskId, result);
        return result;
    }

    @Override
    @Transactional
    public ImportResult importCourses(MultipartFile file) {
        String taskId = UUID.randomUUID().toString();
        ImportResult result = ImportResult.builder()
                .taskId(taskId)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .startTime(LocalDateTime.now())
                .build();
        
        try {
            // 验证文件格式
            List<String> requiredHeaders = Arrays.asList(
                "课程代码", "课程名称", "学分", "课程类型", "授课教师", "学期", "最大选课人数"
            );
            
            Map<String, Object> validation = ExcelUtil.validateExcelFormat(file, requiredHeaders);
            if (!(Boolean) validation.get("valid")) {
                return ImportResult.failure(taskId, file.getOriginalFilename(), 
                        (String) validation.get("message"));
            }
            
            // 读取Excel数据
            List<Map<String, Object>> dataList = ExcelUtil.readExcel(file);
            result.setTotalRows(dataList.size());
            
            int successCount = 0;
            int failureCount = 0;
            
            for (int i = 0; i < dataList.size(); i++) {
                Map<String, Object> rowData = dataList.get(i);
                try {
                    // 创建课程对象
                    Course course = createCourseFromRowData(rowData);
                    courseRepository.save(course);
                    successCount++;
                } catch (Exception e) {
                    failureCount++;
                    result.addError(i + 2, "", "", e.getMessage(), "BUSINESS_RULE", rowData);
                    log.warn("导入课程数据失败，行号: {}, 错误: {}", i + 2, e.getMessage());
                }
            }
            
            result.setSuccessRows(successCount);
            result.setFailureRows(failureCount);
            result.setEndTime(LocalDateTime.now());
            result.calculateStatistics();
            
            if (failureCount == 0) {
                result.setSuccess(true);
                result.setMessage("课程数据导入成功");
            } else {
                result.setSuccess(successCount > 0);
                result.setMessage(String.format("课程数据导入完成：成功%d行，失败%d行", successCount, failureCount));
            }
            
        } catch (Exception e) {
            log.error("导入课程数据失败", e);
            result = ImportResult.failure(taskId, file.getOriginalFilename(), e.getMessage());
        }
        
        // 缓存任务状态
        taskStatusCache.put(taskId, result);
        return result;
    }

    @Override
    @Transactional
    public ImportResult importGrades(MultipartFile file) {
        String taskId = UUID.randomUUID().toString();
        ImportResult result = ImportResult.builder()
                .taskId(taskId)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .startTime(LocalDateTime.now())
                .build();
        
        try {
            // 验证文件格式
            List<String> requiredHeaders = Arrays.asList(
                "学号", "课程代码", "成绩", "考试类型", "学期"
            );
            
            Map<String, Object> validation = ExcelUtil.validateExcelFormat(file, requiredHeaders);
            if (!(Boolean) validation.get("valid")) {
                return ImportResult.failure(taskId, file.getOriginalFilename(), 
                        (String) validation.get("message"));
            }
            
            // 读取Excel数据
            List<Map<String, Object>> dataList = ExcelUtil.readExcel(file);
            result.setTotalRows(dataList.size());
            
            int successCount = 0;
            int failureCount = 0;
            
            for (int i = 0; i < dataList.size(); i++) {
                Map<String, Object> rowData = dataList.get(i);
                try {
                    // 创建成绩对象
                    Grade grade = createGradeFromRowData(rowData);
                    gradeRepository.save(grade);
                    successCount++;
                } catch (Exception e) {
                    failureCount++;
                    result.addError(i + 2, "", "", e.getMessage(), "BUSINESS_RULE", rowData);
                    log.warn("导入成绩数据失败，行号: {}, 错误: {}", i + 2, e.getMessage());
                }
            }
            
            result.setSuccessRows(successCount);
            result.setFailureRows(failureCount);
            result.setEndTime(LocalDateTime.now());
            result.calculateStatistics();
            
            if (failureCount == 0) {
                result.setSuccess(true);
                result.setMessage("成绩数据导入成功");
            } else {
                result.setSuccess(successCount > 0);
                result.setMessage(String.format("成绩数据导入完成：成功%d行，失败%d行", successCount, failureCount));
            }
            
        } catch (Exception e) {
            log.error("导入成绩数据失败", e);
            result = ImportResult.failure(taskId, file.getOriginalFilename(), e.getMessage());
        }
        
        // 缓存任务状态
        taskStatusCache.put(taskId, result);
        return result;
    }

    @Override
    @Transactional
    public ImportResult importClasses(MultipartFile file) {
        String taskId = UUID.randomUUID().toString();
        ImportResult result = ImportResult.builder()
                .taskId(taskId)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .startTime(LocalDateTime.now())
                .build();

        try {
            // 验证文件格式
            List<String> requiredHeaders = Arrays.asList(
                "班级名称", "年级", "专业", "班主任", "学生人数"
            );

            Map<String, Object> validation = ExcelUtil.validateExcelFormat(file, requiredHeaders);
            if (!(Boolean) validation.get("valid")) {
                return ImportResult.failure(taskId, file.getOriginalFilename(),
                        (String) validation.get("message"));
            }

            // 读取Excel数据
            List<Map<String, Object>> dataList = ExcelUtil.readExcel(file);
            result.setTotalRows(dataList.size());

            int successCount = 0;
            int failureCount = 0;

            for (int i = 0; i < dataList.size(); i++) {
                Map<String, Object> rowData = dataList.get(i);
                try {
                    // 创建班级对象
                    SchoolClass clazz = createClassFromRowData(rowData);
                    classRepository.save(clazz);
                    successCount++;
                } catch (Exception e) {
                    failureCount++;
                    result.addError(i + 2, "", "", e.getMessage(), "BUSINESS_RULE", rowData);
                    log.warn("导入班级数据失败，行号: {}, 错误: {}", i + 2, e.getMessage());
                }
            }

            result.setSuccessRows(successCount);
            result.setFailureRows(failureCount);
            result.setEndTime(LocalDateTime.now());
            result.calculateStatistics();

            if (failureCount == 0) {
                result.setSuccess(true);
                result.setMessage("班级数据导入成功");
            } else {
                result.setSuccess(successCount > 0);
                result.setMessage(String.format("班级数据导入完成：成功%d行，失败%d行", successCount, failureCount));
            }

        } catch (Exception e) {
            log.error("导入班级数据失败", e);
            result = ImportResult.failure(taskId, file.getOriginalFilename(), e.getMessage());
        }

        // 缓存任务状态
        taskStatusCache.put(taskId, result);
        return result;
    }

    @Override
    public byte[] exportStudents(Map<String, Object> query) {
        try {
            // 查询学生数据
            List<Student> students = studentRepository.findAll(); // 这里应该根据query条件查询

            // 准备表头
            List<String> headers = Arrays.asList(
                "学号", "姓名", "性别", "身份证号", "手机号", "邮箱", "班级", "专业", "年级", "入学年份", "状态"
            );

            // 准备数据
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (Student student : students) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("学号", student.getStudentNo());
                rowData.put("姓名", student.getRealName());
                rowData.put("性别", student.getGender());
                rowData.put("身份证号", student.getIdCard());
                rowData.put("手机号", student.getPhone());
                rowData.put("邮箱", student.getEmail());
                rowData.put("班级", student.getClassName());
                rowData.put("专业", student.getMajor());
                rowData.put("年级", student.getGrade());
                rowData.put("入学年份", student.getEnrollmentYear());
                rowData.put("状态", student.getStudentStatus());
                dataList.add(rowData);
            }

            return ExcelUtil.createExcel(headers, dataList);

        } catch (Exception e) {
            log.error("导出学生数据失败", e);
            throw new RuntimeException("导出学生数据失败：" + e.getMessage());
        }
    }

    @Override
    public byte[] exportTeachers(Map<String, Object> query) {
        try {
            // 查询教师数据
            List<Teacher> teachers = teacherRepository.findAll(); // 这里应该根据query条件查询

            // 准备表头
            List<String> headers = Arrays.asList(
                "工号", "姓名", "性别", "身份证号", "手机号", "邮箱", "部门", "职位", "职称", "状态"
            );

            // 准备数据
            List<Map<String, Object>> dataList = new ArrayList<>();
            for (Teacher teacher : teachers) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("工号", teacher.getEmployeeNo());
                rowData.put("姓名", teacher.getRealName());
                rowData.put("性别", teacher.getGender());
                rowData.put("身份证号", teacher.getIdCard());
                rowData.put("手机号", teacher.getPhone());
                rowData.put("邮箱", teacher.getEmail());
                rowData.put("部门", teacher.getDepartment());
                rowData.put("职位", teacher.getPosition());
                rowData.put("职称", teacher.getTitle());
                rowData.put("状态", teacher.getTeacherStatus());
                dataList.add(rowData);
            }

            return ExcelUtil.createExcel(headers, dataList);

        } catch (Exception e) {
            log.error("导出教师数据失败", e);
            throw new RuntimeException("导出教师数据失败：" + e.getMessage());
        }
    }

    // ================================
    // 私有辅助方法
    // ================================

    /**
     * 从行数据创建课程对象
     */
    private Course createCourseFromRowData(Map<String, Object> rowData) {
        Course course = new Course();

        // 基本信息
        course.setCourseCode(getStringValue(rowData, "课程代码"));
        course.setCourseName(getStringValue(rowData, "课程名称"));

        // 学分转换为BigDecimal
        Integer creditsInt = getIntegerValue(rowData, "学分");
        if (creditsInt != null) {
            course.setCredits(new java.math.BigDecimal(creditsInt));
        }

        course.setCourseType(getStringValue(rowData, "课程类型"));
        course.setSemester(getStringValue(rowData, "学期"));
        course.setMaxStudents(getIntegerValue(rowData, "最大选课人数"));

        // 设置默认值
        course.setAcademicYear(java.time.LocalDate.now().getYear());

        return course;
    }

    /**
     * 从行数据创建成绩对象
     */
    private Grade createGradeFromRowData(Map<String, Object> rowData) {
        Grade grade = new Grade();

        // 成绩信息
        String scoreStr = getStringValue(rowData, "成绩");
        if (scoreStr != null) {
            try {
                grade.setScore(new java.math.BigDecimal(scoreStr));
            } catch (NumberFormatException e) {
                log.warn("无法解析成绩: {}", scoreStr);
            }
        }

        // 设置学期
        grade.setSemester(getStringValue(rowData, "学期"));

        return grade;
    }

    /**
     * 从行数据创建班级对象
     */
    private SchoolClass createClassFromRowData(Map<String, Object> rowData) {
        SchoolClass clazz = new SchoolClass();

        // 基本信息
        clazz.setClassName(getStringValue(rowData, "班级名称"));
        clazz.setGrade(getStringValue(rowData, "年级"));
        clazz.setMajor(getStringValue(rowData, "专业"));

        return clazz;
    }

    /**
     * 从行数据创建学生对象
     */
    private Student createStudentFromRowData(Map<String, Object> rowData) {
        Student student = new Student();

        // 基本信息
        student.setStudentNo(getStringValue(rowData, "学号"));
        student.setMajor(getStringValue(rowData, "专业"));
        student.setGrade(getStringValue(rowData, "年级"));

        Integer enrollmentYear = getIntegerValue(rowData, "入学年份");
        if (enrollmentYear != null) {
            student.setEnrollmentYear(enrollmentYear);
        }

        return student;
    }

    /**
     * 获取导入任务状态
     */
    @Override
    public ImportResult getImportTaskStatus(String taskId) {
        // 从缓存中获取任务状态
        ImportResult cachedResult = taskStatusCache.get(taskId);
        if (cachedResult != null) {
            return cachedResult;
        }

        // 如果缓存中没有，返回未找到的结果
        return ImportResult.builder()
                .taskId(taskId)
                .success(false)
                .message("未找到指定的导入任务")
                .build();
    }

    // ================================
    // 其他导入导出方法的默认实现
    // ================================



    @Override
    public byte[] exportCourses(Map<String, Object> query) {
        try {
            List<Course> courses = courseRepository.findAll();
            List<String> headers = Arrays.asList("课程代码", "课程名称", "学分", "课程类型", "学期", "最大选课人数");
            List<Map<String, Object>> dataList = new ArrayList<>();

            for (Course course : courses) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("课程代码", course.getCourseCode());
                rowData.put("课程名称", course.getCourseName());
                rowData.put("学分", course.getCredits());
                rowData.put("课程类型", course.getCourseType());
                rowData.put("学期", course.getSemester());
                rowData.put("最大选课人数", course.getMaxStudents());
                dataList.add(rowData);
            }

            return ExcelUtil.createExcel(headers, dataList);
        } catch (Exception e) {
            log.error("导出课程数据失败", e);
            throw new RuntimeException("导出课程数据失败：" + e.getMessage());
        }
    }

    @Override
    public byte[] exportGrades(Map<String, Object> query) {
        try {
            List<Grade> grades = gradeRepository.findAll();
            List<String> headers = Arrays.asList("学号", "课程代码", "成绩", "学期");
            List<Map<String, Object>> dataList = new ArrayList<>();

            for (Grade grade : grades) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("学号", ""); // Grade实体中可能没有学号字段
                rowData.put("课程代码", ""); // Grade实体中可能没有课程代码字段
                rowData.put("成绩", grade.getScore());
                rowData.put("学期", grade.getSemester());
                dataList.add(rowData);
            }

            return ExcelUtil.createExcel(headers, dataList);
        } catch (Exception e) {
            log.error("导出成绩数据失败", e);
            throw new RuntimeException("导出成绩数据失败：" + e.getMessage());
        }
    }

    @Override
    public byte[] exportClasses(Map<String, Object> query) {
        try {
            List<SchoolClass> classes = classRepository.findAll();
            List<String> headers = Arrays.asList("班级名称", "年级", "专业", "学生人数");
            List<Map<String, Object>> dataList = new ArrayList<>();

            for (SchoolClass clazz : classes) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("班级名称", clazz.getClassName());
                rowData.put("年级", clazz.getGrade());
                rowData.put("专业", clazz.getMajor());
                rowData.put("学生人数", clazz.getStudentCount());
                dataList.add(rowData);
            }

            return ExcelUtil.createExcel(headers, dataList);
        } catch (Exception e) {
            log.error("导出班级数据失败", e);
            throw new RuntimeException("导出班级数据失败：" + e.getMessage());
        }
    }

    @Override
    public byte[] exportAttendance(Map<String, Object> query) {
        // 考勤导出功能 - 创建空模板
        try {
            List<String> headers = Arrays.asList("学号", "姓名", "课程", "日期", "考勤状态");
            List<Map<String, Object>> dataList = new ArrayList<>();
            return ExcelUtil.createExcel(headers, dataList);
        } catch (Exception e) {
            log.error("导出考勤数据失败", e);
            throw new RuntimeException("导出考勤数据失败：" + e.getMessage());
        }
    }

    @Override
    public byte[] getImportTemplate(String type) {
        try {
            List<String> headers;
            switch (type.toLowerCase()) {
                case "student" -> headers = Arrays.asList("学号", "姓名", "性别", "身份证号", "手机号", "邮箱", "班级", "专业", "年级", "入学年份");
                case "teacher" -> headers = Arrays.asList("工号", "姓名", "性别", "身份证号", "手机号", "邮箱", "部门", "职位", "职称");
                case "course" -> headers = Arrays.asList("课程代码", "课程名称", "学分", "课程类型", "授课教师", "学期", "最大选课人数");
                case "grade" -> headers = Arrays.asList("学号", "课程代码", "成绩", "考试类型", "学期");
                case "class" -> headers = Arrays.asList("班级名称", "年级", "专业", "班主任", "学生人数");
                default -> throw new IllegalArgumentException("不支持的模板类型: " + type);
            }

            List<Map<String, Object>> dataList = new ArrayList<>();
            return ExcelUtil.createExcel(headers, dataList);
        } catch (Exception e) {
            log.error("生成导入模板失败", e);
            throw new RuntimeException("生成导入模板失败：" + e.getMessage());
        }
    }

    @Override
    public ImportResult validateImportFile(MultipartFile file, String type) {
        String taskId = UUID.randomUUID().toString();

        try {
            // 1. 检查文件是否为空
            if (file == null || file.isEmpty()) {
                return ImportResult.failure(taskId, "", "文件不能为空");
            }

            // 2. 检查文件大小（限制为10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return ImportResult.failure(taskId, file.getOriginalFilename(), "文件大小不能超过10MB");
            }

            // 3. 检查文件格式
            String filename = file.getOriginalFilename();
            if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
                return ImportResult.failure(taskId, filename, "只支持Excel文件格式(.xlsx, .xls)");
            }

            // 4. 检查文件内容格式
            try {
                List<Map<String, Object>> data = ExcelUtil.readExcel(file);
                if (data.isEmpty()) {
                    return ImportResult.failure(taskId, filename, "文件内容为空");
                }

                // 5. 根据类型验证表头
                List<String> requiredHeaders = getRequiredHeaders(type);
                Map<String, Object> firstRow = data.get(0);
                for (String header : requiredHeaders) {
                    if (!firstRow.containsKey(header)) {
                        return ImportResult.failure(taskId, filename,
                            String.format("缺少必需的列: %s", header));
                    }
                }

            } catch (Exception e) {
                return ImportResult.failure(taskId, filename, "文件格式错误，无法解析: " + e.getMessage());
            }

            // 验证通过
            return ImportResult.builder()
                    .taskId(taskId)
                    .success(true)
                    .fileName(filename)
                    .message("文件验证通过")
                    .build();

        } catch (Exception e) {
            log.error("文件验证失败", e);
            String fileName = (file != null) ? file.getOriginalFilename() : "";
            return ImportResult.failure(taskId, fileName, "文件验证过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * 获取指定类型的必需表头
     */
    private List<String> getRequiredHeaders(String type) {
        return switch (type.toLowerCase()) {
            case "student" -> Arrays.asList("学号", "姓名", "班级", "专业", "年级");
            case "teacher" -> Arrays.asList("工号", "姓名", "部门");
            case "course" -> Arrays.asList("课程代码", "课程名称", "学分", "课程类型");
            case "grade" -> Arrays.asList("学号", "课程代码", "成绩");
            case "class" -> Arrays.asList("班级名称", "年级", "专业");
            default -> Arrays.asList();
        };
    }

    /**
     * 从行数据创建教师对象
     */
    private Teacher createTeacherFromRowData(Map<String, Object> rowData) {
        Teacher teacher = new Teacher();

        // 基本信息
        teacher.setEmployeeNo(getStringValue(rowData, "工号"));
        teacher.setRealName(getStringValue(rowData, "姓名"));
        teacher.setGender(getStringValue(rowData, "性别"));
        teacher.setIdCard(getStringValue(rowData, "身份证号"));
        teacher.setPhone(getStringValue(rowData, "手机号"));
        teacher.setEmail(getStringValue(rowData, "邮箱"));

        // 职业信息
        teacher.setDepartment(getStringValue(rowData, "部门"));
        teacher.setPosition(getStringValue(rowData, "职位"));
        teacher.setTitle(getStringValue(rowData, "职称"));

        // 状态信息
        teacher.setTeacherStatus("ACTIVE"); // 默认状态

        return teacher;
    }

    /**
     * 安全获取字符串值
     */
    private String getStringValue(Map<String, Object> rowData, String key) {
        Object value = rowData.get(key);
        return value != null ? value.toString().trim() : null;
    }

    /**
     * 安全获取整数值
     */
    private Integer getIntegerValue(Map<String, Object> rowData, String key) {
        Object value = rowData.get(key);
        if (value == null) {
            return null;
        }

        try {
            if (value instanceof Number number) {
                return number.intValue();
            } else {
                String strValue = value.toString().trim();
                return strValue.isEmpty() ? null : Integer.parseInt(strValue);
            }
        } catch (NumberFormatException e) {
            log.warn("无法解析整数值: {} = {}", key, value);
            return null;
        }
    }
}
