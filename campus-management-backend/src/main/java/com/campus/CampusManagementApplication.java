package com.campus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
public class CampusManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusManagementApplication.class, args);
    }
}
