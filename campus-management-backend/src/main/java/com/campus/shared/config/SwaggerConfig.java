package com.campus.shared.config;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Swagger/OpenAPI配置
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Configuration
public class SwaggerConfig {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    /**
     * OpenAPI配置 - 无需认证，完全开放
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(getServers())
                .components(new Components()
                        .addSecuritySchemes("Bearer", securityScheme())
                );
                // 移除全局安全要求，让API文档和接口完全开放
                // .addSecurityItem(new SecurityRequirement().addList("Bearer"));
    }

    /**
     * API信息配置
     */
    private Info apiInfo() {
        return new Info()
                .title("智慧校园管理平台API文档")
                .description("智慧校园管理平台后端服务API接口文档，提供完整的教务管理、学生管理、财务管理等功能接口。")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Campus Management Team")
                        .email("dev@campus.edu")
                        .url("https://github.com/campus-management/backend")
                )
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT")
                )
                .termsOfService("https://campus.edu/terms");
    }

    /**
     * 安全方案配置
     */
    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .name("Authorization")
                .description("JWT认证令牌，格式：Bearer {token}");
    }

    /**
     * 根据环境配置服务器列表
     */
    private List<Server> getServers() {
        switch (activeProfile) {
            case "prod":
                return List.of(
                    new Server().url("https://api.campus.edu").description("生产环境")
                );
            case "test":
                return List.of(
                    new Server().url("https://test-api.campus.edu").description("测试环境"),
                    new Server().url("http://localhost:8080").description("本地开发环境")
                );
            case "dev":
            default:
                return List.of(
                    new Server().url("http://localhost:8080").description("本地开发环境"),
                    new Server().url("https://dev-api.campus.edu").description("开发环境")
                );
        }
    }

    /**
     * 学生管理API分组
     */
    @Bean
    public GroupedOpenApi studentApi() {
        return GroupedOpenApi.builder()
            .group("学生管理")
            .pathsToMatch("/api/v1/students/**", "/api/v1/grades/**", "/api/v1/attendance/student/**")
            .build();
    }

    /**
     * 教师管理API分组
     */
    @Bean
    public GroupedOpenApi teacherApi() {
        return GroupedOpenApi.builder()
            .group("教师管理")
            .pathsToMatch("/api/v1/teachers/**", "/api/v1/courses/**", "/api/v1/exams/**", "/api/v1/assignments/**")
            .build();
    }

    /**
     * 课程管理API分组
     */
    @Bean
    public GroupedOpenApi courseApi() {
        return GroupedOpenApi.builder()
            .group("课程管理")
            .pathsToMatch("/api/v1/courses/**", "/api/v1/course-schedules/**", "/api/v1/course-selections/**")
            .build();
    }

    /**
     * 系统管理API分组
     */
    @Bean
    public GroupedOpenApi systemApi() {
        return GroupedOpenApi.builder()
            .group("系统管理")
            .pathsToMatch("/api/v1/system/**", "/api/v1/permissions/**", "/api/v1/roles/**")
            .build();
    }

    /**
     * 认证API分组
     */
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
            .group("认证管理")
            .pathsToMatch("/api/v1/auth/**", "/api/v1/users/**")
            .build();
    }
}
