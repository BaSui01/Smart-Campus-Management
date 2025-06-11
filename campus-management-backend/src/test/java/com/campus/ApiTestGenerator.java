package com.campus;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * API测试生成器
 * 自动为所有控制器生成基础测试类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-08
 */
@SpringBootTest
public class ApiTestGenerator {

    // 所有需要测试的API控制器
    private static final List<String> API_CONTROLLERS = Arrays.asList(
        "AssignmentApiController",
        "AttendanceApiController", 
        "AutoScheduleApiController",
        "CacheManagementApiController",
        "ClassApiController",
        "ClassroomApiController",
        "CourseApiController",
        "CourseScheduleApiController",
        "CourseSelectionApiController",
        "CourseSelectionPeriodApiController",
        "DashboardApiController",
        "DepartmentApiController",
        "ExamApiController",
        "FeeItemApiController",
        "GradeApiController",
        "MessageApiController",
        "NotificationApiController",
        "OptimizedStudentApiController",
        "OptimizedUserApiController",
        "PaymentApiController",
        "PermissionApiController",
        "RoleApiController",
        "ScheduleApiController",
        "SystemApiController"
    );

    @Test
    public void generateApiTests() {
        String testPackage = "src/test/java/com/campus/interfaces/rest/v1/";
        
        for (String controller : API_CONTROLLERS) {
            generateTestClass(testPackage, controller);
        }
        
        System.out.println("✅ API测试类生成完成！");
        System.out.println("📁 生成位置: " + testPackage);
        System.out.println("📊 生成数量: " + API_CONTROLLERS.size() + " 个测试类");
    }

    private void generateTestClass(String packagePath, String controllerName) {
        String testClassName = controllerName + "Test";
        String fileName = packagePath + testClassName + ".java";
        
        // 检查文件是否已存在
        if (new File(fileName).exists()) {
            System.out.println("⚠️  测试类已存在，跳过: " + testClassName);
            return;
        }

        try {
            // 确保目录存在
            Path path = Paths.get(packagePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            // 生成测试类内容
            String testContent = generateTestContent(controllerName, testClassName);
            
            // 写入文件
            try (FileWriter writer = new FileWriter(fileName)) {
                writer.write(testContent);
            }
            
            System.out.println("✅ 生成测试类: " + testClassName);
            
        } catch (IOException e) {
            System.err.println("❌ 生成测试类失败: " + testClassName + " - " + e.getMessage());
        }
    }

    private String generateTestContent(String controllerName, String testClassName) {
        String apiPath = getApiPath(controllerName);
        String displayName = getDisplayName(controllerName);
        
        return String.format("""
package com.campus.interfaces.rest.v1;

import com.campus.BaseApiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * %s测试类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-08
 */
@DisplayName("%sAPI接口测试")
class %s extends BaseApiTest {

    private static final String BASE_URL = "%s";

    @Test
    @DisplayName("获取列表 - 成功")
    void testGetList_Success() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", getAdminAuthHeader())
                        .param("page", "1")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(getJsonContentType()));
    }

    @Test
    @DisplayName("获取详情 - 成功")
    void testGetById_Success() throws Exception {
        mockMvc.perform(get(BASE_URL + "/1")
                        .header("Authorization", getAdminAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("创建 - 成功")
    void testCreate_Success() throws Exception {
        String createData = "{}";
        
        mockMvc.perform(post(BASE_URL)
                        .header("Authorization", getAdminAuthHeader())
                        .contentType(getJsonContentType())
                        .content(createData))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("更新 - 成功")
    void testUpdate_Success() throws Exception {
        String updateData = "{}";
        
        mockMvc.perform(put(BASE_URL + "/1")
                        .header("Authorization", getAdminAuthHeader())
                        .contentType(getJsonContentType())
                        .content(updateData))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("删除 - 成功")
    void testDelete_Success() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/999")
                        .header("Authorization", getAdminAuthHeader()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("无权限访问测试")
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("学生权限测试")
    void testStudentPermission() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", getStudentAuthHeader()))
                .andDo(print());
    }

    @Test
    @DisplayName("教师权限测试")
    void testTeacherPermission() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .header("Authorization", getTeacherAuthHeader()))
                .andDo(print());
    }
}
""", controllerName, displayName, testClassName, apiPath);
    }

    private String getApiPath(String controllerName) {
        // 根据控制器名称推断API路径
        String name = controllerName.replace("ApiController", "").toLowerCase();
        
        // 特殊情况处理
        switch (name) {
            case "optimizeduser": return "/api/v1/users/optimized";
            case "optimizedstudent": return "/api/v1/students/optimized";
            case "courseselectionperiod": return "/api/v1/course-selection-periods";
            case "courseschedule": return "/api/v1/course-schedules";
            case "courseselection": return "/api/v1/course-selections";
            case "autoschedule": return "/api/v1/auto-schedule";
            case "cachemanagement": return "/api/v1/cache";
            case "feeitem": return "/api/v1/fee-items";
            default: return "/api/v1/" + name + "s";
        }
    }

    private String getDisplayName(String controllerName) {
        // 根据控制器名称生成中文显示名称
        switch (controllerName) {
            case "AssignmentApiController": return "作业管理";
            case "AttendanceApiController": return "考勤管理";
            case "AutoScheduleApiController": return "自动排课";
            case "CacheManagementApiController": return "缓存管理";
            case "ClassApiController": return "班级管理";
            case "ClassroomApiController": return "教室管理";
            case "CourseApiController": return "课程管理";
            case "CourseScheduleApiController": return "课程安排";
            case "CourseSelectionApiController": return "选课管理";
            case "CourseSelectionPeriodApiController": return "选课时段";
            case "DashboardApiController": return "仪表盘";
            case "DepartmentApiController": return "院系管理";
            case "ExamApiController": return "考试管理";
            case "FeeItemApiController": return "费用项目";
            case "GradeApiController": return "成绩管理";
            case "MessageApiController": return "消息管理";
            case "NotificationApiController": return "通知管理";
            case "OptimizedStudentApiController": return "学生管理(优化)";
            case "OptimizedUserApiController": return "用户管理(优化)";
            case "PaymentApiController": return "支付管理";
            case "PermissionApiController": return "权限管理";
            case "RoleApiController": return "角色管理";
            case "ScheduleApiController": return "课表管理";
            case "SystemApiController": return "系统管理";
            default: return controllerName.replace("ApiController", "");
        }
    }
}
