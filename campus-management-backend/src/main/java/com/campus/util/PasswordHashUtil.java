package com.campus.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码哈希工具类
 * 用于生成BCrypt密码哈希值
 */
public class PasswordHashUtil {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        System.out.println("=== 智慧校园管理系统密码哈希生成器 ===");
        
        // 生成admin123的哈希值
        String admin123Hash = encoder.encode("admin123");
        System.out.println("admin123 的哈希值:");
        System.out.println(admin123Hash);
        System.out.println("验证结果: " + (encoder.matches("admin123", admin123Hash) ? "✓ 正确" : "✗ 错误"));
        
        // 生成student123的哈希值
        String student123Hash = encoder.encode("student123");
        System.out.println("\nstudent123 的哈希值:");
        System.out.println(student123Hash);
        System.out.println("验证结果: " + (encoder.matches("student123", student123Hash) ? "✓ 正确" : "✗ 错误"));
        
        // 生成teacher的哈希值
        String teacherHash = encoder.encode("teacher");
        System.out.println("\nteacher 的哈希值:");
        System.out.println(teacherHash);
        System.out.println("验证结果: " + (encoder.matches("teacher", teacherHash) ? "✓ 正确" : "✗ 错误"));
        
        System.out.println("\n=== SQL更新语句 ===");
        System.out.println("-- 更新admin用户密码为admin123");
        System.out.println("UPDATE tb_user SET password = '" + admin123Hash + "' WHERE username = 'admin';");
        
        System.out.println("\n-- 更新所有管理员用户密码为admin123");
        System.out.println("UPDATE tb_user SET password = '" + admin123Hash + "' WHERE username LIKE 'admin%';");
        
        System.out.println("\n-- 更新所有学生用户密码为student123");
        System.out.println("UPDATE tb_user SET password = '" + student123Hash + "' WHERE username LIKE 'student%';");
        
        System.out.println("\n-- 更新所有教师用户密码为teacher");
        System.out.println("UPDATE tb_user SET password = '" + teacherHash + "' WHERE username LIKE 'teacher%';");
        
        System.out.println("\n-- 更新所有家长用户密码为admin123");
        System.out.println("UPDATE tb_user SET password = '" + admin123Hash + "' WHERE username LIKE 'parent%';");
        
        System.out.println("\n=== 数据库插入脚本更新 ===");
        System.out.println("请将以下哈希值更新到 02_insert_basic_data.sql 文件中:");
        System.out.println("管理员密码 (admin123): " + admin123Hash);
        System.out.println("学生密码 (student123): " + student123Hash);
        System.out.println("教师密码 (teacher): " + teacherHash);
        
        System.out.println("\n=== 完成 ===");
    }
}
