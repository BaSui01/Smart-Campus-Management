package com.campus.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * 数据库初始化工具类
 * 按顺序执行SQL脚本文件
 *
 * @author Smart Campus Team
 * @version 1.0.0
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * SQL脚本文件列表（按执行顺序）
     */
    private final List<String> sqlFiles = Arrays.asList(
        "db/01_create_tables.sql",      // 创建表结构
        "db/02_insert_basic_data.sql",  // 插入基础数据
        "db/03_insert_large_data.sql",  // 生成大规模数据
        "db/04_complete_all_tables.sql", // 补充完整表数据
        "db/05_data_validation.sql",    // 数据验证
        "db/06_data_analysis.sql"       // 数据分析
    );

    /**
     * 脚本描述信息
     */
    private final List<String> descriptions = Arrays.asList(
        "创建数据库表结构",
        "插入基础数据",
        "生成大规模测试数据",
        "补充完整表数据",
        "执行数据验证",
        "执行数据分析"
    );

    @Override
    public void run(String... args) throws Exception {
        // 检查是否需要执行初始化
        if (!shouldInitialize(args)) {
            return;
        }

        log.info("=== 智慧校园管理系统 - 数据库初始化开始 ===");
        
        try {
            // 测试数据库连接
            testDatabaseConnection();
            
            // 按顺序执行SQL脚本
            executeSqlScripts();
            
            // 验证初始化结果
            validateInitialization();
            
            log.info("🎉 数据库初始化完成！系统可以正常使用了！");
            
        } catch (Exception e) {
            log.error("❌ 数据库初始化失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 检查是否需要执行初始化
     */
    private boolean shouldInitialize(String... args) {
        // 检查命令行参数
        boolean hasInitFlag = Arrays.asList(args).contains("--init-db");
        
        if (!hasInitFlag) {
            log.info("跳过数据库初始化。使用 --init-db 参数来执行初始化。");
            return false;
        }

        // 检查是否已经初始化过
        try {
            Long userCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tb_user WHERE deleted = 0", Long.class);
            
            if (userCount != null && userCount > 0) {
                log.warn("数据库已包含数据，跳过初始化。当前用户数: {}", userCount);
                return false;
            }
        } catch (Exception e) {
            log.info("数据库表不存在或为空，将执行初始化...");
        }

        return true;
    }

    /**
     * 测试数据库连接
     */
    private void testDatabaseConnection() throws SQLException {
        log.info("[0/6] 测试数据库连接...");
        
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(5)) {
                log.info("✅ 数据库连接成功！");
            } else {
                throw new SQLException("数据库连接无效");
            }
        }
    }

    /**
     * 按顺序执行SQL脚本
     */
    private void executeSqlScripts() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            for (int i = 0; i < sqlFiles.size(); i++) {
                String sqlFile = sqlFiles.get(i);
                String description = descriptions.get(i);
                
                executeSqlScript(connection, sqlFile, description, i + 1);
            }
        }
    }

    /**
     * 执行单个SQL脚本
     */
    private void executeSqlScript(Connection connection, String sqlFile, 
                                 String description, int step) throws SQLException {
        log.info("[{}/6] {}...", step, description);
        
        try {
            ClassPathResource resource = new ClassPathResource(sqlFile);
            
            if (!resource.exists()) {
                throw new RuntimeException("SQL文件不存在: " + sqlFile);
            }

            // 执行SQL脚本
            ScriptUtils.executeSqlScript(connection, resource);
            
            log.info("✅ {} 完成", description);
            
        } catch (Exception e) {
            log.error("❌ {} 失败: {}", description, e.getMessage());
            throw new SQLException("执行SQL脚本失败: " + sqlFile, e);
        }
    }

    /**
     * 验证初始化结果
     */
    private void validateInitialization() {
        log.info("验证数据库初始化结果...");
        
        try {
            // 检查用户数量
            Long userCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tb_user WHERE deleted = 0", Long.class);
            
            // 检查学生数量
            Long studentCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tb_student WHERE deleted = 0", Long.class);
            
            // 检查课程数量
            Long courseCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tb_course WHERE deleted = 0", Long.class);
            
            // 检查班级数量
            Long classCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tb_class WHERE deleted = 0", Long.class);

            log.info("=== 数据库统计信息 ===");
            log.info("用户总数: {}", userCount);
            log.info("学生总数: {}", studentCount);
            log.info("课程总数: {}", courseCount);
            log.info("班级总数: {}", classCount);
            
            // 验证管理员账号
            Long adminCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tb_user WHERE username = 'admin' AND deleted = 0", 
                Long.class);
            
            if (adminCount != null && adminCount > 0) {
                log.info("✅ 管理员账号已创建 (用户名: admin, 密码: admin123)");
            } else {
                log.warn("⚠️ 管理员账号未找到");
            }
            
        } catch (Exception e) {
            log.error("验证初始化结果时出错: {}", e.getMessage());
        }
    }

    /**
     * 手动执行数据库初始化
     * 可以在其他地方调用此方法
     */
    public void manualInitialize() throws Exception {
        log.info("手动执行数据库初始化...");
        run("--init-db");
    }

    /**
     * 检查数据库是否已初始化
     */
    public boolean isDatabaseInitialized() {
        try {
            Long userCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tb_user WHERE deleted = 0", Long.class);
            return userCount != null && userCount > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取数据库统计信息
     */
    public String getDatabaseStats() {
        try {
            Long userCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tb_user WHERE deleted = 0", Long.class);
            Long studentCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tb_student WHERE deleted = 0", Long.class);
            Long courseCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tb_course WHERE deleted = 0", Long.class);
            Long classCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tb_class WHERE deleted = 0", Long.class);

            return String.format(
                "数据库统计: 用户=%d, 学生=%d, 课程=%d, 班级=%d",
                userCount, studentCount, courseCount, classCount
            );
        } catch (Exception e) {
            return "无法获取数据库统计信息: " + e.getMessage();
        }
    }
}