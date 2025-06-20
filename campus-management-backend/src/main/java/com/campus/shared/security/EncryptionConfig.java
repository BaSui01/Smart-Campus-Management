package com.campus.shared.security;

import com.campus.shared.exception.EncryptionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PostLoad;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 数据加密配置类
 * 提供AES-256-GCM加密服务，用于敏感数据的字段级加密
 * 
 * 功能特性：
 * - AES-256-GCM加密算法（认证加密）
 * - 安全的密钥管理
 * - 随机IV生成
 * - Base64编码存储
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-18
 */
@Configuration
@Profile("!test")  // 测试环境不启用加密
public class EncryptionConfig {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    @Value("${campus.security.encryption.secret-key}")
    private String encryptionKey;

    /**
     * 数据加密服务Bean
     */
    @Bean
    public DataEncryptionService dataEncryptionService() {
        return new DataEncryptionService(encryptionKey);
    }

    /**
     * 数据加密服务实现类
     */
    public static class DataEncryptionService {
        
        private final SecretKey secretKey;
        private final SecureRandom secureRandom;

        public DataEncryptionService(String keyString) {
            this.secretKey = new SecretKeySpec(
                Base64.getDecoder().decode(keyString), 
                ALGORITHM
            );
            this.secureRandom = new SecureRandom();
        }

        /**
         * 加密敏感数据
         * 
         * @param plainText 明文数据
         * @return Base64编码的加密数据（包含IV）
         */
        public String encrypt(String plainText) {
            if (plainText == null || plainText.isEmpty()) {
                return plainText;
            }

            try {
                // 生成随机IV
                byte[] iv = new byte[GCM_IV_LENGTH];
                secureRandom.nextBytes(iv);

                // 初始化加密器
                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

                // 执行加密
                byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

                // 组合IV和加密数据
                byte[] encryptedWithIv = new byte[GCM_IV_LENGTH + encryptedData.length];
                System.arraycopy(iv, 0, encryptedWithIv, 0, GCM_IV_LENGTH);
                System.arraycopy(encryptedData, 0, encryptedWithIv, GCM_IV_LENGTH, encryptedData.length);

                // 返回Base64编码结果
                return Base64.getEncoder().encodeToString(encryptedWithIv);

            } catch (Exception e) {
                throw new EncryptionException("数据加密失败", e);
            }
        }

        /**
         * 解密敏感数据
         * 
         * @param encryptedText Base64编码的加密数据
         * @return 解密后的明文数据
         */
        public String decrypt(String encryptedText) {
            if (encryptedText == null || encryptedText.isEmpty()) {
                return encryptedText;
            }

            try {
                // Base64解码
                byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedText);

                // 提取IV和加密数据
                byte[] iv = new byte[GCM_IV_LENGTH];
                byte[] encryptedData = new byte[encryptedWithIv.length - GCM_IV_LENGTH];
                System.arraycopy(encryptedWithIv, 0, iv, 0, GCM_IV_LENGTH);
                System.arraycopy(encryptedWithIv, GCM_IV_LENGTH, encryptedData, 0, encryptedData.length);

                // 初始化解密器
                Cipher cipher = Cipher.getInstance(TRANSFORMATION);
                GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
                cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

                // 执行解密
                byte[] decryptedData = cipher.doFinal(encryptedData);

                return new String(decryptedData, StandardCharsets.UTF_8);

            } catch (Exception e) {
                throw new EncryptionException("数据解密失败", e);
            }
        }

        /**
         * 生成新的AES-256密钥（用于初始化）
         * 
         * @return Base64编码的密钥
         */
        public static String generateNewKey() {
            try {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
                keyGenerator.init(256);
                SecretKey secretKey = keyGenerator.generateKey();
                return Base64.getEncoder().encodeToString(secretKey.getEncoded());
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("密钥生成失败", e);
            }
        }

        /**
         * 验证密钥格式
         * 
         * @param keyString Base64编码的密钥
         * @return 是否为有效的AES-256密钥
         */
        public static boolean isValidKey(String keyString) {
            try {
                byte[] keyBytes = Base64.getDecoder().decode(keyString);
                return keyBytes.length == 32; // AES-256需要32字节密钥
            } catch (Exception e) {
                return false;
            }
        }
    }

    /**
     * 敏感数据字段加密注解
     * 用于标记需要加密的实体字段
     */
    public @interface EncryptedField {
        /**
         * 是否启用加密
         */
        boolean enabled() default true;
        
        /**
         * 加密算法类型
         */
        String algorithm() default "AES-256-GCM";
    }

    /**
     * JPA实体监听器，用于自动加密/解密
     */
    @Component
    public static class EncryptionEntityListener {

        private static DataEncryptionService encryptionService;

        @Autowired
        public void setEncryptionService(DataEncryptionService service) {
            EncryptionEntityListener.encryptionService = service;
        }

        /**
         * 保存前加密
         */
        @PrePersist
        public void prePersist(Object entity) {
            encryptFields(entity);
        }

        /**
         * 更新前加密
         */
        @PreUpdate
        public void preUpdate(Object entity) {
            encryptFields(entity);
        }

        /**
         * 加载后解密
         */
        @PostLoad
        public void postLoad(Object entity) {
            decryptFields(entity);
        }

        private void encryptFields(Object entity) {
            if (encryptionService == null) return;

            // 使用反射处理带有@EncryptedField注解的字段
            java.lang.reflect.Field[] fields = entity.getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                if (field.isAnnotationPresent(EncryptedField.class)) {
                    try {
                        field.setAccessible(true);
                        String value = (String) field.get(entity);
                        if (value != null && !value.isEmpty() && !isAlreadyEncrypted(value)) {
                            String encryptedValue = encryptionService.encrypt(value);
                            field.set(entity, encryptedValue);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("字段加密失败: " + field.getName(), e);
                    }
                }
            }
        }

        private void decryptFields(Object entity) {
            if (encryptionService == null) return;

            // 使用反射处理带有@EncryptedField注解的字段
            java.lang.reflect.Field[] fields = entity.getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                if (field.isAnnotationPresent(EncryptedField.class)) {
                    try {
                        field.setAccessible(true);
                        String value = (String) field.get(entity);
                        if (value != null && !value.isEmpty() && isAlreadyEncrypted(value)) {
                            String decryptedValue = encryptionService.decrypt(value);
                            field.set(entity, decryptedValue);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("字段解密失败: " + field.getName(), e);
                    }
                }
            }
        }

        /**
         * 检查字段是否已经加密（基于Base64格式判断）
         */
        private boolean isAlreadyEncrypted(String value) {
            try {
                // 简单检查：加密后的数据应该是Base64格式且长度较长
                return value.length() > 50 && value.matches("^[A-Za-z0-9+/]*={0,2}$");
            } catch (Exception e) {
                return false;
            }
        }
    }
}
