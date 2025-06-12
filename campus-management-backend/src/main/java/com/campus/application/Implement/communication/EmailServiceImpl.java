package com.campus.application.Implement.communication;

import com.campus.application.service.communication.EmailService;
import com.campus.domain.entity.academic.Grade;
import com.campus.domain.entity.finance.FeeItem;
import com.campus.domain.entity.organization.Student;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 邮件服务实现类
 * 
 * @author Campus Team
 * @since 2025-06-05
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${campus.mail.from-name:智慧校园管理系统}")
    private String fromName;

    @Override
    @Async
    public void sendGradeNotification(Student student, List<Grade> grades) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 设置邮件基本信息
            helper.setFrom(fromEmail, fromName);
            helper.setTo(student.getEmail());
            helper.setSubject("成绩通知 - " + student.getRealName());

            // 使用模板生成邮件内容
            Context context = new Context();
            context.setVariable("student", student);
            context.setVariable("grades", grades);
            context.setVariable("systemName", fromName);

            String content = templateEngine.process("email/grade-notification", context);
            helper.setText(content, true);

            // 发送邮件
            mailSender.send(message);

            logger.info("成绩通知邮件发送成功: {} - {}", student.getStudentNo(), student.getEmail());

        } catch (Exception e) {
            logger.error("成绩通知邮件发送失败: {} - {}", student.getStudentNo(), student.getEmail(), e);
        }
    }

    @Override
    @Async
    public void sendPaymentReminder(Student student, List<FeeItem> unpaidItems) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(student.getEmail());
            helper.setSubject("缴费提醒 - " + student.getRealName());

            // 计算总金额
            BigDecimal totalAmount = unpaidItems.stream()
                .map(FeeItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            Context context = new Context();
            context.setVariable("student", student);
            context.setVariable("unpaidItems", unpaidItems);
            context.setVariable("totalAmount", totalAmount);
            context.setVariable("systemName", fromName);

            String content = templateEngine.process("email/payment-reminder", context);
            helper.setText(content, true);

            mailSender.send(message);

            logger.info("缴费提醒邮件发送成功: {} - {}", student.getStudentNo(), student.getEmail());

        } catch (Exception e) {
            logger.error("缴费提醒邮件发送失败: {} - {}", student.getStudentNo(), student.getEmail(), e);
        }
    }

    @Override
    @Async
    public void sendSystemNotification(List<String> recipients, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(recipients.toArray(new String[0]));
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariable("content", content);
            context.setVariable("systemName", fromName);

            String htmlContent = templateEngine.process("email/system-notification", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            logger.info("系统通知邮件发送成功，收件人数量: {}", recipients.size());

        } catch (Exception e) {
            logger.error("系统通知邮件发送失败", e);
        }
    }

    @Override
    @Async
    public void sendStatisticsReport(String subject, String content, Map<String, Object> report) {
        try {
            // 获取管理员邮箱列表（这里简化处理，实际应该从数据库获取）
            List<String> adminEmails = List.of("admin@campus.edu", "academic@campus.edu");

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(adminEmails.toArray(new String[0]));
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariable("content", content);
            context.setVariable("report", report);
            context.setVariable("systemName", fromName);

            String htmlContent = templateEngine.process("email/statistics-report", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            logger.info("统计报告邮件发送成功，收件人数量: {}", adminEmails.size());

        } catch (Exception e) {
            logger.error("统计报告邮件发送失败", e);
        }
    }

    @Override
    @Async
    public void sendAdminNotification(String subject, String content) {
        try {
            // 获取管理员邮箱列表
            List<String> adminEmails = List.of("admin@campus.edu", "system@campus.edu");

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(adminEmails.toArray(new String[0]));
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariable("content", content);
            context.setVariable("systemName", fromName);

            String htmlContent = templateEngine.process("email/admin-notification", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            logger.info("管理员通知邮件发送成功，收件人数量: {}", adminEmails.size());

        } catch (Exception e) {
            logger.error("管理员通知邮件发送失败", e);
        }
    }

    @Override
    @Async
    public void sendWelcomeEmail(Student student, String temporaryPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(student.getEmail());
            helper.setSubject("欢迎加入智慧校园管理系统 - " + student.getRealName());

            Context context = new Context();
            context.setVariable("student", student);
            context.setVariable("temporaryPassword", temporaryPassword);
            context.setVariable("systemName", fromName);

            String content = templateEngine.process("email/welcome", context);
            helper.setText(content, true);

            mailSender.send(message);

            logger.info("欢迎邮件发送成功: {} - {}", student.getStudentNo(), student.getEmail());

        } catch (Exception e) {
            logger.error("欢迎邮件发送失败: {} - {}", student.getStudentNo(), student.getEmail(), e);
        }
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String email, String resetToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(email);
            helper.setSubject("密码重置 - " + fromName);

            Context context = new Context();
            context.setVariable("email", email);
            context.setVariable("resetToken", resetToken);
            context.setVariable("systemName", fromName);
            // 重置链接（实际应该是前端页面地址）
            context.setVariable("resetUrl", "http://localhost:8080/reset-password?token=" + resetToken);

            String content = templateEngine.process("email/password-reset", context);
            helper.setText(content, true);

            mailSender.send(message);

            logger.info("密码重置邮件发送成功: {}", email);

        } catch (Exception e) {
            logger.error("密码重置邮件发送失败: {}", email, e);
        }
    }
}
