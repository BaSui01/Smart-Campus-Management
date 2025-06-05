package com.campus.controller.admin;

import com.campus.common.ApiResponse;
import com.campus.entity.Student;
import com.campus.entity.SchoolClass;
import com.campus.service.StudentService;
import com.campus.service.SchoolClassService;
import com.campus.service.GradeService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

import com.campus.entity.Grade;

/**
 * 管理后台学生管理控制器
 * 处理学生管理相关的页面和API请求
 *
 * @author campus
 * @since 2025-06-05
 */
@Controller
@RequestMapping("/admin")
public class AdminStudentController {

    private final StudentService studentService;
    private final SchoolClassService schoolClassService;
    private final GradeService gradeService;

    @Autowired
    public AdminStudentController(StudentService studentService,
                                 SchoolClassService schoolClassService,
                                 GradeService gradeService) {
        this.studentService = studentService;
        this.schoolClassService = schoolClassService;
        this.gradeService = gradeService;
    }

    /**
     * 学生管理页面
     */
    @GetMapping("/students")
    public String students(@RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "20") int size,
                          @RequestParam(defaultValue = "") String search,
                          @RequestParam(defaultValue = "") String grade,
                          @RequestParam(defaultValue = "") String classId,
                          @RequestParam(defaultValue = "") String status,
                          Model model) {
        try {
            // 构建查询参数
            Map<String, Object> params = new HashMap<>();
            if (!search.isEmpty()) {
                params.put("search", search);
            }
            if (!grade.isEmpty()) {
                params.put("grade", grade);
            }
            if (!classId.isEmpty()) {
                params.put("classId", classId);
            }
            if (!status.isEmpty()) {
                params.put("status", status);
            }

            // 分页查询学生
            IPage<Student> studentPage = studentService.findStudentsByPage(page, size, params);

            // 获取学生统计
            StudentService.StudentStatistics studentStats = studentService.getStudentStatistics();

            // 获取班级列表供筛选
            List<SchoolClass> classes = schoolClassService.list();

            // 获取年级列表
            List<String> grades = generateGradeList();

            model.addAttribute("students", studentPage);
            model.addAttribute("stats", studentStats);
            model.addAttribute("classes", classes);
            model.addAttribute("grades", grades);
            model.addAttribute("search", search);
            model.addAttribute("grade", grade);
            model.addAttribute("classId", classId);
            model.addAttribute("status", status);
            model.addAttribute("pageTitle", "学生管理");
            model.addAttribute("currentPage", "students");
            return "admin/students";
        } catch (Exception e) {
            model.addAttribute("error", "加载学生列表失败：" + e.getMessage());
            return "admin/students";
        }
    }

    /**
     * 添加学生页面
     */
    @GetMapping("/students/new")
    public String newStudent(Model model) {
        try {
            // 获取真实班级列表供选择
            List<SchoolClass> allClasses = schoolClassService.list();
            
            // 获取年级列表（从班级数据中提取）
            List<String> grades = allClasses.stream()
                .map(SchoolClass::getGrade)
                .distinct()
                .sorted()
                .toList();
            
            // 从班级名称中提取专业信息（基于命名规则）
            List<String> majors = allClasses.stream()
                .map(SchoolClass::getClassName)
                .map(this::extractMajorFromClassName)
                .distinct()
                .filter(major -> !major.equals("其他专业"))
                .sorted()
                .toList();
            
            // 如果没有提取到专业，使用预定义列表
            if (majors.isEmpty()) {
                majors = List.of("计算机科学与技术", "软件工程", "信息安全", "数据科学与大数据技术", "人工智能", "网络工程", "物联网工程");
            }

            model.addAttribute("classes", allClasses);
            model.addAttribute("grades", grades);
            model.addAttribute("majors", majors);
            model.addAttribute("pageTitle", "添加学生");
            model.addAttribute("currentPage", "students");
            return "admin/student-form";
        } catch (Exception e) {
            model.addAttribute("error", "加载添加学生页面失败：" + e.getMessage());
            return "admin/students";
        }
    }

    // API接口

    /**
     * 获取学生详情 API
     */
    @GetMapping("/api/students/{id}")
    @ResponseBody
    public ApiResponse<Student> getStudentDetail(@PathVariable Long id) {
        try {
            Student student = studentService.getById(id);
            if (student != null) {
                return ApiResponse.success(student);
            } else {
                return ApiResponse.error("学生不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error("获取学生详情失败：" + e.getMessage());
        }
    }

    /**
     * 更改学生状态 API
     */
    @PostMapping("/api/students/{id}/status")
    @ResponseBody
    public ApiResponse<String> changeStudentStatus(@PathVariable Long id,
                                                   @RequestBody Map<String, String> request) {
        try {
            Student student = studentService.getById(id);
            if (student != null) {
                String status = request.get("status");
                if (status != null) {
                    student.setStatus(Integer.parseInt(status));
                    studentService.updateById(student);
                    return ApiResponse.success("学生状态更新成功");
                } else {
                    return ApiResponse.error("状态参数不能为空");
                }
            } else {
                return ApiResponse.error("学生不存在");
            }
        } catch (Exception e) {
            return ApiResponse.error("更新学生状态失败：" + e.getMessage());
        }
    }

    /**
     * 删除学生 API
     */
    @DeleteMapping("/api/students/{id}")
    @ResponseBody
    public ApiResponse<String> deleteStudent(@PathVariable Long id) {
        try {
            boolean success = studentService.removeById(id);
            if (success) {
                return ApiResponse.success("学生删除成功");
            } else {
                return ApiResponse.error("学生删除失败");
            }
        } catch (Exception e) {
            return ApiResponse.error("删除学生失败：" + e.getMessage());
        }
    }

    /**
     * 导出学生Excel API
     */
    @GetMapping("/api/students/export")
    @ResponseBody
    public ApiResponse<Map<String, Object>> exportStudents(@RequestParam(defaultValue = "") String grade,
                                                           @RequestParam(defaultValue = "") String classId,
                                                           @RequestParam(defaultValue = "") String status) {
        try {
            // 构建导出参数
            Map<String, Object> params = new HashMap<>();
            if (!grade.isEmpty()) {
                params.put("grade", grade);
            }
            if (!classId.isEmpty()) {
                params.put("classId", Long.parseLong(classId));
            }
            if (!status.isEmpty()) {
                params.put("status", Integer.parseInt(status));
            }

            // 获取要导出的学生数据
            List<Student> students = studentService.exportStudents(params);

            // 返回导出结果
            Map<String, Object> result = new HashMap<>();
            result.put("totalCount", students.size());
            result.put("exportTime", java.time.LocalDateTime.now().toString());
            result.put("fileName", "students_export_" + System.currentTimeMillis() + ".xlsx");
            result.put("message", "学生数据导出成功，共导出 " + students.size() + " 条记录");

            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("导出学生失败：" + e.getMessage());
        }
    }

    /**
     * 批量导入学生 API
     */
    @PostMapping("/api/students/import")
    @ResponseBody
    public ApiResponse<Map<String, Object>> importStudents(@RequestBody List<Map<String, Object>> studentData) {
        try {
            // 验证导入数据
            if (studentData == null || studentData.isEmpty()) {
                return ApiResponse.error("导入数据不能为空");
            }

            // 转换为Student对象列表
            List<Student> students = new ArrayList<>();
            for (Map<String, Object> data : studentData) {
                Student student = new Student();
                if (data.containsKey("studentNo")) {
                    student.setStudentNo((String) data.get("studentNo"));
                }
                if (data.containsKey("grade")) {
                    student.setGrade((String) data.get("grade"));
                }
                if (data.containsKey("classId")) {
                    student.setClassId(Long.parseLong(data.get("classId").toString()));
                }
                if (data.containsKey("userId")) {
                    student.setUserId(Long.parseLong(data.get("userId").toString()));
                }
                students.add(student);
            }

            // 调用服务层批量导入
            Map<String, Object> importResult = studentService.importStudents(students);

            return ApiResponse.success(importResult);
        } catch (Exception e) {
            return ApiResponse.error("批量导入学生失败：" + e.getMessage());
        }
    }

    /**
     * 学生成绩统计 API - 从数据库获取真实数据
     */
    @GetMapping("/api/students/{id}/grades")
    @ResponseBody
    public ApiResponse<Map<String, Object>> getStudentGrades(@PathVariable Long id) {
        try {
            Student student = studentService.getById(id);
            if (student == null) {
                return ApiResponse.error("学生不存在");
            }

            // 从数据库获取学生的真实成绩数据
            List<Grade> studentGrades = gradeService.findByStudentId(id);

            Map<String, Object> gradeStats = new HashMap<>();
            gradeStats.put("studentId", id);
            gradeStats.put("studentNo", student.getStudentNo());
            gradeStats.put("grade", student.getGrade());
            gradeStats.put("classId", student.getClassId());

            if (studentGrades.isEmpty()) {
                // 如果没有成绩记录，返回空统计
                gradeStats.put("totalCourses", 0);
                gradeStats.put("averageGrade", 0.0);
                gradeStats.put("gpa", 0.0);
                gradeStats.put("passedCourses", 0);
                gradeStats.put("failedCourses", 0);
                gradeStats.put("highestGrade", 0.0);
                gradeStats.put("lowestGrade", 0.0);
                gradeStats.put("status", "暂无成绩记录");
                gradeStats.put("message", "该学生暂无成绩记录");
            } else {
                // 基于真实成绩数据进行统计
                int totalCourses = studentGrades.size();

                // 计算平均成绩（只统计有成绩的课程）
                OptionalDouble averageScore = studentGrades.stream()
                    .filter(grade -> grade.getScore() != null)
                    .mapToDouble(grade -> grade.getScore().doubleValue())
                    .average();

                double avgGrade = averageScore.orElse(0.0);

                // 计算GPA（只统计有绩点的课程）
                OptionalDouble averageGPA = studentGrades.stream()
                    .filter(grade -> grade.getGradePoint() != null)
                    .mapToDouble(grade -> grade.getGradePoint().doubleValue())
                    .average();

                double gpa = averageGPA.orElse(0.0);

                // 统计通过和不及格课程数
                long passedCourses = studentGrades.stream()
                    .filter(grade -> grade.getScore() != null && grade.getScore().doubleValue() >= 60)
                    .count();

                long failedCourses = studentGrades.stream()
                    .filter(grade -> grade.getScore() != null && grade.getScore().doubleValue() < 60)
                    .count();

                // 找出最高分和最低分
                OptionalDouble highestScore = studentGrades.stream()
                    .filter(grade -> grade.getScore() != null)
                    .mapToDouble(grade -> grade.getScore().doubleValue())
                    .max();

                OptionalDouble lowestScore = studentGrades.stream()
                    .filter(grade -> grade.getScore() != null)
                    .mapToDouble(grade -> grade.getScore().doubleValue())
                    .min();

                gradeStats.put("totalCourses", totalCourses);
                gradeStats.put("averageGrade", Math.round(avgGrade * 100.0) / 100.0);
                gradeStats.put("gpa", Math.round(gpa * 100.0) / 100.0);
                gradeStats.put("passedCourses", passedCourses);
                gradeStats.put("failedCourses", failedCourses);
                gradeStats.put("highestGrade", highestScore.isPresent() ? Math.round(highestScore.getAsDouble() * 100.0) / 100.0 : 0.0);
                gradeStats.put("lowestGrade", lowestScore.isPresent() ? Math.round(lowestScore.getAsDouble() * 100.0) / 100.0 : 0.0);

                // 根据成绩情况确定状态
                String status = "正常";
                if (failedCourses > 0) {
                    status = "有不及格课程";
                } else if (avgGrade >= 85) {
                    status = "优秀";
                } else if (avgGrade >= 75) {
                    status = "良好";
                }
                gradeStats.put("status", status);

                // 添加详细的成绩分布信息
                Map<String, Long> gradeDistribution = new HashMap<>();
                gradeDistribution.put("优秀(90-100)", studentGrades.stream()
                    .filter(grade -> grade.getScore() != null && grade.getScore().doubleValue() >= 90)
                    .count());
                gradeDistribution.put("良好(80-89)", studentGrades.stream()
                    .filter(grade -> grade.getScore() != null && grade.getScore().doubleValue() >= 80 && grade.getScore().doubleValue() < 90)
                    .count());
                gradeDistribution.put("中等(70-79)", studentGrades.stream()
                    .filter(grade -> grade.getScore() != null && grade.getScore().doubleValue() >= 70 && grade.getScore().doubleValue() < 80)
                    .count());
                gradeDistribution.put("及格(60-69)", studentGrades.stream()
                    .filter(grade -> grade.getScore() != null && grade.getScore().doubleValue() >= 60 && grade.getScore().doubleValue() < 70)
                    .count());
                gradeDistribution.put("不及格(<60)", studentGrades.stream()
                    .filter(grade -> grade.getScore() != null && grade.getScore().doubleValue() < 60)
                    .count());

                gradeStats.put("gradeDistribution", gradeDistribution);
            }

            gradeStats.put("lastUpdateTime", java.time.LocalDateTime.now().toString());
            gradeStats.put("dataSource", "数据库真实数据");

            return ApiResponse.success(gradeStats);
        } catch (Exception e) {
            return ApiResponse.error("获取学生成绩失败：" + e.getMessage());
        }
    }

    /**
     * 获取学生详细成绩列表 API
     */
    @GetMapping("/api/students/{id}/grades/details")
    @ResponseBody
    public ApiResponse<Map<String, Object>> getStudentGradeDetails(@PathVariable Long id,
                                                                   @RequestParam(defaultValue = "") String semester) {
        try {
            Student student = studentService.getById(id);
            if (student == null) {
                return ApiResponse.error("学生不存在");
            }

            // 获取学生成绩详细列表
            List<Grade> studentGrades;
            if (!semester.isEmpty()) {
                studentGrades = gradeService.findByStudentIdAndSemester(id, semester);
            } else {
                studentGrades = gradeService.findByStudentId(id);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("studentId", id);
            result.put("studentNo", student.getStudentNo());
            result.put("grade", student.getGrade());
            result.put("semester", semester);
            result.put("totalRecords", studentGrades.size());

            // 转换成绩数据为前端友好的格式
            List<Map<String, Object>> gradeDetails = new ArrayList<>();
            for (Grade grade : studentGrades) {
                Map<String, Object> gradeDetail = new HashMap<>();
                gradeDetail.put("id", grade.getId());
                gradeDetail.put("courseId", grade.getCourseId());
                gradeDetail.put("semester", grade.getSemester());
                gradeDetail.put("score", grade.getScore());
                gradeDetail.put("regularScore", grade.getRegularScore());
                gradeDetail.put("midtermScore", grade.getMidtermScore());
                gradeDetail.put("finalScore", grade.getFinalScore());
                gradeDetail.put("gradePoint", grade.getGradePoint());
                gradeDetail.put("level", grade.getLevel());
                gradeDetail.put("isMakeup", grade.getIsMakeup());
                gradeDetail.put("isRetake", grade.getIsRetake());
                gradeDetail.put("status", grade.getStatus());
                gradeDetail.put("remarks", grade.getRemarks());
                gradeDetail.put("createdAt", grade.getCreatedAt());
                gradeDetail.put("updatedAt", grade.getUpdatedAt());
                gradeDetails.add(gradeDetail);
            }

            result.put("grades", gradeDetails);
            result.put("dataSource", "数据库真实数据");
            result.put("queryTime", java.time.LocalDateTime.now().toString());

            return ApiResponse.success(result);
        } catch (Exception e) {
            return ApiResponse.error("获取学生成绩详情失败：" + e.getMessage());
        }
    }

    // 辅助方法

    /**
     * 动态生成年级列表
     */
    private List<String> generateGradeList() {
        List<String> grades = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        
        // 生成过去4年到未来1年的年级
        for (int year = currentYear - 4; year <= currentYear + 1; year++) {
            grades.add(year + "级");
        }
        
        return grades;
    }

    /**
     * 从班级名称中提取专业信息
     */
    private String extractMajorFromClassName(String className) {
        if (className == null || className.isEmpty()) {
            return "其他专业";
        }
        
        // 基于常见的班级命名规则提取专业
        if (className.contains("计算机科学") || className.contains("计科")) {
            return "计算机科学与技术";
        } else if (className.contains("软件工程") || className.contains("软工")) {
            return "软件工程";
        } else if (className.contains("信息安全") || className.contains("信安")) {
            return "信息安全";
        } else if (className.contains("数据科学") || className.contains("大数据")) {
            return "数据科学与大数据技术";
        } else if (className.contains("人工智能") || className.contains("AI")) {
            return "人工智能";
        } else if (className.contains("网络工程")) {
            return "网络工程";
        } else if (className.contains("物联网")) {
            return "物联网工程";
        } else {
            return "其他专业";
        }
    }
}
