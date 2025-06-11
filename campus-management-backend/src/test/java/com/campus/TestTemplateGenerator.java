package com.campus;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 测试模板生成器
 * 为所有API控制器生成标准化的单元测试模板
 * 
 * @author Campus Management Team
 * @since 2025-06-09
 */
public class TestTemplateGenerator {

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
        "SystemApiController",
        "StudentApiController",
        "UserApiController"
    );

    @Test
    public void generateApiControllerTests() {
        String testPackage = "src/test/java/com/campus/interfaces/rest/v1/";
        
        // 创建目录
        File testDir = new File(testPackage);
        if (!testDir.exists()) {
            testDir.mkdirs();
        }
        
        for (String controller : API_CONTROLLERS) {
            generateTestClass(testPackage, controller);
        }
        
        System.out.println("✅ API控制器测试类生成完成！");
        System.out.println("📁 生成位置: " + testPackage);
        System.out.println("📊 生成数量: " + API_CONTROLLERS.size() + " 个测试类");
    }

    private void generateTestClass(String packagePath, String controllerName) {
        String testClassName = controllerName + "Test";
        String filePath = packagePath + testClassName + ".java";
        
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(generateTestContent(controllerName, testClassName));
            System.out.println("✅ 生成测试类: " + testClassName);
        } catch (IOException e) {
            System.err.println("❌ 生成测试类失败: " + testClassName + " - " + e.getMessage());
        }
    }

    private String generateTestContent(String controllerName, String testClassName) {
        String serviceName = controllerName.replace("ApiController", "Service");
        String entityName = controllerName.replace("ApiController", "");
        String entityLowerCase = entityName.toLowerCase();

        StringBuilder sb = new StringBuilder();
        sb.append("package com.campus.interfaces.rest.v1;\n\n");
        sb.append("import com.campus.BaseControllerTest;\n");
        sb.append("import org.junit.jupiter.api.DisplayName;\n");
        sb.append("import org.junit.jupiter.api.Test;\n");
        sb.append("import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;\n");
        sb.append("import org.springframework.boot.test.mock.mockito.MockBean;\n");
        sb.append("import org.springframework.security.test.context.support.WithMockUser;\n");
        sb.append("import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;\n\n");
        sb.append("import java.util.*;\n\n");
        sb.append("import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;\n\n");

        sb.append("/**\n");
        sb.append(" * ").append(controllerName).append(" 单元测试\n");
        sb.append(" * \n");
        sb.append(" * @author Campus Management Team\n");
        sb.append(" * @since 2025-06-09\n");
        sb.append(" */\n");
        sb.append("@WebMvcTest(").append(controllerName).append(".class)\n");
        sb.append("@DisplayName(\"").append(controllerName).append("单元测试\")\n");
        sb.append("class ").append(testClassName).append(" extends BaseControllerTest {\n\n");

        // Mock service
        sb.append("    // @MockBean\n");
        sb.append("    // private ").append(serviceName).append(" ").append(entityLowerCase).append("Service;\n\n");

        // Test methods
        sb.append("    @Test\n");
        sb.append("    @WithMockUser(roles = {\"ADMIN\", \"SYSTEM_ADMIN\"})\n");
        sb.append("    @DisplayName(\"测试获取统计信息\")\n");
        sb.append("    void testGetStats() throws Exception {\n");
        sb.append("        // Given\n");
        sb.append("        Map<String, Object> mockStats = createMockStats();\n\n");
        sb.append("        // When & Then\n");
        sb.append("        mockMvc.perform(MockMvcRequestBuilders.get(\"/api/v1/").append(entityLowerCase).append("/stats\"))\n");
        sb.append("                .andExpect(status().isOk());\n");
        sb.append("    }\n\n");

        sb.append("    @Test\n");
        sb.append("    @WithMockUser(roles = {\"ADMIN\", \"SYSTEM_ADMIN\"})\n");
        sb.append("    @DisplayName(\"测试批量删除\")\n");
        sb.append("    void testBatchDelete() throws Exception {\n");
        sb.append("        // Given\n");
        sb.append("        List<Long> ids = Arrays.asList(1L, 2L, 3L);\n\n");
        sb.append("        // When & Then\n");
        sb.append("        mockMvc.perform(MockMvcRequestBuilders.delete(\"/api/v1/").append(entityLowerCase).append("/batch\")\n");
        sb.append("                .contentType(\"application/json\")\n");
        sb.append("                .content(asJsonString(ids)))\n");
        sb.append("                .andExpect(status().isOk());\n");
        sb.append("    }\n\n");

        sb.append("    @Test\n");
        sb.append("    @WithMockUser(roles = {\"STUDENT\"})\n");
        sb.append("    @DisplayName(\"测试权限控制 - 学生角色应被拒绝\")\n");
        sb.append("    void testPermissionControl() throws Exception {\n");
        sb.append("        // When & Then - 学生角色应该被拒绝访问管理功能\n");
        sb.append("        mockMvc.perform(MockMvcRequestBuilders.delete(\"/api/v1/").append(entityLowerCase).append("/1\"))\n");
        sb.append("                .andExpect(status().isForbidden());\n");
        sb.append("    }\n\n");

        sb.append("    @Test\n");
        sb.append("    @WithMockUser(roles = {\"ADMIN\"})\n");
        sb.append("    @DisplayName(\"测试参数验证\")\n");
        sb.append("    void testParameterValidation() throws Exception {\n");
        sb.append("        // When & Then - 测试无效参数\n");
        sb.append("        mockMvc.perform(MockMvcRequestBuilders.get(\"/api/v1/").append(entityLowerCase).append("\")\n");
        sb.append("                .param(\"page\", \"-1\")\n");
        sb.append("                .param(\"size\", \"0\"))\n");
        sb.append("                .andExpect(status().isBadRequest());\n");
        sb.append("    }\n\n");

        // Add createMockStats method
        sb.append("    /**\n");
        sb.append("     * 创建模拟统计数据\n");
        sb.append("     */\n");
        sb.append("    private Map<String, Object> createMockStats() {\n");
        sb.append("        Map<String, Object> stats = new HashMap<>();\n");
        sb.append("        stats.put(\"total\", 100L);\n");
        sb.append("        stats.put(\"active\", 80L);\n");
        sb.append("        stats.put(\"todayCount\", 5L);\n");
        sb.append("        stats.put(\"weekCount\", 25L);\n");
        sb.append("        stats.put(\"monthCount\", 100L);\n");
        sb.append("        return stats;\n");
        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();
    }
}
