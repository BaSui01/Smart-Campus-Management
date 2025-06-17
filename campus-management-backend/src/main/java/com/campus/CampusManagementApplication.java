package com.campus;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 智慧校园管理平台启动类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class CampusManagementApplication implements ApplicationListener<ApplicationReadyEvent> {

    public static void main(String[] args) {
        SpringApplication.run(CampusManagementApplication.class, args);
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();
        String port = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        String profile = env.getProperty("spring.profiles.active", "default");

        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();

            System.out.println("\n----------------------------------------------------------");
            System.out.println("🚀 应用启动成功！");
            System.out.println("📋 应用信息:");
            System.out.println("   - 应用名称: 智慧校园管理系统");
            System.out.println("   - 运行环境: " + profile);
            System.out.println("   - 服务端口: " + port);
            System.out.println("\n🌐 访问地址:");
            System.out.println("   - 本地访问: http://localhost:" + port + contextPath);
            System.out.println("   - 网络访问: http://" + hostAddress + ":" + port + contextPath);
            System.out.println("\n📱 管理后台:");
            System.out.println("   - 后台地址: http://localhost:" + port + contextPath + "/admin");
            System.out.println("   - 默认账号: admin");
            System.out.println("   - 默认密码: admin123");
            System.out.println("----------------------------------------------------------\n");

        } catch (UnknownHostException e) {
            System.out.println("\n----------------------------------------------------------");
            System.out.println("🚀 应用启动成功！");
            System.out.println("📋 应用信息:");
            System.out.println("   - 应用名称: 智慧校园管理系统");
            System.out.println("   - 运行环境: " + profile);
            System.out.println("   - 服务端口: " + port);
            System.out.println("\n🌐 访问地址:");
            System.out.println("   - 本地访问: http://localhost:" + port + contextPath);
            System.out.println("\n📱 管理后台:");
            System.out.println("   - 后台地址: http://localhost:" + port + contextPath + "/admin");
            System.out.println("   - 默认账号: admin");
            System.out.println("   - 默认密码: admin123");
            System.out.println("----------------------------------------------------------\n");
        }
    }
}
