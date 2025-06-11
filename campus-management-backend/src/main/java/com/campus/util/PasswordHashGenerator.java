package com.campus.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码哈希生成工具
 * 用于生成不同角色的密码哈希值
 */
@Component
public class PasswordHashGenerator implements CommandLineRunner {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否需要生成密码哈希
        if (args.length > 0 && "--generate-hash".equals(args[0])) {
            generatePasswordHashes();
        }
    }

    private void generatePasswordHashes() {
        System.out.println("=== 密码哈希值生成工具 ===");
        
        // 生成不同角色的密码哈希值
        String admin123Hash = passwordEncoder.encode("admin123");
        String student123Hash = passwordEncoder.encode("student123");
        String teacherHash = passwordEncoder.encode("teacher");
        
        System.out.println("admin123 的哈希值:");
        System.out.println(admin123Hash);
        System.out.println("验证结果: " + (passwordEncoder.matches("admin123", admin123Hash) ? "✓ 正确" : "✗ 错误"));
        
        System.out.println("\nstudent123 的哈希值:");
        System.out.println(student123Hash);
        System.out.println("验证结果: " + (passwordEncoder.matches("student123", student123Hash) ? "✓ 正确" : "✗ 错误"));
        
        System.out.println("\nteacher 的哈希值:");
        System.out.println(teacherHash);
        System.out.println("验证结果: " + (passwordEncoder.matches("teacher", teacherHash) ? "✓ 正确" : "✗ 错误"));
        
        System.out.println("\n=== SQL脚本更新内容 ===");
        System.out.println("-- 管理员密码 (admin123):");
        System.out.println("'" + admin123Hash + "'");
        
        System.out.println("\n-- 学生密码 (student123):");
        System.out.println("'" + student123Hash + "'");
        
        System.out.println("\n-- 教师密码 (teacher):");
        System.out.println("'" + teacherHash + "'");
        
        System.out.println("\n=== 数据库更新语句 ===");
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
        
        System.out.println("\n=== 完成 ===");
    }
}
