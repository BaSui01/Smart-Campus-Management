package com.campus.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.campus.domain.repository.UserRepository;
import com.campus.domain.entity.User;

import java.util.Optional;

/**
 * 密码测试工具
 * 用于测试密码验证是否正确
 */
@Component
public class PasswordTester implements CommandLineRunner {

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否需要测试密码
        if (args.length > 0 && "--test-password".equals(args[0])) {
            testPassword();
        }
    }

    private void testPassword() {
        System.out.println("=== 密码验证测试工具 ===");
        
        try {
            // 查找admin用户
            Optional<User> userOpt = userRepository.findByUsername("admin");
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                System.out.println("找到admin用户:");
                System.out.println("- ID: " + user.getId());
                System.out.println("- 用户名: " + user.getUsername());
                System.out.println("- 真实姓名: " + user.getRealName());
                System.out.println("- 状态: " + user.getStatus());
                System.out.println("- 删除标记: " + user.getDeleted());
                System.out.println("- 密码哈希: " + user.getPassword());
                
                // 测试密码验证
                String[] testPasswords = {"admin123", "123456", "admin", "password"};
                
                for (String testPassword : testPasswords) {
                    boolean matches = passwordEncoder.matches(testPassword, user.getPassword());
                    System.out.println("- 测试密码 '" + testPassword + "': " + (matches ? "✓ 正确" : "✗ 错误"));
                }
                
                // 测试findByUsernameAndStatus方法
                System.out.println("\n=== 测试findByUsernameAndStatus方法 ===");
                Optional<User> userWithStatus = userRepository.findByUsernameAndStatus("admin", 1);
                if (userWithStatus.isPresent()) {
                    System.out.println("✓ findByUsernameAndStatus('admin', 1) 找到用户");
                } else {
                    System.out.println("✗ findByUsernameAndStatus('admin', 1) 未找到用户");
                }
                
            } else {
                System.out.println("✗ 未找到admin用户");
                
                // 查看所有用户
                System.out.println("\n=== 查看所有用户 ===");
                userRepository.findAll().forEach(u -> {
                    System.out.println("用户: " + u.getUsername() + " (状态: " + u.getStatus() + ", 删除: " + u.getDeleted() + ")");
                });
            }
            
        } catch (Exception e) {
            System.err.println("测试过程中出错: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n=== 测试完成 ===");
    }
}
