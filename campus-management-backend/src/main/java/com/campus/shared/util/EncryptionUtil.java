package com.campus.shared.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 数据加密工具类
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Slf4j
public class EncryptionUtil {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String SHA256_ALGORITHM = "SHA-256";
    private static final String MD5_ALGORITHM = "MD5";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    // 默认密钥（实际使用时应该从配置文件或环境变量中获取）
    private static final String DEFAULT_SECRET_KEY = "CampusManagementSystem2025SecretKey";

    /**
     * 生成AES密钥
     * 
     * @return Base64编码的密钥
     */
    public static String generateAESKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            log.error("生成AES密钥失败", e);
            throw new RuntimeException("生成AES密钥失败", e);
        }
    }

    /**
     * AES加密
     * 
     * @param plainText 明文
     * @param secretKey 密钥
     * @return 加密后的Base64字符串
     */
    public static String encryptAES(String plainText, String secretKey) {
        if (!StringUtils.hasText(plainText)) {
            return plainText;
        }

        try {
            SecretKeySpec keySpec = new SecretKeySpec(
                    Base64.getDecoder().decode(secretKey), AES_ALGORITHM);

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            
            // 生成随机IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
            byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            // 将IV和加密数据合并
            byte[] encryptedWithIv = new byte[GCM_IV_LENGTH + encryptedData.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedData, 0, encryptedWithIv, GCM_IV_LENGTH, encryptedData.length);
            
            return Base64.getEncoder().encodeToString(encryptedWithIv);
        } catch (Exception e) {
            log.error("AES加密失败", e);
            throw new RuntimeException("AES加密失败", e);
        }
    }

    /**
     * AES解密
     * 
     * @param encryptedText 加密的Base64字符串
     * @param secretKey 密钥
     * @return 解密后的明文
     */
    public static String decryptAES(String encryptedText, String secretKey) {
        if (!StringUtils.hasText(encryptedText)) {
            return encryptedText;
        }

        try {
            SecretKeySpec keySpec = new SecretKeySpec(
                    Base64.getDecoder().decode(secretKey), AES_ALGORITHM);

            byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedText);
            
            // 分离IV和加密数据
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encryptedData = new byte[encryptedWithIv.length - GCM_IV_LENGTH];
            System.arraycopy(encryptedWithIv, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedWithIv, GCM_IV_LENGTH, encryptedData, 0, encryptedData.length);
            
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
            
            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("AES解密失败", e);
            throw new RuntimeException("AES解密失败", e);
        }
    }

    /**
     * 使用默认密钥进行AES加密
     * 
     * @param plainText 明文
     * @return 加密后的Base64字符串
     */
    public static String encryptAES(String plainText) {
        String key = Base64.getEncoder().encodeToString(
                DEFAULT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        return encryptAES(plainText, key);
    }

    /**
     * 使用默认密钥进行AES解密
     * 
     * @param encryptedText 加密的Base64字符串
     * @return 解密后的明文
     */
    public static String decryptAES(String encryptedText) {
        String key = Base64.getEncoder().encodeToString(
                DEFAULT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        return decryptAES(encryptedText, key);
    }

    /**
     * SHA-256哈希
     * 
     * @param input 输入字符串
     * @return SHA-256哈希值
     */
    public static String sha256(String input) {
        if (!StringUtils.hasText(input)) {
            return input;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(SHA256_ALGORITHM);
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256哈希失败", e);
            throw new RuntimeException("SHA-256哈希失败", e);
        }
    }

    /**
     * MD5哈希
     * 
     * @param input 输入字符串
     * @return MD5哈希值
     */
    public static String md5(String input) {
        if (!StringUtils.hasText(input)) {
            return input;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(MD5_ALGORITHM);
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("MD5哈希失败", e);
            throw new RuntimeException("MD5哈希失败", e);
        }
    }

    /**
     * 加盐SHA-256哈希
     * 
     * @param input 输入字符串
     * @param salt 盐值
     * @return 加盐哈希值
     */
    public static String sha256WithSalt(String input, String salt) {
        return sha256(input + salt);
    }

    /**
     * 生成随机盐值
     * 
     * @param length 盐值长度
     * @return 随机盐值
     */
    public static String generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 生成随机盐值（默认16字节）
     * 
     * @return 随机盐值
     */
    public static String generateSalt() {
        return generateSalt(16);
    }

    /**
     * 敏感信息脱敏
     * 
     * @param sensitive 敏感信息
     * @param type 脱敏类型
     * @return 脱敏后的信息
     */
    public static String maskSensitiveInfo(String sensitive, SensitiveType type) {
        if (!StringUtils.hasText(sensitive)) {
            return sensitive;
        }

        switch (type) {
            case PHONE:
                return maskPhone(sensitive);
            case EMAIL:
                return maskEmail(sensitive);
            case ID_CARD:
                return maskIdCard(sensitive);
            case BANK_CARD:
                return maskBankCard(sensitive);
            case NAME:
                return maskName(sensitive);
            default:
                return sensitive;
        }
    }

    /**
     * 手机号脱敏
     */
    private static String maskPhone(String phone) {
        if (phone.length() >= 11) {
            return phone.substring(0, 3) + "****" + phone.substring(7);
        }
        return phone;
    }

    /**
     * 邮箱脱敏
     */
    private static String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex > 0) {
            String username = email.substring(0, atIndex);
            String domain = email.substring(atIndex);
            if (username.length() > 2) {
                return username.substring(0, 2) + "***" + domain;
            }
        }
        return email;
    }

    /**
     * 身份证号脱敏
     */
    private static String maskIdCard(String idCard) {
        if (idCard.length() >= 18) {
            return idCard.substring(0, 6) + "********" + idCard.substring(14);
        }
        return idCard;
    }

    /**
     * 银行卡号脱敏
     */
    private static String maskBankCard(String bankCard) {
        if (bankCard.length() >= 16) {
            return bankCard.substring(0, 4) + " **** **** " + bankCard.substring(bankCard.length() - 4);
        }
        return bankCard;
    }

    /**
     * 姓名脱敏
     */
    private static String maskName(String name) {
        if (name.length() > 1) {
            return name.substring(0, 1) + "*".repeat(name.length() - 1);
        }
        return name;
    }

    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * 敏感信息类型枚举
     */
    public enum SensitiveType {
        PHONE,      // 手机号
        EMAIL,      // 邮箱
        ID_CARD,    // 身份证号
        BANK_CARD,  // 银行卡号
        NAME        // 姓名
    }
}
