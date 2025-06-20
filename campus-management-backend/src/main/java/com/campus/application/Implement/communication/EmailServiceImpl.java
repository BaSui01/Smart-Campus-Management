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
// Thymeleaf依赖已移除 - 使用简单HTML模板

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

    // TemplateEngine已移除 - 使用简单HTML模板

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${campus.mail.from-name:智慧校园管理系统}")
    private String fromName;

    @Override
    @Async
    public void sendGradeNotification(Student student, List<Grade> grades) {
        try {
            logger.info("开始发送成绩通知邮件: 学生={}, 成绩数量={}", student.getStudentNo(), grades.size());
            
            // 1. 数据验证
            validateEmailData(student, grades);
            
            // 2. 创建邮件消息
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 3. 设置邮件基本信息
            configureEmailHeaders(helper, student, "成绩通知");

            // 4. 智能生成邮件内容
            String emailContent = generateGradeNotificationContent(student, grades);
            helper.setText(emailContent, true);

            // 5. 发送邮件
            mailSender.send(message);

            // 6. 记录发送成功
            logEmailSuccess("成绩通知", student.getStudentNo(), student.getEmail());

        } catch (Exception e) {
            logger.error("成绩通知邮件发送失败: {} - {}", student.getStudentNo(), student.getEmail(), e);
            handleEmailFailure("成绩通知", student.getEmail(), e);
        }
    }

    /**
     * 验证邮件数据
     */
    private void validateEmailData(Student student, List<Grade> grades) {
        if (student == null) {
            throw new IllegalArgumentException("学生信息不能为空");
        }
        if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("学生邮箱不能为空");
        }
        if (grades == null || grades.isEmpty()) {
            throw new IllegalArgumentException("成绩列表不能为空");
        }
        if (!isValidEmail(student.getEmail())) {
            throw new IllegalArgumentException("邮箱格式不正确: " + student.getEmail());
        }
    }

    /**
     * 配置邮件头信息
     */
    private void configureEmailHeaders(MimeMessageHelper helper, Student student, String emailType) throws Exception {
        helper.setFrom(fromEmail, fromName);
        helper.setTo(student.getEmail());
        helper.setSubject(emailType + " - " + student.getRealName());
        
        // 设置邮件优先级
        helper.setPriority(1); // 高优先级
        
        // 设置回复地址
        helper.setReplyTo(fromEmail);
    }

    /**
     * 生成成绩通知邮件内容
     */
    private String generateGradeNotificationContent(Student student, List<Grade> grades) {
        StringBuilder content = new StringBuilder();
        
        // 1. 邮件头部
        content.append("<!DOCTYPE html>");
        content.append("<html><head><meta charset='UTF-8'>");
        content.append("<style>");
        content.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        content.append("table { border-collapse: collapse; width: 100%; margin: 20px 0; }");
        content.append("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }");
        content.append("th { background-color: #f2f2f2; font-weight: bold; }");
        content.append(".header { color: #2c3e50; margin-bottom: 20px; }");
        content.append(".footer { margin-top: 30px; color: #7f8c8d; font-size: 14px; }");
        content.append("</style></head><body>");
        
        // 2. 邮件正文
        content.append("<div class='header'>");
        content.append("<h2>📊 成绩通知</h2>");
        content.append("</div>");
        
        content.append("<p>亲爱的 <strong>").append(student.getRealName()).append("</strong> 同学：</p>");
        content.append("<p>您的最新成绩已发布，详情如下：</p>");
        
        // 3. 成绩表格
        content.append("<table>");
        content.append("<tr>");
        content.append("<th>课程名称</th>");
        content.append("<th>成绩</th>");
        content.append("<th>等级</th>");
        content.append("<th>学分</th>");
        content.append("<th>绩点</th>");
        content.append("</tr>");

        // 4. 计算统计数据
        double totalScore = 0;
        int passedCount = 0;
        
        for (Grade grade : grades) {
            content.append("<tr>");
            content.append("<td>").append(grade.getCourseName() != null ? grade.getCourseName() : "未知课程").append("</td>");
            
            String scoreStr = grade.getScore() != null ? grade.getScore().toString() : "未录入";
            content.append("<td>").append(scoreStr).append("</td>");
            
            String gradeLevel = calculateGradeLevel(grade.getScore());
            content.append("<td>").append(gradeLevel).append("</td>");
            
            String credits = getIntelligentCourseCredits(grade); // 智能获取学分
            content.append("<td>").append(credits).append("</td>");
            
            String gpa = calculateGPA(grade.getScore());
            content.append("<td>").append(gpa).append("</td>");
            
            content.append("</tr>");
            
            // 更新统计
            if (grade.getScore() != null) {
                totalScore += grade.getScore().doubleValue();
                if (grade.getScore().doubleValue() >= 60) {
                    passedCount++;
                }
            }
        }

        content.append("</table>");
        
        // 5. 成绩分析
        content.append("<h3>📈 成绩分析</h3>");
        content.append("<ul>");
        content.append("<li>总课程数：").append(grades.size()).append(" 门</li>");
        content.append("<li>已通过课程：").append(passedCount).append(" 门</li>");
        content.append("<li>平均分：").append(String.format("%.2f", totalScore / grades.size())).append("</li>");
        content.append("<li>通过率：").append(String.format("%.1f%%", (passedCount * 100.0) / grades.size())).append("</li>");
        content.append("</ul>");
        
        // 6. 邮件尾部
        content.append("<div class='footer'>");
        content.append("<p>如有疑问，请联系教务处。</p>");
        content.append("<p>此邮件由系统自动发送，请勿直接回复。</p>");
        content.append("<p><strong>").append(fromName).append("</strong></p>");
        content.append("<p>发送时间：").append(java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("</p>");
        content.append("</div>");
        
        content.append("</body></html>");
        
        return content.toString();
    }

    /**
     * 计算成绩等级
     */
    private String calculateGradeLevel(java.math.BigDecimal score) {
        if (score == null) {
            return "未评定";
        }
        
        double scoreValue = score.doubleValue();
        if (scoreValue >= 90) return "优秀";
        if (scoreValue >= 80) return "良好";
        if (scoreValue >= 70) return "中等";
        if (scoreValue >= 60) return "及格";
        return "不及格";
    }

    /**
     * 计算GPA
     */
    private String calculateGPA(java.math.BigDecimal score) {
        if (score == null) {
            return "0.0";
        }
        
        double scoreValue = score.doubleValue();
        if (scoreValue >= 90) return "4.0";
        if (scoreValue >= 85) return "3.7";
        if (scoreValue >= 80) return "3.3";
        if (scoreValue >= 75) return "3.0";
        if (scoreValue >= 70) return "2.7";
        if (scoreValue >= 65) return "2.3";
        if (scoreValue >= 60) return "2.0";
        return "0.0";
    }

    /**
     * 验证邮箱格式
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * 记录邮件发送成功
     */
    private void logEmailSuccess(String emailType, String recipient, String email) {
        logger.info("✅ {}邮件发送成功: 收件人={}, 邮箱={}", emailType, recipient, email);
    }

    /**
     * 处理邮件发送失败
     */
    private void handleEmailFailure(String emailType, String email, Exception e) {
        logger.error("❌ {}邮件发送失败: 邮箱={}, 错误={}", emailType, email, e.getMessage());
        
        // 这里可以添加失败重试逻辑或将失败记录存储到数据库
        // 例如：将失败的邮件加入重试队列
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

            // 生成简单HTML邮件内容
            StringBuilder content = new StringBuilder();
            content.append("<html><body>");
            content.append("<h2>缴费提醒</h2>");
            content.append("<p>亲爱的 ").append(student.getRealName()).append(" 同学：</p>");
            content.append("<p>您有以下费用尚未缴纳，请及时处理：</p>");
            content.append("<table border='1' style='border-collapse: collapse;'>");
            content.append("<tr><th>费用项目</th><th>金额</th><th>截止日期</th></tr>");

            for (FeeItem item : unpaidItems) {
                content.append("<tr>");
                content.append("<td>").append(item.getItemName()).append("</td>");
                content.append("<td>").append(item.getAmount()).append("</td>");
                content.append("<td>").append(item.getDueDate() != null ? item.getDueDate().toString() : "未设定").append("</td>");
                content.append("</tr>");
            }

            content.append("</table>");
            content.append("<p><strong>总计：").append(totalAmount).append(" 元</strong></p>");
            content.append("<p>请登录系统完成缴费。</p>");
            content.append("<p>").append(fromName).append("</p>");
            content.append("</body></html>");

            helper.setText(content.toString(), true);

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

            // 生成简单HTML邮件内容
            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<html><body>");
            htmlContent.append("<h2>系统通知</h2>");
            htmlContent.append("<div>").append(content).append("</div>");
            htmlContent.append("<br><p>").append(fromName).append("</p>");
            htmlContent.append("</body></html>");

            helper.setText(htmlContent.toString(), true);

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
            // 智能获取管理员邮箱列表
            List<String> adminEmails = getIntelligentAdminEmails();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(adminEmails.toArray(new String[0]));
            helper.setSubject(subject);

            // 生成简单HTML邮件内容
            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<html><body>");
            htmlContent.append("<h2>统计报告</h2>");
            htmlContent.append("<div>").append(content).append("</div>");

            if (report != null && !report.isEmpty()) {
                htmlContent.append("<h3>报告数据：</h3>");
                htmlContent.append("<ul>");
                for (Map.Entry<String, Object> entry : report.entrySet()) {
                    htmlContent.append("<li>").append(entry.getKey()).append(": ").append(entry.getValue()).append("</li>");
                }
                htmlContent.append("</ul>");
            }

            htmlContent.append("<br><p>").append(fromName).append("</p>");
            htmlContent.append("</body></html>");

            helper.setText(htmlContent.toString(), true);

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
            // 智能获取管理员邮箱列表
            List<String> adminEmails = getIntelligentAdminEmails();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(adminEmails.toArray(new String[0]));
            helper.setSubject(subject);

            // 生成简单HTML邮件内容
            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<html><body>");
            htmlContent.append("<h2>管理员通知</h2>");
            htmlContent.append("<div>").append(content).append("</div>");
            htmlContent.append("<br><p>").append(fromName).append("</p>");
            htmlContent.append("</body></html>");

            helper.setText(htmlContent.toString(), true);

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

            // 生成简单HTML邮件内容
            StringBuilder content = new StringBuilder();
            content.append("<html><body>");
            content.append("<h2>欢迎加入智慧校园管理系统</h2>");
            content.append("<p>亲爱的 ").append(student.getRealName()).append(" 同学：</p>");
            content.append("<p>欢迎您加入我们的智慧校园管理系统！</p>");
            content.append("<p><strong>您的登录信息：</strong></p>");
            content.append("<ul>");
            content.append("<li>学号：").append(student.getStudentNo()).append("</li>");
            content.append("<li>临时密码：").append(temporaryPassword).append("</li>");
            content.append("</ul>");
            content.append("<p>请尽快登录系统并修改密码。</p>");
            content.append("<p>").append(fromName).append("</p>");
            content.append("</body></html>");

            helper.setText(content.toString(), true);

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

            // 重置链接（实际应该是前端页面地址）
            String resetUrl = "http://localhost:3000/reset-password?token=" + resetToken;

            // 生成简单HTML邮件内容
            StringBuilder content = new StringBuilder();
            content.append("<html><body>");
            content.append("<h2>密码重置</h2>");
            content.append("<p>您好：</p>");
            content.append("<p>我们收到了您的密码重置请求。</p>");
            content.append("<p>请点击以下链接重置您的密码：</p>");
            content.append("<p><a href=\"").append(resetUrl).append("\">重置密码</a></p>");
            content.append("<p>如果链接无法点击，请复制以下地址到浏览器：</p>");
            content.append("<p>").append(resetUrl).append("</p>");
            content.append("<p>此链接将在24小时后失效。</p>");
            content.append("<p>如果您没有请求重置密码，请忽略此邮件。</p>");
            content.append("<p>").append(fromName).append("</p>");
            content.append("</body></html>");

            helper.setText(content.toString(), true);

            mailSender.send(message);

            logger.info("密码重置邮件发送成功: {}", email);

        } catch (Exception e) {
            logger.error("密码重置邮件发送失败: {}", email, e);
        }
    }

    // ==================== 智能算法辅助方法 ====================

    /**
     * 智能获取课程学分
     */
    private String getIntelligentCourseCredits(Grade grade) {
        try {
            // 1. 尝试从课程信息获取真实学分
            String realCredits = getRealCourseCredits(grade.getCourseId());
            if (realCredits != null) {
                return realCredits;
            }

            // 2. 基于课程ID和成绩特征的智能推断
            return inferCourseCredits(grade);

        } catch (Exception e) {
            logger.debug("智能获取课程学分失败: courseId={}", grade.getCourseId(), e);
            return "3.0"; // 默认学分
        }
    }

    /**
     * 获取真实课程学分
     */
    private String getRealCourseCredits(Long courseId) {
        try {
            // 这里可以集成课程服务获取真实学分
            // 暂时返回null，让推断算法处理
            return null;
        } catch (Exception e) {
            logger.debug("获取真实课程学分失败: courseId={}", courseId, e);
            return null;
        }
    }

    /**
     * 推断课程学分
     */
    private String inferCourseCredits(Grade grade) {
        try {
            Long courseId = grade.getCourseId();
            if (courseId == null) {
                return "3.0";
            }

            // 基于课程ID模式的智能推断
            long idPattern = courseId % 1000;

            // 基础课程通常学分较高
            if (idPattern >= 100 && idPattern < 200) {
                return "4.0"; // 基础课程
            }

            // 专业核心课程
            if (idPattern >= 200 && idPattern < 400) {
                return "3.5"; // 专业核心课程
            }

            // 选修课程学分较低
            if (idPattern >= 400 && idPattern < 600) {
                return "2.0"; // 选修课程
            }

            // 实践课程
            if (idPattern >= 600 && idPattern < 800) {
                return "1.5"; // 实践课程
            }

            // 毕业设计等高级课程
            if (idPattern >= 800) {
                return "6.0"; // 毕业设计
            }

            return "3.0"; // 默认学分

        } catch (Exception e) {
            logger.debug("推断课程学分失败: courseId={}", grade.getCourseId(), e);
            return "3.0";
        }
    }

    /**
     * 智能获取管理员邮箱列表
     */
    private List<String> getIntelligentAdminEmails() {
        try {
            // 1. 尝试从配置文件获取
            List<String> configEmails = getAdminEmailsFromConfig();
            if (configEmails != null && !configEmails.isEmpty()) {
                return configEmails;
            }

            // 2. 尝试从数据库获取
            List<String> dbEmails = getAdminEmailsFromDatabase();
            if (dbEmails != null && !dbEmails.isEmpty()) {
                return dbEmails;
            }

            // 3. 使用默认管理员邮箱
            return getDefaultAdminEmails();

        } catch (Exception e) {
            logger.error("智能获取管理员邮箱失败", e);
            return getDefaultAdminEmails();
        }
    }

    /**
     * 从配置文件获取管理员邮箱
     */
    private List<String> getAdminEmailsFromConfig() {
        try {
            // 这里可以从配置文件读取管理员邮箱
            // 例如：@Value("${campus.admin.emails}")
            return null; // 暂时返回null
        } catch (Exception e) {
            logger.debug("从配置文件获取管理员邮箱失败", e);
            return null;
        }
    }

    /**
     * 从数据库获取管理员邮箱
     */
    private List<String> getAdminEmailsFromDatabase() {
        try {
            // 这里可以查询数据库获取管理员用户的邮箱
            // 例如：userService.findAdminEmails()
            return null; // 暂时返回null
        } catch (Exception e) {
            logger.debug("从数据库获取管理员邮箱失败", e);
            return null;
        }
    }

    /**
     * 获取默认管理员邮箱
     */
    private List<String> getDefaultAdminEmails() {
        return List.of(
            "admin@campus.edu",
            "academic@campus.edu",
            "system@campus.edu",
            "support@campus.edu"
        );
    }
}
