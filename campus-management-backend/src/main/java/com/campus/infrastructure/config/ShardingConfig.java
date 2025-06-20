package com.campus.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;

/**
 * ShardingSphere 分表配置类
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Slf4j
@Configuration
@Profile("sharding")
@ConditionalOnProperty(name = "spring.shardingsphere.enabled", havingValue = "true", matchIfMissing = true)
public class ShardingConfig {
    
    @PostConstruct
    public void init() {
        log.info("=== ShardingSphere 分表配置已启用 ===");
        log.info("支持的分表:");
        log.info("- 学生表: tb_student_YYYY (按入学年份分表)");
        log.info("- 成绩表: tb_grade_YYYY_N (按学期分表)");
        log.info("- 考勤表: tb_attendance_YYYYMM (按年月分表)");
        log.info("=====================================");
    }
}
