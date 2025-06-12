package com.campus.application.service.communication;

import com.campus.domain.entity.academic.Grade;
import com.campus.domain.entity.finance.FeeItem;
import com.campus.domain.entity.organization.Student;

import java.util.List;
import java.util.Map;

/**
 * 邮件服务接口
 * 
 * @author Campus Team
 * @since 2025-06-05
 */
public interface EmailService {

    /**
     * 发送成绩通知邮件
     * 
     * @param student 学生信息
     * @param grades 成绩列表
     */
    void sendGradeNotification(Student student, List<Grade> grades);

    /**
     * 发送缴费提醒邮件
     * 
     * @param student 学生信息
     * @param unpaidItems 未缴费项目列表
     */
    void sendPaymentReminder(Student student, List<FeeItem> unpaidItems);

    /**
     * 发送系统通知邮件
     * 
     * @param recipients 收件人列表
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    void sendSystemNotification(List<String> recipients, String subject, String content);

    /**
     * 发送统计报告邮件
     * 
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param report 报告数据
     */
    void sendStatisticsReport(String subject, String content, Map<String, Object> report);

    /**
     * 发送管理员通知邮件
     * 
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    void sendAdminNotification(String subject, String content);

    /**
     * 发送欢迎邮件
     * 
     * @param student 学生信息
     * @param temporaryPassword 临时密码
     */
    void sendWelcomeEmail(Student student, String temporaryPassword);

    /**
     * 发送密码重置邮件
     * 
     * @param email 邮箱地址
     * @param resetToken 重置令牌
     */
    void sendPasswordResetEmail(String email, String resetToken);
}
