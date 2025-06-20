package com.campus.shared.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

/**
 * 数据加密工具类
 * 提供AES-GCM加密/解密功能，用于敏感数据保护
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-18
 */
@Component
public class DataEncryptionUtil {

    private static final Logger logger = LoggerFactory.getLogger(DataEncryptionUtil.class);

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    @Value("${campus.security.encryption.key:Y2FtcHVzLWVuY3J5cHRpb24ta2V5LTIwMjQ=}")
    private String encryptionKey;

    private final SecureRandom secureRandom;

    public DataEncryptionUtil() {
        // 添加BouncyCastle提供者
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        this.secureRandom = new SecureRandom();
    }

    /**
     * 加密字符串数据
     * 
     * @param plainText 明文
     * @return 加密后的Base64编码字符串
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }

        try {
            SecretKey secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            // 生成随机IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

            byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // 将IV和加密数据合并
            byte[] encryptedWithIv = new byte[GCM_IV_LENGTH + encryptedData.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedData, 0, encryptedWithIv, GCM_IV_LENGTH, encryptedData.length);

            return Base64.getEncoder().encodeToString(encryptedWithIv);

        } catch (Exception e) {
            logger.error("数据加密失败", e);
            throw new RuntimeException("数据加密失败", e);
        }
    }

    /**
     * 解密字符串数据
     * 
     * @param encryptedText 加密的Base64编码字符串
     * @return 解密后的明文
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }

        try {
            SecretKey secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedText);

            // 提取IV和加密数据
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encryptedData = new byte[encryptedWithIv.length - GCM_IV_LENGTH];

            System.arraycopy(encryptedWithIv, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedWithIv, GCM_IV_LENGTH, encryptedData, 0, encryptedData.length);

            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new String(decryptedData, StandardCharsets.UTF_8);

        } catch (Exception e) {
            logger.error("数据解密失败", e);
            throw new RuntimeException("数据解密失败", e);
        }
    }

    /**
     * 加密敏感字段（如身份证号、手机号等）
     * 
     * @param sensitiveData 敏感数据
     * @return 加密后的数据
     */
    public String encryptSensitiveData(String sensitiveData) {
        if (sensitiveData == null || sensitiveData.trim().isEmpty()) {
            return sensitiveData;
        }

        // 添加时间戳和随机盐，增强安全性
        String dataWithSalt = sensitiveData + "|" + System.currentTimeMillis() + "|" + generateRandomSalt();
        return encrypt(dataWithSalt);
    }

    /**
     * 解密敏感字段
     * 
     * @param encryptedSensitiveData 加密的敏感数据
     * @return 解密后的原始数据
     */
    public String decryptSensitiveData(String encryptedSensitiveData) {
        if (encryptedSensitiveData == null || encryptedSensitiveData.trim().isEmpty()) {
            return encryptedSensitiveData;
        }

        String decryptedWithSalt = decrypt(encryptedSensitiveData);
        
        // 提取原始数据（去除时间戳和盐）
        String[] parts = decryptedWithSalt.split("\\|");
        return parts.length > 0 ? parts[0] : decryptedWithSalt;
    }

    /**
     * 生成密钥
     */
    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(encryptionKey);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }

    /**
     * 生成随机盐
     */
    private String generateRandomSalt() {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 生成新的加密密钥（用于初始化）
     */
    public static String generateNewEncryptionKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(256);
            SecretKey secretKey = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("生成加密密钥失败", e);
        }
    }

    /**
     * 验证加密密钥是否有效
     */
    public boolean isEncryptionKeyValid() {
        try {
            String testData = "test-encryption-key-validation";
            String encrypted = encrypt(testData);
            String decrypted = decrypt(encrypted);
            return testData.equals(decrypted);
        } catch (Exception e) {
            logger.error("加密密钥验证失败", e);
            return false;
        }
    }
}
