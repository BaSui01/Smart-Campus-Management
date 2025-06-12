package com.campus.infrastructure;

import com.campus.application.service.communication.EmailService;
import com.campus.application.service.finance.PaymentRecordService;
import com.campus.domain.entity.finance.FeeItem;
import com.campus.domain.entity.organization.Student;
import com.campus.shared.util.RedisDistributedLock;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 缴费提醒定时任务
 * 
 * @author Campus Team
 * @since 2025-06-05
 */
@Component
public class PaymentReminderJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(PaymentReminderJob.class);
    
    private static final String LOCK_KEY = "payment_reminder_job";

    @Autowired
    private PaymentRecordService paymentRecordService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RedisDistributedLock distributedLock;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String lockValue = distributedLock.tryLock(LOCK_KEY, 300); // 5分钟锁定时间
        
        if (lockValue == null) {
            logger.warn("缴费提醒任务正在执行中，跳过本次执行");
            return;
        }

        try {
            logger.info("开始执行缴费提醒任务");
            long startTime = System.currentTimeMillis();
            
            // 查找未缴费的学生
            List<Student> unpaidStudents = paymentRecordService.findUnpaidStudents();
            logger.info("找到未缴费学生数量: {}", unpaidStudents.size());
            
            if (unpaidStudents.isEmpty()) {
                logger.info("没有需要发送缴费提醒的学生");
                return;
            }
            
            // 按年级分组发送提醒
            Map<String, List<Student>> studentsByGrade = unpaidStudents.stream()
                .collect(Collectors.groupingBy(Student::getGrade));
            
            int totalSent = 0;
            int totalFailed = 0;
            
            for (Map.Entry<String, List<Student>> entry : studentsByGrade.entrySet()) {
                String grade = entry.getKey();
                List<Student> students = entry.getValue();
                
                logger.info("开始为 {} 年级发送缴费提醒，学生数量: {}", grade, students.size());
                
                for (Student student : students) {
                    try {
                        // 获取学生的未缴费项目
                        List<FeeItem> unpaidItems = paymentRecordService.getUnpaidFeeItems(student.getId());
                        
                        if (!unpaidItems.isEmpty()) {
                            // 发送缴费提醒邮件
                            emailService.sendPaymentReminder(student, unpaidItems);
                            totalSent++;
                            
                            logger.debug("缴费提醒邮件发送成功: {} - {}", 
                                       student.getStudentNo(), student.getRealName());
                        }
                        
                        // 避免发送过于频繁，添加小延迟
                        Thread.sleep(100);
                        
                    } catch (Exception e) {
                        totalFailed++;
                        logger.error("发送缴费提醒失败: {} - {}", 
                                   student.getStudentNo(), student.getRealName(), e);
                    }
                }
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            logger.info("缴费提醒任务执行完成 - 总学生数: {}, 发送成功: {}, 发送失败: {}, 耗时: {}ms", 
                       unpaidStudents.size(), totalSent, totalFailed, duration);
            
            // 发送管理员汇总报告
            sendAdminSummaryReport(unpaidStudents.size(), totalSent, totalFailed, studentsByGrade);
            
        } catch (Exception e) {
            logger.error("缴费提醒任务执行失败", e);
            throw new JobExecutionException("缴费提醒任务执行失败", e);
        } finally {
            // 释放分布式锁
            distributedLock.releaseLock(LOCK_KEY, lockValue);
        }
    }

    /**
     * 发送管理员汇总报告
     */
    private void sendAdminSummaryReport(int totalStudents, int sentCount, int failedCount, 
                                      Map<String, List<Student>> studentsByGrade) {
        try {
            logger.info("开始发送缴费提醒汇总报告给管理员");
            
            String subject = "智慧校园管理系统 - 缴费提醒执行报告";
            String content = buildSummaryReportContent(totalStudents, sentCount, failedCount, studentsByGrade);
            
            emailService.sendAdminNotification(subject, content);
            
            logger.info("缴费提醒汇总报告发送完成");
            
        } catch (Exception e) {
            logger.error("发送缴费提醒汇总报告失败", e);
            // 汇总报告发送失败不应该影响整个任务的执行
        }
    }

    /**
     * 构建汇总报告内容
     */
    private String buildSummaryReportContent(int totalStudents, int sentCount, int failedCount,
                                           Map<String, List<Student>> studentsByGrade) {
        StringBuilder content = new StringBuilder();
        
        content.append("智慧校园管理系统 - 缴费提醒执行报告\n");
        content.append("==========================================\n\n");
        
        content.append("执行时间: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        content.append("任务类型: 缴费提醒推送\n\n");
        
        // 总体统计
        content.append("【执行统计】\n");
        content.append("未缴费学生总数: ").append(totalStudents).append("\n");
        content.append("提醒发送成功: ").append(sentCount).append("\n");
        content.append("提醒发送失败: ").append(failedCount).append("\n");
        content.append("成功率: ").append(totalStudents > 0 ? String.format("%.2f%%", (double) sentCount / totalStudents * 100) : "0%").append("\n\n");
        
        // 按年级统计
        content.append("【年级分布】\n");
        studentsByGrade.forEach((grade, students) -> 
            content.append(grade).append("级: ").append(students.size()).append("人\n"));
        content.append("\n");
        
        // 建议和注意事项
        content.append("【建议和注意事项】\n");
        if (failedCount > 0) {
            content.append("- 有 ").append(failedCount).append(" 个学生的提醒发送失败，建议检查邮箱地址是否正确\n");
        }
        if (totalStudents > 100) {
            content.append("- 未缴费学生数量较多，建议关注缴费情况并采取相应措施\n");
        }
        content.append("- 建议定期检查缴费记录，确保数据准确性\n");
        content.append("- 如有学生反馈未收到提醒邮件，请检查邮件服务配置\n\n");
        
        content.append("==========================================\n");
        content.append("此报告由智慧校园管理系统自动生成\n");
        content.append("如有疑问，请联系系统管理员\n");
        
        return content.toString();
    }
}
