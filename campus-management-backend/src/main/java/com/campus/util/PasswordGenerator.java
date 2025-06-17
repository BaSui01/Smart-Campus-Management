package com.campus.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具
 * 用于生成BCrypt密码哈希
 */
public class PasswordGenerator {

    public static void main(String[] args) {
        // 使用与SecurityConfig相同的BCryptPasswordEncoder
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 生成admin123的哈希值
        String password = "admin123";
        String hash = encoder.encode(password);

        System.out.println("=== 密码哈希生成工具 ===");
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt哈希值: " + hash);
        System.out.println();

        // 验证哈希值
        boolean matches = encoder.matches(password, hash);
        System.out.println("验证结果: " + (matches ? "✓ 正确" : "✗ 错误"));

        // 生成多个哈希值供选择
        System.out.println();
        System.out.println("=== 生成多个哈希值 ===");
        for (int i = 1; i <= 3; i++) {
            String newHash = encoder.encode(password);
            System.out.println("哈希值 " + i + ": " + newHash);
            System.out.println("验证 " + i + ": " + (encoder.matches(password, newHash) ? "✓" : "✗"));
        }

        System.out.println();
        System.out.println("=== SQL更新语句 ===");
        System.out.println("UPDATE tb_user SET password = '" + hash + "' WHERE username = 'admin';");

        System.out.println();
        System.out.println("=== 检查当前数据库中的密码 ===");
        String currentDbPassword = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.";
        System.out.println("当前数据库密码: " + currentDbPassword);
        System.out.println("验证admin123: " + (encoder.matches("admin123", currentDbPassword) ? "✓ 正确" : "✗ 错误"));
        System.out.println("验证admin: " + (encoder.matches("admin", currentDbPassword) ? "✓ 正确" : "✗ 错误"));
        System.out.println("验证123456: " + (encoder.matches("123456", currentDbPassword) ? "✓ 正确" : "✗ 错误"));
    }
}
