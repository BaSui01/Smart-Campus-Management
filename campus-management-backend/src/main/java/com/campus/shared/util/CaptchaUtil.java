package com.campus.shared.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;

/**
 * 验证码工具类
 */
@Component
public class CaptchaUtil {
    
    private static final int WIDTH = 150;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 6;
    private static final String CHARS = "123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    
    private Random random = new Random();
    
    /**
     * 生成验证码图片
     * @return CaptchaResult 包含验证码文本和图片字节数组
     */
    public CaptchaResult generateCaptcha() {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        // 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 填充背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        // 生成验证码文本
        String code = generateCode();
        
        // 绘制验证码
        drawCode(g, code);
        
        // 添加干扰线
        drawNoise(g);
        
        g.dispose();
        
        // 转换为字节数组
        byte[] imageBytes = toByteArray(image);
        
        return new CaptchaResult(code, imageBytes);
    }
    
    /**
     * 生成随机验证码文本
     */
    private String generateCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return code.toString();
    }
    
    /**
     * 绘制验证码文本
     */
    private void drawCode(Graphics2D g, String code) {
        Font[] fonts = {
            new Font("Arial", Font.BOLD, 24),
            new Font("Times New Roman", Font.BOLD, 24),
            new Font("Courier New", Font.BOLD, 24)
        };
        
        int x = 10;
        for (int i = 0; i < code.length(); i++) {
            // 随机字体
            g.setFont(fonts[random.nextInt(fonts.length)]);

            // 随机颜色
            g.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));

            // 随机角度
            double angle = (random.nextDouble() - 0.5) * 0.4;
            g.rotate(angle, x + 12, HEIGHT / 2);

            // 绘制字符
            g.drawString(String.valueOf(code.charAt(i)), x, HEIGHT / 2 + 8);

            // 恢复角度
            g.rotate(-angle, x + 12, HEIGHT / 2);

            x += 22;
        }
    }
    
    /**
     * 绘制干扰线
     */
    private void drawNoise(Graphics2D g) {
        // 绘制干扰线
        for (int i = 0; i < 5; i++) {
            g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            g.drawLine(random.nextInt(WIDTH), random.nextInt(HEIGHT), 
                      random.nextInt(WIDTH), random.nextInt(HEIGHT));
        }
        
        // 绘制干扰点
        for (int i = 0; i < 50; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.fillOval(random.nextInt(WIDTH), random.nextInt(HEIGHT), 2, 2);
        }
    }
    
    /**
     * 将BufferedImage转换为字节数组
     */
    private byte[] toByteArray(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }
    }
    
    /**
     * 验证码结果类
     */
    public static class CaptchaResult {
        private final String code;
        private final byte[] imageBytes;
        
        public CaptchaResult(String code, byte[] imageBytes) {
            this.code = code;
            this.imageBytes = imageBytes;
        }
        
        public String getCode() {
            return code;
        }
        
        public byte[] getImageBytes() {
            return imageBytes;
        }
    }
}
