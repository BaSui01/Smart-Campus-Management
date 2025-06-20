package com.campus.shared.util;

import com.campus.shared.security.EncryptionConfig.DataEncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 加密密钥生成工具
 * 用于生成和管理系统加密密钥
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-18
 */
public class EncryptionKeyGenerator {

    private static final Logger logger = LoggerFactory.getLogger(EncryptionKeyGenerator.class);
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;

    /**
     * 生成新的AES-256密钥
     * 
     * @return Base64编码的密钥字符串
     */
    public static String generateAES256Key() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(KEY_SIZE, new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            
            logger.info("成功生成AES-256密钥，长度: {} 字节", secretKey.getEncoded().length);
            return encodedKey;
        } catch (NoSuchAlgorithmException e) {
            logger.error("生成AES-256密钥失败", e);
            throw new RuntimeException("密钥生成失败", e);
        }
    }

    /**
     * 生成JWT密钥
     * 
     * @return Base64编码的JWT密钥字符串
     */
    public static String generateJWTKey() {
        try {
            // 生成512位的密钥用于JWT签名
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            keyGenerator.init(256, new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            
            logger.info("成功生成JWT密钥，长度: {} 字节", secretKey.getEncoded().length);
            return encodedKey;
        } catch (NoSuchAlgorithmException e) {
            logger.error("生成JWT密钥失败", e);
            throw new RuntimeException("JWT密钥生成失败", e);
        }
    }

    /**
     * 验证密钥格式
     * 
     * @param keyString Base64编码的密钥字符串
     * @return 是否为有效的AES-256密钥
     */
    public static boolean validateAES256Key(String keyString) {
        return DataEncryptionService.isValidKey(keyString);
    }

    /**
     * 生成随机密码
     * 
     * @param length 密码长度
     * @return 随机密码
     */
    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }

    /**
     * 生成安全的随机字符串
     * 
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String generateSecureRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            result.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return result.toString();
    }

    /**
     * 主方法 - 用于生成密钥
     */
    public static void main(String[] args) {
        System.out.println("=== 智慧校园管理系统密钥生成工具 ===");
        System.out.println();
        
        // 生成AES-256加密密钥
        String encryptionKey = generateAES256Key();
        System.out.println("AES-256加密密钥:");
        System.out.println(encryptionKey);
        System.out.println();
        
        // 生成JWT密钥
        String jwtKey = generateJWTKey();
        System.out.println("JWT签名密钥:");
        System.out.println(jwtKey);
        System.out.println();
        
        // 生成随机密码
        String randomPassword = generateRandomPassword(16);
        System.out.println("随机密码 (16位):");
        System.out.println(randomPassword);
        System.out.println();
        
        // 生成环境变量配置示例
        System.out.println("=== 环境变量配置示例 ===");
        System.out.println("# 加密配置");
        System.out.println("ENCRYPTION_SECRET_KEY=" + encryptionKey);
        System.out.println("JWT_SECRET=" + jwtKey);
        System.out.println();
        System.out.println("# 数据库配置");
        System.out.println("DB_PASSWORD=" + generateRandomPassword(12));
        System.out.println();
        System.out.println("# Redis配置");
        System.out.println("REDIS_PASSWORD=" + generateRandomPassword(12));
        System.out.println();
        System.out.println("# SSL配置");
        System.out.println("SSL_KEYSTORE_PASSWORD=" + generateRandomPassword(16));
        System.out.println();
        
        // 验证生成的密钥
        System.out.println("=== 密钥验证 ===");
        System.out.println("AES-256密钥验证: " + (validateAES256Key(encryptionKey) ? "✓ 有效" : "✗ 无效"));
        System.out.println("JWT密钥长度: " + Base64.getDecoder().decode(jwtKey).length + " 字节");
        
        System.out.println();
        System.out.println("=== 使用说明 ===");
        System.out.println("1. 将上述环境变量添加到系统环境变量中");
        System.out.println("2. 或者创建 .env 文件并在应用启动时加载");
        System.out.println("3. 确保密钥安全存储，不要提交到版本控制系统");
        System.out.println("4. 生产环境建议使用专业的密钥管理服务");
    }
}
