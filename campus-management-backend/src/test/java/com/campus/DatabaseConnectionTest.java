package com.campus;

import com.campus.config.TestCacheConfig;
import com.campus.config.TestServiceConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 数据库连接测试
 *
 * @author Campus Management Team
 * @since 2025-06-10
 */
@SpringBootTest(classes = {CampusManagementApplication.class, TestCacheConfig.class, TestServiceConfig.class})
@ActiveProfiles("test")
class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void testDatabaseConnection() {
        assertNotNull(dataSource, "数据源不应为空");

        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "数据库连接不应为空");
            assertFalse(connection.isClosed(), "数据库连接应该是打开的");

            System.out.println("数据库连接成功！");
            System.out.println("数据库URL: " + connection.getMetaData().getURL());
            System.out.println("数据库产品名称: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("数据库版本: " + connection.getMetaData().getDatabaseProductVersion());

        } catch (SQLException e) {
            fail("数据库连接失败: " + e.getMessage());
        }
    }
}
