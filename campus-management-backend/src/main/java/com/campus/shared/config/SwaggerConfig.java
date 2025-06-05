package com.campus.shared.config;

import java.util.List;

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

    /**
     * OpenAPI配置 - 无需认证，完全开放
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("本地开发环境"),
                        new Server().url("https://api.campus.edu").description("生产环境")
                ))
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
}
